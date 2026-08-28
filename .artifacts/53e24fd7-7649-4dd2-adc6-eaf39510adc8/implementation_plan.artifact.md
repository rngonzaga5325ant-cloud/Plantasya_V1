# TFLite Automatic Plant Scanning Implementation Plan

Integrate the `model_unquant.tflite` model to provide real-time, automatic plant identification. The app will continuously analyze the camera feed and display the detected plant name without requiring manual capture.

## User Review Required

> [!IMPORTANT]
> **Automatic Identification vs. Automatic Capture**: This plan implements continuous real-time identification. As soon as you point the camera at a plant, the name and confidence will appear on screen. I will also implement an "Auto-Lock" feature: if a plant is detected with >85% confidence for 1.5 seconds, it will automatically transition to the preview screen.

> [!WARNING]
> No `labels.txt` was found in the project. I will use a hardcoded list based on the `AppDatabase` plant names (`Snake Plant`, `Spider Plant`, `Aglaonema`, etc.). If the model's output indices don't match this order, the identification will be incorrect. If you have the original `labels.txt`, please provide it.

## Proposed Changes

### UI Enhancement

#### [MODIFY] [activity_scan.xml](file:///C:/Users/PC/AndroidStudioProjects/Plantasya_V1/app/src/main/res/layout/activity_scan.xml)
- Add a dedicated `ResultOverlay` (a `CardView` containing a `TextView` and `ProgressBar`) at the top of the screen to show the current detection.
- Add a visual "Scanning" animation to indicate active analysis.

### Scanning Logic

#### [MODIFY] [ScanActivity.kt](file:///C:/Users/PC/AndroidStudioProjects/Plantasya_V1/app/src/main/java/com/example/plantasya_mobileapp/ScanActivity.kt)
- **Continuous Analysis**: Add `ImageAnalysis` to the CameraX pipeline.
- **TFLite Integration**: Use the generated `ModelUnquant` class to process frames at ~5-10 FPS.
- **Auto-Scanning Engine**:
    - Implement a `Recognition` data class.
    - Use a simple smoothing algorithm (e.g., moving average or consistency check) to avoid flickering results.
    - Implement a timer: if the same plant is detected with high confidence for a threshold duration, automatically call `showPreview()`.
- **Optimization**: Run inference on the `cameraExecutor` to keep the UI thread responsive.

#### [MODIFY] [ScanPreviewFragment.kt](file:///C:/Users/PC/AndroidStudioProjects/Plantasya_V1/app/src/main/java/com/example/plantasya_mobileapp/ScanPreviewFragment.kt)
- Pre-fill the plant name field based on the automatic detection result.

### Build Configuration

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/PC/AndroidStudioProjects/Plantasya_V1/app/build.gradle.kts)
- Add `implementation("org.tensorflow:tensorflow-lite-gpu:2.16.1")` for better performance if available.

## Verification Plan

### Automated Tests
- Verify `gradle build` completes (ensuring ML binding is functional).
- Check `ImageProxy` to `Bitmap` conversion for memory leaks.

### Manual Verification
- Point the camera at a known plant (e.g., a Snake Plant image on a screen).
- Verify the UI updates automatically with the plant name.
- Verify that holding the camera steady on the plant triggers the automatic transition to the preview screen.

