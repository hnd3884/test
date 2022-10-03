package org.jvnet.hk2.internal;

import java.lang.reflect.AccessibleObject;
import org.glassfish.hk2.api.HK2Invocation;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.aopalliance.intercept.ConstructorInvocation;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.RandomAccess;
import org.aopalliance.intercept.ConstructorInterceptor;
import java.util.List;
import java.lang.reflect.Constructor;

public class ConstructorInterceptorHandler
{
    private static final ConstructorAction DEFAULT_ACTION;
    
    public static Object construct(final Constructor<?> c, final Object[] args, final boolean neutralCCL, List<ConstructorInterceptor> interceptors, final ConstructorAction action) throws Throwable {
        if (interceptors == null || interceptors.isEmpty()) {
            return action.makeMe(c, args, neutralCCL);
        }
        if (!(interceptors instanceof RandomAccess)) {
            interceptors = new ArrayList<ConstructorInterceptor>(interceptors);
        }
        final ConstructorInterceptor firstInterceptor = interceptors.get(0);
        final Object retVal = firstInterceptor.construct((ConstructorInvocation)new ConstructorInvocationImpl((Constructor)c, args, neutralCCL, action, 0, (List)interceptors, (HashMap)null));
        if (retVal == null) {
            throw new AssertionError((Object)("ConstructorInterceptor construct method returned null for " + c));
        }
        return retVal;
    }
    
    public static Object construct(final Constructor<?> c, final Object[] args, final boolean neutralCCL, final List<ConstructorInterceptor> interceptors) throws Throwable {
        return construct(c, args, neutralCCL, interceptors, ConstructorInterceptorHandler.DEFAULT_ACTION);
    }
    
    static {
        DEFAULT_ACTION = new ConstructorAction() {
            @Override
            public Object makeMe(final Constructor<?> c, final Object[] args, final boolean neutralCCL) throws Throwable {
                return ReflectionHelper.makeMe((Constructor)c, args, neutralCCL);
            }
        };
    }
    
    private static class ConstructorInvocationImpl implements ConstructorInvocation, HK2Invocation
    {
        private final Constructor<?> c;
        private final Object[] args;
        private final boolean neutralCCL;
        private Object myThis;
        private final int index;
        private final ConstructorAction finalAction;
        private final List<ConstructorInterceptor> interceptors;
        private HashMap<String, Object> userData;
        
        private ConstructorInvocationImpl(final Constructor<?> c, final Object[] args, final boolean neutralCCL, final ConstructorAction finalAction, final int index, final List<ConstructorInterceptor> interceptors, final HashMap<String, Object> userData) {
            this.myThis = null;
            this.c = c;
            this.args = args;
            this.neutralCCL = neutralCCL;
            this.finalAction = finalAction;
            this.index = index;
            this.interceptors = interceptors;
            this.userData = userData;
        }
        
        public Object[] getArguments() {
            return this.args;
        }
        
        public AccessibleObject getStaticPart() {
            return this.c;
        }
        
        public Object getThis() {
            return this.myThis;
        }
        
        public Object proceed() throws Throwable {
            final int newIndex = this.index + 1;
            if (newIndex >= this.interceptors.size()) {
                return this.myThis = this.finalAction.makeMe(this.c, this.args, this.neutralCCL);
            }
            final ConstructorInterceptor nextInterceptor = this.interceptors.get(newIndex);
            return this.myThis = nextInterceptor.construct((ConstructorInvocation)new ConstructorInvocationImpl(this.c, this.args, this.neutralCCL, this.finalAction, newIndex, this.interceptors, this.userData));
        }
        
        public Constructor getConstructor() {
            return this.c;
        }
        
        public void setUserData(final String key, final Object data) {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (this.userData == null) {
                this.userData = new HashMap<String, Object>();
            }
            if (data == null) {
                this.userData.remove(key);
            }
            else {
                this.userData.put(key, data);
            }
        }
        
        public Object getUserData(final String key) {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (this.userData == null) {
                return null;
            }
            return this.userData.get(key);
        }
    }
}
