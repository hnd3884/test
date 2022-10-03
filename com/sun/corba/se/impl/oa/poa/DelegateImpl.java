package com.sun.corba.se.impl.oa.poa;

import java.util.EmptyStackException;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POA;
import org.omg.CORBA.Object;
import org.omg.PortableServer.Servant;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableServer.portable.Delegate;

public class DelegateImpl implements Delegate
{
    private ORB orb;
    private POASystemException wrapper;
    private POAFactory factory;
    
    public DelegateImpl(final ORB orb, final POAFactory factory) {
        this.orb = orb;
        this.wrapper = POASystemException.get(orb, "oa");
        this.factory = factory;
    }
    
    @Override
    public org.omg.CORBA.ORB orb(final Servant servant) {
        return this.orb;
    }
    
    @Override
    public org.omg.CORBA.Object this_object(final Servant servant) {
        try {
            final byte[] id = this.orb.peekInvocationInfo().id();
            final POA poa = (POA)this.orb.peekInvocationInfo().oa();
            return poa.create_reference_with_id(id, servant._all_interfaces(poa, id)[0]);
        }
        catch (final EmptyStackException ex) {
            POAImpl poaImpl;
            try {
                poaImpl = (POAImpl)servant._default_POA();
            }
            catch (final ClassCastException ex2) {
                throw this.wrapper.defaultPoaNotPoaimpl(ex2);
            }
            try {
                if (poaImpl.getPolicies().isImplicitlyActivated() || (poaImpl.getPolicies().isUniqueIds() && poaImpl.getPolicies().retainServants())) {
                    return poaImpl.servant_to_reference(servant);
                }
                throw this.wrapper.wrongPoliciesForThisObject();
            }
            catch (final ServantNotActive servantNotActive) {
                throw this.wrapper.thisObjectServantNotActive(servantNotActive);
            }
            catch (final WrongPolicy wrongPolicy) {
                throw this.wrapper.thisObjectWrongPolicy(wrongPolicy);
            }
        }
        catch (final ClassCastException ex3) {
            throw this.wrapper.defaultPoaNotPoaimpl(ex3);
        }
    }
    
    @Override
    public POA poa(final Servant servant) {
        try {
            return (POA)this.orb.peekInvocationInfo().oa();
        }
        catch (final EmptyStackException ex) {
            final POA lookupPOA = this.factory.lookupPOA(servant);
            if (lookupPOA != null) {
                return lookupPOA;
            }
            throw this.wrapper.noContext(ex);
        }
    }
    
    @Override
    public byte[] object_id(final Servant servant) {
        try {
            return this.orb.peekInvocationInfo().id();
        }
        catch (final EmptyStackException ex) {
            throw this.wrapper.noContext(ex);
        }
    }
    
    @Override
    public POA default_POA(final Servant servant) {
        return this.factory.getRootPOA();
    }
    
    @Override
    public boolean is_a(final Servant servant, final String s) {
        final String[] all_interfaces = servant._all_interfaces(this.poa(servant), this.object_id(servant));
        for (int i = 0; i < all_interfaces.length; ++i) {
            if (s.equals(all_interfaces[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean non_existent(final Servant servant) {
        try {
            return this.orb.peekInvocationInfo().id() == null;
        }
        catch (final EmptyStackException ex) {
            throw this.wrapper.noContext(ex);
        }
    }
    
    @Override
    public org.omg.CORBA.Object get_interface_def(final Servant servant) {
        throw this.wrapper.methodNotImplemented();
    }
}
