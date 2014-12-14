package com.sourcefuse.clickinandroid.view;

/**
 * Created by mukesh on 9/9/14.
 */

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sourcefuse.clickinapp.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TabFragment extends Fragment {

    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        index = data.getInt("idx");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_fragment, null);
        TextView tv = (TextView) v.findViewById(R.id.msg);
        tv.setText("Fragment " + (index + 1));

        return v;

    }


}
