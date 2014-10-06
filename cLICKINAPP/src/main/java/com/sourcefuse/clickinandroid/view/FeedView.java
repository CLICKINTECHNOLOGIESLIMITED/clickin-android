package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

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
public class FeedView extends Activity {
    private ListView list;
    private ArrayList<Section> sections = new ArrayList<Section>();
    public static FeedsAdapter adapter;
    private NewsFeedManager newsFeedManager;
    ArrayList<NewsFeedBean> newsFeedBeanArrayList;
    ArrayList<String> senderName = new ArrayList<String>();
    ArrayList<String>  receiverName = new ArrayList<String>();
    ArrayList<Integer> mHeaderPositions = new ArrayList<Integer>();
    ArrayList<String> recieverImages = new ArrayList<String>();
    ArrayList<String> senderImages = new ArrayList<String>();
    ArrayList<String> timeOfFeed = new ArrayList<String>();
    int headerPosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_feedview_list);
        newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
        newsFeedBeanArrayList = newsFeedManager.userFeed;
        initData();
        initControls();
    }

    private void initData() {
        Log.e("FeedSize", String.valueOf(newsFeedBeanArrayList.size()));
        for(NewsFeedBean eachNewsFeed : newsFeedBeanArrayList){
            senderName.add(eachNewsFeed.getNewsFeedArray_senderDetail_name());
            receiverName.add(eachNewsFeed.getNewsFeedArray_receiverDetail_name());
            senderImages.add(eachNewsFeed.getNewsFeedArray_senderDetail_user_pic());
            recieverImages.add(eachNewsFeed.getNewsFeedArray_receiverDetail_user_pic());

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
        list = (ListView)findViewById(R.id.list1);
        list.setHorizontalScrollBarEnabled(false);
        list.setVerticalScrollBarEnabled(false);
        list.setVerticalFadingEdgeEnabled(false);
        adapter = new FeedsAdapter(FeedView.this, R.layout.feed_list_item, newsFeedManager.userFeed);
        for (int i = 0; i < senderName.size(); i++) {
            sections.add(new Section(mHeaderPositions.get(i), senderName.get(i), receiverName.get(i), senderImages.get(i),recieverImages.get(i),timeOfFeed.get(i)));
        }
        SimpleSectionedListAdapter2 simpleSectionedGridAdapter2 = new SimpleSectionedListAdapter2(this, adapter,
                R.layout.list_item_header_feed, R.id.senderUser, R.id.imageView1,R.id.recieverUser,R.id.feed_time);
        simpleSectionedGridAdapter2.setSections(sections.toArray(new Section[0]));
        list.setAdapter(simpleSectionedGridAdapter2);

    }



    private Integer[] mImageIds = {

    };




}
