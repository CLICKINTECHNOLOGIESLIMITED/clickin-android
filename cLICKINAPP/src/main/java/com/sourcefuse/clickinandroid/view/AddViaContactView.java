package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public class AddViaContactView extends Activity implements View.OnClickListener, TextWatcher {
    private String TAG = PlayItSafeView.class.getSimpleName();
    private Button getClickIn;
    private EditText phoneNo, cntry_cd;
    private ImageView conIcon;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        //code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));


        setContentView(R.layout.view_addviacontactlist);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        cntry_cd = (EditText) findViewById(R.id.edt_cntry_cd);
        phoneNo = (EditText) findViewById(R.id.edt_phone_no);
        phoneNo.addTextChangedListener(this);
        getClickIn = (Button) findViewById(R.id.btn_get_clickIn);
        conIcon = (ImageView) findViewById(R.id.iv_contact_icon);
        getClickIn.setOnClickListener(this);
        String countryCode = null;
        String mlPhNo;
        try {
            Bundle bundle = getIntent().getExtras();
            ((TextView) findViewById(R.id.tv_contact_name)).setText("" + bundle.getString("ConName"));
            mlPhNo = bundle.getString("ConNumber");


            //first check country code from SIM and compare it with num
            countryCode = Utils.getCountryCodeFromSim(this);
            if (countryCode != null) {
                if (mlPhNo.startsWith(countryCode)) {
                    mlPhNo = mlPhNo.replace(countryCode, "");

                } else {
                    mlPhNo = mlPhNo;

                }
            }

            /* to check country codetill present in no */

            if (mlPhNo.startsWith("+")) {
                String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
                for (int i = 0; i < rl.length; i++) {
                    String[] g = rl[i].split(",", 2);
                    String tempcountryCode = g[0];
                    tempcountryCode = "+" + tempcountryCode;
                    if (mlPhNo.startsWith(tempcountryCode)) {
                        mlPhNo = mlPhNo.replace(tempcountryCode, "");
                        countryCode = tempcountryCode;

                        break;
                    }
                }
            }


            if (countryCode == null || mlPhNo.startsWith("+")) {
                countryCode = "+(null)";
                if (mlPhNo.contains("+"))
                    mlPhNo = mlPhNo.replace("+", "");
                else
                    mlPhNo = mlPhNo;
            }


            cntry_cd.setText("" + countryCode);
            phoneNo.setText("" + mlPhNo);
            String image_uri = bundle.getString("ConUri");
            try {
                if (!Utils.isEmptyString(image_uri)) {
                    Picasso.with(AddViaContactView.this)
                            .load(image_uri)
                            .error(R.drawable.male_user)
                            .into(conIcon);


                } else {
                    conIcon.setImageResource(R.drawable.default_profile);
                }
            } catch (Exception e) {
                conIcon.setImageResource(R.drawable.default_profile);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        authManager = ModelManager.getInstance().getAuthorizationManager();

        ((RelativeLayout) findViewById(R.id.rl_addvia_contact_action)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cntry_cd.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(phoneNo.getWindowToken(), 0);

            }

        });

        //akshit code to hide visibility for back button .
        if (!getIntent().getBooleanExtra("fromsignup", false)) {
            findViewById(R.id.rl_back).setVisibility(View.GONE);
        } else {
            findViewById(R.id.rl_back).setVisibility(View.VISIBLE);
        }

        //End

        ((TextView) findViewById(R.id.btn_go_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.top_out);
            }
        });
        ProfileManager prfManager = ModelManager.getInstance().getProfileManager();


        //both the list can't be empty at a time, so fetch contact webservice again
        if (prfManager.currentClickerList.size() == 0 && prfManager.spreadTheWorldList.size() == 0) {
            Utils.launchBarDialog(this);
            new LoadContacts().execute();
            //  new FetchContactFromPhone(this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
        }


    }


    @Override

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_get_clickIn:
                String mPhNo;
                String countryCode = cntry_cd.getText().toString().trim();


                if (phoneNo.getText().toString().length() >= 5) {
                    if (!(countryCode.contains("null"))) {
                        mPhNo = cntry_cd.getText().toString().trim() + phoneNo.getText().toString().trim();
                    } else {
                        mPhNo = phoneNo.getText().toString().trim();
                    }

                    ProfileManager prfManager = ModelManager.getInstance().getProfileManager();

                    if (prfManager.currClickersPhoneNums.contains(mPhNo)) {
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        Utils.launchBarDialog(this);
                        authManager.sendNewRequest(authManager.getPhoneNo(), mPhNo, authManager.getUsrToken());
                    } else {

                /* send sms if not not register */
                 /*  send sms for nexus 5 check build version*/
                 /* prafull code */
                        try {


                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE_SPREAD);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.setData(Uri.parse(mPhNo));
                            smsIntent.putExtra("exit_on_sent", true);
                            startActivityForResult(smsIntent, Constants.SMS_SEND);


                        } catch (Exception e) {
                            try {
                                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(AddViaContactView.this); //Need to change the build to API 19

                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                sendIntent.setType("text/plain");
                                sendIntent.putExtra("address", mPhNo);
                                sendIntent.putExtra(Intent.ACTION_ATTACH_DATA, Uri.parse(mPhNo));
                                sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.SEND_REQUEST_WITH_SMS_MESSAGE);
                                if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                                {
                                    sendIntent.setPackage(defaultSmsPackageName);
                                }
                                sendIntent.putExtra("exit_on_sent", true);
                                startActivityForResult(sendIntent, Constants.SMS_SEND);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                    }

                } else {
                    Utils.fromSignalDialog(this, AlertMessage.phone);

                }
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onStart() {
        super.onStart();

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
        authManager = ModelManager.getInstance().getAuthorizationManager();

        if (message.equalsIgnoreCase("RequestSend True")) {
            Utils.dismissBarDialog();
            if (getIntent().getBooleanExtra("fromsignup", false)) {
                Intent intent = new Intent(this, CurrentClickersView.class);
                intent.putExtra("FromMenu", false);
                intent.putExtra("FromSignup", true);
                intent.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
                startActivity(intent);
                finish();
            } else {
                Utils.dismissBarDialog();
                Intent intent = new Intent(this, UserProfileView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("isChangeInList", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        } else if (message.equalsIgnoreCase("RequestSend False")) {
            Utils.dismissBarDialog();

            Utils.fromSignalDialog(this, authManager.getMessage());
        } else if (message.equalsIgnoreCase("RequestSend Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            // Utils.showAlert(AddViaContactView.this, AlertMessage.connectionError);

        }
        if (message.equalsIgnoreCase("CheckFriend True")) {
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("CheckFriend False")) {
            Utils.dismissBarDialog();
            //  Utils.showAlert(this,authManager.getMessage());
            //   Utils.fromSignalDialog(this, authManager.getMessage());

        } else if (message.equalsIgnoreCase("CheckFriend Network Error")) {
            Utils.dismissBarDialog();
            //    Utils.showAlert(this, AlertMessage.connectionError);
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        }
    }


    @Override
    public void onDestroy() {


        super.onDestroy();
    }


    // akshit code start for Active and inActive Button
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        if (phoneNo.getText().toString().length() > 0) {

            getClickIn.setBackgroundResource(R.drawable.c_getclicin_active);
            getClickIn.setEnabled(true);
        } else {
            getClickIn.setEnabled(false);
            getClickIn.setBackgroundResource(R.drawable.c_getclicin_deactive);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private class LoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new FetchContactFromPhone(AddViaContactView.this).readContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            new FetchContactFromPhone(AddViaContactView.this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
        }
    }
// Ends
}
