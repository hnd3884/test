package com.microsoft.sqlserver.jdbc;

import java.util.Calendar;

abstract class DTVImpl
{
    abstract void setValue(final DTV p0, final SQLCollation p1, final JDBCType p2, final Object p3, final JavaType p4, final StreamSetterArgs p5, final Calendar p6, final Integer p7, final SQLServerConnection p8, final boolean p9) throws SQLServerException;
    
    abstract void setValue(final Object p0, final JavaType p1);
    
    abstract void setStreamSetterArgs(final StreamSetterArgs p0);
    
    abstract void setCalendar(final Calendar p0);
    
    abstract void setScale(final Integer p0);
    
    abstract void setForceEncrypt(final boolean p0);
    
    abstract StreamSetterArgs getStreamSetterArgs();
    
    abstract Calendar getCalendar();
    
    abstract Integer getScale();
    
    abstract boolean isNull();
    
    abstract void setJdbcType(final JDBCType p0);
    
    abstract JDBCType getJdbcType();
    
    abstract JavaType getJavaType();
    
    abstract Object getValue(final DTV p0, final JDBCType p1, final int p2, final InputStreamGetterArgs p3, final Calendar p4, final TypeInfo p5, final CryptoMetadata p6, final TDSReader p7) throws SQLServerException;
    
    abstract Object getSetterValue();
    
    abstract void skipValue(final TypeInfo p0, final TDSReader p1, final boolean p2) throws SQLServerException;
    
    abstract void initFromCompressedNull();
    
    abstract SqlVariant getInternalVariant();
}
