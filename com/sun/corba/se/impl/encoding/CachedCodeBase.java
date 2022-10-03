package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.Repository;
import java.util.Iterator;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.org.omg.SendingContext.CodeBase;
import java.util.Hashtable;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;

public class CachedCodeBase extends _CodeBaseImplBase
{
    private Hashtable implementations;
    private Hashtable fvds;
    private Hashtable bases;
    private volatile CodeBase delegate;
    private CorbaConnection conn;
    private static Object iorMapLock;
    private static Hashtable<IOR, CodeBase> iorMap;
    
    public static synchronized void cleanCache(final ORB orb) {
        synchronized (CachedCodeBase.iorMapLock) {
            for (final IOR ior : CachedCodeBase.iorMap.keySet()) {
                if (ior.getORB() == orb) {
                    CachedCodeBase.iorMap.remove(ior);
                }
            }
        }
    }
    
    public CachedCodeBase(final CorbaConnection conn) {
        this.conn = conn;
    }
    
    @Override
    public Repository get_ir() {
        return null;
    }
    
    @Override
    public synchronized String implementation(final String s) {
        String implementation = null;
        if (this.implementations == null) {
            this.implementations = new Hashtable();
        }
        else {
            implementation = this.implementations.get(s);
        }
        if (implementation == null && this.connectedCodeBase()) {
            implementation = this.delegate.implementation(s);
            if (implementation != null) {
                this.implementations.put(s, implementation);
            }
        }
        return implementation;
    }
    
    @Override
    public synchronized String[] implementations(final String[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = this.implementation(array[i]);
        }
        return array2;
    }
    
    @Override
    public synchronized FullValueDescription meta(final String s) {
        FullValueDescription meta = null;
        if (this.fvds == null) {
            this.fvds = new Hashtable();
        }
        else {
            meta = this.fvds.get(s);
        }
        if (meta == null && this.connectedCodeBase()) {
            meta = this.delegate.meta(s);
            if (meta != null) {
                this.fvds.put(s, meta);
            }
        }
        return meta;
    }
    
    @Override
    public synchronized FullValueDescription[] metas(final String[] array) {
        final FullValueDescription[] array2 = new FullValueDescription[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = this.meta(array[i]);
        }
        return array2;
    }
    
    @Override
    public synchronized String[] bases(final String s) {
        String[] bases = null;
        if (this.bases == null) {
            this.bases = new Hashtable();
        }
        else {
            bases = this.bases.get(s);
        }
        if (bases == null && this.connectedCodeBase()) {
            bases = this.delegate.bases(s);
            if (bases != null) {
                this.bases.put(s, bases);
            }
        }
        return bases;
    }
    
    private synchronized boolean connectedCodeBase() {
        if (this.delegate != null) {
            return true;
        }
        if (this.conn.getCodeBaseIOR() == null) {
            if (this.conn.getBroker().transportDebugFlag) {
                this.conn.dprint("CodeBase unavailable on connection: " + this.conn);
            }
            return false;
        }
        synchronized (CachedCodeBase.iorMapLock) {
            if (this.delegate != null) {
                return true;
            }
            this.delegate = CachedCodeBase.iorMap.get(this.conn.getCodeBaseIOR());
            if (this.delegate != null) {
                return true;
            }
            this.delegate = CodeBaseHelper.narrow(this.getObjectFromIOR());
            CachedCodeBase.iorMap.put(this.conn.getCodeBaseIOR(), this.delegate);
        }
        return true;
    }
    
    private final org.omg.CORBA.Object getObjectFromIOR() {
        return CDRInputStream_1_0.internalIORToObject(this.conn.getCodeBaseIOR(), null, this.conn.getBroker());
    }
    
    static {
        CachedCodeBase.iorMapLock = new Object();
        CachedCodeBase.iorMap = new Hashtable<IOR, CodeBase>();
    }
}
