package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class timeAddSub extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        boolean isStringColumn = false;
        boolean isFirstArgTime = false;
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (this.functionArguments.elementAt(i_count).getColumnExpression().get(0) instanceof String) {
                    this.handleStringLiteralForTime(from_sqs, i_count, false, true);
                    String elementAt = this.functionArguments.elementAt(i_count).getColumnExpression().get(0).toString();
                    if (elementAt.contains("/") || elementAt.contains("-")) {
                        elementAt = "CAST(" + elementAt + " AS TIMESTAMP)";
                    }
                    else if (elementAt.contains(":")) {
                        elementAt = "CAST(" + elementAt + " AS TIME)";
                        isFirstArgTime = true;
                    }
                    this.functionArguments.elementAt(i_count).getColumnExpression().set(0, elementAt);
                    if (i_count == 0) {
                        isStringColumn = true;
                    }
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "";
        final StringBuffer str = new StringBuffer();
        String timeString = " CAST( " + arguments.get(1).toString() + " AS TIME)";
        if (!arguments.get(1).toString().contains(":")) {
            timeString = " interval '1' second * " + arguments.get(1).toString();
        }
        String dateTimeString = arguments.get(0).toString();
        if (!isStringColumn) {
            dateTimeString = "CAST(" + arguments.get(0) + " AS TIMESTAMP)";
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("addtime")) {
            qry = (isFirstArgTime ? (" ( " + dateTimeString + " +  " + timeString + ")::time ") : (" ( " + dateTimeString + " +  " + timeString + ")"));
            if (canUseUDFFunction) {
                qry = "DATE_ADD(" + arguments.get(0).toString() + ", TO_TIME_UDF(" + arguments.get(1).toString() + "))";
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("subtime")) {
            qry = (isFirstArgTime ? ("( " + dateTimeString + " -  " + timeString + " )::time ") : ("( " + dateTimeString + " -  " + timeString + " )"));
            if (canUseUDFFunction) {
                qry = "DATE_SUB(" + arguments.get(0).toString() + ", TO_TIME_UDF(" + arguments.get(1).toString() + "))";
            }
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "";
        if (this.functionName.getColumnName().equalsIgnoreCase("ADDTIME")) {
            qry = "Cast(" + arguments.get(0).toString() + " as DATETIME) + Cast(" + arguments.get(1).toString() + " as DATETIME)";
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("SUBTIME")) {
            qry = "Cast(" + arguments.get(0).toString() + " as DATETIME) - Cast(" + arguments.get(1).toString() + " as DATETIME)";
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
            qry = "DATEADD(s," + Long.parseLong(arguments.get(0).toString()) + ",'1970-01-01 00:00:00')";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
