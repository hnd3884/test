package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class unixTimestamp extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selColumn = this.functionArguments.elementAt(i_count);
                if (selColumn.getColumnExpression() != null && !selColumn.getColumnExpression().isEmpty() && selColumn.getColumnExpression().size() == 1 && selColumn.getColumnExpression().get(0) != null && selColumn.getColumnExpression().get(0) instanceof String) {
                    String stringValue = selColumn.getColumnExpression().get(0).toString();
                    stringValue = "CAST(" + this.handleStringLiteralForDateTime(stringValue, from_sqs) + " AS TIMESTAMP)";
                    selColumn.getColumnExpression().set(0, stringValue);
                }
                arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String arg = "";
        if (arguments.size() > 0) {
            arg = arguments.get(0).toString();
        }
        else {
            arg = " now() ";
        }
        final String qry = " cast(EXTRACT(EPOCH from " + arg + ") as bigint) ";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
