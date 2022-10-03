package com.me.ems.framework.common.core;

import java.util.Map;

public interface LiveChatHandler
{
    public static final String COMMON_URL = "https://salesiq.zoho.com/widget";
    public static final String CHINESE_URL = "https://salesiq.zoho.com.cn/widget";
    public static final String WIDGET_CODE = "widgetCode";
    
    void getProductSpecificLiveChatData(final Map<String, Object> p0);
}
