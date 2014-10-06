package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class FeedsAdapter extends ArrayAdapter<NewsFeedBean> {
    Context context;
    int layoutResourceId;
    ArrayList<NewsFeedBean> eachNewsFeed;
    MediaPlayer player;
    public FeedsAdapter(Context context, int layoutResourceId,
                        ArrayList<NewsFeedBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.eachNewsFeed = item;

    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();

            holder.feed_image = (ImageView)row.findViewById(R.id.feed_image);
            holder.layout = (RelativeLayout)row.findViewById(R.id.feed_menu_layout);
            holder.feed_menu = (ImageView)row.findViewById(R.id.feed_menu_image_button);
            holder.clickedIn = (TextView)row.findViewById(R.id.clickedIn);
            holder.layout_clickin = (RelativeLayout)row.findViewById(R.id.layout_clickin);
            holder.feed_audio_button = (Button)row.findViewById(R.id.feed_audio_button);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final RecordHolder rholder = (RecordHolder) row.getTag();


        /*
        Condition for Clickin are there or not
         */
        if(!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks().equalsIgnoreCase("null"))) {

            holder.clickedIn.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks());
            holder.layout_clickin.setVisibility(View.VISIBLE);
        }else{
            holder.layout_clickin.setVisibility(View.GONE);
        }

        /*
        Condition for Audio Type 3
         */
        Log.e("FeedsAdapter++ChatDetailType",eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type());
        if(!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("3"))){
            holder.feed_audio_button.setVisibility(View.GONE);
        }else {
            holder.feed_audio_button.setVisibility(View.VISIBLE);
        }

        holder.feed_audio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initializeMediaPlayer(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());

                Uri myUri = Uri.parse(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(myUri, "audio/*");
                context.startActivity(intent);
                Log.e("FeedsAdapter++ChatDetailContent", eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
            }
        });


        /*
        Condition for Image - Type 2
         */

        if(!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("2"))){
            holder.feed_image.setVisibility(View.GONE);
        }else {
            holder.feed_image.setVisibility(View.VISIBLE);
            Log.e("FeedsAdapter++ChatDetailContent", eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
            Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content()).into(holder.feed_image);
        }


        holder.feed_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation slideLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);

                if(rholder.layout.getVisibility()==View.INVISIBLE){

                    rholder.layout.startAnimation(slideLeft);
                    rholder.layout.setVisibility(0);
                }
            }
        });
        return row;
    }

    private void initializeMediaPlayer(String audioContent) {


            player = new MediaPlayer();
            try {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(audioContent);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    static class RecordHolder {
        ImageView feed_image;
        ImageView feed_menu;
        RelativeLayout layout,layout_clickin;
        TextView clickedIn;
        Button feed_audio_button;

    }


}
