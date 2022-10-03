package com.me.devicemanagement.framework.core;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.admin.ADdomainHandler;
import com.me.devicemanagement.framework.server.admin.CredentialManager;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.admin.DomainHandler;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.Map;
import java.util.logging.Logger;

public class AddDomainUtil
{
    Logger logger;
    
    public AddDomainUtil() {
        this.logger = Logger.getLogger("SoMLogger");
    }
    
    public String addOrUpdateDomainDetails(final Map inputDomainDetails) {
        final HashMap domainDetails = this.prepareDomainDetails(inputDomainDetails);
        String responseText = this.processRequest(domainDetails);
        try {
            if (responseText == null) {
                responseText = I18N.getMsg("desktopcentral.common.aad.generic_error", new Object[0]);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occured while setting error message for add/update domain... ", ex);
        }
        return responseText;
    }
    
    public int deleteDomainDetails(Long domainResourceID, final String domainName, final Long customerID) throws DataAccessException, SyMException {
        int status = 5;
        if (!SyMUtil.isStringValid(domainName)) {
            return status;
        }
        ApiFactoryProvider.getIdPsAPI().handleDomainDeletionInDM(domainName, customerID);
        if (domainResourceID == null) {
            if (customerID != null) {
                final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
                final Criteria resourceTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)5, 0);
                final Criteria domainNameCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false);
                final DataObject dobj = SyMUtil.getPersistence().get("Resource", customerCri.and(resourceTypeCri.and(domainNameCri)));
                final Row row = dobj.getRow("Resource");
                domainResourceID = (Long)row.get("RESOURCE_ID");
            }
            else {
                final Properties domainProperties = SoMADUtil.getInstance().getManagedDomainInfo(domainName);
                if (domainProperties != null && domainProperties.containsKey("RESOURCE_ID")) {
                    domainResourceID = ((Hashtable<K, Long>)domainProperties).get("RESOURCE_ID");
                }
            }
        }
        if (domainResourceID != null) {
            status = DomainHandler.getInstance().deleteDomainDetails(domainName, domainResourceID);
        }
        return status;
    }
    
    @Deprecated
    public String getPasswordForCredentialID(final Long credentialID) {
        String password = null;
        try {
            final DataObject credDO = new CredentialManager().getCredentialDO(new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)credentialID, 0));
            password = ApiFactoryProvider.getCryptoAPI().decrypt((String)credDO.getFirstValue("Credential", "CRD_PASSWORD"), (Integer)credDO.getFirstValue("Credential", "CRD_ENC_TYPE"));
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.WARNING, "Exception in getting credential id for Edit Domain action =>", (Throwable)ex);
        }
        return password;
    }
    
    public String getPasswordForCredentialID(final Long credentialID, final String credentialUUID) {
        String password = null;
        try {
            Criteria credCriteria = new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)credentialID, 0);
            credCriteria = credCriteria.and(new Criteria(Column.getColumn("Credential", "CREDENTIAL_UUID"), (Object)credentialUUID, 0));
            final DataObject credDO = new CredentialManager().getCredentialDO(credCriteria);
            password = ApiFactoryProvider.getCryptoAPI().decrypt((String)credDO.getFirstValue("Credential", "CRD_PASSWORD"), (Integer)credDO.getFirstValue("Credential", "CRD_ENC_TYPE"));
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.WARNING, "Exception in getting credential id for Edit Domain action =>", (Throwable)ex);
        }
        return password;
    }
    
    protected String processRequest(final HashMap domainDetails) {
        final int networkType = domainDetails.get("NETWORK_TYPE");
        if (networkType == 1) {
            return new ADdomainHandler().addOrUpdateWorkGroupInDMAlso(domainDetails);
        }
        if (networkType == 2) {
            return new ADdomainHandler().addOrUpdateStandaloneOnPremiseADInDMAlso(domainDetails);
        }
        return DomainHandler.getInstance().getErrorMessageForErrorID(80006);
    }
    
    protected HashMap prepareDomainDetails(final Map inputDomainDetails) {
        final boolean isADDomain = inputDomainDetails.get("isADDomain");
        Integer networkType = null;
        try {
            networkType = Integer.valueOf(inputDomainDetails.get("networkType"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Trouble in retrieving networkType. method called from a non-mdm page probably.");
        }
        if (networkType == null) {
            if (isADDomain) {
                networkType = 2;
            }
            else {
                networkType = 1;
            }
        }
        final HashMap domainDetails = new HashMap();
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final String action = this.trim(inputDomainDetails.get("actionToPerform"));
        final String previousDomainName = this.trim(inputDomainDetails.get("previousDomainName"));
        final String adDomainName = this.trim(inputDomainDetails.get("adDomainName"));
        domainDetails.put("PREVDOMNAME", previousDomainName);
        domainDetails.put("ACTION", action);
        domainDetails.put("AD_DOMAIN_NAME", adDomainName);
        domainDetails.put("NETWORK_TYPE", networkType);
        domainDetails.put("CUSTOMER_ID", customerID);
        switch (networkType) {
            case 1: {
                domainDetails.putAll(this.prepareOnPremADDetails(inputDomainDetails));
            }
            case 2: {
                domainDetails.putAll(this.prepareOnPremADDetails(inputDomainDetails));
                break;
            }
        }
        this.logger.log(Level.INFO, "Add/Update Constructed Domain Details" + domainDetails);
        String password = inputDomainDetails.get("password");
        if (password == null) {
            final Long credentialID = (inputDomainDetails.get("credentialID") != null) ? inputDomainDetails.get("credentialID") : null;
            final String credentialUUID = (inputDomainDetails.get("credentialUUID") != null) ? inputDomainDetails.get("credentialUUID") : null;
            password = this.getPasswordForCredentialID(credentialID, credentialUUID);
        }
        if (password != null) {
            domainDetails.put("PASSWORD", password);
        }
        String aadPassword = inputDomainDetails.get("aadPassword");
        if (aadPassword == null) {
            final Long adcredentialID = (inputDomainDetails.get("aadCredentialID") != null) ? inputDomainDetails.get("aadCredentialID") : null;
            aadPassword = this.getPasswordForCredentialID(adcredentialID);
        }
        if (aadPassword != null) {
            domainDetails.put("AAD_PASSWORD", aadPassword);
        }
        return domainDetails;
    }
    
    private HashMap prepareOnPremADDetails(final Map inputDomainDetails) {
        final String dcName = this.trim(inputDomainDetails.get("dcName"));
        final String userName = this.trim(inputDomainDetails.get("username"));
        final String dnsName = this.trim(inputDomainDetails.get("dnsSuffix"));
        final String domainName = this.trim(inputDomainDetails.get("domainName"));
        final Boolean isSSL = inputDomainDetails.get("useSSL") != null && inputDomainDetails.get("useSSL");
        final HashMap domainDetails = new HashMap();
        domainDetails.put("DC_NAME", dcName);
        domainDetails.put("DNS_NAME", dnsName);
        domainDetails.put("USERNAME", userName);
        domainDetails.put("DOMAINNAME", domainName);
        domainDetails.put("USE_SSL", isSSL);
        if (isSSL) {
            final String portNo = this.trim(inputDomainDetails.get("portNumber"));
            domainDetails.put("PORT_NO", portNo);
        }
        return domainDetails;
    }
    
    private String trim(final String str) {
        if (str != null) {
            return str.trim();
        }
        return null;
    }
}
