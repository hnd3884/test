package javax.management.modelmbean;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanParameterInfo;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.lang.reflect.Constructor;
import javax.management.Descriptor;
import java.io.ObjectStreamField;
import javax.management.DescriptorAccess;
import javax.management.MBeanConstructorInfo;

public class ModelMBeanConstructorInfo extends MBeanConstructorInfo implements DescriptorAccess
{
    private static final long oldSerialVersionUID = -4440125391095574518L;
    private static final long newSerialVersionUID = 3862947819818064362L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private Descriptor consDescriptor;
    private static final String currClass = "ModelMBeanConstructorInfo";
    
    public ModelMBeanConstructorInfo(final String s, final Constructor<?> constructor) {
        super(s, constructor);
        this.consDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,Constructor)", "Entry");
        }
        this.consDescriptor = this.validDescriptor(null);
    }
    
    public ModelMBeanConstructorInfo(final String s, final Constructor<?> constructor, final Descriptor descriptor) {
        super(s, constructor);
        this.consDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,Constructor,Descriptor)", "Entry");
        }
        this.consDescriptor = this.validDescriptor(descriptor);
    }
    
    public ModelMBeanConstructorInfo(final String s, final String s2, final MBeanParameterInfo[] array) {
        super(s, s2, array);
        this.consDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,String,MBeanParameterInfo[])", "Entry");
        }
        this.consDescriptor = this.validDescriptor(null);
    }
    
    public ModelMBeanConstructorInfo(final String s, final String s2, final MBeanParameterInfo[] array, final Descriptor descriptor) {
        super(s, s2, array);
        this.consDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,String,MBeanParameterInfo[],Descriptor)", "Entry");
        }
        this.consDescriptor = this.validDescriptor(descriptor);
    }
    
    ModelMBeanConstructorInfo(final ModelMBeanConstructorInfo modelMBeanConstructorInfo) {
        super(modelMBeanConstructorInfo.getName(), modelMBeanConstructorInfo.getDescription(), modelMBeanConstructorInfo.getSignature());
        this.consDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(ModelMBeanConstructorInfo)", "Entry");
        }
        this.consDescriptor = this.validDescriptor(this.consDescriptor);
    }
    
    @Override
    public Object clone() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "clone()", "Entry");
        }
        return new ModelMBeanConstructorInfo(this);
    }
    
    @Override
    public Descriptor getDescriptor() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "getDescriptor()", "Entry");
        }
        if (this.consDescriptor == null) {
            this.consDescriptor = this.validDescriptor(null);
        }
        return (Descriptor)this.consDescriptor.clone();
    }
    
    @Override
    public void setDescriptor(final Descriptor descriptor) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "setDescriptor()", "Entry");
        }
        this.consDescriptor = this.validDescriptor(descriptor);
    }
    
    @Override
    public String toString() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "toString()", "Entry");
        }
        String s = "ModelMBeanConstructorInfo: " + this.getName() + " ; Description: " + this.getDescription() + " ; Descriptor: " + this.getDescriptor() + " ; Signature: ";
        final MBeanParameterInfo[] signature = this.getSignature();
        for (int i = 0; i < signature.length; ++i) {
            s = s.concat(signature[i].getType() + ", ");
        }
        return s;
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
            descriptor2.setField("descriptorType", "operation");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"operation\"");
        }
        if (descriptor2.getFieldValue("displayName") == null) {
            descriptor2.setField("displayName", this.getName());
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getName());
        }
        if (descriptor2.getFieldValue("role") == null) {
            descriptor2.setField("role", "constructor");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor role field to \"constructor\"");
        }
        if (!descriptor2.isValid()) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor2.toString());
        }
        if (!this.getName().equalsIgnoreCase((String)descriptor2.getFieldValue("name"))) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + this.getName() + " , was: " + descriptor2.getFieldValue("name"));
        }
        if (!"operation".equalsIgnoreCase((String)descriptor2.getFieldValue("descriptorType"))) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"operation\" , was: " + descriptor2.getFieldValue("descriptorType"));
        }
        if (!((String)descriptor2.getFieldValue("role")).equalsIgnoreCase("constructor")) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"role\" field does not match the object described.  Expected: \"constructor\" , was: " + descriptor2.getFieldValue("role"));
        }
        return descriptor2;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (ModelMBeanConstructorInfo.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("consDescriptor", this.consDescriptor);
            putFields.put("currClass", "ModelMBeanConstructorInfo");
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("consDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("consDescriptor", Descriptor.class) };
        ModelMBeanConstructorInfo.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            ModelMBeanConstructorInfo.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (ModelMBeanConstructorInfo.compat) {
            serialPersistentFields = ModelMBeanConstructorInfo.oldSerialPersistentFields;
            serialVersionUID = -4440125391095574518L;
        }
        else {
            serialPersistentFields = ModelMBeanConstructorInfo.newSerialPersistentFields;
            serialVersionUID = 3862947819818064362L;
        }
    }
}
