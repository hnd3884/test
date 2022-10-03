package javax.sql.rowset.serial;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.sql.ResultSet;
import java.net.URL;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Struct;
import java.sql.SQLException;
import java.util.Map;
import java.io.Serializable;
import java.sql.Array;

public class SerialArray implements Array, Serializable, Cloneable
{
    private Object[] elements;
    private int baseType;
    private String baseTypeName;
    private int len;
    static final long serialVersionUID = -8466174297270688520L;
    
    public SerialArray(final Array array, final Map<String, Class<?>> map) throws SerialException, SQLException {
        if (array == null || map == null) {
            throw new SQLException("Cannot instantiate a SerialArray object with null parameters");
        }
        if ((this.elements = (Object[])array.getArray()) == null) {
            throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized");
        }
        this.elements = (Object[])array.getArray(map);
        this.baseType = array.getBaseType();
        this.baseTypeName = array.getBaseTypeName();
        this.len = this.elements.length;
        switch (this.baseType) {
            case 2002: {
                for (int i = 0; i < this.len; ++i) {
                    this.elements[i] = new SerialStruct((Struct)this.elements[i], map);
                }
                break;
            }
            case 2003: {
                for (int j = 0; j < this.len; ++j) {
                    this.elements[j] = new SerialArray((Array)this.elements[j], map);
                }
                break;
            }
            case 2004: {
                for (int k = 0; k < this.len; ++k) {
                    this.elements[k] = new SerialBlob((Blob)this.elements[k]);
                }
                break;
            }
            case 2005: {
                for (int l = 0; l < this.len; ++l) {
                    this.elements[l] = new SerialClob((Clob)this.elements[l]);
                }
                break;
            }
            case 70: {
                for (int n = 0; n < this.len; ++n) {
                    this.elements[n] = new SerialDatalink((URL)this.elements[n]);
                }
                break;
            }
            case 2000: {
                for (int n2 = 0; n2 < this.len; ++n2) {
                    this.elements[n2] = new SerialJavaObject(this.elements[n2]);
                }
                break;
            }
        }
    }
    
    @Override
    public void free() throws SQLException {
        if (this.elements != null) {
            this.elements = null;
            this.baseTypeName = null;
        }
    }
    
    public SerialArray(final Array array) throws SerialException, SQLException {
        if (array == null) {
            throw new SQLException("Cannot instantiate a SerialArray object with a null Array object");
        }
        if ((this.elements = (Object[])array.getArray()) == null) {
            throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized");
        }
        this.baseType = array.getBaseType();
        this.baseTypeName = array.getBaseTypeName();
        this.len = this.elements.length;
        switch (this.baseType) {
            case 2004: {
                for (int i = 0; i < this.len; ++i) {
                    this.elements[i] = new SerialBlob((Blob)this.elements[i]);
                }
                break;
            }
            case 2005: {
                for (int j = 0; j < this.len; ++j) {
                    this.elements[j] = new SerialClob((Clob)this.elements[j]);
                }
                break;
            }
            case 70: {
                for (int k = 0; k < this.len; ++k) {
                    this.elements[k] = new SerialDatalink((URL)this.elements[k]);
                }
                break;
            }
            case 2000: {
                for (int l = 0; l < this.len; ++l) {
                    this.elements[l] = new SerialJavaObject(this.elements[l]);
                }
                break;
            }
        }
    }
    
    @Override
    public Object getArray() throws SerialException {
        this.isValid();
        final Object[] array = new Object[this.len];
        System.arraycopy(this.elements, 0, array, 0, this.len);
        return array;
    }
    
    @Override
    public Object getArray(final Map<String, Class<?>> map) throws SerialException {
        this.isValid();
        final Object[] array = new Object[this.len];
        System.arraycopy(this.elements, 0, array, 0, this.len);
        return array;
    }
    
    @Override
    public Object getArray(final long n, final int n2) throws SerialException {
        this.isValid();
        final Object[] array = new Object[n2];
        System.arraycopy(this.elements, (int)n, array, 0, n2);
        return array;
    }
    
    @Override
    public Object getArray(final long n, final int n2, final Map<String, Class<?>> map) throws SerialException {
        this.isValid();
        final Object[] array = new Object[n2];
        System.arraycopy(this.elements, (int)n, array, 0, n2);
        return array;
    }
    
    @Override
    public int getBaseType() throws SerialException {
        this.isValid();
        return this.baseType;
    }
    
    @Override
    public String getBaseTypeName() throws SerialException {
        this.isValid();
        return this.baseTypeName;
    }
    
    @Override
    public ResultSet getResultSet(final long n, final int n2) throws SerialException {
        final SerialException ex = new SerialException();
        ex.initCause(new UnsupportedOperationException());
        throw ex;
    }
    
    @Override
    public ResultSet getResultSet(final Map<String, Class<?>> map) throws SerialException {
        final SerialException ex = new SerialException();
        ex.initCause(new UnsupportedOperationException());
        throw ex;
    }
    
    @Override
    public ResultSet getResultSet() throws SerialException {
        final SerialException ex = new SerialException();
        ex.initCause(new UnsupportedOperationException());
        throw ex;
    }
    
    @Override
    public ResultSet getResultSet(final long n, final int n2, final Map<String, Class<?>> map) throws SerialException {
        final SerialException ex = new SerialException();
        ex.initCause(new UnsupportedOperationException());
        throw ex;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SerialArray) {
            final SerialArray serialArray = (SerialArray)o;
            return this.baseType == serialArray.baseType && this.baseTypeName.equals(serialArray.baseTypeName) && Arrays.equals(this.elements, serialArray.elements);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (((31 + Arrays.hashCode(this.elements)) * 31 + this.len) * 31 + this.baseType) * 31 + this.baseTypeName.hashCode();
    }
    
    public Object clone() {
        try {
            final SerialArray serialArray = (SerialArray)super.clone();
            serialArray.elements = (Object[])((this.elements != null) ? Arrays.copyOf(this.elements, this.len) : null);
            return serialArray;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final Object[] array = (Object[])fields.get("elements", null);
        if (array == null) {
            throw new InvalidObjectException("elements is null and should not be!");
        }
        this.elements = array.clone();
        this.len = fields.get("len", 0);
        if (this.elements.length != this.len) {
            throw new InvalidObjectException("elements is not the expected size");
        }
        this.baseType = fields.get("baseType", 0);
        this.baseTypeName = (String)fields.get("baseTypeName", null);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("elements", this.elements);
        putFields.put("len", this.len);
        putFields.put("baseType", this.baseType);
        putFields.put("baseTypeName", this.baseTypeName);
        objectOutputStream.writeFields();
    }
    
    private void isValid() throws SerialException {
        if (this.elements == null) {
            throw new SerialException("Error: You cannot call a method on a SerialArray instance once free() has been called.");
        }
    }
}
