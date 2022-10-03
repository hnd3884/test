package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class insert extends FunctionCalls
{
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionArguments.size() != 4) {
            throw new ConvertException("\nGiven datatype " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in PostgreSQL\n Function Arguments Count Mismatch\n");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN CAST(" + arguments.get(0).toString() + " AS TEXT) ELSE overlay(CAST(" + arguments.get(0) + " AS TEXT) placing CAST(" + arguments.get(3) + " AS TEXT) from CAST(" + arguments.get(1) + " AS TEXT) for CAST(" + arguments.get(2) + " AS TEXT)) END";
        if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (substring(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || substring(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ")) END";
        }
        else if (from_sqs != null && from_sqs.canUseUDFFunctionsForText()) {
            qry = "overlay(CAST(" + arguments.get(0) + " AS TEXT) placing CAST(" + arguments.get(3) + " AS TEXT) from CAST(tointeger_udf(" + arguments.get(1) + ") AS BIGINT) for CAST(tointeger_udf(" + arguments.get(2) + ") AS BIGINT))";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
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
        }
        final String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LEN(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (substring(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) + " + arguments.get(3).toString() + " + substring(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ",LEN(" + arguments.get(0).toString() + ")))END";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(CAST(" + arguments.get(0).toString() + " AS VARCHAR)) THEN CAST(" + arguments.get(0).toString() + " AS VARCHAR) ELSE (CONCAT(substring(CAST(" + arguments.get(0).toString() + " AS VARCHAR),1,((" + arguments.get(1).toString() + ")-1)) , CAST(" + arguments.get(3).toString() + " AS VARCHAR) , substring(CAST(" + arguments.get(0).toString() + " AS VARCHAR)," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ",LENGTH(CAST(" + arguments.get(0).toString() + " AS VARCHAR))))) END";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String qry = "CASE WHEN " + arguments.get(1).toString() + " <= 0 OR " + arguments.get(1).toString() + " > LENGTH(" + arguments.get(0).toString() + ") THEN " + arguments.get(0).toString() + " ELSE (substr(" + arguments.get(0).toString() + ",1," + arguments.get(1).toString() + "-1) || " + arguments.get(3).toString() + " || substr(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + "+" + arguments.get(2).toString() + ",LENGTH(" + arguments.get(0).toString() + "))) END";
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
