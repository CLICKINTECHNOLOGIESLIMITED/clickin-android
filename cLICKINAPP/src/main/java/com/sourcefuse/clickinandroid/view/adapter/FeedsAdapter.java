package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
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

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.view.FeedCommentsView;
import com.sourcefuse.clickinandroid.view.FeedStarsView;
import com.sourcefuse.clickinandroid.view.Feed_large_img;
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
    AuthManager authMgr;
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
            holder.feed_video_button = (Button)row.findViewById(R.id.feed_video_button);
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
            holder.feed_comment_image_button = (ImageView)row.findViewById(R.id.feed_comment_image_button);
            holder.feed_star_image_button = (ImageView)row.findViewById(R.id.feed_star_image_button);
            holder.feed_star_user = (TextView)row.findViewById(R.id.feed_star_user);
            holder.clickedInMessage = (TextView)row.findViewById(R.id.clickedInMessage);
            holder.no_comments = (TextView)row.findViewById(R.id.no_comments);
            holder.audio_layout = (LinearLayout)row.findViewById(R.id.audio_layout);
            holder.video_layout = (RelativeLayout)row.findViewById(R.id.video_layout);
            holder.video_thumb = (ImageView)row.findViewById(R.id.video_thumb);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final RecordHolder rholder = (RecordHolder) row.getTag();
        authMgr = ModelManager.getInstance().getAuthorizationManager();


        /*
        Condition for Clickin are there or not
         */
        if(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks()!=null) {
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks().equalsIgnoreCase("null"))) {
                if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks().equalsIgnoreCase("0"))) {
                    holder.clickedIn.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks().trim());
                    holder.layout_clickin.setVisibility(View.VISIBLE);
                }
                else {
                    holder.layout_clickin.setVisibility(View.GONE);
                }
            } else {
                holder.layout_clickin.setVisibility(View.GONE);
            }
        }
        if(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message()!=null) {
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().equalsIgnoreCase("null"))) {

                holder.clickedInMessage.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().trim());
              }
            else
            {
                holder.clickedInMessage.setVisibility(View.GONE);
            }
        }
//        Log.e("FeedsAdapter++ChatDetailType",eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type());
        if(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type()!=null) {

       /* Condition for Image - Type 2
         */
            if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("2"))
            {
                holder.feed_image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content()).into(holder.feed_image);
            }
            else  if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("5"))
            {
                holder.feed_image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content()).into(holder.feed_image);
            }
            else  if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("6"))
            {
                holder.feed_image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content()).into(holder.feed_image);
            }
            else
            {
                holder.feed_image.setVisibility(View.GONE);
            }
//                if(!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("2"))){
//                holder.feed_image.setVisibility(View.GONE);
//            }else {
//                holder.feed_image.setVisibility(View.VISIBLE);
////                Log.e("FeedsAdapter++ChatDetailContent", eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
//                Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content()).into(holder.feed_image);
//            }
  /*
        Condition for Audio Type 3
         */
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("3"))) {
                holder.audio_layout.setVisibility(View.GONE);
            } else {
                holder.audio_layout.setVisibility(View.VISIBLE);
            }
             /*
             /*
        Condition for Video Type 4
         */
                if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("4"))) {
                    holder.video_layout.setVisibility(View.GONE);
                } else {
                    holder.video_layout.setVisibility(View.VISIBLE);
                }
            /*Condition for Trade Cards Type 5
                    */
//            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("5"))) {
//                holder.feed_image.setVisibility(View.GONE);
//            } else {
//                holder.feed_image.setVisibility(View.VISIBLE);
//                Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content()).into(holder.feed_image);
//            }
           /* Condition for Location Type 6
                    */
//            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("6"))) {
//                holder.feed_image.setVisibility(View.GONE);
//            } else {
//                holder.feed_image.setVisibility(View.VISIBLE);
//              Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content()).into(holder.feed_image);
//            }
        }
        else
        {
            holder.feed_image.setVisibility(View.GONE);
            holder.audio_layout.setVisibility(View.GONE);
            holder.video_layout.setVisibility(View.GONE);
        }
        if(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_video_thumb()!=null)
        {
            holder.video_thumb.setVisibility(View.VISIBLE);
            Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_video_thumb()).into(holder.video_thumb);
        }
        holder.feed_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("2")) {
                    Intent i = new Intent(context, Feed_large_img.class);
                    i.putExtra("url", eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
                    context.startActivity(i);
                }
                else  if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("5")){

                }
                else  if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("6")){

                }
            }
        });
        holder.feed_audio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initializeMediaPlayer(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());

                Uri myUri = Uri.parse(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(myUri, "audio/*");
                context.startActivity(intent);
//                Log.e("FeedsAdapter++ChatDetailContent", eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
            }
        });
        holder.feed_video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initializeMediaPlayer(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());

                Uri myUri = Uri.parse(
//                        "http://download.wavetlan.com/SVV/Media/HTTP/H264/Talkinghead_Media/H264_test1_Talkinghead_mp4_480x360.mp4");
//                        "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp");
                        eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(myUri, "video/*");
                context.startActivity(intent);


                Log.e("video url", eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
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
                    if (!(eachNewsFeed.get(position).getNewsFeedArray_senderDetail_id()).toString().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserId())) {
                        if (eachNewsFeed.get(position).getNewsFeedArray_receiverDetail_id()!=null) {
                            if (!(eachNewsFeed.get(position).getNewsFeedArray_receiverDetail_id()).toString().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserId())) {
                                holder.feed_remove_post.setVisibility(View.GONE);
                            }
                            else
                            {
                                holder.feed_remove_post.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                        {
                            holder.feed_remove_post.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        holder.feed_remove_post.setVisibility(View.VISIBLE);
                    }
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
                rholder.layout.setVisibility(View.GONE);
                holder.feed_menu.setImageResource(report_icon);
                ModelManager.getInstance().getNewsFeedManager().newFeedDelete(authMgr.getPhoneNo(),authMgr.getUsrToken(),eachNewsFeed.get(position).getNewsfeedArray_id());
            }
        });
        holder.feed_report_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rholder.layout.setVisibility(View.GONE);
                holder.feed_menu.setImageResource(report_icon);
            ModelManager.getInstance().getNewsFeedManager().reportInAppropriate(authMgr.getPhoneNo(),authMgr.getUsrToken(),eachNewsFeed.get(position).getNewsfeedArray_id());
            }
        });
        if(eachNewsFeed.get(position).getCommentArrayList()!=null)
        {
//            Log.e("eachNewsFeed.get(position).getCommentArrayList().size()",""+eachNewsFeed.get(position).getCommentArrayList().size());
            holder.feed_comments_layout.setVisibility(View.VISIBLE);
            if(eachNewsFeed.get(position).getNewsfeedArray_comments_count()>3)
            {
                holder.feed_comments_layout1.setVisibility(View.VISIBLE);
                holder.no_comments.setText("View all "+eachNewsFeed.get(position).getNewsfeedArray_comments_count()+" comments");
            for(int k=0;k<eachNewsFeed.get(position).getCommentArrayList().size();k++)
            {
                if(k==0)
                {
                    holder.name2.setVisibility(View.VISIBLE);
                    holder.comment2.setVisibility(View.VISIBLE);
                    holder.name2.setText(eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_user_name());
                    holder.comment2.setText(eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_comment());
                }
                if(k==1)
                {
                    holder.name3.setVisibility(View.VISIBLE);
                    holder.comment3.setVisibility(View.VISIBLE);
                    holder.name3.setText(eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_user_name());
                    holder.comment3.setText(eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_comment());
                }
                if(k==2)
                {holder.name4.setVisibility(View.VISIBLE);
                    holder.comment4.setVisibility(View.VISIBLE);
                    holder.name4.setText(eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_user_name());
                    holder.comment4.setText(eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_comment());
                    break;
                }
//                Log.e("cmnt4",eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_comment());
            }

            }
            else
            {
//                Log.e("name1",""+eachNewsFeed.get(position).getCommentArrayList().size());
                holder.feed_comments_layout1.setVisibility(View.GONE);
                if(eachNewsFeed.get(position).getCommentArrayList().size()==1)
                {
                    holder.name2.setVisibility(View.VISIBLE);
                    holder.comment2.setVisibility(View.VISIBLE);
                    holder.name2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name());
                    holder.comment2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment());
                    holder.name3.setVisibility(View.GONE);
                    holder.comment3.setVisibility(View.GONE);
                    holder.name4.setVisibility(View.GONE);
                    holder.comment4.setVisibility(View.GONE);
                }
                else if(eachNewsFeed.get(position).getCommentArrayList().size()==2)
                {
                    holder.name2.setVisibility(View.VISIBLE);
                    holder.comment2.setVisibility(View.VISIBLE);
                    holder.name3.setVisibility(View.VISIBLE);
                    holder.comment3.setVisibility(View.VISIBLE);

                    holder.name2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name());
                    holder.name3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_user_name());

                    holder.comment2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment());
                    holder.comment3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_comment());
                    holder.name4.setVisibility(View.GONE);
                    holder.comment4.setVisibility(View.GONE);

                }else if(eachNewsFeed.get(position).getCommentArrayList().size()==3)
                {
                    holder.name2.setVisibility(View.VISIBLE);
                    holder.comment2.setVisibility(View.VISIBLE);
                    holder.name3.setVisibility(View.VISIBLE);
                    holder.comment3.setVisibility(View.VISIBLE);
                    holder.name4.setVisibility(View.VISIBLE);
                    holder.comment4.setVisibility(View.VISIBLE);

                    holder.name2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name());
                    holder.name3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_user_name());
                    holder.name4.setText(eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_user_name());

                    holder.comment2.setText(eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment());
                    holder.comment3.setText(eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_comment());
                    holder.comment4.setText(eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_comment());
                }
                else
                {
                    holder.name2.setVisibility(View.GONE);
                    holder.comment2.setVisibility(View.GONE);
                    holder.name3.setVisibility(View.GONE);
                    holder.comment3.setVisibility(View.GONE);
                    holder.name4.setVisibility(View.GONE);
                    holder.comment4.setVisibility(View.GONE);
                    holder.feed_comments_layout1.setVisibility(View.VISIBLE);
                    holder.no_comments.setText("No Comments");
                }
            }

        }
        else
        {
            holder.feed_comments_layout.setVisibility(View.GONE);
        }

        holder.feed_comments_layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedCommentsView.class);
                intent.putExtra("news_feed_id",eachNewsFeed.get(position).getNewsfeedArray_id());
                intent.putExtra("comment_count",eachNewsFeed.get(position).getNewsfeedArray_comments_count());
                context.startActivity(intent);
            }
        });
        if(eachNewsFeed.get(position).getNewsfeedArray_user_commented().equalsIgnoreCase("1"))
        {
            holder.feed_comment_image_button.setImageResource(R.drawable.pink_comment_btn);
        }
        else
            holder.feed_comment_image_button.setImageResource(R.drawable.comment_btn);

        if(eachNewsFeed.get(position).getNewsfeedArray_user_starred().equalsIgnoreCase("1"))
        {
            holder.feed_star_image_button.setImageResource(R.drawable.pink_star_btn);
        }
        else
            holder.feed_star_image_button.setImageResource(R.drawable.star_btn);


        holder.feed_star_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String stars = holder.feed_star_user.getText().toString().trim();
                    if(stars.contains(authMgr.getUserName()))
                    {
                        holder.feed_star_image_button.setImageResource(R.drawable.star_btn);

                        stars = stars.replace(authMgr.getUserName(),"").trim();
                        if(stars.startsWith(","))
                            stars = stars.replaceFirst(",","").trim();
                        if(stars.endsWith(","))
                            stars = stars.substring(0,stars.lastIndexOf(",")-1);
                        if(stars.equalsIgnoreCase("")) {
                            stars = "No Stars";
                            holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.dark_gray));
                            holder.feed_star_user.setClickable(false);
                        }
                        holder.feed_star_user.setText(stars);
                        ModelManager.getInstance().getNewsFeedManager().unStarredNewsFeed(authMgr.getPhoneNo(),authMgr.getUsrToken(),eachNewsFeed.get(position).getNewsfeedArray_id());
                    }

                else {
                    holder.feed_star_image_button.setImageResource(R.drawable.pink_star_btn);
                        if(stars.equalsIgnoreCase("No Stars"))
                            stars = "";
                        if(stars.startsWith(","))
                            stars = stars.replaceFirst(",","").trim();
                        if(stars.endsWith(","))
                            stars = stars.substring(0,stars.lastIndexOf(",")-1);

                        holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.feed_senderuser));
                        holder.feed_star_user.setClickable(true);
                        if(stars.equalsIgnoreCase(""))
                            holder.feed_star_user.setText(ModelManager.getInstance().getAuthorizationManager().getUserName());
                        else
                            holder.feed_star_user.setText(stars+", "+ ModelManager.getInstance().getAuthorizationManager().getUserName());
                    ModelManager.getInstance().getNewsFeedManager().saveStarComment(authMgr.getPhoneNo(), authMgr.getUsrToken(),eachNewsFeed.get(position).getNewsfeedArray_id(),"","star");
                }
            }
        });
        holder.feed_comment_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedCommentsView.class);
                intent.putExtra("news_feed_id",eachNewsFeed.get(position).getNewsfeedArray_id());
                intent.putExtra("comment_count",eachNewsFeed.get(position).getNewsfeedArray_comments_count());
                context.startActivity(intent);
            }
        });
        holder.feed_star_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedStarsView.class);
                intent.putExtra("news_feed_id",eachNewsFeed.get(position).getNewsfeedArray_id());
                context.startActivity(intent);
            }
        });
//        Log.e("stars count",""+eachNewsFeed.get(position).getNewsfeedArray_stars_count());
        if(eachNewsFeed.get(position).getNewsfeedArray_stars_count()==0)
        {
            holder.feed_star_user.setText("No Stars");
            holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.dark_gray));
            holder.feed_star_user.setClickable(false);
        }
        else
        {
//            Log.e("stars count ar size",""+eachNewsFeed.get(position).getStarredArrayList().size());
            if(eachNewsFeed.get(position).getNewsfeedArray_stars_count()>5)
            {
                holder.feed_star_user.setText(eachNewsFeed.get(position).getNewsfeedArray_stars_count()+" Stars");
                holder.feed_star_user.setTextColor(Color.BLACK);
                holder.feed_star_user.setClickable(false);
            }
            else {
                String stars_text = "";
                for (int i = 0; i < eachNewsFeed.get(position).getStarredArrayList().size(); i++) {
                    if (i == eachNewsFeed.get(position).getStarredArrayList().size() - 1) {
                        stars_text += eachNewsFeed.get(position).getStarredArrayList().get(i).getNewsFeedArray_starredArray_user_name();
                    } else {
                        stars_text += eachNewsFeed.get(position).getStarredArrayList().get(i).getNewsFeedArray_starredArray_user_name() + ", ";
                    }
                }
//            Log.e("stars ",stars_text);
                holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.feed_senderuser));
                holder.feed_star_user.setText(stars_text);
                holder.feed_star_user.setClickable(true);
            }
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
        TextView clickedIn,feed_star_user,clickedInMessage;
        Button feed_audio_button,feed_video_button;
        LinearLayout feed_comments_layout4,feed_comments_layout1,feed_comments_layout2,feed_comments_layout3,feed_comments_layout;
        TextView name2,comment2, name3, comment3, name4, comment4,no_comments;
        ImageView feed_star_image_button,feed_comment_image_button;
        LinearLayout audio_layout;
        RelativeLayout video_layout;
        ImageView video_thumb;
    }


}
