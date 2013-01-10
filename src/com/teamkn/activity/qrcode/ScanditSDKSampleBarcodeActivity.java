package com.teamkn.activity.qrcode;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mirasense.scanditsdk.LegacyPortraitScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.ScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDK;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

/**
 * Simple Activity illustrating how to embed the Scandit SDK.
 * 
 * Important information for the developer with respect to Android 2.1 support!
 * 
 * Android 2.1 differs from subsequent versions of Android OS in that it 
 * does not offer a camera preview mode in portrait mode (landscape only). 
 * Android 2.2+ offers both - a camera preview in landscape mode and in portrait mode. 
 * 
 * To address this difference between the Android versions, the Scandit SDK 
 * offers the following approaches and the developer needs to choose his preferred option:
 * 
 * under Android 2.1:
 * 
 * - a scan view in landscape mode scanning only(!) that is fully 
 * customizable by the developer - ScanditSDKBarcodePicker.class
 *  
 *  or
 *  
 * - our own custom scan view with portrait mode scanning that offers only 
 * limited customization options (show/hide title & tool bars, 
 * but no additional Android UI elements) -  LegacyPortraitScanditSDKBarcodePicker.class
 * 
 * under Android 2.2: 
 * 
 * - a scan view with portrait mode scanning that is fully customizable 
 * by the developer (RECOMMENDED) - ScanditSDKBarcodePicker.class
 * 
 * or 
 * 
 * - any of the options listed under Android 2.1
 * 
 * We recommend that developers choose the scan view in portrait mode on Android 2.2.
 * It has the native Android look&feel and provides full customization. We provide our
 * own custom scan view (LegacyPortraitScanditSDKBarcodePicker.class) in Android 2.2
 * to provide backwards compatibility with Android 2.1. 
 *
 * To integrate the Scandit SDK, carry out the following three steps:
 * 
 * 1. Create a BarcodePicker object that manages camera access and 
 *    bar code scanning:
 *    
 *    e.g.
 *    ScanditSDKBarcodePicker barcodePicker = new ScanditSDKBarcodePicker(this, 
 *              R.raw.class, "your app key", true, 
                ScanditSDKBarcodePicker.LOCATION_PROVIDED_BY_SCANDIT_SDK);
 *
 * 2. Add it to the activity:    
 *    my_activity.setContentView(barcodePicker);
 * 
 * 3. Implement the ScanditSDKListener interface (didCancel, didScanBarcode, 
 *    didManualSearch) and register with the ScanditSDKOverlayView to receive 
 *    callbacks:
 *    barcodePicker.getOverlayView().addListener(this);
 * 
 * 
 * If you want to use the custom scan view for scanning in portrait mode in 
 * Android 2.1, instantiate the LegacyPortraitScanditSDKBarcodePicker
 * class (as shown in the example below). There is utility method available 
 * to determine whenever the default portrait scan view is not available
 * ScanditSDKBarcodePicker.canRunPortraitPicker().
 * 
 * 
 * 
 * Copyright 2010 Mirasense AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing premissions and
 * limitations under the License.
 */
public class ScanditSDKSampleBarcodeActivity extends Activity implements ScanditSDKListener {

    // The main object for recognizing a displaying barcodes.
    private ScanditSDK mBarcodePicker;
    
    // Enter your Scandit SDK App key here.
    // Your Scandit SDK App key is available via your Scandit SDK web account.
    private static final String sScanditSdkAppKey = "a2vGnlrQEeKXzArSVddW5nNngJG2NwBiB+iE1qqQHx4";

    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
        
    }
    
    @Override
    protected void onPause() {
        // When the activity is in the background immediately stop the 
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        // Once the activity is in the foreground again, restart scanning.
        mBarcodePicker.startScanning();
        super.onResume();
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // We decide between the legacy and new GUI by invoking the static function
        // canRunPortraitPicker().
        if (ScanditSDKBarcodePicker.canRunPortraitPicker()) {
            // create ScanditSDKBarcodePicker that takes care of the camera 
            // access and barcode recognition. By default a back camera is used.
            // However, if you prefer to use a front camera you can specify
            // your preference and if one is available, it will be used instead.
            ScanditSDKBarcodePicker picker = new ScanditSDKBarcodePicker(
                    this, sScanditSdkAppKey, ScanditSDKBarcodePicker.CAMERA_FACING_BACK);
            
            // Example of adding views directly onto the overlay. This content
            // will be on a lower level than the rectangle indicating where
            // the barcode is.
            // If you want the added views to be on top, simply put them on top
            // of the barcode picker like you would any other view. For example
            // if you added the picker with setContentView(picker), add the 
            // overlaying view with addContentView(view).
            
            /*
            RelativeLayout overlay = picker.getOverlayView();
            RelativeLayout.LayoutParams rParams;
            rParams = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rParams.topMargin = 80;

            LinearLayout lLayout = new LinearLayout(this);
            lLayout.setOrientation(LinearLayout.VERTICAL);
            
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.icon);
            lLayout.addView(imageView);
            
            TextView textView = new TextView(this);
            textView.setText("Overlay image");
            lLayout.addView(textView);
            
            overlay.addView(lLayout, rParams);
            // end of example code that shows how to add image
            */
            
            // Add both views to activity, with the scan GUI on top.
            setContentView(picker);
            mBarcodePicker = picker; 
            
            
        } else {
            // Force the UI into landscape mode. This is essential for use under Android 2.1
        	// where a camera preview in portrait mode is not available.
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            
            // choose the Scandit custom scan view that allows portrait barcode scanning also 
            // in Android 2.1 via "Scandit" magic. 
            LegacyPortraitScanditSDKBarcodePicker picker = new LegacyPortraitScanditSDKBarcodePicker(
                    this, sScanditSdkAppKey);
            
            // or alternatively use the regular Scandit scan view with Android look&feel in landscape mode only.
            //ScanditSDKBarcodePicker picker = new ScanditSDKBarcodePicker(
            //        this, R.raw.class, sScanditSdkAppKey);
            
            // Add both views to activity, with the scan GUI on top.
            setContentView(picker);
            mBarcodePicker = picker;
        }
        
        // Register listener, in order to be notified about relevant events 
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.getOverlayView().addListener(this);
        
        // show search bar in scan user interface
        mBarcodePicker.getOverlayView().showSearchBar(false);
        
        // In the old version, the title and tool bar can be hidden as follows:
        //mBarcodePicker.getOverlayView().showTitleBar(false);
        //mBarcodePicker.getOverlayView().showToolBar(false);
        
        // To activate recognition of 2d codes
        mBarcodePicker.setQrEnabled(true);
        mBarcodePicker.setDataMatrixEnabled(true);
    }

    /** 
     *  Called when a barcode has been decoded successfully.
     *  
     *  @param barcode Scanned barcode content.
     *  @param symbology Scanned barcode symbology.
     */
    public void didScanBarcode(String barcode, String symbology) {
        Toast.makeText(this, symbology + ": " + barcode, 10000).show();
        // Example code that would typically be used in a real-world app using 
        // the Scandit SDK.
        /*
        // Access the image in which the bar code has been recognized.
        byte[] imageDataNV21Encoded = barcodePicker.getCameraPreviewImageOfFirstBarcodeRecognition();
        int imageWidth = barcodePicker.getCameraPreviewImageWidth();
        int imageHeight = barcodePicker.getCameraPreviewImageHeight();
        
        // Stop recognition to save resources.
        mBarcodePicker.stopScanning();
        */
    }
    
    /** 
     * Called when the user entered a bar code manually.
     * 
     * @param entry The information entered by the user.
     */
    public void didManualSearch(String entry) {
        // Example code that would typically be used in a real-world app using 
        // the Scandit SDK.
        /*
        // Access the current camera image.
        byte[] imageDataNV21Encoded = barcodePicker.getMostRecentCameraPreviewImage();
        int imageWidth = barcodePicker.getCameraPreviewImageWidth();
        int imageHeight = barcodePicker.getCameraPreviewImageHeight(); 
         
        // Stop recognition to save resources.
        mBarcodePicker.stopScanning();
        */
    }
    
    @Override
    public void didCancel() {
        mBarcodePicker.stopScanning();
        finish();
    }
    
    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
    }
}
