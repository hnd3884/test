package com.me.mdm.server.remotesession;

import java.util.Collection;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteControlCapabilitiesUtil
{
    Logger mdmLog;
    
    public RemoteControlCapabilitiesUtil() {
        this.mdmLog = Logger.getLogger("MDMRemoteControlLogger");
    }
    
    public void addCapabilitiesInfoToAllDevices() {
        final ArrayList<Long> resourceList = this.getCapabilitiesInfoApplicableDevices();
        if (resourceList.size() != 0) {
            this.addCapabilitiesInfoCommand(resourceList);
        }
        else {
            this.mdmLog.log(Level.INFO, "RemoteControlCapabilitiesUtil : No device found for which the command should be added");
        }
    }
    
    public void addCapabilitiesCommandToUnpopulatedDevices() {
        final ArrayList<Long> allResourceList = this.getCapabilitiesInfoApplicableDevices();
        final ArrayList<Long> alreadyPopulatedDevices = this.getAlreadyPopulatedDevicesList();
        if (allResourceList.size() == 0) {
            this.mdmLog.log(Level.INFO, "RemoteControlCapabilitiesUtil : Not proceeding with the command addition as there are no devices ");
            return;
        }
        this.removeAlreadyPopulatedDevices(allResourceList, alreadyPopulatedDevices);
        if (allResourceList.size() > 0) {
            this.mdmLog.log(Level.INFO, "RemoteControlCapabilitiesUtil : Going to add command for some devices");
            this.addCapabilitiesInfoCommand(allResourceList);
        }
    }
    
    private ArrayList<Long> getAlreadyPopulatedDevicesList() {
        final ArrayList<Long> resourceIdList = new ArrayList<Long>();
        try {
            final SelectQuery remoteCapabilityQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceRemoteControlCapability"));
            final Join managedDeviceJoin = new Join("DeviceRemoteControlCapability", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Criteria androidCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            remoteCapabilityQuery.addJoin(managedDeviceJoin);
            remoteCapabilityQuery.addSelectColumn(Column.getColumn("DeviceRemoteControlCapability", "RESOURCE_ID"));
            remoteCapabilityQuery.setCriteria(androidCriteria);
            final DataObject dataObject = DataAccess.get(remoteCapabilityQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iter = dataObject.getRows("DeviceRemoteControlCapability");
                this.mdmLog.log(Level.INFO, "RemoteControlCapabilitiesUtil : Data was already populated to {0} devices and will not be resent", dataObject.size("ManagedDevice"));
                while (iter.hasNext()) {
                    resourceIdList.add((Long)iter.next().get("RESOURCE_ID"));
                }
            }
            else {
                this.mdmLog.log(Level.INFO, "The data is not populated for any of the devices");
            }
        }
        catch (final Exception exp) {
            this.mdmLog.log(Level.SEVERE, "RemoteControlCapabilitiesUtil : Error while fetching already populated devices list", exp);
        }
        return resourceIdList;
    }
    
    private ArrayList<Long> getCapabilitiesInfoApplicableDevices() {
        final ArrayList<Long> resourceIdList = new ArrayList<Long>();
        try {
            final SelectQuery androidDevicesQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            androidDevicesQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            final Criteria managedDevices = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria androidDevices = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria unassignedDevices = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)5, 0);
            final Criteria agent431OrAbove = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION"), (Object)2300431, 4);
            androidDevicesQuery.setCriteria(managedDevices.or(unassignedDevices).and(androidDevices).and(agent431OrAbove));
            final DataObject dObj = DataAccess.get(androidDevicesQuery);
            if (!dObj.isEmpty()) {
                this.mdmLog.log(Level.INFO, "RemoteControlCapabilitiesUtil : Command will be added to {0} devices", dObj.size("ManagedDevice"));
                final Iterator<Row> iter = dObj.getRows("ManagedDevice");
                while (iter.hasNext()) {
                    resourceIdList.add((Long)iter.next().get("RESOURCE_ID"));
                }
            }
            else {
                this.mdmLog.log(Level.INFO, "RemoteControlCapabilitiesUtil : No devices found for which command should be added");
            }
        }
        catch (final Exception exp) {
            this.mdmLog.log(Level.SEVERE, "RemoteControlCapabilitiesUtil : Error while fetching applicable devices list", exp);
        }
        return resourceIdList;
    }
    
    private void addCapabilitiesInfoCommand(final ArrayList<Long> resourceList) {
        DeviceCommandRepository.getInstance().addCapabilitiesInfoCommand(resourceList);
    }
    
    private void removeAlreadyPopulatedDevices(final ArrayList<Long> allDevicesList, final ArrayList<Long> alreadyPopulatedDevices) {
        allDevicesList.removeAll(alreadyPopulatedDevices);
    }
}
