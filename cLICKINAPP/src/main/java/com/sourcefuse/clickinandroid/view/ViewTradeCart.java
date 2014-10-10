package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinapp.R;

/**
 * Created by prafull on 29/9/14.
 */
public class ViewTradeCart extends Activity implements View.OnClickListener
{
    ImageView mback ;
    EditText card_text;
    TextView trd_clicks_top,trd_clicks_bottom;
    TextView trone,trtwo,trthree,trfour,trfive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_tradecart);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mback = (ImageView)findViewById(R.id.iv_back_noti);
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

        trone.setOnClickListener(this);
        trtwo.setOnClickListener(this);
        trthree.setOnClickListener(this);
        trfour.setOnClickListener(this);
        trfive.setOnClickListener(this);



        card_text=(EditText)findViewById(R.id.card_text12);
        card_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(card_text.length()==14){
                    ((Editable)s).append("\n");

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {


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
                trd_clicks_bottom.setText("05");
                trd_clicks_top.setText("05");
                break;
            case R.id.btn_two:
                trd_clicks_bottom.setText("10");
                trd_clicks_top.setText("10");
                break;
            case R.id.btn_three:
                trd_clicks_bottom.setText("15");
                trd_clicks_top.setText("15");
                break;
            case R.id.btn_four:
                trd_clicks_bottom.setText("20");
                trd_clicks_top.setText("20");
                break;
            case R.id.btn_five:
                trd_clicks_bottom.setText("25");
                trd_clicks_top.setText("25");
                break;
            case R.id.btn_play:
                break;
        }
    }




}
