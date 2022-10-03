package org.apache.catalina.deploy;

import org.apache.juli.logging.LogFactory;
import java.lang.reflect.Field;
import org.apache.catalina.util.Introspection;
import org.apache.catalina.Container;
import org.apache.catalina.JmxEnabled;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.tomcat.util.ExceptionUtils;
import javax.naming.NamingException;
import org.apache.naming.ContextBindings;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.LifecycleException;
import java.util.Iterator;
import java.beans.PropertyChangeListener;
import org.apache.catalina.Engine;
import org.apache.catalina.Context;
import org.apache.catalina.Server;
import org.apache.tomcat.util.descriptor.web.InjectionTarget;
import java.util.List;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.tomcat.util.descriptor.web.ResourceBase;
import java.util.HashSet;
import java.beans.PropertyChangeSupport;
import org.apache.tomcat.util.descriptor.web.ContextTransaction;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import java.util.HashMap;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.descriptor.web.NamingResources;
import java.io.Serializable;
import org.apache.catalina.util.LifecycleMBeanBase;

public class NamingResourcesImpl extends LifecycleMBeanBase implements Serializable, NamingResources
{
    private static final long serialVersionUID = 1L;
    private static final Log log;
    private static final StringManager sm;
    private volatile boolean resourceRequireExplicitRegistration;
    private Object container;
    private final Set<String> entries;
    private final Map<String, ContextEjb> ejbs;
    private final Map<String, ContextEnvironment> envs;
    private final Map<String, ContextLocalEjb> localEjbs;
    private final Map<String, MessageDestinationRef> mdrs;
    private final HashMap<String, ContextResourceEnvRef> resourceEnvRefs;
    private final HashMap<String, ContextResource> resources;
    private final HashMap<String, ContextResourceLink> resourceLinks;
    private final HashMap<String, ContextService> services;
    private ContextTransaction transaction;
    protected final PropertyChangeSupport support;
    
    public NamingResourcesImpl() {
        this.resourceRequireExplicitRegistration = false;
        this.container = null;
        this.entries = new HashSet<String>();
        this.ejbs = new HashMap<String, ContextEjb>();
        this.envs = new HashMap<String, ContextEnvironment>();
        this.localEjbs = new HashMap<String, ContextLocalEjb>();
        this.mdrs = new HashMap<String, MessageDestinationRef>();
        this.resourceEnvRefs = new HashMap<String, ContextResourceEnvRef>();
        this.resources = new HashMap<String, ContextResource>();
        this.resourceLinks = new HashMap<String, ContextResourceLink>();
        this.services = new HashMap<String, ContextService>();
        this.transaction = null;
        this.support = new PropertyChangeSupport(this);
    }
    
    public Object getContainer() {
        return this.container;
    }
    
    public void setContainer(final Object container) {
        this.container = container;
    }
    
    public void setTransaction(final ContextTransaction transaction) {
        this.transaction = transaction;
    }
    
    public ContextTransaction getTransaction() {
        return this.transaction;
    }
    
    public void addEjb(final ContextEjb ejb) {
        final String ejbLink = ejb.getLink();
        final String lookupName = ejb.getLookupName();
        if (ejbLink != null && ejbLink.length() > 0 && lookupName != null && lookupName.length() > 0) {
            throw new IllegalArgumentException(NamingResourcesImpl.sm.getString("namingResources.ejbLookupLink", new Object[] { ejb.getName() }));
        }
        if (this.entries.contains(ejb.getName())) {
            return;
        }
        this.entries.add(ejb.getName());
        synchronized (this.ejbs) {
            ejb.setNamingResources((NamingResources)this);
            this.ejbs.put(ejb.getName(), ejb);
        }
        this.support.firePropertyChange("ejb", null, ejb);
    }
    
    public void addEnvironment(final ContextEnvironment environment) {
        if (this.entries.contains(environment.getName())) {
            final ContextEnvironment ce = this.findEnvironment(environment.getName());
            final ContextResourceLink rl = this.findResourceLink(environment.getName());
            if (ce != null) {
                if (!ce.getOverride()) {
                    return;
                }
                this.removeEnvironment(environment.getName());
            }
            else {
                if (rl == null) {
                    return;
                }
                final NamingResourcesImpl global = this.getServer().getGlobalNamingResources();
                if (global.findEnvironment(rl.getGlobal()) != null) {
                    if (!global.findEnvironment(rl.getGlobal()).getOverride()) {
                        return;
                    }
                    this.removeResourceLink(environment.getName());
                }
            }
        }
        final List<InjectionTarget> injectionTargets = environment.getInjectionTargets();
        final String value = environment.getValue();
        final String lookupName = environment.getLookupName();
        if (injectionTargets != null && injectionTargets.size() > 0 && (value == null || value.length() == 0)) {
            return;
        }
        if (value != null && value.length() > 0 && lookupName != null && lookupName.length() > 0) {
            throw new IllegalArgumentException(NamingResourcesImpl.sm.getString("namingResources.envEntryLookupValue", new Object[] { environment.getName() }));
        }
        if (!this.checkResourceType((ResourceBase)environment)) {
            throw new IllegalArgumentException(NamingResourcesImpl.sm.getString("namingResources.resourceTypeFail", new Object[] { environment.getName(), environment.getType() }));
        }
        this.entries.add(environment.getName());
        synchronized (this.envs) {
            environment.setNamingResources((NamingResources)this);
            this.envs.put(environment.getName(), environment);
        }
        this.support.firePropertyChange("environment", null, environment);
        if (this.resourceRequireExplicitRegistration) {
            try {
                MBeanUtils.createMBean(environment);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanCreateFail", new Object[] { environment.getName() }), (Throwable)e);
            }
        }
    }
    
    private Server getServer() {
        if (this.container instanceof Server) {
            return (Server)this.container;
        }
        if (this.container instanceof Context) {
            final Engine engine = (Engine)((Context)this.container).getParent().getParent();
            return engine.getService().getServer();
        }
        return null;
    }
    
    public void addLocalEjb(final ContextLocalEjb ejb) {
        if (this.entries.contains(ejb.getName())) {
            return;
        }
        this.entries.add(ejb.getName());
        synchronized (this.localEjbs) {
            ejb.setNamingResources((NamingResources)this);
            this.localEjbs.put(ejb.getName(), ejb);
        }
        this.support.firePropertyChange("localEjb", null, ejb);
    }
    
    public void addMessageDestinationRef(final MessageDestinationRef mdr) {
        if (this.entries.contains(mdr.getName())) {
            return;
        }
        if (!this.checkResourceType((ResourceBase)mdr)) {
            throw new IllegalArgumentException(NamingResourcesImpl.sm.getString("namingResources.resourceTypeFail", new Object[] { mdr.getName(), mdr.getType() }));
        }
        this.entries.add(mdr.getName());
        synchronized (this.mdrs) {
            mdr.setNamingResources((NamingResources)this);
            this.mdrs.put(mdr.getName(), mdr);
        }
        this.support.firePropertyChange("messageDestinationRef", null, mdr);
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }
    
    public void addResource(final ContextResource resource) {
        if (this.entries.contains(resource.getName())) {
            return;
        }
        if (!this.checkResourceType((ResourceBase)resource)) {
            throw new IllegalArgumentException(NamingResourcesImpl.sm.getString("namingResources.resourceTypeFail", new Object[] { resource.getName(), resource.getType() }));
        }
        this.entries.add(resource.getName());
        synchronized (this.resources) {
            resource.setNamingResources((NamingResources)this);
            this.resources.put(resource.getName(), resource);
        }
        this.support.firePropertyChange("resource", null, resource);
        if (this.resourceRequireExplicitRegistration) {
            try {
                MBeanUtils.createMBean(resource);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanCreateFail", new Object[] { resource.getName() }), (Throwable)e);
            }
        }
    }
    
    public void addResourceEnvRef(final ContextResourceEnvRef resource) {
        if (this.entries.contains(resource.getName())) {
            return;
        }
        if (!this.checkResourceType((ResourceBase)resource)) {
            throw new IllegalArgumentException(NamingResourcesImpl.sm.getString("namingResources.resourceTypeFail", new Object[] { resource.getName(), resource.getType() }));
        }
        this.entries.add(resource.getName());
        synchronized (this.resourceEnvRefs) {
            resource.setNamingResources((NamingResources)this);
            this.resourceEnvRefs.put(resource.getName(), resource);
        }
        this.support.firePropertyChange("resourceEnvRef", null, resource);
    }
    
    public void addResourceLink(final ContextResourceLink resourceLink) {
        if (this.entries.contains(resourceLink.getName())) {
            return;
        }
        this.entries.add(resourceLink.getName());
        synchronized (this.resourceLinks) {
            resourceLink.setNamingResources((NamingResources)this);
            this.resourceLinks.put(resourceLink.getName(), resourceLink);
        }
        this.support.firePropertyChange("resourceLink", null, resourceLink);
        if (this.resourceRequireExplicitRegistration) {
            try {
                MBeanUtils.createMBean(resourceLink);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanCreateFail", new Object[] { resourceLink.getName() }), (Throwable)e);
            }
        }
    }
    
    public void addService(final ContextService service) {
        if (this.entries.contains(service.getName())) {
            return;
        }
        this.entries.add(service.getName());
        synchronized (this.services) {
            service.setNamingResources((NamingResources)this);
            this.services.put(service.getName(), service);
        }
        this.support.firePropertyChange("service", null, service);
    }
    
    public ContextEjb findEjb(final String name) {
        synchronized (this.ejbs) {
            return this.ejbs.get(name);
        }
    }
    
    public ContextEjb[] findEjbs() {
        synchronized (this.ejbs) {
            final ContextEjb[] results = new ContextEjb[this.ejbs.size()];
            return this.ejbs.values().toArray(results);
        }
    }
    
    public ContextEnvironment findEnvironment(final String name) {
        synchronized (this.envs) {
            return this.envs.get(name);
        }
    }
    
    public ContextEnvironment[] findEnvironments() {
        synchronized (this.envs) {
            final ContextEnvironment[] results = new ContextEnvironment[this.envs.size()];
            return this.envs.values().toArray(results);
        }
    }
    
    public ContextLocalEjb findLocalEjb(final String name) {
        synchronized (this.localEjbs) {
            return this.localEjbs.get(name);
        }
    }
    
    public ContextLocalEjb[] findLocalEjbs() {
        synchronized (this.localEjbs) {
            final ContextLocalEjb[] results = new ContextLocalEjb[this.localEjbs.size()];
            return this.localEjbs.values().toArray(results);
        }
    }
    
    public MessageDestinationRef findMessageDestinationRef(final String name) {
        synchronized (this.mdrs) {
            return this.mdrs.get(name);
        }
    }
    
    public MessageDestinationRef[] findMessageDestinationRefs() {
        synchronized (this.mdrs) {
            final MessageDestinationRef[] results = new MessageDestinationRef[this.mdrs.size()];
            return this.mdrs.values().toArray(results);
        }
    }
    
    public ContextResource findResource(final String name) {
        synchronized (this.resources) {
            return this.resources.get(name);
        }
    }
    
    public ContextResourceLink findResourceLink(final String name) {
        synchronized (this.resourceLinks) {
            return this.resourceLinks.get(name);
        }
    }
    
    public ContextResourceLink[] findResourceLinks() {
        synchronized (this.resourceLinks) {
            final ContextResourceLink[] results = new ContextResourceLink[this.resourceLinks.size()];
            return this.resourceLinks.values().toArray(results);
        }
    }
    
    public ContextResource[] findResources() {
        synchronized (this.resources) {
            final ContextResource[] results = new ContextResource[this.resources.size()];
            return this.resources.values().toArray(results);
        }
    }
    
    public ContextResourceEnvRef findResourceEnvRef(final String name) {
        synchronized (this.resourceEnvRefs) {
            return this.resourceEnvRefs.get(name);
        }
    }
    
    public ContextResourceEnvRef[] findResourceEnvRefs() {
        synchronized (this.resourceEnvRefs) {
            final ContextResourceEnvRef[] results = new ContextResourceEnvRef[this.resourceEnvRefs.size()];
            return this.resourceEnvRefs.values().toArray(results);
        }
    }
    
    public ContextService findService(final String name) {
        synchronized (this.services) {
            return this.services.get(name);
        }
    }
    
    public ContextService[] findServices() {
        synchronized (this.services) {
            final ContextService[] results = new ContextService[this.services.size()];
            return this.services.values().toArray(results);
        }
    }
    
    public void removeEjb(final String name) {
        this.entries.remove(name);
        ContextEjb ejb = null;
        synchronized (this.ejbs) {
            ejb = this.ejbs.remove(name);
        }
        if (ejb != null) {
            this.support.firePropertyChange("ejb", ejb, null);
            ejb.setNamingResources((NamingResources)null);
        }
    }
    
    public void removeEnvironment(final String name) {
        this.entries.remove(name);
        ContextEnvironment environment = null;
        synchronized (this.envs) {
            environment = this.envs.remove(name);
        }
        if (environment != null) {
            this.support.firePropertyChange("environment", environment, null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(environment);
                }
                catch (final Exception e) {
                    NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanDestroyFail", new Object[] { environment.getName() }), (Throwable)e);
                }
            }
            environment.setNamingResources((NamingResources)null);
        }
    }
    
    public void removeLocalEjb(final String name) {
        this.entries.remove(name);
        ContextLocalEjb localEjb = null;
        synchronized (this.localEjbs) {
            localEjb = this.localEjbs.remove(name);
        }
        if (localEjb != null) {
            this.support.firePropertyChange("localEjb", localEjb, null);
            localEjb.setNamingResources((NamingResources)null);
        }
    }
    
    public void removeMessageDestinationRef(final String name) {
        this.entries.remove(name);
        MessageDestinationRef mdr = null;
        synchronized (this.mdrs) {
            mdr = this.mdrs.remove(name);
        }
        if (mdr != null) {
            this.support.firePropertyChange("messageDestinationRef", mdr, null);
            mdr.setNamingResources((NamingResources)null);
        }
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }
    
    public void removeResource(final String name) {
        this.entries.remove(name);
        ContextResource resource = null;
        synchronized (this.resources) {
            resource = this.resources.remove(name);
        }
        if (resource != null) {
            this.support.firePropertyChange("resource", resource, null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(resource);
                }
                catch (final Exception e) {
                    NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanDestroyFail", new Object[] { resource.getName() }), (Throwable)e);
                }
            }
            resource.setNamingResources((NamingResources)null);
        }
    }
    
    public void removeResourceEnvRef(final String name) {
        this.entries.remove(name);
        ContextResourceEnvRef resourceEnvRef = null;
        synchronized (this.resourceEnvRefs) {
            resourceEnvRef = this.resourceEnvRefs.remove(name);
        }
        if (resourceEnvRef != null) {
            this.support.firePropertyChange("resourceEnvRef", resourceEnvRef, null);
            resourceEnvRef.setNamingResources((NamingResources)null);
        }
    }
    
    public void removeResourceLink(final String name) {
        this.entries.remove(name);
        ContextResourceLink resourceLink = null;
        synchronized (this.resourceLinks) {
            resourceLink = this.resourceLinks.remove(name);
        }
        if (resourceLink != null) {
            this.support.firePropertyChange("resourceLink", resourceLink, null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(resourceLink);
                }
                catch (final Exception e) {
                    NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanDestroyFail", new Object[] { resourceLink.getName() }), (Throwable)e);
                }
            }
            resourceLink.setNamingResources((NamingResources)null);
        }
    }
    
    public void removeService(final String name) {
        this.entries.remove(name);
        ContextService service = null;
        synchronized (this.services) {
            service = this.services.remove(name);
        }
        if (service != null) {
            this.support.firePropertyChange("service", service, null);
            service.setNamingResources((NamingResources)null);
        }
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        this.resourceRequireExplicitRegistration = true;
        for (final ContextResource cr : this.resources.values()) {
            try {
                MBeanUtils.createMBean(cr);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanCreateFail", new Object[] { cr.getName() }), (Throwable)e);
            }
        }
        for (final ContextEnvironment ce : this.envs.values()) {
            try {
                MBeanUtils.createMBean(ce);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanCreateFail", new Object[] { ce.getName() }), (Throwable)e);
            }
        }
        for (final ContextResourceLink crl : this.resourceLinks.values()) {
            try {
                MBeanUtils.createMBean(crl);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanCreateFail", new Object[] { crl.getName() }), (Throwable)e);
            }
        }
    }
    
    protected void startInternal() throws LifecycleException {
        this.fireLifecycleEvent("configure_start", null);
        this.setState(LifecycleState.STARTING);
    }
    
    protected void stopInternal() throws LifecycleException {
        this.cleanUp();
        this.setState(LifecycleState.STOPPING);
        this.fireLifecycleEvent("configure_stop", null);
    }
    
    private void cleanUp() {
        if (this.resources.size() == 0) {
            return;
        }
        javax.naming.Context ctxt;
        try {
            if (this.container instanceof Server) {
                ctxt = ((Server)this.container).getGlobalNamingContext();
            }
            else {
                ctxt = ContextBindings.getClassLoader();
                ctxt = (javax.naming.Context)ctxt.lookup("comp/env");
            }
        }
        catch (final NamingException e) {
            NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.cleanupNoContext", new Object[] { this.container }), (Throwable)e);
            return;
        }
        for (final ContextResource cr : this.resources.values()) {
            if (cr.getSingleton()) {
                final String closeMethod = cr.getCloseMethod();
                if (closeMethod == null || closeMethod.length() <= 0) {
                    continue;
                }
                final String name = cr.getName();
                Object resource;
                try {
                    resource = ctxt.lookup(name);
                }
                catch (final NamingException e2) {
                    NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.cleanupNoResource", new Object[] { cr.getName(), this.container }), (Throwable)e2);
                    continue;
                }
                this.cleanUp(resource, name, closeMethod);
            }
        }
    }
    
    private void cleanUp(final Object resource, final String name, final String closeMethod) {
        Method m = null;
        try {
            m = resource.getClass().getMethod(closeMethod, (Class<?>[])null);
        }
        catch (final SecurityException e) {
            NamingResourcesImpl.log.debug((Object)NamingResourcesImpl.sm.getString("namingResources.cleanupCloseSecurity", new Object[] { closeMethod, name, this.container }));
            return;
        }
        catch (final NoSuchMethodException e2) {
            NamingResourcesImpl.log.debug((Object)NamingResourcesImpl.sm.getString("namingResources.cleanupNoClose", new Object[] { name, this.container, closeMethod }));
            return;
        }
        try {
            m.invoke(resource, (Object[])null);
        }
        catch (final IllegalArgumentException | IllegalAccessException e3) {
            NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.cleanupCloseFailed", new Object[] { closeMethod, name, this.container }), (Throwable)e3);
        }
        catch (final InvocationTargetException e4) {
            final Throwable t = ExceptionUtils.unwrapInvocationTargetException((Throwable)e4);
            ExceptionUtils.handleThrowable(t);
            NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.cleanupCloseFailed", new Object[] { closeMethod, name, this.container }), t);
        }
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        this.resourceRequireExplicitRegistration = false;
        for (final ContextResourceLink crl : this.resourceLinks.values()) {
            try {
                MBeanUtils.destroyMBean(crl);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanDestroyFail", new Object[] { crl.getName() }), (Throwable)e);
            }
        }
        for (final ContextEnvironment ce : this.envs.values()) {
            try {
                MBeanUtils.destroyMBean(ce);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanDestroyFail", new Object[] { ce.getName() }), (Throwable)e);
            }
        }
        for (final ContextResource cr : this.resources.values()) {
            try {
                MBeanUtils.destroyMBean(cr);
            }
            catch (final Exception e) {
                NamingResourcesImpl.log.warn((Object)NamingResourcesImpl.sm.getString("namingResources.mbeanDestroyFail", new Object[] { cr.getName() }), (Throwable)e);
            }
        }
        super.destroyInternal();
    }
    
    @Override
    protected String getDomainInternal() {
        final Object c = this.getContainer();
        if (c instanceof JmxEnabled) {
            return ((JmxEnabled)c).getDomain();
        }
        return null;
    }
    
    @Override
    protected String getObjectNameKeyProperties() {
        final Object c = this.getContainer();
        if (c instanceof Container) {
            return "type=NamingResources" + ((Container)c).getMBeanKeyProperties();
        }
        return "type=NamingResources";
    }
    
    private boolean checkResourceType(final ResourceBase resource) {
        if (!(this.container instanceof Context)) {
            return true;
        }
        if (resource.getInjectionTargets() == null || resource.getInjectionTargets().size() == 0) {
            return true;
        }
        final Context context = (Context)this.container;
        final String typeName = resource.getType();
        Class<?> typeClass = null;
        if (typeName != null) {
            typeClass = Introspection.loadClass(context, typeName);
            if (typeClass == null) {
                return true;
            }
        }
        final Class<?> compatibleClass = this.getCompatibleType(context, resource, typeClass);
        if (compatibleClass == null) {
            return false;
        }
        resource.setType(compatibleClass.getCanonicalName());
        return true;
    }
    
    private Class<?> getCompatibleType(final Context context, final ResourceBase resource, final Class<?> typeClass) {
        Class<?> result = null;
        for (final InjectionTarget injectionTarget : resource.getInjectionTargets()) {
            final Class<?> clazz = Introspection.loadClass(context, injectionTarget.getTargetClass());
            if (clazz == null) {
                continue;
            }
            final String targetName = injectionTarget.getTargetName();
            Class<?> targetType = this.getSetterType(clazz, targetName);
            if (targetType == null) {
                targetType = this.getFieldType(clazz, targetName);
            }
            if (targetType == null) {
                continue;
            }
            targetType = Introspection.convertPrimitiveType(targetType);
            if (typeClass == null) {
                if (result == null) {
                    result = targetType;
                }
                else {
                    if (targetType.isAssignableFrom(result)) {
                        continue;
                    }
                    if (!result.isAssignableFrom(targetType)) {
                        return null;
                    }
                    result = targetType;
                }
            }
            else {
                if (!targetType.isAssignableFrom(typeClass)) {
                    return null;
                }
                result = typeClass;
            }
        }
        return result;
    }
    
    private Class<?> getSetterType(final Class<?> clazz, final String name) {
        final Method[] methods = Introspection.getDeclaredMethods(clazz);
        if (methods != null && methods.length > 0) {
            for (final Method method : methods) {
                if (Introspection.isValidSetter(method) && Introspection.getPropertyName(method).equals(name)) {
                    return method.getParameterTypes()[0];
                }
            }
        }
        return null;
    }
    
    private Class<?> getFieldType(final Class<?> clazz, final String name) {
        final Field[] fields = Introspection.getDeclaredFields(clazz);
        if (fields != null && fields.length > 0) {
            for (final Field field : fields) {
                if (field.getName().equals(name)) {
                    return field.getType();
                }
            }
        }
        return null;
    }
    
    static {
        log = LogFactory.getLog((Class)NamingResourcesImpl.class);
        sm = StringManager.getManager((Class)NamingResourcesImpl.class);
    }
}
