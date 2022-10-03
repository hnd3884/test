package com.me.devicemanagement.framework.server.util;

import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;

public interface PatchDataBaseSettingAPI
{
    public static final int MAC_OS_TYPE = 3;
    public static final int MAC_TP_TYPE = 4;
    
    JSONObject getPatchDataBaseSetting();
    
    Criteria getDomainExceptionListCri(final JSONObject p0);
    
    List getDomainExceptionList(final Criteria p0, final String p1);
    
    Long[] getSupportedDomainIdList(final String p0);
    
    void updateDBSync();
    
    Criteria getDomainExceptionListCri(final String p0, final Boolean p1, final Long[] p2);
    
    String isMacEnabledSetup();
    
    void setDomainExceptionListViewCriteria(final SelectQuery p0, final ViewContext p1);
    
    void performActionAfterDomainValidation();
}
