package com.me.mdm.server.datausage;

import com.me.mdm.server.datausage.data.DataUsageHistory;
import java.util.List;
import org.json.JSONObject;

public interface DataUsageParsingInterface
{
    void parseDataUsageSummary(final Long p0, final JSONObject p1, final List<DataUsageHistory> p2) throws Exception;
    
    void parsePerAppUsageSummary(final Long p0, final JSONObject p1, final List<DataUsageHistory> p2) throws Exception;
}
