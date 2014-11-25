package com.sourcefuse.clickinandroid.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Utils {

    public static String deviceId, PROJECT_NUMBER = "1058681021160";
    private static CustomProgressDialog barProgressDialog;

    public static SharedPreferences prefrences;
    public static Activity acty;
    private static Dialog dialog;
    private static Uri mImageCaptureUri;
    private AuthManager authManager;
    static GoogleCloudMessaging gcm;
    static String regid;
    public static ArrayList<ContactBean> itData = new ArrayList<ContactBean>();

    public static ArrayList<String> groupSms = new ArrayList<String>();

    public static HashMap<String, ContactBean> contactMap = new HashMap<String, ContactBean>();

    public static void launchBarDialog(Activity activity) {

        barProgressDialog = new CustomProgressDialog(activity);
//		barProgressDialog.setTitle("Loading ...");
//		barProgressDialog.setMessage("In progress ...");
        barProgressDialog.setCancelable(false);
        barProgressDialog.show();
    }

    public static void dismissBarDialog() {
        if (barProgressDialog != null) {
            try {
                barProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            barProgressDialog = null;
        }
    }

    public static void clickInpdOpen(Activity activity, String dialogMessage) {
        dialog = null;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.clickin_progressbar);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView textView = (TextView) dialog.findViewById(R.id.tv_loading);
        // textView.setText(dialogMessage);
        dialog.show();
    }

    public static void clickInpdClose() {

        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog = null;
        }
    }

    public static void showAlert(Activity activity, String masg) {
        new AlertDialog.Builder(activity).setTitle("Alert").setMessage(masg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Utils.dismissBarDialog();

                    }
                }).show();

    }


    //akshit code starts
    public static void fromSignalDialog1(Activity activity,String msgStrI, String msgStrII) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_nocheck);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
        msgI.setText(msgStrI);
        msgII.setText(msgStrII);

        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    //akshit code dialog

    public static void fromSignalertDialogDammit(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_nocheck);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        TextView msgII = (TextView) dialog.findViewById(R.id.alert_msgII);
        msgI.setText("The Code Has Been re-sent.Please check");
        msgII.setText("");

        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
       // Akshit Code Starts
    public static void fromSignalDialog(Activity activity ,String str){

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
    // Ends



    public static String getCardURLForAndroid(String url) {

        String url_to_load = url.replaceFirst("cards\\/(\\d+)\\.jpg","cards\\/a\\/1080\\/$1\\.jpg");
        return url_to_load;
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPhoneValid(CharSequence number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public static boolean isCountryCodeValid(String code) {
        if (code.length() > 1) {
            if(!(code.contains("+(null)")) && !(code.contains("null"))) {
                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        // Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap ShrinkBitmap(Bitmap inPutBm, int width, int height) {

        Bitmap OutPutBm;
        float scaleWidth;
        float scaleHeight;
        width = inPutBm.getWidth();
        Log.i("Old width................", width + "");
        height = inPutBm.getHeight();
        Log.i("Old height................", height + "");

        Matrix matrix = new Matrix();
        scaleWidth = ((float) width) / width;
        scaleHeight = ((float) height) / height;
        matrix.postScale(scaleWidth, scaleHeight);

        OutPutBm = Bitmap.createBitmap(inPutBm, 0, 0, width, height, matrix,
                true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutPutBm.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

        width = OutPutBm.getWidth();
        Log.i("new width................", width + "");
        height = OutPutBm.getHeight();
        Log.i("new height................", height + "");
        return OutPutBm;
    }

    public static Bitmap decodeUri(Uri selectedImage, Activity _activity)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(_activity.getContentResolver()
                .openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(_activity.getContentResolver()
                .openInputStream(selectedImage), null, o2);
    }

    public static boolean isEmptyString(String str) {
        if (str == null || str.equalsIgnoreCase("null")
                || str.equalsIgnoreCase("") || str.length() < 1) {
            return true;
        }
        return false;
    }

    public static String getCurrentYear(String str) {
        int len = str.length();
        int pastYear = Integer.parseInt(str.substring((len - 4)));
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        return Integer.toString((thisYear - pastYear));
    }


    //Check for Internet Connection

    public static boolean isConnectingToInternet(Activity act) {
        ConnectivityManager connMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            return true;
        } else {
            // display error
            return false;
        }
    }

    // public static Boolean DeleteImage(Uri uri,Activity act) {
    // boolean deleted = false;
    // String[] projection = { MediaStore.Images.Media.DATA };
    // Cursor cursor = act.managedQuery(uri, projection, null, null, null);
    // if(cursor!=null){
    // int column_index =
    // cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    // cursor.moveToFirst();
    // File file = new File(cursor.getString(column_index));
    // deleted = file.delete();
    // }
    // return deleted;
    //
    // }
    public static void showToast(Activity act, String msg) {
        Toast.makeText(act, "" + msg, Toast.LENGTH_SHORT).show();
    }

    public static String getCTime() {
        DateFormat df = new SimpleDateFormat("h:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }


    public static void imageDialog(final Activity contex) {
        String[] addPhoto;
        addPhoto = new String[]{"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(contex);
        dialog.setTitle("Select Option");

        dialog.setItems(addPhoto, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (id == 0) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    try {
                        cameraIntent.putExtra("return-data", true);
                        contex.startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);
                    } catch (ActivityNotFoundException e) {
                    }
                    dialog.dismiss();
                } else if (id == 1) {

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    contex.startActivityForResult(pickPhoto, Constants.SELECT_PICTURE);
                    dialog.dismiss();
                }
            }
        });

        dialog.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }
        );
        dialog.show();
    }

    public static String getRealPathFromURI(Uri contentUri, Activity mContext) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static String getRegId(final Activity contex) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(contex);
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("GCM", "Rregid--->id" + regid);
            }

        }.execute(null, null, null);
        return regid;
    }

    public static void clickCustomLog(String sBody) {
        try {
            File mediaDir = new File("/sdcard/download/media");
            if (!mediaDir.exists()) {
                mediaDir.mkdir();
            }
            File resolveMeSDCard = new File("/sdcard/download/media/clickinCustomLog.txt");
            resolveMeSDCard.createNewFile();
            FileOutputStream fos = new FileOutputStream(resolveMeSDCard);
            fos.write(sBody.getBytes());
            fos.close();

        } catch (Exception e) {
        }

    }

    public static String lineBreacker(String text){


      /* // int s = 25-text.length();
        if(text.length()<25) {
           *//* for(int i =0; i<(50-text.length());i++){
                text = text +"                          ";
            }*//*
            text = text +"                                              ";
           // text = String.format("%s%"+s+"s", "jack"," ");

        }
*/
        String tenCharPerLineString = "";
        while (text.length() > 25) {

            String buffer = text.substring(0, 25);
            tenCharPerLineString = tenCharPerLineString + buffer + "\n";
            text = text.substring(25);
        }

        tenCharPerLineString = tenCharPerLineString + text.substring(0);

/*
        if(text.length()<25) {
            for(int i =0; i<(50-text.length());i++){
                text = text +"   ";
            }
          *//*  tenCharPerLineString = text +"                                       ";*//*
            // text = String.format("%s%"+s+"s", "jack"," ");

        }*/

        return tenCharPerLineString;
    }


    public static Uri decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;

       Bitmap bmp =  BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
           bmp.compress(Bitmap.CompressFormat.JPEG, 10, bytes);
            String path = MediaStore.Images.Media.insertImage(c.getContentResolver(), bmp, "Title", null);


        return Uri.parse(path);
    }
    public static String getLocalDate(String serverDate)
    {
//        Log.e("serverDate",serverDate);
//         serverDate = "2014-10-09 09:46:50";
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        TimeZone tz = TimeZone.getDefault();
//        Calendar cal = Calendar.getInstance();
//        TimeZone tz = cal.getTimeZone();
//        TimeZone tz = TimeZone.getTimeZone("Asia/Calcutta");
//        Log.e("tz",tz.toString());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf = new SimpleDateFormat("HH:mm a");
        sdf.setTimeZone(TimeZone.getDefault());
        String newDateStr = sdf.format(date);


       return newDateStr;
    }

        public static String getLocalDatefromTimestamp(long timestamp)
        {
            Date date = new Date(timestamp);
            DateFormat formatter = new SimpleDateFormat("HH:mm a");
            String dateFormatted = formatter.format(date);
            return dateFormatted;
        }


    //monika- function to get country code from Sim
    public static String getCountryCodeFromSim(Context context){

            String CountryZipCode = null;
            TelephonyManager telephonyManager = (TelephonyManager)context. getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telephonyManager.getSimState();
            //Log.e("simState",""+simState+"/"+TelephonyManager.SIM_STATE_NETWORK_LOCKED+"/"+TelephonyManager.SIM_STATE_UNKNOWN+"/"+TelephonyManager.SIM_STATE_READY);
            switch (simState) {

                case (TelephonyManager.SIM_STATE_ABSENT):

                case (TelephonyManager.SIM_STATE_NETWORK_LOCKED):

                case (TelephonyManager.SIM_STATE_PIN_REQUIRED):

                case (TelephonyManager.SIM_STATE_PUK_REQUIRED):

                case (TelephonyManager.SIM_STATE_UNKNOWN):
                     CountryZipCode=null;

                    break;
                case (TelephonyManager.SIM_STATE_READY): {


                    CountryZipCode = GetCountryZipCode(context);
                    CountryZipCode = "+" + CountryZipCode;
                    Log.e("COUNTRY ZIP CODE", CountryZipCode);


                    break;
                }
            }
            return CountryZipCode;
    }


        public static String GetCountryZipCode(Context context){
            String CountryID="";
            String CountryZipCode="";
            try {
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                //getNetworkCountryIso
                CountryID = manager.getSimCountryIso().toUpperCase();
            }catch (Exception e){
                e.printStackTrace();
            }

            String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
            for(int i=0;i<rl.length;i++){
                String[] g=rl[i].split(",");
                if(g[1].trim().equals(CountryID.trim())){
                    CountryZipCode=g[0];
                    Log.e("Code","Tis is Code>>>>>" +CountryZipCode);
                    break;
                }
            }
            return CountryZipCode;
        }




      /* code for camera*/


      public static final String insertImage(ContentResolver cr,
                                             Bitmap source,
                                             String title,
                                             String description) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, title);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
            values.put(MediaStore.Images.Media.DESCRIPTION, description);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            // Add the date meta data to ensure the image is added at the front of the gallery
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            Uri url = null;
            String stringUrl = null;    /* value to be returned */

            try {
                  url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                  if (source != null) {
                        OutputStream imageOut = cr.openOutputStream(url);
                        try {
                              source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                        } finally {
                              imageOut.close();
                        }

                        long id = ContentUris.parseId(url);
                        // Wait until MINI_KIND thumbnail is generated.
                        Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                        // This is for backward compatibility.
                        storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
                  } else {
                        cr.delete(url, null, null);
                        url = null;
                  }
            } catch (Exception e) {
                  if (url != null) {
                        cr.delete(url, null, null);
                        url = null;
                  }
            }

            if (url != null) {
                  stringUrl = url.toString();
            }

            return stringUrl;
      }

      /**
       * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
       * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
       * meta data. The StoreThumbnail method is private so it must be duplicated here.
       * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
       */
      private static final Bitmap storeThumbnail(
                                                        ContentResolver cr,
                                                        Bitmap source,
                                                        long id,
                                                        float width,
                                                        float height,
                                                        int kind) {

            // create the matrix to scale it
            Matrix matrix = new Matrix();

            float scaleX = width / source.getWidth();
            float scaleY = height / source.getHeight();

            matrix.setScale(scaleX, scaleY);

            Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                                                      source.getWidth(),
                                                      source.getHeight(), matrix,
                                                      true
            );

            ContentValues values = new ContentValues(4);
            values.put(MediaStore.Images.Thumbnails.KIND,kind);
            values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
            values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
            values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

            Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

            try {
                  OutputStream thumbOut = cr.openOutputStream(url);
                  thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
                  thumbOut.close();
                  return thumb;
            } catch (FileNotFoundException ex) {
                  return null;
            } catch (IOException ex) {
                  return null;
            }
      }


      public static Uri savePhoto(Bitmap bmp)
      {
            //File imageFileFolder = new File(Environment.getExternalStorageDirectory(),"MyFolder"); //when you need to save the image inside your own folder in the SD Card
            File path = Environment.getDataDirectory(); //this is the default location inside SD Card - Pictures folder
            //imageFileFolder.mkdir(); //when you create your own folder, you use this line.
            FileOutputStream out = null;
            Calendar c = Calendar.getInstance();
            String date = fromInt(c.get(Calendar.MONTH))
                                  + fromInt(c.get(Calendar.DAY_OF_MONTH))
                                  + fromInt(c.get(Calendar.YEAR))
                                  + fromInt(c.get(Calendar.HOUR_OF_DAY))
                                  + fromInt(c.get(Calendar.MINUTE))
                                  + fromInt(c.get(Calendar.SECOND));
            File imageFileName = new File(path, date.toString() + ".jpg"); //imageFileFolder
            try
            {
                  out = new FileOutputStream(imageFileName);
                  bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                  out.flush();
                  out.close();
                  scanPhoto(imageFileName.toString());
                  out = null;
            } catch (Exception e)
            {
                  e.printStackTrace();
            }
            return Uri.fromFile(imageFileName);
      }


      public static String fromInt(int val)
      {
            return String.valueOf(val);
      }
      public static void scanPhoto(String imageFileName)
      {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(imageFileName);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            //this.cordova.getContext().sendBroadcast(mediaScanIntent); //this is deprecated

      }


    public static String convertClicks(String clicks) {

        String changeClicks = "";

        if (clicks.equalsIgnoreCase("1") || clicks.equalsIgnoreCase("+01") ) {
            changeClicks = "+01       ";
        } else if (clicks.equalsIgnoreCase("2") || clicks.equalsIgnoreCase("+02")) {
            changeClicks = "+02       ";
        } else if (clicks.equalsIgnoreCase("3") || clicks.equalsIgnoreCase("+03")) {
            changeClicks = "+03       ";
        } else if (clicks.equalsIgnoreCase("4") || clicks.equalsIgnoreCase("+04")) {
            changeClicks = "+04       ";
        } else if (clicks.equalsIgnoreCase("5") || clicks.equalsIgnoreCase("+05")){
            changeClicks = "+05       ";
        } else if (clicks.equalsIgnoreCase("6") || clicks.equalsIgnoreCase("+06")) {
            changeClicks = "+06       ";
        } else if (clicks.equalsIgnoreCase("7")|| clicks.equalsIgnoreCase("+07"))  {
            changeClicks = "+07       ";
        } else if (clicks.equalsIgnoreCase("8") || clicks.equalsIgnoreCase("+08")) {
            changeClicks = "+08       ";
        } else if (clicks.equalsIgnoreCase("9") || clicks.equalsIgnoreCase("+09"))  {
            changeClicks = "+09       ";
        } else if (clicks.equalsIgnoreCase("10")|| clicks.equalsIgnoreCase("+10"))  {
            changeClicks = "+10       ";
        } else if (clicks.equalsIgnoreCase("-1")) {
            changeClicks = "-01       ";
        } else if (clicks.equalsIgnoreCase("-2")) {
            changeClicks = "-02       ";
        } else if (clicks.equalsIgnoreCase("-3")) {
            changeClicks = "-03       ";
        } else if (clicks.equalsIgnoreCase("-4")) {
            changeClicks = "-04       ";
        } else if (clicks.equalsIgnoreCase("-5")) {
            changeClicks = "-05       ";
        } else if (clicks.equalsIgnoreCase("-6")) {
            changeClicks = "-06       ";
        } else if (clicks.equalsIgnoreCase("-7")) {
            changeClicks = "-07       ";
        } else if (clicks.equalsIgnoreCase("-8")) {
            changeClicks = "-08       ";
        } else if (clicks.equalsIgnoreCase("-9")) {
            changeClicks = "-09       ";
        } else if (clicks.equalsIgnoreCase("-10")) {
            changeClicks = "-10       ";
        } else if (clicks.equalsIgnoreCase("0")) {

            changeClicks = "";
        }
        return changeClicks;
    }
    }
