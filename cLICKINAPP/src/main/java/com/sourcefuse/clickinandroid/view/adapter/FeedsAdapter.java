package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FeedCommentsView;
import com.sourcefuse.clickinandroid.view.FeedStarsView;
import com.sourcefuse.clickinandroid.view.Feed_large_img;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinandroid.view.MapView;
import com.sourcefuse.clickinandroid.view.UserProfileView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.sourcefuse.clickinapp.R.drawable.report_icon;

public class FeedsAdapter extends ArrayAdapter<NewsFeedBean> implements
        StickyListHeadersAdapter, SectionIndexer {
    Activity context;
    int layoutResourceId;
    ArrayList<NewsFeedBean> eachNewsFeed;
    ArrayList<String> mId;
    MediaPlayer player;
    AuthManager authMgr;
    Typeface typeface, typeface_avenera_medium;

    ArrayList<String> senderName;
    ArrayList<String> senderId;
    ArrayList<String> receiverName;
    ArrayList<String> receiverId;
    ArrayList<Integer> mHeaderPositions;
    ArrayList<String> recieverImages;
    ArrayList<String> senderImages;
    ArrayList<String> senderPhNo;
    ArrayList<String> recieverPhNo;
    ArrayList<String> timeOfFeed;
    private LayoutInflater mInflater;


    public FeedsAdapter(Activity context, int layoutResourceId,
                        ArrayList<NewsFeedBean> item,
                        ArrayList<Integer> mHeaderPositions,
                        ArrayList<String> mSenderName,

                        ArrayList<String> mReciverName,
                        ArrayList<String> mSenderImages,
                        ArrayList<String> mReciverImages,
                        ArrayList<String> mTimeOfFeed,
                        ArrayList<String> mSenderId,
                        ArrayList<String> mReciverId,
                        ArrayList<String> mSenderPhNo,
                        ArrayList<String> mReciverPhNo) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.eachNewsFeed = item;

        this.senderName = mSenderName;
        this.senderId = mSenderId;
        this.receiverName = mReciverName;
        this.receiverId = mReciverId;
        this.mHeaderPositions = mHeaderPositions;
        this.recieverImages = mReciverImages;
        this.senderImages = mSenderImages;
        this.recieverPhNo = mReciverPhNo;
        this.timeOfFeed = mTimeOfFeed;
        this.senderPhNo = mSenderPhNo;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final RecordHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();

            holder.feed_image = (ImageView) row.findViewById(R.id.feed_image);
            holder.mLoadImage = (ProgressBar) row.findViewById(R.id.LoadImage);
//            holder.clicks_heart_top = (ImageView) row.findViewById(R.id.clicks_heart_top);
//            holder.clicks_heart_bottom = (ImageView) row.findViewById(R.id.clicks_heart_bottom);

            holder.layout = (RelativeLayout) row.findViewById(R.id.feed_menu_layout);
            holder.feed_menu = (ImageView) row.findViewById(R.id.feed_menu_image_button);
            holder.clickedIn = (TextView) row.findViewById(R.id.clickedIn);
            holder.custom_message = (TextView) row.findViewById(R.id.custom_message);
            holder.layout_clickin = (LinearLayout) row.findViewById(R.id.layout_clickin);
            holder.cards_relative = (RelativeLayout) row.findViewById(R.id.cards_relative);
            holder.feed_audio_button = (Button) row.findViewById(R.id.feed_audio_button);
            holder.feed_video_button = (Button) row.findViewById(R.id.feed_video_button);
            holder.feed_report_post = (ImageView) row.findViewById(R.id.feed_report_post);
            holder.feed_remove_post = (ImageView) row.findViewById(R.id.feed_remove_post);
            holder.feed_comments_layout1 = (LinearLayout) row.findViewById(R.id.feed_comments_layout1);
            holder.feed_comments_layout2 = (LinearLayout) row.findViewById(R.id.feed_comments_layout2);
            holder.feed_comments_layout3 = (LinearLayout) row.findViewById(R.id.feed_comments_layout3);
            holder.feed_comments_layout4 = (LinearLayout) row.findViewById(R.id.feed_comments_layout4);


            holder.comment2 = (TextView) row.findViewById(R.id.comment2);
            holder.comment3 = (TextView) row.findViewById(R.id.comment3);
            holder.comment4 = (TextView) row.findViewById(R.id.comment4);
            holder.feed_comments_layout = (LinearLayout) row.findViewById(R.id.feed_comments_layout);
            holder.feed_comment_image_button = (ImageView) row.findViewById(R.id.feed_comment_image_button);
            holder.feed_star_image_button = (ImageView) row.findViewById(R.id.feed_star_image_button);
            holder.feed_star_user = (TextView) row.findViewById(R.id.feed_star_user);
            holder.clickedInMessage = (TextView) row.findViewById(R.id.clickedInMessage);
            holder.clickedInMessageLong = (TextView) row.findViewById(R.id.clickedInMessageLong);

            holder.no_comments = (TextView) row.findViewById(R.id.no_comments);
            holder.audio_layout = (LinearLayout) row.findViewById(R.id.audio_layout);
            holder.video_layout = (RelativeLayout) row.findViewById(R.id.video_layout);
            holder.video_thumb = (ImageView) row.findViewById(R.id.video_thumb);
            holder.card_layout = (LinearLayout) row.findViewById(R.id.card_layout);
            holder.card_count1 = (TextView) row.findViewById(R.id.card_count_1);
            holder.card_count2 = (TextView) row.findViewById(R.id.card_count_2);
            holder.card_status = (TextView) row.findViewById(R.id.card_status);
            holder.clickInWhiteImage = (ImageView) row.findViewById(R.id.clickInWhiteImage);
            holder.card_msg = (TextView) row.findViewById(R.id.card_msg);
            holder.card_title = (TextView) row.findViewById(R.id.card_title);
            holder.card_status_img = (ImageView) row.findViewById(R.id.card_status_img);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final RecordHolder rholder = (RecordHolder) row.getTag();
        authMgr = ModelManager.getInstance().getAuthorizationManager();


        /*
        Condition for Clickin are there or not
         */
        /*
        Condition for Clickin are there or not
         */
        holder.layout.setVisibility(GONE);
        holder.feed_menu.setVisibility(VISIBLE);
        holder.feed_menu.setImageResource(report_icon);
        holder.mLoadImage.setVisibility(GONE);

        if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks() != null) {
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks().equalsIgnoreCase("null"))) {
                if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks().equalsIgnoreCase("0"))) {

                    holder.clickedIn.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_clicks().trim());
                    holder.clickedIn.setTextColor(Color.WHITE);
                    holder.clickedIn.setTypeface(null, Typeface.BOLD);
                    holder.layout_clickin.setVisibility(VISIBLE);
                    holder.layout_clickin.setBackgroundResource(R.color.feed_clickin);
                    holder.clickedInMessage.setVisibility(VISIBLE);
                    holder.clickInWhiteImage.setVisibility(VISIBLE);
                    holder.clickedIn.setVisibility(VISIBLE);
                    holder.layout_clickin.setVisibility(VISIBLE);
                    if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().equalsIgnoreCase("null"))) {
                        if (!eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().equalsIgnoreCase("")) {


                            if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().length() > 30) {

                                holder.clickedInMessage.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().trim().substring(0, 30));
                                holder.clickedInMessageLong.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().trim().substring(30, (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().length()-1)));
                                holder.clickedInMessageLong.setVisibility(VISIBLE);
                            } else {
                                holder.clickedInMessageLong.setVisibility(GONE);

                                holder.clickedInMessage.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().trim());
                            }
                        }
                    } else {
                        holder.clickedInMessage.setVisibility(GONE);
                        holder.clickedInMessageLong.setVisibility(GONE);
                    }
                } else {
                    if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().equalsIgnoreCase("null"))) {
                        if (!eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().equalsIgnoreCase("")) {
                            holder.clickedInMessageLong.setVisibility(GONE);
                            holder.clickedInMessage.setVisibility(GONE);
                            holder.clickedIn.setVisibility(VISIBLE);
                            holder.clickedIn.setTextColor(Color.BLACK);
                            holder.clickedIn.setTypeface(null, Typeface.NORMAL);
                            holder.clickedIn.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().trim());

                            holder.layout_clickin.setBackgroundResource(R.color.lightest_gray);
                            holder.layout_clickin.setVisibility(VISIBLE);
//                            holder.clickedInMessage.setVisibility(View.GONE);
                            holder.clickInWhiteImage.setVisibility(GONE);
                        } else {
                            holder.layout_clickin.setVisibility(GONE);
                        }
                    } else {
                        holder.layout_clickin.setVisibility(GONE);
                    }
                }
            } else {
                if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().equalsIgnoreCase("null"))) {
                    if (!eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().trim().equalsIgnoreCase("")) {
                        holder.clickedInMessageLong.setVisibility(GONE);
                        holder.clickedInMessage.setVisibility(GONE);
                        holder.clickedIn.setVisibility(VISIBLE);
                        holder.layout_clickin.setVisibility(VISIBLE);
                        holder.clickedIn.setTextColor(Color.BLACK);
                        holder.clickedIn.setTypeface(null, Typeface.NORMAL);
                        holder.clickedIn.setText(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_message().trim());

                        holder.layout_clickin.setBackgroundResource(R.color.lightest_gray);
//                        holder.clickedInMessage.setVisibility(View.GONE);
                        holder.clickInWhiteImage.setVisibility(GONE);
                    } else {
                        holder.layout_clickin.setVisibility(GONE);
                    }
                } else {
                    holder.layout_clickin.setVisibility(GONE);
                }

            }
        } else {
            holder.layout_clickin.setVisibility(GONE);
        }


        if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type() != null) {

       /* Condition for Image - Type 2 & 6
         */
            if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("2") || eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("6")) {
                holder.feed_image.setVisibility(VISIBLE);

                holder.mLoadImage.setVisibility(VISIBLE);
                Picasso.with(context).load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content()).into(holder.feed_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mLoadImage.setVisibility(GONE);
                    }

                    @Override
                    public void onError() {
                        holder.mLoadImage.setVisibility(GONE);
                    }
                });
            } else {
                holder.feed_image.setVisibility(GONE);
            }

  /*
        Condition for Audio Type 3
         */
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("3"))) {
                holder.audio_layout.setVisibility(GONE);
            } else {
                holder.audio_layout.setVisibility(VISIBLE);
            }
             /*
             /*
        Condition for Video Type 4
         */
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("4"))) {
                holder.video_layout.setVisibility(GONE);
            } else {
                holder.video_layout.setVisibility(VISIBLE);
            }


            /*Condition for Trade Cards Type 5
                    */
            //akshit code for trade cards
            if (!(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("5"))) {
                holder.card_layout.setVisibility(GONE);
            } else {
//                String clicks ;
                holder.card_layout.setVisibility(VISIBLE);
                JSONArray cards = eachNewsFeed.get(position).getNewsFeedArray_chatDetail_cards();

                if (cards.length() >= 10) {

                    try {
                        if (Utils.isEmptyString(cards.get(2).toString()) || cards.get(2).toString().length() <= 3) {
                            holder.cards_relative.setBackgroundResource(R.drawable.tradecardpink_big);


                            holder.card_title.setVisibility(GONE);
                            holder.card_msg.setVisibility(GONE);
                            holder.custom_message.setVisibility(VISIBLE);

                            holder.custom_message.setText(cards.get(1).toString());

                        } else {
                            holder.cards_relative.setBackgroundResource(R.drawable.tradecardbg_blank);
                            holder.custom_message.setVisibility(GONE);
                            holder.card_title.setVisibility(VISIBLE);
                            holder.card_msg.setVisibility(VISIBLE);

                            holder.card_title.setText(cards.get(1).toString());
                            holder.card_msg.setText(cards.get(2).toString());
                        }
                        if (cards.get(4).toString().equalsIgnoreCase("5")) {
                            holder.card_count1.setText("05");
                            holder.card_count2.setText("05");
                        } else {
                            holder.card_count1.setText(cards.get(4).toString());
                            holder.card_count2.setText(cards.get(4).toString());
                        }

                        holder.card_status.setText(cards.get(5).toString() + "!");
                        if (cards.get(5).toString().equalsIgnoreCase("accepted"))
                            holder.card_status_img.setImageResource(R.drawable.accepted_btn);
                        else
                            holder.card_status_img.setImageResource(R.drawable.rejected_btn);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }//ends

        } else {
            holder.feed_image.setVisibility(GONE);
            holder.audio_layout.setVisibility(GONE);
            holder.video_layout.setVisibility(GONE);
        }
        if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_video_thumb() != null) {
            holder.video_thumb.setVisibility(VISIBLE);
            Picasso.with(context)
                    .load(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_video_thumb())
                    .into(holder.video_thumb);
        }
        holder.feed_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //To track through mixPanel.If User View attached image from feed
                Utils.trackMixpanel((Activity) context, "Activity", "ViewAttachedMedia", "LeftMenuTheFeedButtonClicked", false);
                if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("2")) {
                    Intent i = new Intent(context, Feed_large_img.class);
                    i.putExtra("url", eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
                    context.startActivity(i);
                } else if (eachNewsFeed.get(position).getNewsFeedArray_chatDetail_type().equalsIgnoreCase("6")) {
                    Intent i = new Intent(context, MapView.class);
                    i.putExtra("coordinates", eachNewsFeed.get(position).getNewsFeedArray_chatDetail_location_coordinates());
                    context.startActivity(i);
                }
            }
        });
        holder.feed_audio_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //To track through mixPanel.If User play attached Audio from feed
                Utils.trackMixpanel((Activity) context, "Activity", "ViewAttachedMedia", "LeftMenuTheFeedButtonClicked", false);
                Uri myUri = Uri.parse(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(myUri, "audio/*");
                context.startActivity(intent);


            }
        });
        holder.feed_video_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //To track through mixPanel.If User Play attached Video from feed
                Utils.trackMixpanel((Activity) context, "Activity", "ViewAttachedMedia", "LeftMenuTheFeedButtonClicked", false);
                Uri intentUri = Uri.parse(eachNewsFeed.get(position).getNewsFeedArray_chatDetail_content());

                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.setDataAndType(intentUri, "video/*");
                try {
                    context.startActivity(intent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        holder.feed_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation slideLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
                Animation slideRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);


                if (rholder.layout.getVisibility() == GONE) {

                    //To track through mixPanel.If User View's more option in feed
                    Utils.trackMixpanel((Activity) context, "Activity", "MoreButtonClicked", "LeftMenuTheFeedButtonClicked", false);
                    rholder.layout.startAnimation(slideLeft);
                    rholder.layout.setVisibility(VISIBLE);
                    holder.feed_menu.setImageResource(R.drawable.cross_icon);
                    if (!(eachNewsFeed.get(position).getNewsFeedArray_senderDetail_id()).toString().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserId())) {
                        if (eachNewsFeed.get(position).getNewsFeedArray_receiverDetail_id() != null) {
                            if (!(eachNewsFeed.get(position).getNewsFeedArray_receiverDetail_id()).toString().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserId())) {
                                holder.feed_remove_post.setVisibility(GONE);
                            } else {
                                holder.feed_remove_post.setVisibility(VISIBLE);
                            }
                        } else {
                            holder.feed_remove_post.setVisibility(GONE);
                        }
                    } else {
                        holder.feed_remove_post.setVisibility(VISIBLE);
                    }
                } else {

                    rholder.layout.setVisibility(GONE);
                    holder.feed_menu.setImageResource(report_icon);
                }
            }
        });
        holder.feed_remove_post.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.launchBarDialog(context);
                rholder.layout.setVisibility(GONE);
                holder.feed_menu.setImageResource(report_icon);
                ModelManager.getInstance().getNewsFeedManager().newFeedDelete(authMgr.getPhoneNo(), authMgr.getUsrToken(), eachNewsFeed.get(position).getNewsfeedArray_id());
            }
        });
        holder.feed_report_post.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rholder.layout.setVisibility(GONE);
                holder.feed_menu.setImageResource(report_icon);
                ModelManager.getInstance().getNewsFeedManager().reportInAppropriate(authMgr.getPhoneNo(), authMgr.getUsrToken(), eachNewsFeed.get(position).getNewsfeedArray_id());
            }
        });
        if (eachNewsFeed.get(position).getCommentArrayList() != null) {


            holder.feed_comments_layout.setVisibility(VISIBLE);
            if (eachNewsFeed.get(position).getNewsfeedArray_comments_count() > 3) {
                holder.feed_comments_layout1.setVisibility(VISIBLE);

                holder.no_comments.setTypeface(null, Typeface.NORMAL);
                String html = "<font><b>" + "View all " + eachNewsFeed.get(position).getNewsfeedArray_comments_count() + " comments" + "</b></font>";
                holder.no_comments.setText(Html.fromHtml(html));

                for (int k = 0; k < eachNewsFeed.get(position).getCommentArrayList().size(); k++) {

                    if (k == 0) {
                        holder.comment2.setVisibility(VISIBLE);
                        String text = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_comment() + "</font>";

                        holder.comment2.setText(Html.fromHtml(text));
                    }
                    if (k == 1) {
                        holder.comment3.setVisibility(VISIBLE);
                        String text = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_comment() + "</font>";

                        holder.comment3.setText(Html.fromHtml(text));
                    }

                    if (k == 2) {
                        holder.comment4.setVisibility(VISIBLE);
                        String text = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(k).getNewsFeedArray_commentArray_comment() + "</font>";

                        holder.comment4.setText(Html.fromHtml(text));
                        break;
                    }
                }

            } else {
                holder.feed_comments_layout1.setVisibility(GONE);
                if (eachNewsFeed.get(position).getCommentArrayList().size() == 1) {
                    holder.comment2.setVisibility(VISIBLE);
                    holder.comment3.setVisibility(GONE);
                    holder.comment4.setVisibility(GONE);
                    String text = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment() + "</font>";

                    holder.comment2.setText(Html.fromHtml(text));


                } else if (eachNewsFeed.get(position).getCommentArrayList().size() == 2) {
                    holder.comment2.setVisibility(VISIBLE);
                    holder.comment3.setVisibility(VISIBLE);

                    String text = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment() + "</font>";

                    holder.comment2.setText(Html.fromHtml(text));
                    String text2 = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_comment() + "</font>";

                    holder.comment3.setText(Html.fromHtml(text2));


                } else if (eachNewsFeed.get(position).getCommentArrayList().size() == 3) {
                    holder.comment2.setVisibility(VISIBLE);
                    holder.comment3.setVisibility(VISIBLE);
                    holder.comment4.setVisibility(VISIBLE);


                    String text = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(0).getNewsFeedArray_commentArray_comment() + "</font>";

                    holder.comment2.setText(Html.fromHtml(text));
                    String text2 = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(1).getNewsFeedArray_commentArray_comment() + "</font>";

                    holder.comment3.setText(Html.fromHtml(text2));
                    String text3 = "<font color=#01DFD7><b>" + eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_user_name() + "</b></font> <font color=#000000>" + eachNewsFeed.get(position).getCommentArrayList().get(2).getNewsFeedArray_commentArray_comment() + "</font>";

                    holder.comment4.setText(Html.fromHtml(text3));

                } else {
                    typeface_avenera_medium = Typeface.createFromAsset(getContext().getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
                    holder.comment2.setVisibility(GONE);

                    holder.comment3.setVisibility(GONE);

                    holder.comment4.setVisibility(GONE);
                    holder.feed_comments_layout1.setVisibility(VISIBLE);
                    String no_comment = "<font ><b>" + "No Comments" + "</b></font>";
                    holder.no_comments.setText(Html.fromHtml(no_comment));
//                    holder.no_comments.setTypeface(typeface_avenera_medium);
//                    holder.no_comments.setText("No Comments");
                }
            }

        } else {
            holder.feed_comments_layout.setVisibility(GONE);
        }

        holder.feed_comments_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, FeedCommentsView.class);
                intent.putExtra("news_feed_id", eachNewsFeed.get(position).getNewsfeedArray_id());
                intent.putExtra("comment_count", eachNewsFeed.get(position).getNewsfeedArray_comments_count());
                context.startActivity(intent);

            }
        });
        holder.feed_comments_layout1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, FeedCommentsView.class);
                intent.putExtra("news_feed_id", eachNewsFeed.get(position).getNewsfeedArray_id());
                intent.putExtra("comment_count", eachNewsFeed.get(position).getNewsfeedArray_comments_count());
                context.startActivity(intent);
            }
        });
        if (eachNewsFeed.get(position).getNewsfeedArray_user_commented().equalsIgnoreCase("1")) {
            holder.feed_comment_image_button.setImageResource(R.drawable.pink_comment_btn);
        } else
            holder.feed_comment_image_button.setImageResource(R.drawable.comment_btn);

        if (eachNewsFeed.get(position).getNewsfeedArray_user_starred().equalsIgnoreCase("1")) {
            holder.feed_star_image_button.setImageResource(R.drawable.pink_star_btn);
        } else {
            holder.feed_star_image_button.setImageResource(R.drawable.star_btn);
        }


        holder.feed_star_image_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //To track through mixPanel.If User click's on star Button from feed
                Utils.trackMixpanel((Activity) context, "Activity", "StarButtonPressed", "LeftMenuTheFeedButtonClicked", false);
                String stars = holder.feed_star_user.getText().toString().trim();
                if (stars.contains(authMgr.getUserName())) {
                    holder.feed_star_image_button.setImageResource(R.drawable.star_btn);

                    stars = stars.replace(authMgr.getUserName(), "").trim();
                    if (stars.startsWith(","))
                        stars = stars.replaceFirst(",", "").trim();
                    if (stars.endsWith(","))
                        stars = stars.substring(0, stars.lastIndexOf(","));
                    if (stars.equalsIgnoreCase("")) {
                        stars = "No Stars";
                        holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.dark_gray));
                        holder.feed_star_user.setClickable(false);
                    }
                    String star = "<font><b>" + stars + "</b></font>";
                    holder.feed_star_user.setText(Html.fromHtml(star));
                    ModelManager.getInstance().getNewsFeedManager().unStarredNewsFeed(authMgr.getPhoneNo(), authMgr.getUsrToken(), eachNewsFeed.get(position).getNewsfeedArray_id());
                } else {
                    if (eachNewsFeed.get(position).getNewsfeedArray_user_starred().equalsIgnoreCase("1")) {
                        eachNewsFeed.get(position).setNewsfeedArray_user_starred("0");
                        holder.feed_star_image_button.setImageResource(R.drawable.star_btn);
                        if (stars.equalsIgnoreCase("No Stars"))
                            stars = "";
                        if (stars.startsWith(","))
                            stars = stars.replaceFirst(",", "").trim();
                        if (stars.endsWith(","))
                            stars = stars.substring(0, stars.lastIndexOf(","));


                        holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.feed_senderuser));
                        holder.feed_star_user.setClickable(true);
                        if (stars.equalsIgnoreCase("")) {
                            String name_user = "<font><b>" + ModelManager.getInstance().getAuthorizationManager().getUserName() + "</b></font>";
                            holder.feed_star_user.setText(Html.fromHtml(name_user));
                        } else if (stars.endsWith("Stars")) {
                            stars = stars.substring(0, stars.indexOf(" "));
                            int star = Integer.parseInt(stars) - 1;
                            String number_stars = "<font><b>" + star + " Stars" + "</b></font>";
                            holder.feed_star_user.setText(Html.fromHtml(number_stars));
                            holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.dark_gray));//akshit code Black is not needed
                        } else {
                            String st_star = "<font><b>" + stars + ", " + ModelManager.getInstance().getAuthorizationManager().getUserName() + "</b></font>";
                            holder.feed_star_user.setText(Html.fromHtml(st_star));
                        }
                        ModelManager.getInstance().getNewsFeedManager().unStarredNewsFeed(authMgr.getPhoneNo(), authMgr.getUsrToken(), eachNewsFeed.get(position).getNewsfeedArray_id());
                    } else {

                        eachNewsFeed.get(position).setNewsfeedArray_user_starred("1");
                        holder.feed_star_image_button.setImageResource(R.drawable.pink_star_btn);
                        if (stars.equalsIgnoreCase("No Stars"))
                            stars = "";
                        if (stars.startsWith(","))
                            stars = stars.replaceFirst(",", "").trim();
                        if (stars.endsWith(","))
                            stars = stars.substring(0, stars.lastIndexOf(","));


                        holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.feed_senderuser));
                        holder.feed_star_user.setClickable(true);
                        if (stars.equalsIgnoreCase("")) {
                            String html_text = "<font><b>" + ModelManager.getInstance().getAuthorizationManager().getUserName() + "</b></font>";
                            holder.feed_star_user.setText(Html.fromHtml(html_text));
                        } else if (stars.endsWith("Stars")) {
                            stars = stars.substring(0, stars.indexOf(" "));
                            int star = Integer.parseInt(stars) + 1;
                            String star_number = "<font><b>" + star + " Stars" + "</b></font>";
                            holder.feed_star_user.setText(Html.fromHtml(star_number));
                            holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.dark_gray));//akshit code Black is not needed
                        } else {
                            String star_number = "<font><b>" + stars + ", " + ModelManager.getInstance().getAuthorizationManager().getUserName() + "</b></font>";
                            holder.feed_star_user.setText(Html.fromHtml(star_number));
                        }
                        ModelManager.getInstance().getNewsFeedManager().saveStarComment(authMgr.getPhoneNo(), authMgr.getUsrToken(), eachNewsFeed.get(position).getNewsfeedArray_id(), "", "star");
                    }

                }
            }
        });
        holder.feed_comment_image_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedCommentsView.class);
                intent.putExtra("news_feed_id", eachNewsFeed.get(position).getNewsfeedArray_id());
                intent.putExtra("comment_count", eachNewsFeed.get(position).getNewsfeedArray_comments_count());
                context.startActivity(intent);
                //To track through mixPanel.If user clicks on comment button
                Utils.trackMixpanel((Activity) context, "Activity", "CommentsButtonPressed", "LeftMenuTheFeedButtonClicked", false);
            }
        });
        holder.feed_star_user.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedStarsView.class);
                intent.putExtra("news_feed_id", eachNewsFeed.get(position).getNewsfeedArray_id());
                context.startActivity(intent);
            }
        });
        if (eachNewsFeed.get(position).getNewsfeedArray_stars_count() == 0) {
            String no_stars = "<font><b>" + "No Stars" + "</b></font>";
            holder.feed_star_user.setText(Html.fromHtml(no_stars));
            holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.dark_gray));//akshit code Black is not needed
            holder.feed_star_user.setClickable(false);
        } else {
            if (eachNewsFeed.get(position).getNewsfeedArray_stars_count() > 5) {
                String stars_grater = "<font><b>" + eachNewsFeed.get(position).getNewsfeedArray_stars_count() + " Stars" + "</b></font>";
                holder.feed_star_user.setText(Html.fromHtml(stars_grater));
                holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.dark_gray));//akshit code Black is not needed
                holder.feed_star_user.setClickable(true);
            } else {
                String stars_text = "";
                for (int i = 0; i < eachNewsFeed.get(position).getStarredArrayList().size(); i++) {
                    if (i == eachNewsFeed.get(position).getStarredArrayList().size() - 1) {
                        stars_text += eachNewsFeed.get(position).getStarredArrayList().get(i).getNewsFeedArray_starredArray_user_name();
                    } else {
                        stars_text += eachNewsFeed.get(position).getStarredArrayList().get(i).getNewsFeedArray_starredArray_user_name() + ", ";
                    }
                }
                holder.feed_star_user.setTextColor(context.getResources().getColor(R.color.feed_senderuser));
                String html_string = "<font><b>" + stars_text + "</b></font>";
                holder.feed_star_user.setText(Html.fromHtml(html_string));
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

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return sectionIndex;
    }

    @Override
    public int getSectionForPosition(int position) {
        return eachNewsFeed.get(position).getNewsfeedArray_comments_count();  // get length of section index
    }

    @Override
    public View getHeaderView(final int position, View convertView, ViewGroup parent) {
        mInflater = LayoutInflater.from(context);
        convertView = mInflater.inflate(R.layout.list_item_header_feed, parent, false);
        //return convertView;


        final TextView view;
        final TextView view1;
        TextView feed_time;
        ImageView doubleArrow;
        ImageView imageview;
        view = (TextView) convertView.findViewById(R.id.senderUser);
        view1 = (TextView) convertView.findViewById(R.id.recieverUser);
        feed_time = (TextView) convertView.findViewById(R.id.feed_time);
        imageview = (ImageView) convertView.findViewById(R.id.imageView1);
        doubleArrow = (ImageView) convertView.findViewById(R.id.doubleArrow);
        imageview.setScaleType(ImageView.ScaleType.FIT_XY);

        ProfileManager prMgr = ModelManager.getInstance().getProfileManager();


        AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
        if (senderId.get(position).equalsIgnoreCase(authManager.getUserId())) {

            view.setText(senderName.get(position));
            view1.setText(receiverName.get(position));


            if (authManager.getGender() != null) {
                doubleArrow.setImageResource(R.drawable.arrow);
                if (authManager.getGender().matches("guy")) {

                    try {
                        if (!senderImages.get(position).equalsIgnoreCase("")) {
                            Picasso.with(context)
                                    .load(senderImages.get(position))
                                    .error(R.drawable.male_user).into(imageview);

                        } else {
                            imageview.setImageResource(R.drawable.male_user);
                        }
                    } catch (Exception e) {
                        imageview.setImageResource(R.drawable.male_user);

                    }
                } else {
                    try {
                        if (!senderImages.get(position).equalsIgnoreCase("")) {
                            Picasso.with(context)
                                    .load(senderImages.get(position))
                                    .error(R.drawable.female_user).into(imageview);
                        } else {
                            imageview.setImageResource(R.drawable.female_user);
                        }
                    } catch (Exception e) {
                        imageview.setImageResource(R.drawable.female_user);

                    }
                }
            } else {
                imageview.setImageResource(R.drawable.male_user);
            }

        } else {


            if (senderId.get(position).equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserId())) {  // check sender id same as user id or not


                view.setText(senderName.get(position));
                view1.setText(receiverName.get(position));
                try {
                    if (!senderImages.get(position).equalsIgnoreCase("")) {
                        Picasso.with(context)
                                .load(senderImages.get(position))
                                .error(R.drawable.male_user).into(imageview);
                    } else {
                        imageview.setImageResource(R.drawable.male_user);
                    }
                } catch (Exception e) {
                    imageview.setImageResource(R.drawable.male_user);

                }
                doubleArrow.setImageResource(R.drawable.arrow);

            } else {   // else change the direction of arrow as it is reciver


                view.setText(receiverName.get(position));
                view1.setText(senderName.get(position));
                Picasso.with(context).load(recieverImages.get(position)).placeholder(R.drawable.dcontact).into(imageview);
                try {
                    if (!recieverImages.get(position).equalsIgnoreCase("")) {
                        Picasso.with(context)
                                .load(recieverImages.get(position))
                                .error(R.drawable.male_user).into(imageview);
                    } else {
                        imageview.setImageResource(R.drawable.male_user);
                    }
                } catch (Exception e) {
                    imageview.setImageResource(R.drawable.male_user);

                }
                doubleArrow.setImageResource(R.drawable.flip_arow);


            }

        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To track through mixPanel.If User click on his own name from feed header
                Utils.trackMixpanel((Activity) context, "Activity", "FeedScreenMyProfileOpened", "LeftMenuTheFeedButtonClicked", false);
                String phNo, name;
                if (view.getText().toString().trim().equalsIgnoreCase(senderName.get(position).toString())) {
                    phNo = senderPhNo.get(position);
                } else {
                    phNo = recieverPhNo.get(position);
                }
                Intent viewProfile = null;

                viewProfile = new Intent(context, UserProfileView.class);
                viewProfile.putExtra("FromOwnProfile", true);
                viewProfile.putExtra("phNumber", phNo);
                viewProfile.putExtra("name", view.getText().toString());

                context.startActivity(viewProfile);
            }
        });
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //To track through mixPanel.If User click on sender's name from feed header
                Utils.trackMixpanel((Activity) context, "Activity", "FeedScreenOtherUserProfileOpened", "LeftMenuTheFeedButtonClicked", false);
                String phNo;
                if (view1.getText().toString().trim().equalsIgnoreCase(senderName.get(position))) {
                    phNo = senderPhNo.get(position);
                } else {
                    phNo = recieverPhNo.get(position);
                }
                Intent viewProfile = null;

                viewProfile = new Intent(context, JumpOtherProfileView.class);
                viewProfile.putExtra("FromOwnProfile", true);
                viewProfile.putExtra("phNumber", phNo);
                viewProfile.putExtra("name", view1.getText().toString());
//                    }

                viewProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewProfile);
            }
        });
        if (timeOfFeed.get(position) != null) {
            feed_time.setText(Utils.getLocalDate(timeOfFeed.get(position)));
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return position;
    }


    static class RecordHolder {
        ImageView feed_image, feed_remove_post, feed_report_post;
        ImageView feed_menu;
        //        ImageView  clicks_heart_top,clicks_heart_bottom;
        RelativeLayout layout, cards_relative;
        LinearLayout card_layout, layout_clickin;
        TextView clickedIn, custom_message, feed_star_user, clickedInMessage, card_count1, card_count2, card_status, card_title, card_msg, clickedInMessage_plain, clickedInMessageLong;
        Button feed_audio_button, feed_video_button;
        LinearLayout feed_comments_layout1, feed_comments_layout;
        LinearLayout feed_comments_layout2, feed_comments_layout3, feed_comments_layout4;

        TextView comment2, comment3, comment4, no_comments;
        ImageView feed_star_image_button, feed_comment_image_button, clickInWhiteImage;
        LinearLayout audio_layout;
        RelativeLayout video_layout;
        ImageView video_thumb, card_status_img;
        ProgressBar mLoadImage; // progress Bar To load Image
    }


}
