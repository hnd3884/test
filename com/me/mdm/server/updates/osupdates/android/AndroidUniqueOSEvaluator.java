package com.me.mdm.server.updates.osupdates.android;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import com.me.mdm.server.updates.osupdates.OSUpdateCriteriaEvaluator;

public class AndroidUniqueOSEvaluator implements OSUpdateCriteriaEvaluator
{
    @Override
    public Criteria addedCriteria(final JSONObject updateInfo) throws Exception {
        final Integer updateType = updateInfo.getInt("UPDATE_TYPE");
        final Criteria cri = new Criteria(Column.getColumn("ManagedUpdates", "UPDATE_TYPE"), (Object)updateType, 0);
        return cri;
    }
    
    @Override
    public int evaluateAvailableDataForGivenCriteria(final JSONObject updateInfo, final DataObject dataObject) throws Exception {
        if (dataObject == null || dataObject.isEmpty()) {
            return 1;
        }
        return 2;
    }
}
