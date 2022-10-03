package com.theorem.radius3.login;

import com.theorem.radius3.AttributeList;
import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class SetRequestAttributes extends ResponseAttributes implements Callback, Serializable
{
    private AttributeList a;
    
    SetRequestAttributes() {
    }
    
    public final void setAttributes(final AttributeList a) {
        this.a = a;
    }
    
    public final AttributeList getAttributes() {
        return this.a;
    }
}
