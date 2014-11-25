package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.SettingManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.MyCustomAnimation;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.DeactivateAccountView;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class SettingView extends Activity implements View.OnClickListener {
      private ImageView backarrow;
      private static final String TAG = ChatRecordView.class.getSimpleName();
      boolean checkboolean = false, checkboolean1 = false, checkboolean2 = false, checkboolean3 = false, checkboolean4 = false, checkboolean5 = false, checkboolean6 = false;

      LinearLayout sharing_layout, main_password_layout, password_layout, report_layout, not_working_layout,
              general_feed_layout, logout_layout;
      int height;
      private Typeface typefacemedium;
      private Typeface typefaceBold;
      private AuthManager authManager;
      private SettingManager settingManager;
      private UiLifecycleHelper uiHelper;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.view_setting);
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            backarrow = (ImageView) findViewById(R.id.iv_back_noti);
            backarrow.setOnClickListener(this);
            sharing_layout = (LinearLayout) findViewById(R.id.sharing_layout);
            main_password_layout = (LinearLayout) findViewById(R.id.main_password_layout);
            password_layout = (LinearLayout) findViewById(R.id.password_layout);
            report_layout = (LinearLayout) findViewById(R.id.report_layout);
            not_working_layout = (LinearLayout) findViewById(R.id.not_working_layout);
            general_feed_layout = (LinearLayout) findViewById(R.id.general_feed_layout);
            logout_layout = (LinearLayout) findViewById(R.id.logout_layout);

            typefacemedium = Typeface.createFromAsset(SettingView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
            typefaceBold = Typeface.createFromAsset(SettingView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);
            authManager = ModelManager.getInstance().getAuthorizationManager();
            settingManager = ModelManager.getInstance().getSettingManager();

            ((TextView) findViewById(R.id.sharing_text)).setOnClickListener(this);
            ((TextView) findViewById(R.id.account_text)).setOnClickListener(this);
            ((TextView) findViewById(R.id.change_password)).setOnClickListener(this);
            ((TextView) findViewById(R.id.report_text)).setOnClickListener(this);
            ((TextView) findViewById(R.id.not_working_text)).setOnClickListener(this);
            ((TextView) findViewById(R.id.general_feed_back_text)).setOnClickListener(this);
            ((TextView) findViewById(R.id.logout_text)).setOnClickListener(this);
            ((TextView) findViewById(R.id.deactivate_account)).setOnClickListener(this);
            ((TextView) findViewById(R.id.privacy_policy_row)).setOnClickListener(this);
            ((TextView) findViewById(R.id.term_use_row)).setOnClickListener(this);
            ((TextView) findViewById(R.id.spam_text)).setOnClickListener(this);
            ((TextView) findViewById(R.id.edit_profile)).setOnClickListener(this);
            ((TextView) findViewById(R.id.btn_logout_yes)).setOnClickListener(this);
            ((TextView) findViewById(R.id.btn_logout_no)).setOnClickListener(this);
            ((TextView) findViewById(R.id.save_password)).setOnClickListener(this);
            ((TextView) findViewById(R.id.btn_send_not_working)).setOnClickListener(this);
            ((TextView) findViewById(R.id.btn_send_general_problem)).setOnClickListener(this);


            ((LinearLayout) findViewById(R.id.facebook_layout)).setOnClickListener(this);
            ((LinearLayout) findViewById(R.id.twitter_layout)).setOnClickListener(this);
            ((LinearLayout) findViewById(R.id.google_plus_layout)).setOnClickListener(this);

            ((TextView) findViewById(R.id.tv_child_title)).setTypeface(typefacemedium);
            ((TextView) findViewById(R.id.twitter_title)).setTypeface(typefacemedium);
            ((TextView) findViewById(R.id.google_plus_title)).setTypeface(typefacemedium);
            ((TextView) findViewById(R.id.edit_profile)).setTypeface(typefacemedium);
            ((TextView) findViewById(R.id.change_password)).setTypeface(typefacemedium);
            ((TextView) findViewById(R.id.deactivate_account)).setTypeface(typefacemedium);

            ((EditText) findViewById(R.id.old_password)).setTypeface(typefacemedium);
            ((EditText) findViewById(R.id.new_password)).setTypeface(typefacemedium);
            ((EditText) findViewById(R.id.confirm_password)).setTypeface(typefacemedium);
            ((EditText) findViewById(R.id.problem_text)).setTypeface(typefacemedium);
            ((EditText) findViewById(R.id.general_problem_text)).setTypeface(typefacemedium);

            ((TextView) findViewById(R.id.save_password)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.btn_send_not_working)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.btn_send_general_problem)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.btn_logout_yes)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.btn_logout_no)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.push_notification_row)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.in_app_sound_row)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.sharing_text)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.account_text)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.privacy_policy_row)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.term_use_row)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.report_text)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.logout_text)).setTypeface(typefaceBold);
            ((TextView) findViewById(R.id.tv_profile_txt)).setTypeface(typefaceBold);

            ((TextView) findViewById(R.id.spam_text)).setTypeface(typefacemedium);
            ((TextView) findViewById(R.id.tv_logout_msg)).setTypeface(typefacemedium);
            ((TextView) findViewById(R.id.not_working_text)).setTypeface(typefacemedium);
            ((TextView) findViewById(R.id.general_feed_back_text)).setTypeface(typefacemedium);

//  for facebook share

            uiHelper = new UiLifecycleHelper(this, callback);
            uiHelper.onCreate(savedInstanceState);
      }

      @Override
      public void onClick(View view) {
            switch (view.getId()) {
                  case R.id.sharing_text:
                        if (sharing_layout.getVisibility() == View.VISIBLE) {
                              MyCustomAnimation a = new MyCustomAnimation(sharing_layout, 300, MyCustomAnimation.COLLAPSE);
                              height = a.getHeight();
                              sharing_layout.startAnimation(a);
                        } else {
                              MyCustomAnimation a = new MyCustomAnimation(sharing_layout, 300, MyCustomAnimation.EXPAND);
                              if (checkboolean == false) {
                                    height = 300;
                              }
                              checkboolean = true;
                              a.setHeight(height);
                              sharing_layout.startAnimation(a);
                        }
                        break;
                  case R.id.account_text:

                        if (main_password_layout.getVisibility() == View.VISIBLE) {
                              MyCustomAnimation a = new MyCustomAnimation(main_password_layout, 300, MyCustomAnimation.COLLAPSE);
                              height = a.getHeight();
                              main_password_layout.startAnimation(a);
                              password_layout.setVisibility(View.GONE);
                        } else {
                              MyCustomAnimation a = new MyCustomAnimation(main_password_layout, 300, MyCustomAnimation.EXPAND);
                              if (checkboolean1 == false) {
                                    height = 300;
                              }
                              checkboolean1 = true;
                              a.setHeight(height);
                              main_password_layout.startAnimation(a);
                        }
                        break;
                  case R.id.change_password:
                        if (password_layout.getVisibility() == View.VISIBLE) {
                              MyCustomAnimation a = new MyCustomAnimation(password_layout, 300, MyCustomAnimation.COLLAPSE);
                              height = a.getHeight();
                              password_layout.startAnimation(a);
                        } else {
                              MyCustomAnimation a = new MyCustomAnimation(password_layout, 300, MyCustomAnimation.EXPAND);
                              if (checkboolean2 == false) {
                                    height = 300;
                              }
                              checkboolean2 = true;
                              a.setHeight(height);
                              password_layout.startAnimation(a);
                        }
                        break;
                  case R.id.report_text:
                        if (report_layout.getVisibility() == View.VISIBLE) {
                              MyCustomAnimation a = new MyCustomAnimation(report_layout, 300, MyCustomAnimation.COLLAPSE);
                              height = a.getHeight();
                              report_layout.startAnimation(a);
                        } else {
                              MyCustomAnimation a = new MyCustomAnimation(report_layout, 300, MyCustomAnimation.EXPAND);
                              if (checkboolean3 == false) {
                                    height = 300;
                              }
                              checkboolean3 = true;
                              a.setHeight(height);
                              report_layout.startAnimation(a);
                        }
                        break;
                  case R.id.not_working_text:
                        if (not_working_layout.getVisibility() == View.VISIBLE) {
                              MyCustomAnimation a = new MyCustomAnimation(not_working_layout, 300, MyCustomAnimation.COLLAPSE);
                              height = a.getHeight();
                              not_working_layout.startAnimation(a);
                        } else {
                              MyCustomAnimation a = new MyCustomAnimation(not_working_layout, 300, MyCustomAnimation.EXPAND);
                              if (checkboolean4 == false) {
                                    height = 300;
                              }
                              checkboolean4 = true;
                              a.setHeight(height);
                              not_working_layout.startAnimation(a);
                        }
                        break;
                  case R.id.general_feed_back_text:
                        if (general_feed_layout.getVisibility() == View.VISIBLE) {
                              MyCustomAnimation a = new MyCustomAnimation(general_feed_layout, 300, MyCustomAnimation.COLLAPSE);
                              height = a.getHeight();
                              general_feed_layout.startAnimation(a);
                        } else {
                              MyCustomAnimation a = new MyCustomAnimation(general_feed_layout, 300, MyCustomAnimation.EXPAND);
                              if (checkboolean5 == false) {
                                    height = 300;
                              }
                              checkboolean5 = true;
                              a.setHeight(height);
                              general_feed_layout.startAnimation(a);
                        }
                        break;
                  case R.id.logout_text:
                        if (logout_layout.getVisibility() == View.VISIBLE) {
                              MyCustomAnimation a = new MyCustomAnimation(logout_layout, 300, MyCustomAnimation.COLLAPSE);
                              height = a.getHeight();
                              logout_layout.startAnimation(a);
                        } else {
                              MyCustomAnimation a = new MyCustomAnimation(logout_layout, 300, MyCustomAnimation.EXPAND);
                              if (checkboolean6 == false) {
                                    height = 300;
                              }
                              checkboolean6 = true;
                              a.setHeight(height);
                              logout_layout.startAnimation(a);
                        }
                        break;
                  case R.id.deactivate_account:
                        Intent intent = new Intent(this, DeactivateAccountView.class);
                        startActivity(intent);
                        break;
                  case R.id.privacy_policy_row:
                        Intent intent1 = new Intent(this, PrivacyView.class);
                        startActivity(intent1);
                        break;
                  case R.id.term_use_row:
                        Intent intent2 = new Intent(this, TermUseView.class);
                        startActivity(intent2);
                        break;
                  case R.id.spam_text:
                        Intent intent3 = new Intent(this, ViewSpamorAbuse.class);
                        startActivity(intent3);
                        break;
                  case R.id.edit_profile:
                        Intent intent4 = new Intent(this, EditMyProfileView.class);
                        startActivity(intent4);
                        break;
                  case R.id.btn_logout_yes:
                        //   new MyPreference(getApplicationContext()).clearAllPreference();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        Log.e("", "holder.logoutYes");
                        Intent intent5 = new Intent(SettingView.this, SplashView.class);
                        intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent5);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left,R.anim.top_out);
                    //  this.finishAndRemoveTask();
                        break;
                  case R.id.btn_logout_no:
                        MyCustomAnimation a = new MyCustomAnimation(logout_layout, 300, MyCustomAnimation.COLLAPSE);
                        logout_layout.startAnimation(a);
                        break;
// share on facebook
                  case R.id.facebook_layout:

                        if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {

                              FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                                                                   .setLink(Constants.APP_LINK_URL)
                                                                   .build();
                              uiHelper.trackPendingDialogCall(shareDialog.present());

                        } else {
                              Log.e(TAG, "Success!");
                        }


                        break;
// share on Twitter
                  case R.id.twitter_layout:
                        break;
// share on Google Plus
                  case R.id.google_plus_layout:
                        break;
// save password
                  case R.id.save_password:

                        String old_password, new_password, confirm_password, phone_no, user_token;
                        // get value of all passwords
                        old_password = ((EditText) findViewById(R.id.old_password)).getText().toString();
                        new_password = ((EditText) findViewById(R.id.new_password)).getText().toString();
                        confirm_password = ((EditText) findViewById(R.id.confirm_password)).getText().toString();
                        phone_no = authManager.getPhoneNo();
                        user_token = authManager.getUsrToken();

                        if (!Utils.isEmptyString(old_password) && !Utils.isEmptyString(new_password) && !Utils.isEmptyString(confirm_password) && new_password.equalsIgnoreCase(confirm_password) && old_password.length() >= 8 && new_password.length() >= 8 && confirm_password.length() >= 8) {
                              Utils.launchBarDialog(SettingView.this);
                              settingManager.changePassword(phone_no, user_token, old_password, new_password, confirm_password);
                        } else if (!new_password.equalsIgnoreCase(confirm_password)) {
                            Utils.fromSignalDialog(this,AlertMessage.CHANGE_PASSWORD_NOT_MATCH);
                              //Utils.showAlert(this, AlertMessage.CHANGE_PASSWORD_NOT_MATCH);
                        } else if (Utils.isEmptyString(old_password) || Utils.isEmptyString(new_password) || Utils.isEmptyString(confirm_password)) {
                              Utils.fromSignalDialog(this, AlertMessage.CHANGE_PASSWORD);
                        } else if (old_password.length() < 8 || new_password.length() < 8 || confirm_password.length() < 8) {

                              Utils.fromSignalDialog(this, AlertMessage.CHANGE_PASSWORD_CHARACTER);
                        }

                        break;

// Not working problem

                case R.id.btn_send_not_working:
                        String mphone_no, muser_token, problem_type, spam_or_abuse_type, comment;
                        mphone_no = authManager.getPhoneNo();
                        muser_token = authManager.getUsrToken();
                        problem_type = "notworking";
                        EditText text = (EditText)findViewById(R.id.problem_text);


                       comment = ((EditText) findViewById(R.id.problem_text)).getText().toString();

                        spam_or_abuse_type = "";
                        if (Utils.isEmptyString(comment)) {
                              comment = "";
                        }


                        settingManager.reportaproblem(mphone_no, muser_token, problem_type, spam_or_abuse_type, comment);
                        text.setHint("Briefly explain what happened.");

                        break;

// general feedback
                  case R.id.btn_send_general_problem:
                        String mphone_no_general, muser_token_general, problem_type_general, spam_or_abuse_type_general, comment_general;

                        comment_general = ((EditText) findViewById(R.id.general_problem_text)).getText().toString();
                        mphone_no_general = authManager.getPhoneNo();
                        muser_token_general = authManager.getUsrToken();
                        problem_type_general = "feedback";
                        spam_or_abuse_type_general = "";

                        if (Utils.isEmptyString(comment_general)) {
                              comment_general = "";
                        }
                        settingManager.reportaproblem(mphone_no_general, muser_token_general, problem_type_general, spam_or_abuse_type_general, comment_general);

                        break;
                  case R.id.iv_back_noti:
                        finish();
                        overridePendingTransition(0, R.anim.top_out);

                        break;
            }
      }

      @Override
      public void onBackPressed() {
            super.onBackPressed();
            finish();
            overridePendingTransition(0, R.anim.top_out);
      }

      @Override
      public void onStart() {
            super.onStart();
            if (EventBus.getDefault().isRegistered(this)) {
                  EventBus.getDefault().unregister(this);
            }
            EventBus.getDefault().register(this);
      }

      public void onEventMainThread(String message) {
            if (message.equalsIgnoreCase("ChangePassword True")) {
                  Utils.dismissBarDialog();
                  Utils.fromSignalDialog(this, AlertMessage.CHANGE_PASSWORD_SUCESS);
            }
            if (message.equalsIgnoreCase("ChangePassword Network Error")) {
                  Utils.dismissBarDialog();
                  Utils.fromSignalDialog(this, AlertMessage.connectionError);
            }
            if (message.equalsIgnoreCase("ChangePassword False")) {
                  Utils.dismissBarDialog();
                  Utils.fromSignalDialog(this, AlertMessage.CHANGE_PASSWORD_FAILURE);
            }
            if (message.equalsIgnoreCase("ReportaProblem True")) {
                  Utils.dismissBarDialog();

// message on success
                  //Utils.showAlert(this,);
            }
            if (message.equalsIgnoreCase("ReportaProblem False")) {
                  Utils.dismissBarDialog();

// message on failure
                  //Utils.showAlert(this,);
            }
            if (message.equalsIgnoreCase("ReportaProblem Network Error")) {
                  Utils.dismissBarDialog();

// message on onnetwork error
                  //Utils.showAlert(this,);
            }
      }


// for facebook

      private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                  Log.e(TAG, String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                  Log.e(TAG, "Success!");
            }
      };

      private Session.StatusCallback callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                  onSessionStateChange(session, state, exception);
            }
      };

      private void onSessionStateChange(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                  Log.i(TAG, "Logged in...");
            } else if (state.isClosed()) {
                  Log.i(TAG, "Logged out...");
            }
      }

      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
      }

    public void onDestroy(){
        super.onDestroy();
        Log.e("SettingsView","Destroy");
    }


}
