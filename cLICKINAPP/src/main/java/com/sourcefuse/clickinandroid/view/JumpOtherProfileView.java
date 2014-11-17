package com.sourcefuse.clickinandroid.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
	private TextView name, userdetails;
	private boolean othersUser = false;
	public String phone,phForOtherUser;
	private Typeface typeface;
	private boolean whichList =  false;


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
		
	
		othersUser = getIntent().getExtras().getBoolean("FromOwnProfile");
		if(othersUser){
            authManager = ModelManager.getInstance().getAuthorizationManager();
            relationManager = ModelManager.getInstance().getRelationManager();
			phForOtherUser = getIntent().getExtras().getString("phNumber");
            authManager.getProfileInfo(phForOtherUser,authManager.getPhoneNo(), authManager.getUsrToken());
            relationManager.fetchprofilerelationships(phForOtherUser, authManager.getPhoneNo(), authManager.getUsrToken());

            name.setText(getIntent().getExtras().getString("name"));
            profileHeader.setText(getIntent().getExtras().getString("name"));
            if(authManager.getUserName()!=null) {
                if (name.getText().toString().trim().equalsIgnoreCase(authManager.getUserName().toString().trim())) {
                    rlClickWith.setVisibility(View.GONE);
                    follow.setVisibility(View.GONE);
                } else {
                    rlClickWith.setVisibility(View.VISIBLE);
                    follow.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                rlClickWith.setVisibility(View.VISIBLE);
                follow.setVisibility(View.VISIBLE);
            }

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
                intentFollower.putExtra("name", name.getText().toString().substring(0,name.getText().toString().indexOf(" ")));
                startActivity(intentFollower);
                break;
            case R.id.btn_following_other:
                Intent intentFollowing = new Intent(JumpOtherProfileView.this,FollowingListView.class);
                intentFollowing.putExtra("FromOwnProfile", false);
                intentFollowing.putExtra("phoneNo", phForOtherUser);
                intentFollowing.putExtra("name", name.getText().toString().substring(0,name.getText().toString().indexOf(" ")));
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
                    dialogClickwith();

//                new AlertDialog.Builder(JumpOtherProfileView.this)
//                        .setMessage(AlertMessage.MAKECLICKWITH + authManager.getTmpUserName())
//                        .setPositiveButton("Yes Please",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        authManager = ModelManager.getInstance().getAuthorizationManager();
//
//                                        authManager.sendNewRequest(authManager.getPhoneNo(), phForOtherUser, authManager.getUsrToken());
//
//                                    }
//
//                                }
//                        ).setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//                    }
//
//                }).show();
            }
			break;
		case R.id.iv_notification_other:
			slidemenu.showSecondaryMenu(true);
			break;

		}

	}

	private void switchView() {
		Intent intent = new Intent(JumpOtherProfileView.this, FollowerList.class);
		startActivity(intent);
	}
	
	private void switchViewToFollowingList() {
		Intent intent = new Intent(JumpOtherProfileView.this, FollowingListView.class);
		startActivity(intent);
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
        super.onEventMainThread(message);
        relationManager = ModelManager.getInstance().getRelationManager();
        Log.e("onEventMainThread","-------"+message);
			if (message.equalsIgnoreCase("ProfileInfo True")) {
                setProfileData();
            }else if (message.equalsIgnoreCase("ProfileInfo False")) {

            }else if (message.equalsIgnoreCase("ProfileInfoNetwork Error")) {
                Utils.fromSignalDialog(this, AlertMessage.connectionError);
			}else if (message.equalsIgnoreCase("Fetchprofilerelationships True")) {
				setlist();
            }else if (message.equalsIgnoreCase("Fetchprofilerelationships False")) {

            }else if (message.equalsIgnoreCase("Fetchprofilerelationships Network Error")) {
                Utils.fromSignalDialog(this, AlertMessage.connectionError);
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
				Utils.fromSignalDialog(JumpOtherProfileView.this, relationManager.getStatusMsg());
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

   if(authManager.getTmpGender()!=null) {
                if (authManager.getTmpGender().matches("guy")) {

                    try {
                        if (!authManager.getTmpUserPic().equalsIgnoreCase("")) {
                            Picasso.with(JumpOtherProfileView.this)
                                    .load(authManager.getTmpUserPic())
                                    .skipMemoryCache()
                                    .error(R.drawable.male_user).into(userimage);
                        } else {
                            userimage.setImageResource(R.drawable.male_user);
                        }
                    } catch (Exception e) {
                        userimage.setImageResource(R.drawable.male_user);
                    }
                } else {
                    try {
                        if (!authManager.getTmpUserPic().equalsIgnoreCase("")) {
                            Picasso.with(JumpOtherProfileView.this)
                                    .load(authManager.getTmpUserPic())
                                    .skipMemoryCache()

                                    .error(R.drawable.female_user).into(userimage);
                        } else {
                            userimage.setImageResource(R.drawable.female_user);
                        }
                    } catch (Exception e) {
                        userimage.setImageResource(R.drawable.female_user);
                    }
                }
            }
            else
            {
                userimage.setImageResource(R.drawable.male_user);
            }
        }
    }


   // Akshit Code Starts
    public void dialogClickwith(){

        final Dialog dialog = new Dialog(JumpOtherProfileView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_other_profile_view);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
//        msgI.setText(str);
//        msgI.setText(AlertMessage.CURRENTCLICKERPAGE);
          msgII.setText(AlertMessage.MAKECLICKWITH + authManager.getTmpUserName());
        Button skip = (Button)dialog.findViewById(R.id.coolio);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authManager = ModelManager.getInstance().getAuthorizationManager();

                authManager.sendNewRequest(authManager.getPhoneNo(), phForOtherUser, authManager.getUsrToken());
                dialog.dismiss();
            }
        });

        Button dismiss = (Button) dialog.findViewById(R.id.coolio1);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
// Ends
}
