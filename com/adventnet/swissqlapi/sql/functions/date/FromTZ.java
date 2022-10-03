package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class FromTZ extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() > 1) {
            final SelectColumn arg2 = arguments.get(1);
            for (int j = 0; j < arg2.getColumnExpression().size(); ++j) {
                final Object obj = arg2.getColumnExpression().get(j);
                if (obj instanceof String) {
                    String objStr = obj.toString().trim();
                    if (objStr.startsWith("'")) {
                        objStr = objStr.substring(1, objStr.length() - 1).trim();
                    }
                    if (objStr.startsWith(":")) {
                        objStr = objStr.substring(1);
                    }
                    arg2.getColumnExpression().setElementAt("'" + objStr + "'", j);
                }
            }
        }
        if (this.atTimeZoneRegion != null) {
            final SelectColumn convAtTimeZoneRegion = this.atTimeZoneRegion.toTeradataSelect(to_sqs, from_sqs);
            for (int j = 0; j < convAtTimeZoneRegion.getColumnExpression().size(); ++j) {
                final Object obj = convAtTimeZoneRegion.getColumnExpression().get(j);
                if (obj instanceof String) {
                    String objStr = obj.toString().trim();
                    if (objStr.startsWith("'")) {
                        objStr = objStr.substring(1, objStr.length() - 1).trim();
                    }
                    if (objStr.startsWith(":")) {
                        objStr = objStr.substring(1);
                    }
                    convAtTimeZoneRegion.getColumnExpression().setElementAt("'" + objStr + "'", j);
                }
            }
            arguments.add(convAtTimeZoneRegion);
            this.atTimeZoneRegion = null;
        }
        else if (arguments.size() == 2 && arguments.get(1) instanceof SelectColumn) {
            arguments.add(arguments.get(1));
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("from_unixtime")) {
            this.functionName.setColumnName("FROM_UNIXTIME");
            final Vector arguments = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            this.setFunctionArguments(arguments);
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("to_days")) {
            final StringBuffer arguments2 = new StringBuffer();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    if (i_count == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, false);
                    }
                    arguments2.append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments2.append(this.functionArguments.elementAt(i_count));
                }
            }
            this.functionName.setColumnName("date_part('day',(cast(" + (Object)arguments2 + " as timestamp)+interval '1'day *364)-timestamp('0001-01-01'))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("from_days")) {
            final StringBuffer arguments2 = new StringBuffer();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments2.append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments2.append(this.functionArguments.elementAt(i_count));
                }
            }
            this.functionName.setColumnName(" (timestamp('01-01-0001 00:00:00')+interval '1' day *(" + (Object)arguments2 + "-364))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("dayname")) {
            this.functionName.setColumnName("DATE_FORMAT");
            final Vector arguments = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    if (i_count == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                    }
                    arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            arguments.addElement("'%W'");
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        if (this.functionName.getColumnName().equalsIgnoreCase("from_days")) {
            final StringBuffer arguments = new StringBuffer();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments.append(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.append(this.functionArguments.elementAt(i_count));
                }
            }
            this.functionName.setColumnName("(TO_TIMESTAMP(0) + interval '1' day * (ROUND(" + arguments.toString() + ")-719528))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("to_days")) {
            final StringBuffer arguments = new StringBuffer();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    if (i_count == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, canUseUDFFunction);
                    }
                    arguments.append(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.append(this.functionArguments.elementAt(i_count));
                }
            }
            this.functionName.setColumnName("((extract(EPOCH from DATE(" + arguments.toString() + "))/86400)::int+719528)");
            if (canUseUDFFunction) {
                this.functionName.setColumnName("TO_DAYS(" + arguments.toString() + ")");
            }
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("FROM_UNIXTIME")) {
            this.functionName.setColumnName("TO_TIMESTAMP");
            final Vector arguments2 = new Vector();
            for (int i = 0; i < 1; ++i) {
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    arguments2.addElement(this.functionArguments.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else {
                    arguments2.addElement(this.functionArguments.elementAt(i));
                }
            }
            this.setFunctionArguments(arguments2);
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                long seconds = 0L;
                String columnName = "timestamp '1970-01-01 00:00:00'";
                try {
                    seconds = Long.parseLong(arguments2.get(0).toString());
                }
                catch (final Exception e) {
                    seconds = 0L;
                }
                if (seconds < 0L) {
                    columnName = "NULL";
                }
                else if (seconds > 0L) {
                    columnName = "(timestamp '1970-01-01 00:00:00' + interval '1' second * " + seconds + ")";
                }
                this.functionName.setColumnName(columnName);
                this.setOpenBracesForFunctionNameRequired(false);
                this.setFunctionArguments(new Vector());
            }
        }
    }
}
