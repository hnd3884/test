package org.owasp.esapi.util;

import java.lang.reflect.Method;
import org.owasp.esapi.errors.ConfigurationException;
import java.lang.reflect.Modifier;

public class ObjFactory
{
    public static <T> T make(final String className, String typeName) throws ConfigurationException {
        Object obj = null;
        String errMsg = null;
        try {
            if (null == className || "".equals(className)) {
                throw new IllegalArgumentException("Classname cannot be null or empty.");
            }
            if (null == typeName || "".equals(typeName)) {
                typeName = "[unknown?]";
            }
            final Class<?> theClass = Class.forName(className);
            try {
                final Method singleton = theClass.getMethod("getInstance", (Class<?>[])new Class[0]);
                if (!Modifier.isStatic(singleton.getModifiers())) {
                    throw new ConfigurationException("Class [" + className + "] contains a non-static getInstance method.");
                }
                obj = singleton.invoke(null, new Object[0]);
            }
            catch (final NoSuchMethodException e) {
                obj = theClass.newInstance();
            }
            catch (final SecurityException e2) {
                throw new ConfigurationException("The SecurityManager has restricted the object factory from getting a reference to the singleton implementationof the class [" + className + "]", e2);
            }
            return (T)obj;
        }
        catch (final IllegalArgumentException ex) {
            errMsg = ex.toString() + " " + typeName + " type name cannot be null or empty.";
            throw new ConfigurationException(errMsg, ex);
        }
        catch (final ClassNotFoundException ex2) {
            errMsg = ex2.toString() + " " + typeName + " class (" + className + ") must be in class path.";
            throw new ConfigurationException(errMsg, ex2);
        }
        catch (final InstantiationException ex3) {
            errMsg = ex3.toString() + " " + typeName + " class (" + className + ") must be concrete.";
            throw new ConfigurationException(errMsg, ex3);
        }
        catch (final IllegalAccessException ex4) {
            errMsg = ex4.toString() + " " + typeName + " class (" + className + ") must have a public, no-arg constructor.";
            throw new ConfigurationException(errMsg, ex4);
        }
        catch (final ClassCastException ex5) {
            errMsg = ex5.toString() + " " + typeName + " class (" + className + ") must be a subtype of T in ObjFactory<T>";
            throw new ConfigurationException(errMsg, ex5);
        }
        catch (final Exception ex6) {
            errMsg = ex6.toString() + " " + typeName + " class (" + className + ") CTOR threw exception.";
            throw new ConfigurationException(errMsg, ex6);
        }
    }
    
    private ObjFactory() {
    }
}
