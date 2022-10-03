package com.me.mdm.server.easmanagement;

import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class EASPolicyBulkUserAssignCSVProcessor extends CSVProcessor
{
    public static Logger logger;
    public static final String IS_EMAIL_ADDRESS_CSV = "IsEmailInCSV";
    public static final String OPERATION_LABEL = "EASPolicy_BulkAssignUser";
    
    protected String getOperationLabel() {
        return "EASPolicy_BulkAssignUser";
    }
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"EMAIL_ADDRESS", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "IsEmailInCSV"));
            return tableDetails;
        }
        catch (final Exception e) {
            EASPolicyBulkUserAssignCSVProcessor.logger.log(Level.SEVERE, "Error in EAS generateTableDetails", e);
            throw e;
        }
    }
    
    protected List<String> listMandatoryHeaders() throws Exception {
        try {
            final List<String> mandatoryHeaders = new ArrayList<String>();
            mandatoryHeaders.add("EMAIL_ADDRESS");
            return mandatoryHeaders;
        }
        catch (final Exception e) {
            EASPolicyBulkUserAssignCSVProcessor.logger.log(Level.SEVERE, "Error in EAS listMandatoryHeaders", e);
            throw e;
        }
    }
    
    protected void validateRowCount(final int rowCount) throws Exception {
        final Long customerId = Long.valueOf(CustomerInfoThreadLocal.getCustomerId());
        final Criteria custCriteria = new Criteria(Column.getColumn("EASServerDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("EASPolicyBulkUserAssignImportInfo");
        updateQuery.addJoin(new Join("EASPolicyBulkUserAssignImportInfo", "EASMailboxDetails", new String[] { "EMAIL_ADDRESS" }, new String[] { "EMAIL_ADDRESS" }, 2));
        updateQuery.addJoin(new Join("EASMailboxDetails", "EASServerDetails", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_SERVER_ID" }, 2));
        updateQuery.setCriteria(custCriteria);
        updateQuery.setUpdateColumn("EAS_MAILBOX_ID", (Object)Column.getColumn("EASMailboxDetails", "EAS_MAILBOX_ID"));
        SyMUtil.getPersistenceLite().update(updateQuery);
        updateQuery = (UpdateQuery)new UpdateQueryImpl("EASPolicyBulkUserAssignImportInfo");
        updateQuery.addJoin(new Join("EASPolicyBulkUserAssignImportInfo", "EASSelectedMailbox", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 2));
        updateQuery.addJoin(new Join("EASSelectedMailbox", "EASMailboxDetails", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, 2));
        updateQuery.addJoin(new Join("EASMailboxDetails", "EASServerDetails", new String[] { "EAS_SERVER_ID" }, new String[] { "EAS_SERVER_ID" }, 2));
        updateQuery.setCriteria(custCriteria);
        updateQuery.setUpdateColumn("SELECTED_MAILBOX_ID", (Object)Column.getColumn("EASSelectedMailbox", "EAS_MAILBOX_ID"));
        SyMUtil.getPersistenceLite().update(updateQuery);
        int addedCount = 0;
        final JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
        if (exchangeServerDetails != null && exchangeServerDetails.containsKey((Object)"selectedMailboxCount")) {
            addedCount = (int)exchangeServerDetails.get((Object)"selectedMailboxCount");
        }
        final Criteria criteria = new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "EAS_MAILBOX_ID"), (Object)null, 1).and(new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "SELECTED_MAILBOX_ID"), (Object)null, 0)).and(new Criteria(Column.getColumn("EASPolicyBulkUserAssignImportInfo", "CUSTOMER_ID"), (Object)customerId, 0));
        final int actualRowCount = DBUtil.getRecordCount("EASPolicyBulkUserAssignImportInfo", "USER_IMPORT_ID", criteria);
        if (actualRowCount + addedCount > 4000) {
            final String errorMsg = I18N.getMsg("mdm.cea.csv.size.limit", new Object[0]);
            throw new SyMException(30009, errorMsg, (Throwable)new ArrayIndexOutOfBoundsException());
        }
    }
    
    static {
        EASPolicyBulkUserAssignCSVProcessor.logger = Logger.getLogger("EASMgmtLogger");
    }
}
