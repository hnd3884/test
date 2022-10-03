package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class round extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ROUND");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
            if (i_count == 2) {
                final String value = arguments.get(2).toString().trim();
                if (!value.equals("0")) {
                    this.functionName.setColumnName("TRUNC");
                }
                arguments.removeElementAt(2);
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ROUND");
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
            this.functionName.setColumnName("CAST");
            final Vector newArg = new Vector();
            final StringBuffer argBuffer = new StringBuffer();
            argBuffer.append("ROUND(");
            argBuffer.append(this.functionArguments.get(0).toString());
            argBuffer.append(",0) AS INTEGER");
            newArg.add(argBuffer.toString());
            this.setFunctionArguments(newArg);
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ROUND");
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
            this.functionName.setColumnName("CONVERT");
            final Vector newArg = new Vector();
            final StringBuffer argBuffer = new StringBuffer();
            argBuffer.append("INTEGER,ROUND(");
            argBuffer.append(this.functionArguments.get(0).toString());
            argBuffer.append(",0)");
            newArg.add(argBuffer.toString());
            this.setFunctionArguments(newArg);
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ROUND");
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
            this.functionName.setColumnName("CAST");
            final Vector newArg = new Vector();
            final StringBuffer argBuffer = new StringBuffer();
            argBuffer.append("ROUND(");
            argBuffer.append(this.functionArguments.get(0).toString());
            argBuffer.append(",0) AS INTEGER");
            newArg.add(argBuffer.toString());
            this.setFunctionArguments(newArg);
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ROUND");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.elementAt(0) instanceof SelectColumn) {
            final SelectColumn sc = arguments.elementAt(0);
            final Vector vc = sc.getColumnExpression();
            if (vc.elementAt(0) instanceof String) {
                String roundNum = vc.elementAt(0);
                roundNum = roundNum.replaceAll("'", "");
                try {
                    Double.parseDouble(roundNum);
                    vc.setElementAt(roundNum, 0);
                }
                catch (final Exception ex) {}
            }
        }
        if (arguments.size() > 1 && arguments.elementAt(1) instanceof SelectColumn) {
            final SelectColumn sc = arguments.elementAt(1);
            final Vector vc = sc.getColumnExpression();
            if (vc.elementAt(0) instanceof String) {
                String roundNum = vc.elementAt(0);
                roundNum = roundNum.replaceAll("'", "");
                try {
                    Integer.parseInt(roundNum);
                    vc.setElementAt(roundNum, 0);
                }
                catch (final Exception ex2) {}
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ROUND");
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
        this.functionName.setColumnName("ROUND");
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
        this.functionName.setColumnName("ROUND");
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
        if (arguments.size() == 1) {
            boolean isDateArg = false;
            final Object obj = arguments.firstElement();
            if (obj instanceof SelectColumn) {
                final SelectColumn sc = (SelectColumn)obj;
                for (int k = 0; k < sc.getColumnExpression().size(); ++k) {
                    final Object scObj = sc.getColumnExpression().get(k);
                    if (scObj instanceof TableColumn) {
                        final TableColumn tc = (TableColumn)scObj;
                        if (CastingUtil.getValueIgnoreCase(SwisSQLAPI.columnDatatypes, tc.getColumnName()) != null && CastingUtil.getValueIgnoreCase(SwisSQLAPI.columnDatatypes, tc.getColumnName()).toString().equalsIgnoreCase("timestamp")) {
                            isDateArg = true;
                        }
                        if (tc.getColumnName().toLowerCase().startsWith("current_date") || tc.getColumnName().toLowerCase().startsWith("current_time")) {
                            isDateArg = true;
                        }
                    }
                    else if (scObj instanceof FunctionCalls) {
                        final FunctionCalls dateFunc = (FunctionCalls)scObj;
                        if (dateFunc.getFunctionName() != null) {
                            final String returnType = SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments());
                            if (returnType.equalsIgnoreCase("date") || returnType.equalsIgnoreCase("timestamp")) {
                                isDateArg = true;
                            }
                        }
                    }
                }
                if (isDateArg && sc.getColumnExpression().size() > 1) {
                    sc.getColumnExpression().add("DAY(4)");
                    this.functionName.setColumnName("");
                }
                else if (!isDateArg) {
                    this.functionName.setColumnName("CAST");
                    final NumericClass nc = new NumericClass();
                    this.setAsDatatype("AS");
                    nc.setDatatypeName("DECIMAL");
                    nc.setOpenBrace("(");
                    nc.setPrecision("38");
                    nc.setScale("0");
                    nc.setClosedBrace(")");
                    arguments.add(nc);
                    this.setFunctionArguments(arguments);
                }
            }
        }
        else if (arguments.size() == 2) {
            boolean isInteger = false;
            try {
                Integer.parseInt(arguments.get(1).toString());
                isInteger = true;
            }
            catch (final NumberFormatException ex) {}
            if (isInteger) {
                this.functionName.setColumnName("CAST");
                final NumericClass nc2 = new NumericClass();
                this.setAsDatatype("AS");
                nc2.setDatatypeName("DECIMAL");
                nc2.setOpenBrace("(");
                nc2.setPrecision("38");
                final String prec = arguments.get(1).toString();
                nc2.setScale(this.getModifiedPrecision(prec));
                nc2.setClosedBrace(")");
                arguments.setElementAt(nc2, 1);
                this.setFunctionArguments(arguments);
            }
        }
    }
    
    public String getModifiedPrecision(final String num) {
        try {
            int number = Integer.parseInt(num);
            if (number > 22) {
                number = 22;
            }
            return "" + number;
        }
        catch (final NumberFormatException ex) {
            return num;
        }
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ROUND");
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
            this.functionName.setColumnName("CAST");
            final Vector newArg = new Vector();
            final StringBuffer argBuffer = new StringBuffer();
            argBuffer.append("ROUND(");
            argBuffer.append(this.functionArguments.get(0).toString());
            argBuffer.append(",0) AS INTEGER");
            newArg.add(argBuffer.toString());
            this.setFunctionArguments(newArg);
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe function ROUND is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ROUND");
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
        final StringBuffer arguments = new StringBuffer();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (i_count > 0) {
                arguments.append(",");
            }
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.append(this.functionArguments.elementAt(i_count));
            }
        }
        String argument = "";
        if (this.functionArguments.size() < 2) {
            argument = "cast(round(" + (Object)arguments + ",0) as bigint)";
        }
        else {
            argument = "round(" + (Object)arguments + ")";
        }
        this.functionName.setColumnName(argument);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
