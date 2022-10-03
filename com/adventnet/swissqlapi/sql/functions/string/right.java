package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class right extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUBSTR");
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
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final TableColumn outerFunction = new TableColumn();
        outerFunction.setColumnName("LENGTH");
        length.setFunctionName(outerFunction);
        final Vector lenArgument = new Vector();
        lenArgument.add(this.functionArguments.get(0));
        length.setFunctionArguments(lenArgument);
        colExpArg2.addElement(length);
        colExpArg2.addElement("-");
        colExpArg2.addElement(this.functionArguments.get(1));
        colExpArg2.addElement("+1");
        arg2.setColumnExpression(colExpArg2);
        arguments.add(1, arg2);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RIGHT");
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
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RIGHT");
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
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RIGHT");
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
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String functionNameStr = "RIGHT";
        Integer number = null;
        if (from_sqs != null && from_sqs.canUseUDFFunctionsForText()) {
            functionNameStr = "RIGHT_UDF";
        }
        this.functionName.setColumnName(functionNameStr);
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count == 0) {
                    sc.convertSelectColumnToTextDataType();
                }
                String arg = sc.toPostgreSQLSelect(to_sqs, from_sqs).toString();
                if (i_count == 1) {
                    number = StringFunctions.getIntegerValue(arg);
                    if (number != null) {
                        arg = number.toString();
                    }
                    else if (from_sqs != null && from_sqs.canUseUDFFunctionsForText() && from_sqs.canUseUDFFunctionsForNumeric()) {
                        arg = "TOINTEGER_UDF(" + arg + ")";
                    }
                }
                arguments.addElement(arg);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionArguments.size() == 2 && from_sqs != null && (from_sqs.isAmazonRedShift() || !from_sqs.canUseUDFFunctionsForText())) {
            String qry = "(case when " + arguments.get(1) + " < 0 then NULL else right(" + arguments.get(0) + "," + arguments.get(1) + ") end)";
            if (number != null) {
                if (number > 0) {
                    qry = "RIGHT(" + arguments.get(0) + "," + number.toString() + ")";
                }
                else {
                    qry = "CAST(NULL AS TEXT)";
                }
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RIGHT");
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
        this.functionName.setColumnName("SUBSTR");
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
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final TableColumn outerFunction = new TableColumn();
        outerFunction.setOwnerName(this.functionName.getOwnerName());
        outerFunction.setTableName(this.functionName.getTableName());
        outerFunction.setColumnName("LENGTH");
        length.setFunctionName(outerFunction);
        final Vector lenArgument = new Vector();
        lenArgument.add(this.functionArguments.get(0));
        length.setFunctionArguments(lenArgument);
        colExpArg2.addElement(length);
        colExpArg2.addElement("-");
        colExpArg2.addElement(this.functionArguments.get(1));
        colExpArg2.addElement("+1");
        arg2.setColumnExpression(colExpArg2);
        this.functionArguments.insertElementAt(arg2, 1);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUBSTR");
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
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final TableColumn outerFunction = new TableColumn();
        outerFunction.setOwnerName(this.functionName.getOwnerName());
        outerFunction.setTableName(this.functionName.getTableName());
        outerFunction.setColumnName("CHARACTER_LENGTH");
        length.setFunctionName(outerFunction);
        final Vector lenArgument = new Vector();
        lenArgument.add(this.functionArguments.get(0));
        length.setFunctionArguments(lenArgument);
        colExpArg2.addElement(length);
        colExpArg2.addElement("-");
        colExpArg2.addElement(this.functionArguments.get(1));
        colExpArg2.addElement("+1");
        arg2.setColumnExpression(colExpArg2);
        this.functionArguments.insertElementAt(arg2, 1);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUBSTR");
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
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final FunctionCalls sign = new FunctionCalls();
        final FunctionCalls decode = new FunctionCalls();
        final TableColumn outerFunction = new TableColumn();
        final TableColumn signOuterFunction = new TableColumn();
        final TableColumn decodeOuterFunction = new TableColumn();
        decodeOuterFunction.setOwnerName(this.functionName.getOwnerName());
        decodeOuterFunction.setTableName(this.functionName.getTableName());
        outerFunction.setColumnName("LENGTH");
        length.setFunctionName(outerFunction);
        signOuterFunction.setColumnName("SIGN");
        sign.setFunctionName(signOuterFunction);
        decodeOuterFunction.setColumnName("DECODE");
        decode.setFunctionName(decodeOuterFunction);
        final Vector lenArgument = new Vector();
        final Vector signArgument = new Vector();
        final Vector decodeArgument = new Vector();
        lenArgument.add(this.functionArguments.get(0));
        length.setFunctionArguments(lenArgument);
        colExpArg2.addElement(length);
        colExpArg2.addElement("-");
        colExpArg2.addElement(this.functionArguments.get(1));
        colExpArg2.addElement("+1");
        arg2.setColumnExpression(colExpArg2);
        signArgument.addElement(arg2);
        sign.setFunctionArguments(signArgument);
        decodeArgument.addElement(sign);
        decodeArgument.addElement("-1");
        decodeArgument.addElement("0");
        decodeArgument.addElement(arg2);
        decode.setFunctionArguments(decodeArgument);
        this.functionArguments.insertElementAt(decode, 1);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("SUBSTR");
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
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final TableColumn outerFunction = new TableColumn();
        outerFunction.setOwnerName(this.functionName.getOwnerName());
        outerFunction.setTableName(this.functionName.getTableName());
        outerFunction.setColumnName("LENGTH");
        length.setFunctionName(outerFunction);
        final Vector lenArgument = new Vector();
        lenArgument.add(this.functionArguments.get(0));
        length.setFunctionArguments(lenArgument);
        colExpArg2.addElement(length);
        colExpArg2.addElement("-");
        colExpArg2.addElement(this.functionArguments.get(1));
        colExpArg2.addElement("+1");
        arg2.setColumnExpression(colExpArg2);
        this.functionArguments.insertElementAt(arg2, 1);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("RIGHT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count == 0) {
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
