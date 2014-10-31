package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FollowingListView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowingAdapter extends ArrayAdapter<FollowerFollowingBean> {
    private static final String TAG = FollowingAdapter.class.getSimpleName();
	Context context;
	int layoutResourceId;
    private AuthManager authManager;
    private RelationManager relationManager;

	public FollowingAdapter(Context context, int layoutResourceId,
			List<FollowerFollowingBean> item) {
		super(context, layoutResourceId, item);
		this.layoutResourceId = layoutResourceId;
		this.context = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final FollowerFollowingBean item = getItem(position);
		View row = convertView;
		RecordHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RecordHolder();
			holder.usr_name = (TextView) row
					.findViewById(R.id.tv_clickers_name);
			holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
			holder.usrimg.setScaleType(ScaleType.FIT_XY);
			holder.reqbtn = (Button) row.findViewById(R.id.btn_actions);

			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}

        final RecordHolder rholder = (RecordHolder) row.getTag();

        if(FollowingListView.fromOwnProfile==true){
            holder.reqbtn.setVisibility(View.VISIBLE);
        }else{
            holder.reqbtn.setVisibility(View.GONE);
        }

		if(!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("true")){
			rholder.reqbtn.setBackgroundResource(R.drawable.following);
		}else{
			rholder.reqbtn.setBackgroundResource(R.drawable.requested_grey);
		}
		rholder.usr_name.setText(item.getFolloweeName());

        if(!item.getFolloweePic().equalsIgnoreCase("")) {
            try {
                Picasso.with(context).load(item.getFolloweePic())
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



        rholder.reqbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();

                if(item.getAccepted().matches("true") && item.getIsFollowing().matches("false")){
                    new AlertDialog.Builder(context).setMessage(AlertMessage.UNFOLLOWUSER).setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    rholder.reqbtn.setBackgroundResource(R.drawable.follow);
                                    relationManager.unFollowUser(item.getrFollowerId(),"true", authManager.getPhoneNo(), authManager.getUsrToken());
                                    item.setIsFollowing("true");
                                    //FollowerList.adapter.notifyDataSetChanged();
                                    Log.e(TAG, "Click - holder.Unfollow=");
                                }

                            }
                    ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    }).show();
                }else if(item.getIsFollowing().matches("true")){
                    rholder.reqbtn.setBackgroundResource(R.drawable.requested_grey);
                    relationManager.followUser(item.getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                    item.setIsFollowing("false");
                    //FollowerList.adapter.notifyDataSetChanged();
                    Log.e(TAG, "Click - holder.follow=");
                }

            }
        });


		return row;
	}

	static class RecordHolder {
		TextView usr_name;
		ImageView usrimg;
        Button reqbtn;

	}
}
