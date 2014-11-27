package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.utils.AlertMessage;
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
    String clicks,cardTitle,card_id;
    String url ;
    private static final String TAG = "ViewTradeCart";
    Context context ;

    boolean forCounter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_tradecart);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        layout = (RelativeLayout)findViewById(R.id.rr_send);


        mback = (ImageView)findViewById(R.id.m_back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

                overridePendingTransition(0, R.anim.slide_out_finish_up);

            }
        });

        // akshit code for closing keypad if touched anywhere outside
        ((LinearLayout) findViewById(R.id.linear_layout_root_viewtradecard)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(card_text.getWindowToken(), 0);


            }

        });

//ends

        trone = (TextView)findViewById(R.id.btn_one);
        trtwo = (TextView)findViewById(R.id.btn_two);
        trthree = (TextView)findViewById(R.id.btn_three);
        trfour = (TextView)findViewById(R.id.btn_four);
        trfive = (TextView)findViewById(R.id.btn_five);
        tv_about_message = (TextView)findViewById(R.id.custom_card_msg_t);
        card_text=(EditText)findViewById(R.id.card_text12);
        trd_clicks_top=(TextView)findViewById(R.id.trd_clicks_top);
        trd_clicks_bottom=(TextView)findViewById(R.id.trd_clicks_bottom);

        trone.setOnClickListener(this);
        trtwo.setOnClickListener(this);
        trthree.setOnClickListener(this);
        trfour.setOnClickListener(this);
        trfive.setOnClickListener(this);
        card_text.setOnClickListener(this);


        btnPlay = (ImageView)findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);

            try {
                Intent intent = getIntent();
                if (null != intent) {
                    forCounter = intent.getExtras().getBoolean("ForCounter");
                    if (forCounter) {
                        card_text.setVisibility(View.GONE);
                        trd_clicks_bottom.setText(intent.getStringExtra("cardClicks"));
                        trd_clicks_top.setText(intent.getStringExtra("cardClicks"));
                        tv_about_message.setText("HOW MANY CLICKS DO YOU WANT FOR IT?");
                        cardTitle = intent.getStringExtra("Title");
                        Log.e(TAG,""+cardTitle);
                        card_id = intent.getStringExtra("card_id");
                        ((TextView)findViewById(R.id.card_heading)).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.card_heading)).setText(cardTitle);
                        url = intent.getStringExtra("Url");
                        card_text.setText(cardTitle);
                    }else{
                        card_text.setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.card_heading)).setVisibility(View.GONE);
                    }
                } else {
                    Log.e(TAG, "Value in intent in null");
                }
                }catch (Exception e){
                e.printStackTrace();
            }




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

            }
        });


        card_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(card_text.getWindowToken(), 0);
                }
                return false;
            }
        });

    }




    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.btn_one:

                clicks = "05";
                btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play_white));
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
                btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play_white));
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
                btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play_white));
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
                btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play_white));
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
                btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play_white));
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
                    Utils.fromSignalDialog(this, AlertMessage.selectClicks);
                }else if((text == null || text.equalsIgnoreCase("null")
                        || text.equalsIgnoreCase("") || text.length() < 1)) {
                    Utils.fromSignalDialog(this, AlertMessage.enterCustomCardtext);

                }

                else {
                   cardTitle = card_text.getText().toString();
                   Intent i=new Intent();
                   i.setAction("CARD");
                   i.putExtra("FromCard",true);
                   if(forCounter){
                       i.putExtra("isCounter",true);
                       i.putExtra("card_id",card_id);
                       i.putExtra("Title",cardTitle);
                       i.putExtra("is_CustomCard","true");
                       i.putExtra("card_url",url);
                       i.putExtra("card_clicks",clicks);
                   }else{
                       i.putExtra("isCounter",false);
                       i.putExtra("card_id","");
                       i.putExtra("is_CustomCard","true");
                       i.putExtra("Title",cardTitle);
                       i.putExtra("card_url",url);
                       i.putExtra("card_clicks",clicks);
                   }
                   i.setClass(this,ChatRecordView.class);
                   i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(i);

                }
            case R.id.card_text12:
                card_text.setCursorVisible(true);
                card_text.requestFocus();
                card_text.setHint("");
                card_text.setGravity(Gravity.CENTER);
        }


           }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.slide_out_finish_up);
    }
}






