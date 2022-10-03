package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class MakeDate extends FunctionCalls
{
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String[] arguments = new String[this.functionArguments.size()];
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments[i_count] = "" + this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs);
            }
            else {
                arguments[i_count] = "" + this.functionArguments.elementAt(i_count);
            }
        }
        String functionChange = "";
        if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
            functionChange = "((str_to_date(concat(" + arguments[0] + ",'-01-01'),'%Y-%m-%d')+interval '1' day * (" + arguments[1] + ")) - interval '1' day)";
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("period_add")) {
            functionChange = "CAST(DATE_FORMAT(TIMESTAMPADD(MONTH," + arguments[1] + ", (CAST(concat(left(case when lpad(concat(''," + arguments[0] + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments[0] + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments[0] + "),6,'0'),4)) else lpad(concat(''," + arguments[0] + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (integer(right(lpad(concat(''," + arguments[0] + "),6,'0'),2)) -1)) ),'%Y%m') AS INTEGER)";
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("period_diff")) {
            functionChange = "TIMESTAMPDIFF(MONTH,(CAST(concat(left(case when lpad(concat(''," + arguments[1] + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments[1] + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments[1] + "),6,'0'),4)) else lpad(concat(''," + arguments[1] + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (integer(right(lpad(concat(''," + arguments[1] + "),6,'0'),2)) -1)), (CAST(concat(left(case when lpad(concat(''," + arguments[0] + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments[0] + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments[0] + "),6,'0'),4)) else lpad(concat(''," + arguments[0] + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (integer(right(lpad(concat(''," + arguments[0] + "),6,'0'),2)) -1)) )";
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("MAKETIME")) {
            functionChange = "CONCAT(" + arguments[0] + ",':',IF((" + arguments[1] + ") BETWEEN 0 AND 59 , (" + arguments[1] + "), NULL),':',IF((" + arguments[2] + ") BETWEEN 0 AND 59 , (" + arguments[2] + "), NULL))";
        }
        this.functionName.setColumnName(functionChange);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
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
            String qry = "";
            if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
                qry = "(CASE WHEN " + arguments.get(0).toString() + " >= 0 AND " + arguments.get(0).toString() + " <= 9999 AND " + arguments.get(1).toString() + " >= 0 THEN (TO_TIMESTAMP(CAST(" + arguments.get(0).toString() + " AS TEXT) || '-01-01'::text, (LEFT('YYYY'::text,LENGTH(" + arguments.get(0).toString() + ")) || '-MM-DD'::text)) + (" + arguments.get(1).toString() + " -1) * Interval '1 day') ELSE NULL END)";
            }
            else if (this.functionName.getColumnName().equalsIgnoreCase("PERIOD_DIFF")) {
                qry = "DATEDIFF(MONTH, DATE(CAST(concat(left(case when lpad(concat(''," + arguments.get(1).toString() + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments.get(1).toString() + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments.get(1).toString() + "),6,'0'),4)) else lpad(concat(''," + arguments.get(1).toString() + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (CAST(right(lpad(concat(''," + arguments.get(1).toString() + "),6,'0'),2) AS INTEGER) -1)),DATE(CAST(concat(left(case when lpad(concat(''," + arguments.get(0).toString() + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),4)) else lpad(concat(''," + arguments.get(0).toString() + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (CAST(right(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),2) AS INTEGER) -1)) )";
            }
            else if (this.functionName.getColumnName().equalsIgnoreCase("PERIOD_ADD")) {
                qry = "CAST(TO_CHAR( (DATE(CAST(concat(left(case when lpad(concat(''," + arguments.get(0).toString() + "),6,'0') like '00%' then concat((case when cast(substr(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),3,2) as integer) < 70 then '20' else '19' end),right(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),4)) else lpad(concat(''," + arguments.get(0).toString() + "),6,'0') end, 4), '-01-01') AS DATE) + interval '1' month * (CAST(right(lpad(concat(''," + arguments.get(0).toString() + "),6,'0'),2) AS INTEGER) -1)) + (interval '1 month' * " + arguments.get(1).toString() + ")), 'YYYYMM') AS INTEGER)";
            }
            else if (this.functionName.getColumnName().equalsIgnoreCase("MAKETIME")) {
                qry = "(" + arguments.get(0).toString() + " || ':' || (CASE WHEN (" + arguments.get(1).toString() + ") BETWEEN 0 AND 59 THEN (" + arguments.get(1).toString() + ") ELSE NULL END) || ':' || (CASE WHEN (" + arguments.get(2).toString() + ") BETWEEN 0 AND 59 THEN (" + arguments.get(2).toString() + ") ELSE NULL END))";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
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
        String qry = "";
        if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
            qry = " DATEADD(d,CAST(" + Integer.parseInt(arguments.get(1).toString()) + " AS BIGINT)-1 ,'" + arguments.get(0).toString() + "-01-01')";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "";
        if (this.functionName.getColumnName().equalsIgnoreCase("MAKEDATE")) {
            if (!(arguments.elementAt(1) instanceof String) && arguments.elementAt(0) instanceof String) {
                throw new ConvertException("Invalid Argument Value for Function " + this.functionName.getColumnName() + "", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { this.functionName.getColumnName() });
            }
            String year = arguments.get(0).toString();
            year = year.replaceAll("'", "");
            String days = arguments.get(1).toString();
            days = days.replaceAll("'", "");
            qry = "(to_date('" + year + "-01-01','YYYY-MM-DD')+ " + days + " - 1)";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
