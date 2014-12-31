package com.sourcefuse.clickinandroid.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.FeedsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2.Section;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by gagansethi on 3/7/14.
 */
public class FeedView extends ClickInBaseView implements View.OnClickListener {
    public static FeedsAdapter adapter;
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
    private ListView list;
    private ArrayList<Section> sections = new ArrayList<Section>();
    private NewsFeedManager newsFeedManager;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.view_feedview_list);
        addMenu(true);


        newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
        authManager = ModelManager.getInstance().getAuthorizationManager();



        Utils.launchBarDialog(FeedView.this);
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
        android.util.Log.e("FeedSize", String.valueOf(newsFeedBeanArrayList.size()));
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
        list = null;
        list = (ListView) findViewById(R.id.list1);
        list.setHorizontalScrollBarEnabled(false);
        list.setVerticalScrollBarEnabled(false);
        list.setVerticalFadingEdgeEnabled(false);

        sections.clear();

        adapter = new FeedsAdapter(FeedView.this, R.layout.feed_list_item, newsFeedManager.userFeed);
        for (int i = 0; i < senderName.size(); i++) {
            android.util.Log.e("timeOfFeed=", timeOfFeed.get(i));
            sections.add(new Section(mHeaderPositions.get(i), senderName.get(i), receiverName.get(i), senderImages.get(i), recieverImages.get(i), timeOfFeed.get(i), senderId.get(i), receiverId.get(i), senderPhNo.get(i), recieverPhNo.get(i)));
        }
        android.util.Log.e("sections=", "" + sections.size());
        simpleSectionedGridAdapter2 = new SimpleSectionedListAdapter2(this, adapter,
                R.layout.list_item_header_feed, R.id.senderUser, R.id.imageView1, R.id.recieverUser, R.id.feed_time);
        simpleSectionedGridAdapter2.setSections(sections.toArray(new Section[0]));
        if (Constants.comments) {
            simpleSectionedGridAdapter2.notifyDataSetChanged();
            list.invalidateViews();
            ;
            Constants.comments = false;
        } else
            list.setAdapter(simpleSectionedGridAdapter2);

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
            android.util.Log.e("TAG", "Message if True" + message);
            newsFeedBeanArrayList = newsFeedManager.userFeed;
            initData();
            initControls();
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("NewsFeed False")) {
            android.util.Log.e("TAG", "Message if False" + message);
            stopSearch = true;
            Utils.dismissBarDialog();
            newsFeedManager.userFeed.clear();
            ((RelativeLayout) findViewById(R.id.no_feed_image)).setVisibility(View.VISIBLE);
            // no_feed_image.setVisibility(View.VISIBLE);

        } else if (message.equalsIgnoreCase("NewsFeed Network Error")) {
            stopSearch = true;
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(FeedView.this, AlertMessage.connectionError);
           // android.util.android.util.Log.d("3", "message->" + message);
        } else if (message.equalsIgnoreCase("NewsFeedDelete False")) {
        } else if (message.equalsIgnoreCase("NewsFeedDelete True")) {
            newsFeedManager.fetchNewsFeed("", ModelManager.getInstance().getAuthorizationManager().getPhoneNo(), ModelManager.getInstance().getAuthorizationManager().getUsrToken());
        } else if (message.equalsIgnoreCase("NewsFeedDelete Network Error")) {
            Utils.dismissBarDialog();
            Utils.fromSignalDialog(FeedView.this, AlertMessage.connectionError);
        }

    }
}
