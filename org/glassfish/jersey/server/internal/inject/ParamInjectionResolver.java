package org.glassfish.jersey.server.internal.inject;

import javax.ws.rs.Encoded;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.lang.reflect.Type;
import java.lang.reflect.AnnotatedElement;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.reflect.Constructor;
import org.glassfish.jersey.internal.inject.Injectee;
import org.glassfish.jersey.server.ContainerRequest;
import javax.inject.Provider;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import java.lang.annotation.Annotation;

public class ParamInjectionResolver<A extends Annotation> implements InjectionResolver<A>
{
    private final ValueParamProvider valueParamProvider;
    private final Class<A> annotation;
    private final Provider<ContainerRequest> request;
    
    public ParamInjectionResolver(final ValueParamProvider valueParamProvider, final Class<A> annotation, final Provider<ContainerRequest> request) {
        this.valueParamProvider = valueParamProvider;
        this.annotation = annotation;
        this.request = request;
    }
    
    public Object resolve(final Injectee injectee) {
        final AnnotatedElement annotated = injectee.getParent();
        Annotation[] annotations;
        if (annotated.getClass().equals(Constructor.class)) {
            annotations = ((Constructor)annotated).getParameterAnnotations()[injectee.getPosition()];
        }
        else {
            annotations = annotated.getDeclaredAnnotations();
        }
        final Class componentClass = injectee.getInjecteeClass();
        final Type genericType = injectee.getRequiredType();
        Type targetGenericType;
        if (injectee.isFactory()) {
            targetGenericType = ReflectionHelper.getTypeArgument(genericType, 0);
        }
        else {
            targetGenericType = genericType;
        }
        final Class<?> targetType = ReflectionHelper.erasure(targetGenericType);
        final Parameter parameter = Parameter.create(componentClass, componentClass, this.hasEncodedAnnotation(injectee), targetType, targetGenericType, annotations);
        final Function<ContainerRequest, ?> valueProvider = this.valueParamProvider.getValueProvider(parameter);
        if (valueProvider == null) {
            return null;
        }
        if (injectee.isFactory()) {
            return () -> valueProvider.apply(this.request.get());
        }
        return valueProvider.apply((ContainerRequest)this.request.get());
    }
    
    private boolean hasEncodedAnnotation(final Injectee injectee) {
        final AnnotatedElement element = injectee.getParent();
        final boolean isConstructor = element instanceof Constructor;
        final boolean isMethod = element instanceof Method;
        if (isConstructor || isMethod) {
            Annotation[] annotations;
            if (isMethod) {
                annotations = ((Method)element).getParameterAnnotations()[injectee.getPosition()];
            }
            else {
                annotations = ((Constructor)element).getParameterAnnotations()[injectee.getPosition()];
            }
            for (final Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Encoded.class)) {
                    return true;
                }
            }
        }
        if (element.isAnnotationPresent((Class<? extends Annotation>)Encoded.class)) {
            return true;
        }
        final Class<?> clazz = injectee.getInjecteeClass();
        return clazz.isAnnotationPresent((Class<? extends Annotation>)Encoded.class);
    }
    
    public boolean isConstructorParameterIndicator() {
        return true;
    }
    
    public boolean isMethodParameterIndicator() {
        return false;
    }
    
    public Class<A> getAnnotation() {
        return this.annotation;
    }
}
