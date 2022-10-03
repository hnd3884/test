package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.ParserImplBase;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.threadpool.ThreadPoolManagerImpl;
import com.sun.corba.se.impl.legacy.connection.LegacyServerSocketManagerImpl;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.impl.transport.CorbaTransportManagerImpl;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import org.omg.PortableServer.Servant;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Policy;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import org.omg.CORBA.MARSHAL;
import com.sun.corba.se.impl.util.Utility;
import org.omg.CORBA.portable.ValueFactory;
import com.sun.corba.se.impl.encoding.CachedCodeBase;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import java.util.Iterator;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import java.util.Collection;
import java.util.HashSet;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import org.omg.CORBA.ORBPackage.InvalidName;
import java.util.Set;
import java.util.Collections;
import java.util.WeakHashMap;
import com.sun.corba.se.impl.corba.AnyImpl;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.UnionMember;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TCKind;
import org.omg.SendingContext.RunTime;
import org.omg.CORBA.BAD_PARAM;
import com.sun.corba.se.spi.ior.IORFactories;
import org.omg.CORBA.WrongTransaction;
import java.util.Enumeration;
import com.sun.corba.se.impl.corba.AsynchInvoke;
import com.sun.corba.se.impl.corba.RequestImpl;
import org.omg.CORBA.Request;
import com.sun.corba.se.impl.corba.EnvironmentImpl;
import org.omg.CORBA.Environment;
import org.omg.CORBA.Context;
import com.sun.corba.se.impl.corba.ContextListImpl;
import org.omg.CORBA.ContextList;
import com.sun.corba.se.impl.corba.ExceptionListImpl;
import org.omg.CORBA.ExceptionList;
import com.sun.corba.se.impl.corba.NamedValueImpl;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Any;
import com.sun.corba.se.impl.corba.NVListImpl;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Current;
import sun.corba.OutputStreamFactory;
import org.omg.CORBA.portable.OutputStream;
import java.applet.Applet;
import com.sun.corba.se.impl.interceptors.PIHandlerImpl;
import com.sun.corba.se.spi.orb.ORBConfigurator;
import com.sun.corba.se.spi.orb.DataCollector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.sun.corba.se.impl.ior.IORTypeCheckRegistryImpl;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import com.sun.corba.se.impl.orbutil.StackImpl;
import com.sun.corba.se.impl.ior.TaggedProfileTemplateFactoryFinderImpl;
import com.sun.corba.se.impl.ior.TaggedProfileFactoryFinderImpl;
import com.sun.corba.se.impl.ior.TaggedComponentFactoryFinderImpl;
import com.sun.corba.se.impl.copyobject.CopierManagerImpl;
import com.sun.corba.se.impl.protocol.RequestDispatcherRegistryImpl;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.impl.interceptors.PINoOpHandlerImpl;
import java.util.Properties;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.corba.se.spi.ior.IORTypeCheckRegistry;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.orb.ORB;

public class ORBImpl extends ORB
{
    protected TransportManager transportManager;
    protected LegacyServerSocketManager legacyServerSocketManager;
    private ThreadLocal OAInvocationInfoStack;
    private ThreadLocal clientInvocationInfoStack;
    private static IOR codeBaseIOR;
    private Vector dynamicRequests;
    private SynchVariable svResponseReceived;
    private Object runObj;
    private Object shutdownObj;
    private Object waitForCompletionObj;
    private static final byte STATUS_OPERATING = 1;
    private static final byte STATUS_SHUTTING_DOWN = 2;
    private static final byte STATUS_SHUTDOWN = 3;
    private static final byte STATUS_DESTROYED = 4;
    private byte status;
    private Object invocationObj;
    private int numInvocations;
    private ThreadLocal isProcessingInvocation;
    private Map typeCodeForClassMap;
    private Hashtable valueFactoryCache;
    private ThreadLocal orbVersionThreadLocal;
    private RequestDispatcherRegistry requestDispatcherRegistry;
    private CopierManager copierManager;
    private int transientServerId;
    private ServiceContextRegistry serviceContextRegistry;
    private IORTypeCheckRegistry iorTypeCheckRegistry;
    private TOAFactory toaFactory;
    private POAFactory poaFactory;
    private PIHandler pihandler;
    private ORBData configData;
    private BadServerIdHandler badServerIdHandler;
    private ClientDelegateFactory clientDelegateFactory;
    private CorbaContactInfoListFactory corbaContactInfoListFactory;
    private Resolver resolver;
    private LocalResolver localResolver;
    private Operation urlOperation;
    private final Object urlOperationLock;
    private CorbaServerRequestDispatcher insNamingDelegate;
    private final Object resolverLock;
    private static final String IORTYPECHECKREGISTRY_FILTER_PROPNAME = "com.sun.CORBA.ORBIorTypeCheckRegistryFilter";
    private TaggedComponentFactoryFinder taggedComponentFactoryFinder;
    private IdentifiableFactoryFinder taggedProfileFactoryFinder;
    private IdentifiableFactoryFinder taggedProfileTemplateFactoryFinder;
    private ObjectKeyFactory objectKeyFactory;
    private boolean orbOwnsThreadPoolManager;
    private ThreadPoolManager threadpoolMgr;
    private Object badServerIdHandlerAccessLock;
    private static String localHostString;
    private Object clientDelegateFactoryAccessorLock;
    private Object corbaContactInfoListFactoryAccessLock;
    private Object objectKeyFactoryAccessLock;
    private Object transportManagerAccessorLock;
    private Object legacyServerSocketManagerAccessLock;
    private Object threadPoolManagerAccessLock;
    
    private void dprint(final String s) {
        ORBUtility.dprint(this, s);
    }
    
    @Override
    public ORBData getORBData() {
        return this.configData;
    }
    
    @Override
    public PIHandler getPIHandler() {
        return this.pihandler;
    }
    
    public ORBImpl() {
        this.runObj = new Object();
        this.shutdownObj = new Object();
        this.waitForCompletionObj = new Object();
        this.status = 1;
        this.invocationObj = new Object();
        this.numInvocations = 0;
        this.isProcessingInvocation = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return Boolean.FALSE;
            }
        };
        this.valueFactoryCache = new Hashtable();
        this.urlOperationLock = new Object();
        this.resolverLock = new Object();
        this.orbOwnsThreadPoolManager = false;
        this.badServerIdHandlerAccessLock = new Object();
        this.clientDelegateFactoryAccessorLock = new Object();
        this.corbaContactInfoListFactoryAccessLock = new Object();
        this.objectKeyFactoryAccessLock = new Object();
        this.transportManagerAccessorLock = new Object();
        this.legacyServerSocketManagerAccessLock = new Object();
        this.threadPoolManagerAccessLock = new Object();
    }
    
    @Override
    public ORBVersion getORBVersion() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.orbVersionThreadLocal.get();
    }
    
    @Override
    public void setORBVersion(final ORBVersion orbVersion) {
        synchronized (this) {
            this.checkShutdownState();
        }
        this.orbVersionThreadLocal.set(orbVersion);
    }
    
    private void preInit(final String[] array, final Properties properties) {
        this.pihandler = new PINoOpHandlerImpl();
        this.transientServerId = (int)System.currentTimeMillis();
        this.orbVersionThreadLocal = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return ORBVersionFactory.getORBVersion();
            }
        };
        this.requestDispatcherRegistry = new RequestDispatcherRegistryImpl(this, 2);
        this.copierManager = new CopierManagerImpl(this);
        this.taggedComponentFactoryFinder = new TaggedComponentFactoryFinderImpl(this);
        this.taggedProfileFactoryFinder = new TaggedProfileFactoryFinderImpl(this);
        this.taggedProfileTemplateFactoryFinder = new TaggedProfileTemplateFactoryFinderImpl(this);
        this.dynamicRequests = new Vector();
        this.svResponseReceived = new SynchVariable();
        this.OAInvocationInfoStack = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return new StackImpl();
            }
        };
        this.clientInvocationInfoStack = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return new StackImpl();
            }
        };
        this.serviceContextRegistry = new ServiceContextRegistry(this);
    }
    
    private void initIORTypeCheckRegistry() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                String s = System.getProperty("com.sun.CORBA.ORBIorTypeCheckRegistryFilter");
                if (s == null) {
                    s = Security.getProperty("com.sun.CORBA.ORBIorTypeCheckRegistryFilter");
                }
                return s;
            }
        });
        if (s != null) {
            try {
                this.iorTypeCheckRegistry = new IORTypeCheckRegistryImpl(s, this);
            }
            catch (final Exception ex) {
                throw this.wrapper.bootstrapException(ex);
            }
            if (this.orbInitDebugFlag) {
                this.dprint(".initIORTypeCheckRegistry, IORTypeCheckRegistryImpl created for properties == " + s);
            }
        }
        else if (this.orbInitDebugFlag) {
            this.dprint(".initIORTypeCheckRegistry, IORTypeCheckRegistryImpl NOT created for properties == ");
        }
    }
    
    protected void setDebugFlags(final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            try {
                final Field field = this.getClass().getField(s + "DebugFlag");
                final int modifiers = field.getModifiers();
                if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && field.getType() == Boolean.TYPE) {
                    field.setBoolean(this, true);
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    private void postInit(final String[] array, final DataCollector dataCollector) {
        this.configData = new ORBDataParserImpl(this, dataCollector);
        this.setDebugFlags(this.configData.getORBDebugFlags());
        this.getTransportManager();
        this.getLegacyServerSocketManager();
        final ConfigParser configParser = new ConfigParser();
        configParser.init(dataCollector);
        ORBConfigurator orbConfigurator;
        try {
            orbConfigurator = configParser.configurator.newInstance();
        }
        catch (final Exception ex) {
            throw this.wrapper.badOrbConfigurator(ex, configParser.configurator.getName());
        }
        try {
            orbConfigurator.configure(dataCollector, this);
        }
        catch (final Exception ex2) {
            throw this.wrapper.orbConfiguratorError(ex2);
        }
        (this.pihandler = new PIHandlerImpl(this, array)).initialize();
        this.getThreadPoolManager();
        super.getByteBufferPool();
        this.initIORTypeCheckRegistry();
    }
    
    private synchronized POAFactory getPOAFactory() {
        if (this.poaFactory == null) {
            this.poaFactory = (POAFactory)this.requestDispatcherRegistry.getObjectAdapterFactory(32);
        }
        return this.poaFactory;
    }
    
    private synchronized TOAFactory getTOAFactory() {
        if (this.toaFactory == null) {
            this.toaFactory = (TOAFactory)this.requestDispatcherRegistry.getObjectAdapterFactory(2);
        }
        return this.toaFactory;
    }
    
    @Override
    public void set_parameters(final Properties properties) {
        synchronized (this) {
            this.checkShutdownState();
        }
        this.preInit(null, properties);
        this.postInit(null, DataCollectorFactory.create(properties, this.getLocalHostName()));
    }
    
    @Override
    protected void set_parameters(final Applet applet, final Properties properties) {
        this.preInit(null, properties);
        this.postInit(null, DataCollectorFactory.create(applet, properties, this.getLocalHostName()));
    }
    
    @Override
    protected void set_parameters(final String[] array, final Properties properties) {
        this.preInit(array, properties);
        this.postInit(array, DataCollectorFactory.create(array, properties, this.getLocalHostName()));
    }
    
    @Override
    public synchronized OutputStream create_output_stream() {
        this.checkShutdownState();
        return OutputStreamFactory.newEncapsOutputStream(this);
    }
    
    @Override
    @Deprecated
    public synchronized Current get_current() {
        this.checkShutdownState();
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public synchronized NVList create_list(final int n) {
        this.checkShutdownState();
        return new NVListImpl(this, n);
    }
    
    @Override
    public synchronized NVList create_operation_list(final org.omg.CORBA.Object object) {
        this.checkShutdownState();
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public synchronized NamedValue create_named_value(final String s, final Any any, final int n) {
        this.checkShutdownState();
        return new NamedValueImpl(this, s, any, n);
    }
    
    @Override
    public synchronized ExceptionList create_exception_list() {
        this.checkShutdownState();
        return new ExceptionListImpl();
    }
    
    @Override
    public synchronized ContextList create_context_list() {
        this.checkShutdownState();
        return new ContextListImpl(this);
    }
    
    @Override
    public synchronized Context get_default_context() {
        this.checkShutdownState();
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public synchronized Environment create_environment() {
        this.checkShutdownState();
        return new EnvironmentImpl();
    }
    
    @Override
    public synchronized void send_multiple_requests_oneway(final Request[] array) {
        this.checkShutdownState();
        for (int i = 0; i < array.length; ++i) {
            array[i].send_oneway();
        }
    }
    
    @Override
    public synchronized void send_multiple_requests_deferred(final Request[] array) {
        this.checkShutdownState();
        for (int i = 0; i < array.length; ++i) {
            this.dynamicRequests.addElement(array[i]);
        }
        for (int j = 0; j < array.length; ++j) {
            new Thread(new AsynchInvoke(this, (RequestImpl)array[j], true)).start();
        }
    }
    
    @Override
    public synchronized boolean poll_next_response() {
        this.checkShutdownState();
        final Enumeration elements = this.dynamicRequests.elements();
        while (elements.hasMoreElements()) {
            if (((Request)elements.nextElement()).poll_response()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Request get_next_response() throws WrongTransaction {
        synchronized (this) {
            this.checkShutdownState();
        }
        while (true) {
            synchronized (this.dynamicRequests) {
                final Enumeration elements = this.dynamicRequests.elements();
                while (elements.hasMoreElements()) {
                    final Request request = (Request)elements.nextElement();
                    if (request.poll_response()) {
                        request.get_response();
                        this.dynamicRequests.removeElement(request);
                        return request;
                    }
                }
            }
            synchronized (this.svResponseReceived) {
                while (!this.svResponseReceived.value()) {
                    try {
                        this.svResponseReceived.wait();
                    }
                    catch (final InterruptedException ex) {}
                }
                this.svResponseReceived.reset();
            }
        }
    }
    
    @Override
    public void notifyORB() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.svResponseReceived) {
            this.svResponseReceived.set();
            this.svResponseReceived.notify();
        }
    }
    
    @Override
    public synchronized String object_to_string(final org.omg.CORBA.Object object) {
        this.checkShutdownState();
        if (object == null) {
            return IORFactories.makeIOR(this).stringify();
        }
        IOR connectAndGetIOR;
        try {
            connectAndGetIOR = ORBUtility.connectAndGetIOR(this, object);
        }
        catch (final BAD_PARAM bad_PARAM) {
            if (bad_PARAM.minor == 1398079694) {
                throw this.omgWrapper.notAnObjectImpl(bad_PARAM);
            }
            throw bad_PARAM;
        }
        return connectAndGetIOR.stringify();
    }
    
    @Override
    public org.omg.CORBA.Object string_to_object(final String s) {
        final Operation urlOperation;
        synchronized (this) {
            this.checkShutdownState();
            urlOperation = this.urlOperation;
        }
        if (s == null) {
            throw this.wrapper.nullParam();
        }
        synchronized (this.urlOperationLock) {
            return (org.omg.CORBA.Object)urlOperation.operate(s);
        }
    }
    
    @Override
    public synchronized IOR getFVDCodeBaseIOR() {
        this.checkShutdownState();
        if (ORBImpl.codeBaseIOR != null) {
            return ORBImpl.codeBaseIOR;
        }
        return ORBUtility.connectAndGetIOR(this, ORBUtility.createValueHandler().getRunTimeCodeBase());
    }
    
    @Override
    public synchronized TypeCode get_primitive_tc(final TCKind tcKind) {
        this.checkShutdownState();
        return this.get_primitive_tc(tcKind.value());
    }
    
    @Override
    public synchronized TypeCode create_struct_tc(final String s, final String s2, final StructMember[] array) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 15, s, s2, array);
    }
    
    @Override
    public synchronized TypeCode create_union_tc(final String s, final String s2, final TypeCode typeCode, final UnionMember[] array) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 16, s, s2, typeCode, array);
    }
    
    @Override
    public synchronized TypeCode create_enum_tc(final String s, final String s2, final String[] array) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 17, s, s2, array);
    }
    
    @Override
    public synchronized TypeCode create_alias_tc(final String s, final String s2, final TypeCode typeCode) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 21, s, s2, typeCode);
    }
    
    @Override
    public synchronized TypeCode create_exception_tc(final String s, final String s2, final StructMember[] array) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 22, s, s2, array);
    }
    
    @Override
    public synchronized TypeCode create_interface_tc(final String s, final String s2) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 14, s, s2);
    }
    
    @Override
    public synchronized TypeCode create_string_tc(final int n) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 18, n);
    }
    
    @Override
    public synchronized TypeCode create_wstring_tc(final int n) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 27, n);
    }
    
    @Override
    public synchronized TypeCode create_sequence_tc(final int n, final TypeCode typeCode) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 19, n, typeCode);
    }
    
    @Override
    public synchronized TypeCode create_recursive_sequence_tc(final int n, final int n2) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 19, n, n2);
    }
    
    @Override
    public synchronized TypeCode create_array_tc(final int n, final TypeCode typeCode) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 20, n, typeCode);
    }
    
    @Override
    public synchronized TypeCode create_native_tc(final String s, final String s2) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 31, s, s2);
    }
    
    @Override
    public synchronized TypeCode create_abstract_interface_tc(final String s, final String s2) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 32, s, s2);
    }
    
    @Override
    public synchronized TypeCode create_fixed_tc(final short n, final short n2) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 28, n, n2);
    }
    
    @Override
    public synchronized TypeCode create_value_tc(final String s, final String s2, final short n, final TypeCode typeCode, final ValueMember[] array) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 29, s, s2, n, typeCode, array);
    }
    
    @Override
    public synchronized TypeCode create_recursive_tc(final String s) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, s);
    }
    
    @Override
    public synchronized TypeCode create_value_box_tc(final String s, final String s2, final TypeCode typeCode) {
        this.checkShutdownState();
        return new TypeCodeImpl(this, 30, s, s2, typeCode);
    }
    
    @Override
    public synchronized Any create_any() {
        this.checkShutdownState();
        return new AnyImpl(this);
    }
    
    @Override
    public synchronized void setTypeCodeForClass(final Class clazz, final TypeCodeImpl typeCodeImpl) {
        this.checkShutdownState();
        if (this.typeCodeForClassMap == null) {
            this.typeCodeForClassMap = Collections.synchronizedMap(new WeakHashMap<Object, Object>(64));
        }
        if (!this.typeCodeForClassMap.containsKey(clazz)) {
            this.typeCodeForClassMap.put(clazz, typeCodeImpl);
        }
    }
    
    @Override
    public synchronized TypeCodeImpl getTypeCodeForClass(final Class clazz) {
        this.checkShutdownState();
        if (this.typeCodeForClassMap == null) {
            return null;
        }
        return this.typeCodeForClassMap.get(clazz);
    }
    
    @Override
    public String[] list_initial_services() {
        final Resolver resolver;
        synchronized (this) {
            this.checkShutdownState();
            resolver = this.resolver;
        }
        synchronized (this.resolverLock) {
            final Set list = resolver.list();
            return list.toArray(new String[list.size()]);
        }
    }
    
    @Override
    public org.omg.CORBA.Object resolve_initial_references(final String s) throws InvalidName {
        final Resolver resolver;
        synchronized (this) {
            this.checkShutdownState();
            resolver = this.resolver;
        }
        synchronized (this.resolverLock) {
            final org.omg.CORBA.Object resolve = resolver.resolve(s);
            if (resolve == null) {
                throw new InvalidName();
            }
            return resolve;
        }
    }
    
    @Override
    public void register_initial_reference(final String s, final org.omg.CORBA.Object object) throws InvalidName {
        synchronized (this) {
            this.checkShutdownState();
        }
        if (s == null || s.length() == 0) {
            throw new InvalidName();
        }
        synchronized (this) {
            this.checkShutdownState();
        }
        final CorbaServerRequestDispatcher insNamingDelegate;
        synchronized (this.resolverLock) {
            insNamingDelegate = this.insNamingDelegate;
            if (this.localResolver.resolve(s) != null) {
                throw new InvalidName(s + " already registered");
            }
            this.localResolver.register(s, ClosureFactory.makeConstant(object));
        }
        synchronized (this) {
            if (StubAdapter.isStub(object)) {
                this.requestDispatcherRegistry.registerServerRequestDispatcher(insNamingDelegate, s);
            }
        }
    }
    
    @Override
    public void run() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.runObj) {
            try {
                this.runObj.wait();
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    @Override
    public void shutdown(final boolean b) {
        boolean b2 = false;
        synchronized (this) {
            this.checkShutdownState();
            if (b && this.isProcessingInvocation.get() == Boolean.TRUE) {
                throw this.omgWrapper.shutdownWaitForCompletionDeadlock();
            }
            if (this.status == 2) {
                if (!b) {
                    return;
                }
                b2 = true;
            }
            this.status = 2;
        }
        synchronized (this.shutdownObj) {
            if (b2) {
                while (true) {
                    synchronized (this) {
                        if (this.status == 3) {
                            break;
                        }
                    }
                    try {
                        this.shutdownObj.wait();
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            else {
                this.shutdownServants(b);
                if (b) {
                    synchronized (this.waitForCompletionObj) {
                        while (this.numInvocations > 0) {
                            try {
                                this.waitForCompletionObj.wait();
                            }
                            catch (final InterruptedException ex2) {}
                        }
                    }
                }
                synchronized (this.runObj) {
                    this.runObj.notifyAll();
                }
                this.status = 3;
                this.shutdownObj.notifyAll();
            }
        }
    }
    
    protected void shutdownServants(final boolean b) {
        final HashSet set;
        synchronized (this) {
            set = new HashSet((Collection<? extends E>)this.requestDispatcherRegistry.getObjectAdapterFactories());
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            ((ObjectAdapterFactory)iterator.next()).shutdown(b);
        }
    }
    
    @Override
    public void checkShutdownState() {
        if (this.status == 4) {
            throw this.wrapper.orbDestroyed();
        }
        if (this.status == 3) {
            throw this.omgWrapper.badOperationAfterShutdown();
        }
    }
    
    @Override
    public boolean isDuringDispatch() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.isProcessingInvocation.get();
    }
    
    @Override
    public void startingDispatch() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.invocationObj) {
            this.isProcessingInvocation.set(Boolean.TRUE);
            ++this.numInvocations;
        }
    }
    
    @Override
    public void finishedDispatch() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.invocationObj) {
            --this.numInvocations;
            this.isProcessingInvocation.set(false);
            if (this.numInvocations == 0) {
                synchronized (this.waitForCompletionObj) {
                    this.waitForCompletionObj.notifyAll();
                }
            }
            else if (this.numInvocations < 0) {
                throw this.wrapper.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_YES);
            }
        }
    }
    
    @Override
    public void destroy() {
        int n = 0;
        synchronized (this) {
            n = ((this.status == 1) ? 1 : 0);
        }
        if (n != 0) {
            this.shutdown(true);
        }
        synchronized (this) {
            if (this.status < 4) {
                this.getCorbaTransportManager().close();
                this.getPIHandler().destroyInterceptors();
                this.status = 4;
            }
        }
        synchronized (this.threadPoolManagerAccessLock) {
            if (this.orbOwnsThreadPoolManager) {
                try {
                    this.threadpoolMgr.close();
                    this.threadpoolMgr = null;
                }
                catch (final IOException ex) {
                    this.wrapper.ioExceptionOnClose(ex);
                }
            }
        }
        try {
            this.monitoringManager.close();
            this.monitoringManager = null;
        }
        catch (final IOException ex2) {
            this.wrapper.ioExceptionOnClose(ex2);
        }
        CachedCodeBase.cleanCache(this);
        try {
            this.pihandler.close();
        }
        catch (final IOException ex3) {
            this.wrapper.ioExceptionOnClose(ex3);
        }
        super.destroy();
        this.badServerIdHandlerAccessLock = null;
        this.clientDelegateFactoryAccessorLock = null;
        this.corbaContactInfoListFactoryAccessLock = null;
        this.objectKeyFactoryAccessLock = null;
        this.legacyServerSocketManagerAccessLock = null;
        this.threadPoolManagerAccessLock = null;
        this.transportManager = null;
        this.legacyServerSocketManager = null;
        this.OAInvocationInfoStack = null;
        this.clientInvocationInfoStack = null;
        ORBImpl.codeBaseIOR = null;
        this.dynamicRequests = null;
        this.svResponseReceived = null;
        this.runObj = null;
        this.shutdownObj = null;
        this.waitForCompletionObj = null;
        this.invocationObj = null;
        this.isProcessingInvocation = null;
        this.typeCodeForClassMap = null;
        this.valueFactoryCache = null;
        this.orbVersionThreadLocal = null;
        this.requestDispatcherRegistry = null;
        this.copierManager = null;
        this.toaFactory = null;
        this.poaFactory = null;
        this.pihandler = null;
        this.configData = null;
        this.badServerIdHandler = null;
        this.clientDelegateFactory = null;
        this.corbaContactInfoListFactory = null;
        this.resolver = null;
        this.localResolver = null;
        this.insNamingDelegate = null;
        this.urlOperation = null;
        this.taggedComponentFactoryFinder = null;
        this.taggedProfileFactoryFinder = null;
        this.taggedProfileTemplateFactoryFinder = null;
        this.objectKeyFactory = null;
    }
    
    @Override
    public synchronized ValueFactory register_value_factory(final String s, final ValueFactory valueFactory) {
        this.checkShutdownState();
        if (s == null || valueFactory == null) {
            throw this.omgWrapper.unableRegisterValueFactory();
        }
        return this.valueFactoryCache.put(s, valueFactory);
    }
    
    @Override
    public synchronized void unregister_value_factory(final String s) {
        this.checkShutdownState();
        if (this.valueFactoryCache.remove(s) == null) {
            throw this.wrapper.nullParam();
        }
    }
    
    @Override
    public synchronized ValueFactory lookup_value_factory(final String s) {
        this.checkShutdownState();
        ValueFactory factory = this.valueFactoryCache.get(s);
        if (factory == null) {
            try {
                factory = Utility.getFactory(null, null, null, s);
            }
            catch (final MARSHAL marshal) {
                throw this.wrapper.unableFindValueFactory(marshal);
            }
        }
        return factory;
    }
    
    @Override
    public OAInvocationInfo peekInvocationInfo() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return (OAInvocationInfo)this.OAInvocationInfoStack.get().peek();
    }
    
    @Override
    public void pushInvocationInfo(final OAInvocationInfo oaInvocationInfo) {
        synchronized (this) {
            this.checkShutdownState();
        }
        this.OAInvocationInfoStack.get().push(oaInvocationInfo);
    }
    
    @Override
    public OAInvocationInfo popInvocationInfo() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return (OAInvocationInfo)this.OAInvocationInfoStack.get().pop();
    }
    
    @Override
    public void initBadServerIdHandler() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.badServerIdHandlerAccessLock) {
            final Class badServerIdHandler = this.configData.getBadServerIdHandler();
            if (badServerIdHandler != null) {
                try {
                    this.badServerIdHandler = badServerIdHandler.getConstructor(org.omg.CORBA.ORB.class).newInstance(this);
                }
                catch (final Exception ex) {
                    throw this.wrapper.errorInitBadserveridhandler(ex);
                }
            }
        }
    }
    
    @Override
    public void setBadServerIdHandler(final BadServerIdHandler badServerIdHandler) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.badServerIdHandlerAccessLock) {
            this.badServerIdHandler = badServerIdHandler;
        }
    }
    
    @Override
    public void handleBadServerId(final ObjectKey objectKey) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.badServerIdHandlerAccessLock) {
            if (this.badServerIdHandler == null) {
                throw this.wrapper.badServerId();
            }
            this.badServerIdHandler.handle(objectKey);
        }
    }
    
    @Override
    public synchronized Policy create_policy(final int n, final Any any) throws PolicyError {
        this.checkShutdownState();
        return this.pihandler.create_policy(n, any);
    }
    
    @Override
    public synchronized void connect(final org.omg.CORBA.Object object) {
        this.checkShutdownState();
        if (this.getTOAFactory() == null) {
            throw this.wrapper.noToa();
        }
        try {
            this.getTOAFactory().getTOA(Util.getCodebase(object.getClass())).connect(object);
        }
        catch (final Exception ex) {
            throw this.wrapper.orbConnectError(ex);
        }
    }
    
    @Override
    public synchronized void disconnect(final org.omg.CORBA.Object object) {
        this.checkShutdownState();
        if (this.getTOAFactory() == null) {
            throw this.wrapper.noToa();
        }
        try {
            this.getTOAFactory().getTOA().disconnect(object);
        }
        catch (final Exception ex) {
            throw this.wrapper.orbConnectError(ex);
        }
    }
    
    @Override
    public int getTransientServerId() {
        synchronized (this) {
            this.checkShutdownState();
        }
        if (this.configData.getORBServerIdPropertySpecified()) {
            return this.configData.getPersistentServerId();
        }
        return this.transientServerId;
    }
    
    @Override
    public RequestDispatcherRegistry getRequestDispatcherRegistry() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.requestDispatcherRegistry;
    }
    
    @Override
    public ServiceContextRegistry getServiceContextRegistry() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.serviceContextRegistry;
    }
    
    @Override
    public boolean isLocalHost(final String s) {
        synchronized (this) {
            this.checkShutdownState();
        }
        return s.equals(this.configData.getORBServerHost()) || s.equals(this.getLocalHostName());
    }
    
    @Override
    public boolean isLocalServerId(final int n, final int n2) {
        synchronized (this) {
            this.checkShutdownState();
        }
        if (n < 32 || n > 63) {
            return n2 == this.getTransientServerId();
        }
        if (ORBConstants.isTransient(n)) {
            return n2 == this.getTransientServerId();
        }
        return this.configData.getPersistentServerIdInitialized() && n2 == this.configData.getPersistentServerId();
    }
    
    private String getHostName(final String s) throws UnknownHostException {
        return InetAddress.getByName(s).getHostAddress();
    }
    
    private synchronized String getLocalHostName() {
        if (ORBImpl.localHostString == null) {
            try {
                ORBImpl.localHostString = InetAddress.getLocalHost().getHostAddress();
            }
            catch (final Exception ex) {
                throw this.wrapper.getLocalHostFailed(ex);
            }
        }
        return ORBImpl.localHostString;
    }
    
    @Override
    public synchronized boolean work_pending() {
        this.checkShutdownState();
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public synchronized void perform_work() {
        this.checkShutdownState();
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public synchronized void set_delegate(final Object o) {
        this.checkShutdownState();
        final POAFactory poaFactory = this.getPOAFactory();
        if (poaFactory != null) {
            ((Servant)o)._set_delegate(poaFactory.getDelegateImpl());
            return;
        }
        throw this.wrapper.noPoa();
    }
    
    @Override
    public ClientInvocationInfo createOrIncrementInvocationInfo() {
        synchronized (this) {
            this.checkShutdownState();
        }
        final StackImpl stackImpl = this.clientInvocationInfoStack.get();
        Object o = null;
        if (!stackImpl.empty()) {
            o = stackImpl.peek();
        }
        if (o == null || !((ClientInvocationInfo)o).isRetryInvocation()) {
            o = new CorbaInvocationInfo(this);
            this.startingDispatch();
            stackImpl.push(o);
        }
        ((ClientInvocationInfo)o).setIsRetryInvocation(false);
        ((ClientInvocationInfo)o).incrementEntryCount();
        return (ClientInvocationInfo)o;
    }
    
    @Override
    public void releaseOrDecrementInvocationInfo() {
        synchronized (this) {
            this.checkShutdownState();
        }
        final StackImpl stackImpl = this.clientInvocationInfoStack.get();
        if (!stackImpl.empty()) {
            final ClientInvocationInfo clientInvocationInfo = (ClientInvocationInfo)stackImpl.peek();
            clientInvocationInfo.decrementEntryCount();
            clientInvocationInfo.getEntryCount();
            if (clientInvocationInfo.getEntryCount() == 0) {
                if (!clientInvocationInfo.isRetryInvocation()) {
                    stackImpl.pop();
                }
                this.finishedDispatch();
            }
            return;
        }
        throw this.wrapper.invocationInfoStackEmpty();
    }
    
    @Override
    public ClientInvocationInfo getInvocationInfo() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return (ClientInvocationInfo)this.clientInvocationInfoStack.get().peek();
    }
    
    @Override
    public void setClientDelegateFactory(final ClientDelegateFactory clientDelegateFactory) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.clientDelegateFactoryAccessorLock) {
            this.clientDelegateFactory = clientDelegateFactory;
        }
    }
    
    @Override
    public ClientDelegateFactory getClientDelegateFactory() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.clientDelegateFactoryAccessorLock) {
            return this.clientDelegateFactory;
        }
    }
    
    @Override
    public void setCorbaContactInfoListFactory(final CorbaContactInfoListFactory corbaContactInfoListFactory) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.corbaContactInfoListFactoryAccessLock) {
            this.corbaContactInfoListFactory = corbaContactInfoListFactory;
        }
    }
    
    @Override
    public synchronized CorbaContactInfoListFactory getCorbaContactInfoListFactory() {
        this.checkShutdownState();
        return this.corbaContactInfoListFactory;
    }
    
    @Override
    public void setResolver(final Resolver resolver) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.resolverLock) {
            this.resolver = resolver;
        }
    }
    
    @Override
    public Resolver getResolver() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.resolverLock) {
            return this.resolver;
        }
    }
    
    @Override
    public void setLocalResolver(final LocalResolver localResolver) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.resolverLock) {
            this.localResolver = localResolver;
        }
    }
    
    @Override
    public LocalResolver getLocalResolver() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.resolverLock) {
            return this.localResolver;
        }
    }
    
    @Override
    public void setURLOperation(final Operation urlOperation) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.urlOperationLock) {
            this.urlOperation = urlOperation;
        }
    }
    
    @Override
    public Operation getURLOperation() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.urlOperationLock) {
            return this.urlOperation;
        }
    }
    
    @Override
    public void setINSDelegate(final CorbaServerRequestDispatcher insNamingDelegate) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.resolverLock) {
            this.insNamingDelegate = insNamingDelegate;
        }
    }
    
    @Override
    public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.taggedComponentFactoryFinder;
    }
    
    @Override
    public IdentifiableFactoryFinder getTaggedProfileFactoryFinder() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.taggedProfileFactoryFinder;
    }
    
    @Override
    public IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.taggedProfileTemplateFactoryFinder;
    }
    
    @Override
    public ObjectKeyFactory getObjectKeyFactory() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.objectKeyFactoryAccessLock) {
            return this.objectKeyFactory;
        }
    }
    
    @Override
    public void setObjectKeyFactory(final ObjectKeyFactory objectKeyFactory) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.objectKeyFactoryAccessLock) {
            this.objectKeyFactory = objectKeyFactory;
        }
    }
    
    @Override
    public TransportManager getTransportManager() {
        synchronized (this.transportManagerAccessorLock) {
            if (this.transportManager == null) {
                this.transportManager = new CorbaTransportManagerImpl(this);
            }
            return this.transportManager;
        }
    }
    
    @Override
    public CorbaTransportManager getCorbaTransportManager() {
        return (CorbaTransportManager)this.getTransportManager();
    }
    
    @Override
    public LegacyServerSocketManager getLegacyServerSocketManager() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.legacyServerSocketManagerAccessLock) {
            if (this.legacyServerSocketManager == null) {
                this.legacyServerSocketManager = new LegacyServerSocketManagerImpl(this);
            }
            return this.legacyServerSocketManager;
        }
    }
    
    @Override
    public void setThreadPoolManager(final ThreadPoolManager threadpoolMgr) {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.threadPoolManagerAccessLock) {
            this.threadpoolMgr = threadpoolMgr;
        }
    }
    
    @Override
    public ThreadPoolManager getThreadPoolManager() {
        synchronized (this) {
            this.checkShutdownState();
        }
        synchronized (this.threadPoolManagerAccessLock) {
            if (this.threadpoolMgr == null) {
                this.threadpoolMgr = new ThreadPoolManagerImpl();
                this.orbOwnsThreadPoolManager = true;
            }
            return this.threadpoolMgr;
        }
    }
    
    @Override
    public CopierManager getCopierManager() {
        synchronized (this) {
            this.checkShutdownState();
        }
        return this.copierManager;
    }
    
    @Override
    public void validateIORClass(final String s) {
        if (this.iorTypeCheckRegistry != null && !this.iorTypeCheckRegistry.isValidIORType(s)) {
            throw ORBUtilSystemException.get(this, "oa.ior").badStringifiedIor();
        }
    }
    
    static {
        ORBImpl.localHostString = null;
    }
    
    private static class ConfigParser extends ParserImplBase
    {
        public Class configurator;
        
        private ConfigParser() {
            this.configurator = ORBConfiguratorImpl.class;
        }
        
        public PropertyParser makeParser() {
            final PropertyParser propertyParser = new PropertyParser();
            propertyParser.add("com.sun.CORBA.ORBConfigurator", OperationFactory.classAction(), "configurator");
            return propertyParser;
        }
    }
}
