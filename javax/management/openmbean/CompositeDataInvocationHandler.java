package javax.management.openmbean;

import java.lang.reflect.Proxy;
import com.sun.jmx.mbeanserver.MXBeanMappingFactory;
import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import java.lang.reflect.Method;
import com.sun.jmx.mbeanserver.MXBeanLookup;
import java.lang.reflect.InvocationHandler;

public class CompositeDataInvocationHandler implements InvocationHandler
{
    private final CompositeData compositeData;
    private final MXBeanLookup lookup;
    
    public CompositeDataInvocationHandler(final CompositeData compositeData) {
        this(compositeData, null);
    }
    
    CompositeDataInvocationHandler(final CompositeData compositeData, final MXBeanLookup lookup) {
        if (compositeData == null) {
            throw new IllegalArgumentException("compositeData");
        }
        this.compositeData = compositeData;
        this.lookup = lookup;
    }
    
    public CompositeData getCompositeData() {
        assert this.compositeData != null;
        return this.compositeData;
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
        final String name = method.getName();
        if (method.getDeclaringClass() == Object.class) {
            if (name.equals("toString") && array == null) {
                return "Proxy[" + this.compositeData + "]";
            }
            if (name.equals("hashCode") && array == null) {
                return this.compositeData.hashCode() + 1128548680;
            }
            if (name.equals("equals") && array.length == 1 && method.getParameterTypes()[0] == Object.class) {
                return this.equals(o, array[0]);
            }
            return method.invoke(this, array);
        }
        else {
            final String propertyName = DefaultMXBeanMappingFactory.propertyName(method);
            if (propertyName == null) {
                throw new IllegalArgumentException("Method is not getter: " + method.getName());
            }
            Object o2;
            if (this.compositeData.containsKey(propertyName)) {
                o2 = this.compositeData.get(propertyName);
            }
            else {
                final String decapitalize = DefaultMXBeanMappingFactory.decapitalize(propertyName);
                if (!this.compositeData.containsKey(decapitalize)) {
                    throw new IllegalArgumentException("No CompositeData item " + propertyName + (decapitalize.equals(propertyName) ? "" : (" or " + decapitalize)) + " to match " + name);
                }
                o2 = this.compositeData.get(decapitalize);
            }
            return MXBeanMappingFactory.DEFAULT.mappingForType(method.getGenericReturnType(), MXBeanMappingFactory.DEFAULT).fromOpenValue(o2);
        }
    }
    
    private boolean equals(final Object o, final Object o2) {
        if (o2 == null) {
            return false;
        }
        if (o.getClass() != o2.getClass()) {
            return false;
        }
        final InvocationHandler invocationHandler = Proxy.getInvocationHandler(o2);
        return invocationHandler instanceof CompositeDataInvocationHandler && this.compositeData.equals(((CompositeDataInvocationHandler)invocationHandler).compositeData);
    }
}
