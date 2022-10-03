package javax.management.openmbean;

import java.util.Collection;
import javax.management.DescriptorRead;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;
import java.util.Arrays;
import com.sun.jmx.remote.util.EnvHelp;
import java.util.Map;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import javax.management.ImmutableDescriptor;
import javax.management.Descriptor;
import java.util.Set;
import javax.management.MBeanAttributeInfo;

public class OpenMBeanAttributeInfoSupport extends MBeanAttributeInfo implements OpenMBeanAttributeInfo
{
    static final long serialVersionUID = -4867215622149721849L;
    private OpenType<?> openType;
    private final Object defaultValue;
    private final Set<?> legalValues;
    private final Comparable<?> minValue;
    private final Comparable<?> maxValue;
    private transient Integer myHashCode;
    private transient String myToString;
    
    public OpenMBeanAttributeInfoSupport(final String s, final String s2, final OpenType<?> openType, final boolean b, final boolean b2, final boolean b3) {
        this(s, s2, openType, b, b2, b3, null);
    }
    
    public OpenMBeanAttributeInfoSupport(final String s, final String s2, final OpenType<?> openType, final boolean b, final boolean b2, final boolean b3, Descriptor descriptor) {
        super(s, (openType == null) ? null : openType.getClassName(), s2, b, b2, b3, ImmutableDescriptor.union(descriptor, (openType == null) ? null : openType.getDescriptor()));
        this.myHashCode = null;
        this.myToString = null;
        this.openType = openType;
        descriptor = this.getDescriptor();
        this.defaultValue = valueFrom(descriptor, "defaultValue", openType);
        this.legalValues = valuesFrom(descriptor, "legalValues", openType);
        this.minValue = comparableValueFrom(descriptor, "minValue", openType);
        this.maxValue = comparableValueFrom(descriptor, "maxValue", openType);
        try {
            check(this);
        }
        catch (final OpenDataException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
    
    public <T> OpenMBeanAttributeInfoSupport(final String s, final String s2, final OpenType<T> openType, final boolean b, final boolean b2, final boolean b3, final T t) throws OpenDataException {
        this(s, s2, openType, b, b2, b3, t, null);
    }
    
    public <T> OpenMBeanAttributeInfoSupport(final String s, final String s2, final OpenType<T> openType, final boolean b, final boolean b2, final boolean b3, final T t, final T[] array) throws OpenDataException {
        this(s, s2, openType, b, b2, b3, t, array, null, null);
    }
    
    public <T> OpenMBeanAttributeInfoSupport(final String s, final String s2, final OpenType<T> openType, final boolean b, final boolean b2, final boolean b3, final T t, final Comparable<T> comparable, final Comparable<T> comparable2) throws OpenDataException {
        this(s, s2, openType, b, b2, b3, t, null, comparable, comparable2);
    }
    
    private <T> OpenMBeanAttributeInfoSupport(final String s, final String s2, final OpenType<T> openType, final boolean b, final boolean b2, final boolean b3, final T defaultValue, final T[] array, final Comparable<T> minValue, final Comparable<T> maxValue) throws OpenDataException {
        super(s, (openType == null) ? null : openType.getClassName(), s2, b, b2, b3, makeDescriptor(openType, defaultValue, array, minValue, maxValue));
        this.myHashCode = null;
        this.myToString = null;
        this.openType = openType;
        final Descriptor descriptor = this.getDescriptor();
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.legalValues = (Set)descriptor.getFieldValue("legalValues");
        check(this);
    }
    
    private Object readResolve() {
        if (this.getDescriptor().getFieldNames().length == 0) {
            return new OpenMBeanAttributeInfoSupport(this.name, this.description, this.openType, this.isReadable(), this.isWritable(), this.isIs(), makeDescriptor(cast(this.openType), this.defaultValue, cast(this.legalValues), cast(this.minValue), cast(this.maxValue)));
        }
        return this;
    }
    
    static void check(final OpenMBeanParameterInfo openMBeanParameterInfo) throws OpenDataException {
        final OpenType<?> openType = openMBeanParameterInfo.getOpenType();
        if (openType == null) {
            throw new IllegalArgumentException("OpenType cannot be null");
        }
        if (openMBeanParameterInfo.getName() == null || openMBeanParameterInfo.getName().trim().equals("")) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (openMBeanParameterInfo.getDescription() == null || openMBeanParameterInfo.getDescription().trim().equals("")) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (openMBeanParameterInfo.hasDefaultValue()) {
            if (openType.isArray() || openType instanceof TabularType) {
                throw new OpenDataException("Default value not supported for ArrayType and TabularType");
            }
            if (!openType.isValue(openMBeanParameterInfo.getDefaultValue())) {
                throw new OpenDataException("Argument defaultValue's class [\"" + openMBeanParameterInfo.getDefaultValue().getClass().getName() + "\"] does not match the one defined in openType[\"" + openType.getClassName() + "\"]");
            }
        }
        if (openMBeanParameterInfo.hasLegalValues() && (openMBeanParameterInfo.hasMinValue() || openMBeanParameterInfo.hasMaxValue())) {
            throw new OpenDataException("cannot have both legalValue and minValue or maxValue");
        }
        if (openMBeanParameterInfo.hasMinValue() && !openType.isValue(openMBeanParameterInfo.getMinValue())) {
            throw new OpenDataException("Type of minValue [" + openMBeanParameterInfo.getMinValue().getClass().getName() + "] does not match OpenType [" + openType.getClassName() + "]");
        }
        if (openMBeanParameterInfo.hasMaxValue() && !openType.isValue(openMBeanParameterInfo.getMaxValue())) {
            throw new OpenDataException("Type of maxValue [" + openMBeanParameterInfo.getMaxValue().getClass().getName() + "] does not match OpenType [" + openType.getClassName() + "]");
        }
        if (openMBeanParameterInfo.hasDefaultValue()) {
            final Object defaultValue = openMBeanParameterInfo.getDefaultValue();
            if (openMBeanParameterInfo.hasLegalValues() && !openMBeanParameterInfo.getLegalValues().contains(defaultValue)) {
                throw new OpenDataException("defaultValue is not contained in legalValues");
            }
            if (openMBeanParameterInfo.hasMinValue() && compare(openMBeanParameterInfo.getMinValue(), defaultValue) > 0) {
                throw new OpenDataException("minValue cannot be greater than defaultValue");
            }
            if (openMBeanParameterInfo.hasMaxValue() && compare(openMBeanParameterInfo.getMaxValue(), defaultValue) < 0) {
                throw new OpenDataException("maxValue cannot be less than defaultValue");
            }
        }
        if (openMBeanParameterInfo.hasLegalValues()) {
            if (openType instanceof TabularType || openType.isArray()) {
                throw new OpenDataException("Legal values not supported for TabularType and arrays");
            }
            for (final Object next : openMBeanParameterInfo.getLegalValues()) {
                if (!openType.isValue(next)) {
                    throw new OpenDataException("Element of legalValues [" + next + "] is not a valid value for the specified openType [" + openType.toString() + "]");
                }
            }
        }
        if (openMBeanParameterInfo.hasMinValue() && openMBeanParameterInfo.hasMaxValue() && compare(openMBeanParameterInfo.getMinValue(), openMBeanParameterInfo.getMaxValue()) > 0) {
            throw new OpenDataException("minValue cannot be greater than maxValue");
        }
    }
    
    static int compare(final Object o, final Object o2) {
        return ((Comparable)o).compareTo(o2);
    }
    
    static <T> Descriptor makeDescriptor(final OpenType<T> openType, final T t, final T[] array, final Comparable<T> comparable, final Comparable<T> comparable2) {
        final HashMap hashMap = new HashMap();
        if (t != null) {
            hashMap.put("defaultValue", t);
        }
        if (array != null) {
            final HashSet set = new HashSet();
            for (int length = array.length, i = 0; i < length; ++i) {
                set.add(array[i]);
            }
            hashMap.put("legalValues", Collections.unmodifiableSet((Set<?>)set));
        }
        if (comparable != null) {
            hashMap.put("minValue", comparable);
        }
        if (comparable2 != null) {
            hashMap.put("maxValue", comparable2);
        }
        if (hashMap.isEmpty()) {
            return openType.getDescriptor();
        }
        hashMap.put("openType", openType);
        return new ImmutableDescriptor(hashMap);
    }
    
    static <T> Descriptor makeDescriptor(final OpenType<T> openType, final T t, final Set<T> set, final Comparable<T> comparable, final Comparable<T> comparable2) {
        Object[] array;
        if (set == null) {
            array = null;
        }
        else {
            array = cast(new Object[set.size()]);
            set.toArray(array);
        }
        return makeDescriptor((OpenType<Object>)openType, t, array, (Comparable<Object>)comparable, (Comparable<Object>)comparable2);
    }
    
    static <T> T valueFrom(final Descriptor descriptor, final String s, final OpenType<T> openType) {
        final Object fieldValue = descriptor.getFieldValue(s);
        if (fieldValue == null) {
            return null;
        }
        try {
            return convertFrom(fieldValue, openType);
        }
        catch (final Exception ex) {
            throw EnvHelp.initCause(new IllegalArgumentException("Cannot convert descriptor field " + s + "  to " + openType.getTypeName()), ex);
        }
    }
    
    static <T> Set<T> valuesFrom(final Descriptor descriptor, final String s, final OpenType<T> openType) {
        final Object fieldValue = descriptor.getFieldValue(s);
        if (fieldValue == null) {
            return null;
        }
        Object list;
        if (fieldValue instanceof Set) {
            final Set set = (Set)fieldValue;
            boolean b = true;
            final Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                if (!openType.isValue(iterator.next())) {
                    b = false;
                    break;
                }
            }
            if (b) {
                return (Set<T>)cast(set);
            }
            list = set;
        }
        else {
            if (!(fieldValue instanceof Object[])) {
                throw new IllegalArgumentException("Descriptor value for " + s + " must be a Set or an array: " + ((T[])fieldValue).getClass().getName());
            }
            list = Arrays.asList((Object[])fieldValue);
        }
        final HashSet set2 = new HashSet();
        final Iterator iterator2 = ((Collection)list).iterator();
        while (iterator2.hasNext()) {
            set2.add(convertFrom(iterator2.next(), openType));
        }
        return set2;
    }
    
    static <T> Comparable<?> comparableValueFrom(final Descriptor descriptor, final String s, final OpenType<T> openType) {
        final T value = valueFrom(descriptor, s, openType);
        if (value == null || value instanceof Comparable) {
            return (Comparable<?>)value;
        }
        throw new IllegalArgumentException("Descriptor field " + s + " with value " + value + " is not Comparable");
    }
    
    private static <T> T convertFrom(final Object o, final OpenType<T> openType) {
        if (openType.isValue(o)) {
            return cast(o);
        }
        return (T)convertFromStrings(o, (OpenType<Object>)openType);
    }
    
    private static <T> T convertFromStrings(final Object o, final OpenType<T> openType) {
        if (openType instanceof ArrayType) {
            return (T)convertFromStringArray(o, (OpenType<Object>)openType);
        }
        if (o instanceof String) {
            return convertFromString((String)o, openType);
        }
        throw new IllegalArgumentException("Cannot convert value " + o + " of type " + o.getClass().getName() + " to type " + openType.getTypeName());
    }
    
    private static <T> T convertFromString(final String s, final OpenType<T> openType) {
        Class clazz;
        try {
            final String safeGetClassName = openType.safeGetClassName();
            ReflectUtil.checkPackageAccess(safeGetClassName);
            clazz = cast(Class.forName(safeGetClassName));
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.toString());
        }
        Method method;
        try {
            method = clazz.getMethod("valueOf", String.class);
            if (!Modifier.isStatic(method.getModifiers()) || method.getReturnType() != clazz) {
                method = null;
            }
        }
        catch (final NoSuchMethodException ex2) {
            method = null;
        }
        if (method != null) {
            try {
                return (T)clazz.cast(MethodUtil.invoke(method, null, new Object[] { s }));
            }
            catch (final Exception ex3) {
                throw new IllegalArgumentException("Could not convert \"" + s + "\" using method: " + method, ex3);
            }
        }
        Constructor constructor;
        try {
            constructor = clazz.getConstructor(String.class);
        }
        catch (final NoSuchMethodException ex4) {
            constructor = null;
        }
        if (constructor != null) {
            try {
                return (T)constructor.newInstance(s);
            }
            catch (final Exception ex5) {
                throw new IllegalArgumentException("Could not convert \"" + s + "\" using constructor: " + constructor, ex5);
            }
        }
        throw new IllegalArgumentException("Don't know how to convert string to " + openType.getTypeName());
    }
    
    private static <T> T convertFromStringArray(final Object o, final OpenType<T> openType) {
        final ArrayType arrayType = (ArrayType)openType;
        final OpenType elementOpenType = arrayType.getElementOpenType();
        final int dimension = arrayType.getDimension();
        String string = "[";
        for (int i = 1; i < dimension; ++i) {
            string += "[";
        }
        Class<?> forName;
        Class<?> forName2;
        try {
            final String safeGetClassName = elementOpenType.safeGetClassName();
            ReflectUtil.checkPackageAccess(safeGetClassName);
            forName = Class.forName(string + "Ljava.lang.String;");
            forName2 = Class.forName(string + "L" + safeGetClassName + ";");
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.toString());
        }
        if (!forName.isInstance(o)) {
            throw new IllegalArgumentException("Value for " + dimension + "-dimensional array of " + elementOpenType.getTypeName() + " must be same type or a String array with same dimensions");
        }
        OpenType openType2;
        if (dimension == 1) {
            openType2 = elementOpenType;
        }
        else {
            try {
                openType2 = new ArrayType(dimension - 1, elementOpenType);
            }
            catch (final OpenDataException ex2) {
                throw new IllegalArgumentException(ex2.getMessage(), ex2);
            }
        }
        final int length = Array.getLength(o);
        final Object[] array = (Object[])Array.newInstance(forName2.getComponentType(), length);
        for (int j = 0; j < length; ++j) {
            Array.set(array, j, convertFromStrings(Array.get(o, j), openType2));
        }
        return (T)cast(array);
    }
    
    static <T> T cast(final Object o) {
        return (T)o;
    }
    
    @Override
    public OpenType<?> getOpenType() {
        return this.openType;
    }
    
    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public Set<?> getLegalValues() {
        return this.legalValues;
    }
    
    @Override
    public Comparable<?> getMinValue() {
        return this.minValue;
    }
    
    @Override
    public Comparable<?> getMaxValue() {
        return this.maxValue;
    }
    
    @Override
    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }
    
    @Override
    public boolean hasLegalValues() {
        return this.legalValues != null;
    }
    
    @Override
    public boolean hasMinValue() {
        return this.minValue != null;
    }
    
    @Override
    public boolean hasMaxValue() {
        return this.maxValue != null;
    }
    
    @Override
    public boolean isValue(final Object o) {
        return isValue(this, o);
    }
    
    static boolean isValue(final OpenMBeanParameterInfo openMBeanParameterInfo, final Object o) {
        return (openMBeanParameterInfo.hasDefaultValue() && o == null) || (openMBeanParameterInfo.getOpenType().isValue(o) && (!openMBeanParameterInfo.hasLegalValues() || openMBeanParameterInfo.getLegalValues().contains(o)) && (!openMBeanParameterInfo.hasMinValue() || openMBeanParameterInfo.getMinValue().compareTo(o) <= 0) && (!openMBeanParameterInfo.hasMaxValue() || openMBeanParameterInfo.getMaxValue().compareTo(o) >= 0));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof OpenMBeanAttributeInfo)) {
            return false;
        }
        final OpenMBeanAttributeInfo openMBeanAttributeInfo = (OpenMBeanAttributeInfo)o;
        return this.isReadable() == openMBeanAttributeInfo.isReadable() && this.isWritable() == openMBeanAttributeInfo.isWritable() && this.isIs() == openMBeanAttributeInfo.isIs() && equal(this, openMBeanAttributeInfo);
    }
    
    static boolean equal(final OpenMBeanParameterInfo openMBeanParameterInfo, final OpenMBeanParameterInfo openMBeanParameterInfo2) {
        if (openMBeanParameterInfo instanceof DescriptorRead) {
            if (!(openMBeanParameterInfo2 instanceof DescriptorRead)) {
                return false;
            }
            if (!((DescriptorRead)openMBeanParameterInfo).getDescriptor().equals(((DescriptorRead)openMBeanParameterInfo2).getDescriptor())) {
                return false;
            }
        }
        else if (openMBeanParameterInfo2 instanceof DescriptorRead) {
            return false;
        }
        if (openMBeanParameterInfo.getName().equals(openMBeanParameterInfo2.getName()) && openMBeanParameterInfo.getOpenType().equals(openMBeanParameterInfo2.getOpenType())) {
            if (openMBeanParameterInfo.hasDefaultValue()) {
                if (!openMBeanParameterInfo.getDefaultValue().equals(openMBeanParameterInfo2.getDefaultValue())) {
                    return false;
                }
            }
            else if (openMBeanParameterInfo2.hasDefaultValue()) {
                return false;
            }
            if (openMBeanParameterInfo.hasMinValue()) {
                if (!openMBeanParameterInfo.getMinValue().equals(openMBeanParameterInfo2.getMinValue())) {
                    return false;
                }
            }
            else if (openMBeanParameterInfo2.hasMinValue()) {
                return false;
            }
            if (openMBeanParameterInfo.hasMaxValue()) {
                if (!openMBeanParameterInfo.getMaxValue().equals(openMBeanParameterInfo2.getMaxValue())) {
                    return false;
                }
            }
            else if (openMBeanParameterInfo2.hasMaxValue()) {
                return false;
            }
            if (!(openMBeanParameterInfo.hasLegalValues() ? (!openMBeanParameterInfo.getLegalValues().equals(openMBeanParameterInfo2.getLegalValues())) : openMBeanParameterInfo2.hasLegalValues())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            this.myHashCode = hashCode(this);
        }
        return this.myHashCode;
    }
    
    static int hashCode(final OpenMBeanParameterInfo openMBeanParameterInfo) {
        int n = 0 + openMBeanParameterInfo.getName().hashCode() + openMBeanParameterInfo.getOpenType().hashCode();
        if (openMBeanParameterInfo.hasDefaultValue()) {
            n += openMBeanParameterInfo.getDefaultValue().hashCode();
        }
        if (openMBeanParameterInfo.hasMinValue()) {
            n += openMBeanParameterInfo.getMinValue().hashCode();
        }
        if (openMBeanParameterInfo.hasMaxValue()) {
            n += openMBeanParameterInfo.getMaxValue().hashCode();
        }
        if (openMBeanParameterInfo.hasLegalValues()) {
            n += openMBeanParameterInfo.getLegalValues().hashCode();
        }
        if (openMBeanParameterInfo instanceof DescriptorRead) {
            n += ((DescriptorRead)openMBeanParameterInfo).getDescriptor().hashCode();
        }
        return n;
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            this.myToString = toString(this);
        }
        return this.myToString;
    }
    
    static String toString(final OpenMBeanParameterInfo openMBeanParameterInfo) {
        final Descriptor descriptor = (openMBeanParameterInfo instanceof DescriptorRead) ? ((DescriptorRead)openMBeanParameterInfo).getDescriptor() : null;
        return openMBeanParameterInfo.getClass().getName() + "(name=" + openMBeanParameterInfo.getName() + ",openType=" + openMBeanParameterInfo.getOpenType() + ",default=" + openMBeanParameterInfo.getDefaultValue() + ",minValue=" + openMBeanParameterInfo.getMinValue() + ",maxValue=" + openMBeanParameterInfo.getMaxValue() + ",legalValues=" + openMBeanParameterInfo.getLegalValues() + ((descriptor == null) ? "" : (",descriptor=" + descriptor)) + ")";
    }
}
