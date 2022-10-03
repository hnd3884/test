package com.me.ems.onpremise.uac.summaryserver.revamp.probe;

import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public class RoleRevampUtil
{
    private static final Logger REVAMP_LOGGER;
    Map<Long, Long> roleIdMap;
    
    public RoleRevampUtil(final Map<Long, Long> roleIdMap) {
        this.roleIdMap = roleIdMap;
    }
    
    public void changeExistingRoles() throws DataAccessException {
        RoleRevampUtil.REVAMP_LOGGER.log(Level.INFO, "1) Populating Default UMRoles with SS Ids ");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UMRole"));
        selectQuery.addJoin(new Join("UMRole", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 1));
        selectQuery.addJoin(new Join("UMRole", "UsersRoleMapping", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 1));
        selectQuery.addJoin(new Join("UMRole", "RoleSummaryGroupMapping", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 1));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final DataObject dO = (DataObject)new WritableDataObject();
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("UMRole");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long oldId = (Long)row.get("UM_ROLE_ID");
                if (this.roleIdMap.containsKey(oldId)) {
                    row.set("UM_ROLE_ID", (Object)this.roleIdMap.get(oldId));
                    dO.addRow(row);
                }
            }
            final Iterator<Row> iterator2 = dataObject.getRows("UMRoleModuleRelation");
            while (iterator2.hasNext()) {
                final Row row2 = iterator2.next();
                final Long oldId2 = (Long)row2.get("UM_ROLE_ID");
                if (this.roleIdMap.containsKey(oldId2)) {
                    row2.set("UM_ROLE_ID", (Object)this.roleIdMap.get(oldId2));
                    dO.addRow(row2);
                }
            }
            final Iterator<Row> iterator3 = dataObject.getRows("UsersRoleMapping");
            while (iterator3.hasNext()) {
                final Row row3 = iterator3.next();
                final Long oldId3 = (Long)row3.get("UM_ROLE_ID");
                if (this.roleIdMap.containsKey(oldId3)) {
                    row3.set("UM_ROLE_ID", (Object)this.roleIdMap.get(oldId3));
                    dO.addRow(row3);
                }
            }
            final Iterator<Row> iterator4 = dataObject.getRows("RoleSummaryGroupMapping");
            while (iterator4.hasNext()) {
                final Row row4 = iterator4.next();
                final Long oldId4 = (Long)row4.get("UM_ROLE_ID");
                if (this.roleIdMap.containsKey(oldId4)) {
                    row4.set("UM_ROLE_ID", (Object)this.roleIdMap.get(oldId4));
                    dO.addRow(row4);
                }
            }
        }
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("UMRole");
        DataAccess.delete(deleteQuery);
        SyMUtil.getPersistence().add(dO);
        RoleRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Populating Default UMRoles with SS ID Successfully........");
    }
    
    public void updateUMRoleUVHValues(final Map<Long, Long> roleIdMap) throws DataAccessException {
        try {
            RoleRevampUtil.REVAMP_LOGGER.log(Level.INFO, "Updating UMRoles UVH Values..............");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UVHValues"));
            selectQuery.addSelectColumn(new Column("UVHValues", "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), (Object)"UMRole", 0));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("UVHValues");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long psId = (Long)row.get("GENVALUES");
                    if (roleIdMap.containsKey(psId)) {
                        row.set("GENVALUES", (Object)roleIdMap.get(psId));
                        dataObject.updateRow(row);
                    }
                }
            }
            DataAccess.update(dataObject);
            RoleRevampUtil.REVAMP_LOGGER.log(Level.INFO, "UMRoles UVH Values Updated Successfully...............");
        }
        catch (final Exception re) {
            re.printStackTrace();
            RoleRevampUtil.REVAMP_LOGGER.log(Level.SEVERE, "Exception occurred while updateUVHValue for Default Roles");
            throw new DataAccessException();
        }
    }
    
    static {
        REVAMP_LOGGER = Logger.getLogger("ProbeServerRevampLogger");
    }
}
