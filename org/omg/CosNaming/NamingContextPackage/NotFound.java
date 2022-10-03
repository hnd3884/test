package org.omg.CosNaming.NamingContextPackage;

import org.omg.CosNaming.NameComponent;
import org.omg.CORBA.UserException;

public final class NotFound extends UserException
{
    public NotFoundReason why;
    public NameComponent[] rest_of_name;
    
    public NotFound() {
        super(NotFoundHelper.id());
        this.why = null;
        this.rest_of_name = null;
    }
    
    public NotFound(final NotFoundReason why, final NameComponent[] rest_of_name) {
        super(NotFoundHelper.id());
        this.why = null;
        this.rest_of_name = null;
        this.why = why;
        this.rest_of_name = rest_of_name;
    }
    
    public NotFound(final String s, final NotFoundReason why, final NameComponent[] rest_of_name) {
        super(NotFoundHelper.id() + "  " + s);
        this.why = null;
        this.rest_of_name = null;
        this.why = why;
        this.rest_of_name = rest_of_name;
    }
}
