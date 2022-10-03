package org.apache.commons.collections4.map;

import java.util.Collection;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.io.Serializable;

public class PassiveExpiringMap<K, V> extends AbstractMapDecorator<K, V> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Map<Object, Long> expirationMap;
    private final ExpirationPolicy<K, V> expiringPolicy;
    
    private static long validateAndConvertToMillis(final long timeToLive, final TimeUnit timeUnit) {
        if (timeUnit == null) {
            throw new NullPointerException("Time unit must not be null");
        }
        return TimeUnit.MILLISECONDS.convert(timeToLive, timeUnit);
    }
    
    public PassiveExpiringMap() {
        this(-1L);
    }
    
    public PassiveExpiringMap(final ExpirationPolicy<K, V> expiringPolicy) {
        this(expiringPolicy, (Map)new HashMap());
    }
    
    public PassiveExpiringMap(final ExpirationPolicy<K, V> expiringPolicy, final Map<K, V> map) {
        super(map);
        this.expirationMap = new HashMap<Object, Long>();
        if (expiringPolicy == null) {
            throw new NullPointerException("Policy must not be null.");
        }
        this.expiringPolicy = expiringPolicy;
    }
    
    public PassiveExpiringMap(final long timeToLiveMillis) {
        this((ExpirationPolicy)new ConstantTimeToLiveExpirationPolicy(timeToLiveMillis), (Map)new HashMap());
    }
    
    public PassiveExpiringMap(final long timeToLiveMillis, final Map<K, V> map) {
        this((ExpirationPolicy)new ConstantTimeToLiveExpirationPolicy(timeToLiveMillis), map);
    }
    
    public PassiveExpiringMap(final long timeToLive, final TimeUnit timeUnit) {
        this(validateAndConvertToMillis(timeToLive, timeUnit));
    }
    
    public PassiveExpiringMap(final long timeToLive, final TimeUnit timeUnit, final Map<K, V> map) {
        this(validateAndConvertToMillis(timeToLive, timeUnit), map);
    }
    
    public PassiveExpiringMap(final Map<K, V> map) {
        this(-1L, map);
    }
    
    @Override
    public void clear() {
        super.clear();
        this.expirationMap.clear();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        this.removeIfExpired(key, this.now());
        return super.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        this.removeAllExpired(this.now());
        return super.containsValue(value);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        this.removeAllExpired(this.now());
        return super.entrySet();
    }
    
    @Override
    public V get(final Object key) {
        this.removeIfExpired(key, this.now());
        return super.get(key);
    }
    
    @Override
    public boolean isEmpty() {
        this.removeAllExpired(this.now());
        return super.isEmpty();
    }
    
    private boolean isExpired(final long now, final Long expirationTimeObject) {
        if (expirationTimeObject != null) {
            final long expirationTime = expirationTimeObject;
            return expirationTime >= 0L && now >= expirationTime;
        }
        return false;
    }
    
    @Override
    public Set<K> keySet() {
        this.removeAllExpired(this.now());
        return super.keySet();
    }
    
    private long now() {
        return System.currentTimeMillis();
    }
    
    @Override
    public V put(final K key, final V value) {
        final long expirationTime = this.expiringPolicy.expirationTime(key, value);
        this.expirationMap.put(key, expirationTime);
        return super.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> mapToCopy) {
        for (final Map.Entry<? extends K, ? extends V> entry : mapToCopy.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public V remove(final Object key) {
        this.expirationMap.remove(key);
        return super.remove(key);
    }
    
    private void removeAllExpired(final long now) {
        final Iterator<Map.Entry<Object, Long>> iter = this.expirationMap.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Object, Long> expirationEntry = iter.next();
            if (this.isExpired(now, expirationEntry.getValue())) {
                super.remove(expirationEntry.getKey());
                iter.remove();
            }
        }
    }
    
    private void removeIfExpired(final Object key, final long now) {
        final Long expirationTimeObject = this.expirationMap.get(key);
        if (this.isExpired(now, expirationTimeObject)) {
            this.remove(key);
        }
    }
    
    @Override
    public int size() {
        this.removeAllExpired(this.now());
        return super.size();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    @Override
    public Collection<V> values() {
        this.removeAllExpired(this.now());
        return super.values();
    }
    
    public static class ConstantTimeToLiveExpirationPolicy<K, V> implements ExpirationPolicy<K, V>
    {
        private static final long serialVersionUID = 1L;
        private final long timeToLiveMillis;
        
        public ConstantTimeToLiveExpirationPolicy() {
            this(-1L);
        }
        
        public ConstantTimeToLiveExpirationPolicy(final long timeToLiveMillis) {
            this.timeToLiveMillis = timeToLiveMillis;
        }
        
        public ConstantTimeToLiveExpirationPolicy(final long timeToLive, final TimeUnit timeUnit) {
            this(validateAndConvertToMillis(timeToLive, timeUnit));
        }
        
        @Override
        public long expirationTime(final K key, final V value) {
            if (this.timeToLiveMillis < 0L) {
                return -1L;
            }
            final long now = System.currentTimeMillis();
            if (now > Long.MAX_VALUE - this.timeToLiveMillis) {
                return -1L;
            }
            return now + this.timeToLiveMillis;
        }
    }
    
    public interface ExpirationPolicy<K, V> extends Serializable
    {
        long expirationTime(final K p0, final V p1);
    }
}
