package com.adventnet.swissqlapi.sql.functions.analytic;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class RowNumber extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        if (this.obs != null) {
            this.setOrderBy(this.obs.toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.getPartitionByClause() != null) {
            this.setPartitionByClause(this.getPartitionByClause().toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.getWindowingClause() != null) {
            this.setWindowingClause(this.getWindowingClause().toMySQL(to_sqs, from_sqs));
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
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
        if (this.obs != null) {
            this.setOrderBy(this.obs.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.getPartitionByClause() != null) {
            this.setPartitionByClause(this.getPartitionByClause().toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.getWindowingClause() != null) {
            this.setWindowingClause(this.getWindowingClause().toPostgreSQL(to_sqs, from_sqs));
        }
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
        this.setFunctionArguments(arguments);
        if (this.obs != null) {
            this.setOrderBy(this.obs.toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.getPartitionByClause() != null) {
            this.setPartitionByClause(this.getPartitionByClause().toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.getWindowingClause() != null) {
            this.setWindowingClause(this.getWindowingClause().toVectorWise(to_sqs, from_sqs));
        }
    }
}
