package com.sourcefuse.clickinandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sourcefuse.clickinandroid.view.ViewTradeCart;
import com.sourcefuse.clickinapp.R;

/**
 * Created by akshit on 10/10/14.
 */
public class FragmentCustomTab extends Fragment {

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview;
        rootview = inflater.inflate(R.layout.custom_layout, container, false);
        FrameLayout layout = (FrameLayout) rootview.findViewById(R.id.framelayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewTradeCart.class);
                intent.putExtra("ForCounter", false);
                FragmentCustomTab.this.startActivity(intent);
                //((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay);

            }
        });
        return rootview;


    }

//    @Override
//    public void onClick(View view) {
//        Intent intent = new Intent(this.context, ViewTradeCart.class);
//        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
//
//    }

}

