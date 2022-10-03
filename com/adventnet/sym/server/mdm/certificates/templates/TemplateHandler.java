package com.adventnet.sym.server.mdm.certificates.templates;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.certificates.templates.digicert.DigicertTemplateHandler;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.certificates.templates.digicert.DigicertTemplateDbHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateHandler
{
    public static Logger logger;
    
    public static Long addOrUpdateTemplate(final MdmCertTemplate certTemplate, long templateId, final long customerId) throws Exception {
        TemplateHandler.logger.log(Level.INFO, "Adding Template details: {0}", new Object[] { certTemplate.templateName });
        try {
            templateId = TemplateDbHandler.addOrUpdateMdmCertificateTemplate(certTemplate, templateId, customerId);
            if (certTemplate.templateType == MdmCertTemplateType.DIGICERT) {
                DigicertTemplateDbHandler.addOrUpdateMdmTemplateToCertificateOid(certTemplate, templateId);
            }
            TemplateHandler.logger.log(Level.INFO, "Template successfully added/ updated.");
            return templateId;
        }
        catch (final Exception e) {
            TemplateHandler.logger.log(Level.WARNING, "Exception while populating template details", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static JSONObject getTemplateDetailForTemplateID(final JSONObject jsonObject) throws DataAccessException {
        TemplateHandler.logger.log(Level.FINE, "Getting template details based on type");
        final Long customerID = jsonObject.optLong("CUSTOMER_ID");
        final Long template_Id = jsonObject.optLong("TEMPLATE_ID");
        final int template_type = jsonObject.optInt("TEMPLATE_TYPE");
        final JSONObject responseJson = new JSONObject();
        final Criteria templateIdCriteria = TemplateDbHandler.getTemplateIDCriteria(template_Id);
        final Criteria customerCriteria = TemplateDbHandler.getCustomerCriteria(customerID);
        final Criteria overAllCriteria = templateIdCriteria.and(customerCriteria);
        if (template_type == 1) {
            TemplateHandler.logger.log(Level.FINE, "Getting Digicert Certificate template details");
            responseJson.put("templateDetails", (Object)DigicertTemplateHandler.getDigicertTemplates(overAllCriteria));
        }
        return responseJson;
    }
    
    public static JSONObject getTemplateDetailsForTemplateType(final JSONObject jsonObject) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final int templateType = jsonObject.optInt("TEMPLATE_TYPE");
        final Long customerID = jsonObject.getLong("CUSTOMER_ID");
        final Criteria templateTypeCriteria = TemplateDbHandler.getTemplateTypeCriteria(templateType);
        final Criteria customerCriteria = TemplateDbHandler.getCustomerCriteria(customerID);
        final Criteria overAllCriteria = templateTypeCriteria.and(customerCriteria);
        if (templateType == 1) {
            TemplateHandler.logger.log(Level.FINE, "Getting Digicert Certificate template details");
            responseJSON.put("templateDetails", (Object)DigicertTemplateHandler.getDigicertTemplates(overAllCriteria));
        }
        return responseJSON;
    }
    
    public static void deleteTemplate(final Long template_id) throws DataAccessException {
        TemplateHandler.logger.log(Level.FINE, "Deleting template details for the template id {0}", template_id);
        final Criteria templateIDcriteria = new Criteria(new Column("MdmCertificateTemplate", "TEMPLATE_ID"), (Object)template_id, 0);
        MDMUtil.getPersistence().delete(templateIDcriteria);
        TemplateHandler.logger.log(Level.FINE, "Template details for the template id {0} has been deleted successfully", template_id);
    }
    
    static {
        TemplateHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
