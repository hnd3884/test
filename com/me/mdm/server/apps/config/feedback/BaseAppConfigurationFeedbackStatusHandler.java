package com.me.mdm.server.apps.config.feedback;

import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public abstract class BaseAppConfigurationFeedbackStatusHandler
{
    protected Logger logger;
    protected DataObject finalDO;
    
    BaseAppConfigurationFeedbackStatusHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        try {
            this.finalDO = MDMUtil.getPersistence().constructDataObject();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception constructing data object", ex);
        }
    }
    
    public static BaseAppConfigurationFeedbackStatusHandler getInstance(final int platformType) {
        BaseAppConfigurationFeedbackStatusHandler baseAppConfigurationFeedbackStatusHandler = null;
        switch (platformType) {
            case 2: {
                baseAppConfigurationFeedbackStatusHandler = new AndroidAppConfigurationFeedbackStatusHandler();
                break;
            }
        }
        return baseAppConfigurationFeedbackStatusHandler;
    }
    
    private String getUniqueFeedBackPathIdentifier(final Long resourceID, final Long appGroupID) {
        final String uniquePath = File.separator + resourceID + File.separator + appGroupID;
        return uniquePath;
    }
    
    private String getDirectoryName(final Long resourceID, final Long appGroupID) {
        final String dirName = File.separator + "appconfigurationfeedback" + this.getUniqueFeedBackPathIdentifier(resourceID, appGroupID);
        return dirName;
    }
    
    protected String getFeedbackRelativePath(final Long resourceID, final Long appGroupID, final Long customerID) {
        final String relativePath = File.separator + "mdm" + File.separator + customerID + this.getDirectoryName(resourceID, appGroupID);
        return relativePath;
    }
    
    protected String checkAndCreateFeedbackDirectory(final Long resourceID, final Long appGroupID, final Long customerID) {
        final String feedbackFilePath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + this.getFeedbackRelativePath(resourceID, appGroupID, customerID);
        try {
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(feedbackFilePath)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(feedbackFilePath);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating mdm app config feedback directory", ex);
        }
        return feedbackFilePath;
    }
    
    protected void writeFeedBackJSONInFile(String filePath, final JSONObject feedbackJSON) {
        try {
            filePath = filePath + File.separator + "app_config_feedback.json";
            ApiFactoryProvider.getFileAccessAPI().writeFile(filePath, feedbackJSON.toString().getBytes());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception writing app config feedback", ex);
        }
    }
    
    protected void addOrUpdateAppConfigurationFeedback(final Long resourceID, final Long appGroupID, final String feedbackPath) {
        try {
            final Criteria resourceCriteria = new Criteria(new Column("AppConfigurationFeedback", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria appGroupCriteria = new Criteria(new Column("AppConfigurationFeedback", "APP_GROUP_ID"), (Object)appGroupID, 0);
            final DataObject dataObject = DataAccess.get("AppConfigurationFeedback", resourceCriteria.and(appGroupCriteria));
            if (dataObject.isEmpty()) {
                final Row row = new Row("AppConfigurationFeedback");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("APP_GROUP_ID", (Object)appGroupID);
                row.set("FEEDBACK_STORED_PATH", (Object)feedbackPath);
                row.set("LAST_UPDATED_TIME", (Object)new Long(System.currentTimeMillis()));
                this.finalDO.addRow(row);
            }
            else {
                final Row row = dataObject.getFirstRow("AppConfigurationFeedback");
                row.set("LAST_UPDATED_TIME", (Object)new Long(System.currentTimeMillis()));
                this.finalDO.updateBlindly(row);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateAppConfigurationFeedback", ex);
        }
    }
    
    protected void persistFinalDO() {
        try {
            MDMUtil.getPersistence().update(this.finalDO);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception persisting finalDO in app config feedback", (Throwable)ex);
        }
    }
    
    protected Long getResourceIDFromMessage(final JSONObject message) {
        final String udid = String.valueOf(message.get("UDID"));
        return ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
    }
    
    public abstract void parseAndStoreAppConfigFeedback(final String p0, final Long p1);
    
    protected static class FeedbackStatusConstants
    {
        public static final String FEEDBACK = "Feedback";
        public static final String FEEDBACKS = "Feedbacks";
        public static final String PACKAGE_NAME = "PackageName";
        public static final String MDM = "mdm";
        public static final String DIR_NAME = "appconfigurationfeedback";
        public static final String FILE_NAME = "app_config_feedback.json";
    }
}
