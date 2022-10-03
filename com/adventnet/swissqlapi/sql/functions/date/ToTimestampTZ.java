package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Collection;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class ToTimestampTZ extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                SelectColumn sc = this.functionArguments.elementAt(i_count);
                boolean handleToCharForToTimestampTZ = false;
                if (sc.getColumnExpression().firstElement() instanceof FunctionCalls && sc.getColumnExpression().firstElement().getFunctionName().getColumnName().equalsIgnoreCase("to_char")) {
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
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final SelectColumn sc2 = arguments.get(0);
        final Vector newArguments = new Vector();
        String tzr = sc2.getColumnExpression().lastElement().toString();
        String tzrTrimmed = tzr.substring(1, tzr.length() - 1).trim().toUpperCase();
        if (tzrTrimmed.startsWith(":")) {
            tzrTrimmed = tzrTrimmed.substring(1);
        }
        if (tzr.startsWith("'") && (tzr.indexOf("/") != -1 || tzrTrimmed.startsWith("GMT") || SwisSQLUtils.getOracleTimeZones().contains(tzrTrimmed)) && tzrTrimmed.indexOf("MM") == -1 && tzrTrimmed.indexOf("YY") == -1 && tzrTrimmed.indexOf("DD") == -1) {
            tzr = tzr.substring(1, tzr.length() - 1).trim();
            if (tzr.startsWith(":")) {
                tzr = tzr.substring(1);
            }
            newArguments.add("'" + tzr + "'");
            if (this.atTimeZoneRegion != null) {
                final SelectColumn convAtTimeZoneRegion = this.getAtTimeZoneRegion().toTeradataSelect(to_sqs, from_sqs);
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
                newArguments.add(convAtTimeZoneRegion);
            }
            else {
                newArguments.add("'" + tzr + "'");
            }
            sc2.getColumnExpression().removeElementAt(sc2.getColumnExpression().size() - 1);
            if (sc2.getColumnExpression().lastElement().toString().equalsIgnoreCase("||")) {
                sc2.getColumnExpression().removeElementAt(sc2.getColumnExpression().size() - 1);
            }
            arguments.removeElementAt(arguments.size() - 1);
            arguments.addAll(newArguments);
        }
        boolean isDateArg = false;
        if (arguments.elementAt(0) instanceof SelectColumn) {
            if (arguments.elementAt(0).getColumnExpression().get(0) instanceof FunctionCalls) {
                final FunctionCalls dateFunc = arguments.elementAt(0).getColumnExpression().get(0);
                if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                    isDateArg = true;
                }
            }
            else if (arguments.elementAt(0).getColumnExpression().get(0) instanceof SelectColumn) {
                final SelectColumn funcCol = arguments.elementAt(0).getColumnExpression().get(0);
                if (funcCol.getColumnExpression().get(0) instanceof FunctionCalls) {
                    final FunctionCalls dateFunc2 = funcCol.getColumnExpression().get(0);
                    if (dateFunc2.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc2.getFunctionName().getColumnName(), dateFunc2.getFunctionArguments()).equalsIgnoreCase("date")) {
                        isDateArg = true;
                    }
                }
            }
        }
        if (isDateArg) {
            final FunctionCalls castTimestamp = new FunctionCalls();
            final TableColumn castTcn = new TableColumn();
            castTcn.setColumnName("CAST");
            castTimestamp.setFunctionName(castTcn);
            castTimestamp.setAsDatatype("AS");
            final DateClass castDatatype = new DateClass();
            castDatatype.setDatatypeName("TIMESTAMP");
            castDatatype.setOpenBrace("(");
            castDatatype.setSize("0");
            castDatatype.setClosedBrace(")");
            final Vector castTimestampArgs = new Vector();
            castTimestampArgs.add(arguments.elementAt(0));
            castTimestampArgs.add(castDatatype);
            castTimestamp.setFunctionArguments(castTimestampArgs);
            arguments.setElementAt(castTimestamp, 0);
        }
        this.setFunctionArguments(arguments);
        this.setAtTimeZoneRegion(null);
    }
}
