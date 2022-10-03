package org.apache.tomcat.util.modeler;

import org.apache.tomcat.util.buf.StringUtils;
import javax.management.ServiceNotFoundException;
import javax.management.ReflectionException;
import javax.management.AttributeNotFoundException;
import java.lang.reflect.Method;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.InstanceNotFoundException;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import javax.management.DynamicMBean;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import javax.management.MBeanInfo;
import java.util.concurrent.locks.ReadWriteLock;
import java.io.Serializable;

public class ManagedBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String BASE_MBEAN = "org.apache.tomcat.util.modeler.BaseModelMBean";
    static final Class<?>[] NO_ARGS_PARAM_SIG;
    private final ReadWriteLock mBeanInfoLock;
    private transient volatile MBeanInfo info;
    private Map<String, AttributeInfo> attributes;
    private Map<String, OperationInfo> operations;
    protected String className;
    protected String description;
    protected String domain;
    protected String group;
    protected String name;
    private NotificationInfo[] notifications;
    protected String type;
    
    public ManagedBean() {
        this.mBeanInfoLock = new ReentrantReadWriteLock();
        this.info = null;
        this.attributes = new HashMap<String, AttributeInfo>();
        this.operations = new HashMap<String, OperationInfo>();
        this.className = "org.apache.tomcat.util.modeler.BaseModelMBean";
        this.description = null;
        this.domain = null;
        this.group = null;
        this.name = null;
        this.notifications = new NotificationInfo[0];
        this.type = null;
        final AttributeInfo ai = new AttributeInfo();
        ai.setName("modelerType");
        ai.setDescription("Type of the modeled resource. Can be set only once");
        ai.setType("java.lang.String");
        ai.setWriteable(false);
        this.addAttribute(ai);
    }
    
    public AttributeInfo[] getAttributes() {
        final AttributeInfo[] result = new AttributeInfo[this.attributes.size()];
        this.attributes.values().toArray(result);
        return result;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setClassName(final String className) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.className = className;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.description = description;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public void setDomain(final String domain) {
        this.domain = domain;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public void setGroup(final String group) {
        this.group = group;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.name = name;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }
    
    public NotificationInfo[] getNotifications() {
        return this.notifications;
    }
    
    public OperationInfo[] getOperations() {
        final OperationInfo[] result = new OperationInfo[this.operations.size()];
        this.operations.values().toArray(result);
        return result;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.type = type;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }
    
    public void addAttribute(final AttributeInfo attribute) {
        this.attributes.put(attribute.getName(), attribute);
    }
    
    public void addNotification(final NotificationInfo notification) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            final NotificationInfo[] results = new NotificationInfo[this.notifications.length + 1];
            System.arraycopy(this.notifications, 0, results, 0, this.notifications.length);
            results[this.notifications.length] = notification;
            this.notifications = results;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }
    
    public void addOperation(final OperationInfo operation) {
        this.operations.put(this.createOperationKey(operation), operation);
    }
    
    public DynamicMBean createMBean(final Object instance) throws InstanceNotFoundException, MBeanException, RuntimeOperationsException {
        BaseModelMBean mbean = null;
        if (this.getClassName().equals("org.apache.tomcat.util.modeler.BaseModelMBean")) {
            mbean = new BaseModelMBean();
        }
        else {
            Class<?> clazz = null;
            Exception ex = null;
            try {
                clazz = Class.forName(this.getClassName());
            }
            catch (final Exception ex2) {}
            if (clazz == null) {
                try {
                    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl != null) {
                        clazz = cl.loadClass(this.getClassName());
                    }
                }
                catch (final Exception e) {
                    ex = e;
                }
            }
            if (clazz == null) {
                throw new MBeanException(ex, "Cannot load ModelMBean class " + this.getClassName());
            }
            try {
                mbean = (BaseModelMBean)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (final RuntimeOperationsException e2) {
                throw e2;
            }
            catch (final Exception e) {
                throw new MBeanException(e, "Cannot instantiate ModelMBean of class " + this.getClassName());
            }
        }
        mbean.setManagedBean(this);
        try {
            if (instance != null) {
                mbean.setManagedResource(instance, "ObjectReference");
            }
        }
        catch (final InstanceNotFoundException e3) {
            throw e3;
        }
        return mbean;
    }
    
    MBeanInfo getMBeanInfo() {
        this.mBeanInfoLock.readLock().lock();
        try {
            if (this.info != null) {
                return this.info;
            }
        }
        finally {
            this.mBeanInfoLock.readLock().unlock();
        }
        this.mBeanInfoLock.writeLock().lock();
        try {
            if (this.info == null) {
                final AttributeInfo[] attrs = this.getAttributes();
                final MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[attrs.length];
                for (int i = 0; i < attrs.length; ++i) {
                    attributes[i] = attrs[i].createAttributeInfo();
                }
                final OperationInfo[] opers = this.getOperations();
                final MBeanOperationInfo[] operations = new MBeanOperationInfo[opers.length];
                for (int j = 0; j < opers.length; ++j) {
                    operations[j] = opers[j].createOperationInfo();
                }
                final NotificationInfo[] notifs = this.getNotifications();
                final MBeanNotificationInfo[] notifications = new MBeanNotificationInfo[notifs.length];
                for (int k = 0; k < notifs.length; ++k) {
                    notifications[k] = notifs[k].createNotificationInfo();
                }
                this.info = new MBeanInfo(this.getClassName(), this.getDescription(), attributes, new MBeanConstructorInfo[0], operations, notifications);
            }
            return this.info;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ManagedBean[");
        sb.append("name=");
        sb.append(this.name);
        sb.append(", className=");
        sb.append(this.className);
        sb.append(", description=");
        sb.append(this.description);
        if (this.group != null) {
            sb.append(", group=");
            sb.append(this.group);
        }
        sb.append(", type=");
        sb.append(this.type);
        sb.append(']');
        return sb.toString();
    }
    
    Method getGetter(final String aname, final BaseModelMBean mbean, final Object resource) throws AttributeNotFoundException, ReflectionException {
        Method m = null;
        final AttributeInfo attrInfo = this.attributes.get(aname);
        if (attrInfo == null) {
            throw new AttributeNotFoundException(" Cannot find attribute " + aname + " for " + resource);
        }
        final String getMethod = attrInfo.getGetMethod();
        if (getMethod == null) {
            throw new AttributeNotFoundException("Cannot find attribute " + aname + " get method name");
        }
        Object object = null;
        NoSuchMethodException exception = null;
        try {
            object = mbean;
            m = object.getClass().getMethod(getMethod, ManagedBean.NO_ARGS_PARAM_SIG);
        }
        catch (final NoSuchMethodException e) {
            exception = e;
        }
        if (m == null && resource != null) {
            try {
                object = resource;
                m = object.getClass().getMethod(getMethod, ManagedBean.NO_ARGS_PARAM_SIG);
                exception = null;
            }
            catch (final NoSuchMethodException e) {
                exception = e;
            }
        }
        if (exception != null) {
            throw new ReflectionException(exception, "Cannot find getter method " + getMethod);
        }
        return m;
    }
    
    public Method getSetter(final String aname, final BaseModelMBean bean, final Object resource) throws AttributeNotFoundException, ReflectionException {
        Method m = null;
        final AttributeInfo attrInfo = this.attributes.get(aname);
        if (attrInfo == null) {
            throw new AttributeNotFoundException(" Cannot find attribute " + aname);
        }
        final String setMethod = attrInfo.getSetMethod();
        if (setMethod == null) {
            throw new AttributeNotFoundException("Cannot find attribute " + aname + " set method name");
        }
        final String argType = attrInfo.getType();
        final Class<?>[] signature = { BaseModelMBean.getAttributeClass(argType) };
        Object object = null;
        NoSuchMethodException exception = null;
        try {
            object = bean;
            m = object.getClass().getMethod(setMethod, signature);
        }
        catch (final NoSuchMethodException e) {
            exception = e;
        }
        if (m == null && resource != null) {
            try {
                object = resource;
                m = object.getClass().getMethod(setMethod, signature);
                exception = null;
            }
            catch (final NoSuchMethodException e) {
                exception = e;
            }
        }
        if (exception != null) {
            throw new ReflectionException(exception, "Cannot find setter method " + setMethod + " " + resource);
        }
        return m;
    }
    
    public Method getInvoke(final String aname, Object[] params, String[] signature, final BaseModelMBean bean, final Object resource) throws MBeanException, ReflectionException {
        Method method = null;
        if (params == null) {
            params = new Object[0];
        }
        if (signature == null) {
            signature = new String[0];
        }
        if (params.length != signature.length) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Inconsistent arguments and signature"), "Inconsistent arguments and signature");
        }
        final OperationInfo opInfo = this.operations.get(this.createOperationKey(aname, signature));
        if (opInfo == null) {
            throw new MBeanException(new ServiceNotFoundException("Cannot find operation " + aname), "Cannot find operation " + aname);
        }
        final Class<?>[] types = new Class[signature.length];
        for (int i = 0; i < signature.length; ++i) {
            types[i] = BaseModelMBean.getAttributeClass(signature[i]);
        }
        Object object = null;
        Exception exception = null;
        try {
            object = bean;
            method = object.getClass().getMethod(aname, types);
        }
        catch (final NoSuchMethodException e) {
            exception = e;
        }
        try {
            if (method == null && resource != null) {
                object = resource;
                method = object.getClass().getMethod(aname, types);
            }
        }
        catch (final NoSuchMethodException e) {
            exception = e;
        }
        if (method == null) {
            throw new ReflectionException(exception, "Cannot find method " + aname + " with this signature");
        }
        return method;
    }
    
    private String createOperationKey(final OperationInfo operation) {
        final StringBuilder key = new StringBuilder(operation.getName());
        key.append('(');
        StringUtils.join((Object[])operation.getSignature(), ',', (StringUtils.Function)new StringUtils.Function<ParameterInfo>() {
            public String apply(final ParameterInfo t) {
                return t.getType();
            }
        }, key);
        key.append(')');
        return key.toString().intern();
    }
    
    private String createOperationKey(final String methodName, final String[] parameterTypes) {
        final StringBuilder key = new StringBuilder(methodName);
        key.append('(');
        StringUtils.join(parameterTypes, ',', key);
        key.append(')');
        return key.toString().intern();
    }
    
    static {
        NO_ARGS_PARAM_SIG = new Class[0];
    }
}
