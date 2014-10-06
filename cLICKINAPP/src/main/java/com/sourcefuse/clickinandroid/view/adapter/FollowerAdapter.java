
package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FollowerList;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.event.EventBus;

public class FollowerAdapter extends ArrayAdapter<FollowerFollowingBean> {
    private static final String TAG = FollowerAdapter.class.getSimpleName();
	Context context;
	int layoutResourceId;

    private AuthManager authManager;
    private RelationManager relationManager;
    private ProfileManager profileManager;
    private Typeface typeface;



	public FollowerAdapter(Context context, int layoutResourceId,
			List<FollowerFollowingBean> item) {
		super(context, layoutResourceId, item);
		this.layoutResourceId = layoutResourceId;
		this.context = context;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final FollowerFollowingBean item = getItem(position);
		View row = convertView;
		RecordHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RecordHolder();
            typeface = Typeface.createFromAsset(context.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);


			holder.usr_name = (TextView) row.findViewById(R.id.tv_clickers_name);
            holder.hfollowersRequest = (TextView) row.findViewById(R.id.tv_heading_rfollowers);
            holder.hfollowers = (TextView) row.findViewById(R.id.tv_heading_followers);
			holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
			holder.usrimg.setScaleType(ScaleType.FIT_XY);
			holder.reqbtn = (Button) row.findViewById(R.id.btn_actions);
            holder.resBtn = (Button) row.findViewById(R.id.btn_f_resect);
            holder.accBtn = (Button) row.findViewById(R.id.btn_f_accept);
            holder.rlResectAccept = (LinearLayout) row.findViewById(R.id.rl_resect_accept);

            holder.hfollowersRequest.setTypeface(typeface, typeface.BOLD);
            holder.hfollowers.setTypeface(typeface, typeface.BOLD);

			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}
		
		RecordHolder rholder = (RecordHolder) row.getTag();
		rholder.usr_name.setText(item.getFolloweeName());
		Picasso.with(context).load(item.getFolloweePic())
	    .placeholder(R.drawable.default_profile)
	    .error(R.drawable.default_profile)
	    .into(rholder.usrimg);


        profileManager = ModelManager.getInstance().getProfileManager();





        if(profileManager.followRequesed.size()>0 && position == 0){
            holder.hfollowersRequest.setVisibility(View.VISIBLE);
            holder.hfollowersRequest.setText("FOLLOW REQUESTS");
        }else{
            holder.hfollowersRequest.setVisibility(View.GONE);
        }

        if(profileManager.followRequesed.size() == position){
            holder.hfollowers.setVisibility(View.VISIBLE);
            holder.hfollowers.setText("FOLLOWERS");
        }else{
            holder.hfollowers.setVisibility(View.GONE);
        }

        if(!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("true") && item.getFollowingAccepted().matches("true") && item.getIsFollowing().matches("true")){
            if(FollowerList.fromOwnProfile==true){
                holder.reqbtn.setVisibility(View.VISIBLE);
                holder.rlResectAccept.setVisibility(View.GONE);
                rholder.reqbtn.setBackgroundResource(R.drawable.following);

                /*holder.rlResectAccept.setVisibility(View.GONE);*/
            }else{
               // holder.rlResectAccept.setVisibility(View.GONE);
                holder.reqbtn.setVisibility(View.GONE);
                holder.rlResectAccept.setVisibility(View.GONE);
            }

            /*holder.reqbtn.setVisibility(View.VISIBLE);
            holder.rlResectAccept.setVisibility(View.GONE);
			rholder.reqbtn.setBackgroundResource(R.drawable.following);*/
		}else if(!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("true") && item.getFollowingAccepted().matches("true") && item.getIsFollowing().matches("false")){
            if(FollowerList.fromOwnProfile==true){
                holder.reqbtn.setVisibility(View.VISIBLE);
                holder.rlResectAccept.setVisibility(View.GONE);
                rholder.reqbtn.setBackgroundResource(R.drawable.requested_grey);

                /*holder.rlResectAccept.setVisibility(View.GONE);*/
            }else{
                // holder.rlResectAccept.setVisibility(View.GONE);
                holder.reqbtn.setVisibility(View.GONE);
                holder.rlResectAccept.setVisibility(View.GONE);
            }

        }else if(!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("true") && item.getFollowingAccepted().matches("false") && item.getIsFollowing().matches("true")){
            if(FollowerList.fromOwnProfile==true){
                holder.reqbtn.setVisibility(View.VISIBLE);
                holder.rlResectAccept.setVisibility(View.GONE);
                rholder.reqbtn.setBackgroundResource(R.drawable.requested_grey);
            }else{
                holder.reqbtn.setVisibility(View.GONE);
                holder.rlResectAccept.setVisibility(View.GONE);
            }

        }else if (!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("false") && item.getFollowingAccepted().matches("true") && item.getIsFollowing().matches("false")) {


            if(FollowerList.fromOwnProfile==true){
                holder.reqbtn.setVisibility(View.VISIBLE);
                holder.rlResectAccept.setVisibility(View.GONE);
                rholder.reqbtn.setBackgroundResource(R.drawable.follow);
            }else{
                holder.reqbtn.setVisibility(View.GONE);
                holder.rlResectAccept.setVisibility(View.GONE);
            }

        }else if (!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("true") && item.getFollowingAccepted().matches("false") && item.getIsFollowing().matches("false")) {


            if(FollowerList.fromOwnProfile==true){
                holder.reqbtn.setVisibility(View.VISIBLE);
                holder.rlResectAccept.setVisibility(View.GONE);
                rholder.reqbtn.setBackgroundResource(R.drawable.follow);

            }else{

                holder.reqbtn.setVisibility(View.GONE);
                holder.rlResectAccept.setVisibility(View.GONE);
            }

        }else if(item.getAccepted().length()==0){

            if(FollowerList.fromOwnProfile==true){
                holder.rlResectAccept.setVisibility(View.VISIBLE);
                holder.reqbtn.setVisibility(View.GONE);
            }else{
                holder.reqbtn.setVisibility(View.GONE);
                holder.rlResectAccept.setVisibility(View.GONE);
            }


		}else {
            Log.e(TAG, "DATA="+item.getAccepted()+"---"+item.getIsFollowing()+"---"+item.getFollowingAccepted());
        }




        final RecordHolder vholder = (RecordHolder) row.getTag();
        rholder.reqbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();
                if(!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("true") && item.getFollowingAccepted().matches("true") && item.getIsFollowing().matches("true")){

                    new AlertDialog.Builder(context).setMessage(AlertMessage.UNFOLLOWUSER).setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    vholder.reqbtn.setBackgroundResource(R.drawable.follow);
                                 relationManager.unFollowUser(item.getFollowingId(),"true", authManager.getPhoneNo(), authManager.getUsrToken());
                                    item.setIsFollowing("false");
                                    item.setFollowingAccepted("false");
                                    //FollowerList.adapter.notifyDataSetChanged();
                                    Log.e(TAG, "Click - holder.follow="+item.getIsFollowing());
                                }

                            }
                    ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    }).show();
                }else if(!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("true") && item.getIsFollowing().matches("false") && item.getFollowingAccepted().matches("false")){
                    vholder.reqbtn.setBackgroundResource(R.drawable.requested_grey);
                   relationManager.followUser(item.getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                    item.setIsFollowing("true");
                    FollowerList.adapter.notifyDataSetChanged();
                    Log.e(TAG, "Click - holder.requested_grey="+item.getIsFollowing());
                }else if (!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("false") && item.getFollowingAccepted().matches("true") && item.getIsFollowing().matches("true")) {
                    vholder.reqbtn.setBackgroundResource(R.drawable.requested_grey);
                    relationManager.followUser(item.getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                    item.setIsFollowing("true");
                    FollowerList.adapter.notifyDataSetChanged();
                    Log.e(TAG, "Click - holder.requested_grey="+item.getIsFollowing());
                }/*else if (!Utils.isEmptyString(item.getAccepted()) && item.getAccepted().matches("true") && item.getFollowingAccepted().matches("false") && item.getIsFollowing().matches("true")) {
                    Log.e(TAG, "Click - holder.empty-Requested1");
                }else {
                    Log.e(TAG, "DATA="+item.getAccepted()+"---"+item.getIsFollowing()+"---"+item.getFollowingAccepted());
                }*/

            }
        });

        rholder.accBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(TAG, "Click - holder.Accep1t="+item.getAccepted());
                item.setAccepted("true");
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();
                profileManager.followRequesed.remove(position);
                profileManager.followRequesed.iterator();
                FollowerList.adapter.notifyDataSetChanged();
                EventBus.getDefault().post("followUpdateStatus true");
              relationManager.followupdatestatus(item.getrFollowerId(),"true",authManager.getPhoneNo(),authManager.getUsrToken());
                Log.e(TAG, "Click - holder.Accept="+item.getAccepted());
            }
        });
        rholder.resBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                profileManager.followRequesed.remove(position);
                profileManager.followers.remove(position);
                authManager = ModelManager.getInstance().getAuthorizationManager();
                FollowerList.adapter.notifyDataSetChanged();
                relationManager = ModelManager.getInstance().getRelationManager();
                relationManager.followupdatestatus(item.getrFollowerId(),"false",authManager.getPhoneNo(),authManager.getUsrToken());
                Log.e(TAG, "Click - holder.resect");
            }
        });

/*
        if(FollowerList.fromOwnProfile==true){
            holder.rlResectAccept.setVisibility(View.GONE);
        }else{
            holder.rlResectAccept.setVisibility(View.GONE);
        }*/

		return row;
	}

	static class RecordHolder {
        LinearLayout rlResectAccept;
		TextView usr_name,hfollowersRequest,hfollowers;
		ImageView usrimg;
		Button reqbtn;
        Button resBtn;
        Button accBtn;
		

	}
}

