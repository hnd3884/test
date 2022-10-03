package com.sun.net.httpserver;

import javax.net.ssl.SSLContext;
import jdk.Exported;

@Exported
public class HttpsConfigurator
{
    private SSLContext context;
    
    public HttpsConfigurator(final SSLContext context) {
        if (context == null) {
            throw new NullPointerException("null SSLContext");
        }
        this.context = context;
    }
    
    public SSLContext getSSLContext() {
        return this.context;
    }
    
    public void configure(final HttpsParameters httpsParameters) {
        httpsParameters.setSSLParameters(this.getSSLContext().getDefaultSSLParameters());
    }
}
