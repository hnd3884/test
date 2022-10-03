package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.dd.plist.NSDictionary;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;

public interface DO2Settings
{
    List<NSDictionary> createSettingCommand(final DataObject p0, final JSONObject p1);
}
