package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.SettingManager;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.MyCustomAnimation;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.DeactivateAccountView;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class SettingView extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = ChatRecordView.class.getSimpleName();
    public CompoundButton appSound;
    boolean checkboolean = false, checkboolean1 = false, checkboolean2 = false, checkboolean3 = false, checkboolean4 = false, checkboolean5 = false, checkboolean6 = false;
    LinearLayout sharing_layout, main_password_layout, password_layout, report_layout, not_working_layout,
            general_feed_layout, logout_layout;
    int height;
    String problem_type = " ";
    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
        }
    };
    private ImageView backarrow;
    //      private Typeface typefacemedium;
//      private Typeface typefaceBold;
    private AuthManager authManager;
    private SettingManager settingManager;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        //code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

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
//            typefacemedium = Typeface.createFromAsset(SettingView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
//            typefaceBold = Typeface.createFromAsset(SettingView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        settingManager = ModelManager.getInstance().getSettingManager();

        findViewById(R.id.sharing_text).setOnClickListener(this);
        findViewById(R.id.account_text).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);
        findViewById(R.id.report_text).setOnClickListener(this);
        findViewById(R.id.not_working_text).setOnClickListener(this);
        findViewById(R.id.general_feed_back_text).setOnClickListener(this);
        findViewById(R.id.logout_text).setOnClickListener(this);
        findViewById(R.id.deactivate_account).setOnClickListener(this);
        findViewById(R.id.privacy_policy_row).setOnClickListener(this);
        findViewById(R.id.term_use_row).setOnClickListener(this);
        findViewById(R.id.spam_text).setOnClickListener(this);
        findViewById(R.id.edit_profile).setOnClickListener(this);

        findViewById(R.id.btn_logout_yes).setOnClickListener(this);
        findViewById(R.id.btn_logout_no).setOnClickListener(this);
        findViewById(R.id.save_password).setOnClickListener(this);
        findViewById(R.id.btn_send_not_working).setOnClickListener(this);
        findViewById(R.id.btn_send_general_problem).setOnClickListener(this);


        findViewById(R.id.facebook_layout).setOnClickListener(this);
        findViewById(R.id.twitter_layout).setOnClickListener(this);
        findViewById(R.id.google_plus_layout).setOnClickListener(this);


        // akshit code for closing keypad if touched anywhere outside
        ((LinearLayout) findViewById(R.id.linear_layout_root_setting)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TextView view = (TextView) findViewById(R.id.change_password);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (view.getWindowToken() != null)
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


            }

        });

//ends
//akshit code For App Sound
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            appSound = (CompoundButton) findViewById(R.id.toggle_app_sounds);
            appSound.setVisibility(View.VISIBLE);

            if (settingManager.isAppSounds()) {//to check to sate on activity launch,By Default app sound is set Enabled,and To set the position of switch
                appSound.setChecked(true);
            } else {
                appSound.setChecked(false);
            }
            appSound.setOnCheckedChangeListener(this);


            /* for push notification */
            ((CompoundButton) findViewById(R.id.toggle_notification_one)).setVisibility(View.VISIBLE);
            if (SettingManager.mNotification_Enable) {
                ((CompoundButton) findViewById(R.id.toggle_notification_one)).setChecked(true);
            } else {
                ((CompoundButton) findViewById(R.id.toggle_notification_one)).setChecked(false);
            }
            ((CompoundButton) findViewById(R.id.toggle_notification_one)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        try {
                            Utils.getRegId(SettingView.this);
                            // SettingManager.mNotification_Enable = true;
                            //To track through mixPanel.If push notification is enabled by user.
                            Utils.trackMixpanel(SettingView.this, "Activity", "PushNotificationsEnabled", "LeftMenuSettingsButtonClicked", false);
                            settingManager.enableDisablePushNotifications(authManager.getPhoneNo(), authManager.getUsrToken(), "true");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            //  SettingManager.mNotification_Enable = false;
                            //To track through mixPanel.If push notification is disabled by user.
                            Utils.trackMixpanel(SettingView.this, "Activity", "PushNotificationsDisabled", "LeftMenuSettingsButtonClicked", false);
                            settingManager.enableDisablePushNotifications(authManager.getPhoneNo(), authManager.getUsrToken(), "false");
                            Utils.Unregister(SettingView.this);
                        } catch (Exception e) {
                        }
                    }
                }
            });


        } else {
            CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
            if (settingManager.isAppSounds()) {//to check to sate on activity launch,By Default app sound is set Enabled,and To set the position of switch
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            checkBox.setOnCheckedChangeListener(this);

            /* for push notification */


            CheckBox mPushNotification = (CheckBox) findViewById(R.id.checkbox_pushnotificstion);
            mPushNotification.setVisibility(View.VISIBLE);
            mPushNotification.setChecked(true);

            if (SettingManager.mNotification_Enable) {

                mPushNotification.setChecked(true);

            } else {
                mPushNotification.setChecked(false);

            }

            mPushNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        try {
                            Utils.getRegId(SettingView.this);
                            settingManager.enableDisablePushNotifications(authManager.getPhoneNo(), authManager.getUsrToken(), "true");
                            //To track through mixPanel.If push notification is enabled by user.
                            Utils.trackMixpanel(SettingView.this, "Activity", "PushNotificationsEnabled", "LeftMenuSettingsButtonClicked", false);                            //SettingManager.mNotification_Enable = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Utils.Unregister(SettingView.this);
                            settingManager.enableDisablePushNotifications(authManager.getPhoneNo(), authManager.getUsrToken(), "false");
                            //To track through mixPanel.If push notification is Disabled by user.
                            Utils.trackMixpanel(SettingView.this, "Activity", "PushNotificationsDisabled", "LeftMenuSettingsButtonClicked", false);                            //SettingManager.mNotification_Enable = false;
                        } catch (Exception e) {

                        }
                    }
                }
            });


        }
        //ends

        /* for push notification */


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
                    if (!checkboolean) {
                        final LinearLayout sharing_layout = (LinearLayout) findViewById(R.id.facebook_layout);
                        ViewGroup.LayoutParams lp = sharing_layout.getLayoutParams();
                        height = lp.height;
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

                } else {

                    MyCustomAnimation a = new MyCustomAnimation(main_password_layout, 300, MyCustomAnimation.EXPAND);
                    if (!checkboolean1) {

                        final TextView old_password = (TextView) findViewById(R.id.edit_profile);
                        final TextView new_password = (TextView) findViewById(R.id.change_password);
                        final TextView confirm_password = (TextView) findViewById(R.id.deactivate_account);
                        ViewGroup.LayoutParams lp1 = old_password.getLayoutParams();
                        ViewGroup.LayoutParams lp2 = new_password.getLayoutParams();
                        ViewGroup.LayoutParams lp3 = confirm_password.getLayoutParams();
                        height = lp1.height + lp2.height + lp3.height;

                    }
                    // password_layout.setVisibility(View.GONE);
                    checkboolean1 = true;
                    a.setHeight(height);
                    main_password_layout.startAnimation(a);
                    /*((ScrollView) findViewById(R.id.scroll_view)).scrollTo(0, 500);*/
//                    //akshit code
//                    final ScrollView scrollView = (ScrollView)findViewById(R.id.scroll_view_deactivate);
//                    scrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            scrollView.fullScroll(ScrollView.);
//
//                        }
//                    });
//end

                }
                break;
            case R.id.change_password:
                if (password_layout.getVisibility() == View.VISIBLE) {
                    MyCustomAnimation a = new MyCustomAnimation(password_layout, 300, MyCustomAnimation.COLLAPSE);
                    height = a.getHeight();
                    password_layout.startAnimation(a);
                } else {
                    MyCustomAnimation a = new MyCustomAnimation(password_layout, 300, MyCustomAnimation.EXPAND);
                    if (!checkboolean2) {
                        final TextView old_password = (TextView) findViewById(R.id.old_password);
                        final TextView new_password = (TextView) findViewById(R.id.new_password);
                        final TextView confirm_password = (TextView) findViewById(R.id.confirm_password);
                        ViewGroup.LayoutParams lp1 = old_password.getLayoutParams();
                        ViewGroup.LayoutParams lp2 = new_password.getLayoutParams();
                        ViewGroup.LayoutParams lp3 = confirm_password.getLayoutParams();

                        height = lp1.height + lp2.height + lp3.height;
                    }
                    checkboolean2 = true;
                    a.setHeight(height);
                    password_layout.startAnimation(a);
                    /*((ScrollView) findViewById(R.id.scroll_view)).scrollTo(0, 600);*/
                }
                break;
            case R.id.report_text:
                if (report_layout.getVisibility() == View.VISIBLE) {
                    MyCustomAnimation a = new MyCustomAnimation(report_layout, 300, MyCustomAnimation.COLLAPSE);
                    height = a.getHeight();
                    report_layout.startAnimation(a);
                } else {
                    MyCustomAnimation a = new MyCustomAnimation(report_layout, 300, MyCustomAnimation.EXPAND);
                    if (!checkboolean3) {
                        height = 300;
                    }
                    checkboolean3 = true;
                    a.setHeight(height);
                    report_layout.startAnimation(a);
                    /*((ScrollView) findViewById(R.id.scroll_view)).scrollTo(0, 1200);*/
                }
                break;
            case R.id.not_working_text:
                ((EditText) findViewById(R.id.problem_text)).setCursorVisible(true);
                if (not_working_layout.getVisibility() == View.VISIBLE) {
                    MyCustomAnimation a = new MyCustomAnimation(not_working_layout, 300, MyCustomAnimation.COLLAPSE);
                    height = a.getHeight();
                    not_working_layout.startAnimation(a);
                } else {
                    MyCustomAnimation a = new MyCustomAnimation(not_working_layout, 300, MyCustomAnimation.EXPAND);
                    if (!checkboolean4) {
                        height = 300;
                    }
                    checkboolean4 = true;
                    a.setHeight(height);
                    not_working_layout.startAnimation(a);
                    /*((ScrollView) findViewById(R.id.scroll_view)).scrollTo(0, 1200);*/

                }
                break;
            case R.id.general_feed_back_text:

                if (general_feed_layout.getVisibility() == View.VISIBLE) {
                    MyCustomAnimation a = new MyCustomAnimation(general_feed_layout, 300, MyCustomAnimation.COLLAPSE);
                    height = a.getHeight();
                    general_feed_layout.startAnimation(a);
                } else {
                    MyCustomAnimation a = new MyCustomAnimation(general_feed_layout, 300, MyCustomAnimation.EXPAND);
                    if (!checkboolean5) {
                        height = 300;
                    }
                    checkboolean5 = true;
                    a.setHeight(height);
                    general_feed_layout.startAnimation(a);
                    /*((ScrollView) findViewById(R.id.scroll_view)).scrollTo(0, 1400);*/

                }
                break;
            case R.id.logout_text:
                if (logout_layout.getVisibility() == View.VISIBLE) {

                    MyCustomAnimation a = new MyCustomAnimation(logout_layout, 300, MyCustomAnimation.COLLAPSE);
                    height = a.getHeight();
                    logout_layout.startAnimation(a);
                } else {
                    MyCustomAnimation a = new MyCustomAnimation(logout_layout, 300, MyCustomAnimation.EXPAND);
                    if (!checkboolean6) {
                        height = 300;
                    }
                    checkboolean6 = true;
                    a.setHeight(height);
                    logout_layout.startAnimation(a);
                    ((ScrollView) findViewById(R.id.scroll_view)).scrollTo(0, 600);

                }
                break;
            case R.id.deactivate_account:
                Intent intent = new Intent(this, DeactivateAccountView.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//akshit code
                break;
            case R.id.privacy_policy_row:
                Intent intent1 = new Intent(this, PrivacyView.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//akshit code
                break;
            case R.id.term_use_row:
                Intent intent2 = new Intent(this, TermUseView.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//akshit code
                break;
            case R.id.spam_text:
                Intent intent3 = new Intent(this, ViewSpamorAbuse.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//akshit code
                break;
            case R.id.edit_profile:
                Intent intent4 = new Intent(this, EditMyProfileView.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//akshit code
                break;
            case R.id.btn_logout_yes:
                //   new MyPreference(getApplicationContext()).clearAllPreference();
                //To track through mixPanel.If user logout from app.
                Utils.trackMixpanel(this, "Activity", "Logout", "LeftMenuSettingsButtonClicked", false);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                ModelManager.getInstance().getSettingManager().changeLastSeenTime(
                        ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                        ModelManager.getInstance().getAuthorizationManager().getUsrToken(), "yes");//akshit code to logout

                //monika- stop service running in background
                Intent i = new Intent(this, MyQbChatService.class);
                stopService(i);

                ModelManager.setInstance();

                Intent intent5 = new Intent(SettingView.this, SplashView.class);
                intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent5);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.top_out);
                //  this.finishAndRemoveTask();
                //ModelManager.getInstance().getNotificationManagerManager().;

                try {
                    ModelManager.getInstance().getNotificationManagerManager().notificationData.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                    Utils.fromSignalDialog(this, AlertMessage.CHANGE_PASSWORD_NOT_MATCH);
                    //Utils.showAlert(this, AlertMessage.CHANGE_PASSWORD_NOT_MATCH);
                } else if (Utils.isEmptyString(old_password) || Utils.isEmptyString(new_password) || Utils.isEmptyString(confirm_password)) {
                    Utils.fromSignalDialog(this, AlertMessage.CHANGE_PASSWORD);
                } else if (old_password.length() < 8 || new_password.length() < 8 || confirm_password.length() < 8) {

                    Utils.fromSignalDialog(this, AlertMessage.CHANGE_PASSWORD_CHARACTER);
                }

                break;

// Not working problem

            case R.id.btn_send_not_working:
                String mphone_no, muser_token, spam_or_abuse_type, comment;
                mphone_no = authManager.getPhoneNo();
                muser_token = authManager.getUsrToken();
                problem_type = "notworking";
                EditText text = (EditText) findViewById(R.id.problem_text);

                if (text.length() >= 10) {
                    comment = ((EditText) findViewById(R.id.problem_text)).getText().toString();

                    spam_or_abuse_type = "";
                    if (Utils.isEmptyString(comment)) {
                        comment = "";
                    }
                    Utils.launchBarDialog(this);
                    //To track through mixPanel.If User Reports any problem.
                    Utils.trackMixpanel(SettingView.this, "Activity", "ReportProblemNotWorking", "LeftMenuSettingsButtonClicked", false);
                    settingManager.reportaproblem(mphone_no, muser_token, problem_type, spam_or_abuse_type, comment);
                } else {
                    Utils.fromSignalDialog(this, AlertMessage.itsNotworking);
                }


                break;

// general feedback
            case R.id.btn_send_general_problem:


                String mphone_no_general, muser_token_general, spam_or_abuse_type_general, comment_general;

                comment_general = ((EditText) findViewById(R.id.general_problem_text)).getText().toString();
                if (comment_general.length() > 10) {
                    mphone_no_general = authManager.getPhoneNo();
                    muser_token_general = authManager.getUsrToken();
                    problem_type = "feedback";
                    spam_or_abuse_type_general = "";

                    if (Utils.isEmptyString(comment_general)) {
                        comment_general = "";
                    }
                    Utils.launchBarDialog(this);
                    //To track through mixPanel.If user gives general feedback.
                    Utils.trackMixpanel(this, "Activity", "ReportProblemGeneralFeedback", "LeftMenuSettingsButtonClicked", false);
                    settingManager.reportaproblem(mphone_no_general, muser_token_general, problem_type, spam_or_abuse_type_general, comment_general);
                } else {
                    Utils.fromSignalDialog(this, AlertMessage.generalFeedback);
                }

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


// for facebook

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
            ((EditText) findViewById(R.id.old_password)).setText("");
            ((EditText) findViewById(R.id.new_password)).setText("");
            ((EditText) findViewById(R.id.confirm_password)).setText("");
            ((EditText) findViewById(R.id.old_password)).requestFocus();


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
//akshit code starts
            if (problem_type.equalsIgnoreCase("notworking")) {
//                    MyCustomAnimation animation = new MyCustomAnimation(not_working_layout, 300, MyCustomAnimation.COLLAPSE);
//                    height = animation.getHeight();
//                    not_working_layout.startAnimation(animation);
                ((EditText) findViewById(R.id.problem_text)).setText("");
                Utils.fromSignalDialog(this, "Problem Reported");

            } else if (problem_type.equalsIgnoreCase("feedback")) {
//                    MyCustomAnimation animation1 = new MyCustomAnimation(general_feed_layout, 300, MyCustomAnimation.COLLAPSE);
//                    height = animation1.getHeight();
//                    general_feed_layout.startAnimation(animation1);
                ((EditText) findViewById(R.id.general_problem_text)).setText("");
                Utils.fromSignalDialog(this, "Feedback is sent");
            }
            // message on success
            //Utils.showAlert(this,);
        }
        //Ends

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

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
        } else if (state.isClosed()) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (b) {
            settingManager.setAppSounds(true);
        } else {
            settingManager.setAppSounds(false);
        }
    }
}
