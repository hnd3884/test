package com.me.mdm.server.deployment;

import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class GroupToProfileDeploymentConfigHandler
{
    static final Logger LOGGER;
    
    @Deprecated
    public void addOrUpdateGroupToProfileDeployment(final Long groupId, final Long profileId, final Long deploymentConfigId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupToProfileDeploymentConfig"));
        final Criteria groupCriteria = new Criteria(new Column("GroupToProfileDeploymentConfig", "GROUP_ID"), (Object)groupId, 0);
        final Criteria profileCriteria = new Criteria(new Column("GroupToProfileDeploymentConfig", "PROFILE_ID"), (Object)profileId, 0);
        sQuery.setCriteria(groupCriteria.and(profileCriteria));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("GroupToProfileDeploymentConfig");
            row.set("GROUP_ID", (Object)groupId);
            row.set("PROFILE_ID", (Object)profileId);
            row.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("GroupToProfileDeploymentConfig");
            row.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
            dO.updateRow(row);
        }
        DataAccess.update(dO);
        GroupToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "GroupToProfileDeploymentConfigHandler.addOrUpdateGroupToProfileDeployment processed for Group Id : {0}, Profile Id {1}, DeploymentConfigId {2}", new Object[] { groupId, profileId, deploymentConfigId });
    }
    
    @Deprecated
    public Long getDeploymentConfig(final Long groupId, final Long profileId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupToProfileDeploymentConfig"));
        final Criteria groupCriteria = new Criteria(new Column("GroupToProfileDeploymentConfig", "GROUP_ID"), (Object)groupId, 0);
        final Criteria profileCriteria = new Criteria(new Column("GroupToProfileDeploymentConfig", "PROFILE_ID"), (Object)profileId, 0);
        sQuery.setCriteria(groupCriteria.and(profileCriteria));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Row row = dO.getRow("GroupToProfileDeploymentConfig");
            return (Long)row.get("DEPLOYMENT_CONFIG_ID");
        }
        return null;
    }
    
    public void persistGroupToProfileDeploymentConfig(final List<Long> groupIdList, final List<Long> profileList, final Long deploymentConfigId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupToProfileDeploymentConfig"));
        final Criteria groupCriteria = new Criteria(new Column("GroupToProfileDeploymentConfig", "GROUP_ID"), (Object)groupIdList.toArray(), 8);
        sQuery.setCriteria(groupCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            for (final Long profileId : profileList) {
                for (final Long groupId : groupIdList) {
                    final Row row = new Row("GroupToProfileDeploymentConfig");
                    row.set("GROUP_ID", (Object)groupId);
                    row.set("PROFILE_ID", (Object)profileId);
                    row.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                    dO.addRow(row);
                }
            }
        }
        else {
            for (final Long groupId2 : groupIdList) {
                for (final Long profileId2 : profileList) {
                    final Criteria groupIdCriteria = new Criteria(new Column("GroupToProfileDeploymentConfig", "GROUP_ID"), (Object)groupId2, 0);
                    final Criteria profileIdCriteria = new Criteria(new Column("GroupToProfileDeploymentConfig", "PROFILE_ID"), (Object)profileId2, 0);
                    Row row2 = dO.getRow("GroupToProfileDeploymentConfig", groupIdCriteria.and(profileIdCriteria));
                    if (row2 == null) {
                        row2 = new Row("GroupToProfileDeploymentConfig");
                        row2.set("GROUP_ID", (Object)groupId2);
                        row2.set("PROFILE_ID", (Object)profileId2);
                        row2.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                        dO.addRow(row2);
                    }
                    else {
                        row2.set("GROUP_ID", (Object)groupId2);
                        row2.set("PROFILE_ID", (Object)profileId2);
                        row2.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                        dO.updateRow(row2);
                    }
                }
            }
        }
        DataAccess.update(dO);
        GroupToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "GroupToProfileDeploymentConfigHandler.addOrUpdateGroupToProfileDeployment processed for Group Id : {0}, Profile Id {1}, DeploymentConfigId {2}", new Object[] { groupIdList, profileList, deploymentConfigId });
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
