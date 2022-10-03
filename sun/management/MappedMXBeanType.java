package sun.management;

import java.lang.reflect.InvocationTargetException;
import com.sun.management.VMOption;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.LockInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.MemoryUsage;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;
import javax.management.openmbean.CompositeType;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Array;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.CompositeData;
import java.io.InvalidObjectException;
import javax.management.openmbean.OpenDataException;
import java.lang.reflect.GenericArrayType;
import java.util.Map;
import java.util.List;
import java.lang.reflect.ParameterizedType;
import javax.management.openmbean.OpenType;
import java.lang.reflect.Type;
import java.util.WeakHashMap;

public abstract class MappedMXBeanType
{
    private static final WeakHashMap<Type, MappedMXBeanType> convertedTypes;
    boolean isBasicType;
    OpenType<?> openType;
    Class<?> mappedTypeClass;
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String[] mapIndexNames;
    private static final String[] mapItemNames;
    private static final Class<?> COMPOSITE_DATA_CLASS;
    private static final OpenType<?> inProgress;
    private static final OpenType[] simpleTypes;
    
    public MappedMXBeanType() {
        this.isBasicType = false;
        this.openType = MappedMXBeanType.inProgress;
    }
    
    static synchronized MappedMXBeanType newMappedType(final Type type) throws OpenDataException {
        MappedMXBeanType mappedMXBeanType = null;
        if (type instanceof Class) {
            final Class clazz = (Class)type;
            if (clazz.isEnum()) {
                mappedMXBeanType = new EnumMXBeanType(clazz);
            }
            else if (clazz.isArray()) {
                mappedMXBeanType = new ArrayMXBeanType(clazz);
            }
            else {
                mappedMXBeanType = new CompositeDataMXBeanType(clazz);
            }
        }
        else if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            final Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class) {
                final Class clazz2 = (Class)rawType;
                if (clazz2 == List.class) {
                    mappedMXBeanType = new ListMXBeanType(parameterizedType);
                }
                else if (clazz2 == Map.class) {
                    mappedMXBeanType = new MapMXBeanType(parameterizedType);
                }
            }
        }
        else if (type instanceof GenericArrayType) {
            mappedMXBeanType = new GenericArrayMXBeanType((GenericArrayType)type);
        }
        if (mappedMXBeanType == null) {
            throw new OpenDataException(type + " is not a supported MXBean type.");
        }
        MappedMXBeanType.convertedTypes.put(type, mappedMXBeanType);
        return mappedMXBeanType;
    }
    
    static synchronized MappedMXBeanType newBasicType(final Class<?> clazz, final OpenType<?> openType) throws OpenDataException {
        final BasicMXBeanType basicMXBeanType = new BasicMXBeanType(clazz, openType);
        MappedMXBeanType.convertedTypes.put(clazz, basicMXBeanType);
        return basicMXBeanType;
    }
    
    static synchronized MappedMXBeanType getMappedType(final Type type) throws OpenDataException {
        MappedMXBeanType mappedType = MappedMXBeanType.convertedTypes.get(type);
        if (mappedType == null) {
            mappedType = newMappedType(type);
        }
        if (mappedType.getOpenType() instanceof InProgress) {
            throw new OpenDataException("Recursive data structure");
        }
        return mappedType;
    }
    
    public static synchronized OpenType<?> toOpenType(final Type type) throws OpenDataException {
        return getMappedType(type).getOpenType();
    }
    
    public static Object toJavaTypeData(final Object o, final Type type) throws OpenDataException, InvalidObjectException {
        if (o == null) {
            return null;
        }
        return getMappedType(type).toJavaTypeData(o);
    }
    
    public static Object toOpenTypeData(final Object o, final Type type) throws OpenDataException {
        if (o == null) {
            return null;
        }
        return getMappedType(type).toOpenTypeData(o);
    }
    
    OpenType<?> getOpenType() {
        return this.openType;
    }
    
    boolean isBasicType() {
        return this.isBasicType;
    }
    
    String getTypeName() {
        return this.getMappedTypeClass().getName();
    }
    
    Class<?> getMappedTypeClass() {
        return this.mappedTypeClass;
    }
    
    abstract Type getJavaType();
    
    abstract String getName();
    
    abstract Object toOpenTypeData(final Object p0) throws OpenDataException;
    
    abstract Object toJavaTypeData(final Object p0) throws OpenDataException, InvalidObjectException;
    
    private static String decapitalize(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        if (s.length() > 1 && Character.isUpperCase(s.charAt(1)) && Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        final char[] charArray = s.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return new String(charArray);
    }
    
    static {
        convertedTypes = new WeakHashMap<Type, MappedMXBeanType>();
        mapIndexNames = new String[] { "key" };
        mapItemNames = new String[] { "key", "value" };
        COMPOSITE_DATA_CLASS = CompositeData.class;
        InProgress inProgress2;
        try {
            inProgress2 = new InProgress();
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
        inProgress = inProgress2;
        simpleTypes = new OpenType[] { SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID };
        try {
            for (int i = 0; i < MappedMXBeanType.simpleTypes.length; ++i) {
                final OpenType openType = MappedMXBeanType.simpleTypes[i];
                Class<?> forName;
                try {
                    forName = Class.forName(openType.getClassName(), false, MappedMXBeanType.class.getClassLoader());
                    newBasicType(forName, openType);
                }
                catch (final ClassNotFoundException ex2) {
                    throw new AssertionError((Object)ex2);
                }
                catch (final OpenDataException ex3) {
                    throw new AssertionError((Object)ex3);
                }
                if (forName.getName().startsWith("java.lang.")) {
                    try {
                        newBasicType((Class<?>)forName.getField("TYPE").get(null), openType);
                    }
                    catch (final NoSuchFieldException ex4) {}
                    catch (final IllegalAccessException ex5) {
                        throw new AssertionError((Object)ex5);
                    }
                }
            }
        }
        catch (final OpenDataException ex6) {
            throw new AssertionError((Object)ex6);
        }
    }
    
    static class BasicMXBeanType extends MappedMXBeanType
    {
        final Class<?> basicType;
        
        BasicMXBeanType(final Class<?> clazz, final OpenType<?> openType) {
            this.basicType = clazz;
            this.openType = openType;
            this.mappedTypeClass = clazz;
            this.isBasicType = true;
        }
        
        @Override
        Type getJavaType() {
            return this.basicType;
        }
        
        @Override
        String getName() {
            return this.basicType.getName();
        }
        
        @Override
        Object toOpenTypeData(final Object o) throws OpenDataException {
            return o;
        }
        
        @Override
        Object toJavaTypeData(final Object o) throws OpenDataException, InvalidObjectException {
            return o;
        }
    }
    
    static class EnumMXBeanType extends MappedMXBeanType
    {
        final Class enumClass;
        
        EnumMXBeanType(final Class<?> enumClass) {
            this.enumClass = enumClass;
            this.openType = SimpleType.STRING;
            this.mappedTypeClass = String.class;
        }
        
        @Override
        Type getJavaType() {
            return this.enumClass;
        }
        
        @Override
        String getName() {
            return this.enumClass.getName();
        }
        
        @Override
        Object toOpenTypeData(final Object o) throws OpenDataException {
            return ((Enum)o).name();
        }
        
        @Override
        Object toJavaTypeData(final Object o) throws OpenDataException, InvalidObjectException {
            try {
                return Enum.valueOf((Class<Object>)this.enumClass, (String)o);
            }
            catch (final IllegalArgumentException ex) {
                final InvalidObjectException ex2 = new InvalidObjectException("Enum constant named " + (String)o + " is missing");
                ex2.initCause(ex);
                throw ex2;
            }
        }
    }
    
    static class ArrayMXBeanType extends MappedMXBeanType
    {
        final Class<?> arrayClass;
        protected MappedMXBeanType componentType;
        protected MappedMXBeanType baseElementType;
        
        ArrayMXBeanType(final Class<?> arrayClass) throws OpenDataException {
            this.arrayClass = arrayClass;
            this.componentType = MappedMXBeanType.getMappedType(arrayClass.getComponentType());
            StringBuilder sb = new StringBuilder();
            Class<?> componentType;
            int n;
            for (componentType = arrayClass, n = 0; componentType.isArray(); componentType = componentType.getComponentType(), ++n) {
                sb.append('[');
            }
            this.baseElementType = MappedMXBeanType.getMappedType(componentType);
            if (componentType.isPrimitive()) {
                sb = new StringBuilder(arrayClass.getName());
            }
            else {
                sb.append("L" + this.baseElementType.getTypeName() + ";");
            }
            try {
                this.mappedTypeClass = Class.forName(sb.toString());
            }
            catch (final ClassNotFoundException ex) {
                final OpenDataException ex2 = new OpenDataException("Cannot obtain array class");
                ex2.initCause(ex);
                throw ex2;
            }
            this.openType = new ArrayType<Object>(n, this.baseElementType.getOpenType());
        }
        
        protected ArrayMXBeanType() {
            this.arrayClass = null;
        }
        
        @Override
        Type getJavaType() {
            return this.arrayClass;
        }
        
        @Override
        String getName() {
            return this.arrayClass.getName();
        }
        
        @Override
        Object toOpenTypeData(final Object o) throws OpenDataException {
            if (this.baseElementType.isBasicType()) {
                return o;
            }
            final Object[] array = (Object[])o;
            final Object[] array2 = (Object[])Array.newInstance(this.componentType.getMappedTypeClass(), array.length);
            int n = 0;
            for (final Object o2 : array) {
                if (o2 == null) {
                    array2[n] = null;
                }
                else {
                    array2[n] = this.componentType.toOpenTypeData(o2);
                }
                ++n;
            }
            return array2;
        }
        
        @Override
        Object toJavaTypeData(final Object o) throws OpenDataException, InvalidObjectException {
            if (this.baseElementType.isBasicType()) {
                return o;
            }
            final Object[] array = (Object[])o;
            final Object[] array2 = (Object[])Array.newInstance((Class<?>)this.componentType.getJavaType(), array.length);
            int n = 0;
            for (final Object o2 : array) {
                if (o2 == null) {
                    array2[n] = null;
                }
                else {
                    array2[n] = this.componentType.toJavaTypeData(o2);
                }
                ++n;
            }
            return array2;
        }
    }
    
    static class GenericArrayMXBeanType extends ArrayMXBeanType
    {
        final GenericArrayType gtype;
        
        GenericArrayMXBeanType(final GenericArrayType gtype) throws OpenDataException {
            this.gtype = gtype;
            this.componentType = MappedMXBeanType.getMappedType(gtype.getGenericComponentType());
            StringBuilder sb = new StringBuilder();
            Type genericComponentType;
            int n;
            for (genericComponentType = gtype, n = 0; genericComponentType instanceof GenericArrayType; genericComponentType = ((GenericArrayType)genericComponentType).getGenericComponentType(), ++n) {
                sb.append('[');
            }
            this.baseElementType = MappedMXBeanType.getMappedType(genericComponentType);
            if (genericComponentType instanceof Class && ((Class)genericComponentType).isPrimitive()) {
                sb = new StringBuilder(gtype.toString());
            }
            else {
                sb.append("L" + this.baseElementType.getTypeName() + ";");
            }
            try {
                this.mappedTypeClass = Class.forName(sb.toString());
            }
            catch (final ClassNotFoundException ex) {
                final OpenDataException ex2 = new OpenDataException("Cannot obtain array class");
                ex2.initCause(ex);
                throw ex2;
            }
            this.openType = new ArrayType<Object>(n, this.baseElementType.getOpenType());
        }
        
        @Override
        Type getJavaType() {
            return this.gtype;
        }
        
        @Override
        String getName() {
            return this.gtype.toString();
        }
    }
    
    static class ListMXBeanType extends MappedMXBeanType
    {
        final ParameterizedType javaType;
        final MappedMXBeanType paramType;
        final String typeName;
        
        ListMXBeanType(final ParameterizedType javaType) throws OpenDataException {
            this.javaType = javaType;
            final Type[] actualTypeArguments = javaType.getActualTypeArguments();
            assert actualTypeArguments.length == 1;
            if (!(actualTypeArguments[0] instanceof Class)) {
                throw new OpenDataException("Element Type for " + javaType + " not supported");
            }
            final Class clazz = (Class)actualTypeArguments[0];
            if (clazz.isArray()) {
                throw new OpenDataException("Element Type for " + javaType + " not supported");
            }
            this.paramType = MappedMXBeanType.getMappedType(clazz);
            this.typeName = "List<" + this.paramType.getName() + ">";
            try {
                this.mappedTypeClass = Class.forName("[L" + this.paramType.getTypeName() + ";");
            }
            catch (final ClassNotFoundException ex) {
                final OpenDataException ex2 = new OpenDataException("Array class not found");
                ex2.initCause(ex);
                throw ex2;
            }
            this.openType = new ArrayType<Object>(1, this.paramType.getOpenType());
        }
        
        @Override
        Type getJavaType() {
            return this.javaType;
        }
        
        @Override
        String getName() {
            return this.typeName;
        }
        
        @Override
        Object toOpenTypeData(final Object o) throws OpenDataException {
            final List list = (List)o;
            final Object[] array = (Object[])Array.newInstance(this.paramType.getMappedTypeClass(), list.size());
            int n = 0;
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                array[n++] = this.paramType.toOpenTypeData(iterator.next());
            }
            return array;
        }
        
        @Override
        Object toJavaTypeData(final Object o) throws OpenDataException, InvalidObjectException {
            final Object[] array = (Object[])o;
            final ArrayList list = new ArrayList(array.length);
            final Object[] array2 = array;
            for (int length = array2.length, i = 0; i < length; ++i) {
                list.add(this.paramType.toJavaTypeData(array2[i]));
            }
            return list;
        }
    }
    
    static class MapMXBeanType extends MappedMXBeanType
    {
        final ParameterizedType javaType;
        final MappedMXBeanType keyType;
        final MappedMXBeanType valueType;
        final String typeName;
        
        MapMXBeanType(final ParameterizedType javaType) throws OpenDataException {
            this.javaType = javaType;
            final Type[] actualTypeArguments = javaType.getActualTypeArguments();
            assert actualTypeArguments.length == 2;
            this.keyType = MappedMXBeanType.getMappedType(actualTypeArguments[0]);
            this.valueType = MappedMXBeanType.getMappedType(actualTypeArguments[1]);
            this.typeName = "Map<" + this.keyType.getName() + "," + this.valueType.getName() + ">";
            this.openType = new TabularType(this.typeName, this.typeName, new CompositeType(this.typeName, this.typeName, MappedMXBeanType.mapItemNames, MappedMXBeanType.mapItemNames, new OpenType[] { this.keyType.getOpenType(), this.valueType.getOpenType() }), MappedMXBeanType.mapIndexNames);
            this.mappedTypeClass = TabularData.class;
        }
        
        @Override
        Type getJavaType() {
            return this.javaType;
        }
        
        @Override
        String getName() {
            return this.typeName;
        }
        
        @Override
        Object toOpenTypeData(final Object o) throws OpenDataException {
            final Map map = (Map)o;
            final TabularType tabularType = (TabularType)this.openType;
            final TabularDataSupport tabularDataSupport = new TabularDataSupport(tabularType);
            final CompositeType rowType = tabularType.getRowType();
            for (final Map.Entry entry : map.entrySet()) {
                tabularDataSupport.put(new CompositeDataSupport(rowType, MappedMXBeanType.mapItemNames, new Object[] { this.keyType.toOpenTypeData(entry.getKey()), this.valueType.toOpenTypeData(entry.getValue()) }));
            }
            return tabularDataSupport;
        }
        
        @Override
        Object toJavaTypeData(final Object o) throws OpenDataException, InvalidObjectException {
            final TabularData tabularData = (TabularData)o;
            final HashMap hashMap = new HashMap();
            for (final CompositeData compositeData : tabularData.values()) {
                hashMap.put(this.keyType.toJavaTypeData(compositeData.get("key")), this.valueType.toJavaTypeData(compositeData.get("value")));
            }
            return hashMap;
        }
    }
    
    static class CompositeDataMXBeanType extends MappedMXBeanType
    {
        final Class<?> javaClass;
        final boolean isCompositeData;
        Method fromMethod;
        
        CompositeDataMXBeanType(final Class<?> javaClass) throws OpenDataException {
            this.fromMethod = null;
            this.javaClass = javaClass;
            this.mappedTypeClass = MappedMXBeanType.COMPOSITE_DATA_CLASS;
            try {
                this.fromMethod = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                    @Override
                    public Method run() throws NoSuchMethodException {
                        return CompositeDataMXBeanType.this.javaClass.getMethod("from", MappedMXBeanType.COMPOSITE_DATA_CLASS);
                    }
                });
            }
            catch (final PrivilegedActionException ex) {}
            if (MappedMXBeanType.COMPOSITE_DATA_CLASS.isAssignableFrom(javaClass)) {
                this.isCompositeData = true;
                this.openType = null;
            }
            else {
                this.isCompositeData = false;
                final Method[] array = AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
                    @Override
                    public Method[] run() {
                        return CompositeDataMXBeanType.this.javaClass.getMethods();
                    }
                });
                final ArrayList list = new ArrayList();
                final ArrayList list2 = new ArrayList();
                for (int i = 0; i < array.length; ++i) {
                    final Method method = array[i];
                    final String name = method.getName();
                    final Type genericReturnType = method.getGenericReturnType();
                    String s;
                    if (name.startsWith("get")) {
                        s = name.substring(3);
                    }
                    else {
                        if (!name.startsWith("is") || !(genericReturnType instanceof Class) || genericReturnType != Boolean.TYPE) {
                            continue;
                        }
                        s = name.substring(2);
                    }
                    if (!s.equals("") && method.getParameterTypes().length <= 0 && genericReturnType != Void.TYPE) {
                        if (!s.equals("Class")) {
                            list.add(decapitalize(s));
                            list2.add(MappedMXBeanType.toOpenType(genericReturnType));
                        }
                    }
                }
                final String[] array2 = (String[])list.toArray(new String[0]);
                this.openType = new CompositeType(javaClass.getName(), javaClass.getName(), array2, array2, (OpenType<?>[])list2.toArray(new OpenType[0]));
            }
        }
        
        @Override
        Type getJavaType() {
            return this.javaClass;
        }
        
        @Override
        String getName() {
            return this.javaClass.getName();
        }
        
        @Override
        Object toOpenTypeData(final Object o) throws OpenDataException {
            if (o instanceof MemoryUsage) {
                return MemoryUsageCompositeData.toCompositeData((MemoryUsage)o);
            }
            if (o instanceof ThreadInfo) {
                return ThreadInfoCompositeData.toCompositeData((ThreadInfo)o);
            }
            if (o instanceof LockInfo) {
                if (o instanceof MonitorInfo) {
                    return MonitorInfoCompositeData.toCompositeData((MonitorInfo)o);
                }
                return LockInfoCompositeData.toCompositeData((LockInfo)o);
            }
            else {
                if (o instanceof MemoryNotificationInfo) {
                    return MemoryNotifInfoCompositeData.toCompositeData((MemoryNotificationInfo)o);
                }
                if (o instanceof VMOption) {
                    return VMOptionCompositeData.toCompositeData((VMOption)o);
                }
                if (this.isCompositeData) {
                    final CompositeData compositeData = (CompositeData)o;
                    final CompositeType compositeType = compositeData.getCompositeType();
                    final String[] array = compositeType.keySet().toArray(new String[0]);
                    return new CompositeDataSupport(compositeType, array, compositeData.getAll(array));
                }
                throw new OpenDataException(this.javaClass.getName() + " is not supported for platform MXBeans");
            }
        }
        
        @Override
        Object toJavaTypeData(final Object o) throws OpenDataException, InvalidObjectException {
            if (this.fromMethod == null) {
                throw new AssertionError((Object)"Does not support data conversion");
            }
            try {
                return this.fromMethod.invoke(null, o);
            }
            catch (final IllegalAccessException ex) {
                throw new AssertionError((Object)ex);
            }
            catch (final InvocationTargetException ex2) {
                final OpenDataException ex3 = new OpenDataException("Failed to invoke " + this.fromMethod.getName() + " to convert CompositeData  to " + this.javaClass.getName());
                ex3.initCause(ex2);
                throw ex3;
            }
        }
    }
    
    private static class InProgress extends OpenType
    {
        private static final String description = "Marker to detect recursive type use -- internal use only!";
        private static final long serialVersionUID = -3413063475064374490L;
        
        InProgress() throws OpenDataException {
            super("java.lang.String", "java.lang.String", "Marker to detect recursive type use -- internal use only!");
        }
        
        @Override
        public String toString() {
            return "Marker to detect recursive type use -- internal use only!";
        }
        
        @Override
        public int hashCode() {
            return 0;
        }
        
        @Override
        public boolean equals(final Object o) {
            return false;
        }
        
        @Override
        public boolean isValue(final Object o) {
            return false;
        }
    }
}
