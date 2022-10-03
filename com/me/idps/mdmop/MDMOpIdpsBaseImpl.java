package com.me.idps.mdmop;

import java.util.List;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.sync.schedule.DirectoryTask;
import com.me.idps.op.oauth.AzureOauthOPImpl;
import com.adventnet.i18n.I18N;
import com.me.idps.core.api.IdpsAPIException;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.me.mdm.directory.sync.mdm.MDMDirectoryProductImpl;

public class MDMOpIdpsBaseImpl extends MDMDirectoryProductImpl
{
    private static final String DC_NOT_VALID = "AD012";
    private static final String INVALID_AD_DOMAIN_NAME = "AD013";
    private static final String INVALID_DOMAIN_NAME = "AD014";
    private static final String LDAP_SSL_FAILURE = "AD020";
    private static final String OP_AD_VALIDATION_ERROR = "AD021";
    
    public void throwExcepForErrResp(final DirProdImplRequest dirProdImplRequest) {
        super.throwExcepForErrResp(dirProdImplRequest);
        final String response = (String)dirProdImplRequest.args[0];
        if (response.equalsIgnoreCase("Domain controller is not valid or not operational")) {
            throw new IdpsAPIException("AD012");
        }
        if (response.equalsIgnoreCase("Invalid AD Domain Name!")) {
            throw new IdpsAPIException("AD013");
        }
        if (response.contains("SSL certificate is not available in the AD, or the specified port number is not reachable")) {
            throw new IdpsAPIException("AD020");
        }
        if (response.equalsIgnoreCase("Validation Error")) {
            throw new IdpsAPIException("AD021");
        }
        if (response.contains("Invalid username/password")) {
            throw new IdpsAPIException("AD008");
        }
        if (response.toUpperCase().contains("Invalid domain name! Change the domain name to".toUpperCase())) {
            final String error_msg = response;
            final String[] split = error_msg.split(" ");
            String hint;
            try {
                hint = I18N.getMsg("AD014", new Object[] { split[split.length - 1] });
            }
            catch (final Exception ex) {
                throw new IdpsAPIException("COM0014");
            }
            throw new IdpsAPIException(hint);
        }
    }
    
    public void handleUpgrade(final DirProdImplRequest dirProdImplRequest) throws Exception {
        super.handleUpgrade(dirProdImplRequest);
        final int idpsBuildNumberBeforeUpgrade = (int)dirProdImplRequest.args[0];
        if (idpsBuildNumberBeforeUpgrade == 0) {
            new AzureOauthOPImpl().handleAzureOAuth();
        }
        if (idpsBuildNumberBeforeUpgrade < 220503) {
            this.checkAndStartDirScheduler();
        }
    }
    
    private void checkAndStartDirScheduler() throws Exception {
        final String schedulerClassName = DirectoryTask.class.getName();
        IDPSlogger.UPGRADE.log(Level.INFO, "schedulerClassName {0}", new Object[] { schedulerClassName });
        final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        IDPSlogger.UPGRADE.log(Level.INFO, "customerIds {0}", new Object[] { Arrays.toString(customerIDs) });
        final Long schedulerId = (Long)DBUtil.getValueFromDB("SchedulerClasses", "CLASS_NAME", (Object)schedulerClassName, "SCHEDULER_CLASS_ID");
        IDPSlogger.UPGRADE.log(Level.INFO, "schedulerId {0} for {1}", new Object[] { schedulerId, schedulerClassName });
        for (int index = 0; customerIDs != null && index < customerIDs.length; ++index) {
            final Long customerID = customerIDs[index];
            final List dmDomainProps = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerID);
            if (dmDomainProps != null && !dmDomainProps.isEmpty()) {
                try {
                    final boolean schedulerRunning = ApiFactoryProvider.getSchedulerAPI().getSchedulerState(schedulerId);
                    IDPSlogger.UPGRADE.log(Level.INFO, "scheduler{0}running for {1}", new Object[] { schedulerRunning ? " " : " not running ", customerID });
                    if (!schedulerRunning) {
                        IdpsFactoryProvider.getIdpsProdEnvAPI().startADSyncScheduler(customerID);
                        IDPSlogger.UPGRADE.log(Level.INFO, "enabled AD scheduler");
                    }
                }
                catch (final Exception e) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, e);
                }
            }
        }
    }
}
