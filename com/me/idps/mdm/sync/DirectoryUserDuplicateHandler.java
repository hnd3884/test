package com.me.idps.mdm.sync;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.sym.server.mdm.config.ResourceSummaryHandler;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.HashMap;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.sql.Connection;

class DirectoryUserDuplicateHandler
{
    private static DirectoryUserDuplicateHandler directoryUserDuplicateHandler;
    
    static DirectoryUserDuplicateHandler getInstance() {
        if (DirectoryUserDuplicateHandler.directoryUserDuplicateHandler == null) {
            DirectoryUserDuplicateHandler.directoryUserDuplicateHandler = new DirectoryUserDuplicateHandler();
        }
        return DirectoryUserDuplicateHandler.directoryUserDuplicateHandler;
    }
    
    void handleUserResDuplicates(final Connection connection, Criteria resCri, Criteria dirObjRegCri, final Long dmDomainID, final Long collationID) throws Exception {
        resCri = resCri.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0));
        dirObjRegCri = dirObjRegCri.and(new Criteria(Column.getColumn("DirObjRegStrVal", "DIR_RESOURCE_TYPE"), (Object)2, 0));
        final Criteria joinCri = resCri.and(dirObjRegCri).and(new Criteria(Column.getColumn("Resource", "NAME"), (Object)Column.getColumn("DirObjRegStrVal", "VALUE"), 0, false));
        final Column resIdCol = Column.getColumn("Resource", "RESOURCE_ID", "res_id");
        final Column modifiedAtCol = Column.getColumn("DirObjRegStrVal", "MODIFIED_AT", "modifiedAt");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "DirObjRegStrVal", joinCri, 2));
        selectQuery.setCriteria(joinCri);
        selectQuery.addSelectColumn(resIdCol);
        selectQuery.addSelectColumn(modifiedAtCol);
        selectQuery.addSelectColumn(Column.getColumn("Resource", "DB_ADDED_TIME"));
        final DerivedTable dt1 = new DerivedTable("dt1", (Query)selectQuery);
        final HashMap<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.put("Resource.DB_ADDED_TIME", "row_number() over(partition by NAME order by MEMBER_COUNT desc, Resource.RESOURCE_ID desc) as \"rank_one\"");
        replaceMap.put("\"Resource\".\"DB_ADDED_TIME\"", "row_number() over(partition by NAME order by MEMBER_COUNT desc, Resource.RESOURCE_ID desc) as \"rank_one\"");
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Resource");
        updateQuery.setCriteria(resCri.and(new Criteria(new Column(dt1.getTableAlias(), "rank_one"), (Object)1, 0)));
        updateQuery.addJoin(new Join(Table.getTable("Resource"), (Table)dt1, new String[] { "RESOURCE_ID" }, new String[] { resIdCol.getColumnAlias() }, 2));
        updateQuery.setUpdateColumn("DB_UPDATED_TIME", (Object)new Column(dt1.getTableAlias(), modifiedAtCol.getColumnAlias()));
        final String logMsg = "mapping users from directory by picking the maxmimum resource_id amongst ids with maximum devices associated for each name";
        ResourceSummaryHandler.getInstance().updateResSummary(connection, 2, true);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, replaceMap, "postSyncEngine", logMsg, false);
    }
    
    static {
        DirectoryUserDuplicateHandler.directoryUserDuplicateHandler = null;
    }
}
