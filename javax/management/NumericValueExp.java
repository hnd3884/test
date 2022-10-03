package javax.management;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;

class NumericValueExp extends QueryEval implements ValueExp
{
    private static final long oldSerialVersionUID = -6227876276058904000L;
    private static final long newSerialVersionUID = -4679739485102359104L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private Number val;
    private static boolean compat;
    
    public NumericValueExp() {
        this.val = 0.0;
    }
    
    NumericValueExp(final Number val) {
        this.val = 0.0;
        this.val = val;
    }
    
    public double doubleValue() {
        if (this.val instanceof Long || this.val instanceof Integer) {
            return (double)this.val.longValue();
        }
        return this.val.doubleValue();
    }
    
    public long longValue() {
        if (this.val instanceof Long || this.val instanceof Integer) {
            return this.val.longValue();
        }
        return (long)this.val.doubleValue();
    }
    
    public boolean isLong() {
        return this.val instanceof Long || this.val instanceof Integer;
    }
    
    @Override
    public String toString() {
        if (this.val == null) {
            return "null";
        }
        if (this.val instanceof Long || this.val instanceof Integer) {
            return Long.toString(this.val.longValue());
        }
        final double doubleValue = this.val.doubleValue();
        if (Double.isInfinite(doubleValue)) {
            return (doubleValue > 0.0) ? "(1.0 / 0.0)" : "(-1.0 / 0.0)";
        }
        if (Double.isNaN(doubleValue)) {
            return "(0.0 / 0.0)";
        }
        return Double.toString(doubleValue);
    }
    
    @Override
    public ValueExp apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        return this;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (NumericValueExp.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            final double value = fields.get("doubleVal", 0.0);
            if (fields.defaulted("doubleVal")) {
                throw new NullPointerException("doubleVal");
            }
            final long value2 = fields.get("longVal", 0L);
            if (fields.defaulted("longVal")) {
                throw new NullPointerException("longVal");
            }
            final boolean value3 = fields.get("valIsLong", false);
            if (fields.defaulted("valIsLong")) {
                throw new NullPointerException("valIsLong");
            }
            if (value3) {
                this.val = value2;
            }
            else {
                this.val = value;
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (NumericValueExp.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("doubleVal", this.doubleValue());
            putFields.put("longVal", this.longValue());
            putFields.put("valIsLong", this.isLong());
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    @Deprecated
    @Override
    public void setMBeanServer(final MBeanServer mBeanServer) {
        super.setMBeanServer(mBeanServer);
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("longVal", Long.TYPE), new ObjectStreamField("doubleVal", Double.TYPE), new ObjectStreamField("valIsLong", Boolean.TYPE) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("val", Number.class) };
        NumericValueExp.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            NumericValueExp.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (NumericValueExp.compat) {
            serialPersistentFields = NumericValueExp.oldSerialPersistentFields;
            serialVersionUID = -6227876276058904000L;
        }
        else {
            serialPersistentFields = NumericValueExp.newSerialPersistentFields;
            serialVersionUID = -4679739485102359104L;
        }
    }
}
