package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class chr extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int numArgs = this.functionArguments.size();
        final Vector arguments = new Vector();
        if (this.functionName.getColumnName().equalsIgnoreCase("char") && numArgs == 2) {
            this.functionName.setColumnName("TO_CHAR");
            final Object arg1 = this.functionArguments.elementAt(0);
            if (arg1 instanceof SelectColumn) {
                arguments.addElement(((SelectColumn)arg1).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(arg1);
            }
            final Object arg2 = this.functionArguments.elementAt(1);
            if (arg2 instanceof SelectColumn) {
                final Vector colExpr = ((SelectColumn)arg2).getColumnExpression();
                final Object arg2Data = colExpr.get(0);
                if (arg2Data instanceof TableColumn) {
                    final String arg2Str = ((TableColumn)arg2Data).getColumnName();
                    if (arg2Str.equalsIgnoreCase("ISO") || arg2Str.equalsIgnoreCase("JIS")) {
                        colExpr.setElementAt("'YYYY-MM-DD'", 0);
                    }
                    else if (arg2Str.equalsIgnoreCase("EUR")) {
                        colExpr.setElementAt("'DD.MM.YYYY'", 0);
                    }
                    else if (arg2Str.equalsIgnoreCase("USA")) {
                        colExpr.setElementAt("'MM/DD/YYYY'", 0);
                    }
                    else if (arg2Str.equalsIgnoreCase("LOCAL")) {
                        colExpr.setElementAt("'MM-DD-YYYY'", 0);
                    }
                    arguments.addElement(arg2);
                }
                else {
                    arguments.addElement(((SelectColumn)arg2).toOracleSelect(to_sqs, from_sqs));
                }
            }
            else {
                arguments.addElement(arg2);
            }
        }
        else {
            if (SwisSQLOptions.SOURCE_DB_IS_DB2 && this.functionName.getColumnName().equalsIgnoreCase("char")) {
                this.functionName.setColumnName("TO_CHAR");
            }
            else {
                final String fnName = this.functionName.getColumnName();
                this.functionName.setColumnName("CHR");
                if (fnName != null && fnName.equalsIgnoreCase("NCHAR")) {
                    this.functionName.setColumnName("NCHR");
                }
            }
            for (int i_count = 0; i_count < numArgs; ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CHAR");
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
        this.functionName.setColumnName("CHAR");
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
        this.functionName.setColumnName("CHR");
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
        if (arguments.size() > 1) {
            final Vector arguments2 = new Vector();
            for (int j = 0; j < arguments.size(); ++j) {
                arguments2.addElement("CHR(" + arguments.get(j).toString() + ")");
            }
            this.functionName.setColumnName("CONCAT");
            this.setFunctionArguments(arguments2);
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CHAR");
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
        this.functionName.setColumnName("CHAR");
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
        this.functionName.setColumnName("CHR");
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
        this.functionName.setColumnName("CHAR");
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
        if (this.functionName.getColumnName().equalsIgnoreCase("CHAR")) {
            throw new ConvertException("\nThe function CHAR is not supported in TimesTen 5.1.21\n");
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CHR");
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
        this.functionName.setColumnName("CHR");
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
        if (arguments.size() > 1) {
            final Vector arguments2 = new Vector();
            for (int j = 0; j < arguments.size(); ++j) {
                arguments2.addElement("CHR(" + arguments.get(j).toString() + ")");
            }
            this.functionName.setColumnName("CONCAT");
            this.setFunctionArguments(arguments2);
        }
    }
}
