package com.adventnet.authorization;

import com.adventnet.ds.query.Criteria;

public interface TableAccessSPI
{
    Criteria update(final Criteria p0);
}
