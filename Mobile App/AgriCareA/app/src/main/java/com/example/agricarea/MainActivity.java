package com.example.agricarea;

import static com.example.agricarea.Constants.LABELS_PATH;
import static com.example.agricarea.Constants.MODEL_PATH;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.agricarea.databinding.ActivityMainBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements Detector.DetectorListener {
    private ActivityMainBinding binding;
    private boolean isFrontCamera = false;
    private Button b, gob;

    private Preview preview;
    private ImageAnalysis imageAnalyzer;
    private Camera camera;
    private ProcessCameraProvider cameraProvider;
    private Detector detector;

    private ExecutorService cameraExecutor;
    private String detectedClassname;

    private static final String TAG = "Camera";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        detector = new Detector(getBaseContext(), MODEL_PATH, LABELS_PATH, this);
        detector.setup();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        b = findViewById(R.id.btn);
        gob = findViewById(R.id.btn1);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detectedClassname != null) {
                    Intent i = new Intent(MainActivity.this, ChatBotActivity.class);
                    i.putExtra("disease", detectedClassname);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "No disease detected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, profile.class);
                startActivity(i);
                finish();            }
        });
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get();
                bindCameraUseCases();
            } catch (Exception e) {
                Log.e(TAG, "Camera initialization failed.", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) {
            throw new IllegalStateException("Camera initialization failed.");
        }

        int rotation = binding.viewFinder.getDisplay().getRotation();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(isFrontCamera ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
                .build();

        preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .build();

        imageAnalyzer = new ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(rotation)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build();

        imageAnalyzer.setAnalyzer(cameraExecutor, imageProxy -> {
            try {
                Bitmap bitmapBuffer = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
                bitmapBuffer.copyPixelsFromBuffer(imageProxy.getPlanes()[0].getBuffer());

                Matrix matrix = new Matrix();
                matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());

                if (isFrontCamera) {
                    matrix.postScale(-1f, 1f, imageProxy.getWidth() / 2f, imageProxy.getHeight() / 2f);
                }

                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapBuffer, 0, 0, bitmapBuffer.getWidth(), bitmapBuffer.getHeight(), matrix, true);

                // Debug log to check dimensions and rotations
                Log.d(TAG, "Bitmap dimensions: " + rotatedBitmap.getWidth() + "x" + rotatedBitmap.getHeight());

                detector.detect(rotatedBitmap);
            } finally {
                imageProxy.close();
            }
        });


        cameraProvider.unbindAll();

        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer);
            preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
        } catch (Exception exc) {
            Log.e(TAG, "Use case binding failed", exc);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                if (Boolean.TRUE.equals(permissions.get(Manifest.permission.CAMERA))) {
                    startCamera();
                }
            });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.clear();
        cameraExecutor.shutdown();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS);
        }
    }

    @Override
    public void onEmptyDetect() {
        binding.overlay.invalidate();
    }

    @Override
    public void onDetect(List<BoundingBox> boundingBoxes, long inferenceTime) {
        runOnUiThread(() -> {
            binding.inferenceTime.setText(String.format("%dms", inferenceTime));
            binding.overlay.setResults(boundingBoxes);
            binding.overlay.invalidate();

            if (!boundingBoxes.isEmpty()) {
                // Use a Set to collect unique class names
                Set<String> uniqueClassNames = new HashSet<>();
                for (BoundingBox boundingBox : boundingBoxes) {
                    uniqueClassNames.add(boundingBox.getClsName());
                }

                // Convert the Set to a comma-separated string
                String detectedClassNames = String.join(", ", uniqueClassNames);

                // Use the collected unique class names as needed
                detectedClassname = detectedClassNames;
            }
        });
    }


    @Override
    public void onBackPressed() {
        // Optionally you can add some logic here before calling super.onBackPressed()
        super.onBackPressed();
        // This will close the current activity and go back to the previous one
        Intent intent = new Intent(MainActivity.this, profile.class);
        startActivity(intent);
        finish();
    }
}
