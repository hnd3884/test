package io.netty.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.Arrays;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap implements AttributeMap
{
    private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, DefaultAttribute[]> ATTRIBUTES_UPDATER;
    private static final DefaultAttribute[] EMPTY_ATTRIBUTES;
    private volatile DefaultAttribute[] attributes;
    
    public DefaultAttributeMap() {
        this.attributes = DefaultAttributeMap.EMPTY_ATTRIBUTES;
    }
    
    private static int searchAttributeByKey(final DefaultAttribute[] sortedAttributes, final AttributeKey<?> key) {
        int low = 0;
        int high = sortedAttributes.length - 1;
        while (low <= high) {
            final int mid = low + high >>> 1;
            final DefaultAttribute midVal = sortedAttributes[mid];
            final AttributeKey midValKey = midVal.key;
            if (midValKey == key) {
                return mid;
            }
            final int midValKeyId = midValKey.id();
            final int keyId = key.id();
            assert midValKeyId != keyId;
            final boolean searchRight = midValKeyId < keyId;
            if (searchRight) {
                low = mid + 1;
            }
            else {
                high = mid - 1;
            }
        }
        return -(low + 1);
    }
    
    private static void orderedCopyOnInsert(final DefaultAttribute[] sortedSrc, final int srcLength, final DefaultAttribute[] copy, final DefaultAttribute toInsert) {
        final int id = toInsert.key.id();
        int i;
        for (i = srcLength - 1; i >= 0; --i) {
            final DefaultAttribute attribute = sortedSrc[i];
            assert attribute.key.id() != id;
            if (attribute.key.id() < id) {
                break;
            }
            copy[i + 1] = sortedSrc[i];
        }
        copy[i + 1] = toInsert;
        final int toCopy = i + 1;
        if (toCopy > 0) {
            System.arraycopy(sortedSrc, 0, copy, 0, toCopy);
        }
    }
    
    @Override
    public <T> Attribute<T> attr(final AttributeKey<T> key) {
        ObjectUtil.checkNotNull(key, "key");
        DefaultAttribute newAttribute = null;
        while (true) {
            final DefaultAttribute[] attributes = this.attributes;
            final int index = searchAttributeByKey(attributes, key);
            DefaultAttribute[] newAttributes;
            if (index >= 0) {
                final DefaultAttribute attribute = attributes[index];
                assert attribute.key() == key;
                if (!attribute.isRemoved()) {
                    return attribute;
                }
                if (newAttribute == null) {
                    newAttribute = new DefaultAttribute(this, (AttributeKey<T>)key);
                }
                final int count = attributes.length;
                newAttributes = Arrays.copyOf(attributes, count);
                newAttributes[index] = newAttribute;
            }
            else {
                if (newAttribute == null) {
                    newAttribute = new DefaultAttribute(this, (AttributeKey<T>)key);
                }
                final int count2 = attributes.length;
                newAttributes = new DefaultAttribute[count2 + 1];
                orderedCopyOnInsert(attributes, count2, newAttributes, newAttribute);
            }
            if (DefaultAttributeMap.ATTRIBUTES_UPDATER.compareAndSet(this, attributes, newAttributes)) {
                return newAttribute;
            }
        }
    }
    
    @Override
    public <T> boolean hasAttr(final AttributeKey<T> key) {
        ObjectUtil.checkNotNull(key, "key");
        return searchAttributeByKey(this.attributes, key) >= 0;
    }
    
    private <T> void removeAttributeIfMatch(final AttributeKey<T> key, final DefaultAttribute<T> value) {
        while (true) {
            final DefaultAttribute[] attributes = this.attributes;
            final int index = searchAttributeByKey(attributes, key);
            if (index < 0) {
                return;
            }
            final DefaultAttribute attribute = attributes[index];
            assert attribute.key() == key;
            if (attribute != value) {
                return;
            }
            final int count = attributes.length;
            final int newCount = count - 1;
            final DefaultAttribute[] newAttributes = (newCount == 0) ? DefaultAttributeMap.EMPTY_ATTRIBUTES : new DefaultAttribute[newCount];
            System.arraycopy(attributes, 0, newAttributes, 0, index);
            final int remaining = count - index - 1;
            if (remaining > 0) {
                System.arraycopy(attributes, index + 1, newAttributes, index, remaining);
            }
            if (DefaultAttributeMap.ATTRIBUTES_UPDATER.compareAndSet(this, attributes, newAttributes)) {
                return;
            }
        }
    }
    
    static {
        ATTRIBUTES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, (Class<DefaultAttribute[]>)DefaultAttribute[].class, "attributes");
        EMPTY_ATTRIBUTES = new DefaultAttribute[0];
    }
    
    private static final class DefaultAttribute<T> extends AtomicReference<T> implements Attribute<T>
    {
        private static final AtomicReferenceFieldUpdater<DefaultAttribute, DefaultAttributeMap> MAP_UPDATER;
        private static final long serialVersionUID = -2661411462200283011L;
        private volatile DefaultAttributeMap attributeMap;
        private final AttributeKey<T> key;
        
        DefaultAttribute(final DefaultAttributeMap attributeMap, final AttributeKey<T> key) {
            this.attributeMap = attributeMap;
            this.key = key;
        }
        
        @Override
        public AttributeKey<T> key() {
            return this.key;
        }
        
        private boolean isRemoved() {
            return this.attributeMap == null;
        }
        
        @Override
        public T setIfAbsent(final T value) {
            while (!this.compareAndSet(null, value)) {
                final T old = this.get();
                if (old != null) {
                    return old;
                }
            }
            return null;
        }
        
        @Override
        public T getAndRemove() {
            final DefaultAttributeMap attributeMap = this.attributeMap;
            final boolean removed = attributeMap != null && DefaultAttribute.MAP_UPDATER.compareAndSet(this, attributeMap, null);
            final T oldValue = this.getAndSet(null);
            if (removed) {
                attributeMap.removeAttributeIfMatch(this.key, (DefaultAttribute<Object>)this);
            }
            return oldValue;
        }
        
        @Override
        public void remove() {
            final DefaultAttributeMap attributeMap = this.attributeMap;
            final boolean removed = attributeMap != null && DefaultAttribute.MAP_UPDATER.compareAndSet(this, attributeMap, null);
            this.set(null);
            if (removed) {
                attributeMap.removeAttributeIfMatch(this.key, (DefaultAttribute<Object>)this);
            }
        }
        
        static {
            MAP_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultAttribute.class, DefaultAttributeMap.class, "attributeMap");
        }
    }
}
