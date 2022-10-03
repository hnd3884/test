package org.apache.commons.chain.generic;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.chain.Context;
import java.util.WeakHashMap;
import java.util.Map;
import org.apache.commons.chain.Command;

public abstract class DispatchCommand implements Command
{
    private Map methods;
    private String method;
    private String methodKey;
    protected static final Class[] DEFAULT_SIGNATURE;
    
    public DispatchCommand() {
        this.methods = new WeakHashMap();
        this.method = null;
        this.methodKey = null;
    }
    
    public boolean execute(final Context context) throws Exception {
        if (this.getMethod() == null && this.getMethodKey() == null) {
            throw new IllegalStateException("Neither 'method' nor 'methodKey' properties are defined ");
        }
        final Method methodObject = this.extractMethod(context);
        try {
            return this.evaluateResult(methodObject.invoke(this, this.getArguments(context)));
        }
        catch (final InvocationTargetException e) {
            final Throwable cause = e.getTargetException();
            if (cause instanceof Exception) {
                throw (Exception)cause;
            }
            throw e;
        }
    }
    
    protected Method extractMethod(final Context context) throws NoSuchMethodException {
        String methodName = this.getMethod();
        if (methodName == null) {
            final Object methodContextObj = context.get(this.getMethodKey());
            if (methodContextObj == null) {
                throw new NullPointerException("No value found in context under " + this.getMethodKey());
            }
            methodName = methodContextObj.toString();
        }
        Method theMethod = null;
        synchronized (this.methods) {
            theMethod = this.methods.get(methodName);
            if (theMethod == null) {
                theMethod = this.getClass().getMethod(methodName, (Class<?>[])this.getSignature());
                this.methods.put(methodName, theMethod);
            }
        }
        return theMethod;
    }
    
    protected boolean evaluateResult(final Object o) {
        final Boolean result = (Boolean)o;
        return result != null && result;
    }
    
    protected Class[] getSignature() {
        return DispatchCommand.DEFAULT_SIGNATURE;
    }
    
    protected Object[] getArguments(final Context context) {
        return new Object[] { context };
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public String getMethodKey() {
        return this.methodKey;
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public void setMethodKey(final String methodKey) {
        this.methodKey = methodKey;
    }
    
    static {
        DEFAULT_SIGNATURE = new Class[] { Context.class };
    }
}
