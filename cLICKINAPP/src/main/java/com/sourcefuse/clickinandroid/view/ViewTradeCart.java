package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.utils.CardDialog;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

/**
 * Created by prafull on 29/9/14.
 */
public class ViewTradeCart extends Activity implements View.OnClickListener
{
    TextWatcher textWatcher ;
    ImageView mback ,btnPlay;
    EditText card_text;
    TextView trd_clicks_top,trd_clicks_bottom;
    TextView trone,trtwo,trthree,trfour,trfive,tv_about_message;
    RelativeLayout layout ;
    String url,clicks,cardTitle,cardDiscription,card_id;
    private static final String TAG = "CardViewAdapter";


    boolean forCounter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_tradecart);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        layout = (RelativeLayout)findViewById(R.id.rr_send);
        LinearLayout back = (LinearLayout)findViewById(R.id.linear_layout_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_finish_up, R.anim.slide_out_finish_up);
            }
        });

        mback = (ImageView)findViewById(R.id.m_back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

                overridePendingTransition(R.anim.slide_in_finish_up, R.anim.slide_out_finish_up);

            }
        });



        trone = (TextView)findViewById(R.id.btn_one);
        trtwo = (TextView)findViewById(R.id.btn_two);
        trthree = (TextView)findViewById(R.id.btn_three);
        trfour = (TextView)findViewById(R.id.btn_four);
        trfive = (TextView)findViewById(R.id.btn_five);
        tv_about_message = (TextView)findViewById(R.id.tv_about_message);

        trone.setOnClickListener(this);
        trtwo.setOnClickListener(this);
        trthree.setOnClickListener(this);
        trfour.setOnClickListener(this);
        trfive.setOnClickListener(this);
        btnPlay = (ImageView)findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);


//        Intent intent = getIntent();
//        if (null != intent) {
//
//            forCounter = intent.getExtras().getBoolean("ForCounter");
//            if(forCounter){
//                trd_clicks_bottom.setText(intent.getStringExtra("cardClicks"));
//                trd_clicks_top.setText(intent.getStringExtra("cardClicks"));
//                tv_about_message.setText("HOW MANY CLICKS DO YOU WANT FOR IT?");
//            }
//            url = intent.getStringExtra("Url");
//            cardTitle = intent.getStringExtra("Title");
//            cardDiscription = intent.getStringExtra("Discription");
//            card_id = intent.getStringExtra("card_id");
//            Log.e(TAG, "Url for the fetched Card is" + url + "," + cardTitle + "," + cardDiscription + "," + card_id);
//
//        } else {
//            Log.e(TAG, "Value in intent in null");
//        }

        card_text=(EditText)findViewById(R.id.card_text12);
        card_text.setMaxLines(5);
        card_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(50);
                card_text.setFilters(FilterArray);
            }

            @Override
            public void afterTextChanged(Editable editable) {


//                TextWatcher textWatcher1 = null;
//               try {
//                   String s = card_text.getText().toString();
//
//                   if (s.length() % 14 == 0 && s.length() > 0) {
//
//                       s = s + "\n";
//
//                       card_text.removeTextChangedListener(textWatcher1);
//
//                       card_text.setText(s);
//
//                       card_text.addTextChangedListener(textWatcher1);
//                   }
//
//               }catch (Exception e){
//                   e.printStackTrace();
//               }
            }
        });


        trd_clicks_top=(TextView)findViewById(R.id.trd_clicks_top);
        trd_clicks_bottom=(TextView)findViewById(R.id.trd_clicks_bottom);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.btn_one:
                clicks = "05";
                trone.setSelected(true);
                trtwo.setSelected(false);
                trthree.setSelected(false);
                trfour.setSelected(false);
                trfive.setSelected(false);
                trd_clicks_bottom.setText("05");
                trd_clicks_top.setText("05");
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
                clicks = "20";
                trone.setSelected(false);
                trtwo.setSelected(false);
                trthree.setSelected(false);
                trfour.setSelected(true);
                trfive.setSelected(false);
                trd_clicks_bottom.setText("20");
                trd_clicks_top.setText("20");
                break;
            case R.id.btn_five:
                clicks = "25";
                trone.setSelected(false);
                trtwo.setSelected(false);
                trthree.setSelected(false);
                trfour.setSelected(false);
                trfive.setSelected(true);
                trd_clicks_bottom.setText("25");
                trd_clicks_top.setText("25");
                break;
            case R.id.btn_play:
                String text = card_text.getText().toString();
                if (trd_clicks_top.getText().equals(" 0") && trd_clicks_bottom.getText().equals("0 "))
                {
                    CardDialog cardDialog = new CardDialog();
                    cardDialog.popupDialog(ViewTradeCart.this);

                }else if((text == null || text.equalsIgnoreCase("null")
                        || text.equalsIgnoreCase("") || text.length() < 1)){
                    CardDialog cardDialog = new CardDialog();
                    cardDialog.popupDialog1(ViewTradeCart.this);
                }

                else {
                    String strnull = null;

                    Log.e(TAG, "Custom CARD Is >>>>>>>.");
                    Intent i = new Intent();
                    i.putExtra("FromCard", true);
                    if (forCounter) {
                        i.putExtra("isCounter", true);
                    } else {
                        i.putExtra("isCounter", false);
                    }
                    i.putExtra("card_url", strnull);
                    i.putExtra("card_clicks", clicks);
                    i.putExtra("Title", cardTitle);
                    i.putExtra("Discription", cardDiscription);
                    i.putExtra("card_id", strnull);
                    i.putExtra("CardText", text);
                    i.setAction("CARD");

                    i.setClass(this, ChatRecordView.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    //  finish();

//                Intent i=new Intent();
//                i.putExtra("FromCard",true);
//                if(forCounter){
//                    i.putExtra("isCounter",true);
//                }else{
//                    i.putExtra("isCounter",false);
//                }
//                i.putExtra("card_url",url);
//                i.putExtra("card_clicks",clicks);
//                i.putExtra("Title",cardTitle);
//                i.putExtra("Discription",cardDiscription);
//                i.putExtra("card_id",card_id);
//
//                i.setClass(this,ChatRecordView.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
                    //  finish();
                    break;
                }
        }

        }
    }





