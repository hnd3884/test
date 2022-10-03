package com.sun.jmx.mbeanserver;

import javax.management.Attribute;
import javax.management.ObjectName;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import java.lang.reflect.Method;
import java.util.Map;

public class MXBeanProxy
{
    private final Map<Method, Handler> handlerMap;
    
    public MXBeanProxy(final Class<?> clazz) {
        this.handlerMap = Util.newMap();
        if (clazz == null) {
            throw new IllegalArgumentException("Null parameter");
        }
        MBeanAnalyzer<ConvertingMethod> analyzer;
        try {
            analyzer = MXBeanIntrospector.getInstance().getAnalyzer(clazz);
        }
        catch (final NotCompliantMBeanException ex) {
            throw new IllegalArgumentException(ex);
        }
        analyzer.visit(new Visitor());
    }
    
    public Object invoke(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Method method, final Object[] array) throws Throwable {
        final Handler handler = this.handlerMap.get(method);
        final ConvertingMethod convertingMethod = handler.getConvertingMethod();
        final MXBeanLookup lookup = MXBeanLookup.lookupFor(mBeanServerConnection);
        final MXBeanLookup lookup2 = MXBeanLookup.getLookup();
        try {
            MXBeanLookup.setLookup(lookup);
            return convertingMethod.fromOpenReturnValue(lookup, handler.invoke(mBeanServerConnection, objectName, convertingMethod.toOpenParameters(lookup, array)));
        }
        finally {
            MXBeanLookup.setLookup(lookup2);
        }
    }
    
    private class Visitor implements MBeanAnalyzer.MBeanVisitor<ConvertingMethod>
    {
        @Override
        public void visitAttribute(final String s, final ConvertingMethod convertingMethod, final ConvertingMethod convertingMethod2) {
            if (convertingMethod != null) {
                convertingMethod.checkCallToOpen();
                MXBeanProxy.this.handlerMap.put(convertingMethod.getMethod(), new GetHandler(s, convertingMethod));
            }
            if (convertingMethod2 != null) {
                MXBeanProxy.this.handlerMap.put(convertingMethod2.getMethod(), new SetHandler(s, convertingMethod2));
            }
        }
        
        @Override
        public void visitOperation(final String s, final ConvertingMethod convertingMethod) {
            convertingMethod.checkCallToOpen();
            MXBeanProxy.this.handlerMap.put(convertingMethod.getMethod(), new InvokeHandler(s, convertingMethod.getOpenSignature(), convertingMethod));
        }
    }
    
    private abstract static class Handler
    {
        private final String name;
        private final ConvertingMethod convertingMethod;
        
        Handler(final String name, final ConvertingMethod convertingMethod) {
            this.name = name;
            this.convertingMethod = convertingMethod;
        }
        
        String getName() {
            return this.name;
        }
        
        ConvertingMethod getConvertingMethod() {
            return this.convertingMethod;
        }
        
        abstract Object invoke(final MBeanServerConnection p0, final ObjectName p1, final Object[] p2) throws Exception;
    }
    
    private static class GetHandler extends Handler
    {
        GetHandler(final String s, final ConvertingMethod convertingMethod) {
            super(s, convertingMethod);
        }
        
        @Override
        Object invoke(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Object[] array) throws Exception {
            assert array.length == 0;
            return mBeanServerConnection.getAttribute(objectName, this.getName());
        }
    }
    
    private static class SetHandler extends Handler
    {
        SetHandler(final String s, final ConvertingMethod convertingMethod) {
            super(s, convertingMethod);
        }
        
        @Override
        Object invoke(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Object[] array) throws Exception {
            assert array.length == 1;
            mBeanServerConnection.setAttribute(objectName, new Attribute(this.getName(), array[0]));
            return null;
        }
    }
    
    private static class InvokeHandler extends Handler
    {
        private final String[] signature;
        
        InvokeHandler(final String s, final String[] signature, final ConvertingMethod convertingMethod) {
            super(s, convertingMethod);
            this.signature = signature;
        }
        
        @Override
        Object invoke(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Object[] array) throws Exception {
            return mBeanServerConnection.invoke(objectName, this.getName(), array, this.signature);
        }
    }
}
