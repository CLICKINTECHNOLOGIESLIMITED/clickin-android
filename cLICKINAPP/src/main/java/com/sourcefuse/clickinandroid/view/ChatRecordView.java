package com.sourcefuse.clickinandroid.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.services.MyQbChatService;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.AudioUtil;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.utils.VideoUtil;
import com.sourcefuse.clickinandroid.view.adapter.ChatRecordAdapter;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

public class ChatRecordView extends ClickInBaseView implements View.OnClickListener,
        TextWatcher {
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = ChatRecordView.class.getSimpleName();
    private static final String IMAGE_DIRECTORY_NAME = "ClickIn/ClickinImages";
    public static String rId = "";
    //flag to start and stop thread to check online status
    public static boolean CHECK_ONLINE_STATUS_FLAG = false;
    public  TextView load_earlier;
    public MyQbChatService myQbChatService;
    int myvalue = 0, min = -10;//akshit ,To set my value initially to zero for send paper rocket condition
    String chatString = "";
    int seekValue = 0;
    int maxValue = 20; // Double of range
    int initialProgresss = maxValue / 2;
    //private QBPrivateChat chatObject;
    String firstname="";
    String[] splitted;
    TextView send_text;
    //chatId-unquie to chat msg- use to track delivery status
    Integer msgId;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            //Do Something

            AudioUtil.startRecording();
            /*
             * Timer if required
			 */

        }
    };
    private SeekBar mybar;
    private TextView pos, neg, profileName, typingtext, myclicksView, partnerClicksView;
    private Button send, btnToCard;
    private int relationListIndex = -1;
    private String qBId, partnerPic, partnerName, partnerId, partnerPh, myClicks, partnerClicks;
    private ChatManager chatManager;
    private AuthManager authManager;
    private RelationManager relationManager;
    private EditText chatText;
    private ImageView mypix, partnerPix, attachBtn;
    //    private PullToRefreshListView chatListView;
    private ListView chatListView;
    private ChatRecordAdapter adapter = null;
    private TextSwitcher mSwitcher;
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

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            myQbChatService = ((MyQbChatService.LocalBinder) service).getService();
            mIsBound = true;
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
            mIsBound = false;
//            Toast.makeText(Binding.this, R.string.local_service_disconnected,
//                    Toast.LENGTH_SHORT).show();
        }
    };
    private String onlineStatus;
    private String mChatId;  // to save image qbchatid and systemmillseconds
    private String mLocation_Coordinates = "";
    private int chatListSize = 0;


    private ClickinDbHelper dbHelper;
    private String thumurl = null;

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
        cursor.close(); // close cursor
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

    public String writePhotoJpg(Bitmap data, String pathName) {
        Random mRandom = new Random();
        int mName = mRandom.nextInt();
        mName = Math.abs(mName);

        String thumbpath = Utils.mImagePath;
        File file = new File(thumbpath);
        String mPath = file.getAbsolutePath() + "/" + mChatId + ".jpg";  // save image by mChatIdname

        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            File mFile = new File(mPath);
            FileOutputStream os = new FileOutputStream(mFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            data.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            os.flush();
            os.close();

            /* to show image in gallery */

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, mPath);
            values.put(MediaStore.Images.Media.DATE_TAKEN, mFile.lastModified());
            Uri mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); // to notify change
            getContentResolver().notifyChange(Uri.parse(mPath), null);


        } catch (Exception e) {
//            e.printStackTrace();removed by akshit

        }
        return mPath;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_chat_layout);
//code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        Intent i = new Intent(this, MyQbChatService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);


        //check for values, if not set by parent class Clickinbase view, then set it using index
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("partnerIndex")) {
                setValuesUsingIndex(extras.getInt("partnerIndex"));
            } else {
                partnerPic = extras.getString("partnerPic");
                partnerName = extras.getString("partnerName");
                partnerId = extras.getString("partnerId");

                partnerPh = extras.getString("partnerPh");
                relationListIndex = extras.getInt("relationListIndex");
                rId = extras.getString("rId");
                myClicks = extras.getString("myClicks");

                userClicks = extras.getString("userClicks");
                quickBlockId = extras.getString("quickId");
            }

        }

        //clear the message list always to initiate a new chat
        ModelManager.getInstance().getChatManager().chatMessageList.clear();
        setlist();

        addMenu(false);
        slidemenu.setTouchModeAbove(2);

        send = (Button) findViewById(R.id.btn_send);
        chatListView = (ListView) findViewById(R.id.chat_list);

        //akshit code to implement Load Earlier ,Remove Pull to refresh
        LayoutInflater inflater = this.getLayoutInflater();
        View header = inflater.inflate(R.layout.list_header_chat, null);

        load_earlier = (TextView) header.findViewById(R.id.load_earlier);
        if(chatManager.chatMessageList.size()>19){
            load_earlier.setVisibility(View.VISIBLE);
        }else{
            load_earlier.setVisibility(View.GONE);
        }
        load_earlier.setOnClickListener(this);
        chatListView.addHeaderView(header);

//       Code ends

        chatText = (EditText) findViewById(R.id.edit_chatBox);
        mybar = (SeekBar) findViewById(R.id.seekBar1);

        mybar.setProgressDrawable(getResources().getDrawable(R.drawable.seek_bar));

        send_text = (TextView) findViewById(R.id.textview_send);//akshit
        send_text.setOnClickListener(this);//akshit
        pos = (TextView) findViewById(R.id.tv_positive);
        neg = (TextView) findViewById(R.id.tv_negetive);
        mypix = (ImageView) findViewById(R.id.iv_my_pix);
        attachBtn = (ImageView) findViewById(R.id.iv_attach);
        attachBtn.setScaleType(ImageView.ScaleType.FIT_XY);
        partnerPix = (ImageView) findViewById(R.id.iv_partner_pix);
        profileName = (TextView) findViewById(R.id.tv_profiler);
        llAttachment = (LinearLayout) findViewById(R.id.ll_attachment);
        typingtext = (TextView) findViewById(R.id.tv_typing);

        myclicksView = (TextView) findViewById(R.id.tv_myclick);
        partnerClicksView = (TextView) findViewById(R.id.tv_partner_click);

        mSwitcher = (TextSwitcher) findViewById(R.id.textswitcher);


        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                TextView myText = new TextView(ChatRecordView.this);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(87);
                myText.setTextColor(getResources().getColor(R.color.feed_clickin));
                myText.setTypeface(null, Typeface.BOLD);
                return myText;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this, R.anim.number_out);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.number_in);


        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);


        atchPhoto = (ImageView) findViewById(R.id.iv_photo);
        attachAudio = (ImageView) findViewById(R.id.iv_adiuo);
        attachVideo = (ImageView) findViewById(R.id.iv_video);
        attachLocation = (ImageView) findViewById(R.id.iv_location);
        btnToCard = (Button) findViewById(R.id.btn_to_card);

        chatText.setMovementMethod(new ScrollingMovementMethod());// akshit Code to scroll text smoothly inside edit text


        pos.setText("+" + mybar.getMax());
        neg.setText("-" + mybar.getMax());

        mybar.setMax(maxValue);
        mybar.setProgress(initialProgresss);

        send.setOnClickListener(this);
        btnToCard.setOnClickListener(this);
        partnerPix.setOnClickListener(this);


        attachBtn.setOnClickListener(this);

        ModelManager.getInstance().getAuthorizationManager().partnerQbId = quickBlockId;

        if(!Utils.isEmptyString(partnerName)) {
            splitted = partnerName.split("\\s+");
            firstname = splitted[0].toUpperCase();
        }


        ModelManager.getInstance().getRelationManager().getPartnerName = firstname;


        ModelManager.getInstance().getAuthorizationManager().ourClicks = myClicks;

        ModelManager.getInstance().getRelationManager().partnerClicks = userClicks;

        myclicksView.setText("" + ModelManager.getInstance().getAuthorizationManager().ourClicks);
        partnerClicksView.setText("" + ModelManager.getInstance().getRelationManager().partnerClicks);


        profileName.setText("" + splitted[0]);
        try {
            Bitmap imageBitmap;
            String mUserImagePath = null;
            Uri mUserImageUri = null;
            imageBitmap = ModelManager.getInstance().getAuthorizationManager().getUserbitmap();
            if (ModelManager.getInstance().getAuthorizationManager().getUserImageUri() != null)
                mUserImagePath = "" + ModelManager.getInstance().getAuthorizationManager().getUserImageUri().toString();

            if (!Utils.isEmptyString(mUserImagePath))
                mUserImageUri = Utils.getImageContentUri(ChatRecordView.this, new File(mUserImagePath));


            if (!Utils.isEmptyString("" + mUserImageUri))
                mypix.setImageURI(mUserImageUri);
            else if (imageBitmap != null) {
                mypix.setImageBitmap(imageBitmap);
            } else if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUserPic()) && Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getGender()) && ModelManager.getInstance().getAuthorizationManager().getGender().equalsIgnoreCase("girl"))
                Picasso.with(this).load(ModelManager.getInstance().getAuthorizationManager().getUserPic()).error(R.drawable.female_user).into(mypix);
            else
                Picasso.with(this).load(ModelManager.getInstance().getAuthorizationManager().getUserPic()).error(R.drawable.male_user).into(mypix);

        } catch (Exception e) {
            if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUserPic()) && !Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getGender()) &&
                    ModelManager.getInstance().getAuthorizationManager().getGender().equalsIgnoreCase("guy"))
                mypix.setImageResource(R.drawable.male_user);
            else if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getUserPic()) && !Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().getGender()) &&
                    ModelManager.getInstance().getAuthorizationManager().getGender().equalsIgnoreCase("girl"))
                mypix.setImageResource(R.drawable.female_user);
            else
                mypix.setImageResource(R.drawable.male_user);
        }

        if (!Utils.isEmptyString(partnerPic))
            Picasso.with(ChatRecordView.this).load(partnerPic).skipMemoryCache().error(R.drawable.male_user).into(partnerPix);
        else
            partnerPix.setImageResource(R.drawable.male_user);


        mybar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                Utils.trackMixpanel(ChatRecordView.this,"Activity" ,"UserDraggedClickBar","UserDraggedClickBar",false);


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                setVisibilityForSend();//akshit
                myvalue = progress - 10;

                if (myvalue > 0) {
                    // pos.setText("" + myvalue);
                    ((TextView) findViewById(R.id.sign)).setText("+");
                    setVisibilityForSend();//akshit code
                    Utils.playSound(ChatRecordView.this, R.raw.clicker_slider);//akshit code to play app sound
                    mybar.setProgressDrawable(getResources().getDrawable(R.drawable.styled_progress));//akshit code
                    mybar.setThumb(getResources().getDrawable(R.drawable.clickinpinkthumb));//akshit code
                    findViewById(R.id.rl_flipper).setVisibility(View.VISIBLE);
                    findViewById(R.id.rl_flipper).setBackgroundResource(R.color.white_with_transparent);
                    mSwitcher.setText("" + clickForFlipper(myvalue));
                    seekValue = myvalue;
                }

                if (myvalue < 0) {

                    ((TextView) findViewById(R.id.sign)).setText("-");
                    ((TextView) findViewById(R.id.sign)).setPadding(5, 0, 0, 0);
                    setVisibilityForSend();//akshit
                    Utils.playSound(ChatRecordView.this, R.raw.clicker_slider);//akshit code to play app sound
                    mybar.setProgressDrawable(getResources().getDrawable(R.drawable.styled_progress));//akshit code
                    mybar.setThumb(getResources().getDrawable(R.drawable.clickinpinkthumb));//akshit code
                    findViewById(R.id.rl_flipper).setVisibility(View.VISIBLE);
                    findViewById(R.id.rl_flipper).setBackgroundResource(R.color.black_opacity);
                    mSwitcher.setText("" + clickForFlipper(myvalue));
                    seekValue = myvalue;

                }

                if (myvalue == 0) {
                    seekValue = 0;
                    setVisibilityForSendButton();//akshit code
                    mybar.setProgressDrawable(getResources().getDrawable(R.drawable.seek_bar));
                    mybar.setThumb(getResources().getDrawable(R.drawable.thumb_seek));
                    ((RelativeLayout) findViewById(R.id.rl_flipper)).setVisibility(View.GONE);
                }

            }
        });

        chatText.addTextChangedListener(this);

        atchPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To track through mixPanel.
                //click on Image Attach Button.
                Utils.trackMixpanel(ChatRecordView.this, "Activity", "AttachImageButtonClicked", "AttachButtonClicked", false);
                hideAttachView();
                imageDialog();
            }
        });

        attachAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To track through mixPanel.
                //click on Audio Attach Button.
                Utils.trackMixpanel(ChatRecordView.this, "Activity", "AttachAudioNoteButtonClicked", "AttachButtonClicked", false);
                hideAttachView();
                AudioUtil.mAudioName = mChatId;
                alertDialog();

            }
        });

        attachVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To track through mixPanel.
                //click on Video Attach Button.
                Utils.trackMixpanel(ChatRecordView.this, "Activity", "AttachVideoButtonClicked", "AttachButtonClicked", false);
                hideAttachView();
                VideoUtil.name = mChatId;  // name of video
                VideoUtil.videoDialog(ChatRecordView.this);
            }
        });
        attachLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                hideAttachView();
                //To track through mixPanel.
                //click on Location Attach Button.
                Utils.trackMixpanel(ChatRecordView.this, "Activity", "AttachLocationButtonClicked", "AttachButtonClicked", false);
                Intent intent = new Intent(ChatRecordView.this, MapActivity.class);
                startActivityForResult(intent, Constants.START_MAP);
            }
        });

        if(ModelManager.getInstance().getChatManager().chatMessageList.size()>0) {
            adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, ModelManager.getInstance().getChatManager().chatMessageList);

            chatListView.setAdapter(adapter);
            chatListView.setSelection(ModelManager.getInstance().getChatManager().chatMessageList.size() - 1);//akshit code
        }



        //code to check online of offline status

        /* on create */


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Utils.dismissBarDialog();
            }
        }, 3000);



        /* code to show dialog*/
        if (getIntent().getExtras().containsKey("mValue")) {


            String mRelationShipId = ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).getRelationshipId();


            if (getIntent().getStringExtra("mValue").equalsIgnoreCase("one")) {

                Utils.showOverlay(ChatRecordView.this);
                ModelManager.getInstance().getAuthorizationManager().reSetFlag(ModelManager.getInstance().getAuthorizationManager().getPhoneNo(), ModelManager.getInstance().getAuthorizationManager().getUsrToken(), mRelationShipId, "no", "no", relationListIndex);
            } else if (getIntent().getStringExtra("mValue").equalsIgnoreCase("two")) {
                ModelManager.getInstance().getAuthorizationManager().reSetFlag(ModelManager.getInstance().getAuthorizationManager().getPhoneNo(), ModelManager.getInstance().getAuthorizationManager().getUsrToken(), mRelationShipId, "no", "null", relationListIndex);
                Utils.showDialog(ChatRecordView.this);
            }

        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        CHECK_ONLINE_STATUS_FLAG = false;

        //  new DBTask().execute(rId);
    }

    private void updateClicksInRelationshipList() {
        //monika-swap values as per naming convention on server
        ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).setClicks(ModelManager.getInstance().getRelationManager().partnerClicks);
        ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).setUserClicks(ModelManager.getInstance().getAuthorizationManager().ourClicks);

    }

    @Override

   public void onBackPressed() {
      //  super.onBackPressed();
      //  finish();


    }

    public void imageDialog() {

        Dialog dialog = new Dialog(ChatRecordView.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRecordView.this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            case 0:
                                //To track through mixPanel.
                                //Image taken from gallery.
                                Utils.trackMixpanel(ChatRecordView.this, "Activity", "ChooseImageFromGallery", "AttachButtonClicked", false);
                                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, Constants.SELECT_PICTURE);

                                break;

                            case 1:
                                //To track through mixPanel.
                                //Image taken from Camera.
                                Utils.trackMixpanel(ChatRecordView.this, "Activity", "ClickImageFromCamera", "AttachButtonClicked", false);
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                mImageCaptureUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                cameraIntent.putExtra("return-data", true);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                                startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);

                                break;

                            default:
                                break;
                        }
                    }
                }
        );

        builder.show();
        dialog.dismiss();


    }

    public void alertDialog() {
        dialog = new Dialog(ChatRecordView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_record_vice);
        dialog.setCancelable(true);
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

                            if (!Utils.isEmptyString(audioFilePath)) {
                                attachBtn.setImageResource(R.drawable.ic_voicerecordicon);
                                //akshit code to check wether image buton contains image or not;
                                if (attachBtn.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.r_footer_icon).getConstantState()) {
                                    setVisibilityForSendButton();

                                } else {
                                    setVisibilityForSend();
                                }
                                //ends

                            }
                            dialog.dismiss();
                        } catch (Exception e) {
//                            e.printStackTrace();removed by akshit
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
            chatManager = ModelManager.getInstance().getChatManager();

            chatManager.chatMessageList = dbHelper.getAllChat(rId);

            if (chatManager.chatMessageList.size() < 20) {
                //emptyDb = true;
                chatManager.chatMessageList.clear();
                chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), "");
            }

        } catch (Exception e) {
//            e.printStackTrace();removed by akshit
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_earlier://akshit code to fetch history of chat ,after load earlier is clicked

                // int lastIndex = chatManager.chatMessageList.size() - 1;
                chatListSize = chatManager.chatMessageList.size();
                String lastChatId = chatManager.chatMessageList.get(0).chatId;
                Utils.launchBarDialog(this);
                chatManager.fetchChatRecord(rId, authManager.getPhoneNo(), authManager.getUsrToken(), lastChatId);


                break;
            case R.id.btn_send:
                break;
            case R.id.textview_send://akshit code for event on send
                //   break;

                //code to check online status or not

                String chatString = chatText.getText().toString();

                if ((chatString.length() > 0 || isClicks() || mImageCaptureUri != null) || audioFilePath != null || videofilePath != null) {
                    if (mImageCaptureUri == null && audioFilePath == null && videofilePath == null) {// if all media files are null
                        ChatMessageBody temp = new ChatMessageBody();


                        if (isClicks()) {
                            temp.clicks = Utils.convertClicks(String.valueOf(seekValue)).trim();
                            temp.textMsg = temp.clicks + "        " + chatString;


                            //To track through mixPanel.
                            //user dragged clickbar to set click.
//                            Utils.trackMixpanel(ChatRecordView.this, "Activity", "UserDraggedClickBar", "UserDraggedClickBar", false);

                            Utils.trackMixpanel(ChatRecordView.this, "ClicksSent", "" + temp.clicks, "RPageSendButtonClicked", false);

                            //To track through mixPanel.
                            //Click msg send.
                            Utils.trackMixpanel(ChatRecordView.this, "Activity", "ClickMessageSent", "RPageSendButtonClicked", false);

                            Utils.trackMixpanel_superProperties(this, seekValue, "clicks");//Track Mix Panel To send,Clicks,To send Clicks Value

                            /* code to play sound in case of clicks prafull*/
                        } else {
                            temp.clicks = "no";
                            temp.textMsg = chatString;
                            //To track through mixPanel.
                            //Trade card button clicked.

                            Utils.trackMixpanel(ChatRecordView.this, "Activity", "TextMessageSent", "RPageSendButtonClicked", false);


                        }
                        temp.partnerQbId = ModelManager.getInstance().getAuthorizationManager().partnerQbId;
                        temp.senderQbId = ModelManager.getInstance().getAuthorizationManager().getQBId();
                        temp.chatType = Constants.CHAT_TYPE_TEXT;
                        CHAT_TYPE = Constants.CHAT_TYPE_TEXT;

                        long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

                        temp.sentOn = "" + sentOntime;
                        temp.chatId = ModelManager.getInstance().getAuthorizationManager().getQBId() + ModelManager.getInstance().getAuthorizationManager().partnerQbId + sentOntime;   // chat id for text and clicks


                        myQbChatService.sendMessage(temp);
                        ShowValueinChat(temp);

                        createRecordForHistory(temp);

                        chatText.setText("");
                        seekValue = 0;
                        mybar.setProgress(10);

                    } else if (mImageCaptureUri != null) {//image is attachedd
                        CHAT_TYPE = Constants.CHAT_TYPE_IMAGE;
                        sendMsgToQB(path);
                        //To track through mixPanel.
                        //Image send.
                        Utils.trackMixpanel(ChatRecordView.this, "Activity", "MediaSent", "RPageSendButtonClicked", false);


                        /* code to play sound in case of image prafull*/
                       /* Utils.playSound(ChatRecordView.this, R.raw.message_sent);*/

                    } else if (!Utils.isEmptyString(audioFilePath)) { //Audio is attached

                        CHAT_TYPE = Constants.CHAT_TYPE_AUDIO;
                        sendMsgToQB(audioFilePath);
                        //To track through mixPanel.
                        //Audio Send.

                        Utils.trackMixpanel(ChatRecordView.this, "Activity", "MediaSent", "RPageSendButtonClicked", false);

                        /* code to play sound in case of audio prafull*/
                        /*Utils.playSound(ChatRecordView.this, R.raw.message_sent);*/
                    } else if (!Utils.isEmptyString(videofilePath)) { //Video is attached

                        CHAT_TYPE = Constants.CHAT_TYPE_VIDEO;
                        sendMsgToQB(videofilePath);
                        //To track through mixPanel.
                        //Video Send.

                        Utils.trackMixpanel(ChatRecordView.this, "Activity", "MediaSent", "RPageSendButtonClicked", false);

                        /* code to play sound in case of video prafull*/
                        /*Utils.playSound(ChatRecordView.this, R.raw.message_sent);*/
                    }


                }

                /* to detele uri once image is send prafull */
                mImageCaptureUri = null;
                path = null;
                attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attachedfileiconx));


                /* to play sound*/
                Utils.playSound(ChatRecordView.this, R.raw.message_sent);

                break;

            case R.id.btn_to_card:

                long sentOntime1 = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                mChatId = ModelManager.getInstance().getAuthorizationManager().getQBId() + ModelManager.getInstance().getAuthorizationManager().partnerQbId + sentOntime1;  // put value in mChatId once button is pressed

                //akshit code to hide keyboard
                if (chatText.getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(chatText.getWindowToken(), 0);
                }
                //endp
                Intent intent = new Intent(ChatRecordView.this, CardView.class);
                intent.putExtra("qBId", ModelManager.getInstance().getAuthorizationManager().partnerQbId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);//akshit Code
                break;

            case R.id.iv_attach:

                // to set previous values to null once image is pressed again

                long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                mChatId = ModelManager.getInstance().getAuthorizationManager().getQBId() + ModelManager.getInstance().getAuthorizationManager().partnerQbId + sentOntime;  // put value in mChatId once button is pressed

                attachBtn.setImageBitmap(null);
                attachBtn.setImageResource(R.drawable.attachedfileiconx);

                mImageCaptureUri = null;
                audioFilePath = null;
                videofilePath = null;

                if (showAttachmentView) {
                    Utils.playSound(ChatRecordView.this, R.raw.menu_attachments);//akshit code to play app sound
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
                if(Utils.isConnectingToInternet(ChatRecordView.this)) {
                    Intent viewProfile = new Intent(ChatRecordView.this, JumpOtherProfileView.class);
                    //  viewProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    viewProfile.putExtra("FromOwnProfile", true);
                    viewProfile.putExtra("phNumber", partnerPh);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    startActivity(viewProfile);

                    //To track through mixPanel.
                    //click on partner pic.
                    Utils.trackMixpanel(this, "", "", "RPageCheckMyPartnerProfile", false);
                    break;
                }else {
                    Utils.fromSignalDialog(ChatRecordView.this,AlertMessage.connectionError);
                }

        }
    }

    //monika- function to show chat in listview
    private void ShowValueinChat(ChatMessageBody obj) {


        AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
        RelationManager relationManager = ModelManager.getInstance().getRelationManager();

        //if its is not a shared message either accepted or rejected- monika
        if (Utils.isEmptyString(obj.isAccepted)) {
            //calculate and updates clicks value only when card is not present ,also when sharing media is null-monika
            if (Utils.isEmptyString(obj.card_id) && Utils.isEmptyString(obj.sharingMedia)) {
                if (obj.clicks.startsWith("+")) {
                    Utils.updateClicksPartnerWithoutCard(relationManager.partnerClicks, obj.clicks, true);
                } else if (obj.clicks.startsWith("-")) {
                    Utils.updateClicksPartnerWithoutCard(relationManager.partnerClicks, obj.clicks, false);
                }
            }
            myclicksView.setText("" + authManager.ourClicks);
            partnerClicksView.setText("" + relationManager.partnerClicks);
            authManager = ModelManager.getInstance().getAuthorizationManager();


            obj.sharedMessage = null;
            //   obj.deliveredChatID=null;
            // obj.cardDetails=null;


            //monika- remove click value from text msg
            if ((!obj.clicks.equalsIgnoreCase("no"))) {
                if (obj.textMsg.length() > 3) {
                    obj.textMsg = obj.textMsg.substring(3).trim();
                } else
                    obj.textMsg = " ";
            }


            updateClicksInRelationshipList();
        }

        obj.relationshipId = rId;
        obj.userId = authManager.getUserId();
        obj.senderUserToken = authManager.getUsrToken();
        obj.senderQbId = authManager.getQBId();
        ArrayList<ChatMessageBody> tempChatList = ModelManager.getInstance().getChatManager().chatMessageList;
        tempChatList.add(obj);

        if(tempChatList.size()>0) {
            if (adapter == null) {
                adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, tempChatList);

                chatListView.setAdapter(adapter);
                chatListView.setSelection(tempChatList.size() - 1);//akshit code
            } else {
                adapter.notifyDataSetChanged();
            }
        }
        //  return obj;
        //  createRecordForHistory(obj);

    }

    //monika-fucntion to upload file on Qb
    private void uploadImageFileOnQB(final String tempUrl, String msgId, int type) {

        File mfile = new File(tempUrl);
        final String chatId = msgId;
        final int chatType = type;
        Boolean fileIsPublic = true;
        QBContent.uploadFileTask(mfile, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>() {
            @Override
            public void onSuccess(QBFile file, Bundle params) {

                String fileUrl = file.getPublicUrl().toString();

                sendMediaMsgToQB(fileUrl, chatId, chatType);
            }

            @Override
            public void onError(List<String> errors) {
                // error

            }
        });

    }

    //monika- common function to create msg in case of media attachment
    private void sendMsgToQB(String tempPath) {
        ChatMessageBody temp = new ChatMessageBody();
        String tempUrlToUpload = "";
        String chatString = chatText.getText().toString();
        switch (CHAT_TYPE) {
            case Constants.CHAT_TYPE_IMAGE:
                temp.imageRatio = "1.000000";
                temp.content_url = path;
                tempUrlToUpload = new String(tempPath);
                temp.isDelivered = Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_IMAGE;
                break;
            case Constants.CHAT_TYPE_AUDIO:
                temp.content_url = tempPath;
                tempUrlToUpload = new String(tempPath);
                temp.isDelivered = Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_AUDIO;
                break;
            case Constants.CHAT_TYPE_VIDEO:
                temp.content_url = tempPath;
                tempUrlToUpload = thumurl;
                temp.video_thumb = new String(thumurl);
                temp.isDelivered = Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_VIDEO_INITATING;
                break;
            case Constants.CHAT_TYPE_LOCATION:
                temp.content_url = tempPath;
                tempUrlToUpload = new String(tempPath);
                temp.imageRatio = "1.000000";
                temp.location_coordinates = mLocation_Coordinates;
                temp.isDelivered = Constants.MSG_SENDING;
                temp.chatType = Constants.CHAT_TYPE_LOCATION;
                /* sound in case of location */
                Utils.playSound(ChatRecordView.this, R.raw.message_sent);
                break;

            default:
        }
        if (Utils.isEmptyString(temp.location_coordinates)) {
            if (isClicks()) {
                temp.clicks = Utils.convertClicks(String.valueOf(seekValue)).trim();
                temp.textMsg = temp.clicks + "        " + chatString;
//                Utils.trackMixpanel(ChatRecordView.this, "Activity", "UserDraggedClickBar", "UserDraggedClickBar", false);
                Utils.trackMixpanel_superProperties(this, seekValue, "clicks");//Track Mix Panel To send,Clicks,To send Clicks Value in case of media
                Utils.trackMixpanel(ChatRecordView.this, "ClicksSent", "" + temp.clicks, "RPageSendButtonClicked", false);
//                Utils.trackMixpanel(ChatRecordView.this, "Activity", "TextMessageSent", "RPageSendButtonClicked", false);

                switch (CHAT_TYPE){
                    case Constants.CHAT_TYPE_IMAGE:
                        Utils.trackMixpanel(ChatRecordView.this,"ClicksWithPhoto",""+temp.clicks,"UserDraggedClickBar",false);
                        break;
                    case Constants.CHAT_TYPE_AUDIO:
                        Utils.trackMixpanel(ChatRecordView.this,"ClicksWithAudio",""+temp.clicks,"UserDraggedClickBar",false);

                        break;
                    case Constants.CHAT_TYPE_VIDEO:
                        Utils.trackMixpanel(ChatRecordView.this,"ClicksWithVideo",""+temp.clicks,"UserDraggedClickBar",false);
                        break;
                }
            } else {
                temp.clicks = "no";
                temp.textMsg = chatString;
            }
        } else {
            temp.clicks = "no";
            temp.textMsg = "Location Shared "; //needed by IOS
        }
        temp.partnerQbId = ModelManager.getInstance().getAuthorizationManager().partnerQbId;
        temp.senderQbId = ModelManager.getInstance().getAuthorizationManager().getQBId();

        long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

        temp.sentOn = "" + sentOntime;
        temp.chatId = mChatId;    // chat id for audio/video/image/map
        temp.isDelivered = Constants.MSG_SENDING;
        temp.content_uri = tempPath;
        //     setValueForHistory(temp);
        ShowValueinChat(temp);
        //To track through mixPanel.
        //No of clicks send with Media(Audio,Video,Image).


        // mImageCaptureUri=null;

        attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attachedfileiconx));
        uploadImageFileOnQB(tempUrlToUpload, temp.chatId, temp.chatType);
        chatText.setText("");
        seekValue = 0;
        mybar.setProgress(10);
        path = null;
        audioFilePath = null;
        thumurl = null;
        videofilePath = null;


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        typingtext.setText("");
        typingtext.setVisibility(View.GONE);
        relationManager = ModelManager.getInstance().getRelationManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();
        String actionReq = intent.getAction();
        chatText.setText(" ");//akshit code to Refresh Chat box text

        if (actionReq.equalsIgnoreCase("UPDATE")) {

            if (myQbChatService == null) {
                Intent i = new Intent(this, MyQbChatService.class);
                bindService(i, mConnection, Context.BIND_AUTO_CREATE);
            }
            //check for values, if not set by parent class Clickinbase view, then set it using index
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey("partnerIndex")) {
                    setValuesUsingIndex(extras.getInt("partnerIndex"));
                } else {
                    partnerPic = extras.getString("partnerPic");
                    partnerName = extras.getString("partnerName");
                    partnerId = extras.getString("partnerId");

                    partnerPh = extras.getString("partnerPh");
                    relationListIndex = extras.getInt("relationListIndex");
                    rId = extras.getString("rId");
                    myClicks = extras.getString("myClicks");
                    quickBlockId = extras.getString("quickId");
                    userClicks = extras.getString("userClicks");
                }

            }
/* code to show dialog */
        /* code to show dialog */
            if (intent.getExtras().containsKey("mValue")) {

                String mRelationShipId = relationManager.acceptedList.get(relationListIndex).getRelationshipId();


                if (intent.getStringExtra("mValue").equalsIgnoreCase("one")) {

                    Utils.showOverlay(ChatRecordView.this);
                    authManager.reSetFlag(authManager.getPhoneNo(), authManager.getUsrToken(), mRelationShipId, "no", "no", relationListIndex);
                } else if (intent.getStringExtra("mValue").equalsIgnoreCase("two")) {
                    authManager.reSetFlag(authManager.getPhoneNo(), authManager.getUsrToken(), mRelationShipId, "no", "null", relationListIndex);
                    Utils.showDialog(ChatRecordView.this);
                }

            }


            updateValues(intent);
        } else if (actionReq.equalsIgnoreCase("CARD")) {

            ChatMessageBody temp = new ChatMessageBody();


            temp.is_CustomCard = intent.getExtras().getBoolean("is_CustomCard");
            if (!temp.is_CustomCard) {
                temp.card_DB_ID = intent.getExtras().getString("card_DB_ID");
                temp.card_url = intent.getExtras().getString("card_url");
                temp.card_content = intent.getExtras().getString("Discription");

            } else {
                //values required by IOS-Monika

                temp.card_content = "_";


                temp.card_DB_ID = "_";

                temp.card_url = "_";
            }
            temp.card_id = intent.getExtras().getString("card_id");
            if (Utils.isEmptyString(temp.card_id)) {
                long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                temp.card_id = ModelManager.getInstance().getAuthorizationManager().getQBId() + ModelManager.getInstance().getAuthorizationManager().partnerQbId + sentOntime;
            }
            temp.card_heading = intent.getExtras().getString("Title");
            temp.clicks = intent.getExtras().getString("card_clicks");
            temp.card_Accepted_Rejected = intent.getExtras().getString("card_Accepted_Rejected");
            if (Utils.isEmptyString(temp.card_Accepted_Rejected))
                temp.card_Accepted_Rejected = "nil";
            temp.card_Played_Countered = intent.getExtras().getString("played_Countered");
            if (Utils.isEmptyString(temp.card_Played_Countered))
                temp.card_Played_Countered = "playing";
            temp.card_originator = intent.getExtras().getString("card_originator");
            if (Utils.isEmptyString(temp.card_originator))
                temp.card_originator = ModelManager.getInstance().getAuthorizationManager().getUserId();

            long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
            temp.sentOn = "" + sentOntime;

            temp.chatType = Constants.CHAT_TYPE_CARD;
            temp.partnerQbId = ModelManager.getInstance().getAuthorizationManager().partnerQbId;
            temp.textMsg = "   ";
            temp.chatId = ModelManager.getInstance().getAuthorizationManager().getQBId() + temp.partnerQbId + sentOntime;  // chat id for cards
            //code for updating clicks value if card is in accepted stage
            String card_action_status = intent.getExtras().getString("card_Accepted_Rejected");

            temp.card_owner = intent.getExtras().getString("card_owner");
            if (Utils.isEmptyString(temp.card_owner))
                temp.card_owner = ModelManager.getInstance().getAuthorizationManager().getQBId();
            if (card_action_status.equalsIgnoreCase("accepted")) {
                if (temp.card_owner.equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getQBId())) {
                    Utils.updateClicksValue(ModelManager.getInstance().getAuthorizationManager().ourClicks, ModelManager.getInstance().getRelationManager().partnerClicks, temp.clicks, false);
                } else {
                    Utils.updateClicksValue(ModelManager.getInstance().getAuthorizationManager().ourClicks, ModelManager.getInstance().getRelationManager().partnerClicks, temp.clicks, true);
                }

            }
            myclicksView.setText("" + ModelManager.getInstance().getAuthorizationManager().ourClicks);
            partnerClicksView.setText("" + ModelManager.getInstance().getRelationManager().partnerClicks);


            ShowValueinChat(temp);

            if (myQbChatService != null)
                myQbChatService.sendMessage(temp);

            createRecordForHistory(temp);

        } else if (actionReq.equalsIgnoreCase("SHARE")) {
            ChatMessageBody temp = new ChatMessageBody();


            if (intent.hasExtra("clicks"))
                temp.clicks = intent.getExtras().getString("clicks");
            // temp.chatType = Constants.CHAT_TYPE_TEXT;
            if (intent.hasExtra("imageRatio")) {
                temp.imageRatio = intent.getExtras().getString("imageRatio");
                temp.content_url = intent.getExtras().getString("fileId");
                if (intent.hasExtra("location_coordinates")) {
                    temp.location_coordinates = intent.getExtras().getString("location_coordinates");
                    //  temp.chatType = Constants.CHAT_TYPE_LOCATION;
                }/*else{
                    temp.chatType = Constants.CHAT_TYPE_IMAGE;
                }*/

            } else if (intent.hasExtra("videoThumbnail")) {
                temp.video_thumb = intent.getExtras().getString("videoThumbnail");
                temp.content_url = intent.getExtras().getString("videoID");
                //  temp.chatType = Constants.CHAT_TYPE_VIDEO;
            } else if (intent.hasExtra("audioID")) {
                temp.content_url = intent.getExtras().getString("audioID");
                //temp.chatType = Constants.CHAT_TYPE_AUDIO;
            } else if (intent.hasExtra("card_originator")) {
                //  temp.chatType = Constants.CHAT_TYPE_CARD;
                temp.card_Accepted_Rejected = intent.getExtras().getString("card_Accepted_Rejected");
                temp.card_DB_ID = intent.getExtras().getString("card_DB_ID");
                temp.card_Played_Countered = intent.getExtras().getString("card_Played_Countered");
                temp.card_content = intent.getExtras().getString("card_content");
                temp.card_heading = intent.getExtras().getString("card_heading");
                temp.card_originator = intent.getExtras().getString("card_originator");


                temp.card_url = intent.getExtras().getString("card_url");
                temp.card_id = intent.getExtras().getString("card_id");

                temp.is_CustomCard = intent.getExtras().getBoolean("is_CustomCard");
                temp.card_owner = intent.getExtras().getString("card_owner");

                if (Utils.isEmptyString(temp.card_owner))
                    temp.card_owner = ModelManager.getInstance().getAuthorizationManager().getQBId();

                //values required by IOS-Monika
                if (Utils.isEmptyString(temp.card_content))
                    temp.card_content = "_";

                if (Utils.isEmptyString(temp.card_DB_ID))
                    temp.card_DB_ID = "_";

                if (Utils.isEmptyString(temp.card_url))
                    temp.card_url = "_";

            }

            temp.sharingMedia = intent.getExtras().getString("sharingMedia");
            temp.originalMessageID = intent.getExtras().getString("originalChatId");
            temp.messageSenderId = ModelManager.getInstance().getAuthorizationManager().getQBId();
            temp.textMsg = intent.getExtras().getString("textMsg");
            temp.shareComment = intent.getExtras().getString("caption");
            temp.isMessageSender = intent.getExtras().getString("isMessageSender");
            temp.shareStatus = intent.getExtras().getString("shareStatus");
            temp.facebookToken = intent.getExtras().getString("facebookToken");
            if (intent.hasExtra("isAccepted"))
                temp.isAccepted = intent.getExtras().getString("isAccepted");
            else
                temp.isAccepted = null;

            //delivered status here is always delivered or sent
            temp.isDelivered = Constants.MSG_SENT;
            long sentOntime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
            temp.sentOn = "" + sentOntime;
            temp.chatId = ModelManager.getInstance().getAuthorizationManager().getQBId() + ModelManager.getInstance().getAuthorizationManager().partnerQbId + sentOntime;  // chat id for sharing
            temp.chatType = intent.getIntExtra("chatType", -1);
            temp.partnerQbId = ModelManager.getInstance().getAuthorizationManager().partnerQbId;
            if (!Utils.isEmptyString(temp.clicks) && !temp.clicks.equalsIgnoreCase("no") && Utils.isEmptyString(temp.card_owner)) {
                temp.textMsg = temp.clicks + "        " + temp.textMsg;
            } else {
                //check chat for shared accept and reject case
                if (!Utils.isEmptyString(temp.isAccepted))
                    temp.clicks = "no";


            }


            ChatMessageBody objToSend = new ChatMessageBody(temp); //we truncate clicks from text in showvalue function,

            //so keep original object here
            ShowValueinChat(temp);
            objToSend.chatType = Constants.CHAT_TYPE_SHARING;
            if (myQbChatService != null)
                myQbChatService.sendMessage(objToSend);

            createRecordForHistory(temp);

        }
        if(chatManager.chatMessageList.size()>19){
            load_earlier.setVisibility(View.VISIBLE);
        }else{
            load_earlier.setVisibility(View.GONE);
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
        if (s.toString().trim().length() > 0) {//akshit to trim the space while showing send button
            setVisibilityForSend();//akshit code

            if(myQbChatService!=null){

                myQbChatService.sendTypeNotification("YES", ModelManager.getInstance().getAuthorizationManager().partnerQbId);
            }

        } else {
            setVisibilityForSendButton();//akshit code if length is 0
            if (chatText.hasFocus()){

                //akshit code to check focus on edit box.if not focused then isComposing will not appear.
                if(myQbChatService!=null)
                    myQbChatService.sendTypeNotification("NO", ModelManager.getInstance().getAuthorizationManager().partnerQbId);

            }


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


        myHandler = new Handler();

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //code to check online status or not
                if (!Utils.isEmptyString(ModelManager.getInstance().getAuthorizationManager().partnerQbId)) {

                    if (myQbChatService != null) {
                        if (ModelManager.getInstance().getAuthorizationManager().partnerQbId != null)
                            myQbChatService.CheckOnlineStatus(Integer.parseInt(ModelManager.getInstance().getAuthorizationManager().partnerQbId));
                    }
                }
            }
        }, 10000);


    }

    @Override
    public void onStop() {
        super.onStop();
        //    new DBTask().execute();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    /*   public String getRealPathFromURI(Uri uri) {
             Cursor cursor = getContentResolver().query(uri, null, null, null, null);
             cursor.moveToFirst();
             int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
             return cursor.getString(idx);
       }*/


    protected void onResume() {
        super.onResume();
        if (relationListIndex == -1) {
            searchRelationIndex();
        }

        //fetch latest clicks from accepted list everytime
        int size = ModelManager.getInstance().getRelationManager().acceptedList.size();
        if (relationListIndex < size) {
            GetrelationshipsBean tempObj = ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex);
            ModelManager.getInstance().getAuthorizationManager().ourClicks = tempObj.getUserClicks();
            ModelManager.getInstance().getRelationManager().partnerClicks = tempObj.getClicks();
            myclicksView.setText("" + ModelManager.getInstance().getAuthorizationManager().ourClicks);
            partnerClicksView.setText("" + ModelManager.getInstance().getRelationManager().partnerClicks);


            ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).setUnreadMsg(0); // prafull code to set unreaed msg counter to 0 when open from recent app
        }




        //set the flag value to true again -monika
        CHECK_ONLINE_STATUS_FLAG = true;

        if (adapter != null)
            adapter.notifyDataSetChanged();


    }

    public void onEventMainThread(String message) {
        super.onEventMainThread(message);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("FecthChat True")) {
            Utils.dismissBarDialog();

            if (ModelManager.getInstance().getChatManager().chatMessageList.size() > 19 && ModelManager.getInstance().getChatManager().chat_history_size.equalsIgnoreCase("true")) {
                //akshit code set visibility of load earlier ,only is chat records are greater then 20
                load_earlier.setVisibility(View.VISIBLE);                             //also if chat fetched is greater then 20
            } else {
                load_earlier.setVisibility(View.GONE);
            }

            if (ModelManager.getInstance().getChatManager().chatMessageList.size() != 0) {
                if (adapter == null) {
                    adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, ModelManager.getInstance().getChatManager().chatMessageList);

                    chatListView.setAdapter(adapter);
                    chatListView.setSelection(ModelManager.getInstance().getChatManager().chatMessageList.size());

                } else {
                    adapter.notifyDataSetChanged();
                    chatListView.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (chatListSize > 0) {
                                // show last chat when use pull to refresh --"Mukesh"
                                int listsize = ModelManager.getInstance().getChatManager().chatMessageList.size();
                                chatListView.setSelection(listsize - chatListSize);//akshit code
//                                chatListView.getRefreshableView().setSelection((listsize - chatListSize));
                            }

                        }
                    });
                }
            }

        } else if (message.equalsIgnoreCase("No chat history found")) {


        } else if (message.equalsIgnoreCase("FecthChat False")) {
//            chatListView.onRefreshComplete();
       /* on dismiss */
            Utils.dismissBarDialog();

        } else if (message.equalsIgnoreCase("FecthChat Network Error")) {
            Utils.fromSignalDialog(ChatRecordView.this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("Chat Message Recieve")) {
            if (relationListIndex == -1) {
                searchRelationIndex();
            }
            int size = ModelManager.getInstance().getRelationManager().acceptedList.size();
            if (relationListIndex < size) {
                GetrelationshipsBean tempObj = ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex);
                ModelManager.getInstance().getAuthorizationManager().ourClicks = tempObj.getUserClicks();
                ModelManager.getInstance().getRelationManager().partnerClicks = tempObj.getClicks();
                myclicksView.setText("" + ModelManager.getInstance().getAuthorizationManager().ourClicks);
                partnerClicksView.setText("" + ModelManager.getInstance().getRelationManager().partnerClicks);
            }
            if (ModelManager.getInstance().getChatManager().chatMessageList.size()>0) {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                else {
                    adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, ModelManager.getInstance().getChatManager().chatMessageList);

                    chatListView.setAdapter(adapter);
                }
            }
            //  new DBTask().execute(rId);
        } else if (message.equalsIgnoreCase("Composing YES")) {
            typingtext.setVisibility(View.VISIBLE);
            typingtext.setText("Typing..");


        } else if (message.equalsIgnoreCase("Composing NO")) {

            typingtext.setVisibility(View.VISIBLE);
            typingtext.setText("online");


        } else if (message.equalsIgnoreCase("Delivered")) {
            adapter.notifyDataSetChanged();
            //  updateChatDeliverStatusInList(chatId);

        } else if (message.startsWith("ChatShare True")) {
            //adapter.notifyDataSetChanged();
        } else if (message.equalsIgnoreCase("Connected Successfully")) { //monika-code to handle QB connection
            //  Toast.makeText(this, "Connected again", Toast.LENGTH_SHORT).show();
            send.setEnabled(true);
            send_text.setEnabled(true);
        } else if (message.equalsIgnoreCase("Disconnected QB")) {
            // Toast.makeText(this,"Sorry,got disconnected",Toast.LENGTH_SHORT).show();
            send.setEnabled(false);
            send_text.setEnabled(false);
            ModelManager.getInstance().getSettingManager().changeLastSeenTime(
                    ModelManager.getInstance().getAuthorizationManager().getPhoneNo(),
                    ModelManager.getInstance().getAuthorizationManager().getUsrToken(), "");
        } else if (message.equalsIgnoreCase("online")) {
            typingtext.setVisibility(View.VISIBLE);
            typingtext.setText("online");

            /* to update value of last seen time prafull code */
            long timestamp = Utils.ConvertIntoTimeStamp() / 1000;
            ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).mLastSeenTime = String.valueOf(timestamp);
        } else if (message.equalsIgnoreCase("offline")) {


            long timestamp = Utils.ConvertIntoTimeStamp() / 1000;
            //relationManager.acceptedList.get(relationListIndex).mLastSeenTime = String.valueOf(timestamp);

/* code for check when user is offline prafull code */
            String lastSeenTime = "";
            long mLastSeenTimeStamp = 0;

            if (!Utils.isEmptyString(ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).mLastSeenTime))
                mLastSeenTimeStamp = Long.parseLong(ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).mLastSeenTime);
            if (mLastSeenTimeStamp != 0) {
                typingtext.setVisibility(View.VISIBLE);
                if (Utils.getCompareDate(timestamp * 1000).equalsIgnoreCase(Utils.getCompareDate(mLastSeenTimeStamp * 1000))) {// if both are equals than time for
                    typingtext.setText(Utils.getTodaySeenDate(mLastSeenTimeStamp * 1000));
                } else { // when not equal
                    typingtext.setText(Utils.getLastSeenDate(mLastSeenTimeStamp * 1000));
                }
            } else {
                typingtext.setVisibility(View.GONE);
            }

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* try {*/  // Prafull code remove Try
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.CAMERA_REQUEST:
                    Uri targetUri = mImageCaptureUri;


                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    Bitmap bitmap = null;
                    bitmap = decodeSampledBitmapFromUri(targetUri, width, height);
                    if (bitmap != null) {
                            /*try {*/  // Prafull code remove Try
                        ModelManager.getInstance().getAuthorizationManager().setOrginalBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true));
                        Intent intent = new Intent(ChatRecordView.this, CropView.class);
                        intent.putExtra("name", mChatId);  // save image name
                        intent.putExtra("from", "fromchatCamare");
                        intent.putExtra("uri", mImageCaptureUri);
                        startActivityForResult(intent, Constants.CROP_PICTURE);
                            /*} catch (Exception e) {
//                                e.printStackTrace();
                            }*/
                    }
                    try {   // Prafull code put Try for smaller block
                        mImageCaptureUri = Utils.decodeUri(ChatRecordView.this, mImageCaptureUri, 550);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    path = Utils.getRealPathFromURI(mImageCaptureUri, this);
                    currentImagepath = mImageCaptureUri.toString();

                    bitmap.recycle();
                    break;
                case Constants.SELECT_PICTURE:


                    Uri targetUri1 = data.getData();


                    Display display1 = getWindowManager().getDefaultDisplay();
                    Point size1 = new Point();
                    display1.getSize(size1);
                    int width1 = size1.x;
                    int height1 = size1.y;

                    Bitmap bitmap1 = null;
                    bitmap1 = decodeSampledBitmapFromUri(targetUri1, width1, height1);

                    if (bitmap1 != null) { // Prafull code remove try
                            /*try {*/
                        ModelManager.getInstance().getAuthorizationManager().setOrginalBitmap(bitmap1);
                        Intent intent = new Intent(ChatRecordView.this, CropView.class);
                        intent.putExtra("name", "" + mChatId);   // save image by name
                        intent.putExtra("from", "fromchatGallery");
                        intent.putExtra("uri", data.getData());
                        startActivityForResult(intent, Constants.CROP_PICTURE);
                           /* } catch (Exception e) {
//                                e.printStackTrace();
                            }*/
                    }
                    try { // Prafull code put Try for smaller block
                        mImageCaptureUri = Utils.decodeUri(ChatRecordView.this, data.getData(), 550);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    path = Utils.getRealPathFromURI(mImageCaptureUri, this);
                    currentImagepath = mImageCaptureUri.toString();


                    break;
                case VideoUtil.REQUEST_VIDEO_CAPTURED:
                    if (!Utils.isEmptyString(VideoUtil.videofilePath)) {
                        ////To track through mixPanel.Vedio Attached from Camera
                        Utils.trackMixpanel(this, "Activity", "SelectedVideoAttached", "AttachButtonClicked", false);
                        videofilePath = VideoUtil.videofilePath;
                        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(VideoUtil.videofilePath, MediaStore.Video.Thumbnails.MINI_KIND);
                        if (videofilePath.contains(".mp4")) {
                            Random mRandom = new Random();
                            int mName = mRandom.nextInt();
                            mName = Math.abs(mName);
                            thumurl = videofilePath.replace(".mp4", "" + mName);
                            thumurl = writePhotoJpg(bMap, thumurl);
                            //thumurl = Utils.getRealPathFromURI(Uri.parse(thumurl),ChatRecordView.this);
                        }
                        attachBtn.setImageBitmap(bMap);
                        //akshit code to check wether image buton contains image or not;
                        if (attachBtn.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.r_footer_icon).getConstantState()) {
                            setVisibilityForSendButton();

                        } else {
                            setVisibilityForSend();
                        }

                            /* prafull code for image */
                        attachBtn.setImageResource(R.drawable.ic_playvideoicon);
                    }
                    break;
                case VideoUtil.REQUEST_VIDEO_CAPTURED_FROM_GALLERY:
                    // mImageCaptureUri = data.getData();
                    //To track through mixPanel.Attach Vedio From Gallery
                    Utils.trackMixpanel(this, "Activity", "SelectedVideoAttached", "AttachButtonClicked", false);
                    path = Utils.getRealPathFromURI(data.getData(), ChatRecordView.this);
                    videofilePath = path;
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);

                    if (videofilePath.contains(".mp4")) {
                        Random mRandom = new Random();
                        int mName = mRandom.nextInt();
                        mName = Math.abs(mName);

                        thumurl = videofilePath.replace(".mp4", "" + mName);

                        thumurl = writePhotoJpg(bMap, thumurl);



                            /* prafull code for image */
                        attachBtn.setImageResource(R.drawable.ic_playvideoicon);
                        //akshit code to check wether image buton contains image or not;

                    } else {
                        path = null;
                        audioFilePath = null;
                        thumurl = null;
                        videofilePath = null;
                        attachBtn.setImageResource(R.drawable.r_footer_icon);

                    }
                    if (attachBtn.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.r_footer_icon).getConstantState()) {
                        setVisibilityForSendButton();
                    } else {
                        setVisibilityForSend();
                    }


                    //ends
                    break;


                case Constants.CROP_PICTURE:
                    if (data.getStringExtra("retake").equalsIgnoreCase("fromchatCamare")) {


                        //To track through mixPanel.if User Cancells image Clicked from Camera.
                        Utils.trackMixpanel(this, "Activity", "ImageAttachmentCancelled", "AttachButtonClicked", false);

                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mImageCaptureUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        intent1.putExtra("return-data", true);
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(intent1, Constants.CAMERA_REQUEST);
                    } else if (data.getStringExtra("retake").equalsIgnoreCase("fromchatGallery")) {
                        //To track through mixPanel.if User Cancells image Picked from Gallery.
                        Utils.trackMixpanel(this, "Activity", "ImageAttachmentCancelled", "AttachButtonClicked", false);
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                    } else if (authManager.getmResizeBitmap() != null) {
                        ImageView view = (ImageView) findViewById(R.id.iv_attach);
                        view.setImageBitmap(authManager.getmResizeBitmap());
                        authManager.setmResizeBitmap(null);
                        String mPath = data.getStringExtra("path");
                        path = mPath;
                        if (path != null)
                            ((ImageView) findViewById(R.id.iv_attach)).setImageURI(Uri.parse(path));


                        //akshit code to check wether image buton contains image or not;
                        if (view.getDrawable() != null && view.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.r_footer_icon).getConstantState()) {
                            setVisibilityForSendButton();


                        } else {

                            setVisibilityForSend();
                        }
                        //ends
                    } else {
                        mImageCaptureUri = null;
                        path = null;
                    }
                    break;

                case Constants.START_MAP:
                    Utils.launchBarDialog(this);
                    Double latitude = data.getDoubleExtra("lat", 0.0);
                    Double longitude = data.getDoubleExtra("lng", 0.0);
                    mLocation_Coordinates = latitude + "," + longitude;
                    String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&markers=color%3ared|color%3ared|label%3aA|" + latitude + "," + longitude + "&zoom=15&size=650x400&sensor=true";

                    new DownloadImage().execute(url);

                    break;
                default:
                    break;
            }
        }
       /* } catch (Exception e) {
        }*/
    }


// IMAGE STUFF END

    // Audio STUFF STArt

    /* for image */
    public Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {

        Bitmap bm = null;

        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {

//            e.printStackTrace();

        }

        return bm;
    }


// IMAGE STUFF END

    // Audio STUFF STArt

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    // IMAGE STUFF start
    private void uploadImageOnQuickBlox(final String path, final String msg, final String clicks, final String chat_Id) {
        File mfile = new File(path);
        QBContent.uploadFileTask(mfile, true, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBFileUploadTaskResult res = (QBFileUploadTaskResult) result;
                    uploadedImgUrl = res.getFile().getPublicUrl().toString();
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
        }
    }

    // Audio STUFF END


//Add your photo,TAKE A PICTURE,FROM YOUR GALLERY

    private void uploadAudioOnQuickBlox(final String path, final String msg, final String clicks) {
        File mfile = new File(path);
        QBContent.uploadFileTask(mfile, true, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBFileUploadTaskResult res = (QBFileUploadTaskResult) result;
                    audioFilePath = res.getFile().getPublicUrl().toString();
                    sendAudiotoPartner(audioFilePath, msg, clicks);
                    audioFilePath = null;
                }
            }
        });
    }

    private void sendAudiotoPartner(String filepath, String msg, String clicks) {
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

        }
    }


    private boolean isClicks() {
        if (seekValue != 0 && (-10 <= seekValue && seekValue <= 10)) {
            ModelManager.getInstance().getChatManager().setPartnerTotalClick(seekValue);
            return true;
        } else {
            return false;
        }
    }


    private String clickForFlipper(int clicks) {

        String changeClicks = "";

        if (clicks == 1 || clicks == -1) {
            changeClicks = "1";
        } else if (clicks == 2 || clicks == -2) {
            changeClicks = "2";
        } else if (clicks == 3 || clicks == -3) {
            changeClicks = "3";
        } else if (clicks == 4 || clicks == -4) {
            changeClicks = "4";
        } else if (clicks == 5 || clicks == -5) {
            changeClicks = "5";
        } else if (clicks == 6 || clicks == -6) {
            changeClicks = "6";
        } else if (clicks == 7 || clicks == -7) {
            changeClicks = "7";
        } else if (clicks == 8 || clicks == -8) {
            changeClicks = "8";
        } else if (clicks == 9 || clicks == -9) {
            changeClicks = "9";
        } else if (clicks == 10 || clicks == -10) {
            changeClicks = "10";
        }
        return changeClicks;

    }


    private void createRecordForHistory(final ChatMessageBody obj) {
        HashMap<String, Object> fields = new HashMap<String, Object>();
        String clicks = obj.clicks;

        fields.put("message", obj.textMsg);
        //first check whether clicks value is null- in case of shared aaccept/reject this value is null
        if (!Utils.isEmptyString(obj.clicks)) {
            if (obj.clicks.equalsIgnoreCase("no"))  //monika-change value of clicks to null if no click is there
                clicks = null;
        }

        //in case of cards, for history, clicks will be in cards array , not here-monika
        if (Utils.isEmptyString(obj.card_id))
            fields.put("clicks", clicks);
        fields.put("content", obj.content_url);
        fields.put("imageRatio", obj.imageRatio);
        fields.put("relationshipId", obj.relationshipId);
        fields.put("userId", obj.userId);
        fields.put("senderUserToken", obj.senderUserToken);
        fields.put("sentOn", obj.sentOn);// "142455987");//UTC
        fields.put("chatId", obj.chatId);
        fields.put("type", obj.chatType);
        fields.put("video_thumb", obj.video_thumb);
        fields.put("deliveredChatID", obj.deliveredChatID);
        fields.put("location_coordinates", obj.location_coordinates);

        ArrayList<String> cards = null;
        if (obj.card_id != null) {
            cards = new ArrayList<String>();
            cards.add(obj.card_id);
            cards.add(obj.card_heading);
            cards.add(obj.card_content);
            cards.add(obj.card_url);
            cards.add(obj.clicks);
            cards.add(obj.card_Accepted_Rejected);
            cards.add(obj.card_originator);
            cards.add(String.valueOf(obj.is_CustomCard));
            cards.add(obj.card_DB_ID);
            if (obj.card_Accepted_Rejected.equalsIgnoreCase("nil")) {
                cards.add("playing");
            } else {
                cards.add("played");
            }
            cards.add(obj.card_owner);

        }
        fields.put("cards", cards);
        fields.put("location_coordinates", obj.location_coordinates);

        ArrayList<String> sharedMessage = null;
        if (obj.originalMessageID != null) {
            sharedMessage = new ArrayList<String>();
            sharedMessage.add(obj.originalMessageID);
            sharedMessage.add(obj.shareStatus);
            sharedMessage.add(obj.senderQbId);
            sharedMessage.add(obj.isAccepted);
            sharedMessage.add(obj.isMessageSender);
            if (obj.shareComment.equalsIgnoreCase("Write your caption here...")) {//code to filter if Comments are not there (For History Only )//Akshit

                obj.shareComment = " ";
                sharedMessage.add(obj.shareComment);
            } else {
                sharedMessage.add(obj.shareComment);
            }

            sharedMessage.add(obj.sharingMedia);
            sharedMessage.add(obj.facebookToken);
        }
        fields.put("sharedMessage", sharedMessage);

        QBCustomObject qbCustomObject = new QBCustomObject();
        qbCustomObject.setClassName("chats");  // your Class name
        qbCustomObject.setFields(fields);

        //monika-variable to update chat id from QB
        final String chatId = obj.chatId;
        new DBTask().execute(rId);
        QBCustomObjects.createObject(qbCustomObject, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBCustomObjectResult qbCustomObjectResult = (QBCustomObjectResult) result;
                    QBCustomObject qbCustomObject = qbCustomObjectResult.getCustomObject();
                    String chatIdQB = String.valueOf(qbCustomObject.getCustomObjectId());
                    //      updateChatId(chatIdQB,chatId);
                    //  obj.chatId=String.valueOf(chatIdtemp);
                } else {
                }
            }
        });
        // return String.valueOf(msgId);
    }

    private void updateValues(Intent intent) {

        //reset all values for chat
        chatText.setText("");
        seekValue = 0;
        mybar.setProgress(10);
        path = null;
        audioFilePath = null;
        thumurl = null;
        videofilePath = null;
        attachBtn.setImageDrawable(getResources().getDrawable(R.drawable.attachedfileiconx));
        //clear the message list always to initiate a new chat
        ModelManager.getInstance().getChatManager().chatMessageList.clear();
        setlist();
        Bundle bundle = intent.getExtras();
        addMenu(false);

        if (bundle != null) {
            //access value directly from parent class
            ModelManager.getInstance().getAuthorizationManager().partnerQbId = quickBlockId;

            ModelManager.getInstance().getRelationManager().getPartnerName = firstname;

            String[] splitted = partnerName.split("\\s+");
            firstname = splitted[0].toUpperCase();
            ModelManager.getInstance().getRelationManager().getPartnerName = firstname;


            //reset values of clicks
            AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
            authManager.ourClicks = null;
            RelationManager relationManager = ModelManager.getInstance().getRelationManager();
            relationManager.partnerClicks = null;
            authManager.ourClicks = myClicks;

            relationManager.partnerClicks = userClicks;

            myclicksView.setText("" + authManager.ourClicks);
            partnerClicksView.setText("" + relationManager.partnerClicks);


// get Chat record From server
            ModelManager.getInstance().getChatManager().chatListFromServer.clear();


            profileName.setText("" + splitted[0]);
            try {
                Bitmap imageBitmap;
                String mUserImagePath = null;
                Uri mUserImageUri = null;
                imageBitmap = authManager.getUserbitmap();
                if (authManager.getUserImageUri() != null)
                    mUserImagePath = "" + authManager.getUserImageUri().toString();

                if (!Utils.isEmptyString(mUserImagePath))
                    mUserImageUri = Utils.getImageContentUri(ChatRecordView.this, new File(mUserImagePath));


                if (!Utils.isEmptyString("" + mUserImageUri))
                    mypix.setImageURI(mUserImageUri);
                else if (imageBitmap != null)
                    mypix.setImageBitmap(imageBitmap);
                else if (!Utils.isEmptyString(authManager.getUserPic()) && Utils.isEmptyString(authManager.getGender()) && authManager.getGender().equalsIgnoreCase("girl"))
                    Picasso.with(this).load(authManager.getUserPic()).error(R.drawable.female_user).into(mypix);
                else
                    Picasso.with(this).load(authManager.getUserPic()).error(R.drawable.male_user).into(mypix);
            } catch (Exception e) {

                mypix.setImageResource(R.drawable.male_user);
            }
            if (!Utils.isEmptyString(partnerPic))
                Picasso.with(ChatRecordView.this).load(partnerPic).error(R.drawable.male_user).into(partnerPix);
            else
                partnerPix.setImageResource(R.drawable.male_user);


            //code to hide keyboard
            ((RelativeLayout) findViewById(R.id.rr_chat_layout)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (chatText.getWindowToken() != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(chatText.getWindowToken(), 0);
                    }

                }

            });
        }
        adapter = null;
        if(chatManager.chatMessageList.size()>0) {
            adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, chatManager.chatMessageList);
            chatListView.setAdapter(adapter);
            chatListView.setSelection(chatManager.chatMessageList.size()-1);
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Utils.dismissBarDialog();
            }
        }, 3000);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //function to update chatMessagelist for delivered chats
    private void updateChatDeliverStatusInList(String chatId) {
        for (ChatMessageBody msg : ModelManager.getInstance().getChatManager().chatMessageList) {
            if (msg.chatId.equalsIgnoreCase(chatId)) {
                msg.isDelivered = "true";
                break;
            }
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
        else {
            adapter = new ChatRecordAdapter(this, R.layout.view_chat_demo, ModelManager.getInstance().getChatManager().chatMessageList);
            chatListView.setAdapter(adapter);
        }
    }

    //monika-update the content url for specific chatid and send msg to Qb and create history
    private void sendMediaMsgToQB(String fileUrl, String tempChatId, int chatType) {
        // ArrayList<ChatMessageBody> tempChatList=chatManager.chatMessageList;
        //reset the path value set from On activityresult--monika

        for (ChatMessageBody temp : ModelManager.getInstance().getChatManager().chatMessageList) {
            if (!(Utils.isEmptyString(temp.chatId))) {
                if (temp.chatId.equalsIgnoreCase(tempChatId)) {
                    //monika-need to upload two files in case of video
                    if (chatType == Constants.CHAT_TYPE_VIDEO_INITATING) {
                        temp.video_thumb = fileUrl;
                        temp.chatType = Constants.CHAT_TYPE_VIDEO;
                        uploadImageFileOnQB(temp.content_url, tempChatId, temp.chatType);
                    } else {
                        temp.content_url = fileUrl;
                        temp.isDelivered = Constants.MSG_SENT;
//value is trimmed while showing in list, so adding it again
                        ChatMessageBody tempObj = new ChatMessageBody(temp);
                        // code to change value when send copy constructor
                        if (!temp.clicks.equalsIgnoreCase("no")) {

                            tempObj.textMsg = temp.clicks + "        " + temp.textMsg;
                        }
                        myQbChatService.sendMessage(tempObj); // copy constructor

                        adapter.notifyDataSetChanged();
                        createRecordForHistory(temp);

                    }


                }
            }
        }
    }

    //akshit code to set the visibility for send(Textview)
    public void setVisibilityForSend() {

        findViewById(R.id.btn_send).setVisibility(View.GONE);
        findViewById(R.id.textview_send).setVisibility(View.VISIBLE);

    }

    //akshit code to set visibility for paper rocket (button)
    public void setVisibilityForSendButton() {

        if (chatText.getText().toString().trim().length() == 0 && myvalue == 0 && attachBtn.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.attachedfileiconx).getConstantState()) {
            ((Button) findViewById(R.id.btn_send)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textview_send)).setVisibility(View.GONE);
        } else {
            setVisibilityForSend();
        }
    }

    private void updateChatId(String chatQBId, String chatId) {
        ArrayList<ChatMessageBody> chatList = chatManager.chatMessageList;
        if (chatList.size() != 0) {
            for (ChatMessageBody temp : chatList) {
                if (temp.chatId.equalsIgnoreCase(chatId)) {
                    temp.chatId = chatQBId;
                }
            }
        }
    }

    public void searchRelationIndex() {
        if (ModelManager.getInstance().getChatManager().chatMessageList.size() > 0) {
            ChatMessageBody tempObj = ModelManager.getInstance().getChatManager().chatMessageList.get(0);

            int relationIndex = -1;
            ArrayList<GetrelationshipsBean> partnerList = ModelManager.getInstance().getRelationManager().acceptedList;
            for (GetrelationshipsBean temp : partnerList) {
                relationIndex++;
                if (temp.getRelationshipId().equalsIgnoreCase(tempObj.relationshipId)) {
                    break;
                    //update clicks value in accepted list

                }
            }

            relationListIndex = relationIndex;
        }


    }

    @Override
    public void onClose() {

    }

    @Override
    public void onOpen() {

    }

    private void setValuesUsingIndex(int index) {

        if (ModelManager.getInstance().getRelationManager().acceptedList != null &&
                ModelManager.getInstance().getRelationManager().acceptedList.size() > index) {
            GetrelationshipsBean temp = ModelManager.getInstance().getRelationManager().acceptedList.get(index);
            partnerName = temp.getPartnerName();
            rId = temp.getRelationshipId();
            partnerPic = temp.getPartnerPic();
            quickBlockId = temp.getPartnerQBId();


            ModelManager.getInstance().getAuthorizationManager().partnerQbId = temp.getPartnerQBId();


            partnerId = temp.getPartner_id();
            userClicks = temp.getClicks();
            myClicks = temp.getUserClicks();
            partnerPh = temp.getPhoneNo();
            relationListIndex = index;

        }
    }

     /* downoad image from server */

    class DBTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... str) {
            try {
                final String relId = str[0];
                dbHelper.deleteChat(relId);
                dbHelper.addChatList(chatManager.chatMessageList);
            } catch (Exception e) {
//                e.printStackTrace();removed by akshit
            }
            return null;

        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return bitmap;
            }

        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null) {
                String newpath = Utils.mAudioPath;
                Random rn = new Random();
                String path = newpath + "" + rn.nextInt();
                String imagepath = writePhotoJpg(result, path);
                CHAT_TYPE = Constants.CHAT_TYPE_LOCATION;
                sendMsgToQB(imagepath);
            }
            Utils.dismissBarDialog();
        }
    }


}



 /* extension.setValue("audioID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("locationID", "https://qbprod.s3.amazonaws.com/bb19b8ba52764d39b4362299e93ebadf00");
        extension.setValue("location_coordinates", "43546,4646");*/
