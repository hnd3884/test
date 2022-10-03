package com.me.mdm.onpremise.server.license;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.mdm.server.easmanagement.EASMgmt;
import org.json.simple.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.onpremise.remotesession.AssistAuthTokenHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.license.LicenseEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseListener;

public class MDMOnpremiseLicenseListener implements LicenseListener
{
    private static Logger logger;
    
    public void licenseChanged(final LicenseEvent licenseEvent) {
        final LicenseProvider licenseProvider = LicenseProvider.getInstance();
        final String mdmLiceseEditionType;
        final String edition = mdmLiceseEditionType = licenseProvider.getMDMLicenseAPI().getMDMLiceseEditionType();
        licenseProvider.getMDMLicenseAPI();
        if (mdmLiceseEditionType.equalsIgnoreCase("Standard")) {
            try {
                final Long[] customerIdsFromDB;
                final Long[] customerIDS = customerIdsFromDB = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
                for (final Long customerID : customerIdsFromDB) {
                    new AssistAuthTokenHandler().resetAssistIntegDetails(customerID);
                    MessageProvider.getInstance().hideMessage("ASSIST_AUTH_FAILED", customerID);
                    try {
                        final DataObject dObj = MDMUtil.getPersistence().get("EASServerDetails", new Criteria(Column.getColumn("EASServerDetails", "CUSTOMER_ID"), (Object)customerID, 0));
                        final Iterator iterator = dObj.getRows("EASServerDetails");
                        while (iterator != null && iterator.hasNext()) {
                            final Row row = iterator.next();
                            final Long easServerID = (Long)row.get("EAS_SERVER_ID");
                            final JSONObject ceaRemovalDetails = new JSONObject();
                            ceaRemovalDetails.put((Object)"TASK_TYPE", (Object)"Delete");
                            ceaRemovalDetails.put((Object)"EAS_SERVER_ID", (Object)easServerID);
                            ceaRemovalDetails.put((Object)"ROLLBACK_BLOCKED_DEVICES", (Object)Boolean.FALSE);
                            EASMgmt.getInstance().removeCEA(ceaRemovalDetails);
                        }
                    }
                    catch (final Exception ex) {
                        EASMgmt.logger.log(Level.SEVERE, null, ex);
                    }
                }
                MessageProvider.getInstance().hideMessage("MDM_PROFILE_LIST_EAS_MSG");
            }
            catch (final Exception e) {
                MDMOnpremiseLicenseListener.logger.log(Level.SEVERE, "Excpetion in mdm onpremise license listener", e);
            }
        }
    }
    
    static {
        MDMOnpremiseLicenseListener.logger = Logger.getLogger(MDMOnpremiseLicenseListener.class.getName());
    }
}
