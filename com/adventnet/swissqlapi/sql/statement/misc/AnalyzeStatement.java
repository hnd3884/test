package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class AnalyzeStatement implements SwisSQLStatement
{
    private String analyzeString;
    private String tableString;
    private String tableName;
    private String analyzeOption;
    private String sampleNumber;
    private String rowsOrPercent;
    
    public AnalyzeStatement() {
        this.analyzeString = null;
        this.tableString = null;
        this.tableName = null;
        this.analyzeOption = null;
        this.sampleNumber = null;
        this.rowsOrPercent = null;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return null;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return null;
    }
    
    public void setAnalyzeString(final String str) {
        this.analyzeString = str;
    }
    
    public void setTableString(final String str) {
        this.tableString = str;
    }
    
    public void setTableName(final String str) {
        this.tableName = str;
    }
    
    public void setAnalyzeOption(final String str) {
        this.analyzeOption = str;
    }
    
    public void setSampleNumber(final String str) {
        this.sampleNumber = str;
    }
    
    public void setRowsOrPercent(final String str) {
        this.rowsOrPercent = str;
    }
    
    @Override
    public String removeIndent(final String formattedSqlString) {
        return formattedSqlString;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        if (this.tableString != null && this.tableString.equalsIgnoreCase("TABLE")) {
            if (this.tableName != null && this.analyzeOption != null && this.analyzeOption.equalsIgnoreCase("ESTIMATE") && this.rowsOrPercent != null && this.sampleNumber != null && this.rowsOrPercent.equalsIgnoreCase("ROWS")) {
                return "UPDATE STATISTICS " + this.tableName + " WITH SAMPLE " + this.sampleNumber + " " + this.rowsOrPercent;
            }
            if (this.tableName != null && this.analyzeOption != null && this.analyzeOption.equalsIgnoreCase("COMPUTE")) {
                return "UPDATE STATISTICS " + this.tableName;
            }
        }
        return "Query not supported";
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return "Query not supported";
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
