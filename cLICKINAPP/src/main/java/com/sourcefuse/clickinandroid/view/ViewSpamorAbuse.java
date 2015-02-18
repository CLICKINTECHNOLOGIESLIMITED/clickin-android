package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.SettingManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

/**
 * Created by prafull on 22/9/14.
 */
public class ViewSpamorAbuse extends Activity implements View.OnClickListener {

    RadioGroup mradioGroup;
    private AuthManager authManager;
    private SettingManager settingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        if(Utils.mStartExceptionTrack)
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));


        setContentView(R.layout.view_spam);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        findViewById(R.id.iv_back_noti).setOnClickListener(this);

        //akshit code starts::Removed Typeface
        ((RadioButton) findViewById(R.id.spam_radio_one)).setOnClickListener(this);//.setTypeface(typefacemedium)
        ((RadioButton) findViewById(R.id.spam_radio_two)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.spam_radio_three)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.spam_radio_four)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.spam_radio_five)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.spam_radio_six)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.spam_radio_seven)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.spam_radio_eight)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.spam_radio_nine)).setOnClickListener(this);
        ((TextView) findViewById(R.id.btn_report)).setOnClickListener(this);
        ((EditText) findViewById(R.id.spam_edit_txt)).setOnClickListener(this);
        //Ends

//        EditText edittext_spam = ((EditText) findViewById(R.id.spam_edit_txt));
//        TextView textview_header = ((TextView) findViewById(R.id.spam_header_text));
//        TextView textView_choose = ((TextView) findViewById(R.id.spam_chose_text));
//        TextView textview_reporttext = ((TextView) findViewById(R.id.report_text));
//        TextView textView_report=((TextView) findViewById(R.id.btn_report));
//        TextView textView_profile = ((TextView) findViewById(R.id.tv_profile_txt));
//        ((TextView) findViewById(R.id.btn_report)).setOnClickListener(this);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        settingManager = ModelManager.getInstance().getSettingManager();
        mradioGroup = (RadioGroup) findViewById(R.id.rbg_spam);


        // akshit code for closing keypad if touched anywhere outside

        ((RelativeLayout) findViewById(R.id.relative_layout_root_spam)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(((EditText) findViewById(R.id.spam_edit_txt)).getWindowToken() != null)
                    imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.spam_edit_txt)).getWindowToken(), 0);

            }

        });

//ends
        //akshit code
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view_spam);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        });
//end

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);//akshit code for animation
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //akshit code to hide keyboard
            case R.id.spam_radio_one:
            case R.id.spam_radio_two:
            case R.id.spam_radio_three:
            case R.id.spam_radio_four:
            case R.id.spam_radio_five:
            case R.id.spam_radio_six:
            case R.id.spam_radio_seven:
            case R.id.spam_radio_eight:
            case R.id.spam_radio_nine:

                InputMethodManager inputMethodManager1 = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
                    inputMethodManager1.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

                break;// akshit code ends

            case R.id.btn_report:

                Utils.launchBarDialog(this);

                String phone_no, user_token, problem_type, spam_or_abuse_type, comment;
                int radioButtonID = mradioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) mradioGroup.findViewById(radioButtonID);
                spam_or_abuse_type = radioButton.getText().toString();
                phone_no = authManager.getPhoneNo();
                user_token = authManager.getUsrToken();
                problem_type = "spamorabuse";
                comment = ((EditText) findViewById(R.id.spam_edit_txt)).getText().toString();
                if (Utils.isEmptyString(comment)) {
                    comment = "";
                }
                Utils.trackMixpanel(ViewSpamorAbuse.this,"Activity","ReportProblemSpamOrAbuse","LeftMenuSettingsButtonClicked",false);//track Spam Or Abuse mixpanel
                settingManager.reportaproblem(phone_no, user_token, problem_type, spam_or_abuse_type, comment);

                break;

            case R.id.spam_edit_txt:
                ((EditText) findViewById(R.id.spam_edit_txt)).setCursorVisible(true);
                break;
            case R.id.iv_back_noti:
                finish();
                overridePendingTransition(0, R.anim.top_out);//akshit code for animation
                break;
        }
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
        if (message.equalsIgnoreCase("ReportaProblem True")) {
            Utils.dismissBarDialog();
            fromSignalDialog(this, "Problem Reported");

            // message on success
            //Utils.showAlert(this,);
        }
        if (message.equalsIgnoreCase("ReportaProblem False")) {
            Utils.dismissBarDialog();
            finish();

// message on failure
            //Utils.showAlert(this,);
        }
        if (message.equalsIgnoreCase("ReportaProblem Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);

// message on onnetwork error
            //Utils.showAlert(this,);
        }

    }

    // Akshit Code Starts
    public void fromSignalDialog(final Activity activity, String str) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        final Button Cancel = (Button) dialog.findViewById(R.id.coolio);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                finish();
                overridePendingTransition(0, R.anim.top_out);//akshit code for animation

            }
        });
        dialog.show();
    }
    // Ends
}
