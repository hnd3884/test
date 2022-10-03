package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class trunc extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("trunc") || this.functionName.getColumnName().equalsIgnoreCase("truncate")) {
            this.functionName.setColumnName("TRUNC");
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            this.setFunctionArguments(arguments);
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("integer") || this.functionName.getColumnName().equalsIgnoreCase("int")) {
            this.functionName.setColumnName("TRUNC");
            final FunctionCalls subFunction = new FunctionCalls();
            final TableColumn subTC = new TableColumn();
            subTC.setColumnName("TO_NUMBER");
            subFunction.setFunctionName(subTC);
            final Vector subFunctionArgs = new Vector();
            final Vector scColumnExpression = new Vector();
            final SelectColumn sc = new SelectColumn();
            subFunctionArgs.add(this.functionArguments.get(0));
            subFunction.setFunctionArguments(subFunctionArgs);
            scColumnExpression.add(subFunction);
            sc.setColumnExpression(scColumnExpression);
            final Vector newArgument = new Vector();
            newArgument.add(sc);
            this.setFunctionArguments(newArgument);
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("decimal") || this.functionName.getColumnName().equalsIgnoreCase("dec")) {
            this.functionName.setColumnName("TRUNC");
            final FunctionCalls subFunction = new FunctionCalls();
            final TableColumn subTC = new TableColumn();
            subTC.setColumnName("TO_NUMBER");
            subFunction.setFunctionName(subTC);
            final Vector subFunctionArgs = new Vector();
            final Vector scColumnExpression = new Vector();
            final SelectColumn sc = new SelectColumn();
            subFunctionArgs.add(this.functionArguments.get(0));
            subFunction.setFunctionArguments(subFunctionArgs);
            scColumnExpression.add(subFunction);
            sc.setColumnExpression(scColumnExpression);
            Object scaleObj = null;
            if (this.functionArguments.size() > 2) {
                scaleObj = this.functionArguments.get(2);
            }
            final Vector newArgument2 = new Vector();
            newArgument2.add(sc);
            if (scaleObj != null) {
                newArgument2.add(scaleObj);
            }
            this.setFunctionArguments(newArgument2);
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("second")) {
            this.functionName.setColumnName("TRUNC");
            final FunctionCalls subFunction = new FunctionCalls();
            final TableColumn subTC = new TableColumn();
            subTC.setColumnName("EXTRACT");
            subFunction.setFunctionName(subTC);
            final Vector subFunctionArgs = new Vector();
            final Object arg = this.functionArguments.get(0);
            if (arg instanceof SelectColumn) {
                ((SelectColumn)arg).getColumnExpression().add(0, "SECOND FROM");
                subFunctionArgs.add(((SelectColumn)arg).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                subFunctionArgs.add(arg);
            }
            subFunction.setFunctionArguments(subFunctionArgs);
            final SelectColumn sc = new SelectColumn();
            final Vector scColumnExpression2 = new Vector();
            scColumnExpression2.add(subFunction);
            sc.setColumnExpression(scColumnExpression2);
            final Vector newArgument2 = new Vector();
            newArgument2.add(sc);
            this.setFunctionArguments(newArgument2);
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        boolean isArgDate = false;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final Vector colExpressions = this.functionArguments.elementAt(i_count).getColumnExpression();
                if (colExpressions != null) {
                    for (int i = 0; i < colExpressions.size(); ++i) {
                        if (colExpressions.get(i) instanceof TableColumn) {
                            final TableColumn tc = colExpressions.get(i);
                            final String columnName = tc.getColumnName();
                            if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                                isArgDate = true;
                            }
                            else if (columnName != null) {
                                final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                if (dataType != null && dataType.toLowerCase().indexOf("date") != -1) {
                                    isArgDate = true;
                                }
                                else if (columnName.startsWith(":")) {
                                    if (SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName.substring(1))) {
                                        final String dataType2 = SwisSQLAPI.variableDatatypeMapping.get(columnName.substring(1));
                                        if (dataType2.toLowerCase().indexOf("date") != -1) {
                                            isArgDate = true;
                                        }
                                    }
                                }
                                else if (dataType == null && SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName)) {
                                    final String dataType2 = SwisSQLAPI.variableDatatypeMapping.get(columnName);
                                    if (dataType2.toLowerCase().indexOf("date") != -1) {
                                        isArgDate = true;
                                    }
                                }
                            }
                        }
                        else if (colExpressions.get(i) instanceof FunctionCalls) {
                            final FunctionCalls fc = colExpressions.get(i);
                            final TableColumn tc2 = fc.getFunctionName();
                            final String columnName2 = tc2.getColumnName();
                            if (tc2.getColumnName().toString().equalsIgnoreCase("TO_DATE")) {
                                isArgDate = true;
                            }
                            else {
                                final Vector v = fc.getFunctionArguments();
                                if (v != null) {
                                    for (int k = 0; k < v.size(); ++k) {
                                        final Object obj = v.elementAt(k);
                                        if (obj instanceof SelectColumn) {
                                            final Vector tcV = ((SelectColumn)obj).getColumnExpression();
                                            if (tcV != null) {
                                                for (int m = 0; m < tcV.size(); ++m) {
                                                    final Object tcObj = tcV.elementAt(m);
                                                    if (tcObj instanceof TableColumn) {
                                                        final String dataType3 = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)tcObj);
                                                        if (dataType3 != null && dataType3.toLowerCase().indexOf("date") != -1) {
                                                            isArgDate = true;
                                                        }
                                                        else if (columnName2.startsWith(":")) {
                                                            if (SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName2.substring(1))) {
                                                                final String dataType4 = SwisSQLAPI.variableDatatypeMapping.get(columnName2.substring(1));
                                                                if (dataType4.toLowerCase().indexOf("date") != -1) {
                                                                    isArgDate = true;
                                                                }
                                                            }
                                                        }
                                                        else if (dataType3 == null && SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName2)) {
                                                            final String dataType4 = SwisSQLAPI.variableDatatypeMapping.get(columnName2);
                                                            if (dataType4.toLowerCase().indexOf("date") != -1) {
                                                                isArgDate = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (colExpressions.get(i) instanceof SelectQueryStatement) {
                            final SelectQueryStatement sqs = colExpressions.get(i);
                            final SelectStatement ss = sqs.getSelectStatement();
                            final Vector selectColV = ss.getSelectItemList();
                            final SelectColumn sc = selectColV.get(0);
                            final Vector colExprV = sc.getColumnExpression();
                            for (int j = 0; j < colExprV.size(); ++j) {
                                final Object obj2 = colExprV.get(j);
                                if (obj2 instanceof FunctionCalls) {
                                    final FunctionCalls fc2 = (FunctionCalls)obj2;
                                    final Vector funcArgV = fc2.getFunctionArguments();
                                    if (funcArgV != null) {
                                        for (int l = 0; l < funcArgV.size(); ++l) {
                                            final Object funcarg = funcArgV.get(l);
                                            if (funcarg instanceof SelectColumn) {
                                                final Vector colExpV = ((SelectColumn)funcarg).getColumnExpression();
                                                if (colExpV.get(0) instanceof TableColumn) {
                                                    final String dataType5 = MetadataInfoUtil.getDatatypeName(from_sqs, colExpV.get(0));
                                                    if (dataType5 != null && dataType5.toLowerCase().indexOf("date") != -1) {
                                                        isArgDate = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (colExpressions.get(i) instanceof String && (colExpressions.get(i).toString().equalsIgnoreCase("'MONTH'") || colExpressions.get(i).toString().equalsIgnoreCase("'DAY'") || colExpressions.get(i).toString().equalsIgnoreCase("'YEAR'") || colExpressions.get(i).toString().equalsIgnoreCase("'WEEK'") || colExpressions.get(i).toString().equalsIgnoreCase("'HOUR'") || colExpressions.get(i).toString().equalsIgnoreCase("'MINUTE'") || colExpressions.get(i).toString().equalsIgnoreCase("'SECOND'"))) {
                            isArgDate = true;
                        }
                    }
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.functionArguments.get(i_count) instanceof String) {
                String s = this.functionArguments.get(i_count);
                if (s.trim().equalsIgnoreCase("SYSDATE")) {
                    isArgDate = true;
                    s = "GETDATE()";
                }
                if (s.trim().equalsIgnoreCase("SYS_GUID")) {
                    s = "NEWID()";
                }
                arguments.addElement(s);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (FunctionCalls.charToIntName) {
            this.functionName.setColumnName("");
            this.functionName.setColumnName("TRUNC");
            this.functionName.setTableName("dbo");
        }
        else if (!isArgDate) {
            if (arguments.size() == 1) {
                this.functionName.setColumnName("FLOOR");
                final Vector newArguments = new Vector();
                newArguments.add(arguments.get(0));
                this.setFunctionArguments(arguments);
            }
            else if (arguments.size() == 2) {
                this.functionName.setColumnName("");
                final SelectColumn truncSelectColumn = new SelectColumn();
                final SelectColumn castSelectColumn = new SelectColumn();
                final SelectColumn floorSelectColumn = new SelectColumn();
                final FunctionCalls truncFunctionCall = new FunctionCalls();
                final TableColumn truncTableColumn = new TableColumn();
                truncTableColumn.setColumnName("FLOOR");
                final Vector firstFloorArg = new Vector();
                firstFloorArg.add(arguments.elementAt(0));
                truncFunctionCall.setFunctionName(truncTableColumn);
                truncFunctionCall.setFunctionArguments(firstFloorArg);
                final Vector truncArgument = new Vector();
                final FunctionCalls castFunctionCall = new FunctionCalls();
                final TableColumn castFunction = new TableColumn();
                castFunction.setColumnName("CAST");
                castFunctionCall.setFunctionName(castFunction);
                final Vector floorArgument = new Vector();
                final Vector floorInsideFloorArgument = new Vector();
                final FunctionCalls floorFunctionCall = new FunctionCalls();
                final TableColumn floorTableColumn = new TableColumn();
                floorTableColumn.setColumnName("FLOOR");
                floorArgument.addElement("(");
                floorArgument.addElement(arguments.get(0));
                floorArgument.addElement(" - ");
                final FunctionCalls floorInsideFloorFunctionCall = new FunctionCalls();
                final TableColumn floorInsideFloorTableColumn = new TableColumn();
                floorInsideFloorTableColumn.setColumnName("FLOOR");
                floorInsideFloorArgument.addElement(arguments.get(0));
                floorInsideFloorFunctionCall.setFunctionName(floorInsideFloorTableColumn);
                floorInsideFloorFunctionCall.setFunctionArguments(floorInsideFloorArgument);
                floorArgument.addElement(floorInsideFloorFunctionCall);
                floorArgument.addElement(")");
                floorArgument.addElement(" * ");
                final Vector powerArgument = new Vector();
                final FunctionCalls powerFunctionCall = new FunctionCalls();
                final TableColumn powerTableColumn = new TableColumn();
                powerTableColumn.setColumnName("POWER");
                powerArgument.addElement("10");
                powerArgument.addElement(arguments.get(1));
                powerFunctionCall.setFunctionName(powerTableColumn);
                powerFunctionCall.setFunctionArguments(powerArgument);
                floorArgument.addElement(powerFunctionCall);
                floorFunctionCall.setFunctionName(floorTableColumn);
                floorSelectColumn.setColumnExpression(floorArgument);
                final Vector floorSCArgument = new Vector();
                floorSCArgument.add(floorSelectColumn);
                floorFunctionCall.setFunctionArguments(floorSCArgument);
                final Vector castArgument = new Vector();
                castArgument.addElement(floorFunctionCall);
                castArgument.addElement("/");
                castArgument.addElement(powerFunctionCall);
                castArgument.addElement(" AS");
                castArgument.addElement(" FLOAT");
                castSelectColumn.setColumnExpression(castArgument);
                final Vector castSCArgument = new Vector();
                castSCArgument.add(castSelectColumn);
                castFunctionCall.setFunctionArguments(castSCArgument);
                truncArgument.addElement(truncFunctionCall);
                truncArgument.addElement("+");
                truncArgument.addElement(castFunctionCall);
                truncSelectColumn.setColumnExpression(truncArgument);
                final Vector truncSCArgument = new Vector();
                truncSCArgument.add(truncSelectColumn);
                this.setFunctionArguments(truncSCArgument);
            }
        }
        else if (arguments.size() == 1) {
            this.functionName.setColumnName("CONVERT");
            final Vector newArguments = new Vector();
            newArguments.add("DATETIME");
            final FunctionCalls newFunction = new FunctionCalls();
            final TableColumn tc3 = new TableColumn();
            tc3.setColumnName("CONVERT");
            newFunction.setFunctionName(tc3);
            final Vector functionArgs = new Vector();
            functionArgs.add("VARCHAR");
            functionArgs.add(arguments.get(0));
            functionArgs.add("112");
            newFunction.setFunctionArguments(functionArgs);
            newArguments.add(newFunction);
            this.setFunctionArguments(newArguments);
        }
        else if (arguments.size() == 2) {
            final String funcArgument0 = arguments.get(0).toString();
            String funcArgument2 = arguments.get(1).toString();
            if (funcArgument2.toLowerCase().startsWith("timestamp")) {
                funcArgument2 = StringFunctions.replaceFirst("", "timestamp", funcArgument2).trim();
            }
            if (arguments.get(1).toString().equalsIgnoreCase("'MONTH'")) {
                this.functionName.setColumnName("CONVERT(DATETIME, CONVERT(VARCHAR, " + arguments.get(0).toString() + ", 121)) - datepart(day," + arguments.get(0).toString() + ") + 1");
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("'DAY'")) {
                this.functionName.setColumnName("CONVERT(DATETIME, CONVERT(VARCHAR, " + arguments.get(0).toString() + ", 121)) - datepart(dw," + arguments.get(0).toString() + ") + 1");
            }
            if (funcArgument0.equalsIgnoreCase("'MONTH'")) {
                this.functionName.setColumnName("DATEADD(mm, DATEDIFF(mm,0," + funcArgument2 + "), 0)");
            }
            else if (funcArgument0.equalsIgnoreCase("'DAY'")) {
                this.functionName.setColumnName("DATEADD(dd, DATEDIFF(dd,0," + funcArgument2 + "), 0)");
            }
            else if (funcArgument0.equalsIgnoreCase("'YEAR'")) {
                this.functionName.setColumnName("DATEADD(yy, DATEDIFF(yy,0," + funcArgument2 + "), 0)");
            }
            if (funcArgument0.equalsIgnoreCase("'WEEK'")) {
                this.functionName.setColumnName("DATEADD(wk, DATEDIFF(wk,0," + funcArgument2 + "), 0)");
            }
            if (funcArgument0.equalsIgnoreCase("'HOUR'")) {
                this.functionName.setColumnName("DATEADD(hh, DATEDIFF(hh,0," + funcArgument2 + "), 0)");
            }
            if (funcArgument0.equalsIgnoreCase("'MINUTE'")) {
                this.functionName.setColumnName("DATEADD(mi, DATEDIFF(mi,0," + funcArgument2 + "), 0)");
            }
            if (funcArgument0.equalsIgnoreCase("'SECOND'")) {
                this.functionName.setColumnName("CONVERT(DATETIME, CONVERT(VARCHAR," + funcArgument2 + ", 120)) ");
            }
            final Vector newFnArgs = new Vector();
            this.setFunctionArguments(newFnArgs);
            this.setOpenBracesForFunctionNameRequired(false);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        boolean isArgDate = false;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final Vector colExpressions = this.functionArguments.elementAt(i_count).getColumnExpression();
                if (colExpressions != null) {
                    for (int i = 0; i < colExpressions.size(); ++i) {
                        if (colExpressions.get(i) instanceof TableColumn) {
                            final TableColumn tc = colExpressions.get(i);
                            final String columnName = tc.getColumnName();
                            if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                                isArgDate = true;
                            }
                            else if (columnName != null) {
                                final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                if (dataType != null && dataType.toLowerCase().indexOf("date") != -1) {
                                    isArgDate = true;
                                }
                                else if (columnName.startsWith(":")) {
                                    if (SwisSQLAPI.variableDatatypeMapping != null) {
                                        if (SwisSQLAPI.variableDatatypeMapping.containsKey(columnName.substring(1))) {
                                            final String dataType2 = SwisSQLAPI.variableDatatypeMapping.get(columnName.substring(1));
                                            if (dataType2.toLowerCase().indexOf("date") != -1) {
                                                isArgDate = true;
                                            }
                                        }
                                        else if (!SwisSQLOptions.convertTruncWithVariableToFloor) {
                                            isArgDate = true;
                                        }
                                    }
                                    else if (!SwisSQLOptions.convertTruncWithVariableToFloor) {
                                        isArgDate = true;
                                    }
                                }
                                else if (dataType == null && !SwisSQLOptions.convertTruncWithVariableToFloor) {
                                    isArgDate = true;
                                }
                                else if (dataType == null && SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName)) {
                                    final String dataType2 = SwisSQLAPI.variableDatatypeMapping.get(columnName);
                                    if (dataType2.toLowerCase().indexOf("date") != -1) {
                                        isArgDate = true;
                                    }
                                }
                            }
                        }
                        else if (colExpressions.get(i) instanceof FunctionCalls) {
                            final FunctionCalls fc = colExpressions.get(i);
                            final TableColumn tc2 = fc.getFunctionName();
                            if (tc2.getColumnName().toString().equalsIgnoreCase("TO_DATE")) {
                                isArgDate = true;
                            }
                            else {
                                final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc2);
                                if (dataType != null && dataType.toLowerCase().indexOf("date") != -1) {
                                    isArgDate = true;
                                }
                            }
                        }
                        else if (colExpressions.get(i) instanceof String && colExpressions.get(i).toString().equalsIgnoreCase("'MONTH'")) {
                            isArgDate = true;
                        }
                    }
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
            else if (this.functionArguments.get(i_count) instanceof String) {
                String s = this.functionArguments.get(i_count);
                if (s.trim().equalsIgnoreCase("SYSDATE")) {
                    isArgDate = true;
                    s = "GETDATE()";
                }
                if (s.trim().equalsIgnoreCase("SYS_GUID")) {
                    s = "NEWID()";
                }
                arguments.addElement(s);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (!isArgDate) {
            if (arguments.size() == 1) {
                this.functionName.setColumnName("FLOOR");
                final Vector newArguments = new Vector();
                newArguments.add(arguments.get(0));
                this.setFunctionArguments(arguments);
            }
            else if (arguments.size() == 2) {
                this.functionName.setColumnName("");
                final SelectColumn truncSelectColumn = new SelectColumn();
                final SelectColumn castSelectColumn = new SelectColumn();
                final SelectColumn floorSelectColumn = new SelectColumn();
                final FunctionCalls truncFunctionCall = new FunctionCalls();
                final TableColumn truncTableColumn = new TableColumn();
                truncTableColumn.setColumnName("FLOOR");
                final Vector firstFloorArg = new Vector();
                firstFloorArg.add(arguments.elementAt(0));
                truncFunctionCall.setFunctionName(truncTableColumn);
                truncFunctionCall.setFunctionArguments(firstFloorArg);
                final Vector truncArgument = new Vector();
                final FunctionCalls castFunctionCall = new FunctionCalls();
                final TableColumn castFunction = new TableColumn();
                castFunction.setColumnName("CONVERT");
                castFunctionCall.setFunctionName(castFunction);
                final Vector floorArgument = new Vector();
                final Vector floorInsideFloorArgument = new Vector();
                final FunctionCalls floorFunctionCall = new FunctionCalls();
                final TableColumn floorTableColumn = new TableColumn();
                floorTableColumn.setColumnName("FLOAT,FLOOR");
                floorArgument.addElement("(");
                floorArgument.addElement(arguments.get(0));
                floorArgument.addElement(" - ");
                final FunctionCalls floorInsideFloorFunctionCall = new FunctionCalls();
                final TableColumn floorInsideFloorTableColumn = new TableColumn();
                floorInsideFloorTableColumn.setColumnName("FLOOR");
                floorInsideFloorArgument.addElement(arguments.get(0));
                floorInsideFloorFunctionCall.setFunctionName(floorInsideFloorTableColumn);
                floorInsideFloorFunctionCall.setFunctionArguments(floorInsideFloorArgument);
                floorArgument.addElement(floorInsideFloorFunctionCall);
                floorArgument.addElement(")");
                floorArgument.addElement(" * ");
                final Vector powerArgument = new Vector();
                final FunctionCalls powerFunctionCall = new FunctionCalls();
                final TableColumn powerTableColumn = new TableColumn();
                powerTableColumn.setColumnName("POWER");
                powerArgument.addElement("10");
                powerArgument.addElement(arguments.get(1));
                powerFunctionCall.setFunctionName(powerTableColumn);
                powerFunctionCall.setFunctionArguments(powerArgument);
                floorArgument.addElement(powerFunctionCall);
                floorFunctionCall.setFunctionName(floorTableColumn);
                floorSelectColumn.setColumnExpression(floorArgument);
                final Vector floorSCArgument = new Vector();
                floorSCArgument.add(floorSelectColumn);
                floorFunctionCall.setFunctionArguments(floorSCArgument);
                final Vector castArgument = new Vector();
                castArgument.addElement(floorFunctionCall);
                castArgument.addElement("/");
                castArgument.addElement(powerFunctionCall);
                castSelectColumn.setColumnExpression(castArgument);
                final Vector castSCArgument = new Vector();
                castSCArgument.add(castSelectColumn);
                castFunctionCall.setFunctionArguments(castSCArgument);
                truncArgument.addElement(truncFunctionCall);
                truncArgument.addElement("+");
                truncArgument.addElement(castFunctionCall);
                truncSelectColumn.setColumnExpression(truncArgument);
                final Vector truncSCArgument = new Vector();
                truncSCArgument.add(truncSelectColumn);
                this.setFunctionArguments(truncSCArgument);
            }
        }
        else if (arguments.size() == 1) {
            this.functionName.setColumnName("CONVERT");
            final Vector newArguments = new Vector();
            newArguments.add("DATETIME");
            final FunctionCalls newFunction = new FunctionCalls();
            final TableColumn tc3 = new TableColumn();
            tc3.setColumnName("CONVERT");
            newFunction.setFunctionName(tc3);
            final Vector functionArgs = new Vector();
            functionArgs.add("VARCHAR");
            functionArgs.add(arguments.get(0));
            final String style = SwisSQLOptions.dateFormatForConvertFunction;
            if (style != null && style.length() > 0) {
                functionArgs.add(style);
            }
            else {
                functionArgs.add("112");
            }
            newFunction.setFunctionArguments(functionArgs);
            newArguments.add(newFunction);
            this.setFunctionArguments(newArguments);
        }
        else if (arguments.size() == 2 && arguments.get(1).toString().equalsIgnoreCase("'MONTH'")) {
            this.functionName.setColumnName("CONVERT(DATETIME, CONVERT(VARCHAR, " + arguments.get(0).toString() + ", 121)) - datepart(day," + arguments.get(0).toString() + ") + 1");
            final Vector newFnArgs = new Vector();
            this.setFunctionArguments(newFnArgs);
            this.setOpenBracesForFunctionNameRequired(false);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        boolean datetype = false;
        if (this.functionArguments.size() > 0 && this.functionArguments.elementAt(0) instanceof SelectColumn) {
            final Vector v = this.functionArguments.elementAt(0).getColumnExpression();
            if (v.size() == 1) {
                if (v.elementAt(0).toString().equalsIgnoreCase("sysdate")) {
                    datetype = true;
                    this.functionName.setColumnName("TIMESTAMP");
                    final Vector arguments = new Vector();
                    arguments.addElement("SUBSTR(CHAR(CURRENT TIMESTAMP), 1, 10) || '-00.00.00.000000'");
                    this.setFunctionArguments(arguments);
                    return;
                }
                if (v.elementAt(0) instanceof TableColumn) {
                    final String datatype = MetadataInfoUtil.getDatatypeName(from_sqs, v.elementAt(0));
                    if (datatype != null && (datatype.indexOf("date") != -1 || datatype.indexOf("timestamp") != -1)) {
                        datetype = true;
                        this.functionName.setColumnName("TIMESTAMP");
                        final Vector arguments2 = new Vector();
                        arguments2.addElement("SUBSTR(CHAR(" + v.elementAt(0) + "), 1, 10) || '-00.00.00.000000'");
                        this.setFunctionArguments(arguments2);
                        return;
                    }
                }
            }
        }
        if (!datetype && !this.functionName.getColumnName().trim().equalsIgnoreCase("INTEGER")) {
            this.functionName.setColumnName("TRUNC");
            final Vector arguments3 = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments3.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
                }
                else {
                    arguments3.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            this.setFunctionArguments(arguments3);
            if (SwisSQLOptions.UDBSQL) {
                this.functionName.setColumnName("TRUNC");
                return;
            }
            if (arguments3.size() == 1) {
                this.functionName.setColumnName("CAST");
                final Vector newArguments = new Vector();
                newArguments.add(arguments3.get(0));
                this.setAsDatatype("AS");
                newArguments.add("INTEGER");
                this.setFunctionArguments(newArguments);
            }
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        if (this.functionName.getColumnName().equalsIgnoreCase("date_trunc")) {
            return;
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
                    this.handleStringLiteralForTime(from_sqs, i_count, true, true);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("TRUNCATE")) {
            this.functionName.setColumnName("TRUNC(cast(" + arguments.get(0).toString() + " as numeric)," + arguments.get(1).toString() + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
            String qry = " extract(SECOND from  " + arguments.get(0) + " :: time)";
            if (canUseUDFFunction) {
                qry = "SECOND(" + arguments.get(0).toString() + ")";
            }
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                if (arguments.get(0) instanceof SelectColumn) {
                    qry = "extract(SECOND from " + arguments.get(0).toString() + ")";
                }
                else if (arguments.get(0).toString().contains("-") || arguments.get(0).toString().contains("/")) {
                    qry = "extract(SECOND from " + arguments.get(0).toString() + " :: timestamp)";
                }
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().trim().equalsIgnoreCase("SECOND")) {
            this.functionName.setColumnName("SECOND");
        }
        else if (this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
            this.functionName.setColumnName("DECIMAL");
        }
        else {
            this.functionName.setColumnName("TRUNCATE");
        }
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
        if (arguments.size() == 1) {
            if (!this.functionName.getColumnName().trim().equalsIgnoreCase("SECOND")) {
                this.functionName.setColumnName("FLOOR");
            }
            final Vector newArguments = new Vector();
            newArguments.add(arguments.get(0));
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TRUNCATE");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TRUNC");
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
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe function TRUNC is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnName = this.functionName.getColumnName();
        this.functionName.setColumnName("TRUNC");
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
        if (fnName.equalsIgnoreCase("trunc") && arguments.size() == 1) {
            final Object argument1 = arguments.elementAt(0);
            if (argument1 instanceof SelectColumn) {
                final Vector colExp = ((SelectColumn)argument1).getColumnExpression();
                if (colExp.get(0) instanceof TableColumn) {
                    final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, colExp.elementAt(0));
                    if (dataType != null && (dataType.toLowerCase().indexOf("date") != -1 || dataType.toLowerCase().indexOf("timestamp") != -1)) {
                        this.functionName.setColumnName("DATE_TRUNC");
                    }
                    else if (colExp.get(0).getColumnName() != null && colExp.get(0).getColumnName().equalsIgnoreCase("current_date")) {
                        this.functionName.setColumnName("DATE_TRUNC");
                    }
                }
            }
        }
        if (fnName.equalsIgnoreCase("trunc") && arguments.size() > 1) {
            boolean quoted = false;
            String argument2 = arguments.elementAt(1).toString();
            if (argument2.startsWith("\"") || argument2.startsWith("'")) {
                quoted = true;
                argument2 = argument2.substring(1, argument2.length() - 1);
                if (argument2.equalsIgnoreCase("mm") || argument2.equalsIgnoreCase("rm")) {
                    argument2 = "MON";
                }
                else if (argument2.equalsIgnoreCase("syyyy") || argument2.equalsIgnoreCase("yyyy") || argument2.equalsIgnoreCase("syear") || argument2.equalsIgnoreCase("yyy") || argument2.equalsIgnoreCase("yy") || argument2.equalsIgnoreCase("y")) {
                    argument2 = "YEAR";
                }
                else if (argument2.equalsIgnoreCase("ddd") || argument2.equalsIgnoreCase("dd") || argument2.equalsIgnoreCase("j")) {
                    argument2 = "DAY";
                }
                else if (argument2.equalsIgnoreCase("hh") || argument2.equalsIgnoreCase("hh12") || argument2.equalsIgnoreCase("hh24")) {
                    argument2 = "HOUR";
                }
            }
            for (int i = 0; i < SwisSQLUtils.getOracleDateFormats().length; ++i) {
                final String dateFmt = SwisSQLUtils.getOracleDateFormats()[i];
                if (argument2.equalsIgnoreCase(dateFmt)) {
                    final Object obj = arguments.get(0);
                    if (argument2.equalsIgnoreCase("mi")) {
                        argument2 = "MINUTE";
                    }
                    if (quoted) {
                        argument2 = "'" + argument2 + "'";
                    }
                    arguments.setElementAt(argument2, 0);
                    arguments.setElementAt(obj, 1);
                    this.functionName.setColumnName("DATE_TRUNC");
                    break;
                }
            }
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                try {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                    continue;
                }
                catch (final ConvertException ce) {
                    throw ce;
                }
            }
            arguments.addElement(this.functionArguments.elementAt(i_count));
        }
        if (!SwisSQLOptions.convertOracleTruncToCastAsDate) {
            this.functionName.setColumnName("TRUNC");
            this.setFunctionArguments(arguments);
            return;
        }
        if (arguments.size() == 1) {
            boolean castAsDate = true;
            if (arguments.elementAt(0) instanceof SelectColumn && arguments.elementAt(0).getColumnExpression().size() > 1) {
                boolean arithemeticOperatorFound = false;
                int selectColumnCount = 0;
                final Vector argColExp = arguments.elementAt(0).getColumnExpression();
                for (int k = 0; k < argColExp.size(); ++k) {
                    if (argColExp.elementAt(k) instanceof String) {
                        if (argColExp.elementAt(k).toString().equalsIgnoreCase("+") || argColExp.elementAt(k).toString().equalsIgnoreCase("-")) {
                            if (k > 0 && k < argColExp.size() - 1) {
                                final Object nextObj = argColExp.get(k + 1);
                                if (nextObj instanceof String) {
                                    boolean isNum = false;
                                    try {
                                        Integer.parseInt(nextObj.toString());
                                        isNum = true;
                                    }
                                    catch (final NumberFormatException ex) {}
                                    if (isNum) {
                                        argColExp.setElementAt("INTERVAL '" + nextObj + "' day", k + 1);
                                    }
                                }
                            }
                        }
                        else if (argColExp.elementAt(k).toString().equalsIgnoreCase("/")) {
                            arithemeticOperatorFound = true;
                        }
                    }
                    else if (argColExp.elementAt(k) instanceof SelectColumn && argColExp.elementAt(k).getColumnExpression() != null && argColExp.elementAt(k).getColumnExpression().size() > 1) {
                        castAsDate = false;
                    }
                    else {
                        ++selectColumnCount;
                    }
                }
                if (selectColumnCount > 1 && arithemeticOperatorFound) {
                    castAsDate = false;
                }
            }
            if (castAsDate) {
                this.functionName.setColumnName("CAST");
                final DateClass dc = new DateClass();
                dc.setDatatypeName("DATE");
                this.setAsDatatype("AS");
                arguments.add(dc);
                this.setFunctionArguments(arguments);
            }
            else {
                this.functionName.setColumnName("TRUNC");
                this.setFunctionArguments(arguments);
            }
        }
        else if (arguments.size() == 2) {
            String arg2 = arguments.get(1).toString();
            if (arg2.startsWith("'")) {
                arg2 = arg2.substring(1, arg2.length() - 1);
            }
            if (arg2.equalsIgnoreCase("ddd") || arg2.equalsIgnoreCase("dd") || arg2.equalsIgnoreCase("j")) {
                if (arguments.elementAt(0) instanceof SelectColumn && arguments.elementAt(0).getColumnExpression().size() > 1) {
                    final Vector argColExp2 = arguments.elementAt(0).getColumnExpression();
                    for (int i = 0; i < argColExp2.size(); ++i) {
                        if (argColExp2.elementAt(i) instanceof String && (argColExp2.elementAt(i).toString().equalsIgnoreCase("+") || argColExp2.elementAt(i).toString().equalsIgnoreCase("-")) && i > 0 && i < argColExp2.size() - 1) {
                            final Object nextObj2 = argColExp2.get(i + 1);
                            if (nextObj2 instanceof String) {
                                boolean isNum2 = false;
                                try {
                                    Integer.parseInt(nextObj2.toString());
                                    isNum2 = true;
                                }
                                catch (final NumberFormatException ex2) {}
                                if (isNum2) {
                                    argColExp2.setElementAt("INTERVAL '" + nextObj2 + "' day", i + 1);
                                }
                            }
                        }
                    }
                }
                this.functionName.setColumnName("CAST");
                final DateClass dc = new DateClass();
                dc.setDatatypeName("DATE");
                this.setAsDatatype("AS");
                arguments.setElementAt(dc, 1);
                this.setFunctionArguments(arguments);
            }
            else {
                this.functionName.setColumnName("TRUNC");
                this.setFunctionArguments(arguments);
            }
        }
        else {
            this.functionName.setColumnName("TRUNC");
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
            this.functionName.setColumnName("SECOND");
        }
        else if (this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
            this.functionName.setColumnName("DECIMAL");
        }
        else {
            this.functionName.setColumnName("TRUNCATE");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
                    this.handleStringLiteralForTime(from_sqs, i_count, true, true);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionName.getColumnName().trim().equalsIgnoreCase("TRUNCATE")) {
            String qry = "";
            String precision = "5";
            if (arguments.elementAt(1) instanceof SelectColumn) {
                final SelectColumn sc_precision = arguments.elementAt(1);
                final Vector vc_precision = sc_precision.getColumnExpression();
                if (vc_precision.size() == 1 && vc_precision.elementAt(0) instanceof String) {
                    precision = vc_precision.elementAt(0);
                    precision = precision.replaceAll("'", "");
                    try {
                        Double.parseDouble(precision);
                    }
                    catch (final Exception e) {
                        precision = "5";
                    }
                }
            }
            qry = "truncate(cast(" + arguments.get(0).toString() + " as decimal(32," + precision + "))," + arguments.get(1).toString() + ")";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
}
