package com.adventnet.sym.server.mdm.inv.chrome;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class CustomFieldsMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    public static final String ANNOTATED_ASSET_USER = "AnnotatedAssetUser";
    public static final String ANNOTATED_ASSET_LOCATION = "AnnotatedAssetLocation";
    public static final String ANNOTATED_ASSET_ID = "AnnotatedAssetId";
    public static final String ANNOTATED_ASSET_NOTES = "AnnotatedAssetNotes";
    
    public CustomFieldsMDMInventoryImpl() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            final org.json.simple.JSONObject customDetails = MDCustomDetailsRequestHandler.getInstance().getCustomDeviceDetails(inventoryObject.resourceId);
            customDetails.put((Object)"ASSET_OWNER", (Object)inventoryData.optString("AnnotatedAssetUser"));
            customDetails.put((Object)"ASSET_TAG", (Object)inventoryData.optString("AnnotatedAssetId"));
            customDetails.put((Object)"OFFICE", (Object)inventoryData.optString("AnnotatedAssetLocation"));
            customDetails.put((Object)"DESCRIPTION", (Object)inventoryData.optString("AnnotatedAssetNotes"));
            customDetails.put((Object)"IS_MODIFIED", (Object)true);
            MDCustomDetailsRequestHandler.getInstance().addOrUpdateCustomDeviceDetails(customDetails);
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating device details from response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
