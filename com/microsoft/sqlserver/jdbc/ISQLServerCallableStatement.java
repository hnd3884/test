package com.microsoft.sqlserver.jdbc;

import java.sql.SQLType;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Time;
import java.io.InputStream;
import microsoft.sql.DateTimeOffset;
import java.util.Calendar;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.sql.CallableStatement;

public interface ISQLServerCallableStatement extends CallableStatement, ISQLServerPreparedStatement
{
    @Deprecated
    BigDecimal getBigDecimal(final String p0, final int p1) throws SQLServerException;
    
    Timestamp getDateTime(final int p0) throws SQLServerException;
    
    Timestamp getDateTime(final String p0) throws SQLServerException;
    
    Timestamp getDateTime(final int p0, final Calendar p1) throws SQLServerException;
    
    Timestamp getDateTime(final String p0, final Calendar p1) throws SQLServerException;
    
    Timestamp getSmallDateTime(final int p0) throws SQLServerException;
    
    Timestamp getSmallDateTime(final String p0) throws SQLServerException;
    
    Timestamp getSmallDateTime(final int p0, final Calendar p1) throws SQLServerException;
    
    Timestamp getSmallDateTime(final String p0, final Calendar p1) throws SQLServerException;
    
    DateTimeOffset getDateTimeOffset(final int p0) throws SQLServerException;
    
    DateTimeOffset getDateTimeOffset(final String p0) throws SQLServerException;
    
    InputStream getAsciiStream(final int p0) throws SQLServerException;
    
    InputStream getAsciiStream(final String p0) throws SQLServerException;
    
    BigDecimal getMoney(final int p0) throws SQLServerException;
    
    BigDecimal getMoney(final String p0) throws SQLServerException;
    
    BigDecimal getSmallMoney(final int p0) throws SQLServerException;
    
    BigDecimal getSmallMoney(final String p0) throws SQLServerException;
    
    InputStream getBinaryStream(final int p0) throws SQLServerException;
    
    InputStream getBinaryStream(final String p0) throws SQLServerException;
    
    void setTimestamp(final String p0, final Timestamp p1, final Calendar p2, final boolean p3) throws SQLServerException;
    
    void setTime(final String p0, final Time p1, final Calendar p2, final boolean p3) throws SQLServerException;
    
    void setDate(final String p0, final Date p1, final Calendar p2, final boolean p3) throws SQLServerException;
    
    void setNString(final String p0, final String p1, final boolean p2) throws SQLServerException;
    
    void setObject(final String p0, final Object p1, final int p2, final int p3, final boolean p4) throws SQLServerException;
    
    void setObject(final String p0, final Object p1, final int p2, final Integer p3, final int p4) throws SQLServerException;
    
    void setTimestamp(final String p0, final Timestamp p1, final int p2) throws SQLServerException;
    
    void setTimestamp(final String p0, final Timestamp p1, final int p2, final boolean p3) throws SQLServerException;
    
    void setDateTimeOffset(final String p0, final DateTimeOffset p1) throws SQLServerException;
    
    void setDateTimeOffset(final String p0, final DateTimeOffset p1, final int p2) throws SQLServerException;
    
    void setDateTimeOffset(final String p0, final DateTimeOffset p1, final int p2, final boolean p3) throws SQLServerException;
    
    void setTime(final String p0, final Time p1, final int p2) throws SQLServerException;
    
    void setTime(final String p0, final Time p1, final int p2, final boolean p3) throws SQLServerException;
    
    void setDateTime(final String p0, final Timestamp p1) throws SQLServerException;
    
    void setDateTime(final String p0, final Timestamp p1, final boolean p2) throws SQLServerException;
    
    void setSmallDateTime(final String p0, final Timestamp p1) throws SQLServerException;
    
    void setSmallDateTime(final String p0, final Timestamp p1, final boolean p2) throws SQLServerException;
    
    void setUniqueIdentifier(final String p0, final String p1) throws SQLServerException;
    
    void setUniqueIdentifier(final String p0, final String p1, final boolean p2) throws SQLServerException;
    
    void setBytes(final String p0, final byte[] p1, final boolean p2) throws SQLServerException;
    
    void setByte(final String p0, final byte p1, final boolean p2) throws SQLServerException;
    
    void setString(final String p0, final String p1, final boolean p2) throws SQLServerException;
    
    void setMoney(final String p0, final BigDecimal p1) throws SQLServerException;
    
    void setMoney(final String p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void setSmallMoney(final String p0, final BigDecimal p1) throws SQLServerException;
    
    void setSmallMoney(final String p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void setBigDecimal(final String p0, final BigDecimal p1, final int p2, final int p3) throws SQLServerException;
    
    void setBigDecimal(final String p0, final BigDecimal p1, final int p2, final int p3, final boolean p4) throws SQLServerException;
    
    void setDouble(final String p0, final double p1, final boolean p2) throws SQLServerException;
    
    void setFloat(final String p0, final float p1, final boolean p2) throws SQLServerException;
    
    void setInt(final String p0, final int p1, final boolean p2) throws SQLServerException;
    
    void setLong(final String p0, final long p1, final boolean p2) throws SQLServerException;
    
    void setShort(final String p0, final short p1, final boolean p2) throws SQLServerException;
    
    void setBoolean(final String p0, final boolean p1, final boolean p2) throws SQLServerException;
    
    void setStructured(final String p0, final String p1, final SQLServerDataTable p2) throws SQLServerException;
    
    void setStructured(final String p0, final String p1, final ResultSet p2) throws SQLServerException;
    
    void setStructured(final String p0, final String p1, final ISQLServerDataRecord p2) throws SQLServerException;
    
    void registerOutParameter(final String p0, final SQLType p1, final int p2, final int p3) throws SQLServerException;
    
    void registerOutParameter(final int p0, final SQLType p1, final int p2, final int p3) throws SQLServerException;
    
    void registerOutParameter(final int p0, final int p1, final int p2, final int p3) throws SQLServerException;
    
    void registerOutParameter(final String p0, final int p1, final int p2, final int p3) throws SQLServerException;
    
    void setObject(final String p0, final Object p1, final SQLType p2, final int p3, final boolean p4) throws SQLServerException;
}
