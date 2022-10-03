package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class end_day extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        String unit = "";
        if (vector.elementAt(0) instanceof SelectColumn) {
            final SelectColumn sc_units = vector.elementAt(0);
            final Vector vc_units = sc_units.getColumnExpression();
            if (vc_units.elementAt(0) instanceof TableColumn) {
                final TableColumn tb_unit = vc_units.elementAt(0);
                unit = tb_unit.getColumnName();
                unit = unit.replaceAll("'", "");
            }
        }
        if (unit.equalsIgnoreCase("year")) {
            this.functionName.setColumnName("DATE");
            final SelectColumn sc_LastDay = new SelectColumn();
            final FunctionCalls fn_LastDay = new FunctionCalls();
            final TableColumn tb_LastDay = new TableColumn();
            tb_LastDay.setColumnName("LAST_DAY");
            fn_LastDay.setFunctionName(tb_LastDay);
            final Vector vc_LastDayIn = new Vector();
            final Vector vc_LastDayOut = new Vector();
            final SelectColumn sc_dateAdd = new SelectColumn();
            final FunctionCalls fnCall_dateAdd = new FunctionCalls();
            final TableColumn tb_dateAdd = new TableColumn();
            tb_dateAdd.setColumnName("DATE_ADD");
            fnCall_dateAdd.setFunctionName(tb_dateAdd);
            final Vector vc_dateAddIn = new Vector();
            final Vector vc_dateAddOut = new Vector();
            vc_dateAddIn.addElement(vector.get(1));
            final SelectColumn sc_intr = new SelectColumn();
            final Vector vc_intr = new Vector();
            final TableColumn tbCl_intr = new TableColumn();
            tbCl_intr.setColumnName("interval");
            vc_intr.add(tbCl_intr);
            final SelectColumn sc_value = new SelectColumn();
            final Vector vc_value = new Vector();
            vc_value.addElement("12");
            vc_value.addElement("-");
            vc_value.addElement(this.units(to_sqs, from_sqs, "month"));
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_intr.addElement(sc_value);
            vc_intr.add(" MONTH");
            sc_intr.setColumnExpression(vc_intr);
            vc_dateAddIn.addElement(sc_intr);
            fnCall_dateAdd.setFunctionArguments(vc_dateAddIn);
            vc_dateAddOut.addElement(fnCall_dateAdd);
            sc_dateAdd.setColumnExpression(vc_dateAddOut);
            vc_LastDayIn.addElement(sc_dateAdd);
            fn_LastDay.setFunctionArguments(vc_LastDayIn);
            vc_LastDayOut.addElement(fn_LastDay);
            sc_LastDay.setColumnExpression(vc_LastDayOut);
            arguments.addElement(sc_LastDay);
        }
        else if (unit.equalsIgnoreCase("quarter")) {
            this.functionName.setColumnName("DATE");
            final SelectColumn sc_LastDay = new SelectColumn();
            final FunctionCalls fn_LastDay = new FunctionCalls();
            final TableColumn tb_LastDay = new TableColumn();
            tb_LastDay.setColumnName("LAST_DAY");
            fn_LastDay.setFunctionName(tb_LastDay);
            final Vector vc_LastDayIn = new Vector();
            final Vector vc_LastDayOut = new Vector();
            final SelectColumn sc_dateAdd = new SelectColumn();
            final FunctionCalls fnCall_dateAdd = new FunctionCalls();
            final TableColumn tb_dateAdd = new TableColumn();
            tb_dateAdd.setColumnName("DATE_ADD");
            fnCall_dateAdd.setFunctionName(tb_dateAdd);
            final Vector vc_dateAddIn = new Vector();
            final Vector vc_dateAddOut = new Vector();
            vc_dateAddIn.addElement(vector.get(1));
            final SelectColumn sc_intr = new SelectColumn();
            final Vector vc_intr = new Vector();
            final TableColumn tbCl_intr = new TableColumn();
            tbCl_intr.setColumnName("interval");
            vc_intr.add(tbCl_intr);
            final SelectColumn sc_value = new SelectColumn();
            final Vector vc_value = new Vector();
            vc_value.addElement(this.units(to_sqs, from_sqs, "quarter"));
            vc_value.addElement("*");
            vc_value.addElement("3");
            vc_value.addElement("-");
            vc_value.addElement(this.units(to_sqs, from_sqs, "month"));
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_intr.addElement(sc_value);
            vc_intr.add(" MONTH");
            sc_intr.setColumnExpression(vc_intr);
            vc_dateAddIn.addElement(sc_intr);
            fnCall_dateAdd.setFunctionArguments(vc_dateAddIn);
            vc_dateAddOut.addElement(fnCall_dateAdd);
            sc_dateAdd.setColumnExpression(vc_dateAddOut);
            vc_LastDayIn.addElement(sc_dateAdd);
            fn_LastDay.setFunctionArguments(vc_LastDayIn);
            vc_LastDayOut.addElement(fn_LastDay);
            sc_LastDay.setColumnExpression(vc_LastDayOut);
            arguments.addElement(sc_LastDay);
        }
        else if (unit.equalsIgnoreCase("month")) {
            this.functionName.setColumnName("DATE");
            final SelectColumn sc_LastDay = new SelectColumn();
            final FunctionCalls fn_LastDay = new FunctionCalls();
            final TableColumn tb_LastDay = new TableColumn();
            tb_LastDay.setColumnName("LAST_DAY");
            fn_LastDay.setFunctionName(tb_LastDay);
            final Vector vc_LastDayIn = new Vector();
            final Vector vc_LastDayOut = new Vector();
            vc_LastDayIn.addElement(vector.get(1));
            fn_LastDay.setFunctionArguments(vc_LastDayIn);
            vc_LastDayOut.addElement(fn_LastDay);
            sc_LastDay.setColumnExpression(vc_LastDayOut);
            arguments.addElement(sc_LastDay);
        }
        else if (unit.equalsIgnoreCase("week") || unit.equalsIgnoreCase("week_monday")) {
            this.functionName.setColumnName("DATE");
            final SelectColumn sc_dateAdd2 = new SelectColumn();
            final FunctionCalls fnCl_dateAdd = new FunctionCalls();
            final TableColumn tbCl_dateAdd = new TableColumn();
            tbCl_dateAdd.setColumnName("DATE_ADD");
            fnCl_dateAdd.setFunctionName(tbCl_dateAdd);
            final Vector vc_dateAddIn2 = new Vector();
            final Vector vc_dateAddOut2 = new Vector();
            vc_dateAddIn2.addElement(vector.get(1));
            final SelectColumn sc_intr2 = new SelectColumn();
            final Vector vc_intr2 = new Vector();
            final TableColumn tbCl_intr2 = new TableColumn();
            tbCl_intr2.setColumnName("interval");
            vc_intr2.add(tbCl_intr2);
            final SelectColumn sc_value2 = new SelectColumn();
            final Vector vc_value2 = new Vector();
            vc_value2.addElement("6");
            vc_value2.addElement("-");
            vc_value2.addElement(this.units(to_sqs, from_sqs, "weekday"));
            sc_value2.setOpenBrace("(");
            sc_value2.setCloseBrace(")");
            sc_value2.setColumnExpression(vc_value2);
            vc_intr2.addElement(sc_value2);
            vc_intr2.add(" DAY");
            sc_intr2.setColumnExpression(vc_intr2);
            vc_dateAddIn2.addElement(sc_intr2);
            fnCl_dateAdd.setFunctionArguments(vc_dateAddIn2);
            vc_dateAddOut2.addElement(fnCl_dateAdd);
            sc_dateAdd2.setColumnExpression(vc_dateAddOut2);
            arguments.addElement(sc_dateAdd2);
        }
        else {
            if (!unit.equalsIgnoreCase("week_sunday")) {
                throw new ConvertException("Invalid Argument Value for Function END_DAY", "INVALID_ARGUMENT_VALUE", new Object[] { "END_DAY", "DATE_UNITS", "Provide any one of the following value week, week_sunday, week_monday, month, quarter or year" });
            }
            this.functionName.setColumnName("DATE");
            final SelectColumn sc_dateAdd2 = new SelectColumn();
            final FunctionCalls fnCl_dateAdd = new FunctionCalls();
            final TableColumn tbCl_dateAdd = new TableColumn();
            tbCl_dateAdd.setColumnName("DATE_ADD");
            fnCl_dateAdd.setFunctionName(tbCl_dateAdd);
            final Vector vc_dateAddIn2 = new Vector();
            final Vector vc_dateAddOut2 = new Vector();
            vc_dateAddIn2.addElement(vector.get(1));
            final SelectColumn sc_intr2 = new SelectColumn();
            final Vector vc_intr2 = new Vector();
            final TableColumn tbCl_intr2 = new TableColumn();
            tbCl_intr2.setColumnName("interval");
            vc_intr2.add(tbCl_intr2);
            final SelectColumn sc_value2 = new SelectColumn();
            final Vector vc_value2 = new Vector();
            vc_value2.addElement("7");
            vc_value2.addElement("-");
            vc_value2.addElement(this.units(to_sqs, from_sqs, "dayofweek"));
            sc_value2.setOpenBrace("(");
            sc_value2.setCloseBrace(")");
            sc_value2.setColumnExpression(vc_value2);
            vc_intr2.addElement(sc_value2);
            vc_intr2.add(" DAY");
            sc_intr2.setColumnExpression(vc_intr2);
            vc_dateAddIn2.addElement(sc_intr2);
            fnCl_dateAdd.setFunctionArguments(vc_dateAddIn2);
            vc_dateAddOut2.addElement(fnCl_dateAdd);
            sc_dateAdd2.setColumnExpression(vc_dateAddOut2);
            arguments.addElement(sc_dateAdd2);
        }
        this.setFunctionArguments(arguments);
    }
    
    public SelectColumn units(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final String unit) throws ConvertException {
        final SelectColumn sc_unit = new SelectColumn();
        final FunctionCalls fnCall_unit = new FunctionCalls();
        final TableColumn tb_unit = new TableColumn();
        tb_unit.setColumnName(unit);
        fnCall_unit.setFunctionName(tb_unit);
        final Vector vc_unitIn = new Vector();
        final Vector vc_unitOut = new Vector();
        if (this.functionArguments.elementAt(1) instanceof SelectColumn) {
            vc_unitIn.addElement(this.functionArguments.elementAt(1).toMySQLSelect(to_sqs, from_sqs));
        }
        else {
            vc_unitIn.addElement(this.functionArguments.elementAt(1));
        }
        fnCall_unit.setFunctionArguments(vc_unitIn);
        vc_unitOut.addElement(fnCall_unit);
        sc_unit.setOpenBrace("(");
        sc_unit.setCloseBrace(")");
        sc_unit.setColumnExpression(vc_unitOut);
        return sc_unit;
    }
}
