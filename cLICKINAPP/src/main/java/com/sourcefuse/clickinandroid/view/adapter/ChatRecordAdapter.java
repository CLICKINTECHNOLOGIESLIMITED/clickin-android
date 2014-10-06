package com.sourcefuse.clickinandroid.view.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatRecordAdapter extends ArrayAdapter<ChatRecordBeen> {

    private static final String TAG = ChatRecordAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    private AuthManager authManager;

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
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();

            holder.rrMainLayout = (RelativeLayout) row.findViewById(R.id.rr_main_l);
            holder.rlTimeStatusSender = (RelativeLayout) row.findViewById(R.id.rl_time_sender);
            holder.chatParentLayout = (RelativeLayout) row.findViewById(R.id.chat_parent_layout);
            holder.llSrTime = (LinearLayout) row.findViewById(R.id.ll_sr_time);
            holder.clicksArea = (LinearLayout) row.findViewById(R.id.clicks_area);

            holder.chatImage = (ImageView) row.findViewById(R.id.iv_chat_image);
            holder.chatImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.shareIcon = (ImageView) row.findViewById(R.id.iv_type_two_share_icon_r);
            holder.sendStatus = (ImageView) row.findViewById(R.id.iv_send_status);
            holder.clicksHeart = (ImageView) row.findViewById(R.id.iv_clicks_heart);

            holder.chatText = (TextView) row.findViewById(R.id.chat_text);
            holder.timeText = (TextView) row.findViewById(R.id.tv_time_text);
            holder.clicksText = (TextView) row.findViewById(R.id.clicks_text);


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        RecordHolder rholder = (RecordHolder) row.getTag();
        authManager = ModelManager.getInstance().getAuthorizationManager();


        if (item.getChatType().equals("1")) {
            if (item.getRecieverQbId().equalsIgnoreCase(authManager.getQBId())) {
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

                if(item.getClicks().equalsIgnoreCase("no")){
                    holder.chatImage.setVisibility(View.GONE);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.clicksArea.setBackgroundResource(R.drawable.whitechatbg);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    holder.chatText.setText(""+item.getChatText());
                    holder.timeText.setText(""+getCurrentTime().toUpperCase());
                }else{
                    holder.chatImage.setVisibility(View.GONE);
                    holder.shareIcon.setVisibility(View.VISIBLE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_r_bgpink);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksText.setText("" + item.getClicks());
                    holder.clicksHeart.setVisibility(View.VISIBLE);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    if(item.getClicks().equalsIgnoreCase(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                        holder.chatText.setText(""+item.getChatText().substring(8));
                    }
                    holder.timeText.setText(""+getCurrentTime().toUpperCase());
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

                if(item.getClicks().equalsIgnoreCase("no")) {
                    holder.chatImage.setVisibility(View.GONE);
                    holder.shareIcon.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);
                    holder.clicksArea.setBackgroundResource(R.drawable.newbg);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.black));
                    holder.chatText.setText(""+item.getChatText());
                    holder.timeText.setText(""+getCurrentTime().toUpperCase());
                }else{
                    holder.chatImage.setVisibility(View.GONE);
                    holder.shareIcon.setVisibility(View.VISIBLE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_s_bgpink);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksText.setText("" + item.getClicks());
                    holder.clicksHeart.setVisibility(View.VISIBLE);
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    holder.chatText.setText(""+item.getChatText());
                    holder.timeText.setText(""+getCurrentTime().toUpperCase());
                }

            }
        } else if (item.getChatType().equals("2")) {

            if (item.getRecieverQbId().equalsIgnoreCase(authManager.getQBId())) {
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

                if(item.getClicks().equalsIgnoreCase("no") && Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.whitechatbg);
                    holder.shareIcon.setVisibility(View.VISIBLE);
                    holder.clicksArea.setVisibility(View.GONE);
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);

                    Picasso.with(context).load(item.getChatImageUrl())
                           .into(holder.chatImage);

                }if(item.getClicks().equalsIgnoreCase("no") && !Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_white_bg);
                    holder.shareIcon.setVisibility(View.VISIBLE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.whitechatbg);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.clicksText.setVisibility(View.GONE);
                    holder.clicksHeart.setVisibility(View.GONE);


                    Picasso.with(context).load(item.getChatImageUrl())
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.default_profile).into(holder.chatImage);

                }if(!item.getClicks().equalsIgnoreCase("no") && !Utils.isEmptyString(item.getChatText())) {
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_pink_bg);
                    holder.shareIcon.setVisibility(View.VISIBLE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_r_bgpink);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    if(item.getClicks().equalsIgnoreCase(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                        holder.chatText.setText(""+Utils.lineBreacker(item.getChatText().substring(8)));
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

                if(item.getClicks().equalsIgnoreCase("no")) {
                    Log.e(TAG, "" + "Type 22 - No Click");

                    if(item.getClicks().equalsIgnoreCase("no") && Utils.isEmptyString(item.getChatText())) {

                        holder.chatImage.setVisibility(View.VISIBLE);
                        holder.shareIcon.setVisibility(View.VISIBLE);
                        holder.clicksArea.setVisibility(View.GONE);
                        holder.sendStatus.setVisibility(View.GONE);
                        holder.clicksText.setVisibility(View.GONE);
                        holder.clicksHeart.setVisibility(View.GONE);
                        holder.chatImage.setBackgroundResource(R.drawable.newbg);
                        Picasso.with(context).load(item.getChatImageUrl())
                                .into(holder.chatImage);
                        holder.timeText.setText("" + getCurrentTime().toUpperCase());

                    } else if(item.getClicks().equalsIgnoreCase("no") && !Utils.isEmptyString(item.getChatText())) {

                        holder.chatImage.setVisibility(View.VISIBLE);
                        holder.chatImage.setBackgroundResource(R.drawable.chat_image_white_bg);
                        holder.shareIcon.setVisibility(View.VISIBLE);
                        holder.clicksArea.setVisibility(View.VISIBLE);
                        holder.sendStatus.setVisibility(View.GONE);
                        holder.clicksText.setVisibility(View.GONE);
                        holder.clicksHeart.setVisibility(View.GONE);
                        holder.chatText.setVisibility(View.VISIBLE);
                        holder.chatText.setText(""+Utils.lineBreacker(item.getChatText()));
                        holder.clicksArea.setBackgroundResource(R.drawable.newbg);
                        Picasso.with(context).load(item.getChatImageUrl())
                                .into(holder.chatImage);
                        holder.timeText.setText("" + getCurrentTime().toUpperCase());
                    }
                }else if(!item.getClicks().equalsIgnoreCase("no") && !Utils.isEmptyString(item.getChatText()) ){

                    Log.e(TAG, "" + "Type 22 - Click");
                    holder.chatImage.setVisibility(View.VISIBLE);
                    holder.chatImage.setBackgroundResource(R.drawable.chat_image_pink_bg);
                    holder.shareIcon.setVisibility(View.VISIBLE);
                    holder.clicksArea.setVisibility(View.VISIBLE);
                    holder.clicksArea.setBackgroundResource(R.drawable.c_clicks_r_bgpink);
                    holder.chatText.setVisibility(View.VISIBLE);
                    holder.clicksText.setVisibility(View.VISIBLE);
                    holder.clicksHeart.setVisibility(View.VISIBLE);

                    if(item.getClicks().equalsIgnoreCase(item.getChatText())){
                        holder.chatText.setText("");
                    }else{
                        holder.chatText.setText(""+Utils.lineBreacker(item.getChatText().substring(8)));
                    }
                    holder.clicksText.setText(""+item.getClicks());
                    holder.chatText.setTextColor(context.getResources().getColor(R.color.white));
                    holder.sendStatus.setVisibility(View.GONE);
                    holder.timeText.setText(""+getCurrentTime().toUpperCase());
                    Picasso.with(context).load(item.getChatImageUrl())
                            .into(holder.chatImage);
                }

            }


        }

        return row;
    }

    static class RecordHolder {

        RelativeLayout rrMainLayout, rlTimeStatusSender,chatParentLayout;
        LinearLayout llSrTime,clicksArea;
        ImageView chatImage, shareIcon, sendStatus,clicksHeart;
        TextView chatText, timeText,clicksText;


    }

    public Html.ImageGetter imgGetter = new Html.ImageGetter() {

        public Drawable getDrawable(String source) {
            Drawable drawable = null;

            drawable = context.getResources().getDrawable(R.drawable.check);

            drawable.setBounds(10, 10, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            return drawable;
        }
    };

    public  String getCurrentTime(){
        DateFormat df = new SimpleDateFormat("hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }


}

