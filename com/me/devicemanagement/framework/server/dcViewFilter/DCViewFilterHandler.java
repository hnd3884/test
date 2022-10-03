package com.me.devicemanagement.framework.server.dcViewFilter;

import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;

public interface DCViewFilterHandler
{
    String getNativeDCViewFilterCriteria(final DCViewFilterCriteria p0);
    
    Criteria getDCViewFilterCriteria(final DCViewFilterCriteria p0);
}
