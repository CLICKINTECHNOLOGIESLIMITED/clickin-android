package com.sourcefuse.clickinandroid.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Process;

import com.sourcefuse.clickinandroid.view.DialogActivity;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by monika on 3/2/15.
 */

/**
 * This class is called when Uncaught Exception is occurs.
 */
public class UnCaughtExceptionHandler implements
        Thread.UncaughtExceptionHandler {
    private final Activity myContext;

    public UnCaughtExceptionHandler(Activity context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, final Throwable exception) {

        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        String s = stackTrace.toString();


        /* Information When Application Got Crashed.*/
        String mInfo = "BRAND ====>" + Build.BRAND + "\nDevice ====>" + Build.DEVICE + "\nModel ====>" + Build.MODEL +
                "\nId ====>" + Build.ID + "\nProduct ====>" + Build.PRODUCT + "\nSDK ====>" +
                Build.VERSION.SDK + "\nRelease ====>" + Build.VERSION.RELEASE + "\nIncremental ====>" +
                Build.VERSION.INCREMENTAL + "\nMANUFACTURER ====>" + Build.MANUFACTURER + "\nActivity ====>" + myContext + "\nException ====>" + stackTrace.toString();


        /**
         * Start Dialog Activity when application got crashed.
         *
         * */
        Intent intent = new Intent(myContext, DialogActivity.class);
        intent.putExtra("mInfo", mInfo);
        myContext.startActivity(intent);

        //for restarting the Activity
        Process.killProcess(Process.myPid());
        System.exit(0);


    }


}