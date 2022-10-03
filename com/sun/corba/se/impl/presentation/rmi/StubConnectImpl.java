package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.portable.Delegate;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.SystemException;
import java.rmi.RemoteException;
import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.Object;
import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;

public abstract class StubConnectImpl
{
    static UtilSystemException wrapper;
    
    public static StubIORImpl connect(StubIORImpl stubIORImpl, final org.omg.CORBA.Object object, final ObjectImpl objectImpl, final ORB orb) throws RemoteException {
        try {
            try {
                if (StubAdapter.getDelegate(objectImpl).orb(objectImpl) != orb) {
                    throw StubConnectImpl.wrapper.connectWrongOrb();
                }
            }
            catch (final BAD_OPERATION bad_OPERATION) {
                Delegate delegate;
                if (stubIORImpl == null) {
                    final Tie andForgetTie = Utility.getAndForgetTie(object);
                    if (andForgetTie == null) {
                        throw StubConnectImpl.wrapper.connectNoTie();
                    }
                    ORB orb2 = orb;
                    try {
                        orb2 = andForgetTie.orb();
                    }
                    catch (final BAD_OPERATION bad_OPERATION2) {
                        andForgetTie.orb(orb);
                    }
                    catch (final BAD_INV_ORDER bad_INV_ORDER) {
                        andForgetTie.orb(orb);
                    }
                    if (orb2 != orb) {
                        throw StubConnectImpl.wrapper.connectTieWrongOrb();
                    }
                    delegate = StubAdapter.getDelegate(andForgetTie);
                    final CORBAObjectImpl corbaObjectImpl = new CORBAObjectImpl();
                    corbaObjectImpl._set_delegate(delegate);
                    stubIORImpl = new StubIORImpl(corbaObjectImpl);
                }
                else {
                    delegate = stubIORImpl.getDelegate(orb);
                }
                StubAdapter.setDelegate(objectImpl, delegate);
            }
        }
        catch (final SystemException ex) {
            throw new RemoteException("CORBA SystemException", ex);
        }
        return stubIORImpl;
    }
    
    static {
        StubConnectImpl.wrapper = UtilSystemException.get("rmiiiop");
    }
}
