package com.zoho.mickey.api;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Range;
import com.zoho.mickey.db.SQLModifier;

public class SQLStringAPI
{
    private SQLModifier sqlModifier;
    private static SQLStringAPI sqlApi;
    
    public SQLStringAPI(final SQLModifier sqlModifier) {
        this.sqlModifier = null;
        this.sqlModifier = sqlModifier;
        SQLStringAPI.sqlApi = this;
    }
    
    public static SQLStringAPI getInstance() {
        return SQLStringAPI.sqlApi;
    }
    
    public String getSQLForSelectWithRange(final String selectSQL, final Range range) throws QueryConstructionException {
        return this.sqlModifier.getSQLForSelectWithRange(selectSQL, range);
    }
    
    public String getSQLForUnionWithRange(final String unionSQL, final Range range) throws QueryConstructionException {
        return this.sqlModifier.getSQLForUnionWithRange(unionSQL, range);
    }
    
    static {
        SQLStringAPI.sqlApi = null;
    }
}
