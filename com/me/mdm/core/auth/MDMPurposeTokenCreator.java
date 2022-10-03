package com.me.mdm.core.auth;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class MDMPurposeTokenCreator
{
    private static Logger logger;
    
    public static Long getPurposeIDForPurposeKey(final int purpose) {
        Long purposeID = null;
        try {
            final Criteria purposeCriteria = new Criteria(Column.getColumn("MDMPurposeToken", "PURPOSE_KEY"), (Object)purpose, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MDMPurposeToken", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerId(), 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("MDMPurposeToken", purposeCriteria.and(customerCriteria));
            if (!dataObject.isEmpty()) {
                purposeID = (Long)dataObject.getFirstValue("MDMPurposeToken", "MDM_PURPOSE_TOKEN_ID");
            }
            else {
                purposeID = null;
            }
        }
        catch (final Exception e) {
            MDMPurposeTokenCreator.logger.log(Level.SEVERE, "Issue in creating Purpose Token");
        }
        return purposeID;
    }
    
    public static String getPurposeToken(final int purpose) {
        String purposeToken = null;
        try {
            purposeToken = getPurposeToken(purpose, CustomerInfoUtil.getInstance().getCustomerId());
        }
        catch (final Exception e) {
            MDMPurposeTokenCreator.logger.log(Level.SEVERE, "Issue in creating Purpose Token");
        }
        return purposeToken;
    }
    
    public static String getPurposeToken(final int purpose, final Long customerID) {
        String purposeToken = null;
        try {
            final Criteria purposeCriteria = new Criteria(Column.getColumn("MDMPurposeToken", "PURPOSE_KEY"), (Object)purpose, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MDMPurposeToken", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("MDMPurposeToken", purposeCriteria.and(customerCriteria));
            if (dataObject.isEmpty()) {
                purposeToken = addOrUpdatePurposeToken(purpose, false, customerID);
            }
            else {
                purposeToken = (String)dataObject.getFirstValue("MDMPurposeToken", "PURPOSE_TOKEN");
            }
        }
        catch (final Exception e) {
            MDMPurposeTokenCreator.logger.log(Level.SEVERE, "Issue in creating Purpose Token");
        }
        return purposeToken;
    }
    
    public static String addOrUpdatePurposeToken(final int purpose, final boolean update) throws Exception {
        return addOrUpdatePurposeToken(purpose, update, CustomerInfoUtil.getInstance().getCustomerId());
    }
    
    public static String addOrUpdatePurposeToken(final int purpose, final boolean update, final Long customerID) throws Exception {
        final String purposeToken = MDMUtil.generateNewRandomToken("MDMPurposeToken", "PURPOSE_TOKEN", "MDM_PURPOSE_TOKEN_ID");
        if (update) {
            final Criteria purposeCriteria = new Criteria(Column.getColumn("MDMPurposeToken", "PURPOSE_KEY"), (Object)purpose, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MDMPurposeToken", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject dataObject1 = MDMUtil.getPersistence().get("MDMPurposeToken", purposeCriteria.and(customerCriteria));
            final Row row = dataObject1.getFirstRow("MDMPurposeToken");
            row.set("PURPOSE_TOKEN", (Object)purposeToken);
            dataObject1.updateRow(row);
            MDMUtil.getPersistence().update(dataObject1);
            MDMPurposeTokenCreator.logger.log(Level.INFO, "MDMCloudPurposeTokenCreator: Purpose token updated succesfully");
        }
        else {
            final DataObject dataObject2 = (DataObject)new WritableDataObject();
            final Row row2 = new Row("MDMPurposeToken");
            row2.set("PURPOSE_KEY", (Object)purpose);
            row2.set("PURPOSE_TOKEN", (Object)purposeToken);
            row2.set("CUSTOMER_ID", (Object)customerID);
            dataObject2.addRow(row2);
            MDMUtil.getPersistence().add(dataObject2);
            MDMPurposeTokenCreator.logger.log(Level.INFO, "MDMCloudPurposeTokenCreator: Purpose token created succesfully");
        }
        MDMPurposeTokenCreator.logger.log(Level.INFO, "MDMCloudPurposeTokenCreator: Purpose token : {0} Purpose Key : {1}", new Object[] { purposeToken, purpose });
        return purposeToken;
    }
    
    static {
        MDMPurposeTokenCreator.logger = Logger.getLogger("MDMLogger");
    }
}
