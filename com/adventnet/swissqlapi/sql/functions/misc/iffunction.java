package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class iffunction extends FunctionCalls
{
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement caseStmt = new CaseStatement();
        final WhenStatement whenStmt = new WhenStatement();
        final WhereExpression whereExpr = new WhereExpression();
        final WhereItem whereItem = new WhereItem();
        final WhereColumn whereCln = new WhereColumn();
        caseStmt.setCaseClause("CASE");
        whenStmt.setWhenClause("WHEN");
        whenStmt.setThenClause("THEN");
        caseStmt.setElseClause("ELSE");
        caseStmt.setEndClause("END");
        final Vector whenStmtList = new Vector();
        final Vector whereItemVector = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.get(i) instanceof SelectColumn) {
                SelectColumn sc = this.functionArguments.get(i);
                switch (i) {
                    case 0: {
                        sc = sc.toMSSQLServerSelect(to_sqs, from_sqs);
                        whereCln.setColumnExpression(sc.getColumnExpression());
                        whereItem.setLeftWhereExp(whereCln);
                        whereItemVector.add(whereItem);
                        whereExpr.setWhereItem(whereItemVector);
                        whenStmt.setWhenCondition(whereExpr);
                        break;
                    }
                    case 1: {
                        whenStmt.setThenStatement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
                        whenStmtList.add(whenStmt);
                        caseStmt.setWhenStatementList(whenStmtList);
                        break;
                    }
                    case 2: {
                        caseStmt.setElseStatement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
                        break;
                    }
                }
            }
        }
        final SelectColumn caseStmtSC = new SelectColumn();
        final Vector selectColumnExpr = new Vector();
        selectColumnExpr.add(caseStmt);
        caseStmtSC.setColumnExpression(selectColumnExpr);
        final Vector funcArgVector = new Vector();
        funcArgVector.add(caseStmtSC);
        this.setOpenBracesForFunctionNameRequired(false);
        this.setFunctionName(null);
        this.setFunctionArguments(funcArgVector);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("IF");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs, true));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i);
                final Vector v = sc.getColumnExpression();
                if (i == 0 && v != null && v.size() == 1 && !(v.get(0) instanceof WhereItem) && !(v.get(0) instanceof WhereExpression)) {
                    Vector colExp = null;
                    Object object = null;
                    boolean isTableColumn = false;
                    boolean isListedFunction = false;
                    boolean isListedStringFunction = false;
                    boolean isDateSelColExp = false;
                    if (v.get(0) instanceof SelectColumn) {
                        colExp = v.get(0).getColumnExpression();
                        if (colExp != null) {
                            if (colExp.size() == 1) {
                                object = colExp.get(0);
                            }
                            else if (colExp.size() == 5) {
                                final Object arg1 = colExp.get(0);
                                final Object arg2 = colExp.get(4);
                                if (arg1 instanceof FunctionCalls && ((FunctionCalls)arg1).getFunctionNameAsAString() != null && ((FunctionCalls)arg1).getFunctionNameAsAString().trim().equalsIgnoreCase("FROM_UNIXTIME") && arg2 instanceof String && arg2.toString().trim().equalsIgnoreCase("microsecond")) {
                                    isDateSelColExp = true;
                                }
                            }
                        }
                    }
                    else {
                        object = v.get(0);
                    }
                    if (object != null) {
                        if (object instanceof TableColumn) {
                            isTableColumn = true;
                        }
                        else if (object instanceof FunctionCalls) {
                            final TableColumn newfunctionName = ((FunctionCalls)object).getFunctionName();
                            final Vector fnList = this.getFunctionListsForMissingWhereItems();
                            final Vector strFnList = this.getStringFunctionListsForMissingWhereItems();
                            if (newfunctionName != null && newfunctionName.getColumnName() != null) {
                                if (this.ifFunctionWithNumberArguments(newfunctionName.getColumnName().trim(), ((FunctionCalls)object).getFunctionArguments())) {
                                    isListedFunction = true;
                                }
                                else if (fnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                                    isListedFunction = true;
                                }
                                else if (strFnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                                    isListedStringFunction = true;
                                }
                            }
                        }
                    }
                    if (isTableColumn || isListedFunction || isListedStringFunction || isDateSelColExp) {
                        final WhereItem wi = new WhereItem();
                        final WhereColumn wcL = new WhereColumn();
                        wcL.setColumnExpression(v);
                        wi.setLeftWhereExp(wcL);
                        if (isDateSelColExp || isListedStringFunction) {
                            wi.setOperator("IS NOT NULL");
                        }
                        else {
                            final Vector vec = new Vector();
                            vec.add("0");
                            final WhereColumn wcR = new WhereColumn();
                            wcR.setColumnExpression(vec);
                            wi.setRightWhereExp(wcR);
                            wi.setOperator(">");
                        }
                        final Vector v2 = new Vector();
                        v2.add(wi);
                        sc.setColumnExpression(v2);
                    }
                }
                this.typeCastToCharForEmptyString(i, arguments, to_sqs, from_sqs, "VW");
                arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("IF");
        final Vector arguments = new Vector();
        boolean castAllArgs = this.castToTextInsideIf() && to_sqs != null && to_sqs.canCastAllToTextColumns();
        if (!castAllArgs) {
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (i != 0) {
                    final SelectColumn sc = this.functionArguments.elementAt(i);
                    if (sc.needsCastingForStringLiteralsInsideIfFunction()) {
                        castAllArgs = true;
                        break;
                    }
                }
            }
        }
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                final Vector v = sc.getColumnExpression();
                if (i_count == 0 && v != null && v.size() == 1 && !(v.get(0) instanceof WhereItem) && !(v.get(0) instanceof WhereExpression)) {
                    Vector colExp = null;
                    Object object = null;
                    boolean isTableColumn = false;
                    boolean isListedFunction = false;
                    boolean isListedStringFunction = false;
                    boolean isDateSelColExp = false;
                    if (v.get(0) instanceof SelectColumn) {
                        colExp = v.get(0).getColumnExpression();
                        if (colExp != null) {
                            if (colExp.size() == 1) {
                                object = colExp.get(0);
                            }
                            else if (colExp.size() == 5) {
                                final Object arg1 = colExp.get(0);
                                final Object arg2 = colExp.get(4);
                                if (arg1 instanceof FunctionCalls && ((FunctionCalls)arg1).getFunctionNameAsAString() != null && ((FunctionCalls)arg1).getFunctionNameAsAString().trim().equalsIgnoreCase("FROM_UNIXTIME") && arg2 instanceof String && arg2.toString().trim().equalsIgnoreCase("microsecond")) {
                                    isDateSelColExp = true;
                                }
                            }
                        }
                    }
                    else {
                        object = v.get(0);
                    }
                    if (object != null) {
                        if (object instanceof TableColumn) {
                            isTableColumn = true;
                        }
                        else if (object instanceof FunctionCalls) {
                            final TableColumn newfunctionName = ((FunctionCalls)object).getFunctionName();
                            final Vector fnList = this.getFunctionListsForMissingWhereItems();
                            final Vector strFnList = this.getStringFunctionListsForMissingWhereItems();
                            if (newfunctionName != null && newfunctionName.getColumnName() != null) {
                                if (this.ifFunctionWithNumberArguments(newfunctionName.getColumnName().trim(), ((FunctionCalls)object).getFunctionArguments())) {
                                    isListedFunction = true;
                                }
                                else if (fnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                                    isListedFunction = true;
                                }
                                else if (strFnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                                    isListedStringFunction = true;
                                }
                            }
                        }
                    }
                    if (isTableColumn || isListedFunction || isListedStringFunction || isDateSelColExp) {
                        final WhereItem wi = new WhereItem();
                        final WhereColumn wcL = new WhereColumn();
                        wcL.setColumnExpression(v);
                        wi.setLeftWhereExp(wcL);
                        if (isDateSelColExp || isListedStringFunction) {
                            wi.setOperator("IS NOT NULL");
                        }
                        else {
                            final Vector vec = new Vector();
                            vec.add("0");
                            final WhereColumn wcR = new WhereColumn();
                            wcR.setColumnExpression(vec);
                            wi.setRightWhereExp(wcR);
                            wi.setOperator(">");
                        }
                        final Vector v2 = new Vector();
                        v2.add(wi);
                        sc.setColumnExpression(v2);
                    }
                }
                if (i_count != 0 && castAllArgs) {
                    sc.convertSelectColumnArgsToTextDataType(true);
                }
                arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (to_sqs != null) {
            to_sqs.addCurrentIndexToIfFunctionList();
        }
        if (from_sqs == null || !from_sqs.canUseIFFunctionForPGCaseWhenExp() || from_sqs.isAmazonRedShift()) {
            String finalElseString;
            final String elseArgString = finalElseString = arguments.get(2).toString().trim();
            String elseString = " ELSE ";
            try {
                if (elseArgString.toUpperCase().startsWith("(CASE WHEN") && elseArgString.toUpperCase().endsWith("END)")) {
                    finalElseString = elseArgString.substring(5, elseArgString.length() - 4);
                    elseString = "";
                }
            }
            catch (final Exception ex) {}
            final String qry = "(CASE WHEN " + arguments.get(0) + " THEN " + arguments.get(1) + elseString + finalElseString + " END)";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("IF");
        final Vector arguments = new Vector();
        boolean castAllArgs = this.castToTextInsideIf() && to_sqs != null && to_sqs.canCastAllToTextColumns();
        if (!castAllArgs) {
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (i != 0) {
                    final SelectColumn sc = this.functionArguments.elementAt(i);
                    if (sc.needsCastingForStringLiteralsInsideIfFunction()) {
                        castAllArgs = true;
                        break;
                    }
                }
            }
        }
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                final Vector v = sc.getColumnExpression();
                if (i_count == 0 && v != null && v.size() == 1 && !(v.get(0) instanceof WhereItem) && !(v.get(0) instanceof WhereExpression)) {
                    Vector colExp = null;
                    Object object = null;
                    boolean isTableColumn = false;
                    boolean isListedFunction = false;
                    boolean isListedStringFunction = false;
                    boolean isDateSelColExp = false;
                    if (v.get(0) instanceof SelectColumn) {
                        colExp = v.get(0).getColumnExpression();
                        if (colExp != null) {
                            if (colExp.size() == 1) {
                                object = colExp.get(0);
                            }
                            else if (colExp.size() == 5) {
                                final Object arg1 = colExp.get(0);
                                final Object arg2 = colExp.get(4);
                                if (arg1 instanceof FunctionCalls && ((FunctionCalls)arg1).getFunctionNameAsAString() != null && ((FunctionCalls)arg1).getFunctionNameAsAString().trim().equalsIgnoreCase("FROM_UNIXTIME") && arg2 instanceof String && arg2.toString().trim().equalsIgnoreCase("microsecond")) {
                                    isDateSelColExp = true;
                                }
                            }
                        }
                    }
                    else {
                        object = v.get(0);
                    }
                    if (object != null) {
                        if (object instanceof TableColumn) {
                            isTableColumn = true;
                        }
                        else if (object instanceof FunctionCalls) {
                            final TableColumn newfunctionName = ((FunctionCalls)object).getFunctionName();
                            final Vector fnList = this.getFunctionListsForMissingWhereItems();
                            final Vector strFnList = this.getStringFunctionListsForMissingWhereItems();
                            if (newfunctionName != null && newfunctionName.getColumnName() != null) {
                                if (this.ifFunctionWithNumberArguments(newfunctionName.getColumnName().trim(), ((FunctionCalls)object).getFunctionArguments())) {
                                    isListedFunction = true;
                                }
                                else if (fnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                                    isListedFunction = true;
                                }
                                else if (strFnList.contains(newfunctionName.getColumnName().trim().toUpperCase())) {
                                    isListedStringFunction = true;
                                }
                            }
                        }
                    }
                    if (isTableColumn || isListedFunction || isListedStringFunction || isDateSelColExp) {
                        final WhereItem wi = new WhereItem();
                        final WhereColumn wcL = new WhereColumn();
                        wcL.setColumnExpression(v);
                        wi.setLeftWhereExp(wcL);
                        if (isDateSelColExp || isListedStringFunction) {
                            wi.setOperator("IS NOT NULL");
                        }
                        else {
                            final Vector vec = new Vector();
                            vec.add("0");
                            final WhereColumn wcR = new WhereColumn();
                            wcR.setColumnExpression(vec);
                            wi.setRightWhereExp(wcR);
                            wi.setOperator(">");
                        }
                        final Vector v2 = new Vector();
                        v2.add(wi);
                        sc.setColumnExpression(v2);
                    }
                }
                if (i_count != 0 && castAllArgs) {
                    sc.convertSelectColumnArgsToTextDataType(true);
                }
                arguments.addElement(sc.toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (to_sqs != null) {
            to_sqs.addCurrentIndexToIfFunctionList();
        }
        if (from_sqs == null || from_sqs.isOracleLive()) {
            String finalElseString;
            final String elseArgString = finalElseString = arguments.get(2).toString().trim();
            String elseString = " ELSE ";
            try {
                if (elseArgString.toUpperCase().startsWith("(CASE WHEN") && elseArgString.toUpperCase().endsWith("END)")) {
                    finalElseString = elseArgString.substring(5, elseArgString.length() - 4);
                    elseString = "";
                }
            }
            catch (final Exception ex) {}
            final String qry = "(CASE WHEN " + arguments.get(0) + " THEN " + arguments.get(1) + elseString + finalElseString + " END)";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    public boolean ifFunctionWithNumberArguments(final String functionName, final Vector fnArgs) {
        try {
            if (functionName.trim().equalsIgnoreCase("IF") && fnArgs != null && fnArgs.size() == 3 && fnArgs.get(1) instanceof SelectColumn && fnArgs.get(2) instanceof SelectColumn) {
                final SelectColumn sc2 = fnArgs.get(1);
                final SelectColumn sc3 = fnArgs.get(2);
                if (sc2 != null && sc3 != null && sc2.getColumnExpression() != null && sc3.getColumnExpression() != null && sc2.getColumnExpression().size() == 1 && sc3.getColumnExpression().size() == 1 && sc2.getColumnExpression().get(0) instanceof String && sc3.getColumnExpression().get(0) instanceof String) {
                    String sc2value = sc2.getColumnExpression().get(0);
                    String sc3value = sc3.getColumnExpression().get(0);
                    try {
                        if (sc2value != null && sc3value != null) {
                            int nullcount = 0;
                            if (sc2value.equalsIgnoreCase("null")) {
                                sc2value = "0";
                                ++nullcount;
                            }
                            if (sc3value.equalsIgnoreCase("null")) {
                                sc3value = "0";
                                ++nullcount;
                            }
                            if (nullcount != 2) {
                                sc2value = sc2value.replaceAll("'", "");
                                sc3value = sc3value.replaceAll("'", "");
                                Double.parseDouble(sc2value);
                                Double.parseDouble(sc3value);
                                return true;
                            }
                        }
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        catch (final Exception ex2) {}
        return false;
    }
    
    public void typeCastToCharForEmptyString(final int argPosition, final Vector arguments, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final String dbType) {
        try {
            if (argPosition > 0) {
                final int replacePos = (argPosition == 1) ? 2 : 1;
                final SelectColumn selCol = this.functionArguments.elementAt(argPosition);
                final boolean needsCasting = selCol.needsCastingForStringLiterals(true);
                if (needsCasting) {
                    final SelectColumn sc = this.functionArguments.elementAt(replacePos);
                    sc.convertSelectColumnToTextDataType();
                    if (argPosition == 2) {
                        if (dbType.equalsIgnoreCase("VW")) {
                            arguments.setElementAt(this.functionArguments.elementAt(replacePos).toVectorWiseSelect(to_sqs, from_sqs), replacePos);
                        }
                        else {
                            arguments.setElementAt(this.functionArguments.elementAt(replacePos).toPostgreSQLSelect(to_sqs, from_sqs), replacePos);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    public Vector getStringFunctionListsForMissingWhereItems() {
        final Vector fnList = new Vector();
        fnList.add("CONCAT");
        fnList.add("CONCAT_WS");
        fnList.add("STRING_CONCAT");
        fnList.add("SUBSTRING");
        fnList.add("SUBSTR");
        fnList.add("SUBSTRING_UDF");
        fnList.add("SUBSTRING_INDEX");
        fnList.add("UPPER");
        fnList.add("LOWER");
        fnList.add("LEFT");
        fnList.add("LEFT_UDF");
        fnList.add("RIGHT");
        fnList.add("RIGHT_UDF");
        fnList.add("REVERSE");
        fnList.add("REPEAT");
        fnList.add("REPLACE");
        fnList.add("TRIM");
        fnList.add("LTRIM");
        fnList.add("RTRIM");
        fnList.add("LPAD");
        fnList.add("RPAD");
        fnList.add("SOUNDEX");
        fnList.add("SPACE");
        return fnList;
    }
    
    public Vector getFunctionListsForMissingWhereItems() {
        final Vector fnList = new Vector();
        fnList.add("ISNULL");
        fnList.add("LENGTH");
        fnList.add("CHAR_LENGTH");
        fnList.add("CHARACTER_LENGTH");
        fnList.add("LOCATE");
        fnList.add("POSITION");
        fnList.add("TO_DECIMAL");
        fnList.add("TO_INTEGER");
        fnList.add("INDEXOF");
        fnList.add("INSTR");
        fnList.add("ABS");
        fnList.add("CEIL");
        fnList.add("CEILING");
        fnList.add("FLOOR");
        fnList.add("RAND");
        fnList.add("ROUND");
        fnList.add("AVG");
        fnList.add("MIN");
        fnList.add("MAX");
        fnList.add("SUM");
        fnList.add("COUNT");
        fnList.add("STD");
        fnList.add("VARIANCE");
        fnList.add("DATEDIFF");
        fnList.add("DAYOFMONTH");
        fnList.add("DAYOFYEAR");
        fnList.add("HOUR");
        fnList.add("LAST_DAY");
        fnList.add("MICROSECOND");
        fnList.add("MINUTE");
        fnList.add("MONTH");
        fnList.add("PERIOD_ADD");
        fnList.add("PERIOD_DIFF");
        fnList.add("QUARTER");
        fnList.add("TIMESTAMPDIFF");
        fnList.add("TIME_TO_SEC");
        fnList.add("TO_DAYS");
        fnList.add("UNIX_TIMESTAMP");
        fnList.add("WEEK");
        fnList.add("WEEKDAY");
        fnList.add("WEEKOFYEAR");
        fnList.add("YEAR");
        fnList.add("YEARWEEK");
        fnList.add("DATEANDTIMEDIFF");
        return fnList;
    }
}
