package com.sourcefuse.clickinandroid.view;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
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
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.chat.QBChatService;

import com.quickblox.module.content.QBContent;
import com.quickblox.module.content.result.QBFileUploadTaskResult;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.custom.result.QBCustomObjectResult;
import com.quickblox.module.users.model.QBUser;
import com.sourcefuse.clickinandroid.dbhelper.ClickinDbHelper;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
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
import android.content.ServiceConnection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

public class ChatRecordView extends ClickInBaseView implements View.OnClickListener,
                                                                  TextWatcher {
      private SeekBar mybar;
      private TextView pos, neg, profileName, typingtext, myTotalclicks, partnerTotalclicks;
      int myvalue = 10, min = -10;
      String chatString = "";
      int seekValue = 0;
      int maxValue = 20; // Double of range
      int initialProgresss = maxValue / 2;
      private static final String TAG = ChatRecordView.class.getSimpleName();
      private Button send, btnToCard;
      public static final int MEDIA_TYPE_IMAGE = 1;
      private static final String IMAGE_DIRECTORY_NAME = "Clickin Application";

      //private QBPrivateChat chatObject;

      private int relationListIndex, myClicks, userClicks;
      private String qBId, rId, partnerPic, partnerName, partnerId, partnerPh, myTotalString, userTotalClicks;

      private ChatManager chatManager;
      private AuthManager authManager;
      private RelationManager relationManager;
      private EditText chatText;
      private ImageView mypix, partnerPix, menu, attachBtn, notificationIcon;
      private PullToRefreshListView chatListView;
      private ChatRecordAdapter adapter=null;
      public static ArrayList<ChatRecordBeen> chatData = new ArrayList<ChatRecordBeen>();
      public static ArrayList<ChatRecordBeen> databaseList = new ArrayList<ChatRecordBeen>();
      //private Typeface typeface;
      private boolean isHistroy = true;

      private long sentOn;
      private String chatId;
      private boolean emptyDb = true;
      String firstname;
      String[] splitted;

      private boolean showAttachmentView = true;
      private LinearLayout llAttachment;

      private ImageView atchPhoto, attachAudio, attachVideo, attachLocation;
      private Uri mImageCaptureUri = null;
      private String path, uploadedImgUrl, currentImagepath;
      private String videofilePath = null;
 	  public MyQbChatService myQbChatService;
      private Dialog dialog;
      private Handler myHandler;
      private String audioFilePath;

      private int CHAT_TYPE;
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

    private boolean mIsBound;

      private ClickinDbHelper dbHelper;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.view_chat_layout);
  		Intent i=new Intent(this,MyQbChatService.class);
        bindService(i,mConnection,Context.BIND_AUTO_CREATE);
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
                        if (authManager.getGender()!=null) {

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


            ModelManager.getInstance().getChatManager().chatMessageList.clear();
            setlist();


      }

      @Override
      public void onBackPressed() {
            super.onBackPressed();
            finish();
            overridePendingTransition(0, R.anim.top_out);
      }

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


      /*private void sendWithExtension(String messageText, String clicks) {
            //chat = QBChatService.getInstance().createChat();
            try {
           *//* Log.e(TAG,"SEND CHAT DATA--->"+ "messageText ->"+messageText+ "\nclicks ->"+  clicks+ "\ncontent ->"+ content + "\nrelationshipId ->"+ relationshipId + "\nuserId ->"+ userId + "\nsenderUserToken ->"+ senderUserToken
                    + "\nsentOn ->"+ sentOn+ "\nchatId ->"+ chatId + "\ntype ->"+ type + "\nvideo_thumb ->"+ video_thumb + "\nfileID ->"+ fileID + "\nfileIDContent ->"+ fileIDContent + "\nimageRatio ->"+ imageRatio + "\ncards ->"+ cards
                    + "\nlocationCoordinates ->"+ locationCoordinates + "\nsharedMessage ->"+ sharedMessage + "\ndeliveredChatID ->"+ deliveredChatID);
*//*
                  DefaultPacketExtension extension = new DefaultPacketExtension("extraParams", "jabber:client");
                  extension.setValue("clicks", clicks);

                  Log.e(TAG, "SENT Chat-->" + extension.toXML().toString());

                  Message messageWithEx = new Message();
                  messageWithEx.setType(Message.Type.chat);
                  messageWithEx.setBody(messageText);// 1-1 chat message
                  messageWithEx.addExtension(extension);
                 // chatObject.sendMessage(Integer.parseInt(qBId), messageWithEx);
            } catch (Exception e) {
                  e.printStackTrace();
                  try {
                      //  chatObject.removeChatMessageListener(this);
                      //  chatObject.addChatMessageListener(this);
                  } catch (Exception e1) {
                  }
                  Log.e(TAG, "Exception----> " + e.toString());
            }

      }
*/
      public void setlist() {

          /*  try {
                  dbHelper = new ClickinDbHelper(this);
                  dbHelper.openDataBase();
                  authManager = ModelManager.getInstance().getAuthorizationManager();
                  databaseList.clear();

                  databaseList = dbHelper.getAllChat(authManager.getQBId(), qBId);
                  Log.e(TAG, "This get From DATABASE-> " + databaseList.size());
                  if (databaseList.size() == 0) {
                        emptyDb = true;
                        chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), "");
                  }

                  chatManager.chatListFromServer.addAll(databaseList);

            } catch (Exception e) {
                  Log.e(TAG, "Exception-> " + e.toString());
            }*/

          //temp code
          chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), "");
       //     adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, chatManager.chatListFromServer);
            // adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo,chatData);
            //chatListView.setAdapter(adapter);
      }

     /* private void setHistoryChat() {

            adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, chatManager.chatListFromServer);
            chatListView.setAdapter(adapter);

      }*/

      @Override
      public void onClick(View v) {
          switch (v.getId()) {
               case R.id.btn_send:
                 String chatString = chatText.getText().toString();

                  if ((chatString.length() > 0 || isClicks() == true || mImageCaptureUri != null)) {
                      if(mImageCaptureUri==null) {// if all media files are null
                          ChatMessageBody temp = new ChatMessageBody();

                          if (isClicks() == true) {
                              temp.clicks = convertClicks(seekValue).trim();
                              temp.textMsg = temp.clicks + "        " + chatString;
                          } else {
                              temp.clicks = "no";
                              temp.textMsg = chatString;
                          }
                          temp.partnerQbId = qBId;
                          temp.senderQbId = authManager.getQBId();
                          temp.chatType = Constants.CHAT_TYPE_TEXT;
                          CHAT_TYPE = Constants.CHAT_TYPE_TEXT;

                          myQbChatService.sendMessage(temp);
                          setValueForHistory(temp);

                          chatText.setText("");
                          seekValue=0;
                      }else{//if any media is attached
                          if(mImageCaptureUri!=null){//if image is attached
                              CHAT_TYPE = Constants.CHAT_TYPE_IMAGE;
                             // new uploadMediaFileOnQb().execute(path.toString());
                              if(path!=null)
                              uploadImageFileOnQB(path.toString());
                          }
                      }

                  }

           /*     ((RelativeLayout) findViewById(R.id.rl_flipper)).setVisibility(View.GONE);
                chatString = "" + chatText.getText().toString();
                String clicksValue = null;
               ChatRecordBeen addChat = new ChatRecordBeen();
                addChat.setSenderQbId(authManager.getQBId());
                addChat.setRecieverQbId(qBId);
                sentOn = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                chatId = authManager.getQBId() + qBId + sentOn;
                Log.e(TAG, "chatId" + chatId);

                if ((chatString.length() > 0 || isClicks() == true || mImageCaptureUri != null || audioFilePath != null || videofilePath != null)) {
                    try {
                        if (mImageCaptureUri == null && audioFilePath == null && videofilePath == null) {
                            Log.e(TAG, "NO Image- AND CLICKS--> ");

                            addChat.setChatType("1");
                            if (isClicks()) {
                                clicksValue = convertClicks(seekValue).trim();
                            } else {
                                clicksValue = "no";
                            }

                            if (!Utils.isEmptyString(chatString)) {
                                addChat.setChatText("" + chatString);
                            } else {
                                addChat.setChatText(chatString);
                                chatString = "";
                            }
                            addChat.setSenderQbId(authManager.getQBId());
                            addChat.setRecieverQbId(qBId);
                            addChat.setUserId(authManager.getUserId());
                            addChat.setClicks(clicksValue);
                            addChat.setTimeStamp(String.valueOf(sentOn));
                            addChat.setChatId(chatId);


                            if (clicksValue.equalsIgnoreCase("no") && !Utils.isEmptyString(chatString)) {
                                sendWithExtension(chatString, "no");
                                clicksValue = null;
                            } else if (Utils.isEmptyString(chatString) && !clicksValue.equalsIgnoreCase("no")) {
                                sendWithExtension(clicksValue + "   ", clicksValue + "   ");
                            } else if (!Utils.isEmptyString(chatString) && !clicksValue.equalsIgnoreCase("no")) {
                                sendWithExtension(clicksValue + "      " + chatString, clicksValue);
                            }

                            createRfecordOnQuickBlox(chatString, clicksValue, null, rId, authManager.getUserId(), authManager.getUsrToken(), "" + sentOn, chatId, "1", null, null, null, null, null, null);
                            chatManager.chatListFromServer.add(addChat);
                            adapter.notifyDataSetChanged();
                            //chatText.setText("");


                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                            //attachBtn.setBackgroundResource(R.drawable.attach_icon);
                        } else if (mImageCaptureUri != null && audioFilePath == null && videofilePath == null) {

                            addChat.setChatType("2");

                            if (!Utils.isEmptyString(chatString)) {
                                addChat.setChatText("" + chatString);
                            } else {
                                addChat.setChatText("");
                                chatString = "";
                            }

                            if (isClicks()) {
                                clicksValue = convertClicks(seekValue).trim();
                                addChat.setClicks(clicksValue);
                            } else {
                                clicksValue = "no";
                                addChat.setClicks(null);
                            }

                            addChat.setUserId(authManager.getUserId());
                            addChat.setTimeStamp(String.valueOf(sentOn));
                            addChat.setChatImageUrl(currentImagepath);
                            addChat.setChatText("" + chatString);
                            addChat.setChatId(chatId);


                            uploadImageOnQuickBlox(path.toString(), chatString, clicksValue, chatId);

                            chatManager.chatListFromServer.add(addChat);
                            adapter.notifyDataSetChanged();


                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));

                        } else if (mImageCaptureUri == null && audioFilePath != null && videofilePath == null) {

                            addChat.setChatType("3");
                            if (!Utils.isEmptyString(chatString)) {
                                addChat.setChatText("" + chatString);
                            } else {
                                addChat.setChatText("");
                                chatString = "";
                            }

                            if (isClicks()) {
                                clicksValue = convertClicks(seekValue).trim();
                                addChat.setClicks(clicksValue);
                            } else {
                                clicksValue = "no";
                                addChat.setClicks(null);
                            }

                            addChat.setUserId(authManager.getUserId());
                            addChat.setTimeStamp(String.valueOf(sentOn));
                            addChat.setChatImageUrl(audioFilePath);
                            addChat.setChatText("" + chatString);

                            uploadAudioOnQuickBlox(audioFilePath.toString(), chatString, clicksValue);
                            chatManager.chatListFromServer.add(addChat);
                            adapter.notifyDataSetChanged();
                            audioFilePath = null;

                            adapter.notifyDataSetChanged();
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));

                        } else if (videofilePath != null && mImageCaptureUri == null && audioFilePath == null) {
                            Log.e(TAG, "" + "Attach Video file");
                            addChat.setChatType("4");
                            if (!Utils.isEmptyString(chatString)) {
                                addChat.setChatText("" + chatString);
                            } else {
                                addChat.setChatText("");
                                chatString = "";
                            }

                            if (isClicks()) {
                                clicksValue = convertClicks(seekValue).trim();
                                addChat.setClicks(clicksValue);
                            } else {
                                clicksValue = "no";
                                addChat.setClicks(null);
                            }

                            addChat.setUserId(authManager.getUserId());
                            addChat.setTimeStamp(String.valueOf(sentOn));
                            addChat.setChatImageUrl(videofilePath);
                            addChat.setChatText("" + chatString);

                            //  uploadAudioOnQuickBlox(videofilePath.toString(), chatString,clicksValue);
                            chatManager.chatListFromServer.add(addChat);
                            adapter.notifyDataSetChanged();
                            videofilePath = null;

                            adapter.notifyDataSetChanged();
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));

                        } else if (videofilePath == null && mImageCaptureUri == null && audioFilePath == null) {
                            Log.e(TAG, "" + "Attach Card file");
                            addChat.setChatType("5");
                            if (!Utils.isEmptyString(chatString)) {
                                addChat.setChatText("" + chatString);
                            } else {
                                addChat.setChatText("");
                                chatString = "";
                            }
                            if (isClicks()) {
                                clicksValue = convertClicks(seekValue).trim();
                                addChat.setClicks(clicksValue);
                            } else {
                                clicksValue = "no";
                                addChat.setClicks(null);
                            }
                            addChat.setUserId(authManager.getUserId());
                            addChat.setTimeStamp(String.valueOf(sentOn));
                            addChat.setChatImageUrl(videofilePath);
                            addChat.setChatText("" + chatString);
                            //  uploadAudioOnQuickBlox(videofilePath.toString(), chatString,clicksValue);
                            chatManager.chatListFromServer.add(addChat);
                            adapter.notifyDataSetChanged();
                            videofilePath = null;

                            adapter.notifyDataSetChanged();
                            attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));

                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    chatText.setText("");
                    mImageCaptureUri = null;
                    chatString = "";
                    audioFilePath = null;
                    videofilePath = null;

                    //set user Clicks
                    userClicks = userClicks + seekValue;
                    Log.e(TAG, "" + userClicks);
                    partnerTotalclicks.setText("" + userClicks);
                    relationManager = ModelManager.getInstance().getRelationManager();
                    relationManager.acceptedList.get(relationListIndex).setUserClicks(Integer.toString(userClicks));
                    mybar.setProgress(10);
                    seekValue = 0;


                    attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attach_icon));
                }*/

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

//mukesh/monika chat code 
private void setValueForHistory(ChatMessageBody obj){

        authManager = ModelManager.getInstance().getAuthorizationManager();
        long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

        obj.sentOn = ""+sentOntime;
        obj.chatId=authManager.getQBId() + qBId + sentOntime;

        obj.sharedMessage=null;
     //   obj.deliveredChatID=null;
       // obj.cardDetails=null;
        obj.isDelivered=null;
        obj.relationshipId=rId;
        obj.userId=authManager.getUserId();
        obj.senderUserToken=authManager.getUsrToken();
        obj.senderQbId=authManager.getQBId();
        ArrayList<ChatMessageBody>tempChatList=ModelManager.getInstance().getChatManager().chatMessageList;
        tempChatList.add(obj);

        if(adapter==null) {
            adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, tempChatList);
            chatListView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
        createRecordForHistory(obj);

    }

    private void uploadImageFileOnQB(String path){
        File mfile = new File(path);
        QBContent.uploadFileTask(mfile, true, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBFileUploadTaskResult res = (QBFileUploadTaskResult) result;
                    uploadedImgUrl = res.getFile().getPublicUrl().toString();
                    System.out.println(TAG+"<---onPostExecute--->"+uploadedImgUrl);
                    sendMsgToQB(uploadedImgUrl);
                }
            }
        });

    }

    private void sendMsgToQB(String result){
        ChatMessageBody temp = new ChatMessageBody();
        String chatString=chatText.getText().toString();
        switch (CHAT_TYPE){
            case Constants.CHAT_TYPE_IMAGE:
                temp.imageRatio = "1";
                temp.content_url = result;
                temp.chatType = Constants.CHAT_TYPE_IMAGE;
                break;
            default:
        }
        if (isClicks() == true) {
            temp.clicks = convertClicks(seekValue).trim();
            temp.textMsg = temp.clicks + "        " + chatString;
        } else {
            temp.clicks = "no";
            temp.textMsg = chatString;
        }
        temp.partnerQbId = qBId;


        myQbChatService.sendMessage(temp);
        chatText.setText("");
        seekValue=0;
        mImageCaptureUri=null;

    }


      @Override
       protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
           String actionReq = intent.getAction();
            if (actionReq.equalsIgnoreCase("UPDATE")) {
                //  new DBTask().execute();
                  updateValues(intent);
            } else if (actionReq.equalsIgnoreCase("CARD")) {
                  Log.e(TAG + "onNewIntent", "onNewIntent");
                  ChatMessageBody temp=new ChatMessageBody();

                temp.is_CustomCard = intent.getExtras().getBoolean("is_CustomCard");
                if(!temp.is_CustomCard){
                    temp.card_DB_ID = intent.getExtras().getString("card_Db_id");
                    temp.card_url = intent.getExtras().getString("card_url");
                    temp.card_content = intent.getExtras().getString("Discription");

                }
                temp.card_id = intent.getExtras().getString("card_id");
                if(Utils.isEmptyString(temp.card_id)){
                   long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                    temp.card_id = authManager.getQBId() + qBId + sentOntime;
                }
                temp.card_heading = intent.getExtras().getString("Title");
                temp.clicks = intent.getExtras().getString("card_clicks");
                temp.card_Accepted_Rejected = intent.getExtras().getString("card_Accepted_Rejected");
                temp.card_Played_Countered = intent.getExtras().getString("played_Countered");
                temp.card_originator = intent.getExtras().getString("card_originator");

                temp.card_owner = authManager.getQBId();
                temp.chatType=Constants.CHAT_TYPE_CARD;
                temp.partnerQbId=qBId;
                temp.textMsg="";

                if(myQbChatService!=null)
                myQbChatService.sendMessage(temp);

            }

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
                  //chatObject.sendMessage(Integer.parseInt(qBId), message);
            } catch (Exception e) {

                  try {
                       // againLoginToQuickBlox();
                       // chatObject.removeChatMessageListener(this);
                        //chatObject.addChatMessageListener(this);
                  } catch (Exception e1) {
                  }
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
            if (EventBus.getDefault().isRegistered(this)) {
                  EventBus.getDefault().unregister(this);
            }

            try {
                //  chatObject.removeChatMessageListener(this);
            } catch (Exception e) {
            }


      }

 public void onEventMainThread(String message) {
            android.util.Log.d(TAG, "onEventMainThread->" + message);
            authManager = ModelManager.getInstance().getAuthorizationManager();
            if (message.equalsIgnoreCase("FecthChat True")) {
                  chatListView.onRefreshComplete();
                if(chatManager.chatMessageList.size()!=0){
                    if(adapter==null){
                         adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo,chatManager.chatMessageList);
                        chatListView.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                }

                /*  if (isHistroy && emptyDb == false) {
                       // setHistoryChat();
                        isHistroy = false;
                  } else {*/
                        //chatManager.chatListFromServer.addAll(0, chatManager.refreshivechatList);
                      //  adapter.notifyDataSetChanged();
                 // }

                 /* if (emptyDb) {
                        //setHistoryChat();
                        Log.e("emptyDb", "emptyDb->");

                        adapter.notifyDataSetChanged();
                  }

                  emptyDb = false;*/

                  Log.e("1", "message->" + message);
            } else if (message.equalsIgnoreCase("FecthChat False")) {
                  chatListView.onRefreshComplete();
                  //setlist();
                  Utils.dismissBarDialog();
                  android.util.Log.d("2", "message->" + message);
            } else if (message.equalsIgnoreCase("FecthChat NetworkchatType Error")) {
                  Utils.fromSignalDialog(ChatRecordView.this, AlertMessage.connectionError);
                  android.util.Log.d("3", "message->" + message);
            }else if (message.equalsIgnoreCase("Chat Message Recieve")){
                adapter.notifyDataSetChanged();
            }else if (message.equalsIgnoreCase("Composing YES")){
                typingtext.setVisibility(View.VISIBLE);
                typingtext.setText("Typing..");
            }else if (message.equalsIgnoreCase("Composing NO")){
                typingtext.setVisibility(View.VISIBLE);
                typingtext.setText("online");
            }

      }

     /* @Override
      public void processMessage(Message message) {
            //Set typin Status....
            String chatType = "0";
            String fileID = null;
            String body = null;
            String clicks = null;
            String toUserId = null;
            String fromUserId = null;

            //Card DS
            String card_clicks = null;
            String card_owner = null;
            String card_content = null;
            String is_CustomCard = null;
            String card_DB_ID = null;
            String card_heading = null;
            String card_Accepted_Rejected = null;
            String card_url = null;
            String card_id = null;
            String card_Played_Countered = null;
            String card_originator = null;

            // Card DE


            long sentOn = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
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
                              clicks = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("clicks");
                              chatType = "1";
                        }
                        if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("fileID")) {
                              fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("fileID");
                              chatType = "2";

                              body = xmlJSONObj.getJSONObject("message").getString("body");

                              clicks = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("clicks");
                              fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("fileID");

                              Log.e(TAG, "Chattype--2");
                        }
                        if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("audioID")) {
                              fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("audioID");
                              chatType = "3";

                              body = xmlJSONObj.getJSONObject("message").getString("body");

                              clicks = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("clicks");
                              fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("audioID");

                              Log.e(TAG, "Chattype--3");
                        }
                        if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("locationID")) {
                              fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("locationID");
                              chatType = "2";
                              Log.e(TAG, "Chattype--6");
                        }
                        if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("videoID")) {
                              fileID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("videoID");
                              chatType = "4";
                              Log.e(TAG, "Chattype--4");
                        }
                        if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_url")) {


                              card_url = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_url");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_clicks"))
                                    card_clicks = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_clicks");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_owner"))
                                    card_owner = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_owner");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_content"))
                                    card_content = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_content");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("is_CustomCard"))
                                    is_CustomCard = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("is_CustomCard");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_DB_ID"))
                                    card_DB_ID = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_DB_ID");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_heading"))
                                    card_heading = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_heading");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_Accepted_Rejected"))
                                    card_Accepted_Rejected = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_Accepted_Rejected");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_id"))
                                    card_id = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_id");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_Played_Countered"))
                                    xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_Played_Countered");

                              if (xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").has("card_originator"))
                                    card_originator = xmlJSONObj.getJSONObject("message").getJSONObject("extraParams").getString("card_originator");

                              chatType = "5";
                              Log.e(TAG, "Chattype--5");
                        }

                        Log.e(TAG, "--- xmlJSONObj--->" + "body--> " + body + " , clicks--> " + clicks + " , fileID--> " + fileID);

                        chatManager = ModelManager.getInstance().getChatManager();
                        if (chatType.equalsIgnoreCase("1")) {

                              //chatManager = ModelManager.getInstance().getChatManager();
                              addChat.setSenderQbId(authManager.getQBId());
                              addChat.setRecieverQbId(qBId);
                              Log.e(TAG, "-TYPE ONE --");
                              if (clicks.equalsIgnoreCase("no")) {
                                    addChat.setChatType(chatType);
                                    addChat.setUserId(partnerId);
                                    Log.e(TAG, "body-w---> " + body);
                                    addChat.setChatText("" + body);
                                    addChat.setClicks(null);
                                    addChat.setTimeStamp(String.valueOf(sentOn));

                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();

                              } else {
                                    addChat.setChatType(chatType);
                                    addChat.setUserId(partnerId);
                                    if (body.equalsIgnoreCase(clicks)) {
                                          addChat.setClicks(clicks);
                                          addChat.setChatText("");
                                    } else {
                                          addChat.setClicks(clicks);
                                          addChat.setChatText(body.substring(4));
                                    }
                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();
                              }
                        } else if (chatType.equalsIgnoreCase("2")) {
                              addChat.setSenderQbId(authManager.getQBId());
                              addChat.setRecieverQbId(qBId);
                              if (clicks.equalsIgnoreCase("no") && Utils.isEmptyString(body)) {
                                    Log.e(TAG, "Chattype 2 --2");
                                    addChat.setChatType(chatType);
                                    addChat.setChatImageUrl(fileID);
                                    addChat.setUserId(partnerId);
                                    addChat.setChatText(null);
                                    addChat.setClicks(null);
                                    addChat.setTimeStamp(String.valueOf(sentOn));

                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();
                              } else if (clicks.equalsIgnoreCase("no") && !Utils.isEmptyString(body)) {
                                    addChat.setChatType(chatType);
                                    addChat.setChatImageUrl(fileID);
                                    addChat.setUserId(partnerId);
                                    addChat.setClicks(null);
                                    addChat.setChatText(body);
                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();
                              } else if (!clicks.equalsIgnoreCase("no") && !Utils.isEmptyString(body)) {
                                    addChat.setChatType(chatType);
                                    addChat.setChatImageUrl(fileID);
                                    addChat.setUserId(partnerId);
                                    addChat.setClicks(clicks);
                                    addChat.setChatText(body);
                        *//*if( body.equalsIgnoreCase(clicks)){
                            addChat.setClicks(body);
                        }else{
                            addChat.setClicks(body.substring(0, 4));
                        }*//*
                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();
                              }

                        } else if (chatType.equalsIgnoreCase("3")) {
                              addChat.setSenderQbId(authManager.getQBId());
                              addChat.setRecieverQbId(qBId);
                              if (clicks.equalsIgnoreCase("no") && Utils.isEmptyString(body)) {
                                    Log.e(TAG, "Chattype 3 Only Audio File");

                                    addChat.setChatType(chatType);
                                    addChat.setChatImageUrl(fileID);
                                    addChat.setUserId(partnerId);
                                    addChat.setChatText(null);
                                    addChat.setClicks(null);
                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();

                              } else if (clicks.equalsIgnoreCase("no") && !Utils.isEmptyString(body)) {

                                    addChat.setChatType(chatType);
                                    addChat.setChatImageUrl(fileID);
                                    addChat.setUserId(partnerId);
                                    addChat.setClicks(null);
                                    addChat.setChatText(body);
                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();

                              } else if (!clicks.equalsIgnoreCase("no") && !Utils.isEmptyString(body)) {

                                    addChat.setChatType(chatType);
                                    addChat.setChatImageUrl(fileID);
                                    addChat.setUserId(partnerId);
                                    addChat.setClicks(clicks);
                                    addChat.setChatText(body);
                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();

                              }

                        } else if (chatType.equalsIgnoreCase("5")) {
                              String firstname1 = firstname + " ";
                              addChat.setSenderQbId(authManager.getQBId());
                              addChat.setRecieverQbId(qBId);

                              if (card_Accepted_Rejected.equalsIgnoreCase("accepted")) {

                                    Log.e(TAG, "Chattype 5 accepted Only Card File");

                                    addChat.setChatType(chatType);
                                    addChat.setUserId(card_originator);
                                    addChat.setCard_clicks(card_clicks);
                                    addChat.setCard_owner(card_owner);
                                    addChat.setCard_content(card_content);
                                    addChat.setIs_CustomCard(is_CustomCard);
                                    addChat.setCard_DB_ID(card_DB_ID);
                                    addChat.setCard_heading(card_heading);
                                    addChat.setCard_Accepted_Rejected(card_Accepted_Rejected);
                                    addChat.setCard_url(card_url);
                                    addChat.setCard_id(card_id);
                                    addChat.setCard_Played_Countered(card_Played_Countered);
                                    addChat.setCard_originator(card_originator);
                                    addChat.setCardPartnerName(firstname1);

                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);

                                    if (card_originator.equalsIgnoreCase(authManager.getUserId())) {

                                          myClicks = (myClicks - (Integer.parseInt(card_clicks)));
                                          myTotalclicks.setText("" + myClicks);
                                          relationManager = ModelManager.getInstance().getRelationManager();
                                          relationManager.acceptedList.get(relationListIndex).setClicks(Integer.toString(myClicks));

                                          userClicks = (userClicks + (Integer.parseInt(card_clicks)));
                                          partnerTotalclicks.setText("" + userClicks);
                                          relationManager = ModelManager.getInstance().getRelationManager();
                                          relationManager.acceptedList.get(relationListIndex).setUserClicks(Integer.toString(userClicks));
                                    } else {
                                          myClicks = (myClicks + (Integer.parseInt(card_clicks)));
                                          myTotalclicks.setText("" + myClicks);
                                          relationManager = ModelManager.getInstance().getRelationManager();
                                          relationManager.acceptedList.get(relationListIndex).setClicks(Integer.toString(myClicks));

                                          userClicks = (userClicks - (Integer.parseInt(card_clicks)));
                                          partnerTotalclicks.setText("" + userClicks);
                                          relationManager = ModelManager.getInstance().getRelationManager();
                                          relationManager.acceptedList.get(relationListIndex).setUserClicks(Integer.toString(userClicks));
                                    }
                                    adapter.notifyDataSetChanged();

                              } else if (card_Accepted_Rejected.equalsIgnoreCase("rejected")) {

                                    Log.e(TAG, "Chattype 5 rejected Only Card File");

                                    addChat.setChatType(chatType);
                                    addChat.setUserId(partnerId);
                                    addChat.setCard_clicks(card_clicks);
                                    addChat.setCard_owner(card_owner);
                                    addChat.setCard_content(card_content);
                                    addChat.setIs_CustomCard(is_CustomCard);
                                    addChat.setCard_DB_ID(card_DB_ID);
                                    addChat.setCard_heading(card_heading);
                                    addChat.setCard_Accepted_Rejected(card_Accepted_Rejected);
                                    addChat.setCard_url(card_url);
                                    addChat.setCard_id(card_id);
                                    addChat.setCard_Played_Countered(card_Played_Countered);
                                    addChat.setCard_originator(card_originator);
                                    addChat.setCardPartnerName(firstname1);

                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();
                              } else if (card_Accepted_Rejected.equalsIgnoreCase("countered")) {
                                    Log.e(TAG, "Chattype 5 Only Card File");

                                    addChat.setChatType(chatType);
                                    addChat.setUserId(partnerId);
                                    addChat.setCard_clicks(card_clicks);
                                    addChat.setCard_owner(card_owner);
                                    addChat.setCard_content(card_content);
                                    addChat.setIs_CustomCard(is_CustomCard);
                                    if (is_CustomCard.equalsIgnoreCase("true")) {
                                          addChat.setCard_url(card_url);
                                    } else {
                                          addChat.setCard_url("");
                                    }
                                    addChat.setCard_DB_ID(card_DB_ID);
                                    addChat.setCard_heading(card_heading);
                                    addChat.setCard_Accepted_Rejected(card_Accepted_Rejected);
                                    addChat.setCard_url(card_url);
                                    addChat.setCard_id(card_id);
                                    addChat.setCard_Played_Countered(card_Played_Countered);
                                    addChat.setCard_originator(card_originator);
                                    addChat.setCardPartnerName(firstname1);

                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();
                              } else if (card_Accepted_Rejected.equalsIgnoreCase("nil") && !is_CustomCard.equalsIgnoreCase("true")) {
                                    Log.e(TAG, "Chattype 5 Only Card File");

                                    addChat.setChatType(chatType);
                                    addChat.setUserId(partnerId);
                                    addChat.setCard_clicks(card_clicks);
                                    addChat.setCard_owner(card_owner);
                                    addChat.setCard_content(card_content);
                                    addChat.setIs_CustomCard(is_CustomCard);
                                    addChat.setCard_DB_ID(card_DB_ID);
                                    addChat.setCard_heading(card_heading);
                                    addChat.setCard_Accepted_Rejected(card_Accepted_Rejected);
                                    addChat.setCard_url(card_url);
                                    addChat.setCard_id(card_id);
                                    addChat.setCard_Played_Countered(card_Played_Countered);
                                    addChat.setCard_originator(card_originator);
                                    addChat.setCardPartnerName(partnerName);

                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();
                              } else if (is_CustomCard.equalsIgnoreCase("true")) {
                                    Log.e(TAG, "Chattype 5 Only Card Custom");

                                    addChat.setChatType(chatType);
                                    addChat.setUserId(partnerId);
                                    addChat.setCard_clicks(card_clicks);
                                    addChat.setCard_owner(card_owner);
                                    addChat.setCard_content(card_content);
                                    addChat.setIs_CustomCard(is_CustomCard);
                                    addChat.setCard_DB_ID(card_DB_ID);
                                    addChat.setCard_heading(card_heading);
                                    addChat.setCard_Accepted_Rejected(card_Accepted_Rejected);
                                    addChat.setCard_url("");
                                    addChat.setCard_id(card_id);
                                    addChat.setCard_Played_Countered(card_Played_Countered);
                                    addChat.setCard_originator(card_originator);
                                    addChat.setCardPartnerName(partnerName);

                                    addChat.setTimeStamp(String.valueOf(sentOn));
                                    chatManager.chatListFromServer.add(addChat);
                                    adapter.notifyDataSetChanged();
                              }

                        }


                        //Set myclicks on top
                        if (!Utils.isEmptyString(clicks)) {
                              myClicks = myClicks + grandClicksForReceiverEndInt(clicks);
                              myTotalclicks.setText("" + myClicks);
                              relationManager = ModelManager.getInstance().getRelationManager();
                              relationManager.acceptedList.get(relationListIndex).setClicks(Integer.toString(myClicks));
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

     */
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
                                                ExifInterface ei = new ExifInterface(Utils.getRealPathFromURI(mImageCaptureUri,this));
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

      // Audio STUFF END


//Add your photo,TAKE A PICTURE,FROM YOUR GALLERY

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



 private void createRecordForHistory(ChatMessageBody obj) {
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

                ArrayList<String>cards=null;
                    if(obj.card_id!=null){
                        cards=new ArrayList<String>();
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
                              Log.e("New record: ", qbCustomObject.toString());
                        } else {
                              Log.e("Errors", result.getErrors().toString());
                        }
                  }
            });

      }







      private void updateValues(Intent intent) {
            //save previous chat here

            addMenu(false);
            loginToQuickBlox();
            authManager = ModelManager.getInstance().getAuthorizationManager();
            qBId = intent.getExtras().getString("quickId");
            partnerPic = intent.getExtras().getString("partnerPic");
            partnerName = intent.getExtras().getString("partnerName");
            String[] splitted = partnerName.split("\\s+");
            rId = intent.getExtras().getString("rId");
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

            } catch (Exception e) {
                  e.printStackTrace();
            }
            Picasso.with(ChatRecordView.this).load(partnerPic)

                    .error(R.drawable.male_user).into(partnerPix);


            chatData.clear();
          //  setlist();
      }


      class DBTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                  try {
                        //dbHelper.deleteChat(authManager.getQBId(), qBId);
                        dbHelper.addChatList(chatManager.chatMessageList);
                  } catch (Exception e) {
                        e.printStackTrace();
                  }
                  return null;

            }
      }


      public Uri getImageUri(Context inContext, Bitmap inImage) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
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





}



 /* extension.setValue("audioID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("locationID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("location_coordinates", "43546,4646");*/


