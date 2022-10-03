package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.portable.IDLEntity;

public final class NameValuePair implements IDLEntity
{
    public String id;
    public Any value;
    
    public NameValuePair() {
        this.id = null;
        this.value = null;
    }
    
    public NameValuePair(final String id, final Any value) {
        this.id = null;
        this.value = null;
        this.id = id;
        this.value = value;
    }
}
