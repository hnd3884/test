package com.me.ems.framework.common.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.framework.server.core.TimezoneUtil;
import com.me.framework.server.model.DCTimezone;
import java.util.List;

public class TimeZoneService
{
    public List<DCTimezone> getTimezones() throws APIException {
        try {
            return TimezoneUtil.getAvailableTimezones();
        }
        catch (final Exception e) {
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.admin.common.timezone_not_found");
        }
    }
}
