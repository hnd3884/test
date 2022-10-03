package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class NumToYMInterval extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        if (this.functionArguments.size() == 2) {
            final Vector newArguments = new Vector();
            String format = "";
            boolean isNumber = false;
            String period = "";
            String sign = "";
            if (this.functionArguments.get(1) instanceof SelectColumn) {
                final SelectColumn tempSC2 = this.functionArguments.get(1);
                final Vector columnExp2 = tempSC2.getColumnExpression();
                if (columnExp2.get(0) instanceof String) {
                    final String temp = columnExp2.get(0);
                    if (temp.trim().startsWith("'") && temp.trim().endsWith("'")) {
                        format = temp.replaceAll("'", "");
                    }
                }
            }
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn tempSC3 = this.functionArguments.get(0);
                final Vector columnExp3 = tempSC3.getColumnExpression();
                if (columnExp3.get(0) instanceof String) {
                    period = columnExp3.get(0);
                    if (columnExp3.size() == 2 && columnExp3.get(1) instanceof String && (period.trim().equals("-") || period.trim().equals("+"))) {
                        sign = columnExp3.get(0);
                        final String periodTemp = period = columnExp3.get(1);
                    }
                    if (period.indexOf("'") != -1) {
                        period = period.replaceAll("'", "");
                    }
                    try {
                        final int tempPeriod = Integer.parseInt(period);
                        isNumber = true;
                    }
                    catch (final NumberFormatException ne) {
                        isNumber = false;
                    }
                }
            }
            if (isNumber && (format.trim().equalsIgnoreCase("month") || format.trim().equalsIgnoreCase("year"))) {
                String year = "00";
                String month = "00";
                if (format.trim().equalsIgnoreCase("year")) {
                    final int length = period.length();
                    final int delimitter = year.length() - length;
                    year = year.substring(0, delimitter) + period;
                }
                else if (format.trim().equalsIgnoreCase("month")) {
                    final int length = period.length();
                    final int delimitter = month.length() - length;
                    month = month.substring(0, delimitter) + period;
                }
                final String resultString = "'" + sign + year + "-" + month + "'";
                this.functionName.setColumnName("CAST");
                this.setAsDatatype("AS");
                final SelectColumn sc = new SelectColumn();
                final Vector colExp = new Vector();
                colExp.add(resultString);
                sc.setColumnExpression(colExp);
                final DateClass intervalParameters = new DateClass();
                intervalParameters.setDatatypeName("INTERVAL YEAR TO MONTH");
                newArguments.add(sc);
                newArguments.add(intervalParameters);
                this.setFunctionArguments(newArguments);
            }
        }
    }
}
