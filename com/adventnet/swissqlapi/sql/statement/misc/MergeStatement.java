package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.update.SetClause;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.UpdateClause;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class MergeStatement implements SwisSQLStatement
{
    private String mergeInto;
    private String tableAlias;
    private String using;
    private TableObject tableObj;
    private FromTable fromTable;
    private String onClause;
    private String onOpenBrace;
    private String onClosedBrace;
    private String whenMatchedThen;
    private String whenNotMatchedThen;
    private UpdateQueryStatement upQueryStmt;
    private InsertQueryStatement insertQueryStmt;
    private DeleteQueryStatement deleteQueryStmt;
    private WhereExpression whereExp;
    private String upQueryStmtString;
    private String insertQueryStmtString;
    private UpdateQueryStatement convertedUpdQueryStmt;
    private InsertQueryStatement convertedInsQueryStmt;
    private DeleteQueryStatement convertedDelQueryStmt;
    private WhereExpression insertQueryWhereExp;
    private HintClause hintClause;
    private CommentClass commentObj;
    
    public void setMergeInto(final String mergeInto) {
        this.mergeInto = mergeInto;
    }
    
    public void setTableObject(final TableObject tableObj) {
        this.tableObj = tableObj;
    }
    
    public void setTableAlias(final String tableAlias) {
        this.tableAlias = tableAlias;
    }
    
    public void setUsing(final String using) {
        this.using = using;
    }
    
    public void setFromTable(final FromTable ft) {
        this.fromTable = ft;
    }
    
    public void setON(final String onClause) {
        this.onClause = onClause;
    }
    
    public void setONOpenBrace(final String onOpenBrace) {
        this.onOpenBrace = onOpenBrace;
    }
    
    public void setONClosedBrace(final String onClosedBrace) {
        this.onClosedBrace = onClosedBrace;
    }
    
    public void setWhenMatchedThen(final String whenMatchedThen) {
        this.whenMatchedThen = whenMatchedThen;
    }
    
    public void setWhenNotMatchedThen(final String whenNotMatchedThen) {
        this.whenNotMatchedThen = whenNotMatchedThen;
    }
    
    public void setUpdateQueryStatement(final UpdateQueryStatement upQueryStmt) {
        this.upQueryStmt = upQueryStmt;
    }
    
    public void setInsertQueryStatement(final InsertQueryStatement insertQueryStmt) {
        this.insertQueryStmt = insertQueryStmt;
    }
    
    public void setDeleteQueryStatement(final DeleteQueryStatement deleteQueryStmt) {
        this.deleteQueryStmt = deleteQueryStmt;
    }
    
    public void setWhereExpression(final WhereExpression whereExp) {
        this.whereExp = whereExp;
    }
    
    public void setInsertWhereExp(final WhereExpression iWe) {
        this.insertQueryWhereExp = iWe;
    }
    
    public void setUpdateQueryStatementString(final String upQueryStmtString) {
        this.upQueryStmtString = upQueryStmtString;
    }
    
    public void setInsertQueryStatementString(final String insertQueryStmtString) {
        this.insertQueryStmtString = insertQueryStmtString;
    }
    
    public void setHintClause(final HintClause hintClause) {
        this.hintClause = hintClause;
    }
    
    public String getMergeInto() {
        return this.mergeInto;
    }
    
    public TableObject getTableObject() {
        return this.tableObj;
    }
    
    public String getTableAlias() {
        return this.tableAlias;
    }
    
    public String getUsing() {
        return this.using;
    }
    
    public FromTable getFromTable() {
        return this.fromTable;
    }
    
    public String getON() {
        return this.onClause;
    }
    
    public String getONOpenBrace() {
        return this.onOpenBrace;
    }
    
    public String getONClosedBrace() {
        return this.onClosedBrace;
    }
    
    public String getWhenMatchedThen() {
        return this.whenMatchedThen;
    }
    
    public String getWhenNotMatchedThen() {
        return this.whenNotMatchedThen;
    }
    
    public UpdateQueryStatement getUpdateQueryStatement() {
        return this.upQueryStmt;
    }
    
    public InsertQueryStatement getInsertQueryStatement() {
        return this.insertQueryStmt;
    }
    
    public DeleteQueryStatement getDeleteQueryStatement() {
        return this.deleteQueryStmt;
    }
    
    public WhereExpression getWhereExpression() {
        return this.whereExp;
    }
    
    public WhereExpression getInsertWhereExp() {
        return this.insertQueryWhereExp;
    }
    
    public String getUpdateQueryStatementString() {
        return this.upQueryStmtString;
    }
    
    public String getInsertQueryStatementString() {
        return this.insertQueryStmtString;
    }
    
    public HintClause getHintClause() {
        return this.hintClause;
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSIMerge().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataMerge().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Merge().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixMerge().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerMerge().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQLMerge().toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleMerge().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLMerge().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseMerge().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenMerge().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaMerge().toString();
    }
    
    public MergeStatement toANSIMerge() throws ConvertException {
        try {
            final MergeStatement mergeStmt = new MergeStatement();
            if (this.mergeInto != null) {
                mergeStmt.setMergeInto(this.mergeInto);
            }
            if (this.hintClause != null) {
                mergeStmt.setHintClause(this.hintClause);
            }
            if (this.tableObj != null) {
                this.tableObj.toANSISQL();
                mergeStmt.setTableObject(this.tableObj);
            }
            if (this.tableAlias != null) {
                mergeStmt.setTableAlias(this.tableAlias);
            }
            if (this.using != null) {
                mergeStmt.setUsing(this.using);
            }
            if (this.fromTable != null) {
                mergeStmt.setFromTable(this.fromTable.toANSISelect(null, null));
            }
            if (this.onClause != null) {
                mergeStmt.setON(this.onClause);
            }
            if (this.onOpenBrace != null) {
                mergeStmt.setONOpenBrace(this.onOpenBrace);
            }
            if (this.whereExp != null) {
                mergeStmt.setWhereExpression(this.whereExp.toANSISelect(null, null));
            }
            if (this.onClosedBrace != null) {
                mergeStmt.setONClosedBrace(this.onClosedBrace);
            }
            if (this.whenMatchedThen != null) {
                mergeStmt.setWhenMatchedThen(this.whenMatchedThen);
            }
            if (this.upQueryStmt != null) {
                mergeStmt.setUpdateQueryStatementString(this.upQueryStmt.toANSIString());
            }
            if (this.whenNotMatchedThen != null) {
                mergeStmt.setWhenNotMatchedThen(this.whenNotMatchedThen);
            }
            if (this.insertQueryStmt != null) {
                String insertInto = this.insertQueryStmt.toANSIString();
                insertInto = StringFunctions.replaceFirst("", "INTO", insertInto);
                mergeStmt.setInsertQueryStatementString(insertInto);
            }
            return mergeStmt;
        }
        catch (final ConvertException e) {
            throw e;
        }
    }
    
    public MergeStatement toTeradataMerge() throws ConvertException {
        try {
            final MergeStatement mergeStmt = new MergeStatement();
            if (this.commentObj != null) {
                this.commentObj.setSQLDialect(12);
                mergeStmt.setCommentClass(this.commentObj);
            }
            if (this.mergeInto != null) {
                mergeStmt.setMergeInto(this.mergeInto);
            }
            if (this.hintClause != null) {
                mergeStmt.setHintClause(this.hintClause);
            }
            if (this.tableObj != null) {
                this.tableObj.toTeradata();
                mergeStmt.setTableObject(this.tableObj);
            }
            if (this.tableAlias != null) {
                this.tableAlias = CustomizeUtil.objectNamesToQuotedIdentifier(this.tableAlias, SwisSQLUtils.getKeywords("teradata"), null, -1);
                if (this.tableAlias.equalsIgnoreCase("dual") || this.tableAlias.equalsIgnoreCase("sys.dual")) {
                    this.tableAlias = "\"DUAL\"";
                }
                mergeStmt.setTableAlias(this.tableAlias);
            }
            if (this.using != null) {
                mergeStmt.setUsing(this.using);
            }
            if (this.fromTable != null) {
                if (this.fromTable.getTableName() != null && this.fromTable.getTableName() instanceof SelectQueryStatement) {
                    ((SelectQueryStatement)this.fromTable.getTableName()).setTopLevel(true);
                }
                mergeStmt.setFromTable(this.fromTable.toTeradataSelect(null, null));
            }
            if (this.onClause != null) {
                mergeStmt.setON(this.onClause);
            }
            if (this.onOpenBrace != null) {
                mergeStmt.setONOpenBrace(this.onOpenBrace);
            }
            if (this.whereExp != null) {
                mergeStmt.setWhereExpression(this.whereExp.toTeradataSelect(null, null));
            }
            if (this.onClosedBrace != null) {
                mergeStmt.setONClosedBrace(this.onClosedBrace);
            }
            if (this.whenMatchedThen != null) {
                mergeStmt.setWhenMatchedThen(this.whenMatchedThen);
            }
            if (this.upQueryStmt != null) {
                mergeStmt.setUpdateQueryStatementString(this.upQueryStmt.toTeradataString());
            }
            if (this.whenNotMatchedThen != null) {
                mergeStmt.setWhenNotMatchedThen(this.whenNotMatchedThen);
            }
            if (this.insertQueryStmt != null) {
                String insertInto = this.insertQueryStmt.toTeradataString();
                insertInto = StringFunctions.replaceFirst("", "INTO", insertInto);
                mergeStmt.setInsertQueryStatementString(insertInto);
            }
            return mergeStmt;
        }
        catch (final ConvertException e) {
            throw e;
        }
    }
    
    public MergeStatement toDB2Merge() throws ConvertException {
        try {
            throw new ConvertException("Merge query yet to be supported");
        }
        catch (final ConvertException e) {
            throw e;
        }
    }
    
    public MergeStatement toOracleMerge() throws ConvertException {
        final MergeStatement mergeStmt = new MergeStatement();
        if (this.mergeInto != null) {
            mergeStmt.setMergeInto(this.mergeInto);
        }
        if (this.tableObj != null) {
            this.tableObj.toOracle();
            mergeStmt.setTableObject(this.tableObj);
        }
        if (this.tableAlias != null) {
            mergeStmt.setTableAlias(this.tableAlias);
        }
        if (this.using != null) {
            mergeStmt.setUsing(this.using);
        }
        if (this.fromTable != null) {
            mergeStmt.setFromTable(this.fromTable.toOracleSelect(null, null));
        }
        if (this.onClause != null) {
            mergeStmt.setON(this.onClause);
        }
        if (this.onOpenBrace != null) {
            mergeStmt.setONOpenBrace(this.onOpenBrace);
        }
        if (this.whereExp != null) {
            mergeStmt.setWhereExpression(this.whereExp.toOracleSelect(null, null));
        }
        if (this.onClosedBrace != null) {
            mergeStmt.setONClosedBrace(this.onClosedBrace);
        }
        if (this.whenMatchedThen != null) {
            mergeStmt.setWhenMatchedThen(this.whenMatchedThen);
        }
        if (this.upQueryStmt != null) {
            mergeStmt.setUpdateQueryStatementString(this.upQueryStmt.toOracleString());
        }
        if (this.whenNotMatchedThen != null) {
            mergeStmt.setWhenNotMatchedThen(this.whenNotMatchedThen);
        }
        if (this.insertQueryStmt != null) {
            String insertInto = this.insertQueryStmt.toOracleString();
            insertInto = StringFunctions.replaceFirst("", "INTO", insertInto);
            mergeStmt.setInsertQueryStatementString(insertInto);
        }
        return mergeStmt;
    }
    
    public MergeStatement toMSSQLServerMerge() throws ConvertException {
        this.convertedUpdQueryStmt = new UpdateQueryStatement();
        final UpdateClause updClause = new UpdateClause();
        updClause.setUpdate("UPDATE");
        this.convertedUpdQueryStmt.setUpdateClause(updClause);
        final TableExpression tblExp = new TableExpression();
        final ArrayList tblList = new ArrayList();
        this.tableObj.toMSSQLServer();
        tblList.add(this.tableObj);
        tblExp.setTableClauseList(tblList);
        this.convertedUpdQueryStmt.setTableExpression(tblExp);
        final SetClause setClause = new SetClause();
        setClause.setSet("SET");
        setClause.setExpression(this.upQueryStmt.getSetClause().getExpression());
        final ArrayList exprList = setClause.getExpression();
        for (int j = 0; j < exprList.size(); ++j) {
            final Object obj = exprList.get(j);
            if (obj instanceof TableColumn) {
                TableColumn tc = (TableColumn)obj;
                tc = tc.toMSSQLServerSelect(null, null);
                tc.setTableName(null);
                tc.setDot(null);
                exprList.set(j, tc);
            }
        }
        this.convertedUpdQueryStmt.setSetClause(setClause);
        final FromClause fromClause = new FromClause();
        fromClause.setFromClause("FROM");
        final Vector fromClauseSubQuery = new Vector();
        fromClauseSubQuery.add(this.fromTable);
        fromClause.setFromItemList(fromClauseSubQuery);
        this.convertedUpdQueryStmt.setFromClause(fromClause);
        this.convertedUpdQueryStmt.setWhereClause(this.whereExp);
        final WhereExpression whereExpr = this.convertedUpdQueryStmt.getWhereExpression();
        final Vector whereItems = whereExpr.getWhereItems();
        for (int i = 0; i < whereItems.size(); ++i) {
            final WhereItem whereItem = whereItems.get(i);
            final WhereColumn whereColumn = whereItem.getLeftWhereExp();
            final Vector whereColExpr = whereColumn.getColumnExpression();
            for (int k = 0; k < whereColExpr.size(); ++k) {
                final Object obj2 = whereColExpr.get(k);
                if (obj2 instanceof TableColumn) {
                    final TableColumn tc2 = (TableColumn)obj2;
                    if (tc2.getTableName().trim().equalsIgnoreCase(this.tableAlias)) {
                        tc2.setTableName(this.tableObj.getTableName());
                    }
                }
            }
        }
        this.convertedInsQueryStmt = new InsertQueryStatement();
        final InsertClause insertClause = new InsertClause();
        insertClause.setInsert("INSERT");
        final TableExpression insertTblExp = new TableExpression();
        final ArrayList insertTblList = new ArrayList();
        this.tableObj.toMSSQLServer();
        insertTblList.add(this.tableObj);
        insertTblExp.setTableClauseList(tblList);
        insertClause.setTableExpression(insertTblExp);
        final Object obj3 = this.insertQueryStmt.getInsertClause();
        if (obj3 != null) {
            final ArrayList insertColumnList = this.insertQueryStmt.getInsertClause().getColumnList();
            insertClause.setColumnList(insertColumnList);
        }
        final OptionalSpecifier optSpec = new OptionalSpecifier();
        optSpec.setInto("INTO");
        insertClause.setOptionalSpecifier(optSpec);
        this.convertedInsQueryStmt.setInsertClause(insertClause);
        final SelectQueryStatement insertSelect = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        selectStmt.setSelectClause("SELECT");
        final Vector selectStmtItemList = new Vector();
        final ArrayList mergeValuesList = this.insertQueryStmt.getValuesClause().getValuesList();
        for (int l = 0; l < mergeValuesList.size(); ++l) {
            if (mergeValuesList.get(l) instanceof String) {
                final String mergeVal = mergeValuesList.get(l).toString();
                if (!mergeVal.equalsIgnoreCase("(") && !mergeVal.equalsIgnoreCase(")")) {
                    selectStmtItemList.add(mergeValuesList.get(l));
                }
            }
            else {
                selectStmtItemList.add(mergeValuesList.get(l));
            }
        }
        selectStmt.setSelectItemList(selectStmtItemList);
        insertSelect.setSelectStatement(selectStmt);
        final FromClause insertFromClause = new FromClause();
        insertFromClause.setFromClause("FROM");
        final FromTable insertFromTable = this.fromTable;
        final Vector insertFromItemList = new Vector();
        insertFromItemList.add(insertFromTable);
        insertFromClause.setFromItemList(insertFromItemList);
        insertSelect.setFromClause(insertFromClause);
        final ArrayList onClauseLeftWhereColumns = new ArrayList();
        final WhereExpression onClauseWhereExp = this.whereExp;
        final Vector onClauseWhereItems = whereExpr.getWhereItems();
        for (int m = 0; m < whereItems.size(); ++m) {
            final WhereItem whereItem2 = whereItems.get(m);
            final WhereColumn leftWhereColumn = whereItem2.getLeftWhereExp();
            final Vector leftWhereColExpr = leftWhereColumn.getColumnExpression();
            for (int j2 = 0; j2 < leftWhereColExpr.size(); ++j2) {
                final Object obj4 = leftWhereColExpr.get(j2);
                if (obj4 instanceof TableColumn) {
                    final TableColumn tc3 = (TableColumn)obj4;
                    if (tc3.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc3.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                        onClauseLeftWhereColumns.add(tc3.getColumnName());
                    }
                }
            }
            final WhereColumn rightWhereColumn = whereItem2.getRightWhereExp();
            final Vector rightWhereColExpr = rightWhereColumn.getColumnExpression();
            for (int j3 = 0; j3 < rightWhereColExpr.size(); ++j3) {
                final Object obj5 = rightWhereColExpr.get(j3);
                if (obj5 instanceof TableColumn) {
                    final TableColumn tc4 = (TableColumn)obj5;
                    if (tc4.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc4.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                        onClauseLeftWhereColumns.add(tc4.getColumnName());
                    }
                }
            }
        }
        final WhereExpression insertWhereExp = new WhereExpression();
        final WhereItem insertWhereItem = new WhereItem();
        insertWhereItem.setOperator("NOT IN");
        final Vector insertWhereItemLeftWhereExpList = new Vector();
        if (onClauseLeftWhereColumns.size() == 1) {
            insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(0)));
        }
        else {
            insertWhereItemLeftWhereExpList.add("(");
            for (int i2 = 0; i2 < onClauseLeftWhereColumns.size(); ++i2) {
                insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(i2)));
                if (i2 != onClauseLeftWhereColumns.size() - 1) {
                    insertWhereItemLeftWhereExpList.add(",");
                }
            }
            insertWhereItemLeftWhereExpList.add(")");
        }
        final WhereColumn leftWhereColumn2 = new WhereColumn();
        leftWhereColumn2.setColumnExpression(insertWhereItemLeftWhereExpList);
        insertWhereItem.setLeftWhereExp(leftWhereColumn2);
        final WhereColumn rightWhereColumn = new WhereColumn();
        final Vector insertWhereItemRightWhereExpList = new Vector();
        insertWhereItemRightWhereExpList.add("(");
        insertWhereItemRightWhereExpList.add(")");
        insertWhereItem.setRightWhereExp(rightWhereColumn);
        final SelectQueryStatement notInSelectQueryStmt = new SelectQueryStatement();
        final SelectStatement notInSelectStmt = new SelectStatement();
        notInSelectStmt.setSelectClause("SELECT");
        final Vector notInSelectStmtItemList = new Vector();
        for (int i3 = 0; i3 < onClauseLeftWhereColumns.size(); ++i3) {
            final SelectColumn selCol = new SelectColumn();
            final Vector selColExpr = new Vector();
            selColExpr.add(onClauseLeftWhereColumns.get(i3));
            selCol.setColumnExpression(selColExpr);
            notInSelectStmtItemList.add(selCol);
            if (i3 != onClauseLeftWhereColumns.size() - 1) {
                notInSelectStmtItemList.add(",");
            }
        }
        notInSelectStmt.setSelectItemList(notInSelectStmtItemList);
        final FromClause notInFromClause = new FromClause();
        notInFromClause.setFromClause("FROM");
        final FromTable notInFromTable = new FromTable();
        notInFromTable.setTableName(this.tableObj);
        final Vector notInFromItemList = new Vector();
        notInFromItemList.add(notInFromTable);
        notInFromClause.setFromItemList(notInFromItemList);
        notInSelectQueryStmt.setSelectStatement(notInSelectStmt);
        notInSelectQueryStmt.setFromClause(notInFromClause);
        insertWhereItem.setRightWhereSubQuery(notInSelectQueryStmt);
        final Vector insertWhereItemVector = new Vector();
        insertWhereItemVector.add(insertWhereItem);
        insertWhereExp.setWhereItem(insertWhereItemVector);
        insertSelect.setWhereExpression(insertWhereExp);
        this.convertedInsQueryStmt.setSelectQueryStatement(insertSelect);
        return this;
    }
    
    public MergeStatement toMySQLMerge() throws ConvertException {
        try {
            throw new ConvertException("Merge query yet to be supported");
        }
        catch (final ConvertException e) {
            throw e;
        }
    }
    
    public MergeStatement toPostgreSQLMerge() throws ConvertException {
        try {
            throw new ConvertException("Merge query yet to be supported");
        }
        catch (final ConvertException e) {
            throw e;
        }
    }
    
    public MergeStatement toInformixMerge() throws ConvertException {
        try {
            throw new ConvertException("Merge query yet to be supported");
        }
        catch (final ConvertException e) {
            throw e;
        }
    }
    
    public MergeStatement toSybaseMerge() throws ConvertException {
        this.convertedUpdQueryStmt = new UpdateQueryStatement();
        final UpdateClause updClause = new UpdateClause();
        updClause.setUpdate("UPDATE");
        this.convertedUpdQueryStmt.setUpdateClause(updClause);
        final TableExpression tblExp = new TableExpression();
        final ArrayList tblList = new ArrayList();
        this.tableObj.toSybase();
        tblList.add(this.tableObj);
        tblExp.setTableClauseList(tblList);
        this.convertedUpdQueryStmt.setTableExpression(tblExp);
        final SetClause setClause = new SetClause();
        setClause.setSet("SET");
        setClause.setExpression(this.upQueryStmt.getSetClause().getExpression());
        final ArrayList exprList = setClause.getExpression();
        for (int j = 0; j < exprList.size(); ++j) {
            final Object obj = exprList.get(j);
            if (obj instanceof TableColumn) {
                TableColumn tc = (TableColumn)obj;
                tc = tc.toSybaseSelect(null, null);
                tc.setTableName(null);
                tc.setDot(null);
                exprList.set(j, tc);
            }
        }
        this.convertedUpdQueryStmt.setSetClause(setClause);
        final FromClause fromClause = new FromClause();
        fromClause.setFromClause("FROM");
        final Vector fromClauseSubQuery = new Vector();
        fromClauseSubQuery.add(this.fromTable);
        fromClause.setFromItemList(fromClauseSubQuery);
        this.convertedUpdQueryStmt.setFromClause(fromClause);
        this.convertedUpdQueryStmt.setWhereClause(this.whereExp);
        final WhereExpression whereExpr = this.convertedUpdQueryStmt.getWhereExpression();
        final Vector whereItems = whereExpr.getWhereItems();
        for (int i = 0; i < whereItems.size(); ++i) {
            final WhereItem whereItem = whereItems.get(i);
            final WhereColumn whereColumn = whereItem.getLeftWhereExp();
            final Vector whereColExpr = whereColumn.getColumnExpression();
            for (int k = 0; k < whereColExpr.size(); ++k) {
                final Object obj2 = whereColExpr.get(k);
                if (obj2 instanceof TableColumn) {
                    final TableColumn tc2 = (TableColumn)obj2;
                    if (tc2.getTableName().trim().equalsIgnoreCase(this.tableAlias)) {
                        tc2.setTableName(this.tableObj.getTableName());
                    }
                }
            }
        }
        this.convertedInsQueryStmt = new InsertQueryStatement();
        final InsertClause insertClause = new InsertClause();
        insertClause.setInsert("INSERT");
        final TableExpression insertTblExp = new TableExpression();
        final ArrayList insertTblList = new ArrayList();
        this.tableObj.toSybase();
        insertTblList.add(this.tableObj);
        insertTblExp.setTableClauseList(tblList);
        insertClause.setTableExpression(insertTblExp);
        final Object obj3 = this.insertQueryStmt.getInsertClause();
        if (obj3 != null) {
            final ArrayList insertColumnList = this.insertQueryStmt.getInsertClause().getColumnList();
            insertClause.setColumnList(insertColumnList);
        }
        final OptionalSpecifier optSpec = new OptionalSpecifier();
        optSpec.setInto("INTO");
        insertClause.setOptionalSpecifier(optSpec);
        this.convertedInsQueryStmt.setInsertClause(insertClause);
        final SelectQueryStatement insertSelect = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        selectStmt.setSelectClause("SELECT");
        final Vector selectStmtItemList = new Vector();
        final ArrayList mergeValuesList = this.insertQueryStmt.getValuesClause().getValuesList();
        for (int l = 0; l < mergeValuesList.size(); ++l) {
            if (mergeValuesList.get(l) instanceof String) {
                final String mergeVal = mergeValuesList.get(l).toString();
                if (!mergeVal.equalsIgnoreCase("(") && !mergeVal.equalsIgnoreCase(")")) {
                    selectStmtItemList.add(mergeValuesList.get(l));
                }
            }
            else {
                selectStmtItemList.add(mergeValuesList.get(l));
            }
        }
        selectStmt.setSelectItemList(selectStmtItemList);
        insertSelect.setSelectStatement(selectStmt);
        final FromClause insertFromClause = new FromClause();
        insertFromClause.setFromClause("FROM");
        final FromTable insertFromTable = this.fromTable;
        final Vector insertFromItemList = new Vector();
        insertFromItemList.add(insertFromTable);
        insertFromClause.setFromItemList(insertFromItemList);
        insertSelect.setFromClause(insertFromClause);
        final ArrayList onClauseLeftWhereColumns = new ArrayList();
        final WhereExpression onClauseWhereExp = this.whereExp;
        final Vector onClauseWhereItems = whereExpr.getWhereItems();
        for (int m = 0; m < whereItems.size(); ++m) {
            final WhereItem whereItem2 = whereItems.get(m);
            final WhereColumn leftWhereColumn = whereItem2.getLeftWhereExp();
            final Vector leftWhereColExpr = leftWhereColumn.getColumnExpression();
            for (int j2 = 0; j2 < leftWhereColExpr.size(); ++j2) {
                final Object obj4 = leftWhereColExpr.get(j2);
                if (obj4 instanceof TableColumn) {
                    final TableColumn tc3 = (TableColumn)obj4;
                    if (tc3.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc3.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                        onClauseLeftWhereColumns.add(tc3.getColumnName());
                    }
                }
            }
            final WhereColumn rightWhereColumn = whereItem2.getRightWhereExp();
            final Vector rightWhereColExpr = rightWhereColumn.getColumnExpression();
            for (int j3 = 0; j3 < rightWhereColExpr.size(); ++j3) {
                final Object obj5 = rightWhereColExpr.get(j3);
                if (obj5 instanceof TableColumn) {
                    final TableColumn tc4 = (TableColumn)obj5;
                    if (tc4.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc4.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                        onClauseLeftWhereColumns.add(tc4.getColumnName());
                    }
                }
            }
        }
        final WhereExpression insertWhereExp = new WhereExpression();
        final WhereItem insertWhereItem = new WhereItem();
        insertWhereItem.setOperator("NOT IN");
        final Vector insertWhereItemLeftWhereExpList = new Vector();
        if (onClauseLeftWhereColumns.size() == 1) {
            insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(0)));
        }
        else {
            insertWhereItemLeftWhereExpList.add("(");
            for (int i2 = 0; i2 < onClauseLeftWhereColumns.size(); ++i2) {
                insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(i2)));
                if (i2 != onClauseLeftWhereColumns.size() - 1) {
                    insertWhereItemLeftWhereExpList.add(",");
                }
            }
            insertWhereItemLeftWhereExpList.add(")");
        }
        final WhereColumn leftWhereColumn2 = new WhereColumn();
        leftWhereColumn2.setColumnExpression(insertWhereItemLeftWhereExpList);
        insertWhereItem.setLeftWhereExp(leftWhereColumn2);
        final WhereColumn rightWhereColumn = new WhereColumn();
        final Vector insertWhereItemRightWhereExpList = new Vector();
        insertWhereItemRightWhereExpList.add("(");
        insertWhereItemRightWhereExpList.add(")");
        insertWhereItem.setRightWhereExp(rightWhereColumn);
        final SelectQueryStatement notInSelectQueryStmt = new SelectQueryStatement();
        final SelectStatement notInSelectStmt = new SelectStatement();
        notInSelectStmt.setSelectClause("SELECT");
        final Vector notInSelectStmtItemList = new Vector();
        for (int i3 = 0; i3 < onClauseLeftWhereColumns.size(); ++i3) {
            final SelectColumn selCol = new SelectColumn();
            final Vector selColExpr = new Vector();
            selColExpr.add(onClauseLeftWhereColumns.get(i3));
            selCol.setColumnExpression(selColExpr);
            notInSelectStmtItemList.add(selCol);
            if (i3 != onClauseLeftWhereColumns.size() - 1) {
                notInSelectStmtItemList.add(",");
            }
        }
        notInSelectStmt.setSelectItemList(notInSelectStmtItemList);
        final FromClause notInFromClause = new FromClause();
        notInFromClause.setFromClause("FROM");
        final FromTable notInFromTable = new FromTable();
        notInFromTable.setTableName(this.tableObj);
        final Vector notInFromItemList = new Vector();
        notInFromItemList.add(notInFromTable);
        notInFromClause.setFromItemList(notInFromItemList);
        notInSelectQueryStmt.setSelectStatement(notInSelectStmt);
        notInSelectQueryStmt.setFromClause(notInFromClause);
        insertWhereItem.setRightWhereSubQuery(notInSelectQueryStmt);
        final Vector insertWhereItemVector = new Vector();
        insertWhereItemVector.add(insertWhereItem);
        insertWhereExp.setWhereItem(insertWhereItemVector);
        insertSelect.setWhereExpression(insertWhereExp);
        this.convertedInsQueryStmt.setSelectQueryStatement(insertSelect);
        return this;
    }
    
    public MergeStatement toTimesTenMerge() throws ConvertException {
        try {
            throw new ConvertException("Merge query yet to be supported");
        }
        catch (final ConvertException e) {
            throw e;
        }
    }
    
    public MergeStatement toNetezzaMerge() throws ConvertException {
        this.convertedUpdQueryStmt = new UpdateQueryStatement();
        final UpdateClause updClause = new UpdateClause();
        updClause.setUpdate("UPDATE");
        this.convertedUpdQueryStmt.setUpdateClause(updClause);
        final TableExpression tblExp = new TableExpression();
        final ArrayList tblList = new ArrayList();
        this.tableObj.toNetezza();
        tblList.add(this.tableObj);
        tblExp.setTableClauseList(tblList);
        this.convertedUpdQueryStmt.setTableExpression(tblExp);
        final SetClause setClause = new SetClause();
        setClause.setSet("SET");
        this.upQueryStmt.getSetClause().toNetezza();
        setClause.setExpression(this.upQueryStmt.getSetClause().getExpression());
        final ArrayList exprList = setClause.getExpression();
        for (int j = 0; j < exprList.size(); ++j) {
            final Object obj = exprList.get(j);
            if (obj instanceof TableColumn) {
                TableColumn tc = (TableColumn)obj;
                tc = tc.toNetezzaSelect(null, null);
                tc.setTableName(null);
                tc.setDot(null);
                exprList.set(j, tc);
            }
        }
        this.convertedUpdQueryStmt.setSetClause(setClause);
        final FromClause fromClause = new FromClause();
        fromClause.setFromClause("FROM");
        final Vector fromClauseSubQuery = new Vector();
        fromClauseSubQuery.add(this.fromTable.toNetezzaSelect(null, null));
        fromClause.setFromItemList(fromClauseSubQuery);
        this.convertedUpdQueryStmt.setFromClause(fromClause);
        if (this.upQueryStmt.getWhereExpression() != null) {
            this.whereExp.addOperator("AND");
            this.whereExp.addWhereExpression(this.upQueryStmt.getWhereExpression());
        }
        this.convertedUpdQueryStmt.setWhereClause(this.whereExp.toNetezzaSelect(null, null));
        final WhereExpression whereExpr = this.convertedUpdQueryStmt.getWhereExpression();
        final Vector whereItems = whereExpr.getWhereItems();
        for (int i = 0; i < whereItems.size(); ++i) {
            final WhereItem whereItem = whereItems.get(i);
            final WhereColumn whereColumn = whereItem.getLeftWhereExp();
            final Vector whereColExpr = whereColumn.getColumnExpression();
            for (int k = 0; k < whereColExpr.size(); ++k) {
                final Object obj2 = whereColExpr.get(k);
                if (obj2 instanceof TableColumn) {
                    final TableColumn tc2 = (TableColumn)obj2;
                    if (tc2.getTableName().trim().equalsIgnoreCase(this.tableAlias)) {
                        tc2.setTableName(this.tableObj.getTableName());
                    }
                }
            }
        }
        this.convertedInsQueryStmt = new InsertQueryStatement();
        final InsertClause insertClause = new InsertClause();
        insertClause.setInsert("INSERT");
        final TableExpression insertTblExp = new TableExpression();
        final ArrayList insertTblList = new ArrayList();
        this.tableObj.toNetezza();
        insertTblList.add(this.tableObj);
        insertTblExp.setTableClauseList(insertTblList);
        insertClause.setTableExpression(insertTblExp);
        this.insertQueryStmt.getInsertClause().toNetezza(this.insertQueryStmt);
        final ArrayList insertColumnList = this.insertQueryStmt.getInsertClause().getColumnList();
        insertClause.setColumnList(insertColumnList);
        final OptionalSpecifier optSpec = new OptionalSpecifier();
        optSpec.setInto("INTO");
        insertClause.setOptionalSpecifier(optSpec);
        this.convertedInsQueryStmt.setInsertClause(insertClause);
        final SelectQueryStatement insertSelect = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        selectStmt.setSelectClause("SELECT");
        final Vector selectStmtItemList = new Vector();
        final ArrayList mergeValuesList = this.insertQueryStmt.getValuesClause().toNetezza().getValuesList();
        for (int l = 0; l < mergeValuesList.size(); ++l) {
            if (mergeValuesList.get(l) instanceof String) {
                final String mergeVal = mergeValuesList.get(l).toString();
                if (!mergeVal.equalsIgnoreCase("(") && !mergeVal.equalsIgnoreCase(")")) {
                    selectStmtItemList.add(mergeValuesList.get(l));
                }
            }
            else {
                selectStmtItemList.add(mergeValuesList.get(l));
            }
        }
        selectStmt.setSelectItemList(selectStmtItemList);
        insertSelect.setSelectStatement(selectStmt);
        final FromClause insertFromClause = new FromClause();
        insertFromClause.setFromClause("FROM");
        final FromTable insertFromTable = this.fromTable.toNetezzaSelect(null, null);
        final Vector insertFromItemList = new Vector();
        insertFromItemList.add(insertFromTable);
        insertFromClause.setFromItemList(insertFromItemList);
        insertSelect.setFromClause(insertFromClause);
        final ArrayList onClauseLeftWhereColumns = new ArrayList();
        final WhereExpression onClauseWhereExp = this.whereExp.toNetezzaSelect(null, null);
        final Vector onClauseWhereItems = whereExpr.getWhereItems();
        for (int m = 0; m < whereItems.size(); ++m) {
            final WhereItem whereItem2 = whereItems.get(m);
            final WhereColumn leftWhereColumn = whereItem2.getLeftWhereExp();
            final Vector leftWhereColExpr = leftWhereColumn.getColumnExpression();
            for (int j2 = 0; j2 < leftWhereColExpr.size(); ++j2) {
                final Object obj3 = leftWhereColExpr.get(j2);
                if (obj3 instanceof TableColumn) {
                    final TableColumn tc3 = (TableColumn)obj3;
                    if (tc3.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc3.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                        onClauseLeftWhereColumns.add(tc3.getColumnName());
                    }
                }
            }
            final WhereColumn rightWhereColumn = whereItem2.getRightWhereExp();
            final Vector rightWhereColExpr = rightWhereColumn.getColumnExpression();
            for (int j3 = 0; j3 < rightWhereColExpr.size(); ++j3) {
                final Object obj4 = rightWhereColExpr.get(j3);
                if (obj4 instanceof TableColumn) {
                    final TableColumn tc4 = (TableColumn)obj4;
                    if (tc4.getTableName().trim().equalsIgnoreCase(this.tableAlias) || tc4.getTableName().trim().equalsIgnoreCase(this.tableObj.getTableName())) {
                        onClauseLeftWhereColumns.add(tc4.getColumnName());
                    }
                }
            }
        }
        final WhereExpression insertWhereExp = new WhereExpression();
        final WhereItem insertWhereItem = new WhereItem();
        insertWhereItem.setOperator("NOT IN");
        final Vector insertWhereItemLeftWhereExpList = new Vector();
        if (onClauseLeftWhereColumns.size() == 1) {
            insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(0)));
        }
        else {
            insertWhereItemLeftWhereExpList.add("(");
            for (int i2 = 0; i2 < onClauseLeftWhereColumns.size(); ++i2) {
                insertWhereItemLeftWhereExpList.add(this.convertTableColumnToWhereColumn(onClauseLeftWhereColumns.get(i2)));
                if (i2 != onClauseLeftWhereColumns.size() - 1) {
                    insertWhereItemLeftWhereExpList.add(",");
                }
            }
            insertWhereItemLeftWhereExpList.add(")");
        }
        final WhereColumn leftWhereColumn2 = new WhereColumn();
        leftWhereColumn2.setColumnExpression(insertWhereItemLeftWhereExpList);
        insertWhereItem.setLeftWhereExp(leftWhereColumn2);
        final WhereColumn rightWhereColumn = new WhereColumn();
        final Vector insertWhereItemRightWhereExpList = new Vector();
        insertWhereItemRightWhereExpList.add("(");
        insertWhereItemRightWhereExpList.add(")");
        insertWhereItem.setRightWhereExp(rightWhereColumn);
        final SelectQueryStatement notInSelectQueryStmt = new SelectQueryStatement();
        final SelectStatement notInSelectStmt = new SelectStatement();
        notInSelectStmt.setSelectClause("SELECT");
        final Vector notInSelectStmtItemList = new Vector();
        for (int i3 = 0; i3 < onClauseLeftWhereColumns.size(); ++i3) {
            final SelectColumn selCol = new SelectColumn();
            final Vector selColExpr = new Vector();
            selColExpr.add(onClauseLeftWhereColumns.get(i3));
            selCol.setColumnExpression(selColExpr);
            notInSelectStmtItemList.add(selCol);
            if (i3 != onClauseLeftWhereColumns.size() - 1) {
                notInSelectStmtItemList.add(",");
            }
        }
        notInSelectStmt.setSelectItemList(notInSelectStmtItemList);
        final FromClause notInFromClause = new FromClause();
        notInFromClause.setFromClause("FROM");
        final FromTable notInFromTable = new FromTable();
        notInFromTable.setTableName(this.tableObj);
        final Vector notInFromItemList = new Vector();
        notInFromItemList.add(notInFromTable);
        notInFromClause.setFromItemList(notInFromItemList);
        notInSelectQueryStmt.setSelectStatement(notInSelectStmt);
        notInSelectQueryStmt.setFromClause(notInFromClause);
        insertWhereItem.setRightWhereSubQuery(notInSelectQueryStmt);
        final Vector insertWhereItemVector = new Vector();
        insertWhereItemVector.add(insertWhereItem);
        insertWhereExp.setWhereItem(insertWhereItemVector);
        if (this.insertQueryWhereExp != null) {
            insertWhereExp.addOperator("AND");
            insertWhereExp.addWhereExpression(this.insertQueryWhereExp);
        }
        insertSelect.setWhereExpression(insertWhereExp);
        this.convertedInsQueryStmt.setSelectQueryStatement(insertSelect);
        if (this.deleteQueryStmt != null) {
            this.convertedDelQueryStmt = new DeleteQueryStatement();
            final OptionalSpecifier opSpec = new OptionalSpecifier();
            opSpec.setFrom("FROM");
            this.deleteQueryStmt.getDeleteClause().setOptionalSpecifier(opSpec);
            this.deleteQueryStmt.getDeleteClause().toNetezza();
            this.convertedDelQueryStmt.setDeleteClause(this.deleteQueryStmt.getDeleteClause());
            final TableExpression deleteTableExp = new TableExpression();
            final ArrayList deleteTblList = new ArrayList();
            this.tableObj.toNetezza();
            deleteTblList.add(this.tableObj);
            deleteTableExp.setTableClauseList(deleteTblList);
            this.convertedDelQueryStmt.setTableExpression(deleteTableExp);
            this.convertedDelQueryStmt.setWhereClause(this.deleteQueryStmt.getWhereExpression().toNetezzaSelect(null, null));
        }
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuffer upsertStr = new StringBuffer();
        if (this.convertedUpdQueryStmt != null) {
            upsertStr.append(this.convertedUpdQueryStmt.toString() + ";\n");
        }
        if (this.convertedDelQueryStmt != null) {
            upsertStr.append(this.convertedDelQueryStmt.toString() + ";\n");
        }
        if (this.convertedInsQueryStmt != null) {
            upsertStr.append(this.convertedInsQueryStmt.toString() + "\n");
            return upsertStr.toString();
        }
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString() + "\n");
        }
        if (this.mergeInto != null) {
            sb.append(this.getMergeInto() + " ");
        }
        if (this.hintClause != null) {
            sb.append(this.hintClause.toString() + " ");
        }
        if (this.tableObj != null) {
            sb.append(this.getTableObject().toString() + " ");
        }
        if (this.tableAlias != null) {
            sb.append(this.getTableAlias() + "\n");
        }
        if (this.using != null) {
            sb.append(this.getUsing() + " ");
        }
        if (this.fromTable != null) {
            sb.append(this.getFromTable().toString() + "\n");
        }
        if (this.onClause != null) {
            sb.append(this.getON() + " ");
        }
        if (this.onOpenBrace != null) {
            sb.append(this.getONOpenBrace());
        }
        if (this.whereExp != null) {
            sb.append(this.getWhereExpression().toString());
        }
        if (this.onClosedBrace != null) {
            sb.append(this.getONClosedBrace() + "\n");
        }
        if (this.whenMatchedThen != null) {
            sb.append(this.getWhenMatchedThen() + "\n");
        }
        if (this.getUpdateQueryStatementString() != null) {
            sb.append(this.getUpdateQueryStatementString() + "\n");
        }
        else if (this.upQueryStmt != null) {
            sb.append(this.upQueryStmt + "\n");
        }
        if (this.whenNotMatchedThen != null) {
            sb.append(this.getWhenNotMatchedThen() + "\n");
        }
        if (this.getInsertQueryStatementString() != null) {
            sb.append(this.getInsertQueryStatementString());
        }
        else if (this.insertQueryStmt != null) {
            sb.append(this.insertQueryStmt + "\n");
        }
        return sb.toString();
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObj = commentObject;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return new CommentClass();
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
    
    private WhereColumn convertTableColumnToWhereColumn(final Object obj) {
        final TableColumn tableCol = new TableColumn();
        tableCol.setColumnName(obj.toString());
        final WhereColumn whereCol = new WhereColumn();
        final Vector columnExpr = new Vector();
        columnExpr.add(tableCol);
        whereCol.setColumnExpression(columnExpr);
        return whereCol;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
