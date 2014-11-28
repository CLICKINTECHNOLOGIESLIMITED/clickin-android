package com.sourcefuse.clickinandroid.view.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.ImageViewer;
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
        RelativeLayout parentChatLayout=(RelativeLayout)row.findViewById(R.id.chat_parent_layout);
        relationManager = ModelManager.getInstance().getRelationManager();


        if (temp.senderQbId.equalsIgnoreCase(oursQbId)) { //start of sender


            Log.e(TAG, "getView" + "getView");//SENDER


            RelativeLayout rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);

            LinearLayout chatClickTextLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area);



            //code to set time
            TextView timeView=(TextView)row.findViewById(R.id.tv_time_text);
            timeView.setText(Utils.getLocalDatefromTimestamp(Long.parseLong(temp.sentOn)));


            //temp code -for image
            if(!(Utils.isEmptyString(temp.imageRatio))){
                //set layout properties for image view
                LinearLayout media_layout=(LinearLayout)row.findViewById(R.id.media_layout);
                ImageView image_attached=(ImageView)row.findViewById(R.id.iv_chat_image);
                media_layout.setVisibility(View.VISIBLE);
                image_attached.setVisibility(View.VISIBLE);

                if(!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))){
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsrr.addRule(RelativeLayout.BELOW, R.id.media_layout);
                    chatClickTextLayout.setLayoutParams(paramsrr);
                }

                //code to set msg deilvery notification
                ImageView sendStatusView=(ImageView)row.findViewById(R.id.iv_send_status);
                if(!(Utils.isEmptyString(temp.isDelivered)) && temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENDING)) {
                    ((ProgressBar)row.findViewById(R.id.pb_loding)).setVisibility(View.VISIBLE);
                    ((ImageView)row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.GONE);
                   sendStatusView.setImageResource(R.drawable.r_single_tick);
                    //Uri tempUri=Uri.parse(temp.content_url);
                    Picasso.with(context).load(temp.content_url)
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.default_profile).into(image_attached);

                }else if(!(Utils.isEmptyString(temp.isDelivered))&& temp.isDelivered.equalsIgnoreCase(Constants.MSG_SENT)){
                    ((ProgressBar)row.findViewById(R.id.pb_loding)).setVisibility(View.GONE);
                    ((ImageView)row.findViewById(R.id.iv_type_two_share_icon_r)).setVisibility(View.VISIBLE);
                    sendStatusView.setImageResource(R.drawable.r_single_tick);
                    Picasso.with(context).load(temp.content_url)
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.default_profile).into(image_attached);
                }

                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                image_attached.setVisibility(View.VISIBLE);


            }else if(!Utils.isEmptyString(temp.content_url)){// //end of image loop-sender And Audio start

                    parentChatLayout.setBackgroundResource(R.drawable.audio_send);
                    ((ImageView)row.findViewById(R.id.iv_audio_play)).setVisibility(View.VISIBLE);

                    if (!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))) {
                        chatClickTextLayout.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        paramsrr.addRule(RelativeLayout.BELOW, R.id.iv_chat_image);
                        chatClickTextLayout.setLayoutParams(paramsrr);
                    }

                    //code to set msg deilvery notification
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
            }else if(!Utils.isEmptyString(temp.card_url)) {// //end of Audio loop-sender And Card start
                parentChatLayout.setBackgroundResource(R.drawable.newbg);
                ((RelativeLayout)row.findViewById(R.id.send_card_first_time)).setVisibility(View.VISIBLE);
               // ((LinearLayout)row.findViewById(R.id.card_recieve_first_time)).setVisibility(View.VISIBLE);

                ((TextView)row.findViewById(R.id.trd_clicks_top)).setText(temp.clicks);
                ((TextView)row.findViewById(R.id.trd_clicks_bottom)).setText(temp.clicks);

               // ((TextView)row.findViewById(R.id.card_partner_name)).setText(temp.clicks);

                ImageView trade_image =  (ImageView) row.findViewById(R.id.trade_image);
                trade_image.setVisibility(View.VISIBLE);
                String url_to_load = (temp.card_url).replaceFirst("cards\\/(\\d+)\\.jpg","cards\\/a\\/1080\\/$1\\.jpg");
                Picasso.with(context).load(url_to_load)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile).into(trade_image);
            }

        //only text-SENDER CASE
            if (!Utils.isEmptyString(temp.textMsg) && temp.clicks.equalsIgnoreCase("no")) {
                //  RelativeLayout textViewLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);
                chatClickTextLayout.setVisibility(View.VISIBLE);
                chatClickTextLayout.setBackgroundResource(R.drawable.newbg);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);

                TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                chatText.setVisibility(View.VISIBLE);
                chatText.setTextColor(context.getResources().getColor(R.color.black));
                //code to check for long chat text
                if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                    chatText.setText(temp.textMsg.substring(0, 14));
                    //temp code to set width of long chat text view
                    int layoutWidth = chatClickTextLayout.getWidth();
                    TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                    chatTextLong.setVisibility(View.VISIBLE);
                    chatTextLong.setWidth(layoutWidth);
                    chatTextLong.setText(temp.textMsg.substring(14));
                    chatTextLong.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    chatText.setText(temp.textMsg);
                }

            }

            //CLICKS AND TEXT- SENDER CASE
            if (!(temp.clicks.equalsIgnoreCase("no")) ) {
                chatClickTextLayout.setVisibility(View.VISIBLE);
                chatClickTextLayout.setBackgroundResource(R.drawable.c_clicks_s_bgpink);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);
                //  clicksArea.setBackgroundResource(R.drawable.c_clicks_s_bgpink);

                TextView clicksText = (TextView) row.findViewById(R.id.clicks_text);
                clicksText.setVisibility(View.VISIBLE);
                clicksText.setText(temp.clicks);
                clicksText.setTextColor(context.getResources().getColor(R.color.white));


                ImageView clicksHeart = (ImageView) row.findViewById(R.id.iv_clicks_heart);
                clicksHeart.setVisibility(View.VISIBLE);

                //check if only clicks is there
                if (!(Utils.isEmptyString(temp.textMsg))) {

                    TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.setTextColor(context.getResources().getColor(R.color.white));
                    if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                        chatText.setText(temp.textMsg.substring(0, 14));

                        //temp code to set width of long chat text view

                        TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                        chatTextLong.setVisibility(View.VISIBLE);

                        chatTextLong.setText(temp.textMsg.substring(14));
                        chatTextLong.setTextColor(context.getResources().getColor(R.color.white));
                    } else {
                        chatText.setText(temp.textMsg);
                    }
                }
            }//enf of click and text sender loop



        }//end of sender loop
        else {
            //RECEIVER START

            RelativeLayout rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);
            row.findViewById(R.id.iv_send_status).setVisibility(View.GONE);
             RelativeLayout chatParentLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);

            LinearLayout chatClickTextLayout = (LinearLayout) row.findViewById(R.id.parent_clicks_area);
            RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsrr.addRule(RelativeLayout.LEFT_OF, R.id.chat_parent_layout);
            paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.chat_parent_layout);
            rlTimeStatusSender.setLayoutParams(paramsrr);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            chatParentLayout.setLayoutParams(params);


            chatParentLayout.setGravity(Gravity.RIGHT);

            //code to set time
            TextView timeView=(TextView)row.findViewById(R.id.tv_time_text);
            timeView.setText(Utils.getLocalDatefromTimestamp(Long.parseLong(temp.sentOn)));

            //temp code -for image-receiver end
            if(!(Utils.isEmptyString(temp.imageRatio))){
                //set layout properties for image view
                LinearLayout media_layout=(LinearLayout)row.findViewById(R.id.media_layout);
                ImageView image_attached=(ImageView)row.findViewById(R.id.iv_chat_image);
                media_layout.setVisibility(View.VISIBLE);
                image_attached.setVisibility(View.VISIBLE);

                if(!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))){
                    chatClickTextLayout.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams paramsr2 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsr2.addRule(RelativeLayout.BELOW, R.id.media_layout);
                    chatClickTextLayout.setLayoutParams(paramsr2);
                }


                image_attached.setScaleType(ImageView.ScaleType.FIT_XY);
                image_attached.setVisibility(View.VISIBLE);
                Picasso.with(context).load(temp.content_url)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile).into(image_attached);

            }else if(!Utils.isEmptyString(temp.content_url)) {
                    chatParentLayout.setBackgroundResource(R.drawable.audio_recieve);
                    ((ImageView)row.findViewById(R.id.iv_audio_play)).setVisibility(View.VISIBLE);

                    if (!(Utils.isEmptyString(temp.textMsg)) || (!(temp.clicks.equalsIgnoreCase("no")))) {
                        chatClickTextLayout.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams paramsr2 = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        paramsr2.addRule(RelativeLayout.BELOW, R.id.iv_chat_image);
                        chatClickTextLayout.setLayoutParams(paramsr2);
                    }

            } else if(!Utils.isEmptyString(temp.card_url)) {
                parentChatLayout.setBackgroundResource(R.drawable.whitechatbg);
                ((RelativeLayout)row.findViewById(R.id.send_card_first_time)).setVisibility(View.VISIBLE);
                ((LinearLayout)row.findViewById(R.id.card_recieve_first_time)).setVisibility(View.VISIBLE);

                ((TextView)row.findViewById(R.id.trd_clicks_top)).setText(temp.clicks);
                ((TextView)row.findViewById(R.id.trd_clicks_bottom)).setText(temp.clicks);

                ((TextView)row.findViewById(R.id.card_partner_name)).setText(relationManager.getPartnerName);


                ImageView trade_image =  (ImageView) row.findViewById(R.id.trade_image);
                trade_image.setVisibility(View.VISIBLE);
                String url_to_load = (temp.card_url).replaceFirst("cards\\/(\\d+)\\.jpg","cards\\/a\\/1080\\/$1\\.jpg");
                Picasso.with(context).load(url_to_load)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile).into(trade_image);
            }


            //only text-RECEIVER CASE
            if ((!Utils.isEmptyString(temp.textMsg)) && temp.clicks.equalsIgnoreCase("no")) {
                chatClickTextLayout.setVisibility(View.VISIBLE);
                TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                chatClickTextLayout.setBackgroundResource(R.drawable.whitechatbg);
                clicksArea.setVisibility(View.VISIBLE);

                chatText.setVisibility(View.VISIBLE);
                chatText.setTextColor(context.getResources().getColor(R.color.black));
                //code to checck for long chat text
                if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                    chatText.setText(temp.textMsg.substring(0, 14));
                    TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                    //temp code to set width of long chat text view
                    int layoutWidth = chatClickTextLayout.getWidth();
                    chatTextLong.setWidth(layoutWidth);
                    chatTextLong.setVisibility(View.VISIBLE);
                    chatTextLong.setText(temp.textMsg.substring(14));
                    chatTextLong.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    chatText.setText(temp.textMsg);
                }

            }//end of text- receiver end

            //clicks and text- receiver end

            if (!(temp.clicks.equalsIgnoreCase("no")) ) {
                chatClickTextLayout.setVisibility(View.VISIBLE);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);
                chatClickTextLayout.setBackgroundResource(R.drawable.c_clicks_r_bgpink);

                TextView clicksText = (TextView) row.findViewById(R.id.clicks_text);
                clicksText.setVisibility(View.VISIBLE);
                clicksText.setText("" + temp.clicks);
                clicksText.setTextColor(context.getResources().getColor(R.color.white));


                ImageView clicksHeart = (ImageView) row.findViewById(R.id.iv_clicks_heart);
                clicksHeart.setVisibility(View.VISIBLE);

                //check if only clicks is there
                if (!(Utils.isEmptyString(temp.textMsg))) {

                    TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.setTextColor(context.getResources().getColor(R.color.white));
                    if (temp.textMsg.length() > Constants.CHAT_LENTH_LIMIT) {
                        chatText.setText(temp.textMsg.substring(0, 14));
                        //temp code to set width of long chat text view
                        int layoutWidth = chatClickTextLayout.getWidth();
                        TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                        chatTextLong.setVisibility(View.VISIBLE);
                        chatTextLong.setWidth(layoutWidth);
                        chatTextLong.setText(temp.textMsg.substring(14));
                        chatTextLong.setTextColor(context.getResources().getColor(R.color.white));
                    } else {
                        chatText.setText(temp.textMsg);
                    }
                }

            }//end of click and text -reciver end


        }//end of reciver


        ((ImageView)row.findViewById(R.id.iv_chat_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, ImageViewer.class);
                intent.putExtra("Url", temp.content_url);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


            }
        });

        ((ImageView)row.findViewById(R.id.iv_audio_play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Uri myUri = Uri.parse(temp.content_url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(myUri, "audio/*");
                context.startActivity(intent);


            }
        });

        return row;
    }


    /*static class RecordHolder {

        RelativeLayout rrMainLayout, rlTimeStatusSender,chatParentLayout,rl_acc_res_bg,rl_card;
        LinearLayout llSrTime,clicksArea,cardView,cardAction,cardViewCc,cardViewCards,ll_cc_action;
        ImageView chatImage, shareIcon, sendStatus,clicksHeart,audioView,playIcon ,tradeImage,iv_accept_card,iv_resect_card,iv_acc_rec,iv_card_counter,iv_again_counter_acc,iv_again_counter_rej,iv_type_two_share_icon_r;
        TextView chatText, timeText,clicksText,trdClicksBottom,trdClicksTop,card_partner_name,tv_counter_card,tv_acc_res_name,tv_acc_res_status,trd_clicks_top_ar,trd_clicks_bottom_ar,tv_counter_card_action,trd_custom_heading,trd_custom_ar_heading;


    }
*/
}
