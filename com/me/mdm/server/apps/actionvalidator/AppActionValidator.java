package com.me.mdm.server.apps.actionvalidator;

import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public interface AppActionValidator
{
    JSONObject validate(final JSONObject p0, final DataObject p1) throws Exception;
}
