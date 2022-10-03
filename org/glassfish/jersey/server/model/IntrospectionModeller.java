package org.glassfish.jersey.server.model;

import javax.ws.rs.sse.SseEventSink;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.container.Suspended;
import org.glassfish.jersey.server.ManagedAsync;
import org.glassfish.jersey.internal.util.Tokenizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.util.Iterator;
import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import javax.ws.rs.core.MediaType;
import java.util.List;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedList;
import java.lang.reflect.AnnotatedElement;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.Path;
import org.glassfish.jersey.server.model.internal.ModelHelper;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.util.Producer;
import java.util.logging.Logger;

final class IntrospectionModeller
{
    private static final Logger LOGGER;
    private final Class<?> handlerClass;
    private final boolean disableValidation;
    
    public IntrospectionModeller(final Class<?> handlerClass, final boolean disableValidation) {
        this.handlerClass = handlerClass;
        this.disableValidation = disableValidation;
    }
    
    public Resource.Builder createResourceBuilder() {
        return (Resource.Builder)Errors.processWithException((Producer)new Producer<Resource.Builder>() {
            public Resource.Builder call() {
                return IntrospectionModeller.this.doCreateResourceBuilder();
            }
        });
    }
    
    private Resource.Builder doCreateResourceBuilder() {
        if (!this.disableValidation) {
            this.checkForNonPublicMethodIssues();
        }
        final Class<?> annotatedResourceClass = ModelHelper.getAnnotatedResourceClass(this.handlerClass);
        final Path rPathAnnotation = annotatedResourceClass.getAnnotation(Path.class);
        final boolean keepEncodedParams = null != annotatedResourceClass.getAnnotation(Encoded.class);
        final List<MediaType> defaultConsumedTypes = extractMediaTypes(annotatedResourceClass.getAnnotation(Consumes.class));
        final List<MediaType> defaultProducedTypes = extractMediaTypes(annotatedResourceClass.getAnnotation(Produces.class));
        final Collection<Class<? extends Annotation>> defaultNameBindings = ReflectionHelper.getAnnotationTypes((AnnotatedElement)annotatedResourceClass, (Class)NameBinding.class);
        final MethodList methodList = new MethodList(this.handlerClass);
        final List<Parameter> resourceClassParameters = new LinkedList<Parameter>();
        this.checkResourceClassSetters(methodList, keepEncodedParams, resourceClassParameters);
        this.checkResourceClassFields(keepEncodedParams, InvocableValidator.isSingleton(this.handlerClass), resourceClassParameters);
        Resource.Builder resourceBuilder;
        if (null != rPathAnnotation) {
            resourceBuilder = Resource.builder(rPathAnnotation.value());
        }
        else {
            resourceBuilder = Resource.builder();
        }
        boolean extended = false;
        if (this.handlerClass.isAnnotationPresent(ExtendedResource.class)) {
            resourceBuilder.extended(true);
            extended = true;
        }
        resourceBuilder.name(this.handlerClass.getName());
        this.addResourceMethods(resourceBuilder, methodList, resourceClassParameters, keepEncodedParams, defaultConsumedTypes, defaultProducedTypes, defaultNameBindings, extended);
        this.addSubResourceMethods(resourceBuilder, methodList, resourceClassParameters, keepEncodedParams, defaultConsumedTypes, defaultProducedTypes, defaultNameBindings, extended);
        this.addSubResourceLocators(resourceBuilder, methodList, resourceClassParameters, keepEncodedParams, extended);
        if (IntrospectionModeller.LOGGER.isLoggable(Level.FINEST)) {
            IntrospectionModeller.LOGGER.finest(LocalizationMessages.NEW_AR_CREATED_BY_INTROSPECTION_MODELER(resourceBuilder.toString()));
        }
        return resourceBuilder;
    }
    
    private void checkForNonPublicMethodIssues() {
        final MethodList allDeclaredMethods = new MethodList(this.getAllDeclaredMethods(this.handlerClass));
        for (final AnnotatedMethod m : allDeclaredMethods.withMetaAnnotation(HttpMethod.class).withoutAnnotation(Path.class).isNotPublic()) {
            Errors.warning((Object)this.handlerClass, LocalizationMessages.NON_PUB_RES_METHOD(m.getMethod().toGenericString()));
        }
        for (final AnnotatedMethod m : allDeclaredMethods.withMetaAnnotation(HttpMethod.class).withAnnotation(Path.class).isNotPublic()) {
            Errors.warning((Object)this.handlerClass, LocalizationMessages.NON_PUB_SUB_RES_METHOD(m.getMethod().toGenericString()));
        }
        for (final AnnotatedMethod m : allDeclaredMethods.withoutMetaAnnotation(HttpMethod.class).withAnnotation(Path.class).isNotPublic()) {
            Errors.warning((Object)this.handlerClass, LocalizationMessages.NON_PUB_SUB_RES_LOC(m.getMethod().toGenericString()));
        }
    }
    
    private void checkResourceClassSetters(final MethodList methodList, final boolean encodedFlag, final Collection<Parameter> injectableParameters) {
        for (final AnnotatedMethod method : methodList.withoutMetaAnnotation(HttpMethod.class).withoutAnnotation(Path.class).hasNumParams(1).hasReturnType(Void.TYPE).nameStartsWith("set")) {
            final Parameter p = Parameter.create(this.handlerClass, method.getMethod().getDeclaringClass(), encodedFlag || method.isAnnotationPresent((Class<? extends Annotation>)Encoded.class), method.getParameterTypes()[0], method.getGenericParameterTypes()[0], method.getAnnotations());
            if (null != p) {
                ResourceMethodValidator.validateParameter(p, method.getMethod(), method.getMethod().toGenericString(), "1", InvocableValidator.isSingleton(this.handlerClass));
                if (p.getSource() == Parameter.Source.ENTITY) {
                    continue;
                }
                injectableParameters.add(p);
            }
        }
    }
    
    private void checkResourceClassFields(final boolean encodedFlag, final boolean isInSingleton, final Collection<Parameter> injectableParameters) {
        for (final Field field : AccessController.doPrivileged((PrivilegedAction<Field[]>)ReflectionHelper.getDeclaredFieldsPA((Class)this.handlerClass))) {
            if (field.getDeclaredAnnotations().length > 0) {
                final Parameter p = Parameter.create(this.handlerClass, field.getDeclaringClass(), encodedFlag || field.isAnnotationPresent((Class<? extends Annotation>)Encoded.class), field.getType(), field.getGenericType(), field.getAnnotations());
                if (null != p) {
                    ResourceMethodValidator.validateParameter(p, field, field.toGenericString(), field.getName(), isInSingleton);
                    if (p.getSource() != Parameter.Source.ENTITY) {
                        injectableParameters.add(p);
                    }
                }
            }
        }
    }
    
    private List<Method> getAllDeclaredMethods(final Class<?> clazz) {
        final List<Method> result = new LinkedList<Method>();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                for (Class current = clazz; current != Object.class && current != null; current = current.getSuperclass()) {
                    result.addAll(Arrays.asList(current.getDeclaredMethods()));
                }
                return null;
            }
        });
        return result;
    }
    
    private static List<MediaType> resolveConsumedTypes(final AnnotatedMethod am, final List<MediaType> defaultConsumedTypes) {
        if (am.isAnnotationPresent((Class<? extends Annotation>)Consumes.class)) {
            return extractMediaTypes(am.getAnnotation(Consumes.class));
        }
        return defaultConsumedTypes;
    }
    
    private static List<MediaType> resolveProducedTypes(final AnnotatedMethod am, final List<MediaType> defaultProducedTypes) {
        if (am.isAnnotationPresent((Class<? extends Annotation>)Produces.class)) {
            return extractMediaTypes(am.getAnnotation(Produces.class));
        }
        return defaultProducedTypes;
    }
    
    private static List<MediaType> extractMediaTypes(final Consumes annotation) {
        return (annotation != null) ? extractMediaTypes(annotation.value()) : Collections.emptyList();
    }
    
    private static List<MediaType> extractMediaTypes(final Produces annotation) {
        return (annotation != null) ? extractMediaTypes(annotation.value()) : Collections.emptyList();
    }
    
    private static List<MediaType> extractMediaTypes(final String[] values) {
        if (values.length == 0) {
            return Collections.emptyList();
        }
        final List<MediaType> types = new ArrayList<MediaType>(values.length);
        for (final String mtEntry : values) {
            for (final String mt : Tokenizer.tokenize(mtEntry, ",")) {
                types.add(MediaType.valueOf(mt));
            }
        }
        return types;
    }
    
    private static void introspectAsyncFeatures(final AnnotatedMethod am, final ResourceMethod.Builder resourceMethodBuilder) {
        if (am.isAnnotationPresent(ManagedAsync.class)) {
            resourceMethodBuilder.managedAsync();
        }
        for (final Annotation[] array : am.getParameterAnnotations()) {
            final Annotation[] annotations = array;
            for (final Annotation annotation : array) {
                if (annotation.annotationType() == Suspended.class) {
                    resourceMethodBuilder.suspended(0L, TimeUnit.MILLISECONDS);
                }
            }
        }
        for (final Class<?> paramType : am.getParameterTypes()) {
            if (SseEventSink.class.equals(paramType)) {
                resourceMethodBuilder.sse();
            }
        }
    }
    
    private void addResourceMethods(final Resource.Builder resourceBuilder, final MethodList methodList, final List<Parameter> resourceClassParameters, final boolean encodedParameters, final List<MediaType> defaultConsumedTypes, final List<MediaType> defaultProducedTypes, final Collection<Class<? extends Annotation>> defaultNameBindings, final boolean extended) {
        for (final AnnotatedMethod am : methodList.withMetaAnnotation(HttpMethod.class).withoutAnnotation(Path.class)) {
            final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod(am.getMetaMethodAnnotations(HttpMethod.class).get(0).value()).consumes(resolveConsumedTypes(am, defaultConsumedTypes)).produces(resolveProducedTypes(am, defaultProducedTypes)).encodedParameters(encodedParameters || am.isAnnotationPresent((Class<? extends Annotation>)Encoded.class)).nameBindings(defaultNameBindings).nameBindings(am.getAnnotations()).handledBy(this.handlerClass, am.getMethod()).handlingMethod(am.getDeclaredMethod()).handlerParameters(resourceClassParameters).extended(extended || am.isAnnotationPresent(ExtendedResource.class));
            introspectAsyncFeatures(am, methodBuilder);
        }
    }
    
    private void addSubResourceMethods(final Resource.Builder resourceBuilder, final MethodList methodList, final List<Parameter> resourceClassParameters, final boolean encodedParameters, final List<MediaType> defaultConsumedTypes, final List<MediaType> defaultProducedTypes, final Collection<Class<? extends Annotation>> defaultNameBindings, final boolean extended) {
        for (final AnnotatedMethod am : methodList.withMetaAnnotation(HttpMethod.class).withAnnotation(Path.class)) {
            final Resource.Builder childResourceBuilder = resourceBuilder.addChildResource(am.getAnnotation(Path.class).value());
            final ResourceMethod.Builder methodBuilder = childResourceBuilder.addMethod(am.getMetaMethodAnnotations(HttpMethod.class).get(0).value()).consumes(resolveConsumedTypes(am, defaultConsumedTypes)).produces(resolveProducedTypes(am, defaultProducedTypes)).encodedParameters(encodedParameters || am.isAnnotationPresent((Class<? extends Annotation>)Encoded.class)).nameBindings(defaultNameBindings).nameBindings(am.getAnnotations()).handledBy(this.handlerClass, am.getMethod()).handlingMethod(am.getDeclaredMethod()).handlerParameters(resourceClassParameters).extended(extended || am.isAnnotationPresent(ExtendedResource.class));
            introspectAsyncFeatures(am, methodBuilder);
        }
    }
    
    private void addSubResourceLocators(final Resource.Builder resourceBuilder, final MethodList methodList, final List<Parameter> resourceClassParameters, final boolean encodedParameters, final boolean extended) {
        for (final AnnotatedMethod am : methodList.withoutMetaAnnotation(HttpMethod.class).withAnnotation(Path.class)) {
            final String path = am.getAnnotation(Path.class).value();
            Resource.Builder builder = resourceBuilder;
            if (path != null && !path.isEmpty() && !"/".equals(path)) {
                builder = resourceBuilder.addChildResource(path);
            }
            builder.addMethod().encodedParameters(encodedParameters || am.isAnnotationPresent((Class<? extends Annotation>)Encoded.class)).handledBy(this.handlerClass, am.getMethod()).handlingMethod(am.getDeclaredMethod()).handlerParameters(resourceClassParameters).extended(extended || am.isAnnotationPresent(ExtendedResource.class));
        }
    }
    
    static {
        LOGGER = Logger.getLogger(IntrospectionModeller.class.getName());
    }
}
