package com.sourcefuse.clickinandroid.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.FeedsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2.Section;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by gagansethi on 3/7/14.
 */
public class FeedView extends ClickInBaseView implements View.OnClickListener {
    public static StickyListHeadersListView list;
    ArrayList<NewsFeedBean> newsFeedBeanArrayList;
    ArrayList<String> senderName = new ArrayList<String>();
    ArrayList<String> senderId = new ArrayList<String>();
    ArrayList<String> receiverName = new ArrayList<String>();
    ArrayList<String> receiverId = new ArrayList<String>();
    ArrayList<Integer> mHeaderPositions = new ArrayList<Integer>();
    ArrayList<String> recieverImages = new ArrayList<String>();
    ArrayList<String> senderImages = new ArrayList<String>();
    ArrayList<String> senderPhNo = new ArrayList<String>();
    ArrayList<String> recieverPhNo = new ArrayList<String>();
    ArrayList<String> timeOfFeed = new ArrayList<String>();
    int headerPosition = 0;
    SimpleSectionedListAdapter2 simpleSectionedGridAdapter2;
    TextView load_earlier;
    private ArrayList<Section> sections = new ArrayList<Section>();
    private NewsFeedManager newsFeedManager;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//code- to handle uncaught exception
        if (Utils.mStartExceptionTrack)
            Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));


        setContentView(R.layout.view_feedview_list);
        addMenu(true);
        list = (StickyListHeadersListView) findViewById(R.id.list1);
        list.setHorizontalScrollBarEnabled(false);
        list.setVerticalScrollBarEnabled(false);
        list.setVerticalFadingEdgeEnabled(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View header = inflater.inflate(R.layout.list_header_chat, null);
        load_earlier = (TextView) header.findViewById(R.id.load_earlier);
        load_earlier.setText("LOAD EARLIER FEEDS");
        //load_earlier.setVisibility(View.VISIBLE);
        load_earlier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ModelManager.getInstance().getNewsFeedManager().userFeed.size() > 0) {
                    Log.e("ModelManager.getInstance().getNewsFeedManager().userFeed size", "" + ModelManager.getInstance().getNewsFeedManager().userFeed.size());
                    Utils.launchBarDialog(FeedView.this);
                    newsFeedManager.fetchNewsFeed(ModelManager.getInstance().getNewsFeedManager().userFeed.get(ModelManager.getInstance().getNewsFeedManager().userFeed.size() - 1).getNewsfeedArray_id()
                            , authManager.getPhoneNo(), authManager.getUsrToken());
                }

            }
        });
        list.addFooterView(header);


        newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();


        Utils.launchBarDialog(FeedView.this);
        ModelManager.getInstance().getProfileManager().getFollwer("", authManager.getPhoneNo(), authManager.getUsrToken()); // get following list as we need it.
        newsFeedManager.fetchNewsFeed("", authManager.getPhoneNo(), authManager.getUsrToken());

    }

    @Override
    public void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.top_out);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void initData() {
        senderName.clear();
        senderId.clear();
        receiverId.clear();
        receiverName.clear();
        senderImages.clear();
        recieverImages.clear();
        timeOfFeed.clear();
        mHeaderPositions.clear();
        senderPhNo.clear();
        recieverPhNo.clear();
        headerPosition = 0;

        for (NewsFeedBean eachNewsFeed : newsFeedBeanArrayList) {


            senderName.add(eachNewsFeed.getNewsFeedArray_senderDetail_name());
            senderId.add(eachNewsFeed.getNewsFeedArray_senderDetail_id());
            receiverName.add(eachNewsFeed.getNewsFeedArray_receiverDetail_name());
            receiverId.add(eachNewsFeed.getNewsFeedArray_receiverDetail_id());
            senderImages.add(eachNewsFeed.getNewsFeedArray_senderDetail_user_pic());
            recieverImages.add(eachNewsFeed.getNewsFeedArray_receiverDetail_user_pic());
            senderPhNo.add(eachNewsFeed.getNewsFeedArray_senderDetail_phno());
            recieverPhNo.add(eachNewsFeed.getNewsFeedArray_receiverDetail_phno());
            timeOfFeed.add(eachNewsFeed.getNewsfeedArray_created());
            mHeaderPositions.add(headerPosition);
            headerPosition = headerPosition + 1;

        }

    }


    private void initControls() {

        sections.clear();
        FeedsAdapter adapter = new FeedsAdapter(FeedView.this, R.layout.feed_list_item, newsFeedManager.userFeed, mHeaderPositions,
                senderName, receiverName, senderImages, recieverImages, timeOfFeed, senderId, receiverId, senderPhNo, recieverPhNo);

        list.setAdapter(adapter);

        Utils.dismissBarDialog();//

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void onEventMainThread(String message) {

        super.onEventMainThread(message);

        authManager = ModelManager.getInstance().getAuthorizationManager();

        if (message.equalsIgnoreCase("NewsFeed True")) {
            newsFeedBeanArrayList = newsFeedManager.userFeed;
            if (newsFeedManager.mFlag) {
                load_earlier.setVisibility(View.VISIBLE);
            } else {
                load_earlier.setVisibility(View.GONE);
            }
            initData();
            initControls();
            //Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("NewsFeed False")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            //newsFeedManager.userFeed.clear();
            ((RelativeLayout) findViewById(R.id.no_feed_image)).setVisibility(View.VISIBLE);
            // no_feed_image.setVisibility(View.VISIBLE);

        } else if (message.equalsIgnoreCase("NewsFeed Network Error")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(FeedView.this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("NewsFeedDelete False")) {
        } else if (message.equalsIgnoreCase("NewsFeedDelete True")) {
            newsFeedManager.fetchNewsFeed("", ModelManager.getInstance().getAuthorizationManager().getPhoneNo(), ModelManager.getInstance().getAuthorizationManager().getUsrToken());
        } else if (message.equalsIgnoreCase("NewsFeedDelete Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(FeedView.this, AlertMessage.connectionError);
        }

    }
}
