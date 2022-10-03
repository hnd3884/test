package com.adventnet.ds.adapter;

import com.adventnet.ds.query.SelectQuery;

public abstract class AbstractFunctionHandler implements FunctionHandler
{
    SelectQuery actualSQ;
    SelectQuery sqForDataRetrieval;
    
    public AbstractFunctionHandler() {
        this.actualSQ = null;
        this.sqForDataRetrieval = null;
    }
    
    @Override
    public void init(final SelectQuery actualSQ, final SelectQuery sqForDataRetrieval) throws DataSourceException {
        this.actualSQ = actualSQ;
        this.sqForDataRetrieval = sqForDataRetrieval;
    }
}
