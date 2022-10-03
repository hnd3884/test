package com.me.mdm.mdmmigration;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.json.JSONObject;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public abstract class MigrationServlet extends HttpServlet
{
    protected static Logger logger;
    
    public abstract MigrationContext prepareMigrationContext(final HttpServletRequest p0);
    
    protected final void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            final MigrationContext migrationContext = this.prepareMigrationContext(req);
            migrationContext.urlActionHandler.dispatchAction(req, resp);
        }
        catch (final UnsupportedOperationException uoe) {
            MigrationServlet.logger.log(Level.SEVERE, "MigrationServlet: MDM Migration not turned on for the service, so rejecting access");
        }
        catch (final Exception e) {
            MigrationServlet.logger.log(Level.SEVERE, "Error while doGet(): ", e);
        }
    }
    
    protected final void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            final MigrationContext migrationContext = this.prepareMigrationContext(req);
            final String requestData = this.getRequestData(req);
            final JSONObject requestJson = new JSONObject(requestData);
            MigrationServlet.logger.log(Level.INFO, "MigrationServlet: doPost() APIRequestHandler Request data: {0}", requestData);
            final JSONObject responseJson = migrationContext.apiRequestHandler.processRequest(requestJson);
            MigrationServlet.logger.log(Level.INFO, "MigrationServlet: doPost() APIRequestHandler Response data: {0}", responseJson.toString());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(responseJson.toString());
        }
        catch (final UnsupportedOperationException uoe) {
            MigrationServlet.logger.log(Level.SEVERE, "MigrationServlet: MDM Migration not turned on for the service, so rejecting access");
        }
        catch (final Exception e) {
            MigrationServlet.logger.log(Level.SEVERE, "Error while doPost(): ", e);
        }
    }
    
    public String getRequestData(final HttpServletRequest request) {
        Reader reader = null;
        String requestData = null;
        try {
            reader = new BufferedReader(new InputStreamReader((InputStream)request.getInputStream(), Charset.forName("UTF-8")));
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            int read;
            while ((read = reader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            requestData = strBuilder.toString();
        }
        catch (final IOException ex) {
            MigrationServlet.logger.log(Level.WARNING, "Exception occurred while converting the HTTPServlet Request's InputStream to Reader in getProperEncodedReader(): ", ex);
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final IOException e) {
                    MigrationServlet.logger.log(Level.WARNING, "Exception occurred while closing reader: ", e);
                }
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final IOException e2) {
                    MigrationServlet.logger.log(Level.WARNING, "Exception occurred while closing reader: ", e2);
                }
            }
        }
        return requestData;
    }
    
    static {
        MigrationServlet.logger = Logger.getLogger("MDMLogger");
    }
}
