package com.me.devicemanagement.framework.server.customer;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import org.apache.commons.lang.StringUtils;
import java.util.TimeZone;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Properties;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.ReadOnlyPersistence;
import java.util.logging.Logger;

public class CustomerInfoUtil
{
    protected static Logger logger;
    protected static Logger out;
    private static CustomerInfoUtil custInfoUtil;
    @Deprecated
    public static Boolean isSAS;
    private static Boolean isMDM;
    private static Boolean isDC;
    private static Boolean isMDMP;
    private static Boolean isPMP;
    private static Boolean isRAP;
    private static Boolean isOSDProduct;
    private static Boolean isBSPProduct;
    private static Boolean isPMPProduct;
    private static Boolean isVMPProduct;
    private static Boolean isDCPProduct;
    private static Boolean isACPProduct;
    public static Boolean isUESProduct;
    public static final String MSPNAME = "DC_MSP";
    public static final String CUSTOMER_LOGO = "customer-logo.png";
    public static final String[] SECURITY_ADDON_ENABLED_KEYS;
    private static ReadOnlyPersistence cachedPersistence;
    
    public static void resetCustomerInfoUtil() {
        CustomerInfoUtil.isDC = null;
        CustomerInfoUtil.isMDM = null;
        CustomerInfoUtil.isPMP = null;
        CustomerInfoUtil.isRAP = null;
        CustomerInfoUtil.isMDMP = null;
        CustomerInfoUtil.isOSDProduct = null;
        CustomerInfoUtil.isPMPProduct = null;
        CustomerInfoUtil.isVMPProduct = null;
        CustomerInfoUtil.isACPProduct = null;
        CustomerInfoUtil.isDCPProduct = null;
        CustomerInfoUtil.isSAS = null;
    }
    
    protected CustomerInfoUtil() {
        isSAS();
        this.isMSP();
    }
    
    public static void setIsSAS(final boolean issas) {
        CustomerInfoUtil.isSAS = issas;
    }
    
    public static synchronized CustomerInfoUtil getInstance() {
        if (CustomerInfoUtil.custInfoUtil == null) {
            CustomerInfoUtil.custInfoUtil = new CustomerInfoUtil();
        }
        return CustomerInfoUtil.custInfoUtil;
    }
    
    public static ReadOnlyPersistence getCachedPersistence() throws Exception {
        if (CustomerInfoUtil.cachedPersistence == null) {
            CustomerInfoUtil.cachedPersistence = ApiFactoryProvider.getPersistenceAPI().getCachedPersistence();
        }
        return CustomerInfoUtil.cachedPersistence;
    }
    
    public Long getDefaultCustomer() {
        try {
            if (this.isMSP()) {
                CustomerInfoUtil.out.severe("IN MSP EDITION Default customer id is 0.");
                return null;
            }
            if (isSAS()) {
                final Long customerid = ApiFactoryProvider.getUtilAccessAPI().getCustomerID();
                CustomerInfoUtil.out.fine("IN SAS EDITION Default customer id is   " + customerid);
                return customerid;
            }
            final ReadOnlyPersistence cachedPersistence = getCachedPersistence();
            final Criteria custCrit = new Criteria(new Column("CustomerInfo", "CUSTOMER_NAME"), (Object)"DC_CUSTOMER", 0);
            final DataObject custInfoDO = cachedPersistence.get("CustomerInfo", custCrit);
            if (custInfoDO.isEmpty()) {
                CustomerInfoUtil.out.severe("Default customer id is null. This method should not be called");
            }
            return (Long)custInfoDO.getFirstValue("CustomerInfo", "CUSTOMER_ID");
        }
        catch (final Exception e) {
            CustomerInfoUtil.out.log(Level.SEVERE, "Exception in fetching default customerkey", e);
            return null;
        }
    }
    
    public ArrayList getCustomerIdList() throws Exception {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerIdList";
        final ArrayList customerList = new ArrayList();
        final String isSummaryPage = CustomerInfoThreadLocal.getSummaryPage();
        CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> isSummaryPage : " + isSummaryPage);
        if (isSummaryPage != null && isSummaryPage.equals("true")) {
            if (ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName() != null) {
                final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final ArrayList custList = this.getCustomersForLoginUser(userId);
                for (final HashMap map : custList) {
                    customerList.add(map.get("CUSTOMER_ID"));
                }
            }
        }
        else {
            final Long custid = this.getCustomerId();
            if (custid != null) {
                customerList.add(custid);
            }
        }
        if (customerList != null) {
            for (int i = 0; i < customerList.size(); ++i) {
                CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> customerList(" + i + ") : " + customerList.get(i));
            }
        }
        return customerList;
    }
    
    public ArrayList<Long> getCustomerIDsForLogIn(final Long loginId) {
        final Long userId = DMUserHandler.getUserIdForLoginId(loginId);
        final ArrayList<Long> customerList = new ArrayList<Long>();
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        if (!this.isMSP()) {
            customerList.add(this.getDefaultCustomer());
            return customerList;
        }
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("LoginUserCustomerMapping"));
            final Criteria cri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userId, 0);
            query.addSelectColumn(new Column("LoginUserCustomerMapping", "CUSTOMER_ID"));
            query.addSelectColumn(new Column("LoginUserCustomerMapping", "DC_USER_ID"));
            query.setCriteria(cri);
            final DataObject resultDO = SyMUtil.getReadOnlyPersistence().get(query);
            final Iterator customerItr = resultDO.getRows("LoginUserCustomerMapping");
            while (customerItr.hasNext()) {
                final Row row = customerItr.next();
                final Long customerId = (Long)row.get("CUSTOMER_ID");
                customerList.add(customerId);
            }
        }
        catch (final Exception ex) {
            CustomerInfoUtil.out.log(Level.SEVERE, "Caught exception while Fetching customer Ids mapped To Login Id ", ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return customerList;
    }
    
    public Long getCustomerId() {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerId";
        if (this.isMSP()) {
            final String isSummaryPage = CustomerInfoThreadLocal.getSummaryPage();
            CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> isSummaryPage : " + isSummaryPage);
            Long customerId = null;
            if (isSummaryPage != null && isSummaryPage.equals("false")) {
                customerId = Long.parseLong(CustomerInfoThreadLocal.getCustomerId());
                CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + "customer id from Thread local --> customerId : " + customerId);
            }
            return customerId;
        }
        if (isSAS()) {
            final Long customerid = ApiFactoryProvider.getUtilAccessAPI().getCustomerID();
            CustomerInfoUtil.out.fine("IN SAS EDITION Default customer id is   " + customerid);
            return customerid;
        }
        return this.getDefaultCustomer();
    }
    
    public Long[] getCustomers() {
        try {
            final ArrayList customers = this.getCustomerIdList();
            final Long[] customerArr = customers.toArray(new Long[customers.size()]);
            return customerArr;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getCustomerCritForACSQLString(final String colAlias) {
        String custCrit = "";
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerCritForACSQLString";
        try {
            final ArrayList customerList = this.getCustomerIdList();
            if (customerList != null) {
                if (customerList.size() == 1) {
                    custCrit = colAlias + " = " + customerList.get(0);
                }
                else if (customerList.size() > 1) {
                    custCrit = colAlias + " in (";
                    String customers = customerList.get(0).toString();
                    for (int i = 1; i < customerList.size(); ++i) {
                        customers = customers + "," + customerList.get(i);
                    }
                    custCrit = custCrit + customers + ") ";
                }
            }
            CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> custCrit : " + custCrit);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return custCrit;
    }
    
    public List getCustomerInfoList() throws SyMException {
        final List custInfoList = new ArrayList();
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerInfoList";
        try {
            final Criteria crit = null;
            final DataObject cmInfoDO = SyMUtil.getPersistence().get("CustomerInfo", crit);
            final Iterator cmInfoRows = cmInfoDO.getRows("CustomerInfo");
            while (cmInfoRows.hasNext()) {
                final Row cmInfoRow = cmInfoRows.next();
                final Properties prop = new Properties();
                ((Hashtable<String, Object>)prop).put("CUSTOMER_ID", cmInfoRow.get("CUSTOMER_ID"));
                ((Hashtable<String, Object>)prop).put("CUSTOMER_NAME", cmInfoRow.get("CUSTOMER_NAME"));
                ((Hashtable<String, Object>)prop).put("CUSTOMER_EMAIL", cmInfoRow.get("CUSTOMER_EMAIL"));
                custInfoList.add(prop);
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customer info list", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        return custInfoList;
    }
    
    public Long[] getCustomerIdsFromDB() throws SyMException {
        Long[] customerIds = null;
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerIdsFromDB";
        try {
            final Criteria crit = null;
            final DataObject cmInfoDO = SyMUtil.getPersistence().get("CustomerInfo", crit);
            if (!cmInfoDO.isEmpty()) {
                final int size = cmInfoDO.size("CustomerInfo");
                customerIds = new Long[size];
                final Iterator cmInfoRows = cmInfoDO.getRows("CustomerInfo");
                int count = 0;
                while (cmInfoRows.hasNext()) {
                    final Row cmInfoRow = cmInfoRows.next();
                    customerIds[count] = (Long)cmInfoRow.get("CUSTOMER_ID");
                    ++count;
                }
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customer info list", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        return customerIds;
    }
    
    public ArrayList getCreatedCustomerInfoList() throws SyMException {
        final ArrayList custInfoList = new ArrayList();
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerInfoList";
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final DataObject cmInfoDO = SyMUtil.getPersistence().get("CustomerInfo", (Criteria)null);
            final Iterator cmInfoRows = cmInfoDO.getRows("CustomerInfo");
            while (cmInfoRows.hasNext()) {
                final Row cmInfoRow = cmInfoRows.next();
                final HashMap customerMap = new HashMap();
                customerMap.put("CUSTOMER_ID", cmInfoRow.get("CUSTOMER_ID"));
                customerMap.put("CUSTOMER_NAME", cmInfoRow.get("CUSTOMER_NAME"));
                custInfoList.add(customerMap);
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customer info list", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return custInfoList;
    }
    
    public String getCreatedCustomerIdsAsString() throws SyMException {
        final ArrayList custInfoList = this.getCreatedCustomerInfoList();
        final Iterator customerList = custInfoList.iterator();
        StringBuffer customerIds = new StringBuffer("");
        while (customerList.hasNext()) {
            final HashMap customerMap = customerList.next();
            if (customerIds.toString().equals("")) {
                customerIds = customerIds.append(String.valueOf(customerMap.get("CUSTOMER_ID")));
            }
            else {
                customerIds = customerIds.append(",").append(String.valueOf(customerMap.get("CUSTOMER_ID")));
            }
        }
        return customerIds.toString();
    }
    
    public String getCustomerNameFromID(final Long customerID) throws SyMException {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerNameFromID";
        String customerName = "";
        try {
            final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject cmInfoDO = SyMUtil.getPersistence().get("CustomerInfo", crit);
            final Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                customerName = (String)cmInfoRow.get("CUSTOMER_NAME");
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customerName", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> customerName from DB : " + customerName);
        return customerName;
    }
    
    public Long getCustomerIDForResID(final Long resourceID) {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerIDForResID";
        Long customerID = null;
        try {
            final Criteria crit = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
            customerID = this.getCustomerIDForResource(crit);
        }
        catch (final SyMException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customerID from resourceID", ex);
        }
        return customerID;
    }
    
    public Long getCustomerIDForResource(final Criteria crit) throws SyMException {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerNameFromID";
        Long customerID = null;
        try {
            final DataObject cmInfoDO = this.getCustomerInfoDO(crit);
            final Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                customerID = (Long)cmInfoRow.get("CUSTOMER_ID");
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customerName", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> customerID : " + customerID);
        return customerID;
    }
    
    public ArrayList getCustIDsForResList(final List resIDs) throws SyMException {
        final String sourceMethod = "CustomerInfoUtilFW::getCustIDsForResList";
        final ArrayList custIDs = new ArrayList();
        try {
            final Criteria crit = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resIDs.toArray(), 8);
            final DataObject cmInfoDO = this.getCustomerInfoDO(crit);
            final Iterator cmInfoRows = cmInfoDO.getRows("CustomerInfo");
            while (cmInfoRows.hasNext()) {
                final Row cmInfoRow = cmInfoRows.next();
                final Long customerID = (Long)cmInfoRow.get("CUSTOMER_ID");
                custIDs.add(customerID);
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customerName", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> customerIDs : " + custIDs);
        return custIDs;
    }
    
    public Properties getCustomerDetails(final Long customerID) throws DataAccessException {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerDetails";
        final Properties prop = new Properties();
        try {
            final String mspName = "DC_MSP";
            final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject custInfoDO = SyMUtil.getPersistence().get("CustomerInfo", crit);
            final Row cmInfoRow = custInfoDO.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                ((Hashtable<String, Object>)prop).put("CUSTOMER_ID", cmInfoRow.get("CUSTOMER_ID"));
                ((Hashtable<String, Object>)prop).put("CUSTOMER_NAME", cmInfoRow.get("CUSTOMER_NAME"));
                ((Hashtable<String, String>)prop).put("MSP_NAME", mspName);
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customerName", (Throwable)ex);
        }
        CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> customerInfo prop : " + prop);
        return prop;
    }
    
    public Properties getAllDetailsOfCustomer(final Long customerID) throws DataAccessException {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerAddress";
        final Properties prop = new Properties();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0);
            query.addJoin(new Join("CustomerInfo", "CustomerAddress", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.setCriteria(crit);
            final DataObject custInfoDO = SyMUtil.getPersistence().get(query);
            final Row cmInfoRow = custInfoDO.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                ((Hashtable<String, Object>)prop).put("CUSTOMER_ID", cmInfoRow.get("CUSTOMER_ID"));
                ((Hashtable<String, Object>)prop).put("CUSTOMER_NAME", cmInfoRow.get("CUSTOMER_NAME"));
                final Boolean isCustomizedLogo = (Boolean)cmInfoRow.get("IS_CUSTOM_LOGO_ENABLE");
                ((Hashtable<String, Boolean>)prop).put("IS_CUSTOM_LOGO_ENABLE", isCustomizedLogo != null && (boolean)(boolean)isCustomizedLogo);
                ((Hashtable<String, Object>)prop).put("CUSTOMER_EMAIL", cmInfoRow.get("CUSTOMER_EMAIL"));
                ((Hashtable<String, Object>)prop).put("TIMEZONE", (cmInfoRow.get("TIMEZONE") == null) ? "" : cmInfoRow.get("TIMEZONE"));
            }
            final Row cmAdrRow = custInfoDO.getRow("CustomerAddress");
            if (cmAdrRow != null) {
                ((Hashtable<String, Object>)prop).put("COMPANY_NAME", cmAdrRow.get("COMPANY_NAME"));
                ((Hashtable<String, Object>)prop).put("DESCRIPTION", cmAdrRow.get("DESCRIPTION"));
                ((Hashtable<String, Object>)prop).put("DOOR_NO", cmAdrRow.get("DOOR_NO"));
                ((Hashtable<String, Object>)prop).put("STREET", cmAdrRow.get("STREET"));
                ((Hashtable<String, Object>)prop).put("LANDMARK", cmAdrRow.get("LANDMARK"));
                ((Hashtable<String, Object>)prop).put("CITY", cmAdrRow.get("CITY"));
                ((Hashtable<String, Object>)prop).put("POSTALCODE", cmAdrRow.get("POSTALCODE"));
                ((Hashtable<String, Object>)prop).put("STATE", cmAdrRow.get("STATE"));
                ((Hashtable<String, Object>)prop).put("COUNTRY", cmAdrRow.get("COUNTRY"));
                ((Hashtable<String, Object>)prop).put("LANDLINE", cmAdrRow.get("LANDLINE"));
                ((Hashtable<String, Object>)prop).put("FAX", cmAdrRow.get("FAX"));
                ((Hashtable<String, Object>)prop).put("WEB_URL", cmAdrRow.get("WEB_URL"));
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customer details & address", (Throwable)ex);
        }
        CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> customerInfo prop : " + prop);
        return prop;
    }
    
    public Properties getCustomerInfo(final Long customerID) throws DataAccessException {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerInfo";
        final Properties prop = new Properties();
        boolean flagSet = false;
        try {
            final String mspName = "DC_MSP";
            final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addSelectColumn(new Column((String)null, "*"));
            query.setCriteria(crit);
            final DataObject cmInfoDO = SyMUtil.getPersistence().get(query);
            final Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                final List colList = cmInfoRow.getColumns();
                for (final String colName : colList) {
                    final Object colValue = cmInfoRow.get(colName);
                    if (colValue != null) {
                        ((Hashtable<String, Object>)prop).put(colName, colValue);
                    }
                }
                ((Hashtable<String, String>)prop).put("MSP_NAME", mspName);
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customerName", (Throwable)ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> customerInfo prop : " + prop);
        return prop;
    }
    
    public DataObject getCustomerInfoDO(final Criteria crit) throws DataAccessException {
        DataObject customerInfoDO = null;
        final String baseTbl = "CustomerInfo";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTbl));
        query.addJoin(new Join(baseTbl, "Resource", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        query.addSelectColumn(new Column((String)null, "*"));
        if (crit != null) {
            query.setCriteria(crit);
        }
        customerInfoDO = SyMUtil.getPersistence().get(query);
        return customerInfoDO;
    }
    
    public void addDefaultCustomerToUser(final Long userId) throws Exception {
        final String sourceMethod = "CustomerInfoUtilFW::addDefaultCustomerToUser";
        CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> userID :" + userId);
        final Long defaultCustomerId = this.getDefaultCustomer();
        this.addCustomerToUserMapping(userId, defaultCustomerId);
    }
    
    public void addCustomerToUserMapping(final Long userId, final Long customerId) throws Exception {
        final DataObject addDO = (DataObject)new WritableDataObject();
        final Row resRow = new Row("LoginUserCustomerMapping");
        resRow.set("DC_USER_ID", (Object)userId);
        resRow.set("CUSTOMER_ID", (Object)customerId);
        addDO.addRow(resRow);
        SyMUtil.getPersistence().add(addDO);
    }
    
    public void addCustomerToUsersMapping(final String userList, final Long customerID) throws Exception {
        final String sourceMethod = "CustomerInfoUtilFW::addCustomerToUsersMapping";
        try {
            final DataObject addDO = (DataObject)new WritableDataObject();
            final String[] sUserIDs = userList.split(",");
            for (int i = 0; i < sUserIDs.length; ++i) {
                final Row resRow = new Row("LoginUserCustomerMapping");
                resRow.set("DC_USER_ID", (Object)Long.valueOf(sUserIDs[i].trim()));
                resRow.set("CUSTOMER_ID", (Object)customerID);
                addDO.addRow(resRow);
                CustomerInfoUtil.out.log(Level.INFO, sourceMethod + " --> row added String customer: " + resRow);
            }
            SyMUtil.getPersistence().add(addDO);
        }
        catch (final Exception ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while adding customer-user relation in DB ...", ex);
            throw ex;
        }
    }
    
    public void addCustomerToUsersMapping(final List<Long> lUserId, final Long customerId) throws Exception {
        final DataObject addDO = (DataObject)new WritableDataObject();
        final Row resRow = new Row("LoginUserCustomerMapping");
        resRow.set("CUSTOMER_ID", (Object)customerId);
        for (final Long userId : lUserId) {
            resRow.set("DC_USER_ID", (Object)userId);
            addDO.addRow(resRow);
        }
        SyMUtil.getPersistence().add(addDO);
    }
    
    public void addCustomersToUserMapping(final Long userID, final String customerList) throws DataAccessException {
        final String sourceMethod = "CustomerInfoUtilFW::addCustomersToUser";
        try {
            final DataObject addDO = (DataObject)new WritableDataObject();
            final String[] sCustomerIDs = customerList.split(",");
            for (int i = 0; i < sCustomerIDs.length; ++i) {
                final Row resRow = new Row("LoginUserCustomerMapping");
                resRow.set("DC_USER_ID", (Object)userID);
                resRow.set("CUSTOMER_ID", (Object)Long.valueOf(sCustomerIDs[i]));
                addDO.addRow(resRow);
                CustomerInfoUtil.out.log(Level.INFO, sourceMethod + " --> row added String customer: " + resRow);
            }
            SyMUtil.getPersistence().add(addDO);
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while adding customer-user relation in DB ...", (Throwable)ex);
            throw ex;
        }
    }
    
    public void removeUserFromCustomerMapping(final Long loginID) throws DataAccessException {
        final Criteria crtiteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)loginID, 0);
        DataAccess.delete(crtiteria);
    }
    
    public void addCustomersToUserMapping(final Long userID, final List customerList) {
        final String sourceMethod = "CustomerInfoUtilFW::addCustomersToUser";
        try {
            final String baseTbl = "LoginUserCustomerMapping";
            final Criteria crit = new Criteria(new Column(baseTbl, "DC_USER_ID"), (Object)userID, 0);
            DataObject resultDO = SyMUtil.getPersistence().get(baseTbl, crit);
            if (resultDO != null && !resultDO.isEmpty()) {
                final Iterator rows = resultDO.getRows(baseTbl);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final Long customerID = (Long)row.get("CUSTOMER_ID");
                    if (customerList.contains(customerID)) {
                        customerList.remove(customerID);
                    }
                }
            }
            resultDO = SyMUtil.getPersistence().constructDataObject();
            if (customerList != null && !customerList.isEmpty()) {
                for (int i = 0; i < customerList.size(); ++i) {
                    final Row resRow = new Row(baseTbl);
                    resRow.set("DC_USER_ID", (Object)userID);
                    resRow.set("CUSTOMER_ID", (Object)Long.parseLong(customerList.get(i).toString()));
                    resultDO.addRow(resRow);
                    CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> row added : " + resRow);
                }
            }
            resultDO = SyMUtil.getPersistence().add(resultDO);
        }
        catch (final Exception ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while adding customer-user relation in DB ...", ex);
        }
    }
    
    public ArrayList getCustomerDetailsForUser(final Long userId) throws Exception {
        final ArrayList customerListForUser = new ArrayList();
        if (!this.isMSP()) {
            final Properties prop = this.getCustomerDetails(this.getDefaultCustomer());
            final HashMap customerMap = new HashMap();
            customerMap.put("CUSTOMER_ID", ((Hashtable<K, Object>)prop).get("CUSTOMER_ID"));
            customerMap.put("CUSTOMER_NAME", ((Hashtable<K, Object>)prop).get("CUSTOMER_NAME"));
            customerListForUser.add(customerMap);
            return customerListForUser;
        }
        return this.getCustomersForUser(userId);
    }
    
    public ArrayList getCustomersForUser(final Long userId) throws Exception {
        final ArrayList customerListForUser = new ArrayList();
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addJoin(new Join("CustomerInfo", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            query.addSelectColumn(new Column((String)null, "*"));
            final Criteria cri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userId, 0);
            query.setCriteria(cri);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"), true);
            query.addSortColumn(sortColumn);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            final Iterator outerIt = resultDO.getRows("CustomerInfo");
            while (outerIt.hasNext()) {
                final Row custRow = outerIt.next();
                final HashMap customerMap = new HashMap();
                customerMap.put("CUSTOMER_ID", custRow.get("CUSTOMER_ID"));
                customerMap.put("CUSTOMER_NAME", custRow.get("CUSTOMER_NAME"));
                customerListForUser.add(customerMap);
            }
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return customerListForUser;
    }
    
    public ArrayList getCustomersForLoginUser(final Long userID) throws Exception {
        final String DC_LGOIN_TO_CUSTOMER_CACHE_KEY = "DC_LGOIN_TO_CUSTOMER_CACHE_KEY_" + userID;
        final Object custListFromCache = ApiFactoryProvider.getCacheAccessAPI().getCache(DC_LGOIN_TO_CUSTOMER_CACHE_KEY, 2);
        if (custListFromCache != null && !((ArrayList)custListFromCache).isEmpty()) {
            return (ArrayList)custListFromCache;
        }
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addJoin(new Join("CustomerInfo", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            query.addJoin(new Join("LoginUserCustomerMapping", "AaaUser", new String[] { "DC_USER_ID" }, new String[] { "USER_ID" }, 2));
            query.addSelectColumn(new Column("CustomerInfo", "CUSTOMER_ID"));
            query.addSelectColumn(new Column("CustomerInfo", "CUSTOMER_NAME"));
            query.addSelectColumn(new Column("LoginUserCustomerMapping", "CUSTOMER_ID"));
            query.addSelectColumn(new Column("LoginUserCustomerMapping", "DC_USER_ID"));
            query.addSelectColumn(new Column("AaaUser", "USER_ID"));
            final Criteria cri = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)userID, 0);
            query.setCriteria(cri);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"), true);
            query.addSortColumn(sortColumn);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            final ArrayList customerListForUser = new ArrayList();
            final Iterator outerIt = resultDO.getRows("CustomerInfo");
            while (outerIt.hasNext()) {
                final Row custRow = outerIt.next();
                final HashMap customerMap = new HashMap();
                customerMap.put("CUSTOMER_ID", custRow.get("CUSTOMER_ID"));
                customerMap.put("CUSTOMER_NAME", custRow.get("CUSTOMER_NAME"));
                customerListForUser.add(customerMap);
            }
            final ArrayList affectedTableList = new ArrayList();
            affectedTableList.add("LoginUserCustomerMapping");
            affectedTableList.add("AaaUser");
            affectedTableList.add("AaaLogin");
            affectedTableList.add("CustomerInfo");
            CustomerInfoUtil.out.log(Level.INFO, "CustomerInfoUtil: setting customerListForUser in DC_LGOIN_TO_CUSTOMER_CACHE_KEY " + customerListForUser.toString());
            ApiFactoryProvider.getCacheAccessAPI().putCache(DC_LGOIN_TO_CUSTOMER_CACHE_KEY, customerListForUser, affectedTableList, 2);
            return customerListForUser;
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
    }
    
    @Deprecated
    public static boolean isPMP() {
        if (isSAS()) {
            return ApiFactoryProvider.getUtilAccessAPI().getCustomerProduct().equalsIgnoreCase("PMP");
        }
        CustomerInfoUtil.out.log(Level.FINEST, "-----Inside isPMP method. Reading from SystemParams and general Props to set the value");
        final String show_only_patch = SyMUtil.getSyMParameterFromDB("show_only_patch");
        final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
        if ((productCodeList != null && productCodeList.contains("PMP")) || (productCodeList != null && productCodeList.contains("PMPODEE")) || (show_only_patch != null && show_only_patch.equals("true"))) {
            setIsPMPProperty("true");
        }
        else {
            setIsPMPProperty("false");
        }
        return CustomerInfoUtil.isPMP;
    }
    
    @Deprecated
    public static boolean isPMPProduct() {
        if (isSAS()) {
            return isPMP();
        }
        CustomerInfoUtil.out.log(Level.FINEST, "-----Inside isPMPProduct method. Reading from SystemParams and general Props to set the value");
        final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
        if ((productCodeList != null && productCodeList.contains("PMP")) || (productCodeList != null && productCodeList.contains("PMPODEE"))) {
            setIsPMPProductProperty("true");
        }
        else {
            setIsPMPProductProperty("false");
        }
        return CustomerInfoUtil.isPMPProduct;
    }
    
    public static boolean isVMPProduct() {
        if (CustomerInfoUtil.isVMPProduct == null) {
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if ((productCodeList != null && productCodeList.contains("VMP")) || (productCodeList != null && productCodeList.contains("VMPODEE"))) {
                setIsVMPProductProperty("true");
            }
            else {
                setIsVMPProductProperty("false");
            }
        }
        return CustomerInfoUtil.isVMPProduct;
    }
    
    public static boolean isDCPProduct() {
        if (CustomerInfoUtil.isDCPProduct == null) {
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if ((productCodeList != null && productCodeList.contains("DCP")) || (productCodeList != null && productCodeList.contains("DCPODEE"))) {
                setIsDCPProductProperty("true");
            }
            else {
                setIsDCPProductProperty("false");
            }
        }
        return CustomerInfoUtil.isDCPProduct;
    }
    
    public static boolean isACPProduct() {
        if (CustomerInfoUtil.isACPProduct == null) {
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if ((productCodeList != null && productCodeList.contains("ACP")) || (productCodeList != null && productCodeList.contains("ACPODEE"))) {
                setIsACPProductProperty("true");
            }
            else {
                setIsACPProductProperty("false");
            }
        }
        return CustomerInfoUtil.isACPProduct;
    }
    
    public static boolean isUESProduct() {
        if (CustomerInfoUtil.isUESProduct == null) {
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if ((productCodeList != null && productCodeList.contains("UES")) || (productCodeList != null && productCodeList.contains("UESODEE"))) {
                setIsUESProductProperty("true");
            }
            else {
                setIsUESProductProperty("false");
            }
        }
        return CustomerInfoUtil.isUESProduct;
    }
    
    public static boolean isPMPOrPatchEdition() {
        if (isSAS()) {
            return isPMP();
        }
        return LicenseProvider.getInstance().getProductType().equalsIgnoreCase("Patch") || isPMPProduct();
    }
    
    public boolean isMSP() {
        return ApiFactoryProvider.getUtilAccessAPI().isMSP();
    }
    
    private static Properties readProperties(final String confFileName) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
        }
        catch (final Exception ex) {
            CustomerInfoUtil.out.log(Level.SEVERE, "Caught exception: " + ex);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    public static String getProperty(final String key) {
        String value = "false";
        try {
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String fname = serverHome + File.separator + "conf" + File.separator + "msp_properties.conf";
            final Properties props = readProperties(fname);
            value = props.getProperty(key);
        }
        catch (final Exception ex) {
            CustomerInfoUtil.out.log(Level.WARNING, "Caught exception while getting property from msp_properties file : ", ex);
        }
        return value;
    }
    
    private static void setIsMSPProperty(final String value) {
        ApiFactoryProvider.getUtilAccessAPI().isMSP();
    }
    
    private static void setIsPMPProperty(final String value) {
        if (value != null && value.equals("true")) {
            CustomerInfoUtil.isPMP = Boolean.TRUE;
        }
        else {
            CustomerInfoUtil.isPMP = Boolean.FALSE;
        }
    }
    
    private static void setIsPMPProductProperty(final String value) {
        if (value != null && value.equals("true")) {
            CustomerInfoUtil.isPMPProduct = Boolean.TRUE;
        }
        else {
            CustomerInfoUtil.isPMPProduct = Boolean.FALSE;
        }
    }
    
    private static void setIsVMPProductProperty(final String value) {
        if (value != null && value.equals("true")) {
            CustomerInfoUtil.isVMPProduct = Boolean.TRUE;
        }
        else {
            CustomerInfoUtil.isVMPProduct = Boolean.FALSE;
        }
    }
    
    private static void setIsDCPProductProperty(final String value) {
        if (value != null && value.equals("true")) {
            CustomerInfoUtil.isDCPProduct = Boolean.TRUE;
        }
        else {
            CustomerInfoUtil.isDCPProduct = Boolean.FALSE;
        }
    }
    
    private static void setIsACPProductProperty(final String value) {
        if (value != null && value.equals("true")) {
            CustomerInfoUtil.isACPProduct = Boolean.TRUE;
        }
        else {
            CustomerInfoUtil.isACPProduct = Boolean.FALSE;
        }
    }
    
    private static void setIsUESProductProperty(final String value) {
        if (value != null && value.equals("true")) {
            CustomerInfoUtil.isUESProduct = Boolean.TRUE;
        }
        else {
            CustomerInfoUtil.isUESProduct = Boolean.FALSE;
        }
    }
    
    public static void setMSPProperties() {
        ApiFactoryProvider.getUtilAccessAPI().isMSP();
    }
    
    public static boolean isSAS() {
        if (CustomerInfoUtil.isSAS == null) {
            final String val = getProperty("isSAS");
            setIsSASProperty(val);
            CustomerInfoUtil.out.log(Level.INFO, "ISSAS  : " + CustomerInfoUtil.isSAS);
        }
        return CustomerInfoUtil.isSAS;
    }
    
    private static void setIsSASProperty(final String value) {
        CustomerInfoUtil.isSAS = Boolean.valueOf(value);
    }
    
    public static void setSASProperties() {
        final String val = getProperty("isSAS");
        setIsSASProperty(val);
        CustomerInfoUtil.out.log(Level.INFO, "Inside setSASProperties method isSAS " + CustomerInfoUtil.isSAS);
    }
    
    public static boolean isMDM() {
        if (CustomerInfoUtil.isMDM == null) {
            CustomerInfoUtil.out.log(Level.FINEST, "-----Inside isMDM method, isMDM value null. Hence reading from general_properties file and set the value");
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if ((productCodeList != null && productCodeList.contains("DCEE")) || productCodeList.contains("DCMSP") || productCodeList.contains("MDMP") || productCodeList.contains("MDMODEE") || productCodeList.contains("MDMODMSP")) {
                CustomerInfoUtil.isMDM = true;
            }
            else {
                CustomerInfoUtil.isMDM = false;
            }
        }
        return CustomerInfoUtil.isMDM;
    }
    
    public static boolean isDC() {
        if (CustomerInfoUtil.isDC == null) {
            CustomerInfoUtil.out.log(Level.FINEST, "-----Inside isDC method, isMDM value null. Hence reading from general_properties file and set the value");
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if ((productCodeList != null && productCodeList.contains("DCEE")) || productCodeList.contains("DCMSP") || productCodeList.contains("DCODEE") || productCodeList.contains("DCODMSP")) {
                CustomerInfoUtil.isDC = true;
            }
            else {
                CustomerInfoUtil.isDC = false;
            }
        }
        return CustomerInfoUtil.isDC;
    }
    
    public static boolean isMDMP() {
        if (CustomerInfoUtil.isMDMP == null) {
            CustomerInfoUtil.out.log(Level.FINEST, "-----Inside isDC method, isMDM value null. Hence reading from general_properties file and set the value");
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if ((productCodeList != null && productCodeList.contains("MDMP")) || productCodeList.contains("MDMPMSP") || productCodeList.contains("MDMODEE") || productCodeList.contains("MDMODMSP")) {
                CustomerInfoUtil.isMDMP = true;
            }
            else {
                CustomerInfoUtil.isMDMP = false;
            }
        }
        return CustomerInfoUtil.isMDMP;
    }
    
    public static boolean isOSDProduct() {
        if (CustomerInfoUtil.isOSDProduct == null) {
            CustomerInfoUtil.out.log(Level.FINEST, "-----Inside isOSDProduct method, isOSDProduct value null. Hence reading from general_properties file and set the value");
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if (productCodeList != null && productCodeList.contains("OSD")) {
                CustomerInfoUtil.isOSDProduct = true;
            }
            else {
                CustomerInfoUtil.isOSDProduct = false;
            }
        }
        return CustomerInfoUtil.isOSDProduct;
    }
    
    public static boolean isBSPProduct() {
        if (CustomerInfoUtil.isBSPProduct == null) {
            CustomerInfoUtil.out.log(Level.FINEST, "-----Inside isBSPProduct method, isBSPProduct value null. Hence reading from general_properties file and set the value");
            final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
            if (productCode != null && productCode.equals("BSP")) {
                CustomerInfoUtil.isBSPProduct = true;
            }
            else {
                CustomerInfoUtil.isBSPProduct = false;
            }
        }
        return CustomerInfoUtil.isBSPProduct;
    }
    
    public boolean doesProductSupportMDM() {
        return !this.isToolsAddon() && !isPMPOrPatchEdition() && !this.isRAP() && !isVMPProduct() && !isACPProduct() && !isDCPProduct() && !isOSDProduct();
    }
    
    public boolean isRAP() {
        if (isSAS()) {
            return ApiFactoryProvider.getUtilAccessAPI().getCustomerProduct().toUpperCase().contains("RAP");
        }
        if (CustomerInfoUtil.isRAP == null) {
            CustomerInfoUtil.out.log(Level.FINEST, "----- Inside isRAP method, isRAP value is null. Hence reading from general_properties files and set the value");
            final ArrayList productCodeList = EMSProductUtil.getEMSProductCode();
            if (productCodeList != null && (productCodeList.contains("RAP") || productCodeList.contains("RAPODEE"))) {
                this.setIsRAPProperty("true");
            }
            else {
                this.setIsRAPProperty("false");
            }
        }
        return CustomerInfoUtil.isRAP;
    }
    
    public boolean isToolsAddon() {
        return LicenseProvider.getInstance().getProductCategoryString().equalsIgnoreCase("TOOLSADDON");
    }
    
    private void setIsRAPProperty(final String value) {
        if (value != null && value.equals("true")) {
            CustomerInfoUtil.isRAP = Boolean.TRUE;
        }
        else {
            CustomerInfoUtil.isRAP = Boolean.FALSE;
        }
    }
    
    public String getCustomerIdListFromNames(final String[] customerNames) {
        String customerNamesStr = "";
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addSelectColumn(new Column((String)null, "*"));
            Criteria cri = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"), (Object)"DC_CUSTOMER", 1);
            if (customerNames != null) {
                final Criteria cri2 = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"), (Object)customerNames, 8);
                cri = cri.and(cri2);
            }
            query.setCriteria(cri);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            final Iterator outerIt = resultDO.getRows("CustomerInfo");
            while (outerIt.hasNext()) {
                final Row custRow = outerIt.next();
                final Long customerId = (Long)custRow.get("CUSTOMER_ID");
                customerNamesStr = customerNamesStr + customerId.toString() + ",";
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            return customerNamesStr;
        }
        return customerNamesStr.substring(0, customerNamesStr.length() - 1);
    }
    
    public Long addOrUpdateCustomer(final String oldCustomerName, final String customerName, final String emailID) throws Exception {
        final String sourceMethod = "CustomerInfoUtilFW::addOrUpdateCustomer";
        Long customerId = new Long(-2L);
        try {
            final Long oldCustomerID = getInstance().getCustomerId(oldCustomerName);
            if (!oldCustomerID.equals(new Long(-1L))) {
                if (!this.anotherCustomerHasSameName(oldCustomerID, customerName)) {
                    final Properties customerInfo = new Properties();
                    ((Hashtable<String, String>)customerInfo).put("CUSTOMER_EMAIL", emailID);
                    this.updateCustomer(oldCustomerID, customerName, customerInfo);
                }
                else {
                    CustomerInfoUtil.logger.log(Level.WARNING, sourceMethod + "New Customer Name already mapped to some other customer name {0} ", customerName);
                }
            }
            else {
                final Properties customerInfo = new Properties();
                ((Hashtable<String, String>)customerInfo).put("CUSTOMER_EMAIL", emailID);
                customerId = this.addCustomer(customerName, customerInfo);
            }
        }
        catch (final Exception ex) {
            DCEventLogUtil.getInstance().addEvent(802, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), null, "Exception while creating Customer '" + customerName + "'.", true, customerId);
            CustomerInfoUtil.logger.log(Level.WARNING, sourceMethod + "--> Exception while adding customer to DB", ex);
            throw new SyMException(1001, ex);
        }
        return customerId;
    }
    
    public Long updateCustomer(final Properties customerInfo) throws DataAccessException, Exception {
        final String sourceMethod = "CustomerInfoUtilFW::updateCustomerToDB";
        final Long customerID = Long.parseLong(((Hashtable<K, Object>)customerInfo).get("CUSTOMER_ID").toString());
        final String customerName = ((Hashtable<K, String>)customerInfo).get("CUSTOMER_NAME");
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("CustomerInfo"));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.addJoin(new Join("CustomerInfo", "CustomerAddress", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            final DataObject dataObject = SyMUtil.getPersistence().get((SelectQuery)selectQuery);
            final Row cmInfoRow = dataObject.getRow("CustomerInfo");
            if (cmInfoRow == null) {
                return -1L;
            }
            this.addCustomerInfoDetails(cmInfoRow, customerName, customerInfo);
            dataObject.updateRow(cmInfoRow);
            final Row cmAdrRow = dataObject.getRow("CustomerAddress");
            this.addCustomerAddressDetails(cmAdrRow, customerInfo);
            dataObject.updateRow(cmAdrRow);
            SyMUtil.getPersistence().update(dataObject);
            final Properties customerProperties = getInstance().getCustomerInfo(customerID);
            final CustomerEvent customerEvent = new CustomerEvent(customerID, customerProperties);
            CustomerHandler.getInstance().invokeCustomerListeners(customerEvent, 1003);
            return customerID;
        }
        catch (final DataAccessException e) {
            CustomerInfoUtil.logger.log(Level.SEVERE, sourceMethod + " --> DataAccessException while updating customer to DB " + customerID, (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            CustomerInfoUtil.logger.log(Level.SEVERE, sourceMethod + " --> Exception while updating customer to DB", e2);
            throw e2;
        }
    }
    
    @Deprecated
    public Long updateCustomer(final Long customerId, final String customerName, final Properties customerInfo) throws SyMException {
        final String sourceMethod = "CustomerInfoAction::updateCustomerToDB";
        DataObject cmInfoDO = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addSelectColumn(new Column((String)null, "*"));
            final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            query.addJoin(new Join("CustomerInfo", "CustomerAddress", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            query.setCriteria(crit);
            cmInfoDO = SyMUtil.getPersistence().get(query);
            final Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                this.addCustomerInfoDetails(cmInfoRow, customerName, customerInfo);
                cmInfoDO.updateRow(cmInfoRow);
                Row cmAdrRow = cmInfoDO.getRow("CustomerAddress");
                boolean isAddrRowNew = false;
                if (cmAdrRow == null) {
                    isAddrRowNew = true;
                    cmAdrRow = new Row("CustomerAddress");
                    cmAdrRow.set("CUSTOMER_ID", (Object)customerId);
                }
                this.addCustomerAddressDetails(cmAdrRow, customerInfo);
                if (isAddrRowNew) {
                    cmInfoDO.addRow(cmAdrRow);
                }
                else {
                    cmInfoDO.updateRow(cmAdrRow);
                }
                SyMUtil.getPersistence().update(cmInfoDO);
                final Properties customerProperties = getInstance().getCustomerInfo(customerId);
                final CustomerEvent customerEvent = new CustomerEvent(customerId, customerProperties);
                CustomerHandler.getInstance().invokeCustomerListeners(customerEvent, 1003);
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.logger.log(Level.WARNING, sourceMethod + "--> DataAccessException while updating customer to DB " + customerId, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception e) {
            CustomerInfoUtil.logger.log(Level.WARNING, sourceMethod + "--> Exception while updating customer to DB", e);
        }
        return customerId;
    }
    
    public Long addCustomer(final Properties customerInfo) throws DataAccessException, Exception {
        final String sourceMethod = "CustomerInfoUtilFW::addCustomer";
        Long customerID = -2L;
        final String customerName = ((Hashtable<K, String>)customerInfo).get("CUSTOMER_NAME");
        try {
            final Criteria criteria = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"), (Object)customerName, 0, (boolean)Boolean.FALSE);
            DataObject dataObject = SyMUtil.getPersistence().get("CustomerInfo", criteria);
            Row cmInfoRow = dataObject.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                return -1L;
            }
            cmInfoRow = new Row("CustomerInfo");
            this.addCustomerInfoDetails(cmInfoRow, customerName, customerInfo);
            dataObject.addRow(cmInfoRow);
            final Row cmAdrRow = new Row("CustomerAddress");
            cmAdrRow.set("CUSTOMER_ID", cmInfoRow.get("CUSTOMER_ID"));
            this.addCustomerAddressDetails(cmAdrRow, customerInfo);
            dataObject.addRow(cmAdrRow);
            dataObject = SyMUtil.getPersistence().add(dataObject);
            customerID = (Long)dataObject.getFirstValue("CustomerInfo", "CUSTOMER_ID");
            final Properties customerProperties = getInstance().getCustomerInfo(customerID);
            final CustomerEvent customerEvent = new CustomerEvent(customerID, customerProperties);
            CustomerHandler.getInstance().invokeCustomerListeners(customerEvent, 1000);
            return customerID;
        }
        catch (final DataAccessException e) {
            CustomerInfoUtil.logger.log(Level.WARNING, sourceMethod + " --> Exception while adding customer to DB", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            throw new Exception(customerID.toString(), e2);
        }
    }
    
    @Deprecated
    public Long addCustomer(final String customerName, final Properties customerInfo) throws Exception {
        final String sourceMethod = "CustomerInfoUtilFW::addCustomer";
        Long customerId = new Long(-2L);
        try {
            final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_NAME"), (Object)customerName, 0, false);
            DataObject cmInfoDO = SyMUtil.getPersistence().get("CustomerInfo", crit);
            Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
            if (cmInfoRow == null) {
                cmInfoRow = new Row("CustomerInfo");
                this.addCustomerInfoDetails(cmInfoRow, customerName, customerInfo);
                cmInfoDO.addRow(cmInfoRow);
                final Row cmAdrRow = new Row("CustomerAddress");
                cmAdrRow.set("CUSTOMER_ID", cmInfoRow.get("CUSTOMER_ID"));
                this.addCustomerAddressDetails(cmAdrRow, customerInfo);
                cmInfoDO.addRow(cmAdrRow);
                cmInfoDO = SyMUtil.getPersistence().add(cmInfoDO);
                customerId = (Long)cmInfoDO.getFirstValue("CustomerInfo", "CUSTOMER_ID");
                final Properties customerProperties = getInstance().getCustomerInfo(customerId);
                final CustomerEvent customerEvent = new CustomerEvent(customerId, customerProperties);
                CustomerHandler.getInstance().invokeCustomerListeners(customerEvent, 1000);
            }
            else {
                customerId = new Long(-1L);
            }
        }
        catch (final Exception ex) {
            final String i18n = "dc.admin.EXCEPTION__CREATING_CUSTOMER";
            final Object remarksArgs = customerName;
            DCEventLogUtil.getInstance().addEvent(802, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), null, i18n, remarksArgs, true, customerId);
            CustomerInfoUtil.logger.log(Level.WARNING, sourceMethod + "--> Exception while adding customer to DB", ex);
            throw new SyMException(1001, ex);
        }
        return customerId;
    }
    
    private void addCustomerInfoDetails(final Row cmInfoRow, final String customerName, final Properties customerInfo) {
        cmInfoRow.set("CUSTOMER_NAME", (Object)customerName);
        String emailID = ((Hashtable<K, String>)customerInfo).get("CUSTOMER_EMAIL");
        if (emailID == null) {
            emailID = "";
        }
        cmInfoRow.set("CUSTOMER_EMAIL", (Object)emailID);
        String accountHeadName = ((Hashtable<K, String>)customerInfo).get("ACCOUNT_HEAD_NAME");
        if (accountHeadName == null) {
            accountHeadName = "";
        }
        cmInfoRow.set("ACCOUNT_HEAD_NAME", (Object)accountHeadName);
        final String timezone = ((Hashtable<K, String>)customerInfo).get("TIMEZONE");
        if (timezone != null) {
            cmInfoRow.set("TIMEZONE", (Object)timezone);
        }
        else {
            cmInfoRow.set("TIMEZONE", (Object)TimeZone.getDefault().getID());
        }
        final Boolean isNewLogo = ((Hashtable<K, Boolean>)customerInfo).get("IS_CUSTOM_LOGO_ENABLE");
        if (isNewLogo != null) {
            cmInfoRow.set("IS_CUSTOM_LOGO_ENABLE", (Object)isNewLogo);
        }
        SyMUtil.getInstance();
        final long postTime = SyMUtil.getCurrentTimeInMillis();
        cmInfoRow.set("UPDATED_TIME", (Object)postTime);
        if (!StringUtils.isNumeric(cmInfoRow.get("CUSTOMER_ID").toString())) {
            cmInfoRow.set("ADDED_TIME", (Object)postTime);
        }
    }
    
    private void addCustomerAddressDetails(final Row cmAdrRow, final Properties customerInfo) {
        String companyName = ((Hashtable<K, String>)customerInfo).get("COMPANY_NAME");
        if (companyName == null) {
            companyName = "--";
        }
        cmAdrRow.set("COMPANY_NAME", (Object)companyName);
        String description = ((Hashtable<K, String>)customerInfo).get("DESCRIPTION");
        if (description == null) {
            description = "--";
        }
        cmAdrRow.set("DESCRIPTION", (Object)description);
        String doorNo = ((Hashtable<K, String>)customerInfo).get("DOOR_NO");
        if (doorNo == null) {
            doorNo = "--";
        }
        cmAdrRow.set("DOOR_NO", (Object)doorNo);
        String landMark = ((Hashtable<K, String>)customerInfo).get("LANDMARK");
        if (landMark == null) {
            landMark = "--";
        }
        cmAdrRow.set("LANDMARK", (Object)landMark);
        String street = ((Hashtable<K, String>)customerInfo).get("STREET");
        if (street == null) {
            street = "--";
        }
        cmAdrRow.set("STREET", (Object)street);
        String city = ((Hashtable<K, String>)customerInfo).get("CITY");
        if (city == null) {
            city = "--";
        }
        cmAdrRow.set("CITY", (Object)city);
        String postalCode = ((Hashtable<K, String>)customerInfo).get("POSTALCODE");
        if (postalCode == null) {
            postalCode = "--";
        }
        cmAdrRow.set("POSTALCODE", (Object)postalCode);
        String state = ((Hashtable<K, String>)customerInfo).get("STATE");
        if (state == null) {
            state = "--";
        }
        cmAdrRow.set("STATE", (Object)state);
        String country = ((Hashtable<K, String>)customerInfo).get("COUNTRY");
        if (country == null) {
            country = "--";
        }
        cmAdrRow.set("COUNTRY", (Object)country);
        String landLine = ((Hashtable<K, String>)customerInfo).get("LANDLINE");
        if (landLine == null) {
            landLine = "--";
        }
        cmAdrRow.set("LANDLINE", (Object)landLine);
        String fax = ((Hashtable<K, String>)customerInfo).get("FAX");
        if (fax == null) {
            fax = "--";
        }
        cmAdrRow.set("FAX", (Object)fax);
        String url = ((Hashtable<K, String>)customerInfo).get("WEB_URL");
        if (url == null) {
            url = "--";
        }
        cmAdrRow.set("WEB_URL", (Object)url);
    }
    
    public Long getCustomerProperty(final String customerName) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addSelectColumn(new Column((String)null, "*"));
            final Criteria cri = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"), (Object)customerName, 0, false);
            query.setCriteria(cri);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            final Iterator outerIt = resultDO.getRows("CustomerInfo");
            if (outerIt.hasNext()) {
                final Row custRow = outerIt.next();
                return (Long)custRow.get("CUSTOMER_ID");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return -1L;
    }
    
    public Properties getCustomerInfo(final String customerName) throws DataAccessException {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerInfo";
        final Properties prop = new Properties();
        try {
            final String mspName = "DC_MSP";
            final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_NAME"), (Object)customerName, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addSelectColumn(new Column((String)null, "*"));
            query.setCriteria(crit);
            final DataObject cmInfoDO = SyMUtil.getPersistence().get(query);
            final Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                ((Hashtable<String, Object>)prop).put("CUSTOMER_ID", cmInfoRow.get("CUSTOMER_ID"));
                ((Hashtable<String, Object>)prop).put("CUSTOMER_NAME", cmInfoRow.get("CUSTOMER_NAME"));
                ((Hashtable<String, String>)prop).put("MSP_NAME", mspName);
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customerName", (Throwable)ex);
        }
        CustomerInfoUtil.out.log(Level.FINEST, sourceMethod + " --> customerInfo prop : " + prop);
        return prop;
    }
    
    public String getCustomerName(final Long customerId) throws DataAccessException {
        final String sourceMethod = "CustomerInfoUtilFW::getCustomerInfo";
        String customerName = null;
        try {
            final String mspName = "DC_MSP";
            final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            query.addSelectColumn(new Column((String)null, "*"));
            query.setCriteria(crit);
            final DataObject cmInfoDO = SyMUtil.getPersistence().get(query);
            final Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
            if (cmInfoRow != null) {
                customerName = (String)cmInfoRow.get("CUSTOMER_NAME");
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while getting customerName", (Throwable)ex);
        }
        return customerName;
    }
    
    public DataObject getCustomerInfoDOForAPI(final Criteria crit) throws DataAccessException {
        DataObject customerInfoDO = null;
        final String baseTbl = "CustomerInfo";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTbl));
        query.addSelectColumn(new Column((String)null, "*"));
        if (crit != null) {
            query.setCriteria(crit);
        }
        customerInfoDO = SyMUtil.getPersistence().get(query);
        return customerInfoDO;
    }
    
    public Long getFirstCustomerID(final String loginName) throws Exception {
        Long customerId = new Long(-1L);
        final boolean isMSP = this.isMSP();
        if (!isMSP && !isSAS()) {
            getInstance().getDefaultCustomer();
        }
        else {
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final List customerList = getInstance().getCustomersForLoginUser(userId);
            if (customerList.size() > 0) {
                final HashMap firstCustomer = customerList.get(0);
                customerId = firstCustomer.get("CUSTOMER_ID");
            }
        }
        return customerId;
    }
    
    public Long getCustomerId(final String customerName) {
        Long customerId = new Long(-1L);
        try {
            final boolean isMSP = this.isMSP();
            if (!isMSP && !isSAS()) {
                getInstance().getDefaultCustomer();
            }
            else {
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
                query.addSelectColumn(new Column((String)null, "*"));
                final Criteria cri = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"), (Object)customerName, 0, false);
                query.setCriteria(cri);
                final DataObject resultDO = SyMUtil.getPersistence().get(query);
                if (!resultDO.isEmpty()) {
                    final Row cmInfoRow = resultDO.getRow("CustomerInfo");
                    if (cmInfoRow != null) {
                        customerId = (Long)cmInfoRow.get("CUSTOMER_ID");
                    }
                }
            }
        }
        catch (final Exception e) {
            CustomerInfoUtil.out.log(Level.SEVERE, "Exception occurred while getting customer id", e);
        }
        return customerId;
    }
    
    public boolean anotherCustomerHasSameName(final Long customerId, final String customerName) {
        boolean anotherCustomerHasSameName = true;
        try {
            Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_NAME"), (Object)customerName, 0, false);
            if (customerId != null) {
                crit = crit.and(new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerId, 1));
            }
            final DataObject cmInfoDO = SyMUtil.getPersistence().get("CustomerInfo", crit);
            final Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
            if (cmInfoRow == null) {
                anotherCustomerHasSameName = false;
            }
        }
        catch (final Exception ex) {
            anotherCustomerHasSameName = true;
        }
        return anotherCustomerHasSameName;
    }
    
    public String createAndGetCustomerIDS(final String[] customerNames) {
        final StringBuffer stbf = new StringBuffer();
        try {
            for (int i = 0; i < customerNames.length; ++i) {
                final String customerName = customerNames[i];
                Long customerID = getInstance().getCustomerId(customerName);
                if (customerID.equals(new Long(-1L))) {
                    customerID = this.addCustomer(customerName, new Properties());
                }
                stbf.append(customerID);
                if (i < customerNames.length - 1) {
                    stbf.append(",");
                }
            }
        }
        catch (final Exception e) {
            CustomerInfoUtil.logger.log(Level.SEVERE, "Exception occurred while creating customers from names", e);
        }
        return stbf.toString();
    }
    
    public ArrayList getUsersForCustomer(final Long customerId) throws Exception {
        final ArrayList usersListForcustomer = new ArrayList();
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
            query.addSelectColumn(new Column((String)null, "*"));
            final Join join = new Join("AaaLogin", "LoginUserCustomerMapping", new String[] { "USER_ID" }, new String[] { "DC_USER_ID" }, 2);
            final Criteria cri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerId, 0);
            query.setCriteria(cri);
            query.addJoin(join);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            final Iterator outerIt = resultDO.getRows("AaaLogin");
            while (outerIt.hasNext()) {
                final Row custRow = outerIt.next();
                usersListForcustomer.add(custRow.get("LOGIN_ID"));
            }
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return usersListForcustomer;
    }
    
    public String getLogoPath(final Long customerID) {
        final String sourceMethod = "getLogoPath";
        String logoPath = "";
        try {
            logoPath = ApiFactoryProvider.getUtilAccessAPI().getRebrandLogoPath();
            if (isACPProduct()) {
                logoPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + "appctrl-jsp" + File.separator + "acp-logo.gif";
            }
            if (isDCPProduct()) {
                logoPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + SyMUtil.getInstance().getTheme() + File.separator + "DCP" + File.separator + "dcp-logo.gif";
            }
            if (getInstance().isMSP() && customerID != null) {
                getInstance();
                if (!isSAS()) {
                    final Boolean isNewLogo = (Boolean)DBUtil.getValueFromDB("CustomerInfo", "CUSTOMER_ID", customerID, "IS_CUSTOM_LOGO_ENABLE");
                    if (isNewLogo != null && isNewLogo && this.checkIsLogoAvailableInFolder(customerID)) {
                        logoPath = this.getCustomerLogoPath(customerID);
                    }
                }
            }
        }
        catch (final Exception e) {
            CustomerInfoUtil.out.log(Level.INFO, sourceMethod + "--> Exception while getting customer path for customerID::" + customerID, e);
        }
        return logoPath;
    }
    
    public boolean checkIsLogoAvailableInFolder(final long customerID) {
        boolean availableStatus = false;
        final String sourceMethod = "checkIsLogoAvailableInFolder";
        final String path = this.getCustomerLogoPath(customerID);
        try {
            availableStatus = (ApiFactoryProvider.getFileAccessAPI().isFileExists(path) && ApiFactoryProvider.getFileAccessAPI().isFile(path));
        }
        catch (final Exception e) {
            CustomerInfoUtil.out.log(Level.INFO, sourceMethod + "--> Exception in checking logo path ::" + e);
        }
        return availableStatus;
    }
    
    public boolean checkAndUpdateCustomerLogo(final long customerID) {
        final String sourceMethod = "checkAndUpdateCustomerLogo";
        boolean availableStatus = false;
        final String path = this.getCustomerLogoPath(customerID);
        final File imgFile = new File(path);
        availableStatus = (imgFile.exists() && imgFile.isFile());
        if (!availableStatus) {
            CustomerInfoUtil.out.log(Level.INFO, sourceMethod + "-->customer logo is not placed in its location...so going to update in db...for customerID::" + customerID);
            this.updateCustomerLogoStatus(false, customerID);
        }
        return availableStatus;
    }
    
    public boolean updateCustomerLogoStatus(final boolean status, final long customerID) {
        final String sourceMethod = "updateCustomerLogoStatus";
        try {
            final Column col = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
            final Criteria criteria = new Criteria(col, (Object)customerID, 0, false);
            final DataObject dob = SyMUtil.getPersistence().get("CustomerInfo", criteria);
            final Row row = dob.getFirstRow("CustomerInfo");
            final Boolean dbStatus = (Boolean)row.get("IS_CUSTOM_LOGO_ENABLE");
            if (dbStatus == null || dbStatus != status) {
                CustomerInfoUtil.out.log(Level.INFO, sourceMethod + "-->going to upate column name IS_CUSTOM_LOGO_ENABLE... DBstatus::" + dbStatus + " ..input status::" + status);
                row.set("IS_CUSTOM_LOGO_ENABLE", (Object)status);
                dob.updateRow(row);
                SyMUtil.getPersistence().update(dob);
                CustomerInfoUtil.out.log(Level.INFO, sourceMethod + "-->successful update for IS_CUSTOM_LOGO_ENABLE for customerID::" + customerID + "...from DBstatus::" + dbStatus + " ..to input status::" + status);
            }
        }
        catch (final Exception ex) {
            CustomerInfoUtil.out.log(Level.INFO, sourceMethod + "-->Exception. cant update column IS_CUSTOM_LOGO_ENABLE for customerID::" + customerID + "..cant change into status::" + status, ex);
        }
        return status;
    }
    
    public String getCustomerLogoPath(final long customerID) {
        return DCMetaDataUtil.getInstance().getCustomerLogoFolderPath(customerID) + File.separator + "customer-logo.png";
    }
    
    public String getCustomerLogoPathforWbCl(final boolean customIconEnable, final long customerID) {
        String logoPath = "";
        if (customIconEnable) {
            logoPath = this.getCustomerLogoPathforWbCl(customerID);
        }
        else {
            logoPath = this.getRebrandLogoPathForWebConsole();
        }
        return logoPath;
    }
    
    public String getCustomerLogoPathforWbCl(final long customerID) {
        return "/client-data/" + customerID + "/" + "customer-data" + "/" + "logo" + "/" + "customer-logo.png";
    }
    
    public String getRebrandLogoPathForWebConsole() {
        final String isRebranded = SyMUtil.getSyMParameter("IS_REBRANDED");
        if (isRebranded != null && isRebranded.equalsIgnoreCase("true")) {
            return "/images/" + SyMUtil.getInstance().getTheme() + "/" + "rebranded-logo.gif" + "?" + System.currentTimeMillis();
        }
        final String product = ProductUrlLoader.getInstance().getGeneralProperites().getProperty("productcode");
        if (product == null) {
            return "/images/" + SyMUtil.getInstance().getTheme() + "/DCEE/" + "logo.gif";
        }
        return "/images/" + SyMUtil.getInstance().getTheme() + "/" + product + "/" + "logo.gif";
    }
    
    public Long getCustomerIDForLoginUser() throws Exception {
        final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        final List customerList = this.getCustomersForLoginUser(userId);
        final Iterator itr = customerList.iterator();
        if (itr.hasNext()) {
            final HashMap map = itr.next();
            final Long customerID = map.get("CUSTOMER_ID");
            return customerID;
        }
        throw new Exception("Customer ID Not Available.");
    }
    
    public void deleteCustomerAPI(final Long customerID, final Long userID) throws DataAccessException, Exception {
        final Properties customerProperties = getInstance().getCustomerInfo(customerID);
        final String customerName = customerProperties.getProperty("CUSTOMER_NAME");
        try {
            final Criteria criteria = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0);
            SyMUtil.getPersistence().delete(criteria);
            final CustomerEvent customerEvent = new CustomerEvent(customerID, customerProperties);
            CustomerHandler.getInstance().invokeCustomerListeners(customerEvent, 1001);
        }
        catch (final DataAccessException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw e2;
        }
    }
    
    @Deprecated
    public String deleteCustomer(final Long customerId, final Long userId) {
        String sMessage = "";
        try {
            final Properties customerProperties = getInstance().getCustomerInfo(customerId);
            final Criteria crt = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            SyMUtil.getPersistence().delete(crt);
            final CustomerEvent customerEvent = new CustomerEvent(customerId, customerProperties);
            CustomerHandler.getInstance().invokeCustomerListeners(customerEvent, 1001);
            sMessage = "success";
        }
        catch (final Exception ee) {
            CustomerInfoUtil.logger.log(Level.WARNING, "Caught exception while deleting a customer : ", customerId);
            sMessage = "failure";
        }
        return sMessage;
    }
    
    public TimeZone getCustomerTimeZone(final Long customerId) {
        TimeZone timezone = null;
        try {
            final Criteria criteria = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("CustomerInfo", criteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("CustomerInfo");
                final String tz = (String)row.get("TIMEZONE");
                if (tz != null) {
                    timezone = TimeZone.getTimeZone(tz);
                }
            }
        }
        catch (final DataAccessException ex) {
            CustomerInfoUtil.out.log(Level.SEVERE, "DataAccessException getCustomerTimeZone() : " + ex);
        }
        return timezone;
    }
    
    public List<Long> filterCustomerMappedResource(final List<Long> resourceList, final Long customerID) {
        final Criteria resourceCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.setCriteria(resourceCriteria.and(customerCriteria));
        final List<Long> filteredList = new ArrayList<Long>();
        try {
            final DataObject dataObject = SyMUtil.getReadOnlyPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("Resource");
            while (iterator.hasNext()) {
                final Row resourceRow = iterator.next();
                filteredList.add((Long)resourceRow.get("RESOURCE_ID"));
            }
        }
        catch (final Exception ex) {
            CustomerInfoUtil.out.log(Level.SEVERE, "Exception while filtering resourceList based on CustomerID", ex);
        }
        return filteredList;
    }
    
    public static void setIsBLMEnabledProperty(final String value) {
        updateSecurityAddOnDetailsCache("BLM_ADDON_ENABLED", value);
    }
    
    public static boolean isBLMEnabled() {
        return isSecurityModuleAddonEnabled("BLM_ADDON_ENABLED");
    }
    
    public static boolean isDCPAddonEnabled() {
        return isSecurityModuleAddonEnabled("DCP_ADDON_ENABLED");
    }
    
    public static void setIsDCPAddonEnabledProperty(final String value) {
        updateSecurityAddOnDetailsCache("DCP_ADDON_ENABLED", value);
    }
    
    public static void setIsVulnerabilityAddonEnabledProperty(final String value) {
        updateSecurityAddOnDetailsCache("VULNERABILITY_ADDON_ENABLED", value);
    }
    
    public static boolean isACPAddonEnabled() {
        return isSecurityModuleAddonEnabled("ACP_ADDON_ENABLED");
    }
    
    public static void setIsACPAddonEnabledProperty(final String value) {
        updateSecurityAddOnDetailsCache("ACP_ADDON_ENABLED", value);
    }
    
    private static boolean isSecurityModuleAddonEnabled(final String addonKey) {
        Boolean isAddonEnabled = Boolean.FALSE;
        final HashMap<String, Boolean> securityAddonDetailsMap = (HashMap<String, Boolean>)ApiFactoryProvider.getCacheAccessAPI().getCache("SECURITY_ADDON_DETAILS", 2);
        if (securityAddonDetailsMap == null || !securityAddonDetailsMap.containsKey(addonKey) || securityAddonDetailsMap.get(addonKey) == null) {
            isAddonEnabled = updateSecurityAddOnDetailsCache(addonKey, securityModuleAddonCheck(addonKey));
        }
        else {
            isAddonEnabled = securityAddonDetailsMap.get(addonKey);
        }
        CustomerInfoUtil.out.log(Level.INFO, "-----Inside isSecurityModuleAddonEnabled method, " + addonKey + " value null. Hence reading from CUSTOMERPARAMS to set the value " + isAddonEnabled);
        return isAddonEnabled;
    }
    
    public static Boolean updateSecurityAddOnDetailsCache(final String addonKey, final String value) {
        Boolean isAddonEnabled = Boolean.FALSE;
        HashMap<String, Boolean> securityAddonDetailsMap = (HashMap<String, Boolean>)ApiFactoryProvider.getCacheAccessAPI().getCache("SECURITY_ADDON_DETAILS", 2);
        isAddonEnabled = setIsAddOnEnabledProperty(value);
        if (securityAddonDetailsMap == null) {
            securityAddonDetailsMap = new HashMap<String, Boolean>();
        }
        securityAddonDetailsMap.put(addonKey, isAddonEnabled);
        ApiFactoryProvider.getCacheAccessAPI().putCache("SECURITY_ADDON_DETAILS", securityAddonDetailsMap, 2);
        return isAddonEnabled;
    }
    
    public static boolean setIsAddOnEnabledProperty(final String value) {
        boolean isAddonEnabled;
        if (value != null && value.equals("true")) {
            isAddonEnabled = Boolean.TRUE;
        }
        else {
            isAddonEnabled = Boolean.FALSE;
        }
        return isAddonEnabled;
    }
    
    private static String securityModuleAddonCheck(final String addonKey) {
        String addonStatus = "false";
        String addonStatusDB = null;
        try {
            final Criteria addonCriteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)addonKey, 0, false);
            final DataObject dObj = SyMUtil.getPersistence().get("CustomerParams", addonCriteria);
            final Row customerAddonRow = dObj.getRow("CustomerParams");
            if (customerAddonRow != null) {
                addonStatusDB = (String)customerAddonRow.get("PARAM_VALUE");
            }
            addonStatus = (addonStatusDB != null && addonStatusDB.equals("true")) + "";
        }
        catch (final Exception ex) {
            CustomerInfoUtil.out.log(Level.SEVERE, "Exception while filtering resourceList based on CustomerID", ex);
        }
        return addonStatus;
    }
    
    public static void updateAllSecurityAddOnDetailsCache() {
        final HashMap<String, Boolean> securityAddonDetailsMap = new HashMap<String, Boolean>();
        for (final String addonKey : CustomerInfoUtil.SECURITY_ADDON_ENABLED_KEYS) {
            final boolean isAddonEnabled = setIsAddOnEnabledProperty(securityModuleAddonCheck(addonKey));
            securityAddonDetailsMap.put(addonKey, isAddonEnabled);
        }
        ApiFactoryProvider.getCacheAccessAPI().putCache("SECURITY_ADDON_DETAILS", securityAddonDetailsMap, 2);
    }
    
    public static boolean isVulnerabilityEnabled() {
        return isSecurityModuleAddonEnabled("VULNERABILITY_ADDON_ENABLED");
    }
    
    public static boolean isSASAndMSP() {
        return isSAS() && ApiFactoryProvider.getUtilAccessAPI().isMSP();
    }
    
    public static boolean isSecurityAddOnApplied() {
        boolean isAddonEnabled = false;
        for (final String addonKey : CustomerInfoUtil.SECURITY_ADDON_ENABLED_KEYS) {
            isAddonEnabled = setIsAddOnEnabledProperty(securityModuleAddonCheck(addonKey));
            if (isAddonEnabled) {
                break;
            }
        }
        return isAddonEnabled;
    }
    
    static {
        CustomerInfoUtil.logger = SyMLogger.getCustomerLogger();
        CustomerInfoUtil.out = Logger.getLogger(CustomerInfoUtil.class.getName());
        CustomerInfoUtil.custInfoUtil = null;
        CustomerInfoUtil.isSAS = false;
        CustomerInfoUtil.isMDM = null;
        CustomerInfoUtil.isDC = null;
        CustomerInfoUtil.isMDMP = null;
        CustomerInfoUtil.isPMP = null;
        CustomerInfoUtil.isRAP = null;
        CustomerInfoUtil.isOSDProduct = null;
        CustomerInfoUtil.isBSPProduct = null;
        CustomerInfoUtil.isPMPProduct = null;
        CustomerInfoUtil.isVMPProduct = null;
        CustomerInfoUtil.isDCPProduct = null;
        CustomerInfoUtil.isACPProduct = null;
        CustomerInfoUtil.isUESProduct = null;
        SECURITY_ADDON_ENABLED_KEYS = new String[] { "ACP_ADDON_ENABLED", "BLM_ADDON_ENABLED", "DCP_ADDON_ENABLED", "VULNERABILITY_ADDON_ENABLED" };
        CustomerInfoUtil.cachedPersistence = null;
    }
}
