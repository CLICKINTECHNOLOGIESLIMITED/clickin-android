package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileNotFoundException;
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
      private Uri mImageCaptureUri;


      //variables use to maintain current values of user so we can set values later on in auth manager
      private String userName, userLastName, userEmail, userCity, userCountry;
      private Uri userImageUri;

<<<<<<< HEAD

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
=======
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
            userName = names[0];
            myName.setText("" + names[0]);
            userLastName = names[1];
            myLast.setText("" + names[1]);
            userEmail = authManager.getEmailId();
            myEmail.setText(userEmail);
            userCity = authManager.getUserCity();
            myCity.setText(userCity);
            userCountry = authManager.getUserCountry();
            myCountry.setText(userCountry);
        } catch (Exception e) {
        }

        Uri tempUri = authManager.getUserImageUri();
        if (tempUri != null) {
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88
            try {
                  String[] names = (authManager.getUserName().split("\\s+", 2));
                  userName = names[0];
                  myName.setText("" + names[0]);
                  userLastName = names[1];
                  myLast.setText("" + names[1]);
                  userEmail = authManager.getEmailId();
                  myEmail.setText(userEmail);
                  userCity = authManager.getUserCity();
                  myCity.setText(userCity);
                  userCountry = authManager.getUserCountry();
                  myCountry.setText(userCountry);
            } catch (Exception e) {
            }

            Uri tempUri = authManager.getUserImageUri();
            if (tempUri != null) {
                  try {
                        Bitmap imageBitmap = authManager.getUserbitmap();
                        if (imageBitmap != null)
                              mySelfy.setImageBitmap(imageBitmap);
                        else {

                              if (authManager.getGender() != null) {
                                    Log.e("UserPic", authManager.getUserPic());
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
                              } else {
                                    mySelfy.setImageResource(R.drawable.male_user);
                              }
                        }

                  } catch (Exception e) {
                        e.printStackTrace();
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
                                                  .into(mySelfy);
                                    } else {
                                          mySelfy.setImageResource(R.drawable.male_user);
                                    }
                              } catch (Exception e) {
                                    e.printStackTrace();
                                    mySelfy.setImageResource(R.drawable.male_user);
                              }
                        } else if (authManager.getGender().equalsIgnoreCase("girl")) {
                              try {
                                    if (!authManager.getUserPic().equalsIgnoreCase("")) {
                                          Picasso.with(this)
                                                  .load(authManager.getUserPic())
                                                  .skipMemoryCache()

                                                  .error(R.drawable.female_user)
                                                  .into(mySelfy);
                                    } else {
                                          mySelfy.setImageResource(R.drawable.female_user);
                                    }
                              } catch (Exception e) {
                                    e.printStackTrace();
                                    mySelfy.setImageResource(R.drawable.female_user);
                              }
                        }

                  } else {
                        mySelfy.setImageResource(R.drawable.male_user);
                  }

            }

//
<<<<<<< HEAD
      }

      @Override
      public void onClick(View v) {
            switch (v.getId()) {
                  case R.id.iv_edit_gallery:
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                        break;
                  case R.id.iv_menu:
                        finish();
                        break;
                  case R.id.iv_edit_camera:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
=======
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_gallery:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                break;
            case R.id.iv_menu:
                finish();
                break;
            case R.id.iv_edit_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88

                mImageCaptureUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra("return-data", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

<<<<<<< HEAD
                        // start the image capture Intent
                        startActivityForResult(intent, Constants.CAMERA_REQUEST);
                        break;
                  case R.id.btn_click_to_save:
                        if (updateProfileValidation()) {
                              if (Utils.isEmailValid(myEmail.getText().toString())) {
                                    Log.e(TAG, "btn_click_to_save");
                                    Utils.launchBarDialog(EditMyProfileView.this);
                                    try {
                                          authManager = ModelManager.getInstance().getAuthorizationManager();
                                          profileManager = ModelManager.getInstance().getProfileManager();
                                          userName = myName.getText().toString();
                                          userLastName = myLast.getText().toString();
                                          userEmail = myEmail.getText().toString();
                                          userCity = myCity.getText().toString();
                                          userCountry = myCountry.getText().toString();


                                          ImageView im = (ImageView) findViewById(R.id.iv_selfi);
                                          Bitmap bitmap = Bitmap.createBitmap(im.getWidth(), im.getHeight(), Bitmap.Config.ARGB_8888);
                                          Canvas c = new Canvas(bitmap);
                                          im.getDrawable().draw(c);
                                          if (bitmap != null) {
                                                profileManager.setProfile(userName, userLastName, authManager.getPhoneNo(),
                                                                                 authManager.getUsrToken(), "", "", userCity, userCountry, userEmail, "", Utils.encodeTobase64(bitmap));
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
=======
                // start the image capture Intent
                startActivityForResult(intent, Constants.CAMERA_REQUEST);
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

                                ImageView im = (ImageView) findViewById(R.id.iv_selfi);
                                Bitmap bitmap = Bitmap.createBitmap(im.getWidth(), im.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas c = new Canvas(bitmap);
                                im.getDrawable().draw(c);

                                profileManager.setProfile(userName, userLastName, authManager.getPhoneNo(),
                                        authManager.getUsrToken(), "", "", userCity, userCountry, userEmail, "", Utils.encodeTobase64(bitmap));
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
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88


                                    } catch (Exception e) {
                                          Log.e(TAG, "2" + e.toString());
                                    }
                              } else {
                                    Utils.showAlert(EditMyProfileView.this, AlertMessage.vEmailid);
                              }
                        }

                        break;
            }
      }

<<<<<<< HEAD
      /* test code prafull */
      public static final int MEDIA_TYPE_IMAGE = 1;
      private static final String IMAGE_DIRECTORY_NAME = "FootGloryFlow Application";
=======
    /* test code prafull */
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "FootGloryFlow Application";
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88

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

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
      /* test code prafull */


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

<<<<<<< HEAD
      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data) {
=======
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) switch (requestCode) {
            case Constants.CAMERA_REQUEST:
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88


<<<<<<< HEAD
            if (Constants.CAMERA_REQUEST == requestCode && resultCode == RESULT_OK) {

=======
                imageBitmap = BitmapFactory.decodeFile(mImageCaptureUri.getPath(), new BitmapFactory.Options());

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
                    Log.e("angle from camera 1 --->", "" + angle);
                              /*bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);*/
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88

                  Bitmap bitmap = BitmapFactory.decodeFile(mImageCaptureUri.getPath(), new BitmapFactory.Options());
                  BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                  Bitmap bitmap1;
                  bmpFactoryOptions.inSampleSize = 2;
                  bmpFactoryOptions.outWidth = bitmap.getWidth();
                  bmpFactoryOptions.outHeight = bitmap.getHeight();
                  bmpFactoryOptions.inJustDecodeBounds = false;
                  bitmap1 = BitmapFactory.decodeFile(mImageCaptureUri.getPath(), bmpFactoryOptions);

<<<<<<< HEAD
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
                        Log.e("angle from camera 1 --->", "" + angle);

                        Bitmap resize;
                        resize = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), mat, true);
                        if (resize != null) {

                              try {
                                    authManager.setOrginalBitmap(resize);
                                    Intent intent = new Intent(EditMyProfileView.this, CropView.class);
                                    intent.putExtra("from", "fromcamera");
                                    intent.putExtra("uri", mImageCaptureUri.toString());
                                    startActivityForResult(intent, Constants.CROP_PICTURE);
                              } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("exception--->", "exception--->");
                              }
                        }
                        userImageUri = mImageCaptureUri;
                        /*authManager.setUserImageUri(userImageUri);*/
                        // authManager.setUserPic(imageBitmap.toString());
                        mImageCaptureUri = null;
                        authManager.setMenuUserInfoFlag(true);
=======
                    Bitmap resized;
                    if (imageBitmap.getWidth() >= imageBitmap.getHeight()) {

                        resized = Bitmap.createBitmap(
                                imageBitmap,
                                imageBitmap.getWidth() / 2 - imageBitmap.getHeight() / 2,
                                0,
                                imageBitmap.getHeight(),
                                imageBitmap.getHeight(), mat, true
                        );

                    } else {

                        resized = Bitmap.createBitmap(
                                imageBitmap,
                                0,
                                imageBitmap.getHeight() / 2 - imageBitmap.getWidth() / 2,
                                imageBitmap.getWidth(),
                                imageBitmap.getWidth(), mat, true
                        );
                    }
                    imageBitmap.recycle();

                    mySelfy.setImageBitmap(resized);
                    authManager.setUserbitmap(resized);

                    userImageUri = mImageCaptureUri;
                    authManager.setUserImageUri(userImageUri);
                    // authManager.setUserPic(imageBitmap.toString());
                    mImageCaptureUri = null;
                    authManager.setMenuUserInfoFlag(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
/*  test code prafull */
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88

                  } catch (Exception e) {
                        e.printStackTrace();
                  }

<<<<<<< HEAD
=======
                break;
            case Constants.SELECT_PICTURE:
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88

            }
            if (Constants.SELECT_PICTURE == requestCode && resultCode == RESULT_OK) {


<<<<<<< HEAD
                  Bitmap bitmap = getBitmapFromCameraData(data, getApplicationContext());
=======
                imageBitmap = getBitmapFromCameraData(data, getApplicationContext());
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88


                 /*    pick image from gallery  */
                  BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                  Bitmap bitmap1;
                  bmpFactoryOptions.inSampleSize = 2;
                  bmpFactoryOptions.outWidth = bitmap.getWidth();
                  bmpFactoryOptions.outHeight = bitmap.getHeight();
                  bmpFactoryOptions.inJustDecodeBounds = false;
                  bitmap1 = BitmapFactory.decodeFile(getRealPathFromURI(data.getData()), bmpFactoryOptions);

                  try {
                        ExifInterface ei = new ExifInterface(getRealPathFromURI(data.getData()));
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


<<<<<<< HEAD
                        Log.e("angle from gallery --->", "" + angle);

                        bitmap = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), mat, true);


                  } catch (Exception e) {
                        e.printStackTrace();
                  }
=======
                try {
                    ExifInterface ei = new ExifInterface(getRealPathFromURI(data.getData()));
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


                    Log.e("angle from gallery --->", "" + angle);
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88



                  /*      pick image from gallery    */


                  /*Bitmap resized = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);;*/
                  /*if (bitmap.getWidth() >= bitmap.getHeight()) {

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
                  }*/

<<<<<<< HEAD




                  /*bitmap.recycle();*/


                  userImageUri = data.getData();
                  if (bitmap != null) {
                        try {
                              authManager.setOrginalBitmap(null);
                              authManager.setOrginalBitmap(bitmap);
                              Intent intent = new Intent(EditMyProfileView.this, CropView.class);
                              intent.putExtra("from", "fromgallery");
                              intent.putExtra("uri", userImageUri.toString());
                              startActivityForResult(intent, Constants.CROP_PICTURE);
                        } catch (Exception e) {
                              e.printStackTrace();
                              Log.e("exception--->", "exception--->");
                        }
                  }
=======
                    Bitmap resized1;
                    if (imageBitmap.getWidth() >= imageBitmap.getHeight()) {

                        resized1 = Bitmap.createBitmap(
                                imageBitmap,
                                imageBitmap.getWidth() / 2 - imageBitmap.getHeight() / 2,
                                0,
                                imageBitmap.getHeight(),
                                imageBitmap.getHeight(), mat, true
                        );

                    } else {

                        resized1 = Bitmap.createBitmap(
                                imageBitmap,
                                0,
                                imageBitmap.getHeight() / 2 - imageBitmap.getWidth() / 2,
                                imageBitmap.getWidth(),
                                imageBitmap.getWidth(), mat, true
                        );
                    }
                    imageBitmap.recycle();

                    mySelfy.setImageBitmap(resized1);
                    authManager.setUserbitmap(resized1);
                    userImageUri = data.getData();
                    authManager.setUserImageUri(userImageUri);
                    // authManager.setUserPic(imageBitmap.toString());

                    authManager.setMenuUserInfoFlag(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88



<<<<<<< HEAD
                  /*authManager.setUserImageUri(userImageUri);*/
                  // authManager.setUserPic(imageBitmap.toString());

                  authManager.setMenuUserInfoFlag(true);


            }
            if (Constants.CROP_PICTURE == requestCode && resultCode == RESULT_OK) {
=======
            default:
                break;
        }
    }
>>>>>>> 2cedcf82e7488aa1c84e44e314a6cf164b46db88

                  if (data.getStringExtra("retake").equalsIgnoreCase("camare")) {
                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mImageCaptureUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        intent1.putExtra("return-data", true);
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(intent1, Constants.CAMERA_REQUEST);
                  } else if (data.getStringExtra("retake").equalsIgnoreCase("gallery")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                  } else if (authManager.getmResizeBitmap() != null) {
                        mySelfy.setImageBitmap(authManager.getmResizeBitmap());
                        /*Bitmap imageBitmap = authManager.getmResizeBitmap();
                        authManager.setUserbitmap(authManager.getmResizeBitmap());
                        authManager.setOrginalBitmap(null);
                        authManager.setmResizeBitmap(null);*/
                  }
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
                  authManager.setUserName(userName + " " + userLastName);
                  authManager.setEmailId(userEmail);
                  authManager.setUserCity(userCity);
                  authManager.setUserCountry(userCountry);
                  authManager.setEditProfileFlag(true);
                  authManager.setMenuUserInfoFlag(true);

                  authManager.setUserbitmap(authManager.getmResizeBitmap());
                  authManager.setUserImageUri(userImageUri);
                  authManager.setOrginalBitmap(null);
                  authManager.setmResizeBitmap(null);
                  finish();
            } else if (getMsg.equalsIgnoreCase("UpdateProfile False")) {
                  Utils.dismissBarDialog();
                  authManager.setUserbitmap(null);
                  authManager.setUserImageUri(null);
                  authManager.setOrginalBitmap(null);
                  authManager.setmResizeBitmap(null);
            } else if (getMsg.equalsIgnoreCase("UpdateProfile Network Error")) {
                  Utils.dismissBarDialog();
                  authManager.setUserbitmap(null);
                  authManager.setUserImageUri(null);
                  authManager.setOrginalBitmap(null);
                  authManager.setmResizeBitmap(null);
                  Utils.showAlert(EditMyProfileView.this, AlertMessage.connectionError);
            }
      }


}

