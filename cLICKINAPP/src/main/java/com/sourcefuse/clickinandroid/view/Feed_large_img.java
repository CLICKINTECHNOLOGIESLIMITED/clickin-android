package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

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

        setContentView(R.layout.feeds_image);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            url = bundle.getString("url");
        }
        feeds_image = (ImageView)findViewById(R.id.feeds_large_img);
        back = (ImageView)findViewById(R.id.back);

        Picasso.with(this).load(url).into(feeds_image);

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }
}
