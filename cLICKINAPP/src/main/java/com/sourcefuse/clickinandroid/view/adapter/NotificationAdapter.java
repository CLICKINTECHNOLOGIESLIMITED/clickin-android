package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.model.bean.NotificationBean;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FeedView;
import com.sourcefuse.clickinandroid.view.FollowerList;
import com.sourcefuse.clickinandroid.view.FollowingListView;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinandroid.view.PostView;
import com.sourcefuse.clickinandroid.view.UserProfileView;
import com.sourcefuse.clickinapp.R;

import java.io.File;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            holder.mNotificationLayout = (RelativeLayout) row.findViewById(R.id.notification_layout);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        RecordHolder rholder = (RecordHolder) row.getTag();

        if (item.getIs_read().equalsIgnoreCase("false")) {//akshit code to set color for unread notification
            holder.mNotificationLayout.setBackgroundResource(R.color.noti_unread);
        } else {
            holder.mNotificationLayout.setBackgroundResource(R.color.notification_background);
        }

        if (item.getNotificationType().matches(context.getResources().getString(R.string.txt_relation_visibility))) {
            rholder.notificationType.setBackgroundResource(R.drawable.ic_change_relationship);
        } else if (item.getNotificationType().matches(context.getResources().getString(R.string.txt_relationstatus))
                || item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.txt_relationrequest))) {
            rholder.notificationType.setBackgroundResource(R.drawable.ic_request_clickin);
        } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.txt_follow))
                || item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.follow_status))) {
            rholder.notificationType.setBackgroundResource(R.drawable.ic_follow);
        } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.type_share))) {
            rholder.notificationType.setBackgroundResource(R.drawable.c_noti_share);
        } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.txt_relationdelete))) {
            rholder.notificationType.setBackgroundResource(R.drawable.p_delete_relation);
        } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.starrred))) {
            rholder.notificationType.setBackgroundResource(R.drawable.c_noti_star);//akshit code
        } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.commented))) {
            rholder.notificationType.setBackgroundResource(R.drawable.c_noti_comment);//akshit code
        } else {
            rholder.notificationType.setBackgroundResource(R.drawable.ic_request_clickin);//akshit code added to set image other than ic_launcher
        }




/* no image for has ended relation ship */
        rholder.notificationMsg.setText(item.getNotificationMsg());
        holder.mNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.trackMixpanel((Activity) context, "ViewedNotificationName", item.getNotificationType(), "NotificationsOpened", false);//track notification through mixpanel

                if (item.getNotificationType().matches(context.getResources().getString(R.string.txt_relationstatus)) ||
                        item.getNotificationType().matches(context.getResources().getString(R.string.txt_relation_visibility))
                        || item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.txt_relationrequest)) ||
                        item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.txt_relationdelete))) {


                    Intent intent = new Intent(getContext(), UserProfileView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    ActivityManager am = (ActivityManager) context.getSystemService(android.content.Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                    String className = componentInfo.getClassName();

                    if (!className.equalsIgnoreCase("com.sourcefuse.clickinandroid.view.UserProfileView")) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    }
                    intent.putExtra("isChangeInList", true);
                    context.startActivity(intent);
                } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.txt_follow))) {
                    Intent intentFollower = new Intent(getContext(), FollowerList.class);
                    intentFollower.putExtra("FromOwnProfile", true);
                    context.startActivity(intentFollower);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.follow_status))) {
                    Intent intentFollowing = new Intent(getContext(), FollowingListView.class);
                    intentFollowing.putExtra("FromOwnProfile", true);
                    context.startActivity(intentFollowing);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.type_share))) {
                    Intent intent = new Intent(getContext(), FeedView.class);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.type_update))) {


                    RelationManager relationManager = ModelManager.getInstance().getRelationManager();
                    String ph = "";
                    for (int i = 0; i < relationManager.acceptedList.size(); i++) {
                        if (relationManager.acceptedList.get(i).getPartner_id().equalsIgnoreCase(item.update_user_id)) {
                            ph = relationManager.acceptedList.get(i).getPhoneNo();
                            Utils.deletePhoto(ph, context);
                        }
                    }

                    Intent intent = new Intent(getContext(), JumpOtherProfileView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("phNumber", ph);
                    intent.putExtra("FromOwnProfile", true);
                    context.startActivity(intent);


                    PicassoManager.setPicasso(context);
                    PicassoManager.clearCache();


                } else if (item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.star)) ||
                        item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.commented)) ||
                        item.getNotificationType().equalsIgnoreCase(context.getResources().getString(R.string.report))) {
                    Intent intent = new Intent(getContext(), PostView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("updatephoto", true);
                    intent.putExtra("feedId", item.newsfeed_id);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                }
            }
        });


        return row;
    }

    static class RecordHolder {
        TextView notificationMsg;
        ImageView notificationType;
        RelativeLayout mNotificationLayout;
    }



}