package com.me.devicemanagement.framework.webclient.authentication;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.SelectQuery;

public interface RBCAQueryAPI
{
    SelectQuery getQuery(final SelectQuery p0, final HttpServletRequest p1);
}
