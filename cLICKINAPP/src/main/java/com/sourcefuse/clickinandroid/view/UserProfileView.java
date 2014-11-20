package com.sourcefuse.clickinandroid.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ClickInNotificationManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter1;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter1.Section;
import com.sourcefuse.clickinandroid.view.adapter.UserRelationAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserProfileView extends ClickInBaseView implements View.OnClickListener {
    private static final String TAG = UserProfileView.class.getSimpleName();
	private Button follower, following,btnAddSomeone,EditProfile;
	private TextView profileHeader;
	private ListView mUserRelationlistView;
	private ImageView userimage;
	public UserRelationAdapter adapter;
	private AuthManager authManager;
	private RelationManager relationManager;
	private TextView name,userdetails;
	public String phone;
    private ClickInNotificationManager notificationMngr;
	private Typeface typefaceBold,typefaceMedium;
	private View footerView ;
    private Bitmap imageBitmap;
	
	/*
	 * 
	 * HeaderRelated Things
	 */


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.view_userprofile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addMenu(true);
        this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        typefaceMedium = Typeface.createFromAsset(UserProfileView.this.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        typefaceBold = Typeface.createFromAsset(UserProfileView.this.getAssets(),Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);

        following = (Button) findViewById(R.id.btn_following);
        follower = (Button) findViewById(R.id.btn_follower);
        EditProfile = (Button) findViewById(R.id.btn_edit_profile);

        mUserRelationlistView = (ListView) findViewById(R.id.list_click_with_profile);

        TextView title = (TextView)findViewById(R.id.tv_profile_txt);
        title.setText("PROFILE");


        // menu = (ImageView) findViewById(R.id.iv_menu);
        // notification = (ImageView) findViewById(R.id.iv_notification);

        ((ImageView) findViewById(R.id.iv_open_left_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                slidemenu.showMenu(true);
            }
        });
        ((ImageView) findViewById(R.id.iv_open_right_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                slidemenu.showSecondaryMenu(true);
            }
        });


        userimage = (ImageView) findViewById(R.id.iv_usr_icon);
        userimage.setScaleType(ScaleType.FIT_XY);

        name = (TextView) findViewById(R.id.tv_name);
        userdetails = (TextView) findViewById(R.id.tv_user_details);
        profileHeader = (TextView) findViewById(R.id.tv_profile_txt);


        following.setOnClickListener(this);
        follower.setOnClickListener(this);
        //menu.setOnClickListener(this);
        // notification.setOnClickListener(this);
        EditProfile.setOnClickListener(this);

        name.setTypeface(typefaceBold);
        userdetails.setTypeface(typefaceMedium);
        profileHeader.setTypeface(typefaceBold);


        following.setTypeface(typefaceBold);
        follower.setTypeface(typefaceBold);




        //code to set adapter to populate list
        footerView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_pro_list, null, false);
        mUserRelationlistView.addFooterView(footerView);



/*not required to check whether it is logged in or not.
    ** Because on this page it always come after loggin in either manual or through auto login
 */
        /*  if (new MyPreference(UserProfileView.this).isLogin()) {

            Log.e("ClickInBaseView1", "MyPreference");

                Log.e("ClickInBaseView1", "getInstance");
                authManager = ModelManager.getInstance().getAuthorizationManager();
                authManager.setUsrToken(new MyPreference(UserProfileView.this).getToken());
                authManager.setPhoneNo(new MyPreference(UserProfileView.this).getmyPhoneNo());





        }*/


    //    Utils.launchBarDialog(this);
		authManager = ModelManager.getInstance().getAuthorizationManager();
        notificationMngr = ModelManager.getInstance().getNotificationManagerManager();
      //  notificationMngr.getNotification("", authManager.getPhoneNo(), authManager.getUsrToken());

        //make next webservice request after current is finished
        Bundle b=getIntent().getExtras();
        boolean FromSignup=false;
        if(b!=null){
            if(b.containsKey("FromSignup")){
                 FromSignup = getIntent().getExtras().getBoolean("FromSignup");
            }
        }

        if(FromSignup){
            authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
        }else {
            setNotificationList();
            setLeftMenuList();
            setProfileDataView();
            setlist();
         //   relationManager = ModelManager.getInstance().getRelationManager();
           // relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        }
        Log.e(TAG,"vv"+authManager.getPhoneNo()+""+authManager.getUsrToken());
        //profile information is already set in signIn view or in splash screen for auto login
      //  authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
      //  relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
      //  ModelManager.getInstance().getProfileManager().getFollwer("", authManager.getPhoneNo(), authManager.getUsrToken());
	   // setProfileDataView();
        //setlist();


            findViewById(R.id.iv_usr_icon).setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
                  }
            });



	}

	public void setProfileDataView() {

        authManager = ModelManager.getInstance().getAuthorizationManager();
        name.setText(authManager.getUserName());
        String dtails = "";
        String gender = "";
        String dob = "";
        try {
            try {
                Log.e(TAG, "Gender -->" + authManager.getGender());
                if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().matches("girl")) {
                    dtails = "Female, ";
                    gender = "girl";
                } else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().matches("guy")) {
                    dtails = "Male,";
                    gender = "guy";
                }
            } catch (Exception e) {
            }
            dtails = dtails + Utils.getCurrentYear(authManager.getdOB()) + " " + getResources().getString(R.string.txt_yold);
        } catch (Exception e) {
        }

        if (!Utils.isEmptyString(authManager.getUserCity()) && Utils.isEmptyString(authManager.getUserCountry())) {
            dtails = dtails + "\n" + authManager.getUserCity();
            userdetails.setText(dtails);
        } else if (Utils.isEmptyString(authManager.getUserCity()) && !Utils.isEmptyString(authManager.getUserCountry())) {
            dtails = dtails + "\n" + authManager.getUserCountry();
            userdetails.setText(dtails);
        } else if (!Utils.isEmptyString(authManager.getUserCity()) && !Utils.isEmptyString(authManager.getUserCountry())) {
            userdetails.setText(dtails + "\n" + authManager.getUserCity() + "," + authManager.getUserCountry());
        } else {
            userdetails.setText(dtails + "\n");
        }
        String text = "<font color=#cccccc>" + authManager.getFollower() + "</font> <font color=#oob0c7>" + getResources().getString(R.string.txt_follower) + "</font>";
        follower.setText(Html.fromHtml(text));
        String textfollowing = "<font color=#f29691>" + getResources().getString(R.string.txt_following) + "</font> <font color=#cccccc>" + authManager.getFollowing() + "</font>";
        following.setText(Html.fromHtml(textfollowing));


        //akshit code start for image and default image at userprofile
        try {
            Uri tempUri = authManager.getUserImageUri();
            Bitmap imagebitmap1 = authManager.getUserbitmap();
            if (imagebitmap1 != null) {
                imageBitmap = authManager.getUserbitmap();

                if (imageBitmap != null) {   //set image bitmap if not null
                    userimage.setImageBitmap(imageBitmap);
                  //  Log.e("bit map greater than zero->","bit map greater than zero->");
                } else { ///if image bitmap null
                    if (authManager.getGender() != null) {//gender not null
                        if (authManager.getGender().equalsIgnoreCase("guy")) {///if guy
                            try {
                                if (authManager.getUserPic() != null) {
                                    Picasso.with(UserProfileView.this)
                                            .load(authManager.getUserPic())
                                            .skipMemoryCache()
                                            .error(R.drawable.male_user)
                                            .into(userimage);
                                 //   Log.e("guy->","guy->");
                                } else {
                                    userimage.setImageResource(R.drawable.male_user);
                                //    Log.e("guy exception->","guy exceptionElse->");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                userimage.setImageResource(R.drawable.male_user);
                           //     Log.e("guy exception->","guy exception->");
                            }
                        } //if guy ends
                        else if (authManager.getGender().equalsIgnoreCase("girl")) {//if girl
                            try {
                                if (authManager.getUserPic()!=null) {//with pic
                                    Picasso.with(UserProfileView.this)
                                            .load(authManager.getUserPic())
                                            .skipMemoryCache()
                                            .error(R.drawable.female_user)
                                            .into(userimage);
                                //    Log.e("Female->","female->");
                                } else {
                                    userimage.setImageResource(R.drawable.female_user);
                                   // Log.e("Female->","female Else->");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                               // Log.e("Female->","female Exception->");
                                userimage.setImageResource(R.drawable.female_user);
                            }
                        }

                    } else// if gender is null set default
                    {
                      //  Log.e("Default Esle->","DEfault Else->");
                        userimage.setImageResource(R.drawable.male_user);
                    }
                }

            } else {//if temp uri null
                if (authManager.getGender() != null) {//gender not null

                    if (authManager.getGender().equalsIgnoreCase("guy")) {//gender guy
                        try {
                            if (authManager.getUserPic()!=null) {
                                Picasso.with(UserProfileView.this)
                                        .load(authManager.getUserPic())
                                        .skipMemoryCache()
                                        .error(R.drawable.male_user)
                                        .into(userimage);
                                //   userimage.setImageResource(R.drawable.male_user);
                          //      Log.e("URL" ,"Url for giving image for guy" +authManager.getUserPic());
                           //     Log.e("guy->","guy->");
                            } else {
                            //    Log.e("guy->","guyElse->");
                                userimage.setImageResource(R.drawable.male_user);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                          //  Log.e("guy->","guyExceptin->");
                            userimage.setImageResource(R.drawable.male_user);
                        }
                    } //gender guy ends
                    else if (authManager.getGender().equalsIgnoreCase("girl")) {//gender girl
                        try {
                            if (authManager.getUserPic()!=null) {
                                Picasso.with(UserProfileView.this)
                                        .load(authManager.getUserPic())
                                        .skipMemoryCache()

                                        .error(R.drawable.female_user)
                                        .into(userimage);
                                Log.e("girl->","GEnder guy two ->");
                                Log.e("url",""+authManager.getUserPic());
                                //userimage.setImageResource(R.drawable.female_user);
                            } else {
                                userimage.setImageResource(R.drawable.female_user);
                              //  Log.e("girel->","Gender Girl Two->");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            userimage.setImageResource(R.drawable.female_user);
                            //Log.e("girel->","Exception Gender Girl Two->");
                        }
                    }//gender girl end

                }//if gender null
                else {
                    if(authManager.getUserPic()!=null){
                        Picasso.with(UserProfileView.this)
                                .load(authManager.getUserPic())
                                .skipMemoryCache()

                                .error(R.drawable.male_user)
                                .into(userimage);
                    }else {
                        userimage.setImageResource(R.drawable.male_user);
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            userimage.setImageResource(R.drawable.male_user);
            Log.e("User Profile View->","total exception->");
        }
//
    }
//akshit code ends





    @Override
    protected void onResume() {
        Log.e("onResume","onResume UserProfile");
        super.onResume();
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if(authManager.isEditProfileFlag()) {
            //data is already updated in authmanager, so no need to make a webservice call
          //  authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
            setProfileDataView();
            authManager.setEditProfileFlag(false);
        }
    }

    public void setlist() {

        ArrayList<Section> sections = new ArrayList<Section>();
        SimpleSectionedListAdapter1 simpleSectionedGridAdapter ;
        relationManager = ModelManager.getInstance().getRelationManager();
		adapter = new UserRelationAdapter(UserProfileView.this, R.layout.row_userprofile, relationManager.getrelationshipsData);
		String[] mHeaderNames = { "CLICKIN'","CLICKIN'"};
	    String[] mHeaderNames2 = { " REQUESTS"," WITH" };
	    Integer[] mHeaderPositions = { 0,relationManager.requestedList.size()};
		int positionOfHeader = 0;
		int noOfHeader = 0;
		
		if(relationManager.acceptedList.size()==0 && relationManager.requestedList.size()==0){
			positionOfHeader =2;
			noOfHeader = 2;
		}else if(relationManager.requestedList.size()>0 && relationManager.acceptedList.size()==0){
			positionOfHeader =0;
			noOfHeader = 1;
		}else if((relationManager.requestedList.size()==0) && (relationManager.acceptedList.size()>0 )){
	    	positionOfHeader =1;
	    	noOfHeader = 2;
	    }else if((relationManager.requestedList.size()>0) && (relationManager.requestedList.size()>0)){
	    	positionOfHeader = 0;
	    	noOfHeader = 2;
	    }
        for (int i = positionOfHeader; i <noOfHeader; i++) {
			sections.add(new Section(mHeaderPositions[i], mHeaderNames[i], mHeaderNames2[i]));
		}
		simpleSectionedGridAdapter = new SimpleSectionedListAdapter1(UserProfileView.this, adapter,R.layout.list_item_header, R.id.tv_clickintx,R.id.tv_with);
		simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
        mUserRelationlistView.setAdapter(simpleSectionedGridAdapter);

        btnAddSomeone = (Button) footerView.findViewById(R.id.btn_add_someone);
        btnAddSomeone.setOnClickListener(this);
	}
	/*
	 * Header Related things
	 */
    @Override
	public void onClick(View v) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        ProfileManager profileManager = ModelManager.getInstance().getProfileManager();
		switch (v.getId()) {
		case R.id.btn_follower:
            Intent intentFollower = new Intent(UserProfileView.this,FollowerList.class);
            intentFollower.putExtra("FromOwnProfile", true);
            startActivity(intentFollower);
			break;
		case R.id.btn_following:
            Intent intentFollowing = new Intent(UserProfileView.this,FollowingListView.class);
            intentFollowing.putExtra("FromOwnProfile", true);
            startActivity(intentFollowing);
			break;
		case R.id.iv_menu:
			Log.e("iv_menu","iv_menu");
			slidemenu.showMenu(true);

			break;
		case R.id.iv_notification:
			Log.e("iv_notification","iv_notification");
			slidemenu.showSecondaryMenu(true);
			break;
		case R.id.btn_add_someone:
			Intent intent = new Intent(UserProfileView.this,AddSomeoneView.class);
            intent.putExtra("FromOwnProfile", true);
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
        case R.id.btn_edit_profile:
            Intent editProfile = new Intent(UserProfileView.this,EditMyProfileView.class);
            startActivity(editProfile);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                break;
		}
	}

    @Override
    public void onStart() {
          super.onStart();


    }

    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();


    }
    public void onEventMainThread(String message){
        super.onEventMainThread(message);
    Log.e(TAG,"onEventMainThread-->"+message);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();
       if (message.equalsIgnoreCase("deleteRelationship True")) {
           relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        } else if (message.equalsIgnoreCase("deleteRelationship False")) {
           Utils.dismissBarDialog();
           Utils.fromSignalDialog(this,authManager.getMessage());
            //Utils.showAlert(UserProfileView.this, authManager.getMessage());
        } else if(message.equalsIgnoreCase("deleteRelationship Error")){
            Utils.dismissBarDialog();
              Utils.fromSignalDialog(this,AlertMessage.connectionError);

            //Utils.showAlert(UserProfileView.this, AlertMessage.connectionError);
        }else if (message.equalsIgnoreCase("GetRelationShips False")) {
           Utils.dismissBarDialog();
           doRestInitialization();
//           setLeftMenuList();
  //         setlist();
       } else if(message.equalsIgnoreCase("GetRelationShips Network Error")){
           Utils.dismissBarDialog();
           Utils.fromSignalDialog(this,AlertMessage.connectionError);
           //Utils.showAlert(UserProfileView.this, AlertMessage.connectionError);
       }else if (message.equalsIgnoreCase("updateStatus true")) {
           relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
        }/*else if (message.equalsIgnoreCase("SearchResult True")) {
            Utils.dismissBarDialog();
            stopSearch = true;
            searchList.setVisibility(View.VISIBLE);
            setSearchList();
        }*/else if(message.equalsIgnoreCase("UserVisible true")){
            adapter.notifyDataSetChanged();
            Log.d("3", "message->" + message);
        }else if(message.equalsIgnoreCase("Notification true")){
      //     relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
           // Utils.dismissBarDialog();
          /*  new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    setNotificationList();
                }
            }, 2000);*/

        }else if(message.equalsIgnoreCase("GetrelationShips True")){
           Utils.dismissBarDialog();
           // setLeftMenuList();
         //  ModelManager.getInstance().getProfileManager().getFollwer("", authManager.getPhoneNo(), authManager.getUsrToken());
           doRestInitialization();
        }else if (message.equalsIgnoreCase("ProfileInfo True")) {
           Log.e(TAG,"ProfileInfo True");
           //setProfileDataView();
           relationManager. getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
       } else if (message.equalsIgnoreCase("ProfileInfo False")) {
           Utils.dismissBarDialog();
           Utils.fromSignalDialog(this,authManager.getMessage());
           //Utils.showAlert(UserProfileView.this, authManager.getMessage());
       } else if(message.equalsIgnoreCase("ProfileInfo Network Error")){
           Utils.dismissBarDialog();
           Utils.fromSignalDialog(this,AlertMessage.connectionError);
           //Utils.showAlert(UserProfileView.this, AlertMessage.connectionError);
       } /*else if(message.equalsIgnoreCase("GetFollower True")){
           Utils.dismissBarDialog();
           doRestInitialization();
        }*/


    }

    @Override
    public void onBackPressed() {

    }

    private void switchView() {
		Intent intent = new Intent(UserProfileView.this, FollowerList.class);
	//	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		//this.finish();
	}
	
	private void switchViewToFollowingList() {
		Intent intent = new Intent(UserProfileView.this, FollowingListView.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		//this.finish();
	}

    private void doRestInitialization(){

        setNotificationList();
        setLeftMenuList();
        setProfileDataView();
        setlist();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        relationManager = ModelManager.getInstance().getRelationManager();
        relationManager.getRelationShips(authManager.getPhoneNo(), authManager.getUsrToken());
    }

    public void onDestroy(){
        super.onDestroy();

        Log.e("UserprofileView","Destroy");
    }
}
