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
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FollowerList;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowerAdapter extends ArrayAdapter<FollowerFollowingBean> {
    private static final String TAG = FollowerAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    RecordHolder vholder;
    List<FollowerFollowingBean> item;
    private AuthManager authManager;
    private RelationManager relationManager;
    private ProfileManager profileManager;

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


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        RecordHolder rholder = (RecordHolder) row.getTag();
        rholder.usr_name.setText(item.get(position).getFolloweeName());
        if (!item.get(position).getFolloweePic().equalsIgnoreCase("")) {
            try {
                if(PicassoManager.getPicasso() == null)
                {
                    PicassoManager.setLruCache(context);
                    PicassoManager.setPicasso(context, PicassoManager.getLruCache());
                }
               PicassoManager.getPicasso().load(item.get(position).getFolloweePic())
                        .into(rholder.usrimg);
            } catch (Exception e) {
                e.printStackTrace();
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


        if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true") && item.get(position).getFollowingAccepted().equalsIgnoreCase("true") && item.get(position).getIsFollowing().equalsIgnoreCase("true")) {

            holder.reqbtn.setVisibility(View.VISIBLE);
            holder.rlResectAccept.setVisibility(View.GONE);
            rholder.reqbtn.setBackgroundResource(R.drawable.following_btn);

        } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true")
                && item.get(position).getFollowingAccepted().equalsIgnoreCase("true") && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {
            holder.reqbtn.setVisibility(View.VISIBLE);
            holder.rlResectAccept.setVisibility(View.GONE);
            rholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);

        } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true") && item.get(position).getFollowingAccepted().equalsIgnoreCase("false") && item.get(position).getIsFollowing().equalsIgnoreCase("true")) {

            holder.reqbtn.setVisibility(View.VISIBLE);
            holder.rlResectAccept.setVisibility(View.GONE);
            rholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);


        } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("false") && item.get(position).getFollowingAccepted().equalsIgnoreCase("true") && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {

            holder.reqbtn.setVisibility(View.VISIBLE);
            holder.rlResectAccept.setVisibility(View.GONE);
            rholder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);


        } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true") && item.get(position).getFollowingAccepted().equalsIgnoreCase("false") && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {
            holder.reqbtn.setVisibility(View.VISIBLE);
            holder.rlResectAccept.setVisibility(View.GONE);
            rholder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);

        } else if (item.get(position).getAccepted().length() == 0) {

            holder.rlResectAccept.setVisibility(View.VISIBLE);
            holder.reqbtn.setVisibility(View.GONE);


        } else {
        }


        vholder = (RecordHolder) row.getTag();

        rholder.reqbtn.setTag(position);
        rholder.reqbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();


                TextView textView = (TextView) v;

/* condition for following and open unfollow dialog */
                if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true") && item.get(position).getFollowingAccepted().equalsIgnoreCase("true") && item.get(position).getIsFollowing().equalsIgnoreCase("true")) {


                    unfallowDialog(position);

                } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true")
                        && item.get(position).getFollowingAccepted().equalsIgnoreCase("true") && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {
/* condition for requested  */
                } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true") && item.get(position).getFollowingAccepted().equalsIgnoreCase("false") && item.get(position).getIsFollowing().equalsIgnoreCase("true")) {
/* condition for requested  */
                } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("false") && item.get(position).getFollowingAccepted().equalsIgnoreCase("true") && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {

/* condition for folloe change it to requested  */
                    textView.setBackgroundResource(R.drawable.requested_btn);
                    v.setBackgroundResource(R.drawable.requested_btn);
                    relationManager.followUser(item.get(position).getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                    item.get(position).setIsFollowing("true");
                    notifyDataSetChanged();
                    FollowerList.mListchangeVariable_flag = true;

                } else if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true") && item.get(position).getFollowingAccepted().equalsIgnoreCase("false") && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {
/* condition for folloe change it to requested  */
                    textView.setBackgroundResource(R.drawable.requested_btn);
                    v.setBackgroundResource(R.drawable.requested_btn);
                    relationManager.followUser(item.get(position).getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                    item.get(position).setIsFollowing("true");
                    notifyDataSetChanged();
                    FollowerList.mListchangeVariable_flag = true;
                }

            }
        });

        rholder.accBtn.setTag(position);
        rholder.accBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();

      /* condition for accept request   */
                if (profileManager.followers.size() > position) {
                    Utils.launchBarDialog((FollowerList) getContext());
                    FollowerList.mListchangeVariable_flag = true;
                    item.get(position).setAccepted("false");
                    item.get(position).setFollowingAccepted("true");
                    item.get(position).setIsFollowing("false");

                    profileManager.followers.get(position).setAccepted("false");
                    profileManager.followers.get(position).setFollowingAccepted("true");
                    profileManager.followers.get(position).setIsFollowing("false");

                    relationManager.followupdatestatus(profileManager.followers.get(position).getrFollowerId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
                    profileManager.Replacement.add(0, item.get(position));
                    profileManager.followers.remove(position);
                    profileManager.followers.add(profileManager.followers.size(), profileManager.Replacement.get(0));
                    profileManager.Replacement.clear();
                    profileManager.pfollowerList.add(profileManager.followRequesed.get(position));
                    profileManager.followRequesed.remove(position);
                }
            }
        });
        rholder.resBtn.setTag(position);
        rholder.resBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

/* condition for reject request   */
                if (profileManager.followers.size() > position && profileManager.followRequesed.size() > position) {
                    authManager = ModelManager.getInstance().getAuthorizationManager();
                    relationManager = ModelManager.getInstance().getRelationManager();
                    relationManager.followupdatestatus(profileManager.followers.get(position).getrFollowerId(), "false", authManager.getPhoneNo(), authManager.getUsrToken());
                    profileManager.followRequesed.remove(position);
                    profileManager.followers.remove(position);
               /* notifyDataSetChanged();*/
                    FollowerList.mListchangeVariable_flag = true;
                }
            }
        });


        rholder.usrimg.setTag(position);
        rholder.usrimg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


   /* condition to jump on other profile   */

                int position = (Integer) v.getTag();
                Intent intent = new Intent(context, JumpOtherProfileView.class);
                intent.putExtra("FromOwnProfile", true);
                intent.putExtra("phNumber", item.get(position).getPhoneNo());
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                context.startActivity(intent);

            }
        });


        return row;
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

        Typeface face = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNextLTPro-MediumCn_0.otf");
        Typeface typefaceBold = Typeface.createFromAsset(getContext().getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);//akshit to set bold for buttom
        skip.setTypeface(face);
        skip.setTypeface(typefaceBold);//akshit to set bold for buttom
        dismiss.setTypeface(face);
        dismiss.setTypeface(typefaceBold);//akshit to set bold for buttom

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
                dialog.dismiss();

                FollowerList.mListchangeVariable_flag = true;

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

    static class RecordHolder {
        LinearLayout rlResectAccept;
        TextView usr_name, hfollowersRequest, hfollowers;
        ImageView usrimg, divider_;
        TextView reqbtn;
        Button resBtn;
        Button accBtn;


    }
// Ends


}

