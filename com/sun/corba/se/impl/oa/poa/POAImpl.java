package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import java.util.Set;
import java.util.HashSet;
import org.omg.PortableServer.ForwardRequest;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.ServantManager;
import java.util.Collection;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;
import org.omg.PortableServer.AdapterActivatorOperations;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POAManager;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.TaggedProfile;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.impl.ior.POAObjectKeyTemplate;
import com.sun.corba.se.impl.orbutil.concurrent.ReentrantMutex;
import com.sun.corba.se.impl.ior.ObjectAdapterIdArray;
import java.util.HashMap;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.concurrent.SyncUtil;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import org.omg.PortableServer.AdapterActivator;
import java.util.Map;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import org.omg.PortableServer.POA;
import com.sun.corba.se.spi.oa.ObjectAdapterBase;

public class POAImpl extends ObjectAdapterBase implements POA
{
    private boolean debug;
    private static final int STATE_START = 0;
    private static final int STATE_INIT = 1;
    private static final int STATE_INIT_DONE = 2;
    private static final int STATE_RUN = 3;
    private static final int STATE_DESTROYING = 4;
    private static final int STATE_DESTROYED = 5;
    private int state;
    private POAPolicyMediator mediator;
    private int numLevels;
    private ObjectAdapterId poaId;
    private String name;
    private POAManagerImpl manager;
    private int uniquePOAId;
    private POAImpl parent;
    private Map children;
    private AdapterActivator activator;
    private int invocationCount;
    Sync poaMutex;
    private CondVar adapterActivatorCV;
    private CondVar invokeCV;
    private CondVar beingDestroyedCV;
    protected ThreadLocal isDestroying;
    
    private String stateToString() {
        switch (this.state) {
            case 0: {
                return "START";
            }
            case 1: {
                return "INIT";
            }
            case 2: {
                return "INIT_DONE";
            }
            case 3: {
                return "RUN";
            }
            case 4: {
                return "DESTROYING";
            }
            case 5: {
                return "DESTROYED";
            }
            default: {
                return "UNKNOWN(" + this.state + ")";
            }
        }
    }
    
    @Override
    public String toString() {
        return "POA[" + this.poaId.toString() + ", uniquePOAId=" + this.uniquePOAId + ", state=" + this.stateToString() + ", invocationCount=" + this.invocationCount + "]";
    }
    
    boolean getDebug() {
        return this.debug;
    }
    
    static POAFactory getPOAFactory(final ORB orb) {
        return (POAFactory)orb.getRequestDispatcherRegistry().getObjectAdapterFactory(32);
    }
    
    static POAImpl makeRootPOA(final ORB orb) {
        final POAManagerImpl poaManagerImpl = new POAManagerImpl(getPOAFactory(orb), orb.getPIHandler());
        final POAImpl poaImpl = new POAImpl("RootPOA", null, orb, 0);
        poaImpl.initialize(poaManagerImpl, Policies.rootPOAPolicies);
        return poaImpl;
    }
    
    int getPOAId() {
        return this.uniquePOAId;
    }
    
    void lock() {
        SyncUtil.acquire(this.poaMutex);
        if (this.debug) {
            ORBUtility.dprint(this, "LOCKED poa " + this);
        }
    }
    
    void unlock() {
        if (this.debug) {
            ORBUtility.dprint(this, "UNLOCKED poa " + this);
        }
        this.poaMutex.release();
    }
    
    Policies getPolicies() {
        return this.mediator.getPolicies();
    }
    
    private POAImpl(final String name, final POAImpl parent, final ORB orb, final int state) {
        super(orb);
        this.debug = orb.poaDebugFlag;
        if (this.debug) {
            ORBUtility.dprint(this, "Creating POA with name=" + name + " parent=" + parent);
        }
        this.state = state;
        this.name = name;
        this.parent = parent;
        this.children = new HashMap();
        this.activator = null;
        this.uniquePOAId = getPOAFactory(orb).newPOAId();
        if (parent == null) {
            this.numLevels = 1;
        }
        else {
            this.numLevels = parent.numLevels + 1;
            parent.children.put(name, this);
        }
        final String[] array = new String[this.numLevels];
        POAImpl parent2 = this;
        int n = this.numLevels - 1;
        while (parent2 != null) {
            array[n--] = parent2.name;
            parent2 = parent2.parent;
        }
        this.poaId = new ObjectAdapterIdArray(array);
        this.invocationCount = 0;
        this.poaMutex = new ReentrantMutex(orb.poaConcurrencyDebugFlag);
        this.adapterActivatorCV = new CondVar(this.poaMutex, orb.poaConcurrencyDebugFlag);
        this.invokeCV = new CondVar(this.poaMutex, orb.poaConcurrencyDebugFlag);
        this.beingDestroyedCV = new CondVar(this.poaMutex, orb.poaConcurrencyDebugFlag);
        this.isDestroying = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return Boolean.FALSE;
            }
        };
    }
    
    private void initialize(final POAManagerImpl manager, final Policies policies) {
        if (this.debug) {
            ORBUtility.dprint(this, "Initializing poa " + this + " with POAManager=" + manager + " policies=" + policies);
        }
        (this.manager = manager).addPOA(this);
        this.mediator = POAPolicyMediatorFactory.create(policies, this);
        final POAObjectKeyTemplate poaObjectKeyTemplate = new POAObjectKeyTemplate(this.getORB(), this.mediator.getScid(), this.mediator.getServerId(), this.getORB().getORBData().getORBId(), this.poaId);
        if (this.debug) {
            ORBUtility.dprint(this, "Initializing poa: oktemp=" + poaObjectKeyTemplate);
        }
        this.initializeTemplate(poaObjectKeyTemplate, true, policies, null, null, poaObjectKeyTemplate.getObjectAdapterId());
        if (this.state == 0) {
            this.state = 3;
        }
        else {
            if (this.state != 1) {
                throw this.lifecycleWrapper().illegalPoaStateTrans();
            }
            this.state = 2;
        }
    }
    
    private boolean waitUntilRunning() {
        if (this.debug) {
            ORBUtility.dprint(this, "Calling waitUntilRunning on poa " + this);
        }
        while (this.state < 3) {
            try {
                this.adapterActivatorCV.await();
            }
            catch (final InterruptedException ex) {}
        }
        if (this.debug) {
            ORBUtility.dprint(this, "Exiting waitUntilRunning on poa " + this);
        }
        return this.state == 3;
    }
    
    private boolean destroyIfNotInitDone() {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling destroyIfNotInitDone on poa " + this);
            }
            final boolean b = this.state == 2;
            if (b) {
                this.state = 3;
            }
            else {
                new DestroyThread(false, this.debug).doIt(this, true);
            }
            return b;
        }
        finally {
            this.adapterActivatorCV.broadcast();
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting destroyIfNotInitDone on poa " + this);
            }
            this.unlock();
        }
    }
    
    private byte[] internalReferenceToId(final org.omg.CORBA.Object object) throws WrongAdapter {
        final IOR ior = ORBUtility.getIOR(object);
        if (!IORFactories.getIORTemplateList(this.getCurrentFactory()).isEquivalent(ior.getIORTemplates())) {
            throw new WrongAdapter();
        }
        final Iterator<Object> iterator = ior.iterator();
        if (!iterator.hasNext()) {
            throw this.iorWrapper().noProfilesInIor();
        }
        return iterator.next().getObjectId().getId();
    }
    
    void etherealizeAll() {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling etheralizeAll on poa " + this);
            }
            this.mediator.etherealizeAll();
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting etheralizeAll on poa " + this);
            }
            this.unlock();
        }
    }
    
    @Override
    public POA create_POA(final String s, final POAManager poaManager, final Policy[] array) throws AdapterAlreadyExists, InvalidPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling create_POA(name=" + s + " theManager=" + poaManager + " policies=" + array + ") on poa " + this);
            }
            if (this.state > 3) {
                throw this.omgLifecycleWrapper().createPoaDestroy();
            }
            POAImpl poaImpl = this.children.get(s);
            if (poaImpl == null) {
                poaImpl = new POAImpl(s, this, this.getORB(), 0);
            }
            try {
                poaImpl.lock();
                if (this.debug) {
                    ORBUtility.dprint(this, "Calling create_POA: new poa is " + poaImpl);
                }
                if (poaImpl.state != 0 && poaImpl.state != 1) {
                    throw new AdapterAlreadyExists();
                }
                POAManager poaManager2 = poaManager;
                if (poaManager2 == null) {
                    poaManager2 = new POAManagerImpl(this.manager.getFactory(), this.manager.getPIHandler());
                }
                poaImpl.initialize((POAManagerImpl)poaManager2, new Policies(array, this.getORB().getCopierManager().getDefaultId()));
                return poaImpl;
            }
            finally {
                poaImpl.unlock();
            }
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public POA find_POA(final String s, final boolean b) throws AdapterNonExistent {
        AdapterActivatorOperations activator = null;
        this.lock();
        if (this.debug) {
            ORBUtility.dprint(this, "Calling find_POA(name=" + s + " activate=" + b + ") on poa " + this);
        }
        POAImpl poaImpl = this.children.get(s);
        if (poaImpl != null) {
            if (this.debug) {
                ORBUtility.dprint(this, "Calling find_POA: found poa " + poaImpl);
            }
            try {
                poaImpl.lock();
                this.unlock();
                if (!poaImpl.waitUntilRunning()) {
                    throw this.omgLifecycleWrapper().poaDestroyed();
                }
            }
            finally {
                poaImpl.unlock();
            }
        }
        else {
            try {
                if (this.debug) {
                    ORBUtility.dprint(this, "Calling find_POA: no poa found");
                }
                if (!b || this.activator == null) {
                    throw new AdapterNonExistent();
                }
                poaImpl = new POAImpl(s, this, this.getORB(), 1);
                if (this.debug) {
                    ORBUtility.dprint(this, "Calling find_POA: created poa " + poaImpl);
                }
                activator = this.activator;
            }
            finally {
                this.unlock();
            }
        }
        if (activator != null) {
            boolean unknown_adapter = false;
            boolean destroyIfNotInitDone = false;
            Label_0273: {
                if (!this.debug) {
                    break Label_0273;
                }
                ORBUtility.dprint(this, "Calling find_POA: calling AdapterActivator");
                try {
                    synchronized (activator) {
                        unknown_adapter = activator.unknown_adapter(this, s);
                    }
                }
                catch (final SystemException ex) {
                    throw this.omgLifecycleWrapper().adapterActivatorException(ex, s, this.poaId.toString());
                }
                catch (final Throwable t) {
                    this.lifecycleWrapper().unexpectedException(t, this.toString());
                    if (t instanceof ThreadDeath) {
                        throw (ThreadDeath)t;
                    }
                }
                finally {
                    destroyIfNotInitDone = poaImpl.destroyIfNotInitDone();
                }
            }
            if (!unknown_adapter) {
                if (this.debug) {
                    ORBUtility.dprint(this, "Calling find_POA: AdapterActivator returned false");
                }
                throw new AdapterNonExistent();
            }
            if (!destroyIfNotInitDone) {
                throw this.omgLifecycleWrapper().adapterActivatorException(s, this.poaId.toString());
            }
        }
        return poaImpl;
    }
    
    @Override
    public void destroy(final boolean b, final boolean b2) {
        if (b2 && this.getORB().isDuringDispatch()) {
            throw this.lifecycleWrapper().destroyDeadlock();
        }
        new DestroyThread(b, this.debug).doIt(this, b2);
    }
    
    @Override
    public ThreadPolicy create_thread_policy(final ThreadPolicyValue threadPolicyValue) {
        return new ThreadPolicyImpl(threadPolicyValue);
    }
    
    @Override
    public LifespanPolicy create_lifespan_policy(final LifespanPolicyValue lifespanPolicyValue) {
        return new LifespanPolicyImpl(lifespanPolicyValue);
    }
    
    @Override
    public IdUniquenessPolicy create_id_uniqueness_policy(final IdUniquenessPolicyValue idUniquenessPolicyValue) {
        return new IdUniquenessPolicyImpl(idUniquenessPolicyValue);
    }
    
    @Override
    public IdAssignmentPolicy create_id_assignment_policy(final IdAssignmentPolicyValue idAssignmentPolicyValue) {
        return new IdAssignmentPolicyImpl(idAssignmentPolicyValue);
    }
    
    @Override
    public ImplicitActivationPolicy create_implicit_activation_policy(final ImplicitActivationPolicyValue implicitActivationPolicyValue) {
        return new ImplicitActivationPolicyImpl(implicitActivationPolicyValue);
    }
    
    @Override
    public ServantRetentionPolicy create_servant_retention_policy(final ServantRetentionPolicyValue servantRetentionPolicyValue) {
        return new ServantRetentionPolicyImpl(servantRetentionPolicyValue);
    }
    
    @Override
    public RequestProcessingPolicy create_request_processing_policy(final RequestProcessingPolicyValue requestProcessingPolicyValue) {
        return new RequestProcessingPolicyImpl(requestProcessingPolicyValue);
    }
    
    @Override
    public String the_name() {
        try {
            this.lock();
            return this.name;
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public POA the_parent() {
        try {
            this.lock();
            return this.parent;
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public POA[] the_children() {
        try {
            this.lock();
            final Collection values = this.children.values();
            final POA[] array = new POA[values.size()];
            int n = 0;
            final Iterator iterator = values.iterator();
            while (iterator.hasNext()) {
                array[n++] = (POA)iterator.next();
            }
            return array;
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public POAManager the_POAManager() {
        try {
            this.lock();
            return this.manager;
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public AdapterActivator the_activator() {
        try {
            this.lock();
            return this.activator;
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public void the_activator(final AdapterActivator activator) {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling the_activator on poa " + this + " activator=" + activator);
            }
            this.activator = activator;
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public ServantManager get_servant_manager() throws WrongPolicy {
        try {
            this.lock();
            return this.mediator.getServantManager();
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public void set_servant_manager(final ServantManager servantManager) throws WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling set_servant_manager on poa " + this + " servantManager=" + servantManager);
            }
            this.mediator.setServantManager(servantManager);
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public Servant get_servant() throws NoServant, WrongPolicy {
        try {
            this.lock();
            return this.mediator.getDefaultServant();
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public void set_servant(final Servant defaultServant) throws WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling set_servant on poa " + this + " defaultServant=" + defaultServant);
            }
            this.mediator.setDefaultServant(defaultServant);
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public byte[] activate_object(final Servant servant) throws ServantAlreadyActive, WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling activate_object on poa " + this + " (servant=" + servant + ")");
            }
            final byte[] systemId = this.mediator.newSystemId();
            try {
                this.mediator.activateObject(systemId, servant);
            }
            catch (final ObjectAlreadyActive objectAlreadyActive) {}
            return systemId;
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting activate_object on poa " + this);
            }
            this.unlock();
        }
    }
    
    @Override
    public void activate_object_with_id(final byte[] array, final Servant servant) throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling activate_object_with_id on poa " + this + " (servant=" + servant + " id=" + array + ")");
            }
            this.mediator.activateObject(array.clone(), servant);
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting activate_object_with_id on poa " + this);
            }
            this.unlock();
        }
    }
    
    @Override
    public void deactivate_object(final byte[] array) throws ObjectNotActive, WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling deactivate_object on poa " + this + " (id=" + array + ")");
            }
            this.mediator.deactivateObject(array);
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting deactivate_object on poa " + this);
            }
            this.unlock();
        }
    }
    
    @Override
    public org.omg.CORBA.Object create_reference(final String s) throws WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling create_reference(repId=" + s + ") on poa " + this);
            }
            return this.makeObject(s, this.mediator.newSystemId());
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public org.omg.CORBA.Object create_reference_with_id(final byte[] array, final String s) {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling create_reference_with_id(oid=" + array + " repId=" + s + ") on poa " + this);
            }
            return this.makeObject(s, array.clone());
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public byte[] servant_to_id(final Servant servant) throws ServantNotActive, WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling servant_to_id(servant=" + servant + ") on poa " + this);
            }
            return this.mediator.servantToId(servant);
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public org.omg.CORBA.Object servant_to_reference(final Servant servant) throws ServantNotActive, WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling servant_to_reference(servant=" + servant + ") on poa " + this);
            }
            final byte[] servantToId = this.mediator.servantToId(servant);
            return this.create_reference_with_id(servantToId, servant._all_interfaces(this, servantToId)[0]);
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public Servant reference_to_servant(final org.omg.CORBA.Object object) throws ObjectNotActive, WrongPolicy, WrongAdapter {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling reference_to_servant(reference=" + object + ") on poa " + this);
            }
            if (this.state >= 4) {
                throw this.lifecycleWrapper().adapterDestroyed();
            }
            return this.mediator.idToServant(this.internalReferenceToId(object));
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public byte[] reference_to_id(final org.omg.CORBA.Object object) throws WrongAdapter, WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling reference_to_id(reference=" + object + ") on poa " + this);
            }
            if (this.state >= 4) {
                throw this.lifecycleWrapper().adapterDestroyed();
            }
            return this.internalReferenceToId(object);
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public Servant id_to_servant(final byte[] array) throws ObjectNotActive, WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling id_to_servant(id=" + array + ") on poa " + this);
            }
            if (this.state >= 4) {
                throw this.lifecycleWrapper().adapterDestroyed();
            }
            return this.mediator.idToServant(array);
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public org.omg.CORBA.Object id_to_reference(final byte[] array) throws ObjectNotActive, WrongPolicy {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling id_to_reference(id=" + array + ") on poa " + this);
            }
            if (this.state >= 4) {
                throw this.lifecycleWrapper().adapterDestroyed();
            }
            return this.makeObject(this.mediator.idToServant(array)._all_interfaces(this, array)[0], array);
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public byte[] id() {
        try {
            this.lock();
            return this.getAdapterId();
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public Policy getEffectivePolicy(final int n) {
        return this.mediator.getPolicies().get_effective_policy(n);
    }
    
    @Override
    public int getManagerId() {
        return this.manager.getManagerId();
    }
    
    @Override
    public short getState() {
        return this.manager.getORTState();
    }
    
    @Override
    public String[] getInterfaces(final Object o, final byte[] array) {
        return ((Servant)o)._all_interfaces(this, array);
    }
    
    @Override
    protected ObjectCopierFactory getObjectCopierFactory() {
        return this.getORB().getCopierManager().getObjectCopierFactory(this.mediator.getPolicies().getCopierId());
    }
    
    @Override
    public void enter() throws OADestroyed {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling enter on poa " + this);
            }
            while (this.state == 4 && this.isDestroying.get() == Boolean.FALSE) {
                try {
                    this.beingDestroyedCV.await();
                }
                catch (final InterruptedException ex) {}
            }
            if (!this.waitUntilRunning()) {
                throw new OADestroyed();
            }
            ++this.invocationCount;
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting enter on poa " + this);
            }
            this.unlock();
        }
        this.manager.enter();
    }
    
    @Override
    public void exit() {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling exit on poa " + this);
            }
            --this.invocationCount;
            if (this.invocationCount == 0 && this.state == 4) {
                this.invokeCV.broadcast();
            }
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting exit on poa " + this);
            }
            this.unlock();
        }
        this.manager.exit();
    }
    
    @Override
    public void getInvocationServant(final OAInvocationInfo oaInvocationInfo) {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling getInvocationServant on poa " + this);
            }
            Object invocationServant;
            try {
                invocationServant = this.mediator.getInvocationServant(oaInvocationInfo.id(), oaInvocationInfo.getOperation());
            }
            catch (final ForwardRequest forwardRequest) {
                throw new ForwardException(this.getORB(), forwardRequest.forward_reference);
            }
            oaInvocationInfo.setServant(invocationServant);
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting getInvocationServant on poa " + this);
            }
            this.unlock();
        }
    }
    
    @Override
    public org.omg.CORBA.Object getLocalServant(final byte[] array) {
        return null;
    }
    
    @Override
    public void returnServant() {
        try {
            this.lock();
            if (this.debug) {
                ORBUtility.dprint(this, "Calling returnServant on poa " + this);
            }
            this.mediator.returnServant();
        }
        catch (final Throwable t) {
            if (this.debug) {
                ORBUtility.dprint(this, "Exception " + t + " in returnServant on poa " + this);
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting returnServant on poa " + this);
            }
            this.unlock();
        }
    }
    
    static class DestroyThread extends Thread
    {
        private boolean wait;
        private boolean etherealize;
        private boolean debug;
        private POAImpl thePoa;
        
        public DestroyThread(final boolean etherealize, final boolean debug) {
            this.etherealize = etherealize;
            this.debug = debug;
        }
        
        public void doIt(final POAImpl thePoa, final boolean wait) {
            if (this.debug) {
                ORBUtility.dprint(this, "Calling DestroyThread.doIt(thePOA=" + thePoa + " wait=" + wait + " etherealize=" + this.etherealize);
            }
            this.thePoa = thePoa;
            this.wait = wait;
            if (wait) {
                this.run();
            }
            else {
                try {
                    this.setDaemon(true);
                }
                catch (final Exception ex) {}
                this.start();
            }
        }
        
        @Override
        public void run() {
            final HashSet set = new HashSet();
            this.performDestroy(this.thePoa, set);
            final Iterator iterator = set.iterator();
            final ObjectReferenceTemplate[] array = new ObjectReferenceTemplate[set.size()];
            int n = 0;
            while (iterator.hasNext()) {
                array[n++] = (ObjectReferenceTemplate)iterator.next();
            }
            this.thePoa.getORB().getPIHandler().adapterStateChanged(array, (short)4);
        }
        
        private boolean prepareForDestruction(final POAImpl poaImpl, final Set set) {
            POAImpl[] array = null;
            try {
                poaImpl.lock();
                if (this.debug) {
                    ORBUtility.dprint(this, "Calling performDestroy on poa " + poaImpl);
                }
                if (poaImpl.state > 3) {
                    if (this.wait) {
                        while (poaImpl.state != 5) {
                            try {
                                poaImpl.beingDestroyedCV.await();
                            }
                            catch (final InterruptedException ex) {}
                        }
                    }
                    return false;
                }
                poaImpl.state = 4;
                poaImpl.isDestroying.set(Boolean.TRUE);
                array = (POAImpl[])poaImpl.children.values().toArray(new POAImpl[0]);
            }
            finally {
                poaImpl.unlock();
            }
            for (int i = 0; i < array.length; ++i) {
                this.performDestroy(array[i], set);
            }
            return true;
        }
        
        public void performDestroy(final POAImpl poaImpl, final Set set) {
            if (!this.prepareForDestruction(poaImpl, set)) {
                return;
            }
            final POAImpl access$300 = poaImpl.parent;
            final boolean b = access$300 == null;
            try {
                if (!b) {
                    access$300.lock();
                }
                try {
                    poaImpl.lock();
                    this.completeDestruction(poaImpl, access$300, set);
                }
                finally {
                    poaImpl.unlock();
                    if (b) {
                        poaImpl.manager.getFactory().registerRootPOA();
                    }
                }
            }
            finally {
                if (!b) {
                    access$300.unlock();
                    poaImpl.parent = null;
                }
            }
        }
        
        private void completeDestruction(final POAImpl poaImpl, final POAImpl poaImpl2, final Set set) {
            if (this.debug) {
                ORBUtility.dprint(this, "Calling completeDestruction on poa " + poaImpl);
            }
            try {
                while (poaImpl.invocationCount != 0) {
                    try {
                        poaImpl.invokeCV.await();
                    }
                    catch (final InterruptedException ex) {}
                }
                if (poaImpl.mediator != null) {
                    if (this.etherealize) {
                        poaImpl.mediator.etherealizeAll();
                    }
                    poaImpl.mediator.clearAOM();
                }
                if (poaImpl.manager != null) {
                    poaImpl.manager.removePOA(poaImpl);
                }
                if (poaImpl2 != null) {
                    poaImpl2.children.remove(poaImpl.name);
                }
                set.add(poaImpl.getAdapterTemplate());
            }
            catch (final Throwable t) {
                if (t instanceof ThreadDeath) {
                    throw (ThreadDeath)t;
                }
                poaImpl.lifecycleWrapper().unexpectedException(t, poaImpl.toString());
            }
            finally {
                poaImpl.state = 5;
                poaImpl.beingDestroyedCV.broadcast();
                poaImpl.isDestroying.set(Boolean.FALSE);
                if (this.debug) {
                    ORBUtility.dprint(this, "Exiting completeDestruction on poa " + poaImpl);
                }
            }
        }
    }
}
