package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class lpad extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LPAD");
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
    
    private String oracleTOSqlServer(final String ip_str, final String noofchars, final String fillchar) {
        String retStr = null;
        if (SwisSQLAPI.variableDatatypeMapping != null) {
            if (SwisSQLAPI.variableDatatypeMapping.containsKey(ip_str.substring(1, ip_str.length()))) {
                if (SwisSQLAPI.variableDatatypeMapping.get(ip_str.substring(1)) instanceof String) {
                    final String dataType = SwisSQLAPI.variableDatatypeMapping.get(ip_str.substring(1, ip_str.length())).toLowerCase();
                    if (dataType != null && dataType.startsWith("number")) {
                        retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(CONVERT(VARCHAR(4000), " + ip_str + "),1," + noofchars + ") ";
                        retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1," + noofchars + "-LEN(" + ip_str + ")) + CONVERT(VARCHAR(4000)," + ip_str + ") END";
                    }
                    else {
                        retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(" + ip_str + ",1," + noofchars + ") ";
                        retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1," + noofchars + "-LEN(" + ip_str + ")) + " + ip_str + " END";
                    }
                }
                else {
                    retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(" + ip_str + ",1," + noofchars + ") ";
                    retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1," + noofchars + "-LEN(" + ip_str + ")) + " + ip_str + " END";
                }
            }
            else {
                retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(" + ip_str + ",1," + noofchars + ") ";
                retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1," + noofchars + "-LEN(" + ip_str + ")) + " + ip_str + " END";
            }
        }
        else {
            retStr = "CASE WHEN LEN(" + ip_str + ") >= " + noofchars + " THEN SUBSTRING(" + ip_str + ",1," + noofchars + ") ";
            retStr = retStr + "ELSE SUBSTRING(REPLICATE(" + fillchar + "," + noofchars + "),1," + noofchars + "-LEN(" + ip_str + ")) + " + ip_str + " END";
        }
        return retStr;
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
        final int noOfArguments = arguments.size();
        final Object param1 = arguments.get(0);
        final Object param2 = arguments.get(1);
        Object param3;
        if (noOfArguments > 2) {
            param3 = arguments.get(2);
        }
        else {
            param3 = new String("' '");
        }
        this.setOpenBracesForFunctionNameRequired(false);
        this.setFunctionArguments(new Vector(0));
        this.functionName.setColumnName(this.oracleTOSqlServer(param1.toString(), param2.toString(), param3.toString()));
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
        final SelectColumn sc = new SelectColumn();
        final FunctionCalls replicate = new FunctionCalls();
        final FunctionCalls length = new FunctionCalls();
        final FunctionCalls convert = new FunctionCalls();
        final TableColumn replicateFunction = new TableColumn();
        final TableColumn lengthFunction = new TableColumn();
        final TableColumn convertFunction = new TableColumn();
        replicateFunction.setOwnerName(this.functionName.getOwnerName());
        replicateFunction.setTableName(this.functionName.getTableName());
        replicateFunction.setColumnName("REPLICATE");
        final Vector replicateArg = new Vector();
        replicateArg.add(param3);
        final SelectColumn lengthArgSC = new SelectColumn();
        lengthFunction.setOwnerName(this.functionName.getOwnerName());
        lengthFunction.setTableName(this.functionName.getTableName());
        lengthFunction.setColumnName("LEN");
        final Vector lengthArg = new Vector();
        lengthArg.add(param1);
        length.setFunctionName(lengthFunction);
        length.setFunctionArguments(lengthArg);
        final Vector dummyArg = new Vector();
        dummyArg.add(param2);
        dummyArg.add(" - ");
        dummyArg.add(length);
        lengthArgSC.setColumnExpression(dummyArg);
        replicateArg.add(lengthArgSC);
        replicate.setFunctionName(replicateFunction);
        replicate.setFunctionArguments(replicateArg);
        convertFunction.setOwnerName(this.functionName.getOwnerName());
        convertFunction.setTableName(this.functionName.getTableName());
        convertFunction.setColumnName("CONVERT");
        final Vector convertArg = new Vector();
        convertArg.add("VARCHAR");
        convertArg.add(param1);
        convert.setFunctionName(convertFunction);
        convert.setFunctionArguments(convertArg);
        final Vector colExp = new Vector();
        colExp.add(replicate);
        colExp.add(" + ");
        colExp.add(convert);
        sc.setColumnExpression(colExp);
        final Vector newArg = new Vector();
        newArg.add(sc);
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
        this.functionArguments.setElementAt(substring, 0);
        this.functionArguments.setElementAt(param1, 1);
        this.functionArguments.setSize(2);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionArguments.size() != 3) {
            throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported in PostgreSQL\n Function Arguments Count Mismatch\n");
        }
        this.functionName.setColumnName("LPAD");
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
        this.functionName.setColumnName("LPAD");
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
        this.functionName.setColumnName("LPAD");
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
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LPAD");
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
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe function LPAD is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LPAD");
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
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LPAD");
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
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("LPAD");
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
