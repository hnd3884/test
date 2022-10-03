package com.adventnet.authentication.callback;

import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Serializable;
import javax.security.auth.callback.CallbackHandler;

public class LogoutCallbackHandler implements CallbackHandler, Serializable
{
    private static Logger logger;
    private Long sessionId;
    
    public LogoutCallbackHandler(final Long sessionId) {
        this.sessionId = null;
        LogoutCallbackHandler.logger.log(Level.FINEST, "LogoutCallbackHandler initialized with serviceId : {0}", new Object[] { sessionId });
        this.sessionId = sessionId;
    }
    
    @Override
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (callback instanceof SessionIdCallback) {
                ((SessionIdCallback)callback).setSessionId(this.sessionId);
                LogoutCallbackHandler.logger.log(Level.FINEST, "sessionId set in ServiceCallback");
            }
            else {
                LogoutCallbackHandler.logger.log(Level.INFO, "Unknown callback handler obtained in SimpleCallbackHandler.handle : {0}", callback);
            }
        }
    }
    
    static {
        LogoutCallbackHandler.logger = Logger.getLogger(LogoutCallbackHandler.class.getName());
    }
}
