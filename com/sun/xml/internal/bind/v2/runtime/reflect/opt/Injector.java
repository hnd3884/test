package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.xml.internal.bind.Util;
import java.util.WeakHashMap;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.util.HashMap;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class Injector
{
    private static final ReentrantReadWriteLock irwl;
    private static final Lock ir;
    private static final Lock iw;
    private static final Map<ClassLoader, WeakReference<Injector>> injectors;
    private static final Logger logger;
    private final Map<String, Class> classes;
    private final ReentrantReadWriteLock rwl;
    private final Lock r;
    private final Lock w;
    private final ClassLoader parent;
    private final boolean loadable;
    private static final Method defineClass;
    private static final Method resolveClass;
    private static final Method findLoadedClass;
    
    static Class inject(final ClassLoader cl, final String className, final byte[] image) {
        final Injector injector = get(cl);
        if (injector != null) {
            return injector.inject(className, image);
        }
        return null;
    }
    
    static Class find(final ClassLoader cl, final String className) {
        final Injector injector = get(cl);
        if (injector != null) {
            return injector.find(className);
        }
        return null;
    }
    
    private static Injector get(final ClassLoader cl) {
        Injector injector = null;
        Injector.ir.lock();
        WeakReference<Injector> wr;
        try {
            wr = Injector.injectors.get(cl);
        }
        finally {
            Injector.ir.unlock();
        }
        if (wr != null) {
            injector = wr.get();
        }
        if (injector == null) {
            try {
                wr = new WeakReference<Injector>(injector = new Injector(cl));
                Injector.iw.lock();
                try {
                    if (!Injector.injectors.containsKey(cl)) {
                        Injector.injectors.put(cl, wr);
                    }
                }
                finally {
                    Injector.iw.unlock();
                }
            }
            catch (final SecurityException e) {
                Injector.logger.log(Level.FINE, "Unable to set up a back-door for the injector", e);
                return null;
            }
        }
        return injector;
    }
    
    private Injector(final ClassLoader parent) {
        this.classes = new HashMap<String, Class>();
        this.rwl = new ReentrantReadWriteLock();
        this.r = this.rwl.readLock();
        this.w = this.rwl.writeLock();
        this.parent = parent;
        assert parent != null;
        boolean loadableCheck = false;
        try {
            loadableCheck = (parent.loadClass(Accessor.class.getName()) == Accessor.class);
        }
        catch (final ClassNotFoundException ex) {}
        this.loadable = loadableCheck;
    }
    
    private Class inject(final String className, final byte[] image) {
        if (!this.loadable) {
            return null;
        }
        boolean wlocked = false;
        boolean rlocked = false;
        try {
            this.r.lock();
            rlocked = true;
            Class c = this.classes.get(className);
            this.r.unlock();
            rlocked = false;
            if (c == null) {
                try {
                    c = (Class)Injector.findLoadedClass.invoke(this.parent, className.replace('/', '.'));
                }
                catch (final IllegalArgumentException e) {
                    Injector.logger.log(Level.FINE, "Unable to find " + className, e);
                }
                catch (final IllegalAccessException e2) {
                    Injector.logger.log(Level.FINE, "Unable to find " + className, e2);
                }
                catch (final InvocationTargetException e3) {
                    final Throwable t = e3.getTargetException();
                    Injector.logger.log(Level.FINE, "Unable to find " + className, t);
                }
                if (c != null) {
                    this.w.lock();
                    wlocked = true;
                    this.classes.put(className, c);
                    this.w.unlock();
                    wlocked = false;
                    return c;
                }
            }
            if (c == null) {
                this.r.lock();
                rlocked = true;
                c = this.classes.get(className);
                this.r.unlock();
                rlocked = false;
                if (c == null) {
                    try {
                        c = (Class)Injector.defineClass.invoke(this.parent, className.replace('/', '.'), image, 0, image.length);
                        Injector.resolveClass.invoke(this.parent, c);
                    }
                    catch (final IllegalAccessException e2) {
                        Injector.logger.log(Level.FINE, "Unable to inject " + className, e2);
                        return null;
                    }
                    catch (final InvocationTargetException e3) {
                        final Throwable t = e3.getTargetException();
                        if (t instanceof LinkageError) {
                            Injector.logger.log(Level.FINE, "duplicate class definition bug occured? Please report this : " + className, t);
                        }
                        else {
                            Injector.logger.log(Level.FINE, "Unable to inject " + className, t);
                        }
                        return null;
                    }
                    catch (final SecurityException e4) {
                        Injector.logger.log(Level.FINE, "Unable to inject " + className, e4);
                        return null;
                    }
                    catch (final LinkageError e5) {
                        Injector.logger.log(Level.FINE, "Unable to inject " + className, e5);
                        return null;
                    }
                    this.w.lock();
                    wlocked = true;
                    if (!this.classes.containsKey(className)) {
                        this.classes.put(className, c);
                    }
                    this.w.unlock();
                    wlocked = false;
                }
            }
            return c;
        }
        finally {
            if (rlocked) {
                this.r.unlock();
            }
            if (wlocked) {
                this.w.unlock();
            }
        }
    }
    
    private Class find(final String className) {
        this.r.lock();
        try {
            return this.classes.get(className);
        }
        finally {
            this.r.unlock();
        }
    }
    
    static {
        irwl = new ReentrantReadWriteLock();
        ir = Injector.irwl.readLock();
        iw = Injector.irwl.writeLock();
        injectors = new WeakHashMap<ClassLoader, WeakReference<Injector>>();
        logger = Util.getClassLogger();
        try {
            defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
            resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", Class.class);
            findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        }
        catch (final NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                Injector.defineClass.setAccessible(true);
                Injector.resolveClass.setAccessible(true);
                Injector.findLoadedClass.setAccessible(true);
                return null;
            }
        });
    }
}
