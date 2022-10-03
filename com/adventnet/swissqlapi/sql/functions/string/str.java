package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class str extends FunctionCalls
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
        if (this.functionArguments.size() == 2) {
            final FunctionCalls tochar = new FunctionCalls();
            final FunctionCalls ceil = new FunctionCalls();
            final FunctionCalls lpad = new FunctionCalls();
            final TableColumn innerFunction1 = new TableColumn();
            final TableColumn innerFunction2 = new TableColumn();
            final TableColumn innerFunction3 = new TableColumn();
            innerFunction1.setOwnerName(this.functionName.getOwnerName());
            innerFunction1.setTableName(this.functionName.getTableName());
            innerFunction1.setColumnName("ROUND");
            innerFunction2.setOwnerName(this.functionName.getOwnerName());
            innerFunction2.setTableName(this.functionName.getTableName());
            innerFunction2.setColumnName("TO_CHAR");
            innerFunction3.setOwnerName(this.functionName.getOwnerName());
            innerFunction3.setTableName(this.functionName.getTableName());
            innerFunction3.setColumnName("LPAD");
            final Vector tocharArg = new Vector();
            final Vector ceilArg = new Vector();
            final Vector lpadArg = new Vector();
            ceil.setFunctionName(innerFunction1);
            tochar.setFunctionName(innerFunction2);
            lpad.setFunctionName(innerFunction3);
            ceilArg.add(this.functionArguments.get(0));
            ceil.setFunctionArguments(ceilArg);
            tocharArg.add(ceil);
            tochar.setFunctionArguments(tocharArg);
            lpadArg.add(tochar);
            lpadArg.add(this.functionArguments.get(1));
            lpad.setFunctionArguments(lpadArg);
            this.functionArguments.setElementAt(lpad, 0);
            final Object obj = this.functionArguments.get(1);
            this.functionArguments.setElementAt("1", 1);
            this.functionArguments.add(obj);
        }
        if (this.functionArguments.size() == 3) {
            final FunctionCalls tochar = new FunctionCalls();
            final FunctionCalls ceil = new FunctionCalls();
            final FunctionCalls lpad = new FunctionCalls();
            final TableColumn innerFunction1 = new TableColumn();
            final TableColumn innerFunction2 = new TableColumn();
            final TableColumn innerFunction3 = new TableColumn();
            innerFunction1.setOwnerName(this.functionName.getOwnerName());
            innerFunction1.setTableName(this.functionName.getTableName());
            innerFunction1.setColumnName("ROUND");
            innerFunction2.setOwnerName(this.functionName.getOwnerName());
            innerFunction2.setTableName(this.functionName.getTableName());
            innerFunction2.setColumnName("TO_CHAR");
            innerFunction3.setOwnerName(this.functionName.getOwnerName());
            innerFunction3.setTableName(this.functionName.getTableName());
            innerFunction3.setColumnName("LPAD");
            final Vector tocharArg = new Vector();
            final Vector ceilArg = new Vector();
            final Vector lpadArg = new Vector();
            ceil.setFunctionName(innerFunction1);
            tochar.setFunctionName(innerFunction2);
            lpad.setFunctionName(innerFunction3);
            ceilArg.add(this.functionArguments.get(0));
            ceilArg.add(this.functionArguments.remove(2));
            ceil.setFunctionArguments(ceilArg);
            tocharArg.add(ceil);
            tochar.setFunctionArguments(tocharArg);
            lpadArg.add(tochar);
            lpadArg.add(this.functionArguments.get(1));
            lpad.setFunctionArguments(lpadArg);
            this.functionArguments.setElementAt(lpad, 0);
            final Object obj = this.functionArguments.get(1);
            this.functionArguments.setElementAt("1", 1);
            this.functionArguments.add(obj);
        }
        if (this.functionArguments.size() == 1) {
            final FunctionCalls tochar = new FunctionCalls();
            final FunctionCalls round = new FunctionCalls();
            final FunctionCalls lpad = new FunctionCalls();
            final FunctionCalls len = new FunctionCalls();
            final TableColumn innerFunction4 = new TableColumn();
            final TableColumn innerFunction5 = new TableColumn();
            final TableColumn innerFunction6 = new TableColumn();
            final TableColumn lenFunction3 = new TableColumn();
            innerFunction4.setOwnerName(this.functionName.getOwnerName());
            innerFunction4.setTableName(this.functionName.getTableName());
            innerFunction4.setColumnName("ROUND");
            innerFunction5.setOwnerName(this.functionName.getOwnerName());
            innerFunction5.setTableName(this.functionName.getTableName());
            innerFunction5.setColumnName("TO_CHAR");
            innerFunction6.setOwnerName(this.functionName.getOwnerName());
            innerFunction6.setTableName(this.functionName.getTableName());
            innerFunction6.setColumnName("LPAD");
            lenFunction3.setOwnerName(this.functionName.getOwnerName());
            lenFunction3.setTableName(this.functionName.getTableName());
            lenFunction3.setColumnName("LENGTH");
            final Vector tocharArg2 = new Vector();
            final Vector roundArg = new Vector();
            final Vector lpadArg2 = new Vector();
            final Vector lenArg = new Vector();
            round.setFunctionName(innerFunction4);
            tochar.setFunctionName(innerFunction5);
            lpad.setFunctionName(innerFunction6);
            len.setFunctionName(lenFunction3);
            roundArg.add(this.functionArguments.get(0));
            round.setFunctionArguments(roundArg);
            tocharArg2.add(round);
            tochar.setFunctionArguments(tocharArg2);
            lpadArg2.add(tochar);
            lpadArg2.add(new String("10"));
            lpad.setFunctionArguments(lpadArg2);
            lenArg.add(this.functionArguments.get(0));
            len.setFunctionArguments(lenArg);
            this.functionArguments.setElementAt(lpad, 0);
            this.functionArguments.add(new String("1"));
            this.functionArguments.add(new String("10"));
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("STR");
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
        this.functionName.setColumnName("STR");
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
        this.functionName.setColumnName("STR");
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
        this.functionName.setColumnName("STR");
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
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("STR");
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
        this.functionName.setColumnName("STR");
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
        this.functionName.setColumnName("STR");
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
        if (this.functionArguments.size() == 2) {
            final FunctionCalls tochar = new FunctionCalls();
            final FunctionCalls ceil = new FunctionCalls();
            final FunctionCalls lpad = new FunctionCalls();
            final TableColumn innerFunction1 = new TableColumn();
            final TableColumn innerFunction2 = new TableColumn();
            final TableColumn innerFunction3 = new TableColumn();
            innerFunction1.setOwnerName(this.functionName.getOwnerName());
            innerFunction1.setTableName(this.functionName.getTableName());
            innerFunction1.setColumnName("ROUND");
            innerFunction2.setOwnerName(this.functionName.getOwnerName());
            innerFunction2.setTableName(this.functionName.getTableName());
            innerFunction2.setColumnName("TO_CHAR");
            innerFunction3.setOwnerName(this.functionName.getOwnerName());
            innerFunction3.setTableName(this.functionName.getTableName());
            innerFunction3.setColumnName("LPAD");
            final Vector tocharArg = new Vector();
            final Vector ceilArg = new Vector();
            final Vector lpadArg = new Vector();
            ceil.setFunctionName(innerFunction1);
            tochar.setFunctionName(innerFunction2);
            lpad.setFunctionName(innerFunction3);
            ceilArg.add(this.functionArguments.get(0));
            ceil.setFunctionArguments(ceilArg);
            tocharArg.add(ceil);
            tochar.setFunctionArguments(tocharArg);
            lpadArg.add(tochar);
            lpadArg.add(this.functionArguments.get(1));
            lpad.setFunctionArguments(lpadArg);
            this.functionArguments.setElementAt(lpad, 0);
            final Object obj = this.functionArguments.get(1);
            this.functionArguments.setElementAt("1", 1);
            this.functionArguments.add(obj);
        }
        if (this.functionArguments.size() == 1) {
            final FunctionCalls tochar = new FunctionCalls();
            final FunctionCalls ceil = new FunctionCalls();
            final FunctionCalls lpad = new FunctionCalls();
            final FunctionCalls len = new FunctionCalls();
            final TableColumn innerFunction4 = new TableColumn();
            final TableColumn innerFunction5 = new TableColumn();
            final TableColumn innerFunction6 = new TableColumn();
            final TableColumn lenFunction3 = new TableColumn();
            innerFunction4.setOwnerName(this.functionName.getOwnerName());
            innerFunction4.setTableName(this.functionName.getTableName());
            innerFunction4.setColumnName("ROUND");
            innerFunction5.setOwnerName(this.functionName.getOwnerName());
            innerFunction5.setTableName(this.functionName.getTableName());
            innerFunction5.setColumnName("TO_CHAR");
            innerFunction6.setOwnerName(this.functionName.getOwnerName());
            innerFunction6.setTableName(this.functionName.getTableName());
            innerFunction6.setColumnName("LPAD");
            lenFunction3.setOwnerName(this.functionName.getOwnerName());
            lenFunction3.setTableName(this.functionName.getTableName());
            lenFunction3.setColumnName("LENGTH");
            final Vector tocharArg2 = new Vector();
            final Vector ceilArg2 = new Vector();
            final Vector lpadArg2 = new Vector();
            final Vector lenArg = new Vector();
            ceil.setFunctionName(innerFunction4);
            tochar.setFunctionName(innerFunction5);
            lpad.setFunctionName(innerFunction6);
            len.setFunctionName(lenFunction3);
            ceilArg2.add(this.functionArguments.get(0));
            ceil.setFunctionArguments(ceilArg2);
            tocharArg2.add(ceil);
            tochar.setFunctionArguments(tocharArg2);
            lpadArg2.add(tochar);
            lpadArg2.add(new String("10"));
            lpad.setFunctionArguments(lpadArg2);
            lenArg.add(this.functionArguments.get(0));
            len.setFunctionArguments(lenArg);
            this.functionArguments.setElementAt(lpad, 0);
            this.functionArguments.add(new String("1"));
            this.functionArguments.add(new String("10"));
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("STR");
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
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("STR");
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
}
