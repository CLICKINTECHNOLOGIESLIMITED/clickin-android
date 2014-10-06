package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.FetchUsersByNameBean;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mukesh on 2/7/14.
 */

public class SearchAdapter extends ArrayAdapter<FetchUsersByNameBean> {
    Context context;
    int layoutResourceId;

    public SearchAdapter(Context context, int layoutResourceId,
                           List<FetchUsersByNameBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FetchUsersByNameBean item = getItem(position);
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.usr_name = (TextView) row.findViewById(R.id.searchname);
            holder.user_address = (TextView) row.findViewById(R.id.tv_user_address);
            holder.usrimg = (ImageView) row.findViewById(R.id.userImage);
            holder.usrimg.setScaleType(ImageView.ScaleType.FIT_XY);


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        RecordHolder rholder = (RecordHolder) row.getTag();
        if(!Utils.isEmptyString(item.getCity()) && !Utils.isEmptyString(item.getCountry())) {
            holder.user_address.setText(item.getCity()+","+item.getCountry());
        }else if (Utils.isEmptyString(item.getCity())) {
            holder.user_address.setText(item.getCity());
        }else if (Utils.isEmptyString(item.getCountry())) {
            holder.user_address.setText(item.getCountry());
        }

        rholder.usr_name.setText(item.getName());
        Picasso.with(context).load(item.getUserPic())
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(rholder.usrimg);

        return row;
    }

    static class RecordHolder {
        TextView usr_name,user_address;
        ImageView usrimg;


    }
}

