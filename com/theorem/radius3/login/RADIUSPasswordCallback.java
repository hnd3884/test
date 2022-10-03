package com.theorem.radius3.login;

import javax.security.auth.callback.PasswordCallback;

public final class RADIUSPasswordCallback extends PasswordCallback implements RADIUSCallback
{
    protected boolean a;
    
    RADIUSPasswordCallback(final String s, final boolean b) {
        super(s, b);
        this.setReady(true);
    }
    
    public final void setReady(final boolean a) {
        this.a = a;
    }
    
    public final boolean isReady() {
        return this.a;
    }
}
