package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class convertTz extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            this.functionName.setColumnName("CONVERT_TIMEZONE");
            final Vector args = new Vector();
            if (arguments.size() == 2) {
                String arg1 = arguments.get(1).toString().replaceAll("'", "");
                if (arg1.contains("+")) {
                    arg1 = arg1.replaceAll("\\+", "-");
                }
                else {
                    arg1 = arg1.replaceAll("-", "+");
                }
                args.addElement("'UTC+00:00'");
                args.addElement("'UTC" + arg1 + "'");
                args.addElement(arguments.get(0));
            }
            else {
                String arg1 = arguments.get(1).toString().replaceAll("'", "");
                String arg2 = arguments.get(2).toString().replaceAll("'", "");
                if (arg1.contains("+")) {
                    arg1 = arg1.replaceAll("\\+", "-");
                }
                else {
                    arg1 = arg1.replaceAll("-", "+");
                }
                if (arg2.contains("+")) {
                    arg2 = arg2.replaceAll("\\+", "-");
                }
                else {
                    arg2 = arg2.replaceAll("-", "+");
                }
                args.addElement("'UTC" + arg1 + "'");
                args.addElement("'UTC" + arg2 + "'");
                args.addElement(arguments.get(0));
            }
            this.setFunctionArguments(args);
        }
        else {
            String qry = " ((" + arguments.get(0) + " at time zone " + arguments.get(1) + ") at time zone " + arguments.get(2) + " )";
            int finalMins = 0;
            try {
                if (canUseUDFFunction && arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null) {
                    final String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
                    final String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
                    int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
                    final int currentTZMin = (currentTZ.length == 2) ? Integer.parseInt(currentTZ[1]) : 0;
                    int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
                    final int toTZMin = (toTZ.length == 2) ? Integer.parseInt(toTZ[1]) : 0;
                    final int toTZOverallMins = toTZHour * 60 + toTZMin;
                    final int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
                    if (currentTZ[0].contains("-")) {
                        currentTZHour *= -1;
                    }
                    if (toTZ[0].contains("-")) {
                        toTZHour *= -1;
                    }
                    if (currentTZHour < 0) {
                        if (toTZHour < 0) {
                            finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                        }
                        else {
                            finalMins = toTZOverallMins - currentTZOverallMins * -1;
                        }
                    }
                    else if (toTZHour < 0) {
                        finalMins = toTZOverallMins * -1 - currentTZOverallMins;
                    }
                    else {
                        finalMins = toTZOverallMins - currentTZOverallMins;
                    }
                    qry = " (" + arguments.get(0) + " + ( INTERVAL  '1'  MINUTE * " + finalMins + ") )";
                }
            }
            catch (final Exception ex) {}
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
}
