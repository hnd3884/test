package org.glassfish.jersey.server.model.internal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.SecurityContext;
import javax.validation.ValidationException;
import org.glassfish.jersey.server.internal.process.MappableException;
import org.glassfish.jersey.server.SubjectSecurityContext;
import java.util.concurrent.CompletableFuture;
import java.lang.reflect.UndeclaredThrowableException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.concurrent.ExecutionException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletionStage;
import java.security.PrivilegedAction;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.internal.TracingLogger;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.glassfish.jersey.server.model.Invocable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;

abstract class AbstractJavaResourceMethodDispatcher implements ResourceMethodDispatcher
{
    private final Method method;
    private final InvocationHandler methodHandler;
    private final Invocable resourceMethod;
    private final ConfiguredValidator validator;
    
    AbstractJavaResourceMethodDispatcher(final Invocable resourceMethod, final InvocationHandler methodHandler, final ConfiguredValidator validator) {
        this.method = resourceMethod.getDefinitionMethod();
        this.methodHandler = methodHandler;
        this.resourceMethod = resourceMethod;
        this.validator = validator;
    }
    
    @Override
    public final Response dispatch(final Object resource, final ContainerRequest request) throws ProcessingException {
        Response response = null;
        try {
            response = this.doDispatch(resource, request);
        }
        finally {
            TracingLogger.getInstance((PropertiesDelegate)request).log((TracingLogger.Event)ServerTraceEvent.DISPATCH_RESPONSE, new Object[] { response });
        }
        return response;
    }
    
    protected abstract Response doDispatch(final Object p0, final ContainerRequest p1) throws ProcessingException;
    
    final Object invoke(final ContainerRequest containerRequest, final Object resource, final Object... args) throws ProcessingException {
        try {
            if (this.validator != null) {
                this.validator.validateResourceAndInputParams(resource, this.resourceMethod, args);
            }
            final PrivilegedAction invokeMethodAction = new PrivilegedAction() {
                @Override
                public Object run() {
                    final TracingLogger tracingLogger = TracingLogger.getInstance((PropertiesDelegate)containerRequest);
                    final long timestamp = tracingLogger.timestamp((TracingLogger.Event)ServerTraceEvent.METHOD_INVOKE);
                    try {
                        final Object result = AbstractJavaResourceMethodDispatcher.this.methodHandler.invoke(resource, AbstractJavaResourceMethodDispatcher.this.method, args);
                        if (result instanceof CompletionStage) {
                            CompletableFuture resultFuture;
                            try {
                                resultFuture = ((CompletionStage)result).toCompletableFuture();
                            }
                            catch (final UnsupportedOperationException e) {
                                return result;
                            }
                            if (resultFuture != null && resultFuture.isDone()) {
                                if (resultFuture.isCancelled()) {
                                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
                                }
                                try {
                                    return resultFuture.get();
                                }
                                catch (final ExecutionException e2) {
                                    throw new InvocationTargetException(e2.getCause());
                                }
                            }
                        }
                        return result;
                    }
                    catch (final IllegalAccessException | IllegalArgumentException | UndeclaredThrowableException ex) {
                        throw new ProcessingException(LocalizationMessages.ERROR_RESOURCE_JAVA_METHOD_INVOCATION(), (Throwable)ex);
                    }
                    catch (final InvocationTargetException ex2) {
                        throw mapTargetToRuntimeEx(ex2.getCause());
                    }
                    catch (final Throwable t) {
                        throw new ProcessingException(t);
                    }
                    finally {
                        tracingLogger.logDuration((TracingLogger.Event)ServerTraceEvent.METHOD_INVOKE, timestamp, new Object[] { resource, AbstractJavaResourceMethodDispatcher.this.method });
                    }
                }
            };
            final SecurityContext securityContext = containerRequest.getSecurityContext();
            final Object invocationResult = (securityContext instanceof SubjectSecurityContext) ? ((SubjectSecurityContext)securityContext).doAsSubject(invokeMethodAction) : invokeMethodAction.run();
            if (this.validator != null) {
                this.validator.validateResult(resource, this.resourceMethod, invocationResult);
            }
            return invocationResult;
        }
        catch (final ValidationException ex) {
            throw new MappableException((Throwable)ex);
        }
    }
    
    private static RuntimeException mapTargetToRuntimeEx(final Throwable throwable) {
        if (throwable instanceof WebApplicationException) {
            return (RuntimeException)throwable;
        }
        return (RuntimeException)new MappableException(throwable);
    }
    
    @Override
    public String toString() {
        return this.method.toString();
    }
}
