package com.example.gltfviewer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GltfRenderer(private val context: Context) : GLSurfaceView.Renderer {

    // Simple cube data
    private val cubeCoords = floatArrayOf(
        // positions (x, y, z)
        -1f, 1f, 1f,
        -1f, -1f, 1f,
        1f, -1f, 1f,
        1f, 1f, 1f,
        -1f, 1f, -1f,
        -1f, -1f, -1f,
        1f, -1f, -1f,
        1f, 1f, -1f
    )

    private val indexOrder = shortArrayOf(
        0,1,2, 0,2,3, // front
        4,5,6, 4,6,7, // back
        4,0,3, 4,3,7, // top
        1,5,6, 1,6,2, // bottom
        4,5,1, 4,1,0, // left
        3,2,6, 3,6,7  // right
    )

    private val colors = floatArrayOf(
        0.8f, 0.1f, 0.1f, 1f
    )

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: java.nio.ShortBuffer

    private var program = 0

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    // camera control state
    private var angleX = 0f
    private var angleY = 0f
    private var distance = 6f
    private var panX = 0f
    private var panY = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.95f, 0.95f, 0.95f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        vertexBuffer = ByteBuffer.allocateDirect(cubeCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(cubeCoords).position(0)

        indexBuffer = ByteBuffer.allocateDirect(indexOrder.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indexOrder).position(0)

        program = buildProgram(VERTEX_SHADER, FRAGMENT_SHADER)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // camera (view) matrix
        val eyeX = (Math.sin(Math.toRadians(angleY.toDouble())) * Math.cos(Math.toRadians(angleX.toDouble())) * distance).toFloat()
        val eyeY = (Math.sin(Math.toRadians(angleX.toDouble())) * distance).toFloat()
        val eyeZ = (Math.cos(Math.toRadians(angleY.toDouble())) * Math.cos(Math.toRadians(angleX.toDouble())) * distance).toFloat()

        Matrix.setLookAtM(viewMatrix, 0,
            eyeX + panX, eyeY + panY, eyeZ,
            panX, panY, 0f,
            0f, 1f, 0f)

        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        GLES20.glUseProgram(program)

        val posHandle = GLES20.glGetAttribLocation(program, "aPosition")
        val colorHandle = GLES20.glGetUniformLocation(program, "uColor")
        val mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glUniform4fv(colorHandle, 1, colors, 0)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexOrder.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(posHandle)
    }

    fun onRotate(dx: Float, dy: Float) {
        val scale = 0.5f
        angleY += dx * scale
        angleX += dy * scale
        angleX = angleX.coerceIn(-89f, 89f)
    }

    fun onScale(scaleFactor: Float) {
        distance /= scaleFactor
        distance = distance.coerceIn(2f, 50f)
    }

    fun onPan(dx: Float, dy: Float) {
        // convert screen delta to world pan (rough approximation)
        val factor = 0.01f * distance
        panX += -dx * factor
        panY += dy * factor
    }

    fun loadModelFromAssets(assetPath: String) {
        // Placeholder: here a glTF loader (jgltf or other) should parse and upload meshes/textures.
        Log.i("GltfRenderer", "loadModelFromAssets called: $assetPath - not implemented yet")
    }

    private fun buildProgram(vertexSrc: String, fragmentSrc: String): Int {
        val v = loadShader(GLES20.GL_VERTEX_SHADER, vertexSrc)
        val f = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSrc)
        val p = GLES20.glCreateProgram()
        GLES20.glAttachShader(p, v)
        GLES20.glAttachShader(p, f)
        GLES20.glLinkProgram(p)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(p, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val msg = GLES20.glGetProgramInfoLog(p)
            GLES20.glDeleteProgram(p)
            throw RuntimeException("Program link failed: $msg")
        }
        return p
    }

    private fun loadShader(type: Int, src: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, src)
        GLES20.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            val msg = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Shader compile failed: $msg")
        }
        return shader
    }

    companion object {
        private const val VERTEX_SHADER = """
            attribute vec4 aPosition;
            uniform mat4 uMVPMatrix;
            void main() {
                gl_Position = uMVPMatrix * aPosition;
            }
        """

        private const val FRAGMENT_SHADER = """
            precision mediump float;
            uniform vec4 uColor;
            void main() {
                gl_FragColor = uColor;
            }
        """
    }
}
