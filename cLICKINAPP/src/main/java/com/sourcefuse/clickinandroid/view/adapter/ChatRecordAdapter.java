package com.sourcefuse.clickinandroid.view.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.Card;
import com.sourcefuse.clickinandroid.view.ChatRecordView;
import com.sourcefuse.clickinandroid.view.MapView;
import com.sourcefuse.clickinandroid.view.ViewShare;
import com.sourcefuse.clickinandroid.view.ViewTradeCart;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatRecordAdapter extends ArrayAdapter<ChatMessageBody> {

    private static final String TAG = ChatRecordAdapter.class.getSimpleName();
    Context context;

    ArrayList<ChatMessageBody> currentChatList;
    private AuthManager authManager;
    private RelationManager relationManager;


    public ChatRecordAdapter(Context context, int layoutResourceId,
                             ArrayList<ChatMessageBody> chatList) {
        super(context, layoutResourceId, chatList);
        currentChatList = chatList;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMessageBody temp = currentChatList.get(position);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.view_chat_demo, parent, false);
        String oursQbId = ModelManager.getInstance().getAuthorizationManager().getQBId();
        RelativeLayout parentChatLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);
        relationManager = ModelManager.getInstance().getRelationManager();

        //monika- in case of chat history, we get clicks value null, so convert it to standard "no" value
        if (Utils.isEmptyString(temp.clicks))
            temp.clicks = "no";
        if (temp.senderQbId.equalsIgnoreCase(oursQbId)) { //start of sender


            android.util.Log.e(TAG, "getView" + "getView");//SENDER


            LinearLayout parent_shared_layout = (LinearLayout) row.findViewById(R.id.parent_shared_layout);

            parent_shared_layout.setBackgroundResource(R.drawable.newbg_grey);

            RelativeLayout rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);

            LinearLayout chatClickTextLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area);


            //code to set time
            TextView timeView = (TextView) row.findViewById(R.id.tv_time_text);
            timeView.setText(Utils.getLocalDatefromTimestamp(Long.parseLong(temp.sentOn)));

            //code -for image -sender-start here
            if (!(Utils.isEmptyString(temp.imageRatio))) {
                //set layout properties for image view
                /* layout params for image */

                RelativeLayout.LayoutParams mImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout mImageLayout = (LinearLayout) row.findViewById(R.id.media_layout);
                ImageView image_attached = (ImageView) row.findViewById(R.id.iv_chat_image);
                if (!Utils.isEmptyString(temp.textMsg) && !temp.clicks.equalsIgnoreCase("no")) // case for image with click
                {
                    mImageParams.setMargins(5, 2, 5, 7);
                    mImageLayout.setLayoutParams(mImageParams);
                    image_attached.setPadding(5, 0, 0, 0);
                } else {
                    // case without text and without click
                    mImageParams.setMargins(5, 4, 5, 11);
                    mImageLayout.setLayoutParams(mImageParams);
                }


                LinearLayout media_layout = (LinearLayout) row.findViewById(R.id.media_layout);

                media_layout.setVisibility(View.VISIBLE);


                image_attached.setVisibility(View.VISIBLE);

                if (!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))) {
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsrr.addRule(RelativeLayout.BELOW, R.id.media_layout);
                    chatClickTextLayout.setLayoutParams(paramsrr);
                }
                //code to set msg deilvery notification
                ImageView sendStatusView = (ImageView) row.findViewById(R.id.iv_send_status);
                if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
                    sendStatusView.setImageResource(R.drawable.r_single_tick);
                    Bitmap bitmap = null;
                    if (!temp.content_url.contains("http")) {
                        bitmap = Utils.path(Uri.parse(temp.content_url));
                    }
                    if (bitmap != null) {
                        image_attached.setImageBitmap(bitmap);
                    } else {
                        Picasso.with(context).load(temp.content_url)
                                .into(image_attached);
                    }
                } else if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.GONE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.VISIBLE);
                    sendStatusView.setImageResource(R.drawable.double_check);
                    Picasso.with(context).load(temp.content_url).resize(300, 300).centerCrop().into(image_attached);
                }



                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                image_attached.setVisibility(View.VISIBLE);
  /* for map to set text location shared */
                if (!(Utils.isEmptyString(temp.location_coordinates))) {
                    TextView mLongTextView = (TextView) row.findViewById(R.id.long_chat_text);
                    mLongTextView.setVisibility(View.VISIBLE);
                    mLongTextView.setTextColor(context.getResources().getColor(R.color.black));
                    mLongTextView.setText("Location Shared");
                    mLongTextView.setPadding(10,10,10,30);
                    /*LinearLayout.LayoutParams mTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    mTextParams.setMargins(0, 10, 0, 0);
                    mLongTextView.setLayoutParams(mTextParams);*/
                }

                /* for map to set text location shared */


            } else if (!Utils.isEmptyString(temp.content_url) && Utils.isEmptyString(temp.video_thumb)) {// //end of image loop-sender And Audio start

                parentChatLayout.setBackgroundResource(R.color.transparent);
                parent_shared_layout.setBackgroundResource(R.drawable.newbg_grey);

                ImageView mAudioImage = (ImageView) row.findViewById(R.id.iv_play_btn);
                mAudioImage.setVisibility(View.VISIBLE);

                row.findViewById(R.id.temp_layout).setVisibility(View.VISIBLE);

                ImageView mSpeakerImage = (ImageView) row.findViewById(R.id.iv_play_btn_);
                mSpeakerImage.setVisibility(View.VISIBLE);

                TextView mLongTextView = (TextView) row.findViewById(R.id.long_chat_text);
                mLongTextView.setEms(11);


                if (!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))) {
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsrr.addRule(RelativeLayout.BELOW, R.id.temp_layout);
                    chatClickTextLayout.setLayoutParams(paramsrr);
                    if ((temp.clicks.equalsIgnoreCase("no")))
                        row.findViewById(R.id.temp_).setVisibility(View.VISIBLE);

                } else {
                    RelativeLayout mRelative = (RelativeLayout) row.findViewById(R.id.temp_layout);
                    mRelative.setBackgroundResource(R.color.transparent);
                }


                //code to set msg deilvery notificatio
                ImageView sendStatusView = (ImageView) row.findViewById(R.id.iv_send_status);
                if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);

                    sendStatusView.setImageResource(R.drawable.r_single_tick);


                } else if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.GONE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.VISIBLE);
                    sendStatusView.setImageResource(R.drawable.double_check);

                }
            } else if (!Utils.isEmptyString(temp.video_thumb)) {//end of audio code sender START VIDEO VIEW FOR SENDER

                ImageView sendStatusView = (ImageView) row.findViewById(R.id.iv_send_status);
                ImageView image_attached = (ImageView) row.findViewById(R.id.iv_chat_image);
                ImageView play_buttom = (ImageView) row.findViewById(R.id.iv_play_btn);
                play_buttom.setVisibility(View.VISIBLE);

                image_attached.setPadding(5, 5, 5, 10);
                if (!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))) {
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsrr.addRule(RelativeLayout.BELOW, R.id.media_layout);
                    chatClickTextLayout.setLayoutParams(paramsrr);
                }

                /* log for video */


                RelativeLayout mRelative = (RelativeLayout) row.findViewById(R.id.temp_layout);
                mRelative.setVisibility(View.VISIBLE);
                mRelative.setBackgroundResource(R.color.transparent);
                RelativeLayout.LayoutParams mlayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mRelative.setLayoutParams(mlayoutParams);


                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 0);
                play_buttom.setLayoutParams(layoutParams);
                /* log for video */


                if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {
                    //android.util.android.util.Log.e("in one video", "in one video");
                    row.findViewById(R.id.pb_loding).setVisibility(View.VISIBLE);
                    row.findViewById(R.id.iv_type_two_share_icon_r).setVisibility(View.GONE);
                    sendStatusView.setImageResource(R.drawable.r_single_tick);


/* case when thumb is not uploaded form server prafull*/
                    Bitmap bitmap = null;
                    if (!temp.video_thumb.contains("http")) {
                        bitmap = Utils.path(Uri.parse(temp.video_thumb));
                    }
                    if (bitmap != null) {
                        image_attached.setImageBitmap(bitmap);
                    } else {
                        Picasso.with(context).load(temp.video_thumb)
                                .into(image_attached);
                    }

                } else if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)) {

                    // android.util.android.util.Log.e("in two video", "in two video");

                    row.findViewById(R.id.pb_loding).setVisibility(View.GONE);
                    row.findViewById(R.id.iv_type_two_share_icon_r).setVisibility(View.VISIBLE);
                    sendStatusView.setImageResource(R.drawable.double_check);
                    /* case when text is uploaded */
                    Picasso.with(context).load(temp.video_thumb)
                            .into(image_attached);
                }



                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                image_attached.setVisibility(View.VISIBLE);
            } else if (!Utils.isEmptyString(temp.card_id)) {// //end of video loop-sender And Card start
                //  parentChatLayout.setBackgroundResource(R.drawable.newbg);
                //share should not be present for first time case
                ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);
                //check for status of card played basis on accept reject value
                if (temp.card_Accepted_Rejected.equalsIgnoreCase("nil")) { //first time played
                    ((RelativeLayout) row.findViewById(R.id.send_card_first_time)).setVisibility(View.VISIBLE);
                    String clicks = temp.clicks;
                    if (clicks.equalsIgnoreCase("5"))
                        clicks = "05";
                    ((TextView) row.findViewById(R.id.trd_clicks_top)).setVisibility(View.VISIBLE);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom)).setVisibility(View.VISIBLE);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_top)).setText(clicks);
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom)).setText(clicks);


                    ImageView trade_image = (ImageView) row.findViewById(R.id.trade_image);
                    trade_image.setVisibility(View.VISIBLE);
                    // temp.is_CustomCard=Boolean.getBoolean(temp.is_CustomCard);
                    if (temp.is_CustomCard) {
                        TextView custom_heading = (TextView) row.findViewById(R.id.trd_custom_heading);
                        custom_heading.setVisibility(View.VISIBLE);
                        custom_heading.setText(temp.card_heading);
                        trade_image.setImageResource(R.drawable.tradecardpink_big);//Akshit code
                    } else {
                        try {
                            String url_to_load = (temp.card_url).replaceFirst("cards\\/(\\d+)\\.jpg", "cards\\/a\\/1080\\/$1\\.jpg");
                            Picasso.with(context).load(url_to_load)
                                    .into(trade_image);
                            android.util.Log.e("URL FOR CARD", "URL>>>>>>>>>>>>>" + url_to_load);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("accepted")) {//enf of first time played card
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);

                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText("You");
                    ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("ACCEPTED!");

                    ImageView acc_rej_view = ((ImageView) row.findViewById(R.id.iv_acc_rec));
                    acc_rej_view.setVisibility(View.VISIBLE);
                    acc_rej_view.setImageResource(R.drawable.c_card_accept);

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("rejected")) {//enf of accept card
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);

                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText("You");
                    ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("REJECTED!");

                    ImageView acc_rej_view = ((ImageView) row.findViewById(R.id.iv_acc_rec));
                    acc_rej_view.setVisibility(View.VISIBLE);
                    acc_rej_view.setImageResource(R.drawable.c_card_rejected);

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("countered")) {//end of reject card
                    //share should not be present for countered case
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);
                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText("You" + " made a");
                    ((LinearLayout) row.findViewById(R.id.ll_cc_action)).setVisibility(View.GONE);
                    // ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("COUNTER OFFER");

                    // listeners for countered actions- not required at sender side
                 /*   ((ImageView) row.findViewById(R.id.iv_again_counter_rej)).setTag(position);
                    ((ImageView) row.findViewById(R.id.iv_again_counter_rej)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card Reject Action`
                            int position = (Integer) v.getTag();
                            sendUpdateCardValues(position, "rejected", "REJECTED!");

                        }
                    });

                    ((ImageView) row.findViewById(R.id.iv_again_counter_acc)).setTag(position);
                    ((ImageView) row.findViewById(R.id.iv_again_counter_acc)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card ACCEPT Action
                            int position = (Integer) v.getTag();
                            sendUpdateCardValues(position, "accepted", "ACCEPTED!");


                        }
                    });
                    ((ImageView) row.findViewById(R.id.tv_counter_card_action)).setTag(position);
                    ((ImageView) row.findViewById(R.id.tv_counter_card_action)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card countered Action
                            int position = (Integer) v.getTag();
                            sendUpdateCardValues(position, "countered", "COUNTERED CARD!");


                        }
                    }); */

                }//end of countered card
                //common for accept. reject and countered card-display card from local and set values
                if (!Utils.isEmptyString(temp.card_id) && !temp.card_Accepted_Rejected.equalsIgnoreCase("nil")) {//end of reject card
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    String clicks = temp.clicks;
                    if (clicks.equalsIgnoreCase("5"))
                        clicks = "05";
                    ((TextView) row.findViewById(R.id.trd_clicks_top_ar)).setVisibility(View.VISIBLE);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom_ar)).setVisibility(View.VISIBLE);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_top_ar)).setText(clicks);
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom_ar)).setText(clicks);
                    if (temp.is_CustomCard) {
                        ((ImageView) row.findViewById(R.id.iv_static_acc_rej_card)).setImageResource(R.drawable.c_pinknew_chat);//akshit code
                        TextView customHeading = ((TextView) row.findViewById(R.id.tv_custom_card_heading_acc_rej));
                        customHeading.setVisibility(View.VISIBLE);
                        customHeading.setText(temp.card_heading);
                        ((TextView) row.findViewById(R.id.tv_card_heading_acc_rej)).setVisibility(View.GONE);
                        ((TextView) row.findViewById(R.id.tv_card_des_acc_rej)).setVisibility(View.GONE);
                    } else {
                        ((ImageView) row.findViewById(R.id.iv_static_acc_rej_card)).setImageResource(R.drawable.tradecardbg_blank);//Akshit Code
                        ((TextView) row.findViewById(R.id.tv_card_heading_acc_rej)).setText(temp.card_heading);
                        ((TextView) row.findViewById(R.id.tv_card_des_acc_rej)).setText(temp.card_content);
                    }
                }//end of common code for card
            }

            //only text-SENDER CASE
            if (!Utils.isEmptyString(temp.textMsg) && temp.clicks.equalsIgnoreCase("no")) {
                //  RelativeLayout textViewLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);
                //code to hide share icon for text messages-monika
                if (Utils.isEmptyString(temp.content_url))
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);

                chatClickTextLayout.setVisibility(View.VISIBLE);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);
                TextView chatText = (TextView) row.findViewById(R.id.long_chat_text); // prafull code
                chatText.setVisibility(View.VISIBLE);
                chatText.setTextColor(context.getResources().getColor(R.color.black));
                if (!Utils.isEmptyString(temp.sharingMedia)) { // for share case-praful
                    LinearLayout mShareLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area_share);
                    mShareLayout.setBackgroundResource(R.color.white);
                    mShareLayout.setVisibility(View.VISIBLE);
                    TextView mShareText = (TextView) row.findViewById(R.id.long_chat_text_share);
                    mShareText.setVisibility(View.VISIBLE);
                    mShareText.setText("" + temp.textMsg);
                    mShareText.setTextColor(context.getResources().getColor(R.color.black));
//                    RelativeLayout.LayoutParams mTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    mTextParams.setMargins(0, 0, 0, 100);
//                    mShareLayout.setLayoutParams(mTextParams);
                    // mShareLayout.setPadding(0,0,0,50);
                } else {
                    LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                    clicksArea.setVisibility(View.VISIBLE);
                    TextView chatText = (TextView) row.findViewById(R.id.long_chat_text); // prafull code
                    parent_shared_layout.setBackgroundResource(R.drawable.newbg_grey);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.setTextColor(context.getResources().getColor(R.color.black));
                    chatText.setBackgroundResource(R.drawable.grey_square);
                    chatText.setText("" + temp.textMsg);
                    LinearLayout.LayoutParams mTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    mTextParams.setMargins(5, 5, 5, 5);
                    chatText.setLayoutParams(mTextParams);


                }


            }


            //CLICKS AND TEXT- SENDER CASE
            if (!(temp.clicks.equalsIgnoreCase("no")) && Utils.isEmptyString(temp.card_id) && Utils.isEmptyString(temp.sharingMedia)) {
                chatClickTextLayout.setVisibility(View.VISIBLE);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);
                TextView clicksText = (TextView) row.findViewById(R.id.clicks_text);
                clicksText.setVisibility(View.VISIBLE);
                clicksText.setText(temp.clicks);
                clicksText.setTextColor(context.getResources().getColor(R.color.white));
                ImageView clicksHeart = (ImageView) row.findViewById(R.id.iv_clicks_heart);
                clicksHeart.setVisibility(View.VISIBLE);

                LinearLayout mParentClickArea = (LinearLayout) row.findViewById(R.id.parent_clicks_area);
                mParentClickArea.setPadding(15,0,0,0);

                //check if only clicks is there
                parent_shared_layout.setBackgroundResource(R.drawable.newbg_pinkleft);
                if (!(Utils.isEmptyString(temp.textMsg))) {
                    TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                    chatText.setSingleLine(true);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.setTextColor(context.getResources().getColor(R.color.white));
                    if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                        chatText.setText(temp.textMsg.substring(0, 13));
                        TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                        chatTextLong.setVisibility(View.VISIBLE);
                        chatTextLong.setText(temp.textMsg.substring(13));
                        chatTextLong.setTextColor(context.getResources().getColor(R.color.white));
                        parent_shared_layout.setBackgroundResource(R.drawable.newbg_pinkleft);  // code for double line
                    } else {
                        if ((Utils.isEmptyString(temp.content_url))) {  // check prafull
                            clicksArea.setBackgroundResource(R.drawable.pink_squre);
                        } else {
                            parent_shared_layout.setBackgroundResource(R.drawable.newbg_pinkleft);  // code for double line
                            clicksArea.setBackgroundResource(R.drawable.pink_squre);
                            LinearLayout image_attached = (LinearLayout) row.findViewById(R.id.media_layout);
                            RelativeLayout.LayoutParams mLinearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            mLinearParams.setMargins(0, 1, 5, 5);
                            image_attached.setLayoutParams(mLinearParams);
                        }
                        chatText.setText(temp.textMsg);
                        // prafull code
                    }


                } else { // in case only click
                    clicksArea.setBackgroundResource(R.drawable.pink_squre);
                }
                //clicks and text- sharing media-sender case
            } else if (!(temp.clicks.equalsIgnoreCase("no")) && Utils.isEmptyString(temp.card_id) && !Utils.isEmptyString(temp.sharingMedia)) { // for share case

                LinearLayout mParentShareLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area_share);
                mParentShareLayout.setVisibility(View.VISIBLE);
                parent_shared_layout.setBackgroundResource(R.drawable.newbg);
                parent_shared_layout.setVisibility(View.VISIBLE);

                LinearLayout mClickArea = (LinearLayout) row.findViewById(R.id.clicks_area_share);
                mClickArea.setVisibility(View.VISIBLE);
                TextView mShareText = (TextView) row.findViewById(R.id.clicks_text_share);
                TextView mShareSort = (TextView) row.findViewById(R.id.chat_text_share);
                TextView mShareLong = (TextView) row.findViewById(R.id.long_chat_text_share);
                ImageView mHertImage = (ImageView) row.findViewById(R.id.iv_clicks_heart_share);
                mHertImage.setVisibility(View.VISIBLE);
                mShareText.setText(temp.clicks);
                mShareText.setVisibility(View.VISIBLE);
                if (!(Utils.isEmptyString(temp.textMsg))) {


                    if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                        mShareSort.setText(temp.textMsg.substring(0, 12));
                        mShareSort.setVisibility(View.VISIBLE);
                        mShareLong.setText(temp.textMsg.substring(13));//akshit changes
                        mShareLong.setTextColor(context.getResources().getColor(R.color.white));
                        mShareLong.setVisibility(View.VISIBLE);

                    } else {
                        mShareSort.setText(temp.textMsg);
                        mShareSort.setVisibility(View.VISIBLE);
                    }
                }


            }//end of clicks and text-sharing media-sender case


            if (!Utils.isEmptyString(temp.sharingMedia)) {

                android.util.Log.e("in share media-->", "in share media-->");
                if (temp.shareStatus.equalsIgnoreCase("shareAccepted")) { // set tip iamge for share accepted
                    parent_shared_layout.setBackgroundResource(R.drawable.sharedleft);
                    row.findViewById(R.id.parent_clicks_area).setVisibility(View.GONE);
                    row.findViewById(R.id.parent_clicks_area_share).setVisibility(View.GONE);
                    ((ImageView) row.findViewById(R.id.iv_chat_image)).setVisibility(View.GONE);
                } else if (temp.shareStatus.equalsIgnoreCase("shareRejected")) {//// set tip iamge for share Rejected
                    parent_shared_layout.setBackgroundResource(R.drawable.shareddeniedleft);
                    ((ImageView) row.findViewById(R.id.iv_chat_image)).setVisibility(View.GONE);
                } else if (temp.shareStatus.equalsIgnoreCase("shared")) {
                    ((RelativeLayout) row.findViewById(R.id.shared_header_view)).setVisibility(View.VISIBLE);
                    ((TextView) row.findViewById(R.id.shared_by_name)).setText("You");
                    ((TextView) row.findViewById(R.id.shared_message)).setText(" want to share");


                    /* for share prafull code   */
                }
                ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
            }//end of share view at Sender side


        }//end of sender loop
        else {


            //RECEIVER START

            LinearLayout parent_shared_layout = (LinearLayout) row.findViewById(R.id.parent_shared_layout);
            parent_shared_layout.setBackgroundResource(R.drawable.whitechatbg);


            RelativeLayout rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);
            row.findViewById(R.id.iv_send_status).setVisibility(View.GONE);
            RelativeLayout chatParentLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);


            LinearLayout chatClickTextLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area);

            RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsrr.addRule(RelativeLayout.LEFT_OF, R.id.parent_shared_layout);
            paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.parent_shared_layout);
            rlTimeStatusSender.setLayoutParams(paramsrr);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            params.setMargins(0, 0, 10, 0);
            parent_shared_layout.setLayoutParams(params);
            parent_shared_layout.setPadding(0, 0, 10, 0);


            // chatParentLayout.setGravity(Gravity.RIGHT);

            //code to set time
            TextView timeView = (TextView) row.findViewById(R.id.tv_time_text);
            timeView.setText(Utils.getLocalDatefromTimestamp(Long.parseLong(temp.sentOn)));

            //temp code -for image-receiver end
            if (!(Utils.isEmptyString(temp.imageRatio))) {   // image case for reciver end
                //set layout properties for image view
                LinearLayout media_layout = (LinearLayout) row.findViewById(R.id.media_layout);
                ImageView image_attached = (ImageView) row.findViewById(R.id.iv_chat_image);
                media_layout.setVisibility(View.VISIBLE);
                image_attached.setVisibility(View.VISIBLE);


                RelativeLayout.LayoutParams mImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout mImageLayout = (LinearLayout) row.findViewById(R.id.media_layout);

                if (!Utils.isEmptyString(temp.textMsg) && !temp.clicks.equalsIgnoreCase("no")) // case for image with click
                {
                    android.util.Log.e("case 1 --->", "case 1 --->");
                    mImageParams.setMargins(15, 2, 5, 7);
                    mImageLayout.setLayoutParams(mImageParams);
                    image_attached.setPadding(5, 0, 0, 0);
                } else if (!temp.clicks.equalsIgnoreCase("no")) //text without click
                {
                    android.util.Log.e("case 2 --->", "case 2 --->");
                    mImageParams.setMargins(5, 4, 5, 11);
                    mImageLayout.setLayoutParams(mImageParams);
                } else {
                    android.util.Log.e("case 3 --->", "case 3 --->");
                    // case without text and without click
                    mImageParams.setMargins(7, 4, 25, 11);
                    mImageLayout.setLayoutParams(mImageParams);
                }


                if (!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))) {
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams paramsr2 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsr2.addRule(RelativeLayout.BELOW, R.id.media_layout);
                    chatClickTextLayout.setLayoutParams(paramsr2);
                }


                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                image_attached.setVisibility(View.VISIBLE);
                Picasso.with(context).load(temp.content_url)
                        .into(image_attached);

                 /* for map to set text location shared */


                if (!(Utils.isEmptyString(temp.location_coordinates))) {

                    TextView mLongTextView = (TextView) row.findViewById(R.id.long_chat_text);
                    mLongTextView.setVisibility(View.VISIBLE);
                    mLongTextView.setTextColor(context.getResources().getColor(R.color.black));
                    mLongTextView.setText("Location Shared");
                    mLongTextView.setPadding(10,10,10,30);
                    /*LinearLayout.LayoutParams mTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    mTextParams.setMargins(0, 10, 0, 0);
                    mLongTextView.setLayoutParams(mTextParams);*/
                }

               /* for map to set text location shared */

            } else if (!Utils.isEmptyString(temp.content_url) && Utils.isEmptyString(temp.video_thumb)) {//start of audio-RECIVER case


                ImageView mAudioImage = (ImageView) row.findViewById(R.id.iv_play_btn);
                mAudioImage.setVisibility(View.VISIBLE);

                row.findViewById(R.id.temp_).setVisibility(View.GONE);
                TextView mLongTextView = (TextView) row.findViewById(R.id.long_chat_text);
                mLongTextView.setEms(11);

                if (Utils.isEmptyString(temp.textMsg) && temp.clicks.equalsIgnoreCase("no")) {
                    parent_shared_layout.setPadding(10, 5, 30, 5); // set padding
                    android.util.Log.e("in audio case 1", "in audio case 1");
                } else {
                    parent_shared_layout.setPadding(50, 5, 30, 5); // set padding
                    android.util.Log.e("in audio case 2", "in audio case 2");
                }

                row.findViewById(R.id.temp_layout).setVisibility(View.VISIBLE);

                ImageView mSpeakerImage = (ImageView) row.findViewById(R.id.iv_play_btn_);
                mSpeakerImage.setVisibility(View.VISIBLE);


                if (!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))) {
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams paramsrr2 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsrr2.addRule(RelativeLayout.BELOW, R.id.temp_layout);
                    chatClickTextLayout.setLayoutParams(paramsrr2);
                }

            } else if (!Utils.isEmptyString(temp.video_thumb)) {//end of audio code sender START VIDEO VIEW FOR RECEIVER

                ImageView sendStatusView = (ImageView) row.findViewById(R.id.iv_send_status);
                ImageView image_attached = (ImageView) row.findViewById(R.id.iv_chat_image);
                ImageView play_buttom = (ImageView) row.findViewById(R.id.iv_play_btn);
                play_buttom.setVisibility(View.VISIBLE);

                if (Utils.isEmptyString(temp.textMsg))
                    image_attached.setPadding(10, 8, 28, 10);
                else
                    image_attached.setPadding(10, 5, 5, 10);

                if (!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))) {
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams paramsr2 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsr2.addRule(RelativeLayout.BELOW, R.id.media_layout);
                    chatClickTextLayout.setLayoutParams(paramsr2);
                }

                /* prafull code */


                RelativeLayout mRelative = (RelativeLayout) row.findViewById(R.id.temp_layout);
                mRelative.setVisibility(View.VISIBLE);
                mRelative.setBackgroundResource(R.color.transparent);
                RelativeLayout.LayoutParams mlayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mRelative.setLayoutParams(mlayoutParams);


                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 0);
                play_buttom.setLayoutParams(layoutParams);

                /* end */


                /* log for video */


                //android.util.android.util.Log.e("temp thumb for video", "" + temp.video_thumb);


                /* log for video */


                if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {

                    //android.util.android.util.Log.e("in one video", "in one video");

                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
                    sendStatusView.setImageResource(R.drawable.r_single_tick);

                } else if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)) {
                    row.findViewById(R.id.pb_loding).setVisibility(View.GONE);
                    row.findViewById(R.id.iv_type_two_share_icon_r).setVisibility(View.VISIBLE);
                    sendStatusView.setImageResource(R.drawable.double_check);
                    //android.util.android.util.Log.e("in two video", "in two video");
                }

                Picasso.with(context).load(temp.video_thumb)
                        .into(image_attached);
                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                image_attached.setVisibility(View.VISIBLE);
            } else if (!Utils.isEmptyString(temp.card_id)) {//end of video- start of card first time-receiver end
                //Card RECEIVE at 1st Time
                //share should not be present for first time case
                ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);
                if (temp.card_Accepted_Rejected.equalsIgnoreCase("nil")) { //first time played
                    // parentChatLayout.setBackgroundResource(R.drawable.whitechatbg);
                    ((RelativeLayout) row.findViewById(R.id.send_card_first_time)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.card_recieve_first_time)).setVisibility(View.VISIBLE);
                    String clicks = temp.clicks;
                    if (clicks.equalsIgnoreCase("5"))
                        clicks = "05";
                    ((TextView) row.findViewById(R.id.trd_clicks_top)).setVisibility(View.VISIBLE);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom)).setVisibility(View.VISIBLE);//akshit code

                    ((TextView) row.findViewById(R.id.trd_clicks_bottom)).setPadding(0, 13, 7, 13);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_top)).setPadding(0, 13, 0, 0);//akshit code

                    ((TextView) row.findViewById(R.id.trd_clicks_top)).setText(clicks);
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom)).setText(clicks);

                    String[] splitted = relationManager.getPartnerName.split("\\s+");
                    ((TextView) row.findViewById(R.id.card_partner_name)).setText(splitted[0]);

                    ImageView trade_image = (ImageView) row.findViewById(R.id.trade_image);
                    trade_image.setVisibility(View.VISIBLE);
                    //akshit code
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(14, 0, 6, 0);
                    trade_image.setLayoutParams(layoutParams);
                    //End
                    if (temp.is_CustomCard) {
                        TextView custom_heading = (TextView) row.findViewById(R.id.trd_custom_heading);
                        custom_heading.setVisibility(View.VISIBLE);
                        custom_heading.setText(temp.card_heading);
                        trade_image.setImageResource(R.drawable.tradecardpink_big);//Akshit code
                    } else {
                        String url_to_load = (temp.card_url).replaceFirst("cards\\/(\\d+)\\.jpg", "cards\\/a\\/1080\\/$1\\.jpg");
                        Picasso.with(context).load(url_to_load)
                                .into(trade_image);
                    }

                    ((ImageView) row.findViewById(R.id.iv_reject_card)).setTag(position);
                    ((ImageView) row.findViewById(R.id.iv_reject_card)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card Reject Action`
                            int position = (Integer) v.getTag();
                            //card is allowed to play once only- reject, accept or counter change state to played
                            if (temp.card_Played_Countered.equalsIgnoreCase("playing")) {
                                sendUpdateCardValues(position, "rejected", "REJECTED!");
                                temp.card_Played_Countered = "played";
                            }

                        }
                    });

                    ((ImageView) row.findViewById(R.id.iv_accept_card)).setTag(position);
                    ((ImageView) row.findViewById(R.id.iv_accept_card)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card ACCEPT Action
                            int position = (Integer) v.getTag();
                            //card is allowed to play once only- reject, accept or counter change state to played
                            if (temp.card_Played_Countered.equalsIgnoreCase("playing")) {
                                sendUpdateCardValues(position, "accepted", "ACCEPTED!");
                                temp.card_Played_Countered = "played";
                            }


                        }
                    });
                    ((ImageView) row.findViewById(R.id.tv_counter_card)).setTag(position);
                    ((ImageView) row.findViewById(R.id.tv_counter_card)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card countered Action
                            int position = (Integer) v.getTag();
                            //card is allowed to play once only- reject, accept or counter change state to played
                            if (temp.card_Played_Countered.equalsIgnoreCase("playing")) {
                                sendUpdateCardValues(position, "countered", "COUNTERED CARD!");
                                temp.card_Played_Countered = "played";
                            }

                        }
                    });
                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("accepted")) {//enf of first time played card-receiver
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);
                    String[] splitted = relationManager.getPartnerName.split("\\s+");
                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText(splitted[0]);
                    ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("ACCEPTED!");

                    ImageView acc_rej_view = ((ImageView) row.findViewById(R.id.iv_acc_rec));
                    acc_rej_view.setVisibility(View.VISIBLE);
                    acc_rej_view.setImageResource(R.drawable.c_card_accept);

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("rejected")) {//end of accept card-receiver
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);
                    String[] splitted = relationManager.getPartnerName.split("\\s+");
                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText(splitted[0]);
                    ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("REJECTED!");

                    ImageView acc_rej_view = ((ImageView) row.findViewById(R.id.iv_acc_rec));
                    acc_rej_view.setVisibility(View.VISIBLE);
                    acc_rej_view.setImageResource(R.drawable.c_card_rejected);

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("countered")) {//end of reject card-receiver end
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);
                    //share should not be present for counter case
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);

                    String[] splitted = relationManager.getPartnerName.split("\\s+");
                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText(splitted[0] + " made a");
                    //   ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("countered offer");
                    ((LinearLayout) row.findViewById(R.id.ll_cc_action)).setVisibility(View.VISIBLE);


                    //set listeners for countered actions
                    ((ImageView) row.findViewById(R.id.iv_again_counter_rej)).setTag(position);
                    ((ImageView) row.findViewById(R.id.iv_again_counter_rej)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card Reject Action`
                            int position = (Integer) v.getTag();
                            //card is allowed to play once only- reject, accept or counter change state to played
                            if (temp.card_Played_Countered.equalsIgnoreCase("playing")) {
                                sendUpdateCardValues(position, "rejected", "REJECTED!");
                                temp.card_Played_Countered = "played";
                            }
                        }
                    });

                    ((ImageView) row.findViewById(R.id.iv_again_counter_acc)).setTag(position);
                    ((ImageView) row.findViewById(R.id.iv_again_counter_acc)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card ACCEPT Action
                            int position = (Integer) v.getTag();
                            //card is allowed to play once only- reject, accept or counter change state to played
                            if (temp.card_Played_Countered.equalsIgnoreCase("playing")) {
                                sendUpdateCardValues(position, "accepted", "ACCEPTED!");
                                temp.card_Played_Countered = "played";
                            }
                        }
                    });
                    ((ImageView) row.findViewById(R.id.tv_counter_card_action)).setTag(position);
                    ((ImageView) row.findViewById(R.id.tv_counter_card_action)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card countered Action
                            int position = (Integer) v.getTag();
                            //card is allowed to play once only- reject, accept or counter change state to played
                            if (temp.card_Played_Countered.equalsIgnoreCase("playing")) {

                                sendUpdateCardValues(position, "countered", "COUNTERED CARD!");
                                temp.card_Played_Countered = "played";
                            }
                        }
                    });

                }//end of countered card
                //common for accept. reject and countered card-display card from local and set values
                if (!Utils.isEmptyString(temp.card_id) && !temp.card_Accepted_Rejected.equalsIgnoreCase("nil")) {//end of reject card
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    String clicks = temp.clicks;
                    if (clicks.equalsIgnoreCase("5"))
                        clicks = "05";
                    ((TextView) row.findViewById(R.id.trd_clicks_top_ar)).setVisibility(View.VISIBLE);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom_ar)).setVisibility(View.VISIBLE);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_top_ar)).setText(clicks);
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom_ar)).setText(clicks);

                    if (temp.is_CustomCard) {
                        ((ImageView) row.findViewById(R.id.iv_static_acc_rej_card)).setImageResource(R.drawable.c_pinknew_chat);//Akshit code
                        TextView customHeading = ((TextView) row.findViewById(R.id.tv_custom_card_heading_acc_rej));
                        customHeading.setVisibility(View.VISIBLE);
                        customHeading.setText(temp.card_heading);
                        ((TextView) row.findViewById(R.id.tv_card_heading_acc_rej)).setVisibility(View.GONE);
                        ((TextView) row.findViewById(R.id.tv_card_des_acc_rej)).setVisibility(View.GONE);
                    } else {
                        ((ImageView) row.findViewById(R.id.iv_static_acc_rej_card)).setImageResource(R.drawable.tradecardbg_blank);//akshit code
                        ((TextView) row.findViewById(R.id.tv_card_heading_acc_rej)).setText(temp.card_heading);
                        ((TextView) row.findViewById(R.id.tv_card_des_acc_rej)).setText(temp.card_content);
                    }
                }//end of common code for card
            }


            
            /* only text reciver case*/
            if ((!Utils.isEmptyString(temp.textMsg) && temp.clicks.equalsIgnoreCase("no"))) {
                //code to hide share icon for text messages-monika
                if (Utils.isEmptyString(temp.content_url))
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);
                chatClickTextLayout.setVisibility(View.VISIBLE);
                TextView chatText = (TextView) row.findViewById(R.id.long_chat_text); // prafull code
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);
                chatText.setVisibility(View.VISIBLE);
                chatText.setTextColor(context.getResources().getColor(R.color.black));
                if (!Utils.isEmptyString(temp.sharingMedia)) { // for share case
                    LinearLayout mShareLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area_share);
                    mShareLayout.setBackgroundResource(R.color.white);
                    mShareLayout.setVisibility(View.VISIBLE);
                    TextView mShareText = (TextView) row.findViewById(R.id.long_chat_text_share);
                    mShareText.setVisibility(View.VISIBLE);
                    mShareText.setText("" + temp.textMsg);
                    mShareText.setTextColor(context.getResources().getColor(R.color.black));
                } else {

                    /* for layout params at reciver end prafull code*/

                    parent_shared_layout.setBackgroundResource(R.drawable.new_whitearrownew);
                    chatText.setBackgroundResource(R.drawable.white_square);

                    if (Utils.isEmptyString(temp.content_url))
                        chatText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                    if (Utils.isEmptyString(temp.content_url))
                        chatText.setPadding(15, 10, 28, 10);
                    else
                        chatText.setPadding(15, 10, 0, 10);

                    chatText.setText(temp.textMsg);
                }

                android.util.Log.e("reciver only text case------>", "reciver only text case------>");

            }//end of text- receiver end

            //clicks and text- receiver end

            if (!(temp.clicks.equalsIgnoreCase("no")) && Utils.isEmptyString(temp.card_id) && Utils.isEmptyString(temp.sharingMedia)) {

                android.util.Log.e("reciver click case------>", "reciver click case------>");

                chatClickTextLayout.setVisibility(View.VISIBLE);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);
                TextView clicksText = (TextView) row.findViewById(R.id.clicks_text);
                clicksText.setPadding(10, 0,10, 0); // set padding for clicks prafull code
                clicksText.setVisibility(View.VISIBLE);
                clicksText.setText(temp.clicks);
                clicksText.setTextColor(context.getResources().getColor(R.color.white));
                ImageView clicksHeart = (ImageView) row.findViewById(R.id.iv_clicks_heart);
                clicksHeart.setVisibility(View.VISIBLE);
                //check if only clicks is there
                parent_shared_layout.setBackgroundResource(R.drawable.newbg_pinkright);
                if (!(Utils.isEmptyString(temp.textMsg))) {
                    TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                    chatText.setSingleLine(true);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.setTextColor(context.getResources().getColor(R.color.white));
                    if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                        chatText.setText(temp.textMsg.substring(0, 13));
                        TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                        chatTextLong.setVisibility(View.VISIBLE);
                        chatTextLong.setText(temp.textMsg.substring(13));
                        chatTextLong.setTextColor(context.getResources().getColor(R.color.white));
                        parent_shared_layout.setBackgroundResource(R.drawable.newbg_pinkright);  //code for double line
                        parent_shared_layout.setPadding(5, 0, 28, 10); // for padding from bottom prafull
                    } else {
                        if ((Utils.isEmptyString(temp.content_url))) {  // check prafull
                            clicksArea.setBackgroundResource(R.drawable.pink_squre);
                        } else {
                            parent_shared_layout.setBackgroundResource(R.drawable.newbg_pinkright);  // code for double line
                            clicksArea.setBackgroundResource(R.drawable.pink_squre);
                            LinearLayout image_attached = (LinearLayout) row.findViewById(R.id.media_layout);
                            RelativeLayout.LayoutParams mLinearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            mLinearParams.setMargins(0, 1, 5, 5);
                            image_attached.setLayoutParams(mLinearParams);
                        }
                        chatText.setText(temp.textMsg);
                        // prafull code
                    }


                } else { // in case only click
                    clicksArea.setBackgroundResource(R.drawable.pink_squre);
                }
                //clicks and text- sharing media-sender case
            } else if (!(temp.clicks.equalsIgnoreCase("no")) && Utils.isEmptyString(temp.card_id) && !Utils.isEmptyString(temp.sharingMedia)) { // for share case

                LinearLayout mParentShareLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area_share);
                mParentShareLayout.setVisibility(View.VISIBLE);
                parent_shared_layout.setBackgroundResource(R.drawable.newbg);
                parent_shared_layout.setVisibility(View.VISIBLE);

                android.util.Log.e("at reciver end in share--->", "at reciver end in share--->");
                LinearLayout mClickArea = (LinearLayout) row.findViewById(R.id.clicks_area_share);
                mClickArea.setVisibility(View.VISIBLE);
                TextView mShareText = (TextView) row.findViewById(R.id.clicks_text_share);
                TextView mShareSort = (TextView) row.findViewById(R.id.chat_text_share);
                TextView mShareLong = (TextView) row.findViewById(R.id.long_chat_text_share);
                ImageView mHertImage = (ImageView) row.findViewById(R.id.iv_clicks_heart_share);
                mHertImage.setVisibility(View.VISIBLE);
                mShareText.setText(temp.clicks);
                mShareText.setVisibility(View.VISIBLE);
                if (!(Utils.isEmptyString(temp.textMsg))) {


                    if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                        mShareSort.setText(temp.textMsg.substring(0, 12));
                        mShareSort.setVisibility(View.VISIBLE);
                        mShareLong.setTextColor(context.getResources().getColor(R.color.white));
                        mShareLong.setVisibility(View.VISIBLE);

                    } else {
                        mShareSort.setText(temp.textMsg);
                        mShareSort.setVisibility(View.VISIBLE);
                    }
                }


            }//end of click and text -reciver end

            if (!Utils.isEmptyString(temp.sharingMedia)) {

                if (temp.shareStatus.equalsIgnoreCase("shareAccepted")) { // set tip iamge for share accepted

                    parent_shared_layout.setBackgroundResource(R.drawable.sharedright);
                    row.findViewById(R.id.parent_clicks_area).setVisibility(View.GONE);
                    row.findViewById(R.id.parent_clicks_area_share).setVisibility(View.GONE);
                    ((RelativeLayout) row.findViewById(R.id.chat_parent_layout)).setVisibility(View.GONE);
                    ((RelativeLayout) row.findViewById(R.id.shared_header_view)).setVisibility(View.GONE);
                    ((LinearLayout) row.findViewById(R.id.shared_footer_view)).setVisibility(View.GONE);
                } else if (temp.shareStatus.equalsIgnoreCase("shareRejected")) {//// set tip iamge for share Rejected

                    parent_shared_layout.setBackgroundResource(R.drawable.shareddeniedright);
                    ((RelativeLayout) row.findViewById(R.id.chat_parent_layout)).setVisibility(View.GONE);
                    ((RelativeLayout) row.findViewById(R.id.shared_header_view)).setVisibility(View.GONE);
                    ((LinearLayout) row.findViewById(R.id.shared_footer_view)).setVisibility(View.GONE);
                } else if (temp.shareStatus.equalsIgnoreCase("shared")) {
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
                    ((RelativeLayout) row.findViewById(R.id.shared_header_view)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.shared_footer_view)).setVisibility(View.VISIBLE);

                    String[] splitted = relationManager.getPartnerName.split("\\s+");
                    ((TextView) row.findViewById(R.id.shared_by_name)).setTextColor(Color.BLACK);//akshit Code
                    ((TextView) row.findViewById(R.id.shared_by_name)).setText(splitted[0]);
                    ((TextView) row.findViewById(R.id.shared_message)).setText(" wants to share");

                    ((LinearLayout) row.findViewById(R.id.parent_clicks_area_share)).setVisibility(View.GONE);
                }

                    // Mukesh share audio layout
                   if(!Utils.isEmptyString(temp.content_url) && Utils.isEmptyString(temp.video_thumb) &&  Utils.isEmptyString(temp.imageRatio)){
                       RelativeLayout mRelative = (RelativeLayout) row.findViewById(R.id.temp_layout);
                       mRelative.setVisibility(View.VISIBLE);

                       ImageView mAudioImage = (ImageView) row.findViewById(R.id.iv_play_btn);
                       mAudioImage.setVisibility(View.VISIBLE);

                       ImageView mSpeakerImage = (ImageView) row.findViewById(R.id.iv_play_btn_);
                       mSpeakerImage.setVisibility(View.VISIBLE);
                   }
                }
                ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
            }//end of share view at reciver side

        }//end of reciver


        /* prafull code for clicklistener on image audio and video */

        ((ImageView) row.findViewById(R.id.iv_chat_image)).setTag(position);
        ((ImageView) row.findViewById(R.id.iv_chat_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                playmedia(position);

            }
        });
        ((ImageView) row.findViewById(R.id.iv_play_btn)).setTag(position);
        ((ImageView) row.findViewById(R.id.iv_play_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                playmedia(position);

            }
        });

        row.setTag(position);
// Click Action On Share ICon
        ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setTag(position);
        ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = (Integer) v.getTag();
                sendShareValues(position);


            }
        });


        // Click Action On Share With REJECT
        ((TextView) row.findViewById(R.id.shared_message_reject)).setTag(position);
        ((TextView) row.findViewById(R.id.shared_message_reject)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthManager authM = ModelManager.getInstance().getAuthorizationManager();
                int position = (Integer) v.getTag();
                ChatMessageBody item = ModelManager.getInstance().getChatManager().chatMessageList.get(position);

                Intent i = new Intent(context, ChatRecordView.class);
                if (!Utils.isEmptyString(item.imageRatio)) {
                    i.putExtra("imageRatio", item.imageRatio);
                    i.putExtra("fileId", item.content_url);
                } else if (!Utils.isEmptyString(item.video_thumb)) {
                    i.putExtra("videoThumbnail", item.video_thumb);
                    i.putExtra("videoID", item.content_url);
                } else if (!Utils.isEmptyString(item.content_url) && Utils.isEmptyString(item.imageRatio) && Utils.isEmptyString(item.video_thumb)) {
                    i.putExtra("audioID", item.content_url);
                }
                i.putExtra("originalChatId", item.originalMessageID);
                i.putExtra("chatType", item.chatType);
                i.putExtra("clicks", item.clicks);
                i.putExtra("textMsg", item.textMsg);
                i.putExtra("caption", item.shareComment);
                i.putExtra("facebookToken", item.facebookToken);
                i.putExtra("sharingMedia", item.sharingMedia);
                i.putExtra("shareStatus", "shareRejected");
                i.putExtra("isMessageSender", item.isMessageSender);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setAction("SHARE");
                context.startActivity(i);

                ChatManager chatManager = ModelManager.getInstance().getChatManager();
                chatManager.chatShare(authM.getPhoneNo(), authM.getUsrToken(), item.relationshipId, item.chatId, item.sharingMedia, item.facebookToken, item.shareComment, "no");

            }
        });
        // Click Action On Share With ACCEPT-mukesh
        ((TextView) row.findViewById(R.id.shared_message_accept)).setTag(position);
        ((TextView) row.findViewById(R.id.shared_message_accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthManager authM = ModelManager.getInstance().getAuthorizationManager();
                int position = (Integer) v.getTag();
                ChatMessageBody item = ModelManager.getInstance().getChatManager().chatMessageList.get(position);

                Intent i = new Intent(context, ChatRecordView.class);
                if (!Utils.isEmptyString(item.imageRatio)) {
                    i.putExtra("imageRatio", item.imageRatio);
                    i.putExtra("fileId", item.content_url);
                } else if (!Utils.isEmptyString(item.video_thumb)) {
                    i.putExtra("videoThumbnail", item.video_thumb);
                    i.putExtra("videoID", item.content_url);
                } else if (!Utils.isEmptyString(item.content_url) && Utils.isEmptyString(item.imageRatio) && Utils.isEmptyString(item.video_thumb)) {
                    i.putExtra("audioID", item.content_url);
                }
                i.putExtra("originalChatId", item.originalMessageID);
                i.putExtra("chatType", item.chatType);
                i.putExtra("clicks", item.clicks);
                i.putExtra("textMsg", item.textMsg);
                i.putExtra("caption", item.shareComment);
                i.putExtra("facebookToken", item.facebookToken);
                i.putExtra("sharingMedia", item.sharingMedia);
                i.putExtra("shareStatus", "shareAccepted");
                i.putExtra("isMessageSender", item.isMessageSender);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setAction("SHARE");
                context.startActivity(i);

                ChatManager chatManager = ModelManager.getInstance().getChatManager();
                chatManager.chatShare(authM.getPhoneNo(), authM.getUsrToken(), item.relationshipId, item.chatId, item.sharingMedia, item.facebookToken, item.shareComment, "yes");

            }
        });
        return row;
    }

    private void playmedia(int position) {
        ChatMessageBody item = ModelManager.getInstance().getChatManager().chatMessageList.get(position);
        if (!(Utils.isEmptyString(item.location_coordinates))) {

            //android.util.android.util.Log.e("open map--->", "open map--->");
            String coordinates = item.location_coordinates;
            // android.util.android.util.Log.e("location coordinates-->", "" + coordinates);
            Intent intent = new Intent(context, MapView.class);
            intent.putExtra("from", "chatrecord");
            intent.putExtra("coordinates", coordinates);
            try {
                context.startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (!(Utils.isEmptyString(item.imageRatio))) {
           /* android.util.android.util.Log.e("image play", "image play");
            android.util.android.util.Log.e("location coordinates-->", "" + item.location_coordinates);
            android.util.android.util.Log.e("image url --->", "" + item.content_url);*/

            Uri uri = Uri.parse(item.content_url);
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setDataAndType(uri, "image/*");
            try {
                context.startActivity(it);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!Utils.isEmptyString(item.video_thumb)) {
            /*android.util.android.util.Log.e("video play", "video play");
            android.util.android.util.Log.e("video url --->", "" + item.content_url);*/

            Uri intentUri = Uri.parse(item.content_url);

            Intent intent1 = new Intent();
            intent1.setAction(Intent.ACTION_VIEW);
            intent1.setDataAndType(intentUri, "video/*");
            try {
                context.startActivity(intent1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (!Utils.isEmptyString(item.content_url) && Utils.isEmptyString(item.video_thumb)) {
            /*android.util.android.util.Log.e("audio play", "audio play");
            android.util.android.util.Log.e("audio url --->", "" + item.content_url);*/

            android.util.Log.e("play audio", "play audio");

            Uri intentUri = Uri.parse(item.content_url);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(intentUri, "audio/*");
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendUpdateCardValues(int index, String action, String counteredAction) {
        ChatMessageBody item = ModelManager.getInstance().getChatManager().chatMessageList.get(index);

        Intent i = new Intent();

        i.putExtra("card_url", item.card_url);
        i.putExtra("card_clicks", item.clicks);
        i.putExtra("Title", item.card_heading);
        i.putExtra("Discription", item.card_content);
        i.putExtra("card_id", item.card_id);
        i.putExtra("is_CustomCard", item.is_CustomCard);
        i.putExtra("card_DB_ID", item.card_DB_ID);
        i.putExtra("card_Accepted_Rejected", action);
        i.putExtra("played_Countered", item.card_Played_Countered);
        i.putExtra("card_originator", item.card_originator);

        if (action.equalsIgnoreCase("countered")) {
            if (item.is_CustomCard) {
                i.setClass(context, ViewTradeCart.class);
            } else {
                i.setClass(context, Card.class);
            }
            i.putExtra("FromCard", false);
            i.putExtra("ForCounter", true);
        } else {
            i.setClass(context, ChatRecordView.class);
            i.setAction("Card");
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        context.startActivity(i);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.stay);//akshit code

    }

    //mukesh
    private void sendShareValues(int index) {
        ChatMessageBody item = ModelManager.getInstance().getChatManager().chatMessageList.get(index);
        String isMessageSender = "false";
        Intent i = new Intent();
        i.putExtra("chatType", item.chatType);
        i.putExtra("clicks", item.clicks);
        i.putExtra("textMsg", item.textMsg);
        i.putExtra("originalChatId", item.chatId);
        i.putExtra("isMessageSender", isMessageSender);

        if (!Utils.isEmptyString(item.imageRatio)) {
            i.putExtra("imageRatio", item.imageRatio);
            i.putExtra("fileId", item.content_url);
        }else if (!Utils.isEmptyString(item.card_owner)) {
            i.putExtra("card_owner", item.card_owner);
            i.putExtra("card_url", item.card_url);
           // i.putExtra("card_clicks", item.clicks);
        } else if (!Utils.isEmptyString(item.video_thumb)) {
            i.putExtra("videoThumbnail", item.video_thumb);
            i.putExtra("videoID", item.content_url);

        } else if (!Utils.isEmptyString(item.content_url) && Utils.isEmptyString(item.imageRatio) && Utils.isEmptyString(item.video_thumb)) {
            i.putExtra("audioID", item.content_url);
        }

        i.setClass(context, ViewShare.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


        context.startActivity(i);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /*static class RecordHolder {

        RelativeLayout rrMainLayout, rlTimeStatusSender,chatParentLayout,rl_acc_res_bg,rl_card;
        LinearLayout llSrTime,clicksArea,cardView,cardAction,cardViewCc,cardViewCards,ll_cc_action;
        ImageView chatImage, shareIcon, sendStatus,clicksHeart,audioView,playIcon ,tradeImage,iv_accept_card,iv_resect_card,iv_acc_rec,iv_card_counter,iv_again_counter_acc,iv_again_counter_rej,iv_type_two_share_icon_r;
        TextView chatText, timeText,clicksText,trdClicksBottom,trdClicksTop,card_partner_name,tv_counter_card,tv_acc_res_name,tv_acc_res_status,trd_clicks_top_ar,trd_clicks_bottom_ar,tv_counter_card_action,trd_custom_heading,trd_custom_ar_heading;


    }
*/
}