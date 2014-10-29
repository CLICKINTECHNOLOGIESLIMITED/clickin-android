package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
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
import android.widget.TextView;

import com.sourcefuse.clickinapp.R;

/**
 * Created by prafull on 29/9/14.
 */
public class ViewTradeCart extends Activity implements View.OnClickListener
{
    TextWatcher textWatcher ;
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

        trone.setOnClickListener(this);
        trtwo.setOnClickListener(this);
        trthree.setOnClickListener(this);
        trfour.setOnClickListener(this);
        trfive.setOnClickListener(this);



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
                trone.setSelected(true);
                trtwo.setSelected(false);
                trthree.setSelected(false);
                trfour.setSelected(false);
                trfive.setSelected(false);
                trd_clicks_bottom.setText("05");
                trd_clicks_top.setText("05");
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




}
