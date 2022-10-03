package com.me.devicemanagement.framework.server.util;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.resource.ResourceDataProvider;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.admin.CredentialManager;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import com.me.devicemanagement.framework.winaccess.WinAccessProvider;
import java.util.Properties;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.logging.Logger;

public class SoMADUtil
{
    private static Logger somLogger;
    private static SoMADUtil sUtil;
    
    protected SoMADUtil() {
    }
    
    public static synchronized SoMADUtil getInstance() {
        if (SoMADUtil.sUtil == null) {
            SoMADUtil.sUtil = new SoMADUtil();
        }
        return SoMADUtil.sUtil;
    }
    
    public List getmanagedDomainCredentialIDs(final String domainName) throws SyMException {
        Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)5, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false));
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        if (customerID != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
        }
        final List<Long> domainCredentials = new ArrayList<Long>();
        try {
            final SelectQuery query = SyMUtil.formSelectQuery("Resource", criteria, new ArrayList<Column>(Arrays.asList(Column.getColumn("ManagedDomainCredentialRel", "CREDENTIAL_ID"), Column.getColumn("ManagedDomainCredentialRel", "DOMAINRESOURCE_ID"))), null, null, new ArrayList<Join>(Arrays.asList(new Join("Resource", "ManagedDomain", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2), new Join("ManagedDomain", "ManagedDomainCredentialRel", new String[] { "RESOURCE_ID" }, new String[] { "DOMAINRESOURCE_ID" }, 1))), null);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            if (resultDO != null && !resultDO.isEmpty()) {
                final Iterator it = resultDO.getRows("ManagedDomainCredentialRel");
                while (it.hasNext()) {
                    final Row managedDomainCredentialRelRow = it.next();
                    final Long credentialID = (Long)managedDomainCredentialRelRow.get("CREDENTIAL_ID");
                    domainCredentials.add(credentialID);
                }
            }
        }
        catch (final DataAccessException ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domain details from DB for Domain: " + domainName, (Throwable)ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return domainCredentials;
    }
    
    public void setCurrentDomainDetails(final Properties props) throws SyMException, Exception {
        final String domainName = ((Hashtable<K, String>)props).get("DOMAIN_NETBIOS_NAME");
        final String domainFlatName = WinAccessProvider.getWinAccessAPI().getDomainFlatName();
        SoMADUtil.somLogger.log(Level.INFO, "domainFlatName :" + domainFlatName);
        if (domainFlatName.equalsIgnoreCase(domainName)) {
            final int envir = WinAccessProvider.getWinAccessAPI().isAD();
            SoMADUtil.somLogger.log(Level.INFO, "Environment detected now :" + envir);
            final String currentDomainName = WinAccessProvider.getWinAccessAPI().getCurrentDomainName();
            SoMADUtil.somLogger.log(Level.INFO, "currentDomainName :" + currentDomainName);
            if (envir == 3) {
                if (props.get("DC_NAME") == null || props.getProperty("DC_NAME").isEmpty()) {
                    final String currentDcName = WinAccessProvider.getWinAccessAPI().getServerName();
                    if (currentDcName != null) {
                        ((Hashtable<String, String>)props).put("DC_NAME", currentDcName);
                    }
                }
                ((Hashtable<String, String>)props).put("IS_AD_DOMAIN", "true");
                if (currentDomainName != null) {
                    ((Hashtable<String, String>)props).put("AD_DOMAIN_NAME", currentDomainName);
                }
            }
            if (currentDomainName != null && !currentDomainName.equals("")) {
                ((Hashtable<String, String>)props).put("DNS_SUFFIX", currentDomainName);
            }
        }
    }
    
    public void setCurrentDomainDetails(final Map domainDetails) throws Exception {
        final String domainName = domainDetails.get("domainName");
        final String domainFlatName = WinAccessProvider.getWinAccessAPI().getDomainFlatName();
        SoMADUtil.somLogger.log(Level.INFO, "domainFlatName :" + domainFlatName);
        if (domainFlatName.equalsIgnoreCase(domainName)) {
            final int envir = WinAccessProvider.getWinAccessAPI().isAD();
            SoMADUtil.somLogger.log(Level.INFO, "Environment detected now :" + envir);
            final String currentDomainName = WinAccessProvider.getWinAccessAPI().getCurrentDomainName();
            SoMADUtil.somLogger.log(Level.INFO, "current Domain Name :" + currentDomainName);
            if (envir == 3) {
                domainDetails.put("isADDomain", true);
                if (currentDomainName != null) {
                    domainDetails.put("adDomainName", currentDomainName);
                }
                if (domainDetails.get("dcName") == null || domainDetails.get("dcName").isEmpty() || domainDetails.get("dcName").equals("--")) {
                    final String currentDcName = WinAccessProvider.getWinAccessAPI().getServerName();
                    SoMADUtil.somLogger.log(Level.INFO, "current dcName :" + currentDcName);
                    if (currentDcName != null) {
                        domainDetails.put("dcName", currentDcName);
                    }
                }
            }
            if (currentDomainName != null && !currentDomainName.equals("")) {
                domainDetails.put("dnsSuffix", currentDomainName);
            }
        }
    }
    
    public Properties getManagedDomainInfo(final String domainName) throws SyMException {
        Properties dnProps = null;
        try {
            final Column dnameCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
            Criteria criteria = new Criteria(dnameCol, (Object)domainName, 0, false);
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            if (customerID != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            }
            final DataObject resultDO = this.getManagedDomainsDO(criteria);
            if (!resultDO.isEmpty()) {
                final Row resRow = resultDO.getRow("Resource");
                final Row mdRow = resultDO.getRow("ManagedDomain");
                final Row configRow = resultDO.getRow("ManagedDomainConfig");
                dnProps = this.constructManagedDomainProps(resRow, mdRow, configRow);
            }
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domain details from DB for Domain: " + domainName, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return dnProps;
    }
    
    public DataObject getManagedDomainsDO(final Criteria criteria) throws SyMException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        query.addJoin(new Join("Resource", "ManagedDomain", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedDomain", "ManagedDomainCredentialRel", new String[] { "RESOURCE_ID" }, new String[] { "DOMAINRESOURCE_ID" }, 1));
        query.addJoin(new Join("ManagedDomain", "ManagedDomainConfig", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        final Column resTypeCol = Column.getColumn("Resource", "RESOURCE_TYPE");
        Criteria resTypeCri = new Criteria(resTypeCol, (Object)new Integer(5), 0);
        if (criteria != null) {
            resTypeCri = resTypeCri.and(criteria);
        }
        query.setCriteria(resTypeCri);
        query.addSelectColumn(new Column((String)null, "*"));
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get(query);
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domain DO from DB: ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public Properties constructManagedDomainProps(final Row resourceRow, final Row managedDomainRow, final Row domainConfigurationRow) throws Exception {
        if (resourceRow == null) {
            return null;
        }
        final Properties mdProps = new Properties();
        String colName = null;
        final List resourceRowNameList = resourceRow.getColumns();
        Iterator rowIter = resourceRowNameList.iterator();
        while (rowIter.hasNext()) {
            colName = rowIter.next();
            ((Hashtable<String, Object>)mdProps).put(colName, resourceRow.get(colName));
        }
        if (domainConfigurationRow != null) {
            final List domainConfigurationRowList = domainConfigurationRow.getColumns();
            rowIter = domainConfigurationRowList.iterator();
            while (rowIter.hasNext()) {
                colName = rowIter.next();
                ((Hashtable<String, Object>)mdProps).put(colName, domainConfigurationRow.get(colName));
            }
        }
        else {
            this.setDefaultDomainConfigDeatils(mdProps);
        }
        if (managedDomainRow != null) {
            final List managedDomainRowNameList = managedDomainRow.getColumns();
            rowIter = managedDomainRowNameList.iterator();
            while (rowIter.hasNext()) {
                colName = rowIter.next();
                if (!colName.equals("PASSWORD") && !colName.equals("USER_NAME")) {
                    ((Hashtable<String, Object>)mdProps).put(colName, managedDomainRow.get(colName));
                }
            }
            final DataObject credDO = this.getDomainRootCredentialDO((Long)managedDomainRow.get("RESOURCE_ID"));
            if (!credDO.isEmpty()) {
                final String userName = (String)credDO.getFirstValue("Credential", "CRD_USERNAME");
                String password = (String)credDO.getFirstValue("Credential", "CRD_PASSWORD");
                password = ApiFactoryProvider.getCryptoAPI().decrypt(password, (Integer)credDO.getFirstValue("Credential", "CRD_ENC_TYPE"));
                ((Hashtable<String, String>)mdProps).put("USER_NAME", userName);
                ((Hashtable<String, String>)mdProps).put("PASSWORD", password);
                ((Hashtable<String, Object>)mdProps).put("CREDENTIAL_ID", credDO.getFirstValue("Credential", "CREDENTIAL_ID"));
                ((Hashtable<String, Object>)mdProps).put("CREDENTIAL_UUID", credDO.getFirstValue("Credential", "CREDENTIAL_UUID"));
            }
            else {
                SoMADUtil.somLogger.log(Level.INFO, "Returning dummy credential for domain resource Id " + managedDomainRow.get("RESOURCE_ID") + " with hasCredentials as " + managedDomainRow.get("HAS_CREDENTIALS"));
                ((Hashtable<String, String>)mdProps).put("USER_NAME", "--");
                ((Hashtable<String, String>)mdProps).put("PASSWORD", "");
                SoMADUtil.somLogger.log(Level.INFO, "Returning dummy credential for domain  " + mdProps);
            }
        }
        return mdProps;
    }
    
    private void setDefaultDomainConfigDeatils(Properties mdProps) {
        if (mdProps == null) {
            mdProps = new Properties();
        }
        ((Hashtable<String, Boolean>)mdProps).put("USE_SSL", false);
        ((Hashtable<String, Integer>)mdProps).put("PORT_NO", 0);
    }
    
    public boolean addOrUpdateActiveDirectoryInfo(final String domainName, final String dcName, final String userName, final String password, final boolean isSSL, final int port) {
        return this.addOrUpdateActiveDirectoryInfo(null, domainName, dcName, "", userName, password, isSSL, port);
    }
    
    public boolean addOrUpdateActiveDirectoryInfo(final String domainName, final String dcName, final String scName, final String userName, final String password, final boolean isSSL, final int port) {
        return this.addOrUpdateActiveDirectoryInfo(null, domainName, dcName, scName, userName, password, isSSL, port);
    }
    
    public boolean addOrUpdateActiveDirectoryInfo(final Long domainID, final String domainName, final String dcName, final String scName, final String userName, final String password, final boolean isSSL, final int port) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ActiveDirectoryInfo"));
            query.addSelectColumn(new Column("ActiveDirectoryInfo", "*"));
            Criteria criteria = new Criteria(Column.getColumn("ActiveDirectoryInfo", "DEFAULTDOMAIN"), (Object)domainName, 0, false);
            query.setCriteria(criteria);
            if (domainID != null) {
                criteria = new Criteria(Column.getColumn("ActiveDirectoryInfo", "AD_ID"), (Object)domainID, 0);
                query.setCriteria(criteria);
            }
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            Row userRow = dobj.getRow("ActiveDirectoryInfo");
            if (userRow != null) {
                userRow.set("SERVERNAME", (Object)dcName);
                userRow.set("DEFAULTDOMAIN", (Object)domainName.toLowerCase());
                userRow.set("SECONDARYSERVERNAME", (Object)((scName != null && !scName.isEmpty()) ? scName : dcName));
                userRow.set("USERNAME", (Object)userName);
                userRow.set("PASSWORD", (Object)password);
                userRow.set("ISSSL", (Object)isSSL);
                userRow.set("port", (Object)port);
                dobj.updateRow(userRow);
                SyMUtil.getPersistence().update(dobj);
            }
            else {
                final WritableDataObject dobjNew = new WritableDataObject();
                userRow = new Row("ActiveDirectoryInfo");
                userRow.set("DEFAULTDOMAIN", (Object)domainName.toLowerCase());
                userRow.set("SERVERNAME", (Object)dcName);
                userRow.set("SECONDARYSERVERNAME", (Object)((scName != null && !scName.isEmpty()) ? scName : dcName));
                userRow.set("USERNAME", (Object)userName);
                userRow.set("PASSWORD", (Object)password);
                userRow.set("ISSSL", (Object)isSSL);
                userRow.set("port", (Object)port);
                dobjNew.addRow(userRow);
                SyMUtil.getPersistence().add((DataObject)dobjNew);
            }
            return true;
        }
        catch (final Exception e) {
            SoMADUtil.somLogger.log(Level.WARNING, "Caught exception while storing a addOrUpdateActiveDirectoryInfo : ", e);
            return false;
        }
    }
    
    public List getAllManagedDomainProp() throws SyMException {
        final List domainList = new ArrayList();
        DataObject resultDO = null;
        try {
            final Criteria crit = null;
            resultDO = this.getManagedDomainsDO(crit);
            if (!resultDO.isEmpty()) {
                final Iterator resRows = resultDO.getRows("Resource");
                while (resRows.hasNext()) {
                    final Row resRow = resRows.next();
                    final Properties prop = new Properties();
                    final List colList = resRow.getColumns();
                    for (int i = 0; i < colList.size(); ++i) {
                        final String colName = colList.get(i);
                        ((Hashtable<String, Object>)prop).put(colName, resRow.get(colName));
                    }
                    final Long resourceID = (Long)resRow.get("RESOURCE_ID");
                    final Row managedDomainRow = resultDO.getRow("ManagedDomain", new Criteria(Column.getColumn("ManagedDomain", "RESOURCE_ID"), (Object)resourceID, 0));
                    final Row managedDomainCredentialRelRow = resultDO.getRow("ManagedDomainCredentialRel", new Criteria(Column.getColumn("ManagedDomainCredentialRel", "DOMAINRESOURCE_ID"), (Object)resourceID, 0).and(new Criteria(Column.getColumn("ManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0)));
                    if (managedDomainCredentialRelRow != null) {
                        ((Hashtable<String, Object>)prop).put("VALIDATION_STATUS", managedDomainCredentialRelRow.get("VALIDATION_STATUS"));
                    }
                    ((Hashtable<String, Object>)prop).put("DC_NAME", managedDomainRow.get("DC_NAME"));
                    ((Hashtable<String, Object>)prop).put("AD_DOMAIN_NAME", managedDomainRow.get("AD_DOMAIN_NAME"));
                    ((Hashtable<String, Object>)prop).put("IS_AD_DOMAIN", managedDomainRow.get("IS_AD_DOMAIN"));
                    domainList.add(prop);
                }
            }
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domain names from DB: ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return domainList;
    }
    
    public List getADManagedDomainNames() throws SyMException {
        final Column criCol = Column.getColumn("ManagedDomain", "IS_AD_DOMAIN");
        final Criteria criteria = new Criteria(criCol, (Object)Boolean.TRUE, 0);
        return this.getManagedDomainNames(criteria);
    }
    
    public List getManagedDomainNames(final Criteria criteria) throws SyMException {
        final List domainList = new ArrayList();
        DataObject resultDO = null;
        try {
            resultDO = this.getManagedDomainsDO(criteria);
            if (!resultDO.isEmpty()) {
                final Iterator resRows = resultDO.getRows("Resource");
                while (resRows.hasNext()) {
                    final Row resRow = resRows.next();
                    domainList.add(resRow.get("DOMAIN_NETBIOS_NAME"));
                }
            }
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domain names from DB: ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return domainList;
    }
    
    public List getAllManagedDomains() throws SyMException {
        return this.getAllManagedDomains(null);
    }
    
    public List getAllManagedDomains(final Criteria criteria) throws SyMException {
        List managedDomains = new ArrayList();
        try {
            final DataObject resultDO = this.getManagedDomainsDO(criteria);
            managedDomains = this.constructManagedDomainsFromDO(resultDO);
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving all managed domain details from DB: ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return managedDomains;
    }
    
    public List constructManagedDomainsFromDO(final DataObject inputDO) throws Exception {
        final List managedDomains = new ArrayList();
        if (inputDO != null && !inputDO.isEmpty()) {
            final Iterator resRows = inputDO.getRows("Resource");
            while (resRows.hasNext()) {
                final Row resRow = resRows.next();
                final Row mdRowTmp = new Row("ManagedDomain");
                mdRowTmp.set("RESOURCE_ID", resRow.get("RESOURCE_ID"));
                final Row domainConfigRowTmp = new Row("ManagedDomainConfig");
                domainConfigRowTmp.set("RESOURCE_ID", resRow.get("RESOURCE_ID"));
                final Row configRow = inputDO.getRow("ManagedDomainConfig", domainConfigRowTmp);
                final Row mdRow = inputDO.getRow("ManagedDomain", mdRowTmp);
                final Properties mdProps = this.constructManagedDomainProps(resRow, mdRow, configRow);
                managedDomains.add(mdProps);
            }
        }
        return managedDomains;
    }
    
    public String getADManagedDomain(final Long domainID) throws SyMException {
        final List<String> domainNames = getInstance().getManagedDomainNames(new Criteria(Column.getColumn("ManagedDomain", "RESOURCE_ID"), (Object)domainID, 0).and(new Criteria(Column.getColumn("ManagedDomain", "IS_AD_DOMAIN"), (Object)true, 0)));
        if (!domainNames.isEmpty()) {
            return domainNames.get(0);
        }
        return null;
    }
    
    public DataObject getDomainRootCredentialDO(final Long domainId) throws DataAccessException {
        final SelectQuery domainCredQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Credential"));
        domainCredQuery.addSelectColumn(new Column("Credential", "*"));
        domainCredQuery.addSelectColumn(new Column("ManagedDomainCredentialRel", "*"));
        domainCredQuery.addJoin(new Join("Credential", "ManagedDomainCredentialRel", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 2));
        final Criteria domainCri = new Criteria(Column.getColumn("ManagedDomainCredentialRel", "DOMAINRESOURCE_ID"), (Object)domainId, 0);
        domainCredQuery.setCriteria(domainCri.and(new Criteria(Column.getColumn("ManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0)));
        final DataObject credDO = SyMUtil.getPersistence().get(domainCredQuery);
        return credDO;
    }
    
    public String getManagedDomain(String domainName) {
        Column column = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
        final Criteria dnNetBaioscriteria = new Criteria(column, (Object)domainName, 0, false);
        column = Column.getColumn("ManagedDomain", "AD_DOMAIN_NAME");
        final Criteria dncriteria = new Criteria(column, (Object)domainName, 0, false);
        final Criteria criteria = dnNetBaioscriteria.or(dncriteria);
        try {
            final DataObject data = this.getManagedDomainsDO(criteria);
            if (!data.isEmpty() && data.size("Resource") > 0) {
                final Row resRow = data.getFirstRow("Resource");
                if (resRow != null) {
                    domainName = (String)resRow.get("DOMAIN_NETBIOS_NAME");
                }
            }
        }
        catch (final Exception e) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while getting managed domain: ", e);
        }
        return domainName;
    }
    
    public DataObject getManagedDomainDO(final String domainName) throws SyMException {
        DataObject resultDO = null;
        Criteria criteria = null;
        try {
            if (domainName != null) {
                criteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)domainName, 0, false);
            }
            resultDO = this.getManagedDomainsDO(criteria);
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domain DO from DB for the domain name :" + domainName, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public boolean hasCredential(final String domainName) throws SyMException, DataAccessException, Exception {
        final DataObject dobj = this.getManagedDomainDO(domainName);
        SoMADUtil.somLogger.log(Level.FINEST, "resourceDO for domain" + domainName + " is : " + dobj);
        final Row credRow = dobj.getRow("ManagedDomain");
        if (credRow != null) {
            final boolean hasCredentials = (boolean)credRow.get("HAS_CREDENTIALS");
            if (hasCredentials) {
                return true;
            }
        }
        return false;
    }
    
    public String getDomainNameForResourceID(final Long resourceID) throws SyMException {
        String domainName = null;
        try {
            Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer(5), 0));
            final DataObject dataObj = SyMUtil.getPersistence().get("Resource", criteria);
            final Row resRow = dataObj.getFirstRow("Resource");
            SoMADUtil.somLogger.log(Level.INFO, "Resource row for : " + resourceID + " is :" + resRow);
            if (resRow != null) {
                domainName = (String)resRow.get("DOMAIN_NETBIOS_NAME");
            }
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while getting domain Name for resource ID : " + resourceID, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        SoMADUtil.somLogger.log(Level.INFO, "Returning domain : " + domainName);
        return domainName;
    }
    
    public void addOrUpdateDomainCredential(final Properties props, final Long domainId) throws DataAccessException {
        final DataObject domainCredDO = this.getDomainRootCredentialDO(domainId);
        final Properties credProps = new Properties();
        final String userName = props.getProperty("USER_NAME");
        final String userPwd = props.getProperty("PASSWORD");
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
            SoMADUtil.somLogger.log(Level.INFO, "Since the username received is a dummy credential of username " + userName + ", Returning from the addorupdatedomaincredential block..");
            if (!domainCredDO.isEmpty()) {
                SoMADUtil.somLogger.log(Level.INFO, "Dummy credential -> Deleting the old credential of DO ");
                domainCredDO.deleteRow(domainCredDO.getRow("ManagedDomainCredentialRel"));
                domainCredDO.deleteRow(domainCredDO.getRow("Credential"));
                SyMUtil.getPersistence().update(domainCredDO);
            }
            return;
        }
        final String domainName = props.getProperty("DOMAIN_NETBIOS_NAME");
        credProps.setProperty("CredentialName", domainName + "_root");
        final String isADDomain = props.getProperty("IS_AD_DOMAIN");
        boolean isAD = false;
        if (isADDomain != null) {
            isAD = Boolean.valueOf(isADDomain);
        }
        credProps.setProperty("CredentialType", String.valueOf(isAD ? 301 : 302));
        ((Hashtable<String, Object>)credProps).put("CustomerID", ((Hashtable<K, Object>)props).get("CUSTOMER_ID"));
        final Properties logProperties = new Properties();
        logProperties.putAll(credProps);
        logProperties.remove("PassWord");
        Long credId = null;
        String credUUID = null;
        String loginUserName = "";
        try {
            loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        }
        catch (final Exception e) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Unable to get Login Username");
        }
        final Long customerID = Long.parseLong(String.valueOf(((Hashtable<K, Object>)props).get("CUSTOMER_ID")));
        final HashMap credentialDetailMap = new CredentialManager().addOrUpdateCredentialDetails(credProps);
        if (credentialDetailMap != null) {
            credId = credentialDetailMap.get("CREDENTIAL_ID");
            credUUID = credentialDetailMap.get("CREDENTIAL_UUID");
        }
        if (domainCredDO.isEmpty()) {
            SoMADUtil.somLogger.log(Level.INFO, "Adding a new credential of name " + props.getProperty("DOMAIN_NETBIOS_NAME") + " with cred id " + credId);
            if (credId == null || credUUID == null) {
                SoMADUtil.somLogger.log(Level.SEVERE, "Not able to add credentials with props " + logProperties);
                return;
            }
            DCEventLogUtil.getInstance().addEvent(4001, loginUserName, null, "dc.admin.credmgr.cred_added", domainName + "@@@" + loginUserName, false, customerID);
            final Row domainCredMapRow = new Row("ManagedDomainCredentialRel");
            domainCredMapRow.set("DOMAINRESOURCE_ID", (Object)domainId);
            domainCredMapRow.set("CREDENTIAL_ID", (Object)credId);
            domainCredMapRow.set("CREDENTIAL_UUID", (Object)credUUID);
            final DataObject domainCredMapDO = SyMUtil.getPersistence().get("ManagedDomainCredentialRel", domainCredMapRow);
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
            SoMADUtil.somLogger.log(Level.INFO, "Updating a credential of name " + props.getProperty("DOMAIN_NETBIOS_NAME") + " with cred id " + credId);
            if (credId == null || credUUID == null) {
                SoMADUtil.somLogger.log(Level.SEVERE, "Not able to add credentials with props " + logProperties);
                return;
            }
            DCEventLogUtil.getInstance().addEvent(4001, loginUserName, null, "dc.admin.credmgr.cred_modified", domainName, false, customerID);
            final Row domainCredMapRow = domainCredDO.getRow("ManagedDomainCredentialRel");
            domainCredMapRow.set("CREDENTIAL_ID", (Object)credId);
            domainCredMapRow.set("CREDENTIAL_UUID", (Object)credUUID);
            domainCredMapRow.set("IS_ROOT", (Object)1);
            domainCredDO.updateRow(domainCredMapRow);
            SyMUtil.getPersistence().update(domainCredDO);
        }
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userID != null) {
                SoMADUtil.somLogger.log(Level.INFO, "Credential of cred id " + credId + " add / updated by login id " + userID);
                new CredentialManager().addOrUpdateUserCredentialRel(credId, credUUID, userID);
            }
            else {
                SoMADUtil.somLogger.log(Level.SEVERE, "Auth Credential is null, hence login id mapping ignored..");
            }
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Exception in add or update user credential rel " + logProperties, ex);
        }
    }
    
    protected Criteria getCriteriaForPasswordChangedDomain() {
        final Column criCol = Column.getColumn("ManagedDomainCredentialRel", "VALIDATION_STATUS");
        Criteria criteria = new Criteria(criCol, (Object)Boolean.FALSE, 0);
        criteria = criteria.and(Column.getColumn("ManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0);
        final Criteria managedResourceCri = ApiFactoryProvider.getADImpl().getManagedResourceCriteriaForPasswordChangedDomain();
        if (managedResourceCri != null) {
            criteria = criteria.and(managedResourceCri);
        }
        return criteria;
    }
    
    public Criteria getCriteriaForDomainWithoutCredential() {
        final Column criCol = Column.getColumn("ManagedDomain", "HAS_CREDENTIALS");
        final Criteria criteria = new Criteria(criCol, (Object)Boolean.FALSE, 0);
        return criteria;
    }
    
    public Long getResourceIDForDomain(final String domainName, final Long customerID) throws SyMException {
        Long resourceID = null;
        try {
            final Row resourceRow = new Row("Resource");
            resourceRow.set("NAME", (Object)domainName);
            resourceRow.set("DOMAIN_NETBIOS_NAME", (Object)domainName);
            resourceRow.set("RESOURCE_TYPE", (Object)new Integer(5));
            resourceRow.set("CUSTOMER_ID", (Object)new Long(customerID));
            final Row resRow = ResourceDataProvider.getResourceRowFromDB(resourceRow);
            if (resRow != null) {
                resourceID = (Long)resRow.get("RESOURCE_ID");
            }
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while getting resource ID for customerID : " + customerID + " domain: " + domainName, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resourceID;
    }
    
    public void updateManagedDomainCredentialStatus(final Long resourceID, final boolean verifyRes) {
        try {
            Criteria criteria = new Criteria(Column.getColumn("ManagedDomainCredentialRel", "DOMAINRESOURCE_ID"), (Object)resourceID, 0);
            criteria = criteria.and(Column.getColumn("ManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0);
            final DataObject resultDO = SyMUtil.getPersistence().get("ManagedDomainCredentialRel", criteria);
            if (!resultDO.isEmpty()) {
                SoMADUtil.somLogger.log(Level.INFO, "Going to update MANAGEDDOMAINCREDENTIALREL row for resourceID :" + resourceID);
                final Row row = resultDO.getFirstRow("ManagedDomainCredentialRel");
                row.set("VALIDATION_STATUS", (Object)verifyRes);
                resultDO.updateRow(row);
                SoMADUtil.somLogger.log(Level.INFO, "Going to update row :" + row);
                SyMUtil.getPersistence().update(resultDO);
                SoMADUtil.somLogger.log(Level.INFO, "updated row :" + row);
            }
            else {
                SoMADUtil.somLogger.log(Level.INFO, "Dummy Credential ? ManagedDomainCredentialRel row not found for resource id: " + resourceID);
            }
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while adding DomainCredentialStatus entry for resourceID :" + resourceID, ex);
        }
    }
    
    public void hideOrShowDomainPwdChangedMsg(final Long customerID) {
        try {
            final List domainChanged = this.getPasswordChangedManagedDomainNames(customerID);
            if (domainChanged.size() > 0) {
                final MessageProvider msgProvider = MessageProvider.getInstance();
                msgProvider.unhideMessage("DOMAIN_PASSWORD_CHANGED", customerID);
            }
            else {
                MessageProvider.getInstance().hideMessage("DOMAIN_PASSWORD_CHANGED", customerID);
            }
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.SEVERE, "Caught exception while validating AD Domains :", ex);
        }
    }
    
    public List getPasswordChangedManagedDomainNames(final Long customerID) throws SyMException {
        Criteria criteria = this.getCriteriaForPasswordChangedDomain();
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
        return this.getManagedDomainNames(criteria);
    }
    
    public String getUniqueIDParameter(final Long customerID) {
        String uniqueIDParam = "SERVICE_TAG";
        try {
            final String dbValue = CustomerParamsHandler.getInstance().getParameterValue("UNIQUE_ID_PARAM", customerID);
            uniqueIDParam = ((dbValue != null) ? dbValue : uniqueIDParam);
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.WARNING, "Exception in getting unique ID Param", ex);
            uniqueIDParam = "SERVICE_TAG";
        }
        return uniqueIDParam;
    }
    
    public Boolean getUniqueIDStatus(Long customerID) {
        Boolean uniqueIDStatus = Boolean.FALSE;
        try {
            if (customerID == null && !CustomerInfoUtil.getInstance().isMSP() && !CustomerInfoUtil.isSAS()) {
                customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            }
            final String dbValue = CustomerParamsHandler.getInstance().getParameterValue("UNIQUE_ID_STATUS", customerID);
            uniqueIDStatus = Boolean.valueOf(dbValue);
        }
        catch (final Exception ex) {
            SoMADUtil.somLogger.log(Level.WARNING, "Exception in getting unique ID Status", ex);
            uniqueIDStatus = Boolean.FALSE;
        }
        return uniqueIDStatus;
    }
    
    static {
        SoMADUtil.somLogger = Logger.getLogger("SoMLogger");
        SoMADUtil.sUtil = null;
    }
}
