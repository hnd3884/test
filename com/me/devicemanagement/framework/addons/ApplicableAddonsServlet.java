package com.me.devicemanagement.framework.addons;

import java.io.IOException;
import java.io.PrintWriter;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class ApplicableAddonsServlet extends HttpServlet
{
    Logger logger;
    
    public ApplicableAddonsServlet() {
        this.logger = Logger.getLogger("AddOnsLogger");
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)request.getInputStream(), Charset.forName("UTF-8")));
        String responseContent = "";
        String readContent = null;
        while ((readContent = reader.readLine()) != null && readContent.length() != 0) {
            responseContent += readContent;
        }
        JSONArray respJSON = new JSONArray();
        try {
            final JSONObject jsonObject = new JSONObject(responseContent);
            respJSON = AddOnHandler.getInstance().getApplicableAddons(jsonObject.optString("application_name"));
            response.setContentType("application/json");
            final PrintWriter pout = response.getWriter();
            pout.print(respJSON.toString());
            pout.close();
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "There is an error in retreiving the details of the AddOns from the database", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "There is some error in getting applicable addon details for the given application", e2);
        }
    }
}
