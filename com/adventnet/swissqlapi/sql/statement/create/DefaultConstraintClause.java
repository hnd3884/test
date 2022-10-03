package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class DefaultConstraintClause implements ConstraintType
{
    private String constraintName;
    private String defaultValue;
    private String openBrace;
    private String closedBrace;
    private String columnName;
    private String forClause;
    private FunctionCalls defaultFunction;
    private UserObjectContext context;
    
    public DefaultConstraintClause() {
        this.context = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setConstraintName(final String constraintName) {
        this.constraintName = constraintName;
    }
    
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setClosedBrace(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void setDefaultFunction(final FunctionCalls functionCalls) {
        this.defaultFunction = functionCalls;
    }
    
    public void setForClause(final String forClause) {
        this.forClause = forClause;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public FunctionCalls getDefaultFunction() {
        return this.defaultFunction;
    }
    
    public String getForClause() {
        return this.forClause;
    }
    
    @Override
    public void toDB2String() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
                this.setDefaultValue("CURRENT DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
                this.setDefaultValue("CURRENT TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                this.setDefaultValue("CURRENT TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toDB2Select(null, null));
            }
        }
    }
    
    @Override
    public void toMSSQLServerString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
                this.setDefaultValue("NEWID()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE") || this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME") || this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("SYSTEM_USER");
            }
            else if (this.defaultValue.toUpperCase().startsWith("EMPTY_BLOB") || this.defaultValue.toUpperCase().startsWith("EMPTY_CLOB")) {
                this.setDefaultValue("''");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toMSSQLServerSelect(null, null));
            }
        }
    }
    
    @Override
    public void toSybaseString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
                this.setDefaultValue("NEWID()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE") || this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME") || this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
            else if (this.defaultValue.indexOf("\"") != -1) {
                this.setDefaultValue(this.defaultValue = "'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toSybaseSelect(null, null));
            }
        }
    }
    
    @Override
    public void toOracleString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("CURRENT_DATE") || this.defaultValue.equalsIgnoreCase("CURRENT_TIME") || this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT DATE") || this.defaultValue.equalsIgnoreCase("CURRENT TIME") || this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("SYSDATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toOracleSelect(null, null));
            }
        }
    }
    
    @Override
    public void toPostgreSQLString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("USER")) {
                this.setDefaultValue("CURRENT_USER");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toPostgreSQLSelect(null, null));
            }
        }
    }
    
    @Override
    public void toANSIString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("USER")) {
                this.setDefaultValue("CURRENT_USER");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toANSISelect(null, null));
            }
        }
    }
    
    @Override
    public void toMySQLString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER()");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toMySQLSelect(null, null));
            }
        }
    }
    
    @Override
    public void toInformixString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toInformixSelect(null, null));
            }
        }
    }
    
    @Override
    public void toTimesTenString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
                this.setDefaultValue("NEWID()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE") || this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
                this.setDefaultValue("SYSDATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME") || this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
                this.setDefaultValue("SYSDATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                this.setDefaultValue("SYSDATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("SYSDATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
            else if (this.defaultValue.indexOf("\"") != -1) {
                this.setDefaultValue(this.defaultValue = "'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'");
            }
            else if (this.defaultFunction != null) {}
        }
    }
    
    @Override
    public void toNetezzaString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("SYSDATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("USER")) {
                this.setDefaultValue("CURRENT_USER");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toNetezzaSelect(null, null));
            }
        }
    }
    
    @Override
    public void toTeradataString() throws ConvertException {
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("USER")) {
                this.setDefaultValue("CURRENT_USER");
            }
            else if (this.defaultFunction != null) {
                this.setDefaultFunction(this.defaultFunction.toTeradataSelect(null, null));
            }
        }
    }
    
    public ConstraintType copyObjectValues() {
        final DefaultConstraintClause dupDefaultConstraintClause = new DefaultConstraintClause();
        dupDefaultConstraintClause.setConstraintName(this.getConstraintName());
        dupDefaultConstraintClause.setDefaultValue(this.getDefaultValue());
        dupDefaultConstraintClause.setDefaultFunction(this.getDefaultFunction());
        dupDefaultConstraintClause.setForClause(this.getForClause());
        dupDefaultConstraintClause.setObjectContext(this.context);
        return dupDefaultConstraintClause;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.constraintName != null) {
            if (this.context != null) {
                final String temp = this.context.getEquivalent(this.constraintName).toString();
                sb.append(temp + " ");
            }
            else {
                sb.append(this.constraintName + " ");
            }
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.defaultValue != null) {
            sb.append(this.defaultValue + " ");
        }
        if (this.defaultFunction != null) {
            this.defaultFunction.setObjectContext(this.context);
            sb.append(this.defaultFunction.toString() + " ");
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace + " ");
        }
        if (this.forClause != null) {
            sb.append(this.forClause + " ");
            if (this.columnName != null) {
                if (this.context != null) {
                    final String temp = this.context.getEquivalent(this.columnName).toString();
                    sb.append(temp + " ");
                }
                else {
                    sb.append(this.columnName + " ");
                }
            }
        }
        return sb.toString();
    }
}
