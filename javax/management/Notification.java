package javax.management;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.io.ObjectStreamField;
import java.util.EventObject;

public class Notification extends EventObject
{
    private static final long oldSerialVersionUID = 1716977971058914352L;
    private static final long newSerialVersionUID = -7516092053498031989L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private String type;
    private long sequenceNumber;
    private long timeStamp;
    private Object userData;
    private String message;
    protected Object source;
    
    public Notification(final String type, final Object source, final long sequenceNumber) {
        super(source);
        this.userData = null;
        this.message = "";
        this.source = null;
        this.source = source;
        this.type = type;
        this.sequenceNumber = sequenceNumber;
        this.timeStamp = new Date().getTime();
    }
    
    public Notification(final String type, final Object source, final long sequenceNumber, final String message) {
        super(source);
        this.userData = null;
        this.message = "";
        this.source = null;
        this.source = source;
        this.type = type;
        this.sequenceNumber = sequenceNumber;
        this.timeStamp = new Date().getTime();
        this.message = message;
    }
    
    public Notification(final String type, final Object source, final long sequenceNumber, final long timeStamp) {
        super(source);
        this.userData = null;
        this.message = "";
        this.source = null;
        this.source = source;
        this.type = type;
        this.sequenceNumber = sequenceNumber;
        this.timeStamp = timeStamp;
    }
    
    public Notification(final String type, final Object source, final long sequenceNumber, final long timeStamp, final String message) {
        super(source);
        this.userData = null;
        this.message = "";
        this.source = null;
        this.source = source;
        this.type = type;
        this.sequenceNumber = sequenceNumber;
        this.timeStamp = timeStamp;
        this.message = message;
    }
    
    public void setSource(final Object o) {
        super.source = o;
        this.source = o;
    }
    
    public long getSequenceNumber() {
        return this.sequenceNumber;
    }
    
    public void setSequenceNumber(final long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    public String getType() {
        return this.type;
    }
    
    public long getTimeStamp() {
        return this.timeStamp;
    }
    
    public void setTimeStamp(final long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Object getUserData() {
        return this.userData;
    }
    
    public void setUserData(final Object userData) {
        this.userData = userData;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[type=" + this.type + "][message=" + this.message + "]";
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        super.source = this.source;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (Notification.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("type", this.type);
            putFields.put("sequenceNumber", this.sequenceNumber);
            putFields.put("timeStamp", this.timeStamp);
            putFields.put("userData", this.userData);
            putFields.put("message", this.message);
            putFields.put("source", this.source);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("message", String.class), new ObjectStreamField("sequenceNumber", Long.TYPE), new ObjectStreamField("source", Object.class), new ObjectStreamField("sourceObjectName", ObjectName.class), new ObjectStreamField("timeStamp", Long.TYPE), new ObjectStreamField("type", String.class), new ObjectStreamField("userData", Object.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("message", String.class), new ObjectStreamField("sequenceNumber", Long.TYPE), new ObjectStreamField("source", Object.class), new ObjectStreamField("timeStamp", Long.TYPE), new ObjectStreamField("type", String.class), new ObjectStreamField("userData", Object.class) };
        Notification.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            Notification.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (Notification.compat) {
            serialPersistentFields = Notification.oldSerialPersistentFields;
            serialVersionUID = 1716977971058914352L;
        }
        else {
            serialPersistentFields = Notification.newSerialPersistentFields;
            serialVersionUID = -7516092053498031989L;
        }
    }
}
