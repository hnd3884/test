package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Policy;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.net.URL;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.pept.transport.TransportManager;
import org.omg.CORBA.portable.ValueFactory;
import java.rmi.RemoteException;
import java.rmi.Remote;
import org.omg.CORBA.Request;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Current;
import com.sun.corba.se.impl.corba.EnvironmentImpl;
import org.omg.CORBA.Environment;
import org.omg.CORBA.Context;
import com.sun.corba.se.impl.corba.ContextListImpl;
import org.omg.CORBA.ContextList;
import com.sun.corba.se.impl.corba.ExceptionListImpl;
import org.omg.CORBA.ExceptionList;
import com.sun.corba.se.impl.corba.NamedValueImpl;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import com.sun.corba.se.impl.corba.NVListImpl;
import org.omg.CORBA.NVList;
import com.sun.corba.se.impl.corba.AnyImpl;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.UnionMember;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.StructMember;
import sun.corba.OutputStreamFactory;
import org.omg.CORBA.portable.OutputStream;
import java.applet.Applet;
import java.util.Properties;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.orb.ORB;

public class ORBSingleton extends ORB
{
    private ORB fullORB;
    private static PresentationManager.StubFactoryFactory staticStubFactoryFactory;
    
    @Override
    public void set_parameters(final Properties properties) {
    }
    
    @Override
    protected void set_parameters(final Applet applet, final Properties properties) {
    }
    
    @Override
    protected void set_parameters(final String[] array, final Properties properties) {
    }
    
    @Override
    public OutputStream create_output_stream() {
        return OutputStreamFactory.newEncapsOutputStream(this);
    }
    
    @Override
    public TypeCode create_struct_tc(final String s, final String s2, final StructMember[] array) {
        return new TypeCodeImpl(this, 15, s, s2, array);
    }
    
    @Override
    public TypeCode create_union_tc(final String s, final String s2, final TypeCode typeCode, final UnionMember[] array) {
        return new TypeCodeImpl(this, 16, s, s2, typeCode, array);
    }
    
    @Override
    public TypeCode create_enum_tc(final String s, final String s2, final String[] array) {
        return new TypeCodeImpl(this, 17, s, s2, array);
    }
    
    @Override
    public TypeCode create_alias_tc(final String s, final String s2, final TypeCode typeCode) {
        return new TypeCodeImpl(this, 21, s, s2, typeCode);
    }
    
    @Override
    public TypeCode create_exception_tc(final String s, final String s2, final StructMember[] array) {
        return new TypeCodeImpl(this, 22, s, s2, array);
    }
    
    @Override
    public TypeCode create_interface_tc(final String s, final String s2) {
        return new TypeCodeImpl(this, 14, s, s2);
    }
    
    @Override
    public TypeCode create_string_tc(final int n) {
        return new TypeCodeImpl(this, 18, n);
    }
    
    @Override
    public TypeCode create_wstring_tc(final int n) {
        return new TypeCodeImpl(this, 27, n);
    }
    
    @Override
    public TypeCode create_sequence_tc(final int n, final TypeCode typeCode) {
        return new TypeCodeImpl(this, 19, n, typeCode);
    }
    
    @Override
    public TypeCode create_recursive_sequence_tc(final int n, final int n2) {
        return new TypeCodeImpl(this, 19, n, n2);
    }
    
    @Override
    public TypeCode create_array_tc(final int n, final TypeCode typeCode) {
        return new TypeCodeImpl(this, 20, n, typeCode);
    }
    
    @Override
    public TypeCode create_native_tc(final String s, final String s2) {
        return new TypeCodeImpl(this, 31, s, s2);
    }
    
    @Override
    public TypeCode create_abstract_interface_tc(final String s, final String s2) {
        return new TypeCodeImpl(this, 32, s, s2);
    }
    
    @Override
    public TypeCode create_fixed_tc(final short n, final short n2) {
        return new TypeCodeImpl(this, 28, n, n2);
    }
    
    @Override
    public TypeCode create_value_tc(final String s, final String s2, final short n, final TypeCode typeCode, final ValueMember[] array) {
        return new TypeCodeImpl(this, 29, s, s2, n, typeCode, array);
    }
    
    @Override
    public TypeCode create_recursive_tc(final String s) {
        return new TypeCodeImpl(this, s);
    }
    
    @Override
    public TypeCode create_value_box_tc(final String s, final String s2, final TypeCode typeCode) {
        return new TypeCodeImpl(this, 30, s, s2, typeCode);
    }
    
    @Override
    public TypeCode get_primitive_tc(final TCKind tcKind) {
        return this.get_primitive_tc(tcKind.value());
    }
    
    @Override
    public Any create_any() {
        return new AnyImpl(this);
    }
    
    @Override
    public NVList create_list(final int n) {
        return new NVListImpl(this, n);
    }
    
    @Override
    public NVList create_operation_list(final org.omg.CORBA.Object object) {
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public NamedValue create_named_value(final String s, final Any any, final int n) {
        return new NamedValueImpl(this, s, any, n);
    }
    
    @Override
    public ExceptionList create_exception_list() {
        return new ExceptionListImpl();
    }
    
    @Override
    public ContextList create_context_list() {
        return new ContextListImpl(this);
    }
    
    @Override
    public Context get_default_context() {
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public Environment create_environment() {
        return new EnvironmentImpl();
    }
    
    @Override
    public Current get_current() {
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public String[] list_initial_services() {
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public org.omg.CORBA.Object resolve_initial_references(final String s) throws InvalidName {
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public void register_initial_reference(final String s, final org.omg.CORBA.Object object) throws InvalidName {
        throw this.wrapper.genericNoImpl();
    }
    
    @Override
    public void send_multiple_requests_oneway(final Request[] array) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void send_multiple_requests_deferred(final Request[] array) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public boolean poll_next_response() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public Request get_next_response() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public String object_to_string(final org.omg.CORBA.Object object) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public org.omg.CORBA.Object string_to_object(final String s) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public Remote string_to_remote(final String s) throws RemoteException {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void connect(final org.omg.CORBA.Object object) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void disconnect(final org.omg.CORBA.Object object) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void run() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void shutdown(final boolean b) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    protected void shutdownServants(final boolean b) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    protected void destroyConnections() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void destroy() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public boolean work_pending() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void perform_work() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public ValueFactory register_value_factory(final String s, final ValueFactory valueFactory) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void unregister_value_factory(final String s) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public ValueFactory lookup_value_factory(final String s) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public TransportManager getTransportManager() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public CorbaTransportManager getCorbaTransportManager() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public LegacyServerSocketManager getLegacyServerSocketManager() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    private synchronized ORB getFullORB() {
        if (this.fullORB == null) {
            (this.fullORB = new ORBImpl()).set_parameters(new Properties());
        }
        return this.fullORB;
    }
    
    @Override
    public RequestDispatcherRegistry getRequestDispatcherRegistry() {
        return this.getFullORB().getRequestDispatcherRegistry();
    }
    
    @Override
    public ServiceContextRegistry getServiceContextRegistry() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public int getTransientServerId() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public int getORBInitialPort() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public String getORBInitialHost() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public String getORBServerHost() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public int getORBServerPort() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public CodeSetComponentInfo getCodeSetComponentInfo() {
        return new CodeSetComponentInfo();
    }
    
    @Override
    public boolean isLocalHost(final String s) {
        return false;
    }
    
    @Override
    public boolean isLocalServerId(final int n, final int n2) {
        return false;
    }
    
    @Override
    public ORBVersion getORBVersion() {
        return ORBVersionFactory.getORBVersion();
    }
    
    @Override
    public void setORBVersion(final ORBVersion orbVersion) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public String getAppletHost() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public URL getAppletCodeBase() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public int getHighWaterMark() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public int getLowWaterMark() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public int getNumberToReclaim() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public int getGIOPFragmentSize() {
        return 1024;
    }
    
    public int getGIOPBuffMgrStrategy(final GIOPVersion giopVersion) {
        return 0;
    }
    
    @Override
    public IOR getFVDCodeBaseIOR() {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public Policy create_policy(final int n, final Any any) throws PolicyError {
        throw new NO_IMPLEMENT();
    }
    
    public LegacyServerSocketEndPointInfo getServerEndpoint() {
        return null;
    }
    
    public void setPersistentServerId(final int n) {
    }
    
    @Override
    public TypeCodeImpl getTypeCodeForClass(final Class clazz) {
        return null;
    }
    
    @Override
    public void setTypeCodeForClass(final Class clazz, final TypeCodeImpl typeCodeImpl) {
    }
    
    public boolean alwaysSendCodeSetServiceContext() {
        return true;
    }
    
    @Override
    public boolean isDuringDispatch() {
        return false;
    }
    
    @Override
    public void notifyORB() {
    }
    
    @Override
    public PIHandler getPIHandler() {
        return null;
    }
    
    @Override
    public void checkShutdownState() {
    }
    
    @Override
    public void startingDispatch() {
    }
    
    @Override
    public void finishedDispatch() {
    }
    
    public void registerInitialReference(final String s, final Closure closure) {
    }
    
    @Override
    public ORBData getORBData() {
        return this.getFullORB().getORBData();
    }
    
    @Override
    public void setClientDelegateFactory(final ClientDelegateFactory clientDelegateFactory) {
    }
    
    @Override
    public ClientDelegateFactory getClientDelegateFactory() {
        return this.getFullORB().getClientDelegateFactory();
    }
    
    @Override
    public void setCorbaContactInfoListFactory(final CorbaContactInfoListFactory corbaContactInfoListFactory) {
    }
    
    @Override
    public CorbaContactInfoListFactory getCorbaContactInfoListFactory() {
        return this.getFullORB().getCorbaContactInfoListFactory();
    }
    
    @Override
    public Operation getURLOperation() {
        return null;
    }
    
    @Override
    public void setINSDelegate(final CorbaServerRequestDispatcher corbaServerRequestDispatcher) {
    }
    
    @Override
    public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() {
        return this.getFullORB().getTaggedComponentFactoryFinder();
    }
    
    @Override
    public IdentifiableFactoryFinder getTaggedProfileFactoryFinder() {
        return this.getFullORB().getTaggedProfileFactoryFinder();
    }
    
    @Override
    public IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder() {
        return this.getFullORB().getTaggedProfileTemplateFactoryFinder();
    }
    
    @Override
    public ObjectKeyFactory getObjectKeyFactory() {
        return this.getFullORB().getObjectKeyFactory();
    }
    
    @Override
    public void setObjectKeyFactory(final ObjectKeyFactory objectKeyFactory) {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    @Override
    public void handleBadServerId(final ObjectKey objectKey) {
    }
    
    @Override
    public OAInvocationInfo peekInvocationInfo() {
        return null;
    }
    
    @Override
    public void pushInvocationInfo(final OAInvocationInfo oaInvocationInfo) {
    }
    
    @Override
    public OAInvocationInfo popInvocationInfo() {
        return null;
    }
    
    @Override
    public ClientInvocationInfo createOrIncrementInvocationInfo() {
        return null;
    }
    
    @Override
    public void releaseOrDecrementInvocationInfo() {
    }
    
    @Override
    public ClientInvocationInfo getInvocationInfo() {
        return null;
    }
    
    public ConnectionCache getConnectionCache(final ContactInfo contactInfo) {
        return null;
    }
    
    @Override
    public void setResolver(final Resolver resolver) {
    }
    
    @Override
    public Resolver getResolver() {
        return null;
    }
    
    @Override
    public void setLocalResolver(final LocalResolver localResolver) {
    }
    
    @Override
    public LocalResolver getLocalResolver() {
        return null;
    }
    
    @Override
    public void setURLOperation(final Operation operation) {
    }
    
    @Override
    public void setBadServerIdHandler(final BadServerIdHandler badServerIdHandler) {
    }
    
    @Override
    public void initBadServerIdHandler() {
    }
    
    public Selector getSelector(final int n) {
        return null;
    }
    
    @Override
    public void setThreadPoolManager(final ThreadPoolManager threadPoolManager) {
    }
    
    @Override
    public ThreadPoolManager getThreadPoolManager() {
        return null;
    }
    
    @Override
    public CopierManager getCopierManager() {
        return null;
    }
    
    @Override
    public void validateIORClass(final String s) {
        this.getFullORB().validateIORClass(s);
    }
    
    static {
        ORBSingleton.staticStubFactoryFactory = PresentationDefaults.getStaticStubFactoryFactory();
    }
}
