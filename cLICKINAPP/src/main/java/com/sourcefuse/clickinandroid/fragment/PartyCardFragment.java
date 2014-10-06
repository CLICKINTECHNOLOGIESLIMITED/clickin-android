package com.sourcefuse.clickinandroid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.bean.CardBean;
import com.sourcefuse.clickinandroid.view.adapter.CardGridViewAdapter;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;

/**
 * Created by mukesh on 26/8/14.
 */

public class PartyCardFragment extends Fragment {

    GridView gridView;
    ArrayList<CardBean> gridArray = new ArrayList<CardBean>();
    private CardGridViewAdapter customGridAdapter;

    private ChatManager chatManager;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_party, container, false);


        CardBean cb = new CardBean();
        cb.setCardTitle("");
        ArrayList<CardBean> nb = new ArrayList<CardBean>();
        nb.add(cb);

        nb.addAll(chatManager.categories.get("All"));
        /*chatManager = ModelManager.getInstance().getChatManager();
        chatManager.categories.get("All");
       // chatManager.categories.get("All");
        gridView = (GridView) rootView.findViewById(R.id.gridView_party);
        customGridAdapter = new CardGridViewAdapter(getActivity(), R.layout.row_card_grid,  chatManager.categories.get("All"));
        gridView.setAdapter(customGridAdapter);





        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Toast.makeText(getActivity(), ""+position, Toast.LENGTH_SHORT).show();
            }
        });*/


        return rootView;
    }
}