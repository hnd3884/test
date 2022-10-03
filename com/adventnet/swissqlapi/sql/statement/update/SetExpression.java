package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.ArrayList;

public class SetExpression
{
    private ArrayList columnList;
    private String equalto;
    private ArrayList expressionList;
    private SelectQueryStatement subQuery;
    private UserObjectContext context;
    private int setExpressionId;
    
    public SetExpression() {
        this.context = null;
        this.columnList = new ArrayList();
        this.expressionList = new ArrayList();
        this.equalto = new String();
    }
    
    public void setColumnList(final ArrayList list) {
        this.columnList = list;
    }
    
    public ArrayList getColumnList() {
        return this.columnList;
    }
    
    public void setEqualTo(final String s) {
        this.equalto = s;
    }
    
    public String getEqualTo() {
        return this.equalto;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setSubQuery(final SelectQueryStatement s) {
        this.subQuery = s;
    }
    
    public SelectQueryStatement getSubQuery() {
        return this.subQuery;
    }
    
    public void setExpressionList(final ArrayList list) {
        this.expressionList = list;
    }
    
    public ArrayList getExpressionList() {
        return this.expressionList;
    }
    
    public void setSetExpressionId(final int exprId) {
        this.setExpressionId = exprId;
    }
    
    public int getSetExpressionId() {
        return this.setExpressionId;
    }
    
    public void toMySQL() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            this.subQuery = this.subQuery.toMySQLSelect();
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                final Object obj = this.expressionList.get(i);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toMySQLSelect();
                    this.expressionList.set(i, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toMySQLSelect(null, null);
                    this.expressionList.set(i, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int i = 0, size = this.columnList.size(); i < size; ++i) {
                final Object obj = this.columnList.get(i);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toMySQLSelect(null, null);
                    this.columnList.set(i, tc);
                }
            }
        }
    }
    
    public void toOracle() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            this.subQuery = this.subQuery.toOracleSelect();
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                final Object obj = this.expressionList.get(i);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toOracleSelect();
                    this.expressionList.set(i, sqs);
                }
                else if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toOracleSelect(null, null);
                    this.expressionList.set(i, tc);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toOracleSelect(null, null);
                    this.expressionList.set(i, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int i = 0, size = this.columnList.size(); i < size; ++i) {
                final Object obj = this.columnList.get(i);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toOracleSelect(null, null);
                    this.columnList.set(i, tc);
                }
            }
        }
    }
    
    public void toMSSQLServer() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            boolean subqueryHasAggregateFunction = false;
            final SelectStatement subSelectStatement = this.subQuery.getSelectStatement();
            final Vector subSelectCol = subSelectStatement.getSelectItemList();
            if (subSelectCol != null) {
                for (int i = 0; i < subSelectCol.size(); ++i) {
                    if (subSelectCol.get(i) instanceof SelectColumn && (subSelectCol.get(i).isAggregateFunction() || this.selectColumnHasAggrFunction(subSelectCol.get(i).getColumnExpression(), false))) {
                        subqueryHasAggregateFunction = true;
                    }
                }
            }
            if (!subqueryHasAggregateFunction) {
                this.newConversionForRamco();
                return;
            }
            this.subQuery = this.subQuery.toMSSQLServerSelect();
            final ArrayList newExpressionList = new ArrayList();
            final SelectStatement selectStatement = this.subQuery.getSelectStatement();
            final Vector selectList = selectStatement.getSelectItemList();
            if (selectList != null) {
                final ArrayList arrayList = new ArrayList();
                for (int j = 0; j < selectList.size(); ++j) {
                    final SelectColumn newSelectColumn = new SelectColumn();
                    if (selectList.get(j) instanceof SelectColumn) {
                        final SelectColumn oldSelectColumn = selectList.get(j);
                        final Vector colExp = oldSelectColumn.getColumnExpression();
                        final Vector newColExp = new Vector();
                        if (colExp != null) {
                            for (int k = 0; k < colExp.size(); ++k) {
                                newColExp.add(colExp.get(k).toString());
                            }
                        }
                        newSelectColumn.setColumnExpression(newColExp);
                    }
                    final Vector selectItem = new Vector();
                    selectItem.add(newSelectColumn);
                    final SelectQueryStatement eachColumnStmt = new SelectQueryStatement();
                    final SelectStatement newSelectStatement = new SelectStatement();
                    newSelectStatement.setDistinctList(selectStatement.getDistinctList());
                    newSelectStatement.setSelectClause(selectStatement.getSelectClause());
                    newSelectStatement.setSelectQualifier(selectStatement.getSelectQualifier());
                    newSelectStatement.setSelectRowCount(selectStatement.getSelectRowCount());
                    newSelectStatement.setSelectRowSpecifier(selectStatement.getSelectRowSpecifier());
                    newSelectStatement.setSelectSpecialQualifier(selectStatement.getSelectSpecialQualifier());
                    newSelectStatement.setSelectItemList(selectItem);
                    eachColumnStmt.setSelectStatement(newSelectStatement);
                    eachColumnStmt.setFromClause(this.subQuery.getFromClause());
                    eachColumnStmt.setWhereExpression(this.subQuery.getWhereExpression());
                    eachColumnStmt.setGroupByStatement(this.subQuery.getGroupByStatement());
                    eachColumnStmt.setForUpdateStatement(this.subQuery.getForUpdateStatement());
                    eachColumnStmt.setIntoStatement(this.subQuery.getIntoStatement());
                    eachColumnStmt.setSetOperatorClause(this.subQuery.getSetOperatorClause());
                    eachColumnStmt.setHavingStatement(this.subQuery.getHavingStatement());
                    if (selectList.size() > 1) {
                        newExpressionList.add("");
                    }
                    String queryString = eachColumnStmt.toMSSQLServerString();
                    queryString = StringFunctions.replaceAll("\n\t\t\t\t", "\n", queryString);
                    newExpressionList.add("\n\t\t\t\t(\n\t\t\t\t" + queryString + ")" + "\n\t");
                }
            }
            this.setSubQuery(null);
            this.setExpressionList(newExpressionList);
        }
        else {
            for (int l = 0, size = this.expressionList.size(); l < size; ++l) {
                final Object obj = this.expressionList.get(l);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toMSSQLServerSelect();
                    this.expressionList.set(l, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toMSSQLServerSelect(null, null);
                    this.expressionList.set(l, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int l = 0, size = this.columnList.size(); l < size; ++l) {
                final Object obj = this.columnList.get(l);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toMSSQLServerSelect(null, null);
                    this.columnList.set(l, tc);
                }
            }
        }
    }
    
    public void toSybase() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            boolean subqueryHasAggregateFunction = false;
            final SelectStatement subSelectStatement = this.subQuery.getSelectStatement();
            final Vector subSelectCol = subSelectStatement.getSelectItemList();
            if (subSelectCol != null) {
                for (int i = 0; i < subSelectCol.size(); ++i) {
                    if (subSelectCol.get(i) instanceof SelectColumn && subSelectCol.get(i).isAggregateFunction()) {
                        subqueryHasAggregateFunction = true;
                    }
                }
            }
            if (!subqueryHasAggregateFunction) {
                this.newConversionForRamco();
                return;
            }
            this.subQuery = this.subQuery.toSybaseSelect();
            final ArrayList newExpressionList = new ArrayList();
            final SelectStatement selectStatement = this.subQuery.getSelectStatement();
            final Vector selectList = selectStatement.getSelectItemList();
            if (selectList != null) {
                final ArrayList arrayList = new ArrayList();
                for (int j = 0; j < selectList.size(); ++j) {
                    final SelectColumn newSelectColumn = new SelectColumn();
                    if (selectList.get(j) instanceof SelectColumn) {
                        final SelectColumn oldSelectColumn = selectList.get(j);
                        final Vector colExp = oldSelectColumn.getColumnExpression();
                        final Vector newColExp = new Vector();
                        if (colExp != null) {
                            for (int k = 0; k < colExp.size(); ++k) {
                                newColExp.add(colExp.get(k).toString());
                            }
                        }
                        newSelectColumn.setColumnExpression(newColExp);
                    }
                    final Vector selectItem = new Vector();
                    selectItem.add(newSelectColumn);
                    final SelectQueryStatement eachColumnStmt = new SelectQueryStatement();
                    final SelectStatement newSelectStatement = new SelectStatement();
                    newSelectStatement.setDistinctList(selectStatement.getDistinctList());
                    newSelectStatement.setSelectClause(selectStatement.getSelectClause());
                    newSelectStatement.setSelectQualifier(selectStatement.getSelectQualifier());
                    newSelectStatement.setSelectRowCount(selectStatement.getSelectRowCount());
                    newSelectStatement.setSelectRowSpecifier(selectStatement.getSelectRowSpecifier());
                    newSelectStatement.setSelectSpecialQualifier(selectStatement.getSelectSpecialQualifier());
                    newSelectStatement.setSelectItemList(selectItem);
                    eachColumnStmt.setSelectStatement(newSelectStatement);
                    eachColumnStmt.setFromClause(this.subQuery.getFromClause());
                    eachColumnStmt.setWhereExpression(this.subQuery.getWhereExpression());
                    eachColumnStmt.setGroupByStatement(this.subQuery.getGroupByStatement());
                    eachColumnStmt.setForUpdateStatement(this.subQuery.getForUpdateStatement());
                    eachColumnStmt.setIntoStatement(this.subQuery.getIntoStatement());
                    eachColumnStmt.setSetOperatorClause(this.subQuery.getSetOperatorClause());
                    eachColumnStmt.setHavingStatement(this.subQuery.getHavingStatement());
                    if (selectList.size() > 1) {
                        newExpressionList.add("");
                    }
                    String queryString = eachColumnStmt.toSybaseString();
                    queryString = StringFunctions.replaceAll("\n\t\t\t\t", "\n", queryString);
                    newExpressionList.add("\n\t\t\t\t(\n\t\t\t\t" + queryString + ")" + "\n\t");
                }
            }
            this.setSubQuery(null);
            this.setExpressionList(newExpressionList);
        }
        else {
            for (int l = 0, size = this.expressionList.size(); l < size; ++l) {
                final Object obj = this.expressionList.get(l);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs.setObjectContext(this.context);
                    sqs = sqs.toSybaseSelect();
                    this.expressionList.set(l, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc.setObjectContext(this.context);
                    sc = sc.toSybaseSelect(null, null);
                    this.expressionList.set(l, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int l = 0, size = this.columnList.size(); l < size; ++l) {
                final Object obj = this.columnList.get(l);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toSybaseSelect(null, null);
                    this.columnList.set(l, tc);
                }
            }
        }
    }
    
    public void toPostgreSQL() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            this.subQuery = this.subQuery.toPostgreSQLSelect();
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                final Object obj = this.expressionList.get(i);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toPostgreSQLSelect();
                    this.expressionList.set(i, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toPostgreSQLSelect(null, null);
                    this.expressionList.set(i, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int i = 0, size = this.columnList.size(); i < size; ++i) {
                final Object obj = this.columnList.get(i);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toPostgreSQLSelect(null, null);
                    this.columnList.set(i, tc);
                }
            }
        }
    }
    
    public void toDB2() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            this.subQuery = this.subQuery.toDB2Select();
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                final Object obj = this.expressionList.get(i);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toDB2Select();
                    this.expressionList.set(i, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc.setSelectColFromUQSSetExpression(true);
                    sc = sc.toDB2Select(null, null);
                    this.expressionList.set(i, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int i = 0, size = this.columnList.size(); i < size; ++i) {
                final Object obj = this.columnList.get(i);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toDB2Select(null, null);
                    this.columnList.set(i, tc);
                }
            }
        }
    }
    
    public void toInformix() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            this.subQuery = this.subQuery.toInformixSelect();
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                final Object obj = this.expressionList.get(i);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toInformixSelect();
                    this.expressionList.set(i, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toInformixSelect(null, null);
                    this.expressionList.set(i, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int i = 0, size = this.columnList.size(); i < size; ++i) {
                final Object obj = this.columnList.get(i);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toInformixSelect(null, null);
                    this.columnList.set(i, tc);
                }
            }
        }
    }
    
    public void toANSISQL() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            this.subQuery = this.subQuery.toANSISelect();
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                final Object obj = this.expressionList.get(i);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toANSISelect();
                    this.expressionList.set(i, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toANSISelect(null, null);
                    this.expressionList.set(i, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int i = 0, size = this.columnList.size(); i < size; ++i) {
                final Object obj = this.columnList.get(i);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toANSISelect(null, null);
                    this.columnList.set(i, tc);
                }
            }
        }
    }
    
    public void toTeradata() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            this.subQuery = this.subQuery.toTeradataSelect();
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                final Object obj = this.expressionList.get(i);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toTeradataSelect();
                    this.expressionList.set(i, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toTeradataSelect(null, null);
                    this.expressionList.set(i, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int i = 0, size = this.columnList.size(); i < size; ++i) {
                final Object obj = this.columnList.get(i);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toTeradataSelect(null, null);
                    this.columnList.set(i, tc);
                }
            }
        }
    }
    
    public void toTimesTen() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            this.subQuery = this.subQuery.toTimesTenSelect();
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                final Object obj = this.expressionList.get(i);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toTimesTenSelect();
                    this.expressionList.set(i, sqs);
                }
                else if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toTimesTenSelect(null, null);
                    this.expressionList.set(i, tc);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toTimesTenSelect(null, null);
                    this.expressionList.set(i, sc);
                }
            }
            if (this.columnList != null) {
                for (int i = 0, size = this.columnList.size(); i < size; ++i) {
                    final Object obj = this.columnList.get(i);
                    if (obj instanceof TableColumn) {
                        TableColumn tc = (TableColumn)obj;
                        tc = tc.toTimesTenSelect(null, null);
                        this.columnList.set(i, tc);
                    }
                }
            }
        }
    }
    
    public void toNetezzaSQL() throws ConvertException {
        if (this.expressionList == null && this.subQuery != null) {
            boolean subqueryHasAggregateFunction = false;
            final SelectStatement subSelectStatement = this.subQuery.getSelectStatement();
            final Vector subSelectCol = subSelectStatement.getSelectItemList();
            if (subSelectCol != null) {
                for (int i = 0; i < subSelectCol.size(); ++i) {
                    if (subSelectCol.get(i) instanceof SelectColumn && (subSelectCol.get(i).isAggregateFunction() || this.selectColumnHasAggrFunction(subSelectCol.get(i).getColumnExpression(), false))) {
                        subqueryHasAggregateFunction = true;
                    }
                }
            }
            if (!subqueryHasAggregateFunction) {
                this.newConversionForRamco();
                return;
            }
            this.subQuery = this.subQuery.toNetezzaSelect();
            final ArrayList newExpressionList = new ArrayList();
            final SelectStatement selectStatement = this.subQuery.getSelectStatement();
            final Vector selectList = selectStatement.getSelectItemList();
            if (selectList != null) {
                final ArrayList arrayList = new ArrayList();
                for (int j = 0; j < selectList.size(); ++j) {
                    final SelectColumn newSelectColumn = new SelectColumn();
                    if (selectList.get(j) instanceof SelectColumn) {
                        final SelectColumn oldSelectColumn = selectList.get(j);
                        final Vector colExp = oldSelectColumn.getColumnExpression();
                        final Vector newColExp = new Vector();
                        if (colExp != null) {
                            for (int k = 0; k < colExp.size(); ++k) {
                                newColExp.add(colExp.get(k).toString());
                            }
                        }
                        newSelectColumn.setColumnExpression(newColExp);
                    }
                    final Vector selectItem = new Vector();
                    selectItem.add(newSelectColumn);
                    final SelectQueryStatement eachColumnStmt = new SelectQueryStatement();
                    final SelectStatement newSelectStatement = new SelectStatement();
                    newSelectStatement.setDistinctList(selectStatement.getDistinctList());
                    newSelectStatement.setSelectClause(selectStatement.getSelectClause());
                    newSelectStatement.setSelectQualifier(selectStatement.getSelectQualifier());
                    newSelectStatement.setSelectRowCount(selectStatement.getSelectRowCount());
                    newSelectStatement.setSelectRowSpecifier(selectStatement.getSelectRowSpecifier());
                    newSelectStatement.setSelectSpecialQualifier(selectStatement.getSelectSpecialQualifier());
                    newSelectStatement.setSelectItemList(selectItem);
                    eachColumnStmt.setSelectStatement(newSelectStatement);
                    eachColumnStmt.setFromClause(this.subQuery.getFromClause());
                    eachColumnStmt.setWhereExpression(this.subQuery.getWhereExpression());
                    eachColumnStmt.setGroupByStatement(this.subQuery.getGroupByStatement());
                    eachColumnStmt.setForUpdateStatement(this.subQuery.getForUpdateStatement());
                    eachColumnStmt.setIntoStatement(this.subQuery.getIntoStatement());
                    eachColumnStmt.setSetOperatorClause(this.subQuery.getSetOperatorClause());
                    eachColumnStmt.setHavingStatement(this.subQuery.getHavingStatement());
                    if (selectList.size() > 1) {
                        newExpressionList.add("");
                    }
                    String queryString = eachColumnStmt.toNetezzaString();
                    queryString = StringFunctions.replaceAll("\n\t\t\t\t", "\n", queryString);
                    newExpressionList.add("\n\t\t\t\t(\n\t\t\t\t" + queryString + ")" + "\n\t");
                }
            }
            this.setSubQuery(null);
            this.setExpressionList(newExpressionList);
        }
        else {
            for (int l = 0, size = this.expressionList.size(); l < size; ++l) {
                final Object obj = this.expressionList.get(l);
                if (obj instanceof SelectQueryStatement) {
                    SelectQueryStatement sqs = (SelectQueryStatement)obj;
                    sqs = sqs.toNetezzaSelect();
                    this.expressionList.set(l, sqs);
                }
                else if (obj instanceof SelectColumn) {
                    SelectColumn sc = (SelectColumn)obj;
                    sc = sc.toNetezzaSelect(null, null);
                    this.expressionList.set(l, sc);
                }
            }
        }
        if (this.columnList != null) {
            for (int l = 0, size = this.columnList.size(); l < size; ++l) {
                final Object obj = this.columnList.get(l);
                if (obj instanceof TableColumn) {
                    TableColumn tc = (TableColumn)obj;
                    tc = tc.toNetezzaSelect(null, null);
                    this.columnList.set(l, tc);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0, size = this.columnList.size(); i < size; ++i) {
            if (this.columnList.get(i) instanceof TableColumn) {
                this.columnList.get(i).setObjectContext(this.context);
            }
            sb.append(this.columnList.get(i).toString());
        }
        sb.append(" " + this.equalto + " ");
        if (this.expressionList == null) {
            for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                sb.append("\t");
            }
            sb.append("(");
            sb.append(this.subQuery.toString());
            sb.append(")");
        }
        else {
            for (int i = 0, size = this.expressionList.size(); i < size; ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append(this.expressionList.get(i).toString());
            }
        }
        return sb.toString();
    }
    
    public void newConversionForRamco() throws ConvertException {
        final ArrayList newExpressionList = new ArrayList();
        this.subQuery = this.subQuery.toMSSQLServerSelect();
        final SelectStatement selectStatement = this.subQuery.getSelectStatement();
        final Vector selectList = selectStatement.getSelectItemList();
        final FromClause fromClause = this.subQuery.getFromClause();
        final Vector fromList = fromClause.getFromItemList();
        final ArrayList commaRemovedColumnList = new ArrayList();
        for (int i = 0; i < this.columnList.size(); ++i) {
            if (this.columnList.get(i).toString().trim().equals(",") || this.columnList.get(i).toString().trim().equals("(") || this.columnList.get(i).toString().trim().equals(")")) {
                this.columnList.remove(i);
                --i;
            }
            else {
                commaRemovedColumnList.add(this.columnList.get(i));
            }
        }
        if (selectList.size() != commaRemovedColumnList.size()) {
            final String message = "ColumnList size does not match Select column size";
            throw new ConvertException(message);
        }
        for (int i = 0; i < commaRemovedColumnList.size(); ++i) {
            newExpressionList.add(commaRemovedColumnList.get(i));
            newExpressionList.add(" = ");
            final FromTable fromTableObject = fromList.get(0);
            String tableName = null;
            if (fromTableObject.getAliasName() != null) {
                tableName = fromTableObject.getAliasName();
            }
            else {
                tableName = fromTableObject.getTableName().toString();
            }
            final Object selectObj = selectList.get(i).getColumnExpression().get(0);
            if (selectObj instanceof TableColumn) {
                final TableColumn selectTableColumn = (TableColumn)selectObj;
                if (selectTableColumn.getTableName() == null && !selectTableColumn.getColumnName().equalsIgnoreCase("GETDATE()") && !selectTableColumn.getColumnName().equalsIgnoreCase("SYSTEM_USER") && !selectTableColumn.getColumnName().equalsIgnoreCase("CURRENT_TIMESTAMP") && !selectTableColumn.getColumnName().trim().startsWith("@")) {
                    selectTableColumn.setTableName(tableName);
                }
                newExpressionList.add(selectList.get(i));
            }
            else {
                newExpressionList.add(selectList.get(i));
            }
        }
        this.expressionList = newExpressionList;
        this.columnList = null;
    }
    
    private boolean selectColumnHasAggrFunction(final Vector colExp, final boolean inputVal) {
        boolean bool = inputVal;
        if (colExp != null) {
            for (int i = 0; i < colExp.size(); ++i) {
                if (colExp.get(i) instanceof SelectColumn) {
                    if (colExp.get(i).isAggregateFunction()) {
                        return true;
                    }
                    final Vector selColExp = colExp.get(i).getColumnExpression();
                    bool = this.selectColumnHasAggrFunction(selColExp, bool);
                }
                else if (colExp.get(i) instanceof FunctionCalls) {
                    final Vector FunctionArgs = colExp.get(i).getFunctionArguments();
                    bool = this.selectColumnHasAggrFunction(FunctionArgs, bool);
                }
            }
        }
        return bool;
    }
}
