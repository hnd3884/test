package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class DecInt extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final String fnStr = this.functionName.getColumnName().toUpperCase();
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String type = this.functionName.getColumnName().equalsIgnoreCase("TO_INTEGER") ? "SIGNED" : "DECIMAL";
        final Object col = arguments.elementAt(0);
        if (fnStr.equalsIgnoreCase("TO_DECIMAL")) {
            int precision = 0;
            int scale = 0;
            if (argLength > 1 && arguments.elementAt(1) != null && arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc_precision = arguments.elementAt(1);
                final Vector vc_precision = sc_precision.getColumnExpression();
                if (!(vc_precision.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function" + fnStr, "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr, "PRECISION" });
                }
                String precision_str = vc_precision.elementAt(0);
                precision_str = precision_str.replaceAll("'", "");
                try {
                    precision = Integer.parseInt(precision_str);
                }
                catch (final Exception e) {
                    throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "PRECISION", "Provide values between 1 to 38" });
                }
                if (precision < 1 || precision > 38) {
                    throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "PRECISION", "Provide values between 1 to 38" });
                }
            }
            if (argLength > 2 && arguments.elementAt(2) != null && arguments.elementAt(2) instanceof SelectColumn) {
                final SelectColumn sc_scale = arguments.elementAt(2);
                final Vector vc_scale = sc_scale.getColumnExpression();
                if (!(vc_scale.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function" + fnStr, "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr, "SCALE" });
                }
                String scale_str = vc_scale.elementAt(0);
                scale_str = scale_str.replaceAll("'", "");
                try {
                    scale = Integer.parseInt(scale_str);
                }
                catch (final Exception e) {
                    throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "SCALE", "Provide values as the difference of precision value and length of numeric value" });
                }
                if (scale >= precision) {
                    throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "SCALE", "Provide values as the difference of precision value and length of numeric value" });
                }
            }
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("TO_CURRENCY") || this.functionName.getColumnName().equalsIgnoreCase("TO_PERCENTAGE")) {
            type += "(19,4)";
        }
        else if (type.equalsIgnoreCase("DECIMAL")) {
            type = type + "(" + (Object)((argLength >= 2 && arguments.elementAt(1) != null) ? arguments.elementAt(1) : "38") + "," + (Object)((argLength >= 3 && arguments.elementAt(2) != null) ? arguments.elementAt(2) : "2") + ")";
        }
        this.functionName.setColumnName("CONVERT");
        arguments = new Vector();
        arguments.addElement(col);
        arguments.addElement(type);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        Vector arguments = new Vector();
        this.functionArguments.setElementAt("(" + this.functionArguments.get(0) + "::text)", 0);
        for (int i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final SelectColumn udfSC = new SelectColumn();
        final Vector udfSCColExp = new Vector();
        final FunctionCalls udf = new FunctionCalls();
        final Vector udfArgs = new Vector();
        udfArgs.add(arguments.elementAt(0));
        udf.setFunctionArguments(udfArgs);
        udfSCColExp.addElement(udf);
        udfSC.setColumnExpression(udfSCColExp);
        if (this.functionName.getColumnName().equalsIgnoreCase("TO_DECIMAL")) {
            final NumericClass decimalNC = new NumericClass();
            decimalNC.setDatatypeName("DECIMAL");
            decimalNC.setPrecision((argLength >= 2 && arguments.elementAt(1) != null) ? arguments.elementAt(1).toString() : "38");
            decimalNC.setScale((argLength >= 3 && arguments.elementAt(2) != null) ? arguments.elementAt(2).toString() : "2");
            decimalNC.setOpenBrace("(");
            decimalNC.setClosedBrace(")");
            udf.getFunctionName().setColumnName("todouble_udf");
            arguments = new Vector();
            arguments.add(udfSC);
            arguments.add(decimalNC);
        }
        else {
            udf.getFunctionName().setColumnName("tonumeric_udf");
            arguments = new Vector();
            arguments.add(udfSC);
            arguments.add("BIGINT");
        }
        this.functionName.setColumnName("cast");
        this.setAsDatatype("as");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];
        for (int i_count = 0; i_count < argLength; ++i_count) {
            argu[i_count] = new StringBuffer();
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                argu[i_count].append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                argu[i_count].append(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "";
        final String col = argu[0].toString();
        if (this.functionName.getColumnName().equalsIgnoreCase("TO_DECIMAL")) {
            final String precision = (argLength >= 2 && argu[1].toString() != null) ? argu[1].toString() : "38";
            final String scale = (argLength >= 3 && argu[2].toString() != null) ? argu[2].toString() : "2";
            qry = "IF(" + col + " IS DECIMAL, CAST(" + col + " AS DECIMAL(" + precision + ", " + scale + ")),0)";
        }
        else {
            qry = "IF(" + col + " IS INTEGER, BIGINT(" + col + "),0) ";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
