package com.me.devicemanagement.framework.server.search;

import java.util.ArrayList;
import java.util.List;

public interface SuggestQueryIfc
{
    List getSuggestData(final String p0, final String p1);
    
    default List getSuggestDataAPI(final String searchString, final String params) {
        return new ArrayList();
    }
}
