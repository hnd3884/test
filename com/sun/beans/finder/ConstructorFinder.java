package com.sun.beans.finder;

import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Modifier;
import com.sun.beans.util.Cache;
import java.lang.reflect.Constructor;

public final class ConstructorFinder extends AbstractFinder<Constructor<?>>
{
    private static final Cache<Signature, Constructor<?>> CACHE;
    
    public static Constructor<?> findConstructor(final Class<?> clazz, final Class<?>... array) throws NoSuchMethodException {
        if (clazz.isPrimitive()) {
            throw new NoSuchMethodException("Primitive wrapper does not contain constructors");
        }
        if (clazz.isInterface()) {
            throw new NoSuchMethodException("Interface does not contain constructors");
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new NoSuchMethodException("Abstract class cannot be instantiated");
        }
        if (!Modifier.isPublic(clazz.getModifiers()) || !ReflectUtil.isPackageAccessible(clazz)) {
            throw new NoSuchMethodException("Class is not accessible");
        }
        PrimitiveWrapperMap.replacePrimitivesWithWrappers(array);
        final Signature signature = new Signature(clazz, array);
        try {
            return ConstructorFinder.CACHE.get(signature);
        }
        catch (final SignatureException ex) {
            throw ex.toNoSuchMethodException("Constructor is not found");
        }
    }
    
    private ConstructorFinder(final Class<?>[] array) {
        super(array);
    }
    
    static {
        CACHE = new Cache<Signature, Constructor<?>>(Cache.Kind.SOFT, Cache.Kind.SOFT) {
            @Override
            public Constructor create(final Signature signature) {
                try {
                    return new ConstructorFinder(signature.getArgs(), null).find(signature.getType().getConstructors());
                }
                catch (final Exception ex) {
                    throw new SignatureException(ex);
                }
            }
        };
    }
}
