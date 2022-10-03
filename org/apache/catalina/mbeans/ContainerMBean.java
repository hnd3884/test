package org.apache.catalina.mbeans;

import org.apache.catalina.ContainerListener;
import java.util.List;
import java.util.ArrayList;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.Valve;
import org.apache.catalina.LifecycleException;
import javax.management.MBeanException;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.startup.HostConfig;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.Container;
import org.apache.catalina.core.ContainerBase;

public class ContainerMBean extends BaseCatalinaMBean<ContainerBase>
{
    public void addChild(final String type, final String name) throws MBeanException {
        final Container contained = (Container)BaseCatalinaMBean.newInstance(type);
        contained.setName(name);
        if (contained instanceof StandardHost) {
            final HostConfig config = new HostConfig();
            contained.addLifecycleListener(config);
        }
        else if (contained instanceof StandardContext) {
            final ContextConfig config2 = new ContextConfig();
            contained.addLifecycleListener(config2);
        }
        boolean oldValue = true;
        final ContainerBase container = this.doGetManagedResource();
        try {
            oldValue = container.getStartChildren();
            container.setStartChildren(false);
            container.addChild(contained);
            contained.init();
        }
        catch (final LifecycleException e) {
            throw new MBeanException(e);
        }
        finally {
            if (container != null) {
                container.setStartChildren(oldValue);
            }
        }
    }
    
    public void removeChild(final String name) throws MBeanException {
        if (name != null) {
            final Container container = ((BaseCatalinaMBean<Container>)this).doGetManagedResource();
            final Container contained = container.findChild(name);
            container.removeChild(contained);
        }
    }
    
    public String addValve(final String valveType) throws MBeanException {
        final Valve valve = (Valve)BaseCatalinaMBean.newInstance(valveType);
        final Container container = ((BaseCatalinaMBean<Container>)this).doGetManagedResource();
        container.getPipeline().addValve(valve);
        if (valve instanceof JmxEnabled) {
            return ((JmxEnabled)valve).getObjectName().toString();
        }
        return null;
    }
    
    public void removeValve(final String valveName) throws MBeanException {
        final Container container = ((BaseCatalinaMBean<Container>)this).doGetManagedResource();
        ObjectName oname;
        try {
            oname = new ObjectName(valveName);
        }
        catch (final MalformedObjectNameException | NullPointerException e) {
            throw new MBeanException(e);
        }
        if (container != null) {
            final Valve[] arr$;
            final Valve[] valves = arr$ = container.getPipeline().getValves();
            for (final Valve valve : arr$) {
                if (valve instanceof JmxEnabled) {
                    final ObjectName voname = ((JmxEnabled)valve).getObjectName();
                    if (voname.equals(oname)) {
                        container.getPipeline().removeValve(valve);
                    }
                }
            }
        }
    }
    
    public void addLifecycleListener(final String type) throws MBeanException {
        final LifecycleListener listener = (LifecycleListener)BaseCatalinaMBean.newInstance(type);
        final Container container = ((BaseCatalinaMBean<Container>)this).doGetManagedResource();
        container.addLifecycleListener(listener);
    }
    
    public void removeLifecycleListeners(final String type) throws MBeanException {
        final Container container = ((BaseCatalinaMBean<Container>)this).doGetManagedResource();
        final LifecycleListener[] arr$;
        final LifecycleListener[] listeners = arr$ = container.findLifecycleListeners();
        for (final LifecycleListener listener : arr$) {
            if (listener.getClass().getName().equals(type)) {
                container.removeLifecycleListener(listener);
            }
        }
    }
    
    public String[] findLifecycleListenerNames() throws MBeanException {
        final Container container = ((BaseCatalinaMBean<Container>)this).doGetManagedResource();
        final List<String> result = new ArrayList<String>();
        final LifecycleListener[] arr$;
        final LifecycleListener[] listeners = arr$ = container.findLifecycleListeners();
        for (final LifecycleListener listener : arr$) {
            result.add(listener.getClass().getName());
        }
        return result.toArray(new String[0]);
    }
    
    public String[] findContainerListenerNames() throws MBeanException {
        final Container container = ((BaseCatalinaMBean<Container>)this).doGetManagedResource();
        final List<String> result = new ArrayList<String>();
        final ContainerListener[] arr$;
        final ContainerListener[] listeners = arr$ = container.findContainerListeners();
        for (final ContainerListener listener : arr$) {
            result.add(listener.getClass().getName());
        }
        return result.toArray(new String[0]);
    }
}
