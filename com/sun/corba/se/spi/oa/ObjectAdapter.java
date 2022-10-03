package com.sun.corba.se.spi.oa;

import org.omg.CORBA.Object;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import com.sun.corba.se.spi.ior.IORTemplate;
import org.omg.CORBA.Policy;
import com.sun.corba.se.spi.orb.ORB;

public interface ObjectAdapter
{
    ORB getORB();
    
    Policy getEffectivePolicy(final int p0);
    
    IORTemplate getIORTemplate();
    
    int getManagerId();
    
    short getState();
    
    ObjectReferenceTemplate getAdapterTemplate();
    
    ObjectReferenceFactory getCurrentFactory();
    
    void setCurrentFactory(final ObjectReferenceFactory p0);
    
    org.omg.CORBA.Object getLocalServant(final byte[] p0);
    
    void getInvocationServant(final OAInvocationInfo p0);
    
    void enter() throws OADestroyed;
    
    void exit();
    
    void returnServant();
    
    OAInvocationInfo makeInvocationInfo(final byte[] p0);
    
    String[] getInterfaces(final Object p0, final byte[] p1);
}
