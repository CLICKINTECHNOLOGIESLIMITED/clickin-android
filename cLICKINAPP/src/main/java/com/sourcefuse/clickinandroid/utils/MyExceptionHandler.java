package com.sourcefuse.clickinandroid.utils;

import android.content.Context;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;
import android.os.Process;
import android.widget.Toast;

import com.sourcefuse.clickinandroid.view.SplashView;

/**
 * Created by monika on 3/2/15.
 */
public class MyExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Context myContext;


    public MyExceptionHandler(Context context) {

        myContext = context;

    }

    public void uncaughtException(Thread thread, Throwable exception) {

        Toast.makeText(myContext, "App Crashed",Toast.LENGTH_LONG).show();
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        System.err.println(stackTrace);// You can use LogCat too
        Intent intent = new Intent(myContext, SplashView.class);
        String s = stackTrace.toString();
        //you can use this String to know what caused the exception and in which Activity
        intent.putExtra("uncaughtException",
                "Exception is: " + stackTrace.toString());
        intent.putExtra("stacktrace", s);
        myContext.startActivity(intent);
        //for restarting the Activity
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}