package com.me.devicemanagement.onpremise.server.patch;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.patch.EPMPatchUtilAPI;

public class EPMPatchUtilImpl implements EPMPatchUtilAPI
{
    private static final Logger LOGGER;
    
    public String getPatchCrawlerParamsValue(final String paramName) {
        String paramValue = "";
        try {
            final Criteria paramNameCri = new Criteria(Column.getColumn("PatchCrawlerParams", "PARAM_NAME"), (Object)paramName, 0);
            final DataObject paramsDobj = SyMUtil.getPersistence().get("PatchCrawlerParams", paramNameCri);
            if (!paramsDobj.isEmpty()) {
                final Iterator iterate = paramsDobj.getRows("PatchCrawlerParams");
                while (iterate.hasNext()) {
                    final Row paramRow = iterate.next();
                    paramValue = (String)paramRow.get("PARAM_VALUE");
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return paramValue;
    }
    
    public String getUserAgent() {
        String userAgent = null;
        try {
            userAgent = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("USER_AGENT");
            if (userAgent != null) {
                final DataObject serverDO = SyMUtil.getPersistence().get("DCServerInfo", (Criteria)null);
                if (serverDO != null && !serverDO.isEmpty()) {
                    final Long serverInstanceID = (Long)serverDO.getFirstValue("DCServerInfo", "SERVER_INSTANCE_ID");
                    userAgent = String.valueOf(serverInstanceID);
                    ApiFactoryProvider.getCacheAccessAPI().putCache("USER_AGENT", (Object)userAgent);
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        if (userAgent == null) {
            userAgent = System.getProperty("http.agent");
            if (userAgent == null) {
                userAgent = "ManageEngine Endpoint Central";
            }
        }
        return userAgent;
    }
    
    static {
        LOGGER = Logger.getLogger(EPMPatchUtilImpl.class.getName());
    }
}
