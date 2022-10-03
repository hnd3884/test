package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class ShowStatement implements SwisSQLStatement
{
    private String showStr;
    private String selectColumnStr;
    private String fromStr;
    private FromTable fromTableItem;
    private String likeStr;
    private String likePattern;
    private String status;
    
    public void setShow(final String show) {
        this.showStr = show;
    }
    
    public void setSelectColumn(final String selectColumnString) {
        this.selectColumnStr = selectColumnString;
    }
    
    public void setFrom(final String from) {
        this.fromStr = from;
    }
    
    public void setFromItem(final FromTable fromItem) {
        this.fromTableItem = fromItem;
    }
    
    public void setLike(final String like) {
        this.likeStr = like;
    }
    
    public void setLikePattern(final String likePattern) {
        this.likePattern = likePattern;
    }
    
    public String getShow() {
        return this.showStr;
    }
    
    public String getSelectColumn() {
        return this.selectColumnStr;
    }
    
    public String getFrom() {
        return this.fromStr;
    }
    
    public FromTable getFromItem() {
        return this.fromTableItem;
    }
    
    public String getLike() {
        return this.likeStr;
    }
    
    public String getLikePattern() {
        return this.likePattern;
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleShow().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerShow().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseShow().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Show().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLShow().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQLShow().toString();
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSIShow().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataShow().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixShow().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenShow().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaShow().toString();
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
    
    public SwisSQLStatement toOracleShow() throws ConvertException {
        final SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        boolean isShowTables = false;
        if (this.getShow() != null) {
            selectStmt.setSelectClause("SELECT");
        }
        if (this.getSelectColumn() != null) {
            final String selectColStr = this.getSelectColumn();
            final Vector selectItems = new Vector();
            final SelectColumn selectCol = new SelectColumn();
            final Vector selectColVector = new Vector();
            if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
                selectColVector.add("TABLE_NAME");
                isShowTables = true;
                final FromClause fromClause = new FromClause();
                fromClause.setFromClause("FROM");
                final Vector fromItemList = new Vector();
                fromItemList.add("USER_TABLES");
                fromClause.setFromItemList(fromItemList);
                selectQueryStmt.setFromClause(fromClause);
            }
            else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
                selectColVector.add("*");
                isShowTables = false;
            }
            selectCol.setColumnExpression(selectColVector);
            selectItems.add(selectCol);
            selectStmt.setSelectItemList(selectItems);
            selectQueryStmt.setSelectStatement(selectStmt);
        }
        if (this.getFrom() != null && this.getFromItem() != null) {
            final FromClause fromClause2 = new FromClause();
            fromClause2.setFromClause("FROM");
            final Vector fromItemList2 = new Vector();
            fromItemList2.add("USER_TAB_COLUMNS");
            fromClause2.setFromItemList(fromItemList2);
            selectQueryStmt.setFromClause(fromClause2);
            final WhereExpression whereExp = new WhereExpression();
            final Vector whereExpVec = new Vector();
            final WhereItem whereItem = new WhereItem();
            final WhereColumn leftWhereColumn = new WhereColumn();
            final Vector leftColExpr = new Vector();
            final TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("TABLE_NAME");
            leftColExpr.add(leftTableColumn);
            leftWhereColumn.setColumnExpression(leftColExpr);
            final WhereColumn rightWhereColumn = new WhereColumn();
            final Vector rightColExpr = new Vector();
            final FromTable fromItemTable = this.getFromItem();
            final Object fromItemTableName = fromItemTable.getTableName();
            if (fromItemTableName instanceof TableObject) {
                final TableObject fromItemTableObject = (TableObject)fromItemTableName;
                final String fromItemTableObjectName = fromItemTableObject.getTableName();
                rightColExpr.add("'" + fromItemTableObjectName.toUpperCase() + "'");
                rightWhereColumn.setColumnExpression(rightColExpr);
            }
            else if (fromItemTableName instanceof String) {
                rightColExpr.add("'" + fromItemTableName.toString().toUpperCase() + "'");
                rightWhereColumn.setColumnExpression(rightColExpr);
            }
            whereItem.setLeftWhereExp(leftWhereColumn);
            whereItem.setRightWhereExp(rightWhereColumn);
            whereItem.setOperator("=");
            final Vector whereItemVector = new Vector();
            whereItemVector.add(whereItem);
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
        }
        if (this.getLike() != null && this.getLikePattern() != null) {
            final WhereItem likeWhereItem = new WhereItem();
            final WhereColumn leftWhereColumn2 = new WhereColumn();
            final Vector leftColExpr2 = new Vector();
            final TableColumn leftTableColumn2 = new TableColumn();
            if (isShowTables) {
                leftTableColumn2.setColumnName("TABLE_NAME");
            }
            else {
                leftTableColumn2.setColumnName("COLUMN_NAME");
            }
            leftColExpr2.add(leftTableColumn2);
            leftWhereColumn2.setColumnExpression(leftColExpr2);
            final WhereColumn rightWhereColumn2 = new WhereColumn();
            final Vector rightColExpr2 = new Vector();
            rightColExpr2.add(this.getLikePattern());
            rightWhereColumn2.setColumnExpression(rightColExpr2);
            likeWhereItem.setLeftWhereExp(leftWhereColumn2);
            likeWhereItem.setRightWhereExp(rightWhereColumn2);
            likeWhereItem.setOperator("LIKE");
            final Vector whereItemVector2 = new Vector();
            whereItemVector2.add(likeWhereItem);
            if (selectQueryStmt.getWhereExpression() != null) {
                if (selectQueryStmt.getWhereExpression().getOperator() != null) {
                    selectQueryStmt.getWhereExpression().getOperator().add("AND");
                }
                selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
            }
            else {
                final WhereExpression whereExp2 = new WhereExpression();
                whereExp2.setWhereItem(whereItemVector2);
                selectQueryStmt.setWhereExpression(whereExp2);
            }
        }
        return selectQueryStmt;
    }
    
    public SwisSQLStatement toMSSQLServerShow() throws ConvertException {
        final SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        boolean isShowTables = false;
        if (this.getShow() != null) {
            selectStmt.setSelectClause("SELECT");
        }
        if (this.getSelectColumn() != null) {
            final String selectColStr = this.getSelectColumn();
            final Vector selectItems = new Vector();
            final SelectColumn selectCol = new SelectColumn();
            final Vector selectColVector = new Vector();
            if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
                selectColVector.add("TABLE_NAME");
                isShowTables = true;
                final FromClause fromClause = new FromClause();
                fromClause.setFromClause("FROM");
                final Vector fromItemList = new Vector();
                fromItemList.add("INFORMATION_SCHEMA.TABLES");
                fromClause.setFromItemList(fromItemList);
                selectQueryStmt.setFromClause(fromClause);
            }
            else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
                selectColVector.add("*");
                isShowTables = false;
            }
            selectCol.setColumnExpression(selectColVector);
            selectItems.add(selectCol);
            selectStmt.setSelectItemList(selectItems);
            selectQueryStmt.setSelectStatement(selectStmt);
        }
        if (this.getFrom() != null && this.getFromItem() != null) {
            final FromClause fromClause2 = new FromClause();
            fromClause2.setFromClause("FROM");
            final Vector fromItemList2 = new Vector();
            fromItemList2.add("INFORMATION_SCHEMA.COLUMNS");
            fromClause2.setFromItemList(fromItemList2);
            selectQueryStmt.setFromClause(fromClause2);
            final WhereExpression whereExp = new WhereExpression();
            final Vector whereExpVec = new Vector();
            final WhereItem whereItem = new WhereItem();
            final WhereColumn leftWhereColumn = new WhereColumn();
            final Vector leftColExpr = new Vector();
            final TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("TABLE_NAME");
            leftColExpr.add(leftTableColumn);
            leftWhereColumn.setColumnExpression(leftColExpr);
            final WhereColumn rightWhereColumn = new WhereColumn();
            final Vector rightColExpr = new Vector();
            final FromTable fromItemTable = this.getFromItem();
            final Object fromItemTableName = fromItemTable.getTableName();
            if (fromItemTableName instanceof TableObject) {
                final TableObject fromItemTableObject = (TableObject)fromItemTableName;
                final String fromItemTableObjectName = fromItemTableObject.getTableName();
                rightColExpr.add("'" + fromItemTableObjectName + "'");
                rightWhereColumn.setColumnExpression(rightColExpr);
            }
            else if (fromItemTableName instanceof String) {
                rightColExpr.add("'" + fromItemTableName.toString() + "'");
                rightWhereColumn.setColumnExpression(rightColExpr);
            }
            whereItem.setLeftWhereExp(leftWhereColumn);
            whereItem.setRightWhereExp(rightWhereColumn);
            whereItem.setOperator("=");
            final Vector whereItemVector = new Vector();
            whereItemVector.add(whereItem);
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
        }
        if (this.getLike() != null && this.getLikePattern() != null) {
            final WhereItem likeWhereItem = new WhereItem();
            final WhereColumn leftWhereColumn2 = new WhereColumn();
            final Vector leftColExpr2 = new Vector();
            final TableColumn leftTableColumn2 = new TableColumn();
            if (isShowTables) {
                leftTableColumn2.setColumnName("TABLE_NAME");
            }
            else {
                leftTableColumn2.setColumnName("COLUMN_NAME");
            }
            leftColExpr2.add(leftTableColumn2);
            leftWhereColumn2.setColumnExpression(leftColExpr2);
            final WhereColumn rightWhereColumn2 = new WhereColumn();
            final Vector rightColExpr2 = new Vector();
            rightColExpr2.add(this.getLikePattern());
            rightWhereColumn2.setColumnExpression(rightColExpr2);
            likeWhereItem.setLeftWhereExp(leftWhereColumn2);
            likeWhereItem.setRightWhereExp(rightWhereColumn2);
            likeWhereItem.setOperator("LIKE");
            final Vector whereItemVector2 = new Vector();
            whereItemVector2.add(likeWhereItem);
            if (selectQueryStmt.getWhereExpression() != null) {
                if (selectQueryStmt.getWhereExpression().getOperator() != null) {
                    selectQueryStmt.getWhereExpression().getOperator().add("AND");
                }
                selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
            }
            else {
                final WhereExpression whereExp2 = new WhereExpression();
                whereExp2.setWhereItem(whereItemVector2);
                selectQueryStmt.setWhereExpression(whereExp2);
            }
        }
        return selectQueryStmt;
    }
    
    public SwisSQLStatement toSybaseShow() throws ConvertException {
        final SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        boolean isShowTables = false;
        if (this.getShow() != null) {
            selectStmt.setSelectClause("SELECT");
        }
        if (this.getSelectColumn() != null) {
            final String selectColStr = this.getSelectColumn();
            final Vector selectItems = new Vector();
            final SelectColumn selectCol = new SelectColumn();
            final Vector selectColVector = new Vector();
            if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
                selectColVector.add("name");
                isShowTables = true;
                final FromClause fromClause = new FromClause();
                fromClause.setFromClause("FROM");
                final Vector fromItemList = new Vector();
                fromItemList.add("sysobjects");
                fromClause.setFromItemList(fromItemList);
                selectQueryStmt.setFromClause(fromClause);
                final WhereExpression whereExp = new WhereExpression();
                final Vector whereExpVec = new Vector();
                final WhereItem whereItem = new WhereItem();
                final WhereColumn leftWhereColumn = new WhereColumn();
                final Vector leftColExpr = new Vector();
                final TableColumn leftTableColumn = new TableColumn();
                leftTableColumn.setColumnName("type");
                leftColExpr.add(leftTableColumn);
                leftWhereColumn.setColumnExpression(leftColExpr);
                final WhereColumn rightWhereColumn = new WhereColumn();
                final Vector rightColExpr = new Vector();
                rightColExpr.add("'U'");
                rightWhereColumn.setColumnExpression(rightColExpr);
                whereItem.setLeftWhereExp(leftWhereColumn);
                whereItem.setRightWhereExp(rightWhereColumn);
                whereItem.setOperator("=");
                final Vector whereItemVector = new Vector();
                whereItemVector.add(whereItem);
                whereExp.setWhereItem(whereItemVector);
                selectQueryStmt.setWhereExpression(whereExp);
            }
            else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
                selectColVector.add("syscolumns.*");
                isShowTables = false;
            }
            selectCol.setColumnExpression(selectColVector);
            selectItems.add(selectCol);
            selectStmt.setSelectItemList(selectItems);
            selectQueryStmt.setSelectStatement(selectStmt);
        }
        if (this.getFrom() != null && this.getFromItem() != null) {
            final FromClause fromClause2 = new FromClause();
            fromClause2.setFromClause("FROM");
            final Vector fromItemList2 = new Vector();
            fromItemList2.add("syscolumns");
            fromItemList2.add("sysobjects");
            fromClause2.setFromItemList(fromItemList2);
            selectQueryStmt.setFromClause(fromClause2);
            final WhereExpression whereExp2 = new WhereExpression();
            final Vector whereExpVec2 = new Vector();
            final WhereItem whereItem2 = new WhereItem();
            final WhereColumn leftWhereColumn2 = new WhereColumn();
            final Vector leftColExpr2 = new Vector();
            final TableColumn leftTableColumn2 = new TableColumn();
            leftTableColumn2.setColumnName("syscolumns.id");
            leftColExpr2.add(leftTableColumn2);
            leftWhereColumn2.setColumnExpression(leftColExpr2);
            final WhereColumn rightWhereColumn2 = new WhereColumn();
            final Vector rightColExpr2 = new Vector();
            rightColExpr2.add("sysobjects.id");
            rightWhereColumn2.setColumnExpression(rightColExpr2);
            whereItem2.setLeftWhereExp(leftWhereColumn2);
            whereItem2.setRightWhereExp(rightWhereColumn2);
            whereItem2.setOperator("=");
            final WhereItem whereItem3 = new WhereItem();
            final WhereColumn leftWhereColumn3 = new WhereColumn();
            final Vector leftColExpr3 = new Vector();
            final TableColumn leftTableColumn3 = new TableColumn();
            leftTableColumn3.setColumnName("sysobjects.name");
            leftColExpr3.add(leftTableColumn3);
            leftWhereColumn3.setColumnExpression(leftColExpr3);
            final WhereColumn rightWhereColumn3 = new WhereColumn();
            final Vector rightColExpr3 = new Vector();
            final FromTable fromItemTable = this.getFromItem();
            final Object fromItemTableName = fromItemTable.getTableName();
            if (fromItemTableName instanceof TableObject) {
                final TableObject fromItemTableObject = (TableObject)fromItemTableName;
                final String fromItemTableObjectName = fromItemTableObject.getTableName();
                rightColExpr3.add("'" + fromItemTableObjectName + "'");
                rightWhereColumn3.setColumnExpression(rightColExpr3);
            }
            else if (fromItemTableName instanceof String) {
                rightColExpr3.add("'" + fromItemTableName.toString() + "'");
                rightWhereColumn3.setColumnExpression(rightColExpr3);
            }
            whereItem3.setLeftWhereExp(leftWhereColumn3);
            whereItem3.setRightWhereExp(rightWhereColumn3);
            whereItem3.setOperator("=");
            final Vector whereItemVector2 = new Vector();
            whereItemVector2.add(whereItem2);
            whereItemVector2.add(whereItem3);
            whereExp2.setWhereItem(whereItemVector2);
            final Vector whereExpOperatorVec = new Vector();
            whereExpOperatorVec.add("AND");
            whereExp2.setOperator(whereExpOperatorVec);
            selectQueryStmt.setWhereExpression(whereExp2);
        }
        if (this.getLike() != null && this.getLikePattern() != null) {
            final WhereItem likeWhereItem = new WhereItem();
            final WhereColumn leftWhereColumn4 = new WhereColumn();
            final Vector leftColExpr4 = new Vector();
            final TableColumn leftTableColumn4 = new TableColumn();
            if (isShowTables) {
                leftTableColumn4.setColumnName("name");
            }
            else {
                leftTableColumn4.setColumnName("syscolumns.name");
            }
            leftColExpr4.add(leftTableColumn4);
            leftWhereColumn4.setColumnExpression(leftColExpr4);
            final WhereColumn rightWhereColumn4 = new WhereColumn();
            final Vector rightColExpr4 = new Vector();
            rightColExpr4.add(this.getLikePattern());
            rightWhereColumn4.setColumnExpression(rightColExpr4);
            likeWhereItem.setLeftWhereExp(leftWhereColumn4);
            likeWhereItem.setRightWhereExp(rightWhereColumn4);
            likeWhereItem.setOperator("LIKE");
            final Vector whereItemVector3 = new Vector();
            whereItemVector3.add(likeWhereItem);
            if (selectQueryStmt.getWhereExpression() != null) {
                if (selectQueryStmt.getWhereExpression().getOperator() != null) {
                    selectQueryStmt.getWhereExpression().getOperator().add("AND");
                }
                selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
            }
            else {
                final WhereExpression whereExp3 = new WhereExpression();
                whereExp3.setWhereItem(whereItemVector3);
                selectQueryStmt.setWhereExpression(whereExp3);
            }
        }
        return selectQueryStmt;
    }
    
    public SwisSQLStatement toDB2Show() throws ConvertException {
        final SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        boolean isShowTables = false;
        if (this.getShow() != null) {
            selectStmt.setSelectClause("SELECT");
        }
        if (this.getSelectColumn() != null) {
            final String selectColStr = this.getSelectColumn();
            final Vector selectItems = new Vector();
            final SelectColumn selectCol = new SelectColumn();
            final Vector selectColVector = new Vector();
            if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
                selectColVector.add("TABNAME");
                isShowTables = true;
                final FromClause fromClause = new FromClause();
                fromClause.setFromClause("FROM");
                final Vector fromItemList = new Vector();
                fromItemList.add("SYSCAT.TABLES");
                fromClause.setFromItemList(fromItemList);
                selectQueryStmt.setFromClause(fromClause);
            }
            else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
                selectColVector.add("*");
                isShowTables = false;
            }
            selectCol.setColumnExpression(selectColVector);
            selectItems.add(selectCol);
            selectStmt.setSelectItemList(selectItems);
            selectQueryStmt.setSelectStatement(selectStmt);
        }
        if (this.getFrom() != null && this.getFromItem() != null) {
            final FromClause fromClause2 = new FromClause();
            fromClause2.setFromClause("FROM");
            final Vector fromItemList2 = new Vector();
            fromItemList2.add("SYSCAT.COLUMNS");
            fromClause2.setFromItemList(fromItemList2);
            selectQueryStmt.setFromClause(fromClause2);
            final WhereExpression whereExp = new WhereExpression();
            final Vector whereExpVec = new Vector();
            final WhereItem whereItem = new WhereItem();
            final WhereColumn leftWhereColumn = new WhereColumn();
            final Vector leftColExpr = new Vector();
            final TableColumn leftTableColumn = new TableColumn();
            leftTableColumn.setColumnName("TABNAME");
            leftColExpr.add(leftTableColumn);
            leftWhereColumn.setColumnExpression(leftColExpr);
            final WhereColumn rightWhereColumn = new WhereColumn();
            final Vector rightColExpr = new Vector();
            final FromTable fromItemTable = this.getFromItem();
            final Object fromItemTableName = fromItemTable.getTableName();
            if (fromItemTableName instanceof TableObject) {
                final TableObject fromItemTableObject = (TableObject)fromItemTableName;
                final String fromItemTableObjectName = fromItemTableObject.getTableName();
                rightColExpr.add("'" + fromItemTableObjectName.toUpperCase() + "'");
                rightWhereColumn.setColumnExpression(rightColExpr);
            }
            else if (fromItemTableName instanceof String) {
                rightColExpr.add("'" + fromItemTableName.toString().toUpperCase() + "'");
                rightWhereColumn.setColumnExpression(rightColExpr);
            }
            whereItem.setLeftWhereExp(leftWhereColumn);
            whereItem.setRightWhereExp(rightWhereColumn);
            whereItem.setOperator("=");
            final Vector whereItemVector = new Vector();
            whereItemVector.add(whereItem);
            whereExp.setWhereItem(whereItemVector);
            selectQueryStmt.setWhereExpression(whereExp);
        }
        if (this.getLike() != null && this.getLikePattern() != null) {
            final WhereItem likeWhereItem = new WhereItem();
            final WhereColumn leftWhereColumn2 = new WhereColumn();
            final Vector leftColExpr2 = new Vector();
            final TableColumn leftTableColumn2 = new TableColumn();
            if (isShowTables) {
                leftTableColumn2.setColumnName("TABNAME");
            }
            else {
                leftTableColumn2.setColumnName("COLNAME");
            }
            leftColExpr2.add(leftTableColumn2);
            leftWhereColumn2.setColumnExpression(leftColExpr2);
            final WhereColumn rightWhereColumn2 = new WhereColumn();
            final Vector rightColExpr2 = new Vector();
            rightColExpr2.add(this.getLikePattern());
            rightWhereColumn2.setColumnExpression(rightColExpr2);
            likeWhereItem.setLeftWhereExp(leftWhereColumn2);
            likeWhereItem.setRightWhereExp(rightWhereColumn2);
            likeWhereItem.setOperator("LIKE");
            final Vector whereItemVector2 = new Vector();
            whereItemVector2.add(likeWhereItem);
            if (selectQueryStmt.getWhereExpression() != null) {
                if (selectQueryStmt.getWhereExpression().getOperator() != null) {
                    selectQueryStmt.getWhereExpression().getOperator().add("AND");
                }
                selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
            }
            else {
                final WhereExpression whereExp2 = new WhereExpression();
                whereExp2.setWhereItem(whereItemVector2);
                selectQueryStmt.setWhereExpression(whereExp2);
            }
        }
        return selectQueryStmt;
    }
    
    public SwisSQLStatement toPostgreSQLShow() throws ConvertException {
        final SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        boolean isShowTables = false;
        if (this.getShow() != null) {
            selectStmt.setSelectClause("SELECT");
        }
        if (this.getSelectColumn() != null) {
            final String selectColStr = this.getSelectColumn();
            final Vector selectItems = new Vector();
            final SelectColumn selectCol = new SelectColumn();
            final Vector selectColVector = new Vector();
            if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
                selectColVector.add("TABLE_NAME");
                isShowTables = true;
                final FromClause fromClause = new FromClause();
                fromClause.setFromClause("FROM");
                final Vector fromItemList = new Vector();
                fromItemList.add("INFORMATION_SCHEMA.TABLES");
                fromClause.setFromItemList(fromItemList);
                selectQueryStmt.setFromClause(fromClause);
                final WhereExpression whereExp = new WhereExpression();
                final Vector whereExpVec = new Vector();
                final WhereItem whereItem = new WhereItem();
                final WhereColumn leftWhereColumn = new WhereColumn();
                final Vector leftColExpr = new Vector();
                final TableColumn leftTableColumn = new TableColumn();
                leftTableColumn.setColumnName("TABLE_SCHEMA");
                leftColExpr.add(leftTableColumn);
                leftWhereColumn.setColumnExpression(leftColExpr);
                final WhereColumn rightWhereColumn = new WhereColumn();
                final Vector rightColExpr = new Vector();
                rightColExpr.add("'public'");
                rightWhereColumn.setColumnExpression(rightColExpr);
                whereItem.setLeftWhereExp(leftWhereColumn);
                whereItem.setRightWhereExp(rightWhereColumn);
                whereItem.setOperator("=");
                final Vector whereItemVector = new Vector();
                whereItemVector.add(whereItem);
                whereExp.setWhereItem(whereItemVector);
                selectQueryStmt.setWhereExpression(whereExp);
            }
            else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
                selectColVector.add("*");
                isShowTables = false;
            }
            selectCol.setColumnExpression(selectColVector);
            selectItems.add(selectCol);
            selectStmt.setSelectItemList(selectItems);
            selectQueryStmt.setSelectStatement(selectStmt);
        }
        if (this.getFrom() != null && this.getFromItem() != null) {
            final FromClause fromClause2 = new FromClause();
            fromClause2.setFromClause("FROM");
            final Vector fromItemList2 = new Vector();
            fromItemList2.add("INFORMATION_SCHEMA.COLUMNS");
            fromClause2.setFromItemList(fromItemList2);
            selectQueryStmt.setFromClause(fromClause2);
            final WhereExpression whereExp2 = new WhereExpression();
            final Vector whereExpVec2 = new Vector();
            final WhereItem whereItem2 = new WhereItem();
            final WhereColumn leftWhereColumn2 = new WhereColumn();
            final Vector leftColExpr2 = new Vector();
            final TableColumn leftTableColumn2 = new TableColumn();
            leftTableColumn2.setColumnName("TABLE_NAME");
            leftColExpr2.add(leftTableColumn2);
            leftWhereColumn2.setColumnExpression(leftColExpr2);
            final WhereColumn rightWhereColumn2 = new WhereColumn();
            final Vector rightColExpr2 = new Vector();
            final FromTable fromItemTable = this.getFromItem();
            final Object fromItemTableName = fromItemTable.getTableName();
            if (fromItemTableName instanceof TableObject) {
                final TableObject fromItemTableObject = (TableObject)fromItemTableName;
                final String fromItemTableObjectName = fromItemTableObject.getTableName();
                rightColExpr2.add("'" + fromItemTableObjectName + "'");
                rightWhereColumn2.setColumnExpression(rightColExpr2);
            }
            else if (fromItemTableName instanceof String) {
                rightColExpr2.add("'" + fromItemTableName.toString() + "'");
                rightWhereColumn2.setColumnExpression(rightColExpr2);
            }
            whereItem2.setLeftWhereExp(leftWhereColumn2);
            whereItem2.setRightWhereExp(rightWhereColumn2);
            whereItem2.setOperator("=");
            final Vector whereItemVector2 = new Vector();
            whereItemVector2.add(whereItem2);
            whereExp2.setWhereItem(whereItemVector2);
            selectQueryStmt.setWhereExpression(whereExp2);
        }
        if (this.getLike() != null && this.getLikePattern() != null) {
            final WhereItem likeWhereItem = new WhereItem();
            final WhereColumn leftWhereColumn3 = new WhereColumn();
            final Vector leftColExpr3 = new Vector();
            final TableColumn leftTableColumn3 = new TableColumn();
            if (isShowTables) {
                leftTableColumn3.setColumnName("TABLE_NAME");
            }
            else {
                leftTableColumn3.setColumnName("COLUMN_NAME");
            }
            leftColExpr3.add(leftTableColumn3);
            leftWhereColumn3.setColumnExpression(leftColExpr3);
            final WhereColumn rightWhereColumn3 = new WhereColumn();
            final Vector rightColExpr3 = new Vector();
            rightColExpr3.add(this.getLikePattern());
            rightWhereColumn3.setColumnExpression(rightColExpr3);
            likeWhereItem.setLeftWhereExp(leftWhereColumn3);
            likeWhereItem.setRightWhereExp(rightWhereColumn3);
            likeWhereItem.setOperator("LIKE");
            final Vector whereItemVector3 = new Vector();
            whereItemVector3.add(likeWhereItem);
            if (selectQueryStmt.getWhereExpression() != null) {
                if (selectQueryStmt.getWhereExpression().getOperator() != null) {
                    selectQueryStmt.getWhereExpression().getOperator().add("AND");
                }
                selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
            }
            else {
                final WhereExpression whereExp3 = new WhereExpression();
                whereExp3.setWhereItem(whereItemVector3);
                selectQueryStmt.setWhereExpression(whereExp3);
            }
        }
        return selectQueryStmt;
    }
    
    public SwisSQLStatement toMySQLShow() throws ConvertException {
        final ShowStatement show = new ShowStatement();
        if (this.getShow() != null) {
            show.setShow("SHOW");
        }
        if (this.getSelectColumn() != null) {
            final String selectColStr = this.getSelectColumn();
            show.setSelectColumn(selectColStr);
        }
        if (this.getStatus() != null) {
            final String status = this.getStatus();
            show.setStatus(status);
        }
        if (this.getFrom() != null && this.getFromItem() != null) {
            show.setFrom("FROM");
            show.setFromItem(this.getFromItem());
        }
        if (this.getLike() != null && this.getLikePattern() != null) {
            show.setLike("LIKE");
            show.setLikePattern(this.getLikePattern());
        }
        return show;
    }
    
    public SwisSQLStatement toANSIShow() throws ConvertException {
        throw new ConvertException("Show statement yet to be supported");
    }
    
    public SwisSQLStatement toTeradataShow() throws ConvertException {
        throw new ConvertException("Show statement yet to be supported");
    }
    
    public SwisSQLStatement toInformixShow() throws ConvertException {
        final SelectQueryStatement selectQueryStmt = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        boolean isShowTables = false;
        if (this.getShow() != null) {
            selectStmt.setSelectClause("SELECT");
        }
        if (this.getSelectColumn() != null) {
            final String selectColStr = this.getSelectColumn();
            final Vector selectItems = new Vector();
            final SelectColumn selectCol = new SelectColumn();
            final Vector selectColVector = new Vector();
            if (this.selectColumnStr.equalsIgnoreCase("TABLES")) {
                selectColVector.add("tabname");
                isShowTables = true;
                final FromClause fromClause = new FromClause();
                fromClause.setFromClause("FROM");
                final Vector fromItemList = new Vector();
                fromItemList.add("systables");
                fromClause.setFromItemList(fromItemList);
                selectQueryStmt.setFromClause(fromClause);
                final WhereExpression whereExp = new WhereExpression();
                final Vector whereExpVec = new Vector();
                final WhereItem whereItem = new WhereItem();
                final WhereColumn leftWhereColumn = new WhereColumn();
                final Vector leftColExpr = new Vector();
                final TableColumn leftTableColumn = new TableColumn();
                leftTableColumn.setColumnName("tabid");
                leftColExpr.add(leftTableColumn);
                leftWhereColumn.setColumnExpression(leftColExpr);
                final WhereColumn rightWhereColumn = new WhereColumn();
                final Vector rightColExpr = new Vector();
                rightColExpr.add("99");
                rightWhereColumn.setColumnExpression(rightColExpr);
                whereItem.setLeftWhereExp(leftWhereColumn);
                whereItem.setRightWhereExp(rightWhereColumn);
                whereItem.setOperator(">");
                final Vector whereItemVector = new Vector();
                whereItemVector.add(whereItem);
                whereExp.setWhereItem(whereItemVector);
                selectQueryStmt.setWhereExpression(whereExp);
            }
            else if (this.selectColumnStr.equalsIgnoreCase("FIELDS") || this.selectColumnStr.equalsIgnoreCase("COLUMNS")) {
                selectColVector.add("syscolumns.*");
                isShowTables = false;
            }
            selectCol.setColumnExpression(selectColVector);
            selectItems.add(selectCol);
            selectStmt.setSelectItemList(selectItems);
            selectQueryStmt.setSelectStatement(selectStmt);
        }
        if (this.getFrom() != null && this.getFromItem() != null) {
            final FromClause fromClause2 = new FromClause();
            fromClause2.setFromClause("FROM");
            final Vector fromItemList2 = new Vector();
            fromItemList2.add("syscolumns");
            fromItemList2.add("systables");
            fromClause2.setFromItemList(fromItemList2);
            selectQueryStmt.setFromClause(fromClause2);
            final WhereExpression whereExp2 = new WhereExpression();
            final Vector whereExpVec2 = new Vector();
            final WhereItem whereItem2 = new WhereItem();
            final WhereColumn leftWhereColumn2 = new WhereColumn();
            final Vector leftColExpr2 = new Vector();
            final TableColumn leftTableColumn2 = new TableColumn();
            leftTableColumn2.setColumnName("systables.tabid");
            leftColExpr2.add(leftTableColumn2);
            leftWhereColumn2.setColumnExpression(leftColExpr2);
            final WhereColumn rightWhereColumn2 = new WhereColumn();
            final Vector rightColExpr2 = new Vector();
            rightColExpr2.add("syscolumns.tabid");
            rightWhereColumn2.setColumnExpression(rightColExpr2);
            whereItem2.setLeftWhereExp(leftWhereColumn2);
            whereItem2.setRightWhereExp(rightWhereColumn2);
            whereItem2.setOperator("=");
            final WhereItem whereItem3 = new WhereItem();
            final WhereColumn leftWhereColumn3 = new WhereColumn();
            final Vector leftColExpr3 = new Vector();
            final TableColumn leftTableColumn3 = new TableColumn();
            leftTableColumn3.setColumnName("systables.tabname");
            leftColExpr3.add(leftTableColumn3);
            leftWhereColumn3.setColumnExpression(leftColExpr3);
            final WhereColumn rightWhereColumn3 = new WhereColumn();
            final Vector rightColExpr3 = new Vector();
            final FromTable fromItemTable = this.getFromItem();
            final Object fromItemTableName = fromItemTable.getTableName();
            if (fromItemTableName instanceof TableObject) {
                final TableObject fromItemTableObject = (TableObject)fromItemTableName;
                final String fromItemTableObjectName = fromItemTableObject.getTableName();
                rightColExpr3.add("'" + fromItemTableObjectName + "'");
                rightWhereColumn3.setColumnExpression(rightColExpr3);
            }
            else if (fromItemTableName instanceof String) {
                rightColExpr3.add("'" + fromItemTableName.toString() + "'");
                rightWhereColumn3.setColumnExpression(rightColExpr3);
            }
            whereItem3.setLeftWhereExp(leftWhereColumn3);
            whereItem3.setRightWhereExp(rightWhereColumn3);
            whereItem3.setOperator("=");
            final Vector whereItemVector2 = new Vector();
            whereItemVector2.add(whereItem2);
            whereItemVector2.add(whereItem3);
            whereExp2.setWhereItem(whereItemVector2);
            final Vector whereExpOperatorVec = new Vector();
            whereExpOperatorVec.add("AND");
            whereExp2.setOperator(whereExpOperatorVec);
            selectQueryStmt.setWhereExpression(whereExp2);
        }
        if (this.getLike() != null && this.getLikePattern() != null) {
            final WhereItem likeWhereItem = new WhereItem();
            final WhereColumn leftWhereColumn4 = new WhereColumn();
            final Vector leftColExpr4 = new Vector();
            final TableColumn leftTableColumn4 = new TableColumn();
            if (isShowTables) {
                leftTableColumn4.setColumnName("tabname");
            }
            else {
                leftTableColumn4.setColumnName("syscolumns.colname");
            }
            leftColExpr4.add(leftTableColumn4);
            leftWhereColumn4.setColumnExpression(leftColExpr4);
            final WhereColumn rightWhereColumn4 = new WhereColumn();
            final Vector rightColExpr4 = new Vector();
            rightColExpr4.add(this.getLikePattern());
            rightWhereColumn4.setColumnExpression(rightColExpr4);
            likeWhereItem.setLeftWhereExp(leftWhereColumn4);
            likeWhereItem.setRightWhereExp(rightWhereColumn4);
            likeWhereItem.setOperator("LIKE");
            final Vector whereItemVector3 = new Vector();
            whereItemVector3.add(likeWhereItem);
            if (selectQueryStmt.getWhereExpression() != null) {
                if (selectQueryStmt.getWhereExpression().getOperator() != null) {
                    selectQueryStmt.getWhereExpression().getOperator().add("AND");
                }
                selectQueryStmt.getWhereExpression().getWhereItem().add(likeWhereItem);
            }
            else {
                final WhereExpression whereExp3 = new WhereExpression();
                whereExp3.setWhereItem(whereItemVector3);
                selectQueryStmt.setWhereExpression(whereExp3);
            }
        }
        return selectQueryStmt;
    }
    
    public SwisSQLStatement toTimesTenShow() throws ConvertException {
        throw new ConvertException("Show statement yet to be supported");
    }
    
    public SwisSQLStatement toNetezzaShow() throws ConvertException {
        throw new ConvertException("Show statement yet to be supported");
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.getShow() != null) {
            sb.append("SHOW ");
        }
        if (this.getSelectColumn() != null) {
            final String selectColStr = this.getSelectColumn();
            sb.append(selectColStr + " ");
        }
        if (this.getStatus() != null) {
            sb.append(this.getStatus() + " ");
        }
        if (this.getFrom() != null && this.getFromItem() != null) {
            sb.append("FROM ");
            sb.append(this.getFromItem().toString() + " ");
        }
        if (this.getLike() != null && this.getLikePattern() != null) {
            sb.append("LIKE ");
            sb.append(this.getLikePattern() + " ");
        }
        return sb.toString();
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
