package javax.management.modelmbean;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.management.RuntimeOperationsException;
import javax.management.IntrospectionException;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.lang.reflect.Method;
import javax.management.Descriptor;
import java.io.ObjectStreamField;
import javax.management.DescriptorAccess;
import javax.management.MBeanAttributeInfo;

public class ModelMBeanAttributeInfo extends MBeanAttributeInfo implements DescriptorAccess
{
    private static final long oldSerialVersionUID = 7098036920755973145L;
    private static final long newSerialVersionUID = 6181543027787327345L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private Descriptor attrDescriptor;
    private static final String currClass = "ModelMBeanAttributeInfo";
    
    public ModelMBeanAttributeInfo(final String s, final String s2, final Method method, final Method method2) throws IntrospectionException {
        super(s, s2, method, method2);
        this.attrDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,Method,Method)", "Entry", s);
        }
        this.attrDescriptor = this.validDescriptor(null);
    }
    
    public ModelMBeanAttributeInfo(final String s, final String s2, final Method method, final Method method2, final Descriptor descriptor) throws IntrospectionException {
        super(s, s2, method, method2);
        this.attrDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,Method,Method,Descriptor)", "Entry", s);
        }
        this.attrDescriptor = this.validDescriptor(descriptor);
    }
    
    public ModelMBeanAttributeInfo(final String s, final String s2, final String s3, final boolean b, final boolean b2, final boolean b3) {
        super(s, s2, s3, b, b2, b3);
        this.attrDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,String,boolean,boolean,boolean)", "Entry", s);
        }
        this.attrDescriptor = this.validDescriptor(null);
    }
    
    public ModelMBeanAttributeInfo(final String s, final String s2, final String s3, final boolean b, final boolean b2, final boolean b3, final Descriptor descriptor) {
        super(s, s2, s3, b, b2, b3);
        this.attrDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,String,boolean,boolean,boolean,Descriptor)", "Entry", s);
        }
        this.attrDescriptor = this.validDescriptor(descriptor);
    }
    
    public ModelMBeanAttributeInfo(final ModelMBeanAttributeInfo modelMBeanAttributeInfo) {
        super(modelMBeanAttributeInfo.getName(), modelMBeanAttributeInfo.getType(), modelMBeanAttributeInfo.getDescription(), modelMBeanAttributeInfo.isReadable(), modelMBeanAttributeInfo.isWritable(), modelMBeanAttributeInfo.isIs());
        this.attrDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(ModelMBeanAttributeInfo)", "Entry");
        }
        this.attrDescriptor = this.validDescriptor(modelMBeanAttributeInfo.getDescriptor());
    }
    
    @Override
    public Descriptor getDescriptor() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "getDescriptor()", "Entry");
        }
        if (this.attrDescriptor == null) {
            this.attrDescriptor = this.validDescriptor(null);
        }
        return (Descriptor)this.attrDescriptor.clone();
    }
    
    @Override
    public void setDescriptor(final Descriptor descriptor) {
        this.attrDescriptor = this.validDescriptor(descriptor);
    }
    
    @Override
    public Object clone() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "clone()", "Entry");
        }
        return new ModelMBeanAttributeInfo(this);
    }
    
    @Override
    public String toString() {
        return "ModelMBeanAttributeInfo: " + this.getName() + " ; Description: " + this.getDescription() + " ; Types: " + this.getType() + " ; isReadable: " + this.isReadable() + " ; isWritable: " + this.isWritable() + " ; Descriptor: " + this.getDescriptor();
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
            descriptor2.setField("descriptorType", "attribute");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"attribute\"");
        }
        if (descriptor2.getFieldValue("displayName") == null) {
            descriptor2.setField("displayName", this.getName());
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getName());
        }
        if (!descriptor2.isValid()) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor2.toString());
        }
        if (!this.getName().equalsIgnoreCase((String)descriptor2.getFieldValue("name"))) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + this.getName() + " , was: " + descriptor2.getFieldValue("name"));
        }
        if (!"attribute".equalsIgnoreCase((String)descriptor2.getFieldValue("descriptorType"))) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"attribute\" , was: " + descriptor2.getFieldValue("descriptorType"));
        }
        return descriptor2;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (ModelMBeanAttributeInfo.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("attrDescriptor", this.attrDescriptor);
            putFields.put("currClass", "ModelMBeanAttributeInfo");
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("attrDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("attrDescriptor", Descriptor.class) };
        ModelMBeanAttributeInfo.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            ModelMBeanAttributeInfo.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (ModelMBeanAttributeInfo.compat) {
            serialPersistentFields = ModelMBeanAttributeInfo.oldSerialPersistentFields;
            serialVersionUID = 7098036920755973145L;
        }
        else {
            serialPersistentFields = ModelMBeanAttributeInfo.newSerialPersistentFields;
            serialVersionUID = 6181543027787327345L;
        }
    }
}
