package org.omg.CosNaming.NamingContextPackage;

import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CORBA.UserException;

public final class CannotProceed extends UserException
{
    public NamingContext cxt;
    public NameComponent[] rest_of_name;
    
    public CannotProceed() {
        super(CannotProceedHelper.id());
        this.cxt = null;
        this.rest_of_name = null;
    }
    
    public CannotProceed(final NamingContext cxt, final NameComponent[] rest_of_name) {
        super(CannotProceedHelper.id());
        this.cxt = null;
        this.rest_of_name = null;
        this.cxt = cxt;
        this.rest_of_name = rest_of_name;
    }
    
    public CannotProceed(final String s, final NamingContext cxt, final NameComponent[] rest_of_name) {
        super(CannotProceedHelper.id() + "  " + s);
        this.cxt = null;
        this.rest_of_name = null;
        this.cxt = cxt;
        this.rest_of_name = rest_of_name;
    }
}
