package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

import static com.facebook.Session.OpenRequest;

/**
 * Created by prafull on 25/9/14.
 */
public class ViewShare extends Activity implements View.OnClickListener {

    public static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String TAG = ViewShare.class.getSimpleName();
    TextView mshr_point, mshr_comment;
    String fileId, clicks, textMsg, chatType, originalChatId, isMessageSender;
    EditText shr_caption;
    private ChatManager chatManager;
    private AuthManager authManager;
    private String access_Token = "-";
    String image_url ;//akshit to make common variable for image ,vedio Url .
    //Methods for Facebook
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private String imageRatio = null;
    private String videoThumbnail = null;
    private String videoID = null;
    private String audioID = null;
    private String cardOwner = null;
    private String shareMedia ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_share_screen);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        findViewById(R.id.shr_facebook).setOnClickListener(this);
        findViewById(R.id.shr_btn_share).setOnClickListener(this);
        findViewById(R.id.linear_layout_share).setOnClickListener(this);
        mshr_point = (TextView) findViewById(R.id.shr_point);
        mshr_comment = (TextView) findViewById(R.id.shr_comment);
        shr_caption = (EditText) findViewById(R.id.shr_caption);
        shr_caption.setOnClickListener(this);

        // akshit code for closing keypad if touched anywhere outside
        ((LinearLayout) findViewById(R.id.linear_layout_root_share_screen)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(shr_caption.getWindowToken(), 0);

            }

        });

//ends

        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra("imageRatio")) {
                clicks = intent.getStringExtra("clicks");
                imageRatio = intent.getStringExtra("imageRatio");
                fileId = intent.getStringExtra("fileId");
                image_url = fileId;
            } else if (intent.hasExtra("videoThumbnail")) {
                clicks = intent.getStringExtra("clicks");
                videoThumbnail = intent.getStringExtra("videoThumbnail");
                videoID = intent.getStringExtra("videoID");
                image_url= videoThumbnail;
            }else if (intent.hasExtra("card_owner")) {
                clicks = "no";
                videoThumbnail = intent.getStringExtra("card_url");
                cardOwner = intent.getStringExtra("card_owner");
                image_url= videoThumbnail;
            } else if (intent.hasExtra("audioID")) {
                clicks = intent.getStringExtra("clicks");
                audioID = intent.getStringExtra("audioID");
            }else{
                clicks = intent.getStringExtra("clicks");
            }


            textMsg = intent.getStringExtra("textMsg");
            chatType = intent.getStringExtra("chatType");
            isMessageSender = intent.getStringExtra("isMessageSender");
            originalChatId = intent.getStringExtra("originalChatId");


            //akshit Code Starts ,To Upload Image ,Vedio ,Audio.
            if (!Utils.isEmptyString(image_url)) {
                ImageView shr_image = (ImageView) findViewById(R.id.shr_user_image);
                shr_image.setVisibility(View.VISIBLE);
                Picasso.with(ViewShare.this).load(image_url)
                        .resize(200,200).centerCrop()
                        .into(shr_image);
                shr_caption.setHint("Write your caption \nhere...");
            }
            else if (!Utils.isEmptyString(audioID))
            {
                ImageView shr_image = (ImageView) findViewById(R.id.shr_user_image);
                shr_image.setVisibility(View.VISIBLE);
                shr_image.setBackgroundResource(R.drawable.soundicon_);
                shr_caption.setHint("Write your caption \nhere...");
            }

            if(!clicks.equalsIgnoreCase("no")) {
                ((LinearLayout) findViewById(R.id.share_clicks_area)).setVisibility(View.VISIBLE);
                TextView clicktxt = (TextView) findViewById(R.id.share_clicks_text);
                clicktxt.setVisibility(View.VISIBLE);
                clicktxt.setText(clicks);
                clicktxt.setTextColor(getResources().getColor(R.color.white));
                ((ImageView) findViewById(R.id.share_clicks_heart)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.share_clicks_area)).setBackgroundColor(getResources().getColor(R.color.profile_name_b));


                if (!Utils.isEmptyString(textMsg)) {
                    TextView textMsgchat = (TextView) findViewById(R.id.share_chat_text);
                    textMsgchat.setVisibility(View.VISIBLE);
                    textMsgchat.setText(textMsg);
                }


            }else{

                if (!Utils.isEmptyString(textMsg)) {
                    ((LinearLayout) findViewById(R.id.share_clicks_area)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findViewById(R.id.share_clicks_area)).setBackgroundColor(getResources().getColor(R.color.white));
                    TextView textMsgchat = (TextView) findViewById(R.id.share_chat_text);
                    textMsgchat.setVisibility(View.VISIBLE);
                    textMsgchat.setTextColor(Color.BLACK);
                    textMsgchat.setText(textMsg);
                }


            }
//Akshit Code ends

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
        if (getMsg.equalsIgnoreCase("ChatShare True")) {

            finish();
        } else if (getMsg.equalsIgnoreCase("ChatShare False")) {
            Utils.fromSignalDialog(this, AlertMessage.usrAllreadyExists);
        } else if (getMsg.equalsIgnoreCase("ChatShare Network Error")) {
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.shr_facebook:
                Toast.makeText(this,"Will be implemented later on", Toast.LENGTH_SHORT).show();
                if (Utils.isConnectingToInternet(ViewShare.this)) {

                    Session session = Session.getActiveSession();
                    if (session == null) {
                        if (session == null) {
                            session = new Session(this);
                        }
                        Session.setActiveSession(session);
                    }
                    if (!session.isOpened() && !session.isClosed()) {
                        session.openForRead(new OpenRequest(this).setCallback(callback).setPermissions("user_birthday", "basic_info", "email", "user_location"));
                        //session.openForPublish(new OpenRequest(this).setCallback(callback).setPermissions("publish_stream"));
                    } else {
                        Session.openActiveSession(this, true, callback);
                    }
                } else {
                    Utils.fromSignalDialog(this, AlertMessage.connectionError);
                }
                break;
            case R.id.shr_btn_share:
                Intent i = new Intent(this, ChatRecordView.class);

                String commentStr = shr_caption.getText().toString().trim();
                if (Utils.isEmptyString(commentStr))
                    commentStr = "Write your caption here...";

                if (access_Token.length() < 3)
                    access_Token = "-";

                if (Utils.isEmptyString(textMsg))
                    textMsg = "";

                if (!Utils.isEmptyString(imageRatio)) {
                    i.putExtra("imageRatio", imageRatio);
                    i.putExtra("fileId", fileId);
                } else if (!Utils.isEmptyString(videoThumbnail)) {
                    i.putExtra("videoThumbnail", videoThumbnail);
                    i.putExtra("videoID", videoID);
                } else if (!Utils.isEmptyString(audioID)) {
                    i.putExtra("audioID", audioID);
                }else if (!Utils.isEmptyString(cardOwner)) {
                    // i.putExtra("audioID", audioID);
                }

                if(Utils.isEmptyString(cardOwner))
                    i.putExtra("clicks", clicks);
                i.putExtra("originalChatId", originalChatId);
                i.putExtra("chatType", chatType);
                i.putExtra("textMsg", textMsg);
                i.putExtra("caption", commentStr);
                if(access_Token.length()>5){
                    shareMedia = "clickin,facebook";
                }else{
                    shareMedia = "clickin";
                }
                i.putExtra("facebookToken", access_Token);
                i.putExtra("sharingMedia", shareMedia);
                i.putExtra("shareStatus", "shared");
                i.putExtra("isMessageSender", isMessageSender);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setAction("SHARE");
                startActivity(i);

                break;
            case R.id.shr_caption:

                shr_caption.setCursorVisible(true);
                shr_caption.setHint("");
                break;

            case R.id.linear_layout_share:

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(shr_caption.getWindowToken(), 0);

                break;

        }

    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            access_Token = session.getAccessToken();
            Log.e("access_Token", "access_Token->" + access_Token);
            publishStory();
            if(access_Token.length()>5) {
                findViewById(R.id.shr_facebook).setBackgroundResource(R.drawable.facebook_blue);
            }else{
                findViewById(R.id.shr_facebook).setBackgroundResource(R.drawable.facebook_share_background);
            }
        } else if (state.isClosed()) {
            Log.e("access_Token", "access_Token->" + access_Token);
            System.out.println("Logged out..." +access_Token+" -------");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        android.util.Log.e("publishStory", "publishStory--onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void publishStory() {

        android.util.Log.e("publishStory", "publishStory mth");
        Session session = Session.getActiveSession();

        if (session != null) {
            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);

            }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);//akshit code for animation
    }
}
