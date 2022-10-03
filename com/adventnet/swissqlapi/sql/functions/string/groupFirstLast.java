package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class groupFirstLast extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Object arg1 = arguments.get(0);
        final SelectColumn arg1SC = new SelectColumn();
        final Vector arg1SCColExp = new Vector();
        arg1SCColExp.addElement(arg1);
        arg1SC.setColumnExpression(arg1SCColExp);
        final SelectColumn GCnctFSC = new SelectColumn();
        final Vector GCntFSCColExp = new Vector();
        final FunctionCalls GCnctFC = new FunctionCalls();
        GCnctFC.getFunctionName().setColumnName("group_concat");
        final Vector GCnctFCArgs = new Vector();
        GCnctFCArgs.addElement(arg1);
        GCnctFCArgs.addElement("'#@#'");
        GCnctFC.setFunctionArguments(GCnctFCArgs);
        GCnctFC.setSeparatorString("SEPARATOR");
        GCntFSCColExp.addElement(GCnctFC);
        GCnctFSC.setColumnExpression(GCntFSCColExp);
        final SelectColumn subInFSC = new SelectColumn();
        final Vector subInFSCColExp = new Vector();
        final FunctionCalls subInFC = new FunctionCalls();
        subInFC.getFunctionName().setColumnName("substring_index");
        final Vector subInFCArgs = new Vector();
        subInFCArgs.addElement(GCnctFSC);
        subInFCArgs.addElement("'#@#'");
        subInFCArgs.addElement(this.functionName.toString().equalsIgnoreCase("group_first") ? "1" : "-1");
        subInFC.setFunctionArguments(subInFCArgs);
        subInFSCColExp.addElement(subInFC);
        subInFSC.setColumnExpression(subInFSCColExp);
        this.functionName.setColumnName("");
        final Vector fnArgs = new Vector();
        fnArgs.addElement(subInFSC);
        this.setFunctionArguments(fnArgs);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Object arg1 = arguments.get(0);
        final SelectColumn arg1SC = new SelectColumn();
        final Vector arg1SCColExp = new Vector();
        arg1SCColExp.addElement(arg1);
        arg1SC.setColumnExpression(arg1SCColExp);
        final SelectColumn castFSC = new SelectColumn();
        final Vector castFSCColExp = new Vector();
        final FunctionCalls castFC = new FunctionCalls();
        castFC.getFunctionName().setColumnName("cast");
        final Vector castFCArgs = new Vector();
        castFCArgs.addElement(arg1SC);
        castFC.setAsDatatype("as");
        castFCArgs.addElement("TEXT");
        castFC.setFunctionArguments(castFCArgs);
        castFSCColExp.addElement(castFC);
        castFSC.setColumnExpression(castFSCColExp);
        final SelectColumn strAggFSC = new SelectColumn();
        final Vector strAggFSCColExp = new Vector();
        final FunctionCalls strAggFC = new FunctionCalls();
        strAggFC.getFunctionName().setColumnName("string_agg");
        final Vector strAggFCArgs = new Vector();
        strAggFCArgs.addElement(castFSC);
        strAggFCArgs.addElement("'#@#'");
        strAggFC.setFunctionArguments(strAggFCArgs);
        strAggFSCColExp.addElement(strAggFC);
        strAggFSC.setColumnExpression(strAggFSCColExp);
        final SelectColumn subInFSC = new SelectColumn();
        final Vector subInFSCColExp = new Vector();
        final FunctionCalls subInFC = new FunctionCalls();
        subInFC.getFunctionName().setColumnName("substring_index");
        final Vector subInFCArgs = new Vector();
        subInFCArgs.addElement(strAggFSC);
        subInFCArgs.addElement("'#@#'");
        subInFCArgs.addElement(this.functionName.toString().equalsIgnoreCase("group_first") ? "1" : "-1");
        subInFC.setFunctionArguments(subInFCArgs);
        subInFSCColExp.addElement(subInFC);
        subInFSC.setColumnExpression(subInFSCColExp);
        this.functionName.setColumnName("");
        final Vector fnArgs = new Vector();
        fnArgs.addElement(subInFSC);
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
        final String arg1 = arguments.get(0).toString();
        arguments = new Vector();
        arguments.addElement("string_agg(CAST(" + arg1 + " as TEXT), '#@#')");
        arguments.addElement("'#@#'");
        arguments.addElement(this.functionName.toString().equalsIgnoreCase("group_first") ? "1" : "-1");
        this.functionName.setColumnName("substring_index");
        this.setFunctionArguments(arguments);
    }
}
