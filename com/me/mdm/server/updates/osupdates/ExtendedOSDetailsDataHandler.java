package com.me.mdm.server.updates.osupdates;

import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.ds.query.Join;

public abstract class ExtendedOSDetailsDataHandler
{
    protected abstract Join getOSUpdateDetailsExtnJoin();
    
    protected abstract Row getExtnOSDetailsNewRow(final JSONObject p0) throws Exception;
}
