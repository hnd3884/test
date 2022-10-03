package org.omg.PortableServer;

import org.omg.CORBA.Object;
import org.omg.CORBA.UserException;

public final class ForwardRequest extends UserException
{
    public org.omg.CORBA.Object forward_reference;
    
    public ForwardRequest() {
        super(ForwardRequestHelper.id());
        this.forward_reference = null;
    }
    
    public ForwardRequest(final org.omg.CORBA.Object forward_reference) {
        super(ForwardRequestHelper.id());
        this.forward_reference = null;
        this.forward_reference = forward_reference;
    }
    
    public ForwardRequest(final String s, final org.omg.CORBA.Object forward_reference) {
        super(ForwardRequestHelper.id() + "  " + s);
        this.forward_reference = null;
        this.forward_reference = forward_reference;
    }
}
