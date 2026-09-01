gltfviewer (Android library module)

This lightweight library provides a GLSurfaceView-based 3D viewer with basic controls:
- Single-finger drag: rotate camera
- Pinch: zoom
- Two-finger drag: pan

Files included:
- GltfSurfaceView.kt - GLSurfaceView that handles gestures
- GltfRenderer.kt - simple OpenGL renderer (renders a cube placeholder)
- SampleViewerActivity.kt - example activity showing usage

Integration (Option 2):
1) Copy this `gltfviewer` folder into your project root.
2) In your root settings.gradle (or settings.gradle.kts), include the module:
   include ':app', ':gltfviewer'
3) In your app build.gradle add:
   implementation project(':gltfviewer')
4) Use the view in an Activity layout or start the SampleViewerActivity from your app.

Extending to load real glTF/GLB models:
- Implement loadModelFromAssets in GltfRenderer using a glTF loader (e.g., jgltf, Khronos' libraries, or a custom parser).
- Upload meshes and textures to OpenGL buffers and draw.

Notes:
- This module intentionally provides a minimal, dependency-free renderer as a starting point.
- If you want, I can add jgltf integration and an example of loading a .glb from assets.
