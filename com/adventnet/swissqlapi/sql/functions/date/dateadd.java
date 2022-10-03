package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Collection;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.StringTokenizer;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class dateadd extends FunctionCalls
{
    private String dateFormatString;
    private String monthFormat;
    private String yearFormat;
    private int yearSize;
    
    public dateadd() {
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
            }
            else if (stringArrayForHiphen.size() == 4) {
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
        }
        else if (indexOfSlash != -1) {
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
            }
            else if (stringArrayForSlash.size() == 4) {
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
        }
        if (formatString.equals("")) {
            formatString = SwisSQLUtils.getDateFormat(str, 1);
            if (formatString == null) {
                return str;
            }
            if (formatString.startsWith("'1900")) {
                formatString += ", 'YYYY-MM-DD HH24:MI:SS')";
            }
            else {
                formatString = str + ", " + formatString + ")";
            }
        }
        return formatString;
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
        final String funName = this.functionName.getColumnName();
        boolean monthOrQuarterOrYearNotConverted = true;
        boolean isDate_AddOrDate_Sub = false;
        this.functionName.setColumnName("TO_DATE");
        final Vector arguments = new Vector();
        final Vector tempArguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                tempArguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                tempArguments.addElement(this.functionArguments.elementAt(i_count));
            }
            if (i_count == 2 && this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            if (funName != null && (funName.trim().equalsIgnoreCase("DATE_ADD") || funName.trim().equalsIgnoreCase("DATE_SUB")) && i_count == 0 && this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
                isDate_AddOrDate_Sub = true;
            }
            if (funName != null && (funName.trim().equalsIgnoreCase("DATE_ADD") || funName.trim().equalsIgnoreCase("DATE_SUB")) && i_count == 1 && this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
                isDate_AddOrDate_Sub = true;
            }
        }
        String timeString = new String();
        final SelectColumn dateSelectColumn = arguments.elementAt(0);
        final String dateSelectColumnString = dateSelectColumn.toString();
        String newDateFormat = this.dateFormatConversion(dateSelectColumnString);
        String toDateExpression = null;
        if (tempArguments.elementAt(0) instanceof SelectColumn) {
            final SelectColumn tempSelectColumn = tempArguments.elementAt(0).toOracleSelect(to_sqs, from_sqs);
            timeString = tempSelectColumn.toString();
            timeString = timeString.trim();
            if (timeString.equalsIgnoreCase("year") || timeString.equalsIgnoreCase("yy") || timeString.equalsIgnoreCase("yyyy")) {
                monthOrQuarterOrYearNotConverted = false;
                final StringBuffer yearBuffer = new StringBuffer();
                this.functionName.setColumnName("ADD_MONTHS");
                if (arguments.elementAt(0) instanceof SelectColumn) {
                    final SelectColumn tempSelectColumn2 = arguments.elementAt(0).toOracleSelect(to_sqs, from_sqs);
                    String getDateString = tempSelectColumn2.toString();
                    final int indexOfSlash = getDateString.indexOf(47);
                    final int indexOfHiphen = getDateString.indexOf(45);
                    getDateString = getDateString.trim();
                    if (getDateString.equalsIgnoreCase("(Sysdate)")) {
                        yearBuffer.append(getDateString);
                    }
                    else if (indexOfSlash != -1 || indexOfHiphen != -1 || !newDateFormat.equals(dateSelectColumnString)) {
                        if (!dateSelectColumnString.equalsIgnoreCase(newDateFormat)) {
                            yearBuffer.append("to_date(");
                        }
                        yearBuffer.append(newDateFormat);
                    }
                    else {
                        yearBuffer.append(getDateString);
                    }
                }
                if (tempArguments.elementAt(1) instanceof SelectColumn) {
                    final SelectColumn tempSelectColumn3 = tempArguments.elementAt(1).toOracleSelect(to_sqs, from_sqs);
                    timeString = tempSelectColumn3.toString();
                    yearBuffer.append("," + timeString + "*12");
                }
                arguments.setElementAt(yearBuffer, 0);
            }
            else if (timeString.equalsIgnoreCase("quarter") || timeString.equalsIgnoreCase("qq") || timeString.equalsIgnoreCase("q")) {
                monthOrQuarterOrYearNotConverted = false;
                final StringBuffer quarterBuffer = new StringBuffer();
                this.functionName.setColumnName("ADD_MONTHS");
                if (arguments.elementAt(0) instanceof SelectColumn) {
                    final SelectColumn tempSelectColumn2 = arguments.elementAt(0).toOracleSelect(to_sqs, from_sqs);
                    String getDateString = tempSelectColumn2.toString();
                    getDateString = getDateString.trim();
                    final int indexOfSlash = getDateString.indexOf(47);
                    final int indexOfHiphen = getDateString.indexOf(45);
                    if (getDateString.equalsIgnoreCase("(Sysdate)")) {
                        quarterBuffer.append(getDateString);
                    }
                    else if (indexOfSlash != -1 || indexOfHiphen != -1 || !newDateFormat.equals(dateSelectColumnString)) {
                        if (!dateSelectColumnString.equalsIgnoreCase(newDateFormat)) {
                            quarterBuffer.append("to_date(");
                        }
                        quarterBuffer.append(newDateFormat);
                    }
                    else {
                        quarterBuffer.append(getDateString);
                    }
                }
                if (tempArguments.elementAt(1) instanceof SelectColumn) {
                    final SelectColumn tempSelectColumn3 = tempArguments.elementAt(1).toOracleSelect(to_sqs, from_sqs);
                    timeString = tempSelectColumn3.toString();
                    quarterBuffer.append("," + timeString + "*3");
                }
                arguments.setElementAt(quarterBuffer, 0);
            }
            else if (timeString.equalsIgnoreCase("month") || timeString.equalsIgnoreCase("mm") || timeString.equalsIgnoreCase("m")) {
                monthOrQuarterOrYearNotConverted = false;
                final StringBuffer monthBuffer = new StringBuffer();
                this.functionName.setColumnName("ADD_MONTHS");
                if (arguments.elementAt(0) instanceof SelectColumn) {
                    final SelectColumn tempSelectColumn2 = arguments.elementAt(0).toOracleSelect(to_sqs, from_sqs);
                    String getDateString = tempSelectColumn2.toString();
                    getDateString = getDateString.trim();
                    final int indexOfSlash = getDateString.indexOf(47);
                    final int indexOfHiphen = getDateString.indexOf(45);
                    if (getDateString.equalsIgnoreCase("(Sysdate)")) {
                        monthBuffer.append(getDateString);
                    }
                    else if (indexOfHiphen != -1 || indexOfSlash != -1 || !newDateFormat.equals(dateSelectColumnString)) {
                        if (!dateSelectColumnString.equalsIgnoreCase(newDateFormat)) {
                            monthBuffer.append("to_date(");
                        }
                        monthBuffer.append(newDateFormat);
                    }
                    else {
                        monthBuffer.append(getDateString);
                    }
                }
                if (tempArguments.elementAt(1) instanceof SelectColumn) {
                    final SelectColumn tempSelectColumn3 = tempArguments.elementAt(1).toOracleSelect(to_sqs, from_sqs);
                    timeString = tempSelectColumn3.toString();
                    monthBuffer.append("," + timeString);
                }
                arguments.setElementAt(monthBuffer, 0);
            }
            else if (timeString.equalsIgnoreCase("week") || timeString.equalsIgnoreCase("wk") || timeString.equalsIgnoreCase("ww")) {
                this.setToDateExpression("7");
            }
            else if (timeString.equalsIgnoreCase("dayofyear") || timeString.equalsIgnoreCase("day") || timeString.equalsIgnoreCase("y") || timeString.equalsIgnoreCase("dy") || timeString.equalsIgnoreCase("dd") || timeString.equalsIgnoreCase("d") || timeString.equalsIgnoreCase("weekday") || timeString.equalsIgnoreCase("dw")) {
                this.setToDateExpression("1");
            }
            else if (timeString.equalsIgnoreCase("hour") || timeString.equalsIgnoreCase("hh")) {
                toDateExpression = "1/24";
                if (dateSelectColumnString.equalsIgnoreCase("(Sysdate)")) {
                    this.functionName.setColumnName("");
                }
            }
            else if (timeString.equalsIgnoreCase("minute") || timeString.equalsIgnoreCase("mi") || timeString.equalsIgnoreCase("n")) {
                toDateExpression = "1/24/60";
                if (dateSelectColumnString.equalsIgnoreCase("(Sysdate)")) {
                    this.functionName.setColumnName("");
                }
            }
            else if (timeString.equalsIgnoreCase("second") || timeString.equalsIgnoreCase("ss") || timeString.equalsIgnoreCase("s")) {
                toDateExpression = "1/24/60/60";
                if (dateSelectColumnString.equalsIgnoreCase("(Sysdate)")) {
                    this.functionName.setColumnName("");
                }
            }
            else if (timeString.equalsIgnoreCase("millisecond") || timeString.equalsIgnoreCase("ms")) {
                this.functionName.setColumnName("TO_TIMESTAMP");
                toDateExpression = "1/24/60/60/60";
                if (newDateFormat.endsWith(":SS')")) {
                    newDateFormat = newDateFormat.substring(0, newDateFormat.length() - 2) + ".FF')";
                }
            }
        }
        if (monthOrQuarterOrYearNotConverted) {
            if (arguments.elementAt(0) instanceof SelectColumn) {
                final SelectColumn tempSelectColumn = arguments.elementAt(0).toOracleSelect(to_sqs, from_sqs);
                String getDateString2 = tempSelectColumn.toString();
                getDateString2 = getDateString2.trim();
                final int indexOfSlash2 = getDateString2.indexOf(47);
                final int indexOfHiphen2 = getDateString2.indexOf(45);
                if (indexOfSlash2 != -1 || indexOfHiphen2 != -1 || !newDateFormat.equals(dateSelectColumnString)) {
                    if (!dateSelectColumnString.equalsIgnoreCase(newDateFormat)) {
                        newDateFormat = newDateFormat.substring(0, newDateFormat.length() - 1);
                    }
                    arguments.setElementAt(newDateFormat, 0);
                }
            }
            if (tempArguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn tempSelectColumn = tempArguments.elementAt(1).toOracleSelect(to_sqs, from_sqs);
                timeString = tempSelectColumn.toString();
                if (toDateExpression == null) {
                    this.setToDateSymbolValue(timeString);
                }
                else {
                    final Object obj = arguments.elementAt(0);
                    if (obj instanceof SelectColumn) {
                        final SelectColumn sc = (SelectColumn)obj;
                        final Vector colExpr = sc.getColumnExpression();
                        colExpr.add("+ (" + toDateExpression + " * " + timeString + ")");
                        arguments.setElementAt(sc, 0);
                    }
                    else {
                        this.setToDateExpression(toDateExpression);
                        this.setToDateSymbolValue(timeString);
                    }
                }
            }
        }
        if (isDate_AddOrDate_Sub) {
            final StringBuffer dateAddInOracleSB = new StringBuffer();
            String numFunc = null;
            String unitStr = null;
            String exprStr = null;
            final int size = arguments.size();
            if (size == 2) {
                if (arguments.get(0) instanceof SelectColumn) {
                    dateAddInOracleSB.append(arguments.get(0).toString());
                    if (funName.trim().equalsIgnoreCase("DATE_ADD")) {
                        dateAddInOracleSB.append(" + ");
                    }
                    else if (funName.trim().equalsIgnoreCase("DATE_SUB")) {
                        dateAddInOracleSB.append(" - ");
                    }
                }
                if (arguments.get(1) instanceof SelectColumn) {
                    final SelectColumn selCol = arguments.get(1);
                    final Vector colExp = selCol.getColumnExpression();
                    if (colExp != null) {
                        final Vector colExpNew = new Vector();
                        for (int i = 0; i < colExp.size(); ++i) {
                            if (colExp.get(i) instanceof String) {
                                final String str1 = colExp.get(i);
                                if (str1.trim().equalsIgnoreCase("DAY") || str1.trim().equalsIgnoreCase("HOUR") || str1.trim().equalsIgnoreCase("MINUTE") || str1.trim().equalsIgnoreCase("SECOND")) {
                                    numFunc = "NUMTODSINTERVAL";
                                    unitStr = "'" + str1.trim() + "'";
                                }
                                else if (str1.trim().equalsIgnoreCase("MONTH") || str1.trim().equalsIgnoreCase("YEAR")) {
                                    numFunc = "NUMTOYMINTERVAL";
                                    unitStr = "'" + str1.trim() + "'";
                                }
                                else {
                                    exprStr = str1.trim();
                                }
                            }
                        }
                        dateAddInOracleSB.append(numFunc + "(" + exprStr + "," + unitStr + ")");
                        colExpNew.add(dateAddInOracleSB.toString());
                        colExp.clear();
                        colExp.addAll(colExpNew);
                    }
                }
            }
            this.setFunctionName(null);
            this.setToDateExpression(null);
            this.setToDateSymbolValue(null);
            this.setOpenBracesForFunctionNameRequired(false);
            arguments.remove(0);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String funName = this.functionName.getColumnName();
        boolean isdate_add = false;
        if (funName != null && funName.trim().equalsIgnoreCase("DATE_ADD")) {
            isdate_add = true;
        }
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (from_sqs != null && from_sqs.isMSAzure()) {
            final Vector azureArgs = new Vector();
            azureArgs.addElement((arguments.get(1).getColumnExpression().size() == 3) ? arguments.get(1).getColumnExpression().get(2).toString() : "DATE");
            if (this.functionName.getColumnName().equalsIgnoreCase("date_sub") || this.functionName.getColumnName().equalsIgnoreCase("subdate")) {
                azureArgs.addElement("-" + arguments.get(1).getColumnExpression().get(1));
            }
            else {
                azureArgs.addElement(arguments.get(1).getColumnExpression().get(1));
            }
            azureArgs.addElement("CAST(" + arguments.get(0).toString() + " AS DATETIME )");
            this.functionName.setColumnName("DATEADD");
            this.setFunctionArguments(azureArgs);
            return;
        }
        this.functionName.setColumnName("DATEADD");
        if (isdate_add) {
            final int size = arguments.size();
            String daySettings = null;
            String intervalString = null;
            if (size == 2) {
                if (arguments.get(1) instanceof SelectColumn) {
                    final SelectColumn selCol = arguments.get(1);
                    final Vector colExp = selCol.getColumnExpression();
                    if (colExp != null) {
                        final Vector colExpNew = new Vector();
                        for (int i = 0; i < colExp.size(); ++i) {
                            if (colExp.get(i) instanceof String) {
                                final String str1 = colExp.get(i);
                                if (str1.trim().equalsIgnoreCase("DAY")) {
                                    daySettings = "dd";
                                }
                                else {
                                    colExpNew.add(colExp.get(i));
                                }
                            }
                            else if (colExp.get(i) instanceof TableColumn) {
                                final TableColumn tabCol = colExp.get(i);
                                final String str2 = tabCol.getColumnName();
                                if (str2 != null && str2.trim().equalsIgnoreCase("INTERVAL")) {
                                    intervalString = str2;
                                }
                                else {
                                    colExpNew.add(colExp.get(i));
                                }
                            }
                            else {
                                colExpNew.add(colExp.get(i));
                            }
                        }
                        colExp.clear();
                        colExp.addAll(colExpNew);
                    }
                }
                if (daySettings != null) {
                    SelectColumn selCol2 = null;
                    if (arguments.get(0) instanceof SelectColumn) {
                        selCol2 = arguments.remove(0);
                    }
                    arguments.add(0, "dd");
                    if (selCol2 != null) {
                        final Vector colExp = selCol2.getColumnExpression();
                        if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof SelectColumn) {
                            final SelectColumn selCol3 = colExp.get(0);
                            final Vector colExp2 = selCol3.getColumnExpression();
                            if (colExp2 != null && colExp2.size() == 1 && colExp2.get(0) instanceof TableColumn) {
                                final TableColumn tabCol = colExp2.get(0);
                                String funName2 = tabCol.getColumnName();
                                if (funName2 != null && funName2.trim().equalsIgnoreCase("[CURRENT_TIMESTAMP]")) {
                                    funName2 = funName2.substring(1, funName2.length() - 1);
                                    tabCol.setColumnName(funName2);
                                }
                            }
                        }
                        arguments.add(selCol2);
                    }
                }
            }
        }
        if (FunctionCalls.charToIntName && this.functionArguments.size() == 3) {
            final Object obj = this.functionArguments.get(0);
            String arg1 = obj.toString().trim();
            if (arg1.equalsIgnoreCase("HH24")) {
                arg1 = "HH";
                arguments.setElementAt(arg1, 0);
            }
            else if (arg1.startsWith("'") && arg1.endsWith("'")) {
                if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("HH24")) {
                    arg1 = "HH";
                    arguments.setElementAt(arg1, 0);
                }
                else if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("MI")) {
                    arg1 = "MI";
                    arguments.setElementAt(arg1, 0);
                }
                else if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("SS")) {
                    arg1 = "SS";
                    arguments.setElementAt(arg1, 0);
                }
            }
        }
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
        if (FunctionCalls.charToIntName && this.functionArguments.size() == 3) {
            final Object obj = this.functionArguments.get(0);
            String arg1 = obj.toString().trim();
            if (arg1.equalsIgnoreCase("HH24")) {
                arg1 = "HH";
                arguments.setElementAt(arg1, 0);
            }
            else if (arg1.startsWith("'") && arg1.endsWith("'")) {
                if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("HH24")) {
                    arg1 = "HH";
                    arguments.setElementAt(arg1, 0);
                }
                else if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("MI")) {
                    arg1 = "MI";
                    arguments.setElementAt(arg1, 0);
                }
                else if (arg1.substring(1, arg1.length() - 1).equalsIgnoreCase("SS")) {
                    arg1 = "SS";
                    arguments.setElementAt(arg1, 0);
                }
            }
        }
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
                    final Vector colExp = ((SelectColumn)arg2).getColumnExpression();
                    if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                        colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
                    }
                    colExp.addElement("+");
                    colExp.addElement(this.functionArguments.get(1).toString() + " MONTHS");
                    this.functionArguments.setElementAt(arg2, 0);
                    this.functionArguments.setSize(1);
                }
                else if (arg2 instanceof String) {
                    this.functionArguments.setElementAt(arg2 + " + " + this.functionArguments.get(1).toString(), 0);
                    this.functionArguments.setSize(1);
                }
            }
            else if (arg1.equalsIgnoreCase("day") || arg1.equalsIgnoreCase("dd") || arg1.equalsIgnoreCase("d")) {
                this.functionName.setColumnName("");
                final Object arg2 = this.functionArguments.get(2);
                if (arg2 instanceof SelectColumn) {
                    final Vector colExp = ((SelectColumn)arg2).getColumnExpression();
                    if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                        colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
                    }
                    colExp.addElement("+");
                    colExp.addElement("( " + this.functionArguments.get(1).toString() + ")" + " DAYS");
                    this.functionArguments.setElementAt(arg2, 0);
                    this.functionArguments.setSize(1);
                }
                else if (arg2 instanceof String) {
                    this.functionArguments.setElementAt(arg2 + " + " + this.functionArguments.get(1).toString(), 0);
                    this.functionArguments.setSize(1);
                }
            }
            else if (arg1.equalsIgnoreCase("week") || arg1.equalsIgnoreCase("wk") || arg1.equalsIgnoreCase("ww")) {
                this.functionName.setColumnName("");
                final Object arg2 = this.functionArguments.get(2);
                if (arg2 instanceof SelectColumn) {
                    final Vector colExp = ((SelectColumn)arg2).getColumnExpression();
                    if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                        colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
                    }
                    colExp.addElement("+");
                    colExp.addElement("(" + this.functionArguments.get(1).toString() + " * 7) DAYS");
                    this.functionArguments.setElementAt(arg2, 0);
                    this.functionArguments.setSize(1);
                }
                else if (arg2 instanceof String) {
                    this.functionArguments.setElementAt(arg2 + " + " + this.functionArguments.get(1).toString() + "*7", 0);
                    this.functionArguments.setSize(1);
                }
            }
            else if (arg1.equalsIgnoreCase("year") || arg1.equalsIgnoreCase("yy")) {
                this.dateaddToDB2("YEARS");
            }
            else if (arg1.equalsIgnoreCase("quarter") || arg1.equalsIgnoreCase("qq")) {
                this.functionName.setColumnName("");
                final Object arg2 = this.functionArguments.get(2);
                if (arg2 instanceof SelectColumn) {
                    final Vector colExp = ((SelectColumn)arg2).getColumnExpression();
                    if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                        colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
                    }
                    colExp.addElement("+");
                    colExp.addElement("(" + this.functionArguments.get(1).toString() + " * 91) DAYS");
                    this.functionArguments.setElementAt(arg2, 0);
                    this.functionArguments.setSize(1);
                }
            }
            else if (arg1.equalsIgnoreCase("hour") || arg1.equalsIgnoreCase("hh")) {
                this.dateaddToDB2("HOURS");
            }
            else if (arg1.equalsIgnoreCase("minute") || arg1.equalsIgnoreCase("mi")) {
                this.dateaddToDB2("MINUTES");
            }
            else if (arg1.equalsIgnoreCase("second") || arg1.equalsIgnoreCase("ss")) {
                this.dateaddToDB2("SECONDS");
            }
        }
    }
    
    private void dateaddToDB2(final String fnName) {
        this.functionName.setColumnName("");
        final Object arg3 = this.functionArguments.get(2);
        if (arg3 instanceof SelectColumn) {
            final Vector colExp = ((SelectColumn)arg3).getColumnExpression();
            if (colExp.size() == 1 && colExp.get(0).toString().startsWith("'")) {
                colExp.setElementAt("DATE(" + colExp.get(0).toString() + ")", 0);
            }
            colExp.addElement("+");
            colExp.addElement(this.functionArguments.get(1).toString() + " " + fnName);
            this.functionArguments.setElementAt(arg3, 0);
            this.functionArguments.setSize(1);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        boolean isDateSub = false;
        boolean isDateAdd = false;
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        if (this.functionName.getColumnName().trim().equalsIgnoreCase("date_sub") || this.functionName.getColumnName().trim().equalsIgnoreCase("subdate")) {
            isDateSub = true;
        }
        if (this.functionName.getColumnName().trim().equalsIgnoreCase("date_add") || this.functionName.getColumnName().trim().equalsIgnoreCase("adddate")) {
            isDateAdd = true;
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0 && this.functionArguments.elementAt(i_count).getColumnExpression().size() == 1 && this.functionArguments.elementAt(i_count).getColumnExpression().get(0) instanceof String) {
                    String dateString = this.functionArguments.elementAt(i_count).getColumnExpression().get(0).toString();
                    dateString = this.handleStringLiteralForDateTime(dateString, from_sqs);
                    dateString = "CAST(" + dateString + " AS TIMESTAMP)";
                    this.functionArguments.elementAt(i_count).getColumnExpression().set(0, dateString);
                }
                if (this.functionArguments.size() == 2 && i_count == 1 && this.functionArguments.elementAt(i_count).getColumnExpression().size() == 3) {
                    if (this.functionArguments.elementAt(i_count).getColumnExpression().get(1) instanceof SelectColumn) {
                        final SelectColumn sc = this.functionArguments.elementAt(i_count).getColumnExpression().get(1);
                        if (sc.getOpenBrace() == null) {
                            sc.setOpenBrace("(");
                            sc.setCloseBrace(")");
                        }
                    }
                    try {
                        if (this.functionArguments.elementAt(i_count).getColumnExpression().get(0) instanceof TableColumn && this.functionArguments.elementAt(i_count).getColumnExpression().get(0).getColumnName().equalsIgnoreCase("interval")) {
                            Object interval = this.functionArguments.elementAt(i_count).getColumnExpression().get(1);
                            if ((interval instanceof SelectColumn && ((SelectColumn)interval).getColumnExpression() != null && ((SelectColumn)interval).getColumnExpression().size() == 1 && ((SelectColumn)interval).getColumnExpression().get(0) instanceof FunctionCalls && ((SelectColumn)interval).getColumnExpression().get(0).getFunctionNameAsAString() != null && ((SelectColumn)interval).getColumnExpression().get(0).getFunctionNameAsAString().equalsIgnoreCase("ROUND")) || !(interval instanceof FunctionCalls) || ((FunctionCalls)interval).getFunctionName() == null || !((FunctionCalls)interval).getFunctionName().toString().equalsIgnoreCase("ROUND")) {
                                final FunctionCalls fc = new FunctionCalls();
                                final TableColumn tc = new TableColumn();
                                tc.setColumnName("round");
                                fc.setFunctionName(tc);
                                final Vector v = new Vector();
                                if (interval instanceof String && interval.toString().startsWith("'") && interval.toString().endsWith("'")) {
                                    final String intervalNum = interval.toString().replaceAll("'", "");
                                    try {
                                        Double.parseDouble(intervalNum);
                                        interval = intervalNum;
                                    }
                                    catch (final Exception ex) {}
                                }
                                else if (interval instanceof SelectColumn && ((SelectColumn)interval).getColumnExpression().get(0) instanceof String) {
                                    final String intervalNum = ((SelectColumn)interval).getColumnExpression().get(0).toString().replaceAll("'", "");
                                    try {
                                        Double.parseDouble(intervalNum);
                                        interval = intervalNum;
                                    }
                                    catch (final Exception ex2) {}
                                }
                                v.addElement(interval);
                                final SelectColumn colExp = new SelectColumn();
                                colExp.setColumnExpression(v);
                                final Vector fnV = new Vector();
                                fnV.add(colExp);
                                fc.setFunctionArguments(fnV);
                                this.functionArguments.elementAt(i_count).getColumnExpression().remove(1);
                                this.functionArguments.elementAt(i_count).getColumnExpression().add(1, fc);
                            }
                        }
                    }
                    catch (final Exception ex3) {}
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if ((this.functionArguments.size() != 2 || !this.functionName.getColumnName().equalsIgnoreCase("DATE_ADD")) && !this.functionName.getColumnName().equalsIgnoreCase("DATE_SUB") && !this.functionName.getColumnName().equalsIgnoreCase("subdate") && !this.functionName.getColumnName().equalsIgnoreCase("adddate")) {
            this.functionName.setColumnName("DATE_PLI");
            this.setFunctionArguments(arguments);
            if (this.functionArguments.size() == 3) {
                final Object obj = this.functionArguments.get(0);
                final String arg1 = obj.toString().trim();
                String temparg1 = null;
                if (arg1.equalsIgnoreCase("day") || arg1.equalsIgnoreCase("dd") || arg1.equalsIgnoreCase("d")) {
                    temparg1 = "DAY";
                }
                else if (arg1.equalsIgnoreCase("month") || arg1.equalsIgnoreCase("mm") || arg1.equalsIgnoreCase("m")) {
                    temparg1 = "MONTHS";
                }
                if (temparg1 != null) {
                    this.functionName.setColumnName(null);
                    final SelectColumn arg2 = new SelectColumn();
                    final Vector colExp2 = new Vector();
                    colExp2.addElement("DATE");
                    colExp2.addElement(this.functionArguments.get(2));
                    colExp2.addElement("+");
                    colExp2.addElement("INTERVAL '");
                    colExp2.addElement(this.functionArguments.get(1));
                    colExp2.addElement(temparg1);
                    colExp2.addElement("'");
                    arg2.setColumnExpression(colExp2);
                    this.functionArguments.setElementAt(arg2, 0);
                    this.functionArguments.setSize(1);
                }
                else {
                    if (temparg1 == null && !SwisSQLOptions.passFunctionsWithOutThrowingConvertException) {
                        throw new ConvertException("DATEADD function is supported only for 'DAY' and 'MONTH' in PostgreSQL");
                    }
                    if (isDateSub) {
                        this.functionName.setColumnName("DATE_SUB");
                    }
                    else if (isDateAdd) {
                        this.functionName.setColumnName("DATE_ADD");
                    }
                }
            }
            return;
        }
        final String arg3 = arguments.get(1).toString();
        final String argLowerCase = arg3.toLowerCase().trim();
        String operation = "+";
        if (isDateSub) {
            operation = "-";
        }
        if (argLowerCase.startsWith("interval  '1'  year") || argLowerCase.startsWith("interval  '1'  month") || argLowerCase.startsWith("interval  '1'  day") || argLowerCase.startsWith("interval  '1'  hour") || argLowerCase.startsWith("interval  '1'  minute") || argLowerCase.startsWith("interval  '1'  second")) {
            String qry = "(" + arguments.get(0) + " " + operation + " ( " + arguments.get(1) + ") )";
            if (canUseUDFFunction) {
                if (isDateAdd) {
                    qry = "DATE_ADD(" + arguments.get(0) + ", " + arguments.get(1) + ")";
                }
                else {
                    qry = "DATE_SUB(" + arguments.get(0) + ", " + arguments.get(1) + ")";
                }
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
        }
        String qry = "(" + arguments.get(0) + " " + operation + "interval '1' day * (" + arguments.get(1) + "))";
        if (canUseUDFFunction) {
            if (isDateAdd) {
                qry = "DATE_ADD(" + arguments.get(0) + ", " + "INTERVAL '1' DAY * ROUND(" + arguments.get(1) + "))";
            }
            else {
                qry = "DATE_SUB(" + arguments.get(0) + ", " + "INTERVAL '1' DAY * ROUND(" + arguments.get(1) + "))";
            }
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String funName = this.functionName.getColumnName();
        this.functionName.setColumnName("DATE_ADD");
        if (funName != null && funName.trim().equalsIgnoreCase("DATE_SUB")) {
            this.functionName.setColumnName("DATE_SUB");
        }
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
            final Object obj = this.functionArguments.get(0);
            final String arg1 = obj.toString().trim();
            this.functionArguments.setElementAt(this.functionArguments.get(2), 0);
            final SelectColumn arg2 = new SelectColumn();
            final Vector colExp = new Vector();
            colExp.addElement("INTERVAL ");
            colExp.addElement(this.functionArguments.get(1).toString().trim());
            if (arg1.equalsIgnoreCase("month") || arg1.equalsIgnoreCase("mm") || arg1.equalsIgnoreCase("m")) {
                colExp.addElement(" MONTHS");
            }
            else if (arg1.equalsIgnoreCase("day") || arg1.equalsIgnoreCase("dd") || arg1.equalsIgnoreCase("d")) {
                colExp.addElement(" DAY");
            }
            else if (arg1.equalsIgnoreCase("week") || arg1.equalsIgnoreCase("wk") || arg1.equalsIgnoreCase("ww")) {
                colExp.addElement("*7 DAYS");
            }
            arg2.setColumnExpression(colExp);
            this.functionArguments.setElementAt(arg2, 1);
            this.functionArguments.setSize(2);
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
        final Vector newArguments = new Vector();
        if (this.functionArguments.size() > 2 && this.functionArguments.elementAt(0) instanceof SelectColumn) {
            final String datePart = this.functionArguments.elementAt(0).toString();
            if (datePart.trim().equalsIgnoreCase("DAY")) {
                if (this.functionArguments.elementAt(1) instanceof SelectColumn && this.functionArguments.elementAt(2) instanceof SelectColumn) {
                    newArguments.add(this.functionArguments.elementAt(2).toString() + " + " + this.functionArguments.elementAt(1).toString() + " UNITS DAY");
                }
            }
            else if (datePart.trim().equalsIgnoreCase("MONTH")) {
                if (this.functionArguments.elementAt(1) instanceof SelectColumn && this.functionArguments.elementAt(2) instanceof SelectColumn) {
                    newArguments.add(this.functionArguments.elementAt(2).toString() + " + " + this.functionArguments.elementAt(1).toString() + " UNITS MONTH");
                }
            }
            else if (datePart.trim().equalsIgnoreCase("YEAR")) {
                if (this.functionArguments.elementAt(1) instanceof SelectColumn && this.functionArguments.elementAt(2) instanceof SelectColumn) {
                    newArguments.add(this.functionArguments.elementAt(2).toString() + " + " + this.functionArguments.elementAt(1).toString() + " UNITS YEAR");
                }
            }
            else if (datePart.trim().equalsIgnoreCase("HOUR")) {
                if (this.functionArguments.elementAt(1) instanceof SelectColumn && this.functionArguments.elementAt(2) instanceof SelectColumn) {
                    newArguments.add(this.functionArguments.elementAt(2).toString() + " + " + this.functionArguments.elementAt(1).toString() + " UNITS HOUR");
                }
            }
            else if (datePart.trim().equalsIgnoreCase("MINUTE")) {
                if (this.functionArguments.elementAt(1) instanceof SelectColumn && this.functionArguments.elementAt(2) instanceof SelectColumn) {
                    newArguments.add(this.functionArguments.elementAt(2).toString() + " + " + this.functionArguments.elementAt(1).toString() + " UNITS MINUTE");
                }
            }
            else if (datePart.trim().equalsIgnoreCase("SECOND")) {
                if (this.functionArguments.elementAt(1) instanceof SelectColumn && this.functionArguments.elementAt(2) instanceof SelectColumn) {
                    newArguments.add(this.functionArguments.elementAt(2).toString() + " + " + this.functionArguments.elementAt(1).toString() + " UNITS SECOND");
                }
            }
            else if (datePart.trim().equalsIgnoreCase("MILLISECOND") && this.functionArguments.elementAt(1) instanceof SelectColumn && this.functionArguments.elementAt(2) instanceof SelectColumn) {
                newArguments.add(this.functionArguments.elementAt(2).toString() + " + " + this.functionArguments.elementAt(1).toString() + " * 0.OO1 UNITS SECOND");
            }
            this.setFunctionArguments(newArguments);
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe function DATEADD is not supported in TimesTen 5.1.21\n");
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
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String funName = this.functionName.getColumnName();
        String functionOperation = "+";
        if (funName != null && funName.trim().equalsIgnoreCase("DATE_SUB")) {
            functionOperation = "-";
        }
        final StringBuffer arguments = new StringBuffer();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (i_count == 1) {
                arguments.append(") " + functionOperation + " ");
            }
            else if (i_count == 0) {
                arguments.append("timestamp(");
            }
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    this.handleStringLiteralForDateTime(from_sqs, i_count, false);
                }
                arguments.append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.append(this.functionArguments.elementAt(i_count));
            }
        }
        this.functionName.setColumnName("(" + (Object)arguments + ")");
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
