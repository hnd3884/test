package com.me.mdm.server.profiles.ios;

import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.dd.plist.NSObject;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSArray;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.profiles.LockScreenDataHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSLockScreenHandler
{
    private static Logger logger;
    
    public String checkLockScreenConfForResource(final Long resourceId, String strQuery, final String commandUDID) {
        IOSLockScreenHandler.logger.log(Level.FINE, "Entered for checking the lockscreen configuration");
        final LockScreenDataHandler dataHandler = new LockScreenDataHandler();
        final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUDID);
        final Criteria cOSVersion4 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
        final Criteria cOSVersion5 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
        final Criteria cOSVersion6 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
        final Criteria cOSVersion7 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
        if (dataHandler.isLockScreenConfiguredForResource(resourceId, Long.parseLong(collectionId), cOSVersion4.and(cOSVersion5).and(cOSVersion6).and(cOSVersion7))) {
            final NSDictionary commandDict = PlistWrapper.getInstance().getDictForKey("Command", strQuery);
            final NSArray settingArray = (NSArray)commandDict.get((Object)"Settings");
            for (int i = 0; i < settingArray.count(); ++i) {
                final NSDictionary itemDict = (NSDictionary)settingArray.objectAtIndex(i);
                final String itemName = String.valueOf(itemDict.get((Object)"Item"));
                if (itemName.equalsIgnoreCase("Wallpaper")) {
                    final Integer wallpaperPosition = Integer.parseInt(itemDict.get((Object)"Where").toString());
                    if (wallpaperPosition == 1) {
                        IOSLockScreenHandler.logger.log(Level.FINE, "Removing the lockscreen wallpaper for wallpaper payload");
                        settingArray.remove(i);
                    }
                    else if (wallpaperPosition == 3) {
                        IOSLockScreenHandler.logger.log(Level.FINE, "Removing the lockscreen wallpaper for wallpaper payload");
                        itemDict.put("Where", (Object)2);
                    }
                }
            }
            final IOSCommandPayload commandPayload = PayloadHandler.getInstance().createCommandPayload("Settings");
            commandPayload.setCommandUUID(commandUDID, false);
            commandPayload.getCommandDict().put("Settings", (NSObject)settingArray);
            strQuery = commandPayload.getPayloadDict().toXMLPropertyList();
        }
        return strQuery;
    }
    
    public JSONObject deviceResolution(final Long resourceId) {
        final JSONObject deviceResolution = new JSONObject();
        try {
            IOSLockScreenHandler.logger.log(Level.FINE, "Getting the device resolution for resource:{0}", resourceId);
            final JSONObject modelInfo = this.getModelInfoForResource(resourceId);
            final String modelCode = modelInfo.getString("PRODUCT_NAME");
            final Integer modelType = modelInfo.getInt("MODEL_TYPE");
            String screenSize = MDMApiFactoryProvider.getMdmModelNameMappingHandlerAPI().getiOSDeviceResolution(modelCode);
            if (screenSize != null) {
                IOSLockScreenHandler.logger.log(Level.FINE, "Screen size read from MdSupportedDevices table for resource:{0}", resourceId);
            }
            else {
                final Criteria resourceCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
                query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
                query.addJoin(new Join("MdModelInfo", "MdIOSDeviceModel", new String[] { "PRODUCT_NAME" }, new String[] { "MODEL_NAME" }, 2));
                query.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
                query.addSelectColumn(new Column("MdDeviceInfo", "MODEL_ID"));
                query.addSelectColumn(new Column("MdModelInfo", "MODEL_ID"));
                query.addSelectColumn(new Column("MdModelInfo", "MODEL_TYPE"));
                query.addSelectColumn(new Column("MdIOSDeviceModel", "MODEL_NAME"));
                query.addSelectColumn(new Column("MdIOSDeviceModel", "MODEL_ID"));
                query.addSelectColumn(new Column("MdIOSDeviceModel", "MODEL_HEIGHT_RESOLUTION"));
                query.addSelectColumn(new Column("MdIOSDeviceModel", "MODEL_WIDTH_RESOLUTION"));
                query.setCriteria(resourceCriteria);
                final DataObject dataObject = MDMUtil.getPersistence().get(query);
                if (!dataObject.isEmpty()) {
                    final Row iOSDeviceModel = dataObject.getFirstRow("MdIOSDeviceModel");
                    final Integer widthResolution = (Integer)iOSDeviceModel.get("MODEL_WIDTH_RESOLUTION");
                    final Integer heightResolution = (Integer)iOSDeviceModel.get("MODEL_HEIGHT_RESOLUTION");
                    screenSize = widthResolution + "x" + heightResolution;
                    IOSLockScreenHandler.logger.log(Level.FINE, "Screen size read from MdIosDeviceModel table for resource:{0}", resourceId);
                }
            }
            if (screenSize != null && modelType != null) {
                deviceResolution.put("SCREEN_SIZE", (Object)screenSize);
                deviceResolution.put("MODEL_TYPE", (Object)modelType);
                IOSLockScreenHandler.logger.log(Level.FINE, "Width resolution {0} and Height resolution {1} for resource{2}", new Object[] { screenSize.split("x")[0], screenSize.split("x")[1], resourceId });
            }
        }
        catch (final Exception e) {
            IOSLockScreenHandler.logger.log(Level.SEVERE, "Exception while getting iOS device resolution", e);
        }
        return deviceResolution;
    }
    
    private JSONObject getModelInfoForResource(final Long resourceId) {
        final JSONObject modelInfo = new JSONObject();
        try {
            final Criteria resourceCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            query.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            query.addSelectColumn(new Column("MdModelInfo", "MODEL_ID"));
            query.addSelectColumn(new Column("MdModelInfo", "MODEL_TYPE"));
            query.addSelectColumn(new Column("MdModelInfo", "PRODUCT_NAME"));
            query.setCriteria(resourceCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final String modelCode = (String)dataObject.getFirstRow("MdModelInfo").get("PRODUCT_NAME");
            final Integer modelType = (Integer)dataObject.getFirstRow("MdModelInfo").get("MODEL_TYPE");
            modelInfo.put("PRODUCT_NAME", (Object)modelCode);
            modelInfo.put("MODEL_TYPE", (Object)modelType);
        }
        catch (final Exception e) {
            IOSLockScreenHandler.logger.log(Level.SEVERE, "Exception while getting model info for resource {0} - {1}", new Object[] { resourceId, e });
        }
        return modelInfo;
    }
    
    public List getiOSUniqueDeviceResolution(final Integer orientation) {
        List resolutionList = new ArrayList();
        try {
            IOSLockScreenHandler.logger.log(Level.FINE, "Going to get the unique device model for the iOS in orientation {0}", new Object[] { orientation });
            resolutionList = MDMApiFactoryProvider.getMdmModelNameMappingHandlerAPI().getiOSUniqueDeviceResolutions(orientation);
            IOSLockScreenHandler.logger.log(Level.INFO, "Unique device resolution list from MdSupportedDevices : {0}", resolutionList.toString());
            final List resolutionListFromMdIosDeviceModel = this.getUniqueDeviceResolutionsFromMdIosDeviceModel(orientation);
            IOSLockScreenHandler.logger.log(Level.INFO, "Unique device resolution list from MdIosDeviceModel : {0}", resolutionListFromMdIosDeviceModel.toString());
            if (resolutionListFromMdIosDeviceModel.size() > resolutionList.size()) {
                IOSLockScreenHandler.logger.log(Level.FINE, "Unique resolution list read from MdIosDeviceModel table");
                return resolutionListFromMdIosDeviceModel;
            }
            IOSLockScreenHandler.logger.log(Level.FINE, "Unique resolution list read from MdSupportedDevices table");
        }
        catch (final Exception e) {
            IOSLockScreenHandler.logger.log(Level.SEVERE, "Exception while getting iOS device resolution", e);
        }
        return resolutionList;
    }
    
    private List getUniqueDeviceResolutionsFromMdIosDeviceModel(final Integer orientation) {
        List resolutionList = new ArrayList();
        try {
            IOSLockScreenHandler.logger.log(Level.FINE, "Going to get the unique device model for the iOS in orientation {0}", new Object[] { orientation });
            final HashMap<String, Integer> tempHashMap = new HashMap<String, Integer>();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdIOSDeviceModel"));
            final Column distinctWidthColumn = new Column("MdIOSDeviceModel", "MODEL_WIDTH_RESOLUTION");
            final Column distinctHeightColumn = new Column("MdIOSDeviceModel", "MODEL_HEIGHT_RESOLUTION");
            final Column modelName = new Column("MdIOSDeviceModel", "MODEL_SPECIFIC_NAME");
            final Criteria nullCriteria = new Criteria(new Column("MdIOSDeviceModel", "MODEL_HEIGHT_RESOLUTION"), (Object)null, 3).and(new Criteria(new Column("MdIOSDeviceModel", "MODEL_WIDTH_RESOLUTION"), (Object)null, 3));
            query.addSelectColumn(distinctWidthColumn);
            query.addSelectColumn(distinctHeightColumn);
            query.addSelectColumn(new Column("MdIOSDeviceModel", "MODEL_ID"));
            query.addSelectColumn(modelName);
            query.setCriteria(nullCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Iterator iterator = dataObject.getRows("MdIOSDeviceModel");
            while (iterator.hasNext()) {
                int temp = 0;
                final Row modelRow = iterator.next();
                final Integer widthResolution = (Integer)modelRow.get("MODEL_WIDTH_RESOLUTION");
                final Integer heightResolution = (Integer)modelRow.get("MODEL_HEIGHT_RESOLUTION");
                final String modelSpecificName = (String)modelRow.get("MODEL_SPECIFIC_NAME");
                final Object result = tempHashMap.get(widthResolution + "x" + heightResolution);
                if (modelSpecificName.contains("iPhone")) {
                    temp = 1;
                }
                else if (modelSpecificName.contains("iPad")) {
                    temp = 4;
                }
                else if (modelSpecificName.contains("iPod")) {
                    temp = 2;
                }
                final int tempCount = (result != null) ? ((int)result | temp) : temp;
                tempHashMap.put(widthResolution + "x" + heightResolution, tempCount);
            }
            IOSLockScreenHandler.logger.log(Level.FINE, "Model specific unique resolution and supported devices: {0}", tempHashMap.toString());
            final HashMap finalHashMap = this.getFinalResolutionHashMap(tempHashMap, orientation);
            resolutionList = this.getResolutionForHashMap(finalHashMap);
            IOSLockScreenHandler.logger.log(Level.INFO, "Unique device resolution list:{0}", resolutionList.toString());
        }
        catch (final Exception e) {
            IOSLockScreenHandler.logger.log(Level.SEVERE, "Exception while getting iOS device resolution", e);
        }
        return resolutionList;
    }
    
    private List getResolutionForHashMap(final HashMap resolutionMap) {
        final List resolutionList = new ArrayList();
        try {
            final Set resolutionSet = resolutionMap.keySet();
            for (final String key : resolutionSet) {
                resolutionList.add(key);
            }
            IOSLockScreenHandler.logger.log(Level.FINE, "Final unique resolution for iOS:{0}", resolutionList.toString());
        }
        catch (final Exception ex) {
            IOSLockScreenHandler.logger.log(Level.SEVERE, "Exception while getting unique device resolution from hashmap", ex);
        }
        return resolutionList;
    }
    
    private HashMap getFinalResolutionHashMap(final HashMap resolutionMap, final Integer orientation) {
        final HashMap finalHashMap = new HashMap();
        if (orientation == 2) {
            final Set resolutionSet = resolutionMap.keySet();
            for (final String key : resolutionSet) {
                final Integer value = resolutionMap.get(key);
                if (value == 4) {
                    final String[] resolution = key.split("x");
                    final Integer widthResolution = Integer.parseInt(resolution[0]);
                    final Integer heightResolution = Integer.parseInt(resolution[1]);
                    final String changedKey = heightResolution + "x" + widthResolution;
                    finalHashMap.put(changedKey, "");
                }
                else if (value > 4) {
                    final String[] resolution = key.split("x");
                    final Integer widthResolution = Integer.parseInt(resolution[0]);
                    final Integer heightResolution = Integer.parseInt(resolution[1]);
                    final String changedKey = heightResolution + "x" + widthResolution;
                    finalHashMap.put(changedKey, "");
                    finalHashMap.put(key, "");
                }
                else {
                    finalHashMap.put(key, "");
                }
            }
            IOSLockScreenHandler.logger.log(Level.FINE, "Final unique resolution:{0}", finalHashMap.toString());
            return finalHashMap;
        }
        return resolutionMap;
    }
    
    static {
        IOSLockScreenHandler.logger = Logger.getLogger("MDMLogger");
    }
}
