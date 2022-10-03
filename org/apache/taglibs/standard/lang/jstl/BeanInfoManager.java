package org.apache.taglibs.standard.lang.jstl;

import java.security.AccessControlException;
import java.lang.reflect.Modifier;
import java.beans.EventSetDescriptor;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.IndexedPropertyDescriptor;
import java.util.HashMap;
import java.beans.Introspector;
import java.util.Map;
import java.beans.BeanInfo;

public class BeanInfoManager
{
    Class mBeanClass;
    BeanInfo mBeanInfo;
    Map mPropertyByName;
    Map mIndexedPropertyByName;
    Map mEventSetByName;
    boolean mInitialized;
    static Map mBeanInfoManagerByClass;
    
    public Class getBeanClass() {
        return this.mBeanClass;
    }
    
    BeanInfoManager(final Class pBeanClass) {
        this.mBeanClass = pBeanClass;
    }
    
    public static BeanInfoManager getBeanInfoManager(final Class pClass) {
        BeanInfoManager ret = BeanInfoManager.mBeanInfoManagerByClass.get(pClass);
        if (ret == null) {
            ret = createBeanInfoManager(pClass);
        }
        return ret;
    }
    
    static synchronized BeanInfoManager createBeanInfoManager(final Class pClass) {
        BeanInfoManager ret = BeanInfoManager.mBeanInfoManagerByClass.get(pClass);
        if (ret == null) {
            ret = new BeanInfoManager(pClass);
            BeanInfoManager.mBeanInfoManagerByClass.put(pClass, ret);
        }
        return ret;
    }
    
    public static BeanInfoProperty getBeanInfoProperty(final Class pClass, final String pPropertyName, final Logger pLogger) throws ELException {
        return getBeanInfoManager(pClass).getProperty(pPropertyName, pLogger);
    }
    
    public static BeanInfoIndexedProperty getBeanInfoIndexedProperty(final Class pClass, final String pIndexedPropertyName, final Logger pLogger) throws ELException {
        return getBeanInfoManager(pClass).getIndexedProperty(pIndexedPropertyName, pLogger);
    }
    
    void checkInitialized(final Logger pLogger) throws ELException {
        if (!this.mInitialized) {
            synchronized (this) {
                if (!this.mInitialized) {
                    this.initialize(pLogger);
                    this.mInitialized = true;
                }
            }
        }
    }
    
    void initialize(final Logger pLogger) throws ELException {
        try {
            this.mBeanInfo = Introspector.getBeanInfo(this.mBeanClass);
            this.mPropertyByName = new HashMap();
            this.mIndexedPropertyByName = new HashMap();
            final PropertyDescriptor[] pds = this.mBeanInfo.getPropertyDescriptors();
            for (int i = 0; pds != null && i < pds.length; ++i) {
                final PropertyDescriptor pd = pds[i];
                if (pd instanceof IndexedPropertyDescriptor) {
                    final IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)pd;
                    final Method readMethod = getPublicMethod(ipd.getIndexedReadMethod());
                    final Method writeMethod = getPublicMethod(ipd.getIndexedWriteMethod());
                    final BeanInfoIndexedProperty property = new BeanInfoIndexedProperty(readMethod, writeMethod, ipd);
                    this.mIndexedPropertyByName.put(ipd.getName(), property);
                }
                final Method readMethod2 = getPublicMethod(pd.getReadMethod());
                final Method writeMethod2 = getPublicMethod(pd.getWriteMethod());
                final BeanInfoProperty property2 = new BeanInfoProperty(readMethod2, writeMethod2, pd);
                this.mPropertyByName.put(pd.getName(), property2);
            }
            this.mEventSetByName = new HashMap();
            final EventSetDescriptor[] esds = this.mBeanInfo.getEventSetDescriptors();
            for (int j = 0; esds != null && j < esds.length; ++j) {
                final EventSetDescriptor esd = esds[j];
                this.mEventSetByName.put(esd.getName(), esd);
            }
        }
        catch (final IntrospectionException exc) {
            if (pLogger.isLoggingWarning()) {
                pLogger.logWarning(Constants.EXCEPTION_GETTING_BEANINFO, exc, this.mBeanClass.getName());
            }
        }
    }
    
    BeanInfo getBeanInfo(final Logger pLogger) throws ELException {
        this.checkInitialized(pLogger);
        return this.mBeanInfo;
    }
    
    public BeanInfoProperty getProperty(final String pPropertyName, final Logger pLogger) throws ELException {
        this.checkInitialized(pLogger);
        return this.mPropertyByName.get(pPropertyName);
    }
    
    public BeanInfoIndexedProperty getIndexedProperty(final String pIndexedPropertyName, final Logger pLogger) throws ELException {
        this.checkInitialized(pLogger);
        return this.mIndexedPropertyByName.get(pIndexedPropertyName);
    }
    
    public EventSetDescriptor getEventSet(final String pEventSetName, final Logger pLogger) throws ELException {
        this.checkInitialized(pLogger);
        return this.mEventSetByName.get(pEventSetName);
    }
    
    static Method getPublicMethod(final Method pMethod) {
        if (pMethod == null) {
            return null;
        }
        final Class cl = pMethod.getDeclaringClass();
        if (Modifier.isPublic(cl.getModifiers())) {
            return pMethod;
        }
        final Method ret = getPublicMethod(cl, pMethod);
        if (ret != null) {
            return ret;
        }
        return pMethod;
    }
    
    static Method getPublicMethod(final Class pClass, final Method pMethod) {
        if (Modifier.isPublic(pClass.getModifiers())) {
            try {
                Method m;
                try {
                    m = pClass.getDeclaredMethod(pMethod.getName(), (Class[])pMethod.getParameterTypes());
                }
                catch (final AccessControlException ex) {
                    m = pClass.getMethod(pMethod.getName(), (Class[])pMethod.getParameterTypes());
                }
                if (Modifier.isPublic(m.getModifiers())) {
                    return m;
                }
            }
            catch (final NoSuchMethodException ex2) {}
        }
        final Class[] interfaces = pClass.getInterfaces();
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                final Method j = getPublicMethod(interfaces[i], pMethod);
                if (j != null) {
                    return j;
                }
            }
        }
        final Class superclass = pClass.getSuperclass();
        if (superclass != null) {
            final Method k = getPublicMethod(superclass, pMethod);
            if (k != null) {
                return k;
            }
        }
        return null;
    }
    
    static {
        BeanInfoManager.mBeanInfoManagerByClass = new HashMap();
    }
}
