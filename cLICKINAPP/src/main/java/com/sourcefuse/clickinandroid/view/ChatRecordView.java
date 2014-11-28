package com.sourcefuse.clickinandroid.view;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.module.content.QBContent;
import com.quickblox.module.content.model.QBFile;
import com.quickblox.module.content.result.QBFileUploadTaskResult;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.custom.result.QBCustomObjectResult;
import com.sourcefuse.clickinandroid.dbhelper.ClickinDbHelper;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.APIs;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.AudioUtil;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.utils.VideoUtil;
import com.sourcefuse.clickinandroid.view.adapter.ChatRecordAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

public class ChatRecordView extends ClickInBaseView implements View.OnClickListener,
        TextWatcher {
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = ChatRecordView.class.getSimpleName();
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            //Do Something
            Log.e(TAG, "Start Recording");
            AudioUtil.startRecording();
            /*
             * Timer if required
			 */

        }
    };
    private static final String IMAGE_DIRECTORY_NAME = "Clickin Application";
    public MyQbChatService myQbChatService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            myQbChatService = ((MyQbChatService.LocalBinder) service).getService();
           /* myQbChatService.createRoom(mRoomName);*/

            // showMessages();

            // Tell the user about this for our demo.
//            Toast.makeText(Binding.this, R.string.local_service_connected,
//                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            myQbChatService = null;
//            Toast.makeText(Binding.this, R.string.local_service_disconnected,
//                    Toast.LENGTH_SHORT).show();
        }
    };
    int myvalue = 10, min = -10;
    String chatString = "";
    int seekValue = 0;
    int maxValue = 20; // Double of range
    int initialProgresss = maxValue / 2;

    //private QBPrivateChat chatObject;
    String firstname;
    String[] splitted;
    private SeekBar mybar;
    private TextView pos, neg, profileName, typingtext, myTotalclicks, partnerTotalclicks;
    private Button send, btnToCard;
    private int relationListIndex, myClicks, userClicks;
    private String qBId,  partnerPic, partnerName, partnerId, partnerPh, myTotalString, userTotalClicks;
    public static String rId="";
    private ChatManager chatManager;
    private AuthManager authManager;
    private RelationManager relationManager;
    private EditText chatText;
    private ImageView mypix, partnerPix, menu, attachBtn, notificationIcon;
    private PullToRefreshListView chatListView;
    private ChatRecordAdapter adapter = null;
    //private Typeface typeface;
    private boolean isHistroy = true;
    private long sentOn;
    private String chatId;
    private boolean emptyDb = true;
    private boolean showAttachmentView = true;
    private LinearLayout llAttachment;
    private ImageView atchPhoto, attachAudio, attachVideo, attachLocation;
    private Uri mImageCaptureUri = null;
    private String path, uploadedImgUrl, currentImagepath;
    private String videofilePath = null;
    private Dialog dialog;
    private Handler myHandler;
    private String audioFilePath = null;
    private int CHAT_TYPE;
    private boolean mIsBound;

    //chatId-unquie to chat msg- use to track delivery status
    Integer msgId;

    private ClickinDbHelper dbHelper;
    //monika- variable to store video thumbnail
    private String thumurl=null;

    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn =
                {
                        MediaStore.Images.Media.DATA
                };
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_chat_layout);
        Utils.launchBarDialog(this);
        Intent i = new Intent(this, MyQbChatService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        addMenu(false);
        // loginToQuickBlox();
        //  typeface = Typeface.createFromAsset(ChatRecordView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        send = (Button) findViewById(R.id.btn_send);
        chatListView = (PullToRefreshListView) findViewById(R.id.chat_list);
        chatText = (EditText) findViewById(R.id.edit_chatBox);
        mybar = (SeekBar) findViewById(R.id.seekBar1);
        pos = (TextView) findViewById(R.id.tv_positive);
        neg = (TextView) findViewById(R.id.tv_negetive);
        mypix = (ImageView) findViewById(R.id.iv_my_pix);
        attachBtn = (ImageView) findViewById(R.id.iv_attach);
        attachBtn.setScaleType(ImageView.ScaleType.FIT_XY);
        partnerPix = (ImageView) findViewById(R.id.iv_partner_pix);
        menu = (ImageView) findViewById(R.id.iv_menu_button);
        notificationIcon = (ImageView) findViewById(R.id.iv_notification_button);
        profileName = (TextView) findViewById(R.id.tv_profiler);
        llAttachment = (LinearLayout) findViewById(R.id.ll_attachment);
        typingtext = (TextView) findViewById(R.id.tv_typing);

        myTotalclicks = (TextView) findViewById(R.id.tv_myclick);
        partnerTotalclicks = (TextView) findViewById(R.id.tv_partner_click);

        atchPhoto = (ImageView) findViewById(R.id.iv_photo);
        attachAudio = (ImageView) findViewById(R.id.iv_adiuo);
        attachVideo = (ImageView) findViewById(R.id.iv_video);
        attachLocation = (ImageView) findViewById(R.id.iv_location);
        btnToCard = (Button) findViewById(R.id.btn_to_card);

        chatText.setMovementMethod(new ScrollingMovementMethod());// akshit Code to scroll text smoothly inside edit text

        //profileName.setTypeface(typeface, typeface.BOLD);
        // typingtext.setTypeface(typeface);
        menu.setOnClickListener(this);
        notificationIcon.setOnClickListener(this);
        pos.setText("+" + mybar.getMax());
        neg.setText("-" + mybar.getMax());

        mybar.setMax(maxValue);
        mybar.setProgress(initialProgresss);

        send.setOnClickListener(this);
        btnToCard.setOnClickListener(this);
        partnerPix.setOnClickListener(this);


        attachBtn.setOnClickListener(this);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        qBId = getIntent().getExtras().getString("quickId");
        partnerPic = getIntent().getExtras().getString("partnerPic");
        partnerName = getIntent().getExtras().getString("partnerName");
        splitted = partnerName.split("\\s+");
        firstname = splitted[0].toUpperCase();

        rId = getIntent().getExtras().getString("rId");
        partnerId = getIntent().getExtras().getString("partnerId");
        relationManager = ModelManager.getInstance().getRelationManager();
        relationManager.getPartnerName = partnerName;

        myTotalString = getIntent().getExtras().getString("myClicks");
        userTotalClicks = getIntent().getExtras().getString("userClicks");

        myClicks = Integer.parseInt(myTotalString);
        userClicks = Integer.parseInt(userTotalClicks);

        myTotalclicks.setText("" + myTotalString);
        partnerTotalclicks.setText("" + userTotalClicks);

        partnerPh = getIntent().getExtras().getString("partnerPh");
        relationListIndex = getIntent().getExtras().getInt("relationListIndex");
        chatManager = ModelManager.getInstance().getChatManager();
        //chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), "");
// get Chat record From server

        // chatManager.chatListFromServer.clear();


        profileName.setText("" + splitted[0]);
        try {
            Uri tempUri = authManager.getUserImageUri();
            if (tempUri != null) {
                Bitmap imageBitmap;
                imageBitmap = authManager.getUserbitmap();
                if (imageBitmap != null)
                    mypix.setImageBitmap(imageBitmap);
                else {
                    if (!authManager.getGender().equalsIgnoreCase("")) {

                        if (authManager.getGender().equalsIgnoreCase("guy")) {
                            try {
                                if (!authManager.getUserPic().equalsIgnoreCase("")) {
                                    Picasso.with(this)
                                            .load(authManager.getUserPic())
                                            .skipMemoryCache()

                                            .error(R.drawable.male_user)
                                            .into(mypix);
                                } else {
                                    mypix.setImageResource(R.drawable.male_user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mypix.setImageResource(R.drawable.male_user);
                            }
                        } else if (authManager.getGender().equalsIgnoreCase("girl")) {
                            try {
                                if (!authManager.getUserPic().equalsIgnoreCase("")) {
                                    Picasso.with(this)
                                            .load(authManager.getUserPic())
                                            .skipMemoryCache()

                                            .error(R.drawable.female_user)
                                            .into(mypix);
                                } else {
                                    mypix.setImageResource(R.drawable.female_user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mypix.setImageResource(R.drawable.female_user);
                            }
                        }

                    } else {
                        mypix.setImageResource(R.drawable.male_user);
                    }
                }

            } else {
                if (authManager.getGender() != null) {

                    if (authManager.getGender().equalsIgnoreCase("guy")) {
                        try {
                            if (!authManager.getUserPic().equalsIgnoreCase("")) {
                                Picasso.with(this)
                                        .load(authManager.getUserPic())
                                        .skipMemoryCache()

                                        .error(R.drawable.male_user)
                                        .into(mypix);
                            } else {
                                mypix.setImageResource(R.drawable.male_user);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mypix.setImageResource(R.drawable.male_user);
                        }
                    } else if (authManager.getGender().equalsIgnoreCase("girl")) {
                        try {
                            if (!authManager.getUserPic().equalsIgnoreCase("")) {
                                Picasso.with(this)
                                        .load(authManager.getUserPic())
                                        .skipMemoryCache()

                                        .error(R.drawable.female_user)
                                        .into(mypix);
                            } else {
                                mypix.setImageResource(R.drawable.female_user);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mypix.setImageResource(R.drawable.female_user);
                        }
                    }

                } else {
                    mypix.setImageResource(R.drawable.male_user);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Picasso.with(ChatRecordView.this).load(partnerPic)

                .error(R.drawable.male_user).into(partnerPix);


        mybar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                myvalue = progress - 10;
                Log.e("", "progress---->" + progress);
                if (myvalue > 0) {
                    // pos.setText("" + myvalue);
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setVisibility(View.VISIBLE);
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setBackgroundResource(R.color.white);
                    ((TextView) findViewById(R.id.tv_flipper_value)).setText("" + clickForFlipper(myvalue));
                    seekValue = myvalue;
                }
                if (myvalue < 0) {
                    seekValue = myvalue;
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setVisibility(View.VISIBLE);
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setBackgroundResource(R.color.black_opacity);
                    ((TextView) findViewById(R.id.tv_flipper_value)).setText("" + myvalue);
                }
                if (myvalue == 0) {
                    seekValue = 0;
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setVisibility(View.GONE);
                }
            }
        });

        chatText.addTextChangedListener(this);

        atchPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAttachView();
                imageDialog();
            }
        });

        attachAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                hideAttachView();
                alertDialog();

            }
        });

        attachVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                hideAttachView();
                VideoUtil.videoDialog(ChatRecordView.this);
            }
        });
        attachLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                hideAttachView();
                Intent intent = new Intent(ChatRecordView.this, MapView.class);
                startActivity(intent);
            }
        });


        chatListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                try {
                    // int lastIndex = chatManager.chatMessageList.size() - 1;
                    String lastChatId = chatManager.chatMessageList.get(0).chatId;
                    chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), lastChatId);
                } catch (Exception e) {

                }
            }
        });

        //clear the message list always to initiate a new chat
        ModelManager.getInstance().getChatManager().chatMessageList.clear();
        setlist();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.top_out);
    }

     /* private void setHistoryChat() {

            adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, chatManager.chatListFromServer);
            chatListView.setAdapter(adapter);

      }*/

    public void imageDialog() {

        final Dialog mdialog = new Dialog(ChatRecordView.this);
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mdialog.setContentView(R.layout.alert_take_picture);
        Button cancel = (Button) mdialog.findViewById(R.id.dialog_cancel);
        TextView textcamera = (TextView) mdialog.findViewById(R.id.take_picture);
        TextView textgallery = (TextView) mdialog.findViewById(R.id.from_gallery);
        textcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                cameraIntent.putExtra("return-data", true);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);

                mdialog.dismiss();
            }
        });
        textgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.SELECT_PICTURE);
                mdialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdialog.dismiss();
            }
        });
        mdialog.show();
    }

    public void alertDialog() {
        dialog = new Dialog(ChatRecordView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_record_vice);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.txt_hold_to_record);
        Button recordVice = (Button) dialog.findViewById(R.id.btn_record);
        //msgI.setText(msgStrI);
        recordVice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myHandler = new Handler();
                        myHandler.postDelayed(myRunnable, 1000);
                        break;
                    case MotionEvent.ACTION_UP:
                        try {
                            myHandler.removeCallbacks(myRunnable);
                            audioFilePath = AudioUtil.stopRecording();
                            Log.e(TAG, "stop Recording AND File Name is ->" + audioFilePath);
                            if (!Utils.isEmptyString(audioFilePath)) {
                                attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.c_audio_atch));
                            }
                            dialog.dismiss();
                        } catch (Exception e) {
                        }
                        break;
                }
                return true;
            }
        });

        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

   
    public void setlist() {

        try {
            dbHelper = new ClickinDbHelper(this);
            dbHelper.openDataBase();
            authManager = ModelManager.getInstance().getAuthorizationManager();


            chatManager.chatMessageList = dbHelper.getAllChat(rId);

            if (chatManager.chatMessageList.size() == 0) {
                emptyDb = true;
                chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), "");
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception-> " + e.toString());
        }

        //temp code
        //chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), "");
        //  fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), "");
        if (adapter == null) {
            adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, chatManager.chatMessageList);
            // adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo,chatData);
            chatListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        Utils.dismissBarDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String chatString = chatText.getText().toString();

                if ((chatString.length() > 0 || isClicks() == true || mImageCaptureUri != null) || audioFilePath != null || videofilePath != null) {
                    if (mImageCaptureUri == null && audioFilePath == null && videofilePath == null) {// if all media files are null
                        ChatMessageBody temp = new ChatMessageBody();

                        if (isClicks() == true) {
                            temp.clicks = Utils.convertClicks(String.valueOf(seekValue)).trim();
                            temp.textMsg = temp.clicks + "        " + chatString;
                        } else {
                            temp.clicks = "no";
                            temp.textMsg = chatString;
                        }
                        temp.partnerQbId = qBId;
                        temp.senderQbId = authManager.getQBId();
                        temp.chatType = Constants.CHAT_TYPE_TEXT;
                        CHAT_TYPE = Constants.CHAT_TYPE_TEXT;

                        long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

                        temp.sentOn = "" + sentOntime;
                        temp.chatId = authManager.getQBId() + qBId + sentOntime;

                        ShowValueinChat(temp);
                        myQbChatService.sendMessage(temp);
                       // createRecordForHistory(temp);

                        chatText.setText("");
                        seekValue = 0;
                        mybar.setProgress(10);
                    } else if(mImageCaptureUri != null) {//image is attachedd
                            CHAT_TYPE = Constants.CHAT_TYPE_IMAGE;
                            sendMsgToQB(path);

                    } else if(!Utils.isEmptyString(audioFilePath)) { //Audio is attached

                            CHAT_TYPE = Constants.CHAT_TYPE_AUDIO;
                            sendMsgToQB(audioFilePath);

                    }else if(!Utils.isEmptyString(videofilePath)) { //Video is attached

                            CHAT_TYPE = Constants.CHAT_TYPE_VIDEO;
                            sendMsgToQB(videofilePath);
                    }

                }

                /* to detele uri once image is send prafull */
                mImageCaptureUri  = null;
                path = null;
                attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.r_footer_icon));
                // chatText.setText("");
                break;
            case R.id.iv_menu_button:
                hideAttachView();
                slidemenu.showMenu(true);
                break;
            case R.id.iv_notification_button:
                hideAttachView();
                slidemenu.showSecondaryMenu(true);
                break;
            case R.id.btn_to_card:
                Intent intent = new Intent(ChatRecordView.this, CardView.class);
                intent.putExtra("qBId", qBId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                break;

            case R.id.iv_attach:
                if (showAttachmentView) {
                    if (llAttachment.getVisibility() == View.GONE) {
                        Animation slideLeft = AnimationUtils.loadAnimation(ChatRecordView.this, R.anim.slide_attach_view);
                        llAttachment.startAnimation(slideLeft);
                        //llAttachment.setVisibility(0);
                    }
                    llAttachment.setVisibility(View.VISIBLE);
                    showAttachmentView = false;
                } else {
                    hideAttachView();
                }
                break;

            case R.id.iv_partner_pix:
                Intent viewProfile = new Intent(ChatRecordView.this, JumpOtherProfileView.class);
                //  viewProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                viewProfile.putExtra("FromOwnProfile", true);
                viewProfile.putExtra("phNumber", partnerPh);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                startActivity(viewProfile);
                break;


        }
    }



    //monika- function to show chat in listview
    private void ShowValueinChat(ChatMessageBody obj) {

        authManager = ModelManager.getInstance().getAuthorizationManager();


        obj.sharedMessage = null;
        //   obj.deliveredChatID=null;
        // obj.cardDetails=null;
        obj.isDelivered = Constants.MSG_SENDING;
        obj.relationshipId = rId;
        obj.userId = authManager.getUserId();
        obj.senderUserToken = authManager.getUsrToken();
        obj.senderQbId = authManager.getQBId();

        //monika- remove click value from text msg
        if ((!obj.clicks.equalsIgnoreCase("no"))) {
            if (obj.textMsg.length() > 3) {
                obj.textMsg = obj.textMsg.substring(3).trim();
            } else
                obj.textMsg = null;
        }

        ArrayList<ChatMessageBody> tempChatList = ModelManager.getInstance().getChatManager().chatMessageList;
        tempChatList.add(obj);

        if (adapter == null) {
            adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, tempChatList);
            chatListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        //  return obj;
        //  createRecordForHistory(obj);

    }


    /*  private void sendCardToPartner(String card_url, String cardTittle, String cardDiscription, String card_Id, String clicks, String is_CustomCard, String card_DB_ID, String accepted_Rejected, String played_Countered, String card_originator, String card_owner) {

            try {
                  DefaultPacketExtension extension = new DefaultPacketExtension("extraParams", "jabber:client");

                  sentOn = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                  String card_id = authManager.getQBId() + qBId + sentOn;

                  ArrayList al = new ArrayList();

                  al.add(card_Id);
                  al.add(cardTittle);
                  al.add(cardDiscription);
                  al.add(card_url);
                  al.add(clicks);
                  al.add(accepted_Rejected);
                  Log.e(TAG, "card_originator-->" + card_originator);
                  al.add(is_CustomCard);
                  al.add(card_originator);
                  al.add(card_DB_ID);


                  sentOn = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                  String chid = authManager.getQBId() + qBId + sentOn;
         //         createRfecordOnQuickBlox(null, null, null, rId, authManager.getUserId(), authManager.getUsrToken(), "" + sentOn, chid, "5", null, null, al.toString(), null, null, null);

                  if (accepted_Rejected.equalsIgnoreCase("accepted")) {
                        if (card_originator.equalsIgnoreCase(authManager.getUserId())) {

                              myClicks = (myClicks - (Integer.parseInt(clicks)));
                              myTotalclicks.setText("" + myClicks);
                              relationManager = ModelManager.getInstance().getRelationManager();
                              relationManager.acceptedList.get(relationListIndex).setClicks(Integer.toString(myClicks));

                              userClicks = (userClicks + (Integer.parseInt(clicks)));
                              partnerTotalclicks.setText("" + userClicks);
                              relationManager = ModelManager.getInstance().getRelationManager();
                              relationManager.acceptedList.get(relationListIndex).setUserClicks(Integer.toString(userClicks));
                        } else {
                              myClicks = (myClicks + (Integer.parseInt(clicks)));
                              myTotalclicks.setText("" + myClicks);
                              relationManager = ModelManager.getInstance().getRelationManager();
                              relationManager.acceptedList.get(relationListIndex).setClicks(Integer.toString(myClicks));

                              userClicks = (userClicks - (Integer.parseInt(clicks)));
                              partnerTotalclicks.setText("" + userClicks);
                              relationManager = ModelManager.getInstance().getRelationManager();
                              relationManager.acceptedList.get(relationListIndex).setUserClicks(Integer.toString(userClicks));
                        }

                        Log.e(TAG, "---clicks--> " + clicks);
                  }

                  extension.setValue("card_clicks", clicks);
                  extension.setValue("card_owner", card_owner);
                  extension.setValue("card_content", cardDiscription);
                  extension.setValue("is_CustomCard", is_CustomCard);
                  extension.setValue("card_DB_ID", card_DB_ID);
                  extension.setValue("card_heading", cardTittle);
                  extension.setValue("card_Accepted_Rejected", accepted_Rejected);
                  extension.setValue("card_url", card_url);
                  extension.setValue("card_id", card_id);
                  extension.setValue("card_Played_Countered", played_Countered);
                  extension.setValue("card_originator", card_originator);

                  Log.e(TAG, "is_CustomCard---> " + is_CustomCard);

                  Message message = new Message();
                  message.setType(Message.Type.chat); // 1-1 chat message
                  message.setBody("");
                  message.addExtension(extension);
                 // chatObject.sendMessage(Integer.parseInt(qBId), message);

                  ChatRecordBeen addChat = new ChatRecordBeen();
                  addChat.setChatType("5");
                  addChat.setSenderQbId(authManager.getQBId());
                  addChat.setRecieverQbId(qBId);
                  addChat.setUserId(authManager.getUserId());
                  addChat.setCard_clicks(clicks);
                  addChat.setCard_owner(authManager.getQBId());
                  addChat.setCard_content(cardDiscription);
                  addChat.setIs_CustomCard(is_CustomCard);
                  addChat.setCard_DB_ID(card_DB_ID);
                  addChat.setCard_heading(cardTittle);
                  addChat.setCard_Accepted_Rejected(accepted_Rejected);
                  if (is_CustomCard.equalsIgnoreCase("true")) {
                        addChat.setCard_url("https://s3.amazonaws.com/clickin-dev/cards/a/1080/custom_tradecart.jpg");
                  } else {
                        addChat.setCard_url(card_url);
                  }
                  addChat.setCard_id(card_id);
                  addChat.setCard_Played_Countered(played_Countered);
                  addChat.setCard_originator(card_originator);
                  addChat.setCardPartnerName(partnerName);
                  addChat.setTimeStamp(String.valueOf(sentOn));
                  chatManager.chatListFromServer.add(addChat);
                  adapter.notifyDataSetChanged();


            } catch (Exception e) {
                  try {
                        //chatObject.removeChatMessageListener(this);
                       // chatObject.addChatMessageListener(this);
                  } catch (Exception e1) {
                  }
          *//*  chatObject = null;
            authManager = ModelManager.getInstance().getAuthorizationManager();
            chatObject = authManager.getqBPrivateChat();
            chatObject.addChatMessageListener(this);*//*
                  e.printStackTrace();
            }
      }*/

   //monika-fucntion to upload file on Qb
    private void uploadImageFileOnQB(String tempUrl, String msgId,int type) {
        System.out.println("tempUrl---> "+tempUrl);
        File mfile = new File(tempUrl);
        final String chatId=msgId;
        final int chatType=type;
        Boolean fileIsPublic = true;
        QBContent.uploadFileTask(mfile, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>() {
            @Override
            public void onSuccess(QBFile file, Bundle params) {

                    String fileUrl= file.getPublicUrl().toString();
                Log.e(TAG, "uploadImageFileOnQB--> " + fileUrl);
                    sendMediaMsgToQB(fileUrl, chatId,chatType);
            }

            @Override
            public void onError(List<String> errors) {
                // error
                Log.e(TAG,"uploadImageFileOnQB--> "+"error");
            }
        });

    }

    //monika- common function to create msg in case of media attachment
    private void sendMsgToQB(String tempPath) {
        ChatMessageBody temp = new ChatMessageBody();
        String tempUrlToUpload="";
        String chatString = chatText.getText().toString();
        switch (CHAT_TYPE) {
            case Constants.CHAT_TYPE_IMAGE:
                temp.imageRatio = "1";
                temp.content_url = path;
                tempUrlToUpload=tempPath;
                temp.isDelivered=Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_IMAGE;
                break;
            case Constants.CHAT_TYPE_AUDIO:
                temp.content_url = tempPath;
                tempUrlToUpload=path;
                temp.isDelivered=Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_AUDIO;
                break;
            case Constants.CHAT_TYPE_VIDEO:
                temp.content_url = thumurl;
                tempUrlToUpload=thumurl;
                temp.video_thumb = thumurl;
                temp.isDelivered=Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_VIDEO_INITATING;
                break;
            case Constants.CHAT_TYPE_LOCATION:
                temp.content_url = tempPath;
                tempUrlToUpload=tempPath;
                temp.imageRatio = "1";
                temp.location_coordinates = "";
                temp.isDelivered=Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_LOCATION;
                break;

            default:
        }
        if (isClicks() == true) {
            temp.clicks = Utils.convertClicks(String.valueOf(seekValue)).trim();
            temp.textMsg = temp.clicks + "        " + chatString;
        } else {
            temp.clicks = "no";
            temp.textMsg = chatString;
        }
        temp.partnerQbId = qBId;
        temp.senderQbId = authManager.getQBId();

        long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

        temp.sentOn = "" + sentOntime;
        temp.chatId = authManager.getQBId() + qBId + sentOntime;

        //     setValueForHistory(temp);
        ShowValueinChat(temp);
        chatText.setText("");
        seekValue = 0;
        mybar.setProgress(10);
       // mImageCaptureUri=null;

        attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.r_footer_icon));
        uploadImageFileOnQB(tempUrlToUpload,temp.chatId,temp.chatType);



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String actionReq = intent.getAction();
        if (actionReq.equalsIgnoreCase("UPDATE")) {
            //   Utils.launchBarDialog(this);
            Intent i = new Intent(this, MyQbChatService.class);
            bindService(i, mConnection, Context.BIND_AUTO_CREATE);

            updateValues(intent);
        } else if (actionReq.equalsIgnoreCase("CARD")) {
            Log.e(TAG + "onNewIntent", "onNewIntent");
            ChatMessageBody temp = new ChatMessageBody();

            temp.is_CustomCard = intent.getExtras().getBoolean("is_CustomCard");
            if (!temp.is_CustomCard) {
                temp.card_DB_ID = intent.getExtras().getString("card_Db_id");
                temp.card_url = intent.getExtras().getString("card_url");
                temp.card_content = intent.getExtras().getString("Discription");

            }
            temp.card_id = intent.getExtras().getString("card_id");
            if (Utils.isEmptyString(temp.card_id)) {
                long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                temp.card_id = authManager.getQBId() + qBId + sentOntime;
            }
            temp.card_heading = intent.getExtras().getString("Title");
            temp.clicks = intent.getExtras().getString("card_clicks");
            temp.card_Accepted_Rejected = intent.getExtras().getString("card_Accepted_Rejected");
            if(Utils.isEmptyString(temp.card_Accepted_Rejected))
                temp.card_Accepted_Rejected = "nil";
            temp.card_Played_Countered = intent.getExtras().getString("played_Countered");
            if(Utils.isEmptyString(temp.card_Played_Countered))
                temp.card_Played_Countered = "PLAYED A CARD";
            temp.card_originator = intent.getExtras().getString("card_originator");
            if(Utils.isEmptyString(temp.card_originator))
            temp.card_originator = rId;

            long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
            temp.sentOn = "" + sentOntime;
            temp.card_owner = authManager.getQBId();
            temp.chatType = Constants.CHAT_TYPE_CARD;
            temp.partnerQbId = qBId;
            temp.textMsg = "";

            ShowValueinChat(temp);

            if (myQbChatService != null)
                myQbChatService.sendMessage(temp);

        }

    }

    private void hideAttachView() {
        if (llAttachment.getVisibility() == View.VISIBLE) {
            Animation slideLeft = AnimationUtils.loadAnimation(ChatRecordView.this, R.anim.slide_right_to_left);
            llAttachment.startAnimation(slideLeft);
            //llAttachment.setVisibility(0);
            llAttachment.setVisibility(View.GONE);
            showAttachmentView = true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        if (s.length() > 0) {
            myQbChatService.sendTypeNotification("YES", qBId);

        } else {
            myQbChatService.sendTypeNotification("NO", qBId);
        }
     /*   DefaultPacketExtension extension = new DefaultPacketExtension("extraParams", "jabber:client");
        if (s.length() > 0) {
            extension.setValue("isComposing", "YES");
        } else {
            extension.setValue("isComposing", "NO");
        }
        Message message = new Message();
        message.setType(Message.Type.chat); // 1-1 chat message
        message.addExtension(extension);
        try {
            //chatObject.sendMessage(Integer.parseInt(qBId), message);
        } catch (Exception e) {

            try {
                // againLoginToQuickBlox();
                // chatObject.removeChatMessageListener(this);
                //chatObject.addChatMessageListener(this);
            } catch (Exception e1) {
            }
            e.printStackTrace();
        }*/
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        super.onDestroy();

        try {
            // dbHelper.deleteChat(authManager.getQBId(), qBId);
            //dbHelper.addChatList(chatManager.chatListFromServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);

        try {
            authManager = ModelManager.getInstance().getAuthorizationManager();
            //chatObject = authManager.getqBPrivateChat();
            // chatObject.removeChatMessageListener(this);
            //chatObject.addChatMessageListener(this);
        } catch (Exception e) {
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        new DBTask().execute();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public void onEventMainThread(String message) {
        android.util.Log.d(TAG, "onEventMainThread->" + message);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("FecthChat True")) {
            chatListView.onRefreshComplete();
            if (chatManager.chatMessageList.size() != 0) {
                if (adapter == null) {
                    adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, chatManager.chatMessageList);
                    chatListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }


            Log.e("1", "message->" + message);
        } else if (message.equalsIgnoreCase("FecthChat False")) {
            chatListView.onRefreshComplete();
            //setlist();
            Utils.dismissBarDialog();
            android.util.Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("FecthChat Network Error")) {
            Utils.fromSignalDialog(ChatRecordView.this, AlertMessage.connectionError);
            android.util.Log.d("3", "message->" + message);
        } else if (message.equalsIgnoreCase("Chat Message Recieve")) {
            adapter.notifyDataSetChanged();
        } else if (message.equalsIgnoreCase("Composing YES")) {
            typingtext.setVisibility(View.VISIBLE);
            typingtext.setText("Typing..");
        } else if (message.equalsIgnoreCase("Composing NO")) {
            typingtext.setVisibility(View.VISIBLE);
            typingtext.setText("online");
        }else if(message.startsWith("Delivered Msg")){
            String chatId=message.substring(13);
          //  updateChatDeliverStatusInList(chatId);

        }

    }

    /*   public String getRealPathFromURI(Uri uri) {
             Cursor cursor = getContentResolver().query(uri, null, null, null, null);
             cursor.moveToFirst();
             int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
             return cursor.getString(idx);
       }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case Constants.CAMERA_REQUEST:
                        Bitmap imageBitmap;
                        try {

                                          /* code prafull for camera */
                            Bitmap bitmap = BitmapFactory.decodeFile(mImageCaptureUri.getPath(), new BitmapFactory.Options());
                            try {
                                ExifInterface ei = new ExifInterface(mImageCaptureUri.getPath());
                                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                                int angle = 0;

                                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                                    angle = 90;
                                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                                    angle = 180;
                                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                                    angle = 270;
                                }
                                Matrix mat = new Matrix();
                                mat.postRotate(angle);
                                android.util.Log.e("angle from camera --->", "" + angle);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Bitmap resized;
                            if (bitmap.getWidth() >= bitmap.getHeight()) {

                                resized = Bitmap.createBitmap(
                                        bitmap,
                                        bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                                        0,
                                        bitmap.getHeight(),
                                        bitmap.getHeight()
                                );

                            } else {

                                resized = Bitmap.createBitmap(
                                        bitmap,
                                        0,
                                        bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                                        bitmap.getWidth(),
                                        bitmap.getWidth()
                                );
                            }

                            mImageCaptureUri = Utils.decodeUri(ChatRecordView.this, mImageCaptureUri, 550);
                            path = Utils.getRealPathFromURI(mImageCaptureUri, ChatRecordView.this);
                            currentImagepath = mImageCaptureUri.toString();

                            bitmap.recycle();
                            attachBtn.setImageBitmap(resized);

                        } catch (Exception ex) {
                            Log.e("Exception", "Exception-->" + ex.toString());
                            ex.printStackTrace();
                        }


                        break;
                    case Constants.SELECT_PICTURE:
                        Log.e(TAG, "SELECT_PICTURE" + "--> " + mImageCaptureUri);
                        mImageCaptureUri = data.getData();
                        try {

                                          /*  image from gallery  */

                            Bitmap bitmap = getBitmapFromCameraData(data, getApplicationContext());
                 /*    pick image from gallery  prafull  */


                            try {
                                ExifInterface ei = new ExifInterface(Utils.getRealPathFromURI(mImageCaptureUri, this));
                                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                                int angle = 0;

                                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                                    angle = 90;
                                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                                    angle = 180;
                                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                                    angle = 270;
                                }
                                Matrix mat = new Matrix();
                                mat.postRotate(angle);


                                android.util.Log.e("angle from gallery --->", "" + angle);

                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Bitmap resized;
                            if (bitmap.getWidth() >= bitmap.getHeight()) {
                                resized = Bitmap.createBitmap(
                                        bitmap,
                                        bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                                        0,
                                        bitmap.getHeight(),
                                        bitmap.getHeight()
                                );

                            } else {
                                resized = Bitmap.createBitmap(
                                        bitmap,
                                        0,
                                        bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                                        bitmap.getWidth(),
                                        bitmap.getWidth()
                                );
                            }
                            bitmap.recycle();
                            mImageCaptureUri = Utils.decodeUri(ChatRecordView.this, mImageCaptureUri, 550);
                            path = Utils.getRealPathFromURI(mImageCaptureUri, ChatRecordView.this);
                            currentImagepath = mImageCaptureUri.toString();
                            attachBtn.setImageBitmap(resized);

                        } catch (Exception ex) {
                            Log.e("Exception", "Exception-->" + ex);
                        }
                        break;
                    case VideoUtil.REQUEST_VIDEO_CAPTURED:
                        Log.e(TAG, "Video saved to:" + VideoUtil.videofilePath);
                        if (!Utils.isEmptyString(VideoUtil.videofilePath)) {
                            videofilePath = VideoUtil.videofilePath;
                            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(VideoUtil.videofilePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                            if (videofilePath.contains(".mp4")){
                                thumurl = videofilePath.replace(".mp4", "thumb");
                                thumurl = writePhotoJpg(bMap,thumurl);
                            }
                            attachBtn.setImageBitmap(bMap);
                            //uploadImageOnQuickBlox(VideoUtil.videofilePath, "", "");
                        }
                        break;
                    case VideoUtil.REQUEST_VIDEO_CAPTURED_FROM_GALLERY:
                        mImageCaptureUri = data.getData();
                        path = Utils.getRealPathFromURI(mImageCaptureUri, ChatRecordView.this);
                        videofilePath = path;
                        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(VideoUtil.videofilePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                        if (videofilePath.contains(".mp4")){
                            thumurl = videofilePath.replace(".mp4", "thumb");
                            thumurl = writePhotoJpg(bMap,thumurl);
                        }
                        attachBtn.setImageBitmap(bMap);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            android.util.Log.d(TAG, "" + e);
        } catch (Error e) {
            android.util.Log.d(TAG, "" + e);
        }
    }
// IMAGE STUFF END

    // Audio STUFF STArt

    // IMAGE STUFF start
    private void uploadImageOnQuickBlox(final String path, final String msg, final String clicks, final String chat_Id) {
        Log.e(TAG, "uploadImageOnQuickBlox.....Uploading--> " + path);
        File mfile = new File(path);
        QBContent.uploadFileTask(mfile, true, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBFileUploadTaskResult res = (QBFileUploadTaskResult) result;
                    uploadedImgUrl = res.getFile().getPublicUrl().toString();
                    Log.e(TAG, "Uploaded  --> " + uploadedImgUrl);
                    sendImagetoPartner(uploadedImgUrl, msg, clicks);
                    if (clicks.equalsIgnoreCase("no")) {

                        ////createRfecordOnQuickBlox(msg, null, uploadedImgUrl, rId, authManager.getUserId(), authManager.getUsrToken(), "" + sentOn, chat_Id, "2", null, "1.000000", null, null, null, null);
                    } else {
                        //createRfecordOnQuickBlox(msg, clicks, uploadedImgUrl, rId, authManager.getUserId(), authManager.getUsrToken(), "" + sentOn, chat_Id, "2", null, "1.000000", null, null, null, null);

                    }
                }
            }
        });
    }

    private void sendImagetoPartner(String filepath, String msg, String clicks) {
        Log.e(TAG, "uploadImageOnQuickBlox.....msg--> " + msg);
        try {
            DefaultPacketExtension extension = new DefaultPacketExtension("extraParams", "jabber:client");

            extension.setValue("imageRatio", "1");
            if (clicks.equalsIgnoreCase("no")) {
                extension.setValue("clicks", "no");
            } else {
                extension.setValue("clicks", clicks);
            }
            extension.setValue("fileID", filepath);
            Message message = new Message();
            message.setType(Message.Type.chat); // 1-1 chat message
            message.setBody("" + msg);
            message.addExtension(extension);
            // chatObject.sendMessage(Integer.parseInt(qBId), message);
        } catch (Exception e) {
            try {
                //chatObject.removeChatMessageListener(this);
                // chatObject.addChatMessageListener(this);
            } catch (Exception e1) {
            }
          /*  chatObject = null;
            authManager = ModelManager.getInstance().getAuthorizationManager();
            chatObject = authManager.getqBPrivateChat();
            chatObject.addChatMessageListener(this);*/
            e.printStackTrace();
        }
    }

    public static String  writePhotoJpg(Bitmap data, String pathName) {
        String thumbpath = pathName +".jpg";
        File file = new File(thumbpath);
        try {
            file.createNewFile();
            FileOutputStream os = new FileOutputStream(file);
            data.compress(Bitmap.CompressFormat.JPEG, 50, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbpath;
    }

    // Audio STUFF END


//Add your photo,TAKE A PICTURE,FROM YOUR GALLERY

    private void uploadAudioOnQuickBlox(final String path, final String msg, final String clicks) {
        Log.e(TAG, "uploadAudioOnQuickBlox.....Uploading--> " + path);
        File mfile = new File(path);
        QBContent.uploadFileTask(mfile, true, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBFileUploadTaskResult res = (QBFileUploadTaskResult) result;
                    audioFilePath = res.getFile().getPublicUrl().toString();
                    Log.e(TAG, "Uploaded  --> " + audioFilePath);
                    sendAudiotoPartner(audioFilePath, msg, clicks);
                    audioFilePath = null;
                }
            }
        });
    }

    private void sendAudiotoPartner(String filepath, String msg, String clicks) {
        Log.e(TAG, "sendAudiotoPartner.....msg--> " + msg);
        try {
            DefaultPacketExtension extension = new DefaultPacketExtension("extraParams", "jabber:client");
            if (!Utils.isEmptyString(msg)) {
                extension.setValue("message", msg);
            } else {
                extension.setValue("message", "");
            }
            if (clicks.equalsIgnoreCase("no")) {
                extension.setValue("clicks", "no");
            } else {
                extension.setValue("clicks", clicks);
            }
            extension.setValue("audioID", filepath);
            Message message = new Message();
            message.setType(Message.Type.chat); // 1-1 chat message
            message.setBody("" + msg);
            message.addExtension(extension);
            // chatObject.sendMessage(Integer.parseInt(qBId), message);

        } catch (Exception e) {
            // againLoginToQuickBlox();
            /*chatObject = null;
            authManager = ModelManager.getInstance().getAuthorizationManager();
            chatObject = authManager.getqBPrivateChat();
            chatObject.addChatMessageListener(this);*/
            e.printStackTrace();
        }
    }

    private void loginToQuickBlox() {

        try {
            // chatObject = null;
            authManager = ModelManager.getInstance().getAuthorizationManager();
            // chatObject = authManager.getqBPrivateChat();
            // chatObject.addChatMessageListener(this);
            //chatObject.notifyAll();
        } catch (Exception e) {
            // authManager = ModelManager.getInstance().getAuthorizationManager();
            //chatObject = authManager.getqBPrivateChat();
            //againLoginToQuickBlox();
            e.printStackTrace();
        }
    }

    private boolean isClicks() {
        if (seekValue != 0 && (-10 <= seekValue && seekValue <= 10)) {
            chatManager = ModelManager.getInstance().getChatManager();
            chatManager.setPartnerTotalClick(seekValue);
            Log.e(TAG, "myvalue---> " + seekValue);
            return true;
        } else {
            return false;
        }
    }

    private int grandClicksForReceiverEndInt(String clicksValue) {
        int changeClicks = 0;
        if (clicksValue.equalsIgnoreCase("+01")) {
            changeClicks = 1;
        } else if (clicksValue.equalsIgnoreCase("+02")) {
            changeClicks = 2;
        } else if (clicksValue.equalsIgnoreCase("+03")) {
            changeClicks = 3;
        } else if (clicksValue.equalsIgnoreCase("+04")) {
            changeClicks = 4;
        } else if (clicksValue.equalsIgnoreCase("+05")) {
            changeClicks = 5;
        } else if (clicksValue.equalsIgnoreCase("+06")) {
            changeClicks = 6;
        } else if (clicksValue.equalsIgnoreCase("+07")) {
            changeClicks = 7;
        } else if (clicksValue.equalsIgnoreCase("+08")) {
            changeClicks = 8;
        } else if (clicksValue.equalsIgnoreCase("+09")) {
            changeClicks = 9;
        } else if (clicksValue.equalsIgnoreCase("+10")) {
            changeClicks = 10;
        } else if (clicksValue.equalsIgnoreCase("-1")) {
            changeClicks = 1;
        } else if (clicksValue.equalsIgnoreCase("-2")) {
            changeClicks = -2;
        } else if (clicksValue.equalsIgnoreCase("-3")) {
            changeClicks = -3;
        } else if (clicksValue.equalsIgnoreCase("-4")) {
            changeClicks = -4;
        } else if (clicksValue.equalsIgnoreCase("-5")) {
            changeClicks = -5;
        } else if (clicksValue.equalsIgnoreCase("-6")) {
            changeClicks = -6;
        } else if (clicksValue.equalsIgnoreCase("-7")) {
            changeClicks = -7;
        } else if (clicksValue.equalsIgnoreCase("-8")) {
            changeClicks = -8;
        } else if (clicksValue.equalsIgnoreCase("-9")) {
            changeClicks = -9;
        } else if (clicksValue.equalsIgnoreCase("-10")) {
            changeClicks = -10;
        }
        return changeClicks;

    }



    private String clickForFlipper(int clicks) {

        String changeClicks = "";

        if (clicks == 1) {
            changeClicks = "+1";
        } else if (clicks == 2) {
            changeClicks = "+2";
        } else if (clicks == 3) {
            changeClicks = "+3";
        } else if (clicks == 4) {
            changeClicks = "+4";
        } else if (clicks == 5) {
            changeClicks = "+5";
        } else if (clicks == 6) {
            changeClicks = "+6";
        } else if (clicks == 7) {
            changeClicks = "+7";
        } else if (clicks == 8) {
            changeClicks = "+8";
        } else if (clicks == 9) {
            changeClicks = "+9";
        } else if (clicks == 10) {
            changeClicks = "+10";
        }
        return changeClicks;

    }

    public Bitmap ShrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        //this gives the size of the compressed image in kb
        long lengthbmp = imageInByte.length / 1024;

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream("/sdcard/mediaAppPhotos/compressed_new.jpg"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return bitmap;
    }

    private void createRecordForHistory(final ChatMessageBody obj) {
        HashMap<String, Object> fields = new HashMap<String, Object>();


        fields.put("message", obj.textMsg);
        fields.put("clicks", obj.clicks);
        fields.put("content", obj.content_url);
        fields.put("imageRatio", obj.imageRatio);
        fields.put("relationshipId", obj.relationshipId);
        fields.put("userId", obj.userId);
        fields.put("senderUserToken", obj.senderUserToken);
        fields.put("sentOn", obj.sentOn);// "142455987");//UTC
        fields.put("chatId", obj.chatId);
        fields.put("type", obj.chatType);
        fields.put("video_thumb", obj.video_thumb);

        ArrayList<String> cards = null;
        if (obj.card_id != null) {
            cards = new ArrayList<String>();
            cards.add(obj.card_owner);
            cards.add(obj.card_content);
            cards.add(String.valueOf(obj.is_CustomCard));
            cards.add(obj.card_DB_ID);
            cards.add(obj.card_heading);
            cards.add(obj.card_url);
            cards.add(obj.card_id);
            cards.add(obj.card_Played_Countered);
            cards.add(obj.card_originator);
        }
        fields.put("cards", cards);
        fields.put("location_coordinates", obj.location_coordinates);
        fields.put("sharedMessage", obj.sharedMessage);

        QBCustomObject qbCustomObject = new QBCustomObject();
        qbCustomObject.setClassName("chats");  // your Class name
        qbCustomObject.setFields(fields);

        QBCustomObjects.createObject(qbCustomObject, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBCustomObjectResult qbCustomObjectResult = (QBCustomObjectResult) result;
                    QBCustomObject qbCustomObject = qbCustomObjectResult.getCustomObject();
                    //    int chatIdtemp=qbCustomObject.getId();
                    //  obj.chatId=String.valueOf(chatIdtemp);
                    Log.e("New record: ", qbCustomObject.toString());
                } else {
                    Log.e("Errors", result.getErrors().toString());
                }
            }
        });
        // return String.valueOf(msgId);
    }

    private void updateValues(Intent intent) {
        //save previous chat here
        String temprId = intent.getExtras().getString("rId");
        if (!temprId.equalsIgnoreCase(rId)) { //if last chat window and new chat window is not same, means rid is not same

            Utils.launchBarDialog(ChatRecordView.this);
            new DBTask().execute();
            rId = temprId;

            addMenu(false);

            authManager = ModelManager.getInstance().getAuthorizationManager();
            qBId = intent.getExtras().getString("quickId");
            partnerPic = intent.getExtras().getString("partnerPic");
            partnerName = intent.getExtras().getString("partnerName");
            String[] splitted = partnerName.split("\\s+");

            partnerId = intent.getExtras().getString("partnerId");

   /* myClicks = intent.getExtras().getString("myClicks");
    userClicks = intent.getExtras().getString("userClicks");*/

            myTotalString = getIntent().getExtras().getString("myClicks");
            userTotalClicks = getIntent().getExtras().getString("userClicks");

            myClicks = Integer.parseInt(myTotalString);
            userClicks = Integer.parseInt(userTotalClicks);

            partnerPh = intent.getExtras().getString("partnerPh");

// get Chat record From server
            chatManager = ModelManager.getInstance().getChatManager();
            chatManager.chatListFromServer.clear();

            //chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(),"");


            profileName.setText("" + splitted[0]);
            try {
                Uri tempUri = authManager.getUserImageUri();
                if (tempUri != null) {
                    Bitmap imageBitmap;
                    imageBitmap = authManager.getUserbitmap();
                    if (imageBitmap != null)
                        mypix.setImageBitmap(imageBitmap);
                    else {
                        if (!Utils.isEmptyString(authManager.getGender())) {

                            if (authManager.getGender().equalsIgnoreCase("guy")) {
                                try {
                                    if (!authManager.getUserPic().equalsIgnoreCase("")) {
                                        Picasso.with(this)
                                                .load(authManager.getUserPic())
                                                .skipMemoryCache()

                                                .error(R.drawable.male_user)
                                                .into(mypix);
                                    } else {
                                        mypix.setImageResource(R.drawable.male_user);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mypix.setImageResource(R.drawable.male_user);
                                }
                            } else if (authManager.getGender().equalsIgnoreCase("girl")) {
                                try {
                                    if (!authManager.getUserPic().equalsIgnoreCase("")) {
                                        Picasso.with(this)
                                                .load(authManager.getUserPic())
                                                .skipMemoryCache()

                                                .error(R.drawable.female_user)
                                                .into(mypix);
                                    } else {
                                        mypix.setImageResource(R.drawable.female_user);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mypix.setImageResource(R.drawable.female_user);
                                }
                            }

                        } else {
                            mypix.setImageResource(R.drawable.male_user);
                        }
                    }

                } else {
                    if (authManager.getGender() != null) {

                        if (authManager.getGender().equalsIgnoreCase("guy")) {
                            try {
                                if (!authManager.getUserPic().equalsIgnoreCase("")) {
                                    Picasso.with(this)
                                            .load(authManager.getUserPic())
                                            .skipMemoryCache()

                                            .error(R.drawable.male_user)
                                            .into(mypix);
                                } else {
                                    mypix.setImageResource(R.drawable.male_user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mypix.setImageResource(R.drawable.male_user);
                            }
                        } else if (authManager.getGender().equalsIgnoreCase("girl")) {
                            try {
                                if (!Utils.isEmptyString(authManager.getGender())) {
                                    Picasso.with(this)
                                            .load(authManager.getUserPic())
                                            .skipMemoryCache()

                                            .error(R.drawable.female_user)
                                            .into(mypix);
                                } else {
                                    mypix.setImageResource(R.drawable.female_user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mypix.setImageResource(R.drawable.female_user);
                            }
                        }

                    } else {
                        mypix.setImageResource(R.drawable.male_user);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Picasso.with(ChatRecordView.this).load(partnerPic)

                    .error(R.drawable.male_user).into(partnerPix);


            //clear the message list always to initiate a new chat
            ModelManager.getInstance().getChatManager().chatMessageList.clear();

            //code to hide keyboard
            ((RelativeLayout) findViewById(R.id.rr_chat_layout)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(chatText.getWindowToken(), 0);

                }

            });
            setlist();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void fetchChatRecord(String relationshipId, String phone, String usertoken, String chatId) {


        chatManager = ModelManager.getInstance().getChatManager();
        //  ArrayList<ChatMessageBody>refreshivechatList=new ArrayList<ChatMessageBody>();
        AsyncHttpClient client = null;
        StringEntity se = null;
        // TODO Auto-generated method stub
        JSONObject userInputDetails = new JSONObject();
        try {
            userInputDetails.put("phone_no", phone);
            userInputDetails.put("user_token", usertoken);
            userInputDetails.put("relationship_id", relationshipId);
            if (!Utils.isEmptyString(chatId)) {
                userInputDetails.put("last_chat_id", chatId);
            }

            client = new AsyncHttpClient();
            se = new StringEntity(userInputDetails.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            Log.e(TAG, "userInputDetails-->" + userInputDetails);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        client.post(this, APIs.FETCHCHATRECORDS, se, "application/json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Throwable e,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, e, errorResponse);
                        if (errorResponse != null) {
                            Log.e("errorResponse", "->" + errorResponse);
                            EventBus.getDefault().post("FecthChat False");
                        } else {
                            EventBus.getDefault().post("FecthChat Network Error");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode,
                                          org.apache.http.Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        boolean state = false;
                        try {
                            Log.e(TAG, "response FecthChat ->" + response);
                            state = response.getBoolean("success");
                            if (state) {

                                chatManager.refreshivechatList.clear();
                                Utils.clickCustomLog(response.toString());
                                JSONArray list = response.getJSONArray("chats");
                                for (int i = 0; i < list.length(); i++) {
                                    ChatMessageBody temp = new ChatMessageBody();
                                    JSONObject data = list.getJSONObject(i);
                                    JSONObject chatObj = data.getJSONObject("Chat");

                                    // if (chatObj.has("receiverQB_id"))
                                    //   temp.re(chatObj.getString("receiverQB_id"));
                                    if (chatObj.has("sharedMessage"))
                                        temp.sharedMessage = chatObj.getString("sharedMessage");
                                    if (chatObj.has("video_thumb"))
                                        temp.video_thumb = chatObj.getString("video_thumb");
                                    if (chatObj.has("QB_id"))
                                        temp.senderQbId = chatObj.getString("QB_id");
                                    if (chatObj.has("senderUserToken"))
                                        temp.senderUserToken = chatObj.getString("senderUserToken");
                                    if (chatObj.has("type"))
                                        temp.chatType = Integer.parseInt(chatObj.getString("type"));
                                    if (chatObj.has("message"))
                                        temp.textMsg = chatObj.getString("message");
                                    if (chatObj.has("content"))
                                        temp.content_url = chatObj.getString("content");
                                    if (chatObj.has("relationshipId"))
                                        temp.relationshipId = chatObj.getString("relationshipId");
                                    if (chatObj.has("_id"))
                                        temp._id = chatObj.getString("_id");
                                    if (chatObj.has("userId"))
                                        temp.userId = chatObj.getString("userId");
                                    if (chatObj.has("location_coordinates"))
                                        temp.location_coordinates = chatObj.getString("location_coordinates");
                                    if (chatObj.has("clicks")) {
                                        temp.clicks = chatObj.getString("clicks");
                                        if (Utils.isEmptyString(temp.clicks))
                                            temp.clicks = "no";
                                    }
                                    if (chatObj.has("isDelivered"))
                                        temp.isDelivered = chatObj.getString("isDelivered");
                                    if (chatObj.has("imageRatio"))
                                        temp.imageRatio = chatObj.getString("imageRatio");
                                    if (chatObj.has("chatId"))
                                        temp.chatId = chatObj.getString("chatId");
                                    if (chatObj.has("cards"))
                                        // temp.cardchatObj.getString("cards"));
                                        if (chatObj.has("sentOn"))
                                            temp.sentOn = chatObj.getString("sentOn");
                                    chatManager.refreshivechatList.add(temp);
                                }

                                chatManager.chatMessageList.addAll(0, chatManager.refreshivechatList);

                                EventBus.getDefault().post("FecthChat True");
                            } else {
                                EventBus.getDefault().post("FecthChat False");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
        );

    }

    class DBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                dbHelper.deleteChat(rId);
                dbHelper.addChatList(chatManager.chatMessageList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    //function to update chatMessagelist for delivered chats
    private void updateChatDeliverStatusInList(String chatId){
        for(ChatMessageBody msg:chatManager.chatMessageList){
            if(msg.chatId.equalsIgnoreCase(chatId)){
                msg.isDelivered="true";
                break;
            }
        }
        if(adapter!=null)
            adapter.notifyDataSetChanged();
        else{
            adapter=new ChatRecordAdapter(this,R.layout.view_chat_demo,chatManager.chatMessageList);
            chatListView.setAdapter(adapter);
        }
    }


  //monika-update the content url for specific chatid and send msg to Qb and create history
    private void sendMediaMsgToQB(String fileUrl,String tempChatId,int chatType){
        // ArrayList<ChatMessageBody> tempChatList=chatManager.chatMessageList;
       //reset the path value set from On activityresult--monika
        path=null;
        audioFilePath=null;
        for(ChatMessageBody temp:chatManager.chatMessageList) {
            if (!(Utils.isEmptyString(temp.chatId))) {
                if (temp.chatId.equalsIgnoreCase(tempChatId)) {
                    //monika-need to upload two files in case of video
                    if(chatType==Constants.CHAT_TYPE_VIDEO_INITATING){
                        temp.video_thumb=fileUrl;
                        temp.chatType=Constants.CHAT_TYPE_VIDEO;
                        uploadImageFileOnQB(videofilePath,tempChatId,temp.chatType);
                    }else{
                        temp.content_url = fileUrl;
                        temp.isDelivered = Constants.MSG_SENT;
                        myQbChatService.sendMessage(temp);
                        //createRecordForHistory(temp);
                        adapter.notifyDataSetChanged();
                    }


                }
            }
        }
    }

}



 /* extension.setValue("audioID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("locationID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("location_coordinates", "43546,4646");*/
