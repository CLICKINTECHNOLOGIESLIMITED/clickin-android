package com.sourcefuse.clickinandroid.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sourcefuse.clickinapp.R;

/**
 * Created by mukesh on 10/9/14.
 */
public class ClickInAlertDialog {


    public static void clickInAlert(Activity contex, String msg, String tittleText, Boolean tittle) {

        // custom dialog
        final Dialog dialog = new Dialog(contex);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_network_error);
        dialog.setCancelable(false);
        TextView text = (TextView) dialog.findViewById(R.id.text);
        if (tittle) {
            TextView alertTittle = (TextView) dialog.findViewById(R.id.alert_tittle);
            alertTittle.setVisibility(View.VISIBLE);
            alertTittle.setText(tittleText);
        }
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
