package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import java.util.Vector;

public class ComputeByStatement
{
    private String compute;
    private String byString;
    private Vector functionNameVector;
    private Vector tableNameVector;
    
    public void setCompute(final String compute) {
        this.compute = compute;
    }
    
    public void setFunctionNameVector(final Vector functionNameVector) {
        this.functionNameVector = functionNameVector;
    }
    
    public void setBy(final String byString) {
        this.byString = byString;
    }
    
    public void setTableNameVector(final Vector tableNameVector) {
        this.tableNameVector = tableNameVector;
    }
    
    public Vector getFunctionNameVector() {
        return this.functionNameVector;
    }
    
    public Vector getTableNameVector() {
        return this.tableNameVector;
    }
    
    public ComputeByStatement toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toANSISelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toANSISelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toTeradataSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toTeradataSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toDB2Select(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toDB2Select(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toInformixSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toInformixSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toMSSQLServerSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toMSSQLServerSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toMySQLSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toMySQLSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toOracleSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toOracleSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    public ComputeByStatement toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        return cbs;
    }
    
    public ComputeByStatement toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toNetezzaSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toNetezzaSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.compute != null) {
            sb.append(this.compute.toUpperCase());
        }
        if (this.functionNameVector != null) {
            ++SelectQueryStatement.beautyTabCount;
            for (int i_count = 0; i_count < this.functionNameVector.size(); ++i_count) {
                if (i_count == 0) {
                    sb.append(" " + this.functionNameVector.elementAt(i_count).toString());
                }
                else {
                    sb.append(",");
                    sb.append("\n");
                    for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                        sb.append("\t");
                    }
                    sb.append(this.functionNameVector.elementAt(i_count).toString());
                }
            }
            --SelectQueryStatement.beautyTabCount;
        }
        if (this.byString != null) {
            sb.append(" " + this.byString.toUpperCase());
        }
        if (this.tableNameVector != null) {
            ++SelectQueryStatement.beautyTabCount;
            for (int i_count = 0; i_count < this.tableNameVector.size(); ++i_count) {
                if (i_count == 0) {
                    sb.append(" " + this.tableNameVector.elementAt(i_count).toString());
                }
                else {
                    sb.append(",");
                    sb.append("\n");
                    for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                        sb.append("\t\t");
                    }
                    sb.append(this.tableNameVector.elementAt(i_count).toString());
                }
            }
            --SelectQueryStatement.beautyTabCount;
        }
        return sb.toString();
    }
    
    public ComputeByStatement toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ComputeByStatement cbs = new ComputeByStatement();
        cbs.setCompute(this.compute);
        if (this.byString != null) {
            cbs.setBy(this.byString);
        }
        if (this.functionNameVector != null) {
            final Vector functionNames = new Vector();
            for (int i = 0; i < this.functionNameVector.size(); ++i) {
                if (this.functionNameVector.elementAt(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.functionNameVector.get(i);
                    functionNames.add(fc.toVectorWiseSelect(to_sqs, from_sqs));
                }
            }
            cbs.setFunctionNameVector(functionNames);
        }
        if (this.tableNameVector != null) {
            final Vector tableNames = new Vector();
            for (int i = 0; i < this.tableNameVector.size(); ++i) {
                if (this.tableNameVector.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = this.tableNameVector.get(i);
                    tableNames.add(tc.toVectorWiseSelect(to_sqs, from_sqs));
                }
            }
            cbs.setTableNameVector(tableNames);
        }
        return cbs;
    }
}
