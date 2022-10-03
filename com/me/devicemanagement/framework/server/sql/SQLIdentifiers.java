package com.me.devicemanagement.framework.server.sql;

public interface SQLIdentifiers
{
    public static final int MYSQL = 1;
    public static final int MSSSQL = 2;
    public static final int ADCONTAINERREL_INSERT_SQL = 1002;
    public static final int ADGROUPMEMBERREL_INSERT_SQL = 1004;
    public static final int ADGROUPMEMBERREL_PRIMARY_INSERT_SQL = 1005;
    public static final int ADRESOURCEADDED_INSERT_SQL = 1006;
    public static final int INV_COPY_TECH_SW_SUMMARY = 31000;
    public static final int INV_COPY_TECH_SW_DOMAIN_SUMMARY = 31001;
    public static final int INV_COPY_TECH_SW_BRANCH_SUMMARY = 31002;
    public static final int INV_COPY_TECH_HW_SUMMARY = 31003;
    public static final int INV_COPY_TECH_HW_DOMAIN_SUMMARY = 31004;
    public static final int INV_COPY_TECH_HW_BRANCH_SUMMARY = 31005;
    public static final int INV_COPY_TECH_COMPLETE_SUMMARY = 31006;
    public static final int INV_COPY_TECH_COMPLETE_OS_SUMMARY = 31007;
    public static final int INV_COPY_TECH_DOMAIN_SUMMARY = 31008;
    public static final int INV_COPY_TECH_DOMAIN_OS_SUMMARY = 31009;
    public static final int INV_COPY_TECH_BRANCH_SUMMARY = 31010;
    public static final int INV_COPY_TECH_BRANCH_OS_SUMMARY = 31011;
    public static final int INV_SET_SW_ISINUSE_FLAG_FALSE = 2013;
    public static final int INV_SET_SW_ISINUSE_FLAG_TRUE = 2014;
    public static final int PATCH_DELETE_AFFPATCHSTATUS = 6001;
    public static final int PATCH_DELETE_DECAFFPATCHSTATUS = 6002;
    public static final int PATCH_INSERT_AFFPATCHSTATUS = 6003;
    public static final int PATCH_INSERT_DECAFFPATCHSTATUS = 6004;
    public static final int DEL_AFFPATCHSTATUS_BASED_ON_DB_SET = 6007;
    public static final int DEL_CUS_PATCH_STATUS_BASED_ON_DB_SET = 6008;
    public static final int DEL_DEC_PATCH_BASED_ON_DB_SET = 6009;
    public static final int DEL_DEC_APP_BASED_ON_DB_SET = 6010;
    public static final int DEL_APD_UPDATES_BASED_ON_DB_SET = 6011;
    public static final int DEL_APD_SEV_FOR_UPDATE_BASED_ON_DB_SET = 6012;
    public static final int PATCH_INSERT_AFFPATCHSTATUS_FOR_CG = 6013;
    public static final int PATCH_INSERT_DECAFFPATCHSTATUS_FOR_CG = 6014;
    public static final int PATCH_DELETE_AFFPATCHSTATUS_FOR_CG = 6015;
    public static final int PATCH_DELETE_DECAFFPATCHSTATUS_FOR_CG = 6016;
    public static final int DEL_DECAFFPATCHSTATUS_BASED_ON_DB_SET = 6017;
    public static final int DEL_TP_APP_FOR_HEALTH_CALC = 6018;
    public static final int DEL_TESTGRP_UPDATES_BASED_ON_DB_SET = 6019;
    public static final int DEL_TESTGRP_SEV_FOR_UPDATE_BASED_ON_DB_SET = 6020;
    public static final int IMMEDIATE_SHUTDOWN_DELETE_COMPUTERS_WITH_DOMAIN_CRITERIA = 5003;
    public static final int IMMEDIATE_SHUTDOWN_DELETE_COMPUTERS_WITH_CUSTOMGP_CRITERIA = 5004;
    public static final int UPDATE_SECONDSLOTS_COUNT = 9003;
    public static final int UPDATE_MINUTESLOTS_COUNT = 9004;
    public static final int UPDATE_RESOURCE_TO_CFG_SUMMARY = 11000;
    public static final int PMTOOLS_STATUS_VIEW_QUERY = 12001;
    public static final int PMTOOLS_COMPUTER_STATUS_VIEW_QUERY = 12002;
    public static final int PATCH_UPDATE_RESOURCEDEPLOYMENTSTATUS_TIME = 15010;
    public static final int PATCH_DELETE_INSTALLPATCHSTATUS_DATA = 15013;
    public static final int PATCH_DELETE_SUPERCEDEDINSTALLPATCHSTATUS_DATA = 15015;
    public static final int DETECT_DB_LOCK = 17000;
    public static final int PATCH_VIEW_DYNAMIC_MISSING_SYSTEM_SUBQUERY = 41000;
    public static final int PATCH_VIEW_DYNAMIC_FAILED_SYSTEM_SUBQUERY = 41001;
    public static final int PATCH_VIEW_DYNAMIC_INSTALLED_SYSTEM_SUBQUERY = 41002;
    public static final int PATCH_VIEW_FULL_DYNAMIC_MISSING_SYSTEM_SUBQUERY = 41003;
    public static final int PATCH_VIEW_FULL_DYNAMIC_FAILED_SYSTEM_SUBQUERY = 41004;
    public static final int PATCH_VIEW_FULL_DYNAMIC_INSTALLED_SYSTEM_SUBQUERY = 41005;
    public static final int SYSTEM_VIEW_STATIC_MISSING_PATCH_SUBQUERY = 41006;
    public static final int SYSTEM_VIEW_STATIC_INSTALLED_PATCH_SUBQUERY = 41007;
    public static final int SYSTEM_VIEW_DYNAMIC_MISSING_PATCH_SUBQUERY = 41008;
    public static final int SYSTEM_VIEW_DYNAMIC_INSTALLED_PATCH_SUBQUERY = 41009;
    public static final int SYSTEM_VIEW_DYNAMIC_FAILED_PATCH_SUBQUERY = 41010;
    public static final int SYSTEM_VIEW_FULL_DYNAMIC_MISSING_PATCH_SUBQUERY = 41011;
    public static final int SYSTEM_VIEW_FULL_DYNAMIC_INSTALLED_PATCH_SUBQUERY = 41012;
    public static final int SYSTEM_VIEW_FULL_DYNAMIC_FAILED_PATCH_SUBQUERY = 41013;
    public static final int LOAD_INTO_STAGING_TABLE_FROM_CSV = 33000;
    public static final int MERGE_FROM_TEMP_TO_MAIN = 33001;
    public static final int CREATE_TEMP_TABLE = 33002;
    public static final int CREATE_TEMP_TABLE_ON_COMMIT_DROP = 33003;
}
