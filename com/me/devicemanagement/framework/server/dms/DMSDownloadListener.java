package com.me.devicemanagement.framework.server.dms;

public interface DMSDownloadListener
{
    DMSDownloadEvent preFileDownload(final DMSDownloadEvent p0);
    
    DMSDownloadEvent postDownloadEvent(final DMSDownloadEvent p0);
}
