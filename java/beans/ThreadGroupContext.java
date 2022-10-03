package java.beans;

import java.util.WeakHashMap;
import java.awt.GraphicsEnvironment;
import com.sun.beans.finder.PropertyEditorFinder;
import com.sun.beans.finder.BeanInfoFinder;
import java.util.Map;

final class ThreadGroupContext
{
    private static final WeakIdentityMap<ThreadGroupContext> contexts;
    private volatile boolean isDesignTime;
    private volatile Boolean isGuiAvailable;
    private Map<Class<?>, BeanInfo> beanInfoCache;
    private BeanInfoFinder beanInfoFinder;
    private PropertyEditorFinder propertyEditorFinder;
    
    static ThreadGroupContext getContext() {
        return ThreadGroupContext.contexts.get(Thread.currentThread().getThreadGroup());
    }
    
    private ThreadGroupContext() {
    }
    
    boolean isDesignTime() {
        return this.isDesignTime;
    }
    
    void setDesignTime(final boolean isDesignTime) {
        this.isDesignTime = isDesignTime;
    }
    
    boolean isGuiAvailable() {
        final Boolean isGuiAvailable = this.isGuiAvailable;
        return (isGuiAvailable != null) ? isGuiAvailable : (!GraphicsEnvironment.isHeadless());
    }
    
    void setGuiAvailable(final boolean b) {
        this.isGuiAvailable = b;
    }
    
    BeanInfo getBeanInfo(final Class<?> clazz) {
        return (this.beanInfoCache != null) ? this.beanInfoCache.get(clazz) : null;
    }
    
    BeanInfo putBeanInfo(final Class<?> clazz, final BeanInfo beanInfo) {
        if (this.beanInfoCache == null) {
            this.beanInfoCache = new WeakHashMap<Class<?>, BeanInfo>();
        }
        return this.beanInfoCache.put(clazz, beanInfo);
    }
    
    void removeBeanInfo(final Class<?> clazz) {
        if (this.beanInfoCache != null) {
            this.beanInfoCache.remove(clazz);
        }
    }
    
    void clearBeanInfoCache() {
        if (this.beanInfoCache != null) {
            this.beanInfoCache.clear();
        }
    }
    
    synchronized BeanInfoFinder getBeanInfoFinder() {
        if (this.beanInfoFinder == null) {
            this.beanInfoFinder = new BeanInfoFinder();
        }
        return this.beanInfoFinder;
    }
    
    synchronized PropertyEditorFinder getPropertyEditorFinder() {
        if (this.propertyEditorFinder == null) {
            this.propertyEditorFinder = new PropertyEditorFinder();
        }
        return this.propertyEditorFinder;
    }
    
    static {
        contexts = new WeakIdentityMap<ThreadGroupContext>() {
            @Override
            protected ThreadGroupContext create(final Object o) {
                return new ThreadGroupContext(null);
            }
        };
    }
}
