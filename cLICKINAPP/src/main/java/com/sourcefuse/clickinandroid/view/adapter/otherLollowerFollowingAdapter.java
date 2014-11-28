package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FollowingListView;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mukesh on 24/11/14.
 */
public class otherLollowerFollowingAdapter extends ArrayAdapter<FollowerFollowingBean> {
    private static final String TAG = FollowingAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    RecordHolder rholder;
    List<FollowerFollowingBean> item;


    public otherLollowerFollowingAdapter(Context context, int layoutResourceId,
                            List<FollowerFollowingBean> item1) {
        super(context, layoutResourceId, item1);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.item = item1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();



            holder.usr_name = (TextView) row.findViewById(R.id.tv_clickers_name_other);
            holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr_other);
            holder.usrimg.setScaleType(ImageView.ScaleType.FIT_XY);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        rholder = (RecordHolder) row.getTag();




        rholder.usr_name.setText(item.get(position).getFolloweeName());
        if (!item.get(position).getFolloweePic().equalsIgnoreCase("")) {
            try {
                Picasso.with(context).load(item.get(position).getFolloweePic())
                        .skipMemoryCache()
                        .error(R.drawable.male_user)
                        .into(rholder.usrimg);
            } catch (Exception e) {
                holder.usrimg.setImageResource(R.drawable.male_user);
            }
        } else {
            holder.usrimg.setImageResource(R.drawable.male_user);
        }


        return row;
    }

    static class RecordHolder {
        TextView usr_name;
        ImageView usrimg;
    }



}