package com.adventnet.swissqlapi.sql.functions.reports;

import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class fiscalYear extends FunctionCalls
{
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyear")) {
            final StringBuffer[] argu = new StringBuffer[3];
            String qry = "";
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                argu[i_count] = new StringBuffer();
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    argu[i_count].append(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    argu[i_count].append(this.functionArguments.elementAt(i_count));
                }
            }
            final String colName = argu[0].toString().replaceAll("\"", "");
            final int yearOffset = argu[2].toString().equals("1") ? 0 : 1;
            final int addMonth = 12 - Integer.parseInt(argu[1].toString()) + 1 - 12 * yearOffset;
            qry = " YEAR(timestamp(" + colName + ") + INTERVAL '1' MONTH * " + addMonth + ")";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyearintrval")) {
            final StringBuffer[] argu = new StringBuffer[2];
            String qry2 = "";
            for (int i_count2 = 0; i_count2 < this.functionArguments.size(); ++i_count2) {
                argu[i_count2] = new StringBuffer();
                if (this.functionArguments.elementAt(i_count2) instanceof SelectColumn) {
                    argu[i_count2].append(this.functionArguments.elementAt(i_count2).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    argu[i_count2].append(this.functionArguments.elementAt(i_count2));
                }
            }
            final String colNameInt = argu[0].toString().replaceAll("\"", "");
            qry2 = "CAST((((MONTH((FROM_UNIXTIME(0) + INTERVAL  '1'  second * (" + colNameInt + " / 1000))) + " + (Object)argu[1] + " -1) / 12) + YEAR((FROM_UNIXTIME(0) + INTERVAL  '1'  second * (" + colNameInt + " / 1000)))) AS INTEGER)";
            this.functionName.setColumnName(qry2);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fyeardt")) {
            final StringBuffer[] argu = new StringBuffer[2];
            String qry2 = "";
            for (int i_count2 = 0; i_count2 < this.functionArguments.size(); ++i_count2) {
                argu[i_count2] = new StringBuffer();
                if (this.functionArguments.elementAt(i_count2) instanceof SelectColumn) {
                    argu[i_count2].append(this.functionArguments.elementAt(i_count2).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    argu[i_count2].append(this.functionArguments.elementAt(i_count2));
                }
            }
            final String colNameDate = argu[0].toString().replaceAll("\"", "");
            qry2 = "CAST((((MONTH(" + colNameDate + ") + " + (Object)argu[1] + " -1) / 12) + YEAR(" + colNameDate + ")) AS INTEGER)";
            this.functionName.setColumnName(qry2);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
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
        if (from_sqs != null && from_sqs.isAmazonRedShift()) {
            String qry = "((( cast(EXTRACT(MONTH FROM " + arguments.get(0).toString() + ") as int) + " + arguments.get(1).toString() + " -1) / 12) + cast(extract (year from  " + arguments.get(0).toString() + " ) as int) )";
            if (this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearIntrval")) {
                qry = "((( cast(EXTRACT(MONTH FROM (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000))) as int) + " + arguments.get(1).toString() + " -1) / 12) + cast(extract (year from  (timestamp '1970-01-01 00:00:00' + INTERVAL  '1'  second * (" + arguments.get(0).toString() + " / 1000)) ) as int) )";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        String qry = "";
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (from_sqs.isMSAzure() && this.functionName.getColumnName().equalsIgnoreCase("ZR_fYearDt")) {
            qry = "(((MONTH(" + arguments.get(0) + ") + " + arguments.get(1) + " - 1)/12) + YEAR(" + arguments.get(0) + "))";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
