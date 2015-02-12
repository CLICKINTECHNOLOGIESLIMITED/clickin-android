package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.SettingManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.MyPreference;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.SplashView;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

/**
 * Created by prafull on 17/9/14.
 */
public class DeactivateAccountView extends Activity implements View.OnClickListener {
    RadioGroup mradioGroup;
    private ImageView backarrow;
    private AuthManager authManager;
    private SettingManager settingManager;
    private String email_opt_out = "yes";
    private boolean email_opt_out_b = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//akshit code to prevent auto pop-up for keyboard
        setContentView(R.layout.view_deactivate_account);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


        backarrow = (ImageView) findViewById(R.id.iv_back_noti);
        backarrow.setOnClickListener(this);
//        ((TextView) findViewById(R.id.deactivate_msg)).setTypeface(typefacemedium);
//        ((TextView) findViewById(R.id.option_text)).setTypeface(typefacemedium);

        mradioGroup = (RadioGroup) findViewById(R.id.rgOpinion);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        settingManager = ModelManager.getInstance().getSettingManager();

        ((LinearLayout) findViewById(R.id.mail_radio_button_layout)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.deactivate_radio_msg_one)).setOnClickListener(this);//setTypeface(typefacemedium);
        ((RadioButton) findViewById(R.id.deactivate_radio_msg_two)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.deactivate_radio_msg_three)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.deactivate_radio_msg_four)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.deactivate_radio_msg_five)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.deactivate_radio_msg_six)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.deactivate_radio_msg_seven)).setOnClickListener(this);
        // ((RadioButton) findViewById(R.id.mail_radio_button)).setTypeface(typefacemedium);
        ((EditText) findViewById(R.id.general_problem_text)).setOnClickListener(this);

        EditText passw = ((EditText) findViewById(R.id.old_password));

//        TextView text_profile=((TextView) findViewById(R.id.tv_profile_txt));
//        TextView text_good=((TextView) findViewById(R.id.btn_good_bye));
//        TextView text_profile=((TextView) findViewById(R.id.btn_stay));
//        TextView text_profile=((TextView) findViewById(R.id.deactivate_text));
//        TextView text_profile=((TextView) findViewById(R.id.leaving_text));
//        TextView text_profile= ((TextView) findViewById(R.id.email_opt_out_text));
//        TextView text_profile=((TextView) findViewById(R.id.password_text));


        ((TextView) findViewById(R.id.btn_good_bye)).setOnClickListener(this);
        ((TextView) findViewById(R.id.btn_stay)).setOnClickListener(this);


        // akshit code for closing keypad if touched anywhere outside
        ((RelativeLayout) findViewById(R.id.relative_layout_root_deactivate_account)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                if(((EditText) findViewById(R.id.general_problem_text)).getWindowToken() != null)
                    imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.general_problem_text)).getWindowToken(), 0);
                if(((EditText) findViewById(R.id.old_password)).getWindowToken() != null)
                    imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.old_password)).getWindowToken(), 0);

            }

        });

//ends


        //akshit code
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view_deactivate);
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

            //akshit code for hiding keyboard
            case R.id.deactivate_radio_msg_one:
            case R.id.deactivate_radio_msg_two:
            case R.id.deactivate_radio_msg_three:
            case R.id.deactivate_radio_msg_four:
            case R.id.deactivate_radio_msg_five:
            case R.id.deactivate_radio_msg_six:
            case R.id.deactivate_radio_msg_seven:
                InputMethodManager inputMethodManager1 = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                if(this.getCurrentFocus().getWindowToken() != null)
                    inputMethodManager1.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

                break;//ends
            case R.id.iv_back_noti:
                finish();
                break;
            case R.id.btn_good_bye:


                String phone_no, user_token, password, reason_type, other_reason;

                int radioButtonID = mradioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) mradioGroup.findViewById(radioButtonID);

                reason_type = radioButton.getText().toString();
                phone_no = authManager.getPhoneNo();
                user_token = authManager.getUsrToken();
                password = ((EditText) findViewById(R.id.old_password)).getText().toString();
                other_reason = ((EditText) findViewById(R.id.general_problem_text)).getText().toString();

                if (other_reason == null) {
                    other_reason = "";
                }
                /*if (((RadioButton) findViewById(R.id.mail_radio_button)).isChecked()) {
                    email_opt_out = "true";
                } else {
                    email_opt_out = "false";
                }*/
                if (!password.isEmpty() && password.length() >= 8) {
                    Utils.launchBarDialog(DeactivateAccountView.this);
                    settingManager.deactivteAccount(phone_no, user_token, reason_type, other_reason, email_opt_out, password);
                } else if (password.isEmpty()) {
                    Utils.fromSignalDialog(DeactivateAccountView.this, AlertMessage.DEACTIVATE_ENTER_PASSWORD);
                } else if (password.length() < 8) {
                    Utils.fromSignalDialog(DeactivateAccountView.this, AlertMessage.DEACTIVATE_PASSWORD);
                }


                break;
            case R.id.btn_stay:
                finish();
                break;
            case R.id.general_problem_text:
                ((EditText) findViewById(R.id.general_problem_text)).setCursorVisible(true);
                break;
            case R.id.mail_radio_button_layout:
                InputMethodManager inputMethodManager2 = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                if(this.getCurrentFocus().getWindowToken() != null)
                    inputMethodManager2.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);


                if (email_opt_out_b) {
                    ((ImageView) findViewById(R.id.mail_radio_button)).setImageDrawable(getResources().getDrawable(R.drawable.inactive_circle));
                    email_opt_out = "no";
                    email_opt_out_b = false;
                } else {
                    ((ImageView) findViewById(R.id.mail_radio_button)).setImageDrawable(getResources().getDrawable(R.drawable.active_circle));
                    email_opt_out = "yes";
                    email_opt_out_b = true;
                }
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
        if (message.equalsIgnoreCase("DeactivteAccount True")) {
            Utils.dismissBarDialog();
            fromSignalDialog(this, AlertMessage.DEACTIVATE_ON_SUCCESS);

        }
        if (message.equalsIgnoreCase("DeactivteAccount False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.DEACTIVATE_ON_FALIURE);

        }
        if (message.equalsIgnoreCase("DeactivteAccount Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        }
    }


//    public void showAlert(Activity activity, String masg) {
//        new AlertDialog.Builder(activity).setTitle("Alert").setMessage(masg)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Intent intent=new Intent(DeactivateAccountView.this, SplashView.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
//                        new MyPreference(getApplicationContext()).clearAllPreference();
//                        DeactivateAccountView.this.finish();
//
//                    }
//                }).show();
//
//    }


    // Akshit Code Starts
    public void fromSignalDialog(Activity activity, String str) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(DeactivateAccountView.this, SplashView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                new MyPreference(getApplicationContext()).clearAllPreference();
                DeactivateAccountView.this.finish();

            }
        });
        dialog.show();
    }
    // Ends

}
