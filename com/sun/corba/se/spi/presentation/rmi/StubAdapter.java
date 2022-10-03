package com.sun.corba.se.spi.presentation.rmi;

import org.omg.CORBA.portable.OutputStream;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Stub;
import org.omg.CORBA.ORB;
import javax.rmi.CORBA.Tie;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POA;
import com.sun.corba.se.impl.oa.poa.POAManagerImpl;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.Servant;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public abstract class StubAdapter
{
    private static ORBUtilSystemException wrapper;
    
    private StubAdapter() {
    }
    
    public static boolean isStubClass(final Class clazz) {
        return ObjectImpl.class.isAssignableFrom(clazz) || DynamicStub.class.isAssignableFrom(clazz);
    }
    
    public static boolean isStub(final Object o) {
        return o instanceof DynamicStub || o instanceof ObjectImpl;
    }
    
    public static void setDelegate(final Object o, final Delegate delegate) {
        if (o instanceof DynamicStub) {
            ((DynamicStub)o).setDelegate(delegate);
        }
        else {
            if (!(o instanceof ObjectImpl)) {
                throw StubAdapter.wrapper.setDelegateRequiresStub();
            }
            ((ObjectImpl)o)._set_delegate(delegate);
        }
    }
    
    public static org.omg.CORBA.Object activateServant(final Servant servant) {
        final POA default_POA = servant._default_POA();
        org.omg.CORBA.Object servant_to_reference;
        try {
            servant_to_reference = default_POA.servant_to_reference(servant);
        }
        catch (final ServantNotActive servantNotActive) {
            throw StubAdapter.wrapper.getDelegateServantNotActive(servantNotActive);
        }
        catch (final WrongPolicy wrongPolicy) {
            throw StubAdapter.wrapper.getDelegateWrongPolicy(wrongPolicy);
        }
        final POAManager the_POAManager = default_POA.the_POAManager();
        if (the_POAManager instanceof POAManagerImpl) {
            ((POAManagerImpl)the_POAManager).implicitActivation();
        }
        return servant_to_reference;
    }
    
    public static org.omg.CORBA.Object activateTie(final Tie tie) {
        if (tie instanceof ObjectImpl) {
            return tie.thisObject();
        }
        if (tie instanceof Servant) {
            return activateServant((Servant)tie);
        }
        throw StubAdapter.wrapper.badActivateTieCall();
    }
    
    public static Delegate getDelegate(final Object o) {
        if (o instanceof DynamicStub) {
            return ((DynamicStub)o).getDelegate();
        }
        if (o instanceof ObjectImpl) {
            return ((ObjectImpl)o)._get_delegate();
        }
        if (o instanceof Tie) {
            return getDelegate(activateTie((Tie)o));
        }
        throw StubAdapter.wrapper.getDelegateRequiresStub();
    }
    
    public static ORB getORB(final Object o) {
        if (o instanceof DynamicStub) {
            return ((DynamicStub)o).getORB();
        }
        if (o instanceof ObjectImpl) {
            return ((ObjectImpl)o)._orb();
        }
        throw StubAdapter.wrapper.getOrbRequiresStub();
    }
    
    public static String[] getTypeIds(final Object o) {
        if (o instanceof DynamicStub) {
            return ((DynamicStub)o).getTypeIds();
        }
        if (o instanceof ObjectImpl) {
            return ((ObjectImpl)o)._ids();
        }
        throw StubAdapter.wrapper.getTypeIdsRequiresStub();
    }
    
    public static void connect(final Object o, final ORB orb) throws RemoteException {
        if (o instanceof DynamicStub) {
            ((DynamicStub)o).connect(orb);
        }
        else if (o instanceof Stub) {
            ((Stub)o).connect(orb);
        }
        else {
            if (!(o instanceof ObjectImpl)) {
                throw StubAdapter.wrapper.connectRequiresStub();
            }
            orb.connect((org.omg.CORBA.Object)o);
        }
    }
    
    public static boolean isLocal(final Object o) {
        if (o instanceof DynamicStub) {
            return ((DynamicStub)o).isLocal();
        }
        if (o instanceof ObjectImpl) {
            return ((ObjectImpl)o)._is_local();
        }
        throw StubAdapter.wrapper.isLocalRequiresStub();
    }
    
    public static OutputStream request(final Object o, final String s, final boolean b) {
        if (o instanceof DynamicStub) {
            return ((DynamicStub)o).request(s, b);
        }
        if (o instanceof ObjectImpl) {
            return ((ObjectImpl)o)._request(s, b);
        }
        throw StubAdapter.wrapper.requestRequiresStub();
    }
    
    static {
        StubAdapter.wrapper = ORBUtilSystemException.get("rpc.presentation");
    }
}
