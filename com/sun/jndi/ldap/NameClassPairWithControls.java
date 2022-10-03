package com.sun.jndi.ldap;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.NameClassPair;

class NameClassPairWithControls extends NameClassPair implements HasControls
{
    private Control[] controls;
    private static final long serialVersionUID = 2010738921219112944L;
    
    public NameClassPairWithControls(final String s, final String s2, final Control[] controls) {
        super(s, s2);
        this.controls = controls;
    }
    
    @Override
    public Control[] getControls() throws NamingException {
        return this.controls;
    }
}
