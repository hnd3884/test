package com.sun.jmx.mbeanserver;

import javax.management.InstanceNotFoundException;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import javax.management.QueryExp;
import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.RuntimeOperationsException;
import java.util.HashMap;
import javax.management.ObjectName;
import javax.management.DynamicMBean;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;

public class Repository
{
    private final Map<String, Map<String, NamedObject>> domainTb;
    private volatile int nbElements;
    private final String domain;
    private final ReentrantReadWriteLock lock;
    
    private void addAllMatching(final Map<String, NamedObject> map, final Set<NamedObject> set, final ObjectNamePattern objectNamePattern) {
        synchronized (map) {
            for (final NamedObject namedObject : map.values()) {
                if (objectNamePattern.matchKeys(namedObject.getName())) {
                    set.add(namedObject);
                }
            }
        }
    }
    
    private void addNewDomMoi(final DynamicMBean dynamicMBean, final String s, final ObjectName objectName, final RegistrationContext registrationContext) {
        final HashMap hashMap = new HashMap();
        this.addMoiToTb(dynamicMBean, objectName, objectName.getCanonicalKeyPropertyListString(), hashMap, registrationContext);
        this.domainTb.put(s, hashMap);
        ++this.nbElements;
    }
    
    private void registering(final RegistrationContext registrationContext) {
        if (registrationContext == null) {
            return;
        }
        try {
            registrationContext.registering();
        }
        catch (final RuntimeOperationsException ex) {
            throw ex;
        }
        catch (final RuntimeException ex2) {
            throw new RuntimeOperationsException(ex2);
        }
    }
    
    private void unregistering(final RegistrationContext registrationContext, final ObjectName objectName) {
        if (registrationContext == null) {
            return;
        }
        try {
            registrationContext.unregistered();
        }
        catch (final Exception ex) {
            JmxProperties.MBEANSERVER_LOGGER.log(Level.FINE, "Unexpected exception while unregistering " + objectName, ex);
        }
    }
    
    private void addMoiToTb(final DynamicMBean dynamicMBean, final ObjectName objectName, final String s, final Map<String, NamedObject> map, final RegistrationContext registrationContext) {
        this.registering(registrationContext);
        map.put(s, new NamedObject(objectName, dynamicMBean));
    }
    
    private NamedObject retrieveNamedObject(final ObjectName objectName) {
        if (objectName.isPattern()) {
            return null;
        }
        String s = objectName.getDomain().intern();
        if (s.length() == 0) {
            s = this.domain;
        }
        final Map map = this.domainTb.get(s);
        if (map == null) {
            return null;
        }
        return (NamedObject)map.get(objectName.getCanonicalKeyPropertyListString());
    }
    
    public Repository(final String s) {
        this(s, true);
    }
    
    public Repository(final String s, final boolean b) {
        this.nbElements = 0;
        this.lock = new ReentrantReadWriteLock(b);
        this.domainTb = new HashMap<String, Map<String, NamedObject>>(5);
        if (s != null && s.length() != 0) {
            this.domain = s.intern();
        }
        else {
            this.domain = "DefaultDomain";
        }
        this.domainTb.put(this.domain, new HashMap<String, NamedObject>());
    }
    
    public String[] getDomains() {
        this.lock.readLock().lock();
        ArrayList list;
        try {
            list = new ArrayList(this.domainTb.size());
            for (final Map.Entry entry : this.domainTb.entrySet()) {
                final Map map = (Map)entry.getValue();
                if (map != null && map.size() != 0) {
                    list.add(entry.getKey());
                }
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        return (String[])list.toArray(new String[list.size()]);
    }
    
    public void addMBean(final DynamicMBean dynamicMBean, ObjectName objectName, final RegistrationContext registrationContext) throws InstanceAlreadyExistsException {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "addMBean", "name = " + objectName);
        }
        String s = objectName.getDomain().intern();
        if (s.length() == 0) {
            objectName = Util.newObjectName(this.domain + objectName.toString());
        }
        boolean b;
        if (s == this.domain) {
            b = true;
            s = this.domain;
        }
        else {
            b = false;
        }
        if (objectName.isPattern()) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Repository: cannot add mbean for pattern name " + objectName.toString()));
        }
        this.lock.writeLock().lock();
        try {
            if (!b && s.equals("JMImplementation") && this.domainTb.containsKey("JMImplementation")) {
                throw new RuntimeOperationsException(new IllegalArgumentException("Repository: domain name cannot be JMImplementation"));
            }
            final Map map = this.domainTb.get(s);
            if (map == null) {
                this.addNewDomMoi(dynamicMBean, s, objectName, registrationContext);
                return;
            }
            final String canonicalKeyPropertyListString = objectName.getCanonicalKeyPropertyListString();
            if (map.get(canonicalKeyPropertyListString) != null) {
                throw new InstanceAlreadyExistsException(objectName.toString());
            }
            ++this.nbElements;
            this.addMoiToTb(dynamicMBean, objectName, canonicalKeyPropertyListString, map, registrationContext);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    public boolean contains(final ObjectName objectName) {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "contains", " name = " + objectName);
        }
        this.lock.readLock().lock();
        try {
            return this.retrieveNamedObject(objectName) != null;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    public DynamicMBean retrieve(final ObjectName objectName) {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "retrieve", "name = " + objectName);
        }
        this.lock.readLock().lock();
        try {
            final NamedObject retrieveNamedObject = this.retrieveNamedObject(objectName);
            if (retrieveNamedObject == null) {
                return null;
            }
            return retrieveNamedObject.getObject();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    public Set<NamedObject> query(final ObjectName objectName, final QueryExp queryExp) {
        final HashSet set = new HashSet();
        ObjectName wildcard;
        if (objectName == null || objectName.getCanonicalName().length() == 0 || objectName.equals(ObjectName.WILDCARD)) {
            wildcard = ObjectName.WILDCARD;
        }
        else {
            wildcard = objectName;
        }
        this.lock.readLock().lock();
        try {
            if (!wildcard.isPattern()) {
                final NamedObject retrieveNamedObject = this.retrieveNamedObject(wildcard);
                if (retrieveNamedObject != null) {
                    set.add(retrieveNamedObject);
                }
                return set;
            }
            if (wildcard == ObjectName.WILDCARD) {
                final Iterator<Map<String, NamedObject>> iterator = this.domainTb.values().iterator();
                while (iterator.hasNext()) {
                    set.addAll(((Map<K, ? extends E>)iterator.next()).values());
                }
                return set;
            }
            final boolean b = wildcard.getCanonicalKeyPropertyListString().length() == 0;
            final ObjectNamePattern objectNamePattern = b ? null : new ObjectNamePattern(wildcard);
            if (wildcard.getDomain().length() == 0) {
                final Map map = this.domainTb.get(this.domain);
                if (b) {
                    set.addAll(map.values());
                }
                else {
                    this.addAllMatching(map, set, objectNamePattern);
                }
                return set;
            }
            if (wildcard.isDomainPattern()) {
                final String domain = wildcard.getDomain();
                for (final String s : this.domainTb.keySet()) {
                    if (Util.wildmatch(s, domain)) {
                        final Map map2 = this.domainTb.get(s);
                        if (b) {
                            set.addAll(map2.values());
                        }
                        else {
                            this.addAllMatching(map2, set, objectNamePattern);
                        }
                    }
                }
                return set;
            }
            final Map map3 = this.domainTb.get(wildcard.getDomain());
            if (map3 == null) {
                return Collections.emptySet();
            }
            if (b) {
                set.addAll(map3.values());
            }
            else {
                this.addAllMatching(map3, set, objectNamePattern);
            }
            return set;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    public void remove(final ObjectName objectName, final RegistrationContext registrationContext) throws InstanceNotFoundException {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "remove", "name = " + objectName);
        }
        String s = objectName.getDomain().intern();
        if (s.length() == 0) {
            s = this.domain;
        }
        this.lock.writeLock().lock();
        try {
            final Map map = this.domainTb.get(s);
            if (map == null) {
                throw new InstanceNotFoundException(objectName.toString());
            }
            if (map.remove(objectName.getCanonicalKeyPropertyListString()) == null) {
                throw new InstanceNotFoundException(objectName.toString());
            }
            --this.nbElements;
            if (map.isEmpty()) {
                this.domainTb.remove(s);
                if (s == this.domain) {
                    this.domainTb.put(this.domain, new HashMap<String, NamedObject>());
                }
            }
            this.unregistering(registrationContext, objectName);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    public Integer getCount() {
        return this.nbElements;
    }
    
    public String getDefaultDomain() {
        return this.domain;
    }
    
    private static final class ObjectNamePattern
    {
        private final String[] keys;
        private final String[] values;
        private final String properties;
        private final boolean isPropertyListPattern;
        private final boolean isPropertyValuePattern;
        public final ObjectName pattern;
        
        public ObjectNamePattern(final ObjectName objectName) {
            this(objectName.isPropertyListPattern(), objectName.isPropertyValuePattern(), objectName.getCanonicalKeyPropertyListString(), objectName.getKeyPropertyList(), objectName);
        }
        
        ObjectNamePattern(final boolean isPropertyListPattern, final boolean isPropertyValuePattern, final String properties, final Map<String, String> map, final ObjectName pattern) {
            this.isPropertyListPattern = isPropertyListPattern;
            this.isPropertyValuePattern = isPropertyValuePattern;
            this.properties = properties;
            final int size = map.size();
            this.keys = new String[size];
            this.values = new String[size];
            int n = 0;
            for (final Map.Entry entry : map.entrySet()) {
                this.keys[n] = (String)entry.getKey();
                this.values[n] = (String)entry.getValue();
                ++n;
            }
            this.pattern = pattern;
        }
        
        public boolean matchKeys(final ObjectName objectName) {
            if (this.isPropertyValuePattern && !this.isPropertyListPattern && objectName.getKeyPropertyList().size() != this.keys.length) {
                return false;
            }
            if (this.isPropertyValuePattern || this.isPropertyListPattern) {
                for (int i = this.keys.length - 1; i >= 0; --i) {
                    final String keyProperty = objectName.getKeyProperty(this.keys[i]);
                    if (keyProperty == null) {
                        return false;
                    }
                    if (this.isPropertyValuePattern && this.pattern.isPropertyValuePattern(this.keys[i])) {
                        if (!Util.wildmatch(keyProperty, this.values[i])) {
                            return false;
                        }
                    }
                    else if (!keyProperty.equals(this.values[i])) {
                        return false;
                    }
                }
                return true;
            }
            return objectName.getCanonicalKeyPropertyListString().equals(this.properties);
        }
    }
    
    public interface RegistrationContext
    {
        void registering();
        
        void unregistered();
    }
}
