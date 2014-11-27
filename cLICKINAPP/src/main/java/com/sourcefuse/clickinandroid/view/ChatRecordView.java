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
    private String qBId, rId, partnerPic, partnerName, partnerId, partnerPh, myTotalString, userTotalClicks;
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
                    } else if(mImageCaptureUri != null) {//if any media is attached
                       // if (mImageCaptureUri != null) {//if image is attached
                            CHAT_TYPE = Constants.CHAT_TYPE_IMAGE;

                                sendMsgToQB(mImageCaptureUri.toString());

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

                mImageCaptureUri=null;
                audioFilePath = null;
                videofilePath = null;
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


    private boolean storeImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/myAppDir/myImages/";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            String filePath = sdIconStorageDir.toString() + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }

        return true;
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
        // createRecordForHistory(obj);

    }


    //monika-fucntion to upload file on Qb
    private void uploadImageFileOnQB(String path, String msgId) {
        File mfile = new File(path);
        final String chatId=msgId;
        Boolean fileIsPublic = true;
        QBContent.uploadFileTask(mfile, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>() {
            @Override
            public void onSuccess(QBFile file, Bundle params) {

                String fileUrl= file.getPublicUrl().toString();
                sendMediaMsgToQB(fileUrl, chatId);

            }

            @Override
            public void onError(List<String> errors) {
                // error
            }
        });

    }

    //monika- common function to create msg in case of media attachment
    private void sendMsgToQB(String path) {
        ChatMessageBody temp = new ChatMessageBody();
        String chatString = chatText.getText().toString();
        switch (CHAT_TYPE) {
            case Constants.CHAT_TYPE_IMAGE:
                temp.imageRatio = "1";
                temp.content_url = path;
                temp.isDelivered=Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_IMAGE;
                break;
            case Constants.CHAT_TYPE_AUDIO:
                temp.content_url = path;
                temp.isDelivered=Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_AUDIO;
                break;
            case Constants.CHAT_TYPE_VIDEO:
                temp.content_url = path;
                temp.video_thumb = path;
                temp.isDelivered=Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_VIDEO;
                break;
            case Constants.CHAT_TYPE_LOCATION:
                temp.content_url = path;
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

        //setValueForHistory(temp);
        ShowValueinChat(temp);
        chatText.setText("");
        seekValue = 0;
        mybar.setProgress(10);
        mImageCaptureUri=null;

        attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.r_footer_icon));
        uploadImageFileOnQB(path,temp.chatId);



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
            temp.card_Played_Countered = intent.getExtras().getString("played_Countered");
            temp.card_originator = intent.getExtras().getString("card_originator");

            temp.card_owner = authManager.getQBId();
            temp.chatType = Constants.CHAT_TYPE_CARD;
            temp.partnerQbId = qBId;
            temp.textMsg = "";

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

        try {
            if (s.length() > 0) {
                myQbChatService.sendTypeNotification("YES", qBId);

            } else {
                myQbChatService.sendTypeNotification("NO", qBId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

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
            updateChatDeliverStatusInList(chatId);

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
                            path = mImageCaptureUri.toString();
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
                            mImageCaptureUri = Utils.decodeUri(ChatRecordView.this, data.getData(), 550);
                            path = mImageCaptureUri.toString();
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
                            attachBtn.setImageBitmap(bMap);
                            //uploadImageOnQuickBlox(VideoUtil.videofilePath, "", "");
                        }
                        break;
                    case VideoUtil.REQUEST_VIDEO_CAPTURED_FROM_GALLERY:
                        mImageCaptureUri = data.getData();
                        path = Utils.getRealPathFromURI(mImageCaptureUri, ChatRecordView.this);
                        videofilePath = path;
                        //uploadImageOnQuickBlox(path);
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
    private void sendMediaMsgToQB(String fileUrl,String tempChatId){
        // ArrayList<ChatMessageBody> tempChatList=chatManager.chatMessageList;
        path=null;
        for(ChatMessageBody temp:chatManager.chatMessageList) {
            if (!(Utils.isEmptyString(temp.chatId))) {
                if (temp.chatId.equalsIgnoreCase(tempChatId)) {
                    temp.content_url = fileUrl;
                    temp.isDelivered = Constants.MSG_SENT;
                    myQbChatService.sendMessage(temp);

                    adapter.notifyDataSetChanged();

                }
            }
        }
    }

}



 /* extension.setValue("audioID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("locationID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("location_coordinates", "43546,4646");*/

