package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class rpad extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RPAD");
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
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (from_sqs != null && from_sqs.isMSAzure()) {
            final String qry = "SUBSTRING(CAST(" + arguments.get(0).toString() + " AS VARCHAR) + REPLICATE(" + arguments.get(2).toString() + "," + arguments.get(1).toString() + "),1," + arguments.get(1).toString() + ")";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
        }
        if (FunctionCalls.charToIntName) {
            final Vector ramcoArguments = this.ramcoRPADProcessing(arguments);
            if (ramcoArguments.size() < 3) {
                this.functionName.setColumnName("CONVERT");
                ramcoArguments.insertElementAt("VARCHAR", 0);
                this.setFunctionArguments(ramcoArguments);
            }
            else {
                final Vector newArg = this.sqlServerConversion(this.functionName, this.functionArguments);
                this.setFunctionArguments(newArg);
            }
        }
        else {
            final Vector newArg2 = this.sqlServerConversion(this.functionName, this.functionArguments);
            this.setFunctionArguments(newArg2);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final Vector newArg = this.sqlServerConversion(this.functionName, this.functionArguments);
        this.setFunctionArguments(newArg);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONCAT");
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
        final int noOfArguments = this.functionArguments.size();
        final Object param1 = this.functionArguments.get(0);
        final Object param2 = this.functionArguments.get(1);
        Object param3;
        if (noOfArguments > 2) {
            param3 = this.functionArguments.get(2);
        }
        else {
            param3 = new String("' '");
        }
        final FunctionCalls len0 = new FunctionCalls();
        final FunctionCalls len2 = new FunctionCalls();
        final FunctionCalls repeat = new FunctionCalls();
        final FunctionCalls substring = new FunctionCalls();
        final Vector len0Arg = new Vector();
        final Vector len1Arg = new Vector();
        final Vector repeatArg = new Vector();
        final Vector substringArg = new Vector();
        final TableColumn innerFunction1 = new TableColumn();
        innerFunction1.setOwnerName(this.functionName.getOwnerName());
        innerFunction1.setTableName(this.functionName.getTableName());
        innerFunction1.setColumnName("LENGTH");
        final TableColumn innerFunction2 = new TableColumn();
        innerFunction2.setOwnerName(this.functionName.getOwnerName());
        innerFunction2.setTableName(this.functionName.getTableName());
        innerFunction2.setColumnName("REPEAT");
        final TableColumn innerFunction3 = new TableColumn();
        innerFunction3.setOwnerName(this.functionName.getOwnerName());
        innerFunction3.setTableName(this.functionName.getTableName());
        innerFunction3.setColumnName("SUBSTR");
        len0Arg.addElement(param1);
        len1Arg.addElement(param3);
        len0.setFunctionName(innerFunction1);
        len2.setFunctionName(innerFunction1);
        repeat.setFunctionName(innerFunction2);
        substring.setFunctionName(innerFunction3);
        len0.setFunctionArguments(len0Arg);
        len2.setFunctionArguments(len1Arg);
        final SelectColumn repeatArgument2 = new SelectColumn();
        repeatArgument2.setColumnExpression(new Vector());
        repeatArgument2.addColumnExpressionElement("(");
        repeatArgument2.addColumnExpressionElement(param2);
        repeatArgument2.addColumnExpressionElement("/");
        repeatArgument2.addColumnExpressionElement(len2);
        repeatArgument2.addColumnExpressionElement("+");
        repeatArgument2.addColumnExpressionElement("1");
        repeatArgument2.addColumnExpressionElement(")");
        repeatArg.addElement(param3);
        repeatArg.addElement(repeatArgument2);
        repeat.setFunctionArguments(repeatArg);
        final SelectColumn substringArgument3 = new SelectColumn();
        substringArgument3.setColumnExpression(new Vector());
        substringArgument3.addColumnExpressionElement(param2);
        substringArgument3.addColumnExpressionElement("-");
        substringArgument3.addColumnExpressionElement(len0);
        substringArg.add(repeat);
        substringArg.add("1");
        substringArg.add(substringArgument3);
        substring.setFunctionArguments(substringArg);
        this.functionArguments.setElementAt(substring, 1);
        this.functionArguments.setElementAt(param1, 0);
        this.functionArguments.setSize(2);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionArguments.size() != 3) {
            throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in PostgreSQL\n Function Arguments Count Mismatch\n");
        }
        this.functionName.setColumnName("RPAD");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count != 1) {
                    sc.convertSelectColumnToTextDataType();
                }
                arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 3) {
            String secArgument = arguments.get(1).toString();
            final Integer number = StringFunctions.getIntegerValue(secArgument);
            if (number != null) {
                secArgument = number.toString();
            }
            else if (from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForNumeric()) {
                secArgument = "TOINTEGER_UDF(" + secArgument + ")";
            }
            else {
                secArgument = "(" + secArgument + ")::integer";
            }
            arguments.set(1, secArgument);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RPAD");
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
        this.functionName.setColumnName("RPAD");
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
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RPAD");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RPAD");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RPAD");
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
    }
    
    private Vector ramcoRPADProcessing(final Vector functionArguments) {
        final Vector ramcoArguments = new Vector();
        if (functionArguments != null) {
            for (int i = 0; i < functionArguments.size(); ++i) {
                if (functionArguments.get(i) instanceof TableColumn) {
                    ramcoArguments.add(functionArguments.get(i));
                }
                else if (functionArguments.get(i) instanceof String) {
                    if (functionArguments.get(i).trim().startsWith("@")) {
                        ramcoArguments.add(functionArguments.get(i));
                    }
                    else if (functionArguments.get(i).trim().startsWith("'")) {
                        ramcoArguments.add(functionArguments.get(i));
                    }
                }
                else if (functionArguments.get(i) instanceof SelectColumn) {
                    final Vector selfunctionArguments = functionArguments.get(i).getColumnExpression();
                    final Vector tempVector = this.ramcoRPADProcessing(selfunctionArguments);
                    if (tempVector != null) {
                        for (int j = 0; j < tempVector.size(); ++j) {
                            ramcoArguments.add(tempVector.get(j));
                        }
                    }
                }
                else if (functionArguments.get(i) instanceof FunctionCalls) {
                    final Vector FunctionArgs = functionArguments.get(i).getFunctionArguments();
                    final Vector tempVector = this.ramcoRPADProcessing(FunctionArgs);
                    if (tempVector != null) {
                        for (int j = 0; j < tempVector.size(); ++j) {
                            ramcoArguments.add(tempVector.get(j));
                        }
                    }
                }
            }
        }
        return ramcoArguments;
    }
    
    private Vector sqlServerConversion(final TableColumn functionName, final Vector functionArguments) {
        final Vector newArg = new Vector();
        final int noOfArguments = functionArguments.size();
        final Object param1 = functionArguments.get(0);
        final Object param2 = functionArguments.get(1);
        Object param3;
        if (noOfArguments > 2) {
            param3 = functionArguments.get(2);
        }
        else {
            param3 = new String("' '");
        }
        final SelectColumn sc = new SelectColumn();
        final FunctionCalls replicate = new FunctionCalls();
        final FunctionCalls length1 = new FunctionCalls();
        final FunctionCalls length2 = new FunctionCalls();
        final FunctionCalls substring = new FunctionCalls();
        final TableColumn replicateFunction = new TableColumn();
        final TableColumn lengthFunction1 = new TableColumn();
        final TableColumn lengthFunction2 = new TableColumn();
        final TableColumn substringFunction = new TableColumn();
        replicateFunction.setOwnerName(functionName.getOwnerName());
        replicateFunction.setTableName(functionName.getTableName());
        replicateFunction.setColumnName("REPLICATE");
        final Vector replicateArg = new Vector();
        replicateArg.add(param3);
        final SelectColumn lengthArgSC = new SelectColumn();
        lengthFunction1.setOwnerName(functionName.getOwnerName());
        lengthFunction2.setOwnerName(functionName.getOwnerName());
        lengthFunction1.setTableName(functionName.getTableName());
        lengthFunction2.setTableName(functionName.getOwnerName());
        lengthFunction1.setColumnName("LEN");
        lengthFunction2.setColumnName("LEN");
        final Vector lengthArg1 = new Vector();
        lengthArg1.add(param1);
        length1.setFunctionName(lengthFunction1);
        length1.setFunctionArguments(lengthArg1);
        final Vector dummyArg = new Vector();
        dummyArg.add("(");
        dummyArg.add(param2);
        dummyArg.add(" - ");
        dummyArg.add(length1);
        lengthArgSC.setColumnExpression(dummyArg);
        replicateArg.add(lengthArgSC);
        replicate.setFunctionName(replicateFunction);
        replicate.setFunctionArguments(replicateArg);
        final Vector lengthArg2 = new Vector();
        lengthArg2.add(param3);
        length2.setFunctionName(lengthFunction2);
        length2.setFunctionArguments(lengthArg2);
        substringFunction.setOwnerName(functionName.getOwnerName());
        substringFunction.setTableName(functionName.getTableName());
        substringFunction.setColumnName("SUBSTRING");
        final Vector substringArg = new Vector();
        substringArg.add(param3);
        substringArg.add(" 1 ");
        final Vector dummyArg2 = new Vector();
        dummyArg2.add(" ( ");
        dummyArg2.add(param2);
        dummyArg2.add(" - ");
        dummyArg2.add(length1);
        dummyArg2.add(" ) % ");
        dummyArg2.add(length2);
        dummyArg2.add(") ");
        final SelectColumn lengthArgSC2 = new SelectColumn();
        lengthArgSC2.setColumnExpression(dummyArg2);
        substringArg.add(lengthArgSC2);
        substring.setFunctionName(substringFunction);
        substring.setFunctionArguments(substringArg);
        final Vector colExp = new Vector();
        colExp.add(" ( ");
        colExp.add(param1);
        colExp.add(" + ");
        colExp.add(replicate);
        colExp.add(" / ");
        colExp.add(length2);
        colExp.add(" ) ");
        colExp.add(" + ");
        colExp.add(substring);
        sc.setColumnExpression(colExp);
        newArg.add(sc);
        return newArg;
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RPAD");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count != 1) {
                    sc.convertSelectColumnToTextDataType();
                }
                arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
}
