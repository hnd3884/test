package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.statement.select.OrderItem;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class groupConcat extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final int size = (this.separatorString != null) ? (this.functionArguments.size() - 1) : this.functionArguments.size();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selColumn = this.functionArguments.elementAt(i_count);
                selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(size);
                arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "";
        String newqry = "";
        final StringBuffer str = new StringBuffer();
        if (this.functionName.getColumnName().equalsIgnoreCase("group_concat")) {
            if (arguments.size() == 1) {
                String arg = "CAST(" + arguments.get(0).toString() + " AS TEXT)";
                if (this.argumentQualifier != null && this.argumentQualifier.equalsIgnoreCase("distinct")) {
                    arg = "DISTINCT " + arg;
                    this.argumentQualifier = null;
                }
                qry = " array_to_string(ARRAY(SELECT unnest(array_agg(" + arg + ")) ORDER BY 1),',')  ";
                newqry = "string_agg(" + arg + ",',')";
            }
            if (arguments.size() > 1) {
                String s = null;
                int l = 0;
                final String arg3;
                String arg2 = arg3 = "CAST(" + arguments.get(0).toString() + " AS TEXT)";
                boolean isDistinct = false;
                if (this.argumentQualifier != null && this.argumentQualifier.equalsIgnoreCase("distinct")) {
                    arg2 = "DISTINCT " + arg2;
                    isDistinct = true;
                    this.argumentQualifier = null;
                }
                final String orderByArg = this.getOrderByClause(isDistinct, arg3, to_sqs, from_sqs);
                if (this.separatorString != null) {
                    final String sepString = arguments.get(1).toString().contains("\\") ? ("E" + arguments.get(1).toString()) : arguments.get(1).toString();
                    qry = " array_to_string(ARRAY(SELECT unnest(array_agg(" + arg2 + ")) ORDER BY 1)," + arguments.get(1).toString() + ")  ";
                    newqry = "string_agg(" + arg2 + ", " + sepString + " " + orderByArg + ")";
                    this.separatorString = null;
                }
                else {
                    for (int i = 1; i < this.functionArguments.size(); ++i) {
                        str.append("CAST(" + arguments.get(i) + " AS TEXT)");
                        str.append(" || ");
                    }
                    if (str.toString().endsWith(" || ")) {
                        l = str.toString().length() - " || ".length();
                        s = str.toString().substring(0, l);
                    }
                    qry = " array_to_string(ARRAY(SELECT unnest(array_agg(" + arg2 + " || " + s + ")) ORDER BY 1),',')  ";
                    newqry = "string_agg(" + arg2 + " || " + s + ", ',' " + orderByArg + ")";
                }
            }
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                this.functionName.setColumnName(qry);
            }
            else {
                this.functionName.setColumnName(newqry);
            }
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    public String getOrderByClause(final boolean isDistinct, final String argument, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        String orderBy = "";
        try {
            if (this.obs != null && this.obs.getOrderItemList() != null && this.obs.getOrderItemList().get(0) instanceof OrderItem) {
                String order = this.obs.getOrderItemList().get(0).getOrder();
                if (order == null || order.equalsIgnoreCase("null")) {
                    order = "";
                }
                if (isDistinct) {
                    orderBy = "ORDER BY " + argument + " " + order;
                }
                else {
                    orderBy = this.obs.toPostgreSQLSelect(to_sqs, from_sqs).toString();
                }
            }
        }
        catch (final Exception ex) {}
        finally {
            this.obs = null;
        }
        return orderBy;
    }
}
