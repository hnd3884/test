package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class start_day extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE");
        final Vector arguments = new Vector();
        final SelectColumn sc_dateSub = new SelectColumn();
        final FunctionCalls fnCl_dateSub = new FunctionCalls();
        final TableColumn tbCl_dateSub = new TableColumn();
        tbCl_dateSub.setColumnName("DATE_SUB");
        fnCl_dateSub.setFunctionName(tbCl_dateSub);
        final Vector vc_dateSubIn = new Vector();
        final Vector vc_dateSubOut = new Vector();
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
        final SelectColumn sc_interval = new SelectColumn();
        final Vector vc_interval = new Vector();
        final TableColumn tbCl_interval = new TableColumn();
        tbCl_interval.setColumnName("interval");
        vc_interval.addElement(tbCl_interval);
        if (vector.elementAt(0) instanceof SelectColumn) {
            final SelectColumn sc_units = vector.elementAt(0);
            final Vector vc_units = sc_units.getColumnExpression();
            if (vc_units.elementAt(0) instanceof TableColumn) {
                final TableColumn tb_unit = vc_units.elementAt(0);
                unit = tb_unit.getColumnName();
                unit = unit.replaceAll("'", "");
            }
        }
        if (unit.equalsIgnoreCase("WEEK") || unit.equalsIgnoreCase("WEEK_MONDAY")) {
            vc_dateSubIn.addElement(vector.get(1));
            vc_interval.addElement(this.units(to_sqs, from_sqs, "weekday"));
            vc_interval.add(" DAY");
            sc_interval.setColumnExpression(vc_interval);
        }
        else if (unit.equalsIgnoreCase("WEEK_SUNDAY")) {
            vc_dateSubIn.addElement(vector.get(1));
            final SelectColumn sc_value = new SelectColumn();
            final Vector vc_value = new Vector();
            vc_value.addElement(this.units(to_sqs, from_sqs, "dayofweek"));
            vc_value.add("-");
            vc_value.add("1");
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_interval.addElement(sc_value);
            vc_interval.add(" DAY");
            sc_interval.setColumnExpression(vc_interval);
        }
        else if (unit.equalsIgnoreCase("MONTH")) {
            vc_dateSubIn.addElement(vector.get(1));
            final SelectColumn sc_value = new SelectColumn();
            final Vector vc_value = new Vector();
            vc_value.addElement(this.units(to_sqs, from_sqs, "dayofmonth"));
            vc_value.add("-");
            vc_value.add("1");
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_interval.addElement(sc_value);
            vc_interval.add(" DAY");
            sc_interval.setColumnExpression(vc_interval);
        }
        else if (unit.equalsIgnoreCase("QUARTER")) {
            final SelectColumn sc_dateSubQtr = new SelectColumn();
            final FunctionCalls fnCall_dateSubQtr = new FunctionCalls();
            final TableColumn tb_dateSubQtr = new TableColumn();
            tb_dateSubQtr.setColumnName("DATE_SUB");
            fnCall_dateSubQtr.setFunctionName(tb_dateSubQtr);
            final Vector vc_dateSubQtrIn = new Vector();
            final Vector vc_dateSubQtrOut = new Vector();
            vc_dateSubQtrIn.addElement(vector.get(1));
            final SelectColumn sc_intr = new SelectColumn();
            final Vector vc_intr = new Vector();
            final TableColumn tbCl_intr = new TableColumn();
            tbCl_intr.setColumnName("interval");
            vc_intr.add(tbCl_intr);
            final SelectColumn sc_monthValue = new SelectColumn();
            final Vector vc_monthValue = new Vector();
            vc_monthValue.addElement("(");
            vc_monthValue.addElement(this.units(to_sqs, from_sqs, "month"));
            vc_monthValue.add("-");
            vc_monthValue.add("1");
            vc_monthValue.addElement(")");
            vc_monthValue.addElement("%");
            vc_monthValue.addElement("3");
            sc_monthValue.setOpenBrace("(");
            sc_monthValue.setCloseBrace(")");
            sc_monthValue.setColumnExpression(vc_monthValue);
            vc_intr.addElement(sc_monthValue);
            vc_intr.add(" MONTH");
            sc_intr.setColumnExpression(vc_intr);
            vc_dateSubQtrIn.addElement(sc_intr);
            fnCall_dateSubQtr.setFunctionArguments(vc_dateSubQtrIn);
            vc_dateSubQtrOut.addElement(fnCall_dateSubQtr);
            sc_dateSubQtr.setColumnExpression(vc_dateSubQtrOut);
            vc_dateSubIn.addElement(sc_dateSubQtr);
            final SelectColumn sc_value2 = new SelectColumn();
            final Vector vc_value2 = new Vector();
            vc_value2.addElement(this.units(to_sqs, from_sqs, "dayofmonth"));
            vc_value2.add("-");
            vc_value2.add("1");
            sc_value2.setOpenBrace("(");
            sc_value2.setCloseBrace(")");
            sc_value2.setColumnExpression(vc_value2);
            vc_interval.addElement(sc_value2);
            vc_interval.add(" DAY");
            sc_interval.setColumnExpression(vc_interval);
        }
        else {
            if (!unit.equalsIgnoreCase("YEAR")) {
                throw new ConvertException("Invalid Argument Value for Function START_DAY", "INVALID_ARGUMENT_VALUE", new Object[] { "START_DAY", "DATE_UNITS", "Provide any one of the following value week, week_sunday, week_monday, month, quarter or year" });
            }
            vc_dateSubIn.addElement(vector.get(1));
            final SelectColumn sc_value = new SelectColumn();
            final Vector vc_value = new Vector();
            vc_value.addElement(this.units(to_sqs, from_sqs, "dayofyear"));
            vc_value.add("-");
            vc_value.add("1");
            sc_value.setOpenBrace("(");
            sc_value.setCloseBrace(")");
            sc_value.setColumnExpression(vc_value);
            vc_interval.addElement(sc_value);
            vc_interval.add(" DAY");
            sc_interval.setColumnExpression(vc_interval);
        }
        vc_dateSubIn.addElement(sc_interval);
        fnCl_dateSub.setFunctionArguments(vc_dateSubIn);
        vc_dateSubOut.addElement(fnCl_dateSub);
        sc_dateSub.setColumnExpression(vc_dateSubOut);
        arguments.addElement(sc_dateSub);
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
