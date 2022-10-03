package com.sun.jndi.ldap;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.Binding;

class BindingWithControls extends Binding implements HasControls
{
    private Control[] controls;
    private static final long serialVersionUID = 9117274533692320040L;
    
    public BindingWithControls(final String s, final Object o, final Control[] controls) {
        super(s, o);
        this.controls = controls;
    }
    
    @Override
    public Control[] getControls() throws NamingException {
        return this.controls;
    }
}
