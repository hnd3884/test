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

public class Cume_Dist extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.getWithinGroup() != null) {
            if (this.functionArguments.size() > 1 || this.getOrderBy().getOrderItemList().size() > 1) {
                return;
            }
            String caseOper = "<=";
            SelectColumn funcArg = this.functionArguments.firstElement();
            funcArg = funcArg.toTeradataSelect(to_sqs, from_sqs);
            OrderItem orderByArg = this.getOrderBy().getOrderItemList().firstElement();
            orderByArg = orderByArg.toTeradataSelect(to_sqs, from_sqs);
            final SelectColumn orderByCol = orderByArg.getOrderSpecifier();
            if (orderByArg.getOrder() != null && orderByArg.getOrder().equalsIgnoreCase("DESC")) {
                caseOper = ">=";
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
            final SelectColumn newArg = new SelectColumn();
            final Vector newArgExp = new Vector();
            newArgExp.add("(");
            newArgExp.add("(");
            newArgExp.add(castFunc);
            newArgExp.add("+");
            newArgExp.add("1");
            newArgExp.add(")");
            newArgExp.add("/");
            newArgExp.add("(");
            newArgExp.add(countAllFunc);
            newArgExp.add("+");
            newArgExp.add("1");
            newArgExp.add(")");
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
            final FunctionCalls countFunc2 = new FunctionCalls();
            final TableColumn countFuncName2 = new TableColumn();
            countFuncName2.setColumnName("COUNT");
            countFunc2.setFunctionName(countFuncName2);
            final SelectColumn countFuncCol2 = new SelectColumn();
            final Vector countFuncArgExp2 = new Vector();
            countFuncArgExp2.add("*");
            countFuncCol2.setColumnExpression(countFuncArgExp2);
            final Vector countFuncArgs2 = new Vector();
            countFuncArgs2.add(countFuncCol2);
            countFunc2.setFunctionArguments(countFuncArgs2);
            countFunc2.setOver("OVER");
            QueryPartitionClause countFuncPart = new QueryPartitionClause();
            if (this.getPartitionByClause() != null) {
                countFuncPart = this.getPartitionByClause().toTeradataSelect(to_sqs, from_sqs);
                countFunc2.setPartitionByClause(countFuncPart);
            }
            final FunctionCalls rankFunc = new FunctionCalls();
            final TableColumn rankFuncName = new TableColumn();
            rankFuncName.setColumnName("RANK");
            rankFunc.setFunctionName(rankFuncName);
            rankFunc.setOver("OVER");
            rankFunc.setPartitionByClause(countFuncPart);
            rankFunc.setOrderBy(this.getOrderBy().toTeradataSelect(to_sqs, from_sqs));
            final FunctionCalls castFunc2 = new FunctionCalls();
            final TableColumn castName2 = new TableColumn();
            castName2.setColumnName("CAST");
            castFunc2.setFunctionName(castName2);
            final Vector castFuncArgs2 = new Vector();
            castFuncArgs2.add(rankFunc);
            castFuncArgs2.add("DECIMAL(8,6)");
            castFunc2.setAsDatatype("AS");
            castFunc2.setFunctionArguments(castFuncArgs2);
            final SelectColumn newArg2 = new SelectColumn();
            final Vector newArgExp2 = new Vector();
            newArgExp2.add("(");
            newArgExp2.add(castFunc2);
            newArgExp2.add("/");
            newArgExp2.add(countFunc2);
            newArg2.setColumnExpression(newArgExp2);
            this.setFunctionName(null);
            final Vector arguments2 = new Vector();
            arguments2.add(newArg2);
            this.setFunctionArguments(arguments2);
            this.setOver(null);
            this.setOrderBy(null);
            this.setPartitionByClause(null);
        }
    }
}
