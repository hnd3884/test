package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.PortableServer.POA;
import java.util.Hashtable;
import java.io.File;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableServer.ServantLocator;
import org.omg.CORBA.LocalObject;

public class ServantManagerImpl extends LocalObject implements ServantLocator
{
    private static final long serialVersionUID = 4028710359865748280L;
    private ORB orb;
    private NameService theNameService;
    private File logDir;
    private Hashtable contexts;
    private CounterDB counterDb;
    private int counter;
    private static final String objKeyPrefix = "NC";
    
    ServantManagerImpl(final ORB orb, final File logDir, final NameService theNameService) {
        this.logDir = logDir;
        this.orb = orb;
        this.counterDb = new CounterDB(logDir);
        this.contexts = new Hashtable();
        this.theNameService = theNameService;
    }
    
    @Override
    public Servant preinvoke(final byte[] array, final POA poa, final String s, final CookieHolder cookieHolder) throws ForwardRequest {
        final String s2 = new String(array);
        Servant inContext = this.contexts.get(s2);
        if (inContext == null) {
            inContext = this.readInContext(s2);
        }
        return inContext;
    }
    
    @Override
    public void postinvoke(final byte[] array, final POA poa, final String s, final Object o, final Servant servant) {
    }
    
    public NamingContextImpl readInContext(final String s) {
        NamingContextImpl namingContextImpl = this.contexts.get(s);
        if (namingContextImpl != null) {
            return namingContextImpl;
        }
        final File file = new File(this.logDir, s);
        if (file.exists()) {
            try {
                final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                namingContextImpl = (NamingContextImpl)objectInputStream.readObject();
                namingContextImpl.setORB(this.orb);
                namingContextImpl.setServantManagerImpl(this);
                namingContextImpl.setRootNameService(this.theNameService);
                objectInputStream.close();
            }
            catch (final Exception ex) {}
        }
        if (namingContextImpl != null) {
            this.contexts.put(s, namingContextImpl);
        }
        return namingContextImpl;
    }
    
    public NamingContextImpl addContext(final String s, NamingContextImpl inContext) {
        final File file = new File(this.logDir, s);
        if (file.exists()) {
            inContext = this.readInContext(s);
        }
        else {
            try {
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                objectOutputStream.writeObject(inContext);
                objectOutputStream.close();
            }
            catch (final Exception ex) {}
        }
        try {
            this.contexts.remove(s);
        }
        catch (final Exception ex2) {}
        this.contexts.put(s, inContext);
        return inContext;
    }
    
    public void updateContext(final String s, final NamingContextImpl namingContextImpl) {
        File file = new File(this.logDir, s);
        if (file.exists()) {
            file.delete();
            file = new File(this.logDir, s);
        }
        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(namingContextImpl);
            objectOutputStream.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static String getRootObjectKey() {
        return "NC0";
    }
    
    public String getNewObjectKey() {
        return "NC" + this.counterDb.getNextCounter();
    }
}
