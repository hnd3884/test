package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class qtd extends FunctionCalls
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
            final SelectColumn sc_if = new SelectColumn();
            final FunctionCalls fn_if = new FunctionCalls();
            final TableColumn tb_if = new TableColumn();
            tb_if.setColumnName("IF");
            fn_if.setFunctionName(tb_if);
            final Vector vc_ifIn = new Vector();
            final Vector vc_ifOut = new Vector();
            final SelectColumn sc_ifCon = new SelectColumn();
            final Vector vc_ifCon = new Vector();
            final WhereExpression whExp_if = new WhereExpression();
            final Vector vc_whItemList = new Vector();
            final WhereItem whIt_curryearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<=");
            whExp_if.addWhereItem(whIt_curryearCheck);
            final WhereExpression whExp_monthCheck = new WhereExpression();
            final Vector vc_whItemListMonthCheck = new Vector();
            final WhereItem whIt_currMonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<=");
            final WhereItem whIt_prevyearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<");
            vc_whItemListMonthCheck.addElement(whIt_currMonthCheck);
            vc_whItemListMonthCheck.addElement(whIt_prevyearCheck);
            whExp_monthCheck.setWhereItem(vc_whItemListMonthCheck);
            whExp_monthCheck.setOpenBrace("(");
            whExp_monthCheck.setCloseBrace(")");
            final Vector vc_OperatorMonthCheck = new Vector();
            vc_OperatorMonthCheck.addElement("OR");
            whExp_monthCheck.setOperator(vc_OperatorMonthCheck);
            whExp_if.addWhereExpression(whExp_monthCheck);
            final WhereItem whIt_currfiscalMonthCheck = this.constructFiscalWhItem(to_sqs, from_sqs, "<=");
            whExp_if.addWhereItem(whIt_currfiscalMonthCheck);
            final WhereExpression whExp_prevMonthCheck = new WhereExpression();
            final Vector vc_whItemListPrevMonthCheck = new Vector();
            final WhereItem whIt_daysCheck = this.constructWhItem(to_sqs, from_sqs, dySt, "<=");
            final WhereItem whIt_prevfiscalMonthCheck = this.constructFiscalWhItem(to_sqs, from_sqs, "<");
            vc_whItemListPrevMonthCheck.addElement(whIt_daysCheck);
            vc_whItemListPrevMonthCheck.addElement(whIt_prevfiscalMonthCheck);
            whExp_prevMonthCheck.setWhereItem(vc_whItemListPrevMonthCheck);
            whExp_prevMonthCheck.setOpenBrace("(");
            whExp_prevMonthCheck.setCloseBrace(")");
            final Vector vc_OperatorPrevMonthCheck = new Vector();
            vc_OperatorPrevMonthCheck.addElement("OR");
            whExp_prevMonthCheck.setOperator(vc_OperatorPrevMonthCheck);
            whExp_if.addWhereExpression(whExp_prevMonthCheck);
            whExp_if.setOpenBrace("(");
            whExp_if.setCloseBrace(")");
            final Vector vc_Operator = new Vector();
            vc_Operator.addElement("and");
            vc_Operator.addElement("and");
            vc_Operator.addElement("and");
            whExp_if.setOperator(vc_Operator);
            vc_ifCon.addElement(whExp_if);
            sc_ifCon.setColumnExpression(vc_ifCon);
            vc_ifIn.addElement(sc_ifCon);
            vc_ifIn.addElement(dispCol);
            vc_ifIn.addElement("null");
            fn_if.setFunctionArguments(vc_ifIn);
            vc_ifOut.addElement(fn_if);
            sc_if.setColumnExpression(vc_ifOut);
            arguments.addElement(sc_if);
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
    
    public WhereItem constructFiscalWhItem(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final String operator) throws ConvertException {
        final Vector vector = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String fiscalStartMonth = "null";
        if (this.functionArguments.size() > 2 && vector.elementAt(2) instanceof SelectColumn) {
            final SelectColumn sc = vector.elementAt(2);
            final Vector vc = sc.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
                throw new ConvertException("Invalid Argument Value for Function QTD", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "QTD", "FISCAL_START_MONTH" });
            }
            fiscalStartMonth = vc.elementAt(0);
        }
        if (fiscalStartMonth.equalsIgnoreCase("null")) {
            fiscalStartMonth = "1";
        }
        fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
        this.validateFiscalStartMonth(fiscalStartMonth, "QTD");
        final WhereItem whIt_dtType = new WhereItem();
        final WhereColumn whColLeftdtTypeExp = new WhereColumn();
        final Vector vc_whColLeftdtTypeExp = new Vector();
        final WhereColumn whColRightdtTypeExp = new WhereColumn();
        final Vector vc_whColRightdtTypeExp = new Vector();
        final SelectColumn sc_leftdtTypeExp = new SelectColumn();
        final Vector vc_leftdtTypeExp = new Vector();
        final SelectColumn sc_leftdtTypeExpAddend = new SelectColumn();
        final Vector vc_leftdtTypeExpAddend = new Vector();
        final SelectColumn sc_leftdtTypeExpDividend = new SelectColumn();
        final Vector vc_leftdtTypeExpDividend = new Vector();
        vc_leftdtTypeExpDividend.addElement("12");
        vc_leftdtTypeExpDividend.addElement("+");
        final SelectColumn sc_leftMonthExp = this.date_fun(vector, 1, "month");
        vc_leftdtTypeExpDividend.addElement(sc_leftMonthExp);
        vc_leftdtTypeExpDividend.addElement("-");
        vc_leftdtTypeExpDividend.addElement(fiscalStartMonth);
        sc_leftdtTypeExpDividend.setOpenBrace("(");
        sc_leftdtTypeExpDividend.setCloseBrace(")");
        sc_leftdtTypeExpDividend.setColumnExpression(vc_leftdtTypeExpDividend);
        vc_leftdtTypeExpAddend.addElement(sc_leftdtTypeExpDividend);
        vc_leftdtTypeExpAddend.addElement("%");
        vc_leftdtTypeExpAddend.addElement("3");
        sc_leftdtTypeExpAddend.setOpenBrace("(");
        sc_leftdtTypeExpAddend.setCloseBrace(")");
        sc_leftdtTypeExpAddend.setColumnExpression(vc_leftdtTypeExpAddend);
        vc_leftdtTypeExp.addElement(sc_leftdtTypeExpAddend);
        vc_leftdtTypeExp.addElement("+");
        vc_leftdtTypeExp.addElement("1");
        sc_leftdtTypeExp.setOpenBrace("(");
        sc_leftdtTypeExp.setCloseBrace(")");
        sc_leftdtTypeExp.setColumnExpression(vc_leftdtTypeExp);
        vc_whColLeftdtTypeExp.addElement(sc_leftdtTypeExp);
        whColLeftdtTypeExp.setColumnExpression(vc_whColLeftdtTypeExp);
        whIt_dtType.setLeftWhereExp(whColLeftdtTypeExp);
        final SelectColumn rightdtTypeExp_Now = this.now();
        final Vector vc_rightdtTypeExp_Now = new Vector();
        vc_rightdtTypeExp_Now.addElement(rightdtTypeExp_Now);
        final SelectColumn sc_rightdtTypeExp = new SelectColumn();
        final Vector vc_rightdtTypeExp = new Vector();
        final SelectColumn sc_rightdtTypeExpAddend = new SelectColumn();
        final Vector vc_rightdtTypeExpAddend = new Vector();
        final SelectColumn sc_rightdtTypeExpDividend = new SelectColumn();
        final Vector vc_rightdtTypeExpDividend = new Vector();
        vc_rightdtTypeExpDividend.addElement("12");
        vc_rightdtTypeExpDividend.addElement("+");
        final SelectColumn sc_rightMonthExp = this.date_fun(vc_rightdtTypeExp_Now, 0, "month");
        vc_rightdtTypeExpDividend.addElement(sc_rightMonthExp);
        vc_rightdtTypeExpDividend.addElement("-");
        vc_rightdtTypeExpDividend.addElement(fiscalStartMonth);
        sc_rightdtTypeExpDividend.setOpenBrace("(");
        sc_rightdtTypeExpDividend.setCloseBrace(")");
        sc_rightdtTypeExpDividend.setColumnExpression(vc_rightdtTypeExpDividend);
        vc_rightdtTypeExpAddend.addElement(sc_rightdtTypeExpDividend);
        vc_rightdtTypeExpAddend.addElement("%");
        vc_rightdtTypeExpAddend.addElement("3");
        sc_rightdtTypeExpAddend.setOpenBrace("(");
        sc_rightdtTypeExpAddend.setCloseBrace(")");
        sc_rightdtTypeExpAddend.setColumnExpression(vc_rightdtTypeExpAddend);
        vc_rightdtTypeExp.addElement(sc_rightdtTypeExpAddend);
        vc_rightdtTypeExp.addElement("+");
        vc_rightdtTypeExp.addElement("1");
        sc_rightdtTypeExp.setOpenBrace("(");
        sc_rightdtTypeExp.setCloseBrace(")");
        sc_rightdtTypeExp.setColumnExpression(vc_rightdtTypeExp);
        vc_whColRightdtTypeExp.addElement(sc_rightdtTypeExp);
        whColRightdtTypeExp.setColumnExpression(vc_whColRightdtTypeExp);
        whIt_dtType.setRightWhereExp(whColRightdtTypeExp);
        whIt_dtType.setOperator(operator);
        return whIt_dtType;
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
