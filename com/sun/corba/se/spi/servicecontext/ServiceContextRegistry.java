package com.sun.corba.se.spi.servicecontext;

import java.util.Enumeration;
import org.omg.CORBA.BAD_PARAM;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.util.Vector;
import com.sun.corba.se.spi.orb.ORB;

public class ServiceContextRegistry
{
    private ORB orb;
    private Vector scCollection;
    
    private void dprint(final String s) {
        ORBUtility.dprint(this, s);
    }
    
    public ServiceContextRegistry(final ORB orb) {
        this.scCollection = new Vector();
        this.orb = orb;
    }
    
    public void register(final Class clazz) {
        if (ORB.ORBInitDebug) {
            this.dprint("Registering service context class " + clazz);
        }
        final ServiceContextData serviceContextData = new ServiceContextData(clazz);
        if (this.findServiceContextData(serviceContextData.getId()) == null) {
            this.scCollection.addElement(serviceContextData);
            return;
        }
        throw new BAD_PARAM("Tried to register duplicate service context");
    }
    
    public ServiceContextData findServiceContextData(final int n) {
        if (ORB.ORBInitDebug) {
            this.dprint("Searching registry for service context id " + n);
        }
        final Enumeration elements = this.scCollection.elements();
        while (elements.hasMoreElements()) {
            final ServiceContextData serviceContextData = (ServiceContextData)elements.nextElement();
            if (serviceContextData.getId() == n) {
                if (ORB.ORBInitDebug) {
                    this.dprint("Service context data found: " + serviceContextData);
                }
                return serviceContextData;
            }
        }
        if (ORB.ORBInitDebug) {
            this.dprint("Service context data not found");
        }
        return null;
    }
}
