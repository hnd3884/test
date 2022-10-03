package org.apache.jasper.el;

import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.ELContext;
import java.util.Iterator;
import javax.servlet.jsp.el.ScopedAttributeELResolver;
import javax.el.BeanELResolver;
import javax.el.ArrayELResolver;
import javax.el.ListELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.MapELResolver;
import javax.el.StaticFieldELResolver;
import javax.servlet.jsp.el.ImplicitObjectELResolver;
import java.util.List;
import javax.el.ELResolver;
import java.util.concurrent.atomic.AtomicInteger;
import javax.el.CompositeELResolver;

public class JasperELResolver extends CompositeELResolver
{
    private static final int STANDARD_RESOLVERS_COUNT = 9;
    private AtomicInteger resolversSize;
    private volatile ELResolver[] resolvers;
    private final int appResolversSize;
    
    public JasperELResolver(final List<ELResolver> appResolvers, final ELResolver streamResolver) {
        this.resolversSize = new AtomicInteger(0);
        this.appResolversSize = appResolvers.size();
        this.resolvers = new ELResolver[this.appResolversSize + 9];
        this.add((ELResolver)new ImplicitObjectELResolver());
        for (final ELResolver appResolver : appResolvers) {
            this.add(appResolver);
        }
        this.add(streamResolver);
        this.add((ELResolver)new StaticFieldELResolver());
        this.add((ELResolver)new MapELResolver());
        this.add((ELResolver)new ResourceBundleELResolver());
        this.add((ELResolver)new ListELResolver());
        this.add((ELResolver)new ArrayELResolver());
        this.add((ELResolver)new BeanELResolver());
        this.add((ELResolver)new ScopedAttributeELResolver());
    }
    
    public synchronized void add(final ELResolver elResolver) {
        super.add(elResolver);
        final int size = this.resolversSize.get();
        if (this.resolvers.length > size) {
            this.resolvers[size] = elResolver;
        }
        else {
            final ELResolver[] nr = new ELResolver[size + 1];
            System.arraycopy(this.resolvers, 0, nr, 0, size);
            nr[size] = elResolver;
            this.resolvers = nr;
        }
        this.resolversSize.incrementAndGet();
    }
    
    public Object getValue(final ELContext context, final Object base, final Object property) throws NullPointerException, PropertyNotFoundException, ELException {
        context.setPropertyResolved(false);
        Object result = null;
        int start;
        if (base == null) {
            final int index = 1 + this.appResolversSize;
            for (int i = 0; i < index; ++i) {
                result = this.resolvers[i].getValue(context, base, property);
                if (context.isPropertyResolved()) {
                    return result;
                }
            }
            start = index + 7;
        }
        else {
            start = 1;
        }
        for (int size = this.resolversSize.get(), i = start; i < size; ++i) {
            result = this.resolvers[i].getValue(context, base, property);
            if (context.isPropertyResolved()) {
                return result;
            }
        }
        return null;
    }
    
    public Object invoke(final ELContext context, final Object base, final Object method, final Class<?>[] paramTypes, final Object[] params) {
        final String targetMethod = coerceToString(method);
        if (targetMethod.length() == 0) {
            throw new ELException((Throwable)new NoSuchMethodException());
        }
        context.setPropertyResolved(false);
        Object result = null;
        int index = 1 + this.appResolversSize + 2;
        for (int i = 1; i < index; ++i) {
            result = this.resolvers[i].invoke(context, base, (Object)targetMethod, (Class[])paramTypes, params);
            if (context.isPropertyResolved()) {
                return result;
            }
        }
        index += 4;
        for (int size = this.resolversSize.get(), j = index; j < size; ++j) {
            result = this.resolvers[j].invoke(context, base, (Object)targetMethod, (Class[])paramTypes, params);
            if (context.isPropertyResolved()) {
                return result;
            }
        }
        return null;
    }
    
    private static final String coerceToString(final Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String)obj;
        }
        if (obj instanceof Enum) {
            return ((Enum)obj).name();
        }
        return obj.toString();
    }
}
