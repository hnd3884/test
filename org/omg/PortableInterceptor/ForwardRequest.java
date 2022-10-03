package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.CORBA.UserException;

public final class ForwardRequest extends UserException
{
    public org.omg.CORBA.Object forward;
    
    public ForwardRequest() {
        super(ForwardRequestHelper.id());
        this.forward = null;
    }
    
    public ForwardRequest(final org.omg.CORBA.Object forward) {
        super(ForwardRequestHelper.id());
        this.forward = null;
        this.forward = forward;
    }
    
    public ForwardRequest(final String s, final org.omg.CORBA.Object forward) {
        super(ForwardRequestHelper.id() + "  " + s);
        this.forward = null;
        this.forward = forward;
    }
}
