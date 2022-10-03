package com.me.mdm.server.apps;

import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import org.json.JSONObject;
import com.me.mdm.server.apps.ios.IOSAppAdditionDataProvider;
import com.me.mdm.server.apps.android.AndroidAppAdditionDataProvider;

public abstract class BaseAppAdditionDataProvider
{
    public static BaseAppAdditionDataProvider getInstance(final int platform) throws Exception {
        switch (platform) {
            case 2: {
                return new AndroidAppAdditionDataProvider();
            }
            case 1: {
                return new IOSAppAdditionDataProvider();
            }
            default: {
                return new DefaultAppAdditionDataProvider();
            }
        }
    }
    
    public abstract JSONObject modifyAppAdditionData(final JSONObject p0) throws Exception;
    
    public Long getReleaseLabel(final JSONObject appObject) throws DataAccessException {
        Long appReleaseLabelID = appObject.optLong("RELEASE_LABEL_ID", -1L);
        if (appReleaseLabelID.equals(-1L)) {
            appReleaseLabelID = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer((Long)appObject.get("CUSTOMER_ID"));
        }
        return appReleaseLabelID;
    }
}
