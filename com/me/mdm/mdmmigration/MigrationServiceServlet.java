package com.me.mdm.mdmmigration;

import java.util.logging.Level;
import com.me.mdm.mdmmigration.airwatch.AirwatchAPIRequestHandler;
import com.me.mdm.mdmmigration.jamf.JamfAPIRequestHandler;
import com.me.mdm.mdmmigration.meraki.MerakiAPIRequestHandler;
import com.me.mdm.mdmmigration.mobileiron.MobileironAPIRequestHandler;
import com.me.mdm.mdmmigration.ibmmaas.MaasAPIRequestHandler;
import com.me.mdm.mdmmigration.meonpremise.MEOnPremiseAPIRequestHandler;
import com.me.mdm.mdmmigration.meonpremise.MEOnPremiseURLActionHandler;
import com.me.mdm.mdmmigration.mecloud.MECloudAPIRequestHandler;
import com.me.mdm.mdmmigration.mecloud.MECloudURLActionHandler;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;

public class MigrationServiceServlet extends MigrationServlet
{
    @Override
    public MigrationContext prepareMigrationContext(final HttpServletRequest req) {
        final MigrationContext context = new MigrationContext();
        try {
            final Long configId = Long.valueOf(req.getParameter("configID"));
            final JSONObject queryExistingJSON = new JSONObject();
            queryExistingJSON.put("CONFIG_ID", (Object)configId);
            final JSONObject apiDetails = new APIServiceDataHandler().getAPIServiceConfigDetails(queryExistingJSON);
            final JSONObject apiServiceConfig = apiDetails.getJSONObject("APIServiceConfiguration");
            final int serviceId = apiServiceConfig.getInt("SERVICE_ID".toLowerCase());
            switch (serviceId) {
                case 1: {
                    context.urlActionHandler = new MECloudURLActionHandler();
                    context.apiRequestHandler = new MECloudAPIRequestHandler();
                    break;
                }
                case 2: {
                    context.urlActionHandler = new MEOnPremiseURLActionHandler();
                    context.apiRequestHandler = new MEOnPremiseAPIRequestHandler();
                    break;
                }
                case 4: {
                    context.urlActionHandler = new MECloudURLActionHandler();
                    context.apiRequestHandler = new MaasAPIRequestHandler();
                    break;
                }
                case 5: {
                    context.apiRequestHandler = new MobileironAPIRequestHandler();
                    context.urlActionHandler = new MECloudURLActionHandler();
                    break;
                }
                case 6: {
                    context.urlActionHandler = new MECloudURLActionHandler();
                    context.apiRequestHandler = new MerakiAPIRequestHandler();
                    break;
                }
                case 15: {
                    context.apiRequestHandler = new JamfAPIRequestHandler();
                    context.urlActionHandler = new MECloudURLActionHandler();
                    break;
                }
                case 3: {
                    context.apiRequestHandler = new AirwatchAPIRequestHandler();
                    context.urlActionHandler = new MECloudURLActionHandler();
                    break;
                }
            }
        }
        catch (final Exception e) {
            MigrationServiceServlet.logger.log(Level.SEVERE, "Exception in prepare context");
        }
        return context;
    }
}
