package javax.management.modelmbean;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.management.RuntimeOperationsException;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.Descriptor;
import java.io.ObjectStreamField;
import javax.management.DescriptorAccess;
import javax.management.MBeanNotificationInfo;

public class ModelMBeanNotificationInfo extends MBeanNotificationInfo implements DescriptorAccess
{
    private static final long oldSerialVersionUID = -5211564525059047097L;
    private static final long newSerialVersionUID = -7445681389570207141L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private Descriptor notificationDescriptor;
    private static final String currClass = "ModelMBeanNotificationInfo";
    
    public ModelMBeanNotificationInfo(final String[] array, final String s, final String s2) {
        this(array, s, s2, null);
    }
    
    public ModelMBeanNotificationInfo(final String[] array, final String s, final String s2, final Descriptor descriptor) {
        super(array, s, s2);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "ModelMBeanNotificationInfo", "Entry");
        }
        this.notificationDescriptor = this.validDescriptor(descriptor);
    }
    
    public ModelMBeanNotificationInfo(final ModelMBeanNotificationInfo modelMBeanNotificationInfo) {
        this(modelMBeanNotificationInfo.getNotifTypes(), modelMBeanNotificationInfo.getName(), modelMBeanNotificationInfo.getDescription(), modelMBeanNotificationInfo.getDescriptor());
    }
    
    @Override
    public Object clone() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "clone()", "Entry");
        }
        return new ModelMBeanNotificationInfo(this);
    }
    
    @Override
    public Descriptor getDescriptor() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "getDescriptor()", "Entry");
        }
        if (this.notificationDescriptor == null) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "getDescriptor()", "Descriptor value is null, setting descriptor to default values");
            }
            this.notificationDescriptor = this.validDescriptor(null);
        }
        return (Descriptor)this.notificationDescriptor.clone();
    }
    
    @Override
    public void setDescriptor(final Descriptor descriptor) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "setDescriptor(Descriptor)", "Entry");
        }
        this.notificationDescriptor = this.validDescriptor(descriptor);
    }
    
    @Override
    public String toString() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "toString()", "Entry");
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("ModelMBeanNotificationInfo: ").append(this.getName());
        sb.append(" ; Description: ").append(this.getDescription());
        sb.append(" ; Descriptor: ").append(this.getDescriptor());
        sb.append(" ; Types: ");
        final String[] notifTypes = this.getNotifTypes();
        for (int i = 0; i < notifTypes.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(notifTypes[i]);
        }
        return sb.toString();
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
            descriptor2.setField("name", this.getName());
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + this.getName());
        }
        if (b && descriptor2.getFieldValue("descriptorType") == null) {
            descriptor2.setField("descriptorType", "notification");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"notification\"");
        }
        if (descriptor2.getFieldValue("displayName") == null) {
            descriptor2.setField("displayName", this.getName());
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getName());
        }
        if (descriptor2.getFieldValue("severity") == null) {
            descriptor2.setField("severity", "6");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor severity field to 6");
        }
        if (!descriptor2.isValid()) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor2.toString());
        }
        if (!this.getName().equalsIgnoreCase((String)descriptor2.getFieldValue("name"))) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + this.getName() + " , was: " + descriptor2.getFieldValue("name"));
        }
        if (!"notification".equalsIgnoreCase((String)descriptor2.getFieldValue("descriptorType"))) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"notification\" , was: " + descriptor2.getFieldValue("descriptorType"));
        }
        return descriptor2;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (ModelMBeanNotificationInfo.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("notificationDescriptor", this.notificationDescriptor);
            putFields.put("currClass", "ModelMBeanNotificationInfo");
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("notificationDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("notificationDescriptor", Descriptor.class) };
        ModelMBeanNotificationInfo.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            ModelMBeanNotificationInfo.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (ModelMBeanNotificationInfo.compat) {
            serialPersistentFields = ModelMBeanNotificationInfo.oldSerialPersistentFields;
            serialVersionUID = -5211564525059047097L;
        }
        else {
            serialPersistentFields = ModelMBeanNotificationInfo.newSerialPersistentFields;
            serialVersionUID = -7445681389570207141L;
        }
    }
}
