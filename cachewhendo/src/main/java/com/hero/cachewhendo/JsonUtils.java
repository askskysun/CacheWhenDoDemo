package com.hero.cachewhendodemo.cachewhen;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <pre>
 *
 * </pre>
 */
public class JsonUtils {
    public static String javabeanToJson(Object obj) {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            String json = gson.toJson(obj);
            return json != null ? json : "";
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JsonUtils", " javabeanToJson()", e);
            return "";
        }
    }
}
