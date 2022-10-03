package java.beans;

import java.util.EventListener;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import java.awt.event.ComponentListener;
import java.awt.Component;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.util.Objects;
import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;

public class DefaultPersistenceDelegate extends PersistenceDelegate
{
    private static final String[] EMPTY;
    private final String[] constructor;
    private Boolean definesEquals;
    
    public DefaultPersistenceDelegate() {
        this.constructor = DefaultPersistenceDelegate.EMPTY;
    }
    
    public DefaultPersistenceDelegate(final String[] array) {
        this.constructor = ((array == null) ? DefaultPersistenceDelegate.EMPTY : array.clone());
    }
    
    private static boolean definesEquals(final Class<?> clazz) {
        try {
            return clazz == clazz.getMethod("equals", Object.class).getDeclaringClass();
        }
        catch (final NoSuchMethodException ex) {
            return false;
        }
    }
    
    private boolean definesEquals(final Object o) {
        if (this.definesEquals != null) {
            return this.definesEquals == Boolean.TRUE;
        }
        final boolean definesEquals = definesEquals(o.getClass());
        this.definesEquals = (definesEquals ? Boolean.TRUE : Boolean.FALSE);
        return definesEquals;
    }
    
    @Override
    protected boolean mutatesTo(final Object o, final Object o2) {
        return (this.constructor.length == 0 || !this.definesEquals(o)) ? super.mutatesTo(o, o2) : o.equals(o2);
    }
    
    @Override
    protected Expression instantiate(final Object o, final Encoder encoder) {
        final int length = this.constructor.length;
        final Class<?> class1 = o.getClass();
        final Object[] array = new Object[length];
        for (int i = 0; i < length; ++i) {
            try {
                array[i] = MethodUtil.invoke(this.findMethod(class1, this.constructor[i]), o, new Object[0]);
            }
            catch (final Exception ex) {
                encoder.getExceptionListener().exceptionThrown(ex);
            }
        }
        return new Expression(o, o.getClass(), "new", array);
    }
    
    private Method findMethod(final Class<?> clazz, final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Property name is null");
        }
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(clazz, s);
        if (propertyDescriptor == null) {
            throw new IllegalStateException("Could not find property by the name " + s);
        }
        final Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod == null) {
            throw new IllegalStateException("Could not find getter for the property " + s);
        }
        return readMethod;
    }
    
    private void doProperty(final Class<?> clazz, final PropertyDescriptor propertyDescriptor, final Object o, final Object o2, final Encoder encoder) throws Exception {
        final Method readMethod = propertyDescriptor.getReadMethod();
        final Method writeMethod = propertyDescriptor.getWriteMethod();
        if (readMethod != null && writeMethod != null) {
            final Expression expression = new Expression(o, readMethod.getName(), new Object[0]);
            final Expression expression2 = new Expression(o2, readMethod.getName(), new Object[0]);
            final Object value = expression.getValue();
            final Object value2 = expression2.getValue();
            encoder.writeExpression(expression);
            if (!Objects.equals(value2, encoder.get(value))) {
                final Object[] array = (Object[])propertyDescriptor.getValue("enumerationValues");
                if (array instanceof Object[] && Array.getLength(array) % 3 == 0) {
                    final Object[] array2 = array;
                    for (int i = 0; i < array2.length; i += 3) {
                        try {
                            final Field field = clazz.getField((String)array2[i]);
                            if (field.get(null).equals(value)) {
                                encoder.remove(value);
                                encoder.writeExpression(new Expression(value, field, "get", new Object[] { null }));
                            }
                        }
                        catch (final Exception ex) {}
                    }
                }
                invokeStatement(o, writeMethod.getName(), new Object[] { value }, encoder);
            }
        }
    }
    
    static void invokeStatement(final Object o, final String s, final Object[] array, final Encoder encoder) {
        encoder.writeStatement(new Statement(o, s, array));
    }
    
    private void initBean(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
        for (final Field field : clazz.getFields()) {
            if (ReflectUtil.isPackageAccessible(field.getDeclaringClass())) {
                final int modifiers = field.getModifiers();
                if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                    if (!Modifier.isTransient(modifiers)) {
                        try {
                            final Expression expression = new Expression(field, "get", new Object[] { o });
                            final Expression expression2 = new Expression(field, "get", new Object[] { o2 });
                            final Object value = expression.getValue();
                            final Object value2 = expression2.getValue();
                            encoder.writeExpression(expression);
                            if (!Objects.equals(value2, encoder.get(value))) {
                                encoder.writeStatement(new Statement(field, "set", new Object[] { o, value }));
                            }
                        }
                        catch (final Exception ex) {
                            encoder.getExceptionListener().exceptionThrown(ex);
                        }
                    }
                }
            }
        }
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        }
        catch (final IntrospectionException ex2) {
            return;
        }
        for (final PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            if (!propertyDescriptor.isTransient()) {
                try {
                    this.doProperty(clazz, propertyDescriptor, o, o2, encoder);
                }
                catch (final Exception ex3) {
                    encoder.getExceptionListener().exceptionThrown(ex3);
                }
            }
        }
        if (!Component.class.isAssignableFrom(clazz)) {
            return;
        }
        for (final EventSetDescriptor eventSetDescriptor : beanInfo.getEventSetDescriptors()) {
            if (!eventSetDescriptor.isTransient()) {
                final Class<?> listenerType = eventSetDescriptor.getListenerType();
                if (listenerType != ComponentListener.class) {
                    if (listenerType != ChangeListener.class || clazz != JMenuItem.class) {
                        final EventListener[] array = new EventListener[0];
                        final EventListener[] array2 = new EventListener[0];
                        EventListener[] array3;
                        EventListener[] array4;
                        try {
                            final Method getListenerMethod = eventSetDescriptor.getGetListenerMethod();
                            array3 = (EventListener[])MethodUtil.invoke(getListenerMethod, o, new Object[0]);
                            array4 = (EventListener[])MethodUtil.invoke(getListenerMethod, o2, new Object[0]);
                        }
                        catch (final Exception ex4) {
                            try {
                                final Method method = clazz.getMethod("getListeners", Class.class);
                                array3 = (EventListener[])MethodUtil.invoke(method, o, new Object[] { listenerType });
                                array4 = (EventListener[])MethodUtil.invoke(method, o2, new Object[] { listenerType });
                            }
                            catch (final Exception ex5) {
                                return;
                            }
                        }
                        final String name = eventSetDescriptor.getAddListenerMethod().getName();
                        for (int l = array4.length; l < array3.length; ++l) {
                            invokeStatement(o, name, new Object[] { array3[l] }, encoder);
                        }
                        final String name2 = eventSetDescriptor.getRemoveListenerMethod().getName();
                        for (int length4 = array3.length; length4 < array4.length; ++length4) {
                            invokeStatement(o, name2, new Object[] { array4[length4] }, encoder);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
        super.initialize(clazz, o, o2, encoder);
        if (o.getClass() == clazz) {
            this.initBean(clazz, o, o2, encoder);
        }
    }
    
    private static PropertyDescriptor getPropertyDescriptor(final Class<?> clazz, final String s) {
        try {
            for (final PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
                if (s.equals(propertyDescriptor.getName())) {
                    return propertyDescriptor;
                }
            }
        }
        catch (final IntrospectionException ex) {}
        return null;
    }
    
    static {
        EMPTY = new String[0];
    }
}
