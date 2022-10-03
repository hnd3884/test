package com.me.mdm.server.enrollment.admin;

import org.json.JSONObject;

public interface AdminEnrollmentDownloadInterface
{
    String getFileDownloadPath(final JSONObject p0) throws Exception;
}
