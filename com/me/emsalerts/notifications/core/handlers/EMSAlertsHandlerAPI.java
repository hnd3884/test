package com.me.emsalerts.notifications.core.handlers;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.emsalerts.notifications.core.MediumDAOUtil;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import org.json.JSONObject;
import com.me.emsalerts.notifications.core.AlertDetails;

public interface EMSAlertsHandlerAPI
{
    default Object constructAlertData(final AlertDetails alertDetails) throws Exception {
        JSONObject mediumData = new JSONObject();
        final Long customerID = alertDetails.customerID;
        final Long technicianID = alertDetails.technicianID;
        final Long eventCode = alertDetails.eventCode;
        final Long mediumID = alertDetails.mediumID;
        final TemplatesUtil templatesUtil = new TemplatesUtil();
        boolean isDefaultTemplate = false;
        final Long subCategoryID = templatesUtil.getSubCategoryIDForEventCode(eventCode);
        Long templateID = templatesUtil.getTemplateIDForUser(subCategoryID, technicianID, customerID);
        if (templateID == -1L) {
            isDefaultTemplate = true;
            templateID = templatesUtil.getDefaultTemplateForSubCategory(subCategoryID);
        }
        final DataObject mediumDO = new MediumDAOUtil().getMediumDetailsForTemplates(templateID);
        if (!mediumDO.isEmpty()) {
            final Row mediumRow = mediumDO.getRow("TemplateToMediumRel", new Criteria(Column.getColumn("TemplateToMediumRel", "MEDIUM_ID"), (Object)mediumID, 0));
            mediumData = new JSONObject((String)mediumRow.get("MEDIUM_DATA"));
        }
        final String mailMediumID = String.valueOf(new TemplatesUtil().getMediumIdByName("EMAIL"));
        if (isDefaultTemplate && String.valueOf(mediumID).equalsIgnoreCase(mailMediumID)) {
            String description = mediumData.getString("description");
            description += templatesUtil.getDefaultKeyInformation(subCategoryID);
            mediumData.put("description", (Object)description);
        }
        return mediumData;
    }
    
    Object sendAlert(final Object p0, final HashMap p1);
    
    Object getRecipients(final Long p0, final Long p1, final Long p2, final Long p3);
    
    default boolean isAlertConfigured(final AlertDetails alertDetails) throws Exception {
        final Long customerID = alertDetails.customerID;
        final Long technicianID = alertDetails.technicianID;
        final Long eventCode = alertDetails.eventCode;
        final Long mediumID = alertDetails.mediumID;
        final TemplatesUtil templatesUtil = new TemplatesUtil();
        final Long eventID = templatesUtil.getEventIDForCode(eventCode);
        final Long subCategoryID = templatesUtil.getSubCategoryIDForEventCode(eventCode);
        Long templateID = templatesUtil.getTemplateIDForUser(subCategoryID, technicianID, customerID);
        if (templateID == -1L) {
            templateID = templatesUtil.getDefaultTemplateForSubCategory(subCategoryID);
        }
        return templatesUtil.isTemplateConfiguredForMedium(templateID, mediumID, Arrays.asList(eventID), customerID, technicianID);
    }
    
    boolean isMediumSettingsConfigured(final AlertDetails p0);
}
