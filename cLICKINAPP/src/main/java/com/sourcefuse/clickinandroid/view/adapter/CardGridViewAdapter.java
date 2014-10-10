package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.bean.CardBean;
import com.sourcefuse.clickinandroid.utils.Log;

import com.sourcefuse.clickinandroid.view.Card;
import com.sourcefuse.clickinandroid.view.ViewTradeCart;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;
import java.util.List;

public class CardGridViewAdapter extends ArrayAdapter<CardBean> {
    Context context;
    int layoutResourceId;
    int card1;
    ChatManager chatManager;
    private List item;
    int pos;
    View row;

    private static final String TAG = "CardViewAdapter";

    public CardGridViewAdapter(Context context, int layoutResourceId, List<CardBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.item = item;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        pos = position;
        row = convertView;
        final CardBean item = getItem(position);
        RecordHolder holder = null;


        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();

            //holder.imageItem = (ImageView) row.findViewById(R.id.card_image_url);
            holder.cardTittle = (TextView) row.findViewById(R.id.card_tittle);
            holder.cardDescription = (TextView) row.findViewById(R.id.card_description);

//                holder.layout = (LinearLayout) row.findViewById(R.id.ll_layout);
            row.setTag(holder);

        } else {
            holder = (RecordHolder) row.getTag();
        }

        holder.cardTittle.setText(item.getCardTitle());
        holder.cardDescription.setText(item.getCardDescription());

        //  holder.imageItem.setImageBitmap(item.getCardUrl());


        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "View is Clicked" + pos);
                CardBean bean = getItem(position);

                String url = bean.getCardUrl();


                String Title = bean.getCardTitle();
                String Discription = bean.getCardDescription();

//                Log.e(TAG,"Value in Bean Tit" +Title);
//                Log.e(TAG,"Value in Bean Dis " +Discription);
                Intent intent = new Intent(getContext(), Card.class);
                // intent.putExtra("Title", Title);
                //  intent.putExtra("D", Discription);
                intent.putExtra("Url", url);
                Log.e(TAG, "Value in Bean Tit" + url);

                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);


            }
        });
        return row;

    }


    static class RecordHolder {

        TextView cardTittle, cardDescription;

    }
}