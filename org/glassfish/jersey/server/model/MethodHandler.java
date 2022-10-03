package org.glassfish.jersey.server.model;

import org.glassfish.jersey.internal.inject.Injections;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.lang.annotation.Annotation;
import javax.ws.rs.Encoded;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;

public abstract class MethodHandler implements ResourceModelComponent
{
    private final Collection<Parameter> handlerParameters;
    
    protected MethodHandler() {
        this.handlerParameters = (Collection<Parameter>)Collections.emptyList();
    }
    
    protected MethodHandler(final Collection<Parameter> parameters) {
        if (parameters != null) {
            this.handlerParameters = Collections.unmodifiableCollection((Collection<? extends Parameter>)new ArrayList<Parameter>(parameters));
        }
        else {
            this.handlerParameters = (Collection<Parameter>)Collections.emptyList();
        }
    }
    
    public static MethodHandler create(final Class<?> handlerClass) {
        return new ClassBasedMethodHandler(handlerClass, null);
    }
    
    public static MethodHandler create(final Class<?> handlerClass, final boolean keepConstructorParamsEncoded) {
        return new ClassBasedMethodHandler(handlerClass, keepConstructorParamsEncoded, null);
    }
    
    public static MethodHandler create(final Object handlerInstance) {
        return new InstanceBasedMethodHandler(handlerInstance, null);
    }
    
    public static MethodHandler create(final Object handlerInstance, final Class<?> handlerClass) {
        return new InstanceBasedMethodHandler(handlerInstance, handlerClass, null);
    }
    
    public static MethodHandler create(final Class<?> handlerClass, final Collection<Parameter> handlerParameters) {
        return new ClassBasedMethodHandler(handlerClass, handlerParameters);
    }
    
    public static MethodHandler create(final Class<?> handlerClass, final boolean keepConstructorParamsEncoded, final Collection<Parameter> handlerParameters) {
        return new ClassBasedMethodHandler(handlerClass, keepConstructorParamsEncoded, handlerParameters);
    }
    
    public static MethodHandler create(final Object handlerInstance, final Collection<Parameter> handlerParameters) {
        return new InstanceBasedMethodHandler(handlerInstance, handlerParameters);
    }
    
    public static MethodHandler create(final Object handlerInstance, final Class<?> handlerClass, final Collection<Parameter> handlerParameters) {
        return new InstanceBasedMethodHandler(handlerInstance, handlerClass, handlerParameters);
    }
    
    public abstract Class<?> getHandlerClass();
    
    public List<HandlerConstructor> getConstructors() {
        return Collections.emptyList();
    }
    
    public abstract Object getInstance(final InjectionManager p0);
    
    public abstract boolean isClassBased();
    
    public Collection<Parameter> getParameters() {
        return this.handlerParameters;
    }
    
    @Override
    public List<? extends ResourceModelComponent> getComponents() {
        return null;
    }
    
    @Override
    public void accept(final ResourceModelVisitor visitor) {
        visitor.visitMethodHandler(this);
    }
    
    protected abstract Object getHandlerInstance();
    
    private static class ClassBasedMethodHandler extends MethodHandler
    {
        private final Class<?> handlerClass;
        private final List<HandlerConstructor> handlerConstructors;
        
        public ClassBasedMethodHandler(final Class<?> handlerClass, final Collection<Parameter> handlerParameters) {
            this(handlerClass, handlerClass.isAnnotationPresent((Class<? extends Annotation>)Encoded.class), handlerParameters);
        }
        
        public ClassBasedMethodHandler(final Class<?> handlerClass, final boolean disableParamDecoding, final Collection<Parameter> handlerParameters) {
            super(handlerParameters);
            this.handlerClass = handlerClass;
            final List<HandlerConstructor> constructors = new LinkedList<HandlerConstructor>();
            for (final Constructor<?> constructor : handlerClass.getConstructors()) {
                constructors.add(new HandlerConstructor(constructor, Parameter.create(handlerClass, handlerClass, constructor, disableParamDecoding)));
            }
            this.handlerConstructors = Collections.unmodifiableList((List<? extends HandlerConstructor>)constructors);
        }
        
        @Override
        public Class<?> getHandlerClass() {
            return this.handlerClass;
        }
        
        @Override
        public List<HandlerConstructor> getConstructors() {
            return this.handlerConstructors;
        }
        
        @Override
        public Object getInstance(final InjectionManager injectionManager) {
            return Injections.getOrCreate(injectionManager, (Class)this.handlerClass);
        }
        
        @Override
        public boolean isClassBased() {
            return true;
        }
        
        @Override
        protected Object getHandlerInstance() {
            return null;
        }
        
        @Override
        public List<? extends ResourceModelComponent> getComponents() {
            return this.handlerConstructors;
        }
        
        @Override
        public String toString() {
            return "ClassBasedMethodHandler{handlerClass=" + this.handlerClass + ", handlerConstructors=" + this.handlerConstructors + '}';
        }
    }
    
    private static class InstanceBasedMethodHandler extends MethodHandler
    {
        private final Object handler;
        private final Class<?> handlerClass;
        
        public InstanceBasedMethodHandler(final Object handler, final Collection<Parameter> handlerParameters) {
            super(handlerParameters);
            this.handler = handler;
            this.handlerClass = handler.getClass();
        }
        
        public InstanceBasedMethodHandler(final Object handler, final Class<?> handlerClass, final Collection<Parameter> handlerParameters) {
            super(handlerParameters);
            this.handler = handler;
            this.handlerClass = handlerClass;
        }
        
        @Override
        public Class<?> getHandlerClass() {
            return this.handlerClass;
        }
        
        @Override
        protected Object getHandlerInstance() {
            return this.handler;
        }
        
        @Override
        public Object getInstance(final InjectionManager injectionManager) {
            return this.handler;
        }
        
        @Override
        public boolean isClassBased() {
            return false;
        }
        
        @Override
        public String toString() {
            return "InstanceBasedMethodHandler{handler=" + this.handler + ", handlerClass=" + this.handlerClass + '}';
        }
    }
}
