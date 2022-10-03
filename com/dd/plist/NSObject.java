package com.dd.plist;

import java.lang.reflect.Field;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Date;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Type;
import java.io.IOException;

public abstract class NSObject
{
    static final String NEWLINE;
    static final int ASCII_LINE_LENGTH = 80;
    private static final String INDENT = "\t";
    
    abstract void toXML(final StringBuilder p0, final int p1);
    
    void assignIDs(final BinaryPropertyListWriter out) {
        out.assignID(this);
    }
    
    abstract void toBinary(final BinaryPropertyListWriter p0) throws IOException;
    
    public String toXMLPropertyList() {
        final StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(NSObject.NEWLINE).append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">").append(NSObject.NEWLINE).append("<plist version=\"1.0\">").append(NSObject.NEWLINE);
        this.toXML(xml, 0);
        xml.append(NSObject.NEWLINE).append("</plist>");
        return xml.toString();
    }
    
    protected abstract void toASCII(final StringBuilder p0, final int p1);
    
    protected abstract void toASCIIGnuStep(final StringBuilder p0, final int p1);
    
    void indent(final StringBuilder xml, final int level) {
        for (int i = 0; i < level; ++i) {
            xml.append("\t");
        }
    }
    
    public Object toJavaObject() {
        if (this instanceof NSArray) {
            return this.deserializeArray();
        }
        if (this instanceof NSDictionary) {
            return this.deserializeMap();
        }
        if (this instanceof NSSet) {
            return this.deserializeSet();
        }
        if (this instanceof NSNumber) {
            return this.deserializeNumber();
        }
        if (this instanceof NSString) {
            return ((NSString)this).getContent();
        }
        if (this instanceof NSData) {
            return ((NSData)this).bytes();
        }
        if (this instanceof NSDate) {
            return ((NSDate)this).getDate();
        }
        if (this instanceof UID) {
            return ((UID)this).getBytes();
        }
        return this;
    }
    
    public <T> T toJavaObject(final Class<T> clazz) {
        return (T)this.toJavaObject(this, clazz, null);
    }
    
    public static NSObject fromJavaObject(final Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof NSObject) {
            return (NSObject)object;
        }
        final Class<?> objClass = object.getClass();
        if (objClass.isArray()) {
            return fromArray(object, objClass);
        }
        if (isSimple(objClass)) {
            return fromSimple(object, objClass);
        }
        if (Set.class.isAssignableFrom(objClass)) {
            return fromSet((Set<?>)object);
        }
        if (Map.class.isAssignableFrom(objClass)) {
            return fromMap((Map<?, ?>)object);
        }
        if (Collection.class.isAssignableFrom(objClass)) {
            return fromCollection((Collection<?>)object);
        }
        return fromPojo(object, objClass);
    }
    
    private static boolean isSimple(final Class<?> clazz) {
        return clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz) || clazz == String.class || Date.class.isAssignableFrom(clazz);
    }
    
    private static Object getInstance(final Class<?> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (final InstantiationException e) {
            throw new IllegalArgumentException("Could not instantiate class " + clazz.getSimpleName());
        }
        catch (final IllegalAccessException e2) {
            throw new IllegalArgumentException("Could not instantiate class " + clazz.getSimpleName());
        }
    }
    
    private static Class<?> getClassForName(String className) {
        final int spaceIndex = className.indexOf(32);
        if (spaceIndex != -1) {
            className = className.substring(spaceIndex + 1);
        }
        if ("double".equals(className)) {
            return Double.TYPE;
        }
        if ("float".equals(className)) {
            return Float.TYPE;
        }
        if ("int".equals(className)) {
            return Integer.TYPE;
        }
        if ("long".equals(className)) {
            return Long.TYPE;
        }
        if ("short".equals(className)) {
            return Short.TYPE;
        }
        if ("boolean".equals(className)) {
            return Boolean.TYPE;
        }
        if ("byte".equals(className)) {
            return Byte.TYPE;
        }
        try {
            return Class.forName(className);
        }
        catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not load class " + className, e);
        }
    }
    
    private static String makeFirstCharLowercase(final String input) {
        final char[] chars = input.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
    
    private Object toJavaObject(final NSObject payload, final Class<?> clazz, final Type[] types) {
        if (clazz.isArray()) {
            return this.deserializeArray(payload, clazz);
        }
        if (isSimple(clazz)) {
            return deserializeSimple(payload, clazz);
        }
        if (clazz == Object.class && !(payload instanceof NSSet) && !(payload instanceof NSArray)) {
            return deserializeSimple(payload, clazz);
        }
        if (payload instanceof NSSet && Collection.class.isAssignableFrom(clazz)) {
            return this.deserializeCollection(payload, clazz, types);
        }
        if (payload instanceof NSArray && Collection.class.isAssignableFrom(clazz)) {
            return this.deserializeCollection(payload, clazz, types);
        }
        if (payload instanceof NSDictionary) {
            return this.deserializeObject((NSDictionary)payload, clazz, types);
        }
        throw new IllegalArgumentException("Cannot process " + clazz.getSimpleName());
    }
    
    private Object deserializeObject(final NSDictionary payload, final Class<?> clazz, final Type[] types) {
        final Map<String, NSObject> map = payload.getHashMap();
        if (Map.class.isAssignableFrom(clazz)) {
            return this.deserializeMap(clazz, types, map);
        }
        final Object result = getInstance(clazz);
        final Map<String, Method> getters = new HashMap<String, Method>();
        final Map<String, Method> setters = new HashMap<String, Method>();
        for (final Method method : clazz.getMethods()) {
            final String name = method.getName();
            if (name.startsWith("get")) {
                getters.put(makeFirstCharLowercase(name.substring(3)), method);
            }
            else if (name.startsWith("set")) {
                setters.put(makeFirstCharLowercase(name.substring(3)), method);
            }
            else if (name.startsWith("is")) {
                getters.put(makeFirstCharLowercase(name.substring(2)), method);
            }
        }
        for (final Map.Entry<String, NSObject> entry : map.entrySet()) {
            final Method setter = setters.get(makeFirstCharLowercase(entry.getKey()));
            final Method getter = getters.get(makeFirstCharLowercase(entry.getKey()));
            if (setter != null && getter != null) {
                final Class<?> elemClass = getter.getReturnType();
                Type[] elemTypes = null;
                final Type type = getter.getGenericReturnType();
                if (type instanceof ParameterizedType) {
                    elemTypes = ((ParameterizedType)type).getActualTypeArguments();
                }
                try {
                    setter.invoke(result, this.toJavaObject(entry.getValue(), elemClass, elemTypes));
                }
                catch (final IllegalAccessException e) {
                    throw new IllegalArgumentException("Could not access setter " + setter);
                }
                catch (final InvocationTargetException e2) {
                    throw new IllegalArgumentException("Could not invoke setter " + setter);
                }
            }
        }
        return result;
    }
    
    private HashMap<String, Object> deserializeMap() {
        final HashMap<String, NSObject> originalMap = ((NSDictionary)this).getHashMap();
        final HashMap<String, Object> clonedMap = new HashMap<String, Object>(originalMap.size());
        for (final String key : originalMap.keySet()) {
            clonedMap.put(key, originalMap.get(key).toJavaObject());
        }
        return clonedMap;
    }
    
    private Object deserializeMap(final Class<?> clazz, final Type[] types, final Map<String, NSObject> map) {
        Map<String, Object> result;
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            result = new HashMap<String, Object>();
        }
        else {
            final Map<String, Object> temp = result = (Map)getInstance(clazz);
        }
        Class<?> elemClass = Object.class;
        Type[] elemParams = null;
        if (types != null && types.length > 1) {
            final Type elemType = types[1];
            if (elemType instanceof ParameterizedType) {
                elemClass = getClassForName(((ParameterizedType)elemType).getRawType().toString());
                elemParams = ((ParameterizedType)elemType).getActualTypeArguments();
            }
            else {
                elemClass = getClassForName(elemType.toString());
            }
        }
        for (final Map.Entry<String, NSObject> entry : map.entrySet()) {
            result.put(entry.getKey(), this.toJavaObject(entry.getValue(), elemClass, elemParams));
        }
        return result;
    }
    
    private Object deserializeCollection(final NSObject payload, final Class<?> clazz, final Type[] types) {
        Collection<Object> result;
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            if (List.class.isAssignableFrom(clazz)) {
                result = new ArrayList<Object>();
            }
            else {
                if (!Set.class.isAssignableFrom(clazz)) {
                    throw new IllegalArgumentException("Could not find a proper implementation for " + clazz.getSimpleName());
                }
                result = new HashSet<Object>();
            }
        }
        else {
            final Collection<Object> temp = result = (Collection)getInstance(clazz);
        }
        Class<?> elemClass = Object.class;
        Type[] elemTypes = null;
        if (types != null && types.length > 0) {
            if (types[0] instanceof ParameterizedType) {
                elemClass = getClassForName(((ParameterizedType)types[0]).getRawType().toString());
                elemTypes = ((ParameterizedType)types[0]).getActualTypeArguments();
            }
            else {
                elemClass = getClassForName(types[0].toString());
            }
        }
        if (payload instanceof NSArray) {
            for (final NSObject nsObject : ((NSArray)payload).getArray()) {
                result.add(this.toJavaObject(nsObject, elemClass, elemTypes));
            }
            return result;
        }
        if (payload instanceof NSSet) {
            for (final NSObject nsObject2 : ((NSSet)payload).getSet()) {
                result.add(this.toJavaObject(nsObject2, elemClass, elemTypes));
            }
            return result;
        }
        throw new IllegalArgumentException("Unknown NS* type " + payload.getClass().getSimpleName());
    }
    
    private Object[] deserializeArray() {
        final NSObject[] originalArray = ((NSArray)this).getArray();
        final Object[] clonedArray = new Object[originalArray.length];
        for (int i = 0; i < originalArray.length; ++i) {
            clonedArray[i] = originalArray[i].toJavaObject();
        }
        return clonedArray;
    }
    
    private Object deserializeArray(final NSObject payload, final Class<?> clazz) {
        final Class<?> elementClass = getClassForName(clazz.getComponentType().getName());
        if (payload instanceof NSArray) {
            final NSObject[] array = ((NSArray)payload).getArray();
            final Object result = Array.newInstance(elementClass, array.length);
            for (int i = 0; i < array.length; ++i) {
                Array.set(result, i, this.toJavaObject(array[i], elementClass, null));
            }
            return result;
        }
        if (payload instanceof NSSet) {
            final Set<NSObject> set = ((NSSet)payload).getSet();
            final Object result = Array.newInstance(elementClass, set.size());
            int i = 0;
            for (final NSObject aSet : set) {
                Array.set(result, i, this.toJavaObject(aSet, elementClass, null));
                ++i;
            }
            return result;
        }
        if (payload instanceof NSData) {
            return deserializeData((NSData)payload, elementClass);
        }
        throw new IllegalArgumentException("Unable to map " + payload.getClass().getSimpleName() + " to " + clazz.getName());
    }
    
    private Set<Object> deserializeSet() {
        final Set<NSObject> originalSet = ((NSSet)this).getSet();
        Set<Object> clonedSet;
        if (originalSet instanceof LinkedHashSet) {
            clonedSet = new LinkedHashSet<Object>(originalSet.size());
        }
        else {
            clonedSet = new TreeSet<Object>();
        }
        for (final NSObject o : originalSet) {
            clonedSet.add(o.toJavaObject());
        }
        return clonedSet;
    }
    
    private static Object deserializeData(final NSData payload, final Class<?> elementClass) {
        if (elementClass == Byte.TYPE) {
            return payload.bytes();
        }
        if (elementClass == Byte.class) {
            final byte[] bytes = payload.bytes();
            final Object result = Array.newInstance(elementClass, bytes.length);
            for (int i = 0; i < bytes.length; ++i) {
                Array.set(result, i, bytes[i]);
            }
            return result;
        }
        throw new IllegalArgumentException("NSData can only be mapped to byte[] or Byte[].");
    }
    
    private static Object deserializeSimple(final NSObject payload, final Class<?> clazz) {
        if (payload instanceof NSNumber) {
            return deserializeNumber((NSNumber)payload, clazz);
        }
        if (payload instanceof NSDate) {
            return deserializeDate((NSDate)payload, clazz);
        }
        if (payload instanceof NSString) {
            return ((NSString)payload).getContent();
        }
        throw new IllegalArgumentException("Cannot map " + payload.getClass().getSimpleName() + " to " + clazz.getSimpleName());
    }
    
    private static Date deserializeDate(final NSDate date, final Class<?> clazz) {
        if (clazz == Date.class) {
            return date.getDate();
        }
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return date.getDate();
        }
        final Date result = (Date)getInstance(clazz);
        result.setTime(date.getDate().getTime());
        return result;
    }
    
    private Object deserializeNumber() {
        final NSNumber num = (NSNumber)this;
        switch (num.type()) {
            case 0: {
                final long longVal = num.longValue();
                if (longVal > 2147483647L || longVal < -2147483648L) {
                    return longVal;
                }
                return num.intValue();
            }
            case 1: {
                return num.doubleValue();
            }
            case 2: {
                return num.boolValue();
            }
            default: {
                return num.doubleValue();
            }
        }
    }
    
    private static Object deserializeNumber(final NSNumber number, final Class<?> clazz) {
        if (number.isInteger()) {
            if (clazz == Long.TYPE || clazz == Long.class) {
                return number.longValue();
            }
            if (clazz == Integer.TYPE || clazz == Integer.class) {
                return number.intValue();
            }
            if (clazz == Short.TYPE || clazz == Short.class) {
                return (short)number.intValue();
            }
            if (clazz == Byte.TYPE || clazz == Byte.class) {
                return (byte)number.intValue();
            }
        }
        if (number.isReal()) {
            if (clazz == Double.TYPE || clazz == Double.class) {
                return number.doubleValue();
            }
            if (clazz == Float.TYPE || clazz == Float.class) {
                return (float)number.doubleValue();
            }
        }
        if (number.isBoolean() && (clazz == Boolean.TYPE || clazz == Boolean.class)) {
            return number.boolValue();
        }
        throw new IllegalArgumentException("Cannot map NSNumber to " + clazz.getSimpleName());
    }
    
    private static NSObject fromSimple(final Object object, final Class<?> objClass) {
        if (object instanceof Long || objClass == Long.TYPE) {
            return new NSNumber((long)object);
        }
        if (object instanceof Integer || objClass == Integer.TYPE) {
            return new NSNumber((int)object);
        }
        if (object instanceof Short || objClass == Short.TYPE) {
            return new NSNumber((short)object);
        }
        if (object instanceof Byte || objClass == Byte.TYPE) {
            return new NSNumber((byte)object);
        }
        if (object instanceof Double || objClass == Double.TYPE) {
            return new NSNumber((double)object);
        }
        if (object instanceof Float || objClass == Float.TYPE) {
            return new NSNumber((float)object);
        }
        if (object instanceof Boolean || objClass == Boolean.TYPE) {
            return new NSNumber((boolean)object);
        }
        if (object instanceof Date) {
            return new NSDate((Date)object);
        }
        if (objClass == String.class) {
            return new NSString((String)object);
        }
        throw new IllegalArgumentException("Cannot map " + objClass.getSimpleName() + " as a simple type.");
    }
    
    private static NSDictionary fromPojo(final Object object, final Class<?> objClass) {
        final NSDictionary result = new NSDictionary();
        for (final Method method : objClass.getMethods()) {
            Label_0208: {
                if (!Modifier.isNative(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                    if (method.getParameterTypes().length == 0) {
                        String name = method.getName();
                        if (name.startsWith("get")) {
                            name = makeFirstCharLowercase(name.substring(3));
                        }
                        else {
                            if (!name.startsWith("is")) {
                                break Label_0208;
                            }
                            name = makeFirstCharLowercase(name.substring(2));
                        }
                        try {
                            result.put(name, fromJavaObject(method.invoke(object, new Object[0])));
                        }
                        catch (final IllegalAccessException e) {
                            throw new IllegalArgumentException("Could not access getter " + method.getName());
                        }
                        catch (final InvocationTargetException e2) {
                            throw new IllegalArgumentException("Could not invoke getter " + method.getName());
                        }
                    }
                }
            }
        }
        for (final Field field : objClass.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                try {
                    result.put(field.getName(), fromJavaObject(field.get(object)));
                }
                catch (final IllegalAccessException e3) {
                    throw new IllegalArgumentException("Could not access field " + field.getName());
                }
            }
        }
        return result;
    }
    
    private static NSDictionary fromMap(final Map<?, ?> map) {
        final NSDictionary result = new NSDictionary();
        for (final Map.Entry entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new IllegalArgumentException("Maps need a String key for mapping to NSDictionary.");
            }
            result.put(entry.getKey(), fromJavaObject(entry.getValue()));
        }
        return result;
    }
    
    private static NSObject fromArray(final Object object, final Class<?> objClass) {
        final Class<?> elementClass = objClass.getComponentType();
        if (elementClass == Byte.TYPE || elementClass == Byte.class) {
            return fromData(object);
        }
        final int size = Array.getLength(object);
        final NSObject[] array = new NSObject[size];
        for (int i = 0; i < size; ++i) {
            array[i] = fromJavaObject(Array.get(object, i));
        }
        return new NSArray(array);
    }
    
    private static NSData fromData(final Object object) {
        final int size = Array.getLength(object);
        final byte[] array = new byte[size];
        for (int i = 0; i < size; ++i) {
            array[i] = (byte)Array.get(object, i);
        }
        return new NSData(array);
    }
    
    private static NSArray fromCollection(final Collection<?> collection) {
        final List<NSObject> payload = new ArrayList<NSObject>(collection.size());
        for (final Object elem : collection) {
            payload.add(fromJavaObject(elem));
        }
        return new NSArray((NSObject[])payload.toArray(new NSObject[payload.size()]));
    }
    
    private static NSSet fromSet(final Set<?> set) {
        final NSSet result = new NSSet();
        for (final Object elem : set) {
            result.addObject(fromJavaObject(elem));
        }
        return result;
    }
    
    static {
        NEWLINE = System.getProperty("line.separator");
    }
}
