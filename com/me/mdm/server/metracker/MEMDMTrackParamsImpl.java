package com.me.mdm.server.metracker;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackParamsImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackParamProperties;
    private Logger logger;
    private final String sourceClass = "MEMDMTrackParamsImpl";
    
    public MEMDMTrackParamsImpl() {
        this.mdmTrackParamProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, "MEMDMTrackParamsImpl", "getProperties", "MDM Track params impl starts");
            if (!this.mdmTrackParamProperties.isEmpty()) {
                this.mdmTrackParamProperties = new Properties();
            }
            this.constructTrackParamJSON();
            DMSecurityLogger.info(this.logger, "MEMDMTrackParamsImpl", "getProperties", "Details Summary : {0}", (Object)this.mdmTrackParamProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, "MEMDMTrackParamsImpl", "MDMTracakParamsProps", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackParamProperties;
    }
    
    private void constructTrackParamJSON() throws Exception {
        final CustomerInfoUtil customerUtil = CustomerInfoUtil.getInstance();
        if (!customerUtil.isMSP()) {
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "Android_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "Remote_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "Enrollment_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "QR_Enrollment_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "Deprovision_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "OsUpdate_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "Windows_Business_Store_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "Windows_UEM_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "AppleServiceProgram");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "Apps_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "APPLE_APNS_MODULE");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "LIVE_CHAT_TRACK_MODULE");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "Profile_Module");
            this.constructTrackParamJSON(customerUtil.getDefaultCustomer(), "MDM_MODULE");
        }
    }
    
    private void constructTrackParamJSON(final Long customerId, final String moduleName) throws Exception {
        final SelectQuery sQuery = MDMCoreQuery.getInstance().getMDMTrackParamQuery(customerId, moduleName);
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        final Iterator paramItr = dO.getRows("MEMDMTrackParams");
        final JSONObject trackParams = new JSONObject();
        while (paramItr.hasNext()) {
            final Row trackParamRow = paramItr.next();
            final String paramName = (String)trackParamRow.get("PARAM_NAME");
            final String paramValue = (String)trackParamRow.get("PARAM_VALUE");
            trackParams.put(paramName, (Object)paramValue);
        }
        this.mdmTrackParamProperties.setProperty(moduleName, trackParams.toString());
    }
}
