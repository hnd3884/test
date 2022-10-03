package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class PivotClause
{
    private String pivotStr;
    private TableColumn pivotColumn;
    private String forStr;
    private WhereItem inClause;
    private String openBrace;
    private String closedBrace;
    private FunctionCalls aggregateFunction;
    private String asStr;
    private String aliasName;
    private boolean isAS;
    private SelectQueryStatement subQuery;
    private FunctionCalls newFc;
    
    public void setAliasName(final String an) {
        this.aliasName = an;
    }
    
    public void setIsAs(final boolean is) {
        this.isAS = is;
    }
    
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setClosedBrace(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    public void setAggregateFunction(final FunctionCalls fc) {
        this.aggregateFunction = fc;
    }
    
    public void setPivot(final String pivotStr) {
        this.pivotStr = pivotStr;
    }
    
    public void setPivotColumn(final TableColumn tc) {
        this.pivotColumn = tc;
    }
    
    public void setForStr(final String forStr) {
        this.forStr = forStr;
    }
    
    public void setInClause(final WhereItem wi) {
        this.inClause = wi;
    }
    
    public String getAliasName() {
        return this.aliasName;
    }
    
    public boolean getIsAs() {
        return this.isAS;
    }
    
    public String getOpenBrace() {
        return this.openBrace;
    }
    
    public String getClosedBrace() {
        return this.closedBrace;
    }
    
    public FunctionCalls getAggregateFunction() {
        return this.aggregateFunction;
    }
    
    public String getPivot() {
        return this.pivotStr;
    }
    
    public TableColumn getPivotColumn() {
        return this.pivotColumn;
    }
    
    public String getForStr() {
        return this.forStr;
    }
    
    public WhereItem getInClause() {
        return this.inClause;
    }
    
    public void setSubQuery(final SelectQueryStatement selectQueryStatement) {
        this.subQuery = selectQueryStatement;
    }
    
    public void toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toOracleSelect(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toOracleSelect(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toOracleSelect(to_sqs, from_sqs));
    }
    
    public void toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    public void toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toDB2Select(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toDB2Select(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toDB2Select(to_sqs, from_sqs));
    }
    
    public void toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toInformixSelect(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toInformixSelect(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toInformixSelect(to_sqs, from_sqs));
    }
    
    public void toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toPostgreSQLSelect(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toPostgreSQLSelect(to_sqs, from_sqs));
    }
    
    public void toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toMySQLSelect(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toMySQLSelect(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toMySQLSelect(to_sqs, from_sqs));
    }
    
    public void toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toANSISelect(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toANSISelect(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toANSISelect(to_sqs, from_sqs));
    }
    
    public void toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toSybaseSelect(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toSybaseSelect(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toSybaseSelect(to_sqs, from_sqs));
    }
    
    public void toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toNetezzaSelect(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toNetezzaSelect(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toNetezzaSelect(to_sqs, from_sqs));
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.pivotStr != null) {
            sb.append("\n" + this.pivotStr + "\n");
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.aggregateFunction != null) {
            sb.append(this.aggregateFunction.toString() + " ");
        }
        if (this.forStr != null) {
            sb.append(this.forStr + " ");
        }
        if (this.pivotColumn != null) {
            sb.append(this.pivotColumn.toString() + " ");
        }
        if (this.inClause != null) {
            sb.append(this.inClause.toString() + "\n");
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace);
        }
        if (this.isAS) {
            sb.append(" AS ");
        }
        if (this.aliasName != null) {
            sb.append(this.aliasName);
        }
        return sb.toString();
    }
    
    private ArrayList getInClauseColumns() {
        final ArrayList inClauseColumns = new ArrayList();
        final Vector whereColumns = this.inClause.getRightWhereExp().getColumnExpression();
        for (int i = 0; i < whereColumns.size(); ++i) {
            if (whereColumns.get(i) instanceof WhereColumn) {
                final WhereColumn wc = whereColumns.get(i);
                final Vector pivotColumns = wc.getColumnExpression();
                final TableColumn tc = pivotColumns.get(0);
                inClauseColumns.add(tc);
            }
        }
        return inClauseColumns;
    }
    
    private ArrayList getSelectColumns(final SelectQueryStatement from_sqs) {
        final ArrayList colList = new ArrayList();
        final SelectStatement st = from_sqs.getSelectStatement();
        final Vector selectItemsList = st.getSelectItemList();
        for (int i = 0; i < selectItemsList.size(); ++i) {
            if (selectItemsList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = selectItemsList.get(i);
                colList.add(sc);
            }
        }
        return colList;
    }
    
    private Vector getNewSelectItems(final ArrayList inClauseColumns, final ArrayList selectItems, final ArrayList subQuerySelectItems) {
        final Vector newSelectItems = new Vector();
        String alias = null;
        final String cilumnName = null;
        for (int i = 0; i < selectItems.size(); ++i) {
            final SelectColumn sc = selectItems.get(i);
            alias = sc.getAliasName();
            final Vector colExp = sc.getColumnExpression();
            if (colExp.get(0) instanceof String) {
                newSelectItems.add(sc);
            }
            else if (colExp.get(0) instanceof TableColumn) {
                final SelectColumn scTemp = this.generateNewSelectColumn(sc, subQuerySelectItems, inClauseColumns);
                this.setaliasForSelectColumn(scTemp, sc);
                newSelectItems.add(scTemp);
            }
        }
        return newSelectItems;
    }
    
    private SelectColumn generateNewSelectColumn(final SelectColumn sc, final ArrayList subQuerySelectItems, final ArrayList inClauseColumns) {
        final SelectColumn newSc = new SelectColumn();
        newSc.setAliasName(sc.getAliasName());
        newSc.setIsAS(sc.getIsAS());
        newSc.setEndsWith(sc.getEndsWith());
        final Vector colExp = sc.getColumnExpression();
        final Vector newColExp = new Vector();
        for (int i = 0; i < colExp.size(); ++i) {
            final Object obj = colExp.get(i);
            if (obj instanceof TableColumn) {
                final TableColumn tc = (TableColumn)obj;
                final boolean presentInSubQuery = this.changeColumnIfPresentInSubQueryItemsList(newColExp, subQuerySelectItems, tc);
                if (!presentInSubQuery) {
                    this.changeColumnIfPresentInInClause(newColExp, inClauseColumns, tc);
                }
            }
            else {
                newColExp.add(obj);
            }
        }
        newSc.setColumnExpression(newColExp);
        return newSc;
    }
    
    private boolean changeColumnIfPresentInSubQueryItemsList(final Vector cols, final ArrayList subQuerySelectItems, final TableColumn tc) {
        String colName = tc.getColumnName().trim();
        for (int i = 0; i < subQuerySelectItems.size(); ++i) {
            final SelectColumn scTemp = subQuerySelectItems.get(i);
            String aliasName = scTemp.getAliasName();
            if (aliasName != null && (aliasName.startsWith("\"") || aliasName.startsWith("[") || aliasName.startsWith("`"))) {
                aliasName = aliasName.substring(1, aliasName.length() - 1);
            }
            if (colName.startsWith("\"") || colName.startsWith("[") || colName.startsWith("`")) {
                colName = colName.substring(1, colName.length() - 1);
            }
            if (colName.equalsIgnoreCase(aliasName)) {
                cols.add(scTemp.getColumnExpression().get(0));
                return true;
            }
        }
        return false;
    }
    
    private void changeColumnIfPresentInInClause(final Vector newColExp, final ArrayList inClauseColumns, final TableColumn tc) {
        String colName = tc.getColumnName();
        if (colName.startsWith("\"") || colName.startsWith("[") || colName.startsWith("`")) {
            colName = colName.substring(1, colName.length() - 1).trim();
        }
        for (int j = 0; j < inClauseColumns.size(); ++j) {
            final TableColumn tcNew = inClauseColumns.get(j);
            String inColName = tcNew.getColumnName();
            if (inColName.startsWith("\"") || inColName.startsWith("[") || inColName.startsWith("`")) {
                inColName = inColName.substring(1, inColName.length() - 1).trim();
            }
            if (colName.equalsIgnoreCase(inColName)) {
                this.generateCaseStatementWithPivotedColumn(newColExp, inColName);
            }
        }
    }
    
    private void generateCaseStatementWithPivotedColumn(final Vector newColExp, final String inColName) {
        final FunctionCalls fc = new FunctionCalls();
        final Vector functionargs = new Vector();
        final CaseStatement caseStmt = new CaseStatement();
        final SelectColumn sc = this.newFc.getFunctionArguments().get(0);
        final Vector whenStmtList = new Vector();
        final WhenStatement whenStmt = new WhenStatement();
        final WhereExpression whenConditionWE = new WhereExpression();
        final Vector whenConditionVector = new Vector();
        final WhereItem whenWhereItem = new WhereItem();
        final WhereColumn whenLeftWC = new WhereColumn();
        final Vector whenColExp = new Vector();
        final WhereColumn rightWhereExp = new WhereColumn();
        final Vector rightWhereColExp = new Vector();
        final SelectColumn thenStmt = new SelectColumn();
        final Vector thenColExp = new Vector();
        final SelectColumn elseStmt = new SelectColumn();
        final Vector elseColExp = new Vector();
        final String elseExpValue = "0";
        caseStmt.setCaseClause("CASE");
        whenStmt.setWhenClause("WHEN");
        whenStmt.setThenClause("THEN");
        caseStmt.setEndClause("END");
        whenColExp.add(this.pivotColumn);
        whenLeftWC.setColumnExpression(whenColExp);
        whenWhereItem.setLeftWhereExp(whenLeftWC);
        whenConditionVector.add(whenWhereItem);
        whenConditionWE.setWhereItem(whenConditionVector);
        whenStmt.setWhenCondition(whenConditionWE);
        rightWhereColExp.add("'" + inColName + "'");
        rightWhereExp.setColumnExpression(rightWhereColExp);
        whenWhereItem.setRightWhereExp(rightWhereExp);
        whenWhereItem.setOperator("=");
        thenColExp.add(sc);
        thenStmt.setColumnExpression(thenColExp);
        whenStmt.setThenStatement(thenStmt);
        whenStmtList.add(whenStmt);
        elseColExp.add(elseExpValue);
        elseStmt.setColumnExpression(elseColExp);
        caseStmt.setWhenStatementList(whenStmtList);
        final SelectColumn argument = new SelectColumn();
        final Vector colExp = new Vector();
        colExp.add(caseStmt);
        argument.setColumnExpression(colExp);
        final Vector arguments = new Vector();
        arguments.add(argument);
        fc.setFunctionArguments(arguments);
        fc.setFunctionName(this.getAggregateFunction().getFunctionName());
        newColExp.add(fc);
    }
    
    private void setaliasForSelectColumn(final SelectColumn scTemp, final SelectColumn originalSC) {
        final String alias = scTemp.getAliasName();
        if (alias == null || alias.equals("")) {
            final Vector colExp = originalSC.getColumnExpression();
            final TableColumn tc = colExp.get(0);
            scTemp.setAliasName(tc.getColumnName().trim());
        }
    }
    
    private GroupByStatement generateGroupByStatement(final ArrayList subQuerySelectItems) {
        final GroupByStatement gbs = new GroupByStatement();
        final Vector functionArgs = this.getAggregateFunction().getFunctionArguments();
        final String pivotedColumn = this.getPivotColumn().getColumnName().trim();
        final String fArg = this.extractColumnNameFromSelectColumn(functionArgs.get(0));
        final Vector groupByList = new Vector();
        for (int i = 0; i < subQuerySelectItems.size(); ++i) {
            if (subQuerySelectItems.get(i) instanceof SelectColumn) {
                final SelectColumn scTemp = subQuerySelectItems.get(i);
                final String colName = this.extractColumnNameFromSelectColumn(scTemp).trim();
                if (!colName.equalsIgnoreCase(fArg) && !colName.equalsIgnoreCase(pivotedColumn)) {
                    final SelectColumn newSC = new SelectColumn();
                    newSC.setColumnExpression(scTemp.getColumnExpression());
                    groupByList.add(newSC);
                }
            }
        }
        gbs.setGroupClause("GROUP BY ");
        if (groupByList.size() > 0) {
            gbs.setGroupByItemList(groupByList);
            return gbs;
        }
        return null;
    }
    
    private String extractColumnNameFromSelectColumn(final SelectColumn sc) {
        String colName = "";
        final Vector colExp = sc.getColumnExpression();
        if (colExp.get(0) instanceof TableColumn) {
            final TableColumn tc = colExp.get(0);
            colName = tc.getColumnName().trim();
            if (colName.startsWith("\"") || colName.startsWith("[") || colName.startsWith("`")) {
                colName = colName.substring(1, colName.length() - 1);
            }
        }
        return colName;
    }
    
    public void toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.newFc = this.getAggregateFunction().toVectorWiseSelect(to_sqs, from_sqs);
        ArrayList inClauseColumns = new ArrayList();
        ArrayList selectItems = new ArrayList();
        ArrayList subQuerySelectItems = new ArrayList();
        Vector newSelectItemList = new Vector();
        final SelectStatement newST = new SelectStatement();
        newST.setSelectClause("SELECT");
        inClauseColumns = this.getInClauseColumns();
        selectItems = this.getSelectColumns(from_sqs);
        subQuerySelectItems = this.getSelectColumns(this.subQuery);
        newSelectItemList = this.getNewSelectItems(inClauseColumns, selectItems, subQuerySelectItems);
        newST.setSelectItemList(newSelectItemList);
        final GroupByStatement gbs = this.generateGroupByStatement(subQuerySelectItems);
        if (gbs != null) {
            to_sqs.setGroupByStatement(gbs.toVectorWiseSelect(to_sqs, from_sqs));
        }
        to_sqs.setSelectStatement(newST.toVectorWiseSelect(to_sqs, from_sqs));
    }
}
