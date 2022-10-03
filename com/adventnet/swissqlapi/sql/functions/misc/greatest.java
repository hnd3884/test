package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class greatest extends FunctionCalls
{
    private String CaseString;
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("GREATEST");
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
        this.functionName.setColumnName("GREATEST");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
            if (arguments.size() > 0) {
                final ArrayList caseExpression = new ArrayList();
                String caseStr = new String();
                if (arguments.size() == 1) {
                    this.functionName.setColumnName("");
                }
                else {
                    final String tabString = "\n";
                    for (int index = 1; index < arguments.size(); ++index) {
                        if (caseExpression.size() == 1) {
                            caseStr = tabString + "CASE WHEN " + caseExpression.get(0).toString() + " > " + arguments.get(index).toString() + " THEN " + caseExpression.get(0).toString() + " ELSE " + arguments.get(index).toString() + " END";
                            caseExpression.set(0, caseStr);
                        }
                        else {
                            caseStr = tabString + "CASE WHEN " + arguments.get(index - 1).toString() + " > " + arguments.get(index).toString() + " THEN " + arguments.get(index - 1).toString() + " ELSE " + arguments.get(index).toString() + " END";
                            caseExpression.add(caseStr);
                        }
                    }
                    if (from_sqs != null && from_sqs.isMSAzure()) {
                        this.CaseString = "CAST(" + caseStr + " AS FLOAT)";
                    }
                    else {
                        this.CaseString = caseStr;
                    }
                }
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("GREATEST");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
            if (arguments.size() > 0) {
                final ArrayList caseExpression = new ArrayList();
                String caseStr = new String();
                if (arguments.size() == 1) {
                    this.functionName.setColumnName("");
                }
                else {
                    final String tabString = "\n";
                    for (int index = 1; index < arguments.size(); ++index) {
                        if (caseExpression.size() == 1) {
                            caseStr = tabString + "CASE WHEN " + caseExpression.get(0).toString() + " > " + arguments.get(index).toString() + " THEN " + caseExpression.get(0).toString() + " ELSE " + arguments.get(index).toString() + " END";
                            caseExpression.set(0, caseStr);
                        }
                        else {
                            caseStr = tabString + "CASE WHEN " + arguments.get(index - 1).toString() + " > " + arguments.get(index).toString() + " THEN " + arguments.get(index - 1).toString() + " ELSE " + arguments.get(index).toString() + " END";
                            caseExpression.add(caseStr);
                        }
                    }
                    this.CaseString = caseStr;
                }
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("GREATEST");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
            if (arguments.size() > 0) {
                final ArrayList caseExpression = new ArrayList();
                String caseStr = new String();
                if (arguments.size() == 1) {
                    this.functionName.setColumnName("");
                }
                else {
                    final String tabString = "\n";
                    for (int index = 1; index < arguments.size(); ++index) {
                        if (caseExpression.size() == 1) {
                            caseStr = tabString + "CASE WHEN " + caseExpression.get(0).toString() + " > " + arguments.get(index).toString() + " THEN " + caseExpression.get(0).toString() + " ELSE " + arguments.get(index).toString() + " END";
                            caseExpression.set(0, caseStr);
                        }
                        else {
                            caseStr = tabString + "CASE WHEN " + arguments.get(index - 1).toString() + " > " + arguments.get(index).toString() + " THEN " + arguments.get(index - 1).toString() + " ELSE " + arguments.get(index).toString() + " END";
                            caseExpression.add(caseStr);
                        }
                    }
                    this.CaseString = caseStr;
                }
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionArguments.size() == 1) {
            throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported. \n Please ensure that the correct number of arguments are passed\n");
        }
        final boolean needsCasting = to_sqs != null && to_sqs.canCastAllToTextColumns();
        this.functionName.setColumnName("GREATEST");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                sc.convertSelectColumnToTextDataType(needsCasting);
                arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (to_sqs != null) {
            to_sqs.addCurrentIndexToCoalesceFunctionList();
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("GREATEST");
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
        this.functionName.setColumnName("GREATEST");
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
        this.functionName.setColumnName("GREATEST");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() > 0 && arguments.size() < 11) {
            final ArrayList caseExpression = new ArrayList();
            String caseStr = new String();
            if (arguments.size() == 1) {
                this.functionName.setColumnName("");
            }
            else {
                final String tabString = "\n";
                final StringBuffer temp = new StringBuffer();
                temp.append("CASE ");
                for (int index = 0; index < arguments.size(); ++index) {
                    if (index != arguments.size() - 1) {
                        temp.append("WHEN ");
                        for (int k = 0; k != arguments.size(); ++k) {
                            if (k != index) {
                                temp.append(arguments.get(index) + " >= " + arguments.get(k));
                                if (k != arguments.size() - 1) {
                                    temp.append(" AND ");
                                }
                            }
                        }
                        temp.append(" THEN " + arguments.get(index) + " \n");
                    }
                    else {
                        temp.append("ELSE " + arguments.get(index));
                    }
                }
                temp.append(" END");
                caseStr = temp.toString();
                this.CaseString = caseStr;
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("GREATEST");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
            if (arguments.size() > 0) {
                final ArrayList caseExpression = new ArrayList();
                String caseStr = new String();
                if (arguments.size() == 1) {
                    this.functionName.setColumnName("");
                }
                else {
                    final String tabString = "\n";
                    for (int index = 1; index < arguments.size(); ++index) {
                        if (caseExpression.size() == 1) {
                            caseStr = tabString + "CASE WHEN " + caseExpression.get(0).toString() + " > " + arguments.get(index).toString() + " THEN " + caseExpression.get(0).toString() + " ELSE " + arguments.get(index).toString() + " END";
                            caseExpression.set(0, caseStr);
                        }
                        else {
                            caseStr = tabString + "CASE WHEN " + arguments.get(index - 1).toString() + " > " + arguments.get(index).toString() + " THEN " + arguments.get(index - 1).toString() + " ELSE " + arguments.get(index).toString() + " END";
                            caseExpression.add(caseStr);
                        }
                    }
                    this.CaseString = caseStr;
                }
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public String toString() {
        if (this.CaseString != null) {
            String tabString = "\n";
            for (int j = 1; j < SelectQueryStatement.beautyTabCount; ++j) {
                tabString += "\t";
            }
            if (this.CaseString.indexOf(tabString) == -1) {
                this.CaseString = StringFunctions.replaceAll(tabString, "\n", this.CaseString);
            }
            return this.CaseString;
        }
        this.functionName.setColumnName("GREATEST");
        return super.toString();
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("GREATEST");
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
