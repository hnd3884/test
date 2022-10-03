package org.msgpack.template.builder.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.util.Iterator;
import org.apache.harmony.beans.internal.nls.Messages;
import java.lang.reflect.Array;
import org.apache.harmony.beans.BeansUtils;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class Statement
{
    private Object target;
    private String methodName;
    private Object[] arguments;
    private static WeakHashMap<Class<?>, Method[]> classMethodsCache;
    private static final String[][] pdConstructorSignatures;
    
    public Statement(final Object target, final String methodName, final Object[] arguments) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = ((arguments == null) ? BeansUtils.EMPTY_OBJECT_ARRAY : arguments);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.target == null) {
            sb.append("null");
        }
        else {
            final Class<?> clazz = this.target.getClass();
            sb.append((clazz == String.class) ? "\"\"" : BeansUtils.idOfClass(clazz));
        }
        sb.append('.' + this.methodName + '(');
        if (this.arguments != null) {
            for (int index = 0; index < this.arguments.length; ++index) {
                if (index > 0) {
                    sb.append(", ");
                }
                if (this.arguments[index] == null) {
                    sb.append("null");
                }
                else {
                    final Class<?> clazz = this.arguments[index].getClass();
                    sb.append((clazz == String.class) ? ('\"' + (String)this.arguments[index] + '\"') : BeansUtils.idOfClass(clazz));
                }
            }
        }
        sb.append(')');
        sb.append(';');
        return sb.toString();
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public Object[] getArguments() {
        return this.arguments;
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public void execute() throws Exception {
        this.invokeMethod();
    }
    
    Object invokeMethod() throws Exception {
        Object result = null;
        try {
            final Object target = this.getTarget();
            final String methodName = this.getMethodName();
            final Object[] arguments = this.getArguments();
            final Class<?> targetClass = target.getClass();
            if (targetClass.isArray()) {
                final Method method = this.findArrayMethod(methodName, arguments);
                final Object[] copy = new Object[arguments.length + 1];
                copy[0] = target;
                System.arraycopy(arguments, 0, copy, 1, arguments.length);
                result = method.invoke(null, copy);
            }
            else if ("newInstance".equals(methodName) && target == Array.class) {
                result = Array.newInstance((Class<?>)arguments[0], (int)arguments[1]);
            }
            else if ("new".equals(methodName) || "newInstance".equals(methodName)) {
                if (target instanceof Class) {
                    final Constructor<?> constructor = this.findConstructor((Class<?>)target, arguments);
                    result = constructor.newInstance(arguments);
                }
                else {
                    if ("new".equals(methodName)) {
                        throw new NoSuchMethodException(this.toString());
                    }
                    final Method method = findMethod(targetClass, methodName, arguments, false);
                    result = method.invoke(target, arguments);
                }
            }
            else {
                if (methodName.equals("newArray")) {
                    final Class<?> clazz = (Class<?>)target;
                    for (int index = 0; index < arguments.length; ++index) {
                        final Class<?> argClass = (arguments[index] == null) ? null : arguments[index].getClass();
                        if (argClass != null && !clazz.isAssignableFrom(argClass) && !BeansUtils.isPrimitiveWrapper(argClass, clazz)) {
                            throw new IllegalArgumentException(Messages.getString("custom.beans.63"));
                        }
                    }
                    result = Array.newInstance(clazz, arguments.length);
                    if (clazz.isPrimitive()) {
                        this.arrayCopy(clazz, arguments, result, arguments.length);
                    }
                    else {
                        System.arraycopy(arguments, 0, result, 0, arguments.length);
                    }
                    return result;
                }
                if (target instanceof Class) {
                    Method method = null;
                    try {
                        if (target != Class.class) {
                            method = findMethod((Class<?>)target, methodName, arguments, true);
                            result = method.invoke(null, arguments);
                        }
                    }
                    catch (final NoSuchMethodException ex) {}
                    if (method == null) {
                        if ("forName".equals(methodName) && arguments.length == 1 && arguments[0] instanceof String) {
                            try {
                                result = Class.forName((String)arguments[0]);
                            }
                            catch (final ClassNotFoundException e2) {
                                result = Class.forName((String)arguments[0], true, Thread.currentThread().getContextClassLoader());
                            }
                        }
                        else {
                            method = findMethod(targetClass, methodName, arguments, false);
                            result = method.invoke(target, arguments);
                        }
                    }
                }
                else if (target instanceof Iterator) {
                    final Iterator<?> iterator = (Iterator<?>)target;
                    final Method method2 = findMethod(targetClass, methodName, arguments, false);
                    if (iterator.hasNext()) {
                        result = new PrivilegedAction<Object>() {
                            @Override
                            public Object run() {
                                try {
                                    method2.setAccessible(true);
                                    return method2.invoke(iterator, new Object[0]);
                                }
                                catch (final Exception e) {
                                    return null;
                                }
                            }
                        }.run();
                    }
                }
                else {
                    final Method method = findMethod(targetClass, methodName, arguments, false);
                    method.setAccessible(true);
                    result = method.invoke(target, arguments);
                }
            }
        }
        catch (final InvocationTargetException ite) {
            final Throwable t = ite.getCause();
            throw (t != null && t instanceof Exception) ? ((Exception)t) : ite;
        }
        return result;
    }
    
    private void arrayCopy(final Class<?> type, final Object[] src, final Object dest, final int length) {
        if (type == Boolean.TYPE) {
            final boolean[] destination = (boolean[])dest;
            for (int index = 0; index < length; ++index) {
                destination[index] = (boolean)src[index];
            }
        }
        else if (type == Short.TYPE) {
            final short[] destination2 = (short[])dest;
            for (int index = 0; index < length; ++index) {
                destination2[index] = (short)src[index];
            }
        }
        else if (type == Byte.TYPE) {
            final byte[] destination3 = (byte[])dest;
            for (int index = 0; index < length; ++index) {
                destination3[index] = (byte)src[index];
            }
        }
        else if (type == Character.TYPE) {
            final char[] destination4 = (char[])dest;
            for (int index = 0; index < length; ++index) {
                destination4[index] = (char)src[index];
            }
        }
        else if (type == Integer.TYPE) {
            final int[] destination5 = (int[])dest;
            for (int index = 0; index < length; ++index) {
                destination5[index] = (int)src[index];
            }
        }
        else if (type == Long.TYPE) {
            final long[] destination6 = (long[])dest;
            for (int index = 0; index < length; ++index) {
                destination6[index] = (long)src[index];
            }
        }
        else if (type == Float.TYPE) {
            final float[] destination7 = (float[])dest;
            for (int index = 0; index < length; ++index) {
                destination7[index] = (float)src[index];
            }
        }
        else if (type == Double.TYPE) {
            final double[] destination8 = (double[])dest;
            for (int index = 0; index < length; ++index) {
                destination8[index] = (double)src[index];
            }
        }
    }
    
    private Method findArrayMethod(final String methodName, final Object[] args) throws NoSuchMethodException {
        final boolean isGet = "get".equals(methodName);
        final boolean isSet = "set".equals(methodName);
        if (!isGet && !isSet) {
            throw new NoSuchMethodException(Messages.getString("custom.beans.3C"));
        }
        if (args.length > 0 && args[0].getClass() != Integer.class) {
            throw new ClassCastException(Messages.getString("custom.beans.3D"));
        }
        if (isGet && args.length != 1) {
            throw new ArrayIndexOutOfBoundsException(Messages.getString("custom.beans.3E"));
        }
        if (isSet && args.length != 2) {
            throw new ArrayIndexOutOfBoundsException(Messages.getString("custom.beans.3F"));
        }
        final Class<?>[] paraTypes = isGet ? new Class[] { Object.class, Integer.TYPE } : new Class[] { Object.class, Integer.TYPE, Object.class };
        return Array.class.getMethod(methodName, paraTypes);
    }
    
    private Constructor<?> findConstructor(final Class<?> clazz, final Object[] args) throws NoSuchMethodException {
        final Class<?>[] argTypes = getTypes(args);
        Constructor<?> result = null;
        for (final Constructor<?> constructor : clazz.getConstructors()) {
            final Class<?>[] paraTypes = constructor.getParameterTypes();
            if (match(argTypes, paraTypes)) {
                if (result == null) {
                    result = constructor;
                }
                else {
                    final Class<?>[] resultParaTypes = result.getParameterTypes();
                    boolean isAssignable = true;
                    for (int index = 0; index < paraTypes.length; ++index) {
                        if (argTypes[index] != null && !(isAssignable &= resultParaTypes[index].isAssignableFrom(paraTypes[index]))) {
                            break;
                        }
                        if (argTypes[index] == null && !(isAssignable &= paraTypes[index].isAssignableFrom(resultParaTypes[index]))) {
                            break;
                        }
                    }
                    if (isAssignable) {
                        result = constructor;
                    }
                }
            }
        }
        if (result == null) {
            throw new NoSuchMethodException(Messages.getString("custom.beans.40", clazz.getName()));
        }
        return result;
    }
    
    static Method findMethod(final Class<?> clazz, final String methodName, final Object[] args, final boolean isStatic) throws NoSuchMethodException {
        final Class<?>[] argTypes = getTypes(args);
        Method[] methods = null;
        if (Statement.classMethodsCache.containsKey(clazz)) {
            methods = Statement.classMethodsCache.get(clazz);
        }
        else {
            methods = clazz.getMethods();
            Statement.classMethodsCache.put(clazz, methods);
        }
        final ArrayList<Method> fitMethods = new ArrayList<Method>();
        for (final Method method : methods) {
            if (methodName.equals(method.getName()) && (!isStatic || Modifier.isStatic(method.getModifiers())) && match(argTypes, method.getParameterTypes())) {
                fitMethods.add(method);
            }
        }
        final int fitSize = fitMethods.size();
        if (fitSize == 0) {
            throw new NoSuchMethodException(Messages.getString("custom.beans.41", methodName));
        }
        if (fitSize == 1) {
            return fitMethods.get(0);
        }
        final MethodComparator comparator = new MethodComparator(methodName, argTypes);
        final Method[] fitMethodArray = fitMethods.toArray(new Method[fitSize]);
        Method onlyMethod = fitMethodArray[0];
        for (int i = 1; i < fitMethodArray.length; ++i) {
            final int difference;
            if ((difference = comparator.compare(onlyMethod, fitMethodArray[i])) == 0) {
                final Class<?> onlyReturnType = onlyMethod.getReturnType();
                final Class<?> fitReturnType = fitMethodArray[i].getReturnType();
                if (onlyReturnType == fitReturnType) {
                    throw new NoSuchMethodException(Messages.getString("custom.beans.62", methodName));
                }
                if (onlyReturnType.isAssignableFrom(fitReturnType)) {
                    onlyMethod = fitMethodArray[i];
                }
            }
            if (difference > 0) {
                onlyMethod = fitMethodArray[i];
            }
        }
        return onlyMethod;
    }
    
    private static boolean match(final Class<?>[] argTypes, final Class<?>[] paraTypes) {
        if (paraTypes.length != argTypes.length) {
            return false;
        }
        for (int index = 0; index < paraTypes.length; ++index) {
            if (argTypes[index] != null && !paraTypes[index].isAssignableFrom(argTypes[index]) && !BeansUtils.isPrimitiveWrapper(argTypes[index], paraTypes[index])) {
                return false;
            }
        }
        return true;
    }
    
    static boolean isStaticMethodCall(final Statement stmt) {
        final Object target = stmt.getTarget();
        final String methodName = stmt.getMethodName();
        if (!(target instanceof Class)) {
            return false;
        }
        try {
            findMethod((Class<?>)target, methodName, stmt.getArguments(), true);
            return true;
        }
        catch (final NoSuchMethodException e) {
            return false;
        }
    }
    
    static boolean isPDConstructor(final Statement stmt) {
        final Object target = stmt.getTarget();
        final String methodName = stmt.getMethodName();
        final Object[] args = stmt.getArguments();
        final String[] sig = new String[Statement.pdConstructorSignatures[0].length];
        if (target == null || methodName == null || args == null || args.length == 0) {
            return false;
        }
        sig[0] = target.getClass().getName();
        sig[1] = methodName;
        for (int i = 2; i < sig.length; ++i) {
            if (args.length > i - 2) {
                sig[i] = ((args[i - 2] != null) ? args[i - 2].getClass().getName() : "null");
            }
            else {
                sig[i] = "";
            }
        }
        for (final String[] element : Statement.pdConstructorSignatures) {
            if (Arrays.equals(sig, element)) {
                return true;
            }
        }
        return false;
    }
    
    private static Class<?> getPrimitiveWrapper(final Class<?> base) {
        Class<?> res = null;
        if (base == Boolean.TYPE) {
            res = Boolean.class;
        }
        else if (base == Byte.TYPE) {
            res = Byte.class;
        }
        else if (base == Character.TYPE) {
            res = Character.class;
        }
        else if (base == Short.TYPE) {
            res = Short.class;
        }
        else if (base == Integer.TYPE) {
            res = Integer.class;
        }
        else if (base == Long.TYPE) {
            res = Long.class;
        }
        else if (base == Float.TYPE) {
            res = Float.class;
        }
        else if (base == Double.TYPE) {
            res = Double.class;
        }
        return res;
    }
    
    private static Class<?>[] getTypes(final Object[] arguments) {
        final Class<?>[] types = new Class[arguments.length];
        for (int index = 0; index < arguments.length; ++index) {
            types[index] = ((arguments[index] == null) ? null : arguments[index].getClass());
        }
        return types;
    }
    
    static {
        Statement.classMethodsCache = new WeakHashMap<Class<?>, Method[]>();
        pdConstructorSignatures = new String[][] { { "java.lang.Class", "new", "java.lang.Boolean", "", "", "" }, { "java.lang.Class", "new", "java.lang.Byte", "", "", "" }, { "java.lang.Class", "new", "java.lang.Character", "", "", "" }, { "java.lang.Class", "new", "java.lang.Double", "", "", "" }, { "java.lang.Class", "new", "java.lang.Float", "", "", "" }, { "java.lang.Class", "new", "java.lang.Integer", "", "", "" }, { "java.lang.Class", "new", "java.lang.Long", "", "", "" }, { "java.lang.Class", "new", "java.lang.Short", "", "", "" }, { "java.lang.Class", "new", "java.lang.String", "", "", "" }, { "java.lang.Class", "forName", "java.lang.String", "", "", "" }, { "java.lang.Class", "newInstance", "java.lang.Class", "java.lang.Integer", "", "" }, { "java.lang.reflect.Field", "get", "null", "", "", "" }, { "java.lang.Class", "forName", "java.lang.String", "", "", "" } };
    }
    
    static class MethodComparator implements Comparator<Method>
    {
        static int INFINITY;
        private String referenceMethodName;
        private Class<?>[] referenceMethodArgumentTypes;
        private final Map<Method, Integer> cache;
        
        public MethodComparator(final String refMethodName, final Class<?>[] refArgumentTypes) {
            this.referenceMethodName = refMethodName;
            this.referenceMethodArgumentTypes = refArgumentTypes;
            this.cache = new HashMap<Method, Integer>();
        }
        
        @Override
        public int compare(final Method m1, final Method m2) {
            Integer norm1 = this.cache.get(m1);
            Integer norm2 = this.cache.get(m2);
            if (norm1 == null) {
                norm1 = this.getNorm(m1);
                this.cache.put(m1, norm1);
            }
            if (norm2 == null) {
                norm2 = this.getNorm(m2);
                this.cache.put(m2, norm2);
            }
            return norm1 - norm2;
        }
        
        private int getNorm(final Method m) {
            final String methodName = m.getName();
            final Class<?>[] argumentTypes = m.getParameterTypes();
            int totalNorm = 0;
            if (!this.referenceMethodName.equals(methodName) || this.referenceMethodArgumentTypes.length != argumentTypes.length) {
                return MethodComparator.INFINITY;
            }
            for (int i = 0; i < this.referenceMethodArgumentTypes.length; ++i) {
                if (this.referenceMethodArgumentTypes[i] != null) {
                    if (this.referenceMethodArgumentTypes[i].isPrimitive()) {
                        this.referenceMethodArgumentTypes[i] = getPrimitiveWrapper(this.referenceMethodArgumentTypes[i]);
                    }
                    if (argumentTypes[i].isPrimitive()) {
                        argumentTypes[i] = getPrimitiveWrapper(argumentTypes[i]);
                    }
                    totalNorm += getDistance(this.referenceMethodArgumentTypes[i], argumentTypes[i]);
                }
            }
            return totalNorm;
        }
        
        private static int getDistance(final Class<?> clz1, final Class<?> clz2) {
            int superDist = MethodComparator.INFINITY;
            if (!clz2.isAssignableFrom(clz1)) {
                return MethodComparator.INFINITY;
            }
            if (clz1.getName().equals(clz2.getName())) {
                return 0;
            }
            final Class<?> superClz = clz1.getSuperclass();
            if (superClz != null) {
                superDist = getDistance(superClz, clz2);
            }
            if (clz2.isInterface()) {
                final Class<?>[] interfaces = clz1.getInterfaces();
                int bestDist = MethodComparator.INFINITY;
                for (final Class<?> element : interfaces) {
                    final int curDist = getDistance(element, clz2);
                    if (curDist < bestDist) {
                        bestDist = curDist;
                    }
                }
                if (superDist < bestDist) {
                    bestDist = superDist;
                }
                return (bestDist != MethodComparator.INFINITY) ? (bestDist + 1) : MethodComparator.INFINITY;
            }
            return (superDist != MethodComparator.INFINITY) ? (superDist + 2) : MethodComparator.INFINITY;
        }
        
        static {
            MethodComparator.INFINITY = Integer.MAX_VALUE;
        }
    }
}
