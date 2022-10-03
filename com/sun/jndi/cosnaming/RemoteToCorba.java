package com.sun.jndi.cosnaming;

import javax.naming.NamingException;
import javax.naming.ConfigurationException;
import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.rmi.Remote;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.StateFactory;

public class RemoteToCorba implements StateFactory
{
    @Override
    public Object getStateToBind(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        if (o instanceof org.omg.CORBA.Object) {
            return null;
        }
        if (o instanceof Remote) {
            try {
                return CorbaUtils.remoteToCorba((Remote)o, ((CNCtx)context)._orb);
            }
            catch (final ClassNotFoundException ex) {
                throw new ConfigurationException("javax.rmi packages not available");
            }
        }
        return null;
    }
}
