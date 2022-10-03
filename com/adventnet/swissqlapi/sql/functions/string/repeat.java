package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class repeat extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final Object obj = this.functionArguments.get(0);
        String arg1 = obj.toString();
        if (obj instanceof SelectColumn) {
            final SelectColumn arg2 = this.functionArguments.get(1);
            if (arg2.getColumnExpression().get(0) instanceof String) {
                try {
                    for (int repeatCount = Integer.parseInt(arg2.getColumnExpression().get(0)), i = 0; i < repeatCount - 1; ++i) {
                        arg1 = arg1 + "||" + obj.toString();
                    }
                }
                catch (final Exception ex) {}
            }
        }
        this.functionArguments.setElementAt(arg1, 0);
        this.functionArguments.setSize(1);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPLICATE");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPLICATE");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPEAT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count == 0) {
                    sc.convertSelectColumnToTextDataType();
                }
                arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionName.toString().equalsIgnoreCase("group_concat")) {
            final String qry = " array_to_string(ARRAY(SELECT unnest(array_agg(" + arguments.get(0) + ")) ORDER BY 1),',')  ";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else {
            this.functionName.setColumnName("REPEAT");
            if (arguments.size() == 2) {
                String secArgument = arguments.get(1).toString();
                final Integer number = StringFunctions.getIntegerValue(secArgument);
                if (number != null) {
                    secArgument = number.toString();
                }
                else if (from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForNumeric()) {
                    secArgument = "TOINTEGER_UDF(" + secArgument + ")";
                }
                else {
                    secArgument = "ROUND(" + secArgument + ")::integer";
                }
                arguments.set(1, secArgument);
            }
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPEAT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPEAT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPEAT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final Object obj = this.functionArguments.get(0);
        String arg1 = obj.toString();
        if (obj instanceof SelectColumn) {
            final SelectColumn arg2 = this.functionArguments.get(1);
            for (int repeatCount = Integer.parseInt(arg2.getColumnExpression().get(0)), i = 0; i < repeatCount - 1; ++i) {
                arg1 = arg1 + "||" + obj.toString();
            }
        }
        this.functionArguments.setElementAt(arg1, 0);
        this.functionArguments.setSize(1);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPEAT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().trim().equalsIgnoreCase("REPEAT")) {
            final Vector arguments = new Vector();
            this.functionName.setColumnName("RPAD");
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.functionArguments.elementAt(i_count);
                    if (i_count == 0) {
                        sc.convertSelectColumnToTextDataType();
                    }
                    arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            final String lengthArg = "LENGTH(" + arguments.get(0).toString() + ")";
            final String secArg = lengthArg + " + (" + lengthArg + " * CAST(" + arguments.get(1).toString() + " AS BIGINT))";
            arguments.setElementAt(secArg, 1);
            arguments.add(arguments.get(0));
            this.setFunctionArguments(arguments);
        }
        else if (this.functionName.getColumnName().trim().equalsIgnoreCase("group_concat")) {
            final Vector arguments = new Vector();
            final StringBuffer str = new StringBuffer();
            this.functionName.setColumnName("listagg");
            str.append("concat(");
            String separateString = "','";
            int size;
            int i_count2;
            for (size = ((this.separatorString != null) ? (this.functionArguments.size() - 1) : this.functionArguments.size()), i_count2 = 0; i_count2 < size; ++i_count2) {
                if (i_count2 > 0) {
                    str.append(",");
                }
                str.append("cast(");
                if (this.functionArguments.elementAt(i_count2) instanceof SelectColumn) {
                    final SelectColumn selColumn = this.functionArguments.elementAt(i_count2);
                    selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(size);
                    str.append(selColumn.toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    str.append(this.functionArguments.elementAt(i_count2));
                }
                str.append(" as varchar)");
            }
            str.append(")");
            if (this.separatorString != null && i_count2 + 1 == this.functionArguments.size() && this.functionArguments.get(i_count2) instanceof String) {
                separateString = this.functionArguments.elementAt(i_count2).toString();
            }
            arguments.addElement(str);
            arguments.addElement(separateString);
            this.separatorString = null;
            this.setFunctionArguments(arguments);
            String orderByWithinGroup = "";
            if (this.obs != null) {
                if (this.argumentQualifier == null) {
                    final OrderByStatement vwObs = this.obs.toVectorWiseSelect(to_sqs, from_sqs);
                    orderByWithinGroup = "WITHIN GROUP (" + vwObs.toString() + ")";
                }
                else {
                    orderByWithinGroup = "WITHIN GROUP ( ORDER BY " + (Object)str + " ASC NULLS LAST)";
                }
                this.obs = null;
            }
            final String argQualifier = (this.argumentQualifier != null) ? this.argumentQualifier : "";
            this.functionName.setColumnName("LISTAGG(" + argQualifier + " " + (Object)str + "," + separateString + ") " + orderByWithinGroup);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
            this.setArgumentQualifier(null);
        }
        else if (this.functionName.getColumnName().trim().equalsIgnoreCase("substring_index")) {
            final Vector arguments = new Vector();
            this.functionName.setColumnName("substring_index");
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.functionArguments.elementAt(i_count);
                    if (i_count < this.functionArguments.size() - 1) {
                        sc.convertSelectColumnToTextDataType();
                    }
                    arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            this.setFunctionArguments(arguments);
        }
    }
}
