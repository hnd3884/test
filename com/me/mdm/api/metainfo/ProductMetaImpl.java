package com.me.mdm.api.metainfo;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Calendar;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class ProductMetaImpl implements ProductMetaAPI
{
    Logger logger;
    private static final String PRODUCT_NAME = "productname";
    private static final String DISPLAY_NAME = "displayname";
    private static final String PRODUCT_URL = "prodUrl";
    private static final String TRACKING_CODE = "trackingcode";
    private static final String PRODUCT_CODE = "productcode";
    private static final String SUPPORT_MAIL = "supportmailid";
    private static final String TRACKING_LINK = "tracking-quicklinks";
    private static final String PRODUCT_ROADMAP_URL = "product_mdmroadmap_url";
    private static final String PRODUCT_ROADMAP_ADD_URL = "product_mdmroadmap_add_url";
    private static final String MDM_URL = "mdmUrl";
    protected static final String ENDPOINT_SERVICE = "endpoint_service";
    protected static JSONObject product_meta;
    
    public ProductMetaImpl() {
        this.logger = Logger.getLogger(ProductMetaAPIRequestHandler.class.getName());
        try {
            ProductMetaImpl.product_meta.put(MetaConstants.IS_DEMO_MODE, ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
            ProductMetaImpl.product_meta.put("product_version", (Object)SyMUtil.getProductProperty("productversion"));
            ProductMetaImpl.product_meta.put("build_number", (Object)SyMUtil.getProductProperty("buildnumber"));
            final HashMap NATDetails = ApiFactoryProvider.getServerSettingsAPI().getAllNATProperties();
            ProductMetaImpl.product_meta.put("nat_address", NATDetails.get("NAT_ADDRESS"));
            ProductMetaImpl.product_meta.put("nat_https_port", NATDetails.get("NAT_HTTPS_PORT"));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while processing product info", e);
        }
    }
    
    @Override
    public JSONObject getProductMeta() {
        final JSONObject meta = new JSONObject();
        try {
            final String isMsp = CustomerInfoUtil.getInstance().isMSP() ? "true" : "false";
            final boolean isEndpointServiceEnabled = LicenseProvider.getInstance().isEndpointServiceEnabled();
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            final boolean isTrialCustomer = licenseType.equalsIgnoreCase("T");
            final boolean isFreeCustomer = licenseType.equalsIgnoreCase("F");
            final boolean isUemFeatureAllowed = Boolean.parseBoolean(isMsp) || isEndpointServiceEnabled || isTrialCustomer || isFreeCustomer;
            meta.put(MetaConstants.IS_MSP, (Object)isMsp);
            meta.put(MetaConstants.EDITION_INFO, (Object)LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType());
            meta.put("endpoint_service", isUemFeatureAllowed || LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T"));
            meta.put(MetaConstants.IS_DEMO_MODE, ProductMetaImpl.product_meta.get(MetaConstants.IS_DEMO_MODE));
            meta.put(MetaConstants.SUPPORT_MAIL, (Object)I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
            meta.put(MetaConstants.TRACKING_LINK, (Object)I18N.getMsg(ProductUrlLoader.getInstance().getValue("tracking-quicklinks"), new Object[0]));
            meta.put("product_version", ProductMetaImpl.product_meta.get("product_version"));
            meta.put("build_number", ProductMetaImpl.product_meta.get("build_number"));
            meta.put(MetaConstants.PRODUCT_NAME, (Object)ProductUrlLoader.getInstance().getValue("productname"));
            meta.put(MetaConstants.PRODUCT_TITLE, (Object)ProductUrlLoader.getInstance().getValue("displayname"));
            meta.put(MetaConstants.PRODUCT_WEB_URL, (Object)ProductUrlLoader.getInstance().getValue("prodUrl"));
            meta.put(MetaConstants.TRACKING_CODE, (Object)ProductUrlLoader.getInstance().getValue("trackingcode"));
            meta.put(MetaConstants.PRODUCT_TAG, (Object)ProductUrlLoader.getInstance().getValue("productcode"));
            meta.put(MetaConstants.PRODUCT_ROADMAP_URL, (Object)ProductUrlLoader.getInstance().getValue("product_mdmroadmap_url"));
            meta.put(MetaConstants.PRODUCT_ROADMAP_ADD_URL, (Object)ProductUrlLoader.getInstance().getValue("product_mdmroadmap_add_url"));
            meta.put(MetaConstants.MDM_URL, (Object)ProductUrlLoader.getInstance().getValue("mdmUrl"));
            meta.put("nat_address", ProductMetaImpl.product_meta.get("nat_address"));
            meta.put("nat_https_port", ProductMetaImpl.product_meta.optInt("nat_https_port", 443));
            meta.put("license_type", (Object)LicenseProvider.getInstance().getLicenseType());
            int totalNoOfDevices = 0;
            final String totalDevices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
            if (!licenseType.equalsIgnoreCase("T") && !totalDevices.equalsIgnoreCase("unlimited")) {
                totalNoOfDevices = Integer.valueOf(totalDevices);
            }
            final Boolean isLicenseExpired = (Boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("FREE_LICENSE_NOT_CONFIGURED", 2);
            meta.put("is_license_expired", isLicenseExpired != null && isLicenseExpired);
            meta.put("license_type", (Object)licenseType);
            meta.put("total_devices_in_license", totalNoOfDevices);
            meta.put("copyright_year", Calendar.getInstance().get(1));
            meta.put("company_name", (Object)MDMUtil.getCopyrightProps().getProperty("company_name"));
            meta.put("company_url", (Object)MDMUtil.getCopyrightProps().getProperty("company_url"));
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSONException while getting Product Meta", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while getting Product Meta", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return meta;
    }
    
    static {
        ProductMetaImpl.product_meta = new JSONObject();
    }
}
