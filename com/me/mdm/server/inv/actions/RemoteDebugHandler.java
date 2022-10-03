package com.me.mdm.server.inv.actions;

import java.util.List;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import java.util.ArrayList;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RemoteDebugHandler
{
    private static Logger logger;
    
    public void addRemoteDebugData(final JSONObject requestData) throws DataAccessException, JSONException {
        try {
            final DataObject remoteDebugDO = (DataObject)new WritableDataObject();
            final Row row = new Row("MdDeviceRemoteDebug");
            row.set("COMMAND_HISTORY_ID", (Object)requestData.getLong("command_history_id"));
            row.set("TO_EMAIL_ADDRESS", (Object)requestData.getJSONArray("email_address_list").join(",").replace("\"", ""));
            row.set("TICKET_ID", (Object)requestData.optString("ticket_id", ""));
            row.set("DESCRIPTION", (Object)requestData.optString("description", ""));
            remoteDebugDO.addRow(row);
            MDMUtil.getPersistence().add(remoteDebugDO);
        }
        catch (final DataAccessException e) {
            throw e;
        }
    }
    
    public JSONObject getRemoteDebugRequestData(final long commandHistoryId) throws DataAccessException, JSONException {
        try {
            final JSONObject requestData = new JSONObject();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceRemoteDebug"));
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceRemoteDebug", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceRemoteDebug", "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            if (!dO.isEmpty()) {
                final Row row = dO.getFirstRow("MdDeviceRemoteDebug");
                requestData.put("email_address_list", (Object)row.get("TO_EMAIL_ADDRESS"));
                requestData.put("ticket_id", (Object)row.get("TICKET_ID"));
                requestData.put("description", (Object)row.get("DESCRIPTION"));
            }
            return requestData;
        }
        catch (final DataAccessException e) {
            RemoteDebugHandler.logger.log(Level.WARNING, "Error while handling fetch RemoteDebug Request for retrying:", (Throwable)e);
            throw e;
        }
    }
    
    public void handleRemoteDebugRequest(final long resourceId, final JSONObject requestData) throws Exception {
        try {
            final JSONObject commandInfo = new CommandStatusHandler().getRecentCommandInfo(resourceId, DeviceCommandRepository.getInstance().getCommandID("RemoteDebug"));
            final long fileId = Long.valueOf(String.valueOf(requestData.get("file_id")));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceRemoteDebug"));
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceRemoteDebug", "COMMAND_HISTORY_ID"), (Object)commandInfo.getLong("COMMAND_HISTORY_ID"), 0);
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceRemoteDebug", "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            if (!dO.isEmpty()) {
                final Row row = dO.getFirstRow("MdDeviceRemoteDebug");
                final String emailIds = (String)row.get("TO_EMAIL_ADDRESS");
                final JSONObject details = new JSONObject();
                details.put("TICKET_ID", (Object)row.get("TICKET_ID"));
                details.put("DESCRIPTION", (Object)row.get("DESCRIPTION"));
                final String[] split;
                final String[] emailIdsArray = split = emailIds.split(",");
                for (final String email : split) {
                    this.sendEmail(resourceId, fileId, email, details);
                }
            }
        }
        catch (final Exception e) {
            RemoteDebugHandler.logger.log(Level.WARNING, "Error while handling RemoteDebug Request:", e);
            throw e;
        }
        RemoteDebugHandler.logger.log(Level.INFO, "Remote Debug Data :{0}", requestData.toString());
    }
    
    public void sendEmail(final Long resourceID, final Long fileID, final String email, final JSONObject details) throws Exception {
        try {
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
            final String[] i18nParams = { deviceName, String.valueOf(details.get("TICKET_ID")), String.valueOf(details.get("DESCRIPTION")) };
            final String subject = I18N.getMsg("mdm.mail.remote_debug_subject", new Object[] { deviceName });
            final String content = I18N.getMsg("mdm.mail.remote_debug_content", (Object[])i18nParams);
            final String logFilePath = FileUploadManager.getFilePath(fileID);
            final List<String> attachmentList = new ArrayList<String>();
            attachmentList.add(logFilePath);
            final JSONObject additionalParams = new JSONObject().put("readAttachmentsFromDFS", (Object)Boolean.TRUE);
            MDMMailNotificationHandler.getInstance().sendMail(email, content, subject, attachmentList, additionalParams);
        }
        catch (final Exception ex) {
            RemoteDebugHandler.logger.log(Level.WARNING, ex, () -> "Exception occurred while sending email to device user res ID : " + n);
            throw ex;
        }
    }
    
    static {
        RemoteDebugHandler.logger = Logger.getLogger("MDMLogger");
    }
}
