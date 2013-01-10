package com.teamkn.activity.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.mirasense.scanditsdk.LegacyPortraitScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.ScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDK;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.QRCodeResult;

public class ScanditSDKSampleBarcodeActivity extends Activity implements ScanditSDKListener {
    private ScanditSDK mBarcodePicker;
    private static final String sScanditSdkAppKey = "a2vGnlrQEeKXzArSVddW5nNngJG2NwBiB+iE1qqQHx4";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeAndStartBarcodeScanning();
    }
    
    @Override
    protected void onPause() {
        mBarcodePicker.stopScanning();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mBarcodePicker.startScanning();
        super.onResume();
    }
    public void initializeAndStartBarcodeScanning() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (ScanditSDKBarcodePicker.canRunPortraitPicker()) {
            ScanditSDKBarcodePicker picker = new ScanditSDKBarcodePicker( this, sScanditSdkAppKey, ScanditSDKBarcodePicker.CAMERA_FACING_BACK);
            setContentView(picker);
            mBarcodePicker = picker; 
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            LegacyPortraitScanditSDKBarcodePicker picker = new LegacyPortraitScanditSDKBarcodePicker(this, sScanditSdkAppKey);
            setContentView(picker);
            mBarcodePicker = picker;
        }
        mBarcodePicker.getOverlayView().addListener(this);
        
        mBarcodePicker.getOverlayView().showSearchBar(false);
        mBarcodePicker.setQrEnabled(true);
        mBarcodePicker.setDataMatrixEnabled(true);
    }
    public void didScanBarcode(String barcode, String symbology) {
        Bundle get_bundle = getIntent().getExtras();
        QRCodeResult qrcode_result = (QRCodeResult) get_bundle.get("qrcode_result"); 
        DataList data_list = (DataList) get_bundle.get("data_list");
        DataItem data_item = (DataItem)get_bundle.get("data_item");
        String data_list_public = get_bundle.getString("data_list_public");
        
        Bundle bundle = new Bundle();
        QRCodeResult code_result = new QRCodeResult(symbology,barcode);
        bundle.putSerializable("code_result", code_result);
        bundle.putSerializable("data_list", data_list);
        bundle.putSerializable("data_item", data_item);
        bundle.putString("data_list_public", data_list_public);
        Intent intent = new Intent(ScanditSDKSampleBarcodeActivity.this,qrcode_result.result_activity);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
        
    }
    public void didManualSearch(String entry) {
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
