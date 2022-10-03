package com.me.mdm.uem;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.core.management.ManagementConstants;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;

public class ModernCollectionUtil
{
    public static Boolean isModernCollection(final Long collectionID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
        selectQuery.addJoin(new Join("ProfileToCollection", "ProfileToManagement", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToManagement", "ManagementModel", new String[] { "MANAGEMENT_ID" }, new String[] { "MANAGEMENT_ID" }, 2));
        final Criteria collecitonCriteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria modernCriteria = new Criteria(Column.getColumn("ManagementModel", "MANAGEMENT_IDENTIFIER"), (Object)ManagementConstants.Types.MODERN_MGMT, 0);
        selectQuery.setCriteria(collecitonCriteria.and(modernCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ManagementModel", "MANAGEMENT_ID"));
        return !MDMUtil.getPersistenceLite().get(selectQuery).isEmpty();
    }
}
