package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class user extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("USER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("SYSTEM_USER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("SYSTEM_USER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("USER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("CURRENT_USER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("CURRENT_USER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("CURRENT_USER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("USER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.add("CURRENT_USER");
        this.setFunctionArguments(arguments);
    }
}
