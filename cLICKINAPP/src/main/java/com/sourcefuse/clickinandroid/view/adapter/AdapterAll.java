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
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.view.Card;
import com.sourcefuse.clickinandroid.view.ViewTradeCart;
import com.sourcefuse.clickinapp.R;

import java.util.List;

/**
 * Created by akshit on 2/10/14.
 */
public class AdapterAll extends ArrayAdapter<CardBean> {

    Context context ;
    int layoutresourceid ;
    int LayoutCard;
    TextView tv , tv2;
    //List<ArrayList> mlist ;
    public AdapterAll(Context context, int layoutresourceid , int LayoutCard , List<CardBean> item ) {
        super(context, layoutresourceid , LayoutCard ,item);
        this.context = context;
        this.layoutresourceid = layoutresourceid;
        this.LayoutCard = LayoutCard;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // mlist = new ArrayList<ArrayList>();
        View view = convertView;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        if(position==0){
            view = inflater.inflate(LayoutCard,parent,false);
        }else{
            view = inflater.inflate(layoutresourceid,parent,false);
            tv = (TextView)view.findViewById(R.id.card_tittle);
            tv2 = (TextView)view.findViewById(R.id.card_description);

            try{
                CardBean item = getItem(position - 1);
//            for (position= 1;position<=mlist.size();position++){
                //  mlist.add(item);
                tv.setText(item.getCardTitle());
                tv2.setText(item.getCardDescription());

            }catch (Exception e){
                e.printStackTrace();
                Log.e("Exception" ," Exception in " +e.toString());
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position==0){

                    Intent intent = new Intent(getContext(), ViewTradeCart.class);
                    intent.putExtra("ForCounter", false);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

                }
                else{

                    CardBean bean = getItem(position-1);
                    String url = bean.getCardUrl();
//                    String url = bean.getCardUrl() ;
                    String add = "/a/1080" ;


                  //  url=url1.replaceFirst("cards\\/(\\d+)\\.jpg","cards\\/a\\/1080\\/$1\\.jpg");
                    Log.e("All Adapter " , "Original URL::>>>>>>>>" +url);


					
                    String Title = bean.getCardTitle();
                    String Discription = bean.getCardDescription();

                    Intent intent = new Intent(getContext(), Card.class);
                    intent.putExtra("ForCounter", false);
                    intent.putExtra("Title", Title);
                    intent.putExtra("Discription", Discription);
                    intent.putExtra("Url", url);
                    intent.putExtra("card_Db_id", bean.getCard_Id());
                   // intent.putExtra("card_id", bean.getCard_Id());
                    Log.e("CARD DETAILS","----->"+Title+","+Discription+","+bean.getCard_Id());

                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

                }

            }
        });

        return view;


    }
}