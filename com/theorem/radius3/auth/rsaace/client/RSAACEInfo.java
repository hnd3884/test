package com.theorem.radius3.auth.rsaace.client;

import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.AttributeList;

public class RSAACEInfo
{
    public static final int STATE_CONTINUE = 2;
    public static final int STATE_COMPLETE = 3;
    public static final int STATE_ERROR = 4;
    public static final int ERROR_ALLDONE = -1;
    public static final int ERROR_ACCESS_REJECT = -2;
    public static final int ERROR_CLIENT_EXCEPTION = -3;
    protected int a;
    protected String b;
    protected int c;
    protected AttributeList d;
    private String e;
    private byte[] f;
    protected RSAACEHandler g;
    
    protected RSAACEInfo() {
        this.a = 0;
        this.b = "No Error";
    }
    
    protected final void a(final String e) {
        this.e = e;
    }
    
    public final String getDisplayMessage() {
        return this.e;
    }
    
    protected final byte[] a() {
        return this.f;
    }
    
    public final void setResponse(final byte[] f) {
        this.f = f;
    }
    
    public final void setResponse(final String s) {
        this.f = Util.toUTF8(s);
    }
    
    public final int getError() {
        return this.a;
    }
    
    public final String getErrorString() {
        return this.b;
    }
    
    public final int getState() {
        return this.c;
    }
    
    public final String getStateName() {
        String s = null;
        switch (this.c) {
            case 2: {
                s = "CONTINUE";
                break;
            }
            case 4: {
                s = "ERROR: " + this.getError() + " " + this.getErrorString();
                break;
            }
            case 3: {
                s = "COMPLETE";
                break;
            }
            case 1: {
                s = "INIT";
                break;
            }
            default: {
                s = "Unknown (" + this.c + ")";
                break;
            }
        }
        return s;
    }
    
    public final AttributeList getAttributes() {
        return this.d;
    }
    
    public final String toString() {
        final String string = "RSAACEInfo: State " + this.getStateName() + ", Handler: ";
        String s;
        if (this.g != null) {
            s = string + this.g.getClass().getName();
        }
        else {
            s = string + "none";
        }
        return s;
    }
}
