package org.glassfish.jersey.servlet.internal;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.lang.reflect.Method;

public class ThreadLocalNamedInvoker<T> extends ThreadLocalInvoker<T>
{
    private final String name;
    
    public ThreadLocalNamedInvoker(final String name) {
        this.name = name;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (this.get() == null) {
            final Context ctx = new InitialContext();
            final T t = (T)ctx.lookup(this.name);
            this.set(t);
        }
        return super.invoke(proxy, method, args);
    }
}
