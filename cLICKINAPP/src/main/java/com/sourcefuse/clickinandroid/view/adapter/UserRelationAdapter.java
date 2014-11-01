package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserRelationAdapter extends ArrayAdapter<GetrelationshipsBean> {
	Context context;
	int layoutResourceId;
	private AuthManager authManager;
	private ProfileManager profileManager;
	private RelationManager relationManager;
	private Typeface typeface;
	private boolean showpending = false;

	public UserRelationAdapter(Context context, int layoutResourceId,
			List<GetrelationshipsBean> item) {
		super(context, layoutResourceId, item);
		this.layoutResourceId = layoutResourceId;
		this.context = context;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		final GetrelationshipsBean item = getItem(position);
		relationManager = ModelManager.getInstance().getRelationManager();
		RecordHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RecordHolder();
			holder.usr_name = (TextView) row.findViewById(R.id.tv_usr_name);
			holder.pending = (TextView) row.findViewById(R.id.tv_pending);
			holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr_pic);
			holder.privacy = (Button) row.findViewById(R.id.btn_privacy);
			holder.whiteview = (View) row.findViewById(R.id.v_whiteview);
			holder.devider = (View) row.findViewById(R.id.v_devider);
			
			holder.delete = (Button) row.findViewById(R.id.btn_delete_item);
			holder.usrimg.setScaleType(ScaleType.FIT_XY);

			typeface = Typeface.createFromAsset(context.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
			holder.usr_name.setTypeface(typeface,typeface.BOLD);
			
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}
		
		final RecordHolder rholder = (RecordHolder) row.getTag();
		rholder.usr_name.setText(item.getPartnerName());
		
		if(item.getStatusAccepted().matches("true") && (item.getmStatuspublic().matches("false") || Utils.isEmptyString(item.getmStatuspublic()))){
            showpending =false;
			rholder.privacy.setBackgroundResource(R.drawable.owner_profile_eye_cross_icon );
		}else if(item.getStatusAccepted().matches("true") && item.getmStatuspublic().matches("true")){
            showpending =false;
			rholder.privacy.setBackgroundResource(R.drawable.owner_profile_eye_icon);
		}else if(Utils.isEmptyString(item.getStatusAccepted()) && item.getRequestInitiator().matches("true")){

            android.util.Log.e("getRequestInitiator","getRequestInitiator");
			//sent request ClickIcon
			showpending =true;
			rholder.privacy.setBackgroundResource(R.drawable.pending_status);
		}else if(Utils.isEmptyString(item.getStatusAccepted())){
            showpending =false;
			//Coming request
			rholder.privacy.setBackgroundResource(R.drawable.requested_statuts);
		}
		
		if(showpending){
            rholder.pending.setVisibility(View.GONE);
			//holder.pending.setBackgroundResource(R.drawable.white_view);
		}else{
            rholder.pending.setVisibility(View.GONE);
		}



		if(position==relationManager.requestedList.size()-1){
            Log.e("1","1");
			//holder.whiteview.setVisibility(View.VISIBLE);
           // holder.whiteview.setBackgroundResource(R.drawable.owner_list_roundedview);
			//rholder.devider.setVisibility(View.VISIBLE);
			//holder.pending.setBackgroundResource(R.drawable.white_view);
           // rholder.pending.setVisibility(View.VISIBLE);
			rholder.devider.setBackgroundColor(0x00000000);
		}else if(position==(relationManager.acceptedList.size()+relationManager.requestedList.size())-1){
            Log.e("2","2");
			//holder.whiteview.setVisibility(View.VISIBLE);
           // holder.whiteview.setBackgroundResource(R.drawable.owner_list_roundedview);
           /// rholder.pending.setVisibility(View.VISIBLE);
            rholder.whiteview.setVisibility(View.VISIBLE);
            rholder.whiteview.setBackgroundResource(R.drawable.owner_list_roundedview);

			rholder.devider.setVisibility(View.VISIBLE);
			rholder.devider.setBackgroundColor(0x00000000);
           // holder.pending.setBackgroundResource(R.drawable.white_view);
		}else if(position==(relationManager.acceptedList.size())){
            Log.e("3","3");
            rholder.devider.setVisibility(View.VISIBLE);
            rholder.devider.setBackgroundColor(0x00000000);
            rholder.pending.setBackgroundResource(R.drawable.white_view);
        }
        else{
            Log.e("4","4");
            rholder.whiteview.setVisibility(View.GONE);
            rholder.devider.setVisibility(View.GONE);
		}

		showpending =false;
		
		
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
        }		holder.privacy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	authManager = ModelManager.getInstance().getAuthorizationManager();
    			relationManager = ModelManager.getInstance().getRelationManager();
                Log.e("1","1"+item.getmStatuspublic());

                if(item.getStatusAccepted().matches("true") && item.getmStatuspublic().matches("true")){
                    Log.e("1","2");
                    new AlertDialog.Builder(context)
                            .setMessage(AlertMessage.PUBLICMSG+item.getPartnerName()+" private?")
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            item.setmStatuspublic("false");
                                            relationManager.changeUserVisibility(item.getRelationshipId(), "false", authManager.getPhoneNo(), authManager.getUsrToken());
                                            rholder.privacy.setBackgroundResource(R.drawable.owner_profile_eye_cross_icon);

                                        }

                                    }
                            ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }

                    }).show();

        		}else if(item.getStatusAccepted().matches("true") && (item.getmStatuspublic().matches("false") || Utils.isEmptyString(item.getmStatuspublic()))){
                    Log.e("1","3");
                    new AlertDialog.Builder(context)
                            .setMessage(AlertMessage.PRIVATE+item.getPartnerName()+" public?")
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            item.setmStatuspublic("true");
                                            relationManager.changeUserVisibility(item.getRelationshipId(),"true",authManager.getPhoneNo(),authManager.getUsrToken());
                                            rholder.privacy.setBackgroundResource(R.drawable.owner_profile_eye_icon);

                                        }

                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }

                    }).show();


        		}else if (Utils.isEmptyString(item.getStatusAccepted())&& item.getRequestInitiator().matches("true")){
                    Log.e("1","4");
        		}else if (Utils.isEmptyString(item.getStatusAccepted())){
                    Log.e("1","5");
                    Utils.launchBarDialog((Activity)context);
                    relationManager.updateStatus(item.getRelationshipId(),authManager.getPhoneNo(),authManager.getUsrToken(),"true");
                	item.setStatusAccepted("true");
        		}
            }
        });
		
		    holder.usrimg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { 
            	if(item.getStatusAccepted()=="true") {
                    Intent intent = new Intent(context, JumpOtherProfileView.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("FromOwnProfile", true);
                    intent.putExtra("phNumber", item.getPhoneNo());
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    context.startActivity(intent);
                    Log.e("", "holder.usrimg");
                }
                }
        });
		    holder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	authManager = ModelManager.getInstance().getAuthorizationManager();
    			relationManager = ModelManager.getInstance().getRelationManager();
                Constants.itemPosition = position;
                Utils.launchBarDialog((Activity)context);
                relationManager.deleteRelationship(item.getRelationshipId(),authManager.getPhoneNo(),authManager.getUsrToken());

            }
        });
		
		return row;
	}

	static class RecordHolder {
		TextView usr_name,pending;
		ImageView usrimg;
		Button delete;
		Button privacy;
		View devider;
		View whiteview;

	}
}
