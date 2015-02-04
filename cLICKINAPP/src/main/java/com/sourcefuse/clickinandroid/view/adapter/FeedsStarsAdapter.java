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
                /* prafull code */

                if (eachNewsFeed.get(position).getIs_user_follower() == 1) {//1 means there is possibility of some relation-follower or following
                    if (eachNewsFeed.get(position).getIs_user_follower_acceptance() != null &&
                            eachNewsFeed.get(position).getIs_user_follower_acceptance().equalsIgnoreCase("true")) { // follow user if acceptance is true
                        Log.e("user follow-------->", "" + eachNewsFeed.get(position).getUserName());
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        holder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);
                    } else if (eachNewsFeed.get(position).getIs_user_follower_acceptance() == null) {   // requested if user if acceptance is null
                        Log.e("user requested-------->", "" + eachNewsFeed.get(position).getUserName());
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        holder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                    }
                }
                if (eachNewsFeed.get(position).getIs_user_following() == 1) {//1 means there is possibility of some relation-follower or following
                    if (eachNewsFeed.get(position).getIs_user_following_acceptance() != null
                            && eachNewsFeed.get(position).getIs_user_following_acceptance().equalsIgnoreCase("true")) { // following if user if acceptance is null
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        Log.e("user following-------->", "" + eachNewsFeed.get(position).getUserName());
                        holder.reqbtn.setBackgroundResource(R.drawable.following_btn);
                    } else if (eachNewsFeed.get(position).getIs_user_following_acceptance() == null) { // requested if user if acceptance is null
                        Log.e("user requested-------->", "" + eachNewsFeed.get(position).getUserName());
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        holder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                    }
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
