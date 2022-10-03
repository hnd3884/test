package org.apache.commons.chain.generic;

import java.lang.reflect.Method;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.CatalogFactory;
import java.util.WeakHashMap;
import org.apache.commons.chain.Filter;

public class DispatchLookupCommand extends LookupCommand implements Filter
{
    private static final Class[] DEFAULT_SIGNATURE;
    private WeakHashMap methods;
    private String method;
    private String methodKey;
    
    public DispatchLookupCommand() {
        this.methods = new WeakHashMap();
        this.method = null;
        this.methodKey = null;
    }
    
    public DispatchLookupCommand(final CatalogFactory factory) {
        super(factory);
        this.methods = new WeakHashMap();
        this.method = null;
        this.methodKey = null;
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
    
    public boolean execute(final Context context) throws Exception {
        if (this.getMethod() == null && this.getMethodKey() == null) {
            throw new IllegalStateException("Neither 'method' nor 'methodKey' properties are defined ");
        }
        final Command command = this.getCommand(context);
        if (command != null) {
            final Method methodObject = this.extractMethod(command, context);
            final Object obj = methodObject.invoke(command, this.getArguments(context));
            final Boolean result = (Boolean)obj;
            return result != null && result;
        }
        return false;
    }
    
    protected Class[] getSignature() {
        return DispatchLookupCommand.DEFAULT_SIGNATURE;
    }
    
    protected Object[] getArguments(final Context context) {
        return new Object[] { context };
    }
    
    private Method extractMethod(final Command command, final Context context) throws NoSuchMethodException {
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
                theMethod = command.getClass().getMethod(methodName, (Class<?>[])this.getSignature());
                this.methods.put(methodName, theMethod);
            }
        }
        return theMethod;
    }
    
    static {
        DEFAULT_SIGNATURE = new Class[] { Context.class };
    }
}
