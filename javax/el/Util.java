package javax.el;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

class Util
{
    private static final Class<?>[] EMPTY_CLASS_ARRAY;
    private static final Object[] EMPTY_OBJECT_ARRAY;
    private static final CacheValue nullTcclFactory;
    private static final ConcurrentMap<CacheKey, CacheValue> factoryCache;
    
    static void handleThrowable(final Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }
    
    static String message(final ELContext context, final String name, final Object... props) {
        Locale locale = null;
        if (context != null) {
            locale = context.getLocale();
        }
        if (locale == null) {
            locale = Locale.getDefault();
            if (locale == null) {
                return "";
            }
        }
        final ResourceBundle bundle = ResourceBundle.getBundle("javax.el.LocalStrings", locale);
        try {
            String template = bundle.getString(name);
            if (props != null) {
                template = MessageFormat.format(template, props);
            }
            return template;
        }
        catch (final MissingResourceException e) {
            return "Missing Resource: '" + name + "' for Locale " + locale.getDisplayName();
        }
    }
    
    static ExpressionFactory getExpressionFactory() {
        final ClassLoader tccl = getContextClassLoader();
        CacheValue cacheValue = null;
        ExpressionFactory factory = null;
        if (tccl == null) {
            cacheValue = Util.nullTcclFactory;
        }
        else {
            final CacheKey key = new CacheKey(tccl);
            cacheValue = Util.factoryCache.get(key);
            if (cacheValue == null) {
                final CacheValue newCacheValue = new CacheValue();
                cacheValue = Util.factoryCache.putIfAbsent(key, newCacheValue);
                if (cacheValue == null) {
                    cacheValue = newCacheValue;
                }
            }
        }
        final Lock readLock = cacheValue.getLock().readLock();
        readLock.lock();
        try {
            factory = cacheValue.getExpressionFactory();
        }
        finally {
            readLock.unlock();
        }
        if (factory == null) {
            final Lock writeLock = cacheValue.getLock().writeLock();
            writeLock.lock();
            try {
                factory = cacheValue.getExpressionFactory();
                if (factory == null) {
                    factory = ExpressionFactory.newInstance();
                    cacheValue.setExpressionFactory(factory);
                }
            }
            finally {
                writeLock.unlock();
            }
        }
        return factory;
    }
    
    static Method findMethod(final Class<?> clazz, final Object base, final String methodName, Class<?>[] paramTypes, final Object[] paramValues) {
        if (clazz == null || methodName == null) {
            throw new MethodNotFoundException(message(null, "util.method.notfound", clazz, methodName, paramString(paramTypes)));
        }
        if (paramTypes == null) {
            paramTypes = getTypesFromValues(paramValues);
        }
        final Method[] methods = clazz.getMethods();
        final List<Wrapper<Method>> wrappers = Wrapper.wrap(methods, methodName);
        final Wrapper<Method> result = findWrapper(clazz, wrappers, methodName, paramTypes, paramValues);
        return getMethod(clazz, base, result.unWrap());
    }
    
    private static <T> Wrapper<T> findWrapper(final Class<?> clazz, final List<Wrapper<T>> wrappers, final String name, final Class<?>[] paramTypes, final Object[] paramValues) {
        final Map<Wrapper<T>, MatchResult> candidates = new HashMap<Wrapper<T>, MatchResult>();
        final int paramCount = paramTypes.length;
        for (final Wrapper<T> w : wrappers) {
            final Class<?>[] mParamTypes = w.getParameterTypes();
            int mParamCount;
            if (mParamTypes == null) {
                mParamCount = 0;
            }
            else {
                mParamCount = mParamTypes.length;
            }
            if (!w.isVarArgs() && paramCount != mParamCount) {
                continue;
            }
            if (w.isVarArgs() && paramCount < mParamCount - 1) {
                continue;
            }
            if (w.isVarArgs() && paramCount == mParamCount && paramValues != null && paramValues.length > paramCount && !paramTypes[mParamCount - 1].isArray()) {
                continue;
            }
            if (w.isVarArgs() && paramCount > mParamCount && paramValues != null && paramValues.length != paramCount) {
                continue;
            }
            if (!w.isVarArgs() && paramValues != null && paramCount != paramValues.length) {
                continue;
            }
            int exactMatch = 0;
            int assignableMatch = 0;
            int coercibleMatch = 0;
            int varArgsMatch = 0;
            boolean noMatch = false;
            for (int i = 0; i < mParamCount; ++i) {
                if (w.isVarArgs() && i == mParamCount - 1) {
                    if (i == paramCount || (paramValues != null && paramValues.length == i)) {
                        varArgsMatch = Integer.MAX_VALUE;
                        break;
                    }
                    final Class<?> varType = mParamTypes[i].getComponentType();
                    for (int j = i; j < paramCount; ++j) {
                        if (isAssignableFrom(paramTypes[j], varType)) {
                            ++assignableMatch;
                            ++varArgsMatch;
                        }
                        else {
                            if (paramValues == null) {
                                noMatch = true;
                                break;
                            }
                            if (!isCoercibleFrom(paramValues[j], varType)) {
                                noMatch = true;
                                break;
                            }
                            ++coercibleMatch;
                            ++varArgsMatch;
                        }
                    }
                }
                else if (mParamTypes[i].equals(paramTypes[i])) {
                    ++exactMatch;
                }
                else if (paramTypes[i] != null && isAssignableFrom(paramTypes[i], mParamTypes[i])) {
                    ++assignableMatch;
                }
                else {
                    if (paramValues == null) {
                        noMatch = true;
                        break;
                    }
                    if (!isCoercibleFrom(paramValues[i], mParamTypes[i])) {
                        noMatch = true;
                        break;
                    }
                    ++coercibleMatch;
                }
            }
            if (noMatch) {
                continue;
            }
            if (exactMatch == paramCount && varArgsMatch == 0) {
                return w;
            }
            candidates.put(w, new MatchResult(w.isVarArgs(), exactMatch, assignableMatch, coercibleMatch, varArgsMatch, w.isBridge()));
        }
        MatchResult bestMatch = new MatchResult(true, 0, 0, 0, 0, true);
        Wrapper<T> match = null;
        boolean multiple = false;
        for (final Map.Entry<Wrapper<T>, MatchResult> entry : candidates.entrySet()) {
            final int cmp = entry.getValue().compareTo(bestMatch);
            if (cmp > 0 || match == null) {
                bestMatch = entry.getValue();
                match = entry.getKey();
                multiple = false;
            }
            else {
                if (cmp != 0) {
                    continue;
                }
                multiple = true;
            }
        }
        if (multiple) {
            if (bestMatch.getExactCount() == paramCount - 1) {
                match = resolveAmbiguousWrapper(candidates.keySet(), paramTypes);
            }
            else {
                match = null;
            }
            if (match == null) {
                throw new MethodNotFoundException(message(null, "util.method.ambiguous", clazz, name, paramString(paramTypes)));
            }
        }
        if (match == null) {
            throw new MethodNotFoundException(message(null, "util.method.notfound", clazz, name, paramString(paramTypes)));
        }
        return match;
    }
    
    private static final String paramString(final Class<?>[] types) {
        if (types != null) {
            final StringBuilder sb = new StringBuilder();
            for (final Class<?> type : types) {
                if (type == null) {
                    sb.append("null, ");
                }
                else {
                    sb.append(type.getName()).append(", ");
                }
            }
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        }
        return null;
    }
    
    private static <T> Wrapper<T> resolveAmbiguousWrapper(final Set<Wrapper<T>> candidates, final Class<?>[] paramTypes) {
        final Wrapper<T> w = candidates.iterator().next();
        int nonMatchIndex = 0;
        Class<?> nonMatchClass = null;
        for (int i = 0; i < paramTypes.length; ++i) {
            if (w.getParameterTypes()[i] != paramTypes[i]) {
                nonMatchIndex = i;
                nonMatchClass = paramTypes[i];
                break;
            }
        }
        if (nonMatchClass == null) {
            return null;
        }
        for (final Wrapper<T> c : candidates) {
            if (c.getParameterTypes()[nonMatchIndex] == paramTypes[nonMatchIndex]) {
                return null;
            }
        }
        for (Class<?> superClass = nonMatchClass.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            for (final Wrapper<T> c2 : candidates) {
                if (c2.getParameterTypes()[nonMatchIndex].equals(superClass)) {
                    return c2;
                }
            }
        }
        Wrapper<T> match = null;
        if (Number.class.isAssignableFrom(nonMatchClass)) {
            for (final Wrapper<T> c3 : candidates) {
                final Class<?> candidateType = c3.getParameterTypes()[nonMatchIndex];
                if (Number.class.isAssignableFrom(candidateType) || candidateType.isPrimitive()) {
                    if (match != null) {
                        match = null;
                        break;
                    }
                    match = c3;
                }
            }
        }
        return match;
    }
    
    static boolean isAssignableFrom(final Class<?> src, final Class<?> target) {
        if (src == null) {
            return true;
        }
        Class<?> targetClass;
        if (target.isPrimitive()) {
            if (target == Boolean.TYPE) {
                targetClass = Boolean.class;
            }
            else if (target == Character.TYPE) {
                targetClass = Character.class;
            }
            else if (target == Byte.TYPE) {
                targetClass = Byte.class;
            }
            else if (target == Short.TYPE) {
                targetClass = Short.class;
            }
            else if (target == Integer.TYPE) {
                targetClass = Integer.class;
            }
            else if (target == Long.TYPE) {
                targetClass = Long.class;
            }
            else if (target == Float.TYPE) {
                targetClass = Float.class;
            }
            else {
                targetClass = Double.class;
            }
        }
        else {
            targetClass = target;
        }
        return targetClass.isAssignableFrom(src);
    }
    
    private static boolean isCoercibleFrom(final Object src, final Class<?> target) {
        try {
            getExpressionFactory().coerceToType(src, target);
        }
        catch (final ELException e) {
            return false;
        }
        return true;
    }
    
    private static Class<?>[] getTypesFromValues(final Object[] values) {
        if (values == null) {
            return Util.EMPTY_CLASS_ARRAY;
        }
        final Class<?>[] result = new Class[values.length];
        for (int i = 0; i < values.length; ++i) {
            if (values[i] == null) {
                result[i] = null;
            }
            else {
                result[i] = values[i].getClass();
            }
        }
        return result;
    }
    
    static Method getMethod(final Class<?> type, final Object base, final Method m) {
        final JreCompat jreCompat = JreCompat.getInstance();
        if (m == null || (Modifier.isPublic(type.getModifiers()) && (jreCompat.canAccess(base, m) || (base != null && jreCompat.canAccess(null, m))))) {
            return m;
        }
        final Class<?>[] interfaces = type.getInterfaces();
        Method mp = null;
        for (final Class<?> iface : interfaces) {
            try {
                mp = iface.getMethod(m.getName(), m.getParameterTypes());
                mp = getMethod(mp.getDeclaringClass(), base, mp);
                if (mp != null) {
                    return mp;
                }
            }
            catch (final NoSuchMethodException ex) {}
        }
        final Class<?> sup = type.getSuperclass();
        if (sup != null) {
            try {
                mp = sup.getMethod(m.getName(), m.getParameterTypes());
                mp = getMethod(mp.getDeclaringClass(), base, mp);
                if (mp != null) {
                    return mp;
                }
            }
            catch (final NoSuchMethodException ex2) {}
        }
        return null;
    }
    
    static Constructor<?> findConstructor(final Class<?> clazz, Class<?>[] paramTypes, final Object[] paramValues) {
        final String methodName = "<init>";
        if (clazz == null) {
            throw new MethodNotFoundException(message(null, "util.method.notfound", null, methodName, paramString(paramTypes)));
        }
        if (paramTypes == null) {
            paramTypes = getTypesFromValues(paramValues);
        }
        final Constructor<?>[] constructors = clazz.getConstructors();
        final List<Wrapper<Constructor<?>>> wrappers = Wrapper.wrap(constructors);
        final Wrapper<Constructor<?>> wrapper = findWrapper(clazz, wrappers, methodName, paramTypes, paramValues);
        final Constructor<?> constructor = wrapper.unWrap();
        final JreCompat jreCompat = JreCompat.getInstance();
        if (!Modifier.isPublic(clazz.getModifiers()) || !jreCompat.canAccess(null, constructor)) {
            throw new MethodNotFoundException(message(null, "util.method.notfound", clazz, methodName, paramString(paramTypes)));
        }
        return constructor;
    }
    
    static Object[] buildParameters(final Class<?>[] parameterTypes, final boolean isVarArgs, Object[] params) {
        final ExpressionFactory factory = getExpressionFactory();
        Object[] parameters = null;
        if (parameterTypes.length > 0) {
            parameters = new Object[parameterTypes.length];
            if (params == null) {
                params = Util.EMPTY_OBJECT_ARRAY;
            }
            final int paramCount = params.length;
            if (isVarArgs) {
                final int varArgIndex = parameterTypes.length - 1;
                for (int i = 0; i < varArgIndex; ++i) {
                    parameters[i] = factory.coerceToType(params[i], parameterTypes[i]);
                }
                final Class<?> varArgClass = parameterTypes[varArgIndex].getComponentType();
                final Object varargs = Array.newInstance(varArgClass, paramCount - varArgIndex);
                for (int j = varArgIndex; j < paramCount; ++j) {
                    Array.set(varargs, j - varArgIndex, factory.coerceToType(params[j], varArgClass));
                }
                parameters[varArgIndex] = varargs;
            }
            else {
                parameters = new Object[parameterTypes.length];
                for (int k = 0; k < parameterTypes.length; ++k) {
                    parameters[k] = factory.coerceToType(params[k], parameterTypes[k]);
                }
            }
        }
        return parameters;
    }
    
    static ClassLoader getContextClassLoader() {
        ClassLoader tccl;
        if (System.getSecurityManager() != null) {
            final PrivilegedAction<ClassLoader> pa = new PrivilegedGetTccl();
            tccl = AccessController.doPrivileged(pa);
        }
        else {
            tccl = Thread.currentThread().getContextClassLoader();
        }
        return tccl;
    }
    
    static {
        EMPTY_CLASS_ARRAY = new Class[0];
        EMPTY_OBJECT_ARRAY = new Object[0];
        nullTcclFactory = new CacheValue();
        factoryCache = new ConcurrentHashMap<CacheKey, CacheValue>();
    }
    
    private static class CacheKey
    {
        private final int hash;
        private final WeakReference<ClassLoader> ref;
        
        public CacheKey(final ClassLoader key) {
            this.hash = key.hashCode();
            this.ref = new WeakReference<ClassLoader>(key);
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            final ClassLoader thisKey = this.ref.get();
            return thisKey != null && thisKey == ((CacheKey)obj).ref.get();
        }
    }
    
    private static class CacheValue
    {
        private final ReadWriteLock lock;
        private WeakReference<ExpressionFactory> ref;
        
        public CacheValue() {
            this.lock = new ReentrantReadWriteLock();
        }
        
        public ReadWriteLock getLock() {
            return this.lock;
        }
        
        public ExpressionFactory getExpressionFactory() {
            return (this.ref != null) ? this.ref.get() : null;
        }
        
        public void setExpressionFactory(final ExpressionFactory factory) {
            this.ref = new WeakReference<ExpressionFactory>(factory);
        }
    }
    
    private abstract static class Wrapper<T>
    {
        public static List<Wrapper<Method>> wrap(final Method[] methods, final String name) {
            final List<Wrapper<Method>> result = new ArrayList<Wrapper<Method>>();
            for (final Method method : methods) {
                if (method.getName().equals(name)) {
                    result.add(new MethodWrapper(method));
                }
            }
            return result;
        }
        
        public static List<Wrapper<Constructor<?>>> wrap(final Constructor<?>[] constructors) {
            final List<Wrapper<Constructor<?>>> result = new ArrayList<Wrapper<Constructor<?>>>();
            for (final Constructor<?> constructor : constructors) {
                result.add(new ConstructorWrapper(constructor));
            }
            return result;
        }
        
        public abstract T unWrap();
        
        public abstract Class<?>[] getParameterTypes();
        
        public abstract boolean isVarArgs();
        
        public abstract boolean isBridge();
    }
    
    private static class MethodWrapper extends Wrapper<Method>
    {
        private final Method m;
        
        public MethodWrapper(final Method m) {
            this.m = m;
        }
        
        @Override
        public Method unWrap() {
            return this.m;
        }
        
        @Override
        public Class<?>[] getParameterTypes() {
            return this.m.getParameterTypes();
        }
        
        @Override
        public boolean isVarArgs() {
            return this.m.isVarArgs();
        }
        
        @Override
        public boolean isBridge() {
            return this.m.isBridge();
        }
    }
    
    private static class ConstructorWrapper extends Wrapper<Constructor<?>>
    {
        private final Constructor<?> c;
        
        public ConstructorWrapper(final Constructor<?> c) {
            this.c = c;
        }
        
        @Override
        public Constructor<?> unWrap() {
            return this.c;
        }
        
        @Override
        public Class<?>[] getParameterTypes() {
            return this.c.getParameterTypes();
        }
        
        @Override
        public boolean isVarArgs() {
            return this.c.isVarArgs();
        }
        
        @Override
        public boolean isBridge() {
            return false;
        }
    }
    
    private static class MatchResult implements Comparable<MatchResult>
    {
        private final boolean varArgs;
        private final int exactCount;
        private final int assignableCount;
        private final int coercibleCount;
        private final int varArgsCount;
        private final boolean bridge;
        
        public MatchResult(final boolean varArgs, final int exactCount, final int assignableCount, final int coercibleCount, final int varArgsCount, final boolean bridge) {
            this.varArgs = varArgs;
            this.exactCount = exactCount;
            this.assignableCount = assignableCount;
            this.coercibleCount = coercibleCount;
            this.varArgsCount = varArgsCount;
            this.bridge = bridge;
        }
        
        public boolean isVarArgs() {
            return this.varArgs;
        }
        
        public int getExactCount() {
            return this.exactCount;
        }
        
        public int getAssignableCount() {
            return this.assignableCount;
        }
        
        public int getCoercibleCount() {
            return this.coercibleCount;
        }
        
        public int getVarArgsCount() {
            return this.varArgsCount;
        }
        
        public boolean isBridge() {
            return this.bridge;
        }
        
        @Override
        public int compareTo(final MatchResult o) {
            int cmp = Boolean.compare(o.isVarArgs(), this.isVarArgs());
            if (cmp == 0) {
                cmp = Integer.compare(this.getExactCount(), o.getExactCount());
                if (cmp == 0) {
                    cmp = Integer.compare(this.getAssignableCount(), o.getAssignableCount());
                    if (cmp == 0) {
                        cmp = Integer.compare(this.getCoercibleCount(), o.getCoercibleCount());
                        if (cmp == 0) {
                            cmp = Integer.compare(o.getVarArgsCount(), this.getVarArgsCount());
                            if (cmp == 0) {
                                cmp = Boolean.compare(o.isBridge(), this.isBridge());
                            }
                        }
                    }
                }
            }
            return cmp;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (null != o && this.getClass().equals(o.getClass()) && ((MatchResult)o).getExactCount() == this.getExactCount() && ((MatchResult)o).getAssignableCount() == this.getAssignableCount() && ((MatchResult)o).getCoercibleCount() == this.getCoercibleCount() && ((MatchResult)o).getVarArgsCount() == this.getVarArgsCount() && ((MatchResult)o).isVarArgs() == this.isVarArgs() && ((MatchResult)o).isBridge() == this.isBridge());
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + this.assignableCount;
            result = 31 * result + (this.bridge ? 1231 : 1237);
            result = 31 * result + this.coercibleCount;
            result = 31 * result + this.exactCount;
            result = 31 * result + (this.varArgs ? 1231 : 1237);
            result = 31 * result + this.varArgsCount;
            return result;
        }
    }
    
    private static class PrivilegedGetTccl implements PrivilegedAction<ClassLoader>
    {
        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
}
