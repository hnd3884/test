package com.me.mdm.core.auth;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import org.apache.commons.lang3.StringEscapeUtils;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public abstract class MDMDeviceAPIKeyGenerator implements MDMAPIKeyGeneratorAPI
{
    public static final String DEVICE_API_KEY = "encapiKey";
    public static final String TOKEN_NAME = "token_name";
    public static final String TOKEN_VALUE = "token_value";
    private static MDMDeviceAPIKeyGenerator mdmDeviceAPIKeyGenerator;
    private static final Logger LOGGER;
    
    protected MDMDeviceAPIKeyGenerator() {
    }
    
    public static MDMDeviceAPIKeyGenerator getInstance() {
        if (MDMDeviceAPIKeyGenerator.mdmDeviceAPIKeyGenerator == null) {
            try {
                if (CustomerInfoUtil.isSAS) {
                    MDMDeviceAPIKeyGenerator.mdmDeviceAPIKeyGenerator = (MDMDeviceAPIKeyGenerator)Class.forName("com.me.mdmcloud.server.authentication.MDMCloudDeviceAPIKeyGenerator").newInstance();
                }
                else {
                    MDMDeviceAPIKeyGenerator.mdmDeviceAPIKeyGenerator = (MDMDeviceAPIKeyGenerator)Class.forName("com.me.mdm.onpremise.server.authentication.MDMOnPremiseDeviceAPIKeyGenerator").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMDeviceAPIKeyGenerator.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for getMDMDeviceAPIKeyGenerator... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMDeviceAPIKeyGenerator.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for getMDMDeviceAPIKeyGenerator...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMDeviceAPIKeyGenerator.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for getMDMDeviceAPIKeyGenerator...", ie2);
            }
            catch (final Exception ex) {
                MDMDeviceAPIKeyGenerator.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for getMDMDeviceAPIKeyGenerator...", ex);
            }
        }
        return MDMDeviceAPIKeyGenerator.mdmDeviceAPIKeyGenerator;
    }
    
    @Override
    public APIKey generateAPIKey(final JSONObject json) {
        try {
            json.put("DEVICE_TOKEN", (Object)MDMDeviceTokenGenerator.getInstance().generateDeviceToken(JSONUtil.optLongForUVH(json, "ENROLLMENT_REQUEST_ID", (Long)null)));
            return this.createAPIKey(json);
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String replaceDeviceAPIKeyPlaceHolder(String placeHolderURL, final APIKey key, final boolean encodeURL) {
        final String URLParams = key.getAsURLParams();
        if (URLParams.contains("authToken")) {
            MDMDeviceAPIKeyGenerator.LOGGER.log(Level.INFO, "replaceDeviceAPIKeyPlaceHolder method is called with authToken as Parameter");
        }
        else if (URLParams.contains("encapiKey")) {
            MDMDeviceAPIKeyGenerator.LOGGER.log(Level.INFO, "replaceDeviceAPIKeyPlaceHolder method is called with encapiKey as parameter");
        }
        final String dynamicVariable = "%authtoken%";
        if (placeHolderURL.contains(dynamicVariable)) {
            if (key.getAsURLParams() == null || key.getAsURLParams().isEmpty()) {
                placeHolderURL = placeHolderURL.replaceAll(dynamicVariable, "");
            }
            else {
                final String encodedDeviceAPIKey = StringEscapeUtils.escapeXml("?" + key.getAsURLParams());
                placeHolderURL = (encodeURL ? placeHolderURL.replaceAll(dynamicVariable, encodedDeviceAPIKey) : placeHolderURL.replaceAll(dynamicVariable, "?" + key.getAsURLParams()));
            }
        }
        return placeHolderURL;
    }
    
    public String replaceDeviceUDIDPlaceHolder(String placeholderURL, final String udid, final boolean encodeURL) {
        if (udid != null) {
            final String encodedURL = StringEscapeUtils.escapeXml("&udid=" + udid);
            placeholderURL = (encodeURL ? placeholderURL.replaceAll("%deviceudid%", encodedURL) : placeholderURL.replaceAll("%deviceudid%", "&udid=" + udid));
        }
        else {
            placeholderURL = placeholderURL.replaceAll("%deviceudid%", "");
        }
        return placeholderURL;
    }
    
    public HashMap fetchAPIKeyDetails(final int version, final HashMap parameterValueMap) {
        final HashMap hashMap = new HashMap();
        hashMap.put("encapiKey", parameterValueMap.get("encapiKey"));
        return hashMap;
    }
    
    @Override
    public boolean validateAPIKey(final JSONObject json) {
        boolean isValid = Boolean.FALSE;
        try {
            final String deviceToken = json.optString("encapiKey");
            final String udidOfDevice = json.optString("UDID", "");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            query.addJoin(new Join("DeviceEnrollmentRequest", "DeviceToken", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"));
            query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"));
            if (json.has("CACHED_ERID")) {
                query.setCriteria(new Criteria(new Column("DeviceToken", "ENROLLMENT_REQUEST_ID"), json.get("CACHED_ERID"), 0));
            }
            else {
                query.setCriteria(new Criteria(new Column("DeviceToken", "TOKEN_ENCRYPTED"), (Object)deviceToken, 0));
            }
            final DataObject DO = MDMUtil.getPersistence().get(query);
            String loggerUdid = udidOfDevice;
            String platformType = "invalidRequest";
            if (!DO.isEmpty()) {
                Boolean thisDevicePresent = Boolean.FALSE;
                if (!MDMStringUtils.isEmpty(udidOfDevice)) {
                    thisDevicePresent = DO.getRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udidOfDevice, 0, (boolean)Boolean.FALSE)).hasNext();
                }
                final Row enrollmentRequestTableRow = DO.getFirstRow("DeviceEnrollmentRequest");
                platformType = MDMEnrollmentUtil.getPlatformString((int)enrollmentRequestTableRow.get("PLATFORM_TYPE"));
                final Long erid = (Long)enrollmentRequestTableRow.get("ENROLLMENT_REQUEST_ID");
                final Integer status = (Integer)enrollmentRequestTableRow.get("REQUEST_STATUS");
                if (thisDevicePresent) {
                    loggerUdid = erid + "_" + udidOfDevice;
                    isValid = Boolean.TRUE;
                }
                else if (json.has("ENROLLMENT_REQUEST_ID") && ((Long)json.get("ENROLLMENT_REQUEST_ID")).equals(erid) && (status == 1 || status == 0)) {
                    loggerUdid = erid + "_" + udidOfDevice;
                    isValid = Boolean.TRUE;
                }
                else {
                    Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.SEVERE, "UDID/ERID - ENCAPIKEY Pair Did not Match");
                }
            }
            else {
                Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.SEVERE, "UDID - ENCAPIKEY Pair Did not Match");
            }
            Logger.getLogger("LoggingPurposeLogger").log(Level.INFO, "UDID/ERID : {0}, DevicePlatformType : {1}", new Object[] { loggerUdid, platformType });
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.SEVERE, "Exception in MDMDeviceAPIKeyGenerator.validateAPIKey()", ex);
        }
        return isValid;
    }
    
    @Override
    public APIKey updateAPIKey(final JSONObject json) {
        try {
            json.put("DEVICE_TOKEN", (Object)MDMDeviceTokenGenerator.getInstance().addOrUpdateDeviceToken(JSONUtil.optLongForUVH(json, "ENROLLMENT_REQUEST_ID", (Long)null)));
            return this.createAPIKey(json);
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @Override
    public APIKey getAPIKey(final JSONObject json) {
        return this.generateAPIKey(json);
    }
    
    public abstract APIKey getAPIKeyFromMap(final Map p0);
    
    @Override
    public void revokeAPIKey(final JSONObject json) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public boolean isClientVersion2_0(final String servletPath) {
        return servletPath.contains("/client/") || servletPath.contains("mobileapps") || servletPath.contains("/getmemdmFile") || servletPath.contains("docrepository");
    }
    
    public boolean isClientVersion2_0(final HttpServletRequest request) {
        return this.isClientVersion2_0(request.getServletPath()) || (request.getParameter("version") != null && request.getParameter("version").equalsIgnoreCase("2"));
    }
    
    public boolean isValidApiKeyForEnrollmentRequestId(final long enrollmentRequestId, final String encapiKey) {
        final Logger logger = Logger.getLogger("MDMDeviceDataLogger");
        logger.log(Level.INFO, "MdmIosEnrollmentScepServlet: Going to validate erid: {0}", new Object[] { enrollmentRequestId });
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        query.addJoin(new Join("DeviceEnrollmentRequest", "DeviceToken", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
        query.addSelectColumn(Column.getColumn("DeviceToken", "*"));
        final Criteria encapiKeyCriteria = new Criteria(new Column("DeviceToken", "TOKEN_ENCRYPTED"), (Object)encapiKey, 0);
        final Criteria eridCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
        query.setCriteria(eridCriteria.and(encapiKeyCriteria));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                logger.log(Level.SEVERE, "MdmIosEnrollmentScepServlet: EncapiKey validation success for erid: {0}", new Object[] { enrollmentRequestId });
                return true;
            }
        }
        catch (final Exception e) {
            logger.log(Level.SEVERE, "MdmIosEnrollmentScepServlet: Exception while validating Api key: {0}", new Object[] { enrollmentRequestId });
            return false;
        }
        logger.log(Level.INFO, "MdmIosEnrollmentScepServlet: Not a valid encapikey for erid: {0}", new Object[] { enrollmentRequestId });
        return false;
    }
    
    public String replaceDeviceAPIKeyPlaceHolder(final String placeHolderURL, final APIKey key, final boolean encodeURL, final String udid) {
        return this.replaceDeviceAPIKeyPlaceHolder(placeHolderURL, key, encodeURL);
    }
    
    public Boolean isEncodingRequiredForIOSEnterpriseApp() {
        return true;
    }
    
    static {
        MDMDeviceAPIKeyGenerator.mdmDeviceAPIKeyGenerator = null;
        LOGGER = Logger.getLogger(MDMDeviceAPIKeyGenerator.class.getName());
    }
}
