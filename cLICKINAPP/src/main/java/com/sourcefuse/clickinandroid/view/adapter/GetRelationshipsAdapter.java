package com.sourcefuse.clickinandroid.view.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GetRelationshipsAdapter extends ArrayAdapter<GetrelationshipsBean> {
    Context context;
    int layoutResourceId;
    private Typeface typeface;


    public GetRelationshipsAdapter(Context context, int layoutResourceId,
                                   List<GetrelationshipsBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final GetrelationshipsBean item = getItem(position);
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
            holder.usr_name = (TextView) row.findViewById(R.id.tv_clickers_name);
            holder.chatCount = (TextView) row.findViewById(R.id.tv_unread);


            typeface = Typeface.createFromAsset(context.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
            holder.usr_name.setTypeface(typeface, typeface.BOLD);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        final RecordHolder rholder = (RecordHolder) row.getTag();

        try {
            rholder.usr_name.setText(item.getPartnerName());
            if (!item.getPartnerPic().equalsIgnoreCase("")) {
                try {
                    Picasso.with(context).load(item.getPartnerPic())
                            .skipMemoryCache()
                            .error(R.drawable.male_user)
                            .into(rholder.usrimg);
                } catch (Exception e) {
                    holder.usrimg.setImageResource(R.drawable.male_user);
                }
            } else {
                holder.usrimg.setImageResource(R.drawable.male_user);
            }

        } catch (Exception e) {
        }


        return row;
    }

    static class RecordHolder {
        TextView usr_name;
        ImageView usrimg;
        TextView chatCount;

    }
}

