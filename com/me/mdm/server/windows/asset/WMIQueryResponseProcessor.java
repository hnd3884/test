package com.me.mdm.server.windows.asset;

import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.regex.Matcher;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.regex.Pattern;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.net.URLDecoder;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.resource.MDMResourceDataPopulator;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.core.windows.wmi.WMIQuery;
import java.util.List;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.core.windows.wmi.WMIQueryHandler;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WMIQueryResponseProcessor
{
    private static WMIQueryResponseProcessor wmiQueryResponseProcessor;
    
    public static WMIQueryResponseProcessor getInstance() {
        if (WMIQueryResponseProcessor.wmiQueryResponseProcessor == null) {
            WMIQueryResponseProcessor.wmiQueryResponseProcessor = new WMIQueryResponseProcessor();
        }
        return WMIQueryResponseProcessor.wmiQueryResponseProcessor;
    }
    
    public void processResponse(final SyncMLMessage responseSyncML, final long resourceID) throws Exception {
        final HashMap<String, HashMap<String, Object>> wmiResponseDetails = this.getResponseData(responseSyncML);
        final Set<String> keys = wmiResponseDetails.keySet();
        for (final String key : keys) {
            if (key.contains("ComputerSystemProduct")) {
                this.processComputerSystemProductQueryResponse(wmiResponseDetails.get(key), resourceID);
            }
            else if (key.contains("ComputerSystem")) {
                this.processComputerSystemQueryResponse(wmiResponseDetails.get(key), resourceID);
            }
            else if (key.contains("NetworkAdapterConfig")) {
                this.processNetworkAdapterConfigResponse(wmiResponseDetails.get(key), resourceID);
            }
            else {
                if (!key.contains("Bios")) {
                    continue;
                }
                this.processComputerBiosResponse(wmiResponseDetails.get(key), resourceID);
            }
        }
    }
    
    private HashMap<String, HashMap<String, Object>> getResponseData(final SyncMLMessage responseSyncML) {
        HashMap<String, HashMap<String, Object>> wmiResponseDetails = null;
        if (wmiResponseDetails == null) {
            wmiResponseDetails = new HashMap<String, HashMap<String, Object>>();
        }
        final List responseCmds = responseSyncML.getSyncBody().getResponseCmds();
        for (int i = 0; i < responseCmds.size(); ++i) {
            final SyncMLResponseCommand response = responseCmds.get(i);
            if (response instanceof ResultsResponseCommand) {
                final String commandRef = response.getCmdRef();
                final WMIQuery wmiQuery = WMIQueryHandler.getInstance().getWMIQueryObject(commandRef);
                final List responseItems = response.getResponseItems();
                HashMap<String, Object> wmiResponseMap = wmiResponseDetails.get(wmiQuery.getWmiCommandName());
                if (wmiResponseMap == null) {
                    wmiResponseMap = new HashMap<String, Object>();
                }
                for (int j = 0; j < responseItems.size(); ++j) {
                    final Item responseItem = responseItems.get(j);
                    wmiResponseMap.put(responseItem.getSource().getLocUri(), responseItem.getData());
                }
                wmiResponseDetails.put(wmiQuery.getWmiCommandName(), wmiResponseMap);
            }
        }
        return wmiResponseDetails;
    }
    
    private void processComputerSystemQueryResponse(final HashMap<String, Object> wmiResponseDetail, final Long resourceID) throws Exception {
        final Set<String> keys = wmiResponseDetail.keySet();
        int pcSystemEx = 0;
        String domainName = null;
        for (final String key : keys) {
            if (key.contains("PCSystemTypeEx")) {
                pcSystemEx = Integer.valueOf(wmiResponseDetail.get(key));
            }
            if (key.contains("/Domain")) {
                domainName = String.valueOf(wmiResponseDetail.get(key));
            }
        }
        int modelType = 1;
        if (pcSystemEx == 1 || pcSystemEx == 3 || pcSystemEx == 4) {
            modelType = 4;
        }
        else if (pcSystemEx == 2) {
            modelType = 3;
        }
        else if (pcSystemEx == 8) {
            modelType = 2;
        }
        final SelectQuery modelInfoQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdModelInfo"));
        modelInfoQuery.addJoin(new Join("MdModelInfo", "MdDeviceInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        modelInfoQuery.setCriteria(new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0));
        modelInfoQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(modelInfoQuery);
        final Row modelInfoRow = dataObject.getFirstRow("MdModelInfo");
        if (modelInfoRow != null) {
            final long modelID = (long)modelInfoRow.get("MODEL_ID");
            MDMInvDataPopulator.getInstance().updateModelTypeForModelId(modelID, modelType);
        }
        if (!MDMStringUtils.isEmpty(domainName)) {
            MDMResourceDataPopulator.updateDomainName(resourceID, domainName);
        }
    }
    
    private void processNetworkAdapterConfigResponse(final HashMap<String, Object> wmiResponseDetail, final Long resourceID) throws Exception {
        final Set<String> keySet = wmiResponseDetail.keySet();
        Iterator<String> keys = keySet.iterator();
        final HashMap<String, HashMap<String, Object>> adapterConfigDetails = new HashMap<String, HashMap<String, Object>>();
        while (keys.hasNext()) {
            final String key = keys.next();
            String adapterConfigName = key.substring(0, key.lastIndexOf("/"));
            if (adapterConfigName.contains("Win32_NetworkAdapter/")) {
                String instanceName = adapterConfigName.substring(adapterConfigName.lastIndexOf("/") + 1);
                instanceName = URLDecoder.decode(instanceName, "UTF-8");
                instanceName = instanceName.substring(instanceName.indexOf("\"") + 1, instanceName.lastIndexOf("\""));
                final String modifiedInstanceName = "Win32_NetworkAdapterConfiguration.Index=" + instanceName;
                adapterConfigName = "./cimV2/Win32_NetworkAdapterConfiguration/" + URLEncoder.encode(modifiedInstanceName, "UTF-8");
            }
            final String propName = key.substring(key.lastIndexOf("/") + 1);
            HashMap<String, Object> adapterConfigMap = adapterConfigDetails.get(adapterConfigName);
            if (adapterConfigMap == null) {
                adapterConfigMap = new HashMap<String, Object>();
            }
            adapterConfigMap.put(propName, wmiResponseDetail.get(key));
            adapterConfigDetails.put(adapterConfigName, adapterConfigMap);
        }
        keys = adapterConfigDetails.keySet().iterator();
        final HashMap networkInfo = new HashMap();
        final JSONObject deviceIdsJSON = new JSONObject();
        while (keys.hasNext()) {
            final String adapterConfigName2 = keys.next();
            final HashMap<String, Object> adapterConfig = adapterConfigDetails.get(adapterConfigName2);
            final String ipAddress = adapterConfig.get("IPAddress");
            final String netConnectionType = adapterConfig.get("NetConnectionID");
            final String macAddress = adapterConfig.get("MACAddress");
            if ((ipAddress != null && !ipAddress.trim().isEmpty() && !ipAddress.trim().equalsIgnoreCase("null")) || (netConnectionType != null && !netConnectionType.trim().isEmpty() && !netConnectionType.trim().equalsIgnoreCase("null"))) {
                final Pattern wifiPattern = Pattern.compile("wi(-)*fi", 2);
                final Matcher wifiMatcher = wifiPattern.matcher(netConnectionType.trim());
                if (wifiMatcher.find()) {
                    if (networkInfo.get("WIFI_MAC") != null && (networkInfo.get("WIFI_IP") != null || ipAddress == null || ipAddress.trim().isEmpty())) {
                        continue;
                    }
                    networkInfo.put("WIFI_MAC", macAddress);
                    networkInfo.put("WIFI_IP", ipAddress);
                    deviceIdsJSON.put("WIFI_MAC", (Object)macAddress);
                }
                else if (netConnectionType.trim().toLowerCase().contains("ethernet")) {
                    if (networkInfo.get("ETHERNET_MACS") != null && (networkInfo.get("ETHERNET_IP") != null || ipAddress == null || ipAddress.trim().isEmpty())) {
                        continue;
                    }
                    networkInfo.put("ETHERNET_MACS", macAddress);
                    networkInfo.put("ETHERNET_IP", ipAddress);
                    deviceIdsJSON.put("ETHERNET_MAC", (Object)macAddress);
                }
                else {
                    if (!netConnectionType.trim().toLowerCase().contains("bluetooth")) {
                        continue;
                    }
                    networkInfo.put("BLUETOOTH_MAC", macAddress);
                }
            }
        }
        if (networkInfo.get("BLUETOOTH_MAC") == null) {
            networkInfo.put("BLUETOOTH_MAC", "--");
        }
        MDMInvDataPopulator.getInstance().addOrUpdateNetworkInfo(resourceID, networkInfo);
        ManagedDeviceHandler.getInstance().addOrUpdateManagedDeviceUniqueIdsRow(resourceID, deviceIdsJSON);
    }
    
    private void processComputerBiosResponse(final HashMap<String, Object> wmiResponseDetail, final Long resourceID) throws DataAccessException {
        final Set<String> keys = wmiResponseDetail.keySet();
        String serialNumber = null;
        for (final String key : keys) {
            if (key.contains("SerialNumber")) {
                serialNumber = wmiResponseDetail.get(key);
            }
        }
        if (!MDMStringUtils.isEmpty(serialNumber)) {
            final UpdateQuery serialNumberUpdate = (UpdateQuery)new UpdateQueryImpl("MdDeviceInfo");
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            serialNumberUpdate.setCriteria(resourceCriteria);
            serialNumberUpdate.setUpdateColumn("SERIAL_NUMBER", (Object)serialNumber);
            MDMUtil.getPersistence().update(serialNumberUpdate);
        }
    }
    
    private void processComputerSystemProductQueryResponse(final HashMap<String, Object> wmiResponseDetail, final Long resourceID) throws Exception {
        final Set<String> keys = wmiResponseDetail.keySet();
        String serialNumber = null;
        String uuid = null;
        for (final String key : keys) {
            if (key.endsWith("IdentifyingNumber")) {
                serialNumber = wmiResponseDetail.get(key);
            }
            if (key.endsWith("UUID")) {
                uuid = wmiResponseDetail.get(key);
            }
        }
        if (!MDMStringUtils.isEmpty(serialNumber)) {
            final UpdateQuery serialNumberUpdate = (UpdateQuery)new UpdateQueryImpl("MdDeviceInfo");
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            serialNumberUpdate.setCriteria(resourceCriteria);
            serialNumberUpdate.setUpdateColumn("SERIAL_NUMBER", (Object)serialNumber);
            MDMUtil.getPersistence().update(serialNumberUpdate);
        }
        final JSONObject deviceIdsJSON = new JSONObject();
        deviceIdsJSON.put("SERIAL_NUMBER", (Object)serialNumber);
        deviceIdsJSON.put("UUID", (Object)uuid);
        ManagedDeviceHandler.getInstance().addOrUpdateManagedDeviceUniqueIdsRow(resourceID, deviceIdsJSON);
        final JSONObject deviceForEnrollmentJSON = new JSONObject();
        deviceForEnrollmentJSON.put("SERIAL_NUMBER", (Object)serialNumber);
        final String deviceUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
        new DeviceForEnrollmentHandler().updateDeviceForEnrollmentProps("UDID", deviceUDID, deviceForEnrollmentJSON);
    }
    
    static {
        WMIQueryResponseProcessor.wmiQueryResponseProcessor = null;
    }
}
