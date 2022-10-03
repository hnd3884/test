package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class month extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        this.functionName.setColumnName("EXTRACT");
        this.setTrailingString("MONTH");
        this.setFromInTrim("FROM");
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTH");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTH");
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
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTH");
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
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String qry = "";
        final Vector arguments = new Vector();
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        if (this.functionArguments.size() == 1 && this.functionArguments.elementAt(0) instanceof SelectColumn && this.functionArguments.elementAt(0).getColumnExpression().size() == 1 && this.functionArguments.elementAt(0).getColumnExpression().get(0) instanceof String) {
            qry = " timestamp ";
        }
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    this.handleStringLiteralForDateTime(from_sqs, i_count, false);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        qry = " cast(EXTRACT(MONTH FROM " + qry + arguments.get(0) + ") as int)";
        if (canUseUDFFunction) {
            qry = "MONTH(" + arguments.get(0).toString() + ")";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName().toUpperCase();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionArguments.size() == 1) {
            this.functionName.setColumnName("MONTH");
            this.setFunctionArguments(arguments);
        }
        else {
            String fiscalStartMonth = "";
            if (arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc_FiscalStartMonth = arguments.elementAt(1);
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
            if (fiscalStartMonth.equalsIgnoreCase("1")) {
                this.functionName.setColumnName("MONTH");
                final Vector Args = new Vector();
                Args.addElement(arguments.get(0));
                this.setFunctionArguments(Args);
            }
            else {
                this.functionName.setColumnName("");
                final Vector fiscalArguments = new Vector();
                final SelectColumn sc_fiscalMonth = new SelectColumn();
                final Vector vc_fiscalMonth = new Vector();
                final SelectColumn sc_fiscalMonthAddend = new SelectColumn();
                final Vector vc_fiscalMonthAddend = new Vector();
                final SelectColumn sc_fiscalMonthDividend = new SelectColumn();
                final Vector vc_fiscalMonthDividend = new Vector();
                final SelectColumn sc_month = new SelectColumn();
                final FunctionCalls fn_month = new FunctionCalls();
                final TableColumn tb_month = new TableColumn();
                tb_month.setColumnName("MONTH");
                fn_month.setFunctionName(tb_month);
                final Vector vc_monthIn = new Vector();
                final Vector vc_monthOut = new Vector();
                vc_monthIn.addElement(arguments.get(0));
                fn_month.setFunctionArguments(vc_monthIn);
                vc_monthOut.addElement(fn_month);
                sc_month.setOpenBrace("(");
                sc_month.setCloseBrace(")");
                sc_month.setColumnExpression(vc_monthOut);
                vc_fiscalMonthDividend.addElement(sc_month);
                vc_fiscalMonthDividend.addElement("+");
                vc_fiscalMonthDividend.addElement("12");
                vc_fiscalMonthDividend.addElement("-");
                vc_fiscalMonthDividend.addElement("(");
                vc_fiscalMonthDividend.addElement(fiscalStartMonth);
                vc_fiscalMonthDividend.addElement(")");
                sc_fiscalMonthDividend.setOpenBrace("(");
                sc_fiscalMonthDividend.setCloseBrace(")");
                sc_fiscalMonthDividend.setColumnExpression(vc_fiscalMonthDividend);
                vc_fiscalMonthAddend.addElement(sc_fiscalMonthDividend);
                vc_fiscalMonthAddend.addElement("%");
                vc_fiscalMonthAddend.addElement("12");
                sc_fiscalMonthAddend.setOpenBrace("(");
                sc_fiscalMonthAddend.setCloseBrace(")");
                sc_fiscalMonthAddend.setColumnExpression(vc_fiscalMonthAddend);
                vc_fiscalMonth.addElement(sc_fiscalMonthAddend);
                vc_fiscalMonth.addElement("+");
                vc_fiscalMonth.addElement("1");
                sc_fiscalMonth.setColumnExpression(vc_fiscalMonth);
                fiscalArguments.addElement(sc_fiscalMonth);
                this.setFunctionArguments(fiscalArguments);
            }
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTH");
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
        this.functionName.setColumnName("MONTH");
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
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TO_CHAR");
        final Vector arguments = new Vector();
        if (this.functionArguments.size() == 1) {
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                arguments.add(this.functionArguments.get(0).toTimesTenSelect(to_sqs, from_sqs));
            }
            else {
                arguments.add(this.functionArguments.get(0));
            }
            arguments.add("'MM'");
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTH");
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
        this.functionName.setColumnName("MONTH");
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
        this.functionName.setColumnName("MONTH");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
}
