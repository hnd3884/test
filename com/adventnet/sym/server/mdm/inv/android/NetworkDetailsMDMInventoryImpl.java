package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import org.json.JSONArray;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class NetworkDetailsMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String SUBSCRIBER_MNC = "SubscriberMNC";
    private static final String CURRENT_CARRIER_NETWORK = "CurrentCarrierNetwork";
    private static final String SUBSCRIBER_CARRIER_NETWORK = "SubscriberCarrierNetwork";
    private static final String WIFI_MAC = "WiFiMAC";
    private static final String IP_ADDRESS = "IPAddr";
    private static final String CURRENT_MCC = "CurrentMCC";
    private static final String PHONE_NUMBER = "PhoneNumber";
    private static final String CURRENT_MNC = "CurrentMNC";
    private static final String BLUETOOTH_MAC = "BluetoothMAC";
    private static final String ICCID = "ICCID";
    private static final String SUBSCRIBER_MCC = "SubscriberMCC";
    private static final String VOICE_ROAMING_ENABLED = "VoiceRoamingEnabled";
    private static final String DATA_ROAMING_ENABLED = "DataRoamingEnabled";
    private static final String IS_ROAMING = "IsRoaming";
    private static final String IMEI = "IMEI";
    private static final String MEID = "MEID";
    private static final String SLOT = "SimSlot";
    private static final String IMSI = "PrimaryIMSI";
    private static final String SLOT_SPECIFIC_IMSI = "IMSI";
    
    public NetworkDetailsMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> networkInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            networkInfo.put("BLUETOOTH_MAC", inventoryData.optString("BluetoothMAC", "--"));
            networkInfo.put("WIFI_MAC", inventoryData.optString("WiFiMAC", (String)null));
            networkInfo.put("WIFI_IP", inventoryData.optString("IPAddr", "--"));
            networkInfo.put("VOICE_ROAMING_ENABLED", inventoryData.optString("VoiceRoamingEnabled", "true"));
            networkInfo.put("DATA_ROAMING_ENABLED", inventoryData.optString("DataRoamingEnabled", "true"));
            networkInfo.put("IMSI", inventoryData.optString("PrimaryIMSI", "--"));
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateNetworkInfo(inventoryObject.resourceId, networkInfo);
            final ArrayList simArrayList = new ArrayList();
            if (inventoryData.has("NetworkDetailsofSimSlots")) {
                final JSONArray networkData = (JSONArray)inventoryData.get("NetworkDetailsofSimSlots");
                if (networkData.length() > 0 && ((JSONObject)networkData.get(0)).length() > 4) {
                    for (int i = 0; i < networkData.length(); ++i) {
                        final JSONObject slotSpecificData = networkData.getJSONObject(i);
                        final HashMap simInfoHash = new HashMap();
                        simInfoHash.put("IMEI", slotSpecificData.optString("IMEI"));
                        simInfoHash.put("MEID", slotSpecificData.optString("MEID", (String)null));
                        simInfoHash.put("ICCID", slotSpecificData.optString("ICCID", (String)null));
                        simInfoHash.put("CURRENT_CARRIER_NETWORK", slotSpecificData.optString("CurrentCarrierNetwork", (String)null));
                        simInfoHash.put("PHONE_NUMBER", slotSpecificData.optString("PhoneNumber", (String)null));
                        simInfoHash.put("IS_ROAMING", slotSpecificData.optString("IsRoaming", "true"));
                        simInfoHash.put("CURRENT_MCC", slotSpecificData.optString("CurrentMCC", (String)null));
                        simInfoHash.put("CURRENT_MNC", slotSpecificData.optString("CurrentMNC", (String)null));
                        simInfoHash.put("SLOT", slotSpecificData.optInt("SimSlot") + 1);
                        simInfoHash.put("IMSI", slotSpecificData.optString("IMSI"));
                        simArrayList.add(simInfoHash);
                    }
                }
                else {
                    final HashMap simInfo = new HashMap();
                    simInfo.put("IMEI", inventoryData.optString("IMEI", (String)null));
                    simInfo.put("MEID", inventoryData.optString("MEID", (String)null));
                    simInfo.put("ICCID", inventoryData.optString("ICCID", (String)null));
                    simInfo.put("CURRENT_CARRIER_NETWORK", inventoryData.optString("CurrentCarrierNetwork", (String)null));
                    simInfo.put("SUBSCRIBER_CARRIER_NETWORK", inventoryData.optString("SubscriberCarrierNetwork", (String)null));
                    simInfo.put("PHONE_NUMBER", inventoryData.optString("PhoneNumber", (String)null));
                    simInfo.put("IS_ROAMING", inventoryData.optString("IsRoaming", "true"));
                    simInfo.put("SUBSCRIBER_MCC", inventoryData.optString("SubscriberMCC", (String)null));
                    simInfo.put("SUBSCRIBER_MNC", inventoryData.optString("SubscriberMNC", (String)null));
                    simInfo.put("CURRENT_MCC", inventoryData.optString("CurrentMCC", (String)null));
                    simInfo.put("CURRENT_MNC", inventoryData.optString("CurrentMNC", (String)null));
                    simInfo.put("SLOT", 1);
                    simArrayList.add(simInfo);
                }
                invDataPopulator.addOrUpdateSimInfo(inventoryObject.resourceId, simArrayList);
            }
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating security details form response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
