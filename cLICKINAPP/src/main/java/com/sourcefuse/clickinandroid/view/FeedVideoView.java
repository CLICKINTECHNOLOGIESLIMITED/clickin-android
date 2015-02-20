package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinapp.R;

/**
 * Created by charunigam on 30/10/14.
 */
public class FeedVideoView extends Activity {

    //ImageView menu;
    String url;
    ProgressDialog pDialog;
    VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));

        setContentView(R.layout.video_view);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
        }
        // Insert your Video URL
//        String VideoURL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";

        if (Utils.isConnectingToInternet(this)) {
//            try {
//
            videoview = (VideoView) findViewById(R.id.videoview);
//                MediaController mediaController = new MediaController(this);
//                mediaController.setAnchorView(videoView);
//                Uri video = Uri.parse(url);
//                videoView.setMediaController(mediaController);
//                videoView.setVideoURI(video);
//                videoView.start();
//            } catch (Exception e) {
//                // TODO: handle exception
//                Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
//            }


            // Execute StreamVideo AsyncTask

            // Create a progressbar
            pDialog = new ProgressDialog(this);
            // Set progressbar title
            pDialog.setTitle("Streaming Video");
            // Set progressbar message
            pDialog.setMessage("Buffering...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            // Show progressbar
            pDialog.show();

            try {
                // Start the MediaController
                MediaController mediacontroller = new MediaController(
                        this);
                mediacontroller.setAnchorView(videoview);
                // Get the URL from String VideoURL
                Uri video = Uri.parse(url + ".mp4");
                videoview.setMediaController(mediacontroller);
                videoview.setVideoURI(video);

            } catch (Exception e) {
                this.finish();
                Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            videoview.requestFocus();
            videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    pDialog.dismiss();
                    videoview.start();
                }
            });


        } else {
            Utils.fromSignalDialog(this, AlertMessage.connectionError);
            this.finish();
        }
    }

}
