package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class InvalidPolicy extends UserException
{
    public short index;
    
    public InvalidPolicy() {
        super(InvalidPolicyHelper.id());
        this.index = 0;
    }
    
    public InvalidPolicy(final short index) {
        super(InvalidPolicyHelper.id());
        this.index = 0;
        this.index = index;
    }
    
    public InvalidPolicy(final String s, final short index) {
        super(InvalidPolicyHelper.id() + "  " + s);
        this.index = 0;
        this.index = index;
    }
}
