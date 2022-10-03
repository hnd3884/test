package javax.management.openmbean;

import java.io.IOException;
import sun.misc.SharedSecrets;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Iterator;
import com.sun.jmx.mbeanserver.Util;
import java.util.Collection;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.Serializable;
import java.util.Map;

public class TabularDataSupport implements TabularData, Map<Object, Object>, Cloneable, Serializable
{
    static final long serialVersionUID = 5720150593236309827L;
    private Map<Object, CompositeData> dataMap;
    private final TabularType tabularType;
    private transient String[] indexNamesArray;
    
    public TabularDataSupport(final TabularType tabularType) {
        this(tabularType, 16, 0.75f);
    }
    
    public TabularDataSupport(final TabularType tabularType, final int n, final float n2) {
        if (tabularType == null) {
            throw new IllegalArgumentException("Argument tabularType cannot be null.");
        }
        this.tabularType = tabularType;
        final List<String> indexNames = tabularType.getIndexNames();
        this.indexNamesArray = indexNames.toArray(new String[indexNames.size()]);
        this.dataMap = ("true".equalsIgnoreCase(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.tabular.data.hash.map"))) ? new HashMap<Object, CompositeData>(n, n2) : new LinkedHashMap<Object, CompositeData>(n, n2));
    }
    
    @Override
    public TabularType getTabularType() {
        return this.tabularType;
    }
    
    @Override
    public Object[] calculateIndex(final CompositeData compositeData) {
        this.checkValueType(compositeData);
        return this.internalCalculateIndex(compositeData).toArray();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        Object[] array;
        try {
            array = (Object[])o;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        return this.containsKey(array);
    }
    
    @Override
    public boolean containsKey(final Object[] array) {
        return array != null && this.dataMap.containsKey(Arrays.asList(array));
    }
    
    @Override
    public boolean containsValue(final CompositeData compositeData) {
        return this.dataMap.containsValue(compositeData);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.dataMap.containsValue(o);
    }
    
    @Override
    public Object get(final Object o) {
        return this.get((Object[])o);
    }
    
    @Override
    public CompositeData get(final Object[] array) {
        this.checkKeyType(array);
        return this.dataMap.get(Arrays.asList(array));
    }
    
    @Override
    public Object put(final Object o, final Object o2) {
        this.internalPut((CompositeData)o2);
        return o2;
    }
    
    @Override
    public void put(final CompositeData compositeData) {
        this.internalPut(compositeData);
    }
    
    private CompositeData internalPut(final CompositeData compositeData) {
        return this.dataMap.put(this.checkValueAndIndex(compositeData), compositeData);
    }
    
    @Override
    public Object remove(final Object o) {
        return this.remove((Object[])o);
    }
    
    @Override
    public CompositeData remove(final Object[] array) {
        this.checkKeyType(array);
        return this.dataMap.remove(Arrays.asList(array));
    }
    
    @Override
    public void putAll(final Map<?, ?> map) {
        if (map == null || map.size() == 0) {
            return;
        }
        CompositeData[] array;
        try {
            array = map.values().toArray(new CompositeData[map.size()]);
        }
        catch (final ArrayStoreException ex) {
            throw new ClassCastException("Map argument t contains values which are not instances of <tt>CompositeData</tt>");
        }
        this.putAll(array);
    }
    
    @Override
    public void putAll(final CompositeData[] array) {
        if (array == null || array.length == 0) {
            return;
        }
        final ArrayList list = new ArrayList(array.length + 1);
        for (int i = 0; i < array.length; ++i) {
            final List<?> checkValueAndIndex = this.checkValueAndIndex(array[i]);
            if (list.contains(checkValueAndIndex)) {
                throw new KeyAlreadyExistsException("Argument elements values[" + i + "] and values[" + list.indexOf(checkValueAndIndex) + "] have the same indexes, calculated according to this TabularData instance's tabularType.");
            }
            list.add(checkValueAndIndex);
        }
        for (int j = 0; j < array.length; ++j) {
            this.dataMap.put(list.get(j), array[j]);
        }
    }
    
    @Override
    public void clear() {
        this.dataMap.clear();
    }
    
    @Override
    public int size() {
        return this.dataMap.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public Set<Object> keySet() {
        return this.dataMap.keySet();
    }
    
    @Override
    public Collection<Object> values() {
        return Util.cast(this.dataMap.values());
    }
    
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return Util.cast(this.dataMap.entrySet());
    }
    
    public Object clone() {
        try {
            final TabularDataSupport tabularDataSupport = (TabularDataSupport)super.clone();
            tabularDataSupport.dataMap = new HashMap<Object, CompositeData>(tabularDataSupport.dataMap);
            return tabularDataSupport;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex.toString(), ex);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        TabularData tabularData;
        try {
            tabularData = (TabularData)o;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        if (!this.getTabularType().equals(tabularData.getTabularType())) {
            return false;
        }
        if (this.size() != tabularData.size()) {
            return false;
        }
        final Iterator<CompositeData> iterator = this.dataMap.values().iterator();
        while (iterator.hasNext()) {
            if (!tabularData.containsValue(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0 + this.tabularType.hashCode();
        final Iterator<Object> iterator = this.values().iterator();
        while (iterator.hasNext()) {
            n += iterator.next().hashCode();
        }
        return n;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(tabularType=" + this.tabularType.toString() + ",contents=" + this.dataMap.toString() + ")";
    }
    
    private List<?> internalCalculateIndex(final CompositeData compositeData) {
        return Collections.unmodifiableList((List<?>)Arrays.asList((T[])compositeData.getAll(this.indexNamesArray)));
    }
    
    private void checkKeyType(final Object[] array) {
        if (array == null || array.length == 0) {
            throw new NullPointerException("Argument key cannot be null or empty.");
        }
        if (array.length != this.indexNamesArray.length) {
            throw new InvalidKeyException("Argument key's length=" + array.length + " is different from the number of item values, which is " + this.indexNamesArray.length + ", specified for the indexing rows in this TabularData instance.");
        }
        for (int i = 0; i < array.length; ++i) {
            final OpenType<?> type = this.tabularType.getRowType().getType(this.indexNamesArray[i]);
            if (array[i] != null && !type.isValue(array[i])) {
                throw new InvalidKeyException("Argument element key[" + i + "] is not a value for the open type expected for this element of the index, whose name is \"" + this.indexNamesArray[i] + "\" and whose open type is " + type);
            }
        }
    }
    
    private void checkValueType(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Argument value cannot be null.");
        }
        if (!this.tabularType.getRowType().isValue(compositeData)) {
            throw new InvalidOpenTypeException("Argument value's composite type [" + compositeData.getCompositeType() + "] is not assignable to this TabularData instance's row type [" + this.tabularType.getRowType() + "].");
        }
    }
    
    private List<?> checkValueAndIndex(final CompositeData compositeData) {
        this.checkValueType(compositeData);
        final List<?> internalCalculateIndex = this.internalCalculateIndex(compositeData);
        if (this.dataMap.containsKey(internalCalculateIndex)) {
            throw new KeyAlreadyExistsException("Argument value's index, calculated according to this TabularData instance's tabularType, already refers to a value in this table.");
        }
        return internalCalculateIndex;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final List<String> indexNames = this.tabularType.getIndexNames();
        final int size = indexNames.size();
        SharedSecrets.getJavaOISAccess().checkArray(objectInputStream, String[].class, size);
        this.indexNamesArray = indexNames.toArray(new String[size]);
    }
}
