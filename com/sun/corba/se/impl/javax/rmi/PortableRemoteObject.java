package com.sun.corba.se.impl.javax.rmi;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.util.RepositoryId;
import java.io.Externalizable;
import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.server.RemoteObject;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.rmi.server.RemoteStub;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import java.rmi.server.UnicastRemoteObject;
import com.sun.corba.se.impl.util.Utility;
import java.rmi.server.ExportException;
import javax.rmi.CORBA.Util;
import java.rmi.Remote;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;

public class PortableRemoteObject implements PortableRemoteObjectDelegate
{
    @Override
    public void exportObject(final Remote remote) throws RemoteException {
        if (remote == null) {
            throw new NullPointerException("invalid argument");
        }
        if (Util.getTie(remote) != null) {
            throw new ExportException(remote.getClass().getName() + " already exported");
        }
        final Tie loadTie = Utility.loadTie(remote);
        if (loadTie != null) {
            Util.registerTarget(loadTie, remote);
        }
        else {
            UnicastRemoteObject.exportObject(remote);
        }
    }
    
    @Override
    public Remote toStub(final Remote remote) throws NoSuchObjectException {
        Remote remote2 = null;
        if (remote == null) {
            throw new NullPointerException("invalid argument");
        }
        if (StubAdapter.isStub(remote)) {
            return remote;
        }
        if (remote instanceof RemoteStub) {
            return remote;
        }
        final Tie tie = Util.getTie(remote);
        if (tie != null) {
            remote2 = Utility.loadStub(tie, null, null, true);
        }
        else if (Utility.loadTie(remote) == null) {
            remote2 = RemoteObject.toStub(remote);
        }
        if (remote2 == null) {
            throw new NoSuchObjectException("object not exported");
        }
        return remote2;
    }
    
    @Override
    public void unexportObject(final Remote remote) throws NoSuchObjectException {
        if (remote == null) {
            throw new NullPointerException("invalid argument");
        }
        if (StubAdapter.isStub(remote) || remote instanceof RemoteStub) {
            throw new NoSuchObjectException("Can only unexport a server object.");
        }
        if (Util.getTie(remote) != null) {
            Util.unexportObject(remote);
        }
        else {
            if (Utility.loadTie(remote) != null) {
                throw new NoSuchObjectException("Object not exported.");
            }
            UnicastRemoteObject.unexportObject(remote, true);
        }
    }
    
    @Override
    public Object narrow(final Object o, final Class clazz) throws ClassCastException {
        if (o == null) {
            return null;
        }
        if (clazz == null) {
            throw new NullPointerException("invalid argument");
        }
        try {
            if (clazz.isAssignableFrom(o.getClass())) {
                return o;
            }
            if (!clazz.isInterface() || clazz == Serializable.class || clazz == Externalizable.class) {
                throw new ClassCastException("Class " + clazz.getName() + " is not a valid remote interface");
            }
            final org.omg.CORBA.Object object = (org.omg.CORBA.Object)o;
            if (object._is_a(RepositoryId.createForAnyType(clazz))) {
                return Utility.loadStub(object, clazz);
            }
            throw new ClassCastException("Object is not of remote type " + clazz.getName());
        }
        catch (final Exception ex) {
            final ClassCastException ex2 = new ClassCastException();
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public void connect(final Remote remote, final Remote remote2) throws RemoteException {
        if (remote == null || remote2 == null) {
            throw new NullPointerException("invalid argument");
        }
        ORB orb = null;
        try {
            if (StubAdapter.isStub(remote2)) {
                orb = StubAdapter.getORB(remote2);
            }
            else {
                final Tie tie = Util.getTie(remote2);
                if (tie != null) {
                    orb = tie.orb();
                }
            }
        }
        catch (final SystemException ex) {
            throw new RemoteException("'source' object not connected", ex);
        }
        boolean b = false;
        Tie tie2 = null;
        if (StubAdapter.isStub(remote)) {
            b = true;
        }
        else {
            tie2 = Util.getTie(remote);
            if (tie2 != null) {
                b = true;
            }
        }
        if (!b) {
            if (orb != null) {
                throw new RemoteException("'source' object exported to IIOP, 'target' is JRMP");
            }
        }
        else {
            if (orb == null) {
                throw new RemoteException("'source' object is JRMP, 'target' is IIOP");
            }
            try {
                if (tie2 != null) {
                    try {
                        if (tie2.orb() == orb) {
                            return;
                        }
                        throw new RemoteException("'target' object was already connected");
                    }
                    catch (final SystemException ex2) {
                        tie2.orb(orb);
                        return;
                    }
                }
                StubAdapter.connect(remote, orb);
            }
            catch (final SystemException ex3) {
                throw new RemoteException("'target' object was already connected", ex3);
            }
        }
    }
}
