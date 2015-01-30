package com.sourcefuse.clickinandroid.model;

import android.content.Context;
import android.util.Log;


import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

/**
 * Created by prafull on 6/1/15.
 */
public class PicassoManager {
    private static Picasso picasso = null;
    private static CustomLruCache lruCache = null;

    public static Cache getLruCache() {
        return lruCache;
    }

    public static void setLruCache(Context context) {
        lruCache = new CustomLruCache(context);
    }


    public static Picasso getPicasso() {
        return picasso;
    }

    public static void setPicasso(Context context, Cache cache) {
        picasso = new Picasso.Builder(context).memoryCache(cache).build();
    }

    public static void clearCache(String imageUrl) {
        try {
            PicassoManager.getLruCache().clear();
            PicassoManager.getLruCache().set(imageUrl, null);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static class CustomLruCache extends LruCache{
        public CustomLruCache(Context context){
            super(context);
        }

        public CustomLruCache(int value){
            super(value);
        }
    }

}
