package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class ifmatches extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        final Vector vector = new Vector();
        if (this.functionArguments.size() % 2 == 1) {
            final SelectColumn sc_null = new SelectColumn();
            final Vector vc_null = new Vector();
            vc_null.addElement("null");
            sc_null.setColumnExpression(vc_null);
            this.functionArguments.addElement(sc_null);
        }
        arguments.addElement(this.ifStatements(to_sqs, from_sqs, 1));
        this.setOpenBracesForFunctionNameRequired(false);
        this.setFunctionArguments(arguments);
    }
    
    public SelectColumn ifStatements(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final int index) throws ConvertException {
        final Vector vector = new Vector();
        int i_count;
        for (i_count = 0, i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final int arg_len = this.functionArguments.size();
        final SelectColumn sc_ifStmt = new SelectColumn();
        final FunctionCalls fnCall_ifStmt = new FunctionCalls();
        final TableColumn tb_ifStmt = new TableColumn();
        tb_ifStmt.setColumnName("IF");
        fnCall_ifStmt.setFunctionName(tb_ifStmt);
        final Vector vc_ifStmtIn = new Vector();
        final Vector vc_ifStmtOut = new Vector();
        vc_ifStmtIn.addElement(this.subFunctions(to_sqs, from_sqs, vector, index));
        if (this.functionArguments.size() > index + 1) {
            vc_ifStmtIn.addElement(vector.get(index + 1));
        }
        if (this.functionArguments.size() >= index + 2) {
            if (this.functionArguments.size() - 1 == index + 2) {
                vc_ifStmtIn.addElement(vector.get(index + 2));
            }
            else {
                vc_ifStmtIn.addElement(this.ifStatements(to_sqs, from_sqs, index + 2));
            }
        }
        fnCall_ifStmt.setFunctionArguments(vc_ifStmtIn);
        vc_ifStmtOut.addElement(fnCall_ifStmt);
        sc_ifStmt.setColumnExpression(vc_ifStmtOut);
        return sc_ifStmt;
    }
    
    public SelectColumn subFunctions(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final Vector searchValue_vector, final int index) throws ConvertException {
        final Vector vector = new Vector();
        String operator = "=";
        String fnName = "";
        FunctionCalls fnCall_subfn = new FunctionCalls();
        TableColumn tbCl_subfn = new TableColumn();
        Vector vc_subfn = new Vector();
        final SelectColumn sc_whExp = new SelectColumn();
        final Vector vc_whExp = new Vector();
        final WhereExpression whExp = new WhereExpression();
        if (searchValue_vector.elementAt(index) instanceof SelectColumn) {
            final SelectColumn sc_searchValue = searchValue_vector.elementAt(index);
            final Vector vc_searchValue = sc_searchValue.getColumnExpression();
            if (vc_searchValue.elementAt(0) instanceof FunctionCalls) {
                fnCall_subfn = vc_searchValue.elementAt(0);
                tbCl_subfn = fnCall_subfn.getFunctionName();
                vc_subfn = fnCall_subfn.getFunctionArguments();
                fnName = tbCl_subfn.getColumnName();
                if (fnName.equalsIgnoreCase("equals")) {
                    operator = "IN";
                    final WhereItem wi_if = new WhereItem();
                    final WhereColumn if_left = new WhereColumn();
                    final Vector vc_if_left = new Vector();
                    final WhereColumn if_right = new WhereColumn();
                    final Vector vc_if_right = new Vector();
                    if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                        vector.addElement(this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs));
                    }
                    else {
                        vector.addElement(this.functionArguments.elementAt(0));
                    }
                    vc_if_left.addElement(vector.get(vector.size() - 1));
                    if_left.setColumnExpression(vc_if_left);
                    wi_if.setLeftWhereExp(if_left);
                    wi_if.setOperator(operator);
                    final SelectColumn sc_rightExp = new SelectColumn();
                    final Vector vc_rightExp = new Vector();
                    vc_rightExp.addElement(vc_subfn.get(0));
                    for (int i = 0; i < vc_subfn.size(); ++i) {
                        vc_rightExp.addElement(",");
                        vc_rightExp.addElement(vc_subfn.get(i));
                    }
                    sc_rightExp.setColumnExpression(vc_rightExp);
                    sc_rightExp.setOpenBrace("(");
                    sc_rightExp.setCloseBrace(")");
                    vc_if_right.addElement(sc_rightExp);
                    if_right.setColumnExpression(vc_if_right);
                    wi_if.setRightWhereExp(if_right);
                    whExp.addWhereItem(wi_if);
                }
                else {
                    operator = "like";
                    for (int i_count = 0; i_count < vc_subfn.size(); ++i_count) {
                        final WhereItem wi_if2 = new WhereItem();
                        final WhereColumn if_left2 = new WhereColumn();
                        final Vector vc_if_left2 = new Vector();
                        final WhereColumn if_right2 = new WhereColumn();
                        final Vector vc_if_right2 = new Vector();
                        if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                            vector.addElement(this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs));
                        }
                        else {
                            vector.addElement(this.functionArguments.elementAt(0));
                        }
                        vc_if_left2.addElement(vector.get(i_count));
                        if_left2.setColumnExpression(vc_if_left2);
                        wi_if2.setLeftWhereExp(if_left2);
                        wi_if2.setOperator(operator);
                        final SelectColumn sc_concat = new SelectColumn();
                        final FunctionCalls fn_concat = new FunctionCalls();
                        final TableColumn tb_concat = new TableColumn();
                        tb_concat.setColumnName("CONCAT");
                        fn_concat.setFunctionName(tb_concat);
                        final Vector vc_concatIn = new Vector();
                        if (fnName.equalsIgnoreCase("STARTSWITH")) {
                            vc_concatIn.addElement(vc_subfn.get(i_count));
                            vc_concatIn.addElement("'%'");
                        }
                        else if (fnName.equalsIgnoreCase("ENDSWITH")) {
                            vc_concatIn.addElement("'%'");
                            vc_concatIn.addElement(vc_subfn.get(i_count));
                        }
                        else if (fnName.equalsIgnoreCase("CONTAINS")) {
                            vc_concatIn.addElement("'%'");
                            vc_concatIn.addElement(vc_subfn.get(i_count));
                            vc_concatIn.addElement("'%'");
                        }
                        else if (vc_subfn.size() == 1) {
                            vc_concatIn.addElement("'%'");
                            vc_concatIn.addElement(vc_subfn.get(i_count));
                            vc_concatIn.addElement("'%'");
                        }
                        final Vector vc_concatOut = new Vector();
                        fn_concat.setFunctionArguments(vc_concatIn);
                        vc_concatOut.addElement(fn_concat);
                        sc_concat.setOpenBrace("(");
                        sc_concat.setCloseBrace(")");
                        sc_concat.setColumnExpression(vc_concatOut);
                        vc_if_right2.addElement(sc_concat);
                        if_right2.setColumnExpression(vc_if_right2);
                        wi_if2.setRightWhereExp(if_right2);
                        wi_if2.setOpenBrace("(");
                        wi_if2.setCloseBrace(")");
                        whExp.addWhereItem(wi_if2);
                        if (i_count < vc_subfn.size() - 1) {
                            whExp.addOperator("OR");
                        }
                    }
                }
                whExp.setOpenBrace("(");
                whExp.setCloseBrace(")");
                vc_whExp.addElement(whExp);
            }
            else {
                final WhereItem wi_if = new WhereItem();
                final WhereColumn if_left = new WhereColumn();
                final Vector vc_if_left = new Vector();
                final WhereColumn if_right = new WhereColumn();
                final Vector vc_if_right = new Vector();
                if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                    vector.addElement(this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    vector.addElement(this.functionArguments.elementAt(0));
                }
                vc_if_left.addElement(vector.get(0));
                if_left.setColumnExpression(vc_if_left);
                wi_if.setLeftWhereExp(if_left);
                vc_if_right.addElement(searchValue_vector.elementAt(index));
                if_right.setColumnExpression(vc_if_right);
                wi_if.setRightWhereExp(if_right);
                wi_if.setOpenBrace("(");
                wi_if.setCloseBrace(")");
                wi_if.setOperator(operator);
                whExp.addWhereItem(wi_if);
                vc_whExp.addElement(whExp);
            }
        }
        sc_whExp.setColumnExpression(vc_whExp);
        return sc_whExp;
    }
}
