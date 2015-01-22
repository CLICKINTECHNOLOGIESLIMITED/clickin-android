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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.FollowerFollowingBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.FollowingListView;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.event.EventBus;

public class FollowingAdapter extends ArrayAdapter<FollowerFollowingBean> {
    private static final String TAG = FollowingAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    RecordHolder rholder;
    List<FollowerFollowingBean> item;
    private AuthManager authManager;
    private RelationManager relationManager;


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


            holder.usr_name = (TextView) row.findViewById(R.id.tv_clickers_name);
            holder.hfollowersRequest = (TextView) row.findViewById(R.id.tv_heading_rfollowers);
            holder.hfollowers = (TextView) row.findViewById(R.id.tv_heading_followers);
            holder.usrimg = (ImageView) row.findViewById(R.id.iv_usr);
            holder.usrimg.setScaleType(ScaleType.FIT_XY);
            holder.reqbtn = (TextView) row.findViewById(R.id.btn_actions);


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        rholder = (RecordHolder) row.getTag();

        if (FollowingListView.fromOwnProfile) {
            holder.reqbtn.setVisibility(View.VISIBLE);
        } else {
            holder.reqbtn.setVisibility(View.GONE);
        }
/* condition for following */

        if (!Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getAccepted().equalsIgnoreCase("true") && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {
            rholder.reqbtn.setBackgroundResource(R.drawable.following_btn);
        } else if (Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {

/* condition for requested button*/
            rholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
        } else if (Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getIsFollowing().equalsIgnoreCase("true")) {

/* confition for follow button when user unfollow*/
            rholder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);
        }


        rholder.usr_name.setText(item.get(position).getFolloweeName());
        if (!item.get(position).getFolloweePic().equalsIgnoreCase("")) {
            try {
                PicassoManager.getPicasso().load(item.get(position).getFolloweePic())
                        .into(rholder.usrimg);
            } catch (Exception e) {
                //holder.usrimg.setImageResource(R.drawable.male_user);
            }
        } else {
            holder.usrimg.setImageResource(R.drawable.male_user);
        }

        rholder.reqbtn.setTag(position);
        rholder.reqbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int position = (Integer) v.getTag();
                RelativeLayout layout = (RelativeLayout) v.getParent();
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();

                if (item.get(position).getAccepted().equalsIgnoreCase("true") && item.get(position).getIsFollowing().equalsIgnoreCase("false")) {
                    item.get(position).setIsFollowing("true");
                    rholder.reqbtn.setBackgroundResource(R.drawable.c_owner_follow_btn);
                    unfallowingDialog(position, rholder.reqbtn, layout);
/* condition to unfollow dialog*/


                } else if (Utils.isEmptyString(item.get(position).getAccepted()) && item.get(position).getIsFollowing().equalsIgnoreCase("true")) {

/*  conditoin to change follow into requested */
                    TextView button = (TextView) layout.getChildAt(2);
                    button.setBackgroundResource(R.drawable.requested_btn);
                    rholder.reqbtn.setBackgroundResource(R.drawable.requested_btn);
                    relationManager.followUser(item.get(position).getPhoneNo(), authManager.getPhoneNo(), authManager.getUsrToken());
                    item.get(position).setIsFollowing("false");
                    //FollowerList.adapter.notifyDataSetChanged();
                    FollowingListView.mchangeinList = true;
                }

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

            }
        });


        return row;
    }

    // Akshit Code Starts to show pop-up for unfollowing friend
    public void unfallowingDialog(final int position1, final View viewBtn, View view) {

        final Dialog dialog = new Dialog(((Activity) context));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_follower_adapter);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);

        msgI.setText(AlertMessage.unFollowselecteduser);

        //akshit to set bold for buttom
        Typeface typefaceBold = Typeface.createFromAsset(getContext().getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);

        RelativeLayout relativeLayout = (RelativeLayout) view;
        TextView button = (TextView) relativeLayout.getChildAt(2);


        Button skip = (Button) dialog.findViewById(R.id.coolio);
        skip.setTag(button);
        skip.setTypeface(typefaceBold);//akshit to set bold for buttom


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView button1 = (TextView) view.getTag();
                button1.setBackgroundResource(R.drawable.c_owner_follow_btn);

                relationManager.unFollowUser(item.get(position1).getrFollowerId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
                item.get(position1).setIsFollowing("true");
                item.get(position1).setAccepted("null");
                dialog.dismiss();
                notifyDataSetChanged();
                EventBus.getDefault().post("update");
                FollowingListView.mchangeinList = true;
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
        dismiss.setTypeface(typefaceBold);//akshit to set bold for buttom
    }

    static class RecordHolder {
        TextView usr_name, hfollowersRequest, hfollowers;
        ImageView usrimg;
        TextView reqbtn;

    }
// Ends

}
