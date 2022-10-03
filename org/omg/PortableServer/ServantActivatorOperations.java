package org.omg.PortableServer;

public interface ServantActivatorOperations extends ServantManagerOperations
{
    Servant incarnate(final byte[] p0, final POA p1) throws ForwardRequest;
    
    void etherealize(final byte[] p0, final POA p1, final Servant p2, final boolean p3, final boolean p4);
}
