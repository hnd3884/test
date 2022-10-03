package jdk.nashorn.internal.runtime.linker;

import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.Permissions;
import java.net.URL;
import java.security.CodeSource;
import java.security.CodeSigner;
import java.security.SecureClassLoader;
import java.security.PrivilegedAction;
import jdk.internal.org.objectweb.asm.commons.InstructionAdapter;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.nashorn.internal.runtime.Undefined;
import jdk.nashorn.internal.runtime.ECMAErrors;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ScriptRuntime;
import java.lang.invoke.MethodType;
import jdk.nashorn.internal.runtime.ScriptFunction;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.ScriptObject;

public final class JavaAdapterServices
{
    private static final ThreadLocal<ScriptObject> classOverrides;
    private static final MethodHandle NO_PERMISSIONS_INVOKER;
    
    private JavaAdapterServices() {
    }
    
    public static MethodHandle getHandle(final ScriptFunction fn, final MethodType type) {
        return bindAndAdaptHandle(fn, fn.isStrict() ? ScriptRuntime.UNDEFINED : Context.getGlobal(), type);
    }
    
    public static MethodHandle getHandle(final Object obj, final String name, final MethodType type) {
        if (!(obj instanceof ScriptObject)) {
            throw ECMAErrors.typeError("not.an.object", ScriptRuntime.safeToString(obj));
        }
        final ScriptObject sobj = (ScriptObject)obj;
        if ("toString".equals(name) && !sobj.hasOwnProperty("toString")) {
            return null;
        }
        final Object fnObj = sobj.get(name);
        if (fnObj instanceof ScriptFunction) {
            return bindAndAdaptHandle((ScriptFunction)fnObj, sobj, type);
        }
        if (fnObj == null || fnObj instanceof Undefined) {
            return null;
        }
        throw ECMAErrors.typeError("not.a.function", name);
    }
    
    public static Object getClassOverrides() {
        final Object overrides = JavaAdapterServices.classOverrides.get();
        assert overrides != null;
        return overrides;
    }
    
    public static void invokeNoPermissions(final MethodHandle method, final Object arg) throws Throwable {
        JavaAdapterServices.NO_PERMISSIONS_INVOKER.invokeExact(method, arg);
    }
    
    public static void setGlobal(final Object global) {
        Context.setGlobal((ScriptObject)global);
    }
    
    public static Object getGlobal() {
        return Context.getGlobal();
    }
    
    static void setClassOverrides(final ScriptObject overrides) {
        JavaAdapterServices.classOverrides.set(overrides);
    }
    
    private static MethodHandle bindAndAdaptHandle(final ScriptFunction fn, final Object self, final MethodType type) {
        return Bootstrap.getLinkerServices().asType(ScriptObject.pairArguments(fn.getBoundInvokeHandle(self), type, false), type);
    }
    
    private static MethodHandle createNoPermissionsInvoker() {
        final String className = "NoPermissionsInvoker";
        final ClassWriter cw = new ClassWriter(3);
        cw.visit(51, 49, "NoPermissionsInvoker", null, "java/lang/Object", null);
        final Type objectType = Type.getType(Object.class);
        final Type methodHandleType = Type.getType(MethodHandle.class);
        final InstructionAdapter mv = new InstructionAdapter(cw.visitMethod(9, "invoke", Type.getMethodDescriptor(Type.VOID_TYPE, methodHandleType, objectType), null, null));
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.invokevirtual(methodHandleType.getInternalName(), "invokeExact", Type.getMethodDescriptor(Type.VOID_TYPE, objectType), false);
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
        final byte[] bytes = cw.toByteArray();
        final ClassLoader loader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new SecureClassLoader(null) {
                    @Override
                    protected Class<?> findClass(final String name) throws ClassNotFoundException {
                        if (name.equals("NoPermissionsInvoker")) {
                            return this.defineClass(name, bytes, 0, bytes.length, new ProtectionDomain(new CodeSource(null, (CodeSigner[])null), new Permissions()));
                        }
                        throw new ClassNotFoundException(name);
                    }
                };
            }
        });
        try {
            return MethodHandles.lookup().findStatic(Class.forName("NoPermissionsInvoker", true, loader), "invoke", MethodType.methodType(Void.TYPE, MethodHandle.class, Object.class));
        }
        catch (final ReflectiveOperationException e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
    
    public static MethodHandle getObjectConverter(final Class<?> returnType) {
        return Bootstrap.getLinkerServices().getTypeConverter(Object.class, returnType);
    }
    
    public static Object exportReturnValue(final Object obj) {
        return NashornBeansLinker.exportArgument(obj, true);
    }
    
    public static char toCharPrimitive(final Object obj) {
        return JavaArgumentConverters.toCharPrimitive(obj);
    }
    
    public static String toString(final Object obj) {
        return JavaArgumentConverters.toString(obj);
    }
    
    static {
        classOverrides = new ThreadLocal<ScriptObject>();
        NO_PERMISSIONS_INVOKER = createNoPermissionsInvoker();
    }
}