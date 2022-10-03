package org.apache.xmlbeans.impl.jam;

import java.util.Arrays;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Comparator;

public class JamUtils
{
    private static final JamUtils INSTANCE;
    private static Comparator SOURCE_POSITION_COMPARATOR;
    
    public static final JamUtils getInstance() {
        return JamUtils.INSTANCE;
    }
    
    private JamUtils() {
    }
    
    public Method getMethodOn(final JMethod method, final Class containedin) throws NoSuchMethodException, ClassNotFoundException {
        if (containedin == null) {
            throw new IllegalArgumentException("null class");
        }
        if (method == null) {
            throw new IllegalArgumentException("null method");
        }
        Class[] types = null;
        final JParameter[] params = method.getParameters();
        if (params != null && params.length > 0) {
            types = new Class[params.length];
            for (int i = 0; i < types.length; ++i) {
                types[i] = this.loadClass(params[i].getType(), containedin.getClassLoader());
            }
        }
        return containedin.getMethod(method.getSimpleName(), (Class[])types);
    }
    
    public Constructor getConstructorOn(final JConstructor ctor, final Class containedin) throws NoSuchMethodException, ClassNotFoundException {
        if (containedin == null) {
            throw new IllegalArgumentException("null class");
        }
        if (ctor == null) {
            throw new IllegalArgumentException("null ctor");
        }
        Class[] types = null;
        final JParameter[] params = ctor.getParameters();
        if (params != null && params.length > 0) {
            types = new Class[params.length];
            for (int i = 0; i < types.length; ++i) {
                types[i] = this.loadClass(params[i].getType(), containedin.getClassLoader());
            }
        }
        return containedin.getConstructor((Class[])types);
    }
    
    public Field getFieldOn(final JField field, final Class containedin) throws NoSuchFieldException {
        if (containedin == null) {
            throw new IllegalArgumentException("null class");
        }
        if (field == null) {
            throw new IllegalArgumentException("null field");
        }
        return containedin.getField(field.getSimpleName());
    }
    
    public Class loadClass(final JClass clazz, final ClassLoader inThisClassloader) throws ClassNotFoundException {
        return inThisClassloader.loadClass(clazz.getQualifiedName());
    }
    
    public void placeInSourceOrder(final JElement[] elements) {
        if (elements == null) {
            throw new IllegalArgumentException("null elements");
        }
        Arrays.sort(elements, JamUtils.SOURCE_POSITION_COMPARATOR);
    }
    
    static {
        INSTANCE = new JamUtils();
        JamUtils.SOURCE_POSITION_COMPARATOR = new Comparator() {
            @Override
            public int compare(final Object o, final Object o1) {
                final JSourcePosition p1 = ((JElement)o).getSourcePosition();
                final JSourcePosition p2 = ((JElement)o1).getSourcePosition();
                if (p1 == null) {
                    return (p2 == null) ? 0 : -1;
                }
                if (p2 == null) {
                    return 1;
                }
                return (p1.getLine() < p2.getLine()) ? -1 : ((p1.getLine() > p2.getLine()) ? 1 : 0);
            }
        };
    }
}
