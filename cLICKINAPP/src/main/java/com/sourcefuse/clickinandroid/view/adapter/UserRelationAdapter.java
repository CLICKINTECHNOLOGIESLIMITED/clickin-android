package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.RelationManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.AppController;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FeedImageView;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinapp.R;

import java.io.File;
import java.util.List;

public class UserRelationAdapter extends ArrayAdapter<GetrelationshipsBean> {
    Context context;
    int layoutResourceId;
    List<GetrelationshipsBean> itemList;
    TextView privacy;
    String imagespath = Utils.mImagePath;
    private AuthManager authManager;
    private ProfileManager profileManager;
    /*RecordHolder rholder;*/
    private RelationManager relationManager;
    private boolean showpending = false;

    private Dialog dialog;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    /*LruCache mLruCahe;
    Picasso picasso;*/
    public UserRelationAdapter(Context context, int layoutResourceId,
                               List<GetrelationshipsBean> item) {
        super(context, layoutResourceId, item);
        itemList = item;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        Utils.trackMixpanel_superProperties((Activity) context, itemList.size(), "relationshipcount");//Track Relationship Count Through Mix panel
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;


        relationManager = ModelManager.getInstance().getRelationManager();
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);
        final TextView usr_name = (TextView) row.findViewById(R.id.tv_usr_name);
        TextView pending = (TextView) row.findViewById(R.id.tv_pending);
        final ImageView usrimg = (ImageView) row.findViewById(R.id.iv_usr_pic);
        final FeedImageView iv_usr_pic_ = (FeedImageView) row.findViewById(R.id.iv_usr_pic_);

        View whiteview = (View) row.findViewById(R.id.v_whiteview);
        View devider = (View) row.findViewById(R.id.v_devider);
        ImageView btm_divider = (ImageView) row.findViewById(R.id.btm_divider);
        TextView delete = (TextView) row.findViewById(R.id.btn_delete_item);
        usrimg.setScaleType(ScaleType.FIT_XY);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        final String finalMUserImageId = itemList.get(position).getRelationshipId();

        String mContentUri = Utils.mImagePath + finalMUserImageId + ".jpg";
        Uri mUri = Utils.getImageContentUri(context, new File(mContentUri));  //check file exist or not

        File file = new File(mContentUri);


        if (!Utils.isEmptyString("" + mUri) && file.exists()) {
            usrimg.setImageURI(mUri); // if file exists set it by uri
        } else {
            if (file.exists())
                file.delete();


            iv_usr_pic_.setImageUrl(itemList.get(position).getPartnerPic(), imageLoader);
            iv_usr_pic_.setVisibility(View.VISIBLE);
            iv_usr_pic_.setResponseObserver(new FeedImageView.ResponseObserver() { // download image
                @Override
                public void onError(VolleyError volleyError) {
                }

                @Override
                public void onSuccess(ImageLoader.ImageContainer loader) {

                    if (loader.getBitmap() != null) {
                        iv_usr_pic_.setVisibility(View.GONE);
                        String path = Utils.storeImage(loader.getBitmap(), finalMUserImageId, context);  // save image bitmap by chat id
                        if (!Utils.isEmptyString(path))
                            usrimg.setImageURI(Utils.getImageContentUri(context, new File(path))); // set image form uri once downloadedd
                        else
                            fromSignalDialog((Activity) context, context.getResources().getString(R.string.application_crash));

                    }
                }
            });
        }

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
                    Utils.trackMixpanel(((Activity) context), "", "", "AcceptUserRequest", false);//Track AcceptUserRequest through mixpanel
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
                Utils.trackMixpanel(((Activity) context), "", "", "CheckMyPartnerProfile", false);//Track CheckMyPartnerProfile through mixpanel
            }


        });//ends
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                int position = (Integer) v.getTag();

                authManager = ModelManager.getInstance().getAuthorizationManager();
                relationManager = ModelManager.getInstance().getRelationManager();
                Constants.itemPosition = position;
                Utils.launchBarDialog((Activity) context);
                if (Utils.isEmptyString(itemList.get(position).getStatusAccepted()) || Utils.isEmptyString(itemList.get(position).getStatusAccepted()) && itemList.get(position).getRequestInitiator().equalsIgnoreCase("true")) {
                    relationManager.updateStatus(itemList.get(position).getRelationshipId(), authManager.getPhoneNo(), authManager.getUsrToken(), "false");

                    Utils.trackMixpanel(((Activity) context), "", "", "RejectUserRequest", false);//Track RejectUserRequest through mixpanel
                } else {
                    Utils.trackMixpanel(((Activity) context), "", "", "DeletePartner", false);//Track Delete Partner through mixpanel
                    relationManager.deleteRelationship(itemList.get(position).getRelationshipId(), authManager.getPhoneNo(), authManager.getUsrToken());
                }


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

        RelativeLayout relativeLayout = (RelativeLayout) view;
        TextView button = (TextView) relativeLayout.getChildAt(2);
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
                Utils.trackMixpanel(((Activity) context), "", "", "RelationshipPrivacyChanged", false);//Track RelationshipPrivacyChanged through mixpanel
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
        TextView button = (TextView) relativeLayout.getChildAt(2);
        skip.setTag(button);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView button1 = (TextView) view.getTag();
                button1.setBackgroundResource(R.drawable.owner_profile_eye_icon);
                itemList.get(position1).setmStatuspublic("true");
                relationManager.changeUserVisibility(itemList.get(position1).getRelationshipId(), "true", authManager.getPhoneNo(), authManager.getUsrToken());
                Utils.trackMixpanel(((Activity) context), "", "", "RelationshipPrivacyChanged", false);//Track RelationshipPrivacyChanged through mixpanel
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
    public void fromSignalDialog(Activity activity, String str) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }
}
