package com.me.emsalerts.common.listener;

import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerEvent;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class AlertDeletionDataProcessor extends DCQueueDataProcessor
{
    public void processData(final DCQueueData qData) {
        final JSONObject data = (JSONObject)qData.queueData;
        this.logger.log(Level.INFO, "Going to process data : {0} Q Data type " + qData.queueDataType, data);
        if (qData.queueDataType == 101) {
            this.deleteAlertsMappedToUser(data);
            this.deleteTemplatesMappedToUser(data);
        }
        else if (qData.queueDataType == 102) {
            try {
                final CustomerEvent customerEvent = (CustomerEvent)data.get("customerEvent");
                this.deleteEventTemplateMappedToCustomer(customerEvent);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception occured while paring CustomerEvent Data ", e);
            }
        }
    }
    
    private void deleteTemplatesMappedToUser(final JSONObject userEvent) {
        final Long technicianID = userEvent.getLong("userID");
        try {
            final DataObject templateDO = DataAccess.get("NotificationTemplate", new Criteria(Column.getColumn("NotificationTemplate", "CREATED_BY"), (Object)technicianID, 0));
            if (!templateDO.isEmpty()) {
                final Iterator templateItr = templateDO.getRows("NotificationTemplate");
                while (templateItr.hasNext()) {
                    final Row templateRow = templateItr.next();
                    final Long templateID = (Long)templateRow.get("TEMPLATE_ID");
                    this.deleteTemplateToMediumRel(templateID);
                    this.deleteTemplateToCustomerRel(templateID);
                    DataAccess.delete(templateRow);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.INFO, "AlertDeletionDataprocessor: Exception while deleting Templates mapped with User ", (Throwable)e);
        }
    }
    
    private void deleteTemplateToCustomerRel(final Long templateID) throws DataAccessException {
        DataAccess.delete("TemplateToCustomerRel", new Criteria(Column.getColumn("TemplateToCustomerRel", "TEMPLATE_ID"), (Object)templateID, 0));
    }
    
    private void deleteTemplateToMediumRel(final Long templateID) throws DataAccessException {
        DataAccess.delete("TemplateToMediumRel", new Criteria(Column.getColumn("TemplateToMediumRel", "TEMPLATE_ID"), (Object)templateID, 0));
    }
    
    private void deleteAlertsMappedToUser(final JSONObject userEvent) {
        final Long technicianID = userEvent.getLong("userID");
        try {
            final DataObject eventTemplateRelDO = DataAccess.get("EventToTemplateRel", new Criteria(Column.getColumn("EventToTemplateRel", "CREATED_BY"), (Object)technicianID, 0));
            if (!eventTemplateRelDO.isEmpty()) {
                final Iterator eventTemplateItr = eventTemplateRelDO.getRows("EventToTemplateRel");
                while (eventTemplateItr.hasNext()) {
                    final Row eventTemplateRow = eventTemplateItr.next();
                    final Long eventToTemplateRelID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
                    this.deleteEventTemplateCustomerRel(eventToTemplateRelID);
                    this.deleteEventEmailRel(eventToTemplateRelID);
                    this.deleteEventSMSRel(eventToTemplateRelID);
                    this.deleteUserSMSRel(technicianID);
                    DataAccess.delete(eventTemplateRow);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.INFO, "AlertDeletionDataprocessor: Exception occured while deleting the alerts mapped to user ", (Throwable)e);
        }
    }
    
    private void deleteUserSMSRel(final Long technicianID) throws DataAccessException {
        DataAccess.delete("EventSMSDetails", new Criteria(Column.getColumn("EventSMSDetails", "USER_ID"), (Object)technicianID, 0));
    }
    
    private void deleteEventSMSRel(final Long eventToTemplateRelID) throws DataAccessException {
        DataAccess.delete("EventSMSDetails", new Criteria(Column.getColumn("EventSMSDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventToTemplateRelID, 0));
    }
    
    private void deleteEventEmailRel(final Long eventToTemplateRelID) throws DataAccessException {
        DataAccess.delete("EventEmailDetails", new Criteria(Column.getColumn("EventEmailDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventToTemplateRelID, 0));
    }
    
    private void deleteEventTemplateCustomerRel(final Long eventToTemplateRelID) throws DataAccessException {
        DataAccess.delete("EventTemplateCustomerRel", new Criteria(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"), (Object)eventToTemplateRelID, 0));
    }
    
    public void deleteEventTemplateMappedToCustomer(final CustomerEvent customerEvent) {
        try {
            final Long customerID = customerEvent.customerID;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventToTemplateRel"));
            final Join eventTemplateRelJoin = new Join("EventToTemplateRel", "EventTemplateCustomerRel", new String[] { "EVENT_TEMPLATE_REL_ID" }, new String[] { "EVENT_TEMPLATE_REL_ID" }, 2);
            final Criteria customerCrit = new Criteria(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.addJoin(eventTemplateRelJoin);
            selectQuery.setCriteria(customerCrit);
            selectQuery.addSelectColumn(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"));
            final DataObject eventRelDO = SyMUtil.getPersistence().get(selectQuery);
            final Iterator eventTemplateItr = eventRelDO.getRows("EventToTemplateRel");
            while (eventTemplateItr.hasNext()) {
                final Row eventTemplateRelRow = eventTemplateItr.next();
                final Long eventTemplateRelID = (Long)eventTemplateRelRow.get("EVENT_TEMPLATE_REL_ID");
                final Long templateID = (Long)eventTemplateRelRow.get("TEMPLATE_ID");
                DataAccess.delete("EventEmailDetails", new Criteria(Column.getColumn("EventEmailDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
                DataAccess.delete("EventSMSDetails", new Criteria(Column.getColumn("EventSMSDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
                DataAccess.delete("EventTemplateCustomerRel", new Criteria(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
                DataAccess.delete("EventToTemplateRel", new Criteria(Column.getColumn("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"), (Object)eventTemplateRelID, 0));
                final Long systemUserID = DMUserHandler.getUserID("DC-SYSTEM-USER");
                final Criteria templateIDCrit = new Criteria(Column.getColumn("NotificationTemplate", "TEMPLATE_ID"), (Object)templateID, 0);
                final Criteria systemUserCrit = templateIDCrit.and(new Criteria(Column.getColumn("NotificationTemplate", "CREATED_BY"), (Object)systemUserID, 1));
                DataAccess.delete("NotificationTemplate", systemUserCrit);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception deleting customer related alerts details ", e);
        }
    }
}
