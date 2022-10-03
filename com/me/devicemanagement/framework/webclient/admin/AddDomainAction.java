package com.me.devicemanagement.framework.webclient.admin;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.core.AddDomainUtil;
import com.me.devicemanagement.framework.server.admin.DomainHandler;
import com.me.devicemanagement.framework.server.admin.ADdomainHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.admin.CredentialManager;
import java.util.Map;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class AddDomainAction
{
    Logger logger;
    
    public AddDomainAction() {
        this.logger = Logger.getLogger("SoMLogger");
    }
    
    private String trim(final String str) {
        if (str != null) {
            return str.trim();
        }
        return null;
    }
    
    private HashMap prepareOnPremADDetails(final HttpServletRequest request) {
        final String dcName = this.trim(request.getParameter("DC_NAME"));
        final String userName = this.trim(request.getParameter("USER_NAME"));
        final String dnsName = this.trim(request.getParameter("DNS_SUFFIX"));
        final String domainName = this.trim(request.getParameter("DOMAIN_NAME"));
        final String isSSLStr = request.getParameter("USE_SSL");
        Boolean isSSL = false;
        if (isSSLStr != null && !isSSLStr.equalsIgnoreCase("")) {
            isSSL = this.trim(isSSLStr).equalsIgnoreCase("true");
        }
        final HashMap domainDetails = new HashMap();
        if (isSSL) {
            final String portNo = this.trim(request.getParameter("PORT_NO"));
            domainDetails.put("PORT_NO", portNo);
        }
        domainDetails.put("DC_NAME", dcName);
        domainDetails.put("DNS_NAME", dnsName);
        domainDetails.put("USERNAME", userName);
        domainDetails.put("DOMAINNAME", domainName);
        domainDetails.put("USE_SSL", isSSL);
        return domainDetails;
    }
    
    protected HashMap prepareDomainRequestDetails(final HttpServletRequest request) {
        final String isADDomain = request.getParameter("IS_AD_DOMAIN");
        Integer networkType = null;
        try {
            networkType = Integer.valueOf(request.getParameter("NETWORK_TYPE"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "trouble in retrieving networkType. method called from a non-mdm page probably.");
        }
        if (networkType == null) {
            if (isADDomain != null && isADDomain.equalsIgnoreCase("true")) {
                networkType = 2;
            }
            else {
                networkType = 1;
            }
        }
        final HashMap domainDetails = new HashMap();
        final String action = request.getParameter("actionToPerform");
        final String prevDomName = this.trim(request.getParameter("prevDomName"));
        final String adDomainName = this.trim(request.getParameter("AD_DOMAIN_NAME"));
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        domainDetails.put("ACTION", action);
        domainDetails.put("CUSTOMER_ID", customerID);
        domainDetails.put("PREVDOMNAME", prevDomName);
        domainDetails.put("NETWORK_TYPE", networkType);
        domainDetails.put("AD_DOMAIN_NAME", adDomainName);
        switch (networkType) {
            case 1: {
                domainDetails.putAll(this.prepareOnPremADDetails(request));
            }
            case 2: {
                domainDetails.putAll(this.prepareOnPremADDetails(request));
                break;
            }
        }
        this.logger.log(Level.INFO, domainDetails.toString());
        String password = request.getParameter("PASSWORD");
        if (password == null) {
            final Long credentialID = (request.getParameter("CREDENTIAL_ID") != null) ? Long.valueOf(Long.parseLong(request.getParameter("CREDENTIAL_ID"))) : null;
            password = this.getPasswordForCredentialID(credentialID);
        }
        String aadPassword = request.getParameter("AAD_PASSWORD");
        if (aadPassword == null) {
            final Long adcredentialID = (request.getParameter("AAD_CREDENTIAL_ID") != null) ? Long.valueOf(Long.parseLong(request.getParameter("AAD_CREDENTIAL_ID"))) : null;
            aadPassword = this.getPasswordForCredentialID(adcredentialID);
        }
        if (password != null) {
            domainDetails.put("PASSWORD", password);
        }
        if (aadPassword != null) {
            domainDetails.put("AAD_PASSWORD", aadPassword);
        }
        return domainDetails;
    }
    
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
    
    public int deleteDomainDetails(final Long domainResourceID, final String domainName, final Long customerID) throws DataAccessException, SyMException {
        return new AddDomainUtil().deleteDomainDetails(domainResourceID, domainName, customerID);
    }
}
