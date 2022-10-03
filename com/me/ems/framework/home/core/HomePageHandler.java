package com.me.ems.framework.home.core;

import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import com.adventnet.ds.query.SelectQuery;

public interface HomePageHandler
{
    SelectQuery handleSummarySelectQuery(final SelectQuery p0) throws Exception;
    
    void setHomePageMessagesAndDetails(final User p0, final Map<String, Object> p1) throws Exception;
    
    Map<String, Object> getNotificationMsgContent() throws Exception;
}
