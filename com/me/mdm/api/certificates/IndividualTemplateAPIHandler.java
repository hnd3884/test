package com.me.mdm.api.certificates;

import com.adventnet.sym.server.mdm.certificates.templates.MdmCertTemplate;
import com.adventnet.sym.server.mdm.certificates.templates.digicert.DigicertCertTemplate;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.certificates.templates.TemplateHandler;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.certificates.templates.TemplateConstants;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualTemplateAPIHandler extends ApiRequestHandler
{
    Logger logger;
    
    public IndividualTemplateAPIHandler() {
        this.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) {
        this.logger.log(Level.FINE, "Getting template details based on template id");
        try {
            apiRequest.urlStartKey = TemplateConstants.Api.Url.templates;
            final JSONObject request = apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("resource_identifier");
            if (apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("filters").has(TemplateConstants.Api.Key.template_type)) {
                final int template_type = apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("filters").getInt(TemplateConstants.Api.Key.template_type);
                request.put("TEMPLATE_TYPE", template_type);
                request.put("TEMPLATE_ID", request.optLong(TemplateConstants.Api.Key.template_id));
                request.put("CUSTOMER_ID", (Object)APIUtil.optCustomerID(apiRequest.toJSONObject()));
                final JSONObject templateDetails = TemplateHandler.getTemplateDetailForTemplateID(request);
                this.logger.log(Level.FINE, "Template Details: {0}", templateDetails);
                final JSONObject response = new JSONObject();
                response.put("RESPONSE", (Object)templateDetails);
                response.put("status", 200);
                return response;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) {
        this.logger.log(Level.FINE, "doPost: Template addition begins");
        try {
            final JSONObject msgBodyJSON = apiRequest.toJSONObject().getJSONObject("msg_body");
            final long customerId = APIUtil.optCustomerID(apiRequest.toJSONObject());
            final String templateName = msgBodyJSON.getString(TemplateConstants.Api.Key.template_name);
            final String certificateOID = msgBodyJSON.getString(TemplateConstants.Api.Key.Digicert.certificate_oid);
            final int templateType = msgBodyJSON.getInt(TemplateConstants.Api.Key.template_type);
            final JSONObject response = new JSONObject();
            if (templateType == 1) {
                final DigicertCertTemplate certTemplate = new DigicertCertTemplate(templateName, certificateOID);
                final Long templateId = TemplateHandler.addOrUpdateTemplate(certTemplate, -1L, customerId);
                response.put("RESPONSE", (Object)new JSONObject().put("TEMPLATE_ID", (Object)templateId));
                response.put("status", 200);
                return response;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "doPost: Exception while adding templates ", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "doPost: Exception while adding templates ", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) {
        try {
            this.logger.log(Level.INFO, "doPut: Template modification begins");
            apiRequest.urlStartKey = TemplateConstants.Api.Url.templates;
            final JSONObject headerJSON = apiRequest.toJSONObject().getJSONObject("msg_header");
            final Long templateId = headerJSON.getJSONObject("resource_identifier").getLong(TemplateConstants.Api.Key.template_id);
            final JSONObject msgBodyJSON = apiRequest.toJSONObject().getJSONObject("msg_body");
            final long customerId = APIUtil.optCustomerID(apiRequest.toJSONObject());
            final String templateName = msgBodyJSON.getString(TemplateConstants.Api.Key.template_name);
            final String certificateOID = msgBodyJSON.getString(TemplateConstants.Api.Key.Digicert.certificate_oid);
            final int templateType = msgBodyJSON.getInt(TemplateConstants.Api.Key.template_type);
            final JSONObject response = new JSONObject();
            if (templateType == 1) {
                final DigicertCertTemplate certTemplate = new DigicertCertTemplate(templateName, certificateOID);
                TemplateHandler.addOrUpdateTemplate(certTemplate, templateId, customerId);
                response.put("RESPONSE", (Object)new JSONObject().put("TEMPLATE_ID", (Object)templateId));
                response.put("status", 200);
                return response;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "doPost: Exception while adding templates ", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "doPost: Exception while adding templates ", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) {
        try {
            apiRequest.urlStartKey = TemplateConstants.Api.Url.templates;
            final JSONObject request = apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("resource_identifier");
            final long templateId = request.optLong(TemplateConstants.Api.Key.template_id, -1L);
            if (templateId != -1L) {
                TemplateHandler.deleteTemplate(templateId);
                final JSONObject response = new JSONObject();
                response.put("status", 204);
                return response;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
