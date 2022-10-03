package com.sun.corba.se.impl.monitoring;

import java.util.Iterator;
import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.sun.corba.se.spi.monitoring.MonitoredObject;

public class MonitoredObjectImpl implements MonitoredObject
{
    private final String name;
    private final String description;
    private Map children;
    private Map monitoredAttributes;
    private MonitoredObject parent;
    
    MonitoredObjectImpl(final String name, final String description) {
        this.children = new HashMap();
        this.monitoredAttributes = new HashMap();
        this.parent = null;
        this.name = name;
        this.description = description;
    }
    
    @Override
    public MonitoredObject getChild(final String s) {
        synchronized (this) {
            return this.children.get(s);
        }
    }
    
    @Override
    public Collection getChildren() {
        synchronized (this) {
            return this.children.values();
        }
    }
    
    @Override
    public void addChild(final MonitoredObject monitoredObject) {
        if (monitoredObject != null) {
            synchronized (this) {
                this.children.put(monitoredObject.getName(), monitoredObject);
                monitoredObject.setParent(this);
            }
        }
    }
    
    @Override
    public void removeChild(final String s) {
        if (s != null) {
            synchronized (this) {
                this.children.remove(s);
            }
        }
    }
    
    @Override
    public synchronized MonitoredObject getParent() {
        return this.parent;
    }
    
    @Override
    public synchronized void setParent(final MonitoredObject parent) {
        this.parent = parent;
    }
    
    @Override
    public MonitoredAttribute getAttribute(final String s) {
        synchronized (this) {
            return this.monitoredAttributes.get(s);
        }
    }
    
    @Override
    public Collection getAttributes() {
        synchronized (this) {
            return this.monitoredAttributes.values();
        }
    }
    
    @Override
    public void addAttribute(final MonitoredAttribute monitoredAttribute) {
        if (monitoredAttribute != null) {
            synchronized (this) {
                this.monitoredAttributes.put(monitoredAttribute.getName(), monitoredAttribute);
            }
        }
    }
    
    @Override
    public void removeAttribute(final String s) {
        if (s != null) {
            synchronized (this) {
                this.monitoredAttributes.remove(s);
            }
        }
    }
    
    @Override
    public void clearState() {
        synchronized (this) {
            final Iterator iterator = this.monitoredAttributes.values().iterator();
            while (iterator.hasNext()) {
                ((MonitoredAttribute)iterator.next()).clearState();
            }
            final Iterator iterator2 = this.children.values().iterator();
            while (iterator2.hasNext()) {
                ((MonitoredObject)iterator2.next()).clearState();
            }
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
}
