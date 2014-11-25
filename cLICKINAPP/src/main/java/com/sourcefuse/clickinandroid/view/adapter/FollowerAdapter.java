
package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FollowerList;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.event.EventBus;

public class FollowerAdapter extends ArrayAdapter<FollowerFollowingBean> {
      private static final String TAG = FollowerAdapter.class.getSimpleName();
      Context context;
      int layoutResourceId;

      private AuthManager authManager;
      private RelationManager relationManager;
      private ProfileManager profileManager;
      private Typeface typeface;

      RecordHolder vholder;

      List<FollowerFollowingBean> item;

      public FollowerAdapter(Context context, int layoutResourceId,
                             List<FollowerFollowingBean> item1) {
            super(context, layoutResourceId, item1);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.item = item1;

      }

      @Override
      public View getView(final int position, View convertView, ViewGroup parent) {

            View row = convertView;
            RecordHolder holder = null;
            if (row == null) {
                  LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                  row = inflater.inflate(layoutResourceId, parent, false);
                  holder = new RecordHolder();
                  typeface = Typeface.createFromAsset(context.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);


                  holder.usr_name = (TextView) row.findViewById(R.id.tv_clickers_name);
                  holder.hfollowersRequest = (TextView) row.findViewById(R.id.tv_heading_rfollowers);
                  holder.hfollowers = (TextView) row.findViewById(R.id.tv_heading_followers);
                  holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
                  holder.usrimg.setScaleType(ScaleType.FIT_XY);

                  holder.divider_ = (ImageView) row.findViewById(R.id.divider_);

                  holder.reqbtn = (TextView) row.findViewById(R.id.btn_actions);
                  holder.resBtn = (Button) row.findViewById(R.id.btn_f_resect);
                  holder.accBtn = (Button) row.findViewById(R.id.btn_f_accept);
                  holder.rlResectAccept = (LinearLayout) row.findViewById(R.id.rl_resect_accept);

            /*holder.hfollowersRequest.setTypeface(typeface, typeface.BOLD);
            holder.hfollowers.setTypeface(typeface, typeface.BOLD);*/

                  row.setTag(holder);
            } else {
                  holder = (RecordHolder) row.getTag();
            }

            RecordHolder rholder = (RecordHolder) row.getTag();
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


            profileManager = ModelManager.getInstance().getProfileManager();


            if (profileManager.followRequesed.size() > 0 && position == 0) {
                  holder.hfollowersRequest.setVisibility(View.VISIBLE);
                  holder.hfollowersRequest.setText("FOLLOW REQUESTS");
            } else {
                  holder.hfollowersRequest.setVisibility(View.GONE);
            }

            if (profileManager.followRequesed.size() == position) {
                  holder.hfollowers.setVisibility(View.VISIBLE);
                  holder.hfollowers.setText("FOLLOWERS");
            } else {
                  holder.hfollowers.setVisibility(View.GONE);
            }

            if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("true") &&
                        item.get(position).getFollowingAccepted().matches("true") && item.get(position).getIsFollowing().matches("true")) {
                  if (FollowerList.fromOwnProfile == true) {
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                        rholder.reqbtn.setBackgroundResource(R.drawable.following_btn);

                /*holder.rlResectAccept.setVisibility(View.GONE);*/
                  } else {
                        // holder.rlResectAccept.setVisibility(View.GONE);
                        holder.reqbtn.setVisibility(View.GONE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                  }


            } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("true")
                                           && item.get(position).getFollowingAccepted().matches("true") && item.get(position).getIsFollowing().matches("false")) {
                  if (FollowerList.fromOwnProfile == true) {
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                        rholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);


                  } else {

                        holder.reqbtn.setVisibility(View.GONE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                  }

            } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("true") && item.get(position).getFollowingAccepted().matches("false") && item.get(position).getIsFollowing().matches("true")) {
                  if (FollowerList.fromOwnProfile == true) {
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                        rholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                  } else {
                        holder.reqbtn.setVisibility(View.GONE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                  }

            } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("false") && item.get(position).getFollowingAccepted().matches("true") && item.get(position).getIsFollowing().matches("false")) {


                  if (FollowerList.fromOwnProfile == true) {
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                        rholder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);
                  } else {
                        holder.reqbtn.setVisibility(View.GONE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                  }

            } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("true") && item.get(position).getFollowingAccepted().matches("false") && item.get(position).getIsFollowing().matches("false")) {


                  if (FollowerList.fromOwnProfile == true) {
                        holder.reqbtn.setVisibility(View.VISIBLE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                        rholder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);

                  } else {

                        holder.reqbtn.setVisibility(View.GONE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                  }

            } else if (item.get(position).getAccepted().length() == 0) {

                  if (FollowerList.fromOwnProfile == true) {
                        holder.rlResectAccept.setVisibility(View.VISIBLE);
                        holder.reqbtn.setVisibility(View.GONE);
                  } else {
                        holder.reqbtn.setVisibility(View.GONE);
                        holder.rlResectAccept.setVisibility(View.GONE);
                  }


            } else {
                  Log.e(TAG, "DATA=" + item.get(position).getAccepted() + "---" + item.get(position).getIsFollowing() + "---" + item.get(position).getFollowingAccepted());
            }


            vholder = (RecordHolder) row.getTag();

            rholder.reqbtn.setTag(position);
            rholder.reqbtn.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {

                        int position = (Integer) v.getTag();


                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        relationManager = ModelManager.getInstance().getRelationManager();
                        if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("true") && item.get(position).getFollowingAccepted().matches("true") && item.get(position).getIsFollowing().matches("true")) {
                              unfallowDialog(position);

                        } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("true") && item.get(position).getIsFollowing().matches("false") && item.get(position).getFollowingAccepted().matches("false")) {
                              vholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                              relationManager.followUser(item.get(position).getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                              item.get(position).setIsFollowing("true");
                              notifyDataSetChanged();
                              Log.e(TAG, "Click - holder.requested_grey=" + item.get(position).getIsFollowing());
                        } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().matches("false") && item.get(position).getFollowingAccepted().matches("true") && item.get(position).getIsFollowing().matches("true")) {
                              vholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                              relationManager.followUser(item.get(position).getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                              item.get(position).setIsFollowing("true");
                              notifyDataSetChanged();
                              Log.e(TAG, "Click - holder.requested_grey=" + item.get(position).getIsFollowing());
                        }

                  }
            });

            rholder.accBtn.setTag(position);
            rholder.accBtn.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        Log.e(TAG, "Click - holder.Accep1t=" + item.get(position).getAccepted());
                        item.get(position).setAccepted("true");
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        relationManager = ModelManager.getInstance().getRelationManager();
                        profileManager.followRequesed.remove(position);
                        profileManager.followRequesed.iterator();
                        notifyDataSetChanged();
                        EventBus.getDefault().post("followUpdateStatus true");
                        relationManager.followupdatestatus(item.get(position).getrFollowerId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
                        //Log.e(TAG, "Click - holder.Accept=" + item.get(position).getAccepted());
                  }
            });
            rholder.resBtn.setTag(position);
            rholder.resBtn.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        profileManager.followRequesed.remove(position);
                        profileManager.followers.remove(position);
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        notifyDataSetChanged();
                        relationManager = ModelManager.getInstance().getRelationManager();
                        relationManager.followupdatestatus(item.get(position).getrFollowerId(), "false", authManager.getPhoneNo(), authManager.getUsrToken());
                        Log.e(TAG, "Click - holder.resect");
                  }
            });


          rholder.usrimg.setTag(position);
          rholder.usrimg.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  int position = (Integer) v.getTag();
                      Intent intent = new Intent(context, JumpOtherProfileView.class);
                      intent.putExtra("FromOwnProfile", true);
                      intent.putExtra("phNumber", item.get(position).getPhoneNo());
                      ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                      context.startActivity(intent);
                      Log.e("", "holder.usrimg");

              }
          });


/*
        if(FollowerList.fromOwnProfile==true){
            holder.rlResectAccept.setVisibility(View.GONE);
        }else{
            holder.rlResectAccept.setVisibility(View.GONE);
        }*/

            return row;
      }

      static class RecordHolder {
            LinearLayout rlResectAccept;
            TextView usr_name, hfollowersRequest, hfollowers;
            ImageView usrimg, divider_;
            TextView reqbtn;
            Button resBtn;
            Button accBtn;


      }

      // Akshit Code Starts to show pop-up for unfollowing friend
      public void unfallowDialog(int position1) {

            final Dialog dialog = new Dialog(((Activity) context));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.alert_follower_adapter);
            dialog.setCancelable(false);
            TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
            msgI.setText(AlertMessage.unFollowselecteduser);

            Button skip = (Button) dialog.findViewById(R.id.coolio);
            Button dismiss = (Button) dialog.findViewById(R.id.coolio1);

            Typeface face=Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNextLTPro-MediumCn_0.otf");
            skip.setTypeface(face);
            dismiss.setTypeface(face);

            skip.setTag(position1);
            skip.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                        int position = (Integer) view.getTag();

                        vholder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);
                        relationManager.unFollowUser(item.get(position).getFollowingId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
                        item.get(position).setIsFollowing("false");
                        item.get(position).setFollowingAccepted("false");
                        //FollowerList.adapter.notifyDataSetChanged();
                        Log.e(TAG, "Click - holder.follow=" + item.get(position).getIsFollowing());
                        dialog.dismiss();
                  }
            });


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

