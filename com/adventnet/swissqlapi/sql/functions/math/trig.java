package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class trig extends FunctionCalls
{
    CaseStatement caseStatement;
    
    public trig() {
        this.caseStatement = null;
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
            final String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
            arguments = new Vector();
            arguments.add(argumentsNew);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (from_sqs != null && from_sqs.isMSAzure() && (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS"))) {
            final String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
            arguments = new Vector();
            arguments.add(argumentsNew);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
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
        this.functionName.getColumnName().toUpperCase();
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
        this.functionName.getColumnName().toUpperCase();
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
            final String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
            arguments = new Vector();
            arguments.add(argumentsNew);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
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
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
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
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("SIGN")) {
            (this.caseStatement = new CaseStatement()).setCaseClause("CASE");
            final Vector whenStmtList = new Vector();
            for (int i = 0; i < 2; ++i) {
                final WhenStatement when_statement = new WhenStatement();
                when_statement.setWhenClause("WHEN");
                when_statement.setThenClause("THEN");
                final WhereItem wi = new WhereItem();
                final WhereColumn wc = new WhereColumn();
                final Vector wcColExp = new Vector();
                wcColExp.add(this.functionArguments.elementAt(0).toTeradataSelect(to_sqs, from_sqs));
                wc.setColumnExpression(wcColExp);
                wi.setLeftWhereExp(wc);
                if (i == 0) {
                    wi.setOperator("<");
                }
                else {
                    wi.setOperator(">");
                }
                final WhereColumn rwc = new WhereColumn();
                final Vector rwcColExp = new Vector();
                rwcColExp.add("0");
                rwc.setColumnExpression(rwcColExp);
                wi.setRightWhereExp(rwc);
                final WhereExpression we = new WhereExpression();
                we.addWhereItem(wi);
                when_statement.setWhenCondition(we);
                final SelectColumn thenSelectColumn = new SelectColumn();
                final Vector thenSelectColumnExpr = new Vector();
                if (i == 0) {
                    thenSelectColumnExpr.add("-1");
                }
                else {
                    thenSelectColumnExpr.add("1");
                }
                thenSelectColumn.setColumnExpression(thenSelectColumnExpr);
                when_statement.setThenStatement(thenSelectColumn);
                whenStmtList.add(when_statement);
            }
            this.caseStatement.setWhenStatementList(whenStmtList);
            this.caseStatement.setElseClause("ELSE");
            final SelectColumn elseSelectColumn = new SelectColumn();
            final Vector elseSelectColumnExpr = new Vector();
            elseSelectColumnExpr.add("0");
            elseSelectColumn.setColumnExpression(elseSelectColumnExpr);
            this.caseStatement.setElseStatement(elseSelectColumn);
            this.caseStatement.setEndClause("END");
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
        else {
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
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
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
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("SIGN")) {
            final Vector colExpr = this.functionArguments.get(0).getColumnExpression();
            if (colExpr.size() == 1 && colExpr.get(0) instanceof String) {
                this.functionName.setColumnName("");
                this.setOpenBracesForFunctionNameRequired(false);
                final String str = colExpr.get(0).toString();
                final int value = Integer.parseInt(str);
                if (value > 0) {
                    arguments.add("1");
                }
                else {
                    arguments.add("0");
                }
                this.setFunctionArguments(arguments);
            }
            else {
                if (colExpr.size() != 2) {
                    throw new ConvertException("\nThe function SIGN is not supported in TimesTen 5.1.21\n");
                }
                this.functionName.setColumnName("");
                this.setOpenBracesForFunctionNameRequired(false);
                arguments.add("-1");
                this.setFunctionArguments(arguments);
            }
        }
        else {
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
                    throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
                }
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                final Vector colExpr2 = sc.getColumnExpression();
                if (colExpr2.size() != 1 || !(colExpr2.get(0) instanceof String)) {
                    throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
                }
                if (this.functionName.getColumnName().equalsIgnoreCase("ATAN") || this.functionName.getColumnName().equalsIgnoreCase("ATN2")) {
                    arguments.add(Math.atan(Double.parseDouble(colExpr2.get(0).toString())) + "");
                }
                else if (this.functionName.getColumnName().equalsIgnoreCase("ASIN")) {
                    arguments.add(Math.asin(Double.parseDouble(colExpr2.get(0).toString())) + "");
                }
                else if (this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
                    arguments.add(Math.acos(Double.parseDouble(colExpr2.get(0).toString())) + "");
                }
                else if (this.functionName.getColumnName().equalsIgnoreCase("SIN")) {
                    arguments.add(Math.sin(Double.parseDouble(colExpr2.get(0).toString())) + "");
                }
                else if (this.functionName.getColumnName().equalsIgnoreCase("COS")) {
                    arguments.add(Math.cos(Double.parseDouble(colExpr2.get(0).toString())) + "");
                }
                else if (this.functionName.getColumnName().equalsIgnoreCase("TAN")) {
                    arguments.add(Math.tan(Double.parseDouble(colExpr2.get(0).toString())) + "");
                }
                this.functionName.setColumnName("");
                this.setFunctionArguments(arguments);
                this.setOpenBracesForFunctionNameRequired(false);
            }
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.getColumnName().toUpperCase();
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
    public String toString() {
        if (this.caseStatement != null) {
            return this.caseStatement.toString();
        }
        return super.toString();
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("sin")) {
            this.functionName.setColumnName("SIN");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("cos")) {
            this.functionName.setColumnName("COS");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("tan")) {
            this.functionName.setColumnName("TAN");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("asin")) {
            this.functionName.setColumnName("ASIN");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("acos")) {
            this.functionName.setColumnName("ACOS");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("atan")) {
            this.functionName.setColumnName("ATAN");
        }
        else {
            if (!this.functionName.getColumnName().equalsIgnoreCase("sign")) {
                throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
            }
            this.functionName.setColumnName("SIGN");
        }
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("ASIN") || this.functionName.getColumnName().equalsIgnoreCase("ACOS")) {
            final String argumentsNew = "CASE WHEN " + arguments.get(0).toString() + " BETWEEN -1 AND 1 THEN " + arguments.get(0).toString() + " ELSE NULL END";
            arguments = new Vector();
            arguments.add(argumentsNew);
        }
        this.setFunctionArguments(arguments);
    }
}
