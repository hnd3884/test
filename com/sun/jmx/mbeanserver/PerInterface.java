package com.sun.jmx.mbeanserver;

import java.util.Collection;
import java.util.Collections;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Iterator;
import java.util.Arrays;
import javax.management.InvalidAttributeValueException;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import java.util.List;
import java.util.Map;
import javax.management.MBeanInfo;

final class PerInterface<M>
{
    private final Class<?> mbeanInterface;
    private final MBeanIntrospector<M> introspector;
    private final MBeanInfo mbeanInfo;
    private final Map<String, M> getters;
    private final Map<String, M> setters;
    private final Map<String, List<MethodAndSig>> ops;
    
    PerInterface(final Class<?> mbeanInterface, final MBeanIntrospector<M> introspector, final MBeanAnalyzer<M> mBeanAnalyzer, final MBeanInfo mbeanInfo) {
        this.getters = Util.newMap();
        this.setters = Util.newMap();
        this.ops = Util.newMap();
        this.mbeanInterface = mbeanInterface;
        this.introspector = introspector;
        this.mbeanInfo = mbeanInfo;
        mBeanAnalyzer.visit(new InitMaps());
    }
    
    Class<?> getMBeanInterface() {
        return this.mbeanInterface;
    }
    
    MBeanInfo getMBeanInfo() {
        return this.mbeanInfo;
    }
    
    boolean isMXBean() {
        return this.introspector.isMXBean();
    }
    
    Object getAttribute(final Object o, final String s, final Object o2) throws AttributeNotFoundException, MBeanException, ReflectionException {
        final M value = this.getters.get(s);
        if (value == null) {
            String s2;
            if (this.setters.containsKey(s)) {
                s2 = "Write-only attribute: " + s;
            }
            else {
                s2 = "No such attribute: " + s;
            }
            throw new AttributeNotFoundException(s2);
        }
        return this.introspector.invokeM(value, o, null, o2);
    }
    
    void setAttribute(final Object o, final String s, final Object o2, final Object o3) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        final M value = this.setters.get(s);
        if (value == null) {
            String s2;
            if (this.getters.containsKey(s)) {
                s2 = "Read-only attribute: " + s;
            }
            else {
                s2 = "No such attribute: " + s;
            }
            throw new AttributeNotFoundException(s2);
        }
        this.introspector.invokeSetter(s, value, o, o2, o3);
    }
    
    Object invoke(final Object o, final String s, final Object[] array, String[] array2, final Object o2) throws MBeanException, ReflectionException {
        final List list = this.ops.get(s);
        if (list == null) {
            return this.noSuchMethod("No such operation: " + s, o, s, array, array2, o2);
        }
        if (array2 == null) {
            array2 = new String[0];
        }
        MethodAndSig methodAndSig = null;
        for (final MethodAndSig methodAndSig2 : list) {
            if (Arrays.equals(methodAndSig2.signature, array2)) {
                methodAndSig = methodAndSig2;
                break;
            }
        }
        if (methodAndSig == null) {
            final String sigString = this.sigString(array2);
            String s2;
            if (list.size() == 1) {
                s2 = "Signature mismatch for operation " + s + ": " + sigString + " should be " + this.sigString(((MethodAndSig)list.get(0)).signature);
            }
            else {
                s2 = "Operation " + s + " exists but not with this signature: " + sigString;
            }
            return this.noSuchMethod(s2, o, s, array, array2, o2);
        }
        return this.introspector.invokeM(methodAndSig.method, o, array, o2);
    }
    
    private Object noSuchMethod(final String s, final Object o, final String s2, final Object[] array, final String[] array2, final Object o2) throws MBeanException, ReflectionException {
        final ReflectionException ex = new ReflectionException(new NoSuchMethodException(s2 + this.sigString(array2)), s);
        if (this.introspector.isMXBean()) {
            throw ex;
        }
        final GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.invoke.getters");
        String s3;
        try {
            s3 = AccessController.doPrivileged((PrivilegedAction<String>)getPropertyAction);
        }
        catch (final Exception ex2) {
            s3 = null;
        }
        if (s3 == null) {
            throw ex;
        }
        int n = 0;
        Map<String, M> map = null;
        if (array2 == null || array2.length == 0) {
            if (s2.startsWith("get")) {
                n = 3;
            }
            else if (s2.startsWith("is")) {
                n = 2;
            }
            if (n != 0) {
                map = this.getters;
            }
        }
        else if (array2.length == 1 && s2.startsWith("set")) {
            n = 3;
            map = this.setters;
        }
        if (n != 0) {
            final M value = map.get(s2.substring(n));
            if (value != null && this.introspector.getName(value).equals(s2)) {
                final String[] signature = this.introspector.getSignature(value);
                if ((array2 == null && signature.length == 0) || Arrays.equals(array2, signature)) {
                    return this.introspector.invokeM(value, o, array, o2);
                }
            }
        }
        throw ex;
    }
    
    private String sigString(final String[] array) {
        final StringBuilder sb = new StringBuilder("(");
        if (array != null) {
            for (final String s : array) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append(s);
            }
        }
        return sb.append(")").toString();
    }
    
    private class InitMaps implements MBeanAnalyzer.MBeanVisitor<M>
    {
        @Override
        public void visitAttribute(final String s, final M m, final M i) {
            if (m != null) {
                PerInterface.this.introspector.checkMethod(m);
                final M put = PerInterface.this.getters.put(s, m);
                assert put == null;
            }
            if (i != null) {
                PerInterface.this.introspector.checkMethod(i);
                final M put2 = PerInterface.this.setters.put(s, i);
                assert put2 == null;
            }
        }
        
        @Override
        public void visitOperation(final String s, final M method) {
            PerInterface.this.introspector.checkMethod(method);
            final String[] signature = PerInterface.this.introspector.getSignature(method);
            final MethodAndSig methodAndSig = new MethodAndSig();
            methodAndSig.method = method;
            methodAndSig.signature = signature;
            List<?> list = PerInterface.this.ops.get(s);
            if (list == null) {
                list = Collections.singletonList(methodAndSig);
            }
            else {
                if (list.size() == 1) {
                    list = Util.newList(list);
                }
                list.add(methodAndSig);
            }
            PerInterface.this.ops.put(s, list);
        }
    }
    
    private class MethodAndSig
    {
        M method;
        String[] signature;
    }
}
