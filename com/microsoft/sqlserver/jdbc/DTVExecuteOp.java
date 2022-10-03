package com.microsoft.sqlserver.jdbc;

import java.io.Reader;
import java.io.InputStream;
import java.sql.Blob;
import java.math.BigInteger;
import java.math.BigDecimal;
import microsoft.sql.DateTimeOffset;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.sql.Clob;

abstract class DTVExecuteOp
{
    abstract void execute(final DTV p0, final String p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Clob p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Byte p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Integer p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Time p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Date p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Timestamp p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final java.util.Date p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Calendar p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final LocalDate p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final LocalTime p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final LocalDateTime p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final OffsetTime p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final OffsetDateTime p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final DateTimeOffset p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Float p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Double p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final BigDecimal p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Long p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final BigInteger p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Short p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Boolean p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final byte[] p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Blob p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final InputStream p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final Reader p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final SQLServerSQLXML p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final TVP p1) throws SQLServerException;
    
    abstract void execute(final DTV p0, final SqlVariant p1) throws SQLServerException;
}
