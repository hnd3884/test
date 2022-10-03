package javax.management.modelmbean;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.Descriptor;
import java.io.ObjectStreamField;
import javax.management.MBeanInfo;

public class ModelMBeanInfoSupport extends MBeanInfo implements ModelMBeanInfo
{
    private static final long oldSerialVersionUID = -3944083498453227709L;
    private static final long newSerialVersionUID = -1935722590756516193L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private Descriptor modelMBeanDescriptor;
    private MBeanAttributeInfo[] modelMBeanAttributes;
    private MBeanConstructorInfo[] modelMBeanConstructors;
    private MBeanNotificationInfo[] modelMBeanNotifications;
    private MBeanOperationInfo[] modelMBeanOperations;
    private static final String ATTR = "attribute";
    private static final String OPER = "operation";
    private static final String NOTF = "notification";
    private static final String CONS = "constructor";
    private static final String MMB = "mbean";
    private static final String ALL = "all";
    private static final String currClass = "ModelMBeanInfoSupport";
    private static final ModelMBeanAttributeInfo[] NO_ATTRIBUTES;
    private static final ModelMBeanConstructorInfo[] NO_CONSTRUCTORS;
    private static final ModelMBeanNotificationInfo[] NO_NOTIFICATIONS;
    private static final ModelMBeanOperationInfo[] NO_OPERATIONS;
    
    public ModelMBeanInfoSupport(final ModelMBeanInfo modelMBeanInfo) {
        super(modelMBeanInfo.getClassName(), modelMBeanInfo.getDescription(), modelMBeanInfo.getAttributes(), modelMBeanInfo.getConstructors(), modelMBeanInfo.getOperations(), modelMBeanInfo.getNotifications());
        this.modelMBeanDescriptor = null;
        this.modelMBeanAttributes = modelMBeanInfo.getAttributes();
        this.modelMBeanConstructors = modelMBeanInfo.getConstructors();
        this.modelMBeanOperations = modelMBeanInfo.getOperations();
        this.modelMBeanNotifications = modelMBeanInfo.getNotifications();
        try {
            this.modelMBeanDescriptor = this.validDescriptor(modelMBeanInfo.getMBeanDescriptor());
        }
        catch (final MBeanException ex) {
            this.modelMBeanDescriptor = this.validDescriptor(null);
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfo(ModelMBeanInfo)", "Could not get a valid modelMBeanDescriptor, setting a default Descriptor");
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfo(ModelMBeanInfo)", "Exit");
        }
    }
    
    public ModelMBeanInfoSupport(final String s, final String s2, final ModelMBeanAttributeInfo[] array, final ModelMBeanConstructorInfo[] array2, final ModelMBeanOperationInfo[] array3, final ModelMBeanNotificationInfo[] array4) {
        this(s, s2, array, array2, array3, array4, null);
    }
    
    public ModelMBeanInfoSupport(final String s, final String s2, final ModelMBeanAttributeInfo[] modelMBeanAttributes, final ModelMBeanConstructorInfo[] modelMBeanConstructors, final ModelMBeanOperationInfo[] modelMBeanOperations, final ModelMBeanNotificationInfo[] modelMBeanNotifications, final Descriptor descriptor) {
        super(s, s2, (modelMBeanAttributes != null) ? modelMBeanAttributes : ModelMBeanInfoSupport.NO_ATTRIBUTES, (modelMBeanConstructors != null) ? modelMBeanConstructors : ModelMBeanInfoSupport.NO_CONSTRUCTORS, (modelMBeanOperations != null) ? modelMBeanOperations : ModelMBeanInfoSupport.NO_OPERATIONS, (modelMBeanNotifications != null) ? modelMBeanNotifications : ModelMBeanInfoSupport.NO_NOTIFICATIONS);
        this.modelMBeanDescriptor = null;
        this.modelMBeanAttributes = modelMBeanAttributes;
        this.modelMBeanConstructors = modelMBeanConstructors;
        this.modelMBeanOperations = modelMBeanOperations;
        this.modelMBeanNotifications = modelMBeanNotifications;
        this.modelMBeanDescriptor = this.validDescriptor(descriptor);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfoSupport(String,String,ModelMBeanAttributeInfo[],ModelMBeanConstructorInfo[],ModelMBeanOperationInfo[],ModelMBeanNotificationInfo[],Descriptor)", "Exit");
        }
    }
    
    @Override
    public Object clone() {
        return new ModelMBeanInfoSupport(this);
    }
    
    @Override
    public Descriptor[] getDescriptors(String s) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptors(String)", "Entry");
        }
        if (s == null || s.equals("")) {
            s = "all";
        }
        Descriptor[] array;
        if (s.equalsIgnoreCase("mbean")) {
            array = new Descriptor[] { this.modelMBeanDescriptor };
        }
        else if (s.equalsIgnoreCase("attribute")) {
            final MBeanAttributeInfo[] modelMBeanAttributes = this.modelMBeanAttributes;
            int length = 0;
            if (modelMBeanAttributes != null) {
                length = modelMBeanAttributes.length;
            }
            array = new Descriptor[length];
            for (int i = 0; i < length; ++i) {
                array[i] = ((ModelMBeanAttributeInfo)modelMBeanAttributes[i]).getDescriptor();
            }
        }
        else if (s.equalsIgnoreCase("operation")) {
            final MBeanOperationInfo[] modelMBeanOperations = this.modelMBeanOperations;
            int length2 = 0;
            if (modelMBeanOperations != null) {
                length2 = modelMBeanOperations.length;
            }
            array = new Descriptor[length2];
            for (int j = 0; j < length2; ++j) {
                array[j] = ((ModelMBeanOperationInfo)modelMBeanOperations[j]).getDescriptor();
            }
        }
        else if (s.equalsIgnoreCase("constructor")) {
            final MBeanConstructorInfo[] modelMBeanConstructors = this.modelMBeanConstructors;
            int length3 = 0;
            if (modelMBeanConstructors != null) {
                length3 = modelMBeanConstructors.length;
            }
            array = new Descriptor[length3];
            for (int k = 0; k < length3; ++k) {
                array[k] = ((ModelMBeanConstructorInfo)modelMBeanConstructors[k]).getDescriptor();
            }
        }
        else if (s.equalsIgnoreCase("notification")) {
            final MBeanNotificationInfo[] modelMBeanNotifications = this.modelMBeanNotifications;
            int length4 = 0;
            if (modelMBeanNotifications != null) {
                length4 = modelMBeanNotifications.length;
            }
            array = new Descriptor[length4];
            for (int l = 0; l < length4; ++l) {
                array[l] = ((ModelMBeanNotificationInfo)modelMBeanNotifications[l]).getDescriptor();
            }
        }
        else {
            if (!s.equalsIgnoreCase("all")) {
                throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor Type is invalid"), "Exception occurred trying to find the descriptors of the MBean");
            }
            final MBeanAttributeInfo[] modelMBeanAttributes2 = this.modelMBeanAttributes;
            int length5 = 0;
            if (modelMBeanAttributes2 != null) {
                length5 = modelMBeanAttributes2.length;
            }
            final MBeanOperationInfo[] modelMBeanOperations2 = this.modelMBeanOperations;
            int length6 = 0;
            if (modelMBeanOperations2 != null) {
                length6 = modelMBeanOperations2.length;
            }
            final MBeanConstructorInfo[] modelMBeanConstructors2 = this.modelMBeanConstructors;
            int length7 = 0;
            if (modelMBeanConstructors2 != null) {
                length7 = modelMBeanConstructors2.length;
            }
            final MBeanNotificationInfo[] modelMBeanNotifications2 = this.modelMBeanNotifications;
            int length8 = 0;
            if (modelMBeanNotifications2 != null) {
                length8 = modelMBeanNotifications2.length;
            }
            final int n = length5 + length7 + length6 + length8 + 1;
            array = new Descriptor[n];
            array[n - 1] = this.modelMBeanDescriptor;
            int n2 = 0;
            for (int n3 = 0; n3 < length5; ++n3) {
                array[n2] = ((ModelMBeanAttributeInfo)modelMBeanAttributes2[n3]).getDescriptor();
                ++n2;
            }
            for (int n4 = 0; n4 < length7; ++n4) {
                array[n2] = ((ModelMBeanConstructorInfo)modelMBeanConstructors2[n4]).getDescriptor();
                ++n2;
            }
            for (int n5 = 0; n5 < length6; ++n5) {
                array[n2] = ((ModelMBeanOperationInfo)modelMBeanOperations2[n5]).getDescriptor();
                ++n2;
            }
            for (int n6 = 0; n6 < length8; ++n6) {
                array[n2] = ((ModelMBeanNotificationInfo)modelMBeanNotifications2[n6]).getDescriptor();
                ++n2;
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptors(String)", "Exit");
        }
        return array;
    }
    
    @Override
    public void setDescriptors(final Descriptor[] array) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptors(Descriptor[])", "Entry");
        }
        if (array == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor list is invalid"), "Exception occurred trying to set the descriptors of the MBeanInfo");
        }
        if (array.length == 0) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.setDescriptor(array[i], null);
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptors(Descriptor[])", "Exit");
        }
    }
    
    public Descriptor getDescriptor(final String s) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptor(String)", "Entry");
        }
        return this.getDescriptor(s, null);
    }
    
    @Override
    public Descriptor getDescriptor(final String s, final String s2) throws MBeanException, RuntimeOperationsException {
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor is invalid"), "Exception occurred trying to set the descriptors of the MBeanInfo");
        }
        if ("mbean".equalsIgnoreCase(s2)) {
            return (Descriptor)this.modelMBeanDescriptor.clone();
        }
        if ("attribute".equalsIgnoreCase(s2) || s2 == null) {
            final ModelMBeanAttributeInfo attribute = this.getAttribute(s);
            if (attribute != null) {
                return attribute.getDescriptor();
            }
            if (s2 != null) {
                return null;
            }
        }
        if ("operation".equalsIgnoreCase(s2) || s2 == null) {
            final ModelMBeanOperationInfo operation = this.getOperation(s);
            if (operation != null) {
                return operation.getDescriptor();
            }
            if (s2 != null) {
                return null;
            }
        }
        if ("constructor".equalsIgnoreCase(s2) || s2 == null) {
            final ModelMBeanConstructorInfo constructor = this.getConstructor(s);
            if (constructor != null) {
                return constructor.getDescriptor();
            }
            if (s2 != null) {
                return null;
            }
        }
        if ("notification".equalsIgnoreCase(s2) || s2 == null) {
            final ModelMBeanNotificationInfo notification = this.getNotification(s);
            if (notification != null) {
                return notification.getDescriptor();
            }
            if (s2 != null) {
                return null;
            }
        }
        if (s2 == null) {
            return null;
        }
        throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor Type is invalid"), "Exception occurred trying to find the descriptors of the MBean");
    }
    
    @Override
    public void setDescriptor(Descriptor descriptor, String s) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Entry");
        }
        if (descriptor == null) {
            descriptor = new DescriptorSupport();
        }
        if (s == null || s.equals("")) {
            s = (String)descriptor.getFieldValue("descriptorType");
            if (s == null) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "descriptorType null in both String parameter and Descriptor, defaulting to mbean");
                s = "mbean";
            }
        }
        String className = (String)descriptor.getFieldValue("name");
        if (className == null) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "descriptor name null, defaulting to " + this.getClassName());
            className = this.getClassName();
        }
        boolean b = false;
        if (s.equalsIgnoreCase("mbean")) {
            this.setMBeanDescriptor(descriptor);
            b = true;
        }
        else if (s.equalsIgnoreCase("attribute")) {
            final MBeanAttributeInfo[] modelMBeanAttributes = this.modelMBeanAttributes;
            int length = 0;
            if (modelMBeanAttributes != null) {
                length = modelMBeanAttributes.length;
            }
            for (int i = 0; i < length; ++i) {
                if (className.equals(modelMBeanAttributes[i].getName())) {
                    b = true;
                    final ModelMBeanAttributeInfo modelMBeanAttributeInfo = (ModelMBeanAttributeInfo)modelMBeanAttributes[i];
                    modelMBeanAttributeInfo.setDescriptor(descriptor);
                    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Setting descriptor to " + descriptor + "\t\n local: AttributeInfo descriptor is " + modelMBeanAttributeInfo.getDescriptor() + "\t\n modelMBeanInfo: AttributeInfo descriptor is " + this.getDescriptor(className, "attribute"));
                    }
                }
            }
        }
        else if (s.equalsIgnoreCase("operation")) {
            final MBeanOperationInfo[] modelMBeanOperations = this.modelMBeanOperations;
            int length2 = 0;
            if (modelMBeanOperations != null) {
                length2 = modelMBeanOperations.length;
            }
            for (int j = 0; j < length2; ++j) {
                if (className.equals(modelMBeanOperations[j].getName())) {
                    b = true;
                    ((ModelMBeanOperationInfo)modelMBeanOperations[j]).setDescriptor(descriptor);
                }
            }
        }
        else if (s.equalsIgnoreCase("constructor")) {
            final MBeanConstructorInfo[] modelMBeanConstructors = this.modelMBeanConstructors;
            int length3 = 0;
            if (modelMBeanConstructors != null) {
                length3 = modelMBeanConstructors.length;
            }
            for (int k = 0; k < length3; ++k) {
                if (className.equals(modelMBeanConstructors[k].getName())) {
                    b = true;
                    ((ModelMBeanConstructorInfo)modelMBeanConstructors[k]).setDescriptor(descriptor);
                }
            }
        }
        else {
            if (!s.equalsIgnoreCase("notification")) {
                throw new RuntimeOperationsException(new IllegalArgumentException("Invalid descriptor type: " + s), "Exception occurred trying to set the descriptors of the MBean");
            }
            final MBeanNotificationInfo[] modelMBeanNotifications = this.modelMBeanNotifications;
            int length4 = 0;
            if (modelMBeanNotifications != null) {
                length4 = modelMBeanNotifications.length;
            }
            for (int l = 0; l < length4; ++l) {
                if (className.equals(modelMBeanNotifications[l].getName())) {
                    b = true;
                    ((ModelMBeanNotificationInfo)modelMBeanNotifications[l]).setDescriptor(descriptor);
                }
            }
        }
        if (!b) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor name is invalid: type=" + s + "; name=" + className), "Exception occurred trying to set the descriptors of the MBean");
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Exit");
        }
    }
    
    @Override
    public ModelMBeanAttributeInfo getAttribute(final String s) throws MBeanException, RuntimeOperationsException {
        ModelMBeanAttributeInfo modelMBeanAttributeInfo = null;
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "Entry");
        }
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute Name is null"), "Exception occurred trying to get the ModelMBeanAttributeInfo of the MBean");
        }
        final MBeanAttributeInfo[] modelMBeanAttributes = this.modelMBeanAttributes;
        int length = 0;
        if (modelMBeanAttributes != null) {
            length = modelMBeanAttributes.length;
        }
        for (int n = 0; n < length && modelMBeanAttributeInfo == null; ++n) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "\t\n this.getAttributes() MBeanAttributeInfo Array " + n + ":" + ((ModelMBeanAttributeInfo)modelMBeanAttributes[n]).getDescriptor() + "\t\n this.modelMBeanAttributes MBeanAttributeInfo Array " + n + ":" + ((ModelMBeanAttributeInfo)this.modelMBeanAttributes[n]).getDescriptor());
            }
            if (s.equals(modelMBeanAttributes[n].getName())) {
                modelMBeanAttributeInfo = (ModelMBeanAttributeInfo)modelMBeanAttributes[n].clone();
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "Exit");
        }
        return modelMBeanAttributeInfo;
    }
    
    @Override
    public ModelMBeanOperationInfo getOperation(final String s) throws MBeanException, RuntimeOperationsException {
        ModelMBeanOperationInfo modelMBeanOperationInfo = null;
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getOperation(String)", "Entry");
        }
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("inName is null"), "Exception occurred trying to get the ModelMBeanOperationInfo of the MBean");
        }
        final MBeanOperationInfo[] modelMBeanOperations = this.modelMBeanOperations;
        int length = 0;
        if (modelMBeanOperations != null) {
            length = modelMBeanOperations.length;
        }
        for (int n = 0; n < length && modelMBeanOperationInfo == null; ++n) {
            if (s.equals(modelMBeanOperations[n].getName())) {
                modelMBeanOperationInfo = (ModelMBeanOperationInfo)modelMBeanOperations[n].clone();
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getOperation(String)", "Exit");
        }
        return modelMBeanOperationInfo;
    }
    
    public ModelMBeanConstructorInfo getConstructor(final String s) throws MBeanException, RuntimeOperationsException {
        ModelMBeanConstructorInfo modelMBeanConstructorInfo = null;
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getConstructor(String)", "Entry");
        }
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Constructor name is null"), "Exception occurred trying to get the ModelMBeanConstructorInfo of the MBean");
        }
        final MBeanConstructorInfo[] modelMBeanConstructors = this.modelMBeanConstructors;
        int length = 0;
        if (modelMBeanConstructors != null) {
            length = modelMBeanConstructors.length;
        }
        for (int n = 0; n < length && modelMBeanConstructorInfo == null; ++n) {
            if (s.equals(modelMBeanConstructors[n].getName())) {
                modelMBeanConstructorInfo = (ModelMBeanConstructorInfo)modelMBeanConstructors[n].clone();
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getConstructor(String)", "Exit");
        }
        return modelMBeanConstructorInfo;
    }
    
    @Override
    public ModelMBeanNotificationInfo getNotification(final String s) throws MBeanException, RuntimeOperationsException {
        ModelMBeanNotificationInfo modelMBeanNotificationInfo = null;
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getNotification(String)", "Entry");
        }
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Notification name is null"), "Exception occurred trying to get the ModelMBeanNotificationInfo of the MBean");
        }
        final MBeanNotificationInfo[] modelMBeanNotifications = this.modelMBeanNotifications;
        int length = 0;
        if (modelMBeanNotifications != null) {
            length = modelMBeanNotifications.length;
        }
        for (int n = 0; n < length && modelMBeanNotificationInfo == null; ++n) {
            if (s.equals(modelMBeanNotifications[n].getName())) {
                modelMBeanNotificationInfo = (ModelMBeanNotificationInfo)modelMBeanNotifications[n].clone();
            }
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getNotification(String)", "Exit");
        }
        return modelMBeanNotificationInfo;
    }
    
    @Override
    public Descriptor getDescriptor() {
        return this.getMBeanDescriptorNoException();
    }
    
    @Override
    public Descriptor getMBeanDescriptor() throws MBeanException {
        return this.getMBeanDescriptorNoException();
    }
    
    private Descriptor getMBeanDescriptorNoException() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getMBeanDescriptorNoException()", "Entry");
        }
        if (this.modelMBeanDescriptor == null) {
            this.modelMBeanDescriptor = this.validDescriptor(null);
        }
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getMBeanDescriptorNoException()", "Exit, returning: " + this.modelMBeanDescriptor);
        }
        return (Descriptor)this.modelMBeanDescriptor.clone();
    }
    
    @Override
    public void setMBeanDescriptor(final Descriptor descriptor) throws MBeanException, RuntimeOperationsException {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setMBeanDescriptor(Descriptor)", "Entry");
        }
        this.modelMBeanDescriptor = this.validDescriptor(descriptor);
    }
    
    private Descriptor validDescriptor(final Descriptor descriptor) throws RuntimeOperationsException {
        final boolean b = descriptor == null;
        Descriptor descriptor2;
        if (b) {
            descriptor2 = new DescriptorSupport();
            JmxProperties.MODELMBEAN_LOGGER.finer("Null Descriptor, creating new.");
        }
        else {
            descriptor2 = (Descriptor)descriptor.clone();
        }
        if (b && descriptor2.getFieldValue("name") == null) {
            descriptor2.setField("name", this.getClassName());
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + this.getClassName());
        }
        if (b && descriptor2.getFieldValue("descriptorType") == null) {
            descriptor2.setField("descriptorType", "mbean");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"mbean\"");
        }
        if (descriptor2.getFieldValue("displayName") == null) {
            descriptor2.setField("displayName", this.getClassName());
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getClassName());
        }
        if (descriptor2.getFieldValue("persistPolicy") == null) {
            descriptor2.setField("persistPolicy", "never");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor persistPolicy to \"never\"");
        }
        if (descriptor2.getFieldValue("log") == null) {
            descriptor2.setField("log", "F");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor \"log\" field to \"F\"");
        }
        if (descriptor2.getFieldValue("visibility") == null) {
            descriptor2.setField("visibility", "1");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor visibility to 1");
        }
        if (!descriptor2.isValid()) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor2.toString());
        }
        if (!((String)descriptor2.getFieldValue("descriptorType")).equalsIgnoreCase("mbean")) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: mbean , was: " + descriptor2.getFieldValue("descriptorType"));
        }
        return descriptor2;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ModelMBeanInfoSupport.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            this.modelMBeanDescriptor = (Descriptor)fields.get("modelMBeanDescriptor", null);
            if (fields.defaulted("modelMBeanDescriptor")) {
                throw new NullPointerException("modelMBeanDescriptor");
            }
            this.modelMBeanAttributes = (MBeanAttributeInfo[])fields.get("mmbAttributes", null);
            if (fields.defaulted("mmbAttributes")) {
                throw new NullPointerException("mmbAttributes");
            }
            this.modelMBeanConstructors = (MBeanConstructorInfo[])fields.get("mmbConstructors", null);
            if (fields.defaulted("mmbConstructors")) {
                throw new NullPointerException("mmbConstructors");
            }
            this.modelMBeanNotifications = (MBeanNotificationInfo[])fields.get("mmbNotifications", null);
            if (fields.defaulted("mmbNotifications")) {
                throw new NullPointerException("mmbNotifications");
            }
            this.modelMBeanOperations = (MBeanOperationInfo[])fields.get("mmbOperations", null);
            if (fields.defaulted("mmbOperations")) {
                throw new NullPointerException("mmbOperations");
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (ModelMBeanInfoSupport.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("modelMBeanDescriptor", this.modelMBeanDescriptor);
            putFields.put("mmbAttributes", this.modelMBeanAttributes);
            putFields.put("mmbConstructors", this.modelMBeanConstructors);
            putFields.put("mmbNotifications", this.modelMBeanNotifications);
            putFields.put("mmbOperations", this.modelMBeanOperations);
            putFields.put("currClass", "ModelMBeanInfoSupport");
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("modelMBeanDescriptor", Descriptor.class), new ObjectStreamField("mmbAttributes", MBeanAttributeInfo[].class), new ObjectStreamField("mmbConstructors", MBeanConstructorInfo[].class), new ObjectStreamField("mmbNotifications", MBeanNotificationInfo[].class), new ObjectStreamField("mmbOperations", MBeanOperationInfo[].class), new ObjectStreamField("currClass", String.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("modelMBeanDescriptor", Descriptor.class), new ObjectStreamField("modelMBeanAttributes", MBeanAttributeInfo[].class), new ObjectStreamField("modelMBeanConstructors", MBeanConstructorInfo[].class), new ObjectStreamField("modelMBeanNotifications", MBeanNotificationInfo[].class), new ObjectStreamField("modelMBeanOperations", MBeanOperationInfo[].class) };
        ModelMBeanInfoSupport.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            ModelMBeanInfoSupport.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (ModelMBeanInfoSupport.compat) {
            serialPersistentFields = ModelMBeanInfoSupport.oldSerialPersistentFields;
            serialVersionUID = -3944083498453227709L;
        }
        else {
            serialPersistentFields = ModelMBeanInfoSupport.newSerialPersistentFields;
            serialVersionUID = -1935722590756516193L;
        }
        NO_ATTRIBUTES = new ModelMBeanAttributeInfo[0];
        NO_CONSTRUCTORS = new ModelMBeanConstructorInfo[0];
        NO_NOTIFICATIONS = new ModelMBeanNotificationInfo[0];
        NO_OPERATIONS = new ModelMBeanOperationInfo[0];
    }
}
