package org.glassfish.jersey.server.model;

import java.util.Iterator;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.inject.PerLookup;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

class InvocableValidator extends AbstractResourceModelVisitor
{
    private static final Set<Class<?>> SCOPE_ANNOTATIONS;
    protected final Set<Class<?>> checkedClasses;
    
    InvocableValidator() {
        this.checkedClasses = new HashSet<Class<?>>();
    }
    
    private static Set<Class<?>> getScopeAnnotations() {
        final Set<Class<?>> scopeAnnotations = new HashSet<Class<?>>();
        scopeAnnotations.add(Singleton.class);
        scopeAnnotations.add(PerLookup.class);
        return scopeAnnotations;
    }
    
    @Override
    public void visitInvocable(final Invocable invocable) {
        final Class resClass = invocable.getHandler().getHandlerClass();
        if (resClass != null && !this.checkedClasses.contains(resClass)) {
            this.checkedClasses.add(resClass);
            final boolean provider = Providers.isProvider(resClass);
            int counter = 0;
            for (final Annotation annotation : resClass.getAnnotations()) {
                if (InvocableValidator.SCOPE_ANNOTATIONS.contains(annotation.annotationType())) {
                    ++counter;
                }
            }
            if (counter == 0 && provider) {
                Errors.warning((Object)resClass, LocalizationMessages.RESOURCE_IMPLEMENTS_PROVIDER(resClass, Providers.getProviderContracts(resClass)));
            }
            else if (counter > 1) {
                Errors.fatal((Object)resClass, LocalizationMessages.RESOURCE_MULTIPLE_SCOPE_ANNOTATIONS(resClass));
            }
        }
    }
    
    public static boolean isSingleton(final Class<?> resourceClass) {
        return resourceClass.isAnnotationPresent((Class<? extends Annotation>)Singleton.class) || (Providers.isProvider((Class)resourceClass) && !resourceClass.isAnnotationPresent((Class<? extends Annotation>)PerLookup.class));
    }
    
    @Override
    public void visitResourceHandlerConstructor(final HandlerConstructor constructor) {
        final Class<?> resClass = constructor.getConstructor().getDeclaringClass();
        final boolean isSingleton = isSingleton(resClass);
        int paramCount = 0;
        for (final Parameter p : constructor.getParameters()) {
            ResourceMethodValidator.validateParameter(p, constructor.getConstructor(), constructor.getConstructor().toGenericString(), Integer.toString(++paramCount), isSingleton);
        }
    }
    
    static {
        SCOPE_ANNOTATIONS = getScopeAnnotations();
    }
}
