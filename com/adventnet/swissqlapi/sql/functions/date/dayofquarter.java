package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class dayofquarter extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        final Vector vector1 = new Vector();
        final Vector vector2 = new Vector();
        final Vector vector3 = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector1.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector3.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector1.addElement(this.functionArguments.elementAt(i_count));
                vector2.addElement(this.functionArguments.elementAt(i_count));
                vector3.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final SelectColumn sc_DayOfQuarter = new SelectColumn();
        final Vector vc_DayOfQuarter = new Vector();
        final SelectColumn sc_DateDiff = new SelectColumn();
        final FunctionCalls fn_DateDiff = new FunctionCalls();
        final TableColumn tb_DateDiff = new TableColumn();
        tb_DateDiff.setColumnName("DATEDIFF");
        fn_DateDiff.setFunctionName(tb_DateDiff);
        final Vector vc_DateDiffIn = new Vector();
        final Vector vc_DateDiffOut = new Vector();
        vc_DateDiffIn.addElement(vector1.get(0));
        final SelectColumn sc_Str_To_Date = new SelectColumn();
        final FunctionCalls fn_Str_To_Date = new FunctionCalls();
        final TableColumn tb_Str_To_Date = new TableColumn();
        tb_Str_To_Date.setColumnName("STR_TO_DATE");
        fn_Str_To_Date.setFunctionName(tb_Str_To_Date);
        final Vector vc_Str_To_DateIn = new Vector();
        final Vector vc_Str_To_DateOut = new Vector();
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
        vc_yearIn.addElement(vector2.get(0));
        fn_year.setFunctionArguments(vc_yearIn);
        vc_yearOut.addElement(fn_year);
        sc_year.setColumnExpression(vc_yearOut);
        vc_concatwsIn.addElement(sc_year);
        final SelectColumn sc_value = new SelectColumn();
        final Vector vc_value = new Vector();
        final SelectColumn sc_quarter = new SelectColumn();
        final FunctionCalls fn_quarter = new FunctionCalls();
        final TableColumn tb_quarter = new TableColumn();
        tb_quarter.setColumnName("QUARTER");
        fn_quarter.setFunctionName(tb_quarter);
        final Vector vc_quarterIn = new Vector();
        final Vector vc_quarterOut = new Vector();
        vc_quarterIn.addElement(vector3.get(0));
        fn_quarter.setFunctionArguments(vc_quarterIn);
        vc_quarterOut.addElement(fn_quarter);
        sc_quarter.setColumnExpression(vc_quarterOut);
        vc_value.addElement("(");
        vc_value.addElement(sc_quarter);
        vc_value.add("*");
        vc_value.add("3");
        vc_value.addElement(")");
        vc_value.add("-");
        vc_value.add("2");
        sc_value.setOpenBrace("(");
        sc_value.setCloseBrace(")");
        sc_value.setColumnExpression(vc_value);
        vc_concatwsIn.addElement(sc_value);
        vc_concatwsIn.add("1");
        fnCall_concatws.setFunctionArguments(vc_concatwsIn);
        vc_concatwsOut.addElement(fnCall_concatws);
        sc_concatws.setColumnExpression(vc_concatwsOut);
        vc_Str_To_DateIn.addElement(sc_concatws);
        vc_Str_To_DateIn.addElement("'%Y-%m-%d'");
        fn_Str_To_Date.setFunctionArguments(vc_Str_To_DateIn);
        vc_Str_To_DateOut.addElement(fn_Str_To_Date);
        sc_Str_To_Date.setColumnExpression(vc_Str_To_DateOut);
        vc_DateDiffIn.addElement(sc_Str_To_Date);
        fn_DateDiff.setFunctionArguments(vc_DateDiffIn);
        vc_DateDiffOut.addElement(fn_DateDiff);
        sc_DateDiff.setColumnExpression(vc_DateDiffOut);
        vc_DayOfQuarter.addElement(sc_DateDiff);
        vc_DayOfQuarter.addElement("+");
        vc_DayOfQuarter.addElement("1");
        sc_DayOfQuarter.setColumnExpression(vc_DayOfQuarter);
        arguments.addElement(sc_DayOfQuarter);
        this.setFunctionArguments(arguments);
    }
}
