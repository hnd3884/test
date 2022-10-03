package com.me.mdm.server.role;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.persistence.DataAccess;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.Arrays;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class ScopeModificationCGMemberListener implements MDMGroupMemberListener
{
    public Logger logger;
    
    public ScopeModificationCGMemberListener() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        if (groupEvent.groupType != 7) {
            final Long groupId = groupEvent.groupID;
            final Long[] resourceIds = groupEvent.memberIds;
            try {
                final Criteria userCustomGroupCriteria = new Criteria(new Column("UserCustomGroupMapping", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
                final DataObject userCustomGroupMappingDO = MDMUtil.getPersistence().get("UserCustomGroupMapping", userCustomGroupCriteria);
                final Iterator iter = userCustomGroupMappingDO.getRows("UserCustomGroupMapping");
                final DataObject userDeviceMappingDO = MDMUtil.getPersistence().get("UserDeviceMapping", (Criteria)null);
                while (iter.hasNext()) {
                    final Row userCustomGroupRow = iter.next();
                    for (final Long resourceId : resourceIds) {
                        final Row userDeviceRow = new Row("UserDeviceMapping");
                        userDeviceRow.set("LOGIN_ID", userCustomGroupRow.get("LOGIN_ID"));
                        userDeviceRow.set("RESOURCE_ID", (Object)resourceId);
                        if (userDeviceMappingDO.findRow(userDeviceRow) == null) {
                            userDeviceMappingDO.addRow(userDeviceRow);
                        }
                    }
                }
                MDMUtil.getPersistence().update(userDeviceMappingDO);
                this.logger.log(Level.INFO, "Updated UserDeviceMapping table for users mapped to custom group id {0} and added new set of resource ids {1}", new Object[] { groupId, Arrays.asList(resourceIds) });
            }
            catch (final DataAccessException ex) {
                this.logger.log(Level.SEVERE, "Error occured while updating scope for users with custom group mapping", (Throwable)ex);
            }
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        if (groupEvent.groupType != 7) {
            final Long groupId = groupEvent.groupID;
            final Long[] resourceIds = groupEvent.memberIds;
            try {
                final Criteria userCustomGroupCriteria = new Criteria(new Column("UserCustomGroupMapping", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
                final DataObject userCustomGroupMappingDO = MDMUtil.getPersistence().get("UserCustomGroupMapping", userCustomGroupCriteria);
                final Iterator iter = userCustomGroupMappingDO.getRows("UserCustomGroupMapping");
                final HashMap<Long, List> excludeMap = this.getExcludeUserDeviceMap(resourceIds);
                final List<Long> userLoginIdList = new ArrayList<Long>();
                final HashMap<Long, List> deletedMap = new HashMap<Long, List>();
                final List allRes = Arrays.asList(resourceIds);
                while (iter.hasNext()) {
                    final Row userCustomGroupRow = iter.next();
                    final Long loginId = (Long)userCustomGroupRow.get("LOGIN_ID");
                    final List deletableRes = new ArrayList(allRes);
                    final List excludeList = excludeMap.get(loginId);
                    if (excludeList != null) {
                        deletableRes.removeAll(excludeList);
                    }
                    deletedMap.put(loginId, deletableRes);
                    final Criteria userDeviceMappingLoginIdCrit = new Criteria(Column.getColumn("UserDeviceMapping", "LOGIN_ID"), (Object)loginId, 0);
                    final Criteria userDeviceMappingResourceIdCrit = new Criteria(Column.getColumn("UserDeviceMapping", "RESOURCE_ID"), (Object)deletableRes.toArray(), 8);
                    DataAccess.delete(userDeviceMappingLoginIdCrit.and(userDeviceMappingResourceIdCrit));
                }
                MDMGroupHandler.getInstance().reassignDevicesToTechnicianCreatedGroupsOnScopeModification(deletedMap, groupEvent.customerId, groupEvent.userId);
                this.logger.log(Level.INFO, "Updated UserDeviceMapping table for user list {0} mapped to custom group id {1} and removed the set of resource ids {2}", new Object[] { userLoginIdList, groupId, Arrays.asList(resourceIds) });
            }
            catch (final DataAccessException ex) {
                this.logger.log(Level.SEVERE, "Error occured while updating scope for users with custom group mapping", (Throwable)ex);
            }
        }
    }
    
    private HashMap<Long, List> getExcludeUserDeviceMap(final Long[] resourceIds) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UserCustomGroupMapping"));
        sQuery.addJoin(new Join("UserCustomGroupMapping", "CustomGroupMemberRel", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
        sQuery.setCriteria(new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceIds, 8));
        final List groupByColumns = new ArrayList();
        groupByColumns.add(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"));
        groupByColumns.add(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        sQuery.addSelectColumns(groupByColumns);
        final GroupByClause grpByCls = new GroupByClause(groupByColumns);
        sQuery.setGroupByClause(grpByCls);
        DMDataSetWrapper ds = null;
        final HashMap<Long, List> usermap = new HashMap<Long, List>();
        try {
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final Long userId = (Long)ds.getValue("LOGIN_ID");
                final Long deviceId = (Long)ds.getValue("MEMBER_RESOURCE_ID");
                List deviceList = usermap.get(userId);
                if (deviceList == null) {
                    deviceList = new ArrayList();
                }
                else {
                    deviceList = usermap.get(userId);
                }
                deviceList.add(deviceId);
                usermap.put(userId, deviceList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error  while getting exlude device list", e);
        }
        return usermap;
    }
}
