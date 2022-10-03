package org.omg.CosNaming;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

public interface NamingContextExtOperations extends NamingContextOperations
{
    String to_string(final NameComponent[] p0) throws InvalidName;
    
    NameComponent[] to_name(final String p0) throws InvalidName;
    
    String to_url(final String p0, final String p1) throws InvalidAddress, InvalidName;
    
    org.omg.CORBA.Object resolve_str(final String p0) throws NotFound, CannotProceed, InvalidName;
}
