package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.FeedsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.FeedsStarsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2.Section;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by charunigam on 10/10/14.
 */
public class FeedStarsView extends Activity {
    public static FeedsAdapter adapter;
    ImageView menu;
    String news_feedId;
    private ListView list;
    private ArrayList<Section> sections = new ArrayList<Section>();
    private NewsFeedManager newsFeedManager;
    private AuthManager authMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(this));


        setContentView(R.layout.view_feeds_stars);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            news_feedId = bundle.getString("news_feed_id");
        }
        menu = (ImageView) findViewById(R.id.iv_menu);
        list = (ListView) findViewById(R.id.stars_list);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        newsFeedManager = ModelManager.getInstance().getNewsFeedManager();
        authMgr = ModelManager.getInstance().getAuthorizationManager();

        Utils.launchBarDialog(FeedStarsView.this);

        newsFeedManager.fetchCommentStars(authMgr.getPhoneNo(), authMgr.getUsrToken(), "", news_feedId, "star");
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

        if (message.equalsIgnoreCase("FetchCommentStatus True")) {
            FeedsStarsAdapter adapter = new FeedsStarsAdapter(this, R.layout.view_feeds_stars_row, newsFeedManager.feedStarsList);
            list.setAdapter(adapter);
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("FetchCommentStatus False")) {
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("FetchCommentStatus Network Error")) {
            Utils.fromSignalDialog(FeedStarsView.this, AlertMessage.connectionError);
        } else if (message.equalsIgnoreCase("GetFollower True")) {
            newsFeedManager.fetchCommentStars(authMgr.getPhoneNo(), authMgr.getUsrToken(), "", news_feedId, "star");
        } else if (message.equalsIgnoreCase("GetFollower False")) {
            Utils.dismissBarDialog();
        } else if (message.equalsIgnoreCase("GetFollower Network Error")) {
            Utils.fromSignalDialog(FeedStarsView.this, AlertMessage.connectionError);
        }
    }

    //akshit code.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.top_out);//akshit code for animation
    }
}
