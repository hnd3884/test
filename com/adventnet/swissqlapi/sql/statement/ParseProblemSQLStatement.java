package com.adventnet.swissqlapi.sql.statement;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class ParseProblemSQLStatement implements SwisSQLStatement
{
    private String sqlStatement;
    private CommentClass commentObject;
    private String plsqlStatement;
    private UserObjectContext objectContext;
    
    public ParseProblemSQLStatement() {
        this.sqlStatement = null;
        this.plsqlStatement = null;
        this.objectContext = null;
    }
    
    @Override
    public void setCommentClass(CommentClass commentObject) {
        commentObject = this.commentObject;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    public void setSQLStatement(final String sql) {
        this.sqlStatement = sql;
    }
    
    public String getSQLStatement() {
        return this.sqlStatement;
    }
    
    public void setPLSQLStr(final String plsqlStatement) {
        this.plsqlStatement = plsqlStatement;
    }
    
    public String getPLSQLStr() {
        return this.plsqlStatement;
    }
    
    @Override
    public String toString() {
        return this.sqlStatement;
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String removeIndent(final String s_ri) {
        return this.toString();
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.objectContext;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.objectContext = obj;
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toString() + "\n /* SwisSQL MESSAGE : Above statement could not be parsed by SwisSQL API Parser */";
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
