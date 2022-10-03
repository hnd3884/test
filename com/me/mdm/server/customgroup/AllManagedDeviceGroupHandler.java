package com.me.mdm.server.customgroup;

import java.util.List;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;

public class AllManagedDeviceGroupHandler
{
    public static final String ALL_MANAGED_MOBILE_DEVICE_GROUP_NAME = "ALL_MANAGED_MOBILE_DEVICE_GROUP";
    
    private static Long createHiddenCustomGroup(final String groupName, final Long customerID) {
        final MDMCustomGroupDetails cgDetails = new MDMCustomGroupDetails();
        cgDetails.groupType = 8;
        cgDetails.platformType = 0;
        cgDetails.groupCategory = 1;
        cgDetails.customerId = customerID;
        cgDetails.domainName = "MDM";
        cgDetails.groupPlatformType = 0;
        cgDetails.groupName = groupName;
        MDMGroupHandler.getInstance().addGroup(cgDetails);
        return cgDetails.resourceId;
    }
    
    public Long getAllDeviceGroup(final String groupName, final Long customerID) throws DataAccessException {
        Long resID = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        final Criteria nameCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)groupName, 0, false);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria resourcetypeCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0);
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.setCriteria(nameCriteria.and(customerCriteria).and(resourcetypeCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            resID = createHiddenCustomGroup(groupName, customerID);
        }
        else {
            resID = (Long)dataObject.getFirstRow("Resource").get("RESOURCE_ID");
        }
        return resID;
    }
    
    public void addMemberToAllDeviceGroup(final String groupName, final Long customerID, final Long resourceID) throws DataAccessException {
        final Long allGroupID = this.getAllDeviceGroup(groupName, customerID);
        final List groupList = new ArrayList();
        groupList.add(allGroupID);
        MDMGroupHandler.getInstance().addMembertoMultipleGroups(groupList, new Long[] { resourceID }, customerID, null);
    }
}
