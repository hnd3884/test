package org.owasp.esapi.reference.accesscontrol;

import java.util.Iterator;
import org.apache.commons.collections.iterators.ArrayListIterator;
import java.util.Vector;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;

public class DelegatingACR extends BaseACR<DynaBeanACRParameter, Object[]>
{
    protected Method delegateMethod;
    protected Object delegateInstance;
    
    @Override
    public void setPolicyParameters(final DynaBeanACRParameter policyParameter) {
        final String delegateClassName = policyParameter.getString("delegateClass", "").trim();
        final String methodName = policyParameter.getString("delegateMethod", "").trim();
        final String[] parameterClassNames = policyParameter.getStringArray("parameterClasses");
        final Class delegateClass = this.getClass(delegateClassName, "delegate");
        final Class[] parameterClasses = this.getParameters(parameterClassNames);
        try {
            this.delegateMethod = delegateClass.getMethod(methodName, (Class[])parameterClasses);
        }
        catch (final SecurityException e) {
            throw new IllegalArgumentException(e.getMessage() + " delegateClass.delegateMethod(parameterClasses): \"" + delegateClassName + "." + methodName + "(" + parameterClassNames + ")\" must be public.", e);
        }
        catch (final NoSuchMethodException e2) {
            throw new IllegalArgumentException(e2.getMessage() + " delegateClass.delegateMethod(parameterClasses): \"" + delegateClassName + "." + methodName + "(" + parameterClassNames + ")\" does not exist.", e2);
        }
        if (!Modifier.isStatic(this.delegateMethod.getModifiers())) {
            try {
                this.delegateInstance = delegateClass.newInstance();
                return;
            }
            catch (final InstantiationException ex) {
                throw new IllegalArgumentException(" Delegate class \"" + delegateClassName + "\" must be concrete, because method " + delegateClassName + "." + methodName + "(" + parameterClassNames + ") is not static.", ex);
            }
            catch (final IllegalAccessException ex2) {
                final IllegalArgumentException ex3 = new IllegalArgumentException(" Delegate class \"" + delegateClassName + "\" must must have a zero-argument constructor, because " + "method delegateClass.delegateMethod(parameterClasses): \"" + delegateClassName + "." + methodName + "(" + parameterClassNames + ")\" is not static.", ex2);
                return;
            }
        }
        this.delegateInstance = null;
    }
    
    protected final Class[] getParameters(final String[] parameterClassNames) {
        if (parameterClassNames == null) {
            return new Class[0];
        }
        final Vector<Class> classes = new Vector<Class>();
        final Iterator<String> classNames = (Iterator<String>)new ArrayListIterator((Object)parameterClassNames);
        while (classNames.hasNext()) {
            classes.add(this.getClass(classNames.next(), "parameter"));
        }
        return classes.toArray(new Class[classes.size()]);
    }
    
    protected final Class getClass(final String className, final String purpose) {
        try {
            final Class theClass = Class.forName(className);
            return theClass;
        }
        catch (final ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex.getMessage() + " " + purpose + " Class " + className + " must be in the classpath", ex);
        }
    }
    
    @Override
    public boolean isAuthorized(final Object[] runtimeParameters) throws Exception {
        return (boolean)this.delegateMethod.invoke(this.delegateInstance, runtimeParameters);
    }
}
