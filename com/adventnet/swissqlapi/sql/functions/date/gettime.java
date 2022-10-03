package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class gettime extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().toLowerCase().equalsIgnoreCase("now")) {
            return;
        }
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT_TIME");
        this.setFunctionArguments(arg);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arg = new Vector();
        arg.add("CURRENT_TIME");
        this.setFunctionArguments(arg);
    }
}
