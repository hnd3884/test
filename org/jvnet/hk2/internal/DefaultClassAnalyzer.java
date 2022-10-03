package org.jvnet.hk2.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Iterator;
import org.glassfish.hk2.api.MultiException;
import java.lang.reflect.Constructor;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Visibility;
import javax.inject.Named;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ClassAnalyzer;

@Singleton
@Named("default")
@Visibility(DescriptorVisibility.LOCAL)
public class DefaultClassAnalyzer implements ClassAnalyzer
{
    private final ServiceLocatorImpl locator;
    
    public DefaultClassAnalyzer(final ServiceLocatorImpl locator) {
        this.locator = locator;
    }
    
    public <T> Constructor<T> getConstructor(final Class<T> clazz) throws MultiException, NoSuchMethodException {
        final Collector collector = new Collector();
        final Constructor<T> retVal = (Constructor<T>)Utilities.findProducerConstructor(clazz, this.locator, collector);
        try {
            collector.throwIfErrors();
        }
        catch (final MultiException me) {
            for (final Throwable th : me.getErrors()) {
                if (th instanceof NoSuchMethodException) {
                    throw (NoSuchMethodException)th;
                }
            }
            throw me;
        }
        return retVal;
    }
    
    public <T> Set<Method> getInitializerMethods(final Class<T> clazz) throws MultiException {
        final Collector collector = new Collector();
        final Set<Method> retVal = Utilities.findInitializerMethods(clazz, this.locator, collector);
        collector.throwIfErrors();
        return retVal;
    }
    
    public <T> Set<Field> getFields(final Class<T> clazz) throws MultiException {
        final Collector collector = new Collector();
        final Set<Field> retVal = Utilities.findInitializerFields(clazz, this.locator, collector);
        collector.throwIfErrors();
        return retVal;
    }
    
    public <T> Method getPostConstructMethod(final Class<T> clazz) throws MultiException {
        final Collector collector = new Collector();
        final Method retVal = Utilities.findPostConstruct(clazz, this.locator, collector);
        collector.throwIfErrors();
        return retVal;
    }
    
    public <T> Method getPreDestroyMethod(final Class<T> clazz) throws MultiException {
        final Collector collector = new Collector();
        final Method retVal = Utilities.findPreDestroy(clazz, this.locator, collector);
        collector.throwIfErrors();
        return retVal;
    }
}
