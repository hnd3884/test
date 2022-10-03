package com.me.mdm.server.customer;

import java.util.Hashtable;
import java.util.Set;
import java.util.Collection;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Arrays;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.DerivedColumn;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.HashSet;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.io.File;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.files.FileFacade;
import org.apache.tika.Tika;
import org.json.JSONException;
import java.util.Properties;
import java.util.logging.Level;
import com.me.mdm.server.license.MDMLicenseImplMSP;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class CustomerFacade
{
    private static final Logger LOGGER;
    private static final String CUSTOMER_LOGO_DELETED = "customer_logo_deleted";
    private static final String NO_OF_DEVICE_BEFOREEDIT = "no_of_device_beforeedit";
    private static final String COMPANY_LOGO_PATH = "company_logo_path";
    private static final String CUSTOMER_ID = "customer_id";
    private static final String IS_NO_CUSTOMER_ADDEDPAGE = "is_no_customer_addedpage";
    
    public JSONObject getCustomer(final JSONObject message) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getResourceID(message, "CUSTOMER_ID".toLowerCase());
            if (!MDMCustomerInfoUtil.getInstance().isCustomerIDValidForUser(APIUtil.getUserID(message), customerId)) {
                throw new APIHTTPException("COM0008", new Object[] { "Customer Id:" + customerId });
            }
            final Properties prop = MDMCustomerInfoUtil.getInstance().getAllDetailsOfCustomer(customerId);
            if (prop.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { "CUSTOMER_ID :" + customerId });
            }
            final JSONObject json = new JSONObject();
            json.put("CUSTOMER_ID", (Object)customerId);
            json.put("CUSTOMER_NAME", ((Hashtable<K, Object>)prop).get("CUSTOMER_NAME"));
            json.put("CUSTOMER_EMAIL", ((Hashtable<K, Object>)prop).get("CUSTOMER_EMAIL"));
            json.put("COMPANY_NAME", ((Hashtable<K, Object>)prop).get("COMPANY_NAME"));
            json.put("DESCRIPTION", ((Hashtable<K, Object>)prop).get("DESCRIPTION"));
            json.put("WEB_URL", ((Hashtable<K, Object>)prop).get("WEB_URL"));
            json.put("DOOR_NO", ((Hashtable<K, Object>)prop).get("DOOR_NO"));
            json.put("STREET", ((Hashtable<K, Object>)prop).get("STREET"));
            json.put("LANDMARK", ((Hashtable<K, Object>)prop).get("LANDMARK"));
            json.put("CITY", ((Hashtable<K, Object>)prop).get("CITY"));
            json.put("POSTALCODE", ((Hashtable<K, Object>)prop).get("POSTALCODE"));
            json.put("STATE", ((Hashtable<K, Object>)prop).get("STATE"));
            json.put("COUNTRY", ((Hashtable<K, Object>)prop).get("COUNTRY"));
            json.put("LANDLINE", ((Hashtable<K, Object>)prop).get("LANDLINE"));
            json.put("FAX", ((Hashtable<K, Object>)prop).get("FAX"));
            final LicenseProvider licenseHandler = LicenseProvider.getInstance();
            final String licenseType = licenseHandler.getLicenseType();
            int noOfCurrentCustomerAllotedDevices = 0;
            if (!licenseType.equalsIgnoreCase("F")) {
                final Integer device = (Integer)DBUtil.getValueFromDB("DeviceLimitToCustomerMapping", "CUSTOMER_ID", (Object)customerId, "NO_OF_DEVICES");
                if (device != null) {
                    noOfCurrentCustomerAllotedDevices = device;
                }
            }
            json.put("NO_OF_DEVICES", noOfCurrentCustomerAllotedDevices);
            final int noOfOtherCustomerAllottedDevices = new MDMLicenseImplMSP().getTotalDevicesAllocatedExcludingCustomer(customerId);
            int totalNoOfDevices = 0;
            final String totalDevices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
            if (!licenseType.equalsIgnoreCase("T")) {
                totalNoOfDevices = Integer.valueOf(totalDevices);
            }
            final int remainingDevices = totalNoOfDevices - noOfOtherCustomerAllottedDevices - noOfCurrentCustomerAllotedDevices;
            if (licenseType.equalsIgnoreCase("T")) {
                json.put("remaining_devices", (Object)"unlimited");
            }
            else {
                json.put("remaining_devices", remainingDevices);
            }
            json.put("NO_OF_TECHNICIAN", ((Hashtable<K, Object>)prop).get("NO_OF_TECHNICIAN"));
            json.put("EDITION", ((Hashtable<K, Object>)prop).get("EDITION"));
            boolean customIconEnable = ((Hashtable<K, Boolean>)prop).get("IS_CUSTOM_LOGO_ENABLE");
            if (customIconEnable) {
                customIconEnable = MDMCustomerInfoUtil.getInstance().checkAndUpdateCustomerLogo((long)customerId);
            }
            final String logoPath = MDMCustomerInfoUtil.getInstance().getCustomerLogoPathforWbCl(customIconEnable, (long)customerId);
            json.put("company_logo_path", (Object)logoPath);
            return json;
        }
        catch (final Exception ex) {
            CustomerFacade.LOGGER.log(Level.SEVERE, "Exception in getCustomer", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addorUpdateCustomerDetails(final JSONObject message, final boolean isUpdate) throws APIHTTPException {
        try {
            JSONObject requestJSON;
            try {
                requestJSON = message.getJSONObject("msg_body");
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            String name;
            try {
                name = String.valueOf(requestJSON.get("CUSTOMER_NAME".toLowerCase()));
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0005", new Object[] { "CUSTOMER_NAME" });
            }
            try {
                requestJSON.get("CUSTOMER_EMAIL".toLowerCase());
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0005", new Object[] { "CUSTOMER_EMAIL" });
            }
            boolean no_customer_added_page = false;
            if (requestJSON.has("is_no_customer_addedpage") && Boolean.valueOf(requestJSON.get("is_no_customer_addedpage").toString())) {
                no_customer_added_page = true;
            }
            if (!no_customer_added_page) {
                try {
                    requestJSON.get("NO_OF_DEVICES".toLowerCase());
                }
                catch (final JSONException e2) {
                    throw new APIHTTPException("COM0005", new Object[] { "NO_OF_DEVICES" });
                }
            }
            final Properties customerInfo = this.getPropFromJSON(requestJSON);
            boolean isNewLogo = false;
            final Tika tika = new Tika();
            final FileFacade fileFacade = new FileFacade();
            String logoFilePathDMTemp = null;
            try {
                if (requestJSON.has("company_logo_path")) {
                    final Long fileId = Long.valueOf(requestJSON.get("company_logo_path").toString());
                    final String logoFilePathDM = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", fileId)).get("file_path"));
                    logoFilePathDMTemp = fileFacade.getTempLocation(logoFilePathDM);
                    new FileFacade().writeFile(logoFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(logoFilePathDM));
                }
            }
            catch (final Exception e3) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            if (requestJSON.has("customer_logo_deleted") && requestJSON.get("customer_logo_deleted") != null) {
                final Boolean isLogoDeleted = Boolean.valueOf(requestJSON.get("customer_logo_deleted").toString());
                if (!isLogoDeleted) {
                    isNewLogo = true;
                }
            }
            else if (!requestJSON.has("CUSTOMER_ID") && logoFilePathDMTemp != null) {
                isNewLogo = true;
            }
            ((Hashtable<String, Boolean>)customerInfo).put("IS_CUSTOM_LOGO_ENABLE", isNewLogo);
            Long customerId = -1L;
            boolean uploadLogo = false;
            try {
                final String customerName = ((Hashtable<K, String>)customerInfo).get("CUSTOMER_NAME");
                final Criteria crit = new Criteria(new Column("CustomerInfo", "CUSTOMER_NAME"), (Object)customerName, 0, false);
                final DataObject cmInfoDO = MDMUtil.getPersistence().get("CustomerInfo", crit);
                final Row cmInfoRow = cmInfoDO.getRow("CustomerInfo");
                if (cmInfoRow != null && !isUpdate) {
                    throw new APIHTTPException("COMPANY006", new Object[0]);
                }
                if (cmInfoRow == null && !isUpdate) {
                    customerId = MDMCustomerInfoUtil.getInstance().addCustomer(name, customerInfo);
                    String i18n = "desktopcentral.webclient.admin.CustomerInfoAction.Customer.exist";
                    final Object remarksArgs = customerName;
                    if (customerId == -1L || customerId == -2L) {
                        DCEventLogUtil.getInstance().addEvent(802, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, i18n, remarksArgs, true, customerId);
                    }
                    else {
                        i18n = "dc.webclient.CustomerInfoAction.Added";
                        uploadLogo = true;
                        DCEventLogUtil.getInstance().addEvent(801, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, i18n, remarksArgs, true, customerId);
                        final LicenseProvider dcLicenseHandler = LicenseProvider.getInstance();
                        final String licenseType = dcLicenseHandler.getLicenseType();
                        final String allocDevices = ((Hashtable<K, String>)customerInfo).get("NO_OF_DEVICES");
                        final int noOfDevices = (allocDevices != null && !allocDevices.equals("")) ? Integer.parseInt(allocDevices) : 0;
                        if (!licenseType.equalsIgnoreCase("F") && noOfDevices != 0) {
                            final String devicesAllocated = "mdm.msp.device_limit_allocated_event";
                            final Object remarkArgs = customerName + "@@@" + ((Hashtable<K, Object>)customerInfo).get("NO_OF_DEVICES");
                            DCEventLogUtil.getInstance().addEvent(2091, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, devicesAllocated, remarkArgs, true, customerId);
                        }
                    }
                }
                else if (isUpdate) {
                    customerId = APIUtil.getResourceID(message, "customer_id");
                    customerId = MDMCustomerInfoUtil.getInstance().updateCustomer(customerId, name, customerInfo);
                    DCEventLogUtil.getInstance().addEvent(801, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, "mdm.msp.update_customer_success", (Object)customerName, true, customerId);
                    uploadLogo = true;
                    final int deviceNumBeforeEdit = Integer.parseInt(requestJSON.get("no_of_device_beforeedit").toString());
                    int managedDeviceCount = 0;
                    managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCountForCustomer(customerId);
                    final int noDevice = Integer.parseInt(requestJSON.get("NO_OF_DEVICES".toLowerCase()).toString());
                    if (managedDeviceCount < noDevice || (noDevice == 0 && deviceNumBeforeEdit != 0)) {
                        MessageProvider.getInstance().hideMessage("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", customerId);
                    }
                    else if (managedDeviceCount >= noDevice && noDevice != 0) {
                        MessageProvider.getInstance().unhideMessage("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", customerId);
                    }
                }
                if (uploadLogo && logoFilePathDMTemp != null) {
                    final File tFile = new File(logoFilePathDMTemp);
                    final String contentType = tika.detect(tFile);
                    if (!APIUtil.isAllowedImageMimeType(contentType)) {
                        throw new APIHTTPException("COMPANY005", new Object[] { "Invalid file Format" });
                    }
                    final File dispFile = new File(logoFilePathDMTemp);
                    if (dispFile.length() > 51200L) {
                        throw new APIHTTPException("COMPANY005", new Object[] { "Image Size Larger than 50KB" });
                    }
                    isNewLogo = this.updateCompanyLogo(logoFilePathDMTemp, customerId);
                }
            }
            catch (final APIHTTPException ex) {
                throw ex;
            }
            catch (final Exception exp) {
                CustomerFacade.LOGGER.log(Level.INFO, "Exception while checking customer", exp);
            }
            final JSONObject messageHeader = message.getJSONObject("msg_header");
            final JSONObject resourceJSON = message.getJSONObject("msg_header").getJSONObject("resource_identifier");
            resourceJSON.put("customer_id", (Object)customerId);
            resourceJSON.put("user_id", (Object)APIUtil.getUserID(message));
            messageHeader.put("filters", (Object)resourceJSON);
            message.put("msg_header", (Object)messageHeader);
            return this.getCustomer(message);
        }
        catch (final JSONException ex2) {
            CustomerFacade.LOGGER.log(Level.SEVERE, "Exception in addCustomer", (Throwable)ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addCustomer(final JSONObject message) throws APIHTTPException {
        try {
            return this.addorUpdateCustomerDetails(message, false);
        }
        catch (final JSONException ex) {
            CustomerFacade.LOGGER.log(Level.SEVERE, "Exception in addCustomer", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject modifyCustomer(final JSONObject message) throws APIHTTPException {
        JSONObject result = new JSONObject();
        try {
            final Long customerId = APIUtil.getResourceID(message, "customer_id");
            if (!MDMCustomerInfoUtil.getInstance().isCustomerIDValidForUser(APIUtil.getUserID(message), customerId)) {
                throw new APIHTTPException("COM0008", new Object[] { "Customer Id:" + customerId });
            }
            final JSONObject customerJSON = this.getCustomer(message);
            final String noOfDevices = customerJSON.get("NO_OF_DEVICES").toString();
            message.getJSONObject("msg_body").put("no_of_device_beforeedit", (Object)noOfDevices);
            result = this.addorUpdateCustomerDetails(message, true);
        }
        catch (final Exception ex) {
            CustomerFacade.LOGGER.log(Level.SEVERE, "Exception in modifyCustomer", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    public void deleteCustomer(final JSONObject message) throws APIHTTPException {
        final Long customerId = APIUtil.getResourceID(message, "customer_id");
        if (customerId == -1L) {
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        String loginName = null;
        try {
            loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        }
        catch (final Exception ex) {
            CustomerFacade.LOGGER.log(Level.WARNING, "Error while getting login name : ", ex);
        }
        String customerName = null;
        try {
            customerName = CustomerInfoUtil.getInstance().getCustomerNameFromID(customerId);
        }
        catch (final Exception ex2) {
            CustomerFacade.LOGGER.log(Level.WARNING, ex2, () -> "Error while getting customer name for " + n + " : ");
        }
        try {
            if (!MDMCustomerInfoUtil.getInstance().isCustomerIDValidForUser(APIUtil.getUserID(message), customerId)) {
                throw new APIHTTPException("COM0008", new Object[] { "Customer Id:" + customerId });
            }
            MDMCustomerInfoUtil.getInstance().deleteCustomerAPI(customerId, (Long)null);
            DCEventLogUtil.getInstance().addEvent(803, loginName, (HashMap)null, "mdm.msp.Customer.deleted_success", (Object)customerName, true);
        }
        catch (final Exception ex2) {
            CustomerFacade.LOGGER.log(Level.SEVERE, "Exception in deleteCustomer", ex2);
            DCEventLogUtil.getInstance().addEvent(804, loginName, (HashMap)null, "mdm.msp.Customer.delete_failure", (Object)customerName, true);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private boolean updateCompanyLogo(final String logo, final Long customerID) throws Exception {
        final Tika tika = new Tika();
        final File tFile = new File(logo);
        final String imgName = "customer-logo.png";
        final String directory = DCMetaDataUtil.getInstance().getCustomerLogoFolderPath(customerID);
        final boolean copyFile = this.copyCustomerLogo(tFile, directory, imgName);
        return copyFile;
    }
    
    private boolean copyCustomerLogo(final File file, final String directory, final String imgName) {
        String fileName = null;
        try {
            final InputStream fileInput = new FileInputStream(file);
            fileName = file.getName();
            final boolean copyFile = copyFile(fileInput, directory, imgName);
            return copyFile;
        }
        catch (final Exception ex) {
            CustomerFacade.LOGGER.log(Level.WARNING, "Import Form File Operation failed {0} {1}", new Object[] { fileName, ex });
            return false;
        }
    }
    
    private static boolean copyFile(final InputStream fileInput, final String directory, String fileName) {
        final FileOutputStream fout = null;
        try {
            final byte[] file = new byte[fileInput.available()];
            fileInput.read(file);
            CustomerFacade.LOGGER.log(Level.INFO, "Request to import new form file: {0}", fileName);
            if (file.length < 51200) {
                if (file.length != 0) {
                    fileName = directory + File.separator + fileName;
                    ApiFactoryProvider.getFileAccessAPI().writeFile(fileName, file);
                    CustomerFacade.LOGGER.log(Level.INFO, "Successfully copied file : {0} to Images Repository", fileName);
                }
                return true;
            }
            CustomerFacade.LOGGER.log(Level.WARNING, " File size is greater than 50 kb ; So couldn't copy!");
            return false;
        }
        catch (final Exception ex) {
            CustomerFacade.LOGGER.log(Level.WARNING, "Import Form File Operation failed {0} {1}", new Object[] { fileName, ex });
            return false;
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (final Exception ex2) {
                CustomerFacade.LOGGER.log(Level.WARNING, "Exception while closing the stream!");
            }
        }
    }
    
    private Properties getPropFromJSON(final JSONObject requestJSON) {
        final Properties customerInfo = new Properties();
        ((Hashtable<String, Long>)customerInfo).put("CUSTOMER_ID", requestJSON.optLong("CUSTOMER_ID", -2L));
        ((Hashtable<String, String>)customerInfo).put("CUSTOMER_NAME", requestJSON.optString("CUSTOMER_NAME".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("CUSTOMER_EMAIL", requestJSON.optString("CUSTOMER_EMAIL".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("NO_OF_DEVICES", requestJSON.optString("NO_OF_DEVICES".toLowerCase(), "0"));
        ((Hashtable<String, String>)customerInfo).put("COMPANY_NAME", requestJSON.optString("COMPANY_NAME".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("DESCRIPTION", requestJSON.optString("DESCRIPTION".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("DOOR_NO", requestJSON.optString("DOOR_NO".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("STREET", requestJSON.optString("STREET".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("LANDMARK", requestJSON.optString("LANDMARK".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("CITY", requestJSON.optString("CITY".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("POSTALCODE", requestJSON.optString("POSTALCODE".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("STATE", requestJSON.optString("STATE".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("COUNTRY", requestJSON.optString("COUNTRY".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("LANDLINE", requestJSON.optString("LANDLINE".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("FAX", requestJSON.optString("FAX".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("WEB_URL", requestJSON.optString("WEB_URL".toLowerCase()));
        ((Hashtable<String, String>)customerInfo).put("NO_OF_TECHNICIAN", requestJSON.optString("NO_OF_TECHNICIAN"));
        ((Hashtable<String, String>)customerInfo).put("EDITION", requestJSON.optString("EDITION", "S"));
        ((Hashtable<String, String>)customerInfo).put("ADD_USER", requestJSON.optString("ADD_USER", "false"));
        return customerInfo;
    }
    
    public JSONObject getAllCustomer(final JSONObject apiRequest) throws APIHTTPException {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final Boolean selectAll = APIUtil.getBooleanFilter(apiRequest, "select_all", false);
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final PagingUtil pagingUtil = apiUtil.getPagingParams(apiRequest);
            final JSONObject response = new JSONObject();
            JSONArray customers = new JSONArray();
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            subQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            final Column deviceCountCol = Column.getColumn("ManagedDevice", "RESOURCE_ID").count();
            deviceCountCol.setColumnAlias("DEVICE_COUNT");
            subQuery.addSelectColumn(deviceCountCol);
            final Column customerIDCol = Column.getColumn("Resource", "CUSTOMER_ID");
            subQuery.addSelectColumn(customerIDCol);
            final List groupByColumns = new ArrayList();
            groupByColumns.add(customerIDCol);
            final GroupByClause groupByClause = new GroupByClause(groupByColumns);
            subQuery.setGroupByClause(groupByClause);
            final DerivedTable countManagedDevice = new DerivedTable("countManagedDevice", (Query)subQuery);
            final Table customerInfoTable = Table.getTable("CustomerInfo");
            final SelectQuery customerQuery = (SelectQuery)new SelectQueryImpl(customerInfoTable);
            final Long userId = APIUtil.getUserID(apiRequest);
            if (userId != null && userId != -1L) {
                customerQuery.addJoin(new Join(Table.getTable("CustomerInfo"), Table.getTable("LoginUserCustomerMapping"), new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), 0).and(new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userId, 0)), 2));
            }
            customerQuery.addJoin(new Join("CustomerInfo", "DeviceLimitToCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            customerQuery.addJoin(new Join(customerInfoTable, (Table)countManagedDevice, new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            final Column customerCountCol = Column.getColumn("CustomerInfo", "CUSTOMER_ID").count();
            customerQuery.addSelectColumn(customerCountCol);
            final int count = DBUtil.getRecordCount(customerQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            response.put("metadata", (Object)meta);
            if (count != 0) {
                customerQuery.removeSelectColumn(customerCountCol);
                customerQuery.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"));
                customerQuery.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"));
                customerQuery.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_EMAIL"));
                customerQuery.addSelectColumn(Column.getColumn("CustomerInfo", "ADDED_TIME"));
                customerQuery.addSelectColumn(Column.getColumn("CustomerInfo", "UPDATED_TIME"));
                customerQuery.addSelectColumn(Column.getColumn("countManagedDevice", "DEVICE_COUNT"));
                customerQuery.addSelectColumn(Column.getColumn("DeviceLimitToCustomerMapping", "NO_OF_DEVICES"));
                if (!selectAll) {
                    customerQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                    final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                    if (pagingJSON != null) {
                        response.put("paging", (Object)pagingJSON);
                    }
                }
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("customername")) {
                        customerQuery.addSortColumn(new SortColumn("CustomerInfo", "CUSTOMER_NAME", (boolean)isSortOrderASC));
                    }
                    else if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("createdby")) {
                        customerQuery.addSortColumn(new SortColumn("CustomerInfo", "CUSTOMER_ID", (boolean)isSortOrderASC));
                    }
                    else {
                        customerQuery.addSortColumn(new SortColumn("CustomerInfo", "CUSTOMER_NAME", true));
                    }
                }
                else {
                    customerQuery.addSortColumn(new SortColumn("CustomerInfo", "CUSTOMER_NAME", true));
                }
                customers = MDMUtil.executeSelectQueryAndGetOrgJSONArray(customerQuery);
                for (int i = 0; i < customers.length(); ++i) {
                    final JSONObject currentObj = customers.getJSONObject(i);
                    if (currentObj.getInt("NO_OF_DEVICES") == 0) {
                        currentObj.put("NO_OF_DEVICES", (Object)I18N.getMsg("dc.admin.common.no_limit", new Object[0]));
                    }
                    if (currentObj.get("DEVICE_COUNT").equals("NULL_VALUE")) {
                        currentObj.put("DEVICE_COUNT", 0);
                    }
                }
            }
            response.put("customers", (Object)customers);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            CustomerFacade.LOGGER.log(Level.SEVERE, "Exception in getCustomer", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
    }
    
    public JSONObject hasDedicatedTechniciansForCustomer(final JSONObject apiRequest) throws Exception {
        final Long customerId = APIUtil.getResourceID(apiRequest, "customer_id");
        final JSONObject responseJSON = new JSONObject();
        final Set<String> userIdList = new HashSet<String>();
        final List<Long> adminUsers = (List<Long>)DMUserHandler.getUserListForRole("Administrator").stream().map(userInfo -> userInfo.get("USER_ID")).collect(Collectors.toList());
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LoginUserCustomerMapping"));
        selectQuery.setCriteria(new Criteria(new Column("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerId, 0).and(new Criteria(new Column("LoginUserCustomerMapping", "DC_USER_ID"), (Object)adminUsers.toArray(), 9)));
        selectQuery.addSelectColumn(new Column("LoginUserCustomerMapping", "DC_USER_ID"));
        final Column derivedColumn = (Column)new DerivedColumn("derived", selectQuery);
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("LoginUserCustomerMapping"));
        selectQuery2.setCriteria(new Criteria(new Column("LoginUserCustomerMapping", "DC_USER_ID"), (Object)derivedColumn, 8));
        final Column userColumn = new Column("LoginUserCustomerMapping", "DC_USER_ID");
        selectQuery2.setGroupByClause(new GroupByClause((List)Arrays.asList(new GroupByColumn(userColumn, false)), new Criteria(userColumn.count(), (Object)1, 0)));
        selectQuery2.addSelectColumn(new Column("LoginUserCustomerMapping", "DC_USER_ID"));
        final Column derivedColumn2 = (Column)new DerivedColumn("userderived", selectQuery2);
        final SelectQuery selectQuery3 = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
        selectQuery3.addSelectColumn(new Column("AaaUser", "FIRST_NAME"));
        selectQuery3.setCriteria(new Criteria(new Column("AaaUser", "USER_ID"), (Object)derivedColumn2, 8));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery3);
        while (dmDataSetWrapper.next()) {
            userIdList.add((String)dmDataSetWrapper.getValue("FIRST_NAME"));
        }
        if (!userIdList.isEmpty()) {
            final JSONArray userArray = JSONUtil.getInstance().convertListToStringJSONArray(new ArrayList(userIdList));
            responseJSON.put("user_list", (Object)userArray);
        }
        return responseJSON;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
