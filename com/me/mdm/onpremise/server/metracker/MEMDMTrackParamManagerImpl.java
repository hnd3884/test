package com.me.mdm.onpremise.server.metracker;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;

public class MEMDMTrackParamManagerImpl extends MEMDMTrackParamManager
{
    Logger logger;
    
    public MEMDMTrackParamManagerImpl() {
        (this.logger = Logger.getLogger(MEMDMTrackParamManagerImpl.class.getName())).log(Level.INFO, "----------- MEMDMTrackParamManager class object is created -----------");
    }
    
    public void putTrackParamInCache(final Long customerId, final String module, final String paramName, String paramValue, final String paramType) throws JSONException {
        final CacheAccessAPI cacheAccess = ApiFactoryProvider.getCacheAccessAPI();
        final String trackParamsString = (String)cacheAccess.getCache("ME_MDM_TRACK_PARAMS", 2);
        final JSONObject trackParams = (trackParamsString == null) ? new JSONObject() : new JSONObject(trackParamsString);
        JSONObject customerTrackParams = trackParams.optJSONObject(customerId.toString());
        customerTrackParams = ((customerTrackParams == null) ? new JSONObject() : customerTrackParams);
        JSONObject typeTrackParams = customerTrackParams.optJSONObject(paramType);
        typeTrackParams = ((typeTrackParams == null) ? new JSONObject() : typeTrackParams);
        JSONObject moduleTrack = typeTrackParams.optJSONObject(module);
        moduleTrack = ((moduleTrack == null) ? new JSONObject() : moduleTrack);
        if (paramType.equals("INCREMENT_PARAM")) {
            paramValue = Integer.toString(moduleTrack.optInt(paramName) + 1);
        }
        moduleTrack.put(paramName, (Object)paramValue);
        typeTrackParams.put(module, (Object)moduleTrack);
        customerTrackParams.put(paramType, (Object)typeTrackParams);
        trackParams.put(customerId.toString(), (Object)customerTrackParams);
        cacheAccess.putCache("ME_MDM_TRACK_PARAMS", (Object)trackParams.toString(), 2);
    }
}
