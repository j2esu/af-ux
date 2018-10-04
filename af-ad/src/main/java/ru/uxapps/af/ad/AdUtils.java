package ru.uxapps.af.ad;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.google.android.gms.ads.AdRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AdUtils {

    private AdUtils() {}

    public static boolean hasConnection(Context context) {
        ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connect != null ? connect.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static AdRequest buildRequest(Context context, boolean testMode, AdRequest.Builder builder) {
        if (testMode) {
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            builder.addTestDevice(getDeviceId(context));
        }
        AdRequest request = builder.build();
        if (testMode && !request.isTestDevice(context)) {
            //just throw (debug mode)
            throw new RuntimeException("Request test ad, but created ad is not test");
        }
        return request;
    }

    public static AdRequest buildRequest(Context context, boolean testMode) {
        return buildRequest(context, testMode, new AdRequest.Builder());
    }

    public static String getDeviceId(Context context) {
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return md5(androidId).toUpperCase();
    }

    private static String md5(String s) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Can't calculate md5", e);
        }
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();
        StringBuilder hex = new StringBuilder();
        for (byte md : messageDigest) {
            StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & md));
            while (h.length() < 2) h.insert(0, "0");
            hex.append(h);
        }
        return hex.toString();
    }

}
