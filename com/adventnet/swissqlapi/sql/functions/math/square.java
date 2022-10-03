package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class square extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POWER");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toOracleSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SQUARE");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toMSSQLServerSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SQUARE");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toSybaseSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POWER");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toDB2Select(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POW");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toPostgreSQLSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POW");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toMySQLSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POW");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toANSISelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POW");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toTeradataSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POW");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toInformixSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SQUARE");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                this.functionName.setColumnName("");
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                sc.toTimesTenSelect(to_sqs, from_sqs);
                final Vector colExpr = sc.getColumnExpression();
                if (colExpr.size() != 1 || !(colExpr.get(0) instanceof SelectColumn)) {
                    colExpr.add(0, "(");
                    colExpr.add(")");
                }
                final int n = colExpr.size();
                colExpr.add("*");
                for (int i = 0; i < n; ++i) {
                    colExpr.add(colExpr.get(i));
                }
                arguments.addElement(sc);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POW");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toNetezzaSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("POWER");
        final Vector arguments = new Vector();
        final Object obj = this.functionArguments.elementAt(0);
        if (obj instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)obj).toVectorWiseSelect(to_sqs, from_sqs));
        }
        else {
            arguments.addElement(obj);
        }
        arguments.addElement(new String("2"));
        this.setFunctionArguments(arguments);
    }
}
