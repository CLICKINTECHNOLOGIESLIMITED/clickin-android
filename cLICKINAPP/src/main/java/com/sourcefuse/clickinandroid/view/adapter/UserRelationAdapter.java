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
            ImageView btm_divider = (ImageView) row.findViewById(R.id.btm_divider);


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
                  privacy.setTag("public");
            } else if (item.getStatusAccepted().matches("true") && item.getmStatuspublic().matches("true")) {
                  showpending = false;
                  privacy.setBackgroundResource(R.drawable.owner_profile_eye_icon);
                  privacy.setTag("private");
            } else if (Utils.isEmptyString(item.getStatusAccepted()) && item.getRequestInitiator().matches("true")) {
                  android.util.Log.e("getRequestInitiator", "getRequestInitiator");
                  //sent request ClickIcon
                  showpending = true;
                  privacy.setBackgroundResource(R.drawable.pending_status);
                  privacy.setTag("noaction");
            } else if (Utils.isEmptyString(item.getStatusAccepted())) {
                  showpending = false;
                  //Coming request
                  privacy.setBackgroundResource(R.drawable.requested_statuts);
                  privacy.setTag("empty");
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


            if (showpending) {
                  pending.setVisibility(View.VISIBLE);
                  whiteview.setBackgroundColor(context.getResources().getColor(R.color.white));
                  pending.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            } else {
                  pending.setVisibility(View.GONE);

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

                        String viewtag = (String) v.getTag();
                        Log.e("view tag---->", "view tag---->" + viewtag);


                        if (viewtag.equalsIgnoreCase("public")) {
                              relationDialogprivate(AlertMessage.PRIVATE + item.getPartnerName() + " public?");//replace Normal Dialog to custom dialog
                        } else if (viewtag.equalsIgnoreCase("private")) {
                              relationDialog(AlertMessage.PUBLICMSG + item.getPartnerName() + " private?");//request normal dialog to custom dialog
                        } else if (viewtag.equalsIgnoreCase("empty")) {
                              Utils.launchBarDialog((Activity) context);
                              relationManager.updateStatus(item.getRelationshipId(), authManager.getPhoneNo(), authManager.getUsrToken(), "true");
                              item.setStatusAccepted("true");
                        } else if (viewtag.equalsIgnoreCase("noaction")) {
                              Log.e("no action--->", "no action--->");
                        }




                        /*if (item.getStatusAccepted().matches("true") && item.getmStatuspublic().matches("true")) {
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
                        }*/
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
            dialog.setContentView(R.layout.alert_relationship);
            dialog.setCancelable(false);
            TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);

            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNextLTPro-MediumCn_0.otf");


            msgI.setTypeface(tf);
            msgI.setText(str);


            Button skip = (Button) dialog.findViewById(R.id.coolio);
            skip.setTypeface(tf);
            Button dismiss = (Button) dialog.findViewById(R.id.coolio1);
            dismiss.setTypeface(tf);

            skip.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                        item.setmStatuspublic("false");
                        relationManager.changeUserVisibility(item.getRelationshipId(), "false", authManager.getPhoneNo(), authManager.getUsrToken());
                        privacy.setBackgroundResource(R.drawable.owner_profile_eye_cross_icon);

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
//akshit code to show pop-up to make relationship public
      public void relationDialogprivate(String str) {

            final Dialog dialog = new Dialog(((Activity) context));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.alert_relationship);
            dialog.setCancelable(false);
            TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNextLTPro-MediumCn_0.otf");

            msgI.setText(str);
            Button skip = (Button) dialog.findViewById(R.id.coolio);
            Button dismiss = (Button) dialog.findViewById(R.id.coolio1);

            msgI.setTypeface(tf);
            skip.setTypeface(tf);
            dismiss.setTypeface(tf);

            skip.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                        item.setmStatuspublic("true");
                        relationManager.changeUserVisibility(item.getRelationshipId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
                        privacy.setBackgroundResource(R.drawable.owner_profile_eye_icon);

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
