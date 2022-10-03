package com.me.devicemanagement.framework.webclient.reportcriteria;

import java.util.List;
import java.util.Map;

public interface CriteriaColumnValue
{
    public static final String COLUMN_ID = "column_id";
    public static final String VIEW_ID = "view_id";
    
    List getColumnBrowseValues(final Long p0, final Long p1, final Map p2, final Long p3) throws Exception;
}
