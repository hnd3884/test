package com.adventnet.swissqlapi.sql.functions.aggregate;

import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import java.util.Collection;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class sum extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUM");
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
        this.functionName.setColumnName("SUM");
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
        if (this.functionArguments.size() == 1) {
            final SelectColumn sc = new SelectColumn();
            final FunctionCalls fc = new FunctionCalls();
            final TableColumn functionName = new TableColumn();
            functionName.setColumnName("CONVERT");
            fc.setFunctionName(functionName);
            final Vector subFunctionArgs = new Vector();
            subFunctionArgs.add("FLOAT");
            subFunctionArgs.add(this.functionArguments.get(0));
            fc.setFunctionArguments(subFunctionArgs);
            final Vector colExp = new Vector();
            colExp.add(fc);
            sc.setColumnExpression(colExp);
            final Vector newFunctionArg = new Vector();
            newFunctionArg.add(sc);
            this.setFunctionArguments(newFunctionArg);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUM");
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
        this.functionName.setColumnName("SUM");
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
        this.functionName.setColumnName("SUM");
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
        this.functionName.setColumnName("SUM");
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
            this.validateAggFunArgsType(arguments, "SUM");
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUM");
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
        this.functionName.setColumnName("SUM");
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
        this.functionName.setColumnName("SUM");
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
        this.functionName.setColumnName("SUM");
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
        this.functionName.setColumnName("SUM");
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
        if (this.getOrderBy() != null && this.getWindowingClause() != null && this.getWindowingClause().toString().equals("")) {
            final WindowingClause windowClause = new WindowingClause();
            windowClause.setRowsOrRange("ROWS");
            windowClause.setUnbounded("UNBOUNDED");
            windowClause.setPreceding("PRECEDING");
            this.setWindowingClause(windowClause);
        }
        final String argQualifier = this.getArgumentQualifier();
        if (argQualifier != null && argQualifier.equalsIgnoreCase("distinct") && this.getOver() != null && this.getOver().equalsIgnoreCase("over") && this.getPartitionByClause() != null) {
            final String pString = this.getPartitionByClause().toString();
            final String colString = this.getFunctionArguments().get(0).toString();
            FromTable ft = new FromTable();
            if (from_sqs.getSumDerivedTables().containsKey(this.getPartitionByClause().toString())) {
                ft = from_sqs.getSumDerivedTables().get(pString);
                final SelectQueryStatement dummysqs = (SelectQueryStatement)ft.getTableName();
                final Vector oldSelectItems = new Vector(dummysqs.getSelectStatement().getSelectItemList());
                final SelectColumn dummysc = oldSelectItems.lastElement();
                dummysc.setEndsWith(",");
                oldSelectItems.setElementAt(dummysc, oldSelectItems.size() - 1);
                final Vector v = new Vector();
                final FunctionCalls fc = new FunctionCalls();
                fc.setFunctionArguments(this.getFunctionArguments());
                fc.setFunctionName(this.getFunctionName());
                fc.setArgumentQualifier(this.getArgumentQualifier());
                fc.setOrderBy(this.getOrderBy());
                v.add(fc);
                final SelectColumn fnCallsc = new SelectColumn();
                fnCallsc.setColumnExpression(v);
                fnCallsc.setAliasName("sum_" + (oldSelectItems.size() + 1));
                oldSelectItems.add(fnCallsc);
                dummysqs.getSelectStatement().setSelectItemList(oldSelectItems);
                ft.setTableName(dummysqs);
                from_sqs.addSumDerivedTables(pString, ft);
                from_sqs.addSumSelectColumnAlias(pString + colString, "sum_" + oldSelectItems.size());
            }
            else {
                final SelectColumn sc = new SelectColumn();
                SelectColumn dummysc2 = new SelectColumn();
                dummysc2 = this.getFunctionArguments().get(0);
                sc.setColumnExpression(dummysc2.getColumnExpression());
                final String aliasName = "SUM_ALIAS";
                final String indexForAlias = "" + (from_sqs.getSumDerivedTables().size() + 1);
                ft = this.createTeradataDerivedTable(to_sqs, this, sc, aliasName + indexForAlias);
                ft.setAliasName(aliasName + indexForAlias);
                from_sqs.setSumFunctionWithPartitionAvailable(true);
                from_sqs.addSumDerivedTables(pString, ft);
                from_sqs.addSumSelectColumnAlias(pString + colString, "sum_" + this.getPartitionByClause().getSelectColumnList().size());
            }
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
        final WhereItem wi = new WhereItem();
        final WhereColumn lwc = new WhereColumn();
        final Vector lwcColExp = new Vector();
        final WhereColumn rwc = new WhereColumn();
        final Vector rwcColExp = new Vector();
        rwcColExp.add("1");
        lwc.setColumnExpression(lwcColExp);
        rwc.setColumnExpression(rwcColExp);
        wi.setLeftWhereExp(lwc);
        wi.setRightWhereExp(rwc);
        wi.setOperator("=");
        if (fnCall.getPartitionByClause() != null) {
            final ArrayList selColsList = fnCall.getPartitionByClause().getSelectColumnList();
            for (int k = 0; k < selColsList.size(); ++k) {
                if (selColsList.get(k) instanceof SelectColumn) {
                    final SelectColumn partSelCol = selColsList.get(k);
                    final SelectColumn newPartSelCol = new SelectColumn();
                    newPartSelCol.setColumnExpression(partSelCol.getColumnExpression());
                    newPartSelCol.setAliasName("sum_" + k);
                    newPartSelCol.setEndsWith(",");
                    newWhereItemList.add(this.generateWhereItems(partSelCol, alias, "sum_" + k));
                    newSelColList.add(newPartSelCol);
                }
            }
            final Vector v = new Vector();
            final FunctionCalls fc = new FunctionCalls();
            fc.setFunctionArguments(fnCall.getFunctionArguments());
            fc.setFunctionName(fnCall.getFunctionName());
            fc.setArgumentQualifier(fnCall.getArgumentQualifier());
            fc.setOrderBy(fnCall.getOrderBy());
            v.add(fc);
            final SelectColumn fnCallsc = new SelectColumn();
            fnCallsc.setColumnExpression(v);
            fnCallsc.setAliasName("sum_" + selColsList.size());
            newSelColList.add(fnCallsc);
        }
        else {
            newWhereItemList.add(wi);
        }
        newSelColList.lastElement().setEndsWith(null);
        selStmt.setSelectItemList(newSelColList);
        derivedTable.setSelectStatement(selStmt);
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
    
    private WhereItem generateWhereItems(final SelectColumn selCol, final String derivedTableAlias, final String derivedTableColumn) throws ConvertException {
        final WhereItem wi = new WhereItem();
        final WhereColumn lwc = new WhereColumn();
        final Vector lwcColExp = new Vector();
        final WhereColumn rwc = new WhereColumn();
        final Vector rwcColExp = new Vector();
        if (selCol != null) {
            final TableColumn lsc = new TableColumn();
            final Vector vlwc = new Vector(this.changeTableNameInWhereItems(selCol.getColumnExpression(), "orgnl"));
            final SelectColumn sc = new SelectColumn();
            sc.setColumnExpression(vlwc);
            lwcColExp.add(sc);
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
    
    private Vector changeTableNameInWhereItems(final Vector vc, final String alias) {
        final Vector vc2 = new Vector();
        for (int i = 0, size = vc.size(); i < size; ++i) {
            if (vc.get(i) instanceof SelectColumn) {
                SelectColumn scOld = new SelectColumn();
                scOld = vc.get(i);
                final Vector scv = new Vector();
                scv.addAll(scOld.getColumnExpression());
                final SelectColumn scNew = new SelectColumn();
                scNew.setColumnExpression(this.changeTableNameInWhereItems(scv, alias));
                vc2.add(scNew);
            }
            if (vc.get(i) instanceof TableColumn) {
                final TableColumn tc = vc.get(i);
                final TableColumn tcNew = new TableColumn();
                tcNew.setTableName(alias);
                tcNew.setColumnName(tc.getColumnName());
                vc2.add(tcNew);
            }
            if (vc.get(i) instanceof FunctionCalls) {
                Vector vfc = new Vector(vc.get(i).getFunctionArguments());
                vfc = new Vector(this.changeTableNameInWhereItems(vfc, alias));
                final FunctionCalls fc = new FunctionCalls();
                fc.setFunctionArguments(vfc);
                vc2.add(fc);
            }
        }
        return vc2;
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUM");
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
