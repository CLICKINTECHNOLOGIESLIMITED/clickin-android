package com.sourcefuse.clickinandroid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sourcefuse.clickinandroid.model.bean.SpreadTheWordBean;
import com.sourcefuse.clickinandroid.view.adapter.CurrentClickersAdapter;
import com.sourcefuse.clickinapp.R;

import java.util.ArrayList;


public class FacebookFragment extends Fragment {
    private View view;
    private ListView listView;
    private CurrentClickersAdapter adapter;
    public static ArrayList<SpreadTheWordBean> itData = new ArrayList<SpreadTheWordBean>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_phonebook, container, false);
        listView = (ListView) view.findViewById(R.id.list);

        setlist();

        return view;
    }

    public void setlist() {

       /* itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());
        itData.add(new SpreadTheWordBean());

        adapter = new CurrentClickersAdapter(getActivity(), R.layout.row_invitefriend, itData);

        int index = listView.getFirstVisiblePosition();
        View v = listView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        listView.setAdapter(adapter);
        listView.setSelectionFromTop(index, top);*/
    }


}
