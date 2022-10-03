package java.beans;

import java.io.InputStream;
import java.applet.AppletStub;
import java.applet.AppletContext;
import java.net.URL;
import java.applet.Applet;
import java.lang.reflect.Modifier;
import com.sun.beans.finder.ClassFinder;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.beans.beancontext.BeanContext;

public class Beans
{
    public static Object instantiate(final ClassLoader classLoader, final String s) throws IOException, ClassNotFoundException {
        return instantiate(classLoader, s, null, null);
    }
    
    public static Object instantiate(final ClassLoader classLoader, final String s, final BeanContext beanContext) throws IOException, ClassNotFoundException {
        return instantiate(classLoader, s, beanContext, null);
    }
    
    public static Object instantiate(ClassLoader systemClassLoader, final String s, final BeanContext beanContext, final AppletInitializer appletInitializer) throws IOException, ClassNotFoundException {
        Object o = null;
        boolean b = false;
        IOException ex = null;
        if (systemClassLoader == null) {
            try {
                systemClassLoader = ClassLoader.getSystemClassLoader();
            }
            catch (final SecurityException ex2) {}
        }
        final String concat = s.replace('.', '/').concat(".ser");
        InputStream inputStream;
        if (systemClassLoader == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(concat);
        }
        else {
            inputStream = systemClassLoader.getResourceAsStream(concat);
        }
        if (inputStream != null) {
            try {
                ObjectInputStream objectInputStream;
                if (systemClassLoader == null) {
                    objectInputStream = new ObjectInputStream(inputStream);
                }
                else {
                    objectInputStream = new ObjectInputStreamWithLoader(inputStream, systemClassLoader);
                }
                o = objectInputStream.readObject();
                b = true;
                objectInputStream.close();
            }
            catch (final IOException ex3) {
                inputStream.close();
                ex = ex3;
            }
            catch (final ClassNotFoundException ex4) {
                inputStream.close();
                throw ex4;
            }
        }
        if (o == null) {
            Class<?> class1;
            try {
                class1 = ClassFinder.findClass(s, systemClassLoader);
            }
            catch (final ClassNotFoundException ex5) {
                if (ex != null) {
                    throw ex;
                }
                throw ex5;
            }
            if (!Modifier.isPublic(class1.getModifiers())) {
                throw new ClassNotFoundException("" + class1 + " : no public access");
            }
            try {
                o = class1.newInstance();
            }
            catch (final Exception ex6) {
                throw new ClassNotFoundException("" + class1 + " : " + ex6, ex6);
            }
        }
        if (o != null) {
            BeansAppletStub stub = null;
            if (o instanceof Applet) {
                final Applet applet = (Applet)o;
                final boolean b2 = appletInitializer == null;
                if (b2) {
                    String s2;
                    if (b) {
                        s2 = s.replace('.', '/').concat(".ser");
                    }
                    else {
                        s2 = s.replace('.', '/').concat(".class");
                    }
                    URL url = null;
                    URL url2 = null;
                    URL url3;
                    if (systemClassLoader == null) {
                        url3 = ClassLoader.getSystemResource(s2);
                    }
                    else {
                        url3 = systemClassLoader.getResource(s2);
                    }
                    if (url3 != null) {
                        final String externalForm = url3.toExternalForm();
                        if (externalForm.endsWith(s2)) {
                            url = (url2 = new URL(externalForm.substring(0, externalForm.length() - s2.length())));
                            final int lastIndex = externalForm.lastIndexOf(47);
                            if (lastIndex >= 0) {
                                url2 = new URL(externalForm.substring(0, lastIndex + 1));
                            }
                        }
                    }
                    stub = new BeansAppletStub(applet, new BeansAppletContext(applet), url, url2);
                    applet.setStub(stub);
                }
                else {
                    appletInitializer.initialize(applet, beanContext);
                }
                if (beanContext != null) {
                    unsafeBeanContextAdd(beanContext, o);
                }
                if (!b) {
                    applet.setSize(100, 100);
                    applet.init();
                }
                if (b2) {
                    stub.active = true;
                }
                else {
                    appletInitializer.activate(applet);
                }
            }
            else if (beanContext != null) {
                unsafeBeanContextAdd(beanContext, o);
            }
        }
        return o;
    }
    
    private static void unsafeBeanContextAdd(final BeanContext beanContext, final Object o) {
        beanContext.add(o);
    }
    
    public static Object getInstanceOf(final Object o, final Class<?> clazz) {
        return o;
    }
    
    public static boolean isInstanceOf(final Object o, final Class<?> clazz) {
        return Introspector.isSubclass(o.getClass(), clazz);
    }
    
    public static boolean isDesignTime() {
        return ThreadGroupContext.getContext().isDesignTime();
    }
    
    public static boolean isGuiAvailable() {
        return ThreadGroupContext.getContext().isGuiAvailable();
    }
    
    public static void setDesignTime(final boolean designTime) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPropertiesAccess();
        }
        ThreadGroupContext.getContext().setDesignTime(designTime);
    }
    
    public static void setGuiAvailable(final boolean guiAvailable) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPropertiesAccess();
        }
        ThreadGroupContext.getContext().setGuiAvailable(guiAvailable);
    }
}
