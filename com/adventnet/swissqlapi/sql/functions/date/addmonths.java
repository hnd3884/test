package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class addmonths extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        this.functionName.setColumnName("DATEADD");
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
        if (this.functionArguments.size() == 2) {
            this.functionArguments.addElement(this.functionArguments.get(0));
            this.functionArguments.setElementAt("M", 0);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATEADD");
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
        if (this.functionArguments.size() == 2) {
            this.functionArguments.addElement(this.functionArguments.get(0));
            this.functionArguments.setElementAt("MM", 0);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() == 2) {
            final SelectColumn sc = new SelectColumn();
            final Vector colExp = new Vector();
            colExp.add(this.functionArguments.get(0));
            String isSign = new String();
            final Object obj = this.functionArguments.get(1).getColumnExpression().get(0);
            if (obj instanceof TableColumn) {
                final TableColumn tableColumn = (TableColumn)obj;
                isSign = tableColumn.toString();
            }
            else if (obj instanceof String) {
                isSign = (String)obj;
            }
            if ("-".equals(isSign)) {
                colExp.add("-");
                colExp.add(this.functionArguments.get(1).getColumnExpression().get(1) + "  MONTHS");
            }
            else {
                colExp.add("+");
                colExp.add(obj + "  MONTHS");
            }
            sc.setColumnExpression(colExp);
            this.functionArguments.setElementAt(sc, 0);
            this.functionArguments.setSize(1);
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
        if (!SwisSQLOptions.passFunctionsWithOutThrowingConvertException) {
            throw new ConvertException("ADD_MONTHS function is not supported in PostgreSQL");
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE_ADD");
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
        if (this.functionArguments.size() == 2) {
            final SelectColumn sc = new SelectColumn();
            final Vector colExp = new Vector();
            colExp.add(" INTERVAL ");
            String isSign = new String();
            if (this.functionArguments.get(1).getColumnExpression().get(0) instanceof TableColumn) {
                final TableColumn tableColumn = this.functionArguments.get(1).getColumnExpression().get(0);
                isSign = tableColumn.toString();
            }
            else {
                isSign = this.functionArguments.get(1).getColumnExpression().get(0);
            }
            if ("-".equals(isSign)) {
                colExp.add(this.functionArguments.get(1).getColumnExpression().get(1) + "  MONTHS");
                this.functionName.setColumnName("DATE_SUB");
            }
            else {
                colExp.add(this.functionArguments.get(1).getColumnExpression().get(0) + "  MONTH");
            }
            sc.setColumnExpression(colExp);
            this.functionArguments.setElementAt(sc, 1);
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        if (this.functionArguments.size() == 2) {
            final SelectColumn sc = new SelectColumn();
            final Vector colExp = new Vector();
            colExp.add(this.functionArguments.get(0));
            String isSign = new String();
            if (this.functionArguments.get(1).getColumnExpression().get(0) instanceof TableColumn) {
                final TableColumn tableColumn = this.functionArguments.get(1).getColumnExpression().get(0);
                isSign = tableColumn.toString();
            }
            else {
                isSign = this.functionArguments.get(1).getColumnExpression().get(0);
            }
            if ("-".equals(isSign)) {
                colExp.add("-");
                colExp.add(this.functionArguments.get(1).getColumnExpression().get(1) + "  UNITS MONTH");
            }
            else {
                colExp.add("+");
                colExp.add(this.functionArguments.get(1).getColumnExpression().get(0) + "  UNITS MONTH");
            }
            sc.setColumnExpression(colExp);
            this.functionArguments.setElementAt(sc, 0);
            this.functionArguments.setSize(1);
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ADD_MONTHS");
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
}
