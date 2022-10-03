package com.theorem.radius3.login;

import javax.security.auth.callback.LanguageCallback;

public final class RADIUSLanguageCallback extends LanguageCallback implements RADIUSCallback
{
    protected boolean a;
    
    RADIUSLanguageCallback() {
        this.setReady(true);
    }
    
    public final void setReady(final boolean a) {
        this.a = a;
    }
    
    public final boolean isReady() {
        return this.a;
    }
}
