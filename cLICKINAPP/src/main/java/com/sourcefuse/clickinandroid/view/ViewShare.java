package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.facebook.Session.OpenRequest;

/**
 * Created by prafull on 25/9/14.
 */
public class ViewShare extends Activity implements View.OnClickListener{

    private static final String TAG = ViewShare.class.getSimpleName();
    public static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    TextView mshr_point,mshr_comment;
    EditText mshr_caption;
    private ChatManager chatManager;
    private AuthManager authManager;
    private String relationshipId,chatId,accepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_share_screen);

        findViewById(R.id.shr_facebook).setOnClickListener(this);
      //  findViewById(R.id.shr_twitter).setOnClickListener(this);
       // findViewById(R.id.shr_googleplus).setOnClickListener(this);
        findViewById(R.id.shr_btn_share).setOnClickListener(this);
        mshr_point=(TextView)findViewById(R.id.shr_point);
        mshr_comment=(TextView)findViewById(R.id.shr_comment);


        Intent intent = getIntent();
        if (null != intent) {

            chatId = intent.getStringExtra("chat_id");
            accepted = intent.getStringExtra("accepted");


        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.shr_facebook:
                if (Utils.isConnectingToInternet(ViewShare.this)) {

                    Session session = Session.getActiveSession();
                    if (session == null) {
                        if (session == null) {
                            session = new Session(this);
                        }
                        Session.setActiveSession(session);
                    }
                    if (!session.isOpened() && !session.isClosed()) {
                        session.openForRead(new OpenRequest(this).setCallback(callback).setPermissions("user_birthday", "basic_info", "email", "user_location","publish_stream"));
                    } else {
                        Session.openActiveSession(this, true, callback);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
           /* case R.id.shr_twitter:
                break;
            case R.id.shr_googleplus:
                break;*/
            case R.id.shr_btn_share:
                break;

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
            Log.e("access_Token","access_Token->"+access_Token);

/*
            OpenRequest op = new OpenRequest(ViewShare.this);

            op.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            op.setCallback(null);

            List<String> permissions = new ArrayList<String>();
            permissions.add("publish_stream");
            op.setPermissions(permissions);

            Session sessionl = new Session.Builder(ViewShare.this).build();
            Session.setActiveSession(session);
            session.openForPublish(op);
            */

            chatManager = ModelManager.getInstance().getChatManager();
            authManager = ModelManager.getInstance().getAuthorizationManager();
            //chatShare(String phone_no, String user_token, String relationshipId, String chatId, String media, String fbAccessToken, String twitterAccessToken, String twitterAccessTokenSecret, String googlePlusAccessToken, String comment) {
            chatManager.chatShare(authManager.getPhoneNo(),authManager.getUsrToken(),chatManager.getRelationshipId(),chatId,"facebook",access_Token,"","","",mshr_comment.getText().toString(),accepted);
        } else if (state.isClosed()) {
            System.out.println("Logged out...");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);
            try {
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
            } catch (Exception e) {
            }

    }
    private void publishStory() {

        Log.e("publishStory","publishStory");
        Session session = Session.getActiveSession();

        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {

                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }

            Bundle postParams = new Bundle();
            postParams.putString("name", "Facebook SDK for Android");
            postParams.putString("caption", "Build great social apps and get more installs.");
            postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
            postParams.putString("link", "https://developers.facebook.com/android");
            postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

            Request.Callback callback = new Request.Callback() {
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response
                            .getGraphObject()
                            .getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON error " + e.getMessage());
                    }
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(ViewShare.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewShare.this, postId, Toast.LENGTH_LONG).show();
                    }
                }
            };

            Request request = new Request(session, "me/feed", postParams,HttpMethod.POST, callback);

            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }
    }


    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }


}
