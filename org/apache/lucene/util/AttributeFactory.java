package org.apache.lucene.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles;

public abstract class AttributeFactory
{
    private static final MethodHandles.Lookup lookup;
    private static final MethodType NO_ARG_CTOR;
    private static final MethodType NO_ARG_RETURNING_ATTRIBUTEIMPL;
    public static final AttributeFactory DEFAULT_ATTRIBUTE_FACTORY;
    
    public abstract AttributeImpl createAttributeInstance(final Class<? extends Attribute> p0);
    
    static final MethodHandle findAttributeImplCtor(final Class<? extends AttributeImpl> clazz) {
        try {
            return AttributeFactory.lookup.findConstructor(clazz, AttributeFactory.NO_ARG_CTOR).asType(AttributeFactory.NO_ARG_RETURNING_ATTRIBUTEIMPL);
        }
        catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot lookup accessible no-arg constructor for: " + clazz.getName(), e);
        }
    }
    
    public static <A extends AttributeImpl> AttributeFactory getStaticImplementation(final AttributeFactory delegate, final Class<A> clazz) {
        final MethodHandle constr = findAttributeImplCtor(clazz);
        return new StaticImplementationAttributeFactory<A>(delegate, clazz) {
            @Override
            protected A createInstance() {
                try {
                    return (A)constr.invokeExact();
                }
                catch (final Throwable t) {
                    AttributeFactory.rethrow(t);
                    throw new AssertionError();
                }
            }
        };
    }
    
    static void rethrow(final Throwable t) {
        rethrow0(t);
    }
    
    private static <T extends Throwable> void rethrow0(final Throwable t) throws T, Throwable {
        throw t;
    }
    
    static {
        lookup = MethodHandles.publicLookup();
        NO_ARG_CTOR = MethodType.methodType(Void.TYPE);
        NO_ARG_RETURNING_ATTRIBUTEIMPL = MethodType.methodType(AttributeImpl.class);
        DEFAULT_ATTRIBUTE_FACTORY = new DefaultAttributeFactory();
    }
    
    private static final class DefaultAttributeFactory extends AttributeFactory
    {
        private final ClassValue<MethodHandle> constructors;
        
        DefaultAttributeFactory() {
            this.constructors = new ClassValue<MethodHandle>() {
                @Override
                protected MethodHandle computeValue(final Class<?> attClass) {
                    return AttributeFactory.findAttributeImplCtor(DefaultAttributeFactory.this.findImplClass(attClass.asSubclass(Attribute.class)));
                }
            };
        }
        
        @Override
        public AttributeImpl createAttributeInstance(final Class<? extends Attribute> attClass) {
            try {
                return this.constructors.get(attClass).invokeExact();
            }
            catch (final Throwable t) {
                AttributeFactory.rethrow(t);
                throw new AssertionError();
            }
        }
        
        private Class<? extends AttributeImpl> findImplClass(final Class<? extends Attribute> attClass) {
            try {
                return Class.forName(attClass.getName() + "Impl", true, attClass.getClassLoader()).asSubclass(AttributeImpl.class);
            }
            catch (final ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("Cannot find implementing class for: " + attClass.getName());
            }
        }
    }
    
    public abstract static class StaticImplementationAttributeFactory<A extends AttributeImpl> extends AttributeFactory
    {
        private final AttributeFactory delegate;
        private final Class<A> clazz;
        
        public StaticImplementationAttributeFactory(final AttributeFactory delegate, final Class<A> clazz) {
            this.delegate = delegate;
            this.clazz = clazz;
        }
        
        @Override
        public final AttributeImpl createAttributeInstance(final Class<? extends Attribute> attClass) {
            return attClass.isAssignableFrom(this.clazz) ? this.createInstance() : this.delegate.createAttributeInstance(attClass);
        }
        
        protected abstract A createInstance();
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }
            final StaticImplementationAttributeFactory af = (StaticImplementationAttributeFactory)other;
            return this.delegate.equals(af.delegate) && this.clazz == af.clazz;
        }
        
        @Override
        public int hashCode() {
            return 31 * this.delegate.hashCode() + this.clazz.hashCode();
        }
    }
}
