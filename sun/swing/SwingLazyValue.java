package sun.swing;

import java.awt.Color;
import javax.swing.plaf.ColorUIResource;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import sun.reflect.misc.ReflectUtil;
import javax.swing.UIDefaults;

public class SwingLazyValue implements UIDefaults.LazyValue
{
    private String className;
    private String methodName;
    private Object[] args;
    
    public SwingLazyValue(final String s) {
        this(s, (String)null);
    }
    
    public SwingLazyValue(final String s, final String s2) {
        this(s, s2, null);
    }
    
    public SwingLazyValue(final String s, final Object[] array) {
        this(s, null, array);
    }
    
    public SwingLazyValue(final String className, final String methodName, final Object[] array) {
        this.className = className;
        this.methodName = methodName;
        if (array != null) {
            this.args = array.clone();
        }
    }
    
    @Override
    public Object createValue(final UIDefaults uiDefaults) {
        try {
            ReflectUtil.checkPackageAccess(this.className);
            final Class<?> forName = Class.forName(this.className, true, null);
            if (this.methodName != null) {
                final Method method = forName.getMethod(this.methodName, (Class[])this.getClassArray(this.args));
                this.makeAccessible(method);
                return method.invoke(forName, this.args);
            }
            final Constructor constructor = forName.getConstructor((Class[])this.getClassArray(this.args));
            this.makeAccessible(constructor);
            return constructor.newInstance(this.args);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private void makeAccessible(final AccessibleObject accessibleObject) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                accessibleObject.setAccessible(true);
                return null;
            }
        });
    }
    
    private Class[] getClassArray(final Object[] array) {
        Class[] array2 = null;
        if (array != null) {
            array2 = new Class[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] instanceof Integer) {
                    array2[i] = Integer.TYPE;
                }
                else if (array[i] instanceof Boolean) {
                    array2[i] = Boolean.TYPE;
                }
                else if (array[i] instanceof ColorUIResource) {
                    array2[i] = Color.class;
                }
                else {
                    array2[i] = array[i].getClass();
                }
            }
        }
        return array2;
    }
}
