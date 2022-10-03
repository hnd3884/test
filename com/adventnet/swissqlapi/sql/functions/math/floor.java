package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class floor extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("FLOOR");
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
        this.functionName.setColumnName("FLOOR");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final Vector colExpressions = this.functionArguments.elementAt(i_count).getColumnExpression();
                boolean isFirstArgDateType = false;
                if (colExpressions != null) {
                    for (int i = 0; i < colExpressions.size(); ++i) {
                        if (colExpressions.get(i) instanceof TableColumn) {
                            final TableColumn tc = colExpressions.get(i);
                            final String columnName = tc.getColumnName();
                            if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                                isFirstArgDateType = true;
                                tc.setColumnName("(YEAR(GETDATE())*365 + MONTH(GETDATE())*31 + DAY(GETDATE()) + DATEPART(hh,GETDATE())/24.0 + DATEPART(mi,GETDATE())/1440.0 + DATEPART(ss,GETDATE())/86400.0)");
                            }
                            else if (columnName != null) {
                                final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                if ((dataType != null && dataType.toLowerCase().indexOf("date") != -1) || isFirstArgDateType) {
                                    final String colName = tc.toString();
                                    tc.setColumnName("(YEAR(" + colName + ")*365 + MONTH(" + colName + ")*31 + DAY(" + colName + ") + DATEPART(hh," + colName + ")/24.0 + DATEPART(mi," + colName + ")/1440.0 + DATEPART(ss," + colName + ")/86400.0)");
                                    tc.setTableName(null);
                                }
                            }
                        }
                        else if (colExpressions.get(i) instanceof FunctionCalls) {
                            FunctionCalls function = colExpressions.get(i);
                            final String fnName = function.getFunctionName().getColumnName();
                            if (fnName.equalsIgnoreCase("TO_DATE")) {
                                isFirstArgDateType = true;
                                function = function.toMSSQLServerSelect(to_sqs, from_sqs);
                                final TableColumn newTC = new TableColumn();
                                newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                colExpressions.setElementAt(newTC, i);
                            }
                            else if (fnName.equalsIgnoreCase("TRUNC")) {
                                final Vector fnArgs = function.getFunctionArguments();
                                if (fnArgs != null) {
                                    Object obj = fnArgs.get(0);
                                    if (obj instanceof SelectColumn) {
                                        final Vector scExpr = ((SelectColumn)obj).getColumnExpression();
                                        for (int l = 0; l < scExpr.size(); ++l) {
                                            obj = scExpr.get(l);
                                            if (obj instanceof TableColumn) {
                                                final TableColumn tc2 = (TableColumn)obj;
                                                final String columnName2 = tc2.getColumnName();
                                                if (columnName2 != null && columnName2.equalsIgnoreCase("SYSDATE")) {
                                                    isFirstArgDateType = true;
                                                    function = function.toMSSQLServerSelect(to_sqs, from_sqs);
                                                    final TableColumn newTC2 = new TableColumn();
                                                    newTC2.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                                    colExpressions.setElementAt(newTC2, i);
                                                }
                                                else if (columnName2 != null) {
                                                    final String dataType2 = MetadataInfoUtil.getDatatypeName(from_sqs, tc2);
                                                    if ((dataType2 != null && dataType2.toLowerCase().indexOf("date") != -1) || isFirstArgDateType) {
                                                        function = function.toMSSQLServerSelect(to_sqs, from_sqs);
                                                        final TableColumn newTC3 = new TableColumn();
                                                        newTC3.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                                        colExpressions.setElementAt(newTC3, i);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
        this.functionName.setColumnName("FLOOR");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final Vector colExpressions = this.functionArguments.elementAt(i_count).getColumnExpression();
                boolean isFirstArgDateType = false;
                if (colExpressions != null) {
                    for (int i = 0; i < colExpressions.size(); ++i) {
                        if (colExpressions.get(i) instanceof TableColumn) {
                            final TableColumn tc = colExpressions.get(i);
                            final String columnName = tc.getColumnName();
                            if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                                isFirstArgDateType = true;
                                tc.setColumnName("(YEAR(GETDATE())*365 + MONTH(GETDATE())*31 + DAY(GETDATE()) + DATEPART(hh,GETDATE())/24.0 + DATEPART(mi,GETDATE())/1440.0 + DATEPART(ss,GETDATE())/86400.0)");
                            }
                            else if (columnName != null) {
                                final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                if ((dataType != null && dataType.toLowerCase().indexOf("date") != -1) || isFirstArgDateType) {
                                    final String colName = tc.toString();
                                    tc.setColumnName("(YEAR(" + colName + ")*365 + MONTH(" + colName + ")*31 + DAY(" + colName + ") + DATEPART(hh," + colName + ")/24.0 + DATEPART(mi," + colName + ")/1440.0 + DATEPART(ss," + colName + ")/86400.0)");
                                    tc.setTableName(null);
                                }
                            }
                        }
                        else if (colExpressions.get(i) instanceof FunctionCalls) {
                            FunctionCalls function = colExpressions.get(i);
                            final String fnName = function.getFunctionName().getColumnName();
                            if (fnName.equalsIgnoreCase("TO_DATE")) {
                                isFirstArgDateType = true;
                                function = function.toSybaseSelect(to_sqs, from_sqs);
                                final TableColumn newTC = new TableColumn();
                                newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                colExpressions.setElementAt(newTC, i);
                            }
                            else if (fnName.equalsIgnoreCase("TRUNC")) {
                                final Vector fnArgs = function.getFunctionArguments();
                                if (fnArgs != null) {
                                    Object obj = fnArgs.get(0);
                                    if (obj instanceof SelectColumn) {
                                        final Vector scExpr = ((SelectColumn)obj).getColumnExpression();
                                        for (int l = 0; l < scExpr.size(); ++l) {
                                            obj = scExpr.get(l);
                                            if (obj instanceof TableColumn) {
                                                final TableColumn tc2 = (TableColumn)obj;
                                                final String columnName2 = tc2.getColumnName();
                                                if (columnName2 != null && columnName2.equalsIgnoreCase("SYSDATE")) {
                                                    isFirstArgDateType = true;
                                                    function = function.toSybaseSelect(to_sqs, from_sqs);
                                                    final TableColumn newTC2 = new TableColumn();
                                                    newTC2.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                                    colExpressions.setElementAt(newTC2, i);
                                                }
                                                else if (columnName2 != null) {
                                                    final String dataType2 = MetadataInfoUtil.getDatatypeName(from_sqs, tc2);
                                                    if ((dataType2 != null && dataType2.toLowerCase().indexOf("date") != -1) || isFirstArgDateType) {
                                                        function = function.toSybaseSelect(to_sqs, from_sqs);
                                                        final TableColumn newTC3 = new TableColumn();
                                                        newTC3.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                                        colExpressions.setElementAt(newTC3, i);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
        this.functionName.setColumnName("FLOOR");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final Vector colExpressions = this.functionArguments.elementAt(i_count).getColumnExpression();
                boolean isFirstArgDateType = false;
                if (colExpressions != null) {
                    for (int i = 0; i < colExpressions.size(); ++i) {
                        if (colExpressions.get(i) instanceof TableColumn) {
                            final TableColumn tc = colExpressions.get(i);
                            final String columnName = tc.getColumnName();
                            if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                                isFirstArgDateType = true;
                                tc.setColumnName("(YEAR(CURRENT TIMESTAMP)*365 + MONTH(CURRENT TIMESTAMP)*31 + DAY(CURRENT TIMESTAMP) + HOUR(CURRENT TIMESTAMP)/24.0 + MINUTE(CURRENT TIMESTAMP)/1440.0 + SECOND(CURRENT TIMESTAMP)/86400.0)");
                            }
                            else if (columnName != null) {
                                final String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                if ((dataType != null && dataType.toLowerCase().indexOf("date") != -1) || isFirstArgDateType) {
                                    final String colName = tc.toString();
                                    tc.setColumnName("(YEAR(" + colName + ")*365 + MONTH(" + colName + ")*31 + DAY(" + colName + ") + HOUR(" + colName + ")/24.0 + MINUTE(" + colName + ")/1440.0 + SECOND(" + colName + ")/86400.0)");
                                    tc.setTableName(null);
                                }
                            }
                        }
                        else if (colExpressions.get(i) instanceof FunctionCalls) {
                            FunctionCalls function = colExpressions.get(i);
                            final String fnName = function.getFunctionName().getColumnName();
                            if (fnName.equalsIgnoreCase("TO_DATE")) {
                                isFirstArgDateType = true;
                                function = function.toDB2Select(to_sqs, from_sqs);
                                final TableColumn newTC = new TableColumn();
                                newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + HOUR(" + function.toString() + ")/24.0 + MINUTE(" + function.toString() + ")/1440.0 + SECOND(" + function.toString() + ")/86400.0)");
                                colExpressions.setElementAt(newTC, i);
                            }
                            else if (fnName.equalsIgnoreCase("TRUNC")) {
                                final Vector fnArgs = function.getFunctionArguments();
                                if (fnArgs != null) {
                                    Object obj = fnArgs.get(0);
                                    if (obj instanceof SelectColumn) {
                                        final Vector scExpr = ((SelectColumn)obj).getColumnExpression();
                                        for (int l = 0; l < scExpr.size(); ++l) {
                                            obj = scExpr.get(l);
                                            if (obj instanceof TableColumn) {
                                                final TableColumn tc2 = (TableColumn)obj;
                                                final String columnName2 = tc2.getColumnName();
                                                if (columnName2 != null && columnName2.equalsIgnoreCase("SYSDATE")) {
                                                    isFirstArgDateType = true;
                                                    function = function.toDB2Select(to_sqs, from_sqs);
                                                    final TableColumn newTC2 = new TableColumn();
                                                    newTC2.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + HOUR(" + function.toString() + ")/24.0 + MINUTE(" + function.toString() + ")/1440.0 + SECOND(" + function.toString() + ")/86400.0)");
                                                    colExpressions.setElementAt(newTC2, i);
                                                }
                                                else if (columnName2 != null) {
                                                    final String dataType2 = MetadataInfoUtil.getDatatypeName(from_sqs, tc2);
                                                    if ((dataType2 != null && dataType2.toLowerCase().indexOf("date") != -1) || isFirstArgDateType) {
                                                        function = function.toDB2Select(to_sqs, from_sqs);
                                                        final TableColumn newTC3 = new TableColumn();
                                                        newTC3.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + HOUR(" + function.toString() + ")/24.0 + MINUTE(" + function.toString() + ")/1440.0 + SECOND(" + function.toString() + ")/86400.0)");
                                                        colExpressions.setElementAt(newTC3, i);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.functionName.setColumnName("CAST");
        this.setAsDatatype("AS");
        arguments.add("INTEGER");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("FLOOR");
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
        this.functionName.setColumnName("FLOOR");
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
        this.functionName.setColumnName("FLOOR");
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
        this.functionName.setColumnName("FLOOR");
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
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.functionName.setColumnName("CAST");
        this.setAsDatatype("AS");
        arguments.add("INT");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("FLOOR");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                final Vector colExpr = sc.getColumnExpression();
                if (colExpr.size() != 1 || !(colExpr.get(0) instanceof String)) {
                    throw new ConvertException("\nThe function FLOOR is not supported in TimesTen 5.1.21\n");
                }
                this.functionName.setColumnName("");
                this.setOpenBracesForFunctionNameRequired(false);
                arguments.add(Math.floor(Double.parseDouble(colExpr.get(0).toString())) + "");
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("FLOOR");
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
        this.functionName.setColumnName("FLOOR");
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
    }
}
