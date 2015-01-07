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

import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mukesh on 4/7/14.
 */

public class ClickInWithAdapter extends ArrayAdapter<GetrelationshipsBean> {
    Context context;
    int layoutResourceId;
    List<GetrelationshipsBean> item1;
    private Typeface typeface;

    public ClickInWithAdapter(Context context, int layoutResourceId, List<GetrelationshipsBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.item1 = item;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final GetrelationshipsBean item = getItem(position);
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.clickInUsrName = (TextView) row.findViewById(R.id.tv_clickInUsr_name);
            holder.unReadNo = (TextView) row.findViewById(R.id.tv_unread);
            holder.clickInUsrimg = (ImageView) row.findViewById(R.id.iv_ClickInUsrImg);


            typeface = Typeface.createFromAsset(context.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
            holder.clickInUsrName.setTypeface(typeface, typeface.BOLD);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        final RecordHolder rholder = (RecordHolder) row.getTag();

        if (!Utils.isEmptyString(item.getStatusAccepted()) && item.getStatusAccepted().equalsIgnoreCase("true")) {

            rholder.clickInUsrName.setText(item.getPartnerName());

            if (item.getUnreadMsg() != 0) {
                rholder.unReadNo.setText("" + item.getUnreadMsg());
                rholder.unReadNo.setVisibility(View.VISIBLE);

            } else {
                rholder.unReadNo.setVisibility(View.GONE);

            }

            if (!item.getPartnerPic().equalsIgnoreCase("")) {
                try {

                    PicassoManager.getPicasso()  // get picasso from picasso maneger
                    .load(item.getPartnerPic())
                            .into(rholder.clickInUsrimg);
                } catch (Exception e) {
                    // rholder.clickInUsrimg.setImageResource(R.drawable.male_user);
                }

            } else {
                rholder.clickInUsrimg.setImageResource(R.drawable.male_user);
            }
        } else {
            rholder.clickInUsrimg.setImageResource(R.drawable.male_user);
        }

        if (((item1.size() - 1) == position))
            row.findViewById(R.id.clcth_divider).setVisibility(View.GONE);
        else
            row.findViewById(R.id.clcth_divider).setVisibility(View.VISIBLE);


        return row;
    }

    static class RecordHolder {
        TextView clickInUsrName, unReadNo;
        ImageView clickInUsrimg;


    }
}

