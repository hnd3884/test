package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Hashtable;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class dateformat extends FunctionCalls
{
    static Hashtable dateFormats;
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String temp = null;
        final String s_ce = null;
        final Vector arguments = new Vector();
        if (from_sqs.isMSAzure()) {
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            if (this.functionName.getColumnName().equalsIgnoreCase("STR_TO_DATE")) {
                final String dateArg = "CAST(" + arguments.get(0).toString() + " AS DATE)";
                arguments.set(0, dateArg);
            }
            this.functionName.setColumnName("format");
            String arg1 = arguments.get(1).toString();
            arg1 = arg1.replaceAll("%a", "ddd");
            arg1 = arg1.replaceAll("%b", "MMM");
            arg1 = arg1.replaceAll("%c", "MM");
            arg1 = arg1.replaceAll("%d", "dd");
            arg1 = arg1.replaceAll("%e", "d");
            arg1 = arg1.replaceAll("%f", "ms");
            arg1 = arg1.replaceAll("%H", "HH");
            arg1 = arg1.replaceAll("%h", "hh");
            arg1 = arg1.replaceAll("%I", "hh");
            arg1 = arg1.replaceAll("%i", "mm");
            arg1 = arg1.replaceAll("%j", "DDD");
            arg1 = arg1.replaceAll("%k", "H");
            arg1 = arg1.replaceAll("%l", "h");
            arg1 = arg1.replaceAll("%M", "MMM");
            arg1 = arg1.replaceAll("%m", "MM");
            arg1 = arg1.replaceAll("%p", "tt");
            arg1 = arg1.replaceAll("%r", "hh:mm:ss tt");
            arg1 = arg1.replaceAll("%S", "ss");
            arg1 = arg1.replaceAll("%s", "ss");
            arg1 = arg1.replaceAll("%T", "hh:mm:ss");
            arg1 = arg1.replaceAll("%U", "ww");
            arg1 = arg1.replaceAll("%u", "ww");
            arg1 = arg1.replaceAll("%V", "ww");
            arg1 = arg1.replaceAll("%v", "ww");
            arg1 = arg1.replaceAll("%X", "yyyy");
            arg1 = arg1.replaceAll("%x", "yyyy");
            arg1 = arg1.replaceAll("%Y", "yyyy");
            arg1 = arg1.replaceAll("%y", "YY");
            arguments.set(1, arg1);
            this.setFunctionArguments(arguments);
        }
        else {
            String datestr = null;
            if (this.functionName.getColumnName().equalsIgnoreCase("DATE_FORMAT")) {
                this.functionName.setColumnName("format");
            }
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                datestr = this.functionArguments.elementAt(0).toMSSQLServerSelect(to_sqs, from_sqs).toString();
            }
            else {
                datestr = this.functionArguments.elementAt(0).toString();
            }
            String arg2 = this.functionArguments.get(1).toString();
            if (arg2.startsWith("'") && arg2.endsWith("'")) {
                arg2 = arg2.substring(1, arg2.length() - 1);
            }
            if (arg2.lastIndexOf("%") != 0) {
                final int seperatorIndex = arg2.lastIndexOf("%") - 1;
                String ss = arg2.substring(seperatorIndex, seperatorIndex + 1);
                if (arg2.indexOf(ss) != -1 && arg2.lastIndexOf("%") != 0) {
                    ss = (ss.equals(".") ? "\\." : ss);
                    final String[] tokens = arg2.split(ss);
                    for (int i = 0; i < tokens.length; ++i) {
                        if (dateformat.dateFormats.containsKey(tokens[i])) {
                            tokens[i] = dateformat.dateFormats.get(tokens[i]).toString();
                            if (i == 0) {
                                temp = "CAST(" + tokens[0] + datestr + ")" + " AS VARCHAR)";
                                if (tokens.length == 1) {
                                    arguments.add(temp);
                                }
                            }
                            else {
                                temp = temp + "+'" + ss + "'+" + "CAST(" + tokens[i] + datestr + ")" + " AS VARCHAR)";
                            }
                        }
                    }
                    if (tokens.length > 1) {
                        arguments.add(temp);
                    }
                }
            }
            else if (dateformat.dateFormats.containsKey(arg2)) {
                final String singleType = "CAST(" + dateformat.dateFormats.get(arg2).toString() + datestr + ")" + " AS VARCHAR)";
                arguments.add(singleType);
            }
            if (arguments.size() != 0) {
                this.setFunctionArguments(arguments);
            }
            else {
                this.setFunctionArguments(this.functionArguments);
            }
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String oldFunctionName = this.functionName.getColumnName();
        boolean canUseUDFFunction = false;
        if (this.functionName.getColumnName().equalsIgnoreCase("STR_TO_DATE")) {
            this.functionName.setColumnName("TO_TIMESTAMP");
            canUseUDFFunction = (from_sqs != null && !from_sqs.isAmazonRedShift() && (from_sqs.canUseUDFFunctionsForText() || from_sqs.canUseUDFFunctionsForStrToDate()));
            if (canUseUDFFunction) {
                this.functionName.setColumnName("ZR_STR_TO_DATE");
            }
        }
        else {
            this.functionName.setColumnName("TO_CHAR");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (i_count == 0 && this.functionArguments.elementAt(i_count).getColumnExpression().size() == 1 && this.functionArguments.elementAt(i_count).getColumnExpression().get(0) instanceof String) {
                String dateOrTimeString = this.functionArguments.elementAt(i_count).getColumnExpression().get(0).toString();
                if (oldFunctionName.equalsIgnoreCase("TIME_FORMAT")) {
                    dateOrTimeString = "CAST(" + this.handleStringLiteralForTime(dateOrTimeString, from_sqs) + " AS TIME)";
                    this.functionArguments.elementAt(i_count).getColumnExpression().set(0, dateOrTimeString);
                }
                else if (oldFunctionName.equalsIgnoreCase("DATE_FORMAT")) {
                    dateOrTimeString = "CAST(" + this.handleStringLiteralForDateTime(dateOrTimeString, from_sqs) + " AS TIMESTAMP)";
                    this.functionArguments.elementAt(i_count).getColumnExpression().set(0, dateOrTimeString);
                }
                else if (from_sqs != null && !from_sqs.isAmazonRedShift()) {
                    dateOrTimeString = "CAST(" + dateOrTimeString + " AS TEXT)";
                    this.functionArguments.elementAt(i_count).getColumnExpression().set(0, dateOrTimeString);
                }
            }
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count == 0 && canUseUDFFunction) {
                    sc.convertSelectColumnToTextDataType();
                }
                arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String arg1 = arguments.get(1).toString();
        arg1 = arg1.replaceAll("%a", "Dy");
        arg1 = arg1.replaceAll("%b", "Mon");
        arg1 = arg1.replaceAll("%c", "MM");
        arg1 = arg1.replaceAll("%D", "DD");
        arg1 = arg1.replaceAll("%d", "DD");
        arg1 = arg1.replaceAll("%e", "FMdd");
        arg1 = arg1.replaceAll("%f", "US");
        arg1 = arg1.replaceAll("%H", "HH24");
        arg1 = arg1.replaceAll("%h", "HH12");
        arg1 = arg1.replaceAll("%I", "HH12");
        arg1 = arg1.replaceAll("%i", "MI");
        arg1 = arg1.replaceAll("%j", "DDD");
        arg1 = arg1.replaceAll("%k", "FMHH24");
        arg1 = arg1.replaceAll("%l", "FMHH12");
        arg1 = arg1.replaceAll("%M", "FMMonth");
        arg1 = arg1.replaceAll("%m", "MM");
        arg1 = arg1.replaceAll("%p", "AM");
        arg1 = arg1.replaceAll("%r", "HH12:MI:ss AM");
        arg1 = arg1.replaceAll("%S", "SS");
        arg1 = arg1.replaceAll("%s", "SS");
        arg1 = arg1.replaceAll("%T", "HH24:MI:ss");
        arg1 = arg1.replaceAll("%U", "WW");
        arg1 = arg1.replaceAll("%u", "WW");
        arg1 = arg1.replaceAll("%V", "WW");
        arg1 = arg1.replaceAll("%v", "WW");
        arg1 = arg1.replaceAll("%W", "FMDay");
        arg1 = arg1.replaceAll("%w", "D");
        arg1 = arg1.replaceAll("%X", "YYYY");
        arg1 = arg1.replaceAll("%x", "YYYY");
        arg1 = arg1.replaceAll("%Y", "YYYY");
        arg1 = arg1.replaceAll("%y", "YY");
        arguments.set(1, arg1);
        if (this.functionName.getColumnName().equalsIgnoreCase("TO_DATE")) {
            String arg2 = arguments.get(0).toString();
            arg2 = "cast(" + arg2 + " as text)";
            arguments.set(0, arg2);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String oldFunctionName = this.functionName.getColumnName();
        this.functionName.setColumnName("DATE_FORMAT");
        boolean isTimeFunction = false;
        if (oldFunctionName.equalsIgnoreCase("TIME_FORMAT")) {
            this.functionName.setColumnName("TIME_FORMAT");
            isTimeFunction = true;
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    if (isTimeFunction) {
                        this.handleStringLiteralForTime(from_sqs, i_count, true);
                    }
                    else {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                    }
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    static {
        (dateformat.dateFormats = new Hashtable()).put("%c", "DATEPART(month,");
        dateformat.dateFormats.put("%d", "DATEPART(day,");
        dateformat.dateFormats.put("%e", "DATEPART(day,");
        dateformat.dateFormats.put("%H", "DATEPART(hour,");
        dateformat.dateFormats.put("%i", "DATEPART(minute,");
        dateformat.dateFormats.put("%j", "DATEPART(dayofyear,");
        dateformat.dateFormats.put("%k", "DATEPART(hour,");
        dateformat.dateFormats.put("%M", "DATEPART(month,");
        dateformat.dateFormats.put("%m", "DATEPART(month,");
        dateformat.dateFormats.put("%s", "DATEPART(second,");
        dateformat.dateFormats.put("%S", "DATEPART(second,");
        dateformat.dateFormats.put("%U", "DATEPART(week,");
        dateformat.dateFormats.put("%X", "DATEPART(year,");
        dateformat.dateFormats.put("%x", "DATEPART(year,");
        dateformat.dateFormats.put("%Y", "DATEPART(year,");
    }
}
