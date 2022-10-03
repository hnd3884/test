package com.me.devicemanagement.onpremise.start.servlet;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class ServerStatusServlet extends HttpServlet
{
    public void doPost(final HttpServletRequest servReq, final HttpServletResponse servResp) throws ServletException, IOException {
        this.doGet(servReq, servResp);
    }
    
    public void doGet(final HttpServletRequest req, final HttpServletResponse res) throws IOException, ServletException {
        BufferedOutputStream bos = null;
        try {
            res.setContentType("text/plain");
            bos = new BufferedOutputStream((OutputStream)res.getOutputStream());
            final byte[] b = "OpsymServerRunning".getBytes();
            bos.write(b, 0, b.length);
            bos.flush();
        }
        catch (final Exception ex) {}
        finally {
            if (bos != null) {
                bos.close();
            }
        }
    }
}
