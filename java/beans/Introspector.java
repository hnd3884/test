package java.beans;

import com.sun.beans.finder.MethodFinder;
import java.util.EventObject;
import java.awt.Component;
import com.sun.beans.finder.ClassFinder;
import java.lang.reflect.Type;
import java.util.TooManyListenersException;
import com.sun.beans.TypeResolver;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.ArrayList;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.EventListener;
import java.lang.reflect.Method;
import com.sun.beans.WeakCache;

public class Introspector
{
    public static final int USE_ALL_BEANINFO = 1;
    public static final int IGNORE_IMMEDIATE_BEANINFO = 2;
    public static final int IGNORE_ALL_BEANINFO = 3;
    private static final WeakCache<Class<?>, Method[]> declaredMethodCache;
    private Class<?> beanClass;
    private BeanInfo explicitBeanInfo;
    private BeanInfo superBeanInfo;
    private BeanInfo[] additionalBeanInfo;
    private boolean propertyChangeSource;
    private static Class<EventListener> eventListenerType;
    private String defaultEventName;
    private String defaultPropertyName;
    private int defaultEventIndex;
    private int defaultPropertyIndex;
    private Map<String, MethodDescriptor> methods;
    private Map<String, PropertyDescriptor> properties;
    private Map<String, EventSetDescriptor> events;
    private static final EventSetDescriptor[] EMPTY_EVENTSETDESCRIPTORS;
    static final String ADD_PREFIX = "add";
    static final String REMOVE_PREFIX = "remove";
    static final String GET_PREFIX = "get";
    static final String SET_PREFIX = "set";
    static final String IS_PREFIX = "is";
    private HashMap<String, List<PropertyDescriptor>> pdStore;
    
    public static BeanInfo getBeanInfo(final Class<?> clazz) throws IntrospectionException {
        if (!ReflectUtil.isPackageAccessible(clazz)) {
            return new Introspector(clazz, null, 1).getBeanInfo();
        }
        final ThreadGroupContext context = ThreadGroupContext.getContext();
        BeanInfo beanInfo;
        synchronized (Introspector.declaredMethodCache) {
            beanInfo = context.getBeanInfo(clazz);
        }
        if (beanInfo == null) {
            beanInfo = new Introspector(clazz, null, 1).getBeanInfo();
            synchronized (Introspector.declaredMethodCache) {
                context.putBeanInfo(clazz, beanInfo);
            }
        }
        return beanInfo;
    }
    
    public static BeanInfo getBeanInfo(final Class<?> clazz, final int n) throws IntrospectionException {
        return getBeanInfo(clazz, null, n);
    }
    
    public static BeanInfo getBeanInfo(final Class<?> clazz, final Class<?> clazz2) throws IntrospectionException {
        return getBeanInfo(clazz, clazz2, 1);
    }
    
    public static BeanInfo getBeanInfo(final Class<?> clazz, final Class<?> clazz2, final int n) throws IntrospectionException {
        BeanInfo beanInfo;
        if (clazz2 == null && n == 1) {
            beanInfo = getBeanInfo(clazz);
        }
        else {
            beanInfo = new Introspector(clazz, clazz2, n).getBeanInfo();
        }
        return beanInfo;
    }
    
    public static String decapitalize(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        if (s.length() > 1 && Character.isUpperCase(s.charAt(1)) && Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        final char[] charArray = s.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return new String(charArray);
    }
    
    public static String[] getBeanInfoSearchPath() {
        return ThreadGroupContext.getContext().getBeanInfoFinder().getPackages();
    }
    
    public static void setBeanInfoSearchPath(final String[] packages) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPropertiesAccess();
        }
        ThreadGroupContext.getContext().getBeanInfoFinder().setPackages(packages);
    }
    
    public static void flushCaches() {
        synchronized (Introspector.declaredMethodCache) {
            ThreadGroupContext.getContext().clearBeanInfoCache();
            Introspector.declaredMethodCache.clear();
        }
    }
    
    public static void flushFromCaches(final Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        synchronized (Introspector.declaredMethodCache) {
            ThreadGroupContext.getContext().removeBeanInfo(clazz);
            Introspector.declaredMethodCache.put(clazz, null);
        }
    }
    
    private Introspector(final Class<?> beanClass, final Class<?> clazz, final int n) throws IntrospectionException {
        this.propertyChangeSource = false;
        this.defaultEventIndex = -1;
        this.defaultPropertyIndex = -1;
        this.pdStore = new HashMap<String, List<PropertyDescriptor>>();
        this.beanClass = beanClass;
        if (clazz != null) {
            boolean b = false;
            for (Class<?> clazz2 = beanClass.getSuperclass(); clazz2 != null; clazz2 = clazz2.getSuperclass()) {
                if (clazz2 == clazz) {
                    b = true;
                }
            }
            if (!b) {
                throw new IntrospectionException(clazz.getName() + " not superclass of " + beanClass.getName());
            }
        }
        if (n == 1) {
            this.explicitBeanInfo = findExplicitBeanInfo(beanClass);
        }
        final Class<?> superclass = beanClass.getSuperclass();
        if (superclass != clazz) {
            int n2 = n;
            if (n2 == 2) {
                n2 = 1;
            }
            this.superBeanInfo = getBeanInfo(superclass, clazz, n2);
        }
        if (this.explicitBeanInfo != null) {
            this.additionalBeanInfo = this.explicitBeanInfo.getAdditionalBeanInfo();
        }
        if (this.additionalBeanInfo == null) {
            this.additionalBeanInfo = new BeanInfo[0];
        }
    }
    
    private BeanInfo getBeanInfo() throws IntrospectionException {
        return new GenericBeanInfo(this.getTargetBeanDescriptor(), this.getTargetEventInfo(), this.getTargetDefaultEventIndex(), this.getTargetPropertyInfo(), this.getTargetDefaultPropertyIndex(), this.getTargetMethodInfo(), this.explicitBeanInfo);
    }
    
    private static BeanInfo findExplicitBeanInfo(final Class<?> clazz) {
        return ThreadGroupContext.getContext().getBeanInfoFinder().find(clazz);
    }
    
    private PropertyDescriptor[] getTargetPropertyInfo() {
        PropertyDescriptor[] propertyDescriptors = null;
        if (this.explicitBeanInfo != null) {
            propertyDescriptors = this.getPropertyDescriptors(this.explicitBeanInfo);
        }
        if (propertyDescriptors == null && this.superBeanInfo != null) {
            this.addPropertyDescriptors(this.getPropertyDescriptors(this.superBeanInfo));
        }
        for (int i = 0; i < this.additionalBeanInfo.length; ++i) {
            this.addPropertyDescriptors(this.additionalBeanInfo[i].getPropertyDescriptors());
        }
        if (propertyDescriptors != null) {
            this.addPropertyDescriptors(propertyDescriptors);
        }
        else {
            final Method[] publicDeclaredMethods = getPublicDeclaredMethods(this.beanClass);
            for (int j = 0; j < publicDeclaredMethods.length; ++j) {
                final Method method = publicDeclaredMethods[j];
                if (method != null) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        final String name = method.getName();
                        final Class<?>[] parameterTypes = method.getParameterTypes();
                        final Class<?> returnType = method.getReturnType();
                        final int length = parameterTypes.length;
                        PropertyDescriptor propertyDescriptor = null;
                        if (name.length() > 3 || name.startsWith("is")) {
                            try {
                                if (length == 0) {
                                    if (name.startsWith("get")) {
                                        propertyDescriptor = new PropertyDescriptor(this.beanClass, name.substring(3), method, null);
                                    }
                                    else if (returnType == Boolean.TYPE && name.startsWith("is")) {
                                        propertyDescriptor = new PropertyDescriptor(this.beanClass, name.substring(2), method, null);
                                    }
                                }
                                else if (length == 1) {
                                    if (Integer.TYPE.equals(parameterTypes[0]) && name.startsWith("get")) {
                                        propertyDescriptor = new IndexedPropertyDescriptor(this.beanClass, name.substring(3), null, null, method, null);
                                    }
                                    else if (Void.TYPE.equals(returnType) && name.startsWith("set")) {
                                        propertyDescriptor = new PropertyDescriptor(this.beanClass, name.substring(3), null, method);
                                        if (this.throwsException(method, PropertyVetoException.class)) {
                                            propertyDescriptor.setConstrained(true);
                                        }
                                    }
                                }
                                else if (length == 2 && Void.TYPE.equals(returnType) && Integer.TYPE.equals(parameterTypes[0]) && name.startsWith("set")) {
                                    propertyDescriptor = new IndexedPropertyDescriptor(this.beanClass, name.substring(3), null, null, null, method);
                                    if (this.throwsException(method, PropertyVetoException.class)) {
                                        propertyDescriptor.setConstrained(true);
                                    }
                                }
                            }
                            catch (final IntrospectionException ex) {
                                propertyDescriptor = null;
                            }
                            if (propertyDescriptor != null) {
                                if (this.propertyChangeSource) {
                                    propertyDescriptor.setBound(true);
                                }
                                this.addPropertyDescriptor(propertyDescriptor);
                            }
                        }
                    }
                }
            }
        }
        this.processPropertyDescriptors();
        final PropertyDescriptor[] array = this.properties.values().toArray(new PropertyDescriptor[this.properties.size()]);
        if (this.defaultPropertyName != null) {
            for (int k = 0; k < array.length; ++k) {
                if (this.defaultPropertyName.equals(array[k].getName())) {
                    this.defaultPropertyIndex = k;
                }
            }
        }
        return array;
    }
    
    private void addPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        final String name = propertyDescriptor.getName();
        List list = this.pdStore.get(name);
        if (list == null) {
            list = new ArrayList();
            this.pdStore.put(name, list);
        }
        if (this.beanClass != propertyDescriptor.getClass0()) {
            final Method readMethod = propertyDescriptor.getReadMethod();
            final Method writeMethod = propertyDescriptor.getWriteMethod();
            boolean b = true;
            if (readMethod != null) {
                b = (b && readMethod.getGenericReturnType() instanceof Class);
            }
            if (writeMethod != null) {
                b = (b && writeMethod.getGenericParameterTypes()[0] instanceof Class);
            }
            if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
                final IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)propertyDescriptor;
                final Method indexedReadMethod = indexedPropertyDescriptor.getIndexedReadMethod();
                final Method indexedWriteMethod = indexedPropertyDescriptor.getIndexedWriteMethod();
                if (indexedReadMethod != null) {
                    b = (b && indexedReadMethod.getGenericReturnType() instanceof Class);
                }
                if (indexedWriteMethod != null) {
                    b = (b && indexedWriteMethod.getGenericParameterTypes()[1] instanceof Class);
                }
                if (!b) {
                    propertyDescriptor = new IndexedPropertyDescriptor(indexedPropertyDescriptor);
                    propertyDescriptor.updateGenericsFor(this.beanClass);
                }
            }
            else if (!b) {
                propertyDescriptor = new PropertyDescriptor(propertyDescriptor);
                propertyDescriptor.updateGenericsFor(this.beanClass);
            }
        }
        list.add(propertyDescriptor);
    }
    
    private void addPropertyDescriptors(final PropertyDescriptor[] array) {
        if (array != null) {
            for (int length = array.length, i = 0; i < length; ++i) {
                this.addPropertyDescriptor(array[i]);
            }
        }
    }
    
    private PropertyDescriptor[] getPropertyDescriptors(final BeanInfo beanInfo) {
        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        final int defaultPropertyIndex = beanInfo.getDefaultPropertyIndex();
        if (0 <= defaultPropertyIndex && defaultPropertyIndex < propertyDescriptors.length) {
            this.defaultPropertyName = propertyDescriptors[defaultPropertyIndex].getName();
        }
        return propertyDescriptors;
    }
    
    private void processPropertyDescriptors() {
        if (this.properties == null) {
            this.properties = new TreeMap<String, PropertyDescriptor>();
        }
        final Iterator<List<PropertyDescriptor>> iterator = this.pdStore.values().iterator();
        while (iterator.hasNext()) {
            PropertyDescriptor mergePropertyWithIndexedProperty = null;
            PropertyDescriptor mergePropertyWithIndexedProperty2 = null;
            IndexedPropertyDescriptor indexedPropertyDescriptor = null;
            IndexedPropertyDescriptor indexedPropertyDescriptor2 = null;
            final List list = iterator.next();
            for (int i = 0; i < list.size(); ++i) {
                final PropertyDescriptor propertyDescriptor = (PropertyDescriptor)list.get(i);
                if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
                    final IndexedPropertyDescriptor indexedPropertyDescriptor3 = (IndexedPropertyDescriptor)propertyDescriptor;
                    if (indexedPropertyDescriptor3.getIndexedReadMethod() != null) {
                        if (indexedPropertyDescriptor != null) {
                            indexedPropertyDescriptor = new IndexedPropertyDescriptor(indexedPropertyDescriptor, indexedPropertyDescriptor3);
                        }
                        else {
                            indexedPropertyDescriptor = indexedPropertyDescriptor3;
                        }
                    }
                }
                else if (propertyDescriptor.getReadMethod() != null) {
                    final String name = propertyDescriptor.getReadMethod().getName();
                    if (mergePropertyWithIndexedProperty != null) {
                        final String name2 = mergePropertyWithIndexedProperty.getReadMethod().getName();
                        if (name2.equals(name) || !name2.startsWith("is")) {
                            mergePropertyWithIndexedProperty = new PropertyDescriptor(mergePropertyWithIndexedProperty, propertyDescriptor);
                        }
                    }
                    else {
                        mergePropertyWithIndexedProperty = propertyDescriptor;
                    }
                }
            }
            for (int j = 0; j < list.size(); ++j) {
                final PropertyDescriptor propertyDescriptor2 = (PropertyDescriptor)list.get(j);
                if (propertyDescriptor2 instanceof IndexedPropertyDescriptor) {
                    final IndexedPropertyDescriptor indexedPropertyDescriptor4 = (IndexedPropertyDescriptor)propertyDescriptor2;
                    if (indexedPropertyDescriptor4.getIndexedWriteMethod() != null) {
                        if (indexedPropertyDescriptor != null) {
                            if (isAssignable(indexedPropertyDescriptor.getIndexedPropertyType(), indexedPropertyDescriptor4.getIndexedPropertyType())) {
                                if (indexedPropertyDescriptor2 != null) {
                                    indexedPropertyDescriptor2 = new IndexedPropertyDescriptor(indexedPropertyDescriptor2, indexedPropertyDescriptor4);
                                }
                                else {
                                    indexedPropertyDescriptor2 = indexedPropertyDescriptor4;
                                }
                            }
                        }
                        else if (indexedPropertyDescriptor2 != null) {
                            indexedPropertyDescriptor2 = new IndexedPropertyDescriptor(indexedPropertyDescriptor2, indexedPropertyDescriptor4);
                        }
                        else {
                            indexedPropertyDescriptor2 = indexedPropertyDescriptor4;
                        }
                    }
                }
                else if (propertyDescriptor2.getWriteMethod() != null) {
                    if (mergePropertyWithIndexedProperty != null) {
                        if (isAssignable(mergePropertyWithIndexedProperty.getPropertyType(), propertyDescriptor2.getPropertyType())) {
                            if (mergePropertyWithIndexedProperty2 != null) {
                                mergePropertyWithIndexedProperty2 = new PropertyDescriptor(mergePropertyWithIndexedProperty2, propertyDescriptor2);
                            }
                            else {
                                mergePropertyWithIndexedProperty2 = propertyDescriptor2;
                            }
                        }
                    }
                    else if (mergePropertyWithIndexedProperty2 != null) {
                        mergePropertyWithIndexedProperty2 = new PropertyDescriptor(mergePropertyWithIndexedProperty2, propertyDescriptor2);
                    }
                    else {
                        mergePropertyWithIndexedProperty2 = propertyDescriptor2;
                    }
                }
            }
            PropertyDescriptor propertyDescriptor3 = null;
            if (indexedPropertyDescriptor != null && indexedPropertyDescriptor2 != null) {
                PropertyDescriptor propertyDescriptor4;
                if (mergePropertyWithIndexedProperty == mergePropertyWithIndexedProperty2 || mergePropertyWithIndexedProperty == null) {
                    propertyDescriptor4 = mergePropertyWithIndexedProperty2;
                }
                else if (mergePropertyWithIndexedProperty2 == null) {
                    propertyDescriptor4 = mergePropertyWithIndexedProperty;
                }
                else if (mergePropertyWithIndexedProperty2 instanceof IndexedPropertyDescriptor) {
                    propertyDescriptor4 = this.mergePropertyWithIndexedProperty(mergePropertyWithIndexedProperty, (IndexedPropertyDescriptor)mergePropertyWithIndexedProperty2);
                }
                else if (mergePropertyWithIndexedProperty instanceof IndexedPropertyDescriptor) {
                    propertyDescriptor4 = this.mergePropertyWithIndexedProperty(mergePropertyWithIndexedProperty2, (IndexedPropertyDescriptor)mergePropertyWithIndexedProperty);
                }
                else {
                    propertyDescriptor4 = this.mergePropertyDescriptor(mergePropertyWithIndexedProperty, mergePropertyWithIndexedProperty2);
                }
                IndexedPropertyDescriptor mergePropertyDescriptor;
                if (indexedPropertyDescriptor == indexedPropertyDescriptor2) {
                    mergePropertyDescriptor = indexedPropertyDescriptor;
                }
                else {
                    mergePropertyDescriptor = this.mergePropertyDescriptor(indexedPropertyDescriptor, indexedPropertyDescriptor2);
                }
                if (propertyDescriptor4 == null) {
                    propertyDescriptor3 = mergePropertyDescriptor;
                }
                else {
                    final Class<?> propertyType = propertyDescriptor4.getPropertyType();
                    final Class<?> indexedPropertyType = mergePropertyDescriptor.getIndexedPropertyType();
                    if (propertyType.isArray() && propertyType.getComponentType() == indexedPropertyType) {
                        propertyDescriptor3 = (propertyDescriptor4.getClass0().isAssignableFrom(mergePropertyDescriptor.getClass0()) ? new IndexedPropertyDescriptor(propertyDescriptor4, mergePropertyDescriptor) : new IndexedPropertyDescriptor(mergePropertyDescriptor, propertyDescriptor4));
                    }
                    else if (propertyDescriptor4.getClass0().isAssignableFrom(mergePropertyDescriptor.getClass0())) {
                        propertyDescriptor3 = (propertyDescriptor4.getClass0().isAssignableFrom(mergePropertyDescriptor.getClass0()) ? new PropertyDescriptor(propertyDescriptor4, mergePropertyDescriptor) : new PropertyDescriptor(mergePropertyDescriptor, propertyDescriptor4));
                    }
                    else {
                        propertyDescriptor3 = mergePropertyDescriptor;
                    }
                }
            }
            else if (mergePropertyWithIndexedProperty != null && mergePropertyWithIndexedProperty2 != null) {
                if (indexedPropertyDescriptor != null) {
                    mergePropertyWithIndexedProperty = this.mergePropertyWithIndexedProperty(mergePropertyWithIndexedProperty, indexedPropertyDescriptor);
                }
                if (indexedPropertyDescriptor2 != null) {
                    mergePropertyWithIndexedProperty2 = this.mergePropertyWithIndexedProperty(mergePropertyWithIndexedProperty2, indexedPropertyDescriptor2);
                }
                if (mergePropertyWithIndexedProperty == mergePropertyWithIndexedProperty2) {
                    propertyDescriptor3 = mergePropertyWithIndexedProperty;
                }
                else if (mergePropertyWithIndexedProperty2 instanceof IndexedPropertyDescriptor) {
                    propertyDescriptor3 = this.mergePropertyWithIndexedProperty(mergePropertyWithIndexedProperty, (IndexedPropertyDescriptor)mergePropertyWithIndexedProperty2);
                }
                else if (mergePropertyWithIndexedProperty instanceof IndexedPropertyDescriptor) {
                    propertyDescriptor3 = this.mergePropertyWithIndexedProperty(mergePropertyWithIndexedProperty2, (IndexedPropertyDescriptor)mergePropertyWithIndexedProperty);
                }
                else {
                    propertyDescriptor3 = this.mergePropertyDescriptor(mergePropertyWithIndexedProperty, mergePropertyWithIndexedProperty2);
                }
            }
            else if (indexedPropertyDescriptor2 != null) {
                propertyDescriptor3 = indexedPropertyDescriptor2;
                if (mergePropertyWithIndexedProperty2 != null) {
                    propertyDescriptor3 = this.mergePropertyDescriptor(indexedPropertyDescriptor2, mergePropertyWithIndexedProperty2);
                }
                if (mergePropertyWithIndexedProperty != null) {
                    propertyDescriptor3 = this.mergePropertyDescriptor(indexedPropertyDescriptor2, mergePropertyWithIndexedProperty);
                }
            }
            else if (indexedPropertyDescriptor != null) {
                propertyDescriptor3 = indexedPropertyDescriptor;
                if (mergePropertyWithIndexedProperty != null) {
                    propertyDescriptor3 = this.mergePropertyDescriptor(indexedPropertyDescriptor, mergePropertyWithIndexedProperty);
                }
                if (mergePropertyWithIndexedProperty2 != null) {
                    propertyDescriptor3 = this.mergePropertyDescriptor(indexedPropertyDescriptor, mergePropertyWithIndexedProperty2);
                }
            }
            else if (mergePropertyWithIndexedProperty2 != null) {
                propertyDescriptor3 = mergePropertyWithIndexedProperty2;
            }
            else if (mergePropertyWithIndexedProperty != null) {
                propertyDescriptor3 = mergePropertyWithIndexedProperty;
            }
            if (propertyDescriptor3 instanceof IndexedPropertyDescriptor) {
                final IndexedPropertyDescriptor indexedPropertyDescriptor5 = (IndexedPropertyDescriptor)propertyDescriptor3;
                if (indexedPropertyDescriptor5.getIndexedReadMethod() == null && indexedPropertyDescriptor5.getIndexedWriteMethod() == null) {
                    propertyDescriptor3 = new PropertyDescriptor(indexedPropertyDescriptor5);
                }
            }
            if (propertyDescriptor3 == null && list.size() > 0) {
                propertyDescriptor3 = (PropertyDescriptor)list.get(0);
            }
            if (propertyDescriptor3 != null) {
                this.properties.put(propertyDescriptor3.getName(), propertyDescriptor3);
            }
        }
    }
    
    private static boolean isAssignable(final Class<?> clazz, final Class<?> clazz2) {
        return (clazz == null || clazz2 == null) ? (clazz == clazz2) : clazz.isAssignableFrom(clazz2);
    }
    
    private PropertyDescriptor mergePropertyWithIndexedProperty(final PropertyDescriptor propertyDescriptor, final IndexedPropertyDescriptor indexedPropertyDescriptor) {
        final Class<?> propertyType = propertyDescriptor.getPropertyType();
        if (propertyType.isArray() && propertyType.getComponentType() == indexedPropertyDescriptor.getIndexedPropertyType()) {
            return propertyDescriptor.getClass0().isAssignableFrom(indexedPropertyDescriptor.getClass0()) ? new IndexedPropertyDescriptor(propertyDescriptor, indexedPropertyDescriptor) : new IndexedPropertyDescriptor(indexedPropertyDescriptor, propertyDescriptor);
        }
        return propertyDescriptor;
    }
    
    private PropertyDescriptor mergePropertyDescriptor(final IndexedPropertyDescriptor indexedPropertyDescriptor, final PropertyDescriptor propertyDescriptor) {
        final Class<?> propertyType = propertyDescriptor.getPropertyType();
        final Class<?> indexedPropertyType = indexedPropertyDescriptor.getIndexedPropertyType();
        PropertyDescriptor propertyDescriptor2;
        if (propertyType.isArray() && propertyType.getComponentType() == indexedPropertyType) {
            if (propertyDescriptor.getClass0().isAssignableFrom(indexedPropertyDescriptor.getClass0())) {
                propertyDescriptor2 = new IndexedPropertyDescriptor(propertyDescriptor, indexedPropertyDescriptor);
            }
            else {
                propertyDescriptor2 = new IndexedPropertyDescriptor(indexedPropertyDescriptor, propertyDescriptor);
            }
        }
        else if (indexedPropertyDescriptor.getReadMethod() == null && indexedPropertyDescriptor.getWriteMethod() == null) {
            if (propertyDescriptor.getClass0().isAssignableFrom(indexedPropertyDescriptor.getClass0())) {
                propertyDescriptor2 = new PropertyDescriptor(propertyDescriptor, indexedPropertyDescriptor);
            }
            else {
                propertyDescriptor2 = new PropertyDescriptor(indexedPropertyDescriptor, propertyDescriptor);
            }
        }
        else if (propertyDescriptor.getClass0().isAssignableFrom(indexedPropertyDescriptor.getClass0())) {
            propertyDescriptor2 = indexedPropertyDescriptor;
        }
        else {
            propertyDescriptor2 = propertyDescriptor;
            final Method writeMethod = propertyDescriptor2.getWriteMethod();
            Method readMethod = propertyDescriptor2.getReadMethod();
            if (readMethod == null && writeMethod != null) {
                readMethod = findMethod(propertyDescriptor2.getClass0(), "get" + NameGenerator.capitalize(propertyDescriptor2.getName()), 0);
                if (readMethod != null) {
                    try {
                        propertyDescriptor2.setReadMethod(readMethod);
                    }
                    catch (final IntrospectionException ex) {}
                }
            }
            if (writeMethod == null && readMethod != null) {
                final Method method = findMethod(propertyDescriptor2.getClass0(), "set" + NameGenerator.capitalize(propertyDescriptor2.getName()), 1, new Class[] { FeatureDescriptor.getReturnType(propertyDescriptor2.getClass0(), readMethod) });
                if (method != null) {
                    try {
                        propertyDescriptor2.setWriteMethod(method);
                    }
                    catch (final IntrospectionException ex2) {}
                }
            }
        }
        return propertyDescriptor2;
    }
    
    private PropertyDescriptor mergePropertyDescriptor(final PropertyDescriptor propertyDescriptor, final PropertyDescriptor propertyDescriptor2) {
        if (propertyDescriptor.getClass0().isAssignableFrom(propertyDescriptor2.getClass0())) {
            return new PropertyDescriptor(propertyDescriptor, propertyDescriptor2);
        }
        return new PropertyDescriptor(propertyDescriptor2, propertyDescriptor);
    }
    
    private IndexedPropertyDescriptor mergePropertyDescriptor(final IndexedPropertyDescriptor indexedPropertyDescriptor, final IndexedPropertyDescriptor indexedPropertyDescriptor2) {
        if (indexedPropertyDescriptor.getClass0().isAssignableFrom(indexedPropertyDescriptor2.getClass0())) {
            return new IndexedPropertyDescriptor(indexedPropertyDescriptor, indexedPropertyDescriptor2);
        }
        return new IndexedPropertyDescriptor(indexedPropertyDescriptor2, indexedPropertyDescriptor);
    }
    
    private EventSetDescriptor[] getTargetEventInfo() throws IntrospectionException {
        if (this.events == null) {
            this.events = new HashMap<String, EventSetDescriptor>();
        }
        EventSetDescriptor[] eventSetDescriptors = null;
        if (this.explicitBeanInfo != null) {
            eventSetDescriptors = this.explicitBeanInfo.getEventSetDescriptors();
            final int defaultEventIndex = this.explicitBeanInfo.getDefaultEventIndex();
            if (defaultEventIndex >= 0 && defaultEventIndex < eventSetDescriptors.length) {
                this.defaultEventName = eventSetDescriptors[defaultEventIndex].getName();
            }
        }
        if (eventSetDescriptors == null && this.superBeanInfo != null) {
            final EventSetDescriptor[] eventSetDescriptors2 = this.superBeanInfo.getEventSetDescriptors();
            for (int i = 0; i < eventSetDescriptors2.length; ++i) {
                this.addEvent(eventSetDescriptors2[i]);
            }
            final int defaultEventIndex2 = this.superBeanInfo.getDefaultEventIndex();
            if (defaultEventIndex2 >= 0 && defaultEventIndex2 < eventSetDescriptors2.length) {
                this.defaultEventName = eventSetDescriptors2[defaultEventIndex2].getName();
            }
        }
        for (int j = 0; j < this.additionalBeanInfo.length; ++j) {
            final EventSetDescriptor[] eventSetDescriptors3 = this.additionalBeanInfo[j].getEventSetDescriptors();
            if (eventSetDescriptors3 != null) {
                for (int k = 0; k < eventSetDescriptors3.length; ++k) {
                    this.addEvent(eventSetDescriptors3[k]);
                }
            }
        }
        if (eventSetDescriptors != null) {
            for (int l = 0; l < eventSetDescriptors.length; ++l) {
                this.addEvent(eventSetDescriptors[l]);
            }
        }
        else {
            final Method[] publicDeclaredMethods = getPublicDeclaredMethods(this.beanClass);
            Map<String, Method> map = null;
            Map<String, Method> map2 = null;
            Map<String, Method> map3 = null;
            for (int n = 0; n < publicDeclaredMethods.length; ++n) {
                final Method method = publicDeclaredMethods[n];
                if (method != null) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        final String name = method.getName();
                        if (name.startsWith("add") || name.startsWith("remove") || name.startsWith("get")) {
                            if (name.startsWith("add")) {
                                if (method.getReturnType() == Void.TYPE) {
                                    final Type[] genericParameterTypes = method.getGenericParameterTypes();
                                    if (genericParameterTypes.length == 1) {
                                        final Class<?> erase = TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, genericParameterTypes[0]));
                                        if (isSubclass(erase, Introspector.eventListenerType)) {
                                            final String substring = name.substring(3);
                                            if (substring.length() > 0 && erase.getName().endsWith(substring)) {
                                                if (map == null) {
                                                    map = new HashMap<String, Method>();
                                                }
                                                map.put(substring, method);
                                            }
                                        }
                                    }
                                }
                            }
                            else if (name.startsWith("remove")) {
                                if (method.getReturnType() == Void.TYPE) {
                                    final Type[] genericParameterTypes2 = method.getGenericParameterTypes();
                                    if (genericParameterTypes2.length == 1) {
                                        final Class<?> erase2 = TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, genericParameterTypes2[0]));
                                        if (isSubclass(erase2, Introspector.eventListenerType)) {
                                            final String substring2 = name.substring(6);
                                            if (substring2.length() > 0 && erase2.getName().endsWith(substring2)) {
                                                if (map2 == null) {
                                                    map2 = new HashMap<String, Method>();
                                                }
                                                map2.put(substring2, method);
                                            }
                                        }
                                    }
                                }
                            }
                            else if (name.startsWith("get") && method.getParameterTypes().length == 0) {
                                final Class<?> returnType = FeatureDescriptor.getReturnType(this.beanClass, method);
                                if (returnType.isArray()) {
                                    final Class componentType = returnType.getComponentType();
                                    if (isSubclass(componentType, Introspector.eventListenerType)) {
                                        final String substring3 = name.substring(3, name.length() - 1);
                                        if (substring3.length() > 0 && componentType.getName().endsWith(substring3)) {
                                            if (map3 == null) {
                                                map3 = new HashMap<String, Method>();
                                            }
                                            map3.put(substring3, method);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (map != null && map2 != null) {
                for (final String s : map.keySet()) {
                    if (map2.get(s) != null) {
                        if (!s.endsWith("Listener")) {
                            continue;
                        }
                        final String decapitalize = decapitalize(s.substring(0, s.length() - 8));
                        final Method method2 = map.get(s);
                        final Method method3 = map2.get(s);
                        Method method4 = null;
                        if (map3 != null) {
                            method4 = map3.get(s);
                        }
                        final Class<?> clazz = FeatureDescriptor.getParameterTypes(this.beanClass, method2)[0];
                        final Method[] publicDeclaredMethods2 = getPublicDeclaredMethods(clazz);
                        final ArrayList list = new ArrayList(publicDeclaredMethods2.length);
                        for (int n2 = 0; n2 < publicDeclaredMethods2.length; ++n2) {
                            if (publicDeclaredMethods2[n2] != null) {
                                if (this.isEventHandler(publicDeclaredMethods2[n2])) {
                                    list.add((Object)publicDeclaredMethods2[n2]);
                                }
                            }
                        }
                        final EventSetDescriptor eventSetDescriptor = new EventSetDescriptor(decapitalize, clazz, (Method[])list.toArray((Object[])new Method[list.size()]), method2, method3, method4);
                        if (this.throwsException(method2, TooManyListenersException.class)) {
                            eventSetDescriptor.setUnicast(true);
                        }
                        this.addEvent(eventSetDescriptor);
                    }
                }
            }
        }
        EventSetDescriptor[] empty_EVENTSETDESCRIPTORS;
        if (this.events.size() == 0) {
            empty_EVENTSETDESCRIPTORS = Introspector.EMPTY_EVENTSETDESCRIPTORS;
        }
        else {
            empty_EVENTSETDESCRIPTORS = this.events.values().toArray(new EventSetDescriptor[this.events.size()]);
            if (this.defaultEventName != null) {
                for (int defaultEventIndex3 = 0; defaultEventIndex3 < empty_EVENTSETDESCRIPTORS.length; ++defaultEventIndex3) {
                    if (this.defaultEventName.equals(empty_EVENTSETDESCRIPTORS[defaultEventIndex3].getName())) {
                        this.defaultEventIndex = defaultEventIndex3;
                    }
                }
            }
        }
        return empty_EVENTSETDESCRIPTORS;
    }
    
    private void addEvent(final EventSetDescriptor eventSetDescriptor) {
        final String name = eventSetDescriptor.getName();
        if (eventSetDescriptor.getName().equals("propertyChange")) {
            this.propertyChangeSource = true;
        }
        final EventSetDescriptor eventSetDescriptor2 = this.events.get(name);
        if (eventSetDescriptor2 == null) {
            this.events.put(name, eventSetDescriptor);
            return;
        }
        this.events.put(name, new EventSetDescriptor(eventSetDescriptor2, eventSetDescriptor));
    }
    
    private MethodDescriptor[] getTargetMethodInfo() {
        if (this.methods == null) {
            this.methods = new HashMap<String, MethodDescriptor>(100);
        }
        MethodDescriptor[] methodDescriptors = null;
        if (this.explicitBeanInfo != null) {
            methodDescriptors = this.explicitBeanInfo.getMethodDescriptors();
        }
        if (methodDescriptors == null && this.superBeanInfo != null) {
            final MethodDescriptor[] methodDescriptors2 = this.superBeanInfo.getMethodDescriptors();
            for (int i = 0; i < methodDescriptors2.length; ++i) {
                this.addMethod(methodDescriptors2[i]);
            }
        }
        for (int j = 0; j < this.additionalBeanInfo.length; ++j) {
            final MethodDescriptor[] methodDescriptors3 = this.additionalBeanInfo[j].getMethodDescriptors();
            if (methodDescriptors3 != null) {
                for (int k = 0; k < methodDescriptors3.length; ++k) {
                    this.addMethod(methodDescriptors3[k]);
                }
            }
        }
        if (methodDescriptors != null) {
            for (int l = 0; l < methodDescriptors.length; ++l) {
                this.addMethod(methodDescriptors[l]);
            }
        }
        else {
            final Method[] publicDeclaredMethods = getPublicDeclaredMethods(this.beanClass);
            for (int n = 0; n < publicDeclaredMethods.length; ++n) {
                final Method method = publicDeclaredMethods[n];
                if (method != null) {
                    this.addMethod(new MethodDescriptor(method));
                }
            }
        }
        return this.methods.values().toArray(new MethodDescriptor[this.methods.size()]);
    }
    
    private void addMethod(final MethodDescriptor methodDescriptor) {
        final String name = methodDescriptor.getName();
        final MethodDescriptor methodDescriptor2 = this.methods.get(name);
        if (methodDescriptor2 == null) {
            this.methods.put(name, methodDescriptor);
            return;
        }
        final String[] paramNames = methodDescriptor.getParamNames();
        final String[] paramNames2 = methodDescriptor2.getParamNames();
        boolean b = false;
        if (paramNames.length == paramNames2.length) {
            b = true;
            for (int i = 0; i < paramNames.length; ++i) {
                if (paramNames[i] != paramNames2[i]) {
                    b = false;
                    break;
                }
            }
        }
        if (b) {
            this.methods.put(name, new MethodDescriptor(methodDescriptor2, methodDescriptor));
            return;
        }
        final String qualifiedMethodName = makeQualifiedMethodName(name, paramNames);
        final MethodDescriptor methodDescriptor3 = this.methods.get(qualifiedMethodName);
        if (methodDescriptor3 == null) {
            this.methods.put(qualifiedMethodName, methodDescriptor);
            return;
        }
        this.methods.put(qualifiedMethodName, new MethodDescriptor(methodDescriptor3, methodDescriptor));
    }
    
    private static String makeQualifiedMethodName(final String s, final String[] array) {
        final StringBuffer sb = new StringBuffer(s);
        sb.append('=');
        for (int i = 0; i < array.length; ++i) {
            sb.append(':');
            sb.append(array[i]);
        }
        return sb.toString();
    }
    
    private int getTargetDefaultEventIndex() {
        return this.defaultEventIndex;
    }
    
    private int getTargetDefaultPropertyIndex() {
        return this.defaultPropertyIndex;
    }
    
    private BeanDescriptor getTargetBeanDescriptor() {
        if (this.explicitBeanInfo != null) {
            final BeanDescriptor beanDescriptor = this.explicitBeanInfo.getBeanDescriptor();
            if (beanDescriptor != null) {
                return beanDescriptor;
            }
        }
        return new BeanDescriptor(this.beanClass, findCustomizerClass(this.beanClass));
    }
    
    private static Class<?> findCustomizerClass(final Class<?> clazz) {
        final String string = clazz.getName() + "Customizer";
        try {
            final Class<?> class1 = ClassFinder.findClass(string, clazz.getClassLoader());
            if (Component.class.isAssignableFrom(class1) && Customizer.class.isAssignableFrom(class1)) {
                return class1;
            }
        }
        catch (final Exception ex) {}
        return null;
    }
    
    private boolean isEventHandler(final Method method) {
        final Type[] genericParameterTypes = method.getGenericParameterTypes();
        return genericParameterTypes.length == 1 && isSubclass(TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, genericParameterTypes[0])), EventObject.class);
    }
    
    private static Method[] getPublicDeclaredMethods(final Class<?> clazz) {
        if (!ReflectUtil.isPackageAccessible(clazz)) {
            return new Method[0];
        }
        synchronized (Introspector.declaredMethodCache) {
            Method[] methods = Introspector.declaredMethodCache.get(clazz);
            if (methods == null) {
                methods = clazz.getMethods();
                for (int i = 0; i < methods.length; ++i) {
                    final Method method = methods[i];
                    if (!method.getDeclaringClass().equals(clazz)) {
                        methods[i] = null;
                    }
                    else {
                        try {
                            final Method accessibleMethod = MethodFinder.findAccessibleMethod(method);
                            final Class<?> declaringClass = accessibleMethod.getDeclaringClass();
                            methods[i] = ((declaringClass.equals(clazz) || declaringClass.isInterface()) ? accessibleMethod : null);
                        }
                        catch (final NoSuchMethodException ex) {}
                    }
                }
                Introspector.declaredMethodCache.put(clazz, methods);
            }
            return methods;
        }
    }
    
    private static Method internalFindMethod(final Class<?> clazz, final String s, final int n, final Class[] array) {
        for (Class<?> superclass = clazz; superclass != null; superclass = superclass.getSuperclass()) {
            final Method[] publicDeclaredMethods = getPublicDeclaredMethods(superclass);
            for (int i = 0; i < publicDeclaredMethods.length; ++i) {
                final Method method = publicDeclaredMethods[i];
                if (method != null) {
                    if (method.getName().equals(s)) {
                        final Type[] genericParameterTypes = method.getGenericParameterTypes();
                        if (genericParameterTypes.length == n) {
                            if (array != null) {
                                boolean b = false;
                                if (n > 0) {
                                    for (int j = 0; j < n; ++j) {
                                        if (TypeResolver.erase(TypeResolver.resolveInClass(clazz, genericParameterTypes[j])) != array[j]) {
                                            b = true;
                                        }
                                    }
                                    if (b) {
                                        continue;
                                    }
                                }
                            }
                            return method;
                        }
                    }
                }
            }
        }
        Method internalFindMethod = null;
        final Class<?>[] interfaces = clazz.getInterfaces();
        for (int k = 0; k < interfaces.length; ++k) {
            internalFindMethod = internalFindMethod(interfaces[k], s, n, null);
            if (internalFindMethod != null) {
                break;
            }
        }
        return internalFindMethod;
    }
    
    static Method findMethod(final Class<?> clazz, final String s, final int n) {
        return findMethod(clazz, s, n, null);
    }
    
    static Method findMethod(final Class<?> clazz, final String s, final int n, final Class[] array) {
        if (s == null) {
            return null;
        }
        return internalFindMethod(clazz, s, n, array);
    }
    
    static boolean isSubclass(final Class<?> clazz, final Class<?> clazz2) {
        if (clazz == clazz2) {
            return true;
        }
        if (clazz == null || clazz2 == null) {
            return false;
        }
        for (Class<?> superclass = clazz; superclass != null; superclass = superclass.getSuperclass()) {
            if (superclass == clazz2) {
                return true;
            }
            if (clazz2.isInterface()) {
                final Class[] interfaces = superclass.getInterfaces();
                for (int i = 0; i < interfaces.length; ++i) {
                    if (isSubclass(interfaces[i], clazz2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean throwsException(final Method method, final Class<?> clazz) {
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        for (int i = 0; i < exceptionTypes.length; ++i) {
            if (exceptionTypes[i] == clazz) {
                return true;
            }
        }
        return false;
    }
    
    static Object instantiate(final Class<?> clazz, final String s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return ClassFinder.findClass(s, clazz.getClassLoader()).newInstance();
    }
    
    static {
        declaredMethodCache = new WeakCache<Class<?>, Method[]>();
        Introspector.eventListenerType = EventListener.class;
        EMPTY_EVENTSETDESCRIPTORS = new EventSetDescriptor[0];
    }
}
