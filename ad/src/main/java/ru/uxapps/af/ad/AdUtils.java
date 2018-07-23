package ru.uxapps.af.ad;


import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.ads.AdRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AdUtils {

    private AdUtils() {}

    public static AdRequest buildRequest(Context context, boolean testMode) {
        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        if (testMode) {
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            //add current device
            @SuppressLint("HardwareIds")
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            requestBuilder.addTestDevice(md5(androidId).toUpperCase());
        }
        AdRequest adRequest = requestBuilder.build();
        if (testMode && !adRequest.isTestDevice(context)) {
            //just throw (debug mode)
            throw new RuntimeException("Request test ad, but created ad is not test");
        }
        return adRequest;
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
