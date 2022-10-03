package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class start_datetime extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
        this.functionName.setColumnName("TIMESTAMP");
        final Vector finalArgument = new Vector();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final SelectColumn sc_dateFmt = new SelectColumn();
        final FunctionCalls fnCall_dateFmt = new FunctionCalls();
        final TableColumn tb_dateFmt = new TableColumn();
        tb_dateFmt.setColumnName("DATE_FORMAT");
        fnCall_dateFmt.setFunctionName(tb_dateFmt);
        final Vector vc_dateFmtIn = new Vector();
        final Vector vc_dateFmtOut = new Vector();
        String units = "";
        String format = "";
        if (arguments.elementAt(1) instanceof SelectColumn) {
            final SelectColumn sc2 = arguments.elementAt(1);
            final Vector vc = sc2.getColumnExpression();
            if (vc.elementAt(0) instanceof TableColumn) {
                final TableColumn tb_unit = vc.elementAt(0);
                units = tb_unit.getColumnName();
                units = units.replaceAll("'", "");
            }
            if (units.equalsIgnoreCase("SECOND")) {
                vc_dateFmtIn.addElement(arguments.get(0));
                format = "'%Y-%m-%d %H:%i:%S'";
            }
            else if (units.equalsIgnoreCase("MINUTE")) {
                vc_dateFmtIn.addElement(arguments.get(0));
                format = "'%Y-%m-%d %H:%i:00'";
            }
            else if (units.equalsIgnoreCase("HOUR")) {
                vc_dateFmtIn.addElement(arguments.get(0));
                format = "'%Y-%m-%d %H:00:00'";
            }
            else if (units.equalsIgnoreCase("DAY")) {
                vc_dateFmtIn.addElement(arguments.get(0));
                format = "'%Y-%m-%d 00:00:00'";
            }
            else if (units.equalsIgnoreCase("WEEK") || units.equalsIgnoreCase("WEEK_MONDAY")) {
                final SelectColumn sc_dateSub = new SelectColumn();
                final FunctionCalls fnCall_dateSub = new FunctionCalls();
                final TableColumn tb_dateSub = new TableColumn();
                tb_dateSub.setColumnName("DATE_SUB");
                fnCall_dateSub.setFunctionName(tb_dateSub);
                final Vector vc_dateSubIn = new Vector();
                final Vector vc_dateSubOut = new Vector();
                vc_dateSubIn.addElement(arguments.get(0));
                final SelectColumn sc_intr = new SelectColumn();
                final Vector vc_intr = new Vector();
                final TableColumn tbCl_intr = new TableColumn();
                tbCl_intr.setColumnName("INTERVAL");
                vc_intr.add(tbCl_intr);
                final SelectColumn sc_dateWeek = new SelectColumn();
                final FunctionCalls fnCall_dateWeek = new FunctionCalls();
                final TableColumn tb_dateWeek = new TableColumn();
                tb_dateWeek.setColumnName("WEEKDAY");
                fnCall_dateWeek.setFunctionName(tb_dateWeek);
                final Vector vc_dateWeekIn = new Vector();
                final Vector vc_dateWeekOut = new Vector();
                if (arguments.elementAt(0) instanceof SelectColumn) {
                    vc_dateWeekIn.addElement(this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    vc_dateWeekIn.addElement(this.functionArguments.elementAt(0));
                }
                fnCall_dateWeek.setFunctionArguments(vc_dateWeekIn);
                vc_dateWeekOut.addElement(fnCall_dateWeek);
                sc_dateWeek.setColumnExpression(vc_dateWeekOut);
                vc_intr.addElement(sc_dateWeek);
                vc_intr.add(" DAY");
                sc_intr.setColumnExpression(vc_intr);
                vc_dateSubIn.addElement(sc_intr);
                fnCall_dateSub.setFunctionArguments(vc_dateSubIn);
                vc_dateSubOut.addElement(fnCall_dateSub);
                sc_dateSub.setColumnExpression(vc_dateSubOut);
                vc_dateFmtIn.addElement(sc_dateSub);
                format = "'%Y-%m-%d 00:00:00'";
            }
            else if (units.equalsIgnoreCase("WEEK_SUNDAY")) {
                final SelectColumn sc_dateSub = new SelectColumn();
                final FunctionCalls fnCall_dateSub = new FunctionCalls();
                final TableColumn tb_dateSub = new TableColumn();
                tb_dateSub.setColumnName("DATE_SUB");
                fnCall_dateSub.setFunctionName(tb_dateSub);
                final Vector vc_dateSubIn = new Vector();
                final Vector vc_dateSubOut = new Vector();
                vc_dateSubIn.addElement(arguments.get(0));
                final SelectColumn sc_intr = new SelectColumn();
                final Vector vc_intr = new Vector();
                final TableColumn tbCl_intr = new TableColumn();
                tbCl_intr.setColumnName("INTERVAL");
                vc_intr.add(tbCl_intr);
                final SelectColumn sc_value = new SelectColumn();
                final Vector vc_value = new Vector();
                final SelectColumn sc_dateWeek2 = new SelectColumn();
                final FunctionCalls fnCall_dateWeek2 = new FunctionCalls();
                final TableColumn tb_dateWeek2 = new TableColumn();
                tb_dateWeek2.setColumnName("DAYOFWEEK");
                fnCall_dateWeek2.setFunctionName(tb_dateWeek2);
                final Vector vc_dateWeekIn2 = new Vector();
                final Vector vc_dateWeekOut2 = new Vector();
                if (arguments.elementAt(0) instanceof SelectColumn) {
                    vc_dateWeekIn2.addElement(this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    vc_dateWeekIn2.addElement(this.functionArguments.elementAt(0));
                }
                fnCall_dateWeek2.setFunctionArguments(vc_dateWeekIn2);
                vc_dateWeekOut2.addElement(fnCall_dateWeek2);
                sc_dateWeek2.setColumnExpression(vc_dateWeekOut2);
                vc_value.addElement(sc_dateWeek2);
                vc_value.add("-");
                vc_value.add("1");
                sc_value.setOpenBrace("(");
                sc_value.setCloseBrace(")");
                sc_value.setColumnExpression(vc_value);
                vc_intr.addElement(sc_value);
                vc_intr.add(" DAY");
                sc_intr.setColumnExpression(vc_intr);
                vc_dateSubIn.addElement(sc_intr);
                fnCall_dateSub.setFunctionArguments(vc_dateSubIn);
                vc_dateSubOut.addElement(fnCall_dateSub);
                sc_dateSub.setColumnExpression(vc_dateSubOut);
                vc_dateFmtIn.addElement(sc_dateSub);
                format = "'%Y-%m-%d 00:00:00'";
            }
            else if (units.equalsIgnoreCase("MONTH")) {
                vc_dateFmtIn.addElement(arguments.get(0));
                format = "'%Y-%m-01 00:00:00'";
            }
            else if (units.equalsIgnoreCase("QUARTER")) {
                final SelectColumn sc_Date = new SelectColumn();
                final FunctionCalls fn_Date = new FunctionCalls();
                final TableColumn tb_Date = new TableColumn();
                tb_Date.setColumnName("DATE");
                fn_Date.setFunctionName(tb_Date);
                final Vector vc_DateIn = new Vector();
                final Vector vc_DateOut = new Vector();
                final SelectColumn sc_concatws = new SelectColumn();
                final FunctionCalls fnCall_concatws = new FunctionCalls();
                final TableColumn tb_concatws = new TableColumn();
                tb_concatws.setColumnName("CONCAT_WS");
                fnCall_concatws.setFunctionName(tb_concatws);
                final Vector vc_concatwsIn = new Vector();
                final Vector vc_concatwsOut = new Vector();
                vc_concatwsIn.add("'-'");
                final SelectColumn sc_year = new SelectColumn();
                final FunctionCalls fn_year = new FunctionCalls();
                final TableColumn tb_year = new TableColumn();
                tb_year.setColumnName("YEAR");
                fn_year.setFunctionName(tb_year);
                final Vector vc_yearIn = new Vector();
                final Vector vc_yearOut = new Vector();
                vc_yearIn.addElement(arguments.get(0));
                fn_year.setFunctionArguments(vc_yearIn);
                vc_yearOut.addElement(fn_year);
                sc_year.setColumnExpression(vc_yearOut);
                vc_concatwsIn.addElement(sc_year);
                final SelectColumn sc_value2 = new SelectColumn();
                final Vector vc_value2 = new Vector();
                final SelectColumn sc_quarter = new SelectColumn();
                final FunctionCalls fn_quarter = new FunctionCalls();
                final TableColumn tb_quarter = new TableColumn();
                tb_quarter.setColumnName("QUARTER");
                fn_quarter.setFunctionName(tb_quarter);
                final Vector vc_quarterIn = new Vector();
                final Vector vc_quarterOut = new Vector();
                if (arguments.elementAt(0) instanceof SelectColumn) {
                    vc_quarterIn.addElement(this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    vc_quarterIn.addElement(this.functionArguments.elementAt(0));
                }
                fn_quarter.setFunctionArguments(vc_quarterIn);
                vc_quarterOut.addElement(fn_quarter);
                sc_quarter.setColumnExpression(vc_quarterOut);
                vc_value2.addElement("(");
                vc_value2.addElement(sc_quarter);
                vc_value2.add("*");
                vc_value2.add("3");
                vc_value2.addElement(")");
                vc_value2.add("-");
                vc_value2.add("2");
                sc_value2.setOpenBrace("(");
                sc_value2.setCloseBrace(")");
                sc_value2.setColumnExpression(vc_value2);
                vc_concatwsIn.addElement(sc_value2);
                vc_concatwsIn.add("1");
                fnCall_concatws.setFunctionArguments(vc_concatwsIn);
                vc_concatwsOut.addElement(fnCall_concatws);
                sc_concatws.setColumnExpression(vc_concatwsOut);
                vc_DateIn.addElement(sc_concatws);
                fn_Date.setFunctionArguments(vc_DateIn);
                vc_DateOut.addElement(fn_Date);
                sc_Date.setColumnExpression(vc_DateOut);
                vc_dateFmtIn.addElement(sc_Date);
                format = "'%Y-%m-01 00:00:00'";
            }
            else {
                if (!units.equalsIgnoreCase("YEAR")) {
                    throw new ConvertException("Invalid Argument Value for Function START_DATETIME", "INVALID_ARGUMENT_VALUE", new Object[] { "START_DATETIME", "DATE_UNITS", "Provide any one of the following value week, week_sunday, week_monday, month, quarter or year" });
                }
                vc_dateFmtIn.addElement(arguments.get(0));
                format = "'%Y-01-01 00:00:00'";
            }
        }
        vc_dateFmtIn.addElement(format);
        fnCall_dateFmt.setFunctionArguments(vc_dateFmtIn);
        vc_dateFmtOut.addElement(fnCall_dateFmt);
        sc_dateFmt.setColumnExpression(vc_dateFmtOut);
        finalArgument.addElement(sc_dateFmt);
        this.setFunctionArguments(finalArgument);
    }
}
