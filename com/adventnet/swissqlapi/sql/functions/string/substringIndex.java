package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class substringIndex extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("substring_index");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count != 2) {
                    sc.convertSelectColumnToTextDataType();
                }
                arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 3) {
            String thirdArg = arguments.get(2).toString() + "::integer";
            final Integer number = StringFunctions.getIntegerValue(arguments.get(2).toString());
            if (number != null) {
                thirdArg = number.toString();
            }
            else if (from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForNumeric()) {
                thirdArg = "TOINTEGER_UDF(" + arguments.get(2).toString() + ")";
            }
            arguments.set(2, thirdArg);
        }
        this.setFunctionArguments(arguments);
    }
}
