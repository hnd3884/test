package org.glassfish.jersey.server.model;

import java.util.Collections;
import java.util.ArrayList;
import javax.ws.rs.container.ContainerRequestContext;
import org.glassfish.jersey.process.Inflector;
import java.util.function.Function;
import java.util.Iterator;
import javax.ws.rs.NameBinding;
import org.glassfish.jersey.message.internal.MediaTypes;
import java.util.LinkedList;
import java.util.LinkedHashSet;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.Set;
import org.glassfish.jersey.uri.PathPattern;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.glassfish.jersey.model.NameBound;

public final class ResourceMethod implements ResourceModelComponent, Producing, Consuming, Suspendable, NameBound
{
    private final Data data;
    private final Resource parent;
    
    static List<ResourceMethod> transform(final Resource parent, final List<Data> list) {
        return list.stream().map(data1 -> {
            Object o = null;
            if (data1 == null) {
                o = null;
            }
            else {
                new(org.glassfish.jersey.server.model.ResourceMethod.class)();
                new ResourceMethod(parent, data1);
            }
            return o;
        }).collect((Collector<? super Object, ?, List<ResourceMethod>>)Collectors.toList());
    }
    
    ResourceMethod(final Resource parent, final Data data) {
        this.parent = parent;
        this.data = data;
    }
    
    Data getData() {
        return this.data;
    }
    
    public Resource getParent() {
        return this.parent;
    }
    
    public JaxrsType getType() {
        return this.data.getType();
    }
    
    public String getHttpMethod() {
        return this.data.getHttpMethod();
    }
    
    public Invocable getInvocable() {
        return this.data.getInvocable();
    }
    
    public boolean isExtended() {
        return this.data.extended;
    }
    
    @Override
    public List<MediaType> getConsumedTypes() {
        return this.data.getConsumedTypes();
    }
    
    @Override
    public List<MediaType> getProducedTypes() {
        return this.data.getProducedTypes();
    }
    
    @Override
    public long getSuspendTimeout() {
        return this.data.getSuspendTimeout();
    }
    
    @Override
    public TimeUnit getSuspendTimeoutUnit() {
        return this.data.getSuspendTimeoutUnit();
    }
    
    @Override
    public boolean isSuspendDeclared() {
        return this.data.isSuspended();
    }
    
    public boolean isSse() {
        return this.data.isSse();
    }
    
    @Override
    public boolean isManagedAsyncDeclared() {
        return this.data.isManagedAsync();
    }
    
    @Override
    public List<? extends ResourceModelComponent> getComponents() {
        return Arrays.asList(this.data.getInvocable());
    }
    
    @Override
    public void accept(final ResourceModelVisitor visitor) {
        visitor.visitResourceMethod(this);
    }
    
    public boolean isNameBound() {
        return !this.data.getNameBindings().isEmpty();
    }
    
    public Collection<Class<? extends Annotation>> getNameBindings() {
        return this.data.getNameBindings();
    }
    
    @Override
    public String toString() {
        return "ResourceMethod{" + this.data.toString() + '}';
    }
    
    public enum JaxrsType
    {
        RESOURCE_METHOD {
            @Override
            PathPattern createPatternFor(final String pathTemplate) {
                return PathPattern.END_OF_PATH_PATTERN;
            }
        }, 
        SUB_RESOURCE_METHOD {
            @Override
            PathPattern createPatternFor(final String pathTemplate) {
                return new PathPattern(pathTemplate, PathPattern.RightHandPath.capturingZeroSegments);
            }
        }, 
        SUB_RESOURCE_LOCATOR {
            @Override
            PathPattern createPatternFor(final String pathTemplate) {
                return new PathPattern(pathTemplate, PathPattern.RightHandPath.capturingZeroOrMoreSegments);
            }
        };
        
        abstract PathPattern createPatternFor(final String p0);
        
        private static JaxrsType classify(final String httpMethod) {
            if (httpMethod != null && !httpMethod.isEmpty()) {
                return JaxrsType.RESOURCE_METHOD;
            }
            return JaxrsType.SUB_RESOURCE_LOCATOR;
        }
    }
    
    public static final class Builder
    {
        private final Resource.Builder parent;
        private String httpMethod;
        private final Set<MediaType> consumedTypes;
        private final Set<MediaType> producedTypes;
        private boolean managedAsync;
        private boolean sse;
        private boolean suspended;
        private long suspendTimeout;
        private TimeUnit suspendTimeoutUnit;
        private Class<?> handlerClass;
        private Object handlerInstance;
        private final Collection<Parameter> handlerParameters;
        private Method definitionMethod;
        private Method handlingMethod;
        private boolean encodedParams;
        private Type routingResponseType;
        private final Collection<Class<? extends Annotation>> nameBindings;
        private boolean extended;
        
        Builder(final Resource.Builder parent) {
            this.parent = parent;
            this.httpMethod = null;
            this.consumedTypes = new LinkedHashSet<MediaType>();
            this.producedTypes = new LinkedHashSet<MediaType>();
            this.suspended = false;
            this.suspendTimeout = 0L;
            this.suspendTimeoutUnit = TimeUnit.MILLISECONDS;
            this.handlerParameters = new LinkedList<Parameter>();
            this.encodedParams = false;
            this.nameBindings = new LinkedHashSet<Class<? extends Annotation>>();
        }
        
        Builder(final Resource.Builder parent, final ResourceMethod originalMethod) {
            this.parent = parent;
            this.consumedTypes = new LinkedHashSet<MediaType>(originalMethod.getConsumedTypes());
            this.producedTypes = new LinkedHashSet<MediaType>(originalMethod.getProducedTypes());
            this.suspended = originalMethod.isSuspendDeclared();
            this.suspendTimeout = originalMethod.getSuspendTimeout();
            this.suspendTimeoutUnit = originalMethod.getSuspendTimeoutUnit();
            this.handlerParameters = new LinkedHashSet<Parameter>(originalMethod.getInvocable().getHandler().getParameters());
            this.nameBindings = originalMethod.getNameBindings();
            this.httpMethod = originalMethod.getHttpMethod();
            this.managedAsync = originalMethod.isManagedAsyncDeclared();
            final Invocable invocable = originalMethod.getInvocable();
            this.handlingMethod = invocable.getHandlingMethod();
            this.encodedParams = false;
            this.routingResponseType = invocable.getRoutingResponseType();
            this.extended = originalMethod.isExtended();
            final Method handlerMethod = invocable.getDefinitionMethod();
            final MethodHandler handler = invocable.getHandler();
            if (handler.isClassBased()) {
                this.handledBy(handler.getHandlerClass(), handlerMethod);
            }
            else {
                this.handledBy(handler.getHandlerInstance(), handlerMethod);
            }
        }
        
        public Builder httpMethod(final String name) {
            this.httpMethod = name;
            return this;
        }
        
        public Builder produces(final String... types) {
            return this.produces(MediaTypes.createFrom(types));
        }
        
        public Builder produces(final MediaType... types) {
            return this.produces(Arrays.asList(types));
        }
        
        public Builder produces(final Collection<MediaType> types) {
            this.producedTypes.addAll(types);
            return this;
        }
        
        public Builder consumes(final String... types) {
            return this.consumes(MediaTypes.createFrom(types));
        }
        
        public Builder consumes(final MediaType... types) {
            return this.consumes(Arrays.asList(types));
        }
        
        public Builder consumes(final Collection<MediaType> types) {
            this.consumedTypes.addAll(types);
            return this;
        }
        
        public Builder nameBindings(final Collection<Class<? extends Annotation>> nameBindings) {
            for (final Class<? extends Annotation> nameBinding : nameBindings) {
                if (nameBinding.getAnnotation(NameBinding.class) != null) {
                    this.nameBindings.add(nameBinding);
                }
            }
            return this;
        }
        
        @SafeVarargs
        public final Builder nameBindings(final Class<? extends Annotation>... nameBindings) {
            return this.nameBindings(Arrays.asList(nameBindings));
        }
        
        public Builder nameBindings(final Annotation... nameBindings) {
            return this.nameBindings((Collection<Class<? extends Annotation>>)Arrays.stream(nameBindings).map((Function<? super Annotation, ?>)Annotation::annotationType).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        }
        
        public Builder suspended(final long timeout, final TimeUnit unit) {
            this.suspended = true;
            this.suspendTimeout = timeout;
            this.suspendTimeoutUnit = unit;
            return this;
        }
        
        public Builder sse() {
            this.sse = true;
            return this;
        }
        
        public Builder managedAsync() {
            this.managedAsync = true;
            return this;
        }
        
        public Builder encodedParameters(final boolean value) {
            this.encodedParams = value;
            return this;
        }
        
        public Builder handledBy(final Class<?> handlerClass, final Method method) {
            this.handlerInstance = null;
            this.handlerClass = handlerClass;
            this.definitionMethod = method;
            return this;
        }
        
        public Builder handledBy(final Object handlerInstance, final Method method) {
            this.handlerClass = null;
            this.handlerInstance = handlerInstance;
            this.definitionMethod = method;
            return this;
        }
        
        public Builder handledBy(final Inflector<ContainerRequestContext, ?> inflector) {
            return this.handledBy(inflector, Invocable.APPLY_INFLECTOR_METHOD);
        }
        
        public Builder handledBy(final Class<? extends Inflector> inflectorClass) {
            return this.handledBy(inflectorClass, Invocable.APPLY_INFLECTOR_METHOD);
        }
        
        public Builder handlerParameters(final Collection<Parameter> parameters) {
            this.handlerParameters.addAll(parameters);
            return this;
        }
        
        public Builder handlingMethod(final Method handlingMethod) {
            this.handlingMethod = handlingMethod;
            return this;
        }
        
        public Builder routingResponseType(final Type routingResponseType) {
            this.routingResponseType = routingResponseType;
            return this;
        }
        
        public Builder extended(final boolean extended) {
            this.extended = extended;
            return this;
        }
        
        public ResourceMethod build() {
            final Data methodData = new Data(this.httpMethod, (Collection)this.consumedTypes, (Collection)this.producedTypes, this.managedAsync, this.suspended, this.sse, this.suspendTimeout, this.suspendTimeoutUnit, this.createInvocable(), (Collection)this.nameBindings, this.parent.isExtended() || this.extended);
            this.parent.onBuildMethod(this, methodData);
            return new ResourceMethod(null, methodData);
        }
        
        private Invocable createInvocable() {
            assert this.handlerInstance != null;
            MethodHandler handler;
            if (this.handlerClass != null) {
                handler = MethodHandler.create(this.handlerClass, this.encodedParams, this.handlerParameters);
            }
            else {
                handler = MethodHandler.create(this.handlerInstance, this.handlerParameters);
            }
            return Invocable.create(handler, this.definitionMethod, this.handlingMethod, this.encodedParams, this.routingResponseType);
        }
    }
    
    static class Data
    {
        private final JaxrsType type;
        private final String httpMethod;
        private final List<MediaType> consumedTypes;
        private final List<MediaType> producedTypes;
        private final boolean managedAsync;
        private final boolean suspended;
        private final boolean sse;
        private final long suspendTimeout;
        private final TimeUnit suspendTimeoutUnit;
        private final Invocable invocable;
        private final Collection<Class<? extends Annotation>> nameBindings;
        private final boolean extended;
        
        private Data(final String httpMethod, final Collection<MediaType> consumedTypes, final Collection<MediaType> producedTypes, final boolean managedAsync, final boolean suspended, final boolean sse, final long suspendTimeout, final TimeUnit suspendTimeoutUnit, final Invocable invocable, final Collection<Class<? extends Annotation>> nameBindings, final boolean extended) {
            this.managedAsync = managedAsync;
            this.type = classify(httpMethod);
            this.httpMethod = ((httpMethod == null) ? httpMethod : httpMethod.toUpperCase());
            this.consumedTypes = Collections.unmodifiableList((List<? extends MediaType>)new ArrayList<MediaType>(consumedTypes));
            this.producedTypes = Collections.unmodifiableList((List<? extends MediaType>)new ArrayList<MediaType>(producedTypes));
            this.invocable = invocable;
            this.suspended = suspended;
            this.sse = sse;
            this.suspendTimeout = suspendTimeout;
            this.suspendTimeoutUnit = suspendTimeoutUnit;
            this.nameBindings = Collections.unmodifiableCollection((Collection<? extends Class<? extends Annotation>>)new ArrayList<Class<? extends Annotation>>(nameBindings));
            this.extended = extended;
        }
        
        JaxrsType getType() {
            return this.type;
        }
        
        String getHttpMethod() {
            return this.httpMethod;
        }
        
        List<MediaType> getConsumedTypes() {
            return this.consumedTypes;
        }
        
        List<MediaType> getProducedTypes() {
            return this.producedTypes;
        }
        
        boolean isManagedAsync() {
            return this.managedAsync;
        }
        
        boolean isSuspended() {
            return this.suspended;
        }
        
        boolean isSse() {
            return this.sse;
        }
        
        long getSuspendTimeout() {
            return this.suspendTimeout;
        }
        
        TimeUnit getSuspendTimeoutUnit() {
            return this.suspendTimeoutUnit;
        }
        
        Invocable getInvocable() {
            return this.invocable;
        }
        
        boolean isExtended() {
            return this.extended;
        }
        
        Collection<Class<? extends Annotation>> getNameBindings() {
            return this.nameBindings;
        }
        
        @Override
        public String toString() {
            return "httpMethod=" + this.httpMethod + ", consumedTypes=" + this.consumedTypes + ", producedTypes=" + this.producedTypes + ", suspended=" + this.suspended + ", suspendTimeout=" + this.suspendTimeout + ", suspendTimeoutUnit=" + this.suspendTimeoutUnit + ", invocable=" + this.invocable + ", nameBindings=" + this.nameBindings;
        }
    }
}
