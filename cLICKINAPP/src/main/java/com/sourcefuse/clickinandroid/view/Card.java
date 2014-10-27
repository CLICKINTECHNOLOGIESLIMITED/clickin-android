package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by akshit on 2/10/14.
 */
public class Card extends Activity implements View.OnClickListener,TextWatcher {

//    String stringData ;
//    String stringdiscription ;

    private TextView card_title, card_desription;
    private static final String TAG = "CardViewAdapter";
    ImageView mBackButton;
    String url;
    Context context;
    ImageView imageView;
    TextView trd_clicks_top, trd_clicks_bottom ,trone,trtwo,trthree,trfour,trfive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.card_click);

        trd_clicks_bottom = (TextView)findViewById(R.id.trd_clicks_bottom);
        trd_clicks_top = (TextView)findViewById(R.id.trd_clicks_top);

        trone = (TextView)findViewById(R.id.btn_one);
        trone.setOnClickListener(this);
        trtwo = (TextView)findViewById(R.id.btn_two);
        trtwo.setOnClickListener(this);
        trthree = (TextView)findViewById(R.id.btn_three);
        trthree.setOnClickListener(this);
        trfour = (TextView)findViewById(R.id.btn_four);
        trfour.setOnClickListener(this);
        trfive = (TextView)findViewById(R.id.btn_five);
        trfive.setOnClickListener(this);


        imageView = (ImageView) findViewById(R.id.trade_image);
        mBackButton = (ImageView) findViewById(R.id.m_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_finish_up, R.anim.slide_out_finish_up);
            }
        });


        Intent intent = getIntent();
        if (null != intent) {


            url = intent.getStringExtra("Url");
            Log.e(TAG, "Url for the fetched Card is" + url);

        } else {
            Log.e(TAG, "Value in intent in null");
        }

        Picasso.with(this)
                .load(url)
                .into(imageView);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_one:
                trone.setSelected(true);
                trtwo.setSelected(false);
                trthree.setSelected(false);
                trfour.setSelected(false);
                trfive.setSelected(false);
                trd_clicks_bottom.setText("05");
                trd_clicks_top.setText("05")
                ;
                break;
            case R.id.btn_two:
                trone.setSelected(false);
                trtwo.setSelected(true);
                trthree.setSelected(false);
                trfour.setSelected(false);
                trfive.setSelected(false);
                trd_clicks_bottom.setText("10");
                trd_clicks_top.setText("10");
                break;
            case R.id.btn_three:
                trone.setSelected(false);
                trtwo.setSelected(false);
                trthree.setSelected(true);
                trfour.setSelected(false);
                trfive.setSelected(false);
                trd_clicks_bottom.setText("15");
                trd_clicks_top.setText("15");
                break;
            case R.id.btn_four:
                trone.setSelected(false);
                trtwo.setSelected(false);
                trthree.setSelected(false);
                trfour.setSelected(true);
                trfive.setSelected(false);
                trd_clicks_bottom.setText("20");
                trd_clicks_top.setText("20");
                break;
            case R.id.btn_five:
                trone.setSelected(false);
                trtwo.setSelected(false);
                trthree.setSelected(false);
                trfour.setSelected(false);
                trfive.setSelected(true);
                trd_clicks_bottom.setText("25");
                trd_clicks_top.setText("25");
                break;
            case R.id.btn_play:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}


