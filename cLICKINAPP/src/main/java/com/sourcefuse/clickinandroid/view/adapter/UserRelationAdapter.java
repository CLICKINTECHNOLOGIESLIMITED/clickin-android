package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserRelationAdapter extends ArrayAdapter<GetrelationshipsBean> {
      Context context;
      int layoutResourceId;
      private AuthManager authManager;
      private ProfileManager profileManager;
      private RelationManager relationManager;
      private Typeface typeface;
      private boolean showpending = false;
      /*RecordHolder rholder;*/
      GetrelationshipsBean item;

      public UserRelationAdapter(Context context, int layoutResourceId,
                                 List<GetrelationshipsBean> item) {
            super(context, layoutResourceId, item);
            this.layoutResourceId = layoutResourceId;
            this.context = context;

      }

      Button privacy;

      @Override
      public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            item = getItem(position);
            relationManager = ModelManager.getInstance().getRelationManager();
            /*RecordHolder holder = null;*/
            /*if (row == null) {*/
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
                  /*holder = new RecordHolder();*/
            TextView usr_name = (TextView) row.findViewById(R.id.tv_usr_name);
            TextView pending = (TextView) row.findViewById(R.id.tv_pending);
            ImageView usrimg = (ImageView) row.findViewById(R.id.iv_usr_pic);
            privacy = (Button) row.findViewById(R.id.btn_privacy);
            View whiteview = (View) row.findViewById(R.id.v_whiteview);
            View devider = (View) row.findViewById(R.id.v_devider);
            ImageView btm_divider=(ImageView) row.findViewById(R.id.btm_divider);


            Button delete = (Button) row.findViewById(R.id.btn_delete_item);
            usrimg.setScaleType(ScaleType.FIT_XY);

            typeface = Typeface.createFromAsset(context.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
            usr_name.setTypeface(typeface, typeface.BOLD);

                  /*row.setTag(holder);
            } else {
                  holder = (RecordHolder) row.getTag();
            }*/

            /*rholder = (RecordHolder) row.getTag();
            rholder.usr_name.setText(item.getPartnerName());*/

            if (item.getStatusAccepted().matches("true") && (item.getmStatuspublic().matches("false") || Utils.isEmptyString(item.getmStatuspublic()))) {
                  showpending = false;
                  privacy.setBackgroundResource(R.drawable.owner_profile_eye_cross_icon);
            } else if (item.getStatusAccepted().matches("true") && item.getmStatuspublic().matches("true")) {
                  showpending = false;
                  privacy.setBackgroundResource(R.drawable.owner_profile_eye_icon);
            } else if (Utils.isEmptyString(item.getStatusAccepted()) && item.getRequestInitiator().matches("true")) {

                  android.util.Log.e("getRequestInitiator", "getRequestInitiator");
                  //sent request ClickIcon
                  showpending = true;
                  privacy.setBackgroundResource(R.drawable.pending_status);


            } else if (Utils.isEmptyString(item.getStatusAccepted())) {
                  showpending = false;
                  //Coming request
                  privacy.setBackgroundResource(R.drawable.requested_statuts);
            }




            if (position == relationManager.requestedList.size() - 1) {
                  devider.setVisibility(View.VISIBLE);
                  whiteview.setBackgroundResource(R.drawable.owner_list_roundedview);
                  whiteview.setVisibility(View.VISIBLE);
                  btm_divider.setVisibility(View.GONE);
            } else if (position == (relationManager.acceptedList.size() + relationManager.requestedList.size()) - 1) {
                  devider.setVisibility(View.VISIBLE);
                  whiteview.setBackgroundResource(R.drawable.owner_list_roundedview);
                  whiteview.setVisibility(View.VISIBLE);
                  btm_divider.setVisibility(View.GONE);
            } else {
                  devider.setVisibility(View.GONE);
                  whiteview.setVisibility(View.GONE);
            }


            if(showpending)
            {
                  pending.setVisibility(View.VISIBLE);
                  pending.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            }



            showpending = false;


            if (!item.getPartnerPic().equalsIgnoreCase("")) {
                  try {
                        Picasso.with(context).load(item.getPartnerPic())
                                .skipMemoryCache()
                                .error(R.drawable.male_user)
                                .into(usrimg);
                  } catch (Exception e) {
                        usrimg.setImageResource(R.drawable.male_user);
                  }
            } else {
                  usrimg.setImageResource(R.drawable.male_user);
            }
            privacy.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {
                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        relationManager = ModelManager.getInstance().getRelationManager();
                        Log.e("1", "1" + item.getmStatuspublic());

                        if (item.getStatusAccepted().matches("true") && item.getmStatuspublic().matches("true")) {
                              Log.e("1", "2");
                              relationDialog(AlertMessage.PUBLICMSG + item.getPartnerName() + " private?");//request normal dialog to custom dialog


                        } else if (item.getStatusAccepted().matches("true") && (item.getmStatuspublic().matches("false") || Utils.isEmptyString(item.getmStatuspublic()))) {
                              Log.e("1", "3");
                              relationDialogprivate(AlertMessage.PRIVATE + item.getPartnerName() + " public?");//replace Normal Dialog to custom dialog


                        } else if (Utils.isEmptyString(item.getStatusAccepted()) && item.getRequestInitiator().matches("true")) {
                              Log.e("1", "4");
                        } else if (Utils.isEmptyString(item.getStatusAccepted())) {
                              Log.e("1", "5");
                              Utils.launchBarDialog((Activity) context);
                              relationManager.updateStatus(item.getRelationshipId(), authManager.getPhoneNo(), authManager.getUsrToken(), "true");
                              item.setStatusAccepted("true");
                        }
                  }
            });

            usrimg.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {
                        if (item.getStatusAccepted() == "true") {
                              Intent intent = new Intent(context, JumpOtherProfileView.class);
                              //    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              intent.putExtra("FromOwnProfile", true);
                              intent.putExtra("phNumber", item.getPhoneNo());
                              ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                              context.startActivity(intent);
                              Log.e("", "holder.usrimg");
                        }
                  }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {

                        authManager = ModelManager.getInstance().getAuthorizationManager();
                        relationManager = ModelManager.getInstance().getRelationManager();
                        Constants.itemPosition = position;
                        Utils.launchBarDialog((Activity) context);
                        relationManager.deleteRelationship(item.getRelationshipId(), authManager.getPhoneNo(), authManager.getUsrToken());

                  }
            });

            return row;
      }

      /*static class RecordHolder {
            TextView usr_name, pending;
            ImageView usrimg;
            Button delete;
            Button privacy;
            View devider;
            View whiteview;

      }*/

      // Akshit Code Starts to show pop-up to make relation ship private
      public void relationDialog(String str) {

            final Dialog dialog = new Dialog(((Activity) context));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            //   dialog.setContentView(R.layout.alert_relationship);
            dialog.setCancelable(false);
            TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);

            msgI.setText(str);

            Button skip = (Button) dialog.findViewById(R.id.coolio);
            skip.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                        item.setmStatuspublic("false");
                        relationManager.changeUserVisibility(item.getRelationshipId(), "false", authManager.getPhoneNo(), authManager.getUsrToken());
                        privacy.setBackgroundResource(R.drawable.owner_profile_eye_cross_icon);

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
//akshit code to show pop-up to make relationship public
      public void relationDialogprivate(String str) {

            final Dialog dialog = new Dialog(((Activity) context));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            //  dialog.setContentView(R.layout.alert_relationship);
            dialog.setCancelable(false);
            TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);

            msgI.setText(str);

            Button skip = (Button) dialog.findViewById(R.id.coolio);
            skip.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                        item.setmStatuspublic("true");
                        relationManager.changeUserVisibility(item.getRelationshipId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
                        privacy.setBackgroundResource(R.drawable.owner_profile_eye_icon);

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
