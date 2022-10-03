package javax.sql.rowset.serial;

import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.NClob;
import java.net.URL;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import sun.reflect.misc.ReflectUtil;
import java.sql.SQLData;
import java.sql.Struct;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.util.Arrays;
import java.sql.SQLException;
import java.util.Map;
import java.sql.SQLInput;

public class SQLInputImpl implements SQLInput
{
    private boolean lastValueWasNull;
    private int idx;
    private Object[] attrib;
    private Map<String, Class<?>> map;
    
    public SQLInputImpl(final Object[] array, final Map<String, Class<?>> map) throws SQLException {
        if (array == null || map == null) {
            throw new SQLException("Cannot instantiate a SQLInputImpl object with null parameters");
        }
        this.attrib = Arrays.copyOf(array, array.length);
        this.idx = -1;
        this.map = map;
    }
    
    private Object getNextAttribute() throws SQLException {
        if (++this.idx >= this.attrib.length) {
            throw new SQLException("SQLInputImpl exception: Invalid read position");
        }
        this.lastValueWasNull = (this.attrib[this.idx] == null);
        return this.attrib[this.idx];
    }
    
    @Override
    public String readString() throws SQLException {
        return (String)this.getNextAttribute();
    }
    
    @Override
    public boolean readBoolean() throws SQLException {
        final Boolean b = (Boolean)this.getNextAttribute();
        return b != null && b;
    }
    
    @Override
    public byte readByte() throws SQLException {
        final Byte b = (Byte)this.getNextAttribute();
        return (byte)((b == null) ? 0 : ((byte)b));
    }
    
    @Override
    public short readShort() throws SQLException {
        final Short n = (Short)this.getNextAttribute();
        return (short)((n == null) ? 0 : ((short)n));
    }
    
    @Override
    public int readInt() throws SQLException {
        final Integer n = (Integer)this.getNextAttribute();
        return (n == null) ? 0 : n;
    }
    
    @Override
    public long readLong() throws SQLException {
        final Long n = (Long)this.getNextAttribute();
        return (n == null) ? 0L : n;
    }
    
    @Override
    public float readFloat() throws SQLException {
        final Float n = (Float)this.getNextAttribute();
        return (n == null) ? 0.0f : n;
    }
    
    @Override
    public double readDouble() throws SQLException {
        final Double n = (Double)this.getNextAttribute();
        return (n == null) ? 0.0 : n;
    }
    
    @Override
    public BigDecimal readBigDecimal() throws SQLException {
        return (BigDecimal)this.getNextAttribute();
    }
    
    @Override
    public byte[] readBytes() throws SQLException {
        return (byte[])this.getNextAttribute();
    }
    
    @Override
    public Date readDate() throws SQLException {
        return (Date)this.getNextAttribute();
    }
    
    @Override
    public Time readTime() throws SQLException {
        return (Time)this.getNextAttribute();
    }
    
    @Override
    public Timestamp readTimestamp() throws SQLException {
        return (Timestamp)this.getNextAttribute();
    }
    
    @Override
    public Reader readCharacterStream() throws SQLException {
        return (Reader)this.getNextAttribute();
    }
    
    @Override
    public InputStream readAsciiStream() throws SQLException {
        return (InputStream)this.getNextAttribute();
    }
    
    @Override
    public InputStream readBinaryStream() throws SQLException {
        return (InputStream)this.getNextAttribute();
    }
    
    @Override
    public Object readObject() throws SQLException {
        final Object nextAttribute = this.getNextAttribute();
        if (nextAttribute instanceof Struct) {
            final Struct struct = (Struct)nextAttribute;
            final Class clazz = this.map.get(struct.getSQLTypeName());
            if (clazz != null) {
                SQLData sqlData;
                try {
                    sqlData = (SQLData)ReflectUtil.newInstance(clazz);
                }
                catch (final Exception ex) {
                    throw new SQLException("Unable to Instantiate: ", ex);
                }
                sqlData.readSQL(new SQLInputImpl(struct.getAttributes(this.map), this.map), struct.getSQLTypeName());
                return sqlData;
            }
        }
        return nextAttribute;
    }
    
    @Override
    public Ref readRef() throws SQLException {
        return (Ref)this.getNextAttribute();
    }
    
    @Override
    public Blob readBlob() throws SQLException {
        return (Blob)this.getNextAttribute();
    }
    
    @Override
    public Clob readClob() throws SQLException {
        return (Clob)this.getNextAttribute();
    }
    
    @Override
    public Array readArray() throws SQLException {
        return (Array)this.getNextAttribute();
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        return this.lastValueWasNull;
    }
    
    @Override
    public URL readURL() throws SQLException {
        return (URL)this.getNextAttribute();
    }
    
    @Override
    public NClob readNClob() throws SQLException {
        return (NClob)this.getNextAttribute();
    }
    
    @Override
    public String readNString() throws SQLException {
        return (String)this.getNextAttribute();
    }
    
    @Override
    public SQLXML readSQLXML() throws SQLException {
        return (SQLXML)this.getNextAttribute();
    }
    
    @Override
    public RowId readRowId() throws SQLException {
        return (RowId)this.getNextAttribute();
    }
}
