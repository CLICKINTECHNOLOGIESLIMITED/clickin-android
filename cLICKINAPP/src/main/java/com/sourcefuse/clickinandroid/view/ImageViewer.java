package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by akshit on 13/10/14.
 */
public class ImageViewer extends Activity {

    ImageView imageView ,mback ;

    String image ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat_image_viewer);
        mback = (ImageView)findViewById(R.id.iv_back_noti);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        imageView = (ImageView)findViewById(R.id.chat_image);

        Intent intent = getIntent();
        image = intent.getStringExtra("Url");
        Picasso.with(this).load(image).into(imageView);
    }
}
