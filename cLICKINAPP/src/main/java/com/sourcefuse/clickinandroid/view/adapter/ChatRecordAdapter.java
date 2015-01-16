package com.sourcefuse.clickinandroid.view.adapter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.utils.AppController;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FeedImageView;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.Card;
import com.sourcefuse.clickinandroid.view.ChatRecordView;
import com.sourcefuse.clickinandroid.view.MapView;
import com.sourcefuse.clickinandroid.view.ViewShare;
import com.sourcefuse.clickinandroid.view.ViewTradeCart;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ChatRecordAdapter extends ArrayAdapter<ChatMessageBody> {

    private static final String TAG = ChatRecordAdapter.class.getSimpleName();
    public Context context;

    ArrayList<ChatMessageBody> currentChatList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String path;
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
        final View row = inflater.inflate(R.layout.view_chat_demo, parent, false);
        String oursQbId = ModelManager.getInstance().getAuthorizationManager().getQBId();
        RelativeLayout parentChatLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);
        relationManager = ModelManager.getInstance().getRelationManager();

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        //monika- in case of chat history, we get clicks value null, so convert it to standard "no" value
        if (Utils.isEmptyString(temp.clicks))
            temp.clicks = "no";

        if (temp.senderQbId.equalsIgnoreCase(oursQbId)) { //start of sender


            LinearLayout parent_shared_layout = (LinearLayout) row.findViewById(R.id.parent_shared_layout);
            parent_shared_layout.setBackgroundResource(R.drawable.newbg_grey);
            RelativeLayout rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);
            LinearLayout chatClickTextLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area);


            //code to set time
            TextView timeView = (TextView) row.findViewById(R.id.tv_time_text);
            timeView.setText(Utils.getLocalDatefromTimestamp(Long.parseLong(temp.sentOn)));

            //code -for image -sender-start here
            if (!(Utils.isEmptyString(temp.imageRatio))) {    /// imgage code for sender end
                //set layout properties for image view
                /* layout params for image */


                LinearLayout mImageLayout = (LinearLayout) row.findViewById(R.id.media_layout);
                final ImageView image_attached = (ImageView) row.findViewById(R.id.iv_chat_image);
                FeedImageView iv_chat_image_ = (FeedImageView) row.findViewById(R.id.iv_chat_image_);
                LinearLayout media_layout = (LinearLayout) row.findViewById(R.id.media_layout);
                media_layout.setVisibility(View.VISIBLE);
                image_attached.setVisibility(View.VISIBLE);


                //code to set msg deilvery notification
                if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
                } else if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.GONE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.VISIBLE);
                }

                //if sharing media is there, then chatId will be fetched from originalChatIdMessage-monika
                String tempChatid = null;
                if (!Utils.isEmptyString(temp.sharingMedia)) {
                    tempChatid = temp.originalMessageID;
                } else {
                    tempChatid = temp.chatId;
                }

                /* path where clickin image are stored */

                String mContentUri = Utils.mImagePath + tempChatid + ".jpg"; // featch data from

                Uri mUri = Utils.getImageContentUri(context, new File(mContentUri));  //check file exist or not
                if (!Utils.isEmptyString("" + mUri)) {
                    try {
                        image_attached.setImageURI(mUri); // if file exists set it by uri
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                } else {  // when file not exists
                    final ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
                    progressBar.setVisibility(View.VISIBLE);  // show prpgress bar
                    final RelativeLayout mTempLayout = (RelativeLayout) row.findViewById(R.id.temp_layout);

                    RelativeLayout.LayoutParams mlayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    mlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mTempLayout.setLayoutParams(mlayoutParams);

                    mTempLayout.setVisibility(View.VISIBLE);
                    iv_chat_image_.setImageUrl(temp.content_url, imageLoader);

                    //chatId by which image got saved-monika
                    final String chatIdForImage = tempChatid;
                    iv_chat_image_.setResponseObserver(new FeedImageView.ResponseObserver() { // download image
                        @Override
                        public void onError(VolleyError volleyError) {
                        }

                        @Override
                        public void onSuccess(ImageLoader.ImageContainer loader) {
                            if (loader.getBitmap() != null) {
                                String path = Utils.storeImage(loader.getBitmap(), chatIdForImage, context);  // save image bitmap by chat id
                                image_attached.setImageURI(Utils.getImageContentUri(context, new File(path))); // set image form uri once downloadedd
                                progressBar.setVisibility(View.GONE);
                                mTempLayout.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                image_attached.setVisibility(View.VISIBLE);
                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);

  /* for map to set text location shared */
                TextView mLongTextView = (TextView) row.findViewById(R.id.long_chat_text_share);
                if (!(Utils.isEmptyString(temp.location_coordinates))) {
                    row.findViewById(R.id.parent_clicks_area_share).setVisibility(View.VISIBLE);
                    if (!(Utils.isEmptyString(temp.sharingMedia))) {
                        ((LinearLayout) row.findViewById(R.id.parent_clicks_area_share)).setBackgroundColor(context.getResources().getColor(R.color.white));
                    } else {
                        ((LinearLayout) row.findViewById(R.id.parent_clicks_area_share)).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    }

                    mLongTextView.setVisibility(View.VISIBLE);
                    mLongTextView.setPadding(pxlToDp(5), pxlToDp(10), 0, pxlToDp(10));
                    mLongTextView.setTextColor(context.getResources().getColor(R.color.black));
                    mLongTextView.setText("Location Shared");

                }
                /* for map to set text location shared */


            } else if (!Utils.isEmptyString(temp.content_url) && Utils.isEmptyString(temp.video_thumb)) {// //end of image loop-sender And Audio start


                parent_shared_layout.setBackgroundResource(R.drawable.newbg_grey);
                ImageView mAudioImage = (ImageView) row.findViewById(R.id.iv_play_btn);
                ImageView mSpeakerImage = (ImageView) row.findViewById(R.id.iv_play_btn_);
                TextView mLongTextView = (TextView) row.findViewById(R.id.long_chat_text);
                row.findViewById(R.id.temp_layout).setVisibility(View.VISIBLE);
                mAudioImage.setVisibility(View.VISIBLE);
                mSpeakerImage.setVisibility(View.VISIBLE);
                mLongTextView.setEms(10);//akshit change


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
                if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
                } else if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.GONE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.VISIBLE);
                }
            } else if (!Utils.isEmptyString(temp.video_thumb)) {//end of audio code sender START VIDEO VIEW FOR SENDER
                final ImageView image_attached = (ImageView) row.findViewById(R.id.iv_chat_image);
                FeedImageView iv_chat_image_ = (FeedImageView) row.findViewById(R.id.iv_chat_image_);
                final ImageView play_buttom = (ImageView) row.findViewById(R.id.iv_play_btn);
                play_buttom.setVisibility(View.VISIBLE);

                RelativeLayout mRelative = (RelativeLayout) row.findViewById(R.id.temp_layout);
                mRelative.setVisibility(View.VISIBLE);
                mRelative.setBackgroundResource(R.color.transparent);
                RelativeLayout.LayoutParams mlayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mRelative.setLayoutParams(mlayoutParams);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                play_buttom.setLayoutParams(layoutParams);
                if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {
                    row.findViewById(R.id.pb_loding).setVisibility(View.VISIBLE);
                    row.findViewById(R.id.iv_type_two_share_icon_r).setVisibility(View.GONE);
                } else if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)) {
                    row.findViewById(R.id.pb_loding).setVisibility(View.GONE);
                }

                //if sharing media is there, then chatId will be fetched from originalChatIdMessage-monika
                String tempChatid = null;
                if (!Utils.isEmptyString(temp.sharingMedia)) {
                    tempChatid = temp.originalMessageID;
                } else {
                    tempChatid = temp.chatId;
                }

                /* path where clickin image are stored */
                String mContentUri = Utils.mImagePath + tempChatid + ".jpg"; // fetch data from


                Uri mUri = Utils.getImageContentUri(context, new File(mContentUri));
                if (!Utils.isEmptyString("" + mUri)) {  // check video thumb exists or not
                    try {
                        image_attached.setImageURI(mUri);  // set thumb from uri
                        play_buttom.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                } else {  // download image from server

                    final ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
                    progressBar.setVisibility(View.VISIBLE);
                    play_buttom.setVisibility(View.GONE);

                    iv_chat_image_.setImageUrl(temp.video_thumb, imageLoader);  // download image from server
                    //chat Id by which image got saved
                    final String chatIdForImage = tempChatid;
                    iv_chat_image_.setResponseObserver(new FeedImageView.ResponseObserver() {  // response observer
                        @Override
                        public void onError(VolleyError volleyError) {
                        }

                        @Override
                        public void onSuccess(ImageLoader.ImageContainer loader) {
                            if (loader.getBitmap() != null) {
                                String path = Utils.storeImage(loader.getBitmap(), chatIdForImage, context);
                                image_attached.setImageURI(Utils.getImageContentUri(context, new File(path)));
                                progressBar.setVisibility(View.GONE);
                                /*mTempLayout.setVisibility(View.GONE);*/
                                play_buttom.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }


                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                image_attached.setVisibility(View.VISIBLE);
            } else if (!Utils.isEmptyString(temp.card_id)) {// //end of video loop-sender And Card start

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

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("accepted")) {//enf of first time played card
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);

                    //code to decide who is accepted the card- basis on card owner- importance while sharing card
                    String name=" ";
                    if(temp.card_owner.equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getQBId())){

                        //parnter accepted the card
                        String[] splitted = relationManager.getPartnerName.split("\\s+");
                        name=splitted[0];
                    }else{
                        //it means you accepted the card
                        name="You";

                    }
                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText(name);
                    ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("ACCEPTED!");

                    ImageView acc_rej_view = ((ImageView) row.findViewById(R.id.iv_acc_rec));
                    acc_rej_view.setVisibility(View.VISIBLE);
                    acc_rej_view.setImageResource(R.drawable.c_card_accept);

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("rejected")) {//enf of accept card
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);

                    //code to decide who is accepted the card- basis on card owner- importance while sharing card
                    String name=" ";
                    if(temp.card_owner.equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getQBId())){

                        //parnter accepted the card
                        String[] splitted = relationManager.getPartnerName.split("\\s+");
                        name=splitted[0];
                    }else{
                        //it means you accepted the card
                        name="You";

                    }

                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText(name);
                    ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("REJECTED!");

                    ImageView acc_rej_view = ((ImageView) row.findViewById(R.id.iv_acc_rec));
                    acc_rej_view.setVisibility(View.VISIBLE);
                    acc_rej_view.setImageResource(R.drawable.c_card_rejected);

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("countered")) {//end of reject card
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);
                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText("You" + " made a");
                    ((LinearLayout) row.findViewById(R.id.ll_cc_action)).setVisibility(View.GONE);
                }//end of countered card
                //common for accept. reject and countered card-display card from local and set values
                if (!Utils.isEmptyString(temp.card_id) && !temp.card_Accepted_Rejected.equalsIgnoreCase("nil")) {//end of reject card
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    if (temp.card_Played_Countered.equalsIgnoreCase("played")) {//akshit Code for making button in active .
                        ((ImageView) row.findViewById(R.id.tv_counter_card_action)).setImageResource(R.drawable.counterbtnx_deactive);
                        ((ImageView) row.findViewById(R.id.iv_again_counter_acc)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
                        ((ImageView) row.findViewById(R.id.iv_again_counter_rej)).setBackgroundResource(R.drawable.c_card_reject_deactive);
                    }//ends
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
            //in case share accept/reject, ignore the text
            if (!Utils.isEmptyString(temp.textMsg) && temp.clicks.equalsIgnoreCase("no") && Utils.isEmptyString(temp.isAccepted)
                    && Utils.isEmptyString(temp.location_coordinates)) {
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
                } else {
                    parent_shared_layout.setBackgroundResource(R.drawable.newbg_grey);
                    chatText.setBackgroundResource(R.drawable.grey_square);
                    chatText.setText("" + temp.textMsg);
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
                parent_shared_layout.setBackgroundResource(R.drawable.newbg_grey);
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
                        mShareSort.setText(temp.textMsg.substring(0, 15));
                        mShareSort.setVisibility(View.VISIBLE);
                        mShareLong.setText(temp.textMsg.substring(15));//akshit changes
                        mShareLong.setTextColor(context.getResources().getColor(R.color.white));
                        mShareLong.setVisibility(View.VISIBLE);
                    } else {
                        mShareSort.setText(temp.textMsg);
                        mShareSort.setVisibility(View.VISIBLE);
                    }
                }


            }//end of clicks and text-sharing media-sender case


            if (!Utils.isEmptyString(temp.sharingMedia)) {


                if (temp.shareStatus.equalsIgnoreCase("shareAccepted")) { // set tip iamge for share accepted
                    parent_shared_layout.setBackgroundResource(R.drawable.sharedleft);
                    row.findViewById(R.id.parent_clicks_area).setVisibility(View.GONE);
                    row.findViewById(R.id.parent_clicks_area_share).setVisibility(View.GONE);
                    ((ImageView) row.findViewById(R.id.iv_chat_image)).setVisibility(View.GONE);
                    row.findViewById(R.id.chat_text).setVisibility(View.GONE);
                    row.findViewById(R.id.long_chat_text_share).setVisibility(View.GONE);
                    row.findViewById(R.id.temp_layout).setVisibility(View.GONE);
                    row.findViewById(R.id.long_chat_text).setVisibility(View.GONE);
                } else if (temp.shareStatus.equalsIgnoreCase("shareRejected")) {//// set tip iamge for share Rejected
                    parent_shared_layout.setBackgroundResource(R.drawable.shareddeniedleft);
                    ((ImageView) row.findViewById(R.id.iv_chat_image)).setVisibility(View.GONE);
                    row.findViewById(R.id.temp_layout).setVisibility(View.GONE);
                    row.findViewById(R.id.long_chat_text_share).setVisibility(View.GONE);
                    row.findViewById(R.id.long_chat_text).setVisibility(View.GONE);
                    row.findViewById(R.id.chat_text).setVisibility(View.GONE);

                } else if (temp.shareStatus.equalsIgnoreCase("shared")) {
                    ((RelativeLayout) row.findViewById(R.id.shared_header_view)).setVisibility(View.VISIBLE);
                    if (!Utils.isEmptyString(temp.card_id)) {
                        row.findViewById(R.id.except_share).setVisibility(View.GONE);
                        row.findViewById(R.id.incase_share).setVisibility(View.VISIBLE);
                    }
                    ((TextView) row.findViewById(R.id.shared_by_name)).setText("You");
                    ((TextView) row.findViewById(R.id.shared_message)).setText(" want to share");



                    /* for share prafull code   */
                }
                ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
            }//end of share view at Sender side


            //common for all type chat-delivered chat status-monika
            if (!Utils.isEmptyString(temp.deliveredChatID)) {
                ImageView sendStatusView = (ImageView) row.findViewById(R.id.iv_send_status);
                sendStatusView.setImageResource(R.drawable.double_check);
            }
        }//end of sender loop
        else {


            //RECEIVER START
            LinearLayout parent_shared_layout = (LinearLayout) row.findViewById(R.id.parent_shared_layout);
            parent_shared_layout.setBackgroundResource(R.drawable.new_whitearrownew);
            RelativeLayout rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);
            row.findViewById(R.id.iv_send_status).setVisibility(View.GONE);
            RelativeLayout chatParentLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);
            LinearLayout chatClickTextLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area);
            RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsrr.addRule(RelativeLayout.LEFT_OF, R.id.parent_shared_layout);
            paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.parent_shared_layout);
            rlTimeStatusSender.setLayoutParams(paramsrr);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            parent_shared_layout.setLayoutParams(params);
            //code to set time
            TextView timeView = (TextView) row.findViewById(R.id.tv_time_text);
            timeView.setText(Utils.getLocalDatefromTimestamp(Long.parseLong(temp.sentOn)));


            /* code to play sound */
            if (Utils.mPlayChatSound && temp.clicks.equalsIgnoreCase("no") || Utils.mPlayChatSound && !Utils.isEmptyString(temp.card_id)) { // code to play sound in normal case exxcept clicks
                Utils.playSound((Activity) getContext(), R.raw.message_rcvd);
                Utils.mPlayChatSound = false;
                Log.e("in case 1--->", "in case 1--->");
            } else if (Utils.mPlayChatSound && !temp.clicks.equalsIgnoreCase("no") && Utils.isEmptyString(temp.card_id)) {// code to play sound in case of clicks
                Utils.playSound((Activity) getContext(), R.raw.clickreceived);
                Utils.mPlayChatSound = false;
                Log.e("in case 2--->", "in case 2--->");
            }


            //temp code -for image-receiver end
            if (!(Utils.isEmptyString(temp.imageRatio))) {   // image case for reciver end    image for reciver end

                LinearLayout media_layout = (LinearLayout) row.findViewById(R.id.media_layout);
                final ImageView image_attached = (ImageView) row.findViewById(R.id.iv_chat_image);
                final FeedImageView iv_chat_image_ = (FeedImageView) row.findViewById(R.id.iv_chat_image_); // temp view to download image
                media_layout.setVisibility(View.VISIBLE);
                image_attached.setVisibility(View.VISIBLE);
                LinearLayout mImageLayout = (LinearLayout) row.findViewById(R.id.media_layout);
                String tempChatid = null;
                if (!Utils.isEmptyString(temp.sharingMedia)) {
                    tempChatid = temp.originalMessageID;
                } else {
                    tempChatid = temp.chatId;
                }

                /* path where clickin image are stored */
                String mContentUri = Utils.mImagePath + tempChatid + ".jpg"; // fetch data from
                Uri mUri = Utils.getImageContentUri(context, new File(mContentUri));
                if (!Utils.isEmptyString("" + mUri)) {  // check if image exists on uri
                    try {
                        image_attached.setImageURI(mUri); // set image from uri
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {  // else part to download image from url

                    final ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
                    progressBar.setVisibility(View.VISIBLE);
                    final RelativeLayout mTempLayout = (RelativeLayout) row.findViewById(R.id.temp_layout);
                    RelativeLayout.LayoutParams mlayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    mlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mTempLayout.setLayoutParams(mlayoutParams);
                    mTempLayout.setVisibility(View.VISIBLE);
                    mTempLayout.setBackgroundResource(R.color.transparent);
                    iv_chat_image_.setImageUrl(temp.content_url, imageLoader);
                    //chat Id by which image got saved
                    final String chatIdForImage = tempChatid;
                    iv_chat_image_.setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError(VolleyError volleyError) {
                        }

                        @Override
                        public void onSuccess(ImageLoader.ImageContainer loader) {
                            if (loader.getBitmap() != null) {
                                String path = Utils.storeImage(loader.getBitmap(), chatIdForImage, context);
                                image_attached.setImageURI(Utils.getImageContentUri(context, new File(path)));
                                progressBar.setVisibility(View.GONE);
                                mTempLayout.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                image_attached.setVisibility(View.VISIBLE);
                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                 /* for map to set text location shared */


                if (!(Utils.isEmptyString(temp.location_coordinates))) {
                    row.findViewById(R.id.parent_clicks_area_share).setVisibility(View.VISIBLE);
                    if (!(Utils.isEmptyString(temp.sharingMedia))) {
                        ((LinearLayout) row.findViewById(R.id.parent_clicks_area_share)).setBackgroundColor(context.getResources().getColor(R.color.owner_relation_header_with_m));
                    } else {
                        ((LinearLayout) row.findViewById(R.id.parent_clicks_area_share)).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    }
                    TextView mLongTextView = (TextView) row.findViewById(R.id.long_chat_text_share);
                    mLongTextView.setVisibility(View.VISIBLE);
                    mLongTextView.setPadding(pxlToDp(5), pxlToDp(10), 0, pxlToDp(10));
                    mLongTextView.setTextColor(context.getResources().getColor(R.color.black));
                    mLongTextView.setText("Location Shared");

                }
               /* for map to set text location shared */

            } else if (!Utils.isEmptyString(temp.content_url) && Utils.isEmptyString(temp.video_thumb)) {//start of audio-RECIVER case


                ImageView mAudioImage = (ImageView) row.findViewById(R.id.iv_play_btn);
                mAudioImage.setVisibility(View.VISIBLE);

                row.findViewById(R.id.temp_).setVisibility(View.GONE);
                TextView mLongTextView = (TextView) row.findViewById(R.id.long_chat_text);
                mLongTextView.setEms(10);
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
                final ImageView image_attached = (ImageView) row.findViewById(R.id.iv_chat_image);
                final ImageView play_buttom = (ImageView) row.findViewById(R.id.iv_play_btn);
                play_buttom.setVisibility(View.VISIBLE);
                FeedImageView iv_chat_image_ = (FeedImageView) row.findViewById(R.id.iv_chat_image_);
                RelativeLayout mRelative = (RelativeLayout) row.findViewById(R.id.temp_layout);
                mRelative.setVisibility(View.VISIBLE);
                mRelative.setBackgroundResource(R.color.transparent);
                RelativeLayout.LayoutParams mlayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mRelative.setLayoutParams(mlayoutParams);
                if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {
                    ((ProgressBar) row.findViewById(R.id.pb_loding)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
                } else if (!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)) {
                    row.findViewById(R.id.pb_loding).setVisibility(View.GONE);
                    row.findViewById(R.id.iv_type_two_share_icon_r).setVisibility(View.VISIBLE);
                }

                //if sharing media is there, then chatId will be fetched from originalChatIdMessage-monika
                String tempChatid = null;
                if (!Utils.isEmptyString(temp.sharingMedia)) {
                    tempChatid = temp.originalMessageID;
                } else {
                    tempChatid = temp.chatId;
                }

                /* path where clickin image are stored */
                String mContentUri = Utils.mImagePath + tempChatid + ".jpg"; // fetch data from

                Uri mUri = Utils.getImageContentUri(context, new File(mContentUri));
                if (!Utils.isEmptyString("" + mUri)) {  // chdeck video thumb exist or not
                    try {
                        image_attached.setImageURI(mUri);  // set thumb if exists
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {  // download thumb from server
                    final ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
                    progressBar.setVisibility(View.VISIBLE);
                    play_buttom.setVisibility(View.GONE);
                    iv_chat_image_.setImageUrl(temp.video_thumb, imageLoader);  // download image from server
                    //chat Id by which image got saved
                    final String chatIdForImage = tempChatid;
                    iv_chat_image_.setResponseObserver(new FeedImageView.ResponseObserver() {  // response observer
                        @Override
                        public void onError(VolleyError volleyError) {
                        }

                        @Override
                        public void onSuccess(ImageLoader.ImageContainer loader) {
                            if (loader.getBitmap() != null) {
                                String path = Utils.storeImage(loader.getBitmap(), chatIdForImage, context);
                                image_attached.setImageURI(Utils.getImageContentUri(context, new File(path)));
                                progressBar.setVisibility(View.GONE);

                                play_buttom.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                image_attached.setVisibility(View.VISIBLE);
            } else if (!Utils.isEmptyString(temp.card_id)) {//end of video- start of card first time-receiver end
                //Card RECEIVE at 1st Time
                //share should not be present for first time case
                if (temp.card_Played_Countered.equalsIgnoreCase("played")) {//akshit Code to Disable buttons  ,Receiver
                    ((ImageView) row.findViewById(R.id.tv_counter_card_action)).setImageResource(R.drawable.counterbtnx_deactive);
                    ((ImageView) row.findViewById(R.id.iv_again_counter_acc)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
                    ((ImageView) row.findViewById(R.id.iv_again_counter_rej)).setBackgroundResource(R.drawable.c_card_reject_deactive);

                    ((ImageView) row.findViewById(R.id.tv_counter_card)).setImageResource(R.drawable.counterbtnx_deactive);
                    ((ImageView) row.findViewById(R.id.iv_accept_card)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
                    ((ImageView) row.findViewById(R.id.iv_reject_card)).setBackgroundResource(R.drawable.c_card_reject_deactive);

                }//akshit code ends for making button inactive
                ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);
                if (temp.card_Accepted_Rejected.equalsIgnoreCase("nil")) { //first time played

                    ((RelativeLayout) row.findViewById(R.id.send_card_first_time)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.card_recieve_first_time)).setVisibility(View.VISIBLE);
                    String clicks = temp.clicks;
                    if (clicks.equalsIgnoreCase("5"))
                        clicks = "05";
                    ((TextView) row.findViewById(R.id.trd_clicks_top)).setVisibility(View.VISIBLE);//akshit code
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom)).setVisibility(View.VISIBLE);//akshit code

//                    ((TextView) row.findViewById(R.id.trd_clicks_bottom)).setPadding(0, 13, 7, 13);//akshit code
//                    ((TextView) row.findViewById(R.id.trd_clicks_top)).setPadding(0, 13, 0, 0);//akshit code

                    ((TextView) row.findViewById(R.id.trd_clicks_top)).setText(clicks);
                    ((TextView) row.findViewById(R.id.trd_clicks_bottom)).setText(clicks);

                    String[] splitted = relationManager.getPartnerName.split("\\s+");
                    ((TextView) row.findViewById(R.id.card_partner_name)).setText(splitted[0]);

                    ImageView trade_image = (ImageView) row.findViewById(R.id.trade_image);
                    trade_image.setVisibility(View.VISIBLE);

                    //End
                    if (temp.is_CustomCard) {
                        TextView custom_heading = (TextView) row.findViewById(R.id.trd_custom_heading);
                        custom_heading.setVisibility(View.VISIBLE);
                        custom_heading.setText(temp.card_heading);
                        trade_image.setImageResource(R.drawable.tradecardpink_big);//Akshit code
                    } else {
                        String url_to_load = (temp.card_url).replaceFirst("cards\\/(\\d+)\\.jpg", "cards\\/a\\/1080\\/$1\\.jpg");
                        final RelativeLayout mTempLayout = (RelativeLayout) row.findViewById(R.id.temp_layout);
                        final ProgressBar mProgressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
                        RelativeLayout.LayoutParams mTempLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        mTempLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        mTempLayout.setLayoutParams(mTempLayoutParams);
                        mTempLayout.setBackgroundDrawable(null);
                        mTempLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);

                        Picasso.with(context).load(url_to_load)
                                .into(trade_image, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        mTempLayout.setVisibility(View.GONE);
                                        mProgressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        mTempLayout.setVisibility(View.GONE);
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                });
                    }

                    ((ImageView) row.findViewById(R.id.iv_reject_card)).setTag(position);
                    ((ImageView) row.findViewById(R.id.iv_reject_card)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Card Reject Action`
                            int position = (Integer) v.getTag();
                            //card is allowed to play once only- reject, accept or counter change state to played
                            if (temp.card_Played_Countered.equalsIgnoreCase("playing")) {
                                temp.card_Played_Countered = "played";
                                sendUpdateCardValues(position, "rejected", "REJECTED!");

                            }
                            //akshit code to disable button after making counter ,accept ,reject.
                            ((ImageView) row.findViewById(R.id.tv_counter_card)).setImageResource(R.drawable.counterbtnx_deactive);
                            ((ImageView) row.findViewById(R.id.iv_accept_card)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
                            ((ImageView) row.findViewById(R.id.iv_reject_card)).setBackgroundResource(R.drawable.c_card_reject_deactive);
                            //akshit code ends for making button inactive


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
                                temp.card_Played_Countered = "played";
                                sendUpdateCardValues(position, "accepted", "ACCEPTED!");

                            }
                            //akshit code to disable button after making counter ,accept ,reject.
                            ((ImageView) row.findViewById(R.id.tv_counter_card)).setImageResource(R.drawable.counterbtnx_deactive);
                            ((ImageView) row.findViewById(R.id.iv_accept_card)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
                            ((ImageView) row.findViewById(R.id.iv_reject_card)).setBackgroundResource(R.drawable.c_card_reject_deactive);
                            //akshit code ends for making button inactive


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
                                //   temp.card_Played_Countered = "played";
                                sendUpdateCardValues(position, "countered", "COUNTERED CARD!");

                            }
                            //akshit code to disable button after making counter ,accept ,reject.
                            ((ImageView) row.findViewById(R.id.tv_counter_card)).setImageResource(R.drawable.counterbtnx_deactive);
                            ((ImageView) row.findViewById(R.id.iv_accept_card)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
                            ((ImageView) row.findViewById(R.id.iv_reject_card)).setBackgroundResource(R.drawable.c_card_reject_deactive);
                            //akshit code ends for making button inactive


                        }
                    });
                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("accepted")) {//enf of first time played card-receiver
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);

                    //code to decide who is accepted the card- basis on card owner- importance while sharing card
                    String name=" ";
                    if(temp.card_owner.equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getQBId())){

                        //parnter accepted the card
                        String[] splitted = relationManager.getPartnerName.split("\\s+");
                        name=splitted[0];
                    }else{
                        //it means you accepted the card
                        name="You";

                    }

                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText(name);
                    ((TextView) row.findViewById(R.id.tv_acc_res_status)).setText("ACCEPTED!");

                    ImageView acc_rej_view = ((ImageView) row.findViewById(R.id.iv_acc_rec));
                    acc_rej_view.setVisibility(View.VISIBLE);
                    acc_rej_view.setImageResource(R.drawable.c_card_accept);

                } else if (temp.card_Accepted_Rejected.equalsIgnoreCase("rejected")) {//end of accept card-receiver
                    ((RelativeLayout) row.findViewById(R.id.rl_acc_rej_card)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.VISIBLE);
                    ((LinearLayout) row.findViewById(R.id.acc_rej_layout_second)).setVisibility(View.VISIBLE);
                    //code to decide who is accepted the card- basis on card owner- importance while sharing card
                    String name=" ";
                    if(temp.card_owner.equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getQBId())){

                        //parnter accepted the card
                        String[] splitted = relationManager.getPartnerName.split("\\s+");
                        name=splitted[0];
                    }else{
                        //it means you accepted the card
                        name="You";

                    }

                    ((TextView) row.findViewById(R.id.tv_acc_res_name)).setText(name);
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
                                temp.card_Played_Countered = "played";
                                sendUpdateCardValues(position, "rejected", "REJECTED!");

                                /* code to play sound */
                                Utils.playSound((Activity) getContext(), R.raw.tradecard_rejected);
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
                                temp.card_Played_Countered = "played";
                                sendUpdateCardValues(position, "accepted", "ACCEPTED!");


                                /* code to play sound */
                                Utils.playSound((Activity) getContext(), R.raw.tradecard_accepted);
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
                                //   temp.card_Played_Countered = "played";
                                sendUpdateCardValues(position, "countered", "COUNTERED CARD!");

                                /* code to play sound */
                                Utils.playSound((Activity) getContext(), R.raw.message_sent);
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
            //in case share accept/reject, ignore the text

            if (!Utils.isEmptyString(temp.textMsg) && temp.clicks.equalsIgnoreCase("no") && Utils.isEmptyString(temp.isAccepted)
                    && Utils.isEmptyString(temp.location_coordinates)) {

                //code to hide share icon for text messages-monika
                if (Utils.isEmptyString(temp.content_url))
                    ((LinearLayout) row.findViewById(R.id.ll_for_share_icon)).setVisibility(View.GONE);


                TextView chatText = (TextView) row.findViewById(R.id.long_chat_text); // prafull code


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
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.setTextColor(context.getResources().getColor(R.color.black));
                    parent_shared_layout.setBackgroundResource(R.drawable.new_whitearrownew);
                    chatText.setBackgroundResource(R.drawable.white_square);
                    LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                    clicksArea.setVisibility(View.VISIBLE);
                    if (Utils.isEmptyString(temp.content_url))
                        chatText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                    chatText.setText(temp.textMsg);
                }


            }//end of text- receiver end

            //clicks and text- receiver end

            if (!(temp.clicks.equalsIgnoreCase("no")) && Utils.isEmptyString(temp.card_id) && Utils.isEmptyString(temp.sharingMedia)) { // for share case ) {


                chatClickTextLayout.setVisibility(View.VISIBLE);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);
                TextView clicksText = (TextView) row.findViewById(R.id.clicks_text);

                clicksText.setVisibility(View.VISIBLE);
                clicksText.setText(temp.clicks.toString().trim());//akshit,to remove unwanted space from clicks
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

                    } else {
                        if ((Utils.isEmptyString(temp.content_url))) {  // check prafull
                            clicksArea.setBackgroundResource(R.drawable.pink_squre);
                        } else {
                            parent_shared_layout.setBackgroundResource(R.drawable.newbg_pinkright);  // code for double line
                            clicksArea.setBackgroundResource(R.drawable.pink_squre);
                            LinearLayout image_attached = (LinearLayout) row.findViewById(R.id.media_layout);

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
                parent_shared_layout.setBackgroundResource(R.drawable.greyarrownew_white);
                parent_shared_layout.setVisibility(View.VISIBLE);


                LinearLayout mClickArea = (LinearLayout) row.findViewById(R.id.clicks_area_share);
                mClickArea.setVisibility(View.VISIBLE);
                TextView mShareText = (TextView) row.findViewById(R.id.clicks_text_share);
                TextView mShareSort = (TextView) row.findViewById(R.id.chat_text_share);
                TextView mShareLong = (TextView) row.findViewById(R.id.long_chat_text_share);
                ImageView mHertImage = (ImageView) row.findViewById(R.id.iv_clicks_heart_share);
                mHertImage.setVisibility(View.VISIBLE);
                mShareText.setVisibility(View.VISIBLE);
                mShareText.setText(temp.clicks);
                mShareText.setVisibility(View.VISIBLE);
                if (!(Utils.isEmptyString(temp.textMsg))) {

                    if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                        mShareSort.setText(temp.textMsg.substring(0, 12));
                        mShareSort.setVisibility(View.VISIBLE);
                        mShareLong.setText(temp.textMsg.substring(12));//akshit changes
                        mShareLong.setTextColor(context.getResources().getColor(R.color.white));
                        mShareLong.setVisibility(View.VISIBLE);


                    } else {
                        mShareSort.setVisibility(View.VISIBLE);
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
                    if (!Utils.isEmptyString(temp.card_id)) {
                        row.findViewById(R.id.except_share).setVisibility(View.GONE);
                        row.findViewById(R.id.incase_share).setVisibility(View.VISIBLE);
                    }
                    ((LinearLayout) row.findViewById(R.id.shared_footer_view)).setVisibility(View.VISIBLE);

                    String[] splitted = relationManager.getPartnerName.split("\\s+");
                    ((TextView) row.findViewById(R.id.shared_by_name)).setTextColor(Color.BLACK);//akshit Code
                    ((TextView) row.findViewById(R.id.shared_by_name)).setText(splitted[0]);
                    ((TextView) row.findViewById(R.id.shared_message)).setText(" wants to share");


                }

                // Mukesh share audio layout
                if (!Utils.isEmptyString(temp.content_url) && Utils.isEmptyString(temp.video_thumb) && Utils.isEmptyString(temp.imageRatio)) {
                    RelativeLayout mRelative = (RelativeLayout) row.findViewById(R.id.temp_layout);
                    mRelative.setVisibility(View.VISIBLE);
                    mRelative.setBackgroundColor(Color.WHITE);//akshit code

                    ImageView mAudioImage = (ImageView) row.findViewById(R.id.iv_play_btn);
                    mAudioImage.setVisibility(View.VISIBLE);

                    ImageView mSpeakerImage = (ImageView) row.findViewById(R.id.iv_play_btn_);
                    mSpeakerImage.setVisibility(View.VISIBLE);
                }
                ((ImageView) row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
            }

        }//end of share view at reciver side




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
                if (Utils.isEmptyString(item.isAccepted)) { //it means no action was taken on share request-monika
                    Intent i = new Intent(context, ChatRecordView.class);
                    //not required these params at all-monika
                    i.putExtra("originalChatId", item.originalMessageID);
                    i.putExtra("textMsg", "SHARING DENIED");
                    i.putExtra("caption", item.shareComment);
                    i.putExtra("facebookToken", item.facebookToken);
                    i.putExtra("sharingMedia", item.sharingMedia);

                    //update the shareStatus value for current chat item

                    item.isAccepted = "no";
                    i.putExtra("shareStatus", "shareRejected");
                    i.putExtra("isAccepted", "no");
                    i.putExtra("chatType", item.chatType);
                    i.putExtra("isMessageSender", item.isMessageSender);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.setAction("SHARE");
                    context.startActivity(i);

                    ChatManager chatManager = ModelManager.getInstance().getChatManager();
                    chatManager.chatShare(authM.getPhoneNo(), authM.getUsrToken(), item.relationshipId, item.originalMessageID, item.sharingMedia, item.facebookToken, item.shareComment, "no");

                    //akshit code to change images to deactive state
                    ((TextView) row.findViewById(R.id.shared_message_reject)).setBackgroundResource(R.drawable.c_card_reject_deactive);
                    ((TextView) row.findViewById(R.id.shared_message_accept)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
                    //ends

                    /* code to play sound */
                    Utils.playSound((Activity) getContext(), R.raw.message_sent);


                }
            }
        });
        // Click Action On Share With ACCEPT-mukesh
        ((TextView) row.findViewById(R.id.shared_message_accept)).setTag(position);

        if (!Utils.isEmptyString(temp.isAccepted))//akshit code to change images to deactivate state
        {
            ((TextView) row.findViewById(R.id.shared_message_reject)).setBackgroundResource(R.drawable.c_card_reject_deactive);
            ((TextView) row.findViewById(R.id.shared_message_accept)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
        }//akshit code ends

        ((TextView) row.findViewById(R.id.shared_message_accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthManager authM = ModelManager.getInstance().getAuthorizationManager();
                int position = (Integer) v.getTag();
                ChatMessageBody item = ModelManager.getInstance().getChatManager().chatMessageList.get(position);
                if (Utils.isEmptyString(item.isAccepted)) { //it means no action was taken on share request-monika
                    Intent i = new Intent(context, ChatRecordView.class);

                    i.putExtra("originalChatId", item.originalMessageID);

                    i.putExtra("textMsg", "SHARED");
                    i.putExtra("caption", item.shareComment);
                    i.putExtra("facebookToken", item.facebookToken);
                    i.putExtra("sharingMedia", item.sharingMedia);
                    //update value in current chat item

                    item.isAccepted = "yes";
                    i.putExtra("shareStatus", "shareAccepted");
                    i.putExtra("isAccepted", "yes");
                    i.putExtra("chatType", item.chatType);
                    i.putExtra("isMessageSender", item.isMessageSender);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.setAction("SHARE");
                    context.startActivity(i);

                    ChatManager chatManager = ModelManager.getInstance().getChatManager();
                    chatManager.chatShare(authM.getPhoneNo(), authM.getUsrToken(), item.relationshipId, item.originalMessageID, item.sharingMedia, item.facebookToken, item.shareComment, "yes");

                    //akshit code to set image to deactivate state .
                    ((TextView) row.findViewById(R.id.shared_message_reject)).setBackgroundResource(R.drawable.c_card_reject_deactive);
                    ((TextView) row.findViewById(R.id.shared_message_accept)).setBackgroundResource(R.drawable.c_card_accepted_deactive);
                    //ends

                    /* code to play sound */
                    Utils.playSound((Activity) getContext(), R.raw.message_sent);
                }
            }
        });
        return row;
    }

    private void playmedia(int position) {
        ChatMessageBody item = ModelManager.getInstance().getChatManager().chatMessageList.get(position);
        if (!(Utils.isEmptyString(item.location_coordinates))) {


            String coordinates = item.location_coordinates;

            Intent intent = new Intent(context, MapView.class);
            intent.putExtra("from", "chatrecord");
            intent.putExtra("coordinates", coordinates);
            try {
                context.startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (!(Utils.isEmptyString(item.imageRatio))) {


            Uri uri = Uri.parse(item.content_url);
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setDataAndType(uri, "image/*");
            try {
                context.startActivity(it);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!Utils.isEmptyString(item.video_thumb)) {

            Utils.mName = item.chatId;


            String mPath = Utils.mVideoPath + Utils.mName + ".mp4";
            Uri uri = Utils.getVideoContentUri(context, new File(mPath));  //check file exist or not
            if (Utils.isEmptyString("" + uri)) {  // dowonload video

                if (Utils.isConnectingToInternet((Activity) context))
                    path = Utils.mVideoPath;
                new DownloadMusicfromInternet().execute(item.content_url);
            } else {  // play video
                Utils.playvideo(context, uri.toString());
            }


        } else if (!Utils.isEmptyString(item.content_url) && Utils.isEmptyString(item.video_thumb)) {


            Utils.mName = item.chatId; // set Audio name
            String mPath = Utils.mAudioPath + Utils.mName + ".mp4";
            Uri uri = Utils.getAudioContentUri(context, new File(mPath));  //check file exist or not
            if (Utils.isEmptyString("" + uri)) {  // dowonload Audio
                path = Utils.mAudioPath;
                if (Utils.isConnectingToInternet((Activity) context))
                    new DownloadMusicfromInternet().execute(item.content_url);
            } else {  // play Audio
                Utils.playvideo(context, uri.toString());
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
        i.putExtra("card_owner", item.card_owner);
        i.putExtra("chat_id", item.chatId); //update the value to "played" only when user actually counter the card
        //from card- its in case of counter case only-monika
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
        //    i.putExtra("chatType", item.chatType);
        i.putExtra("clicks", item.clicks);
        i.putExtra("textMsg", item.textMsg);
        i.putExtra("originalChatId", item.chatId);
        i.putExtra("isMessageSender", isMessageSender);

        if (!Utils.isEmptyString(item.imageRatio)) {
            i.putExtra("imageRatio", item.imageRatio);
            i.putExtra("fileId", item.content_url);
            if (!Utils.isEmptyString(item.location_coordinates)) {
                i.putExtra("location_coordinates", item.location_coordinates);
            }
        } else if (!Utils.isEmptyString(item.video_thumb)) {
            i.putExtra("videoThumbnail", item.video_thumb);
            i.putExtra("videoID", item.content_url);
        } else if (!Utils.isEmptyString(item.card_originator)) {
            i.putExtra("card_Accepted_Rejected", item.card_Accepted_Rejected);
            i.putExtra("card_DB_ID", item.card_DB_ID);
            i.putExtra("card_id", item.card_id);
            i.putExtra("card_Played_Countered", item.card_Played_Countered);
            i.putExtra("card_clicks", item.clicks);
            i.putExtra("card_content", item.card_content);
            i.putExtra("card_heading", item.card_heading);
            i.putExtra("card_originator", item.card_originator);
            i.putExtra("card_owner", item.card_owner);
            i.putExtra("card_url", item.card_url);
            i.putExtra("is_CustomCard", item.is_CustomCard);


        } else if (!Utils.isEmptyString(item.content_url) && Utils.isEmptyString(item.imageRatio) && Utils.isEmptyString(item.video_thumb)) {
            i.putExtra("audioID", item.content_url);
        }

        i.putExtra("chatType", item.chatType);
        i.setClass(context, ViewShare.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


        context.startActivity(i);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public class DownloadMusicfromInternet extends AsyncTask<String, String, String> { // Async task to download video

        // default video path
        String mPath;
        File mFile;
        URLConnection conection = null;
        OutputStream output = null;
        InputStream input = null;
        int precentage;
        private ProgressDialog prgDialog;

        // Show Progress bar before downloading Music
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgDialog = new ProgressDialog(context);
            prgDialog.setMessage("Downloading file. Please wait...");
            prgDialog.setIndeterminate(false);
            prgDialog.setMax(100);
            prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            prgDialog.setCancelable(true);
            prgDialog.show();

        }

        // Download Music File from Internet
        @Override
        protected String doInBackground(final String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                prgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (output != null && input != null) {
                            try {
                                cancel(true);
                                String mPath = path + Utils.mName + ".mp4";
                                if (precentage != 100) {   // delate file if video/Audio download intrupted
                                    File file = new File(mPath);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    }
                });
                conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                input = new BufferedInputStream(url.openStream(), 10 * 1024);
                mFile = new File(path);
                if (!mFile.exists()) {
                    mFile.mkdir();
                }
                mFile.setWritable(true);
                mFile.setReadable(true);

                mPath = mFile.getAbsolutePath() + "/" + Utils.mName + ".mp4";
                output = new FileOutputStream(mPath);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {

            }
            return null;
        }

        // While Downloading Music File
        protected void onProgressUpdate(String... progress) {
            prgDialog.setProgress(Integer.parseInt(progress[0]));
            precentage = Integer.parseInt(progress[0]);
        }

        // Once Music File is downloaded
        @Override
        protected void onPostExecute(String file_url) {


            if (precentage == 100) {
                /* to mount media in gallery */
                if (path.contains(Utils.mAudioPath)) {  // code to mount Audio


                    // add new file to your media library
                    ContentValues values = new ContentValues();
                    long current = System.currentTimeMillis();
                    values.put(MediaStore.Audio.Media.TITLE, "audio" + Utils.mName);
                    values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4");
                    values.put(MediaStore.Audio.Media.DATA, mFile.getAbsolutePath());
                    ContentResolver contentResolver = context.getContentResolver();
                    Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    Uri newUri = contentResolver.insert(base, values);

// Notifiy the media application on the device
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));

                    /* play video */
                    Uri uri = Utils.getAudioContentUri(context, new File(mPath));  //check file exist or not

                    Utils.playvideo(context, uri.toString());

                } else if (path.contains(Utils.mVideoPath)) {  // code to mount Video

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.DATA, mPath);
                    values.put(MediaStore.Video.Media.DATE_TAKEN, mFile.lastModified());
                    Uri mImageCaptureUri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values); // to notify change
                    context.getContentResolver().notifyChange(Uri.parse(mPath), null);
                    /* play video */
                    Uri uri = Utils.getVideoContentUri(context, new File(mPath));  //check file exist or not

                    Utils.playvideo(context, uri.toString());
                }


                prgDialog.dismiss();
            } else {
                String mPath = path + Utils.mName + ".mp4";
                File file = new File(mPath);
                if (file.exists()) {
                    file.delete();
                }

            }

        }
    }

    private int pxlToDp(int pixel) {

        final float scale = context.getResources().getDisplayMetrics().density;
        int dp = (int) (pixel * scale + 0.5f);
        return dp;
    }
}