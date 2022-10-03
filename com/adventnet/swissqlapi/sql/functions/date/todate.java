package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class todate extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        this.functionName.setColumnName("CONVERT");
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
        if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn && this.functionArguments.get(0) instanceof SelectColumn) {
            final SelectColumn forDatetimeToBeChanged = this.functionArguments.get(0);
            final SelectColumn sc = this.functionArguments.get(1);
            String datetimeToBeChangedString = "";
            if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
                TableColumn tc = new TableColumn();
                tc = forDatetimeToBeChanged.getColumnExpression().get(0);
                datetimeToBeChangedString = tc.getColumnName();
            }
            else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof String) {
                datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0);
            }
            else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof FunctionCalls) {
                datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0).toString();
            }
            final Vector datetimeVector = new Vector();
            if (sc.getColumnExpression().get(0) instanceof String) {
                final String format = sc.getColumnExpression().get(0);
                if (format.trim().startsWith("'") && format.trim().endsWith("'")) {
                    if (FunctionCalls.charToIntName) {
                        if (format.trim().startsWith("'") && format.endsWith("'")) {
                            this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                            this.functionArguments.setElementAt("DATETIME", 0);
                            this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
                        }
                    }
                    else if (format.equalsIgnoreCase("'mon dd yyyy hh:miAM'") || format.equalsIgnoreCase("'mon dd yyyy hh:miPM'")) {
                        arguments.add(2, "100");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.equalsIgnoreCase("'mon dd yyyy hh:mi:ss:mmmAM'") || format.equalsIgnoreCase("'mon dd yyyy hh:mi:ss:mmmPM'")) {
                        arguments.add(2, "109");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.equalsIgnoreCase("'dd mon yyyy hh:mm:ss:mmm'")) {
                        arguments.add(2, "113");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.equalsIgnoreCase("'dd mon yy hh:mm:ss:mmm'")) {
                        arguments.add(2, "13");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("MM/DD/YYY") != -1) {
                        arguments.add(2, "101");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("MM/DD/YY") != -1) {
                        arguments.add(2, "1");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                        arguments.add(2, "102");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YY.MM.DD") != -1) {
                        arguments.add(2, "2");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD/MM/YYY") != -1) {
                        arguments.add(2, "103");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD/MM/YY") != -1) {
                        arguments.add(2, "3");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD.MM.YYY") != -1) {
                        arguments.add(2, "104");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD.MM.YY") != -1) {
                        arguments.add(2, "4");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD-MM-YYY") != -1) {
                        arguments.add(2, "105");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD-MM-YY") != -1) {
                        arguments.add(2, "5");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD MON YYYY") != -1) {
                        arguments.add(2, "106");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD MON YY") != -1) {
                        arguments.add(2, "6");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("MON DD, YYYY") != -1) {
                        arguments.add(2, "107");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("MON DD, YY") != -1) {
                        arguments.add(2, "7");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("HH:MM:SS") != -1) {
                        arguments.add(2, "108");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("MM-DD-YYY") != -1) {
                        arguments.add(2, "110");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("MM-DD-YY") != -1) {
                        arguments.add(2, "10");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YYY/MM/DD") != -1) {
                        arguments.add(2, "111");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YY/MM/DD") != -1) {
                        arguments.add(2, "11");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YYYYMMDD") != -1) {
                        arguments.add(2, "112");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YYMMDD") != -1) {
                        arguments.add(2, "12");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("HH:MI:SS:MMM") != -1) {
                        arguments.add(2, "114");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YYY-MM-DD") != -1) {
                        arguments.add(2, "121");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else {
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                }
                else {
                    if (FunctionCalls.charToIntName && format.trim().startsWith("@")) {
                        this.functionArguments.setElementAt("dbo.FetchSqlDtFormat(" + format + ")", 2);
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("DATETIME", 0);
                }
            }
            else if (sc.getColumnExpression().get(0) instanceof FunctionCalls) {
                arguments.add(2, this.functionArguments.get(1));
                if (FunctionCalls.charToIntName) {
                    final FunctionCalls fc = sc.getColumnExpression().get(0);
                    final TableColumn tc2 = fc.getFunctionName();
                    if (tc2.getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT") && fc.getFunctionArguments() != null && fc.getFunctionArguments().size() == 1) {
                        tc2.setColumnName("");
                        tc2.setTableName(null);
                        tc2.setOwnerName(null);
                    }
                    else if (fc.getFunctionArguments().size() == 0) {
                        if (sc.getColumnExpression().size() > 1 && sc.getColumnExpression().get(1) instanceof String && sc.getColumnExpression().get(1).trim().equals("+")) {
                            if (!this.ramcoSpecificDateFormatHandling(arguments)) {
                                arguments.setElementAt("dbo.FetchSqlDtFormat(" + this.functionArguments.get(1).toString() + ")", 2);
                            }
                        }
                        else {
                            tc2.setColumnName(tc2.getColumnName().trim() + "INT");
                        }
                    }
                    else {
                        this.processFunctionArgumentsForRamco(fc.getFunctionArguments());
                    }
                }
                arguments.setElementAt(this.functionArguments.get(0), 1);
                arguments.setElementAt("DATETIME", 0);
            }
            else {
                arguments.add(2, this.functionArguments.get(1));
                if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof SelectColumn) {
                    final SelectColumn selectcolumn = arguments.get(2);
                    final Vector expression = selectcolumn.getColumnExpression();
                    if (expression != null) {
                        for (int j = 0; j < expression.size(); ++j) {
                            if (expression.get(j) instanceof TableColumn) {
                                final TableColumn tc3 = expression.get(j);
                                String arg2 = tc3.getColumnName();
                                if (arg2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                                    String newformat = arg2.trim().substring(30);
                                    if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                                        newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                                        this.functionArguments.setElementAt(newformat, 2);
                                    }
                                    else {
                                        if (arg2.indexOf(40) == -1) {
                                            arg2 = "dbo." + arg2.trim() + "INT()";
                                        }
                                        arguments.setElementAt(arg2, 2);
                                    }
                                }
                                else if (arg2.trim().startsWith("@")) {
                                    arg2 = "dbo.FetchSqlFormat(" + arg2 + ")";
                                    arguments.setElementAt(arg2, 2);
                                }
                                else if (expression.size() > 1 && expression.get(1) instanceof String && expression.get(1).trim().equals("+")) {
                                    if (!this.ramcoSpecificDateFormatHandling(arguments)) {
                                        arguments.setElementAt("dbo.FetchSqlDtFormat(" + this.functionArguments.get(1).toString() + ")", 2);
                                        break;
                                    }
                                    final SelectColumn selCol = this.functionArguments.get(0);
                                    final Vector colExp = selCol.getColumnExpression();
                                    arguments.setElementAt(colExp.get(0), 0);
                                    arg2 = "dbo." + arg2.trim() + "INT()) + (" + colExp.get(4);
                                    tc3.setColumnName(arg2);
                                    final Vector newColExp = new Vector();
                                    newColExp.add(tc3);
                                    final SelectColumn newSelCol = new SelectColumn();
                                    newSelCol.setColumnExpression(newColExp);
                                    arguments.setElementAt(newSelCol, 2);
                                    break;
                                }
                                else {
                                    arg2 = "dbo." + arg2.trim() + "INT()";
                                    tc3.setColumnName(arg2);
                                    arguments.setElementAt(this.functionArguments.get(1), 2);
                                }
                            }
                            else if (expression.get(j) instanceof FunctionCalls) {
                                final FunctionCalls fncalls = expression.get(j);
                                if (fncalls.getFunctionName().getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT") && fncalls.getFunctionArguments() != null && fncalls.getFunctionArguments().size() == 1) {
                                    arguments.setElementAt(fncalls.getFunctionArguments().get(0), 2);
                                }
                            }
                        }
                    }
                }
                arguments.setElementAt(this.functionArguments.get(0), 1);
                arguments.setElementAt("DATETIME", 0);
            }
        }
        else if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof String) {
            final Vector datetimeVector2 = new Vector();
            if (this.functionArguments.get(1) instanceof String) {
                final String format2 = this.functionArguments.get(1);
                final String datetimeToBeChangedString = this.functionArguments.get(1);
                if (format2.trim().startsWith("'") && format2.trim().endsWith("'")) {
                    if (FunctionCalls.charToIntName) {
                        if (format2.trim().startsWith("'") && format2.endsWith("'")) {
                            this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                            this.functionArguments.setElementAt("DATETIME", 0);
                            this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format2 + ")");
                        }
                    }
                    else if (format2.toUpperCase().indexOf("DD/MM/YYY") != -1) {
                        arguments.add(2, "103");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("MM/DD/YYY") != -1) {
                        arguments.add(2, "101");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("YYY/MM/DD") != -1) {
                        arguments.add(2, "111");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("DD-MM-YYY") != -1) {
                        arguments.add(2, "105");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("MM-DD-YYY") != -1) {
                        arguments.add(2, "110");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("YYY-MM-DD") != -1) {
                        arguments.add(2, "121");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("DD-MM-YY") != -1) {
                        arguments.add(2, "5");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("YYYYMMDD") != -1) {
                        arguments.add(2, "112");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("YYMMDD") != -1) {
                        arguments.add(2, "12");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                        arguments.add(2, "102");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("YY.MM.DD") != -1) {
                        arguments.add(2, "2");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else {
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                }
                else {
                    arguments.add(2, this.functionArguments.get(1));
                    String arg3 = arguments.get(2);
                    if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof String) {
                        if (format2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                            String newformat2 = format2.trim().substring(30);
                            if (newformat2.trim().startsWith("(") && newformat2.trim().endsWith(")")) {
                                newformat2 = newformat2.trim().substring(1, newformat2.trim().length() - 1);
                                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                                this.functionArguments.setElementAt(newformat2, 2);
                            }
                            else {
                                if (arg3.indexOf(40) == -1) {
                                    arg3 = "dbo." + arg3.trim() + "INT()";
                                }
                                else {
                                    arg3 = arg3.substring(0, arg3.indexOf(40)).trim() + "INT" + arg3.substring(arg3.indexOf("("));
                                }
                                arguments.setElementAt(arg3, 2);
                            }
                        }
                        else if (arg3.trim().startsWith("@")) {
                            arg3 = "dbo.FetchSqlFormat(" + arg3 + ")";
                            arguments.setElementAt(arg3, 2);
                        }
                        else {
                            if (arg3.indexOf(40) == -1) {
                                arg3 = "dbo." + arg3.trim() + "INT()";
                            }
                            else {
                                arg3 = arg3.substring(0, arg3.indexOf(40)).trim() + "INT" + arg3.substring(arg3.indexOf("("));
                            }
                            arguments.setElementAt(arg3, 2);
                        }
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("DATETIME", 0);
                }
            }
        }
        else if (this.functionArguments.size() == 1) {
            arguments.add(1, this.functionArguments.get(0));
            arguments.setElementAt("DATETIME", 0);
        }
        else {
            arguments.setElementAt("DATETIME", 0);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONVERT");
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
        if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn && this.functionArguments.get(0) instanceof SelectColumn) {
            final SelectColumn forDatetimeToBeChanged = this.functionArguments.get(0);
            final SelectColumn sc = this.functionArguments.get(1);
            String datetimeToBeChangedString = "";
            if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
                TableColumn tc = new TableColumn();
                tc = forDatetimeToBeChanged.getColumnExpression().get(0);
                datetimeToBeChangedString = tc.getColumnName();
            }
            else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof String) {
                datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0);
            }
            else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof FunctionCalls) {
                datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0).toString();
            }
            final Vector datetimeVector = new Vector();
            if (sc.getColumnExpression().get(0) instanceof String) {
                final String format = sc.getColumnExpression().get(0);
                if (format.trim().startsWith("'") && format.trim().endsWith("'")) {
                    if (FunctionCalls.charToIntName) {
                        if (format.trim().startsWith("'") && format.endsWith("'")) {
                            this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                            this.functionArguments.setElementAt("DATETIME", 0);
                            this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
                        }
                    }
                    else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                        arguments.add(2, "103");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                        arguments.add(2, "102");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                        arguments.add(2, "121");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("DD-MM-YY") != -1) {
                        arguments.add(2, "5");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YYYYMMDD") != -1) {
                        arguments.add(2, "112");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format.toUpperCase().indexOf("YYMMDD") != -1) {
                        arguments.add(2, "12");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else {
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                }
                else {
                    arguments.add(2, this.functionArguments.get(1));
                    if (FunctionCalls.charToIntName && format.trim().startsWith("@")) {
                        this.functionArguments.setElementAt("dbo.FetchSqlDtFormat(" + format + ")", 2);
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("DATETIME", 0);
                }
            }
            else if (sc.getColumnExpression().get(0) instanceof FunctionCalls) {
                arguments.add(2, this.functionArguments.get(1));
                if (FunctionCalls.charToIntName) {
                    final FunctionCalls fc = sc.getColumnExpression().get(0);
                    final TableColumn tc2 = fc.getFunctionName();
                    if (tc2.getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT") && fc.getFunctionArguments() != null && fc.getFunctionArguments().size() == 1) {
                        tc2.setColumnName("");
                        tc2.setTableName(null);
                        tc2.setOwnerName(null);
                    }
                    else if (fc.getFunctionArguments().size() == 0) {
                        if (sc.getColumnExpression().size() > 1 && sc.getColumnExpression().get(1) instanceof String && sc.getColumnExpression().get(1).trim().equals("+")) {
                            if (!this.ramcoSpecificDateFormatHandling(arguments)) {
                                arguments.setElementAt("dbo.FetchSqlDtFormat(" + this.functionArguments.get(1).toString() + ")", 2);
                            }
                        }
                        else {
                            tc2.setColumnName(tc2.getColumnName().trim() + "INT");
                        }
                    }
                    else {
                        this.processFunctionArgumentsForRamco(fc.getFunctionArguments());
                    }
                }
                arguments.setElementAt(this.functionArguments.get(0), 1);
                arguments.setElementAt("DATETIME", 0);
            }
            else {
                arguments.add(2, this.functionArguments.get(1));
                if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof SelectColumn) {
                    final SelectColumn selectcolumn = arguments.get(2);
                    final Vector expression = selectcolumn.getColumnExpression();
                    if (expression != null) {
                        for (int j = 0; j < expression.size(); ++j) {
                            if (expression.get(j) instanceof TableColumn) {
                                final TableColumn tc3 = expression.get(j);
                                String arg2 = tc3.getColumnName();
                                if (arg2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                                    String newformat = arg2.trim().substring(30);
                                    if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                                        newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                                        this.functionArguments.setElementAt(newformat, 2);
                                    }
                                    else {
                                        if (arg2.indexOf(40) == -1) {
                                            arg2 = "dbo." + arg2.trim() + "INT()";
                                        }
                                        arguments.setElementAt(arg2, 2);
                                    }
                                }
                                else if (arg2.trim().startsWith("@")) {
                                    arg2 = "dbo.FetchSqlFormat(" + arg2 + ")";
                                    arguments.setElementAt(arg2, 2);
                                }
                                else if (expression.size() > 1 && expression.get(1) instanceof String && expression.get(1).trim().equals("+")) {
                                    if (!this.ramcoSpecificDateFormatHandling(arguments)) {
                                        arguments.setElementAt("dbo.FetchSqlDtFormat(" + this.functionArguments.get(1).toString() + ")", 2);
                                        break;
                                    }
                                    final SelectColumn selCol = this.functionArguments.get(0);
                                    final Vector colExp = selCol.getColumnExpression();
                                    arguments.setElementAt(colExp.get(0), 0);
                                    arg2 = "dbo." + arg2.trim() + "INT()) + (" + colExp.get(4);
                                    tc3.setColumnName(arg2);
                                    final Vector newColExp = new Vector();
                                    newColExp.add(tc3);
                                    final SelectColumn newSelCol = new SelectColumn();
                                    newSelCol.setColumnExpression(newColExp);
                                    arguments.setElementAt(newSelCol, 2);
                                    break;
                                }
                                else {
                                    arg2 = "dbo." + arg2.trim() + "INT()";
                                    tc3.setColumnName(arg2);
                                    arguments.setElementAt(this.functionArguments.get(1), 2);
                                }
                            }
                            else if (expression.get(j) instanceof FunctionCalls) {
                                final FunctionCalls fncalls = expression.get(j);
                                if (fncalls.getFunctionName().getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT") && fncalls.getFunctionArguments() != null && fncalls.getFunctionArguments().size() == 1) {
                                    arguments.setElementAt(fncalls.getFunctionArguments().get(0), 2);
                                }
                            }
                        }
                    }
                }
                arguments.setElementAt(this.functionArguments.get(0), 1);
                arguments.setElementAt("DATETIME", 0);
            }
        }
        else if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof String) {
            final Vector datetimeVector2 = new Vector();
            if (this.functionArguments.get(1) instanceof String) {
                final String format2 = this.functionArguments.get(1);
                final String datetimeToBeChangedString = this.functionArguments.get(1);
                if (format2.trim().startsWith("'") && format2.trim().endsWith("'")) {
                    if (FunctionCalls.charToIntName) {
                        if (format2.trim().startsWith("'") && format2.endsWith("'")) {
                            this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                            this.functionArguments.setElementAt("DATETIME", 0);
                            this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format2 + ")");
                        }
                    }
                    else if (format2.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                        arguments.add(2, "103");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                        arguments.add(2, "102");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else if (format2.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                        arguments.add(2, "121");
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                    else {
                        arguments.add(2, this.functionArguments.get(1));
                        String arg3 = arguments.get(2);
                        if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof String) {
                            if (format2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                                String newformat2 = format2.trim().substring(30);
                                if (newformat2.trim().startsWith("(") && newformat2.trim().endsWith(")")) {
                                    newformat2 = newformat2.trim().substring(1, newformat2.trim().length() - 1);
                                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                                    this.functionArguments.setElementAt(newformat2, 2);
                                }
                                else {
                                    if (arg3.indexOf(40) == -1) {
                                        arg3 = "dbo." + arg3.trim() + "INT()";
                                    }
                                    else {
                                        arg3 = arg3.substring(0, arg3.indexOf(40)).trim() + "INT" + arg3.substring(arg3.indexOf("("));
                                    }
                                    arguments.setElementAt(arg3, 2);
                                }
                            }
                            else if (arg3.trim().startsWith("@")) {
                                arg3 = "dbo.FetchSqlFormat(" + arg3 + ")";
                                arguments.setElementAt(arg3, 2);
                            }
                            else {
                                if (arg3.indexOf(40) == -1) {
                                    arg3 = "dbo." + arg3.trim() + "INT()";
                                }
                                else {
                                    arg3 = arg3.substring(0, arg3.indexOf(40)).trim() + "INT" + arg3.substring(arg3.indexOf("("));
                                }
                                arguments.setElementAt(arg3, 2);
                            }
                        }
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("DATETIME", 0);
                    }
                }
                else {
                    arguments.add(2, this.functionArguments.get(1));
                    String arg3 = arguments.get(2);
                    if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof String) {
                        if (format2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                            String newformat2 = format2.trim().substring(30);
                            if (newformat2.trim().startsWith("(") && newformat2.trim().endsWith(")")) {
                                newformat2 = newformat2.trim().substring(1, newformat2.trim().length() - 1);
                                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                                this.functionArguments.setElementAt(newformat2, 2);
                            }
                            else {
                                if (arg3.indexOf(40) == -1) {
                                    arg3 = "dbo." + arg3.trim() + "INT()";
                                }
                                else {
                                    arg3 = arg3.substring(0, arg3.indexOf(40)).trim() + "INT" + arg3.substring(arg3.indexOf("("));
                                }
                                arguments.setElementAt(arg3, 2);
                            }
                        }
                        else if (arg3.trim().startsWith("@")) {
                            arg3 = "dbo.FetchSqlFormat(" + arg3 + ")";
                            arguments.setElementAt(arg3, 2);
                        }
                        else {
                            if (arg3.indexOf(40) == -1) {
                                arg3 = "dbo." + arg3.trim() + "INT()";
                            }
                            else {
                                arg3 = arg3.substring(0, arg3.indexOf(40)).trim() + "INT" + arg3.substring(arg3.indexOf("("));
                            }
                            arguments.setElementAt(arg3, 2);
                        }
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("DATETIME", 0);
                }
            }
            else {
                arguments.add(2, this.functionArguments.get(1));
                if (FunctionCalls.charToIntName && arguments.elementAt(2) instanceof String) {
                    String arg4 = arguments.get(2);
                    if (arg4.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                        String newformat3 = arg4.trim().substring(30);
                        if (newformat3.trim().startsWith("(") && newformat3.trim().endsWith(")")) {
                            newformat3 = newformat3.trim().substring(1, newformat3.trim().length() - 1);
                            this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                            this.functionArguments.setElementAt(newformat3, 2);
                        }
                        else {
                            if (arg4.indexOf(40) == -1) {
                                arg4 = "dbo." + arg4.trim() + "INT()";
                            }
                            else {
                                arg4 = arg4.substring(0, arg4.indexOf(40)).trim() + "INT" + arg4.substring(arg4.indexOf("("));
                            }
                            arguments.setElementAt(arg4, 2);
                        }
                    }
                    else if (arg4.trim().startsWith("@")) {
                        arg4 = "dbo.FetchSqlFormat(" + arg4 + ")";
                        arguments.setElementAt(arg4, 2);
                    }
                    else {
                        if (arg4.indexOf(40) == -1) {
                            arg4 = "dbo." + arg4.trim() + "INT()";
                        }
                        else {
                            arg4 = arg4.substring(0, arg4.indexOf(40)).trim() + "INT" + arg4.substring(arg4.indexOf("("));
                        }
                        arguments.setElementAt(arg4, 2);
                    }
                }
                arguments.setElementAt(this.functionArguments.get(0), 1);
                arguments.setElementAt("DATETIME", 0);
            }
        }
        else if (this.functionArguments.size() == 1) {
            arguments.add(1, this.functionArguments.get(0));
            arguments.setElementAt("DATETIME", 0);
        }
        else {
            arguments.setElementAt("DATETIME", 0);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TIMESTAMP");
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
        if (SwisSQLOptions.UDBSQL) {
            if (arguments.size() == 2) {
                this.functionName.setColumnName("TO_DATE");
            }
            return;
        }
        final FunctionCalls subFunction = new FunctionCalls();
        final TableColumn subTC = new TableColumn();
        subTC.setColumnName("DATE");
        subFunction.setFunctionName(subTC);
        if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
            final SelectColumn forDatetimeToBeChanged = this.functionArguments.get(0);
            final SelectColumn sc = this.functionArguments.get(1);
            String datetimeToBeChangedString = "";
            if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
                TableColumn tc = new TableColumn();
                tc = forDatetimeToBeChanged.getColumnExpression().get(0);
                datetimeToBeChangedString = tc.getColumnName();
            }
            else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof String) {
                datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0);
            }
            else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof FunctionCalls) {
                datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0).toString();
            }
            final Vector datetimeVector = new Vector();
            if (sc.getColumnExpression().get(0) instanceof String) {
                final String format = sc.getColumnExpression().get(0);
                if (format.toUpperCase().indexOf("MONTH") != -1) {
                    throw new ConvertException("MONTH in TO_DATE() is not supported");
                }
                if (format.toUpperCase().indexOf("Y") != -1) {
                    if (format.toUpperCase().indexOf("YY") != -1) {
                        if (format.toUpperCase().indexOf("YYY") != -1) {
                            if (format.toUpperCase().indexOf("YYYY") != -1) {
                                if (datetimeToBeChangedString.indexOf("'") != -1 && datetimeToBeChangedString.length() < 12 && datetimeToBeChangedString.length() > 7) {
                                    if (datetimeToBeChangedString.length() == 10) {
                                        this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "),5,4) || '-' || ", 0);
                                    }
                                    else if (datetimeToBeChangedString.length() == 11) {
                                        this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "),6,4) || '-' || ", 0);
                                    }
                                }
                                else {
                                    this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("YYYY") + ",4) || '-' || ", 0);
                                }
                            }
                            else {
                                this.functionArguments.setElementAt("CHAR(YEAR(CURRENT DATE) - MOD(YEAR(CURRENT DATE),1000) + INT(SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("YYY") + ",3))) || '-' || ", 0);
                            }
                        }
                        else {
                            this.functionArguments.setElementAt("CHAR(YEAR(CURRENT DATE) - MOD(YEAR(CURRENT DATE),100) + INT(SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("YY") + ",2))) || '-' || ", 0);
                        }
                    }
                    else {
                        this.functionArguments.setElementAt("CHAR(YEAR(CURRENT DATE) - MOD(YEAR(CURRENT DATE),10) + INT(SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("Y") + ",1))) || '-' || ", 0);
                    }
                }
                else {
                    this.functionArguments.setElementAt("CHAR(YEAR(CURRENT DATE)) || '-' || ", 0);
                }
                if (format.toUpperCase().indexOf("MM") != -1) {
                    if (datetimeToBeChangedString.indexOf("'") != -1 && datetimeToBeChangedString.length() < 12 && datetimeToBeChangedString.length() > 7) {
                        if (datetimeToBeChangedString.length() == 10) {
                            this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "),3,1) || '-' || ", 1);
                        }
                        else if (datetimeToBeChangedString.length() == 11) {
                            this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "),3,2) || '-' || ", 1);
                        }
                    }
                    else {
                        this.functionArguments.setElementAt("SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("MM") + ",2) || '-' || ", 1);
                    }
                }
                else {
                    this.functionArguments.setElementAt("CHAR(MONTH(CURRENT DATE)) || '-' || ", 1);
                }
                if (format.toUpperCase().indexOf("D") != -1) {
                    if (format.toUpperCase().indexOf("DD") != -1) {
                        if (datetimeToBeChangedString.indexOf("'") != -1 && datetimeToBeChangedString.length() < 12 && datetimeToBeChangedString.length() > 7) {
                            this.functionArguments.add("SUBSTR(CHAR(" + datetimeToBeChangedString + "),1,1)");
                        }
                        else {
                            this.functionArguments.add("SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("DD") + ",2)");
                        }
                    }
                    else {
                        this.functionArguments.add("01");
                    }
                }
                else {
                    this.functionArguments.add("CHAR(DAY(CURRENT DATE))");
                }
                if (format.toUpperCase().indexOf("HH") != -1) {
                    if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.add(" || ' ' || SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("HH24:MI:SS") + ", 8)");
                    }
                    else if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                        this.functionArguments.add(" || ' ' || SUBSTR(CHAR(" + datetimeToBeChangedString + "), " + format.toUpperCase().indexOf("HH:MI:SS") + ", 8)");
                    }
                    else {
                        this.functionArguments.add(" || ' 00:00:00'");
                    }
                }
                else {
                    this.functionArguments.add(" || ' 00:00:00'");
                }
                final String newArgString = this.functionArguments.get(0).toString() + this.functionArguments.get(1).toString() + this.functionArguments.get(2).toString() + this.functionArguments.get(3).toString();
                final Vector newArgument = new Vector();
                newArgument.add(newArgString);
                this.setFunctionArguments(newArgument);
            }
        }
        else {
            final SelectColumn sc2 = new SelectColumn();
            final Vector newArgs = new Vector();
            final Vector scArgs = new Vector();
            subFunction.setFunctionArguments(arguments);
            scArgs.add(subFunction);
            sc2.setColumnExpression(scArgs);
            newArgs.add(sc2);
            newArgs.add("'00:00:00'");
            this.setFunctionArguments(newArgs);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
            this.functionName.setColumnName("CONCAT");
            final SelectColumn forDatetimeToBeChanged = this.functionArguments.get(0);
            final SelectColumn sc = this.functionArguments.get(1);
            String datetimeToBeChangedString = "";
            if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
                TableColumn tc = new TableColumn();
                tc = forDatetimeToBeChanged.getColumnExpression().get(0);
                datetimeToBeChangedString = tc.getColumnName();
            }
            else if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof SelectColumn) {
                datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0).toString();
            }
            else {
                datetimeToBeChangedString = forDatetimeToBeChanged.getColumnExpression().get(0);
            }
            final Vector datetimeVector = new Vector();
            if (sc.getColumnExpression().get(0) instanceof String) {
                final String format = sc.getColumnExpression().get(0);
                if (format.toUpperCase().indexOf("MONTH") != -1) {
                    throw new ConvertException("MONTH in TO_DATE() is not supported");
                }
                if (format.toUpperCase().indexOf("Y") != -1) {
                    if (format.toUpperCase().indexOf("YY") != -1) {
                        if (format.toUpperCase().indexOf("YYY") != -1) {
                            if (format.toUpperCase().indexOf("YYYY") != -1) {
                                this.functionArguments.setElementAt("SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("YYYY") + ",4) , '-' ", 0);
                            }
                            else {
                                this.functionArguments.setElementAt("YEAR(CURRENT_DATE) - MOD(YEAR(CURRENT_DATE),1000) + SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("YYY") + ",3) , '-' ", 0);
                            }
                        }
                        else {
                            this.functionArguments.setElementAt("YEAR(CURRENT_DATE) - MOD(YEAR(CURRENT_DATE),100) + SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("YYY") + ",3) , '-' ", 0);
                        }
                    }
                    else {
                        this.functionArguments.setElementAt("YEAR(CURRENT_DATE) - MOD(YEAR(CURRENT_DATE),10) + SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("YYY") + ",3) , '-' ", 0);
                    }
                }
                else {
                    this.functionArguments.setElementAt("YEAR(CURRENT_DATE) , '-' ", 0);
                }
                if (format.toUpperCase().indexOf("MM") != -1) {
                    this.functionArguments.setElementAt("SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("MM") + ",2) , '-' ", 1);
                }
                else {
                    this.functionArguments.setElementAt("MONTH(CURRENT_DATE) , '-' ", 1);
                }
                if (format.toUpperCase().indexOf("D") != -1) {
                    if (format.toUpperCase().indexOf("DD") != -1) {
                        this.functionArguments.add("SUBSTRING(" + datetimeToBeChangedString + ", " + format.toUpperCase().indexOf("DD") + ",2)");
                    }
                    else {
                        this.functionArguments.add("01");
                    }
                }
                else {
                    this.functionArguments.add("DAY(CURRENT_DATE)");
                }
                this.setFunctionArguments(this.functionArguments);
            }
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String orgFnName = this.functionName.getColumnName();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                todate.functionArgsInSingleQuotesToDouble = false;
                arguments.addElement(this.functionArguments.elementAt(i).toANSISelect(to_sqs, from_sqs));
                todate.functionArgsInSingleQuotesToDouble = true;
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        if (arguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final DateClass dc = new DateClass();
            if (orgFnName.equalsIgnoreCase("to_date")) {
                dc.setDatatypeName("DATE");
            }
            else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
                dc.setDatatypeName("TIMESTAMP");
            }
            this.setAsDatatype("AS");
            arguments.add(dc);
            this.setFunctionArguments(arguments);
        }
        else if (arguments.size() == 2) {
            final String arg1 = null;
            String arg2 = null;
            if (arguments.get(0) instanceof SelectColumn && arguments.get(1) instanceof SelectColumn) {
                final SelectColumn sc1 = arguments.get(0);
                final SelectColumn sc2 = arguments.get(1);
                final Vector colExpr1 = sc1.getColumnExpression();
                final Vector colExpr2 = sc2.getColumnExpression();
                if (colExpr1.size() == 1 && colExpr2.size() == 1 && colExpr2.get(0) instanceof String) {
                    arg2 = colExpr2.get(0);
                    if (arg2.indexOf(":") != -1) {
                        arg2 = "TIMESTAMP FORMAT " + arg2;
                        colExpr2.set(0, arg2);
                        this.functionName.setColumnName("CAST");
                        this.setAsDatatype("AS");
                        this.setFunctionArguments(arguments);
                    }
                    else {
                        arg2 = "DATE FORMAT " + arg2;
                        colExpr2.set(0, arg2);
                        this.functionName.setColumnName("CAST");
                        this.setAsDatatype("AS");
                        this.setFunctionArguments(arguments);
                    }
                }
            }
            else {
                this.functionName.setColumnName("CAST");
                final DateClass dc2 = new DateClass();
                this.setAsDatatype("AS");
                if (orgFnName.equalsIgnoreCase("to_date")) {
                    dc2.setDatatypeName("DATE");
                }
                else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
                    dc2.setDatatypeName("TIMESTAMP");
                }
                this.setFunctionArguments(arguments);
            }
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionArguments.size() == 1) {
            final Object arg = this.functionArguments.elementAt(0);
            if (arg.toString().trim().startsWith("'")) {
                String literalValue = arg.toString().trim();
                String format = SwisSQLUtils.getDateFormat(literalValue, 10);
                format = format.trim();
                if (format != null) {
                    if (format.equalsIgnoreCase("'DD-MON-YY'") || format.equalsIgnoreCase("'DD/MON/YY'") || format.equalsIgnoreCase("'DD:MON:YY'")) {
                        final String separator = format.substring(3, 4);
                        String yearpart = literalValue.substring(literalValue.lastIndexOf(separator) + 1, literalValue.length() - 1);
                        int yearpart_int = 0;
                        try {
                            yearpart_int = Integer.parseInt(yearpart);
                        }
                        catch (final Exception ex) {}
                        if (yearpart_int < 50) {
                            if (yearpart_int < 10) {
                                yearpart = "200" + yearpart_int;
                            }
                            else {
                                yearpart = "20" + yearpart_int;
                            }
                        }
                        else {
                            yearpart = "19" + yearpart_int;
                        }
                        literalValue = literalValue.substring(0, literalValue.length() - 3) + yearpart + "'";
                        arguments.addElement(literalValue);
                        format = "DD" + separator + "MON" + separator + "YYYY";
                        arguments.addElement("'" + format + "'");
                    }
                    else {
                        arguments.addElement(literalValue);
                        if (format.startsWith("'") && format.endsWith("'")) {
                            arguments.addElement(format);
                        }
                        else {
                            arguments.addElement("'" + format + "'");
                        }
                    }
                    this.setFunctionArguments(arguments);
                }
            }
        }
        else {
            final Object arg = this.functionArguments.elementAt(0);
            if (arg.toString().trim().startsWith("'")) {
                String literalValue = arg.toString().trim();
                String format = this.functionArguments.elementAt(1).toString().trim();
                if (format != null) {
                    if (format.equalsIgnoreCase("'DD-MON-YY'") || format.equalsIgnoreCase("'DD/MON/YY'") || format.equalsIgnoreCase("'DD:MON:YY'")) {
                        final String separator = format.substring(3, 4);
                        String yearpart = literalValue.substring(literalValue.lastIndexOf(separator) + 1, literalValue.length() - 1);
                        yearpart = "20" + yearpart;
                        literalValue = literalValue.substring(0, literalValue.length() - 3) + yearpart + "'";
                        arguments.addElement(literalValue);
                        format = "DD" + separator + "MON" + separator + "YYYY";
                        arguments.addElement("'" + format + "'");
                    }
                    else if (format.equalsIgnoreCase("'MM-DD-YY'") || format.equalsIgnoreCase("'MM/DD/YY'") || format.equalsIgnoreCase("'MM:DD:YY'")) {
                        final String separator = format.substring(3, 4);
                        String yearpart = literalValue.substring(literalValue.lastIndexOf(separator) + 1, literalValue.length() - 1);
                        yearpart = "20" + yearpart;
                        literalValue = literalValue.substring(0, literalValue.length() - 3) + yearpart + "'";
                        arguments.addElement(literalValue);
                        format = "MM" + separator + "DD" + separator + "YYYY";
                        arguments.addElement("'" + format + "'");
                    }
                    else {
                        arguments.addElement(literalValue);
                        if (format.startsWith("'") && format.endsWith("'")) {
                            arguments.addElement(format);
                        }
                        else {
                            arguments.addElement("'" + format + "'");
                        }
                    }
                    this.setFunctionArguments(arguments);
                }
            }
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String orgFnName = this.functionName.getColumnName();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        if (arguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final DateClass dc = new DateClass();
            if (orgFnName.equalsIgnoreCase("to_date")) {
                dc.setDatatypeName("DATE");
            }
            else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
                dc.setDatatypeName("TIMESTAMP");
            }
            this.setAsDatatype("AS");
            arguments.add(dc);
        }
        else if (arguments.size() == 2) {
            final String arg1 = null;
            String arg2 = null;
            if (arguments.get(0) instanceof SelectColumn && arguments.get(1) instanceof SelectColumn) {
                final SelectColumn sc1 = arguments.get(0);
                final SelectColumn sc2 = arguments.get(1);
                final Vector colExpr1 = sc1.getColumnExpression();
                final Vector colExpr2 = sc2.getColumnExpression();
                if (colExpr1.size() == 1 && colExpr2.size() == 1 && colExpr2.get(0) instanceof String) {
                    arg2 = colExpr2.get(0).toString().toUpperCase();
                    if (arg2.indexOf(":") != -1 && arg2.startsWith("'") && arg2.indexOf(".") != -1) {
                        arg2 = arg2.replaceAll("\\.", " ");
                        colExpr2.set(0, arg2);
                    }
                }
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String orgFnName = this.functionName.getColumnName();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                SelectColumn sc = this.functionArguments.elementAt(i);
                boolean handleToCharForToTimestampTZ = false;
                if (orgFnName.equalsIgnoreCase("to_timestamp_tz") && sc.getColumnExpression().firstElement() instanceof FunctionCalls && sc.getColumnExpression().firstElement().getFunctionName().getColumnName().equalsIgnoreCase("to_char")) {
                    handleToCharForToTimestampTZ = true;
                }
                sc = sc.toTeradataSelect(to_sqs, from_sqs);
                if (handleToCharForToTimestampTZ) {
                    final Object toCharFn = sc.getColumnExpression().firstElement().getFunctionArguments().get(0);
                    sc.getColumnExpression().setElementAt(toCharFn, 0);
                }
                arguments.addElement(sc);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        if (arguments.size() == 3) {
            arguments.removeElementAt(2);
        }
        if (arguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final DateClass dc = new DateClass();
            if (orgFnName.equalsIgnoreCase("to_date")) {
                if (SwisSQLOptions.convert_Oracle_TO_DATE_To_Timestamp) {
                    dc.setDatatypeName("TIMESTAMP");
                }
                else {
                    dc.setDatatypeName("DATE");
                }
            }
            else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
                dc.setDatatypeName("TIMESTAMP");
            }
            this.setAsDatatype("AS");
            arguments.add(dc);
            this.setFunctionArguments(arguments);
        }
        else if (arguments.size() == 2) {
            boolean threeLetterMonthFormat = false;
            boolean monthExpanded = false;
            boolean isAM = false;
            boolean isPM = false;
            boolean ishyphen = false;
            String arg1 = null;
            String arg2 = null;
            if (arguments.get(0) instanceof SelectColumn && arguments.get(1) instanceof SelectColumn) {
                final SelectColumn sc2 = arguments.get(0);
                final SelectColumn sc3 = arguments.get(1);
                final Vector colExpr1 = sc2.getColumnExpression();
                final Vector colExpr2 = sc3.getColumnExpression();
                String[] dateParts = new String[3];
                String monthPart = "";
                String secondsPart = "";
                boolean splitDate = false;
                if (colExpr1.size() > 0 && colExpr2.size() == 1 && colExpr2.get(0) instanceof String) {
                    arg2 = colExpr2.get(0).toString().toUpperCase();
                    if (colExpr1.size() == 1) {
                        arg1 = colExpr1.get(0).toString();
                    }
                    else {
                        arg1 = "";
                        for (int k = 0; k < colExpr1.size(); ++k) {
                            arg1 = arg1 + colExpr1.elementAt(k).toString() + " ";
                        }
                    }
                    if (colExpr1.get(0) instanceof String) {
                        if (arg1.length() < arg2.length() && arg1.startsWith("'") && arg2.startsWith("'")) {
                            arg1 = this.reformatHardCodedDate(arg1.trim(), arg2.trim());
                            if (colExpr1.size() == 1) {
                                colExpr1.setElementAt(arg1, 0);
                            }
                        }
                        else if (arg2.startsWith("'") && !arg1.startsWith("'") && arg1.length() + 2 <= arg2.length()) {
                            arg1 = this.reformatHardCodedDate("'" + arg1.trim() + "'", arg2.trim());
                            if (colExpr1.size() == 1) {
                                colExpr1.setElementAt(arg1, 0);
                            }
                        }
                        final String temp = arg1;
                        String splitWhere = "";
                        if (temp.lastIndexOf("-") > 1 && this.count(temp, "-") > 1) {
                            splitWhere = "-";
                            ishyphen = true;
                            splitDate = true;
                        }
                        else if (temp.lastIndexOf("/") > 1 && this.count(temp, "/") > 1) {
                            splitWhere = "/";
                            splitDate = true;
                        }
                        else {
                            splitDate = false;
                        }
                        if (splitDate) {
                            dateParts = temp.split(splitWhere);
                            monthPart = dateParts[1];
                            secondsPart = dateParts[2];
                        }
                        if (monthPart.trim().length() == 3) {
                            threeLetterMonthFormat = true;
                        }
                        else if (monthPart.trim().length() > 3) {
                            monthExpanded = true;
                        }
                        if (secondsPart.trim().toUpperCase().indexOf("AM") != -1) {
                            isAM = true;
                        }
                        else if (secondsPart.trim().toUpperCase().indexOf("PM") != -1) {
                            isPM = true;
                        }
                    }
                    if (arg2.startsWith("'")) {
                        if (threeLetterMonthFormat) {
                            if (arg2.trim().toUpperCase().indexOf("MON") != -1) {
                                arg2 = arg2.replaceAll("MON", "MMM");
                            }
                            else if (arg2.trim().toUpperCase().indexOf("MM") != -1) {
                                arg2 = arg2.replaceAll("MM", "MMM");
                            }
                        }
                        if (monthExpanded) {
                            if (arg2.trim().toUpperCase().indexOf("MONTH") != -1) {
                                arg2 = arg2.replaceAll("MONTH", "MMMM");
                            }
                            else if (arg2.trim().toUpperCase().indexOf("MON") != -1) {
                                arg2 = arg2.replaceAll("MON", "MMMM");
                            }
                            else if (arg2.trim().toUpperCase().indexOf("MM") != -1) {
                                arg2 = arg2.replaceAll("MM", "MMMM");
                            }
                        }
                        if (arg2.indexOf("HH24") != -1) {
                            arg2 = arg2.replaceAll("HH24", "HH");
                        }
                        else if (arg2.indexOf("HH12") != -1) {
                            arg2 = arg2.replaceAll("HH12", "HH");
                        }
                        if (isAM || isPM) {
                            arg2 = arg2.replaceAll("SS", "SSBT");
                            if (arg2.indexOf("AM") != -1) {
                                arg2 = arg2.replaceAll("AM", "");
                            }
                            else if (arg2.indexOf("PM") != -1) {
                                arg2 = arg2.replaceAll("PM", "");
                            }
                        }
                        if (arg2.indexOf(" ") != -1) {
                            arg2 = arg2.replaceAll(" ", "B");
                        }
                        else if (arg2.indexOf(".") != -1) {
                            arg2 = arg2.replaceAll("\\.", "B");
                        }
                        if (arg2.indexOf("TZR") != -1) {
                            arg2 = arg2.replaceAll("TZR", "");
                        }
                        if (arg2.indexOf("MONTH") != -1) {
                            arg2 = arg2.replaceAll("MONTH", "MMMM");
                        }
                        else if (arg2.indexOf("MON") != -1) {
                            arg2 = arg2.replaceAll("MON", "MMM");
                        }
                        if (arg2.indexOf("RR") != -1) {
                            arg2 = arg2.replaceAll("RR", "YY");
                        }
                    }
                    if (arg2.indexOf(":") != -1 || (arg1.startsWith("'") && arg1.endsWith("'") && arg1.indexOf(":") != -1) || arg2.toUpperCase().indexOf("HH") != -1) {
                        final DateClass timestampType = new DateClass();
                        timestampType.setDatatypeName("TIMESTAMP");
                        timestampType.setOpenBrace("(");
                        timestampType.setClosedBrace(")");
                        timestampType.setSize("0");
                        arg2 = " FORMAT " + arg2;
                        colExpr2.set(0, arg2);
                        colExpr2.insertElementAt(timestampType, 0);
                        if (!this.functionName.getColumnName().trim().equalsIgnoreCase("to_timestamp_tz")) {
                            this.functionName.setColumnName("CAST");
                        }
                        this.setAsDatatype("AS");
                        this.setFunctionArguments(arguments);
                    }
                    else {
                        final DateClass timestampType = new DateClass();
                        timestampType.setDatatypeName("DATE");
                        arg2 = " FORMAT " + arg2;
                        colExpr2.set(0, arg2);
                        colExpr2.insertElementAt(timestampType, 0);
                        if (!this.functionName.getColumnName().trim().equalsIgnoreCase("to_timestamp_tz")) {
                            this.functionName.setColumnName("CAST");
                        }
                        this.setAsDatatype("AS");
                        this.setFunctionArguments(arguments);
                    }
                }
            }
            else {
                this.functionName.setColumnName("CAST");
                final DateClass dc2 = new DateClass();
                this.setAsDatatype("AS");
                if (orgFnName.equalsIgnoreCase("to_date")) {
                    if (SwisSQLOptions.convert_Oracle_TO_DATE_To_Timestamp) {
                        dc2.setDatatypeName("TIMESTAMP");
                    }
                    else {
                        dc2.setDatatypeName("DATE");
                    }
                }
                else if (orgFnName.equalsIgnoreCase("to_timestamp")) {
                    dc2.setDatatypeName("TIMESTAMP");
                }
                arguments.add(dc2);
                this.setFunctionArguments(arguments);
            }
        }
    }
    
    private int count(final String str, final String sub) {
        int cnt = 0;
        for (int i = 1, len = str.length(); i < len; ++i) {
            if (str.charAt(i) == sub.charAt(0)) {
                ++cnt;
            }
        }
        return cnt;
    }
    
    private void processFunctionArgumentsForRamco(final Vector colExp) {
        if (colExp != null) {
            for (int i = 0; i < colExp.size(); ++i) {
                if (colExp.get(i) instanceof TableColumn) {
                    if (colExp.get(i).getColumnName().startsWith("@")) {
                        final FunctionCalls fc = new FunctionCalls();
                        final TableColumn tc = new TableColumn();
                        final SelectColumn sc = new SelectColumn();
                        final Vector tableColVector = new Vector();
                        tableColVector.add(colExp.get(i));
                        sc.setColumnExpression(tableColVector);
                        final Vector selectColumnVector = new Vector();
                        selectColumnVector.add(sc);
                        tc.setColumnName("FetchSqlDtFormat");
                        tc.setTableName("dbo");
                        fc.setFunctionName(tc);
                        colExp.set(i, fc);
                    }
                }
                else if (colExp.get(i) instanceof String) {
                    if (colExp.get(i).trim().startsWith("@")) {
                        colExp.set(i, "dbo.FetchSqlDtFormat(" + colExp.get(i) + ")");
                    }
                }
                else if (colExp.get(i) instanceof SelectColumn) {
                    final Vector selColExp = colExp.get(i).getColumnExpression();
                    this.processFunctionArgumentsForRamco(selColExp);
                }
                else if (colExp.get(i) instanceof FunctionCalls) {
                    final Vector FunctionArgs = colExp.get(i).getFunctionArguments();
                    this.processFunctionArgumentsForRamco(FunctionArgs);
                }
            }
        }
    }
    
    private boolean ramcoSpecificDateFormatHandling(final Vector functionArguments) {
        if (functionArguments.get(0) instanceof SelectColumn) {
            final Vector colExp = functionArguments.get(0).getColumnExpression();
            if (colExp != null && colExp.size() > 4 && colExp.get(1) instanceof String && colExp.get(1).trim().equals("+") && colExp.get(2) instanceof String && colExp.get(2).startsWith("'") && colExp.get(2).endsWith("'") && colExp.get(3) instanceof String && colExp.get(3).trim().equals("+") && colExp.get(4) instanceof String && colExp.get(4).startsWith("@")) {
                return true;
            }
        }
        return false;
    }
    
    private String reformatHardCodedDate(String dateArg, String dateFormat) {
        final StringBuffer newDateArg = new StringBuffer();
        if (dateArg.startsWith("'")) {
            dateArg = dateArg.substring(1, dateArg.length() - 1);
        }
        if (dateFormat.startsWith("'")) {
            dateFormat = dateFormat.substring(1, dateFormat.length() - 1);
        }
        dateFormat = dateFormat.replaceAll("HH24", "HH");
        if (dateFormat.indexOf("HH12") != -1) {
            dateFormat = dateFormat.replaceAll("HH12", "HH");
        }
        String firstPart = "";
        String secondPart = "";
        String thirdPart = "";
        boolean splitDate = false;
        String splitWhere = "";
        final String spaceOrDot = "";
        if (dateFormat.lastIndexOf("-") > 1) {
            splitWhere = "-";
            splitDate = true;
        }
        else if (dateFormat.lastIndexOf("/") > 1) {
            splitWhere = "/";
            splitDate = true;
        }
        else {
            splitDate = false;
        }
        if (splitDate) {
            final String[] dateParts = dateFormat.split(splitWhere);
            for (int k = 0; k < dateParts.length; ++k) {
                if (k == 0) {
                    firstPart = dateParts[0];
                }
                if (k == 1) {
                    secondPart = dateParts[1];
                }
                if (k == 2) {
                    thirdPart = dateParts[2];
                }
            }
        }
        if (dateArg.indexOf(splitWhere) == -1) {
            newDateArg.append(dateArg.substring(0, firstPart.length()) + splitWhere);
            newDateArg.append(dateArg.substring(firstPart.length(), firstPart.length() + secondPart.length()) + splitWhere);
            newDateArg.append(dateArg.substring(firstPart.length() + secondPart.length()));
        }
        else if (splitWhere != "") {
            final String[] dateArgParts = dateArg.split(splitWhere);
            String dateArgFirst = "";
            String dateArgSecond = "";
            String dateArgThird = "";
            for (int i = 0; i < dateArgParts.length; ++i) {
                if (i == 0) {
                    dateArgFirst = dateArgParts[0];
                }
                if (i == 1) {
                    dateArgSecond = dateArgParts[1];
                }
                if (i == 2) {
                    dateArgThird = dateArgParts[2];
                }
            }
            if (dateArgFirst != "" && dateArgFirst.length() < firstPart.length() && firstPart.indexOf("Y") == -1 && Character.isDigit(dateArgFirst.charAt(0))) {
                for (int diff = firstPart.length() - dateArgFirst.length(), j = 0; j < diff; ++j) {
                    dateArgFirst = 0 + dateArgFirst;
                }
            }
            if (dateArgSecond != "" && dateArgSecond.length() < secondPart.length() && secondPart.indexOf("Y") == -1 && Character.isDigit(dateArgSecond.charAt(0))) {
                for (int diff = secondPart.length() - dateArgSecond.length(), j = 0; j < diff; ++j) {
                    dateArgSecond = 0 + dateArgSecond;
                }
            }
            if (dateArgThird != "" && dateArgThird.length() < thirdPart.length() && thirdPart.indexOf("Y") == -1 && Character.isDigit(dateArgThird.charAt(0))) {
                final int diff = thirdPart.length() - dateArgThird.length();
                final int thirdArgLength = dateArgThird.length();
                for (int l = 0; l < diff; ++l) {
                    if (thirdPart.charAt(l + thirdArgLength) == 'D') {
                        dateArgThird = 0 + dateArgThird;
                    }
                    else if (thirdPart.charAt(l + thirdArgLength) == 'H' || thirdPart.charAt(l + thirdArgLength) == 'S') {
                        dateArgThird += 0;
                    }
                    else if (thirdPart.charAt(l + thirdArgLength) == 'M') {
                        if (thirdPart.charAt(l + thirdArgLength + 1) == 'I') {
                            dateArgThird += "00";
                            ++l;
                        }
                        else {
                            dateArgThird = 0 + dateArgThird;
                        }
                    }
                    else {
                        dateArgThird += thirdPart.charAt(l + thirdArgLength);
                    }
                }
            }
            newDateArg.append(dateArgFirst + splitWhere);
            newDateArg.append(dateArgSecond + splitWhere);
            newDateArg.append(dateArgThird);
        }
        else {
            newDateArg.append(dateArg);
        }
        return "'" + newDateArg.toString() + "'";
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
    }
}
