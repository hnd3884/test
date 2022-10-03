package com.me.mdm.directory;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.util.LinkedHashMap;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.api.metainfo.ProductMetaImpl;

public class ProductMetaMDMPImpl extends ProductMetaImpl
{
    private static final String VENDOR = "vendor";
    private static final String VENDOR_URL = "vendor_url";
    private static final String DB_NAME = "db_name";
    private static final String IS_MAIL_SERVER_CONFIGURED = "is_mail_server_configured";
    private static final String IS_REMOTE_DB_CONFIGURED = "is_remote_db_configured";
    Logger logger;
    
    public ProductMetaMDMPImpl() {
        this.logger = Logger.getLogger(ProductMetaMDMPImpl.class.getName());
    }
    
    public JSONObject getProductMeta() {
        try {
            final JSONObject meta = super.getProductMeta();
            meta.put("is_sas", false);
            meta.put("static_server", (Object)"");
            meta.put("vendor", (Object)"ZOHO Corp.");
            meta.put("vendor_url", (Object)I18N.getMsg("dc.vendor.website.zohocorp", new Object[0]));
            final LinkedHashMap dbProbs = (LinkedHashMap)DBUtil.getDBServerProperties();
            final String dbName = String.valueOf(dbProbs.get("db.name")).toUpperCase();
            meta.put("db_name", (Object)dbName);
            meta.put("is_mail_server_configured", ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured());
            meta.put("is_remote_db_configured", (Object)DBUtil.isRemoteDB());
            return meta;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSONException while getting Product Meta", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while getting Product Meta", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
