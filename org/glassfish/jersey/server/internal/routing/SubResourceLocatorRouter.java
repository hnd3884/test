package org.glassfish.jersey.server.internal.routing;

import javax.ws.rs.core.SecurityContext;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import org.glassfish.jersey.server.SubjectSecurityContext;
import java.lang.reflect.InvocationTargetException;
import org.glassfish.jersey.server.internal.process.MappableException;
import javax.ws.rs.WebApplicationException;
import java.lang.reflect.UndeclaredThrowableException;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.model.Resource;
import javax.ws.rs.NotFoundException;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.model.Parameterized;
import org.glassfish.jersey.server.spi.internal.ParameterValueHelper;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.Collection;
import java.util.function.Function;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.server.spi.internal.ParamValueFactoryWithSource;
import java.util.List;
import org.glassfish.jersey.server.model.ResourceMethod;

final class SubResourceLocatorRouter implements Router
{
    private final ResourceMethod locatorModel;
    private final List<ParamValueFactoryWithSource<?>> valueProviders;
    private final RuntimeLocatorModelBuilder runtimeLocatorBuilder;
    private final JerseyResourceContext resourceContext;
    private final Function<Class<?>, ?> createFunction;
    
    SubResourceLocatorRouter(final Function<Class<?>, ?> createServiceFunction, final Collection<ValueParamProvider> valueSuppliers, final ResourceMethod locatorModel, final JerseyResourceContext resourceContext, final RuntimeLocatorModelBuilder runtimeLocatorBuilder) {
        this.runtimeLocatorBuilder = runtimeLocatorBuilder;
        this.locatorModel = locatorModel;
        this.resourceContext = resourceContext;
        this.createFunction = createServiceFunction;
        this.valueProviders = ParameterValueHelper.createValueProviders(valueSuppliers, locatorModel.getInvocable());
    }
    
    @Override
    public Continuation apply(final RequestProcessingContext processingContext) {
        Object subResourceInstance = this.getResource(processingContext);
        if (subResourceInstance == null) {
            throw new NotFoundException();
        }
        final RoutingContext routingContext = processingContext.routingContext();
        LocatorRouting routing;
        if (subResourceInstance instanceof Resource) {
            routing = this.runtimeLocatorBuilder.getRouting((Resource)subResourceInstance);
        }
        else {
            Class<?> locatorClass = subResourceInstance.getClass();
            if (locatorClass.isAssignableFrom(Class.class)) {
                locatorClass = (Class)subResourceInstance;
                if (!this.runtimeLocatorBuilder.isCached(locatorClass)) {
                    subResourceInstance = this.createFunction.apply(locatorClass);
                }
            }
            routingContext.pushMatchedResource(subResourceInstance);
            this.resourceContext.bindResourceIfSingleton(subResourceInstance);
            routing = this.runtimeLocatorBuilder.getRouting(locatorClass);
        }
        routingContext.pushLocatorSubResource(routing.locator.getResources().get(0));
        processingContext.triggerEvent(RequestEvent.Type.SUBRESOURCE_LOCATED);
        return Continuation.of(processingContext, routing.router);
    }
    
    private Object getResource(final RequestProcessingContext context) {
        final Object resource = context.routingContext().peekMatchedResource();
        final Method handlingMethod = this.locatorModel.getInvocable().getHandlingMethod();
        final Object[] parameterValues = ParameterValueHelper.getParameterValues(this.valueProviders, context.request());
        context.triggerEvent(RequestEvent.Type.LOCATOR_MATCHED);
        final PrivilegedAction invokeMethodAction = () -> {
            try {
                return handlingMethod.invoke(resource, parameterValues);
            }
            catch (final IllegalAccessException | IllegalArgumentException | UndeclaredThrowableException ex) {
                throw new ProcessingException(LocalizationMessages.ERROR_RESOURCE_JAVA_METHOD_INVOCATION(), (Throwable)ex);
            }
            catch (final InvocationTargetException ex2) {
                final Throwable cause = ex2.getCause();
                if (cause instanceof WebApplicationException) {
                    throw (WebApplicationException)cause;
                }
                else {
                    throw new MappableException(cause);
                }
            }
            catch (final Throwable t) {
                throw new ProcessingException(t);
            }
        };
        final SecurityContext securityContext = context.request().getSecurityContext();
        return (securityContext instanceof SubjectSecurityContext) ? ((SubjectSecurityContext)securityContext).doAsSubject(invokeMethodAction) : invokeMethodAction.run();
    }
}
