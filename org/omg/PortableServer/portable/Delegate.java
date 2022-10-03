package org.omg.PortableServer.portable;

import org.omg.PortableServer.POA;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.Servant;

public interface Delegate
{
    ORB orb(final Servant p0);
    
    org.omg.CORBA.Object this_object(final Servant p0);
    
    POA poa(final Servant p0);
    
    byte[] object_id(final Servant p0);
    
    POA default_POA(final Servant p0);
    
    boolean is_a(final Servant p0, final String p1);
    
    boolean non_existent(final Servant p0);
    
    org.omg.CORBA.Object get_interface_def(final Servant p0);
}
