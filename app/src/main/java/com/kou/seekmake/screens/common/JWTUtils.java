package com.kou.seekmake.screens.common;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;

public class JWTUtils {

    public static String decoded(String JWTEncoded) {
        String payload = null;

        String[] split = JWTEncoded.split("\\.");
        Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
        Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
        payload = getJson(split[1]);
        return payload;
    }

    private static String getJson(String strEncoded) {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
