package com.zoho.clustering.agent.api;

import com.zoho.clustering.agent.remotemonitor.MonitorPool;
import com.zoho.clustering.agent.util.ServletUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class StatusCheck extends HttpServlet
{
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            final String slaveId = ServletUtil.Param.optionalValue(request, "slaveId");
            if (MonitorPool.isEnabled() && slaveId != null) {
                MonitorPool.getInst().getOrCreate(slaveId).updateLastAccessTime();
            }
            ServletUtil.Write.text(response, "ok");
        }
        catch (final IllegalArgumentException exp) {
            ServletUtil.Write.text(response, 400, exp.getMessage());
        }
        catch (final RuntimeException exp2) {
            ServletUtil.Write.text(response, 500, exp2.getMessage());
        }
    }
}
