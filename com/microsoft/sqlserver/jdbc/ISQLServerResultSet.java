package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityClassification;
import java.sql.Time;
import java.sql.Date;
import java.sql.SQLType;
import java.math.BigDecimal;
import microsoft.sql.DateTimeOffset;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.ResultSet;

public interface ISQLServerResultSet extends ResultSet
{
    public static final int TYPE_SS_DIRECT_FORWARD_ONLY = 2003;
    public static final int TYPE_SS_SERVER_CURSOR_FORWARD_ONLY = 2004;
    public static final int TYPE_SS_SCROLL_STATIC = 1004;
    public static final int TYPE_SS_SCROLL_KEYSET = 1005;
    public static final int TYPE_SS_SCROLL_DYNAMIC = 1006;
    public static final int CONCUR_SS_OPTIMISTIC_CC = 1008;
    public static final int CONCUR_SS_SCROLL_LOCKS = 1009;
    public static final int CONCUR_SS_OPTIMISTIC_CCVAL = 1010;
    
    Geometry getGeometry(final int p0) throws SQLServerException;
    
    Geometry getGeometry(final String p0) throws SQLServerException;
    
    Geography getGeography(final int p0) throws SQLServerException;
    
    Geography getGeography(final String p0) throws SQLServerException;
    
    String getUniqueIdentifier(final int p0) throws SQLServerException;
    
    String getUniqueIdentifier(final String p0) throws SQLServerException;
    
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
    
    BigDecimal getMoney(final int p0) throws SQLServerException;
    
    BigDecimal getMoney(final String p0) throws SQLServerException;
    
    BigDecimal getSmallMoney(final int p0) throws SQLServerException;
    
    BigDecimal getSmallMoney(final String p0) throws SQLServerException;
    
    void updateDateTimeOffset(final int p0, final DateTimeOffset p1) throws SQLServerException;
    
    void updateDateTimeOffset(final String p0, final DateTimeOffset p1) throws SQLServerException;
    
    void updateObject(final int p0, final Object p1, final int p2, final int p3) throws SQLServerException;
    
    void updateObject(final int p0, final Object p1, final SQLType p2, final int p3, final boolean p4) throws SQLServerException;
    
    void updateObject(final String p0, final Object p1, final SQLType p2, final int p3, final boolean p4) throws SQLServerException;
    
    void updateBoolean(final int p0, final boolean p1, final boolean p2) throws SQLServerException;
    
    void updateByte(final int p0, final byte p1, final boolean p2) throws SQLServerException;
    
    void updateShort(final int p0, final short p1, final boolean p2) throws SQLServerException;
    
    void updateInt(final int p0, final int p1, final boolean p2) throws SQLServerException;
    
    void updateLong(final int p0, final long p1, final boolean p2) throws SQLServerException;
    
    void updateFloat(final int p0, final float p1, final boolean p2) throws SQLServerException;
    
    void updateDouble(final int p0, final double p1, final boolean p2) throws SQLServerException;
    
    void updateMoney(final int p0, final BigDecimal p1) throws SQLServerException;
    
    void updateMoney(final int p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void updateMoney(final String p0, final BigDecimal p1) throws SQLServerException;
    
    void updateMoney(final String p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void updateSmallMoney(final int p0, final BigDecimal p1) throws SQLServerException;
    
    void updateSmallMoney(final int p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void updateSmallMoney(final String p0, final BigDecimal p1) throws SQLServerException;
    
    void updateSmallMoney(final String p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void updateBigDecimal(final int p0, final BigDecimal p1, final Integer p2, final Integer p3) throws SQLServerException;
    
    void updateBigDecimal(final int p0, final BigDecimal p1, final Integer p2, final Integer p3, final boolean p4) throws SQLServerException;
    
    void updateString(final int p0, final String p1, final boolean p2) throws SQLServerException;
    
    void updateNString(final int p0, final String p1, final boolean p2) throws SQLServerException;
    
    void updateNString(final String p0, final String p1, final boolean p2) throws SQLServerException;
    
    void updateBytes(final int p0, final byte[] p1, final boolean p2) throws SQLServerException;
    
    void updateDate(final int p0, final Date p1, final boolean p2) throws SQLServerException;
    
    void updateTime(final int p0, final Time p1, final Integer p2) throws SQLServerException;
    
    void updateTime(final int p0, final Time p1, final Integer p2, final boolean p3) throws SQLServerException;
    
    void updateTimestamp(final int p0, final Timestamp p1, final int p2) throws SQLServerException;
    
    void updateTimestamp(final int p0, final Timestamp p1, final int p2, final boolean p3) throws SQLServerException;
    
    void updateDateTime(final int p0, final Timestamp p1) throws SQLServerException;
    
    void updateDateTime(final int p0, final Timestamp p1, final Integer p2) throws SQLServerException;
    
    void updateDateTime(final int p0, final Timestamp p1, final Integer p2, final boolean p3) throws SQLServerException;
    
    void updateSmallDateTime(final int p0, final Timestamp p1) throws SQLServerException;
    
    void updateSmallDateTime(final int p0, final Timestamp p1, final Integer p2) throws SQLServerException;
    
    void updateSmallDateTime(final int p0, final Timestamp p1, final Integer p2, final boolean p3) throws SQLServerException;
    
    void updateDateTimeOffset(final int p0, final DateTimeOffset p1, final Integer p2) throws SQLServerException;
    
    void updateDateTimeOffset(final int p0, final DateTimeOffset p1, final Integer p2, final boolean p3) throws SQLServerException;
    
    void updateUniqueIdentifier(final int p0, final String p1) throws SQLServerException;
    
    void updateUniqueIdentifier(final int p0, final String p1, final boolean p2) throws SQLServerException;
    
    void updateObject(final int p0, final Object p1, final int p2, final int p3, final boolean p4) throws SQLServerException;
    
    void updateBoolean(final String p0, final boolean p1, final boolean p2) throws SQLServerException;
    
    void updateByte(final String p0, final byte p1, final boolean p2) throws SQLServerException;
    
    void updateShort(final String p0, final short p1, final boolean p2) throws SQLServerException;
    
    void updateInt(final String p0, final int p1, final boolean p2) throws SQLServerException;
    
    void updateLong(final String p0, final long p1, final boolean p2) throws SQLServerException;
    
    void updateFloat(final String p0, final float p1, final boolean p2) throws SQLServerException;
    
    void updateDouble(final String p0, final double p1, final boolean p2) throws SQLServerException;
    
    void updateBigDecimal(final String p0, final BigDecimal p1, final boolean p2) throws SQLServerException;
    
    void updateBigDecimal(final String p0, final BigDecimal p1, final Integer p2, final Integer p3) throws SQLServerException;
    
    void updateBigDecimal(final String p0, final BigDecimal p1, final Integer p2, final Integer p3, final boolean p4) throws SQLServerException;
    
    void updateString(final String p0, final String p1, final boolean p2) throws SQLServerException;
    
    void updateBytes(final String p0, final byte[] p1, final boolean p2) throws SQLServerException;
    
    void updateDate(final String p0, final Date p1, final boolean p2) throws SQLServerException;
    
    void updateTime(final String p0, final Time p1, final int p2) throws SQLServerException;
    
    void updateTime(final String p0, final Time p1, final int p2, final boolean p3) throws SQLServerException;
    
    void updateTimestamp(final String p0, final Timestamp p1, final int p2) throws SQLServerException;
    
    void updateTimestamp(final String p0, final Timestamp p1, final int p2, final boolean p3) throws SQLServerException;
    
    void updateDateTime(final String p0, final Timestamp p1) throws SQLServerException;
    
    void updateDateTime(final String p0, final Timestamp p1, final int p2) throws SQLServerException;
    
    void updateDateTime(final String p0, final Timestamp p1, final int p2, final boolean p3) throws SQLServerException;
    
    void updateSmallDateTime(final String p0, final Timestamp p1) throws SQLServerException;
    
    void updateSmallDateTime(final String p0, final Timestamp p1, final int p2) throws SQLServerException;
    
    void updateSmallDateTime(final String p0, final Timestamp p1, final int p2, final boolean p3) throws SQLServerException;
    
    void updateDateTimeOffset(final String p0, final DateTimeOffset p1, final int p2) throws SQLServerException;
    
    void updateDateTimeOffset(final String p0, final DateTimeOffset p1, final int p2, final boolean p3) throws SQLServerException;
    
    void updateUniqueIdentifier(final String p0, final String p1) throws SQLServerException;
    
    void updateUniqueIdentifier(final String p0, final String p1, final boolean p2) throws SQLServerException;
    
    void updateObject(final String p0, final Object p1, final int p2, final int p3) throws SQLServerException;
    
    void updateObject(final String p0, final Object p1, final int p2, final int p3, final boolean p4) throws SQLServerException;
    
    SensitivityClassification getSensitivityClassification();
}
