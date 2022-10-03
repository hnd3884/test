package com.adventnet.sym.server.mdm.ios;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import com.me.mdm.server.ios.apns.APNsWakeUpProcessor;
import com.me.mdm.server.ios.apns.APNsLegacyWakeUpProcessor;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.notification.WakeUpProcessor;

public abstract class APNSImpl extends WakeUpProcessor
{
    public static final String APNS_API_FALLBACK_PROPERTY = "APNSApiFallback";
    public static final String APNS_CONNECTION_ERROR = "ApnsConnectionError";
    private static Logger logger;
    public static Boolean apns_fallback;
    
    public static APNSImpl getInstance() {
        if (isApnsAPIFallback()) {
            APNSImpl.logger.log(Level.INFO, "APNSImpl Legacy flag is set, choosing old API");
            return APNsLegacyWakeUpProcessor.getInstance();
        }
        APNSImpl.logger.log(Level.INFO, "APNSImpl No legacy flag set, choosing new API");
        return APNsWakeUpProcessor.getInstance();
    }
    
    public abstract void reinitialize() throws Throwable;
    
    public abstract boolean IsAPNsReachacble();
    
    public abstract boolean wakeUpDeviceWithERID(final String p0, final String p1, final String p2, final HashMap p3);
    
    public static boolean isApnsAPIFallback() {
        try {
            if (APNSImpl.apns_fallback == null) {
                final String temp = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("APNSApiFallback", "false");
                APNSImpl.apns_fallback = temp.equalsIgnoreCase("true");
            }
        }
        catch (final Exception e) {
            APNSImpl.logger.log(Level.SEVERE, "APNSImpl: Error while isApnsAPIFallback().. So returning false.. ", e);
            APNSImpl.apns_fallback = false;
        }
        return APNSImpl.apns_fallback;
    }
    
    protected static void updateFallbackFlag(final boolean status) {
        APNSImpl.apns_fallback = status;
        MessageProvider.getInstance().hideMessage("APNS_PORT_BLOCKED");
    }
    
    public static void scheduledApiSwitchTask() {
        try {
            APNSImpl.logger.log(Level.INFO, "scheduledApiSwitchTask()..");
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIdsFromDB()[0];
            if (isApnsAPIFallback()) {
                final Boolean isConn = APNsWakeUpProcessor.getInstance().IsAPNsReachacble();
                if (isConn) {
                    APNSImpl.logger.log(Level.INFO, "APNSImpl Migrating from legacy to new API!");
                    updateFallbackFlag(false);
                    MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "APPLE_APNS_MODULE", "APNS_SWITCH_COUNT");
                    MEMDMTrackParamManager.getInstance().addOrUpdateTrackParam(customerId, "APPLE_APNS_MODULE", "IS_APNS_FALLBACK", "false");
                    ApiFactoryProvider.getCacheAccessAPI().putCache("ApnsConnectionError", (Object)"None", 1);
                }
                else {
                    APNSImpl.logger.log(Level.INFO, "APNSImpl New API is still not reachable!");
                    MEMDMTrackParamManager.getInstance().addOrUpdateTrackParam(customerId, "APPLE_APNS_MODULE", "IS_APNS_FALLBACK", "true");
                }
            }
            final Object errorMsg = ApiFactoryProvider.getCacheAccessAPI().getCache("ApnsConnectionError", 1);
            if (errorMsg == null) {
                MEMDMTrackParamManager.getInstance().addOrUpdateTrackParam(customerId, "APPLE_APNS_MODULE", "APNS_CONNECTION_ERROR", "None");
            }
            else {
                MEMDMTrackParamManager.getInstance().addOrUpdateTrackParam(customerId, "APPLE_APNS_MODULE", "APNS_CONNECTION_ERROR", errorMsg.toString());
            }
        }
        catch (final Exception e) {
            APNSImpl.logger.log(Level.SEVERE, "Exception in APNSImpl scheduledApiSwitchTask()", e);
        }
    }
    
    static {
        APNSImpl.logger = Logger.getLogger("MDMLogger");
        APNSImpl.apns_fallback = null;
    }
}
