package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class substring_count extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        final SelectColumn sc = new SelectColumn();
        final Vector finalArgument = new Vector();
        final Vector arguments = new Vector();
        final Vector args_Quotient = new Vector();
        final SelectColumn sc_Quotient = new SelectColumn();
        final Vector vc_Quotient = new Vector();
        final SelectColumn sc_Remainder = new SelectColumn();
        final Vector vc_Remainder = new Vector();
        final FunctionCalls fnCl = new FunctionCalls();
        final TableColumn TC = new TableColumn();
        TC.setColumnName("");
        fnCl.setFunctionName(TC);
        final Vector args = new Vector();
        final SelectColumn sc_left = new SelectColumn();
        final Vector vc_left = new Vector();
        final SelectColumn sc_Right = new SelectColumn();
        final Vector vc_Right = new Vector();
        final FunctionCalls fnCl_lenStr = new FunctionCalls();
        final TableColumn tbCl_lenStr = new TableColumn();
        tbCl_lenStr.setColumnName("LENGTH");
        fnCl_lenStr.setFunctionName(tbCl_lenStr);
        final Vector vc_lenStr = new Vector();
        vc_lenStr.addElement(vector1.get(0));
        fnCl_lenStr.setFunctionArguments(vc_lenStr);
        vc_left.addElement(fnCl_lenStr);
        sc_left.setColumnExpression(vc_left);
        vc_Quotient.addElement(sc_left);
        final String Sub_Operator = "-";
        vc_Quotient.addElement(Sub_Operator);
        final FunctionCalls fnCl_lenRepStr = new FunctionCalls();
        final TableColumn tbCl_lenRepStr = new TableColumn();
        tbCl_lenRepStr.setColumnName("LENGTH");
        fnCl_lenRepStr.setFunctionName(tbCl_lenRepStr);
        final Vector vc_lenRepStr = new Vector();
        final SelectColumn sc_RepStr = new SelectColumn();
        final Vector vc_RepStr = new Vector();
        final FunctionCalls fnCl_RepStr = new FunctionCalls();
        final TableColumn tbCl_RepStr = new TableColumn();
        tbCl_RepStr.setColumnName("REPLACE");
        fnCl_RepStr.setFunctionName(tbCl_RepStr);
        final Vector vc_Replace = new Vector();
        vc_Replace.addElement(vector2.get(0));
        vc_Replace.addElement(vector2.get(1));
        final String Replace_String = "''";
        vc_Replace.addElement(Replace_String);
        fnCl_RepStr.setFunctionArguments(vc_Replace);
        vc_RepStr.addElement(fnCl_RepStr);
        sc_RepStr.setColumnExpression(vc_RepStr);
        vc_lenRepStr.addElement(sc_RepStr);
        fnCl_lenRepStr.setFunctionArguments(vc_lenRepStr);
        vc_Right.addElement(fnCl_lenRepStr);
        sc_Right.setColumnExpression(vc_Right);
        vc_Quotient.addElement(sc_Right);
        sc_Quotient.setColumnExpression(vc_Quotient);
        args.addElement(sc_Quotient);
        fnCl.setFunctionArguments(args);
        final SelectColumn sc2 = new SelectColumn();
        args_Quotient.addElement(fnCl);
        sc2.setColumnExpression(args_Quotient);
        arguments.addElement(sc2);
        final String Div_Operator = "DIV";
        arguments.addElement(Div_Operator);
        final FunctionCalls fnCl_lenRep = new FunctionCalls();
        final TableColumn tbCl_lenRep = new TableColumn();
        tbCl_lenRep.setColumnName("LENGTH");
        fnCl_lenRep.setFunctionName(tbCl_lenRep);
        final Vector vc_lenRep = new Vector();
        vc_lenRep.addElement(vector1.get(1));
        fnCl_lenRep.setFunctionArguments(vc_lenRep);
        vc_Remainder.addElement(fnCl_lenRep);
        sc_Remainder.setColumnExpression(vc_Remainder);
        arguments.addElement(sc_Remainder);
        sc.setColumnExpression(arguments);
        finalArgument.addElement(sc);
        this.setFunctionArguments(finalArgument);
    }
}
