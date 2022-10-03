package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.exception.ConvertException;

public interface ConstraintType
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
}
