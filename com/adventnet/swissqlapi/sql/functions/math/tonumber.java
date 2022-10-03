package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class tonumber extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TO_NUMBER");
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
        this.functionName.setColumnName("CONVERT");
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
        if (arguments.size() == 1) {
            this.functionArguments.add(this.functionArguments.get(0));
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn fSc = this.functionArguments.get(0);
                final Vector fScVec = fSc.getColumnExpression();
                for (int fScVecSiz = fScVec.size(), v = 0; v < fScVecSiz; ++v) {
                    final Object fScVecArg = fScVec.elementAt(v);
                    if (fScVecArg instanceof TableColumn) {
                        final String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)fScVecArg);
                        if (dtype != null && dtype.indexOf("(") != -1) {
                            String dtypeSize = dtype.substring(dtype.indexOf("(") + 1, dtype.indexOf(")"));
                            if (dtypeSize.indexOf(",") == -1 && Integer.parseInt(dtypeSize) > 38) {
                                dtypeSize = "38";
                            }
                            arguments.setElementAt("NUMERIC(" + dtypeSize + ")", 0);
                        }
                        else {
                            arguments.setElementAt("NUMERIC(8, 2)", 0);
                        }
                    }
                    else if (fScVecArg instanceof String) {
                        final String arg = fScVecArg.toString();
                        if (arg.indexOf(".") != -1) {
                            int argLength = arg.length() - 1;
                            if (arg.startsWith("'") || arg.startsWith("\"")) {
                                argLength -= 2;
                            }
                            final int scale = arg.substring(arg.indexOf("."), argLength + 1).length();
                            arguments.setElementAt("NUMERIC(" + argLength + ", " + scale + ")", 0);
                        }
                        else {
                            int argLen = arg.length();
                            if (arg.startsWith("'") || arg.startsWith("\"")) {
                                argLen -= 2;
                            }
                            arguments.setElementAt("NUMERIC(" + argLen + ")", 0);
                        }
                    }
                    else {
                        arguments.setElementAt("NUMERIC(8, 2)", 0);
                    }
                }
            }
            else if (this.functionArguments.get(0) instanceof String) {
                final String arg2 = this.functionArguments.get(0).toString();
                if (arg2.startsWith("'")) {
                    int argLength2 = arg2.length() - 2;
                    if (arg2.indexOf(".") != -1) {
                        argLength2 = arg2.length() - 1;
                        final int scale2 = arg2.substring(arg2.indexOf("."), argLength2 + 1).length();
                        arguments.setElementAt("NUMERIC(" + argLength2 + ", " + scale2 + ")", 0);
                    }
                    else {
                        arguments.setElementAt("NUMERIC(" + argLength2 + ")", 0);
                    }
                }
                else {
                    arguments.setElementAt("NUMERIC(8, 2)", 0);
                }
            }
            else {
                arguments.setElementAt("NUMERIC(8, 2)", 0);
            }
        }
        else if (arguments.size() > 1) {
            this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
            this.functionArguments.setElementAt("NUMERIC(8, 2)", 0);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONVERT");
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
        if (arguments.size() == 1) {
            this.functionArguments.add(this.functionArguments.get(0));
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn fSc = this.functionArguments.get(0);
                final Vector fScVec = fSc.getColumnExpression();
                for (int fScVecSiz = fScVec.size(), v = 0; v < fScVecSiz; ++v) {
                    final Object fScVecArg = fScVec.elementAt(v);
                    if (fScVecArg instanceof TableColumn) {
                        final String dtype = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)fScVecArg);
                        if (dtype != null && dtype.indexOf("(") != -1) {
                            String dtypeSize = dtype.substring(dtype.indexOf("(") + 1, dtype.indexOf(")"));
                            if (dtypeSize.indexOf(",") == -1 && Integer.parseInt(dtypeSize) > 38) {
                                dtypeSize = "38";
                            }
                            arguments.setElementAt("NUMERIC(" + dtypeSize + ")", 0);
                        }
                        else {
                            arguments.setElementAt("NUMERIC(8, 2)", 0);
                        }
                    }
                    else if (fScVecArg instanceof String) {
                        final String arg = fScVecArg.toString();
                        if (arg.indexOf(".") != -1) {
                            int argLength = arg.length() - 1;
                            if (arg.startsWith("'") || arg.startsWith("\"")) {
                                argLength -= 2;
                            }
                            final int scale = arg.substring(arg.indexOf("."), argLength + 1).length();
                            arguments.setElementAt("NUMERIC(" + argLength + ", " + scale + ")", 0);
                        }
                        else {
                            int argLen = arg.length();
                            if (arg.startsWith("'") || arg.startsWith("\"")) {
                                argLen -= 2;
                            }
                            arguments.setElementAt("NUMERIC(" + argLen + ")", 0);
                        }
                    }
                    else {
                        arguments.setElementAt("NUMERIC(8, 2)", 0);
                    }
                }
            }
            else if (this.functionArguments.get(0) instanceof String) {
                final String arg2 = this.functionArguments.get(0).toString();
                if (arg2.startsWith("'")) {
                    if (arg2.indexOf(".") != -1) {
                        int argLength2 = arg2.length() - 1;
                        if (arg2.startsWith("'") || arg2.startsWith("\"")) {
                            argLength2 -= 2;
                        }
                        final int scale2 = arg2.substring(arg2.indexOf("."), argLength2 + 1).length();
                        arguments.setElementAt("NUMERIC(" + argLength2 + ", " + scale2 + ")", 0);
                    }
                    else {
                        arguments.setElementAt("NUMERIC(" + arg2.length() + ")", 0);
                    }
                }
                else {
                    arguments.setElementAt("NUMERIC(8, 2)", 0);
                }
            }
            else {
                arguments.setElementAt("NUMERIC(8, 2)", 0);
            }
        }
        else if (arguments.size() > 1) {
            this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
            this.functionArguments.setElementAt("NUMERIC(8, 2)", 0);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TO_NUMBER");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName("NUMERIC");
            nc.setClosedBrace(")");
            nc.setOpenBrace("(");
            nc.setScale("2");
            nc.setPrecision("22");
            this.setAsDatatype("AS");
            arguments.add(nc);
        }
        else if (arguments.size() == 2) {
            this.functionName.setColumnName("CAST");
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName("NUMERIC");
            nc.setClosedBrace(")");
            nc.setOpenBrace("(");
            nc.setScale("16");
            nc.setPrecision("22");
            this.setAsDatatype("AS");
            arguments.set(1, nc);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TO_NUMBER");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName("NUMERIC");
            nc.setClosedBrace(")");
            nc.setOpenBrace("(");
            nc.setScale("16");
            nc.setPrecision("22");
            this.setAsDatatype("AS");
            arguments.add(nc);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 1 && arguments.get(0) instanceof SelectColumn) {
            this.functionName.setColumnName("");
            final Vector v = arguments.get(0).getColumnExpression();
            v.add("*1");
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TO_NUMBER");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName("NUMERIC");
            this.setAsDatatype("AS");
            arguments.add(nc);
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
        if (arguments.size() > 0) {
            if (arguments.get(0) instanceof SelectColumn) {
                final SelectColumn sc = arguments.get(0);
                final Vector columnExp = sc.getColumnExpression();
                columnExp.add("::");
                columnExp.add("NUMERIC (22, 16)");
            }
            if (arguments.size() == 2) {
                arguments.removeElementAt(1);
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("TO_NUMBER");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName("NUMERIC");
            this.setAsDatatype("AS");
            arguments.add(nc);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                try {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
                    continue;
                }
                catch (final ConvertException ce) {
                    throw ce;
                }
            }
            arguments.addElement(this.functionArguments.elementAt(i_count));
        }
        if (arguments.size() == 3) {
            arguments.removeElementAt(2);
        }
        if (arguments.size() == 2) {
            arguments.removeElementAt(1);
        }
        if (arguments.size() == 1) {
            this.functionName.setColumnName("CAST");
            final NumericClass nc = new NumericClass();
            nc.setDatatypeName("NUMERIC");
            if (this.functionArguments.get(0) instanceof SelectColumn) {
                final String[] precScale = this.getPrecisionAndScale(from_sqs, this.functionArguments.elementAt(0));
                nc.setOpenBrace("(");
                nc.setPrecision(precScale[0]);
                nc.setScale(precScale[1]);
                nc.setClosedBrace(")");
            }
            else {
                nc.setOpenBrace("(");
                nc.setPrecision("38");
                nc.setScale("16");
                nc.setClosedBrace(")");
            }
            this.setAsDatatype("AS");
            arguments.add(nc);
        }
        this.setFunctionArguments(arguments);
    }
    
    private String[] getPrecisionAndScale(final SelectQueryStatement from_sqs, final SelectColumn numberArg) {
        final String[] precScale = new String[2];
        final Vector columnExpression = numberArg.getColumnExpression();
        for (int colExpSize = columnExpression.size(), i = 0; i < colExpSize; ++i) {
            final Object colExprElement = columnExpression.elementAt(i);
            if (colExprElement instanceof TableColumn) {
                final TableColumn tcn = (TableColumn)colExprElement;
                final String datatype = MetadataInfoUtil.getDatatypeName(from_sqs, tcn);
                if (datatype != null) {
                    final int commaIndex = datatype.indexOf(",");
                    if (commaIndex != -1) {
                        final int openBraceIndex = datatype.indexOf("(");
                        final int closeBraceIndex = datatype.indexOf(")");
                        precScale[0] = datatype.substring(openBraceIndex + 1, commaIndex);
                        precScale[1] = datatype.substring(commaIndex + 1, closeBraceIndex);
                    }
                    else {
                        precScale[0] = "38";
                        precScale[1] = "0";
                    }
                }
            }
            else if (colExprElement instanceof String) {
                final String s_ce = (String)colExprElement;
                final int dotIndex = s_ce.indexOf(".");
                if (dotIndex != -1) {
                    if (s_ce.startsWith("'")) {
                        precScale[0] = "" + s_ce.substring(1, s_ce.length() - 1).length();
                        precScale[1] = "" + s_ce.substring(dotIndex + 1, s_ce.length() - 1).length();
                    }
                    else {
                        precScale[0] = "" + s_ce.length();
                        precScale[1] = "" + s_ce.substring(dotIndex + 1, s_ce.length()).length();
                    }
                }
                else {
                    precScale[0] = "38";
                    precScale[1] = "0";
                }
            }
        }
        if (precScale[0] == null) {
            precScale[0] = "38";
        }
        if (precScale[1] == null) {
            precScale[1] = "16";
        }
        return precScale;
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
    }
}
