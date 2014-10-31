package com.sourcefuse.clickinandroid.view.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.ProfileRelationShipBean;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

public class JumpOtherProfileAdapter extends ArrayAdapter<ProfileRelationShipBean> {
	Context context;
	int layoutResourceId;
	private AuthManager authManager;
	private ProfileManager profileManager;
	private RelationManager relationManager;
	private Typeface typeface;

	public JumpOtherProfileAdapter(Context context, int layoutResourceId,
			List<ProfileRelationShipBean> item) {
		super(context, layoutResourceId, item);
		this.layoutResourceId = layoutResourceId;
		this.context = context;

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

			typeface = Typeface.createFromAsset(context.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
			holder.usr_name.setTypeface(typeface,typeface.BOLD);
			
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}
		
		final RecordHolder rholder = (RecordHolder) row.getTag();
		

		rholder.usr_name.setText(item.getPartnerName());


        if(!item.getPartnerPic().equalsIgnoreCase("")) {
            try {
                Picasso.with(context).load(item.getPartnerPic())
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
		return row;
	}

	static class RecordHolder {
		TextView usr_name;
		ImageView usrimg;
	}
}
