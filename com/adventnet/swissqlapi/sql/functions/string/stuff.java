package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class stuff extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPLACE");
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
        final FunctionCalls substr = new FunctionCalls();
        final Vector substrArg = (Vector)this.functionArguments.clone();
        substrArg.setSize(3);
        substr.setFunctionArguments(substrArg);
        final TableColumn substrFunction = new TableColumn();
        substrFunction.setOwnerName(this.functionName.getOwnerName());
        substrFunction.setTableName(this.functionName.getTableName());
        substrFunction.setColumnName("SUBSTR");
        substr.setFunctionName(substrFunction);
        this.functionArguments.setElementAt(substr, 1);
        this.functionArguments.removeElementAt(2);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("STUFF");
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
        this.functionName.setColumnName("STUFF");
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
        this.functionName.setColumnName("REPLACE");
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
        final FunctionCalls substr = new FunctionCalls();
        final Vector substrArg = (Vector)this.functionArguments.clone();
        substrArg.setSize(3);
        substr.setFunctionArguments(substrArg);
        final TableColumn substrFunction = new TableColumn();
        substrFunction.setOwnerName(this.functionName.getOwnerName());
        substrFunction.setTableName(this.functionName.getTableName());
        substrFunction.setColumnName("SUBSTR");
        substr.setFunctionName(substrFunction);
        this.functionArguments.setElementAt(substr, 1);
        this.functionArguments.removeElementAt(2);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final SelectColumn newArgument = new SelectColumn();
        final Vector colExp = new Vector();
        final FunctionCalls substr1 = new FunctionCalls();
        final Vector substr1Arg = (Vector)this.functionArguments.clone();
        substr1Arg.insertElementAt("0", 1);
        substr1Arg.setSize(3);
        final TableColumn substrFunction = new TableColumn();
        substrFunction.setOwnerName(this.functionName.getOwnerName());
        substrFunction.setTableName(this.functionName.getTableName());
        substrFunction.setColumnName("SUBSTR");
        substr1.setFunctionName(substrFunction);
        substr1.setFunctionArguments(substr1Arg);
        final FunctionCalls substr2 = new FunctionCalls();
        final Vector substr2Arg = (Vector)this.functionArguments.clone();
        substr2Arg.setElementAt(this.functionArguments.get(1).toString() + "+" + this.functionArguments.get(2).toString(), 1);
        substr2.setFunctionName(substrFunction);
        substr2.setFunctionArguments(substr2Arg);
        final FunctionCalls length = new FunctionCalls();
        final TableColumn lenFunction = new TableColumn();
        lenFunction.setOwnerName(this.functionName.getOwnerName());
        lenFunction.setTableName(this.functionName.getTableName());
        lenFunction.setColumnName("LENGTH");
        length.setFunctionName(lenFunction);
        final Vector lengthArg = new Vector();
        lengthArg.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArg);
        final String str = length.toString() + " + 1 -" + this.functionArguments.get(2).toString();
        substr2Arg.setElementAt(str, 2);
        substr2Arg.setSize(3);
        colExp.addElement(substr1);
        colExp.addElement("||");
        colExp.addElement(this.functionArguments.get(3));
        colExp.addElement("||");
        colExp.addElement(substr2);
        newArgument.setColumnExpression(colExp);
        this.functionArguments.setElementAt(newArgument, 0);
        this.functionArguments.setSize(1);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPLACE");
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
        final FunctionCalls substr = new FunctionCalls();
        final Vector substrArg = (Vector)this.functionArguments.clone();
        substrArg.setSize(3);
        substr.setFunctionArguments(substrArg);
        final TableColumn substrFunction = new TableColumn();
        substrFunction.setOwnerName(this.functionName.getOwnerName());
        substrFunction.setTableName(this.functionName.getTableName());
        substrFunction.setColumnName("SUBSTRING");
        substr.setFunctionName(substrFunction);
        this.functionArguments.setElementAt(substr, 1);
        this.functionArguments.removeElementAt(2);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final SelectColumn newArgument = new SelectColumn();
        final Vector colExp = new Vector();
        final FunctionCalls substr1 = new FunctionCalls();
        final Vector substr1Arg = (Vector)this.functionArguments.clone();
        substr1Arg.insertElementAt("0", 1);
        substr1Arg.setSize(3);
        final TableColumn substrFunction = new TableColumn();
        substrFunction.setOwnerName(this.functionName.getOwnerName());
        substrFunction.setTableName(this.functionName.getTableName());
        substrFunction.setColumnName("SUBSTR");
        substr1.setFunctionName(substrFunction);
        substr1.setFunctionArguments(substr1Arg);
        final FunctionCalls substr2 = new FunctionCalls();
        final Vector substr2Arg = (Vector)this.functionArguments.clone();
        substr2Arg.setElementAt(this.functionArguments.get(1).toString() + "+" + this.functionArguments.get(2).toString(), 1);
        substr2.setFunctionName(substrFunction);
        substr2.setFunctionArguments(substr2Arg);
        final FunctionCalls length = new FunctionCalls();
        final TableColumn lenFunction = new TableColumn();
        lenFunction.setOwnerName(this.functionName.getOwnerName());
        lenFunction.setTableName(this.functionName.getTableName());
        lenFunction.setColumnName("LENGTH");
        length.setFunctionName(lenFunction);
        final Vector lengthArg = new Vector();
        lengthArg.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArg);
        final String str = length.toString() + " + 1 -" + this.functionArguments.get(2).toString();
        substr2Arg.setElementAt(str, 2);
        substr2Arg.setSize(3);
        colExp.addElement(substr1);
        colExp.addElement("||");
        colExp.addElement(this.functionArguments.get(3));
        colExp.addElement("||");
        colExp.addElement(substr2);
        newArgument.setColumnExpression(colExp);
        this.functionArguments.setElementAt(newArgument, 0);
        this.functionArguments.setSize(1);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        final SelectColumn newArgument = new SelectColumn();
        final Vector colExp = new Vector();
        final FunctionCalls substr1 = new FunctionCalls();
        final Vector substr1Arg = (Vector)this.functionArguments.clone();
        substr1Arg.insertElementAt("0", 1);
        substr1Arg.setSize(3);
        final TableColumn substrFunction = new TableColumn();
        substrFunction.setOwnerName(this.functionName.getOwnerName());
        substrFunction.setTableName(this.functionName.getTableName());
        substrFunction.setColumnName("SUBSTR");
        substr1.setFunctionName(substrFunction);
        substr1.setFunctionArguments(substr1Arg);
        final FunctionCalls substr2 = new FunctionCalls();
        final Vector substr2Arg = (Vector)this.functionArguments.clone();
        substr2Arg.setElementAt(this.functionArguments.get(1).toString() + "+" + this.functionArguments.get(2).toString(), 1);
        substr2.setFunctionName(substrFunction);
        substr2.setFunctionArguments(substr2Arg);
        final FunctionCalls length = new FunctionCalls();
        final TableColumn lenFunction = new TableColumn();
        lenFunction.setOwnerName(this.functionName.getOwnerName());
        lenFunction.setTableName(this.functionName.getTableName());
        lenFunction.setColumnName("CHARACTER_LENGTH");
        length.setFunctionName(lenFunction);
        final Vector lengthArg = new Vector();
        lengthArg.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArg);
        final String str = length.toString() + " + 1 -" + this.functionArguments.get(2).toString();
        substr2Arg.setElementAt(str, 2);
        substr2Arg.setSize(3);
        colExp.addElement(substr1);
        colExp.addElement("||");
        colExp.addElement(this.functionArguments.get(3));
        colExp.addElement("||");
        colExp.addElement(substr2);
        newArgument.setColumnExpression(colExp);
        this.functionArguments.setElementAt(newArgument, 0);
        this.functionArguments.setSize(1);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("REPLACE");
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
        final FunctionCalls substr = new FunctionCalls();
        final Vector substrArg = (Vector)this.functionArguments.clone();
        substrArg.setSize(3);
        substr.setFunctionArguments(substrArg);
        final TableColumn substrFunction = new TableColumn();
        substrFunction.setOwnerName(this.functionName.getOwnerName());
        substrFunction.setTableName(this.functionName.getTableName());
        substrFunction.setColumnName("SUBSTR");
        substr.setFunctionName(substrFunction);
        this.functionArguments.setElementAt(substr, 1);
        this.functionArguments.removeElementAt(2);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        final SelectColumn newArgument = new SelectColumn();
        final Vector colExp = new Vector();
        final FunctionCalls substr1 = new FunctionCalls();
        final Vector substr1Arg = (Vector)this.functionArguments.clone();
        substr1Arg.insertElementAt("0", 1);
        substr1Arg.setSize(3);
        final TableColumn substrFunction = new TableColumn();
        substrFunction.setOwnerName(this.functionName.getOwnerName());
        substrFunction.setTableName(this.functionName.getTableName());
        substrFunction.setColumnName("SUBSTR");
        substr1.setFunctionName(substrFunction);
        substr1.setFunctionArguments(substr1Arg);
        final FunctionCalls substr2 = new FunctionCalls();
        final Vector substr2Arg = (Vector)this.functionArguments.clone();
        substr2Arg.setElementAt(this.functionArguments.get(1).toString() + "+" + this.functionArguments.get(2).toString(), 1);
        substr2.setFunctionName(substrFunction);
        substr2.setFunctionArguments(substr2Arg);
        final FunctionCalls length = new FunctionCalls();
        final TableColumn lenFunction = new TableColumn();
        lenFunction.setOwnerName(this.functionName.getOwnerName());
        lenFunction.setTableName(this.functionName.getTableName());
        lenFunction.setColumnName("LENGTH");
        length.setFunctionName(lenFunction);
        final Vector lengthArg = new Vector();
        lengthArg.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArg);
        final String str = length.toString() + " + 1 -" + this.functionArguments.get(2).toString();
        substr2Arg.setElementAt(str, 2);
        substr2Arg.setSize(3);
        colExp.addElement(substr1);
        colExp.addElement("||");
        colExp.addElement(this.functionArguments.get(3));
        colExp.addElement("||");
        colExp.addElement(substr2);
        newArgument.setColumnExpression(colExp);
        this.functionArguments.setElementAt(newArgument, 0);
        this.functionArguments.setSize(1);
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
    }
}
