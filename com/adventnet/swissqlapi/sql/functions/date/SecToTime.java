package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class SecToTime extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String qry = " to_char( (" + arguments.get(0) + " ||' seconds')::interval, 'HH24:MI:SS' )";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final Vector vector = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.functionName.setColumnName("CONVERT");
        final SelectColumn sc_sectotime = new SelectColumn();
        final Vector vc_sectotime = new Vector();
        final FunctionCalls fnCl_sectotime = new FunctionCalls();
        final TableColumn tbCl_sectotime = new TableColumn();
        tbCl_sectotime.setColumnName("SEC_TO_TIME");
        fnCl_sectotime.setFunctionName(tbCl_sectotime);
        fnCl_sectotime.setFunctionArguments(vector);
        vc_sectotime.addElement(fnCl_sectotime);
        sc_sectotime.setColumnExpression(vc_sectotime);
        arguments.addElement(sc_sectotime);
        arguments.addElement("char(20)");
        this.setFunctionArguments(arguments);
    }
}
