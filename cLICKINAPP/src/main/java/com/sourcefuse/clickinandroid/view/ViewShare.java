package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.facebook.Session.OpenRequest;

/**
 * Created by prafull on 25/9/14.
 */
public class ViewShare extends Activity implements View.OnClickListener {

    public static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String TAG = ViewShare.class.getSimpleName();
    TextView mshr_point, mshr_comment;
    String fileId, clicks, textMsg, originalChatId, isMessageSender;
    String card_Accepted_Rejected, card_DB_ID, card_Played_Countered, card_id, card_content = " ", card_heading, card_originator, card_owner, card_clicks;
    boolean is_CustomCard;


    EditText shr_caption, shr_caption_trade;
    String image_url;//akshit to make common variable for image ,vedio Url .
    private ChatManager chatManager;
    private AuthManager authManager;
    private String access_Token = "-";
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

    private String shareMedia;
    private String mLocationCoordinates = null;
    private int chatType = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.view_share_screen);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        findViewById(R.id.shr_facebook).setOnClickListener(this);
        findViewById(R.id.shr_btn_share).setOnClickListener(this);
        findViewById(R.id.linear_layout_share).setOnClickListener(this);
        mshr_point = (TextView) findViewById(R.id.shr_point);
        mshr_comment = (TextView) findViewById(R.id.shr_comment);
        shr_caption = (EditText) findViewById(R.id.shr_caption);//akshit code
        shr_caption_trade = (EditText) findViewById(R.id.shr_caption_trade);//akshit code
        shr_caption_trade.setOnClickListener(this);
        shr_caption.setOnClickListener(this);

        // akshit code for closing keypad if touched anywhere outside
        ((LinearLayout) findViewById(R.id.linear_layout_root_share_screen)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(shr_caption.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(shr_caption_trade.getWindowToken(), 0);

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
                if (intent.hasExtra("location_coordinates")) {
                    mLocationCoordinates = intent.getStringExtra("location_coordinates");
                }
            } else if (intent.hasExtra("videoThumbnail")) {
                clicks = intent.getStringExtra("clicks");
                videoThumbnail = intent.getStringExtra("videoThumbnail");
                videoID = intent.getStringExtra("videoID");
                image_url = videoThumbnail;
            } else if (intent.hasExtra("card_id")) {
                card_Accepted_Rejected = intent.getStringExtra("card_Accepted_Rejected");
                card_DB_ID = intent.getStringExtra("card_DB_ID");
                card_Played_Countered = intent.getStringExtra("card_Played_Countered");
                card_id = intent.getStringExtra("card_id");
                card_clicks = intent.getStringExtra("clicks");
                if (card_clicks.equalsIgnoreCase("5")) {//akshit code
                    card_clicks = "05";
                }
                clicks = "no";
                card_content = intent.getStringExtra("card_content");
                card_heading = intent.getStringExtra("card_heading");
                card_originator = intent.getStringExtra("card_originator");
                card_owner = intent.getStringExtra("card_owner");
                image_url = intent.getStringExtra("card_url");
                is_CustomCard = intent.getBooleanExtra("is_CustomCard", false);

            } else if (intent.hasExtra("audioID")) {
                clicks = intent.getStringExtra("clicks");
                audioID = intent.getStringExtra("audioID");
            } else {
                clicks = intent.getStringExtra("clicks");
            }

            chatType = intent.getIntExtra("chatType", -1);
            textMsg = intent.getStringExtra("textMsg");

            isMessageSender = intent.getStringExtra("isMessageSender");
            originalChatId = intent.getStringExtra("originalChatId");


            //akshit Code Starts ,To Upload Image ,Vedio ,Audio.
            if (!Utils.isEmptyString(image_url) && Utils.isEmptyString(card_id)) {
//                shr_caption.setHint("Write your caption \nhere...");akshit code
                ImageView shr_image = (ImageView) findViewById(R.id.shr_user_image);
                shr_image.setVisibility(View.VISIBLE);
                //check whether image is already downloaded or not
                   /* default path where image are stored */
                String mContentUri = Utils.mImagePath + originalChatId + ".jpg"; // fetch data from
                Uri mUri = Utils.getImageContentUri(this, new File(mContentUri));
                if (!Utils.isEmptyString("" + mUri)) {  // check image exists or not
                    try {
                        shr_image.setImageURI(mUri);  // set thumb from uri

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    Picasso.with(ViewShare.this).load(image_url)
//                           .resize(200, 200).centerCrop()
                            .into(shr_image);
                }


            } else if (!Utils.isEmptyString(audioID)) {
                ImageView shr_image = (ImageView) findViewById(R.id.shr_user_image);
                shr_image.setVisibility(View.VISIBLE);
                shr_image.setBackgroundResource(R.drawable.soundicon_);
//                shr_caption.setHint("Write your caption \nhere...");akshit code
            }

            //akshit code for Trade Card
            if (!Utils.isEmptyString(card_id) || is_CustomCard) {
                findViewById(R.id.layout_image).setVisibility(View.GONE);
                findViewById(R.id.layout_card).setVisibility(View.VISIBLE);
                ImageView shr_image = (ImageView) findViewById(R.id.shr_user_trade_image);
                shr_image.setVisibility(View.VISIBLE);
//               shr_caption.setHint("Write your caption \nhere...");akshit code
                ((TextView) findViewById(R.id.trd_clicks_top_share)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.trd_clicks_bottom_share)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.trd_clicks_top_share)).setText(card_clicks);
                ((TextView) findViewById(R.id.trd_clicks_bottom_share)).setText(card_clicks);
                if (is_CustomCard) {
                    shr_image.setBackgroundResource(R.drawable.tradecardpink_big);
                    ((TextView) findViewById(R.id.custom_card_msg)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.custom_card_msg)).setText(card_heading);
                } else {
                    shr_image.setBackgroundResource(R.drawable.tradecardbg_blank);
                    ((TextView) findViewById(R.id.heading_top)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.heading_bottom)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.heading_top)).setText(card_heading);
                    ((TextView) findViewById(R.id.heading_bottom)).setText(card_content);

                }
            }//ends


            if (!clicks.equalsIgnoreCase("no")) {
                ((LinearLayout) findViewById(R.id.share_clicks_area)).setVisibility(View.VISIBLE);
                TextView clicktxt = (TextView) findViewById(R.id.share_clicks_text);
                clicktxt.setVisibility(View.VISIBLE);
                clicktxt.setText(clicks);
                clicktxt.setTextColor(getResources().getColor(R.color.white));
                ((ImageView) findViewById(R.id.share_clicks_heart)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.share_clicks_area)).setBackgroundColor(getResources().getColor(R.color.profile_name_b));


                if (!Utils.isEmptyString(textMsg)) {
                    TextView textMsgchat = (TextView) findViewById(R.id.share_chat_text);
                    TextView mShareTextLong = (TextView) findViewById(R.id.share_chat_textLong);
                    textMsgchat.setVisibility(View.VISIBLE);
                    if (textMsg.length() > 30) {
                        textMsgchat.setText(textMsg.substring(0, 30));
                        mShareTextLong.setText(textMsg.substring(30, textMsg.length()));
                        mShareTextLong.setVisibility(View.VISIBLE);
                    } else {
                        textMsgchat.setText(textMsg);
                    }

                }


            } else {

                if (!Utils.isEmptyString(textMsg) && Utils.isEmptyString(card_originator)) {
                    ((LinearLayout) findViewById(R.id.share_clicks_area)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findViewById(R.id.share_clicks_area)).setBackgroundColor(getResources().getColor(R.color.white));
                    TextView textMsgchat = (TextView) findViewById(R.id.share_chat_text);
                    TextView mShareTextLong = (TextView) findViewById(R.id.share_chat_textLong);
                    mShareTextLong.setVisibility(View.VISIBLE);
                    mShareTextLong.setTextColor(Color.BLACK);
                    mShareTextLong.setText(textMsg);
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
                //           Toast.makeText(this, "Will be implemented later on", Toast.LENGTH_SHORT).show();
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
                String commentStr = "";//akshit code

                if (!Utils.isEmptyString(card_originator)) {//akshit code
                    commentStr = shr_caption_trade.getText().toString().trim();


                } else {
                    commentStr = shr_caption.getText().toString().trim();//akshit code

                }


                if (Utils.isEmptyString(commentStr))
                    commentStr = "Write your caption here...";


                if (access_Token.length() < 3)
                    access_Token = "-";

                if (Utils.isEmptyString(textMsg))
                    textMsg = "";

                if (!Utils.isEmptyString(imageRatio)) {
                    i.putExtra("imageRatio", imageRatio);
                    i.putExtra("fileId", fileId);
                    if (!Utils.isEmptyString(mLocationCoordinates)) {
                        i.putExtra("location_coordinates", mLocationCoordinates);
                    }
                } else if (!Utils.isEmptyString(videoThumbnail)) {
                    i.putExtra("videoThumbnail", videoThumbnail);
                    i.putExtra("videoID", videoID);
                } else if (!Utils.isEmptyString(audioID)) {
                    i.putExtra("audioID", audioID);
                } else if (!Utils.isEmptyString(card_originator)) {
                    i.putExtra("card_Accepted_Rejected", card_Accepted_Rejected);
                    i.putExtra("card_DB_ID", card_DB_ID);
                    i.putExtra("card_Played_Countered", card_Played_Countered);

                    i.putExtra("card_content", card_content);
                    i.putExtra("card_heading", card_heading);
                    i.putExtra("card_originator", card_originator);
                    i.putExtra("card_owner", card_owner);
                    i.putExtra("card_url", image_url);
                    i.putExtra("card_id", card_id);
                    i.putExtra("is_CustomCard", is_CustomCard);
                }


                i.putExtra("originalChatId", originalChatId);

                if (!Utils.isEmptyString(card_originator)) {//akshit code to send clicks in case of trade card
                    i.putExtra("clicks", card_clicks);
                } else {
                    i.putExtra("clicks", clicks);
                }
                i.putExtra("textMsg", textMsg);
                i.putExtra("caption", commentStr);
                if (access_Token.length() > 5) {
                    shareMedia = "clickin,facebook";
                } else {
                    shareMedia = "clickin";
                }
                i.putExtra("facebookToken", access_Token);
                i.putExtra("sharingMedia", shareMedia);
                i.putExtra("shareStatus", "shared");
                i.putExtra("isMessageSender", isMessageSender);
                i.putExtra("chatType", chatType);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setAction("SHARE");
                startActivity(i);

                break;
            case R.id.shr_caption:

                shr_caption.setCursorVisible(true);
                shr_caption.setHint("");
                break;

            case R.id.shr_caption_trade://akshit code
                shr_caption_trade.setCursorVisible(true);
                shr_caption_trade.setHint("");
                break;

            case R.id.linear_layout_share:

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(shr_caption.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(shr_caption_trade.getWindowToken(), 0);


                break;

        }

    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            access_Token = session.getAccessToken();
            publishStory();
            if (access_Token.length() > 5) {
                findViewById(R.id.shr_facebook).setBackgroundResource(R.drawable.facebook_blue);
            } else {
                findViewById(R.id.shr_facebook).setBackgroundResource(R.drawable.facebook_share_background);
            }
        } else if (state.isClosed()) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void publishStory() {

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
