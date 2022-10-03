package com.theorem.radius3.login;

import com.theorem.radius3.AttributeList;
import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class Attributes implements RADIUSCallback, Callback, Serializable
{
    public static final int Access_Accept = 2;
    public static final int Access_Challenge = 11;
    public static final int Access_Reject = 3;
    public static final int Access_BadPacket = 0;
    protected boolean a;
    protected int b;
    protected AttributeList c;
    protected AttributeList d;
    
    Attributes() {
        this.setReady(false);
    }
    
    public final AttributeList getResponseAttributes() {
        return this.c;
    }
    
    public final void setRequestAttributes(final AttributeList d) {
        this.d = d;
    }
    
    public final AttributeList getRequestAttributes() {
        return this.d;
    }
    
    public final void setResponse(final int b) {
        this.b = b;
    }
    
    public final int getResponse() {
        return this.b;
    }
    
    public final void setReady(final boolean a) {
        this.a = a;
    }
    
    public final boolean isReady() {
        return this.a;
    }
}
