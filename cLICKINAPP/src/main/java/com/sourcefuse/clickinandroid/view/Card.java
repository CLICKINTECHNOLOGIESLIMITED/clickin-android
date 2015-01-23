package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by akshit on 2/10/14.
 */
public class Card extends Activity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "CardViewAdapter";
    private static Dialog dialog;
    ImageView mBackButton;
    String url, clicks, cardTitle, cardDiscription, card_Db_id, card_id = "", card_Accepted_Rejected;
    String card_originator = null, card_owner =" ";
    Context context;
    ImageView imageView, btnPlay;
    boolean forCounter = false;
    TextView trd_clicks_top, trd_clicks_bottom, trone, trtwo, trthree, trfour, trfive;
    private TextView tv_about_message;
    private AuthManager authManager;
    private String chatId = null;
    // String xyz_url = "https://s3.amazonaws.com/clickin-dev/cards/a/1080/39.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.card_click);
        Utils.launchBarDialog(Card.this);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        ((TextView) findViewById(R.id.textView_clicks)).setText(authManager.ourClicks);
        trd_clicks_bottom = (TextView) findViewById(R.id.trd_clicks_bottom);
        trd_clicks_top = (TextView) findViewById(R.id.trd_clicks_top);
        tv_about_message = (TextView) findViewById(R.id.tv_about_message);

        trone = (TextView) findViewById(R.id.btn_one);
        trone.setOnClickListener(this);
        trtwo = (TextView) findViewById(R.id.btn_two);
        trtwo.setOnClickListener(this);
        trthree = (TextView) findViewById(R.id.btn_three);
        trthree.setOnClickListener(this);
        trfour = (TextView) findViewById(R.id.btn_four);
        trfour.setOnClickListener(this);
        trfive = (TextView) findViewById(R.id.btn_five);
        trfive.setOnClickListener(this);
        btnPlay = (ImageView) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);

//        LinearLayout back = (LinearLayout)findViewById(R.id.linear_layout_back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                overridePendingTransition(R.anim.slide_in_finish_up, R.anim.slide_out_finish_up);
//            }
//        });

        imageView = (ImageView) findViewById(R.id.trade_image);
        mBackButton = (ImageView) findViewById(R.id.m_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, R.anim.slide_out_finish_up);
            }
        });

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
                clicks = intent.getStringExtra("card_clicks");

                if (clicks.equalsIgnoreCase("5"))//akshit code if th clicks ar 5
                {
                    trd_clicks_bottom.setText("05");
                    trd_clicks_top.setText("05");

                } else {
                    trd_clicks_bottom.setText(intent.getStringExtra("card_clicks"));
                    trd_clicks_top.setText(intent.getStringExtra("card_clicks"));
                }

                card_id = intent.getStringExtra("card_id");
                //monika-set the message as per sender and receiver


                card_originator = intent.getExtras().getString("card_originator");
                String msg;
                if (card_owner.equalsIgnoreCase(authManager.getQBId())) {
                    msg = "HOW MANY CLICKS ARE YOU WILLING TO OFFER?";
                } else {
                    msg = "HOW MANY CLICKS DO YOU WANT FOR IT?";
                }

                tv_about_message.setText(msg);

            }
            url = intent.getStringExtra("card_url");
            cardTitle = intent.getStringExtra("Title");
            cardDiscription = intent.getStringExtra("Discription");
            card_Db_id = intent.getStringExtra("card_DB_ID");


        } else {
        }
        try {
            String url_to_load = url.replaceFirst("cards\\/(\\d+)\\.jpg", "cards\\/a\\/1080\\/$1\\.jpg");
            Picasso.with(this)
                    .load(url_to_load)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            trd_clicks_top.setVisibility(View.VISIBLE);
                            trd_clicks_bottom.setVisibility(View.VISIBLE);
                            Utils.dismissBarDialog();

                        }

                        @Override
                        public void onError() {
                            Utils.dismissBarDialog();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                trd_clicks_top.setText("05")
                ;
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
                //monika-code to restrict user if don't have enough clicks


                if (trd_clicks_top.getText().equals("  0") && trd_clicks_bottom.getText().equals("0")) {

                    Utils.fromSignalDialog(this, AlertMessage.selectClicks);

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

                        Utils.fromSignalDialog(Card.this, "You don't have enough clicks to play this card");

                    } else if (tempclicks > tempOurClicks) {
                        Utils.fromSignalDialog(Card.this, "You don't have enough clicks to play this card");


                    } else {


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
                        i.putExtra("is_CustomCard", false);
                        i.putExtra("card_url", url);
                        i.putExtra("card_clicks", clicks);
                        i.putExtra("Title", cardTitle);
                        i.putExtra("Discription", cardDiscription);
                        i.putExtra("card_DB_ID", card_Db_id);

                        i.setClass(this, ChatRecordView.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        overridePendingTransition(0, R.anim.slide_out_finish_up);
                        /*code to play sound in case of trade cart */
                        Utils.playSound(Card.this, R.raw.message_sent);
                    }
                    //loop- ends here if user is card owner
                } else {//
                    //loop starts if user is not card owner

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
                    i.putExtra("is_CustomCard", false);
                    i.putExtra("card_url", url);
                    i.putExtra("card_clicks", clicks);
                    i.putExtra("Title", cardTitle);
                    i.putExtra("Discription", cardDiscription);
                    i.putExtra("card_DB_ID", card_Db_id);

                    i.setClass(this, ChatRecordView.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    overridePendingTransition(0, R.anim.slide_out_finish_up);

                            /*code to play sound in case of trade cart */
                    Utils.playSound(Card.this, R.raw.message_sent);

                }//loop ends here if user is not card owner
                //  finish();
                break;
        }
    }


    //akshit code starts
    public void isClickIsEnough(Activity activity, String msgStrI) {

        final Dialog dialog = new Dialog(activity);
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.slide_out_finish_up);
    }


}

