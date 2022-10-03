package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import java.util.Collection;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class tochar extends FunctionCalls
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
        if (SwisSQLOptions.removeFormatForOracleToCharFunction && this.functionName.getColumnName().equalsIgnoreCase("to_char") && arguments.size() > 1) {
            arguments.removeElementAt(1);
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
            else if (this.functionArguments.get(i) instanceof String) {
                String s = this.functionArguments.get(i);
                if (s.trim().equalsIgnoreCase("SYSDATE")) {
                    s = "GETDATE()";
                }
                if (s.trim().equalsIgnoreCase("SYS_GUID")) {
                    s = "NEWID()";
                }
                arguments.addElement(s);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
            final SelectColumn sc = this.functionArguments.get(1);
            if (sc.getColumnExpression().get(0) instanceof String) {
                String format = sc.getColumnExpression().get(0);
                if (FunctionCalls.charToIntName) {
                    if ((format.trim().startsWith("'") && format.endsWith("'")) || format.trim().startsWith("@")) {
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt("VARCHAR(23)", 0);
                        this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format + ")");
                    }
                    else if (format.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                        String newformat = format.trim().substring(30);
                        if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                            newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                            this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                            this.functionArguments.setElementAt("VARCHAR(23)", 0);
                            this.functionArguments.addElement(newformat);
                        }
                        else {
                            arguments.add(2, this.functionArguments.get(1));
                            arguments.setElementAt(this.functionArguments.get(0), 1);
                            arguments.setElementAt("VARCHAR(23)", 0);
                        }
                    }
                    else {
                        boolean isNotProperString = false;
                        for (int j = 0; j < format.length(); ++j) {
                            if (!Character.isLetterOrDigit(format.charAt(j))) {
                                isNotProperString = true;
                            }
                        }
                        if (!isNotProperString) {
                            format = "dbo." + format.trim() + "INT()";
                            arguments.add(format);
                            arguments.setElementAt(this.functionArguments.get(0), 1);
                            arguments.setElementAt("VARCHAR(23)", 0);
                        }
                        else if (format.indexOf(40) != -1) {
                            format = format.substring(0, format.indexOf(40)).trim() + "INT" + format.substring(format.indexOf("("));
                            arguments.add(format);
                            arguments.setElementAt(this.functionArguments.get(0), 1);
                            arguments.setElementAt("VARCHAR(23)", 0);
                        }
                        else {
                            arguments.add(2, this.functionArguments.get(1));
                            arguments.setElementAt(this.functionArguments.get(0), 1);
                            arguments.setElementAt("VARCHAR(23)", 0);
                        }
                    }
                }
                else if (format.toUpperCase().indexOf("MM/DD/YYY") != -1) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(23)", 0);
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("101) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        this.functionArguments.addElement("101");
                    }
                }
                else if (format.toUpperCase().indexOf("DD.MM.YYY") != -1) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(23)", 0);
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("104) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        this.functionArguments.addElement("104");
                    }
                }
                else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(23)", 0);
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("103) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        this.functionArguments.addElement("103");
                    }
                }
                else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(23)", 0);
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("102) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        this.functionArguments.addElement("102");
                    }
                }
                else if (format.toUpperCase().indexOf("YYY-MM-DD") != -1) {
                    final FunctionCalls fc = new FunctionCalls();
                    final TableColumn innerFunction = new TableColumn();
                    innerFunction.setOwnerName(this.functionName.getOwnerName());
                    if (format.toUpperCase().indexOf("HH24:MI:SS") == -1 || format.toUpperCase().indexOf("HH:MI") == -1 || (format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1)) {
                        innerFunction.setTableName(this.functionName.getTableName());
                        innerFunction.setColumnName("CONVERT");
                        fc.setFunctionName(innerFunction);
                        final Vector args = new Vector();
                        args.addElement("VARCHAR(23)");
                        args.addElement(this.functionArguments.get(0));
                        args.addElement("112");
                        fc.setFunctionArguments(args);
                        this.functionArguments.setElementAt(fc, 1);
                    }
                    else {
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    }
                    this.functionArguments.setElementAt("DATETIME", 0);
                }
                else if (format.toUpperCase().indexOf("YYYY/MM/DD") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("111) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "111");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("YYYYMMDD") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("112) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "112");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("DD MON YYYY HH:MI:SS") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("113) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "113");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("DD-MM-YYY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("105) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "105");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("DD MON YYYY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("106) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "106");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("MON DD, YYYY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("107) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "107");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("MON DD YYYY HH:MI:SS") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("109) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "109");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("MM-DD-YYY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("110) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "110");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("DD-MM-YY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("5) + ' ' + CONVERT(VARCHAR(12)");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "5");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("YY.MM.DD") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("2) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "2");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("DD/MM/YY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("3) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "3");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("MM/DD/YY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("1) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "1");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("DD.MM.YY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("4) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "4");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("DD MON YY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("6) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "6");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("MON DD, YY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("7) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "7");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("MON DD YYYY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("9) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "9");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("MM-DD-YY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("10) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "10");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("YY/MM/DD") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("11) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "11");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("YYMMDD") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("12) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "12");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("DD MON YYYY") != -1) {
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("13) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        arguments.add(2, "13");
                    }
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
                else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 || (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1))) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(23)", 0);
                    this.functionArguments.addElement("108");
                }
                else if (format.trim().startsWith("'") && format.endsWith("'") && format.toLowerCase().indexOf(120) != -1) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("BINARY(4)", 0);
                }
                else if (format.toUpperCase().equals("'YYYY'")) {
                    this.functionName.setColumnName("DATEPART");
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("YYYY", 0);
                }
                else if (format.toUpperCase().equals("'MM'")) {
                    this.functionName.setColumnName("DATEPART");
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("MM", 0);
                }
                else if (format.toUpperCase().equals("'DD'")) {
                    this.functionName.setColumnName("DATEPART");
                    arguments.setElementAt(this.functionArguments.get(0), 1);
                    arguments.setElementAt("DD", 0);
                }
                else if (format.toUpperCase().equals("'DDMMYYYYHHMMSS'")) {
                    final String date = this.functionArguments.get(0).toString();
                    final String fmt = "CASE WHEN LEN(DATENAME(d, " + date + ")) = 1 THEN  '0'+ DATENAME(d, " + date + ") " + "ELSE DATENAME(d, " + date + ") END + " + "CAST(MONTH(" + date + ") AS VARCHAR) + " + "DATENAME(yyyy, " + date + ") + " + "DATENAME(hh, " + date + ") + " + "DATENAME(mi, " + date + ") + " + "DATENAME(ss, " + date + ")";
                    this.functionName.setColumnName(fmt);
                    this.setFunctionArguments(new Vector());
                    this.setOpenBracesForFunctionNameRequired(false);
                }
                else if (format.toUpperCase().equals("'HH24'")) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(2)", 0);
                    this.functionArguments.addElement("108");
                }
                else if (this.checkIfDecimalFormat(format)) {
                    if (format.trim().startsWith("'") && format.trim().endsWith("'")) {
                        format = format.substring(1, format.length() - 1);
                    }
                    final int length = format.length() - 1;
                    final int beginIndex = format.indexOf(".");
                    final int scal = length - beginIndex;
                    final String arg2 = "CONVERT( numeric(" + length + ", " + scal + "), " + this.functionArguments.get(0) + " )";
                    this.functionArguments.setElementAt(arg2, 1);
                    this.functionArguments.setElementAt("VARCHAR ", 0);
                }
                else {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(23)", 0);
                }
            }
            else if (sc.getColumnExpression().get(0) instanceof TableColumn) {
                final TableColumn tc = sc.getColumnExpression().get(0);
                String tcStr = tc.toString();
                if (FunctionCalls.charToIntName) {
                    if (tcStr.trim().startsWith("@")) {
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt("VARCHAR(23)", 0);
                        this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + tcStr + ")");
                    }
                    else {
                        boolean isNotProperString2 = false;
                        for (int k = 0; k < tcStr.length(); ++k) {
                            if (!Character.isLetterOrDigit(tcStr.charAt(k))) {
                                isNotProperString2 = true;
                            }
                        }
                        if (!isNotProperString2) {
                            if (!tcStr.toUpperCase().endsWith("INT")) {
                                tcStr = "dbo." + tcStr.trim() + "INT()";
                                arguments.add(tcStr);
                                arguments.setElementAt(this.functionArguments.get(0), 1);
                                arguments.setElementAt("VARCHAR(23)", 0);
                            }
                        }
                        else if (tcStr.indexOf(40) != -1) {
                            tcStr = tcStr.substring(0, tcStr.indexOf(40)).trim() + "INT" + tcStr.substring(tcStr.indexOf("("));
                            arguments.add(tcStr);
                            arguments.setElementAt(this.functionArguments.get(0), 1);
                            arguments.setElementAt("VARCHAR(23)", 0);
                        }
                        else {
                            arguments.add(2, this.functionArguments.get(1));
                            arguments.setElementAt(this.functionArguments.get(0), 1);
                            arguments.setElementAt("VARCHAR(23)", 0);
                        }
                    }
                }
                else {
                    this.functionArguments.add(2, this.functionArguments.get(1));
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(23)", 0);
                }
            }
            else if (sc.getColumnExpression().get(0) instanceof FunctionCalls) {
                final FunctionCalls fc2 = sc.getColumnExpression().get(0);
                if (FunctionCalls.charToIntName) {
                    final TableColumn tc2 = fc2.getFunctionName();
                    if (tc2.getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT")) {
                        tc2.setColumnName("");
                        tc2.setTableName(null);
                        tc2.setOwnerName(null);
                    }
                    else if (fc2.getFunctionArguments().size() == 0) {
                        tc2.setColumnName(tc2.getColumnName().trim() + "INT");
                    }
                    else {
                        this.processFunctionArgumentsForRamco(fc2.getFunctionArguments());
                    }
                }
                arguments.add(2, this.functionArguments.get(1));
                arguments.setElementAt(this.functionArguments.get(0), 1);
                arguments.setElementAt("VARCHAR(23)", 0);
            }
        }
        else if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof String) {
            String format2 = this.functionArguments.get(1);
            if (FunctionCalls.charToIntName) {
                if ((format2.trim().startsWith("'") && format2.endsWith("'")) || format2.trim().startsWith("@")) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR(23)", 0);
                    this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format2 + ")");
                }
                else if (format2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                    String newformat2 = format2.trim().substring(30);
                    if (newformat2.trim().startsWith("(") && newformat2.trim().endsWith(")")) {
                        newformat2 = newformat2.trim().substring(1, newformat2.trim().length() - 1);
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt("VARCHAR(23)", 0);
                        this.functionArguments.addElement(newformat2);
                    }
                    else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                    }
                }
                else {
                    boolean isNotProperString3 = false;
                    for (int l = 0; l < format2.length(); ++l) {
                        if (!Character.isLetterOrDigit(format2.charAt(l))) {
                            isNotProperString3 = true;
                        }
                    }
                    if (!isNotProperString3) {
                        format2 = "dbo." + format2.trim() + "INT()";
                        arguments.add(format2);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                    }
                    else if (format2.indexOf(40) != -1) {
                        format2 = format2.substring(0, format2.indexOf(40)).trim() + "INT" + format2.substring(format2.indexOf("("));
                        arguments.add(format2);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                    }
                    else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR(23)", 0);
                    }
                }
            }
            else if (format2.toUpperCase().indexOf("MM/DD/YYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("101) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("101");
                }
            }
            else if (format2.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("102) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("102");
                }
            }
            else if (format2.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("103) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("103");
                }
            }
            else if (format2.toUpperCase().indexOf("DD.MM.YYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("104) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("104");
                }
            }
            else if (format2.toUpperCase().indexOf("DD-MM-YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("105) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("105");
                }
            }
            else if (format2.toUpperCase().indexOf("DD MON YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("106) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("106");
                }
            }
            else if (format2.toUpperCase().indexOf("MON DD, YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("107) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("107");
                }
            }
            else if (format2.toUpperCase().indexOf("MON DD YYYY HH:MI:SS") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("109) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("109");
                }
            }
            else if (format2.toUpperCase().indexOf("MM-DD-YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("110) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("110");
                }
            }
            else if (format2.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("102) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("102");
                }
            }
            else if (format2.toUpperCase().indexOf("YY.MM.DD") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("2) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("2");
                }
            }
            else if (format2.toUpperCase().indexOf("YYYY/MM/DD") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("111) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("111");
                }
            }
            else if (format2.toUpperCase().indexOf("YYYY MM DD") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("112) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("112");
                }
            }
            else if (format2.toUpperCase().indexOf("DD MON YYYY HH:MI:SS") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("113) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("113");
                }
            }
            else if (format2.toUpperCase().indexOf("MM/DD/YY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("1) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("1");
                }
            }
            else if (format2.toUpperCase().indexOf("YY.MM.DD") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("2) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("2");
                }
            }
            else if (format2.toUpperCase().indexOf("DD/MM/YY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("3) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("3");
                }
            }
            else if (format2.toUpperCase().indexOf("DD.MM.YY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("4) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("4");
                }
            }
            else if (format2.toUpperCase().indexOf("DD-MM-YY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("5) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("5");
                }
            }
            else if (format2.toUpperCase().indexOf("DD-MON YY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("6) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("6");
                }
            }
            else if (format2.toUpperCase().indexOf("MON DD, YY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("7) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("7");
                }
            }
            else if (format2.toUpperCase().indexOf("MON DD YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("9) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("9");
                }
            }
            else if (format2.toUpperCase().indexOf("MM-DD-YY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("10) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("10");
                }
            }
            else if (format2.toUpperCase().indexOf("YY/MM/DD") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("11) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("11");
                }
            }
            else if (format2.toUpperCase().indexOf("YYMMDD") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("12) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("12");
                }
                else {
                    this.functionArguments.addElement("12");
                }
            }
            else if (format2.toUpperCase().indexOf("DD MON YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("13) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("13");
                }
            }
            else if (format2.toUpperCase().indexOf("YYY-MM-DD") != -1) {
                final FunctionCalls fc2 = new FunctionCalls();
                final TableColumn innerFunction2 = new TableColumn();
                innerFunction2.setOwnerName(this.functionName.getOwnerName());
                if (format2.toUpperCase().indexOf("HH24:MI:SS") == -1 || format2.toUpperCase().indexOf("HH:MI") == -1 || (format2.toUpperCase().indexOf("AM") == -1 && format2.toUpperCase().indexOf("PM") == -1)) {
                    innerFunction2.setTableName(this.functionName.getTableName());
                    innerFunction2.setColumnName("CONVERT");
                    fc2.setFunctionName(innerFunction2);
                    final Vector args2 = new Vector();
                    args2.addElement("VARCHAR(23)");
                    args2.addElement(this.functionArguments.get(0));
                    args2.addElement("112");
                    fc2.setFunctionArguments(args2);
                    this.functionArguments.setElementAt(fc2, 1);
                }
                else {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                }
                this.functionArguments.setElementAt("DATETIME", 0);
            }
            else if (format2.toUpperCase().indexOf("HH24:MI:SS") != -1 || (format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1))) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
                this.functionArguments.addElement("108");
            }
            else if (format2.trim().startsWith("'") && format2.endsWith("'") && format2.toLowerCase().indexOf(120) != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("BINARY(4)", 0);
            }
            else {
                arguments.add(2, this.functionArguments.get(1));
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR(23)", 0);
            }
        }
        else if (this.functionArguments.size() == 1) {
            arguments.add(1, this.functionArguments.get(0));
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn fSc = this.functionArguments.get(0);
                final Vector fScVec = fSc.getColumnExpression();
                for (int fScVecSiz = fScVec.size(), v = 0; v < fScVecSiz; ++v) {
                    final Object fScVecArg = fScVec.elementAt(v);
                    if (fScVecArg instanceof TableColumn) {
                        final String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)fScVecArg);
                        if (dtype != null && dtype.indexOf("(") != -1) {
                            String dtypeSize = dtype.substring(dtype.indexOf("(") + 1, dtype.indexOf(")"));
                            if (dtypeSize.indexOf(",") != -1) {
                                dtypeSize = dtypeSize.substring(0, dtypeSize.indexOf(","));
                            }
                            arguments.setElementAt("VARCHAR(" + dtypeSize + ")", 0);
                        }
                        else {
                            arguments.setElementAt("VARCHAR(23)", 0);
                        }
                    }
                    else if (fScVecArg instanceof String) {
                        final String arg3 = fScVecArg.toString();
                        arguments.setElementAt("VARCHAR(" + arg3.length() + ")", 0);
                    }
                    else {
                        arguments.setElementAt("VARCHAR(23)", 0);
                    }
                }
            }
            else if (this.functionArguments.get(0) instanceof String) {
                final String arg4 = this.functionArguments.get(0).toString();
                if (arg4.startsWith("'")) {
                    arguments.setElementAt("VARCHAR(" + arg4.length() + ")", 0);
                }
                else {
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
            }
            else {
                arguments.setElementAt("VARCHAR(23)", 0);
            }
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
            else if (this.functionArguments.get(i) instanceof String) {
                String s = this.functionArguments.get(i);
                if (s.trim().equalsIgnoreCase("SYSDATE")) {
                    s = "GETDATE()";
                }
                if (s.trim().equalsIgnoreCase("SYS_GUID")) {
                    s = "NEWID()";
                }
                arguments.addElement(s);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
            final SelectColumn sc = this.functionArguments.get(1);
            if (sc.getColumnExpression().get(0) instanceof String) {
                final String format = sc.getColumnExpression().get(0);
                if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR", 0);
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("101) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        this.functionArguments.addElement("101");
                    }
                }
                else if (format.toUpperCase().indexOf("DD.MM.YYYY") != -1) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR", 0);
                    if ((format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) || format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                        this.functionArguments.addElement("104) + ' ' + CONVERT(VARCHAR");
                        this.functionArguments.addElement(this.functionArguments.get(1));
                        this.functionArguments.addElement("108");
                    }
                    else {
                        this.functionArguments.addElement("104");
                    }
                }
                else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                    final FunctionCalls fc = new FunctionCalls();
                    final TableColumn innerFunction = new TableColumn();
                    innerFunction.setOwnerName(this.functionName.getOwnerName());
                    if (format.toUpperCase().indexOf("HH24:MI:SS") == -1 || format.toUpperCase().indexOf("HH:MI") == -1 || (format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1)) {
                        innerFunction.setTableName(this.functionName.getTableName());
                        innerFunction.setColumnName("CONVERT");
                        fc.setFunctionName(innerFunction);
                        final Vector args = new Vector();
                        args.addElement("VARCHAR");
                        args.addElement(this.functionArguments.get(0));
                        args.addElement("112");
                        fc.setFunctionArguments(args);
                        this.functionArguments.setElementAt(fc, 1);
                    }
                    else {
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    }
                    this.functionArguments.setElementAt("DATETIME", 0);
                }
                else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 || (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1))) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR", 0);
                    this.functionArguments.addElement("108");
                }
                else if (this.checkIfDecimalFormat(format)) {
                    final int beginIndex = format.indexOf(".");
                    final int prec = format.length();
                    final int scal = prec - format.substring(beginIndex).length();
                    final String arg2 = "CONVERT( numeric(" + prec + ", " + scal + "), " + this.functionArguments.get(0) + " )";
                    this.functionArguments.setElementAt(arg2, 1);
                    this.functionArguments.setElementAt("VARCHAR ", 0);
                }
                else {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR", 0);
                }
            }
            else if (sc.getColumnExpression().get(0) instanceof TableColumn) {
                final TableColumn tc = sc.getColumnExpression().get(0);
                String tcStr = tc.toString();
                if (FunctionCalls.charToIntName) {
                    if (tcStr.trim().startsWith("@")) {
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt("VARCHAR", 0);
                        this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + tcStr + ")");
                    }
                    else {
                        boolean isNotProperString = false;
                        for (int j = 0; j < tcStr.length(); ++j) {
                            if (!Character.isLetterOrDigit(tcStr.charAt(j))) {
                                isNotProperString = true;
                            }
                        }
                        if (!isNotProperString) {
                            if (!tcStr.toUpperCase().endsWith("INT")) {
                                tcStr = "dbo." + tcStr.trim() + "INT()";
                                arguments.add(tcStr);
                                arguments.setElementAt(this.functionArguments.get(0), 1);
                                arguments.setElementAt("VARCHAR", 0);
                            }
                        }
                        else if (tcStr.indexOf(40) != -1) {
                            tcStr = tcStr.substring(0, tcStr.indexOf(40)).trim() + "INT" + tcStr.substring(tcStr.indexOf("("));
                            arguments.add(tcStr);
                            arguments.setElementAt(this.functionArguments.get(0), 1);
                            arguments.setElementAt("VARCHAR", 0);
                        }
                        else {
                            arguments.add(2, this.functionArguments.get(1));
                            arguments.setElementAt(this.functionArguments.get(0), 1);
                            arguments.setElementAt("VARCHAR", 0);
                        }
                    }
                }
                else {
                    this.functionArguments.add(2, this.functionArguments.get(1));
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR", 0);
                }
            }
            else if (sc.getColumnExpression().get(0) instanceof FunctionCalls) {
                final FunctionCalls fc2 = sc.getColumnExpression().get(0);
                if (FunctionCalls.charToIntName) {
                    final TableColumn tc2 = fc2.getFunctionName();
                    if (tc2.getColumnName().equalsIgnoreCase("CONVERTSQLSERVERDATEFORMAT")) {
                        tc2.setColumnName("");
                        tc2.setTableName(null);
                        tc2.setOwnerName(null);
                    }
                    else if (fc2.getFunctionArguments().size() == 0) {
                        tc2.setColumnName(tc2.getColumnName().trim() + "INT");
                    }
                    else {
                        this.processFunctionArgumentsForRamco(fc2.getFunctionArguments());
                    }
                }
                arguments.add(2, this.functionArguments.get(1));
                arguments.setElementAt(this.functionArguments.get(0), 1);
                arguments.setElementAt("VARCHAR", 0);
            }
        }
        else if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof String) {
            String format2 = this.functionArguments.get(1);
            if (FunctionCalls.charToIntName) {
                if ((format2.trim().startsWith("'") && format2.endsWith("'")) || format2.trim().startsWith("@")) {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                    this.functionArguments.setElementAt("VARCHAR", 0);
                    this.functionArguments.addElement("dbo.FetchSqlDtFormat(" + format2 + ")");
                }
                else if (format2.trim().toUpperCase().startsWith("DBO.CONVERTSQLSERVERDATEFORMAT")) {
                    String newformat = format2.trim().substring(30);
                    if (newformat.trim().startsWith("(") && newformat.trim().endsWith(")")) {
                        newformat = newformat.trim().substring(1, newformat.trim().length() - 1);
                        this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                        this.functionArguments.setElementAt("VARCHAR", 0);
                        this.functionArguments.addElement(newformat);
                    }
                    else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                    }
                }
                else {
                    boolean isNotProperString2 = false;
                    for (int k = 0; k < format2.length(); ++k) {
                        if (!Character.isLetterOrDigit(format2.charAt(k))) {
                            isNotProperString2 = true;
                        }
                    }
                    if (!isNotProperString2) {
                        format2 = "dbo." + format2.trim() + "INT()";
                        arguments.add(format2);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                    }
                    else if (format2.indexOf(40) != -1) {
                        format2 = format2.substring(0, format2.indexOf(40)).trim() + "INT" + format2.substring(format2.indexOf("("));
                        arguments.add(format2);
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                    }
                    else {
                        arguments.add(2, this.functionArguments.get(1));
                        arguments.setElementAt(this.functionArguments.get(0), 1);
                        arguments.setElementAt("VARCHAR", 0);
                    }
                }
            }
            else if (format2.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("101) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("101");
                }
            }
            else if (format2.toUpperCase().indexOf("DD.MM.YYYY") != -1) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR", 0);
                if ((format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1)) || format2.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    this.functionArguments.addElement("104) + ' ' + CONVERT(VARCHAR");
                    this.functionArguments.addElement(this.functionArguments.get(1));
                    this.functionArguments.addElement("108");
                }
                else {
                    this.functionArguments.addElement("104");
                }
            }
            else if (format2.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                final FunctionCalls fc2 = new FunctionCalls();
                final TableColumn innerFunction2 = new TableColumn();
                innerFunction2.setOwnerName(this.functionName.getOwnerName());
                if (format2.toUpperCase().indexOf("HH24:MI:SS") == -1 || format2.toUpperCase().indexOf("HH:MI") == -1 || (format2.toUpperCase().indexOf("AM") == -1 && format2.toUpperCase().indexOf("PM") == -1)) {
                    innerFunction2.setTableName(this.functionName.getTableName());
                    innerFunction2.setColumnName("CONVERT");
                    fc2.setFunctionName(innerFunction2);
                    final Vector args2 = new Vector();
                    args2.addElement("VARCHAR");
                    args2.addElement(this.functionArguments.get(0));
                    args2.addElement("112");
                    fc2.setFunctionArguments(args2);
                    this.functionArguments.setElementAt(fc2, 1);
                }
                else {
                    this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                }
                this.functionArguments.setElementAt("DATETIME", 0);
            }
            else if (format2.toUpperCase().indexOf("HH24:MI:SS") != -1 || (format2.toUpperCase().indexOf("HH:MI") != -1 && (format2.toUpperCase().indexOf("AM") != -1 || format2.toUpperCase().indexOf("PM") != -1))) {
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR", 0);
                this.functionArguments.addElement("108");
            }
            else {
                arguments.add(2, this.functionArguments.get(1));
                this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
                this.functionArguments.setElementAt("VARCHAR", 0);
            }
        }
        else if (this.functionArguments.size() == 1) {
            arguments.add(1, this.functionArguments.get(0));
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn fSc = this.functionArguments.get(0);
                final Vector fScVec = fSc.getColumnExpression();
                for (int fScVecSiz = fScVec.size(), v = 0; v < fScVecSiz; ++v) {
                    final Object fScVecArg = fScVec.elementAt(v);
                    if (fScVecArg instanceof TableColumn) {
                        final String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)fScVecArg);
                        if (dtype != null && dtype.indexOf("(") != -1) {
                            String dtypeSize = dtype.substring(dtype.indexOf("(") + 1, dtype.indexOf(")"));
                            if (dtypeSize.indexOf(",") != -1) {
                                dtypeSize = dtypeSize.substring(0, dtypeSize.indexOf(","));
                            }
                            arguments.setElementAt("VARCHAR(" + dtypeSize + ")", 0);
                        }
                        else {
                            arguments.setElementAt("VARCHAR(23)", 0);
                        }
                    }
                    else if (fScVecArg instanceof String) {
                        final String arg3 = fScVecArg.toString();
                        arguments.setElementAt("VARCHAR(" + arg3.length() + ")", 0);
                    }
                    else {
                        arguments.setElementAt("VARCHAR(23)", 0);
                    }
                }
            }
            else if (this.functionArguments.get(0) instanceof String) {
                final String arg4 = this.functionArguments.get(0).toString();
                if (arg4.startsWith("'")) {
                    arguments.setElementAt("VARCHAR(" + arg4.length() + ")", 0);
                }
                else {
                    arguments.setElementAt("VARCHAR(23)", 0);
                }
            }
            else {
                arguments.setElementAt("VARCHAR(23)", 0);
            }
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CHAR");
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
                this.functionName.setColumnName("TO_CHAR");
            }
            return;
        }
        if (this.functionArguments.size() == 2 && this.functionArguments.get(1) instanceof SelectColumn) {
            final SelectColumn forDatetimeToBeChanged = this.functionArguments.get(0);
            final SelectColumn sc = this.functionArguments.get(1);
            String datetimeToBeChangedString = "";
            TableColumn tc = null;
            if (forDatetimeToBeChanged.getColumnExpression().get(0) instanceof TableColumn) {
                tc = new TableColumn();
                tc = forDatetimeToBeChanged.getColumnExpression().get(0);
                datetimeToBeChangedString = tc.getColumnName();
            }
            final Vector datetimeVector = new Vector();
            if (sc.getColumnExpression().get(0) instanceof String) {
                final String format = sc.getColumnExpression().get(0);
                final Vector arg = new Vector();
                arg.add(this.functionArguments.get(0));
                final FunctionCalls dateFunction = new FunctionCalls();
                final TableColumn dateColumn = new TableColumn();
                dateColumn.setColumnName("DATE");
                dateFunction.setFunctionName(dateColumn);
                dateFunction.setFunctionArguments(arg);
                this.functionArguments.setElementAt(dateFunction, 0);
                if (datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIMESTAMP") || datetimeToBeChangedString.equalsIgnoreCase("CURRENT DATE")) {
                    this.functionArguments.setElementAt("CURRENT DATE", 0);
                    datetimeToBeChangedString = "CURRENT TIME";
                }
                if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                    if (datetimeToBeChangedString != "") {
                        if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                            this.functionArguments.setElementAt("USA) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                            this.functionArguments.addElement("USA");
                        }
                        else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                            this.functionArguments.setElementAt("USA) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                            this.functionArguments.addElement("ISO");
                        }
                        else {
                            this.functionArguments.setElementAt("USA", 1);
                        }
                    }
                    else {
                        this.functionArguments.setElementAt("USA", 1);
                    }
                }
                else if (format.toUpperCase().indexOf("DD.MM.YYYY") != -1) {
                    if (datetimeToBeChangedString != "") {
                        if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                            this.functionArguments.setElementAt("EUR) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                            this.functionArguments.addElement("USA");
                        }
                        else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                            this.functionArguments.setElementAt("EUR) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                            this.functionArguments.addElement("EUR");
                        }
                        else {
                            this.functionArguments.setElementAt("USA", 1);
                        }
                    }
                    else {
                        this.functionArguments.setElementAt("EUR", 1);
                    }
                }
                else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                    if (datetimeToBeChangedString != "") {
                        if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                            this.functionArguments.setElementAt("ISO) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                            this.functionArguments.addElement("USA");
                        }
                        else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                            this.functionArguments.setElementAt("ISO) || ' ' || CHAR(" + datetimeToBeChangedString, 1);
                            this.functionArguments.addElement("ISO");
                        }
                        else {
                            this.functionArguments.setElementAt("USA", 1);
                        }
                    }
                    else {
                        this.functionArguments.setElementAt("ISO", 1);
                    }
                }
                else if (format.toUpperCase().indexOf("DD-MM-YYYY") != -1) {
                    this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2) || '-' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2) || '-' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4)");
                    this.setIfTime(datetimeToBeChangedString, format, tc);
                    this.setDummyArgs();
                }
                else if (format.toUpperCase().indexOf("YYYY-DD-MM") != -1) {
                    this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4) || '-' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2) || '-' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2)");
                    this.setIfTime(datetimeToBeChangedString, format, tc);
                    this.setDummyArgs();
                }
                else if (format.toUpperCase().indexOf("YYYY.DD.MM") != -1) {
                    this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4) || '.' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2) || '.' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2)");
                    this.setIfTime(datetimeToBeChangedString, format, tc);
                    this.setDummyArgs();
                }
                else if (format.toUpperCase().indexOf("YYYY.MM.DD") != -1) {
                    this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4) || '.' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2) || '.' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2)");
                    this.setIfTime(datetimeToBeChangedString, format, tc);
                    this.setDummyArgs();
                }
                else if (format.toUpperCase().indexOf("YYYY/MM/DD") != -1) {
                    this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4) || '/' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2) || '/' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2)");
                    this.setIfTime(datetimeToBeChangedString, format, tc);
                    this.setDummyArgs();
                }
                else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                    this.functionName.setColumnName("SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 1,2) || '/' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 4,2) || '/' || SUBSTR(CHAR(" + this.functionArguments.get(0) + ", EUR), 7,4)");
                    this.setIfTime(datetimeToBeChangedString, format, tc);
                    this.setDummyArgs();
                }
                else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1) && format.toUpperCase().indexOf("YY") == -1) {
                    if (datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIMESTAMP") || datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIME")) {
                        this.functionArguments.setElementAt("CURRENT TIME", 0);
                    }
                    this.functionArguments.setElementAt("USA", 1);
                }
                else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 && format.toUpperCase().indexOf("YY") == -1) {
                    if (datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIMESTAMP") || datetimeToBeChangedString.equalsIgnoreCase("CURRENT TIME")) {
                        this.functionArguments.setElementAt("CURRENT TIME", 0);
                    }
                    this.functionArguments.setElementAt("ISO", 1);
                }
                else if (format.toUpperCase().indexOf("YYYY.IW") != -1) {
                    this.functionName.setColumnName("LTRIM(RTRIM(CHAR(YEAR(" + this.functionArguments.get(0) + ")))) || '.' || LTRIM(RTRIM(CHAR(WEEK(" + this.functionArguments.get(0) + "))))");
                    this.setDummyArgs();
                }
                else if (format.toUpperCase().indexOf("YYYY.MM") != -1) {
                    this.functionName.setColumnName("LTRIM(RTRIM(CHAR(YEAR(" + this.functionArguments.get(0) + ")))) || '.' || LTRIM(RTRIM(CHAR(MONTH(" + this.functionArguments.get(0) + "))))");
                    this.setDummyArgs();
                }
                else if (format.toUpperCase().indexOf("HH") == -1) {
                    this.functionArguments.setElementAt("USA", 1);
                }
                else {
                    this.functionName.setColumnName("CHAR(" + this.functionArguments.get(0) + ", USA)");
                    this.setIfTime(datetimeToBeChangedString, format, tc);
                    this.setDummyArgs();
                }
            }
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
        this.functionName.setColumnName("DATE_FORMAT");
        final Vector arguments = new Vector();
        final boolean convertToCast = false;
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
            final SelectColumn sc = this.functionArguments.get(1);
            if (sc.getColumnExpression().get(0) instanceof String) {
                final String format = sc.getColumnExpression().get(0);
                String mysqlFormat = "";
                if (format.toUpperCase().indexOf("MM/DD/YYYY") != -1) {
                    mysqlFormat = "%m/%d/%Y";
                    if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                        mysqlFormat += " %T";
                    }
                    else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                        mysqlFormat += " %r";
                    }
                }
                else if (format.toUpperCase().indexOf("DD.MM.YYYY") != -1) {
                    mysqlFormat = "%d.%m.%Y";
                    if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                        mysqlFormat += " %T";
                    }
                    else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                        mysqlFormat += " %r";
                    }
                }
                else if (format.toUpperCase().indexOf("DD/MM/YYYY") != -1) {
                    mysqlFormat = "%d/%m/%Y";
                    if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                        mysqlFormat += " %T";
                    }
                    else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                        mysqlFormat += " %r";
                    }
                }
                else if (format.toUpperCase().indexOf("YYYY-MM-DD") != -1) {
                    mysqlFormat = "%Y-%m-%d";
                    if (format.toUpperCase().indexOf("HH:MI:SS") != -1) {
                        mysqlFormat += " %T";
                    }
                    else if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                        mysqlFormat += " %r";
                    }
                }
                else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                    mysqlFormat = "%T";
                }
                else {
                    if (format.toUpperCase().indexOf("HH:MI") == -1 || (format.toUpperCase().indexOf("AM") == -1 && format.toUpperCase().indexOf("PM") == -1)) {
                        throw new ConvertException("DATE FORMAT : " + format + " is yet to be supported in to_char()");
                    }
                    mysqlFormat = "%r";
                }
                mysqlFormat = "'" + mysqlFormat + "'";
                this.functionArguments.setElementAt(mysqlFormat, 1);
            }
        }
        if (this.functionArguments.size() == 1 && this.functionArguments.get(0) instanceof SelectColumn) {
            final SelectColumn sc = this.functionArguments.get(0);
            final Vector colExp = sc.getColumnExpression();
            if (colExp.size() == 1 && colExp.get(0) instanceof String) {
                final String argument = colExp.get(0);
                if (!argument.startsWith("'")) {
                    this.functionName.setColumnName("CAST");
                    final CharacterClass cc = new CharacterClass();
                    cc.setDatatypeName("CHAR");
                    this.setAsDatatype("AS");
                    this.functionArguments.add(cc);
                }
            }
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionArguments.size() == 1) {
            final SelectColumn sc = new SelectColumn();
            final Vector colExpr = new Vector();
            this.functionName.setColumnName("CAST");
            final CharacterClass cc = new CharacterClass();
            cc.setDatatypeName("VARCHAR");
            cc.setSize("4000");
            cc.setOpenBrace("(");
            cc.setClosedBrace(")");
            this.setAsDatatype("AS");
            colExpr.add(cc);
            sc.setColumnExpression(colExpr);
            this.functionArguments.add(sc);
        }
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
    
    private void setDummyArgs() {
        final Vector args = new Vector();
        this.setFunctionArguments(args);
        this.setOpenBracesForFunctionNameRequired(false);
    }
    
    private void setIfTime(final String datetimeToBeChangedString, final String format, final TableColumn tc) {
        if (datetimeToBeChangedString.equals("CURRENT TIME")) {
            if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1)) {
                this.functionName.setColumnName(this.functionName.getColumnName() + " || ' ' || CHAR(" + datetimeToBeChangedString + ", USA)");
            }
            else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1) {
                this.functionName.setColumnName(this.functionName.getColumnName() + " || ' ' || CHAR(" + datetimeToBeChangedString + ", ISO)");
            }
        }
        else if (datetimeToBeChangedString != "") {
            if (format.toUpperCase().indexOf("HH:MI") != -1 && (format.toUpperCase().indexOf("AM") != -1 || format.toUpperCase().indexOf("PM") != -1) && tc != null) {
                this.functionName.setColumnName(this.functionName.getColumnName() + " || ' ' || CHAR(TIME(" + tc.toString() + "), USA)");
            }
            else if (format.toUpperCase().indexOf("HH24:MI:SS") != -1 && tc != null) {
                this.functionName.setColumnName(this.functionName.getColumnName() + " || ' ' || CHAR(TIME(" + tc.toString() + "), ISO)");
            }
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector v = this.getFunctionArguments();
        if (v != null && (v.elementAt(0) != null & v.elementAt(0) instanceof SelectColumn)) {
            final Vector colexpr = v.elementAt(0).getColumnExpression();
            if (colexpr.elementAt(0) != null && colexpr.elementAt(0) instanceof String && colexpr.elementAt(0).toString().startsWith("'")) {
                throw new ConvertException("\nTO_CHAR(literal_string) is not supported in TimesTen 5.1.21\n");
            }
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
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
            final CharacterClass cc = new CharacterClass();
            cc.setDatatypeName("VARCHAR");
            cc.setSize("4000");
            cc.setOpenBrace("(");
            cc.setClosedBrace(")");
            this.setAsDatatype("AS");
            arguments.add(cc);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        if (this.functionArguments.size() == 3) {
            this.functionArguments.removeElementAt(2);
        }
        if (this.functionArguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final CharacterClass cc = new CharacterClass();
            cc.setDatatypeName("VARCHAR");
            cc.setSize("50");
            if (SwisSQLOptions.castCharDatatypeAsCaseSpecific) {
                cc.setCaseSpecificPhrase("CASESPECIFIC");
            }
            cc.setOpenBrace("(");
            cc.setClosedBrace(")");
            this.setAsDatatype("AS");
            this.functionArguments.add(cc);
        }
        else if (this.functionArguments.size() == 2) {
            final FunctionCalls newCastFunction = new FunctionCalls();
            final TableColumn newTableColumn = new TableColumn();
            newTableColumn.setColumnName("CAST");
            final Vector newFunctionArgs = new Vector();
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn firstArg = this.functionArguments.get(0);
                if (firstArg.getColumnExpression().indexOf("+") != -1 || firstArg.getColumnExpression().indexOf("-") != -1) {
                    for (int fi = 0; fi < firstArg.getColumnExpression().size(); ++fi) {
                        if (firstArg.getColumnExpression().get(fi) instanceof TableColumn) {
                            final FunctionCalls castDate = new FunctionCalls();
                            final TableColumn castDateName = new TableColumn();
                            castDate.setFunctionName(castDateName);
                            castDateName.setColumnName("CAST");
                            final DateClass cc2 = new DateClass();
                            cc2.setDatatypeName("DATE");
                            castDate.setAsDatatype("AS");
                            final Vector castDateArgs = new Vector();
                            castDateArgs.add(firstArg.getColumnExpression().get(fi));
                            castDateArgs.add(cc2);
                            castDate.setFunctionArguments(castDateArgs);
                            firstArg.getColumnExpression().setElementAt(castDate, fi);
                        }
                        else if (firstArg.getColumnExpression().get(fi) instanceof FunctionCalls) {
                            final FunctionCalls dateFunc = firstArg.getColumnExpression().get(fi);
                            if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                                final FunctionCalls castDate2 = new FunctionCalls();
                                final TableColumn castDateName2 = new TableColumn();
                                castDate2.setFunctionName(castDateName2);
                                castDateName2.setColumnName("CAST");
                                final DateClass cc3 = new DateClass();
                                cc3.setDatatypeName("DATE");
                                castDate2.setAsDatatype("AS");
                                final Vector castDateArgs2 = new Vector();
                                castDateArgs2.add(firstArg.getColumnExpression().get(fi));
                                castDateArgs2.add(cc3);
                                castDate2.setFunctionArguments(castDateArgs2);
                                firstArg.getColumnExpression().setElementAt(castDate2, fi);
                            }
                        }
                        else if (firstArg.getColumnExpression().get(fi) instanceof SelectColumn) {}
                    }
                }
            }
            newFunctionArgs.add(this.functionArguments.get(0));
            newCastFunction.setAsDatatype("AS");
            newCastFunction.setFunctionName(newTableColumn);
            final Object formatString = this.functionArguments.get(1);
            boolean createCastFunction = true;
            if (formatString instanceof String) {
                newFunctionArgs.add(this.functionArguments.get(1));
            }
            else if (formatString instanceof SelectColumn) {
                final SelectColumn scn = this.functionArguments.get(1);
                final Object scnColObj = scn.getColumnExpression().get(0);
                if (scnColObj instanceof String) {
                    String scnColStr = scnColObj.toString().toUpperCase();
                    if (scnColStr.indexOf("$") != -1 || scnColStr.indexOf("FM") != -1 || scnColStr.indexOf("IW") != -1 || scnColStr.indexOf("J") != -1 || scnColStr.indexOf("Q") != -1 || scnColStr.indexOf("DAY") != -1 || scnColStr.indexOf("RN") != -1 || (scnColStr.indexOf("DD") == -1 && scnColStr.indexOf("DY") == -1 && scnColStr.indexOf("D") != -1) || (scnColStr.indexOf("DL") == -1 && scnColStr.indexOf("L") != -1) || (scnColStr.indexOf("FF") == -1 && scnColStr.length() > 2 && (Character.isDigit(scnColStr.charAt(1)) || Character.isDigit(scnColStr.charAt(scnColStr.length() - 2))))) {
                        createCastFunction = false;
                    }
                    else if (scnColStr.indexOf("WW") != -1) {
                        createCastFunction = false;
                        scnColStr = scnColStr.replaceAll("WW", "IW");
                        scn.getColumnExpression().setElementAt(scnColStr, 0);
                    }
                    else if (scnColStr.indexOf(":") != -1 || scnColStr.indexOf("HH") != -1) {
                        if (scnColStr.indexOf("HH24") != -1) {
                            scnColStr = scnColStr.replaceAll("HH24", "HH");
                        }
                        if (scnColStr.indexOf("AM") != -1) {
                            scnColStr = scnColStr.replaceAll("AM", "T");
                        }
                        else if (scnColStr.indexOf("PM") != -1) {
                            scnColStr = scnColStr.replaceAll("PM", "T");
                        }
                        if (scnColStr.indexOf(" ") != -1) {
                            scnColStr = scnColStr.replaceAll(" ", "B");
                        }
                        else if (scnColStr.indexOf(".") != -1) {
                            scnColStr = scnColStr.replaceAll("\\.", "B");
                        }
                        if (scnColStr.indexOf("DAY") != -1 || scnColStr.indexOf("MON") != -1 || scnColStr.indexOf("YY") != -1 || scnColStr.indexOf("YEAR") != -1 || scnColStr.indexOf("RR") != -1 || scnColStr.indexOf("MM") != -1 || scnColStr.indexOf("IY") != -1 || scnColStr.indexOf("DD") != -1 || scnColStr.indexOf("DY") != -1 || scnColStr.indexOf("CC") != -1) {
                            if (scnColStr.indexOf("MONTH") != -1) {
                                scnColStr = scnColStr.replaceAll("MONTH", "MMMM");
                            }
                            if (scnColStr.indexOf("MON") != -1) {
                                scnColStr = scnColStr.replaceAll("MON", "MMM");
                            }
                            if (scnColStr.indexOf("RR") != -1) {
                                scnColStr = scnColStr.replaceAll("RR", "YY");
                            }
                            if (scnColStr.indexOf("IYYY") != -1) {
                                scnColStr = scnColStr.replaceAll("IYYY", "YYYY");
                            }
                            if (scnColStr.indexOf("DY") != -1) {
                                scnColStr = scnColStr.replaceAll("DY", "EEE");
                            }
                        }
                        final DateClass dc = new DateClass();
                        dc.setDatatypeName("TIMESTAMP");
                        dc.setOpenBrace("(");
                        dc.setClosedBrace(")");
                        dc.setSize("0");
                        scn.getColumnExpression().setElementAt(" FORMAT " + scnColStr, 0);
                        scn.getColumnExpression().insertElementAt(dc, 0);
                    }
                    else if (scnColStr.indexOf("G") != -1 || scnColStr.indexOf("X") != -1) {
                        scn.getColumnExpression().setElementAt(scnColStr.replaceAll(" ", "B"), 0);
                    }
                    else if (scnColStr.indexOf("DAY") != -1 || scnColStr.indexOf("MON") != -1 || scnColStr.indexOf("YY") != -1 || scnColStr.indexOf("YEAR") != -1 || scnColStr.indexOf("RR") != -1 || scnColStr.indexOf("MM") != -1 || scnColStr.indexOf("IY") != -1 || scnColStr.indexOf("DD") != -1 || scnColStr.indexOf("DY") != -1 || scnColStr.indexOf("CC") != -1) {
                        if (scnColStr.indexOf("MONTH") != -1) {
                            scnColStr = scnColStr.replaceAll("MONTH", "MMMM");
                        }
                        if (scnColStr.indexOf("MON") != -1) {
                            scnColStr = scnColStr.replaceAll("MON", "MMM");
                        }
                        if (scnColStr.indexOf("RR") != -1) {
                            scnColStr = scnColStr.replaceAll("RR", "YY");
                        }
                        if (scnColStr.indexOf("IYYY") != -1) {
                            scnColStr = scnColStr.replaceAll("IYYY", "YYYY");
                        }
                        if (scnColStr.indexOf("DY") != -1) {
                            scnColStr = scnColStr.replaceAll("DY", "EEE");
                        }
                        scn.getColumnExpression().setElementAt(scnColStr.replaceAll(" ", "B"), 0);
                        final DateClass dc = new DateClass();
                        dc.setDatatypeName("DATE");
                        scn.getColumnExpression().setElementAt(" FORMAT " + scnColStr.replaceAll(" ", "B"), 0);
                        scn.getColumnExpression().insertElementAt(dc, 0);
                    }
                    if (!scnColStr.startsWith("'") && !scnColStr.endsWith("'")) {
                        try {
                            Double.parseDouble(scnColStr);
                            scnColStr = "'" + scnColStr + "'";
                            scn.getColumnExpression().setElementAt(scnColStr, 0);
                            createCastFunction = false;
                        }
                        catch (final Exception ex) {}
                    }
                    newFunctionArgs.add(this.functionArguments.get(1));
                }
            }
            newCastFunction.setFunctionArguments(newFunctionArgs);
            if (createCastFunction) {
                this.functionName.setColumnName("CAST");
                final CharacterClass cc4 = new CharacterClass();
                cc4.setDatatypeName("VARCHAR");
                cc4.setSize("50");
                if (SwisSQLOptions.castCharDatatypeAsCaseSpecific) {
                    cc4.setCaseSpecificPhrase("CASESPECIFIC");
                }
                cc4.setOpenBrace("(");
                cc4.setClosedBrace(")");
                this.setAsDatatype("AS");
                final SelectColumn fnArgCol = new SelectColumn();
                final Vector fnArgColExp = new Vector();
                fnArgColExp.add(newCastFunction);
                fnArgCol.setColumnExpression(fnArgColExp);
                this.functionArguments.setElementAt(fnArgCol, 0);
                this.functionArguments.setElementAt(cc4, 1);
            }
            else if (this.functionArguments.size() > 1 && this.functionArguments.elementAt(1).toString().equalsIgnoreCase("'d'")) {
                final FunctionCalls tocharFn = new FunctionCalls();
                final TableColumn tocharFnName = new TableColumn();
                tocharFnName.setColumnName(this.functionName.getColumnName());
                tocharFn.setFunctionName(tocharFnName);
                final Vector tocharFnArgs = new Vector();
                tocharFnArgs.addAll(this.functionArguments);
                tocharFn.setFunctionArguments(tocharFnArgs);
                this.functionName.setColumnName("CAST");
                final NumericClass nc = new NumericClass();
                nc.setDatatypeName("INTEGER");
                nc.setPrecision(null);
                nc.setScale(null);
                nc.setOpenBrace(null);
                nc.setClosedBrace(null);
                this.setAsDatatype("AS");
                final SelectColumn fnArgCol2 = new SelectColumn();
                final Vector fnArgColExp2 = new Vector();
                fnArgColExp2.add(tocharFn);
                fnArgCol2.setColumnExpression(fnArgColExp2);
                this.functionArguments.setElementAt(fnArgCol2, 0);
                this.functionArguments.setElementAt(nc, 1);
            }
        }
    }
    
    private boolean checkIfDecimalFormat(final String format) {
        final int beginIndex = format.indexOf(".");
        final int lastIndex = format.lastIndexOf(".");
        if (beginIndex != -1 && beginIndex == lastIndex) {
            try {
                String tempFormat = format;
                if (tempFormat.startsWith("'")) {
                    tempFormat = tempFormat.substring(1, tempFormat.length() - 1);
                }
                Integer.parseInt(tempFormat.substring(0, beginIndex - 1));
                Integer.parseInt(tempFormat.substring(beginIndex, tempFormat.length()));
                return true;
            }
            catch (final NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
    }
}
