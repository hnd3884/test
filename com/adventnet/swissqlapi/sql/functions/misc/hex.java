package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class hex extends FunctionCalls
{
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
        if (this.functionName.getColumnName().equalsIgnoreCase("hex")) {
            this.functionName.setColumnName("encode(CAST(" + arguments.get(0).toString() + " AS TEXT),'hex')");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("unhex")) {
            this.functionName.setColumnName("encode(decode(CAST(" + arguments.get(0).toString() + " AS TEXT),'hex'),'escape')");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("mid")) {
            if (arguments.size() == 2) {
                this.functionName.setColumnName("substring((" + arguments.get(0).toString() + ")::text," + arguments.get(1).toString() + ")");
            }
            else {
                this.functionName.setColumnName("substring((" + arguments.get(0).toString() + ")::text," + arguments.get(1).toString() + "," + arguments.get(2).toString() + ")");
            }
        }
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
