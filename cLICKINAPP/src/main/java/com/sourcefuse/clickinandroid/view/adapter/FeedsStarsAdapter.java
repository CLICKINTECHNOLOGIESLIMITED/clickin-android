package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.bean.FeedStarsBean;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import static com.sourcefuse.clickinapp.R.drawable.report_icon;

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
            holder.reqbtn = (Button) row.findViewById(R.id.btn_actions);


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final RecordHolder rholder = (RecordHolder) row.getTag();
        authMgr = ModelManager.getInstance().getAuthorizationManager();

        holder.usr_name.setText(eachNewsFeed.get(position).getUserName());
        Picasso.with(context).load(eachNewsFeed.get(position).getUserPic()).into(holder.usrimg);
        ProfileManager prMgr = ModelManager.getInstance().getProfileManager();
        if(prMgr.following!=null) {
            Log.e("followRequesed", "" + prMgr.following.size());
            for (int i = 0; i < prMgr.following.size();i++)
            {
                Log.e("getUserId",""+eachNewsFeed.get(position).getUserId());
                Log.e("getFollowingId",""+prMgr.following.get(i).getFolloweeName());
                if(eachNewsFeed.get(position).getUserId().equalsIgnoreCase(prMgr.following.get(i).getFolloweeId()))
                {
                    if(prMgr.following.get(i).getAccepted().equalsIgnoreCase("true"))
                    {
                        holder.reqbtn.setBackgroundResource(R.drawable.following);
                    }
                    else {
                        holder.reqbtn.setBackgroundResource(R.drawable.requested_grey);
                    }
                    break;
                }
            }
        }
//        if(prMgr.pfollowerList!=null) {
//            for (int i = 0; i < prMgr.pfollowerList.size();i++)
//            {
//                if(eachNewsFeed.get(position).getUserId().equalsIgnoreCase(prMgr.pfollowerList.get(i).getFollowingId()))
//                {
//                    holder.reqbtn.setBackgroundResource(R.drawable.following);
//                    break;
//                }
//            }
//        }


        return row;
    }


    static class RecordHolder {
        ImageView usrimg;
//        ImageView reqbtn;
        TextView usr_name;
        Button reqbtn;
    }


}
