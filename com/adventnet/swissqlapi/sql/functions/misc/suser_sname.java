package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class suser_sname extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
            this.functionName.setColumnName("SYS_CONTEXT");
            final Vector arguments = new Vector();
            arguments.add("'USERENV'");
            arguments.add("'MODULE'");
            this.setFunctionArguments(arguments);
        }
        else {
            this.functionName.setColumnName("");
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
            if (arguments.size() == 0) {
                arguments.add("USER");
                this.setFunctionArguments(arguments);
            }
            else if (arguments.size() == 1) {
                if (!arguments.get(0).toString().toLowerCase().equals("null")) {
                    final Vector newArg = new Vector();
                    final String selectString = "SELECT USERNAME FROM DBA_USERS WHERE USER_ID = " + arguments.get(0).toString();
                    newArg.add(selectString);
                    this.setFunctionArguments(newArg);
                }
                else {
                    this.functionName.setColumnName("");
                    this.setOpenBracesForFunctionNameRequired(false);
                }
            }
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
            this.functionName.setColumnName("SUSER_SNAME");
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
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
            this.functionName.setColumnName("SUSER_NAME");
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
            if (arguments.size() == 0) {
                arguments.add("SYSTEM_USER");
                this.setFunctionArguments(arguments);
            }
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
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
            arguments.add("USER");
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
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
            if (arguments.size() == 0) {
                arguments.add("CURRENT_USER");
                this.setFunctionArguments(arguments);
            }
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
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
            if (arguments.size() == 0) {
                arguments.add("USER");
                this.setFunctionArguments(arguments);
            }
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
            if (!this.functionName.getColumnName().equalsIgnoreCase("\"app_name\"")) {
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
                if (arguments.size() == 0) {
                    arguments.add("CURRENT_USER");
                    this.setFunctionArguments(arguments);
                }
            }
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
        if (arguments.size() == 0) {
            arguments.add("CURRENT_USER");
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
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
            if (arguments.size() == 0) {
                arguments.add("USER");
                this.setFunctionArguments(arguments);
            }
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
            if (this.functionArguments.size() != 0) {
                throw new ConvertException("\nUnsupported SQL in TimesTen\n");
            }
            this.functionName.setColumnName("");
            this.functionArguments.add("USER");
            this.setOpenBracesForFunctionNameRequired(false);
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (!this.functionName.getColumnName().equalsIgnoreCase("app_name")) {
            this.functionName.setColumnName("");
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
            if (arguments.size() == 0) {
                arguments.add("CURRENT_USER");
                this.setFunctionArguments(arguments);
            }
        }
    }
}
