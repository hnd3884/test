package org.apache.commons.digester.plugins;

import org.apache.commons.logging.impl.NoOpLog;
import org.apache.commons.logging.Log;
import org.apache.commons.digester.Digester;

class LogUtils
{
    static Log getLogger(final Digester digester) {
        if (digester == null) {
            return (Log)new NoOpLog();
        }
        return digester.getLogger();
    }
}
