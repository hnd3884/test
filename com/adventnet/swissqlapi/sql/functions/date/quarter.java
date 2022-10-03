package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class quarter extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName().toUpperCase();
        String fiscalStartMonth = "";
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
        if (vector.size() == 2 && vector.elementAt(1) instanceof SelectColumn) {
            final SelectColumn sc_FiscalStartMonth = vector.elementAt(1);
            final Vector vc_FiscalStartMonth = sc_FiscalStartMonth.getColumnExpression();
            if (!(vc_FiscalStartMonth.elementAt(0) instanceof String)) {
                throw new ConvertException("Invalid Argument Value for Function " + fnStr, "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr, "FISCAL_START_MONTH" });
            }
            fiscalStartMonth = vc_FiscalStartMonth.elementAt(0);
            if (fiscalStartMonth.equalsIgnoreCase("null")) {
                fiscalStartMonth = "1";
            }
            fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
            this.validateFiscalStartMonth(fiscalStartMonth, this.functionName.getColumnName().toUpperCase());
        }
        if (fnStr.equalsIgnoreCase("QUARTERNAME")) {
            this.functionName.setColumnName("CONCAT");
            arguments.addElement("'Q'");
            final SelectColumn sc_Quarter = new SelectColumn();
            final FunctionCalls fn_Quarter = new FunctionCalls();
            final TableColumn tb_Quarter = new TableColumn();
            final Vector vc_QuarterIn = new Vector();
            vc_QuarterIn.addElement(vector.get(0));
            if (this.functionArguments.size() == 1) {
                tb_Quarter.setColumnName("QUARTER");
            }
            else if (this.functionArguments.size() == 2) {
                if (fiscalStartMonth.equalsIgnoreCase("1")) {
                    tb_Quarter.setColumnName("QUARTER");
                }
                else {
                    tb_Quarter.setColumnName("ZR_FQUARTERDT");
                    vc_QuarterIn.addElement(this.fiscalQuarterNumber(vector));
                }
            }
            fn_Quarter.setFunctionName(tb_Quarter);
            final Vector vc_QuarterOut = new Vector();
            fn_Quarter.setFunctionArguments(vc_QuarterIn);
            vc_QuarterOut.addElement(fn_Quarter);
            sc_Quarter.setColumnExpression(vc_QuarterOut);
            arguments.addElement(sc_Quarter);
        }
        else if (fnStr.equalsIgnoreCase("QUARTERNUM") || fnStr.equalsIgnoreCase("QUARTER")) {
            arguments.addElement(vector.get(0));
            if (this.functionArguments.size() == 1) {
                this.functionName.setColumnName("QUARTER");
            }
            else if (this.functionArguments.size() == 2) {
                if (fiscalStartMonth.equalsIgnoreCase("1")) {
                    this.functionName.setColumnName("QUARTER");
                }
                else {
                    this.functionName.setColumnName("ZR_FQUARTERDT");
                    arguments.addElement(this.fiscalQuarterNumber(vector));
                }
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    public SelectColumn fiscalQuarterNumber(final Vector vector) {
        final SelectColumn sc_addMonth = new SelectColumn();
        final Vector vc_addMonth = new Vector();
        vc_addMonth.addElement("12");
        vc_addMonth.addElement("-");
        vc_addMonth.addElement(vector.get(1));
        vc_addMonth.addElement("+");
        vc_addMonth.addElement("1");
        sc_addMonth.setOpenBrace("(");
        sc_addMonth.setCloseBrace(")");
        sc_addMonth.setColumnExpression(vc_addMonth);
        return sc_addMonth;
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector vector = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String dateString = vector.get(0).toString();
        dateString = StringFunctions.handleLiteralStringDateForOracle(dateString);
        final String query = " TO_NUMBER(TO_CHAR(" + dateString + ", 'Q'))";
        this.functionName.setColumnName(query);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
