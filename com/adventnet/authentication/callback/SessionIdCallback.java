package com.adventnet.authentication.callback;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class SessionIdCallback implements Callback, Serializable
{
    private Long sessionId;
    
    public SessionIdCallback() {
        this.sessionId = null;
    }
    
    public void setSessionId(final Long sessionId) {
        this.sessionId = sessionId;
    }
    
    public Long getSessionId() {
        return this.sessionId;
    }
}
