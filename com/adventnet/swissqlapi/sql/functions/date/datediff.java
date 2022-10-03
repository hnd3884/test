package com.adventnet.swissqlapi.sql.functions.date;

import java.util.Collection;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.StringTokenizer;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class datediff extends FunctionCalls
{
    private String dateFormatString;
    private String monthFormat;
    private String yearFormat;
    private int yearSize;
    
    public datediff() {
        this.dateFormatString = null;
        this.monthFormat = null;
        this.yearFormat = null;
    }
    
    public String yearFormat(final int num) {
        switch (this.yearSize = num) {
            case 1: {
                this.yearFormat = "Y";
                break;
            }
            case 2: {
                this.yearFormat = "YY";
                break;
            }
            case 4: {
                this.yearFormat = "YYYY";
                break;
            }
        }
        return this.yearFormat;
    }
    
    public String dateFormatConversion(final String str) {
        int setFormat = 0;
        this.dateFormatString = str;
        this.dateFormatString = this.dateFormatString.trim();
        final int length = this.dateFormatString.length();
        final StringBuffer sbSlash = new StringBuffer();
        final StringBuffer sbHiphen = new StringBuffer();
        final int indexOfHiphen = this.dateFormatString.indexOf(45);
        final int indexOfSlash = this.dateFormatString.indexOf(47);
        final ArrayList stringArrayForSlash = new ArrayList();
        final StringTokenizer stringTokenForSlash = new StringTokenizer(this.dateFormatString, "/ '");
        while (stringTokenForSlash.hasMoreTokens()) {
            stringArrayForSlash.add(stringTokenForSlash.nextToken());
        }
        final ArrayList stringArrayForHiphen = new ArrayList();
        final StringTokenizer stringTokenForHiphen = new StringTokenizer(this.dateFormatString, "- '");
        while (stringTokenForHiphen.hasMoreTokens()) {
            stringArrayForHiphen.add(stringTokenForHiphen.nextToken());
        }
        String formatString = "";
        if (indexOfHiphen != -1) {
            if (stringArrayForHiphen.size() == 3) {
                String monthString = stringArrayForHiphen.get(1);
                monthString = monthString.trim();
                String year = null;
                if (monthString.equalsIgnoreCase("Jan") || monthString.equalsIgnoreCase("feb") || monthString.equalsIgnoreCase("mar") || monthString.equalsIgnoreCase("apr") || monthString.equalsIgnoreCase("may") || monthString.equalsIgnoreCase("jun") || monthString.equalsIgnoreCase("jul") || monthString.equalsIgnoreCase("aug") || monthString.equalsIgnoreCase("sep") || monthString.equalsIgnoreCase("oct") || monthString.equalsIgnoreCase("nov") || monthString.equalsIgnoreCase("dec")) {
                    final String month = this.monthFormat(monthString);
                    if (stringArrayForHiphen.get(2).length() == 4) {
                        setFormat = 1;
                        String yearString = stringArrayForHiphen.get(2);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForHiphen.get(0);
                        date = date.trim();
                        formatString = month + "-" + date + "-" + yearString;
                    }
                    else if (stringArrayForHiphen.get(0).trim().length() == 4) {
                        setFormat = 2;
                        String yearString = stringArrayForHiphen.get(0);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForHiphen.get(2);
                        date = date.trim();
                        formatString = yearString + "-" + month + "-" + date;
                    }
                    else if (stringArrayForHiphen.get(2).trim().length() == 2 || stringArrayForHiphen.get(2).trim().length() == 1) {
                        setFormat = 1;
                        String yearString = stringArrayForHiphen.get(2);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForHiphen.get(0);
                        date = date.trim();
                        formatString = month + "-" + date + "-" + yearString;
                    }
                }
                else if (stringArrayForHiphen.get(0).trim().length() == 4) {
                    setFormat = 2;
                    String yearString2 = stringArrayForHiphen.get(0);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForHiphen.get(1);
                    monthString = monthString.trim();
                    String date2 = stringArrayForHiphen.get(2);
                    date2 = date2.trim();
                    formatString = yearString2 + "-" + monthString + "-" + date2;
                }
                else if (stringArrayForHiphen.get(2).trim().length() == 4) {
                    setFormat = 1;
                    String yearString2 = stringArrayForHiphen.get(2);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForHiphen.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForHiphen.get(1);
                    date2 = date2.trim();
                    formatString = monthString + "-" + date2 + "-" + yearString2;
                }
                else if (stringArrayForHiphen.get(1).trim().length() == 4) {
                    setFormat = 3;
                    String yearString2 = stringArrayForHiphen.get(1);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForHiphen.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForHiphen.get(2);
                    date2 = date2.trim();
                    formatString = monthString + "-" + yearString2 + "-" + date2;
                }
                else if (stringArrayForHiphen.get(2).trim().length() == 2 || stringArrayForHiphen.get(2).trim().length() == 1) {
                    setFormat = 1;
                    String yearString2 = stringArrayForHiphen.get(2);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForHiphen.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForHiphen.get(1);
                    date2 = date2.trim();
                    formatString = monthString + "-" + date2 + "-" + yearString2;
                }
                if (setFormat == 1) {
                    formatString = "'" + formatString + "'" + " ," + "'MM-DD-" + year + "')";
                }
                else if (setFormat == 2) {
                    formatString = "'" + formatString + "'" + " ," + "'" + year + "-MM-DD" + "')";
                }
                else if (setFormat == 3) {
                    formatString = "'" + formatString + "'" + " ," + "'MM-" + year + "-DD" + "')";
                }
                return formatString;
            }
            if (stringArrayForHiphen.size() == 4) {
                String monthString = stringArrayForHiphen.get(1);
                monthString = monthString.trim();
                String year = null;
                if (monthString.equalsIgnoreCase("Jan") || monthString.equalsIgnoreCase("feb") || monthString.equalsIgnoreCase("mar") || monthString.equalsIgnoreCase("apr") || monthString.equalsIgnoreCase("may") || monthString.equalsIgnoreCase("jun") || monthString.equalsIgnoreCase("jul") || monthString.equalsIgnoreCase("aug") || monthString.equalsIgnoreCase("sep") || monthString.equalsIgnoreCase("oct") || monthString.equalsIgnoreCase("nov") || monthString.equalsIgnoreCase("dec")) {
                    final String month = this.monthFormat(monthString);
                    if (stringArrayForHiphen.get(2).length() == 4) {
                        setFormat = 1;
                        String yearString = stringArrayForHiphen.get(2);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForHiphen.get(0);
                        date = date.trim();
                        formatString = month + "-" + date + "-" + yearString;
                    }
                    else if (stringArrayForHiphen.get(0).trim().length() == 4) {
                        setFormat = 2;
                        String yearString = stringArrayForHiphen.get(0);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForHiphen.get(2);
                        date = date.trim();
                        formatString = yearString + "-" + month + "-" + date;
                    }
                    else if (stringArrayForHiphen.get(2).trim().length() == 2 || stringArrayForHiphen.get(2).trim().length() == 1) {
                        setFormat = 1;
                        String yearString = stringArrayForHiphen.get(2);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForHiphen.get(0);
                        date = date.trim();
                        formatString = month + "-" + date + "-" + yearString;
                    }
                }
                else if (stringArrayForHiphen.get(0).trim().length() == 4) {
                    setFormat = 2;
                    String yearString2 = stringArrayForHiphen.get(0);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForHiphen.get(1);
                    monthString = monthString.trim();
                    String date2 = stringArrayForHiphen.get(2);
                    date2 = date2.trim();
                    formatString = yearString2 + "-" + monthString + "-" + date2;
                }
                else if (stringArrayForHiphen.get(2).trim().length() == 4) {
                    setFormat = 1;
                    String yearString2 = stringArrayForHiphen.get(2);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForHiphen.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForHiphen.get(1);
                    date2 = date2.trim();
                    formatString = monthString + "-" + date2 + "-" + yearString2;
                }
                else if (stringArrayForHiphen.get(1).trim().length() == 4) {
                    setFormat = 3;
                    String yearString2 = stringArrayForHiphen.get(1);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForHiphen.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForHiphen.get(2);
                    date2 = date2.trim();
                    formatString = monthString + "-" + yearString2 + "-" + date2;
                }
                else if (stringArrayForHiphen.get(2).trim().length() == 2 || stringArrayForHiphen.get(2).trim().length() == 1) {
                    setFormat = 1;
                    String yearString2 = stringArrayForHiphen.get(2);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForHiphen.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForHiphen.get(1);
                    date2 = date2.trim();
                    formatString = monthString + "-" + date2 + "-" + yearString2;
                }
                formatString = formatString + " " + stringArrayForHiphen.get(3);
                if (setFormat == 1) {
                    formatString = "'" + formatString + "'" + " ," + "'MM-DD-" + year + " HH24:MI:SS')";
                }
                else if (setFormat == 2) {
                    formatString = "'" + formatString + "'" + " ," + "'" + year + "-MM-DD" + " HH24:MI:SS')";
                }
                else if (setFormat == 3) {
                    formatString = "'" + formatString + "'" + " ," + "'MM-" + year + "-DD" + " HH24:MI:SS')";
                }
            }
            return formatString;
        }
        else {
            if (indexOfSlash == -1) {
                if (formatString.equals("")) {
                    formatString = SwisSQLUtils.getDateFormat(str, 1);
                    if (formatString != null) {
                        if (formatString.startsWith("'1900")) {
                            formatString += ", 'YYYY-MM-DD HH24:MI:SS')";
                        }
                        else {
                            formatString = str + ", " + formatString + ")";
                        }
                    }
                    else {
                        formatString = null;
                    }
                }
                return formatString;
            }
            if (stringArrayForSlash.size() == 3) {
                String monthString = stringArrayForSlash.get(1);
                monthString = monthString.trim();
                String year = null;
                if (monthString.equalsIgnoreCase("Jan") || monthString.equalsIgnoreCase("feb") || monthString.equalsIgnoreCase("mar") || monthString.equalsIgnoreCase("apr") || monthString.equalsIgnoreCase("may") || monthString.equalsIgnoreCase("jun") || monthString.equalsIgnoreCase("jul") || monthString.equalsIgnoreCase("aug") || monthString.equalsIgnoreCase("sep") || monthString.equalsIgnoreCase("oct") || monthString.equalsIgnoreCase("nov") || monthString.equalsIgnoreCase("dec")) {
                    final String month = this.monthFormat(monthString);
                    if (stringArrayForSlash.get(2).length() == 4) {
                        setFormat = 1;
                        String yearString = stringArrayForSlash.get(2);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForSlash.get(0);
                        date = date.trim();
                        formatString = month + "/" + date + "/" + yearString;
                    }
                    else if (stringArrayForSlash.get(0).trim().length() == 4) {
                        setFormat = 2;
                        String yearString = stringArrayForSlash.get(0);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForSlash.get(2);
                        date = date.trim();
                        formatString = yearString + "/" + month + "/" + date;
                    }
                    else if (stringArrayForSlash.get(2).trim().length() == 2 || stringArrayForSlash.get(2).trim().length() == 1) {
                        setFormat = 1;
                        String yearString = stringArrayForSlash.get(2);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForSlash.get(0);
                        date = date.trim();
                        formatString = month + "/" + date + "/" + yearString;
                    }
                }
                else if (stringArrayForSlash.get(0).trim().length() == 4) {
                    setFormat = 2;
                    String yearString2 = stringArrayForSlash.get(0);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForSlash.get(1);
                    monthString = monthString.trim();
                    String date2 = stringArrayForSlash.get(2);
                    date2 = date2.trim();
                    formatString = yearString2 + "/" + monthString + "/" + date2;
                }
                else if (stringArrayForSlash.get(2).trim().length() == 4) {
                    setFormat = 1;
                    String yearString2 = stringArrayForSlash.get(2);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForSlash.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForSlash.get(1);
                    date2 = date2.trim();
                    formatString = monthString + "/" + date2 + "/" + yearString2;
                }
                else if (stringArrayForSlash.get(1).trim().length() == 4) {
                    setFormat = 3;
                    String yearString2 = stringArrayForSlash.get(1);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForSlash.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForSlash.get(2);
                    date2 = date2.trim();
                    formatString = monthString + "/" + yearString2 + "/" + date2;
                }
                else if (stringArrayForSlash.get(2).trim().length() == 2 || stringArrayForSlash.get(2).trim().length() == 1) {
                    setFormat = 1;
                    String yearString2 = stringArrayForSlash.get(2);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForSlash.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForSlash.get(1);
                    date2 = date2.trim();
                    formatString = monthString + "/" + date2 + "/" + yearString2;
                }
                if (setFormat == 1) {
                    formatString = "'" + formatString + "'" + " ," + "'MM/DD/" + year + "')";
                }
                else if (setFormat == 2) {
                    formatString = "'" + formatString + "'" + " ," + "'" + year + "/MM/DD" + "')";
                }
                else if (setFormat == 3) {
                    formatString = "'" + formatString + "'" + " ," + "'MM/" + year + "/DD" + "')";
                }
                return formatString;
            }
            if (stringArrayForSlash.size() == 4) {
                String monthString = stringArrayForSlash.get(1);
                monthString = monthString.trim();
                String year = null;
                if (monthString.equalsIgnoreCase("Jan") || monthString.equalsIgnoreCase("feb") || monthString.equalsIgnoreCase("mar") || monthString.equalsIgnoreCase("apr") || monthString.equalsIgnoreCase("may") || monthString.equalsIgnoreCase("jun") || monthString.equalsIgnoreCase("jul") || monthString.equalsIgnoreCase("aug") || monthString.equalsIgnoreCase("sep") || monthString.equalsIgnoreCase("oct") || monthString.equalsIgnoreCase("nov") || monthString.equalsIgnoreCase("dec")) {
                    final String month = this.monthFormat(monthString);
                    if (stringArrayForSlash.get(2).length() == 4) {
                        setFormat = 1;
                        String yearString = stringArrayForSlash.get(2);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForSlash.get(0);
                        date = date.trim();
                        formatString = month + "/" + date + "/" + yearString;
                    }
                    else if (stringArrayForSlash.get(0).trim().length() == 4) {
                        setFormat = 2;
                        String yearString = stringArrayForSlash.get(0);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForSlash.get(2);
                        date = date.trim();
                        formatString = yearString + "/" + month + "/" + date;
                    }
                    else if (stringArrayForSlash.get(2).trim().length() == 2 || stringArrayForSlash.get(2).trim().length() == 1) {
                        setFormat = 1;
                        String yearString = stringArrayForSlash.get(2);
                        yearString = yearString.trim();
                        final int yearLength = yearString.length();
                        year = this.yearFormat(yearLength);
                        String date = stringArrayForSlash.get(0);
                        date = date.trim();
                        formatString = month + "/" + date + "/" + yearString;
                    }
                }
                else if (stringArrayForSlash.get(0).trim().length() == 4) {
                    setFormat = 2;
                    String yearString2 = stringArrayForSlash.get(0);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForSlash.get(1);
                    monthString = monthString.trim();
                    String date2 = stringArrayForSlash.get(2);
                    date2 = date2.trim();
                    formatString = yearString2 + "/" + monthString + "/" + date2;
                }
                else if (stringArrayForSlash.get(2).trim().length() == 4) {
                    setFormat = 1;
                    String yearString2 = stringArrayForSlash.get(2);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForSlash.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForSlash.get(1);
                    date2 = date2.trim();
                    formatString = monthString + "/" + date2 + "/" + yearString2;
                }
                else if (stringArrayForSlash.get(1).trim().length() == 4) {
                    setFormat = 3;
                    String yearString2 = stringArrayForSlash.get(1);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForSlash.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForSlash.get(2);
                    date2 = date2.trim();
                    formatString = monthString + "/" + yearString2 + "/" + date2;
                }
                else if (stringArrayForSlash.get(2).trim().length() == 2 || stringArrayForSlash.get(2).trim().length() == 1) {
                    setFormat = 1;
                    String yearString2 = stringArrayForSlash.get(2);
                    yearString2 = yearString2.trim();
                    final int yearLength2 = yearString2.length();
                    year = this.yearFormat(yearLength2);
                    monthString = stringArrayForSlash.get(0);
                    monthString = monthString.trim();
                    String date2 = stringArrayForSlash.get(1);
                    date2 = date2.trim();
                    formatString = monthString + "/" + date2 + "/" + yearString2;
                }
                formatString = formatString + " " + stringArrayForSlash.get(3);
                if (setFormat == 1) {
                    formatString = "'" + formatString + "'" + " ," + "'MM/DD/" + year + " HH24:MI:SS')";
                }
                else if (setFormat == 2) {
                    formatString = "'" + formatString + "'" + " ," + "'" + year + "/MM/DD" + " HH24:MI:SS')";
                }
                else if (setFormat == 3) {
                    formatString = "'" + formatString + "'" + " ," + "'MM/" + year + "/DD" + " HH24:MI:SS')";
                }
            }
            return formatString;
        }
    }
    
    public String monthFormat(final String str) {
        this.monthFormat = str;
        if (this.monthFormat.equalsIgnoreCase("Jan")) {
            this.monthFormat = "01";
        }
        else if (this.monthFormat.equalsIgnoreCase("Feb")) {
            this.monthFormat = "02";
        }
        else if (this.monthFormat.equalsIgnoreCase("Mar")) {
            this.monthFormat = "03";
        }
        else if (this.monthFormat.equalsIgnoreCase("Apr")) {
            this.monthFormat = "04";
        }
        else if (this.monthFormat.equalsIgnoreCase("May")) {
            this.monthFormat = "05";
        }
        else if (this.monthFormat.equalsIgnoreCase("Jun")) {
            this.monthFormat = "06";
        }
        else if (this.monthFormat.equalsIgnoreCase("Jul")) {
            this.monthFormat = "07";
        }
        else if (this.monthFormat.equalsIgnoreCase("Aug")) {
            this.monthFormat = "08";
        }
        else if (this.monthFormat.equalsIgnoreCase("Sep")) {
            this.monthFormat = "09";
        }
        else if (this.monthFormat.equalsIgnoreCase("Oct")) {
            this.monthFormat = "10";
        }
        else if (this.monthFormat.equalsIgnoreCase("Nov")) {
            this.monthFormat = "11";
        }
        else if (this.monthFormat.equalsIgnoreCase("Dec")) {
            this.monthFormat = "12";
        }
        return this.monthFormat;
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionArguments.size() > 2) {
            throw new ConvertException("Number of arguments have exceeded the allowed limit");
        }
        final Vector vector = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String date1 = vector.get(0).toString();
        String date2 = vector.get(1).toString();
        date1 = StringFunctions.handleLiteralStringDateForOracle(date1);
        date2 = StringFunctions.handleLiteralStringDateForOracle(date2);
        final String query = " TRUNC(" + date1 + ") - TRUNC(" + date2 + ")";
        this.functionName.setColumnName(query);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.getFunctionName().getColumnName().equalsIgnoreCase("unix_timestamp")) {
            arguments.add(0, "ss");
            arguments.add(1, "'1970-01-01 00:00:00'");
            if (arguments.get(2) instanceof SelectColumn) {
                final SelectColumn argFuncSC = arguments.get(2);
                final Vector argFuncSCColExpr = argFuncSC.getColumnExpression();
                if (argFuncSCColExpr != null && argFuncSCColExpr.get(0) instanceof FunctionCalls) {
                    final FunctionCalls argFunc = argFuncSCColExpr.get(0);
                    if (argFunc.getFunctionName().getColumnName().equalsIgnoreCase("getdate")) {
                        argFunc.getFunctionName().setColumnName("GETUTCDATE");
                    }
                }
            }
        }
        if (from_sqs != null && from_sqs.isMSAzure()) {
            final String arg1 = arguments.get(0).toString();
            final String arg2 = arguments.get(1).toString();
            arguments = new Vector();
            arguments.addElement("dd");
            arguments.addElement(arg2);
            arguments.addElement(arg1);
        }
        this.functionName.setColumnName("DATEDIFF");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        if (this.functionArguments.size() == 3) {
            final Object obj = this.functionArguments.get(0);
            final String arg1 = obj.toString().trim();
            if (arg1.equalsIgnoreCase("month") || arg1.equalsIgnoreCase("mm") || arg1.equalsIgnoreCase("m")) {
                this.functionName.setColumnName("");
                final Object arg2 = this.functionArguments.get(2);
                if (arg2 instanceof SelectColumn) {
                    this.functionName.setColumnName("");
                    final SelectColumn selectArg1 = new SelectColumn();
                    final Vector newArgument = new Vector();
                    final Object arg3 = this.functionArguments.get(1);
                    final TableColumn innerFunction = new TableColumn();
                    innerFunction.setColumnName("MONTH");
                    final FunctionCalls month1 = new FunctionCalls();
                    final Vector month1Arg = new Vector();
                    month1Arg.add(arg2);
                    month1.setFunctionName(innerFunction);
                    month1.setFunctionArguments(month1Arg);
                    newArgument.add(month1);
                    newArgument.add(" - ");
                    final TableColumn innerFunction2 = new TableColumn();
                    innerFunction2.setColumnName("MONTH");
                    final FunctionCalls month2 = new FunctionCalls();
                    final Vector month2Arg = new Vector();
                    month2Arg.add(arg3);
                    month2.setFunctionName(innerFunction2);
                    month2.setFunctionArguments(month2Arg);
                    newArgument.add(month2);
                    selectArg1.setColumnExpression(newArgument);
                    final Vector argument = new Vector();
                    argument.add(selectArg1);
                    this.setFunctionArguments(argument);
                }
                else if (arg2 instanceof String) {
                    this.functionArguments.setElementAt(arg2 + " - " + this.functionArguments.get(1).toString(), 0);
                    this.functionArguments.setSize(1);
                }
            }
            else if (arg1.equalsIgnoreCase("day") || arg1.equalsIgnoreCase("dd") || arg1.equalsIgnoreCase("d")) {
                this.functionName.setColumnName("");
                final Object arg2 = this.functionArguments.get(2);
                if (arg2 instanceof SelectColumn) {
                    this.functionName.setColumnName("");
                    final SelectColumn selectArg1 = new SelectColumn();
                    final Vector newArgument = new Vector();
                    final Object arg3 = this.functionArguments.get(1);
                    final TableColumn innerFunction = new TableColumn();
                    innerFunction.setColumnName("DAYS");
                    final FunctionCalls dayfunction1 = new FunctionCalls();
                    final Vector day1arg = new Vector();
                    day1arg.add(arg2);
                    dayfunction1.setFunctionName(innerFunction);
                    dayfunction1.setFunctionArguments(day1arg);
                    newArgument.add(dayfunction1);
                    newArgument.add(" - ");
                    final TableColumn innerFunction2 = new TableColumn();
                    innerFunction2.setColumnName("DAYS");
                    final FunctionCalls dayfunction2 = new FunctionCalls();
                    final Vector day2arg = new Vector();
                    day2arg.add(arg3);
                    dayfunction2.setFunctionName(innerFunction2);
                    dayfunction2.setFunctionArguments(day2arg);
                    newArgument.add(dayfunction2);
                    selectArg1.setColumnExpression(newArgument);
                    final Vector argument = new Vector();
                    argument.add(selectArg1);
                    this.setFunctionArguments(argument);
                }
                else if (arg2 instanceof String) {
                    this.functionArguments.setElementAt(arg2 + " - " + this.functionArguments.get(1).toString(), 0);
                    this.functionArguments.setSize(1);
                }
            }
            else if (arg1.equalsIgnoreCase("week") || arg1.equalsIgnoreCase("wk") || arg1.equalsIgnoreCase("ww")) {
                this.functionName.setColumnName("");
                final Object arg2 = this.functionArguments.get(2);
                if (arg2 instanceof SelectColumn) {
                    this.functionName.setColumnName("");
                    final SelectColumn selectArg1 = new SelectColumn();
                    final Vector newArgument = new Vector();
                    final Object arg3 = this.functionArguments.get(1);
                    final TableColumn innerFunction = new TableColumn();
                    innerFunction.setColumnName("WEEK");
                    final FunctionCalls weekfunction1 = new FunctionCalls();
                    final Vector week1arg = new Vector();
                    week1arg.add(arg2);
                    weekfunction1.setFunctionName(innerFunction);
                    weekfunction1.setFunctionArguments(week1arg);
                    newArgument.add(weekfunction1);
                    newArgument.add(" - ");
                    final TableColumn innerFunction2 = new TableColumn();
                    innerFunction2.setColumnName("WEEK");
                    final FunctionCalls weekfunction2 = new FunctionCalls();
                    final Vector week2arg = new Vector();
                    week2arg.add(arg3);
                    weekfunction2.setFunctionName(innerFunction2);
                    weekfunction2.setFunctionArguments(week2arg);
                    newArgument.add(weekfunction2);
                    selectArg1.setColumnExpression(newArgument);
                    final Vector argument = new Vector();
                    argument.add(selectArg1);
                    this.setFunctionArguments(argument);
                }
                else if (arg2 instanceof String) {
                    this.functionArguments.setElementAt(arg2 + " - " + this.functionArguments.get(1).toString() + "*7", 0);
                    this.functionArguments.setSize(1);
                }
            }
            else if (arg1.equalsIgnoreCase("year") || arg1.equalsIgnoreCase("yy")) {
                this.dateDiffToDB2("YEAR");
            }
            else if (arg1.equalsIgnoreCase("quarter") || arg1.equalsIgnoreCase("qq")) {
                this.dateDiffToDB2("QUARTER");
            }
            else if (arg1.equalsIgnoreCase("dayofyear") || arg1.equalsIgnoreCase("dy")) {
                this.dateDiffToDB2("DAYOFYEAR");
            }
            else if (arg1.equalsIgnoreCase("weekday") || arg1.equalsIgnoreCase("dw")) {
                this.dateDiffToDB2("DAYOFWEEK");
            }
            else if (arg1.equalsIgnoreCase("hour") || arg1.equalsIgnoreCase("hh")) {
                this.dateDiffToDB2("HOUR");
            }
            else if (arg1.equalsIgnoreCase("minute") || arg1.equalsIgnoreCase("mi")) {
                this.dateDiffToDB2("MINUTE");
            }
            else if (arg1.equalsIgnoreCase("second") || arg1.equalsIgnoreCase("ss")) {
                this.dateDiffToDB2("SECOND");
            }
        }
    }
    
    private void dateDiffToDB2(final String fnName) {
        this.functionName.setColumnName("");
        final Object arg3 = this.functionArguments.get(2);
        if (arg3 instanceof SelectColumn) {
            this.functionName.setColumnName("");
            final SelectColumn selectArg1 = new SelectColumn();
            final Vector newArgument = new Vector();
            final Object arg4 = this.functionArguments.get(1);
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setColumnName(fnName);
            final FunctionCalls function1 = new FunctionCalls();
            final Vector week1arg = new Vector();
            week1arg.add(arg3);
            function1.setFunctionName(innerFunction);
            function1.setFunctionArguments(week1arg);
            newArgument.add(function1);
            newArgument.add(" - ");
            final TableColumn innerFunction2 = new TableColumn();
            innerFunction2.setColumnName(fnName);
            final FunctionCalls function2 = new FunctionCalls();
            final Vector week2arg = new Vector();
            week2arg.add(arg4);
            function2.setFunctionName(innerFunction2);
            function2.setFunctionArguments(week2arg);
            newArgument.add(function2);
            selectArg1.setColumnExpression(newArgument);
            final Vector argument = new Vector();
            argument.add(selectArg1);
            this.setFunctionArguments(argument);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionArguments.size() == 1) {
            throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported. \n Please ensure that the correct number of arguments are passed\n");
        }
        this.functionName.setColumnName("DATE_MI");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (this.functionArguments.size() == 2 || (i_count > 0 && this.functionArguments.size() == 3)) {
                    this.handleStringLiteralForDateTime(from_sqs, i_count, false);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        for (int i_count = 0; i_count < arguments.size(); ++i_count) {
            if (arguments.get(i_count).toString().trim().equalsIgnoreCase("now()")) {
                arguments.set(i_count, "cast(now() as date)");
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionArguments.size() == 3) {
            final Object obj = this.functionArguments.get(0);
            final String arg1 = obj.toString().trim();
            if (arg1.equalsIgnoreCase("day") || arg1.equalsIgnoreCase("dd") || arg1.equalsIgnoreCase("d") || arg1.equalsIgnoreCase("dayofyear") || arg1.equalsIgnoreCase("dy") || arg1.equalsIgnoreCase("y") || arg1.equalsIgnoreCase("weekday") || arg1.equalsIgnoreCase("dw")) {
                final Object arg2 = this.functionArguments.get(2);
                this.functionArguments.setElementAt(arg2, 0);
                this.functionArguments.setElementAt(this.functionArguments.get(1), 1);
                this.functionArguments.setSize(2);
            }
            else {
                if (!SwisSQLOptions.passFunctionsWithOutThrowingConvertException) {
                    throw new ConvertException("DATEDIFF function is supported only for 'DAY' in PostgreSQL");
                }
                this.functionName.setColumnName("DATEDIFF");
            }
        }
        this.functionArguments.setElementAt("DATE(" + this.functionArguments.get(0) + ")", 0);
        this.functionArguments.setElementAt("DATE(" + this.functionArguments.get(1) + ")", 1);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        if (this.functionArguments.size() == 3) {
            final Vector newArguments = new Vector();
            String format = null;
            TableColumn tc = null;
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.get(0);
                final Vector colExp = sc.getColumnExpression();
                if (colExp.get(0) instanceof TableColumn) {
                    tc = colExp.get(0);
                    format = tc.getColumnName();
                }
            }
            if (format != null) {
                if (format.equalsIgnoreCase("dd") || format.equalsIgnoreCase("d") || format.equalsIgnoreCase("dy") || format.equalsIgnoreCase("y") || format.equalsIgnoreCase("day") || format.equalsIgnoreCase("dayofyear") || format.equalsIgnoreCase("weekday") || format.equalsIgnoreCase("dw") || format.equalsIgnoreCase("w")) {
                    newArguments.add(this.functionArguments.get(2));
                    newArguments.add(this.functionArguments.get(1));
                    this.setFunctionArguments(newArguments);
                }
                else if (format.equalsIgnoreCase("mm") || format.equalsIgnoreCase("m") || format.equalsIgnoreCase("month")) {
                    tc.setColumnName("MONTH");
                    this.functionName.setColumnName("TIMESTAMPDIFF");
                    this.setFunctionArguments(this.getNewFunctionArgs("'%Y-%m-01'", arguments));
                }
                else if (format.equalsIgnoreCase("hour") || format.equalsIgnoreCase("hh")) {
                    tc.setColumnName("HOUR");
                    this.functionName.setColumnName("TIMESTAMPDIFF");
                    this.setFunctionArguments(this.getNewFunctionArgs("'%Y-%m-%d %H:00:00'", arguments));
                }
                else if (format.equalsIgnoreCase("minute") || format.equalsIgnoreCase("mi") || format.equalsIgnoreCase("n")) {
                    tc.setColumnName("MINUTE");
                    this.functionName.setColumnName("TIMESTAMPDIFF");
                    this.setFunctionArguments(this.getNewFunctionArgs("'%Y-%m-%d %H:%i:00'", arguments));
                }
                else if (format.equalsIgnoreCase("second") || format.equalsIgnoreCase("ss") || format.equalsIgnoreCase("s")) {
                    tc.setColumnName("SECOND");
                    this.functionName.setColumnName("TIMESTAMPDIFF");
                    this.setFunctionArguments(arguments);
                }
                else if (format.equalsIgnoreCase("millisecond") || format.equalsIgnoreCase("ms") || format.equalsIgnoreCase("nanosecond") || format.equalsIgnoreCase("ns")) {
                    String defVal = "1000";
                    if (format.equalsIgnoreCase("nanosecond") || format.equalsIgnoreCase("ns")) {
                        defVal = "1000000000";
                    }
                    tc.setColumnName("SECOND");
                    final SelectColumn selColumn = new SelectColumn();
                    final Vector colExp2 = new Vector();
                    final Vector fnArgs = new Vector();
                    final FunctionCalls newFunction1 = this.getNewFunction("TIMESTAMPDIFF", arguments);
                    colExp2.add(newFunction1);
                    colExp2.add("*");
                    colExp2.add(defVal);
                    selColumn.setColumnExpression(colExp2);
                    fnArgs.add(selColumn);
                    this.functionName.setColumnName("");
                    this.setFunctionArguments(fnArgs);
                }
                else if (format.equalsIgnoreCase("microsecond") || format.equalsIgnoreCase("mcs")) {
                    tc.setColumnName("MICROSECOND");
                    this.functionName.setColumnName("TIMESTAMPDIFF");
                    this.setFunctionArguments(arguments);
                }
                else if (format.equalsIgnoreCase("quarter") || format.equalsIgnoreCase("qq") || format.equalsIgnoreCase("q")) {
                    tc.setColumnName("QUARTER");
                    this.functionName.setColumnName("TIMESTAMPDIFF");
                    this.setFunctionArguments(arguments);
                }
                else if (format.equalsIgnoreCase("week") || format.equalsIgnoreCase("ww") || format.equalsIgnoreCase("wk")) {
                    tc.setColumnName("WEEK");
                    this.functionName.setColumnName("TIMESTAMPDIFF");
                    this.setFunctionArguments(arguments);
                }
                else if (format.equalsIgnoreCase("yy") || format.equalsIgnoreCase("year") || format.equalsIgnoreCase("yyyy")) {
                    final SelectColumn newSelectColumn = new SelectColumn();
                    final Vector colExp = new Vector();
                    final SelectColumn fromDate = this.functionArguments.get(2);
                    final FunctionCalls newFunction2 = this.getNewFunction("EXTRACT", fromDate);
                    newFunction2.setTrailingString("YEAR");
                    newFunction2.setFromInTrim("FROM");
                    final SelectColumn toDate = this.functionArguments.get(1);
                    final FunctionCalls newFunction3 = this.getNewFunction("EXTRACT", toDate);
                    newFunction3.setTrailingString("YEAR");
                    newFunction3.setFromInTrim("FROM");
                    colExp.add(newFunction2);
                    colExp.add(" - ");
                    colExp.add(newFunction3);
                    newSelectColumn.setColumnExpression(colExp);
                    newArguments.add(newSelectColumn);
                    this.functionName.setColumnName("");
                    this.setFunctionArguments(newArguments);
                }
            }
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe function DATEDIFF is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
    }
    
    private FunctionCalls getNewFunction(final String newFunctionName, final SelectColumn sc) {
        final FunctionCalls newFunction = new FunctionCalls();
        final Vector args = new Vector();
        final TableColumn tc = new TableColumn();
        tc.setColumnName(newFunctionName);
        args.add(sc);
        newFunction.setFunctionName(tc);
        newFunction.setFunctionArguments(args);
        return newFunction;
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("datediff")) {
            final String[] arguments = new String[2];
            for (int i_count = 0; i_count < 2; ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    this.handleStringLiteralForDateTime(from_sqs, i_count, false);
                    arguments[i_count] = "" + this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs);
                }
                else {
                    arguments[i_count] = "" + this.functionArguments.elementAt(i_count);
                }
            }
            this.functionName.setColumnName("TIMESTAMPDIFF(DAY,CAST(" + arguments[1] + " AS DATE) , CAST(" + arguments[0] + " AS DATE))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("unix_timestamp")) {
            final Vector arguments2 = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    this.handleStringLiteralForDateTime(from_sqs, 0, false);
                    arguments2.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments2.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            this.setFunctionArguments(arguments2);
        }
    }
    
    private FunctionCalls getNewFunction(final String newFunctionName, final SelectColumn[] scArr) {
        final FunctionCalls newFunction = new FunctionCalls();
        final Vector args = new Vector();
        final TableColumn tc = new TableColumn();
        tc.setColumnName(newFunctionName);
        if (scArr != null) {
            for (final SelectColumn sc : scArr) {
                args.add(sc);
            }
        }
        newFunction.setFunctionName(tc);
        newFunction.setFunctionArguments(args);
        return newFunction;
    }
    
    private FunctionCalls getNewFunction(final String newFunctionName, final Vector fnArgs) {
        final FunctionCalls newFunction = new FunctionCalls();
        final Vector args = new Vector();
        final TableColumn tc = new TableColumn();
        tc.setColumnName(newFunctionName);
        if (fnArgs != null) {
            args.addAll(fnArgs);
        }
        newFunction.setFunctionName(tc);
        newFunction.setFunctionArguments(args);
        return newFunction;
    }
    
    private Vector getNewFunctionArgs(final String format, final Vector arguments) {
        final SelectColumn newSelectColumn1 = new SelectColumn();
        final Vector colExp1 = new Vector();
        final SelectColumn newSelectColumn2 = new SelectColumn();
        final Vector colExp2 = new Vector();
        final SelectColumn fromDate = arguments.get(1);
        final SelectColumn toDate = arguments.get(2);
        colExp1.add("'%Y-%m-01'");
        newSelectColumn1.setColumnExpression(colExp1);
        final SelectColumn[] arr = { fromDate, newSelectColumn1 };
        final FunctionCalls newFunction1 = this.getNewFunction("DATE_FORMAT", arr);
        colExp2.add(newFunction1);
        newSelectColumn2.setColumnExpression(colExp2);
        final SelectColumn[] arr2 = { toDate, newSelectColumn1 };
        final FunctionCalls newFunction2 = this.getNewFunction("DATE_FORMAT", arr2);
        final SelectColumn newSelectColumn3 = new SelectColumn();
        final Vector colExp3 = new Vector();
        colExp3.add(newFunction2);
        newSelectColumn3.setColumnExpression(colExp3);
        final Vector newArgs = new Vector();
        newArgs.add(arguments.get(0));
        newArgs.add(newSelectColumn2);
        newArgs.add(newSelectColumn3);
        return newArgs;
    }
}
