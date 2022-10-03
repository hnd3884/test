package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class Lead extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MAX");
        final String and = "AND";
        final String between = "BETWEEN";
        final String following = "FOLLOWING";
        final String rows = "ROWS";
        String defaultValue = null;
        SelectColumn defaultValueColumn = null;
        SelectColumn firstWindowExp = null;
        SelectColumn secondWindowExp = null;
        final WindowingClause windowingClause = new WindowingClause();
        final WindowingClause firstWindowingClause = new WindowingClause();
        final WindowingClause secondWindowingClause = new WindowingClause();
        final Vector newFunctionArgs = new Vector();
        final String offset = null;
        final String lag_Default = null;
        for (int count = 0; count < this.functionArguments.size(); ++count) {
            final Object obj = this.functionArguments.get(count);
            if (obj instanceof SelectColumn) {
                if (count == 0) {
                    newFunctionArgs.add(this.functionArguments.get(count).toTeradataSelect(to_sqs, from_sqs));
                    if (this.functionArguments.size() == 1) {
                        firstWindowExp = new SelectColumn();
                        final Vector firstWindowColExp = new Vector();
                        firstWindowColExp.add("1");
                        firstWindowExp.setColumnExpression(firstWindowColExp);
                    }
                }
                else if (count == 1) {
                    firstWindowExp = this.functionArguments.get(count).toTeradataSelect(to_sqs, from_sqs);
                }
                else if (count == 2) {
                    secondWindowExp = this.functionArguments.get(count).toTeradataSelect(to_sqs, from_sqs);
                    if (secondWindowExp != null) {
                        final Vector colExp = secondWindowExp.getColumnExpression();
                        defaultValue = colExp.get(0).toString();
                        defaultValueColumn = this.functionArguments.get(count);
                    }
                }
            }
        }
        firstWindowingClause.setFollowing(following);
        firstWindowingClause.setWindowExpr(firstWindowExp);
        secondWindowingClause.setFollowing(following);
        secondWindowingClause.setWindowExpr(firstWindowExp);
        windowingClause.setAnd(and);
        windowingClause.setBetween(between);
        windowingClause.setRowsOrRange(rows);
        windowingClause.setFirstWindow(firstWindowingClause);
        windowingClause.setSecondWindow(secondWindowingClause);
        this.setFunctionArguments(newFunctionArgs);
        this.setWindowingClause(windowingClause);
        if (defaultValue != null && defaultValue.trim().equals("0")) {
            final FunctionCalls fc = new FunctionCalls();
            final TableColumn newFunctName = new TableColumn();
            final Vector newFunctionArguments = new Vector();
            final SelectColumn scForFirstArgument = new SelectColumn();
            final Vector colExpForFirstArgument = new Vector();
            final SelectColumn scForSecondArgument = new SelectColumn();
            final Vector colExpForSecondArgument = new Vector();
            colExpForFirstArgument.add(this.toTeradataSelect(to_sqs, from_sqs));
            scForFirstArgument.setColumnExpression(colExpForFirstArgument);
            colExpForSecondArgument.add("0");
            scForSecondArgument.setColumnExpression(colExpForSecondArgument);
            newFunctionArguments.add(scForFirstArgument);
            newFunctionArguments.add(scForSecondArgument);
            this.setFunctionArguments(newFunctionArguments);
            this.setOver(null);
            this.setOrderBy(null);
            this.setPartitionByClause(null);
            this.setWindowingClause(null);
            this.functionName.setColumnName("COALESCE");
        }
        else if (defaultValue != null && !defaultValue.trim().equalsIgnoreCase("null")) {
            boolean isDateArg = false;
            if (defaultValueColumn != null) {
                if (defaultValueColumn.getColumnExpression().get(0) instanceof FunctionCalls) {
                    final FunctionCalls dateFunc = defaultValueColumn.getColumnExpression().get(0);
                    if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                        isDateArg = true;
                    }
                }
                else if (defaultValueColumn.getColumnExpression().get(0) instanceof TableColumn) {
                    final TableColumn dateFunc2 = defaultValueColumn.getColumnExpression().get(0);
                    if (SwisSQLUtils.getFunctionReturnType(dateFunc2.getColumnName(), null).equalsIgnoreCase("date")) {
                        isDateArg = true;
                    }
                }
            }
            if (isDateArg) {
                final FunctionCalls fc2 = new FunctionCalls();
                final TableColumn newFunctName2 = new TableColumn();
                final Vector newFunctionArguments2 = new Vector();
                final SelectColumn scForFirstArgument2 = new SelectColumn();
                final Vector colExpForFirstArgument2 = new Vector();
                final SelectColumn scForSecondArgument2 = new SelectColumn();
                final Vector colExpForSecondArgument2 = new Vector();
                colExpForFirstArgument2.add(this.toTeradataSelect(to_sqs, from_sqs));
                scForFirstArgument2.setColumnExpression(colExpForFirstArgument2);
                colExpForSecondArgument2.add(defaultValueColumn.toTeradataSelect(null, null));
                scForSecondArgument2.setColumnExpression(colExpForSecondArgument2);
                newFunctionArguments2.add(scForFirstArgument2);
                newFunctionArguments2.add(scForSecondArgument2);
                this.setFunctionArguments(newFunctionArguments2);
                this.setOver(null);
                this.setOrderBy(null);
                this.setPartitionByClause(null);
                this.setWindowingClause(null);
                this.functionName.setColumnName("COALESCE");
                final Vector newArguments = new Vector();
                for (int k = 0; k < this.functionArguments.size(); ++k) {
                    final FunctionCalls castTimestamp = new FunctionCalls();
                    final TableColumn castTcn = new TableColumn();
                    castTcn.setColumnName("CAST");
                    castTimestamp.setFunctionName(castTcn);
                    castTimestamp.setAsDatatype("AS");
                    final DateClass castDatatype = new DateClass();
                    castDatatype.setDatatypeName("TIMESTAMP");
                    castDatatype.setOpenBrace("(");
                    castDatatype.setSize("0");
                    castDatatype.setClosedBrace(")");
                    final Vector castTimestampArgs = new Vector();
                    castTimestampArgs.add(this.functionArguments.get(k));
                    castTimestampArgs.add(castDatatype);
                    castTimestamp.setFunctionArguments(castTimestampArgs);
                    newArguments.add(castTimestamp);
                }
                this.setFunctionArguments(newArguments);
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
