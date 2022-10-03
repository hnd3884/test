package com.sun.jmx.mbeanserver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javax.management.openmbean.CompositeDataInvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.annotation.Annotation;
import java.util.BitSet;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import sun.reflect.misc.MethodUtil;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import java.util.Comparator;
import java.io.Serializable;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.WeakHashMap;
import java.lang.reflect.Array;
import javax.management.ObjectName;
import javax.management.openmbean.SimpleType;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.InvalidObjectException;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Set;
import javax.management.openmbean.TabularType;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.ArrayType;
import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.ParameterizedType;
import javax.management.JMX;
import java.lang.annotation.ElementType;
import java.lang.reflect.GenericArrayType;
import javax.management.openmbean.OpenDataException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.List;

public class DefaultMXBeanMappingFactory extends MXBeanMappingFactory
{
    private static final Mappings mappings;
    private static final List<MXBeanMapping> permanentMappings;
    private static final String[] keyArray;
    private static final String[] keyValueArray;
    private static final Map<Type, Type> inProgress;
    
    static boolean isIdentity(final MXBeanMapping mxBeanMapping) {
        return mxBeanMapping instanceof NonNullMXBeanMapping && ((NonNullMXBeanMapping)mxBeanMapping).isIdentity();
    }
    
    private static synchronized MXBeanMapping getMapping(final Type type) {
        final WeakReference weakReference = ((WeakHashMap<K, WeakReference>)DefaultMXBeanMappingFactory.mappings).get(type);
        return (weakReference == null) ? null : ((MXBeanMapping)weakReference.get());
    }
    
    private static synchronized void putMapping(final Type type, final MXBeanMapping mxBeanMapping) {
        DefaultMXBeanMappingFactory.mappings.put(type, new WeakReference<MXBeanMapping>(mxBeanMapping));
    }
    
    private static synchronized void putPermanentMapping(final Type type, final MXBeanMapping mxBeanMapping) {
        putMapping(type, mxBeanMapping);
        DefaultMXBeanMappingFactory.permanentMappings.add(mxBeanMapping);
    }
    
    @Override
    public synchronized MXBeanMapping mappingForType(final Type type, final MXBeanMappingFactory mxBeanMappingFactory) throws OpenDataException {
        if (DefaultMXBeanMappingFactory.inProgress.containsKey(type)) {
            throw new OpenDataException("Recursive data structure, including " + MXBeanIntrospector.typeName(type));
        }
        MXBeanMapping mxBeanMapping = getMapping(type);
        if (mxBeanMapping != null) {
            return mxBeanMapping;
        }
        DefaultMXBeanMappingFactory.inProgress.put(type, type);
        try {
            mxBeanMapping = this.makeMapping(type, mxBeanMappingFactory);
        }
        catch (final OpenDataException ex) {
            throw openDataException("Cannot convert type: " + MXBeanIntrospector.typeName(type), ex);
        }
        finally {
            DefaultMXBeanMappingFactory.inProgress.remove(type);
        }
        putMapping(type, mxBeanMapping);
        return mxBeanMapping;
    }
    
    private MXBeanMapping makeMapping(final Type type, final MXBeanMappingFactory mxBeanMappingFactory) throws OpenDataException {
        if (type instanceof GenericArrayType) {
            return this.makeArrayOrCollectionMapping(type, ((GenericArrayType)type).getGenericComponentType(), mxBeanMappingFactory);
        }
        if (type instanceof Class) {
            final Class clazz = (Class)type;
            if (clazz.isEnum()) {
                return makeEnumMapping(clazz, ElementType.class);
            }
            if (clazz.isArray()) {
                return this.makeArrayOrCollectionMapping(clazz, clazz.getComponentType(), mxBeanMappingFactory);
            }
            if (JMX.isMXBeanInterface(clazz)) {
                return makeMXBeanRefMapping(clazz);
            }
            return this.makeCompositeMapping(clazz, mxBeanMappingFactory);
        }
        else {
            if (type instanceof ParameterizedType) {
                return this.makeParameterizedTypeMapping((ParameterizedType)type, mxBeanMappingFactory);
            }
            throw new OpenDataException("Cannot map type: " + type);
        }
    }
    
    private static <T extends Enum<T>> MXBeanMapping makeEnumMapping(final Class<?> clazz, final Class<T> clazz2) {
        ReflectUtil.checkPackageAccess(clazz);
        return new EnumMapping<Object>(Util.cast(clazz));
    }
    
    private MXBeanMapping makeArrayOrCollectionMapping(final Type type, final Type type2, final MXBeanMappingFactory mxBeanMappingFactory) throws OpenDataException {
        final MXBeanMapping mappingForType = mxBeanMappingFactory.mappingForType(type2, mxBeanMappingFactory);
        final ArrayType<Object[]> arrayType = ArrayType.getArrayType(mappingForType.getOpenType());
        final Class<?> openClass = mappingForType.getOpenClass();
        String s;
        if (openClass.isArray()) {
            s = "[" + openClass.getName();
        }
        else {
            s = "[L" + openClass.getName() + ";";
        }
        Class<?> forName;
        try {
            forName = Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw openDataException("Cannot obtain array class", ex);
        }
        if (type instanceof ParameterizedType) {
            return new CollectionMapping(type, arrayType, forName, mappingForType);
        }
        if (isIdentity(mappingForType)) {
            return new IdentityMapping(type, arrayType);
        }
        return new ArrayMapping(type, arrayType, forName, mappingForType);
    }
    
    private MXBeanMapping makeTabularMapping(final Type type, final boolean b, final Type type2, final Type type3, final MXBeanMappingFactory mxBeanMappingFactory) throws OpenDataException {
        final String typeName = MXBeanIntrospector.typeName(type);
        final MXBeanMapping mappingForType = mxBeanMappingFactory.mappingForType(type2, mxBeanMappingFactory);
        final MXBeanMapping mappingForType2 = mxBeanMappingFactory.mappingForType(type3, mxBeanMappingFactory);
        return new TabularMapping(type, b, new TabularType(typeName, typeName, new CompositeType(typeName, typeName, DefaultMXBeanMappingFactory.keyValueArray, DefaultMXBeanMappingFactory.keyValueArray, new OpenType[] { mappingForType.getOpenType(), mappingForType2.getOpenType() }), DefaultMXBeanMappingFactory.keyArray), mappingForType, mappingForType2);
    }
    
    private MXBeanMapping makeParameterizedTypeMapping(final ParameterizedType parameterizedType, final MXBeanMappingFactory mxBeanMappingFactory) throws OpenDataException {
        final Type rawType = parameterizedType.getRawType();
        if (rawType instanceof Class) {
            final Class clazz = (Class)rawType;
            if (clazz == List.class || clazz == Set.class || clazz == SortedSet.class) {
                final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                assert actualTypeArguments.length == 1;
                if (clazz == SortedSet.class) {
                    mustBeComparable(clazz, actualTypeArguments[0]);
                }
                return this.makeArrayOrCollectionMapping(parameterizedType, actualTypeArguments[0], mxBeanMappingFactory);
            }
            else {
                final boolean b = clazz == SortedMap.class;
                if (clazz == Map.class || b) {
                    final Type[] actualTypeArguments2 = parameterizedType.getActualTypeArguments();
                    assert actualTypeArguments2.length == 2;
                    if (b) {
                        mustBeComparable(clazz, actualTypeArguments2[0]);
                    }
                    return this.makeTabularMapping(parameterizedType, b, actualTypeArguments2[0], actualTypeArguments2[1], mxBeanMappingFactory);
                }
            }
        }
        throw new OpenDataException("Cannot convert type: " + parameterizedType);
    }
    
    private static MXBeanMapping makeMXBeanRefMapping(final Type type) throws OpenDataException {
        return new MXBeanRefMapping(type);
    }
    
    private MXBeanMapping makeCompositeMapping(final Class<?> clazz, final MXBeanMappingFactory mxBeanMappingFactory) throws OpenDataException {
        final boolean b = clazz.getName().equals("com.sun.management.GcInfo") && clazz.getClassLoader() == null;
        ReflectUtil.checkPackageAccess(clazz);
        final List<Method> eliminateCovariantMethods = MBeanAnalyzer.eliminateCovariantMethods(Arrays.asList(clazz.getMethods()));
        final SortedMap<Object, Object> sortedMap = Util.newSortedMap();
        for (final Method method : eliminateCovariantMethods) {
            final String propertyName = propertyName(method);
            if (propertyName == null) {
                continue;
            }
            if (b && propertyName.equals("CompositeType")) {
                continue;
            }
            final Method method2 = (Method)sortedMap.put(decapitalize(propertyName), method);
            if (method2 != null) {
                throw new OpenDataException("Class " + clazz.getName() + " has method name clash: " + method2.getName() + ", " + method.getName());
            }
        }
        final int size = sortedMap.size();
        if (size == 0) {
            throw new OpenDataException("Can't map " + clazz.getName() + " to an open data type");
        }
        final Method[] array = new Method[size];
        final String[] array2 = new String[size];
        final OpenType[] array3 = new OpenType[size];
        int n = 0;
        for (final Map.Entry entry : sortedMap.entrySet()) {
            array2[n] = (String)entry.getKey();
            final Method method3 = (Method)entry.getValue();
            array[n] = method3;
            array3[n] = mxBeanMappingFactory.mappingForType(method3.getGenericReturnType(), mxBeanMappingFactory).getOpenType();
            ++n;
        }
        return new CompositeMapping(clazz, new CompositeType(clazz.getName(), clazz.getName(), array2, array2, array3), array2, array, mxBeanMappingFactory);
    }
    
    static InvalidObjectException invalidObjectException(final String s, final Throwable t) {
        return EnvHelp.initCause(new InvalidObjectException(s), t);
    }
    
    static InvalidObjectException invalidObjectException(final Throwable t) {
        return invalidObjectException(t.getMessage(), t);
    }
    
    static OpenDataException openDataException(final String s, final Throwable t) {
        return EnvHelp.initCause(new OpenDataException(s), t);
    }
    
    static OpenDataException openDataException(final Throwable t) {
        return openDataException(t.getMessage(), t);
    }
    
    static void mustBeComparable(final Class<?> clazz, final Type type) throws OpenDataException {
        if (!(type instanceof Class) || !Comparable.class.isAssignableFrom((Class<?>)type)) {
            throw new OpenDataException("Parameter class " + type + " of " + clazz.getName() + " does not implement " + Comparable.class.getName());
        }
    }
    
    public static String decapitalize(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        final int offsetByCodePoints = Character.offsetByCodePoints(s, 0, 1);
        if (offsetByCodePoints < s.length() && Character.isUpperCase(s.codePointAt(offsetByCodePoints))) {
            return s;
        }
        return s.substring(0, offsetByCodePoints).toLowerCase() + s.substring(offsetByCodePoints);
    }
    
    static String capitalize(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        final int offsetByCodePoints = s.offsetByCodePoints(0, 1);
        return s.substring(0, offsetByCodePoints).toUpperCase() + s.substring(offsetByCodePoints);
    }
    
    public static String propertyName(final Method method) {
        String s = null;
        final String name = method.getName();
        if (name.startsWith("get")) {
            s = name.substring(3);
        }
        else if (name.startsWith("is") && method.getReturnType() == Boolean.TYPE) {
            s = name.substring(2);
        }
        if (s == null || s.length() == 0 || method.getParameterTypes().length > 0 || method.getReturnType() == Void.TYPE || name.equals("getClass")) {
            return null;
        }
        return s;
    }
    
    static {
        mappings = new Mappings();
        permanentMappings = Util.newList();
        final OpenType[] array = { SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID };
        for (int i = 0; i < array.length; ++i) {
            final OpenType openType = array[i];
            Class<?> forName;
            try {
                forName = Class.forName(openType.getClassName(), false, ObjectName.class.getClassLoader());
            }
            catch (final ClassNotFoundException ex) {
                throw new Error(ex);
            }
            putPermanentMapping(forName, new IdentityMapping(forName, openType));
            if (forName.getName().startsWith("java.lang.")) {
                try {
                    final Class clazz = (Class)forName.getField("TYPE").get(null);
                    putPermanentMapping(clazz, new IdentityMapping(clazz, openType));
                    if (clazz != Void.TYPE) {
                        final Class<?> class1 = Array.newInstance(clazz, 0).getClass();
                        putPermanentMapping(class1, new IdentityMapping(class1, ArrayType.getPrimitiveArrayType(class1)));
                    }
                }
                catch (final NoSuchFieldException ex2) {}
                catch (final IllegalAccessException ex3) {
                    assert false;
                }
            }
        }
        keyArray = new String[] { "key" };
        keyValueArray = new String[] { "key", "value" };
        inProgress = Util.newIdentityHashMap();
    }
    
    abstract static class NonNullMXBeanMapping extends MXBeanMapping
    {
        NonNullMXBeanMapping(final Type type, final OpenType<?> openType) {
            super(type, openType);
        }
        
        @Override
        public final Object fromOpenValue(final Object o) throws InvalidObjectException {
            if (o == null) {
                return null;
            }
            return this.fromNonNullOpenValue(o);
        }
        
        @Override
        public final Object toOpenValue(final Object o) throws OpenDataException {
            if (o == null) {
                return null;
            }
            return this.toNonNullOpenValue(o);
        }
        
        abstract Object fromNonNullOpenValue(final Object p0) throws InvalidObjectException;
        
        abstract Object toNonNullOpenValue(final Object p0) throws OpenDataException;
        
        boolean isIdentity() {
            return false;
        }
    }
    
    private static final class Mappings extends WeakHashMap<Type, WeakReference<MXBeanMapping>>
    {
    }
    
    private static final class IdentityMapping extends NonNullMXBeanMapping
    {
        IdentityMapping(final Type type, final OpenType<?> openType) {
            super(type, openType);
        }
        
        @Override
        boolean isIdentity() {
            return true;
        }
        
        @Override
        Object fromNonNullOpenValue(final Object o) throws InvalidObjectException {
            return o;
        }
        
        @Override
        Object toNonNullOpenValue(final Object o) throws OpenDataException {
            return o;
        }
    }
    
    private static final class EnumMapping<T extends Enum<T>> extends NonNullMXBeanMapping
    {
        private final Class<T> enumClass;
        
        EnumMapping(final Class<T> enumClass) {
            super(enumClass, SimpleType.STRING);
            this.enumClass = enumClass;
        }
        
        @Override
        final Object toNonNullOpenValue(final Object o) {
            return ((Enum)o).name();
        }
        
        @Override
        final T fromNonNullOpenValue(final Object o) throws InvalidObjectException {
            try {
                return Enum.valueOf(this.enumClass, (String)o);
            }
            catch (final Exception ex) {
                throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot convert to enum: " + o, ex);
            }
        }
    }
    
    private static final class ArrayMapping extends NonNullMXBeanMapping
    {
        private final MXBeanMapping elementMapping;
        
        ArrayMapping(final Type type, final ArrayType<?> arrayType, final Class<?> clazz, final MXBeanMapping elementMapping) {
            super(type, arrayType);
            this.elementMapping = elementMapping;
        }
        
        @Override
        final Object toNonNullOpenValue(final Object o) throws OpenDataException {
            final Object[] array = (Object[])o;
            final int length = array.length;
            final Object[] array2 = (Object[])Array.newInstance(this.getOpenClass().getComponentType(), length);
            for (int i = 0; i < length; ++i) {
                array2[i] = this.elementMapping.toOpenValue(array[i]);
            }
            return array2;
        }
        
        @Override
        final Object fromNonNullOpenValue(final Object o) throws InvalidObjectException {
            final Object[] array = (Object[])o;
            final Type javaType = this.getJavaType();
            Type type;
            if (javaType instanceof GenericArrayType) {
                type = ((GenericArrayType)javaType).getGenericComponentType();
            }
            else {
                if (!(javaType instanceof Class) || !((Class)javaType).isArray()) {
                    throw new IllegalArgumentException("Not an array: " + javaType);
                }
                type = ((Class)javaType).getComponentType();
            }
            final Object[] array2 = (Object[])Array.newInstance((Class<?>)type, array.length);
            for (int i = 0; i < array.length; ++i) {
                array2[i] = this.elementMapping.fromOpenValue(array[i]);
            }
            return array2;
        }
        
        @Override
        public void checkReconstructible() throws InvalidObjectException {
            this.elementMapping.checkReconstructible();
        }
    }
    
    private static final class CollectionMapping extends NonNullMXBeanMapping
    {
        private final Class<? extends Collection<?>> collectionClass;
        private final MXBeanMapping elementMapping;
        
        CollectionMapping(final Type type, final ArrayType<?> arrayType, final Class<?> clazz, final MXBeanMapping elementMapping) {
            super(type, arrayType);
            this.elementMapping = elementMapping;
            final Class clazz2 = (Class)((ParameterizedType)type).getRawType();
            Serializable s;
            if (clazz2 == List.class) {
                s = ArrayList.class;
            }
            else if (clazz2 == Set.class) {
                s = HashSet.class;
            }
            else if (clazz2 == SortedSet.class) {
                s = TreeSet.class;
            }
            else {
                assert false;
                s = null;
            }
            this.collectionClass = (Class<? extends Collection<?>>)Util.cast(s);
        }
        
        @Override
        final Object toNonNullOpenValue(final Object o) throws OpenDataException {
            final Collection collection = (Collection)o;
            if (collection instanceof SortedSet) {
                final Comparator comparator = ((SortedSet)collection).comparator();
                if (comparator != null) {
                    final String string = "Cannot convert SortedSet with non-null comparator: " + comparator;
                    throw DefaultMXBeanMappingFactory.openDataException(string, new IllegalArgumentException(string));
                }
            }
            final Object[] array = (Object[])Array.newInstance(this.getOpenClass().getComponentType(), collection.size());
            int n = 0;
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                array[n++] = this.elementMapping.toOpenValue(iterator.next());
            }
            return array;
        }
        
        @Override
        final Object fromNonNullOpenValue(final Object o) throws InvalidObjectException {
            final Object[] array = (Object[])o;
            Collection collection;
            try {
                collection = Util.cast(this.collectionClass.newInstance());
            }
            catch (final Exception ex) {
                throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot create collection", ex);
            }
            for (final Object o2 : array) {
                if (!collection.add(this.elementMapping.fromOpenValue(o2))) {
                    throw new InvalidObjectException("Could not add " + o2 + " to " + this.collectionClass.getName() + " (duplicate set element?)");
                }
            }
            return collection;
        }
        
        @Override
        public void checkReconstructible() throws InvalidObjectException {
            this.elementMapping.checkReconstructible();
        }
    }
    
    private static final class MXBeanRefMapping extends NonNullMXBeanMapping
    {
        MXBeanRefMapping(final Type type) {
            super(type, SimpleType.OBJECTNAME);
        }
        
        @Override
        final Object toNonNullOpenValue(final Object o) throws OpenDataException {
            final ObjectName mxbeanToObjectName = this.lookupNotNull(OpenDataException.class).mxbeanToObjectName(o);
            if (mxbeanToObjectName == null) {
                throw new OpenDataException("No name for object: " + o);
            }
            return mxbeanToObjectName;
        }
        
        @Override
        final Object fromNonNullOpenValue(final Object o) throws InvalidObjectException {
            final MXBeanLookup lookupNotNull = this.lookupNotNull(InvalidObjectException.class);
            final ObjectName objectName = (ObjectName)o;
            final Object objectNameToMXBean = lookupNotNull.objectNameToMXBean(objectName, (Class<Object>)this.getJavaType());
            if (objectNameToMXBean == null) {
                throw new InvalidObjectException("No MXBean for name: " + objectName);
            }
            return objectNameToMXBean;
        }
        
        private <T extends Exception> MXBeanLookup lookupNotNull(final Class<T> clazz) throws T, Exception {
            final MXBeanLookup lookup = MXBeanLookup.getLookup();
            if (lookup == null) {
                Exception ex;
                try {
                    ex = clazz.getConstructor(String.class).newInstance("Cannot convert MXBean interface in this context");
                }
                catch (final Exception ex2) {
                    throw new RuntimeException(ex2);
                }
                throw ex;
            }
            return lookup;
        }
    }
    
    private static final class TabularMapping extends NonNullMXBeanMapping
    {
        private final boolean sortedMap;
        private final MXBeanMapping keyMapping;
        private final MXBeanMapping valueMapping;
        
        TabularMapping(final Type type, final boolean sortedMap, final TabularType tabularType, final MXBeanMapping keyMapping, final MXBeanMapping valueMapping) {
            super(type, tabularType);
            this.sortedMap = sortedMap;
            this.keyMapping = keyMapping;
            this.valueMapping = valueMapping;
        }
        
        @Override
        final Object toNonNullOpenValue(final Object o) throws OpenDataException {
            final Map map = Util.cast(o);
            if (map instanceof SortedMap) {
                final Comparator comparator = ((SortedMap)map).comparator();
                if (comparator != null) {
                    final String string = "Cannot convert SortedMap with non-null comparator: " + comparator;
                    throw DefaultMXBeanMappingFactory.openDataException(string, new IllegalArgumentException(string));
                }
            }
            final TabularType tabularType = (TabularType)this.getOpenType();
            final TabularDataSupport tabularDataSupport = new TabularDataSupport(tabularType);
            final CompositeType rowType = tabularType.getRowType();
            for (final Map.Entry entry : map.entrySet()) {
                tabularDataSupport.put(new CompositeDataSupport(rowType, DefaultMXBeanMappingFactory.keyValueArray, new Object[] { this.keyMapping.toOpenValue(entry.getKey()), this.valueMapping.toOpenValue(entry.getValue()) }));
            }
            return tabularDataSupport;
        }
        
        @Override
        final Object fromNonNullOpenValue(final Object o) throws InvalidObjectException {
            final Collection collection = Util.cast(((TabularData)o).values());
            final Map<Object, Object> map = this.sortedMap ? Util.newSortedMap() : Util.newInsertionOrderMap();
            for (final CompositeData compositeData : collection) {
                final Object fromOpenValue = this.keyMapping.fromOpenValue(compositeData.get("key"));
                if (map.put(fromOpenValue, this.valueMapping.fromOpenValue(compositeData.get("value"))) != null) {
                    throw new InvalidObjectException("Duplicate entry in TabularData: key=" + fromOpenValue);
                }
            }
            return map;
        }
        
        @Override
        public void checkReconstructible() throws InvalidObjectException {
            this.keyMapping.checkReconstructible();
            this.valueMapping.checkReconstructible();
        }
    }
    
    private final class CompositeMapping extends NonNullMXBeanMapping
    {
        private final String[] itemNames;
        private final Method[] getters;
        private final MXBeanMapping[] getterMappings;
        private CompositeBuilder compositeBuilder;
        
        CompositeMapping(final Class<?> clazz, final CompositeType compositeType, final String[] itemNames, final Method[] getters, final MXBeanMappingFactory mxBeanMappingFactory) throws OpenDataException {
            super(clazz, compositeType);
            assert itemNames.length == getters.length;
            this.itemNames = itemNames;
            this.getters = getters;
            this.getterMappings = new MXBeanMapping[getters.length];
            for (int i = 0; i < getters.length; ++i) {
                this.getterMappings[i] = mxBeanMappingFactory.mappingForType(getters[i].getGenericReturnType(), mxBeanMappingFactory);
            }
        }
        
        @Override
        final Object toNonNullOpenValue(final Object o) throws OpenDataException {
            final CompositeType compositeType = (CompositeType)this.getOpenType();
            if (o instanceof CompositeDataView) {
                return ((CompositeDataView)o).toCompositeData(compositeType);
            }
            if (o == null) {
                return null;
            }
            final Object[] array = new Object[this.getters.length];
            for (int i = 0; i < this.getters.length; ++i) {
                try {
                    array[i] = this.getterMappings[i].toOpenValue(MethodUtil.invoke(this.getters[i], o, null));
                }
                catch (final Exception ex) {
                    throw DefaultMXBeanMappingFactory.openDataException("Error calling getter for " + this.itemNames[i] + ": " + ex, ex);
                }
            }
            return new CompositeDataSupport(compositeType, this.itemNames, array);
        }
        
        private synchronized void makeCompositeBuilder() throws InvalidObjectException {
            if (this.compositeBuilder != null) {
                return;
            }
            final Class clazz = (Class)this.getJavaType();
            final CompositeBuilder[][] array = { { new CompositeBuilderViaFrom(clazz, this.itemNames) }, { new CompositeBuilderViaConstructor(clazz, this.itemNames) }, { new CompositeBuilderCheckGetters(clazz, this.itemNames, this.getterMappings), new CompositeBuilderViaSetters(clazz, this.itemNames), new CompositeBuilderViaProxy(clazz, this.itemNames) } };
            CompositeBuilder compositeBuilder = null;
            final StringBuilder sb = new StringBuilder();
            Throwable t = null;
        Label_0268:
            for (final CompositeBuilder[] array3 : array) {
                for (int j = 0; j < array3.length; ++j) {
                    final CompositeBuilder compositeBuilder2 = array3[j];
                    final String applicable = compositeBuilder2.applicable(this.getters);
                    if (applicable == null) {
                        compositeBuilder = compositeBuilder2;
                        break Label_0268;
                    }
                    final Throwable possibleCause = compositeBuilder2.possibleCause();
                    if (possibleCause != null) {
                        t = possibleCause;
                    }
                    if (applicable.length() > 0) {
                        if (sb.length() > 0) {
                            sb.append("; ");
                        }
                        sb.append(applicable);
                        if (j == 0) {
                            break;
                        }
                    }
                }
            }
            if (compositeBuilder == null) {
                String s = "Do not know how to make a " + clazz.getName() + " from a CompositeData: " + (Object)sb;
                if (t != null) {
                    s += ". Remaining exceptions show a POSSIBLE cause.";
                }
                throw DefaultMXBeanMappingFactory.invalidObjectException(s, t);
            }
            this.compositeBuilder = compositeBuilder;
        }
        
        @Override
        public void checkReconstructible() throws InvalidObjectException {
            this.makeCompositeBuilder();
        }
        
        @Override
        final Object fromNonNullOpenValue(final Object o) throws InvalidObjectException {
            this.makeCompositeBuilder();
            return this.compositeBuilder.fromCompositeData((CompositeData)o, this.itemNames, this.getterMappings);
        }
    }
    
    private abstract static class CompositeBuilder
    {
        private final Class<?> targetClass;
        private final String[] itemNames;
        
        CompositeBuilder(final Class<?> targetClass, final String[] itemNames) {
            this.targetClass = targetClass;
            this.itemNames = itemNames;
        }
        
        Class<?> getTargetClass() {
            return this.targetClass;
        }
        
        String[] getItemNames() {
            return this.itemNames;
        }
        
        abstract String applicable(final Method[] p0) throws InvalidObjectException;
        
        Throwable possibleCause() {
            return null;
        }
        
        abstract Object fromCompositeData(final CompositeData p0, final String[] p1, final MXBeanMapping[] p2) throws InvalidObjectException;
    }
    
    private static final class CompositeBuilderViaFrom extends CompositeBuilder
    {
        private Method fromMethod;
        
        CompositeBuilderViaFrom(final Class<?> clazz, final String[] array) {
            super(clazz, array);
        }
        
        @Override
        String applicable(final Method[] array) throws InvalidObjectException {
            final Class<?> targetClass = this.getTargetClass();
            try {
                final Method method = targetClass.getMethod("from", CompositeData.class);
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new InvalidObjectException("Method from(CompositeData) is not static");
                }
                if (method.getReturnType() != this.getTargetClass()) {
                    throw new InvalidObjectException("Method from(CompositeData) returns " + MXBeanIntrospector.typeName(method.getReturnType()) + " not " + MXBeanIntrospector.typeName(targetClass));
                }
                this.fromMethod = method;
                return null;
            }
            catch (final InvalidObjectException ex) {
                throw ex;
            }
            catch (final Exception ex2) {
                return "no method from(CompositeData)";
            }
        }
        
        @Override
        final Object fromCompositeData(final CompositeData compositeData, final String[] array, final MXBeanMapping[] array2) throws InvalidObjectException {
            try {
                return MethodUtil.invoke(this.fromMethod, null, new Object[] { compositeData });
            }
            catch (final Exception ex) {
                throw DefaultMXBeanMappingFactory.invalidObjectException("Failed to invoke from(CompositeData)", ex);
            }
        }
    }
    
    private static class CompositeBuilderCheckGetters extends CompositeBuilder
    {
        private final MXBeanMapping[] getterConverters;
        private Throwable possibleCause;
        
        CompositeBuilderCheckGetters(final Class<?> clazz, final String[] array, final MXBeanMapping[] getterConverters) {
            super(clazz, array);
            this.getterConverters = getterConverters;
        }
        
        @Override
        String applicable(final Method[] array) {
            for (int i = 0; i < array.length; ++i) {
                try {
                    this.getterConverters[i].checkReconstructible();
                }
                catch (final InvalidObjectException possibleCause) {
                    this.possibleCause = possibleCause;
                    return "method " + array[i].getName() + " returns type that cannot be mapped back from OpenData";
                }
            }
            return "";
        }
        
        @Override
        Throwable possibleCause() {
            return this.possibleCause;
        }
        
        @Override
        final Object fromCompositeData(final CompositeData compositeData, final String[] array, final MXBeanMapping[] array2) {
            throw new Error();
        }
    }
    
    private static class CompositeBuilderViaSetters extends CompositeBuilder
    {
        private Method[] setters;
        
        CompositeBuilderViaSetters(final Class<?> clazz, final String[] array) {
            super(clazz, array);
        }
        
        @Override
        String applicable(final Method[] array) {
            try {
                this.getTargetClass().getConstructor((Class<?>[])new Class[0]);
            }
            catch (final Exception ex) {
                return "does not have a public no-arg constructor";
            }
            final Method[] setters = new Method[array.length];
            for (int i = 0; i < array.length; ++i) {
                final Method method = array[i];
                final Class<?> returnType = method.getReturnType();
                final String string = "set" + DefaultMXBeanMappingFactory.propertyName(method);
                Method method2;
                try {
                    method2 = this.getTargetClass().getMethod(string, returnType);
                    if (method2.getReturnType() != Void.TYPE) {
                        throw new Exception();
                    }
                }
                catch (final Exception ex2) {
                    return "not all getters have corresponding setters (" + method + ")";
                }
                setters[i] = method2;
            }
            this.setters = setters;
            return null;
        }
        
        @Override
        Object fromCompositeData(final CompositeData compositeData, final String[] array, final MXBeanMapping[] array2) throws InvalidObjectException {
            Object instance;
            try {
                final Class<?> targetClass = this.getTargetClass();
                ReflectUtil.checkPackageAccess(targetClass);
                instance = targetClass.newInstance();
                for (int i = 0; i < array.length; ++i) {
                    if (compositeData.containsKey(array[i])) {
                        MethodUtil.invoke(this.setters[i], instance, new Object[] { array2[i].fromOpenValue(compositeData.get(array[i])) });
                    }
                }
            }
            catch (final Exception ex) {
                throw DefaultMXBeanMappingFactory.invalidObjectException(ex);
            }
            return instance;
        }
    }
    
    private static final class CompositeBuilderViaConstructor extends CompositeBuilder
    {
        private List<Constr> annotatedConstructors;
        
        CompositeBuilderViaConstructor(final Class<?> clazz, final String[] array) {
            super(clazz, array);
        }
        
        @Override
        String applicable(final Method[] array) throws InvalidObjectException {
            if (!AnnotationHelper.isAvailable()) {
                return "@ConstructorProperties annotation not available";
            }
            final Constructor<?>[] constructors = this.getTargetClass().getConstructors();
            final List<Object> list = Util.newList();
            for (final Constructor<?> constructor : constructors) {
                if (Modifier.isPublic(constructor.getModifiers()) && AnnotationHelper.getPropertyNames(constructor) != null) {
                    list.add(constructor);
                }
            }
            if (list.isEmpty()) {
                return "no constructor has @ConstructorProperties annotation";
            }
            this.annotatedConstructors = Util.newList();
            final Map<Object, Object> map = Util.newMap();
            final String[] itemNames = this.getItemNames();
            for (int j = 0; j < itemNames.length; ++j) {
                map.put(itemNames[j], j);
            }
            final Set<Object> set = Util.newSet();
            for (final Constructor constructor2 : list) {
                final String[] propertyNames = AnnotationHelper.getPropertyNames(constructor2);
                final Type[] genericParameterTypes = constructor2.getGenericParameterTypes();
                if (genericParameterTypes.length != propertyNames.length) {
                    throw new InvalidObjectException("Number of constructor params does not match @ConstructorProperties annotation: " + constructor2);
                }
                final int[] array3 = new int[array.length];
                for (int k = 0; k < array.length; ++k) {
                    array3[k] = -1;
                }
                final BitSet set2 = new BitSet();
                for (int l = 0; l < propertyNames.length; ++l) {
                    final String s = propertyNames[l];
                    if (!map.containsKey(s)) {
                        String s2 = "@ConstructorProperties includes name " + s + " which does not correspond to a property";
                        for (final String s3 : map.keySet()) {
                            if (s3.equalsIgnoreCase(s)) {
                                s2 = s2 + " (differs only in case from property " + s3 + ")";
                            }
                        }
                        throw new InvalidObjectException(s2 + ": " + constructor2);
                    }
                    final int intValue = map.get(s);
                    array3[intValue] = l;
                    if (set2.get(intValue)) {
                        throw new InvalidObjectException("@ConstructorProperties contains property " + s + " more than once: " + constructor2);
                    }
                    set2.set(intValue);
                    final Type genericReturnType = array[intValue].getGenericReturnType();
                    if (!genericReturnType.equals(genericParameterTypes[l])) {
                        throw new InvalidObjectException("@ConstructorProperties gives property " + s + " of type " + genericReturnType + " for parameter  of type " + genericParameterTypes[l] + ": " + constructor2);
                    }
                }
                if (!set.add(set2)) {
                    throw new InvalidObjectException("More than one constructor has a @ConstructorProperties annotation with this set of names: " + Arrays.toString(propertyNames));
                }
                this.annotatedConstructors.add(new Constr(constructor2, array3, set2));
            }
            for (final BitSet set3 : set) {
                boolean b = false;
                for (final BitSet set4 : set) {
                    if (set3 == set4) {
                        b = true;
                    }
                    else {
                        if (!b) {
                            continue;
                        }
                        final BitSet set5 = new BitSet();
                        set5.or(set3);
                        set5.or(set4);
                        if (!set.contains(set5)) {
                            final TreeSet set6 = new TreeSet();
                            for (int n = set5.nextSetBit(0); n >= 0; n = set5.nextSetBit(n + 1)) {
                                set6.add(itemNames[n]);
                            }
                            throw new InvalidObjectException("Constructors with @ConstructorProperties annotation  would be ambiguous for these items: " + set6);
                        }
                        continue;
                    }
                }
            }
            return null;
        }
        
        @Override
        final Object fromCompositeData(final CompositeData compositeData, final String[] array, final MXBeanMapping[] array2) throws InvalidObjectException {
            final CompositeType compositeType = compositeData.getCompositeType();
            final BitSet set = new BitSet();
            for (int i = 0; i < array.length; ++i) {
                if (compositeType.getType(array[i]) != null) {
                    set.set(i);
                }
            }
            Constr constr = null;
            for (final Constr constr2 : this.annotatedConstructors) {
                if (subset(constr2.presentParams, set) && (constr == null || subset(constr.presentParams, constr2.presentParams))) {
                    constr = constr2;
                }
            }
            if (constr == null) {
                throw new InvalidObjectException("No constructor has a @ConstructorProperties for this set of items: " + compositeType.keySet());
            }
            final Object[] array3 = new Object[constr.presentParams.cardinality()];
            for (int j = 0; j < array.length; ++j) {
                if (constr.presentParams.get(j)) {
                    final Object fromOpenValue = array2[j].fromOpenValue(compositeData.get(array[j]));
                    final int n = constr.paramIndexes[j];
                    if (n >= 0) {
                        array3[n] = fromOpenValue;
                    }
                }
            }
            try {
                ReflectUtil.checkPackageAccess(constr.constructor.getDeclaringClass());
                return constr.constructor.newInstance(array3);
            }
            catch (final Exception ex) {
                throw DefaultMXBeanMappingFactory.invalidObjectException("Exception constructing " + this.getTargetClass().getName(), ex);
            }
        }
        
        private static boolean subset(final BitSet set, final BitSet set2) {
            final BitSet set3 = (BitSet)set.clone();
            set3.andNot(set2);
            return set3.isEmpty();
        }
        
        static class AnnotationHelper
        {
            private static Class<? extends Annotation> constructorPropertiesClass;
            private static Method valueMethod;
            
            private static void findConstructorPropertiesClass() {
                try {
                    AnnotationHelper.constructorPropertiesClass = (Class<? extends Annotation>)Class.forName("java.beans.ConstructorProperties", false, DefaultMXBeanMappingFactory.class.getClassLoader());
                    AnnotationHelper.valueMethod = AnnotationHelper.constructorPropertiesClass.getMethod("value", (Class<?>[])new Class[0]);
                }
                catch (final ClassNotFoundException ex) {}
                catch (final NoSuchMethodException ex2) {
                    throw new InternalError(ex2);
                }
            }
            
            static boolean isAvailable() {
                return AnnotationHelper.constructorPropertiesClass != null;
            }
            
            static String[] getPropertyNames(final Constructor<?> constructor) {
                if (!isAvailable()) {
                    return null;
                }
                final Annotation annotation = constructor.getAnnotation(AnnotationHelper.constructorPropertiesClass);
                if (annotation == null) {
                    return null;
                }
                try {
                    return (String[])AnnotationHelper.valueMethod.invoke(annotation, new Object[0]);
                }
                catch (final InvocationTargetException ex) {
                    throw new InternalError(ex);
                }
                catch (final IllegalAccessException ex2) {
                    throw new InternalError(ex2);
                }
            }
            
            static {
                findConstructorPropertiesClass();
            }
        }
        
        private static class Constr
        {
            final Constructor<?> constructor;
            final int[] paramIndexes;
            final BitSet presentParams;
            
            Constr(final Constructor<?> constructor, final int[] paramIndexes, final BitSet presentParams) {
                this.constructor = constructor;
                this.paramIndexes = paramIndexes;
                this.presentParams = presentParams;
            }
        }
    }
    
    private static final class CompositeBuilderViaProxy extends CompositeBuilder
    {
        CompositeBuilderViaProxy(final Class<?> clazz, final String[] array) {
            super(clazz, array);
        }
        
        @Override
        String applicable(final Method[] array) {
            final Class<?> targetClass = this.getTargetClass();
            if (!targetClass.isInterface()) {
                return "not an interface";
            }
            final Set<Method> set = Util.newSet(Arrays.asList(targetClass.getMethods()));
            set.removeAll(Arrays.asList(array));
            String s = null;
            for (final Method method : set) {
                final String name = method.getName();
                final Class<?>[] parameterTypes = method.getParameterTypes();
                try {
                    if (Modifier.isPublic(Object.class.getMethod(name, parameterTypes).getModifiers())) {
                        continue;
                    }
                    s = name;
                }
                catch (final NoSuchMethodException ex) {
                    s = name;
                }
            }
            if (s != null) {
                return "contains methods other than getters (" + s + ")";
            }
            return null;
        }
        
        @Override
        final Object fromCompositeData(final CompositeData compositeData, final String[] array, final MXBeanMapping[] array2) {
            final Class<?> targetClass = this.getTargetClass();
            return Proxy.newProxyInstance(targetClass.getClassLoader(), new Class[] { targetClass }, new CompositeDataInvocationHandler(compositeData));
        }
    }
}
