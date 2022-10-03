package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class sys_context extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String fnName = this.functionName.getColumnName();
        this.functionName.setColumnName("SYS_CONTEXT");
        if (fnName.equalsIgnoreCase("db_id")) {
            if (this.functionArguments.size() == 0) {
                arguments.add("'USERENV'");
                arguments.add("'CURRENT_SCHEMAID'");
            }
            else {
                this.functionName.setColumnName("");
                arguments.add("SELECT user_id SCHEMA_ID FROM all_users WHERE username = " + this.functionArguments.get(0).toString().toUpperCase());
            }
        }
        else if (fnName.equalsIgnoreCase("db_name") && this.functionArguments.size() == 0) {
            arguments.add("'USERENV'");
            arguments.add("'CURRENT_SCHEMA'");
        }
        else if (fnName.equalsIgnoreCase("host_id") && this.functionArguments.size() == 0) {
            arguments.add("'USERENV'");
            arguments.add("'CLIENT_IDENTIFIER'");
        }
        else if (fnName.equalsIgnoreCase("host_name") && this.functionArguments.size() == 0) {
            arguments.add("'USERENV'");
            arguments.add("'HOST'");
        }
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
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
}
