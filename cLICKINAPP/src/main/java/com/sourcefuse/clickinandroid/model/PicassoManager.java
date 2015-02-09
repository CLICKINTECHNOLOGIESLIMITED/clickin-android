package com.sourcefuse.clickinandroid.model;

import android.content.Context;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

/**
 * Created by prafull on 6/1/15.
 */
public class PicassoManager {
    private static Picasso picasso = null;
    private static LruCache lruCache = null;


    public static Picasso getPicasso() {
        return picasso;
    }

    public static void setPicasso(Context context) {
        lruCache = new LruCache(context);
        picasso = new Picasso.Builder(context).memoryCache(lruCache).build();
    }

    public static void clearCache() {
        if (lruCache != null)
            lruCache.clear();
    }

}
