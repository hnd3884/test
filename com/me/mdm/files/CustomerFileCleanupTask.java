package com.me.mdm.files;

import com.me.mdm.server.doc.DocMgmtConstants;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.adep.AppleDEPCertificateHandler;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerListener;

public class CustomerFileCleanupTask implements CustomerListener
{
    public Logger logger;
    
    public CustomerFileCleanupTask() {
        this.logger = Logger.getLogger(CustomerFileCleanupTask.class.getName());
    }
    
    public void customerAdded(final CustomerEvent customerEvent) {
    }
    
    public void customerDeleted(final CustomerEvent customerEvent) {
        final Long customerId = customerEvent.customerID;
        try {
            this.logger.log(Level.INFO, "Customer {0} delete event:::: Going to delete profilerepository dir", new Object[] { customerId });
            final String profileBasePath = ProfileUtil.getInstance().getProfileRepoDataDir(customerId);
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(profileBasePath);
            this.logger.log(Level.INFO, "Customer {0} delete event:::: Going to delete credentials dir", new Object[] { customerId });
            final String credentialBasePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("credentials") + File.separator + customerId;
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(credentialBasePath);
            this.logger.log(Level.INFO, "Customer {0} delete event:::: Going to delete dep dir", new Object[] { customerId });
            final String depPath = AppleDEPCertificateHandler.getInstance().getDEPCertificateFolder(customerId);
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(depPath);
            this.logger.log(Level.INFO, "Customer {0} delete event:::: Going to clean api_temp_downloads dir", new Object[] { customerId });
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String customerTempDir = serverHome + File.separator + "api_temp_downloads" + File.separator + customerId;
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(customerTempDir);
            this.logger.log(Level.INFO, "Customer {0} delete event:::: Going to clean app repo dir", new Object[] { customerId });
            final String appRepo = AppMgmtConstants.APP_BASE_PATH + AppMgmtConstants.APP_FILE_DIRECTORY + File.separator + customerId;
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(appRepo);
            this.logger.log(Level.INFO, "Customer {0} delete event:::: Going to clean doc repo dir", new Object[] { customerId });
            final String docRepo = DocMgmtConstants.DOC_BASE_DIRECTORY + DocMgmtConstants.DOC_FILE_DIRECTORY + File.separator + customerId;
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(docRepo);
            this.logger.log(Level.INFO, "Customer {0} delete event:::: Going to clean doc repo dir", new Object[] { customerId });
            final String appConfigTemplatePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("appconfigtemplate") + File.separator + customerId;
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(appConfigTemplatePath);
            this.logger.log(Level.INFO, "Customer {0} delete event:::: Going to clean doc repo dir", new Object[] { customerId });
            final String managedAppConfigPath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("managedappconfiguration") + File.separator + customerId;
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(managedAppConfigPath);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Cannot remove deleted customer's file : ", e);
        }
    }
    
    public void customerUpdated(final CustomerEvent customerEvent) {
    }
    
    public void firstCustomerAdded(final CustomerEvent customerEvent) {
    }
}
