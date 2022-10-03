package com.me.mdm.server.updates.osupdates;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;

public interface OSUpdateCriteriaEvaluator
{
    public static final int NEW_UPDATE = 1;
    public static final int EXISTING_UPDATE = 2;
    
    Criteria addedCriteria(final JSONObject p0) throws Exception;
    
    int evaluateAvailableDataForGivenCriteria(final JSONObject p0, final DataObject p1) throws Exception;
}
