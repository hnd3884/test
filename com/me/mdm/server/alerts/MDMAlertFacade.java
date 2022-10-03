package com.me.mdm.server.alerts;

import java.util.Hashtable;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.alert.EmailTemplateListenerHandler;
import com.me.devicemanagement.framework.webclient.alert.EmailTemplateChangeEvent;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.net.URLEncoder;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Map;
import com.adventnet.i18n.I18N;
import java.net.URLDecoder;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.alert.AlertsAPI;
import java.util.List;

public class MDMAlertFacade
{
    private List<Long> alertList;
    private String utfCharset;
    private AlertsAPI alertsAPI;
    protected static Logger logger;
    
    public MDMAlertFacade() {
        this.alertList = null;
        this.utfCharset = "UTF-8";
        this.alertsAPI = null;
        this.alertsAPI = WebclientAPIFactoryProvider.getAlertAPI();
    }
    
    public JSONObject getAlertFormat(final JSONObject message) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final Long alertConstantId = APIUtil.getResourceID(message, "alert_id");
        if (this.alertList == null) {
            MDMUtil.getInstance();
            this.alertList = MDMUtil.getMDMAlertIds();
        }
        if (!this.alertList.contains(alertConstantId)) {
            throw new APIHTTPException("COM0008", new Object[] { "alert_id" });
        }
        final Long customerId = APIUtil.getCustomerID(message);
        Properties subDescProp = null;
        subDescProp = AlertsUtil.getInstance().getCustomerKeyDescription((long)customerId, alertConstantId);
        final String subject = subDescProp.getProperty("subject");
        final String description = subDescProp.getProperty("description");
        final Boolean alertReConfigured = ((Hashtable<K, Boolean>)subDescProp).get("alertReConfigured");
        final String title = this.getAlertTitle(alertConstantId);
        jsonObject.put("description", (Object)URLDecoder.decode(description, this.utfCharset));
        jsonObject.put("subject", (Object)URLDecoder.decode(subject, this.utfCharset));
        jsonObject.put("alertReConfigured", (Object)alertReConfigured);
        if (title != null) {
            jsonObject.put("browserTitle", (Object)I18N.getMsg("dc.mdm.enroll.CONFIGURE_MAIL_TEMPLATE", new Object[0]));
            jsonObject.put("invTitle", (Object)I18N.getMsg(title, new Object[0]));
        }
        final LinkedHashMap<String, String> keyValueMap = AlertsUtil.getInstance().getAlertKeyValueMap(alertConstantId);
        jsonObject.put("keyvalueMap", (Map)keyValueMap);
        return jsonObject;
    }
    
    public JSONObject revertAlertFormat(final JSONObject message) throws Exception {
        final Long alertConstantId = APIUtil.getResourceID(message, "alert_id");
        if (this.alertList == null) {
            MDMUtil.getInstance();
            this.alertList = MDMUtil.getMDMAlertIds();
        }
        if (!this.alertList.contains(alertConstantId)) {
            throw new APIHTTPException("COM0008", new Object[] { "alert_id" });
        }
        this.deleteAlertFormat(alertConstantId);
        return this.getAlertFormat(message);
    }
    
    private String getAlertTitle(final Long alertConstantId) {
        String title = "";
        try {
            title = (String)DBUtil.getValueFromDB("DCAlertType", "ALERT_TYPE_ID", (Object)alertConstantId, "ALERT_TYPE");
        }
        catch (final Exception ex) {}
        return title;
    }
    
    private void deleteAlertFormat(final Long alertConstantId) throws Exception {
        final Criteria deleteCriteria = new Criteria(Column.getColumn("CustomerEmailDCAlert", "ALERT_TYPE_ID"), (Object)alertConstantId, 0);
        SyMUtil.getPersistenceLite().delete(deleteCriteria);
    }
    
    public JSONObject modifyAlertFormat(final JSONObject message) throws Exception {
        JSONObject requestBody = new JSONObject();
        final JSONObject responseBody = new JSONObject();
        final Long alertConstantId = APIUtil.getResourceID(message, "alert_id");
        final Long customerId = APIUtil.getCustomerID(message);
        if (this.alertList == null) {
            MDMUtil.getInstance();
            this.alertList = MDMUtil.getMDMAlertIds();
        }
        if (!this.alertList.contains(alertConstantId)) {
            throw new APIHTTPException("COM0024", new Object[] { ": alert_id" });
        }
        requestBody = message.getJSONObject("msg_body");
        if (!requestBody.has("subject") || !requestBody.has("description")) {
            throw new APIHTTPException("COM0009", new Object[] { "subject / description" });
        }
        String subject = String.valueOf(requestBody.get("subject"));
        String description = String.valueOf(requestBody.get("description"));
        description = description.replaceAll("%24", "\\$");
        subject = URLEncoder.encode(subject, this.utfCharset);
        description = URLEncoder.encode(description, this.utfCharset);
        final String validationMsg = this.validateSubjectDescriptionVariables(alertConstantId, subject, description);
        if (MDMStringUtils.isEmpty(validationMsg)) {
            final boolean addorUpdateStatus = this.addOrUpdateSubDesc(customerId, alertConstantId, subject, description);
            final EmailTemplateChangeEvent emailTemplateChangeEvent = new EmailTemplateChangeEvent(customerId, (Long)null, alertConstantId);
            EmailTemplateListenerHandler.getInstance().invokeEmailTemplateChangeSuccessListeners(emailTemplateChangeEvent);
            MDMAlertFacade.logger.log(Level.INFO, "Add or Update Status from updateConfigureFormatPage", addorUpdateStatus);
            return responseBody;
        }
        throw new APIHTTPException("COM0015", new Object[] { validationMsg });
    }
    
    private String validateSubjectDescriptionVariables(final Long alertConstantId, final String subject, final String description) {
        String validationMsg = "";
        if (this.specialHandlingRequired(alertConstantId)) {
            try {
                if (!this.validateDynamicVariablesInAlerts(alertConstantId, subject, description)) {
                    final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EmailDCAlertTask"));
                    query.addSelectColumn(Column.getColumn("EmailDCAlertTask", "*"));
                    final Criteria criAlertTypeID = new Criteria(Column.getColumn("EmailDCAlertTask", "ALERT_TYPE_ID"), (Object)alertConstantId, 0);
                    query.setCriteria(criAlertTypeID);
                    final DataObject dobj = SyMUtil.getPersistence().get(query);
                    if (!dobj.isEmpty()) {
                        final Row emailDCAlertTaskRow = dobj.getFirstRow("EmailDCAlertTask");
                        final String subjectVariables = (String)emailDCAlertTaskRow.get("SUBJECT_VARIABLES");
                        final String descriptionVariables = (String)emailDCAlertTaskRow.get("DESCRIPTION_VARIABLES");
                        if (!subjectVariables.equals("") && !this.validateVariables(subject, subjectVariables)) {
                            validationMsg = I18N.getMsg("dc.alerts.subject_constraint_msg", new Object[] { subjectVariables });
                        }
                        if (validationMsg.equals("") && !descriptionVariables.equals("")) {
                            final String missingVariables = this.findMissingVariables(description, descriptionVariables);
                            if (!missingVariables.equals("")) {
                                validationMsg = I18N.getMsg("dc.alerts.description_constraint_msg", new Object[] { missingVariables });
                            }
                        }
                    }
                }
            }
            catch (final SyMException ex) {
                validationMsg = ex.getMessage();
                MDMAlertFacade.logger.log(Level.WARNING, "SymException occured in validateSubjectDescriptionVariables method  ", (Throwable)ex);
                return validationMsg;
            }
            catch (final Exception ex2) {
                MDMAlertFacade.logger.log(Level.WARNING, "Exception occured in validateSubjectDescriptionVariables method  ", ex2);
            }
        }
        return validationMsg;
    }
    
    private boolean validateVariables(final String content, final String variables) throws UnsupportedEncodingException {
        boolean isValid = true;
        String decodedContent = URLDecoder.decode(content, this.utfCharset);
        decodedContent = SyMUtil.getInstance().decodeURIComponentEquivalent(decodedContent);
        final String[] sVariablesArr = variables.split(",");
        for (int iVariableIndex = 0; iVariableIndex < sVariablesArr.length; ++iVariableIndex) {
            if (decodedContent.indexOf(sVariablesArr[iVariableIndex].trim()) == -1) {
                isValid = false;
            }
        }
        return isValid;
    }
    
    private String findMissingVariables(final String content, final String variables) throws UnsupportedEncodingException {
        String missingVariables = "";
        String decodedContent = URLDecoder.decode(content, this.utfCharset);
        decodedContent = SyMUtil.getInstance().decodeURIComponentEquivalent(decodedContent);
        final String[] sVariablesArr = variables.split(",");
        for (int iVariableIndex = 0; iVariableIndex < sVariablesArr.length; ++iVariableIndex) {
            final String sVariable = sVariablesArr[iVariableIndex].trim();
            if (decodedContent.indexOf(sVariable) == -1) {
                missingVariables = missingVariables + ", " + sVariable;
            }
        }
        if (!missingVariables.equals("")) {
            missingVariables = missingVariables.substring(2);
        }
        return missingVariables;
    }
    
    private boolean addOrUpdateSubDesc(final long customerId, final Long alertConstantId, final String subject, final String description) {
        boolean addorUpdateStatus = false;
        try {
            final DataObject dobj = SyMUtil.getPersistence().constructDataObject();
            final HashMap map = new HashMap();
            map.put("CUSTOMER_ID", customerId);
            map.put("ALERT_TYPE_ID", alertConstantId);
            map.put("SUBJECT", subject);
            map.put("DESCRIPTION", description);
            if (!this.isAlertTechnicianSegmented(alertConstantId)) {
                final Criteria deleteCriteria = new Criteria(Column.getColumn("CustomerEmailDCAlert", "ALERT_TYPE_ID"), (Object)alertConstantId, 0);
                SyMUtil.getPersistenceLite().delete(deleteCriteria);
                final Row alertKeyRow = new Row("CustomerEmailDCAlert");
                alertKeyRow.setAll((Map)map);
                dobj.addRow(alertKeyRow);
            }
            SyMUtil.getPersistence().add(dobj);
            addorUpdateStatus = true;
        }
        catch (final Exception ex) {
            MDMAlertFacade.logger.log(Level.WARNING, "Exception occured at DCAlertFormatAction-addorUpdateSubDescri  ", ex);
        }
        return addorUpdateStatus;
    }
    
    private boolean validateDynamicVariablesInAlerts(final Long alertConstantId, final String subject, final String description) throws Exception {
        if (this.alertsAPI != null) {
            try {
                return this.alertsAPI.validateDynamicVariablesInAlerts(alertConstantId, subject, description);
            }
            catch (final Exception e) {
                MDMAlertFacade.logger.log(Level.SEVERE, "Exception occurred : ", e);
                throw e;
            }
        }
        return false;
    }
    
    private boolean isAlertTechnicianSegmented(final Long alertConstantID) {
        if (this.alertsAPI != null) {
            try {
                return this.alertsAPI.isAlertTechnicianSegmented(alertConstantID);
            }
            catch (final Exception e) {
                MDMAlertFacade.logger.log(Level.SEVERE, "Exception occurred : ", e);
                return true;
            }
            return true;
        }
        return true;
    }
    
    private boolean specialHandlingRequired(final Long alertConstantId) {
        if (this.alertsAPI != null) {
            try {
                return this.alertsAPI.specialHandlingRequired(alertConstantId);
            }
            catch (final Exception e) {
                MDMAlertFacade.logger.log(Level.SEVERE, "Exception occurred : ", e);
                return false;
            }
            return false;
        }
        return false;
    }
    
    static {
        MDMAlertFacade.logger = Logger.getLogger("MDMAPILogger");
    }
}
