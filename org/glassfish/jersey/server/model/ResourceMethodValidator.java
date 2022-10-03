package org.glassfish.jersey.server.model;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import javax.ws.rs.BeanParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import java.util.HashSet;
import java.lang.reflect.Method;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.spi.internal.ParameterValueHelper;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Iterator;
import javax.ws.rs.Path;
import javax.ws.rs.HttpMethod;
import java.util.LinkedList;
import java.lang.annotation.Annotation;
import javax.ws.rs.FormParam;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.ws.rs.sse.SseEventSink;
import java.util.Set;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.Collection;

class ResourceMethodValidator extends AbstractResourceModelVisitor
{
    private final Collection<ValueParamProvider> valueParamProviders;
    private static final Set<Class> PARAM_ANNOTATION_SET;
    
    ResourceMethodValidator(final Collection<ValueParamProvider> valueParamProviders) {
        this.valueParamProviders = valueParamProviders;
    }
    
    @Override
    public void visitResourceMethod(final ResourceMethod method) {
        switch (method.getType()) {
            case RESOURCE_METHOD: {
                this.visitJaxrsResourceMethod(method);
                break;
            }
            case SUB_RESOURCE_LOCATOR: {
                this.visitSubResourceLocator(method);
                break;
            }
        }
    }
    
    private void visitJaxrsResourceMethod(final ResourceMethod method) {
        this.checkMethod(method);
    }
    
    private void checkMethod(final ResourceMethod method) {
        this.checkValueProviders(method);
        final Invocable invocable = method.getInvocable();
        this.checkParameters(method);
        if ("GET".equals(method.getHttpMethod())) {
            final long eventSinkCount = invocable.getParameters().stream().filter(parameter -> SseEventSink.class.equals(parameter.getRawType())).count();
            final boolean isSse = eventSinkCount > 0L;
            if (eventSinkCount > 1L) {
                Errors.warning((Object)method, LocalizationMessages.MULTIPLE_EVENT_SINK_INJECTION(invocable.getHandlingMethod()));
            }
            if (Void.TYPE == invocable.getHandlingMethod().getReturnType() && !method.isSuspendDeclared() && !isSse) {
                Errors.hint((Object)method, LocalizationMessages.GET_RETURNS_VOID(invocable.getHandlingMethod()));
            }
            if (invocable.requiresEntity() && !invocable.isInflector()) {
                Errors.warning((Object)method, LocalizationMessages.GET_CONSUMES_ENTITY(invocable.getHandlingMethod()));
            }
            for (final Parameter p : invocable.getParameters()) {
                if (p.isAnnotationPresent((Class<? extends Annotation>)FormParam.class)) {
                    Errors.fatal((Object)method, LocalizationMessages.GET_CONSUMES_FORM_PARAM(invocable.getHandlingMethod()));
                    break;
                }
            }
            if (isSse && Void.TYPE != invocable.getHandlingMethod().getReturnType()) {
                Errors.fatal((Object)method, LocalizationMessages.EVENT_SINK_RETURNS_TYPE(invocable.getHandlingMethod()));
            }
        }
        final List<String> httpMethodAnnotations = new LinkedList<String>();
        for (final Annotation a : invocable.getHandlingMethod().getDeclaredAnnotations()) {
            if (null != a.annotationType().getAnnotation(HttpMethod.class)) {
                httpMethodAnnotations.add(a.toString());
            }
        }
        if (httpMethodAnnotations.size() > 1) {
            Errors.fatal((Object)method, LocalizationMessages.MULTIPLE_HTTP_METHOD_DESIGNATORS(invocable.getHandlingMethod(), httpMethodAnnotations.toString()));
        }
        final Type responseType = invocable.getResponseType();
        if (!isConcreteType(responseType)) {
            Errors.warning((Object)invocable.getHandlingMethod(), LocalizationMessages.TYPE_OF_METHOD_NOT_RESOLVABLE_TO_CONCRETE_TYPE(responseType, invocable.getHandlingMethod().toGenericString()));
        }
        final Path pathAnnotation = invocable.getHandlingMethod().getAnnotation(Path.class);
        if (pathAnnotation != null) {
            final String path = pathAnnotation.value();
            if (path == null || path.isEmpty() || "/".equals(path)) {
                Errors.warning((Object)invocable.getHandlingMethod(), LocalizationMessages.METHOD_EMPTY_PATH_ANNOTATION(invocable.getHandlingMethod().getName(), invocable.getHandler().getHandlerClass().getName()));
            }
        }
        if (httpMethodAnnotations.size() != 0) {
            this.checkUnexpectedAnnotations(method);
        }
    }
    
    private void checkUnexpectedAnnotations(final ResourceMethod resourceMethod) {
        final Invocable invocable = resourceMethod.getInvocable();
        for (final Annotation annotation : invocable.getHandlingMethod().getDeclaredAnnotations()) {
            if (ResourceMethodValidator.PARAM_ANNOTATION_SET.contains(annotation.annotationType())) {
                Errors.fatal((Object)resourceMethod, LocalizationMessages.METHOD_UNEXPECTED_ANNOTATION(invocable.getHandlingMethod().getName(), invocable.getHandler().getHandlerClass().getName(), annotation.annotationType().getName()));
            }
        }
    }
    
    private void checkValueProviders(final ResourceMethod method) {
        final List<? extends Function<ContainerRequest, ?>> valueProviders = ParameterValueHelper.createValueProviders(this.valueParamProviders, method.getInvocable());
        if (valueProviders.contains(null)) {
            final int index = valueProviders.indexOf(null);
            Errors.fatal((Object)method, LocalizationMessages.ERROR_PARAMETER_MISSING_VALUE_PROVIDER(index, method.getInvocable().getHandlingMethod()));
        }
    }
    
    private void visitSubResourceLocator(final ResourceMethod locator) {
        this.checkParameters(locator);
        this.checkValueProviders(locator);
        final Invocable invocable = locator.getInvocable();
        if (Void.TYPE == invocable.getRawResponseType()) {
            Errors.fatal((Object)locator, LocalizationMessages.SUBRES_LOC_RETURNS_VOID(invocable.getHandlingMethod()));
        }
        if (invocable.getHandlingMethod().getAnnotation(Path.class) != null) {
            this.checkUnexpectedAnnotations(locator);
        }
    }
    
    private void checkParameters(final ResourceMethod method) {
        final Invocable invocable = method.getInvocable();
        final Method handlingMethod = invocable.getHandlingMethod();
        int paramCount = 0;
        int nonAnnotatedParameters = 0;
        for (final Parameter p : invocable.getParameters()) {
            validateParameter(p, handlingMethod, handlingMethod.toGenericString(), Integer.toString(++paramCount), false);
            if (method.getType() == ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR && Parameter.Source.ENTITY == p.getSource()) {
                Errors.fatal((Object)method, LocalizationMessages.SUBRES_LOC_HAS_ENTITY_PARAM(invocable.getHandlingMethod()));
            }
            else {
                if (p.getAnnotations().length != 0 || ++nonAnnotatedParameters <= 1) {
                    continue;
                }
                Errors.fatal((Object)method, LocalizationMessages.AMBIGUOUS_NON_ANNOTATED_PARAMETER(invocable.getHandlingMethod(), invocable.getHandlingMethod().getDeclaringClass()));
            }
        }
    }
    
    private boolean isSseInjected(final Invocable invocable) {
        return invocable.getParameters().stream().anyMatch(parameter -> SseEventSink.class.equals(parameter.getRawType()));
    }
    
    private static Set<Class> createParamAnnotationSet() {
        final Set<Class> set = new HashSet<Class>(6);
        set.add(HeaderParam.class);
        set.add(CookieParam.class);
        set.add(MatrixParam.class);
        set.add(QueryParam.class);
        set.add(PathParam.class);
        set.add(BeanParam.class);
        return (Set<Class>)Collections.unmodifiableSet((Set<? extends Class>)set);
    }
    
    static void validateParameter(final Parameter parameter, final Object source, final String reportedSourceName, final String reportedParameterName, final boolean injectionsForbidden) {
        Errors.processWithException((Runnable)new Runnable() {
            @Override
            public void run() {
                int counter = 0;
                final Annotation[] annotations2;
                final Annotation[] annotations = annotations2 = parameter.getAnnotations();
                for (final Annotation a : annotations2) {
                    if (ResourceMethodValidator.PARAM_ANNOTATION_SET.contains(a.annotationType())) {
                        if (injectionsForbidden) {
                            Errors.fatal(source, LocalizationMessages.SINGLETON_INJECTS_PARAMETER(reportedSourceName, reportedParameterName));
                            break;
                        }
                        if (++counter > 1) {
                            Errors.warning(source, LocalizationMessages.AMBIGUOUS_PARAMETER(reportedSourceName, reportedParameterName));
                            break;
                        }
                    }
                }
                final Type paramType = parameter.getType();
                if (!isConcreteType(paramType)) {
                    Errors.warning(source, LocalizationMessages.PARAMETER_UNRESOLVABLE(reportedParameterName, paramType, reportedSourceName));
                }
            }
        });
    }
    
    private static boolean isConcreteType(final Type t) {
        if (t instanceof ParameterizedType) {
            return isConcreteParameterizedType((ParameterizedType)t);
        }
        return t instanceof Class;
    }
    
    private static boolean isConcreteParameterizedType(final ParameterizedType pt) {
        boolean isConcrete = true;
        for (final Type t : pt.getActualTypeArguments()) {
            isConcrete &= isConcreteType(t);
        }
        return isConcrete;
    }
    
    static {
        PARAM_ANNOTATION_SET = createParamAnnotationSet();
    }
}
