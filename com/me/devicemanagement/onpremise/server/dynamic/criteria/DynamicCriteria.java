package com.me.devicemanagement.onpremise.server.dynamic.criteria;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.dynamic.criteria.DynamicCriteriaAPI;

public class DynamicCriteria implements DynamicCriteriaAPI
{
    public Criteria getDynamicCriteria() {
        return new Criteria(Column.getColumn("ColumnDetails", "IS_DYNAMIC"), (Object)true, 0);
    }
}
