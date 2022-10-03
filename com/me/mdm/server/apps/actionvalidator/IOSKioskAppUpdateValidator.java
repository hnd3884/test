package com.me.mdm.server.apps.actionvalidator;

import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class IOSKioskAppUpdateValidator implements AppActionValidator
{
    @Override
    public JSONObject validate(final JSONObject request, final DataObject dataObject) throws Exception {
        final IOSKioskAppRemoveValidator handler = new IOSKioskAppRemoveValidator();
        final JSONObject object = handler.iOSKioskProcessor(request, dataObject);
        object.put("I18NRemark", (Object)"mdm.apps.ios.kioskUpdateNote");
        return object;
    }
}
