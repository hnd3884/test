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

public class mtd extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector vector1 = new Vector();
        final Vector vector2 = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector1.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector1.addElement(this.functionArguments.elementAt(i_count));
                vector2.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String aggFunName = null;
        SelectColumn dispCol = null;
        if (vector1.elementAt(0) instanceof SelectColumn) {
            final SelectColumn typeofAggFun = vector1.elementAt(0);
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
            final Vector arguments = new Vector();
            final String yrSt = "year";
            final String mtSt = "month";
            final String dySt = "day";
            final String AndOperator = "AND";
            final String OrOperator = "OR";
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
            final WhereItem whIt_curryearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<=");
            whExp_if.addWhereItem(whIt_curryearCheck);
            final WhereItem whIt_dayCheck = this.constructWhItem(to_sqs, from_sqs, dySt, "<=");
            whExp_if.addWhereItem(whIt_dayCheck);
            final WhereExpression whExp_yearMonthCheck = new WhereExpression();
            final Vector vc_whItemListYearMonthCheck = new Vector();
            final WhereItem whIt_currMonthCheck = this.constructWhItem(to_sqs, from_sqs, mtSt, "<=");
            vc_whItemListYearMonthCheck.addElement(whIt_currMonthCheck);
            final WhereItem whIt_prevYearCheck = this.constructWhItem(to_sqs, from_sqs, yrSt, "<");
            vc_whItemListYearMonthCheck.addElement(whIt_prevYearCheck);
            whExp_yearMonthCheck.setWhereItem(vc_whItemListYearMonthCheck);
            whExp_yearMonthCheck.setOpenBrace("(");
            whExp_yearMonthCheck.setCloseBrace(")");
            final Vector vc_OperatorMonthCheck = new Vector();
            vc_OperatorMonthCheck.addElement(OrOperator);
            whExp_yearMonthCheck.setOperator(vc_OperatorMonthCheck);
            whExp_if.addWhereExpression(whExp_yearMonthCheck);
            final Vector vc_Operator = new Vector();
            vc_Operator.addElement(AndOperator);
            vc_Operator.addElement(AndOperator);
            whExp_if.setOperator(vc_Operator);
            whExp_if.setOpenBrace("(");
            whExp_if.setCloseBrace(")");
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
