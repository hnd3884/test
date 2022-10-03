package com.theorem.radius3.login;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public final class ClientException extends Attributes implements Callback, Serializable
{
    private Exception a;
    private boolean b;
    
    ClientException() {
        this.b = false;
    }
    
    public final Exception getException() {
        return this.a;
    }
    
    public final void resend(final boolean b) {
        this.b = b;
    }
    
    protected final void a(final Exception a) {
        this.a = a;
    }
}
