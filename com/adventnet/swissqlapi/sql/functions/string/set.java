package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class set extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selCol = this.functionArguments.elementAt(i_count);
                if (i_count != 0 || (!this.functionName.getColumnName().equalsIgnoreCase("ELT") && !this.functionName.getColumnName().equalsIgnoreCase("MAKE_SET"))) {
                    selCol.convertSelectColumnToTextDataType();
                }
                arguments.addElement(selCol.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 2) {
            final String firstArgument = arguments.get(0).toString() + "::text";
            arguments.set(0, firstArgument);
            final String secondArg = arguments.get(1).toString() + "::text";
            arguments.set(1, secondArg);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs).toString());
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count).toString());
            }
        }
        if (arguments.size() > 20) {
            throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
        }
        final String searchStr = arguments.get(0).toString();
        final StringBuilder builder = new StringBuilder("(CASE ");
        if (this.functionName.getColumnName().equalsIgnoreCase("FIELD")) {
            builder.append("CAST(").append(searchStr).append(" AS VARCHAR)");
            for (int i = 1; i <= arguments.size() - 1; ++i) {
                builder.append(" WHEN ").append("CAST(").append(arguments.get(i).toString()).append(" AS VARCHAR)").append(" THEN ").append(String.valueOf(i));
            }
            builder.append("ELSE 0 END)");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("ELT")) {
            builder.append(searchStr);
            for (int i = 1; i <= arguments.size() - 1; ++i) {
                builder.append(" WHEN ").append(String.valueOf(i)).append(" THEN ").append("CAST(").append(arguments.get(i).toString()).append(" AS VARCHAR)");
            }
            builder.append("ELSE NULL END)");
        }
        this.functionName.setColumnName(builder.toString());
        this.setOpenBracesForFunctionNameRequired(false);
        this.setFunctionArguments(new Vector());
    }
}
