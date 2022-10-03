package javax.sql.rowset.serial;

import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.NClob;
import java.net.URL;
import java.sql.Array;
import java.sql.Struct;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.sql.SQLData;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;
import java.sql.SQLOutput;

public class SQLOutputImpl implements SQLOutput
{
    private Vector attribs;
    private Map map;
    
    public SQLOutputImpl(final Vector<?> attribs, final Map<String, ?> map) throws SQLException {
        if (attribs == null || map == null) {
            throw new SQLException("Cannot instantiate a SQLOutputImpl instance with null parameters");
        }
        this.attribs = attribs;
        this.map = map;
    }
    
    @Override
    public void writeString(final String s) throws SQLException {
        this.attribs.add(s);
    }
    
    @Override
    public void writeBoolean(final boolean b) throws SQLException {
        this.attribs.add(b);
    }
    
    @Override
    public void writeByte(final byte b) throws SQLException {
        this.attribs.add(b);
    }
    
    @Override
    public void writeShort(final short n) throws SQLException {
        this.attribs.add(n);
    }
    
    @Override
    public void writeInt(final int n) throws SQLException {
        this.attribs.add(n);
    }
    
    @Override
    public void writeLong(final long n) throws SQLException {
        this.attribs.add(n);
    }
    
    @Override
    public void writeFloat(final float n) throws SQLException {
        this.attribs.add(n);
    }
    
    @Override
    public void writeDouble(final double n) throws SQLException {
        this.attribs.add(n);
    }
    
    @Override
    public void writeBigDecimal(final BigDecimal bigDecimal) throws SQLException {
        this.attribs.add(bigDecimal);
    }
    
    @Override
    public void writeBytes(final byte[] array) throws SQLException {
        this.attribs.add(array);
    }
    
    @Override
    public void writeDate(final Date date) throws SQLException {
        this.attribs.add(date);
    }
    
    @Override
    public void writeTime(final Time time) throws SQLException {
        this.attribs.add(time);
    }
    
    @Override
    public void writeTimestamp(final Timestamp timestamp) throws SQLException {
        this.attribs.add(timestamp);
    }
    
    @Override
    public void writeCharacterStream(final Reader reader) throws SQLException {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            int read;
            while ((read = bufferedReader.read()) != -1) {
                final char c = (char)read;
                final StringBuffer sb = new StringBuffer();
                sb.append(c);
                this.writeString(new String(sb).concat(bufferedReader.readLine()));
            }
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public void writeAsciiStream(final InputStream inputStream) throws SQLException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            int read;
            while ((read = bufferedReader.read()) != -1) {
                final char c = (char)read;
                final StringBuffer sb = new StringBuffer();
                sb.append(c);
                this.writeString(new String(sb).concat(bufferedReader.readLine()));
            }
        }
        catch (final IOException ex) {
            throw new SQLException(ex.getMessage());
        }
    }
    
    @Override
    public void writeBinaryStream(final InputStream inputStream) throws SQLException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            int read;
            while ((read = bufferedReader.read()) != -1) {
                final char c = (char)read;
                final StringBuffer sb = new StringBuffer();
                sb.append(c);
                this.writeString(new String(sb).concat(bufferedReader.readLine()));
            }
        }
        catch (final IOException ex) {
            throw new SQLException(ex.getMessage());
        }
    }
    
    @Override
    public void writeObject(final SQLData sqlData) throws SQLException {
        if (sqlData == null) {
            this.attribs.add(null);
        }
        else {
            this.attribs.add(new SerialStruct(sqlData, this.map));
        }
    }
    
    @Override
    public void writeRef(final Ref ref) throws SQLException {
        if (ref == null) {
            this.attribs.add(null);
        }
        else {
            this.attribs.add(new SerialRef(ref));
        }
    }
    
    @Override
    public void writeBlob(final Blob blob) throws SQLException {
        if (blob == null) {
            this.attribs.add(null);
        }
        else {
            this.attribs.add(new SerialBlob(blob));
        }
    }
    
    @Override
    public void writeClob(final Clob clob) throws SQLException {
        if (clob == null) {
            this.attribs.add(null);
        }
        else {
            this.attribs.add(new SerialClob(clob));
        }
    }
    
    @Override
    public void writeStruct(final Struct struct) throws SQLException {
        this.attribs.add(new SerialStruct(struct, this.map));
    }
    
    @Override
    public void writeArray(final Array array) throws SQLException {
        if (array == null) {
            this.attribs.add(null);
        }
        else {
            this.attribs.add(new SerialArray(array, this.map));
        }
    }
    
    @Override
    public void writeURL(final URL url) throws SQLException {
        if (url == null) {
            this.attribs.add(null);
        }
        else {
            this.attribs.add(new SerialDatalink(url));
        }
    }
    
    @Override
    public void writeNString(final String s) throws SQLException {
        this.attribs.add(s);
    }
    
    @Override
    public void writeNClob(final NClob nClob) throws SQLException {
        this.attribs.add(nClob);
    }
    
    @Override
    public void writeRowId(final RowId rowId) throws SQLException {
        this.attribs.add(rowId);
    }
    
    @Override
    public void writeSQLXML(final SQLXML sqlxml) throws SQLException {
        this.attribs.add(sqlxml);
    }
}
