package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class extract extends FunctionCalls
{
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (from_sqs.isMSAzure()) {
            if (this.functionName.toString().equalsIgnoreCase("minute")) {
                arguments.addElement("mi");
            }
            if (this.functionName.toString().equalsIgnoreCase("quarter")) {
                arguments.addElement("q");
            }
            if (this.functionName.toString().equalsIgnoreCase("dayofyear")) {
                arguments.addElement("dy");
            }
        }
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selCol = this.functionArguments.get(0).toMSSQLServerSelect(to_sqs, from_sqs);
                arguments.addElement(selCol);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (from_sqs.isMSAzure()) {
            if (this.functionName.toString().equalsIgnoreCase("minute")) {
                final String arg = "CAST( " + arguments.get(1) + " AS DATETIME)";
                arguments.set(1, arg);
            }
            if (this.functionName.toString().equalsIgnoreCase("quarter")) {
                final String arg = "CAST(DATEPART( " + arguments.get(0).toString() + " , " + arguments.get(1).toString() + ") AS VARCHAR)";
                this.functionName.setColumnName(arg);
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
                return;
            }
            if (this.functionName.toString().equalsIgnoreCase("dayname")) {
                final String arg = "format(" + arguments.get(0).toString() + ",'dddd')";
                this.functionName.setColumnName(arg);
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
                return;
            }
            if (this.functionName.getColumnName().equalsIgnoreCase("WEEKDAY")) {
                final String arg = "(datepart(dw," + arguments.get(0).toString() + ")+5)%7";
                this.functionName.setColumnName(arg);
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
                return;
            }
        }
        this.setFunctionArguments(arguments);
        this.getFunctionName().setColumnName("DATEPART");
        if (!from_sqs.isMSAzure()) {
            this.setFromInTrim(",");
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selCol = this.functionArguments.get(0).toNetezzaSelect(to_sqs, from_sqs);
                if (selCol.getAliasName() != null && selCol.getAliasName().startsWith("\"")) {
                    selCol.setAliasName(selCol.getAliasName().replaceAll("\"", "'"));
                }
                arguments.addElement(selCol);
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
        String qry = "";
        final String cons_val = "";
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0 && this.functionArguments.elementAt(0).getColumnExpression().size() == 1 && this.functionArguments.elementAt(0).getColumnExpression().get(0) instanceof String) {
                    String dateString = this.functionArguments.elementAt(0).getColumnExpression().get(0).toString();
                    if (this.functionName.getColumnName().equalsIgnoreCase("MINUTE") || this.functionName.getColumnName().equalsIgnoreCase("SECOND") || this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND")) {
                        dateString = "CAST(" + this.handleStringLiteralForTime(dateString, from_sqs, true) + " AS TIME)";
                    }
                    else if (canUseUDFFunction && this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND") || this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND") || this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND") || this.trailingString.equalsIgnoreCase("MINUTE_SECOND") || this.trailingString.equalsIgnoreCase("HOUR_SECOND") || this.trailingString.equalsIgnoreCase("HOUR_MINUTE"))) {
                        dateString = "CAST(" + this.handleStringLiteralForTime(dateString, from_sqs, true) + " AS TIMESTAMP)";
                    }
                    else {
                        dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS TIMESTAMP)";
                    }
                    this.functionArguments.elementAt(0).getColumnExpression().set(0, dateString);
                }
                arguments.addElement(this.functionArguments.get(0).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("dayname")) {
            qry = "  to_char(" + cons_val + arguments.get(0) + ",'FMDay') ";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFMONTH")) {
            qry = " cast(EXTRACT(DAY FROM " + cons_val + arguments.get(0) + ") as int)";
            if (canUseUDFFunction) {
                qry = "DAYOFMONTH(" + arguments.get(0).toString() + ")";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFYEAR")) {
            qry = " cast(date_part('doy' ," + cons_val + arguments.get(0) + ") as int)";
            if (canUseUDFFunction) {
                qry = "DAYOFYEAR(" + arguments.get(0).toString() + ")";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("WEEKDAY")) {
            qry = " mod(cast(date_part('dow' ," + cons_val + arguments.get(0) + ") as int) +6, 7)";
            if (canUseUDFFunction) {
                qry = "WEEKDAY(" + arguments.get(0).toString() + ")";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("MINUTE")) {
            qry = " cast(EXTRACT(MINUTE FROM  " + cons_val + arguments.get(0) + ") as int)";
            if (canUseUDFFunction) {
                qry = "MINUTE(" + arguments.get(0).toString() + ")";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
            qry = " cast(EXTRACT(SECOND FROM  " + cons_val + arguments.get(0) + ") as int)";
            if (canUseUDFFunction) {
                qry = "SECOND(" + arguments.get(0).toString() + ")";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND")) {
            qry = " (cast(EXTRACT(MICROSECOND FROM  " + cons_val + arguments.get(0) + ") as int)%1000000)";
            if (canUseUDFFunction) {
                qry = "MICROSECOND(" + arguments.get(0).toString() + ")";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("QUARTER")) {
            qry = " cast(EXTRACT(QUARTER FROM  " + cons_val + arguments.get(0) + ") as int)";
            if (canUseUDFFunction) {
                qry = "QUARTER(" + arguments.get(0).toString() + ")";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("to_days")) {
            this.functionName.setColumnName("(719528+div(EXTRACT(EPOCH FROM(" + arguments.get(0).toString() + ")::date)::bigint,86400))");
            if (canUseUDFFunction) {
                qry = "TO_DAYS(" + arguments.get(0).toString() + ")";
            }
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'SSUS')::bigint");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'MISSUS')::bigint");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'HH24MISSUS')::bigint");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'MISS')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'HH24MISS')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'HH24MI')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'DDHH24MISSUS')::bigint");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'DDHH24MISS')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'DDHH24MI')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'DDHH24')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
            this.functionName.setColumnName("to_char(" + arguments.get(0).toString() + ",'YYYYMM')::int");
            this.setOpenBracesForFunctionNameRequired(false);
            this.trailingString = null;
            this.fromStringInFunction = null;
            this.functionArguments = new Vector();
        }
        else {
            if (canUseUDFFunction) {
                if (this.trailingString != null && this.trailingString.equalsIgnoreCase("YEAR")) {
                    this.functionName.setColumnName("YEAR");
                    this.trailingString = null;
                    this.fromStringInFunction = null;
                }
                else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("MONTH")) {
                    this.functionName.setColumnName("MONTH");
                    this.trailingString = null;
                    this.fromStringInFunction = null;
                }
                else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("DAY")) {
                    this.functionName.setColumnName("DAY");
                    this.trailingString = null;
                    this.fromStringInFunction = null;
                }
                else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("HOUR")) {
                    this.functionName.setColumnName("HOUR");
                    this.trailingString = null;
                    this.fromStringInFunction = null;
                }
                else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("MINUTE")) {
                    this.functionName.setColumnName("MINUTE");
                    this.trailingString = null;
                    this.fromStringInFunction = null;
                }
                else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("SECOND")) {
                    this.functionName.setColumnName("SECOND");
                    this.trailingString = null;
                    this.fromStringInFunction = null;
                }
                else if (this.trailingString != null && this.trailingString.equalsIgnoreCase("MICROSECOND")) {
                    this.functionName.setColumnName("MICROSECOND");
                    this.trailingString = null;
                    this.fromStringInFunction = null;
                }
            }
            final String arg = arguments.get(0).toString();
            arguments.set(0, cons_val + arg);
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selCol = this.functionArguments.get(0).toTeradataSelect(to_sqs, from_sqs);
                arguments.addElement(selCol);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    if (this.functionName.getColumnName().equalsIgnoreCase("MINUTE") || this.functionName.getColumnName().equalsIgnoreCase("SECOND") || this.functionName.getColumnName().equalsIgnoreCase("MICROSECOND") || (this.functionName.getColumnName().equalsIgnoreCase("extract") && this.trailingString != null && (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND") || this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND") || this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND") || this.trailingString.equalsIgnoreCase("MINUTE_SECOND") || this.trailingString.equalsIgnoreCase("HOUR_SECOND") || this.trailingString.equalsIgnoreCase("HOUR_MINUTE")))) {
                        this.handleStringLiteralForTime(from_sqs, i_count, true, true);
                    }
                    else {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                    }
                }
                final SelectColumn selCol = this.functionArguments.get(0).toVectorWiseSelect(to_sqs, from_sqs);
                arguments.addElement(selCol);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.trailingString != null) {
            if (this.trailingString.equalsIgnoreCase("SECOND_MICROSECOND")) {
                this.functionName.setColumnName("bigint(date_format(" + arguments.get(0).toString() + ",'%s%f'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("MINUTE_MICROSECOND")) {
                this.functionName.setColumnName("bigint(date_format(" + arguments.get(0).toString() + ",'%i%s%f'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("MINUTE_SECOND")) {
                this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%i%s'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("HOUR_MICROSECOND")) {
                this.functionName.setColumnName("bigint(date_format(" + arguments.get(0).toString() + ",'%H%i%s%f'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("HOUR_SECOND")) {
                this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%H%i%s'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("HOUR_MINUTE")) {
                this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%H%i'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("DAY_MICROSECOND")) {
                this.functionName.setColumnName("bigint(date_format(" + arguments.get(0).toString() + ",'%e%H%i%s%f'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("DAY_SECOND")) {
                this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%e%H%i%s'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("DAY_MINUTE")) {
                this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%e%H%i'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("DAY_HOUR")) {
                this.functionName.setColumnName("int(date_format(" + arguments.get(0).toString() + ",'%e%H'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.trailingString.equalsIgnoreCase("YEAR_MONTH")) {
                this.functionName.setColumnName("int(to_char(" + arguments.get(0).toString() + ",'YYYYMM'))");
                this.trailingString = null;
                this.fromStringInFunction = null;
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
        }
    }
}
