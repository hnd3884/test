package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.POAPackage.ServantNotActive;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POA;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import org.omg.PortableServer.Servant;

public abstract class POAPolicyMediatorBase_R extends POAPolicyMediatorBase
{
    protected ActiveObjectMap activeObjectMap;
    
    POAPolicyMediatorBase_R(final Policies policies, final POAImpl poaImpl) {
        super(policies, poaImpl);
        if (!policies.retainServants()) {
            throw poaImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
        }
        this.activeObjectMap = ActiveObjectMap.create(poaImpl, !this.isUnique);
    }
    
    @Override
    public void returnServant() {
    }
    
    @Override
    public void clearAOM() {
        this.activeObjectMap.clear();
        this.activeObjectMap = null;
    }
    
    protected Servant internalKeyToServant(final ActiveObjectMap.Key key) {
        final AOMEntry value = this.activeObjectMap.get(key);
        if (value == null) {
            return null;
        }
        return this.activeObjectMap.getServant(value);
    }
    
    protected Servant internalIdToServant(final byte[] array) {
        return this.internalKeyToServant(new ActiveObjectMap.Key(array));
    }
    
    protected void activateServant(final ActiveObjectMap.Key key, final AOMEntry aomEntry, final Servant servant) {
        this.setDelegate(servant, key.id);
        if (this.orb.shutdownDebugFlag) {
            System.out.println("Activating object " + servant + " with POA " + this.poa);
        }
        this.activeObjectMap.putServant(servant, aomEntry);
        if (Util.isInstanceDefined()) {
            ((POAManagerImpl)this.poa.the_POAManager()).getFactory().registerPOAForServant(this.poa, servant);
        }
    }
    
    @Override
    public final void activateObject(final byte[] array, final Servant servant) throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive {
        if (this.isUnique && this.activeObjectMap.contains(servant)) {
            throw new ServantAlreadyActive();
        }
        final ActiveObjectMap.Key key = new ActiveObjectMap.Key(array);
        final AOMEntry value = this.activeObjectMap.get(key);
        value.activateObject();
        this.activateServant(key, value, servant);
    }
    
    @Override
    public Servant deactivateObject(final byte[] array) throws ObjectNotActive, WrongPolicy {
        return this.deactivateObject(new ActiveObjectMap.Key(array));
    }
    
    protected void deactivateHelper(final ActiveObjectMap.Key key, final AOMEntry aomEntry, final Servant servant) throws ObjectNotActive, WrongPolicy {
        this.activeObjectMap.remove(key);
        if (Util.isInstanceDefined()) {
            ((POAManagerImpl)this.poa.the_POAManager()).getFactory().unregisterPOAForServant(this.poa, servant);
        }
    }
    
    public Servant deactivateObject(final ActiveObjectMap.Key key) throws ObjectNotActive, WrongPolicy {
        if (this.orb.poaDebugFlag) {
            ORBUtility.dprint(this, "Calling deactivateObject for key " + key);
        }
        try {
            final AOMEntry value = this.activeObjectMap.get(key);
            if (value == null) {
                throw new ObjectNotActive();
            }
            final Servant servant = this.activeObjectMap.getServant(value);
            if (servant == null) {
                throw new ObjectNotActive();
            }
            if (this.orb.poaDebugFlag) {
                System.out.println("Deactivating object " + servant + " with POA " + this.poa);
            }
            this.deactivateHelper(key, value, servant);
            return servant;
        }
        finally {
            if (this.orb.poaDebugFlag) {
                ORBUtility.dprint(this, "Exiting deactivateObject");
            }
        }
    }
    
    @Override
    public byte[] servantToId(final Servant servant) throws ServantNotActive, WrongPolicy {
        if (!this.isUnique && !this.isImplicit) {
            throw new WrongPolicy();
        }
        if (this.isUnique) {
            final ActiveObjectMap.Key key = this.activeObjectMap.getKey(servant);
            if (key != null) {
                return key.id;
            }
        }
        if (this.isImplicit) {
            try {
                final byte[] systemId = this.newSystemId();
                this.activateObject(systemId, servant);
                return systemId;
            }
            catch (final ObjectAlreadyActive objectAlreadyActive) {
                throw this.poa.invocationWrapper().servantToIdOaa(objectAlreadyActive);
            }
            catch (final ServantAlreadyActive servantAlreadyActive) {
                throw this.poa.invocationWrapper().servantToIdSaa(servantAlreadyActive);
            }
            catch (final WrongPolicy wrongPolicy) {
                throw this.poa.invocationWrapper().servantToIdWp(wrongPolicy);
            }
        }
        throw new ServantNotActive();
    }
}
