package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class pi extends FunctionCalls
{
    private String piString;
    
    public pi() {
        this.piString = "3.141592653589793116";
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.addElement(this.piString);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.addElement(this.piString);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.addElement(this.piString);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.addElement(this.piString);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.addElement(this.piString);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        arguments.addElement("3.141592653589793");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("3.14159265358979");
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
