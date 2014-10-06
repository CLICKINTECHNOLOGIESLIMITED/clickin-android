package com.sourcefuse.clickinandroid.utils;

import android.app.Activity;
import android.app.AlertDialog;
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
    private static final String VIDEO_RECORDER_FOLDER = "/ClickIn/Video";
    private static Uri fileUri = null;
    public static String videofilePath = null;
    public static final int REQUEST_VIDEO_CAPTURED = 99;
    public static final int REQUEST_VIDEO_CAPTURED_FROM_GALLERY = 100;


    public static void videoDialog(final Activity contex) {
        String[] addPhoto;
        addPhoto = new String[]{"CAPTURE A VIDEO", "FROM YOUR GALLERY"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(contex);
        dialog.setTitle("Add your video");

        dialog.setItems(addPhoto, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (id == 0) {
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        fileUri = getOutputMediaFileUri();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, REQUEST_VIDEO_CAPTURED);
                        contex.startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
                    } catch (ActivityNotFoundException e) {
                    }
                    dialog.dismiss();
                } else if (id == 1) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("video/*");
                    contex.startActivityForResult(photoPickerIntent, REQUEST_VIDEO_CAPTURED_FROM_GALLERY);
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
