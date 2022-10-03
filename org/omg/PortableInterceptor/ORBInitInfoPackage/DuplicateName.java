package org.omg.PortableInterceptor.ORBInitInfoPackage;

import org.omg.CORBA.UserException;

public final class DuplicateName extends UserException
{
    public String name;
    
    public DuplicateName() {
        super(DuplicateNameHelper.id());
        this.name = null;
    }
    
    public DuplicateName(final String name) {
        super(DuplicateNameHelper.id());
        this.name = null;
        this.name = name;
    }
    
    public DuplicateName(final String s, final String name) {
        super(DuplicateNameHelper.id() + "  " + s);
        this.name = null;
        this.name = name;
    }
}
