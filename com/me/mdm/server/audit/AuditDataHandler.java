package com.me.mdm.server.audit;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AuditDataHandler
{
    private Logger logger;
    private static final String MAINTAIN_EVENT_LOG = "maintain_event_log";
    
    public AuditDataHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public Long addOrUpdateAuditInfo(final JSONObject auditData) {
        DMSecurityLogger.info(this.logger, "AuditDataHandler", "addOrUpdateAuditInfo", "Audit Data : {0}", (Object)auditData);
        Long auditID = auditData.optLong("AUDIT_ID", -1L);
        final String auditMsg = auditData.optString("AUDIT_MESSAGE", "");
        if (auditMsg != null) {
            try {
                DataObject auditDO = null;
                if (auditID == -1L) {
                    auditDO = MDMUtil.getPersistence().constructDataObject();
                    Row auditRow = new Row("AuditInfo");
                    auditRow.set("TICKET_ID", (Object)auditData.optString("TICKET_ID", (String)null));
                    auditRow.set("AUDIT_MESSAGE", (Object)auditData.optString("AUDIT_MESSAGE"));
                    auditDO.addRow(auditRow);
                    auditDO = MDMUtil.getPersistence().add(auditDO);
                    auditRow = auditDO.getFirstRow("AuditInfo");
                    auditID = (Long)auditRow.get("AUDIT_ID");
                }
                else {
                    final Criteria auditCrit = new Criteria(Column.getColumn("AuditInfo", "AUDIT_ID"), (Object)auditID, 0);
                    auditDO = MDMUtil.getPersistence().get("AuditInfo", auditCrit);
                    if (!auditDO.isEmpty()) {
                        final Row auditRow2 = auditDO.getFirstRow("AuditInfo");
                        auditRow2.set("TICKET_ID", (Object)auditData.optString("TICKET_ID", (String)null));
                        auditRow2.set("AUDIT_MESSAGE", (Object)auditData.optString("AUDIT_MESSAGE"));
                        auditDO.updateRow(auditRow2);
                        MDMUtil.getPersistence().update(auditDO);
                    }
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateAuditInfo ", ex);
            }
        }
        else {
            this.logger.log(Level.INFO, "Audit message empty, We cant add new row");
        }
        this.logger.log(Level.INFO, "AUDIT_ID:{0}", auditID);
        return auditID;
    }
    
    public JSONObject getAuditInfo(final Long auditID) {
        final JSONObject auditInfo = new JSONObject();
        try {
            final Criteria auditCrit = new Criteria(Column.getColumn("AuditInfo", "AUDIT_ID"), (Object)auditID, 0);
            final DataObject auditDO = MDMUtil.getPersistence().get("AuditInfo", auditCrit);
            if (!auditDO.isEmpty()) {
                final Row auditRow = auditDO.getFirstRow("AuditInfo");
                auditInfo.put("AUDIT_ID", (Object)auditID);
                auditInfo.put("TICKET_ID", auditRow.get("TICKET_ID"));
                auditInfo.put("AUDIT_MESSAGE", auditRow.get("AUDIT_MESSAGE"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getAuditInfo", ex);
        }
        this.logger.log(Level.INFO, "Audit information Audit ID : {0}; Audit data : {1}", new Object[] { auditID, auditInfo });
        return auditInfo;
    }
    
    public JSONObject getAuditSettings() throws JSONException {
        final String noOfDays = SyMUtil.getSyMParameter("maintain_event_log");
        final JSONObject auditSettings = new JSONObject();
        auditSettings.put("maintain_event_log", (Object)noOfDays);
        return auditSettings;
    }
    
    public JSONObject addOrUpdateAuditSettings(final JSONObject message) throws JSONException {
        JSONObject auditSettings;
        try {
            auditSettings = message.getJSONObject("msg_body");
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        this.validateAuditSettings(auditSettings);
        final Integer noOfDays = auditSettings.getInt("maintain_event_log");
        SyMUtil.updateSyMParameter("maintain_event_log", noOfDays.toString());
        return auditSettings;
    }
    
    private void validateAuditSettings(final JSONObject auditSettings) throws JSONException {
        if (!auditSettings.has("maintain_event_log") || auditSettings.getInt("maintain_event_log") < 1) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
    }
}
