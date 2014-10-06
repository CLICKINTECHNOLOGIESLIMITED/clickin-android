package com.sourcefuse.clickinandroid.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by mukesh on 25/8/14.
 */

public class FontLoader {

    private static FontLoader INSTANCE;

    private static Context mContext;

    private static HashMap<String, Typeface> mFonts = new HashMap<String, Typeface>();

    FontLoader(Context context) {
        mContext = context;
    }

    public static FontLoader getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FontLoader(context);
        }
        return INSTANCE;
    }

    public static synchronized Typeface getFont(String name) {
        if (!mFonts.containsKey(name)) {
            mFonts.put(name, Typeface.createFromAsset(mContext.getAssets(), "fonts/" + name));
        }
        return mFonts.get(name);
    }
}
