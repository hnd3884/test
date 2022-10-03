package com.me.devicemanagement.framework.server.license.summaryserver.common;

import java.util.ArrayList;
import java.util.List;

public class LicenseDataConstants
{
    public static final int USERS = 0;
    public static final int OSD_SERVER_OS = 1;
    public static final int OSD_WORKSTATION_OS = 2;
    public static final int SERVERS = 3;
    public static final int COMPUTERS = 4;
    public static final int DEVICES = 5;
    public static final int UNIFIED_MC_IMG = 6;
    public static final int OSD = 7;
    public static final int TOTAL_MACHINES = 8;
    public static final List<Integer> RESOURCE_TYPES;
    public static final String PROBE_RESOURCE_COUNT = "probeResourceCount";
    public static final String PROBE_RESOURCE_TOTAL_COUNT = "probeResourceTotalCount";
    public static final String SS_LICENSE_DETAILS = "summaryserver/license";
    public static final String LIMIT = "limit";
    public static final String RESOURCE_COUNT = "resourceCount";
    public static final String FILE = "file";
    public static final String PATH_SEPARATOR = "/";
    public static final String EXCEEDED = "exceeded";
    public static final String REACHED = "reached";
    public static final String COMPONENT = "component";
    public static final String CHECK = "check";
    public static final String LIMIT_CHECK_MIME = "application/limitCheck.v1+json";
    public static final String RESOURCE_COUNT_MIME = "application/resourceCount.v1+json";
    public static final String STATUS = "status";
    public static final String TOTAL_COUNT = "totalCount";
    public static final String SUCCESS = "Success";
    public static final String PS_LICENSE_API = "com.me.dconpremise.server.license.summaryserver.probe.ProbeGeneralLicenseImpl";
    public static final String PS_MDM_LICENSE_API = "com.me.dconpremise.server.license.summaryserver.probe.ProbeMDMLicenseImpl";
    public static final String PS_DC_LICENSE_API = "com.me.dconpremise.server.license.summaryserver.probe.ProbeLicenseImpl";
    public static final String PS_OSD_LICENSE_API = "com.me.dconpremise.server.license.summaryserver.probe.ProbeOSDLicenseImpl";
    public static final String SS_MDM_LICENSE_API = "com.me.dconpremise.server.license.summaryserver.summary.SummaryMDMLicenseImpl";
    public static final String SS_DC_LICENSE_API = "com.me.dconpremise.server.license.summaryserver.summary.SummaryLicenseImpl";
    public static final String SS_OSD_LICENSE_API = "com.me.dconpremise.server.license.summaryserver.summary.SummaryOSDLicenseImpl";
    
    private LicenseDataConstants() {
    }
    
    static {
        (RESOURCE_TYPES = new ArrayList<Integer>()).add(3);
        LicenseDataConstants.RESOURCE_TYPES.add(4);
        LicenseDataConstants.RESOURCE_TYPES.add(7);
        LicenseDataConstants.RESOURCE_TYPES.add(5);
        LicenseDataConstants.RESOURCE_TYPES.add(8);
        LicenseDataConstants.RESOURCE_TYPES.add(6);
        LicenseDataConstants.RESOURCE_TYPES.add(1);
        LicenseDataConstants.RESOURCE_TYPES.add(2);
    }
}
