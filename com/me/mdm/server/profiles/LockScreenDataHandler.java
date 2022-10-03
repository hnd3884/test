package com.me.mdm.server.profiles;

import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import com.me.mdm.framework.image.ImageWriter;
import java.util.Map;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.ios.payload.transform.DO2LockScreenPayload;
import com.me.mdm.framework.image.ImageProcessor;
import com.me.mdm.framework.image.ImageUtil;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import com.adventnet.persistence.Row;
import com.dd.plist.NSData;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import java.util.logging.Logger;

public class LockScreenDataHandler
{
    private static Logger logger;
    private static Logger configLogger;
    Long resourceId;
    String deviceUDID;
    private static final int CUSTOM_WALLPAPER = 1;
    private static final int UPLOAD_WALLPAPER = 2;
    public static final String DATAOBJECT_CONSTANT = "dataObject";
    public static final String HEIGHT_PERCENTAGE = "HeightPercent";
    public static final String RESOURCE_ID = "ResourceId";
    public static final String STRUDID = "strUDID";
    public static final String WIDTH_RESOLUTION = "WidthResolution";
    public static final String HEIGHT_RESOLUTION = "HeightResolution";
    public static final String FONT_PERCENTAGE = "FONT_PERCENTAGE";
    private static final String IMAGE_DATA = "IMAGE_DATA";
    private static final String USER_DETAILS = "USER_DETAILS";
    
    public LockScreenDataHandler() {
        this.resourceId = null;
        this.deviceUDID = null;
    }
    
    public String getGroupNameForLockScreen(final Long resourceId) {
        final List groupList = MDMCustomGroupUtil.getInstance().getAssociatedGroupName(resourceId);
        String group = "";
        if (!groupList.isEmpty()) {
            final Iterator item = groupList.iterator();
            group = item.next();
            while (item.hasNext()) {
                group = group + " |" + item.next();
            }
        }
        return group;
    }
    
    public HashMap getDynamicVariableForResource(final Long resourceId, final String deviceUDID) {
        final HashMap managedUserInfo = ManagedDeviceHandler.getInstance().getManagedDeviceDetails(deviceUDID);
        final String companyName = MDMApiFactoryProvider.getMDMUtilAPI().getOrgName(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId));
        managedUserInfo.put("GROUPNAME", this.getGroupNameForLockScreen(resourceId));
        managedUserInfo.put("ORGANIZATION_NAME", companyName);
        LockScreenDataHandler.logger.log(Level.FINE, "Dynamic variable for device:{0}", new Object[] { managedUserInfo.toString() });
        return managedUserInfo;
    }
    
    public boolean isLockScreenAvailableForCollection(final Long collectionId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            final Criteria collectionCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria lockScreenCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)new Integer[] { 522 }, 8);
            selectQuery.setCriteria(collectionCriteria.and(lockScreenCriteria));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception ex) {
            LockScreenDataHandler.logger.log(Level.SEVERE, "Exception while checking lockscreen", ex);
        }
        return false;
    }
    
    public boolean lockScreenConfiguredForResource(final Long resourceId) {
        return this.isLockScreenConfiguredForResource(resourceId, null, null);
    }
    
    public boolean isLockScreenConfiguredForResource(final Long resourceId, final Long collectionId, final Criteria additionalCriteria) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria deleteCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            Criteria lockScreenCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)new Integer[] { 522 }, 8);
            if (collectionId != null) {
                lockScreenCriteria = lockScreenCriteria.and(new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 1));
            }
            if (additionalCriteria != null) {
                lockScreenCriteria.and(additionalCriteria);
            }
            selectQuery.setCriteria(resourceCriteria.and(deleteCriteria).and(lockScreenCriteria));
            selectQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("RecentProfileForResource", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_ID"));
            selectQuery.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                LockScreenDataHandler.logger.log(Level.FINE, "Lockscreen is configured for the resources");
                return true;
            }
        }
        catch (final Exception e) {
            LockScreenDataHandler.configLogger.log(Level.SEVERE, "Exception while checking lockscreen configured", e);
        }
        return false;
    }
    
    public NSData writeTextToImage(final JSONObject params) {
        NSData finalImageData = null;
        Iterator lockScreenIterator = null;
        try {
            final DataObject dataObject = (DataObject)params.opt("dataObject");
            this.resourceId = params.optLong("ResourceId");
            this.deviceUDID = params.optString("strUDID");
            final Integer widthResolution = params.optInt("WidthResolution");
            final Integer heightResolution = params.optInt("HeightResolution");
            Integer fontPercentage = params.optInt("FONT_PERCENTAGE");
            fontPercentage = ((fontPercentage != 0) ? fontPercentage : 3);
            LockScreenDataHandler.configLogger.log(Level.INFO, "JSON Params for writing the image:{0}", params.toString());
            if (!dataObject.isEmpty()) {
                lockScreenIterator = dataObject.getRows("LockScreenConfiguration");
                while (lockScreenIterator.hasNext()) {
                    final Row lockScreenRow = lockScreenIterator.next();
                    String wallpaperPath = (String)lockScreenRow.get("WALLPAPER_PATH");
                    wallpaperPath = wallpaperPath.replace("/", File.separator);
                    final String webdir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
                    wallpaperPath = webdir + wallpaperPath;
                    if (this.resourceId != null) {
                        final HashMap managedUserInfo = this.getDynamicVariableForResource(this.resourceId, this.deviceUDID);
                        final String imageName = "default_" + (Object)(double)widthResolution + "x" + (Object)(double)heightResolution + ".jpg";
                        final String fileName = wallpaperPath + File.separator + imageName;
                        final ImageUtil imageUtil = new ImageUtil();
                        NSData imageData = imageUtil.getImageDataFromPath(fileName);
                        if (imageData == null) {
                            final ImageProcessor imageProcessor = new ImageProcessor();
                            final Integer wallpaperType = (Integer)lockScreenRow.get("WALLPAPER_TYPE");
                            if (wallpaperType == 1) {
                                final String colour = (String)lockScreenRow.get("BG_COLOUR");
                                imageData = imageProcessor.drawCustomImageData(widthResolution, heightResolution, colour);
                            }
                            else if (wallpaperType == 2) {
                                final NSData tempData = imageUtil.getImageDataFromPath(wallpaperPath + File.separator + DO2LockScreenPayload.lockScreenImageName);
                                imageData = imageProcessor.getScaleImageData(tempData, widthResolution, heightResolution, "jpg");
                            }
                        }
                        final SortColumn sortColumn = new SortColumn(new Column("LockScreenMessages", "ORDER"), true);
                        dataObject.sortRows("LockScreenMessages", new SortColumn[] { sortColumn });
                        final Iterator messageIterator = dataObject.getRows("LockScreenMessages", new Criteria(new Column("LockScreenToMsgInfo", "LOCK_SCREEN_CONFIGURATION_ID"), lockScreenRow.get("LOCK_SCREEN_CONFIGURATION_ID"), 0));
                        final JSONObject imageParams = new JSONObject();
                        imageParams.put("IMAGE_DATA", (Object)imageData);
                        imageParams.put("HeightResolution", (Object)heightResolution);
                        imageParams.put("USER_DETAILS", (Map)managedUserInfo);
                        imageParams.put("FONT_PERCENTAGE", (Object)fontPercentage);
                        LockScreenDataHandler.configLogger.log(Level.INFO, "Going to write the text to the image with params");
                        finalImageData = this.writeMessageToImage(messageIterator, imageParams, managedUserInfo);
                    }
                }
            }
        }
        catch (final Exception ex) {
            LockScreenDataHandler.logger.log(Level.SEVERE, "Exception while writing the text to image", ex);
        }
        return finalImageData;
    }
    
    private NSData writeMessageToImage(final Iterator messageIterator, final JSONObject imageParams, final HashMap managedUserInfo) {
        NSData imageData = (NSData)imageParams.opt("IMAGE_DATA");
        try {
            final Integer heightResolution = imageParams.optInt("HeightResolution");
            final Integer fontPercentage = imageParams.optInt("FONT_PERCENTAGE");
            final ImageWriter writer = new ImageWriter();
            while (messageIterator.hasNext()) {
                final Row messageRow = messageIterator.next();
                String message = (String)messageRow.get("MESSAGE");
                final Integer offsetPercentage = (Integer)messageRow.get("OFFSET_POSITION");
                writer.setHeightOffset(this.setHeightOffset(offsetPercentage, heightResolution, writer.getHeightOffset()));
                message = DynamicVariableHandler.replaceDynamicVariables(message, this.deviceUDID, managedUserInfo);
                final String textColour = (String)messageRow.get("TEXT_COLOUR");
                imageData = writer.drawTextWithPercentage(imageData, message, textColour, fontPercentage);
            }
            LockScreenDataHandler.configLogger.log(Level.INFO, "Text is writtern in the image with final height offset {0}", writer.getHeightOffset());
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in writing the image to the text", e);
        }
        return imageData;
    }
    
    protected Integer setHeightOffset(final Integer offsetPercentage, final Integer heightResolution, Integer heightOffset) {
        if (offsetPercentage != null) {
            final Integer offset = Math.round((float)(heightResolution / 100 * offsetPercentage));
            heightOffset += offset;
        }
        return heightOffset;
    }
    
    public Integer generateImageForPayload(final DataObject dataObject, final List resolutionList) throws Exception {
        Integer status = 0;
        try {
            final Row lockScreenRow = dataObject.getFirstRow("LockScreenConfiguration");
            final Integer wallpaperType = (Integer)lockScreenRow.get("WALLPAPER_TYPE");
            String wallpaperPath = (String)lockScreenRow.get("WALLPAPER_PATH");
            wallpaperPath = wallpaperPath.replace("/", File.separator);
            final String webdir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
            wallpaperPath = webdir + wallpaperPath;
            final ImageProcessor processor = new ImageProcessor();
            final ImageUtil imageUtil = new ImageUtil();
            NSData imageUploadedData = null;
            if (wallpaperType == 2) {
                imageUploadedData = imageUtil.getImageDataFromPath(wallpaperPath + File.separator + DO2LockScreenPayload.lockScreenImageName);
            }
            LockScreenDataHandler.configLogger.log(Level.INFO, "Going to generate the image for specific models for wallpaper type:{0}", new Object[] { wallpaperType });
            for (int j = 0; j < resolutionList.size(); ++j) {
                final String screenSize = resolutionList.get(j);
                final Double width = Double.parseDouble(screenSize.split("x")[0]);
                final Double height = Double.parseDouble(screenSize.split("x")[1]);
                if (wallpaperType == 1) {
                    final String colourCode = (String)lockScreenRow.get("BG_COLOUR");
                    final String imageName = "default_" + width + "x" + height + ".jpg";
                    final NSData imageData = processor.drawCustomImageData(width, height, colourCode);
                    imageUtil.saveImageFromData(imageData, wallpaperPath, imageName);
                }
                else if (wallpaperType == 2) {
                    final NSData imageData2 = new NSData(imageUploadedData.bytes());
                    final String imageName = "default_" + width + "x" + height + ".jpg";
                    final NSData finalImageData = processor.getScaleImageData(imageData2, width, height, "jpg");
                    imageUtil.saveImageFromData(finalImageData, wallpaperPath, imageName);
                }
            }
            LockScreenDataHandler.configLogger.log(Level.INFO, "Image generated for all the device models for wallpaper path:{0}", new Object[] { wallpaperPath });
        }
        catch (final Exception ex) {
            status = 0;
            LockScreenDataHandler.logger.log(Level.SEVERE, "Exception while generating wallpaper", ex);
            throw ex;
        }
        return status;
    }
    
    static {
        LockScreenDataHandler.logger = Logger.getLogger("MDMLogger");
        LockScreenDataHandler.configLogger = Logger.getLogger("MDMConfigLogger");
    }
}
