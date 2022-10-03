package com.adventnet.db.adapter.mysql;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.util.SQLGeneratorForMSP;
import com.adventnet.ds.query.Query;

public class DCMysqlSQLGenerator extends MysqlSQLGenerator
{
    public String getSQLForSelect(Query query) throws QueryConstructionException {
        query = SQLGeneratorForMSP.getInstance().getCustomerCriteriaAppendedQuery(query);
        return super.getSQLForSelect(query);
    }
}
