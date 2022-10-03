package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class concat extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
            this.functionName.setColumnName("");
            final Vector arguments = new Vector();
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i).toOracleSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i));
                }
            }
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            final Object separator = arguments.get(0);
            for (int j = 1; j < arguments.size(); ++j) {
                if (j == arguments.size() - 1) {
                    if (arguments.get(j) instanceof SelectColumn) {
                        final SelectColumn argSC = arguments.get(j);
                        if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                            final String argStr = argSC.getColumnExpression().get(0).toString();
                            if (argStr.equalsIgnoreCase("null")) {
                                final int columnExpSize = columnExp.size();
                                columnExp.remove(columnExpSize - 1);
                                columnExp.remove(columnExpSize - 2);
                                columnExp.remove(columnExpSize - 3);
                                break;
                            }
                        }
                    }
                    columnExp.add(arguments.get(j));
                    break;
                }
                if (arguments.get(j) instanceof SelectColumn) {
                    final SelectColumn argSC = arguments.get(j);
                    if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                        final String argStr = argSC.getColumnExpression().get(0).toString();
                        if (argStr.equalsIgnoreCase("null")) {
                            continue;
                        }
                    }
                }
                columnExp.add(arguments.get(j));
                columnExp.add(new String("||"));
                columnExp.add(separator);
                columnExp.add(new String("||"));
            }
            arg.setColumnExpression(columnExp);
            this.functionArguments.clear();
            this.functionArguments.add(arg);
        }
        else {
            this.functionName.setColumnName("CONCAT");
            final Vector arguments = new Vector();
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i).toOracleSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i));
                }
            }
            this.setFunctionArguments(arguments);
            if (this.functionArguments.size() > 2) {
                this.functionName.setColumnName("");
                final SelectColumn arg = new SelectColumn();
                final Vector columnExp = new Vector();
                columnExp.add(this.functionArguments.get(0));
                for (int k = 0; k < this.functionArguments.size() - 1; ++k) {
                    columnExp.add("||");
                    columnExp.add(this.functionArguments.get(k + 1));
                    arg.setColumnExpression(columnExp);
                    this.functionArguments.setElementAt(arg, k);
                }
                this.functionArguments.setSize(1);
            }
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
            this.functionName.setColumnName("");
            final Vector arguments = new Vector();
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i));
                }
            }
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            final Object separator = arguments.get(0);
            for (int j = 1; j < arguments.size(); ++j) {
                if (j == arguments.size() - 1) {
                    if (arguments.get(j) instanceof SelectColumn) {
                        final SelectColumn argSC = arguments.get(j);
                        if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                            final String argStr = argSC.getColumnExpression().get(0).toString();
                            if (argStr.equalsIgnoreCase("null")) {
                                final int columnExpSize = columnExp.size();
                                columnExp.remove(columnExpSize - 1);
                                columnExp.remove(columnExpSize - 2);
                                columnExp.remove(columnExpSize - 3);
                                break;
                            }
                        }
                    }
                    columnExp.add(arguments.get(j));
                    break;
                }
                if (arguments.get(j) instanceof SelectColumn) {
                    final SelectColumn argSC = arguments.get(j);
                    if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                        final String argStr = argSC.getColumnExpression().get(0).toString();
                        if (argStr.equalsIgnoreCase("null")) {
                            continue;
                        }
                    }
                }
                columnExp.add(arguments.get(j));
                columnExp.add(new String("+"));
                columnExp.add(separator);
                columnExp.add(new String("+"));
            }
            arg.setColumnExpression(columnExp);
            this.functionArguments.clear();
            this.functionArguments.add(arg);
        }
        else {
            final Vector arguments = new Vector();
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i));
                }
            }
            this.setFunctionArguments(arguments);
            if (!from_sqs.isMSAzure()) {
                this.functionName.setColumnName("");
                if (this.functionArguments.size() == 2) {
                    final SelectColumn arg = new SelectColumn();
                    final Vector columnExp = new Vector();
                    columnExp.add(this.functionArguments.get(0));
                    columnExp.add(new String("+"));
                    columnExp.add(this.functionArguments.get(1));
                    arg.setColumnExpression(columnExp);
                    this.functionArguments.setElementAt(arg, 0);
                    this.functionArguments.setSize(1);
                }
                else if (this.functionArguments.size() > 2) {
                    final SelectColumn arg = new SelectColumn();
                    final Vector columnExp = new Vector();
                    columnExp.add(this.functionArguments.get(0));
                    for (int k = 0; k < this.functionArguments.size() - 1; ++k) {
                        columnExp.add(new String("+"));
                        columnExp.add(this.functionArguments.get(k + 1));
                        arg.setColumnExpression(columnExp);
                        this.functionArguments.setElementAt(arg, k);
                    }
                    this.functionArguments.setSize(1);
                }
            }
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionArguments.size() == 2) {
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));
            columnExp.add(new String("+"));
            columnExp.add(this.functionArguments.get(1));
            arg.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(arg, 0);
            this.functionArguments.setSize(1);
        }
        else if (this.functionArguments.size() > 2) {
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));
            for (int j = 0; j < this.functionArguments.size() - 1; ++j) {
                columnExp.add(new String("+"));
                columnExp.add(this.functionArguments.get(j + 1));
                arg.setColumnExpression(columnExp);
                this.functionArguments.setElementAt(arg, j);
            }
            this.functionArguments.setSize(1);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONCAT");
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
        if (this.functionArguments.size() > 2) {
            this.functionName.setColumnName("");
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));
            for (int j = 0; j < this.functionArguments.size() - 1; ++j) {
                columnExp.add(new String("||"));
                columnExp.add(this.functionArguments.get(j + 1));
                arg.setColumnExpression(columnExp);
                this.functionArguments.setElementAt(arg, j);
            }
            this.functionArguments.setSize(1);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    final SelectColumn selCol = this.functionArguments.elementAt(i);
                    if (from_sqs != null && !from_sqs.isAmazonRedShift() && selCol.getColumnExpression() != null && selCol.getColumnExpression().size() == 1 && selCol.getColumnExpression().get(0) instanceof String) {
                        String value = selCol.getColumnExpression().get(0).toString().trim();
                        if (value.startsWith("'") && value.endsWith("'") && value.contains("\\")) {
                            value = "E" + value;
                            selCol.getColumnExpression().set(0, value);
                        }
                    }
                    selCol.convertSelectColumnToTextDataType();
                    arguments.addElement(selCol.toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i));
                }
            }
            this.setFunctionArguments(arguments);
            this.functionName.setColumnName("CONCAT_WS");
        }
        else {
            this.functionName.setColumnName("");
            for (int j = 0; j < this.functionArguments.size(); ++j) {
                if (this.functionArguments.elementAt(j) instanceof SelectColumn) {
                    final SelectColumn selCol = this.functionArguments.elementAt(j);
                    if (from_sqs != null && !from_sqs.isAmazonRedShift() && selCol.getColumnExpression() != null && selCol.getColumnExpression().size() == 1 && selCol.getColumnExpression().get(0) instanceof String) {
                        String value = selCol.getColumnExpression().get(0).toString().trim();
                        if (value.startsWith("'") && value.endsWith("'") && value.contains("\\")) {
                            value = "E" + value;
                            selCol.getColumnExpression().set(0, value);
                        }
                    }
                    selCol.convertSelectColumnToTextDataType();
                    arguments.addElement(selCol.toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement("CAST(" + this.functionArguments.elementAt(j) + " AS TEXT)");
                }
            }
            this.setFunctionArguments(arguments);
            if (from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForText()) {
                this.functionName.setColumnName("STRING_CONCAT");
            }
            else if (this.functionArguments.size() >= 2) {
                final SelectColumn arg = new SelectColumn();
                final Vector columnExp = new Vector();
                columnExp.add(this.functionArguments.get(0));
                for (int k = 0; k < this.functionArguments.size() - 1; ++k) {
                    columnExp.add(new String("||"));
                    columnExp.add(this.functionArguments.get(k + 1));
                    arg.setColumnExpression(columnExp);
                    this.functionArguments.setElementAt(arg, k);
                }
                this.functionArguments.setSize(1);
            }
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
            this.functionName.setColumnName("CONCAT_WS");
        }
        else {
            this.functionName.setColumnName("CONCAT");
            this.modifyFunctionArguments(from_sqs);
        }
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
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() >= 2) {
            this.functionName.setColumnName("");
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));
            for (int j = 0; j < this.functionArguments.size() - 1; ++j) {
                columnExp.add("||");
                columnExp.add(this.functionArguments.get(j + 1));
                arg.setColumnExpression(columnExp);
                this.functionArguments.setElementAt(arg, j);
            }
            this.functionArguments.setSize(1);
        }
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONCAT");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toInformixSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionArguments.size() > 2) {
            this.functionName.setColumnName("");
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));
            for (int j = 0; j < this.functionArguments.size() - 1; ++j) {
                columnExp.add(new String("||"));
                columnExp.add(this.functionArguments.get(j + 1));
                arg.setColumnExpression(columnExp);
                this.functionArguments.setElementAt(arg, j);
            }
            this.functionArguments.setSize(1);
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() >= 2) {
            this.functionName.setColumnName("");
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));
            for (int j = 0; j < this.functionArguments.size() - 1; ++j) {
                columnExp.add(new String("||"));
                columnExp.add(this.functionArguments.get(j + 1));
                arg.setColumnExpression(columnExp);
                this.functionArguments.setElementAt(arg, j);
            }
            this.functionArguments.setSize(1);
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        if (this.functionArguments.size() >= 2) {
            this.functionName.setColumnName("");
            final SelectColumn arg = new SelectColumn();
            final Vector columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));
            for (int j = 0; j < this.functionArguments.size() - 1; ++j) {
                columnExp.add("||");
                columnExp.add(this.functionArguments.get(j + 1));
                arg.setColumnExpression(columnExp);
                this.functionArguments.setElementAt(arg, j);
            }
            this.functionArguments.setSize(1);
        }
    }
    
    private void modifyFunctionArguments(final SelectQueryStatement from_sqs) {
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.get(i) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.get(i);
                final Vector newColumnExpr = new Vector();
                final Vector v = sc.getColumnExpression();
                if (v != null && v.size() == 1 && v.get(0) instanceof TableColumn) {
                    final TableColumn tc = v.get(0);
                    final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                    if (dataType != null && !dataType.toLowerCase().startsWith("char") && !dataType.toLowerCase().startsWith("varchar")) {
                        final SelectColumn newSC = new SelectColumn();
                        final FunctionCalls castFunction = new FunctionCalls();
                        final TableColumn tc2 = new TableColumn();
                        final CharacterClass charClass = new CharacterClass();
                        tc2.setColumnName("CAST");
                        castFunction.setFunctionName(tc2);
                        castFunction.setAsDatatype("AS");
                        charClass.setDatatypeName("CHAR");
                        final Vector newFunctionArgs = new Vector();
                        newFunctionArgs.add(0, sc);
                        newFunctionArgs.add(1, charClass);
                        castFunction.setFunctionArguments(newFunctionArgs);
                        newColumnExpr.add(0, castFunction);
                        newSC.setColumnExpression(newColumnExpr);
                        this.functionArguments.set(i, newSC);
                    }
                }
            }
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
            Object separator;
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(0);
                sc.convertSelectColumnToTextDataType();
                separator = sc.toVectorWiseSelect(to_sqs, from_sqs);
            }
            else {
                separator = "CAST(" + this.functionArguments.elementAt(0).toString() + " AS VARCHAR)";
            }
            for (int i = 1; i < this.functionArguments.size(); ++i) {
                if (i > 1) {
                    arguments.addElement(separator);
                }
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    final SelectColumn sc2 = this.functionArguments.elementAt(i);
                    sc2.convertSelectColumnToTextDataType();
                    arguments.addElement(sc2.toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement("CAST(" + this.functionArguments.elementAt(i) + " AS VARCHAR)");
                }
            }
        }
        else {
            for (int j = 0; j < this.functionArguments.size(); ++j) {
                if (this.functionArguments.elementAt(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.functionArguments.elementAt(j);
                    sc.convertSelectColumnToTextDataType();
                    arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(j));
                }
            }
        }
        this.functionName.setColumnName("CONCAT");
        this.setFunctionArguments(arguments);
    }
}
