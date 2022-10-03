package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class lastDate extends FunctionCalls
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
        if (fnStr.equalsIgnoreCase("ISLAST_NYEAR")) {
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
            wi_if.setOperator("BETWEEN");
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
            vc_Limit.addElement("AND");
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
        else if (fnStr.equalsIgnoreCase("ISLAST_NQUARTER")) {
            this.functionName.setColumnName("ZR_ISLASTQUARTER");
            arguments.addElement(this.current_date());
            this.setFunctionArguments(arguments);
        }
        else if (fnStr.equalsIgnoreCase("ISLAST_NMONTH")) {
            this.functionName.setColumnName("ZR_ISLASTMONTH");
            arguments.addElement(this.current_date());
            this.setFunctionArguments(arguments);
        }
        else if (fnStr.equalsIgnoreCase("ISLAST_NDAY")) {
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
            final TableColumn tbCl_interval = new TableColumn();
            tbCl_interval.setColumnName("INTERVAL");
            vc_intrLow.addElement(tbCl_interval);
            vc_intrLow.add("(");
            vc_intrLow.add("(");
            vc_intrLow.addElement(arguments.get(1));
            vc_intrLow.add(")");
            vc_intrLow.add("-");
            vc_intrLow.add("1");
            vc_intrLow.add(")");
            vc_intrLow.addElement(" day");
            sc_intrLow.setColumnExpression(vc_intrLow);
            vc_dateSubLowIn.addElement(sc_intrLow);
            fn_dateSubLow.setFunctionArguments(vc_dateSubLowIn);
            vc_dateSubLowOut.addElement(fn_dateSubLow);
            sc_dateSubLow.setColumnExpression(vc_dateSubLowOut);
            vc_Limit.addElement(sc_dateSubLow);
            vc_Limit.addElement("AND");
            vc_Limit.addElement(this.current_date());
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
        else if (fnStr.equalsIgnoreCase("LAST_NDAY")) {
            this.functionName.setColumnName("DATE_SUB");
            final Vector vc_lastday = new Vector();
            vc_lastday.addElement(arguments.get(0));
            final SelectColumn sc_interval = new SelectColumn();
            final Vector colExp = new Vector();
            final TableColumn tbCl_interval2 = new TableColumn();
            tbCl_interval2.setColumnName("INTERVAL");
            colExp.add(tbCl_interval2);
            colExp.add("(");
            colExp.add("(");
            colExp.addElement(arguments.get(1));
            colExp.add(")");
            colExp.addElement("-");
            colExp.addElement("1");
            colExp.add(")");
            colExp.add(" DAY");
            sc_interval.setColumnExpression(colExp);
            vc_lastday.addElement(sc_interval);
            this.setFunctionArguments(vc_lastday);
        }
        else if (fnStr.equalsIgnoreCase("LAST_NMONTH")) {
            this.functionName.setColumnName("DATE_SUB");
            final Vector vc_lastmonth = new Vector();
            vc_lastmonth.addElement(arguments.get(0));
            final SelectColumn sc_interval = new SelectColumn();
            final Vector colExp = new Vector();
            final TableColumn tbCl_interval2 = new TableColumn();
            tbCl_interval2.setColumnName("INTERVAL");
            colExp.add(tbCl_interval2);
            colExp.add("(");
            colExp.add("(");
            colExp.addElement(arguments.get(1));
            colExp.add(")");
            colExp.addElement("-");
            colExp.addElement("1");
            colExp.add(")");
            colExp.add(" MONTH");
            sc_interval.setColumnExpression(colExp);
            vc_lastmonth.addElement(sc_interval);
            this.setFunctionArguments(vc_lastmonth);
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
            if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
                qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date-(interval '1' month * ((" + arguments.get(1).toString() + ")-1) ))) and (date_trunc('MONTH', current_date) + INTERVAL '1 MONTH - 1 day') then 1 else 0 end";
            }
            else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
                qry = "case when CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date)-(interval '1' month *3 * ((" + arguments.get(1).toString() + ")-1))) and (date_trunc('quarter',current_date + interval '1' month * 3)-interval '1' day) then 1 else 0 end";
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
        if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
            qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('month',current_date-(interval '1' month * ((" + arguments.get(1).toString() + ")-1) ))) and (date_trunc('MONTH', current_date) + INTERVAL '1' MONTH - INTERVAL '1' day),1,0)";
        }
        else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
            qry = "if(CAST(" + arguments.get(0).toString() + " AS DATE) between (date_trunc('quarter',current_date)-(interval '1' month *3 * ((" + arguments.get(1).toString() + ")-1))) and (date_trunc('quarter',current_date + interval '1' month * 3)-interval '1' day),1,0)";
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
        if (fnStr.equalsIgnoreCase("ZR_ISLASTMONTH")) {
            qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(day, -(DAY(current_date()) -1), CAST(DATEADD(month, -((" + arguments.get(1).toString() + ") -1), CAST(CURRENT_DATE() AS DATETIME )) AS DATETIME ))  AND  DATEADD(D, -DAY(DATEADD(M, 1, current_date())), DATEADD(M, 1, current_date()))  THEN 1 ELSE 0 END";
        }
        else if (fnStr.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
            qry = "CASE WHEN CAST(" + arguments.get(0).toString() + " AS DATE)  BETWEEN DATEADD(quarter, -((" + arguments.get(1).toString() + ") -1), CAST(format(CAST((YEAR(current_date()) + '-' + ((CAST(DATEPART( q , current_date()) AS VARCHAR) * 3) -2) + '-' + 1) AS DATE), 'yyyy-MM-dd') AS DATETIME ))  AND  DATEADD(day, -1, CAST(DATEADD(quarter, CAST(DATEPART( q , current_date()) AS VARCHAR), CAST(format(CURRENT_DATE(), 'yyyy-01-01') AS DATETIME )) AS DATETIME ))  THEN 1 ELSE 0 END";
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
