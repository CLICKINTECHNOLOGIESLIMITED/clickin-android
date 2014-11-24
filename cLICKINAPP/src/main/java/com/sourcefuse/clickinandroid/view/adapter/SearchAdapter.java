package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
      List<FetchUsersByNameBean> item;

    public SearchAdapter(Context context, int layoutResourceId,
                           List<FetchUsersByNameBean> item1) {
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
            holder.usr_name = (TextView) row.findViewById(R.id.searchname);
            holder.user_address = (TextView) row.findViewById(R.id.tv_user_address);
            holder.usrimg = (ImageView) row.findViewById(R.id.userImage);
            holder.usrimg.setScaleType(ImageView.ScaleType.FIT_XY);


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        RecordHolder rholder = (RecordHolder) row.getTag();
        if(!Utils.isEmptyString(item.get(position).getCity()) && !Utils.isEmptyString(item.get(position).getCountry())) {
            holder.user_address.setText(item.get(position).getCity()+","+item.get(position).getCountry());
              Log.e("in 1 --->","in 1 --->");
        }else if (Utils.isEmptyString(item.get(position).getCity())) {
            holder.user_address.setText(item.get(position).getCity());
              Log.e("in 2 --->","in 2 --->");
              Log.e("name--->",""+item.get(position).getCity());
        }else if (Utils.isEmptyString(item.get(position).getCountry())) {
            holder.user_address.setText(item.get(position).getCountry());
              Log.e("in 3 --->","in 3 --->");
              Log.e("name--->",""+item.get(position).getCountry());
        }else{
              Log.e("in final --->","in final --->");
        }

        rholder.usr_name.setText(item.get(position).getName());
        if(!item.get(position).getUserPic().equalsIgnoreCase("")) {
            try {
                Picasso.with(context).load(item.get(position).getUserPic())
                        .skipMemoryCache()
                        .error(R.drawable.male_user)
                        .into(rholder.usrimg);
            }
            catch (Exception e)
            {
                holder.usrimg.setImageResource(R.drawable.male_user);
            }
        }
        else
        {
            holder.usrimg.setImageResource(R.drawable.male_user);
        }

        if((item.size()-1) == position)
            row.findViewById(R.id.divider).setVisibility(View.GONE);
        else
            row.findViewById(R.id.divider).setVisibility(View.VISIBLE);

        return row;
    }

    static class RecordHolder {
        TextView usr_name,user_address;
        ImageView usrimg;


    }
}

