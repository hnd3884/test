package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class nvl2 extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("NVL2");
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
        if (this.functionArguments.size() == 3) {
            final StringBuffer argument = new StringBuffer();
            argument.append("CASE WHEN ");
            argument.append(this.functionArguments.get(0).toString() + " ");
            argument.append(" IS NULL THEN ");
            argument.append(this.functionArguments.get(2).toString() + " ");
            argument.append(" ELSE ");
            argument.append(this.functionArguments.get(1).toString() + " ");
            argument.append("END");
            final Vector arg = new Vector();
            arg.addElement(argument.toString());
            this.functionArguments = arg;
        }
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
        if (this.functionArguments.size() == 3) {
            final StringBuffer argument = new StringBuffer();
            argument.append("CASE WHEN ");
            argument.append(this.functionArguments.get(0).toString() + " ");
            argument.append(" IS NULL THEN ");
            argument.append(this.functionArguments.get(2).toString() + " ");
            argument.append(" ELSE ");
            argument.append(this.functionArguments.get(1).toString() + " ");
            argument.append("END");
            final Vector arg = new Vector();
            arg.addElement(argument.toString());
            this.functionArguments = arg;
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
        if (this.functionArguments.size() == 3) {
            final StringBuffer argument = new StringBuffer();
            argument.append("CASE WHEN ");
            argument.append(this.functionArguments.get(0).toString() + " ");
            argument.append(" IS NULL THEN ");
            argument.append(this.functionArguments.get(2).toString() + " ");
            argument.append(" ELSE ");
            argument.append(this.functionArguments.get(1).toString() + " ");
            argument.append("END");
            final Vector arg = new Vector();
            arg.addElement(argument.toString());
            this.functionArguments = arg;
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() == 3) {
            final StringBuffer argument = new StringBuffer();
            argument.append("CASE WHEN ");
            argument.append(this.functionArguments.get(0).toString() + " ");
            argument.append(" IS NULL THEN ");
            argument.append(this.functionArguments.get(2).toString() + " ");
            argument.append(" ELSE ");
            argument.append(this.functionArguments.get(1).toString() + " ");
            argument.append("END");
            final Vector arg = new Vector();
            arg.addElement(argument.toString());
            this.functionArguments = arg;
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() == 3) {
            final StringBuffer argument = new StringBuffer();
            argument.append("CASE WHEN ");
            argument.append(this.functionArguments.get(0).toString() + " ");
            argument.append(" IS NULL THEN ");
            argument.append(this.functionArguments.get(2).toString() + " ");
            argument.append(" ELSE ");
            argument.append(this.functionArguments.get(1).toString() + " ");
            argument.append("END");
            final Vector arg = new Vector();
            arg.addElement(argument.toString());
            this.functionArguments = arg;
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() == 3) {
            final StringBuffer argument = new StringBuffer();
            argument.append("CASE WHEN ");
            argument.append(this.functionArguments.get(0).toString() + " ");
            argument.append(" IS NULL THEN ");
            argument.append(this.functionArguments.get(2).toString() + " ");
            argument.append(" ELSE ");
            argument.append(this.functionArguments.get(1).toString() + " ");
            argument.append("END");
            final Vector arg = new Vector();
            arg.addElement(argument.toString());
            this.functionArguments = arg;
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() == 3) {
            final StringBuffer argument = new StringBuffer();
            argument.append("CASE WHEN ");
            argument.append(this.functionArguments.get(0).toString() + " ");
            argument.append(" IS NULL THEN ");
            argument.append(this.functionArguments.get(2).toString() + " ");
            argument.append(" ELSE ");
            argument.append(this.functionArguments.get(1).toString() + " ");
            argument.append("END");
            final Vector arg = new Vector();
            arg.addElement(argument.toString());
            this.functionArguments = arg;
        }
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() == 3) {
            final StringBuffer argument = new StringBuffer();
            argument.append("DECODE (");
            argument.append(this.functionArguments.get(0).toString() + " ");
            argument.append(", NULL, ");
            argument.append(this.functionArguments.get(2).toString() + " ");
            argument.append(", ");
            argument.append(this.functionArguments.get(1).toString() + " ");
            argument.append(")");
            final Vector arg = new Vector();
            arg.addElement(argument.toString());
            this.functionArguments = arg;
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("NVL2");
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
}
