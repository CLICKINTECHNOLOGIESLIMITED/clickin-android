package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinapp.R;

/**
 * Created by prafull on 14/11/14.
 */
public class CropView extends Activity implements View.OnClickListener {
    Bitmap bitmap;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.crop_view);
        findViewById(R.id.btn_use).setOnClickListener(this);
        findViewById(R.id.btn_retake).setOnClickListener(this);
        authManager = ModelManager.getInstance().getAuthorizationManager();
        if (getIntent().getExtras() != null) {
            try {

                if (getIntent().getStringExtra("from").equalsIgnoreCase("fromgallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery")) {
                    ((TextView) findViewById(R.id.btn_retake)).setText(getString(R.string.cancel));
                    ((TextView) findViewById(R.id.btn_use)).setText(getString(R.string.choose));
                }



                       /* Uri uri = Uri.parse(getIntent().getStringExtra("uri"));*/
                bitmap = authManager.getOrginalBitmap();

                if (bitmap == null) {
                    android.util.Log.e("bit null", "bit null");
                    finish();
                } else {
                    android.util.Log.e("bit not  null", "bit not  null");
                    ((CropImageView) findViewById(R.id.crop_image)).setImageBitmap(bitmap);

                }
            } catch (Exception e) {
                e.printStackTrace();
                android.util.Log.e("exception --->", "exception --->");
                android.util.Log.e("exception --->", "" + e.toString());
            }


        }
    }

    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatCamare")) {
            android.util.Log.e("in crop 1", "in crop 1");
            try {
                authManager.setmResizeBitmap(null);
                Intent intent = new Intent(CropView.this, ChatRecordView.class);
                intent.putExtra("retake", "fckoff");
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, R.anim.top_out);

            } catch (Exception e) {
                e.printStackTrace();
                android.util.Log.e("Exception----->", e.toString());
            }
        } else {
            android.util.Log.e("in crop 2", "in crop 2");
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
                        android.util.Log.e("Exception----->", e.toString());
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
                        android.util.Log.e("Exception----->", e.toString());
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
                        android.util.Log.e("Exception----->", e.toString());
                    }
                }


                break;
            case R.id.btn_use:

                Bitmap resizebitmap = ((CropImageView) findViewById(R.id.crop_image)).getCroppedImage();

                        /* test code */
                if (resizebitmap.getWidth() >= resizebitmap.getHeight()) {

                    resizebitmap = Bitmap.createBitmap(
                            resizebitmap,
                            resizebitmap.getWidth() / 2 - resizebitmap.getHeight() / 2,
                            0,
                            resizebitmap.getHeight(),
                            resizebitmap.getHeight(), null, true
                    );

                } else {

                    resizebitmap = Bitmap.createBitmap(
                            resizebitmap,
                            0,
                            resizebitmap.getHeight() / 2 - resizebitmap.getWidth() / 2,
                            resizebitmap.getWidth(),
                            resizebitmap.getWidth(), null, true
                    );
                }





                        /*test code*/


                if (getIntent().getStringExtra("from").equalsIgnoreCase("fromchatGallery") || getIntent().getStringExtra("from").equalsIgnoreCase("fromchatCamare")) {
                    authManager.setmResizeBitmap(resizebitmap);
                    Intent intent = new Intent(getApplicationContext(), EditMyProfileView.class);
                    intent.putExtra("retake", "fckoff");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    if (resizebitmap != null) {
                        authManager.setmResizeBitmap(resizebitmap);
                        authManager.setUserImageUri(Uri.parse(getIntent().getStringExtra("uri")));
                        Intent intent = new Intent(getApplicationContext(), EditMyProfileView.class);
                        intent.putExtra("retake", "fckoff");
                        setResult(Activity.RESULT_OK, intent);
                    }
                    finish();
                }
                break;
        }
    }


}
