package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class TimeToSec extends FunctionCalls
{
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String funName = this.functionName.getColumnName().trim();
        if (funName.equalsIgnoreCase("timestampdiff")) {
            final Vector arguments = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    if (i_count != 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                    }
                    arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            if (!arguments.get(1).toString().trim().equalsIgnoreCase("from_unixtime(0)") || !arguments.get(0).toString().trim().equalsIgnoreCase("SECOND")) {
                this.functionName.setColumnName("TIMESTAMPDIFF");
                this.setFunctionArguments(arguments);
                return;
            }
            this.functionName.setColumnName("unix_timestamp(" + arguments.get(2) + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (funName.equalsIgnoreCase("time_to_sec")) {
            final StringBuffer arguments2 = new StringBuffer();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    if (i_count == 0) {
                        this.handleStringLiteralForTime(from_sqs, i_count, false, true);
                    }
                    arguments2.append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments2.append(this.functionArguments.elementAt(i_count));
                }
            }
            this.functionName.setColumnName("CAST(((time(timestamp(" + (Object)arguments2 + "))-(time '00:00:00'))/interval '1' second) AS BIGINT)");
        }
        else if (funName.equalsIgnoreCase("sec_to_time")) {
            final StringBuffer arguments2 = new StringBuffer();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments2.append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments2.append(this.functionArguments.elementAt(i_count));
                }
            }
            final String argument = "(time('00:00:00')+ interval '1' second * (" + (Object)arguments2 + "))";
            this.functionName.setColumnName(argument);
        }
        else if (funName.equalsIgnoreCase("timediff")) {
            final StringBuffer arguments2 = new StringBuffer();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                final StringBuffer temp = new StringBuffer();
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    this.handleStringLiteralForTime(from_sqs, i_count, false, true);
                    temp.append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    temp.append(this.functionArguments.elementAt(i_count));
                }
                if (i_count == 0) {
                    arguments2.append("time(from_unixtime(0) + (time(" + (Object)temp + ")");
                }
                else if (i_count == 1) {
                    arguments2.append("-time(" + (Object)temp + ")))");
                }
            }
            this.functionName.setColumnName((Object)arguments2 + "");
        }
        if (funName.equalsIgnoreCase("ADDDATE") || funName.equalsIgnoreCase("SUBDATE")) {
            String operation = "+";
            if (funName.equalsIgnoreCase("SUBDATE")) {
                operation = "-";
            }
            final StringBuffer arguments3 = new StringBuffer();
            for (int i_count2 = 0; i_count2 < this.functionArguments.size(); ++i_count2) {
                if (i_count2 == 1) {
                    if (this.functionArguments.elementAt(i_count2) instanceof SelectColumn && this.functionArguments.elementAt(i_count2).toString().trim().replaceAll("\\(", "").toLowerCase().startsWith("interval")) {
                        arguments3.append(") " + operation + " ");
                    }
                    else {
                        arguments3.append(") " + operation + " interval '1' day * ");
                    }
                }
                else if (i_count2 == 0) {
                    arguments3.append("timestamp(");
                }
                if (this.functionArguments.elementAt(i_count2) instanceof SelectColumn) {
                    if (i_count2 == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, 0, true);
                    }
                    arguments3.append("(" + this.functionArguments.elementAt(i_count2).toVectorWiseSelect(to_sqs, from_sqs) + ")");
                }
                else {
                    arguments3.append(this.functionArguments.elementAt(i_count2));
                }
            }
            this.functionName.setColumnName("(" + (Object)arguments3 + ")");
        }
        if (funName.equalsIgnoreCase("weekday")) {
            final StringBuffer arguments2 = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                this.handleStringLiteralForDateTime(from_sqs, 0, true);
                arguments2.append(this.functionArguments.elementAt(0).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments2.append(this.functionArguments.elementAt(0));
            }
            final String argument = "mod(int(dayofweek(" + (Object)arguments2 + ")+5),7)";
            this.functionName.setColumnName(argument);
        }
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        String qry = "";
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (this.functionName.getColumnName().equalsIgnoreCase("timestamp")) {
                    if (i_count == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                    }
                    else {
                        this.handleStringLiteralForTime(from_sqs, i_count, true, true);
                    }
                }
                else {
                    this.handleStringLiteralForTime(from_sqs, i_count, true, true);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("microsecond")) {
            qry = " (cast(EXTRACT(MICROSECONDS FROM  (" + arguments.get(0) + ")::time) as int) %1000000) ";
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("timestamp")) {
            if (arguments.size() == 1) {
                if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                    qry = " (" + arguments.get(0) + " :: timestamp) ";
                }
                else {
                    qry = "CAST(" + arguments.get(0) + " AS TIMESTAMP)";
                    if (from_sqs != null && from_sqs.canUseUDFFunctionsForDateTime()) {
                        qry = "TO_TIMESTAMP(" + arguments.get(0) + ")";
                    }
                }
            }
            else if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                qry = " (" + arguments.get(0) + " :: timestamp + " + arguments.get(1) + " ::time)";
            }
            else {
                qry = "( TO_TIMESTAMP(" + arguments.get(0) + ") + (INTERVAL '1' SECOND * CAST(EXTRACT(EPOCH FROM (" + arguments.get(1) + ")::time) AS INTEGER)) )";
                if (from_sqs != null && from_sqs.canUseUDFFunctionsForDateTime()) {
                    qry = "DATE_ADD(" + arguments.get(0) + ", TO_TIME_UDF(" + arguments.get(1).toString() + "))";
                }
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("timediff")) {
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                qry = " to_char(( (" + arguments.get(0) + ")::time -   (" + arguments.get(1) + ")::time ), 'HH24:MI:SS.US')";
            }
            else if (from_sqs != null && from_sqs.canUseUDFFunctionsForDateTime()) {
                qry = "CAST(INTERVAL '1' SECOND * (CAST(EXTRACT(EPOCH FROM TO_TIME_UDF(" + arguments.get(0) + ")) AS INTEGER) - CAST(EXTRACT(EPOCH FROM TO_TIME_UDF(" + arguments.get(1) + ")) AS INTEGER)) AS TEXT)";
            }
            else {
                qry = "CAST(INTERVAL '1' SECOND * (CAST(EXTRACT(EPOCH FROM (" + arguments.get(0) + ")::time) AS INTEGER) - CAST(EXTRACT(EPOCH FROM (" + arguments.get(1) + ")::time) AS INTEGER)) AS TEXT)";
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("time_to_sec")) {
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                qry = "DATEDIFF(SECOND, DATE(" + arguments.get(0).toString() + "), " + arguments.get(0).toString() + ")";
            }
            else {
                qry = "EXTRACT(EPOCH FROM  (" + arguments.get(0) + ")::time)::int";
                if (from_sqs != null && from_sqs.canUseUDFFunctionsForDateTime()) {
                    qry = "TIME_TO_SEC(" + arguments.get(0) + ")";
                }
            }
        }
        else {
            qry = "EXTRACT(EPOCH FROM interval " + arguments.get(0) + ")";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
