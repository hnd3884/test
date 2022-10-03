package com.adventnet.sym.server.filter;

public interface FilterConstants
{
    public static final int DOMAINFILTER = 1;
    public static final int SITEFILTER = 2;
    public static final int OUFILTER = 3;
    public static final int GROUPFILTER = 4;
    public static final int USERFILTER = 7;
    public static final int IPADDRFILTER = 8;
    public static final int COMPUTERFILTER = 9;
    public static final int MACADDRFILTER = 10;
    public static final int OSFILTER = 11;
    public static final int CLASSFILTER = 14;
    public static final int CONNECTIONFILTER = 15;
    public static final int CUSTOMGROUPFILTER = 16;
    public static final int BRANCHFILTER = 17;
    public static final int IP_RANGE_FILTER = 18;
    public static final int GLOBAL_EXCLUSION_FILTER = 19;
    public static final int OSARCHFILTER = 20;
    public static final int SPFILTER = 21;
    public static final int OSVERFILTER = 22;
    public static final int SWFILTER = 23;
    public static final int SWVERFILTER = 24;
    public static final int CRIRERIACUSTOMGROUPFILTER = 25;
    public static final int SCOPEFILTER = 30;
    public static final int REGISTRY_EXISTS_FILTER = 40;
    public static final int REGISTRY_CHECK_FILTER = 41;
    public static final int FF_EXISTS_FILTER = 42;
    public static final int DRIVE_FILTER = 43;
    public static final int PROCESS_FILTER = 44;
    public static final int SCRIPT_FILTER = 45;
    public static final int WORKGROUP_FILTER = 51;
    public static final int BRANCH_ID_FILTER = 52;
    public static final int TYPE_CTGY = 100;
    public static final int OS_CTGY = 101;
    public static final int CLASS_CTGY = 102;
    public static final int CONNECTION_CTGY = 103;
    public static final int OS_ARCH_CTGY = 104;
    public static final int OS_VER_CTGY = 106;
    public static final int SW_CTGY = 107;
    public static final int REGISTRY_CTGY = 109;
    public static final int FILE_CTGY = 110;
    public static final int PROCESS_CTGY = 111;
    public static final int DRIVE_CTGY = 112;
    public static final int SCRIPT_CTGY = 113;
    public static final int REL_OPR_EQ = 15;
    public static final int REL_OPR_NE = 16;
    public static final int REL_OPR_SW = 17;
    public static final int REL_OPR_EW = 18;
    public static final int REL_OPR_CONTAINS = 19;
    public static final int REL_OPR_LT = 20;
    public static final int REL_OPR_GT = 21;
    public static final int REL_OPR_LTEQ = 22;
    public static final int REL_OPR_GTEQ = 23;
    public static final int LOG_OPR_AND = 21;
    public static final int LOG_OPR_OR = 22;
    public static final int CLEAR_OLD_TARGETS = 1000;
    public static final int RETAIN_OLD_TARGETS = 1001;
    public static final int RETAIN_OLD_APPLIED_TARGETS = 1002;
    public static final int LOCAL_DOMAIN_ENV = 1;
    public static final int LOCAL_WORKGROUP_ENV = 2;
    public static final int NEW_FILTER_MODEL = 8;
    public static final int BRANCH_ID_DOMAIN_ENV = 5;
    public static final int BRANCH_ID_WORKGROUP_ENV = 6;
    public static final int OTHERS = 7;
    public static final int NETWORK_ALL = 10;
    public static final int STATIC_CUSTOMGROUP = 11;
    public static final int DYNAMIC_CUSTOMGROUP = 12;
    public static final int BRANCH_DOMAIN_ENV = 3;
    public static final int BRANCH_WORKGROUP_ENV = 4;
    public static final String RESOURCE_GUID = "RESOURCE_GUID";
    public static final String FILTER_XML_FILE = "filter.xml";
    public static final String BRANCH_OFFICE_NAME = "BRANCH_OFFICE_NAME";
    public static final Integer FILTER_COMP_PARAMS_SERVER_SIDE_USE = new Integer(2);
    public static final Integer FILTER_COMP_PARAMS_BOTH_SIDE_USE = new Integer(1);
    public static final String DEFAULT_DOMAIN_VAL = "DOMAIN_NOT_APPLICABLE";
}
