package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.CurrentClickerBean;
import com.sourcefuse.clickinandroid.view.CurrentClickersView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CurrentClickersAdapter extends ArrayAdapter<CurrentClickerBean> {
    Context context;
    int layoutResourceId;
    private AuthManager authManager;
    private RelationManager relationManager;
    private ProfileManager profilemanager;


    public CurrentClickersAdapter(Context context, int layoutResourceId,
                                  List<CurrentClickerBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        final CurrentClickerBean item = getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.clickers = (TextView) row.findViewById(R.id.tv_clickers);
            holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
            holder.follow = (TextView) row.findViewById(R.id.btn_follow);
            holder.whiteDivider = (TextView) row.findViewById(R.id.tv_white);
            profilemanager = ModelManager.getInstance().getProfileManager();
            holder.usrimg.setScaleType(ImageView.ScaleType.FIT_XY);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final RecordHolder rholder = (RecordHolder) row.getTag();


        if (item.getFollow() == 0) {
            rholder.follow.setBackgroundResource(R.drawable.follow_pink);
        } else {
            rholder.follow.setBackgroundResource(R.drawable.requested_grey);
        }

        if (profilemanager.currentClickerList.size() == (position + 1)) {
            rholder.whiteDivider.setVisibility(View.GONE);
        } else {
            rholder.whiteDivider.setVisibility(View.VISIBLE);
        }

        holder.clickers.setText(item.getName());
        try {
            if (!item.getClickerPix().equalsIgnoreCase("")) {
                Picasso.with(context)
                        .load(item.getClickerPix())
                        .into(holder.usrimg);
            } else {
                // holder.usrimg.setImageResource(R.drawable.male_user);
            }
        } catch (Exception e) {
            holder.usrimg.setImageResource(R.drawable.male_user);
        }


        holder.follow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();

                if (item.getFollow() == 0) {
                    item.setFollow(1);
                    relationManager.followUser(item.getGetClickerPhone(), authManager.getPhoneNo(), authManager.getUsrToken());
                    rholder.follow.setBackgroundResource(R.drawable.requested_grey);
                    CurrentClickersView.followReqStatus = true;
                }

            }
        });

        return row;
    }

    static class RecordHolder {
        TextView clickers;
        ImageView usrimg;
        TextView follow;
        View whiteDivider;

    }
}

