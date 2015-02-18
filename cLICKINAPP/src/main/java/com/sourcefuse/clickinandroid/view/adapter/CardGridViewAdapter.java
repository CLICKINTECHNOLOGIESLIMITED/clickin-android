package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.bean.CardBean;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.Card;
import com.sourcefuse.clickinapp.R;

import java.util.List;

public class CardGridViewAdapter extends ArrayAdapter<CardBean> {
    private static final String TAG = "CardViewAdapter";
    Context context;
    int layoutResourceId;
    // String url = "https://s3.amazonaws.com/clickin-dev/cards/a/1080/39.jpg" ;
    private List item;
    int listsize = 0 ;


    public CardGridViewAdapter(Context context, int layoutResourceId, List<CardBean> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.item = item;
        listsize = item.size();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        CardBean card_items = null;

        View row = convertView;
        if(listsize > position) {
        card_items = getItem(position);
        }else {
            card_items = getItem(position-1);
        }
        RecordHolder holder = null;
     if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();


            holder.cardTittle = (TextView) row.findViewById(R.id.card_tittle);
            holder.cardDescription = (TextView) row.findViewById(R.id.card_description);


            row.setTag(holder);

        } else {
            holder = (RecordHolder) row.getTag();
        }


            holder.cardTittle.setText(card_items.getCardTitle());
            holder.cardDescription.setText(card_items.getCardDescription());

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CardBean bean = null;
                if(listsize > position) {
                   bean = getItem(position);
                }else {
                    bean = getItem(position-1);
                }
                String url = bean.getCardUrl();


                String Title = bean.getCardTitle();
                String Discription = bean.getCardDescription();

                Intent intent = new Intent(getContext(), Card.class);
                intent.putExtra("ForCounter", false);
                intent.putExtra("Title", Title);
                intent.putExtra("Discription", Discription);
                intent.putExtra("card_url", url);
                intent.putExtra("card_DB_ID", bean.getCard_Id());


                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
                //To track through mixPanel.
                //TradeCard Visited.
                Utils.trackMixpanel(context, "Card Visited", Discription, "RPageTradeButtonClicked", false);

            }

        });

        return row;

    }


    static class RecordHolder {

        TextView cardTittle, cardDescription;

    }
}