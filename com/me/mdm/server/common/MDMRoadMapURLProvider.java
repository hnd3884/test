package com.me.mdm.server.common;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;

public class MDMRoadMapURLProvider
{
    private static final String ROADMAP_MDM_ADD_DETAILS = "/product-roadmap-add-details.html";
    private static final String ROADMAP_DC_ADD_DETAILS = "/product-roadmap-mobile-add-details.html";
    public static final int MDM_PROFILE = 15;
    public static final int DATA_USAGE_TRACKING = 1;
    public static final int EAS = 13;
    
    public static String getMDMRoadMapURL(final int id) {
        String roadmapDoc;
        if (DMApplicationHandler.isMdmProduct()) {
            roadmapDoc = "/product-roadmap-add-details.html";
        }
        else {
            roadmapDoc = "/product-roadmap-mobile-add-details.html";
        }
        final String parameters = ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue();
        final String idString = "id=" + id;
        final String roadMapURL = ProductUrlLoader.getInstance().getValue("prodUrl") + roadmapDoc + "?" + idString + "&" + parameters;
        return roadMapURL;
    }
}
