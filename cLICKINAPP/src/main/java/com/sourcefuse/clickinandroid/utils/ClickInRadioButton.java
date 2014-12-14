package com.sourcefuse.clickinandroid.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.sourcefuse.clickinapp.R;

/**
 * Created by akshit on 27/11/14.
 */
public class ClickInRadioButton extends RadioButton {
    private static final String TAG = "RadioButton";

    public ClickInRadioButton(Context context) {
        super(context);
    }

    public ClickInRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public ClickInRadioButton(Context context, AttributeSet attrs, int defStyle) {
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
            android.util.Log.e("font not found:::::::::::::::::::::::::::::", "font not found:::::::::::::::::::::::::::::");
            e.printStackTrace();

        }

        return true;
    }
}
