package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class substring_between extends FunctionCalls
{
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("ZR_TEXTBETWEEN");
        final Vector arguments = new Vector();
        final Vector finalArgs = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionArguments.size() > 3) {
            final SelectColumn sc_locate_subStr = new SelectColumn();
            final FunctionCalls fnCall_locate_subStr = new FunctionCalls();
            final TableColumn tbCl_locate_subStr = new TableColumn();
            tbCl_locate_subStr.setColumnName("SUBSTRING");
            fnCall_locate_subStr.setFunctionName(tbCl_locate_subStr);
            final Vector vc_locate_subStrIn = new Vector();
            final Vector vc_locate_subStrOut = new Vector();
            vc_locate_subStrIn.addElement(arguments.get(0));
            vc_locate_subStrIn.addElement(arguments.get(3));
            fnCall_locate_subStr.setFunctionArguments(vc_locate_subStrIn);
            vc_locate_subStrOut.addElement(fnCall_locate_subStr);
            sc_locate_subStr.setColumnExpression(vc_locate_subStrOut);
            finalArgs.addElement(sc_locate_subStr);
            finalArgs.addElement(arguments.get(1));
            finalArgs.addElement(arguments.get(2));
            this.setFunctionArguments(finalArgs);
        }
        else {
            this.setFunctionArguments(arguments);
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
        if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            String qry = "";
            qry = "(CASE WHEN STRPOS(CAST(" + arguments.get(0).toString() + " as TEXT), CAST(" + arguments.get(1).toString() + " as TEXT))  >= 1 and  case when (STRPOS(CAST(" + arguments.get(0).toString() + " as TEXT), CAST(" + arguments.get(1).toString() + " as TEXT)) + 1) = 0 then 0 when (STRPOS(SUBSTRING(CAST(" + arguments.get(0).toString() + " as TEXT), (STRPOS(CAST(" + arguments.get(0).toString() + " as TEXT), CAST(" + arguments.get(1).toString() + " as TEXT)) + 1)),CAST(" + arguments.get(2).toString() + " as TEXT)))=0 then 0 else ((STRPOS(SUBSTRING(CAST(" + arguments.get(0).toString() + " as TEXT), (STRPOS(CAST(" + arguments.get(0).toString() + " as TEXT), CAST(" + arguments.get(1).toString() + " as TEXT)) + 1)), CAST(" + arguments.get(2).toString() + " as TEXT))-1)+(STRPOS(CAST(" + arguments.get(0).toString() + " as TEXT), CAST(" + arguments.get(1).toString() + " as TEXT)) + 1)) end  >= 1 THEN substring_index(substring_index(CAST(" + arguments.get(0).toString() + " as TEXT), CAST(" + arguments.get(2).toString() + " as TEXT), (CASE WHEN " + arguments.get(1).toString() + "  = " + arguments.get(2).toString() + " THEN 2 ELSE 1 END)::integer), CAST(" + arguments.get(1).toString() + " as TEXT), -1) ELSE null END)";
            this.functionName.setColumnName(qry);
            this.setFunctionArguments(new Vector());
            this.setOpenBracesForFunctionNameRequired(false);
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
        String qry = "";
        qry = "if(position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR))  >= 1 and if(position(CAST(" + arguments.get(2).toString() + " as VARCHAR),substring(CAST(" + arguments.get(0).toString() + " as VARCHAR),( position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR)) + 1)))>0,(( position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR)) + 1)+position(CAST(" + arguments.get(2).toString() + " as VARCHAR),substring(CAST(" + arguments.get(0).toString() + " as VARCHAR),( position(CAST(" + arguments.get(1).toString() + " as VARCHAR),CAST(" + arguments.get(0).toString() + " as VARCHAR)) + 1)))-1),0)  >= 1, substring_index(substring_index(CAST(" + arguments.get(0).toString() + " as VARCHAR), CAST(" + arguments.get(2).toString() + " as VARCHAR), if(" + arguments.get(1).toString() + "  = " + arguments.get(2).toString() + ", 2, 1)), CAST(" + arguments.get(1).toString() + " as VARCHAR), -1), null)";
        this.functionName.setColumnName(qry);
        this.setFunctionArguments(new Vector());
        this.setOpenBracesForFunctionNameRequired(false);
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String qry = "CASE WHEN CHARINDEX(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ")  >= 1 and CHARINDEX(" + arguments.get(2).toString() + ", " + arguments.get(0).toString() + ", (CHARINDEX(" + arguments.get(1).toString() + ", " + arguments.get(0).toString() + ") + 1))  >= 1  THEN substring_index(substring_index(" + arguments.get(0).toString() + ", " + arguments.get(2).toString() + ", CASE WHEN " + arguments.get(1).toString() + "  = " + arguments.get(2).toString() + "  THEN 2 ELSE 1 END), " + arguments.get(1).toString() + ", -1) ELSE null END";
        this.functionName.setColumnName(qry);
        this.setFunctionArguments(new Vector());
        this.setOpenBracesForFunctionNameRequired(false);
    }
}
