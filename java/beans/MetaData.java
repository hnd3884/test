package java.beans;

import javax.swing.plaf.ColorUIResource;
import javax.swing.border.MatteBorder;
import javax.swing.JMenu;
import javax.swing.Box;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.Choice;
import javax.swing.JLayeredPane;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import java.awt.Container;
import java.awt.Color;
import java.awt.Window;
import java.awt.Component;
import java.awt.MenuShortcut;
import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Modifier;
import java.awt.AWTKeyStroke;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.awt.Font;
import java.awt.Insets;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.EnumMap;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.lang.reflect.Method;
import com.sun.beans.finder.PrimitiveWrapperMap;
import java.lang.reflect.InvocationHandler;
import java.util.Vector;
import java.util.Objects;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.WeakHashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.lang.reflect.Field;
import java.util.Map;

class MetaData
{
    private static final Map<String, Field> fields;
    private static Hashtable<String, PersistenceDelegate> internalPersistenceDelegates;
    private static PersistenceDelegate nullPersistenceDelegate;
    private static PersistenceDelegate enumPersistenceDelegate;
    private static PersistenceDelegate primitivePersistenceDelegate;
    private static PersistenceDelegate defaultPersistenceDelegate;
    private static PersistenceDelegate arrayPersistenceDelegate;
    private static PersistenceDelegate proxyPersistenceDelegate;
    
    public static synchronized PersistenceDelegate getPersistenceDelegate(final Class clazz) {
        if (clazz == null) {
            return MetaData.nullPersistenceDelegate;
        }
        if (Enum.class.isAssignableFrom(clazz)) {
            return MetaData.enumPersistenceDelegate;
        }
        if (null != XMLEncoder.primitiveTypeFor(clazz)) {
            return MetaData.primitivePersistenceDelegate;
        }
        if (clazz.isArray()) {
            if (MetaData.arrayPersistenceDelegate == null) {
                MetaData.arrayPersistenceDelegate = new ArrayPersistenceDelegate();
            }
            return MetaData.arrayPersistenceDelegate;
        }
        try {
            if (Proxy.isProxyClass(clazz)) {
                if (MetaData.proxyPersistenceDelegate == null) {
                    MetaData.proxyPersistenceDelegate = new ProxyPersistenceDelegate();
                }
                return MetaData.proxyPersistenceDelegate;
            }
        }
        catch (final Exception ex) {}
        final String name = clazz.getName();
        PersistenceDelegate persistenceDelegate = (PersistenceDelegate)getBeanAttribute(clazz, "persistenceDelegate");
        if (persistenceDelegate == null) {
            persistenceDelegate = MetaData.internalPersistenceDelegates.get(name);
            if (persistenceDelegate != null) {
                return persistenceDelegate;
            }
            MetaData.internalPersistenceDelegates.put(name, MetaData.defaultPersistenceDelegate);
            try {
                persistenceDelegate = (PersistenceDelegate)Class.forName("java.beans.MetaData$" + clazz.getName().replace('.', '_') + "_PersistenceDelegate").newInstance();
                MetaData.internalPersistenceDelegates.put(name, persistenceDelegate);
            }
            catch (final ClassNotFoundException ex2) {
                final String[] constructorProperties = getConstructorProperties(clazz);
                if (constructorProperties != null) {
                    persistenceDelegate = new DefaultPersistenceDelegate(constructorProperties);
                    MetaData.internalPersistenceDelegates.put(name, persistenceDelegate);
                }
            }
            catch (final Exception ex3) {
                System.err.println("Internal error: " + ex3);
            }
        }
        return (persistenceDelegate != null) ? persistenceDelegate : MetaData.defaultPersistenceDelegate;
    }
    
    private static String[] getConstructorProperties(final Class<?> clazz) {
        String[] array = null;
        int length = 0;
        for (final Constructor constructor : clazz.getConstructors()) {
            final String[] annotationValue = getAnnotationValue(constructor);
            if (annotationValue != null && length < annotationValue.length && isValid(constructor, annotationValue)) {
                array = annotationValue;
                length = annotationValue.length;
            }
        }
        return array;
    }
    
    private static String[] getAnnotationValue(final Constructor<?> constructor) {
        final ConstructorProperties constructorProperties = constructor.getAnnotation(ConstructorProperties.class);
        return (String[])((constructorProperties != null) ? constructorProperties.value() : null);
    }
    
    private static boolean isValid(final Constructor<?> constructor, final String[] array) {
        if (array.length != constructor.getParameterTypes().length) {
            return false;
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == null) {
                return false;
            }
        }
        return true;
    }
    
    private static Object getBeanAttribute(final Class<?> clazz, final String s) {
        try {
            return Introspector.getBeanInfo(clazz).getBeanDescriptor().getValue(s);
        }
        catch (final IntrospectionException ex) {
            return null;
        }
    }
    
    static Object getPrivateFieldValue(final Object o, final String s) {
        Field field = MetaData.fields.get(s);
        if (field == null) {
            final int lastIndex = s.lastIndexOf(46);
            field = AccessController.doPrivileged((PrivilegedAction<Field>)new PrivilegedAction<Field>() {
                final /* synthetic */ String val$className = s.substring(0, lastIndex);
                final /* synthetic */ String val$fieldName = s.substring(1 + lastIndex);
                
                @Override
                public Field run() {
                    try {
                        final Field declaredField = Class.forName(this.val$className).getDeclaredField(this.val$fieldName);
                        declaredField.setAccessible(true);
                        return declaredField;
                    }
                    catch (final ClassNotFoundException ex) {
                        throw new IllegalStateException("Could not find class", ex);
                    }
                    catch (final NoSuchFieldException ex2) {
                        throw new IllegalStateException("Could not find field", ex2);
                    }
                }
            });
            MetaData.fields.put(s, field);
        }
        try {
            return field.get(o);
        }
        catch (final IllegalAccessException ex) {
            throw new IllegalStateException("Could not get value of the field", ex);
        }
    }
    
    static {
        fields = Collections.synchronizedMap(new WeakHashMap<String, Field>());
        MetaData.internalPersistenceDelegates = new Hashtable<String, PersistenceDelegate>();
        MetaData.nullPersistenceDelegate = new NullPersistenceDelegate();
        MetaData.enumPersistenceDelegate = new EnumPersistenceDelegate();
        MetaData.primitivePersistenceDelegate = new PrimitivePersistenceDelegate();
        MetaData.defaultPersistenceDelegate = new DefaultPersistenceDelegate();
        MetaData.internalPersistenceDelegates.put("java.net.URI", new PrimitivePersistenceDelegate());
        MetaData.internalPersistenceDelegates.put("javax.swing.plaf.BorderUIResource$MatteBorderUIResource", new javax_swing_border_MatteBorder_PersistenceDelegate());
        MetaData.internalPersistenceDelegates.put("javax.swing.plaf.FontUIResource", new java_awt_Font_PersistenceDelegate());
        MetaData.internalPersistenceDelegates.put("javax.swing.KeyStroke", new java_awt_AWTKeyStroke_PersistenceDelegate());
        MetaData.internalPersistenceDelegates.put("java.sql.Date", new java_util_Date_PersistenceDelegate());
        MetaData.internalPersistenceDelegates.put("java.sql.Time", new java_util_Date_PersistenceDelegate());
        MetaData.internalPersistenceDelegates.put("java.util.JumboEnumSet", new java_util_EnumSet_PersistenceDelegate());
        MetaData.internalPersistenceDelegates.put("java.util.RegularEnumSet", new java_util_EnumSet_PersistenceDelegate());
    }
    
    static final class NullPersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            return null;
        }
        
        @Override
        public void writeObject(final Object o, final Encoder encoder) {
        }
    }
    
    static final class EnumPersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o == o2;
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Enum enum1 = (Enum)o;
            return new Expression(enum1, Enum.class, "valueOf", new Object[] { enum1.getDeclaringClass(), enum1.name() });
        }
    }
    
    static final class PrimitivePersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            return new Expression(o, o.getClass(), "new", new Object[] { o.toString() });
        }
    }
    
    static final class ArrayPersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o2 != null && o.getClass() == o2.getClass() && Array.getLength(o) == Array.getLength(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            return new Expression(o, Array.class, "newInstance", new Object[] { o.getClass().getComponentType(), new Integer(Array.getLength(o)) });
        }
        
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            for (int length = Array.getLength(o), i = 0; i < length; ++i) {
                final Integer n = new Integer(i);
                final Expression expression = new Expression(o, "get", new Object[] { n });
                final Expression expression2 = new Expression(o2, "get", new Object[] { n });
                try {
                    final Object value = expression.getValue();
                    final Object value2 = expression2.getValue();
                    encoder.writeExpression(expression);
                    if (!Objects.equals(value2, encoder.get(value))) {
                        DefaultPersistenceDelegate.invokeStatement(o, "set", new Object[] { n, value }, encoder);
                    }
                }
                catch (final Exception ex) {
                    encoder.getExceptionListener().exceptionThrown(ex);
                }
            }
        }
    }
    
    static final class ProxyPersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Class<?> class1 = o.getClass();
            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(o);
            if (invocationHandler instanceof EventHandler) {
                final EventHandler eventHandler = (EventHandler)invocationHandler;
                final Vector vector = new Vector();
                vector.add(class1.getInterfaces()[0]);
                vector.add(eventHandler.getTarget());
                vector.add(eventHandler.getAction());
                if (eventHandler.getEventPropertyName() != null) {
                    vector.add(eventHandler.getEventPropertyName());
                }
                if (eventHandler.getListenerMethodName() != null) {
                    vector.setSize(4);
                    vector.add(eventHandler.getListenerMethodName());
                }
                return new Expression(o, EventHandler.class, "create", vector.toArray());
            }
            return new Expression(o, Proxy.class, "newProxyInstance", new Object[] { class1.getClassLoader(), class1.getInterfaces(), invocationHandler });
        }
    }
    
    static final class java_lang_String_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            return null;
        }
        
        @Override
        public void writeObject(final Object o, final Encoder encoder) {
        }
    }
    
    static final class java_lang_Class_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Class clazz = (Class)o;
            if (clazz.isPrimitive()) {
                Object declaredField = null;
                try {
                    declaredField = PrimitiveWrapperMap.getType(clazz.getName()).getDeclaredField("TYPE");
                }
                catch (final NoSuchFieldException ex) {
                    System.err.println("Unknown primitive type: " + clazz);
                }
                return new Expression(o, declaredField, "get", new Object[] { null });
            }
            if (o == String.class) {
                return new Expression(o, "", "getClass", new Object[0]);
            }
            if (o == Class.class) {
                return new Expression(o, String.class, "getClass", new Object[0]);
            }
            final Expression expression = new Expression(o, Class.class, "forName", new Object[] { clazz.getName() });
            expression.loader = clazz.getClassLoader();
            return expression;
        }
    }
    
    static final class java_lang_reflect_Field_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Field field = (Field)o;
            return new Expression(o, field.getDeclaringClass(), "getField", new Object[] { field.getName() });
        }
    }
    
    static final class java_lang_reflect_Method_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Method method = (Method)o;
            return new Expression(o, method.getDeclaringClass(), "getMethod", new Object[] { method.getName(), method.getParameterTypes() });
        }
    }
    
    static class java_util_Date_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return super.mutatesTo(o, o2) && ((Date)o).getTime() == ((Date)o2).getTime();
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Date date = (Date)o;
            return new Expression(date, date.getClass(), "new", new Object[] { date.getTime() });
        }
    }
    
    static final class java_sql_Timestamp_PersistenceDelegate extends java_util_Date_PersistenceDelegate
    {
        private static final Method getNanosMethod;
        
        private static Method getNanosMethod() {
            try {
                return Class.forName("java.sql.Timestamp", true, null).getMethod("getNanos", (Class<?>[])new Class[0]);
            }
            catch (final ClassNotFoundException ex) {
                return null;
            }
            catch (final NoSuchMethodException ex2) {
                throw new AssertionError((Object)ex2);
            }
        }
        
        private static int getNanos(final Object o) {
            if (java_sql_Timestamp_PersistenceDelegate.getNanosMethod == null) {
                throw new AssertionError((Object)"Should not get here");
            }
            try {
                return (int)java_sql_Timestamp_PersistenceDelegate.getNanosMethod.invoke(o, new Object[0]);
            }
            catch (final InvocationTargetException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new AssertionError((Object)ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new AssertionError((Object)ex2);
            }
        }
        
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            final int nanos = getNanos(o);
            if (nanos != getNanos(o2)) {
                encoder.writeStatement(new Statement(o, "setNanos", new Object[] { nanos }));
            }
        }
        
        static {
            getNanosMethod = getNanosMethod();
        }
    }
    
    private abstract static class java_util_Collections extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            if (!super.mutatesTo(o, o2)) {
                return false;
            }
            if (o instanceof List || o instanceof Set || o instanceof Map) {
                return o.equals(o2);
            }
            final Collection collection = (Collection)o;
            final Collection collection2 = (Collection)o2;
            return collection.size() == collection2.size() && collection.containsAll(collection2);
        }
        
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
        }
        
        static final class EmptyList_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "emptyList", null);
            }
        }
        
        static final class EmptySet_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "emptySet", null);
            }
        }
        
        static final class EmptyMap_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "emptyMap", null);
            }
        }
        
        static final class SingletonList_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "singletonList", new Object[] { ((List)o).get(0) });
            }
        }
        
        static final class SingletonSet_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "singleton", new Object[] { ((Set)o).iterator().next() });
            }
        }
        
        static final class SingletonMap_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                final Map map = (Map)o;
                final Object next = map.keySet().iterator().next();
                return new Expression(o, Collections.class, "singletonMap", new Object[] { next, map.get(next) });
            }
        }
        
        static final class UnmodifiableCollection_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "unmodifiableCollection", new Object[] { new ArrayList((Collection<?>)o) });
            }
        }
        
        static final class UnmodifiableList_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "unmodifiableList", new Object[] { new LinkedList((Collection<?>)o) });
            }
        }
        
        static final class UnmodifiableRandomAccessList_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "unmodifiableList", new Object[] { new ArrayList((Collection<?>)o) });
            }
        }
        
        static final class UnmodifiableSet_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "unmodifiableSet", new Object[] { new HashSet((Collection<?>)o) });
            }
        }
        
        static final class UnmodifiableSortedSet_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "unmodifiableSortedSet", new Object[] { new TreeSet((SortedSet<Object>)o) });
            }
        }
        
        static final class UnmodifiableMap_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "unmodifiableMap", new Object[] { new HashMap((Map<?, ?>)o) });
            }
        }
        
        static final class UnmodifiableSortedMap_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "unmodifiableSortedMap", new Object[] { new TreeMap((SortedMap<Object, ?>)o) });
            }
        }
        
        static final class SynchronizedCollection_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "synchronizedCollection", new Object[] { new ArrayList((Collection<?>)o) });
            }
        }
        
        static final class SynchronizedList_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "synchronizedList", new Object[] { new LinkedList((Collection<?>)o) });
            }
        }
        
        static final class SynchronizedRandomAccessList_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "synchronizedList", new Object[] { new ArrayList((Collection<?>)o) });
            }
        }
        
        static final class SynchronizedSet_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "synchronizedSet", new Object[] { new HashSet((Collection<?>)o) });
            }
        }
        
        static final class SynchronizedSortedSet_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "synchronizedSortedSet", new Object[] { new TreeSet((SortedSet<Object>)o) });
            }
        }
        
        static final class SynchronizedMap_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "synchronizedMap", new Object[] { new HashMap((Map<?, ?>)o) });
            }
        }
        
        static final class SynchronizedSortedMap_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "synchronizedSortedMap", new Object[] { new TreeMap((SortedMap<Object, ?>)o) });
            }
        }
        
        static final class CheckedCollection_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "checkedCollection", new Object[] { new ArrayList((Collection<?>)o), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedCollection.type") });
            }
        }
        
        static final class CheckedList_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "checkedList", new Object[] { new LinkedList((Collection<?>)o), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedCollection.type") });
            }
        }
        
        static final class CheckedRandomAccessList_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "checkedList", new Object[] { new ArrayList((Collection<?>)o), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedCollection.type") });
            }
        }
        
        static final class CheckedSet_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "checkedSet", new Object[] { new HashSet((Collection<?>)o), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedCollection.type") });
            }
        }
        
        static final class CheckedSortedSet_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "checkedSortedSet", new Object[] { new TreeSet((SortedSet<Object>)o), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedCollection.type") });
            }
        }
        
        static final class CheckedMap_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "checkedMap", new Object[] { new HashMap((Map<?, ?>)o), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedMap.keyType"), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedMap.valueType") });
            }
        }
        
        static final class CheckedSortedMap_PersistenceDelegate extends java_util_Collections
        {
            @Override
            protected Expression instantiate(final Object o, final Encoder encoder) {
                return new Expression(o, Collections.class, "checkedSortedMap", new Object[] { new TreeMap((SortedMap<Object, ?>)o), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedMap.keyType"), MetaData.getPrivateFieldValue(o, "java.util.Collections$CheckedMap.valueType") });
            }
        }
    }
    
    static final class java_util_EnumMap_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return super.mutatesTo(o, o2) && getType(o) == getType(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            return new Expression(o, EnumMap.class, "new", new Object[] { getType(o) });
        }
        
        private static Object getType(final Object o) {
            return MetaData.getPrivateFieldValue(o, "java.util.EnumMap.keyType");
        }
    }
    
    static final class java_util_EnumSet_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return super.mutatesTo(o, o2) && getType(o) == getType(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            return new Expression(o, EnumSet.class, "noneOf", new Object[] { getType(o) });
        }
        
        private static Object getType(final Object o) {
            return MetaData.getPrivateFieldValue(o, "java.util.EnumSet.elementType");
        }
    }
    
    static class java_util_Collection_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            final Collection collection = (Collection)o;
            if (((Collection)o2).size() != 0) {
                DefaultPersistenceDelegate.invokeStatement(o, "clear", new Object[0], encoder);
            }
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { iterator.next() }, encoder);
            }
        }
    }
    
    static class java_util_List_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            final List list = (List)o;
            final List list2 = (List)o2;
            final int size = list.size();
            int n = (list2 == null) ? 0 : list2.size();
            if (size < n) {
                DefaultPersistenceDelegate.invokeStatement(o, "clear", new Object[0], encoder);
                n = 0;
            }
            for (int i = 0; i < n; ++i) {
                final Integer n2 = new Integer(i);
                final Expression expression = new Expression(o, "get", new Object[] { n2 });
                final Expression expression2 = new Expression(o2, "get", new Object[] { n2 });
                try {
                    final Object value = expression.getValue();
                    final Object value2 = expression2.getValue();
                    encoder.writeExpression(expression);
                    if (!Objects.equals(value2, encoder.get(value))) {
                        DefaultPersistenceDelegate.invokeStatement(o, "set", new Object[] { n2, value }, encoder);
                    }
                }
                catch (final Exception ex) {
                    encoder.getExceptionListener().exceptionThrown(ex);
                }
            }
            for (int j = n; j < size; ++j) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { list.get(j) }, encoder);
            }
        }
    }
    
    static class java_util_Map_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            final Map map = (Map)o;
            final Map map2 = (Map)o2;
            if (map2 != null) {
                for (final Object o3 : map2.keySet().toArray()) {
                    if (!map.containsKey(o3)) {
                        DefaultPersistenceDelegate.invokeStatement(o, "remove", new Object[] { o3 }, encoder);
                    }
                }
            }
            for (final Object next : map.keySet()) {
                final Expression expression = new Expression(o, "get", new Object[] { next });
                final Expression expression2 = new Expression(o2, "get", new Object[] { next });
                try {
                    final Object value = expression.getValue();
                    final Object value2 = expression2.getValue();
                    encoder.writeExpression(expression);
                    if (!Objects.equals(value2, encoder.get(value))) {
                        DefaultPersistenceDelegate.invokeStatement(o, "put", new Object[] { next, value }, encoder);
                    }
                    else {
                        if (value2 != null || map2.containsKey(next)) {
                            continue;
                        }
                        DefaultPersistenceDelegate.invokeStatement(o, "put", new Object[] { next, value }, encoder);
                    }
                }
                catch (final Exception ex) {
                    encoder.getExceptionListener().exceptionThrown(ex);
                }
            }
        }
    }
    
    static final class java_util_AbstractCollection_PersistenceDelegate extends java_util_Collection_PersistenceDelegate
    {
    }
    
    static final class java_util_AbstractList_PersistenceDelegate extends java_util_List_PersistenceDelegate
    {
    }
    
    static final class java_util_AbstractMap_PersistenceDelegate extends java_util_Map_PersistenceDelegate
    {
    }
    
    static final class java_util_Hashtable_PersistenceDelegate extends java_util_Map_PersistenceDelegate
    {
    }
    
    static final class java_beans_beancontext_BeanContextSupport_PersistenceDelegate extends java_util_Collection_PersistenceDelegate
    {
    }
    
    static final class java_awt_Insets_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Insets insets = (Insets)o;
            return new Expression(insets, insets.getClass(), "new", new Object[] { insets.top, insets.left, insets.bottom, insets.right });
        }
    }
    
    static final class java_awt_Font_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Font font = (Font)o;
            int n = 0;
            Object o2 = null;
            int n2 = 0;
            int intValue = 12;
            final Map<TextAttribute, ?> attributes = font.getAttributes();
            final HashMap hashMap = new HashMap(attributes.size());
            for (final TextAttribute textAttribute : attributes.keySet()) {
                final Object value = attributes.get(textAttribute);
                if (value != null) {
                    hashMap.put((Object)textAttribute, value);
                }
                if (textAttribute == TextAttribute.FAMILY) {
                    if (!(value instanceof String)) {
                        continue;
                    }
                    ++n;
                    o2 = value;
                }
                else if (textAttribute == TextAttribute.WEIGHT) {
                    if (TextAttribute.WEIGHT_REGULAR.equals(value)) {
                        ++n;
                    }
                    else {
                        if (!TextAttribute.WEIGHT_BOLD.equals(value)) {
                            continue;
                        }
                        ++n;
                        n2 |= 0x1;
                    }
                }
                else if (textAttribute == TextAttribute.POSTURE) {
                    if (TextAttribute.POSTURE_REGULAR.equals(value)) {
                        ++n;
                    }
                    else {
                        if (!TextAttribute.POSTURE_OBLIQUE.equals(value)) {
                            continue;
                        }
                        ++n;
                        n2 |= 0x2;
                    }
                }
                else {
                    if (textAttribute != TextAttribute.SIZE || !(value instanceof Number)) {
                        continue;
                    }
                    final Number n3 = (Number)value;
                    intValue = n3.intValue();
                    if (intValue != n3.floatValue()) {
                        continue;
                    }
                    ++n;
                }
            }
            final Class<? extends Font> class1 = font.getClass();
            if (n == hashMap.size()) {
                return new Expression(font, class1, "new", new Object[] { o2, n2, intValue });
            }
            if (class1 == Font.class) {
                return new Expression(font, class1, "getFont", new Object[] { hashMap });
            }
            return new Expression(font, class1, "new", new Object[] { Font.getFont((Map<? extends AttributedCharacterIterator.Attribute, ?>)hashMap) });
        }
    }
    
    static final class java_awt_AWTKeyStroke_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final AWTKeyStroke awtKeyStroke = (AWTKeyStroke)o;
            final char keyChar = awtKeyStroke.getKeyChar();
            final int keyCode = awtKeyStroke.getKeyCode();
            final int modifiers = awtKeyStroke.getModifiers();
            final boolean onKeyRelease = awtKeyStroke.isOnKeyRelease();
            Object[] array = null;
            if (keyChar == '\uffff') {
                array = (onKeyRelease ? new Object[] { keyCode, modifiers, onKeyRelease } : new Object[] { keyCode, modifiers });
            }
            else if (keyCode == 0) {
                if (!onKeyRelease) {
                    array = ((modifiers == 0) ? new Object[] { keyChar } : new Object[] { keyChar, modifiers });
                }
                else if (modifiers == 0) {
                    array = new Object[] { keyChar, onKeyRelease };
                }
            }
            if (array == null) {
                throw new IllegalStateException("Unsupported KeyStroke: " + awtKeyStroke);
            }
            final Class<? extends AWTKeyStroke> class1 = awtKeyStroke.getClass();
            String s = class1.getName();
            final int n = s.lastIndexOf(46) + 1;
            if (n > 0) {
                s = s.substring(n);
            }
            return new Expression(awtKeyStroke, class1, "get" + s, array);
        }
    }
    
    static class StaticFieldsPersistenceDelegate extends PersistenceDelegate
    {
        protected void installFields(final Encoder encoder, final Class<?> clazz) {
            if (Modifier.isPublic(clazz.getModifiers()) && ReflectUtil.isPackageAccessible(clazz)) {
                final Field[] fields = clazz.getFields();
                for (int i = 0; i < fields.length; ++i) {
                    final Field field = fields[i];
                    if (Object.class.isAssignableFrom(field.getType())) {
                        encoder.writeExpression(new Expression(field, "get", new Object[] { null }));
                    }
                }
            }
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            throw new RuntimeException("Unrecognized instance: " + o);
        }
        
        @Override
        public void writeObject(final Object o, final Encoder encoder) {
            if (encoder.getAttribute(this) == null) {
                encoder.setAttribute(this, Boolean.TRUE);
                this.installFields(encoder, o.getClass());
            }
            super.writeObject(o, encoder);
        }
    }
    
    static final class java_awt_SystemColor_PersistenceDelegate extends StaticFieldsPersistenceDelegate
    {
    }
    
    static final class java_awt_font_TextAttribute_PersistenceDelegate extends StaticFieldsPersistenceDelegate
    {
    }
    
    static final class java_awt_MenuShortcut_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final MenuShortcut menuShortcut = (MenuShortcut)o;
            return new Expression(o, menuShortcut.getClass(), "new", new Object[] { new Integer(menuShortcut.getKey()), menuShortcut.usesShiftModifier() });
        }
    }
    
    static final class java_awt_Component_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final Component component = (Component)o;
            final Component component2 = (Component)o2;
            if (!(o instanceof Window)) {
                final Color color = component.isBackgroundSet() ? component.getBackground() : null;
                if (!Objects.equals(color, component2.isBackgroundSet() ? component2.getBackground() : null)) {
                    DefaultPersistenceDelegate.invokeStatement(o, "setBackground", new Object[] { color }, encoder);
                }
                final Color color2 = component.isForegroundSet() ? component.getForeground() : null;
                if (!Objects.equals(color2, component2.isForegroundSet() ? component2.getForeground() : null)) {
                    DefaultPersistenceDelegate.invokeStatement(o, "setForeground", new Object[] { color2 }, encoder);
                }
                final Font font = component.isFontSet() ? component.getFont() : null;
                if (!Objects.equals(font, component2.isFontSet() ? component2.getFont() : null)) {
                    DefaultPersistenceDelegate.invokeStatement(o, "setFont", new Object[] { font }, encoder);
                }
            }
            final Container parent = component.getParent();
            if (parent == null || parent.getLayout() == null) {
                final boolean equals = component.getLocation().equals(component2.getLocation());
                final boolean equals2 = component.getSize().equals(component2.getSize());
                if (!equals && !equals2) {
                    DefaultPersistenceDelegate.invokeStatement(o, "setBounds", new Object[] { component.getBounds() }, encoder);
                }
                else if (!equals) {
                    DefaultPersistenceDelegate.invokeStatement(o, "setLocation", new Object[] { component.getLocation() }, encoder);
                }
                else if (!equals2) {
                    DefaultPersistenceDelegate.invokeStatement(o, "setSize", new Object[] { component.getSize() }, encoder);
                }
            }
        }
    }
    
    static final class java_awt_Container_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            if (o instanceof JScrollPane) {
                return;
            }
            final Container container = (Container)o;
            final Component[] components = container.getComponents();
            final Container container2 = (Container)o2;
            final Component[] array = (container2 == null) ? new Component[0] : container2.getComponents();
            final BorderLayout borderLayout = (container.getLayout() instanceof BorderLayout) ? ((BorderLayout)container.getLayout()) : null;
            final JLayeredPane layeredPane = (o instanceof JLayeredPane) ? ((JLayeredPane)o) : null;
            for (int i = array.length; i < components.length; ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", (borderLayout != null) ? new Object[] { components[i], borderLayout.getConstraints(components[i]) } : ((layeredPane != null) ? new Object[] { components[i], layeredPane.getLayer(components[i]), -1 } : new Object[] { components[i] }), encoder);
            }
        }
    }
    
    static final class java_awt_Choice_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final Choice choice = (Choice)o;
            for (int i = ((Choice)o2).getItemCount(); i < choice.getItemCount(); ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { choice.getItem(i) }, encoder);
            }
        }
    }
    
    static final class java_awt_Menu_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final Menu menu = (Menu)o;
            for (int i = ((Menu)o2).getItemCount(); i < menu.getItemCount(); ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { menu.getItem(i) }, encoder);
            }
        }
    }
    
    static final class java_awt_MenuBar_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final MenuBar menuBar = (MenuBar)o;
            for (int i = ((MenuBar)o2).getMenuCount(); i < menuBar.getMenuCount(); ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { menuBar.getMenu(i) }, encoder);
            }
        }
    }
    
    static final class java_awt_List_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final java.awt.List list = (java.awt.List)o;
            for (int i = ((java.awt.List)o2).getItemCount(); i < list.getItemCount(); ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { list.getItem(i) }, encoder);
            }
        }
    }
    
    static final class java_awt_BorderLayout_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        private static final String[] CONSTRAINTS;
        
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final BorderLayout borderLayout = (BorderLayout)o;
            final BorderLayout borderLayout2 = (BorderLayout)o2;
            for (final String s : java_awt_BorderLayout_PersistenceDelegate.CONSTRAINTS) {
                final Component layoutComponent = borderLayout.getLayoutComponent(s);
                final Component layoutComponent2 = borderLayout2.getLayoutComponent(s);
                if (layoutComponent != null && layoutComponent2 == null) {
                    DefaultPersistenceDelegate.invokeStatement(o, "addLayoutComponent", new Object[] { layoutComponent, s }, encoder);
                }
            }
        }
        
        static {
            CONSTRAINTS = new String[] { "North", "South", "East", "West", "Center", "First", "Last", "Before", "After" };
        }
    }
    
    static final class java_awt_CardLayout_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            if (getVector(o2).isEmpty()) {
                for (final Object next : getVector(o)) {
                    DefaultPersistenceDelegate.invokeStatement(o, "addLayoutComponent", new Object[] { MetaData.getPrivateFieldValue(next, "java.awt.CardLayout$Card.name"), MetaData.getPrivateFieldValue(next, "java.awt.CardLayout$Card.comp") }, encoder);
                }
            }
        }
        
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return super.mutatesTo(o, o2) && getVector(o2).isEmpty();
        }
        
        private static Vector<?> getVector(final Object o) {
            return (Vector)MetaData.getPrivateFieldValue(o, "java.awt.CardLayout.vector");
        }
    }
    
    static final class java_awt_GridBagLayout_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            if (getHashtable(o2).isEmpty()) {
                for (final Map.Entry entry : getHashtable(o).entrySet()) {
                    DefaultPersistenceDelegate.invokeStatement(o, "addLayoutComponent", new Object[] { entry.getKey(), entry.getValue() }, encoder);
                }
            }
        }
        
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return super.mutatesTo(o, o2) && getHashtable(o2).isEmpty();
        }
        
        private static Hashtable<?, ?> getHashtable(final Object o) {
            return (Hashtable)MetaData.getPrivateFieldValue(o, "java.awt.GridBagLayout.comptable");
        }
    }
    
    static final class javax_swing_JFrame_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final Window window = (Window)o;
            final Window window2 = (Window)o2;
            final boolean visible = window.isVisible();
            if (window2.isVisible() != visible) {
                final boolean executeStatements = encoder.executeStatements;
                encoder.executeStatements = false;
                DefaultPersistenceDelegate.invokeStatement(o, "setVisible", new Object[] { visible }, encoder);
                encoder.executeStatements = executeStatements;
            }
        }
    }
    
    static final class javax_swing_DefaultListModel_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final DefaultListModel defaultListModel = (DefaultListModel)o;
            for (int i = ((DefaultListModel)o2).getSize(); i < defaultListModel.getSize(); ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { defaultListModel.getElementAt(i) }, encoder);
            }
        }
    }
    
    static final class javax_swing_DefaultComboBoxModel_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel)o;
            for (int i = 0; i < defaultComboBoxModel.getSize(); ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "addElement", new Object[] { defaultComboBoxModel.getElementAt(i) }, encoder);
            }
        }
    }
    
    static final class javax_swing_tree_DefaultMutableTreeNode_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)o;
            for (int i = ((DefaultMutableTreeNode)o2).getChildCount(); i < defaultMutableTreeNode.getChildCount(); ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { defaultMutableTreeNode.getChildAt(i) }, encoder);
            }
        }
    }
    
    static final class javax_swing_ToolTipManager_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            return new Expression(o, ToolTipManager.class, "sharedInstance", new Object[0]);
        }
    }
    
    static final class javax_swing_JTabbedPane_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final JTabbedPane tabbedPane = (JTabbedPane)o;
            for (int i = 0; i < tabbedPane.getTabCount(); ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "addTab", new Object[] { tabbedPane.getTitleAt(i), tabbedPane.getIconAt(i), tabbedPane.getComponentAt(i) }, encoder);
            }
        }
    }
    
    static final class javax_swing_Box_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return super.mutatesTo(o, o2) && this.getAxis(o).equals(this.getAxis(o2));
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            return new Expression(o, o.getClass(), "new", new Object[] { this.getAxis(o) });
        }
        
        private Integer getAxis(final Object o) {
            return (Integer)MetaData.getPrivateFieldValue(((Box)o).getLayout(), "javax.swing.BoxLayout.axis");
        }
    }
    
    static final class javax_swing_JMenu_PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
            super.initialize(clazz, o, o2, encoder);
            final Component[] menuComponents = ((JMenu)o).getMenuComponents();
            for (int i = 0; i < menuComponents.length; ++i) {
                DefaultPersistenceDelegate.invokeStatement(o, "add", new Object[] { menuComponents[i] }, encoder);
            }
        }
    }
    
    static final class javax_swing_border_MatteBorder_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final MatteBorder matteBorder = (MatteBorder)o;
            final Insets borderInsets = matteBorder.getBorderInsets();
            Object o2 = matteBorder.getTileIcon();
            if (o2 == null) {
                o2 = matteBorder.getMatteColor();
            }
            return new Expression(matteBorder, matteBorder.getClass(), "new", new Object[] { borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right, o2 });
        }
    }
    
    static final class sun_swing_PrintColorUIResource_PersistenceDelegate extends PersistenceDelegate
    {
        @Override
        protected boolean mutatesTo(final Object o, final Object o2) {
            return o.equals(o2);
        }
        
        @Override
        protected Expression instantiate(final Object o, final Encoder encoder) {
            final Color color = (Color)o;
            return new Expression(color, ColorUIResource.class, "new", new Object[] { color.getRGB() });
        }
    }
}
