package com.me.idps.mdm.sync;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.sql.Connection;

class DirectoryGroupDuplicateHandler
{
    private static DirectoryGroupDuplicateHandler directoryGroupDuplicateHandler;
    
    static DirectoryGroupDuplicateHandler getInstance() {
        if (DirectoryGroupDuplicateHandler.directoryGroupDuplicateHandler == null) {
            DirectoryGroupDuplicateHandler.directoryGroupDuplicateHandler = new DirectoryGroupDuplicateHandler();
        }
        return DirectoryGroupDuplicateHandler.directoryGroupDuplicateHandler;
    }
    
    void handleGroupResDuplicates(final Connection connection, Criteria resCri, Criteria dirObjRegCri, final Long dmDomainID, final Long collationID) throws Exception {
        final Criteria cgCri = DirectoryGrouper.getInstance().getCGcri();
        final Criteria resTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0);
        final Criteria dirObjTypeCri = new Criteria(Column.getColumn("DirObjRegStrVal", "DIR_RESOURCE_TYPE"), (Object)new Integer[] { 7, 1003 }, 8);
        resCri = resCri.and(resTypeCri).and(cgCri);
        dirObjRegCri = dirObjRegCri.and(dirObjTypeCri);
        final HashMap<String, String> replaceMap = new HashMap<String, String>();
        final Column dirGrpNameCol = Column.getColumn("DirObjRegStrVal", "VALUE", "dir_grp_name");
        final Column dirGrpTimestampCol = Column.getColumn("DirObjRegStrVal", "MODIFIED_AT", "MODIFIED_AT");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjRegStrVal"));
        selectQuery.setCriteria(dirObjRegCri.and(new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)null, 0)));
        selectQuery.addSelectColumn(dirGrpNameCol);
        selectQuery.addSelectColumn(dirGrpTimestampCol);
        selectQuery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "OBJ_ID"));
        final DerivedTable dt11 = new DerivedTable("dt11", (Query)selectQuery);
        replaceMap.put("DirObjRegStrVal.OBJ_ID", " row_number() over(partition by VALUE order by VALUE) as \"rank_one\"");
        replaceMap.put("\"DirObjRegStrVal\".\"OBJ_ID\"", "row_number() over(partition by VALUE order by VALUE) as \"rank_one\"");
        final Column resIdCol = Column.getColumn("Resource", "RESOURCE_ID", "res_id");
        final Column resGrpNameCol = Column.getColumn("Resource", "NAME", "res_grp_name");
        final SelectQuery resQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        resQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        resQuery.addJoin(new Join("CustomGroup", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        resQuery.setCriteria(resCri.and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 0)));
        resQuery.addSelectColumn(resIdCol);
        resQuery.addSelectColumn(resGrpNameCol);
        resQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        resQuery.addSelectColumn(Column.getColumn("Resource", "DB_UPDATED_TIME"));
        final DerivedTable dt12 = new DerivedTable("dt12", (Query)resQuery);
        replaceMap.put("Resource.CUSTOMER_ID,", "row_number() over(partition by NAME order by NAME) as \"rank_two\",");
        replaceMap.put("\"Resource\".\"CUSTOMER_ID\",", "row_number() over(partition by NAME order by NAME) as \"rank_two\",");
        final Column resIdMappedCol = new Column(dt12.getTableAlias(), resIdCol.getColumnAlias(), "resIdTobeMapped");
        final Column resIdTimeStampCol = new Column(dt11.getTableAlias(), dirGrpTimestampCol.getColumnAlias(), "resIdTimeStampCol");
        final Criteria joinCri = new Criteria(new Column(dt12.getTableAlias(), "rank_two"), (Object)new Column(dt11.getTableAlias(), "rank_one"), 0).and(new Criteria(new Column(dt12.getTableAlias(), resGrpNameCol.getColumnAlias()), (Object)new Column(dt11.getTableAlias(), dirGrpNameCol.getColumnAlias()), 0, false));
        final SelectQuery mappedQuery = (SelectQuery)new SelectQueryImpl((Table)dt12);
        mappedQuery.addJoin(new Join((Table)dt12, (Table)dt11, joinCri, 2));
        mappedQuery.addSelectColumn(resIdMappedCol);
        mappedQuery.addSelectColumn(resIdTimeStampCol);
        final DerivedTable dt13 = new DerivedTable("dt2", (Query)mappedQuery);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Resource");
        updateQuery.addJoin(new Join(Table.getTable("Resource"), (Table)dt13, new String[] { "RESOURCE_ID" }, new String[] { resIdMappedCol.getColumnAlias() }, 2));
        updateQuery.setUpdateColumn("DB_UPDATED_TIME", (Object)new Column(dt13.getTableAlias(), resIdTimeStampCol.getColumnAlias()));
        final String logMsg = "mapping dir grp with res entries by name, row_number with timestamp";
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, replaceMap, "postSyncEngine", logMsg, false);
    }
    
    static {
        DirectoryGroupDuplicateHandler.directoryGroupDuplicateHandler = null;
    }
}
