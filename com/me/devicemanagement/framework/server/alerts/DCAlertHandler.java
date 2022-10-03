package com.me.devicemanagement.framework.server.alerts;

import java.util.Hashtable;
import java.util.Properties;

public abstract class DCAlertHandler
{
    public Hashtable handleAlert(final Properties props) throws Exception {
        return new Hashtable();
    }
}
