package com.zoho.security.eventfw.builtinfieldsImpl;

import com.zoho.security.eventfw.CalleeInfo;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.config.DataFields;
import java.util.Map;

public interface BuiltInFieldsProvider
{
    void fillData(final Map<String, Object> p0, final DataFields p1, final ExecutionTimer p2, final CalleeInfo p3);
}
