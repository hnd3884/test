package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class isnull extends FunctionCalls
{
    private String CaseString;
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("NVL");
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
        if (this.functionArguments.size() == 1) {
            this.functionName.setColumnName("DECODE");
            this.functionArguments.add("NULL , 1 , 0");
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ISNULL");
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
        if (this.functionArguments.size() == 1) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CASE ");
            sb.append(this.functionArguments.elementAt(0).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
            sb.append("WHEN  NULL THEN 1 ELSE 0 ");
            sb.append("END");
            this.CaseString = sb.toString();
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ISNULL");
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
        if (this.functionArguments.size() == 1) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CASE ");
            if (this.context != null) {
                this.functionArguments.elementAt(0).setObjectContext(this.context);
                sb.append(this.context.getEquivalent(this.functionArguments.elementAt(0).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
            }
            else {
                sb.append(this.functionArguments.elementAt(0).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
            }
            sb.append("WHEN  NULL THEN 1 ELSE 0 ");
            sb.append("END");
            this.CaseString = sb.toString();
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
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
        if (this.functionArguments.size() == 1) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CASE ");
            sb.append(this.functionArguments.elementAt(0).toDB2Select(to_sqs, from_sqs).toString() + " ");
            sb.append("WHEN  NULL THEN 1 ELSE 0 ");
            sb.append("END");
            this.CaseString = sb.toString();
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
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
        if (this.functionArguments.size() == 1) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CASE WHEN  ");
            sb.append(arguments.get(0).toString() + " ");
            sb.append(" IS NULL THEN 1 ELSE 0 ");
            sb.append("END");
            this.CaseString = sb.toString();
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionArguments.size() != 1) {
            this.functionName.setColumnName("COALESCE");
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
        if (this.functionArguments.size() == 1) {
            this.functionName.setColumnName("ISNULL");
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
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
        if (this.functionArguments.size() == 1) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CASE ");
            sb.append(this.functionArguments.elementAt(0).toANSISelect(to_sqs, from_sqs).toString() + " ");
            sb.append("WHEN  NULL THEN 1 ELSE 0 END ");
            this.CaseString = sb.toString();
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
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
        if (this.functionArguments.size() == 1) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CASE ");
            sb.append(this.functionArguments.elementAt(0).toTeradataSelect(to_sqs, from_sqs).toString() + " ");
            sb.append("WHEN  NULL THEN 1 ELSE 0 END ");
            this.CaseString = sb.toString();
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("NVL");
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
        if (this.functionArguments.size() == 1) {
            this.functionName.setColumnName("DECODE");
            this.functionArguments.add("NULL , 1 , 0");
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("NVL");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTimesTenSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
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
        if (this.functionArguments.size() == 1) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CASE ");
            sb.append(this.functionArguments.elementAt(0).toNetezzaSelect(to_sqs, from_sqs).toString() + " ");
            sb.append("WHEN  NULL THEN 1 ELSE 0 END ");
            this.CaseString = sb.toString();
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
    }
    
    @Override
    public String toString() {
        if (this.CaseString != null) {
            return this.CaseString;
        }
        return super.toString();
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
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
        if (this.functionArguments.size() == 1) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CASE WHEN ");
            sb.append(arguments.get(0).toString() + " ");
            sb.append(" IS NULL THEN 1 ELSE 0 END ");
            this.CaseString = sb.toString();
            this.functionName = null;
            this.argumentQualifier = null;
            this.functionArguments = null;
        }
    }
}
