package com.adventnet.db.adapter.postgres;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.util.SQLGeneratorForMSP;
import com.adventnet.ds.query.Query;

public class DCPostgresSQLGenerator extends PostgresSQLGenerator
{
    public String getSQLForSelect(Query query) throws QueryConstructionException {
        query = SQLGeneratorForMSP.getInstance().getCustomerCriteriaAppendedQuery(query);
        return super.getSQLForSelect(query);
    }
}
