package java.beans.beancontext;

import java.util.HashSet;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Map;
import java.io.ObjectOutputStream;
import java.util.TooManyListenersException;
import java.util.Iterator;
import java.io.Serializable;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;

public class BeanContextServicesSupport extends BeanContextSupport implements BeanContextServices
{
    private static final long serialVersionUID = -8494482757288719206L;
    protected transient HashMap services;
    protected transient int serializable;
    protected transient BCSSProxyServiceProvider proxy;
    protected transient ArrayList bcsListeners;
    
    public BeanContextServicesSupport(final BeanContextServices beanContextServices, final Locale locale, final boolean b, final boolean b2) {
        super(beanContextServices, locale, b, b2);
        this.serializable = 0;
    }
    
    public BeanContextServicesSupport(final BeanContextServices beanContextServices, final Locale locale, final boolean b) {
        this(beanContextServices, locale, b, true);
    }
    
    public BeanContextServicesSupport(final BeanContextServices beanContextServices, final Locale locale) {
        this(beanContextServices, locale, false, true);
    }
    
    public BeanContextServicesSupport(final BeanContextServices beanContextServices) {
        this(beanContextServices, null, false, true);
    }
    
    public BeanContextServicesSupport() {
        this(null, null, false, true);
    }
    
    public void initialize() {
        super.initialize();
        this.services = new HashMap(this.serializable + 1);
        this.bcsListeners = new ArrayList(1);
    }
    
    public BeanContextServices getBeanContextServicesPeer() {
        return (BeanContextServices)this.getBeanContextChildPeer();
    }
    
    @Override
    protected BCSChild createBCSChild(final Object o, final Object o2) {
        return new BCSSChild(o, o2);
    }
    
    protected BCSSServiceProvider createBCSSServiceProvider(final Class clazz, final BeanContextServiceProvider beanContextServiceProvider) {
        return new BCSSServiceProvider(clazz, beanContextServiceProvider);
    }
    
    @Override
    public void addBeanContextServicesListener(final BeanContextServicesListener beanContextServicesListener) {
        if (beanContextServicesListener == null) {
            throw new NullPointerException("bcsl");
        }
        synchronized (this.bcsListeners) {
            if (this.bcsListeners.contains(beanContextServicesListener)) {
                return;
            }
            this.bcsListeners.add(beanContextServicesListener);
        }
    }
    
    @Override
    public void removeBeanContextServicesListener(final BeanContextServicesListener beanContextServicesListener) {
        if (beanContextServicesListener == null) {
            throw new NullPointerException("bcsl");
        }
        synchronized (this.bcsListeners) {
            if (!this.bcsListeners.contains(beanContextServicesListener)) {
                return;
            }
            this.bcsListeners.remove(beanContextServicesListener);
        }
    }
    
    @Override
    public boolean addService(final Class clazz, final BeanContextServiceProvider beanContextServiceProvider) {
        return this.addService(clazz, beanContextServiceProvider, true);
    }
    
    protected boolean addService(final Class clazz, final BeanContextServiceProvider beanContextServiceProvider, final boolean b) {
        if (clazz == null) {
            throw new NullPointerException("serviceClass");
        }
        if (beanContextServiceProvider == null) {
            throw new NullPointerException("bcsp");
        }
        synchronized (BeanContext.globalHierarchyLock) {
            if (this.services.containsKey(clazz)) {
                return false;
            }
            this.services.put(clazz, this.createBCSSServiceProvider(clazz, beanContextServiceProvider));
            if (beanContextServiceProvider instanceof Serializable) {
                ++this.serializable;
            }
            if (!b) {
                return true;
            }
            final BeanContextServiceAvailableEvent beanContextServiceAvailableEvent = new BeanContextServiceAvailableEvent(this.getBeanContextServicesPeer(), clazz);
            this.fireServiceAdded(beanContextServiceAvailableEvent);
            synchronized (this.children) {
                for (final Object next : this.children.keySet()) {
                    if (next instanceof BeanContextServices) {
                        ((BeanContextServicesListener)next).serviceAvailable(beanContextServiceAvailableEvent);
                    }
                }
            }
            return true;
        }
    }
    
    @Override
    public void revokeService(final Class clazz, final BeanContextServiceProvider beanContextServiceProvider, final boolean b) {
        if (clazz == null) {
            throw new NullPointerException("serviceClass");
        }
        if (beanContextServiceProvider == null) {
            throw new NullPointerException("bcsp");
        }
        synchronized (BeanContext.globalHierarchyLock) {
            if (!this.services.containsKey(clazz)) {
                return;
            }
            if (!this.services.get(clazz).getServiceProvider().equals(beanContextServiceProvider)) {
                throw new IllegalArgumentException("service provider mismatch");
            }
            this.services.remove(clazz);
            if (beanContextServiceProvider instanceof Serializable) {
                --this.serializable;
            }
            final Iterator bcsChildren = this.bcsChildren();
            while (bcsChildren.hasNext()) {
                ((BCSSChild)bcsChildren.next()).revokeService(clazz, false, b);
            }
            this.fireServiceRevoked(clazz, b);
        }
    }
    
    @Override
    public synchronized boolean hasService(final Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("serviceClass");
        }
        synchronized (BeanContext.globalHierarchyLock) {
            if (this.services.containsKey(clazz)) {
                return true;
            }
            BeanContextServices beanContextServices;
            try {
                beanContextServices = (BeanContextServices)this.getBeanContext();
            }
            catch (final ClassCastException ex) {
                return false;
            }
            return beanContextServices != null && beanContextServices.hasService(clazz);
        }
    }
    
    @Override
    public Object getService(final BeanContextChild beanContextChild, final Object o, final Class clazz, final Object o2, final BeanContextServiceRevokedListener beanContextServiceRevokedListener) throws TooManyListenersException {
        if (beanContextChild == null) {
            throw new NullPointerException("child");
        }
        if (clazz == null) {
            throw new NullPointerException("serviceClass");
        }
        if (o == null) {
            throw new NullPointerException("requestor");
        }
        if (beanContextServiceRevokedListener == null) {
            throw new NullPointerException("bcsrl");
        }
        final BeanContextServices beanContextServicesPeer = this.getBeanContextServicesPeer();
        synchronized (BeanContext.globalHierarchyLock) {
            final BCSSChild bcssChild;
            synchronized (this.children) {
                bcssChild = this.children.get(beanContextChild);
            }
            if (bcssChild == null) {
                throw new IllegalArgumentException("not a child of this context");
            }
            final BCSSServiceProvider bcssServiceProvider = this.services.get(clazz);
            if (bcssServiceProvider != null) {
                final BeanContextServiceProvider serviceProvider = bcssServiceProvider.getServiceProvider();
                final Object service = serviceProvider.getService(beanContextServicesPeer, o, clazz, o2);
                if (service != null) {
                    try {
                        bcssChild.usingService(o, service, clazz, serviceProvider, false, beanContextServiceRevokedListener);
                    }
                    catch (final TooManyListenersException ex) {
                        serviceProvider.releaseService(beanContextServicesPeer, o, service);
                        throw ex;
                    }
                    catch (final UnsupportedOperationException ex2) {
                        serviceProvider.releaseService(beanContextServicesPeer, o, service);
                        throw ex2;
                    }
                    return service;
                }
            }
            if (this.proxy != null) {
                final Object service2 = this.proxy.getService(beanContextServicesPeer, o, clazz, o2);
                if (service2 != null) {
                    try {
                        bcssChild.usingService(o, service2, clazz, this.proxy, true, beanContextServiceRevokedListener);
                    }
                    catch (final TooManyListenersException ex3) {
                        this.proxy.releaseService(beanContextServicesPeer, o, service2);
                        throw ex3;
                    }
                    catch (final UnsupportedOperationException ex4) {
                        this.proxy.releaseService(beanContextServicesPeer, o, service2);
                        throw ex4;
                    }
                    return service2;
                }
            }
        }
        return null;
    }
    
    @Override
    public void releaseService(final BeanContextChild beanContextChild, final Object o, final Object o2) {
        if (beanContextChild == null) {
            throw new NullPointerException("child");
        }
        if (o == null) {
            throw new NullPointerException("requestor");
        }
        if (o2 == null) {
            throw new NullPointerException("service");
        }
        synchronized (BeanContext.globalHierarchyLock) {
            final BCSSChild bcssChild;
            synchronized (this.children) {
                bcssChild = this.children.get(beanContextChild);
            }
            if (bcssChild == null) {
                throw new IllegalArgumentException("child actual is not a child of this BeanContext");
            }
            bcssChild.releaseService(o, o2);
        }
    }
    
    @Override
    public Iterator getCurrentServiceClasses() {
        return new BCSIterator(this.services.keySet().iterator());
    }
    
    @Override
    public Iterator getCurrentServiceSelectors(final Class clazz) {
        final BCSSServiceProvider bcssServiceProvider = this.services.get(clazz);
        return (bcssServiceProvider != null) ? new BCSIterator(bcssServiceProvider.getServiceProvider().getCurrentServiceSelectors(this.getBeanContextServicesPeer(), clazz)) : null;
    }
    
    @Override
    public void serviceAvailable(final BeanContextServiceAvailableEvent beanContextServiceAvailableEvent) {
        synchronized (BeanContext.globalHierarchyLock) {
            if (this.services.containsKey(beanContextServiceAvailableEvent.getServiceClass())) {
                return;
            }
            this.fireServiceAdded(beanContextServiceAvailableEvent);
            final Iterator iterator;
            synchronized (this.children) {
                iterator = this.children.keySet().iterator();
            }
            while (iterator.hasNext()) {
                final Object next = iterator.next();
                if (next instanceof BeanContextServices) {
                    ((BeanContextServicesListener)next).serviceAvailable(beanContextServiceAvailableEvent);
                }
            }
        }
    }
    
    @Override
    public void serviceRevoked(final BeanContextServiceRevokedEvent beanContextServiceRevokedEvent) {
        synchronized (BeanContext.globalHierarchyLock) {
            if (this.services.containsKey(beanContextServiceRevokedEvent.getServiceClass())) {
                return;
            }
            this.fireServiceRevoked(beanContextServiceRevokedEvent);
            final Iterator iterator;
            synchronized (this.children) {
                iterator = this.children.keySet().iterator();
            }
            while (iterator.hasNext()) {
                final Object next = iterator.next();
                if (next instanceof BeanContextServices) {
                    ((BeanContextServicesListener)next).serviceRevoked(beanContextServiceRevokedEvent);
                }
            }
        }
    }
    
    protected static final BeanContextServicesListener getChildBeanContextServicesListener(final Object o) {
        try {
            return (BeanContextServicesListener)o;
        }
        catch (final ClassCastException ex) {
            return null;
        }
    }
    
    @Override
    protected void childJustRemovedHook(final Object o, final BCSChild bcsChild) {
        ((BCSSChild)bcsChild).cleanupReferences();
    }
    
    @Override
    protected synchronized void releaseBeanContextResources() {
        super.releaseBeanContextResources();
        final Object[] array;
        synchronized (this.children) {
            if (this.children.isEmpty()) {
                return;
            }
            array = this.children.values().toArray();
        }
        for (int i = 0; i < array.length; ++i) {
            ((BCSSChild)array[i]).revokeAllDelegatedServicesNow();
        }
        this.proxy = null;
    }
    
    @Override
    protected synchronized void initializeBeanContextResources() {
        super.initializeBeanContextResources();
        final BeanContext beanContext = this.getBeanContext();
        if (beanContext == null) {
            return;
        }
        try {
            this.proxy = new BCSSProxyServiceProvider((BeanContextServices)beanContext);
        }
        catch (final ClassCastException ex) {}
    }
    
    protected final void fireServiceAdded(final Class clazz) {
        this.fireServiceAdded(new BeanContextServiceAvailableEvent(this.getBeanContextServicesPeer(), clazz));
    }
    
    protected final void fireServiceAdded(final BeanContextServiceAvailableEvent beanContextServiceAvailableEvent) {
        final Object[] array;
        synchronized (this.bcsListeners) {
            array = this.bcsListeners.toArray();
        }
        for (int i = 0; i < array.length; ++i) {
            ((BeanContextServicesListener)array[i]).serviceAvailable(beanContextServiceAvailableEvent);
        }
    }
    
    protected final void fireServiceRevoked(final BeanContextServiceRevokedEvent beanContextServiceRevokedEvent) {
        final Object[] array;
        synchronized (this.bcsListeners) {
            array = this.bcsListeners.toArray();
        }
        for (int i = 0; i < array.length; ++i) {
            ((BeanContextServiceRevokedListener)array[i]).serviceRevoked(beanContextServiceRevokedEvent);
        }
    }
    
    protected final void fireServiceRevoked(final Class clazz, final boolean b) {
        final BeanContextServiceRevokedEvent beanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(this.getBeanContextServicesPeer(), clazz, b);
        final Object[] array;
        synchronized (this.bcsListeners) {
            array = this.bcsListeners.toArray();
        }
        for (int i = 0; i < array.length; ++i) {
            ((BeanContextServicesListener)array[i]).serviceRevoked(beanContextServiceRevokedEvent);
        }
    }
    
    @Override
    protected synchronized void bcsPreSerializationHook(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.serializable);
        if (this.serializable <= 0) {
            return;
        }
        int n = 0;
        final Iterator iterator = this.services.entrySet().iterator();
        while (iterator.hasNext() && n < this.serializable) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            BCSSServiceProvider bcssServiceProvider;
            try {
                bcssServiceProvider = (BCSSServiceProvider)entry.getValue();
            }
            catch (final ClassCastException ex) {
                continue;
            }
            if (bcssServiceProvider.getServiceProvider() instanceof Serializable) {
                objectOutputStream.writeObject(entry.getKey());
                objectOutputStream.writeObject(bcssServiceProvider);
                ++n;
            }
        }
        if (n != this.serializable) {
            throw new IOException("wrote different number of service providers than expected");
        }
    }
    
    @Override
    protected synchronized void bcsPreDeserializationHook(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.serializable = objectInputStream.readInt();
        for (int i = this.serializable; i > 0; --i) {
            this.services.put(objectInputStream.readObject(), objectInputStream.readObject());
        }
    }
    
    private synchronized void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        this.serialize(objectOutputStream, this.bcsListeners);
    }
    
    private synchronized void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.deserialize(objectInputStream, this.bcsListeners);
    }
    
    protected class BCSSChild extends BCSChild
    {
        private static final long serialVersionUID = -3263851306889194873L;
        private transient HashMap serviceClasses;
        private transient HashMap serviceRequestors;
        
        BCSSChild(final Object o, final Object o2) {
            super(o, o2);
        }
        
        synchronized void usingService(final Object o, final Object o2, final Class clazz, final BeanContextServiceProvider beanContextServiceProvider, final boolean b, final BeanContextServiceRevokedListener beanContextServiceRevokedListener) throws TooManyListenersException, UnsupportedOperationException {
            BCSSCServiceClassRef bcsscServiceClassRef = null;
            if (this.serviceClasses == null) {
                this.serviceClasses = new HashMap(1);
            }
            else {
                bcsscServiceClassRef = this.serviceClasses.get(clazz);
            }
            if (bcsscServiceClassRef == null) {
                bcsscServiceClassRef = new BCSSCServiceClassRef(clazz, beanContextServiceProvider, b);
                this.serviceClasses.put(clazz, bcsscServiceClassRef);
            }
            else {
                bcsscServiceClassRef.verifyAndMaybeSetProvider(beanContextServiceProvider, b);
                bcsscServiceClassRef.verifyRequestor(o, beanContextServiceRevokedListener);
            }
            bcsscServiceClassRef.addRequestor(o, beanContextServiceRevokedListener);
            bcsscServiceClassRef.addRef(b);
            BCSSCServiceRef bcsscServiceRef = null;
            Map<?, ?> map = null;
            if (this.serviceRequestors == null) {
                this.serviceRequestors = new HashMap(1);
            }
            else {
                map = this.serviceRequestors.get(o);
            }
            if (map == null) {
                map = new HashMap<Object, Object>(1);
                this.serviceRequestors.put(o, map);
            }
            else {
                bcsscServiceRef = (BCSSCServiceRef)map.get(o2);
            }
            if (bcsscServiceRef == null) {
                map.put(o2, new BCSSCServiceRef(bcsscServiceClassRef, b));
            }
            else {
                bcsscServiceRef.addRef();
            }
        }
        
        synchronized void releaseService(final Object o, final Object o2) {
            if (this.serviceRequestors == null) {
                return;
            }
            final Map map = this.serviceRequestors.get(o);
            if (map == null) {
                return;
            }
            final BCSSCServiceRef bcsscServiceRef = (BCSSCServiceRef)map.get(o2);
            if (bcsscServiceRef == null) {
                return;
            }
            final BCSSCServiceClassRef serviceClassRef = bcsscServiceRef.getServiceClassRef();
            final boolean delegated = bcsscServiceRef.isDelegated();
            (delegated ? serviceClassRef.getDelegateProvider() : serviceClassRef.getServiceProvider()).releaseService(BeanContextServicesSupport.this.getBeanContextServicesPeer(), o, o2);
            serviceClassRef.releaseRef(delegated);
            serviceClassRef.removeRequestor(o);
            if (bcsscServiceRef.release() == 0) {
                map.remove(o2);
                if (map.isEmpty()) {
                    this.serviceRequestors.remove(o);
                    serviceClassRef.removeRequestor(o);
                }
                if (this.serviceRequestors.isEmpty()) {
                    this.serviceRequestors = null;
                }
                if (serviceClassRef.isEmpty()) {
                    this.serviceClasses.remove(serviceClassRef.getServiceClass());
                }
                if (this.serviceClasses.isEmpty()) {
                    this.serviceClasses = null;
                }
            }
        }
        
        synchronized void revokeService(final Class clazz, final boolean b, final boolean b2) {
            if (this.serviceClasses == null) {
                return;
            }
            final BCSSCServiceClassRef bcsscServiceClassRef = this.serviceClasses.get(clazz);
            if (bcsscServiceClassRef == null) {
                return;
            }
            final Iterator cloneOfEntries = bcsscServiceClassRef.cloneOfEntries();
            final BeanContextServiceRevokedEvent beanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(BeanContextServicesSupport.this.getBeanContextServicesPeer(), clazz, b2);
            int empty = 0;
            while (cloneOfEntries.hasNext() && this.serviceRequestors != null) {
                final Map.Entry entry = cloneOfEntries.next();
                final BeanContextServiceRevokedListener beanContextServiceRevokedListener = (BeanContextServiceRevokedListener)entry.getValue();
                if (b2) {
                    final Object key = entry.getKey();
                    final Map map = this.serviceRequestors.get(key);
                    if (map != null) {
                        final Iterator iterator = map.entrySet().iterator();
                        while (iterator.hasNext()) {
                            final BCSSCServiceRef bcsscServiceRef = ((Map.Entry<K, BCSSCServiceRef>)iterator.next()).getValue();
                            if (bcsscServiceRef.getServiceClassRef().equals(bcsscServiceClassRef) && b == bcsscServiceRef.isDelegated()) {
                                iterator.remove();
                            }
                        }
                        if ((empty = (map.isEmpty() ? 1 : 0)) != 0) {
                            this.serviceRequestors.remove(key);
                        }
                    }
                    if (empty != 0) {
                        bcsscServiceClassRef.removeRequestor(key);
                    }
                }
                beanContextServiceRevokedListener.serviceRevoked(beanContextServiceRevokedEvent);
            }
            if (b2 && this.serviceClasses != null) {
                if (bcsscServiceClassRef.isEmpty()) {
                    this.serviceClasses.remove(clazz);
                }
                if (this.serviceClasses.isEmpty()) {
                    this.serviceClasses = null;
                }
            }
            if (this.serviceRequestors != null && this.serviceRequestors.isEmpty()) {
                this.serviceRequestors = null;
            }
        }
        
        void cleanupReferences() {
            if (this.serviceRequestors == null) {
                return;
            }
            final Iterator iterator = this.serviceRequestors.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                final Object key = entry.getKey();
                final Iterator iterator2 = ((Map)entry.getValue()).entrySet().iterator();
                iterator.remove();
                while (iterator2.hasNext()) {
                    final Map.Entry entry2 = (Map.Entry)iterator2.next();
                    final Object key2 = entry2.getKey();
                    final BCSSCServiceRef bcsscServiceRef = (BCSSCServiceRef)entry2.getValue();
                    final BCSSCServiceClassRef serviceClassRef = bcsscServiceRef.getServiceClassRef();
                    final BeanContextServiceProvider beanContextServiceProvider = bcsscServiceRef.isDelegated() ? serviceClassRef.getDelegateProvider() : serviceClassRef.getServiceProvider();
                    serviceClassRef.removeRequestor(key);
                    iterator2.remove();
                    while (bcsscServiceRef.release() >= 0) {
                        beanContextServiceProvider.releaseService(BeanContextServicesSupport.this.getBeanContextServicesPeer(), key, key2);
                    }
                }
            }
            this.serviceRequestors = null;
            this.serviceClasses = null;
        }
        
        void revokeAllDelegatedServicesNow() {
            if (this.serviceClasses == null) {
                return;
            }
            for (final BCSSCServiceClassRef bcsscServiceClassRef : new HashSet(this.serviceClasses.values())) {
                if (!bcsscServiceClassRef.isDelegated()) {
                    continue;
                }
                final Iterator cloneOfEntries = bcsscServiceClassRef.cloneOfEntries();
                final BeanContextServiceRevokedEvent beanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(BeanContextServicesSupport.this.getBeanContextServicesPeer(), bcsscServiceClassRef.getServiceClass(), true);
                int empty = 0;
                while (cloneOfEntries.hasNext()) {
                    final Map.Entry entry = cloneOfEntries.next();
                    final BeanContextServiceRevokedListener beanContextServiceRevokedListener = (BeanContextServiceRevokedListener)entry.getValue();
                    final Object key = entry.getKey();
                    final Map map = this.serviceRequestors.get(key);
                    if (map != null) {
                        final Iterator iterator2 = map.entrySet().iterator();
                        while (iterator2.hasNext()) {
                            final BCSSCServiceRef bcsscServiceRef = ((Map.Entry<K, BCSSCServiceRef>)iterator2.next()).getValue();
                            if (bcsscServiceRef.getServiceClassRef().equals(bcsscServiceClassRef) && bcsscServiceRef.isDelegated()) {
                                iterator2.remove();
                            }
                        }
                        if ((empty = (map.isEmpty() ? 1 : 0)) != 0) {
                            this.serviceRequestors.remove(key);
                        }
                    }
                    if (empty != 0) {
                        bcsscServiceClassRef.removeRequestor(key);
                    }
                    beanContextServiceRevokedListener.serviceRevoked(beanContextServiceRevokedEvent);
                    if (bcsscServiceClassRef.isEmpty()) {
                        this.serviceClasses.remove(bcsscServiceClassRef.getServiceClass());
                    }
                }
            }
            if (this.serviceClasses.isEmpty()) {
                this.serviceClasses = null;
            }
            if (this.serviceRequestors != null && this.serviceRequestors.isEmpty()) {
                this.serviceRequestors = null;
            }
        }
        
        class BCSSCServiceClassRef
        {
            Class serviceClass;
            BeanContextServiceProvider serviceProvider;
            int serviceRefs;
            BeanContextServiceProvider delegateProvider;
            int delegateRefs;
            HashMap requestors;
            
            BCSSCServiceClassRef(final Class serviceClass, final BeanContextServiceProvider beanContextServiceProvider, final boolean b) {
                this.requestors = new HashMap(1);
                this.serviceClass = serviceClass;
                if (b) {
                    this.delegateProvider = beanContextServiceProvider;
                }
                else {
                    this.serviceProvider = beanContextServiceProvider;
                }
            }
            
            void addRequestor(final Object o, final BeanContextServiceRevokedListener beanContextServiceRevokedListener) throws TooManyListenersException {
                final BeanContextServiceRevokedListener beanContextServiceRevokedListener2 = this.requestors.get(o);
                if (beanContextServiceRevokedListener2 != null && !beanContextServiceRevokedListener2.equals(beanContextServiceRevokedListener)) {
                    throw new TooManyListenersException();
                }
                this.requestors.put(o, beanContextServiceRevokedListener);
            }
            
            void removeRequestor(final Object o) {
                this.requestors.remove(o);
            }
            
            void verifyRequestor(final Object o, final BeanContextServiceRevokedListener beanContextServiceRevokedListener) throws TooManyListenersException {
                final BeanContextServiceRevokedListener beanContextServiceRevokedListener2 = this.requestors.get(o);
                if (beanContextServiceRevokedListener2 != null && !beanContextServiceRevokedListener2.equals(beanContextServiceRevokedListener)) {
                    throw new TooManyListenersException();
                }
            }
            
            void verifyAndMaybeSetProvider(final BeanContextServiceProvider beanContextServiceProvider, final boolean b) {
                BeanContextServiceProvider beanContextServiceProvider2;
                if (b) {
                    beanContextServiceProvider2 = this.delegateProvider;
                    if (beanContextServiceProvider2 == null || beanContextServiceProvider == null) {
                        this.delegateProvider = beanContextServiceProvider;
                        return;
                    }
                }
                else {
                    beanContextServiceProvider2 = this.serviceProvider;
                    if (beanContextServiceProvider2 == null || beanContextServiceProvider == null) {
                        this.serviceProvider = beanContextServiceProvider;
                        return;
                    }
                }
                if (!beanContextServiceProvider2.equals(beanContextServiceProvider)) {
                    throw new UnsupportedOperationException("existing service reference obtained from different BeanContextServiceProvider not supported");
                }
            }
            
            Iterator cloneOfEntries() {
                return ((HashMap)this.requestors.clone()).entrySet().iterator();
            }
            
            Iterator entries() {
                return this.requestors.entrySet().iterator();
            }
            
            boolean isEmpty() {
                return this.requestors.isEmpty();
            }
            
            Class getServiceClass() {
                return this.serviceClass;
            }
            
            BeanContextServiceProvider getServiceProvider() {
                return this.serviceProvider;
            }
            
            BeanContextServiceProvider getDelegateProvider() {
                return this.delegateProvider;
            }
            
            boolean isDelegated() {
                return this.delegateProvider != null;
            }
            
            void addRef(final boolean b) {
                if (b) {
                    ++this.delegateRefs;
                }
                else {
                    ++this.serviceRefs;
                }
            }
            
            void releaseRef(final boolean b) {
                if (b) {
                    if (--this.delegateRefs == 0) {
                        this.delegateProvider = null;
                    }
                }
                else if (--this.serviceRefs <= 0) {
                    this.serviceProvider = null;
                }
            }
            
            int getRefs() {
                return this.serviceRefs + this.delegateRefs;
            }
            
            int getDelegateRefs() {
                return this.delegateRefs;
            }
            
            int getServiceRefs() {
                return this.serviceRefs;
            }
        }
        
        class BCSSCServiceRef
        {
            BCSSCServiceClassRef serviceClassRef;
            int refCnt;
            boolean delegated;
            
            BCSSCServiceRef(final BCSSCServiceClassRef serviceClassRef, final boolean delegated) {
                this.refCnt = 1;
                this.delegated = false;
                this.serviceClassRef = serviceClassRef;
                this.delegated = delegated;
            }
            
            void addRef() {
                ++this.refCnt;
            }
            
            int release() {
                return --this.refCnt;
            }
            
            BCSSCServiceClassRef getServiceClassRef() {
                return this.serviceClassRef;
            }
            
            boolean isDelegated() {
                return this.delegated;
            }
        }
    }
    
    protected static class BCSSServiceProvider implements Serializable
    {
        private static final long serialVersionUID = 861278251667444782L;
        protected BeanContextServiceProvider serviceProvider;
        
        BCSSServiceProvider(final Class clazz, final BeanContextServiceProvider serviceProvider) {
            this.serviceProvider = serviceProvider;
        }
        
        protected BeanContextServiceProvider getServiceProvider() {
            return this.serviceProvider;
        }
    }
    
    protected class BCSSProxyServiceProvider implements BeanContextServiceProvider, BeanContextServiceRevokedListener
    {
        private BeanContextServices nestingCtxt;
        
        BCSSProxyServiceProvider(final BeanContextServices nestingCtxt) {
            this.nestingCtxt = nestingCtxt;
        }
        
        @Override
        public Object getService(final BeanContextServices beanContextServices, final Object o, final Class clazz, final Object o2) {
            Object service;
            try {
                service = this.nestingCtxt.getService(beanContextServices, o, clazz, o2, this);
            }
            catch (final TooManyListenersException ex) {
                return null;
            }
            return service;
        }
        
        @Override
        public void releaseService(final BeanContextServices beanContextServices, final Object o, final Object o2) {
            this.nestingCtxt.releaseService(beanContextServices, o, o2);
        }
        
        @Override
        public Iterator getCurrentServiceSelectors(final BeanContextServices beanContextServices, final Class clazz) {
            return this.nestingCtxt.getCurrentServiceSelectors(clazz);
        }
        
        @Override
        public void serviceRevoked(final BeanContextServiceRevokedEvent beanContextServiceRevokedEvent) {
            final Iterator bcsChildren = BeanContextServicesSupport.this.bcsChildren();
            while (bcsChildren.hasNext()) {
                ((BCSSChild)bcsChildren.next()).revokeService(beanContextServiceRevokedEvent.getServiceClass(), true, beanContextServiceRevokedEvent.isCurrentServiceInvalidNow());
            }
        }
    }
}
