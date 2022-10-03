package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class aggregateIf extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
        final Vector vector = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Vector arguments = new Vector();
        if (fnStr.equalsIgnoreCase("avgif")) {
            this.functionName.setColumnName("AVG");
            if (this.functionArguments.size() < 3) {
                vector.addElement("null");
            }
            final SelectColumn sc_if = new SelectColumn();
            final Vector vc_if = new Vector();
            final FunctionCalls fn_if = new FunctionCalls();
            final TableColumn tb_if = new TableColumn();
            tb_if.setColumnName("IF");
            fn_if.setFunctionName(tb_if);
            fn_if.setFunctionArguments(vector);
            vc_if.addElement(fn_if);
            sc_if.setColumnExpression(vc_if);
            arguments.addElement(sc_if);
        }
        else if (fnStr.equalsIgnoreCase("sumif")) {
            this.functionName.setColumnName("SUM");
            if (this.functionArguments.size() < 3) {
                vector.addElement("null");
            }
            final SelectColumn sc_if = new SelectColumn();
            final Vector vc_if = new Vector();
            final FunctionCalls fn_if = new FunctionCalls();
            final TableColumn tb_if = new TableColumn();
            tb_if.setColumnName("IF");
            fn_if.setFunctionName(tb_if);
            fn_if.setFunctionArguments(vector);
            vc_if.addElement(fn_if);
            sc_if.setColumnExpression(vc_if);
            arguments.addElement(sc_if);
        }
        else if (fnStr.equalsIgnoreCase("countif")) {
            this.functionName.setColumnName("COUNT");
            final Vector vc_count = new Vector();
            vc_count.addElement(vector.get(0));
            vc_count.addElement("1");
            vc_count.addElement("null");
            final SelectColumn sc_if2 = new SelectColumn();
            final Vector vc_if2 = new Vector();
            final FunctionCalls fn_if2 = new FunctionCalls();
            final TableColumn tb_if2 = new TableColumn();
            tb_if2.setColumnName("IF");
            fn_if2.setFunctionName(tb_if2);
            fn_if2.setFunctionArguments(vc_count);
            vc_if2.addElement(fn_if2);
            sc_if2.setColumnExpression(vc_if2);
            arguments.addElement(sc_if2);
        }
        else if (fnStr.equalsIgnoreCase("distinctcount")) {
            this.functionName.setColumnName("COUNT");
            final SelectColumn sc_distinct = new SelectColumn();
            final Vector vc_distinct = new Vector();
            final FunctionCalls fn_distinct = new FunctionCalls();
            final TableColumn tb_distinct = new TableColumn();
            tb_distinct.setColumnName("DISTINCT");
            fn_distinct.setFunctionName(tb_distinct);
            fn_distinct.setFunctionArguments(vector);
            vc_distinct.addElement(fn_distinct);
            sc_distinct.setColumnExpression(vc_distinct);
            arguments.addElement(sc_distinct);
        }
        else if (fnStr.equalsIgnoreCase("count_wb")) {
            this.functionName.setColumnName("SUM");
            final SelectColumn sc_if = new SelectColumn();
            final Vector vc_ifIn = new Vector();
            final Vector vc_ifOut = new Vector();
            final FunctionCalls fn_if2 = new FunctionCalls();
            final TableColumn tb_if2 = new TableColumn();
            tb_if2.setColumnName("IF");
            fn_if2.setFunctionName(tb_if2);
            final SelectColumn sc_whIt = new SelectColumn();
            final Vector vc_whIt = new Vector();
            final WhereItem whItem = new WhereItem();
            final WhereColumn leftExp = new WhereColumn();
            final Vector vc_leftExp = new Vector();
            final WhereColumn rightExp = new WhereColumn();
            final Vector vc_rightExp = new Vector();
            vc_leftExp.addElement(vector.get(0));
            leftExp.setColumnExpression(vc_leftExp);
            whItem.setLeftWhereExp(leftExp);
            whItem.setOperator("is null");
            vc_whIt.addElement(whItem);
            sc_whIt.setColumnExpression(vc_whIt);
            vc_ifIn.addElement(sc_whIt);
            vc_ifIn.addElement("1");
            vc_ifIn.addElement("1");
            fn_if2.setFunctionArguments(vc_ifIn);
            vc_ifOut.addElement(fn_if2);
            sc_if.setColumnExpression(vc_ifOut);
            arguments.addElement(sc_if);
        }
        this.setFunctionArguments(arguments);
    }
}
