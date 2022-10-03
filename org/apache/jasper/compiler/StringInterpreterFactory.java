package org.apache.jasper.compiler;

import javax.servlet.ServletContext;

public class StringInterpreterFactory
{
    public static final String STRING_INTERPRETER_CLASS_NAME;
    private static final StringInterpreter DEFAULT_INSTANCE;
    
    public static StringInterpreter getStringInterpreter(final ServletContext context) throws Exception {
        StringInterpreter result = null;
        final Object attribute = context.getAttribute(StringInterpreterFactory.STRING_INTERPRETER_CLASS_NAME);
        if (attribute instanceof StringInterpreter) {
            return (StringInterpreter)attribute;
        }
        if (attribute instanceof String) {
            result = createInstance(context, (String)attribute);
        }
        if (result == null) {
            final String className = context.getInitParameter(StringInterpreterFactory.STRING_INTERPRETER_CLASS_NAME);
            if (className != null) {
                result = createInstance(context, className);
            }
        }
        if (result == null) {
            result = StringInterpreterFactory.DEFAULT_INSTANCE;
        }
        context.setAttribute(StringInterpreterFactory.STRING_INTERPRETER_CLASS_NAME, (Object)result);
        return result;
    }
    
    private static StringInterpreter createInstance(final ServletContext context, final String className) throws Exception {
        return (StringInterpreter)context.getClassLoader().loadClass(className).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
    }
    
    private StringInterpreterFactory() {
    }
    
    static {
        STRING_INTERPRETER_CLASS_NAME = StringInterpreter.class.getName();
        DEFAULT_INSTANCE = new DefaultStringInterpreter();
    }
    
    public static class DefaultStringInterpreter implements StringInterpreter
    {
        @Override
        public String convertString(final Class<?> c, final String s, final String attrName, final Class<?> propEditorClass, final boolean isNamedAttribute) {
            String quoted = s;
            if (!isNamedAttribute) {
                quoted = Generator.quote(s);
            }
            if (propEditorClass != null) {
                final String className = c.getCanonicalName();
                return "(" + className + ")org.apache.jasper.runtime.JspRuntimeLibrary.getValueFromBeanInfoPropertyEditor(" + className + ".class, \"" + attrName + "\", " + quoted + ", " + propEditorClass.getCanonicalName() + ".class)";
            }
            if (c == String.class) {
                return quoted;
            }
            if (c == Boolean.TYPE) {
                return JspUtil.coerceToPrimitiveBoolean(s, isNamedAttribute);
            }
            if (c == Boolean.class) {
                return JspUtil.coerceToBoolean(s, isNamedAttribute);
            }
            if (c == Byte.TYPE) {
                return JspUtil.coerceToPrimitiveByte(s, isNamedAttribute);
            }
            if (c == Byte.class) {
                return JspUtil.coerceToByte(s, isNamedAttribute);
            }
            if (c == Character.TYPE) {
                return JspUtil.coerceToChar(s, isNamedAttribute);
            }
            if (c == Character.class) {
                return JspUtil.coerceToCharacter(s, isNamedAttribute);
            }
            if (c == Double.TYPE) {
                return JspUtil.coerceToPrimitiveDouble(s, isNamedAttribute);
            }
            if (c == Double.class) {
                return JspUtil.coerceToDouble(s, isNamedAttribute);
            }
            if (c == Float.TYPE) {
                return JspUtil.coerceToPrimitiveFloat(s, isNamedAttribute);
            }
            if (c == Float.class) {
                return JspUtil.coerceToFloat(s, isNamedAttribute);
            }
            if (c == Integer.TYPE) {
                return JspUtil.coerceToInt(s, isNamedAttribute);
            }
            if (c == Integer.class) {
                return JspUtil.coerceToInteger(s, isNamedAttribute);
            }
            if (c == Short.TYPE) {
                return JspUtil.coerceToPrimitiveShort(s, isNamedAttribute);
            }
            if (c == Short.class) {
                return JspUtil.coerceToShort(s, isNamedAttribute);
            }
            if (c == Long.TYPE) {
                return JspUtil.coerceToPrimitiveLong(s, isNamedAttribute);
            }
            if (c == Long.class) {
                return JspUtil.coerceToLong(s, isNamedAttribute);
            }
            if (c == Object.class) {
                return quoted;
            }
            final String result = this.coerceToOtherType(c, s, isNamedAttribute);
            if (result != null) {
                return result;
            }
            final String className2 = c.getCanonicalName();
            return "(" + className2 + ")org.apache.jasper.runtime.JspRuntimeLibrary.getValueFromPropertyEditorManager(" + className2 + ".class, \"" + attrName + "\", " + quoted + ")";
        }
        
        protected String coerceToOtherType(final Class<?> c, final String s, final boolean isNamedAttribute) {
            return null;
        }
    }
}
