package com.theorem.radius3.login;

import com.theorem.radius3.AttributeList;
import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class ResponseAttributes implements Callback, Serializable
{
    public static final int Access_Accept = 2;
    public static final int Access_Challenge = 11;
    public static final int Access_Reject = 3;
    public static final int Access_BadPacket = 0;
    private boolean a;
    protected int b;
    protected AttributeList c;
    
    ResponseAttributes() {
        this.a(false);
    }
    
    protected final void a(final boolean a) {
        this.a = a;
    }
    
    public AttributeList getAttributes() {
        return this.c;
    }
    
    public final int getResponse() {
        return this.b;
    }
    
    public final boolean isValid() {
        return this.a;
    }
}
