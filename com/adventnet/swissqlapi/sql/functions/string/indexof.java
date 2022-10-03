package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class indexof extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int argLength = this.functionArguments.size();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                sc.convertSelectColumnToTextDataType();
                arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        this.functionName.setColumnName("STRPOS");
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("INDEXOF") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS") || this.functionName.getColumnName().equalsIgnoreCase("SUBSTRING_POSITION")) {
            final Object temp = arguments.get(0);
            arguments.set(0, arguments.get(1));
            arguments.set(1, temp);
        }
        this.functionName.setColumnName("LOCATE");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];
        String qry = "";
        String firstArg = "";
        String secArg = "";
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            argu[i_count] = new StringBuffer();
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                sc.convertSelectColumnToTextDataType();
                argu[i_count].append(sc.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                argu[i_count].append(this.functionArguments.elementAt(i_count));
            }
        }
        firstArg = argu[0].toString();
        secArg = argu[1].toString();
        if (this.functionName.getColumnName().equalsIgnoreCase("INDEXOF")) {
            firstArg = argu[1].toString();
            secArg = argu[0].toString();
        }
        qry = " position(" + firstArg + "," + secArg + ")";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
