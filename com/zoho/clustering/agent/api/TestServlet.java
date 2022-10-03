package com.zoho.clustering.agent.api;

import java.io.IOException;
import javax.servlet.ServletException;
import com.zoho.clustering.agent.util.ServletUtil;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class TestServlet extends HttpServlet
{
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        ServletUtil.Write.text(response, "date: " + new Date(System.currentTimeMillis()));
        ServletUtil.Write.text(response, "\ncontext-path: " + request.getContextPath());
        ServletUtil.Write.text(response, "\nservlet-path: " + request.getServletPath());
        ServletUtil.Write.text(response, "\npath-info: " + request.getPathInfo());
        ServletUtil.Write.text(response, "\n\nreq-uri: " + request.getRequestURI());
        ServletUtil.Write.text(response, "\n\nreq-URL: " + request.getRequestURL().toString());
    }
}
