package com.adventnet.ds.adapter;

import java.util.List;
import com.adventnet.ds.query.SelectQuery;

public interface FunctionHandler
{
    void init(final SelectQuery p0, final SelectQuery p1) throws DataSourceException;
    
    void processNextRow(final List p0, final List p1) throws DataSourceException;
}
