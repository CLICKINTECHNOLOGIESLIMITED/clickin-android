package com.sourcefuse.clickinandroid.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class ProfileView extends Activity implements OnClickListener, TextWatcher {
    public static final String[] MONTHS = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int DATE_DIALOG_ID = 9990;
    private static final String IMAGE_DIRECTORY_NAME = "FootGloryFlow Application";
    long diffrence_in_mills;
    long mills_in_17yrs;
    private String TAG = this.getClass().getSimpleName();
    private EditText fname, lname, city, country, email;
    private TextView tvDate, tvMonth, tvYear;
    private LinearLayout datepicker;
    private Button done, guy, girl, connectWithFb;
    private Uri mImageCaptureUri;
    private DatePicker dpResult;
    private ImageView profileimg;
    private Bitmap bitmapImage;
    private int year;
    private int month;
    private int day;
    private int mCurrentyear;
    private String gender_var = "";
    private AuthManager authManager;
    private ProfileManager profileManager;
    private Uri userImageUri;
    private SimpleDateFormat mSimpleDateFormat;
    private int age;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            tvDate.setText("" + day);
            tvMonth.setText("" + MONTHS[month]);
            tvYear.setText("" + year);
            dpResult.init(year, month, day, null);

            Date start_date = null;
            String date = day + "/" + month + "/" + year;
            long time = 0;
            mSimpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            PeriodFormatter mPeriodFormat = new PeriodFormatterBuilder().appendYears()
                    .appendSuffix(" year(s) ").appendMonths().appendSuffix(" month(s) ")
                    .appendDays().appendSuffix(" day(s) ").printZeroNever().toFormatter();

            try {
                start_date = mSimpleDateFormat.parse(date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                time = mSimpleDateFormat.parse(date.toString()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long present = System.currentTimeMillis();
            diffrence_in_mills = Math.abs(time - present);

      /* code for age prafull */

            age = getAge(selectedYear, selectedMonth, selectedDay);
            android.util.Log.e(TAG, "Age Difference " + age);
//            DateTime start  = new DateTime(start_date);
//            DateTime end = new DateTime(new Date());
//
//            diffrence_in_mills = end.getMillis() - start.getMillis();
            long MILLISECONDS_IN_YEAR = (long) 1000 * 60 * 60 * 24 * 365;
            mills_in_17yrs = 17 * MILLISECONDS_IN_YEAR;
//            android.util.Log.e(TAG,"Mill in 17 years" +mills_in_17yrs);
//            periods_years = Years.yearsBetween(start,end);
//           // android.util.Log.e(TAG ,"Actual Difference in years " + PeriodFormat.wordBased().print(periods_years));


        }
    };
    //Methods for Facebook
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_profile);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


        done = (Button) findViewById(R.id.btn_done);
        guy = (Button) findViewById(R.id.btn_guy);
        girl = (Button) findViewById(R.id.btn_girl_btn);
        connectWithFb = (Button) findViewById(R.id.btn_connentwithfb);

        fname = (EditText) findViewById(R.id.etd_fname);
        lname = (EditText) findViewById(R.id.edt_lname);
        city = (EditText) findViewById(R.id.edt_city);
        country = (EditText) findViewById(R.id.edt_cntry);
        email = (EditText) findViewById(R.id.edt_emailid);
        datepicker = (LinearLayout) findViewById(R.id.ll_date_picker);
        profileimg = (ImageView) findViewById(R.id.iv_profile_img);
        profileimg.setScaleType(ScaleType.FIT_XY);

        fname.addTextChangedListener(this);
        lname.addTextChangedListener(this);
        email.addTextChangedListener(this);
        //city.addTextChangedListener(this);
        //country.addTextChangedListener(this);


        done.setOnClickListener(this);
        guy.setOnClickListener(this);
        girl.setOnClickListener(this);
        datepicker.setOnClickListener(this);
        profileimg.setOnClickListener(this);
        connectWithFb.setOnClickListener(this);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvMonth = (TextView) findViewById(R.id.tv_month);
        tvYear = (TextView) findViewById(R.id.tv_year);

        dpResult = (DatePicker) findViewById(R.id.dpResult);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        dpResult.init(year, month, day, null);
        setCurrentDateOnView();
        authManager = ModelManager.getInstance().getAuthorizationManager();


        // akshit code for closing keypad if touched anywhere outside
        ((RelativeLayout) findViewById(R.id.relative_layout_root_profile)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fname.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(lname.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(city.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(country.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(guy.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(girl.getWindowToken(), 0);

            }

        });
        new LoadContacts().execute();
//ends
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (fname.getText().toString().length() > 1
                && lname.getText().toString().length() > 1
                && email.getText().toString().length() > 1 && Utils.isEmailValid(email.getText().toString()) && ((mCurrentyear - year) <= 17)) {
            done.setBackgroundResource(R.drawable.c_next_active);
        } else {
            done.setBackgroundResource(R.drawable.c_next_active);
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

    public void onEventMainThread(String message) {
        if (message.equalsIgnoreCase("UpdateProfile True")) {
            Utils.dismissBarDialog();
            bitmapImage = null;
            //android.util.Log.e("Utils.DeleteImage","Utils.DeleteImage--->"+	Utils.DeleteImage(mImageCaptureUri,ProfileView.this));
            switchView();
        } else if (message.equalsIgnoreCase("UpdateProfile False")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, authManager.getMessage());
            //Utils.showAlert(ProfileView.this, authManager.getMessage());
        } else if (message.equalsIgnoreCase("UpdateProfile Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            //alertDialog(AlertMessage.connectionError);
            //Utils.showAlert(this, AlertMessage.connectionError);
        }

    }

    private void switchView() {
        Intent intent = new Intent(ProfileView.this, PlayItSafeView.class);
        intent.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        switch (v.getId()) {
            case R.id.btn_done:
                String name = fname.getText().toString() + " " + lname.getText().toString();
                Bitmap bitmap;
                if (updateProfileValidation()) {
                    authManager.setUserName(name);


                    //akshit code start to set default pics for male,female and if no gender
                    if (bitmapImage == null) {
                        try {
                            if (gender_var.equalsIgnoreCase("guy")) {
                                bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.male_user);
                            } else if (gender_var.equalsIgnoreCase("girl")) {
                                bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.female_user);

                            } else {
                                bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.male_user);
                            }

//                            ImageView im = (ImageView) findViewById(R.id.iv_profile_img);
//                            bitmap = Bitmap.createBitmap(im.getWidth(), im.getHeight(), Bitmap.Config.ARGB_8888);
//                            Canvas c = new Canvas(bitmap);
//                            im.getDrawable().draw(c);
                        } catch (Exception e) {
                        }
                        if (Utils.isEmailValid(email.getText().toString())) {
                            Utils.launchBarDialog(ProfileView.this);
                            authManager.setEmailId(email.getText().toString());
                            profileManager = ModelManager.getInstance().getProfileManager();
                            String cityStr = "";
                            String countryStr = "";
                            if (city.getText() != null) {
                                cityStr = city.getText().toString();
                            }

                            if (country.getText() != null) {
                                countryStr = country.getText().toString();
                            }
                            profileManager.setProfile(fname.getText().toString(), lname.getText().toString(), authManager.getPhoneNo(),
                                    authManager.getUsrToken(), gender_var, "" + day + month + year, cityStr, countryStr, email.getText().toString(), "", Utils.encodeTobase64(bitmapImage));
                        } else {
                            Utils.fromSignalDialog(this, AlertMessage.vEmailid);
                            //alertDialog(AlertMessage.vEmailid);
                            //Utils.showAlert(ProfileView.this, AlertMessage.vEmailid);
                        }
                    } else {
                        ImageView im = (ImageView) findViewById(R.id.iv_profile_img);

                        bitmap = Bitmap.createBitmap(im.getWidth(), im.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(bitmap);
                        if (im.getDrawable() != null)
                            im.getDrawable().draw(c);

                        if (Utils.isEmailValid(email.getText().toString())) {
                            Utils.launchBarDialog(ProfileView.this);
                            authManager.setEmailId(email.getText().toString());
                            profileManager = ModelManager.getInstance().getProfileManager();
                            String cityStr = "";
                            String countryStr = "";
                            if (city.getText() != null) {
                                cityStr = city.getText().toString();
                            }

                            if (country.getText() != null) {
                                countryStr = country.getText().toString();
                            }
                            profileManager.setProfile(fname.getText().toString(), lname.getText().toString(), authManager.getPhoneNo(),
                                    authManager.getUsrToken(), gender_var, "" + day + month + year, cityStr, countryStr, email.getText().toString(), "", Utils.encodeTobase64(bitmap));
                        } else {

                            Utils.fromSignalDialog(this, AlertMessage.vEmailid);
                            //Utils.showAlert(ProfileView.this, AlertMessage.vEmailid);
                        }

                    }
                }
                break;
            case R.id.btn_guy:
                gender_var = "guy";
                authManager.setGender("guy");
                guy.setBackgroundResource(R.drawable.c_pink_guy);
                girl.setBackgroundResource(R.drawable.c_grey_girl);
                break;
            case R.id.btn_girl_btn:
                gender_var = "girl";
                authManager.setGender("girl");
                guy.setBackgroundResource(R.drawable.c_grey_guy);
                girl.setBackgroundResource(R.drawable.c_pink_girl);
                break;
            case R.id.ll_date_picker:
                showDialog(DATE_DIALOG_ID);
                break;
            case R.id.iv_profile_img:
                imageDialog();
                break;

            case R.id.btn_connentwithfb:
            /*
             * Connect With FB only session is create in this Activity
			 */

                if (Utils.isConnectingToInternet(ProfileView.this)) {

                    Session session = Session.getActiveSession();
                    if (session == null) {
                        if (session == null) {
                            session = new Session(this);
                        }
                        Session.setActiveSession(session);
                    }
                    if (!session.isOpened() && !session.isClosed()) {
                        session.openForRead(new Session.OpenRequest(this).setCallback(callback).setPermissions("user_birthday", "basic_info", "email", "user_location"));
                    } else {
                        Session.openActiveSession(this, true, callback);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {
        if (state.isOpened()) {
            GetFBData(session);
        } else if (state.isClosed()) {
            System.out.println("Logged out...");
        }
    }

    //Get FB Data
    public void GetFBData(Session session) {
        // TODO Auto-generated method stub
        //Sign Up with Facebook to get details
        //User email, name, gender, DOB, Profile Pic, Access token

        final String access_Token = session.getAccessToken();
        android.util.Log.d(TAG, access_Token);
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {

                    android.util.Log.e(TAG, String.valueOf(user));
                    try {
                        android.util.Log.d("user email", user.getInnerJSONObject().getString("email"));
                        email.setText(user.getInnerJSONObject().getString("email"));
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        if (user.getProperty("gender").equals("male")) {
                            gender_var = "guy";
                            guy.setBackgroundResource(R.drawable.c_pink_guy);
                            girl.setBackgroundResource(R.drawable.c_grey_girl);
                        } else {
                            gender_var = "girl";
                            guy.setBackgroundResource(R.drawable.c_grey_guy);
                            girl.setBackgroundResource(R.drawable.c_pink_girl);
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    try {

                        //  android.util.Log.d(TAG, user.getLocation().getProperty("name").toString());
                        String userLocationName = user.getLocation().getProperty("name").toString();

                        String[] citynCountry = userLocationName.split(",");

                        city.setText(citynCountry[0]);
                        country.setText(citynCountry[1].substring(1));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        //userdata.put("name", user.getFirstName()+" "+user.getLastName());
                        fname.setText(user.getFirstName());
                        lname.setText(user.getLastName());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    try {
                        android.util.Log.e("dob", user.getBirthday());

                        String[] dob = user.getBirthday().split("/");
                        year = Integer.valueOf(dob[2]);
                        month = Integer.valueOf(dob[0]);
                        day = Integer.valueOf(dob[1]);

                        age = getAge(year, month, day);


                        tvDate.setText("" + day);
                        tvMonth.setText("" + MONTHS[month - 1]);
                        tvYear.setText("" + (year));
                        dpResult.init(year, month, day, null);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    try {


                        LoadImage task = new LoadImage();
                        String url = "https://graph.facebook.com/" + user.getId() + "/picture?type=large";
                        //      authManager.setUserPic(url);
                        task.execute(url);


                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    Log.i("Facebook::: ", "Logged out...");
                }

            }
        });

        Request.executeBatchAsync(request);
    }

    public void imageDialog() {

        final Dialog mdialog = new Dialog(ProfileView.this);
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mdialog.setContentView(R.layout.alert_take_picture);
        Button cancel = (Button) mdialog.findViewById(R.id.dialog_cancel);
        TextView textcamera = (TextView) mdialog.findViewById(R.id.take_picture);
        TextView textgallery = (TextView) mdialog.findViewById(R.id.from_gallery);
        textcamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra("return-data", true);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                // start the image capture Intent
                startActivityForResult(intent, Constants.CAMERA_REQUEST);

                mdialog.dismiss();
            }
        });
        textgallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                mdialog.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mdialog.dismiss();
            }
        });
        mdialog.show();
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
            } catch (Exception e) {
            }

            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case Constants.CAMERA_REQUEST:
//                        mImageCaptureUri = data.getData();
//                        bitmapImage = Utils.decodeUri(mImageCaptureUri, ProfileView.this);
//                        profileimg.setImageBitmap(bitmapImage);
/*test code akshit */
                        Bitmap bitmap = BitmapFactory.decodeFile(mImageCaptureUri.getPath(), new BitmapFactory.Options());
                        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                        Bitmap bitmap1;
                        bmpFactoryOptions.inSampleSize = 2;
                        bmpFactoryOptions.outWidth = bitmap.getWidth();
                        bmpFactoryOptions.outHeight = bitmap.getHeight();
                        bmpFactoryOptions.inJustDecodeBounds = false;
                        bitmapImage = BitmapFactory.decodeFile(mImageCaptureUri.getPath(), bmpFactoryOptions);

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
                            android.util.Log.e("angle from camera 1 --->", "" + angle);
                              /*bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);*/


                                          /*Bitmap resized;
                                          if (bitmapImage.getWidth() >= bitmapImage.getHeight()) {

                                                resized = Bitmap.createBitmap(
                                                                                     bitmapImage,
                                                                                     bitmapImage.getWidth() / 2 - bitmapImage.getHeight() / 2,
                                                                                     0,
                                                                                     bitmapImage.getHeight(),
                                                                                     bitmapImage.getHeight(), mat, true
                                                );

                                          } else {

                                                resized = Bitmap.createBitmap(
                                                                                     bitmapImage,
                                                                                     0,
                                                                                     bitmapImage.getHeight() / 2 - bitmapImage.getWidth() / 2,
                                                                                     bitmapImage.getWidth(),
                                                                                     bitmapImage.getWidth(), mat, true
                                                );
                                          }*/


                            Bitmap resize;
                            resize = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), mat, true);
                            if (resize != null) {

                                try {
                                    authManager.setOrginalBitmap(resize);
                                    Intent intent = new Intent(ProfileView.this, CropView.class);
                                    intent.putExtra("from", "fromcamera");
                                    intent.putExtra("uri", mImageCaptureUri.toString());
                                    startActivityForResult(intent, Constants.CROP_PICTURE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    android.util.Log.e("exception--->", "exception--->");
                                }
                            }
                                          /*bitmapImage.recycle();
                                          profileimg.setImageBitmap(resized);
                                          userImageUri = mImageCaptureUri;
                                          mImageCaptureUri = null;*/

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constants.SELECT_PICTURE:


                        Bitmap bitmap12 = getBitmapFromCameraData(data, getApplicationContext());


                 /*    pick image from gallery  */
                        BitmapFactory.Options bmpFactoryOptions1 = new BitmapFactory.Options();
                        Bitmap bitmap11;
                        bmpFactoryOptions1.inSampleSize = 2;
                        bmpFactoryOptions1.outWidth = bitmap12.getWidth();
                        bmpFactoryOptions1.outHeight = bitmap12.getHeight();
                        bmpFactoryOptions1.inJustDecodeBounds = false;
                        bitmapImage = BitmapFactory.decodeFile(getRealPathFromURI(data.getData()), bmpFactoryOptions1);

                                    /*bitmapImage = getBitmapFromCameraData(data, getApplicationContext());*/

/*test code akshit */
                 /*    pick image from gallery  */


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


                            android.util.Log.e("angle from gallery --->", "" + angle);

                              /*bitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), mat, true);*/

                            bitmapImage = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), mat, true);

                            userImageUri = data.getData();
                            if (bitmapImage != null) {
                                try {
                                    authManager.setOrginalBitmap(null);
                                    authManager.setOrginalBitmap(bitmapImage);
                                    Intent intent = new Intent(ProfileView.this, CropView.class);
                                    intent.putExtra("from", "fromgallery");
                                    intent.putExtra("uri", userImageUri.toString());
                                    startActivityForResult(intent, Constants.CROP_PICTURE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    android.util.Log.e("exception--->", "exception--->");
                                }
                            }


                  /*      pick image from gallery    */


                                          /*Bitmap resized1;
                                          if (bitmapImage.getWidth() >= bitmapImage.getHeight()) {

                                                resized1 = Bitmap.createBitmap(
                                                                                      bitmapImage,
                                                                                      bitmapImage.getWidth() / 2 - bitmapImage.getHeight() / 2,
                                                                                      0,
                                                                                      bitmapImage.getHeight(),
                                                                                      bitmapImage.getHeight(), mat, true
                                                );

                                          } else {

                                                resized1 = Bitmap.createBitmap(
                                                                                      bitmapImage,
                                                                                      0,
                                                                                      bitmapImage.getHeight() / 2 - bitmapImage.getWidth() / 2,
                                                                                      bitmapImage.getWidth(),
                                                                                      bitmapImage.getWidth(), mat, true
                                                );
                                          }
                                          bitmapImage.recycle();

                                          profileimg.setImageBitmap(resized1);

                                          userImageUri = data.getData();*/
                            //    authManager.setUserImageUri(userImageUri);
                            //  authManager.setUserbitmap(resized1);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;


                    case Constants.CROP_PICTURE:

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
                            profileimg.setImageBitmap(authManager.getmResizeBitmap());

                            authManager.setUserImageUri(userImageUri);
                            authManager.setUserbitmap(authManager.getmResizeBitmap());
                            authManager.setOrginalBitmap(null);
                            authManager.setmResizeBitmap(null);

                                     /*Bitmap imageBitmap = authManager.getmResizeBitmap();
                                          authManager.setUserbitmap(authManager.getmResizeBitmap());
                                          authManager.setOrginalBitmap(null);
                                          authManager.setmResizeBitmap(null);*/
                        }
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

    // display current date
    @SuppressLint("NewApi")
    public void setCurrentDateOnView() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        mCurrentyear = year;
        tvDate.setText("" + day);
        tvMonth.setText("" + MONTHS[month]);
        tvYear.setText("" + year);
        dpResult.init(year, month, day, null);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                DatePickerDialog dialog = new DatePickerDialog(this, datePickerListener, year, month, day);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                return dialog;
            //return new DatePickerDialog(this, datePickerListener, year, month, day);
        }
        return null;
    }

    public int getAge(int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH)) || ((m == cal.get(Calendar.MONTH)) && (d < cal.get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }

        return a > 0 ? a : 0;
    }

/* code for age prafull */

    public boolean updateProfileValidation() {
        android.util.Log.e(TAG, "mCurrentyear" + mCurrentyear + "  year " + year);
        if (fname.getText().toString().length() < 1) {
            Utils.fromSignalDialog(this, AlertMessage.fname);
            // Utils.showAlert(ProfileView.this, AlertMessage.fname);
            return false;
        } else if (lname.getText().toString().length() < 1) {
            Utils.fromSignalDialog(this, AlertMessage.lname);
            // Utils.showAlert(ProfileView.this, AlertMessage.lname);
            return false;
        } else if (email.getText().toString().length() < 1) {
            Utils.fromSignalDialog(this, AlertMessage.emailid);
            //   Utils.showAlert(ProfileView.this, AlertMessage.emailid);
            return false;
        } else if ((mCurrentyear - year) == 0) {
            android.util.Log.e(TAG, "mCurrentyear" + mCurrentyear + "  year " + year);
            Utils.fromSignalDialog(this, AlertMessage.ageValid);
            // Utils.showAlert(ProfileView.this, AlertMessage.ageValid);
            return false;
        }  // else if (periods_years.isLessThan(Years.years(17))) {
        else if (age < 17) {
            android.util.Log.e(TAG, "mCurrentyear" + diffrence_in_mills + "  year " + mills_in_17yrs);
            Utils.fromSignalDialog(this, AlertMessage.UDERAGEMSGII);
            //   Utils.showAlert(ProfileView.this, AlertMessage.UDERAGEMSGII);
            return false;
        }
        return true;
    }

    private Period calcDiff(Date startDate, Date endDate) {
        DateTime START_DT = (startDate == null) ? null : new DateTime(startDate);
        DateTime END_DT = (endDate == null) ? null : new DateTime(endDate);

        Period period = new Period(START_DT, END_DT);

        return period;
    }

    private class LoadImage extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            loading = new ProgressDialog(ProfileView.this);
            loading.setMessage("Loading..");
            loading.setCancelable(false);
            loading.show();

        }

        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                bitmapImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            loading.dismiss();
            profileimg.setScaleType(ScaleType.FIT_XY);
            profileimg.setImageBitmap(bitmapImage);
            Session.getActiveSession().closeAndClearTokenInformation();
        }
    }

    private class LoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new FetchContactFromPhone(ProfileView.this).readContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            new FetchContactFromPhone(ProfileView.this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
        }
    }
}
