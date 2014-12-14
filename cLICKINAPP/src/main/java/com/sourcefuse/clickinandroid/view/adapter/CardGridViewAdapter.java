package com.sourcefuse.clickinandroid.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.bean.CardBean;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.view.Card;
import com.sourcefuse.clickinapp.R;

import java.util.List;

public class CardGridViewAdapter extends ArrayAdapter<CardBean> {
    private static final String TAG = "CardViewAdapter";
    Context context;
    int layoutResourceId;
    int card1;
    ChatManager chatManager;
    int pos;
    View row;
    boolean debug = false;
    // String url = "https://s3.amazonaws.com/clickin-dev/cards/a/1080/39.jpg" ;
    private List item;

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


            holder.cardTittle = (TextView) row.findViewById(R.id.card_tittle);
            holder.cardDescription = (TextView) row.findViewById(R.id.card_description);


            row.setTag(holder);

        } else {
            holder = (RecordHolder) row.getTag();
        }

        holder.cardTittle.setText(item.getCardTitle());
        holder.cardDescription.setText(item.getCardDescription());


        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "View is Clicked" + pos);

                CardBean bean = getItem(position);
                String url = bean.getCardUrl();


                String Title = bean.getCardTitle();
                String Discription = bean.getCardDescription();

                Intent intent = new Intent(getContext(), Card.class);
                intent.putExtra("ForCounter", false);
                intent.putExtra("Title", Title);
                intent.putExtra("Discription", Discription);
                intent.putExtra("card_url", url);
                intent.putExtra("card_DB_ID", bean.getCard_Id());
                // intent.putExtra("card_id", bean.getCard_Id());
                Log.e("CARD DETAILS", "----->" + Title + "," + Discription + "," + bean.getCard_Id());

                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

            }

        });

        return row;

    }


    static class RecordHolder {

        TextView cardTittle, cardDescription;

    }
}