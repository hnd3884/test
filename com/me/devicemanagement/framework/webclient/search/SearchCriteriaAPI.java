package com.me.devicemanagement.framework.webclient.search;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;

public interface SearchCriteriaAPI
{
    SelectQuery setCorrespondingCriteria(final ViewContext p0, final SelectQuery p1, final String p2);
}
