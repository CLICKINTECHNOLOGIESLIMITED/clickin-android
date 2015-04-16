package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;

/**
 * Created by prafull on 29/9/14.
 */
public class ViewTradeCart extends Activity implements View.OnClickListener {
    private static final String TAG = "ViewTradeCart";
    TextWatcher textWatcher;
    ImageView mback, btnPlay;
    EditText card_text;
    TextView trd_clicks_top, trd_clicks_bottom;
    TextView trone, trtwo, trthree, trfour, trfive, tv_about_message;
    RelativeLayout layout;
    String clicks, cardTitle, card_id = null;
    String url;
    Context context;
    String card_originator = null, card_owner = null;
    boolean forCounter = false;
    private Dialog dialog;
    private AuthManager authManager;
    private String chatId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));


        setContentView(R.layout.view_tradecart);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        layout = (RelativeLayout) findViewById(R.id.rr_send);

        authManager = ModelManager.getInstance().getAuthorizationManager();
        ((TextView) findViewById(R.id.textView_clicks)).setText(authManager.ourClicks);

        mback = (ImageView) findViewById(R.id.m_back);
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

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (card_text.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(card_text.getWindowToken(), 0);


            }

        });

//ends

        trone = (TextView) findViewById(R.id.btn_one);
        trtwo = (TextView) findViewById(R.id.btn_two);
        trthree = (TextView) findViewById(R.id.btn_three);
        trfour = (TextView) findViewById(R.id.btn_four);
        trfive = (TextView) findViewById(R.id.btn_five);
        tv_about_message = (TextView) findViewById(R.id.custom_card_msg_t);
        card_text = (EditText) findViewById(R.id.card_text12);
        trd_clicks_top = (TextView) findViewById(R.id.trd_clicks_top);
        trd_clicks_bottom = (TextView) findViewById(R.id.trd_clicks_bottom);

        trone.setOnClickListener(this);
        trtwo.setOnClickListener(this);
        trthree.setOnClickListener(this);
        trfour.setOnClickListener(this);
        trfive.setOnClickListener(this);
        card_text.setOnClickListener(this);


        btnPlay = (ImageView) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);


        AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra("card_owner"))
                card_owner = intent.getExtras().getString("card_owner");
            else
                card_owner = ModelManager.getInstance().getAuthorizationManager().getQBId();
            forCounter = intent.getExtras().getBoolean("ForCounter");
            if (forCounter) {
                chatId = intent.getStringExtra("chat_id");
                ((ImageView) findViewById(R.id.trade_image)).setImageResource(R.drawable.c_pink_counter);//akshit code

                clicks = intent.getStringExtra("card_clicks");

                if (clicks.equalsIgnoreCase("5"))//akshit code if th clicks are 5
                {
                    trd_clicks_bottom.setText("05");
                    trd_clicks_top.setText("05");

                } else {
                    trd_clicks_bottom.setText(intent.getStringExtra("card_clicks"));
                    trd_clicks_top.setText(intent.getStringExtra("card_clicks"));
                }

                card_id = intent.getStringExtra("card_id");
                cardTitle = intent.getStringExtra("Title");

                card_text.setBackground(getResources().getDrawable(R.color.transparent));//akshit code
                card_text.setTextColor(Color.WHITE);//akshit code
                card_text.setInputType(InputType.TYPE_NULL);//akshit code
                card_text.setHorizontallyScrolling(false);//akshit code
                card_text.setSingleLine(false);//akshit code
                card_text.setLines(5);//akshit code
                card_text.setTextSize(25);//akshit code
                card_text.setText(cardTitle);

                card_originator = intent.getExtras().getString("card_originator");

                String msg;
                if (card_owner.equalsIgnoreCase(authManager.getQBId())) {
                    msg = "HOW MANY CLICKS ARE YOU WILLING TO OFFER?";
                } else {
                    msg = "HOW MANY CLICKS DO YOU WANT FOR IT?";
                }
                tv_about_message.setText(msg);

            }

        } else {
        }


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
                    if (card_text.getWindowToken() != null)
                        imm.hideSoftInputFromWindow(card_text.getWindowToken(), 0);
                }
                return false;
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

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
                AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
                String text = card_text.getText().toString();
                if (trd_clicks_top.getText().equals(" 0") && trd_clicks_bottom.getText().equals("0 ")) {
                    Utils.fromSignalDialog(this, AlertMessage.selectClicks);
                } else if ((text == null || text.equalsIgnoreCase("null")
                        || text.equalsIgnoreCase("") || text.length() < 1)) {
                    Utils.fromSignalDialog(this, AlertMessage.enterCustomCardtext);

                } else if (card_owner.equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getQBId())) {
                    //Loop- if user is card owner
                    int tempOurClicks = 0;
                    tempOurClicks = Integer.parseInt(authManager.ourClicks);
                    int tempclicks = 0;
                    if (clicks.equalsIgnoreCase("05")) {
                        tempclicks = 5;
                    } else
                        tempclicks = Integer.parseInt(clicks);

                    //now check whether it has enough clicks or not

                    if (authManager.ourClicks.startsWith("-")) {

                        Utils.fromSignalDialog(ViewTradeCart.this, "You don't have enough clicks to play this card");

                    } else if (tempclicks > tempOurClicks) {
                        Utils.fromSignalDialog(ViewTradeCart.this, "You don't have enough clicks to play this card");


                    } else {

                        cardTitle = card_text.getText().toString();
                        Intent i = new Intent();
                        i.setAction("CARD");
                        i.putExtra("FromCard", true);
                        if (forCounter) {

                            i.putExtra("isCounter", true);

                            i.putExtra("card_Accepted_Rejected", "countered");
                            //for counter case, update the last message in list for card played countered value
                            if (!Utils.isEmptyString(chatId)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList<ChatMessageBody> chatList = ModelManager.getInstance().getChatManager().chatMessageList;
                                        for (ChatMessageBody temp : chatList) {
                                            if (temp.chatId.equalsIgnoreCase(chatId)) {
                                                temp.card_Played_Countered = "played";
                                            }
                                        }
                                    }
                                }).start();
                            }

                        } else {
                            i.putExtra("isCounter", false);
                            i.putExtra("card_Accepted_Rejected", "nil");
                        }
                        i.putExtra("card_originator", card_originator);
                        i.putExtra("card_owner", card_owner);
                        i.putExtra("card_id", card_id);
                        i.putExtra("played_Countered", "playing");
                        i.putExtra("is_CustomCard", true);
                        i.putExtra("card_url", url);
                        i.putExtra("card_clicks", clicks);
                        i.putExtra("Title", cardTitle);


                        i.setClass(this, ChatRecordView.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        overridePendingTransition(0, R.anim.slide_out_finish_up);
                            /*code to play sound in case of trade cart */
                        Utils.playSound(ViewTradeCart.this, R.raw.message_sent);

                        //To track through mixPanel.
                        //TradeCard Visited.
                        Utils.trackMixpanel(this, "Card Visited", cardTitle, "RPageTradeButtonClicked", false);
                        //To track through mixPanel.
                        //TradeCard Played.
                        Utils.trackMixpanel(this, "CardPlayedName", "" + cardTitle, "RPageTradeButtonClicked", false);

                    }
                    //loop- ends here if user is card owner
                } else {//
                    //loop starts if user is not card owner
                    cardTitle = card_text.getText().toString();
                    Intent i = new Intent();
                    i.setAction("CARD");
                    i.putExtra("FromCard", true);
                    if (forCounter) {

                        i.putExtra("isCounter", true);

                        i.putExtra("card_Accepted_Rejected", "countered");
                        //for counter case, update the last message in list for card played countered value
                        if (!Utils.isEmptyString(chatId)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<ChatMessageBody> chatList = ModelManager.getInstance().getChatManager().chatMessageList;
                                    for (ChatMessageBody temp : chatList) {
                                        if (temp.chatId.equalsIgnoreCase(chatId)) {
                                            temp.card_Played_Countered = "played";
                                        }
                                    }
                                }
                            }).start();
                        }

                    } else {
                        i.putExtra("isCounter", false);
                        i.putExtra("card_Accepted_Rejected", "nil");
                    }
                    i.putExtra("card_originator", card_originator);
                    i.putExtra("card_owner", card_owner);
                    i.putExtra("card_id", card_id);
                    i.putExtra("played_Countered", "playing");
                    i.putExtra("is_CustomCard", true);
                    i.putExtra("card_url", url);
                    i.putExtra("card_clicks", clicks);
                    i.putExtra("Title", cardTitle);


                    i.setClass(this, ChatRecordView.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    overridePendingTransition(0, R.anim.slide_out_finish_up);
                        /*code to play sound in case of trade cart */
                    Utils.playSound(ViewTradeCart.this, R.raw.message_sent);

                    //To track through mixPanel.
                    //TradeCard Visited.
                    Utils.trackMixpanel(this, "Card Visited", cardTitle, "RPageTradeButtonClicked", false);
                    //To track through mixPanel.
                    //TradeCard Played.
                    Utils.trackMixpanel(this, "CardPlayedName", "" + cardTitle, "RPageTradeButtonClicked", false);
                }//loop ends here if user is not card owner
                //  finish();
                break;
            case R.id.card_text12:
                card_text.setCursorVisible(true);
                card_text.requestFocus();
                card_text.setHint("");
                card_text.setGravity(Gravity.CENTER);

        }


    }

    //akshit code starts
    public void isClickIsEnough(Activity activity, String msgStrI) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(msgStrI);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
//                finish();
//
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.slide_out_finish_up);
    }
}






