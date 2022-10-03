package org.omg.stub.java.rmi;

import java.rmi.Remote;
import javax.rmi.CORBA.Stub;

public final class _Remote_Stub extends Stub implements Remote
{
    private static final String[] _type_ids;
    
    @Override
    public String[] _ids() {
        return _Remote_Stub._type_ids.clone();
    }
    
    static {
        _type_ids = new String[] { "" };
    }
}
