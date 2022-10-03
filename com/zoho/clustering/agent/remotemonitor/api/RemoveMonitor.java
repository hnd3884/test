package com.zoho.clustering.agent.remotemonitor.api;

import java.io.IOException;
import com.zoho.clustering.agent.remotemonitor.MonitorPool;
import com.zoho.clustering.agent.util.ServletUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RemoveMonitor extends AbstractServlet
{
    @Override
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String slaveId = ServletUtil.Param.value(request, "slaveId");
        MonitorPool.getInst().removeMonitor(slaveId);
    }
}
