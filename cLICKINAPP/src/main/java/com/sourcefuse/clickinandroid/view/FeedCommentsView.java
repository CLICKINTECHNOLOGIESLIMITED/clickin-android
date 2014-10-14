package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.bean.FeedStarsBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.FeedsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.FeedsCommentsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.FeedsStarsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2.Section;
import com.sourcefuse.clickinapp.R;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

/**
 * Created by charunigam on 10/10/14.
 */
public class FeedCommentsView extends Activity {
    private ListView list;
    private ArrayList<Section> sections = new ArrayList<Section>();

    private NewsFeedManager newsFeedManager;
    private AuthManager authMgr;

    ImageView menu,send_btn;
    String news_feedId;
    boolean send = false;
    FeedsCommentsAdapter adapter;
    ArrayList<FeedStarsBean> feedList;
    EditText comment;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.view_feeds_comments);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
           news_feedId = bundle.getString("news_feed_id");
        }
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
        authMgr = ModelManager.getInstance().getAuthorizationManager();
        feedList =  newsFeedManager.feedStarsList;

        menu = (ImageView) findViewById(R.id.iv_menu);
        list = (ListView) findViewById(R.id.stars_list);
        send_btn = (ImageView)findViewById(R.id.send_comment);
        comment = (EditText)findViewById(R.id.comment_edit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
//                if(send)
//                    newsFeedManager.fetchNewsFeed("",ModelManager.getInstance().getAuthorizationManager().getPhoneNo(), ModelManager.getInstance().getAuthorizationManager().getUsrToken());
            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send = true;

                FeedStarsBean feedStarsBean = new FeedStarsBean();
                feedStarsBean.setUserName(authMgr.getUserName());
                feedStarsBean.setUserId(authMgr.getUserId());
                feedStarsBean.setcreated_sec(String.valueOf(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis()));
                feedStarsBean.setUserPic(authMgr.getUserPic());
                feedStarsBean.setComment(comment.getText().toString());
                feedList.add(feedStarsBean);
                adapter.notifyDataSetChanged();

                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);

                newsFeedManager.saveStarComment(authMgr.getPhoneNo(),authMgr.getUsrToken(),news_feedId,comment.getText().toString(),"comment");
                comment.setText("");
            }
        });


        Utils.launchBarDialog(FeedCommentsView.this);

            newsFeedManager.fetchCommentStars(authMgr.getPhoneNo(), authMgr.getUsrToken(), "", news_feedId, "comment");

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
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    public void onEventMainThread(String message) {
//        android.util.Log.d("Clickin", "onEventMainThread->" + message);

        if (message.equalsIgnoreCase("FetchCommentStatus True")) {
            adapter = new FeedsCommentsAdapter(this, R.layout.view_feeds_comments_row, feedList);
            list.setAdapter(adapter);
            Utils.dismissBarDialog();
//            android.util.Log.d("1", "message->" + message);
        } else if (message.equalsIgnoreCase("FetchCommentStatus False")) {
            Utils.dismissBarDialog();
//            android.util.Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("FetchCommentStatus Networkchat Error")) {
            Utils.showAlert(FeedCommentsView.this, AlertMessage.connectionError);
//            android.util.Log.d("3", "message->" + message);

        }
    }
}
