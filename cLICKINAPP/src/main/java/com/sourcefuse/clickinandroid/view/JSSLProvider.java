package com.sourcefuse.clickinandroid.view;

/**
 * Created by akshit on 16/2/15.
 */


/*
* This Class is created to send mail in back ground.It provide
* security permissions so that SSL content may be handled.
* */

import java.security.AccessController;
import java.security.Provider;

public class JSSLProvider extends Provider {

    public JSSLProvider() {

        super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");

        AccessController

                .doPrivileged(new java.security.PrivilegedAction<Void>() {

                    public Void run() {

                        put("SSLContext.TLS",

                                "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");

                        put("Alg.Alias.SSLContext.TLSv1", "TLS");

                        put("KeyManagerFactory.X509",

                                "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");

                        put("TrustManagerFactory.X509",

                                "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");

                        return null;

                    }


                });

    }
}