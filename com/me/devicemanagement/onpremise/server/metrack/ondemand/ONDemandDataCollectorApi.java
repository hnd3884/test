package com.me.devicemanagement.onpremise.server.metrack.ondemand;

public interface ONDemandDataCollectorApi
{
    public static final String DATACOLLECTOR_PRODUCTSPECIFIC_CONF = "datacollector-productspecific.conf";
    public static final String DATACOLLECTOR_CUSTOMERSPECIFIC_CONF = "datacollector-customerspecific.conf";
    public static final String LAST_TASKS_FETCH_TIME = "last_tasks_fetch_time";
    public static final String ZC_DATE_FORMAT = "dd-MMM-yyyy HH:mm:ss";
    public static final int DATA_LIMIT = 20000;
    public static final String STATUS = "Status";
    public static final String OUTPUT = "Output";
    public static final String DEFAULT_FORM_TASKID_COLUMN_NAME = "taskid";
    public static final String ZCAPPLICATION_NAME = "zc_application_name";
    public static final String TASKS_ZCVIEW_NAME = "tasks_zcview_name";
    public static final String ZCKEY = "zc_key";
    public static final String ZCOWNERNAME = "zc_owner_name";
    public static final String ZCCRITERIA_COLUMN_NAME = "zc_criteria_column_name";
    public static final String TASK_ID = "ID";
    public static final String ZCFORM_NAME = "ZCForm_Name";
    public static final String ZCCOLUMN_NAME = "ZCColumn_Name";
    public static final String DATA_POSTED_TO = "Data_Posted_To";
    public static final String TASK_RELEASED_TIME = "Task_Released_Time";
    public static final String EXPIRE_DAYS = "Expire_Days";
    public static final String DATA_FROM = "Data_From";
    public static final String SQL_FOR = "Sql_For";
    public static final String COMMON_QUERY = "Common_Query";
    public static final String POSTGRES_QUERY = "Postgres_Query";
    public static final String MSSQL_QUERY = "Mssql_Query";
    public static final String FILE_PATH = "File_Path";
    public static final String KEY_NAMES = "Key_Names";
    public static final String STARTS_WITH = "Starts_With";
    public static final String ENDS_WITH = "Ends_With";
    public static final String MATCHES = "Matches";
    public static final String MINBUILD_NUM = "MINBuild_Num";
    public static final String MAXBUILD_NUM = "MAXBuild_Num";
    public static final String LICENSE_TYPE = "License_Type.License_Type_Code";
    public static final String LICENSE_EDITION = "License_Edition.License_Edition_Code";
    public static final String PRODUCT_ARCHITECTURE = "Product_Architecture";
    public static final String DB_TYPE = "DB_Type";
    public static final int VALUE_FROM_QUERY = 1;
    public static final int VALUE_FROM_PROPSORCONF = 2;
    public static final int VALUE_FROM_LOG = 3;
    public static final int ALL_DB_COMMON_QUERY = 1;
    public static final int NOT_COMMON = 2;
    public static final int DEFAULT_FORM = 1;
    public static final int NOT_A_DEFAULT_FORM = 2;
    
    ONDemandDataCollectorBean getAllOndemandProperties();
}
