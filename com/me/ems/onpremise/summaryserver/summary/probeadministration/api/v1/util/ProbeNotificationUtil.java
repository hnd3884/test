package com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.util;

import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataAccess;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class ProbeNotificationUtil
{
    public static Logger logger;
    
    public static boolean isEventEnabledForProbe(final Long probeId, final Long eventId) {
        try {
            Criteria criteria = new Criteria(new Column("ProbeEventMapping", "PROBE_ID"), (Object)probeId, 0);
            final Criteria criteria2 = new Criteria(new Column("ProbeEventMapping", "EVENT_ID"), (Object)eventId, 0);
            criteria = criteria.and(criteria2);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeEventMapping"));
            sq.addSelectColumn(new Column("ProbeEventMapping", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                return (boolean)dataObject.getRow("ProbeEventMapping").get("IS_ENABLED");
            }
        }
        catch (final DataAccessException e) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "Exception occured while getting event enabled status for event Id " + eventId + " for probe " + probeId + " ", (Throwable)e);
        }
        return false;
    }
    
    public static void setEventEnabledStatus(final Long eventId, final Long probeId, final boolean isEnabled) {
        try {
            Criteria criteria = new Criteria(new Column("ProbeEventMapping", "PROBE_ID"), (Object)probeId, 0);
            final Criteria criteria2 = new Criteria(new Column("ProbeEventMapping", "EVENT_ID"), (Object)eventId, 0);
            criteria = criteria.and(criteria2);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeEventMapping"));
            sq.addSelectColumn(new Column("ProbeEventMapping", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("ProbeEventMapping");
                row.set("IS_ENABLED", (Object)isEnabled);
                dataObject.updateRow(row);
                SyMUtil.getPersistence().update(dataObject);
            }
            else {
                final Row row = new Row("ProbeEventMapping");
                row.set("PROBE_ID", (Object)probeId);
                row.set("EVENT_ID", (Object)eventId);
                row.set("IS_ENABLED", (Object)isEnabled);
                dataObject.addRow(row);
                SyMUtil.getPersistence().add(dataObject);
                ProbeNotificationUtil.logger.log(Level.FINE, "A new Entry added for probeEvent Mapping for event  " + eventId + " for probe " + probeId);
            }
        }
        catch (final DataAccessException e) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "Exception occured while setting event enabled status " + eventId + " for probe " + probeId + " ", (Throwable)e);
        }
    }
    
    public static boolean isMailEnabledForProbe(final Long probeId) {
        return isModeEnabledForProbe("EMAIL", probeId);
    }
    
    public static boolean isSMSEnabledForProbe(final Long probeId) {
        return isModeEnabledForProbe("SMS", probeId);
    }
    
    public static boolean isModeEnabledForProbe(final String mode, final Long probeId) {
        String tableName = "";
        String columnName = "";
        if (mode.equals("EMAIL")) {
            tableName = "ProbeNotificationEmailAddr";
            columnName = "PROBE_ID";
        }
        else if (mode.equals("SMS")) {
            tableName = "ProbeNotificationSmsUser";
            columnName = "PROBE_ID";
        }
        try {
            final Criteria criteria = new Criteria(new Column(tableName, columnName), (Object)probeId, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table(tableName));
            sq.addSelectColumn(new Column(tableName, "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final DataAccessException e) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "Exception occured while getting notification mode status for probe " + probeId + " ", (Throwable)e);
        }
        return false;
    }
    
    public static void setModeDisabled(final String mode, final Long probeId) {
        String tableName = "";
        String columnName = "";
        if (mode.equals("EMAIL")) {
            tableName = "ProbeNotificationEmailAddr";
            columnName = "PROBE_ID";
        }
        else if (mode.equals("SMS")) {
            tableName = "ProbeNotificationSmsUser";
            columnName = "PROBE_ID";
        }
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
            final Criteria criteria = new Criteria(new Column(tableName, columnName), (Object)probeId, 0);
            deleteQuery.setCriteria(criteria);
            SyMUtil.getPersistence().delete(deleteQuery);
            ProbeNotificationUtil.logger.log(Level.FINE, "Successfuly deleted rows of " + tableName + " for probe " + probeId);
        }
        catch (final DataAccessException e) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "Exception occured while deleting rows of " + tableName + " for probe " + probeId + " ", (Throwable)e);
        }
    }
    
    public static List getEmailAsList(final Long probeId) {
        List emailList = new LinkedList();
        try {
            final Criteria criteria = new Criteria(new Column("ProbeNotificationEmailAddr", "PROBE_ID"), (Object)probeId, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeNotificationEmailAddr"));
            sq.addSelectColumn(new Column("ProbeNotificationEmailAddr", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final String emailIdStrings = (String)dataObject.getFirstRow("ProbeNotificationEmailAddr").get("EMAIL_ADDR");
                final String[] emailArray = emailIdStrings.split(",");
                emailList = Arrays.asList(emailArray);
            }
        }
        catch (final DataAccessException e) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "Exception while getting notification emailId's due to", (Throwable)e);
        }
        catch (final Exception e2) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "exception while getting notification email id's", e2);
        }
        return emailList;
    }
    
    public static ArrayList getSMSUsers(final Long probeId) {
        final ArrayList userList = new ArrayList();
        try {
            final Criteria criteria = new Criteria(new Column("ProbeNotificationSmsUser", "PROBE_ID"), (Object)probeId, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeNotificationSmsUser"));
            sq.addSelectColumn(new Column("ProbeNotificationSmsUser", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ProbeNotificationSmsUser");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final HashMap userDetail = new HashMap();
                    userDetail.put("userID", row.get("USER_ID"));
                    userDetail.put("userName", DMUserHandler.getUserNameFromUserID((Long)row.get("USER_ID")));
                    userList.add(userDetail);
                }
            }
        }
        catch (final DataAccessException | SyMException e) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "Exception while getting SMS User's due to", e);
        }
        return userList;
    }
    
    public static void addOrUpdateSMSUser(final DataObject dObjFromUI, final Long probeId) {
        final Persistence persistence = SyMUtil.getPersistence();
        try {
            final Criteria criteria = new Criteria(new Column("ProbeNotificationSmsUser", "PROBE_ID"), (Object)probeId, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeNotificationSmsUser"));
            sq.addSelectColumn(new Column("ProbeNotificationSmsUser", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObjectFromDB = persistence.get(sq);
            if (dataObjectFromDB.isEmpty()) {
                persistence.add(dObjFromUI);
                ProbeNotificationUtil.logger.log(Level.FINE, "Successfuly added rows without any diff for  ProbeNotificationSmsUser for probe " + probeId);
            }
            else {
                final DataObject insertDiffDO = dataObjectFromDB.diff(dObjFromUI);
                DataAccess.update(insertDiffDO);
            }
        }
        catch (final DataAccessException e) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "Exception while deleting/inserting rows for probe sms notification due to", (Throwable)e);
        }
    }
    
    public static void addOrUpdateEmailAddr(final Long probeId, final String emailAddr) {
        try {
            final Criteria criteria = new Criteria(new Column("ProbeNotificationEmailAddr", "PROBE_ID"), (Object)probeId, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeNotificationEmailAddr"));
            sq.addSelectColumn(new Column("ProbeNotificationEmailAddr", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("ProbeNotificationEmailAddr");
                row.set("EMAIL_ADDR", (Object)emailAddr);
                dataObject.updateRow(row);
                SyMUtil.getPersistence().update(dataObject);
            }
            else {
                final Row row = new Row("ProbeNotificationEmailAddr");
                row.set("PROBE_ID", (Object)probeId);
                row.set("EMAIL_ADDR", (Object)emailAddr);
                dataObject.addRow(row);
                SyMUtil.getPersistence().add(dataObject);
                ProbeNotificationUtil.logger.log(Level.FINE, "A new Entry added for probeEmailaddr for probe " + probeId);
            }
        }
        catch (final DataAccessException e) {
            ProbeNotificationUtil.logger.log(Level.SEVERE, "Exception occured while setting  probeEmailaddr for probe  " + probeId + "DUE TO", (Throwable)e);
        }
    }
    
    static {
        ProbeNotificationUtil.logger = Logger.getLogger("probeActionsLogger");
    }
}
