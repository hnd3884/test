package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class trigh extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
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
        final String fname = this.functionName.getColumnName();
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
        if (fname.equalsIgnoreCase("sinh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("cosh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("tanh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fname = this.functionName.getColumnName();
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
        if (fname.equalsIgnoreCase("sinh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("cosh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("tanh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fname = this.functionName.getColumnName();
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
        if (fname.equalsIgnoreCase("sinh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("cosh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("tanh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fname = this.functionName.getColumnName();
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
        if (fname.equalsIgnoreCase("sinh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("cosh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("tanh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fname = this.functionName.getColumnName();
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
        if (fname.equalsIgnoreCase("sinh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("cosh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("tanh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fname = this.functionName.getColumnName();
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
        if (fname.equalsIgnoreCase("sinh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("cosh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("tanh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fname = this.functionName.getColumnName();
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
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fname = this.functionName.getColumnName();
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
        if (fname.equalsIgnoreCase("sinh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("cosh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("tanh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fname = this.functionName.getColumnName();
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
        if (fname.equalsIgnoreCase("sinh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("cosh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("2");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
        else if (fname.equalsIgnoreCase("tanh")) {
            final TableColumn outerFunction = new TableColumn();
            outerFunction.setOwnerName(this.functionName.getOwnerName());
            outerFunction.setTableName(this.functionName.getTableName());
            outerFunction.setColumnName("");
            this.setFunctionName(outerFunction);
            final SelectColumn argument = new SelectColumn();
            final Object x = this.functionArguments.get(0);
            final Vector arg = new Vector();
            final FunctionCalls exp1 = new FunctionCalls();
            final FunctionCalls exp2 = new FunctionCalls();
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("EXP");
            exp1.setFunctionName(innerFunction);
            exp2.setFunctionName(innerFunction);
            final Vector argList1 = new Vector();
            final Vector argList2 = new Vector();
            argList1.add(x);
            argList2.add("-(" + x + ")");
            exp1.setFunctionArguments(argList1);
            exp2.setFunctionArguments(argList2);
            argument.setColumnExpression(new Vector());
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("-");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            argument.addColumnExpressionElement("/");
            argument.addColumnExpressionElement("(");
            argument.addColumnExpressionElement(exp1);
            argument.addColumnExpressionElement("+");
            argument.addColumnExpressionElement(exp2);
            argument.addColumnExpressionElement(")");
            arg.add(argument);
            this.setFunctionArguments(arg);
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
    }
}
