package com.sourcefuse.clickinandroid.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sourcefuse.clickinapp.R;

/**
 * Created by mukesh on 25/8/14.
 */

public class ClickInTextView extends TextView {
    private static final String TAG = "TextView";

    public ClickInTextView(Context context) {
        super(context);
    }

    public ClickInTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public ClickInTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ClickInTextView);
        String customFont = a.getString(R.styleable.ClickInTextView_customFont);
        setCustomFont(ctx, customFont);
    }

    public boolean setCustomFont(Context ctx, String asset) {
        try {
            setTypeface(FontLoader.getInstance(ctx).getFont(asset));
        } catch (Exception e) {

            e.printStackTrace();

        }

        return true;
    }

}