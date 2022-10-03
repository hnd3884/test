package org.msgpack.template.builder.beans;

import java.util.TooManyListenersException;
import java.util.Iterator;
import org.apache.harmony.beans.internal.nls.Messages;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EventSetDescriptor extends FeatureDescriptor
{
    private Class<?> listenerType;
    private ArrayList<MethodDescriptor> listenerMethodDescriptors;
    private Method[] listenerMethods;
    private Method getListenerMethod;
    private Method addListenerMethod;
    private Method removeListenerMethod;
    private boolean unicast;
    private boolean inDefaultEventSet;
    
    public EventSetDescriptor(final Class<?> sourceClass, final String eventSetName, final Class<?> listenerType, final String listenerMethodName) throws IntrospectionException {
        this.inDefaultEventSet = true;
        this.checkNotNull(sourceClass, eventSetName, listenerType, listenerMethodName);
        this.setName(eventSetName);
        this.listenerType = listenerType;
        final Method method = this.findListenerMethodByName(listenerMethodName);
        checkEventType(eventSetName, method);
        (this.listenerMethodDescriptors = new ArrayList<MethodDescriptor>()).add(new MethodDescriptor(method));
        this.addListenerMethod = this.findMethodByPrefix(sourceClass, "add", "");
        this.removeListenerMethod = this.findMethodByPrefix(sourceClass, "remove", "");
        if (this.addListenerMethod == null || this.removeListenerMethod == null) {
            throw new IntrospectionException(Messages.getString("custom.beans.38"));
        }
        this.getListenerMethod = this.findMethodByPrefix(sourceClass, "get", "s");
        this.unicast = isUnicastByDefault(this.addListenerMethod);
    }
    
    public EventSetDescriptor(final Class<?> sourceClass, final String eventSetName, final Class<?> listenerType, final String[] listenerMethodNames, final String addListenerMethodName, final String removeListenerMethodName) throws IntrospectionException {
        this(sourceClass, eventSetName, listenerType, listenerMethodNames, addListenerMethodName, removeListenerMethodName, null);
    }
    
    public EventSetDescriptor(final Class<?> sourceClass, final String eventSetName, final Class<?> listenerType, final String[] listenerMethodNames, final String addListenerMethodName, final String removeListenerMethodName, final String getListenerMethodName) throws IntrospectionException {
        this.inDefaultEventSet = true;
        this.checkNotNull(sourceClass, eventSetName, listenerType, listenerMethodNames);
        this.setName(eventSetName);
        this.listenerType = listenerType;
        this.listenerMethodDescriptors = new ArrayList<MethodDescriptor>();
        for (final String element : listenerMethodNames) {
            final Method m = this.findListenerMethodByName(element);
            this.listenerMethodDescriptors.add(new MethodDescriptor(m));
        }
        if (addListenerMethodName != null) {
            this.addListenerMethod = this.findAddRemoveListenerMethod(sourceClass, addListenerMethodName);
        }
        if (removeListenerMethodName != null) {
            this.removeListenerMethod = this.findAddRemoveListenerMethod(sourceClass, removeListenerMethodName);
        }
        if (getListenerMethodName != null) {
            this.getListenerMethod = this.findGetListenerMethod(sourceClass, getListenerMethodName);
        }
        this.unicast = isUnicastByDefault(this.addListenerMethod);
    }
    
    private Method findListenerMethodByName(final String listenerMethodName) throws IntrospectionException {
        Method result = null;
        final Method[] arr$;
        final Method[] methods = arr$ = this.listenerType.getMethods();
        for (final Method method : arr$) {
            if (listenerMethodName.equals(method.getName())) {
                final Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1 && paramTypes[0].getName().endsWith("Event")) {
                    result = method;
                    break;
                }
            }
        }
        if (null == result) {
            throw new IntrospectionException(Messages.getString("custom.beans.31", listenerMethodName, this.listenerType.getName()));
        }
        return result;
    }
    
    public EventSetDescriptor(final String eventSetName, final Class<?> listenerType, final Method[] listenerMethods, final Method addListenerMethod, final Method removeListenerMethod) throws IntrospectionException {
        this(eventSetName, listenerType, listenerMethods, addListenerMethod, removeListenerMethod, null);
    }
    
    public EventSetDescriptor(final String eventSetName, final Class<?> listenerType, final Method[] listenerMethods, final Method addListenerMethod, final Method removeListenerMethod, final Method getListenerMethod) throws IntrospectionException {
        this.inDefaultEventSet = true;
        this.setName(eventSetName);
        this.listenerType = listenerType;
        this.listenerMethods = listenerMethods;
        if (listenerMethods != null) {
            this.listenerMethodDescriptors = new ArrayList<MethodDescriptor>();
            for (final Method element : listenerMethods) {
                this.listenerMethodDescriptors.add(new MethodDescriptor(element));
            }
        }
        this.addListenerMethod = addListenerMethod;
        this.removeListenerMethod = removeListenerMethod;
        this.getListenerMethod = getListenerMethod;
        this.unicast = isUnicastByDefault(addListenerMethod);
    }
    
    public EventSetDescriptor(final String eventSetName, final Class<?> listenerType, final MethodDescriptor[] listenerMethodDescriptors, final Method addListenerMethod, final Method removeListenerMethod) throws IntrospectionException {
        this(eventSetName, listenerType, null, addListenerMethod, removeListenerMethod, null);
        if (listenerMethodDescriptors != null) {
            this.listenerMethodDescriptors = new ArrayList<MethodDescriptor>();
            for (final MethodDescriptor element : listenerMethodDescriptors) {
                this.listenerMethodDescriptors.add(element);
            }
        }
    }
    
    private void checkNotNull(final Object sourceClass, final Object eventSetName, final Object alistenerType, final Object listenerMethodName) {
        if (sourceClass == null) {
            throw new NullPointerException(Messages.getString("custom.beans.0C"));
        }
        if (eventSetName == null) {
            throw new NullPointerException(Messages.getString("custom.beans.53"));
        }
        if (alistenerType == null) {
            throw new NullPointerException(Messages.getString("custom.beans.54"));
        }
        if (listenerMethodName == null) {
            throw new NullPointerException(Messages.getString("custom.beans.52"));
        }
    }
    
    private static void checkEventType(final String eventSetName, final Method listenerMethod) throws IntrospectionException {
        final Class<?>[] params = listenerMethod.getParameterTypes();
        String firstParamTypeName = null;
        final String eventTypeName = prepareEventTypeName(eventSetName);
        if (params.length > 0) {
            firstParamTypeName = extractShortClassName(params[0].getName());
        }
        if (firstParamTypeName == null || !firstParamTypeName.equals(eventTypeName)) {
            throw new IntrospectionException(Messages.getString("custom.beans.51", listenerMethod.getName(), eventTypeName));
        }
    }
    
    private static String extractShortClassName(final String fullClassName) {
        int k = fullClassName.lastIndexOf(36);
        k = ((k == -1) ? fullClassName.lastIndexOf(46) : k);
        return fullClassName.substring(k + 1);
    }
    
    private static String prepareEventTypeName(final String eventSetName) {
        final StringBuilder sb = new StringBuilder();
        if (eventSetName != null && eventSetName.length() > 0) {
            sb.append(Character.toUpperCase(eventSetName.charAt(0)));
            if (eventSetName.length() > 1) {
                sb.append(eventSetName.substring(1));
            }
        }
        sb.append("Event");
        return sb.toString();
    }
    
    public Method[] getListenerMethods() {
        if (this.listenerMethods != null) {
            return this.listenerMethods;
        }
        if (this.listenerMethodDescriptors != null) {
            this.listenerMethods = new Method[this.listenerMethodDescriptors.size()];
            int index = 0;
            for (final MethodDescriptor md : this.listenerMethodDescriptors) {
                this.listenerMethods[index++] = md.getMethod();
            }
            return this.listenerMethods;
        }
        return null;
    }
    
    public MethodDescriptor[] getListenerMethodDescriptors() {
        return (MethodDescriptor[])((this.listenerMethodDescriptors == null) ? null : ((MethodDescriptor[])this.listenerMethodDescriptors.toArray(new MethodDescriptor[0])));
    }
    
    public Method getRemoveListenerMethod() {
        return this.removeListenerMethod;
    }
    
    public Method getGetListenerMethod() {
        return this.getListenerMethod;
    }
    
    public Method getAddListenerMethod() {
        return this.addListenerMethod;
    }
    
    public Class<?> getListenerType() {
        return this.listenerType;
    }
    
    public void setUnicast(final boolean unicast) {
        this.unicast = unicast;
    }
    
    public void setInDefaultEventSet(final boolean inDefaultEventSet) {
        this.inDefaultEventSet = inDefaultEventSet;
    }
    
    public boolean isUnicast() {
        return this.unicast;
    }
    
    public boolean isInDefaultEventSet() {
        return this.inDefaultEventSet;
    }
    
    private Method findAddRemoveListenerMethod(final Class<?> sourceClass, final String methodName) throws IntrospectionException {
        try {
            return sourceClass.getMethod(methodName, this.listenerType);
        }
        catch (final NoSuchMethodException e) {
            return this.findAddRemoveListnerMethodWithLessCheck(sourceClass, methodName);
        }
        catch (final Exception e2) {
            throw new IntrospectionException(Messages.getString("custom.beans.31", methodName, this.listenerType.getName()));
        }
    }
    
    private Method findAddRemoveListnerMethodWithLessCheck(final Class<?> sourceClass, final String methodName) throws IntrospectionException {
        final Method[] methods = sourceClass.getMethods();
        Method result = null;
        for (final Method method : methods) {
            if (method.getName().equals(methodName)) {
                final Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1) {
                    result = method;
                    break;
                }
            }
        }
        if (null == result) {
            throw new IntrospectionException(Messages.getString("custom.beans.31", methodName, this.listenerType.getName()));
        }
        return result;
    }
    
    private Method findGetListenerMethod(final Class<?> sourceClass, final String methodName) {
        try {
            return sourceClass.getMethod(methodName, (Class<?>[])new Class[0]);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private Method findMethodByPrefix(final Class<?> sourceClass, final String prefix, final String postfix) {
        String shortName = this.listenerType.getName();
        if (this.listenerType.getPackage() != null) {
            shortName = shortName.substring(this.listenerType.getPackage().getName().length() + 1);
        }
        final String methodName = prefix + shortName + postfix;
        try {
            if ("get".equals(prefix)) {
                return sourceClass.getMethod(methodName, (Class<?>[])new Class[0]);
            }
        }
        catch (final NoSuchMethodException nsme) {
            return null;
        }
        final Method[] methods = sourceClass.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (methods[i].getName().equals(methodName)) {
                final Class<?>[] paramTypes = methods[i].getParameterTypes();
                if (paramTypes.length == 1) {
                    return methods[i];
                }
            }
        }
        return null;
    }
    
    private static boolean isUnicastByDefault(final Method addMethod) {
        if (addMethod != null) {
            final Class[] arr$;
            final Class<?>[] exceptionTypes = arr$ = addMethod.getExceptionTypes();
            for (final Class<?> element : arr$) {
                if (element.equals(TooManyListenersException.class)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    void merge(final EventSetDescriptor event) {
        super.merge(event);
        if (this.addListenerMethod == null) {
            this.addListenerMethod = event.addListenerMethod;
        }
        if (this.getListenerMethod == null) {
            this.getListenerMethod = event.getListenerMethod;
        }
        if (this.listenerMethodDescriptors == null) {
            this.listenerMethodDescriptors = event.listenerMethodDescriptors;
        }
        if (this.listenerMethods == null) {
            this.listenerMethods = event.listenerMethods;
        }
        if (this.listenerType == null) {
            this.listenerType = event.listenerType;
        }
        if (this.removeListenerMethod == null) {
            this.removeListenerMethod = event.removeListenerMethod;
        }
        this.inDefaultEventSet &= event.inDefaultEventSet;
    }
}
