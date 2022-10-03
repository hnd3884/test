package com.adventnet.db.migration.test;

import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;

public interface SanityTestHandler
{
    void preInvokeForSelectSQL(final SelectQuery p0);
    
    boolean isDiffIgnorable(final String p0, final JSONObject p1);
}
