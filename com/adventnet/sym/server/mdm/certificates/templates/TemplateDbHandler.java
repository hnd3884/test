package com.adventnet.sym.server.mdm.certificates.templates;

import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class TemplateDbHandler
{
    public static Logger logger;
    
    public static SelectQuery getTemplateSelectQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdmCertificateTemplate"));
        selectQuery.addSelectColumn(new Column("MdmCertificateTemplate", "*"));
        return selectQuery;
    }
    
    public static DataObject getTemplateDO(final Criteria criteria) throws Exception {
        final SelectQuery selectQuery = getTemplateSelectQuery();
        selectQuery.setCriteria(criteria);
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public static Long addOrUpdateMdmCertificateTemplate(final MdmCertTemplate certTemplate, long templateID, final long customerId) throws Exception {
        final SelectQuery selectQuery = getTemplateSelectQuery();
        selectQuery.setCriteria(getTemplateIDCriteria(templateID).and(getCustomerCriteria(customerId)));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (dataObject.isEmpty()) {
            final Row newTemplateRow = new Row("MdmCertificateTemplate");
            newTemplateRow.set("TEMPLATE_NAME", (Object)certTemplate.templateName);
            newTemplateRow.set("CUSTOMER_ID", (Object)customerId);
            newTemplateRow.set("TEMPLATE_TYPE", (Object)certTemplate.templateType.getTemplateType());
            dataObject.addRow(newTemplateRow);
            SyMUtil.getPersistence().add(dataObject);
            TemplateDbHandler.logger.log(Level.INFO, "New Template added");
        }
        else {
            final Row existingTemplateRow = dataObject.getFirstRow("MdmCertificateTemplate");
            existingTemplateRow.set("TEMPLATE_NAME", (Object)certTemplate.templateName);
            dataObject.updateRow(existingTemplateRow);
            SyMUtil.getPersistence().update(dataObject);
            TemplateDbHandler.logger.log(Level.INFO, "Existing template updated");
        }
        templateID = (long)dataObject.getRow("MdmCertificateTemplate").get("TEMPLATE_ID");
        TemplateDbHandler.logger.log(Level.INFO, "Template ID: {0}", new Object[] { templateID });
        return templateID;
    }
    
    public static Criteria getTemplateIDCriteria(final Long template_id) {
        return new Criteria(new Column("MdmCertificateTemplate", "TEMPLATE_ID"), (Object)template_id, 0);
    }
    
    public static Criteria getTemplateTypeCriteria(final int template_type) {
        return new Criteria(new Column("MdmCertificateTemplate", "TEMPLATE_TYPE"), (Object)template_type, 0);
    }
    
    public static Criteria getCustomerCriteria(final Long customerID) {
        return new Criteria(new Column("MdmCertificateTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
    }
    
    static {
        TemplateDbHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
