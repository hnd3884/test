package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.exception.ConvertException;

public interface Datatype
{
    void toOracleString() throws ConvertException;
    
    void toMSSQLServerString() throws ConvertException;
    
    void toSybaseString() throws ConvertException;
    
    void toDB2String() throws ConvertException;
    
    void toPostgreSQLString() throws ConvertException;
    
    void toMySQLString() throws ConvertException;
    
    void toANSIString() throws ConvertException;
    
    void toInformixString() throws ConvertException;
    
    void toTimesTenString() throws ConvertException;
    
    void toNetezzaString() throws ConvertException;
    
    void toTeradataString() throws ConvertException;
    
    String getDatatypeName();
    
    void setDatatypeName(final String p0);
    
    void setOpenBrace(final String p0);
    
    void setClosedBrace(final String p0);
    
    void setSize(final String p0);
    
    String getOpenBrace();
    
    String getClosedBrace();
    
    String getSize();
    
    void setArray(final String p0);
    
    String getArray();
}
