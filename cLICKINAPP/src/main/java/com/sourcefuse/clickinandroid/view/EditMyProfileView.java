package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by mukesh on 1/9/14.
 */


public class EditMyProfileView extends Activity implements View.OnClickListener {
    private static final String TAG = EditMyProfileView.class.getSimpleName();
    private Button clickToSave;
    private ImageView mySelfy, OpenGallery, OpenCamera, backAction;
    private EditText myName, myLast, myEmail, myCity, myCountry;
    private AuthManager authManager;
    private ProfileManager profileManager;
    private Uri mImageCaptureUri , ImageUri;
    private Bitmap imageBitmap = null;

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "ClickIn' Application";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_editprofile);

        clickToSave = (Button) findViewById(R.id.btn_click_to_save);
        myName = (EditText) findViewById(R.id.edt_my_name);
        myLast = (EditText) findViewById(R.id.edt_my_last);
        myEmail = (EditText) findViewById(R.id.edt_my_email);
        myCity = (EditText) findViewById(R.id.edt_my_city);
        myCountry = (EditText) findViewById(R.id.edt_my_country);
        mySelfy = (ImageView) findViewById(R.id.iv_selfi);
        backAction = (ImageView) findViewById(R.id.iv_menu);
        OpenCamera = (ImageView) findViewById(R.id.iv_edit_camera);
        OpenGallery = (ImageView) findViewById(R.id.iv_edit_gallery);

        mySelfy.setScaleType(ImageView.ScaleType.FIT_XY);
        clickToSave.setOnClickListener(this);
        OpenCamera.setOnClickListener(this);
        OpenGallery.setOnClickListener(this);
        backAction.setOnClickListener(this);


        authManager = ModelManager.getInstance().getAuthorizationManager();
        try {
            String[] names = (authManager.getUserName().split("\\s+", 2));
            myName.setText("" + names[0]);
            myLast.setText("" + names[1]);
            myEmail.setText(authManager.getEmailId());
            myCity.setText(authManager.getUserCity());
            myCountry.setText(authManager.getUserCountry());
        } catch (Exception e) {
        }

//        try {
//            if (!authManager.getUserPic().equalsIgnoreCase("")) {
//            Picasso.with(EditMyProfileView.this).
//                    load(authManager.getUserPic())
//                    .skipMemoryCache()
//                    .centerCrop()
//                    .error(R.drawable.male_user)
//                    .into(mySelfy);
//            } else {
//                mySelfy.setImageResource(R.drawable.male_user);
//            }
//        } catch (Exception e) {
//            mySelfy.setImageResource(R.drawable.male_user);
//        }
        if(authManager.getGender()!=null) {
            if (authManager.getGender().matches("guy")) {

                try {
                    if (!authManager.getUserPic().equalsIgnoreCase("")) {
                        Picasso.with(EditMyProfileView.this)
                                .load(authManager.getUserPic())
                                .skipMemoryCache()

                                .error(R.drawable.male_user).into(mySelfy);
                    } else {
                        mySelfy.setImageResource(R.drawable.male_user);
                    }
                } catch (Exception e) {
                    mySelfy.setImageResource(R.drawable.male_user);
                }
            } else {
                try {
                    if (!authManager.getUserPic().equalsIgnoreCase("")) {
                        Picasso.with(EditMyProfileView.this)
                                .load(authManager.getUserPic())
                                .skipMemoryCache()

                                .error(R.drawable.female_user).into(mySelfy);
                    } else {
                        mySelfy.setImageResource(R.drawable.female_user);
                    }
                } catch (Exception e) {
                    mySelfy.setImageResource(R.drawable.female_user);
                }
            }
        }
        else
        {
            mySelfy.setImageResource(R.drawable.male_user);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_gallery:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                break;
            case R.id.iv_menu:
                finish();
                break;
            case R.id.iv_edit_camera:
                setCameraIntentUri();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //   ImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, ImageUri);
                try {
                    cameraIntent.putExtra("return-data", true);
                    startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);
                } catch (Exception e) {
                }
                break;
            case R.id.btn_click_to_save:
                if (updateProfileValidation()) {
                    if (Utils.isEmailValid(myEmail.getText().toString())) {
                        Log.e(TAG, "btn_click_to_save");
                        try {
                            authManager = ModelManager.getInstance().getAuthorizationManager();
                            profileManager = ModelManager.getInstance().getProfileManager();
                            if (imageBitmap != null) {
                                profileManager.setProfile(myName.getText().toString(), myLast.getText().toString(), authManager.getPhoneNo(),
                                        authManager.getUsrToken(), "", "", myCity.getText().toString(), myCountry.getText().toString(), myEmail.getText().toString(), "", Utils.encodeTobase64(imageBitmap));
                            } else {
                                Log.e(TAG, "btn_click_to_save2");
                                try {
                                    // imageBitmap = Picasso.with(EditMyProfileView.this).load(authManager.getUserPic()).get();
                                    //Utils.encodeTobase64(imageBitmap)
                                    profileManager.setProfile(myName.getText().toString(), myLast.getText().toString(), authManager.getPhoneNo(),
                                            authManager.getUsrToken(), "", "", myCity.getText().toString(), myCountry.getText().toString(), myEmail.getText().toString(), "", "");
                                } catch (Exception e) {
                                    Log.e(TAG, "1" + e.toString());
                                }
                            }
                            authManager.setUserPic(imageBitmap.toString());
                            authManager.setMenuUserInfoFlag(true);
                        } catch (Exception e) {
                            Log.e(TAG, "2" + e.toString());
                        }
                    }
                }

                break;
        }
    }

    public boolean updateProfileValidation() {

        if (myName.getText().toString().length() < 1) {
            Utils.showAlert(EditMyProfileView.this, AlertMessage.fname);
            return false;
        } else if (myLast.getText().toString().length() < 1) {
            Utils.showAlert(EditMyProfileView.this, AlertMessage.lname);
            return false;
        } else if (myEmail.getText().toString().length() < 1) {
            Utils.showAlert(EditMyProfileView.this, AlertMessage.emailid);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) switch (requestCode) {
            case Constants.CAMERA_REQUEST:

                Log.e("camera","camera");

                    Bitmap bitmap = null;

                    try {

                        imageBitmap = null;

//                        Log.e("uri", "" + mImageCaptureUri);
                        if (data != null && data.hasExtra("data")) {
                            Log.e("uri in if", "" + mImageCaptureUri);
                            mImageCaptureUri = null;
                            mImageCaptureUri = data.getData();
                            // Bitmap bmp = BitmapFactory.decodeResource(getResources(), Integer.parseInt(mImageCaptureUri.getPath()));
                            try {
                                imageBitmap = Utils.decodeUri(mImageCaptureUri, EditMyProfileView.this);
                                if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
                                    Matrix matrix = new Matrix();
                                    matrix.postRotate(90);
                                    imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                                    imageBitmap = resizeImage(imageBitmap);
                                    mySelfy.setImageBitmap(imageBitmap);
                                } else {
                                    mySelfy.setImageBitmap(imageBitmap);
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Log.e("uri in else", "" + ImageUri);
                            Bitmap resized = null;
                            try {
                                // Uri outputFileUri =
                                // Uri.fromFile(sdImageMainDirectory);
                                resized = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageUri);
                                if (resized.getWidth() > resized.getHeight()) {
                                    Matrix matrix = new Matrix();
                                    matrix.postRotate(90);
                                    imageBitmap = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, true);
                                    mySelfy.setImageBitmap(imageBitmap);
                                }
                                else
                                {
                                    mySelfy.setImageBitmap(resized);
                                }
//                                imageBitmap = resizeImage(imageBitmap);
                                // filePath = app.encodeTobase64(resized, 100);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            case Constants.SELECT_PICTURE:
                try {
                    Log.e("camera","gallery");
                    imageBitmap = null;
                    mImageCaptureUri = data.getData();
                    imageBitmap = Utils.decodeUri(mImageCaptureUri, EditMyProfileView.this);

                    if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(270);
                        imageBitmap = Bitmap.createBitmap(imageBitmap , 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                    }
                    imageBitmap = resizeImage(imageBitmap);
                    mySelfy.setImageBitmap(imageBitmap);

//                    authManager.setUserPic(imageBitmap.toString());
//                    authManager.setMenuUserInfoFlag(true);


                    /* check code prafull*/




                    Bitmap resized;
                    if (imageBitmap.getWidth() >= imageBitmap.getHeight()) {

                        resized = Bitmap.createBitmap(
                                imageBitmap,
                                imageBitmap.getWidth() / 2 - imageBitmap.getHeight() / 2,
                                0,
                                118,
                                118
                        );

                    } else {

                        resized = Bitmap.createBitmap(
                                imageBitmap,
                                0,
                                imageBitmap.getHeight() / 2 - imageBitmap.getWidth() / 2,
                                118,
                                118
                        );
                    }

                    mySelfy.setImageBitmap(imageBitmap);



                    /* check code prafull*/

                    //authManager.setUserimageuri(mImageCaptureUri);
                    //authManager.setUserPic(Utils.decodeUri(mImageCaptureUri,EditMyProfileView.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            default:
                break;
        }
    }
    public void setCameraIntentUri(){
        File root = new File(Environment
                .getExternalStorageDirectory()
                + File.separator + "myDir" + File.separator);
        root.mkdirs();

        File sdImageMainDirectory = new File(root, "samplepic");
        ImageUri = Uri.fromFile(sdImageMainDirectory);
    }
    private Bitmap resizeImage(Bitmap resized)
    {
        int height = resized.getHeight();
        int width = resized.getWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int deviceHeight = Math.round(Utils.convertDpToPixel(118,this));
        int deviceWidth = Math.round(Utils.convertDpToPixel(118,this));

//        Uri cameraCaptureUri = app.getImageUri(resized, 100);

        int resizeWidth = deviceWidth;
        int resizeHeight = height / deviceHeight * deviceWidth;
        if(height < deviceHeight){
            resizeHeight = height;
        }
        if(width < resizeWidth){
            resizeWidth = width;
        }
        Bitmap imageBitmap = Bitmap.createBitmap(resized , 0, 0, resizeWidth, resizeHeight);
        return imageBitmap;
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
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String getMsg) {
        Log.d(TAG, "onEventMainThread->" + getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("UpdateProfile True")) {
            authManager.setEditProfileFlag(true);
            authManager.setMenuUserInfoFlag(true);
            imageBitmap = null;
            finish();
        } else if (getMsg.equalsIgnoreCase("UpdateProfile False")) {
            Utils.dismissBarDialog();
            // authManager.setEditProfileFlag(false);
        } else if (getMsg.equalsIgnoreCase("UpdateProfile Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(EditMyProfileView.this, AlertMessage.connectionError);
        }
    }

    public Bitmap rotateImage(Bitmap bitmap,int degree)
    {
        Matrix matrix = new Matrix();

        matrix.postRotate(degree);

        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,0,0,true);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
        return rotatedBitmap;
    }



    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

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
