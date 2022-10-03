package com.me.emsalerts.notifications.core;

import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;

public class TemplatesDAOUtil
{
    public DataObject getTemplateDO(final Long templateId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("NotificationTemplate"));
        final Join subCategoryJoin = new Join("NotificationTemplate", "EventSubCategory", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2);
        final Join categoryJoin = new Join("EventSubCategory", "EventCategory", new String[] { "CATEGORY_ID" }, new String[] { "CATEGORY_ID" }, 2);
        final Criteria templateCriteria = new Criteria(Column.getColumn("NotificationTemplate", "TEMPLATE_ID"), (Object)templateId, 0);
        selectQuery.addJoin(subCategoryJoin);
        selectQuery.addJoin(categoryJoin);
        selectQuery.addSelectColumn(Column.getColumn("NotificationTemplate", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("NotificationTemplate", "TEMPLATE_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("NotificationTemplate", "DESCRIPTION"));
        selectQuery.addSelectColumn(Column.getColumn("NotificationTemplate", "SUB_CATEGORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventSubCategory", "SUB_CATEGORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventSubCategory", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("EventSubCategory", "CATEGORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventCategory", "CATEGORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventCategory", "NAME"));
        selectQuery.setCriteria(templateCriteria);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public Long addOrUpdateTemplateDetails(final Map addTemplateInputMap, Long templateID) throws DataAccessException {
        final String notificationTemplateTable = "NotificationTemplate";
        Row templateRow = null;
        DataObject templateObj = (DataObject)new WritableDataObject();
        if (templateID == null) {
            templateObj = (DataObject)new WritableDataObject();
            templateRow = new Row(notificationTemplateTable);
            templateRow = this.constructTemplateRow(templateRow, addTemplateInputMap);
            templateObj.addRow(templateRow);
            templateObj = SyMUtil.getPersistence().add(templateObj);
        }
        else {
            final Criteria templateCrit = new Criteria(Column.getColumn(notificationTemplateTable, "TEMPLATE_ID"), (Object)templateID, 0);
            templateObj = DataAccess.get(notificationTemplateTable, templateCrit);
            if (!templateObj.isEmpty()) {
                templateRow = templateObj.getFirstRow(notificationTemplateTable);
                templateRow = this.constructTemplateRow(templateRow, addTemplateInputMap);
                templateObj.updateRow(templateRow);
                templateObj = SyMUtil.getPersistence().update(templateObj);
            }
        }
        if (!templateObj.isEmpty()) {
            final Row row = templateObj.getFirstRow(notificationTemplateTable);
            templateID = (Long)row.get("TEMPLATE_ID");
        }
        return templateID;
    }
    
    public Row constructTemplateRow(final Row templateRow, final Map templateMap) {
        final String templateName = templateMap.get("templateName");
        if (templateRow != null) {
            templateRow.set("ACTIVE_STATUS", (Object)1);
            if (CustomerInfoUtil.getInstance().isMSP()) {
                templateRow.set("IS_FOR_ALL_CUSTOMERS", (Object)Boolean.FALSE);
            }
            else {
                templateRow.set("IS_FOR_ALL_CUSTOMERS", (Object)Boolean.TRUE);
            }
            templateRow.set("CREATED_BY", templateMap.get("userID"));
            templateRow.set("CREATED_AT", (Object)System.currentTimeMillis());
            templateRow.set("MODIFIED_BY", templateMap.get("userID"));
            templateRow.set("LAST_MODIFIED_AT", (Object)System.currentTimeMillis());
        }
        else {
            templateRow.set("LAST_MODIFIED_AT", (Object)System.currentTimeMillis());
            templateRow.set("MODIFIED_BY", templateMap.get("userID"));
        }
        if (templateName != null) {
            templateRow.set("TEMPLATE_NAME", (Object)templateName);
        }
        final String templateDesc = templateMap.get("description");
        if (templateDesc != null) {
            templateRow.set("DESCRIPTION", (Object)templateDesc);
        }
        if (templateMap.get("subCategoryID") != null) {
            templateRow.set("SUB_CATEGORY_ID", templateMap.get("subCategoryID"));
        }
        return templateRow;
    }
    
    public void addTemplateCustomerRel(final Long templateID, final Long customerID) throws DataAccessException {
        final Row templateCustomerRow = new Row("TemplateToCustomerRel");
        templateCustomerRow.set("TEMPLATE_ID", (Object)templateID);
        templateCustomerRow.set("CUSTOMER_ID", (Object)customerID);
        final DataObject templateCustomerDO = (DataObject)new WritableDataObject();
        templateCustomerDO.addRow(templateCustomerRow);
        DataAccess.add(templateCustomerDO);
    }
    
    public DataObject getAlertsForEventID(final List eventIDList, final Long customerID, final Long userID, final Long mediumID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventToTemplateRel"));
        final Join customerJoin = new Join("EventToTemplateRel", "EventTemplateCustomerRel", new String[] { "EVENT_TEMPLATE_REL_ID" }, new String[] { "EVENT_TEMPLATE_REL_ID" }, 2);
        final Criteria userCriteria = new Criteria(Column.getColumn("EventToTemplateRel", "CREATED_BY"), (Object)userID, 0);
        final Criteria eventIDCriteria = userCriteria.and(new Criteria(Column.getColumn("EventToTemplateRel", "EVENT_ID"), (Object)eventIDList.toArray(), 8));
        final Criteria customerIDCriteria = eventIDCriteria.and(new Criteria(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
        final Criteria mediumCriteria = customerIDCriteria.and(new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)mediumID, 0));
        selectQuery.addJoin(customerJoin);
        selectQuery.setCriteria(mediumCriteria);
        selectQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "EVENT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "CREATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"));
        final DataObject alertsDO = SyMUtil.getPersistence().get(selectQuery);
        return alertsDO;
    }
    
    public DataObject getEventKeysDO(final Long subCategoryID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventVariables"));
        final Join subCategoryJoin = new Join("EventVariables", "SubCategoryToVariableRel", new String[] { "VARIABLE_ID" }, new String[] { "VARIABLE_ID" }, 2);
        final Criteria subCategoryCrit = new Criteria(Column.getColumn("SubCategoryToVariableRel", "SUB_CATEGORY_ID"), (Object)subCategoryID, 0);
        selectQuery.addJoin(subCategoryJoin);
        selectQuery.setCriteria(subCategoryCrit);
        selectQuery.addSelectColumn(Column.getColumn("EventVariables", "VARIABLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventVariables", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("EventVariables", "DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("EventVariables", "DEFAULT_VALUE"));
        selectQuery.addSelectColumn(Column.getColumn("SubCategoryToVariableRel", "VARIABLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("SubCategoryToVariableRel", "SUB_CATEGORY_ID"));
        final DataObject variableDO = SyMUtil.getPersistence().get(selectQuery);
        return variableDO;
    }
}
