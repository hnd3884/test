package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class week extends FunctionCalls
{
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("week") || this.functionName.getColumnName().equalsIgnoreCase("yearweek") || this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
            final Vector arguments = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    if (i_count == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i_count, true);
                    }
                    arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            if (arguments.size() < 2 && this.functionName.getColumnName().equalsIgnoreCase("week")) {
                final Integer arg2 = new Integer(0);
                arguments.addElement(arg2);
            }
            this.setFunctionArguments(arguments);
            if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
                this.functionName.setColumnName("yearweek");
            }
            else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
                this.functionName.setColumnName("week_iso");
            }
            else {
                this.functionName.setColumnName("week");
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("addtime") || this.functionName.getColumnName().equalsIgnoreCase("subtime")) {
            final StringBuffer[] argument = new StringBuffer[2];
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                argument[i] = new StringBuffer();
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    this.handleStringLiteralForTime(from_sqs, i, i == 0, false);
                    argument[i].append(this.functionArguments.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    argument[i].append(this.functionArguments.elementAt(i));
                }
            }
            final String timevalue = "interval '1' second * (TIMESTAMPDIFF(SECOND,timestamp(time('00:00:00')),timestamp(time(" + (Object)argument[1] + "))))";
            if (this.functionName.getColumnName().equalsIgnoreCase("addtime")) {
                this.functionName.setColumnName((Object)argument[0] + "+" + timevalue);
            }
            else {
                this.functionName.setColumnName((Object)argument[0] + "-" + timevalue);
            }
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz") || this.functionName.getColumnName().equalsIgnoreCase("convert_timezone") || this.functionName.getColumnName().equalsIgnoreCase("converttimezone")) {
            final StringBuffer[] argument = new StringBuffer[3];
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                argument[i] = new StringBuffer();
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    if (i == 0) {
                        this.handleStringLiteralForDateTime(from_sqs, i, true);
                    }
                    argument[i].append(this.functionArguments.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    argument[i].append(this.functionArguments.elementAt(i));
                }
            }
            int finalMins = 0;
            try {
                if (argument.length == 3 && argument[1] != null && argument[2] != null) {
                    final String[] currentTZ = argument[1].toString().replaceAll("'", "").split(":");
                    final String[] toTZ = argument[2].toString().replaceAll("'", "").split(":");
                    int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
                    final int currentTZMin = (currentTZ.length == 2) ? Integer.parseInt(currentTZ[1]) : 0;
                    int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
                    final int toTZMin = (toTZ.length == 2) ? Integer.parseInt(toTZ[1]) : 0;
                    final int toTZOverallMins = toTZHour * 60 + toTZMin;
                    final int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
                    if (currentTZ[0].contains("-")) {
                        currentTZHour *= -1;
                    }
                    if (toTZ[0].contains("-")) {
                        toTZHour *= -1;
                    }
                    if (currentTZHour < 0) {
                        if (toTZHour < 0) {
                            finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                        }
                        else {
                            finalMins = toTZOverallMins - currentTZOverallMins * -1;
                        }
                    }
                    else if (toTZHour < 0) {
                        finalMins = toTZOverallMins * -1 - currentTZOverallMins;
                    }
                    else {
                        finalMins = toTZOverallMins - currentTZOverallMins;
                    }
                }
            }
            catch (final Exception ex) {}
            this.functionName.setColumnName("TIMESTAMPADD(MINUTE," + finalMins + "," + (Object)argument[0] + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        String qry = "";
        final Vector arguments = new Vector();
        final boolean canUseUDFFunction = from_sqs != null && !from_sqs.isAmazonRedShift() && from_sqs.canUseUDFFunctionsForDateTime();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                if (i_count == 0 && this.functionArguments.elementAt(0).getColumnExpression().size() == 1 && this.functionArguments.elementAt(0).getColumnExpression().get(0) instanceof String) {
                    String dateString = this.functionArguments.elementAt(0).getColumnExpression().get(0).toString();
                    dateString = "CAST(" + this.handleStringLiteralForDateTime(dateString, from_sqs) + " AS DATE)";
                    this.functionArguments.elementAt(0).getColumnExpression().set(0, dateString);
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final String weekQry = "CAST((CASE      WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-01-01')::date) as int) +6, 7) + 4), 7) -3) * - 1  >=  cast(date_part('doy' ," + arguments.get(0) + ") as int) THEN CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  -1 || '-01-01')::date) as int) +6, 7) + 4), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  -1 || '-12-31')::date) as int) +  cast(date_part('doy' ," + arguments.get(0) + ") as int)) / 7.0)      ELSE       CASE        WHEN ( cast(date_part('doy' ," + arguments.get(0) + ") as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-01-01')::date) as int) +6, 7) + 4), 7) -3))  > 364 THEN         CASE          WHEN (( cast(date_part('doy' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-12-31')::date) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-01-01')::date) as int) +6, 7) + 4), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( cast(date_part('doy' ," + arguments.get(0) + ") as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  " + arguments.get(0) + " ) as int)  || '-01-01')::date) as int) +6, 7) + 4), 7) -3)) / 7.0)       END     END) as int)";
        if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
            if (arguments.size() < 2 || arguments.get(1).toString().equals("6")) {
                qry = "ZR_WeekDtNwkStrtDay(DATE(" + arguments.get(0) + "),4)";
                if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                    qry = "CAST((CASE      WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) * - 1  >=  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) THEN CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int)) / ((case when 7=0 then null else 7 end)*1.0))      ELSE       CASE        WHEN ( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3))  > 364 THEN         CASE          WHEN (( cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) -364)  > 3 THEN 53          ELSE 1         END        ELSE CEIL(( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))       END     END) AS INTEGER)";
                }
            }
            else {
                qry = " cast(extract(week from " + qry + arguments.get(0) + ") as int) ";
                if (canUseUDFFunction) {
                    qry = "WEEK(" + arguments.get(0).toString() + ")";
                }
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
            qry = " cast(extract(week from  " + qry + arguments.get(0) + ") as int) ";
            if (canUseUDFFunction) {
                qry = "WEEKOFYEAR(" + arguments.get(0).toString() + ")";
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
            if (arguments.size() < 2 || arguments.get(1).toString().equals("6")) {
                qry = "ZR_WeekYearDtNwkStrtDay(DATE(" + arguments.get(0) + "),4)";
                if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                    qry = "cast(  CASE    WHEN (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) * - 1  >=  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) THEN (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || SUBSTRING((100 + CEIL(((MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3) +  cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  -1 || '-12-31')::DATE) as int) +  cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int)) / ((case when 7=0 then null else 7 end)*1.0))),2))    ELSE     CASE      WHEN ( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3))  > 364 THEN       CASE        WHEN (( cast(date_part('doy' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-12-31')::DATE) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) -364)  > 3 THEN (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '53')        ELSE (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  + 1 || '01')       END      ELSE (cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || SUBSTRING((100 + CEIL(( cast(date_part('doy' ,CAST(" + arguments.get(0).toString() + " AS DATE)) as int) + (MOD(( mod(cast(date_part('dow' ,(cast(extract (year from  CAST(" + arguments.get(0).toString() + " AS DATE) ) as int)  || '-01-01')::DATE) as int) +6, 7) + 4), 7) -3)) / ((case when 7=0 then null else 7 end)*1.0))),2))     END   END as INTEGER)";
                }
            }
            else {
                qry = "cast(to_char(DATE(" + arguments.get(0) + "),'IYYYIW') as integer)";
            }
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
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
        final String dateArg = arguments.get(0).toString();
        String qry = "";
        if (this.functionName.getColumnName().equalsIgnoreCase("week")) {
            if (arguments.size() < 2 || arguments.get(1).toString().equals("6")) {
                qry = "CASE  WHEN (((((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4)) % ( 7 )) -3) * - 1  >= DATEPART(dy, " + dateArg + ") THEN CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + 4) ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7))      ELSE       CASE        WHEN ((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3))  > 364) THEN         CASE          WHEN (((DATEPART(dy, concat(YEAR(" + dateArg + "), '-12-31')) + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) -364)  > 3) THEN 53          ELSE 1         END        ELSE CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / CONVERT(FLOAT, 7)) END END";
            }
            else {
                qry = "DATEPART(wk," + dateArg + ")";
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("weekofyear")) {
            qry = "DATEPART(wk," + dateArg + ")";
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("yearweek")) {
            if (arguments.size() < 2 || arguments.get(1).toString().equals("6")) {
                qry = "CAST(CASE  WHEN (((((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4)) % ( 7 )) -3) * - 1  >= DATEPART(dy, " + dateArg + ") THEN CONCAT(YEAR(" + dateArg + ") -1, SUBSTRING(Cast((100 + CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + 4) ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7)))AS VARCHAR), 2, LEN((100 + CEILING((((( ((datepart(dw,concat(YEAR(" + dateArg + ") -1, '-01-01'))+6)%7 + 4) ) % ( 7 )) -3) + DATEPART(dy, concat(YEAR(" + dateArg + ") -1, '-12-31')) + DATEPART(dy, " + dateArg + ")) / CONVERT(FLOAT, 7)))) -1))      ELSE       CASE        WHEN ((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3))  > 364) THEN CASE  WHEN (((DATEPART(dy, concat(YEAR(" + dateArg + "), '-12-31')) + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) -364)  > 3) THEN CONCAT(YEAR(" + dateArg + "), '53')  ELSE CONCAT(YEAR(" + dateArg + ") + 1, '01') END    ELSE CONCAT(YEAR(" + dateArg + "), SUBSTRING(CAST((100 + CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / CONVERT(FLOAT, 7)))AS VARCHAR), 2, LEN((100 + CEILING((DATEPART(dy, " + dateArg + ") + ((( ((datepart(dw,concat(YEAR(" + dateArg + "), '-01-01'))+6)%7 + 4) ) % ( 7 )) -3)) / CONVERT(FLOAT, 7)))) -1))       END     END as BIGINT)";
            }
            else {
                qry = "concat(datepart(yyyy," + arguments.get(0).toString() + "),datepart(wk," + arguments.get(0).toString() + "))";
            }
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("convert_tz")) {
            int finalMins = 0;
            try {
                if (arguments.size() == 3 && arguments.get(1) != null && arguments.get(2) != null && arguments.get(1).toString().contains(":") && arguments.get(2).toString().contains(":")) {
                    final String[] currentTZ = arguments.get(1).toString().replaceAll("'", "").split(":");
                    final String[] toTZ = arguments.get(2).toString().replaceAll("'", "").split(":");
                    int currentTZHour = Integer.parseInt(currentTZ[0].replace("-", "").replace("+", ""));
                    final int currentTZMin = Integer.parseInt(currentTZ[1]);
                    int toTZHour = Integer.parseInt(toTZ[0].replace("-", "").replace("+", ""));
                    final int toTZMin = Integer.parseInt(toTZ[1]);
                    final int toTZOverallMins = toTZHour * 60 + toTZMin;
                    final int currentTZOverallMins = currentTZHour * 60 + currentTZMin;
                    if (currentTZ[0].contains("-")) {
                        currentTZHour *= -1;
                    }
                    if (toTZ[0].contains("-")) {
                        toTZHour *= -1;
                    }
                    if (currentTZHour < 0) {
                        if (toTZHour < 0) {
                            finalMins = toTZOverallMins * -1 - currentTZOverallMins * -1;
                        }
                        else {
                            finalMins = toTZOverallMins - currentTZOverallMins * -1;
                        }
                    }
                    else if (toTZHour < 0) {
                        finalMins = toTZOverallMins * -1 - currentTZOverallMins;
                    }
                    else {
                        finalMins = toTZOverallMins - currentTZOverallMins;
                    }
                }
            }
            catch (final Exception ex) {}
            qry = "DATEADD(mi," + finalMins + ",Cast(" + arguments.get(0).toString() + " AS DATETIME))";
        }
        this.functionName.setColumnName(qry);
        this.setOpenBracesForFunctionNameRequired(false);
        this.functionArguments = new Vector();
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final String fnStr = this.functionName.getColumnName().toUpperCase();
        if (fnStr.equalsIgnoreCase("weekofyear")) {
            final Vector vector = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    vector.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    vector.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            String weekStartDay = "1";
            String weekMode = "1";
            String fiscalStartMonth = "1";
            int fiscalStartMonth_int = 0;
            final int weekMode_int = 0;
            final int weekStartDay_int = 0;
            boolean isJanFiscalStMonth = false;
            boolean isISOWeekMode = false;
            if (vector.size() == 4) {
                if (vector.elementAt(3) instanceof SelectColumn) {
                    final SelectColumn sc = vector.elementAt(3);
                    final Vector vc = sc.getColumnExpression();
                    if (!(vc.elementAt(0) instanceof String)) {
                        throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "WEEKOFYEAR", "FISCAL_START_MONTH" });
                    }
                    fiscalStartMonth = vc.elementAt(0);
                    if (fiscalStartMonth.equalsIgnoreCase("null")) {
                        fiscalStartMonth = "1";
                    }
                    fiscalStartMonth = fiscalStartMonth.replaceAll("'", "");
                    this.validateFiscalStartMonth(fiscalStartMonth, fnStr);
                }
                if (fiscalStartMonth.equals("1")) {
                    isJanFiscalStMonth = true;
                }
                else {
                    fiscalStartMonth_int = Integer.parseInt(fiscalStartMonth);
                    if (vector.elementAt(2) instanceof SelectColumn) {
                        final SelectColumn sc = vector.elementAt(2);
                        final Vector vc = sc.getColumnExpression();
                        if (!(vc.elementAt(0) instanceof String)) {
                            throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "WEEKOFYEAR", "WEEK_MODE" });
                        }
                        weekMode = vc.elementAt(0);
                        if (weekMode.equalsIgnoreCase("null")) {
                            weekMode = "1";
                        }
                        weekMode = weekMode.replaceAll("'", "");
                        this.validateWeekMode(weekMode, fnStr);
                    }
                    if (weekMode.equals("2")) {
                        this.WeekMode2FiscalWeeKQuery(to_sqs, from_sqs, fiscalStartMonth);
                    }
                    else {
                        if (vector.elementAt(1) instanceof SelectColumn) {
                            final SelectColumn sc = vector.elementAt(1);
                            final Vector vc = sc.getColumnExpression();
                            if (!(vc.elementAt(0) instanceof String)) {
                                throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "WEEKOFYEAR", "WEEK_START_DAY" });
                            }
                            weekStartDay = vc.elementAt(0);
                            if (weekStartDay.equalsIgnoreCase("null")) {
                                weekStartDay = "1";
                            }
                            weekStartDay = weekStartDay.replaceAll("'", "");
                            this.validateWeek_Start_Day(weekStartDay, fnStr);
                        }
                        this.WeekMode1Args4(vector, weekStartDay, fiscalStartMonth);
                    }
                }
            }
            if (vector.size() == 3 || isJanFiscalStMonth) {
                if (vector.elementAt(2) instanceof SelectColumn) {
                    final SelectColumn sc = vector.elementAt(2);
                    final Vector vc = sc.getColumnExpression();
                    if (!(vc.elementAt(0) instanceof String)) {
                        throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "WEEKOFYEAR", "WEEK_MODE" });
                    }
                    weekMode = vc.elementAt(0);
                    if (weekMode.equalsIgnoreCase("null")) {
                        weekMode = "1";
                    }
                    weekMode = weekMode.replaceAll("'", "");
                    this.validateWeekMode(weekMode, fnStr);
                }
                if (weekMode.equals("2")) {
                    this.WeekMode2WeeKQuery(to_sqs, from_sqs);
                }
                else {
                    isISOWeekMode = true;
                }
            }
            if (vector.size() == 2 || isISOWeekMode) {
                if (vector.elementAt(1) instanceof SelectColumn) {
                    final SelectColumn sc = vector.elementAt(1);
                    final Vector vc = sc.getColumnExpression();
                    if (!(vc.elementAt(0) instanceof String)) {
                        throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "WEEKOFYEAR", "WEEK_START_DAY" });
                    }
                    weekStartDay = vc.elementAt(0);
                    if (weekStartDay.equalsIgnoreCase("null")) {
                        weekStartDay = "1";
                    }
                    weekStartDay = weekStartDay.replaceAll("'", "");
                    this.validateWeek_Start_Day(weekStartDay, fnStr);
                }
                this.WeekMode1Args2(vector, weekStartDay);
            }
            if (vector.size() == 1) {
                this.WeekMode1Args2(vector, "1");
            }
        }
    }
    
    public void WeekMode1Args2(final Vector vector, String weekStDay) {
        int weekStDay_int = Integer.parseInt(weekStDay);
        final Vector vc_week = new Vector();
        vc_week.addElement(vector.get(0));
        if (weekStDay_int == 1) {
            this.functionName.setColumnName("WEEK");
            vc_week.addElement("6");
        }
        else if (weekStDay_int == 2) {
            this.functionName.setColumnName("WEEK");
            vc_week.addElement("3");
        }
        else if (weekStDay_int > 2 && weekStDay_int < 8) {
            this.functionName.setColumnName("ZR_WEEKDTNWKSTRTDAY");
            weekStDay_int = FunctionCalls.getWeekStartDayValue(1, weekStDay_int);
            weekStDay = Integer.toString(weekStDay_int);
            vc_week.addElement(weekStDay);
        }
        this.setFunctionArguments(vc_week);
    }
    
    public void WeekMode1Args4(final Vector vector, String weekStDay, final String fiscalStartMonth) {
        final Vector vc_fiscalWeek = new Vector();
        vc_fiscalWeek.addElement(vector.get(0));
        vc_fiscalWeek.addElement(fiscalStartMonth);
        final int startMonth = Integer.parseInt(fiscalStartMonth);
        final String startDate = (startMonth < 10) ? ("'-0" + startMonth + "-01'") : ("'-" + startMonth + "-01'");
        vc_fiscalWeek.addElement(startDate);
        final int fiscalStartMonth_int = Integer.parseInt(fiscalStartMonth);
        if (weekStDay.equalsIgnoreCase("0")) {
            this.functionName.setColumnName("ZR_FWEEKDT");
        }
        else {
            this.functionName.setColumnName("ZR_FWEEKDTNWKSTRTDAY");
            int weekStDay_int = Integer.parseInt(weekStDay);
            weekStDay_int = FunctionCalls.getWeekStartDayValue(fiscalStartMonth_int, weekStDay_int);
            weekStDay = Integer.toString(weekStDay_int);
            vc_fiscalWeek.addElement(weekStDay);
        }
        this.setFunctionArguments(vc_fiscalWeek);
    }
    
    public void WeekMode2WeeKQuery(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector vector1 = new Vector();
        final Vector vector2 = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector1.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector1.addElement(this.functionArguments.elementAt(i_count));
                vector2.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String weekStartDay = null;
        if (vector1.elementAt(1) instanceof SelectColumn) {
            final SelectColumn sc = vector1.elementAt(1);
            final Vector vc = sc.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
                throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "WEEKOFYEAR", "WEEK_START_DAY" });
            }
            weekStartDay = vc.elementAt(0);
        }
        if (weekStartDay.equalsIgnoreCase("null")) {
            weekStartDay = "1";
        }
        weekStartDay = weekStartDay.replaceAll("'", "");
        this.validateWeek_Start_Day(weekStartDay, "WEEKOFYEAR");
        this.functionName.setColumnName("");
        final Vector fnArguments = new Vector();
        final SelectColumn sc_ceil = new SelectColumn();
        final FunctionCalls fnCl_ceil = new FunctionCalls();
        final TableColumn tbCl_ceil = new TableColumn();
        tbCl_ceil.setColumnName("CEIL");
        fnCl_ceil.setFunctionName(tbCl_ceil);
        final Vector vc_ceilIn = new Vector();
        final Vector vc_ceilOut = new Vector();
        final SelectColumn sc_ceilArgsDividend = new SelectColumn();
        final Vector vc_ceilArgsDividend = new Vector();
        final SelectColumn sc_ceilArgsMultiplicand = new SelectColumn();
        final Vector vc_ceilArgsMultiplicand = new Vector();
        final SelectColumn sc_partsofMultiplicand = new SelectColumn();
        final Vector vc_partsofMultiplicand = new Vector();
        final SelectColumn sc_ceilArgsAddend = new SelectColumn();
        final FunctionCalls fn_ceilArgsAddend = new FunctionCalls();
        final TableColumn tb_ceilArgsAddend = new TableColumn();
        tb_ceilArgsAddend.setColumnName("DAYOFYEAR");
        fn_ceilArgsAddend.setFunctionName(tb_ceilArgsAddend);
        final Vector vc_ceilArgsAddendIn = new Vector();
        vc_ceilArgsAddendIn.addElement(vector1.get(0));
        final Vector vc_ceilArgsAddendOut = new Vector();
        fn_ceilArgsAddend.setFunctionArguments(vc_ceilArgsAddendIn);
        vc_ceilArgsAddendOut.addElement(fn_ceilArgsAddend);
        sc_ceilArgsAddend.setColumnExpression(vc_ceilArgsAddendOut);
        final SelectColumn sc_ceilArgsAdded = new SelectColumn();
        final Vector vc_ceilArgsAdded = new Vector();
        final SelectColumn sc_ceilArgsAddedDividend = new SelectColumn();
        final Vector vc_ceilArgsAddedDividend = new Vector();
        final SelectColumn sc_dayofweek = new SelectColumn();
        final FunctionCalls fn_dayofweek = new FunctionCalls();
        final TableColumn tb_dayofweek = new TableColumn();
        tb_dayofweek.setColumnName("DAYOFWEEK");
        fn_dayofweek.setFunctionName(tb_dayofweek);
        final Vector vc_dayofweekIn = new Vector();
        final Vector vc_dayofweekOut = new Vector();
        final SelectColumn sc_strtodate = new SelectColumn();
        final FunctionCalls fn_strtodate = new FunctionCalls();
        final TableColumn tb_strtodate = new TableColumn();
        tb_strtodate.setColumnName("STR_TO_DATE");
        fn_strtodate.setFunctionName(tb_strtodate);
        final Vector vc_strtodateIn = new Vector();
        final Vector vc_strtodateOut = new Vector();
        final SelectColumn sc_concat = new SelectColumn();
        final FunctionCalls fn_concat = new FunctionCalls();
        final TableColumn tb_concat = new TableColumn();
        tb_concat.setColumnName("CONCAT");
        fn_concat.setFunctionName(tb_concat);
        final Vector vc_concatIn = new Vector();
        final Vector vc_concatOut = new Vector();
        final SelectColumn sc_yearInCeilArgs = new SelectColumn();
        final FunctionCalls fn_yearInCeilArgs = new FunctionCalls();
        final TableColumn tb_yearInCeilArgs = new TableColumn();
        tb_yearInCeilArgs.setColumnName("YEAR");
        fn_yearInCeilArgs.setFunctionName(tb_yearInCeilArgs);
        final Vector vc_yearInCeilArgsIn = new Vector();
        final Vector vc_yearInCeilArgsOut = new Vector();
        vc_yearInCeilArgsIn.addElement(vector2.get(0));
        fn_yearInCeilArgs.setFunctionArguments(vc_yearInCeilArgsIn);
        vc_yearInCeilArgsOut.addElement(fn_yearInCeilArgs);
        sc_yearInCeilArgs.setColumnExpression(vc_yearInCeilArgsOut);
        vc_concatIn.addElement(sc_yearInCeilArgs);
        vc_concatIn.addElement("'-01-01'");
        fn_concat.setFunctionArguments(vc_concatIn);
        vc_concatOut.addElement(fn_concat);
        sc_concat.setColumnExpression(vc_concatOut);
        vc_strtodateIn.addElement(sc_concat);
        vc_strtodateIn.addElement("'%Y-%m-%d'");
        fn_strtodate.setFunctionArguments(vc_strtodateIn);
        vc_strtodateOut.addElement(fn_strtodate);
        sc_strtodate.setColumnExpression(vc_strtodateOut);
        vc_dayofweekIn.addElement(sc_strtodate);
        fn_dayofweek.setFunctionArguments(vc_dayofweekIn);
        vc_dayofweekOut.addElement(fn_dayofweek);
        sc_dayofweek.setColumnExpression(vc_dayofweekOut);
        vc_ceilArgsAddedDividend.addElement(sc_dayofweek);
        vc_ceilArgsAddedDividend.addElement("+");
        vc_ceilArgsAddedDividend.addElement("7");
        vc_ceilArgsAddedDividend.addElement("-");
        vc_ceilArgsAddedDividend.addElement(weekStartDay);
        sc_ceilArgsAddedDividend.setColumnExpression(vc_ceilArgsAddedDividend);
        vc_ceilArgsAdded.addElement("(");
        vc_ceilArgsAdded.addElement(sc_ceilArgsAddedDividend);
        vc_ceilArgsAdded.addElement(")");
        vc_ceilArgsAdded.addElement("%");
        vc_ceilArgsAdded.addElement("7");
        sc_ceilArgsAdded.setColumnExpression(vc_ceilArgsAdded);
        vc_partsofMultiplicand.addElement(sc_ceilArgsAddend);
        vc_partsofMultiplicand.addElement("+");
        vc_partsofMultiplicand.addElement(sc_ceilArgsAdded);
        sc_partsofMultiplicand.setColumnExpression(vc_partsofMultiplicand);
        vc_ceilArgsMultiplicand.addElement("(");
        vc_ceilArgsMultiplicand.addElement(sc_partsofMultiplicand);
        vc_ceilArgsMultiplicand.addElement(")");
        vc_ceilArgsMultiplicand.addElement("*");
        vc_ceilArgsMultiplicand.addElement("1.0");
        sc_ceilArgsMultiplicand.setColumnExpression(vc_ceilArgsMultiplicand);
        vc_ceilArgsDividend.addElement("(");
        vc_ceilArgsDividend.addElement(sc_ceilArgsMultiplicand);
        vc_ceilArgsDividend.addElement(")");
        vc_ceilArgsDividend.addElement("/");
        vc_ceilArgsDividend.addElement("7");
        sc_ceilArgsDividend.setColumnExpression(vc_ceilArgsDividend);
        vc_ceilIn.addElement(sc_ceilArgsDividend);
        fnCl_ceil.setFunctionArguments(vc_ceilIn);
        vc_ceilOut.addElement(fnCl_ceil);
        sc_ceil.setColumnExpression(vc_ceilOut);
        fnArguments.addElement(sc_ceil);
        this.setFunctionArguments(fnArguments);
    }
    
    public void WeekMode2FiscalWeeKQuery(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final String fiscalStartMonth) throws ConvertException {
        final Vector vector = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String weekStartDay = "";
        if (vector.elementAt(1) instanceof SelectColumn) {
            final SelectColumn sc = vector.elementAt(1);
            final Vector vc = sc.getColumnExpression();
            if (!(vc.elementAt(0) instanceof String)) {
                throw new ConvertException("Invalid Argument Value for Function WEEKOFYEAR", "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[] { "WEEKOFYEAR", "WEEK_START_DAY" });
            }
            weekStartDay = vc.elementAt(0);
        }
        if (weekStartDay.equalsIgnoreCase("null")) {
            weekStartDay = "1";
        }
        weekStartDay = weekStartDay.replaceAll("'", "");
        this.validateWeek_Start_Day(weekStartDay, "WEEKOFYEAR");
        this.functionName.setColumnName("");
        final Vector fnArguments = new Vector();
        final SelectColumn sc_ceil = new SelectColumn();
        final FunctionCalls fnCl_ceil = new FunctionCalls();
        final TableColumn tbCl_ceil = new TableColumn();
        tbCl_ceil.setColumnName("CEIL");
        fnCl_ceil.setFunctionName(tbCl_ceil);
        final Vector vc_ceilIn = new Vector();
        final Vector vc_ceilOut = new Vector();
        final SelectColumn sc_ceilArgs = new SelectColumn();
        final Vector vc_ceilArgs = new Vector();
        final SelectColumn sc_ceilDividend = new SelectColumn();
        final Vector vc_ceilDividend = new Vector();
        final SelectColumn sc_ceilMultiplicand = new SelectColumn();
        final Vector vc_ceilMultiplicand = new Vector();
        final SelectColumn sc_datediffAddend = new SelectColumn();
        final FunctionCalls fn_datediffAddend = new FunctionCalls();
        final TableColumn tb_datediffAddend = new TableColumn();
        tb_datediffAddend.setColumnName("DATEDIFF");
        fn_datediffAddend.setFunctionName(tb_datediffAddend);
        final Vector vc_datediffAddendIn = new Vector();
        final Vector vc_datediffAddendOut = new Vector();
        vc_datediffAddendIn.addElement(vector.get(0));
        vc_datediffAddendIn.addElement(this.WeekMode2FiscalYear(to_sqs, from_sqs, fiscalStartMonth));
        fn_datediffAddend.setFunctionArguments(vc_datediffAddendIn);
        vc_datediffAddendOut.addElement(fn_datediffAddend);
        sc_datediffAddend.setColumnExpression(vc_datediffAddendOut);
        final SelectColumn sc_Added = new SelectColumn();
        final Vector vc_Added = new Vector();
        final SelectColumn sc_AddedDividend = new SelectColumn();
        final Vector vc_AddedDividend = new Vector();
        final SelectColumn sc_dayofWeek = new SelectColumn();
        final FunctionCalls fn_dayofWeek = new FunctionCalls();
        final TableColumn tb_dayofWeek = new TableColumn();
        tb_dayofWeek.setColumnName("DAYOFWEEK");
        fn_dayofWeek.setFunctionName(tb_dayofWeek);
        final Vector vc_dayofWeekIn = new Vector();
        vc_dayofWeekIn.addElement(this.WeekMode2FiscalYear(to_sqs, from_sqs, fiscalStartMonth));
        final Vector vc_dayofWeekOut = new Vector();
        fn_dayofWeek.setFunctionArguments(vc_dayofWeekIn);
        vc_dayofWeekOut.addElement(fn_dayofWeek);
        sc_dayofWeek.setColumnExpression(vc_dayofWeekOut);
        vc_AddedDividend.addElement(sc_dayofWeek);
        vc_AddedDividend.addElement("+");
        vc_AddedDividend.addElement("7");
        vc_AddedDividend.addElement("-");
        vc_AddedDividend.addElement(weekStartDay);
        sc_AddedDividend.setColumnExpression(vc_AddedDividend);
        vc_Added.addElement("(");
        vc_Added.addElement(sc_AddedDividend);
        vc_Added.addElement(")");
        vc_Added.addElement("%");
        vc_Added.addElement("7");
        sc_Added.setColumnExpression(vc_Added);
        vc_ceilMultiplicand.addElement("(");
        vc_ceilMultiplicand.addElement(sc_datediffAddend);
        vc_ceilMultiplicand.addElement("+");
        vc_ceilMultiplicand.addElement("1");
        vc_ceilMultiplicand.addElement("+");
        vc_ceilMultiplicand.addElement(sc_Added);
        vc_ceilMultiplicand.addElement(")");
        sc_ceilMultiplicand.setColumnExpression(vc_ceilMultiplicand);
        vc_ceilDividend.addElement("(");
        vc_ceilDividend.addElement(sc_ceilMultiplicand);
        vc_ceilDividend.addElement("*");
        vc_ceilDividend.addElement("1.0");
        vc_ceilDividend.addElement(")");
        sc_ceilDividend.setColumnExpression(vc_ceilDividend);
        vc_ceilArgs.addElement(sc_ceilDividend);
        vc_ceilArgs.addElement("/");
        vc_ceilArgs.addElement("7");
        sc_ceilArgs.setColumnExpression(vc_ceilArgs);
        vc_ceilIn.addElement(sc_ceilArgs);
        fnCl_ceil.setFunctionArguments(vc_ceilIn);
        vc_ceilOut.addElement(fnCl_ceil);
        sc_ceil.setColumnExpression(vc_ceilOut);
        fnArguments.addElement(sc_ceil);
        this.setFunctionArguments(fnArguments);
    }
    
    public SelectColumn WeekMode2FiscalYear(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final String fiscalStartMonth) throws ConvertException {
        final Vector vector1 = new Vector();
        final Vector vector2 = new Vector();
        final Vector vector3 = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                vector1.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                vector3.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                vector1.addElement(this.functionArguments.elementAt(i_count));
                vector2.addElement(this.functionArguments.elementAt(i_count));
                vector3.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final SelectColumn sc_dateDiffStr_To_Date = new SelectColumn();
        final FunctionCalls fn_dateDiffStr_To_Date = new FunctionCalls();
        final TableColumn tb_dateDiffStr_To_Date = new TableColumn();
        tb_dateDiffStr_To_Date.setColumnName("STR_TO_DATE");
        fn_dateDiffStr_To_Date.setFunctionName(tb_dateDiffStr_To_Date);
        final Vector vc_dateDiffStr_To_DateIn = new Vector();
        final Vector vc_dateDiffStr_To_DateOut = new Vector();
        final SelectColumn sc_dateDiffConcat = new SelectColumn();
        final FunctionCalls fn_dateDiffConcat = new FunctionCalls();
        final TableColumn tb_dateDiffConcat = new TableColumn();
        tb_dateDiffConcat.setColumnName("CONCAT");
        fn_dateDiffConcat.setFunctionName(tb_dateDiffConcat);
        final Vector vc_dateDiffConcatIn = new Vector();
        final Vector vc_dateDiffConcatOut = new Vector();
        final SelectColumn sc_ifYearWeekFloor = new SelectColumn();
        final FunctionCalls fn_ifYearWeekFloor = new FunctionCalls();
        final TableColumn tb_ifYearWeekFloor = new TableColumn();
        tb_ifYearWeekFloor.setColumnName("IF");
        fn_ifYearWeekFloor.setFunctionName(tb_ifYearWeekFloor);
        final Vector vc_ifYearWeekFloorIn = new Vector();
        final Vector vc_ifYearWeekFloorOut = new Vector();
        final SelectColumn sc_ifYWFCon = new SelectColumn();
        final Vector vc_ifYWFCon = new Vector();
        final WhereItem whIt_YWCon = new WhereItem();
        final WhereColumn whCol_YWConLeftExp = new WhereColumn();
        final Vector vc_YWConLeftExp = new Vector();
        final SelectColumn sc_yearWeekMonth = new SelectColumn();
        final FunctionCalls fn_yearWeekMonth = new FunctionCalls();
        final TableColumn tb_yearWeekMonth = new TableColumn();
        tb_yearWeekMonth.setColumnName("MONTH");
        fn_yearWeekMonth.setFunctionName(tb_yearWeekMonth);
        final Vector vc_yearWeekMonthIn = new Vector();
        final Vector vc_yearWeekMonthOut = new Vector();
        if (vector1.elementAt(0) instanceof SelectColumn) {
            final SelectColumn sc = vector1.elementAt(0);
            vc_yearWeekMonthIn.addElement(sc);
        }
        else {
            vc_yearWeekMonthIn.addElement(vector1.elementAt(0));
        }
        fn_yearWeekMonth.setFunctionArguments(vc_yearWeekMonthIn);
        vc_yearWeekMonthOut.addElement(fn_yearWeekMonth);
        sc_yearWeekMonth.setColumnExpression(vc_yearWeekMonthOut);
        vc_YWConLeftExp.addElement(sc_yearWeekMonth);
        whCol_YWConLeftExp.setColumnExpression(vc_YWConLeftExp);
        whIt_YWCon.setLeftWhereExp(whCol_YWConLeftExp);
        whIt_YWCon.setOperator("<");
        final WhereColumn whCol_YWConRightExp = new WhereColumn();
        final Vector vc_YWConRightExp = new Vector();
        vc_YWConRightExp.addElement(fiscalStartMonth);
        whCol_YWConRightExp.setColumnExpression(vc_YWConRightExp);
        whIt_YWCon.setRightWhereExp(whCol_YWConRightExp);
        vc_ifYWFCon.addElement(whIt_YWCon);
        sc_ifYWFCon.setColumnExpression(vc_ifYWFCon);
        final SelectColumn sc_YWTrueStmt = new SelectColumn();
        final Vector vc_YWTrueStmt = new Vector();
        final SelectColumn sc_YWYear = new SelectColumn();
        final FunctionCalls fn_YWYear = new FunctionCalls();
        final TableColumn tb_YWYear = new TableColumn();
        tb_YWYear.setColumnName("YEAR");
        fn_YWYear.setFunctionName(tb_YWYear);
        final Vector vc_YWYearIn = new Vector();
        final Vector vc_YWYearOut = new Vector();
        if (vector3.elementAt(0) instanceof SelectColumn) {
            final SelectColumn sc2 = vector3.elementAt(0);
            vc_YWYearIn.addElement(sc2);
        }
        else {
            vc_YWYearIn.addElement(vector3.elementAt(0));
        }
        fn_YWYear.setFunctionArguments(vc_YWYearIn);
        vc_YWYearOut.addElement(fn_YWYear);
        sc_YWYear.setColumnExpression(vc_YWYearOut);
        vc_YWTrueStmt.addElement("(");
        vc_YWTrueStmt.addElement(sc_YWYear);
        vc_YWTrueStmt.addElement("-");
        vc_YWTrueStmt.addElement("1");
        vc_YWTrueStmt.addElement(")");
        sc_YWTrueStmt.setColumnExpression(vc_YWTrueStmt);
        final SelectColumn sc_YWFalseStmt = new SelectColumn();
        final FunctionCalls fn_YWFalseStmt = new FunctionCalls();
        final TableColumn tb_YWFalseStmt = new TableColumn();
        tb_YWFalseStmt.setColumnName("YEAR");
        fn_YWFalseStmt.setFunctionName(tb_YWFalseStmt);
        final Vector vc_YWFalseStmtIn = new Vector();
        final Vector vc_YWFalseStmtOut = new Vector();
        if (vector2.elementAt(0) instanceof SelectColumn) {
            final SelectColumn sc3 = vector2.elementAt(0);
            vc_YWFalseStmtIn.addElement(sc3);
        }
        else {
            vc_YWFalseStmtIn.addElement(vector2.elementAt(0));
        }
        fn_YWFalseStmt.setFunctionArguments(vc_YWFalseStmtIn);
        vc_YWFalseStmtOut.addElement(fn_YWFalseStmt);
        sc_YWFalseStmt.setColumnExpression(vc_YWFalseStmtOut);
        vc_ifYearWeekFloorIn.addElement(sc_ifYWFCon);
        vc_ifYearWeekFloorIn.addElement(sc_YWTrueStmt);
        vc_ifYearWeekFloorIn.addElement(sc_YWFalseStmt);
        fn_ifYearWeekFloor.setFunctionArguments(vc_ifYearWeekFloorIn);
        vc_ifYearWeekFloorOut.addElement(fn_ifYearWeekFloor);
        sc_ifYearWeekFloor.setColumnExpression(vc_ifYearWeekFloorOut);
        vc_dateDiffConcatIn.addElement(sc_ifYearWeekFloor);
        vc_dateDiffConcatIn.addElement("'-'");
        vc_dateDiffConcatIn.addElement(fiscalStartMonth);
        vc_dateDiffConcatIn.addElement("'-01'");
        fn_dateDiffConcat.setFunctionArguments(vc_dateDiffConcatIn);
        vc_dateDiffConcatOut.addElement(fn_dateDiffConcat);
        sc_dateDiffConcat.setColumnExpression(vc_dateDiffConcatOut);
        vc_dateDiffStr_To_DateIn.addElement(sc_dateDiffConcat);
        vc_dateDiffStr_To_DateIn.addElement("'%Y-%m-%d'");
        fn_dateDiffStr_To_Date.setFunctionArguments(vc_dateDiffStr_To_DateIn);
        vc_dateDiffStr_To_DateOut.addElement(fn_dateDiffStr_To_Date);
        sc_dateDiffStr_To_Date.setColumnExpression(vc_dateDiffStr_To_DateOut);
        return sc_dateDiffStr_To_Date;
    }
}
