package org.omg.PortableServer;

import org.omg.PortableServer.POAManagerPackage.State;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

public interface POAManagerOperations
{
    void activate() throws AdapterInactive;
    
    void hold_requests(final boolean p0) throws AdapterInactive;
    
    void discard_requests(final boolean p0) throws AdapterInactive;
    
    void deactivate(final boolean p0, final boolean p1) throws AdapterInactive;
    
    State get_state();
}
