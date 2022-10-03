package com.me.emsalerts.notifications.core;

import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.mailmanager.MailContentGeneratorUtil;
import java.io.File;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TemplatesUtil
{
    Logger logger;
    
    public TemplatesUtil() {
        this.logger = Logger.getLogger("EMSAlertsLogger");
    }
    
    public List getEventKeyList(final Long subCategoryID) {
        final List keysList = new ArrayList();
        try {
            final DataObject keysDO = new TemplatesDAOUtil().getEventKeysDO(subCategoryID);
            final Iterator keysItr = keysDO.getRows("EventVariables");
            while (keysItr.hasNext()) {
                final Row keysRow = keysItr.next();
                final HashMap keysObj = new HashMap();
                keysObj.put("variableID", keysRow.get("VARIABLE_ID"));
                keysObj.put("name", keysRow.get("NAME"));
                keysObj.put("displayName", I18N.getMsg((String)keysRow.get("DISPLAY_NAME"), new Object[0]));
                keysObj.put("description", keysRow.get("DEFAULT_VALUE"));
                keysList.add(keysObj);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while retrieving variable list for Events", e);
        }
        return keysList;
    }
    
    public Long getTemplateIDForUser(final Long subCategoryID, final Long userID, final Long customerID) {
        try {
            final Long allCusTemplateID = this.checkAllCustCritFindTemplateID(subCategoryID, userID);
            if (allCusTemplateID != -1L) {
                return allCusTemplateID;
            }
            final SelectQuery templateQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("NotificationTemplate"));
            final Join customerRelJoin = new Join("NotificationTemplate", "TemplateToCustomerRel", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            final Criteria userIDCrit = new Criteria(Column.getColumn("NotificationTemplate", "CREATED_BY"), (Object)userID, 0);
            final Criteria custIDCrit = new Criteria(Column.getColumn("TemplateToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria subCategoryCrit = new Criteria(Column.getColumn("NotificationTemplate", "SUB_CATEGORY_ID"), (Object)subCategoryID, 0);
            final Criteria queryCrit = userIDCrit.and(custIDCrit).and(subCategoryCrit);
            templateQuery.addJoin(customerRelJoin);
            templateQuery.setCriteria(queryCrit);
            templateQuery.addSelectColumn(Column.getColumn("NotificationTemplate", "TEMPLATE_ID"));
            templateQuery.addSelectColumn(Column.getColumn("NotificationTemplate", "CREATED_BY"));
            templateQuery.addSelectColumn(Column.getColumn("NotificationTemplate", "SUB_CATEGORY_ID"));
            templateQuery.addSelectColumn(Column.getColumn("TemplateToCustomerRel", "TEMPLATE_ID"));
            templateQuery.addSelectColumn(Column.getColumn("TemplateToCustomerRel", "CUSTOMER_ID"));
            final DataObject templateDO = SyMUtil.getPersistence().get(templateQuery);
            if (!templateDO.isEmpty()) {
                final Row templateRow = templateDO.getFirstRow("NotificationTemplate");
                return (Long)templateRow.get("TEMPLATE_ID");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured while getting the template id ", e);
        }
        return -1L;
    }
    
    public Long checkAllCustCritFindTemplateID(final Long subCategoryID, final Long userID) {
        try {
            final Criteria templateUserCrit = new Criteria(Column.getColumn("NotificationTemplate", "CREATED_BY"), (Object)userID, 0);
            final Criteria templateCusCrit = templateUserCrit.and(new Criteria(Column.getColumn("NotificationTemplate", "IS_FOR_ALL_CUSTOMERS"), (Object)Boolean.TRUE, 0));
            final Criteria subCategoryIDCrit = templateCusCrit.and(new Criteria(Column.getColumn("NotificationTemplate", "SUB_CATEGORY_ID"), (Object)subCategoryID, 0));
            final DataObject templateDO = DataAccess.get("NotificationTemplate", subCategoryIDCrit);
            if (!templateDO.isEmpty()) {
                final Row templateRow = templateDO.getFirstRow("NotificationTemplate");
                return (Long)templateRow.get("TEMPLATE_ID");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured while checking any customer exists with all customer criteria ", e);
        }
        return new Long(-1L);
    }
    
    public Long getDefaultTemplateForSubCategory(final Long subCategoryID) throws SyMException {
        final Long systemUserID = DMUserHandler.getUserID("DC-SYSTEM-USER");
        return this.checkAllCustCritFindTemplateID(subCategoryID, systemUserID);
    }
    
    public String getEmailAddressForEvent(final Long customerID, final Long userID, final List eventIDList) {
        try {
            final DataObject mediumDO = DataAccess.get("EventMediums", new Criteria(Column.getColumn("EventMediums", "MEDIUM_NAME"), (Object)"EMAIL", 0));
            final Long mediumID = (Long)(mediumDO.isEmpty() ? -1L : mediumDO.getFirstRow("EventMediums").get("MEDIUM_ID"));
            final DataObject eventTemplateDO = new TemplatesDAOUtil().getAlertsForEventID(eventIDList, customerID, userID, mediumID);
            if (!eventTemplateDO.isEmpty()) {
                final Row eventTemplateRow = eventTemplateDO.getFirstRow("EventToTemplateRel");
                final Long eventToTemplateRelID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
                final DataObject eventEmailDetailsDO = DataAccess.get("EventEmailDetails", new Criteria(Column.getColumn("EventEmailDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventToTemplateRelID, 0));
                if (!eventEmailDetailsDO.isEmpty()) {
                    return (String)eventEmailDetailsDO.getFirstRow("EventEmailDetails").get("EMAIL_ADDRESS");
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured while getting email address for an event");
        }
        return "";
    }
    
    public Long[] getSMSUserIDForEvent(final Long customerID, final Long userID, final List eventIDList) {
        try {
            final DataObject mediumDO = DataAccess.get("EventMediums", new Criteria(Column.getColumn("EventMediums", "MEDIUM_NAME"), (Object)"SMS", 0));
            final Long mediumID = (Long)(mediumDO.isEmpty() ? -1L : mediumDO.getFirstRow("EventMediums").get("MEDIUM_ID"));
            final DataObject eventTemplateDO = new TemplatesDAOUtil().getAlertsForEventID(eventIDList, customerID, userID, mediumID);
            if (!eventTemplateDO.isEmpty()) {
                final Row eventTemplateRow = eventTemplateDO.getFirstRow("EventToTemplateRel");
                final Long eventToTemplateRelID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
                final DataObject eventEmailDetailsDO = DataAccess.get("EventSMSDetails", new Criteria(Column.getColumn("EventSMSDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventToTemplateRelID, 0));
                if (!eventEmailDetailsDO.isEmpty()) {
                    int notificationUserCount = 0;
                    final Long[] notificationUser = new Long[eventEmailDetailsDO.size("EventSMSDetails")];
                    final Iterator eventSMSItr = eventEmailDetailsDO.getRows("EventSMSDetails");
                    while (eventSMSItr.hasNext()) {
                        final Row eventSMSRow = eventSMSItr.next();
                        notificationUser[notificationUserCount++] = (Long)eventSMSRow.get("USER_ID");
                    }
                    return notificationUser;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured while getting SMS details for an event");
        }
        return new Long[0];
    }
    
    public Hashtable isTemplateConfigured(final String subCategoryName, final Long userID, final Long customerID) throws DataAccessException, QueryConstructionException, SyMException {
        final DataObject subCategoryDO = DataAccess.get("EventSubCategory", new Criteria(Column.getColumn("EventSubCategory", "NAME"), (Object)subCategoryName, 0));
        final Long subCategoryID = (Long)subCategoryDO.getFirstRow("EventSubCategory").get("SUB_CATEGORY_ID");
        Long templateID = this.getTemplateIDForUser(subCategoryID, userID, customerID);
        if (templateID == -1L) {
            templateID = this.getDefaultTemplateForSubCategory(subCategoryID);
        }
        final List eventForSubCategoryList = this.getAllEventIDsForSubCategory(subCategoryID);
        final boolean isEmailConfigured = this.isTemplateConfiguredForMedium(templateID, this.getMediumIdByName("EMAIL"), eventForSubCategoryList, customerID, userID);
        final boolean isSMSConfigured = this.isTemplateConfiguredForMedium(templateID, this.getMediumIdByName("SMS"), eventForSubCategoryList, customerID, userID);
        final Hashtable configurationInfo = new Hashtable();
        configurationInfo.put("status", isEmailConfigured || isSMSConfigured);
        configurationInfo.put("emailStatus", isEmailConfigured);
        configurationInfo.put("smsStatus", isSMSConfigured);
        return configurationInfo;
    }
    
    public boolean isTemplateConfiguredForMedium(final Long templateID, final Long mediumID, final List eventForSubCategoryList, final Long customerID, final Long userID) throws QueryConstructionException, DataAccessException {
        final SelectQuery eventTemplateQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventToTemplateRel"));
        final Join eventTemplateCustomerJoin = new Join("EventToTemplateRel", "EventTemplateCustomerRel", new String[] { "EVENT_TEMPLATE_REL_ID" }, new String[] { "EVENT_TEMPLATE_REL_ID" }, 2);
        final Criteria eventIDCrit = new Criteria(Column.getColumn("EventToTemplateRel", "EVENT_ID"), (Object)eventForSubCategoryList.toArray(), 8);
        final Criteria templateIDCrit = eventIDCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"), (Object)templateID, 0));
        final Criteria mediumIdCrit = templateIDCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)mediumID, 0));
        final Criteria userIDCrit = mediumIdCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "CREATED_BY"), (Object)userID, 0));
        final Criteria queryCrit = userIDCrit.and(new Criteria(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
        eventTemplateQuery.addJoin(eventTemplateCustomerJoin);
        eventTemplateQuery.setCriteria(queryCrit);
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "TEMPLATE_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "EVENT_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "MEDIUM_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "CREATED_BY"));
        eventTemplateQuery.addSelectColumn(new Column("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventTemplateCustomerRel", "CUSTOMER_ID"));
        final DataObject eventTemplateDO = SyMUtil.getPersistence().get(eventTemplateQuery);
        return !eventTemplateDO.isEmpty();
    }
    
    public DataObject getEventDO(final Long userID, final Long customerID) throws DataAccessException {
        final SelectQuery eventsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventToTemplateRel"));
        final Join customerJoin = new Join("EventToTemplateRel", "EventTemplateCustomerRel", new String[] { "EVENT_TEMPLATE_REL_ID" }, new String[] { "EVENT_TEMPLATE_REL_ID" }, 2);
        final Criteria userIDCrit = new Criteria(Column.getColumn("EventToTemplateRel", "CREATED_BY"), (Object)userID, 0);
        final Criteria customerIDCrit = new Criteria(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        eventsQuery.addJoin(customerJoin);
        eventsQuery.setCriteria(userIDCrit.and(customerIDCrit));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "CREATED_BY"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "EVENT_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "ENABLED_STATUS"));
        eventsQuery.addSelectColumn(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"));
        final DataObject eventsDO = SyMUtil.getPersistence().get(eventsQuery);
        return eventsDO;
    }
    
    public Long getSubCategoryIDForEventCode(final Long eventCode) throws DataAccessException {
        final DataObject subCategoryDO = DataAccess.get("EMSEvents", new Criteria(Column.getColumn("EMSEvents", "EVENT_CODE"), (Object)eventCode, 0));
        if (!subCategoryDO.isEmpty()) {
            final Row subCategoryRow = subCategoryDO.getFirstRow("EMSEvents");
            return (Long)subCategoryRow.get("SUB_CATEGORY_ID");
        }
        return -1L;
    }
    
    public void addOrUpdateEmailAddressToEvent(final Long eventTemplateRelId, final String emailAddress) throws DataAccessException {
        final DataObject eventEmailDO = DataAccess.get("EventEmailDetails", new Criteria(Column.getColumn("EventEmailDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelId, 0));
        if (eventEmailDO.isEmpty()) {
            final Row eventEmailRow = new Row("EventEmailDetails");
            eventEmailRow.set("EVENT_TEMPLATE_REL_ID", (Object)eventTemplateRelId);
            eventEmailRow.set("EMAIL_ADDRESS", (Object)emailAddress);
            eventEmailDO.addRow(eventEmailRow);
            DataAccess.add(eventEmailDO);
        }
        else {
            final Row eventEmailRow = eventEmailDO.getFirstRow("EventEmailDetails");
            eventEmailRow.set("EMAIL_ADDRESS", (Object)emailAddress);
            eventEmailDO.updateRow(eventEmailRow);
            DataAccess.update(eventEmailDO);
        }
    }
    
    public void addOrUpdateSMSDetailsToEvent(final Long eventTemplateRelId, final Long userID) throws DataAccessException {
        final Criteria eventTemplateIDCrit = new Criteria(Column.getColumn("EventSMSDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelId, 0);
        final Criteria userIDCrit = new Criteria(Column.getColumn("EventSMSDetails", "USER_ID"), (Object)userID, 0);
        final DataObject eventSMSDO = DataAccess.get("EventSMSDetails", userIDCrit.and(eventTemplateIDCrit));
        if (eventSMSDO.isEmpty()) {
            final Row eventSMSRow = new Row("EventSMSDetails");
            eventSMSRow.set("EVENT_TEMPLATE_REL_ID", (Object)eventTemplateRelId);
            eventSMSRow.set("USER_ID", (Object)userID);
            eventSMSDO.addRow(eventSMSRow);
            DataAccess.add(eventSMSDO);
        }
    }
    
    public void deleteSMSDetailsToEvent(final Long eventTemplateRelID) throws DataAccessException {
        DataAccess.delete("EventSMSDetails", new Criteria(Column.getColumn("EventSMSDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
    }
    
    public void deleteEmailDetailsToEvent(final Long eventTemplateRelID) throws DataAccessException {
        DataAccess.delete("EventEmailDetails", new Criteria(Column.getColumn("EventEmailDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
    }
    
    public void deletePhoneNoDetailsToEvent(final Long eventTemplateRelID) throws DataAccessException {
        DataAccess.delete("EventPhoneNoDetails", new Criteria(Column.getColumn("EventPhoneNoDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
    }
    
    public Long getMediumIdByName(final String name) throws DataAccessException {
        final DataObject mediumDO = DataAccess.get("EventMediums", new Criteria(Column.getColumn("EventMediums", "MEDIUM_NAME"), (Object)name, 0));
        if (!mediumDO.isEmpty()) {
            return (Long)mediumDO.getFirstRow("EventMediums").get("MEDIUM_ID");
        }
        return -1L;
    }
    
    public Long getEventIDForCode(final Long eventCode) throws DataAccessException {
        final DataObject eventDO = DataAccess.get("EMSEvents", new Criteria(Column.getColumn("EMSEvents", "EVENT_CODE"), (Object)eventCode, 0));
        return (Long)eventDO.getFirstRow("EMSEvents").get("EVENT_ID");
    }
    
    public List getAllEventIDsForSubCategory(final Long eventSubCategoryID) throws DataAccessException {
        final List<Long> eventIDList = new ArrayList<Long>();
        final DataObject eventDO = DataAccess.get("EMSEvents", new Criteria(Column.getColumn("EMSEvents", "SUB_CATEGORY_ID"), (Object)eventSubCategoryID, 0));
        final Iterator eventItr = eventDO.getRows("EMSEvents");
        while (eventItr.hasNext()) {
            final Row eventRow = eventItr.next();
            eventIDList.add((Long)eventRow.get("EVENT_ID"));
        }
        return eventIDList;
    }
    
    public boolean checkDefaultTemplateData(final Long subCategoryID, final Long mediumID, final String mediumData) throws DataAccessException, SyMException {
        final Long templateID = this.getDefaultTemplateForSubCategory(subCategoryID);
        final Criteria templateIDCriteria = new Criteria(Column.getColumn("TemplateToMediumRel", "TEMPLATE_ID"), (Object)templateID, 0);
        final Criteria mediumIDCriteria = new Criteria(Column.getColumn("TemplateToMediumRel", "MEDIUM_ID"), (Object)mediumID, 0);
        final DataObject templateDO = DataAccess.get("TemplateToMediumRel", templateIDCriteria.and(mediumIDCriteria));
        if (!templateDO.isEmpty()) {
            final String defaultTemplateData = String.valueOf(templateDO.getFirstRow("TemplateToMediumRel").get("MEDIUM_DATA"));
            if (defaultTemplateData.equals(mediumData)) {
                return true;
            }
        }
        return false;
    }
    
    public String getDefaultKeyInformation(final Long subCategoryID) throws Exception {
        String description = "";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventVariables"));
        selectQuery.addJoin(new Join("EventVariables", "SubCategoryToVariableRel", new String[] { "VARIABLE_ID" }, new String[] { "VARIABLE_ID" }, 2));
        final Criteria subCategoryCrit = new Criteria(Column.getColumn("SubCategoryToVariableRel", "SUB_CATEGORY_ID"), (Object)subCategoryID, 0);
        selectQuery.setCriteria(subCategoryCrit.and(new Criteria(Column.getColumn("SubCategoryToVariableRel", "IS_DEFAULT"), (Object)true, 0)));
        selectQuery.addSelectColumn(Column.getColumn("EventVariables", "VARIABLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventVariables", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("EventVariables", "DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("SubCategoryToVariableRel", "SUB_CATEGORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("SubCategoryToVariableRel", "IS_DEFAULT"));
        selectQuery.addSelectColumn(Column.getColumn("SubCategoryToVariableRel", "VARIABLE_ID"));
        final DataObject variableDO = DataAccess.get(selectQuery);
        if (!variableDO.isEmpty()) {
            final Iterator variableItr = variableDO.getRows("EventVariables");
            while (variableItr.hasNext()) {
                final Row variableRow = variableItr.next();
                variableRow.set("DISPLAY_NAME", (Object)I18N.getMsgFromPropFile((String)variableRow.get("DISPLAY_NAME"), new Object[0]));
                variableDO.updateRow(variableRow);
            }
            String templateName = "Alert";
            String emailXsl = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "EMSAlerts" + File.separator + "xsl" + File.separator + "DefaultAlertEmail.xsl";
            final SelectQuery eventToXSLQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EMSEvents"));
            eventToXSLQuery.addJoin(new Join("EMSEvents", "EMSEventToXsl", new String[] { "EVENT_ID" }, new String[] { "EVENT_ID" }, 2));
            final Criteria eventIDCriteria = new Criteria(Column.getColumn("EMSEvents", "SUB_CATEGORY_ID"), (Object)subCategoryID, 0);
            eventToXSLQuery.setCriteria(eventIDCriteria);
            eventToXSLQuery.addSelectColumn(Column.getColumn("EMSEvents", "EVENT_ID"));
            eventToXSLQuery.addSelectColumn(Column.getColumn("EMSEvents", "NAME"));
            eventToXSLQuery.addSelectColumn(Column.getColumn("EMSEvents", "SUB_CATEGORY_ID"));
            eventToXSLQuery.addSelectColumn(Column.getColumn("EMSEventToXsl", "EVENT_ID"));
            eventToXSLQuery.addSelectColumn(Column.getColumn("EMSEventToXsl", "XSL_TEMPLATE_NAME"));
            eventToXSLQuery.addSelectColumn(Column.getColumn("EMSEventToXsl", "XSL_FILE_LOCATION"));
            final DataObject xslDO = DataAccess.get(eventToXSLQuery);
            if (!xslDO.isEmpty()) {
                final Row eventToXSLRow = xslDO.getRow("EMSEventToXsl");
                if (eventToXSLRow != null) {
                    templateName = (String)eventToXSLRow.get("XSL_TEMPLATE_NAME");
                    final String templateFileLocation = (String)eventToXSLRow.get("XSL_FILE_LOCATION");
                    final String templateXsl = SyMUtil.getInstallationDir() + templateFileLocation;
                    emailXsl = SyMUtil.createI18NxslFile(templateXsl, templateName + "-temp.xsl");
                }
            }
            final MailContentGeneratorUtil mg = new MailContentGeneratorUtil();
            description = mg.getHTMLContent(emailXsl, variableDO, templateName);
        }
        return description;
    }
    
    public ArrayList getAlertsEnabledEventList(final Long customerID, final Long userID) throws DataAccessException {
        final ArrayList<Long> eventCodeList = new ArrayList<Long>();
        final DataObject eventToTemplateDO = this.getEventDO(userID, customerID);
        final Iterator eventToTemplateItr = eventToTemplateDO.getRows("EventToTemplateRel");
        while (eventToTemplateItr.hasNext()) {
            final Row row = eventToTemplateItr.next();
            final Long eventID = (Long)row.get("EVENT_ID");
            if (row.get("ENABLED_STATUS")) {
                final DataObject eventDO = DataAccess.get("EMSEvents", new Criteria(Column.getColumn("EMSEvents", "EVENT_ID"), (Object)eventID, 0));
                final Long eventCode = (Long)eventDO.getFirstRow("EMSEvents").get("EVENT_CODE");
                if (eventCodeList.contains(eventCode)) {
                    continue;
                }
                eventCodeList.add(eventCode);
            }
        }
        return eventCodeList;
    }
    
    public void addOrUpdateEventTemplateRel(final Long eventID, final Long templateID, final Long mediumID, final Long userID, final Long customerID, final boolean enabledStatus) {
        try {
            final DataObject eventTemplateDO = this.getEventTemplateRelDO(eventID, templateID, mediumID, userID, customerID);
            if (eventTemplateDO.isEmpty()) {
                final Row eventTemplateRow = new Row("EventToTemplateRel");
                eventTemplateRow.set("EVENT_ID", (Object)eventID);
                eventTemplateRow.set("TEMPLATE_ID", (Object)templateID);
                eventTemplateRow.set("MEDIUM_ID", (Object)mediumID);
                eventTemplateRow.set("CREATED_BY", (Object)userID);
                eventTemplateRow.set("ENABLED_STATUS", (Object)enabledStatus);
                eventTemplateDO.addRow(eventTemplateRow);
                final DataObject resultDO = SyMUtil.getPersistenceLite().add(eventTemplateDO);
                final DataObject eventTemplateDO2 = DataAccess.get("EventTemplateCustomerRel", new Criteria(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"), resultDO.getFirstRow("EventToTemplateRel").get("EVENT_TEMPLATE_REL_ID"), 0));
                if (eventTemplateDO2.isEmpty()) {
                    final Row eventTemplateCusRow = new Row("EventTemplateCustomerRel");
                    eventTemplateCusRow.set("EVENT_TEMPLATE_REL_ID", resultDO.getFirstRow("EventToTemplateRel").get("EVENT_TEMPLATE_REL_ID"));
                    eventTemplateCusRow.set("CUSTOMER_ID", (Object)customerID);
                    eventTemplateDO2.addRow(eventTemplateCusRow);
                    DataAccess.add(eventTemplateDO2);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured while mapping event and templates ", e);
        }
    }
    
    public void deleteEventToTemplateRel(final Long mediumID, final Long templateID, final Long userID, final Long customerID) throws DataAccessException {
        final SelectQuery eventTemplateQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventToTemplateRel"));
        final Join eventTemplateCustomerJoin = new Join("EventToTemplateRel", "EventTemplateCustomerRel", new String[] { "EVENT_TEMPLATE_REL_ID" }, new String[] { "EVENT_TEMPLATE_REL_ID" }, 2);
        final Criteria templateIDCrit = new Criteria(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"), (Object)templateID, 0);
        final Criteria mediumIdCrit = templateIDCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)mediumID, 0));
        final Criteria userIDCrit = mediumIdCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "CREATED_BY"), (Object)userID, 0));
        final Criteria queryCrit = userIDCrit.and(new Criteria(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
        eventTemplateQuery.addJoin(eventTemplateCustomerJoin);
        eventTemplateQuery.setCriteria(queryCrit);
        eventTemplateQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"));
        eventTemplateQuery.addSelectColumn(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"));
        final DataObject eventTemplateDO = SyMUtil.getPersistence().get(eventTemplateQuery);
        if (!eventTemplateDO.isEmpty()) {
            final Iterator eventToTemplateRelItr = eventTemplateDO.getRows("EventToTemplateRel");
            while (eventToTemplateRelItr.hasNext()) {
                final Row eventToTemplateRow = eventToTemplateRelItr.next();
                final Long eventTemplateRelID = (Long)eventToTemplateRow.get("EVENT_TEMPLATE_REL_ID");
                this.deleteEmailDetailsToEvent(eventTemplateRelID);
                this.deleteSMSDetailsToEvent(eventTemplateRelID);
                this.deletePhoneNoDetailsToEvent(eventTemplateRelID);
                DataAccess.delete("EventTemplateCustomerRel", new Criteria(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
                DataAccess.delete(eventToTemplateRow);
            }
        }
    }
    
    public boolean isTemplateDataChangedByUser(final List mediumList) {
        boolean isDefaultTemplate = true;
        for (final Object mediumObj : mediumList) {
            final LinkedHashMap mediumHash = (LinkedHashMap)mediumObj;
            if (!mediumHash.get("defaultTemplate")) {
                isDefaultTemplate = false;
            }
        }
        return isDefaultTemplate;
    }
    
    public boolean getMediumStatus(final Long mediumId, final Long eventCode, final Long templateID, final Long customerID, final Long userID) throws DataAccessException, QueryConstructionException {
        final DataObject eventDO = DataAccess.get("EMSEvents", new Criteria(Column.getColumn("EMSEvents", "EVENT_CODE"), (Object)eventCode, 0));
        final Row eventRow = eventDO.getFirstRow("EMSEvents");
        final Long eventSubCategoryID = (Long)eventRow.get("SUB_CATEGORY_ID");
        final List eventForSubCategoryList = this.getAllEventIDsForSubCategory(eventSubCategoryID);
        final SelectQuery eventTemplateQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventToTemplateRel"));
        final Join eventTemplateCustomerJoin = new Join("EventToTemplateRel", "EventTemplateCustomerRel", new String[] { "EVENT_TEMPLATE_REL_ID" }, new String[] { "EVENT_TEMPLATE_REL_ID" }, 2);
        final Criteria eventIDCrit = new Criteria(Column.getColumn("EventToTemplateRel", "EVENT_ID"), (Object)eventForSubCategoryList.toArray(), 8);
        final Criteria templateIDCrit = eventIDCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"), (Object)templateID, 0));
        final Criteria mediumIdCrit = templateIDCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)mediumId, 0));
        final Criteria userIDCrit = mediumIdCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "CREATED_BY"), (Object)userID, 0));
        final Criteria queryCrit = userIDCrit.and(new Criteria(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
        eventTemplateQuery.addJoin(eventTemplateCustomerJoin);
        eventTemplateQuery.setCriteria(queryCrit);
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "TEMPLATE_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "EVENT_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "MEDIUM_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "CREATED_BY"));
        eventTemplateQuery.addSelectColumn(new Column("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventTemplateCustomerRel", "CUSTOMER_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "ENABLED_STATUS"));
        final DataObject eventTemplateDO = SyMUtil.getPersistence().get(eventTemplateQuery);
        return !eventTemplateDO.isEmpty();
    }
    
    public int getAlertsConfiguredForSMS() {
        int alertsCount = 0;
        try {
            final Criteria smsCriteria = new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)this.getMediumIdByName("SMS"), 0);
            final Criteria enabledStatusCrit = smsCriteria.and(new Criteria(Column.getColumn("EventToTemplateRel", "ENABLED_STATUS"), (Object)true, 0));
            alertsCount = DBUtil.getRecordCount("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID", enabledStatusCrit);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting alerts configured for SMS ", e);
        }
        return alertsCount;
    }
    
    public void updateAlertStatusMediumDetails(final Long eventID, final Long templateID, final Long mediumID, final Long userID, final Long customerID) throws DataAccessException {
        final Long emailMediumID = this.getMediumIdByName("EMAIL");
        final Long smsMediumID = this.getMediumIdByName("SMS");
        final Long otherMediumID = (Long.valueOf(mediumID) == Long.valueOf(emailMediumID)) ? smsMediumID : emailMediumID;
        final DataObject eventTemplateDO = this.getEventTemplateRelDO(eventID, templateID, otherMediumID, userID, customerID);
        if (!eventTemplateDO.isEmpty()) {
            final Row eventTemplateRow = eventTemplateDO.getFirstRow("EventToTemplateRel");
            final boolean enabledStatus = (boolean)eventTemplateRow.get("ENABLED_STATUS");
            final Long eventToTemplateID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
            if (enabledStatus) {
                final Long newEventTemplateRelID = this.updateEventTemplateRel(eventID, templateID, mediumID, userID, customerID, enabledStatus);
                if (newEventTemplateRelID != -1L) {
                    if (Long.valueOf(mediumID) == Long.valueOf(emailMediumID)) {
                        final String emailAddressToSave = this.getEmailAddressSavedByUser(customerID, userID);
                        if (!emailAddressToSave.isEmpty()) {
                            this.addOrUpdateEmailAddressToEvent(newEventTemplateRelID, emailAddressToSave);
                        }
                    }
                    else {
                        final Long[] smsUserIDs = this.getSMSUserIDSavedByUser(customerID, userID);
                        for (int iValue = 0; iValue < smsUserIDs.length; ++iValue) {
                            final Long smsUserID = smsUserIDs[iValue];
                            this.addOrUpdateSMSDetailsToEvent(newEventTemplateRelID, smsUserID);
                        }
                    }
                }
            }
        }
    }
    
    private Long updateEventTemplateRel(final Long eventID, final Long templateID, final Long mediumID, final Long userID, final Long customerID, final boolean enabledStatus) throws DataAccessException {
        Long eventToTemplateID = -1L;
        final DataObject eventTemplateDO = this.getEventTemplateRelDO(eventID, templateID, mediumID, userID, customerID);
        if (!eventTemplateDO.isEmpty()) {
            final Row eventTemplateRow = eventTemplateDO.getFirstRow("EventToTemplateRel");
            final boolean enabledStatusInDB = (boolean)eventTemplateRow.get("ENABLED_STATUS");
            if (enabledStatusInDB != enabledStatus) {
                eventTemplateRow.set("ENABLED_STATUS", (Object)enabledStatus);
                eventTemplateDO.updateRow(eventTemplateRow);
                DataAccess.update(eventTemplateDO);
                eventToTemplateID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
            }
        }
        else {
            eventToTemplateID = (Long)eventTemplateDO.getFirstRow("EventToTemplateRel").get("EVENT_TEMPLATE_REL_ID");
        }
        return eventToTemplateID;
    }
    
    public DataObject getEventTemplateRelDO(final Long eventID, final Long templateID, final Long mediumID, final Long userID, final Long customerID) throws DataAccessException {
        final SelectQuery eventTemplateQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventToTemplateRel"));
        final Join eventTemplateCustomerJoin = new Join("EventToTemplateRel", "EventTemplateCustomerRel", new String[] { "EVENT_TEMPLATE_REL_ID" }, new String[] { "EVENT_TEMPLATE_REL_ID" }, 2);
        final Criteria eventIDCrit = new Criteria(Column.getColumn("EventToTemplateRel", "EVENT_ID"), (Object)eventID, 0);
        final Criteria templateIDCrit = eventIDCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"), (Object)templateID, 0));
        final Criteria mediumIdCrit = templateIDCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)mediumID, 0));
        final Criteria userIDCrit = mediumIdCrit.and(new Criteria(Column.getColumn("EventToTemplateRel", "CREATED_BY"), (Object)userID, 0));
        final Criteria queryCrit = userIDCrit.and(new Criteria(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
        eventTemplateQuery.addJoin(eventTemplateCustomerJoin);
        eventTemplateQuery.setCriteria(queryCrit);
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "TEMPLATE_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "EVENT_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "MEDIUM_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "CREATED_BY"));
        eventTemplateQuery.addSelectColumn(new Column("EventToTemplateRel", "ENABLED_STATUS"));
        eventTemplateQuery.addSelectColumn(new Column("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"));
        eventTemplateQuery.addSelectColumn(new Column("EventTemplateCustomerRel", "CUSTOMER_ID"));
        final DataObject eventTemplateDO = SyMUtil.getPersistence().get(eventTemplateQuery);
        return eventTemplateDO;
    }
    
    public String getEmailAddressSavedByUser(final Long customerID, final Long userID) throws DataAccessException {
        final Criteria mediumIDCrit = new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)this.getMediumIdByName("EMAIL"), 0);
        final Criteria enabledStatusCrit = new Criteria(Column.getColumn("EventToTemplateRel", "ENABLED_STATUS"), (Object)true, 0);
        final Criteria totalCriteria = mediumIDCrit.and(enabledStatusCrit);
        final DataObject eventDO = this.getEventDO(userID, customerID);
        if (!eventDO.isEmpty()) {
            final Iterator eventTemplateItr = eventDO.getRows("EventToTemplateRel", totalCriteria);
            final Row eventTemplateRow = eventTemplateItr.next();
            final Long eventTemplateRelID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
            final DataObject eventEmailDO = DataAccess.get("EventEmailDetails", new Criteria(Column.getColumn("EventEmailDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
            if (!eventEmailDO.isEmpty()) {
                return (String)eventEmailDO.getFirstRow("EventEmailDetails").get("EMAIL_ADDRESS");
            }
        }
        return "";
    }
    
    public Long[] getSMSUserIDSavedByUser(final Long customerID, final Long userID) throws DataAccessException {
        final Criteria mediumIDCrit = new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)this.getMediumIdByName("SMS"), 0);
        final Criteria enabledStatusCrit = new Criteria(Column.getColumn("EventToTemplateRel", "ENABLED_STATUS"), (Object)true, 0);
        final Criteria totalCriteria = mediumIDCrit.and(enabledStatusCrit);
        final DataObject eventDO = this.getEventDO(userID, customerID);
        if (!eventDO.isEmpty()) {
            final Iterator eventTemplateItr = eventDO.getRows("EventToTemplateRel", totalCriteria);
            final Row eventTemplateRow = eventTemplateItr.next();
            final Long eventTemplateRelID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
            final DataObject eventSMSDO = DataAccess.get("EventSMSDetails", new Criteria(Column.getColumn("EventSMSDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
            if (!eventSMSDO.isEmpty()) {
                int notificationUserCount = 0;
                final Long[] notificationUser = new Long[eventSMSDO.size("EventSMSDetails")];
                final Iterator eventSMSItr = eventSMSDO.getRows("EventSMSDetails");
                while (eventSMSItr.hasNext()) {
                    final Row eventSMSRow = eventSMSItr.next();
                    notificationUser[notificationUserCount++] = (Long)eventSMSRow.get("USER_ID");
                }
                return notificationUser;
            }
        }
        return new Long[0];
    }
    
    public Object getPhoneNoForEvent(final Long customerID, final Long userID, final List eventIDList) {
        try {
            final DataObject mediumDO = DataAccess.get("EventMediums", new Criteria(Column.getColumn("EventMediums", "MEDIUM_NAME"), (Object)"SMS", 0));
            final Long mediumID = (Long)(mediumDO.isEmpty() ? -1L : mediumDO.getFirstRow("EventMediums").get("MEDIUM_ID"));
            final DataObject eventTemplateDO = new TemplatesDAOUtil().getAlertsForEventID(eventIDList, customerID, userID, mediumID);
            if (!eventTemplateDO.isEmpty()) {
                final Row eventTemplateRow = eventTemplateDO.getFirstRow("EventToTemplateRel");
                final Long eventToTemplateRelID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
                final DataObject eventPhoneNoDetailsDO = DataAccess.get("EventPhoneNoDetails", new Criteria(Column.getColumn("EventPhoneNoDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventToTemplateRelID, 0));
                if (!eventPhoneNoDetailsDO.isEmpty()) {
                    return eventPhoneNoDetailsDO.getFirstRow("EventPhoneNoDetails").get("PHONE_NO");
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured while getting Phone no details for an event ", e);
        }
        return "";
    }
    
    public void addOrUpdatePhoneNoToEvent(final Long eventTemplatRelID, final String notificationPhoneNo) throws DataAccessException {
        final DataObject eventPhoneNoDO = DataAccess.get("EventPhoneNoDetails", new Criteria(Column.getColumn("EventPhoneNoDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplatRelID, 0));
        if (eventPhoneNoDO.isEmpty()) {
            final Row eventPhoneNoRow = new Row("EventPhoneNoDetails");
            eventPhoneNoRow.set("EVENT_TEMPLATE_REL_ID", (Object)eventTemplatRelID);
            eventPhoneNoRow.set("PHONE_NO", (Object)notificationPhoneNo);
            eventPhoneNoDO.addRow(eventPhoneNoRow);
            DataAccess.add(eventPhoneNoDO);
        }
        else {
            final Row eventEmailRow = eventPhoneNoDO.getFirstRow("EventPhoneNoDetails");
            eventEmailRow.set("PHONE_NO", (Object)notificationPhoneNo);
            eventPhoneNoDO.updateRow(eventEmailRow);
            DataAccess.update(eventPhoneNoDO);
        }
    }
}
