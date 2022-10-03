package com.me.mdm.api.reports.integ;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.reports.ReportBISelectQueryTransformer;

public class GroupQueryTransformer implements ReportBISelectQueryTransformer
{
    public SelectQuery transformSelectQuery(final String tableName, final SelectQuery selectQuery) {
        selectQuery.setCriteria(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)new Integer[] { 3, 4, 5, 6 }, 8));
        return selectQuery;
    }
}
