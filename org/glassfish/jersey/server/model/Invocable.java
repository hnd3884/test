package org.glassfish.jersey.server.model;

import java.util.Arrays;
import java.util.Iterator;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import java.util.Collections;
import javax.ws.rs.core.GenericType;
import java.lang.reflect.ParameterizedType;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.ws.rs.core.Request;
import org.glassfish.jersey.process.Inflector;
import java.lang.reflect.Type;
import java.util.List;
import java.lang.reflect.Method;

public final class Invocable implements Parameterized, ResourceModelComponent
{
    static final Method APPLY_INFLECTOR_METHOD;
    private final MethodHandler handler;
    private final Method definitionMethod;
    private final Method handlingMethod;
    private final List<Parameter> parameters;
    private final Class<?> rawResponseType;
    private final Type responseType;
    private final Type routingResponseType;
    private final Class<?> rawRoutingResponseType;
    
    private static Method initApplyMethod() {
        try {
            return Inflector.class.getMethod("apply", Object.class);
        }
        catch (final NoSuchMethodException e) {
            final IncompatibleClassChangeError error = new IncompatibleClassChangeError("Inflector.apply(Object) method not found");
            error.initCause(e);
            throw error;
        }
    }
    
    public static <T> Invocable create(final Inflector<Request, T> inflector) {
        return create(MethodHandler.create(inflector), Invocable.APPLY_INFLECTOR_METHOD, false);
    }
    
    public static Invocable create(final Class<? extends Inflector> inflectorClass) {
        return create(MethodHandler.create(inflectorClass), Invocable.APPLY_INFLECTOR_METHOD, false);
    }
    
    public static Invocable create(final MethodHandler handler, final Method handlingMethod) {
        return create(handler, handlingMethod, false);
    }
    
    public static Invocable create(final MethodHandler handler, final Method definitionMethod, final boolean encodedParameters) {
        return create(handler, definitionMethod, null, encodedParameters);
    }
    
    public static Invocable create(final MethodHandler handler, final Method definitionMethod, final Method handlingMethod, final boolean encodedParameters) {
        return new Invocable(handler, definitionMethod, handlingMethod, encodedParameters, null);
    }
    
    public static Invocable create(final MethodHandler handler, final Method definitionMethod, final Method handlingMethod, final boolean encodedParameters, final Type routingResponseType) {
        return new Invocable(handler, definitionMethod, handlingMethod, encodedParameters, routingResponseType);
    }
    
    private Invocable(final MethodHandler handler, final Method definitionMethod, final Method handlingMethod, final boolean encodedParameters, final Type routingResponseType) {
        this.handler = handler;
        this.definitionMethod = definitionMethod;
        this.handlingMethod = ((handlingMethod == null) ? ReflectionHelper.findOverridingMethodOnClass((Class)handler.getHandlerClass(), definitionMethod) : handlingMethod);
        final Class<?> handlerClass = handler.getHandlerClass();
        final Class<?> definitionClass = definitionMethod.getDeclaringClass();
        final ClassTypePair handlingCtPair = ReflectionHelper.resolveGenericType((Class)handlerClass, (Class)this.handlingMethod.getDeclaringClass(), (Class)this.handlingMethod.getReturnType(), this.handlingMethod.getGenericReturnType());
        final ClassTypePair definitionCtPair = ReflectionHelper.resolveGenericType((Class)definitionClass, (Class)this.definitionMethod.getDeclaringClass(), (Class)this.definitionMethod.getReturnType(), this.definitionMethod.getGenericReturnType());
        this.rawResponseType = handlingCtPair.rawClass();
        final boolean handlerReturnTypeIsParameterized = handlingCtPair.type() instanceof ParameterizedType;
        final boolean definitionReturnTypeIsParameterized = definitionCtPair.type() instanceof ParameterizedType;
        this.responseType = ((handlingCtPair.rawClass() == definitionCtPair.rawClass() && definitionReturnTypeIsParameterized && !handlerReturnTypeIsParameterized) ? definitionCtPair.type() : handlingCtPair.type());
        if (routingResponseType == null) {
            this.routingResponseType = this.responseType;
            this.rawRoutingResponseType = this.rawResponseType;
        }
        else {
            final GenericType routingResponseGenericType = new GenericType(routingResponseType);
            this.routingResponseType = routingResponseGenericType.getType();
            this.rawRoutingResponseType = routingResponseGenericType.getRawType();
        }
        this.parameters = Collections.unmodifiableList((List<? extends Parameter>)Parameter.create(handlerClass, definitionMethod.getDeclaringClass(), definitionMethod, encodedParameters));
    }
    
    public MethodHandler getHandler() {
        return this.handler;
    }
    
    public Method getHandlingMethod() {
        return this.handlingMethod;
    }
    
    public Method getDefinitionMethod() {
        return this.definitionMethod;
    }
    
    public Type getResponseType() {
        return this.responseType;
    }
    
    public Class<?> getRawResponseType() {
        return this.rawResponseType;
    }
    
    public boolean isInflector() {
        return Invocable.APPLY_INFLECTOR_METHOD == this.definitionMethod || Invocable.APPLY_INFLECTOR_METHOD.equals(this.definitionMethod);
    }
    
    @Override
    public boolean requiresEntity() {
        for (final Parameter p : this.getParameters()) {
            if (Parameter.Source.ENTITY == p.getSource()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }
    
    @Override
    public void accept(final ResourceModelVisitor visitor) {
        visitor.visitInvocable(this);
    }
    
    @Override
    public List<? extends ResourceModelComponent> getComponents() {
        return Arrays.asList(this.handler);
    }
    
    @Override
    public String toString() {
        return "Invocable{handler=" + this.handler + ", definitionMethod=" + this.definitionMethod + ", parameters=" + this.parameters + ", responseType=" + this.responseType + '}';
    }
    
    public Type getRoutingResponseType() {
        return this.routingResponseType;
    }
    
    public Class<?> getRawRoutingResponseType() {
        return this.rawRoutingResponseType;
    }
    
    static {
        APPLY_INFLECTOR_METHOD = initApplyMethod();
    }
}
