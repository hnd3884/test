package com.me.mdm.server.profiles;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Map;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Hashtable;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Deprecated
public class ProfilesToGroupDistributionHandler extends ProfileDistributionHandler
{
    public void validateResourceList(List<Long> groupListFromUser, final int platform) throws DataAccessException, SyMException {
        groupListFromUser = new ArrayList<Long>(new HashSet<Long>(groupListFromUser));
        final DataObject DO = MDMUtil.getPersistence().get("CustomGroup", new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupListFromUser.toArray(new Long[groupListFromUser.size()]), 8));
        if (DO.isEmpty() || DBUtil.getIteratorSize(DO.getRows("CustomGroup")) != groupListFromUser.size()) {
            throw new SyMException(404, ((groupListFromUser.size() == 1) ? "Group" : "Some of the groups are") + " not found", (Throwable)null);
        }
        final List mdmGroupList = MDMGroupHandler.getMDMGroups(platform);
        final List mdmGroupIDList = new ArrayList();
        final Iterator<Hashtable> iterator = (Iterator<Hashtable>)mdmGroupList.iterator();
        while (iterator.hasNext()) {
            mdmGroupIDList.add(iterator.next().get("CUSTOM_GP_ID"));
        }
        groupListFromUser.removeAll(mdmGroupIDList);
        if (!groupListFromUser.isEmpty()) {
            throw new SyMException(409, "Some of the Group Ids provided are invalid / do not belong to same platform as of profile", (Throwable)null);
        }
    }
    
    public void associateCollectionForResource(final Properties properties) {
        ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
    }
    
    public void disassociateCollectionForResource(final Properties properties) {
        ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
    }
    
    public Map getManagedResourcesAssignedForProfile(final Long profileID) throws DataAccessException, QueryConstructionException {
        return new ProfileUtil().getManagedGroupsAssignedForProfile(profileID);
    }
}
