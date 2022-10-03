package com.me.ems.framework.common.api.v1.service;

import com.me.ems.framework.common.api.v1.model.LiteFilter;
import javax.ws.rs.core.MultivaluedMap;
import com.me.ems.framework.uac.api.v1.model.User;

public interface LiteFilterFetchValue
{
    LiteFilter fetchValuesForFilter(final String p0, final User p1, final MultivaluedMap p2);
}
