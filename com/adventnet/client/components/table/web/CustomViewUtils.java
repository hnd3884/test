package com.adventnet.client.components.table.web;

import java.util.List;
import java.util.Iterator;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import java.util.Optional;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;

public final class CustomViewUtils
{
    public static SelectQuery getSelectQuery(final Object customViewId) throws Exception {
        if (customViewId == null) {
            return null;
        }
        final Row customViewRow = new Row("CustomViewConfiguration");
        customViewRow.set(1, customViewId);
        final DataObject customViewDO = LookUpUtil.getPersistence().get("CustomViewConfiguration", customViewRow);
        final long queryID = (long)customViewDO.getFirstValue("CustomViewConfiguration", 3);
        return QueryUtil.getSelectQuery(queryID);
    }
    
    public static SelectQuery getSelectQuery(final Object cvId, final DataObject viewConfig) throws Exception {
        final SelectQuery query = getSelectQuery(cvId);
        final Long viewNameNo = (Long)viewConfig.getFirstValue("ViewConfiguration", "VIEWNAME_NO");
        final Long origViewNameNo = Optional.ofNullable(UserPersonalizationAPI.getOriginalViewNameNo((Object)viewNameNo, WebClientUtil.getAccountId())).orElse(viewNameNo);
        TableUtil.getDynamicColumns(origViewNameNo).forEach(dyCol -> selectQuery.addSelectColumn(Column.getColumn(dyCol.getTableAlias(), dyCol.getColumnName(), dyCol.getColumnAlias())));
        if (viewConfig.containsTable("ACGroupByColumns")) {
            final Iterator ite = viewConfig.getRows("ACGroupByColumns");
            while (ite.hasNext()) {
                final Row r = ite.next();
                final Column col = new Column((String)r.get(2), (String)r.get(3), (String)r.get(4));
                query.addGroupByColumn(col);
            }
        }
        if (viewConfig.containsTable("ACFunctionColumns")) {
            final Iterator ite = viewConfig.getRows("ACFunctionColumns");
            while (ite.hasNext()) {
                final Row r = ite.next();
                Column col = new Column((String)r.get(2), (String)r.get(3), (String)r.get(4));
                if (r.get(5).equals("AVG")) {
                    col = col.average();
                }
                else if (r.get(5).equals("COUNT")) {
                    col = col.count();
                }
                else if (r.get(5).equals("DISTINCT")) {
                    col = col.distinct();
                }
                else if (r.get(5).equals("MAX")) {
                    col = col.maximum();
                }
                else if (r.get(5).equals("MIN")) {
                    col = col.minimum();
                }
                else if (r.get(5).equals("SUM")) {
                    col = col.summation();
                }
                col.setColumnAlias((String)r.get(4));
                if (col.getFunction() == 1) {
                    query.addSelectColumn(col, 0);
                }
                else {
                    query.addSelectColumn(col);
                }
                final List sortList = query.getSortColumns();
                if (sortList != null && sortList.size() > 0) {
                    for (int i = 0; i < sortList.size(); ++i) {
                        final SortColumn sortCol = sortList.get(i);
                        if (r.get(4).equals(sortCol.getColumnAlias())) {
                            final SortColumn newSortCol = new SortColumn(col, sortCol.isAscending());
                            query.removeSortColumn(i);
                            query.addSortColumn(newSortCol, i);
                            break;
                        }
                    }
                }
            }
        }
        return query;
    }
}
