package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.util.List;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.util.Set;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.concurrent.Executor;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.pipe.Codec;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import com.sun.org.glassfish.gmbal.AMXClient;
import javax.management.ObjectName;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.server.WSEndpoint;

public class WSEndpointMOMProxy extends WSEndpoint implements ManagedObjectManager
{
    @NotNull
    private final WSEndpointImpl wsEndpoint;
    private ManagedObjectManager managedObjectManager;
    
    WSEndpointMOMProxy(@NotNull final WSEndpointImpl wsEndpoint) {
        this.wsEndpoint = wsEndpoint;
    }
    
    @Override
    public ManagedObjectManager getManagedObjectManager() {
        if (this.managedObjectManager == null) {
            this.managedObjectManager = this.wsEndpoint.obtainManagedObjectManager();
        }
        return this.managedObjectManager;
    }
    
    void setManagedObjectManager(final ManagedObjectManager managedObjectManager) {
        this.managedObjectManager = managedObjectManager;
    }
    
    public boolean isInitialized() {
        return this.managedObjectManager != null;
    }
    
    public WSEndpointImpl getWsEndpoint() {
        return this.wsEndpoint;
    }
    
    @Override
    public void suspendJMXRegistration() {
        this.getManagedObjectManager().suspendJMXRegistration();
    }
    
    @Override
    public void resumeJMXRegistration() {
        this.getManagedObjectManager().resumeJMXRegistration();
    }
    
    @Override
    public boolean isManagedObject(final Object obj) {
        return this.getManagedObjectManager().isManagedObject(obj);
    }
    
    @Override
    public GmbalMBean createRoot() {
        return this.getManagedObjectManager().createRoot();
    }
    
    @Override
    public GmbalMBean createRoot(final Object root) {
        return this.getManagedObjectManager().createRoot(root);
    }
    
    @Override
    public GmbalMBean createRoot(final Object root, final String name) {
        return this.getManagedObjectManager().createRoot(root, name);
    }
    
    @Override
    public Object getRoot() {
        return this.getManagedObjectManager().getRoot();
    }
    
    @Override
    public GmbalMBean register(final Object parent, final Object obj, final String name) {
        return this.getManagedObjectManager().register(parent, obj, name);
    }
    
    @Override
    public GmbalMBean register(final Object parent, final Object obj) {
        return this.getManagedObjectManager().register(parent, obj);
    }
    
    @Override
    public GmbalMBean registerAtRoot(final Object obj, final String name) {
        return this.getManagedObjectManager().registerAtRoot(obj, name);
    }
    
    @Override
    public GmbalMBean registerAtRoot(final Object obj) {
        return this.getManagedObjectManager().registerAtRoot(obj);
    }
    
    @Override
    public void unregister(final Object obj) {
        this.getManagedObjectManager().unregister(obj);
    }
    
    @Override
    public ObjectName getObjectName(final Object obj) {
        return this.getManagedObjectManager().getObjectName(obj);
    }
    
    @Override
    public AMXClient getAMXClient(final Object obj) {
        return this.getManagedObjectManager().getAMXClient(obj);
    }
    
    @Override
    public Object getObject(final ObjectName oname) {
        return this.getManagedObjectManager().getObject(oname);
    }
    
    @Override
    public void stripPrefix(final String... str) {
        this.getManagedObjectManager().stripPrefix(str);
    }
    
    @Override
    public void stripPackagePrefix() {
        this.getManagedObjectManager().stripPackagePrefix();
    }
    
    @Override
    public String getDomain() {
        return this.getManagedObjectManager().getDomain();
    }
    
    @Override
    public void setMBeanServer(final MBeanServer server) {
        this.getManagedObjectManager().setMBeanServer(server);
    }
    
    @Override
    public MBeanServer getMBeanServer() {
        return this.getManagedObjectManager().getMBeanServer();
    }
    
    @Override
    public void setResourceBundle(final ResourceBundle rb) {
        this.getManagedObjectManager().setResourceBundle(rb);
    }
    
    @Override
    public ResourceBundle getResourceBundle() {
        return this.getManagedObjectManager().getResourceBundle();
    }
    
    @Override
    public void addAnnotation(final AnnotatedElement element, final Annotation annotation) {
        this.getManagedObjectManager().addAnnotation(element, annotation);
    }
    
    @Override
    public void setRegistrationDebug(final RegistrationDebugLevel level) {
        this.getManagedObjectManager().setRegistrationDebug(level);
    }
    
    @Override
    public void setRuntimeDebug(final boolean flag) {
        this.getManagedObjectManager().setRuntimeDebug(flag);
    }
    
    @Override
    public void setTypelibDebug(final int level) {
        this.getManagedObjectManager().setTypelibDebug(level);
    }
    
    @Override
    public void setJMXRegistrationDebug(final boolean flag) {
        this.getManagedObjectManager().setJMXRegistrationDebug(flag);
    }
    
    @Override
    public String dumpSkeleton(final Object obj) {
        return this.getManagedObjectManager().dumpSkeleton(obj);
    }
    
    @Override
    public void suppressDuplicateRootReport(final boolean suppressReport) {
        this.getManagedObjectManager().suppressDuplicateRootReport(suppressReport);
    }
    
    @Override
    public void close() throws IOException {
        this.getManagedObjectManager().close();
    }
    
    @Override
    public boolean equalsProxiedInstance(final WSEndpoint endpoint) {
        if (this.wsEndpoint == null) {
            return endpoint == null;
        }
        return this.wsEndpoint.equals(endpoint);
    }
    
    @Override
    public Codec createCodec() {
        return this.wsEndpoint.createCodec();
    }
    
    @Override
    public QName getServiceName() {
        return this.wsEndpoint.getServiceName();
    }
    
    @Override
    public QName getPortName() {
        return this.wsEndpoint.getPortName();
    }
    
    @Override
    public Class getImplementationClass() {
        return this.wsEndpoint.getImplementationClass();
    }
    
    @Override
    public WSBinding getBinding() {
        return this.wsEndpoint.getBinding();
    }
    
    @Override
    public Container getContainer() {
        return this.wsEndpoint.getContainer();
    }
    
    @Override
    public WSDLPort getPort() {
        return this.wsEndpoint.getPort();
    }
    
    @Override
    public void setExecutor(final Executor exec) {
        this.wsEndpoint.setExecutor(exec);
    }
    
    @Override
    public void schedule(final Packet request, final CompletionCallback callback, final FiberContextSwitchInterceptor interceptor) {
        this.wsEndpoint.schedule(request, callback, interceptor);
    }
    
    @Override
    public PipeHead createPipeHead() {
        return this.wsEndpoint.createPipeHead();
    }
    
    @Override
    public void dispose() {
        if (this.wsEndpoint != null) {
            this.wsEndpoint.dispose();
        }
    }
    
    @Override
    public ServiceDefinition getServiceDefinition() {
        return this.wsEndpoint.getServiceDefinition();
    }
    
    @Override
    public Set getComponentRegistry() {
        return this.wsEndpoint.getComponentRegistry();
    }
    
    @Override
    public SEIModel getSEIModel() {
        return this.wsEndpoint.getSEIModel();
    }
    
    @Override
    public PolicyMap getPolicyMap() {
        return this.wsEndpoint.getPolicyMap();
    }
    
    @Override
    public void closeManagedObjectManager() {
        this.wsEndpoint.closeManagedObjectManager();
    }
    
    @Override
    public ServerTubeAssemblerContext getAssemblerContext() {
        return this.wsEndpoint.getAssemblerContext();
    }
    
    @Override
    public EndpointReference getEndpointReference(final Class clazz, final String address, final String wsdlAddress, final Element... referenceParameters) {
        return this.wsEndpoint.getEndpointReference(clazz, address, wsdlAddress, referenceParameters);
    }
    
    @Override
    public EndpointReference getEndpointReference(final Class clazz, final String address, final String wsdlAddress, final List metadata, final List referenceParameters) {
        return this.wsEndpoint.getEndpointReference(clazz, address, wsdlAddress, metadata, referenceParameters);
    }
    
    @Override
    public OperationDispatcher getOperationDispatcher() {
        return this.wsEndpoint.getOperationDispatcher();
    }
    
    @Override
    public Packet createServiceResponseForException(final ThrowableContainerPropertySet tc, final Packet responsePacket, final SOAPVersion soapVersion, final WSDLPort wsdlPort, final SEIModel seiModel, final WSBinding binding) {
        return this.wsEndpoint.createServiceResponseForException(tc, responsePacket, soapVersion, wsdlPort, seiModel, binding);
    }
}
