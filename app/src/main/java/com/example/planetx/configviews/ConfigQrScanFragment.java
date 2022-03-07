package com.example.planetx.configviews;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.planetx.utilviews.GenericAlertDialogFragment;
import com.example.planetx.R;
import com.example.planetx.viewmodels.ConfigViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigQrScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This class scans a qr code which is required for authentication. It also contains information to complete the configuration.
 */
public class ConfigQrScanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Declare attributes
    private static final String TAG = ConfigQrScanFragment.class.getSimpleName();
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private NavController navController;
    private Button qrScanBackButton;
    private Context context;
    private Activity activity;
    private ImageView flashImage;
    private PreviewView previewView;
    private boolean found;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ConfigViewModel configViewModel;

    //Layout inflated in the constructor
    public ConfigQrScanFragment() {
        super(R.layout.fragment_config_qr_scan);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigQrScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigQrScanFragment newInstance(String param1, String param2) {
        ConfigQrScanFragment fragment = new ConfigQrScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Initialize fragment resources
        navController = NavHostFragment.findNavController(this);
        context = this.getContext();
        activity = this.getActivity();

        //Instantiate view model
        configViewModel = new ViewModelProvider(requireActivity()).get(ConfigViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize views
        qrScanBackButton = view.findViewById(R.id.qr_scan_back_button);

        //Set listeners for clickable views
        setOnClickListeners();

        //Register Permission Callback
        registerCameraPermissionCallback(view);

        //Request Camera Permission
        requestCameraPermission(view);
    }

    private void setOnClickListeners() {
        //QR  Scan Back Button
        qrScanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick(): Back button called");
                navController.navigate(R.id.action_configQrScanFragment_to_configWelcomeFragment);
            }
        });
        //Set other view listeners below
    }

    //registers the permission callback to access the camera.
    private void registerCameraPermissionCallback(View view) {
        Log.i(TAG, "registerPermissionCallback() called");
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        Log.i(TAG, "registerPermissionCallback: Camera permission was granted. called");
                        setUpCameraX(view);
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                        Log.i(TAG, "registerPermissionCallback: Camera permission was denied. called");
                        String message = "You cannot proceed with the app without the camera approval";
                        DialogFragment dialog = new GenericAlertDialogFragment(message);
                        dialog.show(getParentFragmentManager(), "CameraPermissionDenied");
                    }
                });
    }

    //Requests permission to use the camera
    private void requestCameraPermission(View view) {
        Log.i(TAG, "requestCameraPermission(): called");
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            Log.i(TAG, "requestCamera: " + "Camera permission already granted. called");
            setUpCameraX(view);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            Log.i(TAG, "requestCamera: " + "Camera permission educational info requested. called");
            String message = "The app's functionalities are tied to the camera. \nSo please grant the camera permission to proceed with the app." +
                    "\nYou may go to your settings to grant the permission";
            DialogFragment dialog = new GenericAlertDialogFragment(message);
            dialog.show(getParentFragmentManager(), "CameraPermissionEducationalInformation");
            /*showInContextUI(...);*/

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            Log.i(TAG, "requestCamera: " + "Camera permission NOT yet granted called");
            requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
        }
    }

    //This method is called after (if) the user accepts (previously accepted) the camera permission request.
    private void setUpCameraX(View view) {
        Log.i(TAG, "setUpCameraX(): called");
        //Initialize views
        flashImage = view.findViewById(R.id.flash_image);
        previewView = view.findViewById(R.id.preview_view);
        found = false;

        //Request a CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        //Check for CameraProvider availability
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(context));
    }


    private void startCameraX(ProcessCameraProvider cameraProvider) {
        Log.i(TAG, "startCameraX(): called");
        cameraProvider.unbindAll();

        //Preview use case
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //Image analysis use case
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        // enable the following line if RGBA output is needed.
                        //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                // insert your code here.
                //Prepare input image for the ml kit scanner
                @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
                if (mediaImage != null) {
                    InputImage image =
                            InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                    // Pass image to an ML Kit Vision API
                    BarcodeScanner scanner = configureScanner();
                    processImage(scanner, image, imageProxy);

                }
            }
        });

        flashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Camera cam = cameraProvider.bindToLifecycle((LifecycleOwner)ConfigQrScanFragment.this,
                        cameraSelector, imageAnalysis, preview);

                if ( cam.getCameraInfo().hasFlashUnit() ) {

                    if(cam.getCameraInfo().getTorchState().getValue() == TorchState.OFF) {
                        cam.getCameraControl().enableTorch(true);
                        flashImage.setImageResource(R.drawable.ic_baseline_flash_off_64);
                    }
                    else {
                        cam.getCameraControl().enableTorch(false);
                        flashImage.setImageResource(R.drawable.ic_baseline_flash_on_64);
                    }
                }
            }
        });

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }

    // TODO: The configureScanner() can be replaced with a static method in a CameraUtils class
    public BarcodeScanner configureScanner() {
        //Set scanner option to only QR code
        Log.i(TAG, "configureScanner(): called()");
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE)
                        .build();

        return BarcodeScanning.getClient(options);
    }

    // TODO: The processImage(...) can be replaced with a static method in a CameraUtils class
    public void processImage(BarcodeScanner scanner, InputImage image, ImageProxy imageProxy) {
        Log.i(TAG, "processImage(): called");
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        // Task completed successfully
                        // ...
                        Log.i(TAG, "onSuccess(): called in scanner.process(image) called");
                        for (Barcode barcode: barcodes) { //Consider stop after the first successful scan
                            if(found) {
                                break;
                            }

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            if (valueType == Barcode.TYPE_WIFI) {
                                found = true;
                                try {
                                    configViewModel.setConfigData(rawValue);
                                    navController.navigate(R.id.action_configQrScanFragment_to_configDataDisplayFragment);
                                    Log.i(TAG, "onSuccess: Successful scan of valid qr code called");
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "Deserialization error: called " + e.getMessage(), e);
                                    navController.navigate(R.id.action_configQrScanFragment_to_configQrScanDialogFragment);
                                }
                            }
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        e.printStackTrace();
                        Log.e(TAG, "onFailure(): called in scanner.process(image)");
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close()); // after done, release the ImageProxy object
    }

}