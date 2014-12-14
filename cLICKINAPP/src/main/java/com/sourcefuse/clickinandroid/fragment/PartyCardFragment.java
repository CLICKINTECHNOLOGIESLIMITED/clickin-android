package com.sourcefuse.clickinandroid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.sourcefuse.clickinandroid.model.ChatManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.bean.TabBean;
import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinandroid.view.adapter.AdapterAll;
import com.sourcefuse.clickinandroid.view.adapter.CardGridViewAdapter;
import com.sourcefuse.clickinapp.R;

/**
 * Created by mukesh on 26/8/14.
 */

public class PartyCardFragment extends Fragment {

    private static final String TAG = "PartyCardFragment";
    GridView gridView;
    private CardGridViewAdapter customGridAdapter;
    private AdapterAll Alladapter;
    private ChatManager chatManager;
    private TabBean bean = new TabBean();
    private String card;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.frag_party, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView_party);
        chatManager = ModelManager.getInstance().getChatManager();
        card = bean.getTab_content();

        // chatManager.categories.get("All");
        Log.e(TAG, "Msg CCard To check" + card);

        if (card.equals("All")) {

            Alladapter = new AdapterAll(getActivity(), R.layout.row_card_grid, R.layout.card1, chatManager.categories.get("All"));
            gridView.setAdapter(Alladapter);
        } else {
            customGridAdapter = new CardGridViewAdapter(getActivity(), R.layout.row_card_grid, chatManager.categories.get(card));
            gridView.setAdapter(customGridAdapter);
        }
        return rootView;
    }


//    public static PartyCardFragment newInstance(int pos) {
//
//        PartyCardFragment f = new PartyCardFragment();
//
//        Bundle b = new Bundle();
//        b.putInt("text",  pos);
//        f.setArguments(b);
//        return f;
//    }

//    public static PartyCardFragment newInstance(String num) {
//        PartyCardFragment f = new PartyCardFragment();
//
//        // Supply num input as an argument.
//
//        return f;
//    }
}
