package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class WeekOfMonth extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        Vector arguments = new Vector();
        for (int argLength = this.functionArguments.size(), i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0 && this.functionArguments.elementAt(i_count).getColumnExpression().size() == 1 && this.functionArguments.elementAt(i_count).getColumnExpression().get(0) instanceof String) {
                    String dateString = this.functionArguments.elementAt(i_count).getColumnExpression().get(0).toString();
                    dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS TIMESTAMP)";
                    arguments.addElement(dateString);
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String date = arguments.get(0).toString();
        arguments = new Vector();
        final SelectColumn weekOfMonthSC = new SelectColumn();
        final FunctionCalls zrWeekFC = new FunctionCalls();
        zrWeekFC.getFunctionName().setColumnName("ZR_WeekDtNwkStrtDay");
        final Vector zrWeekFCArgs = new Vector();
        final SelectColumn dateFSC = new SelectColumn();
        final Vector dateFSCColExp = new Vector();
        final FunctionCalls dateFC = new FunctionCalls();
        dateFC.getFunctionName().setColumnName("date");
        final Vector dateFCArgs = new Vector();
        final SelectColumn dateSC = new SelectColumn();
        final Vector dateSCVector = new Vector();
        dateSCVector.addElement(date);
        dateSC.setColumnExpression(dateSCVector);
        dateFCArgs.addElement(dateSC);
        dateFC.setFunctionArguments(dateFCArgs);
        dateFSCColExp.addElement(dateFC);
        dateFSC.setColumnExpression(dateFSCColExp);
        zrWeekFCArgs.addElement(dateFSC);
        zrWeekFCArgs.addElement("4");
        zrWeekFC.setFunctionArguments(zrWeekFCArgs);
        arguments.addElement(zrWeekFC);
        final FunctionCalls zrWeekFC2 = new FunctionCalls();
        zrWeekFC2.getFunctionName().setColumnName("ZR_WeekDtNwkStrtDay");
        final Vector zrWeekFC2Args = new Vector();
        final SelectColumn dateFSC2 = new SelectColumn();
        final Vector dateFSC2ColExp = new Vector();
        final FunctionCalls dateFC2 = new FunctionCalls();
        dateFC2.getFunctionName().setColumnName("DATE");
        final Vector dateFC2Args = new Vector();
        final SelectColumn castFSC = new SelectColumn();
        final Vector castFSCColExp = new Vector();
        final FunctionCalls castFC = new FunctionCalls();
        castFC.getFunctionName().setColumnName("cast");
        final Vector castFCArgs = new Vector();
        final SelectColumn extractFSC = new SelectColumn();
        final Vector extractFSCColExp = new Vector();
        final FunctionCalls extractFC = new FunctionCalls();
        extractFC.getFunctionName().setColumnName("extract");
        final Vector extractFCArgs = new Vector();
        extractFC.setTrailingString("DAY");
        extractFC.setFromInTrim("from");
        extractFCArgs.addElement(dateSC);
        extractFC.setFunctionArguments(extractFCArgs);
        extractFSCColExp.addElement(extractFC);
        extractFSC.setColumnExpression(extractFSCColExp);
        castFCArgs.addElement(extractFSC);
        castFC.setAsDatatype("as");
        castFCArgs.addElement("int");
        castFC.setFunctionArguments(castFCArgs);
        castFSCColExp.addElement(dateSC);
        castFSCColExp.addElement("-");
        castFSCColExp.addElement(castFC);
        castFSCColExp.addElement("-");
        castFSCColExp.addElement("1");
        castFSC.setColumnExpression(castFSCColExp);
        dateFC2Args.addElement(castFSC);
        dateFC2.setFunctionArguments(dateFC2Args);
        dateFSC2ColExp.addElement(dateFC2);
        dateFSC2.setColumnExpression(dateFSC2ColExp);
        zrWeekFC2Args.addElement(dateFSC2);
        zrWeekFC2Args.addElement("4");
        zrWeekFC2.setFunctionArguments(zrWeekFC2Args);
        arguments.addElement("-");
        arguments.addElement(zrWeekFC2);
        arguments.addElement("+");
        arguments.addElement("1");
        weekOfMonthSC.setColumnExpression(arguments);
        this.functionName.setColumnName("");
        final Vector fnArgs = new Vector();
        fnArgs.addElement(weekOfMonthSC);
        this.setFunctionArguments(fnArgs);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Object date = arguments.get(0);
        arguments = new Vector();
        final SelectColumn weekOfMonthSC = new SelectColumn();
        final FunctionCalls weekFC = new FunctionCalls();
        weekFC.getFunctionName().setColumnName("week");
        final Vector weekFCVector = new Vector();
        final SelectColumn dateSc = new SelectColumn();
        final Vector dateScVector = new Vector();
        dateScVector.addElement(date);
        dateSc.setColumnExpression(dateScVector);
        weekFCVector.addElement(dateSc);
        weekFC.setFunctionArguments(weekFCVector);
        arguments.addElement(weekFC);
        arguments.addElement("-");
        final FunctionCalls weekTwoFC = new FunctionCalls();
        weekTwoFC.getFunctionName().setColumnName("week");
        final Vector WeekTwoFCArgs = new Vector();
        final SelectColumn subDateSC = new SelectColumn();
        final Vector subDateSCColExp = new Vector();
        final FunctionCalls subDateFC = new FunctionCalls();
        subDateFC.getFunctionName().setColumnName("subdate");
        final Vector subDateFCArgs = new Vector();
        subDateFCArgs.addElement(dateSc);
        final SelectColumn daySC = new SelectColumn();
        final Vector daySCColExp = new Vector();
        final FunctionCalls dayFC = new FunctionCalls();
        dayFC.getFunctionName().setColumnName("day");
        final Vector dayFCArgs = new Vector();
        dayFCArgs.addElement(dateSc);
        dayFC.setFunctionArguments(dayFCArgs);
        daySCColExp.addElement(dayFC);
        daySCColExp.addElement("-");
        daySCColExp.addElement("1");
        daySC.setColumnExpression(daySCColExp);
        subDateFCArgs.addElement(daySC);
        subDateFC.setFunctionArguments(subDateFCArgs);
        subDateSCColExp.addElement(subDateFC);
        subDateSC.setColumnExpression(subDateSCColExp);
        WeekTwoFCArgs.addElement(subDateSC);
        weekTwoFC.setFunctionArguments(WeekTwoFCArgs);
        arguments.addElement(weekTwoFC);
        arguments.addElement("+");
        arguments.addElement("1");
        weekOfMonthSC.setColumnExpression(arguments);
        this.functionName.setColumnName("");
        final Vector fnArgs = new Vector();
        fnArgs.addElement(weekOfMonthSC);
        this.setFunctionArguments(fnArgs);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];
        for (int i_count = 0; i_count < argLength; ++i_count) {
            argu[i_count] = new StringBuffer();
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0) {
                    this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                }
                argu[i_count].append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                argu[i_count].append(this.functionArguments.elementAt(i_count));
            }
        }
        String date = "";
        String qry = "";
        date = argu[0].toString();
        qry = "week(" + date + ", 0) -week(timestamp((" + date + "))- (DAY(" + date + ")-1), 0) +1";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
