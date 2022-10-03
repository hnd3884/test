package com.oracle.webservices.internal.api.message;

import java.lang.reflect.InvocationTargetException;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.AbstractMap;
import java.util.HashSet;
import java.security.AccessController;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.util.Map;

public abstract class BasePropertySet implements PropertySet
{
    private Map<String, Object> mapView;
    
    protected BasePropertySet() {
    }
    
    protected abstract PropertyMap getPropertyMap();
    
    protected static PropertyMap parse(final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<PropertyMap>)new PrivilegedAction<PropertyMap>() {
            @Override
            public PropertyMap run() {
                final PropertyMap props = new PropertyMap();
                for (Class c = clazz; c != null; c = c.getSuperclass()) {
                    for (final Field f : c.getDeclaredFields()) {
                        final Property cp = f.getAnnotation(Property.class);
                        if (cp != null) {
                            for (final String value : cp.value()) {
                                ((HashMap<String, FieldAccessor>)props).put(value, new FieldAccessor(f, value));
                            }
                        }
                    }
                    for (final Method m : c.getDeclaredMethods()) {
                        final Property cp = m.getAnnotation(Property.class);
                        if (cp != null) {
                            final String name = m.getName();
                            assert name.startsWith("get") || name.startsWith("is");
                            final String setName = name.startsWith("is") ? ("set" + name.substring(2)) : ('s' + name.substring(1));
                            Method setter;
                            try {
                                setter = clazz.getMethod(setName, m.getReturnType());
                            }
                            catch (final NoSuchMethodException e) {
                                setter = null;
                            }
                            for (final String value2 : cp.value()) {
                                ((HashMap<String, MethodAccessor>)props).put(value2, new MethodAccessor(m, setter, value2));
                            }
                        }
                    }
                }
                return props;
            }
        });
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final Accessor sp = ((HashMap<K, Accessor>)this.getPropertyMap()).get(key);
        return sp != null && sp.get(this) != null;
    }
    
    @Override
    public Object get(final Object key) {
        final Accessor sp = ((HashMap<K, Accessor>)this.getPropertyMap()).get(key);
        if (sp != null) {
            return sp.get(this);
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }
    
    @Override
    public Object put(final String key, final Object value) {
        final Accessor sp = ((HashMap<K, Accessor>)this.getPropertyMap()).get(key);
        if (sp != null) {
            final Object old = sp.get(this);
            sp.set(this, value);
            return old;
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }
    
    @Override
    public boolean supports(final Object key) {
        return this.getPropertyMap().containsKey(key);
    }
    
    @Override
    public Object remove(final Object key) {
        final Accessor sp = ((HashMap<K, Accessor>)this.getPropertyMap()).get(key);
        if (sp != null) {
            final Object old = sp.get(this);
            sp.set(this, null);
            return old;
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }
    
    @Deprecated
    @Override
    public final Map<String, Object> createMapView() {
        final Set<Map.Entry<String, Object>> core = new HashSet<Map.Entry<String, Object>>();
        this.createEntrySet(core);
        return new AbstractMap<String, Object>() {
            @Override
            public Set<Map.Entry<String, Object>> entrySet() {
                return core;
            }
        };
    }
    
    @Override
    public Map<String, Object> asMap() {
        if (this.mapView == null) {
            this.mapView = this.createView();
        }
        return this.mapView;
    }
    
    protected Map<String, Object> createView() {
        return new MapView(this.mapAllowsAdditionalProperties());
    }
    
    protected boolean mapAllowsAdditionalProperties() {
        return false;
    }
    
    protected void createEntrySet(final Set<Map.Entry<String, Object>> core) {
        for (final Map.Entry<String, Accessor> e : this.getPropertyMap().entrySet()) {
            core.add(new Map.Entry<String, Object>() {
                @Override
                public String getKey() {
                    return e.getKey();
                }
                
                @Override
                public Object getValue() {
                    return e.getValue().get(BasePropertySet.this);
                }
                
                @Override
                public Object setValue(final Object value) {
                    final Accessor acc = e.getValue();
                    final Object old = acc.get(BasePropertySet.this);
                    acc.set(BasePropertySet.this, value);
                    return old;
                }
            });
        }
    }
    
    protected static class PropertyMap extends HashMap<String, Accessor>
    {
        transient PropertyMapEntry[] cachedEntries;
        
        protected PropertyMap() {
            this.cachedEntries = null;
        }
        
        PropertyMapEntry[] getPropertyMapEntries() {
            if (this.cachedEntries == null) {
                this.cachedEntries = this.createPropertyMapEntries();
            }
            return this.cachedEntries;
        }
        
        private PropertyMapEntry[] createPropertyMapEntries() {
            final PropertyMapEntry[] modelEntries = new PropertyMapEntry[this.size()];
            int i = 0;
            for (final Map.Entry<String, Accessor> e : this.entrySet()) {
                modelEntries[i++] = new PropertyMapEntry(e.getKey(), e.getValue());
            }
            return modelEntries;
        }
    }
    
    public static class PropertyMapEntry
    {
        String key;
        Accessor value;
        
        public PropertyMapEntry(final String k, final Accessor v) {
            this.key = k;
            this.value = v;
        }
    }
    
    static final class FieldAccessor implements Accessor
    {
        private final Field f;
        private final String name;
        
        protected FieldAccessor(final Field f, final String name) {
            (this.f = f).setAccessible(true);
            this.name = name;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public boolean hasValue(final PropertySet props) {
            return this.get(props) != null;
        }
        
        @Override
        public Object get(final PropertySet props) {
            try {
                return this.f.get(props);
            }
            catch (final IllegalAccessException e) {
                throw new AssertionError();
            }
        }
        
        @Override
        public void set(final PropertySet props, final Object value) {
            try {
                this.f.set(props, value);
            }
            catch (final IllegalAccessException e) {
                throw new AssertionError();
            }
        }
    }
    
    static final class MethodAccessor implements Accessor
    {
        @NotNull
        private final Method getter;
        @Nullable
        private final Method setter;
        private final String name;
        
        protected MethodAccessor(final Method getter, final Method setter, final String value) {
            this.getter = getter;
            this.setter = setter;
            this.name = value;
            getter.setAccessible(true);
            if (setter != null) {
                setter.setAccessible(true);
            }
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public boolean hasValue(final PropertySet props) {
            return this.get(props) != null;
        }
        
        @Override
        public Object get(final PropertySet props) {
            try {
                return this.getter.invoke(props, new Object[0]);
            }
            catch (final IllegalAccessException e) {
                throw new AssertionError();
            }
            catch (final InvocationTargetException e2) {
                this.handle(e2);
                return 0;
            }
        }
        
        @Override
        public void set(final PropertySet props, final Object value) {
            if (this.setter == null) {
                throw new ReadOnlyPropertyException(this.getName());
            }
            try {
                this.setter.invoke(props, value);
            }
            catch (final IllegalAccessException e) {
                throw new AssertionError();
            }
            catch (final InvocationTargetException e2) {
                this.handle(e2);
            }
        }
        
        private Exception handle(final InvocationTargetException e) {
            final Throwable t = e.getTargetException();
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            throw new Error(e);
        }
    }
    
    final class MapView extends HashMap<String, Object>
    {
        boolean extensible;
        
        MapView(final boolean extensible) {
            super(BasePropertySet.this.getPropertyMap().getPropertyMapEntries().length);
            this.extensible = extensible;
            this.initialize();
        }
        
        public void initialize() {
            final PropertyMapEntry[] propertyMapEntries;
            final PropertyMapEntry[] entries = propertyMapEntries = BasePropertySet.this.getPropertyMap().getPropertyMapEntries();
            for (final PropertyMapEntry entry : propertyMapEntries) {
                super.put(entry.key, entry.value);
            }
        }
        
        @Override
        public Object get(final Object key) {
            final Object o = super.get(key);
            if (o instanceof Accessor) {
                return ((Accessor)o).get(BasePropertySet.this);
            }
            return o;
        }
        
        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            final Set<Map.Entry<String, Object>> entries = new HashSet<Map.Entry<String, Object>>();
            for (final String key : ((HashMap<String, V>)this).keySet()) {
                entries.add(new SimpleImmutableEntry<String, Object>(key, this.get(key)));
            }
            return entries;
        }
        
        @Override
        public Object put(final String key, final Object value) {
            final Object o = super.get(key);
            if (o != null && o instanceof Accessor) {
                final Object oldValue = ((Accessor)o).get(BasePropertySet.this);
                ((Accessor)o).set(BasePropertySet.this, value);
                return oldValue;
            }
            if (this.extensible) {
                return super.put(key, value);
            }
            throw new IllegalStateException("Unknown property [" + key + "] for PropertySet [" + BasePropertySet.this.getClass().getName() + "]");
        }
        
        @Override
        public void clear() {
            for (final String key : ((HashMap<String, V>)this).keySet()) {
                this.remove(key);
            }
        }
        
        @Override
        public Object remove(final Object key) {
            final Object o = super.get(key);
            if (o instanceof Accessor) {
                ((Accessor)o).set(BasePropertySet.this, null);
            }
            return super.remove(key);
        }
    }
    
    protected interface Accessor
    {
        String getName();
        
        boolean hasValue(final PropertySet p0);
        
        Object get(final PropertySet p0);
        
        void set(final PropertySet p0, final Object p1);
    }
}
