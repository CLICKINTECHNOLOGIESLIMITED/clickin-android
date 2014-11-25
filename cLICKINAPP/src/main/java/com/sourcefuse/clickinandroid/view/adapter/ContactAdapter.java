package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ContactAdapter extends ArrayAdapter<ContactBean> {
	Context context;
	int layoutResourceId;

	private ArrayList<ContactBean> conData ;
	private List<ContactBean> refreshList = null;

	public ContactAdapter(Context context, int layoutResourceId,
			List<ContactBean> item) {
		super(context, layoutResourceId, item);
		this.refreshList = item;
		this.layoutResourceId = layoutResourceId;
		this.context = context;

		this.conData = new ArrayList<ContactBean>();
		this.conData.addAll(item);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		final ContactBean item = getItem(position);
		RecordHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RecordHolder();
			holder.contactName = (TextView) row.findViewById(R.id.tv_contact_name);
			holder.contactImage = (ImageView) row.findViewById(R.id.iv_contactImage);
            holder.contactnumber=(TextView) row.findViewById(R.id.tv_contact_number);
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}

		 Bitmap bitmap = null;
		RecordHolder rholder = (RecordHolder) row.getTag();
		try {
			if(!Utils.isEmptyString(item.getConUri()))
			bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),Uri.parse(item.getConUri()));
			holder.contactImage.setImageBitmap(bitmap);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rholder.contactName.setText(item.getConName());
        rholder.contactnumber.setText(item.getConNumber());



		return row;
	}


    // Filter Class
		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			refreshList.clear();

			if (charText.length() == 0) {
				conData.addAll(Utils.itData);
			}
			else
			{
				for (ContactBean wp : conData)
				{
					//if(wp.getConName()!=null)
					if (wp.getConName().toLowerCase(Locale.getDefault()).contains(charText) || wp.getConNumber().toLowerCase(Locale.getDefault()).contains(charText))
					{

                        refreshList.add(wp);

					}
				}
			}
			notifyDataSetChanged();
		}

	static class RecordHolder {
		TextView contactName;
		ImageView contactImage;
		TextView contactnumber ;

	}
}

