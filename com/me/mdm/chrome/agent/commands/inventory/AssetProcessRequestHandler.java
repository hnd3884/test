package com.me.mdm.chrome.agent.commands.inventory;

import com.me.mdm.chrome.agent.ChromeDeviceManager;
import com.me.mdm.chrome.agent.Context;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.core.ProcessRequestHandler;

public class AssetProcessRequestHandler extends ProcessRequestHandler
{
    public Logger logger;
    
    public AssetProcessRequestHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    @Override
    public void processRequest(final Request request, final Response response) {
        this.performAssetScan(request, response);
    }
    
    private void performAssetScan(final Request request, Response response) {
        try {
            this.logger.log(Level.INFO, "Perfoming asset scan");
            final String requestType = request.requestType;
            final ArrayList<InventoryInfo> inventoryInfoList = this.getInventoryInfoList(request.getContainer().getContext(), requestType);
            final JSONObject inventoryData = new JSONObject();
            for (final InventoryInfo inventoryInfo : inventoryInfoList) {
                try {
                    inventoryInfo.fetchInfo(inventoryData);
                }
                catch (final Throwable ex) {
                    this.logger.log(Level.SEVERE, "Exception Occurred on Inventory Info", ex);
                    response.setErrorCode(12070);
                }
            }
            response = this.setResponse(response, inventoryData);
            this.logger.log(Level.INFO, "Inventory data to post: {0}", inventoryData.toString());
        }
        catch (final IOException ex2) {
            this.logger.log(Level.SEVERE, null, ex2);
            response.setErrorCode(12070);
        }
    }
    
    public ArrayList<InventoryInfo> getInventoryInfoList(final Context context, final String inventoryType) throws IOException {
        final ArrayList<InventoryInfo> inventoryInfoList = new ArrayList<InventoryInfo>();
        final ChromeDeviceManager manager = ChromeDeviceManager.getInstance();
        if (inventoryType.equals("AssetScan")) {
            inventoryInfoList.add(manager.getNetworkInfo(context));
            inventoryInfoList.add(manager.getHardwareDetails(context));
            inventoryInfoList.add(manager.getSecurityInfo(context));
            inventoryInfoList.add(manager.getAppInfo(context));
            inventoryInfoList.add(manager.getCustomFieldsInfo(context));
            inventoryInfoList.add(manager.getSystemActivityInfo(context));
        }
        else if (inventoryType.equals("NetworkInfo")) {
            inventoryInfoList.add(manager.getNetworkInfo(context));
        }
        else if (inventoryType.equals("DeviceInfo")) {
            inventoryInfoList.add(manager.getHardwareDetails(context));
        }
        else if (inventoryType.equals("InstalledAppsInfo") || inventoryType.equals("PersonalAppsInfo")) {
            inventoryInfoList.add(manager.getAppInfo(context));
        }
        else if (inventoryType.equals("SecurityInfo")) {
            inventoryInfoList.add(manager.getSecurityInfo(context));
        }
        else if (inventoryType.equals("CustomFieldsInfo")) {
            inventoryInfoList.add(manager.getCustomFieldsInfo(context));
        }
        else if (inventoryType.equals("SystemActivityInfo")) {
            inventoryInfoList.add(manager.getSystemActivityInfo(context));
        }
        return inventoryInfoList;
    }
    
    private Response setResponse(final Response response, final Object obj) {
        try {
            response.setResponseData(obj);
        }
        catch (final Exception e) {
            this.logger.info("Exception Occured to set response data");
        }
        return response;
    }
}
