package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class previousDate extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (fnStr.equalsIgnoreCase("ISPREVIOUS_NYEAR")) {
            this.functionName.setColumnName("IF");
            final Vector vc_ifStatement = new Vector();
            final SelectColumn sc_wi = new SelectColumn();
            final Vector vc_wi = new Vector();
            final WhereItem wi_if = new WhereItem();
            final WhereColumn if_left = new WhereColumn();
            final Vector vc_if_left = new Vector();
            final WhereColumn if_right = new WhereColumn();
            final Vector vc_if_right = new Vector();
            final SelectColumn sc_DateCol = new SelectColumn();
            final FunctionCalls fnCl_LeftExp = new FunctionCalls();
            final TableColumn tbCl_LeftExp = new TableColumn();
            tbCl_LeftExp.setColumnName("YEAR");
            fnCl_LeftExp.setFunctionName(tbCl_LeftExp);
            final Vector vc_LeftExpIn = new Vector();
            final Vector vc_LeftExpOut = new Vector();
            vc_LeftExpIn.addElement(arguments.get(0));
            fnCl_LeftExp.setFunctionArguments(vc_LeftExpIn);
            vc_LeftExpOut.addElement(fnCl_LeftExp);
            sc_DateCol.setColumnExpression(vc_LeftExpOut);
            vc_if_left.addElement(sc_DateCol);
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator(" BETWEEN ");
            final SelectColumn sc_Limit = new SelectColumn();
            final Vector vc_Limit = new Vector();
            final SelectColumn sc_LimitLow = new SelectColumn();
            final Vector vc_LimitLow = new Vector();
            final SelectColumn sc_StartDate = new SelectColumn();
            final FunctionCalls fnCl_StartDate = new FunctionCalls();
            final TableColumn tbCl_StartDate = new TableColumn();
            tbCl_StartDate.setColumnName("YEAR");
            fnCl_StartDate.setFunctionName(tbCl_StartDate);
            final Vector vc_StartDateIn = new Vector();
            final Vector vc_StartDateOut = new Vector();
            vc_StartDateIn.addElement(this.current_date());
            fnCl_StartDate.setFunctionArguments(vc_StartDateIn);
            vc_StartDateOut.addElement(fnCl_StartDate);
            sc_StartDate.setColumnExpression(vc_StartDateOut);
            vc_LimitLow.addElement(sc_StartDate);
            vc_LimitLow.addElement("-");
            vc_LimitLow.addElement(arguments.get(1));
            sc_LimitLow.setOpenBrace("(");
            sc_LimitLow.setCloseBrace(")");
            sc_LimitLow.setColumnExpression(vc_LimitLow);
            vc_Limit.addElement(sc_LimitLow);
            vc_Limit.addElement(" AND ");
            final SelectColumn sc_LimitUp = new SelectColumn();
            final Vector vc_LimitUp = new Vector();
            final SelectColumn sc_EndDate = new SelectColumn();
            final FunctionCalls fnCl_EndDate = new FunctionCalls();
            final TableColumn tbCl_EndDate = new TableColumn();
            tbCl_EndDate.setColumnName("YEAR");
            fnCl_EndDate.setFunctionName(tbCl_StartDate);
            final Vector vc_EndDateIn = new Vector();
            final Vector vc_EndDateOut = new Vector();
            vc_EndDateIn.addElement(this.current_date());
            fnCl_EndDate.setFunctionArguments(vc_EndDateIn);
            vc_EndDateOut.addElement(fnCl_EndDate);
            sc_EndDate.setColumnExpression(vc_EndDateOut);
            vc_LimitUp.addElement(sc_EndDate);
            vc_LimitUp.addElement("-");
            vc_LimitUp.addElement("1");
            sc_LimitUp.setOpenBrace("(");
            sc_LimitUp.setCloseBrace(")");
            sc_LimitUp.setColumnExpression(vc_LimitUp);
            vc_Limit.addElement(sc_LimitUp);
            sc_Limit.setColumnExpression(vc_Limit);
            vc_if_right.addElement(sc_Limit);
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            vc_wi.addElement(wi_if);
            sc_wi.setColumnExpression(vc_wi);
            vc_ifStatement.addElement(sc_wi);
            vc_ifStatement.addElement("1");
            vc_ifStatement.addElement("0");
            this.setFunctionArguments(vc_ifStatement);
        }
        else if (fnStr.equalsIgnoreCase("ISPREVIOUS_NQUARTER")) {
            this.functionName.setColumnName("ZR_ISPREVIOUSQUARTER");
            arguments.addElement(this.current_date());
            this.setFunctionArguments(arguments);
        }
        else if (fnStr.equalsIgnoreCase("ISPREVIOUS_NMONTH")) {
            this.functionName.setColumnName("ZR_ISPREVIOUSMONTH");
            arguments.addElement(this.current_date());
            this.setFunctionArguments(arguments);
        }
        else if (fnStr.equalsIgnoreCase("ISPREVIOUSWEEK")) {
            String weekStDay = "";
            if (arguments.size() > 1 && arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc = arguments.elementAt(1);
                final Vector vc = sc.getColumnExpression();
                if (!(vc.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function ISPREVIOUSWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "ISPREVIOUSWEEK", "WEEK_STARTDAY" });
                }
                weekStDay = vc.elementAt(0);
                weekStDay = weekStDay.replaceAll("'", "");
                weekStDay = weekStDay.trim();
                this.validateWeek_StartDay(weekStDay, fnStr.toUpperCase());
                if (weekStDay.equals("0")) {
                    weekStDay = "6";
                }
                else {
                    weekStDay = "3";
                }
            }
            else {
                weekStDay = "3";
            }
            this.functionName.setColumnName("IF");
            final Vector vc_if = new Vector();
            final SelectColumn sc_wi2 = new SelectColumn();
            final Vector vc_wi2 = new Vector();
            final WhereItem wi_if2 = new WhereItem();
            final WhereColumn if_left2 = new WhereColumn();
            final Vector vc_if_left2 = new Vector();
            final WhereColumn if_right2 = new WhereColumn();
            final Vector vc_if_right2 = new Vector();
            final SelectColumn sc_yearWeekleft = new SelectColumn();
            final FunctionCalls fn_yearWeekleft = new FunctionCalls();
            final TableColumn tb_yearWeekleft = new TableColumn();
            tb_yearWeekleft.setColumnName("YEARWEEK");
            fn_yearWeekleft.setFunctionName(tb_yearWeekleft);
            final Vector vc_yearWeekleftIn = new Vector();
            final Vector vc_yearWeekleftOut = new Vector();
            final SelectColumn sc_dateAdd = new SelectColumn();
            final FunctionCalls fn_dateAdd = new FunctionCalls();
            final TableColumn tb_dateAdd = new TableColumn();
            tb_dateAdd.setColumnName("DATE_ADD");
            fn_dateAdd.setFunctionName(tb_dateAdd);
            final Vector vc_dateAddIn = new Vector();
            final Vector vc_dateAddOut = new Vector();
            vc_dateAddIn.addElement(arguments.get(0));
            final SelectColumn sc_intr = new SelectColumn();
            final Vector vc_intr = new Vector();
            final TableColumn tbCl_interval = new TableColumn();
            tbCl_interval.setColumnName("INTERVAL");
            vc_intr.addElement(tbCl_interval);
            if (weekStDay.equals("6")) {
                final SelectColumn sc_dayofweek = new SelectColumn();
                final FunctionCalls fn_dayofweek = new FunctionCalls();
                final TableColumn tb_dayofweek = new TableColumn();
                tb_dayofweek.setColumnName("DAYOFWEEK");
                fn_dayofweek.setFunctionName(tb_dayofweek);
                final Vector vc_dayofweekIn = new Vector();
                if (arguments.elementAt(0) instanceof SelectColumn) {
                    final SelectColumn sc2 = this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs);
                    vc_dayofweekIn.addElement(sc2);
                }
                else {
                    vc_dayofweekIn.addElement(this.functionArguments.elementAt(0));
                }
                final Vector vc_dayofweekOut = new Vector();
                fn_dayofweek.setFunctionArguments(vc_dayofweekIn);
                vc_dayofweekOut.addElement(fn_dayofweek);
                sc_dayofweek.setColumnExpression(vc_dayofweekOut);
                vc_intr.add("(");
                vc_intr.add("8");
                vc_intr.add("-");
                vc_intr.addElement(sc_dayofweek);
                vc_intr.add(")");
                vc_intr.add(" day");
                sc_intr.setColumnExpression(vc_intr);
            }
            else if (weekStDay.equals("3")) {
                final SelectColumn sc_weekday = new SelectColumn();
                final FunctionCalls fn_weekday = new FunctionCalls();
                final TableColumn tb_weekday = new TableColumn();
                tb_weekday.setColumnName("WEEKDAY");
                fn_weekday.setFunctionName(tb_weekday);
                final Vector vc_weekdayIn = new Vector();
                if (arguments.elementAt(0) instanceof SelectColumn) {
                    final SelectColumn sc2 = this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs);
                    vc_weekdayIn.addElement(sc2);
                }
                else {
                    vc_weekdayIn.addElement(this.functionArguments.elementAt(0));
                }
                final Vector vc_dayofweekOut = new Vector();
                fn_weekday.setFunctionArguments(vc_weekdayIn);
                vc_dayofweekOut.addElement(fn_weekday);
                sc_weekday.setColumnExpression(vc_dayofweekOut);
                vc_intr.add("(");
                vc_intr.add("7");
                vc_intr.add("-");
                vc_intr.addElement(sc_weekday);
                vc_intr.add(")");
                vc_intr.add(" day");
                sc_intr.setColumnExpression(vc_intr);
            }
            vc_dateAddIn.addElement(sc_intr);
            fn_dateAdd.setFunctionArguments(vc_dateAddIn);
            vc_dateAddOut.addElement(fn_dateAdd);
            sc_dateAdd.setOpenBrace("(");
            sc_dateAdd.setCloseBrace(")");
            sc_dateAdd.setColumnExpression(vc_dateAddOut);
            vc_yearWeekleftIn.addElement(sc_dateAdd);
            vc_yearWeekleftIn.addElement(weekStDay);
            fn_yearWeekleft.setFunctionArguments(vc_yearWeekleftIn);
            vc_yearWeekleftOut.addElement(fn_yearWeekleft);
            sc_yearWeekleft.setColumnExpression(vc_yearWeekleftOut);
            vc_if_left2.addElement(sc_yearWeekleft);
            if_left2.setColumnExpression(vc_if_left2);
            wi_if2.setLeftWhereExp(if_left2);
            wi_if2.setOperator("=");
            final SelectColumn sc_yearWeekright = new SelectColumn();
            final FunctionCalls fn_yearWeekright = new FunctionCalls();
            final TableColumn tb_yearWeekright = new TableColumn();
            tb_yearWeekright.setColumnName("YEARWEEK");
            fn_yearWeekright.setFunctionName(tb_yearWeekright);
            final Vector vc_yearWeekrightIn = new Vector();
            final Vector vc_yearWeekrightOut = new Vector();
            vc_yearWeekrightIn.addElement(this.current_date());
            vc_yearWeekrightIn.addElement(weekStDay);
            fn_yearWeekright.setFunctionArguments(vc_yearWeekrightIn);
            vc_yearWeekrightOut.addElement(fn_yearWeekright);
            sc_yearWeekright.setColumnExpression(vc_yearWeekrightOut);
            vc_if_right2.addElement(sc_yearWeekright);
            if_right2.setColumnExpression(vc_if_right2);
            wi_if2.setRightWhereExp(if_right2);
            vc_wi2.addElement(wi_if2);
            sc_wi2.setColumnExpression(vc_wi2);
            vc_if.addElement(sc_wi2);
            final String t_num = "1";
            final String f_num = "0";
            vc_if.addElement(t_num);
            vc_if.addElement(f_num);
            this.setFunctionArguments(vc_if);
        }
        else if (fnStr.equalsIgnoreCase("ISPREVIOUS_NDAY")) {
            this.functionName.setColumnName("IF");
            final Vector vc_ifStatement = new Vector();
            final SelectColumn sc_wi = new SelectColumn();
            final Vector vc_wi = new Vector();
            final WhereItem wi_if = new WhereItem();
            final WhereColumn if_left = new WhereColumn();
            final Vector vc_if_left = new Vector();
            final WhereColumn if_right = new WhereColumn();
            final Vector vc_if_right = new Vector();
            final SelectColumn sc_Date = new SelectColumn();
            final FunctionCalls fn_Date = new FunctionCalls();
            final TableColumn tb_Date = new TableColumn();
            tb_Date.setColumnName("DATE");
            fn_Date.setFunctionName(tb_Date);
            final Vector vc_DateIn = new Vector();
            final Vector vc_DateOut = new Vector();
            vc_DateIn.addElement(arguments.get(0));
            fn_Date.setFunctionArguments(vc_DateIn);
            vc_DateOut.addElement(fn_Date);
            sc_Date.setColumnExpression(vc_DateOut);
            vc_if_left.addElement(sc_Date);
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator("BETWEEN");
            final SelectColumn sc_Limit = new SelectColumn();
            final Vector vc_Limit = new Vector();
            final SelectColumn sc_dateSubLow = new SelectColumn();
            final FunctionCalls fn_dateSubLow = new FunctionCalls();
            final TableColumn tb_dateSubLow = new TableColumn();
            tb_dateSubLow.setColumnName("DATE_SUB");
            fn_dateSubLow.setFunctionName(tb_dateSubLow);
            final Vector vc_dateSubLowIn = new Vector();
            final Vector vc_dateSubLowOut = new Vector();
            vc_dateSubLowIn.addElement(this.current_date());
            final SelectColumn sc_intrLow = new SelectColumn();
            final Vector vc_intrLow = new Vector();
            final TableColumn tbCl_intervalLow = new TableColumn();
            tbCl_intervalLow.setColumnName("INTERVAL");
            vc_intrLow.addElement(tbCl_intervalLow);
            vc_intrLow.add("(");
            vc_intrLow.addElement(arguments.get(1));
            vc_intrLow.add(")");
            vc_intrLow.addElement(" day");
            sc_intrLow.setColumnExpression(vc_intrLow);
            vc_dateSubLowIn.addElement(sc_intrLow);
            fn_dateSubLow.setFunctionArguments(vc_dateSubLowIn);
            vc_dateSubLowOut.addElement(fn_dateSubLow);
            sc_dateSubLow.setColumnExpression(vc_dateSubLowOut);
            vc_Limit.addElement(sc_dateSubLow);
            vc_Limit.addElement("AND");
            final SelectColumn sc_dateSubHigh = new SelectColumn();
            final FunctionCalls fn_dateSubHigh = new FunctionCalls();
            final TableColumn tb_dateSubHigh = new TableColumn();
            tb_dateSubHigh.setColumnName("DATE_SUB");
            fn_dateSubHigh.setFunctionName(tb_dateSubHigh);
            final Vector vc_dateSubHighIn = new Vector();
            final Vector vc_dateSubHighOut = new Vector();
            vc_dateSubHighIn.addElement(this.current_date());
            final SelectColumn sc_intrHigh = new SelectColumn();
            final Vector vc_intrHigh = new Vector();
            final TableColumn tbCl_intervalHigh = new TableColumn();
            tbCl_intervalHigh.setColumnName("INTERVAL");
            vc_intrHigh.addElement(tbCl_intervalHigh);
            vc_intrHigh.add("(");
            vc_intrHigh.add("1");
            vc_intrHigh.add(")");
            vc_intrHigh.addElement(" day");
            sc_intrHigh.setColumnExpression(vc_intrHigh);
            vc_dateSubHighIn.addElement(sc_intrHigh);
            fn_dateSubHigh.setFunctionArguments(vc_dateSubHighIn);
            vc_dateSubHighOut.addElement(fn_dateSubHigh);
            sc_dateSubHigh.setColumnExpression(vc_dateSubHighOut);
            vc_Limit.addElement(sc_dateSubHigh);
            sc_Limit.setColumnExpression(vc_Limit);
            vc_if_right.addElement(sc_Limit);
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            vc_wi.addElement(wi_if);
            sc_wi.setColumnExpression(vc_wi);
            vc_ifStatement.addElement(sc_wi);
            vc_ifStatement.addElement("1");
            vc_ifStatement.addElement("0");
            this.setFunctionArguments(vc_ifStatement);
        }
        else if (fnStr.equalsIgnoreCase("YESTERDAY")) {
            this.functionName.setColumnName("DATE_SUB");
            arguments.addElement(this.current_date());
            final SelectColumn sc3 = new SelectColumn();
            final Vector colExp = new Vector();
            final TableColumn tbCl_interval2 = new TableColumn();
            tbCl_interval2.setColumnName("INTERVAL");
            colExp.addElement(tbCl_interval2);
            colExp.add("1 ");
            colExp.add("DAY");
            sc3.setColumnExpression(colExp);
            arguments.addElement(sc3);
            this.setFunctionArguments(arguments);
        }
        else if (fnStr.equalsIgnoreCase("PREVIOUS_NDAY")) {
            this.functionName.setColumnName("DATE_SUB");
            final SelectColumn sc_interval = new SelectColumn();
            final Vector vc_prevnday = new Vector();
            vc_prevnday.addElement(arguments.get(0));
            final Vector colExp2 = new Vector();
            final TableColumn tbCl_interval3 = new TableColumn();
            tbCl_interval3.setColumnName("INTERVAL");
            colExp2.addElement(tbCl_interval3);
            colExp2.add("(");
            colExp2.addElement(arguments.elementAt(1));
            colExp2.add(")");
            colExp2.add(" DAY");
            sc_interval.setColumnExpression(colExp2);
            vc_prevnday.addElement(sc_interval);
            this.setFunctionArguments(vc_prevnday);
        }
        else if (fnStr.equalsIgnoreCase("PREVIOUS_NMONTH")) {
            this.functionName.setColumnName("DATE_SUB");
            final SelectColumn sc_interval = new SelectColumn();
            final Vector vc_prevnmonth = new Vector();
            vc_prevnmonth.addElement(arguments.get(0));
            final Vector colExp2 = new Vector();
            final TableColumn tbCl_interval3 = new TableColumn();
            tbCl_interval3.setColumnName("INTERVAL");
            colExp2.addElement(tbCl_interval3);
            colExp2.add("(");
            colExp2.addElement(arguments.get(1));
            colExp2.add(")");
            colExp2.add(" MONTH");
            sc_interval.setColumnExpression(colExp2);
            vc_prevnmonth.addElement(sc_interval);
            this.setFunctionArguments(vc_prevnmonth);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
        final Vector arguments = new Vector();
        final boolean isRedshift = from_sqs != null && from_sqs.isAmazonRedShift();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    this.handleStringLiteralForDate(from_sqs, i_count, !isRedshift);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        String qry = "";
        if (isRedshift) {
            if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
                qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date-(interval '1' month * (" + arguments.get(1).toString() + ")))) and (date_trunc('month',current_date)-interval '1' day) then 1 else 0 end";
            }
            else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
                qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date)-(interval '1' month *3 * (" + arguments.get(1).toString() + "))) and (date_trunc('quarter',current_date)-interval '1' day) then 1 else 0 end";
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
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    this.handleStringLiteralForDate(from_sqs, i_count, false);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "";
        if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
            qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date-(interval '1' month * (" + arguments.get(1).toString() + ")))) and (date_trunc('month',current_date)-interval '1' day) ,1 ,0)";
        }
        else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
            qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date)-(interval '1' month *3 *(" + arguments.get(1).toString() + "))) and (date_trunc('quarter',current_date)-interval '1' day) ,1,0)";
        }
        this.functionName.setColumnName(qry);
        this.setFunctionArguments(new Vector());
        this.setOpenBracesForFunctionNameRequired(false);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
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
        if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
            qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(day, -(DAY(current_date()) -1), CAST(DATEADD(month, -(" + arguments.get(1).toString() + "), CAST(CURRENT_DATE() AS DATETIME )) AS DATETIME ))  AND  DATEADD(day, -(DAY(current_date())), CAST(CURRENT_DATE() AS DATETIME ))  THEN 1 ELSE 0 END";
        }
        else if (fnStr.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
            qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(quarter, -(" + arguments.get(1).toString() + "), CAST(format(CAST((YEAR(current_date()) + '-' + ((CAST(DATEPART( q , current_date()) AS VARCHAR) * 3) -2) + '-' + 1) AS DATE), 'yyyy-MM-dd') AS DATETIME ))  AND  DATEADD(day, -1, CAST(DATEADD(quarter, (CAST(DATEPART( q , current_date()) AS VARCHAR) -1), CAST(format(CURRENT_DATE(), 'yyyy-01-01') AS DATETIME )) AS DATETIME ))  THEN 1 ELSE 0 END";
        }
        this.functionName.setColumnName(qry);
        this.setFunctionArguments(new Vector());
        this.setOpenBracesForFunctionNameRequired(false);
    }
    
    public SelectColumn current_date() {
        final SelectColumn sc_CurrDate = new SelectColumn();
        final FunctionCalls fn_CurrDate = new FunctionCalls();
        final TableColumn tb_CurrDate = new TableColumn();
        tb_CurrDate.setColumnName("CURRENT_DATE");
        fn_CurrDate.setFunctionName(tb_CurrDate);
        final Vector vc_CurrDateIn = new Vector();
        final Vector vc_CurrDateOut = new Vector();
        fn_CurrDate.setFunctionArguments(vc_CurrDateIn);
        vc_CurrDateOut.addElement(fn_CurrDate);
        sc_CurrDate.setColumnExpression(vc_CurrDateOut);
        return sc_CurrDate;
    }
}
