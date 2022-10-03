package com.me.ems.framework.common.api.constants;

public class DashboardConstants
{
    public static final String CARD_ID = "cardId";
    public static final String DISPLAY_ORDER = "displayOrder";
    public static final String SHOW_FILTER_STR = "showFilter";
    public static final String FILTER_ID = "filterId";
    public static final String VALUE_ID = "valueId";
    public static final String XPOS = "xPos";
    public static final String YPOS = "yPos";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final Integer HIDE_FILTER;
    public static final Integer SHOW_FILTER;
    public static final String CARD_GRAPH = "GRAPH";
    public static final String CARD_MULTI_CARD = "MULTI_CARD";
    public static final String CARD_MULTI_SINGLE_CARD = "MULTI_SINGLE_CARD";
    public static final String CARD_HTML_TABLE = "HTML_TABLE";
    public static final String CARD_HTML_COUNTS = "HTML_COUNTS";
    public static final String CARD_HTML_TOP_COUNTS = "TOP_COUNTS";
    public static final String CARD_HTML_FEED = "HTML_FEED";
    public static final String CARD_HTML = "HTML";
    public static final String CARD_CC = "Cc";
    public static final String FILTER_VULNERABLE = "Vulnerable";
    public static final String FILTER_EXPLOITABLE = "Exploitable";
    public static final String FILTER_VALUE_EXPLOITABLE = "Exploitable";
    public static final Integer TOTAL;
    public static final Integer FIXABLE;
    public static final Integer NON_FIXABLE;
    
    static {
        HIDE_FILTER = 0;
        SHOW_FILTER = 1;
        TOTAL = 0;
        FIXABLE = 1;
        NON_FIXABLE = 2;
    }
}
