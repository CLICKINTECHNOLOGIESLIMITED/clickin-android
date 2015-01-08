package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.JumpOtherProfileView;
import com.sourcefuse.clickinandroid.view.UserProfileView;
import com.sourcefuse.clickinapp.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by chandra on 3/7/14.
 */
public class SimpleSectionedListAdapter2 extends BaseAdapter implements PinnedSecListViewForFeed.PinnedSectionListAdapter {
    Context mContext;
    Activity activity;
    private boolean mValid = true;
    private int mSectionResourceId;
    private LayoutInflater mLayoutInflater;
    private ListAdapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();
    private int mHeaderTextViewResId;
    private int mHeaderTextViewResId1;
    private int mHeaderImageViewResId;
    private int feedTimeResId;

    public SimpleSectionedListAdapter2(Activity context, BaseAdapter baseAdapter, int sectionResourceId, int headerTextViewResId, int headerImageViewResId, int header1, int feedId) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mSectionResourceId = sectionResourceId;
        mHeaderTextViewResId = headerTextViewResId;
        mHeaderImageViewResId = headerImageViewResId;
        mHeaderTextViewResId1 = header1;
        mBaseAdapter = baseAdapter;
        feedTimeResId = feedId;


        mBaseAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mValid = !mBaseAdapter.isEmpty();
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                mValid = false;
                notifyDataSetInvalidated();
            }
        });
    }

    public void setSections(Section[] sections) {
        mSections.clear();

        notifyDataSetChanged();
        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return ListView.INVALID_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }

    @Override
    public int getCount() {
        return (mValid ? mBaseAdapter.getCount() + mSections.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return isSectionHeaderPosition(position)
                ? mSections.get(position)
                : mBaseAdapter.getItem(sectionedPositionToPosition(position));
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? getViewTypeCount() - 1
                : mBaseAdapter.getItemViewType(position);
    }

    @Override
    public boolean isEnabled(int position) {
        //noinspection SimplifiableConditionalExpression
        return isSectionHeaderPosition(position)
                ? false
                : mBaseAdapter.isEnabled(sectionedPositionToPosition(position));
    }

    @Override
    public int getViewTypeCount() {
        return mBaseAdapter.getViewTypeCount() + 1; // the section headings
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mBaseAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean hasStableIds() {
        return mBaseAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return mBaseAdapter.isEmpty();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (isSectionHeaderPosition(position)) {
            final TextView view, view1;
            TextView feed_time;
            ImageView doubleArrow;
            ImageView imageview;
            if (null == convertView) {
                convertView = mLayoutInflater.inflate(mSectionResourceId, parent, false);
            } else {
                if (null == convertView.findViewById(mHeaderTextViewResId)) {
                    convertView = mLayoutInflater.inflate(mSectionResourceId, parent, false);
                }

            }
            view = (TextView) convertView.findViewById(mHeaderTextViewResId);
            view1 = (TextView) convertView.findViewById(mHeaderTextViewResId1);
            feed_time = (TextView) convertView.findViewById(feedTimeResId);
            imageview = (ImageView) convertView.findViewById(mHeaderImageViewResId);
            doubleArrow = (ImageView) convertView.findViewById(R.id.doubleArrow);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);

            ProfileManager prMgr = ModelManager.getInstance().getProfileManager();
            if (mSections.get(position) != null)
                if ((mSections.get(position).senderId) != null) {
//                    if((mSections.get(position).receiverId)!=null){
                    if (mSections.get(position).senderName.toString().equalsIgnoreCase("null"))
                        mSections.get(position).senderName = "";

                    if ((mSections.get(position).recieverName) != null) {
                        if (mSections.get(position).recieverName.toString().equalsIgnoreCase("null"))
                            mSections.get(position).recieverName = "";
                    }
                    AuthManager authManager = ModelManager.getInstance().getAuthorizationManager();
                    if (mSections.get(position).senderId.toString().equalsIgnoreCase(authManager.getUserId())) {
                        view.setText(mSections.get(position).senderName);
                        view1.setText(mSections.get(position).recieverName);

                        doubleArrow.setImageResource(R.drawable.arrow);
                        if (authManager.getGender() != null) {
                            if (authManager.getGender().matches("guy")) {

                                try {
                                    if (!mSections.get(position).senderImages.toString().equalsIgnoreCase("")) {
                                        Picasso.with(mContext)
                                                .load(mSections.get(position).senderImages)
                                                .error(R.drawable.male_user).into(imageview);

                                    } else {
                                        imageview.setImageResource(R.drawable.male_user);
                                    }
                                } catch (Exception e) {
                                    imageview.setImageResource(R.drawable.male_user);
                                }
                            } else {
                                try {
                                    if (!mSections.get(position).senderImages.toString().equalsIgnoreCase("")) {
                                        Picasso.with(mContext)
                                                .load(mSections.get(position).senderImages)
                                                .error(R.drawable.female_user).into(imageview);
                                    } else {
                                        imageview.setImageResource(R.drawable.female_user);
                                    }
                                } catch (Exception e) {
                                    imageview.setImageResource(R.drawable.female_user);
                                }
                            }
                        } else {
                            imageview.setImageResource(R.drawable.male_user);
                        }

                    } else {
                        if (prMgr.following != null) {
                            for (int i = 0; i < prMgr.following.size(); i++) {
                                if ((mSections.get(position).senderId).toString().equalsIgnoreCase(prMgr.following.get(i).getFolloweeId())) {
                                    view.setText(mSections.get(position).senderName);
                                    view1.setText(mSections.get(position).recieverName);
                                    try {
                                        if (!mSections.get(position).senderImages.toString().equalsIgnoreCase("")) {
                                            Picasso.with(mContext)
                                                    .load(mSections.get(position).senderImages)
                                                    .error(R.drawable.male_user).into(imageview);
                                        } else {
                                            imageview.setImageResource(R.drawable.male_user);
                                        }
                                    } catch (Exception e) {
                                        imageview.setImageResource(R.drawable.male_user);
                                    }
                                    doubleArrow.setImageResource(R.drawable.arrow);
                                    break;
                                } else {
//                                if((mSections.get(position).receiverId)!=null) {
//                                    if ((mSections.get(position).receiverId).toString().equalsIgnoreCase(prMgr.following.get(i).getFolloweeId())) {
                                    doubleArrow.setImageResource(R.drawable.flip_arow);
                                    view.setText(mSections.get(position).recieverName);
                                    view1.setText(mSections.get(position).senderName);
                                    Picasso.with(mContext).load(mSections.get(position).recieverImages).placeholder(R.drawable.dcontact).into(imageview);
                                    try {
                                        if (!mSections.get(position).recieverImages.toString().equalsIgnoreCase("")) {
                                            Picasso.with(mContext)
                                                    .load(mSections.get(position).recieverImages)
                                                    .error(R.drawable.male_user).into(imageview);
                                        } else {
                                            imageview.setImageResource(R.drawable.male_user);
                                        }
                                    } catch (Exception e) {
                                        imageview.setImageResource(R.drawable.male_user);
                                    }
//                                        break;
//                                    }
                                }
                            }
                        } else {
                            view.setText(mSections.get(position).senderName);
                            view1.setText(mSections.get(position).recieverName);
                            try {
                                if (!mSections.get(position).senderImages.toString().equalsIgnoreCase("")) {
                                    Picasso.with(mContext)
                                            .load(mSections.get(position).senderImages)
                                            .error(R.drawable.male_user).into(imageview);
                                } else {
                                    imageview.setImageResource(R.drawable.male_user);
                                }
                            } catch (Exception e) {
                                imageview.setImageResource(R.drawable.male_user);
                            }
                            doubleArrow.setImageResource(R.drawable.arrow);
                        }
                    }
//                    if ((mSections.get(position).senderId).toString().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserId())) {
//
//                        view.setText(mSections.get(position).senderName);
//                        view1.setText(mSections.get(position).recieverName);
//                        Picasso.with(mContext).load(mSections.get(position).senderImages).placeholder(R.drawable.ic_launcher).into(imageview);
//                        doubleArrow.setImageResource(R.drawable.arrow);
//                    } else {
//                        doubleArrow.setImageResource(R.drawable.flip_arow);
//                        view.setText(mSections.get(position).recieverName);
//                        view1.setText(mSections.get(position).senderName);
//                        Picasso.with(mContext).load(mSections.get(position).recieverImages).placeholder(R.drawable.ic_launcher).into(imageview);
//                    }
//                    }
                }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phNo, name;
                    if (view.getText().toString().trim().equalsIgnoreCase(mSections.get(position).senderName.toString())) {
                        phNo = mSections.get(position).senderPhNo;
                    } else {
                        phNo = mSections.get(position).rcvrPhNo;
                    }
                    Intent viewProfile = null;
//                    if(ModelManager.getInstance().getAuthorizationManager().getUserId().toString().equalsIgnoreCase(mSections.get(position).senderId.toString()))
//                    if(view.getText().toString().trim().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserName()))
//                        viewProfile = new Intent(mContext, UserProfileView.class);
//                    else {
                    viewProfile = new Intent(mContext, UserProfileView.class);
                    viewProfile.putExtra("FromOwnProfile", true);
                    viewProfile.putExtra("phNumber", phNo);
                    viewProfile.putExtra("name", view.getText().toString());
//                    }

                    //      viewProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(viewProfile);
                }
            });
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phNo;
                    if (view1.getText().toString().trim().equalsIgnoreCase(mSections.get(position).senderName.toString())) {
                        phNo = mSections.get(position).senderPhNo;
                    } else {
                        phNo = mSections.get(position).rcvrPhNo;
                    }
                    Intent viewProfile = null;
//                    if(ModelManager.getInstance().getAuthorizationManager().getUserId().toString().equalsIgnoreCase(mSections.get(position).senderId.toString()))
//                    if(view1.getText().toString().trim().equalsIgnoreCase(ModelManager.getInstance().getAuthorizationManager().getUserName()))
//                        viewProfile = new Intent(mContext, UserProfileView.class);
//                    else {
                    viewProfile = new Intent(mContext, JumpOtherProfileView.class);
                    viewProfile.putExtra("FromOwnProfile", true);
                    viewProfile.putExtra("phNumber", phNo);
                    viewProfile.putExtra("name", view1.getText().toString());
//                    }

                    viewProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(viewProfile);
                }
            });
            if (mSections.get(position).timeOfFeed != null) {
                feed_time.setText(Utils.getLocalDate(mSections.get(position).timeOfFeed));
            }
            if ((mSections.get(position).senderId) == null)
                return null;
            else
                return convertView;

        } else {
            return mBaseAdapter.getView(sectionedPositionToPosition(position), convertView, parent);
        }
//        return convertView;
    }

    @Override
    public boolean isItemViewTypePinned(int position) {
        return isSectionHeaderPosition(position);
    }

    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence senderName;
        CharSequence recieverName;
        String senderImages;
        String recieverImages;
        CharSequence senderId;
        CharSequence receiverId;
        String senderPhNo, rcvrPhNo;

        String timeOfFeed;

        public Section(int firstPosition, CharSequence title, CharSequence title2, String urlOfImage, String recieverImages, String timeOfFeed, String senderId, String receiverId, String senderPhNo, String rcvrPhNo) {
            this.firstPosition = firstPosition;
            this.senderName = title;
            this.recieverName = title2;
            this.senderImages = urlOfImage;
            this.timeOfFeed = timeOfFeed;
            this.recieverImages = recieverImages;
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.senderPhNo = senderPhNo;
            this.rcvrPhNo = rcvrPhNo;
        }


    }
}
