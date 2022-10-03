package org.omg.DynamicAny.DynAnyFactoryPackage;

import org.omg.CORBA.UserException;

public final class InconsistentTypeCode extends UserException
{
    public InconsistentTypeCode() {
        super(InconsistentTypeCodeHelper.id());
    }
    
    public InconsistentTypeCode(final String s) {
        super(InconsistentTypeCodeHelper.id() + "  " + s);
    }
}
