package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.FileNotFoundException;

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
    private Uri mImageCaptureUri;
    private Bitmap imageBitmap = null;

    //variables use to maintain current values of user so we can set values later on in auth manager
    private String userName, userLastName, userEmail, userCity, userCountry;
    private Uri userImageUri;


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
            userName=names[0];
            myName.setText("" + names[0]);
            userLastName=names[1];
            myLast.setText("" + names[1]);
            userEmail=authManager.getEmailId();
            myEmail.setText(userEmail);
            userCity=authManager.getUserCity();
            myCity.setText(userCity);
            userCountry=authManager.getUserCountry();
            myCountry.setText(userCountry);
        } catch (Exception e) {
        }

     /*   try {
            Picasso.with(EditMyProfileView.this).load(authManager.getUserPic())
                    .skipMemoryCache()
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(mySelfy);
        } catch (Exception e) {
        }*/

        //set image from uri directly
        String dtails = "";
        try {
            try {
                Log.e(TAG,"Gender -->"+authManager.getGender());
                if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().matches("girl")) {
                    dtails = "Female";
                } else if (!Utils.isEmptyString(authManager.getGender()) && authManager.getGender().matches("guy")) {
                    dtails = "Male";
                }
            }catch (Exception e){}
          //  dtails =  dtails+Utils.getCurrentYear(authManager.getdOB()) + " " + getResources().getString(R.string.txt_yold);
        }catch (Exception e){}

        try {
            Uri tempUri=authManager.getUserImageUri();
            if(tempUri!=null){
                imageBitmap = Utils.decodeUri(tempUri, this);
                if(imageBitmap!=null)
                    mySelfy.setImageBitmap(imageBitmap);
                else{
                    try{
                        if(dtails.equalsIgnoreCase("Male")){
                            Picasso.with(this)
                                    .load(authManager.getUserPic())
                                    .skipMemoryCache()
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.male_user)
                                    .into(mySelfy);
                        }else if(dtails.equalsIgnoreCase("Female")) {
                            Picasso.with(this)
                                    .load(authManager.getUserPic())
                                    .skipMemoryCache()
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.female_user)
                                    .into(mySelfy);
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

            }else{
                try{
                    if(dtails.equalsIgnoreCase("Male")){
                        Picasso.with(this)
                                .load(authManager.getUserPic())
                                .skipMemoryCache()
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.male_user)
                                .into(mySelfy);
                    }else if(dtails.equalsIgnoreCase("Female")) {
                        Picasso.with(this)
                                .load(authManager.getUserPic())
                                .skipMemoryCache()
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.female_user)
                                .into(mySelfy);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//
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
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
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
                        Utils.launchBarDialog(EditMyProfileView.this);
                        try {
                            authManager = ModelManager.getInstance().getAuthorizationManager();
                            profileManager = ModelManager.getInstance().getProfileManager();
                            userName=myName.getText().toString();
                            userLastName=myLast.getText().toString();
                            userEmail=myEmail.getText().toString();
                            userCity=myCity.getText().toString();
                            userCountry=myCountry.getText().toString();
                            if (imageBitmap != null) {
                                profileManager.setProfile(userName, userLastName, authManager.getPhoneNo(),
                                        authManager.getUsrToken(), "", "", userCity,userCountry ,userEmail , "", Utils.encodeTobase64(imageBitmap));
                            } else {
                                Log.e(TAG, "btn_click_to_save2");
                                try {
                                    // imageBitmap = Picasso.with(EditMyProfileView.this).load(authManager.getUserPic()).get();
                                    //Utils.encodeTobase64(imageBitmap)
                                    profileManager.setProfile(userName, userLastName, authManager.getPhoneNo(),
                                            authManager.getUsrToken(), "", "", userCity, userCountry, userEmail, "", "");
                                } catch (Exception e) {
                                    Log.e(TAG, "1" + e.toString());
                                }
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "2" + e.toString());
                        }
                    }else {
                        Utils.showAlert(EditMyProfileView.this, AlertMessage.vEmailid);
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

                try {
                   imageBitmap = null;
                   mImageCaptureUri = null;
                   mImageCaptureUri = data.getData();
                   imageBitmap = Utils.decodeUri(mImageCaptureUri, EditMyProfileView.this);
                    mySelfy.setImageBitmap(imageBitmap);
                    userImageUri=mImageCaptureUri;
                 //   authManager.setUserPic(imageBitmap.toString());
                   // authManager.setUserImageUri(mImageCaptureUri);
                    authManager.setMenuUserInfoFlag(true);
                   // authManager.setUserimageuri(mImageCaptureUri);
                        Log.e(TAG ,"EXception is <><><><><><><><><><><><><><><> " +"" +mImageCaptureUri);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case Constants.SELECT_PICTURE:
                try {
                    imageBitmap = null;
                    mImageCaptureUri = data.getData();
                    imageBitmap = Utils.decodeUri(mImageCaptureUri, EditMyProfileView.this);
                    mySelfy.setImageBitmap(imageBitmap);
                    userImageUri=mImageCaptureUri;
                   // authManager.setUserPic(imageBitmap.toString());

                    authManager.setMenuUserInfoFlag(true);
                   // authManager.setUserImageUri(mImageCaptureUri);
                   //authManager.setUserimageuri(mImageCaptureUri);
                    //authManager.setUserPic(Utils.decodeUri(mImageCaptureUri,EditMyProfileView.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            default:
                break;
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

            EventBus.getDefault().unregister(this);

    }

    public void onEventMainThread(String getMsg) {
        Log.d(TAG, "onEventMainThread->" + getMsg);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getMsg.equalsIgnoreCase("UpdateProfile True")) {
            Utils.dismissBarDialog();
            //update user profile information in auth manager now
            authManager.setUserName(userName+" "+userLastName);
            authManager.setEmailId(userEmail);
            authManager.setUserCity(userCity);
            authManager.setUserCountry(userCountry);
            authManager.setUserImageUri(userImageUri);
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

}

