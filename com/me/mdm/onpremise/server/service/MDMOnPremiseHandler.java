package com.me.mdm.onpremise.server.service;

import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.ems.onpremise.security.certificate.api.core.utils.SSLCertificateUtil;
import com.adventnet.sym.server.mdm.certificates.scep.ScepRootCAGenerator;
import java.io.FileNotFoundException;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.adventnet.sym.server.mdm.certificates.MdmCertAuthUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.server.settings.cca.CcaChangeListener;
import com.me.mdm.onpremise.server.settings.cca.CcaChangeListenerMdmImpl;
import com.me.devicemanagement.onpremise.server.settings.cca.CcaChangeEvent;
import com.me.devicemanagement.framework.server.license.LicenseListener;
import com.me.mdm.onpremise.server.license.MDMOnpremiseLicenseListener;
import com.me.devicemanagement.framework.server.license.LicenseListenerHandler;
import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredListener;
import com.me.mdm.onpremise.server.settings.ProxyListenerMDMImpl;
import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredHandler;
import com.me.ems.onpremise.security.certificate.api.core.listeners.ServerCertificateValidator;
import com.me.mdm.onpremise.server.settings.ServerCertificateValidatorMDMImpl;
import com.me.ems.onpremise.security.certificate.api.core.handlers.ServerCertificateValidationHandler;
import com.me.ems.onpremise.security.certificate.api.core.listeners.ImportSSLCertificateListener;
import com.me.mdm.onpremise.server.settings.ImportSSLCertificateListenerMDMImpl;
import com.me.ems.onpremise.security.certificate.api.core.handlers.ImportSSLCertificateHandler;
import com.me.devicemanagement.onpremise.server.certificate.ServerSSLCertificateListener;
import com.me.mdm.onpremise.server.settings.ServerSSLCertificateListenerMDMImpl;
import com.me.devicemanagement.onpremise.server.certificate.ServerSSLCertificateHandler;
import com.me.devicemanagement.onpremise.server.settings.nat.NATListener;
import com.me.mdm.onpremise.server.settings.NATListenerMDMImpl;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.idps.mdmop.IdpsMdmopImpl;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.dep.DEPTechnicianUserListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.service.ServiceHandlerAPI;

public class MDMOnPremiseHandler implements ServiceHandlerAPI
{
    private static Logger logger;
    
    public void initialize() {
        MDMOnPremiseHandler.logger.log(Level.INFO, "Initializing MDM Certificate listeners from MDMOnPremiseHandler.initialize()");
        registerServerSSLCertificateListeners();
        registerCertificateListeners();
        if (isFirstStartup()) {
            new DEPTechnicianUserListener().addAdminEnrollmentTemplateForDefaultAdminUser();
        }
    }
    
    public void initiate() {
        registerProxyListener();
        registerNATChangedListeners();
        this.registerLicenseChangeListener();
        this.registerCcaChangeListener();
        if (CustomerInfoUtil.isMDMP()) {
            doCheckForMDMPProductUpdates();
        }
        setSystemProperty();
        initializeServerInfo();
        this.checkAndisiOSSystemAppsPopulated();
        this.checkAndisWindowsSystemAppsPopulated();
        try {
            MessageProvider.getInstance().hideMessage("MDM_DIGICERT_RESTART_REQUIRED");
            MessageProvider.getInstance().hideMessage("MDM_DIGICERT_RESTART_REQUIRED_NEW");
        }
        catch (final Exception e) {
            MDMOnPremiseHandler.logger.log(Level.WARNING, "Exception while hiding digicert message box ", e);
        }
        registerIDPSimpl();
        this.checkServerTimeMismatch();
        generateScepRootCACertificate();
        this.checkAndDisableCertificateMessages();
    }
    
    private static void registerIDPSimpl() {
        IdpsMdmopImpl.getInstance().register();
    }
    
    private static void initializeServerInfo() {
        MDMUtil.addOrUpdateMDMServerInfo();
    }
    
    public void destroy() {
    }
    
    private static void registerNATChangedListeners() {
        NATHandler.getInstance().addNATListener((NATListener)new NATListenerMDMImpl());
    }
    
    private static void registerServerSSLCertificateListeners() {
        ServerSSLCertificateHandler.getInstance().addServerSSLCertificateListener((ServerSSLCertificateListener)new ServerSSLCertificateListenerMDMImpl());
    }
    
    private static void registerCertificateListeners() {
        ImportSSLCertificateHandler.getInstance().addImportSSLCertificateListener((ImportSSLCertificateListener)new ImportSSLCertificateListenerMDMImpl());
        ServerCertificateValidationHandler.getInstance().addServerCertificateValidationHandler((ServerCertificateValidator)new ServerCertificateValidatorMDMImpl());
    }
    
    private static void registerProxyListener() {
        ProxyConfiguredHandler.getInstance().addProxyConfiguredListener((ProxyConfiguredListener)new ProxyListenerMDMImpl());
    }
    
    private void registerLicenseChangeListener() {
        LicenseListenerHandler.getInstance().addLicenseListener((LicenseListener)new MDMOnpremiseLicenseListener());
    }
    
    private void registerCcaChangeListener() {
        CcaChangeEvent.getInstance().addListener((CcaChangeListener)new CcaChangeListenerMdmImpl());
    }
    
    private static void doCheckForMDMPProductUpdates() {
        MDMOnPremiseHandler.logger.log(Level.INFO, "InSide MDMHandler:doCheckForMDMPProductUpdates()");
        try {
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "MDMPUpdatesCheckerTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            MDMOnPremiseHandler.logger.log(Level.INFO, "Task Info which is passed to Scheduler.executeAsynchronously(): {0}", taskInfoMap);
            final Properties prop = new Properties();
            final CommonQueueData data = new CommonQueueData();
            data.setClassName("com.me.mdm.onpremise.server.admin.task.MDMPUpdatesCheckerTask");
            data.setTaskName("MDMPUpdatesCheckerTask");
            data.setCustomerId(Long.valueOf(1L));
            data.setEmptyJsonQueueData();
            CommonQueueUtil.getInstance().addToQueue(data, CommonQueues.MDM_MAILTASK);
            MDMOnPremiseHandler.logger.log(Level.INFO, "MDMPUpdatesCheckerTask is invoked in async mode");
        }
        catch (final Exception ex) {
            MDMOnPremiseHandler.logger.log(Level.WARNING, "Caught exception while invoking MDMPUpdatesCheckerTask in doCheckForMDMPProductUpdates", ex);
        }
    }
    
    public static void setSystemProperty() {
        System.setProperty("2factor.auth", "com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
    }
    
    private void checkAndisiOSSystemAppsPopulated() {
        try {
            final String value = MDMUtil.getSyMParameter("isiOSSystemAppsPopulated");
            if (!Boolean.parseBoolean(value)) {
                MDMOnPremiseHandler.logger.log(Level.INFO, "Going to populate system apps in server start up");
                final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
                if (customerIds != null) {
                    for (final Long customerId : customerIds) {
                        AppsUtil.getInstance().addiOSSystemAppToAppGroup(customerId);
                    }
                }
                MDMUtil.updateSyMParameter("isiOSSystemAppsPopulated", "true");
            }
        }
        catch (final SyMException ex) {}
    }
    
    private void checkAndisWindowsSystemAppsPopulated() {
        try {
            final boolean value = Boolean.parseBoolean(MDMUtil.getSyMParameter("isWindowsSystemAppsPopulated"));
            if (!value) {
                MDMOnPremiseHandler.logger.log(Level.INFO, "Going to populate system apps in server start up");
                final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
                if (customerIds != null) {
                    for (final Long customerId : customerIds) {
                        AppsUtil.getInstance().addWindowSystemAppToAppGroup(customerId);
                    }
                }
                MDMUtil.updateSyMParameter("isWindowsSystemAppsPopulated", "true");
            }
        }
        catch (final SyMException ex) {}
    }
    
    private void checkServerTimeMismatch() {
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "ServerTimeValidationTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.onpremise.server.time.ServerTimeValidationTask", taskInfoMap, new Properties(), "mdmPool");
    }
    
    private static void generateScepRootCACertificate() {
        final Logger clientCertlogger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        clientCertlogger.log(Level.INFO, "MDMOnPremiseHandler: Generating Root CA cert for MDMP customers");
        Long[] customerIds = null;
        try {
            customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        }
        catch (final Exception e) {
            clientCertlogger.log(Level.SEVERE, "generateScepRootCACertificate: Exception while getting customerIds from DB", e);
        }
        if (customerIds != null) {
            for (final long customerId : customerIds) {
                try {
                    clientCertlogger.log(Level.INFO, "Checking if CA cert and key is present for customer: {0}", new Object[] { customerId });
                    final String rootCACertificatePath = MdmCertAuthUtil.Scep.getScepRootCACertificatePath(Long.valueOf(customerId));
                    final String rootCAPrivateKeyPath = MdmCertAuthUtil.Scep.getScepRootCAPrivateKeyPath(Long.valueOf(customerId));
                    final boolean isCaCertCreated = MDMiOSEntrollmentUtil.getInstance().isCACertCreatedAlready(customerId);
                    clientCertlogger.log(Level.INFO, "CustomerParams: Is CA certificate available for customer {0}? {1}", new Object[] { customerId, isCaCertCreated });
                    if (isCaCertCreated && (ApiFactoryProvider.getFileAccessAPI().readFile(rootCACertificatePath) == null || ApiFactoryProvider.getFileAccessAPI().readFile(rootCAPrivateKeyPath) == null)) {
                        clientCertlogger.log(Level.SEVERE, "Customer param says that the CA cert is created already, but the CA cert or/and key seems to be not present for customer: {0}", new Object[] { customerId });
                        throw new FileNotFoundException("CA cert or key is missing.");
                    }
                    if (!isCaCertCreated) {
                        clientCertlogger.log(Level.INFO, "MDMOnPremiseHandler: Customer : {0}, does not have SCEP root CA cert, so creating one.", new Object[] { customerId });
                        ApiFactoryProvider.getFileAccessAPI().deleteFile(rootCACertificatePath);
                        ApiFactoryProvider.getFileAccessAPI().deleteFile(rootCAPrivateKeyPath);
                        ScepRootCAGenerator.getInstance().generateRootCACertificateForCustomer(Long.valueOf(customerId));
                        clientCertlogger.log(Level.INFO, "MDMOnPremiseHandler: Root CA cert generated for customer: {0}", new Object[] { customerId });
                    }
                }
                catch (final Exception e2) {
                    clientCertlogger.log(Level.SEVERE, e2, () -> "Exception while creating CA cert and key for customer: " + customerId);
                }
            }
        }
    }
    
    private void checkAndDisableCertificateMessages() {
        try {
            if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                MessageProvider.getInstance().hideMessage("MDM_IMPORT_3P_CERT_IOS_ROOT_REGEN");
            }
        }
        catch (final Exception ex) {
            MDMOnPremiseHandler.logger.log(Level.SEVERE, "Exception while disable certificate messages in MDMOnpremise Handler. ", ex);
        }
    }
    
    private static boolean isFirstStartup() {
        boolean isFirstStartup = true;
        try {
            final int recordCount = DBUtil.getRecordActualCount("DCServerUptimeHistory", "DC_UPTIME_RECORD_ID", (Criteria)null);
            if (recordCount > 1) {
                isFirstStartup = false;
            }
        }
        catch (final Exception ex) {
            MDMOnPremiseHandler.logger.log(Level.SEVERE, "Exception while checking for first time startup", ex);
        }
        MDMOnPremiseHandler.logger.log(Level.INFO, "At first server start up {0}", System.currentTimeMillis());
        return isFirstStartup;
    }
    
    static {
        MDMOnPremiseHandler.logger = Logger.getLogger("MDMLogger");
    }
}
