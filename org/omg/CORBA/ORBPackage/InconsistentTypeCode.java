package org.omg.CORBA.ORBPackage;

import org.omg.CORBA.UserException;

public final class InconsistentTypeCode extends UserException
{
    public InconsistentTypeCode() {
    }
    
    public InconsistentTypeCode(final String s) {
        super(s);
    }
}
