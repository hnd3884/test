package com.me.emsalerts.notifications.api.v1.service;

import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataObject;
import javax.ws.rs.WebApplicationException;
import com.me.ems.framework.common.api.response.APIResponse;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import java.util.Hashtable;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.me.emsalerts.notifications.core.MediumDAOUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import com.me.emsalerts.notifications.core.TemplatesDAOUtil;

public class TemplatesService
{
    TemplatesDAOUtil templatesDAOUtil;
    TemplatesUtil templatesUtil;
    private Logger alertsLogger;
    
    public TemplatesService() {
        this.templatesDAOUtil = new TemplatesDAOUtil();
        this.templatesUtil = new TemplatesUtil();
        this.alertsLogger = Logger.getLogger("EMSAlertsLogger");
    }
    
    public HashMap getTemplateDetails(final Long customerID, final Long userID, final Long eventCode) {
        final HashMap templateDetails = new HashMap();
        boolean defaultTemplate = false;
        try {
            final DataObject subCategoryDO = DataAccess.get("EMSEvents", new Criteria(Column.getColumn("EMSEvents", "EVENT_CODE"), (Object)eventCode, 0));
            final Long eventID = (Long)subCategoryDO.getFirstRow("EMSEvents").get("EVENT_ID");
            final Long subCategoryID = this.templatesUtil.getSubCategoryIDForEventCode(eventCode);
            templateDetails.put("variableInfo", this.templatesUtil.getEventKeyList(subCategoryID));
            Long templateID = this.templatesUtil.getTemplateIDForUser(subCategoryID, userID, customerID);
            if (templateID == -1L) {
                templateID = this.templatesUtil.getDefaultTemplateForSubCategory(subCategoryID);
                defaultTemplate = true;
            }
            final DataObject mediumDetailsDO = new MediumDAOUtil().getMediumDetailsForTemplates(templateID);
            final List mediumArray = new ArrayList();
            if (!mediumDetailsDO.isEmpty()) {
                final Iterator mediumItr = mediumDetailsDO.getRows("TemplateToMediumRel");
                while (mediumItr.hasNext()) {
                    final Row mediumRow = mediumItr.next();
                    final Hashtable mediumObj = new Hashtable();
                    final Long mediumID = (Long)mediumRow.get("MEDIUM_ID");
                    final boolean mediumStatus = this.templatesUtil.getMediumStatus(mediumID, eventCode, templateID, customerID, userID);
                    final String mediumData = String.valueOf(mediumRow.get("MEDIUM_DATA"));
                    mediumObj.put("mediumID", mediumID);
                    final DataObject mediumDO = DataAccess.get("EventMediums", new Criteria(Column.getColumn("EventMediums", "MEDIUM_ID"), (Object)mediumID, 0));
                    final String mediumName = String.valueOf(mediumDO.getFirstRow("EventMediums").get("MEDIUM_NAME"));
                    mediumObj.put("mediumName", mediumName);
                    if (defaultTemplate && mediumName.equalsIgnoreCase("EMAIL")) {
                        final JSONObject mediumJSON = new JSONObject(mediumData);
                        String mediumDescription = mediumJSON.getString("description");
                        final String defaultKeyInformation = this.templatesUtil.getDefaultKeyInformation(subCategoryID);
                        if (defaultKeyInformation != null) {
                            mediumDescription += defaultKeyInformation;
                        }
                        mediumJSON.put("description", (Object)mediumDescription);
                        mediumObj.put("mediumData", mediumJSON.toString());
                    }
                    else {
                        mediumObj.put("mediumData", mediumData);
                    }
                    mediumObj.put("mediumStatus", mediumStatus);
                    mediumObj.put("defaultTemplate", defaultTemplate ? defaultTemplate : this.templatesUtil.checkDefaultTemplateData(subCategoryID, mediumID, mediumData));
                    mediumArray.add(mediumObj);
                }
            }
            templateDetails.put("mediumInfo", mediumArray);
            templateDetails.put("name", I18N.getMsg((String)subCategoryDO.getFirstRow("EMSEvents").get("NAME"), new Object[0]));
        }
        catch (final Exception e) {
            this.alertsLogger.log(Level.INFO, "Exception occured while getting Template details :", e);
            throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.INTERNAL_SERVER_ERROR, "10001", "Internal Server Error")));
        }
        return templateDetails;
    }
    
    public Response saveTemplateDetails(final Map templateDetails, final Long eventCode, final Long userID, final Long customerID) {
        try {
            final Long subCategoryID = this.templatesUtil.getSubCategoryIDForEventCode(eventCode);
            Long templateID = -1L;
            final boolean useDefaultTemplate = this.templatesUtil.isTemplateDataChangedByUser(templateDetails.get("mediumInfo"));
            Long anotherTemplateID = -1L;
            if (useDefaultTemplate) {
                templateID = this.templatesUtil.getDefaultTemplateForSubCategory(subCategoryID);
                anotherTemplateID = this.templatesUtil.getTemplateIDForUser(subCategoryID, userID, customerID);
            }
            else {
                templateID = this.templatesUtil.getTemplateIDForUser(subCategoryID, userID, customerID);
                anotherTemplateID = this.templatesUtil.getDefaultTemplateForSubCategory(subCategoryID);
            }
            if (anotherTemplateID != -1L) {
                final DataObject eventToTemplateDO = DataAccess.get("EventToTemplateRel", new Criteria(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"), (Object)anotherTemplateID, 0));
                if (!eventToTemplateDO.isEmpty()) {
                    final Iterator eventToTemplateRelItr = eventToTemplateDO.getRows("EventToTemplateRel");
                    while (eventToTemplateRelItr.hasNext()) {
                        final Row eventToTemplateRow = eventToTemplateRelItr.next();
                        final Long eventTemplateRelID = (Long)eventToTemplateRow.get("EVENT_TEMPLATE_REL_ID");
                        this.templatesUtil.deleteEmailDetailsToEvent(eventTemplateRelID);
                        this.templatesUtil.deleteSMSDetailsToEvent(eventTemplateRelID);
                        this.templatesUtil.deletePhoneNoDetailsToEvent(eventTemplateRelID);
                        DataAccess.delete("EventTemplateCustomerRel", new Criteria(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
                        DataAccess.delete(eventToTemplateRow);
                    }
                }
                final Criteria templateIDCrit = new Criteria(Column.getColumn("NotificationTemplate", "TEMPLATE_ID"), (Object)anotherTemplateID, 0);
                final Criteria systemUserCrit = templateIDCrit.and(new Criteria(Column.getColumn("NotificationTemplate", "CREATED_BY"), (Object)userID, 0));
                DataAccess.delete("NotificationTemplate", systemUserCrit);
            }
            final Map templateMap = new HashMap();
            templateMap.put("userID", userID);
            templateMap.put("subCategoryID", subCategoryID);
            if (templateID == -1L) {
                templateMap.put("templateName", "Template " + System.currentTimeMillis());
                templateMap.put("description", "Template for event " + eventCode);
                templateID = this.templatesDAOUtil.addOrUpdateTemplateDetails(templateMap, null);
                if (CustomerInfoUtil.getInstance().isMSP()) {
                    this.templatesDAOUtil.addTemplateCustomerRel(templateID, customerID);
                }
            }
            else if (!useDefaultTemplate) {
                templateID = this.templatesDAOUtil.addOrUpdateTemplateDetails(templateMap, templateID);
            }
            final List mediumList = templateDetails.get("mediumInfo");
            for (final Object mediumObj : mediumList) {
                final LinkedHashMap mediumHash = (LinkedHashMap)mediumObj;
                final boolean enabledStatus = mediumHash.get("mediumStatus");
                final Long mediumID = Long.valueOf(mediumHash.get("mediumID"));
                if (!useDefaultTemplate) {
                    new MediumDAOUtil().populateMediumData(templateID, mediumHash);
                }
                final DataObject eventDO = DataAccess.get("EMSEvents", new Criteria(Column.getColumn("EMSEvents", "EVENT_CODE"), (Object)eventCode, 0));
                final Row eventRow = eventDO.getFirstRow("EMSEvents");
                final Long eventSubCategoryID = (Long)eventRow.get("SUB_CATEGORY_ID");
                if (enabledStatus) {
                    final List eventIDForSubCategoryList = this.templatesUtil.getAllEventIDsForSubCategory(eventSubCategoryID);
                    for (int itr = 0; itr < eventIDForSubCategoryList.size(); ++itr) {
                        this.templatesUtil.addOrUpdateEventTemplateRel(eventIDForSubCategoryList.get(itr), templateID, mediumID, userID, customerID, false);
                        this.templatesUtil.updateAlertStatusMediumDetails(eventIDForSubCategoryList.get(itr), templateID, mediumID, userID, customerID);
                    }
                }
                else {
                    this.templatesUtil.deleteEventToTemplateRel(mediumID, templateID, userID, customerID);
                }
            }
        }
        catch (final Exception e) {
            this.alertsLogger.log(Level.WARNING, "Exception occured while saving the template details ", e);
            throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.INTERNAL_SERVER_ERROR, "10001", "Internal Server Error")));
        }
        return Response.ok().build();
    }
    
    public HashMap getDefaultMediumData(final Long eventCode, final Long mediumID) {
        final HashMap defaultMediumDataMap = new HashMap();
        try {
            final Long subCategoryID = this.templatesUtil.getSubCategoryIDForEventCode(eventCode);
            final Long templateID = this.templatesUtil.getDefaultTemplateForSubCategory(subCategoryID);
            final DataObject mediumDO = new MediumDAOUtil().mediumDataDO(templateID, mediumID);
            if (!mediumDO.isEmpty()) {
                final Row mediumRow = mediumDO.getFirstRow("TemplateToMediumRel");
                final String mediumData = (String)mediumRow.get("MEDIUM_DATA");
                final DataObject mediumDetails = DataAccess.get("EventMediums", new Criteria(Column.getColumn("EventMediums", "MEDIUM_ID"), (Object)mediumID, 0));
                final String mediumName = String.valueOf(mediumDetails.getFirstRow("EventMediums").get("MEDIUM_NAME"));
                if (mediumName.equalsIgnoreCase("EMAIL")) {
                    final JSONObject mediumJSON = new JSONObject(mediumData);
                    String description = mediumJSON.getString("description");
                    final String defaultKeyInformation = this.templatesUtil.getDefaultKeyInformation(subCategoryID);
                    if (defaultKeyInformation != null) {
                        description += defaultKeyInformation;
                    }
                    mediumJSON.put("description", (Object)description);
                    defaultMediumDataMap.put("mediumData", mediumJSON.toString());
                }
                else {
                    defaultMediumDataMap.put("mediumData", mediumData);
                }
            }
        }
        catch (final Exception e) {
            this.alertsLogger.log(Level.WARNING, "Exception occured while getting default medium data ", e);
            throw new WebApplicationException(APIResponse.errorResponse(new APIException(Response.Status.INTERNAL_SERVER_ERROR, "10001", "Internal Server Error")));
        }
        return defaultMediumDataMap;
    }
}
