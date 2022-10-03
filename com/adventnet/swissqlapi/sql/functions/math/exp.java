package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class exp extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        final Vector vector1 = new Vector();
        final Vector vector2 = new Vector();
        final Vector vector3 = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                vector1.addElement(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
                vector2.addElement(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
                vector3.addElement(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector1.addElement(this.functionArguments.elementAt(i));
                vector2.addElement(this.functionArguments.elementAt(i));
                vector3.addElement(this.functionArguments.elementAt(i));
            }
        }
        final SelectColumn sc_if = new SelectColumn();
        final FunctionCalls fnCl_if = new FunctionCalls();
        final TableColumn tbCl_if = new TableColumn();
        tbCl_if.setColumnName("IF");
        fnCl_if.setFunctionName(tbCl_if);
        final Vector vc_ifIn = new Vector();
        final Vector vc_ifOut = new Vector();
        final SelectColumn sc_whExp_if = new SelectColumn();
        final Vector vc_whExp_if = new Vector();
        final WhereExpression whExp_if = new WhereExpression();
        final WhereItem whItem_if_UpperLimit = new WhereItem();
        final WhereColumn whCol_UpperLimit_LeftExpr = new WhereColumn();
        final Vector vc_UpperLimit_LeftExpr = new Vector();
        vc_UpperLimit_LeftExpr.addElement(vector1.get(0));
        whCol_UpperLimit_LeftExpr.setColumnExpression(vc_UpperLimit_LeftExpr);
        final WhereColumn whCol_UpperLimit_RightExpr = new WhereColumn();
        final Vector vc_UpperLimit_RightExpr = new Vector();
        vc_UpperLimit_RightExpr.addElement("709");
        whCol_UpperLimit_RightExpr.setColumnExpression(vc_UpperLimit_RightExpr);
        whItem_if_UpperLimit.setLeftWhereExp(whCol_UpperLimit_LeftExpr);
        whItem_if_UpperLimit.setOperator(">");
        whItem_if_UpperLimit.setRightWhereExp(whCol_UpperLimit_RightExpr);
        final WhereItem whItem_if_LowerLimit = new WhereItem();
        final WhereColumn whCol_LowerLimit_LeftExpr = new WhereColumn();
        final Vector vc_LowerLimit_LeftExpr = new Vector();
        vc_LowerLimit_LeftExpr.addElement(vector2.get(0));
        whCol_LowerLimit_LeftExpr.setColumnExpression(vc_LowerLimit_LeftExpr);
        final WhereColumn whCol_LowerLimit_RightExpr = new WhereColumn();
        final Vector vc_LowerLimit_RightExpr = new Vector();
        vc_LowerLimit_RightExpr.addElement("-745");
        whCol_LowerLimit_RightExpr.setColumnExpression(vc_LowerLimit_RightExpr);
        whItem_if_LowerLimit.setLeftWhereExp(whCol_LowerLimit_LeftExpr);
        whItem_if_LowerLimit.setOperator("<");
        whItem_if_LowerLimit.setRightWhereExp(whCol_LowerLimit_RightExpr);
        final Vector vc_whExp_if_Operator = new Vector();
        vc_whExp_if_Operator.addElement("OR");
        whExp_if.addWhereItem(whItem_if_UpperLimit);
        whExp_if.addWhereItem(whItem_if_LowerLimit);
        whExp_if.setOperator(vc_whExp_if_Operator);
        vc_whExp_if.addElement(whExp_if);
        sc_whExp_if.setColumnExpression(vc_whExp_if);
        sc_whExp_if.setOpenBrace("(");
        sc_whExp_if.setCloseBrace(")");
        vc_ifIn.addElement(sc_whExp_if);
        vc_ifIn.addElement("null");
        vc_ifIn.addElement(vector3.get(0));
        fnCl_if.setFunctionArguments(vc_ifIn);
        vc_ifOut.addElement(fnCl_if);
        sc_if.setColumnExpression(vc_ifOut);
        arguments.addElement(sc_if);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toInformixSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                final Vector colExpr = sc.getColumnExpression();
                if (colExpr.size() != 1 || !(colExpr.get(0) instanceof String)) {
                    throw new ConvertException("\nThe function EXP is not supported in TimesTen 5.1.21\n");
                }
                this.functionName.setColumnName("");
                this.setOpenBracesForFunctionNameRequired(false);
                arguments.add(Math.exp(Double.parseDouble(colExpr.get(0).toString())) + "");
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("EXP");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
}
