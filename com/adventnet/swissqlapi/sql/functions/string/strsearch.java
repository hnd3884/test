package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class strsearch extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName();
        this.functionName.setColumnName("IF");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (fnStr.equalsIgnoreCase("ISSTARTSWITH")) {
            final Vector finalArguments = new Vector();
            final String true_num = "1";
            final String false_num = "0";
            final SelectColumn sc_if = new SelectColumn();
            final WhereItem wi_if = new WhereItem();
            final Vector vc_if = new Vector();
            final WhereColumn if_left = new WhereColumn();
            final Vector vc_if_left = new Vector();
            final WhereColumn if_right = new WhereColumn();
            final Vector vc_if_right = new Vector();
            final SelectColumn sc = new SelectColumn();
            final Vector vc = new Vector();
            final Vector args = new Vector();
            vc_if_left.addElement(arguments.get(0));
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator("like");
            final StringBuilder strsb = new StringBuilder();
            boolean isString = false;
            if (arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc_strCol = arguments.elementAt(1);
                final Vector vc_strCol = sc_strCol.getColumnExpression();
                if (vc_strCol.elementAt(0) instanceof String) {
                    isString = true;
                    String str = vc_strCol.elementAt(0);
                    str = str.replaceAll("'", "");
                    strsb.append("'");
                    strsb.append(str);
                    strsb.append("%'");
                }
            }
            if (arguments.elementAt(1) instanceof String || isString) {
                final SelectColumn sb_strsb = new SelectColumn();
                final Vector vc_strsb = new Vector();
                vc_strsb.addElement(strsb);
                sb_strsb.setColumnExpression(vc_strsb);
                vc_if_right.addElement(sb_strsb);
            }
            else {
                final SelectColumn sc_concat = new SelectColumn();
                final Vector vc_concatIn = new Vector();
                final Vector vc_concatOut = new Vector();
                final FunctionCalls fn_concat = new FunctionCalls();
                final TableColumn tb_concat = new TableColumn();
                tb_concat.setColumnName("CONCAT");
                fn_concat.setFunctionName(tb_concat);
                vc_concatIn.addElement(arguments.get(1));
                vc_concatIn.add("'%'");
                fn_concat.setFunctionArguments(vc_concatIn);
                vc_concatOut.addElement(fn_concat);
                sc_concat.setColumnExpression(vc_concatOut);
                vc_if_right.addElement(sc_concat);
            }
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            vc_if.addElement(wi_if);
            sc_if.setColumnExpression(vc_if);
            finalArguments.addElement(sc_if);
            finalArguments.addElement(true_num);
            finalArguments.addElement(false_num);
            this.setFunctionArguments(finalArguments);
        }
        else if (fnStr.equalsIgnoreCase("ISENDSWITH")) {
            final Vector finalArguments = new Vector();
            final String true_num = "1";
            final String false_num = "0";
            final SelectColumn sc_if = new SelectColumn();
            final WhereItem wi_if = new WhereItem();
            final Vector vc_if = new Vector();
            final WhereColumn if_left = new WhereColumn();
            final Vector vc_if_left = new Vector();
            final WhereColumn if_right = new WhereColumn();
            final Vector vc_if_right = new Vector();
            final SelectColumn sc = new SelectColumn();
            final Vector vc = new Vector();
            final Vector args = new Vector();
            vc_if_left.addElement(arguments.get(0));
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator("like");
            final StringBuilder strsb = new StringBuilder();
            boolean isString = false;
            if (arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc_strCol = arguments.elementAt(1);
                final Vector vc_strCol = sc_strCol.getColumnExpression();
                if (vc_strCol.elementAt(0) instanceof String) {
                    isString = true;
                    String str = vc_strCol.elementAt(0);
                    str = str.replaceAll("'", "");
                    strsb.append("'%");
                    strsb.append(str);
                    strsb.append("'");
                }
            }
            if (arguments.elementAt(1) instanceof String || isString) {
                final SelectColumn sb_strsb = new SelectColumn();
                final Vector vc_strsb = new Vector();
                vc_strsb.addElement(strsb);
                sb_strsb.setColumnExpression(vc_strsb);
                vc_if_right.addElement(sb_strsb);
            }
            else {
                final SelectColumn sc_concat = new SelectColumn();
                final Vector vc_concatIn = new Vector();
                final Vector vc_concatOut = new Vector();
                final FunctionCalls fn_concat = new FunctionCalls();
                final TableColumn tb_concat = new TableColumn();
                tb_concat.setColumnName("CONCAT");
                fn_concat.setFunctionName(tb_concat);
                vc_concatIn.add("'%'");
                vc_concatIn.addElement(arguments.get(1));
                fn_concat.setFunctionArguments(vc_concatIn);
                vc_concatOut.addElement(fn_concat);
                sc_concat.setColumnExpression(vc_concatOut);
                vc_if_right.addElement(sc_concat);
            }
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            vc_if.addElement(wi_if);
            sc_if.setColumnExpression(vc_if);
            finalArguments.addElement(sc_if);
            finalArguments.addElement(true_num);
            finalArguments.addElement(false_num);
            this.setFunctionArguments(finalArguments);
        }
        else if (fnStr.equalsIgnoreCase("ISCONTAINS")) {
            final Vector finalArguments = new Vector();
            final String true_num = "1";
            final String false_num = "0";
            final SelectColumn sc_if = new SelectColumn();
            final WhereItem wi_if = new WhereItem();
            final Vector vc_if = new Vector();
            final WhereColumn if_left = new WhereColumn();
            final Vector vc_if_left = new Vector();
            final WhereColumn if_right = new WhereColumn();
            final Vector vc_if_right = new Vector();
            final SelectColumn sc = new SelectColumn();
            final Vector vc = new Vector();
            final Vector args = new Vector();
            vc_if_left.addElement(arguments.get(0));
            if_left.setColumnExpression(vc_if_left);
            wi_if.setLeftWhereExp(if_left);
            wi_if.setOperator("like");
            final StringBuilder strsb = new StringBuilder();
            boolean isString = false;
            if (arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc_strCol = arguments.elementAt(1);
                final Vector vc_strCol = sc_strCol.getColumnExpression();
                if (vc_strCol.elementAt(0) instanceof String) {
                    isString = true;
                    String str = vc_strCol.elementAt(0);
                    str = str.replaceAll("'", "");
                    strsb.append("'%");
                    strsb.append(str);
                    strsb.append("%'");
                }
            }
            if (arguments.elementAt(1) instanceof String || isString) {
                final SelectColumn sb_strsb = new SelectColumn();
                final Vector vc_strsb = new Vector();
                vc_strsb.addElement(strsb);
                sb_strsb.setColumnExpression(vc_strsb);
                vc_if_right.addElement(sb_strsb);
            }
            else {
                final SelectColumn sc_concat = new SelectColumn();
                final Vector vc_concatIn = new Vector();
                final Vector vc_concatOut = new Vector();
                final FunctionCalls fn_concat = new FunctionCalls();
                final TableColumn tb_concat = new TableColumn();
                tb_concat.setColumnName("CONCAT");
                fn_concat.setFunctionName(tb_concat);
                vc_concatIn.add("'%'");
                vc_concatIn.addElement(arguments.get(1));
                vc_concatIn.add("'%'");
                fn_concat.setFunctionArguments(vc_concatIn);
                vc_concatOut.addElement(fn_concat);
                sc_concat.setColumnExpression(vc_concatOut);
                vc_if_right.addElement(sc_concat);
            }
            if_right.setColumnExpression(vc_if_right);
            wi_if.setRightWhereExp(if_right);
            vc_if.addElement(wi_if);
            sc_if.setColumnExpression(vc_if);
            finalArguments.addElement(sc_if);
            finalArguments.addElement(true_num);
            finalArguments.addElement(false_num);
            this.setFunctionArguments(finalArguments);
        }
        else if (fnStr.equalsIgnoreCase("ISEMPTY")) {
            this.functionName.setColumnName("IF");
            final Vector vc_isNull = new Vector();
            final SelectColumn sc_whIt = new SelectColumn();
            final Vector vc_whIt = new Vector();
            final WhereItem whIt_isEmpty = new WhereItem();
            final WhereColumn whCol_LeftExp = new WhereColumn();
            final Vector vc_leftExp = new Vector();
            final WhereColumn whCol_RightExp = new WhereColumn();
            final Vector vc_rightExp = new Vector();
            final SelectColumn sc_ifNull = new SelectColumn();
            final Vector vc_ifNullIn = new Vector();
            final Vector vc_ifNullOut = new Vector();
            final FunctionCalls fnCl_ifNull = new FunctionCalls();
            final TableColumn tbCl_ifNull = new TableColumn();
            tbCl_ifNull.setColumnName("IFNULL");
            fnCl_ifNull.setFunctionName(tbCl_ifNull);
            final SelectColumn sc_trim = new SelectColumn();
            final Vector vc_trimIn = new Vector();
            final Vector vc_trimOut = new Vector();
            final FunctionCalls fnCl_trim = new FunctionCalls();
            final TableColumn tbCl_trim = new TableColumn();
            tbCl_trim.setColumnName("TRIM");
            fnCl_trim.setFunctionName(tbCl_trim);
            vc_trimIn.addElement(arguments.get(0));
            fnCl_trim.setFunctionArguments(vc_trimIn);
            vc_trimOut.addElement(fnCl_trim);
            sc_trim.setColumnExpression(vc_trimOut);
            vc_ifNullIn.addElement(sc_trim);
            vc_ifNullIn.addElement("''");
            fnCl_ifNull.setFunctionArguments(vc_ifNullIn);
            vc_ifNullOut.addElement(fnCl_ifNull);
            sc_ifNull.setColumnExpression(vc_ifNullOut);
            vc_leftExp.addElement(sc_ifNull);
            whCol_LeftExp.setColumnExpression(vc_leftExp);
            vc_rightExp.addElement("''");
            whCol_RightExp.setColumnExpression(vc_rightExp);
            whIt_isEmpty.setLeftWhereExp(whCol_LeftExp);
            whIt_isEmpty.setOperator("=");
            whIt_isEmpty.setRightWhereExp(whCol_RightExp);
            vc_whIt.addElement(whIt_isEmpty);
            sc_whIt.setColumnExpression(vc_whIt);
            final SelectColumn sc_trueStmt = new SelectColumn();
            final Vector vc_trueStmt = new Vector();
            vc_trueStmt.addElement("1");
            sc_trueStmt.setColumnExpression(vc_trueStmt);
            final SelectColumn sc_falseStmt = new SelectColumn();
            final Vector vc_falseStmt = new Vector();
            vc_falseStmt.addElement("0");
            sc_falseStmt.setColumnExpression(vc_falseStmt);
            vc_isNull.addElement(sc_whIt);
            vc_isNull.addElement(sc_trueStmt);
            vc_isNull.addElement(sc_falseStmt);
            this.setFunctionArguments(vc_isNull);
        }
        else if (fnStr.equalsIgnoreCase("TO_STRING")) {
            this.functionName.setColumnName("CONVERT");
            final Vector vc_tostring = new Vector();
            final SelectColumn sc_len = new SelectColumn();
            final Vector vc_len = new Vector();
            String str_len = "";
            final int len = 0;
            vc_tostring.addElement(arguments.get(0));
            if (this.functionArguments.size() > 1) {
                if (arguments.elementAt(1) instanceof SelectColumn) {
                    final SelectColumn sc2 = arguments.elementAt(1);
                    final Vector vc2 = sc2.getColumnExpression();
                    if (!(vc2.elementAt(0) instanceof String)) {
                        throw new ConvertException("Invalid Argument Value for Function" + fnStr.toUpperCase(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { fnStr.toUpperCase(), "STRING_LEN" });
                    }
                    str_len = vc2.elementAt(0);
                    str_len = str_len.replaceAll("'", "");
                    this.validateStringLength(str_len, fnStr.toUpperCase());
                }
            }
            else {
                str_len = "100";
            }
            vc_len.addElement("char(" + str_len + ")");
            sc_len.setColumnExpression(vc_len);
            vc_tostring.addElement(sc_len);
            this.setFunctionArguments(vc_tostring);
        }
        else if (fnStr.equalsIgnoreCase("CONVERT_TO_DATETIME")) {
            this.functionName.setColumnName("STR_TO_DATE");
            final Vector vc_datetime = new Vector();
            vc_datetime.addElement(arguments.get(0));
            String str_format = "";
            final SelectColumn sc_format = new SelectColumn();
            if (arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc3 = arguments.elementAt(1);
                final Vector vc3 = sc3.getColumnExpression();
                if (vc3.elementAt(0) instanceof String) {
                    str_format = vc3.elementAt(0);
                }
                str_format = str_format.replaceAll("yyyy", "%Y");
                str_format = str_format.replaceAll("yy", "%y");
                str_format = str_format.replaceAll("MMMM", "%M");
                str_format = str_format.replaceAll("MMM", "%b");
                str_format = str_format.replaceAll("MM", "%m");
                str_format = str_format.replaceAll("EEEE", "%W");
                str_format = str_format.replaceAll("EEE", "%a");
                str_format = str_format.replaceAll("dd", "%d");
                str_format = str_format.replaceAll("HH", "%H");
                str_format = str_format.replaceAll("hh", "%h");
                str_format = str_format.replaceAll("mm", "%i");
                str_format = str_format.replaceAll("ss", "%s");
                str_format = str_format.replaceAll("SSS", "%f");
                str_format = str_format.replaceAll("a", "%p");
            }
            vc_datetime.addElement(str_format);
            this.setFunctionArguments(vc_datetime);
        }
    }
}
