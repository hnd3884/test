package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Collection;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class addToDate extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String qry = "";
        String addStr = "";
        final String funName = this.functionName.getColumnName();
        if (funName.equalsIgnoreCase("ADDYEAR")) {
            addStr = "YEAR";
        }
        else if (funName.equalsIgnoreCase("ADDMONTH")) {
            addStr = "MONTH";
        }
        else if (funName.equalsIgnoreCase("ADDWEEK")) {
            addStr = "WEEK";
        }
        else if (funName.equalsIgnoreCase("ADDQUARTER")) {
            addStr = "QUARTER";
        }
        else if (funName.equalsIgnoreCase("ADDHOUR")) {
            addStr = "HOUR";
        }
        else if (funName.equalsIgnoreCase("ADDMINUTE")) {
            addStr = "MINUTE";
        }
        else if (funName.equalsIgnoreCase("ADDSECOND")) {
            addStr = "SECOND";
        }
        this.functionName.setColumnName("DATE_ADD");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionArguments.size() == 2) {
            final SelectColumn sc = new SelectColumn();
            final Vector colExp = new Vector();
            colExp.add(" INTERVAL ");
            colExp.addAll(arguments.get(1).getColumnExpression());
            colExp.add(addStr);
            sc.setColumnExpression(colExp);
            arguments.setElementAt(sc, 1);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String addStr = "";
        final String funName = this.functionName.getColumnName();
        if (funName.equalsIgnoreCase("ADDYEAR")) {
            addStr = "yy";
        }
        else if (funName.equalsIgnoreCase("ADDMONTH")) {
            addStr = "mm";
        }
        else if (funName.equalsIgnoreCase("ADDWEEK")) {
            addStr = "wk";
        }
        else if (funName.equalsIgnoreCase("ADDQUARTER")) {
            addStr = "qq";
        }
        else if (funName.equalsIgnoreCase("ADDHOUR")) {
            addStr = "hh";
        }
        else if (funName.equalsIgnoreCase("ADDMINUTE")) {
            addStr = "mi";
        }
        else if (funName.equalsIgnoreCase("ADDSECOND")) {
            addStr = "ss";
        }
        this.functionName.setColumnName("DATEADD");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String argOne = "CAST(" + arguments.get(0).toString() + " AS DATETIME)";
        arguments.set(0, addStr);
        arguments.addElement(argOne);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector vector = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String funName = this.functionName.getColumnName();
        String dateColumn = vector.get(0).toString();
        final String interval = vector.get(1).toString();
        dateColumn = StringFunctions.handleLiteralStringDateForOracle(dateColumn);
        String query = null;
        if (funName.equalsIgnoreCase("adddate") || funName.equalsIgnoreCase("subdate") || funName.equalsIgnoreCase("date_add") || funName.equalsIgnoreCase("date_sub")) {
            String intervelVariable = null;
            String intervalType = null;
            if (vector.get(1) instanceof SelectColumn) {
                final SelectColumn selectColumn = vector.get(1);
                final Vector vc = selectColumn.getColumnExpression();
                intervelVariable = vc.elementAt(1).toString();
                intervalType = vc.elementAt(2).toString();
            }
            if (intervelVariable == null && intervalType == null) {
                throw new ConvertException("Invalid Argument Value for Function " + funName + "", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { funName });
            }
            if (funName.equalsIgnoreCase("adddate") || funName.equalsIgnoreCase("date_add")) {
                if (intervalType.equalsIgnoreCase("month")) {
                    query = "CAST(ADD_MONTHS(" + dateColumn + ", " + intervelVariable + ") AS TIMESTAMP)";
                }
                else if (intervalType.equalsIgnoreCase("week")) {
                    query = " (" + dateColumn + " +  ((interval '7' day) * " + intervelVariable + "))";
                }
                else if (intervalType.equalsIgnoreCase("quarter")) {
                    query = "CAST(ADD_MONTHS(" + dateColumn + ", " + intervelVariable + " * 3) AS TIMESTAMP)";
                }
                else {
                    query = " (" + dateColumn + " + ((interval '1' " + intervalType + " ) *  " + intervelVariable + "))";
                }
            }
            else if (funName.equalsIgnoreCase("subdate") || funName.equalsIgnoreCase("date_sub")) {
                if (intervalType.equalsIgnoreCase("month")) {
                    query = "CAST(ADD_MONTHS(" + dateColumn + ", (" + intervelVariable + "* -1)) AS TIMESTAMP)";
                }
                else if (intervalType.equalsIgnoreCase("week")) {
                    query = " (" + dateColumn + " -  ((interval '7' day) * " + intervelVariable + " ))";
                }
                else if (intervalType.equalsIgnoreCase("quarter")) {
                    query = "CAST(ADD_MONTHS(" + dateColumn + ", " + intervelVariable + " * -3 ) AS TIMESTAMP)";
                }
                else {
                    query = " (" + dateColumn + " - ((interval '1' " + intervalType + " ) *  " + intervelVariable + "))";
                }
            }
        }
        else if (funName.equalsIgnoreCase("addtime") || funName.equalsIgnoreCase("subtime")) {
            String operator = null;
            if (funName.equalsIgnoreCase("addtime")) {
                operator = " + ";
            }
            else if (funName.equalsIgnoreCase("subtime")) {
                operator = " - ";
            }
            query = " ( " + dateColumn + operator + " INTERVAL " + interval + " HOUR TO SECOND ) ";
        }
        this.functionName.setColumnName(query);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
