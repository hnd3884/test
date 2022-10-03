package com.adventnet.swissqlapi.sql.functions.reports;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class dateTrunc extends FunctionCalls
{
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
        final String query = "CAST((CASE      WHEN (" + arguments.get(0).toString() + "  = 'year') then timestamp('1900-01-01') + interval  '1'  YEAR * TIMESTAMPDIFF(YEAR, '1900-01-01', " + arguments.get(1).toString() + ") + interval  '1'  month * if(MONTH(" + arguments.get(1).toString() + ")  >= " + arguments.get(4).toString() + ", " + arguments.get(4).toString() + " -1, " + arguments.get(4).toString() + " -13)      WHEN (" + arguments.get(0).toString() + "  = 'quarter') then timestamp('1900-01-01') + interval  '1'  month * TIMESTAMPDIFF(month, '1900-01-01', " + arguments.get(1).toString() + ") -interval  '1'  month * (  MOD((MONTH(" + arguments.get(1).toString() + ") -1 + if((  MOD((" + arguments.get(4).toString() + " -1), 3)  )  = 0, 0, if((  MOD((" + arguments.get(4).toString() + " -1), 3)  )  = 1, 2, 1))), 3)  )      WHEN (" + arguments.get(0).toString() + "  = 'month') then timestamp('1900-01-01') + interval  '1'  MONTH * TIMESTAMPDIFF(MONTH, '1900-01-01', " + arguments.get(1).toString() + ")      WHEN (" + arguments.get(0).toString() + "  = 'week') then if(CEILING(((day(timestamp(" + arguments.get(1).toString() + ")-timestamp(cast(CONCAT(if(MONTH(" + arguments.get(1).toString() + ")  < " + arguments.get(4).toString() + ", YEAR(" + arguments.get(1).toString() + ") -1, YEAR(" + arguments.get(1).toString() + ")), '-', " + arguments.get(4).toString() + ", '-01') as date))) + 1 + (  MOD((DAYOFWEEK(cast(CONCAT(if(MONTH(" + arguments.get(1).toString() + ")  < " + arguments.get(4).toString() + ", YEAR(" + arguments.get(1).toString() + ") -1, YEAR(" + arguments.get(1).toString() + ")), '-', " + arguments.get(4).toString() + ", '-01') as date)) + 7 -" + arguments.get(2).toString() + "), 7)  )) * 1.0) / (if(7=0,null,7)*1.0))  = 1 AND " + arguments.get(3).toString() + "  = 2, cast(CONCAT(YEAR(" + arguments.get(1).toString() + "), '-', LPAD(" + arguments.get(4).toString() + ", 2, 0), '-01') as date), timestamp('1900-01-01') + interval  '1'  day * 7 *  TIMESTAMPDIFF(WEEK, '1900-01-01', " + arguments.get(1).toString() + ") + interval  '1'  DAY * if((  MOD((DAYOFWEEK(" + arguments.get(1).toString() + ") + 5), 7)   + 1)  >= ((  MOD((" + arguments.get(2).toString() + " + 5), 7)  ) + 1), ((  MOD((" + arguments.get(2).toString() + " + 5), 7)  ) + 1) -1, -(7 -((  MOD((" + arguments.get(2).toString() + " + 4), 7)  ) + 1))))      WHEN (" + arguments.get(0).toString() + "  = 'day') then timestamp('1900-01-01') + interval  '1'  DAY * TIMESTAMPDIFF(DAY, '1900-01-01', " + arguments.get(1).toString() + ")      WHEN (" + arguments.get(0).toString() + "  = 'hour') then timestamp('1900-01-01') + interval  '1'  HOUR * TIMESTAMPDIFF(HOUR, '1900-01-01', " + arguments.get(1).toString() + ")      WHEN (" + arguments.get(0).toString() + "  = 'minute') then timestamp('1900-01-01') + interval  '1'  MINUTE * TIMESTAMPDIFF(MINUTE, '1900-01-01', " + arguments.get(1).toString() + ")     END) AS DATE)";
        this.functionName.setColumnName(query);
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
            final String qry = "CAST((  CASE      WHEN (" + arguments.get(0).toString() + "  = 'year') then (CAST('1900-01-01' AS TIMESTAMP) +INTERVAL  '1'  YEAR * ROUND((DATEDIFF(year, CAST('1900-01-01' AS TIMESTAMP), " + arguments.get(1).toString() + ")))) + INTERVAL  '1'  month *  case when  cast(EXTRACT(MONTH FROM " + arguments.get(1).toString() + ") as int)  >= " + arguments.get(4).toString() + " then " + arguments.get(4).toString() + " -1 else " + arguments.get(4).toString() + " -13 end       WHEN (" + arguments.get(0).toString() + "  = 'quarter') then (CAST('1900-01-01' AS TIMESTAMP) +INTERVAL  '1'  month * ROUND((DATEDIFF(month, CAST('1900-01-01' AS TIMESTAMP), " + arguments.get(1).toString() + ")))) -INTERVAL  '1'  month * (( cast(EXTRACT(MONTH FROM " + arguments.get(1).toString() + ") as int) -1 +  case when ((" + arguments.get(4).toString() + " -1) % 3)  = 0 then 0 else  case when ((" + arguments.get(4).toString() + " -1) % 3)  = 1 then 2 else 1 end  end ) % 3)      WHEN (" + arguments.get(0).toString() + "  = 'month') then (CAST('1900-01-01' AS TIMESTAMP) +INTERVAL  '1'  MONTH * ROUND((DATEDIFF(month, CAST('1900-01-01' AS TIMESTAMP), " + arguments.get(1).toString() + "))))      WHEN (" + arguments.get(0).toString() + "  = 'week') then  case when CEIL(((DATE_MI((" + arguments.get(1).toString() + "::date), (CAST((case when  cast(EXTRACT(MONTH FROM " + arguments.get(1).toString() + ") as int)  < " + arguments.get(4).toString() + " then cast(extract (year from  " + arguments.get(1).toString() + " ) as int)  -1 else cast(extract (year from  " + arguments.get(1).toString() + " ) as int)  end  || '-' || " + arguments.get(4).toString() + " || '-01') AS DATE)::date)) + 1 + (( cast( EXTRACT (DOW FROM  CAST((case when  cast(EXTRACT(MONTH FROM " + arguments.get(1).toString() + ") as int)  < " + arguments.get(4).toString() + " then cast(extract (year from  " + arguments.get(1).toString() + " ) as int)  -1 else cast(extract (year from  " + arguments.get(1).toString() + " ) as int)  end  || '-' || " + arguments.get(4).toString() + " || '-01') AS DATE)) + 1 as int)  + 7 -" + arguments.get(2).toString() + ") % 7)) * 1.0) / ((case when 7=0 then null else 7 end)*1.0))  = 1 AND " + arguments.get(3).toString() + "  = 2 then CAST((cast(extract (year from  " + arguments.get(1).toString() + " ) as int)  || '-' || LPAD(" + arguments.get(4).toString() + ", 2::integer, 0::text) || '-01') as DATE) else (CAST('1900-01-01' AS TIMESTAMP) +INTERVAL  '1'  day * 7 *  ROUND((DATEDIFF(week, CAST('1900-01-01' AS TIMESTAMP), " + arguments.get(1).toString() + ")))) + INTERVAL  '1'  DAY *  case when (( cast( EXTRACT (DOW FROM  " + arguments.get(1).toString() + ") + 1 as int)  + 5) % 7 + 1)  >= (((" + arguments.get(2).toString() + " + 5) % 7) + 1) then (((" + arguments.get(2).toString() + " + 5) % 7) + 1) -1 else -(7 -(((" + arguments.get(2).toString() + " + 4) % 7) + 1)) end  end       WHEN (" + arguments.get(0).toString() + "  = 'day') then (CAST('1900-01-01' AS TIMESTAMP) +INTERVAL  '1'  DAY * ROUND((DATEDIFF(day, CAST('1900-01-01' AS TIMESTAMP), " + arguments.get(1).toString() + "))))      WHEN (" + arguments.get(0).toString() + "  = 'hour') then (CAST('1900-01-01' AS TIMESTAMP) +INTERVAL  '1'  HOUR * ROUND((DATEDIFF(hour, CAST('1900-01-01' AS TIMESTAMP), " + arguments.get(1).toString() + "))))      WHEN (" + arguments.get(0).toString() + "  = 'minute') then (CAST('1900-01-01' AS TIMESTAMP) +INTERVAL  '1'  MINUTE * ROUND((DATEDIFF(minute, CAST('1900-01-01' AS TIMESTAMP), " + arguments.get(1).toString() + "))))     END) AS DATE)";
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
        final String vInterval = arguments.get(0).toString();
        final String vDate = arguments.get(1).toString();
        final String weekStartDay = arguments.get(2).toString();
        final String weekMode = arguments.get(3).toString();
        final String startMonth = arguments.get(4).toString();
        String query = "";
        if (vInterval.equalsIgnoreCase("'year'")) {
            query = " DATEADD(MONTH,CASE WHEN MONTH(" + vDate + ") >= " + startMonth + " THEN " + startMonth + " -1 ELSE " + startMonth + " -13 END, CAST(DATEADD(YEAR, DATEDIFF(YEAR, CAST( '1900-01-01' AS DATETIME), CAST( " + vDate + " AS DATETIME)), CAST('1900-01-01' AS DATETIME )) AS DATETIME ))";
        }
        else if (vInterval.equalsIgnoreCase("'quarter'")) {
            query = "DATEADD(MONTH,-((MONTH(" + vDate + ") -1 + CASE WHEN (" + startMonth + " -1) % 3 = 0  THEN 0  ELSE CASE WHEN ( " + startMonth + " -1) % 3 = 1  THEN 2  ELSE 1  END END) % 3),DATEADD(MONTH, DATEDIFF(MONTH, CAST( '1900-01-01' AS DATETIME), CAST(" + vDate + " AS DATETIME)), CAST('1900-01-01' AS DATETIME )))";
        }
        else if (vInterval.equalsIgnoreCase("'month'")) {
            query = " DATEADD(MONTH, DATEDIFF(MONTH, CAST( '1900-01-01' AS DATETIME), CAST( " + vDate + " AS DATETIME)), CAST('1900-01-01' AS DATETIME ))";
        }
        else if (vInterval.equalsIgnoreCase("'week'")) {
            query = "SELECT CASE WHEN CEILING(((DATEDIFF(dd, " + vDate + ", CAST((CASE WHEN MONTH(" + vDate + ")  < " + startMonth + "  THEN YEAR(" + vDate + ") -1 ELSE YEAR(" + vDate + ")  END + '-' + " + startMonth + " + '-01') AS DATETIME)) + 1 + (DATEPART(dw, CAST((CASE WHEN MONTH(" + vDate + ")  < " + startMonth + "  THEN YEAR(" + vDate + ") -1 ELSE YEAR(" + vDate + ") END + '-' + " + startMonth + " + '-01') AS DATETIME)) + 7 -" + weekStartDay + ") % 7) * 1.0) / CONVERT(FLOAT, 7))  = 1 AND " + weekMode + "  = 2  THEN CAST((YEAR(" + vDate + ") + '-' + CASE WHEN LEN(" + startMonth + ") >= 2 THEN SUBSTRING('" + startMonth + "',1,2) ELSE SUBSTRING(REPLICATE(0,2),1,2-LEN(" + startMonth + ")) + " + startMonth + " END + '-01') as DATETIME) ELSE DATEADD(DAY, CASE WHEN ((DATEPART(dw, " + vDate + ") + 5) % 7 + 1)  >= ((" + weekStartDay + " + 5) % 7 + 1)  THEN ((" + weekStartDay + " + 5) % 7 + 1) -1 ELSE -(7 -((" + weekStartDay + " + 4) % 7 + 1)) END, CAST(DATEADD(WEEK, DATEDIFF(WEEK, CAST( '1900-01-01' AS DATETIME), CAST( " + vDate + " AS DATETIME)), CAST('1900-01-01' AS DATETIME )) AS DATETIME )) END ";
        }
        else if (vInterval.equalsIgnoreCase("'day'")) {
            query = " DATEADD(DAY, DATEDIFF(DAY, CAST( '1900-01-01' AS DATETIME), CAST(" + vDate + " AS DATETIME)), CAST('1900-01-01' AS DATETIME ))";
        }
        else if (vInterval.equalsIgnoreCase("'hour'")) {
            query = " DATEADD(HOUR, DATEDIFF(HOUR, CAST( '1900-01-01' AS DATETIME), CAST( " + vDate + " AS DATETIME)), CAST('1900-01-01' AS DATETIME )) ";
        }
        else if (vInterval.equalsIgnoreCase("'minute'")) {
            query = " DATEADD(MINUTE, DATEDIFF(MINUTE, CAST( '1900-01-01' AS DATETIME), CAST( " + vDate + " AS DATETIME)), CAST('1900-01-01' AS DATETIME )) ";
        }
        this.functionName.setColumnName(query);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
}
