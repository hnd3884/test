package javax.management.modelmbean;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;

public class XMLParseException extends Exception
{
    private static final long oldSerialVersionUID = -7780049316655891976L;
    private static final long newSerialVersionUID = 3176664577895105181L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    
    public XMLParseException() {
        super("XML Parse Exception.");
    }
    
    public XMLParseException(final String s) {
        super("XML Parse Exception: " + s);
    }
    
    public XMLParseException(final Exception ex, final String s) {
        super("XML Parse Exception: " + s + ":" + ex.toString());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (XMLParseException.compat) {
            objectOutputStream.putFields().put("msgStr", this.getMessage());
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("msgStr", String.class) };
        newSerialPersistentFields = new ObjectStreamField[0];
        XMLParseException.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            XMLParseException.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (XMLParseException.compat) {
            serialPersistentFields = XMLParseException.oldSerialPersistentFields;
            serialVersionUID = -7780049316655891976L;
        }
        else {
            serialPersistentFields = XMLParseException.newSerialPersistentFields;
            serialVersionUID = 3176664577895105181L;
        }
    }
}
