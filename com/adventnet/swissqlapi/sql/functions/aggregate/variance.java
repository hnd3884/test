package com.adventnet.swissqlapi.sql.functions.aggregate;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class variance extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("VAR")) {
            this.functionName.setColumnName("VARIANCE");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("VARP")) {
            this.functionName.setColumnName("VAR_POP");
        }
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
        if (this.functionName.getColumnName().equalsIgnoreCase("VARP") || this.functionName.getColumnName().equalsIgnoreCase("VAR_POP") || this.functionName.getColumnName().equalsIgnoreCase("VARIANCE")) {
            this.functionName.setColumnName("VARP");
        }
        else {
            this.functionName.setColumnName("VAR");
        }
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
        this.functionName.setColumnName("VARIANCE");
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
        if (this.functionName.getColumnName().equalsIgnoreCase("VAR")) {
            this.functionName.setColumnName("VAR");
        }
        else {
            this.functionName.setColumnName("VARIANCE");
        }
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
        if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
            this.functionName.setColumnName("VAR_SAMP");
        }
        else {
            this.functionName.setColumnName("VAR_POP");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selColumn = this.functionArguments.elementAt(i_count);
                selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(this.functionArguments.size());
                arguments.addElement(selColumn.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
            this.functionName.setColumnName("VAR_SAMP");
        }
        else {
            this.functionName.setColumnName("VARIANCE");
        }
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
        this.functionName.setColumnName("VARIANCE");
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
        this.functionName.setColumnName("VARIANCE");
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
        String firstArg = null;
        String secondArg = null;
        SelectColumn firstArgSelCol = null;
        SelectColumn secondArgSelCol = null;
        if (this.functionName.getColumnName().equalsIgnoreCase("VAR")) {
            this.functionName.setColumnName("VARIANCE");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("VARP")) {
            this.functionName.setColumnName("VAR_POP");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selCol = this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs);
                arguments.addElement(selCol);
                if (i_count == 0) {
                    firstArgSelCol = selCol;
                    firstArg = selCol.toString();
                }
                else if (i_count == 1) {
                    secondArgSelCol = selCol;
                    secondArg = selCol.toString();
                }
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
                if (i_count == 0) {
                    firstArg = this.functionArguments.elementAt(i_count).toString();
                }
                else if (i_count == 1) {
                    secondArg = this.functionArguments.elementAt(i_count).toString();
                }
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionName.getColumnName().equalsIgnoreCase("CORR") || this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
            String target = "";
            if (this.functionName.getColumnName().equalsIgnoreCase("CORR")) {
                target = "((sum(" + firstArg + "*" + secondArg + ")/count(" + firstArg + ")) - (( sum(" + firstArg + ")*sum(" + secondArg + "))/(count(" + firstArg + ")**2)))/(sqrt(var_pop(" + firstArg + ")*var_pop(" + secondArg + ")))  ";
            }
            else if (this.functionName.getColumnName().equalsIgnoreCase("COVAR_POP")) {
                target = "(sum(" + firstArg + "*" + secondArg + ")/count(" + firstArg + ")) - (( sum(" + firstArg + ")*sum(" + secondArg + "))/(count(" + firstArg + ")**2))";
            }
            final Vector targetArg = new Vector();
            targetArg.add(target);
            this.setFunctionArguments(targetArg);
            this.setOver(null);
            this.setOrderBy(null);
            this.setPartitionBy(null);
            this.setWindowingClause(null);
            this.functionName.setColumnName("");
            final TableColumn firstArgTableCol = (TableColumn)this.getTableColumnFromSelectColumn(firstArgSelCol);
            final TableColumn secondArgTableCol = (TableColumn)this.getTableColumnFromSelectColumn(secondArgSelCol);
            final WhereExpression whereExp = this.createWhereExpression(firstArgTableCol, secondArgTableCol);
            final WhereExpression existingWhereExpr = from_sqs.getWhereExpression();
            if (existingWhereExpr != null && firstArgTableCol != null && secondArgTableCol != null) {
                boolean setWhereExp = true;
                final Vector functionColumnVector = existingWhereExpr.getFunctionColumnVector();
                final Vector newTableColVector = new Vector();
                for (int functionColumnVectorSize = functionColumnVector.size(), i = 0; i < functionColumnVectorSize; ++i) {
                    final TableColumn funcTableCol = functionColumnVector.get(i);
                    if (funcTableCol.toString().equalsIgnoreCase(firstArgTableCol.toString()) || funcTableCol.toString().equalsIgnoreCase(secondArgTableCol.toString())) {
                        setWhereExp = false;
                    }
                }
                if (setWhereExp) {
                    existingWhereExpr.addOperator("AND");
                    existingWhereExpr.addWhereExpression(whereExp);
                    newTableColVector.add(firstArgTableCol);
                    newTableColVector.add(secondArgTableCol);
                }
                if (newTableColVector.size() > 0) {
                    functionColumnVector.add(newTableColVector.get(0));
                    functionColumnVector.add(newTableColVector.get(1));
                    newTableColVector.clear();
                }
            }
            else {
                from_sqs.setWhereExpression(whereExp);
            }
            this.functionName.setColumnName("");
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("VAR_POP");
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
    
    private WhereExpression createWhereExpression(final TableColumn firstCol, final TableColumn secondCol) {
        final WhereExpression whereExp = new WhereExpression();
        final WhereItem firstArgItem = new WhereItem();
        final WhereColumn firstArgColumn = new WhereColumn();
        final Vector firstArgColExp = new Vector();
        final TableColumn firstArgTableCol = firstCol;
        firstArgColExp.add(firstArgTableCol);
        firstArgColumn.setColumnExpression(firstArgColExp);
        firstArgItem.setLeftWhereExp(firstArgColumn);
        firstArgItem.setOperator("IS NOT NULL");
        firstArgItem.setRightWhereExp(null);
        final WhereItem secondArgItem = new WhereItem();
        final WhereColumn secondArgColumn = new WhereColumn();
        final Vector secondArgColExp = new Vector();
        final TableColumn secondArgTableCol = secondCol;
        secondArgColExp.add(secondArgTableCol);
        secondArgColumn.setColumnExpression(secondArgColExp);
        secondArgItem.setLeftWhereExp(secondArgColumn);
        secondArgItem.setOperator("IS NOT NULL");
        secondArgItem.setRightWhereExp(null);
        whereExp.addWhereItem(firstArgItem);
        whereExp.addOperator("AND");
        whereExp.addWhereItem(secondArgItem);
        return whereExp;
    }
    
    private Object getTableColumnFromSelectColumn(final SelectColumn selCol) {
        final Vector columnExpression = selCol.getColumnExpression();
        for (int colExpSize = columnExpression.size(), i = 0; i < colExpSize; ++i) {
            if (columnExpression.elementAt(i) instanceof TableColumn) {
                return columnExpression.elementAt(i);
            }
            if (columnExpression.elementAt(i) instanceof FunctionCalls) {
                final FunctionCalls funcCall = columnExpression.elementAt(i);
                final Vector funcArgs = funcCall.getFunctionArguments();
                for (int funcArgsSize = funcArgs.size(), i_count = 0; i_count < funcArgsSize; ++i_count) {
                    if (funcArgs.elementAt(i_count) instanceof SelectColumn) {
                        return this.getTableColumnFromSelectColumn(funcArgs.elementAt(i_count));
                    }
                }
            }
            else if (columnExpression.elementAt(i) instanceof SelectColumn) {
                return this.getTableColumnFromSelectColumn(columnExpression.elementAt(i));
            }
        }
        return null;
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("VAR_SAMP")) {
            this.functionName.setColumnName("VAR_SAMP");
        }
        else {
            this.functionName.setColumnName("VAR_POP");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selColumn = this.functionArguments.elementAt(i_count);
                selColumn.convertWhereExpAloneInsideFunctionTo_IF_Function(this.functionArguments.size());
                arguments.addElement(selColumn.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
}
