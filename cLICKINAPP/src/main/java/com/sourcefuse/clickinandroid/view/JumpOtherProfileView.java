package com.sourcefuse.clickinandroid.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
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
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.JumpOtherProfileAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public class JumpOtherProfileView extends ClickInBaseView implements View.OnClickListener {

    public String phone, phForOtherUser;
    TextView follow;
    String dtails;
    String partner_ID;
    private TextView follower, following;
    private TextView profileHeader, tvclickintx, tvwith, othesProfileName, clickwithHead, clickwithNameHead;
    private ListView listView;
    private ImageView userimage;
    private RelativeLayout rlClickWith;
    private JumpOtherProfileAdapter adapter;
    private AuthManager authManager;
    private RelationManager relationManager;
    private TextView name, userdetails;
    private boolean othersUser = false;
    private Typeface typeface;
    private boolean whichList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        typeface = Typeface.createFromAsset(JumpOtherProfileView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);

        authManager = ModelManager.getInstance().getAuthorizationManager();

        othersUser = getIntent().getExtras().getBoolean("FromOwnProfile");
        if (othersUser) {
            authManager = ModelManager.getInstance().getAuthorizationManager();
            relationManager = ModelManager.getInstance().getRelationManager();

            phForOtherUser = getIntent().getExtras().getString("phNumber");
            partner_ID = getIntent().getExtras().getString("PartnerId");
            Utils.launchBarDialog(this);

            authManager.getProfileInfo(phForOtherUser, authManager.getPhoneNo(), authManager.getUsrToken());

        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("FromOwnProfile") && intent.getBooleanExtra("FromOwnProfile", false)) {
            authManager = ModelManager.getInstance().getAuthorizationManager();
            relationManager = ModelManager.getInstance().getRelationManager();
            phForOtherUser = intent.getExtras().getString("phNumber");
            Utils.launchBarDialog(this);
            authManager.getProfileInfo(phForOtherUser, authManager.getPhoneNo(), authManager.getUsrToken());
            if (slidemenu.isMenuShowing())
                slidemenu.showContent();
        }
    }

    /* akshit code to set data */
    public void setView() {
        setContentView(R.layout.view_othersprofile);
        dtails = "";

        addMenu(false);
        listView = (ListView) findViewById(R.id.list_click_with_other);
        following = (TextView) findViewById(R.id.btn_following_other);
        follower = (TextView) findViewById(R.id.btn_follower_other);
        follow = (TextView) findViewById(R.id.btn_follow);
        userimage = (ImageView) findViewById(R.id.iv_usr_icon);
        name = (TextView) findViewById(R.id.tv_name_other);
        userdetails = (TextView) findViewById(R.id.tv_user_details_other);
        profileHeader = (TextView) findViewById(R.id.tv_profile_other);
        tvclickintx = (TextView) findViewById(R.id.tv_clickintx);
        tvwith = (TextView) findViewById(R.id.tv_with);
        othesProfileName = (TextView) findViewById(R.id.tv_profile_other);
        rlClickWith = (RelativeLayout) findViewById(R.id.rl_add_someone);

        clickwithHead = (TextView) findViewById(R.id.tv_click_with_head);
        clickwithNameHead = (TextView) findViewById(R.id.tv_clickwith_name_head);


        rlClickWith.setOnClickListener(this);
        follow.setOnClickListener(this);
        following.setOnClickListener(this);
        follower.setOnClickListener(this);
        name.setTypeface(typeface, typeface.BOLD);
        othesProfileName.setTypeface(typeface, typeface.BOLD);
        profileHeader.setTypeface(typeface, typeface.BOLD);
//        tvclickintx.setTypeface(typeface, typeface.BOLD);

        following.setTypeface(typeface, typeface.BOLD);
        follower.setTypeface(typeface, typeface.BOLD);
        userimage.setScaleType(ScaleType.FIT_XY);
//        tvwith.setTypeface(typeface);
        userdetails.setTypeface(typeface);


        if (othersUser) {
            name.setText(getIntent().getExtras().getString("name"));
            profileHeader.setText(getIntent().getExtras().getString("name"));
            if (authManager.getUserName() != null) {
                if (name.getText().toString().trim().equalsIgnoreCase(authManager.getUserName().toString().trim())) {
                    rlClickWith.setVisibility(View.GONE);
                    follow.setVisibility(View.GONE);
                } else {
                    rlClickWith.setVisibility(View.VISIBLE);
                    follow.setVisibility(View.VISIBLE);
                }
            } else {
                rlClickWith.setVisibility(View.VISIBLE);
                follow.setVisibility(View.VISIBLE);
            }

        }

        //akshit code for click if user is clicking on same User
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (relationManager.profileRelationShipData.size() > 0 && !relationManager.profileRelationShipData.get(position).getPartner_id().equalsIgnoreCase(authManager.getUserId())) {
                    String phNo = relationManager.profileRelationShipData.get(position).getPhoneNo();
                    switchView(phNo);
                } else {
                }
            }
        });

    }//ends

    public void setlist() {
        if (relationManager.profileRelationShipData.size() == 0) {
            findViewById(R.id.ll_clickin_header).setVisibility(View.GONE);
            ((View) findViewById(R.id.v_devider_header)).setVisibility(View.GONE);
            adapter = new JumpOtherProfileAdapter(this, R.layout.row_othersprofile, relationManager.profileRelationShipData);
            listView.setAdapter(adapter);
        } else {
            ((LinearLayout) findViewById(R.id.ll_clickin_header)).setVisibility(View.VISIBLE);
            ((View) findViewById(R.id.v_devider_header)).setVisibility(View.VISIBLE);
            adapter = new JumpOtherProfileAdapter(this, R.layout.row_othersprofile, relationManager.profileRelationShipData);
            listView.setAdapter(adapter);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_follower_other:
                String s = new String(name.getText().toString());

                Intent intentFollower = new Intent(JumpOtherProfileView.this, OthersFollowingFollowView.class);
                intentFollower.putExtra("FromOwnProfile", false);
                intentFollower.putExtra("phoneNo", phForOtherUser);
                intentFollower.putExtra("isFollowing", false);
                if (s.contains(" "))
                    intentFollower.putExtra("name", name.getText().toString().substring(0, name.getText().toString().indexOf(" ")));
                else
                    intentFollower.putExtra("name", name.getText().toString());
                startActivity(intentFollower);

                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btn_following_other:
                //Intent intentFollowing = new Intent(JumpOtherProfileView.this,FollowingListView.class);
                Intent intentFollowing = new Intent(JumpOtherProfileView.this, OthersFollowingFollowView.class);
                intentFollowing.putExtra("FromOwnProfile", false);
                intentFollowing.putExtra("isFollowing", true);
                intentFollowing.putExtra("phoneNo", phForOtherUser);
                intentFollowing.putExtra("name", name.getText().toString().substring(0, name.getText().toString().indexOf(" ")));
                startActivity(intentFollowing);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btn_follow:
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();
                relationManager.followUser(phForOtherUser, authManager.getPhoneNo(), authManager.getUsrToken());
                break;

            case R.id.rl_add_someone:
                if (Utils.isEmptyString(relationManager.getRelationStatus())) {
                    dialogClickwith();
                }
                break;


        }

    }

    private void switchView() {
        Intent intent = new Intent(JumpOtherProfileView.this, FollowerList.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void switchViewToFollowingList() {
        Intent intent = new Intent(JumpOtherProfileView.this, FollowingListView.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String message) {
        super.onEventMainThread(message);
        relationManager = ModelManager.getInstance().getRelationManager();
        if (message.equalsIgnoreCase("ProfileInfo True")) {
            setView();
            setProfileData();
            relationManager.fetchprofilerelationships(phForOtherUser, authManager.getPhoneNo(), authManager.getUsrToken());

        } else if (message.equalsIgnoreCase("ProfileInfo False")) {
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("ProfileInfoNetwork Error")) {
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("Fetchprofilerelationships True") || message.equalsIgnoreCase("GetRelationShips False") || message.equalsIgnoreCase("GetRelationShips Network Error")) {

            //akshit code
            if (Utils.isEmptyString(relationManager.getRelationStatus())) {
                clickwithHead.setVisibility(View.GONE);
                clickwithNameHead.setText("CLICK WITH\n" + authManager.getTmpUserName());
            } else if (relationManager.getRelationStatus().equalsIgnoreCase("accepted")) {
                clickwithHead.setText("You are already");
                clickwithNameHead.setText("CLICKIN' WITH\n" + authManager.getTmpUserName());
            } else if (relationManager.getRelationStatus().equalsIgnoreCase("requested")) {
                clickwithHead.setText("Requested to");
                clickwithNameHead.setText("CLICK WITH\n" + authManager.getTmpUserName());
            }
            setlist();
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("Fetchprofilerelationships False")) {
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("Fetchprofilerelationships Network Error")) {
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("FollowUser True")) {
            relationManager = ModelManager.getInstance().getRelationManager();
            follow.setBackgroundResource(R.drawable.requested_otherprofile_new);
            //Utils.showToast(JumpOtherProfileView.this, relationManager.getStatusMsg());
        } else if (message.equalsIgnoreCase("FollowUser  false")) {
            relationManager = ModelManager.getInstance().getRelationManager();
            Utils.showToast(JumpOtherProfileView.this, relationManager.getStatusMsg());
        } else if (message.equalsIgnoreCase("UnFollowUser True")) {
            follow.setBackgroundResource(R.drawable.follow_other_profile);
            relationManager = ModelManager.getInstance().getRelationManager();
            Utils.fromSignalDialog(JumpOtherProfileView.this, relationManager.getStatusMsg());
        } else if (message.equalsIgnoreCase("UnFollowUser  false")) {
            relationManager = ModelManager.getInstance().getRelationManager();
            Utils.showToast(JumpOtherProfileView.this, relationManager.getStatusMsg());
        } else if (message.equalsIgnoreCase("GetFollower True")) {
            Utils.dismissBarDialog();
            if (whichList) {
                switchView();
            } else {
                switchViewToFollowingList();
            }
        } else if (message.equalsIgnoreCase("SearchResult True")) {
            Utils.dismissBarDialog();
            stopSearch = true;
            searchList.setVisibility(View.VISIBLE);
            setSearchList();
        } else if (message.equalsIgnoreCase("RequestSend True")) {

            relationManager.setRelationStatus("requested");
            if (Utils.isEmptyString(relationManager.getRelationStatus())) {
                clickwithHead.setVisibility(View.GONE);
                clickwithNameHead.setText("CLICK WITH\n" + authManager.getTmpUserName());
            } else if (relationManager.getRelationStatus().equalsIgnoreCase("accepted")) {
                clickwithHead.setVisibility(View.VISIBLE);
                clickwithHead.setText("You are already");
                clickwithNameHead.setText("CLICKIN' WITH\n" + authManager.getTmpUserName());
            } else if (relationManager.getRelationStatus().equalsIgnoreCase("requested")) {
                clickwithHead.setVisibility(View.VISIBLE);
                clickwithHead.setText("Requested to");
                clickwithNameHead.setText("CLICK WITH\n" + authManager.getTmpUserName());
            } else if (relationManager.getRelationStatus().equalsIgnoreCase("rejected")) {
                clickwithHead.setVisibility(View.GONE);
                clickwithNameHead.setText("CLICK WITH\n" + authManager.getTmpUserName());
            }


        } else if (message.equalsIgnoreCase("RequestSend False")) {

        } else if (message.equalsIgnoreCase("RequestSend True")) {

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);
    }


    private void switchView(String phone) {
        Intent intent = new Intent(JumpOtherProfileView.this, JumpOtherProfileView.class);
        intent.putExtra("FromOwnProfile", true);
        intent.putExtra("phNumber", phone);
        startActivity(intent);
        finish();
    }

    private void setProfileData() {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        relationManager = ModelManager.getInstance().getRelationManager();

        if (othersUser) {
            othesProfileName.setText(authManager.getTmpUserName());
            name.setText(authManager.getTmpUserName());


            if (authManager.getTmpIsFollowingRequested() == 1) {
                follow.setBackgroundResource(R.drawable.requested_otherprofile_new);
            } else if (authManager.getTmpIsFollowing() == 1) {
                follow.setBackgroundResource(R.drawable.following_otherprofile_new);
            } else {
                follow.setBackgroundResource(R.drawable.follow_other_profile);
            }

            //akshit code ,solved gender
            if (!Utils.isEmptyString(authManager.getTmpGender()) && authManager.getTmpGender().equalsIgnoreCase("girl")) {
                dtails = getResources().getString(R.string.txt_female)//akshit was previously showing Reverse  gender
                        + Utils.getCurrentYear(authManager.getTmpDOB()) + " "
                        + getResources().getString(R.string.txt_yold);

            } else if (!Utils.isEmptyString(authManager.getTmpGender()) && authManager.getTmpGender().equalsIgnoreCase("guy")) {
                dtails = getResources().getString(R.string.txt_male)//akshit was previously showing Reverse  gender
                        + Utils.getCurrentYear(authManager.getTmpDOB()) + " "
                        + getResources().getString(R.string.txt_yold);

            } else {
                if (!Utils.isEmptyString(authManager.getTmpDOB())) {
                    dtails = "" + Utils.getCurrentYear(authManager.getTmpDOB()) + " "
                            + getResources().getString(R.string.txt_yold);
                }
            }

            //akshit code For otherUser City and Country

            if (!Utils.isEmptyString(authManager.getTmpCity()) && Utils.isEmptyString(authManager.getTmpCountry())) {
                dtails = dtails + "\n" + authManager.getTmpCity();
                userdetails.setText(dtails + "\n");

            } else if (Utils.isEmptyString(authManager.getTmpCity()) && !Utils.isEmptyString(authManager.getTmpCountry())) {
                dtails = dtails + "\n" + authManager.getTmpCountry();
                userdetails.setText(dtails + "\n");

            } else if (Utils.isEmptyString(authManager.getTmpCity()) && Utils.isEmptyString(authManager.getTmpCountry())) {
                userdetails.setText(dtails + "\n" + "\n");

            } else {
                userdetails.setText(dtails + "\n" + authManager.getTmpCity() + "," + authManager.getTmpCountry() + "\n" + "\n");

            }

            //ends

            Utils.getCurrentYear(authManager.getTmpDOB());
//            userdetails.setText(dtails + "\n");

            relationManager = ModelManager.getInstance().getRelationManager();

            String text = "<font color=#cccccc>" + authManager.getTmpFollower() + "</font> <font color=#39cad4>" + getResources().getString(R.string.txt_follower) + "</font>";
            follower.setText(Html.fromHtml(text));
            String textfollowing = "<font color=#f29691>" + getResources().getString(R.string.txt_following) + "</font> <font color=#cccccc>" + authManager.getTmpFollowing() + "</font>";
            following.setText(Html.fromHtml(textfollowing));


            if (!Utils.isEmptyString(authManager.getTmpGender())) {
                if (authManager.getTmpGender().equalsIgnoreCase("guy")) {

                    try {
                        if (!authManager.getTmpUserPic().equalsIgnoreCase("")) {
                            /*Picasso.with(this)*/

                            PicassoManager.getPicasso()
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
                            //Picasso.with(this)
                            PicassoManager.getPicasso()
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
            } else {
                String textw = "<font color=#cccccc>" + authManager.getTmpFollower() + "</font> <font color=#39cad4>" + getResources().getString(R.string.txt_follower) + "</font>";
                follower.setText(Html.fromHtml(textw));
                String textfollowings = "<font color=#f29691>" + getResources().getString(R.string.txt_following) + "</font> <font color=#cccccc>" + authManager.getTmpFollowing() + "</font>";
                following.setText(Html.fromHtml(textfollowings));

                if (!authManager.getTmpUserPic().equalsIgnoreCase("")) {
                    Picasso.with(JumpOtherProfileView.this)
                            .load(authManager.getTmpUserPic())
                            .skipMemoryCache()
                            .error(R.drawable.male_user).into(userimage);
                } else {
                    userimage.setImageResource(R.drawable.male_user);
                }
            }
        }
    }


    // Akshit Code Starts
    public void dialogClickwith() {

        final Dialog dialog = new Dialog(JumpOtherProfileView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_other_profile_view);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
//        msgI.setText(str);
//        msgI.setText(AlertMessage.CURRENTCLICKERPAGE);
        msgII.setText(AlertMessage.MAKECLICKWITH + "\n" + authManager.getTmpUserName());//akshit change for fixing UI Related bug
        Button skip = (Button) dialog.findViewById(R.id.coolio);
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
