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
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.SettingManager;
import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinapp.R;

import java.io.BufferedOutputStream;
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
import java.util.Random;
import java.util.TimeZone;

public class
        Utils {

    public static boolean DEBUG = true;

    public static String deviceId, PROJECT_NUMBER = "1058681021160";
    public static String mVideoPath = "/storage/emulated/0/ClickIn/ClickinVideo/";
    public static String mImagePath = "/storage/emulated/0/ClickIn/ClickinImages/";
    public static String mAudioPath = "/storage/emulated/0/ClickIn/ClickinAudio/";
    public static boolean appSound;
    public static SharedPreferences prefrences;
    public static Activity acty;
    public static ArrayList<ContactBean> itData = new ArrayList<ContactBean>();
    public static ArrayList<String> groupSms = new ArrayList<String>();
    public static HashMap<String, ContactBean> contactMap = new HashMap<String, ContactBean>();
    public static String mName;
    static GoogleCloudMessaging gcm;
    static String regid;
    private static CustomProgressDialog barProgressDialog;
    private static Dialog dialog;
    private static Uri mImageCaptureUri;
    private static MediaPlayer mplayer;
    public AuthManager authManager;

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

    //akshit code dialog

    //akshit code starts
    public static void fromSignalDialog1(Activity activity, String msgStrI, String msgStrII) {
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
    // Ends

    // Akshit Code Starts
    public static void fromSignalDialog(Activity activity, String str) {

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

    public static void fromSignalDialogSplsh(Activity activity, String str) {

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
                try {
                    System.exit(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        dialog.show();
    }
    public static String getCardURLForAndroid(String url) {

        String url_to_load = url.replaceFirst("cards\\/(\\d+)\\.jpg", "cards\\/a\\/1080\\/$1\\.jpg");
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
            if (!(code.contains("+(null)")) && !(code.contains("null"))) {
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

        return imageEncoded;
    }

    public static Bitmap ShrinkBitmap(Bitmap inPutBm, int width, int height) {

        Bitmap OutPutBm;
        float scaleWidth;
        float scaleHeight;
        width = inPutBm.getWidth();

        height = inPutBm.getHeight();


        Matrix matrix = new Matrix();
        scaleWidth = ((float) width) / width;
        scaleHeight = ((float) height) / height;
        matrix.postScale(scaleWidth, scaleHeight);

        OutPutBm = Bitmap.createBitmap(inPutBm, 0, 0, width, height, matrix,
                true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutPutBm.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

        width = OutPutBm.getWidth();

        height = OutPutBm.getHeight();

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


    //Check for Internet Connection

    public static String getCurrentYear(String str) {
        int len = str.length();
        int pastYear = Integer.parseInt(str.substring((len - 4)));
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        return Integer.toString((thisYear - pastYear));
    }

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
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String decodeSampledBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        String path = null;
        try {

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "Title", null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }


        return path;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

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
                    Utils.deviceId = regid;
                    msg = "Device registered, registration ID=" + regid;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

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

    public static String lineBreacker(String text) {


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

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;

        Bitmap bmp = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, bytes);
        String path = MediaStore.Images.Media.insertImage(c.getContentResolver(), bmp, "Title", null);


        return Uri.parse(path);
    }

    public static String getLocalDate(String serverDate) {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        TimeZone tz = TimeZone.getDefault();

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

    public static String getLocalDatefromTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("HH:mm a");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    //monika- function to get country code from Sim
    public static String getCountryCodeFromSim(Context context) {

        String CountryZipCode = null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();

        switch (simState) {

            case (TelephonyManager.SIM_STATE_ABSENT):

            case (TelephonyManager.SIM_STATE_NETWORK_LOCKED):

            case (TelephonyManager.SIM_STATE_PIN_REQUIRED):

            case (TelephonyManager.SIM_STATE_PUK_REQUIRED):

            case (TelephonyManager.SIM_STATE_UNKNOWN):
                CountryZipCode = null;

                break;
            case (TelephonyManager.SIM_STATE_READY): {


                CountryZipCode = GetCountryZipCode(context);
                CountryZipCode = "+" + CountryZipCode;


                break;
            }
        }
        return CountryZipCode;
    }




      /* code for camera*/

    public static String GetCountryZipCode(Context context) {
        String CountryID = "";
        String CountryZipCode = "";
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //getNetworkCountryIso
            CountryID = manager.getSimCountryIso().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];

                break;
            }
        }
        return CountryZipCode;
    }

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
     *
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
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

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

    public static Uri savePhoto(Bitmap bmp) {
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
        try {
            out = new FileOutputStream(imageFileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            scanPhoto(imageFileName.toString());
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.fromFile(imageFileName);
    }

    public static String fromInt(int val) {
        return String.valueOf(val);
    }

    public static void scanPhoto(String imageFileName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imageFileName);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        //this.cordova.getContext().sendBroadcast(mediaScanIntent); //this is deprecated

    }

    public static String convertClicks(String clicks) {

        String changeClicks = "";

        if (clicks.equalsIgnoreCase("1") || clicks.equalsIgnoreCase("+01")) {
            changeClicks = "+01       ";
        } else if (clicks.equalsIgnoreCase("2") || clicks.equalsIgnoreCase("+02")) {
            changeClicks = "+02       ";
        } else if (clicks.equalsIgnoreCase("3") || clicks.equalsIgnoreCase("+03")) {
            changeClicks = "+03       ";
        } else if (clicks.equalsIgnoreCase("4") || clicks.equalsIgnoreCase("+04")) {
            changeClicks = "+04       ";
        } else if (clicks.equalsIgnoreCase("5") || clicks.equalsIgnoreCase("+05")) {
            changeClicks = "+05       ";
        } else if (clicks.equalsIgnoreCase("6") || clicks.equalsIgnoreCase("+06")) {
            changeClicks = "+06       ";
        } else if (clicks.equalsIgnoreCase("7") || clicks.equalsIgnoreCase("+07")) {
            changeClicks = "+07       ";
        } else if (clicks.equalsIgnoreCase("8") || clicks.equalsIgnoreCase("+08")) {
            changeClicks = "+08       ";
        } else if (clicks.equalsIgnoreCase("9") || clicks.equalsIgnoreCase("+09")) {
            changeClicks = "+09       ";
        } else if (clicks.equalsIgnoreCase("10") || clicks.equalsIgnoreCase("+10")) {
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


    /* find bitmap */

    //function to update clicks value for ours and partner- in case of Cards only monika
    public static void updateClicksValue(String oursClicks, String partnerClicks, String clicks, boolean ours) {
        int tempOurClicks = Integer.parseInt(oursClicks);
        int tempPartnerClicks = Integer.parseInt(partnerClicks);
        int tempClicks;
        RelationManager manager = ModelManager.getInstance().getRelationManager();
        if (clicks.equalsIgnoreCase("05")) {
            tempClicks = 5;
        } else {
            tempClicks = Integer.parseInt(clicks);
        }
        if (ours) {
            tempOurClicks = tempOurClicks + tempClicks;
            ModelManager.getInstance().getAuthorizationManager().ourClicks = String.valueOf(tempOurClicks);
            tempPartnerClicks = tempPartnerClicks - tempClicks;
            manager.partnerClicks = String.valueOf(tempPartnerClicks);
        } else {
            tempOurClicks = tempOurClicks - tempClicks;
            ModelManager.getInstance().getAuthorizationManager().ourClicks = String.valueOf(tempOurClicks);
            tempPartnerClicks = tempPartnerClicks + tempClicks;
            manager.partnerClicks = String.valueOf(tempPartnerClicks);
        }

    }

    public static Bitmap path(Uri mpath) {
        Bitmap resized = null;
        try {

                                          /* code prafull for camera */
            Bitmap bitmap = BitmapFactory.decodeFile(mpath.getPath(), new BitmapFactory.Options());
            try {
                ExifInterface ei = new ExifInterface(mpath.getPath());
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

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);


            } catch (Exception e) {
                e.printStackTrace();
            }


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

        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return resized;

    }

    //monika-convert String clicks value to int
    public static int convertToIntClicks(String clicks) {

        int changeClicks = 0;

        if (clicks.equalsIgnoreCase("1") || clicks.equalsIgnoreCase("01")) {
            changeClicks = 1;
        } else if (clicks.equalsIgnoreCase("2") || clicks.equalsIgnoreCase("02")) {
            changeClicks = 2;
        } else if (clicks.equalsIgnoreCase("3") || clicks.equalsIgnoreCase("03")) {
            changeClicks = 3;
        } else if (clicks.equalsIgnoreCase("4") || clicks.equalsIgnoreCase("04")) {
            changeClicks = 4;
        } else if (clicks.equalsIgnoreCase("5") || clicks.equalsIgnoreCase("05")) {
            changeClicks = 5;
        } else if (clicks.equalsIgnoreCase("6") || clicks.equalsIgnoreCase("06")) {
            changeClicks = 6;
        } else if (clicks.equalsIgnoreCase("7") || clicks.equalsIgnoreCase("07")) {
            changeClicks = 7;
        } else if (clicks.equalsIgnoreCase("8") || clicks.equalsIgnoreCase("08")) {
            changeClicks = 8;
        } else if (clicks.equalsIgnoreCase("9") || clicks.equalsIgnoreCase("09")) {
            changeClicks = 9;
        } else if (clicks.equalsIgnoreCase("10") || clicks.equalsIgnoreCase("10")) {
            changeClicks = 10;
        }
        return changeClicks;
    }

    // //function to update clicks value for ours -without cards-monika
    public static void updateClicksWithoutCard(String oursClicks, String clicks, boolean add) {
        String tempOurClicksString = new String(oursClicks);
        if (tempOurClicksString.startsWith("+") || tempOurClicksString.startsWith("-"))
            tempOurClicksString = tempOurClicksString.substring(1);

        int tempOurClicks = Integer.parseInt(tempOurClicksString);

        int tempClicks = convertToIntClicks(clicks.substring(1));

        if (add) {
            tempOurClicks = tempOurClicks + tempClicks;
            ModelManager.getInstance().getAuthorizationManager().ourClicks = String.valueOf(tempOurClicks);

        } else {//minus cicks
            tempOurClicks = tempOurClicks - tempClicks;
            ModelManager.getInstance().getAuthorizationManager().ourClicks = String.valueOf(tempOurClicks);

        }

    }

    //function to update clicks of partner without card
    // //function to update clicks value for ours -without cards-monika
    public static void updateClicksPartnerWithoutCard(String partnerClicks, String clicks, boolean add) {
        String tempPartnerClicksString = new String(partnerClicks);
        if (tempPartnerClicksString.startsWith("+") || tempPartnerClicksString.startsWith("-"))
            tempPartnerClicksString = tempPartnerClicksString.substring(1);

        int tempPartnerClicks = Integer.parseInt(tempPartnerClicksString);

        int tempClicks = convertToIntClicks(clicks.substring(1));

        if (add) {
            tempPartnerClicks = tempPartnerClicks + tempClicks;
            ModelManager.getInstance().getRelationManager().partnerClicks = String.valueOf(tempPartnerClicks);

        } else {//minus cicks
            tempPartnerClicks = tempPartnerClicks - tempClicks;
            ModelManager.getInstance().getRelationManager().partnerClicks = String.valueOf(tempPartnerClicks);

        }

    }

    //monika- code to

    //akshit code to play sound
    public static void playSound(Activity activity, int resID) {


        SettingManager mSettingManager = ModelManager.getInstance().getSettingManager();

        if (mSettingManager.isAppSounds()) {
            try {


                if (mplayer == null) {
                    mplayer = MediaPlayer.create(activity, resID);
                    mplayer.setLooping(false);
                    mplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    if (!mplayer.isPlaying())
                        mplayer.start();

                    mplayer.setVolume(100, 100);
                    mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mplayer.release();
                            mplayer = null;
                        }
                    });
                }

            } catch (IllegalArgumentException e) {

            } catch (SecurityException e) {

            } catch (IllegalStateException e) {

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {

        }

    }

    public static void Unregister(final Activity contex) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(contex);
                    }
                    gcm.unregister();

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }

        }.execute(null, null, null);

    }

    //monika-function to update clicks for background messages
    public static void updateClicksBackgroundMsgs(int relationIndex, ChatMessageBody obj) {
        ArrayList<GetrelationshipsBean> tempAcceptlist = ModelManager.getInstance().getRelationManager().acceptedList;
        if (relationIndex <= tempAcceptlist.size()) {
            GetrelationshipsBean temp = tempAcceptlist.get(relationIndex);
            String oursClicks = temp.getUserClicks();
            String partnerClicks = temp.getClicks();
            int tempOurClicks = Integer.parseInt(oursClicks);
            int tempPartnerClicks = Integer.parseInt(partnerClicks);
            int tempClicks;
            RelationManager manager = ModelManager.getInstance().getRelationManager();

            //check card is there or not
            if (!Utils.isEmptyString(obj.card_owner)) {
                if (obj.card_Accepted_Rejected.equalsIgnoreCase("accepted")) {
                    if (obj.clicks.equalsIgnoreCase("05")) {
                        tempClicks = 5;
                    } else {
                        tempClicks = Integer.parseInt(obj.clicks);
                    }
                    //if we send the card, then clicks will get subtracted from our clicks
                    if (obj.card_owner.equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getQBId())) {
                        tempOurClicks = tempOurClicks - tempClicks;
                        tempPartnerClicks = tempPartnerClicks + tempClicks;
                    } else {
                        tempOurClicks = tempOurClicks + tempClicks;
                        tempPartnerClicks = tempPartnerClicks - tempClicks;
                    }
                }
            } else {
                if (obj.clicks.equalsIgnoreCase("05")) {
                    tempClicks = 5;
                } else {
                    tempClicks = convertToIntClicks(obj.clicks.substring(1));
                }

                //if card is not there, then clicks will be calculated from ours only
                if (obj.clicks.startsWith("+")) {
                    tempOurClicks = tempOurClicks + tempClicks;
                } else if (obj.clicks.startsWith("-")) {
                    tempOurClicks = tempOurClicks - tempClicks;
                }

            }


            temp.setUserClicks(String.valueOf(tempOurClicks));
            temp.setClicks(String.valueOf(tempPartnerClicks));
        }
    }

    public static void updateClicksInRelationshipList(int relationListIndex) {

        //monika-swap values as per naming convention on server
        ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).setClicks(ModelManager.getInstance().getRelationManager().partnerClicks);
        ModelManager.getInstance().getRelationManager().acceptedList.get(relationListIndex).setUserClicks(ModelManager.getInstance().getAuthorizationManager().ourClicks);

    }

    public static long ConvertIntoTimeStamp() {
        return System.currentTimeMillis();
    }

    public static String getMonth(long timeStamp) {
        DateFormat sdf = new SimpleDateFormat("MM");
        Date netDate = (new Date(timeStamp));
        return sdf.format(netDate);
    }

    public static String getDay(long timeStamp) {
        DateFormat sdf = new SimpleDateFormat("DD");
        Date netDate = (new Date(timeStamp));
        return sdf.format(netDate);
    }

    public static String getCompareDate(long timeStamp) {
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date netDate = (new Date(timeStamp));
        return sdf.format(netDate);
    }

    public static String getTodaySeenDate(long timeStamp) {
        DateFormat sdf = new SimpleDateFormat("hh:mm a");
        Date netDate = (new Date(timeStamp));
        String date = sdf.format(netDate);
        String mReturnValue = "last seen today at " + date;
        return mReturnValue;
    }

    public static String getLastSeenDate(long timeStamp) {
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat sdf1 = new SimpleDateFormat("hh:mm a");
        Date netDate = (new Date(timeStamp));
        Date netDate1 = (new Date(timeStamp));
        String date = sdf.format(netDate);
        String date1 = sdf1.format(netDate);
        String mReturnValue = "last seen on " + date + " at " + date1;
        return mReturnValue;
    }

    public static String storeImage(Bitmap imageData, String filename, Context context) { //to store image once croped
        String iconsStoragePath = null;
        String filePath = null;

        if (Utils.isEmptyString(iconsStoragePath)) {
            String newpath = mImagePath; // path to store image
            Random rn = new Random();
            iconsStoragePath = newpath;
        }

        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }
        sdIconStorageDir.setWritable(true);
        sdIconStorageDir.setReadable(true);

        try {
            filePath = sdIconStorageDir.getAbsolutePath() + "/" + filename + ".jpg";

            File file = new File(filePath);
            if (file.exists())
                file.delete();

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(filePath)));


            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            values.put(MediaStore.Images.Media.DATE_TAKEN, sdIconStorageDir.lastModified());
            Uri mImageCaptureUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); // to notify change
            context.getContentResolver().notifyChange(Uri.parse(filePath), null);

        } catch (FileNotFoundException e) {


            return "";
        } catch (IOException e) {

            return "";
        } catch (Exception e) {

            e.printStackTrace();
            return "";
        }

        return filePath;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            int id1 = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id1);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static Uri getVideoContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID},
                MediaStore.Video.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            int id1 = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id1);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    public static Uri getAudioContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            int id1 = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id1);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    /* download video from url */

    public static Uri getUriFromPath(String filePath, Context context) {
        long photoId;
        Uri photoUri = MediaStore.Images.Media.getContentUri("external");

        String[] projection = {MediaStore.Images.ImageColumns._ID};
        // TODO This will break if we have no matching item in the MediaStore.
        Cursor cursor = context.getContentResolver().query(photoUri, projection, MediaStore.Images.ImageColumns.DATA + " LIKE ?", new String[]{filePath}, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        photoId = cursor.getLong(columnIndex);

        cursor.close();
        return Uri.parse(photoUri.toString() + "/" + photoId);
    }

    public static void playvideo(Context context, String Url) {
    /* play video */

        Uri uri = Uri.parse(Url);
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_VIEW);
        intent1.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);

        intent1.setDataAndType(uri, "video/*");
        try {
            context.startActivity(intent1);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void playAudio(Context context, String Url) {
    /* playAudio */

        Uri uri = Uri.parse(Url);
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_VIEW);
        intent1.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);


        intent1.setDataAndType(uri, "audio/*");
        try {
            context.startActivity(intent1);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private int pxlToDp(int pixel, Context mContext) {

        final float scale = mContext.getResources().getDisplayMetrics().density;
        int dp = (int) (pixel * scale + 0.5f);
        return dp;
    }


    public static void showDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setCancelable(true).setView(layout).show();

        LinearLayout popup_confirm_button = (LinearLayout) layout.findViewById(R.id.popup_view);
        popup_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

}

