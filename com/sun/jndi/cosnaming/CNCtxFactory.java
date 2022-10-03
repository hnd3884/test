package com.sun.jndi.cosnaming;

import javax.naming.NamingException;
import javax.naming.Context;
import java.util.Hashtable;
import javax.naming.spi.InitialContextFactory;

public class CNCtxFactory implements InitialContextFactory
{
    @Override
    public Context getInitialContext(final Hashtable<?, ?> hashtable) throws NamingException {
        return new CNCtx(hashtable);
    }
}
