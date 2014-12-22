package com.sourcefuse.clickinandroid.view;

/**
 * Created by chandra on 25/7/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.sourcefuse.clickinandroid.utils.Log;
import com.sourcefuse.clickinapp.R;

public final class TestFragment extends Fragment {

    static int imageS = 0;
    Activity _activity;

    public static TestFragment newInstance(int imageS) {
        TestFragment fragment = new TestFragment();

        Bundle args = new Bundle();
        args.putInt("img", imageS);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activity = super.getActivity();
        if (getArguments() != null) {
            imageS = getArguments().getInt("img");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout mainlayout = new RelativeLayout(getActivity());
        mainlayout.setLayoutParams(new LayoutParams
                (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


        ImageView img = new ImageView(getActivity());
        img.setImageDrawable(getActivity().getResources().getDrawable(imageS));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mainlayout.addView(img);
        if (imageS == R.drawable.sixth_page) {

            ImageView clickHereToSignup = new ImageView(getActivity());
            clickHereToSignup.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.signup_btn));
            clickHereToSignup.setId(2);
            LayoutParams params = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            params.setMargins(pxlToDp(113), pxlToDp(258), 0, 0);
            clickHereToSignup.setLayoutParams(params);
            mainlayout.addView(clickHereToSignup);

            clickHereToSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.util.Log.e("ClickHereToSignUp Clicked", "true");

                    Intent signup = new Intent(getActivity(), SignUpView.class);
                    getActivity().startActivity(signup);
                    getActivity().finish();

                    coverFlowChecked();

                }


            });

            ImageView loginButton = new ImageView(getActivity());
            loginButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.coverflow_login));
            loginButton.setId(3);
            LayoutParams params1 = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params1.setMargins(pxlToDp(120), 0, 0, pxlToDp(9));
            loginButton.setLayoutParams(params1);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.util.Log.e("Login Clicked", "true");
                    Intent signIn = new Intent(getActivity(), SignInView.class);
                    getActivity().startActivity(signIn);
                    coverFlowChecked();
                    getActivity().finish();
                }
            });


            mainlayout.addView(loginButton);


        }


        return mainlayout;
    }

    private void coverFlowChecked() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("coverFlow", true);
        editor.commit();

    }

    private int pxlToDp(int pixel) {

        final float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (pixel * scale + 0.5f);
        return dp;
    }


}
