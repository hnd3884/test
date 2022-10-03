package com.adventnet.swissqlapi.sql.functions.date;

import java.util.HashMap;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Arrays;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class businessDate extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName().toUpperCase();
        if (fnStr.equalsIgnoreCase("BUSINESS_DAYS")) {
            this.functionName.setColumnName("ZR_BUSINESS_DAYS");
            final Vector arguments = new Vector();
            final Vector vector = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    vector.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    vector.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            String weekendPattern = null;
            int weekdays = 0;
            String remainingDays = null;
            if (this.functionArguments.size() >= 3 && vector.elementAt(2) instanceof SelectColumn) {
                final SelectColumn sc_weekend = vector.elementAt(2);
                final Vector vc_weekend = sc_weekend.getColumnExpression();
                if (!(vc_weekend.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr, "EXCLUDE_WEEKENDS" });
                }
                weekendPattern = vc_weekend.elementAt(0);
                weekendPattern = weekendPattern.replaceAll("'", "");
                if (weekendPattern.length() < 7 || weekendPattern.contains(",")) {
                    this.validateExcludeWeekendAsCharArray(weekendPattern, fnStr);
                    weekendPattern = weekendPattern.replaceAll(",", "");
                    final char[] tobesorted = weekendPattern.toCharArray();
                    Arrays.sort(tobesorted);
                    weekendPattern = new String(tobesorted);
                }
                else {
                    if (weekendPattern.length() != 7) {
                        throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separeted by comma" });
                    }
                    final StringBuilder sb = new StringBuilder();
                    this.validateExcludeWeekendAsString(weekendPattern, fnStr);
                    for (int i = 0; i < 7; ++i) {
                        if (weekendPattern.charAt(i) == '1') {
                            final int a = i + 1;
                            sb.append(a);
                        }
                    }
                    weekendPattern = sb.toString();
                }
            }
            if (weekendPattern != null) {
                weekdays = 7 - weekendPattern.length();
                remainingDays = this.getBusiness_DaysFnRemainingDaysMatrixMap(weekendPattern);
                if (remainingDays == null) {
                    remainingDays = this.getRemainingDaysForBusiness_DaysFnAndBusiness_HoursFn(weekendPattern);
                }
            }
            else {
                remainingDays = "'0123455401234434012332340122123401101234000123450'";
                weekdays = 5;
            }
            final String str_weekEndCount = Integer.toString(weekdays);
            arguments.addElement(vector.get(0));
            arguments.addElement(vector.get(1));
            arguments.addElement(str_weekEndCount);
            arguments.addElement(remainingDays);
            this.setFunctionArguments(arguments);
        }
        else if (fnStr.equalsIgnoreCase("BUSINESS_HOURS")) {
            this.functionName.setColumnName("ZR_BUSINESS_HOURS");
            final Vector arguments = new Vector();
            final Vector vector2 = new Vector();
            final Vector vector3 = new Vector();
            final Vector vector4 = new Vector();
            for (int i_count2 = 0; i_count2 < this.functionArguments.size(); ++i_count2) {
                if (this.functionArguments.elementAt(i_count2) instanceof SelectColumn) {
                    vector2.addElement(this.functionArguments.elementAt(i_count2).toMySQLSelect(to_sqs, from_sqs));
                    vector3.addElement(this.functionArguments.elementAt(i_count2).toMySQLSelect(to_sqs, from_sqs));
                    vector4.addElement(this.functionArguments.elementAt(i_count2).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    vector2.addElement(this.functionArguments.elementAt(i_count2));
                    vector3.addElement(this.functionArguments.elementAt(i_count2));
                    vector4.addElement(this.functionArguments.elementAt(i_count2));
                }
            }
            final StringBuilder notinWeekEndIndex = new StringBuilder();
            String weekendPattern2 = null;
            String remainingDays2 = null;
            int weekdays2 = 0;
            if (this.functionArguments.size() > 4 && vector2.elementAt(4) instanceof SelectColumn) {
                final SelectColumn sc_weekend2 = vector2.elementAt(4);
                final Vector vc_weekend2 = sc_weekend2.getColumnExpression();
                if (!(vc_weekend2.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr, "EXCLUDE_WEEKENDS" });
                }
                weekendPattern2 = vc_weekend2.elementAt(0);
                weekendPattern2 = weekendPattern2.replaceAll("'", "");
                if (weekendPattern2.length() < 7 || weekendPattern2.contains(",")) {
                    this.validateExcludeWeekendAsCharArray(weekendPattern2, fnStr);
                    weekendPattern2 = weekendPattern2.replaceAll(",", "");
                    final char[] tobesorted2 = weekendPattern2.toCharArray();
                    Arrays.sort(tobesorted2);
                    weekendPattern2 = new String(tobesorted2);
                }
                else {
                    if (weekendPattern2.length() != 7) {
                        throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separeted by comma" });
                    }
                    this.validateExcludeWeekendAsString(weekendPattern2, fnStr);
                    final StringBuilder sb2 = new StringBuilder();
                    for (int i = 0; i < 7; ++i) {
                        if (weekendPattern2.charAt(i) == '1') {
                            final int a2 = i + 1;
                            sb2.append(a2);
                        }
                    }
                    weekendPattern2 = sb2.toString();
                }
            }
            if (weekendPattern2 != null) {
                weekdays2 = 7 - weekendPattern2.length();
                remainingDays2 = this.getBusiness_DaysFnRemainingDaysMatrixMap(weekendPattern2);
                if (remainingDays2 == null) {
                    remainingDays2 = this.getRemainingDaysForBusiness_DaysFnAndBusiness_HoursFn(weekendPattern2);
                }
                notinWeekEndIndex.append("'");
                final int[] weekend = new int[7];
                for (int i = 0; i < weekendPattern2.length(); ++i) {
                    if (weekendPattern2.charAt(i) == '1') {
                        weekend[6] = 1;
                    }
                    else {
                        weekend[weekendPattern2.charAt(i) - '0' - 2] = 1;
                    }
                }
                for (int i = 0; i < 7; ++i) {
                    notinWeekEndIndex.append(weekend[i]);
                }
                notinWeekEndIndex.append("'");
            }
            else {
                remainingDays2 = "'0123455401234434012332340122123401101234000123450'";
                weekdays2 = 5;
                notinWeekEndIndex.append("'0000011'");
            }
            final String str_weekDaysCount = Integer.toString(weekdays2);
            final String str_weekEndIndex = notinWeekEndIndex.toString();
            String wst = new String();
            int workStartTime = 0;
            if (vector2.elementAt(2) instanceof SelectColumn) {
                final SelectColumn sc_wst = vector2.elementAt(2);
                final Vector vc_wst = sc_wst.getColumnExpression();
                if (!(vc_wst.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr, "WORK_START_TIME" });
                }
                wst = vc_wst.elementAt(0);
                wst = wst.replaceAll("'", "");
                wst = wst.trim();
                this.validateWorkStartAndEndTime(wst, "WORK_START_TIME", fnStr, 0);
                final String[] array = wst.split(":");
                final int[] a3 = new int[3];
                for (int i = 0; i < 3; ++i) {
                    a3[i] = Integer.parseInt(array[i]);
                }
                workStartTime += a3[0] * 3600 + a3[1] * 60 + a3[2];
            }
            String wet = new String();
            int workEndTime = 0;
            if (vector2.elementAt(3) instanceof SelectColumn) {
                final SelectColumn sc_wet = vector2.elementAt(3);
                final Vector vc_wet = sc_wet.getColumnExpression();
                if (!(vc_wet.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr, "WORK_END_TIME" });
                }
                wet = vc_wet.elementAt(0);
                wet = wet.replaceAll("'", "");
                wet = wet.trim();
                this.validateWorkStartAndEndTime(wet, "WORK_END_TIME", fnStr, workStartTime);
                final String[] array2 = wet.split(":");
                final int[] b = new int[3];
                for (int i = 0; i < 3; ++i) {
                    b[i] = Integer.parseInt(array2[i]);
                }
                workEndTime += b[0] * 3600 + b[1] * 60 + b[2];
            }
            final int workTime = workEndTime - workStartTime;
            final String str_wst = Integer.toString(workStartTime);
            final String str_wet = Integer.toString(workEndTime);
            final String str_wt = Integer.toString(workTime);
            arguments.addElement(this.date(vector2, 0));
            arguments.addElement(this.date(vector2, 1));
            arguments.addElement(this.time_to_sec(vector3, 0));
            arguments.addElement(this.time_to_sec(vector3, 1));
            arguments.addElement(this.weekday(vector4, 0));
            arguments.addElement(this.weekday(vector4, 1));
            arguments.addElement(str_wst);
            arguments.addElement(str_wet);
            arguments.addElement(str_wt);
            arguments.addElement(str_weekDaysCount);
            arguments.addElement(remainingDays2);
            arguments.addElement(str_weekEndIndex);
            this.setFunctionArguments(arguments);
        }
        else if (fnStr.equalsIgnoreCase("BUSINESS_COMPLETION_DAY")) {
            this.functionName.setColumnName("ZR_BUSINESS_ENDDAY");
            final Vector arguments = new Vector();
            final Vector vector2 = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    vector2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    vector2.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            String remainingDays3 = new String();
            String weekend_input = null;
            String str_weekdays_len = "";
            String str_weekdays_lentwice = "";
            int weekend_len = 0;
            int weekdays_lentwice = 0;
            int weekdays_len = 0;
            if (this.functionArguments.size() > 2 && vector2.elementAt(2) instanceof SelectColumn) {
                final SelectColumn sc = vector2.elementAt(2);
                final Vector vc = sc.getColumnExpression();
                if (!(vc.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function" + this.functionName.getColumnName(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr, "EXCLUDE_WEEKENDS" });
                }
                weekend_input = vc.elementAt(0);
                weekend_input = weekend_input.replaceAll("'", "");
                if (weekend_input.length() < 7 || weekend_input.contains(",")) {
                    this.validateExcludeWeekendAsCharArray(weekend_input, fnStr);
                    weekend_input = weekend_input.replaceAll(",", "");
                    final char[] tobesorted2 = weekend_input.toCharArray();
                    Arrays.sort(tobesorted2);
                    weekend_input = new String(tobesorted2);
                }
                else {
                    if (weekend_input.length() != 7) {
                        throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separeted by comma" });
                    }
                    final StringBuilder sb2 = new StringBuilder();
                    this.validateExcludeWeekendAsString(weekend_input, fnStr);
                    for (int j = 0; j < 7; ++j) {
                        if (weekend_input.charAt(j) == '1') {
                            final int a4 = j + 1;
                            sb2.append(a4);
                        }
                    }
                    weekend_input = sb2.toString();
                }
                weekend_len = weekend_input.length();
                weekdays_len = 7 - weekend_len;
                weekdays_lentwice = weekdays_len * 2;
            }
            if (weekend_input != null) {
                remainingDays3 = this.getBusinessEndDateFnRemainingDaysForWeekendPatterns(weekend_input);
                if (remainingDays3 == null) {
                    final char[][] arr = new char[7][weekdays_lentwice];
                    int b2 = 0;
                    final int[] weekend2 = new int[weekend_len];
                    final int[] wwkk = new int[weekend_len];
                    for (int k = 0; k < weekend_len; ++k) {
                        if (weekend_input.charAt(k) == '1') {
                            wwkk[k] = (weekend2[k] = 6);
                        }
                        else {
                            weekend2[k] = weekend_input.charAt(k) - '0' - 2;
                            wwkk[k] = weekend_input.charAt(k) - '0' - 2;
                        }
                    }
                    for (int k = 0; k < 7; ++k) {
                        final int l = k;
                        for (int index = 0; index < weekend_len; ++index) {
                            if (l > weekend2[index]) {
                                final int[] array3 = weekend2;
                                final int n = index;
                                array3[n] += 7;
                            }
                            Arrays.sort(weekend2);
                        }
                        for (int m = 0; m < weekdays_lentwice; ++m) {
                            if (m == 0) {
                                for (int index = 0; index < weekend_len; ++index) {
                                    if (l == weekend2[index]) {
                                        arr[k][m] = '-';
                                        break;
                                    }
                                    arr[k][m] = '0';
                                }
                            }
                            else if (m == 1) {
                                b2 = 0;
                                int l2 = k;
                                for (int index = weekend_len - 1; index >= 0; --index) {
                                    if (l2 == wwkk[index]) {
                                        ++b2;
                                        --l2;
                                    }
                                    if (l2 == -1) {
                                        l2 = 6;
                                    }
                                }
                                if (b2 == 0) {
                                    arr[k][m] = '0';
                                }
                                else {
                                    arr[k][m] = (char)(b2 + 48);
                                }
                            }
                            else if (m % 2 == 0) {
                                arr[k][m] = '0';
                            }
                            else {
                                int x = 0;
                                for (int index = 0; index < weekend_len; ++index) {
                                    if (l < weekend2[index] && m / 2 + l + x >= weekend2[index]) {
                                        ++x;
                                    }
                                }
                                final int A = m / 2 + x;
                                arr[k][m] = (char)(A + 48);
                            }
                        }
                    }
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("'");
                    for (int k = 0; k < 7; ++k) {
                        for (int m = 0; m < weekdays_lentwice; ++m) {
                            sb3.append(arr[k][m]);
                        }
                    }
                    sb3.append("'");
                    remainingDays3 = sb3.toString();
                }
            }
            else {
                remainingDays3 = "'00010203040001020306000102050600010405060003040506-102030405-201020304'";
                weekdays_len = 5;
                weekdays_lentwice = 10;
            }
            str_weekdays_len = Integer.toString(weekdays_len);
            str_weekdays_lentwice = Integer.toString(weekdays_lentwice);
            final SelectColumn sc_Date = new SelectColumn();
            final FunctionCalls fn_Date = new FunctionCalls();
            final TableColumn tb_Date = new TableColumn();
            tb_Date.setColumnName("DATE");
            fn_Date.setFunctionName(tb_Date);
            final Vector vc_DateIn = new Vector();
            final Vector vc_DateOut = new Vector();
            vc_DateIn.addElement(vector2.get(0));
            fn_Date.setFunctionArguments(vc_DateIn);
            vc_DateOut.addElement(fn_Date);
            sc_Date.setColumnExpression(vc_DateOut);
            arguments.addElement(sc_Date);
            arguments.addElement(vector2.get(1));
            arguments.addElement(str_weekdays_len);
            arguments.addElement(str_weekdays_lentwice);
            arguments.addElement(remainingDays3);
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
        final Vector arguments = new Vector();
        String qry = "";
        final boolean isRedshift = from_sqs != null && from_sqs.isAmazonRedShift();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0 || (i_count == 1 && (fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_DAYS") || fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_HOURS")))) {
                    this.handleStringLiteralForDate(from_sqs, i_count, !isRedshift);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (isRedshift) {
            if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
                final String start_date = arguments.get(0).toString();
                final String end_date = arguments.get(1).toString();
                final String weekdays_count = arguments.get(2).toString();
                final String remaining_days = arguments.get(3).toString();
                qry = "((" + weekdays_count + " * (DATE_MI(DATE(" + end_date + "), DATE(" + start_date + ")) / 7)) + cast(substring((" + remaining_days + ")::text,((7 *  mod(cast(date_part('dow' ," + start_date + ") as int) +6, 7)) + ( mod(cast(date_part('dow' ," + end_date + ") as int) +6, 7) + 1)),1) as BIGINT))";
            }
            else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
                final String start_date = arguments.get(0).toString();
                final String end_date = arguments.get(1).toString();
                final String secof_startdate = arguments.get(2).toString();
                final String secof_enddate = arguments.get(3).toString();
                final String weekdayof_startdate = arguments.get(4).toString();
                final String weekdayof_enddate = arguments.get(5).toString();
                final String work_starttime = arguments.get(6).toString();
                final String work_endtime = arguments.get(7).toString();
                final String work_time = arguments.get(8).toString();
                final String weekdays_count2 = arguments.get(9).toString();
                final String remaining_days2 = arguments.get(10).toString();
                final String weekend_string = arguments.get(11).toString();
                qry = "(CASE WHEN DATE(" + start_date + ")  = DATE(" + end_date + ") THEN (LEAST(" + work_endtime + ", " + secof_enddate + ") -GREATEST(" + work_starttime + ", " + secof_startdate + ")) / 3600 ELSE ((CASE WHEN substring((" + weekend_string + ")::text,(" + weekdayof_startdate + " + 1),1)  = '1' THEN 0 ELSE ((CASE WHEN " + secof_startdate + "  < " + work_starttime + " THEN " + work_time + " WHEN " + secof_startdate + "  > " + work_endtime + " THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ")  END)) END) + (((" + weekdays_count2 + ") * (DATE_MI(DATE(" + end_date + "), DATE(" + start_date + ")) / 7)) + cast(substring((" + remaining_days2 + ")::text,((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)),1) as BIGINT) -(CASE WHEN substring((" + weekend_string + ")::text,(" + weekdayof_startdate + " + 1),1)  = '1' THEN 0 ELSE 1 END)) * " + work_time + " + (CASE WHEN substring((" + weekend_string + ")::text,(" + weekdayof_enddate + " + 1),1)  = '1' THEN 0 ELSE ((CASE WHEN " + secof_enddate + "  < " + work_starttime + " THEN 0 WHEN " + secof_enddate + "  > " + work_endtime + " THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ")  END)) END)) / 3600 END)";
            }
            else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
                final String start_date = arguments.get(0).toString();
                final String days = arguments.get(1).toString();
                final String weekdays_count = arguments.get(2).toString();
                final String weekdays_counttwice = arguments.get(3).toString();
                final String remaining_days3 = arguments.get(4).toString();
                qry = "(CASE WHEN " + days + "  = 0 THEN " + start_date + " ELSE (" + start_date + " + ( INTERVAL  '1'  DAY * ROUND(((((" + days + ") / (" + weekdays_count + ")) * 7) + cast(substring((" + remaining_days3 + ")::text,(((" + weekdays_counttwice + ") *  mod(cast(date_part('dow' ," + start_date + ") as int) +6, 7)) + (((" + days + ") % (" + weekdays_count + ")) * 2) + 1),2) as BIGINT)))) ) END)";
            }
            this.functionName.setColumnName(qry);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
        final Vector arguments = new Vector();
        String qry = "";
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0 || (i_count == 1 && (fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_DAYS") || fnStr.trim().equalsIgnoreCase("ZR_BUSINESS_HOURS")))) {
                    this.handleStringLiteralForDate(from_sqs, i_count, false);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
            final String start_date = arguments.get(0).toString();
            final String end_date = arguments.get(1).toString();
            final String weekdays_count = arguments.get(2).toString();
            final String remaining_days = arguments.get(3).toString();
            qry = "CAST(((" + weekdays_count + " * (day(CAST(" + end_date + " AS DATE) - CAST(" + start_date + " AS DATE)) / 7)) + cast(SUBSTRING(CAST(" + remaining_days + " as VARCHAR), ((7 * mod(int(dayofweek(" + start_date + ")+5),7)) + (mod(int(dayofweek(" + end_date + ")+5),7) + 1)), 1) as BIGINT)) AS BIGINT)";
        }
        else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
            final String start_date = arguments.get(0).toString();
            final String end_date = arguments.get(1).toString();
            final String secof_startdate = arguments.get(2).toString();
            final String secof_enddate = arguments.get(3).toString();
            final String weekdayof_startdate = arguments.get(4).toString();
            final String weekdayof_enddate = arguments.get(5).toString();
            final String work_starttime = arguments.get(6).toString();
            final String work_endtime = arguments.get(7).toString();
            final String work_time = arguments.get(8).toString();
            final String weekdays_count2 = arguments.get(9).toString();
            final String remaining_days2 = arguments.get(10).toString();
            final String weekend_string = arguments.get(11).toString();
            qry = "CAST(if(CAST(" + start_date + " AS DATE)  = CAST(" + end_date + " AS DATE), (LEAST(" + work_endtime + ", " + secof_enddate + ") -GREATEST(" + work_starttime + ", " + secof_startdate + ")) / 3600, (if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_startdate + "+1), 1) = '1', 0, (if(" + secof_startdate + "  < " + work_starttime + ", " + work_time + ", if(" + secof_startdate + "  > " + work_endtime + ", 0, (" + work_endtime + " -" + secof_startdate + "))))) + (((" + weekdays_count2 + ") * (day(CAST(" + end_date + " AS DATE) - CAST(" + start_date + " AS DATE)) / 7)) + cast(SUBSTRING(CAST(" + remaining_days2 + " as VARCHAR), ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) as BIGINT) -if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_startdate + " + 1), 1)  = '1', 0, 1)) * " + work_time + " + if(SUBSTRING(CAST(" + weekend_string + " as VARCHAR), (" + weekdayof_enddate + " + 1), 1)  = '1', 0, (if(" + secof_enddate + "  < " + work_starttime + ", 0, if(" + secof_enddate + "  > " + work_endtime + ", " + work_time + ", (" + secof_enddate + " -" + work_starttime + ")))))) / 3600) AS BIGINT)";
        }
        else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
            final String start_date = arguments.get(0).toString();
            final String days = arguments.get(1).toString();
            final String weekdays_count = arguments.get(2).toString();
            final String weekdays_counttwice = arguments.get(3).toString();
            final String remaining_days3 = arguments.get(4).toString();
            qry = "IF(" + days + "  = 0, " + start_date + ", (timestamp(" + start_date + ") + INTERVAL  '1'  DAY * ((((" + days + ") / (" + weekdays_count + ")) * 7) + cast(SUBSTRING(CAST(" + remaining_days3 + " as VARCHAR), (((" + weekdays_counttwice + ") * mod(int(dayofweek(" + start_date + ")+5),7)) + ((  MOD((" + days + "), (" + weekdays_count + "))  ) * 2) + 1),2) as BIGINT))))";
        }
        this.functionName.setColumnName(qry);
        this.setFunctionArguments(new Vector());
        this.setOpenBracesForFunctionNameRequired(false);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
        final Vector arguments = new Vector();
        String qry = "";
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (fnStr.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
            final String start_date = arguments.get(0).toString();
            final String end_date = arguments.get(1).toString();
            final String weekdays_count = arguments.get(2).toString();
            final String remaining_days = arguments.get(3).toString();
            qry = "((" + weekdays_count + " * (DATEDIFF(dd, " + start_date + ", " + end_date + ") / 7)) + CAST(SUBSTRING(" + remaining_days + ", ((7 * (datepart(dw," + start_date + ")+5)%7) + ((datepart(dw," + end_date + ")+5)%7 + 1)), 1) as signed))";
        }
        else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
            final String start_date = arguments.get(0).toString();
            final String end_date = arguments.get(1).toString();
            final String secof_startdate = arguments.get(2).toString();
            final String secof_enddate = arguments.get(3).toString();
            final String weekdayof_startdate = arguments.get(4).toString();
            final String weekdayof_enddate = arguments.get(5).toString();
            final String work_starttime = arguments.get(6).toString();
            final String work_endtime = arguments.get(7).toString();
            final String work_time = arguments.get(8).toString();
            final String weekdays_count2 = arguments.get(9).toString();
            final String remaining_days2 = arguments.get(10).toString();
            final String weekend_string = arguments.get(11).toString();
            qry = "CASE WHEN CONVERT(DATETIME, " + start_date + ")  = CONVERT(DATETIME, " + end_date + ")  THEN (CAST( CASE WHEN " + work_endtime + " < " + secof_enddate + " THEN " + work_endtime + " ELSE " + secof_enddate + " END AS FLOAT) -CAST( CASE WHEN " + work_starttime + " > " + secof_startdate + " THEN " + work_starttime + " ELSE " + secof_startdate + " END AS FLOAT)) DIV 3600 ELSE (CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_startdate + "  < " + work_starttime + "  THEN " + work_time + " ELSE CASE WHEN " + secof_startdate + "  > " + work_endtime + "  THEN 0 ELSE (" + work_endtime + " -" + secof_startdate + ") END END) END + (((" + weekdays_count2 + ") * (DATEDIFF(dd, " + start_date + ", " + end_date + ") DIV 7)) + CAST(SUBSTRING(" + remaining_days2 + ", ((7 * " + weekdayof_startdate + ") + (" + weekdayof_enddate + " + 1)), 1) AS SIGNED) -CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_startdate + " + 1), 1)  = '1'  THEN 0 ELSE 1 END) * " + work_time + " + CASE WHEN SUBSTRING(" + weekend_string + ", (" + weekdayof_enddate + " + 1), 1)  = '1'  THEN 0 ELSE (CASE WHEN " + secof_enddate + "  < " + work_starttime + "  THEN 0 ELSE CASE WHEN " + secof_enddate + "  > " + work_endtime + "  THEN " + work_time + " ELSE (" + secof_enddate + " -" + work_starttime + ") END END) END) DIV 3600 END";
        }
        else if (fnStr.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
            final String start_date = arguments.get(0).toString();
            final String days = arguments.get(1).toString();
            final String weekdays_count = arguments.get(2).toString();
            final String weekdays_counttwice = arguments.get(3).toString();
            final String remaining_days3 = arguments.get(4).toString();
            qry = "CASE WHEN " + days + "  = 0  THEN " + start_date + " ELSE DATEADD(DAY, ((((" + days + ") / (" + weekdays_count + ")) * 7) + CAST(SUBSTRING(" + remaining_days3 + ", (((" + weekdays_counttwice + ") * (datepart(dw," + start_date + ")+5)%7) + (((" + days + ") % (" + weekdays_count + ")) * 2) + 1), 2) AS SIGNED)), CAST(" + start_date + " AS DATETIME )) END";
        }
        this.functionName.setColumnName(qry);
        this.setFunctionArguments(new Vector());
        this.setOpenBracesForFunctionNameRequired(false);
    }
    
    public String getBusinessEndDateFnRemainingDaysForWeekendPatterns(final String weekendPattern) {
        final HashMap<String, String> remainingDaysMap = new HashMap<String, String>();
        remainingDaysMap.put("17", "'00010203040001020306000102050600010405060003040506-102030405-201020304'");
        remainingDaysMap.put("12", "'-20102030400010203040001020306000102050600010405060003040506-102030405'");
        remainingDaysMap.put("23", "'-102030405-20102030400010203040001020306000102050600010405060003040506'");
        remainingDaysMap.put("34", "'0003040506-102030405-2010203040001020304000102030600010205060001040506'");
        remainingDaysMap.put("45", "'00010405060003040506-102030405-201020304000102030400010203060001020506'");
        remainingDaysMap.put("56", "'000102050600010405060003040506-102030405-20102030400010203040001020306'");
        remainingDaysMap.put("67", "'0001020306000102050600010405060003040506-102030405-2010203040001020304'");
        remainingDaysMap.put("1", "'000102030405000102030406000102030506000102040506000103040506000203040506-10102030405'");
        remainingDaysMap.put("2", "'-10102030405000102030405000102030406000102030506000102040506000103040506000203040506'");
        remainingDaysMap.put("3", "'000203040506-10102030405000102030405000102030406000102030506000102040506000103040506'");
        remainingDaysMap.put("4", "'000103040506000203040506-10102030405000102030405000102030406000102030506000102040506'");
        remainingDaysMap.put("5", "'000102040506000103040506000203040506-10102030405000102030405000102030406000102030506'");
        remainingDaysMap.put("6", "'000102030506000102040506000103040506000203040506-10102030405000102030405000102030406'");
        remainingDaysMap.put("7", "'000102030406000102030506000102040506000103040506000203040506-10102030405000102030405'");
        remainingDaysMap.put("13", "'0002030405-1010203040001020305000102040600010305060002040506-101030405'");
        remainingDaysMap.put("14", "'00010304050002030406-101020305000102040500010304060002030506-101020405'");
        remainingDaysMap.put("15", "'000102040500010304060002030506-10102040500010304050002030406-101020305'");
        remainingDaysMap.put("16", "'0001020305000102040600010305060002040506-1010304050002030405-101020304'");
        remainingDaysMap.put("24", "'-1010304050002030405-1010203040001020305000102040600010305060002040506'");
        remainingDaysMap.put("25", "'-10102040500010304050002030406-101020305000102040500010304060002030506'");
        remainingDaysMap.put("26", "'-101020305000102040500010304060002030506-10102040500010304050002030406'");
        remainingDaysMap.put("27", "'-1010203040001020305000102040600010305060002040506-1010304050002030405'");
        remainingDaysMap.put("35", "'0002040506-1010304050002030405-101020304000102030500010204060001030506'");
        remainingDaysMap.put("36", "'0002030506-10102040500010304050002030406-10102030500010204050001030406'");
        remainingDaysMap.put("37", "'0002030406-101020305000102040500010304060002030506-1010204050001030405'");
        remainingDaysMap.put("46", "'00010305060002040506-1010304050002030405-10102030400010203050001020406'");
        remainingDaysMap.put("47", "'00010304060002030506-10102040500010304050002030406-1010203050001020405'");
        remainingDaysMap.put("57", "'000102040600010305060002040506-1010304050002030405-1010203040001020305'");
        return remainingDaysMap.get(weekendPattern);
    }
    
    public String getBusiness_DaysFnRemainingDaysMatrixMap(final String weekendPattern) {
        final HashMap<String, String> remainingDaysMap = new HashMap<String, String>();
        remainingDaysMap.put("17", "'0123455401234434012332340122123401101234000123450'");
        remainingDaysMap.put("12", "'0012345501234544012343340123223401211234010012340'");
        remainingDaysMap.put("23", "'0001234500123455012344440123333401222234011112340'");
        remainingDaysMap.put("34", "'0111234400012345001234550123344401223334011222340'");
        remainingDaysMap.put("45", "'0122234401112334000123450012345501223444011233340'");
        remainingDaysMap.put("56", "'0123334401222334011122340001234500123455011234440'");
        remainingDaysMap.put("67", "'0123444401233334012222340111123400012345001234550'");
        remainingDaysMap.put("1", "'0123456501234545012343450123234501212345010123450'");
        remainingDaysMap.put("2", "'0012345601234555012344450123334501222345011123450'");
        remainingDaysMap.put("3", "'0112345500123456012344550123344501223345011223450'");
        remainingDaysMap.put("4", "'0122345501123445001234560123345501223445011233450'");
        remainingDaysMap.put("5", "'0123345501223445011233450012345601223455011234450'");
        remainingDaysMap.put("6", "'0123445501233445012233450112234500123456011234550'");
        remainingDaysMap.put("7", "'0123455501234445012333450122234501112345001234560'");
        remainingDaysMap.put("13", "'0112345400123445012343440123233401212234010112340'");
        remainingDaysMap.put("14", "'0122345401123434001233450123234401212334010122340'");
        remainingDaysMap.put("15", "'0123345401223434011232340012234501212344010123340'");
        remainingDaysMap.put("16", "'0123445401233434012232340112123400112345010123440'");
        remainingDaysMap.put("24", "'0011234501123444001234450123334401222334011122340'");
        remainingDaysMap.put("25", "'0012234501223444011233340012334501222344011123340'");
        remainingDaysMap.put("26", "'0012334501233444012233340112223400122345011123440'");
        remainingDaysMap.put("27", "'0012344501234444012333340122223401111234001123450'");
        remainingDaysMap.put("35", "'0112234400112345011233440012344501223344011223340'");
        remainingDaysMap.put("36", "'0112334400122345012233440112233400123345011223440'");
        remainingDaysMap.put("37", "'0112344400123345012333440122233401112234001223450'");
        remainingDaysMap.put("46", "'0122334401122334001123450112234400123445011233440'");
        remainingDaysMap.put("47", "'0122344401123334001223450122234401112334001233450'");
        remainingDaysMap.put("57", "'0123344401223334011222340011234501112344001234450'");
        return remainingDaysMap.get(weekendPattern);
    }
    
    public String getRemainingDaysForBusiness_DaysFnAndBusiness_HoursFn(final String weekendPattern) {
        String remainingDays = null;
        int weekdays = 0;
        weekdays = 7 - weekendPattern.length();
        final StringBuilder sb = new StringBuilder();
        final int[][] arr = new int[7][7];
        final int[] weekend = new int[7];
        sb.append("'");
        for (int i = 0; i < weekendPattern.length(); ++i) {
            if (weekendPattern.charAt(i) == '1') {
                weekend[6] = 1;
            }
            else {
                weekend[weekendPattern.charAt(i) - '0' - 2] = 1;
            }
        }
        for (int i = 0; i < 7; ++i) {
            for (int j = i - 1; j >= 0; --j) {
                if (j == i - 1) {
                    if (weekend[j] == 1) {
                        arr[i][j] = weekdays;
                    }
                    else {
                        arr[i][j] = weekdays - 1;
                    }
                }
                else if (weekend[j] == 1) {
                    arr[i][j] = arr[i][j + 1];
                }
                else {
                    arr[i][j] = arr[i][j + 1] - 1;
                }
            }
            for (int j = i; j < 7; ++j) {
                if (i == j) {
                    arr[i][j] = 0;
                }
                else if (weekend[j - 1] == 1) {
                    arr[i][j] = arr[i][j - 1];
                }
                else {
                    arr[i][j] = arr[i][j - 1] + 1;
                }
            }
            for (int j = 0; j < 7; ++j) {
                sb.append(arr[i][j]);
            }
        }
        sb.append("'");
        remainingDays = sb.toString();
        return remainingDays;
    }
    
    public SelectColumn weekday(final Vector vector, final int sded) {
        final SelectColumn sc_weekday = new SelectColumn();
        final FunctionCalls fn_weekday = new FunctionCalls();
        final TableColumn tb_weekday = new TableColumn();
        tb_weekday.setColumnName("WEEKDAY");
        fn_weekday.setFunctionName(tb_weekday);
        final Vector vc_weekdayIn = new Vector();
        final Vector vc_weekdayOut = new Vector();
        vc_weekdayIn.addElement(vector.get(sded));
        fn_weekday.setFunctionArguments(vc_weekdayIn);
        vc_weekdayOut.addElement(fn_weekday);
        sc_weekday.setColumnExpression(vc_weekdayOut);
        return sc_weekday;
    }
    
    public SelectColumn time_to_sec(final Vector vector, final int sded) {
        final SelectColumn sc_timetosec = new SelectColumn();
        final FunctionCalls fn_timetosec = new FunctionCalls();
        final TableColumn tb_timetosec = new TableColumn();
        tb_timetosec.setColumnName("TIME_TO_SEC");
        fn_timetosec.setFunctionName(tb_timetosec);
        final Vector vc_timetosecIn = new Vector();
        final Vector vc_timetosecOut = new Vector();
        vc_timetosecIn.addElement(vector.get(sded));
        fn_timetosec.setFunctionArguments(vc_timetosecIn);
        vc_timetosecOut.addElement(fn_timetosec);
        sc_timetosec.setColumnExpression(vc_timetosecOut);
        return sc_timetosec;
    }
    
    public SelectColumn date(final Vector vector, final int sded) {
        final SelectColumn sc_date = new SelectColumn();
        final FunctionCalls fn_date = new FunctionCalls();
        final TableColumn tb_date = new TableColumn();
        tb_date.setColumnName("DATE");
        fn_date.setFunctionName(tb_date);
        final Vector vc_dateIn = new Vector();
        final Vector vc_dateOut = new Vector();
        vc_dateIn.addElement(vector.get(sded));
        fn_date.setFunctionArguments(vc_dateIn);
        vc_dateOut.addElement(fn_date);
        sc_date.setColumnExpression(vc_dateOut);
        return sc_date;
    }
}
