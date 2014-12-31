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
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.bean.FeedStarsBean;
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
                Picasso.with(context).load(eachNewsFeed.get(position).getUserPic()).skipMemoryCache().error(R.drawable.male_user).into(holder.usrimg);
            } catch (Exception e) {
                holder.usrimg.setImageResource(R.drawable.male_user);
            }
        } else {
            holder.usrimg.setImageResource(R.drawable.male_user);
        }
        ProfileManager prMgr = ModelManager.getInstance().getProfileManager();
        if (prMgr.following != null) {
            android.util.Log.e("followRequesed", "" + prMgr.following.size());
//            for (int i = 0; i < prMgr.following.size();i++)
//            {
//                android.util.Log.e("getUserId",""+eachNewsFeed.get(position).getUserId());
//                android.util.Log.e("getFollowingId",""+prMgr.following.get(i).getFolloweeName());
//                if(eachNewsFeed.get(position).getUserId().equalsIgnoreCase(prMgr.following.get(i).getFolloweeId()))
//                {
//                    if(prMgr.following.get(i).getAccepted().equalsIgnoreCase("true"))
//                    {
//                        holder.reqbtn.setImageResource(R.drawable.following);
//                    }
//                    else {
//                        holder.reqbtn.setImageResource(R.drawable.requested_grey);
//                    }
//                    break;
//                }
            if (eachNewsFeed.get(position).getUserId().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserId())) {
                holder.reqbtn.setVisibility(View.GONE);
            } else if (eachNewsFeed.get(position).getIs_user_in_relation() == 1) {
                holder.reqbtn.setVisibility(View.VISIBLE);

                if (eachNewsFeed.get(position).getIs_user_following() == 1) {
                    if (eachNewsFeed.get(position).getIs_user_following_acceptance() != null) {
                        holder.reqbtn.setBackgroundResource(R.drawable.following_btn);
                    } else {
                        holder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                    }
                } else {
                    holder.reqbtn.setBackgroundResource(R.drawable.c_owner_grey_corss);
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
        TextView reqbtn;//akshit code
        TextView usr_name;
//        Button reqbtn;
    }


}
