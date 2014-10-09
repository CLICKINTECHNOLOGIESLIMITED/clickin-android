package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.view.adapter.FeedsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2.Section;
import com.sourcefuse.clickinapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gagansethi on 3/7/14.
 */
public class FeedView extends ClickInBaseView {
    private ListView list;
    private ArrayList<Section> sections = new ArrayList<Section>();
    public static FeedsAdapter adapter;
    private NewsFeedManager newsFeedManager;
    ArrayList<NewsFeedBean> newsFeedBeanArrayList;
    ArrayList<String> senderName = new ArrayList<String>();
    ArrayList<String> senderId = new ArrayList<String>();
    ArrayList<String>  receiverName = new ArrayList<String>();
    ArrayList<String>  receiverId = new ArrayList<String>();
    ArrayList<Integer> mHeaderPositions = new ArrayList<Integer>();
    ArrayList<String> recieverImages = new ArrayList<String>();
    ArrayList<String> senderImages = new ArrayList<String>();
    ArrayList<String> timeOfFeed = new ArrayList<String>();
    int headerPosition=0;

    ImageView menu,notificationIcon;
    RelativeLayout no_feed_image;
    private LinearLayout llAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.view_feedview_list);
        addMenu(false);

        menu = (ImageView) findViewById(R.id.iv_menu);
        notificationIcon = (ImageView) findViewById(R.id.iv_notification);
        llAttachment = (LinearLayout) findViewById(R.id.ll_attachment);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               slidemenu.showMenu(true);
            }
        });
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               slidemenu.showSecondaryMenu(true);
            }
        });
        newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
        newsFeedBeanArrayList = newsFeedManager.userFeed;
        if(newsFeedBeanArrayList.size()==0)
        {
            no_feed_image = (RelativeLayout)findViewById(R.id.no_feed_image);
            no_feed_image.setVisibility(View.VISIBLE);
        }
        else {
            initData();
            initControls();
        }
    }
    private void hideAttachView() {
        if (llAttachment.getVisibility() == View.VISIBLE) {
            Animation slideLeft = AnimationUtils.loadAnimation(FeedView.this, R.anim.slide_right_to_left);
            llAttachment.startAnimation(slideLeft);
            llAttachment.setVisibility(View.GONE);
            llAttachment.setVisibility(View.GONE);
//            showAttachmentView = true;
        }
    }
    private void initData() {
        Log.e("FeedSize", String.valueOf(newsFeedBeanArrayList.size()));
        for(NewsFeedBean eachNewsFeed : newsFeedBeanArrayList){
            senderName.add(eachNewsFeed.getNewsFeedArray_senderDetail_name());
            senderId.add(eachNewsFeed.getNewsFeedArray_senderDetail_id());
            receiverName.add(eachNewsFeed.getNewsFeedArray_receiverDetail_name());
            receiverId.add(eachNewsFeed.getNewsFeedArray_receiverDetail_id());
            senderImages.add(eachNewsFeed.getNewsFeedArray_senderDetail_user_pic());
            recieverImages.add(eachNewsFeed.getNewsFeedArray_receiverDetail_user_pic());
            Log.e("created time", String.valueOf(eachNewsFeed.getNewsfeedArray_created()));
            timeOfFeed.add(getFeedTime(eachNewsFeed.getNewsfeedArray_created()));
            mHeaderPositions.add(headerPosition);
            headerPosition = headerPosition+1;
            Log.e("News Feed Time ",eachNewsFeed.getNewsfeedArray_created());
        }

    }

    private String getFeedTime(String newsfeedArray_created) {




        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        try {

            Date date = formatter.parse(newsfeedArray_created);
            return (date.getHours()+":"+date.getMinutes());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void initControls() {
        list = null;
        list = (ListView)findViewById(R.id.list1);
        list.setHorizontalScrollBarEnabled(false);
        list.setVerticalScrollBarEnabled(false);
        list.setVerticalFadingEdgeEnabled(false);
        list.setAdapter(null);
        adapter = new FeedsAdapter(FeedView.this, R.layout.feed_list_item, newsFeedManager.userFeed);
        for (int i = 0; i < senderName.size(); i++) {
            sections.add(new Section(mHeaderPositions.get(i), senderName.get(i), receiverName.get(i), senderImages.get(i),recieverImages.get(i),timeOfFeed.get(i),senderId.get(i),receiverId.get(i)));
        }
        SimpleSectionedListAdapter2 simpleSectionedGridAdapter2 = new SimpleSectionedListAdapter2(this, adapter,
                R.layout.list_item_header_feed, R.id.senderUser, R.id.imageView1,R.id.recieverUser,R.id.feed_time);
        simpleSectionedGridAdapter2.setSections(sections.toArray(new Section[0]));
        list.setAdapter(simpleSectionedGridAdapter2);

    }



    private Integer[] mImageIds = {

    };




}
