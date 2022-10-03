package com.adventnet.swissqlapi.sql.statement;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;

public interface SwisSQLStatement
{
    String toString();
    
    String toOracleString() throws ConvertException;
    
    String toMSSQLServerString() throws ConvertException;
    
    String toSybaseString() throws ConvertException;
    
    String toDB2String() throws ConvertException;
    
    String toPostgreSQLString() throws ConvertException;
    
    String toMySQLString() throws ConvertException;
    
    String toANSIString() throws ConvertException;
    
    String toInformixString() throws ConvertException;
    
    String toTimesTenString() throws ConvertException;
    
    String toNetezzaString() throws ConvertException;
    
    String toTeradataString() throws ConvertException;
    
    void setCommentClass(final CommentClass p0);
    
    CommentClass getCommentClass();
    
    UserObjectContext getObjectContext();
    
    void setObjectContext(final UserObjectContext p0);
    
    String removeIndent(final String p0);
    
    String toVectorWiseString() throws ConvertException;
}
