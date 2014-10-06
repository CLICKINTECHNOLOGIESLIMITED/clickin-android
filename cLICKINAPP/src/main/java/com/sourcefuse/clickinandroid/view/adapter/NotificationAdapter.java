package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.NotificationBean;
import com.sourcefuse.clickinapp.R;

import java.util.List;

/**
 * Created by mukesh on 3/7/14.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationBean> {
    private static final String TAG = NotificationAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;

    public NotificationAdapter(Context context, int layoutResourceId,
                           List<NotificationBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NotificationBean item = getItem(position);
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.notificationMsg = (TextView) row.findViewById(R.id.tv_notification_msg);
            holder.notificationType = (ImageView) row.findViewById(R.id.iv_notification_type);
            holder.notificationType.setScaleType(ImageView.ScaleType.FIT_XY);


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        RecordHolder rholder = (RecordHolder) row.getTag();


        if(item.getNotificationType().matches(context.getResources().getString(R.string.txt_relationrequest))){
            rholder.notificationType.setBackgroundResource(R.drawable.c_noti_request);
        }else  if(item.getNotificationType().matches(context.getResources().getString(R.string.txt_relationstatus))){
            rholder.notificationType.setBackgroundResource(R.drawable.c_noti_relationstatus);
        }else  if(item.getNotificationType().matches(context.getResources().getString(R.string.txt_relationdelete))){
           // rholder.notificationType.setBackgroundResource(R.drawable.c_noti_request);
        }else{
            rholder.notificationType.setBackgroundResource(R.drawable.c_noti_request);
        }


        rholder.notificationMsg.setText(item.getNotificationMsg());


        return row;
    }

    static class RecordHolder {
        TextView notificationMsg;
        ImageView notificationType;
    }




}