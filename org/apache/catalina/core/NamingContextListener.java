package org.apache.catalina.core;

import org.apache.juli.logging.LogFactory;
import org.apache.naming.LookupRef;
import java.util.StringTokenizer;
import org.apache.catalina.Engine;
import org.apache.naming.ResourceLinkRef;
import org.apache.naming.ResourceEnvRef;
import org.apache.naming.ResourceRef;
import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.apache.naming.HandlerRef;
import org.apache.naming.ServiceRef;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.reflect.Constructor;
import org.apache.naming.EjbRef;
import org.apache.tomcat.util.descriptor.web.ResourceBase;
import javax.management.MalformedObjectNameException;
import org.apache.catalina.Host;
import org.apache.tomcat.util.descriptor.web.ContextTransaction;
import javax.naming.Reference;
import javax.naming.NameAlreadyBoundException;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import org.apache.naming.TransactionRef;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import java.beans.PropertyChangeEvent;
import org.apache.catalina.ContainerEvent;
import java.util.Iterator;
import java.util.Collection;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.naming.factory.ResourceLinkFactory;
import javax.naming.NamingException;
import org.apache.naming.ContextBindings;
import org.apache.naming.ContextAccessController;
import java.util.Hashtable;
import org.apache.catalina.Server;
import org.apache.catalina.LifecycleEvent;
import javax.management.ObjectName;
import java.util.HashMap;
import javax.naming.Context;
import org.apache.naming.NamingContext;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import java.beans.PropertyChangeListener;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.LifecycleListener;

public class NamingContextListener implements LifecycleListener, ContainerListener, PropertyChangeListener
{
    private static final Log log;
    protected static final StringManager sm;
    protected String name;
    protected Object container;
    private Object token;
    protected boolean initialized;
    protected NamingResourcesImpl namingResources;
    protected NamingContext namingContext;
    protected Context compCtx;
    protected Context envCtx;
    protected HashMap<String, ObjectName> objectNames;
    private boolean exceptionOnFailedWrite;
    
    public NamingContextListener() {
        this.name = "/";
        this.container = null;
        this.token = null;
        this.initialized = false;
        this.namingResources = null;
        this.namingContext = null;
        this.compCtx = null;
        this.envCtx = null;
        this.objectNames = new HashMap<String, ObjectName>();
        this.exceptionOnFailedWrite = true;
    }
    
    public boolean getExceptionOnFailedWrite() {
        return this.exceptionOnFailedWrite;
    }
    
    public void setExceptionOnFailedWrite(final boolean exceptionOnFailedWrite) {
        this.exceptionOnFailedWrite = exceptionOnFailedWrite;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Context getEnvContext() {
        return this.envCtx;
    }
    
    @Override
    public void lifecycleEvent(final LifecycleEvent event) {
        this.container = event.getLifecycle();
        if (this.container instanceof org.apache.catalina.Context) {
            this.namingResources = ((org.apache.catalina.Context)this.container).getNamingResources();
            this.token = ((org.apache.catalina.Context)this.container).getNamingToken();
        }
        else {
            if (!(this.container instanceof Server)) {
                return;
            }
            this.namingResources = ((Server)this.container).getGlobalNamingResources();
            this.token = ((Server)this.container).getNamingToken();
        }
        if ("configure_start".equals(event.getType())) {
            if (this.initialized) {
                return;
            }
            try {
                final Hashtable<String, Object> contextEnv = new Hashtable<String, Object>();
                this.namingContext = new NamingContext(contextEnv, this.getName());
                ContextAccessController.setSecurityToken(this.getName(), this.token);
                ContextAccessController.setSecurityToken(this.container, this.token);
                ContextBindings.bindContext(this.container, this.namingContext, this.token);
                if (NamingContextListener.log.isDebugEnabled()) {
                    NamingContextListener.log.debug((Object)("Bound " + this.container));
                }
                this.namingContext.setExceptionOnFailedWrite(this.getExceptionOnFailedWrite());
                ContextAccessController.setWritable(this.getName(), this.token);
                try {
                    this.createNamingContext();
                }
                catch (final NamingException e) {
                    NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.namingContextCreationFailed", new Object[] { e }));
                }
                this.namingResources.addPropertyChangeListener(this);
                if (this.container instanceof org.apache.catalina.Context) {
                    ContextAccessController.setReadOnly(this.getName());
                    try {
                        ContextBindings.bindClassLoader(this.container, this.token, ((org.apache.catalina.Context)this.container).getLoader().getClassLoader());
                    }
                    catch (final NamingException e) {
                        NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e }));
                    }
                }
                if (this.container instanceof Server) {
                    ResourceLinkFactory.setGlobalContext(this.namingContext);
                    try {
                        ContextBindings.bindClassLoader(this.container, this.token, this.getClass().getClassLoader());
                    }
                    catch (final NamingException e) {
                        NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e }));
                    }
                    if (this.container instanceof StandardServer) {
                        ((StandardServer)this.container).setGlobalNamingContext(this.namingContext);
                    }
                }
            }
            finally {
                this.initialized = true;
            }
        }
        else if ("configure_stop".equals(event.getType())) {
            if (!this.initialized) {
                return;
            }
            try {
                ContextAccessController.setWritable(this.getName(), this.token);
                ContextBindings.unbindContext(this.container, this.token);
                if (this.container instanceof org.apache.catalina.Context) {
                    ContextBindings.unbindClassLoader(this.container, this.token, ((org.apache.catalina.Context)this.container).getLoader().getClassLoader());
                }
                if (this.container instanceof Server) {
                    ContextBindings.unbindClassLoader(this.container, this.token, this.getClass().getClassLoader());
                }
                this.namingResources.removePropertyChangeListener(this);
                ContextAccessController.unsetSecurityToken(this.getName(), this.token);
                ContextAccessController.unsetSecurityToken(this.container, this.token);
                if (!this.objectNames.isEmpty()) {
                    final Collection<ObjectName> names = this.objectNames.values();
                    final Registry registry = Registry.getRegistry((Object)null, (Object)null);
                    for (final ObjectName objectName : names) {
                        registry.unregisterComponent(objectName);
                    }
                }
                final Context global = this.getGlobalNamingContext();
                if (global != null) {
                    ResourceLinkFactory.deregisterGlobalResourceAccess(global);
                }
            }
            finally {
                this.objectNames.clear();
                this.namingContext = null;
                this.envCtx = null;
                this.compCtx = null;
                this.initialized = false;
            }
        }
    }
    
    @Deprecated
    @Override
    public void containerEvent(final ContainerEvent event) {
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (!this.initialized) {
            return;
        }
        final Object source = event.getSource();
        if (source == this.namingResources) {
            ContextAccessController.setWritable(this.getName(), this.token);
            this.processGlobalResourcesChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            ContextAccessController.setReadOnly(this.getName());
        }
    }
    
    private void processGlobalResourcesChange(final String name, final Object oldValue, final Object newValue) {
        if (name.equals("ejb")) {
            if (oldValue != null) {
                final ContextEjb ejb = (ContextEjb)oldValue;
                if (ejb.getName() != null) {
                    this.removeEjb(ejb.getName());
                }
            }
            if (newValue != null) {
                final ContextEjb ejb = (ContextEjb)newValue;
                if (ejb.getName() != null) {
                    this.addEjb(ejb);
                }
            }
        }
        else if (name.equals("environment")) {
            if (oldValue != null) {
                final ContextEnvironment env = (ContextEnvironment)oldValue;
                if (env.getName() != null) {
                    this.removeEnvironment(env.getName());
                }
            }
            if (newValue != null) {
                final ContextEnvironment env = (ContextEnvironment)newValue;
                if (env.getName() != null) {
                    this.addEnvironment(env);
                }
            }
        }
        else if (name.equals("localEjb")) {
            if (oldValue != null) {
                final ContextLocalEjb ejb2 = (ContextLocalEjb)oldValue;
                if (ejb2.getName() != null) {
                    this.removeLocalEjb(ejb2.getName());
                }
            }
            if (newValue != null) {
                final ContextLocalEjb ejb2 = (ContextLocalEjb)newValue;
                if (ejb2.getName() != null) {
                    this.addLocalEjb(ejb2);
                }
            }
        }
        else if (name.equals("messageDestinationRef")) {
            if (oldValue != null) {
                final MessageDestinationRef mdr = (MessageDestinationRef)oldValue;
                if (mdr.getName() != null) {
                    this.removeMessageDestinationRef(mdr.getName());
                }
            }
            if (newValue != null) {
                final MessageDestinationRef mdr = (MessageDestinationRef)newValue;
                if (mdr.getName() != null) {
                    this.addMessageDestinationRef(mdr);
                }
            }
        }
        else if (name.equals("resource")) {
            if (oldValue != null) {
                final ContextResource resource = (ContextResource)oldValue;
                if (resource.getName() != null) {
                    this.removeResource(resource.getName());
                }
            }
            if (newValue != null) {
                final ContextResource resource = (ContextResource)newValue;
                if (resource.getName() != null) {
                    this.addResource(resource);
                }
            }
        }
        else if (name.equals("resourceEnvRef")) {
            if (oldValue != null) {
                final ContextResourceEnvRef resourceEnvRef = (ContextResourceEnvRef)oldValue;
                if (resourceEnvRef.getName() != null) {
                    this.removeResourceEnvRef(resourceEnvRef.getName());
                }
            }
            if (newValue != null) {
                final ContextResourceEnvRef resourceEnvRef = (ContextResourceEnvRef)newValue;
                if (resourceEnvRef.getName() != null) {
                    this.addResourceEnvRef(resourceEnvRef);
                }
            }
        }
        else if (name.equals("resourceLink")) {
            if (oldValue != null) {
                final ContextResourceLink rl = (ContextResourceLink)oldValue;
                if (rl.getName() != null) {
                    this.removeResourceLink(rl.getName());
                }
            }
            if (newValue != null) {
                final ContextResourceLink rl = (ContextResourceLink)newValue;
                if (rl.getName() != null) {
                    this.addResourceLink(rl);
                }
            }
        }
        else if (name.equals("service")) {
            if (oldValue != null) {
                final ContextService service = (ContextService)oldValue;
                if (service.getName() != null) {
                    this.removeService(service.getName());
                }
            }
            if (newValue != null) {
                final ContextService service = (ContextService)newValue;
                if (service.getName() != null) {
                    this.addService(service);
                }
            }
        }
    }
    
    private void createNamingContext() throws NamingException {
        if (this.container instanceof Server) {
            this.compCtx = this.namingContext;
            this.envCtx = this.namingContext;
        }
        else {
            this.compCtx = this.namingContext.createSubcontext("comp");
            this.envCtx = this.compCtx.createSubcontext("env");
        }
        if (NamingContextListener.log.isDebugEnabled()) {
            NamingContextListener.log.debug((Object)"Creating JNDI naming context");
        }
        if (this.namingResources == null) {
            (this.namingResources = new NamingResourcesImpl()).setContainer(this.container);
        }
        final ContextResourceLink[] resourceLinks = this.namingResources.findResourceLinks();
        for (int i = 0; i < resourceLinks.length; ++i) {
            this.addResourceLink(resourceLinks[i]);
        }
        final ContextResource[] resources = this.namingResources.findResources();
        for (int i = 0; i < resources.length; ++i) {
            this.addResource(resources[i]);
        }
        final ContextResourceEnvRef[] resourceEnvRefs = this.namingResources.findResourceEnvRefs();
        for (int i = 0; i < resourceEnvRefs.length; ++i) {
            this.addResourceEnvRef(resourceEnvRefs[i]);
        }
        final ContextEnvironment[] contextEnvironments = this.namingResources.findEnvironments();
        for (int i = 0; i < contextEnvironments.length; ++i) {
            this.addEnvironment(contextEnvironments[i]);
        }
        final ContextEjb[] ejbs = this.namingResources.findEjbs();
        for (int i = 0; i < ejbs.length; ++i) {
            this.addEjb(ejbs[i]);
        }
        final MessageDestinationRef[] mdrs = this.namingResources.findMessageDestinationRefs();
        for (int i = 0; i < mdrs.length; ++i) {
            this.addMessageDestinationRef(mdrs[i]);
        }
        final ContextService[] services = this.namingResources.findServices();
        for (int i = 0; i < services.length; ++i) {
            this.addService(services[i]);
        }
        if (this.container instanceof org.apache.catalina.Context) {
            try {
                final Reference ref = new TransactionRef();
                this.compCtx.bind("UserTransaction", ref);
                final ContextTransaction transaction = this.namingResources.getTransaction();
                if (transaction != null) {
                    final Iterator<String> params = transaction.listProperties();
                    while (params.hasNext()) {
                        final String paramName = params.next();
                        final String paramValue = (String)transaction.getProperty(paramName);
                        final StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                        ref.add(refAddr);
                    }
                }
            }
            catch (final NameAlreadyBoundException ex) {}
            catch (final NamingException e) {
                NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e }));
            }
        }
        if (this.container instanceof org.apache.catalina.Context) {
            try {
                this.compCtx.bind("Resources", ((org.apache.catalina.Context)this.container).getResources());
            }
            catch (final NamingException e) {
                NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e }));
            }
        }
    }
    
    protected ObjectName createObjectName(final ContextResource resource) throws MalformedObjectNameException {
        String domain = null;
        if (this.container instanceof StandardServer) {
            domain = ((StandardServer)this.container).getDomain();
        }
        else if (this.container instanceof ContainerBase) {
            domain = ((ContainerBase)this.container).getDomain();
        }
        if (domain == null) {
            domain = "Catalina";
        }
        ObjectName name = null;
        final String quotedResourceName = ObjectName.quote(resource.getName());
        if (this.container instanceof Server) {
            name = new ObjectName(domain + ":type=DataSource" + ",class=" + resource.getType() + ",name=" + quotedResourceName);
        }
        else if (this.container instanceof org.apache.catalina.Context) {
            String contextName = ((org.apache.catalina.Context)this.container).getName();
            if (!contextName.startsWith("/")) {
                contextName = "/" + contextName;
            }
            final Host host = (Host)((org.apache.catalina.Context)this.container).getParent();
            name = new ObjectName(domain + ":type=DataSource" + ",host=" + host.getName() + ",context=" + contextName + ",class=" + resource.getType() + ",name=" + quotedResourceName);
        }
        return name;
    }
    
    public void addEjb(final ContextEjb ejb) {
        Reference ref = this.lookForLookupRef((ResourceBase)ejb);
        if (ref == null) {
            ref = new EjbRef(ejb.getType(), ejb.getHome(), ejb.getRemote(), ejb.getLink());
            final Iterator<String> params = ejb.listProperties();
            while (params.hasNext()) {
                final String paramName = params.next();
                final String paramValue = (String)ejb.getProperty(paramName);
                final StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            this.createSubcontexts(this.envCtx, ejb.getName());
            this.envCtx.bind(ejb.getName(), ref);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e }));
        }
    }
    
    public void addEnvironment(final ContextEnvironment env) {
        Object value = this.lookForLookupRef((ResourceBase)env);
        if (value == null) {
            final String type = env.getType();
            try {
                if (type.equals("java.lang.String")) {
                    value = env.getValue();
                }
                else if (type.equals("java.lang.Byte")) {
                    if (env.getValue() == null) {
                        value = 0;
                    }
                    else {
                        value = Byte.decode(env.getValue());
                    }
                }
                else if (type.equals("java.lang.Short")) {
                    if (env.getValue() == null) {
                        value = 0;
                    }
                    else {
                        value = Short.decode(env.getValue());
                    }
                }
                else if (type.equals("java.lang.Integer")) {
                    if (env.getValue() == null) {
                        value = 0;
                    }
                    else {
                        value = Integer.decode(env.getValue());
                    }
                }
                else if (type.equals("java.lang.Long")) {
                    if (env.getValue() == null) {
                        value = 0L;
                    }
                    else {
                        value = Long.decode(env.getValue());
                    }
                }
                else if (type.equals("java.lang.Boolean")) {
                    value = Boolean.valueOf(env.getValue());
                }
                else if (type.equals("java.lang.Double")) {
                    if (env.getValue() == null) {
                        value = 0.0;
                    }
                    else {
                        value = Double.valueOf(env.getValue());
                    }
                }
                else if (type.equals("java.lang.Float")) {
                    if (env.getValue() == null) {
                        value = 0.0f;
                    }
                    else {
                        value = Float.valueOf(env.getValue());
                    }
                }
                else if (type.equals("java.lang.Character")) {
                    if (env.getValue() == null) {
                        value = '\0';
                    }
                    else {
                        if (env.getValue().length() != 1) {
                            throw new IllegalArgumentException();
                        }
                        value = env.getValue().charAt(0);
                    }
                }
                else {
                    value = this.constructEnvEntry(env.getType(), env.getValue());
                    if (value == null) {
                        NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.invalidEnvEntryType", new Object[] { env.getName() }));
                    }
                }
            }
            catch (final IllegalArgumentException e) {
                NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.invalidEnvEntryValue", new Object[] { env.getName() }));
            }
        }
        if (value != null) {
            try {
                if (NamingContextListener.log.isDebugEnabled()) {
                    NamingContextListener.log.debug((Object)NamingContextListener.sm.getString("naming.addEnvEntry", new Object[] { env.getName() }));
                }
                this.createSubcontexts(this.envCtx, env.getName());
                this.envCtx.bind(env.getName(), value);
            }
            catch (final NamingException e2) {
                NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.invalidEnvEntryValue", new Object[] { e2 }));
            }
        }
    }
    
    private Object constructEnvEntry(final String type, final String value) {
        try {
            final Class<?> clazz = Class.forName(type);
            Constructor<?> c = null;
            try {
                c = clazz.getConstructor(String.class);
                return c.newInstance(value);
            }
            catch (final NoSuchMethodException ex) {
                if (value.length() != 1) {
                    return null;
                }
                try {
                    c = clazz.getConstructor(Character.TYPE);
                    return c.newInstance(value.charAt(0));
                }
                catch (final NoSuchMethodException ex2) {}
            }
        }
        catch (final Exception ex3) {}
        return null;
    }
    
    public void addLocalEjb(final ContextLocalEjb localEjb) {
    }
    
    public void addMessageDestinationRef(final MessageDestinationRef mdr) {
    }
    
    public void addService(final ContextService service) {
        Reference ref = this.lookForLookupRef((ResourceBase)service);
        if (ref == null) {
            if (service.getWsdlfile() != null) {
                URL wsdlURL = null;
                try {
                    wsdlURL = new URL(service.getWsdlfile());
                }
                catch (final MalformedURLException ex) {}
                if (wsdlURL == null) {
                    try {
                        wsdlURL = ((org.apache.catalina.Context)this.container).getServletContext().getResource(service.getWsdlfile());
                    }
                    catch (final MalformedURLException ex2) {}
                }
                if (wsdlURL == null) {
                    try {
                        wsdlURL = ((org.apache.catalina.Context)this.container).getServletContext().getResource("/" + service.getWsdlfile());
                        NamingContextListener.log.debug((Object)("  Changing service ref wsdl file for /" + service.getWsdlfile()));
                    }
                    catch (final MalformedURLException e) {
                        NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.wsdlFailed", new Object[] { e }));
                    }
                }
                if (wsdlURL == null) {
                    service.setWsdlfile((String)null);
                }
                else {
                    service.setWsdlfile(wsdlURL.toString());
                }
            }
            if (service.getJaxrpcmappingfile() != null) {
                URL jaxrpcURL = null;
                try {
                    jaxrpcURL = new URL(service.getJaxrpcmappingfile());
                }
                catch (final MalformedURLException ex3) {}
                if (jaxrpcURL == null) {
                    try {
                        jaxrpcURL = ((org.apache.catalina.Context)this.container).getServletContext().getResource(service.getJaxrpcmappingfile());
                    }
                    catch (final MalformedURLException ex4) {}
                }
                if (jaxrpcURL == null) {
                    try {
                        jaxrpcURL = ((org.apache.catalina.Context)this.container).getServletContext().getResource("/" + service.getJaxrpcmappingfile());
                        NamingContextListener.log.debug((Object)("  Changing service ref jaxrpc file for /" + service.getJaxrpcmappingfile()));
                    }
                    catch (final MalformedURLException e) {
                        NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.wsdlFailed", new Object[] { e }));
                    }
                }
                if (jaxrpcURL == null) {
                    service.setJaxrpcmappingfile((String)null);
                }
                else {
                    service.setJaxrpcmappingfile(jaxrpcURL.toString());
                }
            }
            ref = new ServiceRef(service.getName(), service.getInterface(), service.getServiceqname(), service.getWsdlfile(), service.getJaxrpcmappingfile());
            final Iterator<String> portcomponent = service.getServiceendpoints();
            while (portcomponent.hasNext()) {
                final String serviceendpoint = portcomponent.next();
                StringRefAddr refAddr = new StringRefAddr("serviceendpointinterface", serviceendpoint);
                ref.add(refAddr);
                final String portlink = service.getPortlink(serviceendpoint);
                refAddr = new StringRefAddr("portcomponentlink", portlink);
                ref.add(refAddr);
            }
            final Iterator<String> handlers = service.getHandlers();
            while (handlers.hasNext()) {
                final String handlername = handlers.next();
                final ContextHandler handler = service.getHandler(handlername);
                final HandlerRef handlerRef = new HandlerRef(handlername, handler.getHandlerclass());
                final Iterator<String> localParts = handler.getLocalparts();
                while (localParts.hasNext()) {
                    final String localPart = localParts.next();
                    final String namespaceURI = handler.getNamespaceuri(localPart);
                    handlerRef.add(new StringRefAddr("handlerlocalpart", localPart));
                    handlerRef.add(new StringRefAddr("handlernamespace", namespaceURI));
                }
                final Iterator<String> params = handler.listProperties();
                while (params.hasNext()) {
                    final String paramName = params.next();
                    final String paramValue = (String)handler.getProperty(paramName);
                    handlerRef.add(new StringRefAddr("handlerparamname", paramName));
                    handlerRef.add(new StringRefAddr("handlerparamvalue", paramValue));
                }
                for (int i = 0; i < handler.getSoapRolesSize(); ++i) {
                    handlerRef.add(new StringRefAddr("handlersoaprole", handler.getSoapRole(i)));
                }
                for (int i = 0; i < handler.getPortNamesSize(); ++i) {
                    handlerRef.add(new StringRefAddr("handlerportname", handler.getPortName(i)));
                }
                ((ServiceRef)ref).addHandler(handlerRef);
            }
        }
        try {
            if (NamingContextListener.log.isDebugEnabled()) {
                NamingContextListener.log.debug((Object)("  Adding service ref " + service.getName() + "  " + ref));
            }
            this.createSubcontexts(this.envCtx, service.getName());
            this.envCtx.bind(service.getName(), ref);
        }
        catch (final NamingException e2) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e2 }));
        }
    }
    
    public void addResource(final ContextResource resource) {
        Reference ref = this.lookForLookupRef((ResourceBase)resource);
        if (ref == null) {
            ref = new ResourceRef(resource.getType(), resource.getDescription(), resource.getScope(), resource.getAuth(), resource.getSingleton());
            final Iterator<String> params = resource.listProperties();
            while (params.hasNext()) {
                final String paramName = params.next();
                final String paramValue = (String)resource.getProperty(paramName);
                final StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            if (NamingContextListener.log.isDebugEnabled()) {
                NamingContextListener.log.debug((Object)("  Adding resource ref " + resource.getName() + "  " + ref));
            }
            this.createSubcontexts(this.envCtx, resource.getName());
            this.envCtx.bind(resource.getName(), ref);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e }));
        }
        if (("javax.sql.DataSource".equals(ref.getClassName()) || "javax.sql.XADataSource".equals(ref.getClassName())) && resource.getSingleton()) {
            Object actualResource = null;
            try {
                final ObjectName on = this.createObjectName(resource);
                actualResource = this.envCtx.lookup(resource.getName());
                Registry.getRegistry((Object)null, (Object)null).registerComponent(actualResource, on, (String)null);
                this.objectNames.put(resource.getName(), on);
            }
            catch (final Exception e2) {
                NamingContextListener.log.warn((Object)NamingContextListener.sm.getString("naming.jmxRegistrationFailed", new Object[] { e2 }));
            }
            if (actualResource instanceof AutoCloseable && !resource.getCloseMethodConfigured()) {
                resource.setCloseMethod("close");
            }
        }
    }
    
    public void addResourceEnvRef(final ContextResourceEnvRef resourceEnvRef) {
        Reference ref = this.lookForLookupRef((ResourceBase)resourceEnvRef);
        if (ref == null) {
            ref = new ResourceEnvRef(resourceEnvRef.getType());
            final Iterator<String> params = resourceEnvRef.listProperties();
            while (params.hasNext()) {
                final String paramName = params.next();
                final String paramValue = (String)resourceEnvRef.getProperty(paramName);
                final StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            if (NamingContextListener.log.isDebugEnabled()) {
                NamingContextListener.log.debug((Object)NamingContextListener.sm.getString("naming.addResourceEnvRef", new Object[] { resourceEnvRef.getName() }));
            }
            this.createSubcontexts(this.envCtx, resourceEnvRef.getName());
            this.envCtx.bind(resourceEnvRef.getName(), ref);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e }));
        }
    }
    
    public void addResourceLink(final ContextResourceLink resourceLink) {
        final Reference ref = new ResourceLinkRef(resourceLink.getType(), resourceLink.getGlobal(), resourceLink.getFactory(), null);
        final Iterator<String> i = resourceLink.listProperties();
        while (i.hasNext()) {
            final String key = i.next();
            final Object val = resourceLink.getProperty(key);
            if (val != null) {
                final StringRefAddr refAddr = new StringRefAddr(key, val.toString());
                ref.add(refAddr);
            }
        }
        final Context ctx = "UserTransaction".equals(resourceLink.getName()) ? this.compCtx : this.envCtx;
        try {
            if (NamingContextListener.log.isDebugEnabled()) {
                NamingContextListener.log.debug((Object)("  Adding resource link " + resourceLink.getName()));
            }
            this.createSubcontexts(this.envCtx, resourceLink.getName());
            ctx.bind(resourceLink.getName(), ref);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.bindFailed", new Object[] { e }));
        }
        ResourceLinkFactory.registerGlobalResourceAccess(this.getGlobalNamingContext(), resourceLink.getName(), resourceLink.getGlobal());
    }
    
    private Context getGlobalNamingContext() {
        if (this.container instanceof org.apache.catalina.Context) {
            final Engine e = (Engine)((org.apache.catalina.Context)this.container).getParent().getParent();
            final Server s = e.getService().getServer();
            if (s != null) {
                return s.getGlobalNamingContext();
            }
        }
        return null;
    }
    
    public void removeEjb(final String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.unbindFailed", new Object[] { name }), (Throwable)e);
        }
    }
    
    public void removeEnvironment(final String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.unbindFailed", new Object[] { name }), (Throwable)e);
        }
    }
    
    public void removeLocalEjb(final String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.unbindFailed", new Object[] { name }), (Throwable)e);
        }
    }
    
    public void removeMessageDestinationRef(final String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.unbindFailed", new Object[] { name }), (Throwable)e);
        }
    }
    
    public void removeService(final String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.unbindFailed", new Object[] { name }), (Throwable)e);
        }
    }
    
    public void removeResource(final String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.unbindFailed", new Object[] { name }), (Throwable)e);
        }
        final ObjectName on = this.objectNames.get(name);
        if (on != null) {
            Registry.getRegistry((Object)null, (Object)null).unregisterComponent(on);
        }
    }
    
    public void removeResourceEnvRef(final String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.unbindFailed", new Object[] { name }), (Throwable)e);
        }
    }
    
    public void removeResourceLink(final String name) {
        try {
            this.envCtx.unbind(name);
        }
        catch (final NamingException e) {
            NamingContextListener.log.error((Object)NamingContextListener.sm.getString("naming.unbindFailed", new Object[] { name }), (Throwable)e);
        }
        ResourceLinkFactory.deregisterGlobalResourceAccess(this.getGlobalNamingContext(), name);
    }
    
    private void createSubcontexts(final Context ctx, final String name) throws NamingException {
        Context currentContext = ctx;
        final StringTokenizer tokenizer = new StringTokenizer(name, "/");
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if (!token.equals("") && tokenizer.hasMoreTokens()) {
                try {
                    currentContext = currentContext.createSubcontext(token);
                }
                catch (final NamingException e) {
                    currentContext = (Context)currentContext.lookup(token);
                }
            }
        }
    }
    
    private LookupRef lookForLookupRef(final ResourceBase resourceBase) {
        final String lookupName = resourceBase.getLookupName();
        if (lookupName != null && !lookupName.equals("")) {
            return new LookupRef(resourceBase.getType(), lookupName);
        }
        return null;
    }
    
    static {
        log = LogFactory.getLog((Class)NamingContextListener.class);
        sm = StringManager.getManager((Class)NamingContextListener.class);
    }
}
