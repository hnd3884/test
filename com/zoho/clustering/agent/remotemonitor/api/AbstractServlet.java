package com.zoho.clustering.agent.remotemonitor.api;

import com.zoho.clustering.agent.util.ServletUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public abstract class AbstractServlet extends HttpServlet
{
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.process(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.process(request, response);
    }
    
    private void process(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            this.processRequest(request, response);
            ServletUtil.Write.text(response, "ok");
        }
        catch (final IllegalArgumentException exp) {
            ServletUtil.Write.text(response, 400, exp.getMessage());
        }
        catch (final RuntimeException exp2) {
            ServletUtil.Write.text(response, 500, exp2.getMessage());
        }
    }
    
    protected abstract void processRequest(final HttpServletRequest p0, final HttpServletResponse p1) throws IOException;
}
