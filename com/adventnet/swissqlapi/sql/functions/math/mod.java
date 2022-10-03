package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class mod extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MOD");
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
        this.functionName.setColumnName("");
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
        final Vector newArguments = new Vector();
        final SelectColumn newArgument = new SelectColumn();
        final Vector colExp = new Vector();
        colExp.addElement("(");
        colExp.addElement(arguments.get(0));
        colExp.addElement(") % (");
        colExp.addElement(arguments.get(1));
        colExp.addElement(")");
        newArgument.setColumnExpression(colExp);
        newArguments.add(newArgument);
        this.setFunctionArguments(newArguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        final Vector newArguments = new Vector();
        final SelectColumn newArgument = new SelectColumn();
        final Vector colExp = new Vector();
        colExp.addElement("(");
        colExp.addElement(arguments.get(0));
        colExp.addElement(") % (");
        colExp.addElement(arguments.get(1));
        colExp.addElement(")");
        newArgument.setColumnExpression(colExp);
        newArguments.add(newArgument);
        this.setFunctionArguments(newArguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MOD");
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
        this.functionName.setColumnName("MOD");
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
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MOD");
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
        this.functionName.setColumnName("MOD");
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
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                try {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                    continue;
                }
                catch (final ConvertException ce) {
                    throw ce;
                }
            }
            arguments.addElement(this.functionArguments.elementAt(i_count));
        }
        this.functionName.setColumnName("");
        this.setFunctionArguments(arguments);
        final Vector newArguments = new Vector();
        final SelectColumn newArgument = new SelectColumn();
        final Vector colExp = new Vector();
        colExp.addElement("(");
        colExp.addElement(arguments.get(0));
        colExp.addElement(") MOD (");
        colExp.addElement(arguments.get(1));
        colExp.addElement(")");
        newArgument.setColumnExpression(colExp);
        newArguments.add(newArgument);
        this.setFunctionArguments(newArguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MOD");
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
        this.functionName.setColumnName("MOD");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTimesTenSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MOD");
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
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MOD");
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
    }
}
