package javax.management;

import com.sun.jmx.mbeanserver.Util;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.SortedMap;
import java.io.InvalidObjectException;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;

public class ImmutableDescriptor implements Descriptor
{
    private static final long serialVersionUID = 8853308591080540165L;
    private final String[] names;
    private final Object[] values;
    private transient int hashCode;
    public static final ImmutableDescriptor EMPTY_DESCRIPTOR;
    
    public ImmutableDescriptor(final String[] array, final Object[] array2) {
        this(makeMap(array, array2));
    }
    
    public ImmutableDescriptor(final String... array) {
        this(makeMap(array));
    }
    
    public ImmutableDescriptor(final Map<String, ?> map) {
        this.hashCode = -1;
        if (map == null) {
            throw new IllegalArgumentException("Null Map");
        }
        final TreeMap treeMap = new TreeMap((Comparator<? super K>)String.CASE_INSENSITIVE_ORDER);
        for (final Map.Entry entry : map.entrySet()) {
            final String s = (String)entry.getKey();
            if (s == null || s.equals("")) {
                throw new IllegalArgumentException("Empty or null field name");
            }
            if (treeMap.containsKey(s)) {
                throw new IllegalArgumentException("Duplicate name: " + s);
            }
            treeMap.put(s, entry.getValue());
        }
        final int size = treeMap.size();
        this.names = (String[])treeMap.keySet().toArray(new String[size]);
        this.values = treeMap.values().toArray(new Object[size]);
    }
    
    private Object readResolve() throws InvalidObjectException {
        int n = 0;
        if (this.names == null || this.values == null || this.names.length != this.values.length) {
            n = 1;
        }
        if (n == 0) {
            if (this.names.length == 0 && this.getClass() == ImmutableDescriptor.class) {
                return ImmutableDescriptor.EMPTY_DESCRIPTOR;
            }
            final Comparator<String> case_INSENSITIVE_ORDER = String.CASE_INSENSITIVE_ORDER;
            String s = "";
            for (int i = 0; i < this.names.length; ++i) {
                if (this.names[i] == null || case_INSENSITIVE_ORDER.compare(s, this.names[i]) >= 0) {
                    n = 1;
                    break;
                }
                s = this.names[i];
            }
        }
        if (n != 0) {
            throw new InvalidObjectException("Bad names or values");
        }
        return this;
    }
    
    private static SortedMap<String, ?> makeMap(final String[] array, final Object[] array2) {
        if (array == null || array2 == null) {
            throw new IllegalArgumentException("Null array parameter");
        }
        if (array.length != array2.length) {
            throw new IllegalArgumentException("Different size arrays");
        }
        final TreeMap treeMap = new TreeMap((Comparator<? super K>)String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            if (s == null || s.equals("")) {
                throw new IllegalArgumentException("Empty or null field name");
            }
            if (treeMap.put(s, array2[i]) != null) {
                throw new IllegalArgumentException("Duplicate field name: " + s);
            }
        }
        return treeMap;
    }
    
    private static SortedMap<String, ?> makeMap(final String[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Null fields parameter");
        }
        final String[] array2 = new String[array.length];
        final String[] array3 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            final int index = s.indexOf(61);
            if (index < 0) {
                throw new IllegalArgumentException("Missing = character: " + s);
            }
            array2[i] = s.substring(0, index);
            array3[i] = s.substring(index + 1);
        }
        return makeMap(array2, array3);
    }
    
    public static ImmutableDescriptor union(final Descriptor... array) {
        final int nonEmpty = findNonEmpty(array, 0);
        if (nonEmpty < 0) {
            return ImmutableDescriptor.EMPTY_DESCRIPTOR;
        }
        if (array[nonEmpty] instanceof ImmutableDescriptor && findNonEmpty(array, nonEmpty + 1) < 0) {
            return (ImmutableDescriptor)array[nonEmpty];
        }
        final TreeMap treeMap = new TreeMap((Comparator<? super K>)String.CASE_INSENSITIVE_ORDER);
        ImmutableDescriptor empty_DESCRIPTOR = ImmutableDescriptor.EMPTY_DESCRIPTOR;
        for (final Descriptor descriptor : array) {
            if (descriptor != null) {
                String[] array2;
                if (descriptor instanceof ImmutableDescriptor) {
                    final ImmutableDescriptor immutableDescriptor = (ImmutableDescriptor)descriptor;
                    array2 = immutableDescriptor.names;
                    if (immutableDescriptor.getClass() == ImmutableDescriptor.class && array2.length > empty_DESCRIPTOR.names.length) {
                        empty_DESCRIPTOR = immutableDescriptor;
                    }
                }
                else {
                    array2 = descriptor.getFieldNames();
                }
                for (final String s : array2) {
                    final Object fieldValue = descriptor.getFieldValue(s);
                    final Object put = treeMap.put(s, fieldValue);
                    if (put != null) {
                        boolean b;
                        if (put.getClass().isArray()) {
                            b = Arrays.deepEquals(new Object[] { put }, new Object[] { fieldValue });
                        }
                        else {
                            b = put.equals(fieldValue);
                        }
                        if (!b) {
                            throw new IllegalArgumentException("Inconsistent values for descriptor field " + s + ": " + put + " :: " + fieldValue);
                        }
                    }
                }
            }
        }
        if (empty_DESCRIPTOR.names.length == treeMap.size()) {
            return empty_DESCRIPTOR;
        }
        return new ImmutableDescriptor(treeMap);
    }
    
    private static boolean isEmpty(final Descriptor descriptor) {
        if (descriptor == null) {
            return true;
        }
        if (descriptor instanceof ImmutableDescriptor) {
            return ((ImmutableDescriptor)descriptor).names.length == 0;
        }
        return descriptor.getFieldNames().length == 0;
    }
    
    private static int findNonEmpty(final Descriptor[] array, final int n) {
        for (int i = n; i < array.length; ++i) {
            if (!isEmpty(array[i])) {
                return i;
            }
        }
        return -1;
    }
    
    private int fieldIndex(final String s) {
        return Arrays.binarySearch(this.names, s, String.CASE_INSENSITIVE_ORDER);
    }
    
    @Override
    public final Object getFieldValue(final String s) {
        checkIllegalFieldName(s);
        final int fieldIndex = this.fieldIndex(s);
        if (fieldIndex < 0) {
            return null;
        }
        final Object o = this.values[fieldIndex];
        if (o == null || !((Object[])o).getClass().isArray()) {
            return o;
        }
        if (o instanceof Object[]) {
            return ((Object[])o).clone();
        }
        final int length = Array.getLength(o);
        final Object instance = Array.newInstance(((Object[])o).getClass().getComponentType(), length);
        System.arraycopy(o, 0, instance, 0, length);
        return instance;
    }
    
    @Override
    public final String[] getFields() {
        final String[] array = new String[this.names.length];
        for (int i = 0; i < array.length; ++i) {
            Object string = this.values[i];
            if (string == null) {
                string = "";
            }
            else if (!(string instanceof String)) {
                string = "(" + string + ")";
            }
            array[i] = this.names[i] + "=" + string;
        }
        return array;
    }
    
    @Override
    public final Object[] getFieldValues(final String... array) {
        if (array == null) {
            return this.values.clone();
        }
        final Object[] array2 = new Object[array.length];
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            if (s != null && !s.equals("")) {
                array2[i] = this.getFieldValue(s);
            }
        }
        return array2;
    }
    
    @Override
    public final String[] getFieldNames() {
        return this.names.clone();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Descriptor)) {
            return false;
        }
        String[] array;
        if (o instanceof ImmutableDescriptor) {
            array = ((ImmutableDescriptor)o).names;
        }
        else {
            array = ((Descriptor)o).getFieldNames();
            Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
        }
        if (this.names.length != array.length) {
            return false;
        }
        for (int i = 0; i < this.names.length; ++i) {
            if (!this.names[i].equalsIgnoreCase(array[i])) {
                return false;
            }
        }
        Object[] array2;
        if (o instanceof ImmutableDescriptor) {
            array2 = ((ImmutableDescriptor)o).values;
        }
        else {
            array2 = ((Descriptor)o).getFieldValues(array);
        }
        return Arrays.deepEquals(this.values, array2);
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = Util.hashCode(this.names, this.values);
        }
        return this.hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < this.names.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.names[i]).append("=");
            Object substring = this.values[i];
            if (substring != null && substring.getClass().isArray()) {
                final String deepToString = Arrays.deepToString(new Object[] { substring });
                substring = deepToString.substring(1, deepToString.length() - 1);
            }
            sb.append(String.valueOf(substring));
        }
        return sb.append("}").toString();
    }
    
    @Override
    public boolean isValid() {
        return true;
    }
    
    @Override
    public Descriptor clone() {
        return this;
    }
    
    @Override
    public final void setFields(final String[] array, final Object[] array2) throws RuntimeOperationsException {
        if (array == null || array2 == null) {
            illegal("Null argument");
        }
        if (array.length != array2.length) {
            illegal("Different array sizes");
        }
        for (int i = 0; i < array.length; ++i) {
            checkIllegalFieldName(array[i]);
        }
        for (int j = 0; j < array.length; ++j) {
            this.setField(array[j], array2[j]);
        }
    }
    
    @Override
    public final void setField(final String s, final Object o) throws RuntimeOperationsException {
        checkIllegalFieldName(s);
        final int fieldIndex = this.fieldIndex(s);
        if (fieldIndex < 0) {
            unsupported();
        }
        final Object o2 = this.values[fieldIndex];
        if (o2 == null) {
            if (o == null) {
                return;
            }
        }
        else if (o2.equals(o)) {
            return;
        }
        unsupported();
    }
    
    @Override
    public final void removeField(final String s) {
        if (s != null && this.fieldIndex(s) >= 0) {
            unsupported();
        }
    }
    
    static Descriptor nonNullDescriptor(final Descriptor descriptor) {
        if (descriptor == null) {
            return ImmutableDescriptor.EMPTY_DESCRIPTOR;
        }
        return descriptor;
    }
    
    private static void checkIllegalFieldName(final String s) {
        if (s == null || s.equals("")) {
            illegal("Null or empty field name");
        }
    }
    
    private static void unsupported() {
        throw new RuntimeOperationsException(new UnsupportedOperationException("Descriptor is read-only"));
    }
    
    private static void illegal(final String s) {
        throw new RuntimeOperationsException(new IllegalArgumentException(s));
    }
    
    static {
        EMPTY_DESCRIPTOR = new ImmutableDescriptor(new String[0]);
    }
}
