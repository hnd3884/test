package com.adventnet.sym.server.mdm.certificates.templates.digicert;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.certificates.templates.TemplateDbHandler;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class DigicertTemplateHandler
{
    public static Logger logger;
    
    public static JSONArray getDigicertTemplates(final Criteria criteria) throws DataAccessException {
        final JSONArray templates = new JSONArray();
        final SelectQuery selectQuery = TemplateDbHandler.getTemplateSelectQuery();
        DigicertTemplateDbHandler.getTemplateToCertificateOIDSelectQuery(selectQuery);
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdmCertificateTemplate");
        if (!dataObject.isEmpty()) {
            DigicertTemplateHandler.logger.log(Level.FINE, "Details found. Creating JSON...");
            while (iterator.hasNext()) {
                final Row templateRow = iterator.next();
                final Long templateID = (Long)templateRow.get("TEMPLATE_ID");
                final JSONObject individualTemplateJson = new JSONObject();
                individualTemplateJson.put("TEMPLATE_ID", templateRow.get("TEMPLATE_ID"));
                individualTemplateJson.put("TEMPLATE_NAME", templateRow.get("TEMPLATE_NAME"));
                individualTemplateJson.put("TEMPLATE_TYPE", templateRow.get("TEMPLATE_TYPE"));
                final Row row = dataObject.getRow("MdmTemplateToCertificateOID", DigicertTemplateDbHandler.getTemplateIDCriteria(templateID));
                if (row != null) {
                    individualTemplateJson.put("CERTIFICATE_OID", row.get("CERTIFICATE_OID"));
                }
                templates.put((Object)individualTemplateJson);
            }
        }
        else {
            DigicertTemplateHandler.logger.log(Level.FINE, "No details found for the given template id.");
        }
        return templates;
    }
    
    static {
        DigicertTemplateHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
