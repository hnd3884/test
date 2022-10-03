package java.beans;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.ref.Reference;

public class EventSetDescriptor extends FeatureDescriptor
{
    private MethodDescriptor[] listenerMethodDescriptors;
    private MethodDescriptor addMethodDescriptor;
    private MethodDescriptor removeMethodDescriptor;
    private MethodDescriptor getMethodDescriptor;
    private Reference<Method[]> listenerMethodsRef;
    private Reference<? extends Class<?>> listenerTypeRef;
    private boolean unicast;
    private boolean inDefaultEventSet;
    
    public EventSetDescriptor(final Class<?> clazz, final String s, final Class<?> clazz2, final String s2) throws IntrospectionException {
        this(clazz, s, clazz2, new String[] { s2 }, "add" + getListenerClassName(clazz2), "remove" + getListenerClassName(clazz2), "get" + getListenerClassName(clazz2) + "s");
        final String string = NameGenerator.capitalize(s) + "Event";
        final Method[] listenerMethods = this.getListenerMethods();
        if (listenerMethods.length > 0) {
            final Class<?>[] parameterTypes = FeatureDescriptor.getParameterTypes(this.getClass0(), listenerMethods[0]);
            if (!"vetoableChange".equals(s) && !parameterTypes[0].getName().endsWith(string)) {
                throw new IntrospectionException("Method \"" + s2 + "\" should have argument \"" + string + "\"");
            }
        }
    }
    
    private static String getListenerClassName(final Class<?> clazz) {
        final String name = clazz.getName();
        return name.substring(name.lastIndexOf(46) + 1);
    }
    
    public EventSetDescriptor(final Class<?> clazz, final String s, final Class<?> clazz2, final String[] array, final String s2, final String s3) throws IntrospectionException {
        this(clazz, s, clazz2, array, s2, s3, null);
    }
    
    public EventSetDescriptor(final Class<?> class0, final String name, final Class<?> listenerType, final String[] array, final String s, final String s2, final String s3) throws IntrospectionException {
        this.inDefaultEventSet = true;
        if (class0 == null || name == null || listenerType == null) {
            throw new NullPointerException();
        }
        this.setName(name);
        this.setClass0(class0);
        this.setListenerType(listenerType);
        final Method[] listenerMethods = new Method[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new NullPointerException();
            }
            listenerMethods[i] = getMethod(listenerType, array[i], 1);
        }
        this.setListenerMethods(listenerMethods);
        this.setAddListenerMethod(getMethod(class0, s, 1));
        this.setRemoveListenerMethod(getMethod(class0, s2, 1));
        final Method method = Introspector.findMethod(class0, s3, 0);
        if (method != null) {
            this.setGetListenerMethod(method);
        }
    }
    
    private static Method getMethod(final Class<?> clazz, final String s, final int n) throws IntrospectionException {
        if (s == null) {
            return null;
        }
        final Method method = Introspector.findMethod(clazz, s, n);
        if (method == null || Modifier.isStatic(method.getModifiers())) {
            throw new IntrospectionException("Method not found: " + s + " on class " + clazz.getName());
        }
        return method;
    }
    
    public EventSetDescriptor(final String s, final Class<?> clazz, final Method[] array, final Method method, final Method method2) throws IntrospectionException {
        this(s, clazz, array, method, method2, null);
    }
    
    public EventSetDescriptor(final String name, final Class<?> listenerType, final Method[] listenerMethods, final Method addListenerMethod, final Method removeListenerMethod, final Method getListenerMethod) throws IntrospectionException {
        this.inDefaultEventSet = true;
        this.setName(name);
        this.setListenerMethods(listenerMethods);
        this.setAddListenerMethod(addListenerMethod);
        this.setRemoveListenerMethod(removeListenerMethod);
        this.setGetListenerMethod(getListenerMethod);
        this.setListenerType(listenerType);
    }
    
    public EventSetDescriptor(final String name, final Class<?> listenerType, final MethodDescriptor[] array, final Method addListenerMethod, final Method removeListenerMethod) throws IntrospectionException {
        this.inDefaultEventSet = true;
        this.setName(name);
        this.listenerMethodDescriptors = (MethodDescriptor[])((array != null) ? ((MethodDescriptor[])array.clone()) : null);
        this.setAddListenerMethod(addListenerMethod);
        this.setRemoveListenerMethod(removeListenerMethod);
        this.setListenerType(listenerType);
    }
    
    public Class<?> getListenerType() {
        return (this.listenerTypeRef != null) ? ((Class)this.listenerTypeRef.get()) : null;
    }
    
    private void setListenerType(final Class<?> clazz) {
        this.listenerTypeRef = FeatureDescriptor.getWeakReference(clazz);
    }
    
    public synchronized Method[] getListenerMethods() {
        Method[] listenerMethods0 = this.getListenerMethods0();
        if (listenerMethods0 == null) {
            if (this.listenerMethodDescriptors != null) {
                listenerMethods0 = new Method[this.listenerMethodDescriptors.length];
                for (int i = 0; i < listenerMethods0.length; ++i) {
                    listenerMethods0[i] = this.listenerMethodDescriptors[i].getMethod();
                }
            }
            this.setListenerMethods(listenerMethods0);
        }
        return listenerMethods0;
    }
    
    private void setListenerMethods(final Method[] array) {
        if (array == null) {
            return;
        }
        if (this.listenerMethodDescriptors == null) {
            this.listenerMethodDescriptors = new MethodDescriptor[array.length];
            for (int i = 0; i < array.length; ++i) {
                this.listenerMethodDescriptors[i] = new MethodDescriptor(array[i]);
            }
        }
        this.listenerMethodsRef = FeatureDescriptor.getSoftReference(array);
    }
    
    private Method[] getListenerMethods0() {
        return (Method[])((this.listenerMethodsRef != null) ? ((Method[])this.listenerMethodsRef.get()) : null);
    }
    
    public synchronized MethodDescriptor[] getListenerMethodDescriptors() {
        return (MethodDescriptor[])((this.listenerMethodDescriptors != null) ? ((MethodDescriptor[])this.listenerMethodDescriptors.clone()) : null);
    }
    
    public synchronized Method getAddListenerMethod() {
        return getMethod(this.addMethodDescriptor);
    }
    
    private synchronized void setAddListenerMethod(final Method method) {
        if (method == null) {
            return;
        }
        if (this.getClass0() == null) {
            this.setClass0(method.getDeclaringClass());
        }
        this.addMethodDescriptor = new MethodDescriptor(method);
        this.setTransient(method.getAnnotation(Transient.class));
    }
    
    public synchronized Method getRemoveListenerMethod() {
        return getMethod(this.removeMethodDescriptor);
    }
    
    private synchronized void setRemoveListenerMethod(final Method method) {
        if (method == null) {
            return;
        }
        if (this.getClass0() == null) {
            this.setClass0(method.getDeclaringClass());
        }
        this.removeMethodDescriptor = new MethodDescriptor(method);
        this.setTransient(method.getAnnotation(Transient.class));
    }
    
    public synchronized Method getGetListenerMethod() {
        return getMethod(this.getMethodDescriptor);
    }
    
    private synchronized void setGetListenerMethod(final Method method) {
        if (method == null) {
            return;
        }
        if (this.getClass0() == null) {
            this.setClass0(method.getDeclaringClass());
        }
        this.getMethodDescriptor = new MethodDescriptor(method);
        this.setTransient(method.getAnnotation(Transient.class));
    }
    
    public void setUnicast(final boolean unicast) {
        this.unicast = unicast;
    }
    
    public boolean isUnicast() {
        return this.unicast;
    }
    
    public void setInDefaultEventSet(final boolean inDefaultEventSet) {
        this.inDefaultEventSet = inDefaultEventSet;
    }
    
    public boolean isInDefaultEventSet() {
        return this.inDefaultEventSet;
    }
    
    EventSetDescriptor(final EventSetDescriptor eventSetDescriptor, final EventSetDescriptor eventSetDescriptor2) {
        super(eventSetDescriptor, eventSetDescriptor2);
        this.inDefaultEventSet = true;
        this.listenerMethodDescriptors = eventSetDescriptor.listenerMethodDescriptors;
        if (eventSetDescriptor2.listenerMethodDescriptors != null) {
            this.listenerMethodDescriptors = eventSetDescriptor2.listenerMethodDescriptors;
        }
        this.listenerTypeRef = eventSetDescriptor.listenerTypeRef;
        if (eventSetDescriptor2.listenerTypeRef != null) {
            this.listenerTypeRef = eventSetDescriptor2.listenerTypeRef;
        }
        this.addMethodDescriptor = eventSetDescriptor.addMethodDescriptor;
        if (eventSetDescriptor2.addMethodDescriptor != null) {
            this.addMethodDescriptor = eventSetDescriptor2.addMethodDescriptor;
        }
        this.removeMethodDescriptor = eventSetDescriptor.removeMethodDescriptor;
        if (eventSetDescriptor2.removeMethodDescriptor != null) {
            this.removeMethodDescriptor = eventSetDescriptor2.removeMethodDescriptor;
        }
        this.getMethodDescriptor = eventSetDescriptor.getMethodDescriptor;
        if (eventSetDescriptor2.getMethodDescriptor != null) {
            this.getMethodDescriptor = eventSetDescriptor2.getMethodDescriptor;
        }
        this.unicast = eventSetDescriptor2.unicast;
        if (!eventSetDescriptor.inDefaultEventSet || !eventSetDescriptor2.inDefaultEventSet) {
            this.inDefaultEventSet = false;
        }
    }
    
    EventSetDescriptor(final EventSetDescriptor eventSetDescriptor) {
        super(eventSetDescriptor);
        this.inDefaultEventSet = true;
        if (eventSetDescriptor.listenerMethodDescriptors != null) {
            final int length = eventSetDescriptor.listenerMethodDescriptors.length;
            this.listenerMethodDescriptors = new MethodDescriptor[length];
            for (int i = 0; i < length; ++i) {
                this.listenerMethodDescriptors[i] = new MethodDescriptor(eventSetDescriptor.listenerMethodDescriptors[i]);
            }
        }
        this.listenerTypeRef = eventSetDescriptor.listenerTypeRef;
        this.addMethodDescriptor = eventSetDescriptor.addMethodDescriptor;
        this.removeMethodDescriptor = eventSetDescriptor.removeMethodDescriptor;
        this.getMethodDescriptor = eventSetDescriptor.getMethodDescriptor;
        this.unicast = eventSetDescriptor.unicast;
        this.inDefaultEventSet = eventSetDescriptor.inDefaultEventSet;
    }
    
    @Override
    void appendTo(final StringBuilder sb) {
        FeatureDescriptor.appendTo(sb, "unicast", this.unicast);
        FeatureDescriptor.appendTo(sb, "inDefaultEventSet", this.inDefaultEventSet);
        FeatureDescriptor.appendTo(sb, "listenerType", this.listenerTypeRef);
        FeatureDescriptor.appendTo(sb, "getListenerMethod", getMethod(this.getMethodDescriptor));
        FeatureDescriptor.appendTo(sb, "addListenerMethod", getMethod(this.addMethodDescriptor));
        FeatureDescriptor.appendTo(sb, "removeListenerMethod", getMethod(this.removeMethodDescriptor));
    }
    
    private static Method getMethod(final MethodDescriptor methodDescriptor) {
        return (methodDescriptor != null) ? methodDescriptor.getMethod() : null;
    }
}
