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
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatRecordAdapter extends ArrayAdapter<ChatMessageBody>{

    private static final String TAG = ChatRecordAdapter.class.getSimpleName();
    Context context;

    private AuthManager authManager;
    ChatRecordBeen item ;
    ArrayList<ChatMessageBody>currentChatList;


    public ChatRecordAdapter(Context context, int layoutResourceId,
                             ArrayList<ChatMessageBody> chatList) {
        super(context, layoutResourceId, chatList);
        currentChatList=chatList;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessageBody temp=currentChatList.get(position);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.view_chat_demo, parent, false);
        String oursQbId=ModelManager.getInstance().getAuthorizationManager().getQBId();
        if(temp.senderQbId==oursQbId) { //start of sender


            Log.e(TAG,"getView"+"getView");//SENDER


            RelativeLayout rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);
         //   RelativeLayout chatParentLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);
            LinearLayout chatClickTextLayout=(LinearLayout)row.findViewById(R.id.parent_clicks_area);




            chatClickTextLayout.setVisibility(View.VISIBLE);


            //only text-SENDER CASE
            if (!Utils.isEmptyString(temp.textMsg) && temp.content_url == null && temp.clicks.equalsIgnoreCase("no") ) {
              //  RelativeLayout textViewLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);

                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);

                TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                chatText.setVisibility(View.VISIBLE);
                chatText.setTextColor(context.getResources().getColor(R.color.black));
                //code to checck for long chat text
                if(temp.textMsg.length()>Constants.CHAT_LENTH_LIMIT){
                    chatText.setText(temp.textMsg.substring(0,14));
                    TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                    chatTextLong.setVisibility(View.VISIBLE);
                    chatTextLong.setText(temp.textMsg.substring(14));
                    chatTextLong.setTextColor(context.getResources().getColor(R.color.black));
                }else{
                    chatText.setText(temp.textMsg);
                }

            }

            //CLICKS AND TEXT- SENDER CASE
            if (!(temp.clicks.equalsIgnoreCase("no")) && temp.content_url == null) {
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
                if(!(Utils.isEmptyString(temp.textMsg))) {

                    TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.setTextColor(context.getResources().getColor(R.color.white));
                    if(temp.textMsg.length()>Constants.CHAT_LENTH_LIMIT){
                        chatText.setText(temp.textMsg.substring(0,14));
                        TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                        chatTextLong.setVisibility(View.VISIBLE);
                        chatTextLong.setText(temp.textMsg.substring(14));
                        chatTextLong.setTextColor(context.getResources().getColor(R.color.white));
                    }else{
                        chatText.setText(temp.textMsg);
                    }
                }


            }

        }//end of sender loop
        else{
        //RECEIVER START

            RelativeLayout rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);
           // RelativeLayout chatParentLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);

            LinearLayout chatClickTextLayout=(LinearLayout)row.findViewById(R.id.parent_clicks_area);
            RelativeLayout.LayoutParams paramsrr = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsrr.addRule(RelativeLayout.LEFT_OF, R.id.parent_clicks_area);
            paramsrr.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.parent_clicks_area);
            rlTimeStatusSender.setLayoutParams(paramsrr);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            chatClickTextLayout.setLayoutParams(params);


            chatClickTextLayout.setGravity(Gravity.RIGHT);
            chatClickTextLayout.setVisibility(View.VISIBLE);

            //only text-RECEIVER CASE
            if ((!Utils.isEmptyString(temp.textMsg)) && temp.content_url == null  && temp.clicks.equalsIgnoreCase("no")) {

                TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                chatClickTextLayout.setBackgroundResource(R.drawable.whitechatbg);
                clicksArea.setVisibility(View.VISIBLE);

                chatText.setVisibility(View.VISIBLE);
                chatText.setTextColor(context.getResources().getColor(R.color.black));
                //code to checck for long chat text
                if(temp.textMsg.length()>Constants.CHAT_LENTH_LIMIT){
                    chatText.setText(temp.textMsg.substring(0,14));
                    TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                    chatTextLong.setVisibility(View.VISIBLE);
                    chatTextLong.setText(temp.textMsg.substring(14));
                    chatTextLong.setTextColor(context.getResources().getColor(R.color.black));
                }else{
                    chatText.setText(temp.textMsg);
                }

            }//end of text- receiver end

            //clicks and text- receiver end
            //temp code
            String clicks=temp.clicks;
            if (!(temp.clicks.equalsIgnoreCase("no")) && temp.content_url == null) {
                LinearLayout clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);
                clicksArea.setVisibility(View.VISIBLE);
                chatClickTextLayout.setBackgroundResource(R.drawable.c_clicks_r_bgpink);

                TextView clicksText = (TextView) row.findViewById(R.id.clicks_text);
                clicksText.setVisibility(View.VISIBLE);
                clicksText.setText(""+temp.clicks);
                clicksText.setTextColor(context.getResources().getColor(R.color.white));


                ImageView clicksHeart = (ImageView) row.findViewById(R.id.iv_clicks_heart);
                clicksHeart.setVisibility(View.VISIBLE);

                //check if only clicks is there
                if(!(Utils.isEmptyString(temp.textMsg))) {

                    TextView chatText = (TextView) row.findViewById(R.id.chat_text);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.setTextColor(context.getResources().getColor(R.color.white));
                    if(temp.textMsg.length()>Constants.CHAT_LENTH_LIMIT){
                        chatText.setText(temp.textMsg.substring(0,14));
                        TextView chatTextLong = (TextView) row.findViewById(R.id.long_chat_text);
                        chatTextLong.setVisibility(View.VISIBLE);
                        chatTextLong.setText(temp.textMsg.substring(14));
                        chatTextLong.setTextColor(context.getResources().getColor(R.color.white));
                    }else{
                        chatText.setText(temp.textMsg);
                    }
                }

            }//end of click and text -reciver end
        }//end of reciver
        //   authManager = ModelManager.getInstance().getAuthorizationManager();
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
