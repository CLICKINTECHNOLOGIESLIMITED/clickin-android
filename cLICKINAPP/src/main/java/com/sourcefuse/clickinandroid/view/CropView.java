package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
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
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
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
        setContentView(R.layout.crop_view);
        findViewById(R.id.btn_use).setOnClickListener(this);
        findViewById(R.id.btn_retake).setOnClickListener(this);
        mCropImageView = (CropImageView) findViewById(R.id.crop_image);
        mCropImageView.setAspectRatio(1, 1);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setGuidelines(1);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getIntent().getExtras() != null) {
            try {

                if (getIntent().getStringExtra("from").equalsIgnoreCase("fromgallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery")) {
                    ((TextView) findViewById(R.id.btn_retake)).setText(getString(R.string.cancel));
                    ((TextView) findViewById(R.id.btn_use)).setText(getString(R.string.choose));


                }
                if (getIntent().getStringExtra("from").equalsIgnoreCase("fromchatCamare") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery")) {
                    try {
                        mName = getIntent().getStringExtra("name");  // name to save image by chatid when came from chat
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                       /* Uri uri = Uri.parse(getIntent().getStringExtra("uri"));*/
                bitmap = authManager.getOrginalBitmap();

                if (bitmap == null) {
                    Log.e("bit null", "bit null");
                    finish();
                } else {
                    Log.e("bit not  null", "bit not  null");
                    ((CropImageView) findViewById(R.id.crop_image)).setImageBitmap(bitmap);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("exception --->", "exception --->");
                Log.e("exception --->", "" + e.toString());
            }


        }
    }

    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatCamare")) {
            Log.e("in crop 1", "in crop 1");
            try {
                authManager.setmResizeBitmap(null);
                Intent intent = new Intent(CropView.this, ChatRecordView.class);
                intent.putExtra("retake", "fckoff");
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, R.anim.top_out);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception----->", e.toString());
            }
        } else {
            Log.e("in crop 2", "in crop 2");
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
                        Log.e("Exception----->", e.toString());
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
                        Log.e("Exception----->", e.toString());
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
                        Log.e("Exception----->", e.toString());
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
                Log.e("path on save bitmap  ---->", "" + path);
                Log.e("uri on save bitmap------->", "" + mImageCaptureUri);
                Log.e("uri on save bitmap mName------->", "" + mName);
                if (!Utils.isEmptyString(path)) {
                    resizebitmap = BitmapFactory.decodeFile(path);
                }


                        /*test code*/


                if (getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatCamare")) {
                    authManager.setmResizeBitmap(resizebitmap);
                    Intent intent = new Intent(getApplicationContext(), EditMyProfileView.class);
                    intent.putExtra("retake", "fckoff");
                    intent.putExtra("path", path);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    //Utils.dismissBarDialog();
                } else {
                    if (resizebitmap != null) {
                        authManager.setmResizeBitmap(resizebitmap);
                        authManager.setUserImageUri(Uri.parse(getIntent().getStringExtra("uri")));
                        Intent intent = new Intent(getApplicationContext(), EditMyProfileView.class);
                        intent.putExtra("retake", "fckoff");
                        intent.putExtra("path", path);
                        setResult(Activity.RESULT_OK, intent);
                    }
                    finish();
                    //Utils.dismissBarDialog();
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
            Log.e("TAG", "Error saving image file: " + e.getMessage());

            return "";
        } catch (IOException e) {
            Log.e("TAG", "Error saving image file: " + e.getMessage());
            return "";
        } catch (Exception e) {
            Log.e("TAG", "Error saving image file: " + e.getMessage());
            e.printStackTrace();
            return "";
        }

        return filePath;
    }


}
