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
import java.lang.reflect.Method;
import javax.management.Descriptor;
import java.io.ObjectStreamField;
import javax.management.DescriptorAccess;
import javax.management.MBeanOperationInfo;

public class ModelMBeanOperationInfo extends MBeanOperationInfo implements DescriptorAccess
{
    private static final long oldSerialVersionUID = 9087646304346171239L;
    private static final long newSerialVersionUID = 6532732096650090465L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private Descriptor operationDescriptor;
    private static final String currClass = "ModelMBeanOperationInfo";
    
    public ModelMBeanOperationInfo(final String s, final Method method) {
        super(s, method);
        this.operationDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,Method)", "Entry");
        }
        this.operationDescriptor = this.validDescriptor(null);
    }
    
    public ModelMBeanOperationInfo(final String s, final Method method, final Descriptor descriptor) {
        super(s, method);
        this.operationDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,Method,Descriptor)", "Entry");
        }
        this.operationDescriptor = this.validDescriptor(descriptor);
    }
    
    public ModelMBeanOperationInfo(final String s, final String s2, final MBeanParameterInfo[] array, final String s3, final int n) {
        super(s, s2, array, s3, n);
        this.operationDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,String,MBeanParameterInfo[],String,int)", "Entry");
        }
        this.operationDescriptor = this.validDescriptor(null);
    }
    
    public ModelMBeanOperationInfo(final String s, final String s2, final MBeanParameterInfo[] array, final String s3, final int n, final Descriptor descriptor) {
        super(s, s2, array, s3, n);
        this.operationDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,String,MBeanParameterInfo[],String,int,Descriptor)", "Entry");
        }
        this.operationDescriptor = this.validDescriptor(descriptor);
    }
    
    public ModelMBeanOperationInfo(final ModelMBeanOperationInfo modelMBeanOperationInfo) {
        super(modelMBeanOperationInfo.getName(), modelMBeanOperationInfo.getDescription(), modelMBeanOperationInfo.getSignature(), modelMBeanOperationInfo.getReturnType(), modelMBeanOperationInfo.getImpact());
        this.operationDescriptor = this.validDescriptor(null);
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(ModelMBeanOperationInfo)", "Entry");
        }
        this.operationDescriptor = this.validDescriptor(modelMBeanOperationInfo.getDescriptor());
    }
    
    @Override
    public Object clone() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "clone()", "Entry");
        }
        return new ModelMBeanOperationInfo(this);
    }
    
    @Override
    public Descriptor getDescriptor() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "getDescriptor()", "Entry");
        }
        if (this.operationDescriptor == null) {
            this.operationDescriptor = this.validDescriptor(null);
        }
        return (Descriptor)this.operationDescriptor.clone();
    }
    
    @Override
    public void setDescriptor(final Descriptor descriptor) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "setDescriptor(Descriptor)", "Entry");
        }
        this.operationDescriptor = this.validDescriptor(descriptor);
    }
    
    @Override
    public String toString() {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "toString()", "Entry");
        }
        String s = "ModelMBeanOperationInfo: " + this.getName() + " ; Description: " + this.getDescription() + " ; Descriptor: " + this.getDescriptor() + " ; ReturnType: " + this.getReturnType() + " ; Signature: ";
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
            descriptor2.setField("role", "operation");
            JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor role field to \"operation\"");
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
        final String s = (String)descriptor2.getFieldValue("role");
        if (!s.equalsIgnoreCase("operation") && !s.equalsIgnoreCase("setter") && !s.equalsIgnoreCase("getter")) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"role\" field does not match the object described.  Expected: \"operation\", \"setter\", or \"getter\" , was: " + descriptor2.getFieldValue("role"));
        }
        final Object fieldValue = descriptor2.getFieldValue("targetType");
        if (fieldValue != null && !(fieldValue instanceof String)) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor field \"targetValue\" is invalid class.  Expected: java.lang.String,  was: " + fieldValue.getClass().getName());
        }
        return descriptor2;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (ModelMBeanOperationInfo.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("operationDescriptor", this.operationDescriptor);
            putFields.put("currClass", "ModelMBeanOperationInfo");
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("operationDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("operationDescriptor", Descriptor.class) };
        ModelMBeanOperationInfo.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            ModelMBeanOperationInfo.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (ModelMBeanOperationInfo.compat) {
            serialPersistentFields = ModelMBeanOperationInfo.oldSerialPersistentFields;
            serialVersionUID = 9087646304346171239L;
        }
        else {
            serialPersistentFields = ModelMBeanOperationInfo.newSerialPersistentFields;
            serialVersionUID = 6532732096650090465L;
        }
    }
}
