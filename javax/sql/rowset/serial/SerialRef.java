package javax.sql.rowset.serial;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.Map;
import java.sql.SQLException;
import java.io.Serializable;
import java.sql.Ref;

public class SerialRef implements Ref, Serializable, Cloneable
{
    private String baseTypeName;
    private Object object;
    private Ref reference;
    static final long serialVersionUID = -4727123500609662274L;
    
    public SerialRef(final Ref ref) throws SerialException, SQLException {
        if (ref == null) {
            throw new SQLException("Cannot instantiate a SerialRef object with a null Ref object");
        }
        this.reference = ref;
        this.object = ref;
        if (ref.getBaseTypeName() == null) {
            throw new SQLException("Cannot instantiate a SerialRef object that returns a null base type name");
        }
        this.baseTypeName = ref.getBaseTypeName();
    }
    
    @Override
    public String getBaseTypeName() throws SerialException {
        return this.baseTypeName;
    }
    
    @Override
    public Object getObject(final Map<String, Class<?>> map) throws SerialException {
        final Hashtable hashtable = new Hashtable((Map<? extends K, ? extends V>)map);
        if (this.object != null) {
            return hashtable.get(this.object);
        }
        throw new SerialException("The object is not set");
    }
    
    @Override
    public Object getObject() throws SerialException {
        if (this.reference != null) {
            try {
                return this.reference.getObject();
            }
            catch (final SQLException ex) {
                throw new SerialException("SQLException: " + ex.getMessage());
            }
        }
        if (this.object != null) {
            return this.object;
        }
        throw new SerialException("The object is not set");
    }
    
    @Override
    public void setObject(final Object o) throws SerialException {
        try {
            this.reference.setObject(o);
        }
        catch (final SQLException ex) {
            throw new SerialException("SQLException: " + ex.getMessage());
        }
        this.object = o;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SerialRef) {
            final SerialRef serialRef = (SerialRef)o;
            return this.baseTypeName.equals(serialRef.baseTypeName) && this.object.equals(serialRef.object);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (31 + this.object.hashCode()) * 31 + this.baseTypeName.hashCode();
    }
    
    public Object clone() {
        try {
            final SerialRef serialRef = (SerialRef)super.clone();
            serialRef.reference = null;
            return serialRef;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        this.object = fields.get("object", null);
        this.baseTypeName = (String)fields.get("baseTypeName", null);
        this.reference = (Ref)fields.get("reference", null);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("baseTypeName", this.baseTypeName);
        putFields.put("object", this.object);
        putFields.put("reference", (this.reference instanceof Serializable) ? this.reference : null);
        objectOutputStream.writeFields();
    }
}
