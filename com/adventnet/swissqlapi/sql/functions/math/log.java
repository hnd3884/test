package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class log extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("log10")) {
            this.functionName.setColumnName("LOG");
            arguments.add("10");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("log2")) {
            this.functionName.setColumnName("LOG");
            arguments.add("2");
        }
        else {
            this.functionName.setColumnName("LN");
        }
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
            if (i == 1) {
                this.functionName.setColumnName("LOG");
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        if (from_sqs != null && from_sqs.isMSAzure()) {
            if (this.functionArguments.size() == 2) {
                final String argument = "CASE WHEN " + arguments.get(1).toString() + " <= 0 THEN NULL ELSE " + arguments.get(1).toString() + " END";
                arguments.set(1, argument);
            }
            else {
                final String argument = "CASE WHEN " + arguments.get(0).toString() + " <= 0 THEN NULL ELSE " + arguments.get(0).toString() + " END";
                arguments.set(0, argument);
            }
            if (this.functionName.toString().equalsIgnoreCase("LOG")) {
                this.functionName.setColumnName("LOG");
                this.setFunctionArguments(arguments);
            }
            else if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
                this.functionName.setColumnName("LOG");
                this.functionArguments.addElement(2);
                this.setFunctionArguments(this.functionArguments);
            }
        }
        else {
            this.functionName.setColumnName("LOG");
            this.setFunctionArguments(arguments);
            if (arguments.size() == 2) {
                this.functionName.setColumnName("LOG10");
                if (arguments.get(0) instanceof SelectColumn) {
                    final SelectColumn sc = arguments.get(0);
                    if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
                        final Vector exp = sc.getColumnExpression();
                        if (exp.get(0) instanceof String) {
                            final String longValue = exp.get(0).toString();
                            if (longValue.equals("10")) {
                                arguments.remove(0);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LOG");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (arguments.size() == 2) {
            this.functionName.setColumnName("LOG10");
            if (arguments.get(0) instanceof SelectColumn) {
                final SelectColumn sc = arguments.get(0);
                if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
                    final Vector exp = sc.getColumnExpression();
                    if (exp.get(0) instanceof String) {
                        final String longValue = exp.get(0).toString();
                        if (longValue.equals("10")) {
                            arguments.remove(0);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LOG");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (arguments.size() == 2) {
            this.functionName.setColumnName("LOG10");
            if (arguments.get(0) instanceof SelectColumn) {
                final SelectColumn sc = arguments.get(0);
                if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
                    final Vector exp = sc.getColumnExpression();
                    if (exp.get(0) instanceof String) {
                        final String longValue = exp.get(0).toString();
                        if (longValue.equals("10")) {
                            arguments.remove(0);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        if (this.functionArguments.size() == 2) {
            final String argument = "CASE WHEN " + arguments.get(1).toString() + " <= 0 THEN NULL ELSE " + arguments.get(1).toString() + " END";
            arguments.set(1, argument);
        }
        else {
            final String argument = "CASE WHEN " + arguments.get(0).toString() + " <= 0 THEN NULL ELSE " + arguments.get(0).toString() + " END";
            arguments.set(0, argument);
        }
        if (this.functionName.toString().equalsIgnoreCase("LOG")) {
            this.functionName.setColumnName("LOG");
            this.setFunctionArguments(arguments);
        }
        else if (this.functionName.toString().equalsIgnoreCase("LOG10")) {
            this.functionName.setColumnName("LOG");
            final Object temp = arguments.get(0);
            this.functionArguments.set(0, 10);
            this.functionArguments.add(1, temp);
            this.setFunctionArguments(this.functionArguments);
        }
        else if (this.functionName.toString().equalsIgnoreCase("LOG2")) {
            this.functionName.setColumnName("LOG");
            final Object temp = arguments.get(0);
            this.functionArguments.set(0, 2);
            this.functionArguments.add(1, temp);
            this.setFunctionArguments(this.functionArguments);
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().trim().equalsIgnoreCase("LOG10")) {
            this.functionName.setColumnName("LOG10");
        }
        else {
            this.functionName.setColumnName("LOG");
        }
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LOG");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (arguments.size() == 2) {
            this.functionName.setColumnName("LOG10");
            if (arguments.get(0) instanceof SelectColumn) {
                final SelectColumn sc = arguments.get(0);
                if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
                    final Vector exp = sc.getColumnExpression();
                    if (exp.get(0) instanceof String) {
                        final String longValue = exp.get(0).toString();
                        if (longValue.equals("10")) {
                            arguments.remove(0);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LOG");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (arguments.size() == 2) {
            this.functionName.setColumnName("LOG10");
            if (arguments.get(0) instanceof SelectColumn) {
                final SelectColumn sc = arguments.get(0);
                if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
                    final Vector exp = sc.getColumnExpression();
                    if (exp.get(0) instanceof String) {
                        final String longValue = exp.get(0).toString();
                        if (longValue.equals("10")) {
                            arguments.remove(0);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("LOG")) {
            this.functionName.setColumnName("LOGN");
        }
        else {
            this.functionName.setColumnName("LOG10");
        }
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
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                final Vector colExpr = sc.getColumnExpression();
                if (colExpr.size() == 1 && colExpr.get(0) instanceof String) {
                    this.functionName.setColumnName("");
                    this.setOpenBracesForFunctionNameRequired(false);
                    arguments.add(Math.log(Double.parseDouble(colExpr.get(0).toString())) + "");
                }
                else {
                    if (this.functionName.getColumnName().equalsIgnoreCase("LOG")) {
                        throw new ConvertException("\nThe function LOG is not supported in TimesTen 5.1.21\n");
                    }
                    if (this.functionName.getColumnName().equalsIgnoreCase("LOG10")) {
                        throw new ConvertException("\nThe function LOG10 is not supported in TimesTen 5.1.21\n");
                    }
                }
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LOG");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (arguments.size() == 2) {
            this.functionName.setColumnName("LOG");
            if (arguments.get(0) instanceof SelectColumn) {
                final SelectColumn sc = arguments.get(0);
                if (sc.getColumnExpression() != null && !sc.getColumnExpression().isEmpty()) {
                    final Vector exp = sc.getColumnExpression();
                    if (exp.get(0) instanceof String) {
                        final String longValue = exp.get(0).toString();
                        if (longValue.equals("10")) {
                            arguments.remove(0);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName().trim();
        String qry = "";
        Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        if (arguments.size() == 2) {
            final String argument_1 = "IF(" + arguments.get(0).toString() + "<=0, NULL, " + arguments.get(0).toString() + ")";
            final String argument_2 = "IF(" + arguments.get(1).toString() + "<=0, NULL, " + arguments.get(0).toString() + ")";
            arguments = new Vector();
            arguments.add(argument_1);
            arguments.add(argument_2);
            qry = "(log(" + arguments.get(1).toString() + ") / log(" + arguments.get(0).toString() + "))";
        }
        else if (fnStr.equalsIgnoreCase("LOG10")) {
            qry = "(log(IF(" + arguments.get(0).toString() + "<=0, NULL, " + arguments.get(0).toString() + ")) / log(10))";
        }
        else if (fnStr.equalsIgnoreCase("LOG2")) {
            qry = "(log(IF(" + arguments.get(0).toString() + "<=0, NULL, " + arguments.get(0).toString() + ")) / log(2))";
        }
        this.functionName.setColumnName(qry);
        this.setFunctionArguments(new Vector());
    }
}
