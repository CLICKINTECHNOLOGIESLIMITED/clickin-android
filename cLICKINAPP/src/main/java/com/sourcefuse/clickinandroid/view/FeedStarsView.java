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
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.NewsFeedManager;
import com.sourcefuse.clickinandroid.model.bean.NewsFeedBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.FeedsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.FeedsStarsAdapter;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2;
import com.sourcefuse.clickinandroid.view.adapter.SimpleSectionedListAdapter2.Section;
import com.sourcefuse.clickinapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by charunigam on 10/10/14.
 */
public class FeedStarsView extends Activity {
    private ListView list;
    private ArrayList<Section> sections = new ArrayList<Section>();
    public static FeedsAdapter adapter;
    private NewsFeedManager newsFeedManager;
    private AuthManager authMgr;

    ImageView menu;
    String news_feedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.view_feeds_stars);
        this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
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
//        if(ModelManager.getInstance().getProfileManager().following.size()==0)
//        {
//            ModelManager.getInstance().getProfileManager().getFollwer("", authMgr.getPhoneNo(), authMgr.getUsrToken());
//        }
//        else {
            newsFeedManager.fetchCommentStars(authMgr.getPhoneNo(), authMgr.getUsrToken(), "", news_feedId, "star");
//        }
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
            FeedsStarsAdapter adapter = new FeedsStarsAdapter(this, R.layout.view_feeds_stars_row, newsFeedManager.feedStarsList);
            list.setAdapter(adapter);
            Utils.dismissBarDialog();
//            android.util.Log.d("1", "message->" + message);
        } else if (message.equalsIgnoreCase("FetchCommentStatus False")) {
            Utils.dismissBarDialog();
//            android.util.Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("FetchCommentStatus Network Error")) {
            Utils.showAlert(FeedStarsView.this, AlertMessage.connectionError);
//            android.util.Log.d("3", "message->" + message);
        } else if (message.equalsIgnoreCase("GetFollower True")){
            newsFeedManager.fetchCommentStars(authMgr.getPhoneNo(), authMgr.getUsrToken(), "", news_feedId, "star");
        } else if (message.equalsIgnoreCase("GetFollower False")) {
            Utils.dismissBarDialog();
//            android.util.Log.d("2", "message->" + message);
        } else if (message.equalsIgnoreCase("GetFollower Network Error")) {
            Utils.showAlert(FeedStarsView.this, AlertMessage.connectionError);
//            android.util.Log.d("3", "message->" + message);
        }
    }
}
