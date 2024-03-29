package com.sourcefuse.clickinandroid.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.JumpOtherProfileAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public class JumpOtherProfileView extends ClickInBaseView implements View.OnClickListener {

	private Button follower, following,follow;
	private TextView profileHeader, tvclickintx, tvwith,othesProfileName,clickwithHead ,clickwithNameHead;
	private ListView listView;
	private ImageView userimage;
	private ImageView menu,noti_other;
	private RelativeLayout rlClickWith;
	private JumpOtherProfileAdapter adapter;
	private AuthManager authManager;
	private RelationManager relationManager;
    private ProfileManager profileManager;
	private TextView name, userdetails;
	private boolean othersUser = false;
	public String phone,phForOtherUser;
	private Typeface typeface;
	private boolean whichList =  false;
    private View headerView ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_othersprofile);
		addMenu(false);
		this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
		typeface = Typeface.createFromAsset(JumpOtherProfileView.this.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
		
		authManager = ModelManager.getInstance().getAuthorizationManager();
		

		listView 	  = (ListView) findViewById(R.id.list_click_with_other);
		following 	  = (Button) findViewById(R.id.btn_following_other);
		follower 	  = (Button) findViewById(R.id.btn_follower_other);
		follow        = (Button) findViewById(R.id.btn_follow);
		menu		  = (ImageView) findViewById(R.id.iv_menu_other);
		noti_other    = (ImageView) findViewById(R.id.iv_notification_other);
		userimage	  = (ImageView) findViewById(R.id.iv_usr_icon);
		name 		  = (TextView) findViewById(R.id.tv_name_other);
		userdetails   = (TextView) findViewById(R.id.tv_user_details_other);
		profileHeader = (TextView) findViewById(R.id.tv_profile_other);
		tvclickintx   = (TextView) findViewById(R.id.tv_clickintx);
		tvwith        = (TextView) findViewById(R.id.tv_with);
		othesProfileName   = (TextView) findViewById(R.id.tv_profile_other);
        rlClickWith   = (RelativeLayout) findViewById(R.id.rl_add_someone);

        clickwithHead = (TextView)findViewById(R.id.tv_click_with_head);
        clickwithNameHead =  (TextView)findViewById(R.id.tv_clickwith_name_head);


        rlClickWith.setOnClickListener(this);
		follow.setOnClickListener(this);
		following.setOnClickListener(this);
		noti_other.setOnClickListener(this);
		follower.setOnClickListener(this);
		menu.setOnClickListener(this);
		name.setTypeface(typeface, typeface.BOLD);
		othesProfileName.setTypeface(typeface, typeface.BOLD);
		profileHeader.setTypeface(typeface, typeface.BOLD);
		tvclickintx.setTypeface(typeface, typeface.BOLD);

		following.setTypeface(typeface, typeface.BOLD);
		follower.setTypeface(typeface, typeface.BOLD);
		userimage.setScaleType(ScaleType.FIT_XY);
		tvwith.setTypeface(typeface);
		userdetails.setTypeface(typeface);
		
	
		//othersUser = getIntent().getExtras().getBoolean("othersProfile");
		othersUser = getIntent().getExtras().getBoolean("FromOwnProfile");
		if(othersUser){
            authManager = ModelManager.getInstance().getAuthorizationManager();
            relationManager = ModelManager.getInstance().getRelationManager();
			phForOtherUser = getIntent().getExtras().getString("phNumber");
            authManager.getProfileInfo(phForOtherUser,authManager.getPhoneNo(), authManager.getUsrToken());
            relationManager.fetchprofilerelationships(phForOtherUser, authManager.getPhoneNo(), authManager.getUsrToken());
		}

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	           if(relationManager.profileRelationShipData.size()>0){
	            Log.e("","position--> "+position+""+relationManager.profileRelationShipData.get(position).getPhoneNo());
	            String phNo = relationManager.profileRelationShipData.get(position).getPhoneNo();
	            switchView(phNo);
	         }
	        }
	    });


//code to set adapter to populate list
       // headerView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.header_otherprofile, null, false);
        //listView.addHeaderView(headerView);
	}

	public void setlist() {
        if(relationManager.profileRelationShipData.size()==0) {
            ((LinearLayout) findViewById(R.id.ll_clickin_header)).setVisibility(View.GONE);
            ((View) findViewById(R.id.v_devider_header)).setVisibility(View.GONE);
        }else{
            ((LinearLayout) findViewById(R.id.ll_clickin_header)).setVisibility(View.VISIBLE);
            ((View) findViewById(R.id.v_devider_header)).setVisibility(View.VISIBLE);
        }
		adapter = new JumpOtherProfileAdapter(this, R.layout.row_othersprofile,relationManager.profileRelationShipData);
		listView.setAdapter(adapter);


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_follower_other:
                Intent intentFollower = new Intent(JumpOtherProfileView.this,FollowerList.class);
                intentFollower.putExtra("FromOwnProfile", false);
                intentFollower.putExtra("phoneNo", phForOtherUser);
                startActivity(intentFollower);
                break;
            case R.id.btn_following_other:
                Intent intentFollowing = new Intent(JumpOtherProfileView.this,FollowingListView.class);
                intentFollowing.putExtra("FromOwnProfile", false);
                intentFollowing.putExtra("phoneNo", phForOtherUser);
                startActivity(intentFollowing);
                break;
		case R.id.btn_follow:
            authManager = ModelManager.getInstance().getAuthorizationManager();
            relationManager = ModelManager.getInstance().getRelationManager();
            relationManager.followUser(phForOtherUser, authManager.getPhoneNo(), authManager.getUsrToken());
			break;
		case R.id.iv_menu_other:
			slidemenu.showMenu(true);
			break;
		case R.id.rl_add_someone:
            if(Utils.isEmptyString(relationManager.getRelationStatus())) {
                new AlertDialog.Builder(JumpOtherProfileView.this)
                        .setMessage(AlertMessage.MAKECLICKWITH + authManager.getTmpUserName())
                        .setPositiveButton("Yes Please",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        authManager = ModelManager.getInstance().getAuthorizationManager();

                                        authManager.sendNewRequest(authManager.getPhoneNo(), phForOtherUser, authManager.getUsrToken());

                                    }

                                }
                        ).setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }

                }).show();
            }
			break;
		case R.id.iv_notification_other:
			slidemenu.showSecondaryMenu(true);
//			Intent intent = new Intent(JumpOtherProfileView.this,AddSomeoneView.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
			break;

		}

	}

	private void switchView() {
		Intent intent = new Intent(JumpOtherProfileView.this, FollowerList.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		//this.finish();
	}
	
	private void switchViewToFollowingList() {
		Intent intent = new Intent(JumpOtherProfileView.this, FollowingListView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		//this.finish();
	}

    @Override
    public void onStart() {
        super.onStart();
        if(EventBus.getDefault().isRegistered(this)){

            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this)){

            EventBus.getDefault().unregister(this);
        }
    }
    public void onEventMainThread(String message){
        relationManager = ModelManager.getInstance().getRelationManager();
        Log.e("onEventMainThread","-------"+message);
			if (message.equalsIgnoreCase("ProfileInfo True")) {
				setProfileData();
			}else if (message.equalsIgnoreCase("Fetchprofilerelationships True")) {
				setlist();
			}else if (message.equalsIgnoreCase("FollowUser True")) {
				relationManager = ModelManager.getInstance().getRelationManager();
				follow.setBackgroundResource(R.drawable.requested_grey);
				//Utils.showToast(JumpOtherProfileView.this, relationManager.getStatusMsg());
			}else if (message.equalsIgnoreCase("FollowUser  false")) {
				relationManager = ModelManager.getInstance().getRelationManager();
				Utils.showToast(JumpOtherProfileView.this, relationManager.getStatusMsg());
			}else if (message.equalsIgnoreCase("UnFollowUser True")) {
				follow.setBackgroundResource(R.drawable.follow);
				relationManager = ModelManager.getInstance().getRelationManager();
				Utils.showToast(JumpOtherProfileView.this, relationManager.getStatusMsg());
			}else if (message.equalsIgnoreCase("UnFollowUser  false")) {
				relationManager = ModelManager.getInstance().getRelationManager();
				Utils.showToast(JumpOtherProfileView.this, relationManager.getStatusMsg());
			}else if (message.equalsIgnoreCase("GetFollower True")) {
				Utils.dismissBarDialog();
					if(whichList){
						switchView();
					}else{
						switchViewToFollowingList();
					}
			}else if (message.equalsIgnoreCase("SearchResult True")) {
                Utils.dismissBarDialog();
                stopSearch = true;
                searchList.setVisibility(View.VISIBLE);
                setSearchList();
            }else if (message.equalsIgnoreCase("RequestSend True")) {
            relationManager.setRelationStatus("requested");
                if(Utils.isEmptyString(relationManager.getRelationStatus())){
                    clickwithHead.setVisibility(View.GONE);
                    clickwithNameHead.setText("CLICK WITH\n"+authManager.getTmpUserName());
                }else if(relationManager.getRelationStatus().matches("accepted")){
                    clickwithHead.setVisibility(View.VISIBLE);
                    clickwithHead.setText("You are already");
                    clickwithNameHead.setText("CLICKIN' WITH\n"+authManager.getTmpUserName());
                }else if(relationManager.getRelationStatus().matches("requested")){
                    clickwithHead.setVisibility(View.VISIBLE);
                    clickwithHead.setText("Requested to");
                    clickwithNameHead.setText("CLICK WITH\n"+authManager.getTmpUserName());
                }else if(relationManager.getRelationStatus().matches("rejected")){
                    clickwithHead.setVisibility(View.GONE);
                    clickwithNameHead.setText("CLICK WITH\n"+authManager.getTmpUserName());
                }


            }else if (message.equalsIgnoreCase("RequestSend False")) {

            }else if (message.equalsIgnoreCase("RequestSend True")) {

             }

		}


	private void switchView(String phone) {
		Intent intent = new Intent(JumpOtherProfileView.this,JumpOtherProfileView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("FromOwnProfile", true);
		intent.putExtra("phNumber", phone);
		startActivity(intent);
		this.finish();
	}

	private void setProfileData() {
		authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();

        if(Utils.isEmptyString(relationManager.getRelationStatus())){
            clickwithHead.setVisibility(View.GONE);
            clickwithNameHead.setText("CLICK WITH\n"+authManager.getTmpUserName());
        }else if(relationManager.getRelationStatus().matches("accepted")){
            clickwithHead.setText("You are already");
            clickwithNameHead.setText("CLICKIN' WITH\n"+authManager.getTmpUserName());
        }else if(relationManager.getRelationStatus().matches("requested")){
            clickwithHead.setText("Requested to");
            clickwithNameHead.setText("CLICK WITH\n"+authManager.getTmpUserName());
        }


		if (othersUser) {
			othesProfileName.setText(authManager.getTmpUserName());
			//clickwithName.setText(authManager.getTmpUserName());
			name.setText(authManager.getTmpUserName());
			String dtails;
			
			if(authManager.getTmpIsFollowingRequested()==1){
			follow.setBackgroundResource(R.drawable.requested_grey);
			}else if(authManager.getTmpIsFollowing()==1){
				follow.setBackgroundResource(R.drawable.following);
			}else{
				follow.setBackgroundResource(R.drawable.follow);
			}

			if (authManager.getTmpGender().matches("guy")) {
				dtails = getResources().getString(R.string.txt_male)
						+ Utils.getCurrentYear(authManager.getTmpDOB()) + " "
						+ getResources().getString(R.string.txt_yold);
			} else {
				dtails = getResources().getString(R.string.txt_female)
						+ Utils.getCurrentYear(authManager.getTmpDOB()) + " "
						+ getResources().getString(R.string.txt_yold);
			}
			Utils.getCurrentYear(authManager.getTmpDOB());
			userdetails.setText(dtails + "\n");
			
			String text = "<font color=#cccccc>"+authManager.getTmpFollower()+"</font> <font color=#39cad4>"+getResources().getString(R.string.txt_follower)+"</font>";
			follower.setText(Html.fromHtml(text));
			String textfollowing = "<font color=#f29691>"+getResources().getString(R.string.txt_following)+"</font> <font color=#cccccc>"+authManager.getTmpFollowing()+"</font>";
			following.setText(Html.fromHtml(textfollowing));
			
			try {
				Picasso.with(JumpOtherProfileView.this)
						.load(authManager.getTmpUserPic())
						.placeholder(R.drawable.default_profile)
						.error(R.drawable.default_profile).into(userimage);
			} catch (Exception e) {
			}

		} 

	}

}
