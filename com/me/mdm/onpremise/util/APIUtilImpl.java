package com.me.mdm.onpremise.util;

import com.me.mdm.api.APIUtil;

public class APIUtilImpl extends APIUtil
{
    public String getPredefinedURL(final String url, final String module) {
        return "predefined.".concat(url);
    }
}
