package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Column;
import java.net.URLDecoder;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import java.util.logging.Logger;

public class VPPServiceConfigHandler
{
    public Logger logger;
    private static VPPServiceConfigHandler serviceHandler;
    
    public VPPServiceConfigHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public static VPPServiceConfigHandler getInstance() {
        if (VPPServiceConfigHandler.serviceHandler == null) {
            VPPServiceConfigHandler.serviceHandler = new VPPServiceConfigHandler();
        }
        return VPPServiceConfigHandler.serviceHandler;
    }
    
    public void checkAndFetchServiceUrl() {
        try {
            final String serviceUrlsLastSyncTimeStr = VPPAppAPIRequestHandler.getInstance().getServiceUrl("VppServiceUrlsLastSyncTime");
            Long serviceUrlsLastSyncTime = -1L;
            if (serviceUrlsLastSyncTimeStr != null) {
                try {
                    serviceUrlsLastSyncTime = Long.parseLong(serviceUrlsLastSyncTimeStr);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, " Exception in getting the VPP service URLS last sync time", ex);
                }
            }
            final Long currentTimt = MDMUtil.getCurrentTimeInMillis();
            if (serviceUrlsLastSyncTime == -1L || currentTimt - serviceUrlsLastSyncTime >= 300000L) {
                this.fetchAndSetServiceUrl();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception in  checkAndFetchServiceUrl", e);
        }
    }
    
    public void fetchAndSetServiceUrl() {
        try {
            this.logger.log(Level.INFO, "Getting VPPServiceConfigSrv");
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(null, "VPPServiceConfigSrv", null, null);
            this.logger.log(Level.INFO, "Response for VPPServiceConfigSrv is {0}", new Object[] { responseJSON });
            final HashMap<String, String> responseMap = JSONUtil.getInstance().ConvertJSONObjectToHash(responseJSON);
            this.populateVPPServiceDetails(responseMap);
            VPPAppAPIRequestHandler.setServiceUrlMap();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while adding fetchAndSetServiceUrl ", e);
        }
    }
    
    private void populateVPPServiceDetails(final HashMap<String, String> responseMap) throws Exception {
        final DataObject DO = MDMUtil.getPersistence().get("MdVPPServiceDetails", (Criteria)null);
        if (!DO.isEmpty()) {
            final Iterator item = DO.getRows("MdVPPServiceDetails");
            while (item.hasNext()) {
                final Row serviceRow = item.next();
                final String serviceName = (String)serviceRow.get("SERVICE_NAME");
                final String serviceUrl = responseMap.get(serviceName);
                if (serviceUrl != null) {
                    serviceRow.set("SERVICE_URL", (Object)URLDecoder.decode(serviceUrl, "UTF-8"));
                    DO.updateRow(serviceRow);
                }
            }
        }
        final Criteria cri = new Criteria(Column.getColumn("MdVPPServiceDetails", "SERVICE_NAME"), (Object)"VppServiceUrlsLastSyncTime", 0);
        final Row lastUpdatedTime = DO.getRow("MdVPPServiceDetails", cri);
        lastUpdatedTime.set("SERVICE_URL", (Object)MDMUtil.getCurrentTimeInMillis());
        DO.updateRow(lastUpdatedTime);
        MDMUtil.getPersistence().update(DO);
    }
    
    static {
        VPPServiceConfigHandler.serviceHandler = null;
    }
}
