/*
 * Copyright 2013 Hari Krishna Dulipudi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sourcefuse.clickinandroid.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.view.AddSomeoneView;
import com.sourcefuse.clickinapp.R;

import java.util.Arrays;
import java.util.Comparator;


public class SimpleSectionedListAdapter extends BaseAdapter implements PinnedSectionListViewNew.PinnedSectionListAdapter {
    Context mContext;
    private boolean mValid = true;
    private int mSectionResourceId;
    private LayoutInflater mLayoutInflater;
    private ListAdapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();
    private int mHeaderTextViewResId;
    private int mHeaderTextViewResId1;
    private Typeface typeface;
    private boolean hidevalue;

    public SimpleSectionedListAdapter(Context context, BaseAdapter baseAdapter, int sectionResourceId, int headerTextViewResId, int header1, Boolean value) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mSectionResourceId = sectionResourceId;
        mHeaderTextViewResId = headerTextViewResId;

        mHeaderTextViewResId1 = header1;
        mBaseAdapter = baseAdapter;
        hidevalue = value;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isSectionHeaderPosition(position)) {
            TextView view, view1;
            ;
            LinearLayout btn_someone_layout;
            convertView = mLayoutInflater.inflate(mSectionResourceId, parent, false);

            typeface = Typeface.createFromAsset(mContext.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);
            view = (TextView) convertView.findViewById(mHeaderTextViewResId);
            view1 = (TextView) convertView.findViewById(mHeaderTextViewResId1);
            btn_someone_layout = (LinearLayout) convertView.findViewById(R.id.btn_someone_layout);
            view1.setTypeface(typeface);
            view.setTypeface(typeface);

            if (hidevalue) {
                btn_someone_layout.setVisibility(View.GONE);

            } else {

                btn_someone_layout.setVisibility(View.VISIBLE);
            }
            btn_someone_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AddSomeoneView.class);
                    intent.putExtra("FromOwnProfile", true);
                    mContext.startActivity(intent);
                }
            });
            view.setText(mSections.get(position).title);
            view1.setText(mSections.get(position).title2);
            return convertView;

        } else {
            return mBaseAdapter.getView(sectionedPositionToPosition(position), convertView, parent);
        }
    }

    @Override
    public boolean isItemViewTypePinned(int position) {
        return isSectionHeaderPosition(position);
    }

    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;
        CharSequence title2;

        public Section(int firstPosition, CharSequence title, CharSequence title2) {
            this.firstPosition = firstPosition;
            this.title = title;
            this.title2 = title2;

        }

        public CharSequence getTitle() {
            return title;
        }
    }
}