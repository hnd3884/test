package com.sun.corba.se.impl.protocol;

import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.protocol.ForwardException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class POALocalCRDImpl extends LocalClientRequestDispatcherBase
{
    private ORBUtilSystemException wrapper;
    private POASystemException poaWrapper;
    
    public POALocalCRDImpl(final ORB orb, final int n, final IOR ior) {
        super(orb, n, ior);
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.poaWrapper = POASystemException.get(orb, "rpc.protocol");
    }
    
    private OAInvocationInfo servantEnter(final ObjectAdapter objectAdapter) throws OADestroyed {
        objectAdapter.enter();
        final OAInvocationInfo invocationInfo = objectAdapter.makeInvocationInfo(this.objectId);
        this.orb.pushInvocationInfo(invocationInfo);
        return invocationInfo;
    }
    
    private void servantExit(final ObjectAdapter objectAdapter) {
        try {
            objectAdapter.returnServant();
        }
        finally {
            objectAdapter.exit();
            this.orb.popInvocationInfo();
        }
    }
    
    @Override
    public ServantObject servant_preinvoke(final org.omg.CORBA.Object object, final String operation, final Class clazz) {
        final ObjectAdapter find = this.oaf.find(this.oaid);
        OAInvocationInfo servantEnter;
        try {
            servantEnter = this.servantEnter(find);
            servantEnter.setOperation(operation);
        }
        catch (final OADestroyed oaDestroyed) {
            return this.servant_preinvoke(object, operation, clazz);
        }
        try {
            try {
                find.getInvocationServant(servantEnter);
                if (!this.checkForCompatibleServant(servantEnter, clazz)) {
                    return null;
                }
            }
            catch (final Throwable t) {
                this.servantExit(find);
                throw t;
            }
        }
        catch (final ForwardException ex) {
            final RuntimeException ex2 = new RuntimeException("deal with this.");
            ex2.initCause(ex);
            throw ex2;
        }
        catch (final ThreadDeath threadDeath) {
            throw this.wrapper.runtimeexception(threadDeath);
        }
        catch (final Throwable t2) {
            if (t2 instanceof SystemException) {
                throw (SystemException)t2;
            }
            throw this.poaWrapper.localServantLookup(t2);
        }
        if (!this.checkForCompatibleServant(servantEnter, clazz)) {
            this.servantExit(find);
            return null;
        }
        return servantEnter;
    }
    
    @Override
    public void servant_postinvoke(final org.omg.CORBA.Object object, final ServantObject servantObject) {
        this.servantExit(this.orb.peekInvocationInfo().oa());
    }
}
