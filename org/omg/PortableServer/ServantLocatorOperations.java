package org.omg.PortableServer;

import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public interface ServantLocatorOperations extends ServantManagerOperations
{
    Servant preinvoke(final byte[] p0, final POA p1, final String p2, final CookieHolder p3) throws ForwardRequest;
    
    void postinvoke(final byte[] p0, final POA p1, final String p2, final Object p3, final Servant p4);
}
