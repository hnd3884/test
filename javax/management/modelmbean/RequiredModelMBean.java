package javax.management.modelmbean;

import java.util.HashMap;
import sun.misc.SharedSecrets;
import javax.management.ObjectName;
import javax.management.MBeanServerFactory;
import javax.management.loading.ClassLoaderRepository;
import javax.management.AttributeChangeNotification;
import java.util.Vector;
import javax.management.AttributeChangeNotificationFilter;
import javax.management.Notification;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InvalidAttributeValueException;
import javax.management.AttributeNotFoundException;
import java.util.HashSet;
import javax.management.RuntimeErrorException;
import java.lang.reflect.InvocationTargetException;
import sun.reflect.misc.MethodUtil;
import java.lang.reflect.Method;
import javax.management.ReflectionException;
import sun.reflect.misc.ReflectUtil;
import java.security.PrivilegedAction;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import java.util.Date;
import javax.management.Descriptor;
import javax.management.ServiceNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.security.AccessController;
import java.util.Set;
import java.util.Map;
import java.security.AccessControlContext;
import sun.misc.JavaSecurityAccess;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.MBeanRegistration;

public class RequiredModelMBean implements ModelMBean, MBeanRegistration, NotificationEmitter
{
    ModelMBeanInfo modelMBeanInfo;
    private NotificationBroadcasterSupport generalBroadcaster;
    private NotificationBroadcasterSupport attributeBroadcaster;
    private Object managedResource;
    private boolean registered;
    private transient MBeanServer server;
    private static final JavaSecurityAccess javaSecurityAccess;
    private final AccessControlContext acc;
    private static final Class<?>[] primitiveClasses;
    private static final Map<String, Class<?>> primitiveClassMap;
    private static Set<String> rmmbMethodNames;
    private static final String[] primitiveTypes;
    private static final String[] primitiveWrappers;
    
    public RequiredModelMBean() throws MBeanException, RuntimeOperationsException {
        this.generalBroadcaster = null;
        this.attributeBroadcaster = null;
        this.managedResource = null;
        this.registered = false;
        this.server = null;
        this.acc = AccessController.getContext();
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Entry");
        }
        this.modelMBeanInfo = this.createDefaultModelMBeanInfo();
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Exit");
        }
    }
    
    public RequiredModelMBean(final ModelMBeanInfo modelMBeanInfo) throws MBeanException, RuntimeOperationsException {
        this.generalBroadcaster = null;
        this.attributeBroadcaster = null;
        this.managedResource = null;
        this.registered = false;
        this.server = null;
        this.acc = AccessController.getContext();
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Entry");
        }
        this.setModelMBeanInfo(modelMBeanInfo);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Exit");
        }
    }
    
    @Override
    public void setModelMBeanInfo(final ModelMBeanInfo modelMBeanInfo) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Entry");
        }
        if (modelMBeanInfo == null) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo is null: Raising exception.");
            }
            throw new RuntimeOperationsException(new IllegalArgumentException("ModelMBeanInfo must not be null"), "Exception occurred trying to initialize the ModelMBeanInfo of the RequiredModelMBean");
        }
        if (this.registered) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "RequiredMBean is registered: Raising exception.");
            }
            throw new RuntimeOperationsException(new IllegalStateException("cannot call setModelMBeanInfo while ModelMBean is registered"), "Exception occurred trying to set the ModelMBeanInfo of the RequiredModelMBean");
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Setting ModelMBeanInfo to " + this.printModelMBeanInfo(modelMBeanInfo));
            int length = 0;
            if (modelMBeanInfo.getNotifications() != null) {
                length = modelMBeanInfo.getNotifications().length;
            }
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo notifications has " + length + " elements");
        }
        this.modelMBeanInfo = (ModelMBeanInfo)modelMBeanInfo.clone();
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "set mbeanInfo to: " + this.printModelMBeanInfo(this.modelMBeanInfo));
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Exit");
        }
    }
    
    @Override
    public void setManagedResource(final Object managedResource, final String s) throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Entry");
        }
        if (s == null || !s.equalsIgnoreCase("objectReference")) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resource Type is not supported: " + s);
            }
            throw new InvalidTargetObjectTypeException(s);
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resource is valid");
        }
        this.managedResource = managedResource;
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object, String)", "Exit");
        }
    }
    
    @Override
    public void load() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException {
        final ServiceNotFoundException ex = new ServiceNotFoundException("Persistence not supported for this MBean");
        throw new MBeanException(ex, ex.getMessage());
    }
    
    @Override
    public void store() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException {
        final ServiceNotFoundException ex = new ServiceNotFoundException("Persistence not supported for this MBean");
        throw new MBeanException(ex, ex.getMessage());
    }
    
    private Object resolveForCacheValue(final Descriptor descriptor) throws MBeanException, RuntimeOperationsException {
        final boolean loggable = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Entry");
        }
        Object o = null;
        if (descriptor == null) {
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Input Descriptor is null");
            }
            return o;
        }
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "descriptor is " + descriptor);
        }
        final Descriptor mBeanDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
        if (mBeanDescriptor == null && loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "MBean Descriptor is null");
        }
        final Object fieldValue = descriptor.getFieldValue("currencyTimeLimit");
        String s;
        if (fieldValue != null) {
            s = fieldValue.toString();
        }
        else {
            s = null;
        }
        if (s == null && mBeanDescriptor != null) {
            final Object fieldValue2 = mBeanDescriptor.getFieldValue("currencyTimeLimit");
            if (fieldValue2 != null) {
                s = fieldValue2.toString();
            }
            else {
                s = null;
            }
        }
        if (s != null) {
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyTimeLimit: " + s);
            }
            final long n = new Long(s) * 1000L;
            boolean b;
            boolean b2;
            if (n < 0L) {
                b = false;
                b2 = true;
                if (loggable) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", n + ": never Cached");
                }
            }
            else if (n == 0L) {
                b = true;
                b2 = false;
                if (loggable) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "always valid Cache");
                }
            }
            else {
                final Object fieldValue3 = descriptor.getFieldValue("lastUpdatedTimeStamp");
                String string;
                if (fieldValue3 != null) {
                    string = fieldValue3.toString();
                }
                else {
                    string = null;
                }
                if (loggable) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "lastUpdatedTimeStamp: " + string);
                }
                if (string == null) {
                    string = "0";
                }
                final long longValue = new Long(string);
                if (loggable) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyPeriod:" + n + " lastUpdatedTimeStamp:" + longValue);
                }
                final long time = new Date().getTime();
                if (time < longValue + n) {
                    b = true;
                    b2 = false;
                    if (loggable) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", " timed valid Cache for " + time + " < " + (longValue + n));
                    }
                }
                else {
                    b = false;
                    b2 = true;
                    if (loggable) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "timed expired cache for " + time + " > " + (longValue + n));
                    }
                }
            }
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "returnCachedValue:" + b + " resetValue: " + b2);
            }
            if (b) {
                final Object fieldValue4 = descriptor.getFieldValue("value");
                if (fieldValue4 != null) {
                    o = fieldValue4;
                    if (loggable) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "valid Cache value: " + fieldValue4);
                    }
                }
                else {
                    o = null;
                    if (loggable) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "no Cached value");
                    }
                }
            }
            if (b2) {
                descriptor.removeField("lastUpdatedTimeStamp");
                descriptor.removeField("value");
                o = null;
                this.modelMBeanInfo.setDescriptor(descriptor, null);
                if (loggable) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "reset cached value to null");
                }
            }
        }
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Exit");
        }
        return o;
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "Entry");
        }
        if (this.modelMBeanInfo == null) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "modelMBeanInfo is null");
            }
            this.modelMBeanInfo = this.createDefaultModelMBeanInfo();
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "ModelMBeanInfo is " + this.modelMBeanInfo.getClassName() + " for " + this.modelMBeanInfo.getDescription());
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", this.printModelMBeanInfo(this.modelMBeanInfo));
        }
        return (MBeanInfo)this.modelMBeanInfo.clone();
    }
    
    private String printModelMBeanInfo(ModelMBeanInfo modelMBeanInfo) {
        final StringBuilder sb = new StringBuilder();
        if (modelMBeanInfo == null) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "printModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo to print is null, printing local ModelMBeanInfo");
            }
            modelMBeanInfo = this.modelMBeanInfo;
        }
        sb.append("\nMBeanInfo for ModelMBean is:");
        sb.append("\nCLASSNAME: \t" + modelMBeanInfo.getClassName());
        sb.append("\nDESCRIPTION: \t" + modelMBeanInfo.getDescription());
        try {
            sb.append("\nMBEAN DESCRIPTOR: \t" + modelMBeanInfo.getMBeanDescriptor());
        }
        catch (final Exception ex) {
            sb.append("\nMBEAN DESCRIPTOR: \t is invalid");
        }
        sb.append("\nATTRIBUTES");
        final MBeanAttributeInfo[] attributes = modelMBeanInfo.getAttributes();
        if (attributes != null && attributes.length > 0) {
            for (int i = 0; i < attributes.length; ++i) {
                final ModelMBeanAttributeInfo modelMBeanAttributeInfo = (ModelMBeanAttributeInfo)attributes[i];
                sb.append(" ** NAME: \t" + modelMBeanAttributeInfo.getName());
                sb.append("    DESCR: \t" + modelMBeanAttributeInfo.getDescription());
                sb.append("    TYPE: \t" + modelMBeanAttributeInfo.getType() + "    READ: \t" + modelMBeanAttributeInfo.isReadable() + "    WRITE: \t" + modelMBeanAttributeInfo.isWritable());
                sb.append("    DESCRIPTOR: " + modelMBeanAttributeInfo.getDescriptor().toString());
            }
        }
        else {
            sb.append(" ** No attributes **");
        }
        sb.append("\nCONSTRUCTORS");
        final MBeanConstructorInfo[] constructors = modelMBeanInfo.getConstructors();
        if (constructors != null && constructors.length > 0) {
            for (int j = 0; j < constructors.length; ++j) {
                final ModelMBeanConstructorInfo modelMBeanConstructorInfo = (ModelMBeanConstructorInfo)constructors[j];
                sb.append(" ** NAME: \t" + modelMBeanConstructorInfo.getName());
                sb.append("    DESCR: \t" + modelMBeanConstructorInfo.getDescription());
                sb.append("    PARAM: \t" + modelMBeanConstructorInfo.getSignature().length + " parameter(s)");
                sb.append("    DESCRIPTOR: " + modelMBeanConstructorInfo.getDescriptor().toString());
            }
        }
        else {
            sb.append(" ** No Constructors **");
        }
        sb.append("\nOPERATIONS");
        final MBeanOperationInfo[] operations = modelMBeanInfo.getOperations();
        if (operations != null && operations.length > 0) {
            for (int k = 0; k < operations.length; ++k) {
                final ModelMBeanOperationInfo modelMBeanOperationInfo = (ModelMBeanOperationInfo)operations[k];
                sb.append(" ** NAME: \t" + modelMBeanOperationInfo.getName());
                sb.append("    DESCR: \t" + modelMBeanOperationInfo.getDescription());
                sb.append("    PARAM: \t" + modelMBeanOperationInfo.getSignature().length + " parameter(s)");
                sb.append("    DESCRIPTOR: " + modelMBeanOperationInfo.getDescriptor().toString());
            }
        }
        else {
            sb.append(" ** No operations ** ");
        }
        sb.append("\nNOTIFICATIONS");
        final MBeanNotificationInfo[] notifications = modelMBeanInfo.getNotifications();
        if (notifications != null && notifications.length > 0) {
            for (int l = 0; l < notifications.length; ++l) {
                final ModelMBeanNotificationInfo modelMBeanNotificationInfo = (ModelMBeanNotificationInfo)notifications[l];
                sb.append(" ** NAME: \t" + modelMBeanNotificationInfo.getName());
                sb.append("    DESCR: \t" + modelMBeanNotificationInfo.getDescription());
                sb.append("    DESCRIPTOR: " + modelMBeanNotificationInfo.getDescriptor().toString());
            }
        }
        else {
            sb.append(" ** No notifications **");
        }
        sb.append(" ** ModelMBean: End of MBeanInfo ** ");
        return sb.toString();
    }
    
    @Override
    public Object invoke(final String s, final Object[] array, final String[] array2) throws MBeanException, ReflectionException {
        final boolean loggable = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Entry");
        }
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Method name must not be null"), "An exception occurred while trying to invoke a method on a RequiredModelMBean");
        }
        String substring = null;
        final int lastIndex = s.lastIndexOf(".");
        String s2;
        if (lastIndex > 0) {
            substring = s.substring(0, lastIndex);
            s2 = s.substring(lastIndex + 1);
        }
        else {
            s2 = s;
        }
        final int index = s2.indexOf("(");
        if (index > 0) {
            s2 = s2.substring(0, index);
        }
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Finding operation " + s + " as " + s2);
        }
        final ModelMBeanOperationInfo operation = this.modelMBeanInfo.getOperation(s2);
        if (operation == null) {
            final String string = "Operation " + s + " not in ModelMBeanInfo";
            throw new MBeanException(new ServiceNotFoundException(string), string);
        }
        final Descriptor descriptor = operation.getDescriptor();
        if (descriptor == null) {
            throw new MBeanException(new ServiceNotFoundException("Operation descriptor null"), "Operation descriptor null");
        }
        final Object resolveForCacheValue = this.resolveForCacheValue(descriptor);
        if (resolveForCacheValue != null) {
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Returning cached value");
            }
            return resolveForCacheValue;
        }
        if (substring == null) {
            substring = (String)descriptor.getFieldValue("class");
        }
        final String s3 = (String)descriptor.getFieldValue("name");
        if (s3 == null) {
            throw new MBeanException(new ServiceNotFoundException("Method descriptor must include `name' field"), "Method descriptor must include `name' field");
        }
        final String s4 = (String)descriptor.getFieldValue("targetType");
        if (s4 != null && !s4.equalsIgnoreCase("objectReference")) {
            final String string2 = "Target type must be objectReference: " + s4;
            throw new MBeanException(new InvalidTargetObjectTypeException(string2), string2);
        }
        final Object fieldValue = descriptor.getFieldValue("targetObject");
        if (loggable && fieldValue != null) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Found target object in descriptor");
        }
        Method method = this.findRMMBMethod(s3, fieldValue, substring, array2);
        Object managedResource;
        if (method != null) {
            managedResource = this;
        }
        else {
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in managedResource class");
            }
            if (fieldValue != null) {
                managedResource = fieldValue;
            }
            else {
                managedResource = this.managedResource;
                if (managedResource == null) {
                    throw new MBeanException(new ServiceNotFoundException("managedResource for invoke " + s + " is null"));
                }
            }
            Class<?> class1 = null;
            Label_0711: {
                if (substring != null) {
                    try {
                        final AccessControlContext context = AccessController.getContext();
                        final Object o = managedResource;
                        final String s5 = substring;
                        final ClassNotFoundException[] array3 = { null };
                        class1 = RequiredModelMBean.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Class<?>>)new PrivilegedAction<Class<?>>() {
                            @Override
                            public Class<?> run() {
                                try {
                                    ReflectUtil.checkPackageAccess(s5);
                                    return Class.forName(s5, false, o.getClass().getClassLoader());
                                }
                                catch (final ClassNotFoundException ex) {
                                    array3[0] = ex;
                                    return null;
                                }
                            }
                        }, context, this.acc);
                        if (array3[0] != null) {
                            throw array3[0];
                        }
                        break Label_0711;
                    }
                    catch (final ClassNotFoundException ex) {
                        throw new ReflectionException(ex, "class for invoke " + s + " not found");
                    }
                }
                class1 = ((RequiredModelMBean)managedResource).getClass();
            }
            method = this.resolveMethod(class1, s3, array2);
        }
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "found " + s3 + ", now invoking");
        }
        final Object invokeMethod = this.invokeMethod(s, method, managedResource, array);
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "successfully invoked method");
        }
        if (invokeMethod != null) {
            this.cacheResult(operation, descriptor, invokeMethod);
        }
        return invokeMethod;
    }
    
    private Method resolveMethod(final Class<?> clazz, final String s, final String[] array) throws ReflectionException {
        final boolean loggable = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolving " + clazz.getName() + "." + s);
        }
        Class[] array2;
        if (array == null) {
            array2 = null;
        }
        else {
            final AccessControlContext context = AccessController.getContext();
            final ReflectionException[] array3 = { null };
            final ClassLoader classLoader = clazz.getClassLoader();
            array2 = new Class[array.length];
            RequiredModelMBean.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    for (int i = 0; i < array.length; ++i) {
                        if (loggable) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolve type " + array[i]);
                        }
                        array2[i] = (Class)RequiredModelMBean.primitiveClassMap.get(array[i]);
                        if (array2[i] == null) {
                            try {
                                ReflectUtil.checkPackageAccess(array[i]);
                                array2[i] = Class.forName(array[i], false, classLoader);
                            }
                            catch (final ClassNotFoundException ex) {
                                if (loggable) {
                                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "class not found");
                                }
                                array3[0] = new ReflectionException(ex, "Parameter class not found");
                            }
                        }
                    }
                    return null;
                }
            }, context, this.acc);
            if (array3[0] != null) {
                throw array3[0];
            }
        }
        try {
            return clazz.getMethod(s, (Class[])array2);
        }
        catch (final NoSuchMethodException ex) {
            throw new ReflectionException(ex, "Target method not found: " + clazz.getName() + "." + s);
        }
    }
    
    private Method findRMMBMethod(final String s, final Object o, final String s2, final String[] array) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in RequiredModelMBean class");
        }
        if (!isRMMBMethodName(s)) {
            return null;
        }
        if (o != null) {
            return null;
        }
        final Class<RequiredModelMBean> clazz = RequiredModelMBean.class;
        Class<RequiredModelMBean> clazz2;
        if (s2 == null) {
            clazz2 = clazz;
        }
        else {
            clazz2 = RequiredModelMBean.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Class<RequiredModelMBean>>)new PrivilegedAction<Class<?>>() {
                @Override
                public Class<?> run() {
                    try {
                        ReflectUtil.checkPackageAccess(s2);
                        final Class<?> forName = Class.forName(s2, false, clazz.getClassLoader());
                        if (!clazz.isAssignableFrom(forName)) {
                            return null;
                        }
                        return forName;
                    }
                    catch (final ClassNotFoundException ex) {
                        return null;
                    }
                }
            }, AccessController.getContext(), this.acc);
        }
        try {
            return (clazz2 != null) ? this.resolveMethod(clazz2, s, array) : null;
        }
        catch (final ReflectionException ex) {
            return null;
        }
    }
    
    private Object invokeMethod(final String s, final Method method, final Object o, final Object[] array) throws MBeanException, ReflectionException {
        try {
            final Throwable[] array2 = { null };
            final Object doIntersectionPrivilege = RequiredModelMBean.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        ReflectUtil.checkPackageAccess(method.getDeclaringClass());
                        return MethodUtil.invoke(method, o, array);
                    }
                    catch (final InvocationTargetException ex) {
                        array2[0] = ex;
                    }
                    catch (final IllegalAccessException ex2) {
                        array2[0] = ex2;
                    }
                    return null;
                }
            }, AccessController.getContext(), this.acc);
            if (array2[0] != null) {
                if (array2[0] instanceof Exception) {
                    throw (Exception)array2[0];
                }
                if (array2[0] instanceof Error) {
                    throw (Error)array2[0];
                }
            }
            return doIntersectionPrivilege;
        }
        catch (final RuntimeErrorException ex) {
            throw new RuntimeOperationsException(ex, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + s);
        }
        catch (final RuntimeException ex2) {
            throw new RuntimeOperationsException(ex2, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + s);
        }
        catch (final IllegalAccessException ex3) {
            throw new ReflectionException(ex3, "IllegalAccessException occurred in RequiredModelMBean while trying to invoke operation " + s);
        }
        catch (final InvocationTargetException ex4) {
            final Throwable targetException = ex4.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw new MBeanException((Exception)targetException, "RuntimeException thrown in RequiredModelMBean while trying to invoke operation " + s);
            }
            if (targetException instanceof Error) {
                throw new RuntimeErrorException((Error)targetException, "Error occurred in RequiredModelMBean while trying to invoke operation " + s);
            }
            if (targetException instanceof ReflectionException) {
                throw (ReflectionException)targetException;
            }
            throw new MBeanException((Exception)targetException, "Exception thrown in RequiredModelMBean while trying to invoke operation " + s);
        }
        catch (final Error error) {
            throw new RuntimeErrorException(error, "Error occurred in RequiredModelMBean while trying to invoke operation " + s);
        }
        catch (final Exception ex5) {
            throw new ReflectionException(ex5, "Exception occurred in RequiredModelMBean while trying to invoke operation " + s);
        }
    }
    
    private void cacheResult(final ModelMBeanOperationInfo modelMBeanOperationInfo, final Descriptor descriptor, final Object o) throws MBeanException {
        final Descriptor mBeanDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
        final Object fieldValue = descriptor.getFieldValue("currencyTimeLimit");
        String s;
        if (fieldValue != null) {
            s = fieldValue.toString();
        }
        else {
            s = null;
        }
        if (s == null && mBeanDescriptor != null) {
            final Object fieldValue2 = mBeanDescriptor.getFieldValue("currencyTimeLimit");
            if (fieldValue2 != null) {
                s = fieldValue2.toString();
            }
            else {
                s = null;
            }
        }
        if (s != null && !s.equals("-1")) {
            descriptor.setField("value", o);
            descriptor.setField("lastUpdatedTimeStamp", String.valueOf(new Date().getTime()));
            this.modelMBeanInfo.setDescriptor(descriptor, "operation");
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String,Object[],Object[])", "new descriptor is " + descriptor);
            }
        }
    }
    
    private static synchronized boolean isRMMBMethodName(final String s) {
        if (RequiredModelMBean.rmmbMethodNames == null) {
            try {
                final HashSet rmmbMethodNames = new HashSet();
                final Method[] methods = RequiredModelMBean.class.getMethods();
                for (int i = 0; i < methods.length; ++i) {
                    rmmbMethodNames.add(methods[i].getName());
                }
                RequiredModelMBean.rmmbMethodNames = rmmbMethodNames;
            }
            catch (final Exception ex) {
                return true;
            }
        }
        return RequiredModelMBean.rmmbMethodNames.contains(s);
    }
    
    @Override
    public Object getAttribute(final String s) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attributeName must not be null"), "Exception occurred trying to get attribute of a RequiredModelMBean");
        }
        final boolean loggable = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Entry with " + s);
        }
        Object o;
        try {
            if (this.modelMBeanInfo == null) {
                throw new AttributeNotFoundException("getAttribute failed: ModelMBeanInfo not found for " + s);
            }
            final ModelMBeanAttributeInfo attribute = this.modelMBeanInfo.getAttribute(s);
            final Descriptor mBeanDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
            if (attribute == null) {
                throw new AttributeNotFoundException("getAttribute failed: ModelMBeanAttributeInfo not found for " + s);
            }
            final Descriptor descriptor = attribute.getDescriptor();
            if (descriptor == null) {
                if (loggable) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed " + s + " not in attributeDescriptor\n");
                }
                throw new MBeanException(new InvalidAttributeValueException("Unable to resolve attribute value, no getMethod defined in descriptor for attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
            }
            if (!attribute.isReadable()) {
                throw new AttributeNotFoundException("getAttribute failed: " + s + " is not readable ");
            }
            o = this.resolveForCacheValue(descriptor);
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "*** cached value is " + o);
            }
            if (o == null) {
                if (loggable) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "**** cached value is null - getting getMethod");
                }
                final String s2 = (String)descriptor.getFieldValue("getMethod");
                if (s2 != null) {
                    if (loggable) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "invoking a getMethod for " + s);
                    }
                    final Object invoke = this.invoke(s2, new Object[0], new String[0]);
                    if (invoke != null) {
                        if (loggable) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a non-null response from getMethod\n");
                        }
                        o = invoke;
                        final Object fieldValue = descriptor.getFieldValue("currencyTimeLimit");
                        String s3;
                        if (fieldValue != null) {
                            s3 = fieldValue.toString();
                        }
                        else {
                            s3 = null;
                        }
                        if (s3 == null && mBeanDescriptor != null) {
                            final Object fieldValue2 = mBeanDescriptor.getFieldValue("currencyTimeLimit");
                            if (fieldValue2 != null) {
                                s3 = fieldValue2.toString();
                            }
                            else {
                                s3 = null;
                            }
                        }
                        if (s3 != null && !s3.equals("-1")) {
                            if (loggable) {
                                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "setting cached value and lastUpdatedTime in descriptor");
                            }
                            descriptor.setField("value", o);
                            descriptor.setField("lastUpdatedTimeStamp", String.valueOf(new Date().getTime()));
                            attribute.setDescriptor(descriptor);
                            this.modelMBeanInfo.setDescriptor(descriptor, "attribute");
                            if (loggable) {
                                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "new descriptor is " + descriptor);
                                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "AttributeInfo descriptor is " + attribute.getDescriptor());
                                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "modelMBeanInfo: AttributeInfo descriptor is " + this.modelMBeanInfo.getDescriptor(s, "attribute").toString());
                            }
                        }
                    }
                    else {
                        if (loggable) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a null response from getMethod\n");
                        }
                        o = null;
                    }
                }
                else {
                    String s4 = "";
                    o = descriptor.getFieldValue("value");
                    if (o == null) {
                        s4 = "default ";
                        o = descriptor.getFieldValue("default");
                    }
                    if (loggable) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "could not find getMethod for " + s + ", returning descriptor " + s4 + "value");
                    }
                }
            }
            final String type = attribute.getType();
            if (o != null) {
                final String name = o.getClass().getName();
                if (!type.equals(name)) {
                    boolean b = false;
                    boolean b2 = false;
                    boolean b3 = false;
                    int i = 0;
                    while (i < RequiredModelMBean.primitiveTypes.length) {
                        if (type.equals(RequiredModelMBean.primitiveTypes[i])) {
                            b2 = true;
                            if (name.equals(RequiredModelMBean.primitiveWrappers[i])) {
                                b3 = true;
                                break;
                            }
                            break;
                        }
                        else {
                            ++i;
                        }
                    }
                    if (b2) {
                        if (!b3) {
                            b = true;
                        }
                    }
                    else {
                        boolean instance;
                        try {
                            final Class<?> class1 = o.getClass();
                            final Exception[] array = { null };
                            final Class clazz = RequiredModelMBean.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Class>)new PrivilegedAction<Class<?>>() {
                                @Override
                                public Class<?> run() {
                                    try {
                                        ReflectUtil.checkPackageAccess(type);
                                        return Class.forName(type, true, class1.getClassLoader());
                                    }
                                    catch (final Exception ex) {
                                        array[0] = ex;
                                        return null;
                                    }
                                }
                            }, AccessController.getContext(), this.acc);
                            if (array[0] != null) {
                                throw array[0];
                            }
                            instance = clazz.isInstance(o);
                        }
                        catch (final Exception ex) {
                            instance = false;
                            if (loggable) {
                                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exception: ", ex);
                            }
                        }
                        if (!instance) {
                            b = true;
                        }
                    }
                    if (b) {
                        if (loggable) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Wrong response type '" + type + "'");
                        }
                        throw new MBeanException(new InvalidAttributeValueException("Wrong value type received for get attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
                    }
                }
            }
        }
        catch (final MBeanException ex2) {
            throw ex2;
        }
        catch (final AttributeNotFoundException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed with " + ex4.getMessage() + " exception type " + ex4.getClass().toString());
            }
            throw new MBeanException(ex4, "An exception occurred while trying to get an attribute value: " + ex4.getMessage());
        }
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exit");
        }
        return o;
    }
    
    @Override
    public AttributeList getAttributes(final String[] array) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Entry");
        }
        if (array == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames must not be null"), "Exception occurred trying to get attributes of a RequiredModelMBean");
        }
        final AttributeList list = new AttributeList();
        for (int i = 0; i < array.length; ++i) {
            try {
                list.add(new Attribute(array[i], this.getAttribute(array[i])));
            }
            catch (final Exception ex) {
                if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                    JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Failed to get \"" + array[i] + "\": ", ex);
                }
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Exit");
        }
        return list;
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        final boolean loggable = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute()", "Entry");
        }
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attribute must not be null"), "Exception occurred trying to set an attribute of a RequiredModelMBean");
        }
        final String name = attribute.getName();
        final Object value = attribute.getValue();
        boolean b = false;
        final ModelMBeanAttributeInfo attribute2 = this.modelMBeanInfo.getAttribute(name);
        if (attribute2 == null) {
            throw new AttributeNotFoundException("setAttribute failed: " + name + " is not found ");
        }
        final Descriptor mBeanDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
        final Descriptor descriptor = attribute2.getDescriptor();
        if (descriptor == null) {
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setMethod failed " + name + " not in attributeDescriptor\n");
            }
            throw new InvalidAttributeValueException("Unable to resolve attribute value, no defined in descriptor for attribute");
        }
        if (!attribute2.isWritable()) {
            throw new AttributeNotFoundException("setAttribute failed: " + name + " is not writable ");
        }
        final String s = (String)descriptor.getFieldValue("setMethod");
        final String s2 = (String)descriptor.getFieldValue("getMethod");
        final String type = attribute2.getType();
        Object attribute3 = "Unknown";
        try {
            attribute3 = this.getAttribute(name);
        }
        catch (final Throwable t) {}
        final Attribute attribute4 = new Attribute(name, attribute3);
        if (s == null) {
            if (value != null) {
                try {
                    final Class<?> loadClass = this.loadClass(type);
                    if (!loadClass.isInstance(value)) {
                        throw new InvalidAttributeValueException(loadClass.getName() + " expected, " + value.getClass().getName() + " received.");
                    }
                }
                catch (final ClassNotFoundException ex) {
                    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Class " + type + " for attribute " + name + " not found: ", ex);
                    }
                }
            }
            b = true;
        }
        else {
            this.invoke(s, new Object[] { value }, new String[] { type });
        }
        final Object fieldValue = descriptor.getFieldValue("currencyTimeLimit");
        String s3;
        if (fieldValue != null) {
            s3 = fieldValue.toString();
        }
        else {
            s3 = null;
        }
        if (s3 == null && mBeanDescriptor != null) {
            final Object fieldValue2 = mBeanDescriptor.getFieldValue("currencyTimeLimit");
            if (fieldValue2 != null) {
                s3 = fieldValue2.toString();
            }
            else {
                s3 = null;
            }
        }
        final boolean b2 = s3 != null && !s3.equals("-1");
        if (s == null && !b2 && s2 != null) {
            throw new MBeanException(new ServiceNotFoundException("No setMethod field is defined in the descriptor for " + name + " attribute and caching is not enabled for it"));
        }
        if (b2 || b) {
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setting cached value of " + name + " to " + value);
            }
            descriptor.setField("value", value);
            if (b2) {
                descriptor.setField("lastUpdatedTimeStamp", String.valueOf(new Date().getTime()));
            }
            attribute2.setDescriptor(descriptor);
            this.modelMBeanInfo.setDescriptor(descriptor, "attribute");
            if (loggable) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "new descriptor is " + descriptor + ". AttributeInfo descriptor is " + attribute2.getDescriptor() + ". AttributeInfo descriptor is " + this.modelMBeanInfo.getDescriptor(name, "attribute"));
            }
        }
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "sending sendAttributeNotification");
        }
        this.sendAttributeChangeNotification(attribute4, attribute);
        if (loggable) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Exit");
        }
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList list) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Entry");
        }
        if (list == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attributes must not be null"), "Exception occurred trying to set attributes of a RequiredModelMBean");
        }
        final AttributeList list2 = new AttributeList();
        for (final Attribute attribute : list.asList()) {
            try {
                this.setAttribute(attribute);
                list2.add(attribute);
            }
            catch (final Exception ex) {
                list2.remove(attribute);
            }
        }
        return list2;
    }
    
    private ModelMBeanInfo createDefaultModelMBeanInfo() {
        return new ModelMBeanInfoSupport(this.getClass().getName(), "Default ModelMBean", null, null, null, null);
    }
    
    private synchronized void writeToLog(final String s, final String s2) throws Exception {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Notification Logging to " + s + ": " + s2);
        }
        if (s == null || s2 == null) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Bad input parameters, will not log this entry.");
            }
            return;
        }
        final FileOutputStream fileOutputStream = new FileOutputStream(s, true);
        try {
            final PrintStream printStream = new PrintStream(fileOutputStream);
            printStream.println(s2);
            printStream.close();
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Successfully opened log " + s);
            }
        }
        catch (final Exception ex) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Exception " + ex.toString() + " trying to write to the Notification log file " + s);
            }
            throw ex;
        }
        finally {
            fileOutputStream.close();
        }
    }
    
    @Override
    public void addNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws IllegalArgumentException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry");
        }
        if (notificationListener == null) {
            throw new IllegalArgumentException("notification listener must not be null");
        }
        if (this.generalBroadcaster == null) {
            this.generalBroadcaster = new NotificationBroadcasterSupport();
        }
        this.generalBroadcaster.addNotificationListener(notificationListener, notificationFilter, o);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "NotificationListener added");
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
        }
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        if (notificationListener == null) {
            throw new ListenerNotFoundException("Notification listener is null");
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Entry");
        }
        if (this.generalBroadcaster == null) {
            throw new ListenerNotFoundException("No notification listeners registered");
        }
        this.generalBroadcaster.removeNotificationListener(notificationListener);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Exit");
        }
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        if (notificationListener == null) {
            throw new ListenerNotFoundException("Notification listener is null");
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry");
        }
        if (this.generalBroadcaster == null) {
            throw new ListenerNotFoundException("No notification listeners registered");
        }
        this.generalBroadcaster.removeNotificationListener(notificationListener, notificationFilter, o);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
        }
    }
    
    @Override
    public void sendNotification(final Notification notification) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Entry");
        }
        if (notification == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("notification object must not be null"), "Exception occurred trying to send a notification from a RequiredModelMBean");
        }
        final Descriptor descriptor = this.modelMBeanInfo.getDescriptor(notification.getType(), "notification");
        final Descriptor mBeanDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
        if (descriptor != null) {
            String s = (String)descriptor.getFieldValue("log");
            if (s == null && mBeanDescriptor != null) {
                s = (String)mBeanDescriptor.getFieldValue("log");
            }
            if (s != null && (s.equalsIgnoreCase("t") || s.equalsIgnoreCase("true"))) {
                String s2 = (String)descriptor.getFieldValue("logfile");
                if (s2 == null && mBeanDescriptor != null) {
                    s2 = (String)mBeanDescriptor.getFieldValue("logfile");
                }
                if (s2 != null) {
                    try {
                        this.writeToLog(s2, "LogMsg: " + new Date(notification.getTimeStamp()).toString() + " " + notification.getType() + " " + notification.getMessage() + " Severity = " + (String)descriptor.getFieldValue("severity"));
                    }
                    catch (final Exception ex) {
                        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Failed to log " + notification.getType() + " notification: ", ex);
                        }
                    }
                }
            }
        }
        if (this.generalBroadcaster != null) {
            this.generalBroadcaster.sendNotification(notification);
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "sendNotification sent provided notification object");
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", " Exit");
        }
    }
    
    @Override
    public void sendNotification(final String s) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Entry");
        }
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("notification message must not be null"), "Exception occurred trying to send a text notification from a ModelMBean");
        }
        this.sendNotification(new Notification("jmx.modelmbean.generic", this, 1L, s));
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Notification sent");
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Exit");
        }
    }
    
    private static final boolean hasNotification(final ModelMBeanInfo modelMBeanInfo, final String s) {
        try {
            return modelMBeanInfo != null && modelMBeanInfo.getNotification(s) != null;
        }
        catch (final MBeanException ex) {
            return false;
        }
        catch (final RuntimeOperationsException ex2) {
            return false;
        }
    }
    
    private static final ModelMBeanNotificationInfo makeGenericInfo() {
        return new ModelMBeanNotificationInfo(new String[] { "jmx.modelmbean.generic" }, "GENERIC", "A text notification has been issued by the managed resource", new DescriptorSupport(new String[] { "name=GENERIC", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.modelmbean.generic" }));
    }
    
    private static final ModelMBeanNotificationInfo makeAttributeChangeInfo() {
        return new ModelMBeanNotificationInfo(new String[] { "jmx.attribute.change" }, "ATTRIBUTE_CHANGE", "Signifies that an observed MBean attribute value has changed", new DescriptorSupport(new String[] { "name=ATTRIBUTE_CHANGE", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.attribute.change" }));
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Entry");
        }
        final boolean hasNotification = hasNotification(this.modelMBeanInfo, "GENERIC");
        final boolean hasNotification2 = hasNotification(this.modelMBeanInfo, "ATTRIBUTE_CHANGE");
        final ModelMBeanNotificationInfo[] array = (ModelMBeanNotificationInfo[])this.modelMBeanInfo.getNotifications();
        final ModelMBeanNotificationInfo[] array2 = new ModelMBeanNotificationInfo[((array == null) ? 0 : array.length) + (hasNotification ? 0 : 1) + (hasNotification2 ? 0 : 1)];
        int n = 0;
        if (!hasNotification) {
            array2[n++] = makeGenericInfo();
        }
        if (!hasNotification2) {
            array2[n++] = makeAttributeChangeInfo();
        }
        final int length = array.length;
        final int n2 = n;
        for (int i = 0; i < length; ++i) {
            array2[n2 + i] = array[i];
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Exit");
        }
        return array2;
    }
    
    @Override
    public void addAttributeChangeNotificationListener(final NotificationListener notificationListener, final String s, final Object o) throws MBeanException, RuntimeOperationsException, IllegalArgumentException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Entry");
        }
        if (notificationListener == null) {
            throw new IllegalArgumentException("Listener to be registered must not be null");
        }
        if (this.attributeBroadcaster == null) {
            this.attributeBroadcaster = new NotificationBroadcasterSupport();
        }
        final AttributeChangeNotificationFilter attributeChangeNotificationFilter = new AttributeChangeNotificationFilter();
        final MBeanAttributeInfo[] attributes = this.modelMBeanInfo.getAttributes();
        boolean b = false;
        if (s == null) {
            if (attributes != null && attributes.length > 0) {
                for (int i = 0; i < attributes.length; ++i) {
                    attributeChangeNotificationFilter.enableAttribute(attributes[i].getName());
                }
            }
        }
        else {
            if (attributes != null && attributes.length > 0) {
                for (int j = 0; j < attributes.length; ++j) {
                    if (s.equals(attributes[j].getName())) {
                        b = true;
                        attributeChangeNotificationFilter.enableAttribute(s);
                        break;
                    }
                }
            }
            if (!b) {
                throw new RuntimeOperationsException(new IllegalArgumentException("The attribute name does not exist"), "Exception occurred trying to add an AttributeChangeNotification listener");
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            final Vector<String> enabledAttributes = attributeChangeNotificationFilter.getEnabledAttributes();
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Set attribute change filter to " + ((enabledAttributes.size() > 1) ? ("[" + enabledAttributes.firstElement() + ", ...]") : enabledAttributes.toString()));
        }
        this.attributeBroadcaster.addNotificationListener(notificationListener, attributeChangeNotificationFilter, o);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Notification listener added for " + s);
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Exit");
        }
    }
    
    @Override
    public void removeAttributeChangeNotificationListener(final NotificationListener notificationListener, final String s) throws MBeanException, RuntimeOperationsException, ListenerNotFoundException {
        if (notificationListener == null) {
            throw new ListenerNotFoundException("Notification listener is null");
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Entry");
        }
        if (this.attributeBroadcaster == null) {
            throw new ListenerNotFoundException("No attribute change notification listeners registered");
        }
        final MBeanAttributeInfo[] attributes = this.modelMBeanInfo.getAttributes();
        boolean b = false;
        if (attributes != null && attributes.length > 0) {
            for (int i = 0; i < attributes.length; ++i) {
                if (attributes[i].getName().equals(s)) {
                    b = true;
                    break;
                }
            }
        }
        if (!b && s != null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid attribute name"), "Exception occurred trying to remove attribute change notification listener");
        }
        this.attributeBroadcaster.removeNotificationListener(notificationListener);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Exit");
        }
    }
    
    @Override
    public void sendAttributeChangeNotification(final AttributeChangeNotification attributeChangeNotification) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Entry");
        }
        if (attributeChangeNotification == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attribute change notification object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean");
        }
        Object oldValue = attributeChangeNotification.getOldValue();
        Object newValue = attributeChangeNotification.getNewValue();
        if (oldValue == null) {
            oldValue = "null";
        }
        if (newValue == null) {
            newValue = "null";
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Sending AttributeChangeNotification with " + attributeChangeNotification.getAttributeName() + attributeChangeNotification.getAttributeType() + attributeChangeNotification.getNewValue() + attributeChangeNotification.getOldValue());
        }
        final Descriptor descriptor = this.modelMBeanInfo.getDescriptor(attributeChangeNotification.getType(), "notification");
        final Descriptor mBeanDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
        if (descriptor != null) {
            String s = (String)descriptor.getFieldValue("log");
            if (s == null && mBeanDescriptor != null) {
                s = (String)mBeanDescriptor.getFieldValue("log");
            }
            if (s != null && (s.equalsIgnoreCase("t") || s.equalsIgnoreCase("true"))) {
                String s2 = (String)descriptor.getFieldValue("logfile");
                if (s2 == null && mBeanDescriptor != null) {
                    s2 = (String)mBeanDescriptor.getFieldValue("logfile");
                }
                if (s2 != null) {
                    try {
                        this.writeToLog(s2, "LogMsg: " + new Date(attributeChangeNotification.getTimeStamp()).toString() + " " + attributeChangeNotification.getType() + " " + attributeChangeNotification.getMessage() + " Name = " + attributeChangeNotification.getAttributeName() + " Old value = " + oldValue + " New value = " + newValue);
                    }
                    catch (final Exception ex) {
                        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Failed to log " + attributeChangeNotification.getType() + " notification: ", ex);
                        }
                    }
                }
            }
        }
        else if (mBeanDescriptor != null) {
            final String s3 = (String)mBeanDescriptor.getFieldValue("log");
            if (s3 != null && (s3.equalsIgnoreCase("t") || s3.equalsIgnoreCase("true"))) {
                final String s4 = (String)mBeanDescriptor.getFieldValue("logfile");
                if (s4 != null) {
                    try {
                        this.writeToLog(s4, "LogMsg: " + new Date(attributeChangeNotification.getTimeStamp()).toString() + " " + attributeChangeNotification.getType() + " " + attributeChangeNotification.getMessage() + " Name = " + attributeChangeNotification.getAttributeName() + " Old value = " + oldValue + " New value = " + newValue);
                    }
                    catch (final Exception ex2) {
                        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
                            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Failed to log " + attributeChangeNotification.getType() + " notification: ", ex2);
                        }
                    }
                }
            }
        }
        if (this.attributeBroadcaster != null) {
            this.attributeBroadcaster.sendNotification(attributeChangeNotification);
        }
        if (this.generalBroadcaster != null) {
            this.generalBroadcaster.sendNotification(attributeChangeNotification);
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "sent notification");
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Exit");
        }
    }
    
    @Override
    public void sendAttributeChangeNotification(final Attribute attribute, final Attribute attribute2) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Entry");
        }
        if (attribute == null || attribute2 == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean");
        }
        if (!attribute.getName().equals(attribute2.getName())) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute names are not the same"), "Exception occurred trying to send attribute change notification of a ModelMBean");
        }
        final Object value = attribute2.getValue();
        final Object value2 = attribute.getValue();
        String s = "unknown";
        if (value != null) {
            s = value.getClass().getName();
        }
        if (value2 != null) {
            s = value2.getClass().getName();
        }
        this.sendAttributeChangeNotification(new AttributeChangeNotification(this, 1L, new Date().getTime(), "AttributeChangeDetected", attribute.getName(), s, attribute.getValue(), attribute2.getValue()));
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Exit");
        }
    }
    
    protected ClassLoaderRepository getClassLoaderRepository() {
        return MBeanServerFactory.getClassLoaderRepository(this.server);
    }
    
    private Class<?> loadClass(final String s) throws ClassNotFoundException {
        final AccessControlContext context = AccessController.getContext();
        final ClassNotFoundException[] array = { null };
        final Class clazz = RequiredModelMBean.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Class>)new PrivilegedAction<Class<?>>() {
            @Override
            public Class<?> run() {
                try {
                    ReflectUtil.checkPackageAccess(s);
                    return Class.forName(s);
                }
                catch (final ClassNotFoundException ex) {
                    final ClassLoaderRepository classLoaderRepository = RequiredModelMBean.this.getClassLoaderRepository();
                    try {
                        if (classLoaderRepository == null) {
                            throw new ClassNotFoundException(s);
                        }
                        return classLoaderRepository.loadClass(s);
                    }
                    catch (final ClassNotFoundException ex2) {
                        array[0] = ex2;
                        return null;
                    }
                }
            }
        }, context, this.acc);
        if (array[0] != null) {
            throw array[0];
        }
        return clazz;
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer server, final ObjectName objectName) throws Exception {
        if (objectName == null) {
            throw new NullPointerException("name of RequiredModelMBean to registered is null");
        }
        this.server = server;
        return objectName;
    }
    
    @Override
    public void postRegister(final Boolean b) {
        this.registered = b;
    }
    
    @Override
    public void preDeregister() throws Exception {
    }
    
    @Override
    public void postDeregister() {
        this.registered = false;
        this.server = null;
    }
    
    static {
        javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
        primitiveClasses = new Class[] { Integer.TYPE, Long.TYPE, Boolean.TYPE, Double.TYPE, Float.TYPE, Short.TYPE, Byte.TYPE, Character.TYPE };
        primitiveClassMap = new HashMap<String, Class<?>>();
        for (int i = 0; i < RequiredModelMBean.primitiveClasses.length; ++i) {
            final Class<?> clazz = RequiredModelMBean.primitiveClasses[i];
            RequiredModelMBean.primitiveClassMap.put(clazz.getName(), clazz);
        }
        primitiveTypes = new String[] { Boolean.TYPE.getName(), Byte.TYPE.getName(), Character.TYPE.getName(), Short.TYPE.getName(), Integer.TYPE.getName(), Long.TYPE.getName(), Float.TYPE.getName(), Double.TYPE.getName(), Void.TYPE.getName() };
        primitiveWrappers = new String[] { Boolean.class.getName(), Byte.class.getName(), Character.class.getName(), Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(), Double.class.getName(), Void.class.getName() };
    }
}
