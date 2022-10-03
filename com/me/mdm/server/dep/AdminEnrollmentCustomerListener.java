package com.me.mdm.server.dep;

import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.core.enrollment.IOSMigrationEnrollmentHandler;
import com.me.mdm.core.enrollment.WindowsLaptopEnrollmentHandler;
import com.me.mdm.core.enrollment.AndroidZTEnrollmentHandler;
import com.me.mdm.core.enrollment.AndroidQREnrollmentHandler;
import com.me.mdm.core.enrollment.WindowsWICDEnrollmentHandler;
import com.me.mdm.core.enrollment.KNOXAdminEnrollmentHandler;
import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.core.enrollment.AndroidAdminEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.customer.CustomerEvent;
import com.me.devicemanagement.framework.server.customer.CustomerListener;

public class AdminEnrollmentCustomerListener implements CustomerListener
{
    public void customerAdded(final CustomerEvent customerEvent) {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Going to populate enrollment template in customerAdded..");
        try {
            if (skipCustomerFilter == null || skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final HashMap<Long, JSONObject> templateData = AdminEnrollmentHandler.getAdminEnrollmentTemplateData(customerEvent.customerID);
            new AndroidAdminEnrollmentHandler().addAdminEnrollmentTemplateForAllUsers(customerEvent.customerID, templateData);
            new AppleConfiguratorEnrollmentHandler().addAdminEnrollmentTemplateForAllUsers(customerEvent.customerID, templateData);
            new KNOXAdminEnrollmentHandler().addAdminEnrollmentTemplateForAllUsers(customerEvent.customerID, templateData);
            new WindowsWICDEnrollmentHandler().addAdminEnrollmentTemplateForAllUsers(customerEvent.customerID, templateData);
            new AndroidQREnrollmentHandler().addAdminEnrollmentTemplateForAllUsers(customerEvent.customerID, templateData);
            new AndroidZTEnrollmentHandler().addAdminEnrollmentTemplateForAllUsers(customerEvent.customerID, templateData);
            new WindowsLaptopEnrollmentHandler().addAdminEnrollmentTemplateForAllUsers(customerEvent.customerID, templateData);
            new IOSMigrationEnrollmentHandler().addAdminEnrollmentTemplateForAllUsers(customerEvent.customerID, templateData);
        }
        catch (final Exception ex) {
            Logger.getLogger(AdminEnrollmentCustomerListener.class.getName()).log(Level.SEVERE, "Exception occured in customerAdded: {0}", ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Populated enrollment template in customerAdded..");
        MessageProvider.getInstance().hideMessage("DEP_ABOUT_TO_EXPIRE_MSG", customerEvent.customerID);
        MessageProvider.getInstance().hideMessage("DEP_EXPIRED_MSG", customerEvent.customerID);
        this.updateAdminUserLastSyncTime(customerEvent.customerID);
    }
    
    public void customerDeleted(final CustomerEvent customerEvent) {
        this.updateAdminUserLastSyncTime(customerEvent.customerID);
    }
    
    public void customerUpdated(final CustomerEvent customerEvent) {
        this.updateAdminUserLastSyncTime(customerEvent.customerID);
    }
    
    public void firstCustomerAdded(final CustomerEvent customerEvent) {
        this.updateAdminUserLastSyncTime(customerEvent.customerID);
    }
    
    private void updateAdminUserLastSyncTime(final Long customerId) {
        try {
            final List<Long> loginIdList = CustomerInfoUtil.getInstance().getUsersForCustomer(customerId);
            for (int i = 0; i < loginIdList.size(); ++i) {
                new AdminDeviceHandler().updateAdminDeviceLastSyncTime(loginIdList.get(i), MDMUtil.getCurrentTimeInMillis());
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(AdminEnrollmentCustomerListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
