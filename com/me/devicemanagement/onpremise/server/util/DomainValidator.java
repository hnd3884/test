package com.me.devicemanagement.onpremise.server.util;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DomainValidator implements SchedulerExecutionInterface
{
    private static Logger logger;
    private static Logger downloadMgrLogger;
    private static final Integer DOMAIN_VAL_GEN_LOCK;
    private boolean isDBUpdateValidation;
    
    public DomainValidator() {
        this.isDBUpdateValidation = Boolean.FALSE;
    }
    
    public void executeTask(final Properties templateProps) {
        try {
            final String isDomainValidationRunning = SyMUtil.getSyMParameter("isDomainValidationRunning");
            if (isDomainValidationRunning == null || isDomainValidationRunning.equals("false")) {
                SyMUtil.updateSyMParameter("isDomainValidationRunning", "true");
                final DownloadManager downloadMgr = DownloadManager.getInstance();
                final int proxyType = DownloadManager.proxyType;
                if (proxyType != 0 && proxyType != 3) {
                    DomainValidator.downloadMgrLogger.log(Level.INFO, "Entered DomainValidator task");
                    boolean startValidation = Boolean.TRUE;
                    if ("in_progress".equals(SyMUtil.getSyMParameter("patch_domain_validation"))) {
                        DomainValidator.downloadMgrLogger.log(Level.INFO, "patch_domain_validation is in progress");
                        startValidation = Boolean.FALSE;
                    }
                    if ("in_progress".equals(SyMUtil.getSyMParameter("inv_domain_validation"))) {
                        DomainValidator.downloadMgrLogger.log(Level.INFO, "inv_domain_validation is in progress");
                        startValidation = Boolean.FALSE;
                    }
                    if ("in_progress".equals(SyMUtil.getSyMParameter("mdm_domain_validation"))) {
                        DomainValidator.downloadMgrLogger.log(Level.INFO, "mdm_domain_validation is in progress");
                        startValidation = Boolean.FALSE;
                    }
                    if ("in_progress".equals(SyMUtil.getSyMParameter("bmp_domain_validation"))) {
                        DomainValidator.downloadMgrLogger.log(Level.INFO, "bmp_domain_validation is in progress");
                        startValidation = Boolean.FALSE;
                    }
                    if (startValidation) {
                        if (templateProps.get("DBUpdateValidation") != null && ((Hashtable<K, Object>)templateProps).get("DBUpdateValidation").equals("true")) {
                            this.isDBUpdateValidation = Boolean.TRUE;
                            DomainValidator.downloadMgrLogger.log(Level.INFO, "DomainValidator task :  this is called after the patch DB sync");
                        }
                        if (!this.isDBUpdateValidation) {
                            DomainValidatorUtil.deleteDCDomainExceptionList();
                        }
                        for (final Object url : ProxyConfiguredHandler.getInstance().getUrlTypeList()) {
                            final String urlType = (String)url;
                            this.validateDomain(urlType);
                        }
                    }
                    if ("true".equals(((Hashtable<K, Object>)templateProps).get("proxyValidation"))) {
                        ApiFactoryProvider.getPatchDBAPI().updateDBSync();
                    }
                    ApiFactoryProvider.getProxyDBAPI().performActionAfterDomainValidation();
                }
                else {
                    DomainValidator.downloadMgrLogger.log(Level.INFO, "Proxy Not Configured, So we skipped the domain validation..");
                }
                SyMUtil.updateSyMParameter("isDomainValidationRunning", "false");
            }
        }
        catch (final Exception ee) {
            if ("in_progress".equals(SyMUtil.getSyMParameter("patch_domain_validation"))) {
                SyMUtil.updateSyMParameter("patch_domain_validation", "failed");
            }
            if ("in_progress".equals(SyMUtil.getSyMParameter("inv_domain_validation"))) {
                SyMUtil.updateSyMParameter("inv_domain_validation", "failed");
            }
            if ("in_progress".equals(SyMUtil.getSyMParameter("mdm_domain_validation"))) {
                SyMUtil.updateSyMParameter("mdm_domain_validation", "failed");
            }
            if ("in_progress".equals(SyMUtil.getSyMParameter("bmp_domain_validation"))) {
                SyMUtil.updateSyMParameter("bmp_domain_validation", "failed");
            }
            DomainValidator.downloadMgrLogger.log(Level.INFO, "DomainValidator task :  Domain Validation failed with exception ");
            ee.printStackTrace();
            SyMUtil.updateSyMParameter("isDomainValidationRunning", "false");
        }
    }
    
    private void validateDomain(final String urlType) throws DataAccessException, Exception {
        DomainValidator.downloadMgrLogger.log(Level.INFO, "DomainValidator task : Given URL type is : {0}", urlType);
        String sysparamKey = null;
        if (urlType.equals("patch")) {
            SyMUtil.updateSyMParameter("patch_domain_validation", "in_progress");
            sysparamKey = "patch_domain_validation";
        }
        else if (urlType.equals("inventory")) {
            SyMUtil.updateSyMParameter("inv_domain_validation", "in_progress");
            sysparamKey = "inv_domain_validation";
        }
        else if (urlType.equals("mdm")) {
            SyMUtil.updateSyMParameter("mdm_domain_validation", "in_progress");
            sysparamKey = "mdm_domain_validation";
        }
        else if (urlType.equals("bmp")) {
            SyMUtil.updateSyMParameter("bmp_domain_validation", "in_progress");
            sysparamKey = "bmp_domain_validation";
        }
        DomainValidatorUtil.clearNotSupportedDomains();
        final Long[] urlid = DomainValidatorUtil.getInstance().getDCDomainIdList();
        final Criteria domainCrit = ApiFactoryProvider.getPatchDBAPI().getDomainExceptionListCri(urlType, Boolean.valueOf(this.isDBUpdateValidation), urlid);
        DomainValidator.logger.log(Level.INFO, "DomainValidator task : {0} : getting DomainExceptionList table values using below Criteria : {1}", new Object[] { sysparamKey, domainCrit });
        List crawlerDomainList = ApiFactoryProvider.getPatchDBAPI().getDomainExceptionList(domainCrit, urlType);
        crawlerDomainList = DomainValidatorUtil.getInstance().getDCValidatedFailedDomainsList(urlType, crawlerDomainList);
        DomainValidator.downloadMgrLogger.log(Level.INFO, "DomainValidator task :  {0} : Domain list size : {1}", new Object[] { sysparamKey, crawlerDomainList.size() });
        if (!this.isDBUpdateValidation) {
            DomainValidatorUtil.getInstance().setDCBulkValidationStatus(urlType, 4);
        }
        boolean validationStatus = Boolean.TRUE;
        for (final Object domainsList : crawlerDomainList) {
            final Properties domainDetails = (Properties)domainsList;
            ((Hashtable<String, String>)domainDetails).put("STATUS", String.valueOf(3));
            DomainValidatorUtil.getInstance().addOrUpdateDCDomainExceptionList(domainDetails);
            final String urlDomain = domainDetails.getProperty("URLDOMAIN");
            final boolean downloadStatus = DownloadManager.getInstance().downloadURLValidator(urlDomain, new SSLValidationType[0]);
            Integer status = 1;
            if (!downloadStatus) {
                status = 2;
                validationStatus = Boolean.FALSE;
            }
            if (domainDetails.containsKey("STATUS")) {
                domainDetails.remove("STATUS");
            }
            ((Hashtable<String, String>)domainDetails).put("STATUS", String.valueOf(status));
            DomainValidatorUtil.getInstance().addOrUpdateDCDomainExceptionList(domainDetails);
        }
        if (sysparamKey != null) {
            if (validationStatus) {
                SyMUtil.updateSyMParameter(sysparamKey, "success");
                DomainValidator.downloadMgrLogger.log(Level.INFO, "DomainValidator task : {0} : Domain validation success ", sysparamKey);
            }
            else {
                SyMUtil.updateSyMParameter(sysparamKey, "failed");
                DomainValidator.downloadMgrLogger.log(Level.INFO, "DomainValidator task : {0} : Domain validation failure ", sysparamKey);
            }
        }
        if (this.isDBUpdateValidation) {
            final Object maxvalue = DBUtil.getMaxOfValue("DomainExceptionList", "VERSION", (Criteria)null);
            if (maxvalue != null) {
                final long maxDomainVersion = Long.parseLong(String.valueOf(maxvalue));
                SyMUtil.updateSyMParameter("validated_domain_max_db_version", String.valueOf(maxDomainVersion));
                DomainValidator.downloadMgrLogger.log(Level.INFO, "DomainValidator task :  Max value stored in DomainExceptionList table " + maxDomainVersion);
            }
        }
    }
    
    static {
        DomainValidator.logger = Logger.getLogger(DomainValidator.class.getName());
        DomainValidator.downloadMgrLogger = Logger.getLogger("DownloadManager");
        DOMAIN_VAL_GEN_LOCK = new Integer(1);
    }
}
