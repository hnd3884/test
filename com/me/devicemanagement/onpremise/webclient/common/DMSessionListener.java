package com.me.devicemanagement.onpremise.webclient.common;

import com.me.devicemanagement.onpremise.webclient.sdp.DCAuthenticateRemote;
import javax.servlet.http.HttpSessionEvent;
import java.util.logging.Logger;
import javax.servlet.http.HttpSessionListener;

public class DMSessionListener implements HttpSessionListener
{
    private static Logger logger;
    
    public void sessionCreated(final HttpSessionEvent se) {
    }
    
    public void sessionDestroyed(final HttpSessionEvent se) {
        final String sessId = se.getSession().getId();
        DCAuthenticateRemote.destorySessionValues(sessId);
    }
    
    static {
        DMSessionListener.logger = Logger.getLogger("SDPLogger");
    }
}
