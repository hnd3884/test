package com.sun.xml.internal.ws.policy.privateutil;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.logging.Logger;

class MethodUtil
{
    private static final Logger LOGGER;
    private static final Method INVOKE_METHOD;
    
    static Object invoke(final Object target, final Method method, final Object[] args) throws IllegalAccessException, InvocationTargetException {
        if (MethodUtil.INVOKE_METHOD != null) {
            if (MethodUtil.LOGGER.isLoggable(Level.FINE)) {
                MethodUtil.LOGGER.log(Level.FINE, "Invoking method using sun.reflect.misc.MethodUtil");
            }
            try {
                return MethodUtil.INVOKE_METHOD.invoke(null, method, target, args);
            }
            catch (final InvocationTargetException ite) {
                throw unwrapException(ite);
            }
        }
        if (MethodUtil.LOGGER.isLoggable(Level.FINE)) {
            MethodUtil.LOGGER.log(Level.FINE, "Invoking method directly, probably non-Oracle JVM");
        }
        return method.invoke(target, args);
    }
    
    private static InvocationTargetException unwrapException(final InvocationTargetException ite) {
        final Throwable targetException = ite.getTargetException();
        if (targetException != null && targetException instanceof InvocationTargetException) {
            if (MethodUtil.LOGGER.isLoggable(Level.FINE)) {
                MethodUtil.LOGGER.log(Level.FINE, "Unwrapping invocation target exception");
            }
            return (InvocationTargetException)targetException;
        }
        return ite;
    }
    
    static {
        LOGGER = Logger.getLogger(MethodUtil.class.getName());
        Method method;
        try {
            final Class<?> clazz = Class.forName("sun.reflect.misc.MethodUtil");
            method = clazz.getMethod("invoke", Method.class, Object.class, Object[].class);
            if (MethodUtil.LOGGER.isLoggable(Level.FINE)) {
                MethodUtil.LOGGER.log(Level.FINE, "Class sun.reflect.misc.MethodUtil found; it will be used to invoke methods.");
            }
        }
        catch (final Throwable t) {
            method = null;
            if (MethodUtil.LOGGER.isLoggable(Level.FINE)) {
                MethodUtil.LOGGER.log(Level.FINE, "Class sun.reflect.misc.MethodUtil not found, probably non-Oracle JVM");
            }
        }
        INVOKE_METHOD = method;
    }
}
