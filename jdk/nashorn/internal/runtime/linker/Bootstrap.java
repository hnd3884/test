package jdk.nashorn.internal.runtime.linker;

import jdk.nashorn.internal.runtime.options.Options;
import jdk.internal.dynalink.linker.MethodTypeConversionStrategy;
import jdk.nashorn.internal.runtime.OptimisticReturnFilters;
import jdk.internal.dynalink.linker.LinkRequest;
import jdk.internal.dynalink.GuardedInvocationFilter;
import jdk.internal.dynalink.linker.GuardingDynamicLinker;
import jdk.internal.dynalink.DynamicLinkerFactory;
import jdk.nashorn.internal.lookup.MethodHandleFactory;
import jdk.internal.dynalink.support.TypeUtilities;
import jdk.internal.dynalink.CallSiteDescriptor;
import jdk.internal.dynalink.linker.GuardedInvocation;
import jdk.internal.dynalink.linker.LinkerServices;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.ECMAErrors;
import jdk.nashorn.internal.runtime.ECMAException;
import jdk.nashorn.api.scripting.JSObject;
import jdk.internal.dynalink.beans.StaticClass;
import jdk.internal.dynalink.beans.BeansLinker;
import jdk.nashorn.internal.runtime.ScriptFunction;
import jdk.nashorn.internal.runtime.ScriptRuntime;
import jdk.internal.dynalink.DynamicLinker;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.lookup.MethodHandleFunctionality;
import jdk.nashorn.internal.codegen.CompilerConstants;

public final class Bootstrap
{
    public static final CompilerConstants.Call BOOTSTRAP;
    private static final MethodHandleFunctionality MH;
    private static final MethodHandle VOID_TO_OBJECT;
    private static final int NASHORN_DEFAULT_UNSTABLE_RELINK_THRESHOLD = 16;
    private static final DynamicLinker dynamicLinker;
    
    private Bootstrap() {
    }
    
    public static boolean isCallable(final Object obj) {
        return obj != ScriptRuntime.UNDEFINED && obj != null && (obj instanceof ScriptFunction || isJSObjectFunction(obj) || BeansLinker.isDynamicMethod(obj) || obj instanceof BoundCallable || isFunctionalInterfaceObject(obj) || obj instanceof StaticClass);
    }
    
    public static boolean isStrictCallable(final Object callable) {
        if (callable instanceof ScriptFunction) {
            return ((ScriptFunction)callable).isStrict();
        }
        if (isJSObjectFunction(callable)) {
            return ((JSObject)callable).isStrictFunction();
        }
        if (callable instanceof BoundCallable) {
            return isStrictCallable(((BoundCallable)callable).getCallable());
        }
        if (BeansLinker.isDynamicMethod(callable) || callable instanceof StaticClass || isFunctionalInterfaceObject(callable)) {
            return false;
        }
        throw notFunction(callable);
    }
    
    private static ECMAException notFunction(final Object obj) {
        return ECMAErrors.typeError("not.a.function", ScriptRuntime.safeToString(obj));
    }
    
    private static boolean isJSObjectFunction(final Object obj) {
        return obj instanceof JSObject && ((JSObject)obj).isFunction();
    }
    
    public static boolean isDynamicMethod(final Object obj) {
        return BeansLinker.isDynamicMethod((obj instanceof BoundCallable) ? ((BoundCallable)obj).getCallable() : obj);
    }
    
    public static boolean isFunctionalInterfaceObject(final Object obj) {
        return !JSType.isPrimitive(obj) && NashornBeansLinker.getFunctionalInterfaceMethodName(obj.getClass()) != null;
    }
    
    public static CallSite bootstrap(final MethodHandles.Lookup lookup, final String opDesc, final MethodType type, final int flags) {
        return Bootstrap.dynamicLinker.link(LinkerCallSite.newLinkerCallSite(lookup, opDesc, type, flags));
    }
    
    public static CallSite mathBootstrap(final MethodHandles.Lookup lookup, final String name, final MethodType type, final int programPoint) {
        MethodHandle mh = null;
        switch (name) {
            case "iadd": {
                mh = JSType.ADD_EXACT.methodHandle();
                break;
            }
            case "isub": {
                mh = JSType.SUB_EXACT.methodHandle();
                break;
            }
            case "imul": {
                mh = JSType.MUL_EXACT.methodHandle();
                break;
            }
            case "idiv": {
                mh = JSType.DIV_EXACT.methodHandle();
                break;
            }
            case "irem": {
                mh = JSType.REM_EXACT.methodHandle();
                break;
            }
            case "ineg": {
                mh = JSType.NEGATE_EXACT.methodHandle();
                break;
            }
            default: {
                throw new AssertionError((Object)"unsupported math intrinsic");
            }
        }
        return new ConstantCallSite(Bootstrap.MH.insertArguments(mh, mh.type().parameterCount() - 1, programPoint));
    }
    
    public static MethodHandle createDynamicInvoker(final String opDesc, final Class<?> rtype, final Class<?>... ptypes) {
        return createDynamicInvoker(opDesc, MethodType.methodType(rtype, ptypes));
    }
    
    public static MethodHandle createDynamicInvoker(final String opDesc, final int flags, final Class<?> rtype, final Class<?>... ptypes) {
        return bootstrap(MethodHandles.publicLookup(), opDesc, MethodType.methodType(rtype, ptypes), flags).dynamicInvoker();
    }
    
    public static MethodHandle createDynamicInvoker(final String opDesc, final MethodType type) {
        return bootstrap(MethodHandles.publicLookup(), opDesc, type, 0).dynamicInvoker();
    }
    
    public static Object bindCallable(final Object callable, final Object boundThis, final Object[] boundArgs) {
        if (callable instanceof ScriptFunction) {
            return ((ScriptFunction)callable).createBound(boundThis, boundArgs);
        }
        if (callable instanceof BoundCallable) {
            return ((BoundCallable)callable).bind(boundArgs);
        }
        if (isCallable(callable)) {
            return new BoundCallable(callable, boundThis, boundArgs);
        }
        throw notFunction(callable);
    }
    
    public static Object createSuperAdapter(final Object adapter) {
        return new JavaSuperAdapter(adapter);
    }
    
    public static void checkReflectionAccess(final Class<?> clazz, final boolean isStatic) {
        ReflectionCheckLinker.checkReflectionAccess(clazz, isStatic);
    }
    
    public static LinkerServices getLinkerServices() {
        return Bootstrap.dynamicLinker.getLinkerServices();
    }
    
    static GuardedInvocation asTypeSafeReturn(final GuardedInvocation inv, final LinkerServices linkerServices, final CallSiteDescriptor desc) {
        return (inv == null) ? null : inv.asTypeSafeReturn(linkerServices, desc.getMethodType());
    }
    
    private static MethodHandle unboxReturnType(final MethodHandle target, final MethodType newType) {
        final MethodType targetType = target.type();
        final Class<?> oldReturnType = targetType.returnType();
        final Class<?> newReturnType = newType.returnType();
        if (TypeUtilities.isWrapperType(oldReturnType)) {
            if (newReturnType.isPrimitive()) {
                assert TypeUtilities.isMethodInvocationConvertible(oldReturnType, newReturnType);
                return MethodHandles.explicitCastArguments(target, targetType.changeReturnType(newReturnType));
            }
        }
        else if (oldReturnType == Void.TYPE && newReturnType == Object.class) {
            return MethodHandles.filterReturnValue(target, Bootstrap.VOID_TO_OBJECT);
        }
        return target;
    }
    
    static {
        BOOTSTRAP = CompilerConstants.staticCallNoLookup(Bootstrap.class, "bootstrap", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, Integer.TYPE);
        MH = MethodHandleFactory.getFunctionality();
        VOID_TO_OBJECT = Bootstrap.MH.constant(Object.class, ScriptRuntime.UNDEFINED);
        final DynamicLinkerFactory factory = new DynamicLinkerFactory();
        final NashornBeansLinker nashornBeansLinker = new NashornBeansLinker();
        factory.setPrioritizedLinkers(new NashornLinker(), new NashornPrimitiveLinker(), new NashornStaticClassLinker(), new BoundCallableLinker(), new JavaSuperAdapterLinker(), new JSObjectLinker(nashornBeansLinker), new BrowserJSObjectLinker(nashornBeansLinker), new ReflectionCheckLinker());
        factory.setFallbackLinkers(nashornBeansLinker, new NashornBottomLinker());
        factory.setSyncOnRelink(true);
        factory.setPrelinkFilter(new GuardedInvocationFilter() {
            @Override
            public GuardedInvocation filter(final GuardedInvocation inv, final LinkRequest request, final LinkerServices linkerServices) {
                final CallSiteDescriptor desc = request.getCallSiteDescriptor();
                return OptimisticReturnFilters.filterOptimisticReturnValue(inv, desc).asType(linkerServices, desc.getMethodType());
            }
        });
        factory.setAutoConversionStrategy(new MethodTypeConversionStrategy() {
            @Override
            public MethodHandle asType(final MethodHandle target, final MethodType newType) {
                return unboxReturnType(target, newType);
            }
        });
        factory.setInternalObjectsFilter(NashornBeansLinker.createHiddenObjectFilter());
        final int relinkThreshold = Options.getIntProperty("nashorn.unstable.relink.threshold", 16);
        if (relinkThreshold > -1) {
            factory.setUnstableRelinkThreshold(relinkThreshold);
        }
        factory.setClassLoader(Bootstrap.class.getClassLoader());
        dynamicLinker = factory.createLinker();
    }
}
