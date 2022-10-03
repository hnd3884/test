package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class Ntile extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectColumn ntileArg = this.functionArguments.get(0).toTeradataSelect(to_sqs, from_sqs);
        final FunctionCalls countFunc = new FunctionCalls();
        final TableColumn countFuncName = new TableColumn();
        countFuncName.setColumnName("COUNT");
        countFunc.setFunctionName(countFuncName);
        final SelectColumn countFuncCol = new SelectColumn();
        final Vector countFuncArgExp = new Vector();
        countFuncArgExp.add("*");
        countFuncCol.setColumnExpression(countFuncArgExp);
        final Vector countFuncArgs = new Vector();
        countFuncArgs.add(countFuncCol);
        countFunc.setFunctionArguments(countFuncArgs);
        countFunc.setOver("OVER");
        QueryPartitionClause countFuncPart = new QueryPartitionClause();
        if (this.getPartitionByClause() != null) {
            countFuncPart = this.getPartitionByClause().toTeradataSelect(to_sqs, from_sqs);
            countFunc.setPartitionByClause(countFuncPart);
        }
        final FunctionCalls rankFunc = new FunctionCalls();
        final TableColumn rankFuncName = new TableColumn();
        rankFuncName.setColumnName("RANK");
        rankFunc.setFunctionName(rankFuncName);
        rankFunc.setOver("OVER");
        rankFunc.setPartitionByClause(countFuncPart);
        rankFunc.setOrderBy(this.getOrderBy().toTeradataSelect(to_sqs, from_sqs));
        final SelectColumn newArg = new SelectColumn();
        final Vector newArgExp = new Vector();
        newArgExp.add("(");
        newArgExp.add("(");
        newArgExp.add(rankFunc);
        newArgExp.add("-");
        newArgExp.add("1");
        newArgExp.add(")");
        newArgExp.add("*");
        newArgExp.add(ntileArg);
        newArgExp.add("/");
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
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
