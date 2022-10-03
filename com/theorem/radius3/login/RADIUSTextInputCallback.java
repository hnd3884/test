package com.theorem.radius3.login;

import javax.security.auth.callback.TextInputCallback;

public final class RADIUSTextInputCallback extends TextInputCallback implements RADIUSCallback
{
    protected int a;
    protected boolean b;
    protected boolean c;
    
    public RADIUSTextInputCallback(final String s) {
        super(s);
        this.setReady(false);
    }
    
    public RADIUSTextInputCallback(final String s, final String s2) {
        super(s, s2);
        this.setReady(false);
    }
    
    public final void setAttributeType(final int a, final boolean b) {
        this.a = a;
        this.b = b;
    }
    
    public final boolean isReady() {
        return this.c;
    }
    
    public final void setReady(final boolean c) {
        this.c = c;
    }
}
