package com.zoho.clustering.agent.remotemonitor.api;

import java.io.IOException;
import com.zoho.clustering.agent.remotemonitor.Monitor;
import com.zoho.clustering.agent.remotemonitor.MonitorPool;
import com.zoho.clustering.agent.util.ServletUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class StartMonitor extends AbstractServlet
{
    @Override
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String slaveId = ServletUtil.Param.value(request, "slaveId");
        final Monitor monitor = MonitorPool.getInst().getOrCreate(slaveId);
        monitor.start();
    }
}
