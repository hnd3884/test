package javax.management.modelmbean;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;

public class InvalidTargetObjectTypeException extends Exception
{
    private static final long oldSerialVersionUID = 3711724570458346634L;
    private static final long newSerialVersionUID = 1190536278266811217L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    Exception exception;
    
    public InvalidTargetObjectTypeException() {
        super("InvalidTargetObjectTypeException: ");
        this.exception = null;
    }
    
    public InvalidTargetObjectTypeException(final String s) {
        super("InvalidTargetObjectTypeException: " + s);
        this.exception = null;
    }
    
    public InvalidTargetObjectTypeException(final Exception exception, final String s) {
        super("InvalidTargetObjectTypeException: " + s + ((exception != null) ? ("\n\t triggered by:" + exception.toString()) : ""));
        this.exception = exception;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (InvalidTargetObjectTypeException.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            this.exception = (Exception)fields.get("relatedExcept", null);
            if (fields.defaulted("relatedExcept")) {
                throw new NullPointerException("relatedExcept");
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (InvalidTargetObjectTypeException.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("relatedExcept", this.exception);
            putFields.put("msgStr", (this.exception != null) ? this.exception.getMessage() : "");
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("msgStr", String.class), new ObjectStreamField("relatedExcept", Exception.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("exception", Exception.class) };
        InvalidTargetObjectTypeException.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            InvalidTargetObjectTypeException.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (InvalidTargetObjectTypeException.compat) {
            serialPersistentFields = InvalidTargetObjectTypeException.oldSerialPersistentFields;
            serialVersionUID = 3711724570458346634L;
        }
        else {
            serialPersistentFields = InvalidTargetObjectTypeException.newSerialPersistentFields;
            serialVersionUID = 1190536278266811217L;
        }
    }
}
