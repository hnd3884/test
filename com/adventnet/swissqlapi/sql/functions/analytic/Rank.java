package com.adventnet.swissqlapi.sql.functions.analytic;

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

public class Rank extends FunctionCalls
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
            final SelectColumn countFuncCol = new SelectColumn();
            final Vector countFuncArgExp = new Vector();
            countFuncArgExp.add(caseStmt);
            countFuncCol.setColumnExpression(countFuncArgExp);
            final Vector countFuncArgs = new Vector();
            countFuncArgs.add(countFuncCol);
            countFunc.setFunctionArguments(countFuncArgs);
            final SelectColumn newArg = new SelectColumn();
            final Vector newArgExp = new Vector();
            if (this.functionName.getColumnName().equalsIgnoreCase("percent_rank")) {
                final FunctionCalls castFunc = new FunctionCalls();
                final TableColumn castName = new TableColumn();
                castName.setColumnName("CAST");
                castFunc.setFunctionName(castName);
                final Vector castFuncArgs = new Vector();
                castFuncArgs.add(countFunc);
                castFuncArgs.add("DECIMAL(8,6)");
                castFunc.setAsDatatype("AS");
                castFunc.setFunctionArguments(castFuncArgs);
                final FunctionCalls countAllFunc = new FunctionCalls();
                final TableColumn countAllFuncName = new TableColumn();
                countAllFuncName.setColumnName("COUNT");
                countAllFunc.setFunctionName(countAllFuncName);
                final SelectColumn countAllFuncCol = new SelectColumn();
                final Vector countAllFuncArgExp = new Vector();
                countAllFuncArgExp.add("*");
                countAllFuncCol.setColumnExpression(countAllFuncArgExp);
                final Vector countAllFuncArgs = new Vector();
                countAllFuncArgs.add(countAllFuncCol);
                countAllFunc.setFunctionArguments(countAllFuncArgs);
                newArgExp.add("(");
                newArgExp.add("(");
                newArgExp.add(castFunc);
                newArgExp.add(")");
                newArgExp.add("/");
                newArgExp.add(countAllFunc);
            }
            else if (this.functionName.getColumnName().equalsIgnoreCase("rank")) {
                newArgExp.add("(");
                newArgExp.add(countFunc);
                newArgExp.add("+");
                newArgExp.add("1");
            }
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
            final Vector arguments2 = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments2.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                }
                else {
                    arguments2.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            this.setFunctionArguments(arguments2);
            if (this.obs != null) {
                this.setOrderBy(this.obs.toTeradataSelect(null, null));
            }
            if (this.getPartitionByClause() != null) {
                this.setPartitionByClause(this.getPartitionByClause().toTeradataSelect(to_sqs, from_sqs));
            }
            if (this.getWindowingClause() != null) {
                this.setWindowingClause(this.getWindowingClause().toTeradata(to_sqs, from_sqs));
            }
        }
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
