package javax.sql.rowset.serial;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Array;
import java.sql.Ref;
import java.sql.Clob;
import java.sql.Blob;
import java.util.Arrays;
import java.sql.SQLOutput;
import java.util.Vector;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.Map;
import java.io.Serializable;
import java.sql.Struct;

public class SerialStruct implements Struct, Serializable, Cloneable
{
    private String SQLTypeName;
    private Object[] attribs;
    static final long serialVersionUID = -8322445504027483372L;
    
    public SerialStruct(final Struct struct, final Map<String, Class<?>> map) throws SerialException {
        try {
            this.SQLTypeName = struct.getSQLTypeName();
            System.out.println("SQLTypeName: " + this.SQLTypeName);
            this.attribs = struct.getAttributes(map);
            this.mapToSerial(map);
        }
        catch (final SQLException ex) {
            throw new SerialException(ex.getMessage());
        }
    }
    
    public SerialStruct(final SQLData sqlData, final Map<String, Class<?>> map) throws SerialException {
        try {
            this.SQLTypeName = sqlData.getSQLTypeName();
            final Vector vector = new Vector();
            sqlData.writeSQL(new SQLOutputImpl(vector, map));
            this.attribs = vector.toArray();
        }
        catch (final SQLException ex) {
            throw new SerialException(ex.getMessage());
        }
    }
    
    @Override
    public String getSQLTypeName() throws SerialException {
        return this.SQLTypeName;
    }
    
    @Override
    public Object[] getAttributes() throws SerialException {
        final Object[] attribs = this.attribs;
        return (Object[])((attribs == null) ? null : Arrays.copyOf(attribs, attribs.length));
    }
    
    @Override
    public Object[] getAttributes(final Map<String, Class<?>> map) throws SerialException {
        final Object[] attribs = this.attribs;
        return (Object[])((attribs == null) ? null : Arrays.copyOf(attribs, attribs.length));
    }
    
    private void mapToSerial(final Map<String, Class<?>> map) throws SerialException {
        try {
            for (int i = 0; i < this.attribs.length; ++i) {
                if (this.attribs[i] instanceof Struct) {
                    this.attribs[i] = new SerialStruct((Struct)this.attribs[i], map);
                }
                else if (this.attribs[i] instanceof SQLData) {
                    this.attribs[i] = new SerialStruct((SQLData)this.attribs[i], map);
                }
                else if (this.attribs[i] instanceof Blob) {
                    this.attribs[i] = new SerialBlob((Blob)this.attribs[i]);
                }
                else if (this.attribs[i] instanceof Clob) {
                    this.attribs[i] = new SerialClob((Clob)this.attribs[i]);
                }
                else if (this.attribs[i] instanceof Ref) {
                    this.attribs[i] = new SerialRef((Ref)this.attribs[i]);
                }
                else if (this.attribs[i] instanceof Array) {
                    this.attribs[i] = new SerialArray((Array)this.attribs[i], map);
                }
            }
        }
        catch (final SQLException ex) {
            throw new SerialException(ex.getMessage());
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SerialStruct) {
            final SerialStruct serialStruct = (SerialStruct)o;
            return this.SQLTypeName.equals(serialStruct.SQLTypeName) && Arrays.equals(this.attribs, serialStruct.attribs);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (31 + Arrays.hashCode(this.attribs)) * 31 * 31 + this.SQLTypeName.hashCode();
    }
    
    public Object clone() {
        try {
            final SerialStruct serialStruct = (SerialStruct)super.clone();
            serialStruct.attribs = Arrays.copyOf(this.attribs, this.attribs.length);
            return serialStruct;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final Object[] array = (Object[])fields.get("attribs", null);
        this.attribs = (Object[])((array == null) ? null : ((Object[])array.clone()));
        this.SQLTypeName = (String)fields.get("SQLTypeName", null);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("attribs", this.attribs);
        putFields.put("SQLTypeName", this.SQLTypeName);
        objectOutputStream.writeFields();
    }
}
