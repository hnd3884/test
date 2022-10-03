package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class AbsoluteQuarter extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int argLength = this.functionArguments.size(), i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0 && this.functionArguments.elementAt(i_count).getColumnExpression().size() == 1 && this.functionArguments.elementAt(i_count).getColumnExpression().get(0) instanceof String) {
                    String dateString = this.functionArguments.elementAt(i_count).getColumnExpression().get(0).toString();
                    dateString = "CAST(" + dateString + " AS TIMESTAMP)";
                    arguments.addElement(dateString);
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Object date = arguments.get(0);
        final SelectColumn absQSC = new SelectColumn();
        final Vector absQSCColExp = new Vector();
        absQSCColExp.addElement("(");
        absQSCColExp.addElement("(");
        absQSCColExp.addElement("'Q'");
        absQSCColExp.addElement("||");
        final SelectColumn dateSC = new SelectColumn();
        final Vector dateScVector = new Vector();
        dateScVector.addElement(date);
        dateSC.setColumnExpression(dateScVector);
        final SelectColumn castFSC = new SelectColumn();
        final Vector castFSCColExp = new Vector();
        final FunctionCalls castFC = new FunctionCalls();
        castFC.getFunctionName().setColumnName("cast");
        final Vector castFCArgs = new Vector();
        final SelectColumn extractFSC = new SelectColumn();
        final Vector extractFSCColExp = new Vector();
        final FunctionCalls extractFC = new FunctionCalls();
        extractFC.getFunctionName().setColumnName("EXTRACT");
        extractFC.setFromInTrim("FROM");
        extractFC.setTrailingString("QUARTER");
        final Vector extractFCArgs = new Vector();
        extractFCArgs.addElement(dateSC);
        extractFC.setFunctionArguments(extractFCArgs);
        extractFSCColExp.addElement(extractFC);
        extractFSC.setColumnExpression(extractFSCColExp);
        castFC.setAsDatatype("as");
        castFCArgs.addElement(extractFSC);
        castFCArgs.addElement("int");
        castFC.setFunctionArguments(castFCArgs);
        castFSCColExp.addElement(castFC);
        castFSC.setColumnExpression(castFSCColExp);
        absQSCColExp.addElement(castFSC);
        absQSCColExp.addElement(")");
        absQSCColExp.addElement("||");
        absQSCColExp.addElement("','");
        absQSCColExp.addElement(")");
        absQSCColExp.addElement("||");
        final SelectColumn cast2FSC = new SelectColumn();
        final Vector cast2FSCColExp = new Vector();
        final FunctionCalls cast2FC = new FunctionCalls();
        cast2FC.getFunctionName().setColumnName("cast");
        final Vector cast2FCArgs = new Vector();
        final SelectColumn extract2FSC = new SelectColumn();
        final Vector extract2FSCColExp = new Vector();
        final FunctionCalls extract2FC = new FunctionCalls();
        extract2FC.getFunctionName().setColumnName("extract");
        extract2FC.setFromInTrim("from");
        extract2FC.setTrailingString("year");
        final Vector extract2FCArgs = new Vector();
        extract2FCArgs.addElement(dateSC);
        extract2FC.setFunctionArguments(extract2FCArgs);
        extract2FSCColExp.addElement(extract2FC);
        extract2FSC.setColumnExpression(extract2FSCColExp);
        cast2FC.setAsDatatype("as");
        cast2FCArgs.addElement(extract2FSC);
        cast2FCArgs.addElement("int");
        cast2FC.setFunctionArguments(cast2FCArgs);
        cast2FSCColExp.addElement(cast2FC);
        cast2FSC.setColumnExpression(cast2FSCColExp);
        absQSCColExp.addElement(cast2FSC);
        absQSC.setColumnExpression(absQSCColExp);
        this.functionName.setColumnName("");
        final Vector fnArgs = new Vector();
        fnArgs.addElement(absQSC);
        this.setFunctionArguments(fnArgs);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final Vector vector1 = new Vector();
        final Vector vector2 = new Vector();
        for (int i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector1.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector1.addElement(this.functionArguments.elementAt(i_count));
                vector2.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final SelectColumn concatFSC = new SelectColumn();
        final Vector concatFSCColExp = new Vector();
        final FunctionCalls concatFC = new FunctionCalls();
        concatFC.getFunctionName().setColumnName("concat");
        final Vector concatFCArgs = new Vector();
        concatFCArgs.addElement("'Q'");
        if (argLength > 1) {
            String fiscal_start_month = "";
            if (vector1.elementAt(1) instanceof SelectColumn) {
                final SelectColumn fiscalSC = vector1.elementAt(1);
                final Vector fiscalVC = fiscalSC.getColumnExpression();
                if (!(fiscalVC.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function ABSQUARTER", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "ABSQUARTER", "FISCAL_START_MONTH" });
                }
                fiscal_start_month = fiscalVC.elementAt(0);
                if (fiscal_start_month.equalsIgnoreCase("null")) {
                    fiscal_start_month = "1";
                }
                fiscal_start_month = fiscal_start_month.replaceAll("'", "");
                this.validateFiscalStartMonth(fiscal_start_month, this.functionName.getColumnName().toUpperCase());
                int addmonth = Integer.parseInt(fiscal_start_month);
                addmonth = 12 - addmonth + 1;
                final String addFiscalMonth = Integer.toString(addmonth);
                final SelectColumn zr_QuarterFSC = new SelectColumn();
                final Vector zr_QuarterFSCColExp = new Vector();
                final FunctionCalls zr_QuarterFC = new FunctionCalls();
                zr_QuarterFC.getFunctionName().setColumnName("ZR_fQuarterDt");
                final Vector zr_QuarterFCArgs = new Vector();
                zr_QuarterFCArgs.addElement(vector1.get(0));
                zr_QuarterFCArgs.addElement(addFiscalMonth);
                zr_QuarterFC.setFunctionArguments(zr_QuarterFCArgs);
                zr_QuarterFSCColExp.addElement(zr_QuarterFC);
                zr_QuarterFSC.setColumnExpression(zr_QuarterFSCColExp);
                final SelectColumn zr_YearFSC = new SelectColumn();
                final Vector zr_YearFSCColExp = new Vector();
                final FunctionCalls zr_YearFC = new FunctionCalls();
                zr_YearFC.getFunctionName().setColumnName("ZR_fyearDt");
                final Vector zr_YearFCArgs = new Vector();
                zr_YearFCArgs.addElement(vector2.get(0));
                zr_YearFCArgs.addElement(addFiscalMonth);
                zr_YearFC.setFunctionArguments(zr_YearFCArgs);
                zr_YearFSCColExp.addElement(zr_YearFC);
                zr_YearFSC.setColumnExpression(zr_YearFSCColExp);
                concatFCArgs.addElement(zr_QuarterFSC);
                concatFCArgs.addElement("', FY '");
                concatFCArgs.addElement(zr_YearFSC);
            }
        }
        else {
            final SelectColumn yearFSC = new SelectColumn();
            final Vector yearFSCColExp = new Vector();
            final FunctionCalls yearFC = new FunctionCalls();
            yearFC.getFunctionName().setColumnName("year");
            final Vector yearFCArgs = new Vector();
            yearFCArgs.addElement(vector1.get(0));
            yearFC.setFunctionArguments(yearFCArgs);
            yearFSCColExp.addElement(yearFC);
            yearFSC.setColumnExpression(yearFSCColExp);
            final SelectColumn quarterFSC = new SelectColumn();
            final Vector quarterFSCColExp = new Vector();
            final FunctionCalls quarterFC = new FunctionCalls();
            quarterFC.getFunctionName().setColumnName("quarter");
            final Vector quarterFCArgs = new Vector();
            quarterFCArgs.addElement(vector2.get(0));
            quarterFC.setFunctionArguments(quarterFCArgs);
            quarterFSCColExp.addElement(quarterFC);
            quarterFSC.setColumnExpression(quarterFSCColExp);
            concatFCArgs.addElement(quarterFSC);
            concatFCArgs.addElement("', '");
            concatFCArgs.addElement(yearFSC);
        }
        concatFC.setFunctionArguments(concatFCArgs);
        concatFSCColExp.addElement(concatFC);
        concatFSC.setColumnExpression(concatFSCColExp);
        this.functionName.setColumnName("");
        final Vector fnArgs = new Vector();
        fnArgs.addElement(concatFSC);
        this.setFunctionArguments(fnArgs);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String date = arguments.get(0).toString();
        arguments = new Vector();
        arguments.addElement("'Q'");
        arguments.addElement("quarter(" + date + ")");
        arguments.addElement("', '");
        arguments.addElement("YEAR(" + date + ")");
        this.functionName.setColumnName("CONCAT");
        this.setFunctionArguments(arguments);
    }
}
