package com.sun.jndi.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.directory.SearchResult;

class SearchResultWithControls extends SearchResult implements HasControls
{
    private Control[] controls;
    private static final long serialVersionUID = 8476983938747908202L;
    
    public SearchResultWithControls(final String s, final Object o, final Attributes attributes, final boolean b, final Control[] controls) {
        super(s, o, attributes, b);
        this.controls = controls;
    }
    
    @Override
    public Control[] getControls() throws NamingException {
        return this.controls;
    }
}
