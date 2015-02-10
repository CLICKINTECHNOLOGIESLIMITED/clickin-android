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
import android.widget.TextView;

import com.sourcefuse.clickinandroid.utils.UnCaughtExceptionHandler;
import com.sourcefuse.clickinapp.R;

public final class TestFragment extends Fragment {

    static int imageS = 0;
    Activity _activity;

    TextView mPrivacyText, mTermText;

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
        //code- to handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtExceptionHandler(getActivity()));
        _activity = super.getActivity();
        if (getArguments() != null) {
            imageS = getArguments().getInt("img");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment, container, false);


        ImageView mImageView = (ImageView) view.findViewById(R.id.main_image);
        ImageView loginButton = (ImageView) view.findViewById(R.id.login);
        mImageView.setBackground(getActivity().getResources().getDrawable(imageS));

        mTermText = (TextView) view.findViewById(R.id.term_);
        mPrivacyText = (TextView) view.findViewById(R.id.privacy_text);
        mTermText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TermUseView.class);
                intent.putExtra("fromsplash", true);
                startActivity(intent);
            }
        });
        mPrivacyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PrivacyView.class);
                intent.putExtra("fromsplash", true);
                startActivity(intent);
            }
        });

        if (imageS == R.drawable.sixth_page) {

            view.findViewById(R.id.term_text).setVisibility(View.VISIBLE);
            view.findViewById(R.id.privacy_layout).setVisibility(View.VISIBLE);

            ImageView mClickHereToSignup = (ImageView) view.findViewById(R.id.clicktosignup);
            mClickHereToSignup.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            mClickHereToSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent signup = new Intent(getActivity(), SignUpView.class);
                    getActivity().startActivity(signup);
                    getActivity().finish();
                    coverFlowChecked();
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signIn = new Intent(getActivity(), SignInView.class);
                    getActivity().startActivity(signIn);
                    coverFlowChecked();
                    getActivity().finish();
                }
            });

        } else {
            view.findViewById(R.id.term_text).setVisibility(View.GONE);
            view.findViewById(R.id.privacy_layout).setVisibility(View.GONE);
            view.findViewById(R.id.clicktosignup).setVisibility(View.GONE);
        }


        return view;
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
