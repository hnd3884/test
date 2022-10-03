package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class FirstDateCurrentWeek extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        Vector arguments = new Vector();
        final int argLength = this.functionArguments.size();
        for (int i_count = 0; i_count < argLength; ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0 && this.functionArguments.elementAt(i_count).getColumnExpression().size() == 1 && this.functionArguments.elementAt(i_count).getColumnExpression().get(0) instanceof String) {
                    String dateString = this.functionArguments.elementAt(i_count).getColumnExpression().get(0).toString();
                    dateString = "CAST(" + dateString + " AS TIMESTAMP)";
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
        String date = "";
        int startDay = 1;
        if (argLength == 2) {
            startDay = Integer.parseInt(arguments.get(1).toString());
            if (startDay > 7 || startDay < 1) {
                throw new ConvertException("Start Day value invalid.");
            }
        }
        date = arguments.elementAt(0).toString();
        arguments = new Vector();
        final SelectColumn firstDateSC = new SelectColumn();
        final SelectColumn dateSC = new SelectColumn();
        final Vector dateSCVector = new Vector();
        dateSCVector.addElement(date);
        dateSC.setColumnExpression(dateSCVector);
        arguments.addElement(dateSC);
        arguments.addElement("-");
        arguments.addElement("interval");
        arguments.addElement("'1'");
        arguments.addElement("day");
        arguments.addElement("*");
        arguments.addElement("(");
        final SelectColumn castFSC = new SelectColumn();
        final Vector castFSCColExp = new Vector();
        final FunctionCalls castFC = new FunctionCalls();
        castFC.getFunctionName().setColumnName("cast");
        final Vector castFCArgs = new Vector();
        final SelectColumn extractFSC = new SelectColumn();
        final Vector extractFSCColExp = new Vector();
        final FunctionCalls extractFC = new FunctionCalls();
        extractFC.getFunctionName().setColumnName("EXTRACT");
        final Vector extractFCArgs = new Vector();
        extractFCArgs.addElement(dateSC);
        extractFC.setFunctionArguments(extractFCArgs);
        extractFC.setFromInTrim("FROM");
        extractFC.setTrailingString("DOW");
        extractFSCColExp.addElement(extractFC);
        extractFSCColExp.addElement("+");
        extractFSCColExp.addElement("1");
        extractFSC.setColumnExpression(extractFSCColExp);
        castFCArgs.addElement(extractFSC);
        castFCArgs.addElement("int");
        castFC.setFunctionArguments(castFCArgs);
        castFC.setAsDatatype("as");
        castFSCColExp.addElement(castFC);
        castFSC.setColumnExpression(castFSCColExp);
        arguments.addElement(castFSC);
        arguments.addElement("-");
        final SelectColumn startDaySC = new SelectColumn();
        final Vector startDaySCColExp = new Vector();
        startDaySCColExp.addElement(startDay);
        startDaySC.setColumnExpression(startDaySCColExp);
        arguments.addElement(startDaySC);
        arguments.addElement(")");
        firstDateSC.setColumnExpression(arguments);
        this.functionName.setColumnName("");
        final Vector fnArgs = new Vector();
        fnArgs.addElement(firstDateSC);
        this.setFunctionArguments(fnArgs);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final Vector arguments = new Vector();
        final Vector vector1 = new Vector();
        final Vector vector2 = new Vector();
        final Vector vector3 = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector1.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector3.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector1.addElement(this.functionArguments.elementAt(i_count));
                vector2.addElement(this.functionArguments.elementAt(i_count));
                vector3.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String startDay_str = "1";
        final Object date = null;
        if (argLength == 2 && vector1.elementAt(1) instanceof SelectColumn) {
            final SelectColumn sc = vector1.elementAt(1);
            final Vector vc = sc.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
                throw new ConvertException("Invalid Argument Value for Function FIRST_DATE_CURRENT_WEEK", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "FIRST_DATE_CURRENT_WEEK", "WEEK_START_DAY" });
            }
            startDay_str = vc.elementAt(0);
            if (startDay_str.equalsIgnoreCase("null")) {
                startDay_str = "1";
            }
            startDay_str = startDay_str.replaceAll("'", "");
            this.validateWeek_Start_Day(startDay_str, this.functionName.getColumnName().toUpperCase());
        }
        this.functionName.setColumnName("IF");
        final SelectColumn sc_whereitem = new SelectColumn();
        final Vector vc_whereitem = new Vector();
        final WhereItem wi_if = new WhereItem();
        final WhereColumn wi_left = new WhereColumn();
        final Vector vc_wileft = new Vector();
        final WhereColumn wi_right = new WhereColumn();
        final Vector vc_wiright = new Vector();
        final FunctionCalls fnCl_dayofweek = new FunctionCalls();
        final TableColumn tbCl_dayofweek = new TableColumn();
        tbCl_dayofweek.setColumnName("DAYOFWEEK");
        fnCl_dayofweek.setFunctionName(tbCl_dayofweek);
        final Vector vc_dayofweek = new Vector();
        vc_dayofweek.addElement(vector1.get(0));
        fnCl_dayofweek.setFunctionArguments(vc_dayofweek);
        vc_wileft.addElement(fnCl_dayofweek);
        wi_left.setColumnExpression(vc_wileft);
        wi_if.setLeftWhereExp(wi_left);
        wi_if.setOperator(">=");
        vc_wiright.add(startDay_str);
        wi_right.setColumnExpression(vc_wiright);
        wi_if.setRightWhereExp(wi_right);
        vc_whereitem.addElement(wi_if);
        sc_whereitem.setColumnExpression(vc_whereitem);
        final SelectColumn sc_falseStmt = new SelectColumn();
        final Vector vc_falseStmt = new Vector();
        final FunctionCalls fnCl_dateSub = new FunctionCalls();
        final TableColumn tbCl_dateSub = new TableColumn();
        tbCl_dateSub.setColumnName("DATE_SUB");
        fnCl_dateSub.setFunctionName(tbCl_dateSub);
        final Vector vc_dateSub = new Vector();
        vc_dateSub.addElement(this.subdate(to_sqs, from_sqs, vector3, startDay_str));
        final SelectColumn sc_interval = new SelectColumn();
        final TableColumn tbCl_interval = new TableColumn();
        tbCl_interval.setColumnName("INTERVAL");
        final Vector vc_interval = new Vector();
        vc_interval.addElement(tbCl_interval);
        vc_interval.addElement("'7'");
        vc_interval.addElement("DAY");
        sc_interval.setColumnExpression(vc_interval);
        vc_dateSub.addElement(sc_interval);
        fnCl_dateSub.setFunctionArguments(vc_dateSub);
        vc_falseStmt.addElement(fnCl_dateSub);
        sc_falseStmt.setOpenBrace("(");
        sc_falseStmt.setCloseBrace(")");
        sc_falseStmt.setColumnExpression(vc_falseStmt);
        arguments.addElement(sc_whereitem);
        arguments.addElement(this.subdate(to_sqs, from_sqs, vector2, startDay_str));
        arguments.addElement(sc_falseStmt);
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];
        for (int i_count = 0; i_count < argLength; ++i_count) {
            argu[i_count] = new StringBuffer();
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                argu[i_count].append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                argu[i_count].append(this.functionArguments.elementAt(i_count));
            }
        }
        int startDay = 1;
        String date = "";
        String qry = "";
        date = argu[0].toString();
        if (argLength == 2) {
            startDay = Integer.parseInt(argu[1].toString());
            if (startDay > 7 || startDay < 1) {
                throw new ConvertException("Start Day value invalid.");
            }
        }
        qry = "timestamp((" + date + ")) - interval '1' day * ((DAYOFWEEK(" + date + ") -" + startDay + "))";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    public SelectColumn subdate(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final Vector vector, final String start_day) throws ConvertException {
        final SelectColumn sc_subdate = new SelectColumn();
        final Vector vc_subdateIn = new Vector();
        final Vector vc_subdateOut = new Vector();
        final FunctionCalls fnCl_subdate = new FunctionCalls();
        final TableColumn tbCl_subdate = new TableColumn();
        tbCl_subdate.setColumnName("subdate");
        fnCl_subdate.setFunctionName(tbCl_subdate);
        vc_subdateIn.addElement(vector.get(0));
        final SelectColumn sc_interval = new SelectColumn();
        final Vector vc_interval = new Vector();
        final FunctionCalls fnCl_dayofweek = new FunctionCalls();
        final TableColumn tbCl_dayofweek = new TableColumn();
        tbCl_dayofweek.setColumnName("DAYOFWEEK");
        fnCl_dayofweek.setFunctionName(tbCl_dayofweek);
        final Vector vc_dayofweek = new Vector();
        vc_dayofweek.addElement(vector.get(0));
        fnCl_dayofweek.setFunctionArguments(vc_dayofweek);
        vc_interval.addElement(fnCl_dayofweek);
        vc_interval.addElement("-");
        vc_interval.addElement(start_day);
        sc_interval.setOpenBrace("(");
        sc_interval.setCloseBrace(")");
        sc_interval.setColumnExpression(vc_interval);
        vc_subdateIn.addElement(sc_interval);
        fnCl_subdate.setFunctionArguments(vc_subdateIn);
        vc_subdateOut.addElement(fnCl_subdate);
        sc_subdate.setColumnExpression(vc_subdateOut);
        return sc_subdate;
    }
}
