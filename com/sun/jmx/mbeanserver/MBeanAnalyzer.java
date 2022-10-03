package com.sun.jmx.mbeanserver;

import java.util.Set;
import java.util.Collection;
import java.util.Comparator;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.management.NotCompliantMBeanException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class MBeanAnalyzer<M>
{
    private Map<String, List<M>> opMap;
    private Map<String, AttrMethods<M>> attrMap;
    
    void visit(final MBeanVisitor<M> mBeanVisitor) {
        for (final Map.Entry entry : this.attrMap.entrySet()) {
            final String s = (String)entry.getKey();
            final AttrMethods attrMethods = (AttrMethods)entry.getValue();
            mBeanVisitor.visitAttribute(s, (M)attrMethods.getter, (M)attrMethods.setter);
        }
        for (final Map.Entry entry2 : this.opMap.entrySet()) {
            final Iterator iterator3 = ((List)entry2.getValue()).iterator();
            while (iterator3.hasNext()) {
                mBeanVisitor.visitOperation((String)entry2.getKey(), (M)iterator3.next());
            }
        }
    }
    
    static <M> MBeanAnalyzer<M> analyzer(final Class<?> clazz, final MBeanIntrospector<M> mBeanIntrospector) throws NotCompliantMBeanException {
        return new MBeanAnalyzer<M>(clazz, mBeanIntrospector);
    }
    
    private MBeanAnalyzer(final Class<?> clazz, final MBeanIntrospector<M> mBeanIntrospector) throws NotCompliantMBeanException {
        this.opMap = Util.newInsertionOrderMap();
        this.attrMap = Util.newInsertionOrderMap();
        if (!clazz.isInterface()) {
            throw new NotCompliantMBeanException("Not an interface: " + clazz.getName());
        }
        if (!Modifier.isPublic(clazz.getModifiers()) && !Introspector.ALLOW_NONPUBLIC_MBEAN) {
            throw new NotCompliantMBeanException("Interface is not public: " + clazz.getName());
        }
        try {
            this.initMaps(clazz, mBeanIntrospector);
        }
        catch (final Exception ex) {
            throw Introspector.throwException(clazz, ex);
        }
    }
    
    private void initMaps(final Class<?> clazz, final MBeanIntrospector<M> mBeanIntrospector) throws Exception {
        for (final Method method : eliminateCovariantMethods(mBeanIntrospector.getMethods(clazz))) {
            final String name = method.getName();
            final int length = method.getParameterTypes().length;
            final Object m = mBeanIntrospector.mFrom(method);
            String s = "";
            if (name.startsWith("get")) {
                s = name.substring(3);
            }
            else if (name.startsWith("is") && method.getReturnType() == Boolean.TYPE) {
                s = name.substring(2);
            }
            if (s.length() != 0 && length == 0 && method.getReturnType() != Void.TYPE) {
                AttrMethods attrMethods = this.attrMap.get(s);
                if (attrMethods == null) {
                    attrMethods = new AttrMethods();
                }
                else if (attrMethods.getter != null) {
                    throw new NotCompliantMBeanException("Attribute " + s + " has more than one getter");
                }
                attrMethods.getter = (M)m;
                this.attrMap.put(s, attrMethods);
            }
            else if (name.startsWith("set") && name.length() > 3 && length == 1 && method.getReturnType() == Void.TYPE) {
                final String substring = name.substring(3);
                AttrMethods attrMethods2 = this.attrMap.get(substring);
                if (attrMethods2 == null) {
                    attrMethods2 = new AttrMethods();
                }
                else if (attrMethods2.setter != null) {
                    throw new NotCompliantMBeanException("Attribute " + substring + " has more than one setter");
                }
                attrMethods2.setter = (M)m;
                this.attrMap.put(substring, attrMethods2);
            }
            else {
                List<Object> list = (List<Object>)this.opMap.get(name);
                if (list == null) {
                    list = (List<Object>)Util.newList();
                }
                list.add(m);
                this.opMap.put(name, (List<M>)list);
            }
        }
        for (final Map.Entry entry : this.attrMap.entrySet()) {
            final AttrMethods attrMethods3 = (AttrMethods)entry.getValue();
            if (!mBeanIntrospector.consistent((M)attrMethods3.getter, (M)attrMethods3.setter)) {
                throw new NotCompliantMBeanException("Getter and setter for " + (String)entry.getKey() + " have inconsistent types");
            }
        }
    }
    
    static List<Method> eliminateCovariantMethods(final List<Method> list) {
        final int size = list.size();
        final Method[] array = list.toArray(new Method[size]);
        Arrays.sort(array, MethodOrder.instance);
        final Set<Object> set = Util.newSet();
        for (int i = 1; i < size; ++i) {
            final Method method = array[i - 1];
            final Method method2 = array[i];
            if (method.getName().equals(method2.getName())) {
                if (Arrays.equals(method.getParameterTypes(), method2.getParameterTypes()) && !set.add(method)) {
                    throw new RuntimeException("Internal error: duplicate Method");
                }
            }
        }
        final List<Object> list2 = Util.newList((Collection<Object>)list);
        list2.removeAll(set);
        return (List<Method>)list2;
    }
    
    private static class AttrMethods<M>
    {
        M getter;
        M setter;
    }
    
    private static class MethodOrder implements Comparator<Method>
    {
        public static final MethodOrder instance;
        
        @Override
        public int compare(final Method method, final Method method2) {
            final int compareTo = method.getName().compareTo(method2.getName());
            if (compareTo != 0) {
                return compareTo;
            }
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Class<?>[] parameterTypes2 = method2.getParameterTypes();
            if (parameterTypes.length != parameterTypes2.length) {
                return parameterTypes.length - parameterTypes2.length;
            }
            if (!Arrays.equals(parameterTypes, parameterTypes2)) {
                return Arrays.toString(parameterTypes).compareTo(Arrays.toString(parameterTypes2));
            }
            final Class<?> returnType = method.getReturnType();
            final Class<?> returnType2 = method2.getReturnType();
            if (returnType == returnType2) {
                return 0;
            }
            if (returnType.isAssignableFrom(returnType2)) {
                return -1;
            }
            return 1;
        }
        
        static {
            instance = new MethodOrder();
        }
    }
    
    interface MBeanVisitor<M>
    {
        void visitAttribute(final String p0, final M p1, final M p2);
        
        void visitOperation(final String p0, final M p1);
    }
}
