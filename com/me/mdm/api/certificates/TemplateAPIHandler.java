package com.me.mdm.api.certificates;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.certificates.templates.TemplateHandler;
import com.me.mdm.api.APIUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.templates.TemplateConstants;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class TemplateAPIHandler extends ApiRequestHandler
{
    public static Logger logger;
    
    @Override
    public Object doGet(final APIRequest apiRequest) {
        final JSONObject response = new JSONObject();
        try {
            if (apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("filters").has(TemplateConstants.Api.Key.template_type)) {
                final int template_type = apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("filters").getInt(TemplateConstants.Api.Key.template_type);
                TemplateAPIHandler.logger.log(Level.FINE, "doGet: Getting templates for type: {0}", template_type);
                final JSONObject tempJson = new JSONObject();
                tempJson.put("TEMPLATE_TYPE", template_type);
                tempJson.put("CUSTOMER_ID", (Object)APIUtil.optCustomerID(apiRequest.toJSONObject()));
                final JSONObject templateDetails = TemplateHandler.getTemplateDetailsForTemplateType(tempJson);
                response.put("RESPONSE", (Object)templateDetails);
                TemplateAPIHandler.logger.log(Level.FINE, "Obtained template details: {0}", templateDetails);
                response.put("status", 200);
                return response;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        TemplateAPIHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
