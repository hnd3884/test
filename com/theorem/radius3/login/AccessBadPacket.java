package com.theorem.radius3.login;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public final class AccessBadPacket extends Attributes implements Callback, Serializable
{
    private String a;
    private int b;
    
    AccessBadPacket() {
    }
    
    protected final void a(final String a, final int b) {
        this.a = a;
        this.b = b;
    }
    
    public final String errorString() {
        return this.a;
    }
    
    public final int errorNumber() {
        return this.b;
    }
}
