package com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool.util;

import java.io.File;

public class DiffToolConstants
{
    public static final String DIFFTOOL_NAME = "DiffTool";
    public static final String DIFFTOOL_CONF_FILE;
    public static final String INVOCATION_TYPE_KEY = "difftool.invocation.type";
    public static final int SCHEDULE_INVOCATION = 1;
    public static final int STARTUP_INVOCATION = 2;
    public static final String SCHEDULE_RUNTIME_KEY = "difftool.schedule.runtime";
    public static final String IGNORE_DIFFTYPE = "difftool.filter.ignore.difftype";
    public static final String IGNORE_MODULENAME = "difftool.filter.ignore.modulename";
    public static final String IGNORE_TABLENAME = "difftool.filter.ignore.tablename";
    public static final String IGNORE_COLUMNNAME = "difftool.filter.ignore.name";
    public static final String DIFF_NAME = "name";
    public static final String DIFF_DESTINATION = "destination";
    public static final String DIFF_MODULENAME = "modulename";
    public static final String DIFF_DIFFTYPE = "difftype";
    public static final String DIFF_SOURCE = "source";
    public static final String DIFF_TABLENAME = "tablename";
    public static final String DIFF_FILENAME = "SchemaDiff.json";
    public static final String FILTERED_DIFF_FILENAME = "Filtered-SchemaDiff.json";
    public static final String FLAG_FILE_NAME = "UpdmgrInvoked.flag";
    public static final String IS_TABLE_EXISTS = "is_table_exists";
    public static final int IS_TABLE_EXISTS_ID = 1;
    public static final String PRIMARY_KEY_COLUMNS = "primary_key_columns";
    public static final int PRIMARY_KEY_COLUMNS_ID = 2;
    public static final String IS_COLUMN_EXISTS = "is_column_exists";
    public static final int IS_COLUMN_EXISTS_ID = 3;
    public static final String COLUMN_DATA_TYPE = "column_data_type";
    public static final int COLUMN_DATA_TYPE_ID = 4;
    public static final String COLUMN_MAX_SIZE = "column_max_size";
    public static final int COLUMN_MAX_SIZE_ID = 5;
    public static final String COLUMN_PRECISION = "column_precision";
    public static final int COLUMN_PRECISION_ID = 6;
    public static final String COLUMN_DEFAULT_VALUE = "column_default_value";
    public static final int COLUMN_DEFAULT_VALUE_ID = 7;
    public static final String COLUMN_NULLABLE = "column_nullable";
    public static final int COLUMN_NULLABLE_ID = 8;
    public static final String IS_FK_EXISTS = "is_fk_exists";
    public static final int IS_FK_EXISTS_ID = 9;
    public static final String FK_DELETE_RULE_NAME = "fk_delete_rule_name";
    public static final int FK_DELETE_RULE_NAME_ID = 10;
    public static final String FK_PARENT_TABLENAME = "fk_parent_tablename";
    public static final int FK_PARENT_TABLENAME_ID = 11;
    public static final String FK_CHILD_TABLENAME = "fk_child_tablename";
    public static final int FK_CHILD_TABLENAME_ID = 12;
    public static final String IS_INDEX_EXISTS = "is_index_exists";
    public static final int IS_INDEX_EXISTS_ID = 13;
    public static final String INDEX_COLUMNS = "index_columns";
    public static final int INDEX_COLUMNS_ID = 14;
    public static final String IS_UNIQUE_KEY_EXISTS = "is_unique_key_exists";
    public static final int IS_UNIQUE_KEY_EXISTS_ID = 15;
    public static final String UK_COLUMNS = "uk_columns";
    public static final int UK_COLUMNS_ID = 16;
    public static final String ABANDONED_CHILDROWS = "abandoned_childrows";
    public static final int ABANDONED_CHILDROWS_ID = 17;
    
    static {
        DIFFTOOL_CONF_FILE = "conf" + File.separator + "DiffTool.conf";
    }
}
