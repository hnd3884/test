package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class substring extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        this.functionName.setColumnName("SUBSTR");
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
        if (originalFunctionName.equalsIgnoreCase("SUBSTRING")) {
            if (this.functionArguments.size() > 2) {
                final SelectColumn expression1 = this.functionArguments.get(1);
                final SelectColumn expression2 = this.functionArguments.get(2);
                if (expression1.getColumnExpression().get(0) instanceof String) {
                    String startAt = new String();
                    for (int i = 0; i < expression1.getColumnExpression().size(); ++i) {
                        startAt += expression1.getColumnExpression().get(i).toString();
                    }
                    String noOfChar = new String();
                    for (int j = 0; j < expression2.getColumnExpression().size(); ++j) {
                        noOfChar += expression2.getColumnExpression().get(j).toString();
                    }
                    try {
                        if (Integer.parseInt(startAt) <= 0) {
                            final int newNoOfChar = Integer.parseInt(noOfChar) + Integer.parseInt(startAt) - 1;
                            this.functionArguments.remove(1);
                            this.functionArguments.add(1, "1");
                            final String numOfCharStr = "" + newNoOfChar;
                            this.functionArguments.remove(2);
                            this.functionArguments.add(2, numOfCharStr);
                        }
                    }
                    catch (final NumberFormatException ex) {}
                }
            }
            else {
                if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
                    this.setFromInTrim(",");
                }
                if (this.getForLength() != null && this.getForLength().equalsIgnoreCase("FOR")) {
                    this.setForLength(",");
                }
            }
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        this.functionName.setColumnName("SUBSTRING");
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
        if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
            if (this.functionArguments.size() == 2) {
                final FunctionCalls len = new FunctionCalls();
                final TableColumn innerFunction = new TableColumn();
                innerFunction.setOwnerName(this.functionName.getOwnerName());
                innerFunction.setTableName(this.functionName.getTableName());
                innerFunction.setColumnName("LEN");
                final Vector argList = new Vector();
                argList.add(this.functionArguments.get(0));
                len.setFunctionArguments(argList);
                len.setFunctionName(innerFunction);
                this.functionArguments.add(len);
            }
            else if (this.functionArguments.size() > 1 && this.functionArguments.get(1) instanceof SelectColumn && this.functionArguments.get(1).getColumnExpression().size() > 1) {
                final SelectColumn expression = this.functionArguments.get(1);
                if (expression.getColumnExpression().get(0) instanceof String) {
                    final String sign = expression.getColumnExpression().get(0);
                    if ("-".equalsIgnoreCase(sign)) {
                        expression.setOpenBrace("(");
                        expression.setCloseBrace(")");
                        final SelectColumn exp = new SelectColumn();
                        exp.setOpenBrace("(");
                        exp.setCloseBrace(")");
                        final Vector arg = new Vector();
                        final FunctionCalls len2 = new FunctionCalls();
                        final TableColumn innerFunction2 = new TableColumn();
                        innerFunction2.setOwnerName(this.functionName.getOwnerName());
                        innerFunction2.setTableName(this.functionName.getTableName());
                        innerFunction2.setColumnName("LEN");
                        final Vector argList2 = new Vector();
                        argList2.add(this.functionArguments.get(0));
                        len2.setFunctionArguments(argList2);
                        len2.setFunctionName(innerFunction2);
                        expression.addColumnExpressionElement("+");
                        expression.addColumnExpressionElement(len2);
                        expression.addColumnExpressionElement("+");
                        expression.addColumnExpressionElement("1");
                    }
                }
            }
        }
        if (originalFunctionName.equalsIgnoreCase("SUBSTRING") && this.functionArguments.size() == 1) {
            if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
                this.setFromInTrim(",");
            }
            if (this.getForLength() != null) {
                if (this.getForLength().equalsIgnoreCase("FOR")) {
                    this.setForLength(",");
                }
            }
            else if (this.trailingString != null) {
                final FunctionCalls len = new FunctionCalls();
                final TableColumn innerFunction = new TableColumn();
                innerFunction.setOwnerName(this.functionName.getOwnerName());
                innerFunction.setTableName(this.functionName.getTableName());
                innerFunction.setColumnName("LEN");
                final Vector argList = new Vector();
                argList.add(this.trailingString);
                len.setFunctionArguments(argList);
                len.setFunctionName(innerFunction);
                this.functionArguments.add(len);
            }
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        this.functionName.setColumnName("SUBSTRING");
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
        if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
            if (this.functionArguments.size() == 2) {
                final FunctionCalls len = new FunctionCalls();
                final TableColumn innerFunction = new TableColumn();
                innerFunction.setOwnerName(this.functionName.getOwnerName());
                innerFunction.setTableName(this.functionName.getTableName());
                innerFunction.setColumnName("LEN");
                final Vector argList = new Vector();
                argList.add(this.functionArguments.get(0));
                len.setFunctionArguments(argList);
                len.setFunctionName(innerFunction);
                this.functionArguments.add(len);
            }
            else if (this.functionArguments.size() > 1 && this.functionArguments.get(1) instanceof SelectColumn && this.functionArguments.get(1).getColumnExpression().size() > 1) {
                final SelectColumn expression = this.functionArguments.get(1);
                if (expression.getColumnExpression().get(0) instanceof String) {
                    final String sign = expression.getColumnExpression().get(0);
                    if ("-".equalsIgnoreCase(sign)) {
                        expression.setOpenBrace("(");
                        expression.setCloseBrace(")");
                        final SelectColumn exp = new SelectColumn();
                        exp.setOpenBrace("(");
                        exp.setCloseBrace(")");
                        final Vector arg = new Vector();
                        final FunctionCalls len2 = new FunctionCalls();
                        final TableColumn innerFunction2 = new TableColumn();
                        innerFunction2.setOwnerName(this.functionName.getOwnerName());
                        innerFunction2.setTableName(this.functionName.getTableName());
                        innerFunction2.setColumnName("LEN");
                        final Vector argList2 = new Vector();
                        argList2.add(this.functionArguments.get(0));
                        len2.setFunctionArguments(argList2);
                        len2.setFunctionName(innerFunction2);
                        expression.addColumnExpressionElement("+");
                        expression.addColumnExpressionElement(len2);
                        expression.addColumnExpressionElement("+");
                        expression.addColumnExpressionElement("1");
                    }
                }
            }
        }
        if (originalFunctionName.equalsIgnoreCase("SUBSTRING") && this.functionArguments.size() == 1) {
            if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
                this.setFromInTrim(",");
            }
            if (this.getForLength() != null) {
                if (this.getForLength().equalsIgnoreCase("FOR")) {
                    this.setForLength(",");
                }
            }
            else if (this.trailingString != null) {
                final FunctionCalls len = new FunctionCalls();
                final TableColumn innerFunction = new TableColumn();
                innerFunction.setOwnerName(this.functionName.getOwnerName());
                innerFunction.setTableName(this.functionName.getTableName());
                innerFunction.setColumnName("LEN");
                final Vector argList = new Vector();
                argList.add(this.trailingString);
                len.setFunctionArguments(argList);
                len.setFunctionName(innerFunction);
                this.functionArguments.add(len);
            }
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        this.functionName.setColumnName("SUBSTR");
        final int direction = 0;
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.size() == 3 && this.functionArguments.elementAt(0) != null && this.functionArguments.elementAt(0) instanceof SelectColumn) {
                final Object selcolobj = this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs);
                if (selcolobj != null) {
                    final String str = selcolobj.toString();
                    if (str.equalsIgnoreCase("CURRENT TIMESTAMP") && this.functionArguments.elementAt(1) != null && this.functionArguments.elementAt(1).toString().equalsIgnoreCase("1") && this.functionArguments.elementAt(2) != null && this.functionArguments.elementAt(2).toString().equalsIgnoreCase("10")) {
                        this.functionName.setColumnName("CHAR");
                        arguments.addElement("CURRENT DATE");
                        break;
                    }
                }
            }
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn obj = this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs);
                if (obj.toString().equalsIgnoreCase("CURRENT TIMESTAMP")) {
                    final FunctionCalls fn = new FunctionCalls();
                    final TableColumn tc = new TableColumn();
                    tc.setColumnName("CHAR");
                    final Vector fnArgs = new Vector();
                    fnArgs.addElement(obj);
                    fn.setFunctionArguments(fnArgs);
                    arguments.addElement(fn);
                }
                else {
                    arguments.addElement(obj);
                }
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
            if (this.functionArguments.size() > 1 && this.functionArguments.get(1).getColumnExpression().size() > 1) {
                final SelectColumn expression = this.functionArguments.get(1);
                if (expression.getColumnExpression().get(0) instanceof String) {
                    final String sign = expression.getColumnExpression().get(0);
                    if ("-".equalsIgnoreCase(sign)) {
                        expression.setOpenBrace("(");
                        expression.setCloseBrace(")");
                        final SelectColumn exp = new SelectColumn();
                        exp.setOpenBrace("(");
                        exp.setCloseBrace(")");
                        final Vector arg = new Vector();
                        final FunctionCalls len = new FunctionCalls();
                        final TableColumn innerFunction = new TableColumn();
                        innerFunction.setOwnerName(this.functionName.getOwnerName());
                        innerFunction.setTableName(this.functionName.getTableName());
                        innerFunction.setColumnName("LENGTH");
                        final Vector argList = new Vector();
                        argList.add(this.functionArguments.get(0));
                        len.setFunctionArguments(argList);
                        len.setFunctionName(innerFunction);
                        expression.addColumnExpressionElement("+");
                        expression.addColumnExpressionElement(len);
                        expression.addColumnExpressionElement("+");
                        expression.addColumnExpressionElement("1");
                    }
                }
            }
        }
        else if (originalFunctionName.equalsIgnoreCase("SUBSTRING")) {
            if (this.functionArguments.size() > 1) {
                final SelectColumn expression2 = this.functionArguments.get(1);
                final SelectColumn expression3 = this.functionArguments.get(2);
                if (expression2.getColumnExpression().get(0) instanceof String) {
                    String startAt = new String();
                    for (int i = 0; i < expression2.getColumnExpression().size(); ++i) {
                        startAt += expression2.getColumnExpression().get(i);
                    }
                    String noOfChar = new String();
                    for (int j = 0; j < expression3.getColumnExpression().size(); ++j) {
                        noOfChar += expression3.getColumnExpression().get(j).toString();
                    }
                    try {
                        if (Integer.parseInt(startAt) <= 0) {
                            final int newNoOfChar = Integer.parseInt(noOfChar) + Integer.parseInt(startAt) - 1;
                            this.functionArguments.remove(1);
                            this.functionArguments.add(1, "1");
                            final String numOfCharStr = "" + newNoOfChar;
                            this.functionArguments.remove(2);
                            this.functionArguments.add(2, numOfCharStr);
                        }
                    }
                    catch (final NumberFormatException ex) {}
                }
            }
            else {
                if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
                    this.setFromInTrim(",");
                }
                if (this.getForLength() != null && this.getForLength().equalsIgnoreCase("FOR")) {
                    this.setForLength(",");
                }
            }
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        final int direction = 0;
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                Object arg = null;
                if (i_count == 0) {
                    sc.convertSelectColumnToTextDataType();
                    arg = sc.toPostgreSQLSelect(to_sqs, from_sqs);
                }
                else {
                    arg = sc.toPostgreSQLSelect(to_sqs, from_sqs);
                    final Integer index = StringFunctions.getIntegerValue(arg.toString());
                    if (index != null) {
                        arg = index.toString();
                    }
                    else if (from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForText() && from_sqs.canUseUDFFunctionsForNumeric()) {
                        arg = "TOINTEGER_UDF(" + arg.toString() + ")";
                    }
                }
                arguments.addElement(arg);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForText()) {
            this.functionName.setColumnName("SUBSTRING_UDF");
        }
        else {
            String qry = "";
            if (this.functionArguments.size() == 2) {
                qry = "( CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN CAST(NULL AS TEXT) WHEN " + arguments.get(1).toString() + " <=0 THEN SUBSTRING(" + arguments.get(0) + ",LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE SUBSTRING(" + arguments.get(0) + "," + arguments.get(1) + ") END )";
                try {
                    final int index2 = Integer.parseInt(arguments.get(1).toString());
                    if (index2 > 0) {
                        qry = "SUBSTRING(" + arguments.get(0) + "," + index2 + ")";
                    }
                    else if (index2 == 0) {
                        qry = "CAST(NULL AS TEXT)";
                    }
                }
                catch (final Exception ex) {}
                this.functionName.setColumnName(qry);
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (this.functionArguments.size() == 3) {
                qry = "( CASE WHEN " + arguments.get(2) + " <=0 THEN NULL WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN CAST(NULL AS TEXT) ELSE SUBSTRING(" + arguments.get(0) + ", CASE WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END,  " + arguments.get(2) + ") END )";
                try {
                    Integer index3 = null;
                    Integer length = null;
                    try {
                        index3 = Integer.parseInt(arguments.get(1).toString());
                    }
                    catch (final Exception ex2) {}
                    try {
                        length = Integer.parseInt(arguments.get(2).toString());
                    }
                    catch (final Exception ex3) {}
                    if (index3 != null && length != null) {
                        if (index3 > 0 && length > 0) {
                            qry = "SUBSTRING(" + arguments.get(0) + "," + index3 + "," + length + ")";
                        }
                        else if (index3 == 0 || length == 0) {
                            qry = "CAST(NULL AS TEXT)";
                        }
                        else if (length > 0) {
                            qry = "SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ")";
                        }
                    }
                    else if (index3 != null && index3 == 0) {
                        qry = "CAST(NULL AS TEXT)";
                    }
                    else if (length != null) {
                        if (length == 0) {
                            qry = "CAST(NULL AS TEXT)";
                        }
                        else if (length > 0) {
                            qry = "SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ")";
                        }
                    }
                }
                catch (final Exception ex4) {}
                this.functionName.setColumnName(qry);
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        this.functionName.setColumnName("SUBSTRING");
        final int direction = 0;
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (originalFunctionName.equalsIgnoreCase("SUBSTR") && this.functionArguments.get(1).getColumnExpression().size() > 1) {
            final SelectColumn expression = this.functionArguments.get(1);
            if (expression.getColumnExpression().get(0) instanceof String) {
                final String sign = expression.getColumnExpression().get(0);
                if ("-".equalsIgnoreCase(sign)) {
                    expression.setOpenBrace("(");
                    expression.setCloseBrace(")");
                    final SelectColumn exp = new SelectColumn();
                    exp.setOpenBrace("(");
                    exp.setCloseBrace(")");
                    final Vector arg = new Vector();
                    final FunctionCalls len = new FunctionCalls();
                    final TableColumn innerFunction = new TableColumn();
                    innerFunction.setOwnerName(this.functionName.getOwnerName());
                    innerFunction.setTableName(this.functionName.getTableName());
                    innerFunction.setColumnName("LENGTH");
                    final Vector argList = new Vector();
                    argList.add(this.functionArguments.get(0));
                    len.setFunctionArguments(argList);
                    len.setFunctionName(innerFunction);
                    expression.addColumnExpressionElement("+");
                    expression.addColumnExpressionElement(len);
                    expression.addColumnExpressionElement("+");
                    expression.addColumnExpressionElement("1");
                }
            }
        }
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        final int direction = 0;
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
        if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
            this.functionName.setColumnName("SUBSTR");
            if (this.functionArguments.size() > 1 && this.functionArguments.get(1).getColumnExpression().size() > 1) {
                final SelectColumn expression = this.functionArguments.get(1);
                if (expression.getColumnExpression().get(0) instanceof String) {
                    final String sign = expression.getColumnExpression().get(0);
                    if ("-".equalsIgnoreCase(sign)) {
                        expression.setOpenBrace("(");
                        expression.setCloseBrace(")");
                        final SelectColumn exp = new SelectColumn();
                        exp.setOpenBrace("(");
                        exp.setCloseBrace(")");
                        final Vector arg = new Vector();
                        final FunctionCalls len = new FunctionCalls();
                        final TableColumn innerFunction = new TableColumn();
                        innerFunction.setOwnerName(this.functionName.getOwnerName());
                        innerFunction.setTableName(this.functionName.getTableName());
                        innerFunction.setColumnName("LENGTH");
                        final Vector argList = new Vector();
                        argList.add(this.functionArguments.get(0));
                        len.setFunctionArguments(argList);
                        len.setFunctionName(innerFunction);
                        expression.addColumnExpressionElement("+");
                        expression.addColumnExpressionElement(len);
                        expression.addColumnExpressionElement("+");
                        expression.addColumnExpressionElement("1");
                    }
                }
            }
        }
        else if (originalFunctionName.equalsIgnoreCase("SUBSTRING")) {
            this.functionName.setColumnName("SUBSTRING");
            if (this.functionArguments.size() > 1) {
                this.functionName.setColumnName("SUBSTR");
                if (this.functionArguments.get(1).getColumnExpression().size() > 1) {
                    final SelectColumn expression = this.functionArguments.get(1);
                    if (expression.getColumnExpression().get(0) instanceof String) {
                        final String sign = expression.getColumnExpression().get(0);
                        if ("-".equalsIgnoreCase(sign)) {
                            expression.setOpenBrace("(");
                            expression.setCloseBrace(")");
                            final SelectColumn exp = new SelectColumn();
                            exp.setOpenBrace("(");
                            exp.setCloseBrace(")");
                            final Vector arg = new Vector();
                            final FunctionCalls len = new FunctionCalls();
                            final TableColumn innerFunction = new TableColumn();
                            innerFunction.setOwnerName(this.functionName.getOwnerName());
                            innerFunction.setTableName(this.functionName.getTableName());
                            innerFunction.setColumnName("LENGTH");
                            final Vector argList = new Vector();
                            argList.add(this.functionArguments.get(0));
                            len.setFunctionArguments(argList);
                            len.setFunctionName(innerFunction);
                            expression.addColumnExpressionElement("+");
                            expression.addColumnExpressionElement(len);
                            expression.addColumnExpressionElement("+");
                            expression.addColumnExpressionElement("1");
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnName = this.functionName.getColumnName();
        if (fnName.equalsIgnoreCase("substr") || fnName.equalsIgnoreCase("substrb")) {
            this.functionName.setColumnName("SUBSTRING");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (SwisSQLOptions.useANSIFormatForSubString) {
            if (arguments.size() > 2 && arguments.get(2) != null) {
                arguments.insertElementAt("FOR", 2);
            }
            arguments.insertElementAt(" FROM ", 1);
            this.setStripComma(true);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUBSTRING");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                final Vector colExpr = sc.getColumnExpression();
                if (colExpr.size() == 1 && colExpr.get(0) instanceof String && i_count == 0 && this.functionArguments.elementAt(1).getColumnExpression().elementAt(0) instanceof String && this.functionArguments.elementAt(2).getColumnExpression().elementAt(0) instanceof String) {
                    this.functionName.setColumnName("");
                    this.setOpenBracesForFunctionNameRequired(false);
                    final String str = colExpr.get(0).toString().substring(1, colExpr.get(0).toString().length() - 1);
                    final int start = Integer.parseInt(this.functionArguments.elementAt(1).toString()) - 1;
                    final int length = Integer.parseInt(this.functionArguments.elementAt(2).toString());
                    int end = start + length;
                    if (end > str.length()) {
                        end = str.length();
                    }
                    arguments.add("'" + str.substring(start, end) + "'");
                }
                else if (!this.functionName.getColumnName().equalsIgnoreCase("")) {
                    throw new ConvertException("\nThe function SUBSTRING is not supported in TimesTen 5.1.21\n");
                }
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        this.functionName.setColumnName("SUBSTR");
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
        if (originalFunctionName.equalsIgnoreCase("SUBSTRING")) {
            if (this.functionArguments.size() > 2) {
                final SelectColumn expression1 = this.functionArguments.get(1);
                final SelectColumn expression2 = this.functionArguments.get(2);
                if (expression1.getColumnExpression().get(0) instanceof String) {
                    String startAt = new String();
                    for (int i = 0; i < expression1.getColumnExpression().size(); ++i) {
                        startAt += expression1.getColumnExpression().get(i).toString();
                    }
                    String noOfChar = new String();
                    for (int j = 0; j < expression2.getColumnExpression().size(); ++j) {
                        noOfChar += expression2.getColumnExpression().get(j).toString();
                    }
                    try {
                        if (Integer.parseInt(startAt) <= 0) {
                            final int newNoOfChar = Integer.parseInt(noOfChar) + Integer.parseInt(startAt) - 1;
                            this.functionArguments.remove(1);
                            this.functionArguments.add(1, "1");
                            final String numOfCharStr = "" + newNoOfChar;
                            this.functionArguments.remove(2);
                            this.functionArguments.add(2, numOfCharStr);
                        }
                    }
                    catch (final NumberFormatException ex) {}
                }
            }
            else {
                if (this.getFromInTrim() != null && this.getFromInTrim().equalsIgnoreCase("FROM")) {
                    this.setFromInTrim(",");
                }
                if (this.getForLength() != null && this.getForLength().equalsIgnoreCase("FOR")) {
                    this.setForLength(",");
                }
            }
        }
        else if (originalFunctionName.equalsIgnoreCase("SUBSTR")) {
            try {
                if (this.functionArguments.size() > 2) {
                    final SelectColumn expression1 = this.functionArguments.get(1);
                    if (expression1.getColumnExpression().get(0) instanceof String) {
                        String startAt2 = new String();
                        for (int k = 0; k < expression1.getColumnExpression().size(); ++k) {
                            startAt2 += expression1.getColumnExpression().get(k).toString();
                        }
                        final int startAtNum = Integer.parseInt(startAt2);
                        if (startAtNum <= 0) {
                            this.functionArguments.remove(1);
                            this.functionArguments.add(1, "1");
                        }
                    }
                }
            }
            catch (final NumberFormatException ex2) {}
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnName = this.functionName.getColumnName();
        if (fnName.equalsIgnoreCase("substr") || fnName.equalsIgnoreCase("substrb")) {
            this.functionName.setColumnName("SUBSTR");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() > 1 && arguments.get(1) != null) {
            final Object arg2 = arguments.get(1);
            if (arg2 instanceof SelectColumn) {
                final SelectColumn arg2Col = (SelectColumn)arg2;
                if (arg2Col.getColumnExpression().size() == 1 && arg2Col.getColumnExpression().get(0).toString().equalsIgnoreCase("0")) {
                    arg2Col.getColumnExpression().setElementAt("1", 0);
                }
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("0")) {
                arguments.setElementAt("1", 1);
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String originalFunctionName = this.functionName.getColumnName();
        this.functionName.setColumnName("SUBSTRING");
        final int direction = 0;
        String argStr = "";
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count == 0) {
                    sc.convertSelectColumnToTextDataType();
                }
                arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 2) {
            argStr = "SUBSTRING(" + arguments.get(0) + ", CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END)";
            try {
                final int index = Integer.parseInt(arguments.get(1).toString());
                if (index > 0) {
                    argStr = "SUBSTRING(" + arguments.get(0) + "," + index + ")";
                }
                else if (index == 0) {
                    argStr = "CAST(NULL AS VARCHAR)";
                }
            }
            catch (final Exception ex) {}
        }
        else if (arguments.size() == 3) {
            argStr = "( CASE WHEN " + arguments.get(2) + " <=0 THEN NULL ELSE SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ") END )";
            try {
                Integer index2 = null;
                Integer length = null;
                try {
                    index2 = Integer.parseInt(arguments.get(1).toString());
                }
                catch (final Exception ex2) {}
                try {
                    length = Integer.parseInt(arguments.get(2).toString());
                }
                catch (final Exception ex3) {}
                if (index2 != null && length != null) {
                    if (index2 > 0 && length > 0) {
                        argStr = "SUBSTRING(" + arguments.get(0) + "," + index2 + "," + length + ")";
                    }
                    else if (index2 == 0 || length == 0) {
                        argStr = "CAST(NULL AS VARCHAR)";
                    }
                    else if (length > 0) {
                        argStr = "SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ")";
                    }
                }
                else if (index2 != null && index2 == 0) {
                    argStr = "CAST(NULL AS VARCHAR)";
                }
                else if (length != null) {
                    if (length == 0) {
                        argStr = "CAST(NULL AS VARCHAR)";
                    }
                    else if (length > 0) {
                        argStr = "SUBSTRING(" + arguments.get(0) + ", (CASE WHEN ABS(" + arguments.get(1).toString() + ") > LENGTH(" + arguments.get(0).toString() + ") THEN NULL WHEN " + arguments.get(1) + " <=0 THEN (LENGTH(" + arguments.get(0) + ")+(" + arguments.get(1) + ")+1) ELSE " + arguments.get(1) + " END),  " + arguments.get(2) + ")";
                    }
                }
            }
            catch (final Exception ex4) {}
        }
        this.functionName.setColumnName(argStr);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
