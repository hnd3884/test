package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class last_day extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LAST_DAY");
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
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LAST_DAY");
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
        if (arguments.size() == 1) {
            this.lastDayWithOneArgForTsqls(arguments.get(0), true);
        }
    }
    
    private void lastDayWithOneArgForTsqls(final Object obj, final boolean isSQLServer) {
        this.functionName.setColumnName("DATEADD");
        final SelectColumn sc = new SelectColumn();
        final Vector scArguments = new Vector();
        final SelectColumn scForSecondArg = new SelectColumn();
        final Vector secondArgColExp = new Vector();
        final SelectColumn scForThirdArg = new SelectColumn();
        final Vector thirdArgForColExp = new Vector();
        final TableColumn tc = new TableColumn();
        if (isSQLServer) {
            tc.setColumnName("D");
        }
        else {
            tc.setColumnName("DD");
        }
        scArguments.add(tc);
        sc.setColumnExpression(scArguments);
        final Vector newFunctionArguments = new Vector();
        final FunctionCalls subFC = new FunctionCalls();
        final Vector subFCArgsForDayFn = new Vector();
        final SelectColumn scForDayFn = new SelectColumn();
        final TableColumn newFunctionName = new TableColumn();
        newFunctionName.setColumnName("-DAY");
        subFC.setFunctionName(newFunctionName);
        final Vector scArgumentsForDayFn = new Vector();
        final FunctionCalls subFCForDateAddFn = new FunctionCalls();
        final TableColumn newFunctionNameForDateAddFn = new TableColumn();
        newFunctionNameForDateAddFn.setColumnName("DATEADD");
        subFCForDateAddFn.setFunctionName(newFunctionNameForDateAddFn);
        final Vector subFCArgForDateAddFn = new Vector();
        final SelectColumn scForDateAddFn = new SelectColumn();
        final Vector scArgumentsForDateAddFn = new Vector();
        final TableColumn tcForDateAddFn = new TableColumn();
        if (isSQLServer) {
            tcForDateAddFn.setColumnName("M");
        }
        else {
            tcForDateAddFn.setColumnName("MM");
        }
        scArgumentsForDateAddFn.add(tcForDateAddFn);
        scForDateAddFn.setColumnExpression(scArgumentsForDateAddFn);
        subFCArgForDateAddFn.add(scForDateAddFn);
        subFCArgForDateAddFn.add("1");
        subFCArgForDateAddFn.add(obj);
        subFCForDateAddFn.setFunctionArguments(subFCArgForDateAddFn);
        scArgumentsForDayFn.add(subFCForDateAddFn);
        scForDayFn.setColumnExpression(scArgumentsForDayFn);
        subFCArgsForDayFn.add(scForDayFn);
        subFC.setFunctionArguments(subFCArgsForDayFn);
        newFunctionArguments.add(sc);
        secondArgColExp.add(subFC);
        scForSecondArg.setColumnExpression(secondArgColExp);
        newFunctionArguments.add(scForSecondArg);
        thirdArgForColExp.add(subFCForDateAddFn);
        scForThirdArg.setColumnExpression(thirdArgForColExp);
        newFunctionArguments.add(scForThirdArg);
        this.setFunctionArguments(newFunctionArguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LAST_DAY");
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
        if (arguments.size() == 1) {
            this.lastDayWithOneArgForTsqls(arguments.get(0), false);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String arg = null;
        String colName = null;
        if (this.functionArguments.size() == 1) {
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                arg = this.functionArguments.elementAt(0).toDB2Select(to_sqs, from_sqs).toString();
            }
            else if (this.functionArguments.elementAt(0) instanceof FunctionCalls) {
                arg = this.functionArguments.elementAt(0).toDB2Select(to_sqs, from_sqs).toString();
            }
            else {
                arg = this.functionArguments.elementAt(0).toString();
            }
            colName = arg + " + 1 MONTH - DAY(" + arg + " + 1 MONTH) DAY";
            this.functionName.setColumnName(colName);
            this.setOpenBracesForFunctionNameRequired(false);
            final Vector arguments = new Vector();
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
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
        if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            this.functionName.setColumnName("LAST_DAY");
            this.setFunctionArguments(arguments);
        }
        else {
            String qry = " date(date_trunc('MONTH', date(" + arguments.get(0) + ")) + INTERVAL '1 MONTH - 1 day') ";
            if (canUseUDFFunction) {
                qry = "LAST_DAY(" + arguments.get(0).toString() + ")";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LAST_DAY");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LAST_DAY");
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
        this.functionName.setColumnName("LAST_DAY");
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
        throw new ConvertException("\nThe function LAST_DAY is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            final SelectColumn selCol = this.functionArguments.elementAt(0).toNetezzaSelect(to_sqs, from_sqs);
            final String target = "date_trunc('month'," + selCol.toString() + ") + interval '1 month' - interval '1 day' as date";
            arguments.add(target);
        }
        else {
            final String target2 = "date_trunc('month'," + this.functionArguments.elementAt(0).toString() + ") + interval '1 month' - interval '1 day' as date";
            arguments.add(target2);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LAST_DAY");
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
        this.functionName.setColumnName("LAST_DAY");
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
