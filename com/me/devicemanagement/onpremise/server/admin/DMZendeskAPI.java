package com.me.devicemanagement.onpremise.server.admin;

import java.util.Properties;

public interface DMZendeskAPI
{
    Properties getUserMappedToZendesk(final Long p0);
    
    String fetchAPIKeyForTech(final Long p0, final int p1);
    
    void deleteZendeskUserMappingForAdmin(final Long p0);
}
