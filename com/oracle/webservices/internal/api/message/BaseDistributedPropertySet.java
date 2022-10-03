package com.oracle.webservices.internal.api.message;

import java.util.HashSet;
import java.util.AbstractMap;
import java.util.Set;
import com.sun.istack.internal.Nullable;
import java.util.Iterator;
import com.sun.istack.internal.NotNull;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class BaseDistributedPropertySet extends BasePropertySet implements DistributedPropertySet
{
    private final Map<Class<? extends PropertySet>, PropertySet> satellites;
    private final Map<String, Object> viewthis;
    
    public BaseDistributedPropertySet() {
        this.satellites = new IdentityHashMap<Class<? extends PropertySet>, PropertySet>();
        this.viewthis = super.createView();
    }
    
    @Override
    public void addSatellite(@NotNull final PropertySet satellite) {
        this.addSatellite(satellite.getClass(), satellite);
    }
    
    @Override
    public void addSatellite(@NotNull final Class<? extends PropertySet> keyClass, @NotNull final PropertySet satellite) {
        this.satellites.put(keyClass, satellite);
    }
    
    @Override
    public void removeSatellite(final PropertySet satellite) {
        this.satellites.remove(satellite.getClass());
    }
    
    public void copySatelliteInto(@NotNull final DistributedPropertySet r) {
        for (final Map.Entry<Class<? extends PropertySet>, PropertySet> entry : this.satellites.entrySet()) {
            r.addSatellite(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void copySatelliteInto(final MessageContext r) {
        this.copySatelliteInto((DistributedPropertySet)r);
    }
    
    @Nullable
    @Override
    public <T extends PropertySet> T getSatellite(final Class<T> satelliteClass) {
        T satellite = (T)this.satellites.get(satelliteClass);
        if (satellite != null) {
            return satellite;
        }
        for (final PropertySet child : this.satellites.values()) {
            if (satelliteClass.isInstance(child)) {
                return satelliteClass.cast(child);
            }
            if (!DistributedPropertySet.class.isInstance(child)) {
                continue;
            }
            satellite = DistributedPropertySet.class.cast(child).getSatellite(satelliteClass);
            if (satellite != null) {
                return satellite;
            }
        }
        return null;
    }
    
    @Override
    public Map<Class<? extends PropertySet>, PropertySet> getSatellites() {
        return this.satellites;
    }
    
    @Override
    public Object get(final Object key) {
        for (final PropertySet child : this.satellites.values()) {
            if (child.supports(key)) {
                return child.get(key);
            }
        }
        return super.get(key);
    }
    
    @Override
    public Object put(final String key, final Object value) {
        for (final PropertySet child : this.satellites.values()) {
            if (child.supports(key)) {
                return child.put(key, value);
            }
        }
        return super.put(key, value);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        if (this.viewthis.containsKey(key)) {
            return true;
        }
        for (final PropertySet child : this.satellites.values()) {
            if (child.containsKey(key)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean supports(final Object key) {
        for (final PropertySet child : this.satellites.values()) {
            if (child.supports(key)) {
                return true;
            }
        }
        return super.supports(key);
    }
    
    @Override
    public Object remove(final Object key) {
        for (final PropertySet child : this.satellites.values()) {
            if (child.supports(key)) {
                return child.remove(key);
            }
        }
        return super.remove(key);
    }
    
    @Override
    protected void createEntrySet(final Set<Map.Entry<String, Object>> core) {
        super.createEntrySet(core);
        for (final PropertySet child : this.satellites.values()) {
            ((BasePropertySet)child).createEntrySet(core);
        }
    }
    
    protected Map<String, Object> asMapLocal() {
        return this.viewthis;
    }
    
    protected boolean supportsLocal(final Object key) {
        return super.supports(key);
    }
    
    @Override
    protected Map<String, Object> createView() {
        return new DistributedMapView();
    }
    
    class DistributedMapView extends AbstractMap<String, Object>
    {
        @Override
        public Object get(final Object key) {
            for (final PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                if (child.supports(key)) {
                    return child.get(key);
                }
            }
            return BaseDistributedPropertySet.this.viewthis.get(key);
        }
        
        @Override
        public int size() {
            int size = BaseDistributedPropertySet.this.viewthis.size();
            for (final PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                size += child.asMap().size();
            }
            return size;
        }
        
        @Override
        public boolean containsKey(final Object key) {
            if (BaseDistributedPropertySet.this.viewthis.containsKey(key)) {
                return true;
            }
            for (final PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                if (child.containsKey(key)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            final Set<Map.Entry<String, Object>> entries = new HashSet<Map.Entry<String, Object>>();
            for (final PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                for (final Map.Entry<String, Object> entry : child.asMap().entrySet()) {
                    entries.add(new SimpleImmutableEntry<String, Object>(entry.getKey(), entry.getValue()));
                }
            }
            for (final Map.Entry<String, Object> entry2 : BaseDistributedPropertySet.this.viewthis.entrySet()) {
                entries.add(new SimpleImmutableEntry<String, Object>(entry2.getKey(), entry2.getValue()));
            }
            return entries;
        }
        
        @Override
        public Object put(final String key, final Object value) {
            for (final PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                if (child.supports(key)) {
                    return child.put(key, value);
                }
            }
            return BaseDistributedPropertySet.this.viewthis.put(key, value);
        }
        
        @Override
        public void clear() {
            BaseDistributedPropertySet.this.satellites.clear();
            BaseDistributedPropertySet.this.viewthis.clear();
        }
        
        @Override
        public Object remove(final Object key) {
            for (final PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                if (child.supports(key)) {
                    return child.remove(key);
                }
            }
            return BaseDistributedPropertySet.this.viewthis.remove(key);
        }
    }
}
