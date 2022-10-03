package javax.sql;

import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Ref;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.util.Map;
import java.sql.SQLException;
import java.sql.ResultSet;

public interface RowSet extends ResultSet
{
    void addRowSetListener(final RowSetListener p0);
    
    void clearParameters() throws SQLException;
    
    void execute() throws SQLException;
    
    String getCommand();
    
    String getDataSourceName();
    
    boolean getEscapeProcessing() throws SQLException;
    
    int getMaxFieldSize() throws SQLException;
    
    int getMaxRows() throws SQLException;
    
    String getPassword();
    
    int getQueryTimeout() throws SQLException;
    
    int getTransactionIsolation();
    
    Map getTypeMap() throws SQLException;
    
    String getUrl() throws SQLException;
    
    String getUsername();
    
    boolean isReadOnly();
    
    void removeRowSetListener(final RowSetListener p0);
    
    void setArray(final int p0, final Array p1) throws SQLException;
    
    void setAsciiStream(final int p0, final InputStream p1, final int p2) throws SQLException;
    
    void setBigDecimal(final int p0, final BigDecimal p1) throws SQLException;
    
    void setBinaryStream(final int p0, final InputStream p1, final int p2) throws SQLException;
    
    void setBlob(final int p0, final Blob p1) throws SQLException;
    
    void setBoolean(final int p0, final boolean p1) throws SQLException;
    
    void setByte(final int p0, final byte p1) throws SQLException;
    
    void setBytes(final int p0, final byte[] p1) throws SQLException;
    
    void setCharacterStream(final int p0, final Reader p1, final int p2) throws SQLException;
    
    void setClob(final int p0, final Clob p1) throws SQLException;
    
    void setCommand(final String p0) throws SQLException;
    
    void setConcurrency(final int p0) throws SQLException;
    
    void setDataSourceName(final String p0) throws SQLException;
    
    void setDate(final int p0, final Date p1) throws SQLException;
    
    void setDate(final int p0, final Date p1, final Calendar p2) throws SQLException;
    
    void setDouble(final int p0, final double p1) throws SQLException;
    
    void setEscapeProcessing(final boolean p0) throws SQLException;
    
    void setFloat(final int p0, final float p1) throws SQLException;
    
    void setInt(final int p0, final int p1) throws SQLException;
    
    void setLong(final int p0, final long p1) throws SQLException;
    
    void setMaxFieldSize(final int p0) throws SQLException;
    
    void setMaxRows(final int p0) throws SQLException;
    
    void setNull(final int p0, final int p1) throws SQLException;
    
    void setNull(final int p0, final int p1, final String p2) throws SQLException;
    
    void setObject(final int p0, final Object p1) throws SQLException;
    
    void setObject(final int p0, final Object p1, final int p2) throws SQLException;
    
    void setObject(final int p0, final Object p1, final int p2, final int p3) throws SQLException;
    
    void setPassword(final String p0) throws SQLException;
    
    void setQueryTimeout(final int p0) throws SQLException;
    
    void setReadOnly(final boolean p0) throws SQLException;
    
    void setRef(final int p0, final Ref p1) throws SQLException;
    
    void setShort(final int p0, final short p1) throws SQLException;
    
    void setString(final int p0, final String p1) throws SQLException;
    
    void setTime(final int p0, final Time p1) throws SQLException;
    
    void setTime(final int p0, final Time p1, final Calendar p2) throws SQLException;
    
    void setTimestamp(final int p0, final Timestamp p1) throws SQLException;
    
    void setTimestamp(final int p0, final Timestamp p1, final Calendar p2) throws SQLException;
    
    void setTransactionIsolation(final int p0) throws SQLException;
    
    void setType(final int p0) throws SQLException;
    
    void setTypeMap(final Map p0) throws SQLException;
    
    void setUrl(final String p0) throws SQLException;
    
    void setUsername(final String p0) throws SQLException;
}
