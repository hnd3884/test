package com.adventnet.authentication.listener;

import com.adventnet.authentication.PAM;
import com.adventnet.authentication.util.AuthUtil;
import javax.servlet.http.HttpSessionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener
{
    private static Logger logger;
    
    public SessionListener() {
        SessionListener.logger.log(Level.INFO, "initialized....");
    }
    
    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
        SessionListener.logger.log(Level.FINEST, "sessionCreated invoked");
    }
    
    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
        SessionListener.logger.log(Level.FINEST, "sessionDestroyed invoked");
        try {
            final String ssoId = (String)httpSessionEvent.getSession().getAttribute("JSESSIONIDSSO");
            if (ssoId == null) {
                SessionListener.logger.log(Level.FINE, "sessionDestroyed ignored as ssoId obtained is null;");
                AuthUtil.flushCredentials();
                return;
            }
            SessionListener.logger.log(Level.FINEST, "JESSIONIDSSO obtained from httpSession : {0}", ssoId);
            httpSessionEvent.getSession().removeAttribute("JSESSIONIDSSO");
            PAM.logout(ssoId);
        }
        catch (final Exception e) {
            SessionListener.logger.log(Level.SEVERE, "Exception occured while logout : ", e);
        }
    }
    
    static {
        SessionListener.logger = Logger.getLogger(SessionListener.class.getName());
    }
}
