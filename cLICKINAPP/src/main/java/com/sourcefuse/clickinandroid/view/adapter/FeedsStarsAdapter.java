package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.bean.FeedStarsBean;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedsStarsAdapter extends ArrayAdapter<FeedStarsBean> {
    Context context;
    int layoutResourceId;
    ArrayList<FeedStarsBean> eachNewsFeed;
    MediaPlayer player;
    AuthManager authMgr;

    public FeedsStarsAdapter(Context context, int layoutResourceId,
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
            holder.reqbtn = (TextView) row.findViewById(R.id.btn_actions);


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final RecordHolder rholder = (RecordHolder) row.getTag();
        authMgr = ModelManager.getInstance().getAuthorizationManager();

        holder.usr_name.setText(eachNewsFeed.get(position).getUserName());
        if (!eachNewsFeed.get(position).getUserPic().equalsIgnoreCase("")) {
            try {
                Picasso.with(context).load(eachNewsFeed.get(position).getUserPic()).error(R.drawable.male_user).into(holder.usrimg);
            } catch (Exception e) {
                holder.usrimg.setImageResource(R.drawable.male_user);
            }
        } else {
            holder.usrimg.setImageResource(R.drawable.male_user);
        }
        ProfileManager prMgr = ModelManager.getInstance().getProfileManager();
        if (prMgr.following != null) {

            if (eachNewsFeed.get(position).getUserId().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserId())) {
                holder.reqbtn.setVisibility(View.GONE);
            } else {

                if (eachNewsFeed.get(position).getIs_user_following() == 0 && eachNewsFeed.get(position).getIs_user_follower() == 0
                        && eachNewsFeed.get(position).getIs_user_in_relation() == 0 && eachNewsFeed.get(position).getIs_user_in_relation_acceptance() == null
                        && eachNewsFeed.get(position).getIs_user_follower_acceptance() == null
                        && eachNewsFeed.get(position).getIs_user_following_acceptance() == null) { // in this case no followers and following and  no relation with start user
                    holder.reqbtn.setVisibility(View.GONE);
                    holder.reqbtn.setBackgroundResource(0);
                } else if (eachNewsFeed.get(position).getIs_user_following() == 1 && eachNewsFeed.get(position).getIs_user_follower() == 1
                        && eachNewsFeed.get(position).getIs_user_following_acceptance() != null &&
                        eachNewsFeed.get(position).getIs_user_in_relation_acceptance() != null) { // user in relation and may be not in relation but following user
                    holder.reqbtn.setVisibility(View.VISIBLE);
                    holder.reqbtn.setBackgroundResource(R.drawable.following_btn);
                } else if (eachNewsFeed.get(position).getIs_user_following() == 0 && eachNewsFeed.get(position).getIs_user_follower() == 1
                        ) {   // user is in relation and follow button
                    holder.reqbtn.setVisibility(View.VISIBLE);
                    holder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);
                } else if (eachNewsFeed.get(position).getIs_user_in_relation_acceptance() == null) { // requested user to follow
                    holder.reqbtn.setVisibility(View.VISIBLE);
                    holder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                }


            }

        }


        return row;
    }


    static class RecordHolder {
        ImageView usrimg;
        TextView reqbtn;//akshit code
        TextView usr_name;
//        Button reqbtn;
    }


}
