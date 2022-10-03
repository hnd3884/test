package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class datename extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TO_CHAR");
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
        if (arguments.size() == 2) {
            final Vector newArguments = new Vector();
            newArguments.add(arguments.get(1));
            if (arguments.get(0) instanceof SelectColumn) {
                if (arguments.get(0).getColumnExpression() != null && arguments.get(0).getColumnExpression().size() > 0) {
                    if (arguments.get(0).getColumnExpression().get(0) instanceof TableColumn) {
                        final TableColumn tc = arguments.get(0).getColumnExpression().get(0);
                        if (tc.getTableName() == null && tc.getColumnName() != null) {
                            boolean ms = false;
                            if (tc.getColumnName().equalsIgnoreCase("MONTH") || tc.getColumnName().equalsIgnoreCase("MM") || tc.getColumnName().equalsIgnoreCase("M")) {
                                newArguments.add("'MONTH'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("YY") || tc.getColumnName().equalsIgnoreCase("YYYY") || tc.getColumnName().equalsIgnoreCase("YEAR")) {
                                newArguments.add("'YYYY'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("DAYOFYEAR") || tc.getColumnName().equalsIgnoreCase("DY") || tc.getColumnName().equalsIgnoreCase("Y")) {
                                newArguments.add("'DDD'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("DAY") || tc.getColumnName().equalsIgnoreCase("DD") || tc.getColumnName().equalsIgnoreCase("D")) {
                                newArguments.add("'DD'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("WEEK") || tc.getColumnName().equalsIgnoreCase("WK") || tc.getColumnName().equalsIgnoreCase("W") || tc.getColumnName().equalsIgnoreCase("WW")) {
                                newArguments.add("'WW'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("QUARTER") || tc.getColumnName().equalsIgnoreCase("QQ") || tc.getColumnName().equalsIgnoreCase("Q")) {
                                newArguments.add("'Q'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("WEEKDAY") || tc.getColumnName().equalsIgnoreCase("DW")) {
                                newArguments.add("'DAY'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("HOUR") || tc.getColumnName().equalsIgnoreCase("HH")) {
                                newArguments.add("'HH24'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("MINUTE") || tc.getColumnName().equalsIgnoreCase("MI") || tc.getColumnName().equalsIgnoreCase("N")) {
                                newArguments.add("'MI'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("SECOND") || tc.getColumnName().equalsIgnoreCase("SS") || tc.getColumnName().equalsIgnoreCase("S")) {
                                newArguments.add("'SS'");
                            }
                            else if (tc.getColumnName().equalsIgnoreCase("MILLISECOND") || tc.getColumnName().equalsIgnoreCase("MS")) {
                                newArguments.add("'FF'");
                                ms = true;
                            }
                            else {
                                newArguments.add("' || " + arguments.get(0) + " || '");
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
                                newArguments.setElementAt(newSC, 0);
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
                        }
                    }
                }
                else {
                    newArguments.add("' || " + arguments.get(0) + " || '");
                }
            }
            else {
                newArguments.add("' || " + arguments.get(0) + " || '");
            }
            this.setFunctionArguments(newArguments);
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATENAME");
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
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATENAME");
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
        this.functionName.setColumnName("DATENAME");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (arguments.size() == 2) {
            final String arg1 = arguments.get(0).toString();
            arguments.remove(0);
            final FunctionCalls innerFunction = new FunctionCalls();
            final TableColumn innerFn = new TableColumn();
            if (arg1.equalsIgnoreCase("yy") || arg1.equalsIgnoreCase("year")) {
                innerFn.setColumnName("YEAR");
            }
            else if (arg1.equalsIgnoreCase("quarter") || arg1.equalsIgnoreCase("qq")) {
                innerFn.setColumnName("QUARTER");
            }
            else if (arg1.equalsIgnoreCase("dd") || arg1.equalsIgnoreCase("day")) {
                innerFn.setColumnName("DAY");
            }
            else if (arg1.equalsIgnoreCase("month") || arg1.equalsIgnoreCase("mm")) {
                this.functionName.setColumnName("MONTHNAME");
            }
            else if (arg1.equalsIgnoreCase("week") || arg1.equalsIgnoreCase("wk")) {
                innerFn.setColumnName("WEEK");
            }
            else if (arg1.equalsIgnoreCase("dayofyear") || arg1.equalsIgnoreCase("dy")) {
                innerFn.setColumnName("DAYOFYEAR");
            }
            else if (arg1.equalsIgnoreCase("weekday") || arg1.equalsIgnoreCase("dw")) {
                this.functionName.setColumnName("DAYNAME");
            }
            else if (arg1.equalsIgnoreCase("hour") || arg1.equalsIgnoreCase("hh")) {
                innerFn.setColumnName("HOUR");
            }
            else if (arg1.equalsIgnoreCase("minute") || arg1.equalsIgnoreCase("mi")) {
                innerFn.setColumnName("MINUTE");
            }
            else if (arg1.equalsIgnoreCase("second") || arg1.equalsIgnoreCase("ss")) {
                innerFn.setColumnName("SECOND");
            }
            else {
                innerFn.setColumnName("");
            }
            if (!arg1.equalsIgnoreCase("month") && !arg1.equalsIgnoreCase("mm") && !arg1.equalsIgnoreCase("weekday") && !arg1.equalsIgnoreCase("dw")) {
                innerFunction.setFunctionArguments(arguments);
                innerFunction.setFunctionName(innerFn);
                final SelectColumn sc = new SelectColumn();
                final Vector colExpr = new Vector();
                colExpr.add(innerFunction);
                sc.setColumnExpression(colExpr);
                final Vector outerArg = new Vector();
                outerArg.add(sc);
                this.setFunctionArguments(outerArg);
                this.functionName.setColumnName("CHAR");
            }
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATENAME");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATENAME");
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
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATENAME");
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
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATENAME");
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
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATENAME");
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
                arguments.add("'MONTH'");
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
        this.functionName.setColumnName("DATENAME");
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
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE_FORMAT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        arguments.addElement("'%W'");
        this.setFunctionArguments(arguments);
    }
}
