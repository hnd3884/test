package com.me.mdm.server.stageddevice;

import java.util.logging.Level;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class StagedDeviceFacade
{
    protected static Logger logger;
    
    public Object getStagedDevice(final JSONObject message) throws APIHTTPException {
        final JSONObject result = new JSONObject();
        final Long customerId = APIUtil.getCustomerID(message);
        Connection conn = null;
        DataSet ds = null;
        try {
            final Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "staged_device_id", (Long)null);
            final SelectQuery query = this.getSelectQuery();
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)5, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0));
            Criteria deviceCriteria = null;
            if (deviceId != -1L) {
                deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
            }
            query.setCriteria(criteria.and(deviceCriteria));
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            if (ds.next()) {
                result.put("device_id", ds.getValue("RESOURCE_ID"));
                final int platformType = Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString());
                if (platformType == 1) {
                    result.put("platform_type", (Object)"ios");
                }
                else if (platformType == 2) {
                    result.put("platform_type", (Object)"android");
                }
                else if (platformType == 3) {
                    result.put("platform_type", (Object)"windows");
                }
                result.put("platform", platformType);
                result.put("model", ds.getValue("MODEL"));
                result.put("product_name", ds.getValue("PRODUCT_NAME"));
                result.put("os_version", ds.getValue("OS_VERSION"));
                result.put("device_capacity", ds.getValue("DEVICE_CAPACITY"));
                result.put("device_name", ds.getValue("DEVICE_NAME"));
                result.put("serial_number", ds.getValue("SERIAL_NUMBER"));
            }
            return result;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            this.closeConnection(conn, ds);
        }
    }
    
    public Object getStagedDevices(final HashMap params, final JSONObject requestJSON) throws APIHTTPException {
        JSONArray result = new JSONArray();
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        final String platform = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("platform", (String)null);
        final String deviceType = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("device_type", (String)null);
        Connection conn = null;
        DataSet ds = null;
        try {
            final String search = params.get("search");
            final SelectQuery query = this.getSelectQuery();
            Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)5, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0));
            Criteria searchCriteria = null;
            if (platform != null && platform.length() != 0) {
                int p = -1;
                if (platform.equalsIgnoreCase("android")) {
                    p = 2;
                }
                else if (platform.equalsIgnoreCase("ios")) {
                    p = 1;
                }
                else if (platform.equalsIgnoreCase("windows")) {
                    p = 3;
                }
                else if (platform.equalsIgnoreCase("chrome")) {
                    p = 4;
                }
                else {
                    final String[] platformTypes = platform.split(",");
                    final ArrayList<Integer> values = new ArrayList<Integer>();
                    for (int i = 0; i < platformTypes.length; ++i) {
                        final int temp = Integer.parseInt(platformTypes[i]);
                        if (temp == 2 || temp == 1 || temp == 3 || temp == 4) {
                            values.add(temp);
                        }
                    }
                    if (values.size() != 0) {
                        final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)values.toArray(), 8);
                        if (criteria == null) {
                            criteria = platformCriteria;
                        }
                        else {
                            criteria = criteria.and(platformCriteria);
                        }
                    }
                }
                if (p != -1) {
                    final Criteria platformCriteria2 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)p, 0);
                    if (criteria == null) {
                        criteria = platformCriteria2;
                    }
                    else {
                        criteria = criteria.and(platformCriteria2);
                    }
                }
            }
            if (deviceType != null) {
                final String[] deviceTypes = deviceType.split(",");
                final ArrayList<Integer> values2 = new ArrayList<Integer>();
                for (int j = 0; j < deviceTypes.length; ++j) {
                    final int temp2 = Integer.parseInt(deviceTypes[j]);
                    if (temp2 == 1 || temp2 == 2 || temp2 == 4 || temp2 == 3 || temp2 == 5) {
                        values2.add(temp2);
                    }
                }
                if (values2.size() != 0) {
                    final Criteria deviceTypeCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)values2.toArray(), 8);
                    if (criteria == null) {
                        criteria = deviceTypeCri;
                    }
                    else {
                        criteria = criteria.and(deviceTypeCri);
                    }
                }
            }
            if (search != null) {
                searchCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)search, 12, false).or(new Criteria(Column.getColumn("MdModelInfo", "PRODUCT_NAME"), (Object)search, 12, false).or(Column.getColumn("MdModelInfo", "MODEL"), (Object)search, 12, false));
            }
            query.setCriteria(criteria.and(searchCriteria));
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final JSONObject groupMap = new JSONObject();
                groupMap.put("device_id", ds.getValue("ENROLLMENT_DEVICE_ID"));
                groupMap.put("device_resource_id", ds.getValue("RESOURCE_ID"));
                final int platformType = Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString());
                if (platformType == 1) {
                    groupMap.put("platform_type", (Object)"ios");
                }
                else if (platformType == 2) {
                    groupMap.put("platform_type", (Object)"android");
                }
                else if (platformType == 3) {
                    groupMap.put("platform_type", (Object)"windows");
                }
                groupMap.put("platform", platformType);
                groupMap.put("model", ds.getValue("MODEL"));
                groupMap.put("product_name", ds.getValue("PRODUCT_NAME"));
                groupMap.put("os_version", ds.getValue("OS_VERSION"));
                groupMap.put("device_capacity", ds.getValue("DEVICE_CAPACITY"));
                groupMap.put("device_name", ds.getValue("DEVICE_NAME"));
                groupMap.put("serial_number", ds.getValue("SERIAL_NUMBER"));
                groupMap.put("owned_by", ds.getValue("OWNED_BY"));
                groupMap.put("enrollment_type", ds.getValue("ENROLLMENT_TYPE"));
                result.put((Object)groupMap);
            }
            result = this.getDeviceInAwaitingActivation(result, platform, deviceType);
            final JSONObject response = new JSONObject();
            response.put("staged_devices", (Object)result);
            return response;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            this.closeConnection(conn, ds);
        }
    }
    
    private JSONArray getDeviceInAwaitingActivation(final JSONArray result, final String platform, final String deviceType) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AppleConfigDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "WinAzureADDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "WindowsLaptopDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "WinModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "WindowsICDDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "KNOXMobileDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidQRDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidNFCDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidZTDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "GSChromeDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "MacModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"));
            Criteria cri = new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"), (Object)null, 0);
            if (platform != null && platform.length() != 0) {
                Criteria allPlatformCri = null;
                Criteria platformCri = null;
                final String[] platformTypes = platform.split(",");
                for (int i = 0; i < platformTypes.length; ++i) {
                    final String temp = platformTypes[i];
                    if (temp.equalsIgnoreCase("android") || temp.equalsIgnoreCase("2")) {
                        platformCri = new Criteria(Column.getColumn("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        platformCri = platformCri.or(new Criteria(Column.getColumn("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                        platformCri = platformCri.or(new Criteria(Column.getColumn("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                        platformCri = platformCri.or(new Criteria(Column.getColumn("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                    }
                    else if (temp.equalsIgnoreCase("ios") || temp.equalsIgnoreCase("1")) {
                        platformCri = new Criteria(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        platformCri = platformCri.or(new Criteria(Column.getColumn("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                        platformCri = platformCri.or(new Criteria(Column.getColumn("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                    }
                    else if (platform.equalsIgnoreCase("windows") || temp.equalsIgnoreCase("3")) {
                        platformCri = new Criteria(Column.getColumn("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        platformCri = platformCri.or(new Criteria(Column.getColumn("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                        platformCri = platformCri.or(new Criteria(Column.getColumn("WinModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                        platformCri = platformCri.or(new Criteria(Column.getColumn("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                    }
                    else if (platform.equalsIgnoreCase("chrome") || temp.equalsIgnoreCase("4")) {
                        platformCri = new Criteria(Column.getColumn("GSChromeDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                    }
                    if (allPlatformCri != null) {
                        allPlatformCri = allPlatformCri.or(platformCri);
                    }
                    else {
                        allPlatformCri = platformCri;
                    }
                }
                cri = cri.and(allPlatformCri);
            }
            selectQuery.setCriteria(cri);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator rows = dataObject.getRows("DeviceForEnrollment");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final JSONObject groupMap = new JSONObject();
                groupMap.put("device_id", row.get("ENROLLMENT_DEVICE_ID"));
                groupMap.put("device_resource_id", (Object)"--");
                groupMap.put("platform_type", (Object)"--");
                groupMap.put("model", (Object)"--");
                groupMap.put("product_name", (Object)"--");
                groupMap.put("os_version", (Object)"--");
                groupMap.put("device_capacity", (Object)"--");
                groupMap.put("device_name", (Object)"--");
                groupMap.put("serial_number", row.get("SERIAL_NUMBER"));
                groupMap.put("owned_by", (Object)"--");
                groupMap.put("enrollment_type", (Object)"--");
                result.put((Object)groupMap);
            }
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    private void closeConnection(final Connection conn, final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            StagedDeviceFacade.logger.log(Level.WARNING, "Exception occoured in closeConnection....", ex);
        }
    }
    
    private SelectQuery getSelectQuery() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Join device_deviceexten = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join mddeviceinfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join modelinfoJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2);
        final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join enrollRequestToDeviceJoin = new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join deviceEnrollToRequestJoin = new Join("EnrollmentRequestToDevice", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        final Join deviceForEnrollJoin = new Join("DeviceEnrollmentToRequest", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2);
        final Join erToDeviceJoin = new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        query.addJoin(device_deviceexten);
        query.addJoin(mddeviceinfoJoin);
        query.addJoin(modelinfoJoin);
        query.addJoin(resourceJoin);
        query.addJoin(enrollRequestToDeviceJoin);
        query.addJoin(deviceEnrollToRequestJoin);
        query.addJoin(deviceForEnrollJoin);
        query.addJoin(erToDeviceJoin);
        query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "PRODUCT_NAME"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "DEVICE_CAPACITY"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "DEVICE_NAME"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"));
        query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"));
        query.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        return query;
    }
    
    static {
        StagedDeviceFacade.logger = Logger.getLogger("MDMApiLogger");
    }
}
