package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by charunigam on 13/10/14.
 */
public class Feed_large_img extends Activity {
    ImageView feeds_image;
    ImageView back;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//code- to handle uncaught exception
        if(Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.feeds_image);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
        }
        feeds_image = (ImageView) findViewById(R.id.feeds_large_img);
        back = (ImageView) findViewById(R.id.back);
        Picasso.with(this).load(url).into(feeds_image);

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.top_out);//akshit code for animation
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.top_out);//akshit code for animation
    }
}
