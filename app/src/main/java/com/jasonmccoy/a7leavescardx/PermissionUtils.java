package com.jasonmccoy.a7leavescardx;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by wghma on 12/26/2016.
 */

public class PermissionUtils {


    public static final int REQUEST_SMS = 111, REQUEST_WRITE_STORAGE = 112,
            REQUEST_LOCATION = 113, REQUEST_CALL = 114, REQUEST_CAMERA = 115;

    // permission to read phone state and receive SMS
    public static final String[] SMS_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_SMS
    };

    // permission to make a phone call
    public static final String[] CALL_PERMISSIONS = {
            Manifest.permission.CALL_PHONE
    };

    // permission to write sd card
    public static final String[] SD_WRITE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // permission to get location access
    public static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // permission to get location access
    public static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA
    };



    public static boolean isPermissionGranted(Activity activity, String[] permissions, int requestCode) {
        boolean requirePermission = false;
        if(permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if ((ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)) {
                    requirePermission = true;
                    break;
                }
            }
        }

        if (requirePermission) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPermissionResultGranted(int[] grantResults) {
        boolean allGranted = true;
        if(grantResults != null && grantResults.length > 0) {
            for (int i : grantResults) {
                if(i != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
        }
        return allGranted;
    }

}
