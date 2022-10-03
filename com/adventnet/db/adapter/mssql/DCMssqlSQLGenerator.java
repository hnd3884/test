package com.adventnet.db.adapter.mssql;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.util.SQLGeneratorForMSP;
import com.adventnet.ds.query.Query;

public class DCMssqlSQLGenerator extends MssqlSQLGenerator
{
    public String getSQLForSelect(Query query) throws QueryConstructionException {
        query = SQLGeneratorForMSP.getInstance().getCustomerCriteriaAppendedQuery(query);
        return super.getSQLForSelect(query);
    }
}
