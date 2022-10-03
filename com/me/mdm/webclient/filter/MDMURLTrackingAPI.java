package com.me.mdm.webclient.filter;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public interface MDMURLTrackingAPI
{
    void postData(final JSONObject p0, final HttpServletRequest p1);
}
