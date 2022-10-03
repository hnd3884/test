package com.adventnet.swissqlapi.sql.functions.aggregate;

import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class count extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selColumn = this.functionArguments.elementAt(i_count);
                selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(this.functionArguments.size());
                arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (from_sqs != null && !from_sqs.getcanAllowLogicalExpInAggFun()) {
            this.validateAggFunArgsType(arguments, "COUNT");
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTimesTenSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
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
        final String argQualifier = this.getArgumentQualifier();
        if (argQualifier != null && argQualifier.equalsIgnoreCase("distinct") && this.getOver() != null && this.getOver().equalsIgnoreCase("over")) {
            from_sqs.setOlapFunctionPresent(true);
            final String alias = from_sqs.getFromClause().getLastElement().getAliasName();
            String idx = "" + from_sqs.getOlapDerivedTables().size();
            if (this.getPartitionByClause() != null) {
                final String countString = "count" + this.getPartitionByClause().toString() + this.obs;
                if (from_sqs.getOlapDerivedTables().containsKey(countString)) {
                    final FromTable derivedTable = from_sqs.getOlapDerivedTables().get(countString);
                    idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
                }
                else {
                    from_sqs.addOlapDerivedTables(countString, this.createTeradataDerivedTable(to_sqs, this, null, alias + idx));
                }
            }
            else {
                from_sqs.addOlapDerivedTables("countpartition" + idx + this.obs, this.createTeradataDerivedTable(to_sqs, this, null, alias + idx));
            }
            final TableColumn newTabCol = new TableColumn();
            newTabCol.setTableName(alias + idx);
            newTabCol.setColumnName("cnt_0");
            this.setFunctionName(newTabCol);
            this.getFunctionArguments().clear();
            this.setArgumentQualifier("");
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
        final SelectColumn rownumCol = this.getNewSelectColumnForDerivedTable(fnCall, 0);
        newSelColList.add(rownumCol);
        final WhereItem wi = new WhereItem();
        final WhereColumn lwc = new WhereColumn();
        final Vector lwcColExp = new Vector();
        if (fnCall.getPartitionByClause() != null) {
            final TableColumn tc1 = new TableColumn();
            tc1.setColumnName(rownumCol.getAliasName());
            lwcColExp.add(tc1);
        }
        else {
            lwcColExp.add("1");
        }
        final WhereColumn rwc = new WhereColumn();
        final Vector rwcColExp = new Vector();
        rwcColExp.add("1");
        lwc.setColumnExpression(lwcColExp);
        rwc.setColumnExpression(rwcColExp);
        wi.setLeftWhereExp(lwc);
        wi.setRightWhereExp(rwc);
        wi.setOperator("=");
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
        newSelColList.lastElement().setEndsWith(null);
        selStmt.setSelectItemList(newSelColList);
        derivedTable.setSelectStatement(selStmt);
        if (fnCall.getPartitionByClause() != null) {
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
        final FromTable derivedTableFromItem = new FromTable();
        derivedTableFromItem.setTableName(derivedTable);
        derivedTableFromItem.setAliasName(alias);
        derivedTableFromItem.setJoinClause("LEFT OUTER JOIN ");
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
        final FunctionCalls countFunc = new FunctionCalls();
        countFunc.setFunctionName(fnCall.getFunctionName());
        final Vector newFuncArgs = new Vector();
        newFuncArgs.addAll(fnCall.getFunctionArguments());
        countFunc.setFunctionArguments(newFuncArgs);
        countFunc.setArgumentQualifier(fnCall.getArgumentQualifier());
        final Vector rownumSelColExp = new Vector();
        rownumSelColExp.add(countFunc);
        rownumSelCol.setColumnExpression(rownumSelColExp);
        rownumSelCol.setAliasName("cnt_" + argIndex);
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
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COUNT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selColumn = this.functionArguments.elementAt(i_count);
                selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(this.functionArguments.size());
                arguments.addElement(selColumn.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
}
