package com.sourcefuse.clickinandroid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.view.adapter.AdapterAll;
import com.sourcefuse.clickinapp.R;

/**
 * Created by akshit on 7/10/14.
 */
public class CustomFragment extends Fragment {

    GridView gridView;
    ChatManager chatManager;
    AdapterAll Alladapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
// View rootView = inflater.inflate(R.layout.frag_party, container, false);

        View rootView = inflater.inflate(R.layout.frag_party,container,false);
        gridView = (GridView) rootView.findViewById(R.id.gridView_party);
        chatManager = ModelManager.getInstance().getChatManager();

        Alladapter = new AdapterAll(getActivity(), R.layout.row_card_grid, R.layout.card1, chatManager.categories.get("All"));
        gridView.setAdapter(Alladapter);



        return rootView;
    }

    public static CustomFragment newInstance(int num) {
        CustomFragment f = new CustomFragment();

        // Supply num input as an argument.

        return f;
    }
}
