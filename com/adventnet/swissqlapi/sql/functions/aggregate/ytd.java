package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class ytd extends FunctionCalls
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
        String aggFunName = null;
        SelectColumn dispCol = null;
        if (vector.elementAt(0) instanceof SelectColumn) {
            final SelectColumn typeofAggFun = vector.elementAt(0);
            final Vector vc_typeofAggFun = typeofAggFun.getColumnExpression();
            if (vc_typeofAggFun.elementAt(0) instanceof FunctionCalls) {
                final FunctionCalls funCallofAggFun = vc_typeofAggFun.elementAt(0);
                final TableColumn tblColofAggFun = funCallofAggFun.getFunctionName();
                aggFunName = tblColofAggFun.getColumnName();
                final Vector vcofAggFun = funCallofAggFun.getFunctionArguments();
                dispCol = vcofAggFun.elementAt(0);
            }
        }
        if (aggFunName != null) {
            this.functionName.setColumnName(aggFunName);
            final String yrSt = "year";
            final String mtSt = "month";
            final String dySt = "day";
            final String AndOperator = "AND";
            final String OrOperator = "OR";
            if (this.functionArguments.size() > 2) {
                String fiscalStartMonth = "null";
                if (this.functionArguments.size() > 2 && vector.elementAt(2) instanceof SelectColumn) {
                    final SelectColumn sc = vector.elementAt(2);
                    final Vector vc = sc.getColumnExpression();
                    if (!(vc.elementAt(0) instanceof String)) {
                        throw new ConvertException("Invalid Argument Value for Function YTD", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "YTD", "FISCAL_START_MONTH" });
                    }
                    fiscalStartMonth = vc.elementAt(0);
                }
                if (fiscalStartMonth.equalsIgnoreCase("null")) {
                    fiscalStartMonth = "1";
                }
                fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
                this.validateFiscalStartMonth(fiscalStartMonth, "YTD");
                final SelectColumn sc_if = new SelectColumn();
                final FunctionCalls fn_if = new FunctionCalls();
                final TableColumn tb_if = new TableColumn();
                tb_if.setColumnName("IF");
                fn_if.setFunctionName(tb_if);
                final Vector vc_ifIn = new Vector();
                final Vector vc_ifOut = new Vector();
                final SelectColumn sc_ifCon = new SelectColumn();
                final Vector vc_ifCon = new Vector();
                final WhereItem whIt_if = new WhereItem();
                final WhereColumn whColLeftExp = new WhereColumn();
                final Vector vc_whColLeftExp = new Vector();
                final WhereColumn whColRightExp = new WhereColumn();
                final Vector vc_whColRightExp = new Vector();
                final SelectColumn sc_ifFiscal = new SelectColumn();
                final FunctionCalls fn_ifFiscal = new FunctionCalls();
                final TableColumn tb_ifFiscal = new TableColumn();
                tb_ifFiscal.setColumnName("IF");
                fn_ifFiscal.setFunctionName(tb_ifFiscal);
                final Vector vc_ifFiscalIn = new Vector();
                final Vector vc_ifFiscalOut = new Vector();
                final SelectColumn sc_ifFiscalCon = new SelectColumn();
                final Vector vc_ifFiscalCon = new Vector();
                final WhereItem whIt_ifFiscalCon = new WhereItem();
                final WhereColumn whCol_FiscalConLeftExp = new WhereColumn();
                final Vector vc_whCol_FiscalConLeftExp = new Vector();
                final WhereColumn whCol_FiscalConRightExp = new WhereColumn();
                final Vector vc_whCol_FiscalConRightExp = new Vector();
                vc_whCol_FiscalConLeftExp.addElement(fiscalStartMonth);
                final Vector vc_currMonth = new Vector();
                vc_currMonth.addElement(this.now());
                vc_whCol_FiscalConRightExp.addElement(this.date_fun(vc_currMonth, 0, "month"));
                whCol_FiscalConLeftExp.setColumnExpression(vc_whCol_FiscalConLeftExp);
                whCol_FiscalConRightExp.setColumnExpression(vc_whCol_FiscalConRightExp);
                whIt_ifFiscalCon.setLeftWhereExp(whCol_FiscalConLeftExp);
                whIt_ifFiscalCon.setOperator("<");
                whIt_ifFiscalCon.setRightWhereExp(whCol_FiscalConRightExp);
                vc_ifFiscalCon.addElement(whIt_ifFiscalCon);
                sc_ifFiscalCon.setColumnExpression(vc_ifFiscalCon);
                vc_ifFiscalIn.addElement(sc_ifFiscalCon);
                vc_ifFiscalIn.addElement(this.ytd_FiscalStmt(to_sqs, from_sqs, fiscalStartMonth, vector, AndOperator));
                vc_ifFiscalIn.addElement(this.ytd_FiscalStmt(to_sqs, from_sqs, fiscalStartMonth, vector, OrOperator));
                fn_ifFiscal.setFunctionArguments(vc_ifFiscalIn);
                vc_ifFiscalOut.addElement(fn_ifFiscal);
                sc_ifFiscal.setColumnExpression(vc_ifFiscalOut);
                vc_whColLeftExp.addElement(sc_ifFiscal);
                vc_whColRightExp.addElement("1");
                whColLeftExp.setColumnExpression(vc_whColLeftExp);
                whColRightExp.setColumnExpression(vc_whColRightExp);
                whIt_if.setLeftWhereExp(whColLeftExp);
                whIt_if.setOperator("=");
                whIt_if.setRightWhereExp(whColRightExp);
                vc_ifCon.addElement(whIt_if);
                sc_ifCon.setColumnExpression(vc_ifCon);
                vc_ifIn.addElement(sc_ifCon);
                vc_ifIn.addElement(dispCol);
                vc_ifIn.addElement("null");
                fn_if.setFunctionArguments(vc_ifIn);
                vc_ifOut.addElement(fn_if);
                sc_if.setColumnExpression(vc_ifOut);
                arguments.addElement(sc_if);
            }
            else {
                final SelectColumn sc_if2 = new SelectColumn();
                final FunctionCalls fn_if2 = new FunctionCalls();
                final TableColumn tb_if2 = new TableColumn();
                tb_if2.setColumnName("IF");
                fn_if2.setFunctionName(tb_if2);
                final Vector vc_ifIn2 = new Vector();
                final Vector vc_ifOut2 = new Vector();
                final SelectColumn sc_ifCon2 = new SelectColumn();
                final Vector vc_ifCon2 = new Vector();
                final WhereExpression whExp_if = new WhereExpression();
                final WhereItem whIt_curryearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<=");
                whExp_if.addWhereItem(whIt_curryearCheck);
                final WhereItem whIt_currmonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<=");
                whExp_if.addWhereItem(whIt_currmonthCheck);
                final WhereExpression whExp_dayMonthCheck = new WhereExpression();
                final Vector vc_whItemListDayMonthCheck = new Vector();
                final WhereItem whIt_dayCheck = this.constructWhItem(to_sqs, from_sqs, dySt, "<=");
                final WhereItem whIt_prevMonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<");
                vc_whItemListDayMonthCheck.addElement(whIt_dayCheck);
                vc_whItemListDayMonthCheck.addElement(whIt_prevMonthCheck);
                whExp_dayMonthCheck.setWhereItem(vc_whItemListDayMonthCheck);
                whExp_dayMonthCheck.setOpenBrace("(");
                whExp_dayMonthCheck.setCloseBrace(")");
                final Vector vc_OperatorMonthCheck = new Vector();
                vc_OperatorMonthCheck.addElement(OrOperator);
                whExp_dayMonthCheck.setOperator(vc_OperatorMonthCheck);
                whExp_if.addWhereExpression(whExp_dayMonthCheck);
                final Vector vc_Operator = new Vector();
                vc_Operator.addElement(AndOperator);
                vc_Operator.addElement(AndOperator);
                whExp_if.setOperator(vc_Operator);
                whExp_if.setOpenBrace("(");
                whExp_if.setCloseBrace(")");
                vc_ifCon2.addElement(whExp_if);
                sc_ifCon2.setColumnExpression(vc_ifCon2);
                vc_ifIn2.addElement(sc_ifCon2);
                vc_ifIn2.addElement(dispCol);
                vc_ifIn2.addElement("null");
                fn_if2.setFunctionArguments(vc_ifIn2);
                vc_ifOut2.addElement(fn_if2);
                sc_if2.setColumnExpression(vc_ifOut2);
                arguments.addElement(sc_if2);
            }
            this.setFunctionArguments(arguments);
        }
    }
    
    public WhereItem constructWhItem(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final String type, final String operator) throws ConvertException {
        final Vector vector = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final WhereItem whIt_dtType = new WhereItem();
        final WhereColumn whColLeftYearExp = new WhereColumn();
        final Vector vc_whColLeftYearExp = new Vector();
        final WhereColumn whColRightYearExp = new WhereColumn();
        final Vector vc_whColRightYearExp = new Vector();
        final SelectColumn sc_leftYearExp = this.date_fun(vector, 1, type);
        vc_whColLeftYearExp.addElement(sc_leftYearExp);
        whColLeftYearExp.setColumnExpression(vc_whColLeftYearExp);
        whIt_dtType.setLeftWhereExp(whColLeftYearExp);
        final SelectColumn rightYearExp_Now = this.now();
        final Vector vc_rightYearExp_Now = new Vector();
        vc_rightYearExp_Now.addElement(rightYearExp_Now);
        final SelectColumn sc_rightYearExp = this.date_fun(vc_rightYearExp_Now, 0, type);
        vc_whColRightYearExp.addElement(sc_rightYearExp);
        whColRightYearExp.setColumnExpression(vc_whColRightYearExp);
        whIt_dtType.setRightWhereExp(whColRightYearExp);
        whIt_dtType.setOperator(operator);
        return whIt_dtType;
    }
    
    public SelectColumn ytd_FiscalStmt(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final String fiscalStartMonth, final Vector vector, final String Operator) throws ConvertException {
        final String yrSt = "year";
        final String mtSt = "month";
        final String dySt = "day";
        final String AndOperator = "AND";
        final String OrOperator = "OR";
        final SelectColumn sc_ifFiscalTrueStmt = new SelectColumn();
        final FunctionCalls fn_ifFiscalTrueStmt = new FunctionCalls();
        final TableColumn tb_ifFiscalTrueStmt = new TableColumn();
        tb_ifFiscalTrueStmt.setColumnName("IF");
        fn_ifFiscalTrueStmt.setFunctionName(tb_ifFiscalTrueStmt);
        final Vector vc_ifFiscalTrueStmtIn = new Vector();
        final Vector vc_ifFiscalTrueStmtOut = new Vector();
        final SelectColumn sc_ifFiscalTrueStmtCon = new SelectColumn();
        final Vector vc_ifFiscalTrueStmtCon = new Vector();
        final WhereExpression whExp_ifFiscalTrueStmt = new WhereExpression();
        final WhereItem whIt_curryearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<=");
        final WhereExpression whExp_dayMonthCheck = new WhereExpression();
        final WhereItem whIt_currmonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<=");
        final WhereExpression whExp_dayPrevMonthCheck = new WhereExpression();
        final Vector vc_whItemListdayPrevMonthCheck = new Vector();
        final WhereItem whIt_dayCheck = this.constructWhItem(to_sqs, from_sqs, dySt, "<=");
        final WhereItem whIt_prevMonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<");
        vc_whItemListdayPrevMonthCheck.addElement(whIt_dayCheck);
        vc_whItemListdayPrevMonthCheck.addElement(whIt_prevMonthCheck);
        whExp_dayPrevMonthCheck.setWhereItem(vc_whItemListdayPrevMonthCheck);
        whExp_dayPrevMonthCheck.setOpenBrace("(");
        whExp_dayPrevMonthCheck.setCloseBrace(")");
        final Vector vc_OperatorPrevMonthCheck = new Vector();
        vc_OperatorPrevMonthCheck.addElement(OrOperator);
        whExp_dayPrevMonthCheck.setOperator(vc_OperatorPrevMonthCheck);
        final WhereItem whIt_fiscalMonthCheck = new WhereItem();
        final WhereColumn whCol_fiscalMonthCheckLeftExp = new WhereColumn();
        final Vector vc_whCol_fiscalMonthCheckLeftExp = new Vector();
        final WhereColumn whCol_fiscalMonthCheckRightExp = new WhereColumn();
        final Vector vc_whCol_fiscalMonthCheckRightExp = new Vector();
        vc_whCol_fiscalMonthCheckLeftExp.addElement(this.date_fun(vector, 1, mtSt));
        vc_whCol_fiscalMonthCheckRightExp.addElement(fiscalStartMonth);
        whCol_fiscalMonthCheckLeftExp.setColumnExpression(vc_whCol_fiscalMonthCheckLeftExp);
        whCol_fiscalMonthCheckRightExp.setColumnExpression(vc_whCol_fiscalMonthCheckRightExp);
        whIt_fiscalMonthCheck.setLeftWhereExp(whCol_fiscalMonthCheckLeftExp);
        whIt_fiscalMonthCheck.setRightWhereExp(whCol_fiscalMonthCheckRightExp);
        whIt_fiscalMonthCheck.setOperator(">=");
        whExp_dayMonthCheck.addWhereItem(whIt_currmonthCheck);
        whExp_dayMonthCheck.addWhereExpression(whExp_dayPrevMonthCheck);
        whExp_dayMonthCheck.addWhereItem(whIt_fiscalMonthCheck);
        whExp_dayMonthCheck.setOpenBrace("(");
        whExp_dayMonthCheck.setCloseBrace(")");
        final Vector vc_OperatorMonthCheck = new Vector();
        vc_OperatorMonthCheck.addElement(AndOperator);
        vc_OperatorMonthCheck.addElement(Operator);
        whExp_dayMonthCheck.setOperator(vc_OperatorMonthCheck);
        final Vector vc_Operator = new Vector();
        vc_Operator.addElement(AndOperator);
        whExp_ifFiscalTrueStmt.setOperator(vc_Operator);
        whExp_ifFiscalTrueStmt.setOpenBrace("(");
        whExp_ifFiscalTrueStmt.setCloseBrace(")");
        whExp_ifFiscalTrueStmt.addWhereItem(whIt_curryearCheck);
        whExp_ifFiscalTrueStmt.addWhereExpression(whExp_dayMonthCheck);
        vc_ifFiscalTrueStmtCon.addElement(whExp_ifFiscalTrueStmt);
        sc_ifFiscalTrueStmtCon.setColumnExpression(vc_ifFiscalTrueStmtCon);
        vc_ifFiscalTrueStmtIn.addElement(sc_ifFiscalTrueStmtCon);
        vc_ifFiscalTrueStmtIn.addElement("1");
        vc_ifFiscalTrueStmtIn.addElement("0");
        fn_ifFiscalTrueStmt.setFunctionArguments(vc_ifFiscalTrueStmtIn);
        vc_ifFiscalTrueStmtOut.addElement(fn_ifFiscalTrueStmt);
        sc_ifFiscalTrueStmt.setColumnExpression(vc_ifFiscalTrueStmtOut);
        return sc_ifFiscalTrueStmt;
    }
    
    public SelectColumn date_fun(final Vector vector, final int arg_index, final String dt_type) {
        final SelectColumn sc_dtType = new SelectColumn();
        final FunctionCalls fn_dtType = new FunctionCalls();
        final TableColumn tb_dtType = new TableColumn();
        tb_dtType.setColumnName(dt_type.toUpperCase());
        fn_dtType.setFunctionName(tb_dtType);
        final Vector vc_dtTypeIn = new Vector();
        final Vector vc_dtTypeOut = new Vector();
        vc_dtTypeIn.addElement(vector.get(arg_index));
        fn_dtType.setFunctionArguments(vc_dtTypeIn);
        vc_dtTypeOut.addElement(fn_dtType);
        sc_dtType.setColumnExpression(vc_dtTypeOut);
        return sc_dtType;
    }
    
    public SelectColumn now() {
        final SelectColumn sc_Now = new SelectColumn();
        final FunctionCalls fn_Now = new FunctionCalls();
        final TableColumn tb_Now = new TableColumn();
        tb_Now.setColumnName("NOW");
        fn_Now.setFunctionName(tb_Now);
        final Vector vc_NowIn = new Vector();
        final Vector vc_NowOut = new Vector();
        fn_Now.setFunctionArguments(vc_NowIn);
        vc_NowOut.addElement(fn_Now);
        sc_Now.setColumnExpression(vc_NowOut);
        return sc_Now;
    }
}
