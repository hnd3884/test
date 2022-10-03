package com.me.ems.summaryserver.common.probeadministration.util;

import java.util.HashMap;
import java.util.Map;

public interface ProbeCRUDAPI
{
    HashMap addProbe(final Map p0);
    
    Long addProbeDetail(final Map p0);
    
    HashMap updateProbeDetail(final Map p0);
}
