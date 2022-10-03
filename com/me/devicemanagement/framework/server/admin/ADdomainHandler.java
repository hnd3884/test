package com.me.devicemanagement.framework.server.admin;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.winaccess.ADAccessProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.Properties;
import java.util.HashMap;

public class ADdomainHandler extends DomainHandler
{
    private Properties addOrUpdateAD(final HashMap domainDetails) {
        Properties addDomainResults = new Properties();
        try {
            final String dcName = domainDetails.get("DC_NAME");
            final String dnsName = domainDetails.get("DNS_NAME");
            final String userName = domainDetails.get("USERNAME");
            final String password = domainDetails.get("PASSWORD");
            final String action = domainDetails.get("ACTION");
            final String domainName = domainDetails.get("DOMAINNAME");
            final Long customerID = domainDetails.get("CUSTOMER_ID");
            final String prevDomName = domainDetails.get("PREVDOMNAME");
            final String isADDomain = domainDetails.get("IS_AD_DOMAIN");
            final Integer networkType = domainDetails.get("NETWORK_TYPE");
            final String adDomainName = domainDetails.get("AD_DOMAIN_NAME");
            Integer portNo = 389;
            final Boolean isSSL = domainDetails.get("USE_SSL");
            if (isSSL && isADDomain.equals("true")) {
                portNo = Integer.parseInt(domainDetails.get("PORT_NO").trim());
            }
            this.somLogger.log(Level.INFO, "Start of checking domain details");
            final Properties existingDomainProps = this.checkForSameDomainExisting(customerID, domainName, adDomainName, networkType, action);
            String i18n = existingDomainProps.getProperty("ERROR_MESSAGE");
            if (i18n != null) {
                this.somLogger.log(Level.INFO, i18n);
                ((Hashtable<String, String>)addDomainResults).put("ERROR_MESSAGE", i18n);
                return addDomainResults;
            }
            final String domainNetBiosNameForAD = this.getDomainName(adDomainName);
            this.somLogger.log(Level.INFO, "Netbios Name for given AD Domain Name " + adDomainName + " is :" + domainNetBiosNameForAD);
            if (prevDomName != null && !prevDomName.equalsIgnoreCase(domainName) && domainNetBiosNameForAD != null) {
                this.somLogger.log(Level.INFO, "Given netbios name is invalid! Domain details given already exists for the domain :" + domainNetBiosNameForAD);
                i18n = I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Domain_details_you_have_given_already_exists", new Object[] { domainNetBiosNameForAD });
                ((Hashtable<String, String>)addDomainResults).put("ERROR_MESSAGE", i18n);
                return addDomainResults;
            }
            this.somLogger.log(Level.INFO, "End of checking domain details");
            addDomainResults = this.requestAddOrUpdateDomain(domainName, adDomainName, dnsName, dcName, userName, password, isADDomain, customerID, networkType, action, isSSL, portNo);
            this.somLogger.log(Level.INFO, "*************************end of addOrUpdateDomainDetails*****************");
            return addDomainResults;
        }
        catch (final Exception exp) {
            this.somLogger.log(Level.WARNING, "Exception while adding new domain details...", exp);
            ((Hashtable<String, String>)addDomainResults).put("ERROR_MESSAGE", exp.getMessage());
            return addDomainResults;
        }
    }
    
    protected Properties requestAddOrUpdateDomain(final String domainName, final String adDomainName, final String dnsName, final String dcName, final String userName, final String password, final String isADDomain, final Long customerID, final int networkType, final String action, final Boolean isSSL, final Integer portNo) throws Exception {
        Properties addDomainResults = new Properties();
        final Properties credentialValidation = this.validatePassWordForDomain(customerID, domainName, adDomainName, dcName, userName, password, networkType, isSSL, portNo);
        final boolean validPassword = ((Hashtable<K, Boolean>)credentialValidation).get("VALID_PASSWORD");
        if (!validPassword) {
            ((Hashtable<String, String>)addDomainResults).put("ERROR_MESSAGE", ((Hashtable<K, String>)credentialValidation).get("ERROR_MESSAGE"));
            return addDomainResults;
        }
        this.somLogger.log(Level.INFO, "domain credentials validation is success");
        try {
            if (isADDomain.equals("true") && validPassword) {
                final String domainNetBiosName = ApiFactoryProvider.getADAccessAPI().getNetBIOSName(adDomainName, dcName, userName, password, isSSL, portNo);
                this.somLogger.log(Level.INFO, "Domain Netbios name for ad domainName :" + adDomainName + " is :" + domainNetBiosName);
                if (!domainNetBiosName.equalsIgnoreCase(domainName)) {
                    this.somLogger.log(Level.INFO, "Invalid Domain Netbios name is given!");
                    final String i18n = I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Invalid_domain_name_Change_the_domain_name", new Object[] { domainNetBiosName });
                    ((Hashtable<String, String>)addDomainResults).put("ERROR_MESSAGE", i18n);
                    return addDomainResults;
                }
            }
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.WARNING, "Exception while getting domain netbios name for the given ad domain details...", ex);
        }
        this.somLogger.log(Level.INFO, "end of getting domain netbios name");
        if (validPassword) {
            String domainGUID = "--";
            if (isADDomain.equals("true")) {
                domainGUID = ADAccessProvider.getInstance().getDomainGUID(domainName, adDomainName, dcName, userName, password, isSSL, portNo);
            }
            addDomainResults = this.handleAddOrUpdateDomainDetails(domainName, isADDomain, dcName, adDomainName, dnsName, userName, password, domainGUID, validPassword, networkType, action, isSSL, portNo);
        }
        else {
            this.handleDomainValidationFailure(domainName, customerID);
        }
        if (!validPassword && isADDomain.equals("true")) {
            final String i18n2 = I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Agent_installation_failed_Read_KB_with_link", new Object[0]);
            final String validationfailed = I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Validation_Error", new Object[0]);
            ((Hashtable<String, String>)addDomainResults).put("ERROR_MESSAGE", validationfailed + i18n2);
            return addDomainResults;
        }
        ((Hashtable<String, String>)addDomainResults).put("ERROR_MESSAGE", String.valueOf(validPassword));
        return addDomainResults;
    }
    
    public Properties handleAddOrUpdateDomainDetails(final String domainName, final String isADDomain, final String dcName, final String adDomainName, final String dnsName, final String userName, final String password, final String domainGUID, final Boolean verifyRes, final int networkType, final String action, final Boolean isSSL, final Integer portNo) throws Exception {
        final Properties addDomainResults = new Properties();
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final Properties existingDomainProps = this.checkForSameDomainExisting(customerID, domainName, adDomainName, networkType, action);
            final boolean isDomainDetailsAvailableInManagedDomain = ((Hashtable<K, Boolean>)existingDomainProps).get("IS_DOMAIN_EXISTING_IN_MANAGEDDOMAIN");
            final Properties props = new Properties();
            ((Hashtable<String, Long>)props).put("CUSTOMER_ID", customerID);
            props.setProperty("PASSWORD", password);
            props.setProperty("USER_NAME", userName);
            props.setProperty("IS_AD_DOMAIN", isADDomain);
            props.setProperty("DOMAIN_NETBIOS_NAME", domainName);
            if (dcName != null && !dcName.equals("")) {
                props.setProperty("DC_NAME", dcName);
            }
            if (adDomainName != null && !adDomainName.equals("")) {
                props.setProperty("AD_DOMAIN_NAME", adDomainName);
                props.setProperty("DNS_SUFFIX", adDomainName);
            }
            if (!isADDomain.equals("true") && dnsName != null) {
                props.setProperty("DNS_SUFFIX", dnsName);
            }
            if (domainGUID != null) {
                props.setProperty("GUID", domainGUID);
            }
            final DataObject domainDO = DomainHandler.getInstance().addOrUpdateManagedDomain(props);
            this.somLogger.log(Level.INFO, "Domain details added successfully:: isADDomain - " + isADDomain);
            if (!domainDO.isEmpty() && domainDO.containsTable("ManagedDomain")) {
                final Long resourceID = (Long)domainDO.getFirstValue("ManagedDomain", "RESOURCE_ID");
                if (isADDomain.equals("true")) {
                    DomainHandler.getInstance().addOrUpdateDomainConfiguration(resourceID, portNo, isSSL);
                    this.somLogger.log(Level.INFO, "Updated domain configuration");
                }
                ((Hashtable<String, Long>)addDomainResults).put("RESOURCE_ID", resourceID);
                SoMADUtil.getInstance().updateManagedDomainCredentialStatus(resourceID, verifyRes);
                SoMADUtil.getInstance().hideOrShowDomainPwdChangedMsg(customerID);
            }
            final Properties addDomainProps = new Properties();
            ((Hashtable<String, String>)addDomainProps).put("isADDomain", isADDomain);
            boolean hasCredentials = false;
            if (isDomainDetailsAvailableInManagedDomain) {
                hasCredentials = SoMADUtil.getInstance().hasCredential(domainName);
            }
            ((Hashtable<String, Boolean>)addDomainProps).put("hasCredentials", hasCredentials);
            ((Hashtable<String, Boolean>)addDomainProps).put("isDomainDetailsAvailableInDB", isDomainDetailsAvailableInManagedDomain);
            ApiFactoryProvider.getADImpl().postAddDomainActions(props, addDomainProps);
            final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
            if (isADDomain.equals("true") && (productCode == null || !productCode.equals("DCMSP"))) {
                SoMADUtil.getInstance().addOrUpdateActiveDirectoryInfo(domainName, dcName, "", "", isSSL, portNo);
            }
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.WARNING, "Exception while adding/updating new domain details...", ex);
            final String[] errorMsg = I18NUtil.parseI18NString(ex.getMessage());
            String msg = I18N.getMsg(errorMsg[0], new Object[0]);
            if (errorMsg.length > 1) {
                msg = I18NUtil.transformRemarks(errorMsg[0], errorMsg[1]);
            }
            ((Hashtable<String, String>)addDomainResults).put("ERROR_MESSAGE", msg);
            return addDomainResults;
        }
        return addDomainResults;
    }
    
    public Properties addOrUpdateWorkGroup(final HashMap domainDetails) {
        domainDetails.put("IS_AD_DOMAIN", "false");
        domainDetails.put("NETWORK_TYPE", 1);
        return this.addOrUpdateAD(domainDetails);
    }
    
    public Properties addOrUpdateStandaloneOnPremiseAD(final HashMap domainDetails) {
        domainDetails.put("IS_AD_DOMAIN", "true");
        domainDetails.put("NETWORK_TYPE", 2);
        return this.addOrUpdateAD(domainDetails);
    }
    
    public String addOrUpdateWorkGroupInDMAlso(final HashMap domainDetails) {
        String errorMsg = null;
        final Properties addDomainResult = this.addOrUpdateWorkGroup(domainDetails);
        errorMsg = addDomainResult.getProperty("ERROR_MESSAGE");
        if (errorMsg != null && errorMsg.equals("true")) {
            final Long resourceID = ((Hashtable<K, Long>)addDomainResult).get("RESOURCE_ID");
            final String domainRelErr = this.addOrUpdateDMMDRel(resourceID, domainDetails);
            if (domainRelErr != null) {
                errorMsg = domainRelErr;
            }
        }
        return errorMsg;
    }
    
    public String addOrUpdateStandaloneOnPremiseADInDMAlso(final HashMap domainDetails) {
        String errorMsg = null;
        final Properties addDomainResult = this.addOrUpdateStandaloneOnPremiseAD(domainDetails);
        errorMsg = addDomainResult.getProperty("ERROR_MESSAGE");
        if (errorMsg != null && errorMsg.equals("true")) {
            final Long resourceID = ((Hashtable<K, Long>)addDomainResult).get("RESOURCE_ID");
            final String domainRelErr = this.addOrUpdateDMMDRel(resourceID, domainDetails);
            if (domainRelErr != null) {
                errorMsg = domainRelErr;
            }
        }
        return errorMsg;
    }
    
    public String addOrUpdateDMMDRel(final Long resourceID, final HashMap domainDetails) {
        final String errorMsg = ApiFactoryProvider.getIdPsAPI().addOrUpdateDMMDrel(resourceID, domainDetails);
        return errorMsg;
    }
}
