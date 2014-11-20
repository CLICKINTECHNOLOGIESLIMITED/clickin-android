package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FollowingListView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowingAdapter extends ArrayAdapter<FollowerFollowingBean> {
      private static final String TAG = FollowingAdapter.class.getSimpleName();
      Context context;
      int layoutResourceId;
      private AuthManager authManager;
      private RelationManager relationManager;
      RecordHolder rholder;
      List<FollowerFollowingBean> item;
      private Typeface typeface;

      public FollowingAdapter(Context context, int layoutResourceId,
                              List<FollowerFollowingBean> item1) {
            super(context, layoutResourceId, item1);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.item = item1;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            RecordHolder holder = null;
            if (row == null) {
                  LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                  row = inflater.inflate(layoutResourceId, parent, false);
                  holder = new RecordHolder();

                  typeface = Typeface.createFromAsset(context.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);

                  holder.usr_name = (TextView) row.findViewById(R.id.tv_clickers_name);
                  holder.hfollowersRequest = (TextView) row.findViewById(R.id.tv_heading_rfollowers);
                  holder.hfollowers = (TextView) row.findViewById(R.id.tv_heading_followers);
                  holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
                  holder.usrimg.setScaleType(ScaleType.FIT_XY);
                  holder.reqbtn = (TextView) row.findViewById(R.id.btn_actions);
                  /*holder.hfollowersRequest.setTypeface(typeface);
                  holder.hfollowers.setTypeface(typeface);*/

                  row.setTag(holder);
            } else {
                  holder = (RecordHolder) row.getTag();
            }

            rholder = (RecordHolder) row.getTag();

            if (FollowingListView.fromOwnProfile == true) {
                  holder.reqbtn.setVisibility(View.VISIBLE);
            } else {
                  holder.reqbtn.setVisibility(View.GONE);
            }

            if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("true")) {
                  rholder.reqbtn.setBackgroundResource(R.drawable.following_btn);
            } else {
                  rholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
            }
            rholder.usr_name.setText(item.get(position).getFolloweeName());
            if (!item.get(position).getFolloweePic().equalsIgnoreCase("")) {
                  try {
                        Picasso.with(context).load(item.get(position).getFolloweePic())
                                .skipMemoryCache()
                                .error(R.drawable.male_user)
                                .into(rholder.usrimg);
                  } catch (Exception e) {
                        holder.usrimg.setImageResource(R.drawable.male_user);
                  }
            } else {
                  holder.usrimg.setImageResource(R.drawable.male_user);
            }

            rholder.reqbtn.setTag(position);
            rholder.reqbtn.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {

                       int position = (Integer) v.getTag();

                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        relationManager = ModelManager.getInstance().getRelationManager();

                        if (item.get(position).getAccepted().matches("true") && item.get(position).getIsFollowing().matches("false")) {
                              unfallowingDialog(position);

                        } else if (item.get(position).getIsFollowing().matches("true")) {
                              rholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                              relationManager.followUser(item.get(position).getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                              item.get(position).setIsFollowing("false");
                              //FollowerList.adapter.notifyDataSetChanged();
                              Log.e(TAG, "Click - holder.follow=");
                        }

                  }
            });


            return row;
      }

      static class RecordHolder {
            TextView usr_name, hfollowersRequest, hfollowers;
            ImageView usrimg;
            TextView reqbtn;

      }

      // Akshit Code Starts to show pop-up for unfollowing friend
      public void unfallowingDialog(int position) {

            final Dialog dialog = new Dialog(((Activity) context));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            // dialog.setContentView(R.layout.alert_follower_adapter);
            dialog.setCancelable(false);
            TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);

            msgI.setText(AlertMessage.unFollowselecteduser);

            Button skip = (Button) dialog.findViewById(R.id.coolio);
            skip.setTag(position);
            skip.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                        int position = (Integer) view.getTag();
                        rholder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);
                        relationManager.unFollowUser(item.get(position).getrFollowerId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
                        item.get(position).setIsFollowing("true");
                        //FollowerList.adapter.notifyDataSetChanged();
                        Log.e(TAG, "Click - holder.Unfollow=");
                        dialog.dismiss();
                  }
            });

            Button dismiss = (Button) dialog.findViewById(R.id.coolio1);
            dismiss.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View arg0) {
                        dialog.dismiss();

                  }
            });
            dialog.show();
      }
// Ends

}
