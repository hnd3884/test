package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class currentDate extends FunctionCalls
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
        if (fnStr.equalsIgnoreCase("ISCURRENTYEAR")) {
            this.functionName.setColumnName("IF");
            final SelectColumn sc_wi = new SelectColumn();
            final Vector vc_wi = new Vector();
            final String t_num = "1";
            final String f_num = "0";
            final WhereItem wi_if = new WhereItem();
            final Vector vc_if = new Vector();
            final WhereColumn if_left = new WhereColumn();
            final Vector vc_if_left = new Vector();
            final WhereColumn if_right = new WhereColumn();
            final Vector vc_if_right = new Vector();
            final SelectColumn sc_year_left = new SelectColumn();
            final FunctionCalls fn_year_left = new FunctionCalls();
            final TableColumn tb_year_left = new TableColumn();
            tb_year_left.setColumnName("YEAR");
            fn_year_left.setFunctionName(tb_year_left);
            final Vector vc_yearIn_left = new Vector();
            final Vector vc_yearOut_left = new Vector();
            vc_yearIn_left.addElement(this.current_date());
            fn_year_left.setFunctionArguments(vc_yearIn_left);
            vc_yearOut_left.addElement(fn_year_left);
            sc_year_left.setColumnExpression(vc_yearOut_left);
            vc_if_left.addElement(sc_year_left);
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator("=");
            final SelectColumn sc_year_right = new SelectColumn();
            final FunctionCalls fn_year_right = new FunctionCalls();
            final TableColumn tb_year_right = new TableColumn();
            tb_year_right.setColumnName("YEAR");
            fn_year_right.setFunctionName(tb_year_right);
            final Vector vc_yearIn_right = new Vector();
            final Vector vc_yearOut_right = new Vector();
            vc_yearIn_right.addElement(arguments.get(0));
            fn_year_right.setFunctionArguments(vc_yearIn_right);
            vc_yearOut_right.addElement(fn_year_right);
            sc_year_right.setColumnExpression(vc_yearOut_right);
            vc_if_right.addElement(sc_year_right);
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            vc_wi.addElement(wi_if);
            sc_wi.setColumnExpression(vc_wi);
            vc_if.addElement(sc_wi);
            vc_if.addElement(t_num);
            vc_if.addElement(f_num);
            this.setFunctionArguments(vc_if);
        }
        else if (fnStr.equalsIgnoreCase("ISCURRENTQUARTER")) {
            this.functionName.setColumnName("IF");
            final Vector vc_if2 = new Vector();
            final SelectColumn sc_wi2 = new SelectColumn();
            final String t_num = "1";
            final String f_num = "0";
            final WhereExpression whExp = new WhereExpression();
            final Vector vc_whExp = new Vector();
            final Vector vc_whItem = new Vector();
            final WhereItem wi_ifyear = new WhereItem();
            final Vector vc_ifyear = new Vector();
            final WhereColumn if_leftyear = new WhereColumn();
            final Vector vc_if_leftyear = new Vector();
            final WhereColumn if_rightyear = new WhereColumn();
            final Vector vc_if_rightyear = new Vector();
            final SelectColumn sc_year_left2 = new SelectColumn();
            final FunctionCalls fn_year_left2 = new FunctionCalls();
            final TableColumn tb_year_left2 = new TableColumn();
            tb_year_left2.setColumnName("YEAR");
            fn_year_left2.setFunctionName(tb_year_left2);
            final Vector vc_yearIn_left2 = new Vector();
            final Vector vc_yearOut_left2 = new Vector();
            vc_yearIn_left2.addElement(this.current_date());
            fn_year_left2.setFunctionArguments(vc_yearIn_left2);
            vc_yearOut_left2.addElement(fn_year_left2);
            sc_year_left2.setColumnExpression(vc_yearOut_left2);
            vc_if_leftyear.addElement(sc_year_left2);
            if_leftyear.setColumnExpression(vc_if_leftyear);
            wi_ifyear.setLeftWhereExp(if_leftyear);
            wi_ifyear.setOperator("=");
            final SelectColumn sc_year_right2 = new SelectColumn();
            final FunctionCalls fn_year_right2 = new FunctionCalls();
            final TableColumn tb_year_right2 = new TableColumn();
            tb_year_right2.setColumnName("YEAR");
            fn_year_right2.setFunctionName(tb_year_right2);
            final Vector vc_yearIn_right2 = new Vector();
            final Vector vc_yearOut_right2 = new Vector();
            vc_yearIn_right2.addElement(arguments.get(0));
            fn_year_right2.setFunctionArguments(vc_yearIn_right2);
            vc_yearOut_right2.addElement(fn_year_right2);
            sc_year_right2.setColumnExpression(vc_yearOut_right2);
            vc_if_rightyear.addElement(sc_year_right2);
            if_rightyear.setColumnExpression(vc_if_rightyear);
            wi_ifyear.setRightWhereExp(if_rightyear);
            vc_whItem.addElement(wi_ifyear);
            final WhereItem wi_ifquarter = new WhereItem();
            final Vector vc_ifquarter = new Vector();
            final WhereColumn if_leftquarter = new WhereColumn();
            final Vector vc_if_leftquarter = new Vector();
            final WhereColumn if_rightquarter = new WhereColumn();
            final Vector vc_if_rightquarter = new Vector();
            final SelectColumn sc_quarter_left = new SelectColumn();
            final FunctionCalls fn_quarter_left = new FunctionCalls();
            final TableColumn tb_quarter_left = new TableColumn();
            tb_quarter_left.setColumnName("QUARTER");
            fn_quarter_left.setFunctionName(tb_quarter_left);
            final Vector vc_quarterIn_left = new Vector();
            final Vector vc_quarterOut_left = new Vector();
            vc_quarterIn_left.addElement(this.current_date());
            fn_quarter_left.setFunctionArguments(vc_quarterIn_left);
            vc_quarterOut_left.addElement(fn_quarter_left);
            sc_quarter_left.setColumnExpression(vc_quarterOut_left);
            vc_if_leftquarter.addElement(sc_quarter_left);
            if_leftquarter.setColumnExpression(vc_if_leftquarter);
            wi_ifquarter.setLeftWhereExp(if_leftquarter);
            wi_ifquarter.setOperator("=");
            final SelectColumn sc_quarter_right = new SelectColumn();
            final FunctionCalls fn_quarter_right = new FunctionCalls();
            final TableColumn tb_quarter_right = new TableColumn();
            tb_quarter_right.setColumnName("QUARTER");
            fn_quarter_right.setFunctionName(tb_quarter_right);
            final Vector vc_quarterIn_right = new Vector();
            final Vector vc_quarterOut_right = new Vector();
            if (arguments.elementAt(0) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs);
                vc_quarterIn_right.addElement(sc);
            }
            else {
                vc_quarterIn_right.addElement(this.functionArguments.elementAt(0));
            }
            fn_quarter_right.setFunctionArguments(vc_quarterIn_right);
            vc_quarterOut_right.addElement(fn_quarter_right);
            sc_quarter_right.setColumnExpression(vc_quarterOut_right);
            vc_if_rightquarter.addElement(sc_quarter_right);
            if_rightquarter.setColumnExpression(vc_if_rightquarter);
            wi_ifquarter.setRightWhereExp(if_rightquarter);
            vc_whItem.addElement(wi_ifquarter);
            whExp.setWhereItem(vc_whItem);
            final Vector vc_Operator = new Vector();
            vc_Operator.addElement("and");
            whExp.setOperator(vc_Operator);
            vc_whExp.addElement(whExp);
            sc_wi2.setColumnExpression(vc_whExp);
            vc_if2.addElement(sc_wi2);
            vc_if2.addElement(t_num);
            vc_if2.addElement(f_num);
            this.setFunctionArguments(vc_if2);
        }
        else if (fnStr.equalsIgnoreCase("ISCURRENTMONTH")) {
            this.functionName.setColumnName("IF");
            final Vector vc_if2 = new Vector();
            final String t_num2 = "1";
            final String f_num2 = "0";
            final SelectColumn sc_wi3 = new SelectColumn();
            final Vector vc_wi2 = new Vector();
            final WhereItem wi_if2 = new WhereItem();
            final WhereColumn if_left = new WhereColumn();
            final Vector vc_if_left = new Vector();
            final WhereColumn if_right = new WhereColumn();
            final Vector vc_if_right = new Vector();
            final SelectColumn sc_dateFmt_left = new SelectColumn();
            final FunctionCalls fn_dateFmt_left = new FunctionCalls();
            final TableColumn tb_dateFmt_left = new TableColumn();
            tb_dateFmt_left.setColumnName("DATE_FORMAT");
            fn_dateFmt_left.setFunctionName(tb_dateFmt_left);
            final Vector vc_dateFmtIn_left = new Vector();
            final Vector vc_dateFmtOut_left = new Vector();
            vc_dateFmtIn_left.addElement(this.current_date());
            vc_dateFmtIn_left.add("'%Y %M'");
            fn_dateFmt_left.setFunctionArguments(vc_dateFmtIn_left);
            vc_dateFmtOut_left.addElement(fn_dateFmt_left);
            sc_dateFmt_left.setColumnExpression(vc_dateFmtOut_left);
            vc_if_left.addElement(sc_dateFmt_left);
            if_left.setColumnExpression(vc_if_left);
            wi_if2.setLeftWhereExp(if_left);
            wi_if2.setOperator("=");
            final SelectColumn sc_dateFmt_right = new SelectColumn();
            final FunctionCalls fn_dateFmt_right = new FunctionCalls();
            final TableColumn tb_dateFmt_right = new TableColumn();
            tb_dateFmt_right.setColumnName("DATE_FORMAT");
            fn_dateFmt_right.setFunctionName(tb_dateFmt_right);
            final Vector vc_dateFmtIn_right = new Vector();
            final Vector vc_dateFmtOut_right = new Vector();
            vc_dateFmtIn_right.addElement(arguments.get(0));
            vc_dateFmtIn_right.add("'%Y %M'");
            fn_dateFmt_right.setFunctionArguments(vc_dateFmtIn_right);
            vc_dateFmtOut_right.addElement(fn_dateFmt_right);
            sc_dateFmt_right.setColumnExpression(vc_dateFmtOut_right);
            vc_if_right.addElement(sc_dateFmt_right);
            if_right.setColumnExpression(vc_if_right);
            wi_if2.setRightWhereExp(if_right);
            vc_wi2.addElement(wi_if2);
            sc_wi3.setColumnExpression(vc_wi2);
            vc_if2.addElement(sc_wi3);
            vc_if2.addElement(t_num2);
            vc_if2.addElement(f_num2);
            this.setFunctionArguments(vc_if2);
        }
        else if (fnStr.equalsIgnoreCase("ISCURRENTWEEK")) {
            String weekStDay = "";
            if (arguments.size() < 2) {
                weekStDay = "3";
                arguments.add(weekStDay);
            }
            else if (arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc2 = arguments.elementAt(1);
                final Vector vc = sc2.getColumnExpression();
                if (!(vc.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function ISCURRENTWEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "ISCURRENTWEEK", "WEEK_STARTDAY" });
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
            this.functionName.setColumnName("IF");
            final Vector vc_if3 = new Vector();
            final SelectColumn sc_wi4 = new SelectColumn();
            final Vector vc_wi3 = new Vector();
            final WhereItem wi_if = new WhereItem();
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
            vc_yearWeekleftIn.addElement(arguments.get(0));
            vc_yearWeekleftIn.addElement(weekStDay);
            fn_yearWeekleft.setFunctionArguments(vc_yearWeekleftIn);
            vc_yearWeekleftOut.addElement(fn_yearWeekleft);
            sc_yearWeekleft.setColumnExpression(vc_yearWeekleftOut);
            vc_if_left2.addElement(sc_yearWeekleft);
            if_left2.setColumnExpression(vc_if_left2);
            wi_if.setLeftWhereExp(if_left2);
            wi_if.setOperator("=");
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
            wi_if.setRightWhereExp(if_right2);
            vc_wi3.addElement(wi_if);
            sc_wi4.setColumnExpression(vc_wi3);
            vc_if3.addElement(sc_wi4);
            final String t_num3 = "1";
            final String f_num3 = "0";
            vc_if3.addElement(t_num3);
            vc_if3.addElement(f_num3);
            this.setFunctionArguments(vc_if3);
        }
        else if (fnStr.equalsIgnoreCase("TODAY")) {
            this.functionName.setColumnName("CURRENT_DATE");
            this.setFunctionArguments(arguments);
        }
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
