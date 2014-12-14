package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.FeedStarsBean;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.sourcefuse.clickinapp.R.id.time_comments;

public class FeedsCommentsAdapter extends ArrayAdapter<FeedStarsBean> {
    Context context;
    int layoutResourceId;
    ArrayList<FeedStarsBean> eachNewsFeed;
    MediaPlayer player;
    AuthManager authMgr;

    public FeedsCommentsAdapter(Context context, int layoutResourceId,
                                ArrayList<FeedStarsBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.eachNewsFeed = item;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final RecordHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.usr_name = (TextView) row
                    .findViewById(R.id.tv_clickers_name);
            holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
            holder.usrimg.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.time = (TextView) row.findViewById(time_comments);
            holder.comment = (TextView) row.findViewById(R.id.tv_clickers_comment);


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final RecordHolder rholder = (RecordHolder) row.getTag();
        authMgr = ModelManager.getInstance().getAuthorizationManager();

        holder.usr_name.setText(eachNewsFeed.get(position).getUserName());
        if (!eachNewsFeed.get(position).getUserPic().equalsIgnoreCase("")) {
            try {
                Picasso.with(context).load(eachNewsFeed.get(position).getUserPic()).skipMemoryCache().error(R.drawable.male_user).into(holder.usrimg);
            } catch (Exception e) {
                holder.usrimg.setImageResource(R.drawable.male_user);
            }
        } else {
            holder.usrimg.setImageResource(R.drawable.male_user);
        }
        holder.comment.setText(eachNewsFeed.get(position).getComment());
        String time = eachNewsFeed.get(position).getcreated_sec();
        if (time.length() == 10)
            holder.time.setText(Utils.getLocalDatefromTimestamp(Long.parseLong(time) * 1000));
        else
            holder.time.setText(Utils.getLocalDatefromTimestamp(Long.parseLong(time)));

        return row;
    }


    static class RecordHolder {
        ImageView usrimg;
        TextView time;
        TextView usr_name, comment;

    }


}
