package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.sql.statement.create.IndexColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.create.CreateIndexClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.create.ForeignConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.ConstraintClause;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import java.util.StringTokenizer;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.drop.DropStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class AlterStatement implements SwisSQLStatement
{
    private UserObjectContext objectContext;
    private String alter;
    private String ignore;
    private String tableOrView;
    private TableObject tableName;
    private AlterTable alterTable;
    private Vector alterStatementVector;
    private boolean commaIsSet;
    private CommentClass commentObject;
    private String onString;
    private String quotedIdentifierString;
    private String multiAlterStatement;
    private String alterSession;
    private String setString;
    private String parameter;
    private String parameterValue;
    public static String generalComment;
    private DatatypeMapping datatypeMapping;
    private CreateQueryStatement indexStatement;
    private DropStatement dropIndexStatement;
    private String sequence;
    private String deltek_triggers;
    
    public AlterStatement() {
        this.objectContext = null;
        this.indexStatement = null;
        this.dropIndexStatement = null;
    }
    
    public void setAlter(final String alter) {
        this.alter = alter;
    }
    
    public void setIgnore(final String ignore) {
        this.ignore = ignore;
    }
    
    public void setTableOrView(final String tableOrView) {
        this.tableOrView = tableOrView;
    }
    
    public void setSession(final String alterSession) {
        this.alterSession = alterSession;
    }
    
    public void setSetString(final String setString) {
        this.setString = setString;
    }
    
    public void setTableName(final TableObject tableName) {
        this.tableName = tableName;
    }
    
    public void setAlterTable(final AlterTable alterTable) {
        this.alterTable = alterTable;
    }
    
    public void setAlterStatementVector(final Vector alterStatementVector) {
        this.alterStatementVector = alterStatementVector;
    }
    
    public void setCommaBooleanValue(final boolean commaIsSet) {
        this.commaIsSet = commaIsSet;
    }
    
    public void setDropClause() {
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    public void setOnCondition(final String onString) {
        this.onString = onString;
    }
    
    public void setQuotedIdentifier(final String quotedIdentifierString) {
        this.quotedIdentifierString = quotedIdentifierString;
    }
    
    public void setMultiAlterStatement(final String multiAlterStatement) {
        this.multiAlterStatement = multiAlterStatement;
    }
    
    public void setParameter(final String parameter) {
        this.parameter = parameter;
    }
    
    public void setParameterValue(final String parameterValue) {
        this.parameterValue = parameterValue;
    }
    
    public void setDatatypeMapping(final DatatypeMapping mapping) {
        this.datatypeMapping = mapping;
    }
    
    public void setIndexStatement(final CreateQueryStatement cqs) {
        this.indexStatement = cqs;
    }
    
    public void setDropIndexStatement(final DropStatement dropStmt) {
        this.dropIndexStatement = dropStmt;
    }
    
    public void setSequence(final String seq) {
        this.sequence = seq;
    }
    
    public void setTriggers(final String s) {
        this.deltek_triggers = s;
    }
    
    public String getTriggers() {
        return this.deltek_triggers;
    }
    
    public String getAlter() {
        return this.alter;
    }
    
    public String getIgnore() {
        return this.ignore;
    }
    
    public String getTableOrView() {
        return this.tableOrView;
    }
    
    public TableObject getTableName() {
        return this.tableName;
    }
    
    public AlterTable getAlterTable() {
        return this.alterTable;
    }
    
    public Vector getAlterStatementVector() {
        return this.alterStatementVector;
    }
    
    public boolean getCommaBooleanValue() {
        return this.commaIsSet;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    private String getMultiAlterStatement() {
        return this.multiAlterStatement;
    }
    
    public String getSession() {
        return this.alterSession;
    }
    
    public String getSetString() {
        return this.setString;
    }
    
    public String getParameter() {
        return this.parameter;
    }
    
    public String getParameterValue() {
        return this.parameterValue;
    }
    
    public String getSequence() {
        return this.sequence;
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleAlter().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerAlter().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseAlter().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Alter().toString();
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSIAlter().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLAlter().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixAlter().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQLAlter().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenAlter().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaAlter().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataAlter().toString();
    }
    
    public AlterStatement toOracleAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        dupAlterStatement.setMultiAlterStatement(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && ((oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) || (oracleTableName.startsWith("`") && oracleTableName.endsWith("`")))) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (SwisSQLOptions.retainQuotedIdentifierForOracle || oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toOracle();
            dupAlterStatement.setTableName(orgTableObject);
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector oracleAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(i);
                final AlterTable oracleAlterTable = tempAlterTable.toOracle();
                if (tempAlterTable.getEnableOrDisable() != null) {
                    dupAlterStatement.setTableOrView("TRIGGER");
                    dupAlterStatement.setTableName(null);
                }
                oracleAlterStatementVector.add(oracleAlterTable);
            }
            dupAlterStatement.setAlterStatementVector(oracleAlterStatementVector);
        }
        dupAlterStatement.setCommaBooleanValue(false);
        return dupAlterStatement;
    }
    
    public String convertOnDeleteSetNullToTriggers() {
        ForeignConstraintClause foreigncontraint = null;
        String onDelete = null;
        String setNull = null;
        String referencingTable = null;
        final String origTableName = this.getTableName().getTableName();
        String constraintName = null;
        Vector constraintColumnNames = new Vector();
        String insertTrigger = null;
        String deleteTrigger = null;
        String updateTrigger1 = null;
        String updateTrigger2 = null;
        if (this.alterStatementVector != null && this.alterStatementVector.size() == 1) {
            final Object obj = this.alterStatementVector.get(0);
            if (obj != null && obj instanceof AlterTable) {
                final AlterTable altertable = this.alterStatementVector.get(0);
                if (altertable != null && altertable.getAddClause() != null) {
                    final AddClause ac = altertable.getAddClause();
                    if (ac != null && ac.getCreateColumnVector() != null && ac.getCreateColumnVector().size() == 1) {
                        final Vector constraintBody = ac.getCreateColumnVector();
                        if (constraintBody != null && constraintBody.size() == 1) {
                            final CreateColumn cc = constraintBody.get(0);
                            final Vector constraintsVector = cc.getConstraintClause();
                            if (constraintsVector != null && constraintsVector.size() == 1) {
                                final Object obj2 = constraintsVector.get(0);
                                if (obj2 instanceof ConstraintClause) {
                                    final ConstraintClause constraintClause = (ConstraintClause)obj2;
                                    constraintName = constraintClause.getConstraintName();
                                    if (constraintClause.getConstraintType() != null && constraintClause.getConstraintType() instanceof ForeignConstraintClause) {
                                        foreigncontraint = (ForeignConstraintClause)constraintClause.getConstraintType();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (foreigncontraint != null) {
            if (foreigncontraint.getOnDelete() != null && foreigncontraint.getActionOnDelete() != null) {
                onDelete = foreigncontraint.getOnDelete();
                if (foreigncontraint.getActionOnDelete().trim().equalsIgnoreCase("SET NULL")) {
                    setNull = foreigncontraint.getActionOnDelete().trim();
                }
            }
            if (foreigncontraint.getReference() != null) {
                referencingTable = foreigncontraint.getTableName().toString();
            }
            if (foreigncontraint.getConstraintColumnNames().size() >= 1) {
                constraintColumnNames = foreigncontraint.getConstraintColumnNames();
            }
        }
        String inserted1 = "";
        String inserted2 = "";
        String delete1 = "";
        String delete2 = "";
        String update1 = "";
        final String update2 = "";
        String update3 = "";
        final String equals = " = ";
        final String isnull = " is null ";
        if (constraintColumnNames.size() >= 1) {
            for (int i = 0; i < constraintColumnNames.size(); ++i) {
                final String temp = referencingTable + "." + constraintColumnNames.get(i);
                final String temp2 = "INSERTED." + constraintColumnNames.get(i);
                final String temp_del = origTableName + "." + constraintColumnNames.get(i);
                final String temp_del2 = "DELETED." + constraintColumnNames.get(i);
                final String temp_update1 = "update(" + constraintColumnNames.get(i) + ")";
                final String temp_update2 = temp_del + equals + temp_del2 + "\n AND " + temp_del2 + " != " + temp2;
                String t;
                String t2;
                String del1;
                String del2;
                String upd1;
                String upd2;
                if (i != constraintColumnNames.size() - 1) {
                    t = temp + equals + temp2 + " AND \n";
                    t2 = temp2 + isnull + " OR \n";
                    del1 = temp_del + equals + " null ,\n";
                    del2 = temp_del + equals + temp_del2 + " AND \n";
                    upd1 = temp_update1 + " OR ";
                    upd2 = temp_update2 + " AND \n";
                }
                else {
                    upd2 = temp_update2;
                    upd1 = temp_update1;
                    del2 = temp_del + equals + temp_del2;
                    del1 = temp_del + equals + " null \n";
                    t = temp + equals + temp2;
                    t2 = temp2 + isnull;
                }
                update3 += upd2;
                inserted1 += t;
                inserted2 += t2;
                delete1 += del1;
                delete2 += del2;
                update1 += upd1;
            }
        }
        if (this.tableOrView.trim().equalsIgnoreCase("TABLE") && foreigncontraint != null && onDelete != null && setNull != null && foreigncontraint.getConstraintName().trim().equalsIgnoreCase("FOREIGN KEY") && onDelete.trim().equalsIgnoreCase("ON DELETE") && setNull.trim().equalsIgnoreCase("SET NULL")) {
            final String insertTriggerName = origTableName + "_INSTRIG";
            insertTrigger = "/* INSERT TRIGGER FOR: " + origTableName + "*/ \n \n CREATE TRIGGER " + insertTriggerName + " \n" + "ON " + origTableName + " FOR INSERT \n AS \n declare @num_rows int \n" + "select @num_rows = @@rowcount \n If @num_rows = 0 \n" + "return \n\n" + "/* " + constraintName + " insert restrict */" + "\n IF (SELECT COUNT(*) FROM " + referencingTable + " , inserted \n WHERE \n " + inserted1 + ") !=" + " @num_rows \n BEGIN \n " + "RAISERROR 40002 'NOT IN " + referencingTable + " for child " + origTableName + "' \n" + "ROLLBACK TRAN RETURN \n \n END \n go";
            final String deleteTriggerName = referencingTable + "_DELTRIG";
            deleteTrigger = "/* DELETE TRIGGER FOR: " + referencingTable + " */ \n\n CREATE TRIGGER " + deleteTriggerName + " \n" + "ON " + referencingTable + " FOR DELETE \n AS \n declare @num_rows int \n" + "select @num_rows = @@rowcount \n \n If @num_rows = 0  \n return \n\n /* " + constraintName + " Delete Set Null */ \n" + "UPDATE " + origTableName + " SET \n" + delete1 + "\n" + "from " + origTableName + " , deleted where " + delete2 + "\n" + "\n go";
            final String updateTriggerName1 = origTableName + "_UPDTRIG";
            updateTrigger1 = " /* UPDATE TRIGGER for: " + origTableName + " */ \n\n CREATE TRIGGER " + updateTriggerName1 + " \n ON " + origTableName + " FOR UPDATE \n\n AS \n" + "declare @num_rows int \n select @num_rows = @@rowcount \n" + "If @num_rows = 0 \n \n  return \n if " + update1 + " \n\n" + "BEGIN \n\n" + "/* " + constraintName + " update change restrict */\n\n" + "IF (SELECT COUNT(*) FROM " + referencingTable + ", inserted where\n" + inserted1 + ") != @num_rows \n\n BEGIN \n RAISERROR 40005 'not in " + referencingTable + " when changing child " + origTableName + "' \n ROLLBACK TRAN RETURN" + "\n \n  END \n END \ngo";
            final String updateTriggerName2 = referencingTable + "_UPDTRIG";
            updateTrigger2 = "/* UPDATE TRIGGER for: " + referencingTable + " */ \n\n CREATE TRIGGER " + updateTriggerName2 + "\n ON " + referencingTable + " FOR UPDATE \n\n AS \n" + "declare @num_rows int \n select @num_rows = @@rowcount \n" + "If @num_rows = 0 \n \n  return \n if " + update1 + " \n\n" + "BEGIN \n\n" + "/* Update Primary Key restrict if no dependants found for" + referencingTable + " */\n\n" + "IF (SELECT COUNT(*) FROM " + origTableName + ",deleted,inserted where \n" + update3 + ") != 0 \n\n BEGIN \n RAISERROR 40001" + " 'Parent table " + referencingTable + " cannot change Primary Key with dependant rows in " + origTableName + "' \n ROLLBACK TRAN RETURN" + "\n \n  END \n END \ngo";
            final String triggers = insertTrigger + "\n\n" + deleteTrigger + "\n\n" + updateTrigger2 + "\n\n" + updateTrigger1;
            return triggers;
        }
        return null;
    }
    
    public AlterStatement toMSSQLServerAlter() throws ConvertException {
        if (SwisSQLOptions.sqlServerTriggers) {
            this.deltek_triggers = this.convertOnDeleteSetNullToTriggers();
        }
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("Query yet to be supported");
        }
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            dupAlterStatement.getTableName();
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector msSQLServerAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            if (tempAlterStatementVector.size() == 1) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(0);
                if (tempAlterTable.toString().equalsIgnoreCase("comment") || tempAlterTable.toString().equalsIgnoreCase("engine")) {
                    throw new ConvertException(tempAlterTable.toString().toUpperCase() + " table option not supported in SQL Server");
                }
                if (tempAlterTable.getAddClause() != null && tempAlterTable.getAddClause().getUniqueOrPrimaryOrIndexOrFullText() != null && tempAlterTable.getAddClause().getUniqueOrPrimaryOrIndexOrFullText().equalsIgnoreCase("index")) {
                    dupAlterStatement.setIndexStatement((CreateQueryStatement)this.handleAddIndexClause(dupAlterStatement, tempAlterTable));
                }
                else if (tempAlterTable.getDropClause() != null && tempAlterTable.getDropClause().getIndex() != null) {
                    dupAlterStatement.setDropIndexStatement((DropStatement)this.handleDropIndexClause(dupAlterStatement, tempAlterTable));
                }
                else {
                    final AlterTable msSQLServerAlterTable = tempAlterTable.toMSSQLServer();
                    if (msSQLServerAlterTable.getChange() != null && !msSQLServerAlterTable.getOrigColumn().trim().equalsIgnoreCase(msSQLServerAlterTable.getCreateColumn().getColumnName().trim())) {
                        final String space = " ";
                        final StringBuffer sp_rename = new StringBuffer();
                        sp_rename.append("exec sp_rename" + space);
                        final String tableName = this.convertTableObjectToString(dupAlterStatement.getTableName());
                        sp_rename.append("'" + tableName + "." + msSQLServerAlterTable.getOrigColumn() + "'");
                        sp_rename.append(space + "," + space);
                        sp_rename.append("'" + msSQLServerAlterTable.getCreateColumn().getColumnName() + "'");
                        sp_rename.append(space + "," + space);
                        sp_rename.append("'column'");
                        msSQLServerAlterTable.setsp_renameStmt(sp_rename.toString());
                    }
                    msSQLServerAlterStatementVector.add(msSQLServerAlterTable);
                    dupAlterStatement.setAlterStatementVector(msSQLServerAlterStatementVector);
                }
                dupAlterStatement.setMultiAlterStatement(null);
            }
            else {
                dupAlterStatement.setMultiAlterStatement("ALTER");
                for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                    final AlterTable tempAlterTable2 = tempAlterStatementVector.get(i);
                    if (tempAlterTable2.getAddClause() != null && tempAlterTable2.getAddClause().getUniqueOrPrimaryOrIndexOrFullText() != null && tempAlterTable2.getAddClause().getUniqueOrPrimaryOrIndexOrFullText().equalsIgnoreCase("index")) {
                        final SwisSQLStatement swisStmt = this.handleAddIndexClause(dupAlterStatement, tempAlterTable2);
                        msSQLServerAlterStatementVector.add(swisStmt);
                    }
                    else if (tempAlterTable2.getDropClause() != null && tempAlterTable2.getDropClause().getIndex() != null) {
                        final SwisSQLStatement swisStmt = this.handleDropIndexClause(dupAlterStatement, tempAlterTable2);
                        msSQLServerAlterStatementVector.add(swisStmt);
                    }
                    else {
                        final AlterTable msSQLServerAlterTable2 = tempAlterTable2.toMSSQLServer();
                        if (msSQLServerAlterTable2.getChange() != null && !msSQLServerAlterTable2.getOrigColumn().trim().equalsIgnoreCase(msSQLServerAlterTable2.getCreateColumn().getColumnName().trim())) {
                            final String space2 = " ";
                            final StringBuffer sp_rename2 = new StringBuffer();
                            sp_rename2.append("exec sp_rename" + space2);
                            final String tableName2 = this.convertTableObjectToString(dupAlterStatement.getTableName());
                            sp_rename2.append("'" + tableName2 + "." + msSQLServerAlterTable2.getOrigColumn() + "'");
                            sp_rename2.append(space2 + "," + space2);
                            sp_rename2.append("'" + msSQLServerAlterTable2.getCreateColumn().getColumnName() + "'");
                            sp_rename2.append(space2 + "," + space2);
                            sp_rename2.append("'column'");
                            msSQLServerAlterTable2.setsp_renameStmt(sp_rename2.toString());
                        }
                        msSQLServerAlterStatementVector.add(msSQLServerAlterTable2);
                    }
                    dupAlterStatement.setAlterStatementVector(msSQLServerAlterStatementVector);
                }
            }
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && oracleTableName.startsWith("`") && oracleTableName.endsWith("`")) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toMSSQLServer();
            dupAlterStatement.setTableName(orgTableObject);
        }
        return dupAlterStatement;
    }
    
    public AlterStatement toSybaseAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("Query yet to be supported");
        }
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
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
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && ((oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) || (oracleTableName.startsWith("`") && oracleTableName.endsWith("`")))) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toSybase();
            dupAlterStatement.setTableName(orgTableObject);
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector sybaseAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            if (tempAlterStatementVector.size() == 1) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(0);
                final AlterTable sybaseAlterTable = tempAlterTable.toSybase();
                sybaseAlterStatementVector.add(sybaseAlterTable);
                dupAlterStatement.setAlterStatementVector(sybaseAlterStatementVector);
                dupAlterStatement.setMultiAlterStatement(null);
            }
            else {
                dupAlterStatement.setMultiAlterStatement("ALTER");
                for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                    final AlterTable tempAlterTable2 = tempAlterStatementVector.get(i);
                    final AlterTable sybaseAlterTable2 = tempAlterTable2.toSybase();
                    sybaseAlterStatementVector.add(sybaseAlterTable2);
                    dupAlterStatement.setAlterStatementVector(sybaseAlterStatementVector);
                }
            }
        }
        return dupAlterStatement;
    }
    
    public AlterStatement toDB2Alter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("Query yet to be supported");
        }
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            dupAlterStatement.getTableName();
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector db2AlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(i);
                tempAlterTable.setDatatypeMapping(this.datatypeMapping);
                if (dupAlterStatement.getTableName() != null) {
                    tempAlterTable.setAlterTableName(dupAlterStatement.getTableName().getTableName());
                }
                final AlterTable db2AlterTable = tempAlterTable.toDB2();
                db2AlterStatementVector.add(db2AlterTable);
            }
            dupAlterStatement.setAlterStatementVector(db2AlterStatementVector);
        }
        dupAlterStatement.setCommaBooleanValue(false);
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
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
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && ((oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) || (oracleTableName.startsWith("`") && oracleTableName.endsWith("`")))) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toDB2();
            dupAlterStatement.setTableName(orgTableObject);
        }
        return dupAlterStatement;
    }
    
    public AlterStatement toPostgreSQLAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("Query yet to be supported");
        }
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            dupAlterStatement.getTableName();
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector postgreSQLAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            if (tempAlterStatementVector.size() == 1) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(0);
                final AlterTable postgreSQLAlterTable = tempAlterTable.toPostgreSQL();
                postgreSQLAlterStatementVector.add(postgreSQLAlterTable);
                dupAlterStatement.setAlterStatementVector(postgreSQLAlterStatementVector);
                dupAlterStatement.setMultiAlterStatement(null);
            }
            else {
                dupAlterStatement.setMultiAlterStatement("ALTER");
                for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                    final AlterTable tempAlterTable2 = tempAlterStatementVector.get(i);
                    final AlterTable postgreAlterTable = tempAlterTable2.toPostgreSQL();
                    postgreSQLAlterStatementVector.add(postgreAlterTable);
                    dupAlterStatement.setAlterStatementVector(postgreSQLAlterStatementVector);
                }
            }
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
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
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && ((oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) || (oracleTableName.startsWith("`") && oracleTableName.endsWith("`")))) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toPostgreSQL();
            dupAlterStatement.setTableName(orgTableObject);
        }
        return dupAlterStatement;
    }
    
    public AlterStatement toMySQLAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("Query yet to be supported");
        }
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        dupAlterStatement.setMultiAlterStatement(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        if (dupAlterStatement.getIgnore() != null) {
            dupAlterStatement.getIgnore();
        }
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && ownerName.startsWith("[") && ownerName.endsWith("]")) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && userName.startsWith("[") && userName.endsWith("]")) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toMySQL();
            dupAlterStatement.setTableName(orgTableObject);
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector mySQLAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(i);
                final AlterTable mySQLAlterTable = tempAlterTable.toMySQL();
                mySQLAlterStatementVector.add(mySQLAlterTable);
            }
            dupAlterStatement.setAlterStatementVector(mySQLAlterStatementVector);
        }
        dupAlterStatement.setCommaBooleanValue(true);
        return dupAlterStatement;
    }
    
    public AlterStatement toANSIAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("Query yet to be supported");
        }
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
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
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && ((oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) || (oracleTableName.startsWith("`") && oracleTableName.endsWith("`")))) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toANSISQL();
            dupAlterStatement.setTableName(orgTableObject);
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector ansiAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            if (tempAlterStatementVector.size() == 1) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(0);
                final AlterTable ansiAlterTable = tempAlterTable.toANSI();
                ansiAlterStatementVector.add(ansiAlterTable);
                dupAlterStatement.setAlterStatementVector(ansiAlterStatementVector);
                dupAlterStatement.setMultiAlterStatement(null);
            }
            else {
                dupAlterStatement.setMultiAlterStatement("ALTER");
                for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                    final AlterTable tempAlterTable2 = tempAlterStatementVector.get(i);
                    final AlterTable ansiAlterTable2 = tempAlterTable2.toANSI();
                    ansiAlterStatementVector.add(ansiAlterTable2);
                    dupAlterStatement.setAlterStatementVector(ansiAlterStatementVector);
                }
            }
        }
        return dupAlterStatement;
    }
    
    public AlterStatement toInformixAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("Query yet to be supported");
        }
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        dupAlterStatement.setMultiAlterStatement(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
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
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && ((oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) || (oracleTableName.startsWith("`") && oracleTableName.endsWith("`")))) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toInformix();
            dupAlterStatement.setTableName(orgTableObject);
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector informixAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(i);
                final AlterTable informixAlterTable = tempAlterTable.toInformix();
                informixAlterStatementVector.add(informixAlterTable);
            }
            dupAlterStatement.setAlterStatementVector(informixAlterStatementVector);
        }
        dupAlterStatement.setCommaBooleanValue(true);
        return dupAlterStatement;
    }
    
    public AlterStatement toTimesTenAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        AlterStatement.generalComment = "";
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        dupAlterStatement.setMultiAlterStatement(null);
        dupAlterStatement.setIgnore(null);
        if (this.alterSession != null) {
            throw new ConvertException("\n'ALTER SESSION'  is not supported in TimesTen 5.1.21\n\n");
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
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
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String timesTenTableName = tokenVector.get(i_count);
                if (timesTenTableName != null && ((timesTenTableName.startsWith("[") && timesTenTableName.endsWith("]")) || (timesTenTableName.startsWith("`") && timesTenTableName.endsWith("`")))) {
                    timesTenTableName = timesTenTableName.substring(1, timesTenTableName.length() - 1);
                    if (timesTenTableName.indexOf(32) != -1) {
                        timesTenTableName = "\"" + timesTenTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(timesTenTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toTimesTen();
            dupAlterStatement.setTableName(orgTableObject);
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector timesTenAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(i);
                final AlterTable timesTenAlterTable = tempAlterTable.toTimesTen();
                timesTenAlterStatementVector.add(timesTenAlterTable);
            }
            dupAlterStatement.setAlterStatementVector(timesTenAlterStatementVector);
        }
        dupAlterStatement.setCommaBooleanValue(false);
        return dupAlterStatement;
    }
    
    public AlterStatement toNetezzaAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("/*SwisSQL Message : Netezza does not support the ALTER SESSION clauses of Oracle*/");
        }
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            final String tempTableOrView = dupAlterStatement.getTableOrView();
            if (tempTableOrView.equalsIgnoreCase("view")) {
                throw new ConvertException("/*SwisSQL Message : Netezza does not support the ALTER VIEW clauses of Oracle*/");
            }
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
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
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && ((oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) || (oracleTableName.startsWith("`") && oracleTableName.endsWith("`")))) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toNetezza();
            dupAlterStatement.setTableName(orgTableObject);
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector netezzaAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            if (tempAlterStatementVector.size() == 1) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(0);
                final AlterTable netezzaAlterTable = tempAlterTable.toNetezza();
                netezzaAlterStatementVector.add(netezzaAlterTable);
                dupAlterStatement.setAlterStatementVector(netezzaAlterStatementVector);
                dupAlterStatement.setMultiAlterStatement(null);
            }
            else {
                dupAlterStatement.setMultiAlterStatement("ALTER");
                for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                    final AlterTable tempAlterTable2 = tempAlterStatementVector.get(i);
                    final AlterTable netezzaAlterTable2 = tempAlterTable2.toNetezza();
                    netezzaAlterStatementVector.add(netezzaAlterTable2);
                    dupAlterStatement.setAlterStatementVector(netezzaAlterStatementVector);
                }
            }
        }
        return dupAlterStatement;
    }
    
    public AlterStatement toTeradataAlter() throws ConvertException {
        final AlterStatement dupAlterStatement = this.copyObjectValues();
        if (this.alterSession != null) {
            throw new ConvertException("Query yet to be supported");
        }
        if (dupAlterStatement.getCommentClass() != null) {
            dupAlterStatement.getCommentClass().setSQLDialect(12);
        }
        dupAlterStatement.setOnCondition(null);
        dupAlterStatement.setQuotedIdentifier(null);
        if (dupAlterStatement.getAlter() != null) {
            dupAlterStatement.getAlter();
        }
        dupAlterStatement.setIgnore(null);
        if (dupAlterStatement.getTableOrView() != null) {
            dupAlterStatement.getTableOrView();
        }
        if (dupAlterStatement.getTableName() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = dupAlterStatement.getTableName();
            final String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
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
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && ((oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) || (oracleTableName.startsWith("`") && oracleTableName.endsWith("`")))) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toTeradata();
            dupAlterStatement.setTableName(orgTableObject);
        }
        if (dupAlterStatement.getAlterStatementVector() != null) {
            final Vector teradataAlterStatementVector = new Vector();
            final Vector tempAlterStatementVector = dupAlterStatement.getAlterStatementVector();
            if (tempAlterStatementVector.size() == 1) {
                final AlterTable tempAlterTable = tempAlterStatementVector.get(0);
                final AlterTable teradataAlterTable = tempAlterTable.toTeradata();
                teradataAlterStatementVector.add(teradataAlterTable);
                dupAlterStatement.setAlterStatementVector(teradataAlterStatementVector);
                dupAlterStatement.setMultiAlterStatement(null);
            }
            else {
                dupAlterStatement.setMultiAlterStatement("ALTER");
                for (int i = 0; i < tempAlterStatementVector.size(); ++i) {
                    final AlterTable tempAlterTable2 = tempAlterStatementVector.get(i);
                    final AlterTable teradataAlterTable2 = tempAlterTable2.toTeradata();
                    teradataAlterStatementVector.add(teradataAlterTable2);
                    dupAlterStatement.setAlterStatementVector(teradataAlterStatementVector);
                }
            }
        }
        return dupAlterStatement;
    }
    
    @Override
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    public AlterStatement copyObjectValues() {
        final AlterStatement dupAlterStatement = new AlterStatement();
        dupAlterStatement.setTriggers(this.getTriggers());
        dupAlterStatement.setAlter(this.getAlter());
        dupAlterStatement.setIgnore(this.getIgnore());
        dupAlterStatement.setTableOrView(this.getTableOrView());
        dupAlterStatement.setTableName(this.getTableName());
        dupAlterStatement.setSession(this.getSession());
        dupAlterStatement.setSetString(this.getSetString());
        dupAlterStatement.setParameter(this.getParameter());
        dupAlterStatement.setParameterValue(this.getParameterValue());
        dupAlterStatement.setSequence(this.getSequence());
        dupAlterStatement.setAlterStatementVector(this.getAlterStatementVector());
        dupAlterStatement.setCommaBooleanValue(this.getCommaBooleanValue());
        dupAlterStatement.setOnCondition(this.onString);
        dupAlterStatement.setQuotedIdentifier(this.quotedIdentifierString);
        dupAlterStatement.setCommentClass(this.commentObject);
        dupAlterStatement.setObjectContext(this.objectContext);
        return dupAlterStatement;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (!SwisSQLUtils.swissqlMessageList.isEmpty()) {
            sb.append("/* SwisSQL Messages :\n");
            for (int i = 0; i < SwisSQLUtils.swissqlMessageList.size(); ++i) {
                sb.append(SwisSQLUtils.swissqlMessageList.get(i).toString() + "\n");
            }
            sb.append("*/\n");
            SwisSQLUtils.swissqlMessageList.clear();
        }
        if (this.commentObject != null) {
            sb.append(this.commentObject.toString() + "\n");
        }
        if (this.indexStatement != null) {
            return this.indexStatement.toString();
        }
        if (this.dropIndexStatement != null) {
            return this.dropIndexStatement.toString();
        }
        if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
            sb.append(this.singleQueryIntoMultipleQueriesForPLSQL());
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
        }
        if (this.alter != null) {
            sb.append(this.alter.toUpperCase() + " ");
        }
        if (this.ignore != null) {
            sb.append(this.ignore.toUpperCase() + " ");
        }
        if (this.tableOrView != null) {
            sb.append(this.tableOrView.toUpperCase() + " ");
        }
        if (this.alterSession != null) {
            sb.append(" " + this.alterSession.toUpperCase());
        }
        if (this.setString != null) {
            sb.append(" " + this.setString.toUpperCase());
        }
        if (this.parameter != null) {
            sb.append(" " + this.parameter.toUpperCase());
        }
        if (this.parameterValue != null) {
            sb.append(" = " + this.parameterValue);
        }
        if (this.tableName != null) {
            this.tableName.setObjectContext(this.objectContext);
            sb.append(this.tableName + " ");
        }
        if (this.alterStatementVector != null) {
            for (int i = 0; i < this.alterStatementVector.size(); ++i) {
                SwisSQLStatement swisStmt = null;
                if (this.alterStatementVector.get(i) instanceof CreateQueryStatement) {
                    swisStmt = this.alterStatementVector.get(i);
                    sb.append(swisStmt.toString() + " ");
                }
                else if (this.alterStatementVector.get(i) instanceof DropStatement) {
                    swisStmt = this.alterStatementVector.get(i);
                    sb.append(swisStmt.toString() + " ");
                }
                else if (this.alterStatementVector.get(i) instanceof AlterTable) {
                    final AlterTable tempAlterTable = this.alterStatementVector.get(i);
                    tempAlterTable.setObjectContext(this.objectContext);
                    if (i == 0) {
                        if (tempAlterTable.getsp_renameStmt() != null) {
                            sb.insert(0, tempAlterTable.getsp_renameStmt() + "\n");
                        }
                        sb.append(tempAlterTable);
                    }
                    else if (this.multiAlterStatement == null) {
                        if (this.commaIsSet) {
                            sb.append("," + tempAlterTable);
                        }
                        else {
                            sb.append(tempAlterTable);
                        }
                    }
                    else if (tempAlterTable.toString().trim().length() < 1) {
                        sb.append(" ");
                    }
                    else {
                        if (tempAlterTable.getsp_renameStmt() != null) {
                            sb.append(tempAlterTable.getsp_renameStmt() + "\n");
                        }
                        sb.append("\n\n" + this.multiAlterStatement + " ");
                        if (this.tableOrView != null) {
                            sb.append(this.tableOrView.toUpperCase() + " ");
                        }
                        if (this.tableName != null) {
                            sb.append(this.tableName + " ");
                        }
                        sb.append(tempAlterTable);
                    }
                }
            }
        }
        if (this.onString != null) {
            sb.append("\n\t" + this.onString);
        }
        if (this.quotedIdentifierString != null) {
            sb.append(" " + this.quotedIdentifierString);
        }
        if (!AlterStatement.generalComment.equalsIgnoreCase("")) {
            sb.append("\n\n" + AlterStatement.generalComment + "\n\n");
        }
        if (this.deltek_triggers != null) {
            sb.append("\n\n" + this.deltek_triggers);
        }
        return sb.toString();
    }
    
    private String singleQueryIntoMultipleQueriesForPLSQL() {
        return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.objectContext;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.objectContext = obj;
    }
    
    private SwisSQLStatement handleAddIndexClause(final AlterStatement dupAlterStatement, final AlterTable tempAlterTable) {
        this.indexStatement = new CreateQueryStatement();
        final CreateIndexClause createIndexClauseObject = new CreateIndexClause();
        final AddClause addClauseIndexObject = new AddClause();
        createIndexClauseObject.setIndexOrKey("CREATE INDEX");
        final TableObject indexNameObj = new TableObject();
        String indexName = tempAlterTable.getAddClause().getUniqueConstraintName();
        if (indexName != null && indexName.startsWith("`") && indexName.endsWith("`")) {
            indexName = indexName.substring(1, indexName.length() - 1);
            if (indexName.indexOf(32) != -1) {
                indexName = "\"" + indexName + "\"";
            }
        }
        indexNameObj.setTableName(indexName);
        createIndexClauseObject.setIndexName(indexNameObj);
        createIndexClauseObject.setOn("ON");
        createIndexClauseObject.setTableOrView(dupAlterStatement.getTableName());
        createIndexClauseObject.setOpenBraces("(");
        final Vector indexColumnVect = tempAlterTable.getAddClause().getIndexColumnVector();
        final ArrayList indexColumnList = new ArrayList();
        for (int i = 0; i < indexColumnVect.size(); ++i) {
            final SelectColumn selCol = new SelectColumn();
            final Vector selectColExp = new Vector();
            String indexColName = indexColumnVect.get(i).toString();
            if (indexColName != null && indexColName.startsWith("`") && indexColName.endsWith("`")) {
                indexColName = indexColName.substring(1, indexColName.length() - 1);
                if (indexColName.indexOf(32) != -1) {
                    indexColName = "\"" + indexColName + "\"";
                }
            }
            selectColExp.addElement(indexColName);
            selCol.setColumnExpression(selectColExp);
            final IndexColumn indexCol = new IndexColumn();
            indexCol.setIndexColumnName(selCol);
            indexColumnList.add(indexCol);
        }
        createIndexClauseObject.setIndexColumns(indexColumnList);
        createIndexClauseObject.setClosedBraces(")");
        this.indexStatement.setCreateIndexClause(createIndexClauseObject);
        return this.indexStatement;
    }
    
    private SwisSQLStatement handleDropIndexClause(final AlterStatement dupAlterStatement, final AlterTable tempAlterTable) {
        (this.dropIndexStatement = new DropStatement()).setDrop("DROP");
        this.dropIndexStatement.setTableOrSequence("INDEX");
        String tableName = dupAlterStatement.getTableName().getTableName();
        if (tableName.startsWith("`") && tableName.endsWith("`")) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        tableName = CustomizeUtil.objectNamesToBracedIdentifier(tableName, SwisSQLUtils.getKeywords(2), null);
        final TableObject indexObject = new TableObject();
        String indexName = tempAlterTable.getDropClause().getIndexName();
        if (indexName.startsWith("`") && indexName.endsWith("`")) {
            indexName = indexName.substring(1, indexName.length() - 1);
        }
        indexName = CustomizeUtil.objectNamesToBracedIdentifier(indexName, SwisSQLUtils.getKeywords(2), null);
        indexObject.setUser(tableName);
        indexObject.setDot(".");
        indexObject.setTableName(indexName);
        indexObject.setOrigTableName(indexName);
        final Vector tableObjectVector = new Vector();
        tableObjectVector.add(indexObject);
        this.dropIndexStatement.setTableNameVector(tableObjectVector);
        return this.dropIndexStatement;
    }
    
    public String convertTableObjectToString(final TableObject tableObject) {
        final StringBuffer stringbuffer = new StringBuffer();
        if (tableObject.getUser() != null) {
            stringbuffer.append(this.handleDelimiters(tableObject.getUser()));
            if (tableObject.getOwner() != null) {
                stringbuffer.append(".");
            }
            else {
                stringbuffer.append("..");
            }
        }
        if (tableObject.getOwner() != null) {
            stringbuffer.append(this.handleDelimiters(tableObject.getOwner()));
            stringbuffer.append(".");
        }
        if (tableObject.getTableName() != null) {
            stringbuffer.append(this.handleDelimiters(tableObject.getTableName()));
        }
        if (tableObject.getDatabaseName() != null) {
            stringbuffer.append(this.handleDelimiters(tableObject.getDatabaseName()));
        }
        return stringbuffer.toString();
    }
    
    private String handleDelimiters(String obj) {
        if (obj.startsWith("`") && obj.endsWith("`")) {
            obj = obj.substring(1, obj.length() - 1);
        }
        return obj;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static {
        AlterStatement.generalComment = "";
    }
}
