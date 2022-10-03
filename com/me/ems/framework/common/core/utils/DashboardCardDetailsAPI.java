package com.me.ems.framework.common.core.utils;

import java.util.Properties;
import java.util.HashMap;

public interface DashboardCardDetailsAPI
{
    HashMap getGraphTypeCardDetails(final Long p0, final HashMap p1) throws Exception;
    
    String getCCTypeCardDetails(final Long p0, final HashMap p1) throws Exception;
    
    Properties getHTMLTypeCardDetails(final Long p0, final HashMap p1) throws Exception;
}
