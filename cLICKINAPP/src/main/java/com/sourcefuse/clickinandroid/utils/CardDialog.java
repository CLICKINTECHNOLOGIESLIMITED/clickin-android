package com.sourcefuse.clickinandroid.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.sourcefuse.clickinapp.R;

/**
 * Created by akshit on 30/10/14.
 */
public class CardDialog {


        public Dialog mdialog ;

        public static void popupDialog(final Activity contex) {

            final Dialog mdialog   = new Dialog(contex);
            mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            mdialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mdialog.setContentView(R.layout.card_click_popup);
            mdialog.setCancelable(false);
            Button ok = (Button)mdialog.findViewById(R.id.dialogButtonOK);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mdialog.dismiss();
                }
            });

            mdialog.show();
        }


    }


