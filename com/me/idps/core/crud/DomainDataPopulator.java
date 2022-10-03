package com.me.idps.core.crud;

import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.admin.CredentialManager;
import java.util.Map;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.Properties;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DomainDataPopulator
{
    protected static final Integer DOMAIN_TABLE_LOCK;
    protected static Logger logger;
    private static String sourceClass;
    
    protected DomainDataPopulator() {
    }
    
    public static DomainDataPopulator getInstance() {
        return DomainDataPopulatorHelper.INSTANCE;
    }
    
    private DataObject addOrUpdateDomain(final String domainName, final Integer networkClientID, final Long customerId) throws DataAccessException {
        final String sourceMethod = "addOrUpdateDomain";
        final Row domainRow = new Row("DMDomain");
        domainRow.set("NAME", (Object)domainName);
        domainRow.set("CLIENT_ID", (Object)networkClientID);
        domainRow.set("CUSTOMER_ID", (Object)customerId);
        SyMLogger.debug(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Entering synchronized block for domain row: {0}", new Object[] { domainRow });
        synchronized (DomainDataPopulator.DOMAIN_TABLE_LOCK) {
            try {
                final DataObject domainDO = DomainDataProvider.getDomainDOFromDB(domainRow);
                if (domainDO.isEmpty()) {
                    SyMLogger.debug(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Domain details does not exist in DB. Going to add: {0}", new Object[] { domainRow });
                    domainDO.addRow(domainRow);
                    SyMUtil.getPersistence().add(domainDO);
                    return domainDO;
                }
                SyMLogger.debug(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Domain details already exist in DB: {0}", new Object[] { domainRow });
                return domainDO;
            }
            catch (final DataAccessException ex) {
                SyMLogger.error(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Caught exception while populating domain details in DB: name: " + domainName + "\t network client ID: " + networkClientID + "\t Customer ID: " + customerId, (Throwable)ex);
                throw ex;
            }
            finally {
                SyMLogger.debug(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Leaving synchronized block for domain row: {0}", new Object[] { domainRow });
            }
        }
    }
    
    private Row populateDomainRow(final String domainName, final Integer networkClientID, final Long customerId) throws DataAccessException {
        final DataObject domainDO = this.addOrUpdateDomain(domainName, networkClientID, customerId);
        return domainDO.getRow("DMDomain");
    }
    
    public void deleteDomain(final HashMap deleteDomainDetails) throws DataAccessException {
        final String sourceMethod = "deleteDomain";
        final Long customerID = deleteDomainDetails.get("CUSTOMER_ID");
        final String domainname = deleteDomainDetails.get("NAME");
        final String adDomainName = deleteDomainDetails.get("AD_DOMAIN_NAME");
        final Integer clientID = deleteDomainDetails.get("CLIENT_ID");
        final SelectQuery domainQuery = DomainDataProvider.getDMManagedDomainQuery(customerID, domainname, adDomainName, new ArrayList<Integer>(Arrays.asList(clientID)), false);
        SyMLogger.debug(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Entering synchronized block for domain query: {0}", new Object[] { domainQuery });
        synchronized (DomainDataPopulator.DOMAIN_TABLE_LOCK) {
            try {
                final DataObject dObj = SyMUtil.getPersistence().get(domainQuery);
                if (!dObj.isEmpty()) {
                    final Iterator it = dObj.getRows("Credential");
                    final List<Long> domainCredentials = new ArrayList<Long>();
                    while (it.hasNext()) {
                        final Row managedDomainCredentialRelRow = it.next();
                        final Long credentialID = (Long)managedDomainCredentialRelRow.get("CREDENTIAL_ID");
                        domainCredentials.add(credentialID);
                    }
                    final Row domainRow = dObj.getFirstRow("DMDomain");
                    final Row dmManagedDomainRow = dObj.getFirstRow("DMManagedDomain");
                    final Properties props = new Properties();
                    ((Hashtable<String, Object>)props).put("NAME", domainRow.get("NAME"));
                    ((Hashtable<String, Object>)props).put("CLIENT_ID", domainRow.get("CLIENT_ID"));
                    ((Hashtable<String, Object>)props).put("CUSTOMER_ID", domainRow.get("CUSTOMER_ID"));
                    ((Hashtable<String, Object>)props).put("DC_NAME", dmManagedDomainRow.get("DC_NAME"));
                    ((Hashtable<String, Object>)props).put("DOMAIN_ID", dmManagedDomainRow.get("DOMAIN_ID"));
                    ((Hashtable<String, Object>)props).put("AD_DOMAIN_NAME", dmManagedDomainRow.get("AD_DOMAIN_NAME"));
                    final DomainEvent[] domainEventArr = { null };
                    final DomainEvent domainEvent = new DomainEvent((Long)dmManagedDomainRow.get("DOMAIN_ID"), customerID, clientID);
                    domainEvent.domainProperties = props;
                    domainEventArr[0] = domainEvent;
                    DMDomainHandler.getInstance().invokeDomainListeners(domainEventArr, 304);
                    dObj.deleteRow(domainRow);
                    SyMUtil.getPersistence().update(dObj);
                    DMDomainDataHandler.getInstance().deleteDomainCredentials(domainCredentials);
                    DMDomainHandler.getInstance().invokeDomainListeners(domainEventArr, 302);
                }
            }
            catch (final DataAccessException ex) {
                SyMLogger.error(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Caught exception while deleting domain details in DB: name: " + adDomainName + "\t network client ID: " + clientID + "\t Customer ID: " + customerID, (Throwable)ex);
                throw ex;
            }
            finally {
                SyMLogger.debug(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Leaving synchronized block for domain query: {0}", new Object[] { domainQuery });
            }
        }
    }
    
    private Row constructDMManagedDomainRow(final Row mdRow, final Properties props) {
        final String isADDomain = props.getProperty("IS_AD_DOMAIN");
        if (isADDomain != null) {
            mdRow.set("IS_AD_DOMAIN", (Object)Boolean.valueOf(isADDomain));
        }
        final String adDomainName = props.getProperty("AD_DOMAIN_NAME");
        if (adDomainName != null) {
            mdRow.set("AD_DOMAIN_NAME", (Object)adDomainName);
        }
        final String dcName = props.getProperty("DC_NAME");
        if (dcName != null) {
            mdRow.set("DC_NAME", (Object)dcName);
        }
        final String dnsName = props.getProperty("DNS_SUFFIX");
        if (dnsName != null) {
            mdRow.set("DNS_SUFFIX", (Object)dnsName);
        }
        return mdRow;
    }
    
    public DataObject addOrUpdateDMManagedDomain(final Properties props) throws SyMException, DataAccessException {
        final String sourceMethod = "addOrUpdateDMManagedDomain";
        final String domainName = props.getProperty("NAME");
        final Long customerID = ((Hashtable<K, Long>)props).get("CUSTOMER_ID");
        final Integer clientID = ((Hashtable<K, Integer>)props).get("CLIENT_ID");
        if (domainName == null || customerID == null || clientID == null) {
            throw new SyMException(1002, "Cannot add or update DM managed domain. required property is null." + props, (Throwable)null);
        }
        final Row domainRow = this.populateDomainRow(domainName, clientID, customerID);
        if (domainRow != null) {
            final Long domainID = (Long)domainRow.get("DOMAIN_ID");
            Row mdRow = new Row("DMManagedDomain");
            mdRow.set("DOMAIN_ID", (Object)domainID);
            DataObject dmManagedDO = SyMUtil.getPersistence().get("DMManagedDomain", mdRow);
            if (!dmManagedDO.isEmpty()) {
                mdRow = dmManagedDO.getFirstRow("DMManagedDomain");
            }
            mdRow = this.constructDMManagedDomainRow(mdRow, props);
            final DomainEvent[] domainEventArr = { null };
            final DomainEvent domainEvent = new DomainEvent(domainID, customerID, clientID);
            domainEvent.domainProperties = props;
            domainEventArr[0] = domainEvent;
            final String userName = props.getProperty("CRD_USERNAME");
            final String userPwd = props.getProperty("CRD_PASSWORD");
            if (dmManagedDO.isEmpty()) {
                if (userName == null || userName.equals("")) {
                    props.setProperty("CRD_USERNAME", "--");
                }
                if (userPwd == null || userPwd.equals("")) {
                    props.setProperty("CRD_PASSWORD", "--");
                }
            }
            int eventID;
            if (dmManagedDO.isEmpty()) {
                dmManagedDO.addRow(mdRow);
                dmManagedDO = SyMUtil.getPersistence().add(dmManagedDO);
                this.logDomainInfo(sourceMethod, props, "ADDED");
                eventID = 301;
            }
            else {
                dmManagedDO.updateRow(mdRow);
                dmManagedDO = SyMUtil.getPersistence().update(dmManagedDO);
                this.logDomainInfo(sourceMethod, props, "MODIFIED");
                eventID = 303;
            }
            if (userName != null || userPwd != null) {
                this.addOrUpdateDMDomainCredential(props, domainID);
            }
            DMDomainHandler.getInstance().invokeDomainListeners(domainEventArr, eventID);
            return dmManagedDO;
        }
        return null;
    }
    
    private void addOrUpdateDMDomainCredential(final Properties props, final Long domainID) throws DataAccessException {
        final DataObject domainCredDO = this.getDomainRootCredentialDO(domainID);
        final Properties credProps = new Properties();
        final String userName = props.getProperty("CRD_USERNAME");
        final String userPwd = props.getProperty("CRD_PASSWORD");
        final Integer clientID = ((Hashtable<K, Integer>)props).get("CLIENT_ID");
        boolean isDummy = false;
        if (userName != null) {
            if (!userName.equals("--")) {
                credProps.setProperty("UserName", userName);
            }
            else {
                isDummy = true;
            }
        }
        if (userPwd != null && !userPwd.equals("--")) {
            credProps.setProperty("PassWord", userPwd);
        }
        if (isDummy) {
            DomainDataPopulator.logger.log(Level.INFO, "Since the username received is a dummy credential of username " + userName + ", Returning from the addorupdateDMdomaincredential block..");
            if (!domainCredDO.isEmpty()) {
                DomainDataPopulator.logger.log(Level.INFO, "Dummy credential -> Deleting the old credential of DO ");
                domainCredDO.deleteRow(domainCredDO.getRow("DMManagedDomainCredentialRel"));
                domainCredDO.deleteRow(domainCredDO.getRow("Credential"));
                SyMUtil.getPersistence().update(domainCredDO);
            }
            return;
        }
        final String domainName = props.getProperty("NAME");
        if (clientID == 2 || clientID == 1) {
            credProps.setProperty("CredentialName", domainName + "_root");
        }
        else {
            credProps.setProperty("CredentialName", "dm_" + domainName + "_root");
        }
        final String isADDomain = props.getProperty("IS_AD_DOMAIN");
        boolean isAD = false;
        if (isADDomain != null) {
            isAD = Boolean.valueOf(isADDomain);
        }
        credProps.setProperty("CredentialType", String.valueOf(isAD ? 301 : 302));
        ((Hashtable<String, Object>)credProps).put("CustomerID", ((Hashtable<K, Object>)props).get("CUSTOMER_ID"));
        Long credId = null;
        String loginUserName = "";
        try {
            loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        }
        catch (final Exception e) {
            DomainDataPopulator.logger.log(Level.SEVERE, "Unable to get Login Username");
        }
        final Long customerID = Long.parseLong(String.valueOf(((Hashtable<K, Object>)props).get("CUSTOMER_ID")));
        final Properties logProperties = new Properties();
        logProperties.putAll(credProps);
        logProperties.remove("PassWord");
        if (domainCredDO.isEmpty()) {
            credId = new CredentialManager().addOrUpdateCredential(credProps);
            DomainDataPopulator.logger.log(Level.INFO, "Adding a new credential of name " + props.getProperty("NAME") + " with cred id " + credId);
            if (credId == null) {
                DomainDataPopulator.logger.log(Level.SEVERE, "Not able to add credentials with props " + logProperties);
                return;
            }
            DCEventLogUtil.getInstance().addEvent(4001, loginUserName, (HashMap)null, "dc.admin.credmgr.cred_added", (Object)(domainName + "@@@" + loginUserName), false, customerID);
            final Row domainCredMapRow = new Row("DMManagedDomainCredentialRel");
            domainCredMapRow.set("DOMAIN_ID", (Object)domainID);
            domainCredMapRow.set("CREDENTIAL_ID", (Object)credId);
            final DataObject domainCredMapDO = SyMUtil.getPersistence().get("DMManagedDomainCredentialRel", domainCredMapRow);
            domainCredMapRow.set("IS_ROOT", (Object)1);
            domainCredMapRow.set("VALIDATION_STATUS", (Object)Boolean.TRUE);
            if (domainCredMapDO.isEmpty()) {
                domainCredMapDO.addRow(domainCredMapRow);
                SyMUtil.getPersistence().add(domainCredMapDO);
            }
            else {
                domainCredMapDO.updateRow(domainCredMapRow);
                SyMUtil.getPersistence().update(domainCredMapDO);
            }
        }
        else {
            credId = new CredentialManager().addOrUpdateCredential(credProps);
            DomainDataPopulator.logger.log(Level.INFO, "Updating a credential of name " + props.getProperty("NAME") + " with cred id " + credId);
            if (credId == null) {
                DomainDataPopulator.logger.log(Level.SEVERE, "Not able to add credentials with props " + logProperties);
                return;
            }
            DCEventLogUtil.getInstance().addEvent(4001, loginUserName, (HashMap)null, "dc.admin.credmgr.cred_modified", (Object)domainName, false, customerID);
            final Row domainCredMapRow = domainCredDO.getRow("DMManagedDomainCredentialRel");
            domainCredMapRow.set("CREDENTIAL_ID", (Object)credId);
            domainCredMapRow.set("IS_ROOT", (Object)1);
            domainCredDO.updateRow(domainCredMapRow);
            SyMUtil.getPersistence().update(domainCredDO);
        }
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userID != null) {
                DomainDataPopulator.logger.log(Level.INFO, "Credential of cred id " + credId + " add / updated by login id " + userID);
                new CredentialManager().addOrUpdateUserCredentialRel(credId, userID);
            }
            else {
                DomainDataPopulator.logger.log(Level.SEVERE, "Auth Credential is null, hence login id mapping ignored..");
            }
        }
        catch (final Exception ex) {
            DomainDataPopulator.logger.log(Level.SEVERE, "Exception in add or update user credential rel " + logProperties, ex);
        }
    }
    
    private void logDomainInfo(final String sourceMethod, final Properties props, final String action) {
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "**************" + action + " DOMAIN DETAILS*******");
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Domain Name :" + ((Hashtable<K, Object>)props).get("NAME"));
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Client ID :" + ((Hashtable<K, Object>)props).get("CLIENT_ID"));
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Customer ID :" + ((Hashtable<K, Object>)props).get("CUSTOMER_ID"));
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Is AD Domain :" + ((Hashtable<K, Object>)props).get("IS_AD_DOMAIN"));
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "Domain Controller Name :" + ((Hashtable<K, Object>)props).get("DC_NAME"));
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "AD Domain Name :" + ((Hashtable<K, Object>)props).get("AD_DOMAIN_NAME"));
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "DNS Suffix :" + ((Hashtable<K, Object>)props).get("DNS_SUFFIX"));
        SyMLogger.info(DomainDataPopulator.logger, DomainDataPopulator.sourceClass, sourceMethod, "**************" + action + " DOMAIN DETAILS*******");
    }
    
    private DataObject getDomainRootCredentialDO(final Long domainId) throws DataAccessException {
        final SelectQuery domainCredQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Credential"));
        domainCredQuery.addSelectColumn(new Column("Credential", "*"));
        domainCredQuery.addSelectColumn(new Column("DMManagedDomainCredentialRel", "*"));
        domainCredQuery.addJoin(new Join("Credential", "DMManagedDomainCredentialRel", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 2));
        final Criteria domainCri = new Criteria(Column.getColumn("DMManagedDomainCredentialRel", "DOMAIN_ID"), (Object)domainId, 0);
        domainCredQuery.setCriteria(domainCri.and(new Criteria(Column.getColumn("DMManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0)));
        final DataObject credDO = SyMUtil.getPersistence().get(domainCredQuery);
        return credDO;
    }
    
    Long addOrUpdateDomainViewRecord(final HashMap domainDetails) {
        try {
            String dcName = domainDetails.get("DC_NAME");
            String dnsName = domainDetails.get("DNS_NAME");
            final String userName = domainDetails.get("USERNAME");
            final String password = domainDetails.get("PASSWORD");
            final String domainName = domainDetails.get("DOMAINNAME");
            final Long customerID = domainDetails.get("CUSTOMER_ID");
            final String isADDomain = domainDetails.get("IS_AD_DOMAIN");
            final Integer networkType = domainDetails.get("NETWORK_TYPE");
            String adDomainName = domainDetails.get("AD_DOMAIN_NAME");
            if (dnsName == null) {
                dnsName = "---";
            }
            if (dcName == null) {
                dcName = "---";
            }
            if (adDomainName == null) {
                adDomainName = domainName;
            }
            final Properties props = new Properties();
            ((Hashtable<String, String>)props).put("NAME", domainName);
            ((Hashtable<String, Integer>)props).put("CLIENT_ID", networkType);
            ((Hashtable<String, Long>)props).put("CUSTOMER_ID", customerID);
            ((Hashtable<String, String>)props).put("DC_NAME", dcName);
            ((Hashtable<String, String>)props).put("CRD_USERNAME", userName);
            ((Hashtable<String, String>)props).put("CRD_PASSWORD", password);
            ((Hashtable<String, String>)props).put("DNS_SUFFIX", dnsName);
            ((Hashtable<String, String>)props).put("IS_AD_DOMAIN", isADDomain);
            ((Hashtable<String, String>)props).put("AD_DOMAIN_NAME", adDomainName);
            final DataObject dObj = getInstance().addOrUpdateDMManagedDomain(props);
            if (dObj != null && dObj.containsTable("DMManagedDomain")) {
                final Long dmDomainID = (Long)dObj.getFirstRow("DMManagedDomain").get("DOMAIN_ID");
                DMDomainDataHandler.getInstance().updateManagedDomainCredentialStatus(dmDomainID, true);
                DMDomainDataHandler.getInstance().hideOrShowDomainPwdChangedMsg();
                return dmDomainID;
            }
        }
        catch (final Exception ex) {
            DomainDataPopulator.logger.log(Level.SEVERE, "Exception in domainsDeleted SoM Mgr Listener", ex);
        }
        return null;
    }
    
    static {
        DOMAIN_TABLE_LOCK = new Integer(1);
        DomainDataPopulator.logger = Logger.getLogger("SoMLogger");
        DomainDataPopulator.sourceClass = "DomainDataPopulator";
    }
    
    private static class DomainDataPopulatorHelper
    {
        private static final DomainDataPopulator INSTANCE;
        
        static {
            INSTANCE = new DomainDataPopulator();
        }
    }
}
