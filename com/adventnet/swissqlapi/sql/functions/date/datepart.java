package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class datepart extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        if (arguments.size() == 2 && arguments.get(0) instanceof SelectColumn && arguments.get(0).getColumnExpression() != null && arguments.get(0).getColumnExpression().size() > 0 && arguments.get(0).getColumnExpression().get(0) instanceof TableColumn) {
            final TableColumn tc = arguments.get(0).getColumnExpression().get(0);
            if (tc.getTableName() == null && tc.getColumnName() != null) {
                final Vector subFunctionArgs = new Vector();
                boolean ms = false;
                if (tc.getColumnName().equalsIgnoreCase("MONTH") || tc.getColumnName().equalsIgnoreCase("MM") || tc.getColumnName().equalsIgnoreCase("M")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'MM'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("YEAR") || tc.getColumnName().equalsIgnoreCase("YYYY") || tc.getColumnName().equalsIgnoreCase("YY")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'YYYY'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("QUARTER") || tc.getColumnName().equalsIgnoreCase("QQ") || tc.getColumnName().equalsIgnoreCase("Q")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'Q'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("DAY") || tc.getColumnName().equalsIgnoreCase("DD") || tc.getColumnName().equalsIgnoreCase("D")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'DD'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("DAYOFYEAR") || tc.getColumnName().equalsIgnoreCase("DY") || tc.getColumnName().equalsIgnoreCase("Y")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'DDD'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("WEEK") || tc.getColumnName().equalsIgnoreCase("WK") || tc.getColumnName().equalsIgnoreCase("WW")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'WW'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("DW") || tc.getColumnName().equalsIgnoreCase("WEEKDAY")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'D'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("HOUR") || tc.getColumnName().equalsIgnoreCase("HH")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'HH24'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("MI") || tc.getColumnName().equalsIgnoreCase("N") || tc.getColumnName().equalsIgnoreCase("MINUTE")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'MI'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("SS") || tc.getColumnName().equalsIgnoreCase("S") || tc.getColumnName().equalsIgnoreCase("SECOND") || tc.getColumnName().equalsIgnoreCase("SECONDS")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    subFunctionArgs.add("'SS'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                }
                else if (tc.getColumnName().equalsIgnoreCase("MS") || tc.getColumnName().equalsIgnoreCase("MILLISECOND")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final FunctionCalls subFunction = new FunctionCalls();
                    final TableColumn subTC = new TableColumn();
                    subTC.setColumnName("TO_CHAR");
                    subFunction.setFunctionName(subTC);
                    final Vector scColumnExpression = new Vector();
                    final SelectColumn sc = new SelectColumn();
                    ms = true;
                    subFunctionArgs.add("'FF'");
                    subFunction.setFunctionArguments(subFunctionArgs);
                    scColumnExpression.add(subFunction);
                    sc.setColumnExpression(scColumnExpression);
                    final Vector newArgument = new Vector();
                    newArgument.add(sc);
                    this.setFunctionArguments(newArgument);
                    if (arguments.get(1).toString().trim().equalsIgnoreCase("(SYSDATE)")) {
                        arguments.set(1, "SYSTIMESTAMP");
                    }
                }
                if (arguments.get(1).toString().trim().startsWith("'")) {
                    final Object obj = arguments.get(1);
                    final String format = SwisSQLUtils.getDateFormat(obj.toString().trim(), 1);
                    final SelectColumn newSC = new SelectColumn();
                    final Vector colExpr = new Vector();
                    final FunctionCalls fc = new FunctionCalls();
                    final TableColumn fctc = new TableColumn();
                    if (ms) {
                        fctc.setColumnName("TO_TIMESTAMP");
                    }
                    else {
                        fctc.setColumnName("TO_DATE");
                    }
                    final Vector fnArgs = new Vector();
                    fnArgs.add(obj);
                    fc.setFunctionArguments(fnArgs);
                    fc.setFunctionName(fctc);
                    colExpr.add(fc);
                    newSC.setColumnExpression(colExpr);
                    subFunctionArgs.add(0, newSC);
                    if (format != null) {
                        if (format.startsWith("'1900")) {
                            fnArgs.setElementAt(format, 0);
                            fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                        }
                        else {
                            fnArgs.add(format);
                        }
                    }
                }
                else {
                    subFunctionArgs.add(0, arguments.get(1));
                }
            }
        }
        final String fname = this.functionName.getColumnName();
        if (fname.equalsIgnoreCase("monthname")) {
            this.functionName.setColumnName("TO_CHAR");
            final Vector colExpr2 = new Vector();
            final SelectColumn sc2 = new SelectColumn();
            colExpr2.add(arguments.get(0));
            sc2.setColumnExpression(colExpr2);
            final SelectColumn sc3 = new SelectColumn();
            final Vector colExpr3 = new Vector();
            colExpr3.add("'FMMonth'");
            sc3.setColumnExpression(colExpr3);
            final Vector functionArguments = new Vector();
            functionArguments.add(sc2);
            functionArguments.add(sc3);
            this.setFunctionArguments(functionArguments);
        }
        else if (fname.equalsIgnoreCase("julian_day")) {
            this.functionName.setColumnName("TO_NUMBER");
            final FunctionCalls subFunction2 = new FunctionCalls();
            final TableColumn subTC2 = new TableColumn();
            subTC2.setColumnName("TO_CHAR");
            subFunction2.setFunctionName(subTC2);
            final Vector subFunctionArgs2 = new Vector();
            final Vector scColumnExpression2 = new Vector();
            final SelectColumn sc4 = new SelectColumn();
            subFunctionArgs2.add(arguments.get(0));
            subFunctionArgs2.add("'J'");
            subFunction2.setFunctionArguments(subFunctionArgs2);
            scColumnExpression2.add(subFunction2);
            sc4.setColumnExpression(scColumnExpression2);
            final Vector newArgument2 = new Vector();
            newArgument2.add(sc4);
            this.setFunctionArguments(newArgument2);
        }
        else if (fname.equalsIgnoreCase("week_iso")) {
            this.functionName.setColumnName("TO_NUMBER");
            final FunctionCalls subFunction2 = new FunctionCalls();
            final TableColumn subTC2 = new TableColumn();
            subTC2.setColumnName("TO_CHAR");
            subFunction2.setFunctionName(subTC2);
            final Vector subFunctionArgs2 = new Vector();
            final Vector scColumnExpression2 = new Vector();
            final SelectColumn sc4 = new SelectColumn();
            subFunctionArgs2.add(arguments.get(0));
            subFunctionArgs2.add("'IW'");
            subFunction2.setFunctionArguments(subFunctionArgs2);
            scColumnExpression2.add(subFunction2);
            sc4.setColumnExpression(scColumnExpression2);
            final Vector newArgument2 = new Vector();
            newArgument2.add(sc4);
            this.setFunctionArguments(newArgument2);
        }
        else if (fname.equalsIgnoreCase("dayofweek")) {
            this.functionName.setColumnName("TO_NUMBER");
            final FunctionCalls subFunction2 = new FunctionCalls();
            final TableColumn subTC2 = new TableColumn();
            subTC2.setColumnName("TO_CHAR");
            subFunction2.setFunctionName(subTC2);
            final Vector subFunctionArgs2 = new Vector();
            final Vector scColumnExpression2 = new Vector();
            final SelectColumn sc4 = new SelectColumn();
            subFunctionArgs2.add(arguments.get(0));
            subFunctionArgs2.add("'D'");
            subFunction2.setFunctionArguments(subFunctionArgs2);
            scColumnExpression2.add(subFunction2);
            sc4.setColumnExpression(scColumnExpression2);
            final Vector newArgument2 = new Vector();
            newArgument2.add(sc4);
            this.setFunctionArguments(newArgument2);
        }
        else if (fname.equalsIgnoreCase("days")) {
            this.functionName.setColumnName("");
            this.setOpenBracesForFunctionNameRequired(true);
            final Vector scColumnExpression3 = new Vector();
            final FunctionCalls subFunction3 = new FunctionCalls();
            final TableColumn subTC3 = new TableColumn();
            subTC3.setColumnName("TO_DATE");
            subFunction3.setFunctionName(subTC3);
            final Vector subFunctionArgs3 = new Vector();
            subFunctionArgs3.add(arguments.get(0));
            subFunction3.setFunctionArguments(subFunctionArgs3);
            scColumnExpression3.add(subFunction3);
            scColumnExpression3.add("-");
            final FunctionCalls subFunction4 = new FunctionCalls();
            final TableColumn subTC4 = new TableColumn();
            subTC4.setColumnName("TO_DATE");
            subFunction4.setFunctionName(subTC4);
            final Vector subFunctionArgs4 = new Vector();
            subFunctionArgs4.add("'01010001'");
            subFunctionArgs4.add("'MMDDYYYY'");
            subFunction4.setFunctionArguments(subFunctionArgs4);
            scColumnExpression3.add(subFunction4);
            scColumnExpression3.add("-");
            scColumnExpression3.add("1");
            final SelectColumn sc5 = new SelectColumn();
            sc5.setColumnExpression(scColumnExpression3);
            final Vector newArgument3 = new Vector();
            newArgument3.add(sc5);
            this.setFunctionArguments(newArgument3);
        }
        else if (fname.equalsIgnoreCase("DAYOFYEAR")) {
            this.functionName.setColumnName("TO_NUMBER");
            final FunctionCalls subFunction2 = new FunctionCalls();
            final TableColumn subTC2 = new TableColumn();
            subTC2.setColumnName("TO_CHAR");
            subFunction2.setFunctionName(subTC2);
            final Vector scColumnExpression4 = new Vector();
            final SelectColumn sc6 = new SelectColumn();
            final Vector subFunctionArgs5 = new Vector();
            String dateStringColumn = arguments.get(0).toString();
            dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
            subFunctionArgs5.add(dateStringColumn);
            subFunctionArgs5.add("'DDD'");
            subFunction2.setFunctionArguments(subFunctionArgs5);
            scColumnExpression4.add(subFunction2);
            sc6.setColumnExpression(scColumnExpression4);
            final Vector newArgument = new Vector();
            newArgument.add(sc6);
            this.setFunctionArguments(newArgument);
        }
        else if (fname.equalsIgnoreCase("from_unixtime")) {
            final String dateString = arguments.get(0).toString();
            final String query = "cast((to_date('1970-01-01 ','YYYY-MM-DD ') + numtodsinterval(" + dateString + ",'SECOND')) as TIMESTAMP)";
            this.functionName.setColumnName(query);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (fname.equalsIgnoreCase("HOUR")) {
            this.functionName.setColumnName("TO_NUMBER");
            final FunctionCalls subFunction2 = new FunctionCalls();
            final TableColumn subTC2 = new TableColumn();
            subTC2.setColumnName("TO_CHAR");
            subFunction2.setFunctionName(subTC2);
            final Vector scColumnExpression4 = new Vector();
            final SelectColumn sc6 = new SelectColumn();
            final Vector subFunctionArgs5 = new Vector();
            String dateStringColumn = arguments.get(0).toString();
            dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
            subFunctionArgs5.add(dateStringColumn);
            subFunctionArgs5.add("'HH24'");
            subFunction2.setFunctionArguments(subFunctionArgs5);
            scColumnExpression4.add(subFunction2);
            sc6.setColumnExpression(scColumnExpression4);
            final Vector newArgument = new Vector();
            newArgument.add(sc6);
            this.setFunctionArguments(newArgument);
        }
        else if (fname.equalsIgnoreCase("minute")) {
            this.functionName.setColumnName("TO_NUMBER");
            final FunctionCalls subFunction2 = new FunctionCalls();
            final TableColumn subTC2 = new TableColumn();
            subTC2.setColumnName("TO_CHAR");
            subFunction2.setFunctionName(subTC2);
            final Vector scColumnExpression4 = new Vector();
            final SelectColumn sc6 = new SelectColumn();
            final Vector subFunctionArgs5 = new Vector();
            String dateStringColumn = arguments.get(0).toString();
            dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
            subFunctionArgs5.add(dateStringColumn);
            subFunctionArgs5.add("'MI'");
            subFunction2.setFunctionArguments(subFunctionArgs5);
            scColumnExpression4.add(subFunction2);
            sc6.setColumnExpression(scColumnExpression4);
            final Vector newArgument = new Vector();
            newArgument.add(sc6);
            this.setFunctionArguments(newArgument);
        }
        else if (fname.equalsIgnoreCase("dayname")) {
            this.functionName.setColumnName("TO_CHAR");
            final Vector colExpr2 = new Vector();
            final SelectColumn sc2 = new SelectColumn();
            String dateStringColumn2 = arguments.get(0).toString();
            dateStringColumn2 = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn2);
            colExpr2.add(dateStringColumn2);
            sc2.setColumnExpression(colExpr2);
            final SelectColumn sc7 = new SelectColumn();
            final Vector colExpr4 = new Vector();
            colExpr4.add("'FMDay'");
            sc7.setColumnExpression(colExpr4);
            final Vector functionArguments2 = new Vector();
            functionArguments2.add(sc2);
            functionArguments2.add(sc7);
            this.setFunctionArguments(functionArguments2);
        }
        else if (fname.equalsIgnoreCase("week")) {
            this.functionName.setColumnName("TO_NUMBER");
            final FunctionCalls subFunction2 = new FunctionCalls();
            final TableColumn subTC2 = new TableColumn();
            subTC2.setColumnName("TO_CHAR");
            subFunction2.setFunctionName(subTC2);
            final Vector scColumnExpression4 = new Vector();
            final SelectColumn sc6 = new SelectColumn();
            final Vector subFunctionArgs5 = new Vector();
            String dateStringColumn = arguments.get(0).toString();
            dateStringColumn = StringFunctions.handleLiteralStringDateForOracle(dateStringColumn);
            subFunctionArgs5.add(dateStringColumn);
            subFunctionArgs5.add("'WW'");
            subFunction2.setFunctionArguments(subFunctionArgs5);
            scColumnExpression4.add(subFunction2);
            sc6.setColumnExpression(scColumnExpression4);
            final Vector newArgument = new Vector();
            newArgument.add(sc6);
            this.setFunctionArguments(newArgument);
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("hour")) {
            this.functionArguments.add(0, "hh");
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("monthname")) {
            this.functionName.setColumnName("DATENAME");
            arguments.addElement("MONTH");
        }
        else {
            if (from_sqs.isMSAzure()) {
                if (this.functionName.getColumnName().equalsIgnoreCase("dayofweek")) {
                    arguments.addElement("dw");
                }
                else if (this.functionName.getColumnName().equalsIgnoreCase("dayofyear")) {
                    arguments.addElement("dy");
                }
            }
            this.functionName.setColumnName("DATEPART");
        }
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String arg1 = this.functionArguments.get(0).toString();
        if (arg1.equalsIgnoreCase("CALDAYOFWEEK") || arg1.equalsIgnoreCase("CDW")) {
            arguments.setElementAt("DW", 0);
        }
        else if (arg1.equalsIgnoreCase("CALWEEKOFYEAR") || arg1.equalsIgnoreCase("CWK")) {
            arguments.setElementAt("WK", 0);
        }
        else if (arg1.equalsIgnoreCase("CALYEAROFWEEK") || arg1.equalsIgnoreCase("CYR")) {
            arguments.setElementAt("YY", 0);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATEPART");
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
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATEPART");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 2) {
            final String arg1 = arguments.get(0).toString();
            arguments.remove(0);
            if (arg1.equalsIgnoreCase("yy") || arg1.equalsIgnoreCase("year")) {
                this.functionName.setColumnName("YEAR");
            }
            else if (arg1.equalsIgnoreCase("quarter") || arg1.equalsIgnoreCase("qq")) {
                this.functionName.setColumnName("QUARTER");
            }
            else if (arg1.equalsIgnoreCase("dd") || arg1.equalsIgnoreCase("day")) {
                this.functionName.setColumnName("DAY");
            }
            else if (arg1.equalsIgnoreCase("month") || arg1.equalsIgnoreCase("mm")) {
                this.functionName.setColumnName("MONTH");
            }
            else if (arg1.equalsIgnoreCase("week") || arg1.equalsIgnoreCase("wk")) {
                this.functionName.setColumnName("WEEK");
            }
            else if (arg1.equalsIgnoreCase("dayofyear") || arg1.equalsIgnoreCase("dy")) {
                this.functionName.setColumnName("DAYOFYEAR");
            }
            else if (arg1.equalsIgnoreCase("weekday") || arg1.equalsIgnoreCase("dw")) {
                this.functionName.setColumnName("DAYOFWEEK");
            }
            else if (arg1.equalsIgnoreCase("hour") || arg1.equalsIgnoreCase("hh")) {
                this.functionName.setColumnName("HOUR");
            }
            else if (arg1.equalsIgnoreCase("minute") || arg1.equalsIgnoreCase("mi")) {
                this.functionName.setColumnName("MINUTE");
            }
            else if (arg1.equalsIgnoreCase("second") || arg1.equalsIgnoreCase("ss")) {
                this.functionName.setColumnName("SECOND");
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                if (this.functionArguments.elementAt(i_count).getColumnExpression() != null && this.functionArguments.elementAt(i_count).getColumnExpression().size() == 1 && this.functionArguments.elementAt(i_count).getColumnExpression().get(0) instanceof String) {
                    if (this.functionName.getColumnName().equalsIgnoreCase("HOUR")) {
                        arguments.add(i_count, " CAST( " + this.handleStringLiteralForTime(arguments.get(i_count).toString(), from_sqs, true) + " AS TIME)");
                    }
                    else {
                        arguments.add(i_count, " CAST( " + this.handleStringLiteralForDateTime(arguments.get(i_count).toString(), from_sqs) + " AS TIMESTAMP)");
                    }
                }
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "";
        if (this.functionName.getColumnName().equalsIgnoreCase("HOUR")) {
            qry = "  cast(EXTRACT (HOUR FROM  (" + arguments.get(0) + ")) as int)";
            if (canUseUDFFunction) {
                qry = "HOUR(" + arguments.get(0).toString() + ")";
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("DAYOFWEEK")) {
            qry = " cast( EXTRACT (DOW FROM  (" + arguments.get(0) + ")) + 1 as int) ";
            if (canUseUDFFunction) {
                qry = "DAYOFWEEK(" + arguments.get(0).toString() + ")";
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("MONTHNAME")) {
            qry = " to_char(DATE(" + arguments.get(0) + "), 'FMMonth') ";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        boolean changeArgumentsForDatePart = false;
        if (this.functionName.getColumnName().trim().equalsIgnoreCase("hour")) {
            this.functionName.setColumnName("hour");
        }
        else if (this.functionName.getColumnName().trim().equalsIgnoreCase("monthname")) {
            this.functionName.setColumnName("MONTHNAME");
        }
        else if (this.functionName.getColumnName().trim().equalsIgnoreCase("dayofweek")) {
            this.functionName.setColumnName("DAYOFWEEK");
        }
        else {
            if (this.functionName.getColumnName().trim().equalsIgnoreCase("DATEPART")) {
                changeArgumentsForDatePart = true;
            }
            this.functionName.setColumnName("DATEPART");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (changeArgumentsForDatePart) {
                    final SelectColumn sc = this.functionArguments.elementAt(i_count);
                    final Vector colExp = sc.getColumnExpression();
                    if (colExp.get(0) instanceof TableColumn && this.functionArguments.size() == 2) {
                        final String datePart = colExp.get(0).getColumnName().trim();
                        if (datePart.equalsIgnoreCase("weekday") || datePart.equalsIgnoreCase("dw")) {
                            this.functionName.setColumnName("DAYOFWEEK");
                        }
                        else if (datePart.equalsIgnoreCase("month")) {
                            this.functionName.setColumnName("MONTH");
                        }
                        else if (datePart.equalsIgnoreCase("day")) {
                            this.functionName.setColumnName("DAY");
                        }
                        else if (datePart.equalsIgnoreCase("year")) {
                            this.functionName.setColumnName("YEAR");
                        }
                        else if (datePart.equalsIgnoreCase("dayofyear")) {
                            this.functionName.setColumnName("DAYOFYEAR");
                        }
                        else if (datePart.equalsIgnoreCase("hour")) {
                            this.functionName.setColumnName("HOUR");
                        }
                        else if (datePart.equalsIgnoreCase("minute")) {
                            this.functionName.setColumnName("MINUTE");
                        }
                        changeArgumentsForDatePart = false;
                    }
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATEPART");
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
        this.functionName.setColumnName("DATEPART");
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
        this.functionName.setColumnName("TO_CHAR");
        final Vector arguments = new Vector();
        if (this.functionArguments.size() == 2) {
            if (this.functionArguments.get(1) instanceof SelectColumn) {
                arguments.add(this.functionArguments.get(1).toTimesTenSelect(to_sqs, from_sqs));
            }
            else {
                arguments.add(this.functionArguments.get(1));
            }
            final SelectColumn sc = this.functionArguments.get(0);
            final String datePart = sc.getColumnExpression().get(0).toString();
            if (datePart.equalsIgnoreCase("year") || datePart.equalsIgnoreCase("yy")) {
                arguments.add("'YYYY'");
            }
            else if (datePart.equalsIgnoreCase("quarter") || datePart.equalsIgnoreCase("qq")) {
                arguments.add("'Q'");
            }
            else if (datePart.equalsIgnoreCase("month") || datePart.equalsIgnoreCase("mm")) {
                arguments.add("'MM'");
            }
            else if (datePart.equalsIgnoreCase("day") || datePart.equalsIgnoreCase("dd")) {
                arguments.add("'DD'");
            }
            else if (datePart.equalsIgnoreCase("hour") || datePart.equalsIgnoreCase("hh")) {
                arguments.add("'HH24'");
            }
            else if (datePart.equalsIgnoreCase("minute") || datePart.equalsIgnoreCase("mi")) {
                arguments.add("'MI'");
            }
            else if (datePart.equalsIgnoreCase("second") || datePart.equalsIgnoreCase("ss")) {
                arguments.add("'SS'");
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATEPART");
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
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATEPART");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String funName = this.functionName.getColumnName().trim();
        if (funName.equalsIgnoreCase("hour")) {
            this.functionName.setColumnName("HOUR");
        }
        else if (!funName.equalsIgnoreCase("monthname")) {
            if (funName.equalsIgnoreCase("QUATER")) {
                this.functionName.setColumnName("QUATER");
            }
            else if (funName.equalsIgnoreCase("dayofweek")) {
                this.functionName.setColumnName("DAYOFWEEK");
            }
            else if (funName.equalsIgnoreCase("dow")) {
                this.functionName.setColumnName("DAYOFWEEK");
            }
            else if (funName.equalsIgnoreCase("second")) {
                this.functionName.setColumnName("SECOND");
            }
            else if (funName.equalsIgnoreCase("MINUTE")) {
                this.functionName.setColumnName("MINUTE");
            }
            else if (funName.equalsIgnoreCase("microsecond")) {
                this.functionName.setColumnName("MICROSECOND");
            }
            else if (funName.equalsIgnoreCase("dayofyear")) {
                this.functionName.setColumnName("dayofyear");
            }
            else if (funName.equalsIgnoreCase("day")) {
                this.functionName.setColumnName("DAY");
            }
            else if (funName.equalsIgnoreCase("month")) {
                this.functionName.setColumnName("MONTH");
            }
            else {
                if (!funName.equalsIgnoreCase("year")) {
                    throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
                }
                this.functionName.setColumnName("YEAR");
            }
        }
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    if (funName.equalsIgnoreCase("HOUR") || funName.equalsIgnoreCase("MINUTE") || funName.equalsIgnoreCase("SECOND") || funName.equalsIgnoreCase("MICROSECOND")) {
                        this.handleStringLiteralForTime(from_sqs, i_count, true, true);
                    }
                    else {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                    }
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (funName.trim().equalsIgnoreCase("monthname")) {
            arguments.addElement("'%M'");
            this.functionName.setColumnName("DATE_FORMAT");
        }
        this.setFunctionArguments(arguments);
    }
}
