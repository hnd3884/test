package javax.print.attribute;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.io.Serializable;

public class HashAttributeSet implements AttributeSet, Serializable
{
    private static final long serialVersionUID = 5311560590283707917L;
    private Class myInterface;
    private transient HashMap attrMap;
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        final Attribute[] array = this.toArray();
        objectOutputStream.writeInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            objectOutputStream.writeObject(array[i]);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.attrMap = new HashMap();
        for (int int1 = objectInputStream.readInt(), i = 0; i < int1; ++i) {
            this.add((Attribute)objectInputStream.readObject());
        }
    }
    
    public HashAttributeSet() {
        this(Attribute.class);
    }
    
    public HashAttributeSet(final Attribute attribute) {
        this(attribute, Attribute.class);
    }
    
    public HashAttributeSet(final Attribute[] array) {
        this(array, Attribute.class);
    }
    
    public HashAttributeSet(final AttributeSet set) {
        this(set, Attribute.class);
    }
    
    protected HashAttributeSet(final Class<?> myInterface) {
        this.attrMap = new HashMap();
        if (myInterface == null) {
            throw new NullPointerException("null interface");
        }
        this.myInterface = myInterface;
    }
    
    protected HashAttributeSet(final Attribute attribute, final Class<?> myInterface) {
        this.attrMap = new HashMap();
        if (myInterface == null) {
            throw new NullPointerException("null interface");
        }
        this.myInterface = myInterface;
        this.add(attribute);
    }
    
    protected HashAttributeSet(final Attribute[] array, final Class<?> myInterface) {
        this.attrMap = new HashMap();
        if (myInterface == null) {
            throw new NullPointerException("null interface");
        }
        this.myInterface = myInterface;
        for (int n = (array == null) ? 0 : array.length, i = 0; i < n; ++i) {
            this.add(array[i]);
        }
    }
    
    protected HashAttributeSet(final AttributeSet set, final Class<?> myInterface) {
        this.attrMap = new HashMap();
        this.myInterface = myInterface;
        if (set != null) {
            final Attribute[] array = set.toArray();
            for (int n = (array == null) ? 0 : array.length, i = 0; i < n; ++i) {
                this.add(array[i]);
            }
        }
    }
    
    @Override
    public Attribute get(final Class<?> clazz) {
        return this.attrMap.get(AttributeSetUtilities.verifyAttributeCategory(clazz, Attribute.class));
    }
    
    @Override
    public boolean add(final Attribute attribute) {
        return !attribute.equals(this.attrMap.put(attribute.getCategory(), AttributeSetUtilities.verifyAttributeValue(attribute, this.myInterface)));
    }
    
    @Override
    public boolean remove(final Class<?> clazz) {
        return clazz != null && AttributeSetUtilities.verifyAttributeCategory(clazz, Attribute.class) != null && this.attrMap.remove(clazz) != null;
    }
    
    @Override
    public boolean remove(final Attribute attribute) {
        return attribute != null && this.attrMap.remove(attribute.getCategory()) != null;
    }
    
    @Override
    public boolean containsKey(final Class<?> clazz) {
        return clazz != null && AttributeSetUtilities.verifyAttributeCategory(clazz, Attribute.class) != null && this.attrMap.get(clazz) != null;
    }
    
    @Override
    public boolean containsValue(final Attribute attribute) {
        return attribute != null && attribute instanceof Attribute && attribute.equals(this.attrMap.get(attribute.getCategory()));
    }
    
    @Override
    public boolean addAll(final AttributeSet set) {
        final Attribute[] array = set.toArray();
        boolean b = false;
        for (int i = 0; i < array.length; ++i) {
            final Attribute verifyAttributeValue = AttributeSetUtilities.verifyAttributeValue(array[i], this.myInterface);
            b = (!verifyAttributeValue.equals(this.attrMap.put(verifyAttributeValue.getCategory(), verifyAttributeValue)) || b);
        }
        return b;
    }
    
    @Override
    public int size() {
        return this.attrMap.size();
    }
    
    @Override
    public Attribute[] toArray() {
        final Attribute[] array = new Attribute[this.size()];
        this.attrMap.values().toArray(array);
        return array;
    }
    
    @Override
    public void clear() {
        this.attrMap.clear();
    }
    
    @Override
    public boolean isEmpty() {
        return this.attrMap.isEmpty();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof AttributeSet)) {
            return false;
        }
        final AttributeSet set = (AttributeSet)o;
        if (set.size() != this.size()) {
            return false;
        }
        final Attribute[] array = this.toArray();
        for (int i = 0; i < array.length; ++i) {
            if (!set.containsValue(array[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        final Attribute[] array = this.toArray();
        for (int i = 0; i < array.length; ++i) {
            n += array[i].hashCode();
        }
        return n;
    }
}
