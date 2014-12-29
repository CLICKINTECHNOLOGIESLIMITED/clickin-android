package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;

public class UserRelationAdapter extends ArrayAdapter<GetrelationshipsBean> {
    Context context;
    int layoutResourceId;
    List<GetrelationshipsBean> itemList;
    TextView privacy;
    private AuthManager authManager;
    private ProfileManager profileManager;
    /*RecordHolder rholder;*/
    private RelationManager relationManager;
    private boolean showpending = false;
    String imagespath = "/storage/emulated/0/ClickIn/Images/";

    public UserRelationAdapter(Context context, int layoutResourceId,
                               List<GetrelationshipsBean> item) {
        super(context, layoutResourceId, item);
        itemList = item;
        this.layoutResourceId = layoutResourceId;
        this.context = context;

    }
//    ((ImageView)row.findViewById(R.id.iv_accept_card)).setTag(position);
//    ((ImageView) row.findViewById(R.id.iv_accept_card)).setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            //Card ACCEPT Action
//            int position = (Integer) v.getTag();
//            sendUpdateCardValues(position, "accepted", "ACCEPTED!");
//
//
//        }
//    });

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        relationManager = ModelManager.getInstance().getRelationManager();
            /*RecordHolder holder = null;*/
           /* if (row == null) {*/
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);
                  /*holder = new RecordHolder();*/
        final TextView usr_name = (TextView) row.findViewById(R.id.tv_usr_name);
        TextView pending = (TextView) row.findViewById(R.id.tv_pending);
        final ImageView usrimg = (ImageView) row.findViewById(R.id.iv_usr_pic);

        View whiteview = (View) row.findViewById(R.id.v_whiteview);
        View devider = (View) row.findViewById(R.id.v_devider);
        ImageView btm_divider = (ImageView) row.findViewById(R.id.btm_divider);
        TextView delete = (TextView) row.findViewById(R.id.btn_delete_item);

        usrimg.setScaleType(ScaleType.FIT_XY);



            /*}*/
        privacy = (TextView) row.findViewById(R.id.btn_privacy);
        delete.setTag(position);
        usrimg.setTag(position);
        usr_name.setText(itemList.get(position).getPartnerName());


        if (itemList.get(position).getStatusAccepted().equalsIgnoreCase("true") && (itemList.get(position).getmStatuspublic().equalsIgnoreCase("false") || Utils.isEmptyString(itemList.get(position).getmStatuspublic()))) {
            showpending = false;
            privacy.setBackgroundResource(R.drawable.owner_profile_eye_cross_icon);

            privacy.setTag(position);

        } else if (itemList.get(position).getStatusAccepted().equalsIgnoreCase("true") && itemList.get(position).getmStatuspublic().equalsIgnoreCase("true")) {
            showpending = false;
            privacy.setBackgroundResource(R.drawable.owner_profile_eye_icon);
            privacy.setTag(position);
        } else if (Utils.isEmptyString(itemList.get(position).getStatusAccepted()) && itemList.get(position).getRequestInitiator().equalsIgnoreCase("true")) {
                  /*android.util.android.Utils.Log.e("getRequestInitiator", "getRequestInitiator");*/
            //sent request ClickIcon
            showpending = true;
            privacy.setBackgroundResource(R.drawable.pending_status);
            privacy.setTag(position);
        } else if (Utils.isEmptyString(itemList.get(position).getStatusAccepted())) {
            showpending = false;
            //Coming request
            privacy.setBackgroundResource(R.drawable.requested_statuts);
            privacy.setTag(position);
        }

        boolean last = false;
        if (position == relationManager.requestedList.size() - 1) {
            devider.setVisibility(View.VISIBLE);
            whiteview.setBackgroundResource(R.drawable.owner_list_roundedview);
            whiteview.setVisibility(View.VISIBLE);
            btm_divider.setVisibility(View.GONE);
            last = true;
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
            if (last) {
                pending.setVisibility(View.VISIBLE);
                whiteview.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                pending.setVisibility(View.VISIBLE);
                whiteview.setBackgroundColor(context.getResources().getColor(R.color.white));
                pending.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            }


        } else {
            pending.setVisibility(View.GONE);

        }


        showpending = false;


        if (!itemList.get(position).getPartnerPic().equalsIgnoreCase("")) {
            try {

               // File file = new File(imagespath);

              /*  File file = new File(Environment.getExternalStorageDirectory(),"ClickIn/Images/Mukesh/");
                file.mkdir();
                Picasso.with(context)
                        .load(itemList.get(position).getPartnerPic())
                        .into((Target) file);*/
/*
                File file = new File(Environment.getExternalStorageDirectory(),
                        "Android/data/com.usd.pop");
                file.mkdirs();

                Picasso.with(context)
                        .load(itemList.get(position).getPartnerPic())
                        .into((Target) file);*/


                Picasso.with(context).load(itemList.get(position).getPartnerPic())

                        .into(usrimg);
            } catch (Exception e) {
                e.printStackTrace();
               // usrimg.setImageResource(R.drawable.male_user);
            }
        } else {
            usrimg.setImageResource(R.drawable.male_user);
        }
        privacy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();


                RelativeLayout layout = (RelativeLayout) v.getParent();


                int position = (Integer) v.getTag();


                if (itemList.get(position).getStatusAccepted().equalsIgnoreCase("true") && itemList.get(position).getmStatuspublic().equalsIgnoreCase("true")) {

                    relationDialog(AlertMessage.PUBLICMSG + itemList.get(position).getPartnerName() + " private?", position, layout);//request normal dialog to custom dialog

                } else if (itemList.get(position).getStatusAccepted().equalsIgnoreCase("true") && (itemList.get(position).getmStatuspublic().equalsIgnoreCase("false") || Utils.isEmptyString(itemList.get(position).getmStatuspublic()))) {

                    relationDialogprivate(AlertMessage.PRIVATE + itemList.get(position).getPartnerName() + " public?", position, layout);//replace Normal Dialog to custom dialog
                } else if (Utils.isEmptyString(itemList.get(position).getStatusAccepted()) && itemList.get(position).getRequestInitiator().equalsIgnoreCase("true")) {
                } else if (Utils.isEmptyString(itemList.get(position).getStatusAccepted())) {
                    Utils.launchBarDialog((Activity) context);
                    relationManager.updateStatus(itemList.get(position).getRelationshipId(), authManager.getPhoneNo(), authManager.getUsrToken(), "true");
                    itemList.get(position).setStatusAccepted("true");
                }
            }
        });

        //akshit Code For clickin
        usrimg.setTag(position);
        usrimg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int pos = (Integer) v.getTag();
                if (itemList.get(pos).getStatusAccepted() == "true") {

                    relationManager = ModelManager.getInstance().getRelationManager();
                    String partnerId = relationManager.getrelationshipsData.get(pos).getPartner_id();

                    Intent intent = new Intent(context, JumpOtherProfileView.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("PartnerId", partnerId);
                    intent.putExtra("FromOwnProfile", true);
                    intent.putExtra("phNumber", itemList.get(position).getPhoneNo());
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    context.startActivity(intent);

                } else {

                }
            }
        });//ends
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                int position = (Integer) v.getTag();

                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();
                Constants.itemPosition = position;
                Utils.launchBarDialog((Activity) context);
                relationManager.deleteRelationship(itemList.get(position).getRelationshipId(), authManager.getPhoneNo(), authManager.getUsrToken());

            }
        });

        return row;
    }


    // Akshit Code Starts to show pop-up to make relation ship private
    public void relationDialog(String str, final int position1, View view) {

        final Dialog dialog = new Dialog(((Activity) context));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_relationship);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);

//        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNextLTPro-MediumCn_0.otf");


        RelativeLayout relativeLayout = (RelativeLayout) view;
        TextView button = (TextView) relativeLayout.getChildAt(1);

//        msgI.setTypeface(tf);
        msgI.setText(str);


        Button skip = (Button) dialog.findViewById(R.id.coolio);
        // skip.setTypeface(tf);
        Button dismiss = (Button) dialog.findViewById(R.id.coolio1);
        //     dismiss.setTypeface(tf);
        skip.setTag(button);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                TextView button1 = (TextView) view.getTag();
                button1.setBackgroundResource(R.drawable.owner_profile_eye_cross_icon);
                itemList.get(position1).setmStatuspublic("false");
                relationManager.changeUserVisibility(itemList.get(position1).getRelationshipId(), "false", authManager.getPhoneNo(), authManager.getUsrToken());
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
    public void relationDialogprivate(String str, final int position1, View view) {

        final Dialog dialog = new Dialog(((Activity) context));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_relationship);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
//        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNextLTPro-MediumCn_0.otf");

        msgI.setText(str);
        Button skip = (Button) dialog.findViewById(R.id.coolio);
        Button dismiss = (Button) dialog.findViewById(R.id.coolio1);

//        msgI.setTypeface(tf);
//        skip.setTypeface(tf);
//        dismiss.setTypeface(tf);


        RelativeLayout relativeLayout = (RelativeLayout) view;
        TextView button = (TextView) relativeLayout.getChildAt(1);
        skip.setTag(button);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView button1 = (TextView) view.getTag();
                button1.setBackgroundResource(R.drawable.owner_profile_eye_icon);
                itemList.get(position1).setmStatuspublic("true");
                relationManager.changeUserVisibility(itemList.get(position1).getRelationshipId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
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
