package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by prafull on 14/11/14.
 */
public class CropView extends Activity implements View.OnClickListener {
    Bitmap bitmap;
    CropImageView mCropImageView;
    Uri mImageCaptureUri;
    private AuthManager authManager;
    private String mName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.crop_view);
        findViewById(R.id.btn_use).setOnClickListener(this);
        findViewById(R.id.btn_retake).setOnClickListener(this);
        mCropImageView = (CropImageView) findViewById(R.id.crop_image);
        mCropImageView.setAspectRatio(1, 1);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setGuidelines(1);
        authManager = ModelManager.getInstance().getAuthorizationManager();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey("from")) {
            String from = bundle.getString("from");
            try {


                if (from.equalsIgnoreCase("fromgallery") || from.equalsIgnoreCase("fromchatGallery")) {
                    ((TextView) findViewById(R.id.btn_retake)).setText(getString(R.string.cancel));
                    ((TextView) findViewById(R.id.btn_use)).setText(getString(R.string.choose));


                }
                if (from.equalsIgnoreCase("fromchatCamare") || from.equalsIgnoreCase("fromchatGallery")) {
                    try {
                        mName = getIntent().getStringExtra("name");  // name to save image by chatid when came from chat
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                       /* Uri uri = Uri.parse(getIntent().getStringExtra("uri"));*/
                bitmap = authManager.getOrginalBitmap();

                if (bitmap == null) {
                    finish();
                } else {
                    ((CropImageView) findViewById(R.id.crop_image)).setImageBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true));
                    bitmap.recycle();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatCamare")) {
            try {
                authManager.setmResizeBitmap(null);
                Intent intent = new Intent(CropView.this, ChatRecordView.class);
                intent.putExtra("retake", "fckoff");
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, R.anim.top_out);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(0, R.anim.top_out);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retake:

                if (getIntent().getStringExtra("from").equalsIgnoreCase("fromcamera")) {
                    try {

                        Intent intent = new Intent(CropView.this, EditMyProfileView.class);
                        intent.putExtra("retake", "camare");
                        setResult(Activity.RESULT_OK, intent);
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (getIntent().getStringExtra("from").equalsIgnoreCase("fromgallery")) {
                    try {

                        Intent intent = new Intent(CropView.this, EditMyProfileView.class);
                        intent.putExtra("retake", "gallery");
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                        overridePendingTransition(0, R.anim.top_out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatCamare")) {

                    try {
                        Intent intent = new Intent(CropView.this, ChatRecordView.class);
                        intent.putExtra("retake", getIntent().getStringExtra("from"));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                        overridePendingTransition(0, R.anim.top_out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                break;
            case R.id.btn_use:

                Utils.launchBarDialog(CropView.this);
                Bitmap resizebitmap = mCropImageView.getCroppedImage();

                        /* test code */


                Random mRandom = new Random();
                int mName = mRandom.nextInt();
                mName = Math.abs(mName);

                String path = storeImage(resizebitmap, "" + mName);
                if (!Utils.isEmptyString(path)) {
                    resizebitmap = BitmapFactory.decodeFile(path);
                }
                if (!Utils.isEmptyString(path)) {

                        /*test code*/

                    Log.e("value of path---->", "" + path);

                    if (getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatCamare")) {
                        //To track through mixPanel.if Image is attached.
                        Utils.trackMixpanel(CropView.this, "Activity", "SelectedImageAttached", "AttachButtonClicked", false);
                        authManager.setmResizeBitmap(resizebitmap.copy(Bitmap.Config.ARGB_8888, true));
                        Intent intent = new Intent(getApplicationContext(), EditMyProfileView.class);
                        intent.putExtra("retake", "fckoff");
                        intent.putExtra("path", path);
                        setResult(Activity.RESULT_OK, intent);
                        resizebitmap.recycle();
                        finish();

                        //Utils.dismissBarDialog();
                    } else {
                        if (resizebitmap != null) {
                            authManager.setmResizeBitmap(resizebitmap.copy(Bitmap.Config.ARGB_8888, true));
                            authManager.setUserImageUri(Uri.parse(path));
                            Intent intent = new Intent(getApplicationContext(), EditMyProfileView.class);
                            intent.putExtra("retake", "fckoff");
                            intent.putExtra("path", path);


                            resizebitmap.recycle();
                            setResult(Activity.RESULT_OK, intent);
                        }
                        finish();
                        //Utils.dismissBarDialog();
                    }
                } else {
                    spaceDialog(CropView.this);  // in this case means user don't have enough storage space.
                }
                break;
        }
    }

    private String storeImage(Bitmap imageData, String filename) { //to store image once croped
        String iconsStoragePath = null;
        String filePath = null;

        if (Utils.isEmptyString(iconsStoragePath)) {
            String newpath = Utils.mImagePath; // path to store image
            Random rn = new Random();
            iconsStoragePath = newpath;
        }

        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }
        Log.e("directory ---->", "" + sdIconStorageDir.exists());

        sdIconStorageDir.setWritable(true);
        sdIconStorageDir.setReadable(true);

        try {
            if (mName != null) {
                filePath = sdIconStorageDir.getAbsolutePath() + "/" + mName + ".jpg";
            } else {
                filePath = sdIconStorageDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
            }

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
            mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); // to notify change
            getContentResolver().notifyChange(Uri.parse(filePath), null);

        } catch (FileNotFoundException e) {

            Log.e("Exception---->1", "" + e.toString());
            return "";
        } catch (IOException e) {
            Log.e("Exception---->2", "" + e.toString());
            return "";
        } catch (Exception e) {
            Log.e("Exception---->3", "" + e.toString());
            e.printStackTrace();
            return "";
        }

        return filePath;
    }


    // Akshit Code Starts
    public void spaceDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(activity.getString(R.string.storage_issue));
        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(CropView.this, ChatRecordView.class);

                setResult(Activity.RESULT_CANCELED, intent);
                finish();
                overridePendingTransition(0, R.anim.top_out);


            }
        });
        dialog.show();
    }

}
