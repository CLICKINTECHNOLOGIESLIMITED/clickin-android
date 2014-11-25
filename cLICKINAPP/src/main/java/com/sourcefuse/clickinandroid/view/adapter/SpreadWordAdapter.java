package com.sourcefuse.clickinandroid.view.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;



public class SpreadWordAdapter extends ArrayAdapter<ContactBean> {
    Context context;
    int layoutResourceId;

    public SpreadWordAdapter(Context context, int layoutResourceId,
                             List<ContactBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        final ContactBean item = getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.clickers = (TextView) row.findViewById(R.id.tv_clickers);
            holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
            holder.follow = (Button) row.findViewById(R.id.btn_follow);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();

        }



        Bitmap bitmap = null;
		final RecordHolder rholder = (RecordHolder) row.getTag();
        if(item.isChecked()){
            rholder.follow.setBackgroundResource(R.drawable.check);
        }else{
            rholder.follow.setBackgroundResource(R.drawable.uncheck);
        }
		try {
			if(!Utils.isEmptyString(item.getConUri())) {
			/*bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),Uri.parse(item.getConUri()));
			holder.usrimg.setImageBitmap(bitmap);*/
                Picasso.with(context).
                        load(item.getConUri())
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(rholder.usrimg);
            }else{
               rholder.usrimg.setImageResource(R.drawable.default_profile);




            }

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rholder.clickers.setText(item.getConName());


        holder.follow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(item.isChecked()){
                    rholder.follow.setBackgroundResource(R.drawable.uncheck);
                    item.setChecked(false);
                    if(Utils.groupSms.size()>0)
                    Utils.groupSms.remove(item.getConNumber());

                }else{
                    rholder.follow.setBackgroundResource(R.drawable.check);
                    item.setChecked(true);
                    Utils.groupSms.add(item.getConNumber());
                }

            }
        });

        return row;
    }

    static class RecordHolder {
        TextView clickers;
        ImageView usrimg;
        Button follow;

    }
}
