package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class instr extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") || this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
            final Object temp = this.functionArguments.get(0);
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
            if (this.functionArguments.size() > 2) {
                final String str = this.functionArguments.get(2).toString();
                if (str.indexOf("-") != -1) {
                    this.functionArguments.setElementAt("1", 2);
                }
                else {
                    try {
                        final int position = Integer.parseInt(str);
                        if (position == 0) {
                            this.functionArguments.setElementAt("1", 2);
                        }
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            Object temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
                String tempString = ((SelectColumn)temp).toString().trim();
                if (tempString.startsWith("'%")) {
                    tempString = (String)(temp = StringFunctions.replaceFirst("'", "'%", tempString));
                }
                if (tempString.endsWith("%'")) {
                    tempString = (String)(temp = tempString.substring(0, tempString.length() - 2) + "'");
                }
            }
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        this.functionName.setColumnName("INSTR");
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
        String orgFunctionName = new String();
        if (this.functionName.getColumnName().equalsIgnoreCase("INSTR") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
            orgFunctionName = this.functionName.getColumnName();
            final Object temp = this.functionArguments.get(0);
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            this.functionName.setColumnName("PATINDEX");
        }
        else if (this.functionArguments.size() <= 3) {
            this.functionName.setColumnName("CHARINDEX");
        }
        else {
            this.functionName.setColumnName("dbo.ADV_CHARINDEX4");
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
        if (orgFunctionName.equalsIgnoreCase("INSTR") && this.functionArguments.size() == 4 && this.functionArguments.get(3) instanceof SelectColumn) {
            try {
                final Vector colExpression = this.functionArguments.get(3).getColumnExpression();
                final int fourthArg = Integer.parseInt(colExpression.get(0));
                if (fourthArg == 1) {
                    this.functionArguments.removeElementAt(3);
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String orgFunctionName = new String();
        if (this.functionName.getColumnName().equalsIgnoreCase("INSTR") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
            orgFunctionName = this.functionName.getColumnName();
            final Object temp = this.functionArguments.get(0);
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            this.functionName.setColumnName("PATINDEX");
        }
        else {
            this.functionName.setColumnName("CHARINDEX");
        }
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
        if (orgFunctionName.equalsIgnoreCase("INSTR") && this.functionArguments.size() == 4 && this.functionArguments.get(3) instanceof SelectColumn) {
            try {
                final Vector colExpression = this.functionArguments.get(3).getColumnExpression();
                final int fourthArg = Integer.parseInt(colExpression.get(0));
                if (fourthArg == 1) {
                    this.functionArguments.removeElementAt(3);
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("INSTR") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
            final Object temp = this.functionArguments.get(0);
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            Object temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
                String tempString = ((SelectColumn)temp).toString().trim();
                if (tempString.startsWith("'%")) {
                    tempString = (String)(temp = StringFunctions.replaceFirst("'", "'%", tempString));
                }
                if (tempString.endsWith("%'")) {
                    tempString = (String)(temp = tempString.substring(0, tempString.length() - 2) + "'");
                }
            }
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        this.functionName.setColumnName("LOCATE");
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
        if (this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") || this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
            final Object temp = this.functionArguments.get(0);
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            Object temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
                String tempString = ((SelectColumn)temp).toString().trim();
                if (tempString.startsWith("'%")) {
                    tempString = (String)(temp = StringFunctions.replaceFirst("'", "'%", tempString));
                }
                if (tempString.endsWith("%'")) {
                    tempString = (String)(temp = tempString.substring(0, tempString.length() - 2) + "'");
                }
            }
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        this.functionName.setColumnName("STRPOS");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count <= 1) {
                    sc.convertSelectColumnToTextDataType();
                }
                arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (SwisSQLOptions.isDataupia && this.functionName.getColumnName() != null && this.functionName.getColumnName().trim().equalsIgnoreCase("INSTR")) {
            return;
        }
        final int argLength = this.functionArguments.size();
        if (argLength == 3) {
            final String qry = " case when " + arguments.get(2) + " = 0 then 0 when (STRPOS(SUBSTRING(" + arguments.get(0) + ", " + arguments.get(2) + ")," + arguments.get(1) + "))=0 then 0 else ((STRPOS(SUBSTRING(" + arguments.get(0) + ", " + arguments.get(2) + "), " + arguments.get(1) + ")-1)+" + arguments.get(2) + ") end";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            Object temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
                String tempString = ((SelectColumn)temp).toString().trim();
                if (tempString.startsWith("'%")) {
                    tempString = (String)(temp = StringFunctions.replaceFirst("'", "'%", tempString));
                }
                if (tempString.endsWith("%'")) {
                    tempString = (String)(temp = tempString.substring(0, tempString.length() - 2) + "'");
                }
            }
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
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
        if (this.functionName.getColumnName().equalsIgnoreCase("INSTR") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
            final Object temp2 = arguments.get(0);
            arguments.set(0, arguments.get(1));
            arguments.set(1, temp2);
        }
        this.functionName.setColumnName("LOCATE");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") || this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
            final Object temp = this.functionArguments.get(0);
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            Object temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
                String tempString = ((SelectColumn)temp).toString().trim();
                if (tempString.startsWith("'%")) {
                    tempString = (String)(temp = StringFunctions.replaceFirst("'", "'%", tempString));
                }
                if (tempString.endsWith("%'")) {
                    tempString = (String)(temp = tempString.substring(0, tempString.length() - 2) + "'");
                }
            }
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        this.functionName.setColumnName("ANSI_INSTR");
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
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") || this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
            final Object temp = this.functionArguments.get(0);
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            Object temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
                String tempString = ((SelectColumn)temp).toString().trim();
                if (tempString.startsWith("'%")) {
                    tempString = (String)(temp = StringFunctions.replaceFirst("'", "'%", tempString));
                }
                if (tempString.endsWith("%'")) {
                    tempString = (String)(temp = tempString.substring(0, tempString.length() - 2) + "'");
                }
            }
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        this.functionName.setColumnName("INSTR");
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
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") || this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
            final Object temp = this.functionArguments.get(0);
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            Object temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
                String tempString = ((SelectColumn)temp).toString().trim();
                if (tempString.startsWith("'%")) {
                    tempString = (String)(temp = StringFunctions.replaceFirst("'", "'%", tempString));
                }
                if (tempString.endsWith("%'")) {
                    tempString = (String)(temp = tempString.substring(0, tempString.length() - 2) + "'");
                }
            }
            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
        }
        this.functionName.setColumnName("INSTR");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.size() == 2) {
            this.functionName.setColumnName("POSITION");
            final Vector newArguments = new Vector();
            newArguments.add(arguments.lastElement());
            newArguments.add(arguments.firstElement());
            this.setFunctionArguments(newArguments);
            this.setAsDatatype("IN");
        }
        else {
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];
        String qry = "";
        String firstArg = "";
        String secArg = "";
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            argu[i_count] = new StringBuffer();
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                if (i_count <= 1) {
                    sc.convertSelectColumnToTextDataType();
                }
                argu[i_count].append(sc.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                argu[i_count].append(this.functionArguments.elementAt(i_count));
            }
        }
        firstArg = argu[0].toString();
        secArg = argu[1].toString();
        if (this.functionName.getColumnName().equalsIgnoreCase("INSTR")) {
            firstArg = argu[1].toString();
            secArg = argu[0].toString();
        }
        if (this.functionArguments.size() == 2) {
            qry = " position(" + firstArg + "," + secArg + ")";
        }
        else if (this.functionArguments.size() == 3) {
            qry = "if(position(" + firstArg + ",substring(" + secArg + "," + (Object)argu[2] + "))>0,(" + (Object)argu[2] + "+position(" + firstArg + ",substring(" + secArg + "," + (Object)argu[2] + "))-1),0)";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
