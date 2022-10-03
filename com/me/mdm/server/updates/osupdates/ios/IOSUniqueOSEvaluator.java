package com.me.mdm.server.updates.osupdates.ios;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import com.me.mdm.server.updates.osupdates.OSUpdateCriteriaEvaluator;

public class IOSUniqueOSEvaluator implements OSUpdateCriteriaEvaluator
{
    @Override
    public Criteria addedCriteria(final JSONObject updateInfo) throws Exception {
        final String productKey = updateInfo.getJSONObject("IOSUpdates").get("PRODUCT_KEY").toString();
        final Criteria cri = new Criteria(Column.getColumn("IOSUpdates", "PRODUCT_KEY"), (Object)productKey, 0);
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
