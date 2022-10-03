package com.adventnet.swissqlapi.sql.functions.date;

import java.util.HashMap;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Map;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class timestampDiff extends FunctionCalls
{
    private static Map<String, String> CONVERTIONMAP;
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final boolean isString = false;
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selColumn = this.functionArguments.elementAt(i_count);
                if (selColumn.getColumnExpression() != null && !selColumn.getColumnExpression().isEmpty() && selColumn.getColumnExpression().get(0) != null && selColumn.getColumnExpression().get(0) instanceof String) {
                    String stringValue = selColumn.getColumnExpression().get(0).toString();
                    stringValue = "CAST(" + this.handleStringLiteralForDateTime(stringValue, from_sqs) + " AS TIMESTAMP)";
                    selColumn.getColumnExpression().set(0, stringValue);
                }
                arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 2) {
            arguments.addElement("CURRENT_DATE");
        }
        if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            if (arguments.get(1).toString().trim().equalsIgnoreCase("to_timestamp(0)") && (arguments.get(0).toString().equalsIgnoreCase("SECOND") || arguments.get(0).toString().equalsIgnoreCase("MICROSECOND"))) {
                String qry = "cast(EXTRACT(EPOCH FROM  (" + arguments.get(2) + " - " + arguments.get(1) + ") ) as bigint)";
                qry = "(" + qry + " * " + timestampDiff.CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + ")";
                this.functionName.setColumnName(qry);
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else {
                this.functionName.setColumnName("DATEDIFF");
                this.setFunctionArguments(arguments);
            }
        }
        else {
            String qry = "";
            final boolean isSecond = arguments.get(0).toString().equalsIgnoreCase("SECOND");
            if (isSecond || arguments.get(0).toString().equalsIgnoreCase("MICROSECOND")) {
                boolean isToTimestamp = false;
                if (arguments.get(1).toString().trim().equalsIgnoreCase("to_timestamp(0)")) {
                    qry = "cast(EXTRACT(EPOCH FROM  (" + arguments.get(2) + " - " + arguments.get(1) + ") ) as bigint)";
                    isToTimestamp = true;
                }
                else {
                    qry = "cast(EXTRACT(EPOCH FROM (" + arguments.get(2) + " - " + arguments.get(1) + ")) as bigint)";
                }
                if (!isSecond) {
                    qry = "(" + qry + " * " + timestampDiff.CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + ")";
                }
                if (canUseUDFFunction && !isToTimestamp) {
                    qry = "TIMESTAMPDIFF(" + arguments.get(1) + "," + arguments.get(2) + "," + "1," + timestampDiff.CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + ")";
                }
            }
            else if (arguments.get(0).toString().equalsIgnoreCase("YEAR")) {
                qry = "cast(EXTRACT(YEAR FROM AGE(" + arguments.get(2) + "," + arguments.get(1) + ")) as bigint)";
                if (canUseUDFFunction) {
                    qry = "TIMESTAMPDIFF_YEAR(" + arguments.get(1) + "," + arguments.get(2) + ")";
                }
            }
            else if (arguments.get(0).toString().equalsIgnoreCase("MONTH")) {
                qry = "cast(((EXTRACT(YEAR FROM AGE(" + arguments.get(2) + "," + arguments.get(1) + ")) * 12) + EXTRACT(MONTH FROM AGE(" + arguments.get(2) + "," + arguments.get(1) + "))) as bigint)";
                if (canUseUDFFunction) {
                    qry = "TIMESTAMPDIFF_MONTH(" + arguments.get(1) + "," + arguments.get(2) + ")";
                }
            }
            else if (arguments.get(0).toString().equalsIgnoreCase("QUARTER")) {
                qry = "cast(((EXTRACT(YEAR FROM AGE(" + arguments.get(2) + "," + arguments.get(1) + ")) * 4) + (EXTRACT(MONTH FROM AGE(" + arguments.get(2) + "," + arguments.get(1) + "))::integer / 3)) as bigint)";
                if (canUseUDFFunction) {
                    qry = "TIMESTAMPDIFF_QUARTER(" + arguments.get(1) + "," + arguments.get(2) + ")";
                }
            }
            else {
                qry = "cast(div(EXTRACT(EPOCH FROM (" + arguments.get(2) + "-" + arguments.get(1) + "))::bigint," + timestampDiff.CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + "::bigint) as bigint)";
                if (canUseUDFFunction) {
                    qry = "TIMESTAMPDIFF(" + arguments.get(1) + "," + arguments.get(2) + "," + timestampDiff.CONVERTIONMAP.get(arguments.get(0).toString().trim().toLowerCase()) + ",1 )";
                }
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("age_years")) {
            arguments.addElement("YEAR");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("age_months")) {
            arguments.addElement("MONTH");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("days_between")) {
            arguments.addElement("DAY");
        }
        this.functionName.setColumnName("TIMESTAMPDIFF");
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() < 3) {
            arguments.add("now()");
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count != 0) {
                    this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.functionName.setColumnName("TIMESTAMPDIFF");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
            if (i_count >= 1) {
                final String argStr = "CAST( " + arguments.get(i_count).toString() + " AS DATETIME)";
                arguments.set(i_count, argStr);
            }
        }
        this.functionName.setColumnName("DATEDIFF");
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
        if (vector.size() == 3) {
            String dateString1 = vector.get(1).toString();
            String dateString2 = vector.get(2).toString();
            dateString1 = StringFunctions.handleLiteralStringDateForOracle(dateString1);
            dateString2 = StringFunctions.handleLiteralStringDateForOracle(dateString2);
            String query = "";
            if (vector.get(0).toString().equalsIgnoreCase("YEAR")) {
                query = "TRUNC( MONTHS_BETWEEN( " + dateString2 + ", " + dateString1 + ") / 12)";
            }
            else if (vector.get(0).toString().equalsIgnoreCase("MONTH")) {
                query = "TRUNC(MONTHS_BETWEEN( " + dateString2 + ", " + dateString1 + "))";
            }
            else if (vector.get(0).toString().equalsIgnoreCase("DAY")) {
                query = "TRUNC( CAST (" + dateString2 + " as DATE) -  CAST ( " + dateString1 + " as DATE))";
            }
            else if (vector.get(0).toString().equalsIgnoreCase("HOUR")) {
                query = "round(( CAST( " + dateString2 + " AS DATE ) - CAST( " + dateString1 + " AS DATE ) ) * 24)";
            }
            else if (vector.get(0).toString().equalsIgnoreCase("MINUTE")) {
                query = "round(( CAST( " + dateString2 + " AS DATE ) - CAST( " + dateString1 + " AS DATE ) ) * 1440)";
            }
            else if (vector.get(0).toString().equalsIgnoreCase("SECOND")) {
                query = "round(( CAST( " + dateString2 + " AS DATE ) - CAST( " + dateString1 + " AS DATE ) ) * 86400)";
            }
            else if (vector.get(0).toString().equalsIgnoreCase("Quarter")) {
                query = "TRUNC( MONTHS_BETWEEN( " + dateString2 + ", " + dateString1 + ") / 3)";
            }
            else if (vector.get(0).toString().equalsIgnoreCase("WEEK")) {
                query = "TRUNC(TRUNC(CAST( " + dateString2 + " AS DATE )  - CAST( " + dateString1 + " AS DATE )) / 7)";
            }
            this.functionName.setColumnName(query);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    static {
        (timestampDiff.CONVERTIONMAP = new HashMap<String, String>()).put("microsecond", "1000000");
        timestampDiff.CONVERTIONMAP.put("second", "1");
        timestampDiff.CONVERTIONMAP.put("minute", "60");
        timestampDiff.CONVERTIONMAP.put("hour", "3600");
        timestampDiff.CONVERTIONMAP.put("day", "86400");
        timestampDiff.CONVERTIONMAP.put("week", "604800");
        timestampDiff.CONVERTIONMAP.put("month", "2629760");
        timestampDiff.CONVERTIONMAP.put("quarter", "7889280");
        timestampDiff.CONVERTIONMAP.put("year", "31557120");
    }
}
