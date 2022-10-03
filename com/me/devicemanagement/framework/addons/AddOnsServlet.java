package com.me.devicemanagement.framework.addons;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.PrintWriter;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import org.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class AddOnsServlet extends HttpServlet
{
    Logger logger;
    
    public AddOnsServlet() {
        this.logger = Logger.getLogger("SecurityAddonLogger");
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String responseData = "";
        try {
            if (!AddOnHandler.getInstance().getAddOnUpdateRunningStatus()) {
                responseData = AddOnHandler.getInstance().getAddOnsDetails().toString();
            }
            else {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("error", (Object)"updating the addon status");
                responseData = jsonObject.toString();
            }
            response.setContentType("application/json");
            final PrintWriter pout = response.getWriter();
            pout.print(responseData);
            pout.close();
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "There is an error in getting the details of the AddOns from the database", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "There is some error while getting the addon details", e2);
        }
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)request.getInputStream(), Charset.forName("UTF-8")));
        final StringBuilder responseContent = new StringBuilder();
        String readContent = null;
        while ((readContent = reader.readLine()) != null && readContent.length() != 0) {
            responseContent.append(readContent);
        }
        final JSONObject respJSON = new JSONObject();
        try {
            final JSONObject jsonObject = new JSONObject(responseContent.toString());
            boolean status = AddOnHandler.getInstance().enableOrDisableAddOns(jsonObject);
            if (status) {
                final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                if (isMSP && jsonObject.has("apply_for_all_customers") && jsonObject.getBoolean("apply_for_all_customers")) {
                    status = AddOnHandler.getInstance().enableOrDisableAddOnsForAllCustomers(jsonObject);
                }
                else {
                    if (!isMSP && !jsonObject.has("customer_id")) {
                        jsonObject.put("customer_id", (Object)CustomerInfoUtil.getInstance().getCustomerId());
                    }
                    status = AddOnHandler.getInstance().updateAddOnStatusForCustomers(jsonObject);
                }
            }
            respJSON.put("updated_addon_status", status);
            if (!status) {
                respJSON.put("error", (Object)jsonObject.optString("error", "something went wrong"));
            }
            else {
                ApiFactoryProvider.getUtilAccessAPI().invokeOnpremiseComponents();
                this.logger.log(Level.INFO, "SGS proxy data sync triggered after add on addition/modification");
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception in the jsonobject : ", (Throwable)e);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " *** Exception while updating status *** ", ex);
        }
        response.setContentType("application/json");
        final PrintWriter pout = response.getWriter();
        pout.print(respJSON.toString());
        pout.close();
    }
}
