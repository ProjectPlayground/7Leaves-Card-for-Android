package com.jasonmccoy.a7leavescardx;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.os.Bundle;
import android.view.ViewGroup;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private String oneItem = "qeAuyF9nzFLbHmqm4qQsx8bkBPx4qzFyxQp9XSH5xRBtfQdK4Ax6rC7tYchkVqYpYxJ4Ranpcsn4K9crL4xgj7rTegkt8qdsKVHHzGS9S3VUvzXpaTwJUx8bNR6DtTWesDgyMCZhjZ3myVCpmwy7dzwpKwZJRHpD73u5K4GxxWAFbHXvB6EfaKdewDARwX8eVBTtfNwW64KYjZXfjTnZYx5SZMZ9nvTZDJu2B73b4DDQbZHc4yBBLFHDsJEdP9veaQcQJBzuf7cUZd3H76m3dkdSvvfp8KVbsWcXwes9apepXr5dX9hDjDaTkLVh7R3CktUvC3xx7C7nLXSEMSVw9BuUWwL3EUkfJhyX7RDQDLvs4b9pFFXqmQLkuUpGyLgWvVuN6NdC5sV7U6ZCqZsu9FpznyBrDvSQHXJn7pct3VLfMbTLGUhbXVrag3cJYnBn4yRbTPMj3s4n3WMUNtG2ARzBnR4jVQtVw6NRTbMcaLNnNDkZSPmjp3qVPjr7YB85";
    private String twoItem = "P97kwFp7xmrbVHPv4UbffeZkcaH7w8PdzrYNS6wvdwacFrQ5732kuA3AKb3jckvn4XdhBqJ7V7SGSCv7VYSPfZhXFzg32aCTue464Haf3gkyB5wzevHZWvSzkBqqVbQPXTnDyRhE9YDUkuvuXA4T8UFpy9bdKKVmNMqRMjCcpT2VghdLJtcGsP4N6YxQrLeSnzRTdQqucrTvHaGA2r6XAZtLq4BSp69zpVZNEaLxSjr2mbSCeAs7cCgkVfKXjFHF62cQWWJwybQMQzhBXSR67u7y7L6Y3Enzbzq4VB7VrBqdzddYUQDqJf39TC8sukhUWkmFRTbVEZBhSc4B8HHKMCdv7aYC86WnQX9KgBYbNNJZZDGCncrMmrb6GKnc5tUdm2xyLjaq92WXa6zvCaHjgGCKdwtyP2QE32sQrutYX7MYvbQaSS8p4yGFa7A2EUhZ2txSA4LunFPRheQQ8csgJyR5bdxVkNEYC7Mz8uZcjJqcjKtZuu32TYkHJMypPmhm";

    private Activity mActivity;
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private boolean mFlash;
    private boolean mAutoFocus = true;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private ZXingScannerView mScannerView;
    private ViewGroup contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initConfig(savedInstanceState);
    }

    private void initVar() {
        mActivity = ScanActivity.this;
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scan);

        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
    }

    private void initConfig(Bundle state) {
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(mAutoFocus);
        setupFormats();
        contentFrame.addView(mScannerView);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CAMERA: {
                if (PermissionUtils.isPermissionResultGranted(grantResults)) {
                    startScanner();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (PermissionUtils.isPermissionGranted(mActivity, PermissionUtils.CAMERA_PERMISSIONS, PermissionUtils.REQUEST_CAMERA)) {
            startScanner();
        }

    }

    private void startScanner() {
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public void handleResult(Result rawResult) {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        Log.d("WARD", rawResult.getText());
        if(rawResult.getText().equals(oneItem)) {
            Intent intent = new Intent();
            intent.putExtra("numberOfItems",1);
            setResult(RESULT_OK,intent);
            finish();
        }else if(rawResult.getText().equals(twoItem)) {
            Intent intent = new Intent();
            intent.putExtra("numberOfItems",2);
            setResult(RESULT_OK,intent);
            finish();
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Oops")
                    .setMessage("Invalid QR Code!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mScannerView.resumeCameraPreview(ScanActivity.this);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
