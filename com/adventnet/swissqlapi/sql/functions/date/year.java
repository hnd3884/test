package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class year extends FunctionCalls
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
        this.setTrailingString("YEAR");
        this.setFromInTrim("FROM");
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("YEAR");
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
        this.functionName.setColumnName("YEAR");
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
        this.functionName.setColumnName("YEAR");
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
        if (!canUseUDFFunction && this.functionArguments.size() == 1 && this.functionArguments.elementAt(0) instanceof SelectColumn && this.functionArguments.elementAt(0).getColumnExpression().size() == 1 && this.functionArguments.elementAt(0).getColumnExpression().get(0) instanceof String) {
            qry = "cast(extract (year from cast( " + this.handleStringLiteralForDateTime(this.functionArguments.elementAt(0).getColumnExpression().get(0).toString(), from_sqs) + " as timestamp)) as int)";
        }
        else {
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    if (i_count == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                    }
                    arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            qry = "cast(extract (year from  " + arguments.get(0) + " ) as int) ";
            if (canUseUDFFunction) {
                qry = "YEAR(" + arguments.get(0).toString() + ")";
            }
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionArguments.size() == 1 || arguments.get(1).toString().equalsIgnoreCase("1")) {
            this.functionName.setColumnName("YEAR");
        }
        else if (this.functionArguments.size() == 2 && arguments.elementAt(1) instanceof SelectColumn) {
            final SelectColumn sc_FiscalStartMonth = arguments.elementAt(1);
            final Vector vc_FiscalStartMonth = sc_FiscalStartMonth.getColumnExpression();
            if (!(vc_FiscalStartMonth.elementAt(0) instanceof String)) {
                throw new ConvertException("Invalid Argument Value for Function YEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "YEAR", "FISCAL_START_MONTH" });
            }
            String fiscalStartMonth_str = vc_FiscalStartMonth.elementAt(0);
            if (fiscalStartMonth_str.equalsIgnoreCase("null")) {
                fiscalStartMonth_str = "1";
            }
            fiscalStartMonth_str = fiscalStartMonth_str.replaceAll("'", "");
            this.validateFiscalStartMonth(fiscalStartMonth_str, this.functionName.getColumnName().toUpperCase());
            this.functionName.setColumnName("ZR_FYEARDT");
            final SelectColumn sc_addMonth = new SelectColumn();
            final Vector vc_addMonth = new Vector();
            vc_addMonth.addElement("12 -");
            vc_addMonth.addElement(arguments.get(1));
            vc_addMonth.addElement("+ 1");
            sc_addMonth.setOpenBrace("(");
            sc_addMonth.setCloseBrace(")");
            sc_addMonth.setColumnExpression(vc_addMonth);
            arguments.setElementAt(sc_addMonth, 1);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("YEAR");
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
        this.functionName.setColumnName("YEAR");
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
            arguments.add("'YYYY'");
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXTRACT");
        this.setTrailingString("YEAR");
        this.setFromInTrim("FROM");
        final Vector arguments = new Vector();
        if (this.functionArguments.size() == 1) {
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn selCol = this.functionArguments.get(0).toNetezzaSelect(to_sqs, from_sqs);
                final TableColumn tableCol = new TableColumn();
                tableCol.setColumnName("DATE");
                selCol.getColumnExpression().insertElementAt(tableCol, 0);
                arguments.add(selCol);
            }
            else {
                arguments.add(this.functionArguments.get(0));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("YEAR");
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
        this.functionName.setColumnName("YEAR");
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
