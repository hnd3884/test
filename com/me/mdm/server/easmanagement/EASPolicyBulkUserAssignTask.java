package com.me.mdm.server.easmanagement;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.DeleteQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class EASPolicyBulkUserAssignTask extends CSVTask
{
    public static Logger logger;
    private EASPolicyBulkUserAssignCSVProcessor easPolicyBulkUserAssignCSVProcessor;
    
    public EASPolicyBulkUserAssignTask() {
        this.easPolicyBulkUserAssignCSVProcessor = new EASPolicyBulkUserAssignCSVProcessor();
    }
    
    protected JSONObject getInputs(final Properties taskProps) throws Exception {
        final Long customerID = Long.parseLong(((Hashtable<K, String>)taskProps).get("customerID"));
        final Long userID = Long.parseLong(((Hashtable<K, String>)taskProps).get("userID"));
        final Long easServerId = Long.parseLong(((Hashtable<K, String>)taskProps).get("EAS_SERVER_ID"));
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put((Object)"CUSTOMER_ID", (Object)customerID);
        jsonObj.put((Object)"USER_ID", (Object)userID);
        jsonObj.put((Object)"EAS_SERVER_ID", (Object)easServerId);
        jsonObj.put((Object)"GRACE_DAYS", ((Hashtable<K, Object>)taskProps).get("GRACE_DAYS"));
        jsonObj.put((Object)"APPLIED_FOR", ((Hashtable<K, Object>)taskProps).get("APPLIED_FOR"));
        jsonObj.put((Object)"UPDATED_BY", ((Hashtable<K, Object>)taskProps).get("UPDATED_BY"));
        jsonObj.put((Object)"ROLLBACK_BLOCKED_DEVICES", ((Hashtable<K, Object>)taskProps).get("ROLLBACK_BLOCKED_DEVICES"));
        jsonObj.put((Object)"UPDATE_POLICY_SELECTION", ((Hashtable<K, Object>)taskProps).get("UPDATE_POLICY_SELECTION"));
        if (taskProps.containsKey("SEND_NOTIF_MAIL")) {
            jsonObj.put((Object)"SEND_NOTIF_MAIL", ((Hashtable<K, Object>)taskProps).get("SEND_NOTIF_MAIL"));
        }
        EASPolicyBulkUserAssignTask.logger.log(Level.INFO, "In executeTask of bulk CEA policy input json:{0}", jsonObj.toJSONString());
        return jsonObj;
    }
    
    protected void performOperation(final JSONObject json) throws Exception {
        try {
            final Long customerId = (Long)json.get((Object)"CUSTOMER_ID");
            final Long easServerId = (Long)json.get((Object)"EAS_SERVER_ID");
            final Criteria custIDcri = new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0);
            final Criteria nullErrRemarksCri = new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "ERROR_REMARKS"), (Object)null, 0);
            final Criteria mailboxNotFoundCri = new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "EAS_MAILBOX_ID"), (Object)null, 0);
            final Criteria mailboxFoundCri = new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "EAS_MAILBOX_ID"), (Object)null, 1);
            final Criteria mailboxNotSelectedCri = new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "SELECTED_MAILBOX_ID"), (Object)null, 0);
            final Criteria mailboxSelectedCri = new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "SELECTED_MAILBOX_ID"), (Object)null, 1);
            UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("EASPolicyBulkUserAssignImportInfo");
            updateQuery.setCriteria(custIDcri.and(nullErrRemarksCri).and(mailboxNotFoundCri));
            updateQuery.setUpdateColumn("ERROR_REMARKS", (Object)I18N.getMsg("mdm.api.error.csv_mailbox_not_exist", new Object[0]));
            SyMUtil.getPersistenceLite().update(updateQuery);
            updateQuery = (UpdateQuery)new UpdateQueryImpl("EASPolicyBulkUserAssignImportInfo");
            updateQuery.setCriteria(custIDcri.and(nullErrRemarksCri).and(mailboxSelectedCri));
            updateQuery.setUpdateColumn("ERROR_REMARKS", (Object)I18N.getMsg("mdm.api.error.csv_mailbox_already_selected", new Object[0]));
            SyMUtil.getPersistenceLite().update(updateQuery);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EASPolicyBulkUserAssignImportInfo"));
            sQuery.addSelectColumn(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "USER_IMPORT_ID"));
            sQuery.addSelectColumn(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "EAS_MAILBOX_ID"));
            sQuery.setCriteria(custIDcri.and(nullErrRemarksCri).and(mailboxFoundCri).and(mailboxNotSelectedCri));
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            if (dobj != null && !dobj.isEmpty() && dobj.containsTable("EASPolicyBulkUserAssignImportInfo")) {
                final Iterator<Row> iter = dobj.getRows("EASPolicyBulkUserAssignImportInfo");
                final ArrayList<String> mailboxList = new ArrayList<String>();
                while (iter != null && iter.hasNext()) {
                    final Row row = iter.next();
                    mailboxList.add(String.valueOf(row.get("EAS_MAILBOX_ID")));
                }
                final JSONObject easBulkJson = new JSONObject();
                easBulkJson.put((Object)"CUSTOMER_ID", (Object)customerId);
                easBulkJson.put((Object)"EASMailboxDetails", (Object)mailboxList);
                easBulkJson.put((Object)"EAS_SERVER_ID", (Object)easServerId);
                easBulkJson.put((Object)"GRACE_DAYS", json.get((Object)"GRACE_DAYS"));
                easBulkJson.put((Object)"UPDATED_BY", json.get((Object)"UPDATED_BY"));
                easBulkJson.put((Object)"APPLIED_FOR", json.get((Object)"APPLIED_FOR"));
                easBulkJson.put((Object)"SEND_NOTIF_MAIL", json.get((Object)"SEND_NOTIF_MAIL"));
                easBulkJson.put((Object)"UPDATE_POLICY_SELECTION", json.get((Object)"UPDATE_POLICY_SELECTION"));
                easBulkJson.put((Object)"ROLLBACK_BLOCKED_DEVICES", json.get((Object)"ROLLBACK_BLOCKED_DEVICES"));
                this.bulkAssignMailBoxToPolicy(easBulkJson);
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("EASPolicyBulkUserAssignImportInfo");
                deleteQuery.setCriteria(new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "EMAIL_ADDRESS"), (Object)mailboxList.toArray(), 8)));
                SyMUtil.getPersistenceLite().delete(deleteQuery);
            }
            this.setFailureCount(this.customerID);
        }
        catch (final Exception ex) {
            EASPolicyBulkUserAssignTask.logger.log(Level.SEVERE, "Exception in  EASPolicyBulkUserAssignTask:{0}", ex);
            throw ex;
        }
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EASPolicyBulkUserAssignImportInfo"));
            final Column countColumn = Column.getColumn("EASPolicyBulkUserAssignImportInfo", "USER_IMPORT_ID").count();
            countColumn.setColumnAlias("COUNT_COLUMN");
            sQuery.addSelectColumn(countColumn);
            sQuery.setCriteria(new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            ds.next();
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)CSVProcessor.getFailedLabel("EASPolicy_BulkAssignUser"), ds.getValue("COUNT_COLUMN"));
            jsonObj.put((Object)CSVProcessor.getStatusLabel("EASPolicy_BulkAssignUser"), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
            EASPolicyBulkUserAssignTask.logger.info("Persisted failure count in Customer Params");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void bulkAssignMailBoxToPolicy(final JSONObject easBulkDetails) {
        EASMgmt.logger.log(Level.INFO, "bulk user assign policy Details : {0}", easBulkDetails.toString());
        try {
            final Long customerId = (Long)easBulkDetails.get((Object)"CUSTOMER_ID");
            final Long easServerId = (Long)easBulkDetails.get((Object)"EAS_SERVER_ID");
            final ArrayList<String> userList = (ArrayList<String>)easBulkDetails.get((Object)"EASMailboxDetails");
            final Criteria customerCriteria = new Criteria(Column.getColumn("EASServerDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria serverCriteria = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)easServerId, 0);
            final SelectQuery existingEASMailboxQuery = CEAApiHandler.getCEAPolicyQuery();
            existingEASMailboxQuery.setCriteria(customerCriteria.and(serverCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(existingEASMailboxQuery);
            if (dataObject != null && !dataObject.isEmpty() && dataObject.containsTable("EASSelectedMailbox")) {
                final Iterator selectedMailBoxIteror = dataObject.getRows("EASSelectedMailbox");
                while (selectedMailBoxIteror != null && selectedMailBoxIteror.hasNext()) {
                    final Row selectedMailBoxRow = selectedMailBoxIteror.next();
                    if (selectedMailBoxIteror != null) {
                        userList.add(String.valueOf(selectedMailBoxRow.get("EAS_MAILBOX_ID")));
                    }
                }
            }
            final JSONObject easJsonObj = new JSONObject();
            easJsonObj.put((Object)"EASSelectedMailbox", (Object)userList);
            easJsonObj.put((Object)"TASK_TYPE", (Object)"update");
            easJsonObj.put((Object)"CUSTOMER_ID", (Object)customerId);
            easJsonObj.put((Object)"EAS_SERVER_ID", (Object)String.valueOf(easServerId));
            easJsonObj.put((Object)"UPDATED_BY", easBulkDetails.get((Object)"UPDATED_BY"));
            easJsonObj.put((Object)"GRACE_DAYS", easBulkDetails.get((Object)"GRACE_DAYS"));
            easJsonObj.put((Object)"APPLIED_FOR", easBulkDetails.get((Object)"APPLIED_FOR"));
            easJsonObj.put((Object)"SEND_NOTIF_MAIL", easBulkDetails.get((Object)"SEND_NOTIF_MAIL"));
            easJsonObj.put((Object)"UPDATE_POLICY_SELECTION", easBulkDetails.get((Object)"UPDATE_POLICY_SELECTION"));
            easJsonObj.put((Object)"ROLLBACK_BLOCKED_DEVICES", easBulkDetails.get((Object)"ROLLBACK_BLOCKED_DEVICES"));
            EASMgmt.getInstance().configCEApolicy(easJsonObj);
        }
        catch (final Exception ex) {
            EASPolicyBulkUserAssignTask.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    static {
        EASPolicyBulkUserAssignTask.logger = Logger.getLogger("EASMgmtLogger");
    }
}
