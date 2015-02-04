package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class VerifyView extends Activity implements OnClickListener,
        TextWatcher {
    private static final String TAG = VerifyView.class.getSimpleName();
    private Button send;
    private EditText d_one, d_two, d_three, d_four;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_verify);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        send = (Button) findViewById(R.id.btn_send);

        d_one = (EditText) findViewById(R.id.edt_one);
        d_two = (EditText) findViewById(R.id.edt_two);
        d_three = (EditText) findViewById(R.id.edt_three);
        d_four = (EditText) findViewById(R.id.edt_four);


        d_one.addTextChangedListener(this);
        d_two.addTextChangedListener(this);
        d_three.addTextChangedListener(this);
        d_four.addTextChangedListener(this);
        send.setOnClickListener(this);

        Utils.prefrences = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);

        Utils.fromSignalDialog1(this, AlertMessage.SENDVERIFYMSGI, AlertMessage.SENDVERIFYMSGII);



        d_one.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode == KeyEvent.KEYCODE_DEL && action == KeyEvent.ACTION_DOWN ) {
                    Log.e("D-One","Edit-Text1");
                    d_one.requestFocus();
               return true;
                }
                return false;
            }
        });
        d_two.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                int action = event.getAction();
                if (keyCode == KeyEvent.KEYCODE_DEL && action == KeyEvent.ACTION_DOWN) {
                    Log.e("D-Two","Edit-Text2");
                    d_one.requestFocus();
                    d_one.setText("");
                    return true;
                }
                return false;
            }
        });
        d_three.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode == KeyEvent.KEYCODE_DEL && action == KeyEvent.ACTION_DOWN) {
                    Log.e("D-Three","Edit-Text3");
                    d_two.requestFocus();
                    d_two.setText("");
                    return true;
                }
                return false;
            }
        });
        d_four.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode == KeyEvent.KEYCODE_DEL && action == KeyEvent.ACTION_DOWN) {
                    Log.e("D-Four","Edit-Text4");
                    d_three.requestFocus();
                    d_three.setText("");

                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (d_one.getText().toString().length() > 0 && d_two.getText().toString().length() == 0 && d_three.getText().toString().length() == 0 && d_four.getText().toString().length() == 0) {
            d_one.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_two.requestFocus();
        } else {
            //d_one.setBackgroundColor(getResources().getColor(R.color.empty_edt));
            d_one.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field_empty));
        }
        if (d_one.getText().toString().length() > 0 && d_two.getText().toString().length() > 0 && d_three.getText().toString().length() == 0 && d_four.getText().toString().length() == 0) {
            d_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_one.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_three.requestFocus();
        } else {
            d_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field_empty));
            //d_one.requestFocus();
        }

        if (d_one.getText().toString().length() > 0 && d_two.getText().toString().length() > 0 && d_three.getText().toString().length() > 0 && d_four.getText().toString().length() == 0) {
            d_one.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_four.requestFocus();
        } else {
            d_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field_empty));
            // d_two.requestFocus();
        }
        String digits = d_one.getText().toString() + d_two.getText().toString()
                + d_three.getText().toString()
                + d_four.getText().toString();
        if (d_four.getText().toString().length() > 0 && digits.length() > 3) {
            d_one.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));
            d_four.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field));

            Utils.launchBarDialog(this);
            authManager.getVerifyCode(authManager.getPhoneNo(), Utils.deviceId, digits, Constants.DEVICETYPE);

        } else {
            d_four.setBackgroundDrawable(getResources().getDrawable(R.drawable.e_v_number_field_empty));
            //d_three.requestFocus();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                Utils.launchBarDialog(VerifyView.this);
                Utils.fromSignalertDialogDammit(VerifyView.this);
                authManager = ModelManager.getInstance().getAuthorizationManager();
                authManager.reSendVerifyCode(authManager.getPhoneNo(), authManager.getUsrToken());
                break;
        }
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

    public void onEventMainThread(String getMsg) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("Verify True")) {
            Utils.dismissBarDialog();
            Intent intent = new Intent(VerifyView.this, ProfileView.class);
            intent.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
            startActivity(intent);
            finish();
        } else if (getMsg.equalsIgnoreCase("Verify False")) {
            Utils.dismissBarDialog();
            alertDialog(AlertMessage.WRONGVERIFYCODEI, AlertMessage.WRONGVERIFYCODEII);
        } else if (getMsg.equalsIgnoreCase("Verify Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(this, AlertMessage.connectionError);
        } else if (getMsg.equalsIgnoreCase("ReSendVerifyCode True")) {
            Utils.dismissBarDialog();
        } else if (getMsg.equalsIgnoreCase("ReSendVerifyCode False")) {

            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
            //Utils.showAlert(VerifyView.this, authManager.getMessage());
        } else if (getMsg.equalsIgnoreCase("ReSendVerifyCode Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //Utils.showAlert(this, AlertMessage.connectionError);
        }
    }

    public void alertDialog(String msgStrI, String msgStrII) {
        final Dialog dialog = new Dialog(VerifyView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        dialog.setContentView(R.layout.alert_nocheck);
        dialog.setCancelable(false);

        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
        msgI.setText(msgStrI);
        msgII.setText(msgStrII);

        dialog.setCancelable(false);
        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setBackgroundResource(R.drawable.try_again);
        dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                //switchView();
                send.setBackgroundResource(R.drawable.damnit_resend_code);
                send.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.fromSignalertDialogDammit(VerifyView.this);
                    }
                });


                d_one.setText("");
                d_two.setText("");
                d_three.setText("");
                d_four.setText("");

            }
        });
        dialog.show();
    }


}
