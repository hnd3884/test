package com.adventnet.authentication.saml;

import java.io.IOException;
import javax.servlet.ServletException;
import java.io.OutputStream;
import com.adventnet.authentication.saml.settings.Metadata;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class SamlSPMetaServlet extends HttpServlet
{
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        String metadata = null;
        try {
            final Metadata metadataObj = new Metadata();
            metadata = metadataObj.getMetadataString();
        }
        catch (final Exception e) {
            throw new Exception(e);
        }
        response.setHeader("Content-Disposition", "attachment; filename=metadata.xml");
        final OutputStream os = (OutputStream)response.getOutputStream();
        final StringBuilder sb = new StringBuilder();
        sb.append(metadata);
        os.write(sb.toString().getBytes("UTF-8"));
        os.flush();
        os.close();
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            this.processRequest(request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new ServletException("Exception during SAML authentication" + e);
        }
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            this.processRequest(request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new ServletException("Exception during SAML authentication" + e);
        }
    }
}
