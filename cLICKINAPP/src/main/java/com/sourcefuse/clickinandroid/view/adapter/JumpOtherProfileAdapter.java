package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Switch;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.ProfileRelationShipBean;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JumpOtherProfileAdapter extends ArrayAdapter<ProfileRelationShipBean> {
    Context context;
    int layoutResourceId;
    private AuthManager authManager;
    private ProfileManager profileManager;
    private RelationManager relationManager;
    private Typeface typeface;
    private int size;
    List<ProfileRelationShipBean> item1;

    public JumpOtherProfileAdapter(Context context, int layoutResourceId,
                                   List<ProfileRelationShipBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.item1 = item;
        this.size = item.size();


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ProfileRelationShipBean item = getItem(position);
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.usr_name = (TextView) row.findViewById(R.id.tv_usr_name);
            holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr_pic);
            holder.usrimg.setScaleType(ScaleType.FIT_XY);

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
                            .into(rholder.usrimg);
                } catch (Exception e) {
                    //holder.usrimg.setImageResource(R.drawable.male_user);
                }
            } else {
                // holder.usrimg.setImageResource(R.drawable.male_user);
            }
        } catch (Exception e) {
        }


        if (position == size-1) {//akshit code
            ((ImageView) row.findViewById(R.id.btm_divider)).setVisibility(View.GONE);
            ((View) row.findViewById(R.id.v_devider)).setVisibility(View.GONE);
        }else
        {
            ((ImageView) row.findViewById(R.id.btm_divider)).setVisibility(View.VISIBLE);
            ((View) row.findViewById(R.id.v_devider)).setVisibility(View.VISIBLE);
        }


        return row;


    }

    static class RecordHolder {
        TextView usr_name;
        ImageView usrimg;
    }
}
