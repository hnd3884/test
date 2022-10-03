package com.me.mdm.server.profiles;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Map;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;

public class ProfilesToDeviceDistributionHandler extends ProfileDistributionHandler
{
    public void validateResourceList(final List<Long> deviceList, final int platform) throws Exception {
        if (DBUtil.getRecordActualCount("ManagedDevice", "RESOURCE_ID", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(new Long[deviceList.size()]), 8).and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0))) == 0) {
            throw new SyMException(404, ((deviceList.size() == 1) ? "Device is" : "Devices are") + " not managed", (Throwable)null);
        }
        if (DBUtil.getRecordActualCount("ManagedDevice", "RESOURCE_ID", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(new Long[deviceList.size()]), 8).and(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 1)).and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0))) > 0) {
            throw new SyMException(409, ((deviceList.size() == 1) ? "Device is invalid / does" : "Some of the devices are invalid / do") + " not belong to same platform as of profile", (Throwable)null);
        }
    }
    
    public void associateCollectionForResource(final Properties properties) {
        ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
    }
    
    public void disassociateCollectionForResource(final Properties properties) {
        ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
    }
    
    public Map getManagedResourcesAssignedForProfile(final Long profileID) throws DataAccessException, QueryConstructionException {
        return new ProfileUtil().getManagedDevicesAssignedForProfile(profileID);
    }
}
