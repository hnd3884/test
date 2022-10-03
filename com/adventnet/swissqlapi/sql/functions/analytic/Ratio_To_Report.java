package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class Ratio_To_Report extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn funcArg = this.functionArguments.get(0).toTeradataSelect(to_sqs, from_sqs);
        final FunctionCalls sumFunc = new FunctionCalls();
        final TableColumn sumFuncName = new TableColumn();
        sumFuncName.setColumnName("SUM");
        sumFunc.setFunctionName(sumFuncName);
        final SelectColumn sumFuncCol = new SelectColumn();
        final Vector sumFuncArgExp = new Vector();
        sumFuncArgExp.add(funcArg);
        sumFuncCol.setColumnExpression(sumFuncArgExp);
        final Vector sumFuncArgs = new Vector();
        sumFuncArgs.add(sumFuncCol);
        sumFunc.setFunctionArguments(sumFuncArgs);
        sumFunc.setOver("OVER");
        QueryPartitionClause sumFuncPart = new QueryPartitionClause();
        if (this.getPartitionByClause() != null) {
            sumFuncPart = this.getPartitionByClause().toTeradataSelect(to_sqs, from_sqs);
            sumFunc.setPartitionByClause(sumFuncPart);
        }
        final SelectColumn newArg = new SelectColumn();
        final Vector newArgExp = new Vector();
        newArgExp.add("(");
        newArgExp.add(funcArg);
        newArgExp.add("/");
        newArgExp.add(sumFunc);
        newArg.setColumnExpression(newArgExp);
        this.setFunctionName(null);
        final Vector arguments = new Vector();
        arguments.add(newArg);
        this.setFunctionArguments(arguments);
        this.setOver(null);
        this.setOrderBy(null);
        this.setPartitionByClause(null);
    }
}
