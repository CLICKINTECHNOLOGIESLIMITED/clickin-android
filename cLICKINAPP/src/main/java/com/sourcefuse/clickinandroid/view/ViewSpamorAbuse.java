package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.SettingManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

/**
 * Created by prafull on 22/9/14.
 */
public class ViewSpamorAbuse extends Activity implements View.OnClickListener {
    private Typeface typefacemedium;
    private Typeface typefaceBold;
    private AuthManager authManager;
    private SettingManager settingManager;
    RadioGroup mradioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_spam);
        this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);

        typefacemedium = Typeface.createFromAsset(ViewSpamorAbuse.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        typefaceBold = Typeface.createFromAsset(ViewSpamorAbuse.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);

        findViewById(R.id.iv_back_noti).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.spam_radio_one)).setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.spam_radio_two)).setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.spam_radio_three)).setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.spam_radio_four)).setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.spam_radio_five)).setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.spam_radio_six)).setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.spam_radio_seven)).setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.spam_radio_eight)).setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.spam_radio_nine)).setTypeface(typefacemedium);
        ((EditText) findViewById(R.id.spam_edit_txt)).setTypeface(typefacemedium);
        ((TextView) findViewById(R.id.spam_header_text)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.spam_chose_text)).setTypeface(typefacemedium);
        ((TextView) findViewById(R.id.report_text)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.btn_report)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.tv_profile_txt)).setTypeface(typefaceBold);
        ((TextView) findViewById(R.id.btn_report)).setOnClickListener(this);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        settingManager = ModelManager.getInstance().getSettingManager();
        mradioGroup = (RadioGroup) findViewById(R.id.rbg_spam);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_report:

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
                settingManager.reportaproblem(phone_no, user_token, problem_type, spam_or_abuse_type, comment);

                break;
            case R.id.iv_back_noti:
                finish();
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

}
