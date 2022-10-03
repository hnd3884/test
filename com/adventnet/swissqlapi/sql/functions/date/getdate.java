package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class getdate extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arg = new Vector();
        if (this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GETUTCDATE")) {
            this.functionName.setColumnName("SYS_EXTRACT_UTC");
            final SelectColumn sc = new SelectColumn();
            final Vector funcArg = new Vector();
            funcArg.add("SYSTIMESTAMP");
            sc.setColumnExpression(funcArg);
            arg.add(sc);
        }
        else {
            this.functionName.setColumnName("");
            arg.add("SYSDATE");
        }
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("now") || this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("sysdate")) {
            this.functionName.setColumnName("GETDATE");
            this.setFunctionArguments(new Vector());
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("now") || this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("sysdate")) {
            this.functionName.setColumnName("GETDATE");
            this.setFunctionArguments(new Vector());
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT TIMESTAMP");
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("now")) {
            return;
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("sysdate")) {
            this.functionName.setColumnName("NOW");
            return;
        }
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT_DATE");
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT_TIMESTAMP");
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT_TIMESTAMP");
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT");
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT_TIMESTAMP");
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT_TIMESTAMP");
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arg = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("now")) {
            this.functionName.setColumnName("CURRENT_TIMESTAMP");
        }
        else {
            this.functionName.setColumnName("CURRENT_DATE");
        }
        this.setOpenBracesForFunctionNameRequired(false);
        this.setFunctionArguments(arg);
    }
}
