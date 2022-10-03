package org.apache.catalina.ha.context;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.core.ApplicationContext;
import org.apache.juli.logging.LogFactory;
import javax.servlet.ServletContext;
import org.apache.catalina.Loader;
import org.apache.catalina.LifecycleException;
import java.util.Map;
import org.apache.catalina.tribes.tipis.ReplicatedMap;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.catalina.core.StandardContext;

public class ReplicatedContext extends StandardContext implements AbstractReplicatedMap.MapOwner
{
    private int mapSendOptions;
    private static final Log log;
    protected static final long DEFAULT_REPL_TIMEOUT = 15000L;
    private static final StringManager sm;
    
    public ReplicatedContext() {
        this.mapSendOptions = 2;
    }
    
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            final CatalinaCluster catclust = (CatalinaCluster)this.getCluster();
            if (catclust != null) {
                final ReplicatedMap<String, Object> map = (ReplicatedMap<String, Object>)new ReplicatedMap((AbstractReplicatedMap.MapOwner)this, catclust.getChannel(), 15000L, this.getName(), this.getClassLoaders());
                map.setChannelSendOptions(this.mapSendOptions);
                ((ReplApplContext)this.context).setAttributeMap((Map<String, Object>)map);
            }
        }
        catch (final Exception x) {
            ReplicatedContext.log.error((Object)ReplicatedContext.sm.getString("replicatedContext.startUnable", new Object[] { this.getName() }), (Throwable)x);
            throw new LifecycleException(ReplicatedContext.sm.getString("replicatedContext.startFailed", new Object[] { this.getName() }), (Throwable)x);
        }
    }
    
    protected synchronized void stopInternal() throws LifecycleException {
        final Map<String, Object> map = ((ReplApplContext)this.context).getAttributeMap();
        super.stopInternal();
        if (map instanceof ReplicatedMap) {
            ((ReplicatedMap)map).breakdown();
        }
    }
    
    public void setMapSendOptions(final int mapSendOptions) {
        this.mapSendOptions = mapSendOptions;
    }
    
    public int getMapSendOptions() {
        return this.mapSendOptions;
    }
    
    public ClassLoader[] getClassLoaders() {
        Loader loader = null;
        ClassLoader classLoader = null;
        loader = this.getLoader();
        if (loader != null) {
            classLoader = loader.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader == Thread.currentThread().getContextClassLoader()) {
            return new ClassLoader[] { classLoader };
        }
        return new ClassLoader[] { classLoader, Thread.currentThread().getContextClassLoader() };
    }
    
    public ServletContext getServletContext() {
        if (this.context == null) {
            this.context = new ReplApplContext(this);
            if (this.getAltDDName() != null) {
                this.context.setAttribute("org.apache.catalina.deploy.alt_dd", (Object)this.getAltDDName());
            }
        }
        return ((ReplApplContext)this.context).getFacade();
    }
    
    public void objectMadePrimary(final Object key, final Object value) {
    }
    
    static {
        log = LogFactory.getLog((Class)ReplicatedContext.class);
        sm = StringManager.getManager((Class)ReplicatedContext.class);
    }
    
    protected static class ReplApplContext extends ApplicationContext
    {
        protected final Map<String, Object> tomcatAttributes;
        
        public ReplApplContext(final ReplicatedContext context) {
            super((StandardContext)context);
            this.tomcatAttributes = new ConcurrentHashMap<String, Object>();
        }
        
        protected ReplicatedContext getParent() {
            return (ReplicatedContext)this.getContext();
        }
        
        protected ServletContext getFacade() {
            return super.getFacade();
        }
        
        public Map<String, Object> getAttributeMap() {
            return this.attributes;
        }
        
        public void setAttributeMap(final Map<String, Object> map) {
            this.attributes = map;
        }
        
        public void removeAttribute(final String name) {
            this.tomcatAttributes.remove(name);
            super.removeAttribute(name);
        }
        
        public void setAttribute(final String name, final Object value) {
            if (name == null) {
                throw new IllegalArgumentException(ReplicatedContext.sm.getString("applicationContext.setAttribute.namenull"));
            }
            if (value == null) {
                this.removeAttribute(name);
                return;
            }
            if (!this.getParent().getState().isAvailable() || "org.apache.jasper.runtime.JspApplicationContextImpl".equals(name)) {
                this.tomcatAttributes.put(name, value);
            }
            else {
                super.setAttribute(name, value);
            }
        }
        
        public Object getAttribute(final String name) {
            final Object obj = this.tomcatAttributes.get(name);
            if (obj == null) {
                return super.getAttribute(name);
            }
            return obj;
        }
        
        public Enumeration<String> getAttributeNames() {
            final Set<String> names = new HashSet<String>(this.attributes.keySet());
            return new MultiEnumeration<String>(new Enumeration[] { super.getAttributeNames(), Collections.enumeration(names) });
        }
    }
    
    protected static class MultiEnumeration<T> implements Enumeration<T>
    {
        private final Enumeration<T>[] enumerations;
        
        public MultiEnumeration(final Enumeration<T>[] enumerations) {
            this.enumerations = enumerations;
        }
        
        @Override
        public boolean hasMoreElements() {
            for (final Enumeration<T> enumeration : this.enumerations) {
                if (enumeration.hasMoreElements()) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public T nextElement() {
            for (final Enumeration<T> enumeration : this.enumerations) {
                if (enumeration.hasMoreElements()) {
                    return enumeration.nextElement();
                }
            }
            return null;
        }
    }
}
