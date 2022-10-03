package com.me.idps.core.crud;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.List;

public class DMDomainDataHandler
{
    private List networkClientIDList;
    private static DMDomainDataHandler dmDomainDataHandler;
    public static Logger logger;
    
    public static DMDomainDataHandler getInstance() {
        if (DMDomainDataHandler.dmDomainDataHandler == null) {
            DMDomainDataHandler.dmDomainDataHandler = new DMDomainDataHandler();
            DMDomainDataHandler.dmDomainDataHandler.networkClientIDList = null;
        }
        return DMDomainDataHandler.dmDomainDataHandler;
    }
    
    public Properties getDomainProp(final String domainName, final Long customerID, final List<Integer> clientIDs) {
        try {
            final SelectQuery domainQuery = DomainDataProvider.getDMManagedDomainQuery(customerID, domainName, null, clientIDs, true);
            final Properties domainProps = getInstance().getDomain(domainQuery);
            return domainProps;
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Properties getDomainProps(final String domainName, final Long customerID, final Integer clientID) {
        try {
            final SelectQuery domainQuery = DomainDataProvider.getDMManagedDomainQuery(customerID, domainName, null, clientID);
            final Properties domainProps = getInstance().getDomain(domainQuery);
            return domainProps;
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @Deprecated
    public Properties getDomainProps(final String domainName, final Long customerID) {
        try {
            final SelectQuery domainQuery = DomainDataProvider.getDMManagedDomainQuery(customerID, domainName, null, null);
            return this.getDomain(domainQuery);
        }
        catch (final SyMException | DataAccessException ex) {
            DMDomainDataHandler.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Properties getDomain(final SelectQuery domainQuery) throws DataAccessException, SyMException {
        final DataObject dmDobj = SyMUtil.getPersistence().get(domainQuery);
        String password = null;
        final Properties domainProperties = new Properties();
        if (!dmDobj.isEmpty()) {
            final Row domainRow = dmDobj.getFirstRow("DMDomain");
            final Row credentialRow = dmDobj.getFirstRow("Credential");
            final Row dmManagedDomainRow = dmDobj.getFirstRow("DMManagedDomain");
            final int clientID = (int)domainRow.get("CLIENT_ID");
            ((Hashtable<String, Integer>)domainProperties).put("CLIENT_ID", clientID);
            ((Hashtable<String, Object>)domainProperties).put("NAME", domainRow.get("NAME"));
            ((Hashtable<String, Object>)domainProperties).put("DOMAIN_ID", domainRow.get("DOMAIN_ID"));
            ((Hashtable<String, Object>)domainProperties).put("CUSTOMER_ID", domainRow.get("CUSTOMER_ID"));
            ((Hashtable<String, Object>)domainProperties).put("CRD_USERNAME", credentialRow.get("CRD_USERNAME"));
            ((Hashtable<String, Object>)domainProperties).put("CREDENTIAL_ID", credentialRow.get("CREDENTIAL_ID"));
            ((Hashtable<String, Object>)domainProperties).put("DC_NAME", dmManagedDomainRow.get("DC_NAME"));
            ((Hashtable<String, Object>)domainProperties).put("AD_DOMAIN_NAME", dmManagedDomainRow.get("AD_DOMAIN_NAME"));
            password = ApiFactoryProvider.getCryptoAPI().decrypt((String)credentialRow.get("CRD_PASSWORD"), (Integer)credentialRow.get("CRD_ENC_TYPE"));
            if (clientID == 2 || clientID == 1) {
                final String dmDomainName = (String)domainRow.get("NAME");
                final Properties domainProps = SoMADUtil.getInstance().getManagedDomainInfo(dmDomainName);
                if (domainProps != null) {
                    final Long resourceID = ((Hashtable<K, Long>)domainProps).get("RESOURCE_ID");
                    ((Hashtable<String, Long>)domainProperties).put("RESOURCE_ID", resourceID);
                    final boolean useSSL = ((Hashtable<K, Boolean>)domainProps).get("USE_SSL");
                    final int portNo = ((Hashtable<K, Integer>)domainProps).get("PORT_NO");
                    ((Hashtable<String, Boolean>)domainProperties).put("USE_SSL", useSSL);
                    ((Hashtable<String, Integer>)domainProperties).put("PORT_NO", portNo);
                }
            }
        }
        else {
            ((Hashtable<String, Integer>)domainProperties).put("CLIENT_ID", -1);
        }
        DMDomainDataHandler.logger.log(Level.FINEST, domainProperties.toString());
        if (password != null) {
            ((Hashtable<String, String>)domainProperties).put("CRD_PASSWORD", password);
        }
        return domainProperties;
    }
    
    public List getDomains(final SelectQuery domainListQuery) throws DataAccessException {
        final ArrayList domainList = new ArrayList();
        final DataObject dObj = SyMUtil.getPersistence().get(domainListQuery);
        final Iterator iterator = dObj.getRows("DMManagedDomain");
        while (iterator.hasNext()) {
            final Row dmManagedDomainRow = iterator.next();
            final Long dmManagedDomainID = (Long)dmManagedDomainRow.get("DOMAIN_ID");
            final Row dmDomainRow = dObj.getRow("DMDomain", new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmManagedDomainID, 0));
            final Row dmManagedDomainCredentialRelRow = dObj.getRow("DMManagedDomainCredentialRel", new Criteria(Column.getColumn("DMManagedDomainCredentialRel", "DOMAIN_ID"), (Object)dmManagedDomainID, 0));
            final Long credentialID = (Long)dmManagedDomainCredentialRelRow.get("CREDENTIAL_ID");
            final Row credentialRow = dObj.getRow("Credential", new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)credentialID, 0));
            final Properties dmDomainProperties = new Properties();
            ((Hashtable<String, Object>)dmDomainProperties).put("NAME", dmDomainRow.get("NAME"));
            ((Hashtable<String, Object>)dmDomainProperties).put("DOMAIN_ID", dmDomainRow.get("DOMAIN_ID"));
            ((Hashtable<String, Object>)dmDomainProperties).put("CLIENT_ID", dmDomainRow.get("CLIENT_ID"));
            ((Hashtable<String, Object>)dmDomainProperties).put("CUSTOMER_ID", dmDomainRow.get("CUSTOMER_ID"));
            ((Hashtable<String, Object>)dmDomainProperties).put("CRD_USERNAME", credentialRow.get("CRD_USERNAME"));
            ((Hashtable<String, Object>)dmDomainProperties).put("DC_NAME", dmManagedDomainRow.get("DC_NAME"));
            ((Hashtable<String, String>)dmDomainProperties).put("AD_DOMAIN_NAME", ((String)dmManagedDomainRow.get("AD_DOMAIN_NAME")).toUpperCase());
            ((Hashtable<String, Object>)dmDomainProperties).put("VALIDATION_STATUS", dmManagedDomainCredentialRelRow.get("VALIDATION_STATUS"));
            if (dObj.containsTable("DMDomainSyncDetails")) {
                final Row dmDomainSyncDetailsRow = dObj.getRow("DMDomainSyncDetails", new Criteria(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"), (Object)dmManagedDomainID, 0));
                if (dmDomainSyncDetailsRow != null) {
                    ((Hashtable<String, Object>)dmDomainProperties).put("FETCH_STATUS", dmDomainSyncDetailsRow.get("FETCH_STATUS"));
                }
            }
            domainList.add(dmDomainProperties);
        }
        return domainList;
    }
    
    public Properties getAdDomainProps(final String adDomainName, final Long customerID) {
        return this.getAdDomainProps(adDomainName, customerID, null);
    }
    
    public Properties getAdDomainProps(final String adDomainName, final Long customerID, final List<Integer> clientIDs) {
        try {
            final SelectQuery domainQuery = DomainDataProvider.getDMManagedDomainQuery(customerID, null, adDomainName, clientIDs, true);
            return this.getDomain(domainQuery);
        }
        catch (final SyMException | DataAccessException ex) {
            DMDomainDataHandler.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Properties getDomainById(final Long domainId) {
        try {
            final Criteria domainIdCriteria = new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)domainId, 0);
            final SelectQuery domainQuery = DomainDataProvider.getDMManagedDomainQuery(domainIdCriteria);
            return this.getDomain(domainQuery);
        }
        catch (final SyMException | DataAccessException ex) {
            DMDomainDataHandler.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private List getNetworkClientIDList() throws DataAccessException {
        if (this.networkClientIDList == null || (this.networkClientIDList != null && this.networkClientIDList.isEmpty())) {
            final DataObject dobj = SyMUtil.getPersistenceLite().get("NetworkClient", new Criteria(Column.getColumn("NetworkClient", "CLIENT_ID"), (Object)1, 1));
            this.networkClientIDList = DBUtil.getColumnValuesAsList(dobj.getRows("NetworkClient"), "CLIENT_ID");
        }
        return this.networkClientIDList;
    }
    
    public void addDirectoryClientType(final String directoryTypeName, final int clientID) throws DataAccessException {
        final Row row = new Row("NetworkClient");
        row.set("NAME", (Object)directoryTypeName);
        row.set("CLIENT_ID", (Object)clientID);
        final DataObject dobj = SyMUtil.getPersistenceLite().constructDataObject();
        dobj.addRow(row);
        SyMUtil.getPersistenceLite().add(dobj);
        final List networkClientIDList = this.getNetworkClientIDList();
        if (!networkClientIDList.contains(clientID)) {
            networkClientIDList.add(clientID);
        }
    }
    
    public List getAllDMManagedProps(final Long customerID) {
        List domainList = new ArrayList();
        try {
            final List networkClientIDList = this.getNetworkClientIDList();
            final SelectQuery domainListQuery = DomainDataProvider.getDMManagedDomainQuery(customerID, null, null, networkClientIDList, true);
            domainList = this.getDomains(domainListQuery);
        }
        catch (final DataAccessException e) {
            DMDomainDataHandler.logger.log(Level.SEVERE, "Caught exception getAllDMManagedProps ", (Throwable)e);
        }
        return domainList;
    }
    
    public boolean isADManagedDomain(final String domainName, final Long customerID) {
        try {
            final Properties domainProps = getInstance().getDomainProps(domainName, customerID);
            if (domainProps != null) {
                final int networkType = ((Hashtable<K, Integer>)domainProps).get("CLIENT_ID");
                if (networkType != -1 && networkType != 1) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            DMDomainDataHandler.logger.log(Level.SEVERE, "Caught exception while checking if AD Domain: ", ex);
        }
        return false;
    }
    
    boolean addDMMDRel(final Long dmDomainID, final Long resourceID, final Integer clientID) {
        try {
            final DataObject dObj = SyMUtil.getPersistence().get("DMMDRel", new Criteria(Column.getColumn("DMMDRel", "DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DMMDRel", "RESOURCE_ID"), (Object)resourceID, 0)));
            if (dObj.isEmpty()) {
                final Row row = new Row("DMMDRel");
                row.set("CLIENT_ID", (Object)clientID);
                row.set("DOMAIN_ID", (Object)dmDomainID);
                row.set("RESOURCE_ID", (Object)resourceID);
                dObj.addRow(row);
                SyMUtil.getPersistence().add(dObj);
            }
            return true;
        }
        catch (final Exception ex) {
            DMDomainDataHandler.logger.log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    int deleteDomainCredentials(final List domainCredentials) {
        if (domainCredentials == null) {
            return 2;
        }
        final Criteria queryCriteria = new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)domainCredentials.toArray(), 8);
        try {
            final DataObject credDO = SyMUtil.getPersistence().get("Credential", queryCriteria);
            if (!credDO.isEmpty()) {
                DMDomainDataHandler.logger.log(Level.SEVERE, "Deleting the credentials of Domain DO " + credDO);
                credDO.deleteRows("Credential", (Criteria)null);
                SyMUtil.getPersistence().update(credDO);
            }
            return 1;
        }
        catch (final Exception ex) {
            DMDomainDataHandler.logger.log(Level.SEVERE, "Exception in domainsDeleted SoM Mgr Listener", ex);
            return 2;
        }
    }
    
    void updateManagedDomainCredentialStatus(final Long dmDomainID, final boolean verifyRes) {
        try {
            Criteria criteria = new Criteria(Column.getColumn("DMManagedDomainCredentialRel", "DOMAIN_ID"), (Object)dmDomainID, 0);
            criteria = criteria.and(Column.getColumn("DMManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0);
            final DataObject resultDO = SyMUtil.getPersistence().get("DMManagedDomainCredentialRel", criteria);
            if (!resultDO.isEmpty()) {
                DMDomainDataHandler.logger.log(Level.INFO, "Going to update DMMANAGEDDOMAINCREDENTIALREL row for dmDomainID :" + dmDomainID);
                final Row row = resultDO.getFirstRow("DMManagedDomainCredentialRel");
                row.set("VALIDATION_STATUS", (Object)verifyRes);
                resultDO.updateRow(row);
                DMDomainDataHandler.logger.log(Level.INFO, "Going to update row :" + row);
                SyMUtil.getPersistence().update(resultDO);
                DMDomainDataHandler.logger.log(Level.INFO, "updated row :" + row);
            }
            else {
                DMDomainDataHandler.logger.log(Level.INFO, "Dummy Credential ? DMManagedDomainCredentialRel row not found for dmDomain id: " + dmDomainID);
            }
        }
        catch (final Exception ex) {
            DMDomainDataHandler.logger.log(Level.SEVERE, "Caught exception while updating DomainCredentialStatus entry for dmDomain id :" + dmDomainID, ex);
        }
    }
    
    public List<String> getPasswordExpiredDomainList() throws DataAccessException, SyMException {
        final List networkClientIDList = this.getNetworkClientIDList();
        final SelectQuery domainListQuery = DomainDataProvider.getDMManagedDomainQuery(null, null, null, networkClientIDList, true);
        Criteria queryCri = domainListQuery.getCriteria();
        final Criteria validationStatusCri = new Criteria(Column.getColumn("DMManagedDomainCredentialRel", "VALIDATION_STATUS"), (Object)false, 0);
        if (queryCri != null) {
            queryCri = queryCri.and(validationStatusCri);
        }
        else {
            queryCri = validationStatusCri;
        }
        domainListQuery.setCriteria(queryCri);
        final List domainList = this.getDomains(domainListQuery);
        return domainList;
    }
    
    public void hideOrShowDomainPwdChangedMsg() {
        try {
            final String msg = "IDP_PASSWORD_CHANGED";
            final List domainChanged = this.getPasswordExpiredDomainList();
            if (domainChanged.size() > 0) {
                final MessageProvider msgProvider = MessageProvider.getInstance();
                msgProvider.unhideMessage(msg);
            }
            else {
                MessageProvider.getInstance().hideMessage(msg);
            }
        }
        catch (final Exception ex) {
            DMDomainDataHandler.logger.log(Level.SEVERE, "Caught exception while validating AD Domains :", ex);
        }
    }
    
    static {
        DMDomainDataHandler.dmDomainDataHandler = null;
        DMDomainDataHandler.logger = Logger.getLogger("SoMLogger");
    }
}
