package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class percentile extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("PERCENTILE_CONT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.elementAt(1) instanceof SelectColumn) {
            final SelectColumn sc = arguments.elementAt(1);
            final Vector vc = sc.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
                throw new ConvertException("Invalid Argument Value for Function PERCENTILE", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "PERCENTILE", "RANGE" });
            }
            String range_str = vc.elementAt(0);
            range_str = range_str.replaceAll("'", "");
            this.validatePercentileRange(range_str, "PERCENTILE");
            final SelectColumn sc_percentile = new SelectColumn();
            final Vector vc_percentile = new Vector();
            final int percentile = Integer.parseInt(range_str);
            final String percentile_str = Double.toString(percentile / 100.0);
            vc_percentile.addElement(percentile_str);
            sc_percentile.setColumnExpression(vc_percentile);
            arguments.set(1, sc_percentile);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String qry = "";
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + "))";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String qry = "";
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + "))";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String qry = "";
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        qry = "PERCENTILE_CONT(" + arguments.get(1).toString() + ") WITHIN GROUP (ORDER BY (" + arguments.get(0).toString() + "))";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
