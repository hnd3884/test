package com.adventnet.swissqlapi.sql.functions.analytic;

import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.HavingStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class FirstValue extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        from_sqs.setOlapFunctionPresent(true);
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final SelectColumn newArgSelCol = new SelectColumn();
        final SelectColumn arg = this.getFunctionArguments().get(0);
        newArgSelCol.setColumnExpression(arg.getColumnExpression());
        newArgSelCol.setIgnoreNulls(arg.getIgnoreNulls());
        String alias = from_sqs.getFromClause().getLastElement().getAliasName();
        String idx = "" + from_sqs.getOlapDerivedTables().size();
        if (this.getPartitionByClause() != null) {
            final String partitionString = "first_value" + this.getPartitionByClause().toString() + this.obs;
            if (from_sqs.getOlapDerivedTables().containsKey(partitionString)) {
                final FromTable derivedTable = from_sqs.getOlapDerivedTables().get(partitionString);
                final Vector existingSelectItems = ((SelectQueryStatement)derivedTable.getTableName()).getSelectStatement().getSelectItemList();
                final int siz = existingSelectItems.size();
                newArgSelCol.setAliasName("arg_" + siz);
                final SelectColumn selCol = existingSelectItems.get(siz - 1);
                if (selCol.getEndsWith() == null) {
                    selCol.setEndsWith(",");
                }
                existingSelectItems.add(newArgSelCol);
                alias = derivedTable.getAliasName();
                idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
            }
            else {
                newArgSelCol.setAliasName("arg_0");
                newArgSelCol.setEndsWith(",");
                from_sqs.addOlapDerivedTables(partitionString, this.createTeradataDerivedTable(to_sqs, this, newArgSelCol, alias + idx));
                alias += idx;
            }
        }
        else {
            final String orderString = "first_value" + this.obs;
            if (from_sqs.getOlapDerivedTables().containsKey(orderString)) {
                final FromTable derivedTable = from_sqs.getOlapDerivedTables().get(orderString);
                final Vector existingSelectItems = ((SelectQueryStatement)derivedTable.getTableName()).getSelectStatement().getSelectItemList();
                final int siz = existingSelectItems.size();
                newArgSelCol.setAliasName("arg_" + siz);
                final SelectColumn selCol = existingSelectItems.get(siz - 1);
                if (selCol.getEndsWith() == null) {
                    selCol.setEndsWith(",");
                }
                existingSelectItems.add(newArgSelCol);
                alias = derivedTable.getAliasName();
                idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
            }
            else {
                newArgSelCol.setAliasName("arg_0");
                from_sqs.addOlapDerivedTables(orderString, this.createTeradataDerivedTable(to_sqs, this, newArgSelCol, alias + idx));
                alias += idx;
            }
        }
        final TableColumn newTabCol = new TableColumn();
        newTabCol.setTableName(alias);
        newTabCol.setColumnName(newArgSelCol.getAliasName());
        this.setFunctionName(newTabCol);
        this.getFunctionArguments().clear();
        this.setOpenBracesForFunctionNameRequired(false);
        this.setPartitionByClause(null);
        this.setOver(null);
        this.setOrderBy(null);
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
        tc1.setTableName(alias);
        lwcColExp.add(tc1);
        final WhereColumn rwc = new WhereColumn();
        final Vector rwcColExp = new Vector();
        rwcColExp.add("1");
        lwc.setColumnExpression(lwcColExp);
        rwc.setColumnExpression(rwcColExp);
        wi.setLeftWhereExp(lwc);
        wi.setRightWhereExp(rwc);
        wi.setOperator("=");
        final WhereItem qualifyWi = new WhereItem();
        final WhereColumn qualifyWilwc = new WhereColumn();
        final Vector qualifyWilwcColExp = new Vector();
        final TableColumn qualifyWitc1 = new TableColumn();
        qualifyWitc1.setColumnName(rownumCol.getAliasName());
        qualifyWilwcColExp.add(qualifyWitc1);
        final WhereColumn qualifyWirwc = new WhereColumn();
        final Vector qualifyWirwcColExp = new Vector();
        qualifyWirwcColExp.add("1");
        qualifyWilwc.setColumnExpression(qualifyWilwcColExp);
        qualifyWirwc.setColumnExpression(qualifyWirwcColExp);
        qualifyWi.setLeftWhereExp(qualifyWilwc);
        qualifyWi.setRightWhereExp(qualifyWirwc);
        qualifyWi.setOperator("=");
        qualifyExpression.addWhereItem(qualifyWi);
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
        final WhereExpression weForDerivedTable = new WhereExpression();
        final Vector whereItems = new Vector();
        final WhereItem newWI = new WhereItem();
        final WhereColumn newWC = new WhereColumn();
        if (functionArgument.getIgnoreNulls() != null) {
            newWI.setOperator("IS NOT NULL");
            newWC.setColumnExpression(functionArgument.getColumnExpression());
            newWI.setLeftWhereExp(newWC);
            whereItems.add(newWI);
            weForDerivedTable.setWhereItem(whereItems);
            derivedTable.setWhereExpression(weForDerivedTable);
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
            if (origFnName.equalsIgnoreCase("first_value")) {
                final Vector orderItemList = obs.getOrderItemList();
                for (int i_count = 0; i_count < orderItemList.size(); ++i_count) {
                    final OrderItem oi = orderItemList.elementAt(i_count);
                    if (oi != null) {
                        final String orderType = oi.getOrder();
                        if (orderType != null && orderType.equalsIgnoreCase("ASC")) {
                            oi.setOrder("ASC");
                        }
                        if (orderType != null && orderType.equalsIgnoreCase("DESC")) {
                            oi.setOrder("DESC");
                        }
                        else if (orderType == null) {
                            oi.setOrder("ASC");
                        }
                    }
                }
            }
            rownumFunc.setOrderBy(obs);
        }
        else {
            final OrderByStatement partitionObs = new OrderByStatement();
            partitionObs.setOrderClause("ORDER BY");
            final Vector v_oil = new Vector();
            for (int pi = 0; pi < fnCall.getPartitionByClause().getSelectColumnList().size(); ++pi) {
                if (fnCall.getPartitionByClause().getSelectColumnList().get(pi) instanceof SelectColumn) {
                    final OrderItem oi = new OrderItem();
                    oi.setOrderSpecifier(fnCall.getPartitionByClause().getSelectColumnList().get(pi));
                    v_oil.add(oi);
                }
            }
            partitionObs.setOrderItemList(v_oil);
            rownumFunc.setOrderBy(partitionObs);
        }
        rownumFunc.setOver("OVER");
        final Vector rownumSelColExp = new Vector();
        rownumSelColExp.add(rownumFunc);
        rownumSelCol.setColumnExpression(rownumSelColExp);
        if (origFnName.equalsIgnoreCase("dense_rank")) {
            rownumSelCol.setAliasName("rnk");
        }
        else {
            rownumSelCol.setAliasName("rownum_" + argIndex);
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
