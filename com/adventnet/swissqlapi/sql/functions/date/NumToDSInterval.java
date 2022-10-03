package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class NumToDSInterval extends FunctionCalls
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
            String period = " ";
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
            if (isNumber && (format.trim().equalsIgnoreCase("day") || format.trim().equalsIgnoreCase("hour") || format.trim().equalsIgnoreCase("minute") || format.trim().equalsIgnoreCase("second"))) {
                String day = "0000";
                String hour = "00";
                String minute = "00";
                String second = "00";
                if (format.trim().equalsIgnoreCase("day")) {
                    final int length = period.length();
                    final int delimitter = day.length() - length;
                    day = day.substring(0, delimitter) + period;
                }
                else if (format.trim().equalsIgnoreCase("hour")) {
                    final int length = period.length();
                    final int delimitter = hour.length() - length;
                    hour = hour.substring(0, delimitter) + period;
                }
                else if (format.trim().equalsIgnoreCase("minute")) {
                    final int length = period.length();
                    final int delimitter = minute.length() - length;
                    minute = minute.substring(0, delimitter) + period;
                }
                else if (format.trim().equalsIgnoreCase("second")) {
                    final int length = period.length();
                    final int delimitter = second.length() - length;
                    second = second.substring(0, delimitter) + period;
                }
                final String resultString = "'" + sign + day + " " + hour + ":" + minute + ":" + second + "'";
                this.functionName.setColumnName("CAST");
                this.setAsDatatype("AS");
                final SelectColumn sc = new SelectColumn();
                final Vector colExp = new Vector();
                colExp.add(resultString);
                sc.setColumnExpression(colExp);
                final DateClass intervalParameters = new DateClass();
                intervalParameters.setDatatypeName("INTERVAL DAY(4) TO SECOND(0)");
                newArguments.add(sc);
                newArguments.add(intervalParameters);
                this.setFunctionArguments(newArguments);
            }
        }
    }
}
