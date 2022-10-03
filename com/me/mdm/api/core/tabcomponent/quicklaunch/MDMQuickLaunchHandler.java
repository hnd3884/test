package com.me.mdm.api.core.tabcomponent.quicklaunch;

import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import com.me.ems.framework.server.quicklaunch.core.QuickLaunchHandler;

public class MDMQuickLaunchHandler implements QuickLaunchHandler
{
    private static final String SCHEDULE_SCAN = "scheduleScan";
    private static final String GEO_TRACKING = "geoTracking";
    private static final String REBRANDING = "rebranding";
    private static final String GETTING_STARTED = "gettingStarted";
    
    public Map<String, Object> customHandling(final Map<String, Object> moduleObject, final User user) throws Exception {
        if (moduleObject.get("id").equals("scheduleScan")) {
            final Boolean isProfessionalEdition = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            final Boolean isEnterpriseEdition = LicenseProvider.getInstance().getMDMLicenseAPI().isEnterpriseLicenseEdition();
            return moduleObject;
        }
        if (moduleObject.get("id").equals("geoTracking")) {
            final Boolean isProfessionalEdition = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            final Boolean isEnterpriseEdition = LicenseProvider.getInstance().getMDMLicenseAPI().isEnterpriseLicenseEdition();
            final Boolean isGeoTrackingEnabled = MDMUtil.getInstance().isGeoTrackingEnabled();
            if (isGeoTrackingEnabled) {
                return moduleObject;
            }
            return new HashMap<String, Object>(1);
        }
        else if (moduleObject.get("id").equals("rebranding")) {
            if (DMApplicationHandler.isMdmProduct()) {
                return moduleObject;
            }
            return new HashMap<String, Object>(1);
        }
        else {
            if (moduleObject.get("id").equals("gettingStarted")) {
                if (DMApplicationHandler.isMdmProduct()) {
                    moduleObject.put("url", moduleObject.get("mdmUrl"));
                }
                else {
                    moduleObject.put("url", moduleObject.get("dcUrl"));
                }
                return moduleObject;
            }
            return moduleObject;
        }
    }
}
