package com.adventnet.swissqlapi.sql.functions.analytic;

import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.HavingStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderItem;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class DenseRank extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.getWithinGroup() != null) {
            if (this.functionArguments.size() > 1 || this.getOrderBy().getOrderItemList().size() > 1) {
                return;
            }
            String caseOper = "<";
            SelectColumn funcArg = this.functionArguments.firstElement();
            funcArg = funcArg.toTeradataSelect(to_sqs, from_sqs);
            OrderItem orderByArg = this.getOrderBy().getOrderItemList().firstElement();
            orderByArg = orderByArg.toTeradataSelect(to_sqs, from_sqs);
            final SelectColumn orderByCol = orderByArg.getOrderSpecifier();
            if (orderByArg.getOrder() != null && orderByArg.getOrder().equalsIgnoreCase("DESC")) {
                caseOper = ">";
            }
            final CaseStatement caseStmt = new CaseStatement();
            caseStmt.setCaseClause("CASE");
            final Vector whenStmtList = new Vector();
            final WhenStatement whenStmt = new WhenStatement();
            whenStmt.setWhenClause("WHEN");
            final WhereExpression whereExp = new WhereExpression();
            final WhereItem wi = new WhereItem();
            final WhereColumn lwe = new WhereColumn();
            final Vector lweExp = new Vector();
            lweExp.add(orderByCol);
            lwe.setColumnExpression(lweExp);
            wi.setLeftWhereExp(lwe);
            wi.setOperator(caseOper);
            final WhereColumn rwe = new WhereColumn();
            final Vector rweExp = new Vector();
            rweExp.add(funcArg);
            rwe.setColumnExpression(rweExp);
            wi.setRightWhereExp(rwe);
            final Vector whereItemList = new Vector();
            whereItemList.add(wi);
            whereExp.setWhereItem(whereItemList);
            whenStmt.setWhenCondition(whereExp);
            whenStmt.setThenClause("THEN");
            whenStmt.setThenStatement(orderByCol);
            whenStmtList.add(whenStmt);
            caseStmt.setWhenStatementList(whenStmtList);
            caseStmt.setElseClause("ELSE");
            final SelectColumn elseCol = new SelectColumn();
            final Vector elseColExp = new Vector();
            elseColExp.add("NULL");
            elseCol.setColumnExpression(elseColExp);
            caseStmt.setElseStatement(elseCol);
            caseStmt.setEndClause("END");
            final FunctionCalls countFunc = new FunctionCalls();
            final TableColumn countFuncName = new TableColumn();
            countFuncName.setColumnName("COUNT");
            countFunc.setFunctionName(countFuncName);
            countFunc.setArgumentQualifier("DISTINCT");
            final SelectColumn countFuncCol = new SelectColumn();
            final Vector countFuncArgExp = new Vector();
            countFuncArgExp.add(caseStmt);
            countFuncCol.setColumnExpression(countFuncArgExp);
            final Vector countFuncArgs = new Vector();
            countFuncArgs.add(countFuncCol);
            countFunc.setFunctionArguments(countFuncArgs);
            final SelectColumn newArg = new SelectColumn();
            final Vector newArgExp = new Vector();
            newArgExp.add("(");
            newArgExp.add(countFunc);
            newArgExp.add("+");
            newArgExp.add("1");
            newArg.setColumnExpression(newArgExp);
            this.setFunctionName(null);
            final Vector arguments = new Vector();
            arguments.add(newArg);
            this.setFunctionArguments(arguments);
            this.setOver(null);
            this.setOrderBy(null);
            this.setPartitionByClause(null);
            this.setWithinGroup(null);
        }
        else {
            from_sqs.setOlapFunctionPresent(true);
            final String alias = from_sqs.getFromClause().getLastElement().getAliasName();
            String idx = "" + from_sqs.getOlapDerivedTables().size();
            if (this.getPartitionByClause() != null) {
                final String partitionString = "denserank" + this.getPartitionByClause().toString();
                if (from_sqs.getOlapDerivedTables().containsKey(partitionString)) {
                    idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
                }
                from_sqs.addOlapDerivedTables(partitionString + this.obs, this.createTeradataDerivedTable(to_sqs, this, null, alias + idx));
            }
            else {
                if (from_sqs.getOlapDerivedTables().containsKey("denserank" + this.obs)) {
                    idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
                }
                from_sqs.addOlapDerivedTables("denserank" + this.obs, this.createTeradataDerivedTable(to_sqs, this, null, alias + idx));
            }
            final TableColumn newTabCol = new TableColumn();
            newTabCol.setTableName(alias + idx);
            newTabCol.setColumnName("rnk");
            this.setFunctionName(newTabCol);
            this.getFunctionArguments().clear();
            this.setOpenBracesForFunctionNameRequired(false);
            this.setPartitionByClause(null);
            this.setOver(null);
            this.setOrderBy(null);
        }
    }
    
    @Override
    public FromTable createTeradataDerivedTable(final SelectQueryStatement to_sqs, final FunctionCalls fnCall, final SelectColumn functionArgument, final String alias) throws ConvertException {
        final String fnName = fnCall.getFunctionName().getColumnName();
        final SelectQueryStatement derivedTable = new SelectQueryStatement();
        final SelectStatement selStmt = new SelectStatement();
        selStmt.setSelectClause("SELECT");
        final Vector newSelColList = new Vector();
        final Vector newWhereItemList = new Vector();
        final HavingStatement qualifyStmt = new HavingStatement();
        qualifyStmt.setHavingClause("QUALIFY");
        final WhereExpression qualifyExpression = new WhereExpression();
        final Vector qualifyItems = new Vector();
        final SelectColumn rownumCol = this.getNewSelectColumnForDerivedTable(fnCall, 0);
        newSelColList.add(rownumCol);
        final WhereItem wi = new WhereItem();
        final WhereColumn lwc = new WhereColumn();
        final Vector lwcColExp = new Vector();
        final TableColumn tc1 = new TableColumn();
        tc1.setColumnName(rownumCol.getAliasName());
        lwcColExp.add(tc1);
        final WhereColumn rwc = new WhereColumn();
        final Vector rwcColExp = new Vector();
        rwcColExp.add("1");
        lwc.setColumnExpression(lwcColExp);
        rwc.setColumnExpression(rwcColExp);
        wi.setLeftWhereExp(lwc);
        wi.setRightWhereExp(rwc);
        wi.setOperator("=");
        qualifyExpression.addWhereItem(wi);
        qualifyItems.add(qualifyExpression);
        if (functionArgument != null) {
            newSelColList.add(functionArgument);
        }
        if (fnCall.getPartitionByClause() != null) {
            final ArrayList selColsList = fnCall.getPartitionByClause().getSelectColumnList();
            for (int k = 0; k < selColsList.size(); ++k) {
                if (selColsList.get(k) instanceof SelectColumn) {
                    final SelectColumn partSelCol = selColsList.get(k);
                    final SelectColumn newPartSelCol = new SelectColumn();
                    newPartSelCol.setColumnExpression(partSelCol.getColumnExpression());
                    newPartSelCol.setAliasName("partition_" + k);
                    newPartSelCol.setEndsWith(",");
                    newWhereItemList.add(this.generateWhereItems(partSelCol, alias, "partition_" + k));
                    newSelColList.add(newPartSelCol);
                }
            }
        }
        else {
            newWhereItemList.add(wi);
        }
        if (fnName.equalsIgnoreCase("dense_rank") && fnCall.getOrderBy() != null) {
            final OrderByStatement obs = fnCall.getOrderBy();
            final Vector orderItemList = obs.getOrderItemList();
            for (int i_count = 0; i_count < orderItemList.size(); ++i_count) {
                final OrderItem oi = orderItemList.elementAt(i_count);
                if (oi != null) {
                    final SelectColumn orderSelCol = oi.getOrderSpecifier();
                    final SelectColumn newOrderSelCol = new SelectColumn();
                    newOrderSelCol.setColumnExpression(orderSelCol.getColumnExpression());
                    newOrderSelCol.setAliasName("order_" + i_count);
                    newOrderSelCol.setEndsWith(",");
                    newWhereItemList.add(this.generateWhereItems(orderSelCol, alias, "order_" + i_count));
                    newSelColList.add(newOrderSelCol);
                }
            }
        }
        newSelColList.lastElement().setEndsWith(null);
        selStmt.setSelectItemList(newSelColList);
        derivedTable.setSelectStatement(selStmt);
        qualifyStmt.setHavingItems(qualifyItems);
        if (fnName.equalsIgnoreCase("dense_rank") || fnName.equalsIgnoreCase("count")) {
            final GroupByStatement gbs = new GroupByStatement();
            gbs.setGroupClause("GROUP BY");
            final Vector groupByItems = new Vector();
            for (int i = 1; i < newSelColList.size(); ++i) {
                final SelectColumn gbsSC = new SelectColumn();
                final Vector gbsSCExp = new Vector();
                gbsSCExp.add("" + (i + 1));
                gbsSC.setColumnExpression(gbsSCExp);
                groupByItems.add(gbsSC);
            }
            gbs.setGroupByItemList(groupByItems);
            derivedTable.setGroupByStatement(gbs);
        }
        else {
            derivedTable.setHavingStatement(qualifyStmt);
        }
        final FromTable derivedTableFromItem = new FromTable();
        derivedTableFromItem.setTableName(derivedTable);
        derivedTableFromItem.setAliasName(alias);
        derivedTableFromItem.setJoinClause("INNER JOIN ");
        derivedTableFromItem.setOnOrUsingJoin("ON");
        final Vector joinCondition = new Vector();
        final WhereExpression we = new WhereExpression();
        we.setWhereItem(newWhereItemList);
        final Vector operators = new Vector();
        for (int s = 0; s < newWhereItemList.size() - 1; ++s) {
            operators.add("AND");
        }
        we.setOperator(operators);
        joinCondition.add(we);
        derivedTableFromItem.setJoinExpression(joinCondition);
        return derivedTableFromItem;
    }
    
    private SelectColumn getNewSelectColumnForDerivedTable(final FunctionCalls fnCall, final int argIndex) throws ConvertException {
        final SelectColumn rownumSelCol = new SelectColumn();
        final String origFnName = fnCall.getFunctionName().getColumnName();
        if (origFnName.equalsIgnoreCase("count")) {
            final FunctionCalls countFunc = new FunctionCalls();
            countFunc.setFunctionName(fnCall.getFunctionName());
            countFunc.setFunctionArguments(fnCall.getFunctionArguments());
            countFunc.setArgumentQualifier(fnCall.getArgumentQualifier());
            final Vector rownumSelColExp = new Vector();
            rownumSelColExp.add(countFunc);
            rownumSelCol.setColumnExpression(rownumSelColExp);
            rownumSelCol.setAliasName("cnt_" + argIndex);
        }
        else {
            final FunctionCalls rownumFunc = new FunctionCalls();
            final TableColumn rownumFuncName = new TableColumn();
            rownumFuncName.setColumnName("ROW_NUMBER");
            rownumFunc.setFunctionName(rownumFuncName);
            rownumFunc.setFunctionArguments(new Vector());
            if (fnCall.getPartitionByClause() != null) {
                rownumFunc.setPartitionByClause(fnCall.getPartitionByClause());
            }
            if (fnCall.getOrderBy() != null) {
                final OrderByStatement obs = fnCall.getOrderBy();
                if (origFnName.equalsIgnoreCase("last_value")) {
                    final Vector orderItemList = obs.getOrderItemList();
                    for (int i_count = 0; i_count < orderItemList.size(); ++i_count) {
                        final OrderItem oi = orderItemList.elementAt(i_count);
                        if (oi != null) {
                            final String orderType = oi.getOrder();
                            if (orderType != null && orderType.equalsIgnoreCase("ASC")) {
                                oi.setOrder("DESC");
                            }
                            if (orderType != null && orderType.equalsIgnoreCase("DESC")) {
                                oi.setOrder("ASC");
                            }
                            else if (orderType == null) {
                                oi.setOrder("DESC");
                            }
                        }
                    }
                }
                rownumFunc.setOrderBy(obs);
            }
            rownumFunc.setOver("OVER");
            final Vector rownumSelColExp2 = new Vector();
            rownumSelColExp2.add(rownumFunc);
            rownumSelCol.setColumnExpression(rownumSelColExp2);
            if (origFnName.equalsIgnoreCase("dense_rank")) {
                rownumSelCol.setAliasName("rnk");
            }
            else {
                rownumSelCol.setAliasName("rownum_" + argIndex);
            }
        }
        rownumSelCol.setEndsWith(",");
        return rownumSelCol;
    }
    
    private WhereItem generateWhereItems(final SelectColumn selCol, final String derivedTableAlias, final String derivedTableColumn) throws ConvertException {
        final WhereItem wi = new WhereItem();
        final WhereColumn lwc = new WhereColumn();
        final Vector lwcColExp = new Vector();
        lwcColExp.add(selCol);
        final WhereColumn rwc = new WhereColumn();
        final Vector rwcColExp = new Vector();
        if (selCol != null) {
            final TableColumn rsc = new TableColumn();
            rsc.setTableName(derivedTableAlias);
            rsc.setColumnName(derivedTableColumn);
            rwcColExp.add(rsc);
        }
        lwc.setColumnExpression(lwcColExp);
        rwc.setColumnExpression(rwcColExp);
        wi.setLeftWhereExp(lwc);
        wi.setRightWhereExp(rwc);
        wi.setOperator("=");
        return wi;
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.obs != null) {
            this.setOrderBy(this.obs.toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.getPartitionByClause() != null) {
            this.setPartitionByClause(this.getPartitionByClause().toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.getWindowingClause() != null) {
            this.setWindowingClause(this.getWindowingClause().toMySQL(to_sqs, from_sqs));
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.obs != null) {
            this.setOrderBy(this.obs.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.getPartitionByClause() != null) {
            this.setPartitionByClause(this.getPartitionByClause().toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.getWindowingClause() != null) {
            this.setWindowingClause(this.getWindowingClause().toPostgreSQL(to_sqs, from_sqs));
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.obs != null) {
            this.setOrderBy(this.obs.toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.getPartitionByClause() != null) {
            this.setPartitionByClause(this.getPartitionByClause().toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.getWindowingClause() != null) {
            this.setWindowingClause(this.getWindowingClause().toVectorWise(to_sqs, from_sqs));
        }
    }
}
