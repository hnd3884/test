package com.me.devicemanagement.framework.server.dcViewFilter;

public class DCViewFilterConstants
{
    public static final int LAST_N_DAYS = 2;
    public static final int CUSTOM_BETWEEN = 3;
    public static final int CUSTOM_DATE = 4;
    public static final int PLACEHOLDER_FOR_CRITERIA = 1;
    public static final int PLACEHOLDER_FOR_JOIN = 2;
    public static final String VIEW_FILTER = "FilterCriteria";
    public static final String PAGE_ID = "pageID";
    public static final String VIEW_ID = "viewID";
    public static final String FILTER_ID = "filterID";
    public static final String COLUMN_ID = "columnID";
    public static final String FILTER_NAME = "filterName";
    public static final String FILTER_NAME_EXISTS = "isFilterNameExists";
    public static final String IS_FILTER_MAPPED_TO_USER = "isFilterMappedToUser";
    public static final String CUSTOM = "custom";
    public static final String SEARCH_VALUE_JOIN_DELIMETER = "$@$";
    public static final String SEARCH_VALUE_JOIN_DELIMETER_PDF = ",";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";
    public static final String FILTER = "filter";
    public static final String CUSTOM_SEARCH_VALUES = "customSearchValues";
    
    public static int getCustomTypes(final String customType) {
        if (customType.equalsIgnoreCase("last_n_days")) {
            return 2;
        }
        if (customType.equalsIgnoreCase("betweenDate")) {
            return 3;
        }
        if (customType.equalsIgnoreCase("isDate")) {
            return 4;
        }
        return -1;
    }
    
    public static String getCustomTypeValue(final Integer customType) {
        if (customType == 2) {
            return "last_n_days";
        }
        if (customType == 3) {
            return "betweenDate";
        }
        if (customType == 4) {
            return "isDate";
        }
        return "--";
    }
}
