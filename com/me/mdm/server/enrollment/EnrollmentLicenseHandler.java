package com.me.mdm.server.enrollment;

import com.me.mdm.server.factory.MDMUtilAPI;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.List;
import java.util.Collection;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class EnrollmentLicenseHandler
{
    private static EnrollmentLicenseHandler enrollmentLicenseHandler;
    public static Logger logger;
    
    public static EnrollmentLicenseHandler getInstance() {
        if (EnrollmentLicenseHandler.enrollmentLicenseHandler == null) {
            EnrollmentLicenseHandler.enrollmentLicenseHandler = new EnrollmentLicenseHandler();
        }
        return EnrollmentLicenseHandler.enrollmentLicenseHandler;
    }
    
    public String manageDevice(final String deviceIDs) {
        final StringTokenizer tokenizer = new StringTokenizer(deviceIDs, ",");
        final List sDeviceIDs = new ArrayList();
        while (tokenizer.hasMoreTokens()) {
            sDeviceIDs.add(Long.parseLong((String)tokenizer.nextElement()));
        }
        final JSONArray resourceIds = new JSONArray((Collection)ManagedDeviceHandler.getInstance().getManagedDeviceIdFromErids(sDeviceIDs, "RESOURCE_ID"));
        return this.manageDevice(resourceIds, sDeviceIDs);
    }
    
    public String manageDevice(final JSONArray resourceIds, final List sDeviceIDs) {
        String msg = null;
        try {
            final JSONObject managedDeviceDetails = new JSONObject();
            managedDeviceDetails.put("MANAGED_STATUS", 2);
            managedDeviceDetails.put("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
            managedDeviceDetails.put("resourceIds", (Object)resourceIds);
            managedDeviceDetails.put("requestIds", (Collection)sDeviceIDs);
            managedDeviceDetails.put("customer_id", (Object)CustomerInfoUtil.getInstance().getCustomerId());
            final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount(MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck());
            if (MDMEnrollmentUtil.getInstance().isLicenseLimitReached(CustomerInfoUtil.getInstance().getCustomerId())) {
                msg = "license_limit_reached";
            }
            else if (LicenseProvider.getInstance().isMobileDeviceLicenseLimitExceed(managedDeviceCount + sDeviceIDs.size())) {
                msg = "additional_license_needed";
            }
            else {
                final boolean successfullyRemoved = ManagedDeviceHandler.getInstance().bulkUpdateManagedDeviceDetails(managedDeviceDetails);
                if (successfullyRemoved) {
                    msg = "success";
                }
                else {
                    msg = "failure";
                }
            }
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        }
        catch (final Exception exp) {
            EnrollmentLicenseHandler.logger.log(Level.WARNING, "Exception while moving device from waiting for license...", exp);
            msg = "failure";
        }
        return msg;
    }
    
    public int getAwaitingLicenseDeviceCount(final Long customerId, final EnrollmentAPIConstants.AwaitingLicenseType awaitingLicenseType) {
        int deviceCount = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(6), 0);
            Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final MDMUtilAPI mdmUtilAPI = MDMApiFactoryProvider.getMDMUtilAPI();
            final Criteria deviceTypeCriteria = (awaitingLicenseType == EnrollmentAPIConstants.AwaitingLicenseType.UEM) ? mdmUtilAPI.getUemManagedDeviceCountCriteriaForLicenseCheck() : mdmUtilAPI.getManagedDeviceCountCriteriaForLicenseCheck();
            if (deviceTypeCriteria != null) {
                customerCriteria = customerCriteria.and(deviceTypeCriteria);
            }
            selectQuery.setCriteria(criteria.and(customerCriteria));
            deviceCount = DBUtil.getRecordCount(selectQuery, "ManagedDevice", "RESOURCE_ID");
        }
        catch (final Exception exp) {
            EnrollmentLicenseHandler.logger.log(Level.SEVERE, "Exception in getAwaitingLicenseDeviceCount", exp);
        }
        return deviceCount;
    }
    
    static {
        EnrollmentLicenseHandler.enrollmentLicenseHandler = null;
        EnrollmentLicenseHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
