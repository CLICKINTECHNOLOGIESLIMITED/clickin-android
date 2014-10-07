package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.CurrentClickersAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

/**
 * Created by mukesh on 22/4/14.
 */
public class CurrentClickersView extends Activity implements OnClickListener {
    private static final String TAG = CurrentClickersView.class.getSimpleName();
	private Button phonebook, facebook;

    private TextView back,next,middleBack;
	private ImageView toboard;
	private View view;
    private ListView listView;
    private CurrentClickersAdapter adapter;
    private ProfileManager profilemanager;
    private AuthManager authManager;

    private NewsFeedManager newsFeedManager;

    private boolean showNextButton = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_currentclickers);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
		phonebook = (Button) findViewById(R.id.btn_phb);
		facebook = (Button) findViewById(R.id.btn_fb);
		listView = (ListView)findViewById(R.id.list_current_clickers);
		back = (TextView) findViewById(R.id.btn_back);
        middleBack = (TextView) findViewById(R.id.btn_middle_back);
        next = (TextView) findViewById(R.id.btn_next);
		toboard = (ImageView) findViewById(R.id.iv_toboard);
		phonebook.setOnClickListener(this);
		facebook.setOnClickListener(this);

		
		back.setOnClickListener(this);
        middleBack.setOnClickListener(this);
		next.setOnClickListener(this);


        showNextButton = getIntent().getExtras().getBoolean("FromMenu");
        if(showNextButton){
            middleBack.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }else{
            middleBack.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }


        authManager = ModelManager.getInstance().getAuthorizationManager();
        Utils.launchBarDialog(CurrentClickersView.this);
        new FetchContactFromPhone(CurrentClickersView.this).getClickerList(authManager.getPhoneNo(),authManager.getUsrToken(),1);

	}

	 public void setlist() {
         profilemanager = ModelManager.getInstance().getProfileManager();

	        adapter = new CurrentClickersAdapter(CurrentClickersView.this, R.layout.row_currentclickerslist, profilemanager.currentClickerList);

	        int index = listView.getFirstVisiblePosition();
	        View v = listView.getChildAt(0);
	        int top = (v == null) ? 0 : v.getTop();
	        listView.setAdapter(adapter);
	        listView.setSelectionFromTop(index, top);
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
	public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_phb) {
            phonebook.setBackgroundResource(R.drawable.c_phonebook_pink);
            facebook.setBackgroundResource(R.drawable.c_fb_grey);

        } else if (i == R.id.btn_fb) {
            phonebook.setBackgroundResource(R.drawable.c_phonebook_grey);
            facebook.setBackgroundResource(R.drawable.c_fb_pink);
            Utils.launchBarDialog(CurrentClickersView.this);
            if (Utils.isConnectingToInternet(CurrentClickersView.this)) {

                Session session = Session.getActiveSession();
                if (session == null) {
                    if (session == null) {
                        session = new Session(this);
                    }
                    Session.setActiveSession(session);
                }
                if (!session.isOpened() && !session.isClosed()) {
                    session.openForRead(new Session.OpenRequest(this).setCallback(callback).setPermissions("user_birthday", "basic_info", "email", "user_location"));
                } else {
                    Session.openActiveSession(this, true, callback);
                }

            } else {
                Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } else if (i == R.id.btn_back) {//	finish();

        } else if (i == R.id.btn_middle_back) {
            finish();

        } else if (i == R.id.btn_next) {
            new AlertDialog.Builder(this)
                    .setMessage(AlertMessage.CURRENTCLICKERPAGE)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }

                            }).setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    Intent clickersView = new Intent(CurrentClickersView.this, SpreadWordView.class);
                    startActivity(clickersView);

                }

            }).show();

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
            Log.d(TAG, "" + e);
        } catch (Error e) {
            Log.d(TAG, "" + e);
        }
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
            Log.d(TAG, access_Token);

            newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
            authManager = ModelManager.getInstance().getAuthorizationManager();
            newsFeedManager.fetchFbFriends(access_Token,authManager.getPhoneNo(),authManager.getUsrToken());

        } else if (state.isClosed()) {
            System.out.println("Logged out...");
        }
    }

    public void onEventMainThread(String message){
        Log.d(TAG, "onEventMainThread->" + message);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("CheckFriend True")) {
            Utils.dismissBarDialog();
            setlist();
        } else if (message.equalsIgnoreCase("CheckFriend False")) {
            Utils.dismissBarDialog();
        } else if(message.equalsIgnoreCase("CheckFriend Network Error")){
            Utils.dismissBarDialog();
            Utils.showAlert(CurrentClickersView.this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("FetchFbFriend True")) {
            Utils.dismissBarDialog();
            setlist();
        } else if (message.equalsIgnoreCase("FetchFbFriend False")) {
            Utils.dismissBarDialog();
        } else if(message.equalsIgnoreCase("FetchFbFriend Network Error")){
            Utils.dismissBarDialog();
            Utils.showAlert(CurrentClickersView.this, AlertMessage.connectionError);
        }

    }



}
