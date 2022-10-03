package com.me.devicemanagement.framework.webclient.reports;

import com.adventnet.ds.query.SelectQuery;

public interface ReportBISelectQueryTransformer
{
    public static final String CUSTOM_I18N_DELIMITER = "###";
    public static final String STRING_CONCAT = "STRING_CONCAT";
    public static final String STRING_CONCAT_BEFORE_MSSQL_2012 = "STRING_CONCAT_BEFORE_MSSQL_2012";
    public static final String CHAR = "CHAR";
    public static final String SUM = "SUM";
    public static final String BIGINT = "BIGINT";
    public static final String DUMMY_COL = "DUMMY_COL";
    
    SelectQuery transformSelectQuery(final String p0, final SelectQuery p1);
}
