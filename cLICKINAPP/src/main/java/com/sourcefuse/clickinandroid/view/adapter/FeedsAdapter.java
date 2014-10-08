package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import static com.sourcefuse.clickinapp.R.drawable.report_icon;

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
        final RecordHolder holder;
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
            holder.feed_report_post = (ImageView)row.findViewById(R.id.feed_report_post);
            holder.feed_remove_post = (ImageView)row.findViewById(R.id.feed_remove_post);
            holder.feed_comments_layout1 = (LinearLayout)row.findViewById(R.id.feed_comments_layout1);
            holder.feed_comments_layout2 = (LinearLayout)row.findViewById(R.id.feed_comments_layout2);
            holder.feed_comments_layout3 = (LinearLayout)row.findViewById(R.id.feed_comments_layout3);
            holder.feed_comments_layout4 = (LinearLayout)row.findViewById(R.id.feed_comments_layout4);
            holder.name2 = (TextView)row.findViewById(R.id.name2);
            holder.name3 = (TextView)row.findViewById(R.id.name3);
            holder.name4 = (TextView)row.findViewById(R.id.name4);
            holder.comment2 = (TextView)row.findViewById(R.id.comment2);
            holder.comment3 = (TextView)row.findViewById(R.id.comment3);
            holder.comment4 = (TextView)row.findViewById(R.id.comment4);
            holder.feed_comments_layout = (LinearLayout)row.findViewById(R.id.feed_comments_layout);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final RecordHolder rholder = (RecordHolder) row.getTag();


        /*
        Condition for Clickin are there or not
         */
        if(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks()!=null) {
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks().equalsIgnoreCase("null"))) {

                holder.clickedIn.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks());
                holder.layout_clickin.setVisibility(View.VISIBLE);
            } else {
                holder.layout_clickin.setVisibility(View.GONE);
            }
        }

        Log.e("FeedsAdapter++ChatDetailType",eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type());
        if(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type()!=null) {
             /*
        Condition for Audio Type 3
         */
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("3"))) {
                holder.feed_audio_button.setVisibility(View.GONE);
            } else {
                holder.feed_audio_button.setVisibility(View.VISIBLE);
            }
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
        }
        else
        {
            holder.feed_image.setVisibility(View.GONE);
            holder.feed_audio_button.setVisibility(View.GONE);
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

        holder.feed_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation slideLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
                Animation slideRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);

                if(rholder.layout.getVisibility()==View.GONE){

                    rholder.layout.startAnimation(slideLeft);
                    rholder.layout.setVisibility(View.VISIBLE);
                    holder.feed_menu.setImageResource(R.drawable.cross_icon);
                }
                else
                {

                    rholder.layout.setVisibility(View.GONE);
                    holder.feed_menu.setImageResource(report_icon);
                }
            }
        });
        holder.feed_remove_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.feed_report_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if(eachNewsFeed.get(position).getCommentArrayList()!=null)
        {Log.e("eachNewsFeed.get(position).getCommentArrayList().size()",""+eachNewsFeed.get(position).getCommentArrayList().size());
            holder.feed_comments_layout.setVisibility(View.VISIBLE);
            if(eachNewsFeed.get(position).getCommentArrayList().size()>3)
            {
                holder.feed_comments_layout1.setVisibility(View.VISIBLE);

                holder.name2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name());
                holder.name3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_user_name());
                holder.name4.setText(eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_user_name());

                holder.comment2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment());
                holder.comment3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_comment());
                holder.comment4.setText(eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_comment());
            }
            else
            {
//                Log.e("name1",""+eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name());
                holder.feed_comments_layout1.setVisibility(View.GONE);
                if(eachNewsFeed.get(position).getCommentArrayList().size()==1)
                {
                    holder.name2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name());
                    holder.comment2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment());
                    holder.name3.setVisibility(View.GONE);
                    holder.comment3.setVisibility(View.GONE);
                    holder.name4.setVisibility(View.GONE);
                    holder.comment4.setVisibility(View.GONE);
                }
                else if(eachNewsFeed.get(position).getCommentArrayList().size()==2)
                {
                    holder.name2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name());
                    holder.name3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_user_name());

                    holder.comment2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment());
                    holder.comment3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_comment());
                    holder.name4.setVisibility(View.GONE);
                    holder.comment4.setVisibility(View.GONE);

                }else if(eachNewsFeed.get(position).getCommentArrayList().size()==3)
                {
                    holder.name2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name());
                    holder.name3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_user_name());
                    holder.name4.setText(eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_user_name());

                    holder.comment2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment());
                    holder.comment3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_comment());
                    holder.comment4.setText(eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_comment());
                }
                else
                {
                    holder.feed_comments_layout.setVisibility(View.GONE);
//                    holder.name2.setVisibility(View.GONE);
//                    holder.name3.setVisibility(View.GONE);
//                    holder.name4.setVisibility(View.GONE);
//
//                    holder.comment2.setVisibility(View.GONE);
//                    holder.comment3.setVisibility(View.GONE);
//                    holder.comment4.setVisibility(View.GONE);
                }
            }

        }
        else
        {
            holder.feed_comments_layout.setVisibility(View.GONE);
//            holder.feed_comments_layout1.setVisibility(View.GONE);
//            holder.name2.setVisibility(View.GONE);
//            holder.name3.setVisibility(View.GONE);
//            holder.name4.setVisibility(View.GONE);
//
//            holder.comment2.setVisibility(View.GONE);
//            holder.comment3.setVisibility(View.GONE);
//            holder.comment4.setVisibility(View.GONE);
        }
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
        ImageView feed_image,feed_remove_post,feed_report_post;
        ImageView feed_menu;
        RelativeLayout layout,layout_clickin;
        TextView clickedIn;
        Button feed_audio_button;
        LinearLayout feed_comments_layout4,feed_comments_layout1,feed_comments_layout2,feed_comments_layout3,feed_comments_layout;
        TextView name2,comment2, name3, comment3, name4, comment4;

    }


}
