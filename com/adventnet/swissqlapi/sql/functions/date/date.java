package com.adventnet.swissqlapi.sql.functions.date;

import java.util.StringTokenizer;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class date extends FunctionCalls
{
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
        String dateString = vector.get(0).toString();
        dateString = StringFunctions.handleLiteralStringDateForOracle(dateString);
        final String query = "CAST(TO_CHAR(" + dateString + ", 'DD-MM-YYYY') as DATE)";
        this.functionName.setColumnName(query);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    private String getDB2DateFormat(final String date) {
        if (date.indexOf(".") != -1) {
            final StringTokenizer st = new StringTokenizer(date, ".");
            if (st.countTokens() == 3) {
                st.nextToken();
                st.nextToken();
                final String token3 = st.nextToken();
                if (token3.length() == 5) {
                    return "'DD.MM.YYYY'";
                }
            }
        }
        else if (date.indexOf("/") != -1) {
            final StringTokenizer st = new StringTokenizer(date, "/");
            if (st.countTokens() == 3) {
                st.nextToken();
                st.nextToken();
                final String token3 = st.nextToken();
                if (token3.length() == 5) {
                    return "'MM/DD/YYYY'";
                }
            }
        }
        else if (date.indexOf("-") != -1) {
            return this.getDB2DateFormatForHyphenatedDate(date);
        }
        return null;
    }
    
    private String getDB2DateFormatForHyphenatedDate(final String date) {
        final StringTokenizer st = new StringTokenizer(date, "-");
        if (st.countTokens() == 3) {
            final String token1 = st.nextToken();
            st.nextToken();
            final String token2 = st.nextToken();
            if (token2.length() == 5) {
                return "'MM-DD-YYYY'";
            }
            if (token1.length() == 5) {
                return "'YYYY-MM-DD'";
            }
        }
        return null;
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONVERT");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        arguments.add(0, "DATETIME");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONVERT");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        arguments.add(0, "DATETIME");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                this.handleStringLiteralForDateTime(from_sqs, 0, false);
                arguments.addElement(this.functionArguments.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.getDateArithmetic() != null) {
            final String[] dateArith = this.getDateArithmetic().trim().split(" ");
            if (dateArith.length > 0 && (dateArith[0].equalsIgnoreCase("+") || dateArith[0].equalsIgnoreCase("-"))) {
                final String newDateArith = " " + dateArith[0] + " " + dateArith[1] + " * " + " interval '1 " + dateArith[2] + "'";
                this.setDateArithmetic(newDateArith);
            }
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("DATE");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                this.handleStringLiteralForDateTime(from_sqs, 0, false);
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        arguments.add("DATE");
        this.setAsDatatype("AS");
        this.setFunctionArguments(arguments);
    }
}
