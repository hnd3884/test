package com.me.mdm.server.updates.osupdates.ios;

import java.util.Hashtable;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class UpdateRetryHandler implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    public static final Long DEFAULT_INSTALL_STATUS_RETRY_PERIOD;
    public static final Long DEFAULT_DOWNLOAD_STATUS_RETRY_PERIOD;
    public static final Long DEFAULT_FAILURE_RETRY_PERIOD;
    public static final int DEFAULT_MAX_FAILURE_RETRIES = 3;
    public static final int DEFAULT_MAX_INSTALL_STATUS_RETRIES = 2;
    public static final int DEFAULT_MAX_DOWNLOAD_STATUS_RETRIES = 5;
    public static final int DEFAULT_DOWNLOAD_STATUS_EXP_BACKOFF_RETRY_THRESHOLD = 3;
    static final String DOWNLOAD_STATUS_RETRIES = "download_retries";
    static final String INSTALL_STATUS_RETRIES = "install_retries";
    static final String FAILURE_RETRIES = "failure_retries";
    static final String TASK_CLASS = "com.me.mdm.server.updates.osupdates.ios.UpdateRetryHandler";
    static final String RESOURCE_ID = "res";
    static final String COLLN_COMMAND_ID = "cmd_id";
    private static final String IS_CMD_SCHEDULED_FOR_RETRY = "is_scheduled_retry";
    
    public static long getInstallStatusRetryPeriod() {
        try {
            return Long.parseLong(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("INSTALL_STATUS_RETRY_PERIOD"));
        }
        catch (final Exception ex) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "Exception in getting INSTALL_STATUS_RETRY_PERIOD from file.Retuning {0} as INSTALL_STATUS_RETRY_PERIOD \n Exception Trace: {1}", new Object[] { UpdateRetryHandler.DEFAULT_INSTALL_STATUS_RETRY_PERIOD, ex });
            return UpdateRetryHandler.DEFAULT_INSTALL_STATUS_RETRY_PERIOD;
        }
    }
    
    public static long getDownloadStatusRetryPeriod() {
        try {
            return Long.parseLong(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("DOWNLOAD_STATUS_RETRY_PERIOD"));
        }
        catch (final Exception ex) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "Exception in getting DOWNLOAD_STATUS_RETRY_PERIOD from file.Retuning {0} as DOWNLOAD_STATUS_RETRY_PERIOD \n Exception Trace: {1}", new Object[] { UpdateRetryHandler.DEFAULT_DOWNLOAD_STATUS_RETRY_PERIOD, ex });
            return UpdateRetryHandler.DEFAULT_DOWNLOAD_STATUS_RETRY_PERIOD;
        }
    }
    
    public static long getFailureRetryPeriod() {
        try {
            return Long.parseLong(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("FAILURE_RETRY_PERIOD"));
        }
        catch (final Exception ex) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "Exception in getting FAILURE_RETRY_PERIOD from file.Retuning {0} as FAILURE_RETRY_PERIOD \n Exception Trace: {1}", new Object[] { UpdateRetryHandler.DEFAULT_FAILURE_RETRY_PERIOD, ex });
            return UpdateRetryHandler.DEFAULT_FAILURE_RETRY_PERIOD;
        }
    }
    
    public static int getMaxFailureRetries() {
        try {
            return Integer.parseInt(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("MAX_FAILURE_RETRIES"));
        }
        catch (final Exception ex) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "Exception in getting MAX_FAILURE_RETRIES from file.Retuning {0} as MAX_FAILURE_RETRIES \n Exception Trace: {1}", new Object[] { 3, ex });
            return 3;
        }
    }
    
    public static int getMaxInstallStatusRetries() {
        try {
            return Integer.parseInt(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("MAX_INSTALL_STATUS_RETRIES"));
        }
        catch (final Exception ex) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "Exception in getting MAX_INSTALL_STATUS_RETRIES from file.Retuning {0} as MAX_INSTALL_STATUS_RETRIES \n Exception Trace: {1}", new Object[] { 2, ex });
            return 2;
        }
    }
    
    public static int getMaxDownloadStatusRetries() {
        try {
            return Integer.parseInt(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("MAX_DOWNLOAD_STATUS_RETRIES"));
        }
        catch (final Exception ex) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "Exception in getting MAX_DOWNLOAD_STATUS_RETRIES from file.Retuning {0} as MAX_DOWNLOAD_STATUS_RETRIES \n Exception Trace: {1}", new Object[] { 5, ex });
            return 5;
        }
    }
    
    public static int getDownloadStatusExpBackoffRetryThreshold() {
        try {
            return Integer.parseInt(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("DOWNLOAD_STATUS_EXP_BACKOFF_RETRY_THRESHOLD"));
        }
        catch (final Exception ex) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "Exception in getting DOWNLOAD_STATUS_EXP_BACKOFF_RETRY_THRESHOLD from file.Retuning {0} as DOWNLOAD_STATUS_EXP_BACKOFF_RETRY_THRESHOLD \n Exception Trace: {1}", new Object[] { 3, ex });
            return 3;
        }
    }
    
    public void executeTask(final Properties properties) {
        try {
            UpdateRetryHandler.LOGGER.log(Level.INFO, "UpdateRetryHandler executeTask() props.. {0}", properties.toString());
            final Long resourceId = Long.parseLong(((Hashtable<K, Object>)properties).get("res").toString());
            final Long cmdId = Long.parseLong(((Hashtable<K, Object>)properties).get("cmd_id").toString());
            final Long collectionId = DeviceCommandRepository.getInstance().getCollectionId(cmdId);
            if (collectionId != null) {
                final Criteria rc = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
                final Criteria cc = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
                selectQuery.setCriteria(rc.and(cc));
                selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
                if (!dO.isEmpty()) {
                    final ResourceOSUpdateDataHandler dataHandler = new ResourceOSUpdateDataHandler();
                    final DataObject availableDO = dataHandler.getAvailableUpdatesForResource(resourceId);
                    if (!availableDO.isEmpty()) {
                        this.executeCommand(resourceId, cmdId, collectionId, properties);
                    }
                }
            }
        }
        catch (final Exception e) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "error...", e);
        }
    }
    
    public void executeCommand(final Long resourceId, final Long cmdId, final Long collectionID, final Properties props) {
        try {
            UpdateRetryHandler.LOGGER.log(Level.INFO, "UpdateRetryHandler executeCommand() called..");
            final List<Long> resList = new ArrayList<Long>();
            resList.add(resourceId);
            final List<Long> cmdList = new ArrayList<Long>();
            cmdList.add(cmdId);
            final JSONObject cmdParams = new JSONObject();
            cmdParams.put("COLLECTION_ID", (Object)collectionID);
            cmdParams.put("install_retries", (Object)props.getProperty("install_retries", "0"));
            cmdParams.put("download_retries", (Object)props.getProperty("download_retries", "0"));
            cmdParams.put("failure_retries", (Object)props.getProperty("failure_retries", "0"));
            final Long profileID = (Long)DBUtil.getValueFromDB("RecentProfileToColln", "COLLECTION_ID", (Object)collectionID, "PROFILE_ID");
            final Long userID = (Long)ProfileUtil.getCreatedUserDetailsForProfile(profileID).get("USER_ID");
            cmdParams.put("UserId", (Object)userID);
            SeqCmdRepository.getInstance().executeSequentially(resList, cmdList, cmdParams);
            NotificationHandler.getInstance().SendNotification(resList, 1);
        }
        catch (final Exception e) {
            UpdateRetryHandler.LOGGER.log(Level.SEVERE, "error... ", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        DEFAULT_INSTALL_STATUS_RETRY_PERIOD = 1800000L;
        DEFAULT_DOWNLOAD_STATUS_RETRY_PERIOD = 600000L;
        DEFAULT_FAILURE_RETRY_PERIOD = 1800000L;
    }
}
