package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class trim extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TRIM");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String newArgumentQualifier = this.getArgumentQualifier();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (FunctionCalls.charToIntName) {
            this.functionName.setColumnName("TRIM");
            this.functionName.setTableName("DBO");
        }
        else if (newArgumentQualifier != null) {
            if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
                this.functionName.setColumnName("LTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final Vector trimArgument = new Vector();
                trimArgument.addElement(this.functionArguments.get(0));
                this.setFunctionArguments(trimArgument);
            }
            else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
                this.functionName.setColumnName("RTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final Vector trimArgument = new Vector();
                trimArgument.addElement(this.functionArguments.get(0));
                this.setFunctionArguments(trimArgument);
            }
            else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
                this.functionName.setColumnName("LTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final FunctionCalls trimBothSides = new FunctionCalls();
                final TableColumn trimRightFunction = new TableColumn();
                trimRightFunction.setColumnName("RTRIM");
                trimBothSides.setFunctionName(trimRightFunction);
                final Vector trimArgument2 = new Vector();
                final Vector ltrimArgument = new Vector();
                trimArgument2.addElement(this.functionArguments.get(0));
                trimBothSides.setFunctionArguments(trimArgument2);
                ltrimArgument.addElement(trimBothSides);
                this.setFunctionArguments(ltrimArgument);
            }
        }
        else {
            this.functionName.setColumnName("LTRIM");
            this.setArgumentQualifier(null);
            this.setTrailingString(null);
            this.setFromInTrim(null);
            this.setLengthString(null);
            final FunctionCalls trimBothSides = new FunctionCalls();
            final TableColumn trimRightFunction = new TableColumn();
            trimRightFunction.setColumnName("RTRIM");
            trimBothSides.setFunctionName(trimRightFunction);
            final Vector trimArgument2 = new Vector();
            final Vector ltrimArgument = new Vector();
            trimArgument2.addElement(this.functionArguments.get(0));
            trimBothSides.setFunctionArguments(trimArgument2);
            ltrimArgument.addElement(trimBothSides);
            this.setFunctionArguments(ltrimArgument);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String newArgumentQualifier = this.getArgumentQualifier();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (newArgumentQualifier != null) {
            if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
                this.functionName.setColumnName("LTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final Vector trimArgument = new Vector();
                trimArgument.addElement(this.functionArguments.get(0));
                this.setFunctionArguments(trimArgument);
            }
            else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
                this.functionName.setColumnName("RTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final Vector trimArgument = new Vector();
                trimArgument.addElement(this.functionArguments.get(0));
                this.setFunctionArguments(trimArgument);
            }
            else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
                this.functionName.setColumnName("LTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final FunctionCalls trimBothSides = new FunctionCalls();
                final TableColumn trimRightFunction = new TableColumn();
                trimRightFunction.setColumnName("RTRIM");
                trimBothSides.setFunctionName(trimRightFunction);
                final Vector trimArgument2 = new Vector();
                final Vector ltrimArgument = new Vector();
                trimArgument2.addElement(this.functionArguments.get(0));
                trimBothSides.setFunctionArguments(trimArgument2);
                ltrimArgument.addElement(trimBothSides);
                this.setFunctionArguments(ltrimArgument);
            }
        }
        else {
            this.functionName.setColumnName("LTRIM");
            this.setArgumentQualifier(null);
            this.setTrailingString(null);
            this.setFromInTrim(null);
            this.setLengthString(null);
            final FunctionCalls trimBothSides = new FunctionCalls();
            final TableColumn trimRightFunction = new TableColumn();
            trimRightFunction.setColumnName("RTRIM");
            trimBothSides.setFunctionName(trimRightFunction);
            final Vector trimArgument2 = new Vector();
            final Vector ltrimArgument = new Vector();
            trimArgument2.addElement(this.functionArguments.get(0));
            trimBothSides.setFunctionArguments(trimArgument2);
            ltrimArgument.addElement(trimBothSides);
            this.setFunctionArguments(ltrimArgument);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String newArgumentQualifier = this.getArgumentQualifier();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (newArgumentQualifier != null) {
            if (newArgumentQualifier.equalsIgnoreCase("LEADING")) {
                this.functionName.setColumnName("LTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final Vector trimArgument = new Vector();
                trimArgument.addElement(this.functionArguments.get(0));
                this.setFunctionArguments(trimArgument);
            }
            else if (newArgumentQualifier.equalsIgnoreCase("TRAILING")) {
                this.functionName.setColumnName("RTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final Vector trimArgument = new Vector();
                trimArgument.addElement(this.functionArguments.get(0));
                this.setFunctionArguments(trimArgument);
            }
            else if (newArgumentQualifier.equalsIgnoreCase("BOTH")) {
                this.functionName.setColumnName("LTRIM");
                this.setArgumentQualifier(null);
                this.setTrailingString(null);
                this.setFromInTrim(null);
                this.setLengthString(null);
                final FunctionCalls trimBothSides = new FunctionCalls();
                final TableColumn trimRightFunction = new TableColumn();
                trimRightFunction.setColumnName("RTRIM");
                trimBothSides.setFunctionName(trimRightFunction);
                final Vector trimArgument2 = new Vector();
                final Vector ltrimArgument = new Vector();
                trimArgument2.addElement(this.functionArguments.get(0));
                trimBothSides.setFunctionArguments(trimArgument2);
                ltrimArgument.addElement(trimBothSides);
                this.setFunctionArguments(ltrimArgument);
            }
        }
        else {
            this.functionName.setColumnName("LTRIM");
            this.setArgumentQualifier(null);
            this.setTrailingString(null);
            this.setFromInTrim(null);
            this.setLengthString(null);
            final FunctionCalls trimBothSides = new FunctionCalls();
            final TableColumn trimRightFunction = new TableColumn();
            trimRightFunction.setColumnName("RTRIM");
            trimBothSides.setFunctionName(trimRightFunction);
            final Vector trimArgument2 = new Vector();
            final Vector ltrimArgument = new Vector();
            trimArgument2.addElement(this.functionArguments.get(0));
            trimBothSides.setFunctionArguments(trimArgument2);
            ltrimArgument.addElement(trimBothSides);
            this.setFunctionArguments(ltrimArgument);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        final String newArgumentQualifier = this.getArgumentQualifier();
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
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String newArgumentQualifier = this.getArgumentQualifier();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String newArgumentQualifier = this.getArgumentQualifier();
        this.functionName.setColumnName("TRIM");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final int argLength = this.functionArguments.size();
        if (argLength == 2) {
            return;
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TRIM");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final int argLength = this.functionArguments.size();
        if (argLength > 1) {
            if (arguments.elementAt(1) instanceof SelectColumn) {
                this.setArgumentQualifier("BOTH " + arguments.get(1).toString());
                arguments.removeElementAt(1);
            }
            else {
                this.setArgumentQualifier("BOTH");
            }
        }
        else {
            this.setArgumentQualifier("BOTH");
        }
        this.setFromInTrim("FROM");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TRIM");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final int argLength = this.functionArguments.size();
        if (argLength > 1) {
            if (arguments.elementAt(1) instanceof SelectColumn) {
                this.setArgumentQualifier("BOTH " + arguments.get(1).toString());
                arguments.removeElementAt(1);
            }
            else {
                this.setArgumentQualifier("BOTH");
            }
        }
        else {
            this.setArgumentQualifier("BOTH");
        }
        this.setFromInTrim("FROM");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String newArgumentQualifier = this.getArgumentQualifier();
        this.functionName.setColumnName("TRIM");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final int argLength = this.functionArguments.size();
        if (argLength == 2) {
            return;
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String newArgumentQualifier = this.getArgumentQualifier();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                sc.convertSelectColumnToTextDataType();
                arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (newArgumentQualifier == null) {
            this.setArgumentQualifier("BOTH");
            this.setFromInTrim("FROM");
        }
        this.setFunctionArguments(arguments);
    }
}
