package com.me.devicemanagement.framework.server.search;

import com.adventnet.ds.query.SelectQuery;

public interface SearchSuggestionAPI
{
    SelectQuery addSuggestionCriteria(final SelectQuery p0, final String p1, final int p2);
}
