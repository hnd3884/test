package org.glassfish.hk2.utilities.reflection;

import org.glassfish.hk2.utilities.reflection.internal.MethodWrapperImpl;
import java.lang.reflect.WildcardType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.Collection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.AnnotatedElement;
import javax.inject.Qualifier;
import javax.inject.Scope;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.Set;
import javax.inject.Named;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import java.util.Map;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;

public final class ReflectionHelper
{
    private static final HashSet<Character> ESCAPE_CHARACTERS;
    private static final char[] ILLEGAL_CHARACTERS;
    private static final HashMap<Character, Character> REPLACE_CHARACTERS;
    private static final String EQUALS_STRING = "=";
    private static final String COMMA_STRING = ",";
    private static final String QUOTE_STRING = "\"";
    
    public static Class<?> getRawClass(final Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof GenericArrayType) {
            final Type componentType = ((GenericArrayType)type).getGenericComponentType();
            if (!(componentType instanceof ParameterizedType) && !(componentType instanceof Class)) {
                return null;
            }
            final Class<?> rawComponentClass = getRawClass(componentType);
            final String forNameName = "[L" + rawComponentClass.getName() + ";";
            try {
                return Class.forName(forNameName);
            }
            catch (final Throwable th) {
                return null;
            }
        }
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType)type).getRawType();
            if (rawType instanceof Class) {
                return (Class)rawType;
            }
        }
        return null;
    }
    
    public static Type resolveField(final Class<?> topclass, final Field field) {
        return resolveMember(topclass, field.getGenericType(), field.getDeclaringClass());
    }
    
    public static Type resolveMember(final Class<?> topclass, final Type lookingForType, final Class<?> declaringClass) {
        final Map<String, Type> typeArguments = typesFromSubClassToDeclaringClass(topclass, declaringClass);
        if (typeArguments == null) {
            return lookingForType;
        }
        if (lookingForType instanceof ParameterizedType) {
            return fixTypeVariables((ParameterizedType)lookingForType, typeArguments);
        }
        if (lookingForType instanceof GenericArrayType) {
            return fixGenericArrayTypeVariables((GenericArrayType)lookingForType, typeArguments);
        }
        if (!(lookingForType instanceof TypeVariable)) {
            return lookingForType;
        }
        final TypeVariable<?> tv = (TypeVariable<?>)lookingForType;
        final String typeVariableName = tv.getName();
        final Type retVal = typeArguments.get(typeVariableName);
        if (retVal == null) {
            return lookingForType;
        }
        if (retVal instanceof Class) {
            return retVal;
        }
        if (retVal instanceof ParameterizedType) {
            return fixTypeVariables((ParameterizedType)retVal, typeArguments);
        }
        if (retVal instanceof GenericArrayType) {
            return fixGenericArrayTypeVariables((GenericArrayType)retVal, typeArguments);
        }
        return retVal;
    }
    
    public static Type resolveKnownType(final TypeVariable<?> userType, final ParameterizedType knownType, final Class<?> knownDeclaringClass) {
        final TypeVariable<?>[] knownTypeVariables = knownDeclaringClass.getTypeParameters();
        for (int lcv = 0; lcv < knownTypeVariables.length; ++lcv) {
            final TypeVariable<?> knownTypeVariable = knownTypeVariables[lcv];
            if (GeneralUtilities.safeEquals(knownTypeVariable.getName(), userType.getName())) {
                return knownType.getActualTypeArguments()[lcv];
            }
        }
        return null;
    }
    
    private static Map<String, Type> typesFromSubClassToDeclaringClass(final Class<?> topClass, final Class<?> declaringClass) {
        if (topClass.equals(declaringClass)) {
            return null;
        }
        Type superType = topClass.getGenericSuperclass();
        Class<?> superClass = getRawClass(superType);
        while (superType != null && superClass != null) {
            if (!(superType instanceof ParameterizedType)) {
                if (superClass.equals(declaringClass)) {
                    return null;
                }
                superType = superClass.getGenericSuperclass();
                superClass = getRawClass(superType);
            }
            else {
                final ParameterizedType superPT = (ParameterizedType)superType;
                final Map<String, Type> typeArguments = getTypeArguments(superClass, superPT);
                if (superClass.equals(declaringClass)) {
                    return typeArguments;
                }
                superType = superClass.getGenericSuperclass();
                superClass = getRawClass(superType);
                if (!(superType instanceof ParameterizedType)) {
                    continue;
                }
                superType = fixTypeVariables((ParameterizedType)superType, typeArguments);
            }
        }
        throw new AssertionError((Object)(topClass.getName() + " is not the same as or a subclass of " + declaringClass.getName()));
    }
    
    public static Type getFirstTypeArgument(final Type type) {
        if (type instanceof Class) {
            return Object.class;
        }
        if (!(type instanceof ParameterizedType)) {
            return Object.class;
        }
        final ParameterizedType pt = (ParameterizedType)type;
        final Type[] arguments = pt.getActualTypeArguments();
        if (arguments.length <= 0) {
            return Object.class;
        }
        return arguments[0];
    }
    
    private static String getNamedName(final Named named, final Class<?> implClass) {
        final String name = named.value();
        if (name != null && !name.equals("")) {
            return name;
        }
        final String cn = implClass.getName();
        final int index = cn.lastIndexOf(".");
        if (index < 0) {
            return cn;
        }
        return cn.substring(index + 1);
    }
    
    public static String getName(final Class<?> implClass) {
        final Named named = implClass.getAnnotation(Named.class);
        final String namedName = (named != null) ? getNamedName(named, implClass) : null;
        if (namedName != null) {
            return namedName;
        }
        return null;
    }
    
    private static void addAllGenericInterfaces(Class<?> rawClass, final Type type, final Set<Type> closures) {
        Map<String, Type> typeArgumentsMap = null;
        for (Type currentType : rawClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType && currentType instanceof ParameterizedType) {
                if (typeArgumentsMap == null) {
                    typeArgumentsMap = getTypeArguments(rawClass, (ParameterizedType)type);
                }
                currentType = fixTypeVariables((ParameterizedType)currentType, typeArgumentsMap);
            }
            closures.add(currentType);
            rawClass = getRawClass(currentType);
            if (rawClass != null) {
                addAllGenericInterfaces(rawClass, currentType, closures);
            }
        }
    }
    
    private static Type fixTypeVariables(final ParameterizedType type, final Map<String, Type> typeArgumentsMap) {
        final Type[] newTypeArguments = getNewTypeArguments(type, typeArgumentsMap);
        if (newTypeArguments != null) {
            return new ParameterizedTypeImpl(type.getRawType(), newTypeArguments);
        }
        return type;
    }
    
    private static Type fixGenericArrayTypeVariables(final GenericArrayType type, final Map<String, Type> typeArgumentsMap) {
        final Type newTypeArgument = getNewTypeArrayArguments(type, typeArgumentsMap);
        if (newTypeArgument == null) {
            return type;
        }
        if (newTypeArgument instanceof Class) {
            return getArrayOfType((Class<?>)newTypeArgument);
        }
        if (newTypeArgument instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)newTypeArgument;
            if (pt.getRawType() instanceof Class) {
                return getArrayOfType((Class<?>)pt.getRawType());
            }
        }
        return new GenericArrayTypeImpl(newTypeArgument);
    }
    
    private static Class<?> getArrayOfType(final Class<?> type) {
        return Array.newInstance(type, 0).getClass();
    }
    
    private static Type[] getNewTypeArguments(final ParameterizedType type, final Map<String, Type> typeArgumentsMap) {
        final Type[] typeArguments = type.getActualTypeArguments();
        final Type[] newTypeArguments = new Type[typeArguments.length];
        boolean newArgsNeeded = false;
        int i = 0;
        for (final Type argType : typeArguments) {
            if (argType instanceof TypeVariable) {
                newTypeArguments[i] = typeArgumentsMap.get(((TypeVariable)argType).getName());
                newArgsNeeded = true;
            }
            else if (argType instanceof ParameterizedType) {
                final ParameterizedType original = (ParameterizedType)argType;
                final Type[] internalTypeArgs = getNewTypeArguments(original, typeArgumentsMap);
                if (internalTypeArgs != null) {
                    newTypeArguments[i] = new ParameterizedTypeImpl(original.getRawType(), internalTypeArgs);
                    newArgsNeeded = true;
                }
                else {
                    newTypeArguments[i] = argType;
                }
            }
            else if (argType instanceof GenericArrayType) {
                final GenericArrayType gat = (GenericArrayType)argType;
                final Type internalTypeArg = getNewTypeArrayArguments(gat, typeArgumentsMap);
                if (internalTypeArg != null) {
                    if (internalTypeArg instanceof Class) {
                        newTypeArguments[i] = getArrayOfType((Class<?>)internalTypeArg);
                        newArgsNeeded = true;
                    }
                    else if (internalTypeArg instanceof ParameterizedType && ((ParameterizedType)internalTypeArg).getRawType() instanceof Class) {
                        final ParameterizedType pt = (ParameterizedType)internalTypeArg;
                        newTypeArguments[i] = getArrayOfType((Class<?>)pt.getRawType());
                        newArgsNeeded = true;
                    }
                    else {
                        newTypeArguments[i] = new GenericArrayTypeImpl(internalTypeArg);
                        newArgsNeeded = true;
                    }
                }
                else {
                    newTypeArguments[i] = argType;
                }
            }
            else {
                newTypeArguments[i] = argType;
            }
            ++i;
        }
        return (Type[])(newArgsNeeded ? newTypeArguments : null);
    }
    
    private static Type getNewTypeArrayArguments(final GenericArrayType gat, final Map<String, Type> typeArgumentsMap) {
        final Type typeArgument = gat.getGenericComponentType();
        if (typeArgument instanceof TypeVariable) {
            return typeArgumentsMap.get(((TypeVariable)typeArgument).getName());
        }
        if (typeArgument instanceof ParameterizedType) {
            final ParameterizedType original = (ParameterizedType)typeArgument;
            final Type[] internalTypeArgs = getNewTypeArguments(original, typeArgumentsMap);
            if (internalTypeArgs != null) {
                return new ParameterizedTypeImpl(original.getRawType(), internalTypeArgs);
            }
            return original;
        }
        else {
            if (!(typeArgument instanceof GenericArrayType)) {
                return null;
            }
            final GenericArrayType original2 = (GenericArrayType)typeArgument;
            final Type internalTypeArg = getNewTypeArrayArguments(original2, typeArgumentsMap);
            if (internalTypeArg == null) {
                return null;
            }
            if (internalTypeArg instanceof Class) {
                return getArrayOfType((Class<?>)internalTypeArg);
            }
            if (internalTypeArg instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)internalTypeArg;
                if (pt.getRawType() instanceof Class) {
                    return getArrayOfType((Class<?>)pt.getRawType());
                }
            }
            return new GenericArrayTypeImpl(internalTypeArg);
        }
    }
    
    private static Map<String, Type> getTypeArguments(final Class<?> rawClass, final ParameterizedType type) {
        final Map<String, Type> typeMap = new HashMap<String, Type>();
        final Type[] typeArguments = type.getActualTypeArguments();
        int i = 0;
        for (final TypeVariable<?> typeVariable : rawClass.getTypeParameters()) {
            typeMap.put(typeVariable.getName(), typeArguments[i++]);
        }
        return typeMap;
    }
    
    private static Set<Type> getTypeClosure(final Type ofType) {
        final Set<Type> retVal = new HashSet<Type>();
        Class<?> rawClass = getRawClass(ofType);
        if (rawClass != null) {
            Map<String, Type> typeArgumentsMap = null;
            Type currentType = ofType;
            while (currentType != null && rawClass != null) {
                retVal.add(currentType);
                addAllGenericInterfaces(rawClass, currentType, retVal);
                if (typeArgumentsMap == null && currentType instanceof ParameterizedType) {
                    typeArgumentsMap = getTypeArguments(rawClass, (ParameterizedType)currentType);
                }
                currentType = rawClass.getGenericSuperclass();
                if (currentType != null) {
                    rawClass = getRawClass(currentType);
                    if (typeArgumentsMap == null || !(currentType instanceof ParameterizedType)) {
                        continue;
                    }
                    currentType = fixTypeVariables((ParameterizedType)currentType, typeArgumentsMap);
                }
            }
        }
        return retVal;
    }
    
    public static Set<Type> getTypeClosure(final Type ofType, final Set<String> contracts) {
        final Set<Type> closure = getTypeClosure(ofType);
        final HashSet<Type> retVal = new HashSet<Type>();
        for (final Type t : closure) {
            final Class<?> rawClass = getRawClass(t);
            if (rawClass == null) {
                continue;
            }
            if (!contracts.contains(rawClass.getName())) {
                continue;
            }
            retVal.add(t);
        }
        return retVal;
    }
    
    public static Set<Type> getAdvertisedTypesFromClass(final Type type, final Class<? extends Annotation> markerAnnotation) {
        final Set<Type> retVal = new LinkedHashSet<Type>();
        if (type == null) {
            return retVal;
        }
        retVal.add(type);
        Class<?> originalRawClass = getRawClass(type);
        if (originalRawClass == null) {
            return retVal;
        }
        Class<?> rawClass;
        for (Type genericSuperclass = originalRawClass.getGenericSuperclass(); genericSuperclass != null; genericSuperclass = rawClass.getGenericSuperclass()) {
            rawClass = getRawClass(genericSuperclass);
            if (rawClass == null) {
                break;
            }
            if (rawClass.isAnnotationPresent(markerAnnotation)) {
                retVal.add(genericSuperclass);
            }
        }
        final Set<Class<?>> alreadyHandled = new HashSet<Class<?>>();
        while (originalRawClass != null) {
            getAllContractsFromInterfaces(originalRawClass, markerAnnotation, retVal, alreadyHandled);
            originalRawClass = originalRawClass.getSuperclass();
        }
        return retVal;
    }
    
    private static void getAllContractsFromInterfaces(final Class<?> clazzOrInterface, final Class<? extends Annotation> markerAnnotation, final Set<Type> addToMe, final Set<Class<?>> alreadyHandled) {
        final Type[] genericInterfaces;
        final Type[] interfacesAsType = genericInterfaces = clazzOrInterface.getGenericInterfaces();
        for (final Type interfaceAsType : genericInterfaces) {
            final Class<?> interfaceAsClass = getRawClass(interfaceAsType);
            if (interfaceAsClass != null) {
                if (!alreadyHandled.contains(interfaceAsClass)) {
                    alreadyHandled.add(interfaceAsClass);
                    if (interfaceAsClass.isAnnotationPresent(markerAnnotation)) {
                        addToMe.add(interfaceAsType);
                    }
                    getAllContractsFromInterfaces(interfaceAsClass, markerAnnotation, addToMe, alreadyHandled);
                }
            }
        }
    }
    
    public static Set<Type> getAdvertisedTypesFromObject(final Object t, final Class<? extends Annotation> markerAnnotation) {
        if (t == null) {
            return Collections.emptySet();
        }
        return getAdvertisedTypesFromClass(t.getClass(), markerAnnotation);
    }
    
    public static Set<String> getContractsFromClass(Class<?> clazz, final Class<? extends Annotation> markerAnnotation) {
        final Set<String> retVal = new LinkedHashSet<String>();
        if (clazz == null) {
            return retVal;
        }
        retVal.add(clazz.getName());
        for (Class<?> extendsClasses = clazz.getSuperclass(); extendsClasses != null; extendsClasses = extendsClasses.getSuperclass()) {
            if (extendsClasses.isAnnotationPresent(markerAnnotation)) {
                retVal.add(extendsClasses.getName());
            }
        }
        while (clazz != null) {
            final Class<?>[] interfaces2;
            final Class<?>[] interfaces = interfaces2 = clazz.getInterfaces();
            for (final Class<?> iFace : interfaces2) {
                if (iFace.isAnnotationPresent(markerAnnotation)) {
                    retVal.add(iFace.getName());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return retVal;
    }
    
    public static Annotation getScopeAnnotationFromObject(final Object t) {
        if (t == null) {
            throw new IllegalArgumentException();
        }
        return getScopeAnnotationFromClass(t.getClass());
    }
    
    public static Annotation getScopeAnnotationFromClass(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        for (final Annotation annotation : clazz.getAnnotations()) {
            final Class<? extends Annotation> annoClass = annotation.annotationType();
            if (annoClass.isAnnotationPresent((Class<? extends Annotation>)Scope.class)) {
                return annotation;
            }
        }
        return null;
    }
    
    public static Annotation getScopeFromObject(final Object t, final Annotation annoDefault) {
        if (t == null) {
            return annoDefault;
        }
        return getScopeFromClass(t.getClass(), annoDefault);
    }
    
    public static Annotation getScopeFromClass(final Class<?> clazz, final Annotation annoDefault) {
        if (clazz == null) {
            return annoDefault;
        }
        for (final Annotation annotation : clazz.getAnnotations()) {
            final Class<? extends Annotation> annoClass = annotation.annotationType();
            if (annoClass.isAnnotationPresent((Class<? extends Annotation>)Scope.class)) {
                return annotation;
            }
        }
        return annoDefault;
    }
    
    public static boolean isAnnotationAQualifier(final Annotation anno) {
        final Class<? extends Annotation> annoType = anno.annotationType();
        return annoType.isAnnotationPresent((Class<? extends Annotation>)Qualifier.class);
    }
    
    public static Set<Annotation> getQualifiersFromObject(final Object t) {
        if (t == null) {
            return Collections.emptySet();
        }
        return getQualifierAnnotations(t.getClass());
    }
    
    public static Set<String> getQualifiersFromClass(Class<?> clazz) {
        final Set<String> retVal = new LinkedHashSet<String>();
        if (clazz == null) {
            return retVal;
        }
        for (final Annotation annotation : clazz.getAnnotations()) {
            if (isAnnotationAQualifier(annotation)) {
                retVal.add(annotation.annotationType().getName());
            }
        }
        while (clazz != null) {
            for (final Class<?> iFace : clazz.getInterfaces()) {
                for (final Annotation annotation2 : iFace.getAnnotations()) {
                    if (isAnnotationAQualifier(annotation2)) {
                        retVal.add(annotation2.annotationType().getName());
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return retVal;
    }
    
    private static Set<Annotation> internalGetQualifierAnnotations(final AnnotatedElement annotatedGuy) {
        final Set<Annotation> retVal = new LinkedHashSet<Annotation>();
        if (annotatedGuy == null) {
            return retVal;
        }
        for (final Annotation annotation : annotatedGuy.getAnnotations()) {
            Label_0113: {
                if (isAnnotationAQualifier(annotation)) {
                    if (annotatedGuy instanceof Field && Named.class.equals(annotation.annotationType())) {
                        final Named n = (Named)annotation;
                        if (n.value() == null) {
                            break Label_0113;
                        }
                        if ("".equals(n.value())) {
                            break Label_0113;
                        }
                    }
                    retVal.add(annotation);
                }
            }
        }
        if (!(annotatedGuy instanceof Class)) {
            return retVal;
        }
        for (Class<?> clazz = (Class<?>)annotatedGuy; clazz != null; clazz = clazz.getSuperclass()) {
            for (final Class<?> iFace : clazz.getInterfaces()) {
                for (final Annotation annotation2 : iFace.getAnnotations()) {
                    if (isAnnotationAQualifier(annotation2)) {
                        retVal.add(annotation2);
                    }
                }
            }
        }
        return retVal;
    }
    
    public static Set<Annotation> getQualifierAnnotations(final AnnotatedElement annotatedGuy) {
        final Set<Annotation> retVal = AccessController.doPrivileged((PrivilegedAction<Set<Annotation>>)new PrivilegedAction<Set<Annotation>>() {
            @Override
            public Set<Annotation> run() {
                return internalGetQualifierAnnotations(annotatedGuy);
            }
        });
        return retVal;
    }
    
    public static String writeSet(final Set<?> set) {
        return writeSet(set, null);
    }
    
    public static String writeSet(final Set<?> set, final Object excludeMe) {
        if (set == null) {
            return "{}";
        }
        final StringBuffer sb = new StringBuffer("{");
        boolean first = true;
        for (final Object writeMe : set) {
            if (excludeMe != null && excludeMe.equals(writeMe)) {
                continue;
            }
            if (first) {
                first = false;
                sb.append(escapeString(writeMe.toString()));
            }
            else {
                sb.append("," + escapeString(writeMe.toString()));
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static void readSet(final String line, final Collection<String> addToMe) throws IOException {
        final char[] asChars = new char[line.length()];
        line.getChars(0, line.length(), asChars, 0);
        internalReadSet(asChars, 0, addToMe);
    }
    
    private static int internalReadSet(final char[] asChars, final int startIndex, final Collection<String> addToMe) throws IOException {
        int dot = startIndex;
        int startOfSet = -1;
        while (dot < asChars.length) {
            if (asChars[dot] == '{') {
                startOfSet = dot;
                ++dot;
                break;
            }
            ++dot;
        }
        if (startOfSet == -1) {
            throw new IOException("Unknown set format, no initial { character : " + new String(asChars));
        }
        StringBuffer elementBuffer = new StringBuffer();
        int endOfSet = -1;
        while (dot < asChars.length) {
            char dotChar = asChars[dot];
            if (dotChar == '}') {
                addToMe.add(elementBuffer.toString());
                endOfSet = dot;
                break;
            }
            if (dotChar == ',') {
                addToMe.add(elementBuffer.toString());
                elementBuffer = new StringBuffer();
            }
            else if (dotChar != '\\') {
                elementBuffer.append(dotChar);
            }
            else {
                if (dot + 1 >= asChars.length) {
                    break;
                }
                ++dot;
                dotChar = asChars[dot];
                if (dotChar == 'n') {
                    elementBuffer.append('\n');
                }
                else if (dotChar == 'r') {
                    elementBuffer.append('\r');
                }
                else {
                    elementBuffer.append(dotChar);
                }
            }
            ++dot;
        }
        if (endOfSet == -1) {
            throw new IOException("Unknown set format, no ending } character : " + new String(asChars));
        }
        return dot - startIndex;
    }
    
    private static int readKeyStringListLine(final char[] asChars, final int startIndex, final Map<String, List<String>> addToMe) throws IOException {
        int dot = startIndex;
        int equalsIndex = -1;
        while (dot < asChars.length) {
            final char dotChar = asChars[dot];
            if (dotChar == '=') {
                equalsIndex = dot;
                break;
            }
            ++dot;
        }
        if (equalsIndex < 0) {
            throw new IOException("Unknown key-string list format, no equals: " + new String(asChars));
        }
        final String key = new String(asChars, startIndex, equalsIndex - startIndex);
        if (++dot >= asChars.length) {
            throw new IOException("Found a key with no value, " + key + " in line " + new String(asChars));
        }
        final LinkedList<String> listValues = new LinkedList<String>();
        final int addOn = internalReadSet(asChars, dot, listValues);
        if (!listValues.isEmpty()) {
            addToMe.put(key, listValues);
        }
        dot += addOn + 1;
        if (dot < asChars.length) {
            final char skipComma = asChars[dot];
            if (skipComma == ',') {
                ++dot;
            }
        }
        return dot - startIndex;
    }
    
    public static void readMetadataMap(final String line, final Map<String, List<String>> addToMe) throws IOException {
        final char[] asChars = new char[line.length()];
        line.getChars(0, line.length(), asChars, 0);
        int addMe;
        for (int dot = 0; dot < asChars.length; dot += addMe) {
            addMe = readKeyStringListLine(asChars, dot, addToMe);
        }
    }
    
    private static String escapeString(final String escapeMe) {
        final char[] asChars = new char[escapeMe.length()];
        escapeMe.getChars(0, escapeMe.length(), asChars, 0);
        final StringBuffer sb = new StringBuffer();
        for (int lcv = 0; lcv < asChars.length; ++lcv) {
            final char candidateChar = asChars[lcv];
            if (ReflectionHelper.ESCAPE_CHARACTERS.contains(candidateChar)) {
                sb.append('\\');
                sb.append(candidateChar);
            }
            else if (ReflectionHelper.REPLACE_CHARACTERS.containsKey(candidateChar)) {
                final char replaceWithMe = ReflectionHelper.REPLACE_CHARACTERS.get(candidateChar);
                sb.append('\\');
                sb.append(replaceWithMe);
            }
            else {
                sb.append(candidateChar);
            }
        }
        return sb.toString();
    }
    
    private static String writeList(final List<String> list) {
        final StringBuffer sb = new StringBuffer("{");
        boolean first = true;
        for (final String writeMe : list) {
            if (first) {
                first = false;
                sb.append(escapeString(writeMe));
            }
            else {
                sb.append("," + escapeString(writeMe));
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String writeMetadata(final Map<String, List<String>> metadata) {
        final StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (final Map.Entry<String, List<String>> entry : metadata.entrySet()) {
            if (first) {
                first = false;
                sb.append(entry.getKey() + '=');
            }
            else {
                sb.append("," + entry.getKey() + '=');
            }
            sb.append(writeList(entry.getValue()));
        }
        return sb.toString();
    }
    
    public static void addMetadata(final Map<String, List<String>> metadatas, final String key, final String value) {
        if (key == null || value == null) {
            return;
        }
        if (key.indexOf(61) >= 0) {
            throw new IllegalArgumentException("The key field may not have an = in it:" + key);
        }
        List<String> inner = metadatas.get(key);
        if (inner == null) {
            inner = new LinkedList<String>();
            metadatas.put(key, inner);
        }
        inner.add(value);
    }
    
    public static boolean removeMetadata(final Map<String, List<String>> metadatas, final String key, final String value) {
        if (key == null || value == null) {
            return false;
        }
        final List<String> inner = metadatas.get(key);
        if (inner == null) {
            return false;
        }
        final boolean retVal = inner.remove(value);
        if (inner.size() <= 0) {
            metadatas.remove(key);
        }
        return retVal;
    }
    
    public static boolean removeAllMetadata(final Map<String, List<String>> metadatas, final String key) {
        final List<String> values = metadatas.remove(key);
        return values != null && values.size() > 0;
    }
    
    public static Map<String, List<String>> deepCopyMetadata(final Map<String, List<String>> copyMe) {
        if (copyMe == null) {
            return null;
        }
        final Map<String, List<String>> retVal = new LinkedHashMap<String, List<String>>();
        for (final Map.Entry<String, List<String>> entry : copyMe.entrySet()) {
            final String key = entry.getKey();
            if (key.indexOf(61) >= 0) {
                throw new IllegalArgumentException("The key field may not have an = in it:" + key);
            }
            final List<String> values = entry.getValue();
            final LinkedList<String> valuesCopy = new LinkedList<String>();
            for (final String value : values) {
                valuesCopy.add(value);
            }
            retVal.put(key, valuesCopy);
        }
        return retVal;
    }
    
    public static void setField(final Field field, final Object instance, final Object value) throws Throwable {
        setAccessible(field);
        try {
            field.set(instance, value);
        }
        catch (final IllegalArgumentException e) {
            Logger.getLogger().debug(field.getDeclaringClass().getName(), field.getName(), e);
            throw e;
        }
        catch (final IllegalAccessException e2) {
            Logger.getLogger().debug(field.getDeclaringClass().getName(), field.getName(), e2);
            throw e2;
        }
    }
    
    public static Object invoke(Object o, final Method m, final Object[] args, final boolean neutralCCL) throws Throwable {
        if (isStatic(m)) {
            o = null;
        }
        setAccessible(m);
        ClassLoader currentCCL = null;
        Label_0025: {
            if (!neutralCCL) {
                break Label_0025;
            }
            currentCCL = getCurrentContextClassLoader();
            try {
                return m.invoke(o, args);
            }
            catch (final InvocationTargetException ite) {
                final Throwable targetException = ite.getTargetException();
                Logger.getLogger().debug(m.getDeclaringClass().getName(), m.getName(), targetException);
                throw targetException;
            }
            catch (final Throwable th) {
                Logger.getLogger().debug(m.getDeclaringClass().getName(), m.getName(), th);
                throw th;
            }
            finally {
                if (neutralCCL) {
                    setContextClassLoader(Thread.currentThread(), currentCCL);
                }
            }
        }
    }
    
    public static boolean isStatic(final Member member) {
        final int modifiers = member.getModifiers();
        return (modifiers & 0x8) != 0x0;
    }
    
    private static void setContextClassLoader(final Thread t, final ClassLoader l) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                t.setContextClassLoader(l);
                return null;
            }
        });
    }
    
    private static void setAccessible(final AccessibleObject ao) {
        if (ao.isAccessible()) {
            return;
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                ao.setAccessible(true);
                return null;
            }
        });
    }
    
    public static Object makeMe(final Constructor<?> c, final Object[] args, final boolean neutralCCL) throws Throwable {
        ClassLoader currentCCL = null;
        Label_0010: {
            if (!neutralCCL) {
                break Label_0010;
            }
            currentCCL = getCurrentContextClassLoader();
            try {
                return c.newInstance(args);
            }
            catch (final InvocationTargetException ite) {
                final Throwable targetException = ite.getTargetException();
                Logger.getLogger().debug(c.getDeclaringClass().getName(), c.getName(), targetException);
                throw targetException;
            }
            catch (final Throwable th) {
                Logger.getLogger().debug(c.getDeclaringClass().getName(), c.getName(), th);
                throw th;
            }
            finally {
                if (neutralCCL) {
                    setContextClassLoader(Thread.currentThread(), currentCCL);
                }
            }
        }
    }
    
    public static void parseServiceMetadataString(final String metadataField, final Map<String, List<String>> metadata) {
        final StringBuffer sb = new StringBuffer(metadataField);
        int dot = 0;
        int nextEquals = sb.indexOf("=", dot);
        while (nextEquals > 0) {
            final String key = sb.substring(dot, nextEquals);
            dot = nextEquals + 1;
            String value = null;
            int commaPlace;
            if (sb.charAt(dot) == '\"') {
                ++dot;
                final int nextQuote = sb.indexOf("\"", dot);
                if (nextQuote < 0) {
                    throw new IllegalStateException("Badly formed metadata \"" + metadataField + "\" for key " + key + " has a leading quote but no trailing quote");
                }
                value = sb.substring(dot, nextQuote);
                dot = nextQuote + 1;
                commaPlace = sb.indexOf(",", dot);
            }
            else {
                commaPlace = sb.indexOf(",", dot);
                if (commaPlace < 0) {
                    value = sb.substring(dot);
                }
                else {
                    value = sb.substring(dot, commaPlace);
                }
            }
            List<String> addToMe = metadata.get(key);
            if (addToMe == null) {
                addToMe = new LinkedList<String>();
                metadata.put(key, addToMe);
            }
            addToMe.add(value);
            if (commaPlace >= 0) {
                dot = commaPlace + 1;
                nextEquals = sb.indexOf("=", dot);
            }
            else {
                nextEquals = -1;
            }
        }
    }
    
    public static String getNameFromAllQualifiers(final Set<Annotation> qualifiers, final AnnotatedElement parent) throws IllegalStateException {
        for (final Annotation qualifier : qualifiers) {
            if (!Named.class.equals(qualifier.annotationType())) {
                continue;
            }
            final Named named = (Named)qualifier;
            if (named.value() == null || named.value().equals("")) {
                if (parent != null) {
                    if (parent instanceof Class) {
                        return Pretty.clazz((Class<?>)parent);
                    }
                    if (parent instanceof Field) {
                        return ((Field)parent).getName();
                    }
                }
                throw new IllegalStateException("@Named must have a value for " + parent);
            }
            return named.value();
        }
        return null;
    }
    
    private static ClassLoader getCurrentContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    public static boolean annotationContainsAll(final Set<Annotation> candidateAnnotations, final Set<Annotation> requiredAnnotations) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return candidateAnnotations.containsAll(requiredAnnotations);
            }
        });
    }
    
    public static Class<?> translatePrimitiveType(final Class<?> type) {
        final Class<?> translation = Constants.PRIMITIVE_MAP.get(type);
        if (translation == null) {
            return type;
        }
        return translation;
    }
    
    public static boolean isPrivate(final Member member) {
        final int modifiers = member.getModifiers();
        return (modifiers & 0x2) != 0x0;
    }
    
    public static Set<Type> getAllTypes(final Type t) {
        final LinkedHashSet<Type> retVal = new LinkedHashSet<Type>();
        retVal.add(t);
        Class<?> rawClass = getRawClass(t);
        if (rawClass == null) {
            return retVal;
        }
        Class<?> rawSuperclass;
        for (Type genericSuperclass = rawClass.getGenericSuperclass(); genericSuperclass != null; genericSuperclass = rawSuperclass.getGenericSuperclass()) {
            rawSuperclass = getRawClass(genericSuperclass);
            if (rawSuperclass == null) {
                break;
            }
            retVal.add(genericSuperclass);
        }
        while (rawClass != null) {
            for (final Type iface : rawClass.getGenericInterfaces()) {
                addAllInterfaceContracts(iface, retVal);
            }
            rawClass = rawClass.getSuperclass();
        }
        final LinkedHashSet<Type> altRetVal = new LinkedHashSet<Type>();
        final HashMap<Class<?>, ParameterizedType> class2TypeMap = new HashMap<Class<?>, ParameterizedType>();
        for (final Type foundType : retVal) {
            if (!(foundType instanceof ParameterizedType)) {
                altRetVal.add(foundType);
            }
            else {
                final ParameterizedType originalPt = (ParameterizedType)foundType;
                final Class<?> rawType = getRawClass(foundType);
                class2TypeMap.put(rawType, originalPt);
                if (isFilledIn(originalPt)) {
                    altRetVal.add(foundType);
                }
                else {
                    final ParameterizedType pti = fillInPT(originalPt, class2TypeMap);
                    altRetVal.add(pti);
                    class2TypeMap.put(rawType, pti);
                }
            }
        }
        return altRetVal;
    }
    
    private static ParameterizedType fillInPT(final ParameterizedType pt, final HashMap<Class<?>, ParameterizedType> class2TypeMap) {
        if (isFilledIn(pt)) {
            return pt;
        }
        final Type[] newActualArguments = new Type[pt.getActualTypeArguments().length];
        for (int outerIndex = 0; outerIndex < newActualArguments.length; ++outerIndex) {
            final Type fillMeIn = pt.getActualTypeArguments()[outerIndex];
            newActualArguments[outerIndex] = fillMeIn;
            if (fillMeIn instanceof ParameterizedType) {
                newActualArguments[outerIndex] = fillInPT((ParameterizedType)fillMeIn, class2TypeMap);
            }
            else if (fillMeIn instanceof TypeVariable) {
                final TypeVariable<Class<?>> tv = (TypeVariable<Class<?>>)fillMeIn;
                final Class<?> genericDeclaration = tv.getGenericDeclaration();
                boolean found = false;
                int count = -1;
                for (final Type parentVariable : genericDeclaration.getTypeParameters()) {
                    ++count;
                    if (parentVariable.equals(tv)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    final ParameterizedType parentPType = class2TypeMap.get(genericDeclaration);
                    if (parentPType != null) {
                        newActualArguments[outerIndex] = parentPType.getActualTypeArguments()[count];
                    }
                }
            }
        }
        final ParameterizedTypeImpl pti = new ParameterizedTypeImpl(getRawClass(pt), newActualArguments);
        return pti;
    }
    
    private static boolean isFilledIn(final ParameterizedType pt, final HashSet<ParameterizedType> recursionKiller) {
        if (recursionKiller.contains(pt)) {
            return false;
        }
        recursionKiller.add(pt);
        for (final Type t : pt.getActualTypeArguments()) {
            if (t instanceof TypeVariable) {
                return false;
            }
            if (t instanceof WildcardType) {
                return false;
            }
            if (t instanceof ParameterizedType) {
                return isFilledIn((ParameterizedType)t, recursionKiller);
            }
        }
        return true;
    }
    
    private static boolean isFilledIn(final ParameterizedType pt) {
        return isFilledIn(pt, new HashSet<ParameterizedType>());
    }
    
    private static void addAllInterfaceContracts(final Type interfaceType, final LinkedHashSet<Type> addToMe) {
        final Class<?> interfaceClass = getRawClass(interfaceType);
        if (interfaceClass == null) {
            return;
        }
        if (addToMe.contains(interfaceType)) {
            return;
        }
        addToMe.add(interfaceType);
        for (final Type extendedInterfaces : interfaceClass.getGenericInterfaces()) {
            addAllInterfaceContracts(extendedInterfaces, addToMe);
        }
    }
    
    public static MethodWrapper createMethodWrapper(final Method wrapMe) {
        return new MethodWrapperImpl(wrapMe);
    }
    
    public static <T> T cast(final Object o) {
        return (T)o;
    }
    
    static {
        ESCAPE_CHARACTERS = new HashSet<Character>();
        ILLEGAL_CHARACTERS = new char[] { '{', '}', '[', ']', ':', ';', '=', ',', '\\' };
        REPLACE_CHARACTERS = new HashMap<Character, Character>();
        for (final char illegal : ReflectionHelper.ILLEGAL_CHARACTERS) {
            ReflectionHelper.ESCAPE_CHARACTERS.add(illegal);
        }
        ReflectionHelper.REPLACE_CHARACTERS.put('\n', 'n');
        ReflectionHelper.REPLACE_CHARACTERS.put('\r', 'r');
    }
}
