package com.me.ems.framework.common.summaryserver.summary.core;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.audit.EventLogUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import com.me.ems.framework.common.core.ActionLogDAOUtil;

public class SSActionLogDAOUtil extends ActionLogDAOUtil
{
    private Logger logger;
    
    public SSActionLogDAOUtil() {
        this.logger = Logger.getLogger(SSActionLogDAOUtil.class.getName());
    }
    
    @Override
    public List<Map<String, String>> getEventModuleList() throws Exception {
        final List<Map<String, String>> modulesList = new ArrayList<Map<String, String>>();
        try {
            final Map<String, String> actionLogModules = EventLogUtil.getInstance().getActionLogModules();
            if (CustomerInfoUtil.getInstance().isMSP()) {
                actionLogModules.remove("AD Reports");
                actionLogModules.put("SoM", I18N.getMsg("dc.common.Agent", new Object[0]));
            }
            this.removeNonSSModules(actionLogModules);
            for (final Map.Entry<String, String> moduleObject : actionLogModules.entrySet()) {
                final Map<String, String> moduleMap = new HashMap<String, String>(3);
                moduleMap.put("value", moduleObject.getKey());
                moduleMap.put("label", moduleObject.getValue());
                modulesList.add(moduleMap);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "ActionLogViewer Exception: Exception while getting event module list", ex);
            throw ex;
        }
        return modulesList;
    }
    
    private void removeNonSSModules(final Map<String, String> actionLogModules) {
        actionLogModules.remove("AD Reports");
        actionLogModules.remove("BMP");
        actionLogModules.remove("Chat");
        actionLogModules.remove("Configuration");
        actionLogModules.remove("Custom Group");
        actionLogModules.remove("Forwarding Server");
        actionLogModules.remove("MDM");
        actionLogModules.remove("Misconfiguration");
        actionLogModules.remove("Vulnerability");
        actionLogModules.remove("Web Server Misconfiguration");
        actionLogModules.remove("Remote Shutdown");
        actionLogModules.remove("System Tools");
        actionLogModules.remove("Wake On LAN");
    }
}
