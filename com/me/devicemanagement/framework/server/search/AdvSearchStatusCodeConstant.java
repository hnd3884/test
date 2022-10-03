package com.me.devicemanagement.framework.server.search;

public class AdvSearchStatusCodeConstant
{
    public static final int SEARCH_DISABLED_ERROR_CODE = 8001;
    public static final int SEARCH_FAILED_ERROR_CODE = 8002;
    public static final int VERIFY_SEARCH_SUCCESS_CODE = 100801;
    public static final int VERIFY_SEARCH_FAILED_CODE = 100802;
    public static final int SEARCH_CHAR_FAILED_CODE = 100803;
    public static final int SEARCH_SYMBOL_FAILED_CODE = 100804;
    public static final int SEARCH_EXCEPTION_FAILED_CODE = 100805;
    public static final int SEARCH_PROMOTION_ENABLE = 100808;
    public static final int SEARCH_PROMOTION_DISABLE = 100809;
    public static final int SEARCH_PROMOTION_REMOVE_FAILED = 100810;
    public static final String SEARCH_SUCCESS_MSG = "Search Success";
    public static final String SEARCH_FAILED_MSG = "Search Failed";
    public static final String SERACH_DISABLED_MSG = "Search Disabled";
    public static final String VERIFY_SEARCH_SUCCESS_MSG = "Authorization Success to AdvSearch ";
    public static final String VERIFY_SEARCH_FAILED_MSG = "Authorization Failed to AdvSearch Failed";
    public static final String SEARCH_CHAR_FAILED_MSG = "Search terms can't contain the following characters: # & % = < > ( ) [ ] { }";
    public static final String SEARCH_SYMBOL_FAILED_MSG = "Search terms contain only symbol characters";
    public static final String SEARCH_EXCEPTION_MSG = "Exception Message";
}
