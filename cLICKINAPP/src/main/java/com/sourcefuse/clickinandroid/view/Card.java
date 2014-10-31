package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by akshit on 2/10/14.
 */
public class Card extends Activity implements View.OnClickListener,TextWatcher {

//    String stringData ;
//    String stringdiscription ;

    private TextView card_title, card_desription,tv_about_message;
    private static final String TAG = "CardViewAdapter";
    ImageView mBackButton;
    String url,clicks,cardTitle,cardDiscription,card_Db_id,card_id;


    Context context;
    ImageView imageView,btnPlay;
    boolean forCounter = false;
    TextView trd_clicks_top, trd_clicks_bottom ,trone,trtwo,trthree,trfour,trfive;
    // String xyz_url = "https://s3.amazonaws.com/clickin-dev/cards/a/1080/39.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.card_click);
        Utils.launchBarDialog(Card.this);
        trd_clicks_bottom = (TextView)findViewById(R.id.trd_clicks_bottom);
        trd_clicks_top = (TextView)findViewById(R.id.trd_clicks_top);
        tv_about_message = (TextView)findViewById(R.id.tv_about_message);

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
        btnPlay = (ImageView)findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);

        LinearLayout back = (LinearLayout)findViewById(R.id.linear_layout_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_finish_up, R.anim.slide_out_finish_up);
            }
        });

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
            forCounter = intent.getExtras().getBoolean("ForCounter");
            if(forCounter){
                trd_clicks_bottom.setText(intent.getStringExtra("cardClicks"));
                trd_clicks_top.setText(intent.getStringExtra("cardClicks"));
                tv_about_message.setText("HOW MANY CLICKS DO YOU WANT FOR IT?");
            }
            url = intent.getStringExtra("Url");
            cardTitle = intent.getStringExtra("Title");
            cardDiscription = intent.getStringExtra("Discription");
            card_Db_id = intent.getStringExtra("card_Db_id");
            try {
                card_id = intent.getStringExtra("card_id");
            }catch (Exception e){

            }
            Log.e(TAG, "Url for the fetched Card is" + url+","+cardTitle+","+cardDiscription+","+card_Db_id);

        } else {
            Log.e(TAG, "Value in intent in null");
        }
//<<<<<<< HEAD
//
////        Picasso.with(this)
////                .load(url)
////
////                .into(imageView , new Callback() {
////                    @Override
////                    public void onSuccess() {
////
////                        trd_clicks_top.setVisibility(View.VISIBLE);
////                        trd_clicks_bottom.setVisibility(View.VISIBLE);
////                        Utils.dismissBarDialog();
////
////                    }
////
////                    @Override
////                    public void onError() {
////
////                    }
////                });
////
////
////
////    }
//
//
//        try {
//            String url_to_load = url.replaceFirst("cards\\/(\\d+)\\.jpg","cards\\/a\\/1080\\/$1\\.jpg");
//            Picasso.with(this)
//                    .load(url_to_load)
//                    .into(imageView, new Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                            trd_clicks_top.setVisibility(View.VISIBLE);
//                            trd_clicks_bottom.setVisibility(View.VISIBLE);
//                            Utils.dismissBarDialog();
//
//                        }
//
//                        @Override
//                        public void onError() {
//
//                        }
//                    });
//            Log.e(TAG,"Modified Url In The Picasso >>>>" +url_to_load);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//Log.e(TAG,"Original Url" +url);
//        }
//=======
        Picasso.with(this)
                .load(url)
                .into(imageView , new Callback() {
                    @Override
                    public void onSuccess() {

                        trd_clicks_top.setVisibility(View.VISIBLE);
                        trd_clicks_bottom.setVisibility(View.VISIBLE);
                        Utils.dismissBarDialog();

                    }

                    @Override
                    public void onError() {

                    }
                });



    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_one:
  				clicks = "05";
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
				clicks = "10";
                trone.setSelected(false);
                trtwo.setSelected(true);
                trthree.setSelected(false);
                trfour.setSelected(false);
                trfive.setSelected(false);
                trd_clicks_bottom.setText("10");
                trd_clicks_top.setText("10");
                break;
            case R.id.btn_three:
 				clicks = "15";
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
                clicks = "20";
                trone.setSelected(false);
                trtwo.setSelected(false);
                trthree.setSelected(false);
                trfour.setSelected(false);
                trfive.setSelected(true);
                trd_clicks_bottom.setText("25");
                trd_clicks_top.setText("25");
                break;
            case R.id.btn_play:

                Intent i=new Intent();
                i.setAction("CARD");
                i.putExtra("FromCard",true);
                if(forCounter){
                 i.putExtra("isCounter",true);
                 i.putExtra("card_id",card_id);
                }else{
                  i.putExtra("isCounter",false);
                }
                i.putExtra("is_CustomCard","false");
                i.putExtra("card_url",url);
                i.putExtra("card_clicks",clicks);
                i.putExtra("Title",cardTitle);
                i.putExtra("Discription",cardDiscription);
                i.putExtra("card_Db_id",card_Db_id);
                i.setClass(this,ChatRecordView.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
              //  finish();
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

