package org.omg.CosNaming;

import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CORBA.Object;

public interface NamingContextOperations
{
    void bind(final NameComponent[] p0, final org.omg.CORBA.Object p1) throws NotFound, CannotProceed, InvalidName, AlreadyBound;
    
    void bind_context(final NameComponent[] p0, final NamingContext p1) throws NotFound, CannotProceed, InvalidName, AlreadyBound;
    
    void rebind(final NameComponent[] p0, final org.omg.CORBA.Object p1) throws NotFound, CannotProceed, InvalidName;
    
    void rebind_context(final NameComponent[] p0, final NamingContext p1) throws NotFound, CannotProceed, InvalidName;
    
    org.omg.CORBA.Object resolve(final NameComponent[] p0) throws NotFound, CannotProceed, InvalidName;
    
    void unbind(final NameComponent[] p0) throws NotFound, CannotProceed, InvalidName;
    
    void list(final int p0, final BindingListHolder p1, final BindingIteratorHolder p2);
    
    NamingContext new_context();
    
    NamingContext bind_new_context(final NameComponent[] p0) throws NotFound, AlreadyBound, CannotProceed, InvalidName;
    
    void destroy() throws NotEmpty;
}
