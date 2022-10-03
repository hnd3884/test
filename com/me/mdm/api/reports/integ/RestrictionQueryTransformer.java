package com.me.mdm.api.reports.integ;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.me.devicemanagement.framework.webclient.reports.ReportBISelectQueryTransformer;

public class RestrictionQueryTransformer implements ReportBISelectQueryTransformer
{
    private static List<String> nonRestrictionCols;
    
    private boolean isRestrictionValueCol(final Column column) {
        return !RestrictionQueryTransformer.nonRestrictionCols.contains(column.getColumnName());
    }
    
    public SelectQuery transformSelectQuery(final String tableName, final SelectQuery query) {
        try {
            final List<Column> columnList = query.getSelectColumns();
            for (final Column column : columnList) {
                if (this.isRestrictionValueCol(column)) {
                    query.removeSelectColumn(column);
                    final CaseExpression ce = new CaseExpression(column.getTableAlias(), column.getColumnName(), column.getColumnAlias());
                    ce.addWhen((Object)(-1), (Object)"Not Applicable");
                    ce.addWhen((Object)0, (Object)"Restricted");
                    ce.addWhen((Object)1, (Object)"Allowed");
                    ce.addWhen((Object)2, (Object)"Unknown");
                    ce.addWhen((Object)4, (Object)"User Controlled");
                    ce.addWhen((Object)5, (Object)"On");
                    ce.addWhen((Object)6, (Object)"Always Off");
                    query.addSelectColumn((Column)ce);
                }
            }
            return query;
        }
        catch (final Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Exception", e);
            return null;
        }
    }
    
    static {
        (RestrictionQueryTransformer.nonRestrictionCols = new ArrayList<String>()).add("RESOURCE_ID");
        RestrictionQueryTransformer.nonRestrictionCols.add("SCOPE");
        RestrictionQueryTransformer.nonRestrictionCols.add("MOVIES_RATING_VALUE");
        RestrictionQueryTransformer.nonRestrictionCols.add("TV_SHOWS_RATING_VALUE");
        RestrictionQueryTransformer.nonRestrictionCols.add("APPS_RATING_VALUE");
    }
}
