package org.msgpack.template.builder.beans;

import java.util.Comparator;
import java.util.Arrays;
import java.util.EventObject;
import java.util.TooManyListenersException;
import java.util.EventListener;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.lang.reflect.Modifier;

class StandardBeanInfo extends SimpleBeanInfo
{
    private static final String PREFIX_IS = "is";
    private static final String PREFIX_GET = "get";
    private static final String PREFIX_SET = "set";
    private static final String PREFIX_ADD = "add";
    private static final String PREFIX_REMOVE = "remove";
    private static final String SUFFIX_LISTEN = "Listener";
    private static final String STR_NORMAL = "normal";
    private static final String STR_INDEXED = "indexed";
    private static final String STR_VALID = "valid";
    private static final String STR_INVALID = "invalid";
    private static final String STR_PROPERTY_TYPE = "PropertyType";
    private static final String STR_IS_CONSTRAINED = "isConstrained";
    private static final String STR_SETTERS = "setters";
    private static final String STR_GETTERS = "getters";
    private boolean explicitMethods;
    private boolean explicitProperties;
    private boolean explicitEvents;
    private BeanInfo explicitBeanInfo;
    private EventSetDescriptor[] events;
    private MethodDescriptor[] methods;
    private PropertyDescriptor[] properties;
    private BeanDescriptor beanDescriptor;
    BeanInfo[] additionalBeanInfo;
    private Class<?> beanClass;
    private int defaultEventIndex;
    private int defaultPropertyIndex;
    private static PropertyComparator comparator;
    private boolean canAddPropertyChangeListener;
    private boolean canRemovePropertyChangeListener;
    
    StandardBeanInfo(final Class<?> beanClass, final BeanInfo explicitBeanInfo, final Class<?> stopClass) throws IntrospectionException {
        this.explicitMethods = false;
        this.explicitProperties = false;
        this.explicitEvents = false;
        this.explicitBeanInfo = null;
        this.events = null;
        this.methods = null;
        this.properties = null;
        this.beanDescriptor = null;
        this.additionalBeanInfo = null;
        this.defaultEventIndex = -1;
        this.defaultPropertyIndex = -1;
        this.beanClass = beanClass;
        if (explicitBeanInfo != null) {
            this.explicitBeanInfo = explicitBeanInfo;
            this.events = explicitBeanInfo.getEventSetDescriptors();
            this.methods = explicitBeanInfo.getMethodDescriptors();
            this.properties = explicitBeanInfo.getPropertyDescriptors();
            this.defaultEventIndex = explicitBeanInfo.getDefaultEventIndex();
            if (this.defaultEventIndex < 0 || this.defaultEventIndex >= this.events.length) {
                this.defaultEventIndex = -1;
            }
            this.defaultPropertyIndex = explicitBeanInfo.getDefaultPropertyIndex();
            if (this.defaultPropertyIndex < 0 || this.defaultPropertyIndex >= this.properties.length) {
                this.defaultPropertyIndex = -1;
            }
            this.additionalBeanInfo = explicitBeanInfo.getAdditionalBeanInfo();
            if (this.events != null) {
                this.explicitEvents = true;
            }
            if (this.methods != null) {
                this.explicitMethods = true;
            }
            if (this.properties != null) {
                this.explicitProperties = true;
            }
        }
        if (this.methods == null) {
            this.methods = this.introspectMethods();
        }
        if (this.properties == null) {
            this.properties = this.introspectProperties(stopClass);
        }
        if (this.events == null) {
            this.events = this.introspectEvents();
        }
    }
    
    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return null;
    }
    
    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return this.events;
    }
    
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return this.methods;
    }
    
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.properties;
    }
    
    @Override
    public BeanDescriptor getBeanDescriptor() {
        if (this.beanDescriptor == null) {
            if (this.explicitBeanInfo != null) {
                this.beanDescriptor = this.explicitBeanInfo.getBeanDescriptor();
            }
            if (this.beanDescriptor == null) {
                this.beanDescriptor = new BeanDescriptor(this.beanClass);
            }
        }
        return this.beanDescriptor;
    }
    
    @Override
    public int getDefaultEventIndex() {
        return this.defaultEventIndex;
    }
    
    @Override
    public int getDefaultPropertyIndex() {
        return this.defaultPropertyIndex;
    }
    
    void mergeBeanInfo(final BeanInfo beanInfo, final boolean force) throws IntrospectionException {
        if (force || !this.explicitProperties) {
            final PropertyDescriptor[] superDescs = beanInfo.getPropertyDescriptors();
            if (superDescs != null) {
                if (this.getPropertyDescriptors() != null) {
                    this.properties = this.mergeProps(superDescs, beanInfo.getDefaultPropertyIndex());
                }
                else {
                    this.properties = superDescs;
                    this.defaultPropertyIndex = beanInfo.getDefaultPropertyIndex();
                }
            }
        }
        if (force || !this.explicitMethods) {
            final MethodDescriptor[] superMethods = beanInfo.getMethodDescriptors();
            if (superMethods != null) {
                if (this.methods != null) {
                    this.methods = this.mergeMethods(superMethods);
                }
                else {
                    this.methods = superMethods;
                }
            }
        }
        if (force || !this.explicitEvents) {
            final EventSetDescriptor[] superEvents = beanInfo.getEventSetDescriptors();
            if (superEvents != null) {
                if (this.events != null) {
                    this.events = this.mergeEvents(superEvents, beanInfo.getDefaultEventIndex());
                }
                else {
                    this.events = superEvents;
                    this.defaultEventIndex = beanInfo.getDefaultEventIndex();
                }
            }
        }
    }
    
    private PropertyDescriptor[] mergeProps(final PropertyDescriptor[] superDescs, final int superDefaultIndex) throws IntrospectionException {
        final HashMap<String, PropertyDescriptor> subMap = internalAsMap(this.properties);
        String defaultPropertyName = null;
        if (this.defaultPropertyIndex >= 0 && this.defaultPropertyIndex < this.properties.length) {
            defaultPropertyName = this.properties[this.defaultPropertyIndex].getName();
        }
        else if (superDefaultIndex >= 0 && superDefaultIndex < superDescs.length) {
            defaultPropertyName = superDescs[superDefaultIndex].getName();
        }
        for (int i = 0; i < superDescs.length; ++i) {
            final PropertyDescriptor superDesc = superDescs[i];
            final String propertyName = superDesc.getName();
            if (!subMap.containsKey(propertyName)) {
                subMap.put(propertyName, superDesc);
            }
            else {
                final Object value = subMap.get(propertyName);
                final Method subGet = ((PropertyDescriptor)value).getReadMethod();
                Method subSet = ((PropertyDescriptor)value).getWriteMethod();
                final Method superGet = superDesc.getReadMethod();
                final Method superSet = superDesc.getWriteMethod();
                final Class<?> superType = superDesc.getPropertyType();
                Class<?> superIndexedType = null;
                final Class<?> subType = ((PropertyDescriptor)value).getPropertyType();
                Class<?> subIndexedType = null;
                if (value instanceof IndexedPropertyDescriptor) {
                    subIndexedType = ((IndexedPropertyDescriptor)value).getIndexedPropertyType();
                }
                if (superDesc instanceof IndexedPropertyDescriptor) {
                    superIndexedType = ((IndexedPropertyDescriptor)superDesc).getIndexedPropertyType();
                }
                if (superIndexedType == null) {
                    PropertyDescriptor subDesc = (PropertyDescriptor)value;
                    if (subIndexedType == null) {
                        if (subType != null && superType != null && subType.getName() != null && subType.getName().equals(superType.getName())) {
                            if (superGet != null && (subGet == null || superGet.equals(subGet))) {
                                subDesc.setReadMethod(superGet);
                            }
                            if (superSet != null && (subSet == null || superSet.equals(subSet))) {
                                subDesc.setWriteMethod(superSet);
                            }
                            if (subType == Boolean.TYPE && subGet != null && superGet != null && superGet.getName().startsWith("is")) {
                                subDesc.setReadMethod(superGet);
                            }
                        }
                        else if ((subGet == null || subSet == null) && superGet != null) {
                            subDesc = new PropertyDescriptor(propertyName, superGet, superSet);
                            if (subGet != null) {
                                final String subGetName = subGet.getName();
                                Method method = null;
                                final MethodDescriptor[] arr$;
                                final MethodDescriptor[] introspectMethods = arr$ = this.introspectMethods();
                                for (final MethodDescriptor methodDesc : arr$) {
                                    method = methodDesc.getMethod();
                                    if (method != subGet && subGetName.equals(method.getName()) && method.getParameterTypes().length == 0 && method.getReturnType() == superType) {
                                        subDesc.setReadMethod(method);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (superType != null && superType.isArray() && superType.getComponentType().getName().equals(subIndexedType.getName())) {
                            if (subGet == null && superGet != null) {
                                subDesc.setReadMethod(superGet);
                            }
                            if (subSet == null && superSet != null) {
                                subDesc.setWriteMethod(superSet);
                            }
                        }
                        if (subIndexedType == Boolean.TYPE && superType == Boolean.TYPE) {
                            final Method subIndexedSet = ((IndexedPropertyDescriptor)subDesc).getIndexedWriteMethod();
                            if (subGet == null && subSet == null && subIndexedSet != null && superGet != null) {
                                try {
                                    subSet = this.beanClass.getDeclaredMethod(subIndexedSet.getName(), Boolean.TYPE);
                                }
                                catch (final Exception ex) {}
                                if (subSet != null) {
                                    subDesc = new PropertyDescriptor(propertyName, superGet, subSet);
                                }
                            }
                        }
                    }
                    subMap.put(propertyName, subDesc);
                }
                else if (subIndexedType == null) {
                    if (subType != null && subType.isArray() && subType.getComponentType().getName().equals(superIndexedType.getName())) {
                        if (subGet != null) {
                            superDesc.setReadMethod(subGet);
                        }
                        if (subSet != null) {
                            superDesc.setWriteMethod(subSet);
                        }
                        subMap.put(propertyName, superDesc);
                    }
                    else {
                        if (subGet == null || subSet == null) {
                            final Class<?> beanSuperClass = this.beanClass.getSuperclass();
                            final String methodSuffix = this.capitalize(propertyName);
                            Method method = null;
                            if (subGet == null) {
                                if (subType == Boolean.TYPE) {
                                    try {
                                        method = beanSuperClass.getDeclaredMethod("is" + methodSuffix, (Class<?>[])new Class[0]);
                                    }
                                    catch (final Exception e) {}
                                }
                                else {
                                    try {
                                        method = beanSuperClass.getDeclaredMethod("get" + methodSuffix, (Class<?>[])new Class[0]);
                                    }
                                    catch (final Exception ex2) {}
                                }
                                if (method != null && !Modifier.isStatic(method.getModifiers()) && method.getReturnType() == subType) {
                                    ((PropertyDescriptor)value).setReadMethod(method);
                                }
                            }
                            else {
                                try {
                                    method = beanSuperClass.getDeclaredMethod("set" + methodSuffix, subType);
                                }
                                catch (final Exception ex3) {}
                                if (method != null && !Modifier.isStatic(method.getModifiers()) && method.getReturnType() == Void.TYPE) {
                                    ((PropertyDescriptor)value).setWriteMethod(method);
                                }
                            }
                        }
                        subMap.put(propertyName, (PropertyDescriptor)value);
                    }
                }
                else if (subIndexedType.getName().equals(superIndexedType.getName())) {
                    final IndexedPropertyDescriptor subDesc2 = (IndexedPropertyDescriptor)value;
                    if (subGet == null && superGet != null) {
                        subDesc2.setReadMethod(superGet);
                    }
                    if (subSet == null && superSet != null) {
                        subDesc2.setWriteMethod(superSet);
                    }
                    final IndexedPropertyDescriptor superIndexedDesc = (IndexedPropertyDescriptor)superDesc;
                    if (subDesc2.getIndexedReadMethod() == null && superIndexedDesc.getIndexedReadMethod() != null) {
                        subDesc2.setIndexedReadMethod(superIndexedDesc.getIndexedReadMethod());
                    }
                    if (subDesc2.getIndexedWriteMethod() == null && superIndexedDesc.getIndexedWriteMethod() != null) {
                        subDesc2.setIndexedWriteMethod(superIndexedDesc.getIndexedWriteMethod());
                    }
                    subMap.put(propertyName, subDesc2);
                }
                mergeAttributes((PropertyDescriptor)value, superDesc);
            }
        }
        final PropertyDescriptor[] theDescs = new PropertyDescriptor[subMap.size()];
        subMap.values().toArray(theDescs);
        if (defaultPropertyName != null && !this.explicitProperties) {
            for (int j = 0; j < theDescs.length; ++j) {
                if (defaultPropertyName.equals(theDescs[j].getName())) {
                    this.defaultPropertyIndex = j;
                    break;
                }
            }
        }
        return theDescs;
    }
    
    private String capitalize(final String name) {
        if (name == null) {
            return null;
        }
        if (name.length() == 0 || (name.length() > 1 && Character.isUpperCase(name.charAt(1)))) {
            return name;
        }
        final char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
    
    private static void mergeAttributes(final PropertyDescriptor subDesc, final PropertyDescriptor superDesc) {
        subDesc.hidden |= superDesc.hidden;
        subDesc.expert |= superDesc.expert;
        subDesc.preferred |= superDesc.preferred;
        subDesc.bound |= superDesc.bound;
        subDesc.constrained |= superDesc.constrained;
        subDesc.name = superDesc.name;
        if (subDesc.shortDescription == null && superDesc.shortDescription != null) {
            subDesc.shortDescription = superDesc.shortDescription;
        }
        if (subDesc.displayName == null && superDesc.displayName != null) {
            subDesc.displayName = superDesc.displayName;
        }
    }
    
    private MethodDescriptor[] mergeMethods(final MethodDescriptor[] superDescs) {
        final HashMap<String, MethodDescriptor> subMap = internalAsMap(this.methods);
        for (final MethodDescriptor superMethod : superDescs) {
            final String methodName = getQualifiedName(superMethod.getMethod());
            final MethodDescriptor method = subMap.get(methodName);
            if (method == null) {
                subMap.put(methodName, superMethod);
            }
            else {
                method.merge(superMethod);
            }
        }
        final MethodDescriptor[] theMethods = new MethodDescriptor[subMap.size()];
        subMap.values().toArray(theMethods);
        return theMethods;
    }
    
    private EventSetDescriptor[] mergeEvents(final EventSetDescriptor[] otherEvents, final int otherDefaultIndex) {
        final HashMap<String, EventSetDescriptor> subMap = internalAsMap(this.events);
        String defaultEventName = null;
        if (this.defaultEventIndex >= 0 && this.defaultEventIndex < this.events.length) {
            defaultEventName = this.events[this.defaultEventIndex].getName();
        }
        else if (otherDefaultIndex >= 0 && otherDefaultIndex < otherEvents.length) {
            defaultEventName = otherEvents[otherDefaultIndex].getName();
        }
        for (final EventSetDescriptor event : otherEvents) {
            final String eventName = event.getName();
            final EventSetDescriptor subEvent = subMap.get(eventName);
            if (subEvent == null) {
                subMap.put(eventName, event);
            }
            else {
                subEvent.merge(event);
            }
        }
        final EventSetDescriptor[] theEvents = new EventSetDescriptor[subMap.size()];
        subMap.values().toArray(theEvents);
        if (defaultEventName != null && !this.explicitEvents) {
            for (int i = 0; i < theEvents.length; ++i) {
                if (defaultEventName.equals(theEvents[i].getName())) {
                    this.defaultEventIndex = i;
                    break;
                }
            }
        }
        return theEvents;
    }
    
    private static HashMap<String, PropertyDescriptor> internalAsMap(final PropertyDescriptor[] propertyDescs) {
        final HashMap<String, PropertyDescriptor> map = new HashMap<String, PropertyDescriptor>();
        for (int i = 0; i < propertyDescs.length; ++i) {
            map.put(propertyDescs[i].getName(), propertyDescs[i]);
        }
        return map;
    }
    
    private static HashMap<String, MethodDescriptor> internalAsMap(final MethodDescriptor[] theDescs) {
        final HashMap<String, MethodDescriptor> map = new HashMap<String, MethodDescriptor>();
        for (int i = 0; i < theDescs.length; ++i) {
            final String qualifiedName = getQualifiedName(theDescs[i].getMethod());
            map.put(qualifiedName, theDescs[i]);
        }
        return map;
    }
    
    private static HashMap<String, EventSetDescriptor> internalAsMap(final EventSetDescriptor[] theDescs) {
        final HashMap<String, EventSetDescriptor> map = new HashMap<String, EventSetDescriptor>();
        for (int i = 0; i < theDescs.length; ++i) {
            map.put(theDescs[i].getName(), theDescs[i]);
        }
        return map;
    }
    
    private static String getQualifiedName(final Method method) {
        String qualifiedName = method.getName();
        final Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; ++i) {
                qualifiedName = qualifiedName + "_" + paramTypes[i].getName();
            }
        }
        return qualifiedName;
    }
    
    private MethodDescriptor[] introspectMethods() {
        return this.introspectMethods(false, this.beanClass);
    }
    
    private MethodDescriptor[] introspectMethods(final boolean includeSuper) {
        return this.introspectMethods(includeSuper, this.beanClass);
    }
    
    private MethodDescriptor[] introspectMethods(final boolean includeSuper, final Class<?> introspectorClass) {
        final Method[] basicMethods = includeSuper ? introspectorClass.getMethods() : introspectorClass.getDeclaredMethods();
        if (basicMethods == null || basicMethods.length == 0) {
            return null;
        }
        final ArrayList<MethodDescriptor> methodList = new ArrayList<MethodDescriptor>(basicMethods.length);
        for (int i = 0; i < basicMethods.length; ++i) {
            final int modifiers = basicMethods[i].getModifiers();
            if (Modifier.isPublic(modifiers)) {
                final MethodDescriptor theDescriptor = new MethodDescriptor(basicMethods[i]);
                methodList.add(theDescriptor);
            }
        }
        final int methodCount = methodList.size();
        MethodDescriptor[] theMethods = null;
        if (methodCount > 0) {
            theMethods = new MethodDescriptor[methodCount];
            theMethods = methodList.toArray(theMethods);
        }
        return theMethods;
    }
    
    private PropertyDescriptor[] introspectProperties(final Class<?> stopClass) throws IntrospectionException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   org/msgpack/template/builder/beans/StandardBeanInfo.introspectMethods:()[Lorg/msgpack/template/builder/beans/MethodDescriptor;
        //     4: astore_2        /* methodDescriptors */
        //     5: aload_2         /* methodDescriptors */
        //     6: ifnonnull       11
        //     9: aconst_null    
        //    10: areturn        
        //    11: new             Ljava/util/ArrayList;
        //    14: dup            
        //    15: invokespecial   java/util/ArrayList.<init>:()V
        //    18: astore_3        /* methodList */
        //    19: iconst_0       
        //    20: istore          index
        //    22: iload           index
        //    24: aload_2         /* methodDescriptors */
        //    25: arraylength    
        //    26: if_icmpge       64
        //    29: aload_2         /* methodDescriptors */
        //    30: iload           index
        //    32: aaload         
        //    33: invokevirtual   org/msgpack/template/builder/beans/MethodDescriptor.getMethod:()Ljava/lang/reflect/Method;
        //    36: invokevirtual   java/lang/reflect/Method.getModifiers:()I
        //    39: istore          modifiers
        //    41: iload           modifiers
        //    43: invokestatic    java/lang/reflect/Modifier.isStatic:(I)Z
        //    46: ifne            58
        //    49: aload_3         /* methodList */
        //    50: aload_2         /* methodDescriptors */
        //    51: iload           index
        //    53: aaload         
        //    54: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //    57: pop            
        //    58: iinc            index, 1
        //    61: goto            22
        //    64: aload_3         /* methodList */
        //    65: invokevirtual   java/util/ArrayList.size:()I
        //    68: istore          methodCount
        //    70: aconst_null    
        //    71: astore          theMethods
        //    73: iload           methodCount
        //    75: ifle            96
        //    78: iload           methodCount
        //    80: anewarray       Lorg/msgpack/template/builder/beans/MethodDescriptor;
        //    83: astore          theMethods
        //    85: aload_3         /* methodList */
        //    86: aload           theMethods
        //    88: invokevirtual   java/util/ArrayList.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //    91: checkcast       [Lorg/msgpack/template/builder/beans/MethodDescriptor;
        //    94: astore          theMethods
        //    96: aload           theMethods
        //    98: ifnonnull       103
        //   101: aconst_null    
        //   102: areturn        
        //   103: new             Ljava/util/HashMap;
        //   106: dup            
        //   107: aload           theMethods
        //   109: arraylength    
        //   110: invokespecial   java/util/HashMap.<init>:(I)V
        //   113: astore          propertyTable
        //   115: iconst_0       
        //   116: istore          i
        //   118: iload           i
        //   120: aload           theMethods
        //   122: arraylength    
        //   123: if_icmpge       158
        //   126: aload           theMethods
        //   128: iload           i
        //   130: aaload         
        //   131: invokevirtual   org/msgpack/template/builder/beans/MethodDescriptor.getMethod:()Ljava/lang/reflect/Method;
        //   134: aload           propertyTable
        //   136: invokestatic    org/msgpack/template/builder/beans/StandardBeanInfo.introspectGet:(Ljava/lang/reflect/Method;Ljava/util/HashMap;)V
        //   139: aload           theMethods
        //   141: iload           i
        //   143: aaload         
        //   144: invokevirtual   org/msgpack/template/builder/beans/MethodDescriptor.getMethod:()Ljava/lang/reflect/Method;
        //   147: aload           propertyTable
        //   149: invokestatic    org/msgpack/template/builder/beans/StandardBeanInfo.introspectSet:(Ljava/lang/reflect/Method;Ljava/util/HashMap;)V
        //   152: iinc            i, 1
        //   155: goto            118
        //   158: aload_0         /* this */
        //   159: aload           propertyTable
        //   161: invokespecial   org/msgpack/template/builder/beans/StandardBeanInfo.fixGetSet:(Ljava/util/HashMap;)V
        //   164: aload_0         /* this */
        //   165: iconst_1       
        //   166: invokespecial   org/msgpack/template/builder/beans/StandardBeanInfo.introspectMethods:(Z)[Lorg/msgpack/template/builder/beans/MethodDescriptor;
        //   169: astore          allMethods
        //   171: aload_1         /* stopClass */
        //   172: ifnull          262
        //   175: aload_0         /* this */
        //   176: iconst_1       
        //   177: aload_1         /* stopClass */
        //   178: invokespecial   org/msgpack/template/builder/beans/StandardBeanInfo.introspectMethods:(ZLjava/lang/Class;)[Lorg/msgpack/template/builder/beans/MethodDescriptor;
        //   181: astore          excludeMethods
        //   183: aload           excludeMethods
        //   185: ifnull          262
        //   188: new             Ljava/util/ArrayList;
        //   191: dup            
        //   192: invokespecial   java/util/ArrayList.<init>:()V
        //   195: astore          tempMethods
        //   197: aload           allMethods
        //   199: astore          arr$
        //   201: aload           arr$
        //   203: arraylength    
        //   204: istore          len$
        //   206: iconst_0       
        //   207: istore          i$
        //   209: iload           i$
        //   211: iload           len$
        //   213: if_icmpge       248
        //   216: aload           arr$
        //   218: iload           i$
        //   220: aaload         
        //   221: astore          method
        //   223: aload_0         /* this */
        //   224: aload           method
        //   226: aload           excludeMethods
        //   228: invokespecial   org/msgpack/template/builder/beans/StandardBeanInfo.isInSuper:(Lorg/msgpack/template/builder/beans/MethodDescriptor;[Lorg/msgpack/template/builder/beans/MethodDescriptor;)Z
        //   231: ifne            242
        //   234: aload           tempMethods
        //   236: aload           method
        //   238: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   241: pop            
        //   242: iinc            i$, 1
        //   245: goto            209
        //   248: aload           tempMethods
        //   250: iconst_0       
        //   251: anewarray       Lorg/msgpack/template/builder/beans/MethodDescriptor;
        //   254: invokevirtual   java/util/ArrayList.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   257: checkcast       [Lorg/msgpack/template/builder/beans/MethodDescriptor;
        //   260: astore          allMethods
        //   262: iconst_0       
        //   263: istore          i
        //   265: iload           i
        //   267: aload           allMethods
        //   269: arraylength    
        //   270: if_icmpge       291
        //   273: aload_0         /* this */
        //   274: aload           allMethods
        //   276: iload           i
        //   278: aaload         
        //   279: invokevirtual   org/msgpack/template/builder/beans/MethodDescriptor.getMethod:()Ljava/lang/reflect/Method;
        //   282: invokespecial   org/msgpack/template/builder/beans/StandardBeanInfo.introspectPropertyListener:(Ljava/lang/reflect/Method;)V
        //   285: iinc            i, 1
        //   288: goto            265
        //   291: new             Ljava/util/ArrayList;
        //   294: dup            
        //   295: invokespecial   java/util/ArrayList.<init>:()V
        //   298: astore          propertyList
        //   300: aload           propertyTable
        //   302: invokevirtual   java/util/HashMap.entrySet:()Ljava/util/Set;
        //   305: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //   310: astore          i$
        //   312: aload           i$
        //   314: invokeinterface java/util/Iterator.hasNext:()Z
        //   319: ifeq            577
        //   322: aload           i$
        //   324: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   329: checkcast       Ljava/util/Map$Entry;
        //   332: astore          entry
        //   334: aload           entry
        //   336: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //   341: checkcast       Ljava/lang/String;
        //   344: astore          propertyName
        //   346: aload           entry
        //   348: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //   353: checkcast       Ljava/util/HashMap;
        //   356: astore          table
        //   358: aload           table
        //   360: ifnonnull       366
        //   363: goto            312
        //   366: aload           table
        //   368: ldc             "normal"
        //   370: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   373: checkcast       Ljava/lang/String;
        //   376: astore          normalTag
        //   378: aload           table
        //   380: ldc             "indexed"
        //   382: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   385: checkcast       Ljava/lang/String;
        //   388: astore          indexedTag
        //   390: aload           normalTag
        //   392: ifnonnull       403
        //   395: aload           indexedTag
        //   397: ifnonnull       403
        //   400: goto            312
        //   403: aload           table
        //   405: ldc             "normalget"
        //   407: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   410: checkcast       Ljava/lang/reflect/Method;
        //   413: astore          get
        //   415: aload           table
        //   417: ldc             "normalset"
        //   419: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   422: checkcast       Ljava/lang/reflect/Method;
        //   425: astore          set
        //   427: aload           table
        //   429: ldc             "indexedget"
        //   431: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   434: checkcast       Ljava/lang/reflect/Method;
        //   437: astore          indexedGet
        //   439: aload           table
        //   441: ldc             "indexedset"
        //   443: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   446: checkcast       Ljava/lang/reflect/Method;
        //   449: astore          indexedSet
        //   451: aconst_null    
        //   452: astore          propertyDesc
        //   454: aload           indexedTag
        //   456: ifnonnull       477
        //   459: new             Lorg/msgpack/template/builder/beans/PropertyDescriptor;
        //   462: dup            
        //   463: aload           propertyName
        //   465: aload           get
        //   467: aload           set
        //   469: invokespecial   org/msgpack/template/builder/beans/PropertyDescriptor.<init>:(Ljava/lang/String;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V
        //   472: astore          propertyDesc
        //   474: goto            518
        //   477: new             Lorg/msgpack/template/builder/beans/IndexedPropertyDescriptor;
        //   480: dup            
        //   481: aload           propertyName
        //   483: aload           get
        //   485: aload           set
        //   487: aload           indexedGet
        //   489: aload           indexedSet
        //   491: invokespecial   org/msgpack/template/builder/beans/IndexedPropertyDescriptor.<init>:(Ljava/lang/String;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V
        //   494: astore          propertyDesc
        //   496: goto            518
        //   499: astore          e
        //   501: new             Lorg/msgpack/template/builder/beans/IndexedPropertyDescriptor;
        //   504: dup            
        //   505: aload           propertyName
        //   507: aconst_null    
        //   508: aconst_null    
        //   509: aload           indexedGet
        //   511: aload           indexedSet
        //   513: invokespecial   org/msgpack/template/builder/beans/IndexedPropertyDescriptor.<init>:(Ljava/lang/String;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V
        //   516: astore          propertyDesc
        //   518: aload_0         /* this */
        //   519: getfield        org/msgpack/template/builder/beans/StandardBeanInfo.canAddPropertyChangeListener:Z
        //   522: ifeq            541
        //   525: aload_0         /* this */
        //   526: getfield        org/msgpack/template/builder/beans/StandardBeanInfo.canRemovePropertyChangeListener:Z
        //   529: ifeq            541
        //   532: aload           propertyDesc
        //   534: iconst_1       
        //   535: invokevirtual   org/msgpack/template/builder/beans/PropertyDescriptor.setBound:(Z)V
        //   538: goto            547
        //   541: aload           propertyDesc
        //   543: iconst_0       
        //   544: invokevirtual   org/msgpack/template/builder/beans/PropertyDescriptor.setBound:(Z)V
        //   547: aload           table
        //   549: ldc             "isConstrained"
        //   551: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   554: getstatic       java/lang/Boolean.TRUE:Ljava/lang/Boolean;
        //   557: if_acmpne       566
        //   560: aload           propertyDesc
        //   562: iconst_1       
        //   563: invokevirtual   org/msgpack/template/builder/beans/PropertyDescriptor.setConstrained:(Z)V
        //   566: aload           propertyList
        //   568: aload           propertyDesc
        //   570: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   573: pop            
        //   574: goto            312
        //   577: aload           propertyList
        //   579: invokevirtual   java/util/ArrayList.size:()I
        //   582: anewarray       Lorg/msgpack/template/builder/beans/PropertyDescriptor;
        //   585: astore          theProperties
        //   587: aload           propertyList
        //   589: aload           theProperties
        //   591: invokevirtual   java/util/ArrayList.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   594: pop            
        //   595: aload           theProperties
        //   597: areturn        
        //    Exceptions:
        //  throws org.msgpack.template.builder.beans.IntrospectionException
        //    Signature:
        //  (Ljava/lang/Class<*>;)[Lorg/msgpack/template/builder/beans/PropertyDescriptor;
        //    StackMapTable: 00 18 FC 00 0B 07 01 4D FD 00 0A 07 01 5A 01 23 FA 00 05 FD 00 1F 01 07 01 4D 06 FD 00 0E 07 01 5F 01 FA 00 27 FF 00 32 00 0D 07 01 70 07 01 51 07 01 4D 07 01 5A 01 07 01 4D 07 01 5F 07 01 4D 07 01 4D 07 01 5A 07 01 4D 01 01 00 00 20 F8 00 05 F9 00 0D FC 00 02 01 FA 00 19 FD 00 14 07 01 5A 07 01 60 FE 00 35 07 01 62 07 01 55 07 01 5F FD 00 24 07 01 55 07 01 55 FF 00 49 00 14 07 01 70 07 01 51 07 01 4D 07 01 5A 01 07 01 4D 07 01 5F 07 01 4D 07 01 5A 07 01 60 07 01 62 07 01 55 07 01 5F 07 01 55 07 01 55 07 01 58 07 01 58 07 01 58 07 01 58 07 01 6D 00 00 55 07 01 69 12 16 05 12 FF 00 0A 00 09 07 01 70 07 01 51 07 01 4D 07 01 5A 01 07 01 4D 07 01 5F 07 01 4D 07 01 5A 00 00
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                       
        //  -----  -----  -----  -----  -----------------------------------------------------------
        //  477    496    499    518    Lorg/msgpack/template/builder/beans/IntrospectionException;
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedGenericType.accept(CoreMetadataFactory.java:653)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:790)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2689)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:667)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:373)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private boolean isInSuper(final MethodDescriptor method, final MethodDescriptor[] excludeMethods) {
        for (final MethodDescriptor m : excludeMethods) {
            if (method.getMethod().equals(m.getMethod())) {
                return true;
            }
        }
        return false;
    }
    
    private void introspectPropertyListener(final Method theMethod) {
        final String methodName = theMethod.getName();
        final Class<?>[] param = theMethod.getParameterTypes();
        if (param.length != 1) {
            return;
        }
        if (methodName.equals("addPropertyChangeListener") && param[0].equals(PropertyChangeListener.class)) {
            this.canAddPropertyChangeListener = true;
        }
        if (methodName.equals("removePropertyChangeListener") && param[0].equals(PropertyChangeListener.class)) {
            this.canRemovePropertyChangeListener = true;
        }
    }
    
    private static void introspectGet(final Method theMethod, final HashMap<String, HashMap> propertyTable) {
        final String methodName = theMethod.getName();
        int prefixLength = 0;
        if (methodName == null) {
            return;
        }
        if (methodName.startsWith("get")) {
            prefixLength = "get".length();
        }
        if (methodName.startsWith("is")) {
            prefixLength = "is".length();
        }
        if (prefixLength == 0) {
            return;
        }
        final String propertyName = Introspector.decapitalize(methodName.substring(prefixLength));
        if (!isValidProperty(propertyName)) {
            return;
        }
        final Class propertyType = theMethod.getReturnType();
        if (propertyType == null || propertyType == Void.TYPE) {
            return;
        }
        if (prefixLength == 2 && propertyType != Boolean.TYPE) {
            return;
        }
        final Class[] paramTypes = theMethod.getParameterTypes();
        if (paramTypes.length > 1 || (paramTypes.length == 1 && paramTypes[0] != Integer.TYPE)) {
            return;
        }
        HashMap table = propertyTable.get(propertyName);
        if (table == null) {
            table = new HashMap();
            propertyTable.put(propertyName, table);
        }
        ArrayList<Method> getters = table.get("getters");
        if (getters == null) {
            getters = new ArrayList<Method>();
            table.put("getters", getters);
        }
        getters.add(theMethod);
    }
    
    private static void introspectSet(final Method theMethod, final HashMap<String, HashMap> propertyTable) {
        final String methodName = theMethod.getName();
        if (methodName == null) {
            return;
        }
        final Class returnType = theMethod.getReturnType();
        if (returnType != Void.TYPE) {
            return;
        }
        if (methodName == null || !methodName.startsWith("set")) {
            return;
        }
        final String propertyName = Introspector.decapitalize(methodName.substring("set".length()));
        if (!isValidProperty(propertyName)) {
            return;
        }
        final Class[] paramTypes = theMethod.getParameterTypes();
        if (paramTypes.length == 0 || paramTypes.length > 2 || (paramTypes.length == 2 && paramTypes[0] != Integer.TYPE)) {
            return;
        }
        HashMap table = propertyTable.get(propertyName);
        if (table == null) {
            table = new HashMap();
            propertyTable.put(propertyName, table);
        }
        ArrayList<Method> setters = table.get("setters");
        if (setters == null) {
            setters = new ArrayList<Method>();
            table.put("setters", setters);
        }
        final Class[] arr$;
        final Class[] exceptions = arr$ = theMethod.getExceptionTypes();
        for (final Class e : arr$) {
            if (e.equals(PropertyVetoException.class)) {
                table.put("isConstrained", Boolean.TRUE);
            }
        }
        setters.add(theMethod);
    }
    
    private void fixGetSet(final HashMap<String, HashMap> propertyTable) throws IntrospectionException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifnonnull       5
        //     4: return         
        //     5: aload_1         /* propertyTable */
        //     6: invokevirtual   java/util/HashMap.entrySet:()Ljava/util/Set;
        //     9: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    14: astore_2        /* i$ */
        //    15: aload_2         /* i$ */
        //    16: invokeinterface java/util/Iterator.hasNext:()Z
        //    21: ifeq            1811
        //    24: aload_2         /* i$ */
        //    25: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    30: checkcast       Ljava/util/Map$Entry;
        //    33: astore_3        /* entry */
        //    34: aload_3         /* entry */
        //    35: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //    40: checkcast       Ljava/util/HashMap;
        //    43: astore          table
        //    45: aload           table
        //    47: ldc             "getters"
        //    49: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    52: checkcast       Ljava/util/ArrayList;
        //    55: astore          getters
        //    57: aload           table
        //    59: ldc             "setters"
        //    61: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    64: checkcast       Ljava/util/ArrayList;
        //    67: astore          setters
        //    69: aconst_null    
        //    70: astore          normalGetter
        //    72: aconst_null    
        //    73: astore          indexedGetter
        //    75: aconst_null    
        //    76: astore          normalSetter
        //    78: aconst_null    
        //    79: astore          indexedSetter
        //    81: aconst_null    
        //    82: astore          normalPropType
        //    84: aconst_null    
        //    85: astore          indexedPropType
        //    87: aload           getters
        //    89: ifnonnull       101
        //    92: new             Ljava/util/ArrayList;
        //    95: dup            
        //    96: invokespecial   java/util/ArrayList.<init>:()V
        //    99: astore          getters
        //   101: aload           setters
        //   103: ifnonnull       115
        //   106: new             Ljava/util/ArrayList;
        //   109: dup            
        //   110: invokespecial   java/util/ArrayList.<init>:()V
        //   113: astore          setters
        //   115: aconst_null    
        //   116: astore          paramTypes
        //   118: aconst_null    
        //   119: astore          methodName
        //   121: aload           getters
        //   123: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //   126: astore          i$
        //   128: aload           i$
        //   130: invokeinterface java/util/Iterator.hasNext:()Z
        //   135: ifeq            261
        //   138: aload           i$
        //   140: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   145: checkcast       Ljava/lang/reflect/Method;
        //   148: astore          getter
        //   150: aload           getter
        //   152: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   155: astore          paramTypes
        //   157: aload           getter
        //   159: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //   162: astore          methodName
        //   164: aload           paramTypes
        //   166: ifnull          175
        //   169: aload           paramTypes
        //   171: arraylength    
        //   172: ifne            194
        //   175: aload           normalGetter
        //   177: ifnull          190
        //   180: aload           methodName
        //   182: ldc             "is"
        //   184: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //   187: ifeq            194
        //   190: aload           getter
        //   192: astore          normalGetter
        //   194: aload           paramTypes
        //   196: ifnull          258
        //   199: aload           paramTypes
        //   201: arraylength    
        //   202: iconst_1       
        //   203: if_icmpne       258
        //   206: aload           paramTypes
        //   208: iconst_0       
        //   209: aaload         
        //   210: getstatic       java/lang/Integer.TYPE:Ljava/lang/Class;
        //   213: if_acmpne       258
        //   216: aload           indexedGetter
        //   218: ifnull          254
        //   221: aload           methodName
        //   223: ldc             "get"
        //   225: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //   228: ifne            254
        //   231: aload           methodName
        //   233: ldc             "is"
        //   235: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //   238: ifeq            258
        //   241: aload           indexedGetter
        //   243: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //   246: ldc             "get"
        //   248: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //   251: ifne            258
        //   254: aload           getter
        //   256: astore          indexedGetter
        //   258: goto            128
        //   261: aload           normalGetter
        //   263: ifnull          340
        //   266: aload           normalGetter
        //   268: invokevirtual   java/lang/reflect/Method.getReturnType:()Ljava/lang/Class;
        //   271: astore          propertyType
        //   273: aload           setters
        //   275: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //   278: astore          i$
        //   280: aload           i$
        //   282: invokeinterface java/util/Iterator.hasNext:()Z
        //   287: ifeq            337
        //   290: aload           i$
        //   292: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   297: checkcast       Ljava/lang/reflect/Method;
        //   300: astore          setter
        //   302: aload           setter
        //   304: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   307: arraylength    
        //   308: iconst_1       
        //   309: if_icmpne       334
        //   312: aload           propertyType
        //   314: aload           setter
        //   316: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   319: iconst_0       
        //   320: aaload         
        //   321: invokevirtual   java/lang/Object.equals:(Ljava/lang/Object;)Z
        //   324: ifeq            334
        //   327: aload           setter
        //   329: astore          normalSetter
        //   331: goto            337
        //   334: goto            280
        //   337: goto            386
        //   340: aload           setters
        //   342: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //   345: astore          i$
        //   347: aload           i$
        //   349: invokeinterface java/util/Iterator.hasNext:()Z
        //   354: ifeq            386
        //   357: aload           i$
        //   359: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   364: checkcast       Ljava/lang/reflect/Method;
        //   367: astore          setter
        //   369: aload           setter
        //   371: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   374: arraylength    
        //   375: iconst_1       
        //   376: if_icmpne       383
        //   379: aload           setter
        //   381: astore          normalSetter
        //   383: goto            347
        //   386: aload           indexedGetter
        //   388: ifnull          478
        //   391: aload           indexedGetter
        //   393: invokevirtual   java/lang/reflect/Method.getReturnType:()Ljava/lang/Class;
        //   396: astore          propertyType
        //   398: aload           setters
        //   400: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //   403: astore          i$
        //   405: aload           i$
        //   407: invokeinterface java/util/Iterator.hasNext:()Z
        //   412: ifeq            475
        //   415: aload           i$
        //   417: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   422: checkcast       Ljava/lang/reflect/Method;
        //   425: astore          setter
        //   427: aload           setter
        //   429: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   432: arraylength    
        //   433: iconst_2       
        //   434: if_icmpne       472
        //   437: aload           setter
        //   439: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   442: iconst_0       
        //   443: aaload         
        //   444: getstatic       java/lang/Integer.TYPE:Ljava/lang/Class;
        //   447: if_acmpne       472
        //   450: aload           propertyType
        //   452: aload           setter
        //   454: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   457: iconst_1       
        //   458: aaload         
        //   459: invokevirtual   java/lang/Object.equals:(Ljava/lang/Object;)Z
        //   462: ifeq            472
        //   465: aload           setter
        //   467: astore          indexedSetter
        //   469: goto            475
        //   472: goto            405
        //   475: goto            537
        //   478: aload           setters
        //   480: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //   483: astore          i$
        //   485: aload           i$
        //   487: invokeinterface java/util/Iterator.hasNext:()Z
        //   492: ifeq            537
        //   495: aload           i$
        //   497: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   502: checkcast       Ljava/lang/reflect/Method;
        //   505: astore          setter
        //   507: aload           setter
        //   509: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   512: arraylength    
        //   513: iconst_2       
        //   514: if_icmpne       534
        //   517: aload           setter
        //   519: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   522: iconst_0       
        //   523: aaload         
        //   524: getstatic       java/lang/Integer.TYPE:Ljava/lang/Class;
        //   527: if_acmpne       534
        //   530: aload           setter
        //   532: astore          indexedSetter
        //   534: goto            485
        //   537: aload           normalGetter
        //   539: ifnull          552
        //   542: aload           normalGetter
        //   544: invokevirtual   java/lang/reflect/Method.getReturnType:()Ljava/lang/Class;
        //   547: astore          normalPropType
        //   549: goto            566
        //   552: aload           normalSetter
        //   554: ifnull          566
        //   557: aload           normalSetter
        //   559: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   562: iconst_0       
        //   563: aaload         
        //   564: astore          normalPropType
        //   566: aload           indexedGetter
        //   568: ifnull          581
        //   571: aload           indexedGetter
        //   573: invokevirtual   java/lang/reflect/Method.getReturnType:()Ljava/lang/Class;
        //   576: astore          indexedPropType
        //   578: goto            595
        //   581: aload           indexedSetter
        //   583: ifnull          595
        //   586: aload           indexedSetter
        //   588: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   591: iconst_1       
        //   592: aaload         
        //   593: astore          indexedPropType
        //   595: aload           normalGetter
        //   597: ifnull          611
        //   600: aload           normalGetter
        //   602: invokevirtual   java/lang/reflect/Method.getReturnType:()Ljava/lang/Class;
        //   605: invokevirtual   java/lang/Class.isArray:()Z
        //   608: ifeq            611
        //   611: aload           normalGetter
        //   613: ifnull          674
        //   616: aload           normalSetter
        //   618: ifnull          674
        //   621: aload           indexedGetter
        //   623: ifnull          631
        //   626: aload           indexedSetter
        //   628: ifnonnull       674
        //   631: aload           table
        //   633: ldc             "normal"
        //   635: ldc             "valid"
        //   637: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   640: pop            
        //   641: aload           table
        //   643: ldc             "normalget"
        //   645: aload           normalGetter
        //   647: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   650: pop            
        //   651: aload           table
        //   653: ldc             "normalset"
        //   655: aload           normalSetter
        //   657: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   660: pop            
        //   661: aload           table
        //   663: ldc             "normalPropertyType"
        //   665: aload           normalPropType
        //   667: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   670: pop            
        //   671: goto            15
        //   674: aload           normalGetter
        //   676: ifnonnull       684
        //   679: aload           normalSetter
        //   681: ifnull          737
        //   684: aload           indexedGetter
        //   686: ifnonnull       737
        //   689: aload           indexedSetter
        //   691: ifnonnull       737
        //   694: aload           table
        //   696: ldc             "normal"
        //   698: ldc             "valid"
        //   700: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   703: pop            
        //   704: aload           table
        //   706: ldc             "normalget"
        //   708: aload           normalGetter
        //   710: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   713: pop            
        //   714: aload           table
        //   716: ldc             "normalset"
        //   718: aload           normalSetter
        //   720: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   723: pop            
        //   724: aload           table
        //   726: ldc             "normalPropertyType"
        //   728: aload           normalPropType
        //   730: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   733: pop            
        //   734: goto            15
        //   737: aload           normalGetter
        //   739: ifnonnull       747
        //   742: aload           normalSetter
        //   744: ifnull          1606
        //   747: aload           indexedGetter
        //   749: ifnonnull       757
        //   752: aload           indexedSetter
        //   754: ifnull          1606
        //   757: aload           normalGetter
        //   759: ifnull          970
        //   762: aload           normalSetter
        //   764: ifnull          970
        //   767: aload           indexedGetter
        //   769: ifnull          970
        //   772: aload           indexedSetter
        //   774: ifnull          970
        //   777: aload           indexedGetter
        //   779: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //   782: ldc             "get"
        //   784: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //   787: ifeq            873
        //   790: aload           table
        //   792: ldc             "normal"
        //   794: ldc             "valid"
        //   796: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   799: pop            
        //   800: aload           table
        //   802: ldc             "normalget"
        //   804: aload           normalGetter
        //   806: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   809: pop            
        //   810: aload           table
        //   812: ldc             "normalset"
        //   814: aload           normalSetter
        //   816: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   819: pop            
        //   820: aload           table
        //   822: ldc             "normalPropertyType"
        //   824: aload           normalPropType
        //   826: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   829: pop            
        //   830: aload           table
        //   832: ldc             "indexed"
        //   834: ldc             "valid"
        //   836: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   839: pop            
        //   840: aload           table
        //   842: ldc             "indexedget"
        //   844: aload           indexedGetter
        //   846: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   849: pop            
        //   850: aload           table
        //   852: ldc             "indexedset"
        //   854: aload           indexedSetter
        //   856: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   859: pop            
        //   860: aload           table
        //   862: ldc             "indexedPropertyType"
        //   864: aload           indexedPropType
        //   866: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   869: pop            
        //   870: goto            15
        //   873: aload           normalPropType
        //   875: getstatic       java/lang/Boolean.TYPE:Ljava/lang/Class;
        //   878: if_acmpeq       927
        //   881: aload           normalGetter
        //   883: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //   886: ldc             "is"
        //   888: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //   891: ifeq            927
        //   894: aload           table
        //   896: ldc             "indexed"
        //   898: ldc             "valid"
        //   900: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   903: pop            
        //   904: aload           table
        //   906: ldc             "indexedset"
        //   908: aload           indexedSetter
        //   910: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   913: pop            
        //   914: aload           table
        //   916: ldc             "indexedPropertyType"
        //   918: aload           indexedPropType
        //   920: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   923: pop            
        //   924: goto            15
        //   927: aload           table
        //   929: ldc             "normal"
        //   931: ldc             "valid"
        //   933: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   936: pop            
        //   937: aload           table
        //   939: ldc             "normalget"
        //   941: aload           normalGetter
        //   943: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   946: pop            
        //   947: aload           table
        //   949: ldc             "normalset"
        //   951: aload           normalSetter
        //   953: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   956: pop            
        //   957: aload           table
        //   959: ldc             "normalPropertyType"
        //   961: aload           normalPropType
        //   963: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   966: pop            
        //   967: goto            15
        //   970: aload           normalGetter
        //   972: ifnull          1086
        //   975: aload           normalSetter
        //   977: ifnonnull       1086
        //   980: aload           indexedGetter
        //   982: ifnull          1086
        //   985: aload           indexedSetter
        //   987: ifnull          1086
        //   990: aload           table
        //   992: ldc             "normal"
        //   994: ldc             "valid"
        //   996: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   999: pop            
        //  1000: aload           table
        //  1002: ldc             "normalget"
        //  1004: aload           normalGetter
        //  1006: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1009: pop            
        //  1010: aload           table
        //  1012: ldc             "normalset"
        //  1014: aload           normalSetter
        //  1016: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1019: pop            
        //  1020: aload           table
        //  1022: ldc             "normalPropertyType"
        //  1024: aload           normalPropType
        //  1026: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1029: pop            
        //  1030: aload           table
        //  1032: ldc             "indexed"
        //  1034: ldc             "valid"
        //  1036: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1039: pop            
        //  1040: aload           indexedGetter
        //  1042: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //  1045: ldc             "get"
        //  1047: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //  1050: ifeq            1063
        //  1053: aload           table
        //  1055: ldc             "indexedget"
        //  1057: aload           indexedGetter
        //  1059: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1062: pop            
        //  1063: aload           table
        //  1065: ldc             "indexedset"
        //  1067: aload           indexedSetter
        //  1069: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1072: pop            
        //  1073: aload           table
        //  1075: ldc             "indexedPropertyType"
        //  1077: aload           indexedPropType
        //  1079: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1082: pop            
        //  1083: goto            15
        //  1086: aload           normalGetter
        //  1088: ifnonnull       1162
        //  1091: aload           normalSetter
        //  1093: ifnull          1162
        //  1096: aload           indexedGetter
        //  1098: ifnull          1162
        //  1101: aload           indexedSetter
        //  1103: ifnull          1162
        //  1106: aload           table
        //  1108: ldc             "indexed"
        //  1110: ldc             "valid"
        //  1112: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1115: pop            
        //  1116: aload           indexedGetter
        //  1118: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //  1121: ldc             "get"
        //  1123: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //  1126: ifeq            1139
        //  1129: aload           table
        //  1131: ldc             "indexedget"
        //  1133: aload           indexedGetter
        //  1135: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1138: pop            
        //  1139: aload           table
        //  1141: ldc             "indexedset"
        //  1143: aload           indexedSetter
        //  1145: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1148: pop            
        //  1149: aload           table
        //  1151: ldc             "indexedPropertyType"
        //  1153: aload           indexedPropType
        //  1155: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1158: pop            
        //  1159: goto            15
        //  1162: aload           normalGetter
        //  1164: ifnull          1321
        //  1167: aload           normalSetter
        //  1169: ifnonnull       1321
        //  1172: aload           indexedGetter
        //  1174: ifnull          1321
        //  1177: aload           indexedSetter
        //  1179: ifnonnull       1321
        //  1182: aload           indexedGetter
        //  1184: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //  1187: ldc             "get"
        //  1189: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //  1192: ifeq            1278
        //  1195: aload           table
        //  1197: ldc             "normal"
        //  1199: ldc             "valid"
        //  1201: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1204: pop            
        //  1205: aload           table
        //  1207: ldc             "normalget"
        //  1209: aload           normalGetter
        //  1211: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1214: pop            
        //  1215: aload           table
        //  1217: ldc             "normalset"
        //  1219: aload           normalSetter
        //  1221: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1224: pop            
        //  1225: aload           table
        //  1227: ldc             "normalPropertyType"
        //  1229: aload           normalPropType
        //  1231: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1234: pop            
        //  1235: aload           table
        //  1237: ldc             "indexed"
        //  1239: ldc             "valid"
        //  1241: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1244: pop            
        //  1245: aload           table
        //  1247: ldc             "indexedget"
        //  1249: aload           indexedGetter
        //  1251: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1254: pop            
        //  1255: aload           table
        //  1257: ldc             "indexedset"
        //  1259: aload           indexedSetter
        //  1261: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1264: pop            
        //  1265: aload           table
        //  1267: ldc             "indexedPropertyType"
        //  1269: aload           indexedPropType
        //  1271: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1274: pop            
        //  1275: goto            15
        //  1278: aload           table
        //  1280: ldc             "normal"
        //  1282: ldc             "valid"
        //  1284: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1287: pop            
        //  1288: aload           table
        //  1290: ldc             "normalget"
        //  1292: aload           normalGetter
        //  1294: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1297: pop            
        //  1298: aload           table
        //  1300: ldc             "normalset"
        //  1302: aload           normalSetter
        //  1304: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1307: pop            
        //  1308: aload           table
        //  1310: ldc             "normalPropertyType"
        //  1312: aload           normalPropType
        //  1314: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1317: pop            
        //  1318: goto            15
        //  1321: aload           normalGetter
        //  1323: ifnonnull       1480
        //  1326: aload           normalSetter
        //  1328: ifnull          1480
        //  1331: aload           indexedGetter
        //  1333: ifnull          1480
        //  1336: aload           indexedSetter
        //  1338: ifnonnull       1480
        //  1341: aload           indexedGetter
        //  1343: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //  1346: ldc             "get"
        //  1348: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //  1351: ifeq            1437
        //  1354: aload           table
        //  1356: ldc             "normal"
        //  1358: ldc             "valid"
        //  1360: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1363: pop            
        //  1364: aload           table
        //  1366: ldc             "normalget"
        //  1368: aload           normalGetter
        //  1370: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1373: pop            
        //  1374: aload           table
        //  1376: ldc             "normalset"
        //  1378: aload           normalSetter
        //  1380: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1383: pop            
        //  1384: aload           table
        //  1386: ldc             "normalPropertyType"
        //  1388: aload           normalPropType
        //  1390: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1393: pop            
        //  1394: aload           table
        //  1396: ldc             "indexed"
        //  1398: ldc             "valid"
        //  1400: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1403: pop            
        //  1404: aload           table
        //  1406: ldc             "indexedget"
        //  1408: aload           indexedGetter
        //  1410: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1413: pop            
        //  1414: aload           table
        //  1416: ldc             "indexedset"
        //  1418: aload           indexedSetter
        //  1420: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1423: pop            
        //  1424: aload           table
        //  1426: ldc             "indexedPropertyType"
        //  1428: aload           indexedPropType
        //  1430: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1433: pop            
        //  1434: goto            15
        //  1437: aload           table
        //  1439: ldc             "normal"
        //  1441: ldc             "valid"
        //  1443: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1446: pop            
        //  1447: aload           table
        //  1449: ldc             "normalget"
        //  1451: aload           normalGetter
        //  1453: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1456: pop            
        //  1457: aload           table
        //  1459: ldc             "normalset"
        //  1461: aload           normalSetter
        //  1463: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1466: pop            
        //  1467: aload           table
        //  1469: ldc             "normalPropertyType"
        //  1471: aload           normalPropType
        //  1473: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1476: pop            
        //  1477: goto            15
        //  1480: aload           normalGetter
        //  1482: ifnull          1543
        //  1485: aload           normalSetter
        //  1487: ifnonnull       1543
        //  1490: aload           indexedGetter
        //  1492: ifnonnull       1543
        //  1495: aload           indexedSetter
        //  1497: ifnull          1543
        //  1500: aload           table
        //  1502: ldc             "indexed"
        //  1504: ldc             "valid"
        //  1506: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1509: pop            
        //  1510: aload           table
        //  1512: ldc             "indexedget"
        //  1514: aload           indexedGetter
        //  1516: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1519: pop            
        //  1520: aload           table
        //  1522: ldc             "indexedset"
        //  1524: aload           indexedSetter
        //  1526: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1529: pop            
        //  1530: aload           table
        //  1532: ldc             "indexedPropertyType"
        //  1534: aload           indexedPropType
        //  1536: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1539: pop            
        //  1540: goto            15
        //  1543: aload           normalGetter
        //  1545: ifnonnull       1606
        //  1548: aload           normalSetter
        //  1550: ifnull          1606
        //  1553: aload           indexedGetter
        //  1555: ifnonnull       1606
        //  1558: aload           indexedSetter
        //  1560: ifnull          1606
        //  1563: aload           table
        //  1565: ldc             "indexed"
        //  1567: ldc             "valid"
        //  1569: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1572: pop            
        //  1573: aload           table
        //  1575: ldc             "indexedget"
        //  1577: aload           indexedGetter
        //  1579: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1582: pop            
        //  1583: aload           table
        //  1585: ldc             "indexedset"
        //  1587: aload           indexedSetter
        //  1589: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1592: pop            
        //  1593: aload           table
        //  1595: ldc             "indexedPropertyType"
        //  1597: aload           indexedPropType
        //  1599: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1602: pop            
        //  1603: goto            15
        //  1606: aload           normalSetter
        //  1608: ifnonnull       1725
        //  1611: aload           normalGetter
        //  1613: ifnonnull       1725
        //  1616: aload           indexedGetter
        //  1618: ifnonnull       1626
        //  1621: aload           indexedSetter
        //  1623: ifnull          1725
        //  1626: aload           indexedGetter
        //  1628: ifnull          1682
        //  1631: aload           indexedGetter
        //  1633: invokevirtual   java/lang/reflect/Method.getName:()Ljava/lang/String;
        //  1636: ldc             "is"
        //  1638: invokevirtual   java/lang/String.startsWith:(Ljava/lang/String;)Z
        //  1641: ifeq            1682
        //  1644: aload           indexedSetter
        //  1646: ifnull          15
        //  1649: aload           table
        //  1651: ldc             "indexed"
        //  1653: ldc             "valid"
        //  1655: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1658: pop            
        //  1659: aload           table
        //  1661: ldc             "indexedset"
        //  1663: aload           indexedSetter
        //  1665: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1668: pop            
        //  1669: aload           table
        //  1671: ldc             "indexedPropertyType"
        //  1673: aload           indexedPropType
        //  1675: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1678: pop            
        //  1679: goto            15
        //  1682: aload           table
        //  1684: ldc             "indexed"
        //  1686: ldc             "valid"
        //  1688: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1691: pop            
        //  1692: aload           table
        //  1694: ldc             "indexedget"
        //  1696: aload           indexedGetter
        //  1698: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1701: pop            
        //  1702: aload           table
        //  1704: ldc             "indexedset"
        //  1706: aload           indexedSetter
        //  1708: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1711: pop            
        //  1712: aload           table
        //  1714: ldc             "indexedPropertyType"
        //  1716: aload           indexedPropType
        //  1718: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1721: pop            
        //  1722: goto            15
        //  1725: aload           normalSetter
        //  1727: ifnonnull       1735
        //  1730: aload           normalGetter
        //  1732: ifnull          1788
        //  1735: aload           indexedGetter
        //  1737: ifnull          1788
        //  1740: aload           indexedSetter
        //  1742: ifnull          1788
        //  1745: aload           table
        //  1747: ldc             "indexed"
        //  1749: ldc             "valid"
        //  1751: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1754: pop            
        //  1755: aload           table
        //  1757: ldc             "indexedget"
        //  1759: aload           indexedGetter
        //  1761: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1764: pop            
        //  1765: aload           table
        //  1767: ldc             "indexedset"
        //  1769: aload           indexedSetter
        //  1771: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1774: pop            
        //  1775: aload           table
        //  1777: ldc             "indexedPropertyType"
        //  1779: aload           indexedPropType
        //  1781: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1784: pop            
        //  1785: goto            15
        //  1788: aload           table
        //  1790: ldc             "normal"
        //  1792: ldc             "invalid"
        //  1794: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1797: pop            
        //  1798: aload           table
        //  1800: ldc             "indexed"
        //  1802: ldc             "invalid"
        //  1804: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1807: pop            
        //  1808: goto            15
        //  1811: return         
        //    Exceptions:
        //  throws org.msgpack.template.builder.beans.IntrospectionException
        //    Signature:
        //  (Ljava/util/HashMap<Ljava/lang/String;Ljava/util/HashMap;>;)V
        //    StackMapTable: 00 37 05 FC 00 09 07 01 60 FF 00 55 00 0D 07 01 70 07 01 5F 07 01 60 07 01 62 07 01 5F 07 01 5A 07 01 5A 07 01 58 07 01 58 07 01 58 07 01 58 07 01 51 07 01 51 00 00 0D FE 00 0C 07 01 4A 07 01 55 07 01 60 FC 00 2E 07 01 58 0E 03 3B FA 00 03 FA 00 02 FD 00 12 07 01 51 07 01 60 35 F9 00 02 02 FC 00 06 07 01 60 23 FA 00 02 FD 00 12 07 01 51 07 01 60 FB 00 42 F9 00 02 02 FC 00 06 07 01 60 30 FA 00 02 0E 0D 0E 0D 0F 13 2A 09 34 09 09 FB 00 73 35 2A FB 00 5C 16 34 16 FB 00 73 2A FB 00 73 2A 3E 3E 13 37 2A 09 34 FF 00 16 00 02 07 01 70 07 01 5F 00 00
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedGenericType.accept(CoreMetadataFactory.java:653)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:790)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2689)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:667)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:373)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private EventSetDescriptor[] introspectEvents() throws IntrospectionException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   org/msgpack/template/builder/beans/StandardBeanInfo.introspectMethods:()[Lorg/msgpack/template/builder/beans/MethodDescriptor;
        //     4: astore_1        /* theMethods */
        //     5: aload_1         /* theMethods */
        //     6: ifnonnull       11
        //     9: aconst_null    
        //    10: areturn        
        //    11: new             Ljava/util/HashMap;
        //    14: dup            
        //    15: aload_1         /* theMethods */
        //    16: arraylength    
        //    17: invokespecial   java/util/HashMap.<init>:(I)V
        //    20: astore_2        /* eventTable */
        //    21: iconst_0       
        //    22: istore_3        /* i */
        //    23: iload_3         /* i */
        //    24: aload_1         /* theMethods */
        //    25: arraylength    
        //    26: if_icmpge       69
        //    29: ldc             "add"
        //    31: aload_1         /* theMethods */
        //    32: iload_3         /* i */
        //    33: aaload         
        //    34: invokevirtual   org/msgpack/template/builder/beans/MethodDescriptor.getMethod:()Ljava/lang/reflect/Method;
        //    37: aload_2         /* eventTable */
        //    38: invokestatic    org/msgpack/template/builder/beans/StandardBeanInfo.introspectListenerMethods:(Ljava/lang/String;Ljava/lang/reflect/Method;Ljava/util/HashMap;)V
        //    41: ldc             "remove"
        //    43: aload_1         /* theMethods */
        //    44: iload_3         /* i */
        //    45: aaload         
        //    46: invokevirtual   org/msgpack/template/builder/beans/MethodDescriptor.getMethod:()Ljava/lang/reflect/Method;
        //    49: aload_2         /* eventTable */
        //    50: invokestatic    org/msgpack/template/builder/beans/StandardBeanInfo.introspectListenerMethods:(Ljava/lang/String;Ljava/lang/reflect/Method;Ljava/util/HashMap;)V
        //    53: aload_1         /* theMethods */
        //    54: iload_3         /* i */
        //    55: aaload         
        //    56: invokevirtual   org/msgpack/template/builder/beans/MethodDescriptor.getMethod:()Ljava/lang/reflect/Method;
        //    59: aload_2         /* eventTable */
        //    60: invokestatic    org/msgpack/template/builder/beans/StandardBeanInfo.introspectGetListenerMethods:(Ljava/lang/reflect/Method;Ljava/util/HashMap;)V
        //    63: iinc            i, 1
        //    66: goto            23
        //    69: new             Ljava/util/ArrayList;
        //    72: dup            
        //    73: invokespecial   java/util/ArrayList.<init>:()V
        //    76: astore_3        /* eventList */
        //    77: aload_2         /* eventTable */
        //    78: invokevirtual   java/util/HashMap.entrySet:()Ljava/util/Set;
        //    81: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    86: astore          i$
        //    88: aload           i$
        //    90: invokeinterface java/util/Iterator.hasNext:()Z
        //    95: ifeq            260
        //    98: aload           i$
        //   100: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   105: checkcast       Ljava/util/Map$Entry;
        //   108: astore          entry
        //   110: aload           entry
        //   112: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //   117: checkcast       Ljava/util/HashMap;
        //   120: astore          table
        //   122: aload           table
        //   124: ldc             "add"
        //   126: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   129: checkcast       Ljava/lang/reflect/Method;
        //   132: astore          add
        //   134: aload           table
        //   136: ldc             "remove"
        //   138: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   141: checkcast       Ljava/lang/reflect/Method;
        //   144: astore          remove
        //   146: aload           add
        //   148: ifnull          88
        //   151: aload           remove
        //   153: ifnonnull       159
        //   156: goto            88
        //   159: aload           table
        //   161: ldc             "get"
        //   163: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   166: checkcast       Ljava/lang/reflect/Method;
        //   169: astore          get
        //   171: aload           table
        //   173: ldc             "listenerType"
        //   175: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   178: checkcast       Ljava/lang/Class;
        //   181: astore          listenerType
        //   183: aload           table
        //   185: ldc             "listenerMethods"
        //   187: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   190: checkcast       [Ljava/lang/reflect/Method;
        //   193: checkcast       [Ljava/lang/reflect/Method;
        //   196: astore          listenerMethods
        //   198: new             Lorg/msgpack/template/builder/beans/EventSetDescriptor;
        //   201: dup            
        //   202: aload           entry
        //   204: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //   209: checkcast       Ljava/lang/String;
        //   212: invokestatic    org/msgpack/template/builder/beans/Introspector.decapitalize:(Ljava/lang/String;)Ljava/lang/String;
        //   215: aload           listenerType
        //   217: aload           listenerMethods
        //   219: aload           add
        //   221: aload           remove
        //   223: aload           get
        //   225: invokespecial   org/msgpack/template/builder/beans/EventSetDescriptor.<init>:(Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V
        //   228: astore          eventSetDescriptor
        //   230: aload           eventSetDescriptor
        //   232: aload           table
        //   234: ldc             "isUnicast"
        //   236: invokevirtual   java/util/HashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   239: ifnull          246
        //   242: iconst_1       
        //   243: goto            247
        //   246: iconst_0       
        //   247: invokevirtual   org/msgpack/template/builder/beans/EventSetDescriptor.setUnicast:(Z)V
        //   250: aload_3         /* eventList */
        //   251: aload           eventSetDescriptor
        //   253: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   256: pop            
        //   257: goto            88
        //   260: aload_3         /* eventList */
        //   261: invokevirtual   java/util/ArrayList.size:()I
        //   264: anewarray       Lorg/msgpack/template/builder/beans/EventSetDescriptor;
        //   267: astore          theEvents
        //   269: aload_3         /* eventList */
        //   270: aload           theEvents
        //   272: invokevirtual   java/util/ArrayList.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   275: pop            
        //   276: aload           theEvents
        //   278: areturn        
        //    Exceptions:
        //  throws org.msgpack.template.builder.beans.IntrospectionException
        //    StackMapTable: 00 08 FC 00 0B 07 01 4D FD 00 0B 07 01 5F 01 FA 00 2D FD 00 12 07 01 5A 07 01 60 FF 00 46 00 09 07 01 70 07 01 4D 07 01 5F 07 01 5A 07 01 60 07 01 62 07 01 5F 07 01 58 07 01 58 00 00 FF 00 56 00 0D 07 01 70 07 01 4D 07 01 5F 07 01 5A 07 01 60 07 01 62 07 01 5F 07 01 58 07 01 58 07 01 58 07 01 51 07 01 4B 07 01 67 00 01 07 01 67 FF 00 00 00 0D 07 01 70 07 01 4D 07 01 5F 07 01 5A 07 01 60 07 01 62 07 01 5F 07 01 58 07 01 58 07 01 58 07 01 51 07 01 4B 07 01 67 00 02 07 01 67 01 FF 00 0C 00 04 07 01 70 07 01 4D 07 01 5F 07 01 5A 00 00
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedGenericType.accept(CoreMetadataFactory.java:653)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:790)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2689)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:667)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:373)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static void introspectListenerMethods(final String type, final Method theMethod, final HashMap<String, HashMap> methodsTable) {
        final String methodName = theMethod.getName();
        if (methodName == null) {
            return;
        }
        if (!methodName.startsWith(type) || !methodName.endsWith("Listener")) {
            return;
        }
        final String listenerName = methodName.substring(type.length());
        final String eventName = listenerName.substring(0, listenerName.lastIndexOf("Listener"));
        if (eventName == null || eventName.length() == 0) {
            return;
        }
        final Class[] paramTypes = theMethod.getParameterTypes();
        if (paramTypes == null || paramTypes.length != 1) {
            return;
        }
        final Class<?> listenerType = paramTypes[0];
        if (!EventListener.class.isAssignableFrom(listenerType)) {
            return;
        }
        if (!listenerType.getName().endsWith(listenerName)) {
            return;
        }
        HashMap table = methodsTable.get(eventName);
        if (table == null) {
            table = new HashMap();
        }
        if (table.get("listenerType") == null) {
            table.put("listenerType", listenerType);
            table.put("listenerMethods", introspectListenerMethods(listenerType));
        }
        table.put(type, theMethod);
        if (type.equals("add")) {
            final Class[] exceptionTypes = theMethod.getExceptionTypes();
            if (exceptionTypes != null) {
                for (int i = 0; i < exceptionTypes.length; ++i) {
                    if (exceptionTypes[i].getName().equals(TooManyListenersException.class.getName())) {
                        table.put("isUnicast", "true");
                        break;
                    }
                }
            }
        }
        methodsTable.put(eventName, table);
    }
    
    private static Method[] introspectListenerMethods(final Class<?> listenerType) {
        final Method[] methods = listenerType.getDeclaredMethods();
        final ArrayList<Method> list = new ArrayList<Method>();
        for (int i = 0; i < methods.length; ++i) {
            final Class<?>[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length == 1) {
                if (EventObject.class.isAssignableFrom(paramTypes[0])) {
                    list.add(methods[i]);
                }
            }
        }
        final Method[] matchedMethods = new Method[list.size()];
        list.toArray(matchedMethods);
        return matchedMethods;
    }
    
    private static void introspectGetListenerMethods(final Method theMethod, final HashMap<String, HashMap> methodsTable) {
        final String type = "get";
        final String methodName = theMethod.getName();
        if (methodName == null) {
            return;
        }
        if (!methodName.startsWith(type) || !methodName.endsWith("Listeners")) {
            return;
        }
        final String listenerName = methodName.substring(type.length(), methodName.length() - 1);
        final String eventName = listenerName.substring(0, listenerName.lastIndexOf("Listener"));
        if (eventName == null || eventName.length() == 0) {
            return;
        }
        final Class[] paramTypes = theMethod.getParameterTypes();
        if (paramTypes == null || paramTypes.length != 0) {
            return;
        }
        final Class returnType = theMethod.getReturnType();
        if (returnType.getComponentType() == null || !returnType.getComponentType().getName().endsWith(listenerName)) {
            return;
        }
        HashMap table = methodsTable.get(eventName);
        if (table == null) {
            table = new HashMap();
        }
        table.put(type, theMethod);
        methodsTable.put(eventName, table);
    }
    
    private static boolean isValidProperty(final String propertyName) {
        return propertyName != null && propertyName.length() != 0;
    }
    
    void init() {
        if (this.events == null) {
            this.events = new EventSetDescriptor[0];
        }
        if (this.properties == null) {
            this.properties = new PropertyDescriptor[0];
        }
        if (this.properties != null) {
            final String defaultPropertyName = (this.defaultPropertyIndex != -1) ? this.properties[this.defaultPropertyIndex].getName() : null;
            Arrays.sort(this.properties, StandardBeanInfo.comparator);
            if (null != defaultPropertyName) {
                for (int i = 0; i < this.properties.length; ++i) {
                    if (defaultPropertyName.equals(this.properties[i].getName())) {
                        this.defaultPropertyIndex = i;
                        break;
                    }
                }
            }
        }
    }
    
    static {
        StandardBeanInfo.comparator = new PropertyComparator();
    }
    
    private static class PropertyComparator implements Comparator<PropertyDescriptor>
    {
        @Override
        public int compare(final PropertyDescriptor object1, final PropertyDescriptor object2) {
            return object1.getName().compareTo(object2.getName());
        }
    }
}
