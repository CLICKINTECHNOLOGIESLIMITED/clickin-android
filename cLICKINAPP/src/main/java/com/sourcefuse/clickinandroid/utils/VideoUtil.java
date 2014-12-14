package com.sourcefuse.clickinandroid.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by mukesh on 17/7/14.
 */
public class VideoUtil {
    public static final int REQUEST_VIDEO_CAPTURED = 99;
    public static final int REQUEST_VIDEO_CAPTURED_FROM_GALLERY = 100;
    private static final String VIDEO_RECORDER_FOLDER = "/ClickIn/Video";
    public static String videofilePath = null;
    private static Uri fileUri = null;
    public Dialog mdialog;

    public static void videoDialog(final Activity contex) {


        Dialog dialog = new Dialog(contex);
        AlertDialog.Builder builder = new AlertDialog.Builder(contex);
        builder.setTitle("Choose Video Source");
        builder.setItems(new CharSequence[]{"GALLERY", "CAPTURE"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            case 0:
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("video/*");
                                contex.startActivityForResult(photoPickerIntent, REQUEST_VIDEO_CAPTURED_FROM_GALLERY);

                                break;

                            case 1:
                                try {
                                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                    fileUri = getOutputMediaFileUri();
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, REQUEST_VIDEO_CAPTURED);
                                    contex.startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
                                } catch (ActivityNotFoundException e) {
                                }

                                break;

                            default:
                                break;
                        }
                    }
                }
        );

        builder.show();
        dialog.dismiss();


        /*final Dialog mdialog = new Dialog(contex);
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        *//*mdialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*//*
        mdialog.setContentView(R.layout.alert_take_vedio);
        mdialog.setCancelable(false);

        TextView takepicture = (TextView) mdialog.findViewById(R.id.take_picture);
        TextView gallery = (TextView) mdialog.findViewById(R.id.from_gallery);
        Button cancel = (Button) mdialog.findViewById(R.id.dialog_cancel);
        takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, REQUEST_VIDEO_CAPTURED);
                    contex.startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
                } catch (ActivityNotFoundException e) {
                }
                mdialog.dismiss();


            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("video*//*");
                contex.startActivityForResult(photoPickerIntent, REQUEST_VIDEO_CAPTURED_FROM_GALLERY);
                mdialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdialog.dismiss();
            }
        });
        mdialog.show();*/
    }

    /**
     * Create a file Uri for saving  video
     */
    private static Uri getOutputMediaFileUri() {
        videofilePath = getOutputMediaFile();
        return Uri.fromFile(new File(videofilePath));
    }

    /**
     * Create a File for saving  video
     */
    private static String getOutputMediaFile() {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, VIDEO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4");
    }


}
