package com.sourcefuse.clickinandroid.view.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.android.Util;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.CardBean;
import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
import com.sourcefuse.clickinandroid.utils.APIs;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.AddSomeoneView;
import com.sourcefuse.clickinandroid.view.Card;
import com.sourcefuse.clickinandroid.view.ChatRecordView;
import com.sourcefuse.clickinandroid.view.ImageViewer;
import com.sourcefuse.clickinandroid.view.ViewShare;
import com.sourcefuse.clickinandroid.view.ViewTradeCart;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatRecordAdapter extends ArrayAdapter<ChatRecordBeen>{

    private static final String TAG = ChatRecordAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    private AuthManager authManager;
    ChatRecordBeen item ;

    public ChatRecordAdapter(Context context, int layoutResourceId,
                             List<ChatRecordBeen> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatRecordBeen item = getItem(position);
        View row = convertView;
        RecordHolder holder = null;
    //    if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();

            holder.rrMainLayout = (RelativeLayout) row.findViewById(R.id.rr_main_l);
            holder.rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);
            holder.chatParentLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);
            holder.llSrTime = (LinearLayout) row.findViewById(R.id.ll_sr_time);
            holder.clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);

            holder.cardView = (LinearLayout) row.findViewById(R.id.card_view);
            holder.cardAction = (LinearLayout) row.findViewById(R.id.iv_card_action);
            holder.cardViewCc = (LinearLayout) row.findViewById(R.id.card_view_cc);
            holder.cardViewCards = (LinearLayout) row.findViewById(R.id.card_view_cards);

        holder.tradeImage = (ImageView) row.findViewById(R.id.trade_image);
        holder.tradeImage.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.trdClicksTop = (TextView) row.findViewById(R.id.trd_clicks_bottom);
        holder.trdClicksBottom = (TextView) row.findViewById(R.id.trd_clicks_top);
        holder.card_partner_name = (TextView) row.findViewById(R.id.card_partner_name);



        holder.iv_accept_card = (ImageView) row.findViewById(R.id.iv_accept_card);
        holder.iv_resect_card = (ImageView) row.findViewById(R.id.iv_resect_card);
        holder.tv_counter_card = (TextView) row.findViewById(R.id.tv_counter_card);

        holder.ll_cc_action = (LinearLayout) row.findViewById(R.id.ll_cc_action);
        holder.iv_acc_rec = (ImageView) row.findViewById(R.id.iv_acc_rec);

        holder.tv_acc_res_name = (TextView) row.findViewById(R.id.tv_acc_res_name);
        holder.tv_acc_res_status = (TextView) row.findViewById(R.id.tv_acc_res_status);
        holder.rl_acc_res_bg = (RelativeLayout) row.findViewById(R.id.rl_acc_res_bg);

        holder.trd_clicks_top_ar = (TextView) row.findViewById(R.id.trd_clicks_top_ar);
        holder.trd_clicks_bottom_ar = (TextView) row.findViewById(R.id.trd_clicks_bottom_ar);

        holder.iv_card_counter = (ImageView) row.findViewById(R.id.iv_card_counter);



        holder.rl_card = (RelativeLayout) row.findViewById(R.id.rl_card);




            holder.chatImage = (ImageView) row.findViewById(R.id.iv_chat_image);
            holder.chatImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.audioView = (ImageView) row.findViewById(R.id.iv_audioView);
            holder.audioView.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.playIcon = (ImageView) row.findViewById(R.id. iv_play_icon);



            holder.shareIcon = (ImageView) row.findViewById(R.id.iv_type_two_share_icon_r);
            holder.sendStatus = (ImageView) row.findViewById(R.id.iv_send_status);
            holder.clicksHeart = (ImageView) row.findViewById(R.id.iv_clicks_heart);

        holder.iv_again_counter_acc = (ImageView) row.findViewById(R.id.iv_again_counter_acc);
        holder.iv_again_counter_rej = (ImageView) row.findViewById(R.id.iv_again_counter_rej);
        holder.tv_counter_card_action = (TextView) row.findViewById(R.id.tv_counter_card_action);



        holder.trd_custom_heading = (TextView) row.findViewById(R.id.trd_custom_heading);
         holder.trd_custom_ar_heading = (TextView) row.findViewById(R.id.trd_custom_ar_heading);




            holder.chatText = (TextView) row.findViewById(R.id.chat_text);
            holder.timeText = (TextView) row.findViewById(R.id.tv_time_text);
            holder.clicksText = (TextView) row.findViewById(R.id.clicks_text);

        holder.iv_type_two_share_icon_r = (ImageView) row.findViewById(R.id.iv_type_two_share_icon_r);



           // row.setTag(holder);
       /* } else {
            holder = (RecordHolder) row.getTag();
        }*/


        authManager = ModelManager.getInstance().getAuthorizationManager();


        if (item.getChatType().equals("1")) {
            Log.e(TAG,"getUserId-->"+authManager.getUserId()+"From List-->"+item.getUserId());//RECIVER
            holder.chatParentLayout.setVisibility(View.VISIBLE);
            holder.cardView.setVisibility(View.GONE);
            holder.cardViewCc.setVisibility(View.GONE);
            if (!item.getUserId().equalsIgnoreCase(authManager.getUserId())) {
                // Log.e(TAG,""+"RECIVER");//RECIVER
                RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsrr.addRule(RelativeLayout.RIGHT_OF, 0);
                paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(paramsrr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rl_time_sender);
                holder.rrMainLayout.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.RIGHT);

                if(Utils.isEmptyString(item.getClicks())){
                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.clicksArea.setBackgroundResource(R.drawable.whitechatbg);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    holder.chatText.setText(""+item.getChatText().trim());
                    holder.timeText.setText(""+Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));
                }else{
                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_r_bgpink);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksText.setText("" + item.getClicks().trim());
                    holder.clicksHeart.setVisibility(View.VISIBLE);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    if(!Utils.isEmptyString(item.getChatText())){
                        holder.chatText.setVisibility(View.VISIBLE);
                        holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                        holder.chatText.setText(""+item.getChatText().trim());
                    }else {
                        holder.chatText.setVisibility(View.GONE);
                    }
                    holder.timeText.setText(""+Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));
                }
            }else{
                // Log.e(TAG,""+"SENDER");//SENDER
                RelativeLayout.LayoutParams paramsr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsr.addRule(RelativeLayout.RIGHT_OF,0);
                paramsr.addRule(RelativeLayout.ALIGN_BOTTOM,0);
                holder.rrMainLayout.setLayoutParams(paramsr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rr_main_l);
                params.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.LEFT);

                if(item.getClicks().equalsIgnoreCase("no") || Utils.isEmptyString(item.getClicks())) {
                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.clicksArea.setBackgroundResource(R.drawable.newbg);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    holder.chatText.setText(""+item.getChatText());
                    holder.timeText.setText(""+Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));
                }else{
                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_s_bgpink);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    holder.clicksText.setText(item.getClicks());
                    if(!Utils.isEmptyString(item.getChatText())){
                        holder.chatText.setVisibility(View.VISIBLE);
                        holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                        holder.chatText.setText(""+item.getChatText());
                    }else {
                        holder.chatText.setVisibility(View.GONE);
                    }
                    holder.timeText.setText(""+Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));
                }

            }
        } else if (item.getChatType().equalsIgnoreCase("2")) {
            holder.chatParentLayout.setVisibility(View.VISIBLE);
            holder.cardView.setVisibility(View.GONE);
            holder.cardViewCc.setVisibility(View.GONE);
            if (!item.getUserId().equalsIgnoreCase(authManager.getUserId())) {
                Log.e(TAG, "" + "Type 22 - RECIVER");//RECIVER

                RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsrr.addRule(RelativeLayout.RIGHT_OF, 0);
                paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(paramsrr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rl_time_sender);
                holder.rrMainLayout.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.RIGHT);

                if(Utils.isEmptyString(item.getClicks()) && Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.chatImage.setBackgroundResource(R.drawable.whitechatbg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);

                    Picasso.with(context).load(item.getChatImageUrl())
                            .into(holder.chatImage);

                }else if(Utils.isEmptyString(item.getClicks()) && !Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_white_bg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.whitechatbg);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    Picasso.with(context).load(item.getChatImageUrl())
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.default_profile).into(holder.chatImage);

                }else if(!Utils.isEmptyString(item.getClicks())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_pink_bg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_r_bgpink);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    if(Utils.isEmptyString(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                            holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    }
                    holder.clicksText.setText(""+item.getClicks());
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    holder.sendStatus.setVisibility(View.GONE);

                    Picasso.with(context).load(item.getChatImageUrl())
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.default_profile).into(holder.chatImage);
                }
            }else{
                Log.e(TAG, "" + "Type 22 - SENDER");

                RelativeLayout.LayoutParams paramsr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsr.addRule(RelativeLayout.RIGHT_OF,0);
                paramsr.addRule(RelativeLayout.ALIGN_BOTTOM,0);
                holder.rrMainLayout.setLayoutParams(paramsr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rr_main_l);
                params.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.LEFT);

                    if(Utils.isEmptyString(item.getClicks()) && Utils.isEmptyString(item.getChatText())) {

                        holder.chatImage.setVisibility(View.VISIBLE);
                        holder.audioView.setVisibility(View.GONE);
                        holder.playIcon.setVisibility(View.GONE);
                        holder.shareIcon.setVisibility(View.GONE);
                        holder.clicksArea.setVisibility(View.GONE);
                        holder.sendStatus.setVisibility(View.GONE);
                        holder.clicksText.setVisibility(View.GONE);
                        holder.clicksHeart.setVisibility(View.GONE);
                        holder.chatImage.setBackgroundResource(R.drawable.newbg);
                        Picasso.with(context)
                                .load(item.getChatImageUrl()).resize(275,275).centerCrop()
                                .into(holder.chatImage);
                        holder.timeText.setText("" + Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));

                    } else if(Utils.isEmptyString(item.getClicks()) && !Utils.isEmptyString(item.getChatText())) {

                        holder.chatImage.setVisibility(View.VISIBLE);
                        holder.audioView.setVisibility(View.GONE);
                        holder.playIcon.setVisibility(View.GONE);
                        holder.chatImage.setBackgroundResource(R.drawable.chat_image_white_bg);
                        holder.shareIcon.setVisibility(View.GONE);
                        holder.clicksArea.setVisibility(View.VISIBLE);
                        holder.sendStatus.setVisibility(View.GONE);
                        holder.clicksText.setVisibility(View.GONE);
                        holder.clicksHeart.setVisibility(View.GONE);
                        holder.chatText.setVisibility(View.VISIBLE);
                        holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                        holder.clicksArea.setBackgroundResource(R.drawable.newbg);
                        Picasso.with(context).load(item.getChatImageUrl()).resize(275,275).centerCrop()
                                .into(holder.chatImage);
                        holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                        holder.timeText.setText("" + Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));

                }else if(!Utils.isEmptyString(item.getClicks()) ){

                    Log.e(TAG, "" + "Type 22 - Click");
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_pink_bg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_s_bgpink);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    if(Utils.isEmptyString(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                            holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    }
                    holder.clicksText.setText(""+item.getClicks());
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.timeText.setText(""+Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));
                    Picasso.with(context).load(item.getChatImageUrl()).resize(275,275).centerCrop()
                            .into(holder.chatImage);

                }

            }
        }else if (item.getChatType().equalsIgnoreCase("3")) {
            holder.chatParentLayout.setVisibility(View.VISIBLE);
            holder.cardView.setVisibility(View.GONE);
            holder.cardViewCc.setVisibility(View.GONE);
            if (!item.getUserId().equalsIgnoreCase(authManager.getUserId())) {
                Log.e(TAG, "" + "Type 33 - RECIVER");//RECIVER

                RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsrr.addRule(RelativeLayout.RIGHT_OF, 0);
                paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(paramsrr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rl_time_sender);
                holder.rrMainLayout.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.RIGHT);

                if(Utils.isEmptyString(item.getClicks()) && Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.VISIBLE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.audioView.setBackgroundResource(R.drawable.c_audio_bg_w);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                }else if(Utils.isEmptyString(item.getClicks()) && !Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.VISIBLE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.audioView.setBackgroundResource(R.drawable.c_audio_bg_w);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.whitechatbg);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));

                }else if(!Utils.isEmptyString(item.getClicks())) {
                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.VISIBLE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.audioView.setBackgroundResource(R.drawable.c_audio_bg_click);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_r_bgpink);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    if(Utils.isEmptyString(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                        holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    }
                    holder.clicksText.setText(""+item.getClicks());
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    holder.sendStatus.setVisibility(View.GONE);


                }
            }else{
                Log.e(TAG, "" + "Type 33 - SENDER");

                RelativeLayout.LayoutParams paramsr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsr.addRule(RelativeLayout.RIGHT_OF,0);
                paramsr.addRule(RelativeLayout.ALIGN_BOTTOM,0);
                holder.rrMainLayout.setLayoutParams(paramsr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rr_main_l);
                params.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.LEFT);


                if(Utils.isEmptyString(item.getClicks()) && Utils.isEmptyString(item.getChatText())) {

                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.VISIBLE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.audioView.setBackgroundResource(R.drawable.c_audio_bg_g);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.timeText.setText("" + Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));

                } else if(Utils.isEmptyString(item.getClicks()) && !Utils.isEmptyString(item.getChatText())) {

                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.VISIBLE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.audioView.setBackgroundResource(R.drawable.c_audio_bg_g);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    holder.clicksArea.setBackgroundResource(R.drawable.newbg);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    holder.timeText.setText("" + Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));

                }else if(!Utils.isEmptyString(item.getClicks()) ){

                    Log.e(TAG, "" + "Type 33 - Click");

                    holder.chatImage.setVisibility(View.GONE);
                    holder.audioView.setVisibility(View.VISIBLE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.audioView.setBackgroundResource(R.drawable.c_audio_bg_click);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_s_bgpink);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    if(Utils.isEmptyString(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                        holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    }
                    holder.clicksText.setText(""+item.getClicks());
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.timeText.setText(""+Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));

                }
            }
        }else if (item.getChatType().equalsIgnoreCase("4")) {
            holder.chatParentLayout.setVisibility(View.VISIBLE);
            holder.cardView.setVisibility(View.GONE);
            holder.cardViewCc.setVisibility(View.GONE);
            if (!item.getUserId().equalsIgnoreCase(authManager.getUserId())) {
                Log.e(TAG, "" + "Type 44 -VIDEO RECIVER");//RECIVER

                RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsrr.addRule(RelativeLayout.RIGHT_OF, 0);
                paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(paramsrr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rl_time_sender);
                holder.rrMainLayout.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.RIGHT);

                if(Utils.isEmptyString(item.getClicks()) && Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.whitechatbg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    Picasso.with(context).load(item.getVideo_thumb())
                            .into(holder.chatImage);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                }else if(Utils.isEmptyString(item.getClicks()) && !Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_white_bg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.whitechatbg);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    Picasso.with(context).load(item.getVideo_thumb())
                            .into(holder.chatImage);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                }else if(!Utils.isEmptyString(item.getClicks())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.c_audio_bg_click);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_r_bgpink);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    if(Utils.isEmptyString(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                        holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    }
                    holder.clicksText.setText(""+item.getClicks());
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    holder.sendStatus.setVisibility(View.GONE);

                    Picasso.with(context).load(item.getVideo_thumb())
                            .into(holder.chatImage);
                }
            }else{
                Log.e(TAG, "" + "Type 44 -VIDEO SENDER");

                RelativeLayout.LayoutParams paramsr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsr.addRule(RelativeLayout.RIGHT_OF,0);
                paramsr.addRule(RelativeLayout.ALIGN_BOTTOM,0);
                holder.rrMainLayout.setLayoutParams(paramsr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rr_main_l);
                params.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.LEFT);


                if(Utils.isEmptyString(item.getClicks()) && Utils.isEmptyString(item.getChatText())) {

                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.newbg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    holder.timeText.setText("" + Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));

                    Picasso.with(context).load(item.getVideo_thumb())
                            .into(holder.chatImage);


                } else if(Utils.isEmptyString(item.getClicks()) && !Utils.isEmptyString(item.getChatText())) {

                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.VISIBLE);
                    holder.audioView.setBackgroundResource(R.drawable.chat_image_white_bg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    holder.clicksArea.setBackgroundResource(R.drawable.newbg);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    holder.timeText.setText("" + Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));

                    Picasso.with(context).load(item.getVideo_thumb())
                            .into(holder.chatImage);
                }else if(!Utils.isEmptyString(item.getClicks()) ){

                    Log.e(TAG, "" + "Type 44 -VIDEO Click");

                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_white_bg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_s_bgpink);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    if(Utils.isEmptyString(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                        holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    }
                    holder.clicksText.setText(""+item.getClicks());
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.timeText.setText(""+Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));
                    Picasso.with(context).load(item.getVideo_thumb())
                            .into(holder.chatImage);
                }
            }
        }else if (item.getChatType().equalsIgnoreCase("6")) {
            holder.chatParentLayout.setVisibility(View.VISIBLE);
            holder.cardView.setVisibility(View.GONE);
            holder.cardViewCc.setVisibility(View.GONE);
            if (!item.getUserId().equalsIgnoreCase(authManager.getUserId())) {
                Log.e(TAG, "" + "Type 66 -Location RECIVER");//RECIVER

                RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsrr.addRule(RelativeLayout.RIGHT_OF, 0);
                paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(paramsrr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rl_time_sender);
                holder.rrMainLayout.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.RIGHT);

                if(!Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_white_bg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.whitechatbg);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    Picasso.with(context).load(item.getChatImageUrl())
                            .into(holder.chatImage);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                }
            }else{
                Log.e(TAG, "" + "Type 66 -Location SENDER");

                RelativeLayout.LayoutParams paramsr = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsr.addRule(RelativeLayout.RIGHT_OF,0);
                paramsr.addRule(RelativeLayout.ALIGN_BOTTOM,0);
                holder.rrMainLayout.setLayoutParams(paramsr);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.rr_main_l);
                params.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.rr_main_l);
                holder.rlTimeStatusSender.setLayoutParams(params);
                holder.chatParentLayout.setGravity(Gravity.LEFT);

            if(!Utils.isEmptyString(item.getChatText())) {

                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.audioView.setVisibility(View.GONE);
                    holder.playIcon.setVisibility(View.GONE);
                    holder.audioView.setBackgroundResource(R.drawable.chat_image_white_bg);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    holder.clicksArea.setBackgroundResource(R.drawable.newbg);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    holder.timeText.setText("" + Utils.getLocalDatefromTimestamp(Long.parseLong(item.getTimeStamp())));

                    Picasso.with(context).load(item.getChatImageUrl())
                            .into(holder.chatImage);
                }
            }
        }else if (item.getChatType().equalsIgnoreCase("5")) {

            holder.cardView.setVisibility(View.VISIBLE);
            holder.cardAction.setVisibility(View.VISIBLE);
            holder.chatParentLayout.setVisibility(View.GONE);
            if (!item.getUserId().equalsIgnoreCase(authManager.getUserId())) {
//RECEIVER CARD
                Log.e(TAG, "" + "Type 55 -Card RECIVER");//RECIVER
            try {

                if (item.getCard_Accepted_Rejected().equalsIgnoreCase("accepted")) {
                    holder.cardViewCc.setVisibility(View.VISIBLE);
                    holder.ll_cc_action.setVisibility(View.GONE);
                    holder.cardView.setVisibility(View.GONE);
                    holder.cardViewCc.setGravity(Gravity.RIGHT);
                    holder.tv_acc_res_name.setText(item.getCardPartnerName());
                    holder.tv_acc_res_status.setText("ACCEPTED!");
                    holder.trd_custom_heading.setVisibility(View.GONE);
                    holder.trd_clicks_top_ar.setText(item.getCard_clicks());
                    holder.trd_clicks_bottom_ar.setText(item.getCard_clicks());

                    if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                        holder.trd_custom_ar_heading.setVisibility(View.VISIBLE);
                        holder.trd_custom_ar_heading.setText(item.getCard_heading());
                        holder.iv_card_counter.setVisibility(View.VISIBLE);
                        holder.iv_card_counter.setImageResource(R.drawable.c_custom_card);

                    }else{

                        holder.trd_custom_ar_heading.setVisibility(View.GONE);
                        Picasso.with(context).load(item.getCard_url())
                                .into(holder.iv_card_counter);
                    }
                    holder.iv_acc_rec.setBackgroundResource(R.drawable.c_card_accept);
                    holder.rl_acc_res_bg.setBackgroundResource(R.drawable.whitechatbg);
                } else if (item.getCard_Accepted_Rejected().equalsIgnoreCase("rejected")) {
                    holder.cardViewCc.setVisibility(View.VISIBLE);
                    holder.ll_cc_action.setVisibility(View.GONE);
                    holder.cardView.setVisibility(View.GONE);
                    holder.cardViewCc.setGravity(Gravity.RIGHT);
                    holder.tv_acc_res_name.setText(item.getCardPartnerName());
                    holder.tv_acc_res_status.setText("REJECTED!");
                    holder.trd_custom_heading.setVisibility(View.GONE);
                    holder.trd_clicks_top_ar.setText(item.getCard_clicks());
                    holder.trd_clicks_bottom_ar.setText(item.getCard_clicks());

                    if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                        holder.trd_custom_ar_heading.setVisibility(View.VISIBLE);
                        holder.trd_custom_ar_heading.setText(item.getCard_heading());
                        /*Picasso.with(context).load(Constants.CUSTOM_CARD_URL)
                                .into(holder.iv_card_counter);*/
                      //  holder.tradeImage.setVisibility(View.VISIBLE);
                        holder.iv_card_counter.setVisibility(View.VISIBLE);
                        holder.iv_card_counter.setImageResource(R.drawable.c_custom_card);

                    }else{

                        holder.trd_custom_ar_heading.setVisibility(View.GONE);
                        Picasso.with(context).load(item.getCard_url())
                                .into(holder.iv_card_counter);
                    }
                    holder.rl_acc_res_bg.setBackgroundResource(R.drawable.whitechatbg);
                    holder.iv_acc_rec.setBackgroundResource(R.drawable.c_card_rejected);

                } else if (item.getCard_Accepted_Rejected().equalsIgnoreCase("countered")) {
                    holder.ll_cc_action.setVisibility(View.VISIBLE);
                    holder.cardViewCc.setVisibility(View.VISIBLE);
                    holder.cardView.setVisibility(View.GONE);
                    holder.cardViewCc.setGravity(Gravity.RIGHT);
                    holder.tv_acc_res_name.setText(item.getCardPartnerName() + "made a");
                    holder.tv_acc_res_status.setText("COUNTER OFFER");

                    holder.trd_clicks_top_ar.setText(item.getCard_clicks());
                    holder.trd_clicks_bottom_ar.setText(item.getCard_clicks());

                    if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                        holder.trd_custom_ar_heading.setVisibility(View.VISIBLE);
                        holder.trd_custom_ar_heading.setText(item.getCard_heading());
                        /*Picasso.with(context).load(Constants.CUSTOM_CARD_URL)
                                .into(holder.iv_card_counter);*/
                        holder.iv_card_counter.setImageResource(R.drawable.c_custom_card);

                    }else{
                        holder.tradeImage.setVisibility(View.GONE);
                        holder.trd_custom_ar_heading.setVisibility(View.GONE);
                        Picasso.with(context).load(item.getCard_url())
                                .into(holder.iv_card_counter);
                    }

                    holder.rl_acc_res_bg.setBackgroundResource(R.drawable.whitechatbg);
                    holder.iv_acc_rec.setVisibility(View.GONE);
                }else if (item.getCard_Accepted_Rejected().equalsIgnoreCase("nil") && !item.getIs_CustomCard().equalsIgnoreCase("true")) {
                    holder.cardViewCards.setVisibility(View.VISIBLE);
                    holder.cardViewCards.setGravity(Gravity.RIGHT);
                    holder.trd_custom_heading.setVisibility(View.GONE);

                    Picasso.with(context).load(Utils.getCardURLForAndroid(item.getCard_url()))
                            .into(holder.tradeImage);

                    try {
                        if (Integer.parseInt(item.getCard_clicks()) < 10) {
                            holder.trdClicksTop.setText(item.getCard_clicks());
                            holder.trdClicksBottom.setText(item.getCard_clicks());
                        } else {
                            holder.trdClicksTop.setText(item.getCard_clicks());
                            holder.trdClicksBottom.setText(item.getCard_clicks());
                        }
                    } catch (Exception e) {

                    }
                    holder.card_partner_name.setText(item.getCardPartnerName().toUpperCase());
                    holder.cardViewCards.setBackgroundResource(R.drawable.whitechatbg);
              }else if (item.getIs_CustomCard().equalsIgnoreCase("true") && item.getCard_Accepted_Rejected().equalsIgnoreCase("nil")) {


                    holder.cardViewCards.setVisibility(View.VISIBLE);
                    Log.e(TAG, "" + "Type 55 -Custom Card");
                    holder.cardViewCards.setGravity(Gravity.RIGHT);

                        /*Picasso.with(context).load(Constants.CUSTOM_CARD_URL)
                                .into(holder.iv_card_counter);*/
                    holder.tradeImage.setBackgroundResource(R.drawable.c_custom_card);
                    holder.trd_custom_heading.setVisibility(View.VISIBLE);
                    holder.trd_custom_heading.setText(item.getCard_heading());
                    holder.cardViewCards.setBackgroundResource(R.drawable.whitechatbg);

                    try {
                        if (Integer.parseInt(item.getCard_clicks()) < 10) {
                            holder.trdClicksTop.setText("" + item.getCard_clicks());
                            holder.trdClicksBottom.setText("" + item.getCard_clicks());
                        } else {
                            holder.trdClicksTop.setText(item.getCard_clicks());
                            holder.trdClicksBottom.setText(item.getCard_clicks());
                        }

                        holder.card_partner_name.setText(item.getCardPartnerName().toUpperCase());

                    } catch (Exception e) {

                    }
                }
            }catch (Exception e){}
                //RECEIVER END FINISH
            }else {
                Log.e(TAG, "" + "Type 55 -Card SENDER");

                holder.cardViewCards.setGravity(Gravity.LEFT);
                holder.cardAction.setVisibility(View.GONE);
                try {

                if (item.getCard_Accepted_Rejected().equalsIgnoreCase("accepted")) {
                    holder.cardViewCc.setVisibility(View.VISIBLE);
                    holder.ll_cc_action.setVisibility(View.GONE);
                    holder.cardView.setVisibility(View.GONE);
                    holder.cardViewCc.setGravity(Gravity.LEFT);
                    holder.tv_acc_res_name.setText("You");
                    holder.tv_acc_res_status.setText("ACCEPTED!");
                    holder.iv_acc_rec.setVisibility(View.VISIBLE);
                    holder.trd_custom_heading.setVisibility(View.GONE);
                    holder.iv_acc_rec.setBackgroundResource(R.drawable.c_card_accept);
                    holder.trd_clicks_top_ar.setText(item.getCard_clicks());
                    holder.trd_clicks_bottom_ar.setText(item.getCard_clicks());
                    if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                        holder.trd_custom_ar_heading.setVisibility(View.VISIBLE);
                        holder.trd_custom_ar_heading.setText(item.getCard_heading());
                        /*Picasso.with(context).load(Constants.CUSTOM_CARD_URL)
                                .into(holder.iv_card_counter);*/
                        holder.iv_card_counter.setVisibility(View.VISIBLE);
                        holder.iv_card_counter.setImageResource(R.drawable.c_custom_card);

                    }else{
                        //holder.tradeImage.setVisibility(View.GONE);
                        holder.trd_custom_ar_heading.setVisibility(View.GONE);
                        Picasso.with(context).load(item.getCard_url())
                                .into(holder.iv_card_counter);
                    }
                    holder.rl_acc_res_bg.setBackgroundResource(R.drawable.newbg);
                } else if (item.getCard_Accepted_Rejected().equalsIgnoreCase("rejected")) {
                    holder.cardViewCc.setVisibility(View.VISIBLE);
                    holder.ll_cc_action.setVisibility(View.GONE);
                    holder.cardView.setVisibility(View.GONE);
                    holder.cardViewCc.setGravity(Gravity.LEFT);
                    holder.tv_acc_res_name.setText("You");
                    holder.tv_acc_res_status.setText("REJECTED!");
                    holder.trd_custom_heading.setVisibility(View.GONE);
                    holder.trd_clicks_top_ar.setText(item.getCard_clicks());
                    holder.trd_clicks_bottom_ar.setText(item.getCard_clicks());
                    if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                        holder.trd_custom_ar_heading.setVisibility(View.VISIBLE);
                        holder.trd_custom_ar_heading.setText(item.getCard_heading());
                       /* Picasso.with(context).load(Constants.CUSTOM_CARD_URL)
                                .into(holder.iv_card_counter);*/
                        holder.iv_card_counter.setVisibility(View.VISIBLE);
                        holder.iv_card_counter.setImageResource(R.drawable.c_custom_card);

                    }else{
                       // holder.tradeImage.setVisibility(View.GONE);
                        holder.trd_custom_ar_heading.setVisibility(View.GONE);
                        Picasso.with(context).load(item.getCard_url())
                                .into(holder.iv_card_counter);
                    }
                    holder.rl_acc_res_bg.setBackgroundResource(R.drawable.newbg);
                    holder.iv_acc_rec.setVisibility(View.VISIBLE);
                    holder.iv_acc_rec.setBackgroundResource(R.drawable.c_card_rejected);
                } else if (item.getCard_Accepted_Rejected().equalsIgnoreCase("countered")) {
                    holder.ll_cc_action.setVisibility(View.VISIBLE);
                    holder.cardViewCc.setVisibility(View.VISIBLE);
                    holder.ll_cc_action.setVisibility(View.GONE);
                    holder.cardView.setVisibility(View.GONE);
                    holder.cardViewCc.setGravity(Gravity.LEFT);
                    holder.tv_acc_res_name.setText("You made a");
                    holder.tv_acc_res_status.setText("COUNTER OFFER");
                    holder.trd_custom_heading.setVisibility(View.GONE);
                    holder.trd_clicks_top_ar.setText(item.getCard_clicks());
                    holder.trd_clicks_bottom_ar.setText(item.getCard_clicks());
                    if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                        holder.trd_custom_ar_heading.setVisibility(View.VISIBLE);
                        holder.trd_custom_ar_heading.setText(item.getCard_heading());
                        /*Picasso.with(context).load(Constants.CUSTOM_CARD_URL)
                                .into(holder.iv_card_counter);*/
                        holder.iv_card_counter.setVisibility(View.VISIBLE);
                        holder.iv_card_counter.setImageResource(R.drawable.c_custom_card);

                    }else{
                       // holder.tradeImage.setVisibility(View.GONE);
                        holder.trd_custom_ar_heading.setVisibility(View.GONE);
                        Picasso.with(context).load(item.getCard_url())
                                .into(holder.iv_card_counter);
                    }
                    holder.rl_acc_res_bg.setBackgroundResource(R.drawable.newbg);
                    holder.iv_acc_rec.setVisibility(View.GONE);

                }else if (item.getCard_Accepted_Rejected().equalsIgnoreCase("nil") && !item.getIs_CustomCard().equalsIgnoreCase("true")) {
                    holder.cardViewCards.setGravity(Gravity.LEFT);

                    Picasso.with(context).load(Utils.getCardURLForAndroid(item.getCard_url()))
                            .into(holder.tradeImage);

                    holder.trd_custom_heading.setVisibility(View.GONE);
                    holder.cardViewCards.setBackgroundResource(R.drawable.newbg);

                    try {
                        if (Integer.parseInt(item.getCard_clicks()) < 10) {
                            holder.trdClicksTop.setText("" + item.getCard_clicks());
                            holder.trdClicksBottom.setText("" + item.getCard_clicks());
                        } else {
                            holder.trdClicksTop.setText(item.getCard_clicks());
                            holder.trdClicksBottom.setText(item.getCard_clicks());
                        }

                        holder.card_partner_name.setText(item.getCardPartnerName().toUpperCase());

                    } catch (Exception e) {

                    }
                }else if (item.getIs_CustomCard().equalsIgnoreCase("true") && item.getCard_Accepted_Rejected().equalsIgnoreCase("nil")) {

                    Log.e(TAG, "" + "Type 55 -Custom Card SENDER");

                    holder.cardViewCards.setVisibility(View.VISIBLE);
                    holder.cardViewCards.setGravity(Gravity.LEFT);

                    /*Picasso.with(context).load(Constants.CUSTOM_CARD_URL)
                            .into(holder.tradeImage);*/
                   // holder.tradeImage.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.c_custom_card));//R.drawable.c_custom_card);


                    //holder.rl_card.setBackgroundResource(R.drawable.c_custom_card);
                    holder.tradeImage.setBackgroundResource(R.drawable.c_custom_card);

                    holder.trd_custom_heading.setVisibility(View.VISIBLE);
                    holder.cardViewCards.setBackgroundResource(R.drawable.newbg);
                    holder.trd_custom_heading.setText("" + item.getCard_heading());
                    try {
                        if (Integer.parseInt(item.getCard_clicks()) < 10) {
                            holder.trdClicksTop.setText("" + item.getCard_clicks());
                            holder.trdClicksBottom.setText("" + item.getCard_clicks());
                        } else {
                            holder.trdClicksTop.setText(item.getCard_clicks());
                            holder.trdClicksBottom.setText(item.getCard_clicks());
                        }

                        holder.card_partner_name.setText(item.getCardPartnerName().toUpperCase());

                    } catch (Exception e) {

                    }
                }
            }catch (Exception e){

                }
            }

        }
        holder.chatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(item.getChatType().equalsIgnoreCase("3")) {
                        try {
                            Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            File file = new File(item.getChatImageUrl());
                            intent.setDataAndType(Uri.fromFile(file), "audio/*");
                            context.startActivity(intent);
                            } catch (Exception e) {
                               Log.e(TAG,"Audio Playing Exception"+e.toString());
                            }

                }else if (item.getChatType().equalsIgnoreCase("4")) {

                }else if (item.getChatType().equalsIgnoreCase("6")) {


                }else{
                    Intent intent = new Intent(context, ImageViewer.class);
                    intent.putExtra("Url" ,item.getChatImageUrl());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }

            }
        });

         // Accept card
        holder.iv_accept_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (item.getChatType().equalsIgnoreCase("5")) {

                    Intent i=new Intent();
                    i.putExtra("FromCard",false);
                    i.putExtra("card_url",item.getCard_url());
                    i.putExtra("card_clicks",item.getCard_clicks());
                    i.putExtra("Title",item.getCard_heading());
                    i.putExtra("Discription",item.getCard_content());
                    i.putExtra("card_id",item.getCard_id());
                    i.putExtra("is_CustomCard",item.getIs_CustomCard());
                    i.putExtra("card_DB_ID",item.getCard_DB_ID());
                    i.putExtra("accepted_Rejected","accepted");
                    i.putExtra("played_Countered","ACCEPTED");
                    i.putExtra("card_originator",item.getCard_originator());

                    i.setClass(context,ChatRecordView.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setAction("Card");
                    context.startActivity(i);
                }
            }
        });
        // Resect card
        holder.iv_resect_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (item.getChatType().equalsIgnoreCase("5")) {
                    Intent i=new Intent();
                    i.putExtra("FromCard",false);
                    i.putExtra("card_url",item.getCard_url());
                    i.putExtra("card_clicks",item.getCard_clicks());
                    i.putExtra("Title",item.getCard_heading());
                    i.putExtra("Discription",item.getCard_content());
                    i.putExtra("card_id",item.getCard_id());
                    i.putExtra("is_CustomCard",item.getIs_CustomCard());
                    i.putExtra("card_DB_ID",item.getCard_DB_ID());
                    i.putExtra("accepted_Rejected","rejected");
                    i.putExtra("played_Countered","REJECTED!");
                    i.putExtra("card_originator",item.getCard_originator());
                    i.setClass(context,ChatRecordView.class);
                    i.setAction("Card");
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                }

            }
        });



        // Counter card
        holder.tv_counter_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (item.getChatType().equalsIgnoreCase("5")) {


                        Intent intent = null;
                        if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                            intent = new Intent(getContext(), ViewTradeCart.class);
                            // intent.putExtra("Url",  Constants.CUSTOM_CARD_URL);
                        }else{
                            intent = new Intent(getContext(), Card.class);
                            intent.putExtra("Url",  item.getCard_url());
                            //intent.putExtra("Url",  item.getCard_url());
                        }



                    intent.putExtra("ForCounter", true);
                    intent.putExtra("Title", item.getCard_heading());
                    intent.putExtra("Discription",  item.getCard_content());

                    intent.putExtra("card_id", item.getCard_id());
                    intent.putExtra("cardClicks",  item.getCard_clicks());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }
        });

        holder.iv_again_counter_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (item.getChatType().equalsIgnoreCase("5")) {
/*
                    Intent i = null;
                    if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                        i = new Intent(getContext(), ViewTradeCart.class);
                       // i.putExtra("Url",  Constants.CUSTOM_CARD_URL);
                    }else{
                        i = new Intent(getContext(), Card.class);
                        //i.putExtra("Url",  item.getCard_url());
                    }
                    i.putExtra("Url",  item.getCard_url());
                    i.putExtra("FromCard",false);
                    i.putExtra("card_clicks",item.getCard_clicks());
                    i.putExtra("Title",item.getCard_heading());
                    i.putExtra("Discription",item.getCard_content());
                    i.putExtra("card_id",item.getCard_id());
                    i.putExtra("is_CustomCard",item.getIs_CustomCard());
                    i.putExtra("card_DB_ID",item.getCard_DB_ID());
                    i.putExtra("accepted_Rejected","accepted");
                    i.putExtra("played_Countered","ACCEPTED");
                    i.putExtra("card_originator",item.getCard_originator());
                    i.setClass(context,ChatRecordView.class);
                    i.setAction("Card");
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);*/

                    Intent i=new Intent();
                    i.putExtra("FromCard",false);
                    i.putExtra("card_url",item.getCard_url());
                    i.putExtra("card_clicks",item.getCard_clicks());
                    i.putExtra("Title",item.getCard_heading());
                    i.putExtra("Discription",item.getCard_content());
                    i.putExtra("card_id",item.getCard_id());
                    i.putExtra("is_CustomCard",item.getIs_CustomCard());
                    i.putExtra("card_DB_ID",item.getCard_DB_ID());
                    i.putExtra("accepted_Rejected","accepted");
                    i.putExtra("played_Countered","ACCEPTED");
                    i.putExtra("card_originator",item.getCard_originator());
                    i.setClass(context,ChatRecordView.class);
                    i.setAction("Card");
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);

                }
            }
        });

        // Resect card
        holder.iv_again_counter_rej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (item.getChatType().equalsIgnoreCase("5")) {
                    Intent i=new Intent();
                    i.putExtra("FromCard",false);
                    i.putExtra("card_url",item.getCard_url());
                    i.putExtra("card_clicks",item.getCard_clicks());
                    i.putExtra("Title",item.getCard_heading());
                    i.putExtra("Discription",item.getCard_content());
                    i.putExtra("card_id",item.getCard_id());
                    i.putExtra("is_CustomCard",item.getIs_CustomCard());
                    i.putExtra("card_DB_ID",item.getCard_DB_ID());
                    i.putExtra("accepted_Rejected","rejected");
                    i.putExtra("played_Countered","REJECTED!");
                    i.putExtra("card_originator",item.getCard_originator());
                    i.setClass(context,ChatRecordView.class);
                    i.setAction("Card");
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                }
            }
        });

        holder.tv_counter_card_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (item.getChatType().equalsIgnoreCase("5")) {
                    Intent intent = null;
                    if(item.getIs_CustomCard().equalsIgnoreCase("true")){
                        intent = new Intent(getContext(), ViewTradeCart.class);
                       // intent.putExtra("Url",  Constants.CUSTOM_CARD_URL);
                    }else{
                        intent = new Intent(getContext(), Card.class);
                        //intent.putExtra("Url",  item.getCard_url());
                    }
                    intent.putExtra("Url",  item.getCard_url());
                    intent.putExtra("ForCounter", true);
                    intent.putExtra("Title", item.getCard_heading());
                    intent.putExtra("Discription",  item.getCard_content());
                    intent.putExtra("card_id", item.getCard_id());
                    intent.putExtra("cardClicks",  item.getCard_clicks());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }
        });


        holder.iv_type_two_share_icon_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                   /* Intent intent = new Intent(getContext(), ViewShare.class);
                    intent.putExtra("chat_id", item.getChatId());
                    intent.putExtra("accepted",  "no");
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);*/

            }
        });


        return row;
    }


    static class RecordHolder {

        RelativeLayout rrMainLayout, rlTimeStatusSender,chatParentLayout,rl_acc_res_bg,rl_card;
        LinearLayout llSrTime,clicksArea,cardView,cardAction,cardViewCc,cardViewCards,ll_cc_action;
        ImageView chatImage, shareIcon, sendStatus,clicksHeart,audioView,playIcon ,tradeImage,iv_accept_card,iv_resect_card,iv_acc_rec,iv_card_counter,iv_again_counter_acc,iv_again_counter_rej,iv_type_two_share_icon_r;
        TextView chatText, timeText,clicksText,trdClicksBottom,trdClicksTop,card_partner_name,tv_counter_card,tv_acc_res_name,tv_acc_res_status,trd_clicks_top_ar,trd_clicks_bottom_ar,tv_counter_card_action,trd_custom_heading,trd_custom_ar_heading;


    }



    public  String getCurrentTime(){
        DateFormat df = new SimpleDateFormat("hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }


}

