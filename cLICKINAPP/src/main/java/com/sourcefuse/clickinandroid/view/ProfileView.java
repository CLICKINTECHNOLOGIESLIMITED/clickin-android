package com.sourcefuse.clickinandroid.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
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
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class ProfileView extends Activity implements View.OnClickListener, TextWatcher {
    private String TAG = this.getClass().getSimpleName();

    public static final String[] MONTHS = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    private EditText fname, lname, city, country, email;
    private TextView tvDate, tvMonth, tvYear;
    private LinearLayout datepicker;
    private Button done, guy, girl, connectWithFb;
    private Uri mImageCaptureUri;
    private DatePicker dpResult;
    private ImageView profileimg;
    private Dialog dialog;

    private static final int DATE_DIALOG_ID = 9990;
    private Bitmap bitmapImage;
    private int year;
    private int month;
    private int day;

    private int mCurrentyear;
    private int mCurrentmonth;
    private int mCurrentday;

    private String gender_var = "";
    public static Activity act;
    public static Context context;
    private AuthManager authManager;
    private ProfileManager profileManager;
    private String usrtoken;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_profile);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        act = this;
        Utils.acty = this;
        context = this;
        typeface = Typeface.createFromAsset(ProfileView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);

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

        fname.setTypeface(typeface);
        lname.setTypeface(typeface);
        city.setTypeface(typeface);
        country.setTypeface(typeface);
        email.setTypeface(typeface);

        tvDate.setTypeface(typeface);
        tvMonth.setTypeface(typeface);
        tvYear.setTypeface(typeface);


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
            done.setBackgroundResource(R.drawable.c_next_inactive);
        }

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

    public void onEventMainThread(String message) {
        Log.d(TAG, "onEventMainThread->" + message);
        Log.e("message ->", "-- " + message);
        if (message.equalsIgnoreCase("UpdateProfile True")) {
            Utils.dismissBarDialog();
            bitmapImage = null;
            //Log.e("Utils.DeleteImage","Utils.DeleteImage--->"+	Utils.DeleteImage(mImageCaptureUri,ProfileView.this));
            switchView();
            Log.d("1", "message->" + message);
        } else if (message.equalsIgnoreCase("UpdateProfile False")) {
            Utils.dismissBarDialog();
            Utils.showAlert(ProfileView.this, authManager.getMessage());
            Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("UpdateProfile Network Error")) {
            Utils.dismissBarDialog();
            Utils.showAlert(act, AlertMessage.connectionError);
            Log.d("3", "message->" + message);
        }

    }


    private void switchView() {
        Intent intent = new Intent(ProfileView.this, PlayItSafeView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        authManager = ModelManager.getInstance().getAuthorizationManager();
        switch (v.getId()) {
            case R.id.btn_done:
                usrtoken = authManager.getUsrToken();

                Log.e("", "---getUsrToken-----" + authManager.getUsrToken());
                if (updateProfileValidation()) {

                    if (bitmapImage == null) {
                        try {
                            bitmapImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile);
                        } catch (Exception e) {
                        }
                    }
                    if (Utils.isEmailValid(email.getText().toString())) {
                        Utils.launchBarDialog(ProfileView.this);
                        authManager.setEmailId(email.getText().toString());
                        profileManager = ModelManager.getInstance().getProfileManager();
                        profileManager.setProfile(fname.getText().toString(), lname.getText().toString(), authManager.getPhoneNo(),
                                authManager.getUsrToken(), gender_var, "" + day + month + year, city.getText().toString(), country.getText().toString(), email.getText().toString(), "", Utils.encodeTobase64(bitmapImage));
                    } else {
                        Utils.showAlert(ProfileView.this, AlertMessage.vEmailid);
                    }
                }
                break;
            case R.id.btn_guy:
                gender_var = "1";
                guy.setBackgroundResource(R.drawable.c_pink_guy);
                girl.setBackgroundResource(R.drawable.c_grey_girl);
                break;
            case R.id.btn_girl_btn:
                gender_var = "0";
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

                //Boolean isInternetPresent = isConnectingToInternet();
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


    //Methods for Facebook
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

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
        Log.d(TAG, access_Token);
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {

                    Log.e(TAG, String.valueOf(user));
                    try {
                        Log.d("user email", user.getInnerJSONObject().getString("email"));
                        email.setText(user.getInnerJSONObject().getString("email"));
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        if (user.getProperty("gender").equals("male")) {
                            gender_var = "1";
                            guy.setBackgroundResource(R.drawable.c_pink_guy);
                            girl.setBackgroundResource(R.drawable.c_grey_girl);
                        } else {
                            gender_var = "0";
                            guy.setBackgroundResource(R.drawable.c_grey_guy);
                            girl.setBackgroundResource(R.drawable.c_pink_girl);
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    try {

                        Log.d(TAG, user.getLocation().getProperty("name").toString());
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
                        Log.e("dob", user.getBirthday());

                        String[] dob = user.getBirthday().split("/");
                        year = Integer.valueOf(dob[2]);
                        month = Integer.valueOf(dob[0]);
                        day = Integer.valueOf(dob[1]);

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
                        task.execute(new String[]{"https://graph.facebook.com/" + user.getId() + "/picture?type=large"});


                        //						Picasso.with(ProfileView.this).load("https://graph.facebook.com/"+user.getId()+"/picture?type=large")
                        //						.placeholder(R.drawable.default_profile)
                        //						.resize(300,300)
                        //						.error(R.drawable.default_profile)
                        //						.into(profileimg);
                        //						//userdata.put("profile_image","https://graph.facebook.com/"+user.getId()+"/picture?type=large");
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


    // get dialog for image . camera or gallery
    public void imageDialog() {
        String[] addPhoto;
        addPhoto = new String[]{"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Option");

        dialog.setItems(addPhoto, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (id == 0) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    try {
                        cameraIntent.putExtra("return-data", true);
                        startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);
                    } catch (ActivityNotFoundException e) {
                    }
                    dialog.dismiss();
                } else if (id == 1) {

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);

					/*Intent intent = new Intent();
					intent.setType("image*//*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"),Constants.SELECT_PICTURE);*/
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
                        mImageCaptureUri = data.getData();
                        bitmapImage = Utils.decodeUri(mImageCaptureUri, ProfileView.this);
                        profileimg.setImageBitmap(bitmapImage);
                        break;
                    case Constants.SELECT_PICTURE:
                        mImageCaptureUri = data.getData();
                        bitmapImage = Utils.decodeUri(mImageCaptureUri, ProfileView.this);
                        profileimg.setImageBitmap(bitmapImage);
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "" + e);
        } catch (Error e) {
            Log.d(TAG, "" + e);
        }
    }

    // display current date
    @SuppressLint("NewApi")
    public void setCurrentDateOnView() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        mCurrentyear = year;

        // String monthname=c.getDisplayName(Calendar.MONTH, Calendar.LONG,
        // Locale.getDefault());
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
                return new DatePickerDialog(this, datePickerListener, year, month, day);
        }
        return null;
    }

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

        }
    };

    public boolean updateProfileValidation() {
        Log.e(TAG, "mCurrentyear" + mCurrentyear + "  year " + year);
        if (fname.getText().toString().length() < 1) {
            Utils.showAlert(ProfileView.this, AlertMessage.fname);
            return false;
        } else if (lname.getText().toString().length() < 1) {
            Utils.showAlert(ProfileView.this, AlertMessage.lname);
            return false;
        } else if (email.getText().toString().length() < 1) {
            Utils.showAlert(ProfileView.this, AlertMessage.emailid);
            return false;
        } else if ((mCurrentyear - year) == 0) {
            Log.e(TAG, "mCurrentyear" + mCurrentyear + "  year " + year);
            Utils.showAlert(ProfileView.this, AlertMessage.ageValid);
            return false;
        } else if ((mCurrentyear - year) <= 17) {
            Log.e(TAG, "mCurrentyear" + mCurrentyear + "  year " + year);
            Utils.showAlert(ProfileView.this, AlertMessage.UDERAGEMSGII);
            return false;
        }
        return true;
    }

    public void alertDialog(String msgStrI, String msgStrII) {
        dialog = new Dialog(ProfileView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        dialog.setContentView(R.layout.alert_nocheck);

        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
        msgI.setText(msgStrI);
        msgII.setText(msgStrII);

        // dialog.setCancelable(true);
        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setBackgroundResource(R.drawable.try_again);
        dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
