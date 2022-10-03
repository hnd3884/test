package com.me.mdm.webclient.alert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.alert.AlertsAPI;

public class MDMAlertsImpl implements AlertsAPI
{
    private static final Logger LOGGER;
    private static final int MDM_ALERTS = 12;
    
    public boolean isAlertTechnicianSegmented(final Long alertConstantID) {
        try {
            Criteria alertCriteria = new Criteria(Column.getColumn("DCAlertType", "MODULE_ID"), (Object)12, 0);
            alertCriteria = alertCriteria.and(new Criteria(Column.getColumn("DCAlertType", "ALERT_TYPE_ID"), (Object)alertConstantID, 0));
            final DataObject alertDO = DataAccess.get("DCAlertType", alertCriteria);
            if (!alertDO.isEmpty()) {
                return false;
            }
        }
        catch (final DataAccessException e) {
            MDMAlertsImpl.LOGGER.log(Level.WARNING, "Exception while checking for MDM alert.. ", (Throwable)e);
            return true;
        }
        return true;
    }
    
    public boolean specialHandlingRequired(final Long alertConstantId) {
        boolean specialHandlingRequired = false;
        if (alertConstantId.equals(MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE) || alertConstantId.equals(MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_AD) || alertConstantId.equals(MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH) || alertConstantId.equals(MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE) || alertConstantId.equals(MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_AD) || alertConstantId.equals(MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH) || alertConstantId.equals(MDMAlertConstants.IOS_APP_DISTRIBUTION_MAIL_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.ANDROID_APP_DISTRIBUTION_MAIL_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.WINDOWS_APP_DISTRIBUTION_MAIL_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE) || alertConstantId.equals(MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_AD) || alertConstantId.equals(MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH) || alertConstantId.equals(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_WINDOWS_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_WINDOWS_DOWNLOAD_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_AD) || alertConstantId.equals(MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH) || alertConstantId.equals(MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE) || alertConstantId.equals(MDMAlertConstants.MDM_EAS_NOTIFY_USER) || alertConstantId.equals(MDMAlertConstants.MDM_EAS_UNAUTHORISED_DEVICE) || alertConstantId.equals(MDMAlertConstants.GENERIC_APP_DISTRIBUTION_MAIL_TEMPLATE)) {
            specialHandlingRequired = true;
        }
        return specialHandlingRequired;
    }
    
    public boolean validateDynamicVariablesInAlerts(final Long alertConstantId, final String subject, final String description) throws Exception {
        final boolean specialHandlingRequired = false;
        if (alertConstantId.equals(MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE) || alertConstantId.equals(MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_AD) || alertConstantId.equals(MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH) || alertConstantId.equals(MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE) || alertConstantId.equals(MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_AD) || alertConstantId.equals(MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH) || alertConstantId.equals(MDMAlertConstants.IOS_APP_DISTRIBUTION_MAIL_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.ANDROID_APP_DISTRIBUTION_MAIL_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.WINDOWS_APP_DISTRIBUTION_MAIL_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE) || alertConstantId.equals(MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_AD) || alertConstantId.equals(MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH) || alertConstantId.equals(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_WINDOWS_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_WINDOWS_DOWNLOAD_TEMPLATE) || alertConstantId.equals(MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_AD) || alertConstantId.equals(MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH) || alertConstantId.equals(MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE) || alertConstantId.equals(MDMAlertConstants.MDM_EAS_NOTIFY_USER) || alertConstantId.equals(MDMAlertConstants.MDM_EAS_UNAUTHORISED_DEVICE) || alertConstantId.equals(MDMAlertConstants.GENERIC_APP_DISTRIBUTION_MAIL_TEMPLATE)) {
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
                    throw new SyMException(41001, I18N.getMsg("dc.alerts.subject_constraint_msg", new Object[] { subjectVariables }), (Throwable)null);
                }
                if (!descriptionVariables.equals("")) {
                    final String missingVariables = this.findMissingVariables(description, descriptionVariables);
                    if (!missingVariables.equals("")) {
                        throw new SyMException(41001, I18N.getMsg("dc.alerts.description_constraint_msg", new Object[] { missingVariables }), (Throwable)null);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean validateVariables(final String content, final String variables) throws UnsupportedEncodingException {
        boolean isValid = true;
        String decodedContent = URLDecoder.decode(content, "UTF-8");
        decodedContent = SyMUtil.getInstance().decodeURIComponentEquivalent(decodedContent);
        final String[] sVariablesArr = variables.split(",");
        for (int iVariableIndex = 0; iVariableIndex < sVariablesArr.length; ++iVariableIndex) {
            if (!sVariablesArr[iVariableIndex].trim().equalsIgnoreCase("$user_name$") && decodedContent.indexOf(sVariablesArr[iVariableIndex].trim()) == -1) {
                isValid = false;
            }
        }
        return isValid;
    }
    
    private String findMissingVariables(final String content, final String variables) throws UnsupportedEncodingException {
        String missingVariables = "";
        String decodedContent = URLDecoder.decode(content, "UTF-8");
        decodedContent = SyMUtil.getInstance().decodeURIComponentEquivalent(decodedContent);
        final String[] sVariablesArr = variables.split(",");
        for (int iVariableIndex = 0; iVariableIndex < sVariablesArr.length; ++iVariableIndex) {
            final String sVariable = sVariablesArr[iVariableIndex].trim();
            if (!sVariable.equalsIgnoreCase("$user_name$") && decodedContent.indexOf(sVariable) == -1) {
                missingVariables = missingVariables + ", " + sVariable;
            }
        }
        if (!missingVariables.equals("")) {
            missingVariables = missingVariables.substring(2);
        }
        return missingVariables;
    }
    
    static {
        LOGGER = Logger.getLogger(MDMAlertsImpl.class.getName());
    }
}
