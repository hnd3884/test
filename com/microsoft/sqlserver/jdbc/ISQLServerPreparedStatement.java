package com.microsoft.sqlserver.jdbc;

import java.sql.ParameterMetaData;
import java.util.Calendar;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Time;
import java.math.BigDecimal;
import java.sql.SQLType;
import microsoft.sql.DateTimeOffset;
import java.sql.PreparedStatement;

public interface ISQLServerPreparedStatement extends PreparedStatement, ISQLServerStatement
{
    void setDateTimeOffset(final int p0, final DateTimeOffset p1) throws SQLServerException;
    
    void setObject(final int p0, final Object p1, final SQLType p2, final Integer p3, final Integer p4) throws SQLServerException;
    
    void setObject(final int p0, final Object p1, final SQLType p2, final Integer p3, final Integer p4, final boolean p5) throws SQLServerException;
    
    int getPreparedStatementHandle() throws SQLServerException;
    
    void setBigDecimal(final int p0, final BigDecimal p1, final int p2, final int p3) throws SQLServerException;
    
    void setBigDecimal(final int p0, final BigDecimal p1, final int p2, final int p3, final boolean p4) throws SQLServerException;
    
    void setMoney(final int p0, final BigDecimal p1) throws SQLServerException;
    
    void setMoney(final int p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void setSmallMoney(final int p0, final BigDecimal p1) throws SQLServerException;
    
    void setSmallMoney(final int p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void setBoolean(final int p0, final boolean p1, final boolean p2) throws SQLServerException;
    
    void setByte(final int p0, final byte p1, final boolean p2) throws SQLServerException;
    
    void setBytes(final int p0, final byte[] p1, final boolean p2) throws SQLServerException;
    
    void setUniqueIdentifier(final int p0, final String p1) throws SQLServerException;
    
    void setUniqueIdentifier(final int p0, final String p1, final boolean p2) throws SQLServerException;
    
    void setDouble(final int p0, final double p1, final boolean p2) throws SQLServerException;
    
    void setFloat(final int p0, final float p1, final boolean p2) throws SQLServerException;
    
    void setGeometry(final int p0, final Geometry p1) throws SQLServerException;
    
    void setGeography(final int p0, final Geography p1) throws SQLServerException;
    
    void setInt(final int p0, final int p1, final boolean p2) throws SQLServerException;
    
    void setLong(final int p0, final long p1, final boolean p2) throws SQLServerException;
    
    void setObject(final int p0, final Object p1, final int p2, final Integer p3, final int p4) throws SQLServerException;
    
    void setObject(final int p0, final Object p1, final int p2, final Integer p3, final int p4, final boolean p5) throws SQLServerException;
    
    void setShort(final int p0, final short p1, final boolean p2) throws SQLServerException;
    
    void setString(final int p0, final String p1, final boolean p2) throws SQLServerException;
    
    void setNString(final int p0, final String p1, final boolean p2) throws SQLServerException;
    
    void setTime(final int p0, final Time p1, final int p2) throws SQLServerException;
    
    void setTime(final int p0, final Time p1, final int p2, final boolean p3) throws SQLServerException;
    
    void setTimestamp(final int p0, final Timestamp p1, final int p2) throws SQLServerException;
    
    void setTimestamp(final int p0, final Timestamp p1, final int p2, final boolean p3) throws SQLServerException;
    
    void setDateTimeOffset(final int p0, final DateTimeOffset p1, final int p2) throws SQLServerException;
    
    void setDateTimeOffset(final int p0, final DateTimeOffset p1, final int p2, final boolean p3) throws SQLServerException;
    
    void setDateTime(final int p0, final Timestamp p1) throws SQLServerException;
    
    void setDateTime(final int p0, final Timestamp p1, final boolean p2) throws SQLServerException;
    
    void setSmallDateTime(final int p0, final Timestamp p1) throws SQLServerException;
    
    void setSmallDateTime(final int p0, final Timestamp p1, final boolean p2) throws SQLServerException;
    
    void setStructured(final int p0, final String p1, final SQLServerDataTable p2) throws SQLServerException;
    
    void setStructured(final int p0, final String p1, final ResultSet p2) throws SQLServerException;
    
    void setStructured(final int p0, final String p1, final ISQLServerDataRecord p2) throws SQLServerException;
    
    void setDate(final int p0, final Date p1, final Calendar p2, final boolean p3) throws SQLServerException;
    
    void setTime(final int p0, final Time p1, final Calendar p2, final boolean p3) throws SQLServerException;
    
    void setTimestamp(final int p0, final Timestamp p1, final Calendar p2, final boolean p3) throws SQLServerException;
    
    ParameterMetaData getParameterMetaData(final boolean p0) throws SQLServerException;
    
    boolean getUseFmtOnly() throws SQLServerException;
    
    void setUseFmtOnly(final boolean p0) throws SQLServerException;
}
