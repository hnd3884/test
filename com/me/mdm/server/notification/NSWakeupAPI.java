package com.me.mdm.server.notification;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

public abstract class NSWakeupAPI extends WakeUpProcessor
{
    @Override
    public abstract HashMap wakeUpDevices(final List p0, final int p1);
    
    public abstract JSONObject getNSConfig();
}
