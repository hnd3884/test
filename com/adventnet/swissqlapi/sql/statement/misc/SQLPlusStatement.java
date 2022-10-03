package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class SQLPlusStatement implements SwisSQLStatement
{
    private String setString;
    private String autoCommitString;
    private String autoCommitState;
    private String equals;
    private String defineString;
    private String notSupportedComment;
    
    public SQLPlusStatement() {
        this.notSupportedComment = null;
    }
    
    public void setSetString(final String setString) {
        this.setString = setString;
    }
    
    public void setAutoCommitString(final String autoCommitString) {
        this.autoCommitString = autoCommitString;
    }
    
    public void setAutoCommitState(final String autoCommitState) {
        this.autoCommitState = autoCommitState;
    }
    
    public void setEquals(final String equals) {
        this.equals = equals;
    }
    
    public void setDefineString(final String defineString) {
        this.defineString = defineString;
    }
    
    public String getSetString() {
        return this.setString;
    }
    
    public String getAutoCommitString() {
        return this.autoCommitString;
    }
    
    public String setAutoCommitState() {
        return this.autoCommitState;
    }
    
    public String getEquals() {
        return this.equals;
    }
    
    public String getDefineString() {
        return this.defineString;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return null;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return null;
    }
    
    @Override
    public String removeIndent(final String str) {
        return str;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentClass) {
    }
    
    @Override
    public void setObjectContext(final UserObjectContext userObjectContext) {
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSISQLPlus().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataSQLPlus().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2SQLPlus().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixSQLPlus().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerSQLPlus().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQLSQLPlus().toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleSQLPlus().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLPlus().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseSQLPlus().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenSQLPlus().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaSQLPlus().toString();
    }
    
    public SQLPlusStatement toANSISQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in ANSI SQL");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(null);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                sqlPlusStatement.setAutoCommitState("ON");
            }
            else if (this.autoCommitState.equals("0")) {
                sqlPlusStatement.setAutoCommitState("OFF");
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toTeradataSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Teradata SQL");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(null);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                sqlPlusStatement.setAutoCommitState("ON");
            }
            else if (this.autoCommitState.equals("0")) {
                sqlPlusStatement.setAutoCommitState("OFF");
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toDB2SQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString("DB2SET");
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString("DB2OPTIONS");
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in DB2");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals("=");
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                sqlPlusStatement.setAutoCommitState("-C");
            }
            else if (this.autoCommitState.equals("0")) {
                sqlPlusStatement.setAutoCommitState("+C");
            }
            else if ((this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) && this.defineString != null) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toInformixSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Informix");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(null);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                sqlPlusStatement.setAutoCommitState("ON");
            }
            else if (this.autoCommitState.equals("0")) {
                sqlPlusStatement.setAutoCommitState("OFF");
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toMSSQLServerSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString("IMPLICIT_TRANSACTIONS");
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in SQL Server");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(null);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                sqlPlusStatement.setAutoCommitState("OFF");
            }
            else if (this.autoCommitState.equals("0")) {
                sqlPlusStatement.setAutoCommitState("ON");
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toMySQLSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in MySQL");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(this.equals);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                sqlPlusStatement.setAutoCommitState("1");
            }
            else if (this.autoCommitState.equals("0")) {
                sqlPlusStatement.setAutoCommitState("0");
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toOracleSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            if (this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
                sqlPlusStatement.setAutoCommitString("CONSTRAINTS");
            }
            else {
                sqlPlusStatement.setAutoCommitString(this.autoCommitString);
            }
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(null);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
                    sqlPlusStatement.setAutoCommitState("IMMEDIATE");
                }
                else {
                    sqlPlusStatement.setAutoCommitState("ON");
                }
            }
            else if (this.autoCommitState.equals("0")) {
                if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
                    sqlPlusStatement.setAutoCommitState("DEFERRED");
                }
                else {
                    sqlPlusStatement.setAutoCommitState("OFF");
                }
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toPostgreSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            if (this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
                sqlPlusStatement.setAutoCommitString("CONSTRAINTS ALL");
            }
            else {
                sqlPlusStatement.setAutoCommitString(this.autoCommitString);
            }
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in PostgreSQL");
        }
        if (this.equals != null && !this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
            sqlPlusStatement.setEquals("TO");
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
                    sqlPlusStatement.setAutoCommitState("IMMEDIATE");
                }
                else {
                    sqlPlusStatement.setAutoCommitState("ON");
                }
            }
            else if (this.autoCommitState.equals("0")) {
                if (this.autoCommitString != null && this.autoCommitString.equalsIgnoreCase("FOREIGN_KEY_CHECKS")) {
                    sqlPlusStatement.setAutoCommitState("DEFERRED");
                }
                else {
                    sqlPlusStatement.setAutoCommitState("OFF");
                }
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toSybaseSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString("CHAINED");
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Sybase");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(null);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                sqlPlusStatement.setAutoCommitState("OFF");
            }
            else if (this.autoCommitState.equals("0")) {
                sqlPlusStatement.setAutoCommitState("ON");
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toTimesTenSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(null);
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString("AUTOCOMMIT");
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in TimesTen");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(null);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equalsIgnoreCase("ON")) {
                sqlPlusStatement.setAutoCommitState("0");
            }
            else if (this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState("1");
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    public SQLPlusStatement toNetezzaSQLPlus() throws ConvertException {
        final SQLPlusStatement sqlPlusStatement = new SQLPlusStatement();
        if (this.setString != null) {
            sqlPlusStatement.setSetString(this.setString);
        }
        if (this.autoCommitString != null) {
            sqlPlusStatement.setAutoCommitString(this.autoCommitString);
        }
        if (this.defineString != null) {
            sqlPlusStatement.setDefineString(this.defineString);
            sqlPlusStatement.setNotSupportedComment("The following construct is not supported in Netezza SQL");
        }
        if (this.equals != null) {
            sqlPlusStatement.setEquals(null);
        }
        if (this.autoCommitState != null) {
            if (this.autoCommitState.equals("1")) {
                sqlPlusStatement.setAutoCommitState("ON");
            }
            else if (this.autoCommitState.equals("0")) {
                sqlPlusStatement.setAutoCommitState("OFF");
            }
            else if (this.autoCommitState.equalsIgnoreCase("ON") || this.autoCommitState.equalsIgnoreCase("OFF")) {
                sqlPlusStatement.setAutoCommitState(this.autoCommitState.toUpperCase());
            }
        }
        return sqlPlusStatement;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String indentString = "\n";
        if (this.setString != null) {
            sb.append(indentString + this.setString.toUpperCase());
        }
        if (this.autoCommitString != null) {
            sb.append(" " + this.autoCommitString.toUpperCase());
        }
        if (this.defineString != null) {
            sb.append(" " + this.defineString.toUpperCase());
        }
        if (this.equals != null) {
            sb.append(" " + this.equals);
        }
        if (this.autoCommitState != null) {
            sb.append(" " + this.autoCommitState.toUpperCase());
        }
        if (this.notSupportedComment != null) {
            sb.insert(0, "/*" + this.notSupportedComment + "\n");
            sb.append("\n*/");
        }
        return sb.toString();
    }
    
    public void setNotSupportedComment(final String notSupportedComment) {
        this.notSupportedComment = notSupportedComment;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
