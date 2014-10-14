package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.module.chat.listeners.ChatMessageListener;
import com.quickblox.module.chat.xmpp.QBPrivateChat;
import com.quickblox.module.content.QBContent;
import com.quickblox.module.content.result.QBFileUploadTaskResult;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.custom.result.QBCustomObjectResult;
import com.sourcefuse.clickinandroid.dbhelper.ClickinDbHelper;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.AudioUtil;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.utils.VideoUtil;
import com.sourcefuse.clickinandroid.view.adapter.ChatRecordAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.json.JSONObject;
import org.json.XML;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

public class ChatRecordView extends ClickInBaseView implements View.OnClickListener,
        TextWatcher, ChatMessageListener,ConnectionListener {
    private SeekBar mybar;
    private TextView pos, neg, profileName, typingtext, myTotalclicks, partnerTotalclicks;

    int myvalue = 10, min = -10;
    String chatString = "";
    int seekValue = 0;
    int maxValue = 20; // Double of range
    int initialProgresss = maxValue / 2;
    private static final String TAG = ChatRecordView.class.getSimpleName();
    private Button send, btnToCard;

    private QBPrivateChat chatObject;
    private String qBId, rId, partnerPic, partnerName, partnerId,myClicks,userClicks;
    private ChatManager chatManager;
    private AuthManager authManager;
    private EditText chatText;
    private ImageView mypix, partnerPix, menu, attachBtn, notificationIcon;
    //private ListView chatListView;
    private PullToRefreshListView chatListView;
    private ChatRecordAdapter adapter;
    public static ArrayList<ChatRecordBeen> chatData = new ArrayList<ChatRecordBeen>();
    public static ArrayList<ChatRecordBeen> databaseList = new ArrayList<ChatRecordBeen>();
    private Typeface typeface;



    private boolean showAttachmentView = true;
    private LinearLayout llAttachment;

    private ImageView atchPhoto, attachAudio, attachVideo, attachLocation;
    private Uri mImageCaptureUri = null;
    private String path, uploadedImgUrl, currentImagepath;

    private Dialog dialog;
    private Handler myHandler;
    private String audioFilePath;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            //Do Something
            com.sourcefuse.clickinandroid.utils.Log.e(TAG, "Start Recording");
            AudioUtil.startRecording();
            /*
             * Timer if required
			 */

        }
    };


    private ClickinDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_chat_layout);
        addMenu(false);
        loginToQuickBlox();
        typeface = Typeface.createFromAsset(ChatRecordView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
        send = (Button) findViewById(R.id.btn_send);
        chatListView = (ListView) findViewById(R.id.chat_list);

       // chatListView = (PullToRefreshListView) findViewById(R.id.chat_list);

        chatText = (EditText) findViewById(R.id.edit_chatBox);
        //  chatText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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


        profileName.setTypeface(typeface, typeface.BOLD);
        typingtext.setTypeface(typeface);
        menu.setOnClickListener(this);
        notificationIcon.setOnClickListener(this);
        pos.setText("-" + mybar.getMax());
        neg.setText("+" + mybar.getMax());

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
        rId = getIntent().getExtras().getString("rId");
        partnerId = getIntent().getExtras().getString("partnerId");


        myClicks = getIntent().getExtras().getString("myClicks");
        userClicks = getIntent().getExtras().getString("userClicks");
        partnerPh =  getIntent().getExtras().getString("partnerPh");




// get Chat record From server
        chatManager = ModelManager.getInstance().getChatManager();
        chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(),"");
// set ClicKs
        if(chatManager.getMyTotalClick()==0 && chatManager.getPartnerTotalClick()==0) {
            chatManager.setPartnerTotalClick(Integer.parseInt(myClicks));
            chatManager.setMyTotalClick(Integer.parseInt(userClicks));
            myTotalclicks.setText("" + userClicks);
            partnerTotalclicks.setText("" + myClicks);
        }else{
            myTotalclicks.setText("" + chatManager.getMyTotalClick());
            partnerTotalclicks.setText("" + chatManager.getPartnerTotalClick());
        }

        profileName.setText("" + partnerName);
        try {
            Picasso.with(ChatRecordView.this).load(authManager.getUserPic())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile).into(mypix);
            Picasso.with(ChatRecordView.this).load(partnerPic)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile).into(partnerPix);
        } catch (Exception e) {
        }


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
                    ((TextView) findViewById(R.id.tv_flipper_value)).setText(""+clickForFlipper(myvalue));
                    seekValue = myvalue;
                   // neg.setText("-10");
                }
                if (myvalue < 0) {
                    //pos.setText("+10");
                   // neg.setText("" + myvalue);
                    seekValue = myvalue;
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setVisibility(View.VISIBLE);
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setBackgroundResource(R.color.black_opacity);
                    ((TextView) findViewById(R.id.tv_flipper_value)).setText(""+myvalue);
                }
                if (myvalue == 0) {
                    seekValue = 0;
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setVisibility(View.GONE);
                    //pos.setText("+10");
                  //  neg.setText("-10");
                }

            }
        });

        chatText.addTextChangedListener(this);

        atchPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAttachView();
             //   Utils.imageDialog(ChatRecordView.this);
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
                int lastIndex = chatManager.chatListFromServer.size()-1;
                String lastChatId = chatManager.chatListFromServer.get(lastIndex).getChatId();

                chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(),lastChatId);
            }
        });



        chatData.clear();
        setlist();



    }






    public void imageDialog() {
        String[] addPhoto;
        addPhoto = new String[]{"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(ChatRecordView.this);
        dialog.setTitle("Select Option");

        dialog.setItems(addPhoto, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (id == 0) {

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,Constants.CAMERA_REQUEST);


                  /*  Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    try {
                        cameraIntent.putExtra("return-data", true);
                        startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);
                    } catch (ActivityNotFoundException e) {
                    }*/
                    dialog.dismiss();
                } else if (id == 1) {

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                    dialog.dismiss();
                }
            }
        });

        dialog.setNeutralButton("Cancel",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }
        );
        dialog.show();
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
                                //uploadImageOnQuickBlox(audioFilePath.toString(), "", "");
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


    private void sendWithExtension(String messageText, String clicks, String content, String relationshipId, String userId, String senderUserToken,
                                   String sentOn, String chatId, String type, String video_thumb, String fileID, String fileIDContent, String imageRatio, String cards,
                                   String locationCoordinates, String sharedMessage, String deliveredChatID) {
        //chat = QBChatService.getInstance().createChat();
        try {
            Log.e(TAG,"SEND CHAT DATA--->"+ "messageText ->"+messageText+ "\nclicks ->"+  clicks+ "\ncontent ->"+ content + "\nrelationshipId ->"+ relationshipId + "\nuserId ->"+ userId + "\nsenderUserToken ->"+ senderUserToken
                    + "\nsentOn ->"+ sentOn+ "\nchatId ->"+ chatId + "\ntype ->"+ type + "\nvideo_thumb ->"+ video_thumb + "\nfileID ->"+ fileID + "\nfileIDContent ->"+ fileIDContent + "\nimageRatio ->"+ imageRatio + "\ncards ->"+ cards
                    + "\nlocationCoordinates ->"+ locationCoordinates + "\nsharedMessage ->"+ sharedMessage + "\ndeliveredChatID ->"+ deliveredChatID);


            DefaultPacketExtension extension = new DefaultPacketExtension("extraParams", "jabber:client");


            if (!Utils.isEmptyString(messageText)) {
                extension.setValue("message", messageText);
            } else {
                extension.setValue("message", "");
            }
            extension.setValue("clicks", clicks);
            if (!Utils.isEmptyString(content)) {
                extension.setValue("content", content);
            }
            extension.setValue("relationshipId", relationshipId);//rId
            extension.setValue("userId", userId);// authManager.getUserId());
            extension.setValue("senderUserToken", senderUserToken);//authManager.getUsrToken());
            extension.setValue("sentOn", sentOn);// "142455987");//UTC
            extension.setValue("chatId", chatId);//Sender.partnerid.second
            extension.setValue("type", type);//"2");

            if (!Utils.isEmptyString(content)) {
                extension.setValue("video_thumb", video_thumb);
            }
            if (!Utils.isEmptyString(fileIDContent)) {
                extension.setValue(fileID, fileIDContent);
                extension.setValue("imageRatio", imageRatio);
            }
            if (!Utils.isEmptyString(cards)) {
                extension.setValue("cards", cards);
            }
            if (!Utils.isEmptyString(locationCoordinates)) {
                extension.setValue("location_coordinates", locationCoordinates);
            }
            if (!Utils.isEmptyString(sharedMessage)) {
                extension.setValue("sharedMessage", sharedMessage);
            }
            if (!Utils.isEmptyString(deliveredChatID)) {
                extension.setValue("deliveredChatID", deliveredChatID);
            }
            Log.e(TAG, "SENT Chat-->" + extension.toXML().toString());

            Message messageWithEx = new Message();
            messageWithEx.setType(Message.Type.chat);
            messageWithEx.setBody(messageText);// 1-1 chat message
            messageWithEx.addExtension(extension);
            chatObject.sendMessage(Integer.parseInt(qBId), messageWithEx);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                chatObject.removeChatMessageListener(this);
                chatObject.addChatMessageListener(this);
            }catch (Exception e1){}
            Log.e(TAG, "Exception----> " + e.toString());
        }

    }

    public void setlist() {

        try {
            dbHelper = new ClickinDbHelper(this);
            dbHelper.openDataBase();
            authManager = ModelManager.getInstance().getAuthorizationManager();
            databaseList.clear();
            chatData.clear();
            databaseList = dbHelper.getAllChat(authManager.getQBId(), qBId);
            Log.e(TAG, "This get From DATABASE-> " + databaseList.size());
            chatData.addAll(databaseList);
        } catch (Exception e) {
            Log.e(TAG, "Exception-> " + e.toString());
        }

       // adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, chatManager.chatListFromServer);
        adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo,chatData);
        chatListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:

                chatString = "" + chatText.getText().toString();
             //   if(!Utils.isEmptyString(chatString) || mImageCaptureUri != null && audioFilePath!=null){

                ChatRecordBeen addChat = new ChatRecordBeen();
                long sentOn = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
                String chatId = authManager.getQBId()+qBId + sentOn;
                Log.e(TAG, "chatId" + chatId);

                if (isClicks()) {
                    Log.e(TAG, "isClicks---> ");
                    try {
                        if (mImageCaptureUri == null && audioFilePath == null) {
                            Log.e(TAG, "NO Image- AND CLICKS--> ");

                            if (!Utils.isEmptyString(chatString)) {
                                addChat.setChatText("" + chatString);
                            } else {
                                addChat.setChatText("");
                                chatString = "";
                            }
                            addChat.setChatType("1");
                            addChat.setSenderQbId(authManager.getQBId());
                            addChat.setRecieverQbId(qBId);
                            addChat.setClicks(convertClicks(seekValue).trim());
                            sendWithExtension(convertClicks(seekValue) + chatString, convertClicks(seekValue), null, rId, authManager.getUserId(), authManager.getUsrToken(), ""+sentOn, chatId, "1", null, null, null, null, null, null, null, null);
                            createRecordOnQuickBlox(convertClicks(seekValue) + chatString, convertClicks(seekValue), null, rId, authManager.getUserId(), authManager.getUsrToken(), ""+sentOn, chatId, "1", null, null, null, null, null, null, null, null);
                            chatData.add(addChat);
                            adapter.notifyDataSetChanged();
                            chatText.setText("");

                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                            //attachBtn.setBackgroundResource(R.drawable.attach_icon);
                        } else if (mImageCaptureUri != null && audioFilePath == null) {
                            Log.e(TAG, "Image- AND CLICKS--> ");

                            if (!Utils.isEmptyString(chatString)) {
                                addChat.setChatText(convertClicks(seekValue) + chatString);
                                chatString = "" + convertClicks(seekValue) + chatString;
                            } else {
                                addChat.setChatText("" + convertClicks(seekValue));
                                chatString = "" + convertClicks(seekValue);
                            }
                            uploadImageOnQuickBlox(path, chatString, convertClicks(seekValue));
                            addChat.setChatType("2");
                            addChat.setClicks(convertClicks(seekValue));
                            addChat.setSenderQbId(authManager.getQBId());
                            addChat.setRecieverQbId(qBId);
                            addChat.setChatImageUrl(currentImagepath);
                            addChat.setChatText("" + chatString);
                            chatData.add(addChat);
                            adapter.notifyDataSetChanged();
                            mImageCaptureUri = null;
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                        } else if (mImageCaptureUri == null && audioFilePath != null) {
                            Log.e(TAG, "Audio- AND CLICKS--> ");

                            if (!Utils.isEmptyString(chatString)) {
                                addChat.setChatText(convertClicks(seekValue) + chatString);
                                chatString = "" + convertClicks(seekValue) + chatString;
                            } else {
                                addChat.setChatText("" + convertClicks(seekValue));
                                chatString = "" + convertClicks(seekValue);
                            }
                            uploadAudioOnQuickBlox(audioFilePath, chatString, convertClicks(seekValue));
                            addChat.setChatType("3");
                            addChat.setClicks(convertClicks(seekValue));
                            addChat.setSenderQbId(authManager.getQBId());
                            addChat.setRecieverQbId(qBId);
                            addChat.setChatImageUrl(audioFilePath);
                            addChat.setChatText("" + chatString);
                            chatData.add(addChat);
                            adapter.notifyDataSetChanged();
                            audioFilePath = null;
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                        }

                        chatManager = ModelManager.getInstance().getChatManager();
                        int myV = grandClicksForSenderEnd(convertClicks(seekValue));
                        chatManager.setPartnerTotalClick(myV);
                        partnerTotalclicks.setText("" + chatManager.getPartnerTotalClick());


                    } catch (Exception e) {
                    }

                } else {
                    Log.e(TAG, "NoImage- AND NoCLICKS--> AND Only TEXT");
                    if (mImageCaptureUri == null && audioFilePath == null) {
                        sendWithExtension(chatString, "no", "", rId, authManager.getUserId(), authManager.getUsrToken(), "" + sentOn, chatId, "1", null, null, null, null, null, null, null, null);
                        createRecordOnQuickBlox(chatString, "no", "", rId, authManager.getUserId(), authManager.getUsrToken(), "" + sentOn, chatId, "1", null, null, null, null, null, null, null, null);
                        addChat.setChatType("1");
                        addChat.setSenderQbId(authManager.getQBId());
                        addChat.setRecieverQbId(qBId);
                        addChat.setChatText("" + chatString);
                        addChat.setClicks("no");
                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();
                    } else if (mImageCaptureUri != null && audioFilePath == null) {
                        Log.e(TAG, "Image- AND No CLICKS--> ");
                        if (Utils.isEmptyString(chatString) && seekValue == 0) {
                            Log.e(TAG, "Only Image- AND No CLICKS--> ");
                            uploadImageOnQuickBlox(path, chatString, "no");
                            addChat.setChatType("2");
                            addChat.setClicks("no");
                            addChat.setSenderQbId(authManager.getQBId());
                            addChat.setRecieverQbId(qBId);
                            addChat.setChatImageUrl(currentImagepath);
                            addChat.setChatText("" + chatString);
                            chatData.add(addChat);
                            adapter.notifyDataSetChanged();
                            mImageCaptureUri = null;
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                        } else if (!Utils.isEmptyString(chatString) && seekValue == 0) {
                            Log.e(TAG, "Image- AND Text &  No CLICKS--> ");
                            uploadImageOnQuickBlox(path, chatString, "no");
                            addChat.setChatType("2");
                            addChat.setClicks("no");
                            addChat.setSenderQbId(authManager.getQBId());
                            addChat.setRecieverQbId(qBId);
                            addChat.setChatImageUrl(currentImagepath);
                            addChat.setChatText("" + chatString);
                            chatData.add(addChat);
                            adapter.notifyDataSetChanged();
                            mImageCaptureUri = null;
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                        }
                    } else if (audioFilePath != null && mImageCaptureUri == null) {
                        Log.e(TAG, "Audio- AND CLICKS--> ");

                        if (Utils.isEmptyString(chatString) && seekValue == 0) {
                            Log.e(TAG, "Only Image- AND No CLICKS--> ");
                            uploadAudioOnQuickBlox(audioFilePath, chatString, "no");
                            addChat.setChatType("3");
                            addChat.setClicks("no");
                            addChat.setSenderQbId(authManager.getQBId());
                            addChat.setRecieverQbId(qBId);
                            addChat.setChatImageUrl(uploadedImgUrl);
                            addChat.setChatText("" + chatString);
                            chatData.add(addChat);
                            adapter.notifyDataSetChanged();
                            mImageCaptureUri = null;
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                        } else if (!Utils.isEmptyString(chatString) && seekValue == 0) {
                            Log.e(TAG, "Audio- AND Text &  No CLICKS--> ");
                            uploadImageOnQuickBlox(audioFilePath, chatString, "no");
                            addChat.setChatType("3");
                            addChat.setClicks("no");
                            addChat.setSenderQbId(authManager.getQBId());
                            addChat.setRecieverQbId(qBId);
                            addChat.setChatImageUrl(uploadedImgUrl);
                            addChat.setChatText("" + chatString);
                            chatData.add(addChat);
                            adapter.notifyDataSetChanged();
                            mImageCaptureUri = null;
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                        }
                    }
                }

                mybar.setProgress(10);
                int seekValue = 0;
                chatText.setText("");
                mImageCaptureUri = null;
                chatString = "";

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
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
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
                viewProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                viewProfile.putExtra("FromOwnProfile", true);
                viewProfile.putExtra("phNumber",partnerPh);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                startActivity(viewProfile);
                break;


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
        DefaultPacketExtension extension = new DefaultPacketExtension("extraParams", "jabber:client");
        if (s.length() > 0) {
            extension.setValue("isComposing", "YES");
        } else {
            extension.setValue("isComposing", "NO");
        }
        Message message = new Message();
        message.setType(Message.Type.chat); // 1-1 chat message
        message.addExtension(extension);
        try {
            chatObject.sendMessage(Integer.parseInt(qBId), message);
        } catch (Exception e) {

           try {
            chatObject.removeChatMessageListener(this);
            chatObject.addChatMessageListener(this);
}catch (Exception e1){}
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

        //QBChatService.getInstance().logout();
        super.onDestroy();

        try {
            chatObject.removeChatMessageListener(this);
        } catch (Exception e) {
        }

        try {
            dbHelper.deleteChat(authManager.getQBId(), qBId);
            dbHelper.addChatList(chatData);
        } catch (SQLException e) {
            e.printStackTrace();
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
        chatObject = authManager.getqBPrivateChat();
        chatObject.removeChatMessageListener(this);
        chatObject.addChatMessageListener(this);
        } catch (Exception e) {
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        try {
            chatObject.removeChatMessageListener(this);
        } catch (Exception e) {
        }

        /*try {
            chatObject.removeChatMessageListener(this);
        } catch (Exception e) {

        }*/

    }


    public void onEventMainThread(String message) {
        android.util.Log.d(TAG, "onEventMainThread->" + message);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("FecthChat True")) {
           // chatListView.onRefreshComplete();
            setlist();
            android.util.Log.d("1", "message->" + message);
        } else if (message.equalsIgnoreCase("FecthChat False")) {
            //chatListView.onRefreshComplete();
            Utils.dismissBarDialog();
            android.util.Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("FecthChat NetworkchatType Error")) {
            Utils.showAlert(ChatRecordView.this, AlertMessage.connectionError);
            android.util.Log.d("3", "message->" + message);
        }
    }

    @Override
    public void processMessage(Message message) {
        //Set typin Status....
        String chatType = "0";
        String fileID = null;
        String body = null;
        String clicks = null;
        String toUserId = null;
        String fromUserId = null;


        try {
            PacketExtension extension = message.getExtension("extraParams", "jabber:client");
            if (extension != null) {
                try {
                    String value = ((DefaultPacketExtension) extension).getValue("isComposing");
                    if (value.equals("YES")) {
                        typingtext.setVisibility(View.VISIBLE);
                        chatType = "0";
                        typingtext.setText("Typing...");
                    } else if (value.equals("NO")) {
                        chatType = "0";
                        typingtext.setVisibility(View.GONE);
                    }
                } catch (Exception f) {
                }
            }

// Get message in XML and convert in JSON OBJECT


            try {
                ChatRecordBeen addChat = new ChatRecordBeen();
                String messageBody = message.getBody();
                JSONObject xmlJSONObj = XML.toJSONObject(message.toXML().toString());
                Log.e(TAG, "--- xmlJSONObj--->" + xmlJSONObj);


                if (!Utils.isEmptyString(xmlJSONObj.getJSONObject("message").getString("body")) && xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("clicks") && !xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("fileID") && !xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("audioID")) {
                    Log.e(TAG, "Chattype--1");
                    body = xmlJSONObj.getJSONObject("message").getString("body");
                    toUserId = xmlJSONObj.getJSONObject("message").getString("to");
                    toUserId = (toUserId.split("-.")[0]).trim();
                    fromUserId = xmlJSONObj.getJSONObject("message").getString("from");
                    fromUserId = (fromUserId.split("-.")[0]).trim();

                    clicks = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("clicks");
                    chatType = "1";
                }
                if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("fileID")) {
                    fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("fileID");
                    chatType = "2";

                    body = xmlJSONObj.getJSONObject("message").getString("body");
                    toUserId = xmlJSONObj.getJSONObject("message").getString("to");
                    toUserId = (toUserId.split("-.")[0]).trim();
                    fromUserId = xmlJSONObj.getJSONObject("message").getString("from");
                    fromUserId = (fromUserId.split("-.")[0]).trim();
                    clicks = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("clicks");
                    fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("fileID");

                    Log.e(TAG, "Chattype--2");
                }
                if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("audioID")) {
                    fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("audioID");
                    chatType = "3";

                    body = xmlJSONObj.getJSONObject("message").getString("body");
                    toUserId = xmlJSONObj.getJSONObject("message").getString("to");
                    toUserId = (toUserId.split("-.")[0]).trim();
                    fromUserId = xmlJSONObj.getJSONObject("message").getString("from");
                    fromUserId = (fromUserId.split("-.")[0]).trim();
                    clicks = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("clicks");
                    fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("audioID");

                    Log.e(TAG, "Chattype--3");
                }
                if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("locationID")) {
                    fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("locationID");
                    chatType = "2";
                    Log.e(TAG, "Chattype--4");
                }
                if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("videoID")) {
                    fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("videoID");
                    chatType = "4";
                    Log.e(TAG, "Chattype--5");
                }
                if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_url")) {

                    fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_url");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_clicks"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_clicks");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_owner"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_owner");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_content"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_content");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("is_CustomCard"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("is_CustomCard");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_DB_ID"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_DB_ID");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_heading"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_heading");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_Accepted_Rejected"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_Accepted_Rejected");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_id"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_id");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_Played_Countered"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_Played_Countered");

                    if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_originator"))
                        xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_originator");

                    chatType = "5";
                    Log.e(TAG, "Chattype--1");
                }

                Log.e(TAG, "--- xmlJSONObj--->" + "body--> " + body + " , clicks--> " + clicks + " , fileID--> " + fileID);


                if (chatType.equalsIgnoreCase("1")) {

                    if (clicks.equalsIgnoreCase("no")) {
                        addChat.setChatType(chatType);
                        addChat.setSenderQbId(fromUserId);
                        addChat.setRecieverQbId(toUserId);
                        addChat.setChatText(body);
                        addChat.setClicks(clicks);

                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();

                    } else {
                        addChat.setChatType(chatType);
                        addChat.setSenderQbId(fromUserId);
                        addChat.setRecieverQbId(toUserId);
                        addChat.setChatText(body);
                        if (body.equalsIgnoreCase(clicks)) {
                            addChat.setClicks(body);
                        } else {
                            addChat.setClicks(body.substring(0, 4));
                        }
                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();
                    }
                } else if (chatType.equalsIgnoreCase("2")) {

                    if (clicks.equalsIgnoreCase("no") && Utils.isEmptyString(body)) {
                        Log.e(TAG, "Chattype 2 --2");
                        addChat.setChatType(chatType);
                        addChat.setChatImageUrl(fileID);
                        addChat.setSenderQbId(fromUserId);
                        addChat.setRecieverQbId(toUserId);
                        addChat.setChatText(body);
                        addChat.setClicks(clicks);
                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();
                    } else if (clicks.equalsIgnoreCase("no") && !Utils.isEmptyString(body)) {
                        addChat.setChatType(chatType);
                        addChat.setChatImageUrl(fileID);
                        addChat.setSenderQbId(fromUserId);
                        addChat.setRecieverQbId(toUserId);
                        addChat.setClicks(clicks);
                        addChat.setChatText(body);
                        /*if( body.equalsIgnoreCase(clicks)){
                            addChat.setClicks(body);
                        }else{
                            addChat.setClicks(body.substring(0, 4));
                        }*/
                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();
                    } else if (!clicks.equalsIgnoreCase("no") && !Utils.isEmptyString(body)) {
                        addChat.setChatType(chatType);
                        addChat.setChatImageUrl(fileID);
                        addChat.setSenderQbId(fromUserId);
                        addChat.setRecieverQbId(toUserId);
                        addChat.setClicks(clicks);
                        addChat.setChatText(body);
                        /*if( body.equalsIgnoreCase(clicks)){
                            addChat.setClicks(body);
                        }else{
                            addChat.setClicks(body.substring(0, 4));
                        }*/
                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();
                    }

                } else if (chatType.equalsIgnoreCase("3")) {

                    if (clicks.equalsIgnoreCase("no") && Utils.isEmptyString(body)) {
                        Log.e(TAG, "Chattype 3 Only Audio File");

                        addChat.setChatType(chatType);
                        addChat.setChatImageUrl(fileID);
                        addChat.setSenderQbId(fromUserId);
                        addChat.setRecieverQbId(toUserId);
                        addChat.setClicks(clicks);
                        addChat.setChatText(body);

                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();

                    } else if (clicks.equalsIgnoreCase("no") && !Utils.isEmptyString(body)) {

                        addChat.setChatType(chatType);
                        addChat.setChatImageUrl(fileID);
                        addChat.setSenderQbId(fromUserId);
                        addChat.setRecieverQbId(toUserId);
                        addChat.setClicks(clicks);
                        addChat.setChatText(body);

                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();

                    } else if (!clicks.equalsIgnoreCase("no") && !Utils.isEmptyString(body)) {

                        addChat.setChatType(chatType);
                        addChat.setChatImageUrl(fileID);
                        addChat.setSenderQbId(fromUserId);
                        addChat.setRecieverQbId(toUserId);
                        addChat.setClicks(clicks);
                        addChat.setChatText(body);

                        chatData.add(addChat);
                        adapter.notifyDataSetChanged();

                    }

                }


               /* chatManager = ModelManager.getInstance().getChatManager();
                int myV = grandClicksForSenderEnd(convertClicks(seekValue));
                chatManager.setMyTotalClick(myV);
                myTotalclicks.setText("" + chatManager.getMyTotalClick());*/


                if (!Utils.isEmptyString(clicks)) {
                    chatManager = ModelManager.getInstance().getChatManager();
                    int v = grandClicksForReceiverEnd(clicks.trim());
                    chatManager.setMyTotalClick(v);
                    myTotalclicks.setText("" + chatManager.getMyTotalClick());
                }

                fileID = null;
                body = null;
                clicks = null;
                toUserId = null;
                fromUserId = null;


            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    @Override
    public boolean accept(Message.Type messageType) {
        switch (messageType) {
            case chat:
                Log.e(TAG, "Gotit");
                return true;
            default:
                Log.e(TAG, "M Not get");
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case Constants.CAMERA_REQUEST:
                        mImageCaptureUri = data.getData();
                        Log.e(TAG, "CAMERA_REQUEST" + "--> " + mImageCaptureUri);
                        try {
                            mImageCaptureUri = Utils.decodeUri(ChatRecordView.this, mImageCaptureUri, 100);
                            // Utils.decodeUri()
                            path = Utils.getRealPathFromURI(mImageCaptureUri, ChatRecordView.this);
                            currentImagepath = mImageCaptureUri.toString();

                            Picasso.with(ChatRecordView.this).load(mImageCaptureUri.toString())
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.default_profile).into(attachBtn);
                        } catch (Exception ex) {
                            Log.e("Exception", "Exception-->" + ex);
                        }


                        break;
                    case Constants.SELECT_PICTURE:
                        Log.e(TAG, "SELECT_PICTURE" + "--> " + mImageCaptureUri);
                        mImageCaptureUri = data.getData();
                        try {

                            mImageCaptureUri = Utils.decodeUri(ChatRecordView.this, mImageCaptureUri, 100);
                            path = Utils.getRealPathFromURI(mImageCaptureUri, ChatRecordView.this);
                            currentImagepath = mImageCaptureUri.toString();

                            Picasso.with(ChatRecordView.this).load(mImageCaptureUri.toString())
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.default_profile).into(attachBtn);
                        } catch (Exception ex) {
                            Log.e("Exception", "Exception-->" + ex);
                        }


                        break;
                    case VideoUtil.REQUEST_VIDEO_CAPTURED:
                        Log.e(TAG, "Video saved to:" + VideoUtil.videofilePath);
                        if (!Utils.isEmptyString(VideoUtil.videofilePath)) {
                            uploadImageOnQuickBlox(VideoUtil.videofilePath, "", "");
                        }
                        break;
                    case VideoUtil.REQUEST_VIDEO_CAPTURED_FROM_GALLERY:
                        mImageCaptureUri = data.getData();
                        path = Utils.getRealPathFromURI(mImageCaptureUri, ChatRecordView.this);
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


    // IMAGE STUFF start
    private void uploadImageOnQuickBlox(final String path, final String msg, final String clicks) {
        Log.e(TAG, "uploadImageOnQuickBlox.....Uploading--> " + path);
        File mfile = new File(path);
        QBContent.uploadFileTask(mfile, true, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBFileUploadTaskResult res = (QBFileUploadTaskResult) result;
                    uploadedImgUrl = res.getFile().getPublicUrl().toString();
                    Log.e(TAG, "Uploaded  --> " + uploadedImgUrl);

                    // if (sendImage) {
                    sendImagetoPartner(uploadedImgUrl, msg, clicks);
                    //sendImage = false;
                    //}

                }
            }
        });
    }

    private void sendImagetoPartner(String filepath, String msg, String clicks) {
        Log.e(TAG, "uploadImageOnQuickBlox.....msg--> " + msg);
        try {
            DefaultPacketExtension extension = new DefaultPacketExtension("extraParams", "jabber:client");
            if (!Utils.isEmptyString(msg)) {
                extension.setValue("message", msg);
            } else {
                extension.setValue("message", "");
            }
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
            chatObject.sendMessage(Integer.parseInt(qBId), message);
        } catch (Exception e) {
try {
            chatObject.removeChatMessageListener(this);
            chatObject.addChatMessageListener(this);
}catch (Exception e1){}
          /*  chatObject = null;
            authManager = ModelManager.getInstance().getAuthorizationManager();
            chatObject = authManager.getqBPrivateChat();
            chatObject.addChatMessageListener(this);*/
            e.printStackTrace();
        }
    }
// IMAGE STUFF END

    // Audio STUFF STArt

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
                    sendAudiotoPartner(uploadedImgUrl, msg, clicks);
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
            chatObject.sendMessage(Integer.parseInt(qBId), message);

        } catch (Exception e) {
            /*chatObject = null;
            authManager = ModelManager.getInstance().getAuthorizationManager();
            chatObject = authManager.getqBPrivateChat();
            chatObject.addChatMessageListener(this);*/
            e.printStackTrace();
        }
    }

    // Audio STUFF END


//Add your photo,TAKE A PICTURE,FROM YOUR GALLERY

    private void loginToQuickBlox() {

        try {
            chatObject = null;
            authManager = ModelManager.getInstance().getAuthorizationManager();
            chatObject = authManager.getqBPrivateChat();
            chatObject.addChatMessageListener(this);
            //chatObject.notifyAll();
        } catch (Exception e) {
            // authManager = ModelManager.getInstance().getAuthorizationManager();
            //chatObject = authManager.getqBPrivateChat();
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


    private String convertClicks(int clicks) {

        String changeClicks = "";

        if (clicks == 1) {
            changeClicks = "+01       ";
        } else if (clicks == 2) {
            changeClicks = "+02       ";
        } else if (clicks == 3) {
            changeClicks = "+03       ";
        } else if (clicks == 4) {
            changeClicks = "+04       ";
        } else if (clicks == 5) {
            changeClicks = "+05       ";
        } else if (clicks == 6) {
            changeClicks = "+06       ";
        } else if (clicks == 7) {
            changeClicks = "+07       ";
        } else if (clicks == 8) {
            changeClicks = "+08       ";
        } else if (clicks == 9) {
            changeClicks = "+09       ";
        } else if (clicks == 10) {
            changeClicks = "+10       ";
        } else if (clicks == -1) {
            changeClicks = "-01       ";
        } else if (clicks == -2) {
            changeClicks = "-02       ";
        } else if (clicks == -3) {
            changeClicks = "-03       ";
        } else if (clicks == -4) {
            changeClicks = "-04       ";
        } else if (clicks == -5) {
            changeClicks = "-05       ";
        } else if (clicks == -6) {
            changeClicks = "-06       ";
        } else if (clicks == -7) {
            changeClicks = "-07       ";
        } else if (clicks == -8) {
            changeClicks = "-08       ";
        } else if (clicks == -9) {
            changeClicks = "-09       ";
        } else if (clicks == -10) {
            changeClicks = "-10       ";
        } else if (clicks == 0) {
            changeClicks = "";
        }
        return changeClicks;

    }


    private int grandClicksForReceiverEnd(String clicksValue) {
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

    private int grandClicksForSenderEnd(String clicksValue) {
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
        } else if (clicksValue.equalsIgnoreCase("-01")) {
            changeClicks = 1;
        } else if (clicksValue.equalsIgnoreCase("-02")) {
            changeClicks = -2;
        } else if (clicksValue.equalsIgnoreCase("-03")) {
            changeClicks = -3;
        } else if (clicksValue.equalsIgnoreCase("-04")) {
            changeClicks = -4;
        } else if (clicksValue.equalsIgnoreCase("-05")) {
            changeClicks = -5;
        } else if (clicksValue.equalsIgnoreCase("-06")) {
            changeClicks = -6;
        } else if (clicksValue.equalsIgnoreCase("-07")) {
            changeClicks = -7;
        } else if (clicksValue.equalsIgnoreCase("-08")) {
            changeClicks = -8;
        } else if (clicksValue.equalsIgnoreCase("-09")) {
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



    public Bitmap ShrinkBitmap(String file, int width, int height){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
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


    @Override
    public void connectionClosed() {
        Log.e(TAG, "connection closed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
       Log.e(TAG, "connection closed on error. It will be established soon");
    }

    @Override
    public void reconnectingIn(int i) {
        Log.e(TAG, "reconnectingIn");

        new SignInView().loginToQuickBlox();
    }

    @Override
    public void reconnectionSuccessful() {
        Log.e(TAG, "reconnectionSuccessful");
    }

    @Override
    public void reconnectionFailed(Exception e) {
       Log.e(TAG, "reconnectionFailed");
    }





    private void  createRecordOnQuickBlox(String messageText, String clicks, String content, String relationshipId, String userId, String senderUserToken,
                                          String sentOn, String chatId, String type, String video_thumb, String fileID, String fileIDContent, String imageRatio, String cards,
                                          String locationCoordinates, String sharedMessage, String deliveredChatID) {
    HashMap<String, Object> fields = new HashMap<String, Object>();

        if (!Utils.isEmptyString(messageText)) {
            fields.put("message", messageText);
        } else {
            fields.put("message", "");
        }
        fields.put("clicks", clicks);
        if (!Utils.isEmptyString(content)) {
            fields.put("content", content);
        }
        fields.put("relationshipId", relationshipId);
        fields.put("userId", userId);
        fields.put("senderUserToken", senderUserToken);
        fields.put("sentOn", sentOn);// "142455987");//UTC
        fields.put("chatId", chatId);
        fields.put("type", type);

        if (!Utils.isEmptyString(content)) {
            fields.put("video_thumb", video_thumb);
        }
        if (!Utils.isEmptyString(fileIDContent)) {
            fields.put(fileID, fileIDContent);
            fields.put("imageRatio", imageRatio);
        }
        if (!Utils.isEmptyString(cards)) {
            fields.put("cards", cards);
        }
        if (!Utils.isEmptyString(locationCoordinates)) {
            fields.put("location_coordinates", locationCoordinates);
        }
        if (!Utils.isEmptyString(sharedMessage)) {
            fields.put("sharedMessage", sharedMessage);
        }
        if (!Utils.isEmptyString(deliveredChatID)) {
            fields.put("deliveredChatID", deliveredChatID);
        }

    QBCustomObject qbCustomObject = new QBCustomObject();
    qbCustomObject.setClassName("chats");  // your Class name
    qbCustomObject.setFields(fields);
    QBCustomObjects.createObject(qbCustomObject, new QBCallbackImpl() {
        @Override
        public void onComplete(Result result) {
            if (result.isSuccess()) {
                QBCustomObjectResult qbCustomObjectResult = (QBCustomObjectResult) result;
                QBCustomObject qbCustomObject = qbCustomObjectResult.getCustomObject();
                Log.e("New record: ", qbCustomObject.toString());
            } else {
                Log.e("Errors", result.getErrors().toString());
            }
        }
    });

}
}

 /* extension.setValue("audioID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("locationID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("location_coordinates", "43546,4646");*/