package com.me.mdm.server.security.passcode;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class AndroidRecoveryPasscodeMangedDeviceListener extends ManagedDeviceListener
{
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        AndroidRecoveryPasscodeMangedDeviceListener.mdmlogger.info("Checking whether Android Passcode Recovery is available for the managed device");
        try {
            final long resourceId = deviceEvent.resourceID;
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria resourceCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            query.setCriteria(resourceCri);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row managedDeviceRow = dataObject.getFirstRow("ManagedDevice");
                final Row mdDeviceInfoRow = dataObject.getFirstRow("MdDeviceInfo");
                final long agentVersionCode = (long)managedDeviceRow.get("AGENT_VERSION_CODE");
                final boolean isSupervised = (boolean)mdDeviceInfoRow.get("IS_SUPERVISED");
                if (agentVersionCode % 100000L >= 563L && isSupervised) {
                    final ArrayList<Long> resourceList = new ArrayList<Long>();
                    resourceList.add(resourceId);
                    DeviceCommandRepository.getInstance().addAndroidPasscodeRecoveryCommand(resourceList, 1);
                    AndroidRecoveryPasscodeMangedDeviceListener.mdmlogger.info("Android Passcode Recovery Command successfully added");
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in adding Android Passcode Recovery checking after enrollment to device ", e);
        }
    }
}
