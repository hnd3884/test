package com.theorem.radius3.login;

import javax.security.auth.callback.TextOutputCallback;

public final class RADIUSTextOutputCallback extends TextOutputCallback implements RADIUSCallback
{
    protected boolean a;
    
    public final void setReady(final boolean a) {
        this.a = a;
    }
    
    public final boolean isReady() {
        return this.a;
    }
}
