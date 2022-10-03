package com.me.devicemanagement.framework.server.admin;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.winaccess.ADAccessProvider;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.TreeMap;
import com.me.devicemanagement.framework.server.factory.DBHandlerFactoryProvider;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.api.ADGeneralAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.resource.ResourceDataPopulator;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.Map;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;

public class DomainHandler
{
    private static DomainHandler domainHandler;
    private List domainListenerList;
    protected Logger somLogger;
    public static final int DOMAINS_ADDED = 102;
    public static final int DOMAINS_DELETED = 103;
    public static final int DOMAINS_UPDATED = 107;
    public static final int DOMAINS_MANAGED = 112;
    public static final int DOMAINS_NOT_MANAGED = 115;
    private static String sourceClass;
    
    protected DomainHandler() {
        this.domainListenerList = new ArrayList();
        this.somLogger = Logger.getLogger("SoMLogger");
    }
    
    public static synchronized DomainHandler getInstance() {
        if (DomainHandler.domainHandler == null) {
            DomainHandler.domainHandler = new DomainHandler();
        }
        return DomainHandler.domainHandler;
    }
    
    public void addDomainListener(final DomainListener listener) {
        this.domainListenerList.add(listener);
    }
    
    public void removeDomainListener(final DomainListener listener) {
        this.domainListenerList.remove(listener);
    }
    
    public List getDomainListenersList() {
        return this.domainListenerList;
    }
    
    public void invokeDomainListeners(final SoMEvent[] somEventArr, final int operation) {
        if (somEventArr == null) {
            return;
        }
        try {
            for (final SoMEvent soMEvent : somEventArr) {
                Long customerID = soMEvent.customerID;
                final Properties resProps = soMEvent.resourceProperties;
                if (customerID == null) {
                    customerID = ((Hashtable<K, Long>)resProps).get("CUSTOMER_ID");
                }
                final JSONObject qData = new JSONObject();
                qData.put("resource_Id", (Object)soMEvent.resourceID);
                qData.put("customer_Id", (Object)customerID);
                qData.put("resource_props", (Map)resProps);
                final Long postedTime = System.currentTimeMillis();
                final String qFileName = customerID + "-" + soMEvent.resourceID + "-" + postedTime + ".txt";
                final DCQueue queue = DCQueueHandler.getQueue("som-listener-data");
                final DCQueueData queueData = new DCQueueData();
                queueData.postTime = postedTime;
                queueData.queueData = qData.toString();
                queueData.queueDataType = operation;
                queueData.fileName = qFileName;
                queue.addToQueue(queueData);
            }
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.SEVERE, "Exception in invokeDomainListeners ", ex);
        }
    }
    
    public boolean isDomainDetailsAvailableInDB(final String domainName, final Long customerID) throws SyMException {
        boolean available = false;
        Criteria crit = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false);
        crit = crit.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject dataObject = this.getManagedDomainsDO(crit);
        if (!dataObject.isEmpty()) {
            available = true;
        }
        return available;
    }
    
    public DataObject getManagedDomainsDO(final Criteria criteria) throws SyMException {
        return SoMADUtil.getInstance().getManagedDomainsDO(criteria);
    }
    
    public String getDomainName(final String adDomainName) throws DataAccessException, SyMException {
        String domainName = null;
        final Criteria crit = new Criteria(Column.getColumn("ManagedDomain", "AD_DOMAIN_NAME"), (Object)adDomainName, 0, false);
        final DataObject dataObject = this.getManagedDomainsDO(crit);
        if (!dataObject.isEmpty()) {
            domainName = (String)dataObject.getFirstValue("Resource", "NAME");
        }
        return domainName;
    }
    
    public DataObject addOrUpdateManagedDomain(final Properties props) throws SyMException {
        final String sourceMethod = "addOrUpdateManagedDomain";
        DataObject resultDO = null;
        try {
            final String domainName = props.getProperty("DOMAIN_NETBIOS_NAME");
            final Long customerID = ((Hashtable<K, Long>)props).get("CUSTOMER_ID");
            if (domainName == null) {
                throw new SyMException(1002, "Cannot add or update managed domain. domainName is null.", null);
            }
            final Column resTypeCol = Column.getColumn("Resource", "RESOURCE_TYPE");
            Criteria criteria = new Criteria(resTypeCol, (Object)new Integer(5), 0);
            final Column dnameCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
            final Criteria dnameCri = new Criteria(dnameCol, (Object)domainName, 0, false);
            criteria = criteria.and(dnameCri);
            final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            criteria = criteria.and(custCri);
            DataObject resDO = SyMUtil.getPersistence().get("Resource", criteria);
            if (resDO.isEmpty()) {
                final Row resRow = new Row("Resource");
                resRow.set("NAME", ((Hashtable<K, Object>)props).get("DOMAIN_NETBIOS_NAME"));
                resRow.set("DOMAIN_NETBIOS_NAME", ((Hashtable<K, Object>)props).get("DOMAIN_NETBIOS_NAME"));
                resRow.set("RESOURCE_TYPE", (Object)new Integer(5));
                resRow.set("CUSTOMER_ID", ((Hashtable<K, Object>)props).get("CUSTOMER_ID"));
                resDO = ResourceDataPopulator.addOrUpdateResourceRow(resRow);
            }
            final Long domainResId = (Long)resDO.getFirstValue("Resource", "RESOURCE_ID");
            Row mdRow = new Row("ManagedDomain");
            mdRow.set("RESOURCE_ID", (Object)domainResId);
            resultDO = SyMUtil.getPersistence().get("ManagedDomain", mdRow);
            String userName = props.getProperty("USER_NAME");
            String userPwd = props.getProperty("PASSWORD");
            if (userName != null && !userName.equals("") && !userName.equals("--") && userPwd != null && !userPwd.equals("") && !userPwd.equals("--")) {
                props.setProperty("HAS_CREDENTIALS", "true");
            }
            else if (resultDO.isEmpty()) {
                props.setProperty("HAS_CREDENTIALS", "false");
                props.setProperty("USER_NAME", "--");
                props.setProperty("PASSWORD", "--");
            }
            if (!resultDO.isEmpty()) {
                mdRow = resultDO.getFirstRow("ManagedDomain");
            }
            mdRow = this.constructManagedDomainRow(mdRow, props);
            props.setProperty("IS_AD_DOMAIN", String.valueOf(mdRow.get("IS_AD_DOMAIN")));
            final SoMEvent[] somEventArr = { null };
            final SoMEvent somEvent = new SoMEvent(domainResId, customerID);
            somEvent.resourceProperties = props;
            somEventArr[0] = somEvent;
            userName = props.getProperty("USER_NAME");
            userPwd = props.getProperty("PASSWORD");
            final Long currentTime = SyMUtil.getCurrentTimeInMillis();
            if (resultDO.isEmpty()) {
                mdRow.set("DB_UPDATED_TIME", (Object)currentTime);
                resultDO.addRow(mdRow);
                resultDO = SyMUtil.getPersistence().add(resultDO);
                if (userName != null || userPwd != null) {
                    SoMADUtil.getInstance().addOrUpdateDomainCredential(props, domainResId);
                }
                this.logDomainInfo(sourceMethod, props, "ADDED");
                getInstance().invokeDomainListeners(somEventArr, 102);
                final ADGeneralAPI adGenAPI = ApiFactoryProvider.getADImpl();
                if (adGenAPI != null) {
                    adGenAPI.generateMetaData(customerID, domainName);
                }
            }
            else {
                mdRow.set("DB_UPDATED_TIME", (Object)currentTime);
                resultDO.updateRow(mdRow);
                resultDO = SyMUtil.getPersistence().update(resultDO);
                this.logDomainInfo(sourceMethod, props, "MODIFIED");
                if (userName != null || userPwd != null) {
                    SoMADUtil.getInstance().addOrUpdateDomainCredential(props, domainResId);
                }
                getInstance().invokeDomainListeners(somEventArr, 107);
            }
            try {
                final String hasManagedComputers = ((Hashtable<K, String>)props).get("HAS_MANAGED_COMPUTERS");
                if (hasManagedComputers != null) {
                    ApiFactoryProvider.getADImpl().updateHasManagedResourceInHash(domainName, customerID, Boolean.valueOf(hasManagedComputers));
                }
            }
            catch (final Exception ex) {
                final Properties logProperties = new Properties();
                logProperties.putAll(props);
                logProperties.remove("PASSWORD");
                this.somLogger.log(Level.WARNING, "Caught exception while updating the HAS_MANAGED_COMPUTERS attribute in memory hash for domain details: " + logProperties, ex);
            }
        }
        catch (final Exception ex2) {
            this.somLogger.log(Level.SEVERE, "Caught exception while add/update of managed domain details.", ex2);
            throw new SyMException(1002, ex2.getMessage(), ex2.fillInStackTrace());
        }
        return resultDO;
    }
    
    private Row constructManagedDomainRow(final Row mdRow, final Properties props) throws Exception {
        final String isADDomain = props.getProperty("IS_AD_DOMAIN");
        if (isADDomain != null) {
            mdRow.set("IS_AD_DOMAIN", (Object)Boolean.valueOf(isADDomain));
        }
        final String adDomainName = props.getProperty("AD_DOMAIN_NAME");
        if (adDomainName != null) {
            mdRow.set("AD_DOMAIN_NAME", (Object)adDomainName);
        }
        if (isADDomain != null && isADDomain.equals("false")) {
            final String DC_NAME = (String)mdRow.get("DC_NAME");
            if (DC_NAME != null && !DC_NAME.equals("--")) {
                mdRow.set("DC_NAME", (Object)"--");
            }
            final String AD_DOMAIN_NAME = (String)mdRow.get("AD_DOMAIN_NAME");
            if (AD_DOMAIN_NAME != null && !AD_DOMAIN_NAME.equals("--")) {
                mdRow.set("AD_DOMAIN_NAME", (Object)"--");
            }
        }
        final String dcName = props.getProperty("DC_NAME");
        if (dcName != null) {
            mdRow.set("DC_NAME", (Object)dcName);
        }
        final String userName = props.getProperty("USER_NAME");
        if (userName != null) {
            mdRow.set("USER_NAME", (Object)userName);
        }
        final String userPwd = "--";
        mdRow.set("PASSWORD", (Object)userPwd);
        final String hasManagedComps = props.getProperty("HAS_MANAGED_COMPUTERS");
        if (hasManagedComps != null) {
            mdRow.set("HAS_MANAGED_COMPUTERS", (Object)Boolean.valueOf(hasManagedComps));
        }
        final String dnsName = props.getProperty("DNS_SUFFIX");
        if (dnsName != null) {
            mdRow.set("DNS_SUFFIX", (Object)dnsName);
        }
        final String computerResolvedBy = props.getProperty("COMPUTER_RESOLVED_BY");
        if (computerResolvedBy != null) {
            mdRow.set("COMPUTER_RESOLVED_BY", (Object)new Integer(computerResolvedBy));
        }
        final String hasCredentials = props.getProperty("HAS_CREDENTIALS");
        if (hasCredentials != null) {
            mdRow.set("HAS_CREDENTIALS", (Object)Boolean.valueOf(hasCredentials));
        }
        final String domainGUID = props.getProperty("GUID");
        if (domainGUID != null) {
            mdRow.set("GUID", (Object)domainGUID);
        }
        return mdRow;
    }
    
    private void logDomainInfo(final String sourceMethod, final Properties props, final String action) {
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "**************" + action + " DOMAIN DETAILS*******");
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "Domain Name :" + ((Hashtable<K, Object>)props).get("DOMAIN_NETBIOS_NAME"));
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "User Name :" + ((Hashtable<K, Object>)props).get("USER_NAME"));
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "Has managed computers :" + ((Hashtable<K, Object>)props).get("HAS_MANAGED_COMPUTERS"));
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "Is AD Domain :" + ((Hashtable<K, Object>)props).get("IS_AD_DOMAIN"));
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "Domain Controller Name :" + ((Hashtable<K, Object>)props).get("DC_NAME"));
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "AD Domain Name :" + ((Hashtable<K, Object>)props).get("AD_DOMAIN_NAME"));
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "DNS Suffix :" + ((Hashtable<K, Object>)props).get("DNS_SUFFIX"));
        SyMLogger.info(this.somLogger, DomainHandler.sourceClass, sourceMethod, "**************" + action + " DOMAIN DETAILS*******");
    }
    
    private Properties getToBeDeletedDomainProperties(Properties domainProperties, final String domainName) {
        if (domainProperties == null) {
            domainProperties = new Properties();
        }
        try {
            final Properties props = SoMADUtil.getInstance().getManagedDomainInfo(domainName);
            if (props != null) {
                if (props.containsKey("AD_DOMAIN_NAME")) {
                    ((Hashtable<String, String>)domainProperties).put("DOMAIN_NETBIOS_NAME", props.getProperty("DOMAIN_NETBIOS_NAME"));
                }
                if (props.containsKey("IS_AD_DOMAIN")) {
                    ((Hashtable<String, String>)domainProperties).put("IS_AD_DOMAIN", String.valueOf(((Hashtable<K, Boolean>)props).get("IS_AD_DOMAIN")));
                }
                if (props.containsKey("USER_NAME")) {
                    ((Hashtable<String, String>)domainProperties).put("USER_NAME", props.getProperty("USER_NAME"));
                }
                if (props.containsKey("DC_NAME")) {
                    ((Hashtable<String, String>)domainProperties).put("DC_NAME", props.getProperty("DC_NAME"));
                }
                ((Hashtable<String, List>)domainProperties).put("CREDENTIAL_ID", SoMADUtil.getInstance().getmanagedDomainCredentialIDs(domainName));
            }
        }
        catch (final SyMException ex) {
            this.somLogger.log(Level.SEVERE, null, ex);
        }
        return domainProperties;
    }
    
    public int deleteDomainDetails(final String domainName, final Long resourceID) throws DataAccessException {
        int status = 5;
        if (resourceID != null) {
            Properties domainProperties = ApiFactoryProvider.getADImpl().deleteManagedResource(domainName);
            domainProperties = this.getToBeDeletedDomainProperties(domainProperties, domainName);
            Long customerId = null;
            if (domainProperties != null) {
                customerId = ((Hashtable<K, Long>)domainProperties).get("customerId");
            }
            final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
            DBHandlerFactoryProvider.getDBHandler().delete(criteria);
            final List domainCredentials = ((Hashtable<K, List>)domainProperties).get("CREDENTIAL_ID");
            final int credentialStatus = ApiFactoryProvider.getIdPsAPI().deleteDomainCredentials(domainCredentials);
            if (credentialStatus == 2) {
                status = 4;
            }
            else if (credentialStatus == 1) {
                status = 3;
            }
            final SoMEvent[] somEventArr = { null };
            final SoMEvent somEvent = new SoMEvent(resourceID, customerId);
            somEvent.resourceProperties = domainProperties;
            somEventArr[0] = somEvent;
            this.somLogger.log(Level.FINEST, "Calling up domains deleted listener ");
            getInstance().invokeDomainListeners(somEventArr, 103);
            final String defaultDomain = SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
            final TreeMap domainList = ApiFactoryProvider.getADAccessAPI().getADDomainNamesForLoginPage();
            if (defaultDomain != null && domainName.equalsIgnoreCase(defaultDomain) && !domainList.isEmpty() && !domainList.containsKey(domainName)) {
                SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", "dcLocal");
            }
            try {
                ApiFactoryProvider.getADImpl().removeHasManagedResourceFromHash(domainName, customerId);
            }
            catch (final Exception ex) {
                this.somLogger.log(Level.WARNING, "Caught exception while removing HAS_MANAGED_COMPUTERS from the memory hash for domain: " + domainName, ex);
            }
        }
        return status;
    }
    
    public DataObject getDomainsDO(final Criteria criteria) throws SyMException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        query.addJoin(new Join("Resource", "ManagedDomain", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addJoin(new Join("ManagedDomain", "ManagedDomainConfig", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.setCriteria(criteria);
        query.addSelectColumn(new Column((String)null, "*"));
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get(query);
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.SEVERE, "Caught exception while retrieving domain details from DB: ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public Properties getDomainInfo(final Long resourceID) throws SyMException {
        try {
            final Column resIDCol = Column.getColumn("Resource", "RESOURCE_ID");
            final Criteria criteria = new Criteria(resIDCol, (Object)resourceID, 0);
            return this.getDomainInfo(criteria);
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.SEVERE, "Caught exception while retrieving domain details from DB for Domain with resource ID: " + resourceID, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
    }
    
    protected Properties getDomainInfo(final Criteria criteria) throws Exception {
        Properties dnProps = null;
        try {
            final DataObject resultDO = this.getDomainsDO(criteria);
            if (!resultDO.isEmpty()) {
                final Row resRow = resultDO.getRow("Resource");
                final Row mdRow = resultDO.getRow("ManagedDomain");
                final Row domainConfigRow = resultDO.getRow("ManagedDomainConfig");
                dnProps = this.constructManagedDomainProps(resRow, mdRow, domainConfigRow);
            }
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.SEVERE, "Caught exception while retrieving domain details from DB for Domain with criteria: " + criteria, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return dnProps;
    }
    
    public Properties getDomainInfo(final String domainName) throws SyMException {
        try {
            final Column dnameCol = Column.getColumn("Resource", "NAME");
            Criteria criteria = new Criteria(dnameCol, (Object)domainName, 0, false);
            final Column typeCol = Column.getColumn("Resource", "RESOURCE_TYPE");
            criteria = criteria.and(new Criteria(typeCol, (Object)new Integer(5), 0));
            return this.getDomainInfo(criteria);
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.SEVERE, "Caught exception while retrieving domain details from DB for Domain: " + domainName, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
    }
    
    private Properties constructManagedDomainProps(final Row resourceRow, final Row managedDomainRow, final Row managedDomainConfigRow) throws Exception {
        return SoMADUtil.getInstance().constructManagedDomainProps(resourceRow, managedDomainRow, managedDomainConfigRow);
    }
    
    protected String getErrorMessageForErrorID(final int errorcode, final String adDomainName) {
        return this.getErrorMessageForErrorID(errorcode, adDomainName, null);
    }
    
    public String getErrorMessageForErrorID(final int errorcode) {
        return this.getErrorMessageForErrorID(errorcode, null);
    }
    
    protected String getErrorMessageForErrorID(final int errorcode, final String adDomainName, final Integer networkType) {
        try {
            switch (errorcode) {
                case 80007: {
                    return I18N.getMsg("desktopcentral.common.network_error", new Object[0]);
                }
                case 80006: {
                    return I18N.getMsg("desktopcentral.common.aad.generic_error", new Object[0]);
                }
                case 1: {
                    return I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Validation_Error", new Object[0]);
                }
                case 60010: {
                    return I18N.getMsg("desktopcentral.common.login.invalid_username_password", new Object[0]);
                }
                case 60012: {
                    return I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Invalid_AD_Domain_Name", new Object[0]);
                }
                case 60011: {
                    return I18N.getMsg("dc.webclient.admin.som.addDomain.DC_not_valid_Operational", new Object[0]);
                }
                case 60013: {
                    return I18N.getMsg("dc.webclient.admin.som.addDomain.SSL_failure", new Object[0]);
                }
            }
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.INFO, "facing an exception while fetching the i18n message", ex);
        }
        return "";
    }
    
    public String getErrorMessageForErrorID(final int errorcode, final int networkType) {
        return this.getErrorMessageForErrorID(errorcode, null, networkType);
    }
    
    public Properties validatePassWordForDomain(final Long customerID, final String domainName, final String adDomainName, final String dcName, final String userName, final String password, final int networkType, final boolean isSSL, final int portNo) {
        String errorMessage = null;
        boolean passwordValidated = false;
        int errorcode = 1;
        switch (networkType) {
            case 2: {
                try {
                    errorcode = ADAccessProvider.getInstance().validatePasswordWithErrorCode(domainName, adDomainName, dcName, userName, password, isSSL, portNo);
                }
                catch (final SyMException ex) {
                    try {
                        this.somLogger.log(Level.WARNING, "Exception while validating domain details...", ex);
                        final String i18n = I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Agent_installation_failed_Read_KB_with_link", new Object[0]);
                        final String[] errorMsg = I18NUtil.parseI18NString(ex.getMessage());
                        String msg = I18N.getMsg(errorMsg[0], new Object[0]);
                        if (errorMsg.length > 1) {
                            msg = I18NUtil.transformRemarks(errorMsg[0], errorMsg[1]);
                        }
                        errorMessage = msg + i18n;
                    }
                    catch (final Exception ex2) {
                        errorMessage = "exception while fetching i18n message";
                        this.somLogger.log(Level.SEVERE, "exception while fetching i18n message", ex2);
                    }
                }
                break;
            }
            case 1: {
                errorcode = 0;
                break;
            }
        }
        this.somLogger.log(Level.INFO, "Domain Details validate password Error Code : " + errorcode);
        final Properties credentialValidationDetails = new Properties();
        if (errorcode == 0) {
            passwordValidated = true;
        }
        else {
            errorMessage = this.getErrorMessageForErrorID(errorcode, adDomainName, networkType);
            if (errorMessage != null) {
                this.somLogger.log(Level.INFO, "Domain Details validate password error message : " + errorMessage);
                ((Hashtable<String, String>)credentialValidationDetails).put("ERROR_MESSAGE", errorMessage);
            }
        }
        ((Hashtable<String, Boolean>)credentialValidationDetails).put("VALID_PASSWORD", passwordValidated);
        return credentialValidationDetails;
    }
    
    public Properties checkForSameDomainExisting(final Long customerID, final String domainName, final String adDomainName, final int toBeAddedDomainNetworkType, final String action) throws Exception {
        return ApiFactoryProvider.getIdPsAPI().checkForSameDomainExisting(customerID, domainName, adDomainName, toBeAddedDomainNetworkType, action);
    }
    
    public void handleDomainValidationFailure(final String domainName, final Long customerID) throws SyMException {
        final Long resourceID = SoMADUtil.getInstance().getResourceIDForDomain(domainName, customerID);
        if (resourceID != null) {
            SoMADUtil.getInstance().updateManagedDomainCredentialStatus(resourceID, Boolean.FALSE);
            SoMADUtil.getInstance().hideOrShowDomainPwdChangedMsg(customerID);
        }
    }
    
    public void addOrUpdateDomainConfiguration(final Long resourceID, final Integer portNo, final Boolean isSSL) throws SyMException {
        this.somLogger.log(Level.INFO, "addOrUpdateDomainConfiguration called");
        try {
            final Column resIdCol = Column.getColumn("ManagedDomainConfig", "RESOURCE_ID");
            final Criteria criteria = new Criteria(resIdCol, (Object)resourceID, 0);
            final DataObject resDO = SyMUtil.getPersistence().get("ManagedDomainConfig", criteria);
            Row configRow = null;
            final Long currentTime = SyMUtil.getCurrentTimeInMillis();
            if (resDO.isEmpty()) {
                if (isSSL) {
                    configRow = new Row("ManagedDomainConfig");
                    configRow.set("RESOURCE_ID", (Object)resourceID);
                    configRow.set("USE_SSL", (Object)isSSL);
                    configRow.set("PORT_NO", (Object)portNo);
                    configRow.set("DB_UPDATED_TIME", (Object)currentTime);
                    resDO.addRow(configRow);
                    SyMUtil.getPersistence().add(resDO);
                    this.somLogger.log(Level.INFO, "new row added");
                }
            }
            else {
                configRow = resDO.getFirstRow("ManagedDomainConfig");
                if (configRow != null) {
                    configRow.set("PORT_NO", (Object)portNo);
                    configRow.set("USE_SSL", (Object)isSSL);
                    configRow.set("DB_UPDATED_TIME", (Object)currentTime);
                    resDO.updateRow(configRow);
                    SyMUtil.getPersistence().update(resDO);
                    this.somLogger.log(Level.INFO, "exisiting row updated");
                }
            }
        }
        catch (final Exception ex) {
            this.somLogger.log(Level.SEVERE, "Caught exception while add/update of  domain configuration.", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
    }
    
    static {
        DomainHandler.domainHandler = null;
        DomainHandler.sourceClass = "DomainHandler";
    }
}
