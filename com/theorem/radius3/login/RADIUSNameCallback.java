package com.theorem.radius3.login;

import javax.security.auth.callback.NameCallback;

public final class RADIUSNameCallback extends NameCallback implements RADIUSCallback
{
    protected boolean a;
    
    RADIUSNameCallback(final String s) {
        super(s);
        this.setReady(true);
    }
    
    public final void setReady(final boolean a) {
        this.a = a;
    }
    
    public final boolean isReady() {
        return this.a;
    }
}
