package com.me.mdm.onpremise.server.status;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class MDMPServerStatusServlet extends HttpServlet
{
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        BufferedOutputStream outputStream = null;
        try {
            response.setContentType("text/plain");
            outputStream = new BufferedOutputStream((OutputStream)response.getOutputStream());
            final byte[] b = "MDMPServerRunning".getBytes();
            outputStream.write(b, 0, b.length);
            outputStream.flush();
        }
        catch (final Exception ex) {}
        finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
