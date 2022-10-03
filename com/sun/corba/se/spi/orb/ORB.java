package com.sun.corba.se.spi.orb;

import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.impl.transport.ByteBufferPoolImpl;
import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import java.util.logging.Logger;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.IOR;
import java.util.Properties;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import sun.awt.AppContext;
import com.sun.corba.se.impl.presentation.rmi.PresentationManagerImpl;
import sun.corba.SharedSecrets;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import java.util.Map;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.corba.TypeCodeFactory;
import com.sun.corba.se.pept.broker.Broker;

public abstract class ORB extends com.sun.corba.se.org.omg.CORBA.ORB implements Broker, TypeCodeFactory
{
    public static boolean ORBInitDebug;
    public boolean transportDebugFlag;
    public boolean subcontractDebugFlag;
    public boolean poaDebugFlag;
    public boolean poaConcurrencyDebugFlag;
    public boolean poaFSMDebugFlag;
    public boolean orbdDebugFlag;
    public boolean namingDebugFlag;
    public boolean serviceContextDebugFlag;
    public boolean transientObjectManagerDebugFlag;
    public boolean giopVersionDebugFlag;
    public boolean shutdownDebugFlag;
    public boolean giopDebugFlag;
    public boolean invocationTimingDebugFlag;
    public boolean orbInitDebugFlag;
    protected static ORBUtilSystemException staticWrapper;
    protected ORBUtilSystemException wrapper;
    protected OMGSystemException omgWrapper;
    private Map typeCodeMap;
    private TypeCodeImpl[] primitiveTypeCodeConstants;
    ByteBufferPool byteBufferPool;
    private Map wrapperMap;
    private static final Object pmLock;
    private static Map staticWrapperMap;
    protected MonitoringManager monitoringManager;
    
    public abstract boolean isLocalHost(final String p0);
    
    public abstract boolean isLocalServerId(final int p0, final int p1);
    
    public abstract OAInvocationInfo peekInvocationInfo();
    
    public abstract void pushInvocationInfo(final OAInvocationInfo p0);
    
    public abstract OAInvocationInfo popInvocationInfo();
    
    public abstract CorbaTransportManager getCorbaTransportManager();
    
    public abstract LegacyServerSocketManager getLegacyServerSocketManager();
    
    private static PresentationManager setupPresentationManager() {
        ORB.staticWrapper = ORBUtilSystemException.get("rpc.presentation");
        final boolean booleanValue = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            @Override
            public Object run() {
                return Boolean.getBoolean("com.sun.CORBA.ORBUseDynamicStub");
            }
        });
        final PresentationManager.StubFactoryFactory stubFactoryFactory = AccessController.doPrivileged((PrivilegedAction<PresentationManager.StubFactoryFactory>)new PrivilegedAction() {
            @Override
            public Object run() {
                PresentationManager.StubFactoryFactory proxyStubFactoryFactory = PresentationDefaults.getProxyStubFactoryFactory();
                final String property = System.getProperty("com.sun.CORBA.ORBDynamicStubFactoryFactoryClass", "com.sun.corba.se.impl.presentation.rmi.bcel.StubFactoryFactoryBCELImpl");
                try {
                    proxyStubFactoryFactory = (PresentationManager.StubFactoryFactory)SharedSecrets.getJavaCorbaAccess().loadClass(property).newInstance();
                }
                catch (final Exception ex) {
                    ORB.staticWrapper.errorInSettingDynamicStubFactoryFactory(ex, property);
                }
                return proxyStubFactoryFactory;
            }
        });
        final PresentationManagerImpl presentationManagerImpl = new PresentationManagerImpl(booleanValue);
        presentationManagerImpl.setStubFactoryFactory(false, PresentationDefaults.getStaticStubFactoryFactory());
        presentationManagerImpl.setStubFactoryFactory(true, stubFactoryFactory);
        return presentationManagerImpl;
    }
    
    @Override
    public void destroy() {
        this.wrapper = null;
        this.omgWrapper = null;
        this.typeCodeMap = null;
        this.primitiveTypeCodeConstants = null;
        this.byteBufferPool = null;
    }
    
    public static PresentationManager getPresentationManager() {
        if (System.getSecurityManager() != null && AppContext.getAppContexts().size() > 0) {
            final AppContext appContext = AppContext.getAppContext();
            if (appContext != null) {
                synchronized (ORB.pmLock) {
                    PresentationManager setupPresentationManager = (PresentationManager)appContext.get(PresentationManager.class);
                    if (setupPresentationManager == null) {
                        setupPresentationManager = setupPresentationManager();
                        appContext.put(PresentationManager.class, setupPresentationManager);
                    }
                    return setupPresentationManager;
                }
            }
        }
        return Holder.defaultPresentationManager;
    }
    
    public static PresentationManager.StubFactoryFactory getStubFactoryFactory() {
        final PresentationManager presentationManager = getPresentationManager();
        return presentationManager.getStubFactoryFactory(presentationManager.useDynamicStubs());
    }
    
    protected ORB() {
        this.transportDebugFlag = false;
        this.subcontractDebugFlag = false;
        this.poaDebugFlag = false;
        this.poaConcurrencyDebugFlag = false;
        this.poaFSMDebugFlag = false;
        this.orbdDebugFlag = false;
        this.namingDebugFlag = false;
        this.serviceContextDebugFlag = false;
        this.transientObjectManagerDebugFlag = false;
        this.giopVersionDebugFlag = false;
        this.shutdownDebugFlag = false;
        this.giopDebugFlag = false;
        this.invocationTimingDebugFlag = false;
        this.orbInitDebugFlag = false;
        this.wrapperMap = new ConcurrentHashMap();
        this.wrapper = ORBUtilSystemException.get(this, "rpc.presentation");
        this.omgWrapper = OMGSystemException.get(this, "rpc.presentation");
        this.typeCodeMap = new HashMap();
        this.primitiveTypeCodeConstants = new TypeCodeImpl[] { new TypeCodeImpl(this, 0), new TypeCodeImpl(this, 1), new TypeCodeImpl(this, 2), new TypeCodeImpl(this, 3), new TypeCodeImpl(this, 4), new TypeCodeImpl(this, 5), new TypeCodeImpl(this, 6), new TypeCodeImpl(this, 7), new TypeCodeImpl(this, 8), new TypeCodeImpl(this, 9), new TypeCodeImpl(this, 10), new TypeCodeImpl(this, 11), new TypeCodeImpl(this, 12), new TypeCodeImpl(this, 13), new TypeCodeImpl(this, 14), null, null, null, new TypeCodeImpl(this, 18), null, null, null, null, new TypeCodeImpl(this, 23), new TypeCodeImpl(this, 24), new TypeCodeImpl(this, 25), new TypeCodeImpl(this, 26), new TypeCodeImpl(this, 27), new TypeCodeImpl(this, 28), new TypeCodeImpl(this, 29), new TypeCodeImpl(this, 30), new TypeCodeImpl(this, 31), new TypeCodeImpl(this, 32) };
        this.monitoringManager = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", "ORB Management and Monitoring Root");
    }
    
    public TypeCodeImpl get_primitive_tc(final int n) {
        synchronized (this) {
            this.checkShutdownState();
        }
        try {
            return this.primitiveTypeCodeConstants[n];
        }
        catch (final Throwable t) {
            throw this.wrapper.invalidTypecodeKind(t, new Integer(n));
        }
    }
    
    @Override
    public synchronized void setTypeCode(final String s, final TypeCodeImpl typeCodeImpl) {
        this.checkShutdownState();
        this.typeCodeMap.put(s, typeCodeImpl);
    }
    
    @Override
    public synchronized TypeCodeImpl getTypeCode(final String s) {
        this.checkShutdownState();
        return this.typeCodeMap.get(s);
    }
    
    public MonitoringManager getMonitoringManager() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.monitoringManager;
    }
    
    public abstract void set_parameters(final Properties p0);
    
    public abstract ORBVersion getORBVersion();
    
    public abstract void setORBVersion(final ORBVersion p0);
    
    public abstract IOR getFVDCodeBaseIOR();
    
    public abstract void handleBadServerId(final ObjectKey p0);
    
    public abstract void setBadServerIdHandler(final BadServerIdHandler p0);
    
    public abstract void initBadServerIdHandler();
    
    public abstract void notifyORB();
    
    public abstract PIHandler getPIHandler();
    
    public abstract void checkShutdownState();
    
    public abstract boolean isDuringDispatch();
    
    public abstract void startingDispatch();
    
    public abstract void finishedDispatch();
    
    public abstract int getTransientServerId();
    
    public abstract ServiceContextRegistry getServiceContextRegistry();
    
    public abstract RequestDispatcherRegistry getRequestDispatcherRegistry();
    
    public abstract ORBData getORBData();
    
    public abstract void setClientDelegateFactory(final ClientDelegateFactory p0);
    
    public abstract ClientDelegateFactory getClientDelegateFactory();
    
    public abstract void setCorbaContactInfoListFactory(final CorbaContactInfoListFactory p0);
    
    public abstract CorbaContactInfoListFactory getCorbaContactInfoListFactory();
    
    public abstract void setResolver(final Resolver p0);
    
    public abstract Resolver getResolver();
    
    public abstract void setLocalResolver(final LocalResolver p0);
    
    public abstract LocalResolver getLocalResolver();
    
    public abstract void setURLOperation(final Operation p0);
    
    public abstract Operation getURLOperation();
    
    public abstract void setINSDelegate(final CorbaServerRequestDispatcher p0);
    
    public abstract TaggedComponentFactoryFinder getTaggedComponentFactoryFinder();
    
    public abstract IdentifiableFactoryFinder getTaggedProfileFactoryFinder();
    
    public abstract IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder();
    
    public abstract ObjectKeyFactory getObjectKeyFactory();
    
    public abstract void setObjectKeyFactory(final ObjectKeyFactory p0);
    
    public Logger getLogger(final String s) {
        synchronized (this) {
            this.checkShutdownState();
        }
        final ORBData orbData = this.getORBData();
        String orbId;
        if (orbData == null) {
            orbId = "_INITIALIZING_";
        }
        else {
            orbId = orbData.getORBId();
            if (orbId.equals("")) {
                orbId = "_DEFAULT_";
            }
        }
        return getCORBALogger(orbId, s);
    }
    
    public static Logger staticGetLogger(final String s) {
        return getCORBALogger("_CORBA_", s);
    }
    
    private static Logger getCORBALogger(final String s, final String s2) {
        return Logger.getLogger("javax.enterprise.resource.corba." + s + "." + s2, "com.sun.corba.se.impl.logging.LogStrings");
    }
    
    public LogWrapperBase getLogWrapper(final String s, final String s2, final LogWrapperFactory logWrapperFactory) {
        final StringPair stringPair = new StringPair(s, s2);
        LogWrapperBase create = this.wrapperMap.get(stringPair);
        if (create == null) {
            create = logWrapperFactory.create(this.getLogger(s));
            this.wrapperMap.put(stringPair, create);
        }
        return create;
    }
    
    public static LogWrapperBase staticGetLogWrapper(final String s, final String s2, final LogWrapperFactory logWrapperFactory) {
        final StringPair stringPair = new StringPair(s, s2);
        LogWrapperBase create = ORB.staticWrapperMap.get(stringPair);
        if (create == null) {
            create = logWrapperFactory.create(staticGetLogger(s));
            ORB.staticWrapperMap.put(stringPair, create);
        }
        return create;
    }
    
    public ByteBufferPool getByteBufferPool() {
        synchronized (this) {
            this.checkShutdownState();
        }
        if (this.byteBufferPool == null) {
            this.byteBufferPool = new ByteBufferPoolImpl(this);
        }
        return this.byteBufferPool;
    }
    
    public abstract void setThreadPoolManager(final ThreadPoolManager p0);
    
    public abstract ThreadPoolManager getThreadPoolManager();
    
    public abstract CopierManager getCopierManager();
    
    public abstract void validateIORClass(final String p0);
    
    static {
        ORB.ORBInitDebug = false;
        pmLock = new Object();
        ORB.staticWrapperMap = new ConcurrentHashMap();
    }
    
    static class Holder
    {
        static final PresentationManager defaultPresentationManager;
        
        static {
            defaultPresentationManager = setupPresentationManager();
        }
    }
}
