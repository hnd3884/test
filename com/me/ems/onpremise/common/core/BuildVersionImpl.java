package com.me.ems.onpremise.common.core;

import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.util.Map;
import com.me.ems.framework.common.core.BuildVersionAPI;

public class BuildVersionImpl extends com.me.ems.framework.common.core.BuildVersionImpl implements BuildVersionAPI
{
    public Map<String, Object> getBuildVersionDetails() throws Exception {
        final Map<String, Object> buildVersionMap = super.getBuildVersionDetails();
        this.showReadMe(buildVersionMap);
        return buildVersionMap;
    }
    
    private void showReadMe(final Map<String, Object> buildVersionMap) {
        buildVersionMap.put("productVersion", this.productProperties.getProperty("productversion"));
        try {
            final Integer currBuildNo = (Integer)DBUtil.getMaxOfValue("DCServerBuildHistory", "BUILD_NUMBER", (Criteria)null);
            final Integer baseBuildNo = (Integer)DBUtil.getMinOfValue("DCServerBuildHistory", "BUILD_NUMBER", (Criteria)null);
            buildVersionMap.put("showReadMe", !currBuildNo.equals(baseBuildNo));
        }
        catch (final Exception ex) {
            BuildVersionImpl.logger.log(Level.WARNING, "Exception occurs while checking the server is fresh pack or not", ex);
            buildVersionMap.put("showReadMe", Boolean.FALSE);
        }
    }
}
