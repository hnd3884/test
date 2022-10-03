package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class months_between extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTHS_BETWEEN");
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
        this.functionName.setColumnName("DATEDIFF");
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
        if (arguments.size() == 2) {
            final Vector newArguments = new Vector();
            newArguments.add("month");
            newArguments.add(arguments.get(1));
            newArguments.add(arguments.get(0));
            this.setFunctionArguments(newArguments);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTHS_BETWEEN");
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
        this.functionName.setColumnName("MONTHS_BETWEEN");
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
        this.functionName.setColumnName("MONTHS_BETWEEN");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        final SelectColumn sc_monthsbwt = new SelectColumn();
        final Vector vc_monthsbwt = new Vector();
        final Vector vector1 = new Vector();
        final Vector vector2 = new Vector();
        final Vector vector3 = new Vector();
        final Vector vector4 = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector1.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector3.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector4.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector1.addElement(this.functionArguments.elementAt(i_count));
                vector2.addElement(this.functionArguments.elementAt(i_count));
                vector3.addElement(this.functionArguments.elementAt(i_count));
                vector4.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionArguments.size() < 2) {
            vector1.add(1, "now()");
            vector2.add(1, "now()");
            vector3.add(1, "now()");
            vector4.add(1, "now()");
        }
        final SelectColumn sc_timestampdiff = new SelectColumn();
        final FunctionCalls fn_timestampdiff = new FunctionCalls();
        final TableColumn tb_timestampdiff = new TableColumn();
        tb_timestampdiff.setColumnName("TIMESTAMPDIFF");
        fn_timestampdiff.setFunctionName(tb_timestampdiff);
        final Vector vc_timestampdiffIn = new Vector();
        final Vector vc_timestampdiffOut = new Vector();
        vc_timestampdiffIn.add("MONTH");
        vc_timestampdiffIn.addElement(vector1.get(0));
        vc_timestampdiffIn.addElement(vector1.get(1));
        fn_timestampdiff.setFunctionArguments(vc_timestampdiffIn);
        vc_timestampdiffOut.addElement(fn_timestampdiff);
        sc_timestampdiff.setColumnExpression(vc_timestampdiffOut);
        vc_monthsbwt.addElement(sc_timestampdiff);
        String absoluteValue = "";
        if (this.functionArguments.size() > 2) {
            if (vector1.elementAt(2) instanceof SelectColumn) {
                final SelectColumn sc = vector1.elementAt(2);
                final Vector vc = sc.getColumnExpression();
                if (!(vc.elementAt(0) instanceof String)) {
                    throw new ConvertException("Invalid Argument Value for Function MONTHS_BETWEEN", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "MONTHS_BETWEEN", "ISWHOLE_VALUE" });
                }
                absoluteValue = vc.elementAt(0);
                absoluteValue = absoluteValue.replaceAll("'", "");
                absoluteValue = absoluteValue.trim();
                this.validateIsWholeValue(absoluteValue, "MONTHS_BETWEEN");
            }
        }
        else {
            absoluteValue = "0";
        }
        if (absoluteValue.equalsIgnoreCase("0")) {
            vc_monthsbwt.add("+");
            final SelectColumn sc_if = new SelectColumn();
            final FunctionCalls fn_if = new FunctionCalls();
            final TableColumn tb_if = new TableColumn();
            tb_if.setColumnName("IF");
            fn_if.setFunctionName(tb_if);
            final Vector vc_ifIn = new Vector();
            final Vector vc_ifOut = new Vector();
            final SelectColumn sc_wi = new SelectColumn();
            final Vector vc_wi = new Vector();
            final WhereItem wi_if = new WhereItem();
            final WhereColumn if_left = new WhereColumn();
            final Vector vc_if_left = new Vector();
            final WhereColumn if_right = new WhereColumn();
            final Vector vc_if_right = new Vector();
            vc_if_left.addElement(this.dayofmonth(vector2, 1));
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator(">=");
            vc_if_right.addElement(this.dayofmonth(vector2, 0));
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            vc_wi.addElement(wi_if);
            sc_wi.setColumnExpression(vc_wi);
            final SelectColumn sc_tStmt = new SelectColumn();
            final Vector vc_tStmt = new Vector();
            final SelectColumn sc_tQuo = new SelectColumn();
            final Vector vc_tQuo = new Vector();
            vc_tQuo.addElement(this.dayofmonth(vector3, 1));
            vc_tQuo.add("-");
            vc_tQuo.addElement(this.dayofmonth(vector3, 0));
            sc_tQuo.setOpenBrace("(");
            sc_tQuo.setColumnExpression(vc_tQuo);
            sc_tQuo.setCloseBrace(")");
            vc_tStmt.addElement(sc_tQuo);
            vc_tStmt.add("/");
            vc_tStmt.add("31");
            sc_tStmt.setColumnExpression(vc_tStmt);
            final SelectColumn sc_fStmt = new SelectColumn();
            final Vector vc_fStmt = new Vector();
            final SelectColumn sc_fQuo = new SelectColumn();
            final Vector vc_fQuo = new Vector();
            vc_fQuo.add("31");
            vc_fQuo.add("-");
            vc_fQuo.addElement(this.dayofmonth(vector4, 0));
            vc_fQuo.add("+");
            vc_fQuo.addElement(this.dayofmonth(vector4, 1));
            sc_fQuo.setOpenBrace("(");
            sc_fQuo.setColumnExpression(vc_fQuo);
            sc_fQuo.setCloseBrace(")");
            vc_fStmt.addElement(sc_fQuo);
            vc_fStmt.add("/");
            vc_fStmt.add("31");
            sc_fStmt.setColumnExpression(vc_fStmt);
            vc_ifIn.addElement(sc_wi);
            vc_ifIn.addElement(sc_tStmt);
            vc_ifIn.addElement(sc_fStmt);
            fn_if.setFunctionArguments(vc_ifIn);
            vc_ifOut.addElement(fn_if);
            sc_if.setColumnExpression(vc_ifOut);
            vc_monthsbwt.addElement(sc_if);
        }
        sc_monthsbwt.setColumnExpression(vc_monthsbwt);
        arguments.addElement(sc_monthsbwt);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTHS_BETWEEN");
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
        this.functionName.setColumnName("MONTHS_BETWEEN");
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
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("MONTHS_BETWEEN");
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
        this.functionName.setColumnName("MONTHS_BETWEEN");
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
        this.functionName.setColumnName("MONTHS_BETWEEN");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    public SelectColumn dayofmonth(final Vector vector, final int sded) {
        final SelectColumn sc_dayofmonth = new SelectColumn();
        final FunctionCalls fn_dayofmonth = new FunctionCalls();
        final TableColumn tb_dayofmonth = new TableColumn();
        tb_dayofmonth.setColumnName("DAYOFMONTH");
        fn_dayofmonth.setFunctionName(tb_dayofmonth);
        final Vector vc_dayofmonthIn = new Vector();
        final Vector vc_dayofmonthOut = new Vector();
        vc_dayofmonthIn.addElement(vector.get(sded));
        fn_dayofmonth.setFunctionArguments(vc_dayofmonthIn);
        vc_dayofmonthOut.addElement(fn_dayofmonth);
        sc_dayofmonth.setColumnExpression(vc_dayofmonthOut);
        return sc_dayofmonth;
    }
}
