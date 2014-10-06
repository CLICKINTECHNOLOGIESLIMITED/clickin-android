package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.CardBean;
import com.sourcefuse.clickinapp.R;

import java.util.List;

public class CardGridViewAdapter extends ArrayAdapter<CardBean> {
    Context context;
    int layoutResourceId;


    public CardGridViewAdapter(Context context, int layoutResourceId,List<CardBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final CardBean item = getItem(position);
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();

            //holder.imageItem = (ImageView) row.findViewById(R.id.card_image_url);
            holder.cardTittle = (TextView) row.findViewById(R.id.card_tittle);
            holder.cardDescription = (TextView) row.findViewById(R.id.card_description);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

       /* try{
            Picasso.with(context).load(item.getCardUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(holder.imageItem);
        }catch(Exception e){

        }*/

        holder.cardTittle.setText(item.getCardTitle());
        holder.cardDescription.setText(item.getCardDescription());

      //  holder.imageItem.setImageBitmap(item.getCardUrl());
        return row;
    }

    static class RecordHolder {
        ImageView imageItem;
       TextView cardTittle,cardDescription;
    }
}

