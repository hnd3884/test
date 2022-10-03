package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class NameValuePair implements IDLEntity
{
    public String id;
    public Any value;
    
    public NameValuePair() {
    }
    
    public NameValuePair(final String id, final Any value) {
        this.id = id;
        this.value = value;
    }
}
