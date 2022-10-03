package com.adventnet.swissqlapi.sql.statement.delete;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectNames;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class TruncateStatement implements SwisSQLStatement
{
    private String truncateClause;
    private String tableString;
    private TableObject truncatedTableObject;
    private ModifiedObjectNames modifiedObjects;
    
    public TruncateStatement() {
        this.truncateClause = null;
        this.tableString = null;
        this.truncatedTableObject = null;
        this.modifiedObjects = null;
    }
    
    public void TruncateStatement() {
    }
    
    public void setTruncateClause(final String truncateClause) {
        this.truncateClause = truncateClause;
    }
    
    public void setTableString(final String tableString) {
        this.tableString = tableString;
    }
    
    public void setTableObject(final TableObject tableObject) {
        this.truncatedTableObject = tableObject;
    }
    
    public String getTruncateClause() {
        return this.truncateClause;
    }
    
    public String getTableString() {
        return this.tableString;
    }
    
    public TableObject getTableObject() {
        return this.truncatedTableObject;
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleTruncate().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerTruncate().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseTruncate().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Truncate().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLTruncate().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQLTruncate().toString();
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSITruncate().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataTruncate().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixTruncate().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenTruncate().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaTruncate().toString();
    }
    
    public SwisSQLStatement toOracleTruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toOracle();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    public SwisSQLStatement toMSSQLServerTruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toMSSQLServer();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    public SwisSQLStatement toSybaseTruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toSybase();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    public SwisSQLStatement toDB2Truncate() throws ConvertException {
        final DeleteQueryStatement deleteStmt = new DeleteQueryStatement();
        final DeleteClause deleteClause = new DeleteClause();
        deleteClause.setDelete("DELETE");
        deleteStmt.setDeleteClause(deleteClause);
        final FromClause fromClause = new FromClause();
        fromClause.setFromClause("FROM");
        final Vector fromTableList = new Vector();
        final FromTable fromTable = new FromTable();
        this.getTableObject().toDB2();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        fromTable.setTableName(this.getTableObject());
        fromTableList.add(fromTable);
        fromClause.setFromItemList(fromTableList);
        deleteStmt.setFromClause(fromClause);
        return deleteStmt;
    }
    
    public SwisSQLStatement toPostgreSQLTruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toPostgreSQL();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    public SwisSQLStatement toMySQLTruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toMySQL();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    public SwisSQLStatement toANSITruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toANSISQL();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    public SwisSQLStatement toTeradataTruncate() throws ConvertException {
        final DeleteQueryStatement deleteStmt = new DeleteQueryStatement();
        final DeleteClause deleteClause = new DeleteClause();
        deleteClause.setDelete("DELETE");
        deleteStmt.setDeleteClause(deleteClause);
        final FromClause fromClause = new FromClause();
        fromClause.setFromClause("FROM");
        final Vector fromTableList = new Vector();
        final FromTable fromTable = new FromTable();
        this.getTableObject().toTeradata();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        fromTable.setTableName(this.getTableObject());
        fromTableList.add(fromTable);
        fromClause.setFromItemList(fromTableList);
        deleteStmt.setFromClause(fromClause);
        final DeleteLimitClause deleteLimitClause = new DeleteLimitClause();
        deleteLimitClause.setLimit("ALL");
        deleteLimitClause.setDimension("");
        deleteStmt.setDeleteLimitClause(deleteLimitClause);
        return deleteStmt;
    }
    
    public SwisSQLStatement toInformixTruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toInformix();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    public SwisSQLStatement toTimesTenTruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toTimesTen();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    public SwisSQLStatement toNetezzaTruncate() throws ConvertException {
        final TruncateStatement truncStmt = new TruncateStatement();
        truncStmt.setTruncateClause("TRUNCATE");
        truncStmt.setTableString("TABLE");
        this.getTableObject().toNetezza();
        this.truncatedTableObject = this.handleTableObject(this.getTableObject());
        truncStmt.setTableObject(this.getTableObject());
        return truncStmt;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
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
    public void setObjectContext(final UserObjectContext obj) {
    }
    
    @Override
    public String removeIndent(String formattedSqlString) {
        formattedSqlString = formattedSqlString.replace('\n', ' ');
        formattedSqlString = formattedSqlString.replace('\t', ' ');
        return formattedSqlString;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.truncateClause != null) {
            sb.append(this.truncateClause + " ");
        }
        if (this.tableString != null) {
            sb.append(this.tableString + " ");
        }
        if (this.truncatedTableObject != null) {
            sb.append(this.truncatedTableObject.toString());
        }
        return sb.toString();
    }
    
    public TableObject handleTableObject(final TableObject tableObj) throws ConvertException {
        if (tableObj != null) {
            final TableObject orgTableObject = tableObj;
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            String tableName = orgTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            orgTableObject.setTableName(tableName);
            return orgTableObject;
        }
        return tableObj;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
