package com.hero.cachewhendodemo.cachewhen;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <pre>
 *
 * </pre>
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/8/25.
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
