package sun.reflect;

import java.util.Objects;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.OptionalDataException;
import java.lang.reflect.InvocationTargetException;
import java.io.ObjectStreamClass;
import java.lang.invoke.MethodHandles;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.lang.invoke.MethodHandle;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Permission;

public class ReflectionFactory
{
    private static boolean initted;
    private static final Permission reflectionFactoryAccessPerm;
    private static final ReflectionFactory soleInstance;
    private static volatile LangReflectAccess langReflectAccess;
    private static volatile Method hasStaticInitializerMethod;
    private static boolean noInflation;
    private static int inflationThreshold;
    
    private ReflectionFactory() {
    }
    
    public static ReflectionFactory getReflectionFactory() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(ReflectionFactory.reflectionFactoryAccessPerm);
        }
        return ReflectionFactory.soleInstance;
    }
    
    public void setLangReflectAccess(final LangReflectAccess langReflectAccess) {
        ReflectionFactory.langReflectAccess = langReflectAccess;
    }
    
    public FieldAccessor newFieldAccessor(final Field field, final boolean b) {
        checkInitted();
        return UnsafeFieldAccessorFactory.newFieldAccessor(field, b);
    }
    
    public MethodAccessor newMethodAccessor(final Method method) {
        checkInitted();
        if (ReflectionFactory.noInflation && !ReflectUtil.isVMAnonymousClass(method.getDeclaringClass())) {
            return new MethodAccessorGenerator().generateMethod(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), method.getReturnType(), method.getExceptionTypes(), method.getModifiers());
        }
        final NativeMethodAccessorImpl nativeMethodAccessorImpl = new NativeMethodAccessorImpl(method);
        final DelegatingMethodAccessorImpl parent = new DelegatingMethodAccessorImpl(nativeMethodAccessorImpl);
        nativeMethodAccessorImpl.setParent(parent);
        return parent;
    }
    
    public ConstructorAccessor newConstructorAccessor(final Constructor<?> constructor) {
        checkInitted();
        final Class<?> declaringClass = constructor.getDeclaringClass();
        if (Modifier.isAbstract(declaringClass.getModifiers())) {
            return new InstantiationExceptionConstructorAccessorImpl(null);
        }
        if (declaringClass == Class.class) {
            return new InstantiationExceptionConstructorAccessorImpl("Can not instantiate java.lang.Class");
        }
        if (Reflection.isSubclassOf(declaringClass, ConstructorAccessorImpl.class)) {
            return new BootstrapConstructorAccessorImpl(constructor);
        }
        if (ReflectionFactory.noInflation && !ReflectUtil.isVMAnonymousClass(constructor.getDeclaringClass())) {
            return new MethodAccessorGenerator().generateConstructor(constructor.getDeclaringClass(), constructor.getParameterTypes(), constructor.getExceptionTypes(), constructor.getModifiers());
        }
        final NativeConstructorAccessorImpl nativeConstructorAccessorImpl = new NativeConstructorAccessorImpl(constructor);
        final DelegatingConstructorAccessorImpl parent = new DelegatingConstructorAccessorImpl(nativeConstructorAccessorImpl);
        nativeConstructorAccessorImpl.setParent(parent);
        return parent;
    }
    
    public Field newField(final Class<?> clazz, final String s, final Class<?> clazz2, final int n, final int n2, final String s2, final byte[] array) {
        return langReflectAccess().newField(clazz, s, clazz2, n, n2, s2, array);
    }
    
    public Method newMethod(final Class<?> clazz, final String s, final Class<?>[] array, final Class<?> clazz2, final Class<?>[] array2, final int n, final int n2, final String s2, final byte[] array3, final byte[] array4, final byte[] array5) {
        return langReflectAccess().newMethod(clazz, s, array, clazz2, array2, n, n2, s2, array3, array4, array5);
    }
    
    public Constructor<?> newConstructor(final Class<?> clazz, final Class<?>[] array, final Class<?>[] array2, final int n, final int n2, final String s, final byte[] array3, final byte[] array4) {
        return langReflectAccess().newConstructor(clazz, array, array2, n, n2, s, array3, array4);
    }
    
    public MethodAccessor getMethodAccessor(final Method method) {
        return langReflectAccess().getMethodAccessor(method);
    }
    
    public void setMethodAccessor(final Method method, final MethodAccessor methodAccessor) {
        langReflectAccess().setMethodAccessor(method, methodAccessor);
    }
    
    public ConstructorAccessor getConstructorAccessor(final Constructor<?> constructor) {
        return langReflectAccess().getConstructorAccessor(constructor);
    }
    
    public void setConstructorAccessor(final Constructor<?> constructor, final ConstructorAccessor constructorAccessor) {
        langReflectAccess().setConstructorAccessor(constructor, constructorAccessor);
    }
    
    public Method copyMethod(final Method method) {
        return langReflectAccess().copyMethod(method);
    }
    
    public Field copyField(final Field field) {
        return langReflectAccess().copyField(field);
    }
    
    public <T> Constructor<T> copyConstructor(final Constructor<T> constructor) {
        return langReflectAccess().copyConstructor(constructor);
    }
    
    public byte[] getExecutableTypeAnnotationBytes(final Executable executable) {
        return langReflectAccess().getExecutableTypeAnnotationBytes(executable);
    }
    
    public Constructor<?> newConstructorForSerialization(final Class<?> clazz, final Constructor<?> constructor) {
        if (constructor.getDeclaringClass() == clazz) {
            return constructor;
        }
        return this.generateConstructor(clazz, constructor);
    }
    
    public final Constructor<?> newConstructorForSerialization(final Class<?> clazz) {
        Class<?> superclass = clazz;
        while (Serializable.class.isAssignableFrom(superclass)) {
            if ((superclass = superclass.getSuperclass()) == null) {
                return null;
            }
        }
        Constructor<?> declaredConstructor;
        try {
            declaredConstructor = superclass.getDeclaredConstructor((Class<?>[])new Class[0]);
            final int modifiers = declaredConstructor.getModifiers();
            if ((modifiers & 0x2) != 0x0 || ((modifiers & 0x5) == 0x0 && !packageEquals(clazz, superclass))) {
                return null;
            }
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
        return this.generateConstructor(clazz, declaredConstructor);
    }
    
    private final Constructor<?> generateConstructor(final Class<?> clazz, final Constructor<?> constructor) {
        final SerializationConstructorAccessorImpl generateSerializationConstructor = new MethodAccessorGenerator().generateSerializationConstructor(clazz, constructor.getParameterTypes(), constructor.getExceptionTypes(), constructor.getModifiers(), constructor.getDeclaringClass());
        final Constructor<?> constructor2 = this.newConstructor(constructor.getDeclaringClass(), constructor.getParameterTypes(), constructor.getExceptionTypes(), constructor.getModifiers(), langReflectAccess().getConstructorSlot(constructor), langReflectAccess().getConstructorSignature(constructor), langReflectAccess().getConstructorAnnotations(constructor), langReflectAccess().getConstructorParameterAnnotations(constructor));
        this.setConstructorAccessor(constructor2, generateSerializationConstructor);
        constructor2.setAccessible(true);
        return constructor2;
    }
    
    public final Constructor<?> newConstructorForExternalization(final Class<?> clazz) {
        if (!Externalizable.class.isAssignableFrom(clazz)) {
            return null;
        }
        try {
            final Constructor<?> constructor = clazz.getConstructor((Class<?>[])new Class[0]);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
    }
    
    public final MethodHandle readObjectForSerialization(final Class<?> clazz) {
        return this.findReadWriteObjectForSerialization(clazz, "readObject", ObjectInputStream.class);
    }
    
    public final MethodHandle readObjectNoDataForSerialization(final Class<?> clazz) {
        return this.findReadWriteObjectForSerialization(clazz, "readObjectNoData", ObjectInputStream.class);
    }
    
    public final MethodHandle writeObjectForSerialization(final Class<?> clazz) {
        return this.findReadWriteObjectForSerialization(clazz, "writeObject", ObjectOutputStream.class);
    }
    
    private final MethodHandle findReadWriteObjectForSerialization(final Class<?> clazz, final String s, final Class<?> clazz2) {
        if (!Serializable.class.isAssignableFrom(clazz)) {
            return null;
        }
        try {
            final Method declaredMethod = clazz.getDeclaredMethod(s, clazz2);
            final int modifiers = declaredMethod.getModifiers();
            if (declaredMethod.getReturnType() != Void.TYPE || Modifier.isStatic(modifiers) || !Modifier.isPrivate(modifiers)) {
                return null;
            }
            declaredMethod.setAccessible(true);
            return MethodHandles.lookup().unreflect(declaredMethod);
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
        catch (final IllegalAccessException ex2) {
            throw new InternalError("Error", ex2);
        }
    }
    
    public final MethodHandle readResolveForSerialization(final Class<?> clazz) {
        return this.getReplaceResolveForSerialization(clazz, "readResolve");
    }
    
    public final MethodHandle writeReplaceForSerialization(final Class<?> clazz) {
        return this.getReplaceResolveForSerialization(clazz, "writeReplace");
    }
    
    private MethodHandle getReplaceResolveForSerialization(final Class<?> clazz, final String s) {
        if (!Serializable.class.isAssignableFrom(clazz)) {
            return null;
        }
        Class<?> superclass = clazz;
        while (superclass != null) {
            try {
                final Method declaredMethod = superclass.getDeclaredMethod(s, (Class[])new Class[0]);
                if (declaredMethod.getReturnType() != Object.class) {
                    return null;
                }
                final int modifiers = declaredMethod.getModifiers();
                if (Modifier.isStatic(modifiers) | Modifier.isAbstract(modifiers)) {
                    return null;
                }
                if (!(Modifier.isPublic(modifiers) | Modifier.isProtected(modifiers))) {
                    if (Modifier.isPrivate(modifiers) && clazz != superclass) {
                        return null;
                    }
                    if (!packageEquals(clazz, superclass)) {
                        return null;
                    }
                }
                try {
                    declaredMethod.setAccessible(true);
                    return MethodHandles.lookup().unreflect(declaredMethod);
                }
                catch (final IllegalAccessException ex) {
                    throw new InternalError("Error", ex);
                }
            }
            catch (final NoSuchMethodException ex2) {
                superclass = superclass.getSuperclass();
                continue;
            }
            break;
        }
        return null;
    }
    
    public final boolean hasStaticInitializerForSerialization(final Class<?> clazz) {
        Method hasStaticInitializerMethod = ReflectionFactory.hasStaticInitializerMethod;
        if (hasStaticInitializerMethod == null) {
            try {
                hasStaticInitializerMethod = ObjectStreamClass.class.getDeclaredMethod("hasStaticInitializer", Class.class);
                hasStaticInitializerMethod.setAccessible(true);
                ReflectionFactory.hasStaticInitializerMethod = hasStaticInitializerMethod;
            }
            catch (final NoSuchMethodException ex) {
                throw new InternalError("No such method hasStaticInitializer on " + ObjectStreamClass.class, ex);
            }
        }
        try {
            return (boolean)hasStaticInitializerMethod.invoke(null, clazz);
        }
        catch (final InvocationTargetException | IllegalAccessException ex2) {
            throw new InternalError("Exception invoking hasStaticInitializer", (Throwable)ex2);
        }
    }
    
    public final OptionalDataException newOptionalDataExceptionForSerialization(final boolean b) {
        try {
            final Constructor<OptionalDataException> declaredConstructor = OptionalDataException.class.getDeclaredConstructor(Boolean.TYPE);
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance(b);
        }
        catch (final NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new InternalError("unable to create OptionalDataException", (Throwable)ex);
        }
    }
    
    static int inflationThreshold() {
        return ReflectionFactory.inflationThreshold;
    }
    
    private static void checkInitted() {
        if (ReflectionFactory.initted) {
            return;
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                if (System.out == null) {
                    return null;
                }
                final String property = System.getProperty("sun.reflect.noInflation");
                if (property != null && property.equals("true")) {
                    ReflectionFactory.noInflation = true;
                }
                final String property2 = System.getProperty("sun.reflect.inflationThreshold");
                if (property2 != null) {
                    try {
                        ReflectionFactory.inflationThreshold = Integer.parseInt(property2);
                    }
                    catch (final NumberFormatException ex) {
                        throw new RuntimeException("Unable to parse property sun.reflect.inflationThreshold", ex);
                    }
                }
                ReflectionFactory.initted = true;
                return null;
            }
        });
    }
    
    private static LangReflectAccess langReflectAccess() {
        if (ReflectionFactory.langReflectAccess == null) {
            Modifier.isPublic(1);
        }
        return ReflectionFactory.langReflectAccess;
    }
    
    private static boolean packageEquals(final Class<?> clazz, final Class<?> clazz2) {
        return clazz.getClassLoader() == clazz2.getClassLoader() && Objects.equals(clazz.getPackage(), clazz2.getPackage());
    }
    
    static {
        ReflectionFactory.initted = false;
        reflectionFactoryAccessPerm = new RuntimePermission("reflectionFactoryAccess");
        soleInstance = new ReflectionFactory();
        ReflectionFactory.noInflation = false;
        ReflectionFactory.inflationThreshold = 15;
    }
    
    public static final class GetReflectionFactoryAction implements PrivilegedAction<ReflectionFactory>
    {
        @Override
        public ReflectionFactory run() {
            return ReflectionFactory.getReflectionFactory();
        }
    }
}
