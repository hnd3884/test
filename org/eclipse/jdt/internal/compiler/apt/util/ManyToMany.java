package org.eclipse.jdt.internal.compiler.apt.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public class ManyToMany<T1, T2>
{
    private final Map<T1, Set<T2>> _forward;
    private final Map<T2, Set<T1>> _reverse;
    private boolean _dirty;
    
    public ManyToMany() {
        this._forward = new HashMap<T1, Set<T2>>();
        this._reverse = new HashMap<T2, Set<T1>>();
        this._dirty = false;
    }
    
    public synchronized boolean clear() {
        final boolean hadContent = !this._forward.isEmpty() || !this._reverse.isEmpty();
        this._reverse.clear();
        this._forward.clear();
        this._dirty |= hadContent;
        return hadContent;
    }
    
    public synchronized void clearDirtyBit() {
        this._dirty = false;
    }
    
    public synchronized boolean containsKey(final T1 key) {
        return this._forward.containsKey(key);
    }
    
    public synchronized boolean containsKeyValuePair(final T1 key, final T2 value) {
        final Set<T2> values = this._forward.get(key);
        return values != null && values.contains(value);
    }
    
    public synchronized boolean containsValue(final T2 value) {
        return this._reverse.containsKey(value);
    }
    
    public synchronized Set<T1> getKeys(final T2 value) {
        final Set<T1> keys = this._reverse.get(value);
        if (keys == null) {
            return Collections.emptySet();
        }
        return new HashSet<T1>((Collection<? extends T1>)keys);
    }
    
    public synchronized Set<T2> getValues(final T1 key) {
        final Set<T2> values = this._forward.get(key);
        if (values == null) {
            return Collections.emptySet();
        }
        return new HashSet<T2>((Collection<? extends T2>)values);
    }
    
    public synchronized Set<T1> getKeySet() {
        final Set<T1> keys = new HashSet<T1>((Collection<? extends T1>)this._forward.keySet());
        return keys;
    }
    
    public synchronized Set<T2> getValueSet() {
        final Set<T2> values = new HashSet<T2>((Collection<? extends T2>)this._reverse.keySet());
        return values;
    }
    
    public synchronized boolean isDirty() {
        return this._dirty;
    }
    
    public synchronized boolean keyHasOtherValues(final T1 key, final T2 value) {
        final Set<T2> values = this._forward.get(key);
        if (values == null) {
            return false;
        }
        final int size = values.size();
        return size != 0 && (size > 1 || !values.contains(value));
    }
    
    public synchronized boolean put(final T1 key, final T2 value) {
        Set<T2> values = this._forward.get(key);
        if (values == null) {
            values = new HashSet<T2>();
            this._forward.put(key, values);
        }
        final boolean added = values.add(value);
        this._dirty |= added;
        Set<T1> keys = this._reverse.get(value);
        if (keys == null) {
            keys = new HashSet<T1>();
            this._reverse.put(value, keys);
        }
        keys.add(key);
        assert this.checkIntegrity();
        return added;
    }
    
    public synchronized boolean remove(final T1 key, final T2 value) {
        final Set<T2> values = this._forward.get(key);
        if (values == null) {
            assert this.checkIntegrity();
            return false;
        }
        else {
            final boolean removed = values.remove(value);
            if (values.isEmpty()) {
                this._forward.remove(key);
            }
            if (removed) {
                this._dirty = true;
                final Set<T1> keys = this._reverse.get(value);
                keys.remove(key);
                if (keys.isEmpty()) {
                    this._reverse.remove(value);
                }
            }
            assert this.checkIntegrity();
            return removed;
        }
    }
    
    public synchronized boolean removeKey(final T1 key) {
        final Set<T2> values = this._forward.get(key);
        if (values == null) {
            assert this.checkIntegrity();
            return false;
        }
        else {
            for (final T2 value : values) {
                final Set<T1> keys = this._reverse.get(value);
                if (keys != null) {
                    keys.remove(key);
                    if (!keys.isEmpty()) {
                        continue;
                    }
                    this._reverse.remove(value);
                }
            }
            this._forward.remove(key);
            this._dirty = true;
            assert this.checkIntegrity();
            return true;
        }
    }
    
    public synchronized boolean removeValue(final T2 value) {
        final Set<T1> keys = this._reverse.get(value);
        if (keys == null) {
            assert this.checkIntegrity();
            return false;
        }
        else {
            for (final T1 key : keys) {
                final Set<T2> values = this._forward.get(key);
                if (values != null) {
                    values.remove(value);
                    if (!values.isEmpty()) {
                        continue;
                    }
                    this._forward.remove(key);
                }
            }
            this._reverse.remove(value);
            this._dirty = true;
            assert this.checkIntegrity();
            return true;
        }
    }
    
    public synchronized boolean valueHasOtherKeys(final T2 value, final T1 key) {
        final Set<T1> keys = this._reverse.get(key);
        if (keys == null) {
            return false;
        }
        final int size = keys.size();
        return size != 0 && (size > 1 || !keys.contains(key));
    }
    
    private boolean checkIntegrity() {
        for (final Map.Entry<T1, Set<T2>> entry : this._forward.entrySet()) {
            final Set<T2> values = entry.getValue();
            if (values.isEmpty()) {
                throw new IllegalStateException("Integrity compromised: forward map contains an empty set");
            }
            for (final T2 value : values) {
                final Set<T1> keys = this._reverse.get(value);
                if (keys == null || !keys.contains(entry.getKey())) {
                    throw new IllegalStateException("Integrity compromised: forward map contains an entry missing from reverse map: " + value);
                }
            }
        }
        for (final Map.Entry<T2, Set<T1>> entry2 : this._reverse.entrySet()) {
            final Set<T1> keys2 = entry2.getValue();
            if (keys2.isEmpty()) {
                throw new IllegalStateException("Integrity compromised: reverse map contains an empty set");
            }
            for (final T1 key : keys2) {
                final Set<T2> values2 = this._forward.get(key);
                if (values2 == null || !values2.contains(entry2.getKey())) {
                    throw new IllegalStateException("Integrity compromised: reverse map contains an entry missing from forward map: " + key);
                }
            }
        }
        return true;
    }
}
