package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.sourcefuse.clickinandroid.model.PicassoManager;
import com.sourcefuse.clickinandroid.model.bean.GetrelationshipsBean;
import com.sourcefuse.clickinandroid.utils.AppController;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FeedImageView;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.AddSomeoneView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by mukesh on 4/7/14.
 */

public class ClickInWithAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {
    Context context;
    int layoutResourceId;
    List<GetrelationshipsBean> item1;
    private Typeface typeface;
    private LayoutInflater mInflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    /*LruCache mLruCahe;
    Picasso picasso;*/
    public ClickInWithAdapter(Context context, int layoutResourceId, List<GetrelationshipsBean> item) {

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.item1 = item;
        /*mLruCahe = new LruCache(context);
        picasso = new Picasso.Builder(context).memoryCache(mLruCahe).build();*/

    }

    @Override
    public int getCount() {
        int size = item1.size();
        if (size != 0)
            return item1.size();
        else
            return 1;


    }

    @Override
    public Object getItem(int position) {
        int size = item1.size();
        if (size != 0)
            return item1.get(position);
        else
            return "a";

        //return item1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    /* for sticky header list*/

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.clickInUsrName = (TextView) row.findViewById(R.id.tv_clickInUsr_name);
            holder.unReadNo = (TextView) row.findViewById(R.id.tv_unread);
            holder.clickInUsrimg = (ImageView) row.findViewById(R.id.iv_ClickInUsrImg);
            holder.iv_usr_pic_ = (FeedImageView) row.findViewById(R.id.iv_ClickInUsrImg_);
            holder.header_layout = (LinearLayout) row.findViewById(R.id.header_layout);
            holder.user_layout = (LinearLayout) row.findViewById(R.id.user_layout);
            typeface = Typeface.createFromAsset(context.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN);
            holder.clickInUsrName.setTypeface(typeface, typeface.BOLD);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        final RecordHolder rholder = (RecordHolder) row.getTag();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        if (item1.size() != 0) {
            rholder.user_layout.setVisibility(View.VISIBLE);
            rholder.header_layout.setVisibility(View.GONE);
            if (!Utils.isEmptyString(item1.get(position).getStatusAccepted()) && item1.get(position).getStatusAccepted().equalsIgnoreCase("true")) {

                rholder.clickInUsrName.setText(item1.get(position).getPartnerName());

                if (item1.get(position).getUnreadMsg() != 0) {
                    rholder.unReadNo.setText("" + item1.get(position).getUnreadMsg());
                    rholder.unReadNo.setVisibility(View.VISIBLE);

                } else {
                    rholder.unReadNo.setVisibility(View.GONE);

                }

                final String finalMUserImageId = item1.get(position).getRelationshipId();

                String mContentUri = Utils.mImagePath + finalMUserImageId + ".jpg";
                Uri mUri = Utils.getImageContentUri(context, new File(mContentUri));  //check file exist or not

                if (!Utils.isEmptyString("" + mUri)) {
                    rholder.clickInUsrimg.setImageURI(mUri); // if file exists set it by uri
                } else {
                    Log.e("User relation adapter ---> ", "" + item1.get(position).getPartnerPic());
                    holder.iv_usr_pic_.setImageUrl(item1.get(position).getPartnerPic(), imageLoader);
                    holder.iv_usr_pic_.setResponseObserver(new FeedImageView.ResponseObserver() { // download image
                        @Override
                        public void onError(VolleyError volleyError) {
                        }

                        @Override
                        public void onSuccess(ImageLoader.ImageContainer loader) {
                            Log.e("on success--->", "on success--->");
                            if (loader.getBitmap() != null) {
                                String path = Utils.storeImage(loader.getBitmap(), finalMUserImageId, context);  // save image bitmap by chat id
                                rholder.clickInUsrimg.setImageURI(Utils.getImageContentUri(context, new File(path))); // set image form uri once downloadedd

                            }
                        }
                    });
                }
            } else {
                rholder.clickInUsrimg.setImageResource(R.drawable.male_user);
            }

            if (((item1.size() - 1) == position))
                row.findViewById(R.id.clcth_divider).setVisibility(View.GONE);
            else
                row.findViewById(R.id.clcth_divider).setVisibility(View.VISIBLE);
        }else {
            rholder.user_layout.setVisibility(View.GONE);
            rholder.header_layout.setVisibility(View.VISIBLE);
            holder.header_layout.findViewById(R.id.header_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddSomeoneView.class);
                    intent.putExtra("FromOwnProfile", true);
                    context.startActivity(intent);
                }
            });


        }

        return row;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        mInflater = LayoutInflater.from(context);
        convertView = mInflater.inflate(R.layout.header_clickwith, parent, false);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    static class RecordHolder {
        TextView clickInUsrName, unReadNo;
        ImageView clickInUsrimg;
        FeedImageView iv_usr_pic_;
        LinearLayout header_layout,user_layout;


    }
}

