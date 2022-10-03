package com.zoho.mickey.db;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Range;

public interface SQLModifier
{
    String getSQLForSelectWithRange(final String p0, final Range p1) throws QueryConstructionException;
    
    String getSQLForUnionWithRange(final String p0, final Range p1) throws QueryConstructionException;
}
