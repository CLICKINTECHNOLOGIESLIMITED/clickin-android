package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.quickblox.module.chat.xmpp.QBPrivateChat;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.SpreadWordAdapter;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by mukesh on 22/4/14.
 */
public class SpreadWordView extends Activity implements OnClickListener {

	private Button phonebook, facebook;

	//private ImageView toboard;
    private QBPrivateChat chat;
     Dialog dialog ;


    private AuthManager authManager;
    public static  ArrayList<String> selectedPhoneArray = new ArrayList<String>();
    private ProfileManager profilemanager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_spread_word);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
		phonebook = (Button) findViewById(R.id.btn_phb);
		facebook = (Button) findViewById(R.id.btn_fb);

        ((TextView) findViewById(R.id.btn_invite)).setOnClickListener(this);

		((TextView) findViewById(R.id.btn_back)).setOnClickListener(this);
        ((TextView) findViewById(R.id.btn_next)).setOnClickListener(this);

		phonebook.setOnClickListener(this);
		facebook.setOnClickListener(this);




        authManager = ModelManager.getInstance().getAuthorizationManager();
        EventBus.getDefault().register(this);
        Utils.launchBarDialog(this);
        new FetchContactFromPhone(SpreadWordView.this).getClickerList(authManager.getPhoneNo(),authManager.getUsrToken(),1);
     //   setlist();
		
	}

	public void setlist() {
        profilemanager = ModelManager.getInstance().getProfileManager();
        ListView listView = (ListView) findViewById(R.id.list_current_clickers);
        SpreadWordAdapter adapter = new SpreadWordAdapter(SpreadWordView.this,R.layout.row_invitefriend, profilemanager.spreadTheWorldList);
		listView.setAdapter(adapter);

	}

      @Override
      public void onBackPressed() {
            super.onBackPressed();
            finish();
            overridePendingTransition(0, R.anim.top_out);
      }

      @Override
    protected void onResume() {
        super.onResume();
      //  Utils.groupSms.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!(EventBus.getDefault().isRegistered(this)))
        EventBus.getDefault().register(this);
    }

    public void onStop(){
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_phb:
            phonebook.setBackgroundResource(R.drawable.c_phonebook_pink);
            facebook.setBackgroundResource(R.drawable.c_fb_grey);
		break;
		case R.id.btn_fb:
            phonebook.setBackgroundResource(R.drawable.c_phonebook_grey);
            facebook.setBackgroundResource(R.drawable.c_fb_pink);
            Utils.launchBarDialog(SpreadWordView.this);
            if (Utils.isConnectingToInternet(SpreadWordView.this)) {
                Session session = Session.getActiveSession();
                if (session == null) {
                    if (session == null) {
                        session = new Session(this);
                    }
                    Session.setActiveSession(session);
                }
                if (!session.isOpened() && !session.isClosed()) {
                    session.openForRead(new Session.OpenRequest(this).setCallback(callback).setPermissions("user_birthday","basic_info","email","user_location"));
                } else {
                    Session.openActiveSession(this, true, callback);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
		break;
		case R.id.btn_back:
			//Log.e("", "COUNT------>" + listView.getCount());
                  finish();
                  overridePendingTransition(0, R.anim.top_out);
		break;
		case R.id.btn_next:
            Utils.launchBarDialog(this);

         //   authManager.getProfileInfo("", authManager.getPhoneNo(), authManager.getUsrToken());
            Intent clickersView = new Intent(SpreadWordView.this,UserProfileView.class);
            clickersView.putExtra("FromSignup", true);
            startActivity(clickersView);
            finish();
	    break;
        case R.id.btn_invite:
            if(Utils.groupSms.size()>0) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("sms_body", Constants.SEND_REQUEST_WITH_SMS_MESSAGE_SPREAD);

                StringBuilder uri = new StringBuilder("sms:");
                for (int i = 0; i < Utils.groupSms.size(); i++) {
                    uri.append(Utils.groupSms.get(i));
                    uri.append(", ");
                }
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse(uri.toString()));
                startActivity(smsIntent);

            /*Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.putExtra("sms_body", "Hello ClickIn");
            smsIntent.putExtra("address", "4");
            smsIntent.setType("vnd.android-dir/mms-sms");
            startActivity(smsIntent);*/

           /* Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", "default content");
            sendIntent.setType("vnd.android-dir/mms-sms");
            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(sendIntent);
            }*/


            }else{
                fromSignalDialog(AlertMessage.GROUPSMSMSG);
                //Utils.showAlert(SpreadWordView.this, AlertMessage.GROUPSMSMSG);
            }
        break;
		}
	}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            try{
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
            }catch(Exception e){

            }
        } catch (Exception e) {
         //   android.util.Log.d(TAG, "" + e);
        } catch (Error e) {
           // android.util.Log.d(TAG, "" + e);
        }
        Utils.dismissBarDialog();
    }
    //Methods for Facebook
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {
        if (state.isOpened()) {
            final String access_Token = session.getAccessToken();
       //     android.util.Log.d(TAG, access_Token);
            sendRequestDialogForFriendList();
        } else if (state.isClosed()) {
            Utils.dismissBarDialog();
            System.out.println("Logged out...");
        }
    }


    private void sendRequestDialogForFriendList() {
        try {
            authManager = ModelManager.getInstance().getAuthorizationManager();
            Bundle params = new Bundle();
            params.putString("message", authManager.getUserName() + AlertMessage.SREADTHEWORDMSGON);
            WebDialog requestsDialog = (
                    new WebDialog.RequestsDialogBuilder(SpreadWordView.this,
                            Session.getActiveSession(),
                            params))
                    .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                        @Override
                        public void onComplete(Bundle values,
                                               FacebookException error) {
                            try {
                                final String requestId = values.getString("request");
                                if (requestId != null) {
                                  //  Log.e(TAG, "Request sent");
                                    Utils.dismissBarDialog();
                                } else {
                                //    Log.e(TAG, "Request cancelled");
                                    Utils.dismissBarDialog();
                                }
                            }catch (Exception e){

                            }

                            phonebook.setBackgroundResource(R.drawable.c_phonebook_pink);
                            facebook.setBackgroundResource(R.drawable.c_fb_grey);
                            Utils.dismissBarDialog();
                            }

                        }

                        )
                                .

                        build();

                        requestsDialog.show();
                    }catch (Exception e){}
    }






    public void onEventMainThread(String message){
       // android.util.Log.d(TAG, "onEventMainThread->" + message);
        if (message.equalsIgnoreCase("CheckFriend True")) {
            Utils.dismissBarDialog();
            setlist();
        } else if (message.equalsIgnoreCase("CheckFriend False")) {
            Utils.dismissBarDialog();

        } else if(message.equalsIgnoreCase("CheckFriend Network Error")){
            Utils.dismissBarDialog();
            fromSignalDialog(AlertMessage.connectionError);
          //  Utils.showAlert(SpreadWordView.this, AlertMessage.connectionError);
        }
    }

    // Akshit Code Starts
    public void fromSignalDialog(String str){

        dialog = new Dialog(SpreadWordView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
// Ends
}
